package org.telegram.messenger.exoplayer2.util;

import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.PlaybackParameters;
import org.telegram.messenger.exoplayer2.Player.EventListener;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.Timeline.Window;
import org.telegram.messenger.exoplayer2.audio.AudioRendererEventListener;
import org.telegram.messenger.exoplayer2.decoder.DecoderCounters;
import org.telegram.messenger.exoplayer2.drm.DefaultDrmSessionManager.EventListener;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.metadata.MetadataOutput;
import org.telegram.messenger.exoplayer2.metadata.emsg.EventMessage;
import org.telegram.messenger.exoplayer2.metadata.id3.ApicFrame;
import org.telegram.messenger.exoplayer2.metadata.id3.CommentFrame;
import org.telegram.messenger.exoplayer2.metadata.id3.GeobFrame;
import org.telegram.messenger.exoplayer2.metadata.id3.Id3Frame;
import org.telegram.messenger.exoplayer2.metadata.id3.PrivFrame;
import org.telegram.messenger.exoplayer2.metadata.id3.TextInformationFrame;
import org.telegram.messenger.exoplayer2.metadata.id3.UrlLinkFrame;
import org.telegram.messenger.exoplayer2.metadata.scte35.SpliceCommand;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener;
import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.source.ads.AdsMediaSource.EventListener;
import org.telegram.messenger.exoplayer2.trackselection.MappingTrackSelector;
import org.telegram.messenger.exoplayer2.trackselection.MappingTrackSelector.MappedTrackInfo;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelectionArray;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.video.VideoRendererEventListener;

