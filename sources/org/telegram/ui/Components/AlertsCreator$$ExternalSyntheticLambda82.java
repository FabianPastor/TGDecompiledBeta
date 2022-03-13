package org.telegram.ui.Components;

import android.util.SparseArray;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda82 implements Runnable {
    public final /* synthetic */ AlertDialog[] f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ SparseArray[] f$10;
    public final /* synthetic */ MessageObject.GroupedMessages f$11;
    public final /* synthetic */ boolean f$12;
    public final /* synthetic */ Runnable f$13;
    public final /* synthetic */ Runnable f$14;
    public final /* synthetic */ Theme.ResourcesProvider f$15;
    public final /* synthetic */ TLRPC$TL_error f$2;
    public final /* synthetic */ BaseFragment f$3;
    public final /* synthetic */ TLRPC$User f$4;
    public final /* synthetic */ TLRPC$Chat f$5;
    public final /* synthetic */ TLRPC$EncryptedChat f$6;
    public final /* synthetic */ TLRPC$ChatFull f$7;
    public final /* synthetic */ long f$8;
    public final /* synthetic */ MessageObject f$9;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda82(AlertDialog[] alertDialogArr, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error, BaseFragment baseFragment, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, TLRPC$EncryptedChat tLRPC$EncryptedChat, TLRPC$ChatFull tLRPC$ChatFull, long j, MessageObject messageObject, SparseArray[] sparseArrayArr, MessageObject.GroupedMessages groupedMessages, boolean z, Runnable runnable, Runnable runnable2, Theme.ResourcesProvider resourcesProvider) {
        this.f$0 = alertDialogArr;
        this.f$1 = tLObject;
        this.f$2 = tLRPC$TL_error;
        this.f$3 = baseFragment;
        this.f$4 = tLRPC$User;
        this.f$5 = tLRPC$Chat;
        this.f$6 = tLRPC$EncryptedChat;
        this.f$7 = tLRPC$ChatFull;
        this.f$8 = j;
        this.f$9 = messageObject;
        this.f$10 = sparseArrayArr;
        this.f$11 = groupedMessages;
        this.f$12 = z;
        this.f$13 = runnable;
        this.f$14 = runnable2;
        this.f$15 = resourcesProvider;
    }

    public final void run() {
        AlertDialog[] alertDialogArr = this.f$0;
        AlertDialog[] alertDialogArr2 = alertDialogArr;
        AlertsCreator.lambda$createDeleteMessagesAlert$93(alertDialogArr2, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14, this.f$15);
    }
}
