package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.DoubleConsumer;
import j$.util.function.IntConsumer;
import j$.util.function.IntFunction;
import j$.util.function.LongConsumer;
import j$.util.stream.Sink;
import java.util.stream.Node;

interface Node<T> {

    public interface Builder<T> extends Sink<T> {

        public interface OfDouble extends Builder<Double>, Sink.OfDouble {

            /* renamed from: j$.util.stream.Node$Builder$OfDouble$-CC  reason: invalid class name */
            public final /* synthetic */ class CC {
            }

            OfDouble build();
        }

        public interface OfInt extends Builder<Integer>, Sink.OfInt {

            /* renamed from: j$.util.stream.Node$Builder$OfInt$-CC  reason: invalid class name */
            public final /* synthetic */ class CC {
            }

            OfInt build();
        }

        public interface OfLong extends Builder<Long>, Sink.OfLong {

            /* renamed from: j$.util.stream.Node$Builder$OfLong$-CC  reason: invalid class name */
            public final /* synthetic */ class CC {
            }

            OfLong build();
        }

        Node<T> build();
    }

    T[] asArray(IntFunction<T[]> intFunction);

    void copyInto(T[] tArr, int i);

    long count();

    void forEach(Consumer<? super T> consumer);

    Node<T> getChild(int i);

    int getChildCount();

    StreamShape getShape();

    Spliterator<T> spliterator();

    Node<T> truncate(long j, long j2, IntFunction<T[]> intFunction);

    /* renamed from: j$.util.stream.Node$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static int $default$getChildCount(Node _this) {
            return 0;
        }

        public static Node $default$getChild(Node _this, int i) {
            throw new IndexOutOfBoundsException();
        }

        public static Node $default$truncate(Node _this, long from, long to, IntFunction intFunction) {
            if (from == 0 && to == _this.count()) {
                return _this;
            }
            Spliterator spliterator = _this.spliterator();
            long size = to - from;
            Node.Builder<T> nodeBuilder = Nodes.builder(size, intFunction);
            nodeBuilder.begin(size);
            for (int i = 0; ((long) i) < from && spliterator.tryAdvance(Node$$ExternalSyntheticLambda0.INSTANCE); i++) {
            }
            for (int i2 = 0; ((long) i2) < size && spliterator.tryAdvance(nodeBuilder); i2++) {
            }
            nodeBuilder.end();
            return nodeBuilder.build();
        }

        public static /* synthetic */ void lambda$truncate$0(Object e) {
        }

        public static StreamShape $default$getShape(Node _this) {
            return StreamShape.REFERENCE;
        }
    }

    public interface OfPrimitive<T, T_CONS, T_ARR, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>, T_NODE extends OfPrimitive<T, T_CONS, T_ARR, T_SPLITR, T_NODE>> extends Node<T> {
        T[] asArray(IntFunction<T[]> intFunction);

        T_ARR asPrimitiveArray();

        void copyInto(T_ARR t_arr, int i);

        void forEach(T_CONS t_cons);

        T_NODE getChild(int i);

        T_ARR newArray(int i);

        T_SPLITR spliterator();

        T_NODE truncate(long j, long j2, IntFunction<T[]> intFunction);

        /* renamed from: j$.util.stream.Node$OfPrimitive$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static OfPrimitive $default$getChild(OfPrimitive _this, int i) {
                throw new IndexOutOfBoundsException();
            }

            public static Object[] $default$asArray(OfPrimitive _this, IntFunction intFunction) {
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling Node.OfPrimitive.asArray");
                }
                if (_this.count() < NUM) {
                    T[] boxed = (Object[]) intFunction.apply((int) _this.count());
                    _this.copyInto(boxed, 0);
                    return boxed;
                }
                throw new IllegalArgumentException("Stream size exceeds max array size");
            }
        }
    }

    public interface OfInt extends OfPrimitive<Integer, IntConsumer, int[], Spliterator.OfInt, OfInt> {
        void copyInto(Integer[] numArr, int i);

        void forEach(Consumer<? super Integer> consumer);

        StreamShape getShape();

        int[] newArray(int i);

        OfInt truncate(long j, long j2, IntFunction<Integer[]> intFunction);

        /* renamed from: j$.util.stream.Node$OfInt$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$forEach(OfInt _this, Consumer consumer) {
                if (consumer instanceof IntConsumer) {
                    _this.forEach((IntConsumer) consumer);
                    return;
                }
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling Node.OfInt.forEachRemaining(Consumer)");
                }
                ((Spliterator.OfInt) _this.spliterator()).forEachRemaining((Consumer<? super Integer>) consumer);
            }

            public static void $default$copyInto(OfInt _this, Integer[] boxed, int offset) {
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling Node.OfInt.copyInto(Integer[], int)");
                }
                int[] array = (int[]) _this.asPrimitiveArray();
                for (int i = 0; i < array.length; i++) {
                    boxed[offset + i] = Integer.valueOf(array[i]);
                }
            }

            public static OfInt $default$truncate(OfInt _this, long from, long to, IntFunction intFunction) {
                if (from == 0 && to == _this.count()) {
                    return _this;
                }
                long size = to - from;
                Spliterator.OfInt spliterator = (Spliterator.OfInt) _this.spliterator();
                Builder.OfInt nodeBuilder = Nodes.intBuilder(size);
                nodeBuilder.begin(size);
                for (int i = 0; ((long) i) < from && spliterator.tryAdvance((IntConsumer) Node$OfInt$$ExternalSyntheticLambda0.INSTANCE); i++) {
                }
                for (int i2 = 0; ((long) i2) < size && spliterator.tryAdvance((IntConsumer) nodeBuilder); i2++) {
                }
                nodeBuilder.end();
                return nodeBuilder.build();
            }

            public static /* synthetic */ void lambda$truncate$0(int e) {
            }

            public static int[] $default$newArray(OfInt _this, int count) {
                return new int[count];
            }

            public static StreamShape $default$getShape(OfInt _this) {
                return StreamShape.INT_VALUE;
            }
        }
    }

    public interface OfLong extends OfPrimitive<Long, LongConsumer, long[], Spliterator.OfLong, OfLong> {
        void copyInto(Long[] lArr, int i);

        void forEach(Consumer<? super Long> consumer);

        StreamShape getShape();

        long[] newArray(int i);

        OfLong truncate(long j, long j2, IntFunction<Long[]> intFunction);

        /* renamed from: j$.util.stream.Node$OfLong$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$forEach(OfLong _this, Consumer consumer) {
                if (consumer instanceof LongConsumer) {
                    _this.forEach((LongConsumer) consumer);
                    return;
                }
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling Node.OfLong.forEachRemaining(Consumer)");
                }
                ((Spliterator.OfLong) _this.spliterator()).forEachRemaining((Consumer<? super Long>) consumer);
            }

            public static void $default$copyInto(OfLong _this, Long[] boxed, int offset) {
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling Node.OfInt.copyInto(Long[], int)");
                }
                long[] array = (long[]) _this.asPrimitiveArray();
                for (int i = 0; i < array.length; i++) {
                    boxed[offset + i] = Long.valueOf(array[i]);
                }
            }

            public static OfLong $default$truncate(OfLong _this, long from, long to, IntFunction intFunction) {
                if (from == 0 && to == _this.count()) {
                    return _this;
                }
                long size = to - from;
                Spliterator.OfLong spliterator = (Spliterator.OfLong) _this.spliterator();
                Builder.OfLong nodeBuilder = Nodes.longBuilder(size);
                nodeBuilder.begin(size);
                for (int i = 0; ((long) i) < from && spliterator.tryAdvance((LongConsumer) Node$OfLong$$ExternalSyntheticLambda0.INSTANCE); i++) {
                }
                for (int i2 = 0; ((long) i2) < size && spliterator.tryAdvance((LongConsumer) nodeBuilder); i2++) {
                }
                nodeBuilder.end();
                return nodeBuilder.build();
            }

            public static /* synthetic */ void lambda$truncate$0(long e) {
            }

            public static long[] $default$newArray(OfLong _this, int count) {
                return new long[count];
            }

            public static StreamShape $default$getShape(OfLong _this) {
                return StreamShape.LONG_VALUE;
            }
        }
    }

    public interface OfDouble extends OfPrimitive<Double, DoubleConsumer, double[], Spliterator.OfDouble, OfDouble> {
        void copyInto(Double[] dArr, int i);

        void forEach(Consumer<? super Double> consumer);

        StreamShape getShape();

        double[] newArray(int i);

        OfDouble truncate(long j, long j2, IntFunction<Double[]> intFunction);

        /* renamed from: j$.util.stream.Node$OfDouble$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$forEach(OfDouble _this, Consumer consumer) {
                if (consumer instanceof DoubleConsumer) {
                    _this.forEach((DoubleConsumer) consumer);
                    return;
                }
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling Node.OfLong.forEachRemaining(Consumer)");
                }
                ((Spliterator.OfDouble) _this.spliterator()).forEachRemaining((Consumer<? super Double>) consumer);
            }

            public static void $default$copyInto(OfDouble _this, Double[] boxed, int offset) {
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling Node.OfDouble.copyInto(Double[], int)");
                }
                double[] array = (double[]) _this.asPrimitiveArray();
                for (int i = 0; i < array.length; i++) {
                    boxed[offset + i] = Double.valueOf(array[i]);
                }
            }

            public static OfDouble $default$truncate(OfDouble _this, long from, long to, IntFunction intFunction) {
                if (from == 0 && to == _this.count()) {
                    return _this;
                }
                long size = to - from;
                Spliterator.OfDouble spliterator = (Spliterator.OfDouble) _this.spliterator();
                Builder.OfDouble nodeBuilder = Nodes.doubleBuilder(size);
                nodeBuilder.begin(size);
                for (int i = 0; ((long) i) < from && spliterator.tryAdvance((DoubleConsumer) Node$OfDouble$$ExternalSyntheticLambda0.INSTANCE); i++) {
                }
                for (int i2 = 0; ((long) i2) < size && spliterator.tryAdvance((DoubleConsumer) nodeBuilder); i2++) {
                }
                nodeBuilder.end();
                return nodeBuilder.build();
            }

            public static /* synthetic */ void lambda$truncate$0(double e) {
            }

            public static double[] $default$newArray(OfDouble _this, int count) {
                return new double[count];
            }

            public static StreamShape $default$getShape(OfDouble _this) {
                return StreamShape.DOUBLE_VALUE;
            }
        }
    }
}
