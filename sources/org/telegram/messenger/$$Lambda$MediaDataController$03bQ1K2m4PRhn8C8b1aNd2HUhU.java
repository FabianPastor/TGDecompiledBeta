package org.telegram.messenger;

import j$.util.Comparator;
import j$.util.function.Function;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import java.util.Comparator;
import org.telegram.tgnet.TLRPC$TL_topPeer;

/* renamed from: org.telegram.messenger.-$$Lambda$MediaDataController$03bQ1K2m4-PRhn8C8b1aNd2HUhU  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MediaDataController$03bQ1K2m4PRhn8C8b1aNd2HUhU implements Comparator, j$.util.Comparator {
    public static final /* synthetic */ $$Lambda$MediaDataController$03bQ1K2m4PRhn8C8b1aNd2HUhU INSTANCE = new $$Lambda$MediaDataController$03bQ1K2m4PRhn8C8b1aNd2HUhU();

    private /* synthetic */ $$Lambda$MediaDataController$03bQ1K2m4PRhn8C8b1aNd2HUhU() {
    }

    public final int compare(Object obj, Object obj2) {
        return MediaDataController.lambda$increaseInlineRaiting$95((TLRPC$TL_topPeer) obj, (TLRPC$TL_topPeer) obj2);
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
