/**
 * HemoScan Appium Gesture Utilities (UiAutomator2 Native Actions)
 */

class GestureUtils {
  constructor(driver) {
    this.driver = driver;
  }

  async tap(element) {
    await element.click();
  }

  async doubleTap(element) {
    const location = await element.getLocation();
    await this.driver.performActions([{
      type: 'pointer',
      id: 'finger1',
      parameters: { pointerType: 'touch' },
      actions: [
        { type: 'pointerMove', duration: 0, x: location.x + 10, y: location.y + 10 },
        { type: 'pointerDown', button: 0 },
        { type: 'pointerUp', button: 0 },
        { type: 'pause', duration: 100 },
        { type: 'pointerDown', button: 0 },
        { type: 'pointerUp', button: 0 }
      ]
    }]);
  }

  async longPress(element, durationMs = 1500) {
    const location = await element.getLocation();
    await this.driver.performActions([{
      type: 'pointer',
      id: 'finger1',
      parameters: { pointerType: 'touch' },
      actions: [
        { type: 'pointerMove', duration: 0, x: location.x + 10, y: location.y + 10 },
        { type: 'pointerDown', button: 0 },
        { type: 'pause', duration: durationMs },
        { type: 'pointerUp', button: 0 }
      ]
    }]);
  }

  async swipe(startX, startY, endX, endY, durationMs = 800) {
    await this.driver.performActions([{
      type: 'pointer',
      id: 'finger1',
      parameters: { pointerType: 'touch' },
      actions: [
        { type: 'pointerMove', duration: 0, x: startX, y: startY },
        { type: 'pointerDown', button: 0 },
        { type: 'pointerMove', duration: durationMs, x: endX, y: endY },
        { type: 'pointerUp', button: 0 }
      ]
    }]);
  }

  async scrollDown() {
    const { width, height } = await this.driver.getWindowSize();
    await this.swipe(width / 2, height * 0.8, width / 2, height * 0.2);
  }

  async scrollUp() {
    const { width, height } = await this.driver.getWindowSize();
    await this.swipe(width / 2, height * 0.2, width / 2, height * 0.8);
  }

  async horizontalSwipeLeft() {
    const { width, height } = await this.driver.getWindowSize();
    await this.swipe(width * 0.8, height / 2, width * 0.2, height / 2);
  }

  async pullToRefresh() {
    const { width, height } = await this.driver.getWindowSize();
    await this.swipe(width / 2, height * 0.2, width / 2, height * 0.7, 1000);
  }
}

module.exports = GestureUtils;
