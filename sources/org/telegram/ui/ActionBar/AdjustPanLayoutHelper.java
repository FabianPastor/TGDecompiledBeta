package org.telegram.ui.ActionBar;

import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.SharedConfig;

public class AdjustPanLayoutHelper {
    private int framesWithoutMovement;
    private int[] loc = new int[2];
    private View parentView;
    private int prevMovement;
    private boolean wasMovement;

    /* access modifiers changed from: protected */
    public void onPanTranslationUpdate(int i) {
    }

    /* access modifiers changed from: protected */
    public void onTransitionEnd() {
    }

    /* access modifiers changed from: protected */
    public void onTransitionStart() {
    }

    public AdjustPanLayoutHelper(View view) {
        this.parentView = view;
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public final void onGlobalLayout() {
                AdjustPanLayoutHelper.this.onUpdate();
            }
        });
        this.parentView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            public final void onScrollChanged() {
                AdjustPanLayoutHelper.this.onUpdate();
            }
        });
        this.parentView.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            public final void onGlobalFocusChanged(View view, View view2) {
                AdjustPanLayoutHelper.this.lambda$new$0$AdjustPanLayoutHelper(view, view2);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$AdjustPanLayoutHelper(View view, View view2) {
        onUpdate();
    }

    /* access modifiers changed from: private */
    public void onUpdate() {
        if (SharedConfig.smoothKeyboard) {
            this.framesWithoutMovement = 0;
            this.wasMovement = false;
            this.parentView.invalidate();
        }
    }

    public void update() {
        if (this.parentView.getVisibility() == 0 && this.parentView.getParent() != null && !AndroidUtilities.usingHardwareInput && SharedConfig.smoothKeyboard) {
            this.parentView.getLocationInWindow(this.loc);
            int[] iArr = this.loc;
            if (iArr[1] <= 0) {
                iArr[1] = (int) (((float) iArr[1]) - this.parentView.getTranslationY());
                if (Build.VERSION.SDK_INT < 21) {
                    int[] iArr2 = this.loc;
                    iArr2[1] = iArr2[1] - AndroidUtilities.statusBarHeight;
                }
            } else {
                iArr[1] = 0;
            }
            if (this.loc[1] != this.prevMovement) {
                if (!this.wasMovement) {
                    onTransitionStart();
                }
                this.wasMovement = true;
                onPanTranslationUpdate(-this.loc[1]);
                this.framesWithoutMovement = 0;
                this.prevMovement = this.loc[1];
            } else {
                this.framesWithoutMovement++;
            }
            if (this.framesWithoutMovement < 5) {
                this.parentView.invalidate();
            } else if (this.wasMovement) {
                onTransitionEnd();
            }
        }
    }
}
