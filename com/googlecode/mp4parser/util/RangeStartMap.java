package com.googlecode.mp4parser.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class RangeStartMap<K extends Comparable, V>
  implements Map<K, V>
{
  TreeMap<K, V> base = new TreeMap(new Comparator()
  {
    public int compare(K paramAnonymousK1, K paramAnonymousK2)
    {
      return -paramAnonymousK1.compareTo(paramAnonymousK2);
    }
  });
  
  public RangeStartMap() {}
  
  public RangeStartMap(K paramK, V paramV)
  {
    put(paramK, paramV);
  }
  
  public void clear()
  {
    this.base.clear();
  }
  
  public boolean containsKey(Object paramObject)
  {
    return this.base.get(paramObject) != null;
  }
  
  public boolean containsValue(Object paramObject)
  {
    return false;
  }
  
  public Set<Map.Entry<K, V>> entrySet()
  {
    return this.base.entrySet();
  }
  
  public V get(Object paramObject)
  {
    if (!(paramObject instanceof Comparable)) {}
    Comparable localComparable;
    do
    {
      return null;
      localComparable = (Comparable)paramObject;
    } while (isEmpty());
    Iterator localIterator = this.base.keySet().iterator();
    paramObject = (Comparable)localIterator.next();
    while (localIterator.hasNext()) {
      if (localComparable.compareTo(paramObject) < 0) {
        paramObject = (Comparable)localIterator.next();
      } else {
        return (V)this.base.get(paramObject);
      }
    }
    return (V)this.base.get(paramObject);
  }
  
  public boolean isEmpty()
  {
    return this.base.isEmpty();
  }
  
  public Set<K> keySet()
  {
    return this.base.keySet();
  }
  
  public V put(K paramK, V paramV)
  {
    return (V)this.base.put(paramK, paramV);
  }
  
  public void putAll(Map<? extends K, ? extends V> paramMap)
  {
    this.base.putAll(paramMap);
  }
  
  public V remove(Object paramObject)
  {
    if (!(paramObject instanceof Comparable)) {}
    Comparable localComparable;
    do
    {
      return null;
      localComparable = (Comparable)paramObject;
    } while (isEmpty());
    Iterator localIterator = this.base.keySet().iterator();
    paramObject = (Comparable)localIterator.next();
    while (localIterator.hasNext()) {
      if (localComparable.compareTo(paramObject) < 0) {
        paramObject = (Comparable)localIterator.next();
      } else {
        return (V)this.base.remove(paramObject);
      }
    }
    return (V)this.base.remove(paramObject);
  }
  
  public int size()
  {
    return this.base.size();
  }
  
  public Collection<V> values()
  {
    return this.base.values();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/util/RangeStartMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */