package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ChatEditTypeActivity;
/* loaded from: classes3.dex */
public final /* synthetic */ class ChatEditTypeActivity$UsernamesListView$$ExternalSyntheticLambda0 implements RequestDelegate {
    public static final /* synthetic */ ChatEditTypeActivity$UsernamesListView$$ExternalSyntheticLambda0 INSTANCE = new ChatEditTypeActivity$UsernamesListView$$ExternalSyntheticLambda0();

    private /* synthetic */ ChatEditTypeActivity$UsernamesListView$$ExternalSyntheticLambda0() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ChatEditTypeActivity.UsernamesListView.lambda$sendReorder$0(tLObject, tLRPC$TL_error);
    }
}
