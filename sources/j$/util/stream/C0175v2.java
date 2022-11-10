package j$.util.stream;
/* renamed from: j$.util.stream.v2  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
class CLASSNAMEv2 extends AbstractCLASSNAMEw2 {
    public final /* synthetic */ int c;
    private final Object d;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public CLASSNAMEv2(InterfaceCLASSNAMEz1 interfaceCLASSNAMEz1, Object obj, int i) {
        super(interfaceCLASSNAMEz1, i);
        this.c = 0;
        this.d = obj;
    }

    @Override // j$.util.stream.AbstractCLASSNAMEw2
    void a() {
        switch (this.c) {
            case 0:
                ((InterfaceCLASSNAMEz1) this.a).d(this.d, this.b);
                return;
            default:
                this.a.i((Object[]) this.d, this.b);
                return;
        }
    }

    @Override // j$.util.stream.AbstractCLASSNAMEw2
    AbstractCLASSNAMEw2 b(int i, int i2) {
        switch (this.c) {
            case 0:
                return new CLASSNAMEv2(this, ((InterfaceCLASSNAMEz1) this.a).moNUMb(i), i2);
            default:
                return new CLASSNAMEv2(this, this.a.moNUMb(i), i2);
        }
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ CLASSNAMEv2(InterfaceCLASSNAMEz1 interfaceCLASSNAMEz1, Object obj, int i, B1 b1) {
        this(interfaceCLASSNAMEz1, obj, i);
        this.c = 0;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public CLASSNAMEv2(A1 a1, Object[] objArr, int i, B1 b1) {
        super(a1, i);
        this.c = 1;
        this.c = 1;
        this.d = objArr;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public CLASSNAMEv2(CLASSNAMEv2 CLASSNAMEv2, InterfaceCLASSNAMEz1 interfaceCLASSNAMEz1, int i) {
        super(CLASSNAMEv2, interfaceCLASSNAMEz1, i);
        this.c = 0;
        this.d = CLASSNAMEv2.d;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public CLASSNAMEv2(CLASSNAMEv2 CLASSNAMEv2, A1 a1, int i) {
        super(CLASSNAMEv2, a1, i);
        this.c = 1;
        this.d = (Object[]) CLASSNAMEv2.d;
    }
}
