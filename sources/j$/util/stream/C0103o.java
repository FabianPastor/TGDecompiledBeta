package j$.util.stream;

import j$.util.concurrent.ConcurrentHashMap;
import j$.util.function.BiConsumer;
import j$.util.function.Consumer;
import j$.util.function.x;
import j$.util.function.y;
import j$.wrappers.CLASSNAMEj0;
import j$.wrappers.E;
import j$.wrappers.V;
import java.util.concurrent.atomic.AtomicBoolean;

/* renamed from: j$.util.stream.o  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEo implements Consumer, y {
    public final /* synthetic */ int a = 5;
    public final /* synthetic */ Object b;
    public final /* synthetic */ Object c;

    public /* synthetic */ CLASSNAMEo(BiConsumer biConsumer, Object obj) {
        this.b = biConsumer;
        this.c = obj;
    }

    public void accept(Object obj) {
        switch (this.a) {
            case 0:
                AtomicBoolean atomicBoolean = (AtomicBoolean) this.b;
                ConcurrentHashMap concurrentHashMap = (ConcurrentHashMap) this.c;
                if (obj == null) {
                    atomicBoolean.set(true);
                    return;
                } else {
                    concurrentHashMap.putIfAbsent(obj, Boolean.TRUE);
                    return;
                }
            case 5:
                ((BiConsumer) this.b).accept(this.c, obj);
                return;
            default:
                ((CLASSNAMEn4) this.b).f((Consumer) this.c, obj);
                return;
        }
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        switch (this.a) {
            case 0:
                return Consumer.CC.$default$andThen(this, consumer);
            case 5:
                return Consumer.CC.$default$andThen(this, consumer);
            default:
                return Consumer.CC.$default$andThen(this, consumer);
        }
    }

    public Object get() {
        switch (this.a) {
            case 1:
                return new CLASSNAMEj1((CLASSNAMEl1) this.b, (E) this.c);
            case 2:
                return new CLASSNAMEh1((CLASSNAMEl1) this.b, (V) this.c);
            case 3:
                return new CLASSNAMEi1((CLASSNAMEl1) this.b, (CLASSNAMEj0) this.c);
            default:
                return new CLASSNAMEg1((CLASSNAMEl1) this.b, (x) this.c);
        }
    }

    public /* synthetic */ CLASSNAMEo(CLASSNAMEl1 l1Var, x xVar) {
        this.b = l1Var;
        this.c = xVar;
    }

    public /* synthetic */ CLASSNAMEo(CLASSNAMEl1 l1Var, E e) {
        this.b = l1Var;
        this.c = e;
    }

    public /* synthetic */ CLASSNAMEo(CLASSNAMEl1 l1Var, V v) {
        this.b = l1Var;
        this.c = v;
    }

    public /* synthetic */ CLASSNAMEo(CLASSNAMEl1 l1Var, CLASSNAMEj0 j0Var) {
        this.b = l1Var;
        this.c = j0Var;
    }

    public /* synthetic */ CLASSNAMEo(CLASSNAMEn4 n4Var, Consumer consumer) {
        this.b = n4Var;
        this.c = consumer;
    }

    public /* synthetic */ CLASSNAMEo(AtomicBoolean atomicBoolean, ConcurrentHashMap concurrentHashMap) {
        this.b = atomicBoolean;
        this.c = concurrentHashMap;
    }
}
