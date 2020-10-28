package j$.util.function;

import java.util.Comparator;

/* renamed from: j$.util.function.d  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEd implements n {
    public final /* synthetic */ Comparator a;

    public /* synthetic */ CLASSNAMEd(Comparator comparator) {
        this.a = comparator;
    }

    public BiFunction a(Function function) {
        function.getClass();
        return new CLASSNAMEb(this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return this.a.compare(obj, obj2) >= 0 ? obj : obj2;
    }
}
