package org.telegram.messenger.volley.toolbox;

import org.telegram.messenger.volley.Cache;
import org.telegram.messenger.volley.Cache.Entry;

public class NoCache
  implements Cache
{
  public void clear() {}
  
  public Cache.Entry get(String paramString)
  {
    return null;
  }
  
  public void initialize() {}
  
  public void invalidate(String paramString, boolean paramBoolean) {}
  
  public void put(String paramString, Cache.Entry paramEntry) {}
  
  public void remove(String paramString) {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/volley/toolbox/NoCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */