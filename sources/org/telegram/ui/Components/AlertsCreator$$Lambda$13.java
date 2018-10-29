package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

final /* synthetic */ class AlertsCreator$$Lambda$13 implements OnClickListener {
    private final LinearLayout arg$1;
    private final int[] arg$2;

    AlertsCreator$$Lambda$13(LinearLayout linearLayout, int[] iArr) {
        this.arg$1 = linearLayout;
        this.arg$2 = iArr;
    }

    public void onClick(View view) {
        AlertsCreator.lambda$createColorSelectDialog$14$AlertsCreator(this.arg$1, this.arg$2, view);
    }
}
