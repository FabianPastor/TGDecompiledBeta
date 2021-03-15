package j$.util.function;

import java.util.Comparator;

/* renamed from: j$.util.function.c  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEc implements n {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ Comparator var_a;

    public /* synthetic */ CLASSNAMEc(Comparator comparator) {
        this.var_a = comparator;
    }

    public BiFunction a(Function function) {
        function.getClass();
        return new CLASSNAMEb(this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return this.var_a.compare(obj, obj2) <= 0 ? obj : obj2;
    }
}
