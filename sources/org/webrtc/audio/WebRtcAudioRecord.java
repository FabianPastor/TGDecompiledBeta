package org.webrtc.audio;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioRecordingConfiguration;
import android.os.Build;
import android.os.Process;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.webrtc.CalledByNative;
import org.webrtc.Logging;
import org.webrtc.ThreadUtils;
import org.webrtc.audio.JavaAudioDeviceModule;

class WebRtcAudioRecord {
    private static final int AUDIO_RECORD_START = 0;
    private static final int AUDIO_RECORD_STOP = 1;
    private static final long AUDIO_RECORD_THREAD_JOIN_TIMEOUT_MS = 2000;
    private static final int BUFFERS_PER_SECOND = 100;
    private static final int BUFFER_SIZE_FACTOR = 2;
    private static final int CALLBACK_BUFFER_SIZE_MS = 10;
    private static final int CHECK_REC_STATUS_DELAY_MS = 100;
    public static final int DEFAULT_AUDIO_FORMAT = 2;
    public static final int DEFAULT_AUDIO_SOURCE = 7;
    private static final String TAG = "WebRtcAudioRecordExternal";
    /* access modifiers changed from: private */
    public static final AtomicInteger nextSchedulerId = new AtomicInteger(0);
    private final int audioFormat;
    private final AudioManager audioManager;
    /* access modifiers changed from: private */
    public AudioRecord audioRecord;
    /* access modifiers changed from: private */
    public final JavaAudioDeviceModule.SamplesReadyCallback audioSamplesReadyCallback;
    private final int audioSource;
    private final AtomicReference<Boolean> audioSourceMatchesRecordingSessionRef;
    private AudioRecordThread audioThread;
    /* access modifiers changed from: private */
    public ByteBuffer byteBuffer;
    private final Context context;
    private final WebRtcAudioEffects effects;
    /* access modifiers changed from: private */
    public byte[] emptyBytes;
    private final JavaAudioDeviceModule.AudioRecordErrorCallback errorCallback;
    private final ScheduledExecutorService executor;
    private ScheduledFuture<String> future;
    private final boolean isAcousticEchoCancelerSupported;
    private final boolean isNoiseSuppressorSupported;
    /* access modifiers changed from: private */
    public volatile boolean microphoneMute;
    /* access modifiers changed from: private */
    public long nativeAudioRecord;
    private AudioDeviceInfo preferredDevice;
    private final JavaAudioDeviceModule.AudioRecordStateCallback stateCallback;

    private static String audioStateToString(int i) {
        return i != 0 ? i != 1 ? "INVALID" : "STOP" : "START";
    }

    private int channelCountToConfiguration(int i) {
        return i == 1 ? 16 : 12;
    }

    private native void nativeCacheDirectBufferAddress(long j, ByteBuffer byteBuffer2);

    /* access modifiers changed from: private */
    public native void nativeDataIsRecorded(long j, int i);

    private class AudioRecordThread extends Thread {
        private volatile boolean keepAlive = true;

        public AudioRecordThread(String str) {
            super(str);
        }

