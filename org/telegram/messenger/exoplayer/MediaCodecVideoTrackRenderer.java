package org.telegram.messenger.exoplayer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCrypto;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Surface;
import java.nio.ByteBuffer;
import org.telegram.messenger.exoplayer.drm.DrmSessionManager;
import org.telegram.messenger.exoplayer.drm.FrameworkMediaCrypto;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.exoplayer.util.TraceUtil;
import org.telegram.messenger.exoplayer.util.Util;

@TargetApi(16)
public class MediaCodecVideoTrackRenderer
  extends MediaCodecTrackRenderer
{
  private static final String KEY_CROP_BOTTOM = "crop-bottom";
  private static final String KEY_CROP_LEFT = "crop-left";
  private static final String KEY_CROP_RIGHT = "crop-right";
  private static final String KEY_CROP_TOP = "crop-top";
  public static final int MSG_SET_SURFACE = 1;
  private final long allowedJoiningTimeUs;
  private int consecutiveDroppedFrameCount;
  private int currentHeight;
  private float currentPixelWidthHeightRatio;
  private int currentUnappliedRotationDegrees;
  private int currentWidth;
  private long droppedFrameAccumulationStartTimeMs;
  private int droppedFrameCount;
  private final EventListener eventListener;
  private final VideoFrameReleaseTimeHelper frameReleaseTimeHelper;
  private long joiningDeadlineUs;
  private int lastReportedHeight;
  private float lastReportedPixelWidthHeightRatio;
  private int lastReportedUnappliedRotationDegrees;
  private int lastReportedWidth;
  private final int maxDroppedFrameCountToNotify;
  private float pendingPixelWidthHeightRatio;
  private int pendingRotationDegrees;
  private boolean renderedFirstFrame;
  private boolean reportedDrawnToSurface;
  private Surface surface;
  private final int videoScalingMode;
  
  public MediaCodecVideoTrackRenderer(Context paramContext, SampleSource paramSampleSource, MediaCodecSelector paramMediaCodecSelector, int paramInt)
  {
    this(paramContext, paramSampleSource, paramMediaCodecSelector, paramInt, 0L);
  }
  
  public MediaCodecVideoTrackRenderer(Context paramContext, SampleSource paramSampleSource, MediaCodecSelector paramMediaCodecSelector, int paramInt, long paramLong)
  {
    this(paramContext, paramSampleSource, paramMediaCodecSelector, paramInt, paramLong, null, null, -1);
  }
  
  public MediaCodecVideoTrackRenderer(Context paramContext, SampleSource paramSampleSource, MediaCodecSelector paramMediaCodecSelector, int paramInt1, long paramLong, Handler paramHandler, EventListener paramEventListener, int paramInt2)
  {
    this(paramContext, paramSampleSource, paramMediaCodecSelector, paramInt1, paramLong, null, false, paramHandler, paramEventListener, paramInt2);
  }
  
  public MediaCodecVideoTrackRenderer(Context paramContext, SampleSource paramSampleSource, MediaCodecSelector paramMediaCodecSelector, int paramInt1, long paramLong, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, boolean paramBoolean, Handler paramHandler, EventListener paramEventListener, int paramInt2)
  {
    super(paramSampleSource, paramMediaCodecSelector, paramDrmSessionManager, paramBoolean, paramHandler, paramEventListener);
    this.frameReleaseTimeHelper = new VideoFrameReleaseTimeHelper(paramContext);
    this.videoScalingMode = paramInt1;
    this.allowedJoiningTimeUs = (1000L * paramLong);
    this.eventListener = paramEventListener;
    this.maxDroppedFrameCountToNotify = paramInt2;
    this.joiningDeadlineUs = -1L;
    this.currentWidth = -1;
    this.currentHeight = -1;
    this.currentPixelWidthHeightRatio = -1.0F;
    this.pendingPixelWidthHeightRatio = -1.0F;
    this.lastReportedWidth = -1;
    this.lastReportedHeight = -1;
    this.lastReportedPixelWidthHeightRatio = -1.0F;
  }
  
  private void maybeNotifyDrawnToSurface()
  {
    if ((this.eventHandler == null) || (this.eventListener == null) || (this.reportedDrawnToSurface)) {
      return;
    }
    final Surface localSurface = this.surface;
    this.eventHandler.post(new Runnable()
    {
      public void run()
      {
        MediaCodecVideoTrackRenderer.this.eventListener.onDrawnToSurface(localSurface);
      }
    });
    this.reportedDrawnToSurface = true;
  }
  
  private void maybeNotifyDroppedFrameCount()
  {
    if ((this.eventHandler == null) || (this.eventListener == null) || (this.droppedFrameCount == 0)) {
      return;
    }
    long l1 = SystemClock.elapsedRealtime();
    final int i = this.droppedFrameCount;
    long l2 = this.droppedFrameAccumulationStartTimeMs;
    this.eventHandler.post(new Runnable()
    {
      public void run()
      {
        MediaCodecVideoTrackRenderer.this.eventListener.onDroppedFrames(i, this.val$elapsedToNotify);
      }
    });
    this.droppedFrameCount = 0;
    this.droppedFrameAccumulationStartTimeMs = l1;
  }
  
  private void maybeNotifyVideoSizeChanged()
  {
    if ((this.eventHandler == null) || (this.eventListener == null) || ((this.lastReportedWidth == this.currentWidth) && (this.lastReportedHeight == this.currentHeight) && (this.lastReportedUnappliedRotationDegrees == this.currentUnappliedRotationDegrees) && (this.lastReportedPixelWidthHeightRatio == this.currentPixelWidthHeightRatio))) {
      return;
    }
    final int i = this.currentWidth;
    final int j = this.currentHeight;
    final int k = this.currentUnappliedRotationDegrees;
    final float f = this.currentPixelWidthHeightRatio;
    this.eventHandler.post(new Runnable()
    {
      public void run()
      {
        MediaCodecVideoTrackRenderer.this.eventListener.onVideoSizeChanged(i, j, k, f);
      }
    });
    this.lastReportedWidth = i;
    this.lastReportedHeight = j;
    this.lastReportedUnappliedRotationDegrees = k;
    this.lastReportedPixelWidthHeightRatio = f;
  }
  
  @SuppressLint({"InlinedApi"})
  private void maybeSetMaxInputSize(android.media.MediaFormat paramMediaFormat, boolean paramBoolean)
  {
    if (paramMediaFormat.containsKey("max-input-size")) {
      return;
    }
    int i = paramMediaFormat.getInteger("height");
    int j = i;
    if (paramBoolean)
    {
      j = i;
      if (paramMediaFormat.containsKey("max-height")) {
        j = Math.max(i, paramMediaFormat.getInteger("max-height"));
      }
    }
    i = paramMediaFormat.getInteger("width");
    int k = i;
    if (paramBoolean)
    {
      k = i;
      if (paramMediaFormat.containsKey("max-width")) {
        k = Math.max(j, paramMediaFormat.getInteger("max-width"));
      }
    }
    String str = paramMediaFormat.getString("mime");
    i = -1;
    switch (str.hashCode())
    {
    default: 
      switch (i)
      {
      default: 
        return;
      case 0: 
      case 1: 
        label160:
        i = k * j;
        j = 2;
      }
      break;
    }
    for (;;)
    {
      paramMediaFormat.setInteger("max-input-size", i * 3 / (j * 2));
      return;
      if (!str.equals("video/3gpp")) {
        break label160;
      }
      i = 0;
      break label160;
      if (!str.equals("video/mp4v-es")) {
        break label160;
      }
      i = 1;
      break label160;
      if (!str.equals("video/avc")) {
        break label160;
      }
      i = 2;
      break label160;
      if (!str.equals("video/x-vnd.on2.vp8")) {
        break label160;
      }
      i = 3;
      break label160;
      if (!str.equals("video/hevc")) {
        break label160;
      }
      i = 4;
      break label160;
      if (!str.equals("video/x-vnd.on2.vp9")) {
        break label160;
      }
      i = 5;
      break label160;
      if ("BRAVIA 4K 2015".equals(Util.MODEL)) {
        break;
      }
      i = (k + 15) / 16 * ((j + 15) / 16) * 16 * 16;
      j = 2;
      continue;
      i = k * j;
      j = 2;
      continue;
      i = k * j;
      j = 4;
    }
  }
  
  private void setSurface(Surface paramSurface)
    throws ExoPlaybackException
  {
    if (this.surface == paramSurface) {}
    int i;
    do
    {
      return;
      this.surface = paramSurface;
      this.reportedDrawnToSurface = false;
      i = getState();
    } while ((i != 2) && (i != 3));
    releaseCodec();
    maybeInitCodec();
  }
  
  protected boolean canReconfigureCodec(MediaCodec paramMediaCodec, boolean paramBoolean, MediaFormat paramMediaFormat1, MediaFormat paramMediaFormat2)
  {
    return (paramMediaFormat2.mimeType.equals(paramMediaFormat1.mimeType)) && ((paramBoolean) || ((paramMediaFormat1.width == paramMediaFormat2.width) && (paramMediaFormat1.height == paramMediaFormat2.height)));
  }
  
  protected void configureCodec(MediaCodec paramMediaCodec, boolean paramBoolean, android.media.MediaFormat paramMediaFormat, MediaCrypto paramMediaCrypto)
  {
    maybeSetMaxInputSize(paramMediaFormat, paramBoolean);
    paramMediaCodec.configure(paramMediaFormat, this.surface, paramMediaCrypto, 0);
  }
  
  protected void dropOutputBuffer(MediaCodec paramMediaCodec, int paramInt)
  {
    TraceUtil.beginSection("dropVideoBuffer");
    paramMediaCodec.releaseOutputBuffer(paramInt, false);
    TraceUtil.endSection();
    paramMediaCodec = this.codecCounters;
    paramMediaCodec.droppedOutputBufferCount += 1;
    this.droppedFrameCount += 1;
    this.consecutiveDroppedFrameCount += 1;
    this.codecCounters.maxConsecutiveDroppedOutputBufferCount = Math.max(this.consecutiveDroppedFrameCount, this.codecCounters.maxConsecutiveDroppedOutputBufferCount);
    if (this.droppedFrameCount == this.maxDroppedFrameCountToNotify) {
      maybeNotifyDroppedFrameCount();
    }
  }
  
  public void handleMessage(int paramInt, Object paramObject)
    throws ExoPlaybackException
  {
    if (paramInt == 1)
    {
      setSurface((Surface)paramObject);
      return;
    }
    super.handleMessage(paramInt, paramObject);
  }
  
  protected boolean handlesTrack(MediaCodecSelector paramMediaCodecSelector, MediaFormat paramMediaFormat)
    throws MediaCodecUtil.DecoderQueryException
  {
    boolean bool2 = false;
    paramMediaFormat = paramMediaFormat.mimeType;
    boolean bool1 = bool2;
    if (MimeTypes.isVideo(paramMediaFormat)) {
      if (!"video/x-unknown".equals(paramMediaFormat))
      {
        bool1 = bool2;
        if (paramMediaCodecSelector.getDecoderInfo(paramMediaFormat, false) == null) {}
      }
      else
      {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  protected final boolean haveRenderedFirstFrame()
  {
    return this.renderedFirstFrame;
  }
  
  protected boolean isReady()
  {
    if ((super.isReady()) && ((this.renderedFirstFrame) || (!codecInitialized()) || (getSourceState() == 2))) {
      this.joiningDeadlineUs = -1L;
    }
    do
    {
      return true;
      if (this.joiningDeadlineUs == -1L) {
        return false;
      }
    } while (SystemClock.elapsedRealtime() * 1000L < this.joiningDeadlineUs);
    this.joiningDeadlineUs = -1L;
    return false;
  }
  
  protected void onDisabled()
    throws ExoPlaybackException
  {
    this.currentWidth = -1;
    this.currentHeight = -1;
    this.currentPixelWidthHeightRatio = -1.0F;
    this.pendingPixelWidthHeightRatio = -1.0F;
    this.lastReportedWidth = -1;
    this.lastReportedHeight = -1;
    this.lastReportedPixelWidthHeightRatio = -1.0F;
    this.frameReleaseTimeHelper.disable();
    super.onDisabled();
  }
  
  protected void onDiscontinuity(long paramLong)
    throws ExoPlaybackException
  {
    super.onDiscontinuity(paramLong);
    this.renderedFirstFrame = false;
    this.consecutiveDroppedFrameCount = 0;
    this.joiningDeadlineUs = -1L;
  }
  
  protected void onEnabled(int paramInt, long paramLong, boolean paramBoolean)
    throws ExoPlaybackException
  {
    super.onEnabled(paramInt, paramLong, paramBoolean);
    if ((paramBoolean) && (this.allowedJoiningTimeUs > 0L)) {
      this.joiningDeadlineUs = (SystemClock.elapsedRealtime() * 1000L + this.allowedJoiningTimeUs);
    }
    this.frameReleaseTimeHelper.enable();
  }
  
  protected void onInputFormatChanged(MediaFormatHolder paramMediaFormatHolder)
    throws ExoPlaybackException
  {
    super.onInputFormatChanged(paramMediaFormatHolder);
    float f;
    if (paramMediaFormatHolder.format.pixelWidthHeightRatio == -1.0F)
    {
      f = 1.0F;
      this.pendingPixelWidthHeightRatio = f;
      if (paramMediaFormatHolder.format.rotationDegrees != -1) {
        break label55;
      }
    }
    label55:
    for (int i = 0;; i = paramMediaFormatHolder.format.rotationDegrees)
    {
      this.pendingRotationDegrees = i;
      return;
      f = paramMediaFormatHolder.format.pixelWidthHeightRatio;
      break;
    }
  }
  
  protected void onOutputFormatChanged(MediaCodec paramMediaCodec, android.media.MediaFormat paramMediaFormat)
  {
    int i;
    int j;
    if ((paramMediaFormat.containsKey("crop-right")) && (paramMediaFormat.containsKey("crop-left")) && (paramMediaFormat.containsKey("crop-bottom")) && (paramMediaFormat.containsKey("crop-top")))
    {
      i = 1;
      if (i == 0) {
        break label167;
      }
      j = paramMediaFormat.getInteger("crop-right") - paramMediaFormat.getInteger("crop-left") + 1;
      label59:
      this.currentWidth = j;
      if (i == 0) {
        break label178;
      }
      i = paramMediaFormat.getInteger("crop-bottom") - paramMediaFormat.getInteger("crop-top") + 1;
      label85:
      this.currentHeight = i;
      this.currentPixelWidthHeightRatio = this.pendingPixelWidthHeightRatio;
      if (Util.SDK_INT < 21) {
        break label188;
      }
      if ((this.pendingRotationDegrees == 90) || (this.pendingRotationDegrees == 270))
      {
        i = this.currentWidth;
        this.currentWidth = this.currentHeight;
        this.currentHeight = i;
        this.currentPixelWidthHeightRatio = (1.0F / this.currentPixelWidthHeightRatio);
      }
    }
    for (;;)
    {
      paramMediaCodec.setVideoScalingMode(this.videoScalingMode);
      return;
      i = 0;
      break;
      label167:
      j = paramMediaFormat.getInteger("width");
      break label59;
      label178:
      i = paramMediaFormat.getInteger("height");
      break label85;
      label188:
      this.currentUnappliedRotationDegrees = this.pendingRotationDegrees;
    }
  }
  
  protected void onStarted()
  {
    super.onStarted();
    this.droppedFrameCount = 0;
    this.droppedFrameAccumulationStartTimeMs = SystemClock.elapsedRealtime();
  }
  
  protected void onStopped()
  {
    this.joiningDeadlineUs = -1L;
    maybeNotifyDroppedFrameCount();
    super.onStopped();
  }
  
  protected boolean processOutputBuffer(long paramLong1, long paramLong2, MediaCodec paramMediaCodec, ByteBuffer paramByteBuffer, MediaCodec.BufferInfo paramBufferInfo, int paramInt, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      skipOutputBuffer(paramMediaCodec, paramInt);
      this.consecutiveDroppedFrameCount = 0;
      return true;
    }
    if (!this.renderedFirstFrame)
    {
      if (Util.SDK_INT >= 21) {
        renderOutputBufferV21(paramMediaCodec, paramInt, System.nanoTime());
      }
      for (;;)
      {
        this.consecutiveDroppedFrameCount = 0;
        return true;
        renderOutputBuffer(paramMediaCodec, paramInt);
      }
    }
    if (getState() != 3) {
      return false;
    }
    long l2 = SystemClock.elapsedRealtime();
    long l3 = paramBufferInfo.presentationTimeUs;
    long l1 = System.nanoTime();
    paramLong1 = this.frameReleaseTimeHelper.adjustReleaseTime(paramBufferInfo.presentationTimeUs, l1 + 1000L * (l3 - paramLong1 - (l2 * 1000L - paramLong2)));
    paramLong2 = (paramLong1 - l1) / 1000L;
    if (paramLong2 < -30000L)
    {
      dropOutputBuffer(paramMediaCodec, paramInt);
      return true;
    }
    if (Util.SDK_INT >= 21)
    {
      if (paramLong2 < 50000L)
      {
        renderOutputBufferV21(paramMediaCodec, paramInt, paramLong1);
        this.consecutiveDroppedFrameCount = 0;
        return true;
      }
    }
    else if (paramLong2 < 30000L)
    {
      if (paramLong2 > 11000L) {}
      try
      {
        Thread.sleep((paramLong2 - 10000L) / 1000L);
        renderOutputBuffer(paramMediaCodec, paramInt);
        this.consecutiveDroppedFrameCount = 0;
        return true;
      }
      catch (InterruptedException paramByteBuffer)
      {
        for (;;)
        {
          Thread.currentThread().interrupt();
        }
      }
    }
    return false;
  }
  
  protected void renderOutputBuffer(MediaCodec paramMediaCodec, int paramInt)
  {
    maybeNotifyVideoSizeChanged();
    TraceUtil.beginSection("releaseOutputBuffer");
    paramMediaCodec.releaseOutputBuffer(paramInt, true);
    TraceUtil.endSection();
    paramMediaCodec = this.codecCounters;
    paramMediaCodec.renderedOutputBufferCount += 1;
    this.renderedFirstFrame = true;
    maybeNotifyDrawnToSurface();
  }
  
  @TargetApi(21)
  protected void renderOutputBufferV21(MediaCodec paramMediaCodec, int paramInt, long paramLong)
  {
    maybeNotifyVideoSizeChanged();
    TraceUtil.beginSection("releaseOutputBuffer");
    paramMediaCodec.releaseOutputBuffer(paramInt, paramLong);
    TraceUtil.endSection();
    paramMediaCodec = this.codecCounters;
    paramMediaCodec.renderedOutputBufferCount += 1;
    this.renderedFirstFrame = true;
    maybeNotifyDrawnToSurface();
  }
  
  protected boolean shouldInitCodec()
  {
    return (super.shouldInitCodec()) && (this.surface != null) && (this.surface.isValid());
  }
  
  protected void skipOutputBuffer(MediaCodec paramMediaCodec, int paramInt)
  {
    TraceUtil.beginSection("skipVideoBuffer");
    paramMediaCodec.releaseOutputBuffer(paramInt, false);
    TraceUtil.endSection();
    paramMediaCodec = this.codecCounters;
    paramMediaCodec.skippedOutputBufferCount += 1;
  }
  
  public static abstract interface EventListener
    extends MediaCodecTrackRenderer.EventListener
  {
    public abstract void onDrawnToSurface(Surface paramSurface);
    
    public abstract void onDroppedFrames(int paramInt, long paramLong);
    
    public abstract void onVideoSizeChanged(int paramInt1, int paramInt2, int paramInt3, float paramFloat);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/MediaCodecVideoTrackRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */