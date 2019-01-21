package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

final /* synthetic */ class ChatEditActivity$$Lambda$17 implements OnCancelListener {
    private final ChatEditActivity arg$1;

    ChatEditActivity$$Lambda$17(ChatEditActivity chatEditActivity) {
        this.arg$1 = chatEditActivity;
    }

    public void onCancel(DialogInterface dialogInterface) {
        this.arg$1.lambda$processDone$20$ChatEditActivity(dialogInterface);
    }
}
