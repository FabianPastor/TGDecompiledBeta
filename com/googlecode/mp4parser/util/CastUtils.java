package com.googlecode.mp4parser.util;

public class CastUtils
{
  public static int l2i(long paramLong)
  {
    if ((paramLong > 2147483647L) || (paramLong < -2147483648L)) {
      throw new RuntimeException("A cast to int has gone wrong. Please contact the mp4parser discussion group (" + paramLong + ")");
    }
    return (int)paramLong;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/util/CastUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */