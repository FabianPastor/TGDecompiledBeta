package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessageObject;

final /* synthetic */ class AudioPlayerAlert$$Lambda$13 implements OnClickListener {
    private final AudioPlayerAlert arg$1;
    private final MessageObject arg$2;
    private final boolean[] arg$3;

    AudioPlayerAlert$$Lambda$13(AudioPlayerAlert audioPlayerAlert, MessageObject messageObject, boolean[] zArr) {
        this.arg$1 = audioPlayerAlert;
        this.arg$2 = messageObject;
        this.arg$3 = zArr;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onSubItemClick$12$AudioPlayerAlert(this.arg$2, this.arg$3, dialogInterface, i);
    }
}
