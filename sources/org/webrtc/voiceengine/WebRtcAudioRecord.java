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
    private WebRtcAudioEffects effects = WebRtcAudioEffects.create();
    /* access modifiers changed from: private */
    public byte[] emptyBytes;
    private boolean isScreenCapture;
    /* access modifiers changed from: private */
    public final long nativeAudioRecord;

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

    private int channelCountToConfiguration(int i) {
        return i == 1 ? 16 : 12;
    }

    private static int getDefaultAudioSource() {
        return 7;
    }

    private native void nativeCacheDirectBufferAddress(ByteBuffer byteBuffer2, long j);

    /* access modifiers changed from: private */
    public native void nativeDataIsRecorded(int i, long j);

    static {
        int defaultAudioSource = getDefaultAudioSource();
        DEFAULT_AUDIO_SOURCE = defaultAudioSource;
        audioSource = defaultAudioSource;
    }

    public static void setErrorCallback(WebRtcAudioRecordErrorCallback webRtcAudioRecordErrorCallback) {
        Logging.d("WebRtcAudioRecord", "Set error callback");
        errorCallback = webRtcAudioRecordErrorCallback;
    }

    public static class AudioSamples {
        private final int audioFormat;
        private final int channelCount;
        private final byte[] data;
        private final int sampleRate;

        private AudioSamples(AudioRecord audioRecord, byte[] bArr) {
            this.audioFormat = audioRecord.getAudioFormat();
            this.channelCount = audioRecord.getChannelCount();
            this.sampleRate = audioRecord.getSampleRate();
            this.data = bArr;
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

    public static void setOnAudioSamplesReady(WebRtcAudioRecordSamplesReadyCallback webRtcAudioRecordSamplesReadyCallback) {
        audioSamplesReadyCallback = webRtcAudioRecordSamplesReadyCallback;
    }

    private class AudioRecordThread extends Thread {
        private volatile boolean keepAlive = true;

        public AudioRecordThread(String str) {
            super(str);
        }

        public void run() {
            Process.setThreadPriority(-19);
            Logging.d("WebRtcAudioRecord", "AudioRecordThread" + WebRtcAudioUtils.getThreadInfo());
            System.nanoTime();
            while (this.keepAlive) {
                int read = WebRtcAudioRecord.this.audioRecord.read(WebRtcAudioRecord.this.byteBuffer, WebRtcAudioRecord.this.byteBuffer.capacity());
                if (read == WebRtcAudioRecord.this.byteBuffer.capacity()) {
                    if (WebRtcAudioRecord.microphoneMute) {
                        WebRtcAudioRecord.this.byteBuffer.clear();
                        WebRtcAudioRecord.this.byteBuffer.put(WebRtcAudioRecord.this.emptyBytes);
                    }
                    if (this.keepAlive) {
                        WebRtcAudioRecord webRtcAudioRecord = WebRtcAudioRecord.this;
                        webRtcAudioRecord.nativeDataIsRecorded(read, webRtcAudioRecord.nativeAudioRecord);
                    }
                    if (WebRtcAudioRecord.audioSamplesReadyCallback != null) {
                        WebRtcAudioRecord.audioSamplesReadyCallback.onWebRtcAudioRecordSamplesReady(new AudioSamples(WebRtcAudioRecord.this.audioRecord, Arrays.copyOf(WebRtcAudioRecord.this.byteBuffer.array(), WebRtcAudioRecord.this.byteBuffer.capacity())));
                    }
                } else {
                    String str = "AudioRecord.read failed: " + read;
                    Logging.e("WebRtcAudioRecord", str);
                    if (read == -3) {
                        this.keepAlive = false;
                        WebRtcAudioRecord.this.reportWebRtcAudioRecordError(str);
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

    WebRtcAudioRecord(long j, boolean z) {
        Logging.d("WebRtcAudioRecord", "ctor" + WebRtcAudioUtils.getThreadInfo());
        this.nativeAudioRecord = j;
        this.isScreenCapture = z;
    }

    private boolean enableBuiltInAEC(boolean z) {
        Logging.d("WebRtcAudioRecord", "enableBuiltInAEC(" + z + ')');
        WebRtcAudioEffects webRtcAudioEffects = this.effects;
        if (webRtcAudioEffects != null) {
            return webRtcAudioEffects.setAEC(z);
        }
        Logging.e("WebRtcAudioRecord", "Built-in AEC is not supported on this platform");
        return false;
    }

    private boolean enableBuiltInNS(boolean z) {
        Logging.d("WebRtcAudioRecord", "enableBuiltInNS(" + z + ')');
        WebRtcAudioEffects webRtcAudioEffects = this.effects;
        if (webRtcAudioEffects != null) {
            return webRtcAudioEffects.setNS(z);
        }
        Logging.e("WebRtcAudioRecord", "Built-in NS is not supported on this platform");
        return false;
    }

    private int initRecording(int i, int i2) {
        WebRtcAudioEffects webRtcAudioEffects;
        if (this.isScreenCapture && Build.VERSION.SDK_INT < 29) {
            return -1;
        }
        Logging.d("WebRtcAudioRecord", "initRecording(sampleRate=" + i + ", channels=" + i2 + ")");
        if (this.audioRecord != null) {
            reportWebRtcAudioRecordInitError("InitRecording called twice without StopRecording.");
            return -1;
        }
        int i3 = i / 100;
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(i2 * 2 * i3);
        this.byteBuffer = allocateDirect;
        allocateDirect.order(ByteOrder.nativeOrder());
        Logging.d("WebRtcAudioRecord", "byteBuffer.capacity: " + this.byteBuffer.capacity());
        this.emptyBytes = new byte[this.byteBuffer.capacity()];
        nativeCacheDirectBufferAddress(this.byteBuffer, this.nativeAudioRecord);
        int channelCountToConfiguration = channelCountToConfiguration(i2);
        int minBufferSize = AudioRecord.getMinBufferSize(i, channelCountToConfiguration, 2);
        if (minBufferSize == -1 || minBufferSize == -2) {
            reportWebRtcAudioRecordInitError("AudioRecord.getMinBufferSize failed: " + minBufferSize);
            return -1;
        }
        Logging.d("WebRtcAudioRecord", "AudioRecord.getMinBufferSize: " + minBufferSize);
        int max = Math.max(minBufferSize * 2, this.byteBuffer.capacity());
        Logging.d("WebRtcAudioRecord", "bufferSizeInBytes: " + max);
        if (!this.isScreenCapture) {
            try {
                this.audioRecord = new AudioRecord(audioSource, i, channelCountToConfiguration, 2, max);
            } catch (IllegalArgumentException e) {
                reportWebRtcAudioRecordInitError("AudioRecord ctor error: " + e.getMessage());
                releaseAudioResources();
                return -1;
            }
        } else if (Build.VERSION.SDK_INT >= 29) {
            try {
                MediaProjection mediaProjection = VideoCapturerDevice.getMediaProjection();
                if (mediaProjection == null) {
                    return -1;
                }
                AudioPlaybackCaptureConfiguration.Builder builder = new AudioPlaybackCaptureConfiguration.Builder(mediaProjection);
                builder.addMatchingUsage(1);
                builder.addMatchingUsage(14);
                AudioRecord.Builder builder2 = new AudioRecord.Builder();
                builder2.setAudioPlaybackCaptureConfig(builder.build());
                builder2.setAudioFormat(new AudioFormat.Builder().setChannelMask(channelCountToConfiguration).setSampleRate(i).setEncoding(2).build());
                builder2.setBufferSizeInBytes(max);
                this.audioRecord = builder2.build();
            } catch (Throwable th) {
                reportWebRtcAudioRecordInitError("AudioRecord ctor error: " + th.getMessage());
                releaseAudioResources();
                return -1;
            }
        }
        AudioRecord audioRecord2 = this.audioRecord;
        if (audioRecord2 == null || audioRecord2.getState() != 1) {
            reportWebRtcAudioRecordInitError("Failed to create a new AudioRecord instance");
            releaseAudioResources();
            return -1;
        }
        if (!this.isScreenCapture && (webRtcAudioEffects = this.effects) != null) {
            webRtcAudioEffects.enable(this.audioRecord.getAudioSessionId());
        }
        logMainParameters();
        logMainParametersExtended();
        return i3;
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
        } catch (Throwable th) {
            FileLog.e(th);
        }
        releaseAudioResources();
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

    private static void assertTrue(boolean z) {
        if (!z) {
            throw new AssertionError("Expected condition to be true");
        }
    }

    public static synchronized void setAudioSource(int i) {
        synchronized (WebRtcAudioRecord.class) {
            Logging.w("WebRtcAudioRecord", "Audio source is changed from: " + audioSource + " to " + i);
            audioSource = i;
        }
    }

    public static void setMicrophoneMute(boolean z) {
        Logging.w("WebRtcAudioRecord", "setMicrophoneMute(" + z + ")");
        microphoneMute = z;
    }

    private void releaseAudioResources() {
        Logging.d("WebRtcAudioRecord", "releaseAudioResources");
        AudioRecord audioRecord2 = this.audioRecord;
        if (audioRecord2 != null) {
            audioRecord2.release();
            this.audioRecord = null;
        }
    }

    private void reportWebRtcAudioRecordInitError(String str) {
        Logging.e("WebRtcAudioRecord", "Init recording error: " + str);
        WebRtcAudioUtils.logAudioState("WebRtcAudioRecord");
        WebRtcAudioRecordErrorCallback webRtcAudioRecordErrorCallback = errorCallback;
        if (webRtcAudioRecordErrorCallback != null) {
            webRtcAudioRecordErrorCallback.onWebRtcAudioRecordInitError(str);
        }
    }

    private void reportWebRtcAudioRecordStartError(AudioRecordStartErrorCode audioRecordStartErrorCode, String str) {
        Logging.e("WebRtcAudioRecord", "Start recording error: " + audioRecordStartErrorCode + ". " + str);
        WebRtcAudioUtils.logAudioState("WebRtcAudioRecord");
        WebRtcAudioRecordErrorCallback webRtcAudioRecordErrorCallback = errorCallback;
        if (webRtcAudioRecordErrorCallback != null) {
            webRtcAudioRecordErrorCallback.onWebRtcAudioRecordStartError(audioRecordStartErrorCode, str);
        }
    }

    /* access modifiers changed from: private */
    public void reportWebRtcAudioRecordError(String str) {
        Logging.e("WebRtcAudioRecord", "Run-time recording error: " + str);
        WebRtcAudioUtils.logAudioState("WebRtcAudioRecord");
        WebRtcAudioRecordErrorCallback webRtcAudioRecordErrorCallback = errorCallback;
        if (webRtcAudioRecordErrorCallback != null) {
            webRtcAudioRecordErrorCallback.onWebRtcAudioRecordError(str);
        }
    }
}
