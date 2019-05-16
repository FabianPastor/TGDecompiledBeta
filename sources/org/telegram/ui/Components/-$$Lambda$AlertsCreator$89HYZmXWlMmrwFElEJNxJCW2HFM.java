package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Components.AlertsCreator.AccountSelectDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$89HYZmXWlMmrwFElEJNxJCW2HFM implements OnClickListener {
    private final /* synthetic */ AlertDialog[] f$0;
    private final /* synthetic */ Runnable f$1;
    private final /* synthetic */ AccountSelectDelegate f$2;

    public /* synthetic */ -$$Lambda$AlertsCreator$89HYZmXWlMmrwFElEJNxJCW2HFM(AlertDialog[] alertDialogArr, Runnable runnable, AccountSelectDelegate accountSelectDelegate) {
        this.f$0 = alertDialogArr;
        this.f$1 = runnable;
        this.f$2 = accountSelectDelegate;
    }

    public final void onClick(View view) {
        AlertsCreator.lambda$createAccountSelectDialog$39(this.f$0, this.f$1, this.f$2, view);
    }
}
