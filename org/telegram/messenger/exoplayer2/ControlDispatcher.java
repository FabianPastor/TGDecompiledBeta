package org.telegram.messenger.exoplayer2;

public abstract interface ControlDispatcher
{
  public abstract boolean dispatchSeekTo(Player paramPlayer, int paramInt, long paramLong);
  
  public abstract boolean dispatchSetPlayWhenReady(Player paramPlayer, boolean paramBoolean);
  
  public abstract boolean dispatchSetRepeatMode(Player paramPlayer, int paramInt);
  
  public abstract boolean dispatchSetShuffleModeEnabled(Player paramPlayer, boolean paramBoolean);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/ControlDispatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */