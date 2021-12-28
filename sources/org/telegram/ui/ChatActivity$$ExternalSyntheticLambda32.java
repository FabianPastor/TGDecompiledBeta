package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC$TL_messages_requestUrlAuth;
import org.telegram.tgnet.TLRPC$TL_urlAuthResultRequest;
import org.telegram.ui.Cells.CheckBoxCell;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda32 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ CheckBoxCell[] f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ TLRPC$TL_messages_requestUrlAuth f$3;
    public final /* synthetic */ TLRPC$TL_urlAuthResultRequest f$4;
    public final /* synthetic */ boolean f$5;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda32(ChatActivity chatActivity, CheckBoxCell[] checkBoxCellArr, String str, TLRPC$TL_messages_requestUrlAuth tLRPC$TL_messages_requestUrlAuth, TLRPC$TL_urlAuthResultRequest tLRPC$TL_urlAuthResultRequest, boolean z) {
        this.f$0 = chatActivity;
        this.f$1 = checkBoxCellArr;
        this.f$2 = str;
        this.f$3 = tLRPC$TL_messages_requestUrlAuth;
        this.f$4 = tLRPC$TL_urlAuthResultRequest;
        this.f$5 = z;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$showRequestUrlAlert$177(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, dialogInterface, i);
    }
}
