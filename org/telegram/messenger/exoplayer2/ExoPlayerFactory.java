package org.telegram.messenger.exoplayer2;

import android.content.Context;
import org.telegram.messenger.exoplayer2.drm.DrmSessionManager;
import org.telegram.messenger.exoplayer2.drm.FrameworkMediaCrypto;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelector;
import org.telegram.messenger.exoplayer2.util.Clock;

public final class ExoPlayerFactory
{
  public static ExoPlayer newInstance(Renderer[] paramArrayOfRenderer, TrackSelector paramTrackSelector)
  {
    return newInstance(paramArrayOfRenderer, paramTrackSelector, new DefaultLoadControl());
  }
  
  public static ExoPlayer newInstance(Renderer[] paramArrayOfRenderer, TrackSelector paramTrackSelector, LoadControl paramLoadControl)
  {
    return new ExoPlayerImpl(paramArrayOfRenderer, paramTrackSelector, paramLoadControl, Clock.DEFAULT);
  }
  
  public static SimpleExoPlayer newSimpleInstance(Context paramContext, TrackSelector paramTrackSelector)
  {
    return newSimpleInstance(new DefaultRenderersFactory(paramContext), paramTrackSelector);
  }
  
  @Deprecated
  public static SimpleExoPlayer newSimpleInstance(Context paramContext, TrackSelector paramTrackSelector, LoadControl paramLoadControl)
  {
    return newSimpleInstance(new DefaultRenderersFactory(paramContext), paramTrackSelector, paramLoadControl);
  }
  
  @Deprecated
  public static SimpleExoPlayer newSimpleInstance(Context paramContext, TrackSelector paramTrackSelector, LoadControl paramLoadControl, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager)
  {
    return newSimpleInstance(new DefaultRenderersFactory(paramContext, paramDrmSessionManager), paramTrackSelector, paramLoadControl);
  }
  
  @Deprecated
  public static SimpleExoPlayer newSimpleInstance(Context paramContext, TrackSelector paramTrackSelector, LoadControl paramLoadControl, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, int paramInt)
  {
    return newSimpleInstance(new DefaultRenderersFactory(paramContext, paramDrmSessionManager, paramInt), paramTrackSelector, paramLoadControl);
  }
  
  @Deprecated
  public static SimpleExoPlayer newSimpleInstance(Context paramContext, TrackSelector paramTrackSelector, LoadControl paramLoadControl, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, int paramInt, long paramLong)
  {
    return newSimpleInstance(new DefaultRenderersFactory(paramContext, paramDrmSessionManager, paramInt, paramLong), paramTrackSelector, paramLoadControl);
  }
  
  public static SimpleExoPlayer newSimpleInstance(RenderersFactory paramRenderersFactory, TrackSelector paramTrackSelector)
  {
    return newSimpleInstance(paramRenderersFactory, paramTrackSelector, new DefaultLoadControl());
  }
  
  public static SimpleExoPlayer newSimpleInstance(RenderersFactory paramRenderersFactory, TrackSelector paramTrackSelector, LoadControl paramLoadControl)
  {
    return new SimpleExoPlayer(paramRenderersFactory, paramTrackSelector, paramLoadControl);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/ExoPlayerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */