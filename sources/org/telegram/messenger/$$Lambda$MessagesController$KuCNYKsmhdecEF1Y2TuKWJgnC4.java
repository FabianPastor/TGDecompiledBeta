package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$KuCNYKsmh-decEF1Y2TuKWJgnC4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$KuCNYKsmhdecEF1Y2TuKWJgnC4 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$KuCNYKsmhdecEF1Y2TuKWJgnC4 INSTANCE = new $$Lambda$MessagesController$KuCNYKsmhdecEF1Y2TuKWJgnC4();

    private /* synthetic */ $$Lambda$MessagesController$KuCNYKsmhdecEF1Y2TuKWJgnC4() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMentionMessageAsRead$180(tLObject, tLRPC$TL_error);
    }
}
