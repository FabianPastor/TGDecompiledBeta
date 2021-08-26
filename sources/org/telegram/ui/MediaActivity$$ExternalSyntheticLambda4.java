package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.MediaActivity;

public final /* synthetic */ class MediaActivity$$ExternalSyntheticLambda4 implements RecyclerListView.OnItemClickListener {
    public final /* synthetic */ MediaActivity f$0;
    public final /* synthetic */ MediaActivity.MediaPage f$1;

    public /* synthetic */ MediaActivity$$ExternalSyntheticLambda4(MediaActivity mediaActivity, MediaActivity.MediaPage mediaPage) {
        this.f$0 = mediaActivity;
        this.f$1 = mediaPage;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.lambda$createView$2(this.f$1, view, i);
    }
}
