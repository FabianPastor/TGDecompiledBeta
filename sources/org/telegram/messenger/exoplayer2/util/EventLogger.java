package org.telegram.messenger.exoplayer2.util;

import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.PlaybackParameters;
import org.telegram.messenger.exoplayer2.Player.EventListener;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.Timeline.Window;
import org.telegram.messenger.exoplayer2.audio.AudioRendererEventListener;
import org.telegram.messenger.exoplayer2.decoder.DecoderCounters;
import org.telegram.messenger.exoplayer2.drm.DefaultDrmSessionManager;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.metadata.Metadata.Entry;
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
import org.telegram.messenger.exoplayer2.source.ads.AdsMediaSource;
import org.telegram.messenger.exoplayer2.trackselection.MappingTrackSelector;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelectionArray;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.video.VideoRendererEventListener;

public class EventLogger implements EventListener, AudioRendererEventListener, DefaultDrmSessionManager.EventListener, MetadataOutput, MediaSourceEventListener, AdsMediaSource.EventListener, VideoRendererEventListener {
    private static final int MAX_TIMELINE_ITEM_LINES = 3;
    private static final String TAG = "EventLogger";
    private static final NumberFormat TIME_FORMAT = NumberFormat.getInstance(Locale.US);
    private final Period period = new Period();
    private final long startTimeMs = SystemClock.elapsedRealtime();
    private final MappingTrackSelector trackSelector;
    private final Window window = new Window();

    private static String getAdaptiveSupportString(int i, int i2) {
        return i < 2 ? "N/A" : i2 != 0 ? i2 != 8 ? i2 != 16 ? "?" : "YES" : "YES_NOT_SEAMLESS" : "NO";
    }

    private static String getDiscontinuityReasonString(int i) {
        switch (i) {
            case 0:
                return "PERIOD_TRANSITION";
            case 1:
                return "SEEK";
            case 2:
                return "SEEK_ADJUSTMENT";
            case 3:
                return "AD_INSERTION";
            case 4:
                return "INTERNAL";
            default:
                return "?";
        }
    }

    private static String getFormatSupportString(int i) {
        switch (i) {
            case 0:
                return "NO";
            case 1:
                return "NO_UNSUPPORTED_TYPE";
            case 2:
                return "NO_UNSUPPORTED_DRM";
            case 3:
                return "NO_EXCEEDS_CAPABILITIES";
            case 4:
                return "YES";
            default:
                return "?";
        }
    }

    private static String getRepeatModeString(int i) {
        switch (i) {
            case 0:
                return "OFF";
            case 1:
                return "ONE";
            case 2:
                return "ALL";
            default:
                return "?";
        }
    }

    private static String getStateString(int i) {
        switch (i) {
            case 1:
                return "I";
            case 2:
                return "B";
            case 3:
                return "R";
            case 4:
                return "E";
            default:
                return "?";
        }
    }

    private static String getTimelineChangeReasonString(int i) {
        switch (i) {
            case 0:
                return "PREPARED";
            case 1:
                return "RESET";
            case 2:
                return "DYNAMIC";
            default:
                return "?";
        }
    }

    private static String getTrackStatusString(boolean z) {
        return z ? "[X]" : "[ ]";
    }

    public void onAdClicked() {
    }

    public void onAdTapped() {
    }

    public void onDownstreamFormatChanged(int i, Format format, int i2, Object obj, long j) {
    }

    public void onLoadCanceled(DataSpec dataSpec, int i, int i2, Format format, int i3, Object obj, long j, long j2, long j3, long j4, long j5) {
    }

    public void onLoadCompleted(DataSpec dataSpec, int i, int i2, Format format, int i3, Object obj, long j, long j2, long j3, long j4, long j5) {
    }

    public void onLoadStarted(DataSpec dataSpec, int i, int i2, Format format, int i3, Object obj, long j, long j2, long j3) {
    }

    public void onUpstreamDiscarded(int i, long j, long j2) {
    }

