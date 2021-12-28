package org.telegram.messenger;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import com.carrotsearch.randomizedtesting.Xoroshiro128PlusRandom;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {
    public static volatile DispatchQueue cacheClearQueue = new DispatchQueue("cacheClearQueue");
    public static Random fastRandom = new Xoroshiro128PlusRandom(random.nextLong());
    public static volatile DispatchQueue globalQueue = new DispatchQueue("globalQueue");
    protected static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static Pattern pattern = Pattern.compile("[\\-0-9]+");
    public static volatile DispatchQueue phoneBookQueue = new DispatchQueue("phoneBookQueue");
    public static SecureRandom random = new SecureRandom();
    public static volatile DispatchQueue searchQueue = new DispatchQueue("searchQueue");
    public static volatile DispatchQueue stageQueue = new DispatchQueue("stageQueue");
    public static volatile DispatchQueue themeQueue = new DispatchQueue("themeQueue");

    public static native void aesCbcEncryption(ByteBuffer byteBuffer, byte[] bArr, byte[] bArr2, int i, int i2, int i3);

    private static native void aesCbcEncryptionByteArray(byte[] bArr, byte[] bArr2, byte[] bArr3, int i, int i2, int i3, int i4);

    public static native void aesCtrDecryption(ByteBuffer byteBuffer, byte[] bArr, byte[] bArr2, int i, int i2);

    public static native void aesCtrDecryptionByteArray(byte[] bArr, byte[] bArr2, byte[] bArr3, int i, int i2, int i3);

    private static native void aesIgeEncryption(ByteBuffer byteBuffer, byte[] bArr, byte[] bArr2, boolean z, int i, int i2);

    private static native void aesIgeEncryptionByteArray(byte[] bArr, byte[] bArr2, byte[] bArr3, boolean z, int i, int i2);

    public static native void blurBitmap(Object obj, int i, int i2, int i3, int i4, int i5);

    public static native void calcCDT(ByteBuffer byteBuffer, int i, int i2, ByteBuffer byteBuffer2, ByteBuffer byteBuffer3);

    public static native void clearDir(String str, int i, long j, boolean z);

    public static native int convertVideoFrame(ByteBuffer byteBuffer, ByteBuffer byteBuffer2, int i, int i2, int i3, int i4, int i5);

    public static native void drawDitheredGradient(Bitmap bitmap, int[] iArr, int i, int i2, int i3, int i4);

    public static native void generateGradient(Bitmap bitmap, boolean z, int i, float f, int i2, int i3, int i4, int[] iArr);

    public static native long getDirSize(String str, int i, boolean z);

    public static byte[] intToBytes(int i) {
        return new byte[]{(byte) (i >>> 24), (byte) (i >>> 16), (byte) (i >>> 8), (byte) i};
    }

    public static native boolean loadWebpImage(Bitmap bitmap, ByteBuffer byteBuffer, int i, BitmapFactory.Options options, boolean z);

    public static native int needInvert(Object obj, int i, int i2, int i3, int i4);

    private static native int pbkdf2(byte[] bArr, byte[] bArr2, byte[] bArr3, int i);

    public static native int pinBitmap(Bitmap bitmap);

    public static native String readlink(String str);

    public static native String readlinkFd(int i);

    public static native int saveProgressiveJpeg(Bitmap bitmap, int i, int i2, int i3, int i4, String str);

    public static native void stackBlurBitmap(Bitmap bitmap, int i);

    public static native void unpinBitmap(Bitmap bitmap);

    static {
        try {
            FileInputStream fileInputStream = new FileInputStream(new File("/dev/urandom"));
            byte[] bArr = new byte[1024];
            fileInputStream.read(bArr);
            fileInputStream.close();
            random.setSeed(bArr);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static Bitmap blurWallpaper(Bitmap bitmap) {
        Bitmap bitmap2;
        if (bitmap == null) {
            return null;
        }
        if (bitmap.getHeight() > bitmap.getWidth()) {
            bitmap2 = Bitmap.createBitmap(Math.round((((float) bitmap.getWidth()) * 450.0f) / ((float) bitmap.getHeight())), 450, Bitmap.Config.ARGB_8888);
        } else {
            bitmap2 = Bitmap.createBitmap(450, Math.round((((float) bitmap.getHeight()) * 450.0f) / ((float) bitmap.getWidth())), Bitmap.Config.ARGB_8888);
        }
        Paint paint = new Paint(2);
        new Canvas(bitmap2).drawBitmap(bitmap, (Rect) null, new Rect(0, 0, bitmap2.getWidth(), bitmap2.getHeight()), paint);
        stackBlurBitmap(bitmap2, 12);
        return bitmap2;
    }

    public static void aesIgeEncryption(ByteBuffer byteBuffer, byte[] bArr, byte[] bArr2, boolean z, boolean z2, int i, int i2) {
        if (!z2) {
            bArr2 = (byte[]) bArr2.clone();
        }
        aesIgeEncryption(byteBuffer, bArr, bArr2, z, i, i2);
    }

    public static void aesIgeEncryptionByteArray(byte[] bArr, byte[] bArr2, byte[] bArr3, boolean z, boolean z2, int i, int i2) {
        if (!z2) {
            bArr3 = (byte[]) bArr3.clone();
        }
        aesIgeEncryptionByteArray(bArr, bArr2, bArr3, z, i, i2);
    }

    public static void aesCbcEncryptionByteArraySafe(byte[] bArr, byte[] bArr2, byte[] bArr3, int i, int i2, int i3, int i4) {
        aesCbcEncryptionByteArray(bArr, bArr2, (byte[]) bArr3.clone(), i, i2, i3, i4);
    }

    public static Integer parseInt(CharSequence charSequence) {
        int i = 0;
        if (charSequence == null) {
            return 0;
        }
        try {
            Matcher matcher = pattern.matcher(charSequence);
            if (matcher.find()) {
                i = Integer.parseInt(matcher.group(0));
            }
        } catch (Exception unused) {
        }
        return Integer.valueOf(i);
    }

    public static Long parseLong(String str) {
        long j = 0;
        if (str == null) {
            return 0L;
        }
        try {
            Matcher matcher = pattern.matcher(str);
            if (matcher.find()) {
                j = Long.parseLong(matcher.group(0));
            }
        } catch (Exception unused) {
        }
        return Long.valueOf(j);
    }

    public static String parseIntToString(String str) {
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    public static String bytesToHex(byte[] bArr) {
        if (bArr == null) {
            return "";
        }
        char[] cArr = new char[(bArr.length * 2)];
        for (int i = 0; i < bArr.length; i++) {
            byte b = bArr[i] & 255;
            int i2 = i * 2;
            char[] cArr2 = hexArray;
            cArr[i2] = cArr2[b >>> 4];
            cArr[i2 + 1] = cArr2[b & 15];
        }
        return new String(cArr);
    }

    public static byte[] hexToBytes(String str) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        byte[] bArr = new byte[(length / 2)];
        for (int i = 0; i < length; i += 2) {
            bArr[i / 2] = (byte) ((Character.digit(str.charAt(i), 16) << 4) + Character.digit(str.charAt(i + 1), 16));
        }
        return bArr;
    }

    public static boolean isGoodPrime(byte[] bArr, int i) {
        int intValue;
        if (i < 2 || i > 7 || bArr.length != 256 || bArr[0] >= 0) {
            return false;
        }
        BigInteger bigInteger = new BigInteger(1, bArr);
        if (i == 2) {
            if (bigInteger.mod(BigInteger.valueOf(8)).intValue() != 7) {
                return false;
            }
        } else if (i == 3) {
            if (bigInteger.mod(BigInteger.valueOf(3)).intValue() != 2) {
                return false;
            }
        } else if (i == 5) {
            int intValue2 = bigInteger.mod(BigInteger.valueOf(5)).intValue();
            if (!(intValue2 == 1 || intValue2 == 4)) {
                return false;
            }
        } else if (i == 6) {
            int intValue3 = bigInteger.mod(BigInteger.valueOf(24)).intValue();
            if (!(intValue3 == 19 || intValue3 == 23)) {
                return false;
            }
        } else if (!(i != 7 || (intValue = bigInteger.mod(BigInteger.valueOf(7)).intValue()) == 3 || intValue == 5 || intValue == 6)) {
            return false;
        }
        if (bytesToHex(bArr).equals("CLASSNAMECAEB9C6B1CLASSNAMEE6CLASSNAMEvar_var_var_D40238E3E21CLASSNAMED037563D930var_A0AA7CLASSNAMED22530F4DBFA336F6E0ACLASSNAMEAED44CCE7CLASSNAMEFD51var_ACLASSNAMECD4FE6B6B13ABDCLASSNAMEvar_FAF8CLASSNAMEvar_FE96BB2A941D5BCD1D4AC8CCLASSNAMEFA9B378E3C4F3A9060BEE67CF9A4A4A695811051907E162753B56B0F6B410DBA74D8A84B2A14B3144E0Evar_FD17ED950D5965B4B9DD46582DB1178D169C6BCLASSNAMEB0D6FF9CA3928FEF5B9AE4E418FCLASSNAMEE83EBEA0var_FA9FF5EED70050DED2849var_Bvar_D956850CE929851F0D8115var_B105EE2E4E15D04B2454BF6F4FADvar_B10403119CD8E3B92FCC5B")) {
            return true;
        }
        BigInteger divide = bigInteger.subtract(BigInteger.valueOf(1)).divide(BigInteger.valueOf(2));
        if (!bigInteger.isProbablePrime(30) || !divide.isProbablePrime(30)) {
            return false;
        }
        return true;
    }

    public static boolean isGoodGaAndGb(BigInteger bigInteger, BigInteger bigInteger2) {
        return bigInteger.compareTo(BigInteger.valueOf(1)) > 0 && bigInteger.compareTo(bigInteger2.subtract(BigInteger.valueOf(1))) < 0;
    }

    public static boolean arraysEquals(byte[] bArr, int i, byte[] bArr2, int i2) {
        if (bArr == null || bArr2 == null || i < 0 || i2 < 0 || bArr.length - i > bArr2.length - i2 || bArr.length - i < 0 || bArr2.length - i2 < 0) {
            return false;
        }
        boolean z = true;
        for (int i3 = i; i3 < bArr.length; i3++) {
            if (bArr[i3 + i] != bArr2[i3 + i2]) {
                z = false;
            }
        }
        return z;
    }

    public static byte[] computeSHA1(byte[] bArr, int i, int i2) {
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-1");
            instance.update(bArr, i, i2);
            return instance.digest();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return new byte[20];
        }
    }

    /* JADX INFO: finally extract failed */
    public static byte[] computeSHA1(ByteBuffer byteBuffer, int i, int i2) {
        int position = byteBuffer.position();
        int limit = byteBuffer.limit();
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-1");
            byteBuffer.position(i);
            byteBuffer.limit(i2);
            instance.update(byteBuffer);
            byte[] digest = instance.digest();
            byteBuffer.limit(limit);
            byteBuffer.position(position);
            return digest;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            byteBuffer.limit(limit);
            byteBuffer.position(position);
            return new byte[20];
        } catch (Throwable th) {
            byteBuffer.limit(limit);
            byteBuffer.position(position);
            throw th;
        }
    }

    public static byte[] computeSHA1(ByteBuffer byteBuffer) {
        return computeSHA1(byteBuffer, 0, byteBuffer.limit());
    }

    public static byte[] computeSHA1(byte[] bArr) {
        return computeSHA1(bArr, 0, bArr.length);
    }

    public static byte[] computeSHA256(byte[] bArr) {
        return computeSHA256(bArr, 0, bArr.length);
    }

    public static byte[] computeSHA256(byte[] bArr, int i, int i2) {
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-256");
            instance.update(bArr, i, i2);
            return instance.digest();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return new byte[32];
        }
    }

    public static byte[] computeSHA256(byte[]... bArr) {
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-256");
            for (int i = 0; i < bArr.length; i++) {
                instance.update(bArr[i], 0, bArr[i].length);
            }
            return instance.digest();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return new byte[32];
        }
    }

    public static byte[] computeSHA512(byte[] bArr) {
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-512");
            instance.update(bArr, 0, bArr.length);
            return instance.digest();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return new byte[64];
        }
    }

    public static byte[] computeSHA512(byte[] bArr, byte[] bArr2) {
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-512");
            instance.update(bArr, 0, bArr.length);
            instance.update(bArr2, 0, bArr2.length);
            return instance.digest();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return new byte[64];
        }
    }

    public static byte[] computePBKDF2(byte[] bArr, byte[] bArr2) {
        byte[] bArr3 = new byte[64];
        pbkdf2(bArr, bArr2, bArr3, 100000);
        return bArr3;
    }

    public static byte[] computeSHA512(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-512");
            instance.update(bArr, 0, bArr.length);
            instance.update(bArr2, 0, bArr2.length);
            instance.update(bArr3, 0, bArr3.length);
            return instance.digest();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return new byte[64];
        }
    }

    /* JADX INFO: finally extract failed */
    public static byte[] computeSHA256(byte[] bArr, int i, int i2, ByteBuffer byteBuffer, int i3, int i4) {
        int position = byteBuffer.position();
        int limit = byteBuffer.limit();
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-256");
            instance.update(bArr, i, i2);
            byteBuffer.position(i3);
            byteBuffer.limit(i4);
            instance.update(byteBuffer);
            byte[] digest = instance.digest();
            byteBuffer.limit(limit);
            byteBuffer.position(position);
            return digest;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            byteBuffer.limit(limit);
            byteBuffer.position(position);
            return new byte[32];
        } catch (Throwable th) {
            byteBuffer.limit(limit);
            byteBuffer.position(position);
            throw th;
        }
    }

    public static long bytesToLong(byte[] bArr) {
        return (((long) bArr[7]) << 56) + ((((long) bArr[6]) & 255) << 48) + ((((long) bArr[5]) & 255) << 40) + ((((long) bArr[4]) & 255) << 32) + ((((long) bArr[3]) & 255) << 24) + ((((long) bArr[2]) & 255) << 16) + ((((long) bArr[1]) & 255) << 8) + (((long) bArr[0]) & 255);
    }

    public static int bytesToInt(byte[] bArr) {
        return ((bArr[3] & 255) << 24) + ((bArr[2] & 255) << 16) + ((bArr[1] & 255) << 8) + (bArr[0] & 255);
    }

    public static String MD5(String str) {
        if (str == null) {
            return null;
        }
        try {
            byte[] digest = MessageDigest.getInstance("MD5").digest(AndroidUtilities.getStringBytes(str));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(Integer.toHexString((b & 255) | 256).substring(1, 3));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }
}
