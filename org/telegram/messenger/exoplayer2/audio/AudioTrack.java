package org.telegram.messenger.exoplayer2.audio;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.AudioAttributes.Builder;
import android.media.AudioFormat;
import android.media.AudioFormat.Builder;
import android.media.AudioTimestamp;
import android.os.ConditionVariable;
import android.os.SystemClock;
import android.util.Log;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.LinkedList;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.PlaybackParameters;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

public final class AudioTrack
{
  private static final int BUFFER_MULTIPLICATION_FACTOR = 4;
  public static final long CURRENT_POSITION_NOT_SET = Long.MIN_VALUE;
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
  private static final int SONIC_MIN_BYTES_FOR_SPEEDUP = 1024;
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
  private android.media.AudioTrack audioTrack;
  private final AudioTrackUtil audioTrackUtil;
  private ByteBuffer avSyncHeader;
  private final AudioProcessor[] availableAudioProcessors;
  private int bufferSize;
  private long bufferSizeUs;
  private int bytesUntilNextAvSync;
  private int channelConfig;
  private final ChannelMappingAudioProcessor channelMappingAudioProcessor;
  private int drainingAudioProcessorIndex;
  private PlaybackParameters drainingPlaybackParameters;
  private int encoding;
  private int framesPerEncodedSample;
  private Method getLatencyMethod;
  private boolean handledEndOfStream;
  private boolean hasData;
  private ByteBuffer inputBuffer;
  private android.media.AudioTrack keepSessionIdAudioTrack;
  private long lastFeedElapsedRealtimeMs;
  private long lastPlayheadSampleTimeUs;
  private long lastTimestampSampleTimeUs;
  private long latencyUs;
  private final Listener listener;
  private int nextPlayheadOffsetIndex;
  private ByteBuffer outputBuffer;
  private ByteBuffer[] outputBuffers;
  private int outputEncoding;
  private int outputPcmFrameSize;
  private boolean passthrough;
  private int pcmFrameSize;
  private PlaybackParameters playbackParameters;
  private final LinkedList<PlaybackParametersCheckpoint> playbackParametersCheckpoints;
  private long playbackParametersOffsetUs;
  private long playbackParametersPositionUs;
  private int playheadOffsetCount;
  private final long[] playheadOffsets;
  private boolean playing;
  private byte[] preV21OutputBuffer;
  private int preV21OutputBufferOffset;
  private final ConditionVariable releasingConditionVariable;
  private long resumeSystemTimeUs;
  private int sampleRate;
  private long smoothedPlayheadOffsetUs;
  private final SonicAudioProcessor sonicAudioProcessor;
  private int startMediaTimeState;
  private long startMediaTimeUs;
  private long submittedEncodedFrames;
  private long submittedPcmBytes;
  private boolean tunneling;
  private float volume;
  private long writtenEncodedFrames;
  private long writtenPcmBytes;
  
