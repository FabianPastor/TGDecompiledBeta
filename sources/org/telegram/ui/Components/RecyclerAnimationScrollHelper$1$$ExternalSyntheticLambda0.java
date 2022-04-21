package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import java.util.ArrayList;
import org.telegram.ui.Components.RecyclerAnimationScrollHelper;

public final /* synthetic */ class RecyclerAnimationScrollHelper$1$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ RecyclerAnimationScrollHelper.AnonymousClass1 f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ ArrayList f$4;

    public /* synthetic */ RecyclerAnimationScrollHelper$1$$ExternalSyntheticLambda0(RecyclerAnimationScrollHelper.AnonymousClass1 r1, ArrayList arrayList, boolean z, int i, ArrayList arrayList2) {
        this.f$0 = r1;
        this.f$1 = arrayList;
        this.f$2 = z;
        this.f$3 = i;
        this.f$4 = arrayList2;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.m4289xb4285cce(this.f$1, this.f$2, this.f$3, this.f$4, valueAnimator);
    }
}
