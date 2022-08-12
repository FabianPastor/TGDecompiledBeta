package j$.util.stream;

/* renamed from: j$.util.stream.v2  reason: case insensitive filesystem */
class CLASSNAMEv2 extends CLASSNAMEw2 {
    public final /* synthetic */ int c;
    private final Object d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public CLASSNAMEv2(CLASSNAMEz1 z1Var, Object obj, int i) {
        super(z1Var, i);
        this.c = 0;
        this.d = obj;
    }

    /* access modifiers changed from: package-private */
    public void a() {
        switch (this.c) {
            case 0:
                ((CLASSNAMEz1) this.a).d(this.d, this.b);
                return;
            default:
                this.a.i((Object[]) this.d, this.b);
                return;
        }
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEw2 b(int i, int i2) {
        switch (this.c) {
            case 0:
                return new CLASSNAMEv2(this, ((CLASSNAMEz1) this.a).b(i), i2);
            default:
                return new CLASSNAMEv2(this, this.a.b(i), i2);
        }
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public /* synthetic */ CLASSNAMEv2(CLASSNAMEz1 z1Var, Object obj, int i, B1 b1) {
        this(z1Var, obj, i);
        this.c = 0;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public CLASSNAMEv2(A1 a1, Object[] objArr, int i, B1 b1) {
        super(a1, i);
        this.c = 1;
        this.c = 1;
        this.d = objArr;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public CLASSNAMEv2(CLASSNAMEv2 v2Var, CLASSNAMEz1 z1Var, int i) {
        super(v2Var, z1Var, i);
        this.c = 0;
        this.d = v2Var.d;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public CLASSNAMEv2(CLASSNAMEv2 v2Var, A1 a1, int i) {
        super(v2Var, a1, i);
        this.c = 1;
        this.d = (Object[]) v2Var.d;
    }
}