    static {
        TIME_FORMAT.setMinimumFractionDigits(2);
        TIME_FORMAT.setMaximumFractionDigits(2);
        TIME_FORMAT.setGroupingUsed(false);
    }

    public EventLogger(MappingTrackSelector mappingTrackSelector) {
        this.trackSelector = mappingTrackSelector;
    }

    public void onLoadingChanged(boolean z) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("loading [");
        stringBuilder.append(z);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
    }

    public void onPlayerStateChanged(boolean z, int i) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("state [");
        stringBuilder.append(getSessionTimeString());
        stringBuilder.append(", ");
        stringBuilder.append(z);
        stringBuilder.append(", ");
        stringBuilder.append(getStateString(i));
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
    }

    public void onRepeatModeChanged(int i) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("repeatMode [");
        stringBuilder.append(getRepeatModeString(i));
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
    }

    public void onShuffleModeEnabledChanged(boolean z) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("shuffleModeEnabled [");
        stringBuilder.append(z);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
    }

    public void onPositionDiscontinuity(int i) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("positionDiscontinuity [");
        stringBuilder.append(getDiscontinuityReasonString(i));
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
    }

    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("playbackParameters ");
        stringBuilder.append(String.format("[speed=%.2f, pitch=%.2f]", new Object[]{Float.valueOf(playbackParameters.speed), Float.valueOf(playbackParameters.pitch)}));
        Log.d(str, stringBuilder.toString());
    }

    public void onTimelineChanged(Timeline timeline, Object obj, int i) {
        obj = timeline.getPeriodCount();
        int windowCount = timeline.getWindowCount();
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("timelineChanged [periodCount=");
        stringBuilder.append(obj);
        stringBuilder.append(", windowCount=");
        stringBuilder.append(windowCount);
        stringBuilder.append(", reason=");
        stringBuilder.append(getTimelineChangeReasonString(i));
        Log.d(str, stringBuilder.toString());
        i = 0;
        for (int i2 = 0; i2 < Math.min(obj, 3); i2++) {
            timeline.getPeriod(i2, this.period);
            String str2 = TAG;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("  period [");
            stringBuilder2.append(getTimeString(this.period.getDurationMs()));
            stringBuilder2.append("]");
            Log.d(str2, stringBuilder2.toString());
        }
        if (obj > 3) {
            Log.d(TAG, "  ...");
        }
        while (i < Math.min(windowCount, 3)) {
            timeline.getWindow(i, this.window);
            obj = TAG;
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("  window [");
            stringBuilder3.append(getTimeString(this.window.getDurationMs()));
            stringBuilder3.append(", ");
            stringBuilder3.append(this.window.isSeekable);
            stringBuilder3.append(", ");
            stringBuilder3.append(this.window.isDynamic);
            stringBuilder3.append("]");
            Log.d(obj, stringBuilder3.toString());
            i++;
        }
        if (windowCount > 3) {
            Log.d(TAG, "  ...");
        }
        Log.d(TAG, "]");
    }

    public void onPlayerError(ExoPlaybackException exoPlaybackException) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("playerFailed [");
        stringBuilder.append(getSessionTimeString());
        stringBuilder.append("]");
        Log.e(str, stringBuilder.toString(), exoPlaybackException);
    }

    public void onTracksChanged(TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {
        trackGroupArray = this.trackSelector.getCurrentMappedTrackInfo();
        if (trackGroupArray == null) {
            Log.d(TAG, "Tracks []");
            return;
        }
        Log.d(TAG, "Tracks [");
        for (int i = 0; i < trackGroupArray.length; i++) {
            int i2;
            TrackGroupArray trackGroups = trackGroupArray.getTrackGroups(i);
            TrackSelection trackSelection = trackSelectionArray.get(i);
            if (trackGroups.length > 0) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("  Renderer:");
                stringBuilder.append(i);
                stringBuilder.append(" [");
                Log.d(str, stringBuilder.toString());
                for (int i3 = 0; i3 < trackGroups.length; i3++) {
                    TrackGroup trackGroup = trackGroups.get(i3);
                    String adaptiveSupportString = getAdaptiveSupportString(trackGroup.length, trackGroupArray.getAdaptiveSupport(i, i3, false));
                    String str2 = TAG;
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("    Group:");
                    stringBuilder2.append(i3);
                    stringBuilder2.append(", adaptive_supported=");
                    stringBuilder2.append(adaptiveSupportString);
                    stringBuilder2.append(" [");
                    Log.d(str2, stringBuilder2.toString());
                    for (int i4 = 0; i4 < trackGroup.length; i4++) {
                        str2 = getTrackStatusString(trackSelection, trackGroup, i4);
                        String formatSupportString = getFormatSupportString(trackGroupArray.getTrackFormatSupport(i, i3, i4));
                        String str3 = TAG;
                        StringBuilder stringBuilder3 = new StringBuilder();
                        stringBuilder3.append("      ");
                        stringBuilder3.append(str2);
                        stringBuilder3.append(" Track:");
                        stringBuilder3.append(i4);
                        stringBuilder3.append(", ");
                        stringBuilder3.append(Format.toLogString(trackGroup.getFormat(i4)));
                        stringBuilder3.append(", supported=");
                        stringBuilder3.append(formatSupportString);
                        Log.d(str3, stringBuilder3.toString());
                    }
                    Log.d(TAG, "    ]");
                }
                if (trackSelection != null) {
                    for (i2 = 0; i2 < trackSelection.length(); i2++) {
                        Metadata metadata = trackSelection.getFormat(i2).metadata;
                        if (metadata != null) {
                            Log.d(TAG, "    Metadata [");
                            printMetadata(metadata, "      ");
                            Log.d(TAG, "    ]");
                            break;
                        }
                    }
                }
                Log.d(TAG, "  ]");
            }
        }
        trackGroupArray = trackGroupArray.getUnassociatedTrackGroups();
        if (trackGroupArray.length > null) {
            Log.d(TAG, "  Renderer:None [");
            for (trackSelectionArray = null; trackSelectionArray < trackGroupArray.length; trackSelectionArray++) {
                String str4 = TAG;
                StringBuilder stringBuilder4 = new StringBuilder();
                stringBuilder4.append("    Group:");
                stringBuilder4.append(trackSelectionArray);
                stringBuilder4.append(" [");
                Log.d(str4, stringBuilder4.toString());
                TrackGroup trackGroup2 = trackGroupArray.get(trackSelectionArray);
                for (i2 = 0; i2 < trackGroup2.length; i2++) {
                    String trackStatusString = getTrackStatusString(false);
                    str = getFormatSupportString(0);
                    String str5 = TAG;
                    StringBuilder stringBuilder5 = new StringBuilder();
                    stringBuilder5.append("      ");
                    stringBuilder5.append(trackStatusString);
                    stringBuilder5.append(" Track:");
                    stringBuilder5.append(i2);
                    stringBuilder5.append(", ");
                    stringBuilder5.append(Format.toLogString(trackGroup2.getFormat(i2)));
                    stringBuilder5.append(", supported=");
                    stringBuilder5.append(str);
                    Log.d(str5, stringBuilder5.toString());
                }
                Log.d(TAG, "    ]");
            }
            Log.d(TAG, "  ]");
        }
        Log.d(TAG, "]");
    }

    public void onSeekProcessed() {
        Log.d(TAG, "seekProcessed");
    }

    public void onMetadata(Metadata metadata) {
        Log.d(TAG, "onMetadata [");
        printMetadata(metadata, "  ");
        Log.d(TAG, "]");
    }

    public void onAudioEnabled(DecoderCounters decoderCounters) {
        decoderCounters = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("audioEnabled [");
        stringBuilder.append(getSessionTimeString());
        stringBuilder.append("]");
        Log.d(decoderCounters, stringBuilder.toString());
    }

    public void onAudioSessionId(int i) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("audioSessionId [");
        stringBuilder.append(i);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
    }

    public void onAudioDecoderInitialized(String str, long j, long j2) {
        j = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("audioDecoderInitialized [");
        stringBuilder.append(getSessionTimeString());
        stringBuilder.append(", ");
        stringBuilder.append(str);
        stringBuilder.append("]");
        Log.d(j, stringBuilder.toString());
    }

    public void onAudioInputFormatChanged(Format format) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("audioFormatChanged [");
        stringBuilder.append(getSessionTimeString());
        stringBuilder.append(", ");
        stringBuilder.append(Format.toLogString(format));
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
    }

    public void onAudioDisabled(DecoderCounters decoderCounters) {
        decoderCounters = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("audioDisabled [");
        stringBuilder.append(getSessionTimeString());
        stringBuilder.append("]");
        Log.d(decoderCounters, stringBuilder.toString());
    }

    public void onAudioSinkUnderrun(int i, long j, long j2) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("audioTrackUnderrun [");
        stringBuilder.append(i);
        stringBuilder.append(", ");
        stringBuilder.append(j);
        stringBuilder.append(", ");
        stringBuilder.append(j2);
        stringBuilder.append("]");
        printInternalError(stringBuilder.toString(), 0);
    }

    public void onVideoEnabled(DecoderCounters decoderCounters) {
        decoderCounters = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("videoEnabled [");
        stringBuilder.append(getSessionTimeString());
        stringBuilder.append("]");
        Log.d(decoderCounters, stringBuilder.toString());
    }

    public void onVideoDecoderInitialized(String str, long j, long j2) {
        j = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("videoDecoderInitialized [");
        stringBuilder.append(getSessionTimeString());
        stringBuilder.append(", ");
        stringBuilder.append(str);
        stringBuilder.append("]");
        Log.d(j, stringBuilder.toString());
    }

    public void onVideoInputFormatChanged(Format format) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("videoFormatChanged [");
        stringBuilder.append(getSessionTimeString());
        stringBuilder.append(", ");
        stringBuilder.append(Format.toLogString(format));
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
    }

    public void onVideoDisabled(DecoderCounters decoderCounters) {
        decoderCounters = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("videoDisabled [");
        stringBuilder.append(getSessionTimeString());
        stringBuilder.append("]");
        Log.d(decoderCounters, stringBuilder.toString());
    }

    public void onDroppedFrames(int i, long j) {
        j = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("droppedFrames [");
        stringBuilder.append(getSessionTimeString());
        stringBuilder.append(", ");
        stringBuilder.append(i);
        stringBuilder.append("]");
        Log.d(j, stringBuilder.toString());
    }

    public void onVideoSizeChanged(int i, int i2, int i3, float f) {
        i3 = TAG;
        f = new StringBuilder();
        f.append("videoSizeChanged [");
        f.append(i);
        f.append(", ");
        f.append(i2);
        f.append("]");
        Log.d(i3, f.toString());
    }

    public void onRenderedFirstFrame(Surface surface) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("renderedFirstFrame [");
        stringBuilder.append(surface);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
    }

    public void onDrmSessionManagerError(Exception exception) {
        printInternalError("drmSessionManagerError", exception);
    }

    public void onDrmKeysRestored() {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("drmKeysRestored [");
        stringBuilder.append(getSessionTimeString());
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
    }

    public void onDrmKeysRemoved() {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("drmKeysRemoved [");
        stringBuilder.append(getSessionTimeString());
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
    }

    public void onDrmKeysLoaded() {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("drmKeysLoaded [");
        stringBuilder.append(getSessionTimeString());
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
    }

    public void onLoadError(DataSpec dataSpec, int i, int i2, Format format, int i3, Object obj, long j, long j2, long j3, long j4, long j5, IOException iOException, boolean z) {
        printInternalError("loadError", iOException);
    }

    public void onAdLoadError(IOException iOException) {
        printInternalError("adLoadError", iOException);
    }

    private void printInternalError(String str, Exception exception) {
        String str2 = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("internalError [");
        stringBuilder.append(getSessionTimeString());
        stringBuilder.append(", ");
        stringBuilder.append(str);
        stringBuilder.append("]");
        Log.e(str2, stringBuilder.toString(), exception);
    }

    private void printMetadata(Metadata metadata, String str) {
        for (int i = 0; i < metadata.length(); i++) {
            Entry entry = metadata.get(i);
            String str2;
            StringBuilder stringBuilder;
            if (entry instanceof TextInformationFrame) {
                TextInformationFrame textInformationFrame = (TextInformationFrame) entry;
                str2 = TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(String.format("%s: value=%s", new Object[]{textInformationFrame.id, textInformationFrame.value}));
                Log.d(str2, stringBuilder.toString());
            } else if (entry instanceof UrlLinkFrame) {
                UrlLinkFrame urlLinkFrame = (UrlLinkFrame) entry;
                str2 = TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(String.format("%s: url=%s", new Object[]{urlLinkFrame.id, urlLinkFrame.url}));
                Log.d(str2, stringBuilder.toString());
            } else if (entry instanceof PrivFrame) {
                PrivFrame privFrame = (PrivFrame) entry;
                str2 = TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(String.format("%s: owner=%s", new Object[]{privFrame.id, privFrame.owner}));
                Log.d(str2, stringBuilder.toString());
            } else if (entry instanceof GeobFrame) {
                GeobFrame geobFrame = (GeobFrame) entry;
                str2 = TAG;
                r7 = new StringBuilder();
                r7.append(str);
                r7.append(String.format("%s: mimeType=%s, filename=%s, description=%s", new Object[]{geobFrame.id, geobFrame.mimeType, geobFrame.filename, geobFrame.description}));
                Log.d(str2, r7.toString());
            } else if (entry instanceof ApicFrame) {
                ApicFrame apicFrame = (ApicFrame) entry;
                str2 = TAG;
                r7 = new StringBuilder();
                r7.append(str);
                r7.append(String.format("%s: mimeType=%s, description=%s", new Object[]{apicFrame.id, apicFrame.mimeType, apicFrame.description}));
                Log.d(str2, r7.toString());
            } else if (entry instanceof CommentFrame) {
                CommentFrame commentFrame = (CommentFrame) entry;
                str2 = TAG;
                r7 = new StringBuilder();
                r7.append(str);
                r7.append(String.format("%s: language=%s, description=%s", new Object[]{commentFrame.id, commentFrame.language, commentFrame.description}));
                Log.d(str2, r7.toString());
            } else if (entry instanceof Id3Frame) {
                Id3Frame id3Frame = (Id3Frame) entry;
                str2 = TAG;
                r4 = new StringBuilder();
                r4.append(str);
                r4.append(String.format("%s", new Object[]{id3Frame.id}));
                Log.d(str2, r4.toString());
            } else if (entry instanceof EventMessage) {
                EventMessage eventMessage = (EventMessage) entry;
                str2 = TAG;
                r7 = new StringBuilder();
                r7.append(str);
                r7.append(String.format("EMSG: scheme=%s, id=%d, value=%s", new Object[]{eventMessage.schemeIdUri, Long.valueOf(eventMessage.id), eventMessage.value}));
                Log.d(str2, r7.toString());
            } else if (entry instanceof SpliceCommand) {
                String format = String.format("SCTE-35 splice command: type=%s.", new Object[]{entry.getClass().getSimpleName()});
                str2 = TAG;
                r4 = new StringBuilder();
                r4.append(str);
                r4.append(format);
                Log.d(str2, r4.toString());
            }
        }
    }

    private String getSessionTimeString() {
        return getTimeString(SystemClock.elapsedRealtime() - this.startTimeMs);
    }

    private static String getTimeString(long j) {
        return j == C0542C.TIME_UNSET ? "?" : TIME_FORMAT.format((double) (((float) j) / 1000.0f));
    }

    private static String getTrackStatusString(TrackSelection trackSelection, TrackGroup trackGroup, int i) {
        trackSelection = (trackSelection == null || trackSelection.getTrackGroup() != trackGroup || trackSelection.indexOf(i) == -1) ? null : true;
        return getTrackStatusString(trackSelection);
    }
}
