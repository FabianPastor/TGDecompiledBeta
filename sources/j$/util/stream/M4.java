package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.z;
import j$.util.y;

final class M4 extends CLASSNAMEg4 {
    M4(CLASSNAMEz2 z2Var, z zVar, boolean z) {
        super(z2Var, zVar, z);
    }

    M4(CLASSNAMEz2 z2Var, y yVar, boolean z) {
        super(z2Var, yVar, z);
    }

    public boolean b(Consumer consumer) {
        Object obj;
        consumer.getClass();
        boolean a = a();
        if (a) {
            CLASSNAMEb4 b4Var = (CLASSNAMEb4) this.h;
            long j = this.g;
            if (b4Var.c == 0) {
                if (j < ((long) b4Var.b)) {
                    obj = b4Var.e[(int) j];
                } else {
                    throw new IndexOutOfBoundsException(Long.toString(j));
                }
            } else if (j < b4Var.count()) {
                int i = 0;
                while (i <= b4Var.c) {
                    long[] jArr = b4Var.d;
                    long j2 = jArr[i];
                    Object[][] objArr = b4Var.f;
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
        this.b.u0(new L4(consumer), this.d);
        this.i = true;
    }

    /* access modifiers changed from: package-private */
    public void j() {
        CLASSNAMEb4 b4Var = new CLASSNAMEb4();
        this.h = b4Var;
        this.e = this.b.v0(new L4(b4Var));
        this.f = new CLASSNAMEb(this);
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEg4 l(y yVar) {
        return new M4(this.b, yVar, this.a);
    }
}
