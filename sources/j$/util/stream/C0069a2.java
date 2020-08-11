package j$.util.stream;

import j$.util.Spliterator;
import j$.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountedCompleter;
import java.util.stream.ForEachOps;

/* renamed from: j$.util.stream.a2  reason: case insensitive filesystem */
final class CLASSNAMEa2 extends CountedCompleter {
    private final CLASSNAMEq4 a;
    private Spliterator b;
    private final long c;
    private final ConcurrentHashMap d;
    private final G5 e;
    private final CLASSNAMEa2 f;
    private CLASSNAMEt3 g;

    protected CLASSNAMEa2(CLASSNAMEq4 helper, Spliterator spliterator, G5 action) {
        super((CountedCompleter) null);
        this.a = helper;
        this.b = spliterator;
        this.c = CLASSNAMEk1.j(spliterator.estimateSize());
        this.d = new ConcurrentHashMap(Math.max(16, CLASSNAMEk1.g << 1));
        this.e = action;
        this.f = null;
    }

    CLASSNAMEa2(CLASSNAMEa2 parent, Spliterator spliterator, CLASSNAMEa2 leftPredecessor) {
        super(parent);
        this.a = parent.a;
        this.b = spliterator;
        this.c = parent.c;
        this.d = parent.d;
        this.e = parent.e;
        this.f = leftPredecessor;
    }

    public final void compute() {
        a(this);
    }

    private static void a(ForEachOps.ForEachOrderedTask<S, T> task) {
        ForEachOps.ForEachOrderedTask<S, T> taskToFork;
        Spliterator spliterator = task.b;
        long sizeThreshold = task.c;
        boolean forkRight = false;
        while (spliterator.estimateSize() > sizeThreshold) {
            Spliterator trySplit = spliterator.trySplit();
            Spliterator spliterator2 = trySplit;
            if (trySplit == null) {
                break;
            }
            ForEachOps.ForEachOrderedTask<S, T> leftChild = new CLASSNAMEa2((CLASSNAMEa2) task, spliterator2, task.f);
            ForEachOps.ForEachOrderedTask<S, T> rightChild = new CLASSNAMEa2((CLASSNAMEa2) task, spliterator, (CLASSNAMEa2) leftChild);
            task.addToPendingCount(1);
            rightChild.addToPendingCount(1);
            task.d.put(leftChild, rightChild);
            if (task.f != null) {
                leftChild.addToPendingCount(1);
                if (task.d.replace(task.f, task, leftChild)) {
                    task.addToPendingCount(-1);
                } else {
                    leftChild.addToPendingCount(-1);
                }
            }
            if (forkRight) {
                forkRight = false;
                spliterator = spliterator2;
                task = leftChild;
                taskToFork = rightChild;
            } else {
                forkRight = true;
                task = rightChild;
                taskToFork = leftChild;
            }
            taskToFork.fork();
        }
        if (task.getPendingCount() > 0) {
            A a2 = A.a;
            CLASSNAMEq4 q4Var = task.a;
            CLASSNAMEk3 s0 = q4Var.s0(q4Var.p0(spliterator), a2);
            task.a.t0(s0, spliterator);
            task.g = s0.b();
            task.b = null;
        }
        task.tryComplete();
    }

    static /* synthetic */ Object[] b(int size) {
        return new Object[size];
    }

    public void onCompletion(CountedCompleter countedCompleter) {
        CLASSNAMEt3 t3Var = this.g;
        if (t3Var != null) {
            t3Var.forEach(this.e);
            this.g = null;
        } else {
            Spliterator spliterator = this.b;
            if (spliterator != null) {
                this.a.t0(this.e, spliterator);
                this.b = null;
            }
        }
        ForEachOps.ForEachOrderedTask<S, T> leftDescendant = (CLASSNAMEa2) this.d.remove(this);
        if (leftDescendant != null) {
            leftDescendant.tryComplete();
        }
    }
}
