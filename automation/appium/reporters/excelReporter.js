const ExcelJS = require('exceljs');
const fs = require('fs');
const path = require('path');

class AppiumExcelReporter {
  static async generateReport(testResults, deviceInfo = {}) {
    const workbook = new ExcelJS.Workbook();
    const outputDir = path.resolve(__dirname, '../../../reports/appium');
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
    const passRate = total > 0 ? ((passed / total) * 100).toFixed(2) + '%' : '0%';

    summarySheet.addRows([
      { metric: 'Framework', value: 'Appium 2.x UiAutomator2' },
      { metric: 'Application', value: 'HemoScan Android App' },
      { metric: 'Total Executed Tests', value: total },
      { metric: 'Passed Tests', value: passed },
      { metric: 'Failed Tests', value: failed },
      { metric: 'Pass Rate', value: passRate },
      { metric: 'Device', value: deviceInfo.device || 'Android Emulator' },
      { metric: 'OS Version', value: deviceInfo.platformVersion || 'Android 14 (API 34/35)' }
    ]);

    // Sheet 2: Test Cases
    const casesSheet = workbook.addWorksheet('Test Cases');
    casesSheet.columns = [
      { header: 'Test ID', key: 'id', width: 12 },
      { header: 'Module', key: 'module', width: 20 },
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

    // Sheet 4: Device Information
    const deviceSheet = workbook.addWorksheet('Device Information');
    deviceSheet.columns = [
      { header: 'Property', key: 'prop', width: 25 },
      { header: 'Detail', key: 'val', width: 45 }
    ];
    deviceSheet.addRows([
      { prop: 'App Package', val: 'com.example.brainhemorrhage' },
      { prop: 'App Activity', val: 'com.example.brainhemorrhage.MainActivity' },
      { prop: 'Automation Name', val: 'UiAutomator2' },
      { prop: 'Target SDK', val: '35' },
      { prop: 'Min SDK', val: '27' }
    ]);

    const reportPath = path.join(outputDir, 'HemoScan_Appium_Report.xlsx');
    await workbook.xlsx.writeFile(reportPath);
    return reportPath;
  }
}

module.exports = AppiumExcelReporter;
