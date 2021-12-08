package org.telegram.ui.Components;

import androidx.collection.LongSparseArray;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Components.SharedMediaLayout;

public final /* synthetic */ class SharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda4 implements SearchAdapterHelper.SearchAdapterHelperDelegate {
    public final /* synthetic */ SharedMediaLayout.GroupUsersSearchAdapter f$0;

    public /* synthetic */ SharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda4(SharedMediaLayout.GroupUsersSearchAdapter groupUsersSearchAdapter) {
        this.f$0 = groupUsersSearchAdapter;
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
        this.f$0.m2593x8d979017(i);
    }

    public /* synthetic */ void onSetHashtags(ArrayList arrayList, HashMap hashMap) {
        SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
    }
}
