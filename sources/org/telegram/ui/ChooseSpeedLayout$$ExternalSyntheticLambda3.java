package org.telegram.ui;

import android.view.View;
import org.telegram.ui.ChooseSpeedLayout;

public final /* synthetic */ class ChooseSpeedLayout$$ExternalSyntheticLambda3 implements View.OnClickListener {
    public final /* synthetic */ ChooseSpeedLayout.Callback f$0;

    public /* synthetic */ ChooseSpeedLayout$$ExternalSyntheticLambda3(ChooseSpeedLayout.Callback callback) {
        this.f$0 = callback;
    }

    public final void onClick(View view) {
        this.f$0.onSpeedSelected(1.0f);
    }
}