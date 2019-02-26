package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.ActionBar.BaseFragment;

final /* synthetic */ class AlertsCreator$$Lambda$5 implements OnClickListener {
    private final BaseFragment arg$1;

    AlertsCreator$$Lambda$5(BaseFragment baseFragment) {
        this.arg$1 = baseFragment;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.performAskAQuestion(this.arg$1);
    }
}
