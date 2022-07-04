package org.webrtc.voiceengine;

import android.media.AudioFormat;
import android.media.AudioPlaybackCaptureConfiguration;
import android.media.AudioRecord;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.os.Process;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.voip.VideoCapturerDevice;
import org.webrtc.Logging;
import org.webrtc.ThreadUtils;

public class WebRtcAudioRecord {
    private static final long AUDIO_RECORD_THREAD_JOIN_TIMEOUT_MS = 2000;
    private static final int BITS_PER_SAMPLE = 16;
    private static final int BUFFERS_PER_SECOND = 100;
    private static final int BUFFER_SIZE_FACTOR = 2;
    private static final int CALLBACK_BUFFER_SIZE_MS = 10;
    private static final int DEFAULT_AUDIO_SOURCE;
    public static WebRtcAudioRecord Instance = null;
    private static final String TAG = "WebRtcAudioRecord";
    /* access modifiers changed from: private */
    public static WebRtcAudioRecordSamplesReadyCallback audioSamplesReadyCallback;
    private static int audioSource;
    private static WebRtcAudioRecordErrorCallback errorCallback;
    /* access modifiers changed from: private */
    public static volatile boolean microphoneMute;
    /* access modifiers changed from: private */
    public AudioRecord audioRecord;
    private AudioRecordThread audioThread;
    /* access modifiers changed from: private */
    public ByteBuffer byteBuffer;
    private int captureType;
    /* access modifiers changed from: private */
    public AudioRecord deviceAudioRecord;
    /* access modifiers changed from: private */
    public ByteBuffer deviceByteBuffer;
    private WebRtcAudioEffects effects;
    /* access modifiers changed from: private */
    public byte[] emptyBytes;
    /* access modifiers changed from: private */
    public final long nativeAudioRecord;
    private int requestedChannels = 1;
    private int requestedSampleRate = 48000;

    public enum AudioRecordStartErrorCode {
        AUDIO_RECORD_START_EXCEPTION,
        AUDIO_RECORD_START_STATE_MISMATCH
    }

    public interface WebRtcAudioRecordErrorCallback {
        void onWebRtcAudioRecordError(String str);

        void onWebRtcAudioRecordInitError(String str);

        void onWebRtcAudioRecordStartError(AudioRecordStartErrorCode audioRecordStartErrorCode, String str);
    }

    public interface WebRtcAudioRecordSamplesReadyCallback {
        void onWebRtcAudioRecordSamplesReady(AudioSamples audioSamples);
    }

    private native void nativeCacheDirectBufferAddress(ByteBuffer byteBuffer2, long j);

    /* access modifiers changed from: private */
    public native void nativeDataIsRecorded(int i, long j);

    static {
        int defaultAudioSource = getDefaultAudioSource();
        DEFAULT_AUDIO_SOURCE = defaultAudioSource;
        audioSource = defaultAudioSource;
    }

    public static void setErrorCallback(WebRtcAudioRecordErrorCallback errorCallback2) {
        Logging.d("WebRtcAudioRecord", "Set error callback");
        errorCallback = errorCallback2;
    }

    public static class AudioSamples {
        private final int audioFormat;
        private final int channelCount;
        private final byte[] data;
        private final int sampleRate;

        private AudioSamples(AudioRecord audioRecord, byte[] data2) {
            this.audioFormat = audioRecord.getAudioFormat();
            this.channelCount = audioRecord.getChannelCount();
            this.sampleRate = audioRecord.getSampleRate();
            this.data = data2;
        }

        public int getAudioFormat() {
            return this.audioFormat;
        }

        public int getChannelCount() {
            return this.channelCount;
        }

        public int getSampleRate() {
            return this.sampleRate;
        }

        public byte[] getData() {
            return this.data;
        }
    }

    public static void setOnAudioSamplesReady(WebRtcAudioRecordSamplesReadyCallback callback) {
        audioSamplesReadyCallback = callback;
    }

    private class AudioRecordThread extends Thread {
        private volatile boolean keepAlive = true;

        public AudioRecordThread(String name) {
            super(name);
        }

        public void run() {
            int deviceBytesRead;
            Process.setThreadPriority(-19);
            Logging.d("WebRtcAudioRecord", "AudioRecordThread" + WebRtcAudioUtils.getThreadInfo());
            long nanoTime = System.nanoTime();
            while (this.keepAlive) {
                int bytesRead = WebRtcAudioRecord.this.audioRecord.read(WebRtcAudioRecord.this.byteBuffer, WebRtcAudioRecord.this.byteBuffer.capacity());
                if (WebRtcAudioRecord.this.deviceAudioRecord != null) {
                    deviceBytesRead = WebRtcAudioRecord.this.deviceAudioRecord.read(WebRtcAudioRecord.this.deviceByteBuffer, WebRtcAudioRecord.this.deviceByteBuffer.capacity());
                } else {
                    deviceBytesRead = 0;
                }
                if (bytesRead == WebRtcAudioRecord.this.byteBuffer.capacity()) {
                    if (WebRtcAudioRecord.microphoneMute) {
                        WebRtcAudioRecord.this.byteBuffer.clear();
                        WebRtcAudioRecord.this.byteBuffer.put(WebRtcAudioRecord.this.emptyBytes);
                    }
                    if (bytesRead == deviceBytesRead) {
                        WebRtcAudioRecord.this.deviceByteBuffer.position(0);
                        WebRtcAudioRecord.this.byteBuffer.position(0);
                        for (int a = 0; a < bytesRead / 2; a++) {
                            int mixed = WebRtcAudioRecord.this.byteBuffer.getShort(a * 2) + (WebRtcAudioRecord.this.deviceByteBuffer.getShort(a * 2) / 10);
                            if (mixed > 32767) {
                                mixed = 32767;
                            }
                            if (mixed < -32768) {
                                mixed = -32768;
                            }
                            WebRtcAudioRecord.this.byteBuffer.putShort(a * 2, (short) mixed);
                        }
                    }
                    if (this.keepAlive != 0) {
                        try {
                            WebRtcAudioRecord webRtcAudioRecord = WebRtcAudioRecord.this;
                            webRtcAudioRecord.nativeDataIsRecorded(bytesRead, webRtcAudioRecord.nativeAudioRecord);
                        } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
                            FileLog.e((Throwable) unsatisfiedLinkError);
                            this.keepAlive = false;
                        }
                    }
                    if (WebRtcAudioRecord.audioSamplesReadyCallback != null) {
                        WebRtcAudioRecord.audioSamplesReadyCallback.onWebRtcAudioRecordSamplesReady(new AudioSamples(WebRtcAudioRecord.this.audioRecord, Arrays.copyOf(WebRtcAudioRecord.this.byteBuffer.array(), WebRtcAudioRecord.this.byteBuffer.capacity())));
                    }
                } else {
                    String errorMessage = "AudioRecord.read failed: " + bytesRead;
                    Logging.e("WebRtcAudioRecord", errorMessage);
                    if (bytesRead == -3) {
                        this.keepAlive = false;
                        WebRtcAudioRecord.this.reportWebRtcAudioRecordError(errorMessage);
                    }
                }
            }
            try {
                if (WebRtcAudioRecord.this.audioRecord != null) {
                    WebRtcAudioRecord.this.audioRecord.stop();
                }
            } catch (IllegalStateException e) {
                Logging.e("WebRtcAudioRecord", "AudioRecord.stop failed: " + e.getMessage());
            }
        }

        public void stopThread() {
            Logging.d("WebRtcAudioRecord", "stopThread");
            this.keepAlive = false;
        }
    }

    WebRtcAudioRecord(long nativeAudioRecord2, int type) {
        Logging.d("WebRtcAudioRecord", "ctor" + WebRtcAudioUtils.getThreadInfo());
        this.nativeAudioRecord = nativeAudioRecord2;
        this.effects = WebRtcAudioEffects.create();
        this.captureType = type;
        if (type == 2 && Instance == null) {
            Instance = this;
        }
    }

    private void onDestroy() {
        stopDeviceAudioRecord();
        if (Instance == this) {
            Instance = null;
        }
    }

    private boolean enableBuiltInAEC(boolean enable) {
        Logging.d("WebRtcAudioRecord", "enableBuiltInAEC(" + enable + ')');
        WebRtcAudioEffects webRtcAudioEffects = this.effects;
        if (webRtcAudioEffects != null) {
            return webRtcAudioEffects.setAEC(enable);
        }
        Logging.e("WebRtcAudioRecord", "Built-in AEC is not supported on this platform");
        return false;
    }

    private boolean enableBuiltInNS(boolean enable) {
        Logging.d("WebRtcAudioRecord", "enableBuiltInNS(" + enable + ')');
        WebRtcAudioEffects webRtcAudioEffects = this.effects;
        if (webRtcAudioEffects != null) {
            return webRtcAudioEffects.setNS(enable);
        }
        Logging.e("WebRtcAudioRecord", "Built-in NS is not supported on this platform");
        return false;
    }

    private int initRecording(int sampleRate, int channels) {
        boolean z;
        WebRtcAudioEffects webRtcAudioEffects;
        String str;
        int i = sampleRate;
        int i2 = channels;
        if (this.captureType == 1 && Build.VERSION.SDK_INT < 29) {
            return -1;
        }
        this.requestedSampleRate = i;
        this.requestedChannels = i2;
        Logging.d("WebRtcAudioRecord", "initRecording(sampleRate=" + i + ", channels=" + i2 + ")");
        if (this.audioRecord != null) {
            reportWebRtcAudioRecordInitError("InitRecording called twice without StopRecording.");
            return -1;
        }
        int framesPerBuffer = i / 100;
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(i2 * 2 * framesPerBuffer);
        this.byteBuffer = allocateDirect;
        allocateDirect.order(ByteOrder.nativeOrder());
        Logging.d("WebRtcAudioRecord", "byteBuffer.capacity: " + this.byteBuffer.capacity());
        this.emptyBytes = new byte[this.byteBuffer.capacity()];
        nativeCacheDirectBufferAddress(this.byteBuffer, this.nativeAudioRecord);
        int channelConfig = channelCountToConfiguration(i2);
        int minBufferSize = AudioRecord.getMinBufferSize(i, channelConfig, 2);
        if (minBufferSize == -1 || minBufferSize == -2) {
            reportWebRtcAudioRecordInitError("AudioRecord.getMinBufferSize failed: " + minBufferSize);
            return -1;
        }
        Logging.d("WebRtcAudioRecord", "AudioRecord.getMinBufferSize: " + minBufferSize);
        int bufferSizeInBytes = Math.max(minBufferSize * 2, this.byteBuffer.capacity());
        Logging.d("WebRtcAudioRecord", "bufferSizeInBytes: " + bufferSizeInBytes);
        if (this.captureType != 1) {
            try {
                z = false;
                str = "AudioRecord ctor error: ";
                int i3 = bufferSizeInBytes;
                try {
                    AudioRecord audioRecord2 = new AudioRecord(audioSource, sampleRate, channelConfig, 2, bufferSizeInBytes);
                    this.audioRecord = audioRecord2;
                } catch (IllegalArgumentException e) {
                    e = e;
                }
            } catch (IllegalArgumentException e2) {
                e = e2;
                str = "AudioRecord ctor error: ";
                int i4 = bufferSizeInBytes;
                z = false;
                reportWebRtcAudioRecordInitError(str + e.getMessage());
                releaseAudioResources(z);
                return -1;
            }
        } else if (Build.VERSION.SDK_INT >= 29) {
            try {
                MediaProjection projection = VideoCapturerDevice.getMediaProjection();
                if (projection == null) {
                    return -1;
                }
                AudioPlaybackCaptureConfiguration.Builder builder = new AudioPlaybackCaptureConfiguration.Builder(projection);
                builder.addMatchingUsage(1);
                builder.addMatchingUsage(14);
                builder.addMatchingUsage(0);
                AudioRecord.Builder audioRecordBuilder = new AudioRecord.Builder();
                audioRecordBuilder.setAudioPlaybackCaptureConfig(builder.build());
                audioRecordBuilder.setAudioFormat(new AudioFormat.Builder().setChannelMask(channelConfig).setSampleRate(i).setEncoding(2).build());
                audioRecordBuilder.setBufferSizeInBytes(bufferSizeInBytes);
                this.audioRecord = audioRecordBuilder.build();
                int i5 = bufferSizeInBytes;
                z = false;
            } catch (Throwable e3) {
                reportWebRtcAudioRecordInitError("AudioRecord ctor error: " + e3.getMessage());
                releaseAudioResources(false);
                return -1;
            }
        } else {
            int i6 = bufferSizeInBytes;
            z = false;
        }
        AudioRecord audioRecord3 = this.audioRecord;
        if (audioRecord3 == null || audioRecord3.getState() != 1) {
            reportWebRtcAudioRecordInitError("Failed to create a new AudioRecord instance");
            releaseAudioResources(z);
            return -1;
        }
        if (this.captureType == 0 && (webRtcAudioEffects = this.effects) != null) {
            webRtcAudioEffects.enable(this.audioRecord.getAudioSessionId());
        }
        logMainParameters();
        logMainParametersExtended();
        return framesPerBuffer;
    }

    public void initDeviceAudioRecord(MediaProjection mediaProjection) {
        if (Build.VERSION.SDK_INT >= 29) {
            ByteBuffer allocateDirect = ByteBuffer.allocateDirect(this.requestedChannels * 2 * (this.requestedSampleRate / 100));
            this.deviceByteBuffer = allocateDirect;
            allocateDirect.order(ByteOrder.nativeOrder());
            int channelConfig = channelCountToConfiguration(this.requestedChannels);
            int minBufferSize = AudioRecord.getMinBufferSize(this.requestedSampleRate, channelConfig, 2);
            if (minBufferSize == -1 || minBufferSize == -2) {
                reportWebRtcAudioRecordInitError("AudioRecord.getMinBufferSize failed: " + minBufferSize);
                return;
            }
            int bufferSizeInBytes = Math.max(minBufferSize * 2, this.deviceByteBuffer.capacity());
            try {
                AudioPlaybackCaptureConfiguration.Builder builder = new AudioPlaybackCaptureConfiguration.Builder(mediaProjection);
                builder.addMatchingUsage(1);
                builder.addMatchingUsage(14);
                AudioRecord.Builder audioRecordBuilder = new AudioRecord.Builder();
                audioRecordBuilder.setAudioPlaybackCaptureConfig(builder.build());
                audioRecordBuilder.setAudioFormat(new AudioFormat.Builder().setChannelMask(channelConfig).setSampleRate(this.requestedSampleRate).setEncoding(2).build());
                audioRecordBuilder.setBufferSizeInBytes(bufferSizeInBytes);
                AudioRecord build = audioRecordBuilder.build();
                this.deviceAudioRecord = build;
                if (build == null || build.getState() != 1) {
                    reportWebRtcAudioRecordInitError("Failed to create a new AudioRecord instance");
                    releaseAudioResources(true);
                    return;
                }
                try {
                    this.deviceAudioRecord.startRecording();
                    if (this.deviceAudioRecord.getRecordingState() != 3) {
                        AudioRecordStartErrorCode audioRecordStartErrorCode = AudioRecordStartErrorCode.AUDIO_RECORD_START_STATE_MISMATCH;
                        reportWebRtcAudioRecordStartError(audioRecordStartErrorCode, "AudioRecord.startRecording failed - incorrect state :" + this.deviceAudioRecord.getRecordingState());
                    }
                } catch (IllegalStateException e) {
                    AudioRecordStartErrorCode audioRecordStartErrorCode2 = AudioRecordStartErrorCode.AUDIO_RECORD_START_EXCEPTION;
                    reportWebRtcAudioRecordStartError(audioRecordStartErrorCode2, "AudioRecord.startRecording failed: " + e.getMessage());
                }
            } catch (Throwable e2) {
                reportWebRtcAudioRecordInitError("AudioRecord ctor error: " + e2.getMessage());
                releaseAudioResources(true);
            }
        }
    }

    public void stopDeviceAudioRecord() {
        AudioRecord audioRecord2 = this.deviceAudioRecord;
        if (audioRecord2 != null) {
            try {
                audioRecord2.stop();
            } catch (Throwable e) {
                FileLog.e(e);
            }
            releaseAudioResources(true);
        }
    }

    private boolean startRecording() {
        Logging.d("WebRtcAudioRecord", "startRecording");
        assertTrue(this.audioRecord != null);
        assertTrue(this.audioThread == null);
        try {
            this.audioRecord.startRecording();
            if (this.audioRecord.getRecordingState() != 3) {
                AudioRecordStartErrorCode audioRecordStartErrorCode = AudioRecordStartErrorCode.AUDIO_RECORD_START_STATE_MISMATCH;
                reportWebRtcAudioRecordStartError(audioRecordStartErrorCode, "AudioRecord.startRecording failed - incorrect state :" + this.audioRecord.getRecordingState());
                return false;
            }
            AudioRecordThread audioRecordThread = new AudioRecordThread("AudioRecordJavaThread");
            this.audioThread = audioRecordThread;
            audioRecordThread.start();
            return true;
        } catch (IllegalStateException e) {
            AudioRecordStartErrorCode audioRecordStartErrorCode2 = AudioRecordStartErrorCode.AUDIO_RECORD_START_EXCEPTION;
            reportWebRtcAudioRecordStartError(audioRecordStartErrorCode2, "AudioRecord.startRecording failed: " + e.getMessage());
            return false;
        }
    }

    private boolean stopRecording() {
        Logging.d("WebRtcAudioRecord", "stopRecording");
        assertTrue(this.audioThread != null);
        this.audioThread.stopThread();
        if (!ThreadUtils.joinUninterruptibly(this.audioThread, 2000)) {
            Logging.e("WebRtcAudioRecord", "Join of AudioRecordJavaThread timed out");
            WebRtcAudioUtils.logAudioState("WebRtcAudioRecord");
        }
        this.audioThread = null;
        WebRtcAudioEffects webRtcAudioEffects = this.effects;
        if (webRtcAudioEffects != null) {
            webRtcAudioEffects.release();
        }
        try {
            this.audioRecord.stop();
        } catch (Throwable e) {
            FileLog.e(e);
        }
        releaseAudioResources(false);
        return true;
    }

    private void logMainParameters() {
        Logging.d("WebRtcAudioRecord", "AudioRecord: session ID: " + this.audioRecord.getAudioSessionId() + ", channels: " + this.audioRecord.getChannelCount() + ", sample rate: " + this.audioRecord.getSampleRate());
    }

    private void logMainParametersExtended() {
        if (Build.VERSION.SDK_INT >= 23) {
            Logging.d("WebRtcAudioRecord", "AudioRecord: buffer size in frames: " + this.audioRecord.getBufferSizeInFrames());
        }
    }

    private static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected condition to be true");
        }
    }

    private int channelCountToConfiguration(int channels) {
        return channels == 1 ? 16 : 12;
    }

    public static synchronized void setAudioSource(int source) {
        synchronized (WebRtcAudioRecord.class) {
            Logging.w("WebRtcAudioRecord", "Audio source is changed from: " + audioSource + " to " + source);
            audioSource = source;
        }
    }

    private static int getDefaultAudioSource() {
        return 7;
    }

    public static void setMicrophoneMute(boolean mute) {
        Logging.w("WebRtcAudioRecord", "setMicrophoneMute(" + mute + ")");
        microphoneMute = mute;
    }

    private void releaseAudioResources(boolean device) {
        Logging.d("WebRtcAudioRecord", "releaseAudioResources " + device);
        if (device) {
            AudioRecord audioRecord2 = this.deviceAudioRecord;
            if (audioRecord2 != null) {
                audioRecord2.release();
                this.deviceAudioRecord = null;
                return;
            }
            return;
        }
        AudioRecord audioRecord3 = this.audioRecord;
        if (audioRecord3 != null) {
            audioRecord3.release();
            this.audioRecord = null;
        }
    }

    private void reportWebRtcAudioRecordInitError(String errorMessage) {
        Logging.e("WebRtcAudioRecord", "Init recording error: " + errorMessage);
        WebRtcAudioUtils.logAudioState("WebRtcAudioRecord");
        WebRtcAudioRecordErrorCallback webRtcAudioRecordErrorCallback = errorCallback;
        if (webRtcAudioRecordErrorCallback != null) {
            webRtcAudioRecordErrorCallback.onWebRtcAudioRecordInitError(errorMessage);
        }
    }

    private void reportWebRtcAudioRecordStartError(AudioRecordStartErrorCode errorCode, String errorMessage) {
        Logging.e("WebRtcAudioRecord", "Start recording error: " + errorCode + ". " + errorMessage);
        WebRtcAudioUtils.logAudioState("WebRtcAudioRecord");
        WebRtcAudioRecordErrorCallback webRtcAudioRecordErrorCallback = errorCallback;
        if (webRtcAudioRecordErrorCallback != null) {
            webRtcAudioRecordErrorCallback.onWebRtcAudioRecordStartError(errorCode, errorMessage);
        }
    }

    /* access modifiers changed from: private */
    public void reportWebRtcAudioRecordError(String errorMessage) {
        Logging.e("WebRtcAudioRecord", "Run-time recording error: " + errorMessage);
        WebRtcAudioUtils.logAudioState("WebRtcAudioRecord");
        WebRtcAudioRecordErrorCallback webRtcAudioRecordErrorCallback = errorCallback;
        if (webRtcAudioRecordErrorCallback != null) {
            webRtcAudioRecordErrorCallback.onWebRtcAudioRecordError(errorMessage);
        }
    }
}
