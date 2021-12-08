package org.webrtc.audio;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Build;
import org.webrtc.Logging;

class WebRtcAudioManager {
    private static final int BITS_PER_SAMPLE = 16;
    private static final int DEFAULT_FRAME_PER_BUFFER = 256;
    private static final int DEFAULT_SAMPLE_RATE_HZ = 16000;
    private static final String TAG = "WebRtcAudioManagerExternal";

    WebRtcAudioManager() {
    }

    static AudioManager getAudioManager(Context context) {
        return (AudioManager) context.getSystemService("audio");
    }

    static int getOutputBufferSize(Context context, AudioManager audioManager, int sampleRate, int numberOfOutputChannels) {
        if (isLowLatencyOutputSupported(context)) {
            return getLowLatencyFramesPerBuffer(audioManager);
        }
        return getMinOutputFrameSize(sampleRate, numberOfOutputChannels);
    }

    static int getInputBufferSize(Context context, AudioManager audioManager, int sampleRate, int numberOfInputChannels) {
        if (isLowLatencyInputSupported(context)) {
            return getLowLatencyFramesPerBuffer(audioManager);
        }
        return getMinInputFrameSize(sampleRate, numberOfInputChannels);
    }

    private static boolean isLowLatencyOutputSupported(Context context) {
        return context.getPackageManager().hasSystemFeature("android.hardware.audio.low_latency");
    }

    private static boolean isLowLatencyInputSupported(Context context) {
        return Build.VERSION.SDK_INT >= 21 && isLowLatencyOutputSupported(context);
    }

    static int getSampleRate(AudioManager audioManager) {
        if (WebRtcAudioUtils.runningOnEmulator()) {
            Logging.d("WebRtcAudioManagerExternal", "Running emulator, overriding sample rate to 8 kHz.");
            return 8000;
        }
        int sampleRateHz = getSampleRateForApiLevel(audioManager);
        Logging.d("WebRtcAudioManagerExternal", "Sample rate is set to " + sampleRateHz + " Hz");
        return sampleRateHz;
    }

    private static int getSampleRateForApiLevel(AudioManager audioManager) {
        String sampleRateString;
        if (Build.VERSION.SDK_INT >= 17 && (sampleRateString = audioManager.getProperty("android.media.property.OUTPUT_SAMPLE_RATE")) != null) {
            return Integer.parseInt(sampleRateString);
        }
        return 16000;
    }

    private static int getLowLatencyFramesPerBuffer(AudioManager audioManager) {
        String framesPerBuffer;
        if (Build.VERSION.SDK_INT >= 17 && (framesPerBuffer = audioManager.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER")) != null) {
            return Integer.parseInt(framesPerBuffer);
        }
        return 256;
    }

    private static int getMinOutputFrameSize(int sampleRateInHz, int numChannels) {
        return AudioTrack.getMinBufferSize(sampleRateInHz, numChannels == 1 ? 4 : 12, 2) / (numChannels * 2);
    }

    private static int getMinInputFrameSize(int sampleRateInHz, int numChannels) {
        return AudioRecord.getMinBufferSize(sampleRateInHz, numChannels == 1 ? 16 : 12, 2) / (numChannels * 2);
    }
}
