package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.ImageView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Bulletin;
@SuppressLint({"ViewConstructor"})
/* loaded from: classes3.dex */
public class ReorderingBulletinLayout extends Bulletin.SimpleLayout {
    private final ReorderingHintDrawable hintDrawable;

    public ReorderingBulletinLayout(Context context, String str, Theme.ResourcesProvider resourcesProvider) {
        super(context, resourcesProvider);
        this.textView.setText(str);
        this.textView.setTranslationY(-1.0f);
        ImageView imageView = this.imageView;
        ReorderingHintDrawable reorderingHintDrawable = new ReorderingHintDrawable();
        this.hintDrawable = reorderingHintDrawable;
        imageView.setImageDrawable(reorderingHintDrawable);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.telegram.ui.Components.Bulletin.Layout
    public void onEnterTransitionEnd() {
        super.onEnterTransitionEnd();
        this.hintDrawable.startAnimation();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.telegram.ui.Components.Bulletin.Layout
    public void onExitTransitionEnd() {
        super.onExitTransitionEnd();
        this.hintDrawable.resetAnimation();
    }
}
