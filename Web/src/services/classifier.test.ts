import { describe, it, expect, vi, beforeEach } from 'vitest';
import { processBrainScan, initializeModels } from './classifier';

describe('classifierService (AI Inference Interface)', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
  });

  it('should verify server readiness via initializeModels', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({ ok: true, status: 200 }));
    const onProgress = vi.fn();
    const result = await initializeModels(onProgress);

    expect(result).toBe(true);
    expect(onProgress).toHaveBeenCalledWith('Connecting to AI inference server...');
    expect(onProgress).toHaveBeenCalledWith('AI inference server ready.');
  });

  it('should correctly handle offline server response', async () => {
    vi.stubGlobal('fetch', vi.fn().mockRejectedValue(new Error('Network Error')));

    const file = new File(['sample'], 'scan.jpg', { type: 'image/jpeg' });
    const res = await processBrainScan('data:image/jpeg;base64,sample', file);

    expect(res.validationFailed).toBe(true);
    expect(res.validationError).toContain('AI inference server is offline');
    expect(res.hasHemorrhage).toBe(false);
  });

  it('should correctly process normal brain CT scan response', async () => {
    const mockResponse = {
      validationFailed: false,
      hasHemorrhage: false,
      highestConfidence: 0.95,
      detectionCount: 0,
      intraventricular: 0,
      intraparenchymal: 0,
      subarachnoid: 0,
      epidural: 0,
      subdural: 0,
    };

    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: true,
      json: async () => mockResponse,
    }));

    const file = new File(['normal_data'], 'normal_ct.jpg', { type: 'image/jpeg' });
    const res = await processBrainScan('data:image/jpeg;base64,normal', file);

    expect(res.validationFailed).toBe(false);
    expect(res.hasHemorrhage).toBe(false);
    expect(res.highestConfidence).toBe(0.95);
  });

  it('should correctly process hemorrhage CT scan response with subtype probabilities', async () => {
    const mockResponse = {
      validationFailed: false,
      hasHemorrhage: true,
      highestConfidence: 0.88,
      detectionCount: 1,
      topSubtype: 'Intraventricular',
      intraventricular: 0.85,
      intraparenchymal: 0.05,
      subarachnoid: 0.03,
      epidural: 0.02,
      subdural: 0.05,
    };

    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: true,
      json: async () => mockResponse,
    }));

    const file = new File(['abnormal_data'], 'hemorrhage_ct.jpg', { type: 'image/jpeg' });
    const res = await processBrainScan('data:image/jpeg;base64,hemorrhage', file);

    expect(res.validationFailed).toBe(false);
    expect(res.hasHemorrhage).toBe(true);
    expect(res.highestConfidence).toBe(0.88);
    expect(res.topSubtype).toBe('Intraventricular');
    expect(res.intraventricular).toBe(0.85);
  });
});
