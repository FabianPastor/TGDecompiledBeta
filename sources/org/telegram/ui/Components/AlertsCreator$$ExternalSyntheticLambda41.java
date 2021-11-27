package org.telegram.ui.Components;

import android.view.View;
import java.util.ArrayList;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda41 implements View.OnClickListener {
    public final /* synthetic */ ArrayList f$0;
    public final /* synthetic */ Runnable f$1;
    public final /* synthetic */ AlertDialog.Builder f$2;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda41(ArrayList arrayList, Runnable runnable, AlertDialog.Builder builder) {
        this.f$0 = arrayList;
        this.f$1 = runnable;
        this.f$2 = builder;
    }

    public final void onClick(View view) {
        AlertsCreator.lambda$showSecretLocationAlert$9(this.f$0, this.f$1, this.f$2, view);
    }
}
