module = module.exports = {
  baseUrl: process.env.WEB_BASE_URL || 'http://localhost:5173',
  browser: process.env.BROWSER || 'chrome',
  headless: process.env.HEADLESS !== 'false',
  implicitWaitMs: 5000,
  explicitWaitMs: 15000,
  supportedBrowsers: ['chrome', 'firefox', 'edge'],
  viewports: [
    { name: 'Desktop 1080p', width: 1920, height: 1080 },
    { name: 'Laptop 768p', width: 1366, height: 768 },
    { name: 'Tablet Portrait', width: 768, height: 1024 },
    { name: 'Mobile Portrait', width: 375, height: 812 }
  ],
  locales: ['en', 'hi', 'ta', 'te', 'kn', 'gu', 'mr', 'bn', 'es'],
  targets: {
    totalTestsTarget: 350
  }
};
