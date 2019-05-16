package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$KOZG0pVJG6nMdk1njo_1lCbwC_Q implements OnClickListener {
    private final /* synthetic */ BaseFragment f$0;

    public /* synthetic */ -$$Lambda$AlertsCreator$KOZG0pVJG6nMdk1njo_1lCbwC_Q(BaseFragment baseFragment) {
        this.f$0 = baseFragment;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.performAskAQuestion(this.f$0);
    }
}
