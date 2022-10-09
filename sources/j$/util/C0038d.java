package j$.util;

import j$.util.function.Function;
import j$.util.function.ToIntFunction;
import java.io.Serializable;
import java.util.Comparator;
/* renamed from: j$.util.d  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEd implements Comparator, Serializable {
    public final /* synthetic */ int a = 3;
    public final /* synthetic */ Object b;

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        switch (this.a) {
            case 0:
                Function function = (Function) this.b;
                return ((Comparable) function.apply(obj)).compareTo(function.apply(obj2));
            case 1:
                j$.util.function.z zVar = (j$.util.function.z) this.b;
                return Double.compare(zVar.applyAsDouble(obj), zVar.applyAsDouble(obj2));
            case 2:
                ToIntFunction toIntFunction = (ToIntFunction) this.b;
                int applyAsInt = toIntFunction.applyAsInt(obj);
                int applyAsInt2 = toIntFunction.applyAsInt(obj2);
                if (applyAsInt == applyAsInt2) {
                    return 0;
                }
                return applyAsInt < applyAsInt2 ? -1 : 1;
            default:
                j$.util.function.A a = (j$.util.function.A) this.b;
                return (a.applyAsLong(obj) > a.applyAsLong(obj2) ? 1 : (a.applyAsLong(obj) == a.applyAsLong(obj2) ? 0 : -1));
        }
    }
}
