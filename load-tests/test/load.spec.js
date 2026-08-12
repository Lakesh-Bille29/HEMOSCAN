const { expect } = require('chai');
const { generateLoadTestCases, TOTAL_LOAD_TESTS_TARGET } = require('../generators/loadTestGenerator');

describe(`HemoScan Enterprise k6 Load & Performance Testing Suite (Target: ${TOTAL_LOAD_TESTS_TARGET} Tests)`, function () {
  this.timeout(120000);
  const testCases = generateLoadTestCases();

  it(`verify load generator produces target range (${TOTAL_LOAD_TESTS_TARGET} test scenarios)`, function () {
    expect(testCases.length).to.be.within(300, 400);
  });

  testCases.forEach((tc) => {
    it(`${tc.id}: ${tc.title}`, async function () {
      const res = await tc.execute();
      expect(res.status).to.equal('PASSED');
    });
  });
});
