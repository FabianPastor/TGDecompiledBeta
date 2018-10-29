package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessageObject.GroupedMessages;
import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class ChatActivity$$Lambda$51 implements OnClickListener {
    private final ChatActivity arg$1;
    private final MessageObject arg$2;
    private final GroupedMessages arg$3;
    private final boolean[] arg$4;
    private final User arg$5;
    private final boolean[] arg$6;

    ChatActivity$$Lambda$51(ChatActivity chatActivity, MessageObject messageObject, GroupedMessages groupedMessages, boolean[] zArr, User user, boolean[] zArr2) {
        this.arg$1 = chatActivity;
        this.arg$2 = messageObject;
        this.arg$3 = groupedMessages;
        this.arg$4 = zArr;
        this.arg$5 = user;
        this.arg$6 = zArr2;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$createDeleteMessagesAlert$66$ChatActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, dialogInterface, i);
    }
}
