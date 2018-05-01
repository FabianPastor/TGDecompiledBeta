package org.telegram.messenger.exoplayer2;

public class DefaultControlDispatcher
  implements ControlDispatcher
{
  public boolean dispatchSeekTo(Player paramPlayer, int paramInt, long paramLong)
  {
    paramPlayer.seekTo(paramInt, paramLong);
    return true;
  }
  
  public boolean dispatchSetPlayWhenReady(Player paramPlayer, boolean paramBoolean)
  {
    paramPlayer.setPlayWhenReady(paramBoolean);
    return true;
  }
  
  public boolean dispatchSetRepeatMode(Player paramPlayer, int paramInt)
  {
    paramPlayer.setRepeatMode(paramInt);
    return true;
  }
  
  public boolean dispatchSetShuffleModeEnabled(Player paramPlayer, boolean paramBoolean)
  {
    paramPlayer.setShuffleModeEnabled(paramBoolean);
    return true;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/DefaultControlDispatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */