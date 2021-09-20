package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.ChatActivity;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda19 implements DialogInterface.OnClickListener {
    public final /* synthetic */ TLRPC$User f$0;
    public final /* synthetic */ AccountInstance f$1;
    public final /* synthetic */ ChatActivity f$2;
    public final /* synthetic */ TLRPC$Chat f$3;
    public final /* synthetic */ MessageObject f$4;
    public final /* synthetic */ CheckBoxCell[] f$5;
    public final /* synthetic */ Theme.ResourcesProvider f$6;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda19(TLRPC$User tLRPC$User, AccountInstance accountInstance, ChatActivity chatActivity, TLRPC$Chat tLRPC$Chat, MessageObject messageObject, CheckBoxCell[] checkBoxCellArr, Theme.ResourcesProvider resourcesProvider) {
        this.f$0 = tLRPC$User;
        this.f$1 = accountInstance;
        this.f$2 = chatActivity;
        this.f$3 = tLRPC$Chat;
        this.f$4 = messageObject;
        this.f$5 = checkBoxCellArr;
        this.f$6 = resourcesProvider;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$showBlockReportSpamReplyAlert$5(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, dialogInterface, i);
    }
}
