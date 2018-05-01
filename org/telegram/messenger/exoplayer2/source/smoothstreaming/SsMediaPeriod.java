package org.telegram.messenger.exoplayer2.source.smoothstreaming;

import android.util.Base64;
import java.io.IOException;
import java.util.ArrayList;
import org.telegram.messenger.exoplayer2.SeekParameters;
import org.telegram.messenger.exoplayer2.extractor.mp4.TrackEncryptionBox;
import org.telegram.messenger.exoplayer2.source.CompositeSequenceableLoaderFactory;
import org.telegram.messenger.exoplayer2.source.MediaPeriod;
import org.telegram.messenger.exoplayer2.source.MediaPeriod.Callback;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.source.SampleStream;
import org.telegram.messenger.exoplayer2.source.SequenceableLoader;
import org.telegram.messenger.exoplayer2.source.SequenceableLoader.Callback;
import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkSampleStream;
import org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest.SsManifest;
import org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest.SsManifest.ProtectionElement;
import org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest.SsManifest.StreamElement;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.LoaderErrorThrower;

final class SsMediaPeriod
  implements MediaPeriod, SequenceableLoader.Callback<ChunkSampleStream<SsChunkSource>>
{
  private static final int INITIALIZATION_VECTOR_SIZE = 8;
  private final Allocator allocator;
  private MediaPeriod.Callback callback;
  private final SsChunkSource.Factory chunkSourceFactory;
  private SequenceableLoader compositeSequenceableLoader;
  private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
  private final MediaSourceEventListener.EventDispatcher eventDispatcher;
  private SsManifest manifest;
  private final LoaderErrorThrower manifestLoaderErrorThrower;
  private final int minLoadableRetryCount;
  private ChunkSampleStream<SsChunkSource>[] sampleStreams;
  private final TrackEncryptionBox[] trackEncryptionBoxes;
  private final TrackGroupArray trackGroups;
  
  public SsMediaPeriod(SsManifest paramSsManifest, SsChunkSource.Factory paramFactory, CompositeSequenceableLoaderFactory paramCompositeSequenceableLoaderFactory, int paramInt, MediaSourceEventListener.EventDispatcher paramEventDispatcher, LoaderErrorThrower paramLoaderErrorThrower, Allocator paramAllocator)
  {
    this.chunkSourceFactory = paramFactory;
    this.manifestLoaderErrorThrower = paramLoaderErrorThrower;
    this.minLoadableRetryCount = paramInt;
    this.eventDispatcher = paramEventDispatcher;
    this.allocator = paramAllocator;
    this.compositeSequenceableLoaderFactory = paramCompositeSequenceableLoaderFactory;
    this.trackGroups = buildTrackGroups(paramSsManifest);
    paramFactory = paramSsManifest.protectionElement;
    if (paramFactory != null) {}
    for (this.trackEncryptionBoxes = new TrackEncryptionBox[] { new TrackEncryptionBox(true, null, 8, getProtectionElementKeyId(paramFactory.data), 0, 0, null) };; this.trackEncryptionBoxes = null)
    {
      this.manifest = paramSsManifest;
      this.sampleStreams = newSampleStreamArray(0);
      this.compositeSequenceableLoader = paramCompositeSequenceableLoaderFactory.createCompositeSequenceableLoader(this.sampleStreams);
      return;
    }
  }
  
  private ChunkSampleStream<SsChunkSource> buildSampleStream(TrackSelection paramTrackSelection, long paramLong)
  {
    int i = this.trackGroups.indexOf(paramTrackSelection.getTrackGroup());
    paramTrackSelection = this.chunkSourceFactory.createChunkSource(this.manifestLoaderErrorThrower, this.manifest, i, paramTrackSelection, this.trackEncryptionBoxes);
    return new ChunkSampleStream(this.manifest.streamElements[i].type, null, null, paramTrackSelection, this, this.allocator, paramLong, this.minLoadableRetryCount, this.eventDispatcher);
  }
  
  private static TrackGroupArray buildTrackGroups(SsManifest paramSsManifest)
  {
    TrackGroup[] arrayOfTrackGroup = new TrackGroup[paramSsManifest.streamElements.length];
    for (int i = 0; i < paramSsManifest.streamElements.length; i++) {
      arrayOfTrackGroup[i] = new TrackGroup(paramSsManifest.streamElements[i].formats);
    }
    return new TrackGroupArray(arrayOfTrackGroup);
  }
  
  private static byte[] getProtectionElementKeyId(byte[] paramArrayOfByte)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < paramArrayOfByte.length; i += 2) {
      localStringBuilder.append((char)paramArrayOfByte[i]);
    }
    paramArrayOfByte = localStringBuilder.toString();
    paramArrayOfByte = Base64.decode(paramArrayOfByte.substring(paramArrayOfByte.indexOf("<KID>") + 5, paramArrayOfByte.indexOf("</KID>")), 0);
    swap(paramArrayOfByte, 0, 3);
    swap(paramArrayOfByte, 1, 2);
    swap(paramArrayOfByte, 4, 5);
    swap(paramArrayOfByte, 6, 7);
    return paramArrayOfByte;
  }
  
  private static ChunkSampleStream<SsChunkSource>[] newSampleStreamArray(int paramInt)
  {
    return new ChunkSampleStream[paramInt];
  }
  
  private static void swap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = paramArrayOfByte[paramInt1];
    paramArrayOfByte[paramInt1] = ((byte)paramArrayOfByte[paramInt2]);
    paramArrayOfByte[paramInt2] = ((byte)i);
  }
  
  public boolean continueLoading(long paramLong)
  {
    return this.compositeSequenceableLoader.continueLoading(paramLong);
  }
  
  public void discardBuffer(long paramLong, boolean paramBoolean)
  {
    ChunkSampleStream[] arrayOfChunkSampleStream = this.sampleStreams;
    int i = arrayOfChunkSampleStream.length;
    for (int j = 0; j < i; j++) {
      arrayOfChunkSampleStream[j].discardBuffer(paramLong, paramBoolean);
    }
  }
  
  public long getAdjustedSeekPositionUs(long paramLong, SeekParameters paramSeekParameters)
  {
    ChunkSampleStream[] arrayOfChunkSampleStream = this.sampleStreams;
    int i = arrayOfChunkSampleStream.length;
    for (int j = 0;; j++)
    {
      long l = paramLong;
      if (j < i)
      {
        ChunkSampleStream localChunkSampleStream = arrayOfChunkSampleStream[j];
        if (localChunkSampleStream.primaryTrackType == 2) {
          l = localChunkSampleStream.getAdjustedSeekPositionUs(paramLong, paramSeekParameters);
        }
      }
      else
      {
        return l;
      }
    }
  }
  
  public long getBufferedPositionUs()
  {
    return this.compositeSequenceableLoader.getBufferedPositionUs();
  }
  
  public long getNextLoadPositionUs()
  {
    return this.compositeSequenceableLoader.getNextLoadPositionUs();
  }
  
  public TrackGroupArray getTrackGroups()
  {
    return this.trackGroups;
  }
  
  public void maybeThrowPrepareError()
    throws IOException
  {
    this.manifestLoaderErrorThrower.maybeThrowError();
  }
  
  public void onContinueLoadingRequested(ChunkSampleStream<SsChunkSource> paramChunkSampleStream)
  {
    this.callback.onContinueLoadingRequested(this);
  }
  
  public void prepare(MediaPeriod.Callback paramCallback, long paramLong)
  {
    this.callback = paramCallback;
    paramCallback.onPrepared(this);
  }
  
  public long readDiscontinuity()
  {
    return -9223372036854775807L;
  }
  
  public void reevaluateBuffer(long paramLong)
  {
    this.compositeSequenceableLoader.reevaluateBuffer(paramLong);
  }
  
  public void release()
  {
    ChunkSampleStream[] arrayOfChunkSampleStream = this.sampleStreams;
    int i = arrayOfChunkSampleStream.length;
    for (int j = 0; j < i; j++) {
      arrayOfChunkSampleStream[j].release();
    }
  }
  
  public long seekToUs(long paramLong)
  {
    ChunkSampleStream[] arrayOfChunkSampleStream = this.sampleStreams;
    int i = arrayOfChunkSampleStream.length;
    for (int j = 0; j < i; j++) {
      arrayOfChunkSampleStream[j].seekToUs(paramLong);
    }
    return paramLong;
  }
  
  public long selectTracks(TrackSelection[] paramArrayOfTrackSelection, boolean[] paramArrayOfBoolean1, SampleStream[] paramArrayOfSampleStream, boolean[] paramArrayOfBoolean2, long paramLong)
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    if (i < paramArrayOfTrackSelection.length)
    {
      ChunkSampleStream localChunkSampleStream;
      if (paramArrayOfSampleStream[i] != null)
      {
        localChunkSampleStream = (ChunkSampleStream)paramArrayOfSampleStream[i];
        if ((paramArrayOfTrackSelection[i] != null) && (paramArrayOfBoolean1[i] != 0)) {
          break label111;
        }
        localChunkSampleStream.release();
        paramArrayOfSampleStream[i] = null;
      }
      for (;;)
      {
        if ((paramArrayOfSampleStream[i] == null) && (paramArrayOfTrackSelection[i] != null))
        {
          localChunkSampleStream = buildSampleStream(paramArrayOfTrackSelection[i], paramLong);
          localArrayList.add(localChunkSampleStream);
          paramArrayOfSampleStream[i] = localChunkSampleStream;
          paramArrayOfBoolean2[i] = true;
        }
        i++;
        break;
        label111:
        localArrayList.add(localChunkSampleStream);
      }
    }
    this.sampleStreams = newSampleStreamArray(localArrayList.size());
    localArrayList.toArray(this.sampleStreams);
    this.compositeSequenceableLoader = this.compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(this.sampleStreams);
    return paramLong;
  }
  
  public void updateManifest(SsManifest paramSsManifest)
  {
    this.manifest = paramSsManifest;
    ChunkSampleStream[] arrayOfChunkSampleStream = this.sampleStreams;
    int i = arrayOfChunkSampleStream.length;
    for (int j = 0; j < i; j++) {
      ((SsChunkSource)arrayOfChunkSampleStream[j].getChunkSource()).updateManifest(paramSsManifest);
    }
    this.callback.onContinueLoadingRequested(this);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/smoothstreaming/SsMediaPeriod.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */