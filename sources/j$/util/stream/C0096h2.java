package j$.util.stream;

import j$.util.AbstractCLASSNAMEa;
import j$.util.function.Consumer;
/* renamed from: j$.util.stream.h2  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
final class CLASSNAMEh2 extends AbstractCLASSNAMEi2 implements j$.util.v {
    /* JADX INFO: Access modifiers changed from: package-private */
    public CLASSNAMEh2(InterfaceCLASSNAMEy1 interfaceCLASSNAMEy1) {
        super(interfaceCLASSNAMEy1);
    }

    @Override // j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return AbstractCLASSNAMEa.l(this, consumer);
    }

    @Override // j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        AbstractCLASSNAMEa.d(this, consumer);
    }
}
