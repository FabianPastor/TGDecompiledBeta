package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import java.util.ArrayList;
import org.telegram.ui.ActionBar.AlertDialog.Builder;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$OnXNDjADtC_bD3GK3ao8UUcfhIA implements OnClickListener {
    private final /* synthetic */ ArrayList f$0;
    private final /* synthetic */ Runnable f$1;
    private final /* synthetic */ Builder f$2;

    public /* synthetic */ -$$Lambda$AlertsCreator$OnXNDjADtC_bD3GK3ao8UUcfhIA(ArrayList arrayList, Runnable runnable, Builder builder) {
        this.f$0 = arrayList;
        this.f$1 = runnable;
        this.f$2 = builder;
    }

    public final void onClick(View view) {
        AlertsCreator.lambda$showSecretLocationAlert$6(this.f$0, this.f$1, this.f$2, view);
    }
}
