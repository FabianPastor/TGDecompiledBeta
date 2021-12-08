package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Adapters.FiltersView;

public final /* synthetic */ class FilteredSearchView$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ FilteredSearchView f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ long f$10;
    public final /* synthetic */ ArrayList f$11;
    public final /* synthetic */ ArrayList f$12;
    public final /* synthetic */ TLRPC.TL_error f$2;
    public final /* synthetic */ TLObject f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ String f$6;
    public final /* synthetic */ ArrayList f$7;
    public final /* synthetic */ FiltersView.MediaFilterData f$8;
    public final /* synthetic */ long f$9;

    public /* synthetic */ FilteredSearchView$$ExternalSyntheticLambda1(FilteredSearchView filteredSearchView, int i, TLRPC.TL_error tL_error, TLObject tLObject, int i2, boolean z, String str, ArrayList arrayList, FiltersView.MediaFilterData mediaFilterData, long j, long j2, ArrayList arrayList2, ArrayList arrayList3) {
        this.f$0 = filteredSearchView;
        this.f$1 = i;
        this.f$2 = tL_error;
        this.f$3 = tLObject;
        this.f$4 = i2;
        this.f$5 = z;
        this.f$6 = str;
        this.f$7 = arrayList;
        this.f$8 = mediaFilterData;
        this.f$9 = j;
        this.f$10 = j2;
        this.f$11 = arrayList2;
        this.f$12 = arrayList3;
    }

    public final void run() {
        this.f$0.m2931lambda$search$2$orgtelegramuiFilteredSearchView(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12);
    }
}
