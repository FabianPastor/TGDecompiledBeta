package org.telegram.messenger.exoplayer.upstream.cache;

public final class NoOpCacheEvictor
  implements CacheEvictor
{
  public void onCacheInitialized() {}
  
  public void onSpanAdded(Cache paramCache, CacheSpan paramCacheSpan) {}
  
  public void onSpanRemoved(Cache paramCache, CacheSpan paramCacheSpan) {}
  
  public void onSpanTouched(Cache paramCache, CacheSpan paramCacheSpan1, CacheSpan paramCacheSpan2) {}
  
  public void onStartFile(Cache paramCache, String paramString, long paramLong1, long paramLong2) {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/upstream/cache/NoOpCacheEvictor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */