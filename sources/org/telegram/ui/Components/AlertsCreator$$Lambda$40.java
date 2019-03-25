package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;

final /* synthetic */ class AlertsCreator$$Lambda$40 implements OnClickListener {
    private final boolean[] arg$1;

    AlertsCreator$$Lambda$40(boolean[] zArr) {
        this.arg$1 = zArr;
    }

    public void onClick(View view) {
        AlertsCreator.lambda$createDeleteMessagesAlert$45$AlertsCreator(this.arg$1, view);
    }
}
