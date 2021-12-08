package j$.util.stream;

import j$.util.Spliterator;
import j$.util.stream.AbstractTask;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;

abstract class AbstractTask<P_IN, P_OUT, R, K extends AbstractTask<P_IN, P_OUT, R, K>> extends CountedCompleter<R> {
    static final int LEAF_TARGET = (ForkJoinPool.getCommonPoolParallelism() << 2);
    protected final PipelineHelper<P_OUT> helper;
    protected K leftChild;
    private R localResult;
    protected K rightChild;
    protected Spliterator<P_IN> spliterator;
    protected long targetSize;

    /* access modifiers changed from: protected */
    public abstract R doLeaf();

    /* access modifiers changed from: protected */
    public abstract K makeChild(Spliterator<P_IN> spliterator2);

    protected AbstractTask(PipelineHelper<P_OUT> helper2, Spliterator<P_IN> spliterator2) {
        super((CountedCompleter) null);
        this.helper = helper2;
        this.spliterator = spliterator2;
        this.targetSize = 0;
    }

    protected AbstractTask(K parent, Spliterator<P_IN> spliterator2) {
        super(parent);
        this.spliterator = spliterator2;
        this.helper = parent.helper;
        this.targetSize = parent.targetSize;
    }

    public static long suggestTargetSize(long sizeEstimate) {
        long est = sizeEstimate / ((long) LEAF_TARGET);
        if (est > 0) {
            return est;
        }
        return 1;
    }

    /* access modifiers changed from: protected */
    public final long getTargetSize(long sizeEstimate) {
        long j = this.targetSize;
        long s = j;
        if (j != 0) {
            return s;
        }
        long suggestTargetSize = suggestTargetSize(sizeEstimate);
        this.targetSize = suggestTargetSize;
        return suggestTargetSize;
    }

    public R getRawResult() {
        return this.localResult;
    }

    /* access modifiers changed from: protected */
    public void setRawResult(R result) {
        if (result != null) {
            throw new IllegalStateException();
        }
    }

    /* access modifiers changed from: protected */
    public R getLocalResult() {
        return this.localResult;
    }

    /* access modifiers changed from: protected */
    public void setLocalResult(R localResult2) {
        this.localResult = localResult2;
    }

    /* access modifiers changed from: protected */
    public boolean isLeaf() {
        return this.leftChild == null;
    }

    /* access modifiers changed from: protected */
    public boolean isRoot() {
        return getParent() == null;
    }

    /* access modifiers changed from: protected */
    public K getParent() {
        return (AbstractTask) getCompleter();
    }

    public void compute() {
        K taskToFork;
        Spliterator<P_IN> spliterator2 = this.spliterator;
        long sizeEstimate = spliterator2.estimateSize();
        long sizeThreshold = getTargetSize(sizeEstimate);
        boolean forkRight = false;
        K task = this;
        while (sizeEstimate > sizeThreshold) {
            Spliterator<P_IN> trySplit = spliterator2.trySplit();
            Spliterator<P_IN> spliterator3 = trySplit;
            if (trySplit == null) {
                break;
            }
            K makeChild = task.makeChild(spliterator3);
            K leftChild2 = makeChild;
            task.leftChild = makeChild;
            K makeChild2 = task.makeChild(spliterator2);
            K rightChild2 = makeChild2;
            task.rightChild = makeChild2;
            task.setPendingCount(1);
            if (forkRight) {
                forkRight = false;
                spliterator2 = spliterator3;
                task = leftChild2;
                taskToFork = rightChild2;
            } else {
                forkRight = true;
                task = rightChild2;
                taskToFork = leftChild2;
            }
            taskToFork.fork();
            sizeEstimate = spliterator2.estimateSize();
        }
        task.setLocalResult(task.doLeaf());
        task.tryComplete();
    }

    public void onCompletion(CountedCompleter<?> countedCompleter) {
        this.spliterator = null;
        this.rightChild = null;
        this.leftChild = null;
    }

    /* access modifiers changed from: protected */
    public boolean isLeftmostNode() {
        K node = this;
        while (node != null) {
            K parent = node.getParent();
            if (parent != null && parent.leftChild != node) {
                return false;
            }
            node = parent;
        }
        return true;
    }
}
