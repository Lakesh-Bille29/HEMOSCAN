package com.example.brainhemorrhage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Log;

import androidx.exifinterface.media.ExifInterface;

import java.io.InputStream;

/**
 * BitmapUtils — memory-safe image loader and EXIF orientation handler.
 * Prevents OutOfMemory errors on low-RAM devices and ensures photos captured on 
 * Samsung, Xiaomi, Vivo, Oppo, and Realme devices are correctly rotated before inference.
 */
public class BitmapUtils {

    private static final String TAG = "BitmapUtils";
    public static final int DEFAULT_MAX_DIMENSION = 1024;

    /**
     * Decodes a memory-optimized Bitmap from a Uri, respecting max dimensions and EXIF rotation.
     */
    public static Bitmap decodeSampledBitmapFromUri(Context context, Uri uri, int reqWidth, int reqHeight) {
        if (context == null || uri == null) return null;

        InputStream is = null;
        try {
            // 1. Measure dimensions without allocating memory
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            is = context.getContentResolver().openInputStream(uri);
            if (is == null) return null;
            BitmapFactory.decodeStream(is, null, options);
            is.close();

            // 2. Calculate sample size (power of 2 downsampling)
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            // 3. Decode downsampled bitmap
            is = context.getContentResolver().openInputStream(uri);
            if (is == null) return null;
            Bitmap sampledBitmap = BitmapFactory.decodeStream(is, null, options);
            is.close();

            if (sampledBitmap == null) return null;

            // 4. Correct EXIF orientation
            int rotationDegrees = getExifRotationDegrees(context, uri);
            if (rotationDegrees != 0) {
                Bitmap rotated = rotateBitmap(sampledBitmap, rotationDegrees);
                if (rotated != sampledBitmap) {
                    safeRecycle(sampledBitmap);
                    sampledBitmap = rotated;
                }
            }

            return sampledBitmap;
        } catch (Exception e) {
            Log.e(TAG, "Error decoding sampled bitmap from Uri: " + uri, e);
            return null;
        } finally {
            try {
                if (is != null) is.close();
            } catch (Exception ignored) {}
        }
    }

    public static Bitmap decodeSampledBitmapFromUri(Context context, Uri uri) {
        return decodeSampledBitmapFromUri(context, uri, DEFAULT_MAX_DIMENSION, DEFAULT_MAX_DIMENSION);
    }

    /**
     * Calculates optimal inSampleSize downscaling factor.
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * Reads EXIF orientation from Uri and returns rotation degrees (0, 90, 180, 270).
     */
    public static int getExifRotationDegrees(Context context, Uri uri) {
        try (InputStream is = context.getContentResolver().openInputStream(uri)) {
            if (is == null) return 0;
            ExifInterface exif = new ExifInterface(is);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90: return 90;
                case ExifInterface.ORIENTATION_ROTATE_180: return 180;
                case ExifInterface.ORIENTATION_ROTATE_270: return 270;
                default: return 0;
            }
        } catch (Exception e) {
            Log.w(TAG, "Could not read EXIF orientation: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Rotates a Bitmap by degrees. Returns original if degrees == 0.
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        if (bitmap == null || degrees == 0) return bitmap;
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * Safely recycles a Bitmap if non-null and not already recycled.
     */
    public static void safeRecycle(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }
}
