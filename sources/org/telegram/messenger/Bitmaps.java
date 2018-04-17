package org.telegram.messenger;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build.VERSION;

public class Bitmaps {
    private static final ThreadLocal<byte[]> jpegData = new C00701();
    private static volatile Matrix sScaleMatrix;

    /* renamed from: org.telegram.messenger.Bitmaps$1 */
    static class C00701 extends ThreadLocal<byte[]> {
        C00701() {
        }

        protected byte[] initialValue() {
            return new byte[]{(byte) -1, (byte) -40, (byte) -1, (byte) -37, (byte) 0, (byte) 67, (byte) 0, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -64, (byte) 0, (byte) 17, (byte) 8, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 3, (byte) 1, (byte) 34, (byte) 0, (byte) 2, (byte) 17, (byte) 0, (byte) 3, (byte) 17, (byte) 0, (byte) -1, (byte) -60, (byte) 0, (byte) 31, (byte) 0, (byte) 0, (byte) 1, (byte) 5, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9, (byte) 10, (byte) 11, (byte) -1, (byte) -60, (byte) 0, (byte) -75, (byte) 16, (byte) 0, (byte) 2, (byte) 1, (byte) 3, (byte) 3, (byte) 2, (byte) 4, (byte) 3, (byte) 5, (byte) 5, (byte) 4, (byte) 4, (byte) 0, (byte) 0, (byte) 1, (byte) 125, (byte) 1, (byte) 2, (byte) 3, (byte) 0, (byte) 4, (byte) 17, (byte) 5, (byte) 18, (byte) 33, (byte) 49, (byte) 65, (byte) 6, (byte) 19, (byte) 81, (byte) 97, (byte) 7, (byte) 34, (byte) 113, (byte) 20, (byte) 50, (byte) -127, (byte) -111, (byte) -95, (byte) 8, (byte) 35, (byte) 66, (byte) -79, (byte) -63, (byte) 21, (byte) 82, (byte) -47, (byte) -16, (byte) 36, (byte) 51, (byte) 98, (byte) 114, (byte) -126, (byte) 9, (byte) 10, (byte) 22, (byte) 23, (byte) 24, (byte) 25, (byte) 26, (byte) 37, (byte) 38, (byte) 39, (byte) 40, (byte) 41, (byte) 42, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 58, (byte) 67, (byte) 68, (byte) 69, (byte) 70, (byte) 71, (byte) 72, (byte) 73, (byte) 74, (byte) 83, (byte) 84, (byte) 85, (byte) 86, (byte) 87, (byte) 88, (byte) 89, (byte) 90, (byte) 99, (byte) 100, (byte) 101, (byte) 102, (byte) 103, (byte) 104, (byte) 105, (byte) 106, (byte) 115, (byte) 116, (byte) 117, (byte) 118, (byte) 119, (byte) 120, (byte) 121, (byte) 122, (byte) -125, (byte) -124, (byte) -123, (byte) -122, (byte) -121, (byte) -120, (byte) -119, (byte) -118, (byte) -110, (byte) -109, (byte) -108, (byte) -107, (byte) -106, (byte) -105, (byte) -104, (byte) -103, (byte) -102, (byte) -94, (byte) -93, (byte) -92, (byte) -91, (byte) -90, (byte) -89, (byte) -88, (byte) -87, (byte) -86, (byte) -78, (byte) -77, (byte) -76, (byte) -75, (byte) -74, (byte) -73, (byte) -72, (byte) -71, (byte) -70, (byte) -62, (byte) -61, (byte) -60, (byte) -59, (byte) -58, (byte) -57, (byte) -56, (byte) -55, (byte) -54, (byte) -46, (byte) -45, (byte) -44, (byte) -43, (byte) -42, (byte) -41, (byte) -40, (byte) -39, (byte) -38, (byte) -31, (byte) -30, (byte) -29, (byte) -28, (byte) -27, (byte) -26, (byte) -25, (byte) -24, (byte) -23, (byte) -22, (byte) -15, (byte) -14, (byte) -13, (byte) -12, (byte) -11, (byte) -10, (byte) -9, (byte) -8, (byte) -7, (byte) -6, (byte) -1, (byte) -60, (byte) 0, (byte) 31, (byte) 1, (byte) 0, (byte) 3, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9, (byte) 10, (byte) 11, (byte) -1, (byte) -60, (byte) 0, (byte) -75, (byte) 17, (byte) 0, (byte) 2, (byte) 1, (byte) 2, (byte) 4, (byte) 4, (byte) 3, (byte) 4, (byte) 7, (byte) 5, (byte) 4, (byte) 4, (byte) 0, (byte) 1, (byte) 2, (byte) 119, (byte) 0, (byte) 1, (byte) 2, (byte) 3, (byte) 17, (byte) 4, (byte) 5, (byte) 33, (byte) 49, (byte) 6, (byte) 18, (byte) 65, (byte) 81, (byte) 7, (byte) 97, (byte) 113, (byte) 19, (byte) 34, (byte) 50, (byte) -127, (byte) 8, (byte) 20, (byte) 66, (byte) -111, (byte) -95, (byte) -79, (byte) -63, (byte) 9, (byte) 35, (byte) 51, (byte) 82, (byte) -16, (byte) 21, (byte) 98, (byte) 114, (byte) -47, (byte) 10, (byte) 22, (byte) 36, (byte) 52, (byte) -31, (byte) 37, (byte) -15, (byte) 23, (byte) 24, (byte) 25, (byte) 26, (byte) 38, (byte) 39, (byte) 40, (byte) 41, (byte) 42, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 58, (byte) 67, (byte) 68, (byte) 69, (byte) 70, (byte) 71, (byte) 72, (byte) 73, (byte) 74, (byte) 83, (byte) 84, (byte) 85, (byte) 86, (byte) 87, (byte) 88, (byte) 89, (byte) 90, (byte) 99, (byte) 100, (byte) 101, (byte) 102, (byte) 103, (byte) 104, (byte) 105, (byte) 106, (byte) 115, (byte) 116, (byte) 117, (byte) 118, (byte) 119, (byte) 120, (byte) 121, (byte) 122, (byte) -126, (byte) -125, (byte) -124, (byte) -123, (byte) -122, (byte) -121, (byte) -120, (byte) -119, (byte) -118, (byte) -110, (byte) -109, (byte) -108, (byte) -107, (byte) -106, (byte) -105, (byte) -104, (byte) -103, (byte) -102, (byte) -94, (byte) -93, (byte) -92, (byte) -91, (byte) -90, (byte) -89, (byte) -88, (byte) -87, (byte) -86, (byte) -78, (byte) -77, (byte) -76, (byte) -75, (byte) -74, (byte) -73, (byte) -72, (byte) -71, (byte) -70, (byte) -62, (byte) -61, (byte) -60, (byte) -59, (byte) -58, (byte) -57, (byte) -56, (byte) -55, (byte) -54, (byte) -46, (byte) -45, (byte) -44, (byte) -43, (byte) -42, (byte) -41, (byte) -40, (byte) -39, (byte) -38, (byte) -30, (byte) -29, (byte) -28, (byte) -27, (byte) -26, (byte) -25, (byte) -24, (byte) -23, (byte) -22, (byte) -14, (byte) -13, (byte) -12, (byte) -11, (byte) -10, (byte) -9, (byte) -8, (byte) -7, (byte) -6, (byte) -1, (byte) -38, (byte) 0, (byte) 12, (byte) 3, (byte) 1, (byte) 0, (byte) 2, (byte) 17, (byte) 3, (byte) 17, (byte) 0, (byte) 63, (byte) 0, (byte) -114, (byte) -118, (byte) 40, (byte) -96, (byte) 15, (byte) -1, (byte) -39};
        }
    }

    /* renamed from: org.telegram.messenger.Bitmaps$2 */
    static /* synthetic */ class C00712 {
        static final /* synthetic */ int[] $SwitchMap$android$graphics$Bitmap$Config = new int[Config.values().length];

        static {
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Config.RGB_565.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Config.ALPHA_8.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Config.ARGB_4444.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Config.ARGB_8888.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public static android.graphics.Bitmap createScaledBitmap(android.graphics.Bitmap r16, int r17, int r18, boolean r19) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.fixSplitterBlock(BlockFinish.java:63)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:34)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 21;
        if (r1 < r2) goto L_0x000b;
    L_0x0006:
        r1 = android.graphics.Bitmap.createScaledBitmap(r16, r17, r18, r19);
        return r1;
    L_0x000b:
        r1 = android.graphics.Bitmap.class;
        monitor-enter(r1);
        r2 = sScaleMatrix;	 Catch:{ all -> 0x004f }
        r3 = 0;	 Catch:{ all -> 0x004f }
        sScaleMatrix = r3;	 Catch:{ all -> 0x004f }
        monitor-exit(r1);	 Catch:{ all -> 0x004f }
        if (r2 != 0) goto L_0x001c;
    L_0x0016:
        r1 = new android.graphics.Matrix;
        r1.<init>();
        r2 = r1;
    L_0x001c:
        r10 = r16.getWidth();
        r11 = r16.getHeight();
        r12 = r17;
        r1 = (float) r12;
        r3 = (float) r10;
        r13 = r1 / r3;
        r14 = r18;
        r1 = (float) r14;
        r3 = (float) r11;
        r15 = r1 / r3;
        r2.setScale(r13, r15);
        r4 = 0;
        r5 = 0;
        r3 = r16;
        r6 = r10;
        r7 = r11;
        r8 = r2;
        r9 = r19;
        r3 = createBitmap(r3, r4, r5, r6, r7, r8, r9);
        r4 = android.graphics.Bitmap.class;
        monitor-enter(r4);
        r1 = sScaleMatrix;	 Catch:{ all -> 0x004b }
        if (r1 != 0) goto L_0x0049;	 Catch:{ all -> 0x004b }
    L_0x0047:
        sScaleMatrix = r2;	 Catch:{ all -> 0x004b }
    L_0x0049:
        monitor-exit(r4);	 Catch:{ all -> 0x004b }
        return r3;	 Catch:{ all -> 0x004b }
    L_0x004b:
        r0 = move-exception;	 Catch:{ all -> 0x004b }
        r1 = r0;	 Catch:{ all -> 0x004b }
        monitor-exit(r4);	 Catch:{ all -> 0x004b }
        throw r1;
    L_0x004f:
        r0 = move-exception;
        r12 = r17;
        r14 = r18;
    L_0x0054:
        r2 = r0;
        monitor-exit(r1);	 Catch:{ all -> 0x0057 }
        throw r2;
    L_0x0057:
        r0 = move-exception;
        goto L_0x0054;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.Bitmaps.createScaledBitmap(android.graphics.Bitmap, int, int, boolean):android.graphics.Bitmap");
    }

