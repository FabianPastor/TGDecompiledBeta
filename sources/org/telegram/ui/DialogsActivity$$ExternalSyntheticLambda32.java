package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class DialogsActivity$$ExternalSyntheticLambda32 implements Runnable {
    public final /* synthetic */ DialogsActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ TLRPC.User f$3;
    public final /* synthetic */ TLRPC.Chat f$4;
    public final /* synthetic */ long f$5;
    public final /* synthetic */ boolean f$6;
    public final /* synthetic */ TLRPC.TL_error f$7;
    public final /* synthetic */ TLRPC.TL_messages_checkHistoryImportPeer f$8;

    public /* synthetic */ DialogsActivity$$ExternalSyntheticLambda32(DialogsActivity dialogsActivity, AlertDialog alertDialog, TLObject tLObject, TLRPC.User user, TLRPC.Chat chat, long j, boolean z, TLRPC.TL_error tL_error, TLRPC.TL_messages_checkHistoryImportPeer tL_messages_checkHistoryImportPeer) {
        this.f$0 = dialogsActivity;
        this.f$1 = alertDialog;
        this.f$2 = tLObject;
        this.f$3 = user;
        this.f$4 = chat;
        this.f$5 = j;
        this.f$6 = z;
        this.f$7 = tL_error;
        this.f$8 = tL_messages_checkHistoryImportPeer;
    }

    public final void run() {
        this.f$0.m2084lambda$didSelectResult$46$orgtelegramuiDialogsActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}
