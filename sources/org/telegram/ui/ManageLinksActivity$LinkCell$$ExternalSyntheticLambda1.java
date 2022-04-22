package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.ui.ManageLinksActivity;

public final /* synthetic */ class ManageLinksActivity$LinkCell$$ExternalSyntheticLambda1 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ManageLinksActivity.LinkCell f$0;
    public final /* synthetic */ TLRPC$TL_chatInviteExported f$1;

    public /* synthetic */ ManageLinksActivity$LinkCell$$ExternalSyntheticLambda1(ManageLinksActivity.LinkCell linkCell, TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported) {
        this.f$0 = linkCell;
        this.f$1 = tLRPC$TL_chatInviteExported;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$new$0(this.f$1, dialogInterface, i);
    }
}
