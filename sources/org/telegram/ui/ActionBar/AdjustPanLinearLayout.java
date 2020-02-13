package org.telegram.ui.ActionBar;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.LinearLayout;

public class AdjustPanLinearLayout extends LinearLayout {
    private AdjustPanLayoutHelper adjustPanLayoutHelper = new AdjustPanLayoutHelper(this) {
        /* access modifiers changed from: protected */
        public void onPanTranslationUpdate(int i) {
            AdjustPanLinearLayout.this.onPanTranslationUpdate(i);
        }
    };

    /* access modifiers changed from: protected */
    public void onPanTranslationUpdate(int i) {
    }

    public AdjustPanLinearLayout(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        this.adjustPanLayoutHelper.update();
        super.dispatchDraw(canvas);
    }
}
