package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.MediaActivity.MediaSearchAdapter;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaActivity$MediaSearchAdapter$TrL5Kc9dMCjvGfdjifj6CuBCggQ implements RequestDelegate {
    private final /* synthetic */ MediaSearchAdapter f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ int f$2;

    public /* synthetic */ -$$Lambda$MediaActivity$MediaSearchAdapter$TrL5Kc9dMCjvGfdjifj6CuBCggQ(MediaSearchAdapter mediaSearchAdapter, int i, int i2) {
        this.f$0 = mediaSearchAdapter;
        this.f$1 = i;
        this.f$2 = i2;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$queryServerSearch$1$MediaActivity$MediaSearchAdapter(this.f$1, this.f$2, tLObject, tL_error);
    }
}
