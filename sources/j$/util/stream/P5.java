package j$.util.stream;

import j$.util.CLASSNAMEk;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

final class P5 extends H5 {
    private ArrayList d;

    P5(CLASSNAMEt5 t5Var, Comparator comparator) {
        super(t5Var, comparator);
    }

    public void accept(Object obj) {
        this.d.add(obj);
    }

    public void m() {
        CLASSNAMEk.B(this.d, this.b);
        this.a.n((long) this.d.size());
        if (!this.c) {
            ArrayList arrayList = this.d;
            CLASSNAMEt5 t5Var = this.a;
            t5Var.getClass();
            CLASSNAMEk.s(arrayList, new CLASSNAMEb(t5Var));
        } else {
            Iterator it = this.d.iterator();
            while (it.hasNext()) {
                Object next = it.next();
                if (this.a.p()) {
                    break;
                }
                this.a.accept(next);
            }
        }
        this.a.m();
        this.d = null;
    }

    public void n(long j) {
        ArrayList arrayList;
        if (j < NUM) {
            if (j >= 0) {
                int i = (int) j;
            } else {
                arrayList = new ArrayList();
            }
            this.d = arrayList;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }
}
