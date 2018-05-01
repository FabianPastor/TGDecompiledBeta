package org.telegram.messenger.exoplayer.dash;

import java.io.IOException;
import org.telegram.messenger.exoplayer.dash.mpd.MediaPresentationDescription;

public abstract interface DashTrackSelector
{
  public abstract void selectTracks(MediaPresentationDescription paramMediaPresentationDescription, int paramInt, Output paramOutput)
    throws IOException;
  
  public static abstract interface Output
  {
    public abstract void adaptiveTrack(MediaPresentationDescription paramMediaPresentationDescription, int paramInt1, int paramInt2, int[] paramArrayOfInt);
    
    public abstract void fixedTrack(MediaPresentationDescription paramMediaPresentationDescription, int paramInt1, int paramInt2, int paramInt3);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/dash/DashTrackSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */