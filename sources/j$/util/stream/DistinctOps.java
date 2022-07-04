package j$.util.stream;

import j$.util.Spliterator;
import j$.util.concurrent.ConcurrentHashMap;
import j$.util.stream.ReferencePipeline;
import j$.util.stream.Sink;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

final class DistinctOps {
    private DistinctOps() {
    }

    static <T> ReferencePipeline<T, T> makeRef(AbstractPipeline<?, T, ?> upstream) {
        return new ReferencePipeline.StatefulOp<T, T>(upstream, StreamShape.REFERENCE, StreamOpFlag.IS_DISTINCT | StreamOpFlag.NOT_SIZED) {
            /* access modifiers changed from: package-private */
            public <P_IN> Node<T> reduce(PipelineHelper<T> helper, Spliterator<P_IN> spliterator) {
                return Nodes.node((Collection) ReduceOps.makeRef(DistinctOps$1$$ExternalSyntheticLambda3.INSTANCE, DistinctOps$1$$ExternalSyntheticLambda0.INSTANCE, DistinctOps$1$$ExternalSyntheticLambda1.INSTANCE).evaluateParallel(helper, spliterator));
            }

            /* JADX WARNING: type inference failed for: r7v0, types: [j$.util.Spliterator, j$.util.Spliterator<P_IN>] */
            /* JADX WARNING: type inference failed for: r8v0, types: [j$.util.function.IntFunction, j$.util.function.IntFunction<T[]>] */
            /* access modifiers changed from: package-private */
            /* JADX WARNING: Unknown variable types count: 2 */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public <P_IN> j$.util.stream.Node<T> opEvaluateParallel(j$.util.stream.PipelineHelper<T> r6, j$.util.Spliterator<P_IN> r7, j$.util.function.IntFunction<T[]> r8) {
                /*
                    r5 = this;
                    j$.util.stream.StreamOpFlag r0 = j$.util.stream.StreamOpFlag.DISTINCT
                    int r1 = r6.getStreamAndOpFlags()
                    boolean r0 = r0.isKnown(r1)
                    r1 = 0
                    if (r0 == 0) goto L_0x0012
                    j$.util.stream.Node r0 = r6.evaluate(r7, r1, r8)
                    return r0
                L_0x0012:
                    j$.util.stream.StreamOpFlag r0 = j$.util.stream.StreamOpFlag.ORDERED
                    int r2 = r6.getStreamAndOpFlags()
                    boolean r0 = r0.isKnown(r2)
                    if (r0 == 0) goto L_0x0023
                    j$.util.stream.Node r0 = r5.reduce(r6, r7)
                    return r0
                L_0x0023:
                    java.util.concurrent.atomic.AtomicBoolean r0 = new java.util.concurrent.atomic.AtomicBoolean
                    r0.<init>(r1)
                    j$.util.concurrent.ConcurrentHashMap r2 = new j$.util.concurrent.ConcurrentHashMap
                    r2.<init>()
                    j$.util.stream.DistinctOps$1$$ExternalSyntheticLambda2 r3 = new j$.util.stream.DistinctOps$1$$ExternalSyntheticLambda2
                    r3.<init>(r0, r2)
                    j$.util.stream.TerminalOp r1 = j$.util.stream.ForEachOps.makeRef(r3, r1)
                    r1.evaluateParallel(r6, r7)
                    java.util.Set r3 = r2.keySet()
                    boolean r4 = r0.get()
                    if (r4 == 0) goto L_0x004d
                    java.util.HashSet r4 = new java.util.HashSet
                    r4.<init>(r3)
                    r3 = r4
                    r4 = 0
                    r3.add(r4)
                L_0x004d:
                    j$.util.stream.Node r4 = j$.util.stream.Nodes.node(r3)
                    return r4
                */
                throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.DistinctOps.AnonymousClass1.opEvaluateParallel(j$.util.stream.PipelineHelper, j$.util.Spliterator, j$.util.function.IntFunction):j$.util.stream.Node");
            }

            static /* synthetic */ void lambda$opEvaluateParallel$0(AtomicBoolean seenNull, ConcurrentHashMap map, Object t) {
                if (t == null) {
                    seenNull.set(true);
                } else {
                    map.putIfAbsent(t, Boolean.TRUE);
                }
            }

            /* JADX WARNING: type inference failed for: r4v0, types: [j$.util.Spliterator, j$.util.Spliterator<P_IN>] */
            /* access modifiers changed from: package-private */
            /* JADX WARNING: Unknown variable types count: 1 */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public <P_IN> j$.util.Spliterator<T> opEvaluateParallelLazy(j$.util.stream.PipelineHelper<T> r3, j$.util.Spliterator<P_IN> r4) {
                /*
                    r2 = this;
                    j$.util.stream.StreamOpFlag r0 = j$.util.stream.StreamOpFlag.DISTINCT
                    int r1 = r3.getStreamAndOpFlags()
                    boolean r0 = r0.isKnown(r1)
                    if (r0 == 0) goto L_0x0011
                    j$.util.Spliterator r0 = r3.wrapSpliterator(r4)
                    return r0
                L_0x0011:
                    j$.util.stream.StreamOpFlag r0 = j$.util.stream.StreamOpFlag.ORDERED
                    int r1 = r3.getStreamAndOpFlags()
                    boolean r0 = r0.isKnown(r1)
                    if (r0 == 0) goto L_0x0026
                    j$.util.stream.Node r0 = r2.reduce(r3, r4)
                    j$.util.Spliterator r0 = r0.spliterator()
                    return r0
                L_0x0026:
                    j$.util.stream.StreamSpliterators$DistinctSpliterator r0 = new j$.util.stream.StreamSpliterators$DistinctSpliterator
                    j$.util.Spliterator r1 = r3.wrapSpliterator(r4)
                    r0.<init>(r1)
                    return r0
                */
                throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.DistinctOps.AnonymousClass1.opEvaluateParallelLazy(j$.util.stream.PipelineHelper, j$.util.Spliterator):j$.util.Spliterator");
            }

            /* access modifiers changed from: package-private */
            public Sink<T> opWrapSink(int flags, Sink<T> sink) {
                sink.getClass();
                if (StreamOpFlag.DISTINCT.isKnown(flags)) {
                    return sink;
                }
                return StreamOpFlag.SORTED.isKnown(flags) ? new Sink.ChainedReference<T, T>(sink) {
                    T lastSeen;
                    boolean seenNull;

                    public void begin(long size) {
                        this.seenNull = false;
                        this.lastSeen = null;
                        this.downstream.begin(-1);
                    }

                    public void end() {
                        this.seenNull = false;
                        this.lastSeen = null;
                        this.downstream.end();
                    }

                    public void accept(T t) {
                        if (t != null) {
                            T t2 = this.lastSeen;
                            if (t2 == null || !t.equals(t2)) {
                                Sink sink = this.downstream;
                                this.lastSeen = t;
                                sink.accept(t);
                            }
                        } else if (!this.seenNull) {
                            this.seenNull = true;
                            Sink sink2 = this.downstream;
                            this.lastSeen = null;
                            sink2.accept(null);
                        }
                    }
                } : new Sink.ChainedReference<T, T>(sink) {
                    Set<T> seen;

                    public void begin(long size) {
                        this.seen = new HashSet();
                        this.downstream.begin(-1);
                    }

                    public void end() {
                        this.seen = null;
                        this.downstream.end();
                    }

                    public void accept(T t) {
                        if (!this.seen.contains(t)) {
                            this.seen.add(t);
                            this.downstream.accept(t);
                        }
                    }
                };
            }
        };
    }
}
