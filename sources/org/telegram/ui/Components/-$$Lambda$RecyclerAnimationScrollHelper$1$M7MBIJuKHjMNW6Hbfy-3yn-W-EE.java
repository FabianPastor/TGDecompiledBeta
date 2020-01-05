package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import java.util.ArrayList;
import org.telegram.ui.Components.RecyclerAnimationScrollHelper.AnonymousClass1;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$RecyclerAnimationScrollHelper$1$M7MBIJuKHjMNW6Hbfy-3yn-W-EE implements AnimatorUpdateListener {
    private final /* synthetic */ AnonymousClass1 f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ ArrayList f$4;

    public /* synthetic */ -$$Lambda$RecyclerAnimationScrollHelper$1$M7MBIJuKHjMNW6Hbfy-3yn-W-EE(AnonymousClass1 anonymousClass1, ArrayList arrayList, boolean z, int i, ArrayList arrayList2) {
        this.f$0 = anonymousClass1;
        this.f$1 = arrayList;
        this.f$2 = z;
        this.f$3 = i;
        this.f$4 = arrayList2;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$onLayoutChange$0$RecyclerAnimationScrollHelper$1(this.f$1, this.f$2, this.f$3, this.f$4, valueAnimator);
    }
}
