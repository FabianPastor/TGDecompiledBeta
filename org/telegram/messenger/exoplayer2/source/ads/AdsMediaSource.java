package org.telegram.messenger.exoplayer2.source.ads;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.source.DeferredMediaPeriod;
import org.telegram.messenger.exoplayer2.source.ExtractorMediaSource;
import org.telegram.messenger.exoplayer2.source.MediaPeriod;
import org.telegram.messenger.exoplayer2.source.MediaSource;
import org.telegram.messenger.exoplayer2.source.MediaSource.Listener;
import org.telegram.messenger.exoplayer2.source.MediaSource.MediaPeriodId;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class AdsMediaSource implements MediaSource {
    private static final String TAG = "AdsMediaSource";
    private long[][] adDurationsUs;
    private MediaSource[][] adGroupMediaSources;
    private final MediaSourceFactory adMediaSourceFactory;
    private AdPlaybackState adPlaybackState;
    private final ViewGroup adUiViewGroup;
    private final AdsLoader adsLoader;
    private final ComponentListener componentListener;
    private Object contentManifest;
    private final MediaSource contentMediaSource;
    private Timeline contentTimeline;
    private final Map<MediaSource, List<DeferredMediaPeriod>> deferredMediaPeriodByAdMediaSource;
    private final Handler eventHandler;
    private final EventListener eventListener;
    private Listener listener;
    private final Handler mainHandler;
    private final Period period;
    private ExoPlayer player;
    private Handler playerHandler;
    private volatile boolean released;

    public interface MediaSourceFactory {
        MediaSource createMediaSource(Uri uri, Handler handler, MediaSourceEventListener mediaSourceEventListener);

        int[] getSupportedTypes();
    }

    private final class ComponentListener implements org.telegram.messenger.exoplayer2.source.ads.AdsLoader.EventListener {
        private ComponentListener() {
        }

        public void onAdPlaybackState(final AdPlaybackState adPlaybackState) {
            if (!AdsMediaSource.this.released) {
                AdsMediaSource.this.playerHandler.post(new Runnable() {
                    public void run() {
                        if (!AdsMediaSource.this.released) {
                            AdsMediaSource.this.onAdPlaybackState(adPlaybackState);
                        }
                    }
                });
            }
        }

        public void onAdClicked() {
            if (AdsMediaSource.this.eventHandler != null && AdsMediaSource.this.eventListener != null) {
                AdsMediaSource.this.eventHandler.post(new Runnable() {
                    public void run() {
                        if (!AdsMediaSource.this.released) {
                            AdsMediaSource.this.eventListener.onAdClicked();
                        }
                    }
                });
            }
        }

        public void onAdTapped() {
            if (AdsMediaSource.this.eventHandler != null && AdsMediaSource.this.eventListener != null) {
                AdsMediaSource.this.eventHandler.post(new Runnable() {
                    public void run() {
                        if (!AdsMediaSource.this.released) {
                            AdsMediaSource.this.eventListener.onAdTapped();
                        }
                    }
                });
            }
        }

        public void onLoadError(final IOException error) {
            if (!AdsMediaSource.this.released) {
                AdsMediaSource.this.playerHandler.post(new Runnable() {
                    public void run() {
                        if (!AdsMediaSource.this.released) {
                            AdsMediaSource.this.onLoadError(error);
                        }
                    }
                });
            }
        }
    }

    public interface EventListener extends MediaSourceEventListener {
        void onAdClicked();

        void onAdLoadError(IOException iOException);

        void onAdTapped();
    }

    public AdsMediaSource(MediaSource contentMediaSource, Factory dataSourceFactory, AdsLoader adsLoader, ViewGroup adUiViewGroup) {
        this(contentMediaSource, dataSourceFactory, adsLoader, adUiViewGroup, null, null);
    }

    public AdsMediaSource(MediaSource contentMediaSource, Factory dataSourceFactory, AdsLoader adsLoader, ViewGroup adUiViewGroup, Handler eventHandler, EventListener eventListener) {
        this(contentMediaSource, new ExtractorMediaSource.Factory(dataSourceFactory), adsLoader, adUiViewGroup, eventHandler, eventListener);
    }

    public AdsMediaSource(MediaSource contentMediaSource, MediaSourceFactory adMediaSourceFactory, AdsLoader adsLoader, ViewGroup adUiViewGroup, Handler eventHandler, EventListener eventListener) {
        this.contentMediaSource = contentMediaSource;
        this.adMediaSourceFactory = adMediaSourceFactory;
        this.adsLoader = adsLoader;
        this.adUiViewGroup = adUiViewGroup;
        this.eventHandler = eventHandler;
        this.eventListener = eventListener;
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.componentListener = new ComponentListener();
        this.deferredMediaPeriodByAdMediaSource = new HashMap();
        this.period = new Period();
        this.adGroupMediaSources = new MediaSource[0][];
        this.adDurationsUs = new long[0][];
        adsLoader.setSupportedContentTypes(adMediaSourceFactory.getSupportedTypes());
    }

    public void prepareSource(final ExoPlayer player, boolean isTopLevelSource, Listener listener) {
        Assertions.checkArgument(isTopLevelSource);
        Assertions.checkState(this.listener == null, MediaSource.MEDIA_SOURCE_REUSED_ERROR_MESSAGE);
        this.listener = listener;
        this.player = player;
        this.playerHandler = new Handler();
        this.contentMediaSource.prepareSource(player, false, new Listener() {
            public void onSourceInfoRefreshed(MediaSource source, Timeline timeline, Object manifest) {
                AdsMediaSource.this.onContentSourceInfoRefreshed(timeline, manifest);
            }
        });
        this.mainHandler.post(new Runnable() {
            public void run() {
                AdsMediaSource.this.adsLoader.attachPlayer(player, AdsMediaSource.this.componentListener, AdsMediaSource.this.adUiViewGroup);
            }
        });
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
        this.contentMediaSource.maybeThrowSourceInfoRefreshError();
        for (MediaSource[] mediaSources : this.adGroupMediaSources) {
            for (MediaSource mediaSource : r5[r4]) {
                if (mediaSource != null) {
                    mediaSource.maybeThrowSourceInfoRefreshError();
                }
            }
        }
    }

    public MediaPeriod createPeriod(MediaPeriodId id, Allocator allocator) {
        if (this.adPlaybackState.adGroupCount <= 0 || !id.isAd()) {
            DeferredMediaPeriod mediaPeriod = new DeferredMediaPeriod(this.contentMediaSource, id, allocator);
            mediaPeriod.createPeriod();
            return mediaPeriod;
        }
        final int adGroupIndex = id.adGroupIndex;
        final int adIndexInAdGroup = id.adIndexInAdGroup;
        if (this.adGroupMediaSources[adGroupIndex].length <= adIndexInAdGroup) {
            final MediaSource adMediaSource = this.adMediaSourceFactory.createMediaSource(this.adPlaybackState.adUris[id.adGroupIndex][id.adIndexInAdGroup], this.eventHandler, this.eventListener);
            int oldAdCount = this.adGroupMediaSources[id.adGroupIndex].length;
            if (adIndexInAdGroup >= oldAdCount) {
                int adCount = adIndexInAdGroup + 1;
                this.adGroupMediaSources[adGroupIndex] = (MediaSource[]) Arrays.copyOf(this.adGroupMediaSources[adGroupIndex], adCount);
                this.adDurationsUs[adGroupIndex] = Arrays.copyOf(this.adDurationsUs[adGroupIndex], adCount);
                Arrays.fill(this.adDurationsUs[adGroupIndex], oldAdCount, adCount, C.TIME_UNSET);
            }
            this.adGroupMediaSources[adGroupIndex][adIndexInAdGroup] = adMediaSource;
            this.deferredMediaPeriodByAdMediaSource.put(adMediaSource, new ArrayList());
            adMediaSource.prepareSource(this.player, false, new Listener() {
                public void onSourceInfoRefreshed(MediaSource source, Timeline timeline, Object manifest) {
                    AdsMediaSource.this.onAdSourceInfoRefreshed(adMediaSource, adGroupIndex, adIndexInAdGroup, timeline);
                }
            });
        }
        MediaSource mediaSource = this.adGroupMediaSources[adGroupIndex][adIndexInAdGroup];
        DeferredMediaPeriod deferredMediaPeriod = new DeferredMediaPeriod(mediaSource, new MediaPeriodId(0), allocator);
        List<DeferredMediaPeriod> mediaPeriods = (List) this.deferredMediaPeriodByAdMediaSource.get(mediaSource);
        if (mediaPeriods == null) {
            deferredMediaPeriod.createPeriod();
            return deferredMediaPeriod;
        }
        mediaPeriods.add(deferredMediaPeriod);
        return deferredMediaPeriod;
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        ((DeferredMediaPeriod) mediaPeriod).releasePeriod();
    }

    public void releaseSource() {
        this.released = true;
        this.contentMediaSource.releaseSource();
        for (MediaSource[] mediaSources : this.adGroupMediaSources) {
            for (MediaSource mediaSource : r5[r4]) {
                if (mediaSource != null) {
                    mediaSource.releaseSource();
                }
            }
        }
        this.mainHandler.post(new Runnable() {
            public void run() {
                AdsMediaSource.this.adsLoader.detachPlayer();
            }
        });
    }

    private void onAdPlaybackState(AdPlaybackState adPlaybackState) {
        if (this.adPlaybackState == null) {
            this.adGroupMediaSources = new MediaSource[adPlaybackState.adGroupCount][];
            Arrays.fill(this.adGroupMediaSources, new MediaSource[0]);
            this.adDurationsUs = new long[adPlaybackState.adGroupCount][];
            Arrays.fill(this.adDurationsUs, new long[0]);
        }
        this.adPlaybackState = adPlaybackState;
        maybeUpdateSourceInfo();
    }

    private void onLoadError(final IOException error) {
        Log.w(TAG, "Ad load error", error);
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new Runnable() {
                public void run() {
                    if (!AdsMediaSource.this.released) {
                        AdsMediaSource.this.eventListener.onAdLoadError(error);
                    }
                }
            });
        }
    }

    private void onContentSourceInfoRefreshed(Timeline timeline, Object manifest) {
        this.contentTimeline = timeline;
        this.contentManifest = manifest;
        maybeUpdateSourceInfo();
    }

    private void onAdSourceInfoRefreshed(MediaSource mediaSource, int adGroupIndex, int adIndexInAdGroup, Timeline timeline) {
        boolean z = true;
        if (timeline.getPeriodCount() != 1) {
            z = false;
        }
        Assertions.checkArgument(z);
        this.adDurationsUs[adGroupIndex][adIndexInAdGroup] = timeline.getPeriod(0, this.period).getDurationUs();
        if (this.deferredMediaPeriodByAdMediaSource.containsKey(mediaSource)) {
            List<DeferredMediaPeriod> mediaPeriods = (List) this.deferredMediaPeriodByAdMediaSource.get(mediaSource);
            for (int i = 0; i < mediaPeriods.size(); i++) {
                ((DeferredMediaPeriod) mediaPeriods.get(i)).createPeriod();
            }
            this.deferredMediaPeriodByAdMediaSource.remove(mediaSource);
        }
        maybeUpdateSourceInfo();
    }

    private void maybeUpdateSourceInfo() {
        if (this.adPlaybackState != null && this.contentTimeline != null) {
            this.listener.onSourceInfoRefreshed(this, this.adPlaybackState.adGroupCount == 0 ? this.contentTimeline : new SinglePeriodAdTimeline(this.contentTimeline, this.adPlaybackState.adGroupTimesUs, this.adPlaybackState.adCounts, this.adPlaybackState.adsLoadedCounts, this.adPlaybackState.adsPlayedCounts, this.adDurationsUs, this.adPlaybackState.adResumePositionUs, this.adPlaybackState.contentDurationUs), this.contentManifest);
        }
    }
}
