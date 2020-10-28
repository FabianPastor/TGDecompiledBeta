package org.telegram.ui.Components;

import j$.util.Comparator;
import j$.util.function.Function;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import java.util.Comparator;
import org.telegram.ui.Components.PollVotesAlert;

/* renamed from: org.telegram.ui.Components.-$$Lambda$PollVotesAlert$JlbsBgirEz4oQSTYz_nZssUAgSk  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$PollVotesAlert$JlbsBgirEz4oQSTYz_nZssUAgSk implements Comparator, j$.util.Comparator {
    public static final /* synthetic */ $$Lambda$PollVotesAlert$JlbsBgirEz4oQSTYz_nZssUAgSk INSTANCE = new $$Lambda$PollVotesAlert$JlbsBgirEz4oQSTYz_nZssUAgSk();

    private /* synthetic */ $$Lambda$PollVotesAlert$JlbsBgirEz4oQSTYz_nZssUAgSk() {
    }

    public final int compare(Object obj, Object obj2) {
        return PollVotesAlert.lambda$updateButtons$5((PollVotesAlert.Button) obj, (PollVotesAlert.Button) obj2);
    }

    public /* synthetic */ Comparator reversed() {
        return Comparator.CC.$default$reversed(this);
    }

    public /* synthetic */ java.util.Comparator thenComparing(Function function) {
        return Comparator.CC.$default$thenComparing((java.util.Comparator) this, function);
    }

    public /* synthetic */ java.util.Comparator thenComparing(Function function, java.util.Comparator comparator) {
        return Comparator.CC.$default$thenComparing(this, function, comparator);
    }

    public /* synthetic */ java.util.Comparator thenComparing(java.util.Comparator comparator) {
        return Comparator.CC.$default$thenComparing((java.util.Comparator) this, comparator);
    }

    public /* synthetic */ java.util.Comparator thenComparingDouble(ToDoubleFunction toDoubleFunction) {
        return Comparator.CC.$default$thenComparingDouble(this, toDoubleFunction);
    }

    public /* synthetic */ java.util.Comparator thenComparingInt(ToIntFunction toIntFunction) {
        return Comparator.CC.$default$thenComparingInt(this, toIntFunction);
    }

    public /* synthetic */ java.util.Comparator thenComparingLong(ToLongFunction toLongFunction) {
        return Comparator.CC.$default$thenComparingLong(this, toLongFunction);
    }
}
