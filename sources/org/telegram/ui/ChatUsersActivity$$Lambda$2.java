package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.ChatParticipant;

final /* synthetic */ class ChatUsersActivity$$Lambda$2 implements OnClickListener {
    private final ChatUsersActivity arg$1;
    private final ArrayList arg$2;
    private final ChatParticipant arg$3;

    ChatUsersActivity$$Lambda$2(ChatUsersActivity chatUsersActivity, ArrayList arrayList, ChatParticipant chatParticipant) {
        this.arg$1 = chatUsersActivity;
        this.arg$2 = arrayList;
        this.arg$3 = chatParticipant;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$createMenuForParticipant$2$ChatUsersActivity(this.arg$2, this.arg$3, dialogInterface, i);
    }
}
