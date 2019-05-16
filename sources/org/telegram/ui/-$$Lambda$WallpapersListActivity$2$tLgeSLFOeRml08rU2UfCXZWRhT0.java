package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.WallpapersListActivity.AnonymousClass2;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WallpapersListActivity$2$tLgeSLFOeRml08rU2UfCXZWRhT0 implements RequestDelegate {
    private final /* synthetic */ AnonymousClass2 f$0;
    private final /* synthetic */ int[] f$1;

    public /* synthetic */ -$$Lambda$WallpapersListActivity$2$tLgeSLFOeRml08rU2UfCXZWRhT0(AnonymousClass2 anonymousClass2, int[] iArr) {
        this.f$0 = anonymousClass2;
        this.f$1 = iArr;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$1$WallpapersListActivity$2(this.f$1, tLObject, tL_error);
    }
}
