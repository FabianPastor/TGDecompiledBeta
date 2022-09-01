package org.telegram.ui.Components.Premium;

import android.view.View;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class PremiumPreviewBottomSheet$$ExternalSyntheticLambda5 implements RecyclerListView.OnItemClickListener {
    public final /* synthetic */ PremiumPreviewBottomSheet f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ BaseFragment f$2;

    public /* synthetic */ PremiumPreviewBottomSheet$$ExternalSyntheticLambda5(PremiumPreviewBottomSheet premiumPreviewBottomSheet, int i, BaseFragment baseFragment) {
        this.f$0 = premiumPreviewBottomSheet;
        this.f$1 = i;
        this.f$2 = baseFragment;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.lambda$new$0(this.f$1, this.f$2, view, i);
    }
}
