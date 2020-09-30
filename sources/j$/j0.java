package j$;

import j$.util.stream.CLASSNAMEl1;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.stream.BaseStream;

public final /* synthetic */ class j0 implements BaseStream {
    final /* synthetic */ CLASSNAMEl1 a;

    private /* synthetic */ j0(CLASSNAMEl1 l1Var) {
        this.a = l1Var;
    }

    public static /* synthetic */ BaseStream c(CLASSNAMEl1 l1Var) {
        if (l1Var == null) {
            return null;
        }
        return new j0(l1Var);
    }

    public /* synthetic */ void close() {
        this.a.close();
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ Iterator iterator() {
        return this.a.iterator();
    }

    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return c(this.a.onClose(runnable));
    }

    public /* synthetic */ BaseStream parallel() {
        return c(this.a.parallel());
    }

    public /* synthetic */ BaseStream sequential() {
        return c(this.a.sequential());
    }

    public /* synthetic */ Spliterator spliterator() {
        return CLASSNAMEi.a(this.a.spliterator());
    }

    public /* synthetic */ BaseStream unordered() {
        return c(this.a.unordered());
    }
}
