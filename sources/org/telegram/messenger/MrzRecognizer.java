package org.telegram.messenger;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
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
        return c - '0';
    }

    private static int parseGender(char c) {
        if (c != 'F') {
            return c != 'M' ? 0 : 1;
        }
        return 2;
    }

    private static native String performRecognition(Bitmap bitmap, int i, int i2, AssetManager assetManager);

    private static native void setYuvBitmapPixels(Bitmap bitmap, byte[] bArr);

    public static Result recognize(Bitmap bitmap, boolean z) {
        Result recognizeBarcode;
        Result recognizeBarcode2;
        if (z && (recognizeBarcode2 = recognizeBarcode(bitmap)) != null) {
            return recognizeBarcode2;
        }
        try {
            Result recognizeMRZ = recognizeMRZ(bitmap);
            if (recognizeMRZ != null) {
                return recognizeMRZ;
            }
        } catch (Exception unused) {
        }
        if (z || (recognizeBarcode = recognizeBarcode(bitmap)) == null) {
            return null;
        }
        return recognizeBarcode;
    }

    /* JADX WARNING: Removed duplicated region for block: B:28:0x00b1  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00bb  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00e9  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x011b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static org.telegram.messenger.MrzRecognizer.Result recognizeBarcode(android.graphics.Bitmap r11) {
        /*
            com.google.android.gms.vision.barcode.BarcodeDetector$Builder r0 = new com.google.android.gms.vision.barcode.BarcodeDetector$Builder
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            r0.<init>(r1)
            com.google.android.gms.vision.barcode.BarcodeDetector r0 = r0.build()
            int r1 = r11.getWidth()
            r2 = 1500(0x5dc, float:2.102E-42)
            r3 = 1
            if (r1 > r2) goto L_0x001a
            int r1 = r11.getHeight()
            if (r1 <= r2) goto L_0x0045
        L_0x001a:
            r1 = 1153138688(0x44bb8000, float:1500.0)
            int r2 = r11.getWidth()
            int r4 = r11.getHeight()
            int r2 = java.lang.Math.max(r2, r4)
            float r2 = (float) r2
            float r1 = r1 / r2
            int r2 = r11.getWidth()
            float r2 = (float) r2
            float r2 = r2 * r1
            int r2 = java.lang.Math.round(r2)
            int r4 = r11.getHeight()
            float r4 = (float) r4
            float r4 = r4 * r1
            int r1 = java.lang.Math.round(r4)
            android.graphics.Bitmap r11 = android.graphics.Bitmap.createScaledBitmap(r11, r2, r1, r3)
        L_0x0045:
            com.google.android.gms.vision.Frame$Builder r1 = new com.google.android.gms.vision.Frame$Builder
            r1.<init>()
            r1.setBitmap(r11)
            com.google.android.gms.vision.Frame r11 = r1.build()
            android.util.SparseArray r11 = r0.detect(r11)
            r0 = 0
            r1 = 0
        L_0x0057:
            int r2 = r11.size()
            if (r1 >= r2) goto L_0x0253
            java.lang.Object r2 = r11.valueAt(r1)
            com.google.android.gms.vision.barcode.Barcode r2 = (com.google.android.gms.vision.barcode.Barcode) r2
            int r4 = r2.valueFormat
            r5 = 12
            r6 = 6
            r7 = 2
            r8 = 4
            if (r4 != r5) goto L_0x01a1
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r4 = r2.driverLicense
            if (r4 == 0) goto L_0x01a1
            org.telegram.messenger.MrzRecognizer$Result r11 = new org.telegram.messenger.MrzRecognizer$Result
            r11.<init>()
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r1 = r2.driverLicense
            java.lang.String r1 = r1.documentType
            java.lang.String r4 = "ID"
            boolean r1 = r4.equals(r1)
            if (r1 == 0) goto L_0x0083
            r1 = 2
            goto L_0x0084
        L_0x0083:
            r1 = 4
        L_0x0084:
            r11.type = r1
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r1 = r2.driverLicense
            java.lang.String r1 = r1.issuingCountry
            int r4 = r1.hashCode()
            r5 = 66480(0x103b0, float:9.3158E-41)
            java.lang.String r9 = "USA"
            r10 = -1
            if (r4 == r5) goto L_0x00a4
            r5 = 84323(0x14963, float:1.18162E-40)
            if (r4 == r5) goto L_0x009c
            goto L_0x00ae
        L_0x009c:
            boolean r1 = r1.equals(r9)
            if (r1 == 0) goto L_0x00ae
            r1 = 0
            goto L_0x00af
        L_0x00a4:
            java.lang.String r4 = "CAN"
            boolean r1 = r1.equals(r4)
            if (r1 == 0) goto L_0x00ae
            r1 = 1
            goto L_0x00af
        L_0x00ae:
            r1 = -1
        L_0x00af:
            if (r1 == 0) goto L_0x00bb
            if (r1 == r3) goto L_0x00b4
            goto L_0x00c1
        L_0x00b4:
            java.lang.String r1 = "CA"
            r11.issuingCountry = r1
            r11.nationality = r1
            goto L_0x00c1
        L_0x00bb:
            java.lang.String r1 = "US"
            r11.issuingCountry = r1
            r11.nationality = r1
        L_0x00c1:
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r1 = r2.driverLicense
            java.lang.String r1 = r1.firstName
            java.lang.String r1 = capitalize(r1)
            r11.firstName = r1
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r1 = r2.driverLicense
            java.lang.String r1 = r1.lastName
            java.lang.String r1 = capitalize(r1)
            r11.lastName = r1
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r1 = r2.driverLicense
            java.lang.String r1 = r1.middleName
            java.lang.String r1 = capitalize(r1)
            r11.middleName = r1
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r1 = r2.driverLicense
            java.lang.String r4 = r1.licenseNumber
            r11.number = r4
            java.lang.String r1 = r1.gender
            if (r1 == 0) goto L_0x0113
            int r4 = r1.hashCode()
            r5 = 49
            if (r4 == r5) goto L_0x0100
            r5 = 50
            if (r4 == r5) goto L_0x00f6
            goto L_0x0109
        L_0x00f6:
            java.lang.String r4 = "2"
            boolean r1 = r1.equals(r4)
            if (r1 == 0) goto L_0x0109
            r10 = 1
            goto L_0x0109
        L_0x0100:
            java.lang.String r4 = "1"
            boolean r1 = r1.equals(r4)
            if (r1 == 0) goto L_0x0109
            r10 = 0
        L_0x0109:
            if (r10 == 0) goto L_0x0111
            if (r10 == r3) goto L_0x010e
            goto L_0x0113
        L_0x010e:
            r11.gender = r7
            goto L_0x0113
        L_0x0111:
            r11.gender = r3
        L_0x0113:
            java.lang.String r1 = r11.issuingCountry
            boolean r1 = r9.equals(r1)
            if (r1 == 0) goto L_0x011e
            r0 = 4
            r6 = 2
            r8 = 0
        L_0x011e:
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r1 = r2.driverLicense     // Catch:{ NumberFormatException -> 0x01a0 }
            java.lang.String r1 = r1.birthDate     // Catch:{ NumberFormatException -> 0x01a0 }
            r3 = 8
            if (r1 == 0) goto L_0x0160
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r1 = r2.driverLicense     // Catch:{ NumberFormatException -> 0x01a0 }
            java.lang.String r1 = r1.birthDate     // Catch:{ NumberFormatException -> 0x01a0 }
            int r1 = r1.length()     // Catch:{ NumberFormatException -> 0x01a0 }
            if (r1 != r3) goto L_0x0160
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r1 = r2.driverLicense     // Catch:{ NumberFormatException -> 0x01a0 }
            java.lang.String r1 = r1.birthDate     // Catch:{ NumberFormatException -> 0x01a0 }
            int r4 = r0 + 4
            java.lang.String r1 = r1.substring(r0, r4)     // Catch:{ NumberFormatException -> 0x01a0 }
            int r1 = java.lang.Integer.parseInt(r1)     // Catch:{ NumberFormatException -> 0x01a0 }
            r11.birthYear = r1     // Catch:{ NumberFormatException -> 0x01a0 }
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r1 = r2.driverLicense     // Catch:{ NumberFormatException -> 0x01a0 }
            java.lang.String r1 = r1.birthDate     // Catch:{ NumberFormatException -> 0x01a0 }
            int r4 = r8 + 2
            java.lang.String r1 = r1.substring(r8, r4)     // Catch:{ NumberFormatException -> 0x01a0 }
            int r1 = java.lang.Integer.parseInt(r1)     // Catch:{ NumberFormatException -> 0x01a0 }
            r11.birthMonth = r1     // Catch:{ NumberFormatException -> 0x01a0 }
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r1 = r2.driverLicense     // Catch:{ NumberFormatException -> 0x01a0 }
            java.lang.String r1 = r1.birthDate     // Catch:{ NumberFormatException -> 0x01a0 }
            int r4 = r6 + 2
            java.lang.String r1 = r1.substring(r6, r4)     // Catch:{ NumberFormatException -> 0x01a0 }
            int r1 = java.lang.Integer.parseInt(r1)     // Catch:{ NumberFormatException -> 0x01a0 }
            r11.birthDay = r1     // Catch:{ NumberFormatException -> 0x01a0 }
        L_0x0160:
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r1 = r2.driverLicense     // Catch:{ NumberFormatException -> 0x01a0 }
            java.lang.String r1 = r1.expiryDate     // Catch:{ NumberFormatException -> 0x01a0 }
            if (r1 == 0) goto L_0x01a0
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r1 = r2.driverLicense     // Catch:{ NumberFormatException -> 0x01a0 }
            java.lang.String r1 = r1.expiryDate     // Catch:{ NumberFormatException -> 0x01a0 }
            int r1 = r1.length()     // Catch:{ NumberFormatException -> 0x01a0 }
            if (r1 != r3) goto L_0x01a0
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r1 = r2.driverLicense     // Catch:{ NumberFormatException -> 0x01a0 }
            java.lang.String r1 = r1.expiryDate     // Catch:{ NumberFormatException -> 0x01a0 }
            int r3 = r0 + 4
            java.lang.String r0 = r1.substring(r0, r3)     // Catch:{ NumberFormatException -> 0x01a0 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ NumberFormatException -> 0x01a0 }
            r11.expiryYear = r0     // Catch:{ NumberFormatException -> 0x01a0 }
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r0 = r2.driverLicense     // Catch:{ NumberFormatException -> 0x01a0 }
            java.lang.String r0 = r0.expiryDate     // Catch:{ NumberFormatException -> 0x01a0 }
            int r1 = r8 + 2
            java.lang.String r0 = r0.substring(r8, r1)     // Catch:{ NumberFormatException -> 0x01a0 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ NumberFormatException -> 0x01a0 }
            r11.expiryMonth = r0     // Catch:{ NumberFormatException -> 0x01a0 }
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r0 = r2.driverLicense     // Catch:{ NumberFormatException -> 0x01a0 }
            java.lang.String r0 = r0.expiryDate     // Catch:{ NumberFormatException -> 0x01a0 }
            int r1 = r6 + 2
            java.lang.String r0 = r0.substring(r6, r1)     // Catch:{ NumberFormatException -> 0x01a0 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ NumberFormatException -> 0x01a0 }
            r11.expiryDay = r0     // Catch:{ NumberFormatException -> 0x01a0 }
        L_0x01a0:
            return r11
        L_0x01a1:
            int r4 = r2.valueFormat
            r5 = 7
            if (r4 != r5) goto L_0x024f
            int r4 = r2.format
            r5 = 2048(0x800, float:2.87E-42)
            if (r4 != r5) goto L_0x024f
            java.lang.String r4 = r2.rawValue
            java.lang.String r5 = "^[A-Za-z0-9=]+$"
            boolean r4 = r4.matches(r5)
            if (r4 == 0) goto L_0x024f
            java.lang.String r2 = r2.rawValue     // Catch:{ Exception -> 0x024f }
            byte[] r2 = android.util.Base64.decode(r2, r0)     // Catch:{ Exception -> 0x024f }
            java.lang.String r4 = new java.lang.String     // Catch:{ Exception -> 0x024f }
            java.lang.String r5 = "windows-1251"
            r4.<init>(r2, r5)     // Catch:{ Exception -> 0x024f }
            java.lang.String r2 = "\\|"
            java.lang.String[] r2 = r4.split(r2)     // Catch:{ Exception -> 0x024f }
            int r4 = r2.length     // Catch:{ Exception -> 0x024f }
            r5 = 10
            if (r4 < r5) goto L_0x024f
            org.telegram.messenger.MrzRecognizer$Result r4 = new org.telegram.messenger.MrzRecognizer$Result     // Catch:{ Exception -> 0x024f }
            r4.<init>()     // Catch:{ Exception -> 0x024f }
            r4.type = r8     // Catch:{ Exception -> 0x024f }
            java.lang.String r5 = "RU"
            r4.issuingCountry = r5     // Catch:{ Exception -> 0x024f }
            r4.nationality = r5     // Catch:{ Exception -> 0x024f }
            r5 = r2[r0]     // Catch:{ Exception -> 0x024f }
            r4.number = r5     // Catch:{ Exception -> 0x024f }
            r5 = r2[r7]     // Catch:{ Exception -> 0x024f }
            java.lang.String r5 = r5.substring(r0, r8)     // Catch:{ Exception -> 0x024f }
            int r5 = java.lang.Integer.parseInt(r5)     // Catch:{ Exception -> 0x024f }
            r4.expiryYear = r5     // Catch:{ Exception -> 0x024f }
            r5 = r2[r7]     // Catch:{ Exception -> 0x024f }
            java.lang.String r5 = r5.substring(r8, r6)     // Catch:{ Exception -> 0x024f }
            int r5 = java.lang.Integer.parseInt(r5)     // Catch:{ Exception -> 0x024f }
            r4.expiryMonth = r5     // Catch:{ Exception -> 0x024f }
            r5 = r2[r7]     // Catch:{ Exception -> 0x024f }
            java.lang.String r5 = r5.substring(r6)     // Catch:{ Exception -> 0x024f }
            int r5 = java.lang.Integer.parseInt(r5)     // Catch:{ Exception -> 0x024f }
            r4.expiryDay = r5     // Catch:{ Exception -> 0x024f }
            r5 = 3
            r5 = r2[r5]     // Catch:{ Exception -> 0x024f }
            java.lang.String r5 = cyrillicToLatin(r5)     // Catch:{ Exception -> 0x024f }
            java.lang.String r5 = capitalize(r5)     // Catch:{ Exception -> 0x024f }
            r4.lastName = r5     // Catch:{ Exception -> 0x024f }
            r5 = r2[r8]     // Catch:{ Exception -> 0x024f }
            java.lang.String r5 = cyrillicToLatin(r5)     // Catch:{ Exception -> 0x024f }
            java.lang.String r5 = capitalize(r5)     // Catch:{ Exception -> 0x024f }
            r4.firstName = r5     // Catch:{ Exception -> 0x024f }
            r5 = 5
            r5 = r2[r5]     // Catch:{ Exception -> 0x024f }
            java.lang.String r5 = cyrillicToLatin(r5)     // Catch:{ Exception -> 0x024f }
            java.lang.String r5 = capitalize(r5)     // Catch:{ Exception -> 0x024f }
            r4.middleName = r5     // Catch:{ Exception -> 0x024f }
            r5 = r2[r6]     // Catch:{ Exception -> 0x024f }
            java.lang.String r5 = r5.substring(r0, r8)     // Catch:{ Exception -> 0x024f }
            int r5 = java.lang.Integer.parseInt(r5)     // Catch:{ Exception -> 0x024f }
            r4.birthYear = r5     // Catch:{ Exception -> 0x024f }
            r5 = r2[r6]     // Catch:{ Exception -> 0x024f }
            java.lang.String r5 = r5.substring(r8, r6)     // Catch:{ Exception -> 0x024f }
            int r5 = java.lang.Integer.parseInt(r5)     // Catch:{ Exception -> 0x024f }
            r4.birthMonth = r5     // Catch:{ Exception -> 0x024f }
            r2 = r2[r6]     // Catch:{ Exception -> 0x024f }
            java.lang.String r2 = r2.substring(r6)     // Catch:{ Exception -> 0x024f }
            int r2 = java.lang.Integer.parseInt(r2)     // Catch:{ Exception -> 0x024f }
            r4.birthDay = r2     // Catch:{ Exception -> 0x024f }
            return r4
        L_0x024f:
            int r1 = r1 + 1
            goto L_0x0057
        L_0x0253:
            r11 = 0
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MrzRecognizer.recognizeBarcode(android.graphics.Bitmap):org.telegram.messenger.MrzRecognizer$Result");
    }

    /* JADX WARNING: Removed duplicated region for block: B:103:0x045d  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x04c0  */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x026c A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x028b A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x020e  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0246  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0257  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x026d  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x03d9  */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x040b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static org.telegram.messenger.MrzRecognizer.Result recognizeMRZ(android.graphics.Bitmap r26) {
        /*
            r0 = r26
            int r1 = r26.getWidth()
            r2 = 1
            r3 = 512(0x200, float:7.175E-43)
            if (r1 > r3) goto L_0x0018
            int r1 = r26.getHeight()
            r3 = 512(0x200, float:7.175E-43)
            if (r1 <= r3) goto L_0x0014
            goto L_0x0018
        L_0x0014:
            r1 = 1065353216(0x3var_, float:1.0)
            r3 = r0
            goto L_0x0042
        L_0x0018:
            r1 = 1140850688(0x44000000, float:512.0)
            int r3 = r26.getWidth()
            int r4 = r26.getHeight()
            int r3 = java.lang.Math.max(r3, r4)
            float r3 = (float) r3
            float r1 = r1 / r3
            int r3 = r26.getWidth()
            float r3 = (float) r3
            float r3 = r3 * r1
            int r3 = java.lang.Math.round(r3)
            int r4 = r26.getHeight()
            float r4 = (float) r4
            float r4 = r4 * r1
            int r4 = java.lang.Math.round(r4)
            android.graphics.Bitmap r3 = android.graphics.Bitmap.createScaledBitmap(r0, r3, r4, r2)
        L_0x0042:
            int[] r3 = findCornerPoints(r3)
            r4 = 1065353216(0x3var_, float:1.0)
            float r4 = r4 / r1
            r1 = 6
            r5 = 3
            r6 = 5
            r7 = 2
            r8 = 0
            if (r3 == 0) goto L_0x01c4
            android.graphics.Point r9 = new android.graphics.Point
            r10 = r3[r8]
            r11 = r3[r2]
            r9.<init>(r10, r11)
            android.graphics.Point r10 = new android.graphics.Point
            r11 = r3[r7]
            r12 = r3[r5]
            r10.<init>(r11, r12)
            android.graphics.Point r11 = new android.graphics.Point
            r12 = 4
            r12 = r3[r12]
            r13 = r3[r6]
            r11.<init>(r12, r13)
            android.graphics.Point r12 = new android.graphics.Point
            r13 = r3[r1]
            r14 = 7
            r3 = r3[r14]
            r12.<init>(r13, r3)
            int r3 = r10.x
            int r13 = r9.x
            if (r3 >= r13) goto L_0x007d
            goto L_0x0087
        L_0x007d:
            r24 = r10
            r10 = r9
            r9 = r24
            r25 = r12
            r12 = r11
            r11 = r25
        L_0x0087:
            int r3 = r9.x
            int r13 = r10.x
            int r3 = r3 - r13
            double r13 = (double) r3
            int r3 = r9.y
            int r15 = r10.y
            int r3 = r3 - r15
            double r5 = (double) r3
            double r5 = java.lang.Math.hypot(r13, r5)
            int r3 = r11.x
            int r13 = r12.x
            int r3 = r3 - r13
            double r13 = (double) r3
            int r3 = r11.y
            int r15 = r12.y
            int r3 = r3 - r15
            double r1 = (double) r3
            double r1 = java.lang.Math.hypot(r13, r1)
            int r3 = r12.x
            int r13 = r10.x
            int r3 = r3 - r13
            double r13 = (double) r3
            int r3 = r12.y
            int r15 = r10.y
            int r3 = r3 - r15
            double r7 = (double) r3
            double r7 = java.lang.Math.hypot(r13, r7)
            int r3 = r11.x
            int r13 = r9.x
            int r3 = r3 - r13
            double r13 = (double) r3
            int r3 = r11.y
            int r15 = r9.y
            int r3 = r3 - r15
            r16 = r11
            r17 = r12
            double r11 = (double) r3
            double r11 = java.lang.Math.hypot(r13, r11)
            double r13 = r5 / r7
            double r5 = r5 / r11
            double r7 = r1 / r7
            double r1 = r1 / r11
            r11 = 4608758678669597082(0x3fvar_a, double:1.35)
            int r3 = (r13 > r11 ? 1 : (r13 == r11 ? 0 : -1))
            if (r3 < 0) goto L_0x01d5
            r11 = 4610560118520545280(0x3ffcNUM, double:1.75)
            int r3 = (r13 > r11 ? 1 : (r13 == r11 ? 0 : -1))
            if (r3 > 0) goto L_0x01d5
            r11 = 4608758678669597082(0x3fvar_a, double:1.35)
            int r3 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r3 < 0) goto L_0x01d5
            r11 = 4610560118520545280(0x3ffcNUM, double:1.75)
            int r3 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r3 > 0) goto L_0x01d5
            r11 = 4608758678669597082(0x3fvar_a, double:1.35)
            int r3 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1))
            if (r3 < 0) goto L_0x01d5
            r11 = 4610560118520545280(0x3ffcNUM, double:1.75)
            int r3 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1))
            if (r3 > 0) goto L_0x01d5
            r11 = 4608758678669597082(0x3fvar_a, double:1.35)
            int r3 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r3 < 0) goto L_0x01d5
            r11 = 4610560118520545280(0x3ffcNUM, double:1.75)
            int r3 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r3 > 0) goto L_0x01d5
            double r13 = r13 + r5
            double r13 = r13 + r7
            double r13 = r13 + r1
            r1 = 4616189618054758400(0xNUM, double:4.0)
            double r13 = r13 / r1
            r1 = 1024(0x400, float:1.435E-42)
            r2 = 4652218415073722368(0xNUM, double:1024.0)
            double r2 = r2 / r13
            long r2 = java.lang.Math.round(r2)
            int r3 = (int) r2
            android.graphics.Bitmap$Config r2 = android.graphics.Bitmap.Config.ARGB_8888
            android.graphics.Bitmap r1 = android.graphics.Bitmap.createBitmap(r1, r3, r2)
            android.graphics.Canvas r2 = new android.graphics.Canvas
            r2.<init>(r1)
            r3 = 8
            float[] r3 = new float[r3]
            r5 = 0
            r6 = 0
            r3[r6] = r5
            r6 = 1
            r3[r6] = r5
            int r5 = r1.getWidth()
            float r5 = (float) r5
            r6 = 2
            r3[r6] = r5
            r5 = 0
            r6 = 3
            r3[r6] = r5
            r5 = 4
            int r6 = r1.getWidth()
            float r6 = (float) r6
            r3[r5] = r6
            int r5 = r1.getHeight()
            float r5 = (float) r5
            r6 = 5
            r3[r6] = r5
            r5 = 0
            r6 = 6
            r3[r6] = r5
            r5 = 7
            int r6 = r1.getHeight()
            float r6 = (float) r6
            r3[r5] = r6
            r5 = 8
            float[] r5 = new float[r5]
            int r6 = r10.x
            float r6 = (float) r6
            float r6 = r6 * r4
            r7 = 0
            r5[r7] = r6
            int r6 = r10.y
            float r6 = (float) r6
            float r6 = r6 * r4
            r7 = 1
            r5[r7] = r6
            int r6 = r9.x
            float r6 = (float) r6
            float r6 = r6 * r4
            r7 = 2
            r5[r7] = r6
            int r6 = r9.y
            float r6 = (float) r6
            float r6 = r6 * r4
            r7 = 3
            r5[r7] = r6
            r6 = 4
            r11 = r16
            int r7 = r11.x
            float r7 = (float) r7
            float r7 = r7 * r4
            r5[r6] = r7
            int r6 = r11.y
            float r6 = (float) r6
            float r6 = r6 * r4
            r7 = 5
            r5[r7] = r6
            r11 = r17
            int r6 = r11.x
            float r6 = (float) r6
            float r6 = r6 * r4
            r7 = 6
            r5[r7] = r6
            r6 = 7
            int r7 = r11.y
            float r7 = (float) r7
            float r7 = r7 * r4
            r5[r6] = r7
            android.graphics.Matrix r4 = new android.graphics.Matrix
            r4.<init>()
            r20 = 0
            r22 = 0
            int r6 = r5.length
            r7 = 1
            int r23 = r6 >> 1
            r18 = r4
            r19 = r5
            r21 = r3
            r18.setPolyToPoly(r19, r20, r21, r22, r23)
            android.graphics.Paint r3 = new android.graphics.Paint
            r5 = 2
            r3.<init>(r5)
            r2.drawBitmap(r0, r4, r3)
            r0 = r1
            goto L_0x01d5
        L_0x01c4:
            int r1 = r26.getWidth()
            r2 = 1500(0x5dc, float:2.102E-42)
            if (r1 > r2) goto L_0x01d7
            int r1 = r26.getHeight()
            r2 = 1500(0x5dc, float:2.102E-42)
            if (r1 <= r2) goto L_0x01d5
            goto L_0x01d7
        L_0x01d5:
            r3 = 1
            goto L_0x0203
        L_0x01d7:
            r1 = 1153138688(0x44bb8000, float:1500.0)
            int r2 = r26.getWidth()
            int r3 = r26.getHeight()
            int r2 = java.lang.Math.max(r2, r3)
            float r2 = (float) r2
            float r1 = r1 / r2
            int r2 = r26.getWidth()
            float r2 = (float) r2
            float r2 = r2 * r1
            int r2 = java.lang.Math.round(r2)
            int r3 = r26.getHeight()
            float r3 = (float) r3
            float r3 = r3 * r1
            int r1 = java.lang.Math.round(r3)
            r3 = 1
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r2, r1, r3)
        L_0x0203:
            r1 = 0
            r4 = r1
            r5 = r4
            r2 = 0
            r11 = 0
            r12 = 0
        L_0x0209:
            r13 = 30
            r6 = 3
            if (r2 >= r6) goto L_0x028b
            if (r2 == r3) goto L_0x022c
            r3 = 2
            if (r2 == r3) goto L_0x0215
            r9 = r1
            goto L_0x0244
        L_0x0215:
            android.graphics.Matrix r4 = new android.graphics.Matrix
            r4.<init>()
            r5 = -1082130432(0xffffffffbvar_, float:-1.0)
            int r6 = r0.getWidth()
            int r6 = r6 / r3
            float r6 = (float) r6
            int r7 = r0.getHeight()
            int r7 = r7 / r3
            float r7 = (float) r7
            r4.setRotate(r5, r6, r7)
            goto L_0x0243
        L_0x022c:
            r3 = 2
            android.graphics.Matrix r4 = new android.graphics.Matrix
            r4.<init>()
            r5 = 1065353216(0x3var_, float:1.0)
            int r6 = r0.getWidth()
            int r6 = r6 / r3
            float r6 = (float) r6
            int r7 = r0.getHeight()
            int r7 = r7 / r3
            float r3 = (float) r7
            r4.setRotate(r5, r6, r3)
        L_0x0243:
            r9 = r4
        L_0x0244:
            if (r9 == 0) goto L_0x0257
            r5 = 0
            r6 = 0
            int r7 = r0.getWidth()
            int r8 = r0.getHeight()
            r10 = 1
            r4 = r0
            android.graphics.Bitmap r3 = android.graphics.Bitmap.createBitmap(r4, r5, r6, r7, r8, r9, r10)
            goto L_0x0258
        L_0x0257:
            r3 = r0
        L_0x0258:
            int r4 = r3.getWidth()
            int r5 = r3.getHeight()
            android.graphics.Bitmap$Config r6 = android.graphics.Bitmap.Config.ALPHA_8
            android.graphics.Bitmap r4 = android.graphics.Bitmap.createBitmap(r4, r5, r6)
            android.graphics.Rect[][] r5 = binarizeAndFindCharacters(r3, r4)
            if (r5 != 0) goto L_0x026d
            return r1
        L_0x026d:
            int r3 = r5.length
            r6 = 0
        L_0x026f:
            if (r6 >= r3) goto L_0x0280
            r7 = r5[r6]
            int r8 = r7.length
            int r11 = java.lang.Math.max(r8, r11)
            int r7 = r7.length
            if (r7 <= 0) goto L_0x027d
            int r12 = r12 + 1
        L_0x027d:
            int r6 = r6 + 1
            goto L_0x026f
        L_0x0280:
            r6 = 2
            if (r12 < r6) goto L_0x0286
            if (r11 < r13) goto L_0x0286
            goto L_0x028c
        L_0x0286:
            int r2 = r2 + 1
            r3 = 1
            goto L_0x0209
        L_0x028b:
            r6 = 2
        L_0x028c:
            if (r11 < r13) goto L_0x07a4
            if (r12 >= r6) goto L_0x0292
            goto L_0x07a4
        L_0x0292:
            r0 = 0
            r2 = r5[r0]
            int r0 = r2.length
            int r0 = r0 * 10
            int r2 = r5.length
            int r2 = r2 * 15
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ALPHA_8
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createBitmap(r0, r2, r3)
            android.graphics.Canvas r2 = new android.graphics.Canvas
            r2.<init>(r0)
            android.graphics.Paint r3 = new android.graphics.Paint
            r6 = 2
            r3.<init>(r6)
            android.graphics.Rect r6 = new android.graphics.Rect
            r7 = 10
            r8 = 15
            r9 = 0
            r6.<init>(r9, r9, r7, r8)
            int r7 = r5.length
            r8 = 0
            r9 = 0
        L_0x02b9:
            if (r8 >= r7) goto L_0x02ec
            r10 = r5[r8]
            int r11 = r10.length
            r12 = 0
            r14 = 0
        L_0x02c0:
            if (r12 >= r11) goto L_0x02e2
            r15 = r10[r12]
            int r13 = r14 * 10
            int r1 = r9 * 15
            r17 = r7
            int r7 = r13 + 10
            r18 = r10
            int r10 = r1 + 15
            r6.set(r13, r1, r7, r10)
            r2.drawBitmap(r4, r15, r6, r3)
            r1 = 1
            int r14 = r14 + r1
            int r12 = r12 + 1
            r7 = r17
            r10 = r18
            r1 = 0
            r13 = 30
            goto L_0x02c0
        L_0x02e2:
            r17 = r7
            int r9 = r9 + 1
            int r8 = r8 + 1
            r1 = 0
            r13 = 30
            goto L_0x02b9
        L_0x02ec:
            int r1 = r5.length
            r2 = 0
            r3 = r5[r2]
            int r2 = r3.length
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.content.res.AssetManager r3 = r3.getAssets()
            java.lang.String r0 = performRecognition(r0, r1, r2, r3)
            if (r0 != 0) goto L_0x02ff
            r1 = 0
            return r1
        L_0x02ff:
            java.lang.String r1 = "\n"
            java.lang.String[] r0 = android.text.TextUtils.split(r0, r1)
            org.telegram.messenger.MrzRecognizer$Result r1 = new org.telegram.messenger.MrzRecognizer$Result
            r1.<init>()
            int r2 = r0.length
            r3 = 2
            if (r2 < r3) goto L_0x07a2
            r2 = 0
            r3 = r0[r2]
            int r3 = r3.length()
            r4 = 30
            if (r3 < r4) goto L_0x07a2
            r3 = 1
            r4 = r0[r3]
            int r3 = r4.length()
            r4 = r0[r2]
            int r4 = r4.length()
            if (r3 != r4) goto L_0x07a2
            java.lang.String r3 = "\n"
            java.lang.String r3 = android.text.TextUtils.join(r3, r0)
            r1.rawMRZ = r3
            java.util.HashMap r3 = getCountriesMap()
            r4 = r0[r2]
            char r4 = r4.charAt(r2)
            r5 = 80
            r6 = 49
            r7 = 27
            r8 = 73
            r9 = 32
            r10 = 60
            r11 = 79
            r12 = 48
            if (r4 != r5) goto L_0x04ee
            r5 = 1
            r1.type = r5
            r4 = r0[r2]
            int r4 = r4.length()
            r5 = 44
            if (r4 != r5) goto L_0x077b
            r4 = r0[r2]
            r5 = 5
            r13 = 2
            java.lang.String r4 = r4.substring(r13, r5)
            r1.issuingCountry = r4
            r4 = r0[r2]
            java.lang.String r13 = "<<"
            r14 = 6
            int r4 = r4.indexOf(r13, r14)
            r13 = -1
            if (r4 == r13) goto L_0x03b3
            r13 = r0[r2]
            java.lang.String r5 = r13.substring(r5, r4)
            java.lang.String r5 = r5.replace(r10, r9)
            java.lang.String r5 = r5.replace(r12, r11)
            java.lang.String r5 = r5.trim()
            r1.lastName = r5
            r5 = r0[r2]
            r2 = 2
            int r4 = r4 + r2
            java.lang.String r2 = r5.substring(r4)
            java.lang.String r2 = r2.replace(r10, r9)
            java.lang.String r2 = r2.replace(r12, r11)
            java.lang.String r2 = r2.trim()
            r1.firstName = r2
            java.lang.String r2 = r1.firstName
            java.lang.String r4 = "   "
            boolean r2 = r2.contains(r4)
            if (r2 == 0) goto L_0x03b3
            java.lang.String r2 = r1.firstName
            java.lang.String r4 = "   "
            int r4 = r2.indexOf(r4)
            r5 = 0
            java.lang.String r2 = r2.substring(r5, r4)
            r1.firstName = r2
            goto L_0x03b4
        L_0x03b3:
            r5 = 0
        L_0x03b4:
            r2 = 1
            r4 = r0[r2]
            r13 = 9
            java.lang.String r4 = r4.substring(r5, r13)
            java.lang.String r4 = r4.replace(r10, r9)
            java.lang.String r4 = r4.replace(r11, r12)
            java.lang.String r4 = r4.trim()
            int r5 = checksum(r4)
            r9 = r0[r2]
            char r9 = r9.charAt(r13)
            int r9 = getNumber(r9)
            if (r5 != r9) goto L_0x03db
            r1.number = r4
        L_0x03db:
            r4 = r0[r2]
            r5 = 10
            r9 = 13
            java.lang.String r4 = r4.substring(r5, r9)
            r1.nationality = r4
            r4 = r0[r2]
            r5 = 13
            r9 = 19
            java.lang.String r4 = r4.substring(r5, r9)
            java.lang.String r4 = r4.replace(r11, r12)
            java.lang.String r4 = r4.replace(r8, r6)
            int r5 = checksum(r4)
            r9 = r0[r2]
            r13 = 19
            char r9 = r9.charAt(r13)
            int r9 = getNumber(r9)
            if (r5 != r9) goto L_0x040e
            parseBirthDate(r4, r1)
        L_0x040e:
            r4 = r0[r2]
            r5 = 20
            char r4 = r4.charAt(r5)
            int r4 = parseGender(r4)
            r1.gender = r4
            r4 = r0[r2]
            r5 = 21
            java.lang.String r4 = r4.substring(r5, r7)
            java.lang.String r4 = r4.replace(r11, r12)
            java.lang.String r4 = r4.replace(r8, r6)
            int r5 = checksum(r4)
            r6 = r0[r2]
            char r6 = r6.charAt(r7)
            int r6 = getNumber(r6)
            if (r5 == r6) goto L_0x0444
            r5 = r0[r2]
            char r2 = r5.charAt(r7)
            if (r2 != r10) goto L_0x0447
        L_0x0444:
            parseExpiryDate(r4, r1)
        L_0x0447:
            java.lang.String r2 = r1.issuingCountry
            java.lang.String r4 = "RUS"
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L_0x04c0
            r2 = 0
            r4 = r0[r2]
            r5 = 1
            char r4 = r4.charAt(r5)
            r5 = 78
            if (r4 != r5) goto L_0x04c0
            r4 = 3
            r1.type = r4
            java.lang.String r4 = r1.firstName
            java.lang.String r5 = " "
            java.lang.String[] r4 = r4.split(r5)
            r5 = r4[r2]
            java.lang.String r2 = russianPassportTranslit(r5)
            java.lang.String r2 = cyrillicToLatin(r2)
            r1.firstName = r2
            int r2 = r4.length
            r5 = 1
            if (r2 <= r5) goto L_0x0484
            r2 = r4[r5]
            java.lang.String r2 = russianPassportTranslit(r2)
            java.lang.String r2 = cyrillicToLatin(r2)
            r1.middleName = r2
        L_0x0484:
            java.lang.String r2 = r1.lastName
            java.lang.String r2 = russianPassportTranslit(r2)
            java.lang.String r2 = cyrillicToLatin(r2)
            r1.lastName = r2
            java.lang.String r2 = r1.number
            if (r2 == 0) goto L_0x04d4
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = r1.number
            r5 = 3
            r6 = 0
            java.lang.String r4 = r4.substring(r6, r5)
            r2.append(r4)
            r4 = 1
            r0 = r0[r4]
            r4 = 28
            char r0 = r0.charAt(r4)
            r2.append(r0)
            java.lang.String r0 = r1.number
            java.lang.String r0 = r0.substring(r5)
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r1.number = r0
            goto L_0x04d4
        L_0x04c0:
            java.lang.String r0 = r1.firstName
            r2 = 56
            r4 = 66
            java.lang.String r0 = r0.replace(r2, r4)
            r1.firstName = r0
            java.lang.String r0 = r1.lastName
            java.lang.String r0 = r0.replace(r2, r4)
            r1.lastName = r0
        L_0x04d4:
            java.lang.String r0 = r1.lastName
            java.lang.String r0 = capitalize(r0)
            r1.lastName = r0
            java.lang.String r0 = r1.firstName
            java.lang.String r0 = capitalize(r0)
            r1.firstName = r0
            java.lang.String r0 = r1.middleName
            java.lang.String r0 = capitalize(r0)
            r1.middleName = r0
            goto L_0x077b
        L_0x04ee:
            if (r4 == r8) goto L_0x04fb
            r2 = 65
            if (r4 == r2) goto L_0x04fb
            r2 = 67
            if (r4 != r2) goto L_0x04f9
            goto L_0x04fb
        L_0x04f9:
            r2 = 0
            return r2
        L_0x04fb:
            r2 = 2
            r1.type = r2
            int r5 = r0.length
            r13 = 3
            if (r5 != r13) goto L_0x05de
            r5 = 0
            r13 = r0[r5]
            int r13 = r13.length()
            r14 = 30
            if (r13 != r14) goto L_0x05de
            r13 = r0[r2]
            int r13 = r13.length()
            if (r13 != r14) goto L_0x05de
            r4 = r0[r5]
            r7 = 5
            java.lang.String r4 = r4.substring(r2, r7)
            r1.issuingCountry = r4
            r2 = r0[r5]
            r4 = 14
            java.lang.String r2 = r2.substring(r7, r4)
            java.lang.String r2 = r2.replace(r10, r9)
            java.lang.String r2 = r2.replace(r11, r12)
            java.lang.String r2 = r2.trim()
            int r4 = checksum(r2)
            r7 = r0[r5]
            r13 = 14
            char r7 = r7.charAt(r13)
            int r7 = r7 - r12
            if (r4 != r7) goto L_0x0543
            r1.number = r2
        L_0x0543:
            r2 = 1
            r4 = r0[r2]
            r7 = 6
            java.lang.String r4 = r4.substring(r5, r7)
            java.lang.String r4 = r4.replace(r11, r12)
            java.lang.String r4 = r4.replace(r8, r6)
            int r5 = checksum(r4)
            r13 = r0[r2]
            char r7 = r13.charAt(r7)
            int r7 = getNumber(r7)
            if (r5 != r7) goto L_0x0566
            parseBirthDate(r4, r1)
        L_0x0566:
            r4 = r0[r2]
            r5 = 7
            char r4 = r4.charAt(r5)
            int r4 = parseGender(r4)
            r1.gender = r4
            r4 = r0[r2]
            r5 = 8
            r7 = 14
            java.lang.String r4 = r4.substring(r5, r7)
            java.lang.String r4 = r4.replace(r11, r12)
            java.lang.String r4 = r4.replace(r8, r6)
            int r5 = checksum(r4)
            r6 = r0[r2]
            char r6 = r6.charAt(r7)
            int r6 = getNumber(r6)
            if (r5 == r6) goto L_0x059f
            r5 = r0[r2]
            r6 = 14
            char r5 = r5.charAt(r6)
            if (r5 != r10) goto L_0x05a2
        L_0x059f:
            parseExpiryDate(r4, r1)
        L_0x05a2:
            r2 = r0[r2]
            r4 = 15
            r5 = 18
            java.lang.String r2 = r2.substring(r4, r5)
            r1.nationality = r2
            r2 = 2
            r4 = r0[r2]
            java.lang.String r5 = "<<"
            int r4 = r4.indexOf(r5)
            r5 = -1
            if (r4 == r5) goto L_0x0757
            r5 = r0[r2]
            r6 = 0
            java.lang.String r5 = r5.substring(r6, r4)
            java.lang.String r5 = r5.replace(r10, r9)
            java.lang.String r5 = r5.trim()
            r1.lastName = r5
            r0 = r0[r2]
            int r4 = r4 + r2
            java.lang.String r0 = r0.substring(r4)
            java.lang.String r0 = r0.replace(r10, r9)
            java.lang.String r0 = r0.trim()
            r1.firstName = r0
            goto L_0x0757
        L_0x05de:
            int r2 = r0.length
            r5 = 2
            if (r2 != r5) goto L_0x0757
            r2 = 0
            r13 = r0[r2]
            int r13 = r13.length()
            r14 = 36
            if (r13 != r14) goto L_0x0757
            r13 = r0[r2]
            r14 = 5
            java.lang.String r13 = r13.substring(r5, r14)
            r1.issuingCountry = r13
            java.lang.String r5 = r1.issuingCountry
            java.lang.String r13 = "FRA"
            boolean r5 = r13.equals(r5)
            if (r5 == 0) goto L_0x0696
            if (r4 != r8) goto L_0x0696
            r4 = r0[r2]
            r5 = 1
            char r4 = r4.charAt(r5)
            r13 = 68
            if (r4 != r13) goto L_0x0696
            java.lang.String r4 = "FRA"
            r1.nationality = r4
            r4 = r0[r2]
            r2 = 30
            r13 = 5
            java.lang.String r2 = r4.substring(r13, r2)
            java.lang.String r2 = r2.replace(r10, r9)
            java.lang.String r2 = r2.trim()
            r1.lastName = r2
            r2 = r0[r5]
            r4 = 13
            java.lang.String r2 = r2.substring(r4, r7)
            java.lang.String r4 = "<<"
            java.lang.String r13 = ", "
            java.lang.String r2 = r2.replace(r4, r13)
            java.lang.String r2 = r2.replace(r10, r9)
            java.lang.String r2 = r2.trim()
            r1.firstName = r2
            r2 = r0[r5]
            r4 = 12
            r9 = 0
            java.lang.String r2 = r2.substring(r9, r4)
            java.lang.String r2 = r2.replace(r11, r12)
            int r4 = checksum(r2)
            r9 = r0[r5]
            r10 = 12
            char r9 = r9.charAt(r10)
            int r9 = getNumber(r9)
            if (r4 != r9) goto L_0x065f
            r1.number = r2
        L_0x065f:
            r2 = r0[r5]
            r4 = 33
            java.lang.String r2 = r2.substring(r7, r4)
            java.lang.String r2 = r2.replace(r11, r12)
            java.lang.String r2 = r2.replace(r8, r6)
            int r4 = checksum(r2)
            r6 = r0[r5]
            r7 = 33
            char r6 = r6.charAt(r7)
            int r6 = getNumber(r6)
            if (r4 != r6) goto L_0x0684
            parseBirthDate(r2, r1)
        L_0x0684:
            r0 = r0[r5]
            r2 = 34
            char r0 = r0.charAt(r2)
            int r0 = parseGender(r0)
            r1.gender = r0
            r1.doesNotExpire = r5
            goto L_0x0757
        L_0x0696:
            r4 = r0[r2]
            java.lang.String r5 = "<<"
            int r4 = r4.indexOf(r5)
            r5 = -1
            if (r4 == r5) goto L_0x06c4
            r5 = r0[r2]
            r13 = 5
            java.lang.String r5 = r5.substring(r13, r4)
            java.lang.String r5 = r5.replace(r10, r9)
            java.lang.String r5 = r5.trim()
            r1.lastName = r5
            r5 = r0[r2]
            r13 = 2
            int r4 = r4 + r13
            java.lang.String r4 = r5.substring(r4)
            java.lang.String r4 = r4.replace(r10, r9)
            java.lang.String r4 = r4.trim()
            r1.firstName = r4
        L_0x06c4:
            r4 = 1
            r5 = r0[r4]
            r13 = 9
            java.lang.String r2 = r5.substring(r2, r13)
            java.lang.String r2 = r2.replace(r10, r9)
            java.lang.String r2 = r2.replace(r11, r12)
            java.lang.String r2 = r2.trim()
            int r5 = checksum(r2)
            r9 = r0[r4]
            char r9 = r9.charAt(r13)
            int r9 = getNumber(r9)
            if (r5 != r9) goto L_0x06eb
            r1.number = r2
        L_0x06eb:
            r2 = r0[r4]
            r5 = 10
            r9 = 13
            java.lang.String r2 = r2.substring(r5, r9)
            r1.nationality = r2
            r2 = r0[r4]
            r5 = 13
            r9 = 19
            java.lang.String r2 = r2.substring(r5, r9)
            java.lang.String r2 = r2.replace(r11, r12)
            java.lang.String r2 = r2.replace(r8, r6)
            int r5 = checksum(r2)
            r9 = r0[r4]
            r13 = 19
            char r9 = r9.charAt(r13)
            int r9 = getNumber(r9)
            if (r5 != r9) goto L_0x071e
            parseBirthDate(r2, r1)
        L_0x071e:
            r2 = r0[r4]
            r5 = 20
            char r2 = r2.charAt(r5)
            int r2 = parseGender(r2)
            r1.gender = r2
            r2 = r0[r4]
            r5 = 21
            java.lang.String r2 = r2.substring(r5, r7)
            java.lang.String r2 = r2.replace(r11, r12)
            java.lang.String r2 = r2.replace(r8, r6)
            int r5 = checksum(r2)
            r6 = r0[r4]
            char r6 = r6.charAt(r7)
            int r6 = getNumber(r6)
            if (r5 == r6) goto L_0x0754
            r0 = r0[r4]
            char r0 = r0.charAt(r7)
            if (r0 != r10) goto L_0x0757
        L_0x0754:
            parseExpiryDate(r2, r1)
        L_0x0757:
            java.lang.String r0 = r1.firstName
            java.lang.String r0 = r0.replace(r12, r11)
            r2 = 56
            r4 = 66
            java.lang.String r0 = r0.replace(r2, r4)
            java.lang.String r0 = capitalize(r0)
            r1.firstName = r0
            java.lang.String r0 = r1.lastName
            java.lang.String r0 = r0.replace(r12, r11)
            java.lang.String r0 = r0.replace(r2, r4)
            java.lang.String r0 = capitalize(r0)
            r1.lastName = r0
        L_0x077b:
            java.lang.String r0 = r1.firstName
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x078d
            java.lang.String r0 = r1.lastName
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x078d
            r0 = 0
            return r0
        L_0x078d:
            java.lang.String r0 = r1.issuingCountry
            java.lang.Object r0 = r3.get(r0)
            java.lang.String r0 = (java.lang.String) r0
            r1.issuingCountry = r0
            java.lang.String r0 = r1.nationality
            java.lang.Object r0 = r3.get(r0)
            java.lang.String r0 = (java.lang.String) r0
            r1.nationality = r0
            return r1
        L_0x07a2:
            r0 = 0
            return r0
        L_0x07a4:
            r0 = r1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MrzRecognizer.recognizeMRZ(android.graphics.Bitmap):org.telegram.messenger.MrzRecognizer$Result");
    }

    public static Result recognize(byte[] bArr, int i, int i2, int i3) {
        Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
        setYuvBitmapPixels(createBitmap, bArr);
        Matrix matrix = new Matrix();
        matrix.setRotate((float) i3);
        int min = Math.min(i, i2);
        int round = Math.round(((float) min) * 0.704f);
        boolean z = i3 == 90 || i3 == 270;
        return recognize(Bitmap.createBitmap(createBitmap, z ? (i / 2) - (round / 2) : 0, z ? 0 : (i2 / 2) - (round / 2), z ? round : min, z ? min : round, matrix, false), false);
    }

    private static String capitalize(String str) {
        if (str == null) {
            return null;
        }
        char[] charArray = str.toCharArray();
        boolean z = true;
        for (int i = 0; i < charArray.length; i++) {
            if (z || !Character.isLetter(charArray[i])) {
                z = charArray[i] == ' ';
            } else {
                charArray[i] = Character.toLowerCase(charArray[i]);
            }
        }
        return new String(charArray);
    }

    private static int checksum(String str) {
        int i;
        char[] charArray = str.toCharArray();
        int[] iArr = {7, 3, 1};
        int i2 = 0;
        for (int i3 = 0; i3 < charArray.length; i3++) {
            if (charArray[i3] < '0' || charArray[i3] > '9') {
                i = (charArray[i3] < 'A' || charArray[i3] > 'Z') ? 0 : (charArray[i3] - 'A') + 10;
            } else {
                i = charArray[i3] - '0';
            }
            i2 += i * iArr[i3 % iArr.length];
        }
        return i2 % 10;
    }

    private static void parseBirthDate(String str, Result result) {
        try {
            result.birthYear = Integer.parseInt(str.substring(0, 2));
            result.birthYear = result.birthYear < (Calendar.getInstance().get(1) % 100) + -5 ? result.birthYear + 2000 : result.birthYear + 1900;
            result.birthMonth = Integer.parseInt(str.substring(2, 4));
            result.birthDay = Integer.parseInt(str.substring(4));
        } catch (NumberFormatException unused) {
        }
    }

    private static void parseExpiryDate(String str, Result result) {
        try {
            if ("<<<<<<".equals(str)) {
                result.doesNotExpire = true;
                return;
            }
            result.expiryYear = Integer.parseInt(str.substring(0, 2)) + 2000;
            result.expiryMonth = Integer.parseInt(str.substring(2, 4));
            result.expiryDay = Integer.parseInt(str.substring(4));
        } catch (NumberFormatException unused) {
        }
    }

    private static String russianPassportTranslit(String str) {
        char[] charArray = str.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            int indexOf = "ABVGDE2JZIQKLMNOPRSTUFHCLASSNAMEWXY9678".indexOf(charArray[i]);
            if (indexOf != -1) {
                charArray[i] = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ".charAt(indexOf);
            }
        }
        return new String(charArray);
    }

    private static String cyrillicToLatin(String str) {
        int i = 0;
        String[] strArr = {"A", "B", "V", "G", "D", "E", "E", "ZH", "Z", "I", "I", "K", "L", "M", "N", "O", "P", "R", "S", "T", "U", "F", "KH", "TS", "CH", "SH", "SHCH", "IE", "Y", "", "E", "IU", "IA"};
        while (i < strArr.length) {
            int i2 = i + 1;
            str = str.replace("АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ".substring(i, i2), strArr[i]);
            i = i2;
        }
        return str;
    }

    private static HashMap<String, String> getCountriesMap() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("AFG", "AF");
        hashMap.put("ALA", "AX");
        hashMap.put("ALB", "AL");
        hashMap.put("DZA", "DZ");
        hashMap.put("ASM", "AS");
        hashMap.put("AND", "AD");
        hashMap.put("AGO", "AO");
        hashMap.put("AIA", "AI");
        hashMap.put("ATA", "AQ");
        hashMap.put("ATG", "AG");
        hashMap.put("ARG", "AR");
        hashMap.put("ARM", "AM");
        hashMap.put("ABW", "AW");
        hashMap.put("AUS", "AU");
        hashMap.put("AUT", "AT");
        hashMap.put("AZE", "AZ");
        hashMap.put("BHS", "BS");
        hashMap.put("BHR", "BH");
        hashMap.put("BGD", "BD");
        hashMap.put("BRB", "BB");
        hashMap.put("BLR", "BY");
        hashMap.put("BEL", "BE");
        hashMap.put("BLZ", "BZ");
        hashMap.put("BEN", "BJ");
        hashMap.put("BMU", "BM");
        hashMap.put("BTN", "BT");
        hashMap.put("BOL", "BO");
        hashMap.put("BES", "BQ");
        hashMap.put("BIH", "BA");
        hashMap.put("BWA", "BW");
        hashMap.put("BVT", "BV");
        hashMap.put("BRA", "BR");
        hashMap.put("IOT", "IO");
        hashMap.put("BRN", "BN");
        hashMap.put("BGR", "BG");
        hashMap.put("BFA", "BF");
        hashMap.put("BDI", "BI");
        hashMap.put("CPV", "CV");
        hashMap.put("KHM", "KH");
        hashMap.put("CMR", "CM");
        hashMap.put("CAN", "CA");
        hashMap.put("CYM", "KY");
        hashMap.put("CAF", "CF");
        hashMap.put("TCD", "TD");
        hashMap.put("CHL", "CL");
        hashMap.put("CHN", "CN");
        hashMap.put("CXR", "CX");
        hashMap.put("CCK", "CC");
        hashMap.put("COL", "CO");
        hashMap.put("COM", "KM");
        hashMap.put("COG", "CG");
        hashMap.put("COD", "CD");
        hashMap.put("COK", "CK");
        hashMap.put("CRI", "CR");
        hashMap.put("CIV", "CI");
        hashMap.put("HRV", "HR");
        hashMap.put("CUB", "CU");
        hashMap.put("CUW", "CW");
        hashMap.put("CYP", "CY");
        hashMap.put("CZE", "CZ");
        hashMap.put("DNK", "DK");
        hashMap.put("DJI", "DJ");
        hashMap.put("DMA", "DM");
        hashMap.put("DOM", "DO");
        hashMap.put("ECU", "EC");
        hashMap.put("EGY", "EG");
        hashMap.put("SLV", "SV");
        hashMap.put("GNQ", "GQ");
        hashMap.put("ERI", "ER");
        hashMap.put("EST", "EE");
        hashMap.put("ETH", "ET");
        hashMap.put("FLK", "FK");
        hashMap.put("FRO", "FO");
        hashMap.put("FJI", "FJ");
        hashMap.put("FIN", "FI");
        hashMap.put("FRA", "FR");
        hashMap.put("GUF", "GF");
        hashMap.put("PYF", "PF");
        hashMap.put("ATF", "TF");
        hashMap.put("GAB", "GA");
        hashMap.put("GMB", "GM");
        hashMap.put("GEO", "GE");
        hashMap.put("D<<", "DE");
        hashMap.put("GHA", "GH");
        hashMap.put("GIB", "GI");
        hashMap.put("GRC", "GR");
        hashMap.put("GRL", "GL");
        hashMap.put("GRD", "GD");
        hashMap.put("GLP", "GP");
        hashMap.put("GUM", "GU");
        hashMap.put("GTM", "GT");
        hashMap.put("GGY", "GG");
        hashMap.put("GIN", "GN");
        hashMap.put("GNB", "GW");
        hashMap.put("GUY", "GY");
        hashMap.put("HTI", "HT");
        hashMap.put("HMD", "HM");
        hashMap.put("VAT", "VA");
        hashMap.put("HND", "HN");
        hashMap.put("HKG", "HK");
        hashMap.put("HUN", "HU");
        hashMap.put("ISL", "IS");
        hashMap.put("IND", "IN");
        hashMap.put("IDN", "ID");
        hashMap.put("IRN", "IR");
        hashMap.put("IRQ", "IQ");
        hashMap.put("IRL", "IE");
        hashMap.put("IMN", "IM");
        hashMap.put("ISR", "IL");
        hashMap.put("ITA", "IT");
        hashMap.put("JAM", "JM");
        hashMap.put("JPN", "JP");
        hashMap.put("JEY", "JE");
        hashMap.put("JOR", "JO");
        hashMap.put("KAZ", "KZ");
        hashMap.put("KEN", "KE");
        hashMap.put("KIR", "KI");
        hashMap.put("PRK", "KP");
        hashMap.put("KOR", "KR");
        hashMap.put("KWT", "KW");
        hashMap.put("KGZ", "KG");
        hashMap.put("LAO", "LA");
        hashMap.put("LVA", "LV");
        hashMap.put("LBN", "LB");
        hashMap.put("LSO", "LS");
        hashMap.put("LBR", "LR");
        hashMap.put("LBY", "LY");
        hashMap.put("LIE", "LI");
        hashMap.put("LTU", "LT");
        hashMap.put("LUX", "LU");
        hashMap.put("MAC", "MO");
        hashMap.put("MKD", "MK");
        hashMap.put("MDG", "MG");
        hashMap.put("MWI", "MW");
        hashMap.put("MYS", "MY");
        hashMap.put("MDV", "MV");
        hashMap.put("MLI", "ML");
        hashMap.put("MLT", "MT");
        hashMap.put("MHL", "MH");
        hashMap.put("MTQ", "MQ");
        hashMap.put("MRT", "MR");
        hashMap.put("MUS", "MU");
        hashMap.put("MYT", "YT");
        hashMap.put("MEX", "MX");
        hashMap.put("FSM", "FM");
        hashMap.put("MDA", "MD");
        hashMap.put("MCO", "MC");
        hashMap.put("MNG", "MN");
        hashMap.put("MNE", "ME");
        hashMap.put("MSR", "MS");
        hashMap.put("MAR", "MA");
        hashMap.put("MOZ", "MZ");
        hashMap.put("MMR", "MM");
        hashMap.put("NAM", "NA");
        hashMap.put("NRU", "NR");
        hashMap.put("NPL", "NP");
        hashMap.put("NLD", "NL");
        hashMap.put("NCL", "NC");
        hashMap.put("NZL", "NZ");
        hashMap.put("NIC", "NI");
        hashMap.put("NER", "NE");
        hashMap.put("NGA", "NG");
        hashMap.put("NIU", "NU");
        hashMap.put("NFK", "NF");
        hashMap.put("MNP", "MP");
        hashMap.put("NOR", "NO");
        hashMap.put("OMN", "OM");
        hashMap.put("PAK", "PK");
        hashMap.put("PLW", "PW");
        hashMap.put("PSE", "PS");
        hashMap.put("PAN", "PA");
        hashMap.put("PNG", "PG");
        hashMap.put("PRY", "PY");
        hashMap.put("PER", "PE");
        hashMap.put("PHL", "PH");
        hashMap.put("PCN", "PN");
        hashMap.put("POL", "PL");
        hashMap.put("PRT", "PT");
        hashMap.put("PRI", "PR");
        hashMap.put("QAT", "QA");
        hashMap.put("REU", "RE");
        hashMap.put("ROU", "RO");
        hashMap.put("RUS", "RU");
        hashMap.put("RWA", "RW");
        hashMap.put("BLM", "BL");
        hashMap.put("SHN", "SH");
        hashMap.put("KNA", "KN");
        hashMap.put("LCA", "LC");
        hashMap.put("MAF", "MF");
        hashMap.put("SPM", "PM");
        hashMap.put("VCT", "VC");
        hashMap.put("WSM", "WS");
        hashMap.put("SMR", "SM");
        hashMap.put("STP", "ST");
        hashMap.put("SAU", "SA");
        hashMap.put("SEN", "SN");
        hashMap.put("SRB", "RS");
        hashMap.put("SYC", "SC");
        hashMap.put("SLE", "SL");
        hashMap.put("SGP", "SG");
        hashMap.put("SXM", "SX");
        hashMap.put("SVK", "SK");
        hashMap.put("SVN", "SI");
        hashMap.put("SLB", "SB");
        hashMap.put("SOM", "SO");
        hashMap.put("ZAF", "ZA");
        hashMap.put("SGS", "GS");
        hashMap.put("SSD", "SS");
        hashMap.put("ESP", "ES");
        hashMap.put("LKA", "LK");
        hashMap.put("SDN", "SD");
        hashMap.put("SUR", "SR");
        hashMap.put("SJM", "SJ");
        hashMap.put("SWZ", "SZ");
        hashMap.put("SWE", "SE");
        hashMap.put("CHE", "CH");
        hashMap.put("SYR", "SY");
        hashMap.put("TWN", "TW");
        hashMap.put("TJK", "TJ");
        hashMap.put("TZA", "TZ");
        hashMap.put("THA", "TH");
        hashMap.put("TLS", "TL");
        hashMap.put("TGO", "TG");
        hashMap.put("TKL", "TK");
        hashMap.put("TON", "TO");
        hashMap.put("TTO", "TT");
        hashMap.put("TUN", "TN");
        hashMap.put("TUR", "TR");
        hashMap.put("TKM", "TM");
        hashMap.put("TCA", "TC");
        hashMap.put("TUV", "TV");
        hashMap.put("UGA", "UG");
        hashMap.put("UKR", "UA");
        hashMap.put("ARE", "AE");
        hashMap.put("GBR", "GB");
        hashMap.put("USA", "US");
        hashMap.put("UMI", "UM");
        hashMap.put("URY", "UY");
        hashMap.put("UZB", "UZ");
        hashMap.put("VUT", "VU");
        hashMap.put("VEN", "VE");
        hashMap.put("VNM", "VN");
        hashMap.put("VGB", "VG");
        hashMap.put("VIR", "VI");
        hashMap.put("WLF", "WF");
        hashMap.put("ESH", "EH");
        hashMap.put("YEM", "YE");
        hashMap.put("ZMB", "ZM");
        hashMap.put("ZWE", "ZW");
        return hashMap;
    }
}
