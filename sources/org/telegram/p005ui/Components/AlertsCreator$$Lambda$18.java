package org.telegram.p005ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

/* renamed from: org.telegram.ui.Components.AlertsCreator$$Lambda$18 */
final /* synthetic */ class AlertsCreator$$Lambda$18 implements OnClickListener {
    private final int[] arg$1;
    private final LinearLayout arg$2;

    AlertsCreator$$Lambda$18(int[] iArr, LinearLayout linearLayout) {
        this.arg$1 = iArr;
        this.arg$2 = linearLayout;
    }

    public void onClick(View view) {
        AlertsCreator.lambda$createLocationUpdateDialog$19$AlertsCreator(this.arg$1, this.arg$2, view);
    }
}
