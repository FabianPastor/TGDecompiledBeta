package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda77 implements DialogInterface.OnClickListener {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ TLRPC.TL_contacts_resolvedPeer f$1;
    public final /* synthetic */ BaseFragment f$2;
    public final /* synthetic */ TLRPC.User f$3;
    public final /* synthetic */ String f$4;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda77(int i, TLRPC.TL_contacts_resolvedPeer tL_contacts_resolvedPeer, BaseFragment baseFragment, TLRPC.User user, String str) {
        this.f$0 = i;
        this.f$1 = tL_contacts_resolvedPeer;
        this.f$2 = baseFragment;
        this.f$3 = user;
        this.f$4 = str;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        LaunchActivity.lambda$runLinkRequest$30(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, dialogInterface, i);
    }
}
