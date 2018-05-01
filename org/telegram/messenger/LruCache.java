package org.telegram.messenger;

import android.graphics.drawable.BitmapDrawable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class LruCache
{
  private final LinkedHashMap<String, BitmapDrawable> map;
  private final LinkedHashMap<String, ArrayList<String>> mapFilters;
  private int maxSize;
  private int size;
  
  public LruCache(int paramInt)
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException("maxSize <= 0");
    }
    this.maxSize = paramInt;
    this.map = new LinkedHashMap(0, 0.75F, true);
    this.mapFilters = new LinkedHashMap();
  }
  
  private int safeSizeOf(String paramString, BitmapDrawable paramBitmapDrawable)
  {
    int i = sizeOf(paramString, paramBitmapDrawable);
    if (i < 0) {
      throw new IllegalStateException("Negative size: " + paramString + "=" + paramBitmapDrawable);
    }
    return i;
  }
  
  /* Error */
  private void trimToSize(int paramInt, String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 34	org/telegram/messenger/LruCache:map	Ljava/util/LinkedHashMap;
    //   6: invokevirtual 70	java/util/LinkedHashMap:entrySet	()Ljava/util/Set;
    //   9: invokeinterface 76 1 0
    //   14: astore_3
    //   15: aload_3
    //   16: invokeinterface 82 1 0
    //   21: ifeq +21 -> 42
    //   24: aload_0
    //   25: getfield 84	org/telegram/messenger/LruCache:size	I
    //   28: iload_1
    //   29: if_icmple +13 -> 42
    //   32: aload_0
    //   33: getfield 34	org/telegram/messenger/LruCache:map	Ljava/util/LinkedHashMap;
    //   36: invokevirtual 87	java/util/LinkedHashMap:isEmpty	()Z
    //   39: ifeq +6 -> 45
    //   42: aload_0
    //   43: monitorexit
    //   44: return
    //   45: aload_3
    //   46: invokeinterface 91 1 0
    //   51: checkcast 93	java/util/Map$Entry
    //   54: astore 4
    //   56: aload 4
    //   58: invokeinterface 96 1 0
    //   63: checkcast 98	java/lang/String
    //   66: astore 5
    //   68: aload_2
    //   69: ifnull +12 -> 81
    //   72: aload_2
    //   73: aload 5
    //   75: invokevirtual 102	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   78: ifne -63 -> 15
    //   81: aload 4
    //   83: invokeinterface 105 1 0
    //   88: checkcast 107	android/graphics/drawable/BitmapDrawable
    //   91: astore 6
    //   93: aload_0
    //   94: aload_0
    //   95: getfield 84	org/telegram/messenger/LruCache:size	I
    //   98: aload_0
    //   99: aload 5
    //   101: aload 6
    //   103: invokespecial 109	org/telegram/messenger/LruCache:safeSizeOf	(Ljava/lang/String;Landroid/graphics/drawable/BitmapDrawable;)I
    //   106: isub
    //   107: putfield 84	org/telegram/messenger/LruCache:size	I
    //   110: aload_3
    //   111: invokeinterface 112 1 0
    //   116: aload 5
    //   118: ldc 114
    //   120: invokevirtual 118	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
    //   123: astore 4
    //   125: aload 4
    //   127: arraylength
    //   128: iconst_1
    //   129: if_icmple +54 -> 183
    //   132: aload_0
    //   133: getfield 37	org/telegram/messenger/LruCache:mapFilters	Ljava/util/LinkedHashMap;
    //   136: aload 4
    //   138: iconst_0
    //   139: aaload
    //   140: invokevirtual 122	java/util/LinkedHashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   143: checkcast 124	java/util/ArrayList
    //   146: astore 7
    //   148: aload 7
    //   150: ifnull +33 -> 183
    //   153: aload 7
    //   155: aload 4
    //   157: iconst_1
    //   158: aaload
    //   159: invokevirtual 126	java/util/ArrayList:remove	(Ljava/lang/Object;)Z
    //   162: pop
    //   163: aload 7
    //   165: invokevirtual 127	java/util/ArrayList:isEmpty	()Z
    //   168: ifeq +15 -> 183
    //   171: aload_0
    //   172: getfield 37	org/telegram/messenger/LruCache:mapFilters	Ljava/util/LinkedHashMap;
    //   175: aload 4
    //   177: iconst_0
    //   178: aaload
    //   179: invokevirtual 129	java/util/LinkedHashMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   182: pop
    //   183: aload_0
    //   184: iconst_1
    //   185: aload 5
    //   187: aload 6
    //   189: aconst_null
    //   190: invokevirtual 133	org/telegram/messenger/LruCache:entryRemoved	(ZLjava/lang/String;Landroid/graphics/drawable/BitmapDrawable;Landroid/graphics/drawable/BitmapDrawable;)V
    //   193: goto -178 -> 15
    //   196: astore_2
    //   197: aload_0
    //   198: monitorexit
    //   199: aload_2
    //   200: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	201	0	this	LruCache
    //   0	201	1	paramInt	int
    //   0	201	2	paramString	String
    //   14	97	3	localIterator	java.util.Iterator
    //   54	122	4	localObject	Object
    //   66	120	5	str	String
    //   91	97	6	localBitmapDrawable	BitmapDrawable
    //   146	18	7	localArrayList	ArrayList
    // Exception table:
    //   from	to	target	type
    //   2	15	196	finally
    //   15	42	196	finally
    //   42	44	196	finally
    //   45	68	196	finally
    //   72	81	196	finally
    //   81	148	196	finally
    //   153	183	196	finally
    //   183	193	196	finally
    //   197	199	196	finally
  }
  
  public boolean contains(String paramString)
  {
    return this.map.containsKey(paramString);
  }
  
  protected void entryRemoved(boolean paramBoolean, String paramString, BitmapDrawable paramBitmapDrawable1, BitmapDrawable paramBitmapDrawable2) {}
  
  public final void evictAll()
  {
    trimToSize(-1, null);
  }
  
  /* Error */
  public final BitmapDrawable get(String paramString)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +13 -> 14
    //   4: new 144	java/lang/NullPointerException
    //   7: dup
    //   8: ldc -110
    //   10: invokespecial 147	java/lang/NullPointerException:<init>	(Ljava/lang/String;)V
    //   13: athrow
    //   14: aload_0
    //   15: monitorenter
    //   16: aload_0
    //   17: getfield 34	org/telegram/messenger/LruCache:map	Ljava/util/LinkedHashMap;
    //   20: aload_1
    //   21: invokevirtual 122	java/util/LinkedHashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   24: checkcast 107	android/graphics/drawable/BitmapDrawable
    //   27: astore_1
    //   28: aload_1
    //   29: ifnull +7 -> 36
    //   32: aload_0
    //   33: monitorexit
    //   34: aload_1
    //   35: areturn
    //   36: aload_0
    //   37: monitorexit
    //   38: aconst_null
    //   39: astore_1
    //   40: goto -6 -> 34
    //   43: astore_1
    //   44: aload_0
    //   45: monitorexit
    //   46: aload_1
    //   47: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	48	0	this	LruCache
    //   0	48	1	paramString	String
    // Exception table:
    //   from	to	target	type
    //   16	28	43	finally
    //   32	34	43	finally
    //   36	38	43	finally
    //   44	46	43	finally
  }
  
  public ArrayList<String> getFilterKeys(String paramString)
  {
    paramString = (ArrayList)this.mapFilters.get(paramString);
    if (paramString != null) {}
    for (paramString = new ArrayList(paramString);; paramString = null) {
      return paramString;
    }
  }
  
  public final int maxSize()
  {
    try
    {
      int i = this.maxSize;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public BitmapDrawable put(String paramString, BitmapDrawable paramBitmapDrawable)
  {
    if ((paramString == null) || (paramBitmapDrawable == null)) {
      throw new NullPointerException("key == null || value == null");
    }
    try
    {
      this.size += safeSizeOf(paramString, paramBitmapDrawable);
      BitmapDrawable localBitmapDrawable = (BitmapDrawable)this.map.put(paramString, paramBitmapDrawable);
      if (localBitmapDrawable != null) {
        this.size -= safeSizeOf(paramString, localBitmapDrawable);
      }
      String[] arrayOfString = paramString.split("@");
      if (arrayOfString.length > 1)
      {
        ArrayList localArrayList1 = (ArrayList)this.mapFilters.get(arrayOfString[0]);
        ArrayList localArrayList2 = localArrayList1;
        if (localArrayList1 == null)
        {
          localArrayList2 = new ArrayList();
          this.mapFilters.put(arrayOfString[0], localArrayList2);
        }
        if (!localArrayList2.contains(arrayOfString[1])) {
          localArrayList2.add(arrayOfString[1]);
        }
      }
      if (localBitmapDrawable != null) {
        entryRemoved(false, paramString, localBitmapDrawable, paramBitmapDrawable);
      }
      trimToSize(this.maxSize, paramString);
      return localBitmapDrawable;
    }
    finally {}
  }
  
  public final BitmapDrawable remove(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("key == null");
    }
    try
    {
      BitmapDrawable localBitmapDrawable = (BitmapDrawable)this.map.remove(paramString);
      if (localBitmapDrawable != null) {
        this.size -= safeSizeOf(paramString, localBitmapDrawable);
      }
      if (localBitmapDrawable != null)
      {
        String[] arrayOfString = paramString.split("@");
        if (arrayOfString.length > 1)
        {
          ArrayList localArrayList = (ArrayList)this.mapFilters.get(arrayOfString[0]);
          if (localArrayList != null)
          {
            localArrayList.remove(arrayOfString[1]);
            if (localArrayList.isEmpty()) {
              this.mapFilters.remove(arrayOfString[0]);
            }
          }
        }
        entryRemoved(false, paramString, localBitmapDrawable, null);
      }
      return localBitmapDrawable;
    }
    finally {}
  }
  
  public final int size()
  {
    try
    {
      int i = this.size;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  protected int sizeOf(String paramString, BitmapDrawable paramBitmapDrawable)
  {
    return 1;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/LruCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */