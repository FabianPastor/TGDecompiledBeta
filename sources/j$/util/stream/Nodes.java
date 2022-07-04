package j$.util.stream;

import j$.util.Collection;
import j$.util.DesugarArrays;
import j$.util.Spliterator;
import j$.util.Spliterators;
import j$.util.function.BinaryOperator;
import j$.util.function.Consumer;
import j$.util.function.DoubleConsumer;
import j$.util.function.IntConsumer;
import j$.util.function.IntFunction;
import j$.util.function.LongConsumer;
import j$.util.function.LongFunction;
import j$.util.stream.Node;
import j$.util.stream.Sink;
import j$.util.stream.SpinedBuffer;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.concurrent.CountedCompleter;

final class Nodes {
    static final String BAD_SIZE = "Stream size exceeds max array size";
    /* access modifiers changed from: private */
    public static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
    private static final Node.OfDouble EMPTY_DOUBLE_NODE = new EmptyNode.OfDouble();
    /* access modifiers changed from: private */
    public static final int[] EMPTY_INT_ARRAY = new int[0];
    private static final Node.OfInt EMPTY_INT_NODE = new EmptyNode.OfInt();
    /* access modifiers changed from: private */
    public static final long[] EMPTY_LONG_ARRAY = new long[0];
    private static final Node.OfLong EMPTY_LONG_NODE = new EmptyNode.OfLong();
    private static final Node EMPTY_NODE = new EmptyNode.OfRef((AnonymousClass1) null);
    static final long MAX_ARRAY_SIZE = NUM;

    private Nodes() {
        throw new Error("no instances");
    }

    /* renamed from: j$.util.stream.Nodes$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
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

    static <T> Node<T> emptyNode(StreamShape shape) {
        switch (AnonymousClass1.$SwitchMap$java$util$stream$StreamShape[shape.ordinal()]) {
            case 1:
                return EMPTY_NODE;
            case 2:
                return EMPTY_INT_NODE;
            case 3:
                return EMPTY_LONG_NODE;
            case 4:
                return EMPTY_DOUBLE_NODE;
            default:
                throw new IllegalStateException("Unknown shape " + shape);
        }
    }

    static <T> Node<T> conc(StreamShape shape, Node<T> left, Node<T> right) {
        switch (AnonymousClass1.$SwitchMap$java$util$stream$StreamShape[shape.ordinal()]) {
            case 1:
                return new ConcNode(left, right);
            case 2:
                return new ConcNode.OfInt((Node.OfInt) left, (Node.OfInt) right);
            case 3:
                return new ConcNode.OfLong((Node.OfLong) left, (Node.OfLong) right);
            case 4:
                return new ConcNode.OfDouble((Node.OfDouble) left, (Node.OfDouble) right);
            default:
                throw new IllegalStateException("Unknown shape " + shape);
        }
    }

    static <T> Node<T> node(T[] array) {
        return new ArrayNode(array);
    }

    static <T> Node<T> node(Collection<T> c) {
        return new CollectionNode(c);
    }

    /* access modifiers changed from: package-private */
    public static <T> Node.Builder<T> builder(long exactSizeIfKnown, IntFunction<T[]> intFunction) {
        if (exactSizeIfKnown < 0 || exactSizeIfKnown >= NUM) {
            return builder();
        }
        return new FixedNodeBuilder(exactSizeIfKnown, intFunction);
    }

    static <T> Node.Builder<T> builder() {
        return new SpinedNodeBuilder();
    }

    static Node.OfInt node(int[] array) {
        return new IntArrayNode(array);
    }

    static Node.Builder.OfInt intBuilder(long exactSizeIfKnown) {
        if (exactSizeIfKnown < 0 || exactSizeIfKnown >= NUM) {
            return intBuilder();
        }
        return new IntFixedNodeBuilder(exactSizeIfKnown);
    }

    static Node.Builder.OfInt intBuilder() {
        return new IntSpinedNodeBuilder();
    }

    static Node.OfLong node(long[] array) {
        return new LongArrayNode(array);
    }

    static Node.Builder.OfLong longBuilder(long exactSizeIfKnown) {
        if (exactSizeIfKnown < 0 || exactSizeIfKnown >= NUM) {
            return longBuilder();
        }
        return new LongFixedNodeBuilder(exactSizeIfKnown);
    }

    static Node.Builder.OfLong longBuilder() {
        return new LongSpinedNodeBuilder();
    }

    static Node.OfDouble node(double[] array) {
        return new DoubleArrayNode(array);
    }

    static Node.Builder.OfDouble doubleBuilder(long exactSizeIfKnown) {
        if (exactSizeIfKnown < 0 || exactSizeIfKnown >= NUM) {
            return doubleBuilder();
        }
        return new DoubleFixedNodeBuilder(exactSizeIfKnown);
    }

    static Node.Builder.OfDouble doubleBuilder() {
        return new DoubleSpinedNodeBuilder();
    }

    public static <P_IN, P_OUT> Node<P_OUT> collect(PipelineHelper<P_OUT> helper, Spliterator<P_IN> spliterator, boolean flattenTree, IntFunction<P_OUT[]> intFunction) {
        long size = helper.exactOutputSizeIfKnown(spliterator);
        if (size < 0 || !spliterator.hasCharacteristics(16384)) {
            java.util.stream.Node<P_OUT> node = (Node) new CollectorTask.OfRef(helper, intFunction, spliterator).invoke();
            return flattenTree ? flatten(node, intFunction) : node;
        } else if (size < NUM) {
            P_OUT[] array = (Object[]) intFunction.apply((int) size);
            new SizedCollectorTask.OfRef(spliterator, helper, array).invoke();
            return node((T[]) array);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static <P_IN> Node.OfInt collectInt(PipelineHelper<Integer> helper, Spliterator<P_IN> spliterator, boolean flattenTree) {
        long size = helper.exactOutputSizeIfKnown(spliterator);
        if (size < 0 || !spliterator.hasCharacteristics(16384)) {
            Node.OfInt node = (Node.OfInt) new CollectorTask.OfInt(helper, spliterator).invoke();
            return flattenTree ? flattenInt(node) : node;
        } else if (size < NUM) {
            int[] array = new int[((int) size)];
            new SizedCollectorTask.OfInt(spliterator, helper, array).invoke();
            return node(array);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static <P_IN> Node.OfLong collectLong(PipelineHelper<Long> helper, Spliterator<P_IN> spliterator, boolean flattenTree) {
        long size = helper.exactOutputSizeIfKnown(spliterator);
        if (size < 0 || !spliterator.hasCharacteristics(16384)) {
            Node.OfLong node = (Node.OfLong) new CollectorTask.OfLong(helper, spliterator).invoke();
            return flattenTree ? flattenLong(node) : node;
        } else if (size < NUM) {
            long[] array = new long[((int) size)];
            new SizedCollectorTask.OfLong(spliterator, helper, array).invoke();
            return node(array);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static <P_IN> Node.OfDouble collectDouble(PipelineHelper<Double> helper, Spliterator<P_IN> spliterator, boolean flattenTree) {
        long size = helper.exactOutputSizeIfKnown(spliterator);
        if (size < 0 || !spliterator.hasCharacteristics(16384)) {
            Node.OfDouble node = (Node.OfDouble) new CollectorTask.OfDouble(helper, spliterator).invoke();
            return flattenTree ? flattenDouble(node) : node;
        } else if (size < NUM) {
            double[] array = new double[((int) size)];
            new SizedCollectorTask.OfDouble(spliterator, helper, array).invoke();
            return node(array);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static <T> Node<T> flatten(Node<T> node, IntFunction<T[]> intFunction) {
        if (node.getChildCount() <= 0) {
            return node;
        }
        long size = node.count();
        if (size < NUM) {
            T[] array = (Object[]) intFunction.apply((int) size);
            new ToArrayTask.OfRef(node, array, 0, (AnonymousClass1) null).invoke();
            return node(array);
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public static Node.OfInt flattenInt(Node.OfInt node) {
        if (node.getChildCount() <= 0) {
            return node;
        }
        long size = node.count();
        if (size < NUM) {
            int[] array = new int[((int) size)];
            new ToArrayTask.OfInt(node, array, 0, (AnonymousClass1) null).invoke();
            return node(array);
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public static Node.OfLong flattenLong(Node.OfLong node) {
        if (node.getChildCount() <= 0) {
            return node;
        }
        long size = node.count();
        if (size < NUM) {
            long[] array = new long[((int) size)];
            new ToArrayTask.OfLong(node, array, 0, (AnonymousClass1) null).invoke();
            return node(array);
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public static Node.OfDouble flattenDouble(Node.OfDouble node) {
        if (node.getChildCount() <= 0) {
            return node;
        }
        long size = node.count();
        if (size < NUM) {
            double[] array = new double[((int) size)];
            new ToArrayTask.OfDouble(node, array, 0, (AnonymousClass1) null).invoke();
            return node(array);
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    private static abstract class EmptyNode<T, T_ARR, T_CONS> implements Node<T> {
        public /* synthetic */ Node getChild(int i) {
            return Node.CC.$default$getChild(this, i);
        }

        public /* synthetic */ int getChildCount() {
            return Node.CC.$default$getChildCount(this);
        }

        public /* synthetic */ StreamShape getShape() {
            return Node.CC.$default$getShape(this);
        }

        public /* synthetic */ Node truncate(long j, long j2, IntFunction intFunction) {
            return Node.CC.$default$truncate(this, j, j2, intFunction);
        }

        EmptyNode() {
        }

        public T[] asArray(IntFunction<T[]> intFunction) {
            return (Object[]) intFunction.apply(0);
        }

        public void copyInto(T_ARR t_arr, int offset) {
        }

        public long count() {
            return 0;
        }

        public void forEach(T_CONS t_cons) {
        }

        private static class OfRef<T> extends EmptyNode<T, T[], Consumer<? super T>> {
            /* synthetic */ OfRef(AnonymousClass1 x0) {
                this();
            }

            public /* bridge */ /* synthetic */ void copyInto(Object[] objArr, int i) {
                super.copyInto(objArr, i);
            }

            public /* bridge */ /* synthetic */ void forEach(Consumer consumer) {
                super.forEach(consumer);
            }

            private OfRef() {
            }

            public Spliterator<T> spliterator() {
                return Spliterators.emptySpliterator();
            }
        }

        private static final class OfInt extends EmptyNode<Integer, int[], IntConsumer> implements Node.OfInt {
            public /* synthetic */ void copyInto(Integer[] numArr, int i) {
                Node.OfInt.CC.$default$copyInto((Node.OfInt) this, numArr, i);
            }

            public /* bridge */ /* synthetic */ void copyInto(Object[] objArr, int i) {
                copyInto((Integer[]) objArr, i);
            }

            public /* synthetic */ void forEach(Consumer consumer) {
                Node.OfInt.CC.$default$forEach(this, consumer);
            }

            public /* synthetic */ StreamShape getShape() {
                return Node.OfInt.CC.$default$getShape(this);
            }

            OfInt() {
            }

            public Spliterator.OfInt spliterator() {
                return Spliterators.emptyIntSpliterator();
            }

            public int[] asPrimitiveArray() {
                return Nodes.EMPTY_INT_ARRAY;
            }
        }

        private static final class OfLong extends EmptyNode<Long, long[], LongConsumer> implements Node.OfLong {
            public /* synthetic */ void copyInto(Long[] lArr, int i) {
                Node.OfLong.CC.$default$copyInto((Node.OfLong) this, lArr, i);
            }

            public /* bridge */ /* synthetic */ void copyInto(Object[] objArr, int i) {
                copyInto((Long[]) objArr, i);
            }

            public /* synthetic */ void forEach(Consumer consumer) {
                Node.OfLong.CC.$default$forEach(this, consumer);
            }

            public /* synthetic */ StreamShape getShape() {
                return Node.OfLong.CC.$default$getShape(this);
            }

            OfLong() {
            }

            public Spliterator.OfLong spliterator() {
                return Spliterators.emptyLongSpliterator();
            }

            public long[] asPrimitiveArray() {
                return Nodes.EMPTY_LONG_ARRAY;
            }
        }

        private static final class OfDouble extends EmptyNode<Double, double[], DoubleConsumer> implements Node.OfDouble {
            public /* synthetic */ void copyInto(Double[] dArr, int i) {
                Node.OfDouble.CC.$default$copyInto((Node.OfDouble) this, dArr, i);
            }

            public /* bridge */ /* synthetic */ void copyInto(Object[] objArr, int i) {
                copyInto((Double[]) objArr, i);
            }

            public /* synthetic */ void forEach(Consumer consumer) {
                Node.OfDouble.CC.$default$forEach(this, consumer);
            }

            public /* synthetic */ StreamShape getShape() {
                return Node.OfDouble.CC.$default$getShape(this);
            }

            OfDouble() {
            }

            public Spliterator.OfDouble spliterator() {
                return Spliterators.emptyDoubleSpliterator();
            }

            public double[] asPrimitiveArray() {
                return Nodes.EMPTY_DOUBLE_ARRAY;
            }
        }
    }

    private static class ArrayNode<T> implements Node<T> {
        final T[] array;
        int curSize;

        public /* synthetic */ Node getChild(int i) {
            return Node.CC.$default$getChild(this, i);
        }

        public /* synthetic */ int getChildCount() {
            return Node.CC.$default$getChildCount(this);
        }

        public /* synthetic */ StreamShape getShape() {
            return Node.CC.$default$getShape(this);
        }

        public /* synthetic */ Node truncate(long j, long j2, IntFunction intFunction) {
            return Node.CC.$default$truncate(this, j, j2, intFunction);
        }

        ArrayNode(long size, IntFunction<T[]> intFunction) {
            if (size < NUM) {
                this.array = (Object[]) intFunction.apply((int) size);
                this.curSize = 0;
                return;
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }

        ArrayNode(T[] array2) {
            this.array = array2;
            this.curSize = array2.length;
        }

        public Spliterator<T> spliterator() {
            return DesugarArrays.spliterator(this.array, 0, this.curSize);
        }

        public void copyInto(T[] dest, int destOffset) {
            System.arraycopy(this.array, 0, dest, destOffset, this.curSize);
        }

        public T[] asArray(IntFunction<T[]> intFunction) {
            T[] tArr = this.array;
            if (tArr.length == this.curSize) {
                return tArr;
            }
            throw new IllegalStateException();
        }

        public long count() {
            return (long) this.curSize;
        }

        public void forEach(Consumer<? super T> consumer) {
            for (int i = 0; i < this.curSize; i++) {
                consumer.accept(this.array[i]);
            }
        }

        public String toString() {
            return String.format("ArrayNode[%d][%s]", new Object[]{Integer.valueOf(this.array.length - this.curSize), Arrays.toString(this.array)});
        }
    }

    private static final class CollectionNode<T> implements Node<T> {
        private final Collection<T> c;

        public /* synthetic */ Node getChild(int i) {
            return Node.CC.$default$getChild(this, i);
        }

        public /* synthetic */ int getChildCount() {
            return Node.CC.$default$getChildCount(this);
        }

        public /* synthetic */ StreamShape getShape() {
            return Node.CC.$default$getShape(this);
        }

        public /* synthetic */ Node truncate(long j, long j2, IntFunction intFunction) {
            return Node.CC.$default$truncate(this, j, j2, intFunction);
        }

        CollectionNode(Collection<T> c2) {
            this.c = c2;
        }

        public Spliterator<T> spliterator() {
            return Collection.EL.stream(this.c).spliterator();
        }

        public void copyInto(T[] array, int offset) {
            for (T t : this.c) {
                array[offset] = t;
                offset++;
            }
        }

        public T[] asArray(IntFunction<T[]> intFunction) {
            java.util.Collection<T> collection = this.c;
            return collection.toArray((Object[]) intFunction.apply(collection.size()));
        }

        public long count() {
            return (long) this.c.size();
        }

        public void forEach(Consumer<? super T> consumer) {
            Collection.EL.forEach(this.c, consumer);
        }

        public String toString() {
            return String.format("CollectionNode[%d][%s]", new Object[]{Integer.valueOf(this.c.size()), this.c});
        }
    }

    private static abstract class AbstractConcNode<T, T_NODE extends Node<T>> implements Node<T> {
        protected final T_NODE left;
        protected final T_NODE right;
        private final long size;

        public /* synthetic */ StreamShape getShape() {
            return Node.CC.$default$getShape(this);
        }

        public /* synthetic */ Node truncate(long j, long j2, IntFunction intFunction) {
            return Node.CC.$default$truncate(this, j, j2, intFunction);
        }

        AbstractConcNode(T_NODE left2, T_NODE right2) {
            this.left = left2;
            this.right = right2;
            this.size = left2.count() + right2.count();
        }

        public int getChildCount() {
            return 2;
        }

        public T_NODE getChild(int i) {
            if (i == 0) {
                return this.left;
            }
            if (i == 1) {
                return this.right;
            }
            throw new IndexOutOfBoundsException();
        }

        public long count() {
            return this.size;
        }
    }

    static final class ConcNode<T> extends AbstractConcNode<T, Node<T>> implements Node<T> {
        ConcNode(Node<T> left, Node<T> right) {
            super(left, right);
        }

        public Spliterator<T> spliterator() {
            return new InternalNodeSpliterator.OfRef(this);
        }

        public void copyInto(T[] array, int offset) {
            array.getClass();
            this.left.copyInto(array, offset);
            this.right.copyInto(array, ((int) this.left.count()) + offset);
        }

        public T[] asArray(IntFunction<T[]> intFunction) {
            long size = count();
            if (size < NUM) {
                T[] array = (Object[]) intFunction.apply((int) size);
                copyInto(array, 0);
                return array;
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }

        public void forEach(Consumer<? super T> consumer) {
            this.left.forEach(consumer);
            this.right.forEach(consumer);
        }

        public Node<T> truncate(long from, long to, IntFunction<T[]> intFunction) {
            if (from == 0 && to == count()) {
                return this;
            }
            long leftCount = this.left.count();
            if (from >= leftCount) {
                return this.right.truncate(from - leftCount, to - leftCount, intFunction);
            }
            if (to <= leftCount) {
                return this.left.truncate(from, to, intFunction);
            }
            return Nodes.conc(getShape(), this.left.truncate(from, leftCount, intFunction), this.right.truncate(0, to - leftCount, intFunction));
        }

        public String toString() {
            if (count() < 32) {
                return String.format("ConcNode[%s.%s]", new Object[]{this.left, this.right});
            }
            return String.format("ConcNode[size=%d]", new Object[]{Long.valueOf(count())});
        }

        private static abstract class OfPrimitive<E, T_CONS, T_ARR, T_SPLITR extends Spliterator.OfPrimitive<E, T_CONS, T_SPLITR>, T_NODE extends Node.OfPrimitive<E, T_CONS, T_ARR, T_SPLITR, T_NODE>> extends AbstractConcNode<E, T_NODE> implements Node.OfPrimitive<E, T_CONS, T_ARR, T_SPLITR, T_NODE> {
            public /* synthetic */ Object[] asArray(IntFunction intFunction) {
                return Node.OfPrimitive.CC.$default$asArray(this, intFunction);
            }

            public /* bridge */ /* synthetic */ Spliterator spliterator() {
                return spliterator();
            }

            public /* bridge */ /* synthetic */ Node truncate(long j, long j2, IntFunction intFunction) {
                return truncate(j, j2, intFunction);
            }

            public /* bridge */ /* synthetic */ Node.OfPrimitive getChild(int i) {
                return (Node.OfPrimitive) super.getChild(i);
            }

            OfPrimitive(T_NODE left, T_NODE right) {
                super(left, right);
            }

            public void forEach(T_CONS consumer) {
                ((Node.OfPrimitive) this.left).forEach(consumer);
                ((Node.OfPrimitive) this.right).forEach(consumer);
            }

            public void copyInto(T_ARR array, int offset) {
                ((Node.OfPrimitive) this.left).copyInto(array, offset);
                ((Node.OfPrimitive) this.right).copyInto(array, ((int) ((Node.OfPrimitive) this.left).count()) + offset);
            }

            public T_ARR asPrimitiveArray() {
                long size = count();
                if (size < NUM) {
                    T_ARR array = newArray((int) size);
                    copyInto(array, 0);
                    return array;
                }
                throw new IllegalArgumentException("Stream size exceeds max array size");
            }

            public String toString() {
                if (count() < 32) {
                    return String.format("%s[%s.%s]", new Object[]{getClass().getName(), this.left, this.right});
                }
                return String.format("%s[size=%d]", new Object[]{getClass().getName(), Long.valueOf(count())});
            }
        }

        static final class OfInt extends OfPrimitive<Integer, IntConsumer, int[], Spliterator.OfInt, Node.OfInt> implements Node.OfInt {
            public /* synthetic */ void copyInto(Integer[] numArr, int i) {
                Node.OfInt.CC.$default$copyInto((Node.OfInt) this, numArr, i);
            }

            public /* bridge */ /* synthetic */ void copyInto(Object[] objArr, int i) {
                copyInto((Integer[]) objArr, i);
            }

            public /* synthetic */ void forEach(Consumer consumer) {
                Node.OfInt.CC.$default$forEach(this, consumer);
            }

            public /* synthetic */ StreamShape getShape() {
                return Node.OfInt.CC.$default$getShape(this);
            }

            OfInt(Node.OfInt left, Node.OfInt right) {
                super(left, right);
            }

            public Spliterator.OfInt spliterator() {
                return new InternalNodeSpliterator.OfInt(this);
            }
        }

        static final class OfLong extends OfPrimitive<Long, LongConsumer, long[], Spliterator.OfLong, Node.OfLong> implements Node.OfLong {
            public /* synthetic */ void copyInto(Long[] lArr, int i) {
                Node.OfLong.CC.$default$copyInto((Node.OfLong) this, lArr, i);
            }

            public /* bridge */ /* synthetic */ void copyInto(Object[] objArr, int i) {
                copyInto((Long[]) objArr, i);
            }

            public /* synthetic */ void forEach(Consumer consumer) {
                Node.OfLong.CC.$default$forEach(this, consumer);
            }

            public /* synthetic */ StreamShape getShape() {
                return Node.OfLong.CC.$default$getShape(this);
            }

            OfLong(Node.OfLong left, Node.OfLong right) {
                super(left, right);
            }

            public Spliterator.OfLong spliterator() {
                return new InternalNodeSpliterator.OfLong(this);
            }
        }

        static final class OfDouble extends OfPrimitive<Double, DoubleConsumer, double[], Spliterator.OfDouble, Node.OfDouble> implements Node.OfDouble {
            public /* synthetic */ void copyInto(Double[] dArr, int i) {
                Node.OfDouble.CC.$default$copyInto((Node.OfDouble) this, dArr, i);
            }

            public /* bridge */ /* synthetic */ void copyInto(Object[] objArr, int i) {
                copyInto((Double[]) objArr, i);
            }

            public /* synthetic */ void forEach(Consumer consumer) {
                Node.OfDouble.CC.$default$forEach(this, consumer);
            }

            public /* synthetic */ StreamShape getShape() {
                return Node.OfDouble.CC.$default$getShape(this);
            }

            OfDouble(Node.OfDouble left, Node.OfDouble right) {
                super(left, right);
            }

            public Spliterator.OfDouble spliterator() {
                return new InternalNodeSpliterator.OfDouble(this);
            }
        }
    }

    private static abstract class InternalNodeSpliterator<T, S extends Spliterator<T>, N extends Node<T>> implements Spliterator<T> {
        int curChildIndex;
        N curNode;
        S lastNodeSpliterator;
        S tryAdvanceSpliterator;
        Deque<N> tryAdvanceStack;

        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.CC.$default$forEachRemaining(this, consumer);
        }

        public /* synthetic */ Comparator getComparator() {
            return Spliterator.CC.$default$getComparator(this);
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return Spliterator.CC.$default$getExactSizeIfKnown(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return Spliterator.CC.$default$hasCharacteristics(this, i);
        }

        InternalNodeSpliterator(N curNode2) {
            this.curNode = curNode2;
        }

        /* access modifiers changed from: protected */
        public final Deque<N> initStack() {
            Deque<N> stack = new ArrayDeque<>(8);
            int i = this.curNode.getChildCount();
            while (true) {
                i--;
                if (i < this.curChildIndex) {
                    return stack;
                }
                stack.addFirst(this.curNode.getChild(i));
            }
        }

        /* access modifiers changed from: protected */
        public final N findNextLeafNode(Deque<N> stack) {
            while (true) {
                N n = (Node) stack.pollFirst();
                N n2 = n;
                if (n == null) {
                    return null;
                }
                if (n2.getChildCount() != 0) {
                    for (int i = n2.getChildCount() - 1; i >= 0; i--) {
                        stack.addFirst(n2.getChild(i));
                    }
                } else if (n2.count() > 0) {
                    return n2;
                }
            }
        }

        /* access modifiers changed from: protected */
        public final boolean initTryAdvance() {
            if (this.curNode == null) {
                return false;
            }
            if (this.tryAdvanceSpliterator != null) {
                return true;
            }
            S s = this.lastNodeSpliterator;
            if (s == null) {
                Deque<N> initStack = initStack();
                this.tryAdvanceStack = initStack;
                N leaf = findNextLeafNode(initStack);
                if (leaf != null) {
                    this.tryAdvanceSpliterator = leaf.spliterator();
                    return true;
                }
                this.curNode = null;
                return false;
            }
            this.tryAdvanceSpliterator = s;
            return true;
        }

        public final S trySplit() {
            N n = this.curNode;
            if (n == null || this.tryAdvanceSpliterator != null) {
                return null;
            }
            S s = this.lastNodeSpliterator;
            if (s != null) {
                return s.trySplit();
            }
            if (this.curChildIndex < n.getChildCount() - 1) {
                N n2 = this.curNode;
                int i = this.curChildIndex;
                this.curChildIndex = i + 1;
                return n2.getChild(i).spliterator();
            }
            N child = this.curNode.getChild(this.curChildIndex);
            this.curNode = child;
            if (child.getChildCount() == 0) {
                S spliterator = this.curNode.spliterator();
                this.lastNodeSpliterator = spliterator;
                return spliterator.trySplit();
            }
            this.curChildIndex = 0;
            N n3 = this.curNode;
            this.curChildIndex = 1;
            return n3.getChild(0).spliterator();
        }

        public final long estimateSize() {
            if (this.curNode == null) {
                return 0;
            }
            S s = this.lastNodeSpliterator;
            if (s != null) {
                return s.estimateSize();
            }
            long size = 0;
            for (int i = this.curChildIndex; i < this.curNode.getChildCount(); i++) {
                size += this.curNode.getChild(i).count();
            }
            return size;
        }

        public final int characteristics() {
            return 64;
        }

        private static final class OfRef<T> extends InternalNodeSpliterator<T, Spliterator<T>, Node<T>> {
            OfRef(Node<T> curNode) {
                super(curNode);
            }

            public boolean tryAdvance(Consumer<? super T> consumer) {
                java.util.stream.Node<T> leaf;
                if (!initTryAdvance()) {
                    return false;
                }
                boolean hasNext = this.tryAdvanceSpliterator.tryAdvance(consumer);
                if (!hasNext) {
                    if (this.lastNodeSpliterator != null || (leaf = findNextLeafNode(this.tryAdvanceStack)) == null) {
                        this.curNode = null;
                    } else {
                        this.tryAdvanceSpliterator = leaf.spliterator();
                        return this.tryAdvanceSpliterator.tryAdvance(consumer);
                    }
                }
                return hasNext;
            }

            public void forEachRemaining(Consumer<? super T> consumer) {
                if (this.curNode != null) {
                    if (this.tryAdvanceSpliterator != null) {
                        do {
                        } while (tryAdvance(consumer));
                    } else if (this.lastNodeSpliterator == null) {
                        Deque<java.util.stream.Node<T>> stack = initStack();
                        while (true) {
                            Node findNextLeafNode = findNextLeafNode(stack);
                            Node node = findNextLeafNode;
                            if (findNextLeafNode != null) {
                                node.forEach(consumer);
                            } else {
                                this.curNode = null;
                                return;
                            }
                        }
                    } else {
                        this.lastNodeSpliterator.forEachRemaining(consumer);
                    }
                }
            }
        }

        private static abstract class OfPrimitive<T, T_CONS, T_ARR, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>, N extends Node.OfPrimitive<T, T_CONS, T_ARR, T_SPLITR, N>> extends InternalNodeSpliterator<T, T_SPLITR, N> implements Spliterator.OfPrimitive<T, T_CONS, T_SPLITR> {
            public /* bridge */ /* synthetic */ Spliterator.OfPrimitive trySplit() {
                return (Spliterator.OfPrimitive) super.trySplit();
            }

            OfPrimitive(N cur) {
                super(cur);
            }

            public boolean tryAdvance(T_CONS consumer) {
                N leaf;
                if (!initTryAdvance()) {
                    return false;
                }
                boolean hasNext = ((Spliterator.OfPrimitive) this.tryAdvanceSpliterator).tryAdvance(consumer);
                if (!hasNext) {
                    if (this.lastNodeSpliterator != null || (leaf = (Node.OfPrimitive) findNextLeafNode(this.tryAdvanceStack)) == null) {
                        this.curNode = null;
                    } else {
                        this.tryAdvanceSpliterator = leaf.spliterator();
                        return ((Spliterator.OfPrimitive) this.tryAdvanceSpliterator).tryAdvance(consumer);
                    }
                }
                return hasNext;
            }

            public void forEachRemaining(T_CONS consumer) {
                if (this.curNode != null) {
                    if (this.tryAdvanceSpliterator != null) {
                        do {
                        } while (tryAdvance(consumer));
                    } else if (this.lastNodeSpliterator == null) {
                        Deque<N> stack = initStack();
                        while (true) {
                            N n = (Node.OfPrimitive) findNextLeafNode(stack);
                            N leaf = n;
                            if (n != null) {
                                leaf.forEach(consumer);
                            } else {
                                this.curNode = null;
                                return;
                            }
                        }
                    } else {
                        ((Spliterator.OfPrimitive) this.lastNodeSpliterator).forEachRemaining(consumer);
                    }
                }
            }
        }

        private static final class OfInt extends OfPrimitive<Integer, IntConsumer, int[], Spliterator.OfInt, Node.OfInt> implements Spliterator.OfInt {
            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                Spliterator.OfInt.CC.$default$forEachRemaining((Spliterator.OfInt) this, consumer);
            }

            public /* synthetic */ boolean tryAdvance(Consumer consumer) {
                return Spliterator.OfInt.CC.$default$tryAdvance((Spliterator.OfInt) this, consumer);
            }

            public /* bridge */ /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
                super.forEachRemaining(intConsumer);
            }

            public /* bridge */ /* synthetic */ boolean tryAdvance(IntConsumer intConsumer) {
                return super.tryAdvance(intConsumer);
            }

            public /* bridge */ /* synthetic */ Spliterator.OfInt trySplit() {
                return (Spliterator.OfInt) super.trySplit();
            }

            OfInt(Node.OfInt cur) {
                super(cur);
            }
        }

        private static final class OfLong extends OfPrimitive<Long, LongConsumer, long[], Spliterator.OfLong, Node.OfLong> implements Spliterator.OfLong {
            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                Spliterator.OfLong.CC.$default$forEachRemaining((Spliterator.OfLong) this, consumer);
            }

            public /* synthetic */ boolean tryAdvance(Consumer consumer) {
                return Spliterator.OfLong.CC.$default$tryAdvance((Spliterator.OfLong) this, consumer);
            }

            public /* bridge */ /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
                super.forEachRemaining(longConsumer);
            }

            public /* bridge */ /* synthetic */ boolean tryAdvance(LongConsumer longConsumer) {
                return super.tryAdvance(longConsumer);
            }

            public /* bridge */ /* synthetic */ Spliterator.OfLong trySplit() {
                return (Spliterator.OfLong) super.trySplit();
            }

            OfLong(Node.OfLong cur) {
                super(cur);
            }
        }

        private static final class OfDouble extends OfPrimitive<Double, DoubleConsumer, double[], Spliterator.OfDouble, Node.OfDouble> implements Spliterator.OfDouble {
            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                Spliterator.OfDouble.CC.$default$forEachRemaining((Spliterator.OfDouble) this, consumer);
            }

            public /* synthetic */ boolean tryAdvance(Consumer consumer) {
                return Spliterator.OfDouble.CC.$default$tryAdvance((Spliterator.OfDouble) this, consumer);
            }

            public /* bridge */ /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
                super.forEachRemaining(doubleConsumer);
            }

            public /* bridge */ /* synthetic */ boolean tryAdvance(DoubleConsumer doubleConsumer) {
                return super.tryAdvance(doubleConsumer);
            }

            public /* bridge */ /* synthetic */ Spliterator.OfDouble trySplit() {
                return (Spliterator.OfDouble) super.trySplit();
            }

            OfDouble(Node.OfDouble cur) {
                super(cur);
            }
        }
    }

    private static final class FixedNodeBuilder<T> extends ArrayNode<T> implements Node.Builder<T> {
        static final /* synthetic */ boolean $assertionsDisabled = true;

        public /* synthetic */ void accept(double d) {
            Sink.CC.$default$accept((Sink) this, d);
        }

        public /* synthetic */ void accept(int i) {
            Sink.CC.$default$accept((Sink) this, i);
        }

        public /* synthetic */ void accept(long j) {
            Sink.CC.$default$accept((Sink) this, j);
        }

        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return Consumer.CC.$default$andThen(this, consumer);
        }

        public /* synthetic */ boolean cancellationRequested() {
            return Sink.CC.$default$cancellationRequested(this);
        }

        FixedNodeBuilder(long size, IntFunction<T[]> intFunction) {
            super(size, intFunction);
            if (!$assertionsDisabled && size >= NUM) {
                throw new AssertionError();
            }
        }

        public Node<T> build() {
            if (this.curSize >= this.array.length) {
                return this;
            }
            throw new IllegalStateException(String.format("Current size %d is less than fixed size %d", new Object[]{Integer.valueOf(this.curSize), Integer.valueOf(this.array.length)}));
        }

        public void begin(long size) {
            if (size == ((long) this.array.length)) {
                this.curSize = 0;
            } else {
                throw new IllegalStateException(String.format("Begin size %d is not equal to fixed size %d", new Object[]{Long.valueOf(size), Integer.valueOf(this.array.length)}));
            }
        }

        public void accept(T t) {
            if (this.curSize < this.array.length) {
                Object[] objArr = this.array;
                int i = this.curSize;
                this.curSize = i + 1;
                objArr[i] = t;
                return;
            }
            throw new IllegalStateException(String.format("Accept exceeded fixed size of %d", new Object[]{Integer.valueOf(this.array.length)}));
        }

        public void end() {
            if (this.curSize < this.array.length) {
                throw new IllegalStateException(String.format("End size %d is less than fixed size %d", new Object[]{Integer.valueOf(this.curSize), Integer.valueOf(this.array.length)}));
            }
        }

        public String toString() {
            return String.format("FixedNodeBuilder[%d][%s]", new Object[]{Integer.valueOf(this.array.length - this.curSize), Arrays.toString(this.array)});
        }
    }

    private static final class SpinedNodeBuilder<T> extends SpinedBuffer<T> implements Node<T>, Node.Builder<T> {
        static final /* synthetic */ boolean $assertionsDisabled = true;
        private boolean building = false;

        public /* synthetic */ void accept(double d) {
            Sink.CC.$default$accept((Sink) this, d);
        }

        public /* synthetic */ void accept(int i) {
            Sink.CC.$default$accept((Sink) this, i);
        }

        public /* synthetic */ void accept(long j) {
            Sink.CC.$default$accept((Sink) this, j);
        }

        public /* synthetic */ boolean cancellationRequested() {
            return Sink.CC.$default$cancellationRequested(this);
        }

        public /* synthetic */ Node getChild(int i) {
            return Node.CC.$default$getChild(this, i);
        }

        public /* synthetic */ int getChildCount() {
            return Node.CC.$default$getChildCount(this);
        }

        public /* synthetic */ StreamShape getShape() {
            return Node.CC.$default$getShape(this);
        }

        public /* synthetic */ Node truncate(long j, long j2, IntFunction intFunction) {
            return Node.CC.$default$truncate(this, j, j2, intFunction);
        }

        SpinedNodeBuilder() {
        }

        public Spliterator<T> spliterator() {
            if ($assertionsDisabled || !this.building) {
                return super.spliterator();
            }
            throw new AssertionError("during building");
        }

        public void forEach(Consumer<? super T> consumer) {
            if ($assertionsDisabled || !this.building) {
                super.forEach(consumer);
                return;
            }
            throw new AssertionError("during building");
        }

        public void begin(long size) {
            if ($assertionsDisabled || !this.building) {
                this.building = true;
                clear();
                ensureCapacity(size);
                return;
            }
            throw new AssertionError("was already building");
        }

        public void accept(T t) {
            if ($assertionsDisabled || this.building) {
                super.accept(t);
                return;
            }
            throw new AssertionError("not building");
        }

        public void end() {
            if ($assertionsDisabled || this.building) {
                this.building = false;
                return;
            }
            throw new AssertionError("was not building");
        }

        public void copyInto(T[] array, int offset) {
            if ($assertionsDisabled || !this.building) {
                super.copyInto(array, offset);
                return;
            }
            throw new AssertionError("during building");
        }

        public T[] asArray(IntFunction<T[]> intFunction) {
            if ($assertionsDisabled || !this.building) {
                return super.asArray(intFunction);
            }
            throw new AssertionError("during building");
        }

        public Node<T> build() {
            if ($assertionsDisabled || !this.building) {
                return this;
            }
            throw new AssertionError("during building");
        }
    }

    private static class IntArrayNode implements Node.OfInt {
        final int[] array;
        int curSize;

        public /* synthetic */ Object[] asArray(IntFunction intFunction) {
            return Node.OfPrimitive.CC.$default$asArray(this, intFunction);
        }

        public /* synthetic */ void copyInto(Integer[] numArr, int i) {
            Node.OfInt.CC.$default$copyInto((Node.OfInt) this, numArr, i);
        }

        public /* bridge */ /* synthetic */ void copyInto(Object[] objArr, int i) {
            copyInto((Integer[]) objArr, i);
        }

        public /* synthetic */ void forEach(Consumer consumer) {
            Node.OfInt.CC.$default$forEach(this, consumer);
        }

        public /* synthetic */ int getChildCount() {
            return Node.CC.$default$getChildCount(this);
        }

        public /* synthetic */ StreamShape getShape() {
            return Node.OfInt.CC.$default$getShape(this);
        }

        IntArrayNode(long size) {
            if (size < NUM) {
                this.array = new int[((int) size)];
                this.curSize = 0;
                return;
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }

        IntArrayNode(int[] array2) {
            this.array = array2;
            this.curSize = array2.length;
        }

        public Spliterator.OfInt spliterator() {
            return DesugarArrays.spliterator(this.array, 0, this.curSize);
        }

        public int[] asPrimitiveArray() {
            int[] iArr = this.array;
            int length = iArr.length;
            int i = this.curSize;
            if (length == i) {
                return iArr;
            }
            return Arrays.copyOf(iArr, i);
        }

        public void copyInto(int[] dest, int destOffset) {
            System.arraycopy(this.array, 0, dest, destOffset, this.curSize);
        }

        public long count() {
            return (long) this.curSize;
        }

        public void forEach(IntConsumer consumer) {
            for (int i = 0; i < this.curSize; i++) {
                consumer.accept(this.array[i]);
            }
        }

        public String toString() {
            return String.format("IntArrayNode[%d][%s]", new Object[]{Integer.valueOf(this.array.length - this.curSize), Arrays.toString(this.array)});
        }
    }

    private static class LongArrayNode implements Node.OfLong {
        final long[] array;
        int curSize;

        public /* synthetic */ Object[] asArray(IntFunction intFunction) {
            return Node.OfPrimitive.CC.$default$asArray(this, intFunction);
        }

        public /* synthetic */ void copyInto(Long[] lArr, int i) {
            Node.OfLong.CC.$default$copyInto((Node.OfLong) this, lArr, i);
        }

        public /* bridge */ /* synthetic */ void copyInto(Object[] objArr, int i) {
            copyInto((Long[]) objArr, i);
        }

        public /* synthetic */ void forEach(Consumer consumer) {
            Node.OfLong.CC.$default$forEach(this, consumer);
        }

        public /* synthetic */ int getChildCount() {
            return Node.CC.$default$getChildCount(this);
        }

        public /* synthetic */ StreamShape getShape() {
            return Node.OfLong.CC.$default$getShape(this);
        }

        LongArrayNode(long size) {
            if (size < NUM) {
                this.array = new long[((int) size)];
                this.curSize = 0;
                return;
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }

        LongArrayNode(long[] array2) {
            this.array = array2;
            this.curSize = array2.length;
        }

        public Spliterator.OfLong spliterator() {
            return DesugarArrays.spliterator(this.array, 0, this.curSize);
        }

        public long[] asPrimitiveArray() {
            long[] jArr = this.array;
            int length = jArr.length;
            int i = this.curSize;
            if (length == i) {
                return jArr;
            }
            return Arrays.copyOf(jArr, i);
        }

        public void copyInto(long[] dest, int destOffset) {
            System.arraycopy(this.array, 0, dest, destOffset, this.curSize);
        }

        public long count() {
            return (long) this.curSize;
        }

        public void forEach(LongConsumer consumer) {
            for (int i = 0; i < this.curSize; i++) {
                consumer.accept(this.array[i]);
            }
        }

        public String toString() {
            return String.format("LongArrayNode[%d][%s]", new Object[]{Integer.valueOf(this.array.length - this.curSize), Arrays.toString(this.array)});
        }
    }

    private static class DoubleArrayNode implements Node.OfDouble {
        final double[] array;
        int curSize;

        public /* synthetic */ Object[] asArray(IntFunction intFunction) {
            return Node.OfPrimitive.CC.$default$asArray(this, intFunction);
        }

        public /* synthetic */ void copyInto(Double[] dArr, int i) {
            Node.OfDouble.CC.$default$copyInto((Node.OfDouble) this, dArr, i);
        }

        public /* bridge */ /* synthetic */ void copyInto(Object[] objArr, int i) {
            copyInto((Double[]) objArr, i);
        }

        public /* synthetic */ void forEach(Consumer consumer) {
            Node.OfDouble.CC.$default$forEach(this, consumer);
        }

        public /* synthetic */ int getChildCount() {
            return Node.CC.$default$getChildCount(this);
        }

        public /* synthetic */ StreamShape getShape() {
            return Node.OfDouble.CC.$default$getShape(this);
        }

        DoubleArrayNode(long size) {
            if (size < NUM) {
                this.array = new double[((int) size)];
                this.curSize = 0;
                return;
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }

        DoubleArrayNode(double[] array2) {
            this.array = array2;
            this.curSize = array2.length;
        }

        public Spliterator.OfDouble spliterator() {
            return DesugarArrays.spliterator(this.array, 0, this.curSize);
        }

        public double[] asPrimitiveArray() {
            double[] dArr = this.array;
            int length = dArr.length;
            int i = this.curSize;
            if (length == i) {
                return dArr;
            }
            return Arrays.copyOf(dArr, i);
        }

        public void copyInto(double[] dest, int destOffset) {
            System.arraycopy(this.array, 0, dest, destOffset, this.curSize);
        }

        public long count() {
            return (long) this.curSize;
        }

        public void forEach(DoubleConsumer consumer) {
            for (int i = 0; i < this.curSize; i++) {
                consumer.accept(this.array[i]);
            }
        }

        public String toString() {
            return String.format("DoubleArrayNode[%d][%s]", new Object[]{Integer.valueOf(this.array.length - this.curSize), Arrays.toString(this.array)});
        }
    }

    private static final class IntFixedNodeBuilder extends IntArrayNode implements Node.Builder.OfInt {
        static final /* synthetic */ boolean $assertionsDisabled = true;

        public /* synthetic */ void accept(double d) {
            Sink.CC.$default$accept((Sink) this, d);
        }

        public /* synthetic */ void accept(long j) {
            Sink.CC.$default$accept((Sink) this, j);
        }

        public /* synthetic */ void accept(Integer num) {
            Sink.OfInt.CC.$default$accept((Sink.OfInt) this, num);
        }

        public /* bridge */ /* synthetic */ void accept(Object obj) {
            accept((Integer) obj);
        }

        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return Consumer.CC.$default$andThen(this, consumer);
        }

        public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
            return IntConsumer.CC.$default$andThen(this, intConsumer);
        }

        public /* synthetic */ boolean cancellationRequested() {
            return Sink.CC.$default$cancellationRequested(this);
        }

        IntFixedNodeBuilder(long size) {
            super(size);
            if (!$assertionsDisabled && size >= NUM) {
                throw new AssertionError();
            }
        }

        public Node.OfInt build() {
            if (this.curSize >= this.array.length) {
                return this;
            }
            throw new IllegalStateException(String.format("Current size %d is less than fixed size %d", new Object[]{Integer.valueOf(this.curSize), Integer.valueOf(this.array.length)}));
        }

        public void begin(long size) {
            if (size == ((long) this.array.length)) {
                this.curSize = 0;
            } else {
                throw new IllegalStateException(String.format("Begin size %d is not equal to fixed size %d", new Object[]{Long.valueOf(size), Integer.valueOf(this.array.length)}));
            }
        }

        public void accept(int i) {
            if (this.curSize < this.array.length) {
                int[] iArr = this.array;
                int i2 = this.curSize;
                this.curSize = i2 + 1;
                iArr[i2] = i;
                return;
            }
            throw new IllegalStateException(String.format("Accept exceeded fixed size of %d", new Object[]{Integer.valueOf(this.array.length)}));
        }

        public void end() {
            if (this.curSize < this.array.length) {
                throw new IllegalStateException(String.format("End size %d is less than fixed size %d", new Object[]{Integer.valueOf(this.curSize), Integer.valueOf(this.array.length)}));
            }
        }

        public String toString() {
            return String.format("IntFixedNodeBuilder[%d][%s]", new Object[]{Integer.valueOf(this.array.length - this.curSize), Arrays.toString(this.array)});
        }
    }

    private static final class LongFixedNodeBuilder extends LongArrayNode implements Node.Builder.OfLong {
        static final /* synthetic */ boolean $assertionsDisabled = true;

        public /* synthetic */ void accept(double d) {
            Sink.CC.$default$accept((Sink) this, d);
        }

        public /* synthetic */ void accept(int i) {
            Sink.CC.$default$accept((Sink) this, i);
        }

        public /* synthetic */ void accept(Long l) {
            Sink.OfLong.CC.$default$accept((Sink.OfLong) this, l);
        }

        public /* bridge */ /* synthetic */ void accept(Object obj) {
            accept((Long) obj);
        }

        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return Consumer.CC.$default$andThen(this, consumer);
        }

        public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
            return LongConsumer.CC.$default$andThen(this, longConsumer);
        }

        public /* synthetic */ boolean cancellationRequested() {
            return Sink.CC.$default$cancellationRequested(this);
        }

        LongFixedNodeBuilder(long size) {
            super(size);
            if (!$assertionsDisabled && size >= NUM) {
                throw new AssertionError();
            }
        }

        public Node.OfLong build() {
            if (this.curSize >= this.array.length) {
                return this;
            }
            throw new IllegalStateException(String.format("Current size %d is less than fixed size %d", new Object[]{Integer.valueOf(this.curSize), Integer.valueOf(this.array.length)}));
        }

        public void begin(long size) {
            if (size == ((long) this.array.length)) {
                this.curSize = 0;
            } else {
                throw new IllegalStateException(String.format("Begin size %d is not equal to fixed size %d", new Object[]{Long.valueOf(size), Integer.valueOf(this.array.length)}));
            }
        }

        public void accept(long i) {
            if (this.curSize < this.array.length) {
                long[] jArr = this.array;
                int i2 = this.curSize;
                this.curSize = i2 + 1;
                jArr[i2] = i;
                return;
            }
            throw new IllegalStateException(String.format("Accept exceeded fixed size of %d", new Object[]{Integer.valueOf(this.array.length)}));
        }

        public void end() {
            if (this.curSize < this.array.length) {
                throw new IllegalStateException(String.format("End size %d is less than fixed size %d", new Object[]{Integer.valueOf(this.curSize), Integer.valueOf(this.array.length)}));
            }
        }

        public String toString() {
            return String.format("LongFixedNodeBuilder[%d][%s]", new Object[]{Integer.valueOf(this.array.length - this.curSize), Arrays.toString(this.array)});
        }
    }

    private static final class DoubleFixedNodeBuilder extends DoubleArrayNode implements Node.Builder.OfDouble {
        static final /* synthetic */ boolean $assertionsDisabled = true;

        public /* synthetic */ void accept(int i) {
            Sink.CC.$default$accept((Sink) this, i);
        }

        public /* synthetic */ void accept(long j) {
            Sink.CC.$default$accept((Sink) this, j);
        }

        public /* synthetic */ void accept(Double d) {
            Sink.OfDouble.CC.$default$accept((Sink.OfDouble) this, d);
        }

        public /* bridge */ /* synthetic */ void accept(Object obj) {
            accept((Double) obj);
        }

        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return Consumer.CC.$default$andThen(this, consumer);
        }

        public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
            return DoubleConsumer.CC.$default$andThen(this, doubleConsumer);
        }

        public /* synthetic */ boolean cancellationRequested() {
            return Sink.CC.$default$cancellationRequested(this);
        }

        DoubleFixedNodeBuilder(long size) {
            super(size);
            if (!$assertionsDisabled && size >= NUM) {
                throw new AssertionError();
            }
        }

        public Node.OfDouble build() {
            if (this.curSize >= this.array.length) {
                return this;
            }
            throw new IllegalStateException(String.format("Current size %d is less than fixed size %d", new Object[]{Integer.valueOf(this.curSize), Integer.valueOf(this.array.length)}));
        }

        public void begin(long size) {
            if (size == ((long) this.array.length)) {
                this.curSize = 0;
            } else {
                throw new IllegalStateException(String.format("Begin size %d is not equal to fixed size %d", new Object[]{Long.valueOf(size), Integer.valueOf(this.array.length)}));
            }
        }

        public void accept(double i) {
            if (this.curSize < this.array.length) {
                double[] dArr = this.array;
                int i2 = this.curSize;
                this.curSize = i2 + 1;
                dArr[i2] = i;
                return;
            }
            throw new IllegalStateException(String.format("Accept exceeded fixed size of %d", new Object[]{Integer.valueOf(this.array.length)}));
        }

        public void end() {
            if (this.curSize < this.array.length) {
                throw new IllegalStateException(String.format("End size %d is less than fixed size %d", new Object[]{Integer.valueOf(this.curSize), Integer.valueOf(this.array.length)}));
            }
        }

        public String toString() {
            return String.format("DoubleFixedNodeBuilder[%d][%s]", new Object[]{Integer.valueOf(this.array.length - this.curSize), Arrays.toString(this.array)});
        }
    }

    private static final class IntSpinedNodeBuilder extends SpinedBuffer.OfInt implements Node.OfInt, Node.Builder.OfInt {
        static final /* synthetic */ boolean $assertionsDisabled = true;
        private boolean building = false;

        public /* synthetic */ void accept(double d) {
            Sink.CC.$default$accept((Sink) this, d);
        }

        public /* synthetic */ void accept(long j) {
            Sink.CC.$default$accept((Sink) this, j);
        }

        public /* synthetic */ void accept(Integer num) {
            Sink.OfInt.CC.$default$accept((Sink.OfInt) this, num);
        }

        public /* bridge */ /* synthetic */ void accept(Object obj) {
            accept((Integer) obj);
        }

        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return Consumer.CC.$default$andThen(this, consumer);
        }

        public /* synthetic */ Object[] asArray(IntFunction intFunction) {
            return Node.OfPrimitive.CC.$default$asArray(this, intFunction);
        }

        public /* synthetic */ boolean cancellationRequested() {
            return Sink.CC.$default$cancellationRequested(this);
        }

        public /* synthetic */ void copyInto(Integer[] numArr, int i) {
            Node.OfInt.CC.$default$copyInto((Node.OfInt) this, numArr, i);
        }

        public /* bridge */ /* synthetic */ void copyInto(Object[] objArr, int i) {
            copyInto((Integer[]) objArr, i);
        }

        public /* synthetic */ int getChildCount() {
            return Node.CC.$default$getChildCount(this);
        }

        public /* synthetic */ StreamShape getShape() {
            return Node.OfInt.CC.$default$getShape(this);
        }

        IntSpinedNodeBuilder() {
        }

        public Spliterator.OfInt spliterator() {
            if ($assertionsDisabled || !this.building) {
                return super.spliterator();
            }
            throw new AssertionError("during building");
        }

        public void forEach(IntConsumer consumer) {
            if ($assertionsDisabled || !this.building) {
                super.forEach(consumer);
                return;
            }
            throw new AssertionError("during building");
        }

        public void begin(long size) {
            if ($assertionsDisabled || !this.building) {
                this.building = true;
                clear();
                ensureCapacity(size);
                return;
            }
            throw new AssertionError("was already building");
        }

        public void accept(int i) {
            if ($assertionsDisabled || this.building) {
                super.accept(i);
                return;
            }
            throw new AssertionError("not building");
        }

        public void end() {
            if ($assertionsDisabled || this.building) {
                this.building = false;
                return;
            }
            throw new AssertionError("was not building");
        }

        public void copyInto(int[] array, int offset) {
            if ($assertionsDisabled || !this.building) {
                super.copyInto(array, offset);
                return;
            }
            throw new AssertionError("during building");
        }

        public int[] asPrimitiveArray() {
            if ($assertionsDisabled || !this.building) {
                return (int[]) super.asPrimitiveArray();
            }
            throw new AssertionError("during building");
        }

        public Node.OfInt build() {
            if ($assertionsDisabled || !this.building) {
                return this;
            }
            throw new AssertionError("during building");
        }
    }

    private static final class LongSpinedNodeBuilder extends SpinedBuffer.OfLong implements Node.OfLong, Node.Builder.OfLong {
        static final /* synthetic */ boolean $assertionsDisabled = true;
        private boolean building = false;

        public /* synthetic */ void accept(double d) {
            Sink.CC.$default$accept((Sink) this, d);
        }

        public /* synthetic */ void accept(int i) {
            Sink.CC.$default$accept((Sink) this, i);
        }

        public /* synthetic */ void accept(Long l) {
            Sink.OfLong.CC.$default$accept((Sink.OfLong) this, l);
        }

        public /* bridge */ /* synthetic */ void accept(Object obj) {
            accept((Long) obj);
        }

        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return Consumer.CC.$default$andThen(this, consumer);
        }

        public /* synthetic */ Object[] asArray(IntFunction intFunction) {
            return Node.OfPrimitive.CC.$default$asArray(this, intFunction);
        }

        public /* synthetic */ boolean cancellationRequested() {
            return Sink.CC.$default$cancellationRequested(this);
        }

        public /* synthetic */ void copyInto(Long[] lArr, int i) {
            Node.OfLong.CC.$default$copyInto((Node.OfLong) this, lArr, i);
        }

        public /* bridge */ /* synthetic */ void copyInto(Object[] objArr, int i) {
            copyInto((Long[]) objArr, i);
        }

        public /* synthetic */ int getChildCount() {
            return Node.CC.$default$getChildCount(this);
        }

        public /* synthetic */ StreamShape getShape() {
            return Node.OfLong.CC.$default$getShape(this);
        }

        LongSpinedNodeBuilder() {
        }

        public Spliterator.OfLong spliterator() {
            if ($assertionsDisabled || !this.building) {
                return super.spliterator();
            }
            throw new AssertionError("during building");
        }

        public void forEach(LongConsumer consumer) {
            if ($assertionsDisabled || !this.building) {
                super.forEach(consumer);
                return;
            }
            throw new AssertionError("during building");
        }

        public void begin(long size) {
            if ($assertionsDisabled || !this.building) {
                this.building = true;
                clear();
                ensureCapacity(size);
                return;
            }
            throw new AssertionError("was already building");
        }

        public void accept(long i) {
            if ($assertionsDisabled || this.building) {
                super.accept(i);
                return;
            }
            throw new AssertionError("not building");
        }

        public void end() {
            if ($assertionsDisabled || this.building) {
                this.building = false;
                return;
            }
            throw new AssertionError("was not building");
        }

        public void copyInto(long[] array, int offset) {
            if ($assertionsDisabled || !this.building) {
                super.copyInto(array, offset);
                return;
            }
            throw new AssertionError("during building");
        }

        public long[] asPrimitiveArray() {
            if ($assertionsDisabled || !this.building) {
                return (long[]) super.asPrimitiveArray();
            }
            throw new AssertionError("during building");
        }

        public Node.OfLong build() {
            if ($assertionsDisabled || !this.building) {
                return this;
            }
            throw new AssertionError("during building");
        }
    }

    private static final class DoubleSpinedNodeBuilder extends SpinedBuffer.OfDouble implements Node.OfDouble, Node.Builder.OfDouble {
        static final /* synthetic */ boolean $assertionsDisabled = true;
        private boolean building = false;

        public /* synthetic */ void accept(int i) {
            Sink.CC.$default$accept((Sink) this, i);
        }

        public /* synthetic */ void accept(long j) {
            Sink.CC.$default$accept((Sink) this, j);
        }

        public /* synthetic */ void accept(Double d) {
            Sink.OfDouble.CC.$default$accept((Sink.OfDouble) this, d);
        }

        public /* bridge */ /* synthetic */ void accept(Object obj) {
            accept((Double) obj);
        }

        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return Consumer.CC.$default$andThen(this, consumer);
        }

        public /* synthetic */ Object[] asArray(IntFunction intFunction) {
            return Node.OfPrimitive.CC.$default$asArray(this, intFunction);
        }

        public /* synthetic */ boolean cancellationRequested() {
            return Sink.CC.$default$cancellationRequested(this);
        }

        public /* synthetic */ void copyInto(Double[] dArr, int i) {
            Node.OfDouble.CC.$default$copyInto((Node.OfDouble) this, dArr, i);
        }

        public /* bridge */ /* synthetic */ void copyInto(Object[] objArr, int i) {
            copyInto((Double[]) objArr, i);
        }

        public /* synthetic */ int getChildCount() {
            return Node.CC.$default$getChildCount(this);
        }

        public /* synthetic */ StreamShape getShape() {
            return Node.OfDouble.CC.$default$getShape(this);
        }

        DoubleSpinedNodeBuilder() {
        }

        public Spliterator.OfDouble spliterator() {
            if ($assertionsDisabled || !this.building) {
                return super.spliterator();
            }
            throw new AssertionError("during building");
        }

        public void forEach(DoubleConsumer consumer) {
            if ($assertionsDisabled || !this.building) {
                super.forEach(consumer);
                return;
            }
            throw new AssertionError("during building");
        }

        public void begin(long size) {
            if ($assertionsDisabled || !this.building) {
                this.building = true;
                clear();
                ensureCapacity(size);
                return;
            }
            throw new AssertionError("was already building");
        }

        public void accept(double i) {
            if ($assertionsDisabled || this.building) {
                super.accept(i);
                return;
            }
            throw new AssertionError("not building");
        }

        public void end() {
            if ($assertionsDisabled || this.building) {
                this.building = false;
                return;
            }
            throw new AssertionError("was not building");
        }

        public void copyInto(double[] array, int offset) {
            if ($assertionsDisabled || !this.building) {
                super.copyInto(array, offset);
                return;
            }
            throw new AssertionError("during building");
        }

        public double[] asPrimitiveArray() {
            if ($assertionsDisabled || !this.building) {
                return (double[]) super.asPrimitiveArray();
            }
            throw new AssertionError("during building");
        }

        public Node.OfDouble build() {
            if ($assertionsDisabled || !this.building) {
                return this;
            }
            throw new AssertionError("during building");
        }
    }

    private static abstract class SizedCollectorTask<P_IN, P_OUT, T_SINK extends Sink<P_OUT>, K extends SizedCollectorTask<P_IN, P_OUT, T_SINK, K>> extends CountedCompleter<Void> implements Sink<P_OUT> {
        static final /* synthetic */ boolean $assertionsDisabled = true;
        protected int fence;
        protected final PipelineHelper<P_OUT> helper;
        protected int index;
        protected long length;
        protected long offset;
        protected final Spliterator<P_IN> spliterator;
        protected final long targetSize;

        public /* synthetic */ void accept(double d) {
            Sink.CC.$default$accept((Sink) this, d);
        }

        public /* synthetic */ void accept(int i) {
            Sink.CC.$default$accept((Sink) this, i);
        }

        public /* synthetic */ void accept(long j) {
            Sink.CC.$default$accept((Sink) this, j);
        }

        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return Consumer.CC.$default$andThen(this, consumer);
        }

        public /* synthetic */ boolean cancellationRequested() {
            return Sink.CC.$default$cancellationRequested(this);
        }

        public /* synthetic */ void end() {
            Sink.CC.$default$end(this);
        }

        /* access modifiers changed from: package-private */
        public abstract K makeChild(Spliterator<P_IN> spliterator2, long j, long j2);

        SizedCollectorTask(Spliterator<P_IN> spliterator2, PipelineHelper<P_OUT> helper2, int arrayLength) {
            if ($assertionsDisabled || spliterator2.hasCharacteristics(16384)) {
                this.spliterator = spliterator2;
                this.helper = helper2;
                this.targetSize = AbstractTask.suggestTargetSize(spliterator2.estimateSize());
                this.offset = 0;
                this.length = (long) arrayLength;
                return;
            }
            throw new AssertionError();
        }

        SizedCollectorTask(K parent, Spliterator<P_IN> spliterator2, long offset2, long length2, int arrayLength) {
            super(parent);
            if ($assertionsDisabled || spliterator2.hasCharacteristics(16384)) {
                this.spliterator = spliterator2;
                this.helper = parent.helper;
                this.targetSize = parent.targetSize;
                this.offset = offset2;
                this.length = length2;
                if (offset2 < 0 || length2 < 0 || (offset2 + length2) - 1 >= ((long) arrayLength)) {
                    throw new IllegalArgumentException(String.format("offset and length interval [%d, %d + %d) is not within array size interval [0, %d)", new Object[]{Long.valueOf(offset2), Long.valueOf(offset2), Long.valueOf(length2), Integer.valueOf(arrayLength)}));
                }
                return;
            }
            throw new AssertionError();
        }

        public void compute() {
            SizedCollectorTask sizedCollectorTask = this;
            Spliterator<P_IN> spliterator2 = this.spliterator;
            while (spliterator2.estimateSize() > sizedCollectorTask.targetSize) {
                Spliterator<P_IN> trySplit = spliterator2.trySplit();
                Spliterator<P_IN> spliterator3 = trySplit;
                if (trySplit == null) {
                    break;
                }
                sizedCollectorTask.setPendingCount(1);
                long leftSplitSize = spliterator3.estimateSize();
                sizedCollectorTask.makeChild(spliterator3, sizedCollectorTask.offset, leftSplitSize).fork();
                sizedCollectorTask = sizedCollectorTask.makeChild(spliterator2, sizedCollectorTask.offset + leftSplitSize, sizedCollectorTask.length - leftSplitSize);
            }
            if ($assertionsDisabled || sizedCollectorTask.offset + sizedCollectorTask.length < NUM) {
                sizedCollectorTask.helper.wrapAndCopyInto(sizedCollectorTask, spliterator2);
                sizedCollectorTask.propagateCompletion();
                return;
            }
            throw new AssertionError();
        }

        public void begin(long size) {
            long j = this.length;
            if (size <= j) {
                int i = (int) this.offset;
                this.index = i;
                this.fence = i + ((int) j);
                return;
            }
            throw new IllegalStateException("size passed to Sink.begin exceeds array length");
        }

        static final class OfRef<P_IN, P_OUT> extends SizedCollectorTask<P_IN, P_OUT, Sink<P_OUT>, OfRef<P_IN, P_OUT>> implements Sink<P_OUT> {
            private final P_OUT[] array;

            OfRef(Spliterator<P_IN> spliterator, PipelineHelper<P_OUT> helper, P_OUT[] array2) {
                super(spliterator, helper, array2.length);
                this.array = array2;
            }

            OfRef(OfRef<P_IN, P_OUT> parent, Spliterator<P_IN> spliterator, long offset, long length) {
                super(parent, spliterator, offset, length, parent.array.length);
                this.array = parent.array;
            }

            /* access modifiers changed from: package-private */
            public OfRef<P_IN, P_OUT> makeChild(Spliterator<P_IN> spliterator, long offset, long size) {
                return new OfRef(this, spliterator, offset, size);
            }

            public void accept(P_OUT value) {
                if (this.index < this.fence) {
                    P_OUT[] p_outArr = this.array;
                    int i = this.index;
                    this.index = i + 1;
                    p_outArr[i] = value;
                    return;
                }
                throw new IndexOutOfBoundsException(Integer.toString(this.index));
            }
        }

        static final class OfInt<P_IN> extends SizedCollectorTask<P_IN, Integer, Sink.OfInt, OfInt<P_IN>> implements Sink.OfInt {
            private final int[] array;

            public /* synthetic */ void accept(Integer num) {
                Sink.OfInt.CC.$default$accept((Sink.OfInt) this, num);
            }

            public /* bridge */ /* synthetic */ void accept(Object obj) {
                accept((Integer) obj);
            }

            public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
                return IntConsumer.CC.$default$andThen(this, intConsumer);
            }

            OfInt(Spliterator<P_IN> spliterator, PipelineHelper<Integer> helper, int[] array2) {
                super(spliterator, helper, array2.length);
                this.array = array2;
            }

            OfInt(OfInt<P_IN> parent, Spliterator<P_IN> spliterator, long offset, long length) {
                super(parent, spliterator, offset, length, parent.array.length);
                this.array = parent.array;
            }

            /* access modifiers changed from: package-private */
            public OfInt<P_IN> makeChild(Spliterator<P_IN> spliterator, long offset, long size) {
                return new OfInt(this, spliterator, offset, size);
            }

            public void accept(int value) {
                if (this.index < this.fence) {
                    int[] iArr = this.array;
                    int i = this.index;
                    this.index = i + 1;
                    iArr[i] = value;
                    return;
                }
                throw new IndexOutOfBoundsException(Integer.toString(this.index));
            }
        }

        static final class OfLong<P_IN> extends SizedCollectorTask<P_IN, Long, Sink.OfLong, OfLong<P_IN>> implements Sink.OfLong {
            private final long[] array;

            public /* synthetic */ void accept(Long l) {
                Sink.OfLong.CC.$default$accept((Sink.OfLong) this, l);
            }

            public /* bridge */ /* synthetic */ void accept(Object obj) {
                accept((Long) obj);
            }

            public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
                return LongConsumer.CC.$default$andThen(this, longConsumer);
            }

            OfLong(Spliterator<P_IN> spliterator, PipelineHelper<Long> helper, long[] array2) {
                super(spliterator, helper, array2.length);
                this.array = array2;
            }

            OfLong(OfLong<P_IN> parent, Spliterator<P_IN> spliterator, long offset, long length) {
                super(parent, spliterator, offset, length, parent.array.length);
                this.array = parent.array;
            }

            /* access modifiers changed from: package-private */
            public OfLong<P_IN> makeChild(Spliterator<P_IN> spliterator, long offset, long size) {
                return new OfLong(this, spliterator, offset, size);
            }

            public void accept(long value) {
                if (this.index < this.fence) {
                    long[] jArr = this.array;
                    int i = this.index;
                    this.index = i + 1;
                    jArr[i] = value;
                    return;
                }
                throw new IndexOutOfBoundsException(Integer.toString(this.index));
            }
        }

        static final class OfDouble<P_IN> extends SizedCollectorTask<P_IN, Double, Sink.OfDouble, OfDouble<P_IN>> implements Sink.OfDouble {
            private final double[] array;

            public /* synthetic */ void accept(Double d) {
                Sink.OfDouble.CC.$default$accept((Sink.OfDouble) this, d);
            }

            public /* bridge */ /* synthetic */ void accept(Object obj) {
                accept((Double) obj);
            }

            public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
                return DoubleConsumer.CC.$default$andThen(this, doubleConsumer);
            }

            OfDouble(Spliterator<P_IN> spliterator, PipelineHelper<Double> helper, double[] array2) {
                super(spliterator, helper, array2.length);
                this.array = array2;
            }

            OfDouble(OfDouble<P_IN> parent, Spliterator<P_IN> spliterator, long offset, long length) {
                super(parent, spliterator, offset, length, parent.array.length);
                this.array = parent.array;
            }

            /* access modifiers changed from: package-private */
            public OfDouble<P_IN> makeChild(Spliterator<P_IN> spliterator, long offset, long size) {
                return new OfDouble(this, spliterator, offset, size);
            }

            public void accept(double value) {
                if (this.index < this.fence) {
                    double[] dArr = this.array;
                    int i = this.index;
                    this.index = i + 1;
                    dArr[i] = value;
                    return;
                }
                throw new IndexOutOfBoundsException(Integer.toString(this.index));
            }
        }
    }

    private static abstract class ToArrayTask<T, T_NODE extends Node<T>, K extends ToArrayTask<T, T_NODE, K>> extends CountedCompleter<Void> {
        protected final T_NODE node;
        protected final int offset;

        /* access modifiers changed from: package-private */
        public abstract void copyNodeToArray();

        /* access modifiers changed from: package-private */
        public abstract K makeChild(int i, int i2);

        ToArrayTask(T_NODE node2, int offset2) {
            this.node = node2;
            this.offset = offset2;
        }

        ToArrayTask(K parent, T_NODE node2, int offset2) {
            super(parent);
            this.node = node2;
            this.offset = offset2;
        }

        public void compute() {
            ToArrayTask toArrayTask = this;
            while (toArrayTask.node.getChildCount() != 0) {
                toArrayTask.setPendingCount(toArrayTask.node.getChildCount() - 1);
                int size = 0;
                int i = 0;
                while (i < toArrayTask.node.getChildCount() - 1) {
                    K leftTask = toArrayTask.makeChild(i, toArrayTask.offset + size);
                    size = (int) (((long) size) + leftTask.node.count());
                    leftTask.fork();
                    i++;
                }
                toArrayTask = toArrayTask.makeChild(i, toArrayTask.offset + size);
            }
            toArrayTask.copyNodeToArray();
            toArrayTask.propagateCompletion();
        }

        private static final class OfRef<T> extends ToArrayTask<T, Node<T>, OfRef<T>> {
            private final T[] array;

            /* synthetic */ OfRef(Node x0, Object[] x1, int x2, AnonymousClass1 x3) {
                this(x0, (T[]) x1, x2);
            }

            private OfRef(Node<T> node, T[] array2, int offset) {
                super(node, offset);
                this.array = array2;
            }

            private OfRef(OfRef<T> parent, Node<T> node, int offset) {
                super(parent, node, offset);
                this.array = parent.array;
            }

            /* access modifiers changed from: package-private */
            public OfRef<T> makeChild(int childIndex, int offset) {
                return new OfRef<>(this, this.node.getChild(childIndex), offset);
            }

            /* access modifiers changed from: package-private */
            public void copyNodeToArray() {
                this.node.copyInto(this.array, this.offset);
            }
        }

        private static class OfPrimitive<T, T_CONS, T_ARR, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>, T_NODE extends Node.OfPrimitive<T, T_CONS, T_ARR, T_SPLITR, T_NODE>> extends ToArrayTask<T, T_NODE, OfPrimitive<T, T_CONS, T_ARR, T_SPLITR, T_NODE>> {
            private final T_ARR array;

            /* synthetic */ OfPrimitive(Node.OfPrimitive x0, Object x1, int x2, AnonymousClass1 x3) {
                this(x0, x1, x2);
            }

            private OfPrimitive(T_NODE node, T_ARR array2, int offset) {
                super(node, offset);
                this.array = array2;
            }

            private OfPrimitive(OfPrimitive<T, T_CONS, T_ARR, T_SPLITR, T_NODE> parent, T_NODE node, int offset) {
                super(parent, node, offset);
                this.array = parent.array;
            }

            /* access modifiers changed from: package-private */
            public OfPrimitive<T, T_CONS, T_ARR, T_SPLITR, T_NODE> makeChild(int childIndex, int offset) {
                return new OfPrimitive<>(this, ((Node.OfPrimitive) this.node).getChild(childIndex), offset);
            }

            /* access modifiers changed from: package-private */
            public void copyNodeToArray() {
                ((Node.OfPrimitive) this.node).copyInto(this.array, this.offset);
            }
        }

        private static final class OfInt extends OfPrimitive<Integer, IntConsumer, int[], Spliterator.OfInt, Node.OfInt> {
            /* synthetic */ OfInt(Node.OfInt x0, int[] x1, int x2, AnonymousClass1 x3) {
                this(x0, x1, x2);
            }

            private OfInt(Node.OfInt node, int[] array, int offset) {
                super(node, array, offset, (AnonymousClass1) null);
            }
        }

        private static final class OfLong extends OfPrimitive<Long, LongConsumer, long[], Spliterator.OfLong, Node.OfLong> {
            /* synthetic */ OfLong(Node.OfLong x0, long[] x1, int x2, AnonymousClass1 x3) {
                this(x0, x1, x2);
            }

            private OfLong(Node.OfLong node, long[] array, int offset) {
                super(node, array, offset, (AnonymousClass1) null);
            }
        }

        private static final class OfDouble extends OfPrimitive<Double, DoubleConsumer, double[], Spliterator.OfDouble, Node.OfDouble> {
            /* synthetic */ OfDouble(Node.OfDouble x0, double[] x1, int x2, AnonymousClass1 x3) {
                this(x0, x1, x2);
            }

            private OfDouble(Node.OfDouble node, double[] array, int offset) {
                super(node, array, offset, (AnonymousClass1) null);
            }
        }
    }

    private static class CollectorTask<P_IN, P_OUT, T_NODE extends Node<P_OUT>, T_BUILDER extends Node.Builder<P_OUT>> extends AbstractTask<P_IN, P_OUT, T_NODE, CollectorTask<P_IN, P_OUT, T_NODE, T_BUILDER>> {
        protected final LongFunction<T_BUILDER> builderFactory;
        protected final BinaryOperator<T_NODE> concFactory;
        protected final PipelineHelper<P_OUT> helper;

        CollectorTask(PipelineHelper<P_OUT> helper2, Spliterator<P_IN> spliterator, LongFunction<T_BUILDER> longFunction, BinaryOperator<T_NODE> binaryOperator) {
            super(helper2, spliterator);
            this.helper = helper2;
            this.builderFactory = longFunction;
            this.concFactory = binaryOperator;
        }

        CollectorTask(CollectorTask<P_IN, P_OUT, T_NODE, T_BUILDER> parent, Spliterator<P_IN> spliterator) {
            super(parent, spliterator);
            this.helper = parent.helper;
            this.builderFactory = parent.builderFactory;
            this.concFactory = parent.concFactory;
        }

        /* access modifiers changed from: protected */
        public CollectorTask<P_IN, P_OUT, T_NODE, T_BUILDER> makeChild(Spliterator<P_IN> spliterator) {
            return new CollectorTask<>(this, spliterator);
        }

        /* access modifiers changed from: protected */
        public T_NODE doLeaf() {
            return ((Node.Builder) this.helper.wrapAndCopyInto((Node.Builder) this.builderFactory.apply(this.helper.exactOutputSizeIfKnown(this.spliterator)), this.spliterator)).build();
        }

        public void onCompletion(CountedCompleter<?> caller) {
            if (!isLeaf()) {
                setLocalResult((Node) this.concFactory.apply((Node) ((CollectorTask) this.leftChild).getLocalResult(), (Node) ((CollectorTask) this.rightChild).getLocalResult()));
            }
            super.onCompletion(caller);
        }

        private static final class OfRef<P_IN, P_OUT> extends CollectorTask<P_IN, P_OUT, Node<P_OUT>, Node.Builder<P_OUT>> {
            OfRef(PipelineHelper<P_OUT> helper, IntFunction<P_OUT[]> intFunction, Spliterator<P_IN> spliterator) {
                super(helper, spliterator, new Nodes$CollectorTask$OfRef$$ExternalSyntheticLambda1(intFunction), Nodes$CollectorTask$OfRef$$ExternalSyntheticLambda0.INSTANCE);
            }
        }

        private static final class OfInt<P_IN> extends CollectorTask<P_IN, Integer, Node.OfInt, Node.Builder.OfInt> {
            OfInt(PipelineHelper<Integer> helper, Spliterator<P_IN> spliterator) {
                super(helper, spliterator, Nodes$CollectorTask$OfInt$$ExternalSyntheticLambda1.INSTANCE, Nodes$CollectorTask$OfInt$$ExternalSyntheticLambda0.INSTANCE);
            }
        }

        private static final class OfLong<P_IN> extends CollectorTask<P_IN, Long, Node.OfLong, Node.Builder.OfLong> {
            OfLong(PipelineHelper<Long> helper, Spliterator<P_IN> spliterator) {
                super(helper, spliterator, Nodes$CollectorTask$OfLong$$ExternalSyntheticLambda1.INSTANCE, Nodes$CollectorTask$OfLong$$ExternalSyntheticLambda0.INSTANCE);
            }
        }

        private static final class OfDouble<P_IN> extends CollectorTask<P_IN, Double, Node.OfDouble, Node.Builder.OfDouble> {
            OfDouble(PipelineHelper<Double> helper, Spliterator<P_IN> spliterator) {
                super(helper, spliterator, Nodes$CollectorTask$OfDouble$$ExternalSyntheticLambda1.INSTANCE, Nodes$CollectorTask$OfDouble$$ExternalSyntheticLambda0.INSTANCE);
            }
        }
    }
}
