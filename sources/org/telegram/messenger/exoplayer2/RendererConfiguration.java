package org.telegram.messenger.exoplayer2;

public final class RendererConfiguration {
    public static final RendererConfiguration DEFAULT = new RendererConfiguration(0);
    public final int tunnelingAudioSessionId;

    public RendererConfiguration(int tunnelingAudioSessionId) {
        this.tunnelingAudioSessionId = tunnelingAudioSessionId;
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                if (this.tunnelingAudioSessionId != ((RendererConfiguration) obj).tunnelingAudioSessionId) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        return this.tunnelingAudioSessionId;
    }
}
