package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$NotificationsController$KyQqllEdy_fdmMCr6frsin2S3Cs implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$NotificationsController$KyQqllEdy_fdmMCr6frsin2S3Cs INSTANCE = new -$$Lambda$NotificationsController$KyQqllEdy_fdmMCr6frsin2S3Cs();

    private /* synthetic */ -$$Lambda$NotificationsController$KyQqllEdy_fdmMCr6frsin2S3Cs() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        NotificationsController.lambda$updateServerNotificationsSettings$36(tLObject, tL_error);
    }
}