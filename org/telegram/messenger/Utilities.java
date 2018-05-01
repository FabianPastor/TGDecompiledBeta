package org.telegram.messenger;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities
{
  public static volatile DispatchQueue globalQueue;
  protected static final char[] hexArray = "0123456789ABCDEF".toCharArray();
  public static Pattern pattern = Pattern.compile("[\\-0-9]+");
  public static volatile DispatchQueue phoneBookQueue;
  public static SecureRandom random = new SecureRandom();
  public static volatile DispatchQueue searchQueue;
  public static volatile DispatchQueue stageQueue = new DispatchQueue("stageQueue");
  
  static
  {
    globalQueue = new DispatchQueue("globalQueue");
    searchQueue = new DispatchQueue("searchQueue");
    phoneBookQueue = new DispatchQueue("photoBookQueue");
    try
    {
      Object localObject = new java/io/File;
      ((File)localObject).<init>("/dev/urandom");
      FileInputStream localFileInputStream = new java/io/FileInputStream;
      localFileInputStream.<init>((File)localObject);
      localObject = new byte['Ѐ'];
      localFileInputStream.read((byte[])localObject);
      localFileInputStream.close();
      random.setSeed((byte[])localObject);
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  public static String MD5(String paramString)
  {
    Object localObject = null;
    if (paramString == null) {
      paramString = (String)localObject;
    }
    for (;;)
    {
      return paramString;
      try
      {
        byte[] arrayOfByte = MessageDigest.getInstance("MD5").digest(paramString.getBytes());
        paramString = new java/lang/StringBuilder;
        paramString.<init>();
        for (int i = 0; i < arrayOfByte.length; i++) {
          paramString.append(Integer.toHexString(arrayOfByte[i] & 0xFF | 0x100).substring(1, 3));
        }
        paramString = paramString.toString();
      }
      catch (NoSuchAlgorithmException paramString)
      {
        FileLog.e(paramString);
        paramString = (String)localObject;
      }
    }
  }
  
  public static native void aesCtrDecryption(ByteBuffer paramByteBuffer, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2);
  
  public static native void aesCtrDecryptionByteArray(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt1, int paramInt2, int paramInt3);
  
  private static native void aesIgeEncryption(ByteBuffer paramByteBuffer, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, boolean paramBoolean, int paramInt1, int paramInt2);
  
  public static void aesIgeEncryption(ByteBuffer paramByteBuffer, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2)
  {
    if (paramBoolean2) {}
    for (;;)
    {
      aesIgeEncryption(paramByteBuffer, paramArrayOfByte1, paramArrayOfByte2, paramBoolean1, paramInt1, paramInt2);
      return;
      paramArrayOfByte2 = (byte[])paramArrayOfByte2.clone();
    }
  }
  
  public static boolean arraysEquals(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2)
  {
    boolean bool1;
    if ((paramArrayOfByte1 == null) || (paramArrayOfByte2 == null) || (paramInt1 < 0) || (paramInt2 < 0) || (paramArrayOfByte1.length - paramInt1 > paramArrayOfByte2.length - paramInt2) || (paramArrayOfByte1.length - paramInt1 < 0) || (paramArrayOfByte2.length - paramInt2 < 0))
    {
      bool1 = false;
      return bool1;
    }
    boolean bool2 = true;
    for (int i = paramInt1;; i++)
    {
      bool1 = bool2;
      if (i >= paramArrayOfByte1.length) {
        break;
      }
      if (paramArrayOfByte1[(i + paramInt1)] != paramArrayOfByte2[(i + paramInt2)]) {
        bool2 = false;
      }
    }
  }
  
  public static native void blurBitmap(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  public static String bytesToHex(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {}
    char[] arrayOfChar;
    for (paramArrayOfByte = "";; paramArrayOfByte = new String(arrayOfChar))
    {
      return paramArrayOfByte;
      arrayOfChar = new char[paramArrayOfByte.length * 2];
      for (int i = 0; i < paramArrayOfByte.length; i++)
      {
        int j = paramArrayOfByte[i] & 0xFF;
        arrayOfChar[(i * 2)] = ((char)hexArray[(j >>> 4)]);
        arrayOfChar[(i * 2 + 1)] = ((char)hexArray[(j & 0xF)]);
      }
    }
  }
  
  public static long bytesToLong(byte[] paramArrayOfByte)
  {
    return (paramArrayOfByte[7] << 56) + ((paramArrayOfByte[6] & 0xFF) << 48) + ((paramArrayOfByte[5] & 0xFF) << 40) + ((paramArrayOfByte[4] & 0xFF) << 32) + ((paramArrayOfByte[3] & 0xFF) << 24) + ((paramArrayOfByte[2] & 0xFF) << 16) + ((paramArrayOfByte[1] & 0xFF) << 8) + (paramArrayOfByte[0] & 0xFF);
  }
  
  public static native void calcCDT(ByteBuffer paramByteBuffer1, int paramInt1, int paramInt2, ByteBuffer paramByteBuffer2);
  
  public static native void clearDir(String paramString, int paramInt, long paramLong);
  
  public static byte[] computeSHA1(ByteBuffer paramByteBuffer)
  {
    return computeSHA1(paramByteBuffer, 0, paramByteBuffer.limit());
  }
  
  public static byte[] computeSHA1(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2)
  {
    i = paramByteBuffer.position();
    j = paramByteBuffer.limit();
    try
    {
      localObject1 = MessageDigest.getInstance("SHA-1");
      paramByteBuffer.position(paramInt1);
      paramByteBuffer.limit(paramInt2);
      ((MessageDigest)localObject1).update(paramByteBuffer);
      localObject1 = ((MessageDigest)localObject1).digest();
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Object localObject1;
        FileLog.e(localException);
        paramByteBuffer.limit(j);
        paramByteBuffer.position(i);
        byte[] arrayOfByte = new byte[20];
      }
    }
    finally
    {
      paramByteBuffer.limit(j);
      paramByteBuffer.position(i);
    }
    return (byte[])localObject1;
  }
  
  public static byte[] computeSHA1(byte[] paramArrayOfByte)
  {
    return computeSHA1(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public static byte[] computeSHA1(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    try
    {
      MessageDigest localMessageDigest = MessageDigest.getInstance("SHA-1");
      localMessageDigest.update(paramArrayOfByte, paramInt1, paramInt2);
      paramArrayOfByte = localMessageDigest.digest();
      return paramArrayOfByte;
    }
    catch (Exception paramArrayOfByte)
    {
      for (;;)
      {
        FileLog.e(paramArrayOfByte);
        paramArrayOfByte = new byte[20];
      }
    }
  }
  
  public static byte[] computeSHA256(byte[] paramArrayOfByte)
  {
    return computeSHA256(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public static byte[] computeSHA256(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    try
    {
      MessageDigest localMessageDigest = MessageDigest.getInstance("SHA-256");
      localMessageDigest.update(paramArrayOfByte, paramInt1, paramInt2);
      paramArrayOfByte = localMessageDigest.digest();
      return paramArrayOfByte;
    }
    catch (Exception paramArrayOfByte)
    {
      for (;;)
      {
        FileLog.e(paramArrayOfByte);
        paramArrayOfByte = new byte[32];
      }
    }
  }
  
  public static byte[] computeSHA256(byte[] paramArrayOfByte, int paramInt1, int paramInt2, ByteBuffer paramByteBuffer, int paramInt3, int paramInt4)
  {
    i = paramByteBuffer.position();
    j = paramByteBuffer.limit();
    try
    {
      MessageDigest localMessageDigest = MessageDigest.getInstance("SHA-256");
      localMessageDigest.update(paramArrayOfByte, paramInt1, paramInt2);
      paramByteBuffer.position(paramInt3);
      paramByteBuffer.limit(paramInt4);
      localMessageDigest.update(paramByteBuffer);
      paramArrayOfByte = localMessageDigest.digest();
    }
    catch (Exception paramArrayOfByte)
    {
      for (;;)
      {
        FileLog.e(paramArrayOfByte);
        paramByteBuffer.limit(j);
        paramByteBuffer.position(i);
        paramArrayOfByte = new byte[32];
      }
    }
    finally
    {
      paramByteBuffer.limit(j);
      paramByteBuffer.position(i);
    }
    return paramArrayOfByte;
  }
  
  public static native int convertVideoFrame(ByteBuffer paramByteBuffer1, ByteBuffer paramByteBuffer2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  public static native long getDirSize(String paramString, int paramInt);
  
  public static byte[] hexToBytes(String paramString)
  {
    Object localObject;
    if (paramString == null)
    {
      localObject = null;
      return (byte[])localObject;
    }
    int i = paramString.length();
    byte[] arrayOfByte = new byte[i / 2];
    for (int j = 0;; j += 2)
    {
      localObject = arrayOfByte;
      if (j >= i) {
        break;
      }
      arrayOfByte[(j / 2)] = ((byte)(byte)((Character.digit(paramString.charAt(j), 16) << 4) + Character.digit(paramString.charAt(j + 1), 16)));
    }
  }
  
  public static boolean isGoodGaAndGb(BigInteger paramBigInteger1, BigInteger paramBigInteger2)
  {
    boolean bool = true;
    if ((paramBigInteger1.compareTo(BigInteger.valueOf(1L)) == 1) && (paramBigInteger1.compareTo(paramBigInteger2.subtract(BigInteger.valueOf(1L))) == -1)) {}
    for (;;)
    {
      return bool;
      bool = false;
    }
  }
  
  public static boolean isGoodPrime(byte[] paramArrayOfByte, int paramInt)
  {
    boolean bool1 = true;
    boolean bool2 = false;
    boolean bool3 = bool2;
    if (paramInt >= 2)
    {
      if (paramInt <= 7) {
        break label24;
      }
      bool3 = bool2;
    }
    label24:
    BigInteger localBigInteger;
    for (;;)
    {
      return bool3;
      bool3 = bool2;
      if (paramArrayOfByte.length == 256)
      {
        bool3 = bool2;
        if (paramArrayOfByte[0] < 0)
        {
          localBigInteger = new BigInteger(1, paramArrayOfByte);
          if (paramInt == 2)
          {
            bool3 = bool2;
            if (localBigInteger.mod(BigInteger.valueOf(8L)).intValue() != 7) {}
          }
          else
          {
            label130:
            label166:
            label205:
            do
            {
              do
              {
                do
                {
                  do
                  {
                    do
                    {
                      if (!bytesToHex(paramArrayOfByte).equals("C71CAEB9C6B1C9048E6C522F70F13F73980D40238E3E21C14934D037563D930F48198A0AA7C14058229493D22530F4DBFA336F6E0AC925139543AED44CCE7C3720FD51F69458705AC68CD4FE6B6B13ABDC9746512969328454F18FAF8C595F642477FE96BB2A941D5BCD1D4AC8CC49880708FA9B378E3C4F3A9060BEE67CF9A4A4A695811051907E162753B56B0F6B410DBA74D8A84B2A14B3144E0EF1284754FD17ED950D5965B4B9DD46582DB1178D169C6BC465B0D6FF9CA3928FEF5B9AE4E418FC15E83EBEA0F87FA9FF5EED70050DED2849F47BF959D956850CE929851F0D8115F635B105EE2E4E15D04B2454BF6F4FADF034B10403119CD8E3B92FCC5B")) {
                        break label248;
                      }
                      bool3 = true;
                      break;
                      if (paramInt != 3) {
                        break label130;
                      }
                    } while (localBigInteger.mod(BigInteger.valueOf(3L)).intValue() == 2);
                    bool3 = bool2;
                    break;
                    if (paramInt != 5) {
                      break label166;
                    }
                    paramInt = localBigInteger.mod(BigInteger.valueOf(5L)).intValue();
                  } while ((paramInt == 1) || (paramInt == 4));
                  bool3 = bool2;
                  break;
                  if (paramInt != 6) {
                    break label205;
                  }
                  paramInt = localBigInteger.mod(BigInteger.valueOf(24L)).intValue();
                } while ((paramInt == 19) || (paramInt == 23));
                bool3 = bool2;
                break;
              } while (paramInt != 7);
              paramInt = localBigInteger.mod(BigInteger.valueOf(7L)).intValue();
            } while ((paramInt == 3) || (paramInt == 5) || (paramInt == 6));
            bool3 = bool2;
          }
        }
      }
    }
    label248:
    paramArrayOfByte = localBigInteger.subtract(BigInteger.valueOf(1L)).divide(BigInteger.valueOf(2L));
    if ((localBigInteger.isProbablePrime(30)) && (paramArrayOfByte.isProbablePrime(30))) {}
    for (bool3 = bool1;; bool3 = false) {
      break;
    }
  }
  
  public static native boolean loadWebpImage(Bitmap paramBitmap, ByteBuffer paramByteBuffer, int paramInt, BitmapFactory.Options paramOptions, boolean paramBoolean);
  
  public static Integer parseInt(String paramString)
  {
    if (paramString == null) {
      paramString = Integer.valueOf(0);
    }
    for (;;)
    {
      return paramString;
      Integer localInteger = Integer.valueOf(0);
      try
      {
        Matcher localMatcher = pattern.matcher(paramString);
        paramString = localInteger;
        if (localMatcher.find())
        {
          int i = Integer.parseInt(localMatcher.group(0));
          paramString = Integer.valueOf(i);
        }
      }
      catch (Exception paramString)
      {
        FileLog.e(paramString);
        paramString = localInteger;
      }
    }
  }
  
  public static String parseIntToString(String paramString)
  {
    paramString = pattern.matcher(paramString);
    if (paramString.find()) {}
    for (paramString = paramString.group(0);; paramString = null) {
      return paramString;
    }
  }
  
  public static Long parseLong(String paramString)
  {
    if (paramString == null) {
      paramString = Long.valueOf(0L);
    }
    for (;;)
    {
      return paramString;
      Long localLong = Long.valueOf(0L);
      try
      {
        Matcher localMatcher = pattern.matcher(paramString);
        paramString = localLong;
        if (localMatcher.find())
        {
          long l = Long.parseLong(localMatcher.group(0));
          paramString = Long.valueOf(l);
        }
      }
      catch (Exception paramString)
      {
        FileLog.e(paramString);
        paramString = localLong;
      }
    }
  }
  
  public static native int pinBitmap(Bitmap paramBitmap);
  
  public static native String readlink(String paramString);
  
  public static native void unpinBitmap(Bitmap paramBitmap);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/Utilities.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */