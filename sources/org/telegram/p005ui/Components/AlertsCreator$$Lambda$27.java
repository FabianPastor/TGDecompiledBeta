package org.telegram.p005ui.Components;

import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;

/* renamed from: org.telegram.ui.Components.AlertsCreator$$Lambda$27 */
final /* synthetic */ class AlertsCreator$$Lambda$27 implements OnClickListener {
    private final int[] arg$1;
    private final long arg$2;
    private final int arg$3;
    private final SharedPreferences arg$4;
    private final Builder arg$5;
    private final Runnable arg$6;

    AlertsCreator$$Lambda$27(int[] iArr, long j, int i, SharedPreferences sharedPreferences, Builder builder, Runnable runnable) {
        this.arg$1 = iArr;
        this.arg$2 = j;
        this.arg$3 = i;
        this.arg$4 = sharedPreferences;
        this.arg$5 = builder;
        this.arg$6 = runnable;
    }

    public void onClick(View view) {
        AlertsCreator.lambda$createPrioritySelectDialog$28$AlertsCreator(this.arg$1, this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, view);
    }
}
