package com.example.brainhemorrhage.api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface BrainScanApi {

    @FormUrlEncoded
    @POST("login.php")
    Call<LoginResponse> login(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("signup.php")
    Call<BaseResponse> signup(
            @Field("name") String name,
            @Field("email") String email,
            @Field("mobile") String mobile,
            @Field("gender") String gender,
            @Field("password") String password,
            @Field("otp_code") String otpCode   // sent for atomic server-side OTP check
    );

    @FormUrlEncoded
    @POST("delete_account.php")
    Call<BaseResponse> deleteAccount(
            @Field("email") String email,
            @Field("password") String password
    );

    @GET("get_scans.php")
    Call<ScanResponse> getPatientScans(
            @Query("doctor_email") String doctorEmail,
            @Query("patient_id") String patientId,
            @Query("patient_name") String patientName,
            @Query("patient_age") String patientAge,
            @Query("patient_gender") String patientGender
    );

    @Multipart
    @POST("upload_scan.php")
    Call<BaseResponse> uploadScan(
            @Part("doctor_email") RequestBody doctorEmail,
            @Part("patient_id") RequestBody patientId,
            @Part("patient_name") RequestBody patientName,
            @Part("patient_age") RequestBody patientAge,
            @Part("patient_gender") RequestBody patientGender,
            @Part("result") RequestBody result,
            @Part("risk_level") RequestBody riskLevel,
            @Part MultipartBody.Part image
    );

    @Multipart
    @POST("update_profile.php")
    Call<BaseResponse> updateProfile(
            @Part("email") RequestBody email,
            @Part("name") RequestBody name,
            @Part("specialty") RequestBody specialty,
            @Part MultipartBody.Part profileImage
    );

    /** Text-only profile update (no photo — avoids passing null MultipartBody.Part) */
    @FormUrlEncoded
    @POST("update_profile.php")
    Call<BaseResponse> updateProfileTextOnly(
            @Field("email") String email,
            @Field("name") String name,
            @Field("specialty") String specialty
    );

    @FormUrlEncoded
    @POST("update_profile.php")
    Call<BaseResponse> updateProfileFields(
            @Field("email") String email,
            @Field("name") String name,
            @Field("specialty") String specialty,
            @Field("bio") String bio,
            @Field("hospital") String hospital,
            @Field("license") String license,
            @Field("years_exp") Integer yearsExp,
            @Field("dark_mode") Integer darkMode,
            @Field("language") String language,
            @Field("daily_summary") Integer dailySummary,
            @Field("sound") Integer sound,
            @Field("vibration") Integer vibration,
            @Field("theme_mode") Integer themeMode
    );


    @FormUrlEncoded
    @POST("send_otp.php")
    Call<BaseResponse> sendOtp(
            @Field("email") String email,
            @Field("action") String action
    );

    @FormUrlEncoded
    @POST("verify_otp.php")
    Call<BaseResponse> verifyOtp(
            @Field("email") String email,
            @Field("otp_code") String otpCode,
            @Field("action") String action
    );

    @FormUrlEncoded
    @POST("reset_password.php")
    Call<BaseResponse> resetPassword(
            @Field("email") String email,
            @Field("otp_code") String otpCode,
            @Field("new_password") String newPassword
    );

    /**
     * Validates that the locally-cached email still exists in the server database.
     * Called at every app launch (SplashFragment) to detect stale sessions caused
     * by DB resets or account deletions. Returns "success" + fresh user data if
     * the account exists, or "error" if it no longer exists.
     */
    @FormUrlEncoded
    @POST("check_user.php")
    Call<LoginResponse> checkUser(
            @Field("email") String email
    );
    /**
     * Submit a support ticket.
     * Returns BaseResponse with ticket_number on success.
     */
    @FormUrlEncoded
    @POST("submit_ticket.php")
    Call<BaseResponse> submitTicket(
            @Field("email")    String email,
            @Field("category") String category,
            @Field("message")  String message,
            @Field("platform") String platform,
            @Field("device")   String device
    );

    /** Retrieve all tickets for a given doctor email. */
    @GET("get_tickets.php")
    Call<BaseResponse> getTickets(
            @Query("email") String email
    );

    /**
     * Save or update the FCM push token for a doctor on this device.
     * Called by HemoScanFirebaseService.onNewToken() and BrainHemorrhageApp on startup.
     */
    @FormUrlEncoded
    @POST("save_fcm_token.php")
    Call<BaseResponse> saveFcmToken(
            @Field("email")    String email,
            @Field("token")    String token,
            @Field("platform") String platform
    );
}
