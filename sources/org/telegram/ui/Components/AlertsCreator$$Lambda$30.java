package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Components.AlertsCreator.AccountSelectDelegate;

final /* synthetic */ class AlertsCreator$$Lambda$30 implements OnClickListener {
    private final AlertDialog[] arg$1;
    private final Runnable arg$2;
    private final AccountSelectDelegate arg$3;

    AlertsCreator$$Lambda$30(AlertDialog[] alertDialogArr, Runnable runnable, AccountSelectDelegate accountSelectDelegate) {
        this.arg$1 = alertDialogArr;
        this.arg$2 = runnable;
        this.arg$3 = accountSelectDelegate;
    }

    public void onClick(View view) {
        AlertsCreator.lambda$createAccountSelectDialog$31$AlertsCreator(this.arg$1, this.arg$2, this.arg$3, view);
    }
}
