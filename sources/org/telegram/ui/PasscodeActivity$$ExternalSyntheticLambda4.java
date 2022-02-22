package org.telegram.ui;

import android.content.Context;
import android.view.View;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class PasscodeActivity$$ExternalSyntheticLambda4 implements View.OnClickListener {
    public final /* synthetic */ Context f$0;

    public /* synthetic */ PasscodeActivity$$ExternalSyntheticLambda4(Context context) {
        this.f$0 = context;
    }

    public final void onClick(View view) {
        AlertsCreator.createForgotPasscodeDialog(this.f$0).show();
    }
}
