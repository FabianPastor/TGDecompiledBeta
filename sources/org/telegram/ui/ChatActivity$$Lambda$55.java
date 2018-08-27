package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class ChatActivity$$Lambda$55 implements OnClickListener {
    private final ChatActivity arg$1;
    private final int arg$2;
    private final boolean[] arg$3;

    ChatActivity$$Lambda$55(ChatActivity chatActivity, int i, boolean[] zArr) {
        this.arg$1 = chatActivity;
        this.arg$2 = i;
        this.arg$3 = zArr;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$processSelectedOption$71$ChatActivity(this.arg$2, this.arg$3, dialogInterface, i);
    }
}
