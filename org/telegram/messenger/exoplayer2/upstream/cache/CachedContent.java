package org.telegram.messenger.exoplayer2.upstream.cache;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;
import org.telegram.messenger.exoplayer2.util.Assertions;

final class CachedContent
{
  private final TreeSet<SimpleCacheSpan> cachedSpans;
  public final int id;
  public final String key;
  private long length;
  private boolean locked;
  
  public CachedContent(int paramInt, String paramString, long paramLong)
  {
    this.id = paramInt;
    this.key = paramString;
    this.length = paramLong;
    this.cachedSpans = new TreeSet();
  }
  
  public CachedContent(DataInputStream paramDataInputStream)
    throws IOException
  {
    this(paramDataInputStream.readInt(), paramDataInputStream.readUTF(), paramDataInputStream.readLong());
  }
  
  public void addSpan(SimpleCacheSpan paramSimpleCacheSpan)
  {
    this.cachedSpans.add(paramSimpleCacheSpan);
  }
  
  public long getCachedBytesLength(long paramLong1, long paramLong2)
  {
    Object localObject = getSpan(paramLong1);
    if (((SimpleCacheSpan)localObject).isHoleSpan())
    {
      if (((SimpleCacheSpan)localObject).isOpenEnded()) {}
      for (paramLong1 = Long.MAX_VALUE;; paramLong1 = ((SimpleCacheSpan)localObject).length)
      {
        paramLong1 = -Math.min(paramLong1, paramLong2);
        return paramLong1;
      }
    }
    long l1 = paramLong1 + paramLong2;
    long l2 = ((SimpleCacheSpan)localObject).position + ((SimpleCacheSpan)localObject).length;
    long l3 = l2;
    label92:
    SimpleCacheSpan localSimpleCacheSpan;
    if (l2 < l1)
    {
      localObject = this.cachedSpans.tailSet(localObject, false).iterator();
      l3 = l2;
      if (((Iterator)localObject).hasNext())
      {
        localSimpleCacheSpan = (SimpleCacheSpan)((Iterator)localObject).next();
        if (localSimpleCacheSpan.position <= l2) {
          break label145;
        }
        l3 = l2;
      }
    }
    for (;;)
    {
      paramLong1 = Math.min(l3 - paramLong1, paramLong2);
      break;
      label145:
      l3 = Math.max(l2, localSimpleCacheSpan.position + localSimpleCacheSpan.length);
      l2 = l3;
      if (l3 < l1) {
        break label92;
      }
    }
  }
  
  public long getLength()
  {
    return this.length;
  }
  
  public SimpleCacheSpan getSpan(long paramLong)
  {
    SimpleCacheSpan localSimpleCacheSpan1 = SimpleCacheSpan.createLookup(this.key, paramLong);
    SimpleCacheSpan localSimpleCacheSpan2 = (SimpleCacheSpan)this.cachedSpans.floor(localSimpleCacheSpan1);
    if ((localSimpleCacheSpan2 != null) && (localSimpleCacheSpan2.position + localSimpleCacheSpan2.length > paramLong)) {
      return localSimpleCacheSpan2;
    }
    localSimpleCacheSpan2 = (SimpleCacheSpan)this.cachedSpans.ceiling(localSimpleCacheSpan1);
    if (localSimpleCacheSpan2 == null) {}
    for (localSimpleCacheSpan2 = SimpleCacheSpan.createOpenHole(this.key, paramLong);; localSimpleCacheSpan2 = SimpleCacheSpan.createClosedHole(this.key, paramLong, localSimpleCacheSpan2.position - paramLong)) {
      break;
    }
  }
  
  public TreeSet<SimpleCacheSpan> getSpans()
  {
    return this.cachedSpans;
  }
  
  public int headerHashCode()
  {
    return (this.id * 31 + this.key.hashCode()) * 31 + (int)(this.length ^ this.length >>> 32);
  }
  
  public boolean isEmpty()
  {
    return this.cachedSpans.isEmpty();
  }
  
  public boolean isLocked()
  {
    return this.locked;
  }
  
  public boolean removeSpan(CacheSpan paramCacheSpan)
  {
    if (this.cachedSpans.remove(paramCacheSpan)) {
      paramCacheSpan.file.delete();
    }
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void setLength(long paramLong)
  {
    this.length = paramLong;
  }
  
  public void setLocked(boolean paramBoolean)
  {
    this.locked = paramBoolean;
  }
  
  public SimpleCacheSpan touch(SimpleCacheSpan paramSimpleCacheSpan)
    throws Cache.CacheException
  {
    Assertions.checkState(this.cachedSpans.remove(paramSimpleCacheSpan));
    SimpleCacheSpan localSimpleCacheSpan = paramSimpleCacheSpan.copyWithUpdatedLastAccessTime(this.id);
    if (!paramSimpleCacheSpan.file.renameTo(localSimpleCacheSpan.file)) {
      throw new Cache.CacheException("Renaming of " + paramSimpleCacheSpan.file + " to " + localSimpleCacheSpan.file + " failed.");
    }
    this.cachedSpans.add(localSimpleCacheSpan);
    return localSimpleCacheSpan;
  }
  
  public void writeToStream(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeInt(this.id);
    paramDataOutputStream.writeUTF(this.key);
    paramDataOutputStream.writeLong(this.length);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/cache/CachedContent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */