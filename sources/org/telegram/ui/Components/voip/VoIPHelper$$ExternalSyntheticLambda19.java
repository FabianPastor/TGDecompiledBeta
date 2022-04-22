package org.telegram.ui.Components.voip;

import android.view.View;
import org.telegram.ui.Components.BetterRatingView;

public final /* synthetic */ class VoIPHelper$$ExternalSyntheticLambda19 implements BetterRatingView.OnRatingChangeListener {
    public final /* synthetic */ View f$0;

    public /* synthetic */ VoIPHelper$$ExternalSyntheticLambda19(View view) {
        this.f$0 = view;
    }

    public final void onRatingChanged(int i) {
        VoIPHelper.lambda$showRateAlert$14(this.f$0, i);
    }
}
