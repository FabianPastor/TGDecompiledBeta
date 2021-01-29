package org.telegram.messenger;

import j$.util.Comparator;
import j$.util.function.Function;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import java.util.Comparator;
import org.telegram.messenger.ContactsController;

/* renamed from: org.telegram.messenger.-$$Lambda$ContactsController$blJ94lXmSDb1Y3assau-bI1Ozn4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ContactsController$blJ94lXmSDb1Y3assaubI1Ozn4 implements Comparator, j$.util.Comparator {
    public static final /* synthetic */ $$Lambda$ContactsController$blJ94lXmSDb1Y3assaubI1Ozn4 INSTANCE = new $$Lambda$ContactsController$blJ94lXmSDb1Y3assaubI1Ozn4();

    private /* synthetic */ $$Lambda$ContactsController$blJ94lXmSDb1Y3assaubI1Ozn4() {
    }

    public final int compare(Object obj, Object obj2) {
        return ContactsController.lambda$updateUnregisteredContacts$40((ContactsController.Contact) obj, (ContactsController.Contact) obj2);
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
