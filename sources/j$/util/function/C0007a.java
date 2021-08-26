package j$.util.function;

import j$.util.concurrent.a;
import java.util.Comparator;

/* renamed from: j$.util.function.a  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEa implements CLASSNAMEb, x {
    public final /* synthetic */ int a;
    public final /* synthetic */ Object b;

    public /* synthetic */ CLASSNAMEa(x xVar) {
        this.a = 2;
        this.b = xVar;
    }

    public x a(x xVar) {
        xVar.getClass();
        return new w(this, xVar, 1);
    }

    public Object apply(Object obj, Object obj2) {
        switch (this.a) {
            case 0:
                return ((Comparator) this.b).compare(obj, obj2) >= 0 ? obj : obj2;
            default:
                return ((Comparator) this.b).compare(obj, obj2) <= 0 ? obj : obj2;
        }
    }

    public BiFunction b(Function function) {
        switch (this.a) {
            case 0:
                function.getClass();
                return new a((BiFunction) this, function);
            default:
                function.getClass();
                return new a((BiFunction) this, function);
        }
    }

    public x c(x xVar) {
        xVar.getClass();
        return new w(this, xVar, 0);
    }

    public x negate() {
        return new CLASSNAMEa(this);
    }

    public boolean test(Object obj) {
        return !((x) this.b).test(obj);
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
