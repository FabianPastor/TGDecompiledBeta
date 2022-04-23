package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_getDiscussionMessage;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda67 implements RequestDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ Integer f$2;
    public final /* synthetic */ TLRPC$Chat f$3;
    public final /* synthetic */ TLRPC$TL_messages_getDiscussionMessage f$4;
    public final /* synthetic */ Integer f$5;
    public final /* synthetic */ Integer f$6;
    public final /* synthetic */ AlertDialog f$7;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda67(LaunchActivity launchActivity, int i, Integer num, TLRPC$Chat tLRPC$Chat, TLRPC$TL_messages_getDiscussionMessage tLRPC$TL_messages_getDiscussionMessage, Integer num2, Integer num3, AlertDialog alertDialog) {
        this.f$0 = launchActivity;
        this.f$1 = i;
        this.f$2 = num;
        this.f$3 = tLRPC$Chat;
        this.f$4 = tLRPC$TL_messages_getDiscussionMessage;
        this.f$5 = num2;
        this.f$6 = num3;
        this.f$7 = alertDialog;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$runCommentRequest$23(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, tLObject, tLRPC$TL_error);
    }
}
