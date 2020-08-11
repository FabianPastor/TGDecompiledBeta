package org.telegram.ui.Adapters;

import j$.util.Comparator;
import j$.util.function.Function;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import java.util.Comparator;
import org.telegram.ui.Adapters.SearchAdapterHelper;

/* renamed from: org.telegram.ui.Adapters.-$$Lambda$SearchAdapterHelper$lNn-ZPLbXEKTf0Mjca-3cj25zrw  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$SearchAdapterHelper$lNnZPLbXEKTf0Mjca3cj25zrw implements Comparator {
    public static final /* synthetic */ $$Lambda$SearchAdapterHelper$lNnZPLbXEKTf0Mjca3cj25zrw INSTANCE = new $$Lambda$SearchAdapterHelper$lNnZPLbXEKTf0Mjca3cj25zrw();

    private /* synthetic */ $$Lambda$SearchAdapterHelper$lNnZPLbXEKTf0Mjca3cj25zrw() {
    }

    public final int compare(Object obj, Object obj2) {
        return SearchAdapterHelper.lambda$null$4((SearchAdapterHelper.HashtagObject) obj, (SearchAdapterHelper.HashtagObject) obj2);
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
