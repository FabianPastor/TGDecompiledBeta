package org.telegram.p005ui.Components;

import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.p005ui.ActionBar.BaseFragment;

/* renamed from: org.telegram.ui.Components.AlertsCreator$$Lambda$27 */
final /* synthetic */ class AlertsCreator$$Lambda$27 implements OnClickListener {
    private final BaseFragment arg$1;
    private final DialogInterface.OnClickListener arg$2;

    AlertsCreator$$Lambda$27(BaseFragment baseFragment, DialogInterface.OnClickListener onClickListener) {
        this.arg$1 = baseFragment;
        this.arg$2 = onClickListener;
    }

    public void onClick(View view) {
        AlertsCreator.lambda$createSingleChoiceDialog$28$AlertsCreator(this.arg$1, this.arg$2, view);
    }
}
