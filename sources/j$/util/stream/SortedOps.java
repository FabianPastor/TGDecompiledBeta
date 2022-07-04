package j$.util.stream;

import j$.util.Collection;
import j$.util.Comparator;
import j$.util.List;
import j$.util.stream.DoublePipeline;
import j$.util.stream.IntPipeline;
import j$.util.stream.LongPipeline;
import j$.util.stream.ReferencePipeline;
import j$.util.stream.Sink;
import j$.util.stream.SpinedBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

final class SortedOps {
    private SortedOps() {
    }

    static <T> Stream<T> makeRef(AbstractPipeline<?, T, ?> upstream) {
        return new OfRef(upstream);
    }

    static <T> Stream<T> makeRef(AbstractPipeline<?, T, ?> upstream, Comparator<? super T> comparator) {
        return new OfRef(upstream, comparator);
    }

    static <T> IntStream makeInt(AbstractPipeline<?, Integer, ?> upstream) {
        return new OfInt(upstream);
    }

    static <T> LongStream makeLong(AbstractPipeline<?, Long, ?> upstream) {
        return new OfLong(upstream);
    }

    static <T> DoubleStream makeDouble(AbstractPipeline<?, Double, ?> upstream) {
        return new OfDouble(upstream);
    }

    private static final class OfRef<T> extends ReferencePipeline.StatefulOp<T, T> {
        private final Comparator<? super T> comparator;
        private final boolean isNaturalSort;

        OfRef(AbstractPipeline<?, T, ?> upstream) {
            super(upstream, StreamShape.REFERENCE, StreamOpFlag.IS_ORDERED | StreamOpFlag.IS_SORTED);
            this.isNaturalSort = true;
            this.comparator = Comparator.CC.naturalOrder();
        }

        OfRef(AbstractPipeline<?, T, ?> upstream, java.util.Comparator<? super T> comparator2) {
            super(upstream, StreamShape.REFERENCE, StreamOpFlag.IS_ORDERED | StreamOpFlag.NOT_SORTED);
            this.isNaturalSort = false;
            comparator2.getClass();
            this.comparator = comparator2;
        }

        public Sink<T> opWrapSink(int flags, Sink<T> sink) {
            sink.getClass();
            if (StreamOpFlag.SORTED.isKnown(flags) && this.isNaturalSort) {
                return sink;
            }
            if (StreamOpFlag.SIZED.isKnown(flags)) {
                return new SizedRefSortingSink(sink, this.comparator);
            }
            return new RefSortingSink(sink, this.comparator);
        }

