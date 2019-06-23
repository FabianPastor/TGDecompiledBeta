package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_getWebPagePreview;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$I1XtQFQJnTJgtZS3VsAS9var_ZpU implements Runnable {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TLObject f$2;
    private final /* synthetic */ TL_messages_getWebPagePreview f$3;

    public /* synthetic */ -$$Lambda$ChatActivity$I1XtQFQJnTJgtZS3VsAS9var_ZpU(ChatActivity chatActivity, TL_error tL_error, TLObject tLObject, TL_messages_getWebPagePreview tL_messages_getWebPagePreview) {
        this.f$0 = chatActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = tL_messages_getWebPagePreview;
    }

    public final void run() {
        this.f$0.lambda$null$51$ChatActivity(this.f$1, this.f$2, this.f$3);
    }
}
