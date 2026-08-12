const ExcelJS = require('exceljs');
const fs = require('fs');
const path = require('path');

class SeleniumExcelReporter {
  static async generateReport(testResults, browserInfo = {}) {
    const workbook = new ExcelJS.Workbook();
    const outputDir = path.resolve(__dirname, '../../../reports/selenium');
    if (!fs.existsSync(outputDir)) {
      fs.mkdirSync(outputDir, { recursive: true });
    }

    // Sheet 1: Summary
    const summarySheet = workbook.addWorksheet('Summary');
    summarySheet.columns = [
      { header: 'Metric', key: 'metric', width: 30 },
      { header: 'Value', key: 'value', width: 30 }
    ];
    const total = testResults.length;
    const passed = testResults.filter(t => t.status === 'PASSED').length;
    const failed = testResults.filter(t => t.status === 'FAILED').length;

    summarySheet.addRows([
      { metric: 'Framework', value: 'Selenium WebDriver Node.js' },
      { metric: 'Application', value: 'HemoScan React Web Dashboard' },
      { metric: 'Total Executed Tests', value: total },
      { metric: 'Passed Tests', value: passed },
      { metric: 'Failed Tests', value: failed },
      { metric: 'Pass Rate', value: total > 0 ? ((passed / total) * 100).toFixed(2) + '%' : '0%' },
      { metric: 'Browser Engine', value: browserInfo.browser || 'Google Chrome Headless' }
    ]);

    // Sheet 2: Test Cases
    const casesSheet = workbook.addWorksheet('Test Cases');
    casesSheet.columns = [
      { header: 'Test ID', key: 'id', width: 15 },
      { header: 'Module', key: 'module', width: 22 },
      { header: 'Test Title', key: 'title', width: 50 },
      { header: 'Status', key: 'status', width: 12 },
      { header: 'Duration (ms)', key: 'duration', width: 15 },
      { header: 'Error Message', key: 'error', width: 40 }
    ];
    testResults.forEach(t => casesSheet.addRow(t));

    // Sheet 3: Failed Tests
    const failedSheet = workbook.addWorksheet('Failed Tests');
    failedSheet.columns = casesSheet.columns;
    testResults.filter(t => t.status === 'FAILED').forEach(t => failedSheet.addRow(t));

    const reportPath = path.join(outputDir, 'HemoScan_Web_Report.xlsx');
    await workbook.xlsx.writeFile(reportPath);
    return reportPath;
  }
}

module.exports = SeleniumExcelReporter;
