package org.aspectj.runtime.internal;

public final class Conversions
{
  public static Object booleanObject(boolean paramBoolean)
  {
    return new Boolean(paramBoolean);
  }
  
  public static boolean booleanValue(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if ((paramObject instanceof Boolean)) {
      return ((Boolean)paramObject).booleanValue();
    }
    throw new ClassCastException(paramObject.getClass().getName() + " can not be converted to boolean");
  }
  
  public static Object byteObject(byte paramByte)
  {
    return new Byte(paramByte);
  }
  
  public static byte byteValue(Object paramObject)
  {
    if (paramObject == null) {
      return 0;
    }
    if ((paramObject instanceof Number)) {
      return ((Number)paramObject).byteValue();
    }
    throw new ClassCastException(paramObject.getClass().getName() + " can not be converted to byte");
  }
  
  public static Object charObject(char paramChar)
  {
    return new Character(paramChar);
  }
  
  public static char charValue(Object paramObject)
  {
    if (paramObject == null) {
      return '\000';
    }
    if ((paramObject instanceof Character)) {
      return ((Character)paramObject).charValue();
    }
    throw new ClassCastException(paramObject.getClass().getName() + " can not be converted to char");
  }
  
  public static Object doubleObject(double paramDouble)
  {
    return new Double(paramDouble);
  }
  
  public static double doubleValue(Object paramObject)
  {
    if (paramObject == null) {
      return 0.0D;
    }
    if ((paramObject instanceof Number)) {
      return ((Number)paramObject).doubleValue();
    }
    throw new ClassCastException(paramObject.getClass().getName() + " can not be converted to double");
  }
  
  public static Object floatObject(float paramFloat)
  {
    return new Float(paramFloat);
  }
  
  public static float floatValue(Object paramObject)
  {
    if (paramObject == null) {
      return 0.0F;
    }
    if ((paramObject instanceof Number)) {
      return ((Number)paramObject).floatValue();
    }
    throw new ClassCastException(paramObject.getClass().getName() + " can not be converted to float");
  }
  
  public static Object intObject(int paramInt)
  {
    return new Integer(paramInt);
  }
  
  public static int intValue(Object paramObject)
  {
    if (paramObject == null) {
      return 0;
    }
    if ((paramObject instanceof Number)) {
      return ((Number)paramObject).intValue();
    }
    throw new ClassCastException(paramObject.getClass().getName() + " can not be converted to int");
  }
  
  public static Object longObject(long paramLong)
  {
    return new Long(paramLong);
  }
  
  public static long longValue(Object paramObject)
  {
    if (paramObject == null) {
      return 0L;
    }
    if ((paramObject instanceof Number)) {
      return ((Number)paramObject).longValue();
    }
    throw new ClassCastException(paramObject.getClass().getName() + " can not be converted to long");
  }
  
  public static Object shortObject(short paramShort)
  {
    return new Short(paramShort);
  }
  
  public static short shortValue(Object paramObject)
  {
    if (paramObject == null) {
      return 0;
    }
    if ((paramObject instanceof Number)) {
      return ((Number)paramObject).shortValue();
    }
    throw new ClassCastException(paramObject.getClass().getName() + " can not be converted to short");
  }
  
  public static Object voidObject()
  {
    return null;
  }
  
  public static Object voidValue(Object paramObject)
  {
    if (paramObject == null) {}
    return paramObject;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/runtime/internal/Conversions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */