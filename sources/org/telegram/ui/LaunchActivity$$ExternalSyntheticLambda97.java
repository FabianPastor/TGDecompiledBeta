package org.telegram.ui;

import android.view.View;
import androidx.recyclerview.widget.ItemTouchHelper;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda97 implements RecyclerListView.OnItemLongClickListener {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ ItemTouchHelper f$1;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda97(LaunchActivity launchActivity, ItemTouchHelper itemTouchHelper) {
        this.f$0 = launchActivity;
        this.f$1 = itemTouchHelper;
    }

    public final boolean onItemClick(View view, int i) {
        return this.f$0.lambda$onCreate$5(this.f$1, view, i);
    }
}
