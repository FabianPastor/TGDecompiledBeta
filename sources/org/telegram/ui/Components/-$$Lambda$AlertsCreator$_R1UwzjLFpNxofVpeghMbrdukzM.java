package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Components.AlertsCreator.AccountSelectDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$_R1UwzjLFpNxofVpeghMbrdukzM implements OnClickListener {
    private final /* synthetic */ AlertDialog[] f$0;
    private final /* synthetic */ Runnable f$1;
    private final /* synthetic */ AccountSelectDelegate f$2;

    public /* synthetic */ -$$Lambda$AlertsCreator$_R1UwzjLFpNxofVpeghMbrdukzM(AlertDialog[] alertDialogArr, Runnable runnable, AccountSelectDelegate accountSelectDelegate) {
        this.f$0 = alertDialogArr;
        this.f$1 = runnable;
        this.f$2 = accountSelectDelegate;
    }

    public final void onClick(View view) {
        AlertsCreator.lambda$createAccountSelectDialog$42(this.f$0, this.f$1, this.f$2, view);
    }
}