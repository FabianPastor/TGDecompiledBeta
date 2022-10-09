package j$.util.stream;

import j$.util.function.Function;
import j$.util.function.ToIntFunction;
import j$.wrappers.CLASSNAMEb0;
import j$.wrappers.CLASSNAMEn0;
/* loaded from: classes2.dex */
class M extends K0 {
    public final /* synthetic */ int l = 1;
    final /* synthetic */ Object m;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public M(T t, AbstractCLASSNAMEc abstractCLASSNAMEc, EnumCLASSNAMEe4 enumCLASSNAMEe4, int i, j$.wrappers.G g) {
        super(abstractCLASSNAMEc, enumCLASSNAMEe4, i);
        this.m = g;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractCLASSNAMEc
    public InterfaceCLASSNAMEm3 H0(int i, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        switch (this.l) {
            case 0:
                return new J(this, interfaceCLASSNAMEm3);
            case 1:
                return new F0(this, interfaceCLASSNAMEm3);
            case 2:
                return new F0(this, interfaceCLASSNAMEm3, (j$.lang.a) null);
            case 3:
                return new F0(this, interfaceCLASSNAMEm3, (j$.lang.b) null);
            case 4:
                return new F0(this, interfaceCLASSNAMEm3, (j$.lang.c) null);
            case 5:
                return new Z0(this, interfaceCLASSNAMEm3);
            case 6:
                return new Y2(this, interfaceCLASSNAMEm3);
            default:
                return new r(this, interfaceCLASSNAMEm3);
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public M(L0 l0, AbstractCLASSNAMEc abstractCLASSNAMEc, EnumCLASSNAMEe4 enumCLASSNAMEe4, int i, j$.util.function.l lVar) {
        super(abstractCLASSNAMEc, enumCLASSNAMEe4, i);
        this.m = lVar;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public M(L0 l0, AbstractCLASSNAMEc abstractCLASSNAMEc, EnumCLASSNAMEe4 enumCLASSNAMEe4, int i, j$.util.function.m mVar) {
        super(abstractCLASSNAMEc, enumCLASSNAMEe4, i);
        this.m = mVar;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public M(L0 l0, AbstractCLASSNAMEc abstractCLASSNAMEc, EnumCLASSNAMEe4 enumCLASSNAMEe4, int i, j$.wrappers.V v) {
        super(abstractCLASSNAMEc, enumCLASSNAMEe4, i);
        this.m = v;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public M(L0 l0, AbstractCLASSNAMEc abstractCLASSNAMEc, EnumCLASSNAMEe4 enumCLASSNAMEe4, int i, CLASSNAMEb0 CLASSNAMEb0) {
        super(abstractCLASSNAMEc, enumCLASSNAMEe4, i);
        this.m = CLASSNAMEb0;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public M(AbstractCLASSNAMEd1 abstractCLASSNAMEd1, AbstractCLASSNAMEc abstractCLASSNAMEc, EnumCLASSNAMEe4 enumCLASSNAMEe4, int i, CLASSNAMEn0 CLASSNAMEn0) {
        super(abstractCLASSNAMEc, enumCLASSNAMEe4, i);
        this.m = CLASSNAMEn0;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public M(AbstractCLASSNAMEe3 abstractCLASSNAMEe3, AbstractCLASSNAMEc abstractCLASSNAMEc, EnumCLASSNAMEe4 enumCLASSNAMEe4, int i, Function function) {
        super(abstractCLASSNAMEc, enumCLASSNAMEe4, i);
        this.m = function;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public M(AbstractCLASSNAMEe3 abstractCLASSNAMEe3, AbstractCLASSNAMEc abstractCLASSNAMEc, EnumCLASSNAMEe4 enumCLASSNAMEe4, int i, ToIntFunction toIntFunction) {
        super(abstractCLASSNAMEc, enumCLASSNAMEe4, i);
        this.m = toIntFunction;
    }
}
