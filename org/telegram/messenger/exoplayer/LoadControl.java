package org.telegram.messenger.exoplayer;

import org.telegram.messenger.exoplayer.upstream.Allocator;

public abstract interface LoadControl
{
  public abstract Allocator getAllocator();
  
  public abstract void register(Object paramObject, int paramInt);
  
  public abstract void trimAllocator();
  
  public abstract void unregister(Object paramObject);
  
  public abstract boolean update(Object paramObject, long paramLong1, long paramLong2, boolean paramBoolean);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/LoadControl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */