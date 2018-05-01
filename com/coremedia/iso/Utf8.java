package com.coremedia.iso;

import java.io.UnsupportedEncodingException;

public final class Utf8
{
  public static String convert(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte != null) {}
    for (;;)
    {
      try
      {
        paramArrayOfByte = new String(paramArrayOfByte, "UTF-8");
        return paramArrayOfByte;
      }
      catch (UnsupportedEncodingException paramArrayOfByte)
      {
        throw new Error(paramArrayOfByte);
      }
      paramArrayOfByte = null;
    }
  }
  
  public static byte[] convert(String paramString)
  {
    if (paramString != null) {}
    for (;;)
    {
      try
      {
        paramString = paramString.getBytes("UTF-8");
        return paramString;
      }
      catch (UnsupportedEncodingException paramString)
      {
        throw new Error(paramString);
      }
      paramString = null;
    }
  }
  
  public static int utf8StringLengthInBytes(String paramString)
  {
    if (paramString != null) {}
    for (;;)
    {
      try
      {
        i = paramString.getBytes("UTF-8").length;
        return i;
      }
      catch (UnsupportedEncodingException paramString)
      {
        int i;
        throw new RuntimeException();
      }
      i = 0;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/Utf8.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */