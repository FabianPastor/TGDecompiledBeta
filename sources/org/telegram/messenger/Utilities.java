package org.telegram.messenger;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {
    public static volatile DispatchQueue globalQueue = new DispatchQueue("globalQueue");
    protected static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static Pattern pattern = Pattern.compile("[\\-0-9]+");
    public static volatile DispatchQueue phoneBookQueue = new DispatchQueue("phoneBookQueue");
    public static SecureRandom random = new SecureRandom();
    public static volatile DispatchQueue searchQueue = new DispatchQueue("searchQueue");
    public static volatile DispatchQueue stageQueue = new DispatchQueue("stageQueue");

    public static native void aesCbcEncryption(ByteBuffer byteBuffer, byte[] bArr, byte[] bArr2, int i, int i2, int i3);

    private static native void aesCbcEncryptionByteArray(byte[] bArr, byte[] bArr2, byte[] bArr3, int i, int i2, int i3, int i4);

    public static native void aesCtrDecryption(ByteBuffer byteBuffer, byte[] bArr, byte[] bArr2, int i, int i2);

    public static native void aesCtrDecryptionByteArray(byte[] bArr, byte[] bArr2, byte[] bArr3, int i, int i2, int i3);

    private static native void aesIgeEncryption(ByteBuffer byteBuffer, byte[] bArr, byte[] bArr2, boolean z, int i, int i2);

    public static native int argon2(int i);

    public static native void blurBitmap(Object obj, int i, int i2, int i3, int i4, int i5);

    public static native void calcCDT(ByteBuffer byteBuffer, int i, int i2, ByteBuffer byteBuffer2);

    public static native void clearDir(String str, int i, long j);

    public static native int convertVideoFrame(ByteBuffer byteBuffer, ByteBuffer byteBuffer2, int i, int i2, int i3, int i4, int i5);

    public static native long getDirSize(String str, int i);

    public static native boolean loadWebpImage(Bitmap bitmap, ByteBuffer byteBuffer, int i, Options options, boolean z);

    public static native int needInvert(Object obj, int i, int i2, int i3, int i4);

    private static native int pbkdf2(byte[] bArr, byte[] bArr2, byte[] bArr3, int i);

    public static native int pinBitmap(Bitmap bitmap);

    public static native String readlink(String str);

    public static native void stackBlurBitmap(Bitmap bitmap, int i);

    public static native void unpinBitmap(Bitmap bitmap);

    static {
        try {
            FileInputStream sUrandomIn = new FileInputStream(new File("/dev/urandom"));
            byte[] buffer = new byte[1024];
            sUrandomIn.read(buffer);
            sUrandomIn.close();
            random.setSeed(buffer);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public static Bitmap blurWallpaper(Bitmap src) {
        if (src == null) {
            return null;
        }
        Bitmap b;
        if (src.getHeight() > src.getWidth()) {
            b = Bitmap.createBitmap(Math.round((((float) src.getWidth()) * 450.0f) / ((float) src.getHeight())), 450, Config.ARGB_8888);
        } else {
            b = Bitmap.createBitmap(450, Math.round((((float) src.getHeight()) * 450.0f) / ((float) src.getWidth())), Config.ARGB_8888);
        }
        Paint paint = new Paint(2);
        new Canvas(b).drawBitmap(src, null, new Rect(0, 0, b.getWidth(), b.getHeight()), paint);
        stackBlurBitmap(b, 12);
        return b;
    }

    public static void aesIgeEncryption(ByteBuffer buffer, byte[] key, byte[] iv, boolean encrypt, boolean changeIv, int offset, int length) {
        aesIgeEncryption(buffer, key, changeIv ? iv : (byte[]) iv.clone(), encrypt, offset, length);
    }

    public static void aesCbcEncryptionByteArraySafe(byte[] buffer, byte[] key, byte[] iv, int offset, int length, int n, int encrypt) {
        aesCbcEncryptionByteArray(buffer, key, (byte[]) iv.clone(), offset, length, n, encrypt);
    }

    public static Integer parseInt(String value) {
        if (value == null) {
            return Integer.valueOf(0);
        }
        Integer val = Integer.valueOf(0);
        try {
            Matcher matcher = pattern.matcher(value);
            if (matcher.find()) {
                return Integer.valueOf(Integer.parseInt(matcher.group(0)));
            }
            return val;
        } catch (Throwable e) {
            FileLog.e(e);
            return val;
        }
    }

    public static Long parseLong(String value) {
        if (value == null) {
            return Long.valueOf(0);
        }
        Long val = Long.valueOf(0);
        try {
            Matcher matcher = pattern.matcher(value);
            if (matcher.find()) {
                return Long.valueOf(Long.parseLong(matcher.group(0)));
            }
            return val;
        } catch (Throwable e) {
            FileLog.e(e);
            return val;
        }
    }

    public static String parseIntToString(String value) {
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    public static String bytesToHex(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        char[] hexChars = new char[(bytes.length * 2)];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 255;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[(j * 2) + 1] = hexArray[v & 15];
        }
        return new String(hexChars);
    }

    public static byte[] hexToBytes(String hex) {
        if (hex == null) {
            return null;
        }
        int len = hex.length();
        byte[] data = new byte[(len / 2)];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    public static boolean isGoodPrime(byte[] prime, int g) {
        boolean z = true;
        if (g < 2 || g > 7 || prime.length != 256 || prime[0] >= (byte) 0) {
            return false;
        }
        BigInteger dhBI = new BigInteger(1, prime);
        int val;
        if (g == 2) {
            if (dhBI.mod(BigInteger.valueOf(8)).intValue() != 7) {
                return false;
            }
        } else if (g == 3) {
            if (dhBI.mod(BigInteger.valueOf(3)).intValue() != 2) {
                return false;
            }
        } else if (g == 5) {
            val = dhBI.mod(BigInteger.valueOf(5)).intValue();
            if (!(val == 1 || val == 4)) {
                return false;
            }
        } else if (g == 6) {
            val = dhBI.mod(BigInteger.valueOf(24)).intValue();
            if (!(val == 19 || val == 23)) {
                return false;
            }
        } else if (g == 7) {
            val = dhBI.mod(BigInteger.valueOf(7)).intValue();
            if (!(val == 3 || val == 5 || val == 6)) {
                return false;
            }
        }
        if (bytesToHex(prime).equals("CLASSNAMECAEB9C6B1CLASSNAMEE6CLASSNAMEvar_var_var_D40238E3E21CLASSNAMED037563D930var_A0AA7CLASSNAMED22530F4DBFA336F6E0ACLASSNAMEAED44CCE7CLASSNAMEFD51var_ACLASSNAMECD4FE6B6B13ABDCLASSNAMEvar_FAF8CLASSNAMEvar_FE96BB2A941D5BCD1D4AC8CCLASSNAMEFA9B378E3C4F3A9060BEE67CF9A4A4A695811051907E162753B56B0F6B410DBA74D8A84B2A14B3144E0Evar_FD17ED950D5965B4B9DD46582DB1178D169C6BCLASSNAMEB0D6FF9CA3928FEF5B9AE4E418FCLASSNAMEE83EBEA0var_FA9FF5EED70050DED2849var_Bvar_D956850CE929851F0D8115var_B105EE2E4E15D04B2454BF6F4FADvar_B10403119CD8E3B92FCC5B")) {
            return true;
        }
        BigInteger dhBI2 = dhBI.subtract(BigInteger.valueOf(1)).divide(BigInteger.valueOf(2));
        if (!(dhBI.isProbablePrime(30) && dhBI2.isProbablePrime(30))) {
            z = false;
        }
        return z;
    }

    public static boolean isGoodGaAndGb(BigInteger g_a, BigInteger p) {
        return g_a.compareTo(BigInteger.valueOf(1)) > 0 && g_a.compareTo(p.subtract(BigInteger.valueOf(1))) < 0;
    }

    public static boolean arraysEquals(byte[] arr1, int offset1, byte[] arr2, int offset2) {
        if (arr1 == null || arr2 == null || offset1 < 0 || offset2 < 0 || arr1.length - offset1 > arr2.length - offset2 || arr1.length - offset1 < 0 || arr2.length - offset2 < 0) {
            return false;
        }
        boolean result = true;
        for (int a = offset1; a < arr1.length; a++) {
            if (arr1[a + offset1] != arr2[a + offset2]) {
                result = false;
            }
        }
        return result;
    }

    public static byte[] computeSHA1(byte[] convertme, int offset, int len) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(convertme, offset, len);
            return md.digest();
        } catch (Throwable e) {
            FileLog.e(e);
            return new byte[20];
        }
    }

    public static byte[] computeSHA1(ByteBuffer convertme, int offset, int len) {
        int oldp = convertme.position();
        int oldl = convertme.limit();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            convertme.position(offset);
            convertme.limit(len);
            md.update(convertme);
            byte[] digest = md.digest();
            return digest;
        } catch (Throwable e) {
            FileLog.e(e);
            return new byte[20];
        } finally {
            convertme.limit(oldl);
            convertme.position(oldp);
        }
    }

    public static byte[] computeSHA1(ByteBuffer convertme) {
        return computeSHA1(convertme, 0, convertme.limit());
    }

    public static byte[] computeSHA1(byte[] convertme) {
        return computeSHA1(convertme, 0, convertme.length);
    }

    public static byte[] computeSHA256(byte[] convertme) {
        return computeSHA256(convertme, 0, convertme.length);
    }

    public static byte[] computeSHA256(byte[] convertme, int offset, int len) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(convertme, offset, len);
            return md.digest();
        } catch (Throwable e) {
            FileLog.e(e);
            return new byte[32];
        }
    }

    public static byte[] computeSHA256(byte[]... args) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            for (int a = 0; a < args.length; a++) {
                md.update(args[a], 0, args[a].length);
            }
            return md.digest();
        } catch (Throwable e) {
            FileLog.e(e);
            return new byte[32];
        }
    }

    public static byte[] computeSHA512(byte[] convertme) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(convertme, 0, convertme.length);
            return md.digest();
        } catch (Throwable e) {
            FileLog.e(e);
            return new byte[64];
        }
    }

    public static byte[] computeSHA512(byte[] convertme, byte[] convertme2) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(convertme, 0, convertme.length);
            md.update(convertme2, 0, convertme2.length);
            return md.digest();
        } catch (Throwable e) {
            FileLog.e(e);
            return new byte[64];
        }
    }

    public static byte[] computePBKDF2(byte[] password, byte[] salt) {
        byte[] dst = new byte[64];
        pbkdf2(password, salt, dst, 100000);
        return dst;
    }

    public static byte[] computeSHA512(byte[] convertme, byte[] convertme2, byte[] convertme3) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(convertme, 0, convertme.length);
            md.update(convertme2, 0, convertme2.length);
            md.update(convertme3, 0, convertme3.length);
            return md.digest();
        } catch (Throwable e) {
            FileLog.e(e);
            return new byte[64];
        }
    }

    public static byte[] computeSHA256(byte[] b1, int o1, int l1, ByteBuffer b2, int o2, int l2) {
        int oldp = b2.position();
        int oldl = b2.limit();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(b1, o1, l1);
            b2.position(o2);
            b2.limit(l2);
            md.update(b2);
            byte[] digest = md.digest();
            return digest;
        } catch (Throwable e) {
            FileLog.e(e);
            return new byte[32];
        } finally {
            b2.limit(oldl);
            b2.position(oldp);
        }
    }

    public static long bytesToLong(byte[] bytes) {
        return (((((((((long) bytes[7]) << 56) + ((((long) bytes[6]) & 255) << 48)) + ((((long) bytes[5]) & 255) << 40)) + ((((long) bytes[4]) & 255) << 32)) + ((((long) bytes[3]) & 255) << 24)) + ((((long) bytes[2]) & 255) << 16)) + ((((long) bytes[1]) & 255) << 8)) + (((long) bytes[0]) & 255);
    }

    public static int bytesToInt(byte[] bytes) {
        return ((((bytes[3] & 255) << 24) + ((bytes[2] & 255) << 16)) + ((bytes[1] & 255) << 8)) + (bytes[0] & 255);
    }

    public static String MD5(String md5) {
        if (md5 == null) {
            return null;
        }
        try {
            byte[] array = MessageDigest.getInstance("MD5").digest(AndroidUtilities.getStringBytes(md5));
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(Integer.toHexString((b & 255) | 256).substring(1, 3));
            }
            return sb.toString();
        } catch (Throwable e) {
            FileLog.e(e);
            return null;
        }
    }
}
