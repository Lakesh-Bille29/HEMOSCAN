<?php
// submit_ticket.php — Create a support ticket, store in DB, send acknowledgment email
require_once 'db.php';
require_once 'config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(["status" => "error", "message" => "POST required"]);
    exit;
}

// ── Sanitize & validate inputs ────────────────────────────────────────────────────
$email    = isset($_POST['email'])    ? trim($_POST['email'])    : '';
$category = isset($_POST['category']) ? trim($_POST['category']) : '';
$message  = isset($_POST['message'])  ? trim($_POST['message'])  : '';

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    echo json_encode(["status" => "error", "message" => "Invalid email address."]);
    exit;
}
if (empty($category)) {
    echo json_encode(["status" => "error", "message" => "Category is required."]);
    exit;
}
if (mb_strlen($message) < 20) {
    echo json_encode(["status" => "error", "message" => "Message must be at least 20 characters."]);
    exit;
}
if (mb_strlen($message) > 5000) {
    echo json_encode(["status" => "error", "message" => "Message must be under 5000 characters."]);
    exit;
}

// ── Rate limiting: max 3 tickets per email per hour ───────────────────────────────
$rateStmt = $conn->prepare(
    "SELECT COUNT(*) AS cnt FROM support_tickets
     WHERE doctor_email = ? AND created_at > DATE_SUB(NOW(), INTERVAL 1 HOUR)"
);
$rateStmt->bind_param("s", $email);
$rateStmt->execute();
$rateResult = $rateStmt->get_result()->fetch_assoc();
$rateStmt->close();

if ($rateResult['cnt'] >= 3) {
    echo json_encode([
        "status"  => "error",
        "message" => "Rate limit reached. Maximum 3 tickets per hour. Please try again later."
    ]);
    exit;
}

// ── Generate unique ticket number ─────────────────────────────────────────────────
$ticketNum = 'HST-' . date('Ymd') . '-' . strtoupper(substr(md5(uniqid()), 0, 6));

// ── Insert ticket ──────────────────────────────────────────────────────────────────
$platform = isset($_POST['platform']) ? trim($_POST['platform']) : 'unknown'; // 'android' or 'web'
$device   = isset($_POST['device'])   ? trim($_POST['device'])   : '';

$stmt = $conn->prepare(
    "INSERT INTO support_tickets
     (ticket_number, doctor_email, category, message, platform, device_info, status, priority)
     VALUES (?, ?, ?, ?, ?, ?, 'open', 'medium')"
);
$stmt->bind_param("ssssss", $ticketNum, $email, $category, $message, $platform, $device);

if (!$stmt->execute()) {
    echo json_encode(["status" => "error", "message" => "Failed to save ticket. Please try again."]);
    $stmt->close();
    $conn->close();
    exit;
}
$stmt->close();

// ── Send acknowledgment email via PHPMailer ────────────────────────────────────────
$emailSent = false;
try {
    require_once 'phpmailer/src/PHPMailer.php';
    require_once 'phpmailer/src/SMTP.php';
    require_once 'phpmailer/src/Exception.php';

    $mail = new PHPMailer\PHPMailer\PHPMailer(true);
    $mail->isSMTP();
    $mail->Host       = SMTP_HOST;
    $mail->SMTPAuth   = true;
    $mail->Username   = SMTP_USERNAME;
    $mail->Password   = SMTP_PASSWORD;
    $mail->SMTPSecure = PHPMailer\PHPMailer\PHPMailer::ENCRYPTION_STARTTLS;
    $mail->Port       = SMTP_PORT;

    $mail->setFrom(SMTP_FROM, SMTP_FROM_NAME);
    $mail->addAddress($email);
    $mail->addReplyTo(SMTP_FROM, SMTP_FROM_NAME);

    $mail->Subject = "[HemoScan Support] Ticket #{$ticketNum} Received";
    $mail->isHTML(true);
    $mail->Body = "
    <div style='font-family: Inter, sans-serif; max-width: 600px; margin: 0 auto; background: #f8f9fa; padding: 32px; border-radius: 12px;'>
      <div style='background: linear-gradient(135deg, #0ea5e9, #6366f1); padding: 24px; border-radius: 8px; text-align: center; margin-bottom: 24px;'>
        <h1 style='color: white; margin: 0; font-size: 24px;'>🧠 HemoScan Support</h1>
        <p style='color: rgba(255,255,255,0.85); margin: 8px 0 0;'>We received your support request</p>
      </div>
      <div style='background: white; padding: 24px; border-radius: 8px; margin-bottom: 16px;'>
        <h2 style='margin: 0 0 16px; color: #1e293b;'>Ticket Confirmed</h2>
        <table style='width: 100%; border-collapse: collapse;'>
          <tr><td style='padding: 8px 0; color: #64748b; width: 140px;'>Ticket Number</td><td style='padding: 8px 0; font-weight: 600; color: #0ea5e9;'>#{$ticketNum}</td></tr>
          <tr><td style='padding: 8px 0; color: #64748b;'>Category</td><td style='padding: 8px 0; color: #1e293b;'>{$category}</td></tr>
          <tr><td style='padding: 8px 0; color: #64748b;'>Status</td><td style='padding: 8px 0;'><span style='background: #dbeafe; color: #1d4ed8; padding: 2px 8px; border-radius: 4px; font-size: 13px;'>Open</span></td></tr>
        </table>
      </div>
      <div style='background: white; padding: 24px; border-radius: 8px; margin-bottom: 16px;'>
        <h3 style='margin: 0 0 12px; color: #1e293b; font-size: 16px;'>Your Message</h3>
        <p style='color: #475569; line-height: 1.6; margin: 0;'>" . nl2br(htmlspecialchars($message)) . "</p>
      </div>
      <p style='color: #64748b; font-size: 14px; text-align: center; margin: 0;'>
        Our team will respond within <strong>24–48 business hours</strong>.<br>
        Please keep your ticket number for reference.
      </p>
    </div>";
    $mail->AltBody = "HemoScan Support - Ticket #{$ticketNum} received.\nCategory: {$category}\nWe will respond within 24-48 business hours.";

    $mail->send();
    $emailSent = true;
} catch (Exception $e) {
    // Email failure is non-fatal — ticket is already stored in DB
    error_log("HemoScan ticket email failed for {$ticketNum}: " . $e->getMessage());
}

echo json_encode([
    "status"        => "success",
    "message"       => "Your support ticket has been submitted successfully.",
    "ticket_number" => $ticketNum,
    "email_sent"    => $emailSent
]);

$conn->close();
?>
