const path = require('path');

module.exports = {
  hostname: process.env.APPIUM_HOST || '127.0.0.1',
  port: parseInt(process.env.APPIUM_PORT || '4723', 10),
  path: '/',
  capabilities: {
    platformName: 'Android',
    'appium:automationName': 'UiAutomator2',
    'appium:deviceName': process.env.ANDROID_DEVICE_NAME || 'Android Emulator',
    'appium:app': path.resolve(__dirname, '../../Android-App/app/build/outputs/apk/debug/app-debug.apk'),
    'appium:appPackage': 'com.example.brainhemorrhage',
    'appium:appActivity': 'com.example.brainhemorrhage.MainActivity',
    'appium:noReset': false,
    'appium:fullReset': false,
    'appium:autoGrantPermissions': true,
    'appium:newCommandTimeout': 120,
    'appium:uiautomator2ServerInstallTimeout': 60000,
  },
  targets: {
    totalTestsTarget: 350
  }
};
