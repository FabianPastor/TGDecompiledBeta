package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_getWebPagePreview;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$sj4J8GZ5uOmopCpGberTaaO96RM implements RequestDelegate {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ TL_messages_getWebPagePreview f$1;

    public /* synthetic */ -$$Lambda$ChatActivity$sj4J8GZ5uOmopCpGberTaaO96RM(ChatActivity chatActivity, TL_messages_getWebPagePreview tL_messages_getWebPagePreview) {
        this.f$0 = chatActivity;
        this.f$1 = tL_messages_getWebPagePreview;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$58$ChatActivity(this.f$1, tLObject, tL_error);
    }
}