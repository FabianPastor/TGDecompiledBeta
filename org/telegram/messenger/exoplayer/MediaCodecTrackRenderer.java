package org.telegram.messenger.exoplayer;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodec.CodecException;
import android.media.MediaCodec.CryptoException;
import android.media.MediaCodec.CryptoInfo;
import android.media.MediaCrypto;
import android.os.Handler;
import android.os.SystemClock;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer.drm.DrmInitData;
import org.telegram.messenger.exoplayer.drm.DrmSessionManager;
import org.telegram.messenger.exoplayer.drm.FrameworkMediaCrypto;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.messenger.exoplayer.util.TraceUtil;
import org.telegram.messenger.exoplayer.util.Util;

@TargetApi(16)
public abstract class MediaCodecTrackRenderer
  extends SampleSourceTrackRenderer
{
  private static final byte[] ADAPTATION_WORKAROUND_BUFFER = Util.getBytesFromHexString("0000016742C00BDA259000000168CE0F13200000016588840DCE7118A0002FBF1C31C3275D78");
  private static final int ADAPTATION_WORKAROUND_SLICE_WIDTH_HEIGHT = 32;
  private static final long MAX_CODEC_HOTSWAP_TIME_MS = 1000L;
  private static final int RECONFIGURATION_STATE_NONE = 0;
  private static final int RECONFIGURATION_STATE_QUEUE_PENDING = 2;
  private static final int RECONFIGURATION_STATE_WRITE_PENDING = 1;
  private static final int REINITIALIZATION_STATE_NONE = 0;
  private static final int REINITIALIZATION_STATE_SIGNAL_END_OF_STREAM = 1;
  private static final int REINITIALIZATION_STATE_WAIT_END_OF_STREAM = 2;
  protected static final int SOURCE_STATE_NOT_READY = 0;
  protected static final int SOURCE_STATE_READY = 1;
  protected static final int SOURCE_STATE_READY_READ_MAY_FAIL = 2;
  private MediaCodec codec;
  public final CodecCounters codecCounters;
  private long codecHotswapTimeMs;
  private boolean codecIsAdaptive;
  private boolean codecNeedsAdaptationWorkaround;
  private boolean codecNeedsAdaptationWorkaroundBuffer;
  private boolean codecNeedsDiscardToSpsWorkaround;
  private boolean codecNeedsEosFlushWorkaround;
  private boolean codecNeedsEosPropagationWorkaround;
  private boolean codecNeedsFlushWorkaround;
  private boolean codecNeedsMonoChannelCountWorkaround;
  private boolean codecReceivedBuffers;
  private boolean codecReceivedEos;
  private int codecReconfigurationState;
  private boolean codecReconfigured;
  private int codecReinitializationState;
  private final List<Long> decodeOnlyPresentationTimestamps;
  private final boolean deviceNeedsAutoFrcWorkaround;
  private DrmInitData drmInitData;
  private final DrmSessionManager<FrameworkMediaCrypto> drmSessionManager;
  protected final Handler eventHandler;
  private final EventListener eventListener;
  private MediaFormat format;
  private final MediaFormatHolder formatHolder;
  private ByteBuffer[] inputBuffers;
  private int inputIndex;
  private boolean inputStreamEnded;
  private final MediaCodecSelector mediaCodecSelector;
  private boolean openedDrmSession;
  private final MediaCodec.BufferInfo outputBufferInfo;
  private ByteBuffer[] outputBuffers;
  private int outputIndex;
  private boolean outputStreamEnded;
  private final boolean playClearSamplesWithoutKeys;
  private final SampleHolder sampleHolder;
  private boolean shouldSkipAdaptationWorkaroundOutputBuffer;
  private int sourceState;
  private boolean waitingForFirstSyncFrame;
  private boolean waitingForKeys;
  
  public MediaCodecTrackRenderer(SampleSource paramSampleSource, MediaCodecSelector paramMediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, boolean paramBoolean, Handler paramHandler, EventListener paramEventListener)
  {
    this(new SampleSource[] { paramSampleSource }, paramMediaCodecSelector, paramDrmSessionManager, paramBoolean, paramHandler, paramEventListener);
  }
  
  public MediaCodecTrackRenderer(SampleSource[] paramArrayOfSampleSource, MediaCodecSelector paramMediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, boolean paramBoolean, Handler paramHandler, EventListener paramEventListener)
  {
    super(paramArrayOfSampleSource);
    if (Util.SDK_INT >= 16) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.mediaCodecSelector = ((MediaCodecSelector)Assertions.checkNotNull(paramMediaCodecSelector));
      this.drmSessionManager = paramDrmSessionManager;
      this.playClearSamplesWithoutKeys = paramBoolean;
      this.eventHandler = paramHandler;
      this.eventListener = paramEventListener;
      this.deviceNeedsAutoFrcWorkaround = deviceNeedsAutoFrcWorkaround();
      this.codecCounters = new CodecCounters();
      this.sampleHolder = new SampleHolder(0);
      this.formatHolder = new MediaFormatHolder();
      this.decodeOnlyPresentationTimestamps = new ArrayList();
      this.outputBufferInfo = new MediaCodec.BufferInfo();
      this.codecReconfigurationState = 0;
      this.codecReinitializationState = 0;
      return;
    }
  }
  
  private static boolean codecNeedsAdaptationWorkaround(String paramString)
  {
    return (Util.SDK_INT < 24) && (("OMX.Nvidia.h264.decode".equals(paramString)) || ("OMX.Nvidia.h264.decode.secure".equals(paramString))) && ((Util.DEVICE.equals("flounder")) || (Util.DEVICE.equals("flounder_lte")) || (Util.DEVICE.equals("grouper")) || (Util.DEVICE.equals("tilapia")));
  }
  
  private static boolean codecNeedsDiscardToSpsWorkaround(String paramString, MediaFormat paramMediaFormat)
  {
    return (Util.SDK_INT < 21) && (paramMediaFormat.initializationData.isEmpty()) && ("OMX.MTK.VIDEO.DECODER.AVC".equals(paramString));
  }
  
  private static boolean codecNeedsEosFlushWorkaround(String paramString)
  {
    return (Util.SDK_INT <= 23) && ("OMX.google.vorbis.decoder".equals(paramString));
  }
  
  private static boolean codecNeedsEosPropagationWorkaround(String paramString)
  {
    return (Util.SDK_INT <= 17) && (("OMX.rk.video_decoder.avc".equals(paramString)) || ("OMX.allwinner.video.decoder.avc".equals(paramString)));
  }
  
  private static boolean codecNeedsFlushWorkaround(String paramString)
  {
    return (Util.SDK_INT < 18) || ((Util.SDK_INT == 18) && (("OMX.SEC.avc.dec".equals(paramString)) || ("OMX.SEC.avc.dec.secure".equals(paramString)))) || ((Util.SDK_INT == 19) && (Util.MODEL.startsWith("SM-G800")) && (("OMX.Exynos.avc.dec".equals(paramString)) || ("OMX.Exynos.avc.dec.secure".equals(paramString))));
  }
  
  private static boolean codecNeedsMonoChannelCountWorkaround(String paramString, MediaFormat paramMediaFormat)
  {
    return (Util.SDK_INT <= 18) && (paramMediaFormat.channelCount == 1) && ("OMX.MTK.AUDIO.DECODER.MP3".equals(paramString));
  }
  
  private static boolean deviceNeedsAutoFrcWorkaround()
  {
    return (Util.SDK_INT <= 22) && ("foster".equals(Util.DEVICE)) && ("NVIDIA".equals(Util.MANUFACTURER));
  }
  
  private boolean drainOutputBuffer(long paramLong1, long paramLong2)
    throws ExoPlaybackException
  {
    if (this.outputStreamEnded) {
      return false;
    }
    if (this.outputIndex < 0) {
      this.outputIndex = this.codec.dequeueOutputBuffer(this.outputBufferInfo, getDequeueOutputBufferTimeoutUs());
    }
    if (this.outputIndex == -2)
    {
      processOutputFormat();
      return true;
    }
    if (this.outputIndex == -3)
    {
      this.outputBuffers = this.codec.getOutputBuffers();
      localObject = this.codecCounters;
      ((CodecCounters)localObject).outputBuffersChangedCount += 1;
      return true;
    }
    if (this.outputIndex < 0)
    {
      if ((this.codecNeedsEosPropagationWorkaround) && ((this.inputStreamEnded) || (this.codecReinitializationState == 2)))
      {
        processEndOfStream();
        return true;
      }
      return false;
    }
    if (this.shouldSkipAdaptationWorkaroundOutputBuffer)
    {
      this.shouldSkipAdaptationWorkaroundOutputBuffer = false;
      this.codec.releaseOutputBuffer(this.outputIndex, false);
      this.outputIndex = -1;
      return true;
    }
    if ((this.outputBufferInfo.flags & 0x4) != 0)
    {
      processEndOfStream();
      return false;
    }
    int i = getDecodeOnlyIndex(this.outputBufferInfo.presentationTimeUs);
    Object localObject = this.codec;
    ByteBuffer localByteBuffer = this.outputBuffers[this.outputIndex];
    MediaCodec.BufferInfo localBufferInfo = this.outputBufferInfo;
    int j = this.outputIndex;
    if (i != -1) {}
    for (boolean bool = true; processOutputBuffer(paramLong1, paramLong2, (MediaCodec)localObject, localByteBuffer, localBufferInfo, j, bool); bool = false)
    {
      onProcessedOutputBuffer(this.outputBufferInfo.presentationTimeUs);
      if (i != -1) {
        this.decodeOnlyPresentationTimestamps.remove(i);
      }
      this.outputIndex = -1;
      return true;
    }
    return false;
  }
  
  private boolean feedInputBuffer(long paramLong, boolean paramBoolean)
    throws ExoPlaybackException
  {
    if ((this.inputStreamEnded) || (this.codecReinitializationState == 2)) {
      return false;
    }
    if (this.inputIndex < 0)
    {
      this.inputIndex = this.codec.dequeueInputBuffer(0L);
      if (this.inputIndex < 0) {
        return false;
      }
      this.sampleHolder.data = this.inputBuffers[this.inputIndex];
      this.sampleHolder.clearData();
    }
    if (this.codecReinitializationState == 1)
    {
      if (this.codecNeedsEosPropagationWorkaround) {}
      for (;;)
      {
        this.codecReinitializationState = 2;
        return false;
        this.codecReceivedEos = true;
        this.codec.queueInputBuffer(this.inputIndex, 0, 0, 0L, 4);
        this.inputIndex = -1;
      }
    }
    if (this.codecNeedsAdaptationWorkaroundBuffer)
    {
      this.codecNeedsAdaptationWorkaroundBuffer = false;
      this.sampleHolder.data.put(ADAPTATION_WORKAROUND_BUFFER);
      this.codec.queueInputBuffer(this.inputIndex, 0, ADAPTATION_WORKAROUND_BUFFER.length, 0L, 0);
      this.inputIndex = -1;
      this.codecReceivedBuffers = true;
      return true;
    }
    int i;
    if (this.waitingForKeys) {
      i = -3;
    }
    int j;
    while (i == -2)
    {
      return false;
      if (this.codecReconfigurationState == 1)
      {
        i = 0;
        while (i < this.format.initializationData.size())
        {
          byte[] arrayOfByte = (byte[])this.format.initializationData.get(i);
          this.sampleHolder.data.put(arrayOfByte);
          i += 1;
        }
        this.codecReconfigurationState = 2;
      }
      j = readSource(paramLong, this.formatHolder, this.sampleHolder);
      i = j;
      if (paramBoolean)
      {
        i = j;
        if (this.sourceState == 1)
        {
          i = j;
          if (j == -2)
          {
            this.sourceState = 2;
            i = j;
          }
        }
      }
    }
    if (i == -4)
    {
      if (this.codecReconfigurationState == 2)
      {
        this.sampleHolder.clearData();
        this.codecReconfigurationState = 1;
      }
      onInputFormatChanged(this.formatHolder);
      return true;
    }
    if (i == -1)
    {
      if (this.codecReconfigurationState == 2)
      {
        this.sampleHolder.clearData();
        this.codecReconfigurationState = 1;
      }
      this.inputStreamEnded = true;
      if (!this.codecReceivedBuffers)
      {
        processEndOfStream();
        return false;
      }
      try
      {
        if (this.codecNeedsEosPropagationWorkaround) {
          break label748;
        }
        this.codecReceivedEos = true;
        this.codec.queueInputBuffer(this.inputIndex, 0, 0, 0L, 4);
        this.inputIndex = -1;
      }
      catch (MediaCodec.CryptoException localCryptoException1)
      {
        notifyCryptoError(localCryptoException1);
        throw new ExoPlaybackException(localCryptoException1);
      }
    }
    if (this.waitingForFirstSyncFrame)
    {
      if (!this.sampleHolder.isSyncFrame())
      {
        this.sampleHolder.clearData();
        if (this.codecReconfigurationState == 2) {
          this.codecReconfigurationState = 1;
        }
        return true;
      }
      this.waitingForFirstSyncFrame = false;
    }
    paramBoolean = this.sampleHolder.isEncrypted();
    this.waitingForKeys = shouldWaitForKeys(paramBoolean);
    if (this.waitingForKeys) {
      return false;
    }
    if ((this.codecNeedsDiscardToSpsWorkaround) && (!paramBoolean))
    {
      NalUnitUtil.discardToSps(this.sampleHolder.data);
      if (this.sampleHolder.data.position() == 0) {
        return true;
      }
      this.codecNeedsDiscardToSpsWorkaround = false;
    }
    try
    {
      i = this.sampleHolder.data.position();
      j = this.sampleHolder.size;
      paramLong = this.sampleHolder.timeUs;
      if (this.sampleHolder.isDecodeOnly()) {
        this.decodeOnlyPresentationTimestamps.add(Long.valueOf(paramLong));
      }
      onQueuedInputBuffer(paramLong, this.sampleHolder.data, i, paramBoolean);
      Object localObject;
      if (paramBoolean)
      {
        localObject = getFrameworkCryptoInfo(this.sampleHolder, i - j);
        this.codec.queueSecureInputBuffer(this.inputIndex, 0, (MediaCodec.CryptoInfo)localObject, paramLong, 0);
      }
      for (;;)
      {
        this.inputIndex = -1;
        this.codecReceivedBuffers = true;
        this.codecReconfigurationState = 0;
        localObject = this.codecCounters;
        ((CodecCounters)localObject).inputBufferCount += 1;
        return true;
        this.codec.queueInputBuffer(this.inputIndex, 0, i, paramLong, 0);
      }
      return false;
    }
    catch (MediaCodec.CryptoException localCryptoException2)
    {
      notifyCryptoError(localCryptoException2);
      throw new ExoPlaybackException(localCryptoException2);
    }
  }
  
  private int getDecodeOnlyIndex(long paramLong)
  {
    int j = this.decodeOnlyPresentationTimestamps.size();
    int i = 0;
    while (i < j)
    {
      if (((Long)this.decodeOnlyPresentationTimestamps.get(i)).longValue() == paramLong) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  private static MediaCodec.CryptoInfo getFrameworkCryptoInfo(SampleHolder paramSampleHolder, int paramInt)
  {
    paramSampleHolder = paramSampleHolder.cryptoInfo.getFrameworkCryptoInfoV16();
    if (paramInt == 0) {
      return paramSampleHolder;
    }
    if (paramSampleHolder.numBytesOfClearData == null) {
      paramSampleHolder.numBytesOfClearData = new int[1];
    }
    int[] arrayOfInt = paramSampleHolder.numBytesOfClearData;
    arrayOfInt[0] += paramInt;
    return paramSampleHolder;
  }
  
  private android.media.MediaFormat getFrameworkMediaFormat(MediaFormat paramMediaFormat)
  {
    paramMediaFormat = paramMediaFormat.getFrameworkMediaFormatV16();
    if (this.deviceNeedsAutoFrcWorkaround) {
      paramMediaFormat.setInteger("auto-frc", 0);
    }
    return paramMediaFormat;
  }
  
  private boolean isWithinHotswapPeriod()
  {
    return SystemClock.elapsedRealtime() < this.codecHotswapTimeMs + 1000L;
  }
  
  private void notifyAndThrowDecoderInitError(DecoderInitializationException paramDecoderInitializationException)
    throws ExoPlaybackException
  {
    notifyDecoderInitializationError(paramDecoderInitializationException);
    throw new ExoPlaybackException(paramDecoderInitializationException);
  }
  
  private void notifyCryptoError(final MediaCodec.CryptoException paramCryptoException)
  {
    if ((this.eventHandler != null) && (this.eventListener != null)) {
      this.eventHandler.post(new Runnable()
      {
        public void run()
        {
          MediaCodecTrackRenderer.this.eventListener.onCryptoError(paramCryptoException);
        }
      });
    }
  }
  
  private void notifyDecoderInitializationError(final DecoderInitializationException paramDecoderInitializationException)
  {
    if ((this.eventHandler != null) && (this.eventListener != null)) {
      this.eventHandler.post(new Runnable()
      {
        public void run()
        {
          MediaCodecTrackRenderer.this.eventListener.onDecoderInitializationError(paramDecoderInitializationException);
        }
      });
    }
  }
  
  private void notifyDecoderInitialized(final String paramString, final long paramLong1, long paramLong2)
  {
    if ((this.eventHandler != null) && (this.eventListener != null)) {
      this.eventHandler.post(new Runnable()
      {
        public void run()
        {
          MediaCodecTrackRenderer.this.eventListener.onDecoderInitialized(paramString, paramLong1, this.val$initializationDuration);
        }
      });
    }
  }
  
  private void processEndOfStream()
    throws ExoPlaybackException
  {
    if (this.codecReinitializationState == 2)
    {
      releaseCodec();
      maybeInitCodec();
      return;
    }
    this.outputStreamEnded = true;
    onOutputStreamEnded();
  }
  
  private void processOutputFormat()
    throws ExoPlaybackException
  {
    Object localObject = this.codec.getOutputFormat();
    if ((this.codecNeedsAdaptationWorkaround) && (((android.media.MediaFormat)localObject).getInteger("width") == 32) && (((android.media.MediaFormat)localObject).getInteger("height") == 32))
    {
      this.shouldSkipAdaptationWorkaroundOutputBuffer = true;
      return;
    }
    if (this.codecNeedsMonoChannelCountWorkaround) {
      ((android.media.MediaFormat)localObject).setInteger("channel-count", 1);
    }
    onOutputFormatChanged(this.codec, (android.media.MediaFormat)localObject);
    localObject = this.codecCounters;
    ((CodecCounters)localObject).outputFormatChangedCount += 1;
  }
  
  private void readFormat(long paramLong)
    throws ExoPlaybackException
  {
    if (readSource(paramLong, this.formatHolder, null) == -4) {
      onInputFormatChanged(this.formatHolder);
    }
  }
  
  private boolean shouldWaitForKeys(boolean paramBoolean)
    throws ExoPlaybackException
  {
    if (!this.openedDrmSession) {}
    int i;
    do
    {
      return false;
      i = this.drmSessionManager.getState();
      if (i == 0) {
        throw new ExoPlaybackException(this.drmSessionManager.getError());
      }
    } while ((i == 4) || ((!paramBoolean) && (this.playClearSamplesWithoutKeys)));
    return true;
  }
  
  protected boolean canReconfigureCodec(MediaCodec paramMediaCodec, boolean paramBoolean, MediaFormat paramMediaFormat1, MediaFormat paramMediaFormat2)
  {
    return false;
  }
  
  protected final boolean codecInitialized()
  {
    return this.codec != null;
  }
  
  protected abstract void configureCodec(MediaCodec paramMediaCodec, boolean paramBoolean, android.media.MediaFormat paramMediaFormat, MediaCrypto paramMediaCrypto);
  
  protected void doSomeWork(long paramLong1, long paramLong2, boolean paramBoolean)
    throws ExoPlaybackException
  {
    int i;
    if (paramBoolean) {
      if (this.sourceState == 0) {
        i = 1;
      }
    }
    for (;;)
    {
      this.sourceState = i;
      if (this.format == null) {
        readFormat(paramLong1);
      }
      maybeInitCodec();
      if (this.codec != null)
      {
        TraceUtil.beginSection("drainAndFeed");
        while (drainOutputBuffer(paramLong1, paramLong2)) {}
        while ((feedInputBuffer(paramLong1, true)) && (feedInputBuffer(paramLong1, false))) {}
        TraceUtil.endSection();
      }
      this.codecCounters.ensureUpdated();
      return;
      i = this.sourceState;
      continue;
      i = 0;
    }
  }
  
  protected void flushCodec()
    throws ExoPlaybackException
  {
    this.codecHotswapTimeMs = -1L;
    this.inputIndex = -1;
    this.outputIndex = -1;
    this.waitingForFirstSyncFrame = true;
    this.waitingForKeys = false;
    this.decodeOnlyPresentationTimestamps.clear();
    this.codecNeedsAdaptationWorkaroundBuffer = false;
    this.shouldSkipAdaptationWorkaroundOutputBuffer = false;
    if ((this.codecNeedsFlushWorkaround) || ((this.codecNeedsEosFlushWorkaround) && (this.codecReceivedEos)))
    {
      releaseCodec();
      maybeInitCodec();
    }
    for (;;)
    {
      if ((this.codecReconfigured) && (this.format != null)) {
        this.codecReconfigurationState = 1;
      }
      return;
      if (this.codecReinitializationState != 0)
      {
        releaseCodec();
        maybeInitCodec();
      }
      else
      {
        this.codec.flush();
        this.codecReceivedBuffers = false;
      }
    }
  }
  
  protected DecoderInfo getDecoderInfo(MediaCodecSelector paramMediaCodecSelector, String paramString, boolean paramBoolean)
    throws MediaCodecUtil.DecoderQueryException
  {
    return paramMediaCodecSelector.getDecoderInfo(paramString, paramBoolean);
  }
  
  protected long getDequeueOutputBufferTimeoutUs()
  {
    return 0L;
  }
  
  protected final int getSourceState()
  {
    return this.sourceState;
  }
  
  protected abstract boolean handlesTrack(MediaCodecSelector paramMediaCodecSelector, MediaFormat paramMediaFormat)
    throws MediaCodecUtil.DecoderQueryException;
  
  protected final boolean handlesTrack(MediaFormat paramMediaFormat)
    throws MediaCodecUtil.DecoderQueryException
  {
    return handlesTrack(this.mediaCodecSelector, paramMediaFormat);
  }
  
  protected final boolean haveFormat()
  {
    return this.format != null;
  }
  
  protected boolean isEnded()
  {
    return this.outputStreamEnded;
  }
  
  protected boolean isReady()
  {
    return (this.format != null) && (!this.waitingForKeys) && ((this.sourceState != 0) || (this.outputIndex >= 0) || (isWithinHotswapPeriod()));
  }
  
  protected final void maybeInitCodec()
    throws ExoPlaybackException
  {
    if (!shouldInitCodec()) {}
    int i;
    do
    {
      return;
      localObject3 = this.format.mimeType;
      localObject1 = null;
      bool = false;
      if (this.drmInitData == null) {
        break;
      }
      if (this.drmSessionManager == null) {
        throw new ExoPlaybackException("Media requires a DrmSessionManager");
      }
      if (!this.openedDrmSession)
      {
        this.drmSessionManager.open(this.drmInitData);
        this.openedDrmSession = true;
      }
      i = this.drmSessionManager.getState();
      if (i == 0) {
        throw new ExoPlaybackException(this.drmSessionManager.getError());
      }
    } while ((i != 3) && (i != 4));
    localObject1 = ((FrameworkMediaCrypto)this.drmSessionManager.getMediaCrypto()).getWrappedMediaCrypto();
    bool = this.drmSessionManager.requiresSecureDecoderComponent((String)localObject3);
    localObject2 = null;
    try
    {
      localObject3 = getDecoderInfo(this.mediaCodecSelector, (String)localObject3, bool);
      localObject2 = localObject3;
    }
    catch (MediaCodecUtil.DecoderQueryException localDecoderQueryException)
    {
      try
      {
        l1 = SystemClock.elapsedRealtime();
        TraceUtil.beginSection("createByCodecName(" + (String)localObject3 + ")");
        this.codec = MediaCodec.createByCodecName((String)localObject3);
        TraceUtil.endSection();
        TraceUtil.beginSection("configureCodec");
        configureCodec(this.codec, ((DecoderInfo)localObject2).adaptive, getFrameworkMediaFormat(this.format), (MediaCrypto)localObject1);
        TraceUtil.endSection();
        TraceUtil.beginSection("codec.start()");
        this.codec.start();
        TraceUtil.endSection();
        long l2 = SystemClock.elapsedRealtime();
        notifyDecoderInitialized((String)localObject3, l2, l2 - l1);
        this.inputBuffers = this.codec.getInputBuffers();
        this.outputBuffers = this.codec.getOutputBuffers();
        if (getState() != 3) {
          break label500;
        }
        l1 = SystemClock.elapsedRealtime();
        this.codecHotswapTimeMs = l1;
        this.inputIndex = -1;
        this.outputIndex = -1;
        this.waitingForFirstSyncFrame = true;
        localObject1 = this.codecCounters;
        ((CodecCounters)localObject1).codecInitCount += 1;
        return;
        localDecoderQueryException = localDecoderQueryException;
        notifyAndThrowDecoderInitError(new DecoderInitializationException(this.format, localDecoderQueryException, bool, -49998));
      }
      catch (Exception localException)
      {
        for (;;)
        {
          notifyAndThrowDecoderInitError(new DecoderInitializationException(this.format, localException, bool, localDecoderQueryException));
          continue;
          long l1 = -1L;
        }
      }
    }
    if (localObject2 == null) {
      notifyAndThrowDecoderInitError(new DecoderInitializationException(this.format, null, bool, -49999));
    }
    localObject3 = ((DecoderInfo)localObject2).name;
    this.codecIsAdaptive = ((DecoderInfo)localObject2).adaptive;
    this.codecNeedsDiscardToSpsWorkaround = codecNeedsDiscardToSpsWorkaround((String)localObject3, this.format);
    this.codecNeedsFlushWorkaround = codecNeedsFlushWorkaround((String)localObject3);
    this.codecNeedsAdaptationWorkaround = codecNeedsAdaptationWorkaround((String)localObject3);
    this.codecNeedsEosPropagationWorkaround = codecNeedsEosPropagationWorkaround((String)localObject3);
    this.codecNeedsEosFlushWorkaround = codecNeedsEosFlushWorkaround((String)localObject3);
    this.codecNeedsMonoChannelCountWorkaround = codecNeedsMonoChannelCountWorkaround((String)localObject3, this.format);
  }
  
  /* Error */
  protected void onDisabled()
    throws ExoPlaybackException
  {
    // Byte code:
    //   0: aload_0
    //   1: aconst_null
    //   2: putfield 361	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:format	Lorg/telegram/messenger/exoplayer/MediaFormat;
    //   5: aload_0
    //   6: aconst_null
    //   7: putfield 619	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:drmInitData	Lorg/telegram/messenger/exoplayer/drm/DrmInitData;
    //   10: aload_0
    //   11: invokevirtual 507	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:releaseCodec	()V
    //   14: aload_0
    //   15: getfield 540	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:openedDrmSession	Z
    //   18: ifeq +17 -> 35
    //   21: aload_0
    //   22: getfield 139	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:drmSessionManager	Lorg/telegram/messenger/exoplayer/drm/DrmSessionManager;
    //   25: invokeinterface 716 1 0
    //   30: aload_0
    //   31: iconst_0
    //   32: putfield 540	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:openedDrmSession	Z
    //   35: aload_0
    //   36: invokespecial 718	org/telegram/messenger/exoplayer/SampleSourceTrackRenderer:onDisabled	()V
    //   39: return
    //   40: astore_1
    //   41: aload_0
    //   42: invokespecial 718	org/telegram/messenger/exoplayer/SampleSourceTrackRenderer:onDisabled	()V
    //   45: aload_1
    //   46: athrow
    //   47: astore_1
    //   48: aload_0
    //   49: getfield 540	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:openedDrmSession	Z
    //   52: ifeq +17 -> 69
    //   55: aload_0
    //   56: getfield 139	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:drmSessionManager	Lorg/telegram/messenger/exoplayer/drm/DrmSessionManager;
    //   59: invokeinterface 716 1 0
    //   64: aload_0
    //   65: iconst_0
    //   66: putfield 540	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:openedDrmSession	Z
    //   69: aload_0
    //   70: invokespecial 718	org/telegram/messenger/exoplayer/SampleSourceTrackRenderer:onDisabled	()V
    //   73: aload_1
    //   74: athrow
    //   75: astore_1
    //   76: aload_0
    //   77: invokespecial 718	org/telegram/messenger/exoplayer/SampleSourceTrackRenderer:onDisabled	()V
    //   80: aload_1
    //   81: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	82	0	this	MediaCodecTrackRenderer
    //   40	6	1	localObject1	Object
    //   47	27	1	localObject2	Object
    //   75	6	1	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   14	35	40	finally
    //   10	14	47	finally
    //   48	69	75	finally
  }
  
  protected void onDiscontinuity(long paramLong)
    throws ExoPlaybackException
  {
    this.sourceState = 0;
    this.inputStreamEnded = false;
    this.outputStreamEnded = false;
    if (this.codec != null) {
      flushCodec();
    }
  }
  
  protected void onInputFormatChanged(MediaFormatHolder paramMediaFormatHolder)
    throws ExoPlaybackException
  {
    boolean bool = true;
    MediaFormat localMediaFormat = this.format;
    this.format = paramMediaFormatHolder.format;
    this.drmInitData = paramMediaFormatHolder.drmInitData;
    if ((this.codec != null) && (canReconfigureCodec(this.codec, this.codecIsAdaptive, localMediaFormat, this.format)))
    {
      this.codecReconfigured = true;
      this.codecReconfigurationState = 1;
      if ((this.codecNeedsAdaptationWorkaround) && (this.format.width == localMediaFormat.width) && (this.format.height == localMediaFormat.height)) {}
      for (;;)
      {
        this.codecNeedsAdaptationWorkaroundBuffer = bool;
        return;
        bool = false;
      }
    }
    if (this.codecReceivedBuffers)
    {
      this.codecReinitializationState = 1;
      return;
    }
    releaseCodec();
    maybeInitCodec();
  }
  
  protected void onOutputFormatChanged(MediaCodec paramMediaCodec, android.media.MediaFormat paramMediaFormat)
    throws ExoPlaybackException
  {}
  
  protected void onOutputStreamEnded() {}
  
  protected void onProcessedOutputBuffer(long paramLong) {}
  
  protected void onQueuedInputBuffer(long paramLong, ByteBuffer paramByteBuffer, int paramInt, boolean paramBoolean) {}
  
  protected void onStarted() {}
  
  protected void onStopped() {}
  
  protected abstract boolean processOutputBuffer(long paramLong1, long paramLong2, MediaCodec paramMediaCodec, ByteBuffer paramByteBuffer, MediaCodec.BufferInfo paramBufferInfo, int paramInt, boolean paramBoolean)
    throws ExoPlaybackException;
  
  /* Error */
  protected void releaseCodec()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 265	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:codec	Landroid/media/MediaCodec;
    //   4: ifnull +148 -> 152
    //   7: aload_0
    //   8: ldc2_w 578
    //   11: putfield 482	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:codecHotswapTimeMs	J
    //   14: aload_0
    //   15: iconst_m1
    //   16: putfield 329	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:inputIndex	I
    //   19: aload_0
    //   20: iconst_m1
    //   21: putfield 263	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:outputIndex	I
    //   24: aload_0
    //   25: iconst_0
    //   26: putfield 359	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:waitingForKeys	Z
    //   29: aload_0
    //   30: getfield 173	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:decodeOnlyPresentationTimestamps	Ljava/util/List;
    //   33: invokeinterface 582 1 0
    //   38: aload_0
    //   39: aconst_null
    //   40: putfield 334	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:inputBuffers	[Ljava/nio/ByteBuffer;
    //   43: aload_0
    //   44: aconst_null
    //   45: putfield 284	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:outputBuffers	[Ljava/nio/ByteBuffer;
    //   48: aload_0
    //   49: iconst_0
    //   50: putfield 588	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:codecReconfigured	Z
    //   53: aload_0
    //   54: iconst_0
    //   55: putfield 357	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:codecReceivedBuffers	Z
    //   58: aload_0
    //   59: iconst_0
    //   60: putfield 658	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:codecIsAdaptive	Z
    //   63: aload_0
    //   64: iconst_0
    //   65: putfield 400	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:codecNeedsDiscardToSpsWorkaround	Z
    //   68: aload_0
    //   69: iconst_0
    //   70: putfield 584	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:codecNeedsFlushWorkaround	Z
    //   73: aload_0
    //   74: iconst_0
    //   75: putfield 518	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:codecNeedsAdaptationWorkaround	Z
    //   78: aload_0
    //   79: iconst_0
    //   80: putfield 289	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:codecNeedsEosPropagationWorkaround	Z
    //   83: aload_0
    //   84: iconst_0
    //   85: putfield 586	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:codecNeedsEosFlushWorkaround	Z
    //   88: aload_0
    //   89: iconst_0
    //   90: putfield 528	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:codecNeedsMonoChannelCountWorkaround	Z
    //   93: aload_0
    //   94: iconst_0
    //   95: putfield 349	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:codecNeedsAdaptationWorkaroundBuffer	Z
    //   98: aload_0
    //   99: iconst_0
    //   100: putfield 296	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:shouldSkipAdaptationWorkaroundOutputBuffer	Z
    //   103: aload_0
    //   104: iconst_0
    //   105: putfield 343	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:codecReceivedEos	Z
    //   108: aload_0
    //   109: iconst_0
    //   110: putfield 180	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:codecReconfigurationState	I
    //   113: aload_0
    //   114: iconst_0
    //   115: putfield 182	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:codecReinitializationState	I
    //   118: aload_0
    //   119: getfield 156	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:codecCounters	Lorg/telegram/messenger/exoplayer/CodecCounters;
    //   122: astore_1
    //   123: aload_1
    //   124: aload_1
    //   125: getfield 734	org/telegram/messenger/exoplayer/CodecCounters:codecReleaseCount	I
    //   128: iconst_1
    //   129: iadd
    //   130: putfield 734	org/telegram/messenger/exoplayer/CodecCounters:codecReleaseCount	I
    //   133: aload_0
    //   134: getfield 265	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:codec	Landroid/media/MediaCodec;
    //   137: invokevirtual 737	android/media/MediaCodec:stop	()V
    //   140: aload_0
    //   141: getfield 265	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:codec	Landroid/media/MediaCodec;
    //   144: invokevirtual 740	android/media/MediaCodec:release	()V
    //   147: aload_0
    //   148: aconst_null
    //   149: putfield 265	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:codec	Landroid/media/MediaCodec;
    //   152: return
    //   153: astore_1
    //   154: aload_0
    //   155: aconst_null
    //   156: putfield 265	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:codec	Landroid/media/MediaCodec;
    //   159: aload_1
    //   160: athrow
    //   161: astore_1
    //   162: aload_0
    //   163: getfield 265	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:codec	Landroid/media/MediaCodec;
    //   166: invokevirtual 740	android/media/MediaCodec:release	()V
    //   169: aload_0
    //   170: aconst_null
    //   171: putfield 265	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:codec	Landroid/media/MediaCodec;
    //   174: aload_1
    //   175: athrow
    //   176: astore_1
    //   177: aload_0
    //   178: aconst_null
    //   179: putfield 265	org/telegram/messenger/exoplayer/MediaCodecTrackRenderer:codec	Landroid/media/MediaCodec;
    //   182: aload_1
    //   183: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	184	0	this	MediaCodecTrackRenderer
    //   122	3	1	localCodecCounters	CodecCounters
    //   153	7	1	localObject1	Object
    //   161	14	1	localObject2	Object
    //   176	7	1	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   140	147	153	finally
    //   133	140	161	finally
    //   162	169	176	finally
  }
  
  protected boolean shouldInitCodec()
  {
    return (this.codec == null) && (this.format != null);
  }
  
  public static class DecoderInitializationException
    extends Exception
  {
    private static final int CUSTOM_ERROR_CODE_BASE = -50000;
    private static final int DECODER_QUERY_ERROR = -49998;
    private static final int NO_SUITABLE_DECODER_ERROR = -49999;
    public final String decoderName;
    public final String diagnosticInfo;
    public final String mimeType;
    public final boolean secureDecoderRequired;
    
    public DecoderInitializationException(MediaFormat paramMediaFormat, Throwable paramThrowable, boolean paramBoolean, int paramInt)
    {
      super(paramThrowable);
      this.mimeType = paramMediaFormat.mimeType;
      this.secureDecoderRequired = paramBoolean;
      this.decoderName = null;
      this.diagnosticInfo = buildCustomDiagnosticInfo(paramInt);
    }
    
    public DecoderInitializationException(MediaFormat paramMediaFormat, Throwable paramThrowable, boolean paramBoolean, String paramString)
    {
      super(paramThrowable);
      this.mimeType = paramMediaFormat.mimeType;
      this.secureDecoderRequired = paramBoolean;
      this.decoderName = paramString;
      if (Util.SDK_INT >= 21) {}
      for (paramMediaFormat = getDiagnosticInfoV21(paramThrowable);; paramMediaFormat = null)
      {
        this.diagnosticInfo = paramMediaFormat;
        return;
      }
    }
    
    private static String buildCustomDiagnosticInfo(int paramInt)
    {
      if (paramInt < 0) {}
      for (String str = "neg_";; str = "") {
        return "org.telegram.messenger.exoplayer.MediaCodecTrackRenderer_" + str + Math.abs(paramInt);
      }
    }
    
    @TargetApi(21)
    private static String getDiagnosticInfoV21(Throwable paramThrowable)
    {
      if ((paramThrowable instanceof MediaCodec.CodecException)) {
        return ((MediaCodec.CodecException)paramThrowable).getDiagnosticInfo();
      }
      return null;
    }
  }
  
  public static abstract interface EventListener
  {
    public abstract void onCryptoError(MediaCodec.CryptoException paramCryptoException);
    
    public abstract void onDecoderInitializationError(MediaCodecTrackRenderer.DecoderInitializationException paramDecoderInitializationException);
    
    public abstract void onDecoderInitialized(String paramString, long paramLong1, long paramLong2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/MediaCodecTrackRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */