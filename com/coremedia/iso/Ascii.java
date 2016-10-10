package com.coremedia.iso;

import java.io.UnsupportedEncodingException;

public final class Ascii
{
  public static String convert(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte != null) {}
    try
    {
      paramArrayOfByte = new String(paramArrayOfByte, "us-ascii");
      return paramArrayOfByte;
    }
    catch (UnsupportedEncodingException paramArrayOfByte)
    {
      throw new Error(paramArrayOfByte);
    }
    return null;
  }
  
  public static byte[] convert(String paramString)
  {
    if (paramString != null) {}
    try
    {
      paramString = paramString.getBytes("us-ascii");
      return paramString;
    }
    catch (UnsupportedEncodingException paramString)
    {
      throw new Error(paramString);
    }
    return null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/Ascii.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */