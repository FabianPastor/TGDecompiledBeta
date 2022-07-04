package org.telegram.ui.Components;

import android.view.View;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class PermanentLinkBottomSheet$$ExternalSyntheticLambda0 implements View.OnClickListener {
    public final /* synthetic */ PermanentLinkBottomSheet f$0;
    public final /* synthetic */ TLRPC.ChatFull f$1;
    public final /* synthetic */ BaseFragment f$2;

    public /* synthetic */ PermanentLinkBottomSheet$$ExternalSyntheticLambda0(PermanentLinkBottomSheet permanentLinkBottomSheet, TLRPC.ChatFull chatFull, BaseFragment baseFragment) {
        this.f$0 = permanentLinkBottomSheet;
        this.f$1 = chatFull;
        this.f$2 = baseFragment;
    }

    public final void onClick(View view) {
        this.f$0.m1156lambda$new$1$orgtelegramuiComponentsPermanentLinkBottomSheet(this.f$1, this.f$2, view);
    }
}
