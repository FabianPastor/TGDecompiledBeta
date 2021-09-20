package j$.util.function;

import j$.lang.d;

public final /* synthetic */ class x implements y {
    public final /* synthetic */ int a;
    public final /* synthetic */ y b;
    public final /* synthetic */ y c;

    public /* synthetic */ x(y yVar, y yVar2, int i) {
        this.a = i;
        if (i != 1) {
            this.b = yVar;
            this.c = yVar2;
            return;
        }
        this.b = yVar;
        this.c = yVar2;
    }

    public y a(y yVar) {
        switch (this.a) {
            case 0:
                yVar.getClass();
                return new x(this, yVar, 1);
            default:
                yVar.getClass();
                return new x(this, yVar, 1);
        }
    }

    public y b(y yVar) {
        switch (this.a) {
            case 0:
                yVar.getClass();
                return new x(this, yVar, 0);
            default:
                yVar.getClass();
                return new x(this, yVar, 0);
        }
    }

    public y negate() {
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
