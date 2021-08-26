package j$.util.stream;

import j$.lang.a;
import j$.util.function.Function;

/* renamed from: j$.util.stream.b3  reason: case insensitive filesystem */
class CLASSNAMEb3 extends CLASSNAMEe3 {
    public final /* synthetic */ int l;
    final /* synthetic */ Function m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public CLASSNAMEb3(CLASSNAMEf3 f3Var, CLASSNAMEc cVar, CLASSNAMEf4 f4Var, int i, Function function, int i2) {
        super(cVar, f4Var, i);
        this.l = i2;
        if (i2 != 1) {
            this.m = function;
            return;
        }
        this.m = function;
        super(cVar, f4Var, i);
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEn3 H0(int i, CLASSNAMEn3 n3Var) {
        switch (this.l) {
            case 0:
                return new Z2(this, n3Var);
            default:
                return new Z2(this, n3Var, (a) null);
        }
    }
}
