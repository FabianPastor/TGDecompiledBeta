package org.telegram.ui;

import android.view.View;
import org.telegram.ui.ChooseSpeedLayout;

public final /* synthetic */ class ChooseSpeedLayout$$ExternalSyntheticLambda4 implements View.OnClickListener {
    public final /* synthetic */ ChooseSpeedLayout.Callback f$0;

    public /* synthetic */ ChooseSpeedLayout$$ExternalSyntheticLambda4(ChooseSpeedLayout.Callback callback) {
        this.f$0 = callback;
    }

    public final void onClick(View view) {
        this.f$0.onSpeedSelected(2.0f);
    }
}
