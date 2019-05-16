package org.telegram.ui;

import android.content.Intent;
import org.telegram.ui.Components.AlertsCreator.AccountSelectDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ExternalActionActivity$7MenW-e7ZVDhaPYTW6k7H5-jjBA implements AccountSelectDelegate {
    private final /* synthetic */ ExternalActionActivity f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ Intent f$2;
    private final /* synthetic */ boolean f$3;
    private final /* synthetic */ boolean f$4;
    private final /* synthetic */ boolean f$5;

    public /* synthetic */ -$$Lambda$ExternalActionActivity$7MenW-e7ZVDhaPYTW6k7H5-jjBA(ExternalActionActivity externalActionActivity, int i, Intent intent, boolean z, boolean z2, boolean z3) {
        this.f$0 = externalActionActivity;
        this.f$1 = i;
        this.f$2 = intent;
        this.f$3 = z;
        this.f$4 = z2;
        this.f$5 = z3;
    }

    public final void didSelectAccount(int i) {
        this.f$0.lambda$handleIntent$3$ExternalActionActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, i);
    }
}
