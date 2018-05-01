package org.telegram.messenger.exoplayer2.mediacodec;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodec.CodecException;
import android.media.MediaCodec.CryptoInfo;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer2.BaseRenderer;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.decoder.CryptoInfo;
import org.telegram.messenger.exoplayer2.decoder.DecoderCounters;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;
import org.telegram.messenger.exoplayer2.drm.DrmSession;
import org.telegram.messenger.exoplayer2.drm.DrmSessionManager;
import org.telegram.messenger.exoplayer2.drm.FrameworkMediaCrypto;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.TraceUtil;
import org.telegram.messenger.exoplayer2.util.Util;

@TargetApi(16)
public abstract class MediaCodecRenderer
  extends BaseRenderer
{
  private static final byte[] ADAPTATION_WORKAROUND_BUFFER = Util.getBytesFromHexString("0000016742C00BDA259000000168CE0F13200000016588840DCE7118A0002FBF1C31C3275D78");
  private static final int ADAPTATION_WORKAROUND_MODE_ALWAYS = 2;
  private static final int ADAPTATION_WORKAROUND_MODE_NEVER = 0;
  private static final int ADAPTATION_WORKAROUND_MODE_SAME_RESOLUTION = 1;
  private static final int ADAPTATION_WORKAROUND_SLICE_WIDTH_HEIGHT = 32;
  private static final long MAX_CODEC_HOTSWAP_TIME_MS = 1000L;
  private static final int RECONFIGURATION_STATE_NONE = 0;
  private static final int RECONFIGURATION_STATE_QUEUE_PENDING = 2;
  private static final int RECONFIGURATION_STATE_WRITE_PENDING = 1;
  private static final int REINITIALIZATION_STATE_NONE = 0;
  private static final int REINITIALIZATION_STATE_SIGNAL_END_OF_STREAM = 1;
  private static final int REINITIALIZATION_STATE_WAIT_END_OF_STREAM = 2;
  private static final String TAG = "MediaCodecRenderer";
  private final DecoderInputBuffer buffer;
  private MediaCodec codec;
  private int codecAdaptationWorkaroundMode;
  private long codecHotswapDeadlineMs;
  private MediaCodecInfo codecInfo;
  private boolean codecNeedsAdaptationWorkaroundBuffer;
  private boolean codecNeedsDiscardToSpsWorkaround;
  private boolean codecNeedsEosFlushWorkaround;
  private boolean codecNeedsEosOutputExceptionWorkaround;
  private boolean codecNeedsEosPropagationWorkaround;
  private boolean codecNeedsFlushWorkaround;
  private boolean codecNeedsMonoChannelCountWorkaround;
  private boolean codecReceivedBuffers;
  private boolean codecReceivedEos;
  private int codecReconfigurationState;
  private boolean codecReconfigured;
  private int codecReinitializationState;
  private final List<Long> decodeOnlyPresentationTimestamps;
  protected DecoderCounters decoderCounters;
  private DrmSession<FrameworkMediaCrypto> drmSession;
  private final DrmSessionManager<FrameworkMediaCrypto> drmSessionManager;
  private final DecoderInputBuffer flagsOnlyBuffer;
  private Format format;
  private final FormatHolder formatHolder;
  private ByteBuffer[] inputBuffers;
  private int inputIndex;
  private boolean inputStreamEnded;
  private final MediaCodecSelector mediaCodecSelector;
  private ByteBuffer outputBuffer;
  private final MediaCodec.BufferInfo outputBufferInfo;
  private ByteBuffer[] outputBuffers;
  private int outputIndex;
  private boolean outputStreamEnded;
  private DrmSession<FrameworkMediaCrypto> pendingDrmSession;
  private final boolean playClearSamplesWithoutKeys;
  private boolean shouldSkipAdaptationWorkaroundOutputBuffer;
  private boolean shouldSkipOutputBuffer;
  private boolean waitingForFirstSyncFrame;
  private boolean waitingForKeys;
  
  public MediaCodecRenderer(int paramInt, MediaCodecSelector paramMediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, boolean paramBoolean)
  {
    super(paramInt);
    if (Util.SDK_INT >= 16) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.mediaCodecSelector = ((MediaCodecSelector)Assertions.checkNotNull(paramMediaCodecSelector));
      this.drmSessionManager = paramDrmSessionManager;
      this.playClearSamplesWithoutKeys = paramBoolean;
      this.buffer = new DecoderInputBuffer(0);
      this.flagsOnlyBuffer = DecoderInputBuffer.newFlagsOnlyInstance();
      this.formatHolder = new FormatHolder();
      this.decodeOnlyPresentationTimestamps = new ArrayList();
      this.outputBufferInfo = new MediaCodec.BufferInfo();
      this.codecReconfigurationState = 0;
      this.codecReinitializationState = 0;
      return;
    }
  }
  
  private int codecAdaptationWorkaroundMode(String paramString)
  {
    int i;
    if ((Util.SDK_INT <= 25) && ("OMX.Exynos.avc.dec.secure".equals(paramString)) && ((Util.MODEL.startsWith("SM-T585")) || (Util.MODEL.startsWith("SM-A510")) || (Util.MODEL.startsWith("SM-A520")) || (Util.MODEL.startsWith("SM-J700")))) {
      i = 2;
    }
    for (;;)
    {
      return i;
      if ((Util.SDK_INT < 24) && (("OMX.Nvidia.h264.decode".equals(paramString)) || ("OMX.Nvidia.h264.decode.secure".equals(paramString))) && (("flounder".equals(Util.DEVICE)) || ("flounder_lte".equals(Util.DEVICE)) || ("grouper".equals(Util.DEVICE)) || ("tilapia".equals(Util.DEVICE)))) {
        i = 1;
      } else {
        i = 0;
      }
    }
  }
  
  private static boolean codecNeedsDiscardToSpsWorkaround(String paramString, Format paramFormat)
  {
    if ((Util.SDK_INT < 21) && (paramFormat.initializationData.isEmpty()) && ("OMX.MTK.VIDEO.DECODER.AVC".equals(paramString))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private static boolean codecNeedsEosFlushWorkaround(String paramString)
  {
    if (((Util.SDK_INT <= 23) && ("OMX.google.vorbis.decoder".equals(paramString))) || ((Util.SDK_INT <= 19) && ("hb2000".equals(Util.DEVICE)) && (("OMX.amlogic.avc.decoder.awesome".equals(paramString)) || ("OMX.amlogic.avc.decoder.awesome.secure".equals(paramString))))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private static boolean codecNeedsEosOutputExceptionWorkaround(String paramString)
  {
    if ((Util.SDK_INT == 21) && ("OMX.google.aac.decoder".equals(paramString))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private static boolean codecNeedsEosPropagationWorkaround(String paramString)
  {
    if ((Util.SDK_INT <= 17) && (("OMX.rk.video_decoder.avc".equals(paramString)) || ("OMX.allwinner.video.decoder.avc".equals(paramString)))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private static boolean codecNeedsFlushWorkaround(String paramString)
  {
    if ((Util.SDK_INT < 18) || ((Util.SDK_INT == 18) && (("OMX.SEC.avc.dec".equals(paramString)) || ("OMX.SEC.avc.dec.secure".equals(paramString)))) || ((Util.SDK_INT == 19) && (Util.MODEL.startsWith("SM-G800")) && (("OMX.Exynos.avc.dec".equals(paramString)) || ("OMX.Exynos.avc.dec.secure".equals(paramString))))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private static boolean codecNeedsMonoChannelCountWorkaround(String paramString, Format paramFormat)
  {
    boolean bool = true;
    if ((Util.SDK_INT <= 18) && (paramFormat.channelCount == 1) && ("OMX.MTK.AUDIO.DECODER.MP3".equals(paramString))) {}
    for (;;)
    {
      return bool;
      bool = false;
    }
  }
  
  @TargetApi(23)
  private static void configureMediaFormatForPlaybackV23(MediaFormat paramMediaFormat)
  {
    paramMediaFormat.setInteger("priority", 0);
  }
  
  private boolean drainOutputBuffer(long paramLong1, long paramLong2)
    throws ExoPlaybackException
  {
    if (!hasOutputBuffer()) {
      if ((!this.codecNeedsEosOutputExceptionWorkaround) || (!this.codecReceivedEos)) {}
    }
    for (;;)
    {
      boolean bool;
      try
      {
        i = this.codec.dequeueOutputBuffer(this.outputBufferInfo, getDequeueOutputBufferTimeoutUs());
        if (i < 0) {
          break label290;
        }
        if (!this.shouldSkipAdaptationWorkaroundOutputBuffer) {
          break label114;
        }
        this.shouldSkipAdaptationWorkaroundOutputBuffer = false;
        this.codec.releaseOutputBuffer(i, false);
        bool = true;
      }
      catch (IllegalStateException localIllegalStateException1)
      {
        processEndOfStream();
        if (!this.outputStreamEnded) {
          continue;
        }
        releaseCodec();
        bool = false;
        continue;
      }
      return bool;
      int i = this.codec.dequeueOutputBuffer(this.outputBufferInfo, getDequeueOutputBufferTimeoutUs());
      continue;
      label114:
      if ((this.outputBufferInfo.flags & 0x4) != 0)
      {
        processEndOfStream();
        bool = false;
      }
      else
      {
        this.outputIndex = i;
        this.outputBuffer = getOutputBuffer(i);
        if (this.outputBuffer != null)
        {
          this.outputBuffer.position(this.outputBufferInfo.offset);
          this.outputBuffer.limit(this.outputBufferInfo.offset + this.outputBufferInfo.size);
        }
        this.shouldSkipOutputBuffer = shouldSkipOutputBuffer(this.outputBufferInfo.presentationTimeUs);
        if ((this.codecNeedsEosOutputExceptionWorkaround) && (this.codecReceivedEos)) {}
        for (;;)
        {
          try
          {
            bool = processOutputBuffer(paramLong1, paramLong2, this.codec, this.outputBuffer, this.outputIndex, this.outputBufferInfo.flags, this.outputBufferInfo.presentationTimeUs, this.shouldSkipOutputBuffer);
            if (!bool) {
              break label420;
            }
            onProcessedOutputBuffer(this.outputBufferInfo.presentationTimeUs);
            resetOutputBuffer();
            bool = true;
          }
          catch (IllegalStateException localIllegalStateException2)
          {
            label290:
            processEndOfStream();
            if (!this.outputStreamEnded) {
              continue;
            }
            releaseCodec();
            bool = false;
          }
          if (i == -2)
          {
            processOutputFormat();
            bool = true;
            break;
          }
          if (i == -3)
          {
            processOutputBuffersChanged();
            bool = true;
            break;
          }
          if ((this.codecNeedsEosPropagationWorkaround) && ((this.inputStreamEnded) || (this.codecReinitializationState == 2))) {
            processEndOfStream();
          }
          bool = false;
          break;
          break;
          bool = processOutputBuffer(paramLong1, paramLong2, this.codec, this.outputBuffer, this.outputIndex, this.outputBufferInfo.flags, this.outputBufferInfo.presentationTimeUs, this.shouldSkipOutputBuffer);
        }
        label420:
        bool = false;
      }
    }
  }
  
  /* Error */
  private boolean feedInputBuffer()
    throws ExoPlaybackException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 279	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   4: ifnull +18 -> 22
    //   7: aload_0
    //   8: getfield 170	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecReinitializationState	I
    //   11: iconst_2
    //   12: if_icmpeq +10 -> 22
    //   15: aload_0
    //   16: getfield 358	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:inputStreamEnded	Z
    //   19: ifeq +7 -> 26
    //   22: iconst_0
    //   23: istore_1
    //   24: iload_1
    //   25: ireturn
    //   26: aload_0
    //   27: getfield 364	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:inputIndex	I
    //   30: ifge +49 -> 79
    //   33: aload_0
    //   34: aload_0
    //   35: getfield 279	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   38: lconst_0
    //   39: invokevirtual 368	android/media/MediaCodec:dequeueInputBuffer	(J)I
    //   42: putfield 364	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:inputIndex	I
    //   45: aload_0
    //   46: getfield 364	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:inputIndex	I
    //   49: ifge +8 -> 57
    //   52: iconst_0
    //   53: istore_1
    //   54: goto -30 -> 24
    //   57: aload_0
    //   58: getfield 144	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:buffer	Lorg/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer;
    //   61: aload_0
    //   62: aload_0
    //   63: getfield 364	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:inputIndex	I
    //   66: invokespecial 371	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:getInputBuffer	(I)Ljava/nio/ByteBuffer;
    //   69: putfield 374	org/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer:data	Ljava/nio/ByteBuffer;
    //   72: aload_0
    //   73: getfield 144	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:buffer	Lorg/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer;
    //   76: invokevirtual 377	org/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer:clear	()V
    //   79: aload_0
    //   80: getfield 170	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecReinitializationState	I
    //   83: iconst_1
    //   84: if_icmpne +47 -> 131
    //   87: aload_0
    //   88: getfield 356	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsEosPropagationWorkaround	Z
    //   91: ifeq +13 -> 104
    //   94: aload_0
    //   95: iconst_2
    //   96: putfield 170	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecReinitializationState	I
    //   99: iconst_0
    //   100: istore_1
    //   101: goto -77 -> 24
    //   104: aload_0
    //   105: iconst_1
    //   106: putfield 277	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecReceivedEos	Z
    //   109: aload_0
    //   110: getfield 279	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   113: aload_0
    //   114: getfield 364	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:inputIndex	I
    //   117: iconst_0
    //   118: iconst_0
    //   119: lconst_0
    //   120: iconst_4
    //   121: invokevirtual 381	android/media/MediaCodec:queueInputBuffer	(IIIJI)V
    //   124: aload_0
    //   125: invokespecial 384	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:resetInputBuffer	()V
    //   128: goto -34 -> 94
    //   131: aload_0
    //   132: getfield 386	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsAdaptationWorkaroundBuffer	Z
    //   135: ifeq +54 -> 189
    //   138: aload_0
    //   139: iconst_0
    //   140: putfield 386	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsAdaptationWorkaroundBuffer	Z
    //   143: aload_0
    //   144: getfield 144	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:buffer	Lorg/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer;
    //   147: getfield 374	org/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer:data	Ljava/nio/ByteBuffer;
    //   150: getstatic 112	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:ADAPTATION_WORKAROUND_BUFFER	[B
    //   153: invokevirtual 390	java/nio/ByteBuffer:put	([B)Ljava/nio/ByteBuffer;
    //   156: pop
    //   157: aload_0
    //   158: getfield 279	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   161: aload_0
    //   162: getfield 364	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:inputIndex	I
    //   165: iconst_0
    //   166: getstatic 112	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:ADAPTATION_WORKAROUND_BUFFER	[B
    //   169: arraylength
    //   170: lconst_0
    //   171: iconst_0
    //   172: invokevirtual 381	android/media/MediaCodec:queueInputBuffer	(IIIJI)V
    //   175: aload_0
    //   176: invokespecial 384	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:resetInputBuffer	()V
    //   179: aload_0
    //   180: iconst_1
    //   181: putfield 392	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecReceivedBuffers	Z
    //   184: iconst_1
    //   185: istore_1
    //   186: goto -162 -> 24
    //   189: iconst_0
    //   190: istore_2
    //   191: aload_0
    //   192: getfield 394	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:waitingForKeys	Z
    //   195: ifeq +17 -> 212
    //   198: bipush -4
    //   200: istore_3
    //   201: iload_3
    //   202: bipush -3
    //   204: if_icmpne +104 -> 308
    //   207: iconst_0
    //   208: istore_1
    //   209: goto -185 -> 24
    //   212: aload_0
    //   213: getfield 168	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecReconfigurationState	I
    //   216: iconst_1
    //   217: if_icmpne +63 -> 280
    //   220: iconst_0
    //   221: istore_2
    //   222: iload_2
    //   223: aload_0
    //   224: getfield 396	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:format	Lorg/telegram/messenger/exoplayer2/Format;
    //   227: getfield 217	org/telegram/messenger/exoplayer2/Format:initializationData	Ljava/util/List;
    //   230: invokeinterface 399 1 0
    //   235: if_icmpge +40 -> 275
    //   238: aload_0
    //   239: getfield 396	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:format	Lorg/telegram/messenger/exoplayer2/Format;
    //   242: getfield 217	org/telegram/messenger/exoplayer2/Format:initializationData	Ljava/util/List;
    //   245: iload_2
    //   246: invokeinterface 403 2 0
    //   251: checkcast 404	[B
    //   254: astore 4
    //   256: aload_0
    //   257: getfield 144	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:buffer	Lorg/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer;
    //   260: getfield 374	org/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer:data	Ljava/nio/ByteBuffer;
    //   263: aload 4
    //   265: invokevirtual 390	java/nio/ByteBuffer:put	([B)Ljava/nio/ByteBuffer;
    //   268: pop
    //   269: iinc 2 1
    //   272: goto -50 -> 222
    //   275: aload_0
    //   276: iconst_2
    //   277: putfield 168	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecReconfigurationState	I
    //   280: aload_0
    //   281: getfield 144	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:buffer	Lorg/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer;
    //   284: getfield 374	org/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer:data	Ljava/nio/ByteBuffer;
    //   287: invokevirtual 406	java/nio/ByteBuffer:position	()I
    //   290: istore_2
    //   291: aload_0
    //   292: aload_0
    //   293: getfield 156	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:formatHolder	Lorg/telegram/messenger/exoplayer2/FormatHolder;
    //   296: aload_0
    //   297: getfield 144	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:buffer	Lorg/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer;
    //   300: iconst_0
    //   301: invokevirtual 410	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:readSource	(Lorg/telegram/messenger/exoplayer2/FormatHolder;Lorg/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer;Z)I
    //   304: istore_3
    //   305: goto -104 -> 201
    //   308: iload_3
    //   309: bipush -5
    //   311: if_icmpne +39 -> 350
    //   314: aload_0
    //   315: getfield 168	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecReconfigurationState	I
    //   318: iconst_2
    //   319: if_icmpne +15 -> 334
    //   322: aload_0
    //   323: getfield 144	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:buffer	Lorg/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer;
    //   326: invokevirtual 377	org/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer:clear	()V
    //   329: aload_0
    //   330: iconst_1
    //   331: putfield 168	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecReconfigurationState	I
    //   334: aload_0
    //   335: aload_0
    //   336: getfield 156	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:formatHolder	Lorg/telegram/messenger/exoplayer2/FormatHolder;
    //   339: getfield 411	org/telegram/messenger/exoplayer2/FormatHolder:format	Lorg/telegram/messenger/exoplayer2/Format;
    //   342: invokevirtual 415	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:onInputFormatChanged	(Lorg/telegram/messenger/exoplayer2/Format;)V
    //   345: iconst_1
    //   346: istore_1
    //   347: goto -323 -> 24
    //   350: aload_0
    //   351: getfield 144	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:buffer	Lorg/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer;
    //   354: invokevirtual 418	org/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer:isEndOfStream	()Z
    //   357: ifeq +95 -> 452
    //   360: aload_0
    //   361: getfield 168	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecReconfigurationState	I
    //   364: iconst_2
    //   365: if_icmpne +15 -> 380
    //   368: aload_0
    //   369: getfield 144	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:buffer	Lorg/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer;
    //   372: invokevirtual 377	org/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer:clear	()V
    //   375: aload_0
    //   376: iconst_1
    //   377: putfield 168	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecReconfigurationState	I
    //   380: aload_0
    //   381: iconst_1
    //   382: putfield 358	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:inputStreamEnded	Z
    //   385: aload_0
    //   386: getfield 392	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecReceivedBuffers	Z
    //   389: ifne +12 -> 401
    //   392: aload_0
    //   393: invokespecial 298	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:processEndOfStream	()V
    //   396: iconst_0
    //   397: istore_1
    //   398: goto -374 -> 24
    //   401: aload_0
    //   402: getfield 356	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsEosPropagationWorkaround	Z
    //   405: ifeq +8 -> 413
    //   408: iconst_0
    //   409: istore_1
    //   410: goto -386 -> 24
    //   413: aload_0
    //   414: iconst_1
    //   415: putfield 277	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecReceivedEos	Z
    //   418: aload_0
    //   419: getfield 279	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   422: aload_0
    //   423: getfield 364	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:inputIndex	I
    //   426: iconst_0
    //   427: iconst_0
    //   428: lconst_0
    //   429: iconst_4
    //   430: invokevirtual 381	android/media/MediaCodec:queueInputBuffer	(IIIJI)V
    //   433: aload_0
    //   434: invokespecial 384	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:resetInputBuffer	()V
    //   437: goto -29 -> 408
    //   440: astore 4
    //   442: aload 4
    //   444: aload_0
    //   445: invokevirtual 421	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:getIndex	()I
    //   448: invokestatic 425	org/telegram/messenger/exoplayer2/ExoPlaybackException:createForRenderer	(Ljava/lang/Exception;I)Lorg/telegram/messenger/exoplayer2/ExoPlaybackException;
    //   451: athrow
    //   452: aload_0
    //   453: getfield 427	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:waitingForFirstSyncFrame	Z
    //   456: ifeq +38 -> 494
    //   459: aload_0
    //   460: getfield 144	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:buffer	Lorg/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer;
    //   463: invokevirtual 430	org/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer:isKeyFrame	()Z
    //   466: ifne +28 -> 494
    //   469: aload_0
    //   470: getfield 144	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:buffer	Lorg/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer;
    //   473: invokevirtual 377	org/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer:clear	()V
    //   476: aload_0
    //   477: getfield 168	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecReconfigurationState	I
    //   480: iconst_2
    //   481: if_icmpne +8 -> 489
    //   484: aload_0
    //   485: iconst_1
    //   486: putfield 168	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecReconfigurationState	I
    //   489: iconst_1
    //   490: istore_1
    //   491: goto -467 -> 24
    //   494: aload_0
    //   495: iconst_0
    //   496: putfield 427	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:waitingForFirstSyncFrame	Z
    //   499: aload_0
    //   500: getfield 144	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:buffer	Lorg/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer;
    //   503: invokevirtual 433	org/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer:isEncrypted	()Z
    //   506: istore_1
    //   507: aload_0
    //   508: aload_0
    //   509: iload_1
    //   510: invokespecial 437	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:shouldWaitForKeys	(Z)Z
    //   513: putfield 394	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:waitingForKeys	Z
    //   516: aload_0
    //   517: getfield 394	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:waitingForKeys	Z
    //   520: ifeq +8 -> 528
    //   523: iconst_0
    //   524: istore_1
    //   525: goto -501 -> 24
    //   528: aload_0
    //   529: getfield 439	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsDiscardToSpsWorkaround	Z
    //   532: ifeq +40 -> 572
    //   535: iload_1
    //   536: ifne +36 -> 572
    //   539: aload_0
    //   540: getfield 144	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:buffer	Lorg/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer;
    //   543: getfield 374	org/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer:data	Ljava/nio/ByteBuffer;
    //   546: invokestatic 445	org/telegram/messenger/exoplayer2/util/NalUnitUtil:discardToSps	(Ljava/nio/ByteBuffer;)V
    //   549: aload_0
    //   550: getfield 144	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:buffer	Lorg/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer;
    //   553: getfield 374	org/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer:data	Ljava/nio/ByteBuffer;
    //   556: invokevirtual 406	java/nio/ByteBuffer:position	()I
    //   559: ifne +8 -> 567
    //   562: iconst_1
    //   563: istore_1
    //   564: goto -540 -> 24
    //   567: aload_0
    //   568: iconst_0
    //   569: putfield 439	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsDiscardToSpsWorkaround	Z
    //   572: aload_0
    //   573: getfield 144	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:buffer	Lorg/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer;
    //   576: getfield 448	org/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer:timeUs	J
    //   579: lstore 5
    //   581: aload_0
    //   582: getfield 144	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:buffer	Lorg/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer;
    //   585: invokevirtual 451	org/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer:isDecodeOnly	()Z
    //   588: ifeq +18 -> 606
    //   591: aload_0
    //   592: getfield 161	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:decodeOnlyPresentationTimestamps	Ljava/util/List;
    //   595: lload 5
    //   597: invokestatic 457	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   600: invokeinterface 460 2 0
    //   605: pop
    //   606: aload_0
    //   607: getfield 144	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:buffer	Lorg/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer;
    //   610: invokevirtual 463	org/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer:flip	()V
    //   613: aload_0
    //   614: aload_0
    //   615: getfield 144	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:buffer	Lorg/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer;
    //   618: invokevirtual 467	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:onQueueInputBuffer	(Lorg/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer;)V
    //   621: iload_1
    //   622: ifeq +67 -> 689
    //   625: aload_0
    //   626: getfield 144	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:buffer	Lorg/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer;
    //   629: iload_2
    //   630: invokestatic 471	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:getFrameworkCryptoInfo	(Lorg/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer;I)Landroid/media/MediaCodec$CryptoInfo;
    //   633: astore 4
    //   635: aload_0
    //   636: getfield 279	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   639: aload_0
    //   640: getfield 364	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:inputIndex	I
    //   643: iconst_0
    //   644: aload 4
    //   646: lload 5
    //   648: iconst_0
    //   649: invokevirtual 475	android/media/MediaCodec:queueSecureInputBuffer	(IILandroid/media/MediaCodec$CryptoInfo;JI)V
    //   652: aload_0
    //   653: invokespecial 384	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:resetInputBuffer	()V
    //   656: aload_0
    //   657: iconst_1
    //   658: putfield 392	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecReceivedBuffers	Z
    //   661: aload_0
    //   662: iconst_0
    //   663: putfield 168	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecReconfigurationState	I
    //   666: aload_0
    //   667: getfield 477	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:decoderCounters	Lorg/telegram/messenger/exoplayer2/decoder/DecoderCounters;
    //   670: astore 4
    //   672: aload 4
    //   674: aload 4
    //   676: getfield 482	org/telegram/messenger/exoplayer2/decoder/DecoderCounters:inputBufferCount	I
    //   679: iconst_1
    //   680: iadd
    //   681: putfield 482	org/telegram/messenger/exoplayer2/decoder/DecoderCounters:inputBufferCount	I
    //   684: iconst_1
    //   685: istore_1
    //   686: goto -662 -> 24
    //   689: aload_0
    //   690: getfield 279	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   693: aload_0
    //   694: getfield 364	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:inputIndex	I
    //   697: iconst_0
    //   698: aload_0
    //   699: getfield 144	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:buffer	Lorg/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer;
    //   702: getfield 374	org/telegram/messenger/exoplayer2/decoder/DecoderInputBuffer:data	Ljava/nio/ByteBuffer;
    //   705: invokevirtual 484	java/nio/ByteBuffer:limit	()I
    //   708: lload 5
    //   710: iconst_0
    //   711: invokevirtual 381	android/media/MediaCodec:queueInputBuffer	(IIIJI)V
    //   714: goto -62 -> 652
    //   717: astore 4
    //   719: aload 4
    //   721: aload_0
    //   722: invokevirtual 421	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:getIndex	()I
    //   725: invokestatic 425	org/telegram/messenger/exoplayer2/ExoPlaybackException:createForRenderer	(Ljava/lang/Exception;I)Lorg/telegram/messenger/exoplayer2/ExoPlaybackException;
    //   728: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	729	0	this	MediaCodecRenderer
    //   23	663	1	bool	boolean
    //   190	440	2	i	int
    //   200	112	3	j	int
    //   254	10	4	arrayOfByte	byte[]
    //   440	3	4	localCryptoException1	android.media.MediaCodec.CryptoException
    //   633	42	4	localObject	Object
    //   717	3	4	localCryptoException2	android.media.MediaCodec.CryptoException
    //   579	130	5	l	long
    // Exception table:
    //   from	to	target	type
    //   401	408	440	android/media/MediaCodec$CryptoException
    //   413	437	440	android/media/MediaCodec$CryptoException
    //   572	606	717	android/media/MediaCodec$CryptoException
    //   606	621	717	android/media/MediaCodec$CryptoException
    //   625	652	717	android/media/MediaCodec$CryptoException
    //   652	684	717	android/media/MediaCodec$CryptoException
    //   689	714	717	android/media/MediaCodec$CryptoException
  }
  
  private void getCodecBuffers()
  {
    if (Util.SDK_INT < 21)
    {
      this.inputBuffers = this.codec.getInputBuffers();
      this.outputBuffers = this.codec.getOutputBuffers();
    }
  }
  
  private static MediaCodec.CryptoInfo getFrameworkCryptoInfo(DecoderInputBuffer paramDecoderInputBuffer, int paramInt)
  {
    MediaCodec.CryptoInfo localCryptoInfo = paramDecoderInputBuffer.cryptoInfo.getFrameworkCryptoInfoV16();
    if (paramInt == 0) {}
    for (;;)
    {
      return localCryptoInfo;
      if (localCryptoInfo.numBytesOfClearData == null) {
        localCryptoInfo.numBytesOfClearData = new int[1];
      }
      paramDecoderInputBuffer = localCryptoInfo.numBytesOfClearData;
      paramDecoderInputBuffer[0] += paramInt;
    }
  }
  
  private ByteBuffer getInputBuffer(int paramInt)
  {
    if (Util.SDK_INT >= 21) {}
    for (ByteBuffer localByteBuffer = this.codec.getInputBuffer(paramInt);; localByteBuffer = this.inputBuffers[paramInt]) {
      return localByteBuffer;
    }
  }
  
  private ByteBuffer getOutputBuffer(int paramInt)
  {
    if (Util.SDK_INT >= 21) {}
    for (ByteBuffer localByteBuffer = this.codec.getOutputBuffer(paramInt);; localByteBuffer = this.outputBuffers[paramInt]) {
      return localByteBuffer;
    }
  }
  
  private boolean hasOutputBuffer()
  {
    if (this.outputIndex >= 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private void processEndOfStream()
    throws ExoPlaybackException
  {
    if (this.codecReinitializationState == 2)
    {
      releaseCodec();
      maybeInitCodec();
    }
    for (;;)
    {
      return;
      this.outputStreamEnded = true;
      renderToEndOfStream();
    }
  }
  
  private void processOutputBuffersChanged()
  {
    if (Util.SDK_INT < 21) {
      this.outputBuffers = this.codec.getOutputBuffers();
    }
  }
  
  private void processOutputFormat()
    throws ExoPlaybackException
  {
    MediaFormat localMediaFormat = this.codec.getOutputFormat();
    if ((this.codecAdaptationWorkaroundMode != 0) && (localMediaFormat.getInteger("width") == 32) && (localMediaFormat.getInteger("height") == 32)) {
      this.shouldSkipAdaptationWorkaroundOutputBuffer = true;
    }
    for (;;)
    {
      return;
      if (this.codecNeedsMonoChannelCountWorkaround) {
        localMediaFormat.setInteger("channel-count", 1);
      }
      onOutputFormatChanged(this.codec, localMediaFormat);
    }
  }
  
  private void resetCodecBuffers()
  {
    if (Util.SDK_INT < 21)
    {
      this.inputBuffers = null;
      this.outputBuffers = null;
    }
  }
  
  private void resetInputBuffer()
  {
    this.inputIndex = -1;
    this.buffer.data = null;
  }
  
  private void resetOutputBuffer()
  {
    this.outputIndex = -1;
    this.outputBuffer = null;
  }
  
  private boolean shouldSkipOutputBuffer(long paramLong)
  {
    int i = this.decodeOnlyPresentationTimestamps.size();
    int j = 0;
    if (j < i) {
      if (((Long)this.decodeOnlyPresentationTimestamps.get(j)).longValue() == paramLong) {
        this.decodeOnlyPresentationTimestamps.remove(j);
      }
    }
    for (boolean bool = true;; bool = false)
    {
      return bool;
      j++;
      break;
    }
  }
  
  private boolean shouldWaitForKeys(boolean paramBoolean)
    throws ExoPlaybackException
  {
    boolean bool = true;
    if ((this.drmSession == null) || ((!paramBoolean) && (this.playClearSamplesWithoutKeys))) {
      paramBoolean = false;
    }
    for (;;)
    {
      return paramBoolean;
      int i = this.drmSession.getState();
      if (i == 1) {
        throw ExoPlaybackException.createForRenderer(this.drmSession.getError(), getIndex());
      }
      paramBoolean = bool;
      if (i == 4) {
        paramBoolean = false;
      }
    }
  }
  
  private void throwDecoderInitError(DecoderInitializationException paramDecoderInitializationException)
    throws ExoPlaybackException
  {
    throw ExoPlaybackException.createForRenderer(paramDecoderInitializationException, getIndex());
  }
  
  protected boolean canReconfigureCodec(MediaCodec paramMediaCodec, boolean paramBoolean, Format paramFormat1, Format paramFormat2)
  {
    return false;
  }
  
  protected abstract void configureCodec(MediaCodecInfo paramMediaCodecInfo, MediaCodec paramMediaCodec, Format paramFormat, MediaCrypto paramMediaCrypto)
    throws MediaCodecUtil.DecoderQueryException;
  
  protected void flushCodec()
    throws ExoPlaybackException
  {
    this.codecHotswapDeadlineMs = -9223372036854775807L;
    resetInputBuffer();
    resetOutputBuffer();
    this.waitingForFirstSyncFrame = true;
    this.waitingForKeys = false;
    this.shouldSkipOutputBuffer = false;
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
  
  protected final MediaCodec getCodec()
  {
    return this.codec;
  }
  
  protected final MediaCodecInfo getCodecInfo()
  {
    return this.codecInfo;
  }
  
  protected MediaCodecInfo getDecoderInfo(MediaCodecSelector paramMediaCodecSelector, Format paramFormat, boolean paramBoolean)
    throws MediaCodecUtil.DecoderQueryException
  {
    return paramMediaCodecSelector.getDecoderInfo(paramFormat.sampleMimeType, paramBoolean);
  }
  
  protected long getDequeueOutputBufferTimeoutUs()
  {
    return 0L;
  }
  
  protected final MediaFormat getMediaFormatForPlayback(Format paramFormat)
  {
    paramFormat = paramFormat.getFrameworkMediaFormatV16();
    if (Util.SDK_INT >= 23) {
      configureMediaFormatForPlaybackV23(paramFormat);
    }
    return paramFormat;
  }
  
  public boolean isEnded()
  {
    return this.outputStreamEnded;
  }
  
  public boolean isReady()
  {
    if ((this.format != null) && (!this.waitingForKeys) && ((isSourceReady()) || (hasOutputBuffer()) || ((this.codecHotswapDeadlineMs != -9223372036854775807L) && (SystemClock.elapsedRealtime() < this.codecHotswapDeadlineMs)))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  protected final void maybeInitCodec()
    throws ExoPlaybackException
  {
    if ((this.codec != null) || (this.format == null)) {}
    for (;;)
    {
      return;
      this.drmSession = this.pendingDrmSession;
      Object localObject1 = this.format.sampleMimeType;
      Object localObject2 = null;
      boolean bool1 = false;
      boolean bool2 = bool1;
      Object localObject3 = localObject2;
      FrameworkMediaCrypto localFrameworkMediaCrypto;
      if (this.drmSession != null)
      {
        localFrameworkMediaCrypto = (FrameworkMediaCrypto)this.drmSession.getMediaCrypto();
        if (localFrameworkMediaCrypto != null) {
          break label470;
        }
        if (this.drmSession.getError() == null) {
          continue;
        }
        localObject3 = localObject2;
        bool2 = bool1;
      }
      label85:
      if (this.codecInfo == null) {}
      try
      {
        this.codecInfo = getDecoderInfo(this.mediaCodecSelector, this.format, bool2);
        if ((this.codecInfo == null) && (bool2))
        {
          this.codecInfo = getDecoderInfo(this.mediaCodecSelector, this.format, false);
          if (this.codecInfo != null)
          {
            localObject2 = new java/lang/StringBuilder;
            ((StringBuilder)localObject2).<init>();
            Log.w("MediaCodecRenderer", "Drm session requires secure decoder for " + (String)localObject1 + ", but no secure decoder available. Trying to proceed with " + this.codecInfo.name + ".");
          }
        }
        if (this.codecInfo == null) {
          throwDecoderInitError(new DecoderInitializationException(this.format, null, bool2, -49999));
        }
        if (!shouldInitCodec(this.codecInfo)) {
          continue;
        }
        localObject2 = this.codecInfo.name;
        this.codecAdaptationWorkaroundMode = codecAdaptationWorkaroundMode((String)localObject2);
        this.codecNeedsDiscardToSpsWorkaround = codecNeedsDiscardToSpsWorkaround((String)localObject2, this.format);
        this.codecNeedsFlushWorkaround = codecNeedsFlushWorkaround((String)localObject2);
        this.codecNeedsEosPropagationWorkaround = codecNeedsEosPropagationWorkaround((String)localObject2);
        this.codecNeedsEosFlushWorkaround = codecNeedsEosFlushWorkaround((String)localObject2);
        this.codecNeedsEosOutputExceptionWorkaround = codecNeedsEosOutputExceptionWorkaround((String)localObject2);
        this.codecNeedsMonoChannelCountWorkaround = codecNeedsMonoChannelCountWorkaround((String)localObject2, this.format);
      }
      catch (MediaCodecUtil.DecoderQueryException localDecoderQueryException)
      {
        try
        {
          long l1 = SystemClock.elapsedRealtime();
          localObject1 = new java/lang/StringBuilder;
          ((StringBuilder)localObject1).<init>();
          TraceUtil.beginSection("createCodec:" + (String)localObject2);
          this.codec = MediaCodec.createByCodecName((String)localObject2);
          TraceUtil.endSection();
          TraceUtil.beginSection("configureCodec");
          configureCodec(this.codecInfo, this.codec, this.format, (MediaCrypto)localObject3);
          TraceUtil.endSection();
          TraceUtil.beginSection("startCodec");
          this.codec.start();
          TraceUtil.endSection();
          l2 = SystemClock.elapsedRealtime();
          onCodecInitialized((String)localObject2, l2, l2 - l1);
          getCodecBuffers();
          if (getState() == 2)
          {
            l2 = SystemClock.elapsedRealtime() + 1000L;
            this.codecHotswapDeadlineMs = l2;
            resetInputBuffer();
            resetOutputBuffer();
            this.waitingForFirstSyncFrame = true;
            localObject3 = this.decoderCounters;
            ((DecoderCounters)localObject3).decoderInitCount += 1;
            continue;
            label470:
            localObject3 = localFrameworkMediaCrypto.getWrappedMediaCrypto();
            bool2 = localFrameworkMediaCrypto.requiresSecureDecoderComponent((String)localObject1);
            break label85;
            localDecoderQueryException = localDecoderQueryException;
            throwDecoderInitError(new DecoderInitializationException(this.format, localDecoderQueryException, bool2, -49998));
          }
        }
        catch (Exception localException)
        {
          for (;;)
          {
            throwDecoderInitError(new DecoderInitializationException(this.format, localException, bool2, localDecoderQueryException));
            continue;
            long l2 = -9223372036854775807L;
          }
        }
      }
    }
  }
  
  protected void onCodecInitialized(String paramString, long paramLong1, long paramLong2) {}
  
  /* Error */
  protected void onDisabled()
  {
    // Byte code:
    //   0: aload_0
    //   1: aconst_null
    //   2: putfield 396	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:format	Lorg/telegram/messenger/exoplayer2/Format;
    //   5: aload_0
    //   6: invokevirtual 303	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:releaseCodec	()V
    //   9: aload_0
    //   10: getfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   13: ifnull +16 -> 29
    //   16: aload_0
    //   17: getfield 137	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lorg/telegram/messenger/exoplayer2/drm/DrmSessionManager;
    //   20: aload_0
    //   21: getfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   24: invokeinterface 728 2 0
    //   29: aload_0
    //   30: getfield 617	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   33: ifnull +27 -> 60
    //   36: aload_0
    //   37: getfield 617	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   40: aload_0
    //   41: getfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   44: if_acmpeq +16 -> 60
    //   47: aload_0
    //   48: getfield 137	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lorg/telegram/messenger/exoplayer2/drm/DrmSessionManager;
    //   51: aload_0
    //   52: getfield 617	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   55: invokeinterface 728 2 0
    //   60: aload_0
    //   61: aconst_null
    //   62: putfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   65: aload_0
    //   66: aconst_null
    //   67: putfield 617	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   70: return
    //   71: astore_1
    //   72: aload_0
    //   73: aconst_null
    //   74: putfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   77: aload_0
    //   78: aconst_null
    //   79: putfield 617	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   82: aload_1
    //   83: athrow
    //   84: astore_1
    //   85: aload_0
    //   86: getfield 617	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   89: ifnull +27 -> 116
    //   92: aload_0
    //   93: getfield 617	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   96: aload_0
    //   97: getfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   100: if_acmpeq +16 -> 116
    //   103: aload_0
    //   104: getfield 137	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lorg/telegram/messenger/exoplayer2/drm/DrmSessionManager;
    //   107: aload_0
    //   108: getfield 617	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   111: invokeinterface 728 2 0
    //   116: aload_0
    //   117: aconst_null
    //   118: putfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   121: aload_0
    //   122: aconst_null
    //   123: putfield 617	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   126: aload_1
    //   127: athrow
    //   128: astore_1
    //   129: aload_0
    //   130: aconst_null
    //   131: putfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   134: aload_0
    //   135: aconst_null
    //   136: putfield 617	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   139: aload_1
    //   140: athrow
    //   141: astore_1
    //   142: aload_0
    //   143: getfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   146: ifnull +16 -> 162
    //   149: aload_0
    //   150: getfield 137	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lorg/telegram/messenger/exoplayer2/drm/DrmSessionManager;
    //   153: aload_0
    //   154: getfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   157: invokeinterface 728 2 0
    //   162: aload_0
    //   163: getfield 617	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   166: ifnull +27 -> 193
    //   169: aload_0
    //   170: getfield 617	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   173: aload_0
    //   174: getfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   177: if_acmpeq +16 -> 193
    //   180: aload_0
    //   181: getfield 137	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lorg/telegram/messenger/exoplayer2/drm/DrmSessionManager;
    //   184: aload_0
    //   185: getfield 617	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   188: invokeinterface 728 2 0
    //   193: aload_0
    //   194: aconst_null
    //   195: putfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   198: aload_0
    //   199: aconst_null
    //   200: putfield 617	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   203: aload_1
    //   204: athrow
    //   205: astore_1
    //   206: aload_0
    //   207: aconst_null
    //   208: putfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   211: aload_0
    //   212: aconst_null
    //   213: putfield 617	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   216: aload_1
    //   217: athrow
    //   218: astore_1
    //   219: aload_0
    //   220: getfield 617	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   223: ifnull +27 -> 250
    //   226: aload_0
    //   227: getfield 617	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   230: aload_0
    //   231: getfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   234: if_acmpeq +16 -> 250
    //   237: aload_0
    //   238: getfield 137	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lorg/telegram/messenger/exoplayer2/drm/DrmSessionManager;
    //   241: aload_0
    //   242: getfield 617	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   245: invokeinterface 728 2 0
    //   250: aload_0
    //   251: aconst_null
    //   252: putfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   255: aload_0
    //   256: aconst_null
    //   257: putfield 617	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   260: aload_1
    //   261: athrow
    //   262: astore_1
    //   263: aload_0
    //   264: aconst_null
    //   265: putfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   268: aload_0
    //   269: aconst_null
    //   270: putfield 617	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   273: aload_1
    //   274: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	275	0	this	MediaCodecRenderer
    //   71	12	1	localObject1	Object
    //   84	43	1	localObject2	Object
    //   128	12	1	localObject3	Object
    //   141	63	1	localObject4	Object
    //   205	12	1	localObject5	Object
    //   218	43	1	localObject6	Object
    //   262	12	1	localObject7	Object
    // Exception table:
    //   from	to	target	type
    //   29	60	71	finally
    //   9	29	84	finally
    //   85	116	128	finally
    //   5	9	141	finally
    //   162	193	205	finally
    //   142	162	218	finally
    //   219	250	262	finally
  }
  
  protected void onEnabled(boolean paramBoolean)
    throws ExoPlaybackException
  {
    this.decoderCounters = new DecoderCounters();
  }
  
  protected void onInputFormatChanged(Format paramFormat)
    throws ExoPlaybackException
  {
    Format localFormat = this.format;
    this.format = paramFormat;
    DrmInitData localDrmInitData = this.format.drmInitData;
    int i;
    if (localFormat == null)
    {
      paramFormat = null;
      if (Util.areEqual(localDrmInitData, paramFormat)) {
        break label83;
      }
      i = 1;
    }
    for (;;)
    {
      if (i != 0)
      {
        if (this.format.drmInitData == null) {
          break label241;
        }
        if (this.drmSessionManager == null)
        {
          throw ExoPlaybackException.createForRenderer(new IllegalStateException("Media requires a DrmSessionManager"), getIndex());
          paramFormat = localFormat.drmInitData;
          break;
          label83:
          i = 0;
          continue;
        }
        this.pendingDrmSession = this.drmSessionManager.acquireSession(Looper.myLooper(), this.format.drmInitData);
        if (this.pendingDrmSession == this.drmSession) {
          this.drmSessionManager.releaseSession(this.pendingDrmSession);
        }
      }
    }
    boolean bool;
    if ((this.pendingDrmSession == this.drmSession) && (this.codec != null) && (canReconfigureCodec(this.codec, this.codecInfo.adaptive, localFormat, this.format)))
    {
      this.codecReconfigured = true;
      this.codecReconfigurationState = 1;
      if ((this.codecAdaptationWorkaroundMode == 2) || ((this.codecAdaptationWorkaroundMode == 1) && (this.format.width == localFormat.width) && (this.format.height == localFormat.height)))
      {
        bool = true;
        label234:
        this.codecNeedsAdaptationWorkaroundBuffer = bool;
      }
    }
    for (;;)
    {
      return;
      label241:
      this.pendingDrmSession = null;
      break;
      bool = false;
      break label234;
      if (this.codecReceivedBuffers)
      {
        this.codecReinitializationState = 1;
      }
      else
      {
        releaseCodec();
        maybeInitCodec();
      }
    }
  }
  
  protected void onOutputFormatChanged(MediaCodec paramMediaCodec, MediaFormat paramMediaFormat)
    throws ExoPlaybackException
  {}
  
  protected void onPositionReset(long paramLong, boolean paramBoolean)
    throws ExoPlaybackException
  {
    this.inputStreamEnded = false;
    this.outputStreamEnded = false;
    if (this.codec != null) {
      flushCodec();
    }
  }
  
  protected void onProcessedOutputBuffer(long paramLong) {}
  
  protected void onQueueInputBuffer(DecoderInputBuffer paramDecoderInputBuffer) {}
  
  protected void onStarted() {}
  
  protected void onStopped() {}
  
  protected abstract boolean processOutputBuffer(long paramLong1, long paramLong2, MediaCodec paramMediaCodec, ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, long paramLong3, boolean paramBoolean)
    throws ExoPlaybackException;
  
  /* Error */
  protected void releaseCodec()
  {
    // Byte code:
    //   0: aload_0
    //   1: ldc2_w 569
    //   4: putfield 572	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecHotswapDeadlineMs	J
    //   7: aload_0
    //   8: invokespecial 384	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:resetInputBuffer	()V
    //   11: aload_0
    //   12: invokespecial 348	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:resetOutputBuffer	()V
    //   15: aload_0
    //   16: iconst_0
    //   17: putfield 394	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:waitingForKeys	Z
    //   20: aload_0
    //   21: iconst_0
    //   22: putfield 337	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:shouldSkipOutputBuffer	Z
    //   25: aload_0
    //   26: getfield 161	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:decodeOnlyPresentationTimestamps	Ljava/util/List;
    //   29: invokeinterface 573 1 0
    //   34: aload_0
    //   35: invokespecial 769	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:resetCodecBuffers	()V
    //   38: aload_0
    //   39: aconst_null
    //   40: putfield 588	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecInfo	Lorg/telegram/messenger/exoplayer2/mediacodec/MediaCodecInfo;
    //   43: aload_0
    //   44: iconst_0
    //   45: putfield 579	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecReconfigured	Z
    //   48: aload_0
    //   49: iconst_0
    //   50: putfield 392	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecReceivedBuffers	Z
    //   53: aload_0
    //   54: iconst_0
    //   55: putfield 439	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsDiscardToSpsWorkaround	Z
    //   58: aload_0
    //   59: iconst_0
    //   60: putfield 575	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsFlushWorkaround	Z
    //   63: aload_0
    //   64: iconst_0
    //   65: putfield 526	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecAdaptationWorkaroundMode	I
    //   68: aload_0
    //   69: iconst_0
    //   70: putfield 356	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsEosPropagationWorkaround	Z
    //   73: aload_0
    //   74: iconst_0
    //   75: putfield 577	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsEosFlushWorkaround	Z
    //   78: aload_0
    //   79: iconst_0
    //   80: putfield 535	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsMonoChannelCountWorkaround	Z
    //   83: aload_0
    //   84: iconst_0
    //   85: putfield 386	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsAdaptationWorkaroundBuffer	Z
    //   88: aload_0
    //   89: iconst_0
    //   90: putfield 291	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:shouldSkipAdaptationWorkaroundOutputBuffer	Z
    //   93: aload_0
    //   94: iconst_0
    //   95: putfield 277	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecReceivedEos	Z
    //   98: aload_0
    //   99: iconst_0
    //   100: putfield 168	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecReconfigurationState	I
    //   103: aload_0
    //   104: iconst_0
    //   105: putfield 170	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecReinitializationState	I
    //   108: aload_0
    //   109: getfield 279	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   112: ifnull +73 -> 185
    //   115: aload_0
    //   116: getfield 477	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:decoderCounters	Lorg/telegram/messenger/exoplayer2/decoder/DecoderCounters;
    //   119: astore_1
    //   120: aload_1
    //   121: aload_1
    //   122: getfield 772	org/telegram/messenger/exoplayer2/decoder/DecoderCounters:decoderReleaseCount	I
    //   125: iconst_1
    //   126: iadd
    //   127: putfield 772	org/telegram/messenger/exoplayer2/decoder/DecoderCounters:decoderReleaseCount	I
    //   130: aload_0
    //   131: getfield 279	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   134: invokevirtual 775	android/media/MediaCodec:stop	()V
    //   137: aload_0
    //   138: getfield 279	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   141: invokevirtual 778	android/media/MediaCodec:release	()V
    //   144: aload_0
    //   145: aconst_null
    //   146: putfield 279	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   149: aload_0
    //   150: getfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   153: ifnull +32 -> 185
    //   156: aload_0
    //   157: getfield 617	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   160: aload_0
    //   161: getfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   164: if_acmpeq +21 -> 185
    //   167: aload_0
    //   168: getfield 137	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lorg/telegram/messenger/exoplayer2/drm/DrmSessionManager;
    //   171: aload_0
    //   172: getfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   175: invokeinterface 728 2 0
    //   180: aload_0
    //   181: aconst_null
    //   182: putfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   185: return
    //   186: astore_1
    //   187: aload_0
    //   188: aconst_null
    //   189: putfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   192: aload_1
    //   193: athrow
    //   194: astore_1
    //   195: aload_0
    //   196: aconst_null
    //   197: putfield 279	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   200: aload_0
    //   201: getfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   204: ifnull +32 -> 236
    //   207: aload_0
    //   208: getfield 617	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   211: aload_0
    //   212: getfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   215: if_acmpeq +21 -> 236
    //   218: aload_0
    //   219: getfield 137	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lorg/telegram/messenger/exoplayer2/drm/DrmSessionManager;
    //   222: aload_0
    //   223: getfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   226: invokeinterface 728 2 0
    //   231: aload_0
    //   232: aconst_null
    //   233: putfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   236: aload_1
    //   237: athrow
    //   238: astore_1
    //   239: aload_0
    //   240: aconst_null
    //   241: putfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   244: aload_1
    //   245: athrow
    //   246: astore_1
    //   247: aload_0
    //   248: getfield 279	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   251: invokevirtual 778	android/media/MediaCodec:release	()V
    //   254: aload_0
    //   255: aconst_null
    //   256: putfield 279	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   259: aload_0
    //   260: getfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   263: ifnull +32 -> 295
    //   266: aload_0
    //   267: getfield 617	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   270: aload_0
    //   271: getfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   274: if_acmpeq +21 -> 295
    //   277: aload_0
    //   278: getfield 137	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lorg/telegram/messenger/exoplayer2/drm/DrmSessionManager;
    //   281: aload_0
    //   282: getfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   285: invokeinterface 728 2 0
    //   290: aload_0
    //   291: aconst_null
    //   292: putfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   295: aload_1
    //   296: athrow
    //   297: astore_1
    //   298: aload_0
    //   299: aconst_null
    //   300: putfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   303: aload_1
    //   304: athrow
    //   305: astore_1
    //   306: aload_0
    //   307: aconst_null
    //   308: putfield 279	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   311: aload_0
    //   312: getfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   315: ifnull +32 -> 347
    //   318: aload_0
    //   319: getfield 617	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   322: aload_0
    //   323: getfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   326: if_acmpeq +21 -> 347
    //   329: aload_0
    //   330: getfield 137	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSessionManager	Lorg/telegram/messenger/exoplayer2/drm/DrmSessionManager;
    //   333: aload_0
    //   334: getfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   337: invokeinterface 728 2 0
    //   342: aload_0
    //   343: aconst_null
    //   344: putfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   347: aload_1
    //   348: athrow
    //   349: astore_1
    //   350: aload_0
    //   351: aconst_null
    //   352: putfield 550	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   355: aload_1
    //   356: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	357	0	this	MediaCodecRenderer
    //   119	3	1	localDecoderCounters	DecoderCounters
    //   186	7	1	localObject1	Object
    //   194	43	1	localObject2	Object
    //   238	7	1	localObject3	Object
    //   246	50	1	localObject4	Object
    //   297	7	1	localObject5	Object
    //   305	43	1	localObject6	Object
    //   349	7	1	localObject7	Object
    // Exception table:
    //   from	to	target	type
    //   167	180	186	finally
    //   137	144	194	finally
    //   218	231	238	finally
    //   130	137	246	finally
    //   277	290	297	finally
    //   247	254	305	finally
    //   329	342	349	finally
  }
  
  public void render(long paramLong1, long paramLong2)
    throws ExoPlaybackException
  {
    if (this.outputStreamEnded)
    {
      renderToEndOfStream();
      return;
    }
    int i;
    if (this.format == null)
    {
      this.flagsOnlyBuffer.clear();
      i = readSource(this.formatHolder, this.flagsOnlyBuffer, true);
      if (i == -5) {
        onInputFormatChanged(this.formatHolder.format);
      }
    }
    else
    {
      maybeInitCodec();
      if (this.codec == null) {
        break label134;
      }
      TraceUtil.beginSection("drainAndFeed");
      while (drainOutputBuffer(paramLong1, paramLong2)) {}
      while (feedInputBuffer()) {}
      TraceUtil.endSection();
    }
    for (;;)
    {
      this.decoderCounters.ensureUpdated();
      break;
      if (i != -4) {
        break;
      }
      Assertions.checkState(this.flagsOnlyBuffer.isEndOfStream());
      this.inputStreamEnded = true;
      processEndOfStream();
      break;
      label134:
      DecoderCounters localDecoderCounters = this.decoderCounters;
      localDecoderCounters.skippedInputBufferCount += skipSource(paramLong1);
      this.flagsOnlyBuffer.clear();
      i = readSource(this.formatHolder, this.flagsOnlyBuffer, false);
      if (i == -5)
      {
        onInputFormatChanged(this.formatHolder.format);
      }
      else if (i == -4)
      {
        Assertions.checkState(this.flagsOnlyBuffer.isEndOfStream());
        this.inputStreamEnded = true;
        processEndOfStream();
      }
    }
  }
  
  protected void renderToEndOfStream()
    throws ExoPlaybackException
  {}
  
  protected boolean shouldInitCodec(MediaCodecInfo paramMediaCodecInfo)
  {
    return true;
  }
  
  public final int supportsFormat(Format paramFormat)
    throws ExoPlaybackException
  {
    try
    {
      int i = supportsFormat(this.mediaCodecSelector, this.drmSessionManager, paramFormat);
      return i;
    }
    catch (MediaCodecUtil.DecoderQueryException paramFormat)
    {
      throw ExoPlaybackException.createForRenderer(paramFormat, getIndex());
    }
  }
  
  protected abstract int supportsFormat(MediaCodecSelector paramMediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, Format paramFormat)
    throws MediaCodecUtil.DecoderQueryException;
  
  public final int supportsMixedMimeTypeAdaptation()
  {
    return 8;
  }
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface AdaptationWorkaroundMode {}
  
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
    
    public DecoderInitializationException(Format paramFormat, Throwable paramThrowable, boolean paramBoolean, int paramInt)
    {
      super(paramThrowable);
      this.mimeType = paramFormat.sampleMimeType;
      this.secureDecoderRequired = paramBoolean;
      this.decoderName = null;
      this.diagnosticInfo = buildCustomDiagnosticInfo(paramInt);
    }
    
    public DecoderInitializationException(Format paramFormat, Throwable paramThrowable, boolean paramBoolean, String paramString)
    {
      super(paramThrowable);
      this.mimeType = paramFormat.sampleMimeType;
      this.secureDecoderRequired = paramBoolean;
      this.decoderName = paramString;
      if (Util.SDK_INT >= 21) {}
      for (paramFormat = getDiagnosticInfoV21(paramThrowable);; paramFormat = null)
      {
        this.diagnosticInfo = paramFormat;
        return;
      }
    }
    
    private static String buildCustomDiagnosticInfo(int paramInt)
    {
      if (paramInt < 0) {}
      for (String str = "neg_";; str = "") {
        return "com.google.android.exoplayer.MediaCodecTrackRenderer_" + str + Math.abs(paramInt);
      }
    }
    
    @TargetApi(21)
    private static String getDiagnosticInfoV21(Throwable paramThrowable)
    {
      if ((paramThrowable instanceof MediaCodec.CodecException)) {}
      for (paramThrowable = ((MediaCodec.CodecException)paramThrowable).getDiagnosticInfo();; paramThrowable = null) {
        return paramThrowable;
      }
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface ReconfigurationState {}
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface ReinitializationState {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/mediacodec/MediaCodecRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */