package org.telegram.ui.Components;

import java.util.List;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ReactedHeaderView$$ExternalSyntheticLambda7 implements RequestDelegate {
    public final /* synthetic */ ReactedHeaderView f$0;
    public final /* synthetic */ List f$1;
    public final /* synthetic */ List f$2;
    public final /* synthetic */ Runnable f$3;

    public /* synthetic */ ReactedHeaderView$$ExternalSyntheticLambda7(ReactedHeaderView reactedHeaderView, List list, List list2, Runnable runnable) {
        this.f$0 = reactedHeaderView;
        this.f$1 = list;
        this.f$2 = list2;
        this.f$3 = runnable;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$onAttachedToWindow$2(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
    }
}