    public static Bitmap createBitmap(int width, int height, Config config) {
        Bitmap bitmap;
        if (VERSION.SDK_INT < 21) {
            Options options = new Options();
            options.inDither = true;
            options.inPreferredConfig = config;
            options.inPurgeable = true;
            options.inSampleSize = 1;
            options.inMutable = true;
            byte[] array = (byte[]) jpegData.get();
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
        Bitmap bitmap2 = bitmap;
        if (config == Config.ARGB_8888 || config == Config.ARGB_4444) {
            bitmap2.eraseColor(0);
        }
        return bitmap2;
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
        Bitmap bitmap = source;
        int i = x;
        int i2 = y;
        int i3 = width;
        int i4 = height;
        Matrix matrix = m;
        if (VERSION.SDK_INT >= 21) {
            return Bitmap.createBitmap(source, x, y, width, height, m, filter);
        }
        checkXYSign(x, y);
        checkWidthHeight(width, height);
        if (i + i3 > source.getWidth()) {
            throw new IllegalArgumentException("x + width must be <= bitmap.width()");
        } else if (i2 + i4 > source.getHeight()) {
            throw new IllegalArgumentException("y + height must be <= bitmap.height()");
        } else if (!source.isMutable() && i == 0 && i2 == 0 && i3 == source.getWidth() && i4 == source.getHeight() && (matrix == null || m.isIdentity())) {
            return bitmap;
        } else {
            Paint paint;
            Bitmap bitmap2;
            int neww = i3;
            int newh = i4;
            Canvas canvas = new Canvas();
            Rect srcR = new Rect(i, i2, i + i3, i2 + i4);
            RectF dstR = new RectF(0.0f, 0.0f, (float) i3, (float) i4);
            Config newConfig = Config.ARGB_8888;
            Config config = source.getConfig();
            if (config != null) {
                switch (C00712.$SwitchMap$android$graphics$Bitmap$Config[config.ordinal()]) {
                    case 1:
                        newConfig = Config.ARGB_8888;
                        break;
                    case 2:
                        newConfig = Config.ALPHA_8;
                        break;
                    default:
                        newConfig = Config.ARGB_8888;
                        break;
                }
            }
            if (matrix != null) {
                if (!m.isIdentity()) {
                    boolean transformed = m.rectStaysRect() ^ true;
                    RectF deviceR = new RectF();
                    matrix.mapRect(deviceR, dstR);
                    Bitmap bitmap3 = createBitmap(Math.round(deviceR.width()), Math.round(deviceR.height()), transformed ? Config.ARGB_8888 : newConfig);
                    canvas.translate(-deviceR.left, -deviceR.top);
                    canvas.concat(matrix);
                    Paint paint2 = new Paint();
                    paint2.setFilterBitmap(filter);
                    if (transformed) {
                        paint2.setAntiAlias(true);
                    }
                    paint = paint2;
                    bitmap2 = bitmap3;
                    bitmap2.setDensity(source.getDensity());
                    bitmap2.setHasAlpha(source.hasAlpha());
                    if (VERSION.SDK_INT >= 19) {
                        bitmap2.setPremultiplied(source.isPremultiplied());
                    }
                    canvas.setBitmap(bitmap2);
                    canvas.drawBitmap(bitmap, srcR, dstR, paint);
                    canvas.setBitmap(null);
                    return bitmap2;
                }
            }
            bitmap2 = createBitmap(neww, newh, newConfig);
            paint = null;
            bitmap2.setDensity(source.getDensity());
            bitmap2.setHasAlpha(source.hasAlpha());
            if (VERSION.SDK_INT >= 19) {
                bitmap2.setPremultiplied(source.isPremultiplied());
            }
            canvas.setBitmap(bitmap2);
            canvas.drawBitmap(bitmap, srcR, dstR, paint);
            try {
                canvas.setBitmap(null);
            } catch (Exception e) {
            }
            return bitmap2;
        }
    }

    public static Bitmap createBitmap(Bitmap source, int x, int y, int width, int height) {
        return createBitmap(source, x, y, width, height, null, false);
    }
}
