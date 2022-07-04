package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ManageLinksActivity;

public final /* synthetic */ class ManageLinksActivity$LinkCell$$ExternalSyntheticLambda2 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ManageLinksActivity.LinkCell f$0;
    public final /* synthetic */ TLRPC.TL_chatInviteExported f$1;

    public /* synthetic */ ManageLinksActivity$LinkCell$$ExternalSyntheticLambda2(ManageLinksActivity.LinkCell linkCell, TLRPC.TL_chatInviteExported tL_chatInviteExported) {
        this.f$0 = linkCell;
        this.f$1 = tL_chatInviteExported;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m3939lambda$new$1$orgtelegramuiManageLinksActivity$LinkCell(this.f$1, dialogInterface, i);
    }
}
