package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.ChatSettingsActivity$$Lambda$2 */
final /* synthetic */ class ChatSettingsActivity$$Lambda$2 implements OnClickListener {
    private final ChatSettingsActivity arg$1;
    private final int arg$2;

    ChatSettingsActivity$$Lambda$2(ChatSettingsActivity chatSettingsActivity, int i) {
        this.arg$1 = chatSettingsActivity;
        this.arg$2 = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$1$ChatSettingsActivity(this.arg$2, dialogInterface, i);
    }
}
