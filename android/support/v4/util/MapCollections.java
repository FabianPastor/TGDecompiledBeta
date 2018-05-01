package android.support.v4.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

abstract class MapCollections<K, V>
{
  MapCollections<K, V>.EntrySet mEntrySet;
  MapCollections<K, V>.KeySet mKeySet;
  MapCollections<K, V>.ValuesCollection mValues;
  
  public static <K, V> boolean containsAllHelper(Map<K, V> paramMap, Collection<?> paramCollection)
  {
    paramCollection = paramCollection.iterator();
    do
    {
      if (!paramCollection.hasNext()) {
        break;
      }
    } while (paramMap.containsKey(paramCollection.next()));
    for (boolean bool = false;; bool = true) {
      return bool;
    }
  }
  
  public static <T> boolean equalsSetHelper(Set<T> paramSet, Object paramObject)
  {
    boolean bool1 = true;
    boolean bool2 = false;
    boolean bool3;
    if (paramSet == paramObject) {
      bool3 = true;
    }
    for (;;)
    {
      return bool3;
      bool3 = bool2;
      if ((paramObject instanceof Set))
      {
        paramObject = (Set)paramObject;
        try
        {
          if (paramSet.size() == ((Set)paramObject).size())
          {
            bool3 = paramSet.containsAll((Collection)paramObject);
            if (!bool3) {}
          }
          for (bool3 = bool1;; bool3 = false) {
            break;
          }
        }
        catch (NullPointerException paramSet)
        {
          bool3 = bool2;
        }
        catch (ClassCastException paramSet)
        {
          bool3 = bool2;
        }
      }
    }
  }
  
  public static <K, V> boolean removeAllHelper(Map<K, V> paramMap, Collection<?> paramCollection)
  {
    int i = paramMap.size();
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext()) {
      paramMap.remove(paramCollection.next());
    }
    if (i != paramMap.size()) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static <K, V> boolean retainAllHelper(Map<K, V> paramMap, Collection<?> paramCollection)
  {
    int i = paramMap.size();
    Iterator localIterator = paramMap.keySet().iterator();
    while (localIterator.hasNext()) {
      if (!paramCollection.contains(localIterator.next())) {
        localIterator.remove();
      }
    }
    if (i != paramMap.size()) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  protected abstract void colClear();
  
  protected abstract Object colGetEntry(int paramInt1, int paramInt2);
  
  protected abstract Map<K, V> colGetMap();
  
  protected abstract int colGetSize();
  
  protected abstract int colIndexOfKey(Object paramObject);
  
  protected abstract int colIndexOfValue(Object paramObject);
  
  protected abstract void colPut(K paramK, V paramV);
  
  protected abstract void colRemoveAt(int paramInt);
  
  protected abstract V colSetValue(int paramInt, V paramV);
  
  public Set<Map.Entry<K, V>> getEntrySet()
  {
    if (this.mEntrySet == null) {
      this.mEntrySet = new EntrySet();
    }
    return this.mEntrySet;
  }
  
  public Set<K> getKeySet()
  {
    if (this.mKeySet == null) {
      this.mKeySet = new KeySet();
    }
    return this.mKeySet;
  }
  
  public Collection<V> getValues()
  {
    if (this.mValues == null) {
      this.mValues = new ValuesCollection();
    }
    return this.mValues;
  }
  
  public Object[] toArrayHelper(int paramInt)
  {
    int i = colGetSize();
    Object[] arrayOfObject = new Object[i];
    for (int j = 0; j < i; j++) {
      arrayOfObject[j] = colGetEntry(j, paramInt);
    }
    return arrayOfObject;
  }
  
  public <T> T[] toArrayHelper(T[] paramArrayOfT, int paramInt)
  {
    int i = colGetSize();
    Object localObject = paramArrayOfT;
    if (paramArrayOfT.length < i) {
      localObject = (Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), i);
    }
    for (int j = 0; j < i; j++) {
      localObject[j] = colGetEntry(j, paramInt);
    }
    if (localObject.length > i) {
      localObject[i] = null;
    }
    return (T[])localObject;
  }
  
  final class ArrayIterator<T>
    implements Iterator<T>
  {
    boolean mCanRemove = false;
    int mIndex;
    final int mOffset;
    int mSize;
    
    ArrayIterator(int paramInt)
    {
      this.mOffset = paramInt;
      this.mSize = MapCollections.this.colGetSize();
    }
    
    public boolean hasNext()
    {
      if (this.mIndex < this.mSize) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public T next()
    {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      Object localObject = MapCollections.this.colGetEntry(this.mIndex, this.mOffset);
      this.mIndex += 1;
      this.mCanRemove = true;
      return (T)localObject;
    }
    
    public void remove()
    {
      if (!this.mCanRemove) {
        throw new IllegalStateException();
      }
      this.mIndex -= 1;
      this.mSize -= 1;
      this.mCanRemove = false;
      MapCollections.this.colRemoveAt(this.mIndex);
    }
  }
  
  final class EntrySet
    implements Set<Map.Entry<K, V>>
  {
    EntrySet() {}
    
    public boolean add(Map.Entry<K, V> paramEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(Collection<? extends Map.Entry<K, V>> paramCollection)
    {
      int i = MapCollections.this.colGetSize();
      paramCollection = paramCollection.iterator();
      while (paramCollection.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)paramCollection.next();
        MapCollections.this.colPut(localEntry.getKey(), localEntry.getValue());
      }
      if (i != MapCollections.this.colGetSize()) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void clear()
    {
      MapCollections.this.colClear();
    }
    
    public boolean contains(Object paramObject)
    {
      boolean bool = false;
      if (!(paramObject instanceof Map.Entry)) {}
      for (;;)
      {
        return bool;
        paramObject = (Map.Entry)paramObject;
        int i = MapCollections.this.colIndexOfKey(((Map.Entry)paramObject).getKey());
        if (i >= 0) {
          bool = ContainerHelpers.equal(MapCollections.this.colGetEntry(i, 1), ((Map.Entry)paramObject).getValue());
        }
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
    
    public boolean equals(Object paramObject)
    {
      return MapCollections.equalsSetHelper(this, paramObject);
    }
    
    public int hashCode()
    {
      int i = 0;
      int j = MapCollections.this.colGetSize() - 1;
      if (j >= 0)
      {
        Object localObject1 = MapCollections.this.colGetEntry(j, 0);
        Object localObject2 = MapCollections.this.colGetEntry(j, 1);
        int k;
        if (localObject1 == null)
        {
          k = 0;
          label44:
          if (localObject2 != null) {
            break label75;
          }
        }
        label75:
        for (int m = 0;; m = localObject2.hashCode())
        {
          i += (m ^ k);
          j--;
          break;
          k = localObject1.hashCode();
          break label44;
        }
      }
      return i;
    }
    
    public boolean isEmpty()
    {
      if (MapCollections.this.colGetSize() == 0) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public Iterator<Map.Entry<K, V>> iterator()
    {
      return new MapCollections.MapIterator(MapCollections.this);
    }
    
    public boolean remove(Object paramObject)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean removeAll(Collection<?> paramCollection)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(Collection<?> paramCollection)
    {
      throw new UnsupportedOperationException();
    }
    
    public int size()
    {
      return MapCollections.this.colGetSize();
    }
    
    public Object[] toArray()
    {
      throw new UnsupportedOperationException();
    }
    
    public <T> T[] toArray(T[] paramArrayOfT)
    {
      throw new UnsupportedOperationException();
    }
  }
  
  final class KeySet
    implements Set<K>
  {
    KeySet() {}
    
    public boolean add(K paramK)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(Collection<? extends K> paramCollection)
    {
      throw new UnsupportedOperationException();
    }
    
    public void clear()
    {
      MapCollections.this.colClear();
    }
    
    public boolean contains(Object paramObject)
    {
      if (MapCollections.this.colIndexOfKey(paramObject) >= 0) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public boolean containsAll(Collection<?> paramCollection)
    {
      return MapCollections.containsAllHelper(MapCollections.this.colGetMap(), paramCollection);
    }
    
    public boolean equals(Object paramObject)
    {
      return MapCollections.equalsSetHelper(this, paramObject);
    }
    
    public int hashCode()
    {
      int i = 0;
      int j = MapCollections.this.colGetSize() - 1;
      if (j >= 0)
      {
        Object localObject = MapCollections.this.colGetEntry(j, 0);
        if (localObject == null) {}
        for (int k = 0;; k = localObject.hashCode())
        {
          i += k;
          j--;
          break;
        }
      }
      return i;
    }
    
    public boolean isEmpty()
    {
      if (MapCollections.this.colGetSize() == 0) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public Iterator<K> iterator()
    {
      return new MapCollections.ArrayIterator(MapCollections.this, 0);
    }
    
    public boolean remove(Object paramObject)
    {
      int i = MapCollections.this.colIndexOfKey(paramObject);
      if (i >= 0) {
        MapCollections.this.colRemoveAt(i);
      }
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public boolean removeAll(Collection<?> paramCollection)
    {
      return MapCollections.removeAllHelper(MapCollections.this.colGetMap(), paramCollection);
    }
    
    public boolean retainAll(Collection<?> paramCollection)
    {
      return MapCollections.retainAllHelper(MapCollections.this.colGetMap(), paramCollection);
    }
    
    public int size()
    {
      return MapCollections.this.colGetSize();
    }
    
    public Object[] toArray()
    {
      return MapCollections.this.toArrayHelper(0);
    }
    
    public <T> T[] toArray(T[] paramArrayOfT)
    {
      return MapCollections.this.toArrayHelper(paramArrayOfT, 0);
    }
  }
  
  final class MapIterator
    implements Iterator<Map.Entry<K, V>>, Map.Entry<K, V>
  {
    int mEnd = MapCollections.this.colGetSize() - 1;
    boolean mEntryValid = false;
    int mIndex = -1;
    
    MapIterator() {}
    
    public boolean equals(Object paramObject)
    {
      boolean bool1 = true;
      boolean bool2 = false;
      if (!this.mEntryValid) {
        throw new IllegalStateException("This container does not support retaining Map.Entry objects");
      }
      if (!(paramObject instanceof Map.Entry)) {
        return bool2;
      }
      paramObject = (Map.Entry)paramObject;
      if ((ContainerHelpers.equal(((Map.Entry)paramObject).getKey(), MapCollections.this.colGetEntry(this.mIndex, 0))) && (ContainerHelpers.equal(((Map.Entry)paramObject).getValue(), MapCollections.this.colGetEntry(this.mIndex, 1)))) {}
      for (bool2 = bool1;; bool2 = false) {
        break;
      }
    }
    
    public K getKey()
    {
      if (!this.mEntryValid) {
        throw new IllegalStateException("This container does not support retaining Map.Entry objects");
      }
      return (K)MapCollections.this.colGetEntry(this.mIndex, 0);
    }
    
    public V getValue()
    {
      if (!this.mEntryValid) {
        throw new IllegalStateException("This container does not support retaining Map.Entry objects");
      }
      return (V)MapCollections.this.colGetEntry(this.mIndex, 1);
    }
    
    public boolean hasNext()
    {
      if (this.mIndex < this.mEnd) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public int hashCode()
    {
      int i = 0;
      if (!this.mEntryValid) {
        throw new IllegalStateException("This container does not support retaining Map.Entry objects");
      }
      Object localObject1 = MapCollections.this.colGetEntry(this.mIndex, 0);
      Object localObject2 = MapCollections.this.colGetEntry(this.mIndex, 1);
      int j;
      if (localObject1 == null)
      {
        j = 0;
        if (localObject2 != null) {
          break label70;
        }
      }
      for (;;)
      {
        return i ^ j;
        j = localObject1.hashCode();
        break;
        label70:
        i = localObject2.hashCode();
      }
    }
    
    public Map.Entry<K, V> next()
    {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      this.mIndex += 1;
      this.mEntryValid = true;
      return this;
    }
    
    public void remove()
    {
      if (!this.mEntryValid) {
        throw new IllegalStateException();
      }
      MapCollections.this.colRemoveAt(this.mIndex);
      this.mIndex -= 1;
      this.mEnd -= 1;
      this.mEntryValid = false;
    }
    
    public V setValue(V paramV)
    {
      if (!this.mEntryValid) {
        throw new IllegalStateException("This container does not support retaining Map.Entry objects");
      }
      return (V)MapCollections.this.colSetValue(this.mIndex, paramV);
    }
    
    public String toString()
    {
      return getKey() + "=" + getValue();
    }
  }
  
  final class ValuesCollection
    implements Collection<V>
  {
    ValuesCollection() {}
    
    public boolean add(V paramV)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(Collection<? extends V> paramCollection)
    {
      throw new UnsupportedOperationException();
    }
    
    public void clear()
    {
      MapCollections.this.colClear();
    }
    
    public boolean contains(Object paramObject)
    {
      if (MapCollections.this.colIndexOfValue(paramObject) >= 0) {}
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
    
    public boolean isEmpty()
    {
      if (MapCollections.this.colGetSize() == 0) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public Iterator<V> iterator()
    {
      return new MapCollections.ArrayIterator(MapCollections.this, 1);
    }
    
    public boolean remove(Object paramObject)
    {
      int i = MapCollections.this.colIndexOfValue(paramObject);
      if (i >= 0) {
        MapCollections.this.colRemoveAt(i);
      }
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public boolean removeAll(Collection<?> paramCollection)
    {
      int i = MapCollections.this.colGetSize();
      boolean bool = false;
      int j = 0;
      while (j < i)
      {
        int k = i;
        int m = j;
        if (paramCollection.contains(MapCollections.this.colGetEntry(j, 1)))
        {
          MapCollections.this.colRemoveAt(j);
          m = j - 1;
          k = i - 1;
          bool = true;
        }
        j = m + 1;
        i = k;
      }
      return bool;
    }
    
    public boolean retainAll(Collection<?> paramCollection)
    {
      int i = MapCollections.this.colGetSize();
      boolean bool = false;
      int j = 0;
      while (j < i)
      {
        int k = i;
        int m = j;
        if (!paramCollection.contains(MapCollections.this.colGetEntry(j, 1)))
        {
          MapCollections.this.colRemoveAt(j);
          m = j - 1;
          k = i - 1;
          bool = true;
        }
        j = m + 1;
        i = k;
      }
      return bool;
    }
    
    public int size()
    {
      return MapCollections.this.colGetSize();
    }
    
    public Object[] toArray()
    {
      return MapCollections.this.toArrayHelper(1);
    }
    
    public <T> T[] toArray(T[] paramArrayOfT)
    {
      return MapCollections.this.toArrayHelper(paramArrayOfT, 1);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/util/MapCollections.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */