package j$.util.stream;

import j$.util.AbstractCLASSNAMEa;
import j$.util.Collection$EL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
/* loaded from: classes2.dex */
final class N3 extends F3 {
    private ArrayList d;

    /* JADX INFO: Access modifiers changed from: package-private */
    public N3(InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3, Comparator comparator) {
        super(interfaceCLASSNAMEm3, comparator);
    }

    @Override // j$.util.function.Consumer
    public void accept(Object obj) {
        this.d.add(obj);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEi3, j$.util.stream.InterfaceCLASSNAMEm3
    public void m() {
        AbstractCLASSNAMEa.G(this.d, this.b);
        this.a.n(this.d.size());
        if (!this.c) {
            ArrayList arrayList = this.d;
            InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3 = this.a;
            interfaceCLASSNAMEm3.getClass();
            Collection$EL.a(arrayList, new CLASSNAMEb(interfaceCLASSNAMEm3));
        } else {
            Iterator it = this.d.iterator();
            while (it.hasNext()) {
                Object next = it.next();
                if (this.a.o()) {
                    break;
                }
                this.a.accept((InterfaceCLASSNAMEm3) next);
            }
        }
        this.a.m();
        this.d = null;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public void n(long j) {
        if (j < NUM) {
            this.d = j >= 0 ? new ArrayList((int) j) : new ArrayList();
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }
}
