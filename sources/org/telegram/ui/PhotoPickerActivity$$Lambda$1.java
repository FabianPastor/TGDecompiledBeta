package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;

final /* synthetic */ class PhotoPickerActivity$$Lambda$1 implements OnItemLongClickListener {
    private final PhotoPickerActivity arg$1;

    PhotoPickerActivity$$Lambda$1(PhotoPickerActivity photoPickerActivity) {
        this.arg$1 = photoPickerActivity;
    }

    public boolean onItemClick(View view, int i) {
        return this.arg$1.lambda$createView$2$PhotoPickerActivity(view, i);
    }
}
