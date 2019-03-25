package org.telegram.ui.Components;

import android.util.SparseArray;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessageObject.GroupedMessages;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

final /* synthetic */ class AlertsCreator$$Lambda$45 implements Runnable {
    private final AlertDialog[] arg$1;
    private final SparseArray[] arg$10;
    private final GroupedMessages arg$11;
    private final Runnable arg$12;
    private final TLObject arg$2;
    private final BaseFragment arg$3;
    private final User arg$4;
    private final Chat arg$5;
    private final EncryptedChat arg$6;
    private final ChatFull arg$7;
    private final long arg$8;
    private final MessageObject arg$9;

    AlertsCreator$$Lambda$45(AlertDialog[] alertDialogArr, TLObject tLObject, BaseFragment baseFragment, User user, Chat chat, EncryptedChat encryptedChat, ChatFull chatFull, long j, MessageObject messageObject, SparseArray[] sparseArrayArr, GroupedMessages groupedMessages, Runnable runnable) {
        this.arg$1 = alertDialogArr;
        this.arg$2 = tLObject;
        this.arg$3 = baseFragment;
        this.arg$4 = user;
        this.arg$5 = chat;
        this.arg$6 = encryptedChat;
        this.arg$7 = chatFull;
        this.arg$8 = j;
        this.arg$9 = messageObject;
        this.arg$10 = sparseArrayArr;
        this.arg$11 = groupedMessages;
        this.arg$12 = runnable;
    }

    public void run() {
        AlertsCreator.lambda$null$40$AlertsCreator(this.arg$1, this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9, this.arg$10, this.arg$11, this.arg$12);
    }
}
