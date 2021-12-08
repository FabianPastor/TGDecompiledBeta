package j$.util.stream;

import j$.util.Spliterator;
import j$.util.stream.AbstractShortCircuitTask;
import java.util.concurrent.atomic.AtomicReference;

abstract class AbstractShortCircuitTask<P_IN, P_OUT, R, K extends AbstractShortCircuitTask<P_IN, P_OUT, R, K>> extends AbstractTask<P_IN, P_OUT, R, K> {
    protected volatile boolean canceled;
    protected final AtomicReference<R> sharedResult;

    /* access modifiers changed from: protected */
    public abstract R getEmptyResult();

    protected AbstractShortCircuitTask(PipelineHelper<P_OUT> helper, Spliterator<P_IN> spliterator) {
        super(helper, spliterator);
        this.sharedResult = new AtomicReference<>((Object) null);
    }

    protected AbstractShortCircuitTask(K parent, Spliterator<P_IN> spliterator) {
        super(parent, spliterator);
        this.sharedResult = parent.sharedResult;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0054, code lost:
        r9 = r6.doLeaf();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void compute() {
        /*
            r13 = this;
            j$.util.Spliterator r0 = r13.spliterator
            long r1 = r0.estimateSize()
            long r3 = r13.getTargetSize(r1)
            r5 = 0
            r6 = r13
            java.util.concurrent.atomic.AtomicReference<R> r7 = r13.sharedResult
        L_0x000e:
            java.lang.Object r8 = r7.get()
            r9 = r8
            if (r8 != 0) goto L_0x0058
            boolean r8 = r6.taskCanceled()
            if (r8 == 0) goto L_0x0020
            java.lang.Object r9 = r6.getEmptyResult()
            goto L_0x0058
        L_0x0020:
            int r8 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r8 <= 0) goto L_0x0054
            j$.util.Spliterator r8 = r0.trySplit()
            r10 = r8
            if (r8 != 0) goto L_0x002c
            goto L_0x0054
        L_0x002c:
            j$.util.stream.AbstractTask r8 = r6.makeChild(r10)
            j$.util.stream.AbstractShortCircuitTask r8 = (j$.util.stream.AbstractShortCircuitTask) r8
            r11 = r8
            r6.leftChild = r8
            j$.util.stream.AbstractTask r8 = r6.makeChild(r0)
            j$.util.stream.AbstractShortCircuitTask r8 = (j$.util.stream.AbstractShortCircuitTask) r8
            r12 = r8
            r6.rightChild = r8
            r8 = 1
            r6.setPendingCount(r8)
            if (r5 == 0) goto L_0x0049
            r5 = 0
            r0 = r10
            r6 = r11
            r8 = r12
            goto L_0x004c
        L_0x0049:
            r5 = 1
            r6 = r12
            r8 = r11
        L_0x004c:
            r8.fork()
            long r1 = r0.estimateSize()
            goto L_0x000e
        L_0x0054:
            java.lang.Object r9 = r6.doLeaf()
        L_0x0058:
            r6.setLocalResult(r9)
            r6.tryComplete()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.AbstractShortCircuitTask.compute():void");
    }

    /* access modifiers changed from: protected */
    public void shortCircuit(R result) {
        if (result != null) {
            this.sharedResult.compareAndSet((Object) null, result);
        }
    }

    /* access modifiers changed from: protected */
    public void setLocalResult(R localResult) {
        if (!isRoot()) {
            super.setLocalResult(localResult);
        } else if (localResult != null) {
            this.sharedResult.compareAndSet((Object) null, localResult);
        }
    }

    public R getRawResult() {
        return getLocalResult();
    }

    public R getLocalResult() {
        if (!isRoot()) {
            return super.getLocalResult();
        }
        R answer = this.sharedResult.get();
        return answer == null ? getEmptyResult() : answer;
    }

    /* access modifiers changed from: protected */
    public void cancel() {
        this.canceled = true;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean taskCanceled() {
        /*
            r3 = this;
            boolean r0 = r3.canceled
            if (r0 != 0) goto L_0x0018
            j$.util.stream.AbstractTask r1 = r3.getParent()
            j$.util.stream.AbstractShortCircuitTask r1 = (j$.util.stream.AbstractShortCircuitTask) r1
        L_0x000a:
            if (r0 != 0) goto L_0x0018
            if (r1 == 0) goto L_0x0018
            boolean r0 = r1.canceled
            j$.util.stream.AbstractTask r2 = r1.getParent()
            r1 = r2
            j$.util.stream.AbstractShortCircuitTask r1 = (j$.util.stream.AbstractShortCircuitTask) r1
            goto L_0x000a
        L_0x0018:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.AbstractShortCircuitTask.taskCanceled():boolean");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void cancelLaterNodes() {
        /*
            r4 = this;
            j$.util.stream.AbstractTask r0 = r4.getParent()
            j$.util.stream.AbstractShortCircuitTask r0 = (j$.util.stream.AbstractShortCircuitTask) r0
            r1 = r4
        L_0x0007:
            if (r0 == 0) goto L_0x0021
            j$.util.stream.AbstractTask r2 = r0.leftChild
            if (r2 != r1) goto L_0x0018
            j$.util.stream.AbstractTask r2 = r0.rightChild
            j$.util.stream.AbstractShortCircuitTask r2 = (j$.util.stream.AbstractShortCircuitTask) r2
            boolean r3 = r2.canceled
            if (r3 != 0) goto L_0x0018
            r2.cancel()
        L_0x0018:
            r1 = r0
            j$.util.stream.AbstractTask r2 = r0.getParent()
            r0 = r2
            j$.util.stream.AbstractShortCircuitTask r0 = (j$.util.stream.AbstractShortCircuitTask) r0
            goto L_0x0007
        L_0x0021:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.AbstractShortCircuitTask.cancelLaterNodes():void");
    }
}
