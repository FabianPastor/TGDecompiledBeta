package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC$TL_authorization;
import org.telegram.ui.SessionBottomSheet;

public final /* synthetic */ class SessionBottomSheet$8$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ SessionBottomSheet.AnonymousClass8 f$0;
    public final /* synthetic */ SessionBottomSheet.Callback f$1;
    public final /* synthetic */ TLRPC$TL_authorization f$2;

    public /* synthetic */ SessionBottomSheet$8$$ExternalSyntheticLambda0(SessionBottomSheet.AnonymousClass8 r1, SessionBottomSheet.Callback callback, TLRPC$TL_authorization tLRPC$TL_authorization) {
        this.f$0 = r1;
        this.f$1 = callback;
        this.f$2 = tLRPC$TL_authorization;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$onClick$0(this.f$1, this.f$2, dialogInterface, i);
    }
}