        /* JADX WARNING: type inference failed for: r4v0, types: [j$.util.Spliterator, j$.util.Spliterator<P_IN>] */
        /* JADX WARNING: type inference failed for: r5v0, types: [j$.util.function.IntFunction, j$.util.function.IntFunction<T[]>] */
        /* JADX WARNING: Unknown variable types count: 2 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public <P_IN> j$.util.stream.Node<T> opEvaluateParallel(j$.util.stream.PipelineHelper<T> r3, j$.util.Spliterator<P_IN> r4, j$.util.function.IntFunction<T[]> r5) {
            /*
                r2 = this;
                j$.util.stream.StreamOpFlag r0 = j$.util.stream.StreamOpFlag.SORTED
                int r1 = r3.getStreamAndOpFlags()
                boolean r0 = r0.isKnown(r1)
                if (r0 == 0) goto L_0x0016
                boolean r0 = r2.isNaturalSort
                if (r0 == 0) goto L_0x0016
                r0 = 0
                j$.util.stream.Node r0 = r3.evaluate(r4, r0, r5)
                return r0
            L_0x0016:
                r0 = 1
                j$.util.stream.Node r0 = r3.evaluate(r4, r0, r5)
                java.lang.Object[] r0 = r0.asArray(r5)
                java.util.Comparator<? super T> r1 = r2.comparator
                java.util.Arrays.sort(r0, r1)
                j$.util.stream.Node r1 = j$.util.stream.Nodes.node((T[]) r0)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.SortedOps.OfRef.opEvaluateParallel(j$.util.stream.PipelineHelper, j$.util.Spliterator, j$.util.function.IntFunction):j$.util.stream.Node");
        }
    }

    private static final class OfInt extends IntPipeline.StatefulOp<Integer> {
        OfInt(AbstractPipeline<?, Integer, ?> upstream) {
            super(upstream, StreamShape.INT_VALUE, StreamOpFlag.IS_ORDERED | StreamOpFlag.IS_SORTED);
        }

        public Sink<Integer> opWrapSink(int flags, Sink<Integer> sink) {
            sink.getClass();
            if (StreamOpFlag.SORTED.isKnown(flags)) {
                return sink;
            }
            if (StreamOpFlag.SIZED.isKnown(flags)) {
                return new SizedIntSortingSink(sink);
            }
            return new IntSortingSink(sink);
        }

        /* JADX WARNING: type inference failed for: r5v0, types: [j$.util.Spliterator, j$.util.Spliterator<P_IN>] */
        /* JADX WARNING: type inference failed for: r6v0, types: [j$.util.function.IntFunction<java.lang.Integer[]>, j$.util.function.IntFunction] */
        /* JADX WARNING: Unknown variable types count: 2 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public <P_IN> j$.util.stream.Node<java.lang.Integer> opEvaluateParallel(j$.util.stream.PipelineHelper<java.lang.Integer> r4, j$.util.Spliterator<P_IN> r5, j$.util.function.IntFunction<java.lang.Integer[]> r6) {
            /*
                r3 = this;
                j$.util.stream.StreamOpFlag r0 = j$.util.stream.StreamOpFlag.SORTED
                int r1 = r4.getStreamAndOpFlags()
                boolean r0 = r0.isKnown(r1)
                if (r0 == 0) goto L_0x0012
                r0 = 0
                j$.util.stream.Node r0 = r4.evaluate(r5, r0, r6)
                return r0
            L_0x0012:
                r0 = 1
                j$.util.stream.Node r0 = r4.evaluate(r5, r0, r6)
                j$.util.stream.Node$OfInt r0 = (j$.util.stream.Node.OfInt) r0
                java.lang.Object r1 = r0.asPrimitiveArray()
                int[] r1 = (int[]) r1
                java.util.Arrays.sort(r1)
                j$.util.stream.Node$OfInt r2 = j$.util.stream.Nodes.node((int[]) r1)
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.SortedOps.OfInt.opEvaluateParallel(j$.util.stream.PipelineHelper, j$.util.Spliterator, j$.util.function.IntFunction):j$.util.stream.Node");
        }
    }

    private static final class OfLong extends LongPipeline.StatefulOp<Long> {
        OfLong(AbstractPipeline<?, Long, ?> upstream) {
            super(upstream, StreamShape.LONG_VALUE, StreamOpFlag.IS_ORDERED | StreamOpFlag.IS_SORTED);
        }

        public Sink<Long> opWrapSink(int flags, Sink<Long> sink) {
            sink.getClass();
            if (StreamOpFlag.SORTED.isKnown(flags)) {
                return sink;
            }
            if (StreamOpFlag.SIZED.isKnown(flags)) {
                return new SizedLongSortingSink(sink);
            }
            return new LongSortingSink(sink);
        }

        /* JADX WARNING: type inference failed for: r5v0, types: [j$.util.Spliterator, j$.util.Spliterator<P_IN>] */
        /* JADX WARNING: type inference failed for: r6v0, types: [j$.util.function.IntFunction<java.lang.Long[]>, j$.util.function.IntFunction] */
        /* JADX WARNING: Unknown variable types count: 2 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public <P_IN> j$.util.stream.Node<java.lang.Long> opEvaluateParallel(j$.util.stream.PipelineHelper<java.lang.Long> r4, j$.util.Spliterator<P_IN> r5, j$.util.function.IntFunction<java.lang.Long[]> r6) {
            /*
                r3 = this;
                j$.util.stream.StreamOpFlag r0 = j$.util.stream.StreamOpFlag.SORTED
                int r1 = r4.getStreamAndOpFlags()
                boolean r0 = r0.isKnown(r1)
                if (r0 == 0) goto L_0x0012
                r0 = 0
                j$.util.stream.Node r0 = r4.evaluate(r5, r0, r6)
                return r0
            L_0x0012:
                r0 = 1
                j$.util.stream.Node r0 = r4.evaluate(r5, r0, r6)
                j$.util.stream.Node$OfLong r0 = (j$.util.stream.Node.OfLong) r0
                java.lang.Object r1 = r0.asPrimitiveArray()
                long[] r1 = (long[]) r1
                java.util.Arrays.sort(r1)
                j$.util.stream.Node$OfLong r2 = j$.util.stream.Nodes.node((long[]) r1)
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.SortedOps.OfLong.opEvaluateParallel(j$.util.stream.PipelineHelper, j$.util.Spliterator, j$.util.function.IntFunction):j$.util.stream.Node");
        }
    }

    private static final class OfDouble extends DoublePipeline.StatefulOp<Double> {
        OfDouble(AbstractPipeline<?, Double, ?> upstream) {
            super(upstream, StreamShape.DOUBLE_VALUE, StreamOpFlag.IS_ORDERED | StreamOpFlag.IS_SORTED);
        }

        public Sink<Double> opWrapSink(int flags, Sink<Double> sink) {
            sink.getClass();
            if (StreamOpFlag.SORTED.isKnown(flags)) {
                return sink;
            }
            if (StreamOpFlag.SIZED.isKnown(flags)) {
                return new SizedDoubleSortingSink(sink);
            }
            return new DoubleSortingSink(sink);
        }

        /* JADX WARNING: type inference failed for: r5v0, types: [j$.util.Spliterator, j$.util.Spliterator<P_IN>] */
        /* JADX WARNING: type inference failed for: r6v0, types: [j$.util.function.IntFunction<java.lang.Double[]>, j$.util.function.IntFunction] */
        /* JADX WARNING: Unknown variable types count: 2 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public <P_IN> j$.util.stream.Node<java.lang.Double> opEvaluateParallel(j$.util.stream.PipelineHelper<java.lang.Double> r4, j$.util.Spliterator<P_IN> r5, j$.util.function.IntFunction<java.lang.Double[]> r6) {
            /*
                r3 = this;
                j$.util.stream.StreamOpFlag r0 = j$.util.stream.StreamOpFlag.SORTED
                int r1 = r4.getStreamAndOpFlags()
                boolean r0 = r0.isKnown(r1)
                if (r0 == 0) goto L_0x0012
                r0 = 0
                j$.util.stream.Node r0 = r4.evaluate(r5, r0, r6)
                return r0
            L_0x0012:
                r0 = 1
                j$.util.stream.Node r0 = r4.evaluate(r5, r0, r6)
                j$.util.stream.Node$OfDouble r0 = (j$.util.stream.Node.OfDouble) r0
                java.lang.Object r1 = r0.asPrimitiveArray()
                double[] r1 = (double[]) r1
                java.util.Arrays.sort(r1)
                j$.util.stream.Node$OfDouble r2 = j$.util.stream.Nodes.node((double[]) r1)
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.SortedOps.OfDouble.opEvaluateParallel(j$.util.stream.PipelineHelper, j$.util.Spliterator, j$.util.function.IntFunction):j$.util.stream.Node");
        }
    }

    private static abstract class AbstractRefSortingSink<T> extends Sink.ChainedReference<T, T> {
        protected boolean cancellationWasRequested;
        protected final java.util.Comparator<? super T> comparator;

        AbstractRefSortingSink(Sink<? super T> downstream, java.util.Comparator<? super T> comparator2) {
            super(downstream);
            this.comparator = comparator2;
        }

        public final boolean cancellationRequested() {
            this.cancellationWasRequested = true;
            return false;
        }
    }

    private static final class SizedRefSortingSink<T> extends AbstractRefSortingSink<T> {
        private T[] array;
        private int offset;

        SizedRefSortingSink(Sink<? super T> sink, java.util.Comparator<? super T> comparator) {
            super(sink, comparator);
        }

        public void begin(long size) {
            if (size < NUM) {
                this.array = new Object[((int) size)];
                return;
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }

        public void end() {
            Arrays.sort(this.array, 0, this.offset, this.comparator);
            this.downstream.begin((long) this.offset);
            if (!this.cancellationWasRequested) {
                for (int i = 0; i < this.offset; i++) {
                    this.downstream.accept(this.array[i]);
                }
            } else {
                for (int i2 = 0; i2 < this.offset && !this.downstream.cancellationRequested(); i2++) {
                    this.downstream.accept(this.array[i2]);
                }
            }
            this.downstream.end();
            this.array = null;
        }

        public void accept(T t) {
            T[] tArr = this.array;
            int i = this.offset;
            this.offset = i + 1;
            tArr[i] = t;
        }
    }

    private static final class RefSortingSink<T> extends AbstractRefSortingSink<T> {
        private ArrayList<T> list;

        RefSortingSink(Sink<? super T> sink, java.util.Comparator<? super T> comparator) {
            super(sink, comparator);
        }

        public void begin(long size) {
            ArrayList<T> arrayList;
            if (size < NUM) {
                if (size >= 0) {
                    int i = (int) size;
                } else {
                    arrayList = new ArrayList<>();
                }
                this.list = arrayList;
                return;
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }

        public void end() {
            List.EL.sort(this.list, this.comparator);
            this.downstream.begin((long) this.list.size());
            if (!this.cancellationWasRequested) {
                ArrayList<T> arrayList = this.list;
                Sink sink = this.downstream;
                sink.getClass();
                Collection.EL.forEach(arrayList, new SortedOps$RefSortingSink$$ExternalSyntheticLambda0(sink));
            } else {
                Iterator<T> it = this.list.iterator();
                while (it.hasNext()) {
                    T t = it.next();
                    if (this.downstream.cancellationRequested()) {
                        break;
                    }
                    this.downstream.accept(t);
                }
            }
            this.downstream.end();
            this.list = null;
        }

        public void accept(T t) {
            this.list.add(t);
        }
    }

    private static abstract class AbstractIntSortingSink extends Sink.ChainedInt<Integer> {
        protected boolean cancellationWasRequested;

        AbstractIntSortingSink(Sink<? super Integer> downstream) {
            super(downstream);
        }

        public final boolean cancellationRequested() {
            this.cancellationWasRequested = true;
            return false;
        }
    }

    private static final class SizedIntSortingSink extends AbstractIntSortingSink {
        private int[] array;
        private int offset;

        SizedIntSortingSink(Sink<? super Integer> downstream) {
            super(downstream);
        }

        public void begin(long size) {
            if (size < NUM) {
                this.array = new int[((int) size)];
                return;
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }

        public void end() {
            Arrays.sort(this.array, 0, this.offset);
            this.downstream.begin((long) this.offset);
            if (!this.cancellationWasRequested) {
                for (int i = 0; i < this.offset; i++) {
                    this.downstream.accept(this.array[i]);
                }
            } else {
                for (int i2 = 0; i2 < this.offset && !this.downstream.cancellationRequested(); i2++) {
                    this.downstream.accept(this.array[i2]);
                }
            }
            this.downstream.end();
            this.array = null;
        }

        public void accept(int t) {
            int[] iArr = this.array;
            int i = this.offset;
            this.offset = i + 1;
            iArr[i] = t;
        }
    }

    private static final class IntSortingSink extends AbstractIntSortingSink {
        private SpinedBuffer.OfInt b;

        IntSortingSink(Sink<? super Integer> sink) {
            super(sink);
        }

        public void begin(long size) {
            SpinedBuffer.OfInt ofInt;
            if (size < NUM) {
                if (size > 0) {
                    int i = (int) size;
                } else {
                    ofInt = new SpinedBuffer.OfInt();
                }
                this.b = ofInt;
                return;
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }

        public void end() {
            int[] ints = (int[]) this.b.asPrimitiveArray();
            Arrays.sort(ints);
            this.downstream.begin((long) ints.length);
            int i = 0;
            if (!this.cancellationWasRequested) {
                int length = ints.length;
                while (i < length) {
                    this.downstream.accept(ints[i]);
                    i++;
                }
            } else {
                int length2 = ints.length;
                while (i < length2) {
                    int anInt = ints[i];
                    if (this.downstream.cancellationRequested()) {
                        break;
                    }
                    this.downstream.accept(anInt);
                    i++;
                }
            }
            this.downstream.end();
        }

        public void accept(int t) {
            this.b.accept(t);
        }
    }

    private static abstract class AbstractLongSortingSink extends Sink.ChainedLong<Long> {
        protected boolean cancellationWasRequested;

        AbstractLongSortingSink(Sink<? super Long> downstream) {
            super(downstream);
        }

        public final boolean cancellationRequested() {
            this.cancellationWasRequested = true;
            return false;
        }
    }

    private static final class SizedLongSortingSink extends AbstractLongSortingSink {
        private long[] array;
        private int offset;

        SizedLongSortingSink(Sink<? super Long> downstream) {
            super(downstream);
        }

        public void begin(long size) {
            if (size < NUM) {
                this.array = new long[((int) size)];
                return;
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }

        public void end() {
            Arrays.sort(this.array, 0, this.offset);
            this.downstream.begin((long) this.offset);
            if (!this.cancellationWasRequested) {
                for (int i = 0; i < this.offset; i++) {
                    this.downstream.accept(this.array[i]);
                }
            } else {
                for (int i2 = 0; i2 < this.offset && !this.downstream.cancellationRequested(); i2++) {
                    this.downstream.accept(this.array[i2]);
                }
            }
            this.downstream.end();
            this.array = null;
        }

        public void accept(long t) {
            long[] jArr = this.array;
            int i = this.offset;
            this.offset = i + 1;
            jArr[i] = t;
        }
    }

    private static final class LongSortingSink extends AbstractLongSortingSink {
        private SpinedBuffer.OfLong b;

        LongSortingSink(Sink<? super Long> sink) {
            super(sink);
        }

        public void begin(long size) {
            SpinedBuffer.OfLong ofLong;
            if (size < NUM) {
                if (size > 0) {
                    int i = (int) size;
                } else {
                    ofLong = new SpinedBuffer.OfLong();
                }
                this.b = ofLong;
                return;
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }

        public void end() {
            long[] longs = (long[]) this.b.asPrimitiveArray();
            Arrays.sort(longs);
            this.downstream.begin((long) longs.length);
            int i = 0;
            if (!this.cancellationWasRequested) {
                int length = longs.length;
                while (i < length) {
                    this.downstream.accept(longs[i]);
                    i++;
                }
            } else {
                int length2 = longs.length;
                while (i < length2) {
                    long aLong = longs[i];
                    if (this.downstream.cancellationRequested()) {
                        break;
                    }
                    this.downstream.accept(aLong);
                    i++;
                }
            }
            this.downstream.end();
        }

        public void accept(long t) {
            this.b.accept(t);
        }
    }

    private static abstract class AbstractDoubleSortingSink extends Sink.ChainedDouble<Double> {
        protected boolean cancellationWasRequested;

        AbstractDoubleSortingSink(Sink<? super Double> downstream) {
            super(downstream);
        }

        public final boolean cancellationRequested() {
            this.cancellationWasRequested = true;
            return false;
        }
    }

    private static final class SizedDoubleSortingSink extends AbstractDoubleSortingSink {
        private double[] array;
        private int offset;

        SizedDoubleSortingSink(Sink<? super Double> downstream) {
            super(downstream);
        }

        public void begin(long size) {
            if (size < NUM) {
                this.array = new double[((int) size)];
                return;
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }

        public void end() {
            Arrays.sort(this.array, 0, this.offset);
            this.downstream.begin((long) this.offset);
            if (!this.cancellationWasRequested) {
                for (int i = 0; i < this.offset; i++) {
                    this.downstream.accept(this.array[i]);
                }
            } else {
                for (int i2 = 0; i2 < this.offset && !this.downstream.cancellationRequested(); i2++) {
                    this.downstream.accept(this.array[i2]);
                }
            }
            this.downstream.end();
            this.array = null;
        }

        public void accept(double t) {
            double[] dArr = this.array;
            int i = this.offset;
            this.offset = i + 1;
            dArr[i] = t;
        }
    }

    private static final class DoubleSortingSink extends AbstractDoubleSortingSink {
        private SpinedBuffer.OfDouble b;

        DoubleSortingSink(Sink<? super Double> sink) {
            super(sink);
        }

        public void begin(long size) {
            SpinedBuffer.OfDouble ofDouble;
            if (size < NUM) {
                if (size > 0) {
                    int i = (int) size;
                } else {
                    ofDouble = new SpinedBuffer.OfDouble();
                }
                this.b = ofDouble;
                return;
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }

        public void end() {
            double[] doubles = (double[]) this.b.asPrimitiveArray();
            Arrays.sort(doubles);
            this.downstream.begin((long) doubles.length);
            int i = 0;
            if (!this.cancellationWasRequested) {
                int length = doubles.length;
                while (i < length) {
                    this.downstream.accept(doubles[i]);
                    i++;
                }
            } else {
                int length2 = doubles.length;
                while (i < length2) {
                    double aDouble = doubles[i];
                    if (this.downstream.cancellationRequested()) {
                        break;
                    }
                    this.downstream.accept(aDouble);
                    i++;
                }
            }
            this.downstream.end();
        }

        public void accept(double t) {
            this.b.accept(t);
        }
    }
}
