package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_editMessage;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$o7IqOMfZbb_OvPyuKGDV1FCihUQ implements RequestDelegate {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ AlertDialog[] f$1;
    private final /* synthetic */ TL_messages_editMessage f$2;

    public /* synthetic */ -$$Lambda$ChatActivity$o7IqOMfZbb_OvPyuKGDV1FCihUQ(ChatActivity chatActivity, AlertDialog[] alertDialogArr, TL_messages_editMessage tL_messages_editMessage) {
        this.f$0 = chatActivity;
        this.f$1 = alertDialogArr;
        this.f$2 = tL_messages_editMessage;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$92$ChatActivity(this.f$1, this.f$2, tLObject, tL_error);
    }
}
