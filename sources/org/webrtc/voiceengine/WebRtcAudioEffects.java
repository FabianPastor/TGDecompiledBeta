package org.webrtc.voiceengine;

import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build;
import java.util.UUID;
import org.telegram.messenger.SharedConfig;
import org.webrtc.Logging;

public class WebRtcAudioEffects {
    private static final UUID AOSP_ACOUSTIC_ECHO_CANCELER = UUID.fromString("bb392ec0-8d4d-11e0-a896-0002a5d5CLASSNAMEb");
    private static final UUID AOSP_NOISE_SUPPRESSOR = UUID.fromString("CLASSNAMECLASSNAME-8e06-11e0-9cb6-0002a5d5CLASSNAMEb");
    private static final boolean DEBUG = false;
    private static final String TAG = "WebRtcAudioEffects";
    private static AudioEffect.Descriptor[] cachedEffects;
    private AcousticEchoCanceler aec;
    private NoiseSuppressor ns;
    private boolean shouldEnableAec;
    private boolean shouldEnableNs;

    public static boolean isAcousticEchoCancelerSupported() {
        return isAcousticEchoCancelerEffectAvailable();
    }

    public static boolean isNoiseSuppressorSupported() {
        return isNoiseSuppressorEffectAvailable();
    }

    public static boolean isAcousticEchoCancelerBlacklisted() {
        boolean isBlacklisted = WebRtcAudioUtils.getBlackListedModelsForAecUsage().contains(Build.MODEL);
        if (isBlacklisted) {
            Logging.w("WebRtcAudioEffects", Build.MODEL + " is blacklisted for HW AEC usage!");
        }
        return isBlacklisted;
    }

    public static boolean isNoiseSuppressorBlacklisted() {
        boolean isBlacklisted = WebRtcAudioUtils.getBlackListedModelsForNsUsage().contains(Build.MODEL);
        if (isBlacklisted) {
            Logging.w("WebRtcAudioEffects", Build.MODEL + " is blacklisted for HW NS usage!");
        }
        return isBlacklisted;
    }

    private static boolean isAcousticEchoCancelerExcludedByUUID() {
        if (Build.VERSION.SDK_INT < 18) {
            return false;
        }
        for (AudioEffect.Descriptor d : getAvailableEffects()) {
            if (d.type.equals(AudioEffect.EFFECT_TYPE_AEC) && d.uuid.equals(AOSP_ACOUSTIC_ECHO_CANCELER)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isNoiseSuppressorExcludedByUUID() {
        if (Build.VERSION.SDK_INT < 18) {
            return false;
        }
        for (AudioEffect.Descriptor d : getAvailableEffects()) {
            if (d.type.equals(AudioEffect.EFFECT_TYPE_NS) && d.uuid.equals(AOSP_NOISE_SUPPRESSOR)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isAcousticEchoCancelerEffectAvailable() {
        if (Build.VERSION.SDK_INT < 18) {
            return false;
        }
        return isEffectTypeAvailable(AudioEffect.EFFECT_TYPE_AEC);
    }

    private static boolean isNoiseSuppressorEffectAvailable() {
        if (Build.VERSION.SDK_INT < 18) {
            return false;
        }
        return isEffectTypeAvailable(AudioEffect.EFFECT_TYPE_NS);
    }

    public static boolean canUseAcousticEchoCanceler() {
        boolean canUseAcousticEchoCanceler = isAcousticEchoCancelerSupported() && !WebRtcAudioUtils.useWebRtcBasedAcousticEchoCanceler() && !isAcousticEchoCancelerBlacklisted() && !isAcousticEchoCancelerExcludedByUUID();
        Logging.d("WebRtcAudioEffects", "canUseAcousticEchoCanceler: " + canUseAcousticEchoCanceler);
        return canUseAcousticEchoCanceler;
    }

    public static boolean canUseNoiseSuppressor() {
        boolean canUseNoiseSuppressor = isNoiseSuppressorSupported() && !WebRtcAudioUtils.useWebRtcBasedNoiseSuppressor() && !isNoiseSuppressorBlacklisted() && !isNoiseSuppressorExcludedByUUID();
        Logging.d("WebRtcAudioEffects", "canUseNoiseSuppressor: " + canUseNoiseSuppressor);
        return canUseNoiseSuppressor;
    }

    public static WebRtcAudioEffects create() {
        return new WebRtcAudioEffects();
    }

    private WebRtcAudioEffects() {
        Logging.d("WebRtcAudioEffects", "ctor" + WebRtcAudioUtils.getThreadInfo());
    }

    public boolean setAEC(boolean enable) {
        Logging.d("WebRtcAudioEffects", "setAEC(" + enable + ")");
        if (!canUseAcousticEchoCanceler()) {
            Logging.w("WebRtcAudioEffects", "Platform AEC is not supported");
            this.shouldEnableAec = false;
            return false;
        } else if (this.aec == null || enable == this.shouldEnableAec) {
            this.shouldEnableAec = enable;
            return true;
        } else {
            Logging.e("WebRtcAudioEffects", "Platform AEC state can't be modified while recording");
            return false;
        }
    }

    public boolean setNS(boolean enable) {
        Logging.d("WebRtcAudioEffects", "setNS(" + enable + ")");
        if (!canUseNoiseSuppressor()) {
            Logging.w("WebRtcAudioEffects", "Platform NS is not supported");
            this.shouldEnableNs = false;
            return false;
        } else if (this.ns == null || enable == this.shouldEnableNs) {
            this.shouldEnableNs = enable;
            return true;
        } else {
            Logging.e("WebRtcAudioEffects", "Platform NS state can't be modified while recording");
            return false;
        }
    }

    public void enable(int audioSession) {
        String str;
        String str2;
        String str3;
        Logging.d("WebRtcAudioEffects", "enable(audioSession=" + audioSession + ")");
        boolean enable = true;
        assertTrue(this.aec == null);
        assertTrue(this.ns == null);
        String str4 = "enabled";
        if (isAcousticEchoCancelerSupported()) {
            AcousticEchoCanceler create = AcousticEchoCanceler.create(audioSession);
            this.aec = create;
            if (create != null) {
                boolean enabled = create.getEnabled();
                boolean enable2 = this.shouldEnableAec && canUseAcousticEchoCanceler() && !SharedConfig.disableVoiceAudioEffects;
                if (this.aec.setEnabled(enable2) != 0) {
                    Logging.e("WebRtcAudioEffects", "Failed to set the AcousticEchoCanceler state");
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
                Logging.d("WebRtcAudioEffects", sb.toString());
            } else {
                Logging.e("WebRtcAudioEffects", "Failed to create the AcousticEchoCanceler instance");
            }
        }
        if (isNoiseSuppressorSupported()) {
            NoiseSuppressor create2 = NoiseSuppressor.create(audioSession);
            this.ns = create2;
            if (create2 != null) {
                boolean enabled2 = create2.getEnabled();
                if (!this.shouldEnableNs || !canUseNoiseSuppressor() || SharedConfig.disableVoiceAudioEffects) {
                    enable = false;
                }
                if (this.ns.setEnabled(enable) != 0) {
                    Logging.e("WebRtcAudioEffects", "Failed to set the NoiseSuppressor state");
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
                Logging.d("WebRtcAudioEffects", sb2.toString());
                return;
            }
            Logging.e("WebRtcAudioEffects", "Failed to create the NoiseSuppressor instance");
        }
    }

    public void release() {
        Logging.d("WebRtcAudioEffects", "release");
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

    private static boolean isEffectTypeAvailable(UUID effectType) {
        AudioEffect.Descriptor[] effects = getAvailableEffects();
        if (effects == null) {
            return false;
        }
        for (AudioEffect.Descriptor d : effects) {
            if (d.type.equals(effectType)) {
                return true;
            }
        }
        return false;
    }
}
