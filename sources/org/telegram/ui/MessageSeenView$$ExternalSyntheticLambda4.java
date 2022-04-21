package org.telegram.ui;

import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessageSeenView$$ExternalSyntheticLambda4 implements RequestDelegate {
    public final /* synthetic */ MessageSeenView f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ HashMap f$2;
    public final /* synthetic */ ArrayList f$3;

    public /* synthetic */ MessageSeenView$$ExternalSyntheticLambda4(MessageSeenView messageSeenView, int i, HashMap hashMap, ArrayList arrayList) {
        this.f$0 = messageSeenView;
        this.f$1 = i;
        this.f$2 = hashMap;
        this.f$3 = arrayList;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2619lambda$new$3$orgtelegramuiMessageSeenView(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
