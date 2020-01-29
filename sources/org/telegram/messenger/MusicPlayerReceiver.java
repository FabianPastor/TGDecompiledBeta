package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

public class MusicPlayerReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        KeyEvent keyEvent;
        if (intent.getAction().equals("android.intent.action.MEDIA_BUTTON")) {
            if (intent.getExtras() != null && (keyEvent = (KeyEvent) intent.getExtras().get("android.intent.extra.KEY_EVENT")) != null && keyEvent.getAction() == 0) {
                int keyCode = keyEvent.getKeyCode();
                if (keyCode != 79) {
                    if (keyCode == 126) {
                        MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
                        return;
                    } else if (keyCode != 127) {
                        switch (keyCode) {
                            case 85:
                                break;
                            case 87:
                                MediaController.getInstance().playNextMessage();
                                return;
                            case 88:
                                MediaController.getInstance().playPreviousMessage();
                                return;
                            default:
                                return;
                        }
                    } else {
                        MediaController.getInstance().lambda$startAudioAgain$6$MediaController(MediaController.getInstance().getPlayingMessageObject());
                        return;
                    }
                }
                if (MediaController.getInstance().isMessagePaused()) {
                    MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
                } else {
                    MediaController.getInstance().lambda$startAudioAgain$6$MediaController(MediaController.getInstance().getPlayingMessageObject());
                }
            }
        } else if (intent.getAction().equals("org.telegram.android.musicplayer.play")) {
            MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
        } else if (intent.getAction().equals("org.telegram.android.musicplayer.pause") || intent.getAction().equals("android.media.AUDIO_BECOMING_NOISY")) {
            MediaController.getInstance().lambda$startAudioAgain$6$MediaController(MediaController.getInstance().getPlayingMessageObject());
        } else if (intent.getAction().equals("org.telegram.android.musicplayer.next")) {
            MediaController.getInstance().playNextMessage();
        } else if (intent.getAction().equals("org.telegram.android.musicplayer.close")) {
            MediaController.getInstance().cleanupPlayer(true, true);
        } else if (intent.getAction().equals("org.telegram.android.musicplayer.previous")) {
            MediaController.getInstance().playPreviousMessage();
        }
    }
}
