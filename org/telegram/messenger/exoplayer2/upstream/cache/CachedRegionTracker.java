package org.telegram.messenger.exoplayer2.upstream.cache;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;
import org.telegram.messenger.exoplayer2.extractor.ChunkIndex;

public final class CachedRegionTracker
  implements Cache.Listener
{
  public static final int CACHED_TO_END = -2;
  public static final int NOT_CACHED = -1;
  private static final String TAG = "CachedRegionTracker";
  private final Cache cache;
  private final String cacheKey;
  private final ChunkIndex chunkIndex;
  private final Region lookupRegion;
  private final TreeSet<Region> regions;
  
  public CachedRegionTracker(Cache paramCache, String paramString, ChunkIndex paramChunkIndex)
  {
    this.cache = paramCache;
    this.cacheKey = paramString;
    this.chunkIndex = paramChunkIndex;
    this.regions = new TreeSet();
    this.lookupRegion = new Region(0L, 0L);
    try
    {
      paramCache = paramCache.addListener(paramString, this).descendingIterator();
      while (paramCache.hasNext()) {
        mergeSpan((CacheSpan)paramCache.next());
      }
    }
    finally {}
  }
  
  private void mergeSpan(CacheSpan paramCacheSpan)
  {
    Region localRegion1 = new Region(paramCacheSpan.position, paramCacheSpan.position + paramCacheSpan.length);
    Region localRegion2 = (Region)this.regions.floor(localRegion1);
    paramCacheSpan = (Region)this.regions.ceiling(localRegion1);
    boolean bool = regionsConnect(localRegion2, localRegion1);
    if (regionsConnect(localRegion1, paramCacheSpan)) {
      if (bool)
      {
        localRegion2.endOffset = paramCacheSpan.endOffset;
        localRegion2.endOffsetIndex = paramCacheSpan.endOffsetIndex;
        this.regions.remove(paramCacheSpan);
      }
    }
    for (;;)
    {
      return;
      localRegion1.endOffset = paramCacheSpan.endOffset;
      localRegion1.endOffsetIndex = paramCacheSpan.endOffsetIndex;
      this.regions.add(localRegion1);
      break;
      int i;
      if (bool)
      {
        localRegion2.endOffset = localRegion1.endOffset;
        for (i = localRegion2.endOffsetIndex; (i < this.chunkIndex.length - 1) && (this.chunkIndex.offsets[(i + 1)] <= localRegion2.endOffset); i++) {}
        localRegion2.endOffsetIndex = i;
      }
      else
      {
        int j = Arrays.binarySearch(this.chunkIndex.offsets, localRegion1.endOffset);
        i = j;
        if (j < 0) {
          i = -j - 2;
        }
        localRegion1.endOffsetIndex = i;
        this.regions.add(localRegion1);
      }
    }
  }
  
  private boolean regionsConnect(Region paramRegion1, Region paramRegion2)
  {
    if ((paramRegion1 != null) && (paramRegion2 != null) && (paramRegion1.endOffset == paramRegion2.startOffset)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  /* Error */
  public int getRegionEndTimeMs(long paramLong)
  {
    // Byte code:
    //   0: iconst_m1
    //   1: istore_3
    //   2: aload_0
    //   3: monitorenter
    //   4: aload_0
    //   5: getfield 49	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker:lookupRegion	Lorg/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker$Region;
    //   8: lload_1
    //   9: putfield 126	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker$Region:startOffset	J
    //   12: aload_0
    //   13: getfield 44	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker:regions	Ljava/util/TreeSet;
    //   16: aload_0
    //   17: getfield 49	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker:lookupRegion	Lorg/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker$Region;
    //   20: invokevirtual 89	java/util/TreeSet:floor	(Ljava/lang/Object;)Ljava/lang/Object;
    //   23: checkcast 8	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker$Region
    //   26: astore 4
    //   28: iload_3
    //   29: istore 5
    //   31: aload 4
    //   33: ifnull +32 -> 65
    //   36: iload_3
    //   37: istore 5
    //   39: lload_1
    //   40: aload 4
    //   42: getfield 99	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker$Region:endOffset	J
    //   45: lcmp
    //   46: ifgt +19 -> 65
    //   49: aload 4
    //   51: getfield 102	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker$Region:endOffsetIndex	I
    //   54: istore 5
    //   56: iload 5
    //   58: iconst_m1
    //   59: if_icmpne +11 -> 70
    //   62: iload_3
    //   63: istore 5
    //   65: aload_0
    //   66: monitorexit
    //   67: iload 5
    //   69: ireturn
    //   70: aload 4
    //   72: getfield 102	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker$Region:endOffsetIndex	I
    //   75: istore 5
    //   77: iload 5
    //   79: aload_0
    //   80: getfield 39	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker:chunkIndex	Lorg/telegram/messenger/exoplayer2/extractor/ChunkIndex;
    //   83: getfield 113	org/telegram/messenger/exoplayer2/extractor/ChunkIndex:length	I
    //   86: iconst_1
    //   87: isub
    //   88: if_icmpne +41 -> 129
    //   91: aload 4
    //   93: getfield 99	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker$Region:endOffset	J
    //   96: aload_0
    //   97: getfield 39	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker:chunkIndex	Lorg/telegram/messenger/exoplayer2/extractor/ChunkIndex;
    //   100: getfield 117	org/telegram/messenger/exoplayer2/extractor/ChunkIndex:offsets	[J
    //   103: iload 5
    //   105: laload
    //   106: aload_0
    //   107: getfield 39	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker:chunkIndex	Lorg/telegram/messenger/exoplayer2/extractor/ChunkIndex;
    //   110: getfield 132	org/telegram/messenger/exoplayer2/extractor/ChunkIndex:sizes	[I
    //   113: iload 5
    //   115: iaload
    //   116: i2l
    //   117: ladd
    //   118: lcmp
    //   119: ifne +10 -> 129
    //   122: bipush -2
    //   124: istore 5
    //   126: goto -61 -> 65
    //   129: aload_0
    //   130: getfield 39	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker:chunkIndex	Lorg/telegram/messenger/exoplayer2/extractor/ChunkIndex;
    //   133: getfield 135	org/telegram/messenger/exoplayer2/extractor/ChunkIndex:durationsUs	[J
    //   136: iload 5
    //   138: laload
    //   139: aload 4
    //   141: getfield 99	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker$Region:endOffset	J
    //   144: aload_0
    //   145: getfield 39	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker:chunkIndex	Lorg/telegram/messenger/exoplayer2/extractor/ChunkIndex;
    //   148: getfield 117	org/telegram/messenger/exoplayer2/extractor/ChunkIndex:offsets	[J
    //   151: iload 5
    //   153: laload
    //   154: lsub
    //   155: lmul
    //   156: aload_0
    //   157: getfield 39	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker:chunkIndex	Lorg/telegram/messenger/exoplayer2/extractor/ChunkIndex;
    //   160: getfield 132	org/telegram/messenger/exoplayer2/extractor/ChunkIndex:sizes	[I
    //   163: iload 5
    //   165: iaload
    //   166: i2l
    //   167: ldiv
    //   168: lstore_1
    //   169: aload_0
    //   170: getfield 39	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker:chunkIndex	Lorg/telegram/messenger/exoplayer2/extractor/ChunkIndex;
    //   173: getfield 138	org/telegram/messenger/exoplayer2/extractor/ChunkIndex:timesUs	[J
    //   176: iload 5
    //   178: laload
    //   179: lload_1
    //   180: ladd
    //   181: ldc2_w 139
    //   184: ldiv
    //   185: lstore_1
    //   186: lload_1
    //   187: l2i
    //   188: istore 5
    //   190: goto -125 -> 65
    //   193: astore 4
    //   195: aload_0
    //   196: monitorexit
    //   197: aload 4
    //   199: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	200	0	this	CachedRegionTracker
    //   0	200	1	paramLong	long
    //   1	62	3	i	int
    //   26	114	4	localRegion	Region
    //   193	5	4	localObject	Object
    //   29	160	5	j	int
    // Exception table:
    //   from	to	target	type
    //   4	28	193	finally
    //   39	56	193	finally
    //   70	122	193	finally
    //   129	186	193	finally
  }
  
  public void onSpanAdded(Cache paramCache, CacheSpan paramCacheSpan)
  {
    try
    {
      mergeSpan(paramCacheSpan);
      return;
    }
    finally
    {
      paramCache = finally;
      throw paramCache;
    }
  }
  
  /* Error */
  public void onSpanRemoved(Cache paramCache, CacheSpan paramCacheSpan)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: new 8	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker$Region
    //   5: astore_1
    //   6: aload_1
    //   7: aload_2
    //   8: getfield 82	org/telegram/messenger/exoplayer2/upstream/cache/CacheSpan:position	J
    //   11: aload_2
    //   12: getfield 82	org/telegram/messenger/exoplayer2/upstream/cache/CacheSpan:position	J
    //   15: aload_2
    //   16: getfield 85	org/telegram/messenger/exoplayer2/upstream/cache/CacheSpan:length	J
    //   19: ladd
    //   20: invokespecial 47	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker$Region:<init>	(JJ)V
    //   23: aload_0
    //   24: getfield 44	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker:regions	Ljava/util/TreeSet;
    //   27: aload_1
    //   28: invokevirtual 89	java/util/TreeSet:floor	(Ljava/lang/Object;)Ljava/lang/Object;
    //   31: checkcast 8	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker$Region
    //   34: astore_2
    //   35: aload_2
    //   36: ifnonnull +14 -> 50
    //   39: ldc 18
    //   41: ldc -111
    //   43: invokestatic 151	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   46: pop
    //   47: aload_0
    //   48: monitorexit
    //   49: return
    //   50: aload_0
    //   51: getfield 44	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker:regions	Ljava/util/TreeSet;
    //   54: aload_2
    //   55: invokevirtual 106	java/util/TreeSet:remove	(Ljava/lang/Object;)Z
    //   58: pop
    //   59: aload_2
    //   60: getfield 126	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker$Region:startOffset	J
    //   63: aload_1
    //   64: getfield 126	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker$Region:startOffset	J
    //   67: lcmp
    //   68: ifge +66 -> 134
    //   71: new 8	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker$Region
    //   74: astore_3
    //   75: aload_3
    //   76: aload_2
    //   77: getfield 126	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker$Region:startOffset	J
    //   80: aload_1
    //   81: getfield 126	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker$Region:startOffset	J
    //   84: invokespecial 47	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker$Region:<init>	(JJ)V
    //   87: aload_0
    //   88: getfield 39	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker:chunkIndex	Lorg/telegram/messenger/exoplayer2/extractor/ChunkIndex;
    //   91: getfield 117	org/telegram/messenger/exoplayer2/extractor/ChunkIndex:offsets	[J
    //   94: aload_3
    //   95: getfield 99	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker$Region:endOffset	J
    //   98: invokestatic 123	java/util/Arrays:binarySearch	([JJ)I
    //   101: istore 4
    //   103: iload 4
    //   105: istore 5
    //   107: iload 4
    //   109: ifge +10 -> 119
    //   112: iload 4
    //   114: ineg
    //   115: iconst_2
    //   116: isub
    //   117: istore 5
    //   119: aload_3
    //   120: iload 5
    //   122: putfield 102	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker$Region:endOffsetIndex	I
    //   125: aload_0
    //   126: getfield 44	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker:regions	Ljava/util/TreeSet;
    //   129: aload_3
    //   130: invokevirtual 109	java/util/TreeSet:add	(Ljava/lang/Object;)Z
    //   133: pop
    //   134: aload_2
    //   135: getfield 99	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker$Region:endOffset	J
    //   138: aload_1
    //   139: getfield 99	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker$Region:endOffset	J
    //   142: lcmp
    //   143: ifle -96 -> 47
    //   146: new 8	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker$Region
    //   149: astore_3
    //   150: aload_3
    //   151: aload_1
    //   152: getfield 99	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker$Region:endOffset	J
    //   155: lconst_1
    //   156: ladd
    //   157: aload_2
    //   158: getfield 99	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker$Region:endOffset	J
    //   161: invokespecial 47	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker$Region:<init>	(JJ)V
    //   164: aload_3
    //   165: aload_2
    //   166: getfield 102	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker$Region:endOffsetIndex	I
    //   169: putfield 102	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker$Region:endOffsetIndex	I
    //   172: aload_0
    //   173: getfield 44	org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker:regions	Ljava/util/TreeSet;
    //   176: aload_3
    //   177: invokevirtual 109	java/util/TreeSet:add	(Ljava/lang/Object;)Z
    //   180: pop
    //   181: goto -134 -> 47
    //   184: astore_1
    //   185: aload_0
    //   186: monitorexit
    //   187: aload_1
    //   188: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	189	0	this	CachedRegionTracker
    //   0	189	1	paramCache	Cache
    //   0	189	2	paramCacheSpan	CacheSpan
    //   74	103	3	localRegion	Region
    //   101	12	4	i	int
    //   105	16	5	j	int
    // Exception table:
    //   from	to	target	type
    //   2	35	184	finally
    //   39	47	184	finally
    //   50	103	184	finally
    //   119	134	184	finally
    //   134	181	184	finally
  }
  
  public void onSpanTouched(Cache paramCache, CacheSpan paramCacheSpan1, CacheSpan paramCacheSpan2) {}
  
  public void release()
  {
    this.cache.removeListener(this.cacheKey, this);
  }
  
  private static class Region
    implements Comparable<Region>
  {
    public long endOffset;
    public int endOffsetIndex;
    public long startOffset;
    
    public Region(long paramLong1, long paramLong2)
    {
      this.startOffset = paramLong1;
      this.endOffset = paramLong2;
    }
    
    public int compareTo(Region paramRegion)
    {
      int i;
      if (this.startOffset < paramRegion.startOffset) {
        i = -1;
      }
      for (;;)
      {
        return i;
        if (this.startOffset == paramRegion.startOffset) {
          i = 0;
        } else {
          i = 1;
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/cache/CachedRegionTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */