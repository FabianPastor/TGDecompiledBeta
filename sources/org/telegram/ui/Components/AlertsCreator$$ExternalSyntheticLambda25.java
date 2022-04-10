package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.Cells.CheckBoxCell;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda25 implements DialogInterface.OnClickListener {
    public final /* synthetic */ TLRPC$User f$0;
    public final /* synthetic */ AccountInstance f$1;
    public final /* synthetic */ CheckBoxCell[] f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ TLRPC$Chat f$4;
    public final /* synthetic */ TLRPC$EncryptedChat f$5;
    public final /* synthetic */ boolean f$6;
    public final /* synthetic */ MessagesStorage.IntCallback f$7;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda25(TLRPC$User tLRPC$User, AccountInstance accountInstance, CheckBoxCell[] checkBoxCellArr, long j, TLRPC$Chat tLRPC$Chat, TLRPC$EncryptedChat tLRPC$EncryptedChat, boolean z, MessagesStorage.IntCallback intCallback) {
        this.f$0 = tLRPC$User;
        this.f$1 = accountInstance;
        this.f$2 = checkBoxCellArr;
        this.f$3 = j;
        this.f$4 = tLRPC$Chat;
        this.f$5 = tLRPC$EncryptedChat;
        this.f$6 = z;
        this.f$7 = intCallback;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$showBlockReportSpamAlert$14(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, dialogInterface, i);
    }
}
