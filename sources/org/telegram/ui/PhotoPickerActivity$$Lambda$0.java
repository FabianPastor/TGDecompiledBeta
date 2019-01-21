package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class PhotoPickerActivity$$Lambda$0 implements OnItemClickListener {
    private final PhotoPickerActivity arg$1;

    PhotoPickerActivity$$Lambda$0(PhotoPickerActivity photoPickerActivity) {
        this.arg$1 = photoPickerActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$0$PhotoPickerActivity(view, i);
    }
}
