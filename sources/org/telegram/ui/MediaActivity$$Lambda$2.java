package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;

final /* synthetic */ class MediaActivity$$Lambda$2 implements OnItemLongClickListener {
    private final MediaActivity arg$1;
    private final MediaPage arg$2;

    MediaActivity$$Lambda$2(MediaActivity mediaActivity, MediaPage mediaPage) {
        this.arg$1 = mediaActivity;
        this.arg$2 = mediaPage;
    }

    public boolean onItemClick(View view, int i) {
        return this.arg$1.lambda$createView$2$MediaActivity(this.arg$2, view, i);
    }
}
