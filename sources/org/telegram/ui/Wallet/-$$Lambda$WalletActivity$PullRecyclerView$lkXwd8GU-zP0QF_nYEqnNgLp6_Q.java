package org.telegram.ui.Wallet;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import org.telegram.ui.Wallet.WalletActivity.PullRecyclerView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WalletActivity$PullRecyclerView$lkXwd8GU-zP0QF_nYEqnNgLp6_Q implements AnimatorUpdateListener {
    private final /* synthetic */ PullRecyclerView f$0;

    public /* synthetic */ -$$Lambda$WalletActivity$PullRecyclerView$lkXwd8GU-zP0QF_nYEqnNgLp6_Q(PullRecyclerView pullRecyclerView) {
        this.f$0 = pullRecyclerView;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$onTouchEvent$0$WalletActivity$PullRecyclerView(valueAnimator);
    }
}
