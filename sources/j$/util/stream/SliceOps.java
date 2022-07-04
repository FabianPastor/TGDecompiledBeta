package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.IntFunction;
import j$.util.stream.DoublePipeline;
import j$.util.stream.IntPipeline;
import j$.util.stream.LongPipeline;
import j$.util.stream.Node;
import j$.util.stream.ReferencePipeline;
import j$.util.stream.Sink;
import j$.util.stream.StreamSpliterators;
import java.util.concurrent.CountedCompleter;
import java.util.stream.Node;
import java.util.stream.SliceOps;

final class SliceOps {
    static final /* synthetic */ boolean $assertionsDisabled = true;

    private SliceOps() {
    }

    /* access modifiers changed from: private */
    public static long calcSize(long size, long skip, long limit) {
        if (size >= 0) {
            return Math.max(-1, Math.min(size - skip, limit));
        }
        return -1;
    }

    /* access modifiers changed from: private */
    public static long calcSliceFence(long skip, long limit) {
        long sliceFence = limit >= 0 ? skip + limit : Long.MAX_VALUE;
        if (sliceFence >= 0) {
            return sliceFence;
        }
        return Long.MAX_VALUE;
    }

    /* access modifiers changed from: private */
    public static <P_IN> Spliterator<P_IN> sliceSpliterator(StreamShape shape, Spliterator<P_IN> spliterator, long skip, long limit) {
        if ($assertionsDisabled || spliterator.hasCharacteristics(16384)) {
            long sliceFence = calcSliceFence(skip, limit);
            switch (AnonymousClass5.$SwitchMap$java$util$stream$StreamShape[shape.ordinal()]) {
                case 1:
                    return new StreamSpliterators.SliceSpliterator.OfRef(spliterator, skip, sliceFence);
                case 2:
                    return new StreamSpliterators.SliceSpliterator.OfInt((Spliterator.OfInt) spliterator, skip, sliceFence);
                case 3:
                    return new StreamSpliterators.SliceSpliterator.OfLong((Spliterator.OfLong) spliterator, skip, sliceFence);
                case 4:
                    return new StreamSpliterators.SliceSpliterator.OfDouble((Spliterator.OfDouble) spliterator, skip, sliceFence);
                default:
                    throw new IllegalStateException("Unknown shape " + shape);
            }
        } else {
            throw new AssertionError();
        }
    }

