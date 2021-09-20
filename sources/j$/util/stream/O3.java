package j$.util.stream;

import j$.util.CLASSNAMEa;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

final class O3 extends G3 {
    private ArrayList d;

    O3(CLASSNAMEn3 n3Var, Comparator comparator) {
        super(n3Var, comparator);
    }

    public void accept(Object obj) {
        this.d.add(obj);
    }

    public void m() {
        CLASSNAMEa.H(this.d, this.b);
        this.a.n((long) this.d.size());
        if (!this.c) {
            ArrayList arrayList = this.d;
            CLASSNAMEn3 n3Var = this.a;
            n3Var.getClass();
            CLASSNAMEa.y(arrayList, new CLASSNAMEb(n3Var));
        } else {
            Iterator it = this.d.iterator();
            while (it.hasNext()) {
                Object next = it.next();
                if (this.a.o()) {
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