public class EventLogger
  implements Player.EventListener, AudioRendererEventListener, DefaultDrmSessionManager.EventListener, MetadataOutput, MediaSourceEventListener, AdsMediaSource.EventListener, VideoRendererEventListener
{
  private static final int MAX_TIMELINE_ITEM_LINES = 3;
  private static final String TAG = "EventLogger";
  private static final NumberFormat TIME_FORMAT = NumberFormat.getInstance(Locale.US);
  private final Timeline.Period period;
  private final long startTimeMs;
  private final MappingTrackSelector trackSelector;
  private final Timeline.Window window;
  
  static
  {
    TIME_FORMAT.setMinimumFractionDigits(2);
    TIME_FORMAT.setMaximumFractionDigits(2);
    TIME_FORMAT.setGroupingUsed(false);
  }
  
  public EventLogger(MappingTrackSelector paramMappingTrackSelector)
  {
    this.trackSelector = paramMappingTrackSelector;
    this.window = new Timeline.Window();
    this.period = new Timeline.Period();
    this.startTimeMs = SystemClock.elapsedRealtime();
  }
  
  private static String getAdaptiveSupportString(int paramInt1, int paramInt2)
  {
    String str;
    if (paramInt1 < 2) {
      str = "N/A";
    }
    for (;;)
    {
      return str;
      switch (paramInt2)
      {
      default: 
        str = "?";
        break;
      case 16: 
        str = "YES";
        break;
      case 8: 
        str = "YES_NOT_SEAMLESS";
        break;
      case 0: 
        str = "NO";
      }
    }
  }
  
  private static String getDiscontinuityReasonString(int paramInt)
  {
    String str;
    switch (paramInt)
    {
    default: 
      str = "?";
    }
    for (;;)
    {
      return str;
      str = "PERIOD_TRANSITION";
      continue;
      str = "SEEK";
      continue;
      str = "SEEK_ADJUSTMENT";
      continue;
      str = "AD_INSERTION";
      continue;
      str = "INTERNAL";
    }
  }
  
  private static String getFormatSupportString(int paramInt)
  {
    String str;
    switch (paramInt)
    {
    default: 
      str = "?";
    }
    for (;;)
    {
      return str;
      str = "YES";
      continue;
      str = "NO_EXCEEDS_CAPABILITIES";
      continue;
      str = "NO_UNSUPPORTED_DRM";
      continue;
      str = "NO_UNSUPPORTED_TYPE";
      continue;
      str = "NO";
    }
  }
  
  private static String getRepeatModeString(int paramInt)
  {
    String str;
    switch (paramInt)
    {
    default: 
      str = "?";
    }
    for (;;)
    {
      return str;
      str = "OFF";
      continue;
      str = "ONE";
      continue;
      str = "ALL";
    }
  }
  
  private String getSessionTimeString()
  {
    return getTimeString(SystemClock.elapsedRealtime() - this.startTimeMs);
  }
  
  private static String getStateString(int paramInt)
  {
    String str;
    switch (paramInt)
    {
    default: 
      str = "?";
    }
    for (;;)
    {
      return str;
      str = "B";
      continue;
      str = "E";
      continue;
      str = "I";
      continue;
      str = "R";
    }
  }
  
  private static String getTimeString(long paramLong)
  {
    if (paramLong == -9223372036854775807L) {}
    for (String str = "?";; str = TIME_FORMAT.format((float)paramLong / 1000.0F)) {
      return str;
    }
  }
  
  private static String getTimelineChangeReasonString(int paramInt)
  {
    String str;
    switch (paramInt)
    {
    default: 
      str = "?";
    }
    for (;;)
    {
      return str;
      str = "PREPARED";
      continue;
      str = "RESET";
      continue;
      str = "DYNAMIC";
    }
  }
  
  private static String getTrackStatusString(TrackSelection paramTrackSelection, TrackGroup paramTrackGroup, int paramInt)
  {
    if ((paramTrackSelection != null) && (paramTrackSelection.getTrackGroup() == paramTrackGroup) && (paramTrackSelection.indexOf(paramInt) != -1)) {}
    for (boolean bool = true;; bool = false) {
      return getTrackStatusString(bool);
    }
  }
  
  private static String getTrackStatusString(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (String str = "[X]";; str = "[ ]") {
      return str;
    }
  }
  
  private void printInternalError(String paramString, Exception paramException)
  {
    Log.e("EventLogger", "internalError [" + getSessionTimeString() + ", " + paramString + "]", paramException);
  }
  
  private void printMetadata(Metadata paramMetadata, String paramString)
  {
    int i = 0;
    if (i < paramMetadata.length())
    {
      Object localObject = paramMetadata.get(i);
      if ((localObject instanceof TextInformationFrame))
      {
        localObject = (TextInformationFrame)localObject;
        Log.d("EventLogger", paramString + String.format("%s: value=%s", new Object[] { ((TextInformationFrame)localObject).id, ((TextInformationFrame)localObject).value }));
      }
      for (;;)
      {
        i++;
        break;
        if ((localObject instanceof UrlLinkFrame))
        {
          localObject = (UrlLinkFrame)localObject;
          Log.d("EventLogger", paramString + String.format("%s: url=%s", new Object[] { ((UrlLinkFrame)localObject).id, ((UrlLinkFrame)localObject).url }));
        }
        else if ((localObject instanceof PrivFrame))
        {
          localObject = (PrivFrame)localObject;
          Log.d("EventLogger", paramString + String.format("%s: owner=%s", new Object[] { ((PrivFrame)localObject).id, ((PrivFrame)localObject).owner }));
        }
        else if ((localObject instanceof GeobFrame))
        {
          localObject = (GeobFrame)localObject;
          Log.d("EventLogger", paramString + String.format("%s: mimeType=%s, filename=%s, description=%s", new Object[] { ((GeobFrame)localObject).id, ((GeobFrame)localObject).mimeType, ((GeobFrame)localObject).filename, ((GeobFrame)localObject).description }));
        }
        else if ((localObject instanceof ApicFrame))
        {
          localObject = (ApicFrame)localObject;
          Log.d("EventLogger", paramString + String.format("%s: mimeType=%s, description=%s", new Object[] { ((ApicFrame)localObject).id, ((ApicFrame)localObject).mimeType, ((ApicFrame)localObject).description }));
        }
        else if ((localObject instanceof CommentFrame))
        {
          localObject = (CommentFrame)localObject;
          Log.d("EventLogger", paramString + String.format("%s: language=%s, description=%s", new Object[] { ((CommentFrame)localObject).id, ((CommentFrame)localObject).language, ((CommentFrame)localObject).description }));
        }
        else if ((localObject instanceof Id3Frame))
        {
          localObject = (Id3Frame)localObject;
          Log.d("EventLogger", paramString + String.format("%s", new Object[] { ((Id3Frame)localObject).id }));
        }
        else if ((localObject instanceof EventMessage))
        {
          localObject = (EventMessage)localObject;
          Log.d("EventLogger", paramString + String.format("EMSG: scheme=%s, id=%d, value=%s", new Object[] { ((EventMessage)localObject).schemeIdUri, Long.valueOf(((EventMessage)localObject).id), ((EventMessage)localObject).value }));
        }
        else if ((localObject instanceof SpliceCommand))
        {
          localObject = String.format("SCTE-35 splice command: type=%s.", new Object[] { localObject.getClass().getSimpleName() });
          Log.d("EventLogger", paramString + (String)localObject);
        }
      }
    }
  }
  
  public void onAdClicked() {}
  
  public void onAdLoadError(IOException paramIOException)
  {
    printInternalError("adLoadError", paramIOException);
  }
  
  public void onAdTapped() {}
  
  public void onAudioDecoderInitialized(String paramString, long paramLong1, long paramLong2)
  {
    Log.d("EventLogger", "audioDecoderInitialized [" + getSessionTimeString() + ", " + paramString + "]");
  }
  
  public void onAudioDisabled(DecoderCounters paramDecoderCounters)
  {
    Log.d("EventLogger", "audioDisabled [" + getSessionTimeString() + "]");
  }
  
  public void onAudioEnabled(DecoderCounters paramDecoderCounters)
  {
    Log.d("EventLogger", "audioEnabled [" + getSessionTimeString() + "]");
  }
  
  public void onAudioInputFormatChanged(Format paramFormat)
  {
    Log.d("EventLogger", "audioFormatChanged [" + getSessionTimeString() + ", " + Format.toLogString(paramFormat) + "]");
  }
  
  public void onAudioSessionId(int paramInt)
  {
    Log.d("EventLogger", "audioSessionId [" + paramInt + "]");
  }
  
  public void onAudioSinkUnderrun(int paramInt, long paramLong1, long paramLong2)
  {
    printInternalError("audioTrackUnderrun [" + paramInt + ", " + paramLong1 + ", " + paramLong2 + "]", null);
  }
  
  public void onDownstreamFormatChanged(int paramInt1, Format paramFormat, int paramInt2, Object paramObject, long paramLong) {}
  
  public void onDrmKeysLoaded()
  {
    Log.d("EventLogger", "drmKeysLoaded [" + getSessionTimeString() + "]");
  }
  
  public void onDrmKeysRemoved()
  {
    Log.d("EventLogger", "drmKeysRemoved [" + getSessionTimeString() + "]");
  }
  
  public void onDrmKeysRestored()
  {
    Log.d("EventLogger", "drmKeysRestored [" + getSessionTimeString() + "]");
  }
  
  public void onDrmSessionManagerError(Exception paramException)
  {
    printInternalError("drmSessionManagerError", paramException);
  }
  
  public void onDroppedFrames(int paramInt, long paramLong)
  {
    Log.d("EventLogger", "droppedFrames [" + getSessionTimeString() + ", " + paramInt + "]");
  }
  
  public void onLoadCanceled(DataSpec paramDataSpec, int paramInt1, int paramInt2, Format paramFormat, int paramInt3, Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) {}
  
  public void onLoadCompleted(DataSpec paramDataSpec, int paramInt1, int paramInt2, Format paramFormat, int paramInt3, Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) {}
  
  public void onLoadError(DataSpec paramDataSpec, int paramInt1, int paramInt2, Format paramFormat, int paramInt3, Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, IOException paramIOException, boolean paramBoolean)
  {
    printInternalError("loadError", paramIOException);
  }
  
  public void onLoadStarted(DataSpec paramDataSpec, int paramInt1, int paramInt2, Format paramFormat, int paramInt3, Object paramObject, long paramLong1, long paramLong2, long paramLong3) {}
  
  public void onLoadingChanged(boolean paramBoolean)
  {
    Log.d("EventLogger", "loading [" + paramBoolean + "]");
  }
  
  public void onMetadata(Metadata paramMetadata)
  {
    Log.d("EventLogger", "onMetadata [");
    printMetadata(paramMetadata, "  ");
    Log.d("EventLogger", "]");
  }
  
  public void onPlaybackParametersChanged(PlaybackParameters paramPlaybackParameters)
  {
    Log.d("EventLogger", "playbackParameters " + String.format("[speed=%.2f, pitch=%.2f]", new Object[] { Float.valueOf(paramPlaybackParameters.speed), Float.valueOf(paramPlaybackParameters.pitch) }));
  }
  
  public void onPlayerError(ExoPlaybackException paramExoPlaybackException)
  {
    Log.e("EventLogger", "playerFailed [" + getSessionTimeString() + "]", paramExoPlaybackException);
  }
  
  public void onPlayerStateChanged(boolean paramBoolean, int paramInt)
  {
    Log.d("EventLogger", "state [" + getSessionTimeString() + ", " + paramBoolean + ", " + getStateString(paramInt) + "]");
  }
  
  public void onPositionDiscontinuity(int paramInt)
  {
    Log.d("EventLogger", "positionDiscontinuity [" + getDiscontinuityReasonString(paramInt) + "]");
  }
  
  public void onRenderedFirstFrame(Surface paramSurface)
  {
    Log.d("EventLogger", "renderedFirstFrame [" + paramSurface + "]");
  }
  
  public void onRepeatModeChanged(int paramInt)
  {
    Log.d("EventLogger", "repeatMode [" + getRepeatModeString(paramInt) + "]");
  }
  
  public void onSeekProcessed()
  {
    Log.d("EventLogger", "seekProcessed");
  }
  
  public void onShuffleModeEnabledChanged(boolean paramBoolean)
  {
    Log.d("EventLogger", "shuffleModeEnabled [" + paramBoolean + "]");
  }
  
  public void onTimelineChanged(Timeline paramTimeline, Object paramObject, int paramInt)
  {
    int i = paramTimeline.getPeriodCount();
    int j = paramTimeline.getWindowCount();
    Log.d("EventLogger", "timelineChanged [periodCount=" + i + ", windowCount=" + j + ", reason=" + getTimelineChangeReasonString(paramInt));
    for (paramInt = 0; paramInt < Math.min(i, 3); paramInt++)
    {
      paramTimeline.getPeriod(paramInt, this.period);
      Log.d("EventLogger", "  period [" + getTimeString(this.period.getDurationMs()) + "]");
    }
    if (i > 3) {
      Log.d("EventLogger", "  ...");
    }
    for (paramInt = 0; paramInt < Math.min(j, 3); paramInt++)
    {
      paramTimeline.getWindow(paramInt, this.window);
      Log.d("EventLogger", "  window [" + getTimeString(this.window.getDurationMs()) + ", " + this.window.isSeekable + ", " + this.window.isDynamic + "]");
    }
    if (j > 3) {
      Log.d("EventLogger", "  ...");
    }
    Log.d("EventLogger", "]");
  }
  
  public void onTracksChanged(TrackGroupArray paramTrackGroupArray, TrackSelectionArray paramTrackSelectionArray)
  {
    Object localObject1 = this.trackSelector.getCurrentMappedTrackInfo();
    if (localObject1 == null) {
      Log.d("EventLogger", "Tracks []");
    }
    for (;;)
    {
      return;
      Log.d("EventLogger", "Tracks [");
      int i = 0;
      int j;
      if (i < ((MappingTrackSelector.MappedTrackInfo)localObject1).length)
      {
        localObject2 = ((MappingTrackSelector.MappedTrackInfo)localObject1).getTrackGroups(i);
        paramTrackGroupArray = paramTrackSelectionArray.get(i);
        if (((TrackGroupArray)localObject2).length > 0)
        {
          Log.d("EventLogger", "  Renderer:" + i + " [");
          for (j = 0; j < ((TrackGroupArray)localObject2).length; j++)
          {
            TrackGroup localTrackGroup = ((TrackGroupArray)localObject2).get(j);
            String str1 = getAdaptiveSupportString(localTrackGroup.length, ((MappingTrackSelector.MappedTrackInfo)localObject1).getAdaptiveSupport(i, j, false));
            Log.d("EventLogger", "    Group:" + j + ", adaptive_supported=" + str1 + " [");
            for (int k = 0; k < localTrackGroup.length; k++)
            {
              str1 = getTrackStatusString(paramTrackGroupArray, localTrackGroup, k);
              String str2 = getFormatSupportString(((MappingTrackSelector.MappedTrackInfo)localObject1).getTrackFormatSupport(i, j, k));
              Log.d("EventLogger", "      " + str1 + " Track:" + k + ", " + Format.toLogString(localTrackGroup.getFormat(k)) + ", supported=" + str2);
            }
            Log.d("EventLogger", "    ]");
          }
          if (paramTrackGroupArray == null) {}
        }
        for (j = 0;; j++) {
          if (j < paramTrackGroupArray.length())
          {
            localObject2 = paramTrackGroupArray.getFormat(j).metadata;
            if (localObject2 != null)
            {
              Log.d("EventLogger", "    Metadata [");
              printMetadata((Metadata)localObject2, "      ");
              Log.d("EventLogger", "    ]");
            }
          }
          else
          {
            Log.d("EventLogger", "  ]");
            i++;
            break;
          }
        }
      }
      Object localObject2 = ((MappingTrackSelector.MappedTrackInfo)localObject1).getUnassociatedTrackGroups();
      if (((TrackGroupArray)localObject2).length > 0)
      {
        Log.d("EventLogger", "  Renderer:None [");
        for (i = 0; i < ((TrackGroupArray)localObject2).length; i++)
        {
          Log.d("EventLogger", "    Group:" + i + " [");
          paramTrackSelectionArray = ((TrackGroupArray)localObject2).get(i);
          for (j = 0; j < paramTrackSelectionArray.length; j++)
          {
            localObject1 = getTrackStatusString(false);
            paramTrackGroupArray = getFormatSupportString(0);
            Log.d("EventLogger", "      " + (String)localObject1 + " Track:" + j + ", " + Format.toLogString(paramTrackSelectionArray.getFormat(j)) + ", supported=" + paramTrackGroupArray);
          }
          Log.d("EventLogger", "    ]");
        }
        Log.d("EventLogger", "  ]");
      }
      Log.d("EventLogger", "]");
    }
  }
  
  public void onUpstreamDiscarded(int paramInt, long paramLong1, long paramLong2) {}
  
  public void onVideoDecoderInitialized(String paramString, long paramLong1, long paramLong2)
  {
    Log.d("EventLogger", "videoDecoderInitialized [" + getSessionTimeString() + ", " + paramString + "]");
  }
  
  public void onVideoDisabled(DecoderCounters paramDecoderCounters)
  {
    Log.d("EventLogger", "videoDisabled [" + getSessionTimeString() + "]");
  }
  
  public void onVideoEnabled(DecoderCounters paramDecoderCounters)
  {
    Log.d("EventLogger", "videoEnabled [" + getSessionTimeString() + "]");
  }
  
  public void onVideoInputFormatChanged(Format paramFormat)
  {
    Log.d("EventLogger", "videoFormatChanged [" + getSessionTimeString() + ", " + Format.toLogString(paramFormat) + "]");
  }
  
  public void onVideoSizeChanged(int paramInt1, int paramInt2, int paramInt3, float paramFloat)
  {
    Log.d("EventLogger", "videoSizeChanged [" + paramInt1 + ", " + paramInt2 + "]");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/util/EventLogger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */