package org.webrtc.audio;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import java.util.concurrent.ScheduledExecutorService;
import org.webrtc.JniCommon;
import org.webrtc.Logging;

public class JavaAudioDeviceModule implements AudioDeviceModule {
    private static final String TAG = "JavaAudioDeviceModule";
    private final WebRtcAudioRecord audioInput;
    private final AudioManager audioManager;
    private final WebRtcAudioTrack audioOutput;
    private final Context context;
    private final int inputSampleRate;
    private long nativeAudioDeviceModule;
    private final Object nativeLock;
    private final int outputSampleRate;
    private final boolean useStereoInput;
    private final boolean useStereoOutput;

    public interface AudioRecordErrorCallback {
        void onWebRtcAudioRecordError(String str);

        void onWebRtcAudioRecordInitError(String str);

        void onWebRtcAudioRecordStartError(AudioRecordStartErrorCode audioRecordStartErrorCode, String str);
    }

    public enum AudioRecordStartErrorCode {
        AUDIO_RECORD_START_EXCEPTION,
        AUDIO_RECORD_START_STATE_MISMATCH
    }

    public interface AudioRecordStateCallback {
        void onWebRtcAudioRecordStart();

        void onWebRtcAudioRecordStop();
    }

    public interface AudioTrackErrorCallback {
        void onWebRtcAudioTrackError(String str);

        void onWebRtcAudioTrackInitError(String str);

        void onWebRtcAudioTrackStartError(AudioTrackStartErrorCode audioTrackStartErrorCode, String str);
    }

    public enum AudioTrackStartErrorCode {
        AUDIO_TRACK_START_EXCEPTION,
        AUDIO_TRACK_START_STATE_MISMATCH
    }

    public interface AudioTrackStateCallback {
        void onWebRtcAudioTrackStart();

        void onWebRtcAudioTrackStop();
    }

    public interface SamplesReadyCallback {
        void onWebRtcAudioRecordSamplesReady(AudioSamples audioSamples);
    }

    private static native long nativeCreateAudioDeviceModule(Context context2, AudioManager audioManager2, WebRtcAudioRecord webRtcAudioRecord, WebRtcAudioTrack webRtcAudioTrack, int i, int i2, boolean z, boolean z2);

    public static Builder builder(Context context2) {
        return new Builder(context2);
    }

    public static class Builder {
        private AudioAttributes audioAttributes;
        private int audioFormat;
        private final AudioManager audioManager;
        private AudioRecordErrorCallback audioRecordErrorCallback;
        private AudioRecordStateCallback audioRecordStateCallback;
        private int audioSource;
        private AudioTrackErrorCallback audioTrackErrorCallback;
        private AudioTrackStateCallback audioTrackStateCallback;
        private final Context context;
        private int inputSampleRate;
        private int outputSampleRate;
        private SamplesReadyCallback samplesReadyCallback;
        private ScheduledExecutorService scheduler;
        private boolean useHardwareAcousticEchoCanceler;
        private boolean useHardwareNoiseSuppressor;
        private boolean useLowLatency;
        private boolean useStereoInput;
        private boolean useStereoOutput;

        private Builder(Context context2) {
            this.audioSource = 7;
            this.audioFormat = 2;
            this.useHardwareAcousticEchoCanceler = JavaAudioDeviceModule.isBuiltInAcousticEchoCancelerSupported();
            this.useHardwareNoiseSuppressor = JavaAudioDeviceModule.isBuiltInNoiseSuppressorSupported();
            this.context = context2;
            AudioManager audioManager2 = (AudioManager) context2.getSystemService("audio");
            this.audioManager = audioManager2;
            this.inputSampleRate = WebRtcAudioManager.getSampleRate(audioManager2);
            this.outputSampleRate = WebRtcAudioManager.getSampleRate(audioManager2);
            this.useLowLatency = false;
        }

        public Builder setScheduler(ScheduledExecutorService scheduler2) {
            this.scheduler = scheduler2;
            return this;
        }

        public Builder setSampleRate(int sampleRate) {
            Logging.d("JavaAudioDeviceModule", "Input/Output sample rate overridden to: " + sampleRate);
            this.inputSampleRate = sampleRate;
            this.outputSampleRate = sampleRate;
            return this;
        }

        public Builder setInputSampleRate(int inputSampleRate2) {
            Logging.d("JavaAudioDeviceModule", "Input sample rate overridden to: " + inputSampleRate2);
            this.inputSampleRate = inputSampleRate2;
            return this;
        }

        public Builder setOutputSampleRate(int outputSampleRate2) {
            Logging.d("JavaAudioDeviceModule", "Output sample rate overridden to: " + outputSampleRate2);
            this.outputSampleRate = outputSampleRate2;
            return this;
        }

        public Builder setAudioSource(int audioSource2) {
            this.audioSource = audioSource2;
            return this;
        }

        public Builder setAudioFormat(int audioFormat2) {
            this.audioFormat = audioFormat2;
            return this;
        }

        public Builder setAudioTrackErrorCallback(AudioTrackErrorCallback audioTrackErrorCallback2) {
            this.audioTrackErrorCallback = audioTrackErrorCallback2;
            return this;
        }

        public Builder setAudioRecordErrorCallback(AudioRecordErrorCallback audioRecordErrorCallback2) {
            this.audioRecordErrorCallback = audioRecordErrorCallback2;
            return this;
        }

        public Builder setSamplesReadyCallback(SamplesReadyCallback samplesReadyCallback2) {
            this.samplesReadyCallback = samplesReadyCallback2;
            return this;
        }

        public Builder setAudioTrackStateCallback(AudioTrackStateCallback audioTrackStateCallback2) {
            this.audioTrackStateCallback = audioTrackStateCallback2;
            return this;
        }

        public Builder setAudioRecordStateCallback(AudioRecordStateCallback audioRecordStateCallback2) {
            this.audioRecordStateCallback = audioRecordStateCallback2;
            return this;
        }

        public Builder setUseHardwareNoiseSuppressor(boolean useHardwareNoiseSuppressor2) {
            if (useHardwareNoiseSuppressor2 && !JavaAudioDeviceModule.isBuiltInNoiseSuppressorSupported()) {
                Logging.e("JavaAudioDeviceModule", "HW NS not supported");
                useHardwareNoiseSuppressor2 = false;
            }
            this.useHardwareNoiseSuppressor = useHardwareNoiseSuppressor2;
            return this;
        }

        public Builder setUseHardwareAcousticEchoCanceler(boolean useHardwareAcousticEchoCanceler2) {
            if (useHardwareAcousticEchoCanceler2 && !JavaAudioDeviceModule.isBuiltInAcousticEchoCancelerSupported()) {
                Logging.e("JavaAudioDeviceModule", "HW AEC not supported");
                useHardwareAcousticEchoCanceler2 = false;
            }
            this.useHardwareAcousticEchoCanceler = useHardwareAcousticEchoCanceler2;
            return this;
        }

        public Builder setUseStereoInput(boolean useStereoInput2) {
            this.useStereoInput = useStereoInput2;
            return this;
        }

        public Builder setUseStereoOutput(boolean useStereoOutput2) {
            this.useStereoOutput = useStereoOutput2;
            return this;
        }

        public Builder setUseLowLatency(boolean useLowLatency2) {
            this.useLowLatency = useLowLatency2;
            return this;
        }

        public Builder setAudioAttributes(AudioAttributes audioAttributes2) {
            this.audioAttributes = audioAttributes2;
            return this;
        }

        public JavaAudioDeviceModule createAudioDeviceModule() {
            Logging.d("JavaAudioDeviceModule", "createAudioDeviceModule");
            if (this.useHardwareNoiseSuppressor) {
                Logging.d("JavaAudioDeviceModule", "HW NS will be used.");
            } else {
                if (JavaAudioDeviceModule.isBuiltInNoiseSuppressorSupported()) {
                    Logging.d("JavaAudioDeviceModule", "Overriding default behavior; now using WebRTC NS!");
                }
                Logging.d("JavaAudioDeviceModule", "HW NS will not be used.");
            }
            if (this.useHardwareAcousticEchoCanceler) {
                Logging.d("JavaAudioDeviceModule", "HW AEC will be used.");
            } else {
                if (JavaAudioDeviceModule.isBuiltInAcousticEchoCancelerSupported()) {
                    Logging.d("JavaAudioDeviceModule", "Overriding default behavior; now using WebRTC AEC!");
                }
                Logging.d("JavaAudioDeviceModule", "HW AEC will not be used.");
            }
            if (this.useLowLatency && Build.VERSION.SDK_INT >= 26) {
                Logging.d("JavaAudioDeviceModule", "Low latency mode will be used.");
            }
            ScheduledExecutorService executor = this.scheduler;
            if (executor == null) {
                executor = WebRtcAudioRecord.newDefaultScheduler();
            }
            return new JavaAudioDeviceModule(this.context, this.audioManager, new WebRtcAudioRecord(this.context, executor, this.audioManager, this.audioSource, this.audioFormat, this.audioRecordErrorCallback, this.audioRecordStateCallback, this.samplesReadyCallback, this.useHardwareAcousticEchoCanceler, this.useHardwareNoiseSuppressor), new WebRtcAudioTrack(this.context, this.audioManager, this.audioAttributes, this.audioTrackErrorCallback, this.audioTrackStateCallback, this.useLowLatency), this.inputSampleRate, this.outputSampleRate, this.useStereoInput, this.useStereoOutput);
        }
    }

    public static class AudioSamples {
        private final int audioFormat;
        private final int channelCount;
        private final byte[] data;
        private final int sampleRate;

        public AudioSamples(int audioFormat2, int channelCount2, int sampleRate2, byte[] data2) {
            this.audioFormat = audioFormat2;
            this.channelCount = channelCount2;
            this.sampleRate = sampleRate2;
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

    public static boolean isBuiltInAcousticEchoCancelerSupported() {
        return WebRtcAudioEffects.isAcousticEchoCancelerSupported();
    }

    public static boolean isBuiltInNoiseSuppressorSupported() {
        return WebRtcAudioEffects.isNoiseSuppressorSupported();
    }

    private JavaAudioDeviceModule(Context context2, AudioManager audioManager2, WebRtcAudioRecord audioInput2, WebRtcAudioTrack audioOutput2, int inputSampleRate2, int outputSampleRate2, boolean useStereoInput2, boolean useStereoOutput2) {
        this.nativeLock = new Object();
        this.context = context2;
        this.audioManager = audioManager2;
        this.audioInput = audioInput2;
        this.audioOutput = audioOutput2;
        this.inputSampleRate = inputSampleRate2;
        this.outputSampleRate = outputSampleRate2;
        this.useStereoInput = useStereoInput2;
        this.useStereoOutput = useStereoOutput2;
    }

    public long getNativeAudioDeviceModulePointer() {
        long j;
        synchronized (this.nativeLock) {
            if (this.nativeAudioDeviceModule == 0) {
                this.nativeAudioDeviceModule = nativeCreateAudioDeviceModule(this.context, this.audioManager, this.audioInput, this.audioOutput, this.inputSampleRate, this.outputSampleRate, this.useStereoInput, this.useStereoOutput);
            }
            j = this.nativeAudioDeviceModule;
        }
        return j;
    }

    public void release() {
        synchronized (this.nativeLock) {
            long j = this.nativeAudioDeviceModule;
            if (j != 0) {
                JniCommon.nativeReleaseRef(j);
                this.nativeAudioDeviceModule = 0;
            }
        }
    }

    public void setSpeakerMute(boolean mute) {
        Logging.d("JavaAudioDeviceModule", "setSpeakerMute: " + mute);
        this.audioOutput.setSpeakerMute(mute);
    }

    public void setMicrophoneMute(boolean mute) {
        Logging.d("JavaAudioDeviceModule", "setMicrophoneMute: " + mute);
        this.audioInput.setMicrophoneMute(mute);
    }

    public void setPreferredInputDevice(AudioDeviceInfo preferredInputDevice) {
        Logging.d("JavaAudioDeviceModule", "setPreferredInputDevice: " + preferredInputDevice);
        this.audioInput.setPreferredDevice(preferredInputDevice);
    }
}
