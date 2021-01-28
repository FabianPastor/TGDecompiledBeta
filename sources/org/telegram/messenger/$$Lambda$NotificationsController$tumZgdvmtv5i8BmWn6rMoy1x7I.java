package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$NotificationsController$tumZgdvmtv5i8BmWn-6rMoy1x7I  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$NotificationsController$tumZgdvmtv5i8BmWn6rMoy1x7I implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$NotificationsController$tumZgdvmtv5i8BmWn6rMoy1x7I INSTANCE = new $$Lambda$NotificationsController$tumZgdvmtv5i8BmWn6rMoy1x7I();

    private /* synthetic */ $$Lambda$NotificationsController$tumZgdvmtv5i8BmWn6rMoy1x7I() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        NotificationsController.lambda$updateServerNotificationsSettings$39(tLObject, tLRPC$TL_error);
    }
}
