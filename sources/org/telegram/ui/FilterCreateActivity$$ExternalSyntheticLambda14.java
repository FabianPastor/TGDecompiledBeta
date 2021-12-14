package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.FilterUsersActivity;

public final /* synthetic */ class FilterCreateActivity$$ExternalSyntheticLambda14 implements FilterUsersActivity.FilterUsersActivityDelegate {
    public final /* synthetic */ FilterCreateActivity f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ FilterCreateActivity$$ExternalSyntheticLambda14(FilterCreateActivity filterCreateActivity, int i) {
        this.f$0 = filterCreateActivity;
        this.f$1 = i;
    }

    public final void didSelectChats(ArrayList arrayList, int i) {
        this.f$0.lambda$createView$0(this.f$1, arrayList, i);
    }
}
