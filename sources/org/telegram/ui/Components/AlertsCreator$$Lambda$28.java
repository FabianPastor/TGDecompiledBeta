package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

final /* synthetic */ class AlertsCreator$$Lambda$28 implements OnClickListener {
    private final int[] arg$1;
    private final LinearLayout arg$2;

    AlertsCreator$$Lambda$28(int[] iArr, LinearLayout linearLayout) {
        this.arg$1 = iArr;
        this.arg$2 = linearLayout;
    }

    public void onClick(View view) {
        AlertsCreator.lambda$createFreeSpaceDialog$31$AlertsCreator(this.arg$1, this.arg$2, view);
    }
}
