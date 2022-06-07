package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.ui.Components.AnimatedTextView;

public final /* synthetic */ class AnimatedTextView$AnimatedTextDrawable$$ExternalSyntheticLambda1 implements AnimatedTextView.AnimatedTextDrawable.RegionCallback {
    public final /* synthetic */ AnimatedTextView.AnimatedTextDrawable f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ ArrayList f$3;

    public /* synthetic */ AnimatedTextView$AnimatedTextDrawable$$ExternalSyntheticLambda1(AnimatedTextView.AnimatedTextDrawable animatedTextDrawable, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
        this.f$0 = animatedTextDrawable;
        this.f$1 = arrayList;
        this.f$2 = arrayList2;
        this.f$3 = arrayList3;
    }

    public final void run(CharSequence charSequence, int i, int i2) {
        this.f$0.lambda$setText$2(this.f$1, this.f$2, this.f$3, charSequence, i, i2);
    }
}
