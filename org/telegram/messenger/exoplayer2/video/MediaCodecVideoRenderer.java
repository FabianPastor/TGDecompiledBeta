package org.telegram.messenger.exoplayer2.video;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.media.MediaCodec;
import android.media.MediaCodec.OnFrameRenderedListener;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;
import java.nio.ByteBuffer;
import java.util.List;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.RendererConfiguration;
import org.telegram.messenger.exoplayer2.decoder.DecoderCounters;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;
import org.telegram.messenger.exoplayer2.drm.DrmInitData.SchemeData;
import org.telegram.messenger.exoplayer2.drm.DrmSessionManager;
import org.telegram.messenger.exoplayer2.drm.FrameworkMediaCrypto;
import org.telegram.messenger.exoplayer2.mediacodec.MediaCodecInfo;
import org.telegram.messenger.exoplayer2.mediacodec.MediaCodecRenderer;
import org.telegram.messenger.exoplayer2.mediacodec.MediaCodecSelector;
import org.telegram.messenger.exoplayer2.mediacodec.MediaCodecUtil;
import org.telegram.messenger.exoplayer2.mediacodec.MediaCodecUtil.DecoderQueryException;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.TraceUtil;
import org.telegram.messenger.exoplayer2.util.Util;

@TargetApi(16)
public class MediaCodecVideoRenderer
  extends MediaCodecRenderer
{
  private static final String KEY_CROP_BOTTOM = "crop-bottom";
  private static final String KEY_CROP_LEFT = "crop-left";
  private static final String KEY_CROP_RIGHT = "crop-right";
  private static final String KEY_CROP_TOP = "crop-top";
  private static final int MAX_PENDING_OUTPUT_STREAM_OFFSET_COUNT = 10;
  private static final int[] STANDARD_LONG_EDGE_VIDEO_PX = { 1920, 1600, 1440, 1280, 960, 854, 640, 540, 480 };
  private static final String TAG = "MediaCodecVideoRenderer";
  private final long allowedJoiningTimeMs;
  private int buffersInCodecCount;
  private CodecMaxValues codecMaxValues;
  private boolean codecNeedsSetOutputSurfaceWorkaround;
  private int consecutiveDroppedFrameCount;
  private final Context context;
  private int currentHeight;
  private float currentPixelWidthHeightRatio;
  private int currentUnappliedRotationDegrees;
  private int currentWidth;
  private final boolean deviceNeedsAutoFrcWorkaround;
  private long droppedFrameAccumulationStartTimeMs;
  private int droppedFrames;
  private Surface dummySurface;
  private final VideoRendererEventListener.EventDispatcher eventDispatcher;
  private boolean forceRenderFrame;
  private final VideoFrameReleaseTimeHelper frameReleaseTimeHelper;
  private long joiningDeadlineMs;
  private final int maxDroppedFramesToNotify;
  private long outputStreamOffsetUs;
  private int pendingOutputStreamOffsetCount;
  private final long[] pendingOutputStreamOffsetsUs;
  private float pendingPixelWidthHeightRatio;
  private int pendingRotationDegrees;
  private boolean renderedFirstFrame;
  private int reportedHeight;
  private float reportedPixelWidthHeightRatio;
  private int reportedUnappliedRotationDegrees;
  private int reportedWidth;
  private int scalingMode;
  private Format[] streamFormats;
  private Surface surface;
  private boolean tunneling;
  private int tunnelingAudioSessionId;
  OnFrameRenderedListenerV23 tunnelingOnFrameRenderedListener;
  
  public MediaCodecVideoRenderer(Context paramContext, MediaCodecSelector paramMediaCodecSelector)
  {
    this(paramContext, paramMediaCodecSelector, 0L);
  }
  
  public MediaCodecVideoRenderer(Context paramContext, MediaCodecSelector paramMediaCodecSelector, long paramLong)
  {
    this(paramContext, paramMediaCodecSelector, paramLong, null, null, -1);
  }
  
  public MediaCodecVideoRenderer(Context paramContext, MediaCodecSelector paramMediaCodecSelector, long paramLong, Handler paramHandler, VideoRendererEventListener paramVideoRendererEventListener, int paramInt)
  {
    this(paramContext, paramMediaCodecSelector, paramLong, null, false, paramHandler, paramVideoRendererEventListener, paramInt);
  }
  
  public MediaCodecVideoRenderer(Context paramContext, MediaCodecSelector paramMediaCodecSelector, long paramLong, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, boolean paramBoolean, Handler paramHandler, VideoRendererEventListener paramVideoRendererEventListener, int paramInt)
  {
    super(2, paramMediaCodecSelector, paramDrmSessionManager, paramBoolean);
    this.allowedJoiningTimeMs = paramLong;
    this.maxDroppedFramesToNotify = paramInt;
    this.context = paramContext.getApplicationContext();
    this.frameReleaseTimeHelper = new VideoFrameReleaseTimeHelper(paramContext);
    this.eventDispatcher = new VideoRendererEventListener.EventDispatcher(paramHandler, paramVideoRendererEventListener);
    this.deviceNeedsAutoFrcWorkaround = deviceNeedsAutoFrcWorkaround();
    this.pendingOutputStreamOffsetsUs = new long[10];
    this.outputStreamOffsetUs = -9223372036854775807L;
    this.joiningDeadlineMs = -9223372036854775807L;
    this.currentWidth = -1;
    this.currentHeight = -1;
    this.currentPixelWidthHeightRatio = -1.0F;
    this.pendingPixelWidthHeightRatio = -1.0F;
    this.scalingMode = 1;
    clearReportedVideoSize();
  }
  
  private static boolean areAdaptationCompatible(boolean paramBoolean, Format paramFormat1, Format paramFormat2)
  {
    if ((paramFormat1.sampleMimeType.equals(paramFormat2.sampleMimeType)) && (getRotationDegrees(paramFormat1) == getRotationDegrees(paramFormat2)) && ((paramBoolean) || ((paramFormat1.width == paramFormat2.width) && (paramFormat1.height == paramFormat2.height)))) {}
    for (paramBoolean = true;; paramBoolean = false) {
      return paramBoolean;
    }
  }
  
  private void clearRenderedFirstFrame()
  {
    this.renderedFirstFrame = false;
    if ((Util.SDK_INT >= 23) && (this.tunneling))
    {
      MediaCodec localMediaCodec = getCodec();
      if (localMediaCodec != null) {
        this.tunnelingOnFrameRenderedListener = new OnFrameRenderedListenerV23(localMediaCodec, null);
      }
    }
  }
  
  private void clearReportedVideoSize()
  {
    this.reportedWidth = -1;
    this.reportedHeight = -1;
    this.reportedPixelWidthHeightRatio = -1.0F;
    this.reportedUnappliedRotationDegrees = -1;
  }
  
  private static boolean codecNeedsSetOutputSurfaceWorkaround(String paramString)
  {
    if (((!"deb".equals(Util.DEVICE)) && (!"flo".equals(Util.DEVICE))) || (("OMX.qcom.video.decoder.avc".equals(paramString)) || (((!"tcl_eu".equals(Util.DEVICE)) && (!"SVP-DTV15".equals(Util.DEVICE)) && (!"BRAVIA_ATV2".equals(Util.DEVICE))) || (("OMX.MTK.VIDEO.DECODER.AVC".equals(paramString)) || (("OMX.k3.video.decoder.avc".equals(paramString)) && ("ALE-L21".equals(Util.MODEL))))))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  @TargetApi(21)
  private static void configureTunnelingV21(MediaFormat paramMediaFormat, int paramInt)
  {
    paramMediaFormat.setFeatureEnabled("tunneled-playback", true);
    paramMediaFormat.setInteger("audio-session-id", paramInt);
  }
  
  private static boolean deviceNeedsAutoFrcWorkaround()
  {
    if ((Util.SDK_INT <= 22) && ("foster".equals(Util.DEVICE)) && ("NVIDIA".equals(Util.MANUFACTURER))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private static Point getCodecMaxSize(MediaCodecInfo paramMediaCodecInfo, Format paramFormat)
    throws MediaCodecUtil.DecoderQueryException
  {
    int i;
    int j;
    label22:
    int k;
    label32:
    int n;
    int i1;
    int i2;
    Object localObject;
    if (paramFormat.height > paramFormat.width)
    {
      i = 1;
      if (i == 0) {
        break label100;
      }
      j = paramFormat.height;
      if (i == 0) {
        break label108;
      }
      k = paramFormat.width;
      float f1 = k / j;
      int[] arrayOfInt = STANDARD_LONG_EDGE_VIDEO_PX;
      int m = arrayOfInt.length;
      n = 0;
      if (n >= m) {
        break label271;
      }
      i1 = arrayOfInt[n];
      i2 = (int)(i1 * f1);
      if ((i1 > j) && (i2 > k)) {
        break label117;
      }
      localObject = null;
    }
    for (;;)
    {
      return (Point)localObject;
      i = 0;
      break;
      label100:
      j = paramFormat.width;
      break label22;
      label108:
      k = paramFormat.height;
      break label32;
      label117:
      int i3;
      if (Util.SDK_INT >= 21) {
        if (i != 0)
        {
          i3 = i2;
          label133:
          if (i == 0) {
            break label194;
          }
          i2 = i1;
          label141:
          Point localPoint = paramMediaCodecInfo.alignVideoSizeV21(i3, i2);
          float f2 = paramFormat.frameRate;
          localObject = localPoint;
          if (paramMediaCodecInfo.isVideoSizeAndRateSupportedV21(localPoint.x, localPoint.y, f2)) {
            continue;
          }
        }
      }
      label194:
      do
      {
        n++;
        break;
        i3 = i1;
        break label133;
        break label141;
        i3 = Util.ceilDivide(i1, 16) * 16;
        i2 = Util.ceilDivide(i2, 16) * 16;
      } while (i3 * i2 > MediaCodecUtil.maxH264DecodableFrameSize());
      if (i != 0)
      {
        j = i2;
        label239:
        if (i == 0) {
          break label264;
        }
      }
      for (;;)
      {
        localObject = new Point(j, i3);
        break;
        j = i3;
        break label239;
        label264:
        i3 = i2;
      }
      label271:
      localObject = null;
    }
  }
  
  private static int getMaxInputSize(String paramString, int paramInt1, int paramInt2)
  {
    int i = -1;
    int j = i;
    if (paramInt1 != -1) {
      if (paramInt2 != -1) {
        break label21;
      }
    }
    for (j = i;; j = i)
    {
      return j;
      label21:
      switch (paramString.hashCode())
      {
      default: 
        label84:
        j = -1;
        label87:
        switch (j)
        {
        }
        break;
      }
    }
    paramInt1 *= paramInt2;
    paramInt2 = 2;
    for (;;)
    {
      j = paramInt1 * 3 / (paramInt2 * 2);
      break;
      if (!paramString.equals("video/3gpp")) {
        break label84;
      }
      j = 0;
      break label87;
      if (!paramString.equals("video/mp4v-es")) {
        break label84;
      }
      j = 1;
      break label87;
      if (!paramString.equals("video/avc")) {
        break label84;
      }
      j = 2;
      break label87;
      if (!paramString.equals("video/x-vnd.on2.vp8")) {
        break label84;
      }
      j = 3;
      break label87;
      if (!paramString.equals("video/hevc")) {
        break label84;
      }
      j = 4;
      break label87;
      if (!paramString.equals("video/x-vnd.on2.vp9")) {
        break label84;
      }
      j = 5;
      break label87;
      j = i;
      if ("BRAVIA 4K 2015".equals(Util.MODEL)) {
        break;
      }
      paramInt1 = Util.ceilDivide(paramInt1, 16) * Util.ceilDivide(paramInt2, 16) * 16 * 16;
      paramInt2 = 2;
      continue;
      paramInt1 *= paramInt2;
      paramInt2 = 2;
      continue;
      paramInt1 *= paramInt2;
      paramInt2 = 4;
    }
  }
  
  private static int getMaxInputSize(Format paramFormat)
  {
    int i;
    if (paramFormat.maxInputSize != -1)
    {
      i = 0;
      int j = paramFormat.initializationData.size();
      for (k = 0; k < j; k++) {
        i += ((byte[])paramFormat.initializationData.get(k)).length;
      }
    }
    for (int k = paramFormat.maxInputSize + i;; k = getMaxInputSize(paramFormat.sampleMimeType, paramFormat.width, paramFormat.height)) {
      return k;
    }
  }
  
  private static float getPixelWidthHeightRatio(Format paramFormat)
  {
    if (paramFormat.pixelWidthHeightRatio == -1.0F) {}
    for (float f = 1.0F;; f = paramFormat.pixelWidthHeightRatio) {
      return f;
    }
  }
  
  private static int getRotationDegrees(Format paramFormat)
  {
    if (paramFormat.rotationDegrees == -1) {}
    for (int i = 0;; i = paramFormat.rotationDegrees) {
      return i;
    }
  }
  
  private static boolean isBufferLate(long paramLong)
  {
    if (paramLong < -30000L) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private static boolean isBufferVeryLate(long paramLong)
  {
    if (paramLong < -500000L) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private void maybeNotifyDroppedFrames()
  {
    if (this.droppedFrames > 0)
    {
      long l1 = SystemClock.elapsedRealtime();
      long l2 = this.droppedFrameAccumulationStartTimeMs;
      this.eventDispatcher.droppedFrames(this.droppedFrames, l1 - l2);
      this.droppedFrames = 0;
      this.droppedFrameAccumulationStartTimeMs = l1;
    }
  }
  
  private void maybeNotifyVideoSizeChanged()
  {
    if (((this.currentWidth != -1) || (this.currentHeight != -1)) && ((this.reportedWidth != this.currentWidth) || (this.reportedHeight != this.currentHeight) || (this.reportedUnappliedRotationDegrees != this.currentUnappliedRotationDegrees) || (this.reportedPixelWidthHeightRatio != this.currentPixelWidthHeightRatio)))
    {
      this.eventDispatcher.videoSizeChanged(this.currentWidth, this.currentHeight, this.currentUnappliedRotationDegrees, this.currentPixelWidthHeightRatio);
      this.reportedWidth = this.currentWidth;
      this.reportedHeight = this.currentHeight;
      this.reportedUnappliedRotationDegrees = this.currentUnappliedRotationDegrees;
      this.reportedPixelWidthHeightRatio = this.currentPixelWidthHeightRatio;
    }
  }
  
  private void maybeRenotifyRenderedFirstFrame()
  {
    if (this.renderedFirstFrame) {
      this.eventDispatcher.renderedFirstFrame(this.surface);
    }
  }
  
  private void maybeRenotifyVideoSizeChanged()
  {
    if ((this.reportedWidth != -1) || (this.reportedHeight != -1)) {
      this.eventDispatcher.videoSizeChanged(this.reportedWidth, this.reportedHeight, this.reportedUnappliedRotationDegrees, this.reportedPixelWidthHeightRatio);
    }
  }
  
  private void setJoiningDeadlineMs()
  {
    if (this.allowedJoiningTimeMs > 0L) {}
    for (long l = SystemClock.elapsedRealtime() + this.allowedJoiningTimeMs;; l = -9223372036854775807L)
    {
      this.joiningDeadlineMs = l;
      return;
    }
  }
  
  @TargetApi(23)
  private static void setOutputSurfaceV23(MediaCodec paramMediaCodec, Surface paramSurface)
  {
    paramMediaCodec.setOutputSurface(paramSurface);
  }
  
  private void setSurface(Surface paramSurface)
    throws ExoPlaybackException
  {
    Surface localSurface = paramSurface;
    if (paramSurface == null)
    {
      if (this.dummySurface != null) {
        localSurface = this.dummySurface;
      }
    }
    else
    {
      if (this.surface == localSurface) {
        break label179;
      }
      this.surface = localSurface;
      int i = getState();
      if ((i == 1) || (i == 2))
      {
        paramSurface = getCodec();
        if ((Util.SDK_INT < 23) || (paramSurface == null) || (localSurface == null) || (this.codecNeedsSetOutputSurfaceWorkaround)) {
          break label157;
        }
        setOutputSurfaceV23(paramSurface, localSurface);
      }
      label79:
      if ((localSurface == null) || (localSurface == this.dummySurface)) {
        break label168;
      }
      maybeRenotifyVideoSizeChanged();
      clearRenderedFirstFrame();
      if (i == 2) {
        setJoiningDeadlineMs();
      }
    }
    for (;;)
    {
      return;
      MediaCodecInfo localMediaCodecInfo = getCodecInfo();
      localSurface = paramSurface;
      if (localMediaCodecInfo == null) {
        break;
      }
      localSurface = paramSurface;
      if (!shouldUseDummySurface(localMediaCodecInfo)) {
        break;
      }
      this.dummySurface = DummySurface.newInstanceV17(this.context, localMediaCodecInfo.secure);
      localSurface = this.dummySurface;
      break;
      label157:
      releaseCodec();
      maybeInitCodec();
      break label79;
      label168:
      clearReportedVideoSize();
      clearRenderedFirstFrame();
      continue;
      label179:
      if ((localSurface != null) && (localSurface != this.dummySurface))
      {
        maybeRenotifyVideoSizeChanged();
        maybeRenotifyRenderedFirstFrame();
      }
    }
  }
  
  private static void setVideoScalingMode(MediaCodec paramMediaCodec, int paramInt)
  {
    paramMediaCodec.setVideoScalingMode(paramInt);
  }
  
  private boolean shouldUseDummySurface(MediaCodecInfo paramMediaCodecInfo)
  {
    if ((Util.SDK_INT >= 23) && (!this.tunneling) && (!codecNeedsSetOutputSurfaceWorkaround(paramMediaCodecInfo.name)) && ((!paramMediaCodecInfo.secure) || (DummySurface.isSecureSupported(this.context)))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  protected boolean canReconfigureCodec(MediaCodec paramMediaCodec, boolean paramBoolean, Format paramFormat1, Format paramFormat2)
  {
    if ((areAdaptationCompatible(paramBoolean, paramFormat1, paramFormat2)) && (paramFormat2.width <= this.codecMaxValues.width) && (paramFormat2.height <= this.codecMaxValues.height) && (getMaxInputSize(paramFormat2) <= this.codecMaxValues.inputSize)) {}
    for (paramBoolean = true;; paramBoolean = false) {
      return paramBoolean;
    }
  }
  
  protected void configureCodec(MediaCodecInfo paramMediaCodecInfo, MediaCodec paramMediaCodec, Format paramFormat, MediaCrypto paramMediaCrypto)
    throws MediaCodecUtil.DecoderQueryException
  {
    this.codecMaxValues = getCodecMaxValues(paramMediaCodecInfo, paramFormat, this.streamFormats);
    paramFormat = getMediaFormat(paramFormat, this.codecMaxValues, this.deviceNeedsAutoFrcWorkaround, this.tunnelingAudioSessionId);
    if (this.surface == null)
    {
      Assertions.checkState(shouldUseDummySurface(paramMediaCodecInfo));
      if (this.dummySurface == null) {
        this.dummySurface = DummySurface.newInstanceV17(this.context, paramMediaCodecInfo.secure);
      }
      this.surface = this.dummySurface;
    }
    paramMediaCodec.configure(paramFormat, this.surface, paramMediaCrypto, 0);
    if ((Util.SDK_INT >= 23) && (this.tunneling)) {
      this.tunnelingOnFrameRenderedListener = new OnFrameRenderedListenerV23(paramMediaCodec, null);
    }
  }
  
  protected void dropOutputBuffer(MediaCodec paramMediaCodec, int paramInt, long paramLong)
  {
    TraceUtil.beginSection("dropVideoBuffer");
    paramMediaCodec.releaseOutputBuffer(paramInt, false);
    TraceUtil.endSection();
    updateDroppedBufferCounters(1);
  }
  
  protected void flushCodec()
    throws ExoPlaybackException
  {
    super.flushCodec();
    this.buffersInCodecCount = 0;
    this.forceRenderFrame = false;
  }
  
  protected CodecMaxValues getCodecMaxValues(MediaCodecInfo paramMediaCodecInfo, Format paramFormat, Format[] paramArrayOfFormat)
    throws MediaCodecUtil.DecoderQueryException
  {
    int i = paramFormat.width;
    int j = paramFormat.height;
    int k = getMaxInputSize(paramFormat);
    if (paramArrayOfFormat.length == 1) {}
    int i1;
    int i4;
    int i5;
    for (paramMediaCodecInfo = new CodecMaxValues(i, j, k);; paramMediaCodecInfo = new CodecMaxValues(i1, i4, i5))
    {
      return paramMediaCodecInfo;
      int m = 0;
      int n = paramArrayOfFormat.length;
      i1 = 0;
      if (i1 < n)
      {
        Format localFormat = paramArrayOfFormat[i1];
        int i2 = m;
        int i3 = j;
        i4 = k;
        i5 = i;
        if (areAdaptationCompatible(paramMediaCodecInfo.adaptive, paramFormat, localFormat)) {
          if ((localFormat.width != -1) && (localFormat.height != -1)) {
            break label178;
          }
        }
        label178:
        for (i5 = 1;; i5 = 0)
        {
          i2 = m | i5;
          i5 = Math.max(i, localFormat.width);
          i3 = Math.max(j, localFormat.height);
          i4 = Math.max(k, getMaxInputSize(localFormat));
          i1++;
          m = i2;
          j = i3;
          k = i4;
          i = i5;
          break;
        }
      }
      i4 = j;
      i5 = k;
      i1 = i;
      if (m != 0)
      {
        Log.w("MediaCodecVideoRenderer", "Resolutions unknown. Codec max resolution: " + i + "x" + j);
        paramMediaCodecInfo = getCodecMaxSize(paramMediaCodecInfo, paramFormat);
        i4 = j;
        i5 = k;
        i1 = i;
        if (paramMediaCodecInfo != null)
        {
          i1 = Math.max(i, paramMediaCodecInfo.x);
          i4 = Math.max(j, paramMediaCodecInfo.y);
          i5 = Math.max(k, getMaxInputSize(paramFormat.sampleMimeType, i1, i4));
          Log.w("MediaCodecVideoRenderer", "Codec max resolution adjusted to: " + i1 + "x" + i4);
        }
      }
    }
  }
  
  @SuppressLint({"InlinedApi"})
  protected MediaFormat getMediaFormat(Format paramFormat, CodecMaxValues paramCodecMaxValues, boolean paramBoolean, int paramInt)
  {
    paramFormat = getMediaFormatForPlayback(paramFormat);
    paramFormat.setInteger("max-width", paramCodecMaxValues.width);
    paramFormat.setInteger("max-height", paramCodecMaxValues.height);
    if (paramCodecMaxValues.inputSize != -1) {
      paramFormat.setInteger("max-input-size", paramCodecMaxValues.inputSize);
    }
    if (paramBoolean) {
      paramFormat.setInteger("auto-frc", 0);
    }
    if (paramInt != 0) {
      configureTunnelingV21(paramFormat, paramInt);
    }
    return paramFormat;
  }
  
  public void handleMessage(int paramInt, Object paramObject)
    throws ExoPlaybackException
  {
    if (paramInt == 1) {
      setSurface((Surface)paramObject);
    }
    for (;;)
    {
      return;
      if (paramInt == 4)
      {
        this.scalingMode = ((Integer)paramObject).intValue();
        paramObject = getCodec();
        if (paramObject != null) {
          setVideoScalingMode((MediaCodec)paramObject, this.scalingMode);
        }
      }
      else
      {
        super.handleMessage(paramInt, paramObject);
      }
    }
  }
  
  public boolean isReady()
  {
    boolean bool = true;
    if ((super.isReady()) && ((this.renderedFirstFrame) || ((this.dummySurface != null) && (this.surface == this.dummySurface)) || (getCodec() == null) || (this.tunneling))) {
      this.joiningDeadlineMs = -9223372036854775807L;
    }
    for (;;)
    {
      return bool;
      if (this.joiningDeadlineMs == -9223372036854775807L)
      {
        bool = false;
      }
      else if (SystemClock.elapsedRealtime() >= this.joiningDeadlineMs)
      {
        this.joiningDeadlineMs = -9223372036854775807L;
        bool = false;
      }
    }
  }
  
  protected boolean maybeDropBuffersToKeyframe(MediaCodec paramMediaCodec, int paramInt, long paramLong1, long paramLong2)
    throws ExoPlaybackException
  {
    paramInt = skipSource(paramLong2);
    if (paramInt == 0) {}
    for (boolean bool = false;; bool = true)
    {
      return bool;
      paramMediaCodec = this.decoderCounters;
      paramMediaCodec.droppedToKeyframeCount += 1;
      updateDroppedBufferCounters(this.buffersInCodecCount + paramInt);
      flushCodec();
    }
  }
  
  void maybeNotifyRenderedFirstFrame()
  {
    if (!this.renderedFirstFrame)
    {
      this.renderedFirstFrame = true;
      this.eventDispatcher.renderedFirstFrame(this.surface);
    }
  }
  
  protected void onCodecInitialized(String paramString, long paramLong1, long paramLong2)
  {
    this.eventDispatcher.decoderInitialized(paramString, paramLong1, paramLong2);
    this.codecNeedsSetOutputSurfaceWorkaround = codecNeedsSetOutputSurfaceWorkaround(paramString);
  }
  
  protected void onDisabled()
  {
    this.currentWidth = -1;
    this.currentHeight = -1;
    this.currentPixelWidthHeightRatio = -1.0F;
    this.pendingPixelWidthHeightRatio = -1.0F;
    this.outputStreamOffsetUs = -9223372036854775807L;
    this.pendingOutputStreamOffsetCount = 0;
    clearReportedVideoSize();
    clearRenderedFirstFrame();
    this.frameReleaseTimeHelper.disable();
    this.tunnelingOnFrameRenderedListener = null;
    this.tunneling = false;
    try
    {
      super.onDisabled();
      return;
    }
    finally
    {
      this.decoderCounters.ensureUpdated();
      this.eventDispatcher.disabled(this.decoderCounters);
    }
  }
  
  protected void onEnabled(boolean paramBoolean)
    throws ExoPlaybackException
  {
    super.onEnabled(paramBoolean);
    this.tunnelingAudioSessionId = getConfiguration().tunnelingAudioSessionId;
    if (this.tunnelingAudioSessionId != 0) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      this.tunneling = paramBoolean;
      this.eventDispatcher.enabled(this.decoderCounters);
      this.frameReleaseTimeHelper.enable();
      return;
    }
  }
  
  protected void onInputFormatChanged(Format paramFormat)
    throws ExoPlaybackException
  {
    super.onInputFormatChanged(paramFormat);
    this.eventDispatcher.inputFormatChanged(paramFormat);
    this.pendingPixelWidthHeightRatio = getPixelWidthHeightRatio(paramFormat);
    this.pendingRotationDegrees = getRotationDegrees(paramFormat);
  }
  
  protected void onOutputFormatChanged(MediaCodec paramMediaCodec, MediaFormat paramMediaFormat)
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
        break label179;
      }
      i = paramMediaFormat.getInteger("crop-bottom") - paramMediaFormat.getInteger("crop-top") + 1;
      label85:
      this.currentHeight = i;
      this.currentPixelWidthHeightRatio = this.pendingPixelWidthHeightRatio;
      if (Util.SDK_INT < 21) {
        break label190;
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
      setVideoScalingMode(paramMediaCodec, this.scalingMode);
      return;
      i = 0;
      break;
      label167:
      j = paramMediaFormat.getInteger("width");
      break label59;
      label179:
      i = paramMediaFormat.getInteger("height");
      break label85;
      label190:
      this.currentUnappliedRotationDegrees = this.pendingRotationDegrees;
    }
  }
  
  protected void onPositionReset(long paramLong, boolean paramBoolean)
    throws ExoPlaybackException
  {
    super.onPositionReset(paramLong, paramBoolean);
    clearRenderedFirstFrame();
    this.consecutiveDroppedFrameCount = 0;
    if (this.pendingOutputStreamOffsetCount != 0)
    {
      this.outputStreamOffsetUs = this.pendingOutputStreamOffsetsUs[(this.pendingOutputStreamOffsetCount - 1)];
      this.pendingOutputStreamOffsetCount = 0;
    }
    if (paramBoolean) {
      setJoiningDeadlineMs();
    }
    for (;;)
    {
      return;
      this.joiningDeadlineMs = -9223372036854775807L;
    }
  }
  
  protected void onProcessedOutputBuffer(long paramLong)
  {
    this.buffersInCodecCount -= 1;
  }
  
  protected void onQueueInputBuffer(DecoderInputBuffer paramDecoderInputBuffer)
  {
    this.buffersInCodecCount += 1;
    if ((Util.SDK_INT < 23) && (this.tunneling)) {
      maybeNotifyRenderedFirstFrame();
    }
  }
  
  protected void onStarted()
  {
    super.onStarted();
    this.droppedFrames = 0;
    this.droppedFrameAccumulationStartTimeMs = SystemClock.elapsedRealtime();
  }
  
  protected void onStopped()
  {
    this.joiningDeadlineMs = -9223372036854775807L;
    maybeNotifyDroppedFrames();
    super.onStopped();
  }
  
  protected void onStreamChanged(Format[] paramArrayOfFormat, long paramLong)
    throws ExoPlaybackException
  {
    this.streamFormats = paramArrayOfFormat;
    if (this.outputStreamOffsetUs == -9223372036854775807L)
    {
      this.outputStreamOffsetUs = paramLong;
      super.onStreamChanged(paramArrayOfFormat, paramLong);
      return;
    }
    if (this.pendingOutputStreamOffsetCount == this.pendingOutputStreamOffsetsUs.length) {
      Log.w("MediaCodecVideoRenderer", "Too many stream changes, so dropping offset: " + this.pendingOutputStreamOffsetsUs[(this.pendingOutputStreamOffsetCount - 1)]);
    }
    for (;;)
    {
      this.pendingOutputStreamOffsetsUs[(this.pendingOutputStreamOffsetCount - 1)] = paramLong;
      break;
      this.pendingOutputStreamOffsetCount += 1;
    }
  }
  
  protected boolean processOutputBuffer(long paramLong1, long paramLong2, MediaCodec paramMediaCodec, ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, long paramLong3, boolean paramBoolean)
    throws ExoPlaybackException
  {
    while ((this.pendingOutputStreamOffsetCount != 0) && (paramLong3 >= this.pendingOutputStreamOffsetsUs[0]))
    {
      this.outputStreamOffsetUs = this.pendingOutputStreamOffsetsUs[0];
      this.pendingOutputStreamOffsetCount -= 1;
      System.arraycopy(this.pendingOutputStreamOffsetsUs, 1, this.pendingOutputStreamOffsetsUs, 0, this.pendingOutputStreamOffsetCount);
    }
    long l1 = paramLong3 - this.outputStreamOffsetUs;
    if (paramBoolean)
    {
      skipOutputBuffer(paramMediaCodec, paramInt1, l1);
      paramBoolean = true;
    }
    for (;;)
    {
      return paramBoolean;
      long l2 = paramLong3 - paramLong1;
      if (this.surface == this.dummySurface)
      {
        if (isBufferLate(l2))
        {
          this.forceRenderFrame = false;
          skipOutputBuffer(paramMediaCodec, paramInt1, l1);
          paramBoolean = true;
        }
        else
        {
          paramBoolean = false;
        }
      }
      else
      {
        if ((!this.renderedFirstFrame) || (this.forceRenderFrame))
        {
          this.forceRenderFrame = false;
          if (Util.SDK_INT >= 21) {
            renderOutputBufferV21(paramMediaCodec, paramInt1, l1, System.nanoTime());
          }
          for (;;)
          {
            paramBoolean = true;
            break;
            renderOutputBuffer(paramMediaCodec, paramInt1, l1);
          }
        }
        if (getState() != 2)
        {
          paramBoolean = false;
        }
        else
        {
          long l3 = SystemClock.elapsedRealtime();
          long l4 = System.nanoTime();
          paramLong3 = this.frameReleaseTimeHelper.adjustReleaseTime(paramLong3, l4 + 1000L * (l2 - (l3 * 1000L - paramLong2)));
          l4 = (paramLong3 - l4) / 1000L;
          if ((shouldDropBuffersToKeyframe(l4, paramLong2)) && (maybeDropBuffersToKeyframe(paramMediaCodec, paramInt1, l1, paramLong1)))
          {
            this.forceRenderFrame = true;
            paramBoolean = false;
          }
          else if (shouldDropOutputBuffer(l4, paramLong2))
          {
            dropOutputBuffer(paramMediaCodec, paramInt1, l1);
            paramBoolean = true;
          }
          else
          {
            if (Util.SDK_INT >= 21)
            {
              if (l4 < 50000L)
              {
                renderOutputBufferV21(paramMediaCodec, paramInt1, l1, paramLong3);
                paramBoolean = true;
              }
            }
            else if (l4 < 30000L)
            {
              if (l4 > 11000L) {}
              try
              {
                Thread.sleep((l4 - 10000L) / 1000L);
                renderOutputBuffer(paramMediaCodec, paramInt1, l1);
                paramBoolean = true;
              }
              catch (InterruptedException paramByteBuffer)
              {
                for (;;)
                {
                  Thread.currentThread().interrupt();
                }
              }
            }
            paramBoolean = false;
          }
        }
      }
    }
  }
  
  protected void releaseCodec()
  {
    try
    {
      super.releaseCodec();
      return;
    }
    finally
    {
      this.buffersInCodecCount = 0;
      this.forceRenderFrame = false;
      if (this.dummySurface != null)
      {
        if (this.surface == this.dummySurface) {
          this.surface = null;
        }
        this.dummySurface.release();
        this.dummySurface = null;
      }
    }
  }
  
  protected void renderOutputBuffer(MediaCodec paramMediaCodec, int paramInt, long paramLong)
  {
    maybeNotifyVideoSizeChanged();
    TraceUtil.beginSection("releaseOutputBuffer");
    paramMediaCodec.releaseOutputBuffer(paramInt, true);
    TraceUtil.endSection();
    paramMediaCodec = this.decoderCounters;
    paramMediaCodec.renderedOutputBufferCount += 1;
    this.consecutiveDroppedFrameCount = 0;
    maybeNotifyRenderedFirstFrame();
  }
  
  @TargetApi(21)
  protected void renderOutputBufferV21(MediaCodec paramMediaCodec, int paramInt, long paramLong1, long paramLong2)
  {
    maybeNotifyVideoSizeChanged();
    TraceUtil.beginSection("releaseOutputBuffer");
    paramMediaCodec.releaseOutputBuffer(paramInt, paramLong2);
    TraceUtil.endSection();
    paramMediaCodec = this.decoderCounters;
    paramMediaCodec.renderedOutputBufferCount += 1;
    this.consecutiveDroppedFrameCount = 0;
    maybeNotifyRenderedFirstFrame();
  }
  
  protected boolean shouldDropBuffersToKeyframe(long paramLong1, long paramLong2)
  {
    return isBufferVeryLate(paramLong1);
  }
  
  protected boolean shouldDropOutputBuffer(long paramLong1, long paramLong2)
  {
    return isBufferLate(paramLong1);
  }
  
  protected boolean shouldInitCodec(MediaCodecInfo paramMediaCodecInfo)
  {
    if ((this.surface != null) || (shouldUseDummySurface(paramMediaCodecInfo))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  protected void skipOutputBuffer(MediaCodec paramMediaCodec, int paramInt, long paramLong)
  {
    TraceUtil.beginSection("skipVideoBuffer");
    paramMediaCodec.releaseOutputBuffer(paramInt, false);
    TraceUtil.endSection();
    paramMediaCodec = this.decoderCounters;
    paramMediaCodec.skippedOutputBufferCount += 1;
  }
  
  protected int supportsFormat(MediaCodecSelector paramMediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, Format paramFormat)
    throws MediaCodecUtil.DecoderQueryException
  {
    String str = paramFormat.sampleMimeType;
    int i;
    if (!MimeTypes.isVideo(str)) {
      i = 0;
    }
    MediaCodecInfo localMediaCodecInfo;
    for (;;)
    {
      return i;
      bool1 = false;
      bool2 = false;
      DrmInitData localDrmInitData = paramFormat.drmInitData;
      if (localDrmInitData != null) {
        for (i = 0;; i++)
        {
          bool1 = bool2;
          if (i >= localDrmInitData.schemeDataCount) {
            break;
          }
          bool2 |= localDrmInitData.get(i).requiresSecureDecryption;
        }
      }
      localMediaCodecInfo = paramMediaCodecSelector.getDecoderInfo(str, bool1);
      if (localMediaCodecInfo == null)
      {
        if ((bool1) && (paramMediaCodecSelector.getDecoderInfo(str, false) != null)) {
          i = 2;
        } else {
          i = 1;
        }
      }
      else
      {
        if (supportsFormatDrm(paramDrmSessionManager, localDrmInitData)) {
          break;
        }
        i = 2;
      }
    }
    boolean bool1 = localMediaCodecInfo.isCodecSupported(paramFormat.codecs);
    boolean bool2 = bool1;
    if (bool1)
    {
      bool2 = bool1;
      if (paramFormat.width > 0)
      {
        bool2 = bool1;
        if (paramFormat.height > 0)
        {
          if (Util.SDK_INT < 21) {
            break label251;
          }
          bool2 = localMediaCodecInfo.isVideoSizeAndRateSupportedV21(paramFormat.width, paramFormat.height, paramFormat.frameRate);
        }
      }
    }
    label218:
    int j;
    if (localMediaCodecInfo.adaptive)
    {
      i = 16;
      if (!localMediaCodecInfo.tunneling) {
        break label358;
      }
      j = 32;
      label230:
      if (!bool2) {
        break label364;
      }
    }
    label251:
    label358:
    label364:
    for (int k = 4;; k = 3)
    {
      i = i | j | k;
      break;
      if (paramFormat.width * paramFormat.height <= MediaCodecUtil.maxH264DecodableFrameSize()) {}
      for (bool1 = true;; bool1 = false)
      {
        bool2 = bool1;
        if (bool1) {
          break;
        }
        Log.d("MediaCodecVideoRenderer", "FalseCheck [legacyFrameSize, " + paramFormat.width + "x" + paramFormat.height + "] [" + Util.DEVICE_DEBUG_INFO + "]");
        bool2 = bool1;
        break;
      }
      i = 8;
      break label218;
      j = 0;
      break label230;
    }
  }
  
  protected void updateDroppedBufferCounters(int paramInt)
  {
    DecoderCounters localDecoderCounters = this.decoderCounters;
    localDecoderCounters.droppedBufferCount += paramInt;
    this.droppedFrames += paramInt;
    this.consecutiveDroppedFrameCount += paramInt;
    this.decoderCounters.maxConsecutiveDroppedBufferCount = Math.max(this.consecutiveDroppedFrameCount, this.decoderCounters.maxConsecutiveDroppedBufferCount);
    if (this.droppedFrames >= this.maxDroppedFramesToNotify) {
      maybeNotifyDroppedFrames();
    }
  }
  
  protected static final class CodecMaxValues
  {
    public final int height;
    public final int inputSize;
    public final int width;
    
    public CodecMaxValues(int paramInt1, int paramInt2, int paramInt3)
    {
      this.width = paramInt1;
      this.height = paramInt2;
      this.inputSize = paramInt3;
    }
  }
  
  @TargetApi(23)
  private final class OnFrameRenderedListenerV23
    implements MediaCodec.OnFrameRenderedListener
  {
    private OnFrameRenderedListenerV23(MediaCodec paramMediaCodec)
    {
      paramMediaCodec.setOnFrameRenderedListener(this, new Handler());
    }
    
    public void onFrameRendered(MediaCodec paramMediaCodec, long paramLong1, long paramLong2)
    {
      if (this != MediaCodecVideoRenderer.this.tunnelingOnFrameRenderedListener) {}
      for (;;)
      {
        return;
        MediaCodecVideoRenderer.this.maybeNotifyRenderedFirstFrame();
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/video/MediaCodecVideoRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */