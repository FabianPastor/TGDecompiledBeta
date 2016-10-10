package org.telegram.messenger.exoplayer.smoothstreaming;

import java.io.IOException;

public abstract interface SmoothStreamingTrackSelector
{
  public abstract void selectTracks(SmoothStreamingManifest paramSmoothStreamingManifest, Output paramOutput)
    throws IOException;
  
  public static abstract interface Output
  {
    public abstract void adaptiveTrack(SmoothStreamingManifest paramSmoothStreamingManifest, int paramInt, int[] paramArrayOfInt);
    
    public abstract void fixedTrack(SmoothStreamingManifest paramSmoothStreamingManifest, int paramInt1, int paramInt2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/smoothstreaming/SmoothStreamingTrackSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */