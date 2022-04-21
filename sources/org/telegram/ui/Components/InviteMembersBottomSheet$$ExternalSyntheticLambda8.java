package org.telegram.ui.Components;

import android.content.Context;
import android.view.View;
import androidx.collection.LongSparseArray;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class InviteMembersBottomSheet$$ExternalSyntheticLambda8 implements RecyclerListView.OnItemClickListener {
    public final /* synthetic */ InviteMembersBottomSheet f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ BaseFragment f$2;
    public final /* synthetic */ LongSparseArray f$3;
    public final /* synthetic */ Context f$4;

    public /* synthetic */ InviteMembersBottomSheet$$ExternalSyntheticLambda8(InviteMembersBottomSheet inviteMembersBottomSheet, long j, BaseFragment baseFragment, LongSparseArray longSparseArray, Context context) {
        this.f$0 = inviteMembersBottomSheet;
        this.f$1 = j;
        this.f$2 = baseFragment;
        this.f$3 = longSparseArray;
        this.f$4 = context;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.m4091lambda$new$0$orgtelegramuiComponentsInviteMembersBottomSheet(this.f$1, this.f$2, this.f$3, this.f$4, view, i);
    }
}
