package org.telegram.tgnet;

import j$.util.Comparator;
import j$.util.function.Function;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import java.util.Comparator;
import org.telegram.tgnet.ConnectionsManager;

/* renamed from: org.telegram.tgnet.-$$Lambda$ConnectionsManager$GoogleDnsLoadTask$5CfuSkds626j4zMK1LBk4I-jQL4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ConnectionsManager$GoogleDnsLoadTask$5CfuSkds626j4zMK1LBk4IjQL4 implements Comparator, j$.util.Comparator {
    public static final /* synthetic */ $$Lambda$ConnectionsManager$GoogleDnsLoadTask$5CfuSkds626j4zMK1LBk4IjQL4 INSTANCE = new $$Lambda$ConnectionsManager$GoogleDnsLoadTask$5CfuSkds626j4zMK1LBk4IjQL4();

    private /* synthetic */ $$Lambda$ConnectionsManager$GoogleDnsLoadTask$5CfuSkds626j4zMK1LBk4IjQL4() {
    }

    public final int compare(Object obj, Object obj2) {
        return ConnectionsManager.GoogleDnsLoadTask.lambda$doInBackground$0((String) obj, (String) obj2);
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
