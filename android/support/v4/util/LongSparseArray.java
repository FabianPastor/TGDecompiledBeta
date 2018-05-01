package android.support.v4.util;

public class LongSparseArray<E>
  implements Cloneable
{
  private static final Object DELETED = new Object();
  private boolean mGarbage = false;
  private long[] mKeys;
  private int mSize;
  private Object[] mValues;
  
  public LongSparseArray()
  {
    this(10);
  }
  
  public LongSparseArray(int paramInt)
  {
    if (paramInt == 0) {
      this.mKeys = ContainerHelpers.EMPTY_LONGS;
    }
    for (this.mValues = ContainerHelpers.EMPTY_OBJECTS;; this.mValues = new Object[paramInt])
    {
      this.mSize = 0;
      return;
      paramInt = ContainerHelpers.idealLongArraySize(paramInt);
      this.mKeys = new long[paramInt];
    }
  }
  
  private void gc()
  {
    int i = this.mSize;
    int j = 0;
    long[] arrayOfLong = this.mKeys;
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
          arrayOfLong[j] = arrayOfLong[k];
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
  
  public LongSparseArray<E> clone()
  {
    Object localObject = null;
    try
    {
      LongSparseArray localLongSparseArray = (LongSparseArray)super.clone();
      localObject = localLongSparseArray;
      localLongSparseArray.mKeys = ((long[])this.mKeys.clone());
      localObject = localLongSparseArray;
      localLongSparseArray.mValues = ((Object[])this.mValues.clone());
      localObject = localLongSparseArray;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      for (;;) {}
    }
    return (LongSparseArray<E>)localObject;
  }
  
  public E get(long paramLong)
  {
    return (E)get(paramLong, null);
  }
  
  public E get(long paramLong, E paramE)
  {
    int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramLong);
    Object localObject = paramE;
    if (i >= 0) {
      if (this.mValues[i] != DELETED) {
        break label41;
      }
    }
    label41:
    for (localObject = paramE;; localObject = this.mValues[i]) {
      return (E)localObject;
    }
  }
  
  public long keyAt(int paramInt)
  {
    if (this.mGarbage) {
      gc();
    }
    return this.mKeys[paramInt];
  }
  
  public void put(long paramLong, E paramE)
  {
    int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramLong);
    if (i >= 0) {
      this.mValues[i] = paramE;
    }
    for (;;)
    {
      return;
      int j = i ^ 0xFFFFFFFF;
      if ((j < this.mSize) && (this.mValues[j] == DELETED))
      {
        this.mKeys[j] = paramLong;
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
            i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramLong) ^ 0xFFFFFFFF;
          }
        }
        if (this.mSize >= this.mKeys.length)
        {
          j = ContainerHelpers.idealLongArraySize(this.mSize + 1);
          long[] arrayOfLong = new long[j];
          Object[] arrayOfObject = new Object[j];
          System.arraycopy(this.mKeys, 0, arrayOfLong, 0, this.mKeys.length);
          System.arraycopy(this.mValues, 0, arrayOfObject, 0, this.mValues.length);
          this.mKeys = arrayOfLong;
          this.mValues = arrayOfObject;
        }
        if (this.mSize - i != 0)
        {
          System.arraycopy(this.mKeys, i, this.mKeys, i + 1, this.mSize - i);
          System.arraycopy(this.mValues, i, this.mValues, i + 1, this.mSize - i);
        }
        this.mKeys[i] = paramLong;
        this.mValues[i] = paramE;
        this.mSize += 1;
      }
    }
  }
  
  public void removeAt(int paramInt)
  {
    if (this.mValues[paramInt] != DELETED)
    {
      this.mValues[paramInt] = DELETED;
      this.mGarbage = true;
    }
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
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/util/LongSparseArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */