package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$NotificationsController$w9HtqTbEDgkwB57xEiog8KyWkW8  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$NotificationsController$w9HtqTbEDgkwB57xEiog8KyWkW8 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$NotificationsController$w9HtqTbEDgkwB57xEiog8KyWkW8 INSTANCE = new $$Lambda$NotificationsController$w9HtqTbEDgkwB57xEiog8KyWkW8();

    private /* synthetic */ $$Lambda$NotificationsController$w9HtqTbEDgkwB57xEiog8KyWkW8() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        NotificationsController.lambda$updateServerNotificationsSettings$38(tLObject, tLRPC$TL_error);
    }
}
