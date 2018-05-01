package org.telegram.messenger.exoplayer2.source.dash;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput.CryptoData;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.metadata.MetadataInputBuffer;
import org.telegram.messenger.exoplayer2.metadata.emsg.EventMessage;
import org.telegram.messenger.exoplayer2.metadata.emsg.EventMessageDecoder;
import org.telegram.messenger.exoplayer2.source.SampleQueue;
import org.telegram.messenger.exoplayer2.source.chunk.Chunk;
import org.telegram.messenger.exoplayer2.source.dash.manifest.DashManifest;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

public final class PlayerEmsgHandler
  implements Handler.Callback
{
  private static final int EMSG_MANIFEST_EXPIRED = 2;
  private static final int EMSG_MEDIA_PRESENTATION_ENDED = 1;
  private final Allocator allocator;
  private final EventMessageDecoder decoder;
  private boolean dynamicMediaPresentationEnded;
  private long expiredManifestPublishTimeUs;
  private final Handler handler;
  private boolean isWaitingForManifestRefresh;
  private long lastLoadedChunkEndTimeBeforeRefreshUs;
  private long lastLoadedChunkEndTimeUs;
  private DashManifest manifest;
  private final TreeMap<Long, Long> manifestPublishTimeToExpiryTimeUs;
  private final PlayerEmsgCallback playerEmsgCallback;
  private boolean released;
  
  public PlayerEmsgHandler(DashManifest paramDashManifest, PlayerEmsgCallback paramPlayerEmsgCallback, Allocator paramAllocator)
  {
    this.manifest = paramDashManifest;
    this.playerEmsgCallback = paramPlayerEmsgCallback;
    this.allocator = paramAllocator;
    this.manifestPublishTimeToExpiryTimeUs = new TreeMap();
    this.handler = new Handler(this);
    this.decoder = new EventMessageDecoder();
    this.lastLoadedChunkEndTimeUs = -9223372036854775807L;
    this.lastLoadedChunkEndTimeBeforeRefreshUs = -9223372036854775807L;
  }
  
  private Map.Entry<Long, Long> ceilingExpiryEntryForPublishTime(long paramLong)
  {
    if (this.manifestPublishTimeToExpiryTimeUs.isEmpty()) {}
    for (Object localObject = null;; localObject = this.manifestPublishTimeToExpiryTimeUs.ceilingEntry(Long.valueOf(paramLong))) {
      return (Map.Entry<Long, Long>)localObject;
    }
  }
  
  private static long getManifestPublishTimeMsInEmsg(EventMessage paramEventMessage)
  {
    try
    {
      String str = new java/lang/String;
      str.<init>(paramEventMessage.messageData);
      l = Util.parseXsDateTime(str);
      return l;
    }
    catch (ParserException paramEventMessage)
    {
      for (;;)
      {
        long l = -9223372036854775807L;
      }
    }
  }
  
  private void handleManifestExpiredMessage(long paramLong1, long paramLong2)
  {
    if (!this.manifestPublishTimeToExpiryTimeUs.containsKey(Long.valueOf(paramLong2))) {
      this.manifestPublishTimeToExpiryTimeUs.put(Long.valueOf(paramLong2), Long.valueOf(paramLong1));
    }
    for (;;)
    {
      return;
      if (((Long)this.manifestPublishTimeToExpiryTimeUs.get(Long.valueOf(paramLong2))).longValue() > paramLong1) {
        this.manifestPublishTimeToExpiryTimeUs.put(Long.valueOf(paramLong2), Long.valueOf(paramLong1));
      }
    }
  }
  
  private void handleMediaPresentationEndedMessageEncountered()
  {
    this.dynamicMediaPresentationEnded = true;
    notifySourceMediaPresentationEnded();
  }
  
  private static boolean isMessageSignalingMediaPresentationEnded(EventMessage paramEventMessage)
  {
    if ((paramEventMessage.presentationTimeUs == 0L) && (paramEventMessage.durationMs == 0L)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isPlayerEmsgEvent(String paramString1, String paramString2)
  {
    if (("urn:mpeg:dash:event:2012".equals(paramString1)) && (("1".equals(paramString2)) || ("2".equals(paramString2)) || ("3".equals(paramString2)))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private void maybeNotifyDashManifestRefreshNeeded()
  {
    if ((this.lastLoadedChunkEndTimeBeforeRefreshUs != -9223372036854775807L) && (this.lastLoadedChunkEndTimeBeforeRefreshUs == this.lastLoadedChunkEndTimeUs)) {}
    for (;;)
    {
      return;
      this.isWaitingForManifestRefresh = true;
      this.lastLoadedChunkEndTimeBeforeRefreshUs = this.lastLoadedChunkEndTimeUs;
      this.playerEmsgCallback.onDashManifestRefreshRequested();
    }
  }
  
  private void notifyManifestPublishTimeExpired()
  {
    this.playerEmsgCallback.onDashManifestPublishTimeExpired(this.expiredManifestPublishTimeUs);
  }
  
  private void notifySourceMediaPresentationEnded()
  {
    this.playerEmsgCallback.onDashLiveMediaPresentationEndSignalEncountered();
  }
  
  private void removePreviouslyExpiredManifestPublishTimeValues()
  {
    Iterator localIterator = this.manifestPublishTimeToExpiryTimeUs.entrySet().iterator();
    while (localIterator.hasNext()) {
      if (((Long)((Map.Entry)localIterator.next()).getKey()).longValue() < this.manifest.publishTimeMs) {
        localIterator.remove();
      }
    }
  }
  
  public boolean handleMessage(Message paramMessage)
  {
    boolean bool = true;
    if (this.released) {}
    for (;;)
    {
      return bool;
      switch (paramMessage.what)
      {
      default: 
        bool = false;
        break;
      case 1: 
        handleMediaPresentationEndedMessageEncountered();
        break;
      case 2: 
        paramMessage = (ManifestExpiryEventInfo)paramMessage.obj;
        handleManifestExpiredMessage(paramMessage.eventTimeUs, paramMessage.manifestPublishTimeMsInEmsg);
      }
    }
  }
  
  boolean maybeRefreshManifestBeforeLoadingNextChunk(long paramLong)
  {
    if (!this.manifest.dynamic) {}
    for (boolean bool1 = false;; bool1 = true)
    {
      return bool1;
      if (!this.isWaitingForManifestRefresh) {
        break;
      }
    }
    bool1 = false;
    boolean bool2;
    if (this.dynamicMediaPresentationEnded) {
      bool2 = true;
    }
    for (;;)
    {
      bool1 = bool2;
      if (!bool2) {
        break;
      }
      maybeNotifyDashManifestRefreshNeeded();
      bool1 = bool2;
      break;
      Map.Entry localEntry = ceilingExpiryEntryForPublishTime(this.manifest.publishTimeMs);
      bool2 = bool1;
      if (localEntry != null)
      {
        bool2 = bool1;
        if (((Long)localEntry.getValue()).longValue() < paramLong)
        {
          this.expiredManifestPublishTimeUs = ((Long)localEntry.getKey()).longValue();
          notifyManifestPublishTimeExpired();
          bool2 = true;
        }
      }
    }
  }
  
  boolean maybeRefreshManifestOnLoadingError(Chunk paramChunk)
  {
    boolean bool = false;
    if (!this.manifest.dynamic) {}
    label67:
    for (;;)
    {
      return bool;
      if (this.isWaitingForManifestRefresh)
      {
        bool = true;
      }
      else
      {
        if ((this.lastLoadedChunkEndTimeUs != -9223372036854775807L) && (this.lastLoadedChunkEndTimeUs < paramChunk.startTimeUs)) {}
        for (int i = 1;; i = 0)
        {
          if (i == 0) {
            break label67;
          }
          maybeNotifyDashManifestRefreshNeeded();
          bool = true;
          break;
        }
      }
    }
  }
  
  public PlayerTrackEmsgHandler newPlayerTrackEmsgHandler()
  {
    return new PlayerTrackEmsgHandler(new SampleQueue(this.allocator));
  }
  
  void onChunkLoadCompleted(Chunk paramChunk)
  {
    if ((this.lastLoadedChunkEndTimeUs != -9223372036854775807L) || (paramChunk.endTimeUs > this.lastLoadedChunkEndTimeUs)) {
      this.lastLoadedChunkEndTimeUs = paramChunk.endTimeUs;
    }
  }
  
  public void release()
  {
    this.released = true;
    this.handler.removeCallbacksAndMessages(null);
  }
  
  public void updateManifest(DashManifest paramDashManifest)
  {
    this.isWaitingForManifestRefresh = false;
    this.expiredManifestPublishTimeUs = -9223372036854775807L;
    this.manifest = paramDashManifest;
    removePreviouslyExpiredManifestPublishTimeValues();
  }
  
  private static final class ManifestExpiryEventInfo
  {
    public final long eventTimeUs;
    public final long manifestPublishTimeMsInEmsg;
    
    public ManifestExpiryEventInfo(long paramLong1, long paramLong2)
    {
      this.eventTimeUs = paramLong1;
      this.manifestPublishTimeMsInEmsg = paramLong2;
    }
  }
  
  public static abstract interface PlayerEmsgCallback
  {
    public abstract void onDashLiveMediaPresentationEndSignalEncountered();
    
    public abstract void onDashManifestPublishTimeExpired(long paramLong);
    
    public abstract void onDashManifestRefreshRequested();
  }
  
  public final class PlayerTrackEmsgHandler
    implements TrackOutput
  {
    private final MetadataInputBuffer buffer;
    private final FormatHolder formatHolder;
    private final SampleQueue sampleQueue;
    
    PlayerTrackEmsgHandler(SampleQueue paramSampleQueue)
    {
      this.sampleQueue = paramSampleQueue;
      this.formatHolder = new FormatHolder();
      this.buffer = new MetadataInputBuffer();
    }
    
    private MetadataInputBuffer dequeueSample()
    {
      this.buffer.clear();
      if (this.sampleQueue.read(this.formatHolder, this.buffer, false, false, 0L) == -4) {
        this.buffer.flip();
      }
      for (MetadataInputBuffer localMetadataInputBuffer = this.buffer;; localMetadataInputBuffer = null) {
        return localMetadataInputBuffer;
      }
    }
    
    private void onManifestExpiredMessageEncountered(long paramLong1, long paramLong2)
    {
      PlayerEmsgHandler.ManifestExpiryEventInfo localManifestExpiryEventInfo = new PlayerEmsgHandler.ManifestExpiryEventInfo(paramLong1, paramLong2);
      PlayerEmsgHandler.this.handler.sendMessage(PlayerEmsgHandler.this.handler.obtainMessage(2, localManifestExpiryEventInfo));
    }
    
    private void onMediaPresentationEndedMessageEncountered()
    {
      PlayerEmsgHandler.this.handler.sendMessage(PlayerEmsgHandler.this.handler.obtainMessage(1));
    }
    
    private void parseAndDiscardSamples()
    {
      while (this.sampleQueue.hasNextSample())
      {
        Object localObject = dequeueSample();
        if (localObject != null)
        {
          long l = ((MetadataInputBuffer)localObject).timeUs;
          localObject = (EventMessage)PlayerEmsgHandler.this.decoder.decode((MetadataInputBuffer)localObject).get(0);
          if (PlayerEmsgHandler.isPlayerEmsgEvent(((EventMessage)localObject).schemeIdUri, ((EventMessage)localObject).value)) {
            parsePlayerEmsgEvent(l, (EventMessage)localObject);
          }
        }
      }
      this.sampleQueue.discardToRead();
    }
    
    private void parsePlayerEmsgEvent(long paramLong, EventMessage paramEventMessage)
    {
      long l = PlayerEmsgHandler.getManifestPublishTimeMsInEmsg(paramEventMessage);
      if (l == -9223372036854775807L) {}
      for (;;)
      {
        return;
        if (PlayerEmsgHandler.isMessageSignalingMediaPresentationEnded(paramEventMessage)) {
          onMediaPresentationEndedMessageEncountered();
        } else {
          onManifestExpiredMessageEncountered(paramLong, l);
        }
      }
    }
    
    public void format(Format paramFormat)
    {
      this.sampleQueue.format(paramFormat);
    }
    
    public boolean maybeRefreshManifestBeforeLoadingNextChunk(long paramLong)
    {
      return PlayerEmsgHandler.this.maybeRefreshManifestBeforeLoadingNextChunk(paramLong);
    }
    
    public boolean maybeRefreshManifestOnLoadingError(Chunk paramChunk)
    {
      return PlayerEmsgHandler.this.maybeRefreshManifestOnLoadingError(paramChunk);
    }
    
    public void onChunkLoadCompleted(Chunk paramChunk)
    {
      PlayerEmsgHandler.this.onChunkLoadCompleted(paramChunk);
    }
    
    public void release()
    {
      this.sampleQueue.reset();
    }
    
    public int sampleData(ExtractorInput paramExtractorInput, int paramInt, boolean paramBoolean)
      throws IOException, InterruptedException
    {
      return this.sampleQueue.sampleData(paramExtractorInput, paramInt, paramBoolean);
    }
    
    public void sampleData(ParsableByteArray paramParsableByteArray, int paramInt)
    {
      this.sampleQueue.sampleData(paramParsableByteArray, paramInt);
    }
    
    public void sampleMetadata(long paramLong, int paramInt1, int paramInt2, int paramInt3, TrackOutput.CryptoData paramCryptoData)
    {
      this.sampleQueue.sampleMetadata(paramLong, paramInt1, paramInt2, paramInt3, paramCryptoData);
      parseAndDiscardSamples();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/dash/PlayerEmsgHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */