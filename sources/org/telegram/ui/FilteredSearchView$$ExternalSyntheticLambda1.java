package org.telegram.ui;

import org.telegram.ui.Adapters.FiltersView;

public final /* synthetic */ class FilteredSearchView$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ FilteredSearchView f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$10;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ FiltersView.MediaFilterData f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ long f$5;
    public final /* synthetic */ long f$6;
    public final /* synthetic */ boolean f$7;
    public final /* synthetic */ boolean f$8;
    public final /* synthetic */ String f$9;

    public /* synthetic */ FilteredSearchView$$ExternalSyntheticLambda1(FilteredSearchView filteredSearchView, int i, String str, FiltersView.MediaFilterData mediaFilterData, int i2, long j, long j2, boolean z, boolean z2, String str2, int i3) {
        this.f$0 = filteredSearchView;
        this.f$1 = i;
        this.f$2 = str;
        this.f$3 = mediaFilterData;
        this.f$4 = i2;
        this.f$5 = j;
        this.f$6 = j2;
        this.f$7 = z;
        this.f$8 = z2;
        this.f$9 = str2;
        this.f$10 = i3;
    }

    public final void run() {
        this.f$0.lambda$search$4(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10);
    }
}
