package org.telegram.messenger.exoplayer2.trackselection;

import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.RendererCapabilities;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;

public abstract class TrackSelector
{
  private InvalidationListener listener;
  
  public final void init(InvalidationListener paramInvalidationListener)
  {
    this.listener = paramInvalidationListener;
  }
  
  protected final void invalidate()
  {
    if (this.listener != null) {
      this.listener.onTrackSelectionsInvalidated();
    }
  }
  
  public abstract void onSelectionActivated(Object paramObject);
  
  public abstract TrackSelectorResult selectTracks(RendererCapabilities[] paramArrayOfRendererCapabilities, TrackGroupArray paramTrackGroupArray)
    throws ExoPlaybackException;
  
  public static abstract interface InvalidationListener
  {
    public abstract void onTrackSelectionsInvalidated();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/trackselection/TrackSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */