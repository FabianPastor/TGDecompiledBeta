package com.coremedia.iso;

import java.io.ByteArrayOutputStream;

public class Hex
{
  private static final char[] DIGITS = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };
  
  public static byte[] decodeHex(String paramString)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    for (int i = 0;; i += 2)
    {
      if (i >= paramString.length()) {
        return localByteArrayOutputStream.toByteArray();
      }
      localByteArrayOutputStream.write(Integer.parseInt(paramString.substring(i, i + 2), 16));
    }
  }
  
  public static String encodeHex(byte[] paramArrayOfByte)
  {
    return encodeHex(paramArrayOfByte, 0);
  }
  
  public static String encodeHex(byte[] paramArrayOfByte, int paramInt)
  {
    int i = paramArrayOfByte.length;
    if (paramInt > 0) {}
    char[] arrayOfChar;
    int k;
    for (int j = i / paramInt;; j = 0)
    {
      arrayOfChar = new char[j + (i << 1)];
      k = 0;
      j = 0;
      if (k < i) {
        break;
      }
      return new String(arrayOfChar);
    }
    int m;
    if ((paramInt > 0) && (k % paramInt == 0) && (j > 0))
    {
      m = j + 1;
      arrayOfChar[j] = ((char)45);
      j = m;
    }
    for (;;)
    {
      m = j + 1;
      arrayOfChar[j] = ((char)DIGITS[((paramArrayOfByte[k] & 0xF0) >>> 4)]);
      arrayOfChar[m] = ((char)DIGITS[(paramArrayOfByte[k] & 0xF)]);
      k++;
      j = m + 1;
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/Hex.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */