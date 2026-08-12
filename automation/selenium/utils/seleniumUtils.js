const { By, until } = require('selenium-webdriver');
const fs = require('fs');
const path = require('path');

class SeleniumUtils {
  constructor(driver) {
    this.driver = driver;
  }

  async waitForElement(selector, timeoutMs = 10000) {
    const element = await this.driver.wait(until.elementLocated(selector), timeoutMs);
    await this.driver.wait(until.elementIsVisible(element), timeoutMs);
    return element;
  }

  async safeClick(selector) {
    try {
      const element = await this.waitForElement(selector);
      await element.click();
    } catch {
      // Stale element recovery via JS click
      const element = await this.driver.findElement(selector);
      await this.driver.executeScript('arguments[0].click();', element);
    }
  }

  async scrollToElement(selector) {
    const element = await this.driver.findElement(selector);
    await this.driver.executeScript('arguments[0].scrollIntoView({ behavior: "smooth", block: "center" });', element);
  }

  async captureScreenshot(testName) {
    const sanitized = testName.replace(/[^a-zA-Z0-9_-]/g, '_');
    const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
    const targetDir = path.resolve(__dirname, '../../../reports/failures/selenium');

    if (!fs.existsSync(targetDir)) {
      fs.mkdirSync(targetDir, { recursive: true });
    }

    const image = await this.driver.takeScreenshot();
    fs.writeFileSync(path.join(targetDir, `${sanitized}_${timestamp}.png`), image, 'base64');
  }
}

module.exports = SeleniumUtils;
