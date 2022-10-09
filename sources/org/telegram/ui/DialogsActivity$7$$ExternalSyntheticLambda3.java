package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.DialogsActivity;
/* loaded from: classes3.dex */
public final /* synthetic */ class DialogsActivity$7$$ExternalSyntheticLambda3 implements RequestDelegate {
    public static final /* synthetic */ DialogsActivity$7$$ExternalSyntheticLambda3 INSTANCE = new DialogsActivity$7$$ExternalSyntheticLambda3();

    private /* synthetic */ DialogsActivity$7$$ExternalSyntheticLambda3() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        DialogsActivity.AnonymousClass7.lambda$showDeleteAlert$1(tLObject, tLRPC$TL_error);
    }
}
