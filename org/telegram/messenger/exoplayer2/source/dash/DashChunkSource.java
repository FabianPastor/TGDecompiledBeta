package org.telegram.messenger.exoplayer2.source.dash;

import org.telegram.messenger.exoplayer2.source.chunk.ChunkSource;
import org.telegram.messenger.exoplayer2.source.dash.manifest.DashManifest;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.LoaderErrorThrower;

public abstract interface DashChunkSource
  extends ChunkSource
{
  public abstract void updateManifest(DashManifest paramDashManifest, int paramInt);
  
  public static abstract interface Factory
  {
    public abstract DashChunkSource createDashChunkSource(LoaderErrorThrower paramLoaderErrorThrower, DashManifest paramDashManifest, int paramInt1, int[] paramArrayOfInt, TrackSelection paramTrackSelection, int paramInt2, long paramLong, boolean paramBoolean1, boolean paramBoolean2, PlayerEmsgHandler.PlayerTrackEmsgHandler paramPlayerTrackEmsgHandler);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/dash/DashChunkSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */