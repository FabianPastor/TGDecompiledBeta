package net.hockeyapp.android.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {
    public static int determineOrientation(File file) throws IOException {
        Throwable th;
        InputStream input = null;
        try {
            InputStream input2 = new FileInputStream(file);
            try {
                int determineOrientation = determineOrientation(input2);
                if (input2 != null) {
                    input2.close();
                }
                return determineOrientation;
            } catch (Throwable th2) {
                th = th2;
                input = input2;
                if (input != null) {
                    input.close();
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            if (input != null) {
                input.close();
            }
            throw th;
        }
    }

    public static int determineOrientation(Context context, Uri uri) {
        int determineOrientation;
        InputStream input = null;
        try {
            input = context.getContentResolver().openInputStream(uri);
            determineOrientation = determineOrientation(input);
            if (input != null) {
                try {
                    input.close();
                } catch (Throwable e) {
                    HockeyLog.error("Unable to close input stream.", e);
                }
            }
        } catch (Throwable e2) {
            HockeyLog.error("Unable to determine necessary screen orientation.", e2);
            determineOrientation = 1;
            if (input != null) {
                try {
                    input.close();
                } catch (Throwable e22) {
                    HockeyLog.error("Unable to close input stream.", e22);
                }
            }
        } catch (Throwable th) {
            if (input != null) {
                try {
                    input.close();
                } catch (Throwable e222) {
                    HockeyLog.error("Unable to close input stream.", e222);
                }
            }
        }
        return determineOrientation;
    }

    public static int determineOrientation(InputStream input) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(input, null, options);
        if (options.outWidth == -1 || options.outHeight == -1 || ((float) options.outWidth) / ((float) options.outHeight) <= 1.0f) {
            return 1;
        }
        return 0;
    }

    public static Bitmap decodeSampledBitmap(File file, int reqWidth, int reqHeight) throws IOException {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    public static Bitmap decodeSampledBitmap(Context context, Uri imageUri, int reqWidth, int reqHeight) throws IOException {
        InputStream inputBounds = null;
        InputStream inputBitmap = null;
        try {
            Options options = new Options();
            options.inJustDecodeBounds = true;
            inputBounds = context.getContentResolver().openInputStream(imageUri);
            BitmapFactory.decodeStream(inputBounds, null, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            inputBitmap = context.getContentResolver().openInputStream(imageUri);
            Bitmap decodeStream = BitmapFactory.decodeStream(inputBitmap, null, options);
            return decodeStream;
        } finally {
            if (inputBounds != null) {
                inputBounds.close();
            }
            if (inputBitmap != null) {
                inputBitmap.close();
            }
        }
    }

    private static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;
            while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
