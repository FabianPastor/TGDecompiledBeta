package j$.util;

import j$.util.function.A;
import j$.util.function.B;
import j$.util.function.C;
import j$.util.function.Function;
import java.io.Serializable;
import java.util.Comparator;

/* renamed from: j$.util.d  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEd implements Comparator, Serializable {
    public final /* synthetic */ int a = 1;
    public final /* synthetic */ Object b;

    public /* synthetic */ CLASSNAMEd(Function function) {
        this.b = function;
    }

    public final int compare(Object obj, Object obj2) {
        switch (this.a) {
            case 0:
                Function function = (Function) this.b;
                return ((Comparable) function.apply(obj)).compareTo(function.apply(obj2));
            case 1:
                A a2 = (A) this.b;
                return Double.compare(a2.applyAsDouble(obj), a2.applyAsDouble(obj2));
            case 2:
                B b2 = (B) this.b;
                int applyAsInt = b2.applyAsInt(obj);
                int applyAsInt2 = b2.applyAsInt(obj2);
                if (applyAsInt == applyAsInt2) {
                    return 0;
                }
                return applyAsInt < applyAsInt2 ? -1 : 1;
            default:
                C c = (C) this.b;
                return (c.applyAsLong(obj) > c.applyAsLong(obj2) ? 1 : (c.applyAsLong(obj) == c.applyAsLong(obj2) ? 0 : -1));
        }
    }

    public /* synthetic */ CLASSNAMEd(A a2) {
        this.b = a2;
    }

    public /* synthetic */ CLASSNAMEd(B b2) {
        this.b = b2;
    }

    public /* synthetic */ CLASSNAMEd(C c) {
        this.b = c;
    }
}
