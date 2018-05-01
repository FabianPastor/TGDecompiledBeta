package org.telegram.messenger.exoplayer2;

import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelectionArray;
import org.telegram.messenger.exoplayer2.upstream.Allocator;

public abstract interface LoadControl
{
  public abstract Allocator getAllocator();
  
  public abstract long getBackBufferDurationUs();
  
  public abstract void onPrepared();
  
  public abstract void onReleased();
  
  public abstract void onStopped();
  
  public abstract void onTracksSelected(Renderer[] paramArrayOfRenderer, TrackGroupArray paramTrackGroupArray, TrackSelectionArray paramTrackSelectionArray);
  
  public abstract boolean retainBackBufferFromKeyframe();
  
  public abstract boolean shouldContinueLoading(long paramLong, float paramFloat);
  
  public abstract boolean shouldStartPlayback(long paramLong, float paramFloat, boolean paramBoolean);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/LoadControl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */