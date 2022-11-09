package j$.util.stream;

import j$.util.function.Consumer;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.b3  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public class CLASSNAMEb3 extends AbstractCLASSNAMEe3 {
    /* JADX INFO: Access modifiers changed from: package-private */
    public CLASSNAMEb3(j$.util.u uVar, int i, boolean z) {
        super(uVar, i, z);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc
    final boolean G0() {
        throw new UnsupportedOperationException();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractCLASSNAMEc
    public final InterfaceCLASSNAMEm3 H0(int i, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        throw new UnsupportedOperationException();
    }

    @Override // j$.util.stream.AbstractCLASSNAMEe3, j$.util.stream.Stream
    public void e(Consumer consumer) {
        if (!isParallel()) {
            J0().forEachRemaining(consumer);
            return;
        }
        consumer.getClass();
        x0(new CLASSNAMEn0(consumer, true));
    }

    @Override // j$.util.stream.AbstractCLASSNAMEe3, j$.util.stream.Stream
    public void forEach(Consumer consumer) {
        if (!isParallel()) {
            J0().forEachRemaining(consumer);
        } else {
            super.forEach(consumer);
        }
    }
}
