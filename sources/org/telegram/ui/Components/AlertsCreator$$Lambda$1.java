package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.ui.ActionBar.BaseFragment;

final /* synthetic */ class AlertsCreator$$Lambda$1 implements OnClickListener {
    private final int arg$1;
    private final long arg$2;
    private final BaseFragment arg$3;
    private final IntCallback arg$4;

    AlertsCreator$$Lambda$1(int i, long j, BaseFragment baseFragment, IntCallback intCallback) {
        this.arg$1 = i;
        this.arg$2 = j;
        this.arg$3 = baseFragment;
        this.arg$4 = intCallback;
    }

    public void onClick(View view) {
        AlertsCreator.lambda$showCustomNotificationsDialog$1$AlertsCreator(this.arg$1, this.arg$2, this.arg$3, this.arg$4, view);
    }
}
