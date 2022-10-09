package j$.util.stream;

import java.util.Comparator;
/* loaded from: classes2.dex */
abstract class F3 extends AbstractCLASSNAMEi3 {
    protected final Comparator b;
    protected boolean c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public F3(InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3, Comparator comparator) {
        super(interfaceCLASSNAMEm3);
        this.b = comparator;
    }

    @Override // j$.util.stream.AbstractCLASSNAMEi3, j$.util.stream.InterfaceCLASSNAMEm3
    public final boolean o() {
        this.c = true;
        return false;
    }
}
