package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda5 implements DialogInterface.OnClickListener {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC.TL_contacts_resolvedPeer f$2;
    public final /* synthetic */ DialogsActivity f$3;
    public final /* synthetic */ BaseFragment f$4;
    public final /* synthetic */ TLRPC.User f$5;
    public final /* synthetic */ String f$6;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda5(LaunchActivity launchActivity, int i, TLRPC.TL_contacts_resolvedPeer tL_contacts_resolvedPeer, DialogsActivity dialogsActivity, BaseFragment baseFragment, TLRPC.User user, String str) {
        this.f$0 = launchActivity;
        this.f$1 = i;
        this.f$2 = tL_contacts_resolvedPeer;
        this.f$3 = dialogsActivity;
        this.f$4 = baseFragment;
        this.f$5 = user;
        this.f$6 = str;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m3666lambda$runLinkRequest$34$orgtelegramuiLaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, dialogInterface, i);
    }
}
