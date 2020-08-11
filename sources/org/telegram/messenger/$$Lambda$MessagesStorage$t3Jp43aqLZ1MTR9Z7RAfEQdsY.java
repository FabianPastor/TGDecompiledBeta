package org.telegram.messenger;

import j$.util.Comparator;
import j$.util.function.Function;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import java.util.Comparator;
import org.telegram.messenger.MessagesController;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesStorage$-t3Jp43aqLZ1-MTR9Z7RAfEQdsY  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesStorage$t3Jp43aqLZ1MTR9Z7RAfEQdsY implements Comparator {
    public static final /* synthetic */ $$Lambda$MessagesStorage$t3Jp43aqLZ1MTR9Z7RAfEQdsY INSTANCE = new $$Lambda$MessagesStorage$t3Jp43aqLZ1MTR9Z7RAfEQdsY();

    private /* synthetic */ $$Lambda$MessagesStorage$t3Jp43aqLZ1MTR9Z7RAfEQdsY() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$processLoadedFilterPeersInternal$35((MessagesController.DialogFilter) obj, (MessagesController.DialogFilter) obj2);
    }

    public /* synthetic */ Comparator<T> reversed() {
        return Comparator.CC.$default$reversed(this);
    }

    public /* synthetic */ <U extends Comparable<? super U>> java.util.Comparator<T> thenComparing(Function<? super T, ? extends U> function) {
        return Comparator.CC.$default$thenComparing((java.util.Comparator) this, (Function) function);
    }

    public /* synthetic */ <U> java.util.Comparator<T> thenComparing(Function<? super T, ? extends U> function, java.util.Comparator<? super U> comparator) {
        return Comparator.CC.$default$thenComparing(this, function, comparator);
    }

    public /* synthetic */ java.util.Comparator<T> thenComparing(java.util.Comparator<? super T> comparator) {
        return Comparator.CC.$default$thenComparing((java.util.Comparator) this, (java.util.Comparator) comparator);
    }

    public /* synthetic */ java.util.Comparator<T> thenComparingDouble(ToDoubleFunction<? super T> toDoubleFunction) {
        return Comparator.CC.$default$thenComparingDouble(this, toDoubleFunction);
    }

    public /* synthetic */ java.util.Comparator<T> thenComparingInt(ToIntFunction<? super T> toIntFunction) {
        return Comparator.CC.$default$thenComparingInt(this, toIntFunction);
    }

    public /* synthetic */ java.util.Comparator<T> thenComparingLong(ToLongFunction<? super T> toLongFunction) {
        return Comparator.CC.$default$thenComparingLong(this, toLongFunction);
    }
}
