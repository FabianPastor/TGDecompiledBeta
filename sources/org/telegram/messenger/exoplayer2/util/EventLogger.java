package org.telegram.messenger.exoplayer2.util;

import android.net.NetworkInfo;
import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import org.telegram.messenger.exoplayer2.C0555C;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.PlaybackParameters;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.Timeline.Window;
import org.telegram.messenger.exoplayer2.analytics.AnalyticsListener;
import org.telegram.messenger.exoplayer2.analytics.AnalyticsListener.EventTime;
import org.telegram.messenger.exoplayer2.decoder.DecoderCounters;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener.LoadEventInfo;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener.MediaLoadData;
import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.trackselection.MappingTrackSelector;
import org.telegram.messenger.exoplayer2.trackselection.MappingTrackSelector.MappedTrackInfo;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelectionArray;

public class EventLogger implements AnalyticsListener {
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

    public void onLoadingChanged(EventTime eventTime, boolean isLoading) {
        logd(eventTime, "loading", Boolean.toString(isLoading));
    }

    public void onPlayerStateChanged(EventTime eventTime, boolean playWhenReady, int state) {
        logd(eventTime, "state", playWhenReady + ", " + getStateString(state));
    }

    public void onRepeatModeChanged(EventTime eventTime, int repeatMode) {
        logd(eventTime, "repeatMode", getRepeatModeString(repeatMode));
    }

    public void onShuffleModeChanged(EventTime eventTime, boolean shuffleModeEnabled) {
        logd(eventTime, "shuffleModeEnabled", Boolean.toString(shuffleModeEnabled));
    }

    public void onPositionDiscontinuity(EventTime eventTime, int reason) {
        logd(eventTime, "positionDiscontinuity", getDiscontinuityReasonString(reason));
    }

    public void onSeekStarted(EventTime eventTime) {
        logd(eventTime, "seekStarted");
    }

    public void onPlaybackParametersChanged(EventTime eventTime, PlaybackParameters playbackParameters) {
        logd(eventTime, "playbackParameters", Util.formatInvariant("speed=%.2f, pitch=%.2f, skipSilence=%s", Float.valueOf(playbackParameters.speed), Float.valueOf(playbackParameters.pitch), Boolean.valueOf(playbackParameters.skipSilence)));
    }

    public void onTimelineChanged(EventTime eventTime, int reason) {
        int i;
        int periodCount = eventTime.timeline.getPeriodCount();
        int windowCount = eventTime.timeline.getWindowCount();
        logd("timelineChanged [" + getEventTimeString(eventTime) + ", periodCount=" + periodCount + ", windowCount=" + windowCount + ", reason=" + getTimelineChangeReasonString(reason));
        for (i = 0; i < Math.min(periodCount, 3); i++) {
            eventTime.timeline.getPeriod(i, this.period);
            logd("  period [" + getTimeString(this.period.getDurationMs()) + "]");
        }
        if (periodCount > 3) {
            logd("  ...");
        }
        for (i = 0; i < Math.min(windowCount, 3); i++) {
            eventTime.timeline.getWindow(i, this.window);
            logd("  window [" + getTimeString(this.window.getDurationMs()) + ", " + this.window.isSeekable + ", " + this.window.isDynamic + "]");
        }
        if (windowCount > 3) {
            logd("  ...");
        }
        logd("]");
    }

    public void onPlayerError(EventTime eventTime, ExoPlaybackException e) {
        loge(eventTime, "playerFailed", e);
    }

