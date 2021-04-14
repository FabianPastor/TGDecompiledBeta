package j$.util.stream;

import j$.util.k;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

final class N2<T> extends F2<T> {
    private ArrayList d;

    N2(A2 a2, Comparator comparator) {
        super(a2, comparator);
    }

    public void accept(Object obj) {
        this.d.add(obj);
    }

    public void m() {
        k.B(this.d, this.b);
        this.var_a.n((long) this.d.size());
        if (!this.c) {
            ArrayList arrayList = this.d;
            A2 a2 = this.var_a;
            a2.getClass();
            k.s(arrayList, new CLASSNAMEb(a2));
        } else {
            Iterator it = this.d.iterator();
            while (it.hasNext()) {
                Object next = it.next();
                if (this.var_a.p()) {
                    break;
                }
                this.var_a.accept(next);
            }
        }
        this.var_a.m();
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
