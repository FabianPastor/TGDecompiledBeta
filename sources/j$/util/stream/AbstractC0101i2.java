package j$.util.stream;

import java.util.Deque;
/* renamed from: j$.util.stream.i2  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
abstract class AbstractCLASSNAMEi2 extends AbstractCLASSNAMEk2 implements j$.util.w {
    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractCLASSNAMEi2(InterfaceCLASSNAMEz1 interfaceCLASSNAMEz1) {
        super(interfaceCLASSNAMEz1);
    }

    @Override // j$.util.w
    /* renamed from: forEachRemaining */
    public void e(Object obj) {
        if (this.a == null) {
            return;
        }
        if (this.d != null) {
            do {
            } while (k(obj));
            return;
        }
        j$.util.u uVar = this.c;
        if (uVar != null) {
            ((j$.util.w) uVar).forEachRemaining(obj);
            return;
        }
        Deque f = f();
        while (true) {
            InterfaceCLASSNAMEz1 interfaceCLASSNAMEz1 = (InterfaceCLASSNAMEz1) a(f);
            if (interfaceCLASSNAMEz1 == null) {
                this.a = null;
                return;
            }
            interfaceCLASSNAMEz1.g(obj);
        }
    }

    @Override // j$.util.w
    /* renamed from: tryAdvance */
    public boolean k(Object obj) {
        InterfaceCLASSNAMEz1 interfaceCLASSNAMEz1;
        if (!h()) {
            return false;
        }
        boolean tryAdvance = ((j$.util.w) this.d).tryAdvance(obj);
        if (!tryAdvance) {
            if (this.c == null && (interfaceCLASSNAMEz1 = (InterfaceCLASSNAMEz1) a(this.e)) != null) {
                j$.util.w mo285spliterator = interfaceCLASSNAMEz1.mo285spliterator();
                this.d = mo285spliterator;
                return mo285spliterator.tryAdvance(obj);
            }
            this.a = null;
        }
        return tryAdvance;
    }
}
