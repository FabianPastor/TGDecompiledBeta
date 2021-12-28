package org.telegram.messenger;

import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class AndroidUtilities$$ExternalSyntheticLambda9 implements RecyclerListView.IntReturnCallback {
    public final /* synthetic */ BaseFragment f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ RecyclerListView f$2;

    public /* synthetic */ AndroidUtilities$$ExternalSyntheticLambda9(BaseFragment baseFragment, String str, RecyclerListView recyclerListView) {
        this.f$0 = baseFragment;
        this.f$1 = str;
        this.f$2 = recyclerListView;
    }

    public final int run() {
        return AndroidUtilities.lambda$scrollToFragmentRow$9(this.f$0, this.f$1, this.f$2);
    }
}
