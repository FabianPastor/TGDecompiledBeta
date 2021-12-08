package j$.time.format;

import j$.time.chrono.Chronology;
import j$.time.format.DateTimeFormatterBuilder;
import j$.util.function.Consumer;

public final /* synthetic */ class DateTimeFormatterBuilder$ReducedPrinterParser$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ DateTimeFormatterBuilder.ReducedPrinterParser f$0;
    public final /* synthetic */ DateTimeParseContext f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ int f$4;

    public /* synthetic */ DateTimeFormatterBuilder$ReducedPrinterParser$$ExternalSyntheticLambda0(DateTimeFormatterBuilder.ReducedPrinterParser reducedPrinterParser, DateTimeParseContext dateTimeParseContext, long j, int i, int i2) {
        this.f$0 = reducedPrinterParser;
        this.f$1 = dateTimeParseContext;
        this.f$2 = j;
        this.f$3 = i;
        this.f$4 = i2;
    }

    public final void accept(Object obj) {
        this.f$0.m0xdf3a601e(this.f$1, this.f$2, this.f$3, this.f$4, (Chronology) obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }
}
