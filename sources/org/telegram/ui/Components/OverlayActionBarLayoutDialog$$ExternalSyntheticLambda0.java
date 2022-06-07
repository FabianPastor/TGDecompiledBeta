package org.telegram.ui.Components;

import android.view.View;
import android.view.WindowInsets;

public final /* synthetic */ class OverlayActionBarLayoutDialog$$ExternalSyntheticLambda0 implements View.OnApplyWindowInsetsListener {
    public static final /* synthetic */ OverlayActionBarLayoutDialog$$ExternalSyntheticLambda0 INSTANCE = new OverlayActionBarLayoutDialog$$ExternalSyntheticLambda0();

    private /* synthetic */ OverlayActionBarLayoutDialog$$ExternalSyntheticLambda0() {
    }

    public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
        return view.setPadding(0, 0, 0, windowInsets.getSystemWindowInsetBottom());
    }
}
