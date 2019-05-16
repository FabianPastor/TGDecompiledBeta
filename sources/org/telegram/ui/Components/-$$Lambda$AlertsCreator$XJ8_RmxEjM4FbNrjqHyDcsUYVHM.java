package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$XJ8_RmxEjM4FbNrjqHyDcsUYVHM implements OnClickListener {
    private final /* synthetic */ LinearLayout f$0;
    private final /* synthetic */ int[] f$1;

    public /* synthetic */ -$$Lambda$AlertsCreator$XJ8_RmxEjM4FbNrjqHyDcsUYVHM(LinearLayout linearLayout, int[] iArr) {
        this.f$0 = linearLayout;
        this.f$1 = iArr;
    }

    public final void onClick(View view) {
        AlertsCreator.lambda$createColorSelectDialog$22(this.f$0, this.f$1, view);
    }
}