  public AudioTrack(AudioCapabilities paramAudioCapabilities, AudioProcessor[] paramArrayOfAudioProcessor, Listener paramListener)
  {
    this.audioCapabilities = paramAudioCapabilities;
    this.listener = paramListener;
    this.releasingConditionVariable = new ConditionVariable(true);
    if (Util.SDK_INT >= 18) {}
    try
    {
      this.getLatencyMethod = android.media.AudioTrack.class.getMethod("getLatency", (Class[])null);
      if (Util.SDK_INT >= 19) {}
      for (this.audioTrackUtil = new AudioTrackUtilV19();; this.audioTrackUtil = new AudioTrackUtil(null))
      {
        this.channelMappingAudioProcessor = new ChannelMappingAudioProcessor();
        this.sonicAudioProcessor = new SonicAudioProcessor();
        this.availableAudioProcessors = new AudioProcessor[paramArrayOfAudioProcessor.length + 3];
        this.availableAudioProcessors[0] = new ResamplingAudioProcessor();
        this.availableAudioProcessors[1] = this.channelMappingAudioProcessor;
        System.arraycopy(paramArrayOfAudioProcessor, 0, this.availableAudioProcessors, 2, paramArrayOfAudioProcessor.length);
        this.availableAudioProcessors[(paramArrayOfAudioProcessor.length + 2)] = this.sonicAudioProcessor;
        this.playheadOffsets = new long[10];
        this.volume = 1.0F;
        this.startMediaTimeState = 0;
        this.audioAttributes = AudioAttributes.DEFAULT;
        this.audioSessionId = 0;
        this.playbackParameters = PlaybackParameters.DEFAULT;
        this.drainingAudioProcessorIndex = -1;
        this.audioProcessors = new AudioProcessor[0];
        this.outputBuffers = new ByteBuffer[0];
        this.playbackParametersCheckpoints = new LinkedList();
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
      return this.playbackParametersOffsetUs + paramLong - this.playbackParametersPositionUs;
    }
    if ((this.playbackParametersCheckpoints.isEmpty()) && (this.sonicAudioProcessor.getOutputByteCount() >= 1024L))
    {
      long l = this.playbackParametersOffsetUs;
      return Util.scaleLargeTimestamp(paramLong - this.playbackParametersPositionUs, this.sonicAudioProcessor.getInputByteCount(), this.sonicAudioProcessor.getOutputByteCount()) + l;
    }
    return this.playbackParametersOffsetUs + (this.playbackParameters.speed * (paramLong - this.playbackParametersPositionUs));
  }
  
  @TargetApi(21)
  private android.media.AudioTrack createAudioTrackV21()
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
      return new android.media.AudioTrack(localAudioAttributes, localAudioFormat, this.bufferSize, 1, i);
      localAudioAttributes = this.audioAttributes.getAudioAttributesV21();
      break;
    }
  }
  
  private boolean drainAudioProcessorsToEndOfStream()
    throws AudioTrack.WriteException
  {
    int i = 0;
    if (this.drainingAudioProcessorIndex == -1)
    {
      if (this.passthrough)
      {
        i = this.audioProcessors.length;
        this.drainingAudioProcessorIndex = i;
        i = 1;
      }
    }
    else
    {
      label30:
      if (this.drainingAudioProcessorIndex >= this.audioProcessors.length) {
        break label100;
      }
      AudioProcessor localAudioProcessor = this.audioProcessors[this.drainingAudioProcessorIndex];
      if (i != 0) {
        localAudioProcessor.queueEndOfStream();
      }
      processBuffers(-9223372036854775807L);
      if (localAudioProcessor.isEnded()) {
        break label85;
      }
    }
    label85:
    label100:
    do
    {
      return false;
      i = 0;
      break;
      i = 1;
      this.drainingAudioProcessorIndex += 1;
      break label30;
      if (this.outputBuffer == null) {
        break label126;
      }
      writeBuffer(this.outputBuffer, -9223372036854775807L);
    } while (this.outputBuffer != null);
    label126:
    this.drainingAudioProcessorIndex = -1;
    return true;
  }
  
  private long durationUsToFrames(long paramLong)
  {
    return this.sampleRate * paramLong / 1000000L;
  }
  
  private long framesToDurationUs(long paramLong)
  {
    return 1000000L * paramLong / this.sampleRate;
  }
  
  private static int getEncodingForMimeType(String paramString)
  {
    int i = -1;
    switch (paramString.hashCode())
    {
    }
    for (;;)
    {
      switch (i)
      {
      default: 
        return 0;
        if (paramString.equals("audio/ac3"))
        {
          i = 0;
          continue;
          if (paramString.equals("audio/eac3"))
          {
            i = 1;
            continue;
            if (paramString.equals("audio/vnd.dts"))
            {
              i = 2;
              continue;
              if (paramString.equals("audio/vnd.dts.hd")) {
                i = 3;
              }
            }
          }
        }
        break;
      }
    }
    return 5;
    return 6;
    return 7;
    return 8;
  }
  
  private static int getFramesPerEncodedSample(int paramInt, ByteBuffer paramByteBuffer)
  {
    if ((paramInt == 7) || (paramInt == 8)) {
      return DtsUtil.parseDtsAudioSampleCount(paramByteBuffer);
    }
    if (paramInt == 5) {
      return Ac3Util.getAc3SyncframeAudioSampleCount();
    }
    if (paramInt == 6) {
      return Ac3Util.parseEAc3SyncframeAudioSampleCount(paramByteBuffer);
    }
    throw new IllegalStateException("Unexpected audio encoding: " + paramInt);
  }
  
  private long getSubmittedFrames()
  {
    if (this.passthrough) {
      return this.submittedEncodedFrames;
    }
    return this.submittedPcmBytes / this.pcmFrameSize;
  }
  
  private long getWrittenFrames()
  {
    if (this.passthrough) {
      return this.writtenEncodedFrames;
    }
    return this.writtenPcmBytes / this.outputPcmFrameSize;
  }
  
  private boolean hasCurrentPositionUs()
  {
    return (isInitialized()) && (this.startMediaTimeState != 0);
  }
  
  private void initialize()
    throws AudioTrack.InitializationException
  {
    this.releasingConditionVariable.block();
    this.audioTrack = initializeAudioTrack();
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
      this.listener.onAudioSessionId(i);
    }
    this.audioTrackUtil.reconfigure(this.audioTrack, needsPassthroughWorkarounds());
    setVolumeInternal();
    this.hasData = false;
  }
  
  private android.media.AudioTrack initializeAudioTrack()
    throws AudioTrack.InitializationException
  {
    android.media.AudioTrack localAudioTrack;
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
          throw new InitializationException(i, this.sampleRate, this.channelConfig, this.bufferSize);
          i = Util.getStreamTypeForAudioUsage(this.audioAttributes.usage);
          if (this.audioSessionId == 0) {
            localAudioTrack = new android.media.AudioTrack(i, this.sampleRate, this.channelConfig, this.outputEncoding, this.bufferSize, 1);
          } else {
            localAudioTrack = new android.media.AudioTrack(i, this.sampleRate, this.channelConfig, this.outputEncoding, this.bufferSize, 1, this.audioSessionId);
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
  
  private android.media.AudioTrack initializeKeepSessionIdAudioTrack(int paramInt)
  {
    return new android.media.AudioTrack(3, 4000, 4, 2, 2, 0, paramInt);
  }
  
  private boolean isInitialized()
  {
    return this.audioTrack != null;
  }
  
  private void maybeSampleSyncParams()
  {
    long l1 = this.audioTrackUtil.getPositionUs();
    if (l1 == 0L) {}
    long l2;
    do
    {
      return;
      l2 = System.nanoTime() / 1000L;
      if (l2 - this.lastPlayheadSampleTimeUs >= 30000L)
      {
        this.playheadOffsets[this.nextPlayheadOffsetIndex] = (l1 - l2);
        this.nextPlayheadOffsetIndex = ((this.nextPlayheadOffsetIndex + 1) % 10);
        if (this.playheadOffsetCount < 10) {
          this.playheadOffsetCount += 1;
        }
        this.lastPlayheadSampleTimeUs = l2;
        this.smoothedPlayheadOffsetUs = 0L;
        int i = 0;
        while (i < this.playheadOffsetCount)
        {
          this.smoothedPlayheadOffsetUs += this.playheadOffsets[i] / this.playheadOffsetCount;
          i += 1;
        }
      }
    } while ((needsPassthroughWorkarounds()) || (l2 - this.lastTimestampSampleTimeUs < 500000L));
    this.audioTimestampSet = this.audioTrackUtil.updateTimestamp();
    long l3;
    long l4;
    if (this.audioTimestampSet)
    {
      l3 = this.audioTrackUtil.getTimestampNanoTime() / 1000L;
      l4 = this.audioTrackUtil.getTimestampFramePosition();
      if (l3 >= this.resumeSystemTimeUs) {
        break label321;
      }
      this.audioTimestampSet = false;
    }
    for (;;)
    {
      if ((this.getLatencyMethod != null) && (!this.passthrough)) {}
      try
      {
        this.latencyUs = (((Integer)this.getLatencyMethod.invoke(this.audioTrack, (Object[])null)).intValue() * 1000L - this.bufferSizeUs);
        this.latencyUs = Math.max(this.latencyUs, 0L);
        if (this.latencyUs > 5000000L)
        {
          Log.w("AudioTrack", "Ignoring impossibly large audio latency: " + this.latencyUs);
          this.latencyUs = 0L;
        }
        this.lastTimestampSampleTimeUs = l2;
        return;
        label321:
        if (Math.abs(l3 - l2) > 5000000L)
        {
          str = "Spurious audio timestamp (system clock mismatch): " + l4 + ", " + l3 + ", " + l2 + ", " + l1 + ", " + getSubmittedFrames() + ", " + getWrittenFrames();
          if (failOnSpuriousAudioTimestamp) {
            throw new InvalidAudioTrackTimestampException(str);
          }
          Log.w("AudioTrack", str);
          this.audioTimestampSet = false;
          continue;
        }
        if (Math.abs(framesToDurationUs(l4) - l1) <= 5000000L) {
          continue;
        }
        String str = "Spurious audio timestamp (frame position mismatch): " + l4 + ", " + l3 + ", " + l2 + ", " + l1 + ", " + getSubmittedFrames() + ", " + getWrittenFrames();
        if (failOnSpuriousAudioTimestamp) {
          throw new InvalidAudioTrackTimestampException(str);
        }
        Log.w("AudioTrack", str);
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
    return (Util.SDK_INT < 23) && ((this.outputEncoding == 5) || (this.outputEncoding == 6));
  }
  
  private boolean overrideHasPendingData()
  {
    return (needsPassthroughWorkarounds()) && (this.audioTrack.getPlayState() == 2) && (this.audioTrack.getPlaybackHeadPosition() == 0);
  }
  
  private void processBuffers(long paramLong)
    throws AudioTrack.WriteException
  {
    int j = this.audioProcessors.length;
    int i = j;
    for (;;)
    {
      ByteBuffer localByteBuffer;
      if (i >= 0)
      {
        if (i <= 0) {
          break label51;
        }
        localByteBuffer = this.outputBuffers[(i - 1)];
        label28:
        if (i != j) {
          break label75;
        }
        writeBuffer(localByteBuffer, paramLong);
      }
      for (;;)
      {
        if (localByteBuffer.hasRemaining())
        {
          return;
          label51:
          if (this.inputBuffer != null)
          {
            localByteBuffer = this.inputBuffer;
            break label28;
          }
          localByteBuffer = AudioProcessor.EMPTY_BUFFER;
          break label28;
          label75:
          Object localObject = this.audioProcessors[i];
          ((AudioProcessor)localObject).queueInput(localByteBuffer);
          localObject = ((AudioProcessor)localObject).getOutput();
          this.outputBuffers[i] = localObject;
          if (((ByteBuffer)localObject).hasRemaining())
          {
            i += 1;
            break;
          }
        }
      }
      i -= 1;
    }
  }
  
  private void releaseKeepSessionIdAudioTrack()
  {
    if (this.keepSessionIdAudioTrack == null) {
      return;
    }
    final android.media.AudioTrack localAudioTrack = this.keepSessionIdAudioTrack;
    this.keepSessionIdAudioTrack = null;
    new Thread()
    {
      public void run()
      {
        localAudioTrack.release();
      }
    }.start();
  }
  
  private void resetAudioProcessors()
  {
    Object localObject = new ArrayList();
    AudioProcessor[] arrayOfAudioProcessor = this.availableAudioProcessors;
    int j = arrayOfAudioProcessor.length;
    int i = 0;
    if (i < j)
    {
      AudioProcessor localAudioProcessor = arrayOfAudioProcessor[i];
      if (localAudioProcessor.isActive()) {
        ((ArrayList)localObject).add(localAudioProcessor);
      }
      for (;;)
      {
        i += 1;
        break;
        localAudioProcessor.flush();
      }
    }
    j = ((ArrayList)localObject).size();
    this.audioProcessors = ((AudioProcessor[])((ArrayList)localObject).toArray(new AudioProcessor[j]));
    this.outputBuffers = new ByteBuffer[j];
    i = 0;
    while (i < j)
    {
      localObject = this.audioProcessors[i];
      ((AudioProcessor)localObject).flush();
      this.outputBuffers[i] = ((AudioProcessor)localObject).getOutput();
      i += 1;
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
    if (!isInitialized()) {
      return;
    }
    if (Util.SDK_INT >= 21)
    {
      setVolumeInternalV21(this.audioTrack, this.volume);
      return;
    }
    setVolumeInternalV3(this.audioTrack, this.volume);
  }
  
  @TargetApi(21)
  private static void setVolumeInternalV21(android.media.AudioTrack paramAudioTrack, float paramFloat)
  {
    paramAudioTrack.setVolume(paramFloat);
  }
  
  private static void setVolumeInternalV3(android.media.AudioTrack paramAudioTrack, float paramFloat)
  {
    paramAudioTrack.setStereoVolume(paramFloat, paramFloat);
  }
  
  private boolean writeBuffer(ByteBuffer paramByteBuffer, long paramLong)
    throws AudioTrack.WriteException
  {
    if (!paramByteBuffer.hasRemaining()) {
      return true;
    }
    boolean bool;
    label32:
    int k;
    int i;
    int j;
    if (this.outputBuffer != null) {
      if (this.outputBuffer == paramByteBuffer)
      {
        bool = true;
        Assertions.checkArgument(bool);
        k = paramByteBuffer.remaining();
        i = 0;
        if (Util.SDK_INT >= 21) {
          break label253;
        }
        j = (int)(this.writtenPcmBytes - this.audioTrackUtil.getPlaybackHeadPosition() * this.outputPcmFrameSize);
        j = this.bufferSize - j;
        if (j > 0)
        {
          i = Math.min(k, j);
          j = this.audioTrack.write(this.preV21OutputBuffer, this.preV21OutputBufferOffset, i);
          i = j;
          if (j > 0)
          {
            this.preV21OutputBufferOffset += j;
            paramByteBuffer.position(paramByteBuffer.position() + j);
            i = j;
          }
        }
      }
    }
    for (;;)
    {
      this.lastFeedElapsedRealtimeMs = SystemClock.elapsedRealtime();
      if (i >= 0) {
        break label314;
      }
      throw new WriteException(i);
      bool = false;
      break;
      this.outputBuffer = paramByteBuffer;
      if (Util.SDK_INT >= 21) {
        break label32;
      }
      i = paramByteBuffer.remaining();
      if ((this.preV21OutputBuffer == null) || (this.preV21OutputBuffer.length < i)) {
        this.preV21OutputBuffer = new byte[i];
      }
      j = paramByteBuffer.position();
      paramByteBuffer.get(this.preV21OutputBuffer, 0, i);
      paramByteBuffer.position(j);
      this.preV21OutputBufferOffset = 0;
      break label32;
      label253:
      if (this.tunneling)
      {
        if (paramLong != -9223372036854775807L) {}
        for (bool = true;; bool = false)
        {
          Assertions.checkState(bool);
          i = writeNonBlockingWithAvSyncV21(this.audioTrack, paramByteBuffer, k, paramLong);
          break;
        }
      }
      i = writeNonBlockingV21(this.audioTrack, paramByteBuffer, k);
    }
    label314:
    if (!this.passthrough) {
      this.writtenPcmBytes += i;
    }
    if (i == k)
    {
      if (this.passthrough) {
        this.writtenEncodedFrames += this.framesPerEncodedSample;
      }
      this.outputBuffer = null;
      return true;
    }
    return false;
  }
  
  @TargetApi(21)
  private static int writeNonBlockingV21(android.media.AudioTrack paramAudioTrack, ByteBuffer paramByteBuffer, int paramInt)
  {
    return paramAudioTrack.write(paramByteBuffer, paramInt, 1);
  }
  
  @TargetApi(21)
  private int writeNonBlockingWithAvSyncV21(android.media.AudioTrack paramAudioTrack, ByteBuffer paramByteBuffer, int paramInt, long paramLong)
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
    int k = this.avSyncHeader.remaining();
    if (k > 0)
    {
      int j = paramAudioTrack.write(this.avSyncHeader, k, 1);
      if (j < 0)
      {
        this.bytesUntilNextAvSync = 0;
        i = j;
      }
      while (j < k) {
        return i;
      }
    }
    paramInt = writeNonBlockingV21(paramAudioTrack, paramByteBuffer, paramInt);
    if (paramInt < 0)
    {
      this.bytesUntilNextAvSync = 0;
      return paramInt;
    }
    this.bytesUntilNextAvSync -= paramInt;
    return paramInt;
  }
  
  public void configure(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws AudioTrack.ConfigurationException
  {
    configure(paramString, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void configure(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
    throws AudioTrack.ConfigurationException
  {
    boolean bool;
    if (!"audio/raw".equals(paramString))
    {
      bool = true;
      if (!bool) {
        break label152;
      }
    }
    int i1;
    label152:
    for (int i = getEncodingForMimeType(paramString);; i = paramInt3)
    {
      m = 0;
      int n = 0;
      j = i;
      k = paramInt1;
      if (bool) {
        break label200;
      }
      this.pcmFrameSize = Util.getPcmFrameSize(paramInt3, paramInt1);
      this.channelMappingAudioProcessor.setChannelMap(paramArrayOfInt);
      paramString = this.availableAudioProcessors;
      k = paramString.length;
      j = 0;
      paramInt3 = n;
      while (j < k)
      {
        paramArrayOfInt = paramString[j];
        try
        {
          i1 = paramArrayOfInt.configure(paramInt2, paramInt1, i);
          paramInt3 |= i1;
          if (paramArrayOfInt.isActive())
          {
            paramInt1 = paramArrayOfInt.getOutputChannelCount();
            i = paramArrayOfInt.getOutputEncoding();
          }
          j += 1;
        }
        catch (AudioProcessor.UnhandledFormatException paramString)
        {
          throw new ConfigurationException(paramString);
        }
      }
      bool = false;
      break;
    }
    int j = i;
    int m = paramInt3;
    int k = paramInt1;
    if (paramInt3 != 0)
    {
      resetAudioProcessors();
      k = paramInt1;
      m = paramInt3;
      j = i;
    }
    switch (k)
    {
    default: 
      throw new ConfigurationException("Unsupported channel count: " + k);
    case 1: 
      label200:
      paramInt1 = 4;
      paramInt3 = paramInt1;
      if (Util.SDK_INT <= 23)
      {
        paramInt3 = paramInt1;
        if ("foster".equals(Util.DEVICE))
        {
          paramInt3 = paramInt1;
          if ("NVIDIA".equals(Util.MANUFACTURER))
          {
            paramInt3 = paramInt1;
            switch (k)
            {
            default: 
              paramInt3 = paramInt1;
            }
          }
        }
      }
      break;
    }
    for (;;)
    {
      paramInt1 = paramInt3;
      if (Util.SDK_INT <= 25)
      {
        paramInt1 = paramInt3;
        if ("fugu".equals(Util.DEVICE))
        {
          paramInt1 = paramInt3;
          if (bool)
          {
            paramInt1 = paramInt3;
            if (k == 1) {
              paramInt1 = 12;
            }
          }
        }
      }
      if ((m != 0) || (!isInitialized()) || (this.encoding != j) || (this.sampleRate != paramInt2) || (this.channelConfig != paramInt1)) {
        break label510;
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
      paramInt3 = C.CHANNEL_OUT_7POINT1_SURROUND;
      continue;
      paramInt3 = 252;
    }
    label510:
    reset();
    this.encoding = j;
    this.passthrough = bool;
    this.sampleRate = paramInt2;
    this.channelConfig = paramInt1;
    if (bool)
    {
      this.outputEncoding = j;
      this.outputPcmFrameSize = Util.getPcmFrameSize(2, k);
      if (paramInt4 == 0) {
        break label600;
      }
      this.bufferSize = paramInt4;
      label568:
      if (!bool) {
        break label745;
      }
    }
    label600:
    label663:
    label730:
    label745:
    for (long l = -9223372036854775807L;; l = framesToDurationUs(this.bufferSize / this.outputPcmFrameSize))
    {
      this.bufferSizeUs = l;
      setPlaybackParameters(this.playbackParameters);
      return;
      j = 2;
      break;
      if (bool)
      {
        if ((this.outputEncoding == 5) || (this.outputEncoding == 6))
        {
          this.bufferSize = 20480;
          break label568;
        }
        this.bufferSize = 49152;
        break label568;
      }
      paramInt3 = android.media.AudioTrack.getMinBufferSize(paramInt2, paramInt1, this.outputEncoding);
      if (paramInt3 != -2)
      {
        i1 = 1;
        Assertions.checkState(i1);
        paramInt1 = paramInt3 * 4;
        paramInt2 = (int)durationUsToFrames(250000L) * this.outputPcmFrameSize;
        paramInt3 = (int)Math.max(paramInt3, durationUsToFrames(750000L) * this.outputPcmFrameSize);
        if (paramInt1 >= paramInt2) {
          break label730;
        }
        paramInt1 = paramInt2;
      }
      for (;;)
      {
        this.bufferSize = paramInt1;
        break;
        i1 = 0;
        break label663;
        if (paramInt1 > paramInt3) {
          paramInt1 = paramInt3;
        }
      }
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
    if (!hasCurrentPositionUs()) {
      return Long.MIN_VALUE;
    }
    if (this.audioTrack.getPlayState() == 3) {
      maybeSampleSyncParams();
    }
    long l1 = System.nanoTime() / 1000L;
    if (this.audioTimestampSet)
    {
      l1 = durationUsToFrames(l1 - this.audioTrackUtil.getTimestampNanoTime() / 1000L);
      l1 = framesToDurationUs(this.audioTrackUtil.getTimestampFramePosition() + l1);
      return this.startMediaTimeUs + applySpeedup(l1);
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
    throws AudioTrack.InitializationException, AudioTrack.WriteException
  {
    if ((this.inputBuffer == null) || (paramByteBuffer == this.inputBuffer)) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkArgument(bool);
      if (!isInitialized())
      {
        initialize();
        if (this.playing) {
          play();
        }
      }
      if (!needsPassthroughWorkarounds()) {
        break label101;
      }
      if (this.audioTrack.getPlayState() != 2) {
        break;
      }
      this.hasData = false;
      return false;
    }
    if ((this.audioTrack.getPlayState() == 1) && (this.audioTrackUtil.getPlaybackHeadPosition() != 0L)) {
      return false;
    }
    label101:
    bool = this.hasData;
    this.hasData = hasPendingData();
    long l1;
    if ((bool) && (!this.hasData) && (this.audioTrack.getPlayState() != 1))
    {
      l1 = SystemClock.elapsedRealtime();
      long l2 = this.lastFeedElapsedRealtimeMs;
      this.listener.onUnderrun(this.bufferSize, C.usToMs(this.bufferSizeUs), l1 - l2);
    }
    if (this.inputBuffer == null)
    {
      if (!paramByteBuffer.hasRemaining()) {
        return true;
      }
      if ((this.passthrough) && (this.framesPerEncodedSample == 0)) {
        this.framesPerEncodedSample = getFramesPerEncodedSample(this.outputEncoding, paramByteBuffer);
      }
      if (this.drainingPlaybackParameters != null)
      {
        if (!drainAudioProcessorsToEndOfStream()) {
          return false;
        }
        this.playbackParametersCheckpoints.add(new PlaybackParametersCheckpoint(this.drainingPlaybackParameters, Math.max(0L, paramLong), framesToDurationUs(getWrittenFrames()), null));
        this.drainingPlaybackParameters = null;
        resetAudioProcessors();
      }
      if (this.startMediaTimeState == 0)
      {
        this.startMediaTimeUs = Math.max(0L, paramLong);
        this.startMediaTimeState = 1;
        if (!this.passthrough) {
          break label478;
        }
        this.submittedEncodedFrames += this.framesPerEncodedSample;
        label316:
        this.inputBuffer = paramByteBuffer;
      }
    }
    else
    {
      if (!this.passthrough) {
        break label495;
      }
      writeBuffer(this.inputBuffer, paramLong);
    }
    for (;;)
    {
      if (this.inputBuffer.hasRemaining()) {
        break label503;
      }
      this.inputBuffer = null;
      return true;
      l1 = this.startMediaTimeUs + framesToDurationUs(getSubmittedFrames());
      if ((this.startMediaTimeState == 1) && (Math.abs(l1 - paramLong) > 200000L))
      {
        Log.e("AudioTrack", "Discontinuity detected [expected " + l1 + ", got " + paramLong + "]");
        this.startMediaTimeState = 2;
      }
      if (this.startMediaTimeState != 2) {
        break;
      }
      this.startMediaTimeUs += paramLong - l1;
      this.startMediaTimeState = 1;
      this.listener.onPositionDiscontinuity();
      break;
      label478:
      this.submittedPcmBytes += paramByteBuffer.remaining();
      break label316;
      label495:
      processBuffers(paramLong);
    }
    label503:
    return false;
  }
  
  public void handleDiscontinuity()
  {
    if (this.startMediaTimeState == 1) {
      this.startMediaTimeState = 2;
    }
  }
  
  public boolean hasPendingData()
  {
    return (isInitialized()) && ((getWrittenFrames() > this.audioTrackUtil.getPlaybackHeadPosition()) || (overrideHasPendingData()));
  }
  
  public boolean isEnded()
  {
    return (!isInitialized()) || ((this.handledEndOfStream) && (!hasPendingData()));
  }
  
  public boolean isPassthroughSupported(String paramString)
  {
    return (this.audioCapabilities != null) && (this.audioCapabilities.supportsEncoding(getEncodingForMimeType(paramString)));
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
    throws AudioTrack.WriteException
  {
    if ((this.handledEndOfStream) || (!isInitialized())) {}
    while (!drainAudioProcessorsToEndOfStream()) {
      return;
    }
    this.audioTrackUtil.handleEndOfStream(getWrittenFrames());
    this.bytesUntilNextAvSync = 0;
    this.handledEndOfStream = true;
  }
  
  public void release()
  {
    reset();
    releaseKeepSessionIdAudioTrack();
    AudioProcessor[] arrayOfAudioProcessor = this.availableAudioProcessors;
    int j = arrayOfAudioProcessor.length;
    int i = 0;
    while (i < j)
    {
      arrayOfAudioProcessor[i].reset();
      i += 1;
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
        int i = 0;
        while (i < this.audioProcessors.length)
        {
          localObject = this.audioProcessors[i];
          ((AudioProcessor)localObject).flush();
          this.outputBuffers[i] = ((AudioProcessor)localObject).getOutput();
          i += 1;
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
            AudioTrack.this.releasingConditionVariable.open();
          }
        }
      }.start();
    }
  }
  
  public void setAudioAttributes(AudioAttributes paramAudioAttributes)
  {
    if (this.audioAttributes.equals(paramAudioAttributes)) {}
    do
    {
      return;
      this.audioAttributes = paramAudioAttributes;
    } while (this.tunneling);
    reset();
    this.audioSessionId = 0;
  }
  
  public void setAudioSessionId(int paramInt)
  {
    if (this.audioSessionId != paramInt)
    {
      this.audioSessionId = paramInt;
      reset();
    }
  }
  
  public PlaybackParameters setPlaybackParameters(PlaybackParameters paramPlaybackParameters)
  {
    if (this.passthrough)
    {
      this.playbackParameters = PlaybackParameters.DEFAULT;
      return this.playbackParameters;
    }
    PlaybackParameters localPlaybackParameters = new PlaybackParameters(this.sonicAudioProcessor.setSpeed(paramPlaybackParameters.speed), this.sonicAudioProcessor.setPitch(paramPlaybackParameters.pitch));
    if (this.drainingPlaybackParameters != null)
    {
      paramPlaybackParameters = this.drainingPlaybackParameters;
      if (!localPlaybackParameters.equals(paramPlaybackParameters))
      {
        if (!isInitialized()) {
          break label121;
        }
        this.drainingPlaybackParameters = localPlaybackParameters;
      }
    }
    for (;;)
    {
      return this.playbackParameters;
      if (!this.playbackParametersCheckpoints.isEmpty())
      {
        paramPlaybackParameters = ((PlaybackParametersCheckpoint)this.playbackParametersCheckpoints.getLast()).playbackParameters;
        break;
      }
      paramPlaybackParameters = this.playbackParameters;
      break;
      label121:
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
    protected android.media.AudioTrack audioTrack;
    private long endPlaybackHeadPosition;
    private long lastRawPlaybackHeadPosition;
    private boolean needsPassthroughWorkaround;
    private long passthroughWorkaroundPauseOffset;
    private long rawPlaybackHeadWrapCount;
    private int sampleRate;
    private long stopPlaybackHeadPosition;
    private long stopTimestampUs;
    
    public long getPlaybackHeadPosition()
    {
      long l1 = 0L;
      if (this.stopTimestampUs != -9223372036854775807L)
      {
        l1 = SystemClock.elapsedRealtime();
        l2 = this.stopTimestampUs;
        l1 = this.sampleRate * (l1 * 1000L - l2) / 1000000L;
        l1 = Math.min(this.endPlaybackHeadPosition, this.stopPlaybackHeadPosition + l1);
      }
      int i;
      do
      {
        return l1;
        i = this.audioTrack.getPlayState();
      } while (i == 1);
      long l2 = 0xFFFFFFFF & this.audioTrack.getPlaybackHeadPosition();
      l1 = l2;
      if (this.needsPassthroughWorkaround)
      {
        if ((i == 2) && (l2 == 0L)) {
          this.passthroughWorkaroundPauseOffset = this.lastRawPlaybackHeadPosition;
        }
        l1 = l2 + this.passthroughWorkaroundPauseOffset;
      }
      if (this.lastRawPlaybackHeadPosition > l1) {
        this.rawPlaybackHeadWrapCount += 1L;
      }
      this.lastRawPlaybackHeadPosition = l1;
      return (this.rawPlaybackHeadWrapCount << 32) + l1;
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
    
    public void pause()
    {
      if (this.stopTimestampUs != -9223372036854775807L) {
        return;
      }
      this.audioTrack.pause();
    }
    
    public void reconfigure(android.media.AudioTrack paramAudioTrack, boolean paramBoolean)
    {
      this.audioTrack = paramAudioTrack;
      this.needsPassthroughWorkaround = paramBoolean;
      this.stopTimestampUs = -9223372036854775807L;
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
    extends AudioTrack.AudioTrackUtil
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
    
    public void reconfigure(android.media.AudioTrack paramAudioTrack, boolean paramBoolean)
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
  
  public static final class ConfigurationException
    extends Exception
  {
    public ConfigurationException(String paramString)
    {
      super();
    }
    
    public ConfigurationException(Throwable paramThrowable)
    {
      super();
    }
  }
  
  public static final class InitializationException
    extends Exception
  {
    public final int audioTrackState;
    
    public InitializationException(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super();
      this.audioTrackState = paramInt1;
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
  
  public static abstract interface Listener
  {
    public abstract void onAudioSessionId(int paramInt);
    
    public abstract void onPositionDiscontinuity();
    
    public abstract void onUnderrun(int paramInt, long paramLong1, long paramLong2);
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
  
  public static final class WriteException
    extends Exception
  {
    public final int errorCode;
    
    public WriteException(int paramInt)
    {
      super();
      this.errorCode = paramInt;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/audio/AudioTrack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */