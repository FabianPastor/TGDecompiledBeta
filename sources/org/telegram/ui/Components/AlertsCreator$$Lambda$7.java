package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;

final /* synthetic */ class AlertsCreator$$Lambda$7 implements OnClickListener {
    private final boolean[] arg$1;

    AlertsCreator$$Lambda$7(boolean[] zArr) {
        this.arg$1 = zArr;
    }

    public void onClick(View view) {
        AlertsCreator.lambda$createClearOrDeleteDialogAlert$9$AlertsCreator(this.arg$1, view);
    }
}
