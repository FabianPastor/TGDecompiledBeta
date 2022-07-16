package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ProfileActivity;

public final /* synthetic */ class ProfileActivity$SearchAdapter$$ExternalSyntheticLambda88 implements RequestDelegate {
    public final /* synthetic */ ProfileActivity.SearchAdapter f$0;

    public /* synthetic */ ProfileActivity$SearchAdapter$$ExternalSyntheticLambda88(ProfileActivity.SearchAdapter searchAdapter) {
        this.f$0 = searchAdapter;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadFaqWebPage$86(tLObject, tLRPC$TL_error);
    }
}
