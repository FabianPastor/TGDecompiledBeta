package org.telegram.ui.Components;

import android.view.View;
import android.view.WindowInsets;

public final /* synthetic */ class BotWebViewSheet$$ExternalSyntheticLambda1 implements View.OnApplyWindowInsetsListener {
    public static final /* synthetic */ BotWebViewSheet$$ExternalSyntheticLambda1 INSTANCE = new BotWebViewSheet$$ExternalSyntheticLambda1();

    private /* synthetic */ BotWebViewSheet$$ExternalSyntheticLambda1() {
    }

    public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
        return view.setPadding(0, 0, 0, windowInsets.getSystemWindowInsetBottom());
    }
}
