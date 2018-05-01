package org.telegram.messenger.support;

import java.lang.reflect.Array;

public class ArrayUtils
{
  private static final int CACHE_SIZE = 73;
  private static Object[] EMPTY = new Object[0];
  private static Object[] sCache = new Object[73];
  
  public static <T> T[] appendElement(Class<T> paramClass, T[] paramArrayOfT, T paramT)
  {
    int i;
    if (paramArrayOfT != null)
    {
      i = paramArrayOfT.length;
      paramClass = (Object[])Array.newInstance(paramClass, i + 1);
      System.arraycopy(paramArrayOfT, 0, paramClass, 0, i);
    }
    for (;;)
    {
      paramClass[i] = paramT;
      return paramClass;
      i = 0;
      paramClass = (Object[])Array.newInstance(paramClass, 1);
    }
  }
  
  public static int[] appendInt(int[] paramArrayOfInt, int paramInt)
  {
    int[] arrayOfInt;
    if (paramArrayOfInt == null)
    {
      arrayOfInt = new int[1];
      arrayOfInt[0] = paramInt;
    }
    for (;;)
    {
      return arrayOfInt;
      int i = paramArrayOfInt.length;
      for (int j = 0;; j++)
      {
        if (j >= i) {
          break label42;
        }
        arrayOfInt = paramArrayOfInt;
        if (paramArrayOfInt[j] == paramInt) {
          break;
        }
      }
      label42:
      arrayOfInt = new int[i + 1];
      System.arraycopy(paramArrayOfInt, 0, arrayOfInt, 0, i);
      arrayOfInt[i] = paramInt;
    }
  }
  
  public static boolean contains(int[] paramArrayOfInt, int paramInt)
  {
    boolean bool1 = false;
    int i = paramArrayOfInt.length;
    for (int j = 0;; j++)
    {
      boolean bool2 = bool1;
      if (j < i)
      {
        if (paramArrayOfInt[j] == paramInt) {
          bool2 = true;
        }
      }
      else {
        return bool2;
      }
    }
  }
  
  public static <T> boolean contains(T[] paramArrayOfT, T paramT)
  {
    boolean bool1 = true;
    int i = paramArrayOfT.length;
    int j = 0;
    T ?;
    boolean bool2;
    if (j < i)
    {
      ? = paramArrayOfT[j];
      if (? == null)
      {
        if (paramT != null) {
          break label51;
        }
        bool2 = bool1;
      }
    }
    for (;;)
    {
      return bool2;
      if (paramT != null)
      {
        bool2 = bool1;
        if (?.equals(paramT)) {}
      }
      else
      {
        label51:
        j++;
        break;
        bool2 = false;
      }
    }
  }
  
  public static <T> T[] emptyArray(Class<T> paramClass)
  {
    if (paramClass == Object.class) {}
    Object localObject2;
    for (paramClass = (Object[])EMPTY;; paramClass = (Object[])localObject2)
    {
      return paramClass;
      int i = (System.identityHashCode(paramClass) / 8 & 0x7FFFFFFF) % 73;
      Object localObject1 = sCache[i];
      if (localObject1 != null)
      {
        localObject2 = localObject1;
        if (localObject1.getClass().getComponentType() == paramClass) {}
      }
      else
      {
        localObject2 = Array.newInstance(paramClass, 0);
        sCache[i] = localObject2;
      }
    }
  }
  
  public static boolean equals(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt)
  {
    boolean bool1 = true;
    if (paramArrayOfByte1 == paramArrayOfByte2) {}
    for (boolean bool2 = bool1;; bool2 = false)
    {
      return bool2;
      if ((paramArrayOfByte1 != null) && (paramArrayOfByte2 != null) && (paramArrayOfByte1.length >= paramInt) && (paramArrayOfByte2.length >= paramInt)) {
        break;
      }
    }
    for (int i = 0;; i++)
    {
      bool2 = bool1;
      if (i >= paramInt) {
        break;
      }
      if (paramArrayOfByte1[i] != paramArrayOfByte2[i])
      {
        bool2 = false;
        break;
      }
    }
  }
  
  public static int idealBooleanArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt);
  }
  
  public static int idealByteArraySize(int paramInt)
  {
    for (int i = 4;; i++)
    {
      int j = paramInt;
      if (i < 32)
      {
        if (paramInt <= (1 << i) - 12) {
          j = (1 << i) - 12;
        }
      }
      else {
        return j;
      }
    }
  }
  
  public static int idealCharArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt * 2) / 2;
  }
  
  public static int idealFloatArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt * 4) / 4;
  }
  
  public static int idealIntArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt * 4) / 4;
  }
  
  public static int idealLongArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt * 8) / 8;
  }
  
  public static int idealObjectArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt * 4) / 4;
  }
  
  public static int idealShortArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt * 2) / 2;
  }
  
  public static <T> T[] removeElement(Class<T> paramClass, T[] paramArrayOfT, T paramT)
  {
    int i;
    int j;
    if (paramArrayOfT != null)
    {
      i = paramArrayOfT.length;
      j = 0;
      if (j < i) {
        if (paramArrayOfT[j] == paramT) {
          if (i == 1) {
            paramClass = null;
          }
        }
      }
    }
    for (;;)
    {
      return paramClass;
      paramClass = (Object[])Array.newInstance(paramClass, i - 1);
      System.arraycopy(paramArrayOfT, 0, paramClass, 0, j);
      System.arraycopy(paramArrayOfT, j + 1, paramClass, j, i - j - 1);
      continue;
      j++;
      break;
      paramClass = paramArrayOfT;
    }
  }
  
  public static int[] removeInt(int[] paramArrayOfInt, int paramInt)
  {
    Object localObject;
    if (paramArrayOfInt == null) {
      localObject = null;
    }
    for (;;)
    {
      return (int[])localObject;
      int i = paramArrayOfInt.length;
      for (int j = 0;; j++)
      {
        if (j >= i) {
          break label91;
        }
        if (paramArrayOfInt[j] == paramInt)
        {
          int[] arrayOfInt = new int[i - 1];
          if (j > 0) {
            System.arraycopy(paramArrayOfInt, 0, arrayOfInt, 0, j);
          }
          localObject = arrayOfInt;
          if (j >= i - 1) {
            break;
          }
          System.arraycopy(paramArrayOfInt, j + 1, arrayOfInt, j, i - j - 1);
          localObject = arrayOfInt;
          break;
        }
      }
      label91:
      localObject = paramArrayOfInt;
    }
  }
  
  public static long total(long[] paramArrayOfLong)
  {
    long l = 0L;
    int i = paramArrayOfLong.length;
    for (int j = 0; j < i; j++) {
      l += paramArrayOfLong[j];
    }
    return l;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/ArrayUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */