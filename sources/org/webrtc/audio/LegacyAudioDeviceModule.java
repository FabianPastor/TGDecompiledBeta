package org.webrtc.audio;
@Deprecated
/* loaded from: classes.dex */
public class LegacyAudioDeviceModule implements AudioDeviceModule {
    @Override // org.webrtc.audio.AudioDeviceModule
    public long getNativeAudioDeviceModulePointer() {
        return 0L;
    }

    @Override // org.webrtc.audio.AudioDeviceModule
    public void release() {
    }

    @Override // org.webrtc.audio.AudioDeviceModule
    public void setSpeakerMute(boolean z) {
        org.webrtc.voiceengine.WebRtcAudioTrack.setSpeakerMute(z);
    }

    @Override // org.webrtc.audio.AudioDeviceModule
    public void setMicrophoneMute(boolean z) {
        org.webrtc.voiceengine.WebRtcAudioRecord.setMicrophoneMute(z);
    }
}
