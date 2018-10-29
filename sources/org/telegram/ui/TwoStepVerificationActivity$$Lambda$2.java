package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class TwoStepVerificationActivity$$Lambda$2 implements OnItemClickListener {
    private final TwoStepVerificationActivity arg$1;

    TwoStepVerificationActivity$$Lambda$2(TwoStepVerificationActivity twoStepVerificationActivity) {
        this.arg$1 = twoStepVerificationActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$8$TwoStepVerificationActivity(view, i);
    }
}
