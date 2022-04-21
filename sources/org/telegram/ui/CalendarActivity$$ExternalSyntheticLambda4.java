package org.telegram.ui;

import java.util.Calendar;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class CalendarActivity$$ExternalSyntheticLambda4 implements RequestDelegate {
    public final /* synthetic */ CalendarActivity f$0;
    public final /* synthetic */ Calendar f$1;

    public /* synthetic */ CalendarActivity$$ExternalSyntheticLambda4(CalendarActivity calendarActivity, Calendar calendar) {
        this.f$0 = calendarActivity;
        this.f$1 = calendar;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1441lambda$loadNext$3$orgtelegramuiCalendarActivity(this.f$1, tLObject, tL_error);
    }
}
