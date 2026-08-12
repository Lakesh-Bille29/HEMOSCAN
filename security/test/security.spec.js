const { expect } = require('chai');
const { generateSecurityTestCases, TOTAL_SECURITY_TESTS_TARGET } = require('../generators/securityTestGenerator');

describe(`HemoScan Enterprise OWASP Vulnerability & SAST Security Suite (Target: ${TOTAL_SECURITY_TESTS_TARGET} Tests)`, function () {
  this.timeout(120000);
  const testCases = generateSecurityTestCases();

  it(`verify security generator produces target range (${TOTAL_SECURITY_TESTS_TARGET} test scenarios)`, function () {
    expect(testCases.length).to.be.within(300, 400);
  });

  testCases.forEach((tc) => {
    it(`${tc.id}: ${tc.title}`, async function () {
      const res = await tc.execute();
      expect(res.status).to.equal('PASSED');
    });
  });
});
