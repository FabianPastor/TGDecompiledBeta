package org.telegram.messenger.exoplayer2.audio;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.AudioAttributes.Builder;
import android.media.AudioFormat;
import android.media.AudioFormat.Builder;
import android.media.AudioTimestamp;
import android.media.AudioTrack;
import android.os.ConditionVariable;
import android.os.SystemClock;
import android.util.Log;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.PlaybackParameters;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

public final class DefaultAudioSink
  implements AudioSink
{
  private static final int BUFFER_MULTIPLICATION_FACTOR = 4;
  private static final int ERROR_BAD_VALUE = -2;
  private static final long MAX_AUDIO_TIMESTAMP_OFFSET_US = 5000000L;
  private static final long MAX_BUFFER_DURATION_US = 750000L;
  private static final long MAX_LATENCY_US = 5000000L;
  private static final int MAX_PLAYHEAD_OFFSET_COUNT = 10;
  private static final long MIN_BUFFER_DURATION_US = 250000L;
  private static final int MIN_PLAYHEAD_OFFSET_SAMPLE_INTERVAL_US = 30000;
  private static final int MIN_TIMESTAMP_SAMPLE_INTERVAL_US = 500000;
  private static final int MODE_STATIC = 0;
  private static final int MODE_STREAM = 1;
  private static final long PASSTHROUGH_BUFFER_DURATION_US = 250000L;
  private static final int PLAYSTATE_PAUSED = 2;
  private static final int PLAYSTATE_PLAYING = 3;
  private static final int PLAYSTATE_STOPPED = 1;
  private static final int START_IN_SYNC = 1;
  private static final int START_NEED_SYNC = 2;
  private static final int START_NOT_SET = 0;
  private static final int STATE_INITIALIZED = 1;
  private static final String TAG = "AudioTrack";
  @SuppressLint({"InlinedApi"})
  private static final int WRITE_NON_BLOCKING = 1;
  public static boolean enablePreV21AudioSessionWorkaround = false;
  public static boolean failOnSpuriousAudioTimestamp = false;
  private AudioAttributes audioAttributes;
  private final AudioCapabilities audioCapabilities;
  private AudioProcessor[] audioProcessors;
  private int audioSessionId;
  private boolean audioTimestampSet;
  private AudioTrack audioTrack;
  private final AudioTrackUtil audioTrackUtil;
  private ByteBuffer avSyncHeader;
  private int bufferSize;
  private long bufferSizeUs;
  private int bytesUntilNextAvSync;
  private boolean canApplyPlaybackParameters;
  private int channelConfig;
  private final ChannelMappingAudioProcessor channelMappingAudioProcessor;
  private int drainingAudioProcessorIndex;
  private PlaybackParameters drainingPlaybackParameters;
  private final boolean enableConvertHighResIntPcmToFloat;
  private int framesPerEncodedSample;
  private Method getLatencyMethod;
  private boolean handledEndOfStream;
  private boolean hasData;
  private ByteBuffer inputBuffer;
  private int inputSampleRate;
  private boolean isInputPcm;
  private AudioTrack keepSessionIdAudioTrack;
  private long lastFeedElapsedRealtimeMs;
  private long lastPlayheadSampleTimeUs;
  private long lastTimestampSampleTimeUs;
  private long latencyUs;
  private AudioSink.Listener listener;
  private int nextPlayheadOffsetIndex;
  private ByteBuffer outputBuffer;
  private ByteBuffer[] outputBuffers;
  private int outputEncoding;
  private int outputPcmFrameSize;
  private int pcmFrameSize;
  private PlaybackParameters playbackParameters;
  private final ArrayDeque<PlaybackParametersCheckpoint> playbackParametersCheckpoints;
  private long playbackParametersOffsetUs;
  private long playbackParametersPositionUs;
  private int playheadOffsetCount;
  private final long[] playheadOffsets;
  private boolean playing;
  private byte[] preV21OutputBuffer;
  private int preV21OutputBufferOffset;
  private boolean processingEnabled;
  private final ConditionVariable releasingConditionVariable;
  private long resumeSystemTimeUs;
  private int sampleRate;
  private boolean shouldConvertHighResIntPcmToFloat;
  private long smoothedPlayheadOffsetUs;
  private final SonicAudioProcessor sonicAudioProcessor;
  private int startMediaTimeState;
  private long startMediaTimeUs;
  private long submittedEncodedFrames;
  private long submittedPcmBytes;
  private final AudioProcessor[] toFloatPcmAvailableAudioProcessors;
  private final AudioProcessor[] toIntPcmAvailableAudioProcessors;
  private final TrimmingAudioProcessor trimmingAudioProcessor;
  private boolean tunneling;
  private float volume;
  private long writtenEncodedFrames;
  private long writtenPcmBytes;
  
  public DefaultAudioSink(AudioCapabilities paramAudioCapabilities, AudioProcessor[] paramArrayOfAudioProcessor)
  {
    this(paramAudioCapabilities, paramArrayOfAudioProcessor, false);
  }
  
  public DefaultAudioSink(AudioCapabilities paramAudioCapabilities, AudioProcessor[] paramArrayOfAudioProcessor, boolean paramBoolean)
  {
    this.audioCapabilities = paramAudioCapabilities;
    this.enableConvertHighResIntPcmToFloat = paramBoolean;
    this.releasingConditionVariable = new ConditionVariable(true);
    if (Util.SDK_INT >= 18) {}
    try
    {
      this.getLatencyMethod = AudioTrack.class.getMethod("getLatency", (Class[])null);
      if (Util.SDK_INT >= 19) {}
      for (this.audioTrackUtil = new AudioTrackUtilV19();; this.audioTrackUtil = new AudioTrackUtil(null))
      {
        this.channelMappingAudioProcessor = new ChannelMappingAudioProcessor();
        this.trimmingAudioProcessor = new TrimmingAudioProcessor();
        this.sonicAudioProcessor = new SonicAudioProcessor();
        this.toIntPcmAvailableAudioProcessors = new AudioProcessor[paramArrayOfAudioProcessor.length + 4];
        this.toIntPcmAvailableAudioProcessors[0] = new ResamplingAudioProcessor();
        this.toIntPcmAvailableAudioProcessors[1] = this.channelMappingAudioProcessor;
        this.toIntPcmAvailableAudioProcessors[2] = this.trimmingAudioProcessor;
        System.arraycopy(paramArrayOfAudioProcessor, 0, this.toIntPcmAvailableAudioProcessors, 3, paramArrayOfAudioProcessor.length);
        this.toIntPcmAvailableAudioProcessors[(paramArrayOfAudioProcessor.length + 3)] = this.sonicAudioProcessor;
        this.toFloatPcmAvailableAudioProcessors = new AudioProcessor[] { new FloatResamplingAudioProcessor() };
        this.playheadOffsets = new long[10];
        this.volume = 1.0F;
        this.startMediaTimeState = 0;
        this.audioAttributes = AudioAttributes.DEFAULT;
        this.audioSessionId = 0;
        this.playbackParameters = PlaybackParameters.DEFAULT;
        this.drainingAudioProcessorIndex = -1;
        this.audioProcessors = new AudioProcessor[0];
        this.outputBuffers = new ByteBuffer[0];
        this.playbackParametersCheckpoints = new ArrayDeque();
        return;
      }
    }
    catch (NoSuchMethodException paramAudioCapabilities)
    {
      for (;;) {}
    }
  }
  
  private long applySpeedup(long paramLong)
  {
    while ((!this.playbackParametersCheckpoints.isEmpty()) && (paramLong >= ((PlaybackParametersCheckpoint)this.playbackParametersCheckpoints.getFirst()).positionUs))
    {
      PlaybackParametersCheckpoint localPlaybackParametersCheckpoint = (PlaybackParametersCheckpoint)this.playbackParametersCheckpoints.remove();
      this.playbackParameters = localPlaybackParametersCheckpoint.playbackParameters;
      this.playbackParametersPositionUs = localPlaybackParametersCheckpoint.positionUs;
      this.playbackParametersOffsetUs = (localPlaybackParametersCheckpoint.mediaTimeUs - this.startMediaTimeUs);
    }
    if (this.playbackParameters.speed == 1.0F) {
      paramLong = this.playbackParametersOffsetUs + paramLong - this.playbackParametersPositionUs;
    }
    for (;;)
    {
      return paramLong;
      if (this.playbackParametersCheckpoints.isEmpty()) {
        paramLong = this.playbackParametersOffsetUs + this.sonicAudioProcessor.scaleDurationForSpeedup(paramLong - this.playbackParametersPositionUs);
      } else {
        paramLong = this.playbackParametersOffsetUs + Util.getMediaDurationForPlayoutDuration(paramLong - this.playbackParametersPositionUs, this.playbackParameters.speed);
      }
    }
  }
  
  @TargetApi(21)
  private AudioTrack createAudioTrackV21()
  {
    android.media.AudioAttributes localAudioAttributes;
    AudioFormat localAudioFormat;
    if (this.tunneling)
    {
      localAudioAttributes = new AudioAttributes.Builder().setContentType(3).setFlags(16).setUsage(1).build();
      localAudioFormat = new AudioFormat.Builder().setChannelMask(this.channelConfig).setEncoding(this.outputEncoding).setSampleRate(this.sampleRate).build();
      if (this.audioSessionId == 0) {
        break label102;
      }
    }
    label102:
    for (int i = this.audioSessionId;; i = 0)
    {
      return new AudioTrack(localAudioAttributes, localAudioFormat, this.bufferSize, 1, i);
      localAudioAttributes = this.audioAttributes.getAudioAttributesV21();
      break;
    }
  }
  
  private boolean drainAudioProcessorsToEndOfStream()
    throws AudioSink.WriteException
  {
    boolean bool = false;
    int i = 0;
    if (this.drainingAudioProcessorIndex == -1)
    {
      if (this.processingEnabled)
      {
        i = 0;
        this.drainingAudioProcessorIndex = i;
        i = 1;
      }
    }
    else
    {
      label28:
      if (this.drainingAudioProcessorIndex >= this.audioProcessors.length) {
        break label102;
      }
      AudioProcessor localAudioProcessor = this.audioProcessors[this.drainingAudioProcessorIndex];
      if (i != 0) {
        localAudioProcessor.queueEndOfStream();
      }
      processBuffers(-9223372036854775807L);
      if (localAudioProcessor.isEnded()) {
        break label87;
      }
    }
    for (;;)
    {
      return bool;
      i = this.audioProcessors.length;
      break;
      label87:
      i = 1;
      this.drainingAudioProcessorIndex += 1;
      break label28;
      label102:
      if (this.outputBuffer != null)
      {
        writeBuffer(this.outputBuffer, -9223372036854775807L);
        if (this.outputBuffer != null) {}
      }
      else
      {
        this.drainingAudioProcessorIndex = -1;
        bool = true;
      }
    }
  }
  
  private long durationUsToFrames(long paramLong)
  {
    return this.sampleRate * paramLong / 1000000L;
  }
  
  private long framesToDurationUs(long paramLong)
  {
    return 1000000L * paramLong / this.sampleRate;
  }
  
  private AudioProcessor[] getAvailableAudioProcessors()
  {
    if (this.shouldConvertHighResIntPcmToFloat) {}
    for (AudioProcessor[] arrayOfAudioProcessor = this.toFloatPcmAvailableAudioProcessors;; arrayOfAudioProcessor = this.toIntPcmAvailableAudioProcessors) {
      return arrayOfAudioProcessor;
    }
  }
  
  private static int getFramesPerEncodedSample(int paramInt, ByteBuffer paramByteBuffer)
  {
    if ((paramInt == 7) || (paramInt == 8)) {
      paramInt = DtsUtil.parseDtsAudioSampleCount(paramByteBuffer);
    }
    for (;;)
    {
      return paramInt;
      if (paramInt == 5)
      {
        paramInt = Ac3Util.getAc3SyncframeAudioSampleCount();
      }
      else if (paramInt == 6)
      {
        paramInt = Ac3Util.parseEAc3SyncframeAudioSampleCount(paramByteBuffer);
      }
      else
      {
        if (paramInt != 14) {
          break;
        }
        paramInt = Ac3Util.parseTrueHdSyncframeAudioSampleCount(paramByteBuffer) * 8;
      }
    }
    throw new IllegalStateException("Unexpected audio encoding: " + paramInt);
  }
  
  private long getSubmittedFrames()
  {
    if (this.isInputPcm) {}
    for (long l = this.submittedPcmBytes / this.pcmFrameSize;; l = this.submittedEncodedFrames) {
      return l;
    }
  }
  
  private long getWrittenFrames()
  {
    if (this.isInputPcm) {}
    for (long l = this.writtenPcmBytes / this.outputPcmFrameSize;; l = this.writtenEncodedFrames) {
      return l;
    }
  }
  
  private boolean hasCurrentPositionUs()
  {
    if ((isInitialized()) && (this.startMediaTimeState != 0)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private void initialize()
    throws AudioSink.InitializationException
  {
    this.releasingConditionVariable.block();
    this.audioTrack = initializeAudioTrack();
    setPlaybackParameters(this.playbackParameters);
    resetAudioProcessors();
    int i = this.audioTrack.getAudioSessionId();
    if ((enablePreV21AudioSessionWorkaround) && (Util.SDK_INT < 21))
    {
      if ((this.keepSessionIdAudioTrack != null) && (i != this.keepSessionIdAudioTrack.getAudioSessionId())) {
        releaseKeepSessionIdAudioTrack();
      }
      if (this.keepSessionIdAudioTrack == null) {
        this.keepSessionIdAudioTrack = initializeKeepSessionIdAudioTrack(i);
      }
    }
    if (this.audioSessionId != i)
    {
      this.audioSessionId = i;
      if (this.listener != null) {
        this.listener.onAudioSessionId(i);
      }
    }
    this.audioTrackUtil.reconfigure(this.audioTrack, needsPassthroughWorkarounds());
    setVolumeInternal();
    this.hasData = false;
  }
  
  private AudioTrack initializeAudioTrack()
    throws AudioSink.InitializationException
  {
    AudioTrack localAudioTrack;
    if (Util.SDK_INT >= 21) {
      localAudioTrack = createAudioTrackV21();
    }
    for (;;)
    {
      int i = localAudioTrack.getState();
      if (i != 1) {
        try
        {
          localAudioTrack.release();
          throw new AudioSink.InitializationException(i, this.sampleRate, this.channelConfig, this.bufferSize);
          i = Util.getStreamTypeForAudioUsage(this.audioAttributes.usage);
          if (this.audioSessionId == 0) {
            localAudioTrack = new AudioTrack(i, this.sampleRate, this.channelConfig, this.outputEncoding, this.bufferSize, 1);
          } else {
            localAudioTrack = new AudioTrack(i, this.sampleRate, this.channelConfig, this.outputEncoding, this.bufferSize, 1, this.audioSessionId);
          }
        }
        catch (Exception localException)
        {
          for (;;) {}
        }
      }
    }
    return localException;
  }
  
  private AudioTrack initializeKeepSessionIdAudioTrack(int paramInt)
  {
    return new AudioTrack(3, 4000, 4, 2, 2, 0, paramInt);
  }
  
  private long inputFramesToDurationUs(long paramLong)
  {
    return 1000000L * paramLong / this.inputSampleRate;
  }
  
  private static boolean isEncodingPcm(int paramInt)
  {
    if ((paramInt == 3) || (paramInt == 2) || (paramInt == Integer.MIN_VALUE) || (paramInt == NUM) || (paramInt == 4)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private boolean isInitialized()
  {
    if (this.audioTrack != null) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private void maybeSampleSyncParams()
  {
    long l1 = this.audioTrackUtil.getPositionUs();
    if (l1 == 0L) {}
    for (;;)
    {
      return;
      long l2 = System.nanoTime() / 1000L;
      if (l2 - this.lastPlayheadSampleTimeUs >= 30000L)
      {
        this.playheadOffsets[this.nextPlayheadOffsetIndex] = (l1 - l2);
        this.nextPlayheadOffsetIndex = ((this.nextPlayheadOffsetIndex + 1) % 10);
        if (this.playheadOffsetCount < 10) {
          this.playheadOffsetCount += 1;
        }
        this.lastPlayheadSampleTimeUs = l2;
        this.smoothedPlayheadOffsetUs = 0L;
        for (int i = 0; i < this.playheadOffsetCount; i++) {
          this.smoothedPlayheadOffsetUs += this.playheadOffsets[i] / this.playheadOffsetCount;
        }
      }
      if ((needsPassthroughWorkarounds()) || (l2 - this.lastTimestampSampleTimeUs < 500000L)) {
        continue;
      }
      this.audioTimestampSet = this.audioTrackUtil.updateTimestamp();
      long l3;
      long l4;
      if (this.audioTimestampSet)
      {
        l3 = this.audioTrackUtil.getTimestampNanoTime() / 1000L;
        l4 = this.audioTrackUtil.getTimestampFramePosition();
        if (l3 < this.resumeSystemTimeUs) {
          this.audioTimestampSet = false;
        }
      }
      else
      {
        label205:
        if ((this.getLatencyMethod == null) || (!this.isInputPcm)) {}
      }
      try
      {
        this.latencyUs = (((Integer)this.getLatencyMethod.invoke(this.audioTrack, (Object[])null)).intValue() * 1000L - this.bufferSizeUs);
        this.latencyUs = Math.max(this.latencyUs, 0L);
        if (this.latencyUs > 5000000L)
        {
          localObject = new java/lang/StringBuilder;
          ((StringBuilder)localObject).<init>();
          Log.w("AudioTrack", "Ignoring impossibly large audio latency: " + this.latencyUs);
          this.latencyUs = 0L;
        }
        this.lastTimestampSampleTimeUs = l2;
        continue;
        if (Math.abs(l3 - l2) > 5000000L)
        {
          localObject = "Spurious audio timestamp (system clock mismatch): " + l4 + ", " + l3 + ", " + l2 + ", " + l1 + ", " + getSubmittedFrames() + ", " + getWrittenFrames();
          if (failOnSpuriousAudioTimestamp) {
            throw new InvalidAudioTrackTimestampException((String)localObject);
          }
          Log.w("AudioTrack", (String)localObject);
          this.audioTimestampSet = false;
          break label205;
        }
        if (Math.abs(framesToDurationUs(l4) - l1) <= 5000000L) {
          break label205;
        }
        Object localObject = "Spurious audio timestamp (frame position mismatch): " + l4 + ", " + l3 + ", " + l2 + ", " + l1 + ", " + getSubmittedFrames() + ", " + getWrittenFrames();
        if (failOnSpuriousAudioTimestamp) {
          throw new InvalidAudioTrackTimestampException((String)localObject);
        }
        Log.w("AudioTrack", (String)localObject);
        this.audioTimestampSet = false;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          this.getLatencyMethod = null;
        }
      }
    }
  }
  
  private boolean needsPassthroughWorkarounds()
  {
    if ((Util.SDK_INT < 23) && ((this.outputEncoding == 5) || (this.outputEncoding == 6))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private boolean overrideHasPendingData()
  {
    if ((needsPassthroughWorkarounds()) && (this.audioTrack.getPlayState() == 2) && (this.audioTrack.getPlaybackHeadPosition() == 0)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private void processBuffers(long paramLong)
    throws AudioSink.WriteException
  {
    int i = this.audioProcessors.length;
    int j = i;
    for (;;)
    {
      ByteBuffer localByteBuffer;
      if (j >= 0)
      {
        if (j <= 0) {
          break label52;
        }
        localByteBuffer = this.outputBuffers[(j - 1)];
        label30:
        if (j != i) {
          break label76;
        }
        writeBuffer(localByteBuffer, paramLong);
      }
      for (;;)
      {
        if (localByteBuffer.hasRemaining())
        {
          return;
          label52:
          if (this.inputBuffer != null)
          {
            localByteBuffer = this.inputBuffer;
            break label30;
          }
          localByteBuffer = AudioProcessor.EMPTY_BUFFER;
          break label30;
          label76:
          Object localObject = this.audioProcessors[j];
          ((AudioProcessor)localObject).queueInput(localByteBuffer);
          localObject = ((AudioProcessor)localObject).getOutput();
          this.outputBuffers[j] = localObject;
          if (((ByteBuffer)localObject).hasRemaining())
          {
            j++;
            break;
          }
        }
      }
      j--;
    }
  }
  
  private void releaseKeepSessionIdAudioTrack()
  {
    if (this.keepSessionIdAudioTrack == null) {}
    for (;;)
    {
      return;
      final AudioTrack localAudioTrack = this.keepSessionIdAudioTrack;
      this.keepSessionIdAudioTrack = null;
      new Thread()
      {
        public void run()
        {
          localAudioTrack.release();
        }
      }.start();
    }
  }
  
  private void resetAudioProcessors()
  {
    ArrayList localArrayList = new ArrayList();
    AudioProcessor[] arrayOfAudioProcessor = getAvailableAudioProcessors();
    int i = arrayOfAudioProcessor.length;
    int j = 0;
    AudioProcessor localAudioProcessor;
    if (j < i)
    {
      localAudioProcessor = arrayOfAudioProcessor[j];
      if (localAudioProcessor.isActive()) {
        localArrayList.add(localAudioProcessor);
      }
      for (;;)
      {
        j++;
        break;
        localAudioProcessor.flush();
      }
    }
    i = localArrayList.size();
    this.audioProcessors = ((AudioProcessor[])localArrayList.toArray(new AudioProcessor[i]));
    this.outputBuffers = new ByteBuffer[i];
    for (j = 0; j < i; j++)
    {
      localAudioProcessor = this.audioProcessors[j];
      localAudioProcessor.flush();
      this.outputBuffers[j] = localAudioProcessor.getOutput();
    }
  }
  
  private void resetSyncParams()
  {
    this.smoothedPlayheadOffsetUs = 0L;
    this.playheadOffsetCount = 0;
    this.nextPlayheadOffsetIndex = 0;
    this.lastPlayheadSampleTimeUs = 0L;
    this.audioTimestampSet = false;
    this.lastTimestampSampleTimeUs = 0L;
  }
  
  private void setVolumeInternal()
  {
    if (!isInitialized()) {}
    for (;;)
    {
      return;
      if (Util.SDK_INT >= 21) {
        setVolumeInternalV21(this.audioTrack, this.volume);
      } else {
        setVolumeInternalV3(this.audioTrack, this.volume);
      }
    }
  }
  
  @TargetApi(21)
  private static void setVolumeInternalV21(AudioTrack paramAudioTrack, float paramFloat)
  {
    paramAudioTrack.setVolume(paramFloat);
  }
  
  private static void setVolumeInternalV3(AudioTrack paramAudioTrack, float paramFloat)
  {
    paramAudioTrack.setStereoVolume(paramFloat, paramFloat);
  }
  
  private void writeBuffer(ByteBuffer paramByteBuffer, long paramLong)
    throws AudioSink.WriteException
  {
    boolean bool1 = true;
    if (!paramByteBuffer.hasRemaining()) {}
    for (;;)
    {
      return;
      boolean bool2;
      label34:
      int i;
      int j;
      int k;
      if (this.outputBuffer != null) {
        if (this.outputBuffer == paramByteBuffer)
        {
          bool2 = true;
          Assertions.checkArgument(bool2);
          i = paramByteBuffer.remaining();
          j = 0;
          if (Util.SDK_INT >= 21) {
            break label255;
          }
          k = (int)(this.writtenPcmBytes - this.audioTrackUtil.getPlaybackHeadPosition() * this.outputPcmFrameSize);
          k = this.bufferSize - k;
          if (k > 0)
          {
            j = Math.min(i, k);
            k = this.audioTrack.write(this.preV21OutputBuffer, this.preV21OutputBufferOffset, j);
            j = k;
            if (k > 0)
            {
              this.preV21OutputBufferOffset += k;
              paramByteBuffer.position(paramByteBuffer.position() + k);
              j = k;
            }
          }
        }
      }
      for (;;)
      {
        this.lastFeedElapsedRealtimeMs = SystemClock.elapsedRealtime();
        if (j >= 0) {
          break label317;
        }
        throw new AudioSink.WriteException(j);
        bool2 = false;
        break;
        this.outputBuffer = paramByteBuffer;
        if (Util.SDK_INT >= 21) {
          break label34;
        }
        j = paramByteBuffer.remaining();
        if ((this.preV21OutputBuffer == null) || (this.preV21OutputBuffer.length < j)) {
          this.preV21OutputBuffer = new byte[j];
        }
        k = paramByteBuffer.position();
        paramByteBuffer.get(this.preV21OutputBuffer, 0, j);
        paramByteBuffer.position(k);
        this.preV21OutputBufferOffset = 0;
        break label34;
        label255:
        if (this.tunneling)
        {
          if (paramLong != -9223372036854775807L) {}
          for (bool2 = bool1;; bool2 = false)
          {
            Assertions.checkState(bool2);
            j = writeNonBlockingWithAvSyncV21(this.audioTrack, paramByteBuffer, i, paramLong);
            break;
          }
        }
        j = writeNonBlockingV21(this.audioTrack, paramByteBuffer, i);
      }
      label317:
      if (this.isInputPcm) {
        this.writtenPcmBytes += j;
      }
      if (j == i)
      {
        if (!this.isInputPcm) {
          this.writtenEncodedFrames += this.framesPerEncodedSample;
        }
        this.outputBuffer = null;
      }
    }
  }
  
  @TargetApi(21)
  private static int writeNonBlockingV21(AudioTrack paramAudioTrack, ByteBuffer paramByteBuffer, int paramInt)
  {
    return paramAudioTrack.write(paramByteBuffer, paramInt, 1);
  }
  
  @TargetApi(21)
  private int writeNonBlockingWithAvSyncV21(AudioTrack paramAudioTrack, ByteBuffer paramByteBuffer, int paramInt, long paramLong)
  {
    int i = 0;
    if (this.avSyncHeader == null)
    {
      this.avSyncHeader = ByteBuffer.allocate(16);
      this.avSyncHeader.order(ByteOrder.BIG_ENDIAN);
      this.avSyncHeader.putInt(NUM);
    }
    if (this.bytesUntilNextAvSync == 0)
    {
      this.avSyncHeader.putInt(4, paramInt);
      this.avSyncHeader.putLong(8, 1000L * paramLong);
      this.avSyncHeader.position(0);
      this.bytesUntilNextAvSync = paramInt;
    }
    int j = this.avSyncHeader.remaining();
    int k;
    if (j > 0)
    {
      k = paramAudioTrack.write(this.avSyncHeader, j, 1);
      if (k < 0)
      {
        this.bytesUntilNextAvSync = 0;
        paramInt = k;
      }
    }
    for (;;)
    {
      return paramInt;
      if (k < j)
      {
        paramInt = i;
      }
      else
      {
        paramInt = writeNonBlockingV21(paramAudioTrack, paramByteBuffer, paramInt);
        if (paramInt < 0) {
          this.bytesUntilNextAvSync = 0;
        } else {
          this.bytesUntilNextAvSync -= paramInt;
        }
      }
    }
  }
  
  public void configure(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5, int paramInt6)
    throws AudioSink.ConfigurationException
  {
    int i = 0;
    int j = 0;
    this.inputSampleRate = paramInt3;
    int k = paramInt3;
    this.isInputPcm = isEncodingPcm(paramInt1);
    boolean bool;
    if ((this.enableConvertHighResIntPcmToFloat) && (isEncodingSupported(NUM)) && (Util.isEncodingHighResolutionIntegerPcm(paramInt1)))
    {
      bool = true;
      this.shouldConvertHighResIntPcmToFloat = bool;
      if (this.isInputPcm) {
        this.pcmFrameSize = Util.getPcmFrameSize(paramInt1, paramInt2);
      }
      paramInt3 = paramInt1;
      if ((!this.isInputPcm) || (paramInt1 == 4)) {
        break label257;
      }
      bool = true;
      label88:
      if ((!bool) || (this.shouldConvertHighResIntPcmToFloat)) {
        break label263;
      }
    }
    int n;
    int i1;
    int i2;
    label257:
    label263:
    for (int m = 1;; m = 0)
    {
      this.canApplyPlaybackParameters = m;
      n = paramInt2;
      i1 = paramInt3;
      i2 = k;
      if (!bool) {
        break label281;
      }
      this.trimmingAudioProcessor.setTrimSampleCount(paramInt5, paramInt6);
      this.channelMappingAudioProcessor.setChannelMap(paramArrayOfInt);
      paramArrayOfInt = getAvailableAudioProcessors();
      int i3 = paramArrayOfInt.length;
      paramInt6 = 0;
      paramInt1 = k;
      paramInt5 = j;
      for (;;)
      {
        n = paramInt2;
        i1 = paramInt3;
        i = paramInt5;
        i2 = paramInt1;
        if (paramInt6 >= i3) {
          break;
        }
        int i4 = paramArrayOfInt[paramInt6];
        try
        {
          m = i4.configure(paramInt1, paramInt2, paramInt3);
          paramInt5 |= m;
          if (i4.isActive())
          {
            paramInt2 = i4.getOutputChannelCount();
            paramInt1 = i4.getOutputSampleRateHz();
            paramInt3 = i4.getOutputEncoding();
          }
          paramInt6++;
        }
        catch (AudioProcessor.UnhandledFormatException paramArrayOfInt)
        {
          throw new AudioSink.ConfigurationException(paramArrayOfInt);
        }
      }
      bool = false;
      break;
      bool = false;
      break label88;
    }
    switch (n)
    {
    default: 
      throw new AudioSink.ConfigurationException("Unsupported channel count: " + n);
    case 1: 
      label281:
      paramInt1 = 4;
      paramInt2 = paramInt1;
      if (Util.SDK_INT <= 23)
      {
        paramInt2 = paramInt1;
        if ("foster".equals(Util.DEVICE))
        {
          paramInt2 = paramInt1;
          if ("NVIDIA".equals(Util.MANUFACTURER))
          {
            paramInt2 = paramInt1;
            switch (n)
            {
            default: 
              paramInt2 = paramInt1;
            }
          }
        }
      }
      break;
    }
    for (;;)
    {
      paramInt1 = paramInt2;
      if (Util.SDK_INT <= 25)
      {
        paramInt1 = paramInt2;
        if ("fugu".equals(Util.DEVICE))
        {
          paramInt1 = paramInt2;
          if (!this.isInputPcm)
          {
            paramInt1 = paramInt2;
            if (n == 1) {
              paramInt1 = 12;
            }
          }
        }
      }
      if ((i != 0) || (!isInitialized()) || (this.outputEncoding != i1) || (this.sampleRate != i2) || (this.channelConfig != paramInt1)) {
        break label582;
      }
      return;
      paramInt1 = 12;
      break;
      paramInt1 = 28;
      break;
      paramInt1 = 204;
      break;
      paramInt1 = 220;
      break;
      paramInt1 = 252;
      break;
      paramInt1 = 1276;
      break;
      paramInt1 = C.CHANNEL_OUT_7POINT1_SURROUND;
      break;
      paramInt2 = C.CHANNEL_OUT_7POINT1_SURROUND;
      continue;
      paramInt2 = 252;
    }
    label582:
    reset();
    this.processingEnabled = bool;
    this.sampleRate = i2;
    this.channelConfig = paramInt1;
    this.outputEncoding = i1;
    if (this.isInputPcm) {
      this.outputPcmFrameSize = Util.getPcmFrameSize(this.outputEncoding, n);
    }
    if (paramInt4 != 0)
    {
      this.bufferSize = paramInt4;
      label640:
      if (!this.isInputPcm) {
        break label811;
      }
    }
    label811:
    for (long l = framesToDurationUs(this.bufferSize / this.outputPcmFrameSize);; l = -9223372036854775807L)
    {
      this.bufferSizeUs = l;
      break;
      if (this.isInputPcm)
      {
        paramInt1 = AudioTrack.getMinBufferSize(i2, paramInt1, this.outputEncoding);
        if (paramInt1 != -2) {}
        for (bool = true;; bool = false)
        {
          Assertions.checkState(bool);
          this.bufferSize = Util.constrainValue(paramInt1 * 4, (int)durationUsToFrames(250000L) * this.outputPcmFrameSize, (int)Math.max(paramInt1, durationUsToFrames(750000L) * this.outputPcmFrameSize));
          break;
        }
      }
      if ((this.outputEncoding == 5) || (this.outputEncoding == 6))
      {
        this.bufferSize = 20480;
        break label640;
      }
      if (this.outputEncoding == 7)
      {
        this.bufferSize = 49152;
        break label640;
      }
      this.bufferSize = 294912;
      break label640;
    }
  }
  
  public void disableTunneling()
  {
    if (this.tunneling)
    {
      this.tunneling = false;
      this.audioSessionId = 0;
      reset();
    }
  }
  
  public void enableTunnelingV21(int paramInt)
  {
    if (Util.SDK_INT >= 21) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      if ((!this.tunneling) || (this.audioSessionId != paramInt))
      {
        this.tunneling = true;
        this.audioSessionId = paramInt;
        reset();
      }
      return;
    }
  }
  
  public long getCurrentPositionUs(boolean paramBoolean)
  {
    if (!hasCurrentPositionUs()) {}
    for (long l1 = Long.MIN_VALUE;; l1 = this.startMediaTimeUs + applySpeedup(l1))
    {
      return l1;
      if (this.audioTrack.getPlayState() == 3) {
        maybeSampleSyncParams();
      }
      l1 = System.nanoTime() / 1000L;
      if (!this.audioTimestampSet) {
        break;
      }
      l1 = durationUsToFrames(l1 - this.audioTrackUtil.getTimestampNanoTime() / 1000L);
      l1 = framesToDurationUs(this.audioTrackUtil.getTimestampFramePosition() + l1);
      l1 = Math.min(l1, framesToDurationUs(getWrittenFrames()));
    }
    if (this.playheadOffsetCount == 0) {}
    for (long l2 = this.audioTrackUtil.getPositionUs();; l2 = l1 + this.smoothedPlayheadOffsetUs)
    {
      l1 = l2;
      if (paramBoolean) {
        break;
      }
      l1 = l2 - this.latencyUs;
      break;
    }
  }
  
  public PlaybackParameters getPlaybackParameters()
  {
    return this.playbackParameters;
  }
  
  public boolean handleBuffer(ByteBuffer paramByteBuffer, long paramLong)
    throws AudioSink.InitializationException, AudioSink.WriteException
  {
    boolean bool;
    if ((this.inputBuffer == null) || (paramByteBuffer == this.inputBuffer))
    {
      bool = true;
      Assertions.checkArgument(bool);
      if (!isInitialized())
      {
        initialize();
        if (this.playing) {
          play();
        }
      }
      if (!needsPassthroughWorkarounds()) {
        break label109;
      }
      if (this.audioTrack.getPlayState() != 2) {
        break label80;
      }
      this.hasData = false;
      bool = false;
    }
    for (;;)
    {
      return bool;
      bool = false;
      break;
      label80:
      if ((this.audioTrack.getPlayState() == 1) && (this.audioTrackUtil.getPlaybackHeadPosition() != 0L))
      {
        bool = false;
      }
      else
      {
        label109:
        bool = this.hasData;
        this.hasData = hasPendingData();
        long l2;
        if ((bool) && (!this.hasData) && (this.audioTrack.getPlayState() != 1) && (this.listener != null))
        {
          long l1 = SystemClock.elapsedRealtime();
          l2 = this.lastFeedElapsedRealtimeMs;
          this.listener.onUnderrun(this.bufferSize, C.usToMs(this.bufferSizeUs), l1 - l2);
        }
        if (this.inputBuffer == null)
        {
          if (!paramByteBuffer.hasRemaining())
          {
            bool = true;
            continue;
          }
          if ((!this.isInputPcm) && (this.framesPerEncodedSample == 0))
          {
            this.framesPerEncodedSample = getFramesPerEncodedSample(this.outputEncoding, paramByteBuffer);
            if (this.framesPerEncodedSample == 0)
            {
              bool = true;
              continue;
            }
          }
          if (this.drainingPlaybackParameters != null)
          {
            if (!drainAudioProcessorsToEndOfStream())
            {
              bool = false;
              continue;
            }
            this.playbackParametersCheckpoints.add(new PlaybackParametersCheckpoint(this.drainingPlaybackParameters, Math.max(0L, paramLong), framesToDurationUs(getWrittenFrames()), null));
            this.drainingPlaybackParameters = null;
            resetAudioProcessors();
          }
          if (this.startMediaTimeState == 0)
          {
            this.startMediaTimeUs = Math.max(0L, paramLong);
            this.startMediaTimeState = 1;
            label331:
            if (!this.isInputPcm) {
              break label520;
            }
            this.submittedPcmBytes += paramByteBuffer.remaining();
            label352:
            this.inputBuffer = paramByteBuffer;
          }
        }
        else
        {
          if (!this.processingEnabled) {
            break label537;
          }
          processBuffers(paramLong);
        }
        for (;;)
        {
          if (this.inputBuffer.hasRemaining()) {
            break label549;
          }
          this.inputBuffer = null;
          bool = true;
          break;
          l2 = this.startMediaTimeUs + inputFramesToDurationUs(getSubmittedFrames());
          if ((this.startMediaTimeState == 1) && (Math.abs(l2 - paramLong) > 200000L))
          {
            Log.e("AudioTrack", "Discontinuity detected [expected " + l2 + ", got " + paramLong + "]");
            this.startMediaTimeState = 2;
          }
          if (this.startMediaTimeState != 2) {
            break label331;
          }
          this.startMediaTimeUs += paramLong - l2;
          this.startMediaTimeState = 1;
          if (this.listener == null) {
            break label331;
          }
          this.listener.onPositionDiscontinuity();
          break label331;
          label520:
          this.submittedEncodedFrames += this.framesPerEncodedSample;
          break label352;
          label537:
          writeBuffer(this.inputBuffer, paramLong);
        }
        label549:
        if (this.audioTrackUtil.needsReset(getWrittenFrames()))
        {
          Log.w("AudioTrack", "Resetting stalled audio track");
          reset();
          bool = true;
        }
        else
        {
          bool = false;
        }
      }
    }
  }
  
  public void handleDiscontinuity()
  {
    if (this.startMediaTimeState == 1) {
      this.startMediaTimeState = 2;
    }
  }
  
  public boolean hasPendingData()
  {
    if ((isInitialized()) && ((getWrittenFrames() > this.audioTrackUtil.getPlaybackHeadPosition()) || (overrideHasPendingData()))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isEncodingSupported(int paramInt)
  {
    boolean bool1 = true;
    boolean bool2 = false;
    if (isEncodingPcm(paramInt)) {
      if (paramInt == 4)
      {
        bool1 = bool2;
        if (Util.SDK_INT < 21) {}
      }
      else
      {
        bool1 = true;
      }
    }
    while ((this.audioCapabilities != null) && (this.audioCapabilities.supportsEncoding(paramInt))) {
      return bool1;
    }
    for (;;)
    {
      bool1 = false;
    }
  }
  
  public boolean isEnded()
  {
    if ((!isInitialized()) || ((this.handledEndOfStream) && (!hasPendingData()))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void pause()
  {
    this.playing = false;
    if (isInitialized())
    {
      resetSyncParams();
      this.audioTrackUtil.pause();
    }
  }
  
  public void play()
  {
    this.playing = true;
    if (isInitialized())
    {
      this.resumeSystemTimeUs = (System.nanoTime() / 1000L);
      this.audioTrack.play();
    }
  }
  
  public void playToEndOfStream()
    throws AudioSink.WriteException
  {
    if ((this.handledEndOfStream) || (!isInitialized())) {}
    for (;;)
    {
      return;
      if (drainAudioProcessorsToEndOfStream())
      {
        this.audioTrackUtil.handleEndOfStream(getWrittenFrames());
        this.bytesUntilNextAvSync = 0;
        this.handledEndOfStream = true;
      }
    }
  }
  
  public void release()
  {
    reset();
    releaseKeepSessionIdAudioTrack();
    AudioProcessor[] arrayOfAudioProcessor = this.toIntPcmAvailableAudioProcessors;
    int i = arrayOfAudioProcessor.length;
    for (int j = 0; j < i; j++) {
      arrayOfAudioProcessor[j].reset();
    }
    arrayOfAudioProcessor = this.toFloatPcmAvailableAudioProcessors;
    i = arrayOfAudioProcessor.length;
    for (j = 0; j < i; j++) {
      arrayOfAudioProcessor[j].reset();
    }
    this.audioSessionId = 0;
    this.playing = false;
  }
  
  public void reset()
  {
    if (isInitialized())
    {
      this.submittedPcmBytes = 0L;
      this.submittedEncodedFrames = 0L;
      this.writtenPcmBytes = 0L;
      this.writtenEncodedFrames = 0L;
      this.framesPerEncodedSample = 0;
      if (this.drainingPlaybackParameters != null)
      {
        this.playbackParameters = this.drainingPlaybackParameters;
        this.drainingPlaybackParameters = null;
      }
      for (;;)
      {
        this.playbackParametersCheckpoints.clear();
        this.playbackParametersOffsetUs = 0L;
        this.playbackParametersPositionUs = 0L;
        this.inputBuffer = null;
        this.outputBuffer = null;
        for (int i = 0; i < this.audioProcessors.length; i++)
        {
          localObject = this.audioProcessors[i];
          ((AudioProcessor)localObject).flush();
          this.outputBuffers[i] = ((AudioProcessor)localObject).getOutput();
        }
        if (!this.playbackParametersCheckpoints.isEmpty()) {
          this.playbackParameters = ((PlaybackParametersCheckpoint)this.playbackParametersCheckpoints.getLast()).playbackParameters;
        }
      }
      this.handledEndOfStream = false;
      this.drainingAudioProcessorIndex = -1;
      this.avSyncHeader = null;
      this.bytesUntilNextAvSync = 0;
      this.startMediaTimeState = 0;
      this.latencyUs = 0L;
      resetSyncParams();
      if (this.audioTrack.getPlayState() == 3) {
        this.audioTrack.pause();
      }
      final Object localObject = this.audioTrack;
      this.audioTrack = null;
      this.audioTrackUtil.reconfigure(null, false);
      this.releasingConditionVariable.close();
      new Thread()
      {
        public void run()
        {
          try
          {
            localObject.flush();
            localObject.release();
            return;
          }
          finally
          {
            DefaultAudioSink.this.releasingConditionVariable.open();
          }
        }
      }.start();
    }
  }
  
  public void setAudioAttributes(AudioAttributes paramAudioAttributes)
  {
    if (this.audioAttributes.equals(paramAudioAttributes)) {}
    for (;;)
    {
      return;
      this.audioAttributes = paramAudioAttributes;
      if (!this.tunneling)
      {
        reset();
        this.audioSessionId = 0;
      }
    }
  }
  
  public void setAudioSessionId(int paramInt)
  {
    if (this.audioSessionId != paramInt)
    {
      this.audioSessionId = paramInt;
      reset();
    }
  }
  
  public void setListener(AudioSink.Listener paramListener)
  {
    this.listener = paramListener;
  }
  
  public PlaybackParameters setPlaybackParameters(PlaybackParameters paramPlaybackParameters)
  {
    if ((isInitialized()) && (!this.canApplyPlaybackParameters))
    {
      this.playbackParameters = PlaybackParameters.DEFAULT;
      paramPlaybackParameters = this.playbackParameters;
      return paramPlaybackParameters;
    }
    PlaybackParameters localPlaybackParameters = new PlaybackParameters(this.sonicAudioProcessor.setSpeed(paramPlaybackParameters.speed), this.sonicAudioProcessor.setPitch(paramPlaybackParameters.pitch));
    if (this.drainingPlaybackParameters != null)
    {
      paramPlaybackParameters = this.drainingPlaybackParameters;
      label70:
      if (!localPlaybackParameters.equals(paramPlaybackParameters))
      {
        if (!isInitialized()) {
          break label133;
        }
        this.drainingPlaybackParameters = localPlaybackParameters;
      }
    }
    for (;;)
    {
      paramPlaybackParameters = this.playbackParameters;
      break;
      if (!this.playbackParametersCheckpoints.isEmpty())
      {
        paramPlaybackParameters = ((PlaybackParametersCheckpoint)this.playbackParametersCheckpoints.getLast()).playbackParameters;
        break label70;
      }
      paramPlaybackParameters = this.playbackParameters;
      break label70;
      label133:
      this.playbackParameters = localPlaybackParameters;
    }
  }
  
  public void setVolume(float paramFloat)
  {
    if (this.volume != paramFloat)
    {
      this.volume = paramFloat;
      setVolumeInternal();
    }
  }
  
  private static class AudioTrackUtil
  {
    private static final long FORCE_RESET_WORKAROUND_TIMEOUT_MS = 200L;
    protected AudioTrack audioTrack;
    private long endPlaybackHeadPosition;
    private long forceResetWorkaroundTimeMs;
    private long lastRawPlaybackHeadPosition;
    private boolean needsPassthroughWorkaround;
    private long passthroughWorkaroundPauseOffset;
    private long rawPlaybackHeadWrapCount;
    private int sampleRate;
    private long stopPlaybackHeadPosition;
    private long stopTimestampUs;
    
    public long getPlaybackHeadPosition()
    {
      long l1;
      long l2;
      if (this.stopTimestampUs != -9223372036854775807L)
      {
        l1 = SystemClock.elapsedRealtime();
        l2 = this.stopTimestampUs;
        l2 = this.sampleRate * (l1 * 1000L - l2) / 1000000L;
        l2 = Math.min(this.endPlaybackHeadPosition, this.stopPlaybackHeadPosition + l2);
      }
      for (;;)
      {
        return l2;
        int i = this.audioTrack.getPlayState();
        if (i == 1)
        {
          l2 = 0L;
        }
        else
        {
          l1 = 0xFFFFFFFF & this.audioTrack.getPlaybackHeadPosition();
          l2 = l1;
          if (this.needsPassthroughWorkaround)
          {
            if ((i == 2) && (l1 == 0L)) {
              this.passthroughWorkaroundPauseOffset = this.lastRawPlaybackHeadPosition;
            }
            l2 = l1 + this.passthroughWorkaroundPauseOffset;
          }
          if (Util.SDK_INT <= 26)
          {
            if ((l2 == 0L) && (this.lastRawPlaybackHeadPosition > 0L) && (i == 3))
            {
              if (this.forceResetWorkaroundTimeMs == -9223372036854775807L) {
                this.forceResetWorkaroundTimeMs = SystemClock.elapsedRealtime();
              }
              l2 = this.lastRawPlaybackHeadPosition;
            }
            else
            {
              this.forceResetWorkaroundTimeMs = -9223372036854775807L;
            }
          }
          else
          {
            if (this.lastRawPlaybackHeadPosition > l2) {
              this.rawPlaybackHeadWrapCount += 1L;
            }
            this.lastRawPlaybackHeadPosition = l2;
            l2 = (this.rawPlaybackHeadWrapCount << 32) + l2;
          }
        }
      }
    }
    
    public long getPositionUs()
    {
      return getPlaybackHeadPosition() * 1000000L / this.sampleRate;
    }
    
    public long getTimestampFramePosition()
    {
      throw new UnsupportedOperationException();
    }
    
    public long getTimestampNanoTime()
    {
      throw new UnsupportedOperationException();
    }
    
    public void handleEndOfStream(long paramLong)
    {
      this.stopPlaybackHeadPosition = getPlaybackHeadPosition();
      this.stopTimestampUs = (SystemClock.elapsedRealtime() * 1000L);
      this.endPlaybackHeadPosition = paramLong;
      this.audioTrack.stop();
    }
    
    public boolean needsReset(long paramLong)
    {
      if ((this.forceResetWorkaroundTimeMs != -9223372036854775807L) && (paramLong > 0L) && (SystemClock.elapsedRealtime() - this.forceResetWorkaroundTimeMs >= 200L)) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void pause()
    {
      if (this.stopTimestampUs != -9223372036854775807L) {}
      for (;;)
      {
        return;
        this.audioTrack.pause();
      }
    }
    
    public void reconfigure(AudioTrack paramAudioTrack, boolean paramBoolean)
    {
      this.audioTrack = paramAudioTrack;
      this.needsPassthroughWorkaround = paramBoolean;
      this.stopTimestampUs = -9223372036854775807L;
      this.forceResetWorkaroundTimeMs = -9223372036854775807L;
      this.lastRawPlaybackHeadPosition = 0L;
      this.rawPlaybackHeadWrapCount = 0L;
      this.passthroughWorkaroundPauseOffset = 0L;
      if (paramAudioTrack != null) {
        this.sampleRate = paramAudioTrack.getSampleRate();
      }
    }
    
    public boolean updateTimestamp()
    {
      return false;
    }
  }
  
  @TargetApi(19)
  private static class AudioTrackUtilV19
    extends DefaultAudioSink.AudioTrackUtil
  {
    private final AudioTimestamp audioTimestamp = new AudioTimestamp();
    private long lastRawTimestampFramePosition;
    private long lastTimestampFramePosition;
    private long rawTimestampFramePositionWrapCount;
    
    public AudioTrackUtilV19()
    {
      super();
    }
    
    public long getTimestampFramePosition()
    {
      return this.lastTimestampFramePosition;
    }
    
    public long getTimestampNanoTime()
    {
      return this.audioTimestamp.nanoTime;
    }
    
    public void reconfigure(AudioTrack paramAudioTrack, boolean paramBoolean)
    {
      super.reconfigure(paramAudioTrack, paramBoolean);
      this.rawTimestampFramePositionWrapCount = 0L;
      this.lastRawTimestampFramePosition = 0L;
      this.lastTimestampFramePosition = 0L;
    }
    
    public boolean updateTimestamp()
    {
      boolean bool = this.audioTrack.getTimestamp(this.audioTimestamp);
      if (bool)
      {
        long l = this.audioTimestamp.framePosition;
        if (this.lastRawTimestampFramePosition > l) {
          this.rawTimestampFramePositionWrapCount += 1L;
        }
        this.lastRawTimestampFramePosition = l;
        this.lastTimestampFramePosition = ((this.rawTimestampFramePositionWrapCount << 32) + l);
      }
      return bool;
    }
  }
  
  public static final class InvalidAudioTrackTimestampException
    extends RuntimeException
  {
    public InvalidAudioTrackTimestampException(String paramString)
    {
      super();
    }
  }
  
  private static final class PlaybackParametersCheckpoint
  {
    private final long mediaTimeUs;
    private final PlaybackParameters playbackParameters;
    private final long positionUs;
    
    private PlaybackParametersCheckpoint(PlaybackParameters paramPlaybackParameters, long paramLong1, long paramLong2)
    {
      this.playbackParameters = paramPlaybackParameters;
      this.mediaTimeUs = paramLong1;
      this.positionUs = paramLong2;
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface StartMediaTimeState {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/audio/DefaultAudioSink.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */