package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.TL_messages_requestUrlAuth;
import org.telegram.tgnet.TLRPC.TL_urlAuthResultRequest;
import org.telegram.ui.Cells.CheckBoxCell;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$xvfKlqAN5zLCNcXFvy1IsBVDaBI implements OnClickListener {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ CheckBoxCell[] f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ TL_messages_requestUrlAuth f$3;
    private final /* synthetic */ TL_urlAuthResultRequest f$4;

    public /* synthetic */ -$$Lambda$ChatActivity$xvfKlqAN5zLCNcXFvy1IsBVDaBI(ChatActivity chatActivity, CheckBoxCell[] checkBoxCellArr, String str, TL_messages_requestUrlAuth tL_messages_requestUrlAuth, TL_urlAuthResultRequest tL_urlAuthResultRequest) {
        this.f$0 = chatActivity;
        this.f$1 = checkBoxCellArr;
        this.f$2 = str;
        this.f$3 = tL_messages_requestUrlAuth;
        this.f$4 = tL_urlAuthResultRequest;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$showRequestUrlAlert$103$ChatActivity(this.f$1, this.f$2, this.f$3, this.f$4, dialogInterface, i);
    }
}
