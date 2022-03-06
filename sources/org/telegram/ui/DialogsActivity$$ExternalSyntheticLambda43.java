package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_checkHistoryImportPeer;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class DialogsActivity$$ExternalSyntheticLambda43 implements RequestDelegate {
    public final /* synthetic */ DialogsActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLRPC$User f$2;
    public final /* synthetic */ TLRPC$Chat f$3;
    public final /* synthetic */ long f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ TLRPC$TL_messages_checkHistoryImportPeer f$6;

    public /* synthetic */ DialogsActivity$$ExternalSyntheticLambda43(DialogsActivity dialogsActivity, AlertDialog alertDialog, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, long j, boolean z, TLRPC$TL_messages_checkHistoryImportPeer tLRPC$TL_messages_checkHistoryImportPeer) {
        this.f$0 = dialogsActivity;
        this.f$1 = alertDialog;
        this.f$2 = tLRPC$User;
        this.f$3 = tLRPC$Chat;
        this.f$4 = j;
        this.f$5 = z;
        this.f$6 = tLRPC$TL_messages_checkHistoryImportPeer;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$didSelectResult$45(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, tLObject, tLRPC$TL_error);
    }
}
