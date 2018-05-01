package org.telegram.messenger.exoplayer2.source.smoothstreaming;

import org.telegram.messenger.exoplayer2.extractor.mp4.TrackEncryptionBox;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkSource;
import org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest.SsManifest;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.LoaderErrorThrower;

public abstract interface SsChunkSource
  extends ChunkSource
{
  public abstract void updateManifest(SsManifest paramSsManifest);
  
  public static abstract interface Factory
  {
    public abstract SsChunkSource createChunkSource(LoaderErrorThrower paramLoaderErrorThrower, SsManifest paramSsManifest, int paramInt, TrackSelection paramTrackSelection, TrackEncryptionBox[] paramArrayOfTrackEncryptionBox);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/smoothstreaming/SsChunkSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */