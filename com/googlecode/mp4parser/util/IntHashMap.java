package com.googlecode.mp4parser.util;

public class IntHashMap
{
  private transient int count;
  private float loadFactor;
  private transient Entry[] table;
  private int threshold;
  
  public IntHashMap()
  {
    this(20, 0.75F);
  }
  
  public IntHashMap(int paramInt)
  {
    this(paramInt, 0.75F);
  }
  
  public IntHashMap(int paramInt, float paramFloat)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Illegal Capacity: " + paramInt);
    }
    if (paramFloat <= 0.0F) {
      throw new IllegalArgumentException("Illegal Load: " + paramFloat);
    }
    int i = paramInt;
    if (paramInt == 0) {
      i = 1;
    }
    this.loadFactor = paramFloat;
    this.table = new Entry[i];
    this.threshold = ((int)(i * paramFloat));
  }
  
  /* Error */
  public void clear()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 51	com/googlecode/mp4parser/util/IntHashMap:table	[Lcom/googlecode/mp4parser/util/IntHashMap$Entry;
    //   6: astore_2
    //   7: aload_2
    //   8: arraylength
    //   9: istore_1
    //   10: iload_1
    //   11: iconst_1
    //   12: isub
    //   13: istore_1
    //   14: iload_1
    //   15: ifge +11 -> 26
    //   18: aload_0
    //   19: iconst_0
    //   20: putfield 56	com/googlecode/mp4parser/util/IntHashMap:count	I
    //   23: aload_0
    //   24: monitorexit
    //   25: return
    //   26: aload_2
    //   27: iload_1
    //   28: aconst_null
    //   29: aastore
    //   30: goto -20 -> 10
    //   33: astore_2
    //   34: aload_0
    //   35: monitorexit
    //   36: aload_2
    //   37: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	38	0	this	IntHashMap
    //   9	19	1	i	int
    //   6	21	2	arrayOfEntry	Entry[]
    //   33	4	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	10	33	finally
    //   18	23	33	finally
  }
  
  public boolean contains(Object paramObject)
  {
    if (paramObject == null) {
      throw new NullPointerException();
    }
    Entry[] arrayOfEntry = this.table;
    int i = arrayOfEntry.length;
    int j = i - 1;
    if (i <= 0) {
      return false;
    }
    for (Entry localEntry = arrayOfEntry[j];; localEntry = localEntry.next)
    {
      if (localEntry == null)
      {
        i = j;
        break;
      }
      if (localEntry.value.equals(paramObject)) {
        return true;
      }
    }
  }
  
  public boolean containsKey(int paramInt)
  {
    Object localObject = this.table;
    for (localObject = localObject[((0x7FFFFFFF & paramInt) % localObject.length)];; localObject = ((Entry)localObject).next)
    {
      if (localObject == null) {
        return false;
      }
      if (((Entry)localObject).hash == paramInt) {
        return true;
      }
    }
  }
  
  public boolean containsValue(Object paramObject)
  {
    return contains(paramObject);
  }
  
  public Object get(int paramInt)
  {
    Object localObject = this.table;
    for (localObject = localObject[((0x7FFFFFFF & paramInt) % localObject.length)];; localObject = ((Entry)localObject).next)
    {
      if (localObject == null) {
        return null;
      }
      if (((Entry)localObject).hash == paramInt) {
        return ((Entry)localObject).value;
      }
    }
  }
  
  public boolean isEmpty()
  {
    return this.count == 0;
  }
  
  public Object put(int paramInt, Object paramObject)
  {
    Object localObject2 = this.table;
    int i = (paramInt & 0x7FFFFFFF) % localObject2.length;
    for (Object localObject1 = localObject2[i];; localObject1 = ((Entry)localObject1).next)
    {
      if (localObject1 == null)
      {
        localObject1 = localObject2;
        if (this.count >= this.threshold)
        {
          rehash();
          localObject1 = this.table;
          i = (paramInt & 0x7FFFFFFF) % localObject1.length;
        }
        localObject1[i] = new Entry(paramInt, paramInt, paramObject, localObject1[i]);
        this.count += 1;
        return null;
      }
      if (((Entry)localObject1).hash == paramInt)
      {
        localObject2 = ((Entry)localObject1).value;
        ((Entry)localObject1).value = paramObject;
        return localObject2;
      }
    }
  }
  
  protected void rehash()
  {
    int i = this.table.length;
    Entry[] arrayOfEntry1 = this.table;
    int k = i * 2 + 1;
    Entry[] arrayOfEntry2 = new Entry[k];
    this.threshold = ((int)(k * this.loadFactor));
    this.table = arrayOfEntry2;
    int j = i - 1;
    if (i <= 0) {
      return;
    }
    Entry localEntry;
    for (Object localObject = arrayOfEntry1[j];; localObject = localEntry)
    {
      if (localObject == null)
      {
        i = j;
        break;
      }
      localEntry = ((Entry)localObject).next;
      i = (((Entry)localObject).hash & 0x7FFFFFFF) % k;
      ((Entry)localObject).next = arrayOfEntry2[i];
      arrayOfEntry2[i] = localObject;
    }
  }
  
  public Object remove(int paramInt)
  {
    Entry[] arrayOfEntry = this.table;
    int i = (0x7FFFFFFF & paramInt) % arrayOfEntry.length;
    Entry localEntry = arrayOfEntry[i];
    Object localObject = null;
    for (;;)
    {
      if (localEntry == null) {
        return null;
      }
      if (localEntry.hash == paramInt)
      {
        if (localObject != null) {
          ((Entry)localObject).next = localEntry.next;
        }
        for (;;)
        {
          this.count -= 1;
          localObject = localEntry.value;
          localEntry.value = null;
          return localObject;
          arrayOfEntry[i] = localEntry.next;
        }
      }
      localObject = localEntry;
      localEntry = localEntry.next;
    }
  }
  
  public int size()
  {
    return this.count;
  }
  
  private static class Entry
  {
    int hash;
    int key;
    Entry next;
    Object value;
    
    protected Entry(int paramInt1, int paramInt2, Object paramObject, Entry paramEntry)
    {
      this.hash = paramInt1;
      this.key = paramInt2;
      this.value = paramObject;
      this.next = paramEntry;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/util/IntHashMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */