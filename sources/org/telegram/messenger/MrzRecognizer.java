package org.telegram.messenger;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Base64;
import android.util.SparseArray;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.barcode.BarcodeDetector.Builder;
import java.util.Calendar;
import java.util.HashMap;

public class MrzRecognizer {

    public static class Result {
        public static final int GENDER_FEMALE = 2;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_UNKNOWN = 0;
        public static final int TYPE_DRIVER_LICENSE = 4;
        public static final int TYPE_ID = 2;
        public static final int TYPE_INTERNAL_PASSPORT = 3;
        public static final int TYPE_PASSPORT = 1;
        public int birthDay;
        public int birthMonth;
        public int birthYear;
        public boolean doesNotExpire;
        public int expiryDay;
        public int expiryMonth;
        public int expiryYear;
        public String firstName;
        public int gender;
        public String issuingCountry;
        public String lastName;
        public boolean mainCheckDigitIsValid;
        public String middleName;
        public String nationality;
        public String number;
        public String rawMRZ;
        public int type;
    }

    private static native Rect[][] binarizeAndFindCharacters(Bitmap bitmap, Bitmap bitmap2);

    private static native int[] findCornerPoints(Bitmap bitmap);

    private static native String performRecognition(Bitmap bitmap, int i, int i2, AssetManager assetManager);

    private static native void setYuvBitmapPixels(Bitmap bitmap, byte[] bArr);

    public static Result recognize(Bitmap bitmap, boolean tryDriverLicenseFirst) {
        Result res;
        Result result = null;
        if (tryDriverLicenseFirst) {
            result = recognizeBarcode(bitmap);
            if (result != null) {
                res = result;
                return result;
            }
        }
        try {
            result = recognizeMRZ(bitmap);
            if (result != null) {
                res = result;
                return result;
            }
        } catch (Exception e) {
        }
        if (!tryDriverLicenseFirst) {
            result = recognizeBarcode(bitmap);
            if (result != null) {
                res = result;
                return result;
            }
        }
        res = result;
        return null;
    }

    private static Result recognizeBarcode(Bitmap bitmap) {
        BarcodeDetector detector = new Builder(ApplicationLoader.applicationContext).build();
        if (bitmap.getWidth() > 1500 || bitmap.getHeight() > 1500) {
            float scale = 1500.0f / ((float) Math.max(bitmap.getWidth(), bitmap.getHeight()));
            bitmap = Bitmap.createScaledBitmap(bitmap, Math.round(((float) bitmap.getWidth()) * scale), Math.round(((float) bitmap.getHeight()) * scale), true);
        }
        SparseArray<Barcode> barcodes = detector.detect(new Frame.Builder().setBitmap(bitmap).build());
        int i = 0;
        while (i < barcodes.size()) {
            Barcode code = (Barcode) barcodes.valueAt(i);
            Result res;
            String str;
            if (code.valueFormat != 12 || code.driverLicense == null) {
                if (code.valueFormat == 7 && code.format == 2048 && code.rawValue.matches("^[A-Za-z0-9=]+$")) {
                    try {
                        String[] data = new String(Base64.decode(code.rawValue, 0), "windows-1251").split("\\|");
                        if (data.length >= 10) {
                            res = new Result();
                            res.type = 4;
                            str = "RU";
                            res.issuingCountry = str;
                            res.nationality = str;
                            res.number = data[0];
                            res.expiryYear = Integer.parseInt(data[2].substring(0, 4));
                            res.expiryMonth = Integer.parseInt(data[2].substring(4, 6));
                            res.expiryDay = Integer.parseInt(data[2].substring(6));
                            res.lastName = capitalize(cyrillicToLatin(data[3]));
                            res.firstName = capitalize(cyrillicToLatin(data[4]));
                            res.middleName = capitalize(cyrillicToLatin(data[5]));
                            res.birthYear = Integer.parseInt(data[6].substring(0, 4));
                            res.birthMonth = Integer.parseInt(data[6].substring(4, 6));
                            res.birthDay = Integer.parseInt(data[6].substring(6));
                            return res;
                        }
                    } catch (Exception e) {
                    }
                }
                i++;
            } else {
                int yearOffset;
                int dayOffset;
                int monthOffset;
                res = new Result();
                res.type = "ID".equals(code.driverLicense.documentType) ? 2 : 4;
                String str2 = code.driverLicense.issuingCountry;
                Object obj = -1;
                switch (str2.hashCode()) {
                    case 66480:
                        if (str2.equals("CAN")) {
                            obj = 1;
                            break;
                        }
                        break;
                    case 84323:
                        if (str2.equals("USA")) {
                            obj = null;
                            break;
                        }
                        break;
                }
                switch (obj) {
                    case null:
                        str = "US";
                        res.issuingCountry = str;
                        res.nationality = str;
                        break;
                    case 1:
                        str = "CA";
                        res.issuingCountry = str;
                        res.nationality = str;
                        break;
                }
                res.firstName = capitalize(code.driverLicense.firstName);
                res.lastName = capitalize(code.driverLicense.lastName);
                res.middleName = capitalize(code.driverLicense.middleName);
                res.number = code.driverLicense.licenseNumber;
                if (code.driverLicense.gender != null) {
                    str2 = code.driverLicense.gender;
                    obj = -1;
                    switch (str2.hashCode()) {
                        case 49:
                            if (str2.equals("1")) {
                                obj = null;
                                break;
                            }
                            break;
                        case 50:
                            if (str2.equals("2")) {
                                obj = 1;
                                break;
                            }
                            break;
                    }
                    switch (obj) {
                        case null:
                            res.gender = 1;
                            break;
                        case 1:
                            res.gender = 2;
                            break;
                    }
                }
                if ("USA".equals(res.issuingCountry)) {
                    yearOffset = 4;
                    dayOffset = 2;
                    monthOffset = 0;
                } else {
                    yearOffset = 0;
                    monthOffset = 4;
                    dayOffset = 6;
                }
                try {
                    if (code.driverLicense.birthDate != null && code.driverLicense.birthDate.length() == 8) {
                        res.birthYear = Integer.parseInt(code.driverLicense.birthDate.substring(yearOffset, yearOffset + 4));
                        res.birthMonth = Integer.parseInt(code.driverLicense.birthDate.substring(monthOffset, monthOffset + 2));
                        res.birthDay = Integer.parseInt(code.driverLicense.birthDate.substring(dayOffset, dayOffset + 2));
                    }
                    if (code.driverLicense.expiryDate == null || code.driverLicense.expiryDate.length() != 8) {
                        return res;
                    }
                    res.expiryYear = Integer.parseInt(code.driverLicense.expiryDate.substring(yearOffset, yearOffset + 4));
                    res.expiryMonth = Integer.parseInt(code.driverLicense.expiryDate.substring(monthOffset, monthOffset + 2));
                    res.expiryDay = Integer.parseInt(code.driverLicense.expiryDate.substring(dayOffset, dayOffset + 2));
                    return res;
                } catch (NumberFormatException e2) {
                    return res;
                }
            }
        }
        return null;
    }

    private static org.telegram.messenger.MrzRecognizer.Result recognizeMRZ(android.graphics.Bitmap r71) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r61_0 'topLeft' android.graphics.Point) in PHI: PHI: (r61_2 'topLeft' android.graphics.Point) = (r61_0 'topLeft' android.graphics.Point), (r61_1 'topLeft' android.graphics.Point) binds: {(r61_0 'topLeft' android.graphics.Point)=B:8:0x0086, (r61_1 'topLeft' android.graphics.Point)=B:9:0x0088}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r53 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r6 = r71.getWidth();
        r8 = 512; // 0x200 float:7.175E-43 double:2.53E-321;
        if (r6 > r8) goto L_0x0012;
    L_0x000a:
        r6 = r71.getHeight();
        r8 = 512; // 0x200 float:7.175E-43 double:2.53E-321;
        if (r6 <= r8) goto L_0x0245;
    L_0x0012:
        r6 = NUM; // 0x44000000 float:512.0 double:5.63655132E-315;
        r8 = r71.getWidth();
        r9 = r71.getHeight();
        r8 = java.lang.Math.max(r8, r9);
        r8 = (float) r8;
        r53 = r6 / r8;
        r6 = r71.getWidth();
        r6 = (float) r6;
        r6 = r6 * r53;
        r6 = java.lang.Math.round(r6);
        r8 = r71.getHeight();
        r8 = (float) r8;
        r8 = r8 * r53;
        r8 = java.lang.Math.round(r8);
        r9 = 1;
        r0 = r71;
        r56 = android.graphics.Bitmap.createScaledBitmap(r0, r6, r8, r9);
    L_0x0040:
        r48 = findCornerPoints(r56);
        r6 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r49 = r6 / r53;
        if (r48 == 0) goto L_0x0249;
    L_0x004a:
        r61 = new android.graphics.Point;
        r6 = 0;
        r6 = r48[r6];
        r8 = 1;
        r8 = r48[r8];
        r0 = r61;
        r0.<init>(r6, r8);
        r64 = new android.graphics.Point;
        r6 = 2;
        r6 = r48[r6];
        r8 = 3;
        r8 = r48[r8];
        r0 = r64;
        r0.<init>(r6, r8);
        r21 = new android.graphics.Point;
        r6 = 4;
        r6 = r48[r6];
        r8 = 5;
        r8 = r48[r8];
        r0 = r21;
        r0.<init>(r6, r8);
        r26 = new android.graphics.Point;
        r6 = 6;
        r6 = r48[r6];
        r8 = 7;
        r8 = r48[r8];
        r0 = r26;
        r0.<init>(r6, r8);
        r0 = r64;
        r6 = r0.x;
        r0 = r61;
        r8 = r0.x;
        if (r6 >= r8) goto L_0x0094;
    L_0x0088:
        r57 = r64;
        r64 = r61;
        r61 = r57;
        r57 = r26;
        r26 = r21;
        r21 = r57;
    L_0x0094:
        r0 = r64;
        r6 = r0.x;
        r0 = r61;
        r8 = r0.x;
        r6 = r6 - r8;
        r8 = (double) r6;
        r0 = r64;
        r6 = r0.y;
        r0 = r61;
        r10 = r0.y;
        r6 = r6 - r10;
        r10 = (double) r6;
        r62 = java.lang.Math.hypot(r8, r10);
        r0 = r26;
        r6 = r0.x;
        r0 = r21;
        r8 = r0.x;
        r6 = r6 - r8;
        r8 = (double) r6;
        r0 = r26;
        r6 = r0.y;
        r0 = r21;
        r10 = r0.y;
        r6 = r6 - r10;
        r10 = (double) r6;
        r24 = java.lang.Math.hypot(r8, r10);
        r0 = r21;
        r6 = r0.x;
        r0 = r61;
        r8 = r0.x;
        r6 = r6 - r8;
        r8 = (double) r6;
        r0 = r21;
        r6 = r0.y;
        r0 = r61;
        r10 = r0.y;
        r6 = r6 - r10;
        r10 = (double) r6;
        r38 = java.lang.Math.hypot(r8, r10);
        r0 = r26;
        r6 = r0.x;
        r0 = r64;
        r8 = r0.x;
        r6 = r6 - r8;
        r8 = (double) r6;
        r0 = r26;
        r6 = r0.y;
        r0 = r64;
        r10 = r0.y;
        r6 = r6 - r10;
        r10 = (double) r6;
        r54 = java.lang.Math.hypot(r8, r10);
        r58 = r62 / r38;
        r66 = r62 / r54;
        r22 = r24 / r38;
        r28 = r24 / r54;
        r8 = 460875867NUM; // 0x3ff599999999999a float:-1.5881868E-23 double:1.35;
        r6 = (r58 > r8 ? 1 : (r58 == r8 ? 0 : -1));
        if (r6 < 0) goto L_0x0200;
    L_0x0105:
        r8 = 461056011NUM; // 0x3ffc00NUM float:0.0 double:1.75;
        r6 = (r58 > r8 ? 1 : (r58 == r8 ? 0 : -1));
        if (r6 > 0) goto L_0x0200;
    L_0x010b:
        r8 = 460875867NUM; // 0x3ff599999999999a float:-1.5881868E-23 double:1.35;
        r6 = (r22 > r8 ? 1 : (r22 == r8 ? 0 : -1));
        if (r6 < 0) goto L_0x0200;
    L_0x0114:
        r8 = 461056011NUM; // 0x3ffc00NUM float:0.0 double:1.75;
        r6 = (r22 > r8 ? 1 : (r22 == r8 ? 0 : -1));
        if (r6 > 0) goto L_0x0200;
    L_0x011a:
        r8 = 460875867NUM; // 0x3ff599999999999a float:-1.5881868E-23 double:1.35;
        r6 = (r66 > r8 ? 1 : (r66 == r8 ? 0 : -1));
        if (r6 < 0) goto L_0x0200;
    L_0x0123:
        r8 = 461056011NUM; // 0x3ffc00NUM float:0.0 double:1.75;
        r6 = (r66 > r8 ? 1 : (r66 == r8 ? 0 : -1));
        if (r6 > 0) goto L_0x0200;
    L_0x0129:
        r8 = 460875867NUM; // 0x3ff599999999999a float:-1.5881868E-23 double:1.35;
        r6 = (r28 > r8 ? 1 : (r28 == r8 ? 0 : -1));
        if (r6 < 0) goto L_0x0200;
    L_0x0132:
        r8 = 461056011NUM; // 0x3ffc00NUM float:0.0 double:1.75;
        r6 = (r28 > r8 ? 1 : (r28 == r8 ? 0 : -1));
        if (r6 > 0) goto L_0x0200;
    L_0x0138:
        r8 = r58 + r66;
        r8 = r8 + r22;
        r8 = r8 + r28;
        r10 = 461618961NUM; // 0x401000NUM float:0.0 double:4.0;
        r16 = r8 / r10;
        r6 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r8 = 465221841NUM; // 0x409000NUM float:0.0 double:1024.0;
        r8 = r8 / r16;
        r8 = java.lang.Math.round(r8);
        r8 = (int) r8;
        r9 = android.graphics.Bitmap.Config.ARGB_8888;
        r57 = android.graphics.Bitmap.createBitmap(r6, r8, r9);
        r27 = new android.graphics.Canvas;
        r0 = r27;
        r1 = r57;
        r0.<init>(r1);
        r6 = 8;
        r7 = new float[r6];
        r6 = 0;
        r8 = 0;
        r7[r6] = r8;
        r6 = 1;
        r8 = 0;
        r7[r6] = r8;
        r6 = 2;
        r8 = r57.getWidth();
        r8 = (float) r8;
        r7[r6] = r8;
        r6 = 3;
        r8 = 0;
        r7[r6] = r8;
        r6 = 4;
        r8 = r57.getWidth();
        r8 = (float) r8;
        r7[r6] = r8;
        r6 = 5;
        r8 = r57.getHeight();
        r8 = (float) r8;
        r7[r6] = r8;
        r6 = 6;
        r8 = 0;
        r7[r6] = r8;
        r6 = 7;
        r8 = r57.getHeight();
        r8 = (float) r8;
        r7[r6] = r8;
        r6 = 8;
        r5 = new float[r6];
        r6 = 0;
        r0 = r61;
        r8 = r0.x;
        r8 = (float) r8;
        r8 = r8 * r49;
        r5[r6] = r8;
        r6 = 1;
        r0 = r61;
        r8 = r0.y;
        r8 = (float) r8;
        r8 = r8 * r49;
        r5[r6] = r8;
        r6 = 2;
        r0 = r64;
        r8 = r0.x;
        r8 = (float) r8;
        r8 = r8 * r49;
        r5[r6] = r8;
        r6 = 3;
        r0 = r64;
        r8 = r0.y;
        r8 = (float) r8;
        r8 = r8 * r49;
        r5[r6] = r8;
        r6 = 4;
        r0 = r26;
        r8 = r0.x;
        r8 = (float) r8;
        r8 = r8 * r49;
        r5[r6] = r8;
        r6 = 5;
        r0 = r26;
        r8 = r0.y;
        r8 = (float) r8;
        r8 = r8 * r49;
        r5[r6] = r8;
        r6 = 6;
        r0 = r21;
        r8 = r0.x;
        r8 = (float) r8;
        r8 = r8 * r49;
        r5[r6] = r8;
        r6 = 7;
        r0 = r21;
        r8 = r0.y;
        r8 = (float) r8;
        r8 = r8 * r49;
        r5[r6] = r8;
        r4 = new android.graphics.Matrix;
        r4.<init>();
        r6 = 0;
        r8 = 0;
        r9 = r5.length;
        r9 = r9 >> 1;
        r4.setPolyToPoly(r5, r6, r7, r8, r9);
        r6 = new android.graphics.Paint;
        r8 = 2;
        r6.<init>(r8);
        r0 = r27;
        r1 = r71;
        r0.drawBitmap(r1, r4, r6);
        r71 = r57;
    L_0x0200:
        r18 = 0;
        r30 = 0;
        r30 = (android.graphics.Rect[][]) r30;
        r42 = 0;
        r41 = 0;
        r36 = 0;
    L_0x020c:
        r6 = 3;
        r0 = r36;
        if (r0 >= r6) goto L_0x02e4;
    L_0x0211:
        r13 = 0;
        r60 = r71;
        switch(r36) {
            case 1: goto L_0x028a;
            case 2: goto L_0x02a4;
            default: goto L_0x0217;
        };
    L_0x0217:
        if (r13 == 0) goto L_0x022a;
    L_0x0219:
        r9 = 0;
        r10 = 0;
        r11 = r71.getWidth();
        r12 = r71.getHeight();
        r14 = 1;
        r8 = r71;
        r60 = android.graphics.Bitmap.createBitmap(r8, r9, r10, r11, r12, r13, r14);
    L_0x022a:
        r6 = r60.getWidth();
        r8 = r60.getHeight();
        r9 = android.graphics.Bitmap.Config.ALPHA_8;
        r18 = android.graphics.Bitmap.createBitmap(r6, r8, r9);
        r0 = r60;
        r1 = r18;
        r30 = binarizeAndFindCharacters(r0, r1);
        if (r30 != 0) goto L_0x02be;
    L_0x0242:
        r52 = 0;
    L_0x0244:
        return r52;
    L_0x0245:
        r56 = r71;
        goto L_0x0040;
    L_0x0249:
        r6 = r71.getWidth();
        r8 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        if (r6 > r8) goto L_0x0259;
    L_0x0251:
        r6 = r71.getHeight();
        r8 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        if (r6 <= r8) goto L_0x0200;
    L_0x0259:
        r6 = NUM; // 0x44bb8000 float:1500.0 double:5.697262106E-315;
        r8 = r71.getWidth();
        r9 = r71.getHeight();
        r8 = java.lang.Math.max(r8, r9);
        r8 = (float) r8;
        r53 = r6 / r8;
        r6 = r71.getWidth();
        r6 = (float) r6;
        r6 = r6 * r53;
        r6 = java.lang.Math.round(r6);
        r8 = r71.getHeight();
        r8 = (float) r8;
        r8 = r8 * r53;
        r8 = java.lang.Math.round(r8);
        r9 = 1;
        r0 = r71;
        r71 = android.graphics.Bitmap.createScaledBitmap(r0, r6, r8, r9);
        goto L_0x0200;
    L_0x028a:
        r13 = new android.graphics.Matrix;
        r13.<init>();
        r6 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r8 = r71.getWidth();
        r8 = r8 / 2;
        r8 = (float) r8;
        r9 = r71.getHeight();
        r9 = r9 / 2;
        r9 = (float) r9;
        r13.setRotate(r6, r8, r9);
        goto L_0x0217;
    L_0x02a4:
        r13 = new android.graphics.Matrix;
        r13.<init>();
        r6 = -NUM; // 0xffffffffbf800000 float:-1.0 double:NaN;
        r8 = r71.getWidth();
        r8 = r8 / 2;
        r8 = (float) r8;
        r9 = r71.getHeight();
        r9 = r9 / 2;
        r9 = (float) r9;
        r13.setRotate(r6, r8, r9);
        goto L_0x0217;
    L_0x02be:
        r0 = r30;
        r8 = r0.length;
        r6 = 0;
    L_0x02c2:
        if (r6 >= r8) goto L_0x02d9;
    L_0x02c4:
        r51 = r30[r6];
        r0 = r51;
        r9 = r0.length;
        r0 = r42;
        r42 = java.lang.Math.max(r9, r0);
        r0 = r51;
        r9 = r0.length;
        if (r9 <= 0) goto L_0x02d6;
    L_0x02d4:
        r41 = r41 + 1;
    L_0x02d6:
        r6 = r6 + 1;
        goto L_0x02c2;
    L_0x02d9:
        r6 = 2;
        r0 = r41;
        if (r0 < r6) goto L_0x02f3;
    L_0x02de:
        r6 = 30;
        r0 = r42;
        if (r0 < r6) goto L_0x02f3;
    L_0x02e4:
        r6 = 30;
        r0 = r42;
        if (r0 < r6) goto L_0x02ef;
    L_0x02ea:
        r6 = 2;
        r0 = r41;
        if (r0 >= r6) goto L_0x02f7;
    L_0x02ef:
        r52 = 0;
        goto L_0x0244;
    L_0x02f3:
        r36 = r36 + 1;
        goto L_0x020c;
    L_0x02f7:
        r6 = 0;
        r6 = r30[r6];
        r6 = r6.length;
        r6 = r6 * 10;
        r0 = r30;
        r8 = r0.length;
        r8 = r8 * 15;
        r9 = android.graphics.Bitmap.Config.ALPHA_8;
        r31 = android.graphics.Bitmap.createBitmap(r6, r8, r9);
        r32 = new android.graphics.Canvas;
        r0 = r32;
        r1 = r31;
        r0.<init>(r1);
        r15 = new android.graphics.Paint;
        r6 = 2;
        r15.<init>(r6);
        r7 = new android.graphics.Rect;
        r6 = 0;
        r8 = 0;
        r9 = 10;
        r10 = 15;
        r7.<init>(r6, r8, r9, r10);
        r69 = 0;
        r0 = r30;
        r9 = r0.length;
        r6 = 0;
        r8 = r6;
    L_0x0329:
        if (r8 >= r9) goto L_0x035c;
    L_0x032b:
        r40 = r30[r8];
        r68 = 0;
        r0 = r40;
        r10 = r0.length;
        r6 = 0;
    L_0x0333:
        if (r6 >= r10) goto L_0x0356;
    L_0x0335:
        r50 = r40[r6];
        r11 = r68 * 10;
        r12 = r69 * 15;
        r14 = r68 * 10;
        r14 = r14 + 10;
        r70 = r69 * 15;
        r70 = r70 + 15;
        r0 = r70;
        r7.set(r11, r12, r14, r0);
        r0 = r32;
        r1 = r18;
        r2 = r50;
        r0.drawBitmap(r1, r2, r7, r15);
        r68 = r68 + 1;
        r6 = r6 + 1;
        goto L_0x0333;
    L_0x0356:
        r69 = r69 + 1;
        r6 = r8 + 1;
        r8 = r6;
        goto L_0x0329;
    L_0x035c:
        r0 = r30;
        r6 = r0.length;
        r8 = 0;
        r8 = r30[r8];
        r8 = r8.length;
        r9 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r9 = r9.getAssets();
        r0 = r31;
        r43 = performRecognition(r0, r6, r8, r9);
        if (r43 != 0) goto L_0x0375;
    L_0x0371:
        r52 = 0;
        goto L_0x0244;
    L_0x0375:
        r6 = "\n";
        r0 = r43;
        r44 = android.text.TextUtils.split(r0, r6);
        r52 = new org.telegram.messenger.MrzRecognizer$Result;
        r52.<init>();
        r0 = r44;
        r6 = r0.length;
        r8 = 2;
        if (r6 < r8) goto L_0x09e0;
    L_0x0389:
        r6 = 0;
        r6 = r44[r6];
        r6 = r6.length();
        r8 = 30;
        if (r6 < r8) goto L_0x09e0;
    L_0x0394:
        r6 = 1;
        r6 = r44[r6];
        r6 = r6.length();
        r8 = 0;
        r8 = r44[r8];
        r8 = r8.length();
        if (r6 != r8) goto L_0x09e0;
    L_0x03a4:
        r6 = "\n";
        r0 = r44;
        r6 = android.text.TextUtils.join(r6, r0);
        r0 = r52;
        r0.rawMRZ = r6;
        r33 = getCountriesMap();
        r6 = 0;
        r6 = r44[r6];
        r8 = 0;
        r65 = r6.charAt(r8);
        r6 = 80;
        r0 = r65;
        if (r0 != r6) goto L_0x0621;
    L_0x03c3:
        r6 = 1;
        r0 = r52;
        r0.type = r6;
        r6 = 0;
        r6 = r44[r6];
        r6 = r6.length();
        r8 = 44;
        if (r6 != r8) goto L_0x05e8;
    L_0x03d3:
        r6 = 0;
        r6 = r44[r6];
        r8 = 2;
        r9 = 5;
        r6 = r6.substring(r8, r9);
        r0 = r52;
        r0.issuingCountry = r6;
        r6 = 0;
        r6 = r44[r6];
        r8 = "<<";
        r9 = 6;
        r37 = r6.indexOf(r8, r9);
        r6 = -1;
        r0 = r37;
        if (r0 == r6) goto L_0x0458;
    L_0x03f0:
        r6 = 0;
        r6 = r44[r6];
        r8 = 5;
        r0 = r37;
        r6 = r6.substring(r8, r0);
        r8 = 60;
        r9 = 32;
        r6 = r6.replace(r8, r9);
        r8 = 48;
        r9 = 79;
        r6 = r6.replace(r8, r9);
        r6 = r6.trim();
        r0 = r52;
        r0.lastName = r6;
        r6 = 0;
        r6 = r44[r6];
        r8 = r37 + 2;
        r6 = r6.substring(r8);
        r8 = 60;
        r9 = 32;
        r6 = r6.replace(r8, r9);
        r8 = 48;
        r9 = 79;
        r6 = r6.replace(r8, r9);
        r6 = r6.trim();
        r0 = r52;
        r0.firstName = r6;
        r0 = r52;
        r6 = r0.firstName;
        r8 = "   ";
        r6 = r6.contains(r8);
        if (r6 == 0) goto L_0x0458;
    L_0x0440:
        r0 = r52;
        r6 = r0.firstName;
        r8 = 0;
        r0 = r52;
        r9 = r0.firstName;
        r10 = "   ";
        r9 = r9.indexOf(r10);
        r6 = r6.substring(r8, r9);
        r0 = r52;
        r0.firstName = r6;
    L_0x0458:
        r6 = 1;
        r6 = r44[r6];
        r8 = 0;
        r9 = 9;
        r6 = r6.substring(r8, r9);
        r8 = 60;
        r9 = 32;
        r6 = r6.replace(r8, r9);
        r8 = 79;
        r9 = 48;
        r6 = r6.replace(r8, r9);
        r46 = r6.trim();
        r47 = checksum(r46);
        r6 = 1;
        r6 = r44[r6];
        r8 = 9;
        r6 = r6.charAt(r8);
        r6 = getNumber(r6);
        r0 = r47;
        if (r0 != r6) goto L_0x0491;
    L_0x048b:
        r0 = r46;
        r1 = r52;
        r1.number = r0;
    L_0x0491:
        r6 = 1;
        r6 = r44[r6];
        r8 = 10;
        r9 = 13;
        r6 = r6.substring(r8, r9);
        r0 = r52;
        r0.nationality = r6;
        r6 = 1;
        r6 = r44[r6];
        r8 = 13;
        r9 = 19;
        r6 = r6.substring(r8, r9);
        r8 = 79;
        r9 = 48;
        r6 = r6.replace(r8, r9);
        r8 = 73;
        r9 = 49;
        r19 = r6.replace(r8, r9);
        r20 = checksum(r19);
        r6 = 1;
        r6 = r44[r6];
        r8 = 19;
        r6 = r6.charAt(r8);
        r6 = getNumber(r6);
        r0 = r20;
        if (r0 != r6) goto L_0x04d7;
    L_0x04d0:
        r0 = r19;
        r1 = r52;
        parseBirthDate(r0, r1);
    L_0x04d7:
        r6 = 1;
        r6 = r44[r6];
        r8 = 20;
        r6 = r6.charAt(r8);
        r6 = parseGender(r6);
        r0 = r52;
        r0.gender = r6;
        r6 = 1;
        r6 = r44[r6];
        r8 = 21;
        r9 = 27;
        r6 = r6.substring(r8, r9);
        r8 = 79;
        r9 = 48;
        r6 = r6.replace(r8, r9);
        r8 = 73;
        r9 = 49;
        r34 = r6.replace(r8, r9);
        r35 = checksum(r34);
        r6 = 1;
        r6 = r44[r6];
        r8 = 27;
        r6 = r6.charAt(r8);
        r6 = getNumber(r6);
        r0 = r35;
        if (r0 == r6) goto L_0x0525;
    L_0x0518:
        r6 = 1;
        r6 = r44[r6];
        r8 = 27;
        r6 = r6.charAt(r8);
        r8 = 60;
        if (r6 != r8) goto L_0x052c;
    L_0x0525:
        r0 = r34;
        r1 = r52;
        parseExpiryDate(r0, r1);
    L_0x052c:
        r6 = "RUS";
        r0 = r52;
        r8 = r0.issuingCountry;
        r6 = r6.equals(r8);
        if (r6 == 0) goto L_0x0600;
    L_0x0539:
        r6 = 0;
        r6 = r44[r6];
        r8 = 1;
        r6 = r6.charAt(r8);
        r8 = 78;
        if (r6 != r8) goto L_0x0600;
    L_0x0545:
        r6 = 3;
        r0 = r52;
        r0.type = r6;
        r0 = r52;
        r6 = r0.firstName;
        r8 = " ";
        r45 = r6.split(r8);
        r6 = 0;
        r6 = r45[r6];
        r6 = russianPassportTranslit(r6);
        r6 = cyrillicToLatin(r6);
        r0 = r52;
        r0.firstName = r6;
        r0 = r45;
        r6 = r0.length;
        r8 = 1;
        if (r6 <= r8) goto L_0x0579;
    L_0x056a:
        r6 = 1;
        r6 = r45[r6];
        r6 = russianPassportTranslit(r6);
        r6 = cyrillicToLatin(r6);
        r0 = r52;
        r0.middleName = r6;
    L_0x0579:
        r0 = r52;
        r6 = r0.lastName;
        r6 = russianPassportTranslit(r6);
        r6 = cyrillicToLatin(r6);
        r0 = r52;
        r0.lastName = r6;
        r0 = r52;
        r6 = r0.number;
        if (r6 == 0) goto L_0x05c4;
    L_0x058f:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r0 = r52;
        r8 = r0.number;
        r9 = 0;
        r10 = 3;
        r8 = r8.substring(r9, r10);
        r6 = r6.append(r8);
        r8 = 1;
        r8 = r44[r8];
        r9 = 28;
        r8 = r8.charAt(r9);
        r6 = r6.append(r8);
        r0 = r52;
        r8 = r0.number;
        r9 = 3;
        r8 = r8.substring(r9);
        r6 = r6.append(r8);
        r6 = r6.toString();
        r0 = r52;
        r0.number = r6;
    L_0x05c4:
        r0 = r52;
        r6 = r0.lastName;
        r6 = capitalize(r6);
        r0 = r52;
        r0.lastName = r6;
        r0 = r52;
        r6 = r0.firstName;
        r6 = capitalize(r6);
        r0 = r52;
        r0.firstName = r6;
        r0 = r52;
        r6 = r0.middleName;
        r6 = capitalize(r6);
        r0 = r52;
        r0.middleName = r6;
    L_0x05e8:
        r0 = r52;
        r6 = r0.firstName;
        r6 = android.text.TextUtils.isEmpty(r6);
        if (r6 == 0) goto L_0x09be;
    L_0x05f2:
        r0 = r52;
        r6 = r0.lastName;
        r6 = android.text.TextUtils.isEmpty(r6);
        if (r6 == 0) goto L_0x09be;
    L_0x05fc:
        r52 = 0;
        goto L_0x0244;
    L_0x0600:
        r0 = r52;
        r6 = r0.firstName;
        r8 = 56;
        r9 = 66;
        r6 = r6.replace(r8, r9);
        r0 = r52;
        r0.firstName = r6;
        r0 = r52;
        r6 = r0.lastName;
        r8 = 56;
        r9 = 66;
        r6 = r6.replace(r8, r9);
        r0 = r52;
        r0.lastName = r6;
        goto L_0x05c4;
    L_0x0621:
        r6 = 73;
        r0 = r65;
        if (r0 == r6) goto L_0x0633;
    L_0x0627:
        r6 = 65;
        r0 = r65;
        if (r0 == r6) goto L_0x0633;
    L_0x062d:
        r6 = 67;
        r0 = r65;
        if (r0 != r6) goto L_0x09ba;
    L_0x0633:
        r6 = 2;
        r0 = r52;
        r0.type = r6;
        r0 = r44;
        r6 = r0.length;
        r8 = 3;
        if (r6 != r8) goto L_0x07ab;
    L_0x063e:
        r6 = 0;
        r6 = r44[r6];
        r6 = r6.length();
        r8 = 30;
        if (r6 != r8) goto L_0x07ab;
    L_0x0649:
        r6 = 2;
        r6 = r44[r6];
        r6 = r6.length();
        r8 = 30;
        if (r6 != r8) goto L_0x07ab;
    L_0x0654:
        r6 = 0;
        r6 = r44[r6];
        r8 = 2;
        r9 = 5;
        r6 = r6.substring(r8, r9);
        r0 = r52;
        r0.issuingCountry = r6;
        r6 = 0;
        r6 = r44[r6];
        r8 = 5;
        r9 = 14;
        r6 = r6.substring(r8, r9);
        r8 = 60;
        r9 = 32;
        r6 = r6.replace(r8, r9);
        r8 = 79;
        r9 = 48;
        r6 = r6.replace(r8, r9);
        r46 = r6.trim();
        r47 = checksum(r46);
        r6 = 0;
        r6 = r44[r6];
        r8 = 14;
        r6 = r6.charAt(r8);
        r6 = r6 + -48;
        r0 = r47;
        if (r0 != r6) goto L_0x0698;
    L_0x0692:
        r0 = r46;
        r1 = r52;
        r1.number = r0;
    L_0x0698:
        r6 = 1;
        r6 = r44[r6];
        r8 = 0;
        r9 = 6;
        r6 = r6.substring(r8, r9);
        r8 = 79;
        r9 = 48;
        r6 = r6.replace(r8, r9);
        r8 = 73;
        r9 = 49;
        r19 = r6.replace(r8, r9);
        r20 = checksum(r19);
        r6 = 1;
        r6 = r44[r6];
        r8 = 6;
        r6 = r6.charAt(r8);
        r6 = getNumber(r6);
        r0 = r20;
        if (r0 != r6) goto L_0x06cc;
    L_0x06c5:
        r0 = r19;
        r1 = r52;
        parseBirthDate(r0, r1);
    L_0x06cc:
        r6 = 1;
        r6 = r44[r6];
        r8 = 7;
        r6 = r6.charAt(r8);
        r6 = parseGender(r6);
        r0 = r52;
        r0.gender = r6;
        r6 = 1;
        r6 = r44[r6];
        r8 = 8;
        r9 = 14;
        r6 = r6.substring(r8, r9);
        r8 = 79;
        r9 = 48;
        r6 = r6.replace(r8, r9);
        r8 = 73;
        r9 = 49;
        r34 = r6.replace(r8, r9);
        r35 = checksum(r34);
        r6 = 1;
        r6 = r44[r6];
        r8 = 14;
        r6 = r6.charAt(r8);
        r6 = getNumber(r6);
        r0 = r35;
        if (r0 == r6) goto L_0x0719;
    L_0x070c:
        r6 = 1;
        r6 = r44[r6];
        r8 = 14;
        r6 = r6.charAt(r8);
        r8 = 60;
        if (r6 != r8) goto L_0x0720;
    L_0x0719:
        r0 = r34;
        r1 = r52;
        parseExpiryDate(r0, r1);
    L_0x0720:
        r6 = 1;
        r6 = r44[r6];
        r8 = 15;
        r9 = 18;
        r6 = r6.substring(r8, r9);
        r0 = r52;
        r0.nationality = r6;
        r6 = 2;
        r6 = r44[r6];
        r8 = "<<";
        r37 = r6.indexOf(r8);
        r6 = -1;
        r0 = r37;
        if (r0 == r6) goto L_0x0771;
    L_0x073e:
        r6 = 2;
        r6 = r44[r6];
        r8 = 0;
        r0 = r37;
        r6 = r6.substring(r8, r0);
        r8 = 60;
        r9 = 32;
        r6 = r6.replace(r8, r9);
        r6 = r6.trim();
        r0 = r52;
        r0.lastName = r6;
        r6 = 2;
        r6 = r44[r6];
        r8 = r37 + 2;
        r6 = r6.substring(r8);
        r8 = 60;
        r9 = 32;
        r6 = r6.replace(r8, r9);
        r6 = r6.trim();
        r0 = r52;
        r0.firstName = r6;
    L_0x0771:
        r0 = r52;
        r6 = r0.firstName;
        r8 = 48;
        r9 = 79;
        r6 = r6.replace(r8, r9);
        r8 = 56;
        r9 = 66;
        r6 = r6.replace(r8, r9);
        r6 = capitalize(r6);
        r0 = r52;
        r0.firstName = r6;
        r0 = r52;
        r6 = r0.lastName;
        r8 = 48;
        r9 = 79;
        r6 = r6.replace(r8, r9);
        r8 = 56;
        r9 = 66;
        r6 = r6.replace(r8, r9);
        r6 = capitalize(r6);
        r0 = r52;
        r0.lastName = r6;
        goto L_0x05e8;
    L_0x07ab:
        r0 = r44;
        r6 = r0.length;
        r8 = 2;
        if (r6 != r8) goto L_0x0771;
    L_0x07b1:
        r6 = 0;
        r6 = r44[r6];
        r6 = r6.length();
        r8 = 36;
        if (r6 != r8) goto L_0x0771;
    L_0x07bc:
        r6 = 0;
        r6 = r44[r6];
        r8 = 2;
        r9 = 5;
        r6 = r6.substring(r8, r9);
        r0 = r52;
        r0.issuingCountry = r6;
        r6 = "FRA";
        r0 = r52;
        r8 = r0.issuingCountry;
        r6 = r6.equals(r8);
        if (r6 == 0) goto L_0x08a6;
    L_0x07d6:
        r6 = 73;
        r0 = r65;
        if (r0 != r6) goto L_0x08a6;
    L_0x07dc:
        r6 = 0;
        r6 = r44[r6];
        r8 = 1;
        r6 = r6.charAt(r8);
        r8 = 68;
        if (r6 != r8) goto L_0x08a6;
    L_0x07e8:
        r6 = "FRA";
        r0 = r52;
        r0.nationality = r6;
        r6 = 0;
        r6 = r44[r6];
        r8 = 5;
        r9 = 30;
        r6 = r6.substring(r8, r9);
        r8 = 60;
        r9 = 32;
        r6 = r6.replace(r8, r9);
        r6 = r6.trim();
        r0 = r52;
        r0.lastName = r6;
        r6 = 1;
        r6 = r44[r6];
        r8 = 13;
        r9 = 27;
        r6 = r6.substring(r8, r9);
        r8 = "<<";
        r9 = ", ";
        r6 = r6.replace(r8, r9);
        r8 = 60;
        r9 = 32;
        r6 = r6.replace(r8, r9);
        r6 = r6.trim();
        r0 = r52;
        r0.firstName = r6;
        r6 = 1;
        r6 = r44[r6];
        r8 = 0;
        r9 = 12;
        r6 = r6.substring(r8, r9);
        r8 = 79;
        r9 = 48;
        r46 = r6.replace(r8, r9);
        r6 = checksum(r46);
        r8 = 1;
        r8 = r44[r8];
        r9 = 12;
        r8 = r8.charAt(r9);
        r8 = getNumber(r8);
        if (r6 != r8) goto L_0x0859;
    L_0x0853:
        r0 = r46;
        r1 = r52;
        r1.number = r0;
    L_0x0859:
        r6 = 1;
        r6 = r44[r6];
        r8 = 27;
        r9 = 33;
        r6 = r6.substring(r8, r9);
        r8 = 79;
        r9 = 48;
        r6 = r6.replace(r8, r9);
        r8 = 73;
        r9 = 49;
        r19 = r6.replace(r8, r9);
        r6 = checksum(r19);
        r8 = 1;
        r8 = r44[r8];
        r9 = 33;
        r8 = r8.charAt(r9);
        r8 = getNumber(r8);
        if (r6 != r8) goto L_0x088e;
    L_0x0887:
        r0 = r19;
        r1 = r52;
        parseBirthDate(r0, r1);
    L_0x088e:
        r6 = 1;
        r6 = r44[r6];
        r8 = 34;
        r6 = r6.charAt(r8);
        r6 = parseGender(r6);
        r0 = r52;
        r0.gender = r6;
        r6 = 1;
        r0 = r52;
        r0.doesNotExpire = r6;
        goto L_0x0771;
    L_0x08a6:
        r6 = 0;
        r6 = r44[r6];
        r8 = "<<";
        r37 = r6.indexOf(r8);
        r6 = -1;
        r0 = r37;
        if (r0 == r6) goto L_0x08e8;
    L_0x08b5:
        r6 = 0;
        r6 = r44[r6];
        r8 = 5;
        r0 = r37;
        r6 = r6.substring(r8, r0);
        r8 = 60;
        r9 = 32;
        r6 = r6.replace(r8, r9);
        r6 = r6.trim();
        r0 = r52;
        r0.lastName = r6;
        r6 = 0;
        r6 = r44[r6];
        r8 = r37 + 2;
        r6 = r6.substring(r8);
        r8 = 60;
        r9 = 32;
        r6 = r6.replace(r8, r9);
        r6 = r6.trim();
        r0 = r52;
        r0.firstName = r6;
    L_0x08e8:
        r6 = 1;
        r6 = r44[r6];
        r8 = 0;
        r9 = 9;
        r6 = r6.substring(r8, r9);
        r8 = 60;
        r9 = 32;
        r6 = r6.replace(r8, r9);
        r8 = 79;
        r9 = 48;
        r6 = r6.replace(r8, r9);
        r46 = r6.trim();
        r47 = checksum(r46);
        r6 = 1;
        r6 = r44[r6];
        r8 = 9;
        r6 = r6.charAt(r8);
        r6 = getNumber(r6);
        r0 = r47;
        if (r0 != r6) goto L_0x0921;
    L_0x091b:
        r0 = r46;
        r1 = r52;
        r1.number = r0;
    L_0x0921:
        r6 = 1;
        r6 = r44[r6];
        r8 = 10;
        r9 = 13;
        r6 = r6.substring(r8, r9);
        r0 = r52;
        r0.nationality = r6;
        r6 = 1;
        r6 = r44[r6];
        r8 = 13;
        r9 = 19;
        r6 = r6.substring(r8, r9);
        r8 = 79;
        r9 = 48;
        r6 = r6.replace(r8, r9);
        r8 = 73;
        r9 = 49;
        r19 = r6.replace(r8, r9);
        r6 = checksum(r19);
        r8 = 1;
        r8 = r44[r8];
        r9 = 19;
        r8 = r8.charAt(r9);
        r8 = getNumber(r8);
        if (r6 != r8) goto L_0x0965;
    L_0x095e:
        r0 = r19;
        r1 = r52;
        parseBirthDate(r0, r1);
    L_0x0965:
        r6 = 1;
        r6 = r44[r6];
        r8 = 20;
        r6 = r6.charAt(r8);
        r6 = parseGender(r6);
        r0 = r52;
        r0.gender = r6;
        r6 = 1;
        r6 = r44[r6];
        r8 = 21;
        r9 = 27;
        r6 = r6.substring(r8, r9);
        r8 = 79;
        r9 = 48;
        r6 = r6.replace(r8, r9);
        r8 = 73;
        r9 = 49;
        r34 = r6.replace(r8, r9);
        r6 = checksum(r34);
        r8 = 1;
        r8 = r44[r8];
        r9 = 27;
        r8 = r8.charAt(r9);
        r8 = getNumber(r8);
        if (r6 == r8) goto L_0x09b1;
    L_0x09a4:
        r6 = 1;
        r6 = r44[r6];
        r8 = 27;
        r6 = r6.charAt(r8);
        r8 = 60;
        if (r6 != r8) goto L_0x0771;
    L_0x09b1:
        r0 = r34;
        r1 = r52;
        parseExpiryDate(r0, r1);
        goto L_0x0771;
    L_0x09ba:
        r52 = 0;
        goto L_0x0244;
    L_0x09be:
        r0 = r52;
        r6 = r0.issuingCountry;
        r0 = r33;
        r6 = r0.get(r6);
        r6 = (java.lang.String) r6;
        r0 = r52;
        r0.issuingCountry = r6;
        r0 = r52;
        r6 = r0.nationality;
        r0 = r33;
        r6 = r0.get(r6);
        r6 = (java.lang.String) r6;
        r0 = r52;
        r0.nationality = r6;
        goto L_0x0244;
    L_0x09e0:
        r52 = 0;
        goto L_0x0244;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MrzRecognizer.recognizeMRZ(android.graphics.Bitmap):org.telegram.messenger.MrzRecognizer$Result");
    }

    public static Result recognize(byte[] yuvData, int width, int height, int rotation) {
        boolean swap;
        int i;
        Bitmap bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        setYuvBitmapPixels(bmp, yuvData);
        Matrix m = new Matrix();
        m.setRotate((float) rotation);
        int minSize = Math.min(width, height);
        int dw = minSize;
        int dh = Math.round(((float) minSize) * 0.704f);
        if (rotation == 90 || rotation == 270) {
            swap = true;
        } else {
            swap = false;
        }
        if (swap) {
            i = (width / 2) - (dh / 2);
        } else {
            i = 0;
        }
        return recognize(Bitmap.createBitmap(bmp, i, swap ? 0 : (height / 2) - (dh / 2), swap ? dh : dw, swap ? dw : dh, m, false), false);
    }

    private static String capitalize(String s) {
        if (s == null) {
            return null;
        }
        char[] chars = s.toCharArray();
        boolean prevIsSpace = true;
        int i = 0;
        while (i < chars.length) {
            if (prevIsSpace || !Character.isLetter(chars[i])) {
                prevIsSpace = chars[i] == ' ';
            } else {
                chars[i] = Character.toLowerCase(chars[i]);
            }
            i++;
        }
        return new String(chars);
    }

    private static int checksum(String s) {
        int val = 0;
        char[] chars = s.toCharArray();
        int[] weights = new int[]{7, 3, 1};
        int i = 0;
        while (i < chars.length) {
            int charVal = 0;
            if (chars[i] >= '0' && chars[i] <= '9') {
                charVal = chars[i] - 48;
            } else if (chars[i] >= 'A' && chars[i] <= 'Z') {
                charVal = (chars[i] - 65) + 10;
            }
            val += weights[i % weights.length] * charVal;
            i++;
        }
        return val % 10;
    }

    private static void parseBirthDate(String birthDate, Result result) {
        try {
            result.birthYear = Integer.parseInt(birthDate.substring(0, 2));
            result.birthYear = result.birthYear < (Calendar.getInstance().get(1) % 100) + -5 ? result.birthYear + 2000 : result.birthYear + 1900;
            result.birthMonth = Integer.parseInt(birthDate.substring(2, 4));
            result.birthDay = Integer.parseInt(birthDate.substring(4));
        } catch (NumberFormatException e) {
        }
    }

    private static void parseExpiryDate(String expiryDate, Result result) {
        try {
            if ("<<<<<<".equals(expiryDate)) {
                result.doesNotExpire = true;
                return;
            }
            result.expiryYear = Integer.parseInt(expiryDate.substring(0, 2)) + 2000;
            result.expiryMonth = Integer.parseInt(expiryDate.substring(2, 4));
            result.expiryDay = Integer.parseInt(expiryDate.substring(4));
        } catch (NumberFormatException e) {
        }
    }

    private static int parseGender(char gender) {
        switch (gender) {
            case 'F':
                return 2;
            case 'M':
                return 1;
            default:
                return 0;
        }
    }

    private static String russianPassportTranslit(String s) {
        String cyrillic = "\u0410\u0411\u0412\u0413\u0414\u0415\u0401\u0416\u0417\u0418\u0419\u041a\u041b\u041c\u041d\u041e\u041f\u0420\u0421\u0422\u0423\u0424\u0425\u0426\u0427\u0428\u0429\u042a\u042b\u042c\u042d\u042e\u042f";
        String latin = "ABVGDE2JZIQKLMNOPRSTUFHC34WXY9678";
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int idx = "ABVGDE2JZIQKLMNOPRSTUFHC34WXY9678".indexOf(chars[i]);
            if (idx != -1) {
                chars[i] = "\u0410\u0411\u0412\u0413\u0414\u0415\u0401\u0416\u0417\u0418\u0419\u041a\u041b\u041c\u041d\u041e\u041f\u0420\u0421\u0422\u0423\u0424\u0425\u0426\u0427\u0428\u0429\u042a\u042b\u042c\u042d\u042e\u042f".charAt(idx);
            }
        }
        return new String(chars);
    }

    private static String cyrillicToLatin(String s) {
        String alphabet = "\u0410\u0411\u0412\u0413\u0414\u0415\u0401\u0416\u0417\u0418\u0419\u041a\u041b\u041c\u041d\u041e\u041f\u0420\u0421\u0422\u0423\u0424\u0425\u0426\u0427\u0428\u0429\u042a\u042b\u042c\u042d\u042e\u042f";
        String[] replacements = new String[]{"A", "B", "V", "G", "D", "E", "E", "ZH", "Z", "I", "I", "K", "L", "M", "N", "O", "P", "R", "S", "T", "U", "F", "KH", "TS", "CH", "SH", "SHCH", "IE", "Y", TtmlNode.ANONYMOUS_REGION_ID, "E", "IU", "IA"};
        for (int i = 0; i < replacements.length; i++) {
            s = s.replace("\u0410\u0411\u0412\u0413\u0414\u0415\u0401\u0416\u0417\u0418\u0419\u041a\u041b\u041c\u041d\u041e\u041f\u0420\u0421\u0422\u0423\u0424\u0425\u0426\u0427\u0428\u0429\u042a\u042b\u042c\u042d\u042e\u042f".substring(i, i + 1), replacements[i]);
        }
        return s;
    }

    private static int getNumber(char c) {
        if (c == 'O') {
            return 0;
        }
        if (c == 'I') {
            return 1;
        }
        if (c == 'B') {
            return 8;
        }
        return c - 48;
    }

    private static HashMap<String, String> getCountriesMap() {
        HashMap<String, String> countries = new HashMap();
        countries.put("AFG", "AF");
        countries.put("ALA", "AX");
        countries.put("ALB", "AL");
        countries.put("DZA", "DZ");
        countries.put("ASM", "AS");
        countries.put("AND", "AD");
        countries.put("AGO", "AO");
        countries.put("AIA", "AI");
        countries.put("ATA", "AQ");
        countries.put("ATG", "AG");
        countries.put("ARG", "AR");
        countries.put("ARM", "AM");
        countries.put("ABW", "AW");
        countries.put("AUS", "AU");
        countries.put("AUT", "AT");
        countries.put("AZE", "AZ");
        countries.put("BHS", "BS");
        countries.put("BHR", "BH");
        countries.put("BGD", "BD");
        countries.put("BRB", "BB");
        countries.put("BLR", "BY");
        countries.put("BEL", "BE");
        countries.put("BLZ", "BZ");
        countries.put("BEN", "BJ");
        countries.put("BMU", "BM");
        countries.put("BTN", "BT");
        countries.put("BOL", "BO");
        countries.put("BES", "BQ");
        countries.put("BIH", "BA");
        countries.put("BWA", "BW");
        countries.put("BVT", "BV");
        countries.put("BRA", "BR");
        countries.put("IOT", "IO");
        countries.put("BRN", "BN");
        countries.put("BGR", "BG");
        countries.put("BFA", "BF");
        countries.put("BDI", "BI");
        countries.put("CPV", "CV");
        countries.put("KHM", "KH");
        countries.put("CMR", "CM");
        countries.put("CAN", "CA");
        countries.put("CYM", "KY");
        countries.put("CAF", "CF");
        countries.put("TCD", "TD");
        countries.put("CHL", "CL");
        countries.put("CHN", "CN");
        countries.put("CXR", "CX");
        countries.put("CCK", "CC");
        countries.put("COL", "CO");
        countries.put("COM", "KM");
        countries.put("COG", "CG");
        countries.put("COD", "CD");
        countries.put("COK", "CK");
        countries.put("CRI", "CR");
        countries.put("CIV", "CI");
        countries.put("HRV", "HR");
        countries.put("CUB", "CU");
        countries.put("CUW", "CW");
        countries.put("CYP", "CY");
        countries.put("CZE", "CZ");
        countries.put("DNK", "DK");
        countries.put("DJI", "DJ");
        countries.put("DMA", "DM");
        countries.put("DOM", "DO");
        countries.put("ECU", "EC");
        countries.put("EGY", "EG");
        countries.put("SLV", "SV");
        countries.put("GNQ", "GQ");
        countries.put("ERI", "ER");
        countries.put("EST", "EE");
        countries.put("ETH", "ET");
        countries.put("FLK", "FK");
        countries.put("FRO", "FO");
        countries.put("FJI", "FJ");
        countries.put("FIN", "FI");
        countries.put("FRA", "FR");
        countries.put("GUF", "GF");
        countries.put("PYF", "PF");
        countries.put("ATF", "TF");
        countries.put("GAB", "GA");
        countries.put("GMB", "GM");
        countries.put("GEO", "GE");
        countries.put("D<<", "DE");
        countries.put("GHA", "GH");
        countries.put("GIB", "GI");
        countries.put("GRC", "GR");
        countries.put("GRL", "GL");
        countries.put("GRD", "GD");
        countries.put("GLP", "GP");
        countries.put("GUM", "GU");
        countries.put("GTM", "GT");
        countries.put("GGY", "GG");
        countries.put("GIN", "GN");
        countries.put("GNB", "GW");
        countries.put("GUY", "GY");
        countries.put("HTI", "HT");
        countries.put("HMD", "HM");
        countries.put("VAT", "VA");
        countries.put("HND", "HN");
        countries.put("HKG", "HK");
        countries.put("HUN", "HU");
        countries.put("ISL", "IS");
        countries.put("IND", "IN");
        countries.put("IDN", "ID");
        countries.put("IRN", "IR");
        countries.put("IRQ", "IQ");
        countries.put("IRL", "IE");
        countries.put("IMN", "IM");
        countries.put("ISR", "IL");
        countries.put("ITA", "IT");
        countries.put("JAM", "JM");
        countries.put("JPN", "JP");
        countries.put("JEY", "JE");
        countries.put("JOR", "JO");
        countries.put("KAZ", "KZ");
        countries.put("KEN", "KE");
        countries.put("KIR", "KI");
        countries.put("PRK", "KP");
        countries.put("KOR", "KR");
        countries.put("KWT", "KW");
        countries.put("KGZ", "KG");
        countries.put("LAO", "LA");
        countries.put("LVA", "LV");
        countries.put("LBN", "LB");
        countries.put("LSO", "LS");
        countries.put("LBR", "LR");
        countries.put("LBY", "LY");
        countries.put("LIE", "LI");
        countries.put("LTU", "LT");
        countries.put("LUX", "LU");
        countries.put("MAC", "MO");
        countries.put("MKD", "MK");
        countries.put("MDG", "MG");
        countries.put("MWI", "MW");
        countries.put("MYS", "MY");
        countries.put("MDV", "MV");
        countries.put("MLI", "ML");
        countries.put("MLT", "MT");
        countries.put("MHL", "MH");
        countries.put("MTQ", "MQ");
        countries.put("MRT", "MR");
        countries.put("MUS", "MU");
        countries.put("MYT", "YT");
        countries.put("MEX", "MX");
        countries.put("FSM", "FM");
        countries.put("MDA", "MD");
        countries.put("MCO", "MC");
        countries.put("MNG", "MN");
        countries.put("MNE", "ME");
        countries.put("MSR", "MS");
        countries.put("MAR", "MA");
        countries.put("MOZ", "MZ");
        countries.put("MMR", "MM");
        countries.put("NAM", "NA");
        countries.put("NRU", "NR");
        countries.put("NPL", "NP");
        countries.put("NLD", "NL");
        countries.put("NCL", "NC");
        countries.put("NZL", "NZ");
        countries.put("NIC", "NI");
        countries.put("NER", "NE");
        countries.put("NGA", "NG");
        countries.put("NIU", "NU");
        countries.put("NFK", "NF");
        countries.put("MNP", "MP");
        countries.put("NOR", "NO");
        countries.put("OMN", "OM");
        countries.put("PAK", "PK");
        countries.put("PLW", "PW");
        countries.put("PSE", "PS");
        countries.put("PAN", "PA");
        countries.put("PNG", "PG");
        countries.put("PRY", "PY");
        countries.put("PER", "PE");
        countries.put("PHL", "PH");
        countries.put("PCN", "PN");
        countries.put("POL", "PL");
        countries.put("PRT", "PT");
        countries.put("PRI", "PR");
        countries.put("QAT", "QA");
        countries.put("REU", "RE");
        countries.put("ROU", "RO");
        countries.put("RUS", "RU");
        countries.put("RWA", "RW");
        countries.put("BLM", "BL");
        countries.put("SHN", "SH");
        countries.put("KNA", "KN");
        countries.put("LCA", "LC");
        countries.put("MAF", "MF");
        countries.put("SPM", "PM");
        countries.put("VCT", "VC");
        countries.put("WSM", "WS");
        countries.put("SMR", "SM");
        countries.put("STP", "ST");
        countries.put("SAU", "SA");
        countries.put("SEN", "SN");
        countries.put("SRB", "RS");
        countries.put("SYC", "SC");
        countries.put("SLE", "SL");
        countries.put("SGP", "SG");
        countries.put("SXM", "SX");
        countries.put("SVK", "SK");
        countries.put("SVN", "SI");
        countries.put("SLB", "SB");
        countries.put("SOM", "SO");
        countries.put("ZAF", "ZA");
        countries.put("SGS", "GS");
        countries.put("SSD", "SS");
        countries.put("ESP", "ES");
        countries.put("LKA", "LK");
        countries.put("SDN", "SD");
        countries.put("SUR", "SR");
        countries.put("SJM", "SJ");
        countries.put("SWZ", "SZ");
        countries.put("SWE", "SE");
        countries.put("CHE", "CH");
        countries.put("SYR", "SY");
        countries.put("TWN", "TW");
        countries.put("TJK", "TJ");
        countries.put("TZA", "TZ");
        countries.put("THA", "TH");
        countries.put("TLS", "TL");
        countries.put("TGO", "TG");
        countries.put("TKL", "TK");
        countries.put("TON", "TO");
        countries.put("TTO", "TT");
        countries.put("TUN", "TN");
        countries.put("TUR", "TR");
        countries.put("TKM", "TM");
        countries.put("TCA", "TC");
        countries.put("TUV", "TV");
        countries.put("UGA", "UG");
        countries.put("UKR", "UA");
        countries.put("ARE", "AE");
        countries.put("GBR", "GB");
        countries.put("USA", "US");
        countries.put("UMI", "UM");
        countries.put("URY", "UY");
        countries.put("UZB", "UZ");
        countries.put("VUT", "VU");
        countries.put("VEN", "VE");
        countries.put("VNM", "VN");
        countries.put("VGB", "VG");
        countries.put("VIR", "VI");
        countries.put("WLF", "WF");
        countries.put("ESH", "EH");
        countries.put("YEM", "YE");
        countries.put("ZMB", "ZM");
        countries.put("ZWE", "ZW");
        return countries;
    }
}
