package org.telegram.messenger.exoplayer2;

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.media.PlaybackParams;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import org.telegram.messenger.exoplayer2.audio.AudioAttributes;
import org.telegram.messenger.exoplayer2.audio.AudioAttributes.Builder;
import org.telegram.messenger.exoplayer2.audio.AudioRendererEventListener;
import org.telegram.messenger.exoplayer2.decoder.DecoderCounters;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.metadata.MetadataOutput;
import org.telegram.messenger.exoplayer2.source.MediaSource;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.text.Cue;
import org.telegram.messenger.exoplayer2.text.TextOutput;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelectionArray;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelector;
import org.telegram.messenger.exoplayer2.util.Clock;
import org.telegram.messenger.exoplayer2.util.Util;
import org.telegram.messenger.exoplayer2.video.VideoListener;
import org.telegram.messenger.exoplayer2.video.VideoRendererEventListener;

@TargetApi(16)
public class SimpleExoPlayer
  implements ExoPlayer, Player.TextComponent, Player.VideoComponent
{
  private static final String TAG = "SimpleExoPlayer";
  private AudioAttributes audioAttributes;
  private final CopyOnWriteArraySet<AudioRendererEventListener> audioDebugListeners = new CopyOnWriteArraySet();
  private DecoderCounters audioDecoderCounters;
  private Format audioFormat;
  private int audioSessionId;
  private float audioVolume;
  private final ComponentListener componentListener = new ComponentListener(null);
  private final CopyOnWriteArraySet<MetadataOutput> metadataOutputs = new CopyOnWriteArraySet();
  private boolean needSetSurface = true;
  private boolean ownsSurface;
  private final ExoPlayer player;
  protected final Renderer[] renderers;
  private Surface surface;
  private SurfaceHolder surfaceHolder;
  private final CopyOnWriteArraySet<TextOutput> textOutputs = new CopyOnWriteArraySet();
  private TextureView textureView;
  private final CopyOnWriteArraySet<VideoRendererEventListener> videoDebugListeners = new CopyOnWriteArraySet();
  private DecoderCounters videoDecoderCounters;
  private Format videoFormat;
  private final CopyOnWriteArraySet<VideoListener> videoListeners = new CopyOnWriteArraySet();
  private int videoScalingMode;
  
  protected SimpleExoPlayer(RenderersFactory paramRenderersFactory, TrackSelector paramTrackSelector, LoadControl paramLoadControl)
  {
    this(paramRenderersFactory, paramTrackSelector, paramLoadControl, Clock.DEFAULT);
  }
  
  protected SimpleExoPlayer(RenderersFactory paramRenderersFactory, TrackSelector paramTrackSelector, LoadControl paramLoadControl, Clock paramClock)
  {
    if (Looper.myLooper() != null) {}
    for (Looper localLooper = Looper.myLooper();; localLooper = Looper.getMainLooper())
    {
      this.renderers = paramRenderersFactory.createRenderers(new Handler(localLooper), this.componentListener, this.componentListener, this.componentListener, this.componentListener);
      this.audioVolume = 1.0F;
      this.audioSessionId = 0;
      this.audioAttributes = AudioAttributes.DEFAULT;
      this.videoScalingMode = 1;
      this.player = createExoPlayerImpl(this.renderers, paramTrackSelector, paramLoadControl, paramClock);
      return;
    }
  }
  
  private void removeSurfaceCallbacks()
  {
    if (this.textureView != null)
    {
      if (this.textureView.getSurfaceTextureListener() == this.componentListener) {
        break label60;
      }
      Log.w("SimpleExoPlayer", "SurfaceTextureListener already unset or replaced.");
    }
    for (;;)
    {
      this.textureView = null;
      if (this.surfaceHolder != null)
      {
        this.surfaceHolder.removeCallback(this.componentListener);
        this.surfaceHolder = null;
      }
      return;
      label60:
      this.textureView.setSurfaceTextureListener(null);
    }
  }
  
  private void setVideoSurfaceInternal(Surface paramSurface, boolean paramBoolean)
  {
    ArrayList localArrayList = new ArrayList();
    Object localObject;
    for (localObject : this.renderers) {
      if (((Renderer)localObject).getTrackType() == 2) {
        localArrayList.add(this.player.createMessage((PlayerMessage.Target)localObject).setType(1).setPayload(paramSurface).send());
      }
    }
    if ((this.surface != null) && (this.surface != paramSurface)) {
      try
      {
        localObject = localArrayList.iterator();
        while (((Iterator)localObject).hasNext()) {
          ((PlayerMessage)((Iterator)localObject).next()).blockUntilDelivered();
        }
        this.surface = paramSurface;
      }
      catch (InterruptedException localInterruptedException)
      {
        Thread.currentThread().interrupt();
        if (this.ownsSurface) {
          this.surface.release();
        }
      }
    }
    this.ownsSurface = paramBoolean;
  }
  
  public void addAudioDebugListener(AudioRendererEventListener paramAudioRendererEventListener)
  {
    this.audioDebugListeners.add(paramAudioRendererEventListener);
  }
  
  public void addListener(Player.EventListener paramEventListener)
  {
    this.player.addListener(paramEventListener);
  }
  
  public void addMetadataOutput(MetadataOutput paramMetadataOutput)
  {
    this.metadataOutputs.add(paramMetadataOutput);
  }
  
  public void addTextOutput(TextOutput paramTextOutput)
  {
    this.textOutputs.add(paramTextOutput);
  }
  
  public void addVideoDebugListener(VideoRendererEventListener paramVideoRendererEventListener)
  {
    this.videoDebugListeners.add(paramVideoRendererEventListener);
  }
  
  public void addVideoListener(VideoListener paramVideoListener)
  {
    this.videoListeners.add(paramVideoListener);
  }
  
  public void blockingSendMessages(ExoPlayer.ExoPlayerMessage... paramVarArgs)
  {
    this.player.blockingSendMessages(paramVarArgs);
  }
  
  @Deprecated
  public void clearMetadataOutput(MetadataOutput paramMetadataOutput)
  {
    removeMetadataOutput(paramMetadataOutput);
  }
  
  @Deprecated
  public void clearTextOutput(TextOutput paramTextOutput)
  {
    removeTextOutput(paramTextOutput);
  }
  
  @Deprecated
  public void clearVideoListener(VideoListener paramVideoListener)
  {
    removeVideoListener(paramVideoListener);
  }
  
  public void clearVideoSurface()
  {
    setVideoSurface(null);
  }
  
  public void clearVideoSurface(Surface paramSurface)
  {
    if ((paramSurface != null) && (paramSurface == this.surface)) {
      setVideoSurface(null);
    }
  }
  
  public void clearVideoSurfaceHolder(SurfaceHolder paramSurfaceHolder)
  {
    if ((paramSurfaceHolder != null) && (paramSurfaceHolder == this.surfaceHolder)) {
      setVideoSurfaceHolder(null);
    }
  }
  
  public void clearVideoSurfaceView(SurfaceView paramSurfaceView)
  {
    if (paramSurfaceView == null) {}
    for (paramSurfaceView = null;; paramSurfaceView = paramSurfaceView.getHolder())
    {
      clearVideoSurfaceHolder(paramSurfaceView);
      return;
    }
  }
  
  public void clearVideoTextureView(TextureView paramTextureView)
  {
    if ((paramTextureView != null) && (paramTextureView == this.textureView)) {
      setVideoTextureView(null);
    }
  }
  
  protected ExoPlayer createExoPlayerImpl(Renderer[] paramArrayOfRenderer, TrackSelector paramTrackSelector, LoadControl paramLoadControl, Clock paramClock)
  {
    return new ExoPlayerImpl(paramArrayOfRenderer, paramTrackSelector, paramLoadControl, paramClock);
  }
  
  public PlayerMessage createMessage(PlayerMessage.Target paramTarget)
  {
    return this.player.createMessage(paramTarget);
  }
  
  public AudioAttributes getAudioAttributes()
  {
    return this.audioAttributes;
  }
  
  public DecoderCounters getAudioDecoderCounters()
  {
    return this.audioDecoderCounters;
  }
  
  public Format getAudioFormat()
  {
    return this.audioFormat;
  }
  
  public int getAudioSessionId()
  {
    return this.audioSessionId;
  }
  
  @Deprecated
  public int getAudioStreamType()
  {
    return Util.getStreamTypeForAudioUsage(this.audioAttributes.usage);
  }
  
  public int getBufferedPercentage()
  {
    return this.player.getBufferedPercentage();
  }
  
  public long getBufferedPosition()
  {
    return this.player.getBufferedPosition();
  }
  
  public long getContentPosition()
  {
    return this.player.getContentPosition();
  }
  
  public int getCurrentAdGroupIndex()
  {
    return this.player.getCurrentAdGroupIndex();
  }
  
  public int getCurrentAdIndexInAdGroup()
  {
    return this.player.getCurrentAdIndexInAdGroup();
  }
  
  public Object getCurrentManifest()
  {
    return this.player.getCurrentManifest();
  }
  
  public int getCurrentPeriodIndex()
  {
    return this.player.getCurrentPeriodIndex();
  }
  
  public long getCurrentPosition()
  {
    return this.player.getCurrentPosition();
  }
  
  public Timeline getCurrentTimeline()
  {
    return this.player.getCurrentTimeline();
  }
  
  public TrackGroupArray getCurrentTrackGroups()
  {
    return this.player.getCurrentTrackGroups();
  }
  
  public TrackSelectionArray getCurrentTrackSelections()
  {
    return this.player.getCurrentTrackSelections();
  }
  
  public int getCurrentWindowIndex()
  {
    return this.player.getCurrentWindowIndex();
  }
  
  public long getDuration()
  {
    return this.player.getDuration();
  }
  
  public int getNextWindowIndex()
  {
    return this.player.getNextWindowIndex();
  }
  
  public boolean getPlayWhenReady()
  {
    return this.player.getPlayWhenReady();
  }
  
  public Looper getPlaybackLooper()
  {
    return this.player.getPlaybackLooper();
  }
  
  public PlaybackParameters getPlaybackParameters()
  {
    return this.player.getPlaybackParameters();
  }
  
  public int getPlaybackState()
  {
    return this.player.getPlaybackState();
  }
  
  public int getPreviousWindowIndex()
  {
    return this.player.getPreviousWindowIndex();
  }
  
  public int getRendererCount()
  {
    return this.player.getRendererCount();
  }
  
  public int getRendererType(int paramInt)
  {
    return this.player.getRendererType(paramInt);
  }
  
  public int getRepeatMode()
  {
    return this.player.getRepeatMode();
  }
  
  public boolean getShuffleModeEnabled()
  {
    return this.player.getShuffleModeEnabled();
  }
  
  public Player.TextComponent getTextComponent()
  {
    return this;
  }
  
  public Player.VideoComponent getVideoComponent()
  {
    return this;
  }
  
  public DecoderCounters getVideoDecoderCounters()
  {
    return this.videoDecoderCounters;
  }
  
  public Format getVideoFormat()
  {
    return this.videoFormat;
  }
  
  public int getVideoScalingMode()
  {
    return this.videoScalingMode;
  }
  
  public float getVolume()
  {
    return this.audioVolume;
  }
  
  public boolean isCurrentWindowDynamic()
  {
    return this.player.isCurrentWindowDynamic();
  }
  
  public boolean isCurrentWindowSeekable()
  {
    return this.player.isCurrentWindowSeekable();
  }
  
  public boolean isLoading()
  {
    return this.player.isLoading();
  }
  
  public boolean isPlayingAd()
  {
    return this.player.isPlayingAd();
  }
  
  public void prepare(MediaSource paramMediaSource)
  {
    this.player.prepare(paramMediaSource);
  }
  
  public void prepare(MediaSource paramMediaSource, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.player.prepare(paramMediaSource, paramBoolean1, paramBoolean2);
  }
  
  public void release()
  {
    this.player.release();
    removeSurfaceCallbacks();
    if (this.surface != null)
    {
      if (this.ownsSurface) {
        this.surface.release();
      }
      this.surface = null;
    }
  }
  
  public void removeAudioDebugListener(AudioRendererEventListener paramAudioRendererEventListener)
  {
    this.audioDebugListeners.remove(paramAudioRendererEventListener);
  }
  
  public void removeListener(Player.EventListener paramEventListener)
  {
    this.player.removeListener(paramEventListener);
  }
  
  public void removeMetadataOutput(MetadataOutput paramMetadataOutput)
  {
    this.metadataOutputs.remove(paramMetadataOutput);
  }
  
  public void removeTextOutput(TextOutput paramTextOutput)
  {
    this.textOutputs.remove(paramTextOutput);
  }
  
  public void removeVideoDebugListener(VideoRendererEventListener paramVideoRendererEventListener)
  {
    this.videoDebugListeners.remove(paramVideoRendererEventListener);
  }
  
  public void removeVideoListener(VideoListener paramVideoListener)
  {
    this.videoListeners.remove(paramVideoListener);
  }
  
  public void seekTo(int paramInt, long paramLong)
  {
    this.player.seekTo(paramInt, paramLong);
  }
  
  public void seekTo(long paramLong)
  {
    this.player.seekTo(paramLong);
  }
  
  public void seekToDefaultPosition()
  {
    this.player.seekToDefaultPosition();
  }
  
  public void seekToDefaultPosition(int paramInt)
  {
    this.player.seekToDefaultPosition(paramInt);
  }
  
  public void sendMessages(ExoPlayer.ExoPlayerMessage... paramVarArgs)
  {
    this.player.sendMessages(paramVarArgs);
  }
  
  public void setAudioAttributes(AudioAttributes paramAudioAttributes)
  {
    this.audioAttributes = paramAudioAttributes;
    for (Renderer localRenderer : this.renderers) {
      if (localRenderer.getTrackType() == 1) {
        this.player.createMessage(localRenderer).setType(3).setPayload(paramAudioAttributes).send();
      }
    }
  }
  
  @Deprecated
  public void setAudioDebugListener(AudioRendererEventListener paramAudioRendererEventListener)
  {
    this.audioDebugListeners.clear();
    if (paramAudioRendererEventListener != null) {
      addAudioDebugListener(paramAudioRendererEventListener);
    }
  }
  
  @Deprecated
  public void setAudioStreamType(int paramInt)
  {
    int i = Util.getAudioUsageForStreamType(paramInt);
    paramInt = Util.getAudioContentTypeForStreamType(paramInt);
    setAudioAttributes(new AudioAttributes.Builder().setUsage(i).setContentType(paramInt).build());
  }
  
  @Deprecated
  public void setMetadataOutput(MetadataOutput paramMetadataOutput)
  {
    this.metadataOutputs.clear();
    if (paramMetadataOutput != null) {
      addMetadataOutput(paramMetadataOutput);
    }
  }
  
  public void setPlayWhenReady(boolean paramBoolean)
  {
    this.player.setPlayWhenReady(paramBoolean);
  }
  
  public void setPlaybackParameters(PlaybackParameters paramPlaybackParameters)
  {
    this.player.setPlaybackParameters(paramPlaybackParameters);
  }
  
  @Deprecated
  @TargetApi(23)
  public void setPlaybackParams(PlaybackParams paramPlaybackParams)
  {
    if (paramPlaybackParams != null) {
      paramPlaybackParams.allowDefaults();
    }
    for (paramPlaybackParams = new PlaybackParameters(paramPlaybackParams.getSpeed(), paramPlaybackParams.getPitch());; paramPlaybackParams = null)
    {
      setPlaybackParameters(paramPlaybackParams);
      return;
    }
  }
  
  public void setRepeatMode(int paramInt)
  {
    this.player.setRepeatMode(paramInt);
  }
  
  public void setSeekParameters(SeekParameters paramSeekParameters)
  {
    this.player.setSeekParameters(paramSeekParameters);
  }
  
  public void setShuffleModeEnabled(boolean paramBoolean)
  {
    this.player.setShuffleModeEnabled(paramBoolean);
  }
  
  @Deprecated
  public void setTextOutput(TextOutput paramTextOutput)
  {
    this.textOutputs.clear();
    if (paramTextOutput != null) {
      addTextOutput(paramTextOutput);
    }
  }
  
  @Deprecated
  public void setVideoDebugListener(VideoRendererEventListener paramVideoRendererEventListener)
  {
    this.videoDebugListeners.clear();
    if (paramVideoRendererEventListener != null) {
      addVideoDebugListener(paramVideoRendererEventListener);
    }
  }
  
  @Deprecated
  public void setVideoListener(VideoListener paramVideoListener)
  {
    this.videoListeners.clear();
    if (paramVideoListener != null) {
      addVideoListener(paramVideoListener);
    }
  }
  
  public void setVideoScalingMode(int paramInt)
  {
    this.videoScalingMode = paramInt;
    for (Renderer localRenderer : this.renderers) {
      if (localRenderer.getTrackType() == 2) {
        this.player.createMessage(localRenderer).setType(4).setPayload(Integer.valueOf(paramInt)).send();
      }
    }
  }
  
  public void setVideoSurface(Surface paramSurface)
  {
    removeSurfaceCallbacks();
    setVideoSurfaceInternal(paramSurface, false);
  }
  
  public void setVideoSurfaceHolder(SurfaceHolder paramSurfaceHolder)
  {
    removeSurfaceCallbacks();
    this.surfaceHolder = paramSurfaceHolder;
    if (paramSurfaceHolder == null)
    {
      setVideoSurfaceInternal(null, false);
      return;
    }
    paramSurfaceHolder.addCallback(this.componentListener);
    paramSurfaceHolder = paramSurfaceHolder.getSurface();
    if ((paramSurfaceHolder != null) && (paramSurfaceHolder.isValid())) {}
    for (;;)
    {
      setVideoSurfaceInternal(paramSurfaceHolder, false);
      break;
      paramSurfaceHolder = null;
    }
  }
  
  public void setVideoSurfaceView(SurfaceView paramSurfaceView)
  {
    if (paramSurfaceView == null) {}
    for (paramSurfaceView = null;; paramSurfaceView = paramSurfaceView.getHolder())
    {
      setVideoSurfaceHolder(paramSurfaceView);
      return;
    }
  }
  
  public void setVideoTextureView(TextureView paramTextureView)
  {
    Object localObject = null;
    if (this.textureView == paramTextureView) {}
    for (;;)
    {
      return;
      removeSurfaceCallbacks();
      this.textureView = paramTextureView;
      this.needSetSurface = true;
      if (paramTextureView != null) {
        break;
      }
      setVideoSurfaceInternal(null, true);
    }
    if (paramTextureView.getSurfaceTextureListener() != null) {
      Log.w("SimpleExoPlayer", "Replacing existing SurfaceTextureListener.");
    }
    paramTextureView.setSurfaceTextureListener(this.componentListener);
    if (paramTextureView.isAvailable())
    {
      paramTextureView = paramTextureView.getSurfaceTexture();
      label74:
      if (paramTextureView != null) {
        break label94;
      }
    }
    label94:
    for (paramTextureView = (TextureView)localObject;; paramTextureView = new Surface(paramTextureView))
    {
      setVideoSurfaceInternal(paramTextureView, true);
      break;
      paramTextureView = null;
      break label74;
    }
  }
  
  public void setVolume(float paramFloat)
  {
    this.audioVolume = paramFloat;
    for (Renderer localRenderer : this.renderers) {
      if (localRenderer.getTrackType() == 1) {
        this.player.createMessage(localRenderer).setType(2).setPayload(Float.valueOf(paramFloat)).send();
      }
    }
  }
  
  public void stop()
  {
    this.player.stop();
  }
  
  public void stop(boolean paramBoolean)
  {
    this.player.stop(paramBoolean);
  }
  
  private final class ComponentListener
    implements SurfaceHolder.Callback, TextureView.SurfaceTextureListener, AudioRendererEventListener, MetadataOutput, TextOutput, VideoRendererEventListener
  {
    private ComponentListener() {}
    
    public void onAudioDecoderInitialized(String paramString, long paramLong1, long paramLong2)
    {
      Iterator localIterator = SimpleExoPlayer.this.audioDebugListeners.iterator();
      while (localIterator.hasNext()) {
        ((AudioRendererEventListener)localIterator.next()).onAudioDecoderInitialized(paramString, paramLong1, paramLong2);
      }
    }
    
    public void onAudioDisabled(DecoderCounters paramDecoderCounters)
    {
      Iterator localIterator = SimpleExoPlayer.this.audioDebugListeners.iterator();
      while (localIterator.hasNext()) {
        ((AudioRendererEventListener)localIterator.next()).onAudioDisabled(paramDecoderCounters);
      }
      SimpleExoPlayer.access$902(SimpleExoPlayer.this, null);
      SimpleExoPlayer.access$602(SimpleExoPlayer.this, null);
      SimpleExoPlayer.access$802(SimpleExoPlayer.this, 0);
    }
    
    public void onAudioEnabled(DecoderCounters paramDecoderCounters)
    {
      SimpleExoPlayer.access$602(SimpleExoPlayer.this, paramDecoderCounters);
      Iterator localIterator = SimpleExoPlayer.this.audioDebugListeners.iterator();
      while (localIterator.hasNext()) {
        ((AudioRendererEventListener)localIterator.next()).onAudioEnabled(paramDecoderCounters);
      }
    }
    
    public void onAudioInputFormatChanged(Format paramFormat)
    {
      SimpleExoPlayer.access$902(SimpleExoPlayer.this, paramFormat);
      Iterator localIterator = SimpleExoPlayer.this.audioDebugListeners.iterator();
      while (localIterator.hasNext()) {
        ((AudioRendererEventListener)localIterator.next()).onAudioInputFormatChanged(paramFormat);
      }
    }
    
    public void onAudioSessionId(int paramInt)
    {
      SimpleExoPlayer.access$802(SimpleExoPlayer.this, paramInt);
      Iterator localIterator = SimpleExoPlayer.this.audioDebugListeners.iterator();
      while (localIterator.hasNext()) {
        ((AudioRendererEventListener)localIterator.next()).onAudioSessionId(paramInt);
      }
    }
    
    public void onAudioSinkUnderrun(int paramInt, long paramLong1, long paramLong2)
    {
      Iterator localIterator = SimpleExoPlayer.this.audioDebugListeners.iterator();
      while (localIterator.hasNext()) {
        ((AudioRendererEventListener)localIterator.next()).onAudioSinkUnderrun(paramInt, paramLong1, paramLong2);
      }
    }
    
    public void onCues(List<Cue> paramList)
    {
      Iterator localIterator = SimpleExoPlayer.this.textOutputs.iterator();
      while (localIterator.hasNext()) {
        ((TextOutput)localIterator.next()).onCues(paramList);
      }
    }
    
    public void onDroppedFrames(int paramInt, long paramLong)
    {
      Iterator localIterator = SimpleExoPlayer.this.videoDebugListeners.iterator();
      while (localIterator.hasNext()) {
        ((VideoRendererEventListener)localIterator.next()).onDroppedFrames(paramInt, paramLong);
      }
    }
    
    public void onMetadata(Metadata paramMetadata)
    {
      Iterator localIterator = SimpleExoPlayer.this.metadataOutputs.iterator();
      while (localIterator.hasNext()) {
        ((MetadataOutput)localIterator.next()).onMetadata(paramMetadata);
      }
    }
    
    public void onRenderedFirstFrame(Surface paramSurface)
    {
      if (SimpleExoPlayer.this.surface == paramSurface)
      {
        localIterator = SimpleExoPlayer.this.videoListeners.iterator();
        while (localIterator.hasNext()) {
          ((VideoListener)localIterator.next()).onRenderedFirstFrame();
        }
      }
      Iterator localIterator = SimpleExoPlayer.this.videoDebugListeners.iterator();
      while (localIterator.hasNext()) {
        ((VideoRendererEventListener)localIterator.next()).onRenderedFirstFrame(paramSurface);
      }
    }
    
    public void onSurfaceTextureAvailable(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2)
    {
      if (SimpleExoPlayer.this.needSetSurface)
      {
        SimpleExoPlayer.this.setVideoSurfaceInternal(new Surface(paramSurfaceTexture), true);
        SimpleExoPlayer.access$1302(SimpleExoPlayer.this, false);
      }
    }
    
    public boolean onSurfaceTextureDestroyed(SurfaceTexture paramSurfaceTexture)
    {
      boolean bool = true;
      Iterator localIterator = SimpleExoPlayer.this.videoListeners.iterator();
      while (localIterator.hasNext()) {
        if (((VideoListener)localIterator.next()).onSurfaceDestroyed(paramSurfaceTexture)) {
          bool = false;
        }
      }
      for (;;)
      {
        return bool;
        SimpleExoPlayer.this.setVideoSurfaceInternal(null, true);
        SimpleExoPlayer.access$1302(SimpleExoPlayer.this, true);
      }
    }
    
    public void onSurfaceTextureSizeChanged(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2) {}
    
    public void onSurfaceTextureUpdated(SurfaceTexture paramSurfaceTexture)
    {
      Iterator localIterator = SimpleExoPlayer.this.videoListeners.iterator();
      while (localIterator.hasNext()) {
        ((VideoListener)localIterator.next()).onSurfaceTextureUpdated(paramSurfaceTexture);
      }
    }
    
    public void onVideoDecoderInitialized(String paramString, long paramLong1, long paramLong2)
    {
      Iterator localIterator = SimpleExoPlayer.this.videoDebugListeners.iterator();
      while (localIterator.hasNext()) {
        ((VideoRendererEventListener)localIterator.next()).onVideoDecoderInitialized(paramString, paramLong1, paramLong2);
      }
    }
    
    public void onVideoDisabled(DecoderCounters paramDecoderCounters)
    {
      Iterator localIterator = SimpleExoPlayer.this.videoDebugListeners.iterator();
      while (localIterator.hasNext()) {
        ((VideoRendererEventListener)localIterator.next()).onVideoDisabled(paramDecoderCounters);
      }
      SimpleExoPlayer.access$302(SimpleExoPlayer.this, null);
      SimpleExoPlayer.access$102(SimpleExoPlayer.this, null);
    }
    
    public void onVideoEnabled(DecoderCounters paramDecoderCounters)
    {
      SimpleExoPlayer.access$102(SimpleExoPlayer.this, paramDecoderCounters);
      Iterator localIterator = SimpleExoPlayer.this.videoDebugListeners.iterator();
      while (localIterator.hasNext()) {
        ((VideoRendererEventListener)localIterator.next()).onVideoEnabled(paramDecoderCounters);
      }
    }
    
    public void onVideoInputFormatChanged(Format paramFormat)
    {
      SimpleExoPlayer.access$302(SimpleExoPlayer.this, paramFormat);
      Iterator localIterator = SimpleExoPlayer.this.videoDebugListeners.iterator();
      while (localIterator.hasNext()) {
        ((VideoRendererEventListener)localIterator.next()).onVideoInputFormatChanged(paramFormat);
      }
    }
    
    public void onVideoSizeChanged(int paramInt1, int paramInt2, int paramInt3, float paramFloat)
    {
      Iterator localIterator = SimpleExoPlayer.this.videoListeners.iterator();
      while (localIterator.hasNext()) {
        ((VideoListener)localIterator.next()).onVideoSizeChanged(paramInt1, paramInt2, paramInt3, paramFloat);
      }
      localIterator = SimpleExoPlayer.this.videoDebugListeners.iterator();
      while (localIterator.hasNext()) {
        ((VideoRendererEventListener)localIterator.next()).onVideoSizeChanged(paramInt1, paramInt2, paramInt3, paramFloat);
      }
    }
    
    public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3) {}
    
    public void surfaceCreated(SurfaceHolder paramSurfaceHolder)
    {
      SimpleExoPlayer.this.setVideoSurfaceInternal(paramSurfaceHolder.getSurface(), false);
    }
    
    public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder)
    {
      SimpleExoPlayer.this.setVideoSurfaceInternal(null, false);
    }
  }
  
  @Deprecated
  public static abstract interface VideoListener
    extends VideoListener
  {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/SimpleExoPlayer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */