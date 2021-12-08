package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.util.SparseArray;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda11 implements DialogInterface.OnClickListener {
    public final /* synthetic */ MessageObject f$0;
    public final /* synthetic */ MessageObject.GroupedMessages f$1;
    public final /* synthetic */ boolean[] f$10;
    public final /* synthetic */ TLRPC.Chat f$11;
    public final /* synthetic */ TLRPC.ChatFull f$12;
    public final /* synthetic */ Runnable f$13;
    public final /* synthetic */ TLRPC.EncryptedChat f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ long f$4;
    public final /* synthetic */ boolean[] f$5;
    public final /* synthetic */ boolean f$6;
    public final /* synthetic */ SparseArray[] f$7;
    public final /* synthetic */ TLRPC.User f$8;
    public final /* synthetic */ TLRPC.Chat f$9;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda11(MessageObject messageObject, MessageObject.GroupedMessages groupedMessages, TLRPC.EncryptedChat encryptedChat, int i, long j, boolean[] zArr, boolean z, SparseArray[] sparseArrayArr, TLRPC.User user, TLRPC.Chat chat, boolean[] zArr2, TLRPC.Chat chat2, TLRPC.ChatFull chatFull, Runnable runnable) {
        this.f$0 = messageObject;
        this.f$1 = groupedMessages;
        this.f$2 = encryptedChat;
        this.f$3 = i;
        this.f$4 = j;
        this.f$5 = zArr;
        this.f$6 = z;
        this.f$7 = sparseArrayArr;
        this.f$8 = user;
        this.f$9 = chat;
        this.f$10 = zArr2;
        this.f$11 = chat2;
        this.f$12 = chatFull;
        this.f$13 = runnable;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createDeleteMessagesAlert$96(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, dialogInterface, i);
    }
}
