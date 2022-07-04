package org.telegram.ui;

import android.content.Context;
import android.view.View;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class DataSettingsActivity$$ExternalSyntheticLambda6 implements RecyclerListView.OnItemClickListenerExtended {
    public final /* synthetic */ DataSettingsActivity f$0;
    public final /* synthetic */ Context f$1;

    public /* synthetic */ DataSettingsActivity$$ExternalSyntheticLambda6(DataSettingsActivity dataSettingsActivity, Context context) {
        this.f$0 = dataSettingsActivity;
        this.f$1 = context;
    }

    public /* synthetic */ boolean hasDoubleTap(View view, int i) {
        return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i);
    }

    public /* synthetic */ void onDoubleTap(View view, int i, float f, float f2) {
        RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i, f, f2);
    }

    public final void onItemClick(View view, int i, float f, float f2) {
        this.f$0.m3363lambda$createView$6$orgtelegramuiDataSettingsActivity(this.f$1, view, i, f, f2);
    }
}
