package org.telegram.messenger.exoplayer2.util;

import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import org.telegram.messenger.exoplayer2.C0539C;
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
import org.telegram.messenger.exoplayer2.trackselection.MappingTrackSelector.MappedTrackInfo;
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

    static {
        TIME_FORMAT.setMinimumFractionDigits(2);
        TIME_FORMAT.setMaximumFractionDigits(2);
        TIME_FORMAT.setGroupingUsed(false);
    }

    public EventLogger(MappingTrackSelector trackSelector) {
        this.trackSelector = trackSelector;
    }

    public void onLoadingChanged(boolean isLoading) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("loading [");
        stringBuilder.append(isLoading);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
    }

    public void onPlayerStateChanged(boolean playWhenReady, int state) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("state [");
        stringBuilder.append(getSessionTimeString());
        stringBuilder.append(", ");
        stringBuilder.append(playWhenReady);
        stringBuilder.append(", ");
        stringBuilder.append(getStateString(state));
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
    }

    public void onRepeatModeChanged(int repeatMode) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("repeatMode [");
        stringBuilder.append(getRepeatModeString(repeatMode));
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
    }

    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("shuffleModeEnabled [");
        stringBuilder.append(shuffleModeEnabled);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
    }

    public void onPositionDiscontinuity(int reason) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("positionDiscontinuity [");
        stringBuilder.append(getDiscontinuityReasonString(reason));
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

    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
        int periodCount = timeline.getPeriodCount();
        int windowCount = timeline.getWindowCount();
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("timelineChanged [periodCount=");
        stringBuilder.append(periodCount);
        stringBuilder.append(", windowCount=");
        stringBuilder.append(windowCount);
        stringBuilder.append(", reason=");
        stringBuilder.append(getTimelineChangeReasonString(reason));
        Log.d(str, stringBuilder.toString());
        int i = 0;
        for (int i2 = 0; i2 < Math.min(periodCount, 3); i2++) {
            timeline.getPeriod(i2, this.period);
            String str2 = TAG;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("  period [");
            stringBuilder2.append(getTimeString(this.period.getDurationMs()));
            stringBuilder2.append("]");
            Log.d(str2, stringBuilder2.toString());
        }
        if (periodCount > 3) {
            Log.d(TAG, "  ...");
        }
        while (i < Math.min(windowCount, 3)) {
            timeline.getWindow(i, this.window);
            String str3 = TAG;
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("  window [");
            stringBuilder2.append(getTimeString(this.window.getDurationMs()));
            stringBuilder2.append(", ");
            stringBuilder2.append(this.window.isSeekable);
            stringBuilder2.append(", ");
            stringBuilder2.append(this.window.isDynamic);
            stringBuilder2.append("]");
            Log.d(str3, stringBuilder2.toString());
            i++;
        }
        if (windowCount > 3) {
            Log.d(TAG, "  ...");
        }
        Log.d(TAG, "]");
    }

    public void onPlayerError(ExoPlaybackException e) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("playerFailed [");
        stringBuilder.append(getSessionTimeString());
        stringBuilder.append("]");
        Log.e(str, stringBuilder.toString(), e);
    }

    public void onTracksChanged(TrackGroupArray ignored, TrackSelectionArray trackSelections) {
        MappedTrackInfo mappedTrackInfo = this.trackSelector.getCurrentMappedTrackInfo();
        if (mappedTrackInfo == null) {
            Log.d(TAG, "Tracks []");
            return;
        }
        Log.d(TAG, "Tracks [");
        for (int rendererIndex = 0; rendererIndex < mappedTrackInfo.length; rendererIndex++) {
            int groupIndex;
            TrackGroupArray rendererTrackGroups = mappedTrackInfo.getTrackGroups(rendererIndex);
            TrackSelection trackSelection = trackSelections.get(rendererIndex);
            if (rendererTrackGroups.length > 0) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("  Renderer:");
                stringBuilder.append(rendererIndex);
                stringBuilder.append(" [");
                Log.d(str, stringBuilder.toString());
                for (groupIndex = 0; groupIndex < rendererTrackGroups.length; groupIndex++) {
                    TrackGroup trackGroup = rendererTrackGroups.get(groupIndex);
                    String adaptiveSupport = getAdaptiveSupportString(trackGroup.length, mappedTrackInfo.getAdaptiveSupport(rendererIndex, groupIndex, false));
                    String str2 = TAG;
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("    Group:");
                    stringBuilder2.append(groupIndex);
                    stringBuilder2.append(", adaptive_supported=");
                    stringBuilder2.append(adaptiveSupport);
                    stringBuilder2.append(" [");
                    Log.d(str2, stringBuilder2.toString());
                    for (int trackIndex = 0; trackIndex < trackGroup.length; trackIndex++) {
                        String status = getTrackStatusString(trackSelection, trackGroup, trackIndex);
                        String formatSupport = getFormatSupportString(mappedTrackInfo.getTrackFormatSupport(rendererIndex, groupIndex, trackIndex));
                        String str3 = TAG;
                        StringBuilder stringBuilder3 = new StringBuilder();
                        stringBuilder3.append("      ");
                        stringBuilder3.append(status);
                        stringBuilder3.append(" Track:");
                        stringBuilder3.append(trackIndex);
                        stringBuilder3.append(", ");
                        stringBuilder3.append(Format.toLogString(trackGroup.getFormat(trackIndex)));
                        stringBuilder3.append(", supported=");
                        stringBuilder3.append(formatSupport);
                        Log.d(str3, stringBuilder3.toString());
                    }
                    Log.d(TAG, "    ]");
                }
                if (trackSelection != null) {
                    for (groupIndex = 0; groupIndex < trackSelection.length(); groupIndex++) {
                        Metadata metadata = trackSelection.getFormat(groupIndex).metadata;
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
        TrackSelectionArray trackSelectionArray = trackSelections;
        TrackGroupArray unassociatedTrackGroups = mappedTrackInfo.getUnassociatedTrackGroups();
        if (unassociatedTrackGroups.length > 0) {
            Log.d(TAG, "  Renderer:None [");
            for (int groupIndex2 = 0; groupIndex2 < unassociatedTrackGroups.length; groupIndex2++) {
                String str4 = TAG;
                StringBuilder stringBuilder4 = new StringBuilder();
                stringBuilder4.append("    Group:");
                stringBuilder4.append(groupIndex2);
                stringBuilder4.append(" [");
                Log.d(str4, stringBuilder4.toString());
                TrackGroup trackGroup2 = unassociatedTrackGroups.get(groupIndex2);
                for (groupIndex = 0; groupIndex < trackGroup2.length; groupIndex++) {
                    String status2 = getTrackStatusString(false);
                    adaptiveSupport = getFormatSupportString(0);
                    str2 = TAG;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("      ");
                    stringBuilder2.append(status2);
                    stringBuilder2.append(" Track:");
                    stringBuilder2.append(groupIndex);
                    stringBuilder2.append(", ");
                    stringBuilder2.append(Format.toLogString(trackGroup2.getFormat(groupIndex)));
                    stringBuilder2.append(", supported=");
                    stringBuilder2.append(adaptiveSupport);
                    Log.d(str2, stringBuilder2.toString());
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

    public void onAudioEnabled(DecoderCounters counters) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("audioEnabled [");
        stringBuilder.append(getSessionTimeString());
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
    }

    public void onAudioSessionId(int audioSessionId) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("audioSessionId [");
        stringBuilder.append(audioSessionId);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
    }

    public void onAudioDecoderInitialized(String decoderName, long elapsedRealtimeMs, long initializationDurationMs) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("audioDecoderInitialized [");
        stringBuilder.append(getSessionTimeString());
        stringBuilder.append(", ");
        stringBuilder.append(decoderName);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
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

    public void onAudioDisabled(DecoderCounters counters) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("audioDisabled [");
        stringBuilder.append(getSessionTimeString());
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
    }

    public void onAudioSinkUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("audioTrackUnderrun [");
        stringBuilder.append(bufferSize);
        stringBuilder.append(", ");
        stringBuilder.append(bufferSizeMs);
        stringBuilder.append(", ");
        stringBuilder.append(elapsedSinceLastFeedMs);
        stringBuilder.append("]");
        printInternalError(stringBuilder.toString(), null);
    }

    public void onVideoEnabled(DecoderCounters counters) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("videoEnabled [");
        stringBuilder.append(getSessionTimeString());
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
    }

    public void onVideoDecoderInitialized(String decoderName, long elapsedRealtimeMs, long initializationDurationMs) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("videoDecoderInitialized [");
        stringBuilder.append(getSessionTimeString());
        stringBuilder.append(", ");
        stringBuilder.append(decoderName);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
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

    public void onVideoDisabled(DecoderCounters counters) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("videoDisabled [");
        stringBuilder.append(getSessionTimeString());
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
    }

    public void onDroppedFrames(int count, long elapsed) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("droppedFrames [");
        stringBuilder.append(getSessionTimeString());
        stringBuilder.append(", ");
        stringBuilder.append(count);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
    }

    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("videoSizeChanged [");
        stringBuilder.append(width);
        stringBuilder.append(", ");
        stringBuilder.append(height);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
    }

    public void onRenderedFirstFrame(Surface surface) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("renderedFirstFrame [");
        stringBuilder.append(surface);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
    }

    public void onDrmSessionManagerError(Exception e) {
        printInternalError("drmSessionManagerError", e);
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

    public void onLoadStarted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs) {
    }

    public void onLoadError(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded, IOException error, boolean wasCanceled) {
        printInternalError("loadError", error);
    }

    public void onLoadCanceled(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
    }

    public void onLoadCompleted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
    }

    public void onUpstreamDiscarded(int trackType, long mediaStartTimeMs, long mediaEndTimeMs) {
    }

    public void onDownstreamFormatChanged(int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaTimeMs) {
    }

    public void onAdLoadError(IOException error) {
        printInternalError("adLoadError", error);
    }

    public void onAdClicked() {
    }

    public void onAdTapped() {
    }

    private void printInternalError(String type, Exception e) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("internalError [");
        stringBuilder.append(getSessionTimeString());
        stringBuilder.append(", ");
        stringBuilder.append(type);
        stringBuilder.append("]");
        Log.e(str, stringBuilder.toString(), e);
    }

    private void printMetadata(Metadata metadata, String prefix) {
        for (int i = 0; i < metadata.length(); i++) {
            Entry entry = metadata.get(i);
            String str;
            StringBuilder stringBuilder;
            if (entry instanceof TextInformationFrame) {
                TextInformationFrame textInformationFrame = (TextInformationFrame) entry;
                str = TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append(prefix);
                stringBuilder.append(String.format("%s: value=%s", new Object[]{textInformationFrame.id, textInformationFrame.value}));
                Log.d(str, stringBuilder.toString());
            } else if (entry instanceof UrlLinkFrame) {
                UrlLinkFrame urlLinkFrame = (UrlLinkFrame) entry;
                str = TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append(prefix);
                stringBuilder.append(String.format("%s: url=%s", new Object[]{urlLinkFrame.id, urlLinkFrame.url}));
                Log.d(str, stringBuilder.toString());
            } else if (entry instanceof PrivFrame) {
                PrivFrame privFrame = (PrivFrame) entry;
                str = TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append(prefix);
                stringBuilder.append(String.format("%s: owner=%s", new Object[]{privFrame.id, privFrame.owner}));
                Log.d(str, stringBuilder.toString());
            } else if (entry instanceof GeobFrame) {
                GeobFrame geobFrame = (GeobFrame) entry;
                r7 = TAG;
                r8 = new StringBuilder();
                r8.append(prefix);
                r8.append(String.format("%s: mimeType=%s, filename=%s, description=%s", new Object[]{geobFrame.id, geobFrame.mimeType, geobFrame.filename, geobFrame.description}));
                Log.d(r7, r8.toString());
            } else if (entry instanceof ApicFrame) {
                ApicFrame apicFrame = (ApicFrame) entry;
                r7 = TAG;
                r8 = new StringBuilder();
                r8.append(prefix);
                r8.append(String.format("%s: mimeType=%s, description=%s", new Object[]{apicFrame.id, apicFrame.mimeType, apicFrame.description}));
                Log.d(r7, r8.toString());
            } else if (entry instanceof CommentFrame) {
                CommentFrame commentFrame = (CommentFrame) entry;
                r7 = TAG;
                r8 = new StringBuilder();
                r8.append(prefix);
                r8.append(String.format("%s: language=%s, description=%s", new Object[]{commentFrame.id, commentFrame.language, commentFrame.description}));
                Log.d(r7, r8.toString());
            } else if (entry instanceof Id3Frame) {
                Id3Frame id3Frame = (Id3Frame) entry;
                r4 = TAG;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(prefix);
                stringBuilder2.append(String.format("%s", new Object[]{id3Frame.id}));
                Log.d(r4, stringBuilder2.toString());
            } else if (entry instanceof EventMessage) {
                EventMessage eventMessage = (EventMessage) entry;
                r7 = TAG;
                r8 = new StringBuilder();
                r8.append(prefix);
                r8.append(String.format("EMSG: scheme=%s, id=%d, value=%s", new Object[]{eventMessage.schemeIdUri, Long.valueOf(eventMessage.id), eventMessage.value}));
                Log.d(r7, r8.toString());
            } else if (entry instanceof SpliceCommand) {
                String description = String.format("SCTE-35 splice command: type=%s.", new Object[]{entry.getClass().getSimpleName()});
                r4 = TAG;
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append(prefix);
                stringBuilder3.append(description);
                Log.d(r4, stringBuilder3.toString());
            }
        }
    }

    private String getSessionTimeString() {
        return getTimeString(SystemClock.elapsedRealtime() - this.startTimeMs);
    }

    private static String getTimeString(long timeMs) {
        return timeMs == C0539C.TIME_UNSET ? "?" : TIME_FORMAT.format((double) (((float) timeMs) / 1000.0f));
    }

    private static String getStateString(int state) {
        switch (state) {
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

    private static String getFormatSupportString(int formatSupport) {
        switch (formatSupport) {
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

    private static String getAdaptiveSupportString(int trackCount, int adaptiveSupport) {
        if (trackCount < 2) {
            return "N/A";
        }
        if (adaptiveSupport == 0) {
            return "NO";
        }
        if (adaptiveSupport == 8) {
            return "YES_NOT_SEAMLESS";
        }
        if (adaptiveSupport != 16) {
            return "?";
        }
        return "YES";
    }

    private static String getTrackStatusString(TrackSelection selection, TrackGroup group, int trackIndex) {
        boolean z = (selection == null || selection.getTrackGroup() != group || selection.indexOf(trackIndex) == -1) ? false : true;
        return getTrackStatusString(z);
    }

    private static String getTrackStatusString(boolean enabled) {
        return enabled ? "[X]" : "[ ]";
    }

    private static String getRepeatModeString(int repeatMode) {
        switch (repeatMode) {
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

    private static String getDiscontinuityReasonString(int reason) {
        switch (reason) {
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

    private static String getTimelineChangeReasonString(int reason) {
        switch (reason) {
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
}
