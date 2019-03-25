package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.ActionBar.AlertDialog.Builder;

final /* synthetic */ class AlertsCreator$$Lambda$33 implements OnClickListener {
    private final Builder arg$1;
    private final DialogInterface.OnClickListener arg$2;

    AlertsCreator$$Lambda$33(Builder builder, DialogInterface.OnClickListener onClickListener) {
        this.arg$1 = builder;
        this.arg$2 = onClickListener;
    }

    public void onClick(View view) {
        AlertsCreator.lambda$createSingleChoiceDialog$36$AlertsCreator(this.arg$1, this.arg$2, view);
    }
}
