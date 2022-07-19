package j$.util.stream;

import j$.lang.a;
import j$.util.function.Function;

/* renamed from: j$.util.stream.a3  reason: case insensitive filesystem */
class CLASSNAMEa3 extends CLASSNAMEd3 {
    public final /* synthetic */ int l;
    final /* synthetic */ Function m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public CLASSNAMEa3(CLASSNAMEe3 e3Var, CLASSNAMEc cVar, CLASSNAMEe4 e4Var, int i, Function function, int i2) {
        super(cVar, e4Var, i);
        this.l = i2;
        if (i2 != 1) {
            this.m = function;
            return;
        }
        this.m = function;
        super(cVar, e4Var, i);
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEm3 H0(int i, CLASSNAMEm3 m3Var) {
        switch (this.l) {
            case 0:
                return new Y2(this, m3Var);
            default:
                return new Y2(this, m3Var, (a) null);
        }
    }
}
