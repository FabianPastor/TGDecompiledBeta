package org.telegram.messenger.volley;

import java.util.Collections;
import java.util.Map;

public abstract interface Cache
{
  public abstract void clear();
  
  public abstract Entry get(String paramString);
  
  public abstract void initialize();
  
  public abstract void invalidate(String paramString, boolean paramBoolean);
  
  public abstract void put(String paramString, Entry paramEntry);
  
  public abstract void remove(String paramString);
  
  public static class Entry
  {
    public byte[] data;
    public String etag;
    public long lastModified;
    public Map<String, String> responseHeaders = Collections.emptyMap();
    public long serverDate;
    public long softTtl;
    public long ttl;
    
    public boolean isExpired()
    {
      return this.ttl < System.currentTimeMillis();
    }
    
    public boolean refreshNeeded()
    {
      return this.softTtl < System.currentTimeMillis();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/volley/Cache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */