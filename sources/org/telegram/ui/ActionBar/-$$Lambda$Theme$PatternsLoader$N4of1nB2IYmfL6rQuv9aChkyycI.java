package org.telegram.ui.ActionBar;

import java.util.ArrayList;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.Theme.PatternsLoader;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$Theme$PatternsLoader$N4of1nB2IYmfL6rQuv9aChkyycI implements RequestDelegate {
    private final /* synthetic */ PatternsLoader f$0;
    private final /* synthetic */ ArrayList f$1;

    public /* synthetic */ -$$Lambda$Theme$PatternsLoader$N4of1nB2IYmfL6rQuv9aChkyycI(PatternsLoader patternsLoader, ArrayList arrayList) {
        this.f$0 = patternsLoader;
        this.f$1 = arrayList;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$0$Theme$PatternsLoader(this.f$1, tLObject, tL_error);
    }
}
