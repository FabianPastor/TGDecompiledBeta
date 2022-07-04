package org.webrtc.audio;

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

    private native void nativeCacheDirectBufferAddress(long j, ByteBuffer byteBuffer2);

    /* access modifiers changed from: private */
    public native void nativeDataIsRecorded(long j, int i);

    private class AudioRecordThread extends Thread {
        private volatile boolean keepAlive = true;

        public AudioRecordThread(String name) {
            super(name);
        }

        public void run() {
            Process.setThreadPriority(-19);
            Logging.d("WebRtcAudioRecordExternal", "AudioRecordThread" + WebRtcAudioUtils.getThreadInfo());
            WebRtcAudioRecord.assertTrue(WebRtcAudioRecord.this.audioRecord.getRecordingState() == 3);
            WebRtcAudioRecord.this.doAudioRecordStateCallback(0);
            long nanoTime = System.nanoTime();
            while (this.keepAlive) {
                int bytesRead = WebRtcAudioRecord.this.audioRecord.read(WebRtcAudioRecord.this.byteBuffer, WebRtcAudioRecord.this.byteBuffer.capacity());
                if (bytesRead == WebRtcAudioRecord.this.byteBuffer.capacity()) {
                    if (WebRtcAudioRecord.this.microphoneMute) {
                        WebRtcAudioRecord.this.byteBuffer.clear();
                        WebRtcAudioRecord.this.byteBuffer.put(WebRtcAudioRecord.this.emptyBytes);
                    }
                    if (this.keepAlive) {
                        WebRtcAudioRecord webRtcAudioRecord = WebRtcAudioRecord.this;
                        webRtcAudioRecord.nativeDataIsRecorded(webRtcAudioRecord.nativeAudioRecord, bytesRead);
                    }
                    if (WebRtcAudioRecord.this.audioSamplesReadyCallback != null) {
                        WebRtcAudioRecord.this.audioSamplesReadyCallback.onWebRtcAudioRecordSamplesReady(new JavaAudioDeviceModule.AudioSamples(WebRtcAudioRecord.this.audioRecord.getAudioFormat(), WebRtcAudioRecord.this.audioRecord.getChannelCount(), WebRtcAudioRecord.this.audioRecord.getSampleRate(), Arrays.copyOfRange(WebRtcAudioRecord.this.byteBuffer.array(), WebRtcAudioRecord.this.byteBuffer.arrayOffset(), WebRtcAudioRecord.this.byteBuffer.capacity() + WebRtcAudioRecord.this.byteBuffer.arrayOffset())));
                    }
                } else {
                    String errorMessage = "AudioRecord.read failed: " + bytesRead;
                    Logging.e("WebRtcAudioRecordExternal", errorMessage);
                    if (bytesRead == -3) {
                        this.keepAlive = false;
                        WebRtcAudioRecord.this.reportWebRtcAudioRecordError(errorMessage);
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

    WebRtcAudioRecord(Context context2, AudioManager audioManager2) {
        this(context2, newDefaultScheduler(), audioManager2, 7, 2, (JavaAudioDeviceModule.AudioRecordErrorCallback) null, (JavaAudioDeviceModule.AudioRecordStateCallback) null, (JavaAudioDeviceModule.SamplesReadyCallback) null, WebRtcAudioEffects.isAcousticEchoCancelerSupported(), WebRtcAudioEffects.isNoiseSuppressorSupported());
    }

    public WebRtcAudioRecord(Context context2, ScheduledExecutorService scheduler, AudioManager audioManager2, int audioSource2, int audioFormat2, JavaAudioDeviceModule.AudioRecordErrorCallback errorCallback2, JavaAudioDeviceModule.AudioRecordStateCallback stateCallback2, JavaAudioDeviceModule.SamplesReadyCallback audioSamplesReadyCallback2, boolean isAcousticEchoCancelerSupported2, boolean isNoiseSuppressorSupported2) {
        this.effects = new WebRtcAudioEffects();
        this.audioSourceMatchesRecordingSessionRef = new AtomicReference<>();
        if (isAcousticEchoCancelerSupported2 && !WebRtcAudioEffects.isAcousticEchoCancelerSupported()) {
            throw new IllegalArgumentException("HW AEC not supported");
        } else if (!isNoiseSuppressorSupported2 || WebRtcAudioEffects.isNoiseSuppressorSupported()) {
            this.context = context2;
            this.executor = scheduler;
            this.audioManager = audioManager2;
            this.audioSource = audioSource2;
            this.audioFormat = audioFormat2;
            this.errorCallback = errorCallback2;
            this.stateCallback = stateCallback2;
            this.audioSamplesReadyCallback = audioSamplesReadyCallback2;
            this.isAcousticEchoCancelerSupported = isAcousticEchoCancelerSupported2;
            this.isNoiseSuppressorSupported = isNoiseSuppressorSupported2;
            Logging.d("WebRtcAudioRecordExternal", "ctor" + WebRtcAudioUtils.getThreadInfo());
        } else {
            throw new IllegalArgumentException("HW NS not supported");
        }
    }

    public void setNativeAudioRecord(long nativeAudioRecord2) {
        this.nativeAudioRecord = nativeAudioRecord2;
    }

    /* access modifiers changed from: package-private */
    public boolean isAcousticEchoCancelerSupported() {
        return this.isAcousticEchoCancelerSupported;
    }

    /* access modifiers changed from: package-private */
    public boolean isNoiseSuppressorSupported() {
        return this.isNoiseSuppressorSupported;
    }

    /* access modifiers changed from: package-private */
    public boolean isAudioConfigVerified() {
        return this.audioSourceMatchesRecordingSessionRef.get() != null;
    }

    /* access modifiers changed from: package-private */
    public boolean isAudioSourceMatchingRecordingSession() {
        Boolean audioSourceMatchesRecordingSession = this.audioSourceMatchesRecordingSessionRef.get();
        if (audioSourceMatchesRecordingSession != null) {
            return audioSourceMatchesRecordingSession.booleanValue();
        }
        Logging.w("WebRtcAudioRecordExternal", "Audio configuration has not yet been verified");
        return false;
    }

    private boolean enableBuiltInAEC(boolean enable) {
        Logging.d("WebRtcAudioRecordExternal", "enableBuiltInAEC(" + enable + ")");
        return this.effects.setAEC(enable);
    }

    private boolean enableBuiltInNS(boolean enable) {
        Logging.d("WebRtcAudioRecordExternal", "enableBuiltInNS(" + enable + ")");
        return this.effects.setNS(enable);
    }

    private int initRecording(int sampleRate, int channels) {
        Logging.d("WebRtcAudioRecordExternal", "initRecording(sampleRate=" + sampleRate + ", channels=" + channels + ")");
        if (this.audioRecord != null) {
            reportWebRtcAudioRecordInitError("InitRecording called twice without StopRecording.");
            return -1;
        }
        int framesPerBuffer = sampleRate / 100;
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(getBytesPerSample(this.audioFormat) * channels * framesPerBuffer);
        this.byteBuffer = allocateDirect;
        if (!allocateDirect.hasArray()) {
            reportWebRtcAudioRecordInitError("ByteBuffer does not have backing array.");
            return -1;
        }
        Logging.d("WebRtcAudioRecordExternal", "byteBuffer.capacity: " + this.byteBuffer.capacity());
        this.emptyBytes = new byte[this.byteBuffer.capacity()];
        nativeCacheDirectBufferAddress(this.nativeAudioRecord, this.byteBuffer);
        int channelConfig = channelCountToConfiguration(channels);
        int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, this.audioFormat);
        if (minBufferSize == -1 || minBufferSize == -2) {
            reportWebRtcAudioRecordInitError("AudioRecord.getMinBufferSize failed: " + minBufferSize);
            return -1;
        }
        Logging.d("WebRtcAudioRecordExternal", "AudioRecord.getMinBufferSize: " + minBufferSize);
        int bufferSizeInBytes = Math.max(minBufferSize * 2, this.byteBuffer.capacity());
        Logging.d("WebRtcAudioRecordExternal", "bufferSizeInBytes: " + bufferSizeInBytes);
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                this.audioRecord = createAudioRecordOnMOrHigher(this.audioSource, sampleRate, channelConfig, this.audioFormat, bufferSizeInBytes);
                this.audioSourceMatchesRecordingSessionRef.set((Object) null);
                AudioDeviceInfo audioDeviceInfo = this.preferredDevice;
                if (audioDeviceInfo != null) {
                    setPreferredDevice(audioDeviceInfo);
                }
            } else {
                this.audioRecord = createAudioRecordOnLowerThanM(this.audioSource, sampleRate, channelConfig, this.audioFormat, bufferSizeInBytes);
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
            int numActiveRecordingSessions = logRecordingConfigurations(this.audioRecord, false);
            if (numActiveRecordingSessions != 0) {
                Logging.w("WebRtcAudioRecordExternal", "Potential microphone conflict. Active sessions: " + numActiveRecordingSessions);
            }
            return framesPerBuffer;
        } catch (IllegalArgumentException | UnsupportedOperationException e) {
            reportWebRtcAudioRecordInitError(e.getMessage());
            releaseAudioResources();
            return -1;
        }
    }

    /* access modifiers changed from: package-private */
    public void setPreferredDevice(AudioDeviceInfo preferredDevice2) {
        StringBuilder sb = new StringBuilder();
        sb.append("setPreferredDevice ");
        sb.append(preferredDevice2 != null ? Integer.valueOf(preferredDevice2.getId()) : null);
        Logging.d("WebRtcAudioRecordExternal", sb.toString());
        this.preferredDevice = preferredDevice2;
        AudioRecord audioRecord2 = this.audioRecord;
        if (audioRecord2 != null && !audioRecord2.setPreferredDevice(preferredDevice2)) {
            Logging.e("WebRtcAudioRecordExternal", "setPreferredDevice failed");
        }
    }

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

    private static AudioRecord createAudioRecordOnMOrHigher(int audioSource2, int sampleRate, int channelConfig, int audioFormat2, int bufferSizeInBytes) {
        Logging.d("WebRtcAudioRecordExternal", "createAudioRecordOnMOrHigher");
        return new AudioRecord.Builder().setAudioSource(audioSource2).setAudioFormat(new AudioFormat.Builder().setEncoding(audioFormat2).setSampleRate(sampleRate).setChannelMask(channelConfig).build()).setBufferSizeInBytes(bufferSizeInBytes).build();
    }

    private static AudioRecord createAudioRecordOnLowerThanM(int audioSource2, int sampleRate, int channelConfig, int audioFormat2, int bufferSizeInBytes) {
        Logging.d("WebRtcAudioRecordExternal", "createAudioRecordOnLowerThanM");
        return new AudioRecord(audioSource2, sampleRate, channelConfig, audioFormat2, bufferSizeInBytes);
    }

    private void logMainParameters() {
        Logging.d("WebRtcAudioRecordExternal", "AudioRecord: session ID: " + this.audioRecord.getAudioSessionId() + ", channels: " + this.audioRecord.getChannelCount() + ", sample rate: " + this.audioRecord.getSampleRate());
    }

    private void logMainParametersExtended() {
        if (Build.VERSION.SDK_INT >= 23) {
            Logging.d("WebRtcAudioRecordExternal", "AudioRecord: buffer size in frames: " + this.audioRecord.getBufferSizeInFrames());
        }
    }

    private int logRecordingConfigurations(AudioRecord audioRecord2, boolean verifyAudioConfig) {
        if (Build.VERSION.SDK_INT < 24) {
            Logging.w("WebRtcAudioRecordExternal", "AudioManager#getActiveRecordingConfigurations() requires N or higher");
            return 0;
        } else if (audioRecord2 == null) {
            return 0;
        } else {
            List<AudioRecordingConfiguration> configs = this.audioManager.getActiveRecordingConfigurations();
            int numActiveRecordingSessions = configs.size();
            Logging.d("WebRtcAudioRecordExternal", "Number of active recording sessions: " + numActiveRecordingSessions);
            if (numActiveRecordingSessions > 0) {
                logActiveRecordingConfigs(audioRecord2.getAudioSessionId(), configs);
                if (verifyAudioConfig) {
                    this.audioSourceMatchesRecordingSessionRef.set(Boolean.valueOf(verifyAudioConfig(audioRecord2.getAudioSource(), audioRecord2.getAudioSessionId(), audioRecord2.getFormat(), audioRecord2.getRoutedDevice(), configs)));
                }
            }
            return numActiveRecordingSessions;
        }
    }

    /* access modifiers changed from: private */
    public static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected condition to be true");
        }
    }

    private int channelCountToConfiguration(int channels) {
        return channels == 1 ? 16 : 12;
    }

    public void setMicrophoneMute(boolean mute) {
        Logging.w("WebRtcAudioRecordExternal", "setMicrophoneMute(" + mute + ")");
        this.microphoneMute = mute;
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

    private void reportWebRtcAudioRecordInitError(String errorMessage) {
        Logging.e("WebRtcAudioRecordExternal", "Init recording error: " + errorMessage);
        WebRtcAudioUtils.logAudioState("WebRtcAudioRecordExternal", this.context, this.audioManager);
        logRecordingConfigurations(this.audioRecord, false);
        JavaAudioDeviceModule.AudioRecordErrorCallback audioRecordErrorCallback = this.errorCallback;
        if (audioRecordErrorCallback != null) {
            audioRecordErrorCallback.onWebRtcAudioRecordInitError(errorMessage);
        }
    }

    private void reportWebRtcAudioRecordStartError(JavaAudioDeviceModule.AudioRecordStartErrorCode errorCode, String errorMessage) {
        Logging.e("WebRtcAudioRecordExternal", "Start recording error: " + errorCode + ". " + errorMessage);
        WebRtcAudioUtils.logAudioState("WebRtcAudioRecordExternal", this.context, this.audioManager);
        logRecordingConfigurations(this.audioRecord, false);
        JavaAudioDeviceModule.AudioRecordErrorCallback audioRecordErrorCallback = this.errorCallback;
        if (audioRecordErrorCallback != null) {
            audioRecordErrorCallback.onWebRtcAudioRecordStartError(errorCode, errorMessage);
        }
    }

    /* access modifiers changed from: private */
    public void reportWebRtcAudioRecordError(String errorMessage) {
        Logging.e("WebRtcAudioRecordExternal", "Run-time recording error: " + errorMessage);
        WebRtcAudioUtils.logAudioState("WebRtcAudioRecordExternal", this.context, this.audioManager);
        JavaAudioDeviceModule.AudioRecordErrorCallback audioRecordErrorCallback = this.errorCallback;
        if (audioRecordErrorCallback != null) {
            audioRecordErrorCallback.onWebRtcAudioRecordError(errorMessage);
        }
    }

    /* access modifiers changed from: private */
    public void doAudioRecordStateCallback(int audioState) {
        Logging.d("WebRtcAudioRecordExternal", "doAudioRecordStateCallback: " + audioStateToString(audioState));
        JavaAudioDeviceModule.AudioRecordStateCallback audioRecordStateCallback = this.stateCallback;
        if (audioRecordStateCallback == null) {
            return;
        }
        if (audioState == 0) {
            audioRecordStateCallback.onWebRtcAudioRecordStart();
        } else if (audioState == 1) {
            audioRecordStateCallback.onWebRtcAudioRecordStop();
        } else {
            Logging.e("WebRtcAudioRecordExternal", "Invalid audio state");
        }
    }

    private static int getBytesPerSample(int audioFormat2) {
        switch (audioFormat2) {
            case 1:
            case 2:
            case 13:
                return 2;
            case 3:
                return 1;
            case 4:
                return 4;
            default:
                throw new IllegalArgumentException("Bad audio format " + audioFormat2);
        }
    }

    private void scheduleLogRecordingConfigurationsTask(AudioRecord audioRecord2) {
        Logging.d("WebRtcAudioRecordExternal", "scheduleLogRecordingConfigurationsTask");
        if (Build.VERSION.SDK_INT >= 24) {
            Callable<String> callable = new WebRtcAudioRecord$$ExternalSyntheticLambda0(this, audioRecord2);
            ScheduledFuture<String> scheduledFuture = this.future;
            if (scheduledFuture != null && !scheduledFuture.isDone()) {
                this.future.cancel(true);
            }
            this.future = this.executor.schedule(callable, 100, TimeUnit.MILLISECONDS);
        }
    }

    /* renamed from: lambda$scheduleLogRecordingConfigurationsTask$0$org-webrtc-audio-WebRtcAudioRecord  reason: not valid java name */
    public /* synthetic */ String m1677xd3061741(AudioRecord audioRecord2) throws Exception {
        if (this.audioRecord == audioRecord2) {
            logRecordingConfigurations(audioRecord2, true);
            return "Scheduled task is done";
        }
        Logging.d("WebRtcAudioRecordExternal", "audio record has changed");
        return "Scheduled task is done";
    }

    private static boolean logActiveRecordingConfigs(int session, List<AudioRecordingConfiguration> configs) {
        assertTrue(!configs.isEmpty());
        Logging.d("WebRtcAudioRecordExternal", "AudioRecordingConfigurations: ");
        for (AudioRecordingConfiguration config : configs) {
            StringBuilder conf = new StringBuilder();
            int audioSource2 = config.getClientAudioSource();
            conf.append("  client audio source=");
            conf.append(WebRtcAudioUtils.audioSourceToString(audioSource2));
            conf.append(", client session id=");
            conf.append(config.getClientAudioSessionId());
            conf.append(" (");
            conf.append(session);
            conf.append(")");
            conf.append("\n");
            AudioFormat format = config.getFormat();
            conf.append("  Device AudioFormat: ");
            conf.append("channel count=");
            conf.append(format.getChannelCount());
            conf.append(", channel index mask=");
            conf.append(format.getChannelIndexMask());
            conf.append(", channel mask=");
            conf.append(WebRtcAudioUtils.channelMaskToString(format.getChannelMask()));
            conf.append(", encoding=");
            conf.append(WebRtcAudioUtils.audioEncodingToString(format.getEncoding()));
            conf.append(", sample rate=");
            conf.append(format.getSampleRate());
            conf.append("\n");
            AudioFormat format2 = config.getClientFormat();
            conf.append("  Client AudioFormat: ");
            conf.append("channel count=");
            conf.append(format2.getChannelCount());
            conf.append(", channel index mask=");
            conf.append(format2.getChannelIndexMask());
            conf.append(", channel mask=");
            conf.append(WebRtcAudioUtils.channelMaskToString(format2.getChannelMask()));
            conf.append(", encoding=");
            conf.append(WebRtcAudioUtils.audioEncodingToString(format2.getEncoding()));
            conf.append(", sample rate=");
            conf.append(format2.getSampleRate());
            conf.append("\n");
            AudioDeviceInfo device = config.getAudioDevice();
            if (device != null) {
                assertTrue(device.isSource());
                conf.append("  AudioDevice: ");
                conf.append("type=");
                conf.append(WebRtcAudioUtils.deviceTypeToString(device.getType()));
                conf.append(", id=");
                conf.append(device.getId());
            }
            Logging.d("WebRtcAudioRecordExternal", conf.toString());
        }
        return true;
    }

    private static boolean verifyAudioConfig(int source, int session, AudioFormat format, AudioDeviceInfo device, List<AudioRecordingConfiguration> configs) {
        assertTrue(!configs.isEmpty());
        for (AudioRecordingConfiguration config : configs) {
            AudioDeviceInfo configDevice = config.getAudioDevice();
            if (configDevice != null && config.getClientAudioSource() == source && config.getClientAudioSessionId() == session && config.getClientFormat().getEncoding() == format.getEncoding() && config.getClientFormat().getSampleRate() == format.getSampleRate() && config.getClientFormat().getChannelMask() == format.getChannelMask() && config.getClientFormat().getChannelIndexMask() == format.getChannelIndexMask() && config.getFormat().getEncoding() != 0 && config.getFormat().getSampleRate() > 0) {
                if (!(config.getFormat().getChannelMask() == 0 && config.getFormat().getChannelIndexMask() == 0) && checkDeviceMatch(configDevice, device)) {
                    Logging.d("WebRtcAudioRecordExternal", "verifyAudioConfig: PASS");
                    return true;
                }
            }
        }
        Logging.e("WebRtcAudioRecordExternal", "verifyAudioConfig: FAILED");
        return false;
    }

    private static boolean checkDeviceMatch(AudioDeviceInfo devA, AudioDeviceInfo devB) {
        return devA.getId() == devB.getId() && devA.getType() == devB.getType();
    }

    private static String audioStateToString(int state) {
        switch (state) {
            case 0:
                return "START";
            case 1:
                return "STOP";
            default:
                return "INVALID";
        }
    }

    static ScheduledExecutorService newDefaultScheduler() {
        final AtomicInteger nextThreadId = new AtomicInteger(0);
        return Executors.newScheduledThreadPool(0, new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread thread = Executors.defaultThreadFactory().newThread(r);
                thread.setName(String.format("WebRtcAudioRecordScheduler-%s-%s", new Object[]{Integer.valueOf(WebRtcAudioRecord.nextSchedulerId.getAndIncrement()), Integer.valueOf(nextThreadId.getAndIncrement())}));
                return thread;
            }
        });
    }
}
