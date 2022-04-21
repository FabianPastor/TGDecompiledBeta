package org.telegram.messenger;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;

public class Bitmaps {
    protected static byte[] footer = {-1, -39};
    protected static byte[] header = {-1, -40, -1, -32, 0, 16, 74, 70, 73, 70, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0, -1, -37, 0, 67, 0, 40, 28, 30, 35, 30, 25, 40, 35, 33, 35, 45, 43, 40, 48, 60, 100, 65, 60, 55, 55, 60, 123, 88, 93, 73, 100, -111, Byte.MIN_VALUE, -103, -106, -113, Byte.MIN_VALUE, -116, -118, -96, -76, -26, -61, -96, -86, -38, -83, -118, -116, -56, -1, -53, -38, -18, -11, -1, -1, -1, -101, -63, -1, -1, -1, -6, -1, -26, -3, -1, -8, -1, -37, 0, 67, 1, 43, 45, 45, 60, 53, 60, 118, 65, 65, 118, -8, -91, -116, -91, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -8, -1, -64, 0, 17, 8, 0, 30, 0, 40, 3, 1, 34, 0, 2, 17, 1, 3, 17, 1, -1, -60, 0, 31, 0, 0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, -1, -60, 0, -75, 16, 0, 2, 1, 3, 3, 2, 4, 3, 5, 5, 4, 4, 0, 0, 1, 125, 1, 2, 3, 0, 4, 17, 5, 18, 33, 49, 65, 6, 19, 81, 97, 7, 34, 113, 20, 50, -127, -111, -95, 8, 35, 66, -79, -63, 21, 82, -47, -16, 36, 51, 98, 114, -126, 9, 10, 22, 23, 24, 25, 26, 37, 38, 39, 40, 41, 42, 52, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, -125, -124, -123, -122, -121, -120, -119, -118, -110, -109, -108, -107, -106, -105, -104, -103, -102, -94, -93, -92, -91, -90, -89, -88, -87, -86, -78, -77, -76, -75, -74, -73, -72, -71, -70, -62, -61, -60, -59, -58, -57, -56, -55, -54, -46, -45, -44, -43, -42, -41, -40, -39, -38, -31, -30, -29, -28, -27, -26, -25, -24, -23, -22, -15, -14, -13, -12, -11, -10, -9, -8, -7, -6, -1, -60, 0, 31, 1, 0, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, -1, -60, 0, -75, 17, 0, 2, 1, 2, 4, 4, 3, 4, 7, 5, 4, 4, 0, 1, 2, 119, 0, 1, 2, 3, 17, 4, 5, 33, 49, 6, 18, 65, 81, 7, 97, 113, 19, 34, 50, -127, 8, 20, 66, -111, -95, -79, -63, 9, 35, 51, 82, -16, 21, 98, 114, -47, 10, 22, 36, 52, -31, 37, -15, 23, 24, 25, 26, 38, 39, 40, 41, 42, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, -126, -125, -124, -123, -122, -121, -120, -119, -118, -110, -109, -108, -107, -106, -105, -104, -103, -102, -94, -93, -92, -91, -90, -89, -88, -87, -86, -78, -77, -76, -75, -74, -73, -72, -71, -70, -62, -61, -60, -59, -58, -57, -56, -55, -54, -46, -45, -44, -43, -42, -41, -40, -39, -38, -30, -29, -28, -27, -26, -25, -24, -23, -22, -14, -13, -12, -11, -10, -9, -8, -7, -6, -1, -38, 0, 12, 3, 1, 0, 2, 17, 3, 17, 0, 63, 0};
    private static final ThreadLocal<byte[]> jpegData = new ThreadLocal<byte[]>() {
        /* access modifiers changed from: protected */
        public byte[] initialValue() {
            return new byte[]{-1, -40, -1, -37, 0, 67, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -64, 0, 17, 8, 0, 0, 0, 0, 3, 1, 34, 0, 2, 17, 0, 3, 17, 0, -1, -60, 0, 31, 0, 0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, -1, -60, 0, -75, 16, 0, 2, 1, 3, 3, 2, 4, 3, 5, 5, 4, 4, 0, 0, 1, 125, 1, 2, 3, 0, 4, 17, 5, 18, 33, 49, 65, 6, 19, 81, 97, 7, 34, 113, 20, 50, -127, -111, -95, 8, 35, 66, -79, -63, 21, 82, -47, -16, 36, 51, 98, 114, -126, 9, 10, 22, 23, 24, 25, 26, 37, 38, 39, 40, 41, 42, 52, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, -125, -124, -123, -122, -121, -120, -119, -118, -110, -109, -108, -107, -106, -105, -104, -103, -102, -94, -93, -92, -91, -90, -89, -88, -87, -86, -78, -77, -76, -75, -74, -73, -72, -71, -70, -62, -61, -60, -59, -58, -57, -56, -55, -54, -46, -45, -44, -43, -42, -41, -40, -39, -38, -31, -30, -29, -28, -27, -26, -25, -24, -23, -22, -15, -14, -13, -12, -11, -10, -9, -8, -7, -6, -1, -60, 0, 31, 1, 0, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, -1, -60, 0, -75, 17, 0, 2, 1, 2, 4, 4, 3, 4, 7, 5, 4, 4, 0, 1, 2, 119, 0, 1, 2, 3, 17, 4, 5, 33, 49, 6, 18, 65, 81, 7, 97, 113, 19, 34, 50, -127, 8, 20, 66, -111, -95, -79, -63, 9, 35, 51, 82, -16, 21, 98, 114, -47, 10, 22, 36, 52, -31, 37, -15, 23, 24, 25, 26, 38, 39, 40, 41, 42, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, -126, -125, -124, -123, -122, -121, -120, -119, -118, -110, -109, -108, -107, -106, -105, -104, -103, -102, -94, -93, -92, -91, -90, -89, -88, -87, -86, -78, -77, -76, -75, -74, -73, -72, -71, -70, -62, -61, -60, -59, -58, -57, -56, -55, -54, -46, -45, -44, -43, -42, -41, -40, -39, -38, -30, -29, -28, -27, -26, -25, -24, -23, -22, -14, -13, -12, -11, -10, -9, -8, -7, -6, -1, -38, 0, 12, 3, 1, 0, 2, 17, 3, 17, 0, 63, 0, -114, -118, 40, -96, 15, -1, -39};
        }
    };
    private static volatile Matrix sScaleMatrix;

    public static Bitmap createBitmap(int width, int height, Bitmap.Config config) {
        Bitmap bitmap;
        if (Build.VERSION.SDK_INT < 21) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDither = true;
            options.inPreferredConfig = config;
            options.inPurgeable = true;
            options.inSampleSize = 1;
            options.inMutable = true;
            byte[] array = jpegData.get();
            array[76] = (byte) (height >> 8);
            array[77] = (byte) (height & 255);
            array[78] = (byte) (width >> 8);
            array[79] = (byte) (width & 255);
            bitmap = BitmapFactory.decodeByteArray(array, 0, array.length, options);
            Utilities.pinBitmap(bitmap);
            bitmap.setHasAlpha(true);
            bitmap.eraseColor(0);
        } else {
            bitmap = Bitmap.createBitmap(width, height, config);
        }
        if (config == Bitmap.Config.ARGB_8888 || config == Bitmap.Config.ARGB_4444) {
            bitmap.eraseColor(0);
        }
        return bitmap;
    }

    private static void checkXYSign(int x, int y) {
        if (x < 0) {
            throw new IllegalArgumentException("x must be >= 0");
        } else if (y < 0) {
            throw new IllegalArgumentException("y must be >= 0");
        }
    }

    private static void checkWidthHeight(int width, int height) {
        if (width <= 0) {
            throw new IllegalArgumentException("width must be > 0");
        } else if (height <= 0) {
            throw new IllegalArgumentException("height must be > 0");
        }
    }

    public static Bitmap createBitmap(Bitmap source, int x, int y, int width, int height, Matrix m, boolean filter) {
        Bitmap bitmap;
        Paint paint;
        Bitmap bitmap2 = source;
        int i = x;
        int i2 = y;
        int i3 = width;
        int i4 = height;
        Matrix matrix = m;
        if (Build.VERSION.SDK_INT >= 21) {
            return Bitmap.createBitmap(source, x, y, width, height, m, filter);
        }
        checkXYSign(x, y);
        checkWidthHeight(width, height);
        if (i + i3 > source.getWidth()) {
            throw new IllegalArgumentException("x + width must be <= bitmap.width()");
        } else if (i2 + i4 > source.getHeight()) {
            throw new IllegalArgumentException("y + height must be <= bitmap.height()");
        } else if (!source.isMutable() && i == 0 && i2 == 0 && i3 == source.getWidth() && i4 == source.getHeight() && (matrix == null || m.isIdentity())) {
            return bitmap2;
        } else {
            int neww = width;
            int newh = height;
            Canvas canvas = new Canvas();
            Rect srcR = new Rect(i, i2, i + i3, i2 + i4);
            RectF dstR = new RectF(0.0f, 0.0f, (float) i3, (float) i4);
            Bitmap.Config newConfig = Bitmap.Config.ARGB_8888;
            Bitmap.Config config = source.getConfig();
            if (config != null) {
                switch (AnonymousClass2.$SwitchMap$android$graphics$Bitmap$Config[config.ordinal()]) {
                    case 1:
                        newConfig = Bitmap.Config.ARGB_8888;
                        break;
                    case 2:
                        newConfig = Bitmap.Config.ALPHA_8;
                        break;
                    default:
                        newConfig = Bitmap.Config.ARGB_8888;
                        break;
                }
            }
            if (matrix == null || m.isIdentity()) {
                bitmap = createBitmap(neww, newh, newConfig);
                paint = null;
                int i5 = neww;
            } else {
                boolean transformed = !m.rectStaysRect();
                RectF deviceR = new RectF();
                matrix.mapRect(deviceR, dstR);
                int neww2 = Math.round(deviceR.width());
                bitmap = createBitmap(neww2, Math.round(deviceR.height()), transformed ? Bitmap.Config.ARGB_8888 : newConfig);
                int i6 = neww2;
                canvas.translate(-deviceR.left, -deviceR.top);
                canvas.concat(matrix);
                Paint paint2 = new Paint();
                paint2.setFilterBitmap(filter);
                if (transformed) {
                    paint2.setAntiAlias(true);
                }
                paint = paint2;
            }
            bitmap.setDensity(source.getDensity());
            bitmap.setHasAlpha(source.hasAlpha());
            if (Build.VERSION.SDK_INT >= 19) {
                bitmap.setPremultiplied(source.isPremultiplied());
            }
            canvas.setBitmap(bitmap);
            canvas.drawBitmap(bitmap2, srcR, dstR, paint);
            try {
                canvas.setBitmap((Bitmap) null);
            } catch (Exception e) {
            }
            return bitmap;
        }
    }

    /* renamed from: org.telegram.messenger.Bitmaps$2  reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$android$graphics$Bitmap$Config;

        static {
            int[] iArr = new int[Bitmap.Config.values().length];
            $SwitchMap$android$graphics$Bitmap$Config = iArr;
            try {
                iArr[Bitmap.Config.RGB_565.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Bitmap.Config.ALPHA_8.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Bitmap.Config.ARGB_4444.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Bitmap.Config.ARGB_8888.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public static Bitmap createBitmap(Bitmap source, int x, int y, int width, int height) {
        return createBitmap(source, x, y, width, height, (Matrix) null, false);
    }

    /* JADX INFO: finally extract failed */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0057, code lost:
        r0 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Bitmap createScaledBitmap(android.graphics.Bitmap r15, int r16, int r17, boolean r18) {
        /*
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 21
            if (r0 < r1) goto L_0x000b
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r15, r16, r17, r18)
            return r0
        L_0x000b:
            java.lang.Class<android.graphics.Bitmap> r1 = android.graphics.Bitmap.class
            monitor-enter(r1)
            android.graphics.Matrix r0 = sScaleMatrix     // Catch:{ all -> 0x0050 }
            r2 = 0
            sScaleMatrix = r2     // Catch:{ all -> 0x0050 }
            monitor-exit(r1)     // Catch:{ all -> 0x0050 }
            if (r0 != 0) goto L_0x001e
            android.graphics.Matrix r1 = new android.graphics.Matrix
            r1.<init>()
            r0 = r1
            r8 = r0
            goto L_0x001f
        L_0x001e:
            r8 = r0
        L_0x001f:
            int r9 = r15.getWidth()
            int r10 = r15.getHeight()
            r11 = r16
            float r0 = (float) r11
            float r1 = (float) r9
            float r12 = r0 / r1
            r13 = r17
            float r0 = (float) r13
            float r1 = (float) r10
            float r14 = r0 / r1
            r8.setScale(r12, r14)
            r2 = 0
            r3 = 0
            r1 = r15
            r4 = r9
            r5 = r10
            r6 = r8
            r7 = r18
            android.graphics.Bitmap r2 = createBitmap(r1, r2, r3, r4, r5, r6, r7)
            java.lang.Class<android.graphics.Bitmap> r3 = android.graphics.Bitmap.class
            monitor-enter(r3)
            android.graphics.Matrix r0 = sScaleMatrix     // Catch:{ all -> 0x004d }
            if (r0 != 0) goto L_0x004b
            sScaleMatrix = r8     // Catch:{ all -> 0x004d }
        L_0x004b:
            monitor-exit(r3)     // Catch:{ all -> 0x004d }
            return r2
        L_0x004d:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x004d }
            throw r0
        L_0x0050:
            r0 = move-exception
            r11 = r16
            r13 = r17
        L_0x0055:
            monitor-exit(r1)     // Catch:{ all -> 0x0057 }
            throw r0
        L_0x0057:
            r0 = move-exception
            goto L_0x0055
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.Bitmaps.createScaledBitmap(android.graphics.Bitmap, int, int, boolean):android.graphics.Bitmap");
    }
}
