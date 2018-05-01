package android.support.v4.util;

import java.util.ConcurrentModificationException;
import java.util.Map;

public class SimpleArrayMap<K, V>
{
  static Object[] mBaseCache;
  static int mBaseCacheSize;
  static Object[] mTwiceBaseCache;
  static int mTwiceBaseCacheSize;
  Object[] mArray;
  int[] mHashes;
  int mSize;
  
  public SimpleArrayMap()
  {
    this.mHashes = ContainerHelpers.EMPTY_INTS;
    this.mArray = ContainerHelpers.EMPTY_OBJECTS;
    this.mSize = 0;
  }
  
  public SimpleArrayMap(int paramInt)
  {
    if (paramInt == 0)
    {
      this.mHashes = ContainerHelpers.EMPTY_INTS;
      this.mArray = ContainerHelpers.EMPTY_OBJECTS;
    }
    for (;;)
    {
      this.mSize = 0;
      return;
      allocArrays(paramInt);
    }
  }
  
  private void allocArrays(int paramInt)
  {
    if (paramInt == 8) {}
    for (;;)
    {
      try
      {
        if (mTwiceBaseCache != null)
        {
          Object[] arrayOfObject1 = mTwiceBaseCache;
          this.mArray = arrayOfObject1;
          mTwiceBaseCache = (Object[])arrayOfObject1[0];
          this.mHashes = ((int[])arrayOfObject1[1]);
          arrayOfObject1[1] = null;
          arrayOfObject1[0] = null;
          mTwiceBaseCacheSize -= 1;
          return;
        }
        this.mHashes = new int[paramInt];
        this.mArray = new Object[paramInt << 1];
        continue;
        if (paramInt != 4) {
          continue;
        }
      }
      finally {}
      try
      {
        if (mBaseCache != null)
        {
          Object[] arrayOfObject2 = mBaseCache;
          this.mArray = arrayOfObject2;
          mBaseCache = (Object[])arrayOfObject2[0];
          this.mHashes = ((int[])arrayOfObject2[1]);
          arrayOfObject2[1] = null;
          arrayOfObject2[0] = null;
          mBaseCacheSize -= 1;
          continue;
        }
      }
      finally {}
    }
  }
  
  private static int binarySearchHashes(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    try
    {
      paramInt1 = ContainerHelpers.binarySearch(paramArrayOfInt, paramInt1, paramInt2);
      return paramInt1;
    }
    catch (ArrayIndexOutOfBoundsException paramArrayOfInt)
    {
      throw new ConcurrentModificationException();
    }
  }
  
  private static void freeArrays(int[] paramArrayOfInt, Object[] paramArrayOfObject, int paramInt)
  {
    if (paramArrayOfInt.length == 8) {}
    for (;;)
    {
      try
      {
        if (mTwiceBaseCacheSize < 10)
        {
          paramArrayOfObject[0] = mTwiceBaseCache;
          paramArrayOfObject[1] = paramArrayOfInt;
          paramInt = (paramInt << 1) - 1;
          if (paramInt >= 2)
          {
            paramArrayOfObject[paramInt] = null;
            paramInt--;
            continue;
          }
          mTwiceBaseCache = paramArrayOfObject;
          mTwiceBaseCacheSize += 1;
        }
        return;
      }
      finally {}
      if (paramArrayOfInt.length != 4) {
        continue;
      }
      try
      {
        if (mBaseCacheSize < 10)
        {
          paramArrayOfObject[0] = mBaseCache;
          paramArrayOfObject[1] = paramArrayOfInt;
          for (paramInt = (paramInt << 1) - 1; paramInt >= 2; paramInt--) {
            paramArrayOfObject[paramInt] = null;
          }
          mBaseCache = paramArrayOfObject;
          mBaseCacheSize += 1;
        }
      }
      finally {}
    }
  }
  
  public void clear()
  {
    if (this.mSize > 0)
    {
      int[] arrayOfInt = this.mHashes;
      Object[] arrayOfObject = this.mArray;
      int i = this.mSize;
      this.mHashes = ContainerHelpers.EMPTY_INTS;
      this.mArray = ContainerHelpers.EMPTY_OBJECTS;
      this.mSize = 0;
      freeArrays(arrayOfInt, arrayOfObject, i);
    }
    if (this.mSize > 0) {
      throw new ConcurrentModificationException();
    }
  }
  
  public boolean containsKey(Object paramObject)
  {
    if (indexOfKey(paramObject) >= 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean containsValue(Object paramObject)
  {
    if (indexOfValue(paramObject) >= 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void ensureCapacity(int paramInt)
  {
    int i = this.mSize;
    if (this.mHashes.length < paramInt)
    {
      int[] arrayOfInt = this.mHashes;
      Object[] arrayOfObject = this.mArray;
      allocArrays(paramInt);
      if (this.mSize > 0)
      {
        System.arraycopy(arrayOfInt, 0, this.mHashes, 0, i);
        System.arraycopy(arrayOfObject, 0, this.mArray, 0, i << 1);
      }
      freeArrays(arrayOfInt, arrayOfObject, i);
    }
    if (this.mSize != i) {
      throw new ConcurrentModificationException();
    }
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool1 = true;
    boolean bool2;
    if (this == paramObject) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      Object localObject1;
      int i;
      Object localObject2;
      Object localObject3;
      if ((paramObject instanceof SimpleArrayMap))
      {
        localObject1 = (SimpleArrayMap)paramObject;
        if (size() != ((SimpleArrayMap)localObject1).size())
        {
          bool2 = false;
        }
        else
        {
          i = 0;
          bool2 = bool1;
          try
          {
            if (i >= this.mSize) {
              continue;
            }
            paramObject = keyAt(i);
            localObject2 = valueAt(i);
            localObject3 = ((SimpleArrayMap)localObject1).get(paramObject);
            if (localObject2 == null)
            {
              if ((localObject3 != null) || (!((SimpleArrayMap)localObject1).containsKey(paramObject))) {
                bool2 = false;
              }
            }
            else
            {
              bool2 = localObject2.equals(localObject3);
              if (!bool2)
              {
                bool2 = false;
                continue;
              }
            }
            i++;
          }
          catch (NullPointerException paramObject)
          {
            bool2 = false;
            continue;
          }
          catch (ClassCastException paramObject)
          {
            bool2 = false;
          }
        }
      }
      else if ((paramObject instanceof Map))
      {
        localObject2 = (Map)paramObject;
        if (size() != ((Map)localObject2).size())
        {
          bool2 = false;
        }
        else
        {
          i = 0;
          bool2 = bool1;
          try
          {
            if (i >= this.mSize) {
              continue;
            }
            paramObject = keyAt(i);
            localObject3 = valueAt(i);
            localObject1 = ((Map)localObject2).get(paramObject);
            if (localObject3 == null)
            {
              if ((localObject1 != null) || (!((Map)localObject2).containsKey(paramObject))) {
                bool2 = false;
              }
            }
            else
            {
              bool2 = localObject3.equals(localObject1);
              if (!bool2)
              {
                bool2 = false;
                continue;
              }
            }
            i++;
          }
          catch (NullPointerException paramObject)
          {
            bool2 = false;
            continue;
          }
          catch (ClassCastException paramObject)
          {
            bool2 = false;
          }
        }
      }
      else
      {
        bool2 = false;
      }
    }
  }
  
  public V get(Object paramObject)
  {
    int i = indexOfKey(paramObject);
    if (i >= 0) {}
    for (paramObject = this.mArray[((i << 1) + 1)];; paramObject = null) {
      return (V)paramObject;
    }
  }
  
  public int hashCode()
  {
    int[] arrayOfInt = this.mHashes;
    Object[] arrayOfObject = this.mArray;
    int i = 0;
    int j = 0;
    int k = 1;
    int m = this.mSize;
    if (j < m)
    {
      Object localObject = arrayOfObject[k];
      int n = arrayOfInt[j];
      if (localObject == null) {}
      for (int i1 = 0;; i1 = localObject.hashCode())
      {
        i += (i1 ^ n);
        j++;
        k += 2;
        break;
      }
    }
    return i;
  }
  
  int indexOf(Object paramObject, int paramInt)
  {
    int i = this.mSize;
    int j;
    if (i == 0) {
      j = -1;
    }
    for (;;)
    {
      return j;
      int k = binarySearchHashes(this.mHashes, i, paramInt);
      j = k;
      if (k >= 0)
      {
        j = k;
        if (!paramObject.equals(this.mArray[(k << 1)]))
        {
          for (j = k + 1;; j++)
          {
            if ((j >= i) || (this.mHashes[j] != paramInt)) {
              break label103;
            }
            if (paramObject.equals(this.mArray[(j << 1)])) {
              break;
            }
          }
          label103:
          k--;
          for (;;)
          {
            if ((k < 0) || (this.mHashes[k] != paramInt)) {
              break label151;
            }
            if (paramObject.equals(this.mArray[(k << 1)]))
            {
              j = k;
              break;
            }
            k--;
          }
          label151:
          j ^= 0xFFFFFFFF;
        }
      }
    }
  }
  
  public int indexOfKey(Object paramObject)
  {
    if (paramObject == null) {}
    for (int i = indexOfNull();; i = indexOf(paramObject, paramObject.hashCode())) {
      return i;
    }
  }
  
  int indexOfNull()
  {
    int i = this.mSize;
    int j;
    if (i == 0) {
      j = -1;
    }
    for (;;)
    {
      return j;
      int k = binarySearchHashes(this.mHashes, i, 0);
      j = k;
      if (k >= 0)
      {
        j = k;
        if (this.mArray[(k << 1)] != null)
        {
          for (j = k + 1;; j++)
          {
            if ((j >= i) || (this.mHashes[j] != 0)) {
              break label80;
            }
            if (this.mArray[(j << 1)] == null) {
              break;
            }
          }
          label80:
          k--;
          for (;;)
          {
            if ((k < 0) || (this.mHashes[k] != 0)) {
              break label118;
            }
            if (this.mArray[(k << 1)] == null)
            {
              j = k;
              break;
            }
            k--;
          }
          label118:
          j ^= 0xFFFFFFFF;
        }
      }
    }
  }
  
  int indexOfValue(Object paramObject)
  {
    int i = this.mSize * 2;
    Object[] arrayOfObject = this.mArray;
    int j;
    if (paramObject == null)
    {
      j = 1;
      if (j >= i) {
        break label82;
      }
      if (arrayOfObject[j] == null) {
        j >>= 1;
      }
    }
    for (;;)
    {
      return j;
      j += 2;
      break;
      for (j = 1;; j += 2)
      {
        if (j >= i) {
          break label82;
        }
        if (paramObject.equals(arrayOfObject[j]))
        {
          j >>= 1;
          break;
        }
      }
      label82:
      j = -1;
    }
  }
  
  public boolean isEmpty()
  {
    if (this.mSize <= 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public K keyAt(int paramInt)
  {
    return (K)this.mArray[(paramInt << 1)];
  }
  
  public V put(K paramK, V paramV)
  {
    int i = 8;
    int j = this.mSize;
    int k;
    int m;
    if (paramK == null)
    {
      k = 0;
      m = indexOfNull();
      if (m < 0) {
        break label71;
      }
      m = (m << 1) + 1;
      paramK = this.mArray[m];
      this.mArray[m] = paramV;
    }
    for (;;)
    {
      return paramK;
      k = paramK.hashCode();
      m = indexOf(paramK, k);
      break;
      label71:
      int n = m ^ 0xFFFFFFFF;
      if (j >= this.mHashes.length)
      {
        if (j >= 8) {
          m = j + (j >> 1);
        }
        int[] arrayOfInt;
        Object[] arrayOfObject;
        for (;;)
        {
          arrayOfInt = this.mHashes;
          arrayOfObject = this.mArray;
          allocArrays(m);
          if (j == this.mSize) {
            break;
          }
          throw new ConcurrentModificationException();
          m = i;
          if (j < 4) {
            m = 4;
          }
        }
        if (this.mHashes.length > 0)
        {
          System.arraycopy(arrayOfInt, 0, this.mHashes, 0, arrayOfInt.length);
          System.arraycopy(arrayOfObject, 0, this.mArray, 0, arrayOfObject.length);
        }
        freeArrays(arrayOfInt, arrayOfObject, j);
      }
      if (n < j)
      {
        System.arraycopy(this.mHashes, n, this.mHashes, n + 1, j - n);
        System.arraycopy(this.mArray, n << 1, this.mArray, n + 1 << 1, this.mSize - n << 1);
      }
      if ((j != this.mSize) || (n >= this.mHashes.length)) {
        throw new ConcurrentModificationException();
      }
      this.mHashes[n] = k;
      this.mArray[(n << 1)] = paramK;
      this.mArray[((n << 1) + 1)] = paramV;
      this.mSize += 1;
      paramK = null;
    }
  }
  
  public V remove(Object paramObject)
  {
    int i = indexOfKey(paramObject);
    if (i >= 0) {}
    for (paramObject = removeAt(i);; paramObject = null) {
      return (V)paramObject;
    }
  }
  
  public V removeAt(int paramInt)
  {
    int i = 8;
    Object localObject = this.mArray[((paramInt << 1) + 1)];
    int j = this.mSize;
    if (j <= 1)
    {
      freeArrays(this.mHashes, this.mArray, j);
      this.mHashes = ContainerHelpers.EMPTY_INTS;
      this.mArray = ContainerHelpers.EMPTY_OBJECTS;
      i = 0;
    }
    while (j != this.mSize)
    {
      throw new ConcurrentModificationException();
      int k = j - 1;
      if ((this.mHashes.length > 8) && (this.mSize < this.mHashes.length / 3))
      {
        if (j > 8) {
          i = j + (j >> 1);
        }
        int[] arrayOfInt = this.mHashes;
        Object[] arrayOfObject = this.mArray;
        allocArrays(i);
        if (j != this.mSize) {
          throw new ConcurrentModificationException();
        }
        if (paramInt > 0)
        {
          System.arraycopy(arrayOfInt, 0, this.mHashes, 0, paramInt);
          System.arraycopy(arrayOfObject, 0, this.mArray, 0, paramInt << 1);
        }
        i = k;
        if (paramInt < k)
        {
          System.arraycopy(arrayOfInt, paramInt + 1, this.mHashes, paramInt, k - paramInt);
          System.arraycopy(arrayOfObject, paramInt + 1 << 1, this.mArray, paramInt << 1, k - paramInt << 1);
          i = k;
        }
      }
      else
      {
        if (paramInt < k)
        {
          System.arraycopy(this.mHashes, paramInt + 1, this.mHashes, paramInt, k - paramInt);
          System.arraycopy(this.mArray, paramInt + 1 << 1, this.mArray, paramInt << 1, k - paramInt << 1);
        }
        this.mArray[(k << 1)] = null;
        this.mArray[((k << 1) + 1)] = null;
        i = k;
      }
    }
    this.mSize = i;
    return (V)localObject;
  }
  
  public V setValueAt(int paramInt, V paramV)
  {
    paramInt = (paramInt << 1) + 1;
    Object localObject = this.mArray[paramInt];
    this.mArray[paramInt] = paramV;
    return (V)localObject;
  }
  
  public int size()
  {
    return this.mSize;
  }
  
  public String toString()
  {
    if (isEmpty()) {}
    for (Object localObject1 = "{}";; localObject1 = ((StringBuilder)localObject1).toString())
    {
      return (String)localObject1;
      localObject1 = new StringBuilder(this.mSize * 28);
      ((StringBuilder)localObject1).append('{');
      int i = 0;
      if (i < this.mSize)
      {
        if (i > 0) {
          ((StringBuilder)localObject1).append(", ");
        }
        Object localObject2 = keyAt(i);
        if (localObject2 != this)
        {
          ((StringBuilder)localObject1).append(localObject2);
          label72:
          ((StringBuilder)localObject1).append('=');
          localObject2 = valueAt(i);
          if (localObject2 == this) {
            break label112;
          }
          ((StringBuilder)localObject1).append(localObject2);
        }
        for (;;)
        {
          i++;
          break;
          ((StringBuilder)localObject1).append("(this Map)");
          break label72;
          label112:
          ((StringBuilder)localObject1).append("(this Map)");
        }
      }
      ((StringBuilder)localObject1).append('}');
    }
  }
  
  public V valueAt(int paramInt)
  {
    return (V)this.mArray[((paramInt << 1) + 1)];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/util/SimpleArrayMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */