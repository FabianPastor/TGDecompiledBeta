package org.telegram.ui.Components;

import android.view.View;
import java.util.HashMap;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class InviteLinkBottomSheet$$ExternalSyntheticLambda4 implements RecyclerListView.OnItemClickListener {
    public final /* synthetic */ InviteLinkBottomSheet f$0;
    public final /* synthetic */ TLRPC.TL_chatInviteExported f$1;
    public final /* synthetic */ HashMap f$2;
    public final /* synthetic */ BaseFragment f$3;

    public /* synthetic */ InviteLinkBottomSheet$$ExternalSyntheticLambda4(InviteLinkBottomSheet inviteLinkBottomSheet, TLRPC.TL_chatInviteExported tL_chatInviteExported, HashMap hashMap, BaseFragment baseFragment) {
        this.f$0 = inviteLinkBottomSheet;
        this.f$1 = tL_chatInviteExported;
        this.f$2 = hashMap;
        this.f$3 = baseFragment;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.m2363lambda$new$1$orgtelegramuiComponentsInviteLinkBottomSheet(this.f$1, this.f$2, this.f$3, view, i);
    }
}
