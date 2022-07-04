package org.telegram.ui;

import android.animation.ValueAnimator;
import org.telegram.ui.CalendarActivity;

public final /* synthetic */ class CalendarActivity$MonthView$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ CalendarActivity.MonthView f$0;
    public final /* synthetic */ CalendarActivity.RowAnimationValue f$1;
    public final /* synthetic */ float f$2;
    public final /* synthetic */ float f$3;
    public final /* synthetic */ float f$4;
    public final /* synthetic */ float f$5;
    public final /* synthetic */ float f$6;
    public final /* synthetic */ float f$7;

    public /* synthetic */ CalendarActivity$MonthView$$ExternalSyntheticLambda0(CalendarActivity.MonthView monthView, CalendarActivity.RowAnimationValue rowAnimationValue, float f, float f2, float f3, float f4, float f5, float f6) {
        this.f$0 = monthView;
        this.f$1 = rowAnimationValue;
        this.f$2 = f;
        this.f$3 = f2;
        this.f$4 = f3;
        this.f$5 = f4;
        this.f$6 = f5;
        this.f$7 = f6;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.m2736lambda$animateRow$1$orgtelegramuiCalendarActivity$MonthView(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, valueAnimator);
    }
}
