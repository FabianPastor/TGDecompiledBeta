package j$.wrappers;

import j$.time.TimeConversions;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/* renamed from: j$.wrappers.$r8$wrapper$java$time$Clock$-WRP  reason: invalid class name */
/* compiled from: Clock */
public final /* synthetic */ class C$r8$wrapper$java$time$Clock$WRP extends Clock {
    final /* synthetic */ j$.time.Clock wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$time$Clock$WRP(j$.time.Clock clock) {
        this.wrappedValue = clock;
    }

    public static /* synthetic */ Clock convert(j$.time.Clock clock) {
        if (clock == null) {
            return null;
        }
        return clock instanceof C$r8$wrapper$java$time$Clock$VWRP ? ((C$r8$wrapper$java$time$Clock$VWRP) clock).wrappedValue : new C$r8$wrapper$java$time$Clock$WRP(clock);
    }

    public /* synthetic */ boolean equals(Object obj) {
        return this.wrappedValue.equals(obj);
    }

    public /* synthetic */ ZoneId getZone() {
        return TimeConversions.convert(this.wrappedValue.getZone());
    }

    public /* synthetic */ int hashCode() {
        return this.wrappedValue.hashCode();
    }

    public /* synthetic */ Instant instant() {
        return TimeConversions.convert(this.wrappedValue.instant());
    }

    public /* synthetic */ long millis() {
        return this.wrappedValue.millis();
    }

    public /* synthetic */ Clock withZone(ZoneId zoneId) {
        return convert(this.wrappedValue.withZone(TimeConversions.convert(zoneId)));
    }
}
