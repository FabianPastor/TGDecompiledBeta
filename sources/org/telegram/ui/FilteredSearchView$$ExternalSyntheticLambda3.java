package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.Adapters.FiltersView;

public final /* synthetic */ class FilteredSearchView$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ FilteredSearchView f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ FiltersView.MediaFilterData f$5;
    public final /* synthetic */ long f$6;
    public final /* synthetic */ long f$7;
    public final /* synthetic */ ArrayList f$8;
    public final /* synthetic */ ArrayList f$9;

    public /* synthetic */ FilteredSearchView$$ExternalSyntheticLambda3(FilteredSearchView filteredSearchView, int i, String str, int i2, boolean z, FiltersView.MediaFilterData mediaFilterData, long j, long j2, ArrayList arrayList, ArrayList arrayList2) {
        this.f$0 = filteredSearchView;
        this.f$1 = i;
        this.f$2 = str;
        this.f$3 = i2;
        this.f$4 = z;
        this.f$5 = mediaFilterData;
        this.f$6 = j;
        this.f$7 = j2;
        this.f$8 = arrayList;
        this.f$9 = arrayList2;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$search$3(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, tLObject, tLRPC$TL_error);
    }
}
