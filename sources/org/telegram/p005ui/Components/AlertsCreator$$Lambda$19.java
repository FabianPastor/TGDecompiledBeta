package org.telegram.p005ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;

/* renamed from: org.telegram.ui.Components.AlertsCreator$$Lambda$19 */
final /* synthetic */ class AlertsCreator$$Lambda$19 implements OnClickListener {
    private final int[] arg$1;
    private final long arg$2;
    private final String arg$3;
    private final Builder arg$4;
    private final Runnable arg$5;

    AlertsCreator$$Lambda$19(int[] iArr, long j, String str, Builder builder, Runnable runnable) {
        this.arg$1 = iArr;
        this.arg$2 = j;
        this.arg$3 = str;
        this.arg$4 = builder;
        this.arg$5 = runnable;
    }

    public void onClick(View view) {
        AlertsCreator.lambda$createVibrationSelectDialog$20$AlertsCreator(this.arg$1, this.arg$2, this.arg$3, this.arg$4, this.arg$5, view);
    }
}
