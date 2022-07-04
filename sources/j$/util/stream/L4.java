package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.y;
import j$.util.u;

final class L4 extends CLASSNAMEf4 {
    L4(CLASSNAMEy2 y2Var, y yVar, boolean z) {
        super(y2Var, yVar, z);
    }

    L4(CLASSNAMEy2 y2Var, u uVar, boolean z) {
        super(y2Var, uVar, z);
    }

    public boolean b(Consumer consumer) {
        Object obj;
        consumer.getClass();
        boolean a = a();
        if (a) {
            CLASSNAMEa4 a4Var = (CLASSNAMEa4) this.h;
            long j = this.g;
            if (a4Var.c == 0) {
                if (j < ((long) a4Var.b)) {
                    obj = a4Var.e[(int) j];
                } else {
                    throw new IndexOutOfBoundsException(Long.toString(j));
                }
            } else if (j < a4Var.count()) {
                int i = 0;
                while (i <= a4Var.c) {
                    long[] jArr = a4Var.d;
                    long j2 = jArr[i];
                    Object[][] objArr = a4Var.f;
                    if (j < j2 + ((long) objArr[i].length)) {
                        obj = objArr[i][(int) (j - jArr[i])];
                    } else {
                        i++;
                    }
                }
                throw new IndexOutOfBoundsException(Long.toString(j));
            } else {
                throw new IndexOutOfBoundsException(Long.toString(j));
            }
            consumer.accept(obj);
        }
        return a;
    }

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

    /* access modifiers changed from: package-private */
    public void j() {
        CLASSNAMEa4 a4Var = new CLASSNAMEa4();
        this.h = a4Var;
        this.e = this.b.v0(new K4(a4Var));
        this.f = new CLASSNAMEb(this);
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEf4 l(u uVar) {
        return new L4(this.b, uVar, this.a);
    }
}