    public void onTracksChanged(EventTime eventTime, TrackGroupArray ignored, TrackSelectionArray trackSelections) {
        MappedTrackInfo mappedTrackInfo = this.trackSelector != null ? this.trackSelector.getCurrentMappedTrackInfo() : null;
        if (mappedTrackInfo == null) {
            logd(eventTime, "tracksChanged", "[]");
            return;
        }
        logd("tracksChanged [" + getEventTimeString(eventTime) + ", ");
        int rendererCount = mappedTrackInfo.getRendererCount();
        for (int rendererIndex = 0; rendererIndex < rendererCount; rendererIndex++) {
            int groupIndex;
            TrackGroup trackGroup;
            int trackIndex;
            TrackGroupArray rendererTrackGroups = mappedTrackInfo.getTrackGroups(rendererIndex);
            TrackSelection trackSelection = trackSelections.get(rendererIndex);
            if (rendererTrackGroups.length > 0) {
                logd("  Renderer:" + rendererIndex + " [");
                for (groupIndex = 0; groupIndex < rendererTrackGroups.length; groupIndex++) {
                    trackGroup = rendererTrackGroups.get(groupIndex);
                    logd("    Group:" + groupIndex + ", adaptive_supported=" + getAdaptiveSupportString(trackGroup.length, mappedTrackInfo.getAdaptiveSupport(rendererIndex, groupIndex, false)) + " [");
                    for (trackIndex = 0; trackIndex < trackGroup.length; trackIndex++) {
                        logd("      " + getTrackStatusString(trackSelection, trackGroup, trackIndex) + " Track:" + trackIndex + ", " + Format.toLogString(trackGroup.getFormat(trackIndex)) + ", supported=" + getFormatSupportString(mappedTrackInfo.getTrackSupport(rendererIndex, groupIndex, trackIndex)));
                    }
                    logd("    ]");
                }
                if (trackSelection != null) {
                    for (int selectionIndex = 0; selectionIndex < trackSelection.length(); selectionIndex++) {
                        Metadata metadata = trackSelection.getFormat(selectionIndex).metadata;
                        if (metadata != null) {
                            logd("    Metadata [");
                            printMetadata(metadata, "      ");
                            logd("    ]");
                            break;
                        }
                    }
                }
                logd("  ]");
            }
        }
        TrackGroupArray unassociatedTrackGroups = mappedTrackInfo.getUnmappedTrackGroups();
        if (unassociatedTrackGroups.length > 0) {
            logd("  Renderer:None [");
            for (groupIndex = 0; groupIndex < unassociatedTrackGroups.length; groupIndex++) {
                logd("    Group:" + groupIndex + " [");
                trackGroup = unassociatedTrackGroups.get(groupIndex);
                for (trackIndex = 0; trackIndex < trackGroup.length; trackIndex++) {
                    logd("      " + getTrackStatusString(false) + " Track:" + trackIndex + ", " + Format.toLogString(trackGroup.getFormat(trackIndex)) + ", supported=" + getFormatSupportString(0));
                }
                logd("    ]");
            }
            logd("  ]");
        }
        logd("]");
    }

    public void onSeekProcessed(EventTime eventTime) {
        logd(eventTime, "seekProcessed");
    }

    public void onMetadata(EventTime eventTime, Metadata metadata) {
        logd("metadata [" + getEventTimeString(eventTime) + ", ");
        printMetadata(metadata, "  ");
        logd("]");
    }

    public void onDecoderEnabled(EventTime eventTime, int trackType, DecoderCounters counters) {
        logd(eventTime, "decoderEnabled", getTrackTypeString(trackType));
    }

    public void onAudioSessionId(EventTime eventTime, int audioSessionId) {
        logd(eventTime, "audioSessionId", Integer.toString(audioSessionId));
    }

    public void onDecoderInitialized(EventTime eventTime, int trackType, String decoderName, long initializationDurationMs) {
        logd(eventTime, "decoderInitialized", getTrackTypeString(trackType) + ", " + decoderName);
    }

    public void onDecoderInputFormatChanged(EventTime eventTime, int trackType, Format format) {
        logd(eventTime, "decoderInputFormatChanged", getTrackTypeString(trackType) + ", " + Format.toLogString(format));
    }

    public void onDecoderDisabled(EventTime eventTime, int trackType, DecoderCounters counters) {
        logd(eventTime, "decoderDisabled", getTrackTypeString(trackType));
    }

    public void onAudioUnderrun(EventTime eventTime, int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
        loge(eventTime, "audioTrackUnderrun", bufferSize + ", " + bufferSizeMs + ", " + elapsedSinceLastFeedMs + "]", null);
    }

    public void onDroppedVideoFrames(EventTime eventTime, int count, long elapsedMs) {
        logd(eventTime, "droppedFrames", Integer.toString(count));
    }

    public void onVideoSizeChanged(EventTime eventTime, int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        logd(eventTime, "videoSizeChanged", width + ", " + height);
    }

    public void onRenderedFirstFrame(EventTime eventTime, Surface surface) {
        logd(eventTime, "renderedFirstFrame", surface.toString());
    }

    public void onMediaPeriodCreated(EventTime eventTime) {
        logd(eventTime, "mediaPeriodCreated");
    }

    public void onMediaPeriodReleased(EventTime eventTime) {
        logd(eventTime, "mediaPeriodReleased");
    }

    public void onLoadStarted(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
    }

    public void onLoadError(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
        printInternalError(eventTime, "loadError", error);
    }

    public void onLoadCanceled(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
    }

    public void onLoadCompleted(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
    }

    public void onReadingStarted(EventTime eventTime) {
        logd(eventTime, "mediaPeriodReadingStarted");
    }

    public void onBandwidthEstimate(EventTime eventTime, int totalLoadTimeMs, long totalBytesLoaded, long bitrateEstimate) {
    }

