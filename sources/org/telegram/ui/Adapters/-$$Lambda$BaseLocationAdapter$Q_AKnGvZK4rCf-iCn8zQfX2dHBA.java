package org.telegram.ui.Adapters;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$BaseLocationAdapter$Q_AKnGvZK4rCf-iCn8zQfX2dHBA implements RequestDelegate {
    private final /* synthetic */ BaseLocationAdapter f$0;
    private final /* synthetic */ String f$1;

    public /* synthetic */ -$$Lambda$BaseLocationAdapter$Q_AKnGvZK4rCf-iCn8zQfX2dHBA(BaseLocationAdapter baseLocationAdapter, String str) {
        this.f$0 = baseLocationAdapter;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$searchPlacesWithQuery$5$BaseLocationAdapter(this.f$1, tLObject, tL_error);
    }
}
