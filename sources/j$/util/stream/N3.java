package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.Collection$EL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

final class N3 extends F3 {
    private ArrayList d;

    N3(CLASSNAMEm3 m3Var, Comparator comparator) {
        super(m3Var, comparator);
    }

    public void accept(Object obj) {
        this.d.add(obj);
    }

    public void m() {
        CLASSNAMEa.G(this.d, this.b);
        this.a.n((long) this.d.size());
        if (!this.c) {
            ArrayList arrayList = this.d;
            CLASSNAMEm3 m3Var = this.a;
            m3Var.getClass();
            Collection$EL.a(arrayList, new CLASSNAMEb(m3Var));
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
