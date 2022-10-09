package j$.util.stream;

import j$.util.function.Function;
/* renamed from: j$.util.stream.a3  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
class CLASSNAMEa3 extends AbstractCLASSNAMEd3 {
    public final /* synthetic */ int l;
    final /* synthetic */ Function m;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public CLASSNAMEa3(AbstractCLASSNAMEe3 abstractCLASSNAMEe3, AbstractCLASSNAMEc abstractCLASSNAMEc, EnumCLASSNAMEe4 enumCLASSNAMEe4, int i, Function function, int i2) {
        super(abstractCLASSNAMEc, enumCLASSNAMEe4, i);
        this.l = i2;
        if (i2 != 1) {
            this.m = function;
            return;
        }
        this.m = function;
        super(abstractCLASSNAMEc, enumCLASSNAMEe4, i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractCLASSNAMEc
    public InterfaceCLASSNAMEm3 H0(int i, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        switch (this.l) {
            case 0:
                return new Y2(this, interfaceCLASSNAMEm3);
            default:
                return new Y2(this, interfaceCLASSNAMEm3, (j$.lang.a) null);
        }
    }
}
