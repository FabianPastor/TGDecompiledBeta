package org.telegram.ui;

import java.util.Calendar;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class CalendarActivity$$ExternalSyntheticLambda4 implements RequestDelegate {
    public final /* synthetic */ CalendarActivity f$0;
    public final /* synthetic */ Calendar f$1;

    public /* synthetic */ CalendarActivity$$ExternalSyntheticLambda4(CalendarActivity calendarActivity, Calendar calendar) {
        this.f$0 = calendarActivity;
        this.f$1 = calendar;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadNext$3(this.f$1, tLObject, tLRPC$TL_error);
    }
}
