package org.telegram.ui;

import android.content.Intent;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class ExternalActionActivity$$ExternalSyntheticLambda9 implements AlertsCreator.AccountSelectDelegate {
    public final /* synthetic */ ExternalActionActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ Intent f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ boolean f$5;

    public /* synthetic */ ExternalActionActivity$$ExternalSyntheticLambda9(ExternalActionActivity externalActionActivity, int i, Intent intent, boolean z, boolean z2, boolean z3) {
        this.f$0 = externalActionActivity;
        this.f$1 = i;
        this.f$2 = intent;
        this.f$3 = z;
        this.f$4 = z2;
        this.f$5 = z3;
    }

    public final void didSelectAccount(int i) {
        this.f$0.lambda$handleIntent$3(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, i);
    }
}
