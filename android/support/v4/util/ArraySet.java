package android.support.v4.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class ArraySet<E>
  implements Collection<E>, Set<E>
{
  private static final int[] INT = new int[0];
  private static final Object[] OBJECT = new Object[0];
  private static Object[] sBaseCache;
  private static int sBaseCacheSize;
  private static Object[] sTwiceBaseCache;
  private static int sTwiceBaseCacheSize;
  private Object[] mArray;
  private MapCollections<E, E> mCollections;
  private int[] mHashes;
  private int mSize;
  
  public ArraySet()
  {
    this(0);
  }
  
  public ArraySet(int paramInt)
  {
    if (paramInt == 0)
    {
      this.mHashes = INT;
      this.mArray = OBJECT;
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
        if (sTwiceBaseCache != null)
        {
          Object[] arrayOfObject1 = sTwiceBaseCache;
          this.mArray = arrayOfObject1;
          sTwiceBaseCache = (Object[])arrayOfObject1[0];
          this.mHashes = ((int[])arrayOfObject1[1]);
          arrayOfObject1[1] = null;
          arrayOfObject1[0] = null;
          sTwiceBaseCacheSize -= 1;
          return;
        }
        this.mHashes = new int[paramInt];
        this.mArray = new Object[paramInt];
        continue;
        if (paramInt != 4) {
          continue;
        }
      }
      finally {}
      try
      {
        if (sBaseCache != null)
        {
          Object[] arrayOfObject2 = sBaseCache;
          this.mArray = arrayOfObject2;
          sBaseCache = (Object[])arrayOfObject2[0];
          this.mHashes = ((int[])arrayOfObject2[1]);
          arrayOfObject2[1] = null;
          arrayOfObject2[0] = null;
          sBaseCacheSize -= 1;
          continue;
        }
      }
      finally {}
    }
  }
  
  private static void freeArrays(int[] paramArrayOfInt, Object[] paramArrayOfObject, int paramInt)
  {
    if (paramArrayOfInt.length == 8) {}
    for (;;)
    {
      try
      {
        if (sTwiceBaseCacheSize < 10)
        {
          paramArrayOfObject[0] = sTwiceBaseCache;
          paramArrayOfObject[1] = paramArrayOfInt;
          paramInt--;
          if (paramInt >= 2)
          {
            paramArrayOfObject[paramInt] = null;
            paramInt--;
            continue;
          }
          sTwiceBaseCache = paramArrayOfObject;
          sTwiceBaseCacheSize += 1;
        }
        return;
      }
      finally {}
      if (paramArrayOfInt.length != 4) {
        continue;
      }
      try
      {
        if (sBaseCacheSize < 10)
        {
          paramArrayOfObject[0] = sBaseCache;
          paramArrayOfObject[1] = paramArrayOfInt;
          paramInt--;
          while (paramInt >= 2)
          {
            paramArrayOfObject[paramInt] = null;
            paramInt--;
          }
          sBaseCache = paramArrayOfObject;
          sBaseCacheSize += 1;
        }
      }
      finally {}
    }
  }
  
  private MapCollections<E, E> getCollection()
  {
    if (this.mCollections == null) {
      this.mCollections = new MapCollections()
      {
        protected void colClear()
        {
          ArraySet.this.clear();
        }
        
        protected Object colGetEntry(int paramAnonymousInt1, int paramAnonymousInt2)
        {
          return ArraySet.this.mArray[paramAnonymousInt1];
        }
        
        protected Map<E, E> colGetMap()
        {
          throw new UnsupportedOperationException("not a map");
        }
        
        protected int colGetSize()
        {
          return ArraySet.this.mSize;
        }
        
        protected int colIndexOfKey(Object paramAnonymousObject)
        {
          return ArraySet.this.indexOf(paramAnonymousObject);
        }
        
        protected int colIndexOfValue(Object paramAnonymousObject)
        {
          return ArraySet.this.indexOf(paramAnonymousObject);
        }
        
        protected void colPut(E paramAnonymousE1, E paramAnonymousE2)
        {
          ArraySet.this.add(paramAnonymousE1);
        }
        
        protected void colRemoveAt(int paramAnonymousInt)
        {
          ArraySet.this.removeAt(paramAnonymousInt);
        }
        
        protected E colSetValue(int paramAnonymousInt, E paramAnonymousE)
        {
          throw new UnsupportedOperationException("not a map");
        }
      };
    }
    return this.mCollections;
  }
  
  private int indexOf(Object paramObject, int paramInt)
  {
    int i = this.mSize;
    int j;
    if (i == 0) {
      j = -1;
    }
    for (;;)
    {
      return j;
      int k = ContainerHelpers.binarySearch(this.mHashes, i, paramInt);
      j = k;
      if (k >= 0)
      {
        j = k;
        if (!paramObject.equals(this.mArray[k]))
        {
          for (j = k + 1;; j++)
          {
            if ((j >= i) || (this.mHashes[j] != paramInt)) {
              break label99;
            }
            if (paramObject.equals(this.mArray[j])) {
              break;
            }
          }
          label99:
          k--;
          for (;;)
          {
            if ((k < 0) || (this.mHashes[k] != paramInt)) {
              break label145;
            }
            if (paramObject.equals(this.mArray[k]))
            {
              j = k;
              break;
            }
            k--;
          }
          label145:
          j ^= 0xFFFFFFFF;
        }
      }
    }
  }
  
  private int indexOfNull()
  {
    int i = this.mSize;
    int j;
    if (i == 0) {
      j = -1;
    }
    for (;;)
    {
      return j;
      int k = ContainerHelpers.binarySearch(this.mHashes, i, 0);
      j = k;
      if (k >= 0)
      {
        j = k;
        if (this.mArray[k] != null)
        {
          for (j = k + 1;; j++)
          {
            if ((j >= i) || (this.mHashes[j] != 0)) {
              break label76;
            }
            if (this.mArray[j] == null) {
              break;
            }
          }
          label76:
          k--;
          for (;;)
          {
            if ((k < 0) || (this.mHashes[k] != 0)) {
              break label112;
            }
            if (this.mArray[k] == null)
            {
              j = k;
              break;
            }
            k--;
          }
          label112:
          j ^= 0xFFFFFFFF;
        }
      }
    }
  }
  
  public boolean add(E paramE)
  {
    int i = 8;
    int j;
    if (paramE == null) {
      j = 0;
    }
    boolean bool;
    for (int k = indexOfNull(); k >= 0; k = indexOf(paramE, j))
    {
      bool = false;
      return bool;
      j = paramE.hashCode();
    }
    int m = k ^ 0xFFFFFFFF;
    if (this.mSize >= this.mHashes.length)
    {
      if (this.mSize < 8) {
        break label236;
      }
      k = this.mSize + (this.mSize >> 1);
    }
    for (;;)
    {
      int[] arrayOfInt = this.mHashes;
      Object[] arrayOfObject = this.mArray;
      allocArrays(k);
      if (this.mHashes.length > 0)
      {
        System.arraycopy(arrayOfInt, 0, this.mHashes, 0, arrayOfInt.length);
        System.arraycopy(arrayOfObject, 0, this.mArray, 0, arrayOfObject.length);
      }
      freeArrays(arrayOfInt, arrayOfObject, this.mSize);
      if (m < this.mSize)
      {
        System.arraycopy(this.mHashes, m, this.mHashes, m + 1, this.mSize - m);
        System.arraycopy(this.mArray, m, this.mArray, m + 1, this.mSize - m);
      }
      this.mHashes[m] = j;
      this.mArray[m] = paramE;
      this.mSize += 1;
      bool = true;
      break;
      label236:
      k = i;
      if (this.mSize < 4) {
        k = 4;
      }
    }
  }
  
  public boolean addAll(Collection<? extends E> paramCollection)
  {
    ensureCapacity(this.mSize + paramCollection.size());
    boolean bool = false;
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext()) {
      bool |= add(paramCollection.next());
    }
    return bool;
  }
  
  public void clear()
  {
    if (this.mSize != 0)
    {
      freeArrays(this.mHashes, this.mArray, this.mSize);
      this.mHashes = INT;
      this.mArray = OBJECT;
      this.mSize = 0;
    }
  }
  
  public boolean contains(Object paramObject)
  {
    if (indexOf(paramObject) >= 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean containsAll(Collection<?> paramCollection)
  {
    paramCollection = paramCollection.iterator();
    do
    {
      if (!paramCollection.hasNext()) {
        break;
      }
    } while (contains(paramCollection.next()));
    for (boolean bool = false;; bool = true) {
      return bool;
    }
  }
  
  public void ensureCapacity(int paramInt)
  {
    if (this.mHashes.length < paramInt)
    {
      int[] arrayOfInt = this.mHashes;
      Object[] arrayOfObject = this.mArray;
      allocArrays(paramInt);
      if (this.mSize > 0)
      {
        System.arraycopy(arrayOfInt, 0, this.mHashes, 0, this.mSize);
        System.arraycopy(arrayOfObject, 0, this.mArray, 0, this.mSize);
      }
      freeArrays(arrayOfInt, arrayOfObject, this.mSize);
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
      if ((paramObject instanceof Set))
      {
        paramObject = (Set)paramObject;
        if (size() != ((Set)paramObject).size())
        {
          bool2 = false;
        }
        else
        {
          int i = 0;
          bool2 = bool1;
          try
          {
            if (i >= this.mSize) {
              continue;
            }
            bool2 = ((Set)paramObject).contains(valueAt(i));
            if (!bool2)
            {
              bool2 = false;
              continue;
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
  
  public int hashCode()
  {
    int[] arrayOfInt = this.mHashes;
    int i = 0;
    int j = 0;
    int k = this.mSize;
    while (j < k)
    {
      i += arrayOfInt[j];
      j++;
    }
    return i;
  }
  
  public int indexOf(Object paramObject)
  {
    if (paramObject == null) {}
    for (int i = indexOfNull();; i = indexOf(paramObject, paramObject.hashCode())) {
      return i;
    }
  }
  
  public boolean isEmpty()
  {
    if (this.mSize <= 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public Iterator<E> iterator()
  {
    return getCollection().getKeySet().iterator();
  }
  
  public boolean remove(Object paramObject)
  {
    int i = indexOf(paramObject);
    if (i >= 0) {
      removeAt(i);
    }
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean removeAll(Collection<?> paramCollection)
  {
    boolean bool = false;
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext()) {
      bool |= remove(paramCollection.next());
    }
    return bool;
  }
  
  public E removeAt(int paramInt)
  {
    int i = 8;
    Object localObject = this.mArray[paramInt];
    if (this.mSize <= 1)
    {
      freeArrays(this.mHashes, this.mArray, this.mSize);
      this.mHashes = INT;
      this.mArray = OBJECT;
      this.mSize = 0;
    }
    for (;;)
    {
      return (E)localObject;
      if ((this.mHashes.length > 8) && (this.mSize < this.mHashes.length / 3))
      {
        if (this.mSize > 8) {
          i = this.mSize + (this.mSize >> 1);
        }
        int[] arrayOfInt = this.mHashes;
        Object[] arrayOfObject = this.mArray;
        allocArrays(i);
        this.mSize -= 1;
        if (paramInt > 0)
        {
          System.arraycopy(arrayOfInt, 0, this.mHashes, 0, paramInt);
          System.arraycopy(arrayOfObject, 0, this.mArray, 0, paramInt);
        }
        if (paramInt < this.mSize)
        {
          System.arraycopy(arrayOfInt, paramInt + 1, this.mHashes, paramInt, this.mSize - paramInt);
          System.arraycopy(arrayOfObject, paramInt + 1, this.mArray, paramInt, this.mSize - paramInt);
        }
      }
      else
      {
        this.mSize -= 1;
        if (paramInt < this.mSize)
        {
          System.arraycopy(this.mHashes, paramInt + 1, this.mHashes, paramInt, this.mSize - paramInt);
          System.arraycopy(this.mArray, paramInt + 1, this.mArray, paramInt, this.mSize - paramInt);
        }
        this.mArray[this.mSize] = null;
      }
    }
  }
  
  public boolean retainAll(Collection<?> paramCollection)
  {
    boolean bool = false;
    for (int i = this.mSize - 1; i >= 0; i--) {
      if (!paramCollection.contains(this.mArray[i]))
      {
        removeAt(i);
        bool = true;
      }
    }
    return bool;
  }
  
  public int size()
  {
    return this.mSize;
  }
  
  public Object[] toArray()
  {
    Object[] arrayOfObject = new Object[this.mSize];
    System.arraycopy(this.mArray, 0, arrayOfObject, 0, this.mSize);
    return arrayOfObject;
  }
  
  public <T> T[] toArray(T[] paramArrayOfT)
  {
    Object localObject = paramArrayOfT;
    if (paramArrayOfT.length < this.mSize) {
      localObject = (Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), this.mSize);
    }
    System.arraycopy(this.mArray, 0, localObject, 0, this.mSize);
    if (localObject.length > this.mSize) {
      localObject[this.mSize] = null;
    }
    return (T[])localObject;
  }
  
  public String toString()
  {
    if (isEmpty()) {}
    for (Object localObject1 = "{}";; localObject1 = ((StringBuilder)localObject1).toString())
    {
      return (String)localObject1;
      localObject1 = new StringBuilder(this.mSize * 14);
      ((StringBuilder)localObject1).append('{');
      int i = 0;
      if (i < this.mSize)
      {
        if (i > 0) {
          ((StringBuilder)localObject1).append(", ");
        }
        Object localObject2 = valueAt(i);
        if (localObject2 != this) {
          ((StringBuilder)localObject1).append(localObject2);
        }
        for (;;)
        {
          i++;
          break;
          ((StringBuilder)localObject1).append("(this Set)");
        }
      }
      ((StringBuilder)localObject1).append('}');
    }
  }
  
  public E valueAt(int paramInt)
  {
    return (E)this.mArray[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/util/ArraySet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */