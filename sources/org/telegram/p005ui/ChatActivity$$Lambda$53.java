package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.ArrayList;

/* renamed from: org.telegram.ui.ChatActivity$$Lambda$53 */
final /* synthetic */ class ChatActivity$$Lambda$53 implements OnClickListener {
    private final ChatActivity arg$1;
    private final ArrayList arg$2;

    ChatActivity$$Lambda$53(ChatActivity chatActivity, ArrayList arrayList) {
        this.arg$1 = chatActivity;
        this.arg$2 = arrayList;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$createMenu$68$ChatActivity(this.arg$2, dialogInterface, i);
    }
}
