package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.SparseArray;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessageObject.GroupedMessages;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class AlertsCreator$$Lambda$42 implements OnClickListener {
    private final MessageObject arg$1;
    private final ChatFull arg$10;
    private final Runnable arg$11;
    private final GroupedMessages arg$2;
    private final EncryptedChat arg$3;
    private final int arg$4;
    private final boolean[] arg$5;
    private final SparseArray[] arg$6;
    private final User arg$7;
    private final boolean[] arg$8;
    private final Chat arg$9;

    AlertsCreator$$Lambda$42(MessageObject messageObject, GroupedMessages groupedMessages, EncryptedChat encryptedChat, int i, boolean[] zArr, SparseArray[] sparseArrayArr, User user, boolean[] zArr2, Chat chat, ChatFull chatFull, Runnable runnable) {
        this.arg$1 = messageObject;
        this.arg$2 = groupedMessages;
        this.arg$3 = encryptedChat;
        this.arg$4 = i;
        this.arg$5 = zArr;
        this.arg$6 = sparseArrayArr;
        this.arg$7 = user;
        this.arg$8 = zArr2;
        this.arg$9 = chat;
        this.arg$10 = chatFull;
        this.arg$11 = runnable;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createDeleteMessagesAlert$48$AlertsCreator(this.arg$1, this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9, this.arg$10, this.arg$11, dialogInterface, i);
    }
}
