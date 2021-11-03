package org.telegram.ui;

import java.util.Calendar;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaCalendarActivity$$ExternalSyntheticLambda1 implements RequestDelegate {
    public final /* synthetic */ MediaCalendarActivity f$0;
    public final /* synthetic */ Calendar f$1;

    public /* synthetic */ MediaCalendarActivity$$ExternalSyntheticLambda1(MediaCalendarActivity mediaCalendarActivity, Calendar calendar) {
        this.f$0 = mediaCalendarActivity;
        this.f$1 = calendar;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadNext$1(this.f$1, tLObject, tLRPC$TL_error);
    }
}
