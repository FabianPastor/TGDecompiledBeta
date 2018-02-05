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
import org.telegram.messenger.exoplayer2.source.CompositeMediaSource;
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

public final class AdsMediaSource extends CompositeMediaSource<MediaPeriodId> {
    private static final String TAG = "AdsMediaSource";
    private long[][] adDurationsUs;
    private MediaSource[][] adGroupMediaSources;
    private final MediaSourceFactory adMediaSourceFactory;
    private AdPlaybackState adPlaybackState;
    private final ViewGroup adUiViewGroup;
    private final AdsLoader adsLoader;
    private ComponentListener componentListener;
    private Object contentManifest;
    private final MediaSource contentMediaSource;
    private Timeline contentTimeline;
    private final Map<MediaSource, List<DeferredMediaPeriod>> deferredMediaPeriodByAdMediaSource;
    private final Handler eventHandler;
    private final EventListener eventListener;
    private Listener listener;
    private final Handler mainHandler;
    private final Period period;

    public interface MediaSourceFactory {
        MediaSource createMediaSource(Uri uri, Handler handler, MediaSourceEventListener mediaSourceEventListener);

        int[] getSupportedTypes();
    }

    private final class ComponentListener implements org.telegram.messenger.exoplayer2.source.ads.AdsLoader.EventListener {
        private final Handler playerHandler = new Handler();
        private volatile boolean released;

        public void release() {
            this.released = true;
            this.playerHandler.removeCallbacksAndMessages(null);
        }

        public void onAdPlaybackState(final AdPlaybackState adPlaybackState) {
            if (!this.released) {
                this.playerHandler.post(new Runnable() {
                    public void run() {
                        if (!ComponentListener.this.released) {
                            AdsMediaSource.this.onAdPlaybackState(adPlaybackState);
                        }
                    }
                });
            }
        }

        public void onAdClicked() {
            if (!this.released && AdsMediaSource.this.eventHandler != null && AdsMediaSource.this.eventListener != null) {
                AdsMediaSource.this.eventHandler.post(new Runnable() {
                    public void run() {
                        if (!ComponentListener.this.released) {
                            AdsMediaSource.this.eventListener.onAdClicked();
                        }
                    }
                });
            }
        }

        public void onAdTapped() {
            if (!this.released && AdsMediaSource.this.eventHandler != null && AdsMediaSource.this.eventListener != null) {
                AdsMediaSource.this.eventHandler.post(new Runnable() {
                    public void run() {
                        if (!ComponentListener.this.released) {
                            AdsMediaSource.this.eventListener.onAdTapped();
                        }
                    }
                });
            }
        }

        public void onLoadError(final IOException error) {
            if (!this.released) {
                Log.w(AdsMediaSource.TAG, "Ad load error", error);
                if (AdsMediaSource.this.eventHandler != null && AdsMediaSource.this.eventListener != null) {
                    AdsMediaSource.this.eventHandler.post(new Runnable() {
                        public void run() {
                            if (!ComponentListener.this.released) {
                                AdsMediaSource.this.eventListener.onAdLoadError(error);
                            }
                        }
                    });
                }
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
        this.deferredMediaPeriodByAdMediaSource = new HashMap();
        this.period = new Period();
        this.adGroupMediaSources = new MediaSource[0][];
        this.adDurationsUs = new long[0][];
        adsLoader.setSupportedContentTypes(adMediaSourceFactory.getSupportedTypes());
    }

    public void prepareSource(final ExoPlayer player, boolean isTopLevelSource, Listener listener) {
        super.prepareSource(player, isTopLevelSource, listener);
        Assertions.checkArgument(isTopLevelSource);
        final ComponentListener componentListener = new ComponentListener();
        this.listener = listener;
        this.componentListener = componentListener;
        prepareChildSource(new MediaPeriodId(0), this.contentMediaSource);
        this.mainHandler.post(new Runnable() {
            public void run() {
                AdsMediaSource.this.adsLoader.attachPlayer(player, componentListener, AdsMediaSource.this.adUiViewGroup);
            }
        });
    }

    public MediaPeriod createPeriod(MediaPeriodId id, Allocator allocator) {
        if (this.adPlaybackState.adGroupCount <= 0 || !id.isAd()) {
            DeferredMediaPeriod mediaPeriod = new DeferredMediaPeriod(this.contentMediaSource, id, allocator);
            mediaPeriod.createPeriod();
            return mediaPeriod;
        }
        int adGroupIndex = id.adGroupIndex;
        int adIndexInAdGroup = id.adIndexInAdGroup;
        if (this.adGroupMediaSources[adGroupIndex].length <= adIndexInAdGroup) {
            MediaSource adMediaSource = this.adMediaSourceFactory.createMediaSource(this.adPlaybackState.adGroups[id.adGroupIndex].uris[id.adIndexInAdGroup], this.eventHandler, this.eventListener);
            int oldAdCount = this.adGroupMediaSources[id.adGroupIndex].length;
            if (adIndexInAdGroup >= oldAdCount) {
                int adCount = adIndexInAdGroup + 1;
                this.adGroupMediaSources[adGroupIndex] = (MediaSource[]) Arrays.copyOf(this.adGroupMediaSources[adGroupIndex], adCount);
                this.adDurationsUs[adGroupIndex] = Arrays.copyOf(this.adDurationsUs[adGroupIndex], adCount);
                Arrays.fill(this.adDurationsUs[adGroupIndex], oldAdCount, adCount, C.TIME_UNSET);
            }
            this.adGroupMediaSources[adGroupIndex][adIndexInAdGroup] = adMediaSource;
            this.deferredMediaPeriodByAdMediaSource.put(adMediaSource, new ArrayList());
            prepareChildSource(id, adMediaSource);
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
        DeferredMediaPeriod deferredMediaPeriod = (DeferredMediaPeriod) mediaPeriod;
        List<DeferredMediaPeriod> mediaPeriods = (List) this.deferredMediaPeriodByAdMediaSource.get(deferredMediaPeriod.mediaSource);
        if (mediaPeriods != null) {
            mediaPeriods.remove(deferredMediaPeriod);
        }
        deferredMediaPeriod.releasePeriod();
    }

    public void releaseSource() {
        super.releaseSource();
        this.componentListener.release();
        this.componentListener = null;
        this.deferredMediaPeriodByAdMediaSource.clear();
        this.contentTimeline = null;
        this.contentManifest = null;
        this.adPlaybackState = null;
        this.adGroupMediaSources = new MediaSource[0][];
        this.adDurationsUs = new long[0][];
        this.listener = null;
        this.mainHandler.post(new Runnable() {
            public void run() {
                AdsMediaSource.this.adsLoader.detachPlayer();
            }
        });
    }

    protected void onChildSourceInfoRefreshed(MediaPeriodId mediaPeriodId, MediaSource mediaSource, Timeline timeline, Object manifest) {
        if (mediaPeriodId.isAd()) {
            onAdSourceInfoRefreshed(mediaSource, mediaPeriodId.adGroupIndex, mediaPeriodId.adIndexInAdGroup, timeline);
        } else {
            onContentSourceInfoRefreshed(timeline, manifest);
        }
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
            this.adPlaybackState = this.adPlaybackState.withAdDurationsUs(this.adDurationsUs);
            this.listener.onSourceInfoRefreshed(this, this.adPlaybackState.adGroupCount == 0 ? this.contentTimeline : new SinglePeriodAdTimeline(this.contentTimeline, this.adPlaybackState), this.contentManifest);
        }
    }
}
