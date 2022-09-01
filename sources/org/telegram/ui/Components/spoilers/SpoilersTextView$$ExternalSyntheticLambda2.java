package org.telegram.ui.Components.spoilers;

import org.telegram.ui.Components.spoilers.SpoilersClickDetector;

public final /* synthetic */ class SpoilersTextView$$ExternalSyntheticLambda2 implements SpoilersClickDetector.OnSpoilerClickedListener {
    public final /* synthetic */ SpoilersTextView f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ SpoilersTextView$$ExternalSyntheticLambda2(SpoilersTextView spoilersTextView, boolean z) {
        this.f$0 = spoilersTextView;
        this.f$1 = z;
    }

    public final void onSpoilerClicked(SpoilerEffect spoilerEffect, float f, float f2) {
        this.f$0.lambda$new$2(this.f$1, spoilerEffect, f, f2);
    }
}
