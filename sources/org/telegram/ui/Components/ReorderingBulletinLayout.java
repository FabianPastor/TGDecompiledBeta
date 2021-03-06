package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.ImageView;
import org.telegram.ui.Components.Bulletin;

@SuppressLint({"ViewConstructor"})
public class ReorderingBulletinLayout extends Bulletin.SimpleLayout {
    private final ReorderingHintDrawable hintDrawable;

    public ReorderingBulletinLayout(Context context, String str) {
        super(context);
        this.textView.setText(str);
        this.textView.setTranslationY(-1.0f);
        ImageView imageView = this.imageView;
        ReorderingHintDrawable reorderingHintDrawable = new ReorderingHintDrawable();
        this.hintDrawable = reorderingHintDrawable;
        imageView.setImageDrawable(reorderingHintDrawable);
    }

    /* access modifiers changed from: protected */
    public void onEnterTransitionEnd() {
        super.onEnterTransitionEnd();
        this.hintDrawable.startAnimation();
    }

    /* access modifiers changed from: protected */
    public void onExitTransitionEnd() {
        super.onExitTransitionEnd();
        this.hintDrawable.resetAnimation();
    }
}
