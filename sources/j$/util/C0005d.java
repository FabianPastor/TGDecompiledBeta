package j$.util;

import j$.util.function.A;
import j$.util.function.B;
import j$.util.function.Function;
import j$.util.function.z;
import java.io.Serializable;
import java.util.Comparator;

/* renamed from: j$.util.d  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEd implements Comparator, Serializable {
    public final /* synthetic */ int a = 2;
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
                z zVar = (z) this.b;
                return Double.compare(zVar.applyAsDouble(obj), zVar.applyAsDouble(obj2));
            case 2:
                A a2 = (A) this.b;
                int applyAsInt = a2.applyAsInt(obj);
                int applyAsInt2 = a2.applyAsInt(obj2);
                if (applyAsInt == applyAsInt2) {
                    return 0;
                }
                return applyAsInt < applyAsInt2 ? -1 : 1;
            default:
                B b2 = (B) this.b;
                return (b2.applyAsLong(obj) > b2.applyAsLong(obj2) ? 1 : (b2.applyAsLong(obj) == b2.applyAsLong(obj2) ? 0 : -1));
        }
    }

    public /* synthetic */ CLASSNAMEd(z zVar) {
        this.b = zVar;
    }

    public /* synthetic */ CLASSNAMEd(A a2) {
        this.b = a2;
    }

    public /* synthetic */ CLASSNAMEd(B b2) {
        this.b = b2;
    }
}
