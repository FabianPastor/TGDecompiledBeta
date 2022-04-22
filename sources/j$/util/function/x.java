package j$.util.function;

import j$.util.function.Predicate;

public final /* synthetic */ class x implements Predicate {
    public final /* synthetic */ int a;
    public final /* synthetic */ Predicate b;
    public final /* synthetic */ Predicate c;

    public /* synthetic */ x(Predicate predicate, Predicate predicate2, int i) {
        this.a = i;
        if (i != 1) {
            this.b = predicate;
            this.c = predicate2;
            return;
        }
        this.b = predicate;
        this.c = predicate2;
    }

    public /* synthetic */ Predicate and(Predicate predicate) {
        switch (this.a) {
            case 0:
                return Predicate.CC.$default$and(this, predicate);
            default:
                return Predicate.CC.$default$and(this, predicate);
        }
    }

    public /* synthetic */ Predicate negate() {
        switch (this.a) {
            case 0:
                return Predicate.CC.$default$negate(this);
            default:
                return Predicate.CC.$default$negate(this);
        }
    }

    public /* synthetic */ Predicate or(Predicate predicate) {
        switch (this.a) {
            case 0:
                return Predicate.CC.$default$or(this, predicate);
            default:
                return Predicate.CC.$default$or(this, predicate);
        }
    }

    public final boolean test(Object obj) {
        switch (this.a) {
            case 0:
                Predicate predicate = this.b;
                Predicate predicate2 = this.c;
                if (!predicate.test(obj) || !predicate2.test(obj)) {
                    return false;
                }
                return true;
            default:
                Predicate predicate3 = this.b;
                Predicate predicate4 = this.c;
                if (predicate3.test(obj) || predicate4.test(obj)) {
                    return true;
                }
                return false;
        }
    }
}
