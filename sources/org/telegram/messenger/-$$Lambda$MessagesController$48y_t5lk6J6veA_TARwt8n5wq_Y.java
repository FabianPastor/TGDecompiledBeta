package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$48y_t5lk6J6veA_TARwt8n5wq_Y implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$48y_t5lk6J6veA_TARwt8n5wq_Y INSTANCE = new -$$Lambda$MessagesController$48y_t5lk6J6veA_TARwt8n5wq_Y();

    private /* synthetic */ -$$Lambda$MessagesController$48y_t5lk6J6veA_TARwt8n5wq_Y() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$hideReportSpam$23(tLObject, tL_error);
    }
}
