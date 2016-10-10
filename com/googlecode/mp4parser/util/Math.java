package com.googlecode.mp4parser.util;

public class Math
{
  public static int gcd(int paramInt1, int paramInt2)
  {
    for (;;)
    {
      if (paramInt2 <= 0) {
        return paramInt1;
      }
      int i = paramInt1 % paramInt2;
      paramInt1 = paramInt2;
      paramInt2 = i;
    }
  }
  
  public static long gcd(long paramLong1, long paramLong2)
  {
    for (;;)
    {
      if (paramLong2 <= 0L) {
        return paramLong1;
      }
      long l = paramLong1 % paramLong2;
      paramLong1 = paramLong2;
      paramLong2 = l;
    }
  }
  
  public static int lcm(int paramInt1, int paramInt2)
  {
    return paramInt2 / gcd(paramInt1, paramInt2) * paramInt1;
  }
  
  public static long lcm(long paramLong1, long paramLong2)
  {
    return paramLong2 / gcd(paramLong1, paramLong2) * paramLong1;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/util/Math.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */