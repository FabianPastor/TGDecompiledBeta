package j$.util.stream;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
final class L4 extends AbstractCLASSNAMEf4 {
    /* JADX INFO: Access modifiers changed from: package-private */
    public L4(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.function.y yVar, boolean z) {
        super(abstractCLASSNAMEy2, yVar, z);
    }

    L4(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar, boolean z) {
        super(abstractCLASSNAMEy2, uVar, z);
    }

    @Override // j$.util.u
    public boolean b(Consumer consumer) {
        Object obj;
        consumer.getClass();
        boolean a = a();
        if (a) {
            CLASSNAMEa4 CLASSNAMEa4 = (CLASSNAMEa4) this.h;
            long j = this.g;
            if (CLASSNAMEa4.c != 0) {
                if (j >= CLASSNAMEa4.count()) {
                    throw new IndexOutOfBoundsException(Long.toString(j));
                }
                for (int i = 0; i <= CLASSNAMEa4.c; i++) {
                    long[] jArr = CLASSNAMEa4.d;
                    long j2 = jArr[i];
                    Object[][] objArr = CLASSNAMEa4.f;
                    if (j < j2 + objArr[i].length) {
                        obj = objArr[i][(int) (j - jArr[i])];
                    }
                }
                throw new IndexOutOfBoundsException(Long.toString(j));
            } else if (j >= CLASSNAMEa4.b) {
                throw new IndexOutOfBoundsException(Long.toString(j));
            } else {
                obj = CLASSNAMEa4.e[(int) j];
            }
            consumer.accept(obj);
        }
        return a;
    }

    @Override // j$.util.u
    public void forEachRemaining(Consumer consumer) {
        if (this.h != null || this.i) {
            do {
            } while (b(consumer));
            return;
        }
        consumer.getClass();
        h();
        this.b.u0(new K4(consumer), this.d);
        this.i = true;
    }

    @Override // j$.util.stream.AbstractCLASSNAMEf4
    void j() {
        CLASSNAMEa4 CLASSNAMEa4 = new CLASSNAMEa4();
        this.h = CLASSNAMEa4;
        this.e = this.b.v0(new K4(CLASSNAMEa4));
        this.f = new CLASSNAMEb(this);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEf4
    AbstractCLASSNAMEf4 l(j$.util.u uVar) {
        return new L4(this.b, uVar, this.a);
    }
}
