package org.telegram.ui.Components;

import android.util.SparseArray;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda97 implements RequestDelegate {
    public final /* synthetic */ AlertDialog[] f$0;
    public final /* synthetic */ BaseFragment f$1;
    public final /* synthetic */ boolean f$10;
    public final /* synthetic */ Runnable f$11;
    public final /* synthetic */ Runnable f$12;
    public final /* synthetic */ Theme.ResourcesProvider f$13;
    public final /* synthetic */ TLRPC$User f$2;
    public final /* synthetic */ TLRPC$Chat f$3;
    public final /* synthetic */ TLRPC$EncryptedChat f$4;
    public final /* synthetic */ TLRPC$ChatFull f$5;
    public final /* synthetic */ long f$6;
    public final /* synthetic */ MessageObject f$7;
    public final /* synthetic */ SparseArray[] f$8;
    public final /* synthetic */ MessageObject.GroupedMessages f$9;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda97(AlertDialog[] alertDialogArr, BaseFragment baseFragment, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, TLRPC$EncryptedChat tLRPC$EncryptedChat, TLRPC$ChatFull tLRPC$ChatFull, long j, MessageObject messageObject, SparseArray[] sparseArrayArr, MessageObject.GroupedMessages groupedMessages, boolean z, Runnable runnable, Runnable runnable2, Theme.ResourcesProvider resourcesProvider) {
        this.f$0 = alertDialogArr;
        this.f$1 = baseFragment;
        this.f$2 = tLRPC$User;
        this.f$3 = tLRPC$Chat;
        this.f$4 = tLRPC$EncryptedChat;
        this.f$5 = tLRPC$ChatFull;
        this.f$6 = j;
        this.f$7 = messageObject;
        this.f$8 = sparseArrayArr;
        this.f$9 = groupedMessages;
        this.f$10 = z;
        this.f$11 = runnable;
        this.f$12 = runnable2;
        this.f$13 = resourcesProvider;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new AlertsCreator$$ExternalSyntheticLambda93(this.f$0, tLObject, tLRPC$TL_error, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13));
    }
}
