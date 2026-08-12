const { expect } = require('chai');
const { generateAppiumTestCases, TOTAL_TESTS_TARGET } = require('../generators/testGenerator');
const AppiumExcelReporter = require('../reporters/excelReporter');

describe(`HemoScan Enterprise Appium 2.x Automation Suite (Target: ${TOTAL_TESTS_TARGET} Tests)`, function () {
  this.timeout(120000);
  const testCases = generateAppiumTestCases();
  const executedResults = [];

  it(`verify generator produces target range (${TOTAL_TESTS_TARGET} test cases)`, function () {
    expect(testCases.length).to.be.within(300, 400);
  });

  testCases.forEach((tc) => {
    it(`${tc.id}: ${tc.title}`, async function () {
      const startTime = Date.now();
      try {
        const res = await tc.execute();
        const duration = Date.now() - startTime;
        executedResults.push({
          id: tc.id,
          module: tc.module,
          title: tc.title,
          status: 'PASSED',
          duration,
          error: ''
        });
        expect(res.status).to.equal('PASSED');
      } catch (err) {
        const duration = Date.now() - startTime;
        executedResults.push({
          id: tc.id,
          module: tc.module,
          title: tc.title,
          status: 'FAILED',
          duration,
          error: err.message
        });
        throw err;
      }
    });
  });

  after(async function () {
    if (executedResults.length > 0) {
      const reportPath = await AppiumExcelReporter.generateReport(executedResults, {
        device: 'Android Emulator / Pixel 7',
        platformVersion: 'Android 14 (API 34/35)'
      });
      console.log(`\n[Appium Suite] Report generated successfully: ${reportPath}`);
    }
  });
});
