package org.telegram.ui.Adapters;

import j$.util.Comparator;
import j$.util.function.Function;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import java.util.Comparator;
import org.telegram.ui.Adapters.DialogsSearchAdapter;

/* renamed from: org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$_pBPL6sPCHJiVM9cPl1od_rxvJg  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$DialogsSearchAdapter$_pBPL6sPCHJiVM9cPl1od_rxvJg implements Comparator {
    public static final /* synthetic */ $$Lambda$DialogsSearchAdapter$_pBPL6sPCHJiVM9cPl1od_rxvJg INSTANCE = new $$Lambda$DialogsSearchAdapter$_pBPL6sPCHJiVM9cPl1od_rxvJg();

    private /* synthetic */ $$Lambda$DialogsSearchAdapter$_pBPL6sPCHJiVM9cPl1od_rxvJg() {
    }

    public final int compare(Object obj, Object obj2) {
        return DialogsSearchAdapter.lambda$null$8((DialogsSearchAdapter.DialogSearchResult) obj, (DialogsSearchAdapter.DialogSearchResult) obj2);
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
