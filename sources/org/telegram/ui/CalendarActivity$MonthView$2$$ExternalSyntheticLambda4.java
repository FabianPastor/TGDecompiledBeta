package org.telegram.ui;

import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.CalendarActivity;

public final /* synthetic */ class CalendarActivity$MonthView$2$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ CalendarActivity.MonthView.AnonymousClass2 f$0;
    public final /* synthetic */ BaseFragment f$1;
    public final /* synthetic */ CalendarActivity.PeriodDay f$2;

    public /* synthetic */ CalendarActivity$MonthView$2$$ExternalSyntheticLambda4(CalendarActivity.MonthView.AnonymousClass2 r1, BaseFragment baseFragment, CalendarActivity.PeriodDay periodDay) {
        this.f$0 = r1;
        this.f$1 = baseFragment;
        this.f$2 = periodDay;
    }

    public final void run() {
        this.f$0.lambda$onLongPress$0(this.f$1, this.f$2);
    }
}