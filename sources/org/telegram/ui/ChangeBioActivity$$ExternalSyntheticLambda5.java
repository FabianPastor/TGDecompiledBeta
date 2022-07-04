package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class ChangeBioActivity$$ExternalSyntheticLambda5 implements RequestDelegate {
    public final /* synthetic */ ChangeBioActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLRPC.UserFull f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ TLRPC.TL_account_updateProfile f$4;

    public /* synthetic */ ChangeBioActivity$$ExternalSyntheticLambda5(ChangeBioActivity changeBioActivity, AlertDialog alertDialog, TLRPC.UserFull userFull, String str, TLRPC.TL_account_updateProfile tL_account_updateProfile) {
        this.f$0 = changeBioActivity;
        this.f$1 = alertDialog;
        this.f$2 = userFull;
        this.f$3 = str;
        this.f$4 = tL_account_updateProfile;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2842lambda$saveName$4$orgtelegramuiChangeBioActivity(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
