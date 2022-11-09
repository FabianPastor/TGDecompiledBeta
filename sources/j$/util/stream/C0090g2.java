package j$.util.stream;

import j$.util.AbstractCLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.u;
/* renamed from: j$.util.stream.g2  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
final class CLASSNAMEg2 extends AbstractCLASSNAMEi2 implements u.a {
    /* JADX INFO: Access modifiers changed from: package-private */
    public CLASSNAMEg2(InterfaceCLASSNAMEw1 interfaceCLASSNAMEw1) {
        super(interfaceCLASSNAMEw1);
    }

    @Override // j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return AbstractCLASSNAMEa.k(this, consumer);
    }

    @Override // j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        AbstractCLASSNAMEa.c(this, consumer);
    }
}