        public void run() {
            Process.setThreadPriority(-19);
            Logging.d("WebRtcAudioRecordExternal", "AudioRecordThread" + WebRtcAudioUtils.getThreadInfo());
            WebRtcAudioRecord.assertTrue(WebRtcAudioRecord.this.audioRecord.getRecordingState() == 3);
            WebRtcAudioRecord.this.doAudioRecordStateCallback(0);
            System.nanoTime();
            while (this.keepAlive) {
                int read = WebRtcAudioRecord.this.audioRecord.read(WebRtcAudioRecord.this.byteBuffer, WebRtcAudioRecord.this.byteBuffer.capacity());
                if (read == WebRtcAudioRecord.this.byteBuffer.capacity()) {
                    if (WebRtcAudioRecord.this.microphoneMute) {
                        WebRtcAudioRecord.this.byteBuffer.clear();
                        WebRtcAudioRecord.this.byteBuffer.put(WebRtcAudioRecord.this.emptyBytes);
                    }
                    if (this.keepAlive) {
                        WebRtcAudioRecord webRtcAudioRecord = WebRtcAudioRecord.this;
                        webRtcAudioRecord.nativeDataIsRecorded(webRtcAudioRecord.nativeAudioRecord, read);
                    }
                    if (WebRtcAudioRecord.this.audioSamplesReadyCallback != null) {
                        WebRtcAudioRecord.this.audioSamplesReadyCallback.onWebRtcAudioRecordSamplesReady(new JavaAudioDeviceModule.AudioSamples(WebRtcAudioRecord.this.audioRecord.getAudioFormat(), WebRtcAudioRecord.this.audioRecord.getChannelCount(), WebRtcAudioRecord.this.audioRecord.getSampleRate(), Arrays.copyOfRange(WebRtcAudioRecord.this.byteBuffer.array(), WebRtcAudioRecord.this.byteBuffer.arrayOffset(), WebRtcAudioRecord.this.byteBuffer.capacity() + WebRtcAudioRecord.this.byteBuffer.arrayOffset())));
                    }
                } else {
                    String str = "AudioRecord.read failed: " + read;
                    Logging.e("WebRtcAudioRecordExternal", str);
                    if (read == -3) {
                        this.keepAlive = false;
                        WebRtcAudioRecord.this.reportWebRtcAudioRecordError(str);
                    }
                }
            }
            try {
                if (WebRtcAudioRecord.this.audioRecord != null) {
                    WebRtcAudioRecord.this.audioRecord.stop();
                    WebRtcAudioRecord.this.doAudioRecordStateCallback(1);
                }
            } catch (IllegalStateException e) {
                Logging.e("WebRtcAudioRecordExternal", "AudioRecord.stop failed: " + e.getMessage());
            }
        }

        public void stopThread() {
            Logging.d("WebRtcAudioRecordExternal", "stopThread");
            this.keepAlive = false;
        }
    }

    @CalledByNative
    WebRtcAudioRecord(Context context2, AudioManager audioManager2) {
        this(context2, newDefaultScheduler(), audioManager2, 7, 2, (JavaAudioDeviceModule.AudioRecordErrorCallback) null, (JavaAudioDeviceModule.AudioRecordStateCallback) null, (JavaAudioDeviceModule.SamplesReadyCallback) null, WebRtcAudioEffects.isAcousticEchoCancelerSupported(), WebRtcAudioEffects.isNoiseSuppressorSupported());
    }

    public WebRtcAudioRecord(Context context2, ScheduledExecutorService scheduledExecutorService, AudioManager audioManager2, int i, int i2, JavaAudioDeviceModule.AudioRecordErrorCallback audioRecordErrorCallback, JavaAudioDeviceModule.AudioRecordStateCallback audioRecordStateCallback, JavaAudioDeviceModule.SamplesReadyCallback samplesReadyCallback, boolean z, boolean z2) {
        this.effects = new WebRtcAudioEffects();
        this.audioSourceMatchesRecordingSessionRef = new AtomicReference<>();
        if (z && !WebRtcAudioEffects.isAcousticEchoCancelerSupported()) {
            throw new IllegalArgumentException("HW AEC not supported");
        } else if (!z2 || WebRtcAudioEffects.isNoiseSuppressorSupported()) {
            this.context = context2;
            this.executor = scheduledExecutorService;
            this.audioManager = audioManager2;
            this.audioSource = i;
            this.audioFormat = i2;
            this.errorCallback = audioRecordErrorCallback;
            this.stateCallback = audioRecordStateCallback;
            this.audioSamplesReadyCallback = samplesReadyCallback;
            this.isAcousticEchoCancelerSupported = z;
            this.isNoiseSuppressorSupported = z2;
            Logging.d("WebRtcAudioRecordExternal", "ctor" + WebRtcAudioUtils.getThreadInfo());
        } else {
            throw new IllegalArgumentException("HW NS not supported");
        }
    }

    @CalledByNative
    public void setNativeAudioRecord(long j) {
        this.nativeAudioRecord = j;
    }

    /* access modifiers changed from: package-private */
    @CalledByNative
    public boolean isAcousticEchoCancelerSupported() {
        return this.isAcousticEchoCancelerSupported;
    }

    /* access modifiers changed from: package-private */
    @CalledByNative
    public boolean isNoiseSuppressorSupported() {
        return this.isNoiseSuppressorSupported;
    }

    /* access modifiers changed from: package-private */
    @CalledByNative
    public boolean isAudioConfigVerified() {
        return this.audioSourceMatchesRecordingSessionRef.get() != null;
    }

    /* access modifiers changed from: package-private */
    @CalledByNative
    public boolean isAudioSourceMatchingRecordingSession() {
        Boolean bool = this.audioSourceMatchesRecordingSessionRef.get();
        if (bool != null) {
            return bool.booleanValue();
        }
        Logging.w("WebRtcAudioRecordExternal", "Audio configuration has not yet been verified");
        return false;
    }

    @CalledByNative
    private boolean enableBuiltInAEC(boolean z) {
        Logging.d("WebRtcAudioRecordExternal", "enableBuiltInAEC(" + z + ")");
        return this.effects.setAEC(z);
    }

    @CalledByNative
    private boolean enableBuiltInNS(boolean z) {
        Logging.d("WebRtcAudioRecordExternal", "enableBuiltInNS(" + z + ")");
        return this.effects.setNS(z);
    }

