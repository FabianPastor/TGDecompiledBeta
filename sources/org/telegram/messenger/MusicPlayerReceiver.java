package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

public class MusicPlayerReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        KeyEvent keyEvent;
        if (!intent.getAction().equals("android.intent.action.MEDIA_BUTTON")) {
            String action = intent.getAction();
            char c = 65535;
            switch (action.hashCode()) {
                case -1461225938:
                    if (action.equals("org.telegram.android.musicplayer.close")) {
                        c = 4;
                        break;
                    }
                    break;
                case -1449542100:
                    if (action.equals("org.telegram.android.musicplayer.pause")) {
                        c = 1;
                        break;
                    }
                    break;
                case -1293741059:
                    if (action.equals("org.telegram.android.musicplayer.next")) {
                        c = 3;
                        break;
                    }
                    break;
                case -1293675458:
                    if (action.equals("org.telegram.android.musicplayer.play")) {
                        c = 0;
                        break;
                    }
                    break;
                case -549244379:
                    if (action.equals("android.media.AUDIO_BECOMING_NOISY")) {
                        c = 2;
                        break;
                    }
                    break;
                case 40087297:
                    if (action.equals("org.telegram.android.musicplayer.previous")) {
                        c = 5;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
                    return;
                case 1:
                case 2:
                    MediaController.getInstance().m102lambda$startAudioAgain$7$orgtelegrammessengerMediaController(MediaController.getInstance().getPlayingMessageObject());
                    return;
                case 3:
                    MediaController.getInstance().playNextMessage();
                    return;
                case 4:
                    MediaController.getInstance().cleanupPlayer(true, true);
                    return;
                case 5:
                    MediaController.getInstance().playPreviousMessage();
                    return;
                default:
                    return;
            }
        } else if (intent.getExtras() != null && (keyEvent = (KeyEvent) intent.getExtras().get("android.intent.extra.KEY_EVENT")) != null && keyEvent.getAction() == 0) {
            switch (keyEvent.getKeyCode()) {
                case 79:
                case 85:
                    if (MediaController.getInstance().isMessagePaused()) {
                        MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
                        return;
                    } else {
                        MediaController.getInstance().m102lambda$startAudioAgain$7$orgtelegrammessengerMediaController(MediaController.getInstance().getPlayingMessageObject());
                        return;
                    }
                case 87:
                    MediaController.getInstance().playNextMessage();
                    return;
                case 88:
                    MediaController.getInstance().playPreviousMessage();
                    return;
                case 126:
                    MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
                    return;
                case 127:
                    MediaController.getInstance().m102lambda$startAudioAgain$7$orgtelegrammessengerMediaController(MediaController.getInstance().getPlayingMessageObject());
                    return;
                default:
                    return;
            }
        }
    }
}
