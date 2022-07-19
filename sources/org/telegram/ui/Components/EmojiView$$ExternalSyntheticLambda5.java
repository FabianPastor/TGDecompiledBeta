package org.telegram.ui.Components;

import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.StickersActivity;

public final /* synthetic */ class EmojiView$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ BaseFragment f$0;

    public /* synthetic */ EmojiView$$ExternalSyntheticLambda5(BaseFragment baseFragment) {
        this.f$0 = baseFragment;
    }

    public final void run() {
        this.f$0.presentFragment(new StickersActivity(5));
    }
}