    @CalledByNative
    private int initRecording(int i, int i2) {
        Logging.d("WebRtcAudioRecordExternal", "initRecording(sampleRate=" + i + ", channels=" + i2 + ")");
        if (this.audioRecord != null) {
            reportWebRtcAudioRecordInitError("InitRecording called twice without StopRecording.");
            return -1;
        }
        int i3 = i / 100;
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(getBytesPerSample(this.audioFormat) * i2 * i3);
        this.byteBuffer = allocateDirect;
        if (!allocateDirect.hasArray()) {
            reportWebRtcAudioRecordInitError("ByteBuffer does not have backing array.");
            return -1;
        }
        Logging.d("WebRtcAudioRecordExternal", "byteBuffer.capacity: " + this.byteBuffer.capacity());
        this.emptyBytes = new byte[this.byteBuffer.capacity()];
        nativeCacheDirectBufferAddress(this.nativeAudioRecord, this.byteBuffer);
        int channelCountToConfiguration = channelCountToConfiguration(i2);
        int minBufferSize = AudioRecord.getMinBufferSize(i, channelCountToConfiguration, this.audioFormat);
        if (minBufferSize == -1 || minBufferSize == -2) {
            reportWebRtcAudioRecordInitError("AudioRecord.getMinBufferSize failed: " + minBufferSize);
            return -1;
        }
        Logging.d("WebRtcAudioRecordExternal", "AudioRecord.getMinBufferSize: " + minBufferSize);
        int max = Math.max(minBufferSize * 2, this.byteBuffer.capacity());
        Logging.d("WebRtcAudioRecordExternal", "bufferSizeInBytes: " + max);
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                this.audioRecord = createAudioRecordOnMOrHigher(this.audioSource, i, channelCountToConfiguration, this.audioFormat, max);
                this.audioSourceMatchesRecordingSessionRef.set((Object) null);
                AudioDeviceInfo audioDeviceInfo = this.preferredDevice;
                if (audioDeviceInfo != null) {
                    setPreferredDevice(audioDeviceInfo);
                }
            } else {
                this.audioRecord = createAudioRecordOnLowerThanM(this.audioSource, i, channelCountToConfiguration, this.audioFormat, max);
                this.audioSourceMatchesRecordingSessionRef.set((Object) null);
            }
            AudioRecord audioRecord2 = this.audioRecord;
            if (audioRecord2 == null || audioRecord2.getState() != 1) {
                reportWebRtcAudioRecordInitError("Creation or initialization of audio recorder failed.");
                releaseAudioResources();
                return -1;
            }
            this.effects.enable(this.audioRecord.getAudioSessionId());
            logMainParameters();
            logMainParametersExtended();
            int logRecordingConfigurations = logRecordingConfigurations(this.audioRecord, false);
            if (logRecordingConfigurations != 0) {
                Logging.w("WebRtcAudioRecordExternal", "Potential microphone conflict. Active sessions: " + logRecordingConfigurations);
            }
            return i3;
        } catch (IllegalArgumentException | UnsupportedOperationException e) {
            reportWebRtcAudioRecordInitError(e.getMessage());
            releaseAudioResources();
            return -1;
        }
    }

    /* access modifiers changed from: package-private */
    @TargetApi(23)
    public void setPreferredDevice(AudioDeviceInfo audioDeviceInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("setPreferredDevice ");
        sb.append(audioDeviceInfo != null ? Integer.valueOf(audioDeviceInfo.getId()) : null);
        Logging.d("WebRtcAudioRecordExternal", sb.toString());
        this.preferredDevice = audioDeviceInfo;
        AudioRecord audioRecord2 = this.audioRecord;
        if (audioRecord2 != null && !audioRecord2.setPreferredDevice(audioDeviceInfo)) {
            Logging.e("WebRtcAudioRecordExternal", "setPreferredDevice failed");
        }
    }

    @CalledByNative
    private boolean startRecording() {
        Logging.d("WebRtcAudioRecordExternal", "startRecording");
        assertTrue(this.audioRecord != null);
        assertTrue(this.audioThread == null);
        try {
            this.audioRecord.startRecording();
            if (this.audioRecord.getRecordingState() != 3) {
                JavaAudioDeviceModule.AudioRecordStartErrorCode audioRecordStartErrorCode = JavaAudioDeviceModule.AudioRecordStartErrorCode.AUDIO_RECORD_START_STATE_MISMATCH;
                reportWebRtcAudioRecordStartError(audioRecordStartErrorCode, "AudioRecord.startRecording failed - incorrect state: " + this.audioRecord.getRecordingState());
                return false;
            }
            AudioRecordThread audioRecordThread = new AudioRecordThread("AudioRecordJavaThread");
            this.audioThread = audioRecordThread;
            audioRecordThread.start();
            scheduleLogRecordingConfigurationsTask(this.audioRecord);
            return true;
        } catch (IllegalStateException e) {
            JavaAudioDeviceModule.AudioRecordStartErrorCode audioRecordStartErrorCode2 = JavaAudioDeviceModule.AudioRecordStartErrorCode.AUDIO_RECORD_START_EXCEPTION;
            reportWebRtcAudioRecordStartError(audioRecordStartErrorCode2, "AudioRecord.startRecording failed: " + e.getMessage());
            return false;
        }
    }

    @CalledByNative
    private boolean stopRecording() {
        Logging.d("WebRtcAudioRecordExternal", "stopRecording");
        assertTrue(this.audioThread != null);
        ScheduledFuture<String> scheduledFuture = this.future;
        if (scheduledFuture != null) {
            if (!scheduledFuture.isDone()) {
                this.future.cancel(true);
            }
            this.future = null;
        }
        this.audioThread.stopThread();
        if (!ThreadUtils.joinUninterruptibly(this.audioThread, 2000)) {
            Logging.e("WebRtcAudioRecordExternal", "Join of AudioRecordJavaThread timed out");
            WebRtcAudioUtils.logAudioState("WebRtcAudioRecordExternal", this.context, this.audioManager);
        }
        this.audioThread = null;
        this.effects.release();
        releaseAudioResources();
        return true;
    }

    @TargetApi(23)
    private static AudioRecord createAudioRecordOnMOrHigher(int i, int i2, int i3, int i4, int i5) {
        Logging.d("WebRtcAudioRecordExternal", "createAudioRecordOnMOrHigher");
        return new AudioRecord.Builder().setAudioSource(i).setAudioFormat(new AudioFormat.Builder().setEncoding(i4).setSampleRate(i2).setChannelMask(i3).build()).setBufferSizeInBytes(i5).build();
    }

    private static AudioRecord createAudioRecordOnLowerThanM(int i, int i2, int i3, int i4, int i5) {
        Logging.d("WebRtcAudioRecordExternal", "createAudioRecordOnLowerThanM");
        return new AudioRecord(i, i2, i3, i4, i5);
    }

    private void logMainParameters() {
        Logging.d("WebRtcAudioRecordExternal", "AudioRecord: session ID: " + this.audioRecord.getAudioSessionId() + ", channels: " + this.audioRecord.getChannelCount() + ", sample rate: " + this.audioRecord.getSampleRate());
    }

    @TargetApi(23)
    private void logMainParametersExtended() {
        if (Build.VERSION.SDK_INT >= 23) {
            Logging.d("WebRtcAudioRecordExternal", "AudioRecord: buffer size in frames: " + this.audioRecord.getBufferSizeInFrames());
        }
    }

    @TargetApi(24)
    private int logRecordingConfigurations(AudioRecord audioRecord2, boolean z) {
        if (Build.VERSION.SDK_INT < 24) {
            Logging.w("WebRtcAudioRecordExternal", "AudioManager#getActiveRecordingConfigurations() requires N or higher");
            return 0;
        } else if (audioRecord2 == null) {
            return 0;
        } else {
            List<AudioRecordingConfiguration> activeRecordingConfigurations = this.audioManager.getActiveRecordingConfigurations();
            int size = activeRecordingConfigurations.size();
            Logging.d("WebRtcAudioRecordExternal", "Number of active recording sessions: " + size);
            if (size > 0) {
                logActiveRecordingConfigs(audioRecord2.getAudioSessionId(), activeRecordingConfigurations);
                if (z) {
                    this.audioSourceMatchesRecordingSessionRef.set(Boolean.valueOf(verifyAudioConfig(audioRecord2.getAudioSource(), audioRecord2.getAudioSessionId(), audioRecord2.getFormat(), audioRecord2.getRoutedDevice(), activeRecordingConfigurations)));
                }
            }
            return size;
        }
    }

    /* access modifiers changed from: private */
    public static void assertTrue(boolean z) {
        if (!z) {
            throw new AssertionError("Expected condition to be true");
        }
    }

    public void setMicrophoneMute(boolean z) {
        Logging.w("WebRtcAudioRecordExternal", "setMicrophoneMute(" + z + ")");
        this.microphoneMute = z;
    }

    private void releaseAudioResources() {
        Logging.d("WebRtcAudioRecordExternal", "releaseAudioResources");
        AudioRecord audioRecord2 = this.audioRecord;
        if (audioRecord2 != null) {
            audioRecord2.release();
            this.audioRecord = null;
        }
        this.audioSourceMatchesRecordingSessionRef.set((Object) null);
    }

    private void reportWebRtcAudioRecordInitError(String str) {
        Logging.e("WebRtcAudioRecordExternal", "Init recording error: " + str);
        WebRtcAudioUtils.logAudioState("WebRtcAudioRecordExternal", this.context, this.audioManager);
        logRecordingConfigurations(this.audioRecord, false);
        JavaAudioDeviceModule.AudioRecordErrorCallback audioRecordErrorCallback = this.errorCallback;
        if (audioRecordErrorCallback != null) {
            audioRecordErrorCallback.onWebRtcAudioRecordInitError(str);
        }
    }

    private void reportWebRtcAudioRecordStartError(JavaAudioDeviceModule.AudioRecordStartErrorCode audioRecordStartErrorCode, String str) {
        Logging.e("WebRtcAudioRecordExternal", "Start recording error: " + audioRecordStartErrorCode + ". " + str);
        WebRtcAudioUtils.logAudioState("WebRtcAudioRecordExternal", this.context, this.audioManager);
        logRecordingConfigurations(this.audioRecord, false);
        JavaAudioDeviceModule.AudioRecordErrorCallback audioRecordErrorCallback = this.errorCallback;
        if (audioRecordErrorCallback != null) {
            audioRecordErrorCallback.onWebRtcAudioRecordStartError(audioRecordStartErrorCode, str);
        }
    }

    /* access modifiers changed from: private */
    public void reportWebRtcAudioRecordError(String str) {
        Logging.e("WebRtcAudioRecordExternal", "Run-time recording error: " + str);
        WebRtcAudioUtils.logAudioState("WebRtcAudioRecordExternal", this.context, this.audioManager);
        JavaAudioDeviceModule.AudioRecordErrorCallback audioRecordErrorCallback = this.errorCallback;
        if (audioRecordErrorCallback != null) {
            audioRecordErrorCallback.onWebRtcAudioRecordError(str);
        }
    }

    /* access modifiers changed from: private */
    public void doAudioRecordStateCallback(int i) {
        Logging.d("WebRtcAudioRecordExternal", "doAudioRecordStateCallback: " + audioStateToString(i));
        JavaAudioDeviceModule.AudioRecordStateCallback audioRecordStateCallback = this.stateCallback;
        if (audioRecordStateCallback == null) {
            return;
        }
        if (i == 0) {
            audioRecordStateCallback.onWebRtcAudioRecordStart();
        } else if (i == 1) {
            audioRecordStateCallback.onWebRtcAudioRecordStop();
        } else {
            Logging.e("WebRtcAudioRecordExternal", "Invalid audio state");
        }
    }

    private static int getBytesPerSample(int i) {
        int i2 = 1;
        if (!(i == 1 || i == 2)) {
            if (i != 3) {
                i2 = 4;
                if (i != 4) {
                    if (i != 13) {
                        throw new IllegalArgumentException("Bad audio format " + i);
                    }
                }
            }
            return i2;
        }
        return 2;
    }

    private void scheduleLogRecordingConfigurationsTask(AudioRecord audioRecord2) {
        Logging.d("WebRtcAudioRecordExternal", "scheduleLogRecordingConfigurationsTask");
        if (Build.VERSION.SDK_INT >= 24) {
            $$Lambda$WebRtcAudioRecord$lg6CuAUIs5U3enlajsxoI_3Bk r0 = new Callable(audioRecord2) {
                public final /* synthetic */ AudioRecord f$1;

                {
                    this.f$1 = r2;
                }

                public final Object call() {
                    return WebRtcAudioRecord.this.lambda$scheduleLogRecordingConfigurationsTask$0$WebRtcAudioRecord(this.f$1);
                }
            };
            ScheduledFuture<String> scheduledFuture = this.future;
            if (scheduledFuture != null && !scheduledFuture.isDone()) {
                this.future.cancel(true);
            }
            this.future = this.executor.schedule(r0, 100, TimeUnit.MILLISECONDS);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$scheduleLogRecordingConfigurationsTask$0 */
    public /* synthetic */ String lambda$scheduleLogRecordingConfigurationsTask$0$WebRtcAudioRecord(AudioRecord audioRecord2) throws Exception {
        if (this.audioRecord == audioRecord2) {
            logRecordingConfigurations(audioRecord2, true);
            return "Scheduled task is done";
        }
        Logging.d("WebRtcAudioRecordExternal", "audio record has changed");
        return "Scheduled task is done";
    }

    @TargetApi(24)
    private static boolean logActiveRecordingConfigs(int i, List<AudioRecordingConfiguration> list) {
        assertTrue(!list.isEmpty());
        Logging.d("WebRtcAudioRecordExternal", "AudioRecordingConfigurations: ");
        for (AudioRecordingConfiguration next : list) {
            StringBuilder sb = new StringBuilder();
            int clientAudioSource = next.getClientAudioSource();
            sb.append("  client audio source=");
            sb.append(WebRtcAudioUtils.audioSourceToString(clientAudioSource));
            sb.append(", client session id=");
            sb.append(next.getClientAudioSessionId());
            sb.append(" (");
            sb.append(i);
            sb.append(")");
            sb.append("\n");
            AudioFormat format = next.getFormat();
            sb.append("  Device AudioFormat: ");
            sb.append("channel count=");
            sb.append(format.getChannelCount());
            sb.append(", channel index mask=");
            sb.append(format.getChannelIndexMask());
            sb.append(", channel mask=");
            sb.append(WebRtcAudioUtils.channelMaskToString(format.getChannelMask()));
            sb.append(", encoding=");
            sb.append(WebRtcAudioUtils.audioEncodingToString(format.getEncoding()));
            sb.append(", sample rate=");
            sb.append(format.getSampleRate());
            sb.append("\n");
            AudioFormat clientFormat = next.getClientFormat();
            sb.append("  Client AudioFormat: ");
            sb.append("channel count=");
            sb.append(clientFormat.getChannelCount());
            sb.append(", channel index mask=");
            sb.append(clientFormat.getChannelIndexMask());
            sb.append(", channel mask=");
            sb.append(WebRtcAudioUtils.channelMaskToString(clientFormat.getChannelMask()));
            sb.append(", encoding=");
            sb.append(WebRtcAudioUtils.audioEncodingToString(clientFormat.getEncoding()));
            sb.append(", sample rate=");
            sb.append(clientFormat.getSampleRate());
            sb.append("\n");
            AudioDeviceInfo audioDevice = next.getAudioDevice();
            if (audioDevice != null) {
                assertTrue(audioDevice.isSource());
                sb.append("  AudioDevice: ");
                sb.append("type=");
                sb.append(WebRtcAudioUtils.deviceTypeToString(audioDevice.getType()));
                sb.append(", id=");
                sb.append(audioDevice.getId());
            }
            Logging.d("WebRtcAudioRecordExternal", sb.toString());
        }
        return true;
    }

    @TargetApi(24)
    private static boolean verifyAudioConfig(int i, int i2, AudioFormat audioFormat2, AudioDeviceInfo audioDeviceInfo, List<AudioRecordingConfiguration> list) {
        assertTrue(!list.isEmpty());
        for (AudioRecordingConfiguration next : list) {
            AudioDeviceInfo audioDevice = next.getAudioDevice();
            if (audioDevice != null && next.getClientAudioSource() == i && next.getClientAudioSessionId() == i2 && next.getClientFormat().getEncoding() == audioFormat2.getEncoding() && next.getClientFormat().getSampleRate() == audioFormat2.getSampleRate() && next.getClientFormat().getChannelMask() == audioFormat2.getChannelMask() && next.getClientFormat().getChannelIndexMask() == audioFormat2.getChannelIndexMask() && next.getFormat().getEncoding() != 0 && next.getFormat().getSampleRate() > 0) {
                if (!(next.getFormat().getChannelMask() == 0 && next.getFormat().getChannelIndexMask() == 0) && checkDeviceMatch(audioDevice, audioDeviceInfo)) {
                    Logging.d("WebRtcAudioRecordExternal", "verifyAudioConfig: PASS");
                    return true;
                }
            }
        }
        Logging.e("WebRtcAudioRecordExternal", "verifyAudioConfig: FAILED");
        return false;
    }

    @TargetApi(24)
    private static boolean checkDeviceMatch(AudioDeviceInfo audioDeviceInfo, AudioDeviceInfo audioDeviceInfo2) {
        return audioDeviceInfo.getId() == audioDeviceInfo2.getId() && audioDeviceInfo.getType() == audioDeviceInfo2.getType();
    }

    static ScheduledExecutorService newDefaultScheduler() {
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        return Executors.newScheduledThreadPool(0, new ThreadFactory() {
            public Thread newThread(Runnable runnable) {
                Thread newThread = Executors.defaultThreadFactory().newThread(runnable);
                newThread.setName(String.format("WebRtcAudioRecordScheduler-%s-%s", new Object[]{Integer.valueOf(WebRtcAudioRecord.nextSchedulerId.getAndIncrement()), Integer.valueOf(atomicInteger.getAndIncrement())}));
                return newThread;
            }
        });
    }
}
