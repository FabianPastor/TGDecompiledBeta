package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.TL_messages_requestUrlAuth;
import org.telegram.ui.Cells.CheckBoxCell;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$-ci97wn33L-_XDhoSMWcH2iYMLs implements OnClickListener {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ CheckBoxCell[] f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ TL_messages_requestUrlAuth f$3;

    public /* synthetic */ -$$Lambda$ChatActivity$-ci97wn33L-_XDhoSMWcH2iYMLs(ChatActivity chatActivity, CheckBoxCell[] checkBoxCellArr, String str, TL_messages_requestUrlAuth tL_messages_requestUrlAuth) {
        this.f$0 = chatActivity;
        this.f$1 = checkBoxCellArr;
        this.f$2 = str;
        this.f$3 = tL_messages_requestUrlAuth;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$showRequestUrlAlert$90$ChatActivity(this.f$1, this.f$2, this.f$3, dialogInterface, i);
    }
}
