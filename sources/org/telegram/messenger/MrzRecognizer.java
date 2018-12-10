package org.telegram.messenger;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.TextUtils;
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
        Result result;
        Result res = null;
        if (tryDriverLicenseFirst) {
            res = recognizeBarcode(bitmap);
            if (res != null) {
                return res;
            }
        }
        try {
            res = recognizeMRZ(bitmap);
            if (res != null) {
                result = res;
                return res;
            }
        } catch (Exception e) {
        }
        if (!tryDriverLicenseFirst) {
            res = recognizeBarcode(bitmap);
            if (res != null) {
                result = res;
                return res;
            }
        }
        result = res;
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

    private static Result recognizeMRZ(Bitmap bitmap) {
        Bitmap smallBitmap;
        Canvas canvas;
        float scale = 1.0f;
        if (bitmap.getWidth() > 512 || bitmap.getHeight() > 512) {
            scale = 512.0f / ((float) Math.max(bitmap.getWidth(), bitmap.getHeight()));
            smallBitmap = Bitmap.createScaledBitmap(bitmap, Math.round(((float) bitmap.getWidth()) * scale), Math.round(((float) bitmap.getHeight()) * scale), true);
        } else {
            smallBitmap = bitmap;
        }
        int[] points = findCornerPoints(smallBitmap);
        float pointsScale = 1.0f / scale;
        if (points != null) {
            Point topRight;
            Point topLeft;
            Point bottomRight;
            Point bottomLeft;
            Point point = new Point(points[0], points[1]);
            point = new Point(points[2], points[3]);
            point = new Point(points[4], points[5]);
            point = new Point(points[6], points[7]);
            if (point.x < point.x) {
                topRight = point;
                topLeft = point;
                bottomRight = point;
                bottomLeft = point;
            }
            double topLength = Math.hypot((double) (topRight.x - topLeft.x), (double) (topRight.y - topLeft.y));
            double bottomLength = Math.hypot((double) (bottomRight.x - bottomLeft.x), (double) (bottomRight.y - bottomLeft.y));
            double leftLength = Math.hypot((double) (bottomLeft.x - topLeft.x), (double) (bottomLeft.y - topLeft.y));
            double rightLength = Math.hypot((double) (bottomRight.x - topRight.x), (double) (bottomRight.y - topRight.y));
            double tlRatio = topLength / leftLength;
            double trRatio = topLength / rightLength;
            double blRatio = bottomLength / leftLength;
            double brRatio = bottomLength / rightLength;
            if (tlRatio >= 1.35d && tlRatio <= 1.75d && blRatio >= 1.35d && blRatio <= 1.75d && trRatio >= 1.35d && trRatio <= 1.75d && brRatio >= 1.35d && brRatio <= 1.75d) {
                Bitmap tmp = Bitmap.createBitmap(1024, (int) Math.round(1024.0d / ((((tlRatio + trRatio) + blRatio) + brRatio) / 4.0d)), Config.ARGB_8888);
                canvas = new Canvas(tmp);
                float[] dst = new float[]{0.0f, 0.0f, (float) tmp.getWidth(), 0.0f, (float) tmp.getWidth(), (float) tmp.getHeight(), 0.0f, (float) tmp.getHeight()};
                float[] src = new float[]{((float) topLeft.x) * pointsScale, ((float) topLeft.y) * pointsScale, ((float) topRight.x) * pointsScale, ((float) topRight.y) * pointsScale, ((float) bottomRight.x) * pointsScale, ((float) bottomRight.y) * pointsScale, ((float) bottomLeft.x) * pointsScale, ((float) bottomLeft.y) * pointsScale};
                Matrix perspMatrix = new Matrix();
                perspMatrix.setPolyToPoly(src, 0, dst, 0, src.length >> 1);
                canvas.drawBitmap(bitmap, perspMatrix, new Paint(2));
                bitmap = tmp;
            }
        } else if (bitmap.getWidth() > 1500 || bitmap.getHeight() > 1500) {
            scale = 1500.0f / ((float) Math.max(bitmap.getWidth(), bitmap.getHeight()));
            bitmap = Bitmap.createScaledBitmap(bitmap, Math.round(((float) bitmap.getWidth()) * scale), Math.round(((float) bitmap.getHeight()) * scale), true);
        }
        Bitmap binaryBitmap = null;
        Rect[][] charRects = null;
        int maxLength = 0;
        int lineCount = 0;
        int i = 0;
        while (i < 3) {
            Matrix m = null;
            Bitmap toProcess = bitmap;
            switch (i) {
                case 1:
                    m = new Matrix();
                    m.setRotate(1.0f, (float) (bitmap.getWidth() / 2), (float) (bitmap.getHeight() / 2));
                    break;
                case 2:
                    m = new Matrix();
                    m.setRotate(-1.0f, (float) (bitmap.getWidth() / 2), (float) (bitmap.getHeight() / 2));
                    break;
            }
            if (m != null) {
                toProcess = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
            }
            binaryBitmap = Bitmap.createBitmap(toProcess.getWidth(), toProcess.getHeight(), Config.ALPHA_8);
            charRects = binarizeAndFindCharacters(toProcess, binaryBitmap);
            if (charRects == null) {
                return null;
            }
            for (Rect[] rects : charRects) {
                maxLength = Math.max(rects.length, maxLength);
                if (rects.length > 0) {
                    lineCount++;
                }
            }
            if (lineCount < 2 || maxLength < 30) {
                i++;
            } else if (maxLength >= 30 || lineCount < 2) {
                return null;
            } else {
                Bitmap chrBitmap = Bitmap.createBitmap(charRects[0].length * 10, charRects.length * 15, Config.ALPHA_8);
                canvas = new Canvas(chrBitmap);
                Paint aaPaint = new Paint(2);
                Rect dst2 = new Rect(0, 0, 10, 15);
                int y = 0;
                for (Rect[] line : charRects) {
                    int x = 0;
                    for (Rect rect : charRects[r8]) {
                        dst2.set(x * 10, y * 15, (x * 10) + 10, (y * 15) + 15);
                        canvas.drawBitmap(binaryBitmap, rect, dst2, aaPaint);
                        x++;
                    }
                    y++;
                }
                String mrz = performRecognition(chrBitmap, charRects.length, charRects[0].length, ApplicationLoader.applicationContext.getAssets());
                if (mrz == null) {
                    return null;
                }
                String[] mrzLines = TextUtils.split(mrz, "\n");
                Result result = new Result();
                if (mrzLines.length < 2 || mrzLines[0].length() < 30 || mrzLines[1].length() != mrzLines[0].length()) {
                    return null;
                }
                result.rawMRZ = TextUtils.join("\n", mrzLines);
                HashMap<String, String> countries = getCountriesMap();
                char type = mrzLines[0].charAt(0);
                int lastNameEnd;
                String number;
                String birthDate;
                String expiryDate;
                if (type == 'P') {
                    result.type = 1;
                    if (mrzLines[0].length() == 44) {
                        result.issuingCountry = mrzLines[0].substring(2, 5);
                        lastNameEnd = mrzLines[0].indexOf("<<", 6);
                        if (lastNameEnd != -1) {
                            result.lastName = mrzLines[0].substring(5, lastNameEnd).replace('<', ' ').replace('0', 'O').trim();
                            result.firstName = mrzLines[0].substring(lastNameEnd + 2).replace('<', ' ').replace('0', 'O').trim();
                            if (result.firstName.contains("   ")) {
                                result.firstName = result.firstName.substring(0, result.firstName.indexOf("   "));
                            }
                        }
                        number = mrzLines[1].substring(0, 9).replace('<', ' ').replace('O', '0').trim();
                        if (checksum(number) == getNumber(mrzLines[1].charAt(9))) {
                            result.number = number;
                        }
                        result.nationality = mrzLines[1].substring(10, 13);
                        birthDate = mrzLines[1].substring(13, 19).replace('O', '0').replace('I', '1');
                        if (checksum(birthDate) == getNumber(mrzLines[1].charAt(19))) {
                            parseBirthDate(birthDate, result);
                        }
                        result.gender = parseGender(mrzLines[1].charAt(20));
                        expiryDate = mrzLines[1].substring(21, 27).replace('O', '0').replace('I', '1');
                        if (checksum(expiryDate) == getNumber(mrzLines[1].charAt(27)) || mrzLines[1].charAt(27) == '<') {
                            parseExpiryDate(expiryDate, result);
                        }
                        if ("RUS".equals(result.issuingCountry) && mrzLines[0].charAt(1) == 'N') {
                            result.type = 3;
                            String[] names = result.firstName.split(" ");
                            result.firstName = cyrillicToLatin(russianPassportTranslit(names[0]));
                            if (names.length > 1) {
                                result.middleName = cyrillicToLatin(russianPassportTranslit(names[1]));
                            }
                            result.lastName = cyrillicToLatin(russianPassportTranslit(result.lastName));
                            if (result.number != null) {
                                result.number = result.number.substring(0, 3) + mrzLines[1].charAt(28) + result.number.substring(3);
                            }
                        } else {
                            result.firstName = result.firstName.replace('8', 'B');
                            result.lastName = result.lastName.replace('8', 'B');
                        }
                        result.lastName = capitalize(result.lastName);
                        result.firstName = capitalize(result.firstName);
                        result.middleName = capitalize(result.middleName);
                    }
                } else if (type != 'I' && type != 'A' && type != 'C') {
                    return null;
                } else {
                    result.type = 2;
                    if (mrzLines.length == 3 && mrzLines[0].length() == 30 && mrzLines[2].length() == 30) {
                        result.issuingCountry = mrzLines[0].substring(2, 5);
                        number = mrzLines[0].substring(5, 14).replace('<', ' ').replace('O', '0').trim();
                        if (checksum(number) == mrzLines[0].charAt(14) - 48) {
                            result.number = number;
                        }
                        birthDate = mrzLines[1].substring(0, 6).replace('O', '0').replace('I', '1');
                        if (checksum(birthDate) == getNumber(mrzLines[1].charAt(6))) {
                            parseBirthDate(birthDate, result);
                        }
                        result.gender = parseGender(mrzLines[1].charAt(7));
                        expiryDate = mrzLines[1].substring(8, 14).replace('O', '0').replace('I', '1');
                        if (checksum(expiryDate) == getNumber(mrzLines[1].charAt(14)) || mrzLines[1].charAt(14) == '<') {
                            parseExpiryDate(expiryDate, result);
                        }
                        result.nationality = mrzLines[1].substring(15, 18);
                        lastNameEnd = mrzLines[2].indexOf("<<");
                        if (lastNameEnd != -1) {
                            result.lastName = mrzLines[2].substring(0, lastNameEnd).replace('<', ' ').trim();
                            result.firstName = mrzLines[2].substring(lastNameEnd + 2).replace('<', ' ').trim();
                        }
                    } else if (mrzLines.length == 2 && mrzLines[0].length() == 36) {
                        result.issuingCountry = mrzLines[0].substring(2, 5);
                        if ("FRA".equals(result.issuingCountry) && type == 'I' && mrzLines[0].charAt(1) == 'D') {
                            result.nationality = "FRA";
                            result.lastName = mrzLines[0].substring(5, 30).replace('<', ' ').trim();
                            result.firstName = mrzLines[1].substring(13, 27).replace("<<", ", ").replace('<', ' ').trim();
                            number = mrzLines[1].substring(0, 12).replace('O', '0');
                            if (checksum(number) == getNumber(mrzLines[1].charAt(12))) {
                                result.number = number;
                            }
                            birthDate = mrzLines[1].substring(27, 33).replace('O', '0').replace('I', '1');
                            if (checksum(birthDate) == getNumber(mrzLines[1].charAt(33))) {
                                parseBirthDate(birthDate, result);
                            }
                            result.gender = parseGender(mrzLines[1].charAt(34));
                            result.doesNotExpire = true;
                        } else {
                            lastNameEnd = mrzLines[0].indexOf("<<");
                            if (lastNameEnd != -1) {
                                result.lastName = mrzLines[0].substring(5, lastNameEnd).replace('<', ' ').trim();
                                result.firstName = mrzLines[0].substring(lastNameEnd + 2).replace('<', ' ').trim();
                            }
                            number = mrzLines[1].substring(0, 9).replace('<', ' ').replace('O', '0').trim();
                            if (checksum(number) == getNumber(mrzLines[1].charAt(9))) {
                                result.number = number;
                            }
                            result.nationality = mrzLines[1].substring(10, 13);
                            birthDate = mrzLines[1].substring(13, 19).replace('O', '0').replace('I', '1');
                            if (checksum(birthDate) == getNumber(mrzLines[1].charAt(19))) {
                                parseBirthDate(birthDate, result);
                            }
                            result.gender = parseGender(mrzLines[1].charAt(20));
                            expiryDate = mrzLines[1].substring(21, 27).replace('O', '0').replace('I', '1');
                            if (checksum(expiryDate) == getNumber(mrzLines[1].charAt(27)) || mrzLines[1].charAt(27) == '<') {
                                parseExpiryDate(expiryDate, result);
                            }
                        }
                    }
                    result.firstName = capitalize(result.firstName.replace('0', 'O').replace('8', 'B'));
                    result.lastName = capitalize(result.lastName.replace('0', 'O').replace('8', 'B'));
                }
                if (TextUtils.isEmpty(result.firstName) && TextUtils.isEmpty(result.lastName)) {
                    return null;
                }
                result.issuingCountry = (String) countries.get(result.issuingCountry);
                result.nationality = (String) countries.get(result.nationality);
                return result;
            }
        }
        if (maxLength >= 30) {
        }
        return null;
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
        String latin = "ABVGDE2JZIQKLMNOPRSTUFHCLASSNAMEWXY9678";
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int idx = "ABVGDE2JZIQKLMNOPRSTUFHCLASSNAMEWXY9678".indexOf(chars[i]);
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
