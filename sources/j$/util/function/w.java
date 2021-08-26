package j$.util.function;

import j$.lang.d;

public final /* synthetic */ class w implements x {
    public final /* synthetic */ int a;
    public final /* synthetic */ x b;
    public final /* synthetic */ x c;

    public /* synthetic */ w(x xVar, x xVar2, int i) {
        this.a = i;
        if (i != 1) {
            this.b = xVar;
            this.c = xVar2;
            return;
        }
        this.b = xVar;
        this.c = xVar2;
    }

    public x a(x xVar) {
        switch (this.a) {
            case 0:
                xVar.getClass();
                return new w(this, xVar, 1);
            default:
                xVar.getClass();
                return new w(this, xVar, 1);
        }
    }

    public x c(x xVar) {
        switch (this.a) {
            case 0:
                xVar.getClass();
                return new w(this, xVar, 0);
            default:
                xVar.getClass();
                return new w(this, xVar, 0);
        }
    }

    public x negate() {
        switch (this.a) {
            case 0:
                return new CLASSNAMEa(this);
            default:
                return new CLASSNAMEa(this);
        }
    }

    public final boolean test(Object obj) {
        switch (this.a) {
            case 0:
                return d.b(this.b, this.c, obj);
            default:
                return this.b.test(obj) || this.c.test(obj);
        }
    }
}
