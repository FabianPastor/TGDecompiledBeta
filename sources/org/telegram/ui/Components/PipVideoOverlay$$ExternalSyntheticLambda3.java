package org.telegram.ui.Components;

import android.view.View;

public final /* synthetic */ class PipVideoOverlay$$ExternalSyntheticLambda3 implements View.OnClickListener {
    public static final /* synthetic */ PipVideoOverlay$$ExternalSyntheticLambda3 INSTANCE = new PipVideoOverlay$$ExternalSyntheticLambda3();

    private /* synthetic */ PipVideoOverlay$$ExternalSyntheticLambda3() {
    }

    public final void onClick(View view) {
        PipVideoOverlay.dimissAndDestroy();
    }
}
