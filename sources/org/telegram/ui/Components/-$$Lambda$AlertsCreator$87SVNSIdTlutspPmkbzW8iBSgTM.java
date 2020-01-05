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

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$87SVNSIdTlutspPmkbzW8iBSgTM implements Runnable {
    private final /* synthetic */ AlertDialog[] f$0;
    private final /* synthetic */ TLObject f$1;
    private final /* synthetic */ GroupedMessages f$10;
    private final /* synthetic */ boolean f$11;
    private final /* synthetic */ Runnable f$12;
    private final /* synthetic */ BaseFragment f$2;
    private final /* synthetic */ User f$3;
    private final /* synthetic */ Chat f$4;
    private final /* synthetic */ EncryptedChat f$5;
    private final /* synthetic */ ChatFull f$6;
    private final /* synthetic */ long f$7;
    private final /* synthetic */ MessageObject f$8;
    private final /* synthetic */ SparseArray[] f$9;

    public /* synthetic */ -$$Lambda$AlertsCreator$87SVNSIdTlutspPmkbzW8iBSgTM(AlertDialog[] alertDialogArr, TLObject tLObject, BaseFragment baseFragment, User user, Chat chat, EncryptedChat encryptedChat, ChatFull chatFull, long j, MessageObject messageObject, SparseArray[] sparseArrayArr, GroupedMessages groupedMessages, boolean z, Runnable runnable) {
        this.f$0 = alertDialogArr;
        this.f$1 = tLObject;
        this.f$2 = baseFragment;
        this.f$3 = user;
        this.f$4 = chat;
        this.f$5 = encryptedChat;
        this.f$6 = chatFull;
        this.f$7 = j;
        this.f$8 = messageObject;
        this.f$9 = sparseArrayArr;
        this.f$10 = groupedMessages;
        this.f$11 = z;
        this.f$12 = runnable;
    }

    public final void run() {
        AlertsCreator.lambda$null$52(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12);
    }
}
