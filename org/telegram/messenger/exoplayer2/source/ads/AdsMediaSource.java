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
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.source.CompositeMediaSource;
import org.telegram.messenger.exoplayer2.source.DeferredMediaPeriod;
import org.telegram.messenger.exoplayer2.source.ExtractorMediaSource.Factory;
import org.telegram.messenger.exoplayer2.source.MediaPeriod;
import org.telegram.messenger.exoplayer2.source.MediaSource;
import org.telegram.messenger.exoplayer2.source.MediaSource.Listener;
import org.telegram.messenger.exoplayer2.source.MediaSource.MediaPeriodId;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class AdsMediaSource
  extends CompositeMediaSource<MediaSource.MediaPeriodId>
{
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
  private MediaSource.Listener listener;
  private final Handler mainHandler;
  private final Timeline.Period period;
  
  public AdsMediaSource(MediaSource paramMediaSource, MediaSourceFactory paramMediaSourceFactory, AdsLoader paramAdsLoader, ViewGroup paramViewGroup, Handler paramHandler, EventListener paramEventListener)
  {
    this.contentMediaSource = paramMediaSource;
    this.adMediaSourceFactory = paramMediaSourceFactory;
    this.adsLoader = paramAdsLoader;
    this.adUiViewGroup = paramViewGroup;
    this.eventHandler = paramHandler;
    this.eventListener = paramEventListener;
    this.mainHandler = new Handler(Looper.getMainLooper());
    this.deferredMediaPeriodByAdMediaSource = new HashMap();
    this.period = new Timeline.Period();
    this.adGroupMediaSources = new MediaSource[0][];
    this.adDurationsUs = new long[0][];
    paramAdsLoader.setSupportedContentTypes(paramMediaSourceFactory.getSupportedTypes());
  }
  
  public AdsMediaSource(MediaSource paramMediaSource, DataSource.Factory paramFactory, AdsLoader paramAdsLoader, ViewGroup paramViewGroup)
  {
    this(paramMediaSource, paramFactory, paramAdsLoader, paramViewGroup, null, null);
  }
  
  public AdsMediaSource(MediaSource paramMediaSource, DataSource.Factory paramFactory, AdsLoader paramAdsLoader, ViewGroup paramViewGroup, Handler paramHandler, EventListener paramEventListener)
  {
    this(paramMediaSource, new ExtractorMediaSource.Factory(paramFactory), paramAdsLoader, paramViewGroup, paramHandler, paramEventListener);
  }
  
  private void maybeUpdateSourceInfo()
  {
    if ((this.adPlaybackState != null) && (this.contentTimeline != null))
    {
      this.adPlaybackState = this.adPlaybackState.withAdDurationsUs(this.adDurationsUs);
      if (this.adPlaybackState.adGroupCount != 0) {
        break label60;
      }
    }
    label60:
    for (Object localObject = this.contentTimeline;; localObject = new SinglePeriodAdTimeline(this.contentTimeline, this.adPlaybackState))
    {
      this.listener.onSourceInfoRefreshed(this, (Timeline)localObject, this.contentManifest);
      return;
    }
  }
  
  private void onAdPlaybackState(AdPlaybackState paramAdPlaybackState)
  {
    if (this.adPlaybackState == null)
    {
      this.adGroupMediaSources = new MediaSource[paramAdPlaybackState.adGroupCount][];
      Arrays.fill(this.adGroupMediaSources, new MediaSource[0]);
      this.adDurationsUs = new long[paramAdPlaybackState.adGroupCount][];
      Arrays.fill(this.adDurationsUs, new long[0]);
    }
    this.adPlaybackState = paramAdPlaybackState;
    maybeUpdateSourceInfo();
  }
  
  private void onAdSourceInfoRefreshed(MediaSource paramMediaSource, int paramInt1, int paramInt2, Timeline paramTimeline)
  {
    boolean bool = true;
    if (paramTimeline.getPeriodCount() == 1) {}
    for (;;)
    {
      Assertions.checkArgument(bool);
      this.adDurationsUs[paramInt1][paramInt2] = paramTimeline.getPeriod(0, this.period).getDurationUs();
      if (!this.deferredMediaPeriodByAdMediaSource.containsKey(paramMediaSource)) {
        break label116;
      }
      paramTimeline = (List)this.deferredMediaPeriodByAdMediaSource.get(paramMediaSource);
      for (paramInt1 = 0; paramInt1 < paramTimeline.size(); paramInt1++) {
        ((DeferredMediaPeriod)paramTimeline.get(paramInt1)).createPeriod();
      }
      bool = false;
    }
    this.deferredMediaPeriodByAdMediaSource.remove(paramMediaSource);
    label116:
    maybeUpdateSourceInfo();
  }
  
  private void onContentSourceInfoRefreshed(Timeline paramTimeline, Object paramObject)
  {
    this.contentTimeline = paramTimeline;
    this.contentManifest = paramObject;
    maybeUpdateSourceInfo();
  }
  
  public MediaPeriod createPeriod(MediaSource.MediaPeriodId paramMediaPeriodId, Allocator paramAllocator)
  {
    if ((this.adPlaybackState.adGroupCount > 0) && (paramMediaPeriodId.isAd()))
    {
      int i = paramMediaPeriodId.adGroupIndex;
      int j = paramMediaPeriodId.adIndexInAdGroup;
      if (this.adGroupMediaSources[i].length <= j)
      {
        localObject = this.adPlaybackState.adGroups[paramMediaPeriodId.adGroupIndex].uris[paramMediaPeriodId.adIndexInAdGroup];
        localObject = this.adMediaSourceFactory.createMediaSource((Uri)localObject, this.eventHandler, this.eventListener);
        int k = this.adGroupMediaSources[paramMediaPeriodId.adGroupIndex].length;
        if (j >= k)
        {
          int m = j + 1;
          this.adGroupMediaSources[i] = ((MediaSource[])Arrays.copyOf(this.adGroupMediaSources[i], m));
          this.adDurationsUs[i] = Arrays.copyOf(this.adDurationsUs[i], m);
          Arrays.fill(this.adDurationsUs[i], k, m, -9223372036854775807L);
        }
        this.adGroupMediaSources[i][j] = localObject;
        this.deferredMediaPeriodByAdMediaSource.put(localObject, new ArrayList());
        prepareChildSource(paramMediaPeriodId, (MediaSource)localObject);
      }
      Object localObject = this.adGroupMediaSources[i][j];
      paramMediaPeriodId = new DeferredMediaPeriod((MediaSource)localObject, new MediaSource.MediaPeriodId(0), paramAllocator);
      paramAllocator = (List)this.deferredMediaPeriodByAdMediaSource.get(localObject);
      if (paramAllocator == null) {
        paramMediaPeriodId.createPeriod();
      }
    }
    for (;;)
    {
      return paramMediaPeriodId;
      paramAllocator.add(paramMediaPeriodId);
      continue;
      paramMediaPeriodId = new DeferredMediaPeriod(this.contentMediaSource, paramMediaPeriodId, paramAllocator);
      paramMediaPeriodId.createPeriod();
    }
  }
  
  protected void onChildSourceInfoRefreshed(MediaSource.MediaPeriodId paramMediaPeriodId, MediaSource paramMediaSource, Timeline paramTimeline, Object paramObject)
  {
    if (paramMediaPeriodId.isAd()) {
      onAdSourceInfoRefreshed(paramMediaSource, paramMediaPeriodId.adGroupIndex, paramMediaPeriodId.adIndexInAdGroup, paramTimeline);
    }
    for (;;)
    {
      return;
      onContentSourceInfoRefreshed(paramTimeline, paramObject);
    }
  }
  
  public void prepareSource(final ExoPlayer paramExoPlayer, boolean paramBoolean, MediaSource.Listener paramListener)
  {
    super.prepareSource(paramExoPlayer, paramBoolean, paramListener);
    Assertions.checkArgument(paramBoolean);
    final ComponentListener localComponentListener = new ComponentListener();
    this.listener = paramListener;
    this.componentListener = localComponentListener;
    prepareChildSource(new MediaSource.MediaPeriodId(0), this.contentMediaSource);
    this.mainHandler.post(new Runnable()
    {
      public void run()
      {
        AdsMediaSource.this.adsLoader.attachPlayer(paramExoPlayer, localComponentListener, AdsMediaSource.this.adUiViewGroup);
      }
    });
  }
  
  public void releasePeriod(MediaPeriod paramMediaPeriod)
  {
    DeferredMediaPeriod localDeferredMediaPeriod = (DeferredMediaPeriod)paramMediaPeriod;
    paramMediaPeriod = (List)this.deferredMediaPeriodByAdMediaSource.get(localDeferredMediaPeriod.mediaSource);
    if (paramMediaPeriod != null) {
      paramMediaPeriod.remove(localDeferredMediaPeriod);
    }
    localDeferredMediaPeriod.releasePeriod();
  }
  
  public void releaseSource()
  {
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
    this.mainHandler.post(new Runnable()
    {
      public void run()
      {
        AdsMediaSource.this.adsLoader.detachPlayer();
      }
    });
  }
  
  private final class ComponentListener
    implements AdsLoader.EventListener
  {
    private final Handler playerHandler = new Handler();
    private volatile boolean released;
    
    public ComponentListener() {}
    
    public void onAdClicked()
    {
      if (this.released) {}
      for (;;)
      {
        return;
        if ((AdsMediaSource.this.eventHandler != null) && (AdsMediaSource.this.eventListener != null)) {
          AdsMediaSource.this.eventHandler.post(new Runnable()
          {
            public void run()
            {
              if (!AdsMediaSource.ComponentListener.this.released) {
                AdsMediaSource.this.eventListener.onAdClicked();
              }
            }
          });
        }
      }
    }
    
    public void onAdPlaybackState(final AdPlaybackState paramAdPlaybackState)
    {
      if (this.released) {}
      for (;;)
      {
        return;
        this.playerHandler.post(new Runnable()
        {
          public void run()
          {
            if (AdsMediaSource.ComponentListener.this.released) {}
            for (;;)
            {
              return;
              AdsMediaSource.this.onAdPlaybackState(paramAdPlaybackState);
            }
          }
        });
      }
    }
    
    public void onAdTapped()
    {
      if (this.released) {}
      for (;;)
      {
        return;
        if ((AdsMediaSource.this.eventHandler != null) && (AdsMediaSource.this.eventListener != null)) {
          AdsMediaSource.this.eventHandler.post(new Runnable()
          {
            public void run()
            {
              if (!AdsMediaSource.ComponentListener.this.released) {
                AdsMediaSource.this.eventListener.onAdTapped();
              }
            }
          });
        }
      }
    }
    
    public void onLoadError(final IOException paramIOException)
    {
      if (this.released) {}
      for (;;)
      {
        return;
        Log.w("AdsMediaSource", "Ad load error", paramIOException);
        if ((AdsMediaSource.this.eventHandler != null) && (AdsMediaSource.this.eventListener != null)) {
          AdsMediaSource.this.eventHandler.post(new Runnable()
          {
            public void run()
            {
              if (!AdsMediaSource.ComponentListener.this.released) {
                AdsMediaSource.this.eventListener.onAdLoadError(paramIOException);
              }
            }
          });
        }
      }
    }
    
    public void release()
    {
      this.released = true;
      this.playerHandler.removeCallbacksAndMessages(null);
    }
  }
  
  public static abstract interface EventListener
    extends MediaSourceEventListener
  {
    public abstract void onAdClicked();
    
    public abstract void onAdLoadError(IOException paramIOException);
    
    public abstract void onAdTapped();
  }
  
  public static abstract interface MediaSourceFactory
  {
    public abstract MediaSource createMediaSource(Uri paramUri, Handler paramHandler, MediaSourceEventListener paramMediaSourceEventListener);
    
    public abstract int[] getSupportedTypes();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/ads/AdsMediaSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */