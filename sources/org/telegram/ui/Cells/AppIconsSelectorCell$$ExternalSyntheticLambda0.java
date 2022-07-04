package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class AppIconsSelectorCell$$ExternalSyntheticLambda0 implements RecyclerListView.OnItemClickListener {
    public final /* synthetic */ AppIconsSelectorCell f$0;
    public final /* synthetic */ BaseFragment f$1;
    public final /* synthetic */ Context f$2;

    public /* synthetic */ AppIconsSelectorCell$$ExternalSyntheticLambda0(AppIconsSelectorCell appIconsSelectorCell, BaseFragment baseFragment, Context context) {
        this.f$0 = appIconsSelectorCell;
        this.f$1 = baseFragment;
        this.f$2 = context;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.lambda$new$0(this.f$1, this.f$2, view, i);
    }
}
