package org.telegram.ui.Adapters;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$BaseLocationAdapter$OUu2tRVUfz8Mg0cIvwT-Hlk9dBk implements RequestDelegate {
    private final /* synthetic */ BaseLocationAdapter f$0;

    public /* synthetic */ -$$Lambda$BaseLocationAdapter$OUu2tRVUfz8Mg0cIvwT-Hlk9dBk(BaseLocationAdapter baseLocationAdapter) {
        this.f$0 = baseLocationAdapter;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$searchBotUser$1$BaseLocationAdapter(tLObject, tL_error);
    }
}
