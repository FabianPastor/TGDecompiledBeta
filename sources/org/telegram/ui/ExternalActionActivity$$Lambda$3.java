package org.telegram.ui;

import android.content.Intent;
import org.telegram.ui.Components.AlertsCreator.AccountSelectDelegate;

final /* synthetic */ class ExternalActionActivity$$Lambda$3 implements AccountSelectDelegate {
    private final ExternalActionActivity arg$1;
    private final int arg$2;
    private final Intent arg$3;
    private final boolean arg$4;
    private final boolean arg$5;
    private final boolean arg$6;

    ExternalActionActivity$$Lambda$3(ExternalActionActivity externalActionActivity, int i, Intent intent, boolean z, boolean z2, boolean z3) {
        this.arg$1 = externalActionActivity;
        this.arg$2 = i;
        this.arg$3 = intent;
        this.arg$4 = z;
        this.arg$5 = z2;
        this.arg$6 = z3;
    }

    public void didSelectAccount(int i) {
        this.arg$1.lambda$handleIntent$3$ExternalActionActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, i);
    }
}
