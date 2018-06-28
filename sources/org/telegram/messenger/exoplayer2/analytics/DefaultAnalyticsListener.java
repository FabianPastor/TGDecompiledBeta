package org.telegram.messenger.exoplayer2.analytics;

import android.net.NetworkInfo;
import android.view.Surface;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.PlaybackParameters;
import org.telegram.messenger.exoplayer2.analytics.AnalyticsListener.EventTime;
import org.telegram.messenger.exoplayer2.decoder.DecoderCounters;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener.LoadEventInfo;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener.MediaLoadData;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelectionArray;

public abstract class DefaultAnalyticsListener implements AnalyticsListener {
    public void onPlayerStateChanged(EventTime eventTime, boolean playWhenReady, int playbackState) {
    }

    public void onTimelineChanged(EventTime eventTime, int reason) {
    }

    public void onPositionDiscontinuity(EventTime eventTime, int reason) {
    }

    public void onSeekStarted(EventTime eventTime) {
    }

    public void onSeekProcessed(EventTime eventTime) {
    }

    public void onPlaybackParametersChanged(EventTime eventTime, PlaybackParameters playbackParameters) {
    }

    public void onRepeatModeChanged(EventTime eventTime, int repeatMode) {
    }

    public void onShuffleModeChanged(EventTime eventTime, boolean shuffleModeEnabled) {
    }

    public void onLoadingChanged(EventTime eventTime, boolean isLoading) {
    }

    public void onPlayerError(EventTime eventTime, ExoPlaybackException error) {
    }

    public void onTracksChanged(EventTime eventTime, TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    public void onLoadStarted(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
    }

    public void onLoadCompleted(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
    }

    public void onLoadCanceled(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
    }

    public void onLoadError(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
    }

    public void onDownstreamFormatChanged(EventTime eventTime, MediaLoadData mediaLoadData) {
    }

    public void onUpstreamDiscarded(EventTime eventTime, MediaLoadData mediaLoadData) {
    }

    public void onMediaPeriodCreated(EventTime eventTime) {
    }

    public void onMediaPeriodReleased(EventTime eventTime) {
    }

    public void onReadingStarted(EventTime eventTime) {
    }

    public void onBandwidthEstimate(EventTime eventTime, int totalLoadTimeMs, long totalBytesLoaded, long bitrateEstimate) {
    }

    public void onViewportSizeChange(EventTime eventTime, int width, int height) {
    }

    public void onNetworkTypeChanged(EventTime eventTime, NetworkInfo networkInfo) {
    }

    public void onMetadata(EventTime eventTime, Metadata metadata) {
    }

    public void onDecoderEnabled(EventTime eventTime, int trackType, DecoderCounters decoderCounters) {
    }

    public void onDecoderInitialized(EventTime eventTime, int trackType, String decoderName, long initializationDurationMs) {
    }

    public void onDecoderInputFormatChanged(EventTime eventTime, int trackType, Format format) {
    }

    public void onDecoderDisabled(EventTime eventTime, int trackType, DecoderCounters decoderCounters) {
    }

    public void onAudioSessionId(EventTime eventTime, int audioSessionId) {
    }

    public void onAudioUnderrun(EventTime eventTime, int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
    }

    public void onDroppedVideoFrames(EventTime eventTime, int droppedFrames, long elapsedMs) {
    }

    public void onVideoSizeChanged(EventTime eventTime, int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
    }

    public void onRenderedFirstFrame(EventTime eventTime, Surface surface) {
    }

    public void onDrmKeysLoaded(EventTime eventTime) {
    }

    public void onDrmSessionManagerError(EventTime eventTime, Exception error) {
    }

    public void onDrmKeysRestored(EventTime eventTime) {
    }

    public void onDrmKeysRemoved(EventTime eventTime) {
    }
}
