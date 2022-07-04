package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class DialogsActivity$$ExternalSyntheticLambda46 implements RequestDelegate {
    public final /* synthetic */ DialogsActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLRPC.User f$2;
    public final /* synthetic */ TLRPC.Chat f$3;
    public final /* synthetic */ long f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ TLRPC.TL_messages_checkHistoryImportPeer f$6;

    public /* synthetic */ DialogsActivity$$ExternalSyntheticLambda46(DialogsActivity dialogsActivity, AlertDialog alertDialog, TLRPC.User user, TLRPC.Chat chat, long j, boolean z, TLRPC.TL_messages_checkHistoryImportPeer tL_messages_checkHistoryImportPeer) {
        this.f$0 = dialogsActivity;
        this.f$1 = alertDialog;
        this.f$2 = user;
        this.f$3 = chat;
        this.f$4 = j;
        this.f$5 = z;
        this.f$6 = tL_messages_checkHistoryImportPeer;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3388lambda$didSelectResult$54$orgtelegramuiDialogsActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, tLObject, tL_error);
    }
}
