package org.telegram.ui.Components;

import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.ActionBar.AlertDialog.Builder;

final /* synthetic */ class AlertsCreator$$Lambda$31 implements OnClickListener {
    private final int[] arg$1;
    private final long arg$2;
    private final int arg$3;
    private final SharedPreferences arg$4;
    private final Builder arg$5;
    private final Runnable arg$6;

    AlertsCreator$$Lambda$31(int[] iArr, long j, int i, SharedPreferences sharedPreferences, Builder builder, Runnable runnable) {
        this.arg$1 = iArr;
        this.arg$2 = j;
        this.arg$3 = i;
        this.arg$4 = sharedPreferences;
        this.arg$5 = builder;
        this.arg$6 = runnable;
    }

    public void onClick(View view) {
        AlertsCreator.lambda$createPrioritySelectDialog$34$AlertsCreator(this.arg$1, this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, view);
    }
}
