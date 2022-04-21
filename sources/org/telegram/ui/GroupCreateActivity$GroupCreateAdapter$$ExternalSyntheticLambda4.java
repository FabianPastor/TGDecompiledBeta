package org.telegram.ui;

import androidx.collection.LongSparseArray;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.GroupCreateActivity;

public final /* synthetic */ class GroupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda4 implements SearchAdapterHelper.SearchAdapterHelperDelegate {
    public final /* synthetic */ GroupCreateActivity.GroupCreateAdapter f$0;

    public /* synthetic */ GroupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda4(GroupCreateActivity.GroupCreateAdapter groupCreateAdapter) {
        this.f$0 = groupCreateAdapter;
    }

    public /* synthetic */ boolean canApplySearchResults(int i) {
        return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$canApplySearchResults(this, i);
    }

    public /* synthetic */ LongSparseArray getExcludeCallParticipants() {
        return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeCallParticipants(this);
    }

    public /* synthetic */ LongSparseArray getExcludeUsers() {
        return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
    }

    public final void onDataSetChanged(int i) {
        this.f$0.m2262x6d31CLASSNAME(i);
    }

    public /* synthetic */ void onSetHashtags(ArrayList arrayList, HashMap hashMap) {
        SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
    }
}
