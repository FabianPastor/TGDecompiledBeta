package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.SessionBottomSheet;

public final /* synthetic */ class SessionBottomSheet$8$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ SessionBottomSheet.AnonymousClass8 f$0;
    public final /* synthetic */ SessionBottomSheet.Callback f$1;
    public final /* synthetic */ TLRPC.TL_authorization f$2;

    public /* synthetic */ SessionBottomSheet$8$$ExternalSyntheticLambda0(SessionBottomSheet.AnonymousClass8 r1, SessionBottomSheet.Callback callback, TLRPC.TL_authorization tL_authorization) {
        this.f$0 = r1;
        this.f$1 = callback;
        this.f$2 = tL_authorization;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m3848lambda$onClick$0$orgtelegramuiSessionBottomSheet$8(this.f$1, this.f$2, dialogInterface, i);
    }
}
