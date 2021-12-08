package org.telegram.ui;

import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ProfileActivity;

public final /* synthetic */ class ProfileActivity$4$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ ProfileActivity.AnonymousClass4 f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ UserConfig f$2;
    public final /* synthetic */ TLRPC.Photo f$3;

    public /* synthetic */ ProfileActivity$4$$ExternalSyntheticLambda4(ProfileActivity.AnonymousClass4 r1, TLObject tLObject, UserConfig userConfig, TLRPC.Photo photo) {
        this.f$0 = r1;
        this.f$1 = tLObject;
        this.f$2 = userConfig;
        this.f$3 = photo;
    }

    public final void run() {
        this.f$0.m3727lambda$onItemClick$6$orgtelegramuiProfileActivity$4(this.f$1, this.f$2, this.f$3);
    }
}
