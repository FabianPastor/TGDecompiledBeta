package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

public class MusicPlayerReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.MEDIA_BUTTON") != null) {
            if (intent.getExtras() != null) {
                KeyEvent keyEvent = (KeyEvent) intent.getExtras().get("android.intent.extra.KEY_EVENT");
                if (keyEvent != null && keyEvent.getAction() == null) {
                    context = keyEvent.getKeyCode();
                    if (context != 79) {
                        switch (context) {
                            case 85:
                                break;
                            case 86:
                                break;
                            case 87:
                                MediaController.getInstance().playNextMessage();
                                break;
                            case 88:
                                MediaController.getInstance().playPreviousMessage();
                                break;
                            default:
                                switch (context) {
                                    case 126:
                                        MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
                                        break;
                                    case 127:
                                        MediaController.getInstance().pauseMessage(MediaController.getInstance().getPlayingMessageObject());
                                        break;
                                    default:
                                        break;
                                }
                        }
                    }
                    if (MediaController.getInstance().isMessagePaused() != null) {
                        MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
                    } else {
                        MediaController.getInstance().pauseMessage(MediaController.getInstance().getPlayingMessageObject());
                    }
                }
            }
        } else if (intent.getAction().equals(MusicPlayerService.NOTIFY_PLAY) != null) {
            MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
        } else {
            if (intent.getAction().equals(MusicPlayerService.NOTIFY_PAUSE) == null) {
                if (intent.getAction().equals("android.media.AUDIO_BECOMING_NOISY") == null) {
                    if (intent.getAction().equals(MusicPlayerService.NOTIFY_NEXT) != null) {
                        MediaController.getInstance().playNextMessage();
                    } else if (intent.getAction().equals(MusicPlayerService.NOTIFY_CLOSE) != null) {
                        MediaController.getInstance().cleanupPlayer(true, true);
                    } else if (intent.getAction().equals(MusicPlayerService.NOTIFY_PREVIOUS) != null) {
                        MediaController.getInstance().playPreviousMessage();
                    }
                }
            }
            MediaController.getInstance().pauseMessage(MediaController.getInstance().getPlayingMessageObject());
        }
    }
}
