package org.telegram.messenger.exoplayer.upstream.cache;

public abstract interface CacheEvictor
  extends Cache.Listener
{
  public abstract void onCacheInitialized();
  
  public abstract void onStartFile(Cache paramCache, String paramString, long paramLong1, long paramLong2);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/upstream/cache/CacheEvictor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */