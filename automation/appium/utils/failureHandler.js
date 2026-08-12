const fs = require('fs');
const path = require('path');

class FailureHandler {
  static async captureArtifacts(driver, testName) {
    const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
    const sanitizedName = testName.replace(/[^a-zA-Z0-9_-]/g, '_');
    const targetDir = path.resolve(__dirname, '../../../reports/failures/appium');

    if (!fs.existsSync(targetDir)) {
      fs.mkdirSync(targetDir, { recursive: true });
    }

    try {
      // 1. Screenshot
      const screenshot = await driver.takeScreenshot();
      fs.writeFileSync(path.join(targetDir, `${sanitizedName}_${timestamp}.png`), screenshot, 'base64');

      // 2. Page Hierarchy XML
      const pageSource = await driver.getPageSource();
      fs.writeFileSync(path.join(targetDir, `${sanitizedName}_${timestamp}.xml`), pageSource, 'utf-8');

      // 3. Current Activity
      const currentActivity = await driver.getCurrentActivity().catch(() => 'unknown');
      fs.writeFileSync(
        path.join(targetDir, `${sanitizedName}_${timestamp}.meta.txt`),
        `Test: ${testName}\nTimestamp: ${timestamp}\nActivity: ${currentActivity}\n`
      );
    } catch (err) {
      console.error('FailureHandler error:', err.message);
    }
  }
}

module.exports = FailureHandler;
