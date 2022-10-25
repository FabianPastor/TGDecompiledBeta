package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.TopicCreateFragment;
/* loaded from: classes3.dex */
public final /* synthetic */ class TopicCreateFragment$1$$ExternalSyntheticLambda2 implements RequestDelegate {
    public static final /* synthetic */ TopicCreateFragment$1$$ExternalSyntheticLambda2 INSTANCE = new TopicCreateFragment$1$$ExternalSyntheticLambda2();

    private /* synthetic */ TopicCreateFragment$1$$ExternalSyntheticLambda2() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        TopicCreateFragment.AnonymousClass1.lambda$onItemClick$2(tLObject, tLRPC$TL_error);
    }
}
