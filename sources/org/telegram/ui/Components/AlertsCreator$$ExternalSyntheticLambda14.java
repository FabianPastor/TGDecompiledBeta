package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.util.SparseArray;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda14 implements DialogInterface.OnClickListener {
    public final /* synthetic */ MessageObject f$0;
    public final /* synthetic */ MessageObject.GroupedMessages f$1;
    public final /* synthetic */ TLRPC$Chat f$10;
    public final /* synthetic */ TLRPC$ChatFull f$11;
    public final /* synthetic */ Runnable f$12;
    public final /* synthetic */ TLRPC$EncryptedChat f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ long f$4;
    public final /* synthetic */ boolean[] f$5;
    public final /* synthetic */ boolean f$6;
    public final /* synthetic */ SparseArray[] f$7;
    public final /* synthetic */ TLRPC$User f$8;
    public final /* synthetic */ boolean[] f$9;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda14(MessageObject messageObject, MessageObject.GroupedMessages groupedMessages, TLRPC$EncryptedChat tLRPC$EncryptedChat, int i, long j, boolean[] zArr, boolean z, SparseArray[] sparseArrayArr, TLRPC$User tLRPC$User, boolean[] zArr2, TLRPC$Chat tLRPC$Chat, TLRPC$ChatFull tLRPC$ChatFull, Runnable runnable) {
        this.f$0 = messageObject;
        this.f$1 = groupedMessages;
        this.f$2 = tLRPC$EncryptedChat;
        this.f$3 = i;
        this.f$4 = j;
        this.f$5 = zArr;
        this.f$6 = z;
        this.f$7 = sparseArrayArr;
        this.f$8 = tLRPC$User;
        this.f$9 = zArr2;
        this.f$10 = tLRPC$Chat;
        this.f$11 = tLRPC$ChatFull;
        this.f$12 = runnable;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createDeleteMessagesAlert$93(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, dialogInterface, i);
    }
}
