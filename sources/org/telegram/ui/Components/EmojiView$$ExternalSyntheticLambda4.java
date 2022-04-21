package org.telegram.ui.Components;

import android.view.MotionEvent;
import android.view.View;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class EmojiView$$ExternalSyntheticLambda4 implements View.OnTouchListener {
    public final /* synthetic */ EmojiView f$0;
    public final /* synthetic */ Theme.ResourcesProvider f$1;

    public /* synthetic */ EmojiView$$ExternalSyntheticLambda4(EmojiView emojiView, Theme.ResourcesProvider resourcesProvider) {
        this.f$0 = emojiView;
        this.f$1 = resourcesProvider;
    }

    public final boolean onTouch(View view, MotionEvent motionEvent) {
        return this.f$0.m3965lambda$new$4$orgtelegramuiComponentsEmojiView(this.f$1, view, motionEvent);
    }
}
