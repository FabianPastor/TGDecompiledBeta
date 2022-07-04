package org.webrtc.audio;

import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build;
import java.util.UUID;
import org.webrtc.Logging;

class WebRtcAudioEffects {
    private static final UUID AOSP_ACOUSTIC_ECHO_CANCELER = UUID.fromString("bb392ec0-8d4d-11e0-a896-0002a5d5CLASSNAMEb");
    private static final UUID AOSP_NOISE_SUPPRESSOR = UUID.fromString("CLASSNAMECLASSNAME-8e06-11e0-9cb6-0002a5d5CLASSNAMEb");
    private static final boolean DEBUG = false;
    private static final String TAG = "WebRtcAudioEffectsExternal";
    private static AudioEffect.Descriptor[] cachedEffects;
    private AcousticEchoCanceler aec;
    private NoiseSuppressor ns;
    private boolean shouldEnableAec;
    private boolean shouldEnableNs;

    public static boolean isAcousticEchoCancelerSupported() {
        if (Build.VERSION.SDK_INT < 18) {
            return false;
        }
        return isEffectTypeAvailable(AudioEffect.EFFECT_TYPE_AEC, AOSP_ACOUSTIC_ECHO_CANCELER);
    }

    public static boolean isNoiseSuppressorSupported() {
        if (Build.VERSION.SDK_INT < 18) {
            return false;
        }
        return isEffectTypeAvailable(AudioEffect.EFFECT_TYPE_NS, AOSP_NOISE_SUPPRESSOR);
    }

    public WebRtcAudioEffects() {
        Logging.d("WebRtcAudioEffectsExternal", "ctor" + WebRtcAudioUtils.getThreadInfo());
    }

    public boolean setAEC(boolean enable) {
        Logging.d("WebRtcAudioEffectsExternal", "setAEC(" + enable + ")");
        if (!isAcousticEchoCancelerSupported()) {
            Logging.w("WebRtcAudioEffectsExternal", "Platform AEC is not supported");
            this.shouldEnableAec = false;
            return false;
        } else if (this.aec == null || enable == this.shouldEnableAec) {
            this.shouldEnableAec = enable;
            return true;
        } else {
            Logging.e("WebRtcAudioEffectsExternal", "Platform AEC state can't be modified while recording");
            return false;
        }
    }

    public boolean setNS(boolean enable) {
        Logging.d("WebRtcAudioEffectsExternal", "setNS(" + enable + ")");
        if (!isNoiseSuppressorSupported()) {
            Logging.w("WebRtcAudioEffectsExternal", "Platform NS is not supported");
            this.shouldEnableNs = false;
            return false;
        } else if (this.ns == null || enable == this.shouldEnableNs) {
            this.shouldEnableNs = enable;
            return true;
        } else {
            Logging.e("WebRtcAudioEffectsExternal", "Platform NS state can't be modified while recording");
            return false;
        }
    }

    public void enable(int audioSession) {
        String str;
        String str2;
        String str3;
        Logging.d("WebRtcAudioEffectsExternal", "enable(audioSession=" + audioSession + ")");
        boolean enable = true;
        assertTrue(this.aec == null);
        assertTrue(this.ns == null);
        String str4 = "enabled";
        if (isAcousticEchoCancelerSupported()) {
            AcousticEchoCanceler create = AcousticEchoCanceler.create(audioSession);
            this.aec = create;
            if (create != null) {
                boolean enabled = create.getEnabled();
                boolean enable2 = this.shouldEnableAec && isAcousticEchoCancelerSupported();
                if (this.aec.setEnabled(enable2) != 0) {
                    Logging.e("WebRtcAudioEffectsExternal", "Failed to set the AcousticEchoCanceler state");
                }
                StringBuilder sb = new StringBuilder();
                sb.append("AcousticEchoCanceler: was ");
                if (enabled) {
                    str2 = str4;
                } else {
                    str2 = "disabled";
                }
                sb.append(str2);
                sb.append(", enable: ");
                sb.append(enable2);
                sb.append(", is now: ");
                if (this.aec.getEnabled()) {
                    str3 = str4;
                } else {
                    str3 = "disabled";
                }
                sb.append(str3);
                Logging.d("WebRtcAudioEffectsExternal", sb.toString());
            } else {
                Logging.e("WebRtcAudioEffectsExternal", "Failed to create the AcousticEchoCanceler instance");
            }
        }
        if (isNoiseSuppressorSupported()) {
            NoiseSuppressor create2 = NoiseSuppressor.create(audioSession);
            this.ns = create2;
            if (create2 != null) {
                boolean enabled2 = create2.getEnabled();
                if (!this.shouldEnableNs || !isNoiseSuppressorSupported()) {
                    enable = false;
                }
                if (this.ns.setEnabled(enable) != 0) {
                    Logging.e("WebRtcAudioEffectsExternal", "Failed to set the NoiseSuppressor state");
                }
                StringBuilder sb2 = new StringBuilder();
                sb2.append("NoiseSuppressor: was ");
                if (enabled2) {
                    str = str4;
                } else {
                    str = "disabled";
                }
                sb2.append(str);
                sb2.append(", enable: ");
                sb2.append(enable);
                sb2.append(", is now: ");
                if (!this.ns.getEnabled()) {
                    str4 = "disabled";
                }
                sb2.append(str4);
                Logging.d("WebRtcAudioEffectsExternal", sb2.toString());
                return;
            }
            Logging.e("WebRtcAudioEffectsExternal", "Failed to create the NoiseSuppressor instance");
        }
    }

    public void release() {
        Logging.d("WebRtcAudioEffectsExternal", "release");
        AcousticEchoCanceler acousticEchoCanceler = this.aec;
        if (acousticEchoCanceler != null) {
            acousticEchoCanceler.release();
            this.aec = null;
        }
        NoiseSuppressor noiseSuppressor = this.ns;
        if (noiseSuppressor != null) {
            noiseSuppressor.release();
            this.ns = null;
        }
    }

    private boolean effectTypeIsVoIP(UUID type) {
        if (Build.VERSION.SDK_INT < 18) {
            return false;
        }
        if ((!AudioEffect.EFFECT_TYPE_AEC.equals(type) || !isAcousticEchoCancelerSupported()) && (!AudioEffect.EFFECT_TYPE_NS.equals(type) || !isNoiseSuppressorSupported())) {
            return false;
        }
        return true;
    }

    private static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected condition to be true");
        }
    }

    private static AudioEffect.Descriptor[] getAvailableEffects() {
        AudioEffect.Descriptor[] descriptorArr = cachedEffects;
        if (descriptorArr != null) {
            return descriptorArr;
        }
        AudioEffect.Descriptor[] queryEffects = AudioEffect.queryEffects();
        cachedEffects = queryEffects;
        return queryEffects;
    }

    private static boolean isEffectTypeAvailable(UUID effectType, UUID blockListedUuid) {
        AudioEffect.Descriptor[] effects = getAvailableEffects();
        if (effects == null) {
            return false;
        }
        for (AudioEffect.Descriptor d : effects) {
            if (d.type.equals(effectType)) {
                return !d.uuid.equals(blockListedUuid);
            }
        }
        return false;
    }
}