    /* renamed from: j$.util.stream.SliceOps$5  reason: invalid class name */
    static /* synthetic */ class AnonymousClass5 {
        static final /* synthetic */ int[] $SwitchMap$java$util$stream$StreamShape;

        static {
            int[] iArr = new int[StreamShape.values().length];
            $SwitchMap$java$util$stream$StreamShape = iArr;
            try {
                iArr[StreamShape.REFERENCE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$util$stream$StreamShape[StreamShape.INT_VALUE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$util$stream$StreamShape[StreamShape.LONG_VALUE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$java$util$stream$StreamShape[StreamShape.DOUBLE_VALUE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    /* access modifiers changed from: private */
    public static <T> IntFunction<T[]> castingArray() {
        return SliceOps$$ExternalSyntheticLambda0.INSTANCE;
    }

    static /* synthetic */ Object[] lambda$castingArray$0(int size) {
        return new Object[size];
    }

    public static <T> Stream<T> makeRef(AbstractPipeline<?, T, ?> upstream, long skip, long limit) {
        if (skip >= 0) {
            final long j = skip;
            final long j2 = limit;
            return new ReferencePipeline.StatefulOp<T, T>(upstream, StreamShape.REFERENCE, flags(limit)) {
                /* access modifiers changed from: package-private */
                public Spliterator<T> unorderedSkipLimitSpliterator(Spliterator<T> spliterator, long skip, long limit, long sizeIfKnown) {
                    if (skip <= sizeIfKnown) {
                        long j = sizeIfKnown - skip;
                        if (limit >= 0) {
                            j = Math.min(limit, j);
                        }
                        limit = j;
                        skip = 0;
                    }
                    return new StreamSpliterators.UnorderedSliceSpliterator.OfRef(spliterator, skip, limit);
                }

                /* JADX WARNING: type inference failed for: r16v0, types: [j$.util.Spliterator, j$.util.Spliterator<P_IN>] */
                /* access modifiers changed from: package-private */
                /* JADX WARNING: Unknown variable types count: 1 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public <P_IN> j$.util.Spliterator<T> opEvaluateParallelLazy(j$.util.stream.PipelineHelper<T> r15, j$.util.Spliterator<P_IN> r16) {
                    /*
                        r14 = this;
                        r9 = r14
                        long r10 = r15.exactOutputSizeIfKnown(r16)
                        r0 = 0
                        int r2 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
                        if (r2 <= 0) goto L_0x0028
                        r0 = 16384(0x4000, float:2.2959E-41)
                        r12 = r16
                        boolean r0 = r12.hasCharacteristics(r0)
                        if (r0 == 0) goto L_0x002a
                        j$.util.stream.StreamSpliterators$SliceSpliterator$OfRef r0 = new j$.util.stream.StreamSpliterators$SliceSpliterator$OfRef
                        j$.util.Spliterator r2 = r15.wrapSpliterator(r16)
                        long r3 = r7
                        long r5 = r9
                        long r5 = j$.util.stream.SliceOps.calcSliceFence(r3, r5)
                        r1 = r0
                        r1.<init>(r2, r3, r5)
                        return r0
                    L_0x0028:
                        r12 = r16
                    L_0x002a:
                        j$.util.stream.StreamOpFlag r0 = j$.util.stream.StreamOpFlag.ORDERED
                        int r1 = r15.getStreamAndOpFlags()
                        boolean r0 = r0.isKnown(r1)
                        if (r0 != 0) goto L_0x0046
                        j$.util.Spliterator r1 = r15.wrapSpliterator(r16)
                        long r2 = r7
                        long r4 = r9
                        r0 = r14
                        r6 = r10
                        j$.util.Spliterator r0 = r0.unorderedSkipLimitSpliterator(r1, r2, r4, r6)
                        return r0
                    L_0x0046:
                        j$.util.stream.SliceOps$SliceTask r13 = new j$.util.stream.SliceOps$SliceTask
                        j$.util.function.IntFunction r4 = j$.util.stream.SliceOps.castingArray()
                        long r5 = r7
                        long r7 = r9
                        r0 = r13
                        r1 = r14
                        r2 = r15
                        r3 = r16
                        r0.<init>(r1, r2, r3, r4, r5, r7)
                        java.lang.Object r0 = r13.invoke()
                        j$.util.stream.Node r0 = (j$.util.stream.Node) r0
                        j$.util.Spliterator r0 = r0.spliterator()
                        return r0
                    */
                    throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.SliceOps.AnonymousClass1.opEvaluateParallelLazy(j$.util.stream.PipelineHelper, j$.util.Spliterator):j$.util.Spliterator");
                }

                /* JADX WARNING: type inference failed for: r18v0, types: [j$.util.Spliterator, j$.util.Spliterator<P_IN>] */
                /* access modifiers changed from: package-private */
                /* JADX WARNING: Unknown variable types count: 1 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public <P_IN> j$.util.stream.Node<T> opEvaluateParallel(j$.util.stream.PipelineHelper<T> r17, j$.util.Spliterator<P_IN> r18, j$.util.function.IntFunction<T[]> r19) {
                    /*
                        r16 = this;
                        r9 = r16
                        r10 = r19
                        long r11 = r17.exactOutputSizeIfKnown(r18)
                        r8 = 1
                        r0 = 0
                        int r2 = (r11 > r0 ? 1 : (r11 == r0 ? 0 : -1))
                        if (r2 <= 0) goto L_0x0031
                        r0 = 16384(0x4000, float:2.2959E-41)
                        r13 = r18
                        boolean r0 = r13.hasCharacteristics(r0)
                        if (r0 == 0) goto L_0x002e
                        j$.util.stream.StreamShape r1 = r17.getSourceShape()
                        long r3 = r7
                        long r5 = r9
                        r2 = r18
                        j$.util.Spliterator r0 = j$.util.stream.SliceOps.sliceSpliterator(r1, r2, r3, r5)
                        r14 = r17
                        j$.util.stream.Node r1 = j$.util.stream.Nodes.collect(r14, r0, r8, r10)
                        return r1
                    L_0x002e:
                        r14 = r17
                        goto L_0x0035
                    L_0x0031:
                        r14 = r17
                        r13 = r18
                    L_0x0035:
                        j$.util.stream.StreamOpFlag r0 = j$.util.stream.StreamOpFlag.ORDERED
                        int r1 = r17.getStreamAndOpFlags()
                        boolean r0 = r0.isKnown(r1)
                        if (r0 != 0) goto L_0x0056
                        j$.util.Spliterator r1 = r17.wrapSpliterator(r18)
                        long r2 = r7
                        long r4 = r9
                        r0 = r16
                        r6 = r11
                        j$.util.Spliterator r0 = r0.unorderedSkipLimitSpliterator(r1, r2, r4, r6)
                        j$.util.stream.Node r1 = j$.util.stream.Nodes.collect(r9, r0, r8, r10)
                        return r1
                    L_0x0056:
                        j$.util.stream.SliceOps$SliceTask r15 = new j$.util.stream.SliceOps$SliceTask
                        long r5 = r7
                        long r7 = r9
                        r0 = r15
                        r1 = r16
                        r2 = r17
                        r3 = r18
                        r4 = r19
                        r0.<init>(r1, r2, r3, r4, r5, r7)
                        java.lang.Object r0 = r15.invoke()
                        j$.util.stream.Node r0 = (j$.util.stream.Node) r0
                        return r0
                    */
                    throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.SliceOps.AnonymousClass1.opEvaluateParallel(j$.util.stream.PipelineHelper, j$.util.Spliterator, j$.util.function.IntFunction):j$.util.stream.Node");
                }

                /* access modifiers changed from: package-private */
                public Sink<T> opWrapSink(int flags, Sink<T> sink) {
                    return new Sink.ChainedReference<T, T>(sink) {
                        long m;
                        long n;

                        {
                            this.n = j;
                            this.m = j2 >= 0 ? j2 : Long.MAX_VALUE;
                        }

                        public void begin(long size) {
                            this.downstream.begin(SliceOps.calcSize(size, j, this.m));
                        }

                        public void accept(T t) {
                            long j = this.n;
                            if (j == 0) {
                                long j2 = this.m;
                                if (j2 > 0) {
                                    this.m = j2 - 1;
                                    this.downstream.accept(t);
                                    return;
                                }
                                return;
                            }
                            this.n = j - 1;
                        }

                        public boolean cancellationRequested() {
                            return this.m == 0 || this.downstream.cancellationRequested();
                        }
                    };
                }
            };
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + skip);
    }

    public static IntStream makeInt(AbstractPipeline<?, Integer, ?> upstream, long skip, long limit) {
        if (skip >= 0) {
            final long j = skip;
            final long j2 = limit;
            return new IntPipeline.StatefulOp<Integer>(upstream, StreamShape.INT_VALUE, flags(limit)) {
                /* access modifiers changed from: package-private */
                public Spliterator.OfInt unorderedSkipLimitSpliterator(Spliterator.OfInt s, long skip, long limit, long sizeIfKnown) {
                    if (skip <= sizeIfKnown) {
                        long j = sizeIfKnown - skip;
                        if (limit >= 0) {
                            j = Math.min(limit, j);
                        }
                        limit = j;
                        skip = 0;
                    }
                    return new StreamSpliterators.UnorderedSliceSpliterator.OfInt(s, skip, limit);
                }

                /* JADX WARNING: type inference failed for: r16v0, types: [j$.util.Spliterator, j$.util.Spliterator<P_IN>] */
                /* access modifiers changed from: package-private */
                /* JADX WARNING: Unknown variable types count: 1 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public <P_IN> j$.util.Spliterator<java.lang.Integer> opEvaluateParallelLazy(j$.util.stream.PipelineHelper<java.lang.Integer> r15, j$.util.Spliterator<P_IN> r16) {
                    /*
                        r14 = this;
                        r9 = r14
                        long r10 = r15.exactOutputSizeIfKnown(r16)
                        r0 = 0
                        int r2 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
                        if (r2 <= 0) goto L_0x002b
                        r0 = 16384(0x4000, float:2.2959E-41)
                        r12 = r16
                        boolean r0 = r12.hasCharacteristics(r0)
                        if (r0 == 0) goto L_0x002d
                        j$.util.stream.StreamSpliterators$SliceSpliterator$OfInt r0 = new j$.util.stream.StreamSpliterators$SliceSpliterator$OfInt
                        j$.util.Spliterator r1 = r15.wrapSpliterator(r16)
                        r2 = r1
                        j$.util.Spliterator$OfInt r2 = (j$.util.Spliterator.OfInt) r2
                        long r3 = r7
                        long r5 = r9
                        long r5 = j$.util.stream.SliceOps.calcSliceFence(r3, r5)
                        r1 = r0
                        r1.<init>(r2, r3, r5)
                        return r0
                    L_0x002b:
                        r12 = r16
                    L_0x002d:
                        j$.util.stream.StreamOpFlag r0 = j$.util.stream.StreamOpFlag.ORDERED
                        int r1 = r15.getStreamAndOpFlags()
                        boolean r0 = r0.isKnown(r1)
                        if (r0 != 0) goto L_0x004c
                        j$.util.Spliterator r0 = r15.wrapSpliterator(r16)
                        r1 = r0
                        j$.util.Spliterator$OfInt r1 = (j$.util.Spliterator.OfInt) r1
                        long r2 = r7
                        long r4 = r9
                        r0 = r14
                        r6 = r10
                        j$.util.Spliterator$OfInt r0 = r0.unorderedSkipLimitSpliterator(r1, r2, r4, r6)
                        return r0
                    L_0x004c:
                        j$.util.stream.SliceOps$SliceTask r13 = new j$.util.stream.SliceOps$SliceTask
                        j$.util.stream.SliceOps$2$$ExternalSyntheticLambda0 r4 = j$.util.stream.SliceOps$2$$ExternalSyntheticLambda0.INSTANCE
                        long r5 = r7
                        long r7 = r9
                        r0 = r13
                        r1 = r14
                        r2 = r15
                        r3 = r16
                        r0.<init>(r1, r2, r3, r4, r5, r7)
                        java.lang.Object r0 = r13.invoke()
                        j$.util.stream.Node r0 = (j$.util.stream.Node) r0
                        j$.util.Spliterator r0 = r0.spliterator()
                        return r0
                    */
                    throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.SliceOps.AnonymousClass2.opEvaluateParallelLazy(j$.util.stream.PipelineHelper, j$.util.Spliterator):j$.util.Spliterator");
                }

                static /* synthetic */ Integer[] lambda$opEvaluateParallelLazy$0(int x$0) {
                    return new Integer[x$0];
                }

                /* JADX WARNING: type inference failed for: r17v0, types: [j$.util.Spliterator, j$.util.Spliterator<P_IN>] */
                /* access modifiers changed from: package-private */
                /* JADX WARNING: Unknown variable types count: 1 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public <P_IN> j$.util.stream.Node<java.lang.Integer> opEvaluateParallel(j$.util.stream.PipelineHelper<java.lang.Integer> r16, j$.util.Spliterator<P_IN> r17, j$.util.function.IntFunction<java.lang.Integer[]> r18) {
                    /*
                        r15 = this;
                        r9 = r15
                        long r10 = r16.exactOutputSizeIfKnown(r17)
                        r8 = 1
                        r0 = 0
                        int r2 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
                        if (r2 <= 0) goto L_0x002e
                        r0 = 16384(0x4000, float:2.2959E-41)
                        r12 = r17
                        boolean r0 = r12.hasCharacteristics(r0)
                        if (r0 == 0) goto L_0x002b
                        j$.util.stream.StreamShape r1 = r16.getSourceShape()
                        long r3 = r7
                        long r5 = r9
                        r2 = r17
                        j$.util.Spliterator r0 = j$.util.stream.SliceOps.sliceSpliterator(r1, r2, r3, r5)
                        r13 = r16
                        j$.util.stream.Node$OfInt r1 = j$.util.stream.Nodes.collectInt(r13, r0, r8)
                        return r1
                    L_0x002b:
                        r13 = r16
                        goto L_0x0032
                    L_0x002e:
                        r13 = r16
                        r12 = r17
                    L_0x0032:
                        j$.util.stream.StreamOpFlag r0 = j$.util.stream.StreamOpFlag.ORDERED
                        int r1 = r16.getStreamAndOpFlags()
                        boolean r0 = r0.isKnown(r1)
                        if (r0 != 0) goto L_0x0055
                        j$.util.Spliterator r0 = r16.wrapSpliterator(r17)
                        r1 = r0
                        j$.util.Spliterator$OfInt r1 = (j$.util.Spliterator.OfInt) r1
                        long r2 = r7
                        long r4 = r9
                        r0 = r15
                        r6 = r10
                        j$.util.Spliterator$OfInt r0 = r0.unorderedSkipLimitSpliterator(r1, r2, r4, r6)
                        j$.util.stream.Node$OfInt r1 = j$.util.stream.Nodes.collectInt(r15, r0, r8)
                        return r1
                    L_0x0055:
                        j$.util.stream.SliceOps$SliceTask r14 = new j$.util.stream.SliceOps$SliceTask
                        long r5 = r7
                        long r7 = r9
                        r0 = r14
                        r1 = r15
                        r2 = r16
                        r3 = r17
                        r4 = r18
                        r0.<init>(r1, r2, r3, r4, r5, r7)
                        java.lang.Object r0 = r14.invoke()
                        j$.util.stream.Node r0 = (j$.util.stream.Node) r0
                        return r0
                    */
                    throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.SliceOps.AnonymousClass2.opEvaluateParallel(j$.util.stream.PipelineHelper, j$.util.Spliterator, j$.util.function.IntFunction):j$.util.stream.Node");
                }

                /* access modifiers changed from: package-private */
                public Sink<Integer> opWrapSink(int flags, Sink<Integer> sink) {
                    return new Sink.ChainedInt<Integer>(sink) {
                        long m;
                        long n;

                        {
                            this.n = j;
                            this.m = j2 >= 0 ? j2 : Long.MAX_VALUE;
                        }

                        public void begin(long size) {
                            this.downstream.begin(SliceOps.calcSize(size, j, this.m));
                        }

                        public void accept(int t) {
                            long j = this.n;
                            if (j == 0) {
                                long j2 = this.m;
                                if (j2 > 0) {
                                    this.m = j2 - 1;
                                    this.downstream.accept(t);
                                    return;
                                }
                                return;
                            }
                            this.n = j - 1;
                        }

                        public boolean cancellationRequested() {
                            return this.m == 0 || this.downstream.cancellationRequested();
                        }
                    };
                }
            };
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + skip);
    }

    public static LongStream makeLong(AbstractPipeline<?, Long, ?> upstream, long skip, long limit) {
        if (skip >= 0) {
            final long j = skip;
            final long j2 = limit;
            return new LongPipeline.StatefulOp<Long>(upstream, StreamShape.LONG_VALUE, flags(limit)) {
                /* access modifiers changed from: package-private */
                public Spliterator.OfLong unorderedSkipLimitSpliterator(Spliterator.OfLong s, long skip, long limit, long sizeIfKnown) {
                    if (skip <= sizeIfKnown) {
                        long j = sizeIfKnown - skip;
                        if (limit >= 0) {
                            j = Math.min(limit, j);
                        }
                        limit = j;
                        skip = 0;
                    }
                    return new StreamSpliterators.UnorderedSliceSpliterator.OfLong(s, skip, limit);
                }

                /* JADX WARNING: type inference failed for: r16v0, types: [j$.util.Spliterator, j$.util.Spliterator<P_IN>] */
                /* access modifiers changed from: package-private */
                /* JADX WARNING: Unknown variable types count: 1 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public <P_IN> j$.util.Spliterator<java.lang.Long> opEvaluateParallelLazy(j$.util.stream.PipelineHelper<java.lang.Long> r15, j$.util.Spliterator<P_IN> r16) {
                    /*
                        r14 = this;
                        r9 = r14
                        long r10 = r15.exactOutputSizeIfKnown(r16)
                        r0 = 0
                        int r2 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
                        if (r2 <= 0) goto L_0x002b
                        r0 = 16384(0x4000, float:2.2959E-41)
                        r12 = r16
                        boolean r0 = r12.hasCharacteristics(r0)
                        if (r0 == 0) goto L_0x002d
                        j$.util.stream.StreamSpliterators$SliceSpliterator$OfLong r0 = new j$.util.stream.StreamSpliterators$SliceSpliterator$OfLong
                        j$.util.Spliterator r1 = r15.wrapSpliterator(r16)
                        r2 = r1
                        j$.util.Spliterator$OfLong r2 = (j$.util.Spliterator.OfLong) r2
                        long r3 = r7
                        long r5 = r9
                        long r5 = j$.util.stream.SliceOps.calcSliceFence(r3, r5)
                        r1 = r0
                        r1.<init>(r2, r3, r5)
                        return r0
                    L_0x002b:
                        r12 = r16
                    L_0x002d:
                        j$.util.stream.StreamOpFlag r0 = j$.util.stream.StreamOpFlag.ORDERED
                        int r1 = r15.getStreamAndOpFlags()
                        boolean r0 = r0.isKnown(r1)
                        if (r0 != 0) goto L_0x004c
                        j$.util.Spliterator r0 = r15.wrapSpliterator(r16)
                        r1 = r0
                        j$.util.Spliterator$OfLong r1 = (j$.util.Spliterator.OfLong) r1
                        long r2 = r7
                        long r4 = r9
                        r0 = r14
                        r6 = r10
                        j$.util.Spliterator$OfLong r0 = r0.unorderedSkipLimitSpliterator(r1, r2, r4, r6)
                        return r0
                    L_0x004c:
                        j$.util.stream.SliceOps$SliceTask r13 = new j$.util.stream.SliceOps$SliceTask
                        j$.util.stream.SliceOps$3$$ExternalSyntheticLambda0 r4 = j$.util.stream.SliceOps$3$$ExternalSyntheticLambda0.INSTANCE
                        long r5 = r7
                        long r7 = r9
                        r0 = r13
                        r1 = r14
                        r2 = r15
                        r3 = r16
                        r0.<init>(r1, r2, r3, r4, r5, r7)
                        java.lang.Object r0 = r13.invoke()
                        j$.util.stream.Node r0 = (j$.util.stream.Node) r0
                        j$.util.Spliterator r0 = r0.spliterator()
                        return r0
                    */
                    throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.SliceOps.AnonymousClass3.opEvaluateParallelLazy(j$.util.stream.PipelineHelper, j$.util.Spliterator):j$.util.Spliterator");
                }

                static /* synthetic */ Long[] lambda$opEvaluateParallelLazy$0(int x$0) {
                    return new Long[x$0];
                }

                /* JADX WARNING: type inference failed for: r17v0, types: [j$.util.Spliterator, j$.util.Spliterator<P_IN>] */
                /* access modifiers changed from: package-private */
                /* JADX WARNING: Unknown variable types count: 1 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public <P_IN> j$.util.stream.Node<java.lang.Long> opEvaluateParallel(j$.util.stream.PipelineHelper<java.lang.Long> r16, j$.util.Spliterator<P_IN> r17, j$.util.function.IntFunction<java.lang.Long[]> r18) {
                    /*
                        r15 = this;
                        r9 = r15
                        long r10 = r16.exactOutputSizeIfKnown(r17)
                        r8 = 1
                        r0 = 0
                        int r2 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
                        if (r2 <= 0) goto L_0x002e
                        r0 = 16384(0x4000, float:2.2959E-41)
                        r12 = r17
                        boolean r0 = r12.hasCharacteristics(r0)
                        if (r0 == 0) goto L_0x002b
                        j$.util.stream.StreamShape r1 = r16.getSourceShape()
                        long r3 = r7
                        long r5 = r9
                        r2 = r17
                        j$.util.Spliterator r0 = j$.util.stream.SliceOps.sliceSpliterator(r1, r2, r3, r5)
                        r13 = r16
                        j$.util.stream.Node$OfLong r1 = j$.util.stream.Nodes.collectLong(r13, r0, r8)
                        return r1
                    L_0x002b:
                        r13 = r16
                        goto L_0x0032
                    L_0x002e:
                        r13 = r16
                        r12 = r17
                    L_0x0032:
                        j$.util.stream.StreamOpFlag r0 = j$.util.stream.StreamOpFlag.ORDERED
                        int r1 = r16.getStreamAndOpFlags()
                        boolean r0 = r0.isKnown(r1)
                        if (r0 != 0) goto L_0x0055
                        j$.util.Spliterator r0 = r16.wrapSpliterator(r17)
                        r1 = r0
                        j$.util.Spliterator$OfLong r1 = (j$.util.Spliterator.OfLong) r1
                        long r2 = r7
                        long r4 = r9
                        r0 = r15
                        r6 = r10
                        j$.util.Spliterator$OfLong r0 = r0.unorderedSkipLimitSpliterator(r1, r2, r4, r6)
                        j$.util.stream.Node$OfLong r1 = j$.util.stream.Nodes.collectLong(r15, r0, r8)
                        return r1
                    L_0x0055:
                        j$.util.stream.SliceOps$SliceTask r14 = new j$.util.stream.SliceOps$SliceTask
                        long r5 = r7
                        long r7 = r9
                        r0 = r14
                        r1 = r15
                        r2 = r16
                        r3 = r17
                        r4 = r18
                        r0.<init>(r1, r2, r3, r4, r5, r7)
                        java.lang.Object r0 = r14.invoke()
                        j$.util.stream.Node r0 = (j$.util.stream.Node) r0
                        return r0
                    */
                    throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.SliceOps.AnonymousClass3.opEvaluateParallel(j$.util.stream.PipelineHelper, j$.util.Spliterator, j$.util.function.IntFunction):j$.util.stream.Node");
                }

                /* access modifiers changed from: package-private */
                public Sink<Long> opWrapSink(int flags, Sink<Long> sink) {
                    return new Sink.ChainedLong<Long>(sink) {
                        long m;
                        long n;

                        {
                            this.n = j;
                            this.m = j2 >= 0 ? j2 : Long.MAX_VALUE;
                        }

                        public void begin(long size) {
                            this.downstream.begin(SliceOps.calcSize(size, j, this.m));
                        }

                        public void accept(long t) {
                            long j = this.n;
                            if (j == 0) {
                                long j2 = this.m;
                                if (j2 > 0) {
                                    this.m = j2 - 1;
                                    this.downstream.accept(t);
                                    return;
                                }
                                return;
                            }
                            this.n = j - 1;
                        }

                        public boolean cancellationRequested() {
                            return this.m == 0 || this.downstream.cancellationRequested();
                        }
                    };
                }
            };
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + skip);
    }

    public static DoubleStream makeDouble(AbstractPipeline<?, Double, ?> upstream, long skip, long limit) {
        if (skip >= 0) {
            final long j = skip;
            final long j2 = limit;
            return new DoublePipeline.StatefulOp<Double>(upstream, StreamShape.DOUBLE_VALUE, flags(limit)) {
                /* access modifiers changed from: package-private */
                public Spliterator.OfDouble unorderedSkipLimitSpliterator(Spliterator.OfDouble s, long skip, long limit, long sizeIfKnown) {
                    if (skip <= sizeIfKnown) {
                        long j = sizeIfKnown - skip;
                        if (limit >= 0) {
                            j = Math.min(limit, j);
                        }
                        limit = j;
                        skip = 0;
                    }
                    return new StreamSpliterators.UnorderedSliceSpliterator.OfDouble(s, skip, limit);
                }

                /* JADX WARNING: type inference failed for: r16v0, types: [j$.util.Spliterator, j$.util.Spliterator<P_IN>] */
                /* access modifiers changed from: package-private */
                /* JADX WARNING: Unknown variable types count: 1 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public <P_IN> j$.util.Spliterator<java.lang.Double> opEvaluateParallelLazy(j$.util.stream.PipelineHelper<java.lang.Double> r15, j$.util.Spliterator<P_IN> r16) {
                    /*
                        r14 = this;
                        r9 = r14
                        long r10 = r15.exactOutputSizeIfKnown(r16)
                        r0 = 0
                        int r2 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
                        if (r2 <= 0) goto L_0x002b
                        r0 = 16384(0x4000, float:2.2959E-41)
                        r12 = r16
                        boolean r0 = r12.hasCharacteristics(r0)
                        if (r0 == 0) goto L_0x002d
                        j$.util.stream.StreamSpliterators$SliceSpliterator$OfDouble r0 = new j$.util.stream.StreamSpliterators$SliceSpliterator$OfDouble
                        j$.util.Spliterator r1 = r15.wrapSpliterator(r16)
                        r2 = r1
                        j$.util.Spliterator$OfDouble r2 = (j$.util.Spliterator.OfDouble) r2
                        long r3 = r7
                        long r5 = r9
                        long r5 = j$.util.stream.SliceOps.calcSliceFence(r3, r5)
                        r1 = r0
                        r1.<init>(r2, r3, r5)
                        return r0
                    L_0x002b:
                        r12 = r16
                    L_0x002d:
                        j$.util.stream.StreamOpFlag r0 = j$.util.stream.StreamOpFlag.ORDERED
                        int r1 = r15.getStreamAndOpFlags()
                        boolean r0 = r0.isKnown(r1)
                        if (r0 != 0) goto L_0x004c
                        j$.util.Spliterator r0 = r15.wrapSpliterator(r16)
                        r1 = r0
                        j$.util.Spliterator$OfDouble r1 = (j$.util.Spliterator.OfDouble) r1
                        long r2 = r7
                        long r4 = r9
                        r0 = r14
                        r6 = r10
                        j$.util.Spliterator$OfDouble r0 = r0.unorderedSkipLimitSpliterator(r1, r2, r4, r6)
                        return r0
                    L_0x004c:
                        j$.util.stream.SliceOps$SliceTask r13 = new j$.util.stream.SliceOps$SliceTask
                        j$.util.stream.SliceOps$4$$ExternalSyntheticLambda0 r4 = j$.util.stream.SliceOps$4$$ExternalSyntheticLambda0.INSTANCE
                        long r5 = r7
                        long r7 = r9
                        r0 = r13
                        r1 = r14
                        r2 = r15
                        r3 = r16
                        r0.<init>(r1, r2, r3, r4, r5, r7)
                        java.lang.Object r0 = r13.invoke()
                        j$.util.stream.Node r0 = (j$.util.stream.Node) r0
                        j$.util.Spliterator r0 = r0.spliterator()
                        return r0
                    */
                    throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.SliceOps.AnonymousClass4.opEvaluateParallelLazy(j$.util.stream.PipelineHelper, j$.util.Spliterator):j$.util.Spliterator");
                }

                static /* synthetic */ Double[] lambda$opEvaluateParallelLazy$0(int x$0) {
                    return new Double[x$0];
                }

                /* JADX WARNING: type inference failed for: r17v0, types: [j$.util.Spliterator, j$.util.Spliterator<P_IN>] */
                /* access modifiers changed from: package-private */
                /* JADX WARNING: Unknown variable types count: 1 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public <P_IN> j$.util.stream.Node<java.lang.Double> opEvaluateParallel(j$.util.stream.PipelineHelper<java.lang.Double> r16, j$.util.Spliterator<P_IN> r17, j$.util.function.IntFunction<java.lang.Double[]> r18) {
                    /*
                        r15 = this;
                        r9 = r15
                        long r10 = r16.exactOutputSizeIfKnown(r17)
                        r8 = 1
                        r0 = 0
                        int r2 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
                        if (r2 <= 0) goto L_0x002e
                        r0 = 16384(0x4000, float:2.2959E-41)
                        r12 = r17
                        boolean r0 = r12.hasCharacteristics(r0)
                        if (r0 == 0) goto L_0x002b
                        j$.util.stream.StreamShape r1 = r16.getSourceShape()
                        long r3 = r7
                        long r5 = r9
                        r2 = r17
                        j$.util.Spliterator r0 = j$.util.stream.SliceOps.sliceSpliterator(r1, r2, r3, r5)
                        r13 = r16
                        j$.util.stream.Node$OfDouble r1 = j$.util.stream.Nodes.collectDouble(r13, r0, r8)
                        return r1
                    L_0x002b:
                        r13 = r16
                        goto L_0x0032
                    L_0x002e:
                        r13 = r16
                        r12 = r17
                    L_0x0032:
                        j$.util.stream.StreamOpFlag r0 = j$.util.stream.StreamOpFlag.ORDERED
                        int r1 = r16.getStreamAndOpFlags()
                        boolean r0 = r0.isKnown(r1)
                        if (r0 != 0) goto L_0x0055
                        j$.util.Spliterator r0 = r16.wrapSpliterator(r17)
                        r1 = r0
                        j$.util.Spliterator$OfDouble r1 = (j$.util.Spliterator.OfDouble) r1
                        long r2 = r7
                        long r4 = r9
                        r0 = r15
                        r6 = r10
                        j$.util.Spliterator$OfDouble r0 = r0.unorderedSkipLimitSpliterator(r1, r2, r4, r6)
                        j$.util.stream.Node$OfDouble r1 = j$.util.stream.Nodes.collectDouble(r15, r0, r8)
                        return r1
                    L_0x0055:
                        j$.util.stream.SliceOps$SliceTask r14 = new j$.util.stream.SliceOps$SliceTask
                        long r5 = r7
                        long r7 = r9
                        r0 = r14
                        r1 = r15
                        r2 = r16
                        r3 = r17
                        r4 = r18
                        r0.<init>(r1, r2, r3, r4, r5, r7)
                        java.lang.Object r0 = r14.invoke()
                        j$.util.stream.Node r0 = (j$.util.stream.Node) r0
                        return r0
                    */
                    throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.SliceOps.AnonymousClass4.opEvaluateParallel(j$.util.stream.PipelineHelper, j$.util.Spliterator, j$.util.function.IntFunction):j$.util.stream.Node");
                }

                /* access modifiers changed from: package-private */
                public Sink<Double> opWrapSink(int flags, Sink<Double> sink) {
                    return new Sink.ChainedDouble<Double>(sink) {
                        long m;
                        long n;

                        {
                            this.n = j;
                            this.m = j2 >= 0 ? j2 : Long.MAX_VALUE;
                        }

                        public void begin(long size) {
                            this.downstream.begin(SliceOps.calcSize(size, j, this.m));
                        }

                        public void accept(double t) {
                            long j = this.n;
                            if (j == 0) {
                                long j2 = this.m;
                                if (j2 > 0) {
                                    this.m = j2 - 1;
                                    this.downstream.accept(t);
                                    return;
                                }
                                return;
                            }
                            this.n = j - 1;
                        }

                        public boolean cancellationRequested() {
                            return this.m == 0 || this.downstream.cancellationRequested();
                        }
                    };
                }
            };
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + skip);
    }

    private static int flags(long limit) {
        return StreamOpFlag.NOT_SIZED | (limit != -1 ? StreamOpFlag.IS_SHORT_CIRCUIT : 0);
    }

    private static final class SliceTask<P_IN, P_OUT> extends AbstractShortCircuitTask<P_IN, P_OUT, Node<P_OUT>, SliceTask<P_IN, P_OUT>> {
        private volatile boolean completed;
        private final IntFunction<P_OUT[]> generator;
        private final AbstractPipeline<P_OUT, P_OUT, ?> op;
        private final long targetOffset;
        private final long targetSize;
        private long thisNodeSize;

        SliceTask(AbstractPipeline<P_OUT, P_OUT, ?> op2, PipelineHelper<P_OUT> helper, Spliterator<P_IN> spliterator, IntFunction<P_OUT[]> intFunction, long offset, long size) {
            super(helper, spliterator);
            this.op = op2;
            this.generator = intFunction;
            this.targetOffset = offset;
            this.targetSize = size;
        }

        SliceTask(SliceTask<P_IN, P_OUT> parent, Spliterator<P_IN> spliterator) {
            super(parent, spliterator);
            this.op = parent.op;
            this.generator = parent.generator;
            this.targetOffset = parent.targetOffset;
            this.targetSize = parent.targetSize;
        }

        /* access modifiers changed from: protected */
        public SliceTask<P_IN, P_OUT> makeChild(Spliterator<P_IN> spliterator) {
            return new SliceTask<>(this, spliterator);
        }

        /* access modifiers changed from: protected */
        public final Node<P_OUT> getEmptyResult() {
            return Nodes.emptyNode(this.op.getOutputShape());
        }

        /* access modifiers changed from: protected */
        public final Node<P_OUT> doLeaf() {
            long sizeIfKnown = -1;
            if (isRoot()) {
                if (StreamOpFlag.SIZED.isPreserved(this.op.sourceOrOpFlags)) {
                    sizeIfKnown = this.op.exactOutputSizeIfKnown(this.spliterator);
                }
                Node.Builder<P_OUT> nb = this.op.makeNodeBuilder(sizeIfKnown, this.generator);
                this.helper.copyIntoWithCancel(this.helper.wrapSink(this.op.opWrapSink(this.helper.getStreamAndOpFlags(), nb)), this.spliterator);
                return nb.build();
            }
            Node<P_OUT> node = ((Node.Builder) this.helper.wrapAndCopyInto(this.helper.makeNodeBuilder(-1, this.generator), this.spliterator)).build();
            this.thisNodeSize = node.count();
            this.completed = true;
            this.spliterator = null;
            return node;
        }

        public final void onCompletion(CountedCompleter<?> caller) {
            java.util.stream.Node<P_OUT> result;
            if (!isLeaf()) {
                this.thisNodeSize = ((SliceTask) this.leftChild).thisNodeSize + ((SliceTask) this.rightChild).thisNodeSize;
                if (this.canceled) {
                    this.thisNodeSize = 0;
                    result = getEmptyResult();
                } else if (this.thisNodeSize == 0) {
                    result = getEmptyResult();
                } else if (((SliceTask) this.leftChild).thisNodeSize == 0) {
                    result = (Node) ((SliceTask) this.rightChild).getLocalResult();
                } else {
                    result = Nodes.conc(this.op.getOutputShape(), (Node) ((SliceTask) this.leftChild).getLocalResult(), (Node) ((SliceTask) this.rightChild).getLocalResult());
                }
                setLocalResult(isRoot() ? doTruncate(result) : result);
                this.completed = true;
            }
            if (this.targetSize >= 0 && !isRoot() && isLeftCompleted(this.targetOffset + this.targetSize)) {
                cancelLaterNodes();
            }
            super.onCompletion(caller);
        }

        /* access modifiers changed from: protected */
        public void cancel() {
            super.cancel();
            if (this.completed) {
                setLocalResult(getEmptyResult());
            }
        }

        private Node<P_OUT> doTruncate(Node<P_OUT> input) {
            return input.truncate(this.targetOffset, this.targetSize >= 0 ? Math.min(input.count(), this.targetOffset + this.targetSize) : this.thisNodeSize, this.generator);
        }

        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private boolean isLeftCompleted(long r9) {
            /*
                r8 = this;
                boolean r0 = r8.completed
                if (r0 == 0) goto L_0x0007
                long r0 = r8.thisNodeSize
                goto L_0x000b
            L_0x0007:
                long r0 = r8.completedSize(r9)
            L_0x000b:
                r2 = 1
                int r3 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
                if (r3 < 0) goto L_0x0011
                return r2
            L_0x0011:
                j$.util.stream.AbstractTask r3 = r8.getParent()
                j$.util.stream.SliceOps$SliceTask r3 = (j$.util.stream.SliceOps.SliceTask) r3
                r4 = r8
            L_0x0018:
                if (r3 == 0) goto L_0x0037
                j$.util.stream.AbstractTask r5 = r3.rightChild
                if (r4 != r5) goto L_0x002e
                j$.util.stream.AbstractTask r5 = r3.leftChild
                j$.util.stream.SliceOps$SliceTask r5 = (j$.util.stream.SliceOps.SliceTask) r5
                if (r5 == 0) goto L_0x002e
                long r6 = r5.completedSize(r9)
                long r0 = r0 + r6
                int r6 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
                if (r6 < 0) goto L_0x002e
                return r2
            L_0x002e:
                r4 = r3
                j$.util.stream.AbstractTask r5 = r3.getParent()
                r3 = r5
                j$.util.stream.SliceOps$SliceTask r3 = (j$.util.stream.SliceOps.SliceTask) r3
                goto L_0x0018
            L_0x0037:
                int r3 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
                if (r3 < 0) goto L_0x003c
                goto L_0x003d
            L_0x003c:
                r2 = 0
            L_0x003d:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.SliceOps.SliceTask.isLeftCompleted(long):boolean");
        }

        private long completedSize(long target) {
            if (this.completed) {
                return this.thisNodeSize;
            }
            SliceOps.SliceTask<P_IN, P_OUT> left = (SliceTask) this.leftChild;
            SliceOps.SliceTask<P_IN, P_OUT> right = (SliceTask) this.rightChild;
            if (left == null || right == null) {
                return this.thisNodeSize;
            }
            long leftSize = left.completedSize(target);
            return leftSize >= target ? leftSize : right.completedSize(target) + leftSize;
        }
    }
}
