package org.telegram.ui;

import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import androidx.viewpager.widget.ViewPager;
import java.util.concurrent.atomic.AtomicBoolean;
import org.telegram.ui.Components.ReactionTabHolderView;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda53 implements View.OnClickListener {
    public final /* synthetic */ ViewPager f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ LinearLayout f$2;
    public final /* synthetic */ AtomicBoolean f$3;
    public final /* synthetic */ HorizontalScrollView f$4;
    public final /* synthetic */ ReactionTabHolderView f$5;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda53(ViewPager viewPager, int i, LinearLayout linearLayout, AtomicBoolean atomicBoolean, HorizontalScrollView horizontalScrollView, ReactionTabHolderView reactionTabHolderView) {
        this.f$0 = viewPager;
        this.f$1 = i;
        this.f$2 = linearLayout;
        this.f$3 = atomicBoolean;
        this.f$4 = horizontalScrollView;
        this.f$5 = reactionTabHolderView;
    }

    public final void onClick(View view) {
        ChatActivity.lambda$createMenu$151(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, view);
    }
}
