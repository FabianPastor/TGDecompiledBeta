package com.googlecode.mp4parser.h264;

import java.io.PrintStream;
import java.nio.ShortBuffer;

public class Debug
{
  public static final boolean debug = false;
  
  public static void print(int paramInt) {}
  
  public static void print(String paramString) {}
  
  public static void print(short[] paramArrayOfShort)
  {
    int j = 0;
    int i = 0;
    if (i >= 8) {
      return;
    }
    int k = 0;
    for (;;)
    {
      if (k >= 8)
      {
        System.out.println();
        i += 1;
        break;
      }
      System.out.printf("%3d, ", new Object[] { Short.valueOf(paramArrayOfShort[j]) });
      j += 1;
      k += 1;
    }
  }
  
  public static final void print8x8(ShortBuffer paramShortBuffer)
  {
    int i = 0;
    if (i >= 8) {
      return;
    }
    int j = 0;
    for (;;)
    {
      if (j >= 8)
      {
        System.out.println();
        i += 1;
        break;
      }
      System.out.printf("%3d, ", new Object[] { Short.valueOf(paramShortBuffer.get()) });
      j += 1;
    }
  }
  
  public static final void print8x8(int[] paramArrayOfInt)
  {
    int j = 0;
    int i = 0;
    if (i >= 8) {
      return;
    }
    int k = 0;
    for (;;)
    {
      if (k >= 8)
      {
        System.out.println();
        i += 1;
        break;
      }
      System.out.printf("%3d, ", new Object[] { Integer.valueOf(paramArrayOfInt[j]) });
      j += 1;
      k += 1;
    }
  }
  
  public static final void print8x8(short[] paramArrayOfShort)
  {
    int j = 0;
    int i = 0;
    if (i >= 8) {
      return;
    }
    int k = 0;
    for (;;)
    {
      if (k >= 8)
      {
        System.out.println();
        i += 1;
        break;
      }
      System.out.printf("%3d, ", new Object[] { Short.valueOf(paramArrayOfShort[j]) });
      j += 1;
      k += 1;
    }
  }
  
  public static void println(String paramString) {}
  
  public static void trace(String paramString, Object... paramVarArgs) {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/h264/Debug.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */