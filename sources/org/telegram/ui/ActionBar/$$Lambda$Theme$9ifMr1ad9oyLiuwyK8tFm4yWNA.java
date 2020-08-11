package org.telegram.ui.ActionBar;

import j$.util.Comparator;
import j$.util.function.Function;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import java.util.Comparator;
import org.telegram.ui.ActionBar.Theme;

/* renamed from: org.telegram.ui.ActionBar.-$$Lambda$Theme$9ifMr-1ad9oyLiuwyK8tFm4yWNA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$Theme$9ifMr1ad9oyLiuwyK8tFm4yWNA implements Comparator {
    public static final /* synthetic */ $$Lambda$Theme$9ifMr1ad9oyLiuwyK8tFm4yWNA INSTANCE = new $$Lambda$Theme$9ifMr1ad9oyLiuwyK8tFm4yWNA();

    private /* synthetic */ $$Lambda$Theme$9ifMr1ad9oyLiuwyK8tFm4yWNA() {
    }

    public final int compare(Object obj, Object obj2) {
        return Theme.lambda$sortThemes$1((Theme.ThemeInfo) obj, (Theme.ThemeInfo) obj2);
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
