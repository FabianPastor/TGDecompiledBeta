package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ChatUsersActivity;

public final /* synthetic */ class ChatUsersActivity$9$$ExternalSyntheticLambda2 implements MessagesController.ErrorDelegate {
    public final /* synthetic */ int[] f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ TLRPC$User f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ Runnable f$4;

    public /* synthetic */ ChatUsersActivity$9$$ExternalSyntheticLambda2(int[] iArr, ArrayList arrayList, TLRPC$User tLRPC$User, int i, Runnable runnable) {
        this.f$0 = iArr;
        this.f$1 = arrayList;
        this.f$2 = tLRPC$User;
        this.f$3 = i;
        this.f$4 = runnable;
    }

    public final boolean run(TLRPC$TL_error tLRPC$TL_error) {
        return ChatUsersActivity.AnonymousClass9.lambda$didSelectUsers$2(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, tLRPC$TL_error);
    }
}
