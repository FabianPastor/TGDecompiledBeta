package org.telegram.ui;

import android.content.Context;
import android.view.View;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class ManageLinksActivity$$ExternalSyntheticLambda16 implements RecyclerListView.OnItemClickListener {
    public final /* synthetic */ ManageLinksActivity f$0;
    public final /* synthetic */ Context f$1;

    public /* synthetic */ ManageLinksActivity$$ExternalSyntheticLambda16(ManageLinksActivity manageLinksActivity, Context context) {
        this.f$0 = manageLinksActivity;
        this.f$1 = context;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.lambda$createView$9(this.f$1, view, i);
    }
}
