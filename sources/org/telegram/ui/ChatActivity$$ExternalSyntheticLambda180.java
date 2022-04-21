package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Cells.CheckBoxCell;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda180 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ CheckBoxCell[] f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ TLRPC.TL_messages_requestUrlAuth f$3;
    public final /* synthetic */ TLRPC.TL_urlAuthResultRequest f$4;
    public final /* synthetic */ boolean f$5;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda180(ChatActivity chatActivity, CheckBoxCell[] checkBoxCellArr, String str, TLRPC.TL_messages_requestUrlAuth tL_messages_requestUrlAuth, TLRPC.TL_urlAuthResultRequest tL_urlAuthResultRequest, boolean z) {
        this.f$0 = chatActivity;
        this.f$1 = checkBoxCellArr;
        this.f$2 = str;
        this.f$3 = tL_messages_requestUrlAuth;
        this.f$4 = tL_urlAuthResultRequest;
        this.f$5 = z;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m1828lambda$showRequestUrlAlert$221$orgtelegramuiChatActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, dialogInterface, i);
    }
}
