package org.telegram.ui.Components;

import android.util.SparseArray;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda79 implements Runnable {
    public final /* synthetic */ AlertDialog[] f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ SparseArray[] f$10;
    public final /* synthetic */ MessageObject.GroupedMessages f$11;
    public final /* synthetic */ boolean f$12;
    public final /* synthetic */ Runnable f$13;
    public final /* synthetic */ Theme.ResourcesProvider f$14;
    public final /* synthetic */ TLRPC.TL_error f$2;
    public final /* synthetic */ BaseFragment f$3;
    public final /* synthetic */ TLRPC.User f$4;
    public final /* synthetic */ TLRPC.Chat f$5;
    public final /* synthetic */ TLRPC.EncryptedChat f$6;
    public final /* synthetic */ TLRPC.ChatFull f$7;
    public final /* synthetic */ long f$8;
    public final /* synthetic */ MessageObject f$9;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda79(AlertDialog[] alertDialogArr, TLObject tLObject, TLRPC.TL_error tL_error, BaseFragment baseFragment, TLRPC.User user, TLRPC.Chat chat, TLRPC.EncryptedChat encryptedChat, TLRPC.ChatFull chatFull, long j, MessageObject messageObject, SparseArray[] sparseArrayArr, MessageObject.GroupedMessages groupedMessages, boolean z, Runnable runnable, Theme.ResourcesProvider resourcesProvider) {
        this.f$0 = alertDialogArr;
        this.f$1 = tLObject;
        this.f$2 = tL_error;
        this.f$3 = baseFragment;
        this.f$4 = user;
        this.f$5 = chat;
        this.f$6 = encryptedChat;
        this.f$7 = chatFull;
        this.f$8 = j;
        this.f$9 = messageObject;
        this.f$10 = sparseArrayArr;
        this.f$11 = groupedMessages;
        this.f$12 = z;
        this.f$13 = runnable;
        this.f$14 = resourcesProvider;
    }

    public final void run() {
        AlertsCreator.lambda$createDeleteMessagesAlert$88(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14);
    }
}
