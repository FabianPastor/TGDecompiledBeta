package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$9i3XB_1s2n8aR5VYe0IfcGoMi5U implements OnClickListener {
    private final /* synthetic */ long f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ BaseFragment f$2;
    private final /* synthetic */ Context f$3;

    public /* synthetic */ -$$Lambda$AlertsCreator$9i3XB_1s2n8aR5VYe0IfcGoMi5U(long j, int i, BaseFragment baseFragment, Context context) {
        this.f$0 = j;
        this.f$1 = i;
        this.f$2 = baseFragment;
        this.f$3 = context;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createReportAlert$23(this.f$0, this.f$1, this.f$2, this.f$3, dialogInterface, i);
    }
}