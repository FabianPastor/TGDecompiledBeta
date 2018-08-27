package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class MediaActivity$$Lambda$1 implements OnItemClickListener {
    private final MediaActivity arg$1;
    private final MediaPage arg$2;

    MediaActivity$$Lambda$1(MediaActivity mediaActivity, MediaPage mediaPage) {
        this.arg$1 = mediaActivity;
        this.arg$2 = mediaPage;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$1$MediaActivity(this.arg$2, view, i);
    }
}
