package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.ActionBar.AlertDialog.Builder;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$LWtnoVn16WLVqoU3zq1q2hohPjg implements OnClickListener {
    private final /* synthetic */ Builder f$0;
    private final /* synthetic */ DialogInterface.OnClickListener f$1;

    public /* synthetic */ -$$Lambda$AlertsCreator$LWtnoVn16WLVqoU3zq1q2hohPjg(Builder builder, DialogInterface.OnClickListener onClickListener) {
        this.f$0 = builder;
        this.f$1 = onClickListener;
    }

    public final void onClick(View view) {
        AlertsCreator.lambda$createSingleChoiceDialog$48(this.f$0, this.f$1, view);
    }
}
