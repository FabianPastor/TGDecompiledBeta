package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;

final /* synthetic */ class AlertsCreator$$Lambda$41 implements OnClickListener {
    private final boolean[] arg$1;

    AlertsCreator$$Lambda$41(boolean[] zArr) {
        this.arg$1 = zArr;
    }

    public void onClick(View view) {
        AlertsCreator.lambda$createDeleteMessagesAlert$46$AlertsCreator(this.arg$1, view);
    }
}
