package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_checkHistoryImportPeer;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class DialogsActivity$$ExternalSyntheticLambda46 implements Runnable {
    public final /* synthetic */ DialogsActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ TLRPC$User f$3;
    public final /* synthetic */ TLRPC$Chat f$4;
    public final /* synthetic */ long f$5;
    public final /* synthetic */ boolean f$6;
    public final /* synthetic */ TLRPC$TL_error f$7;
    public final /* synthetic */ TLRPC$TL_messages_checkHistoryImportPeer f$8;

    public /* synthetic */ DialogsActivity$$ExternalSyntheticLambda46(DialogsActivity dialogsActivity, AlertDialog alertDialog, TLObject tLObject, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, long j, boolean z, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_messages_checkHistoryImportPeer tLRPC$TL_messages_checkHistoryImportPeer) {
        this.f$0 = dialogsActivity;
        this.f$1 = alertDialog;
        this.f$2 = tLObject;
        this.f$3 = tLRPC$User;
        this.f$4 = tLRPC$Chat;
        this.f$5 = j;
        this.f$6 = z;
        this.f$7 = tLRPC$TL_error;
        this.f$8 = tLRPC$TL_messages_checkHistoryImportPeer;
    }

    public final void run() {
        this.f$0.lambda$didSelectResult$55(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}
