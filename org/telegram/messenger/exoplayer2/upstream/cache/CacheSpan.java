package org.telegram.messenger.exoplayer2.upstream.cache;

import java.io.File;

public class CacheSpan
  implements Comparable<CacheSpan>
{
  public final File file;
  public final boolean isCached;
  public final String key;
  public final long lastAccessTimestamp;
  public final long length;
  public final long position;
  
  public CacheSpan(String paramString, long paramLong1, long paramLong2)
  {
    this(paramString, paramLong1, paramLong2, -9223372036854775807L, null);
  }
  
  public CacheSpan(String paramString, long paramLong1, long paramLong2, long paramLong3, File paramFile)
  {
    this.key = paramString;
    this.position = paramLong1;
    this.length = paramLong2;
    if (paramFile != null) {}
    for (boolean bool = true;; bool = false)
    {
      this.isCached = bool;
      this.file = paramFile;
      this.lastAccessTimestamp = paramLong3;
      return;
    }
  }
  
  public int compareTo(CacheSpan paramCacheSpan)
  {
    int i;
    if (!this.key.equals(paramCacheSpan.key)) {
      i = this.key.compareTo(paramCacheSpan.key);
    }
    for (;;)
    {
      return i;
      long l = this.position - paramCacheSpan.position;
      if (l == 0L) {
        i = 0;
      } else if (l < 0L) {
        i = -1;
      } else {
        i = 1;
      }
    }
  }
  
  public boolean isHoleSpan()
  {
    if (!this.isCached) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isOpenEnded()
  {
    if (this.length == -1L) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/cache/CacheSpan.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */