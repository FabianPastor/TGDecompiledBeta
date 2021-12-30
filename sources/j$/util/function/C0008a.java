package j$.util.function;

import j$.util.concurrent.a;
import j$.util.function.Predicate;
import java.util.Comparator;

/* renamed from: j$.util.function.a  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEa implements CLASSNAMEb, Predicate {
    public final /* synthetic */ int a;
    public final /* synthetic */ Object b;

    public /* synthetic */ CLASSNAMEa(Predicate predicate) {
        this.a = 2;
        this.b = predicate;
    }

    public /* synthetic */ Predicate and(Predicate predicate) {
        return Predicate.CC.$default$and(this, predicate);
    }

    public BiFunction andThen(Function function) {
        switch (this.a) {
            case 0:
                function.getClass();
                return new a((BiFunction) this, function);
            default:
                function.getClass();
                return new a((BiFunction) this, function);
        }
    }

    public Object apply(Object obj, Object obj2) {
        switch (this.a) {
            case 0:
                return ((Comparator) this.b).compare(obj, obj2) >= 0 ? obj : obj2;
            default:
                return ((Comparator) this.b).compare(obj, obj2) <= 0 ? obj : obj2;
        }
    }

    public /* synthetic */ Predicate negate() {
        return Predicate.CC.$default$negate(this);
    }

    public /* synthetic */ Predicate or(Predicate predicate) {
        return Predicate.CC.$default$or(this, predicate);
    }

    public boolean test(Object obj) {
        return !((Predicate) this.b).test(obj);
    }

    public /* synthetic */ CLASSNAMEa(Comparator comparator, int i) {
        this.a = i;
        if (i != 1) {
            this.b = comparator;
        } else {
            this.b = comparator;
        }
    }
}
