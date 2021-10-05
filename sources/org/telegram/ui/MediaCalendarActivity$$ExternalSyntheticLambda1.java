package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaCalendarActivity$$ExternalSyntheticLambda1 implements RequestDelegate {
    public final /* synthetic */ MediaCalendarActivity f$0;

    public /* synthetic */ MediaCalendarActivity$$ExternalSyntheticLambda1(MediaCalendarActivity mediaCalendarActivity) {
        this.f$0 = mediaCalendarActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$createView$1(tLObject, tLRPC$TL_error);
    }
}
