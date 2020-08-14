package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.CLASSNAMEv;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

/* renamed from: j$.util.stream.c6  reason: case insensitive filesystem */
final class CLASSNAMEc6 extends U5 {
    private ArrayList d;

    CLASSNAMEc6(G5 sink, Comparator comparator) {
        super(sink, comparator);
    }

    public void s(long size) {
        ArrayList arrayList;
        if (size < NUM) {
            if (size >= 0) {
                int i = (int) size;
            } else {
                arrayList = new ArrayList();
            }
            this.d = arrayList;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public void r() {
        CLASSNAMEv.b(this.d, this.b);
        this.a.s((long) this.d.size());
        if (!this.c) {
            ArrayList arrayList = this.d;
            G5 g5 = this.a;
            g5.getClass();
            CLASSNAMEk.a(arrayList, new CLASSNAMEg(g5));
        } else {
            Iterator it = this.d.iterator();
            while (it.hasNext()) {
                T t = it.next();
                if (this.a.u()) {
                    break;
                }
                this.a.accept(t);
            }
        }
        this.a.r();
        this.d = null;
    }

    public void accept(Object t) {
        this.d.add(t);
    }
}
