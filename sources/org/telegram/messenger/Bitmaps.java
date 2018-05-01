package org.telegram.messenger;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
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
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
            /*
            r0 = android.graphics.Bitmap.Config.values();
            r0 = r0.length;
            r0 = new int[r0];
            $SwitchMap$android$graphics$Bitmap$Config = r0;
            r0 = $SwitchMap$android$graphics$Bitmap$Config;	 Catch:{ NoSuchFieldError -> 0x0014 }
            r1 = android.graphics.Bitmap.Config.RGB_565;	 Catch:{ NoSuchFieldError -> 0x0014 }
            r1 = r1.ordinal();	 Catch:{ NoSuchFieldError -> 0x0014 }
            r2 = 1;	 Catch:{ NoSuchFieldError -> 0x0014 }
            r0[r1] = r2;	 Catch:{ NoSuchFieldError -> 0x0014 }
        L_0x0014:
            r0 = $SwitchMap$android$graphics$Bitmap$Config;	 Catch:{ NoSuchFieldError -> 0x001f }
            r1 = android.graphics.Bitmap.Config.ALPHA_8;	 Catch:{ NoSuchFieldError -> 0x001f }
            r1 = r1.ordinal();	 Catch:{ NoSuchFieldError -> 0x001f }
            r2 = 2;	 Catch:{ NoSuchFieldError -> 0x001f }
            r0[r1] = r2;	 Catch:{ NoSuchFieldError -> 0x001f }
        L_0x001f:
            r0 = $SwitchMap$android$graphics$Bitmap$Config;	 Catch:{ NoSuchFieldError -> 0x002a }
            r1 = android.graphics.Bitmap.Config.ARGB_4444;	 Catch:{ NoSuchFieldError -> 0x002a }
            r1 = r1.ordinal();	 Catch:{ NoSuchFieldError -> 0x002a }
            r2 = 3;	 Catch:{ NoSuchFieldError -> 0x002a }
            r0[r1] = r2;	 Catch:{ NoSuchFieldError -> 0x002a }
        L_0x002a:
            r0 = $SwitchMap$android$graphics$Bitmap$Config;	 Catch:{ NoSuchFieldError -> 0x0035 }
            r1 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ NoSuchFieldError -> 0x0035 }
            r1 = r1.ordinal();	 Catch:{ NoSuchFieldError -> 0x0035 }
            r2 = 4;	 Catch:{ NoSuchFieldError -> 0x0035 }
            r0[r1] = r2;	 Catch:{ NoSuchFieldError -> 0x0035 }
        L_0x0035:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.Bitmaps.2.<clinit>():void");
        }
    }

    public static Bitmap createBitmap(int i, int i2, Config config) {
        if (VERSION.SDK_INT < 21) {
            Options options = new Options();
            options.inDither = true;
            options.inPreferredConfig = config;
            options.inPurgeable = true;
            options.inSampleSize = 1;
            options.inMutable = true;
            byte[] bArr = (byte[]) jpegData.get();
            bArr[76] = (byte) (i2 >> 8);
            bArr[77] = (byte) (i2 & 255);
            bArr[78] = (byte) (i >> 8);
            bArr[79] = (byte) (i & 255);
            i = BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
            Utilities.pinBitmap(i);
            i.setHasAlpha(true);
            i.eraseColor(0);
        } else {
            i = Bitmap.createBitmap(i, i2, config);
        }
        if (config == Config.ARGB_8888 || config == Config.ARGB_4444) {
            i.eraseColor(0);
        }
        return i;
    }

    private static void checkXYSign(int i, int i2) {
        if (i < 0) {
            throw new IllegalArgumentException("x must be >= 0");
        } else if (i2 < 0) {
            throw new IllegalArgumentException("y must be >= 0");
        }
    }

    private static void checkWidthHeight(int i, int i2) {
        if (i <= 0) {
            throw new IllegalArgumentException("width must be > 0");
        } else if (i2 <= 0) {
            throw new IllegalArgumentException("height must be > 0");
        }
    }

    public static android.graphics.Bitmap createBitmap(android.graphics.Bitmap r6, int r7, int r8, int r9, int r10, android.graphics.Matrix r11, boolean r12) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
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
        r0 = android.os.Build.VERSION.SDK_INT;
        r1 = 21;
        if (r0 < r1) goto L_0x000b;
    L_0x0006:
        r6 = android.graphics.Bitmap.createBitmap(r6, r7, r8, r9, r10, r11, r12);
        return r6;
    L_0x000b:
        checkXYSign(r7, r8);
        checkWidthHeight(r9, r10);
        r0 = r7 + r9;
        r1 = r6.getWidth();
        if (r0 <= r1) goto L_0x0021;
    L_0x0019:
        r6 = new java.lang.IllegalArgumentException;
        r7 = "x + width must be <= bitmap.width()";
        r6.<init>(r7);
        throw r6;
    L_0x0021:
        r1 = r8 + r10;
        r2 = r6.getHeight();
        if (r1 <= r2) goto L_0x0031;
    L_0x0029:
        r6 = new java.lang.IllegalArgumentException;
        r7 = "y + height must be <= bitmap.height()";
        r6.<init>(r7);
        throw r6;
    L_0x0031:
        r2 = r6.isMutable();
        if (r2 != 0) goto L_0x0050;
    L_0x0037:
        if (r7 != 0) goto L_0x0050;
    L_0x0039:
        if (r8 != 0) goto L_0x0050;
    L_0x003b:
        r2 = r6.getWidth();
        if (r9 != r2) goto L_0x0050;
    L_0x0041:
        r2 = r6.getHeight();
        if (r10 != r2) goto L_0x0050;
    L_0x0047:
        if (r11 == 0) goto L_0x004f;
    L_0x0049:
        r2 = r11.isIdentity();
        if (r2 == 0) goto L_0x0050;
    L_0x004f:
        return r6;
    L_0x0050:
        r2 = new android.graphics.Canvas;
        r2.<init>();
        r3 = new android.graphics.Rect;
        r3.<init>(r7, r8, r0, r1);
        r7 = new android.graphics.RectF;
        r8 = (float) r9;
        r0 = (float) r10;
        r1 = 0;
        r7.<init>(r1, r1, r8, r0);
        r8 = android.graphics.Bitmap.Config.ARGB_8888;
        r0 = r6.getConfig();
        if (r0 == 0) goto L_0x007d;
    L_0x006a:
        r8 = org.telegram.messenger.Bitmaps.C00712.$SwitchMap$android$graphics$Bitmap$Config;
        r0 = r0.ordinal();
        r8 = r8[r0];
        switch(r8) {
            case 1: goto L_0x007b;
            case 2: goto L_0x0078;
            default: goto L_0x0075;
        };
    L_0x0075:
        r8 = android.graphics.Bitmap.Config.ARGB_8888;
        goto L_0x007d;
    L_0x0078:
        r8 = android.graphics.Bitmap.Config.ALPHA_8;
        goto L_0x007d;
    L_0x007b:
        r8 = android.graphics.Bitmap.Config.ARGB_8888;
    L_0x007d:
        r0 = 0;
        if (r11 == 0) goto L_0x00c7;
    L_0x0080:
        r1 = r11.isIdentity();
        if (r1 == 0) goto L_0x0087;
    L_0x0086:
        goto L_0x00c7;
    L_0x0087:
        r9 = r11.rectStaysRect();
        r10 = 1;
        r9 = r9 ^ r10;
        r1 = new android.graphics.RectF;
        r1.<init>();
        r11.mapRect(r1, r7);
        r4 = r1.width();
        r4 = java.lang.Math.round(r4);
        r5 = r1.height();
        r5 = java.lang.Math.round(r5);
        if (r9 == 0) goto L_0x00a9;
    L_0x00a7:
        r8 = android.graphics.Bitmap.Config.ARGB_8888;
    L_0x00a9:
        r8 = createBitmap(r4, r5, r8);
        r4 = r1.left;
        r4 = -r4;
        r1 = r1.top;
        r1 = -r1;
        r2.translate(r4, r1);
        r2.concat(r11);
        r11 = new android.graphics.Paint;
        r11.<init>();
        r11.setFilterBitmap(r12);
        if (r9 == 0) goto L_0x00cc;
    L_0x00c3:
        r11.setAntiAlias(r10);
        goto L_0x00cc;
    L_0x00c7:
        r8 = createBitmap(r9, r10, r8);
        r11 = r0;
    L_0x00cc:
        r9 = r6.getDensity();
        r8.setDensity(r9);
        r9 = r6.hasAlpha();
        r8.setHasAlpha(r9);
        r9 = android.os.Build.VERSION.SDK_INT;
        r10 = 19;
        if (r9 < r10) goto L_0x00e7;
    L_0x00e0:
        r9 = r6.isPremultiplied();
        r8.setPremultiplied(r9);
    L_0x00e7:
        r2.setBitmap(r8);
        r2.drawBitmap(r6, r3, r7, r11);
        r2.setBitmap(r0);	 Catch:{ Exception -> 0x00f0 }
    L_0x00f0:
        return r8;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.Bitmaps.createBitmap(android.graphics.Bitmap, int, int, int, int, android.graphics.Matrix, boolean):android.graphics.Bitmap");
    }

    public static Bitmap createBitmap(Bitmap bitmap, int i, int i2, int i3, int i4) {
        return createBitmap(bitmap, i, i2, i3, i4, null, false);
    }

    public static Bitmap createScaledBitmap(Bitmap bitmap, int i, int i2, boolean z) {
        if (VERSION.SDK_INT >= 21) {
            return Bitmap.createScaledBitmap(bitmap, i, i2, z);
        }
        Matrix matrix;
        synchronized (Bitmap.class) {
            matrix = sScaleMatrix;
            sScaleMatrix = null;
        }
        if (matrix == null) {
            matrix = new Matrix();
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        matrix.setScale(((float) i) / ((float) width), ((float) i2) / ((float) height));
        bitmap = createBitmap(bitmap, 0, 0, width, height, matrix, z);
        synchronized (Bitmap.class) {
            if (sScaleMatrix == 0) {
                sScaleMatrix = matrix;
            }
        }
        return bitmap;
    }
}
