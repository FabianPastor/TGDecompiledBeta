package org.telegram.ui.Components;

import android.view.View;
/* loaded from: classes3.dex */
public final /* synthetic */ class PipVideoOverlay$$ExternalSyntheticLambda3 implements View.OnClickListener {
    public static final /* synthetic */ PipVideoOverlay$$ExternalSyntheticLambda3 INSTANCE = new PipVideoOverlay$$ExternalSyntheticLambda3();

    private /* synthetic */ PipVideoOverlay$$ExternalSyntheticLambda3() {
    }

    @Override // android.view.View.OnClickListener
    public final void onClick(View view) {
        PipVideoOverlay.dimissAndDestroy();
    }
}
