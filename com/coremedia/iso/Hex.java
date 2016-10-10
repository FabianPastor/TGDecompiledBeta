package com.coremedia.iso;

import java.io.ByteArrayOutputStream;

public class Hex
{
  private static final char[] DIGITS = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };
  
  public static byte[] decodeHex(String paramString)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    int i = 0;
    for (;;)
    {
      if (i >= paramString.length()) {
        return localByteArrayOutputStream.toByteArray();
      }
      localByteArrayOutputStream.write(Integer.parseInt(paramString.substring(i, i + 2), 16));
      i += 2;
    }
  }
  
  public static String encodeHex(byte[] paramArrayOfByte)
  {
    return encodeHex(paramArrayOfByte, 0);
  }
  
  public static String encodeHex(byte[] paramArrayOfByte, int paramInt)
  {
    int m = paramArrayOfByte.length;
    if (paramInt > 0) {}
    char[] arrayOfChar;
    int j;
    for (int i = m / paramInt;; i = 0)
    {
      arrayOfChar = new char[i + (m << 1)];
      j = 0;
      i = 0;
      if (j < m) {
        break;
      }
      return new String(arrayOfChar);
    }
    int k;
    if ((paramInt > 0) && (j % paramInt == 0) && (i > 0))
    {
      k = i + 1;
      arrayOfChar[i] = '-';
      i = k;
    }
    for (;;)
    {
      k = i + 1;
      arrayOfChar[i] = DIGITS[((paramArrayOfByte[j] & 0xF0) >>> 4)];
      arrayOfChar[k] = DIGITS[(paramArrayOfByte[j] & 0xF)];
      j += 1;
      i = k + 1;
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/Hex.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */