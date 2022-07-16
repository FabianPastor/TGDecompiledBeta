package org.telegram.ui.Components;

import android.view.View;
import android.view.WindowInsets;

public final /* synthetic */ class BotWebViewSheet$$ExternalSyntheticLambda2 implements View.OnApplyWindowInsetsListener {
    public static final /* synthetic */ BotWebViewSheet$$ExternalSyntheticLambda2 INSTANCE = new BotWebViewSheet$$ExternalSyntheticLambda2();

    private /* synthetic */ BotWebViewSheet$$ExternalSyntheticLambda2() {
    }

    public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
        return view.setPadding(0, 0, 0, windowInsets.getSystemWindowInsetBottom());
    }
}
