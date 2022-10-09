package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.Predicate;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class L extends AbstractCLASSNAMEd3 {
    public final /* synthetic */ int l = 1;
    final /* synthetic */ Object m;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public L(T t, AbstractCLASSNAMEc abstractCLASSNAMEc, EnumCLASSNAMEe4 enumCLASSNAMEe4, int i, j$.util.function.g gVar) {
        super(abstractCLASSNAMEc, enumCLASSNAMEe4, i);
        this.m = gVar;
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
                return new Z0(this, interfaceCLASSNAMEm3);
            case 3:
                return new Y2(this, interfaceCLASSNAMEm3);
            default:
                return new Y2(this, interfaceCLASSNAMEm3, (j$.lang.a) null);
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public L(L0 l0, AbstractCLASSNAMEc abstractCLASSNAMEc, EnumCLASSNAMEe4 enumCLASSNAMEe4, int i, j$.util.function.m mVar) {
        super(abstractCLASSNAMEc, enumCLASSNAMEe4, i);
        this.m = mVar;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public L(AbstractCLASSNAMEd1 abstractCLASSNAMEd1, AbstractCLASSNAMEc abstractCLASSNAMEc, EnumCLASSNAMEe4 enumCLASSNAMEe4, int i, j$.util.function.r rVar) {
        super(abstractCLASSNAMEc, enumCLASSNAMEe4, i);
        this.m = rVar;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public L(AbstractCLASSNAMEe3 abstractCLASSNAMEe3, AbstractCLASSNAMEc abstractCLASSNAMEc, EnumCLASSNAMEe4 enumCLASSNAMEe4, int i, Consumer consumer) {
        super(abstractCLASSNAMEc, enumCLASSNAMEe4, i);
        this.m = consumer;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public L(AbstractCLASSNAMEe3 abstractCLASSNAMEe3, AbstractCLASSNAMEc abstractCLASSNAMEc, EnumCLASSNAMEe4 enumCLASSNAMEe4, int i, Predicate predicate) {
        super(abstractCLASSNAMEc, enumCLASSNAMEe4, i);
        this.m = predicate;
    }
}
