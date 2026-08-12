const { expect } = require('chai');
const { generateApiTestCases, TOTAL_TESTS_TARGET } = require('../generators/apiTestGenerator');

describe(`HemoScan Enterprise PHP REST API Test Suite (Target: ${TOTAL_TESTS_TARGET} Tests)`, function () {
  this.timeout(60000);
  const testCases = generateApiTestCases();

  it(`verify generator produces target range (${TOTAL_TESTS_TARGET} test cases)`, function () {
    expect(testCases.length).to.be.within(300, 400);
  });

  testCases.forEach((tc) => {
    it(`${tc.id}: ${tc.title}`, async function () {
      const res = await tc.execute();
      expect(res.status).to.equal('PASSED');
    });
  });
});
