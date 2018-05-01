package org.telegram.messenger.support;

public class SparseLongArray
  implements Cloneable
{
  private int[] mKeys;
  private int mSize;
  private long[] mValues;
  
  public SparseLongArray()
  {
    this(10);
  }
  
  public SparseLongArray(int paramInt)
  {
    paramInt = ArrayUtils.idealLongArraySize(paramInt);
    this.mKeys = new int[paramInt];
    this.mValues = new long[paramInt];
    this.mSize = 0;
  }
  
  private static int binarySearch(int[] paramArrayOfInt, int paramInt1, int paramInt2, long paramLong)
  {
    int i = paramInt1 + paramInt2;
    int j = paramInt1 - 1;
    while (i - j > 1)
    {
      int k = (i + j) / 2;
      if (paramArrayOfInt[k] < paramLong) {
        j = k;
      } else {
        i = k;
      }
    }
    if (i == paramInt1 + paramInt2) {
      paramInt1 = paramInt1 + paramInt2 ^ 0xFFFFFFFF;
    }
    for (;;)
    {
      return paramInt1;
      paramInt1 = i;
      if (paramArrayOfInt[i] != paramLong) {
        paramInt1 = i ^ 0xFFFFFFFF;
      }
    }
  }
  
  private void growKeyAndValueArrays(int paramInt)
  {
    paramInt = ArrayUtils.idealLongArraySize(paramInt);
    int[] arrayOfInt = new int[paramInt];
    long[] arrayOfLong = new long[paramInt];
    System.arraycopy(this.mKeys, 0, arrayOfInt, 0, this.mKeys.length);
    System.arraycopy(this.mValues, 0, arrayOfLong, 0, this.mValues.length);
    this.mKeys = arrayOfInt;
    this.mValues = arrayOfLong;
  }
  
  public void append(int paramInt, long paramLong)
  {
    if ((this.mSize != 0) && (paramInt <= this.mKeys[(this.mSize - 1)])) {
      put(paramInt, paramLong);
    }
    for (;;)
    {
      return;
      int i = this.mSize;
      if (i >= this.mKeys.length) {
        growKeyAndValueArrays(i + 1);
      }
      this.mKeys[i] = paramInt;
      this.mValues[i] = paramLong;
      this.mSize = (i + 1);
    }
  }
  
  public void clear()
  {
    this.mSize = 0;
  }
  
  public SparseLongArray clone()
  {
    Object localObject = null;
    try
    {
      SparseLongArray localSparseLongArray = (SparseLongArray)super.clone();
      localObject = localSparseLongArray;
      localSparseLongArray.mKeys = ((int[])this.mKeys.clone());
      localObject = localSparseLongArray;
      localSparseLongArray.mValues = ((long[])this.mValues.clone());
      localObject = localSparseLongArray;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      for (;;) {}
    }
    return (SparseLongArray)localObject;
  }
  
  public void delete(int paramInt)
  {
    paramInt = binarySearch(this.mKeys, 0, this.mSize, paramInt);
    if (paramInt >= 0) {
      removeAt(paramInt);
    }
  }
  
  public long get(int paramInt)
  {
    return get(paramInt, 0L);
  }
  
  public long get(int paramInt, long paramLong)
  {
    paramInt = binarySearch(this.mKeys, 0, this.mSize, paramInt);
    if (paramInt < 0) {}
    for (;;)
    {
      return paramLong;
      paramLong = this.mValues[paramInt];
    }
  }
  
  public int indexOfKey(int paramInt)
  {
    return binarySearch(this.mKeys, 0, this.mSize, paramInt);
  }
  
  public int indexOfValue(long paramLong)
  {
    int i = 0;
    if (i < this.mSize) {
      if (this.mValues[i] != paramLong) {}
    }
    for (;;)
    {
      return i;
      i++;
      break;
      i = -1;
    }
  }
  
  public int keyAt(int paramInt)
  {
    return this.mKeys[paramInt];
  }
  
  public void put(int paramInt, long paramLong)
  {
    int i = binarySearch(this.mKeys, 0, this.mSize, paramInt);
    if (i >= 0) {
      this.mValues[i] = paramLong;
    }
    for (;;)
    {
      return;
      i ^= 0xFFFFFFFF;
      if (this.mSize >= this.mKeys.length) {
        growKeyAndValueArrays(this.mSize + 1);
      }
      if (this.mSize - i != 0)
      {
        System.arraycopy(this.mKeys, i, this.mKeys, i + 1, this.mSize - i);
        System.arraycopy(this.mValues, i, this.mValues, i + 1, this.mSize - i);
      }
      this.mKeys[i] = paramInt;
      this.mValues[i] = paramLong;
      this.mSize += 1;
    }
  }
  
  public void removeAt(int paramInt)
  {
    System.arraycopy(this.mKeys, paramInt + 1, this.mKeys, paramInt, this.mSize - (paramInt + 1));
    System.arraycopy(this.mValues, paramInt + 1, this.mValues, paramInt, this.mSize - (paramInt + 1));
    this.mSize -= 1;
  }
  
  public int size()
  {
    return this.mSize;
  }
  
  public long valueAt(int paramInt)
  {
    return this.mValues[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/SparseLongArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */