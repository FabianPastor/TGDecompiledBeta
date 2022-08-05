package org.telegram.ui.ActionBar;

import java.util.HashMap;
import org.telegram.messenger.Utilities;

public final /* synthetic */ class Theme$$ExternalSyntheticLambda12 implements Utilities.Callback {
    public final /* synthetic */ Runnable f$0;

    public /* synthetic */ Theme$$ExternalSyntheticLambda12(Runnable runnable) {
        this.f$0 = runnable;
    }

    public final void run(Object obj) {
        Theme.lambda$applyThemeInBackground$4(this.f$0, (HashMap) obj);
    }
}
