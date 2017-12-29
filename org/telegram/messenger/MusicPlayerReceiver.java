package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

public class MusicPlayerReceiver extends BroadcastReceiver {
    private int currentAccount = UserConfig.selectedAccount;

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.MEDIA_BUTTON")) {
            if (intent.getExtras() != null) {
                KeyEvent keyEvent = (KeyEvent) intent.getExtras().get("android.intent.extra.KEY_EVENT");
                if (keyEvent != null && keyEvent.getAction() == 0) {
                    switch (keyEvent.getKeyCode()) {
                        case 79:
                        case 85:
                            if (MediaController.getInstance(this.currentAccount).isMessagePaused()) {
                                MediaController.getInstance(this.currentAccount).playMessage(MediaController.getInstance(this.currentAccount).getPlayingMessageObject());
                                return;
                            } else {
                                MediaController.getInstance(this.currentAccount).pauseMessage(MediaController.getInstance(this.currentAccount).getPlayingMessageObject());
                                return;
                            }
                        case 86:
                            return;
                        case 87:
                            MediaController.getInstance(this.currentAccount).playNextMessage();
                            return;
                        case 88:
                            MediaController.getInstance(this.currentAccount).playPreviousMessage();
                            return;
                        case 126:
                            MediaController.getInstance(this.currentAccount).playMessage(MediaController.getInstance(this.currentAccount).getPlayingMessageObject());
                            return;
                        case 127:
                            MediaController.getInstance(this.currentAccount).pauseMessage(MediaController.getInstance(this.currentAccount).getPlayingMessageObject());
                            return;
                        default:
                            return;
                    }
                }
            }
        } else if (intent.getAction().equals(MusicPlayerService.NOTIFY_PLAY)) {
            MediaController.getInstance(this.currentAccount).playMessage(MediaController.getInstance(this.currentAccount).getPlayingMessageObject());
        } else if (intent.getAction().equals(MusicPlayerService.NOTIFY_PAUSE) || intent.getAction().equals("android.media.AUDIO_BECOMING_NOISY")) {
            MediaController.getInstance(this.currentAccount).pauseMessage(MediaController.getInstance(this.currentAccount).getPlayingMessageObject());
        } else if (intent.getAction().equals(MusicPlayerService.NOTIFY_NEXT)) {
            MediaController.getInstance(this.currentAccount).playNextMessage();
        } else if (intent.getAction().equals(MusicPlayerService.NOTIFY_CLOSE)) {
            MediaController.getInstance(this.currentAccount).cleanupPlayer(true, true);
        } else if (intent.getAction().equals(MusicPlayerService.NOTIFY_PREVIOUS)) {
            MediaController.getInstance(this.currentAccount).playPreviousMessage();
        }
    }
}
