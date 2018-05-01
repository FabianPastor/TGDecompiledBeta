package org.telegram.messenger.support.widget;

import java.util.ArrayList;

class PositionMap<E>
  implements Cloneable
{
  private static final Object DELETED = new Object();
  private boolean mGarbage = false;
  private int[] mKeys;
  private int mSize;
  private Object[] mValues;
  
  PositionMap()
  {
    this(10);
  }
  
  PositionMap(int paramInt)
  {
    if (paramInt == 0) {
      this.mKeys = ContainerHelpers.EMPTY_INTS;
    }
    for (this.mValues = ContainerHelpers.EMPTY_OBJECTS;; this.mValues = new Object[paramInt])
    {
      this.mSize = 0;
      return;
      paramInt = idealIntArraySize(paramInt);
      this.mKeys = new int[paramInt];
    }
  }
  
  private void gc()
  {
    int i = this.mSize;
    int j = 0;
    int[] arrayOfInt = this.mKeys;
    Object[] arrayOfObject = this.mValues;
    int k = 0;
    while (k < i)
    {
      Object localObject = arrayOfObject[k];
      int m = j;
      if (localObject != DELETED)
      {
        if (k != j)
        {
          arrayOfInt[j] = arrayOfInt[k];
          arrayOfObject[j] = localObject;
          arrayOfObject[k] = null;
        }
        m = j + 1;
      }
      k++;
      j = m;
    }
    this.mGarbage = false;
    this.mSize = j;
  }
  
  static int idealBooleanArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt);
  }
  
  static int idealByteArraySize(int paramInt)
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
  
  static int idealCharArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt * 2) / 2;
  }
  
  static int idealFloatArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt * 4) / 4;
  }
  
  static int idealIntArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt * 4) / 4;
  }
  
  static int idealLongArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt * 8) / 8;
  }
  
  static int idealObjectArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt * 4) / 4;
  }
  
  static int idealShortArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt * 2) / 2;
  }
  
  public void append(int paramInt, E paramE)
  {
    if ((this.mSize != 0) && (paramInt <= this.mKeys[(this.mSize - 1)])) {
      put(paramInt, paramE);
    }
    for (;;)
    {
      return;
      if ((this.mGarbage) && (this.mSize >= this.mKeys.length)) {
        gc();
      }
      int i = this.mSize;
      if (i >= this.mKeys.length)
      {
        int j = idealIntArraySize(i + 1);
        int[] arrayOfInt = new int[j];
        Object[] arrayOfObject = new Object[j];
        System.arraycopy(this.mKeys, 0, arrayOfInt, 0, this.mKeys.length);
        System.arraycopy(this.mValues, 0, arrayOfObject, 0, this.mValues.length);
        this.mKeys = arrayOfInt;
        this.mValues = arrayOfObject;
      }
      this.mKeys[i] = paramInt;
      this.mValues[i] = paramE;
      this.mSize = (i + 1);
    }
  }
  
  public void clear()
  {
    int i = this.mSize;
    Object[] arrayOfObject = this.mValues;
    for (int j = 0; j < i; j++) {
      arrayOfObject[j] = null;
    }
    this.mSize = 0;
    this.mGarbage = false;
  }
  
  public PositionMap<E> clone()
  {
    Object localObject = null;
    try
    {
      PositionMap localPositionMap = (PositionMap)super.clone();
      localObject = localPositionMap;
      localPositionMap.mKeys = ((int[])this.mKeys.clone());
      localObject = localPositionMap;
      localPositionMap.mValues = ((Object[])this.mValues.clone());
      localObject = localPositionMap;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      for (;;) {}
    }
    return (PositionMap<E>)localObject;
  }
  
  public void delete(int paramInt)
  {
    paramInt = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramInt);
    if ((paramInt >= 0) && (this.mValues[paramInt] != DELETED))
    {
      this.mValues[paramInt] = DELETED;
      this.mGarbage = true;
    }
  }
  
  public E get(int paramInt)
  {
    return (E)get(paramInt, null);
  }
  
  public E get(int paramInt, E paramE)
  {
    paramInt = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramInt);
    Object localObject = paramE;
    if (paramInt >= 0) {
      if (this.mValues[paramInt] != DELETED) {
        break label35;
      }
    }
    label35:
    for (localObject = paramE;; localObject = this.mValues[paramInt]) {
      return (E)localObject;
    }
  }
  
  public int indexOfKey(int paramInt)
  {
    if (this.mGarbage) {
      gc();
    }
    return ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramInt);
  }
  
  public int indexOfValue(E paramE)
  {
    if (this.mGarbage) {
      gc();
    }
    int i = 0;
    if (i < this.mSize) {
      if (this.mValues[i] != paramE) {}
    }
    for (;;)
    {
      return i;
      i++;
      break;
      i = -1;
    }
  }
  
  public void insertKeyRange(int paramInt1, int paramInt2) {}
  
  public int keyAt(int paramInt)
  {
    if (this.mGarbage) {
      gc();
    }
    return this.mKeys[paramInt];
  }
  
  public void put(int paramInt, E paramE)
  {
    int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramInt);
    if (i >= 0) {
      this.mValues[i] = paramE;
    }
    for (;;)
    {
      return;
      int j = i ^ 0xFFFFFFFF;
      if ((j < this.mSize) && (this.mValues[j] == DELETED))
      {
        this.mKeys[j] = paramInt;
        this.mValues[j] = paramE;
      }
      else
      {
        i = j;
        if (this.mGarbage)
        {
          i = j;
          if (this.mSize >= this.mKeys.length)
          {
            gc();
            i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramInt) ^ 0xFFFFFFFF;
          }
        }
        if (this.mSize >= this.mKeys.length)
        {
          j = idealIntArraySize(this.mSize + 1);
          int[] arrayOfInt = new int[j];
          Object[] arrayOfObject = new Object[j];
          System.arraycopy(this.mKeys, 0, arrayOfInt, 0, this.mKeys.length);
          System.arraycopy(this.mValues, 0, arrayOfObject, 0, this.mValues.length);
          this.mKeys = arrayOfInt;
          this.mValues = arrayOfObject;
        }
        if (this.mSize - i != 0)
        {
          System.arraycopy(this.mKeys, i, this.mKeys, i + 1, this.mSize - i);
          System.arraycopy(this.mValues, i, this.mValues, i + 1, this.mSize - i);
        }
        this.mKeys[i] = paramInt;
        this.mValues[i] = paramE;
        this.mSize += 1;
      }
    }
  }
  
  public void remove(int paramInt)
  {
    delete(paramInt);
  }
  
  public void removeAt(int paramInt)
  {
    if (this.mValues[paramInt] != DELETED)
    {
      this.mValues[paramInt] = DELETED;
      this.mGarbage = true;
    }
  }
  
  public void removeAtRange(int paramInt1, int paramInt2)
  {
    paramInt2 = Math.min(this.mSize, paramInt1 + paramInt2);
    while (paramInt1 < paramInt2)
    {
      removeAt(paramInt1);
      paramInt1++;
    }
  }
  
  public void removeKeyRange(ArrayList<E> paramArrayList, int paramInt1, int paramInt2) {}
  
  public void setValueAt(int paramInt, E paramE)
  {
    if (this.mGarbage) {
      gc();
    }
    this.mValues[paramInt] = paramE;
  }
  
  public int size()
  {
    if (this.mGarbage) {
      gc();
    }
    return this.mSize;
  }
  
  public String toString()
  {
    if (size() <= 0) {}
    StringBuilder localStringBuilder;
    for (Object localObject = "{}";; localObject = localStringBuilder.toString())
    {
      return (String)localObject;
      localStringBuilder = new StringBuilder(this.mSize * 28);
      localStringBuilder.append('{');
      int i = 0;
      if (i < this.mSize)
      {
        if (i > 0) {
          localStringBuilder.append(", ");
        }
        localStringBuilder.append(keyAt(i));
        localStringBuilder.append('=');
        localObject = valueAt(i);
        if (localObject != this) {
          localStringBuilder.append(localObject);
        }
        for (;;)
        {
          i++;
          break;
          localStringBuilder.append("(this Map)");
        }
      }
      localStringBuilder.append('}');
    }
  }
  
  public E valueAt(int paramInt)
  {
    if (this.mGarbage) {
      gc();
    }
    return (E)this.mValues[paramInt];
  }
  
  static class ContainerHelpers
  {
    static final boolean[] EMPTY_BOOLEANS = new boolean[0];
    static final int[] EMPTY_INTS = new int[0];
    static final long[] EMPTY_LONGS = new long[0];
    static final Object[] EMPTY_OBJECTS = new Object[0];
    
    static int binarySearch(int[] paramArrayOfInt, int paramInt1, int paramInt2)
    {
      int i = 0;
      int j = paramInt1 - 1;
      paramInt1 = i;
      while (paramInt1 <= j)
      {
        i = paramInt1 + j >>> 1;
        int k = paramArrayOfInt[i];
        if (k < paramInt2)
        {
          paramInt1 = i + 1;
        }
        else
        {
          j = i;
          if (k <= paramInt2) {
            break label62;
          }
          j = i - 1;
        }
      }
      j = paramInt1 ^ 0xFFFFFFFF;
      label62:
      return j;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/widget/PositionMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */