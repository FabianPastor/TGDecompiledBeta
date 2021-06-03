package j$.util.stream;

import j$.time.a;
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

    public void l() {
        a.B(this.d, this.b);
        this.a.m((long) this.d.size());
        if (!this.c) {
            ArrayList arrayList = this.d;
            A2 a2 = this.a;
            a2.getClass();
            a.r(arrayList, new CLASSNAMEb(a2));
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
        this.a.l();
        this.d = null;
    }

    public void m(long j) {
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
