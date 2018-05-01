package org.aspectj.runtime.internal;

public final class Conversions
{
  public static Object booleanObject(boolean paramBoolean)
  {
    return new Boolean(paramBoolean);
  }
  
  public static Object byteObject(byte paramByte)
  {
    return new Byte(paramByte);
  }
  
  public static Object doubleObject(double paramDouble)
  {
    return new Double(paramDouble);
  }
  
  public static Object floatObject(float paramFloat)
  {
    return new Float(paramFloat);
  }
  
  public static Object intObject(int paramInt)
  {
    return new Integer(paramInt);
  }
  
  public static Object longObject(long paramLong)
  {
    return new Long(paramLong);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/runtime/internal/Conversions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */