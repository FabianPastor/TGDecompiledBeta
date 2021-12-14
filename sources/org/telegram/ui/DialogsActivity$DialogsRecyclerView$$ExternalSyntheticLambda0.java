package org.telegram.ui;

import android.animation.ValueAnimator;
import org.telegram.ui.DialogsActivity;

public final /* synthetic */ class DialogsActivity$DialogsRecyclerView$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ DialogsActivity.DialogsRecyclerView f$0;

    public /* synthetic */ DialogsActivity$DialogsRecyclerView$$ExternalSyntheticLambda0(DialogsActivity.DialogsRecyclerView dialogsRecyclerView) {
        this.f$0 = dialogsRecyclerView;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$onTouchEvent$0(valueAnimator);
    }
}
