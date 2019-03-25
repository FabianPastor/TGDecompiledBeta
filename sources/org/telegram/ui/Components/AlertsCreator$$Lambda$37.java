package org.telegram.ui.Components;

import android.util.SparseArray;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessageObject.GroupedMessages;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

final /* synthetic */ class AlertsCreator$$Lambda$37 implements RequestDelegate {
    private final AlertDialog[] arg$1;
    private final GroupedMessages arg$10;
    private final Runnable arg$11;
    private final BaseFragment arg$2;
    private final User arg$3;
    private final Chat arg$4;
    private final EncryptedChat arg$5;
    private final ChatFull arg$6;
    private final long arg$7;
    private final MessageObject arg$8;
    private final SparseArray[] arg$9;

    AlertsCreator$$Lambda$37(AlertDialog[] alertDialogArr, BaseFragment baseFragment, User user, Chat chat, EncryptedChat encryptedChat, ChatFull chatFull, long j, MessageObject messageObject, SparseArray[] sparseArrayArr, GroupedMessages groupedMessages, Runnable runnable) {
        this.arg$1 = alertDialogArr;
        this.arg$2 = baseFragment;
        this.arg$3 = user;
        this.arg$4 = chat;
        this.arg$5 = encryptedChat;
        this.arg$6 = chatFull;
        this.arg$7 = j;
        this.arg$8 = messageObject;
        this.arg$9 = sparseArrayArr;
        this.arg$10 = groupedMessages;
        this.arg$11 = runnable;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new AlertsCreator$$Lambda$45(this.arg$1, tLObject, this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9, this.arg$10, this.arg$11));
    }
}
