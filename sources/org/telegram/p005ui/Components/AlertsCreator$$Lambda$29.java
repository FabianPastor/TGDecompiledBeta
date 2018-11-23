package org.telegram.p005ui.Components;

import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;

/* renamed from: org.telegram.ui.Components.AlertsCreator$$Lambda$29 */
final /* synthetic */ class AlertsCreator$$Lambda$29 implements OnClickListener {
    private final Builder arg$1;
    private final DialogInterface.OnClickListener arg$2;

    AlertsCreator$$Lambda$29(Builder builder, DialogInterface.OnClickListener onClickListener) {
        this.arg$1 = builder;
        this.arg$2 = onClickListener;
    }

    public void onClick(View view) {
        AlertsCreator.lambda$createSingleChoiceDialog$30$AlertsCreator(this.arg$1, this.arg$2, view);
    }
}
