package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaCodec.CryptoException;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.Surface;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import org.telegram.messenger.exoplayer.DummyTrackRenderer;
import org.telegram.messenger.exoplayer.ExoPlaybackException;
import org.telegram.messenger.exoplayer.ExoPlayer;
import org.telegram.messenger.exoplayer.ExoPlayer.Factory;
import org.telegram.messenger.exoplayer.ExoPlayer.Listener;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecSelector;
import org.telegram.messenger.exoplayer.MediaCodecTrackRenderer.DecoderInitializationException;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer.EventListener;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.SampleSource;
import org.telegram.messenger.exoplayer.TrackRenderer;
import org.telegram.messenger.exoplayer.audio.AudioCapabilities;
import org.telegram.messenger.exoplayer.extractor.Extractor;
import org.telegram.messenger.exoplayer.extractor.ExtractorSampleSource;
import org.telegram.messenger.exoplayer.upstream.Allocator;
import org.telegram.messenger.exoplayer.upstream.DefaultAllocator;
import org.telegram.messenger.exoplayer.upstream.DefaultUriDataSource;
import org.telegram.messenger.exoplayer.util.PlayerControl;

@SuppressLint({"NewApi"})
public class VideoPlayer
  implements ExoPlayer.Listener, MediaCodecVideoTrackRenderer.EventListener
{
  private static final int RENDERER_BUILDING_STATE_BUILDING = 2;
  private static final int RENDERER_BUILDING_STATE_BUILT = 3;
  private static final int RENDERER_BUILDING_STATE_IDLE = 1;
  public static final int RENDERER_COUNT = 2;
  public static final int STATE_BUFFERING = 3;
  public static final int STATE_ENDED = 5;
  public static final int STATE_IDLE = 1;
  public static final int STATE_PREPARING = 2;
  public static final int STATE_READY = 4;
  public static final int TRACK_DEFAULT = 0;
  public static final int TRACK_DISABLED = -1;
  public static final int TYPE_AUDIO = 1;
  public static final int TYPE_VIDEO = 0;
  private TrackRenderer audioRenderer;
  private boolean backgrounded;
  private boolean lastReportedPlayWhenReady;
  private int lastReportedPlaybackState;
  private final CopyOnWriteArrayList<Listener> listeners;
  private final Handler mainHandler;
  private final ExoPlayer player;
  private final PlayerControl playerControl;
  private final RendererBuilder rendererBuilder;
  private int rendererBuildingState;
  private Surface surface;
  private TrackRenderer videoRenderer;
  private int videoTrackToRestore;
  
  public VideoPlayer(RendererBuilder paramRendererBuilder)
  {
    this.rendererBuilder = paramRendererBuilder;
    this.player = ExoPlayer.Factory.newInstance(2, 1000, 5000);
    this.player.addListener(this);
    this.playerControl = new PlayerControl(this.player);
    this.mainHandler = new Handler();
    this.listeners = new CopyOnWriteArrayList();
    this.lastReportedPlaybackState = 1;
    this.rendererBuildingState = 1;
  }
  
  private void maybeReportPlayerState()
  {
    boolean bool = this.player.getPlayWhenReady();
    int i = getPlaybackState();
    if ((this.lastReportedPlayWhenReady != bool) || (this.lastReportedPlaybackState != i))
    {
      Iterator localIterator = this.listeners.iterator();
      while (localIterator.hasNext()) {
        ((Listener)localIterator.next()).onStateChanged(bool, i);
      }
      this.lastReportedPlayWhenReady = bool;
      this.lastReportedPlaybackState = i;
    }
  }
  
  private void pushSurface(boolean paramBoolean)
  {
    if (this.videoRenderer == null) {
      return;
    }
    if (paramBoolean)
    {
      this.player.blockingSendMessage(this.videoRenderer, 1, this.surface);
      return;
    }
    this.player.sendMessage(this.videoRenderer, 1, this.surface);
  }
  
  public void addListener(Listener paramListener)
  {
    this.listeners.add(paramListener);
  }
  
  public void blockingClearSurface()
  {
    this.surface = null;
    pushSurface(true);
  }
  
  public boolean getBackgrounded()
  {
    return this.backgrounded;
  }
  
  public int getBufferedPercentage()
  {
    return this.player.getBufferedPercentage();
  }
  
  public long getCurrentPosition()
  {
    return this.player.getCurrentPosition();
  }
  
  public long getDuration()
  {
    return this.player.getDuration();
  }
  
  Handler getMainHandler()
  {
    return this.mainHandler;
  }
  
  public boolean getPlayWhenReady()
  {
    return this.player.getPlayWhenReady();
  }
  
  Looper getPlaybackLooper()
  {
    return this.player.getPlaybackLooper();
  }
  
  public int getPlaybackState()
  {
    int i;
    if (this.rendererBuildingState == 2) {
      i = 2;
    }
    int j;
    do
    {
      do
      {
        return i;
        j = this.player.getPlaybackState();
        i = j;
      } while (this.rendererBuildingState != 3);
      i = j;
    } while (j != 1);
    return 2;
  }
  
  public PlayerControl getPlayerControl()
  {
    return this.playerControl;
  }
  
  public int getSelectedTrack(int paramInt)
  {
    return this.player.getSelectedTrack(paramInt);
  }
  
  public Surface getSurface()
  {
    return this.surface;
  }
  
  public int getTrackCount(int paramInt)
  {
    return this.player.getTrackCount(paramInt);
  }
  
  public MediaFormat getTrackFormat(int paramInt1, int paramInt2)
  {
    return this.player.getTrackFormat(paramInt1, paramInt2);
  }
  
  public void onCryptoError(MediaCodec.CryptoException paramCryptoException)
  {
    Iterator localIterator = this.listeners.iterator();
    while (localIterator.hasNext()) {
      ((Listener)localIterator.next()).onError(paramCryptoException);
    }
  }
  
  public void onDecoderInitializationError(MediaCodecTrackRenderer.DecoderInitializationException paramDecoderInitializationException)
  {
    Iterator localIterator = this.listeners.iterator();
    while (localIterator.hasNext()) {
      ((Listener)localIterator.next()).onError(paramDecoderInitializationException);
    }
  }
  
  public void onDecoderInitialized(String paramString, long paramLong1, long paramLong2) {}
  
  public void onDrawnToSurface(Surface paramSurface) {}
  
  public void onDroppedFrames(int paramInt, long paramLong) {}
  
  public void onPlayWhenReadyCommitted() {}
  
  public void onPlayerError(ExoPlaybackException paramExoPlaybackException)
  {
    this.rendererBuildingState = 1;
    Iterator localIterator = this.listeners.iterator();
    while (localIterator.hasNext()) {
      ((Listener)localIterator.next()).onError(paramExoPlaybackException);
    }
  }
  
  public void onPlayerStateChanged(boolean paramBoolean, int paramInt)
  {
    maybeReportPlayerState();
  }
  
  void onRenderers(TrackRenderer[] paramArrayOfTrackRenderer)
  {
    int i = 0;
    while (i < 2)
    {
      if (paramArrayOfTrackRenderer[i] == null) {
        paramArrayOfTrackRenderer[i] = new DummyTrackRenderer();
      }
      i += 1;
    }
    this.videoRenderer = paramArrayOfTrackRenderer[0];
    this.audioRenderer = paramArrayOfTrackRenderer[1];
    pushSurface(false);
    this.player.prepare(paramArrayOfTrackRenderer);
    this.rendererBuildingState = 3;
  }
  
  void onRenderersError(Exception paramException)
  {
    Iterator localIterator = this.listeners.iterator();
    while (localIterator.hasNext()) {
      ((Listener)localIterator.next()).onError(paramException);
    }
    this.rendererBuildingState = 1;
    maybeReportPlayerState();
  }
  
  public void onVideoSizeChanged(int paramInt1, int paramInt2, int paramInt3, float paramFloat)
  {
    Iterator localIterator = this.listeners.iterator();
    while (localIterator.hasNext()) {
      ((Listener)localIterator.next()).onVideoSizeChanged(paramInt1, paramInt2, paramInt3, paramFloat);
    }
  }
  
  public void prepare()
  {
    if (this.rendererBuildingState == 3) {
      this.player.stop();
    }
    this.rendererBuilder.cancel();
    this.videoRenderer = null;
    this.audioRenderer = null;
    this.rendererBuildingState = 2;
    maybeReportPlayerState();
    this.rendererBuilder.buildRenderers(this);
  }
  
  public void release()
  {
    this.rendererBuilder.cancel();
    this.rendererBuildingState = 1;
    this.surface = null;
    this.player.release();
  }
  
  public void removeListener(Listener paramListener)
  {
    this.listeners.remove(paramListener);
  }
  
  public void seekTo(long paramLong)
  {
    this.player.seekTo(paramLong);
  }
  
  public void setBackgrounded(boolean paramBoolean)
  {
    if (this.backgrounded == paramBoolean) {
      return;
    }
    this.backgrounded = paramBoolean;
    if (paramBoolean)
    {
      this.videoTrackToRestore = getSelectedTrack(0);
      setSelectedTrack(0, -1);
      blockingClearSurface();
      return;
    }
    setSelectedTrack(0, this.videoTrackToRestore);
  }
  
  public void setMute(boolean paramBoolean)
  {
    if (this.audioRenderer == null) {
      return;
    }
    if (paramBoolean)
    {
      this.player.sendMessage(this.audioRenderer, 1, Float.valueOf(0.0F));
      return;
    }
    this.player.sendMessage(this.audioRenderer, 1, Float.valueOf(1.0F));
  }
  
  public void setPlayWhenReady(boolean paramBoolean)
  {
    this.player.setPlayWhenReady(paramBoolean);
  }
  
  public void setSelectedTrack(int paramInt1, int paramInt2)
  {
    this.player.setSelectedTrack(paramInt1, paramInt2);
  }
  
  public void setSurface(Surface paramSurface)
  {
    this.surface = paramSurface;
    pushSurface(false);
  }
  
  public static class ExtractorRendererBuilder
    implements VideoPlayer.RendererBuilder
  {
    private static final int BUFFER_SEGMENT_COUNT = 256;
    private static final int BUFFER_SEGMENT_SIZE = 262144;
    private final Context context;
    private final Uri uri;
    private final String userAgent;
    
    public ExtractorRendererBuilder(Context paramContext, String paramString, Uri paramUri)
    {
      this.context = paramContext;
      this.userAgent = paramString;
      this.uri = paramUri;
    }
    
    public void buildRenderers(VideoPlayer paramVideoPlayer)
    {
      Object localObject = new DefaultAllocator(262144);
      Handler localHandler = paramVideoPlayer.getMainHandler();
      DefaultUriDataSource localDefaultUriDataSource = new DefaultUriDataSource(this.context, this.userAgent);
      localObject = new ExtractorSampleSource(this.uri, localDefaultUriDataSource, (Allocator)localObject, 67108864, localHandler, null, 0, new Extractor[0]);
      paramVideoPlayer.onRenderers(new TrackRenderer[] { new MediaCodecVideoTrackRenderer(this.context, (SampleSource)localObject, MediaCodecSelector.DEFAULT, 1, 5000L, localHandler, paramVideoPlayer, 50)new MediaCodecAudioTrackRenderer
      {
        protected void doSomeWork(long paramAnonymousLong1, long paramAnonymousLong2, boolean paramAnonymousBoolean)
          throws ExoPlaybackException
        {
          super.doSomeWork(paramAnonymousLong1, paramAnonymousLong2, paramAnonymousBoolean);
        }
      }, new MediaCodecAudioTrackRenderer((SampleSource)localObject, MediaCodecSelector.DEFAULT, null, true, localHandler, null, AudioCapabilities.getCapabilities(this.context), 3) });
    }
    
    public void cancel() {}
  }
  
  public static abstract interface Listener
  {
    public abstract void onError(Exception paramException);
    
    public abstract void onStateChanged(boolean paramBoolean, int paramInt);
    
    public abstract void onVideoSizeChanged(int paramInt1, int paramInt2, int paramInt3, float paramFloat);
  }
  
  public static abstract interface RendererBuilder
  {
    public abstract void buildRenderers(VideoPlayer paramVideoPlayer);
    
    public abstract void cancel();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/VideoPlayer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */