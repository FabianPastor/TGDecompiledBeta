package j$.util.function;

import j$.util.concurrent.a;
import java.util.Comparator;

/* renamed from: j$.util.function.a  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEa implements CLASSNAMEb, y {
    public final /* synthetic */ int a;
    public final /* synthetic */ Object b;

    public /* synthetic */ CLASSNAMEa(y yVar) {
        this.a = 2;
        this.b = yVar;
    }

    public y a(y yVar) {
        yVar.getClass();
        return new x(this, yVar, 1);
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

    public y b(y yVar) {
        yVar.getClass();
        return new x(this, yVar, 0);
    }

    public y negate() {
        return new CLASSNAMEa(this);
    }

    public boolean test(Object obj) {
        return !((y) this.b).test(obj);
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
