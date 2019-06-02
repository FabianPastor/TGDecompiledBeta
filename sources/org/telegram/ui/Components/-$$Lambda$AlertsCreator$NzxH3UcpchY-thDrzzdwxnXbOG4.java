package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.ActionBar.AlertDialog.Builder;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$NzxH3UcpchY-thDrzzdwxnXbOG4 implements OnClickListener {
    private final /* synthetic */ Builder f$0;
    private final /* synthetic */ DialogInterface.OnClickListener f$1;

    public /* synthetic */ -$$Lambda$AlertsCreator$NzxH3UcpchY-thDrzzdwxnXbOG4(Builder builder, DialogInterface.OnClickListener onClickListener) {
        this.f$0 = builder;
        this.f$1 = onClickListener;
    }

    public final void onClick(View view) {
        AlertsCreator.lambda$createSingleChoiceDialog$37(this.f$0, this.f$1, view);
    }
}
