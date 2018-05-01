package org.telegram.messenger.exoplayer2.audio;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.telegram.messenger.exoplayer2.BaseRenderer;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.PlaybackParameters;
import org.telegram.messenger.exoplayer2.RendererConfiguration;
import org.telegram.messenger.exoplayer2.decoder.DecoderCounters;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.decoder.SimpleDecoder;
import org.telegram.messenger.exoplayer2.decoder.SimpleOutputBuffer;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;
import org.telegram.messenger.exoplayer2.drm.DrmSession;
import org.telegram.messenger.exoplayer2.drm.DrmSessionManager;
import org.telegram.messenger.exoplayer2.drm.ExoMediaCrypto;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.MediaClock;
import org.telegram.messenger.exoplayer2.util.TraceUtil;
import org.telegram.messenger.exoplayer2.util.Util;

public abstract class SimpleDecoderAudioRenderer
  extends BaseRenderer
  implements MediaClock
{
  private static final int REINITIALIZATION_STATE_NONE = 0;
  private static final int REINITIALIZATION_STATE_SIGNAL_END_OF_STREAM = 1;
  private static final int REINITIALIZATION_STATE_WAIT_END_OF_STREAM = 2;
  private boolean allowPositionDiscontinuity;
  private final AudioSink audioSink;
  private boolean audioTrackNeedsConfigure;
  private long currentPositionUs;
  private SimpleDecoder<DecoderInputBuffer, ? extends SimpleOutputBuffer, ? extends AudioDecoderException> decoder;
  private DecoderCounters decoderCounters;
  private boolean decoderReceivedBuffers;
  private int decoderReinitializationState;
  private DrmSession<ExoMediaCrypto> drmSession;
  private final DrmSessionManager<ExoMediaCrypto> drmSessionManager;
  private int encoderDelay;
  private int encoderPadding;
  private final AudioRendererEventListener.EventDispatcher eventDispatcher;
  private final DecoderInputBuffer flagsOnlyBuffer;
  private final FormatHolder formatHolder;
  private DecoderInputBuffer inputBuffer;
  private Format inputFormat;
  private boolean inputStreamEnded;
  private SimpleOutputBuffer outputBuffer;
  private boolean outputStreamEnded;
  private DrmSession<ExoMediaCrypto> pendingDrmSession;
  private final boolean playClearSamplesWithoutKeys;
  private boolean waitingForKeys;
  
  public SimpleDecoderAudioRenderer()
  {
    this(null, null, new AudioProcessor[0]);
  }
  
  public SimpleDecoderAudioRenderer(Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener, AudioCapabilities paramAudioCapabilities)
  {
    this(paramHandler, paramAudioRendererEventListener, paramAudioCapabilities, null, false, new AudioProcessor[0]);
  }
  
  public SimpleDecoderAudioRenderer(Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener, AudioCapabilities paramAudioCapabilities, DrmSessionManager<ExoMediaCrypto> paramDrmSessionManager, boolean paramBoolean, AudioProcessor... paramVarArgs)
  {
    this(paramHandler, paramAudioRendererEventListener, paramDrmSessionManager, paramBoolean, new DefaultAudioSink(paramAudioCapabilities, paramVarArgs));
  }
  
  public SimpleDecoderAudioRenderer(Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener, DrmSessionManager<ExoMediaCrypto> paramDrmSessionManager, boolean paramBoolean, AudioSink paramAudioSink)
  {
    super(1);
    this.drmSessionManager = paramDrmSessionManager;
    this.playClearSamplesWithoutKeys = paramBoolean;
    this.eventDispatcher = new AudioRendererEventListener.EventDispatcher(paramHandler, paramAudioRendererEventListener);
    this.audioSink = paramAudioSink;
    paramAudioSink.setListener(new AudioSinkListener(null));
    this.formatHolder = new FormatHolder();
    this.flagsOnlyBuffer = DecoderInputBuffer.newFlagsOnlyInstance();
    this.decoderReinitializationState = 0;
    this.audioTrackNeedsConfigure = true;
  }
  
  public SimpleDecoderAudioRenderer(Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener, AudioProcessor... paramVarArgs)
  {
    this(paramHandler, paramAudioRendererEventListener, null, null, false, paramVarArgs);
  }
  
  private boolean drainOutputBuffer()
    throws ExoPlaybackException, AudioDecoderException, AudioSink.ConfigurationException, AudioSink.InitializationException, AudioSink.WriteException
  {
    boolean bool = false;
    if (this.outputBuffer == null)
    {
      this.outputBuffer = ((SimpleOutputBuffer)this.decoder.dequeueOutputBuffer());
      if (this.outputBuffer != null) {}
    }
    for (;;)
    {
      return bool;
      Object localObject = this.decoderCounters;
      ((DecoderCounters)localObject).skippedOutputBufferCount += this.outputBuffer.skippedOutputBufferCount;
      if (this.outputBuffer.isEndOfStream())
      {
        if (this.decoderReinitializationState == 2)
        {
          releaseDecoder();
          maybeInitDecoder();
          this.audioTrackNeedsConfigure = true;
        }
        else
        {
          this.outputBuffer.release();
          this.outputBuffer = null;
          processEndOfStream();
        }
      }
      else
      {
        if (this.audioTrackNeedsConfigure)
        {
          localObject = getOutputFormat();
          this.audioSink.configure(((Format)localObject).pcmEncoding, ((Format)localObject).channelCount, ((Format)localObject).sampleRate, 0, null, this.encoderDelay, this.encoderPadding);
          this.audioTrackNeedsConfigure = false;
        }
        if (this.audioSink.handleBuffer(this.outputBuffer.data, this.outputBuffer.timeUs))
        {
          localObject = this.decoderCounters;
          ((DecoderCounters)localObject).renderedOutputBufferCount += 1;
          this.outputBuffer.release();
          this.outputBuffer = null;
          bool = true;
        }
      }
    }
  }
  
  private boolean feedInputBuffer()
    throws AudioDecoderException, ExoPlaybackException
  {
    boolean bool1 = false;
    boolean bool2 = bool1;
    if (this.decoder != null)
    {
      bool2 = bool1;
      if (this.decoderReinitializationState != 2)
      {
        if (!this.inputStreamEnded) {
          break label32;
        }
        bool2 = bool1;
      }
    }
    for (;;)
    {
      return bool2;
      label32:
      if (this.inputBuffer == null)
      {
        this.inputBuffer = this.decoder.dequeueInputBuffer();
        bool2 = bool1;
        if (this.inputBuffer == null) {}
      }
      else if (this.decoderReinitializationState == 1)
      {
        this.inputBuffer.setFlags(4);
        this.decoder.queueInputBuffer(this.inputBuffer);
        this.inputBuffer = null;
        this.decoderReinitializationState = 2;
        bool2 = bool1;
      }
      else
      {
        if (this.waitingForKeys) {}
        for (int i = -4;; i = readSource(this.formatHolder, this.inputBuffer, false))
        {
          bool2 = bool1;
          if (i == -3) {
            break;
          }
          if (i != -5) {
            break label158;
          }
          onInputFormatChanged(this.formatHolder.format);
          bool2 = true;
          break;
        }
        label158:
        if (this.inputBuffer.isEndOfStream())
        {
          this.inputStreamEnded = true;
          this.decoder.queueInputBuffer(this.inputBuffer);
          this.inputBuffer = null;
          bool2 = bool1;
        }
        else
        {
          this.waitingForKeys = shouldWaitForKeys(this.inputBuffer.isEncrypted());
          bool2 = bool1;
          if (!this.waitingForKeys)
          {
            this.inputBuffer.flip();
            this.decoder.queueInputBuffer(this.inputBuffer);
            this.decoderReceivedBuffers = true;
            DecoderCounters localDecoderCounters = this.decoderCounters;
            localDecoderCounters.inputBufferCount += 1;
            this.inputBuffer = null;
            bool2 = true;
          }
        }
      }
    }
  }
  
  private void flushDecoder()
    throws ExoPlaybackException
  {
    this.waitingForKeys = false;
    if (this.decoderReinitializationState != 0)
    {
      releaseDecoder();
      maybeInitDecoder();
    }
    for (;;)
    {
      return;
      this.inputBuffer = null;
      if (this.outputBuffer != null)
      {
        this.outputBuffer.release();
        this.outputBuffer = null;
      }
      this.decoder.flush();
      this.decoderReceivedBuffers = false;
    }
  }
  
  private void maybeInitDecoder()
    throws ExoPlaybackException
  {
    if (this.decoder != null) {}
    for (;;)
    {
      return;
      this.drmSession = this.pendingDrmSession;
      Object localObject = null;
      if (this.drmSession != null)
      {
        ExoMediaCrypto localExoMediaCrypto = this.drmSession.getMediaCrypto();
        localObject = localExoMediaCrypto;
        if (localExoMediaCrypto == null)
        {
          if (this.drmSession.getError() == null) {
            continue;
          }
          localObject = localExoMediaCrypto;
        }
      }
      try
      {
        long l1 = SystemClock.elapsedRealtime();
        TraceUtil.beginSection("createAudioDecoder");
        this.decoder = createDecoder(this.inputFormat, (ExoMediaCrypto)localObject);
        TraceUtil.endSection();
        long l2 = SystemClock.elapsedRealtime();
        this.eventDispatcher.decoderInitialized(this.decoder.getName(), l2, l2 - l1);
        localObject = this.decoderCounters;
        ((DecoderCounters)localObject).decoderInitCount += 1;
      }
      catch (AudioDecoderException localAudioDecoderException)
      {
        throw ExoPlaybackException.createForRenderer(localAudioDecoderException, getIndex());
      }
    }
  }
  
  private void onInputFormatChanged(Format paramFormat)
    throws ExoPlaybackException
  {
    int i = 0;
    Object localObject = this.inputFormat;
    this.inputFormat = paramFormat;
    DrmInitData localDrmInitData = this.inputFormat.drmInitData;
    if (localObject == null)
    {
      localObject = null;
      if (Util.areEqual(localDrmInitData, localObject)) {
        break label87;
      }
      j = 1;
    }
    for (;;)
    {
      if (j != 0)
      {
        if (this.inputFormat.drmInitData == null) {
          break label195;
        }
        if (this.drmSessionManager == null)
        {
          throw ExoPlaybackException.createForRenderer(new IllegalStateException("Media requires a DrmSessionManager"), getIndex());
          localObject = ((Format)localObject).drmInitData;
          break;
          label87:
          j = 0;
          continue;
        }
        this.pendingDrmSession = this.drmSessionManager.acquireSession(Looper.myLooper(), this.inputFormat.drmInitData);
        if (this.pendingDrmSession == this.drmSession) {
          this.drmSessionManager.releaseSession(this.pendingDrmSession);
        }
      }
    }
    if (this.decoderReceivedBuffers)
    {
      this.decoderReinitializationState = 1;
      label152:
      if (paramFormat.encoderDelay != -1) {
        break label219;
      }
      j = 0;
      label163:
      this.encoderDelay = j;
      if (paramFormat.encoderPadding != -1) {
        break label228;
      }
    }
    label195:
    label219:
    label228:
    for (int j = i;; j = paramFormat.encoderPadding)
    {
      this.encoderPadding = j;
      this.eventDispatcher.inputFormatChanged(paramFormat);
      return;
      this.pendingDrmSession = null;
      break;
      releaseDecoder();
      maybeInitDecoder();
      this.audioTrackNeedsConfigure = true;
      break label152;
      j = paramFormat.encoderDelay;
      break label163;
    }
  }
  
  private void processEndOfStream()
    throws ExoPlaybackException
  {
    this.outputStreamEnded = true;
    try
    {
      this.audioSink.playToEndOfStream();
      return;
    }
    catch (AudioSink.WriteException localWriteException)
    {
      throw ExoPlaybackException.createForRenderer(localWriteException, getIndex());
    }
  }
  
  private void releaseDecoder()
  {
    if (this.decoder == null) {}
    for (;;)
    {
      return;
      this.inputBuffer = null;
      this.outputBuffer = null;
      this.decoder.release();
      this.decoder = null;
      DecoderCounters localDecoderCounters = this.decoderCounters;
      localDecoderCounters.decoderReleaseCount += 1;
      this.decoderReinitializationState = 0;
      this.decoderReceivedBuffers = false;
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
  
  private void updateCurrentPosition()
  {
    long l = this.audioSink.getCurrentPositionUs(isEnded());
    if (l != Long.MIN_VALUE) {
      if (!this.allowPositionDiscontinuity) {
        break label40;
      }
    }
    for (;;)
    {
      this.currentPositionUs = l;
      this.allowPositionDiscontinuity = false;
      return;
      label40:
      l = Math.max(this.currentPositionUs, l);
    }
  }
  
  protected abstract SimpleDecoder<DecoderInputBuffer, ? extends SimpleOutputBuffer, ? extends AudioDecoderException> createDecoder(Format paramFormat, ExoMediaCrypto paramExoMediaCrypto)
    throws AudioDecoderException;
  
  public MediaClock getMediaClock()
  {
    return this;
  }
  
  protected Format getOutputFormat()
  {
    return Format.createAudioSampleFormat(null, "audio/raw", null, -1, -1, this.inputFormat.channelCount, this.inputFormat.sampleRate, 2, null, null, 0, null);
  }
  
  public PlaybackParameters getPlaybackParameters()
  {
    return this.audioSink.getPlaybackParameters();
  }
  
  public long getPositionUs()
  {
    if (getState() == 2) {
      updateCurrentPosition();
    }
    return this.currentPositionUs;
  }
  
  public void handleMessage(int paramInt, Object paramObject)
    throws ExoPlaybackException
  {
    switch (paramInt)
    {
    default: 
      super.handleMessage(paramInt, paramObject);
    }
    for (;;)
    {
      return;
      this.audioSink.setVolume(((Float)paramObject).floatValue());
      continue;
      paramObject = (AudioAttributes)paramObject;
      this.audioSink.setAudioAttributes((AudioAttributes)paramObject);
    }
  }
  
  public boolean isEnded()
  {
    if ((this.outputStreamEnded) && (this.audioSink.isEnded())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isReady()
  {
    if ((this.audioSink.hasPendingData()) || ((this.inputFormat != null) && (!this.waitingForKeys) && ((isSourceReady()) || (this.outputBuffer != null)))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  protected void onAudioSessionId(int paramInt) {}
  
  protected void onAudioTrackPositionDiscontinuity() {}
  
  protected void onAudioTrackUnderrun(int paramInt, long paramLong1, long paramLong2) {}
  
  /* Error */
  protected void onDisabled()
  {
    // Byte code:
    //   0: aload_0
    //   1: aconst_null
    //   2: putfield 294	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:inputFormat	Lorg/telegram/messenger/exoplayer2/Format;
    //   5: aload_0
    //   6: iconst_1
    //   7: putfield 124	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:audioTrackNeedsConfigure	Z
    //   10: aload_0
    //   11: iconst_0
    //   12: putfield 233	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:waitingForKeys	Z
    //   15: aload_0
    //   16: invokespecial 169	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:releaseDecoder	()V
    //   19: aload_0
    //   20: getfield 97	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:audioSink	Lorg/telegram/messenger/exoplayer2/audio/AudioSink;
    //   23: invokeinterface 437 1 0
    //   28: aload_0
    //   29: getfield 268	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   32: ifnull +16 -> 48
    //   35: aload_0
    //   36: getfield 86	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSessionManager	Lorg/telegram/messenger/exoplayer2/drm/DrmSessionManager;
    //   39: aload_0
    //   40: getfield 268	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   43: invokeinterface 352 2 0
    //   48: aload_0
    //   49: getfield 266	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   52: ifnull +27 -> 79
    //   55: aload_0
    //   56: getfield 266	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   59: aload_0
    //   60: getfield 268	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   63: if_acmpeq +16 -> 79
    //   66: aload_0
    //   67: getfield 86	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSessionManager	Lorg/telegram/messenger/exoplayer2/drm/DrmSessionManager;
    //   70: aload_0
    //   71: getfield 266	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   74: invokeinterface 352 2 0
    //   79: aload_0
    //   80: aconst_null
    //   81: putfield 268	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   84: aload_0
    //   85: aconst_null
    //   86: putfield 266	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   89: aload_0
    //   90: getfield 157	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lorg/telegram/messenger/exoplayer2/decoder/DecoderCounters;
    //   93: invokevirtual 440	org/telegram/messenger/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   96: aload_0
    //   97: getfield 95	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:eventDispatcher	Lorg/telegram/messenger/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   100: aload_0
    //   101: getfield 157	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lorg/telegram/messenger/exoplayer2/decoder/DecoderCounters;
    //   104: invokevirtual 444	org/telegram/messenger/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lorg/telegram/messenger/exoplayer2/decoder/DecoderCounters;)V
    //   107: return
    //   108: astore_1
    //   109: aload_0
    //   110: aconst_null
    //   111: putfield 268	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   114: aload_0
    //   115: aconst_null
    //   116: putfield 266	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   119: aload_0
    //   120: getfield 157	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lorg/telegram/messenger/exoplayer2/decoder/DecoderCounters;
    //   123: invokevirtual 440	org/telegram/messenger/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   126: aload_0
    //   127: getfield 95	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:eventDispatcher	Lorg/telegram/messenger/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   130: aload_0
    //   131: getfield 157	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lorg/telegram/messenger/exoplayer2/decoder/DecoderCounters;
    //   134: invokevirtual 444	org/telegram/messenger/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lorg/telegram/messenger/exoplayer2/decoder/DecoderCounters;)V
    //   137: aload_1
    //   138: athrow
    //   139: astore_1
    //   140: aload_0
    //   141: getfield 266	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   144: ifnull +27 -> 171
    //   147: aload_0
    //   148: getfield 266	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   151: aload_0
    //   152: getfield 268	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   155: if_acmpeq +16 -> 171
    //   158: aload_0
    //   159: getfield 86	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSessionManager	Lorg/telegram/messenger/exoplayer2/drm/DrmSessionManager;
    //   162: aload_0
    //   163: getfield 266	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   166: invokeinterface 352 2 0
    //   171: aload_0
    //   172: aconst_null
    //   173: putfield 268	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   176: aload_0
    //   177: aconst_null
    //   178: putfield 266	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   181: aload_0
    //   182: getfield 157	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lorg/telegram/messenger/exoplayer2/decoder/DecoderCounters;
    //   185: invokevirtual 440	org/telegram/messenger/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   188: aload_0
    //   189: getfield 95	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:eventDispatcher	Lorg/telegram/messenger/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   192: aload_0
    //   193: getfield 157	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lorg/telegram/messenger/exoplayer2/decoder/DecoderCounters;
    //   196: invokevirtual 444	org/telegram/messenger/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lorg/telegram/messenger/exoplayer2/decoder/DecoderCounters;)V
    //   199: aload_1
    //   200: athrow
    //   201: astore_1
    //   202: aload_0
    //   203: aconst_null
    //   204: putfield 268	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   207: aload_0
    //   208: aconst_null
    //   209: putfield 266	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   212: aload_0
    //   213: getfield 157	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lorg/telegram/messenger/exoplayer2/decoder/DecoderCounters;
    //   216: invokevirtual 440	org/telegram/messenger/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   219: aload_0
    //   220: getfield 95	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:eventDispatcher	Lorg/telegram/messenger/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   223: aload_0
    //   224: getfield 157	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lorg/telegram/messenger/exoplayer2/decoder/DecoderCounters;
    //   227: invokevirtual 444	org/telegram/messenger/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lorg/telegram/messenger/exoplayer2/decoder/DecoderCounters;)V
    //   230: aload_1
    //   231: athrow
    //   232: astore_1
    //   233: aload_0
    //   234: getfield 268	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   237: ifnull +16 -> 253
    //   240: aload_0
    //   241: getfield 86	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSessionManager	Lorg/telegram/messenger/exoplayer2/drm/DrmSessionManager;
    //   244: aload_0
    //   245: getfield 268	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   248: invokeinterface 352 2 0
    //   253: aload_0
    //   254: getfield 266	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   257: ifnull +27 -> 284
    //   260: aload_0
    //   261: getfield 266	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   264: aload_0
    //   265: getfield 268	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   268: if_acmpeq +16 -> 284
    //   271: aload_0
    //   272: getfield 86	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSessionManager	Lorg/telegram/messenger/exoplayer2/drm/DrmSessionManager;
    //   275: aload_0
    //   276: getfield 266	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   279: invokeinterface 352 2 0
    //   284: aload_0
    //   285: aconst_null
    //   286: putfield 268	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   289: aload_0
    //   290: aconst_null
    //   291: putfield 266	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   294: aload_0
    //   295: getfield 157	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lorg/telegram/messenger/exoplayer2/decoder/DecoderCounters;
    //   298: invokevirtual 440	org/telegram/messenger/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   301: aload_0
    //   302: getfield 95	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:eventDispatcher	Lorg/telegram/messenger/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   305: aload_0
    //   306: getfield 157	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lorg/telegram/messenger/exoplayer2/decoder/DecoderCounters;
    //   309: invokevirtual 444	org/telegram/messenger/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lorg/telegram/messenger/exoplayer2/decoder/DecoderCounters;)V
    //   312: aload_1
    //   313: athrow
    //   314: astore_1
    //   315: aload_0
    //   316: aconst_null
    //   317: putfield 268	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   320: aload_0
    //   321: aconst_null
    //   322: putfield 266	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   325: aload_0
    //   326: getfield 157	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lorg/telegram/messenger/exoplayer2/decoder/DecoderCounters;
    //   329: invokevirtual 440	org/telegram/messenger/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   332: aload_0
    //   333: getfield 95	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:eventDispatcher	Lorg/telegram/messenger/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   336: aload_0
    //   337: getfield 157	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lorg/telegram/messenger/exoplayer2/decoder/DecoderCounters;
    //   340: invokevirtual 444	org/telegram/messenger/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lorg/telegram/messenger/exoplayer2/decoder/DecoderCounters;)V
    //   343: aload_1
    //   344: athrow
    //   345: astore_1
    //   346: aload_0
    //   347: getfield 266	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   350: ifnull +27 -> 377
    //   353: aload_0
    //   354: getfield 266	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   357: aload_0
    //   358: getfield 268	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   361: if_acmpeq +16 -> 377
    //   364: aload_0
    //   365: getfield 86	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSessionManager	Lorg/telegram/messenger/exoplayer2/drm/DrmSessionManager;
    //   368: aload_0
    //   369: getfield 266	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   372: invokeinterface 352 2 0
    //   377: aload_0
    //   378: aconst_null
    //   379: putfield 268	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   382: aload_0
    //   383: aconst_null
    //   384: putfield 266	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   387: aload_0
    //   388: getfield 157	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lorg/telegram/messenger/exoplayer2/decoder/DecoderCounters;
    //   391: invokevirtual 440	org/telegram/messenger/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   394: aload_0
    //   395: getfield 95	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:eventDispatcher	Lorg/telegram/messenger/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   398: aload_0
    //   399: getfield 157	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lorg/telegram/messenger/exoplayer2/decoder/DecoderCounters;
    //   402: invokevirtual 444	org/telegram/messenger/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lorg/telegram/messenger/exoplayer2/decoder/DecoderCounters;)V
    //   405: aload_1
    //   406: athrow
    //   407: astore_1
    //   408: aload_0
    //   409: aconst_null
    //   410: putfield 268	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   413: aload_0
    //   414: aconst_null
    //   415: putfield 266	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lorg/telegram/messenger/exoplayer2/drm/DrmSession;
    //   418: aload_0
    //   419: getfield 157	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lorg/telegram/messenger/exoplayer2/decoder/DecoderCounters;
    //   422: invokevirtual 440	org/telegram/messenger/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   425: aload_0
    //   426: getfield 95	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:eventDispatcher	Lorg/telegram/messenger/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   429: aload_0
    //   430: getfield 157	org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lorg/telegram/messenger/exoplayer2/decoder/DecoderCounters;
    //   433: invokevirtual 444	org/telegram/messenger/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:disabled	(Lorg/telegram/messenger/exoplayer2/decoder/DecoderCounters;)V
    //   436: aload_1
    //   437: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	438	0	this	SimpleDecoderAudioRenderer
    //   108	30	1	localObject1	Object
    //   139	61	1	localObject2	Object
    //   201	30	1	localObject3	Object
    //   232	81	1	localObject4	Object
    //   314	30	1	localObject5	Object
    //   345	61	1	localObject6	Object
    //   407	30	1	localObject7	Object
    // Exception table:
    //   from	to	target	type
    //   48	79	108	finally
    //   28	48	139	finally
    //   140	171	201	finally
    //   15	28	232	finally
    //   253	284	314	finally
    //   233	253	345	finally
    //   346	377	407	finally
  }
  
  protected void onEnabled(boolean paramBoolean)
    throws ExoPlaybackException
  {
    this.decoderCounters = new DecoderCounters();
    this.eventDispatcher.enabled(this.decoderCounters);
    int i = getConfiguration().tunnelingAudioSessionId;
    if (i != 0) {
      this.audioSink.enableTunnelingV21(i);
    }
    for (;;)
    {
      return;
      this.audioSink.disableTunneling();
    }
  }
  
  protected void onPositionReset(long paramLong, boolean paramBoolean)
    throws ExoPlaybackException
  {
    this.audioSink.reset();
    this.currentPositionUs = paramLong;
    this.allowPositionDiscontinuity = true;
    this.inputStreamEnded = false;
    this.outputStreamEnded = false;
    if (this.decoder != null) {
      flushDecoder();
    }
  }
  
  protected void onStarted()
  {
    this.audioSink.play();
  }
  
  protected void onStopped()
  {
    this.audioSink.pause();
    updateCurrentPosition();
  }
  
  public void render(long paramLong1, long paramLong2)
    throws ExoPlaybackException
  {
    if (this.outputStreamEnded) {}
    for (;;)
    {
      try
      {
        this.audioSink.playToEndOfStream();
        return;
      }
      catch (AudioSink.WriteException localWriteException1)
      {
        throw ExoPlaybackException.createForRenderer(localWriteException1, getIndex());
      }
      int i;
      if (this.inputFormat == null)
      {
        this.flagsOnlyBuffer.clear();
        i = readSource(this.formatHolder, this.flagsOnlyBuffer, true);
        if (i == -5) {
          onInputFormatChanged(this.formatHolder.format);
        }
      }
      else
      {
        maybeInitDecoder();
        if (this.decoder == null) {
          continue;
        }
      }
      try
      {
        TraceUtil.beginSection("drainAndFeed");
        while (drainOutputBuffer()) {}
        while (feedInputBuffer()) {}
        TraceUtil.endSection();
        this.decoderCounters.ensureUpdated();
      }
      catch (AudioSink.WriteException localWriteException2)
      {
        throw ExoPlaybackException.createForRenderer(localWriteException2, getIndex());
      }
      catch (AudioDecoderException localAudioDecoderException)
      {
        for (;;) {}
      }
      catch (AudioSink.ConfigurationException localConfigurationException)
      {
        for (;;) {}
      }
      catch (AudioSink.InitializationException localInitializationException)
      {
        for (;;) {}
      }
      if (i == -4)
      {
        Assertions.checkState(this.flagsOnlyBuffer.isEndOfStream());
        this.inputStreamEnded = true;
        processEndOfStream();
      }
    }
  }
  
  public PlaybackParameters setPlaybackParameters(PlaybackParameters paramPlaybackParameters)
  {
    return this.audioSink.setPlaybackParameters(paramPlaybackParameters);
  }
  
  public final int supportsFormat(Format paramFormat)
  {
    int i = supportsFormatInternal(this.drmSessionManager, paramFormat);
    if (i <= 2)
    {
      j = i;
      return j;
    }
    if (Util.SDK_INT >= 21) {}
    for (int j = 32;; j = 0)
    {
      j = i | j | 0x8;
      break;
    }
  }
  
  protected abstract int supportsFormatInternal(DrmSessionManager<ExoMediaCrypto> paramDrmSessionManager, Format paramFormat);
  
  protected final boolean supportsOutputEncoding(int paramInt)
  {
    return this.audioSink.isEncodingSupported(paramInt);
  }
  
  private final class AudioSinkListener
    implements AudioSink.Listener
  {
    private AudioSinkListener() {}
    
    public void onAudioSessionId(int paramInt)
    {
      SimpleDecoderAudioRenderer.this.eventDispatcher.audioSessionId(paramInt);
      SimpleDecoderAudioRenderer.this.onAudioSessionId(paramInt);
    }
    
    public void onPositionDiscontinuity()
    {
      SimpleDecoderAudioRenderer.this.onAudioTrackPositionDiscontinuity();
      SimpleDecoderAudioRenderer.access$202(SimpleDecoderAudioRenderer.this, true);
    }
    
    public void onUnderrun(int paramInt, long paramLong1, long paramLong2)
    {
      SimpleDecoderAudioRenderer.this.eventDispatcher.audioTrackUnderrun(paramInt, paramLong1, paramLong2);
      SimpleDecoderAudioRenderer.this.onAudioTrackUnderrun(paramInt, paramLong1, paramLong2);
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface ReinitializationState {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */