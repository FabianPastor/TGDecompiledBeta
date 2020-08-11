package org.telegram.ui;

import j$.util.Comparator;
import j$.util.function.Function;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import java.util.Comparator;
import org.telegram.ui.CountrySelectActivity;

/* renamed from: org.telegram.ui.-$$Lambda$CountrySelectActivity$CountryAdapter$GbbT4_eUqPD5K07oYJV3z7sG8q4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$CountrySelectActivity$CountryAdapter$GbbT4_eUqPD5K07oYJV3z7sG8q4 implements Comparator {
    public static final /* synthetic */ $$Lambda$CountrySelectActivity$CountryAdapter$GbbT4_eUqPD5K07oYJV3z7sG8q4 INSTANCE = new $$Lambda$CountrySelectActivity$CountryAdapter$GbbT4_eUqPD5K07oYJV3z7sG8q4();

    private /* synthetic */ $$Lambda$CountrySelectActivity$CountryAdapter$GbbT4_eUqPD5K07oYJV3z7sG8q4() {
    }

    public final int compare(Object obj, Object obj2) {
        return ((CountrySelectActivity.Country) obj).name.compareTo(((CountrySelectActivity.Country) obj2).name);
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
