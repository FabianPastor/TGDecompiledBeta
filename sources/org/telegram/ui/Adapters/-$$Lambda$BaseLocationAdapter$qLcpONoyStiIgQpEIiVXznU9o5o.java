package org.telegram.ui.Adapters;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$BaseLocationAdapter$qLcpONoyStiIgQpEIiVXznU9o5o implements RequestDelegate {
    private final /* synthetic */ BaseLocationAdapter f$0;

    public /* synthetic */ -$$Lambda$BaseLocationAdapter$qLcpONoyStiIgQpEIiVXznU9o5o(BaseLocationAdapter baseLocationAdapter) {
        this.f$0 = baseLocationAdapter;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$searchPlacesWithQuery$3$BaseLocationAdapter(tLObject, tL_error);
    }
}
