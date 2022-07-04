package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.Switch;

public final /* synthetic */ class vibroslider$$ExternalSyntheticLambda1 implements View.OnClickListener {
    public final /* synthetic */ Switch f$0;

    public /* synthetic */ vibroslider$$ExternalSyntheticLambda1(Switch switchR) {
        this.f$0 = switchR;
    }

    public final void onClick(View view) {
        this.f$0.setChecked(!this.f$0.isChecked(), true);
    }
}
