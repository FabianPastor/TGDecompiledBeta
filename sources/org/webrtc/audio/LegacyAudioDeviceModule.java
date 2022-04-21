package org.webrtc.audio;

import org.webrtc.voiceengine.WebRtcAudioRecord;
import org.webrtc.voiceengine.WebRtcAudioTrack;

@Deprecated
public class LegacyAudioDeviceModule implements AudioDeviceModule {
    public long getNativeAudioDeviceModulePointer() {
        return 0;
    }

    public void release() {
    }

    public void setSpeakerMute(boolean mute) {
        WebRtcAudioTrack.setSpeakerMute(mute);
    }

    public void setMicrophoneMute(boolean mute) {
        WebRtcAudioRecord.setMicrophoneMute(mute);
    }
}
