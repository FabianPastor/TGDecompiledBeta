package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class DataAutoDownloadActivity$$ExternalSyntheticLambda5 implements RecyclerListView.OnItemClickListenerExtended {
    public final /* synthetic */ DataAutoDownloadActivity f$0;

    public /* synthetic */ DataAutoDownloadActivity$$ExternalSyntheticLambda5(DataAutoDownloadActivity dataAutoDownloadActivity) {
        this.f$0 = dataAutoDownloadActivity;
    }

    public /* synthetic */ boolean hasDoubleTap(View view, int i) {
        return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i);
    }

    public /* synthetic */ void onDoubleTap(View view, int i, float f, float f2) {
        RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i, f, f2);
    }

    public final void onItemClick(View view, int i, float f, float f2) {
        this.f$0.m3355lambda$createView$4$orgtelegramuiDataAutoDownloadActivity(view, i, f, f2);
    }
}
