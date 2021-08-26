package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.MediaActivity;

public final /* synthetic */ class MediaActivity$$ExternalSyntheticLambda5 implements RecyclerListView.OnItemLongClickListener {
    public final /* synthetic */ MediaActivity f$0;
    public final /* synthetic */ MediaActivity.MediaPage f$1;

    public /* synthetic */ MediaActivity$$ExternalSyntheticLambda5(MediaActivity mediaActivity, MediaActivity.MediaPage mediaPage) {
        this.f$0 = mediaActivity;
        this.f$1 = mediaPage;
    }

    public final boolean onItemClick(View view, int i) {
        return this.f$0.lambda$createView$3(this.f$1, view, i);
    }
}
