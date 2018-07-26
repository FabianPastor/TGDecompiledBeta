package org.telegram.messenger.exoplayer2.source.ads;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.exoplayer2.C0559C;
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.source.CompositeMediaSource;
import org.telegram.messenger.exoplayer2.source.DeferredMediaPeriod;
import org.telegram.messenger.exoplayer2.source.DeferredMediaPeriod.PrepareErrorListener;
import org.telegram.messenger.exoplayer2.source.ExtractorMediaSource;
import org.telegram.messenger.exoplayer2.source.MediaPeriod;
import org.telegram.messenger.exoplayer2.source.MediaSource;
import org.telegram.messenger.exoplayer2.source.MediaSource.MediaPeriodId;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
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
    private final Handler mainHandler;
    private final Period period;

    /* renamed from: org.telegram.messenger.exoplayer2.source.ads.AdsMediaSource$2 */
    class C06402 implements Runnable {
        C06402() {
        }

        public void run() {
            AdsMediaSource.this.adsLoader.detachPlayer();
        }
    }

    public static final class AdLoadException extends IOException {
        public static final int TYPE_AD = 0;
        public static final int TYPE_AD_GROUP = 1;
        public static final int TYPE_ALL_ADS = 2;
        public static final int TYPE_UNEXPECTED = 3;
        public final int type;

        @Retention(RetentionPolicy.SOURCE)
        public @interface Type {
        }

        public static AdLoadException createForAd(Exception error) {
            return new AdLoadException(0, error);
        }

        public static AdLoadException createForAdGroup(Exception error, int adGroupIndex) {
            return new AdLoadException(1, new IOException("Failed to load ad group " + adGroupIndex, error));
        }

        public static AdLoadException createForAllAds(Exception error) {
            return new AdLoadException(2, error);
        }

        public static AdLoadException createForUnexpected(RuntimeException error) {
            return new AdLoadException(3, error);
        }

        private AdLoadException(int type, Exception cause) {
            super(cause);
            this.type = type;
        }

        public RuntimeException getRuntimeExceptionForUnexpected() {
            Assertions.checkState(this.type == 3);
            return (RuntimeException) getCause();
        }
    }

    @Deprecated
    public interface EventListener {
        void onAdClicked();

        void onAdLoadError(IOException iOException);

        void onAdTapped();

        void onInternalAdLoadError(RuntimeException runtimeException);
    }

    public interface MediaSourceFactory {
        MediaSource createMediaSource(Uri uri);

        int[] getSupportedTypes();
    }

    private final class AdPrepareErrorListener implements PrepareErrorListener {
        private final int adGroupIndex;
        private final int adIndexInAdGroup;
        private final Uri adUri;

        public AdPrepareErrorListener(Uri adUri, int adGroupIndex, int adIndexInAdGroup) {
            this.adUri = adUri;
            this.adGroupIndex = adGroupIndex;
            this.adIndexInAdGroup = adIndexInAdGroup;
        }

        public void onPrepareError(MediaPeriodId mediaPeriodId, final IOException exception) {
            AdsMediaSource.this.createEventDispatcher(mediaPeriodId).loadError(new DataSpec(this.adUri), 6, -1, 0, 0, AdLoadException.createForAd(exception), true);
            AdsMediaSource.this.mainHandler.post(new Runnable() {
                public void run() {
                    AdsMediaSource.this.adsLoader.handlePrepareError(AdPrepareErrorListener.this.adGroupIndex, AdPrepareErrorListener.this.adIndexInAdGroup, exception);
                }
            });
        }
    }

    private final class ComponentListener implements org.telegram.messenger.exoplayer2.source.ads.AdsLoader.EventListener {
        private final Handler playerHandler = new Handler();
        private volatile boolean released;

        /* renamed from: org.telegram.messenger.exoplayer2.source.ads.AdsMediaSource$ComponentListener$2 */
        class C06432 implements Runnable {
            C06432() {
            }

            public void run() {
                if (!ComponentListener.this.released) {
                    AdsMediaSource.this.eventListener.onAdClicked();
                }
            }
        }

        /* renamed from: org.telegram.messenger.exoplayer2.source.ads.AdsMediaSource$ComponentListener$3 */
        class C06443 implements Runnable {
            C06443() {
            }

            public void run() {
                if (!ComponentListener.this.released) {
                    AdsMediaSource.this.eventListener.onAdTapped();
                }
            }
        }

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
                AdsMediaSource.this.eventHandler.post(new C06432());
            }
        }

        public void onAdTapped() {
            if (!this.released && AdsMediaSource.this.eventHandler != null && AdsMediaSource.this.eventListener != null) {
                AdsMediaSource.this.eventHandler.post(new C06443());
            }
        }

        public void onAdLoadError(final AdLoadException error, DataSpec dataSpec) {
            if (!this.released) {
                AdsMediaSource.this.createEventDispatcher(null).loadError(dataSpec, 6, -1, 0, 0, error, true);
                if (AdsMediaSource.this.eventHandler != null && AdsMediaSource.this.eventListener != null) {
                    AdsMediaSource.this.eventHandler.post(new Runnable() {
                        public void run() {
                            if (!ComponentListener.this.released) {
                                if (error.type == 3) {
                                    AdsMediaSource.this.eventListener.onInternalAdLoadError(error.getRuntimeExceptionForUnexpected());
                                } else {
                                    AdsMediaSource.this.eventListener.onAdLoadError(error);
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    public AdsMediaSource(MediaSource contentMediaSource, Factory dataSourceFactory, AdsLoader adsLoader, ViewGroup adUiViewGroup) {
        this(contentMediaSource, new ExtractorMediaSource.Factory(dataSourceFactory), adsLoader, adUiViewGroup, null, null);
    }

    public AdsMediaSource(MediaSource contentMediaSource, MediaSourceFactory adMediaSourceFactory, AdsLoader adsLoader, ViewGroup adUiViewGroup) {
        this(contentMediaSource, adMediaSourceFactory, adsLoader, adUiViewGroup, null, null);
    }

    @Deprecated
    public AdsMediaSource(MediaSource contentMediaSource, Factory dataSourceFactory, AdsLoader adsLoader, ViewGroup adUiViewGroup, Handler eventHandler, EventListener eventListener) {
        this(contentMediaSource, new ExtractorMediaSource.Factory(dataSourceFactory), adsLoader, adUiViewGroup, eventHandler, eventListener);
    }

    @Deprecated
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

    public void prepareSourceInternal(final ExoPlayer player, boolean isTopLevelSource) {
        super.prepareSourceInternal(player, isTopLevelSource);
        Assertions.checkArgument(isTopLevelSource);
        final ComponentListener componentListener = new ComponentListener();
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
        Uri adUri = this.adPlaybackState.adGroups[adGroupIndex].uris[adIndexInAdGroup];
        if (this.adGroupMediaSources[adGroupIndex].length <= adIndexInAdGroup) {
            MediaSource adMediaSource = this.adMediaSourceFactory.createMediaSource(adUri);
            int oldAdCount = this.adGroupMediaSources[adGroupIndex].length;
            if (adIndexInAdGroup >= oldAdCount) {
                int adCount = adIndexInAdGroup + 1;
                this.adGroupMediaSources[adGroupIndex] = (MediaSource[]) Arrays.copyOf(this.adGroupMediaSources[adGroupIndex], adCount);
                this.adDurationsUs[adGroupIndex] = Arrays.copyOf(this.adDurationsUs[adGroupIndex], adCount);
                Arrays.fill(this.adDurationsUs[adGroupIndex], oldAdCount, adCount, C0559C.TIME_UNSET);
            }
            this.adGroupMediaSources[adGroupIndex][adIndexInAdGroup] = adMediaSource;
            this.deferredMediaPeriodByAdMediaSource.put(adMediaSource, new ArrayList());
            prepareChildSource(id, adMediaSource);
        }
        MediaSource mediaSource = this.adGroupMediaSources[adGroupIndex][adIndexInAdGroup];
        DeferredMediaPeriod deferredMediaPeriod = new DeferredMediaPeriod(mediaSource, new MediaPeriodId(0, id.windowSequenceNumber), allocator);
        deferredMediaPeriod.setPrepareErrorListener(new AdPrepareErrorListener(adUri, adGroupIndex, adIndexInAdGroup));
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

    public void releaseSourceInternal() {
        super.releaseSourceInternal();
        this.componentListener.release();
        this.componentListener = null;
        this.deferredMediaPeriodByAdMediaSource.clear();
        this.contentTimeline = null;
        this.contentManifest = null;
        this.adPlaybackState = null;
        this.adGroupMediaSources = new MediaSource[0][];
        this.adDurationsUs = new long[0][];
        this.mainHandler.post(new C06402());
    }

    protected void onChildSourceInfoRefreshed(MediaPeriodId mediaPeriodId, MediaSource mediaSource, Timeline timeline, Object manifest) {
        if (mediaPeriodId.isAd()) {
            onAdSourceInfoRefreshed(mediaSource, mediaPeriodId.adGroupIndex, mediaPeriodId.adIndexInAdGroup, timeline);
        } else {
            onContentSourceInfoRefreshed(timeline, manifest);
        }
    }

    protected MediaPeriodId getMediaPeriodIdForChildMediaPeriodId(MediaPeriodId childId, MediaPeriodId mediaPeriodId) {
        return childId.isAd() ? childId : mediaPeriodId;
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
            refreshSourceInfo(this.adPlaybackState.adGroupCount == 0 ? this.contentTimeline : new SinglePeriodAdTimeline(this.contentTimeline, this.adPlaybackState), this.contentManifest);
        }
    }
}
