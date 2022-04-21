package org.telegram.ui.ActionBar;

import java.util.ArrayList;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class Theme$PatternsLoader$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ Theme.PatternsLoader f$0;
    public final /* synthetic */ ArrayList f$1;

    public /* synthetic */ Theme$PatternsLoader$$ExternalSyntheticLambda3(Theme.PatternsLoader patternsLoader, ArrayList arrayList) {
        this.f$0 = patternsLoader;
        this.f$1 = arrayList;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1290lambda$new$0$orgtelegramuiActionBarTheme$PatternsLoader(this.f$1, tLObject, tL_error);
    }
}
