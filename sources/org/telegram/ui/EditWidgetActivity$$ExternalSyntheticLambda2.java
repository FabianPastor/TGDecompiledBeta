package org.telegram.ui;

import android.content.Context;
import android.view.View;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class EditWidgetActivity$$ExternalSyntheticLambda2 implements RecyclerListView.OnItemClickListener {
    public final /* synthetic */ EditWidgetActivity f$0;
    public final /* synthetic */ Context f$1;

    public /* synthetic */ EditWidgetActivity$$ExternalSyntheticLambda2(EditWidgetActivity editWidgetActivity, Context context) {
        this.f$0 = editWidgetActivity;
        this.f$1 = context;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.lambda$createView$1(this.f$1, view, i);
    }
}
