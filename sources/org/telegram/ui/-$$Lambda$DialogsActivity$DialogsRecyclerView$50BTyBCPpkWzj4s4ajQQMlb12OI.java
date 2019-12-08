package org.telegram.ui;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import org.telegram.ui.DialogsActivity.DialogsRecyclerView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DialogsActivity$DialogsRecyclerView$50BTyBCPpkWzj4s4ajQQMlb12OI implements AnimatorUpdateListener {
    private final /* synthetic */ DialogsRecyclerView f$0;

    public /* synthetic */ -$$Lambda$DialogsActivity$DialogsRecyclerView$50BTyBCPpkWzj4s4ajQQMlb12OI(DialogsRecyclerView dialogsRecyclerView) {
        this.f$0 = dialogsRecyclerView;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$onTouchEvent$0$DialogsActivity$DialogsRecyclerView(valueAnimator);
    }
}
