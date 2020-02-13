package org.telegram.ui.ActionBar;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.FrameLayout;

public class AdjustPanFrameLayout extends FrameLayout {
    private AdjustPanLayoutHelper adjustPanLayoutHelper = new AdjustPanLayoutHelper(this) {
        /* access modifiers changed from: protected */
        public void onPanTranslationUpdate(int i) {
            AdjustPanFrameLayout.this.onPanTranslationUpdate(i);
        }

        /* access modifiers changed from: protected */
        public void onTransitionStart() {
            AdjustPanFrameLayout.this.onTransitionStart();
        }

        /* access modifiers changed from: protected */
        public void onTransitionEnd() {
            AdjustPanFrameLayout.this.onTransitionEnd();
        }
    };

    /* access modifiers changed from: protected */
    public void onPanTranslationUpdate(int i) {
    }

    /* access modifiers changed from: protected */
    public void onTransitionEnd() {
    }

    /* access modifiers changed from: protected */
    public void onTransitionStart() {
    }

    public AdjustPanFrameLayout(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        this.adjustPanLayoutHelper.update();
        super.dispatchDraw(canvas);
    }
}
