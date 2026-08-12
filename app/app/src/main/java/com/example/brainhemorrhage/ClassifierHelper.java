package com.example.brainhemorrhage;

import android.content.Context;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ClassifierHelper {
    private Interpreter interpreter;
    private int inputSize;
    private List<String> labels;

    public static class ClassifierResult {
        public String predictedClass;
        public float confidence;
    }

    public ClassifierHelper(Context context) throws IOException {
        MappedByteBuffer modelFile = FileUtil.loadMappedFile(context, "brain_ct_classifier.tflite");
        
        // We utilize our robust internal explicit label mapping logic to interpret outputs safely,
        // avoiding dependency on the external TFLite metadata extractor package to ensure stable, lightweight compilation.
        labels = new ArrayList<>();

        Interpreter.Options options = new Interpreter.Options();
        int numThreads = Math.max(1, Math.min(Runtime.getRuntime().availableProcessors(), 4));
        options.setNumThreads(numThreads);
        interpreter = new Interpreter(modelFile, options);

        int[] inputShape = interpreter.getInputTensor(0).shape();
        inputSize = inputShape[1];
    }

    public ClassifierResult classify(Bitmap originalBitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, inputSize, inputSize, true);

        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4);
        inputBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[inputSize * inputSize];
        resizedBitmap.getPixels(intValues, 0, resizedBitmap.getWidth(), 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight());

        int pixel = 0;
        for (int i = 0; i < inputSize; ++i) {
            for (int j = 0; j < inputSize; ++j) {
                int val = intValues[pixel++];
                inputBuffer.putFloat(((val >> 16) & 0xFF) / 255.0f);
                inputBuffer.putFloat(((val >> 8) & 0xFF) / 255.0f);
                inputBuffer.putFloat((val & 0xFF) / 255.0f);
            }
        }

        if (resizedBitmap != originalBitmap) {
            BitmapUtils.safeRecycle(resizedBitmap);
        }

        int[] outputShape = interpreter.getOutputTensor(0).shape();
        float[][] outputArr = new float[1][outputShape[1]];
        interpreter.run(inputBuffer, outputArr);

        ClassifierResult result = new ClassifierResult();

        // Gatekeeper confidence threshold — image must score at least this to pass as brain_ct.
        // Raising above 0.5 reduces false-accepts of non-CT images.
        final float GATE_THRESHOLD = 0.55f;

        if (outputShape[1] == 1) {
            // Single sigmoid output: output > GATE_THRESHOLD means brain_ct.
            float prob = outputArr[0][0];
            // Teachable Machine sigmoid: high value = the class it was trained to detect.
            // We treat the positive class (brain_ct) as prob >= GATE_THRESHOLD.
            boolean isBrainCt = prob >= GATE_THRESHOLD;
            result.predictedClass = isBrainCt ? "brain_ct" : "non_brain_ct";
            result.confidence = isBrainCt ? prob : (1.0f - prob);
        } else {
            // Two-output softmax.
            // Teachable Machine exports classes in ALPHABETICAL order:
            //   index 0 = brain_ct   (b comes before n)
            //   index 1 = non_brain_ct
            // So prob0 is the brain_ct score and prob1 is the non_brain_ct score.
            float prob0 = outputArr[0][0]; // brain_ct
            float prob1 = outputArr[0][1]; // non_brain_ct

            android.util.Log.d("ClassifierHelper",
                String.format("Gatekeeper: brain_ct=%.3f  non_brain_ct=%.3f  threshold=%.2f",
                    prob0, prob1, GATE_THRESHOLD));

            if (prob0 >= prob1 && prob0 >= GATE_THRESHOLD) {
                // brain_ct wins and clears the confidence threshold
                result.predictedClass = "brain_ct";
                result.confidence = prob0;
            } else {
                // non_brain_ct wins OR brain_ct didn't clear the threshold
                result.predictedClass = "non_brain_ct";
                result.confidence = prob1;
            }
        }

        return result;
    }

    public void close() {
        if (interpreter != null) {
            interpreter.close();
            interpreter = null;
        }
    }
}
