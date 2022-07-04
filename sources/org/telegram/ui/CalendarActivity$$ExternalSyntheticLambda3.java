package org.telegram.ui;

import java.util.Calendar;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class CalendarActivity$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ CalendarActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ Calendar f$3;

    public /* synthetic */ CalendarActivity$$ExternalSyntheticLambda3(CalendarActivity calendarActivity, TLRPC.TL_error tL_error, TLObject tLObject, Calendar calendar) {
        this.f$0 = calendarActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = calendar;
    }

    public final void run() {
        this.f$0.m2734lambda$loadNext$2$orgtelegramuiCalendarActivity(this.f$1, this.f$2, this.f$3);
    }
}
