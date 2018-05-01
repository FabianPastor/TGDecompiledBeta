package org.telegram.messenger.exoplayer2.source.hls;

import android.os.Handler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.SeekParameters;
import org.telegram.messenger.exoplayer2.source.CompositeSequenceableLoaderFactory;
import org.telegram.messenger.exoplayer2.source.MediaPeriod;
import org.telegram.messenger.exoplayer2.source.MediaPeriod.Callback;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.source.SampleStream;
import org.telegram.messenger.exoplayer2.source.SequenceableLoader;
import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylistTracker;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylistTracker.PlaylistEventListener;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.Util;

public final class HlsMediaPeriod
  implements MediaPeriod, HlsSampleStreamWrapper.Callback, HlsPlaylistTracker.PlaylistEventListener
{
  private final Allocator allocator;
  private final boolean allowChunklessPreparation;
  private MediaPeriod.Callback callback;
  private SequenceableLoader compositeSequenceableLoader;
  private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
  private final Handler continueLoadingHandler;
  private final HlsDataSourceFactory dataSourceFactory;
  private HlsSampleStreamWrapper[] enabledSampleStreamWrappers;
  private final MediaSourceEventListener.EventDispatcher eventDispatcher;
  private final HlsExtractorFactory extractorFactory;
  private final int minLoadableRetryCount;
  private int pendingPrepareCount;
  private final HlsPlaylistTracker playlistTracker;
  private HlsSampleStreamWrapper[] sampleStreamWrappers;
  private final IdentityHashMap<SampleStream, Integer> streamWrapperIndices;
  private final TimestampAdjusterProvider timestampAdjusterProvider;
  private TrackGroupArray trackGroups;
  
  public HlsMediaPeriod(HlsExtractorFactory paramHlsExtractorFactory, HlsPlaylistTracker paramHlsPlaylistTracker, HlsDataSourceFactory paramHlsDataSourceFactory, int paramInt, MediaSourceEventListener.EventDispatcher paramEventDispatcher, Allocator paramAllocator, CompositeSequenceableLoaderFactory paramCompositeSequenceableLoaderFactory, boolean paramBoolean)
  {
    this.extractorFactory = paramHlsExtractorFactory;
    this.playlistTracker = paramHlsPlaylistTracker;
    this.dataSourceFactory = paramHlsDataSourceFactory;
    this.minLoadableRetryCount = paramInt;
    this.eventDispatcher = paramEventDispatcher;
    this.allocator = paramAllocator;
    this.compositeSequenceableLoaderFactory = paramCompositeSequenceableLoaderFactory;
    this.allowChunklessPreparation = paramBoolean;
    this.streamWrapperIndices = new IdentityHashMap();
    this.timestampAdjusterProvider = new TimestampAdjusterProvider();
    this.continueLoadingHandler = new Handler();
    this.sampleStreamWrappers = new HlsSampleStreamWrapper[0];
    this.enabledSampleStreamWrappers = new HlsSampleStreamWrapper[0];
  }
  
  private void buildAndPrepareMainSampleStreamWrapper(HlsMasterPlaylist paramHlsMasterPlaylist, long paramLong)
  {
    ArrayList localArrayList = new ArrayList(paramHlsMasterPlaylist.variants);
    Object localObject1 = new ArrayList();
    Object localObject2 = new ArrayList();
    int i = 0;
    Object localObject3;
    Object localObject4;
    if (i < localArrayList.size())
    {
      localObject3 = (HlsMasterPlaylist.HlsUrl)localArrayList.get(i);
      localObject4 = ((HlsMasterPlaylist.HlsUrl)localObject3).format;
      if ((((Format)localObject4).height > 0) || (Util.getCodecsOfType(((Format)localObject4).codecs, 2) != null)) {
        ((ArrayList)localObject1).add(localObject3);
      }
      for (;;)
      {
        i++;
        break;
        if (Util.getCodecsOfType(((Format)localObject4).codecs, 1) != null) {
          ((ArrayList)localObject2).add(localObject3);
        }
      }
    }
    boolean bool;
    label145:
    int j;
    if (!((ArrayList)localObject1).isEmpty())
    {
      if (((List)localObject1).isEmpty()) {
        break label335;
      }
      bool = true;
      Assertions.checkArgument(bool);
      localObject4 = (HlsMasterPlaylist.HlsUrl[])((List)localObject1).toArray(new HlsMasterPlaylist.HlsUrl[0]);
      localObject3 = localObject4[0].format.codecs;
      localObject2 = buildSampleStreamWrapper(0, (HlsMasterPlaylist.HlsUrl[])localObject4, paramHlsMasterPlaylist.muxedAudioFormat, paramHlsMasterPlaylist.muxedCaptionFormats, paramLong);
      this.sampleStreamWrappers[0] = localObject2;
      if ((!this.allowChunklessPreparation) || (localObject3 == null)) {
        break label629;
      }
      if (Util.getCodecsOfType((String)localObject3, 2) == null) {
        break label341;
      }
      j = 1;
      label228:
      if (Util.getCodecsOfType((String)localObject3, 1) == null) {
        break label347;
      }
    }
    label335:
    label341:
    label347:
    for (i = 1;; i = 0)
    {
      localArrayList = new ArrayList();
      if (j == 0) {
        break label493;
      }
      localObject1 = new Format[((List)localObject1).size()];
      for (j = 0; j < localObject1.length; j++) {
        localObject1[j] = deriveVideoFormat(localObject4[j].format);
      }
      localObject1 = localArrayList;
      if (((ArrayList)localObject2).size() >= localArrayList.size()) {
        break;
      }
      localArrayList.removeAll((Collection)localObject2);
      localObject1 = localArrayList;
      break;
      bool = false;
      break label145;
      j = 0;
      break label228;
    }
    localArrayList.add(new TrackGroup((Format[])localObject1));
    if ((i != 0) && ((paramHlsMasterPlaylist.muxedAudioFormat != null) || (paramHlsMasterPlaylist.audios.isEmpty()))) {
      localArrayList.add(new TrackGroup(new Format[] { deriveMuxedAudioFormat(localObject4[0].format, paramHlsMasterPlaylist.muxedAudioFormat, -1) }));
    }
    paramHlsMasterPlaylist = paramHlsMasterPlaylist.muxedCaptionFormats;
    if (paramHlsMasterPlaylist != null)
    {
      for (i = 0; i < paramHlsMasterPlaylist.size(); i++) {
        localArrayList.add(new TrackGroup(new Format[] { (Format)paramHlsMasterPlaylist.get(i) }));
      }
      label493:
      if (i != 0)
      {
        localObject3 = new Format[((List)localObject1).size()];
        for (i = 0; i < localObject3.length; i++)
        {
          localObject1 = localObject4[i].format;
          localObject3[i] = deriveMuxedAudioFormat((Format)localObject1, paramHlsMasterPlaylist.muxedAudioFormat, ((Format)localObject1).bitrate);
        }
        localArrayList.add(new TrackGroup((Format[])localObject3));
      }
    }
    else
    {
      ((HlsSampleStreamWrapper)localObject2).prepareWithMasterPlaylistInfo(new TrackGroupArray((TrackGroup[])localArrayList.toArray(new TrackGroup[0])), 0);
    }
    for (;;)
    {
      return;
      throw new IllegalArgumentException("Unexpected codecs attribute: " + (String)localObject3);
      label629:
      ((HlsSampleStreamWrapper)localObject2).setIsTimestampMaster(true);
      ((HlsSampleStreamWrapper)localObject2).continuePreparing();
    }
  }
  
  private void buildAndPrepareSampleStreamWrappers(long paramLong)
  {
    Object localObject1 = this.playlistTracker.getMasterPlaylist();
    Object localObject2 = ((HlsMasterPlaylist)localObject1).audios;
    List localList = ((HlsMasterPlaylist)localObject1).subtitles;
    int i = ((List)localObject2).size() + 1 + localList.size();
    this.sampleStreamWrappers = new HlsSampleStreamWrapper[i];
    this.pendingPrepareCount = i;
    buildAndPrepareMainSampleStreamWrapper((HlsMasterPlaylist)localObject1, paramLong);
    i = 1;
    int j = 0;
    if (j < ((List)localObject2).size())
    {
      localObject1 = (HlsMasterPlaylist.HlsUrl)((List)localObject2).get(j);
      Object localObject3 = Collections.emptyList();
      HlsSampleStreamWrapper localHlsSampleStreamWrapper = buildSampleStreamWrapper(1, new HlsMasterPlaylist.HlsUrl[] { localObject1 }, null, (List)localObject3, paramLong);
      this.sampleStreamWrappers[i] = localHlsSampleStreamWrapper;
      localObject3 = ((HlsMasterPlaylist.HlsUrl)localObject1).format;
      if ((this.allowChunklessPreparation) && (((Format)localObject3).codecs != null)) {
        localHlsSampleStreamWrapper.prepareWithMasterPlaylistInfo(new TrackGroupArray(new TrackGroup[] { new TrackGroup(new Format[] { ((HlsMasterPlaylist.HlsUrl)localObject1).format }) }), 0);
      }
      for (;;)
      {
        j++;
        i++;
        break;
        localHlsSampleStreamWrapper.continuePreparing();
      }
    }
    j = 0;
    while (j < localList.size())
    {
      localObject2 = (HlsMasterPlaylist.HlsUrl)localList.get(j);
      localObject1 = Collections.emptyList();
      localObject1 = buildSampleStreamWrapper(3, new HlsMasterPlaylist.HlsUrl[] { localObject2 }, null, (List)localObject1, paramLong);
      this.sampleStreamWrappers[i] = localObject1;
      ((HlsSampleStreamWrapper)localObject1).prepareWithMasterPlaylistInfo(new TrackGroupArray(new TrackGroup[] { new TrackGroup(new Format[] { ((HlsMasterPlaylist.HlsUrl)localObject2).format }) }), 0);
      j++;
      i++;
    }
    this.enabledSampleStreamWrappers = this.sampleStreamWrappers;
  }
  
  private HlsSampleStreamWrapper buildSampleStreamWrapper(int paramInt, HlsMasterPlaylist.HlsUrl[] paramArrayOfHlsUrl, Format paramFormat, List<Format> paramList, long paramLong)
  {
    return new HlsSampleStreamWrapper(paramInt, this, new HlsChunkSource(this.extractorFactory, this.playlistTracker, paramArrayOfHlsUrl, this.dataSourceFactory, this.timestampAdjusterProvider, paramList), this.allocator, paramLong, paramFormat, this.minLoadableRetryCount, this.eventDispatcher);
  }
  
  private void continuePreparingOrLoading()
  {
    if (this.trackGroups != null) {
      this.callback.onContinueLoadingRequested(this);
    }
    for (;;)
    {
      return;
      HlsSampleStreamWrapper[] arrayOfHlsSampleStreamWrapper = this.sampleStreamWrappers;
      int i = arrayOfHlsSampleStreamWrapper.length;
      for (int j = 0; j < i; j++) {
        arrayOfHlsSampleStreamWrapper[j].continuePreparing();
      }
    }
  }
  
  private static Format deriveMuxedAudioFormat(Format paramFormat1, Format paramFormat2, int paramInt)
  {
    int i = -1;
    int j = 0;
    String str1 = null;
    String str2;
    if (paramFormat2 != null)
    {
      str2 = paramFormat2.codecs;
      i = paramFormat2.channelCount;
      j = paramFormat2.selectionFlags;
    }
    for (paramFormat2 = paramFormat2.language;; paramFormat2 = str1)
    {
      str1 = MimeTypes.getMediaMimeType(str2);
      return Format.createAudioSampleFormat(paramFormat1.id, str1, str2, paramInt, -1, i, -1, null, null, j, paramFormat2);
      str2 = Util.getCodecsOfType(paramFormat1.codecs, 1);
    }
  }
  
  private static Format deriveVideoFormat(Format paramFormat)
  {
    String str1 = Util.getCodecsOfType(paramFormat.codecs, 2);
    String str2 = MimeTypes.getMediaMimeType(str1);
    return Format.createVideoSampleFormat(paramFormat.id, str2, str1, paramFormat.bitrate, -1, paramFormat.width, paramFormat.height, paramFormat.frameRate, null, null);
  }
  
  public boolean continueLoading(long paramLong)
  {
    return this.compositeSequenceableLoader.continueLoading(paramLong);
  }
  
  public void discardBuffer(long paramLong, boolean paramBoolean)
  {
    HlsSampleStreamWrapper[] arrayOfHlsSampleStreamWrapper = this.enabledSampleStreamWrappers;
    int i = arrayOfHlsSampleStreamWrapper.length;
    for (int j = 0; j < i; j++) {
      arrayOfHlsSampleStreamWrapper[j].discardBuffer(paramLong, paramBoolean);
    }
  }
  
  public long getAdjustedSeekPositionUs(long paramLong, SeekParameters paramSeekParameters)
  {
    return paramLong;
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
    HlsSampleStreamWrapper[] arrayOfHlsSampleStreamWrapper = this.sampleStreamWrappers;
    int i = arrayOfHlsSampleStreamWrapper.length;
    for (int j = 0; j < i; j++) {
      arrayOfHlsSampleStreamWrapper[j].maybeThrowPrepareError();
    }
  }
  
  public void onContinueLoadingRequested(HlsSampleStreamWrapper paramHlsSampleStreamWrapper)
  {
    if (this.trackGroups == null) {}
    for (;;)
    {
      return;
      this.callback.onContinueLoadingRequested(this);
    }
  }
  
  public void onPlaylistBlacklisted(HlsMasterPlaylist.HlsUrl paramHlsUrl, long paramLong)
  {
    HlsSampleStreamWrapper[] arrayOfHlsSampleStreamWrapper = this.sampleStreamWrappers;
    int i = arrayOfHlsSampleStreamWrapper.length;
    for (int j = 0; j < i; j++) {
      arrayOfHlsSampleStreamWrapper[j].onPlaylistBlacklisted(paramHlsUrl, paramLong);
    }
    continuePreparingOrLoading();
  }
  
  public void onPlaylistChanged()
  {
    continuePreparingOrLoading();
  }
  
  public void onPlaylistRefreshRequired(HlsMasterPlaylist.HlsUrl paramHlsUrl)
  {
    this.playlistTracker.refreshPlaylist(paramHlsUrl);
  }
  
  public void onPrepared()
  {
    int i = 0;
    int j = this.pendingPrepareCount - 1;
    this.pendingPrepareCount = j;
    if (j > 0) {}
    for (;;)
    {
      return;
      int k = 0;
      HlsSampleStreamWrapper[] arrayOfHlsSampleStreamWrapper1 = this.sampleStreamWrappers;
      int m = arrayOfHlsSampleStreamWrapper1.length;
      for (j = 0; j < m; j++) {
        k += arrayOfHlsSampleStreamWrapper1[j].getTrackGroups().length;
      }
      TrackGroup[] arrayOfTrackGroup = new TrackGroup[k];
      j = 0;
      HlsSampleStreamWrapper[] arrayOfHlsSampleStreamWrapper2 = this.sampleStreamWrappers;
      m = arrayOfHlsSampleStreamWrapper2.length;
      for (k = i; k < m; k++)
      {
        arrayOfHlsSampleStreamWrapper1 = arrayOfHlsSampleStreamWrapper2[k];
        int n = arrayOfHlsSampleStreamWrapper1.getTrackGroups().length;
        i = 0;
        while (i < n)
        {
          arrayOfTrackGroup[j] = arrayOfHlsSampleStreamWrapper1.getTrackGroups().get(i);
          i++;
          j++;
        }
      }
      this.trackGroups = new TrackGroupArray(arrayOfTrackGroup);
      this.callback.onPrepared(this);
    }
  }
  
  public void prepare(MediaPeriod.Callback paramCallback, long paramLong)
  {
    this.callback = paramCallback;
    this.playlistTracker.addListener(this);
    buildAndPrepareSampleStreamWrappers(paramLong);
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
    this.playlistTracker.removeListener(this);
    this.continueLoadingHandler.removeCallbacksAndMessages(null);
    HlsSampleStreamWrapper[] arrayOfHlsSampleStreamWrapper = this.sampleStreamWrappers;
    int i = arrayOfHlsSampleStreamWrapper.length;
    for (int j = 0; j < i; j++) {
      arrayOfHlsSampleStreamWrapper[j].release();
    }
  }
  
  public long seekToUs(long paramLong)
  {
    if (this.enabledSampleStreamWrappers.length > 0)
    {
      boolean bool = this.enabledSampleStreamWrappers[0].seekToUs(paramLong, false);
      for (int i = 1; i < this.enabledSampleStreamWrappers.length; i++) {
        this.enabledSampleStreamWrappers[i].seekToUs(paramLong, bool);
      }
      if (bool) {
        this.timestampAdjusterProvider.reset();
      }
    }
    return paramLong;
  }
  
  public long selectTracks(TrackSelection[] paramArrayOfTrackSelection, boolean[] paramArrayOfBoolean1, SampleStream[] paramArrayOfSampleStream, boolean[] paramArrayOfBoolean2, long paramLong)
  {
    int[] arrayOfInt1 = new int[paramArrayOfTrackSelection.length];
    int[] arrayOfInt2 = new int[paramArrayOfTrackSelection.length];
    int i = 0;
    label32:
    Object localObject;
    if (i < paramArrayOfTrackSelection.length)
    {
      if (paramArrayOfSampleStream[i] == null)
      {
        j = -1;
        arrayOfInt1[i] = j;
        arrayOfInt2[i] = -1;
        if (paramArrayOfTrackSelection[i] != null) {
          localObject = paramArrayOfTrackSelection[i].getTrackGroup();
        }
      }
      for (j = 0;; j++) {
        if (j < this.sampleStreamWrappers.length)
        {
          if (this.sampleStreamWrappers[j].getTrackGroups().indexOf((TrackGroup)localObject) != -1) {
            arrayOfInt2[i] = j;
          }
        }
        else
        {
          i++;
          break;
          j = ((Integer)this.streamWrapperIndices.get(paramArrayOfSampleStream[i])).intValue();
          break label32;
        }
      }
    }
    boolean bool1 = false;
    this.streamWrapperIndices.clear();
    SampleStream[] arrayOfSampleStream1 = new SampleStream[paramArrayOfTrackSelection.length];
    SampleStream[] arrayOfSampleStream2 = new SampleStream[paramArrayOfTrackSelection.length];
    TrackSelection[] arrayOfTrackSelection = new TrackSelection[paramArrayOfTrackSelection.length];
    int k = 0;
    HlsSampleStreamWrapper[] arrayOfHlsSampleStreamWrapper = new HlsSampleStreamWrapper[this.sampleStreamWrappers.length];
    int j = 0;
    if (j < this.sampleStreamWrappers.length)
    {
      i = 0;
      if (i < paramArrayOfTrackSelection.length)
      {
        if (arrayOfInt1[i] == j)
        {
          localObject = paramArrayOfSampleStream[i];
          label219:
          arrayOfSampleStream2[i] = localObject;
          if (arrayOfInt2[i] != j) {
            break label261;
          }
        }
        label261:
        for (localObject = paramArrayOfTrackSelection[i];; localObject = null)
        {
          arrayOfTrackSelection[i] = localObject;
          i++;
          break;
          localObject = null;
          break label219;
        }
      }
      localObject = this.sampleStreamWrappers[j];
      boolean bool2 = ((HlsSampleStreamWrapper)localObject).selectTracks(arrayOfTrackSelection, paramArrayOfBoolean1, arrayOfSampleStream2, paramArrayOfBoolean2, paramLong, bool1);
      int m = 0;
      int n = 0;
      if (n < paramArrayOfTrackSelection.length)
      {
        if (arrayOfInt2[n] == j) {
          if (arrayOfSampleStream2[n] != null)
          {
            bool3 = true;
            label328:
            Assertions.checkState(bool3);
            arrayOfSampleStream1[n] = arrayOfSampleStream2[n];
            i = 1;
            this.streamWrapperIndices.put(arrayOfSampleStream2[n], Integer.valueOf(j));
          }
        }
        do
        {
          n++;
          m = i;
          break;
          bool3 = false;
          break label328;
          i = m;
        } while (arrayOfInt1[n] != j);
        if (arrayOfSampleStream2[n] == null) {}
        for (bool3 = true;; bool3 = false)
        {
          Assertions.checkState(bool3);
          i = m;
          break;
        }
      }
      boolean bool3 = bool1;
      i = k;
      if (m != 0)
      {
        arrayOfHlsSampleStreamWrapper[k] = localObject;
        i = k + 1;
        if (k != 0) {
          break label508;
        }
        ((HlsSampleStreamWrapper)localObject).setIsTimestampMaster(true);
        if ((!bool2) && (this.enabledSampleStreamWrappers.length != 0) && (localObject == this.enabledSampleStreamWrappers[0])) {
          break label514;
        }
        this.timestampAdjusterProvider.reset();
      }
      label508:
      label514:
      for (bool3 = true;; bool3 = bool1)
      {
        j++;
        bool1 = bool3;
        k = i;
        break;
        ((HlsSampleStreamWrapper)localObject).setIsTimestampMaster(false);
      }
    }
    System.arraycopy(arrayOfSampleStream1, 0, paramArrayOfSampleStream, 0, arrayOfSampleStream1.length);
    this.enabledSampleStreamWrappers = ((HlsSampleStreamWrapper[])Arrays.copyOf(arrayOfHlsSampleStreamWrapper, k));
    this.compositeSequenceableLoader = this.compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(this.enabledSampleStreamWrappers);
    return paramLong;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/hls/HlsMediaPeriod.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */