package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PhotoPickerActivity$YlYtGKi2LwgC6UMh4JLKKv8JxjQ implements RequestDelegate {
    private final /* synthetic */ PhotoPickerActivity f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ User f$3;

    public /* synthetic */ -$$Lambda$PhotoPickerActivity$YlYtGKi2LwgC6UMh4JLKKv8JxjQ(PhotoPickerActivity photoPickerActivity, int i, boolean z, User user) {
        this.f$0 = photoPickerActivity;
        this.f$1 = i;
        this.f$2 = z;
        this.f$3 = user;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$searchImages$12$PhotoPickerActivity(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
