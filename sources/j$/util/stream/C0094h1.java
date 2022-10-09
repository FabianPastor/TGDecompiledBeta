package j$.util.stream;

import j$.wrappers.CLASSNAMEj0;
/* renamed from: j$.util.stream.h1  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
class CLASSNAMEh1 extends AbstractCLASSNAMEj1 implements InterfaceCLASSNAMEl3 {
    final /* synthetic */ EnumCLASSNAMEk1 c;
    final /* synthetic */ CLASSNAMEj0 d;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public CLASSNAMEh1(EnumCLASSNAMEk1 enumCLASSNAMEk1, CLASSNAMEj0 CLASSNAMEj0) {
        super(enumCLASSNAMEk1);
        this.c = enumCLASSNAMEk1;
        this.d = CLASSNAMEj0;
    }

    @Override // j$.util.stream.AbstractCLASSNAMEj1, j$.util.stream.InterfaceCLASSNAMEm3, j$.util.stream.InterfaceCLASSNAMEl3, j$.util.function.q
    public void accept(long j) {
        boolean z;
        boolean z2;
        if (!this.a) {
            boolean b = this.d.b(j);
            z = this.c.a;
            if (b != z) {
                return;
            }
            this.a = true;
            z2 = this.c.b;
            this.b = z2;
        }
    }

    @Override // j$.util.function.Consumer
    /* renamed from: b */
    public /* synthetic */ void accept(Long l) {
        AbstractCLASSNAMEo1.c(this, l);
    }

    @Override // j$.util.function.q
    public j$.util.function.q f(j$.util.function.q qVar) {
        qVar.getClass();
        return new j$.util.function.p(this, qVar);
    }
}
