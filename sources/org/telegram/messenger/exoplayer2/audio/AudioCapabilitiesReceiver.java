package org.telegram.messenger.exoplayer2.audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

public final class AudioCapabilitiesReceiver {
    AudioCapabilities audioCapabilities;
    private final Context context;
    private final Listener listener;
    private final BroadcastReceiver receiver;

    private final class HdmiAudioPlugBroadcastReceiver extends BroadcastReceiver {
        private HdmiAudioPlugBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (!isInitialStickyBroadcast()) {
                AudioCapabilities newAudioCapabilities = AudioCapabilities.getCapabilities(intent);
                if (!newAudioCapabilities.equals(AudioCapabilitiesReceiver.this.audioCapabilities)) {
                    AudioCapabilitiesReceiver.this.audioCapabilities = newAudioCapabilities;
                    AudioCapabilitiesReceiver.this.listener.onAudioCapabilitiesChanged(newAudioCapabilities);
                }
            }
        }
    }

    public interface Listener {
        void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities);
    }

    public AudioCapabilitiesReceiver(Context context, Listener listener) {
        this.context = (Context) Assertions.checkNotNull(context);
        this.listener = (Listener) Assertions.checkNotNull(listener);
        this.receiver = Util.SDK_INT >= 21 ? new HdmiAudioPlugBroadcastReceiver() : null;
    }

    public AudioCapabilities register() {
        Intent stickyIntent;
        if (this.receiver == null) {
            stickyIntent = null;
        } else {
            stickyIntent = this.context.registerReceiver(this.receiver, new IntentFilter("android.media.action.HDMI_AUDIO_PLUG"));
        }
        this.audioCapabilities = AudioCapabilities.getCapabilities(stickyIntent);
        return this.audioCapabilities;
    }

    public void unregister() {
        if (this.receiver != null) {
            this.context.unregisterReceiver(this.receiver);
        }
    }
}
