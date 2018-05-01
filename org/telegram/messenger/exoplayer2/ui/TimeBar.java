package org.telegram.messenger.exoplayer2.ui;

import android.support.annotation.Nullable;

public abstract interface TimeBar
{
  public abstract void setAdBreakTimesMs(@Nullable long[] paramArrayOfLong, int paramInt);
  
  public abstract void setBufferedPosition(long paramLong);
  
  public abstract void setDuration(long paramLong);
  
  public abstract void setEnabled(boolean paramBoolean);
  
  public abstract void setKeyCountIncrement(int paramInt);
  
  public abstract void setKeyTimeIncrement(long paramLong);
  
  public abstract void setListener(OnScrubListener paramOnScrubListener);
  
  public abstract void setPosition(long paramLong);
  
  public static abstract interface OnScrubListener
  {
    public abstract void onScrubMove(TimeBar paramTimeBar, long paramLong);
    
    public abstract void onScrubStart(TimeBar paramTimeBar);
    
    public abstract void onScrubStop(TimeBar paramTimeBar, long paramLong, boolean paramBoolean);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/ui/TimeBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */