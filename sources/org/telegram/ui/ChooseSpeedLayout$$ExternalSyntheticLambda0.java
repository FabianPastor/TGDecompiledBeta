package org.telegram.ui;

import android.view.View;
import org.telegram.ui.ChooseSpeedLayout;

public final /* synthetic */ class ChooseSpeedLayout$$ExternalSyntheticLambda0 implements View.OnClickListener {
    public final /* synthetic */ ChooseSpeedLayout.Callback f$0;

    public /* synthetic */ ChooseSpeedLayout$$ExternalSyntheticLambda0(ChooseSpeedLayout.Callback callback) {
        this.f$0 = callback;
    }

    public final void onClick(View view) {
        this.f$0.onSpeedSelected(0.5f);
    }
}