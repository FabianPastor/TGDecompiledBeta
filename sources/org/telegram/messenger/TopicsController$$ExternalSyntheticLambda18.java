package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
/* loaded from: classes.dex */
public final /* synthetic */ class TopicsController$$ExternalSyntheticLambda18 implements RequestDelegate {
    public static final /* synthetic */ TopicsController$$ExternalSyntheticLambda18 INSTANCE = new TopicsController$$ExternalSyntheticLambda18();

    private /* synthetic */ TopicsController$$ExternalSyntheticLambda18() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        TopicsController.lambda$pinTopic$12(tLObject, tLRPC$TL_error);
    }
}