    public void onViewportSizeChange(EventTime eventTime, int width, int height) {
        logd(eventTime, "viewportSizeChanged", width + ", " + height);
    }

    public void onNetworkTypeChanged(EventTime eventTime, NetworkInfo networkInfo) {
        logd(eventTime, "networkTypeChanged", networkInfo == null ? "none" : networkInfo.toString());
    }

    public void onUpstreamDiscarded(EventTime eventTime, MediaLoadData mediaLoadData) {
        logd(eventTime, "upstreamDiscarded", Format.toLogString(mediaLoadData.trackFormat));
    }

    public void onDownstreamFormatChanged(EventTime eventTime, MediaLoadData mediaLoadData) {
        logd(eventTime, "downstreamFormatChanged", Format.toLogString(mediaLoadData.trackFormat));
    }

    public void onDrmSessionManagerError(EventTime eventTime, Exception e) {
        printInternalError(eventTime, "drmSessionManagerError", e);
    }

    public void onDrmKeysRestored(EventTime eventTime) {
        logd(eventTime, "drmKeysRestored");
    }

    public void onDrmKeysRemoved(EventTime eventTime) {
        logd(eventTime, "drmKeysRemoved");
    }

    public void onDrmKeysLoaded(EventTime eventTime) {
        logd(eventTime, "drmKeysLoaded");
    }

    protected void logd(String msg) {
        Log.d(TAG, msg);
    }

    protected void loge(String msg, Throwable tr) {
        Log.e(TAG, msg, tr);
    }

    private void logd(EventTime eventTime, String eventName) {
        logd(getEventString(eventTime, eventName));
    }

    private void logd(EventTime eventTime, String eventName, String eventDescription) {
        logd(getEventString(eventTime, eventName, eventDescription));
    }

    private void loge(EventTime eventTime, String eventName, Throwable throwable) {
        loge(getEventString(eventTime, eventName), throwable);
    }

    private void loge(EventTime eventTime, String eventName, String eventDescription, Throwable throwable) {
        loge(getEventString(eventTime, eventName, eventDescription), throwable);
    }

    private void printInternalError(EventTime eventTime, String type, Exception e) {
        loge(eventTime, "internalError", type, e);
    }

    private void printMetadata(Metadata metadata, String prefix) {
        for (int i = 0; i < metadata.length(); i++) {
            logd(prefix + metadata.get(i));
        }
    }

    private String getEventString(EventTime eventTime, String eventName) {
        return eventName + " [" + getEventTimeString(eventTime) + "]";
    }

    private String getEventString(EventTime eventTime, String eventName, String eventDescription) {
        return eventName + " [" + getEventTimeString(eventTime) + ", " + eventDescription + "]";
    }

    private String getEventTimeString(EventTime eventTime) {
        String windowPeriodString = "window=" + eventTime.windowIndex;
        if (eventTime.mediaPeriodId != null) {
            windowPeriodString = windowPeriodString + ", period=" + eventTime.mediaPeriodId.periodIndex;
            if (eventTime.mediaPeriodId.isAd()) {
                windowPeriodString = (windowPeriodString + ", adGroup=" + eventTime.mediaPeriodId.adGroupIndex) + ", ad=" + eventTime.mediaPeriodId.adIndexInAdGroup;
            }
        }
        return getTimeString(eventTime.realtimeMs - this.startTimeMs) + ", " + getTimeString(eventTime.currentPlaybackPositionMs) + ", " + windowPeriodString;
    }

    private static String getTimeString(long timeMs) {
        return timeMs == C0555C.TIME_UNSET ? "?" : TIME_FORMAT.format((double) (((float) timeMs) / 1000.0f));
    }

    private static String getStateString(int state) {
        switch (state) {
            case 1:
                return "IDLE";
            case 2:
                return "BUFFERING";
            case 3:
                return "READY";
            case 4:
                return "ENDED";
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
        switch (adaptiveSupport) {
            case 0:
                return "NO";
            case 8:
                return "YES_NOT_SEAMLESS";
            case 16:
                return "YES";
            default:
                return "?";
        }
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

    private static String getTrackTypeString(int trackType) {
        switch (trackType) {
            case 0:
                return "default";
            case 1:
                return MimeTypes.BASE_TYPE_AUDIO;
            case 2:
                return MimeTypes.BASE_TYPE_VIDEO;
            case 3:
                return MimeTypes.BASE_TYPE_TEXT;
            case 4:
                return TtmlNode.TAG_METADATA;
            case 5:
                return "none";
            default:
                if (trackType >= 10000) {
                    return "custom (" + trackType + ")";
                }
                return "?";
        }
    }
}
