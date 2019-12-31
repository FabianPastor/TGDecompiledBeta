package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.util.Pair;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.MarginLayoutParams;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.TLRPC.TL_pageTableCell;
import org.telegram.ui.ArticleViewer.DrawingText;
import org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView;
import org.telegram.ui.Cells.TextSelectionHelper.ArticleTextSelectionHelper;

public class TableLayout extends View {
    public static final int ALIGN_BOUNDS = 0;
    public static final int ALIGN_MARGINS = 1;
    public static final Alignment BASELINE = new Alignment() {
        public int getAlignmentValue(Child child, int i) {
            return Integer.MIN_VALUE;
        }

        /* Access modifiers changed, original: 0000 */
        public int getGravityOffset(Child child, int i) {
            return 0;
        }

        public Bounds getBounds() {
            return new Bounds() {
                private int size;

                /* Access modifiers changed, original: protected */
                public void reset() {
                    super.reset();
                    this.size = Integer.MIN_VALUE;
                }

                /* Access modifiers changed, original: protected */
                public void include(int i, int i2) {
                    super.include(i, i2);
                    this.size = Math.max(this.size, i + i2);
                }

                /* Access modifiers changed, original: protected */
                public int size(boolean z) {
                    return Math.max(super.size(z), this.size);
                }

                /* Access modifiers changed, original: protected */
                public int getOffset(TableLayout tableLayout, Child child, Alignment alignment, int i, boolean z) {
                    return Math.max(0, super.getOffset(tableLayout, child, alignment, i, z));
                }
            };
        }
    };
    public static final Alignment BOTTOM;
    private static final int CAN_STRETCH = 2;
    public static final Alignment CENTER = new Alignment() {
        public int getAlignmentValue(Child child, int i) {
            return i >> 1;
        }

        /* Access modifiers changed, original: 0000 */
        public int getGravityOffset(Child child, int i) {
            return i >> 1;
        }
    };
    private static final int DEFAULT_ALIGNMENT_MODE = 1;
    private static final int DEFAULT_COUNT = Integer.MIN_VALUE;
    private static final boolean DEFAULT_ORDER_PRESERVED = true;
    private static final int DEFAULT_ORIENTATION = 0;
    private static final boolean DEFAULT_USE_DEFAULT_MARGINS = false;
    public static final Alignment END;
    public static final Alignment FILL = new Alignment() {
        public int getAlignmentValue(Child child, int i) {
            return Integer.MIN_VALUE;
        }

        /* Access modifiers changed, original: 0000 */
        public int getGravityOffset(Child child, int i) {
            return 0;
        }

        public int getSizeInCell(Child child, int i, int i2) {
            return i2;
        }
    };
    public static final int HORIZONTAL = 0;
    private static final int INFLEXIBLE = 0;
    private static final Alignment LEADING = new Alignment() {
        public int getAlignmentValue(Child child, int i) {
            return 0;
        }

        /* Access modifiers changed, original: 0000 */
        public int getGravityOffset(Child child, int i) {
            return 0;
        }
    };
    public static final Alignment LEFT = createSwitchingAlignment(START);
    static final int MAX_SIZE = 100000;
    public static final Alignment RIGHT = createSwitchingAlignment(END);
    public static final Alignment START;
    public static final Alignment TOP;
    private static final Alignment TRAILING = new Alignment() {
        public int getAlignmentValue(Child child, int i) {
            return i;
        }

        /* Access modifiers changed, original: 0000 */
        public int getGravityOffset(Child child, int i) {
            return i;
        }
    };
    public static final int UNDEFINED = Integer.MIN_VALUE;
    static final Alignment UNDEFINED_ALIGNMENT = new Alignment() {
        public int getAlignmentValue(Child child, int i) {
            return Integer.MIN_VALUE;
        }

        /* Access modifiers changed, original: 0000 */
        public int getGravityOffset(Child child, int i) {
            return Integer.MIN_VALUE;
        }
    };
    static final int UNINITIALIZED_HASH = 0;
    public static final int VERTICAL = 1;
    private Path backgroundPath = new Path();
    private ArrayList<Child> cellsToFixHeight = new ArrayList();
    private ArrayList<Child> childrens = new ArrayList();
    private int colCount;
    private TableLayoutDelegate delegate;
    private boolean drawLines;
    private boolean isRtl;
    private boolean isStriped;
    private int itemPaddingLeft = AndroidUtilities.dp(8.0f);
    private int itemPaddingTop = AndroidUtilities.dp(7.0f);
    private Path linePath = new Path();
    private int mAlignmentMode = 1;
    private int mDefaultGap;
    private final Axis mHorizontalAxis = new Axis(this, true, null);
    private int mLastLayoutParamsHashCode = 0;
    private int mOrientation = 0;
    private boolean mUseDefaultMargins = false;
    private final Axis mVerticalAxis = new Axis(this, false, null);
    private float[] radii = new float[8];
    private RectF rect = new RectF();
    private ArrayList<Point> rowSpans = new ArrayList();
    private ArticleTextSelectionHelper textSelectionHelper;

    public static abstract class Alignment {
        public abstract int getAlignmentValue(Child child, int i);

        public abstract int getGravityOffset(Child child, int i);

        /* Access modifiers changed, original: 0000 */
        public int getSizeInCell(Child child, int i, int i2) {
            return i;
        }

        Alignment() {
        }

        /* Access modifiers changed, original: 0000 */
        public Bounds getBounds() {
            return new Bounds();
        }
    }

    static final class Arc {
        public final Interval span;
        public boolean valid = true;
        public final MutableInt value;

        public Arc(Interval interval, MutableInt mutableInt) {
            this.span = interval;
            this.value = mutableInt;
        }
    }

    static final class Assoc<K, V> extends ArrayList<Pair<K, V>> {
        private final Class<K> keyType;
        private final Class<V> valueType;

        private Assoc(Class<K> cls, Class<V> cls2) {
            this.keyType = cls;
            this.valueType = cls2;
        }

        public static <K, V> Assoc<K, V> of(Class<K> cls, Class<V> cls2) {
            return new Assoc(cls, cls2);
        }

        public void put(K k, V v) {
            add(Pair.create(k, v));
        }

        public PackedMap<K, V> pack() {
            int size = size();
            Object[] objArr = (Object[]) Array.newInstance(this.keyType, size);
            Object[] objArr2 = (Object[]) Array.newInstance(this.valueType, size);
            for (int i = 0; i < size; i++) {
                objArr[i] = ((Pair) get(i)).first;
                objArr2[i] = ((Pair) get(i)).second;
            }
            return new PackedMap(objArr, objArr2, null);
        }
    }

    final class Axis {
        private static final int COMPLETE = 2;
        private static final int NEW = 0;
        private static final int PENDING = 1;
        public Arc[] arcs;
        public boolean arcsValid;
        PackedMap<Interval, MutableInt> backwardLinks;
        public boolean backwardLinksValid;
        public int definedCount;
        public int[] deltas;
        PackedMap<Interval, MutableInt> forwardLinks;
        public boolean forwardLinksValid;
        PackedMap<Spec, Bounds> groupBounds;
        public boolean groupBoundsValid;
        public boolean hasWeights;
        public boolean hasWeightsValid;
        public final boolean horizontal;
        public int[] leadingMargins;
        public boolean leadingMarginsValid;
        public int[] locations;
        public boolean locationsValid;
        private int maxIndex;
        boolean orderPreserved;
        private MutableInt parentMax;
        private MutableInt parentMin;
        public int[] trailingMargins;
        public boolean trailingMarginsValid;

        /* synthetic */ Axis(TableLayout tableLayout, boolean z, AnonymousClass1 anonymousClass1) {
            this(z);
        }

        private Axis(boolean z) {
            this.definedCount = Integer.MIN_VALUE;
            this.maxIndex = Integer.MIN_VALUE;
            this.groupBoundsValid = false;
            this.forwardLinksValid = false;
            this.backwardLinksValid = false;
            this.leadingMarginsValid = false;
            this.trailingMarginsValid = false;
            this.arcsValid = false;
            this.locationsValid = false;
            this.hasWeightsValid = false;
            this.orderPreserved = true;
            this.parentMin = new MutableInt(0);
            this.parentMax = new MutableInt(-100000);
            this.horizontal = z;
        }

        private int calculateMaxIndex() {
            int childCount = TableLayout.this.getChildCount();
            int i = -1;
            for (int i2 = 0; i2 < childCount; i2++) {
                LayoutParams layoutParams = TableLayout.this.getChildAt(i2).getLayoutParams();
                Interval interval = (this.horizontal ? layoutParams.columnSpec : layoutParams.rowSpec).span;
                i = Math.max(Math.max(Math.max(i, interval.min), interval.max), interval.size());
            }
            return i == -1 ? Integer.MIN_VALUE : i;
        }

        private int getMaxIndex() {
            if (this.maxIndex == Integer.MIN_VALUE) {
                this.maxIndex = Math.max(0, calculateMaxIndex());
            }
            return this.maxIndex;
        }

        public int getCount() {
            return Math.max(this.definedCount, getMaxIndex());
        }

        public void setCount(int i) {
            if (i != Integer.MIN_VALUE && i < getMaxIndex()) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(this.horizontal ? "column" : "row");
                stringBuilder.append("Count must be greater than or equal to the maximum of all grid indices (and spans) defined in the LayoutParams of each child");
                TableLayout.handleInvalidParams(stringBuilder.toString());
            }
            this.definedCount = i;
        }

        public boolean isOrderPreserved() {
            return this.orderPreserved;
        }

        public void setOrderPreserved(boolean z) {
            this.orderPreserved = z;
            invalidateStructure();
        }

        private PackedMap<Spec, Bounds> createGroupBounds() {
            Assoc of = Assoc.of(Spec.class, Bounds.class);
            int childCount = TableLayout.this.getChildCount();
            for (int i = 0; i < childCount; i++) {
                LayoutParams layoutParams = TableLayout.this.getChildAt(i).getLayoutParams();
                Spec spec = this.horizontal ? layoutParams.columnSpec : layoutParams.rowSpec;
                of.put(spec, spec.getAbsoluteAlignment(this.horizontal).getBounds());
            }
            return of.pack();
        }

        private void computeGroupBounds() {
            Bounds[] boundsArr = (Bounds[]) this.groupBounds.values;
            for (Bounds reset : boundsArr) {
                reset.reset();
            }
            int childCount = TableLayout.this.getChildCount();
            int i = 0;
            while (i < childCount) {
                Child childAt = TableLayout.this.getChildAt(i);
                LayoutParams layoutParams = childAt.getLayoutParams();
                Spec spec = this.horizontal ? layoutParams.columnSpec : layoutParams.rowSpec;
                ((Bounds) this.groupBounds.getValue(i)).include(TableLayout.this, childAt, spec, this, TableLayout.this.getMeasurementIncludingMargin(childAt, this.horizontal) + (spec.weight == 0.0f ? 0 : this.deltas[i]));
                i++;
            }
        }

        public PackedMap<Spec, Bounds> getGroupBounds() {
            if (this.groupBounds == null) {
                this.groupBounds = createGroupBounds();
            }
            if (!this.groupBoundsValid) {
                computeGroupBounds();
                this.groupBoundsValid = true;
            }
            return this.groupBounds;
        }

        private PackedMap<Interval, MutableInt> createLinks(boolean z) {
            Assoc of = Assoc.of(Interval.class, MutableInt.class);
            Spec[] specArr = (Spec[]) getGroupBounds().keys;
            int length = specArr.length;
            for (int i = 0; i < length; i++) {
                of.put(z ? specArr[i].span : specArr[i].span.inverse(), new MutableInt());
            }
            return of.pack();
        }

        private void computeLinks(PackedMap<Interval, MutableInt> packedMap, boolean z) {
            int size;
            MutableInt mutableInt;
            MutableInt[] mutableIntArr = (MutableInt[]) packedMap.values;
            for (MutableInt mutableInt2 : mutableIntArr) {
                mutableInt2.reset();
            }
            Bounds[] boundsArr = (Bounds[]) getGroupBounds().values;
            for (int i = 0; i < boundsArr.length; i++) {
                size = boundsArr[i].size(z);
                mutableInt2 = (MutableInt) packedMap.getValue(i);
                int i2 = mutableInt2.value;
                if (!z) {
                    size = -size;
                }
                mutableInt2.value = Math.max(i2, size);
            }
        }

        private PackedMap<Interval, MutableInt> getForwardLinks() {
            if (this.forwardLinks == null) {
                this.forwardLinks = createLinks(true);
            }
            if (!this.forwardLinksValid) {
                computeLinks(this.forwardLinks, true);
                this.forwardLinksValid = true;
            }
            return this.forwardLinks;
        }

        private PackedMap<Interval, MutableInt> getBackwardLinks() {
            if (this.backwardLinks == null) {
                this.backwardLinks = createLinks(false);
            }
            if (!this.backwardLinksValid) {
                computeLinks(this.backwardLinks, false);
                this.backwardLinksValid = true;
            }
            return this.backwardLinks;
        }

        private void include(List<Arc> list, Interval interval, MutableInt mutableInt, boolean z) {
            if (interval.size() != 0) {
                if (z) {
                    for (Arc arc : list) {
                        if (arc.span.equals(interval)) {
                            return;
                        }
                    }
                }
                list.add(new Arc(interval, mutableInt));
            }
        }

        private void include(List<Arc> list, Interval interval, MutableInt mutableInt) {
            include(list, interval, mutableInt, true);
        }

        /* Access modifiers changed, original: 0000 */
        public Arc[][] groupArcsByFirstVertex(Arc[] arcArr) {
            int i;
            int i2;
            int count = getCount() + 1;
            Arc[][] arcArr2 = new Arc[count][];
            int[] iArr = new int[count];
            int i3 = 0;
            for (Arc arc : arcArr) {
                i2 = arc.span.min;
                iArr[i2] = iArr[i2] + 1;
            }
            for (i = 0; i < iArr.length; i++) {
                arcArr2[i] = new Arc[iArr[i]];
            }
            Arrays.fill(iArr, 0);
            i = arcArr.length;
            while (i3 < i) {
                Arc arc2 = arcArr[i3];
                i2 = arc2.span.min;
                Arc[] arcArr3 = arcArr2[i2];
                int i4 = iArr[i2];
                iArr[i2] = i4 + 1;
                arcArr3[i4] = arc2;
                i3++;
            }
            return arcArr2;
        }

        private Arc[] topologicalSort(final Arc[] arcArr) {
            return new Object() {
                Arc[][] arcsByVertex;
                int cursor = (this.result.length - 1);
                Arc[] result;
                int[] visited;

                /* Access modifiers changed, original: 0000 */
                public void walk(int i) {
                    int[] iArr = this.visited;
                    if (iArr[i] == 0) {
                        iArr[i] = 1;
                        for (Arc arc : this.arcsByVertex[i]) {
                            walk(arc.span.max);
                            Arc[] arcArr = this.result;
                            int i2 = this.cursor;
                            this.cursor = i2 - 1;
                            arcArr[i2] = arc;
                        }
                        this.visited[i] = 2;
                    }
                }

                /* Access modifiers changed, original: 0000 */
                public Arc[] sort() {
                    int length = this.arcsByVertex.length;
                    for (int i = 0; i < length; i++) {
                        walk(i);
                    }
                    return this.result;
                }
            }.sort();
        }

        private Arc[] topologicalSort(List<Arc> list) {
            return topologicalSort((Arc[]) list.toArray(new Arc[list.size()]));
        }

        private void addComponentSizes(List<Arc> list, PackedMap<Interval, MutableInt> packedMap) {
            int i = 0;
            while (true) {
                Object[] objArr = packedMap.keys;
                if (i < ((Interval[]) objArr).length) {
                    include(list, ((Interval[]) objArr)[i], ((MutableInt[]) packedMap.values)[i], false);
                    i++;
                } else {
                    return;
                }
            }
        }

        private Arc[] createArcs() {
            int i;
            List arrayList = new ArrayList();
            List arrayList2 = new ArrayList();
            addComponentSizes(arrayList, getForwardLinks());
            addComponentSizes(arrayList2, getBackwardLinks());
            if (this.orderPreserved) {
                i = 0;
                while (i < getCount()) {
                    int i2 = i + 1;
                    include(arrayList, new Interval(i, i2), new MutableInt(0));
                    i = i2;
                }
            }
            i = getCount();
            include(arrayList, new Interval(0, i), this.parentMin, false);
            include(arrayList2, new Interval(i, 0), this.parentMax, false);
            return (Arc[]) TableLayout.append(topologicalSort(arrayList), topologicalSort(arrayList2));
        }

        private void computeArcs() {
            getForwardLinks();
            getBackwardLinks();
        }

        public Arc[] getArcs() {
            if (this.arcs == null) {
                this.arcs = createArcs();
            }
            if (!this.arcsValid) {
                computeArcs();
                this.arcsValid = true;
            }
            return this.arcs;
        }

        private boolean relax(int[] iArr, Arc arc) {
            if (!arc.valid) {
                return false;
            }
            Interval interval = arc.span;
            int i = interval.min;
            int i2 = interval.max;
            i = iArr[i] + arc.value.value;
            if (i <= iArr[i2]) {
                return false;
            }
            iArr[i2] = i;
            return true;
        }

        private void init(int[] iArr) {
            Arrays.fill(iArr, 0);
        }

        private boolean solve(Arc[] arcArr, int[] iArr) {
            return solve(arcArr, iArr, true);
        }

        private boolean solve(Arc[] arcArr, int[] iArr, boolean z) {
            int count = getCount() + 1;
            for (int i = 0; i < arcArr.length; i++) {
                int i2;
                int length;
                init(iArr);
                for (int i3 = 0; i3 < count; i3++) {
                    i2 = 0;
                    for (Arc relax : arcArr) {
                        i2 |= relax(iArr, relax);
                    }
                    if (i2 == 0) {
                        return true;
                    }
                }
                if (!z) {
                    return false;
                }
                int i4;
                boolean[] zArr = new boolean[arcArr.length];
                for (i4 = 0; i4 < count; i4++) {
                    length = arcArr.length;
                    for (i2 = 0; i2 < length; i2++) {
                        zArr[i2] = zArr[i2] | relax(iArr, arcArr[i2]);
                    }
                }
                for (i4 = 0; i4 < arcArr.length; i4++) {
                    if (zArr[i4]) {
                        Arc arc = arcArr[i4];
                        Interval interval = arc.span;
                        if (interval.min >= interval.max) {
                            arc.valid = false;
                            break;
                        }
                    }
                }
            }
            return true;
        }

        private void computeMargins(boolean z) {
            int[] iArr = z ? this.leadingMargins : this.trailingMargins;
            int childCount = TableLayout.this.getChildCount();
            for (int i = 0; i < childCount; i++) {
                Child childAt = TableLayout.this.getChildAt(i);
                LayoutParams layoutParams = childAt.getLayoutParams();
                Interval interval = (this.horizontal ? layoutParams.columnSpec : layoutParams.rowSpec).span;
                int i2 = z ? interval.min : interval.max;
                iArr[i2] = Math.max(iArr[i2], TableLayout.this.getMargin1(childAt, this.horizontal, z));
            }
        }

        public int[] getLeadingMargins() {
            if (this.leadingMargins == null) {
                this.leadingMargins = new int[(getCount() + 1)];
            }
            if (!this.leadingMarginsValid) {
                computeMargins(true);
                this.leadingMarginsValid = true;
            }
            return this.leadingMargins;
        }

        public int[] getTrailingMargins() {
            if (this.trailingMargins == null) {
                this.trailingMargins = new int[(getCount() + 1)];
            }
            if (!this.trailingMarginsValid) {
                computeMargins(false);
                this.trailingMarginsValid = true;
            }
            return this.trailingMargins;
        }

        private boolean solve(int[] iArr) {
            return solve(getArcs(), iArr);
        }

        private boolean computeHasWeights() {
            int childCount = TableLayout.this.getChildCount();
            for (int i = 0; i < childCount; i++) {
                LayoutParams layoutParams = TableLayout.this.getChildAt(i).getLayoutParams();
                if ((this.horizontal ? layoutParams.columnSpec : layoutParams.rowSpec).weight != 0.0f) {
                    return true;
                }
            }
            return false;
        }

        private boolean hasWeights() {
            if (!this.hasWeightsValid) {
                this.hasWeights = computeHasWeights();
                this.hasWeightsValid = true;
            }
            return this.hasWeights;
        }

        public int[] getDeltas() {
            if (this.deltas == null) {
                this.deltas = new int[TableLayout.this.getChildCount()];
            }
            return this.deltas;
        }

        private void shareOutDelta(int i, float f) {
            int i2 = 0;
            Arrays.fill(this.deltas, 0);
            int childCount = TableLayout.this.getChildCount();
            while (i2 < childCount) {
                LayoutParams layoutParams = TableLayout.this.getChildAt(i2).getLayoutParams();
                float f2 = (this.horizontal ? layoutParams.columnSpec : layoutParams.rowSpec).weight;
                if (f2 != 0.0f) {
                    int round = Math.round((((float) i) * f2) / f);
                    this.deltas[i2] = round;
                    i -= round;
                    f -= f2;
                }
                i2++;
            }
        }

        private void solveAndDistributeSpace(int[] iArr) {
            Arrays.fill(getDeltas(), 0);
            solve(iArr);
            int childCount = (this.parentMin.value * TableLayout.this.getChildCount()) + 1;
            if (childCount >= 2) {
                float calculateTotalWeight = calculateTotalWeight();
                int i = -1;
                int i2 = childCount;
                childCount = 0;
                boolean z = true;
                while (childCount < i2) {
                    int i3 = (int) ((((long) childCount) + ((long) i2)) / 2);
                    invalidateValues();
                    shareOutDelta(i3, calculateTotalWeight);
                    z = solve(getArcs(), iArr, false);
                    if (z) {
                        childCount = i3 + 1;
                        i = i3;
                    } else {
                        i2 = i3;
                    }
                }
                if (i > 0 && !z) {
                    invalidateValues();
                    shareOutDelta(i, calculateTotalWeight);
                    solve(iArr);
                }
            }
        }

        private float calculateTotalWeight() {
            int childCount = TableLayout.this.getChildCount();
            float f = 0.0f;
            for (int i = 0; i < childCount; i++) {
                LayoutParams layoutParams = TableLayout.this.getChildAt(i).getLayoutParams();
                f += (this.horizontal ? layoutParams.columnSpec : layoutParams.rowSpec).weight;
            }
            return f;
        }

        private void computeLocations(int[] iArr) {
            if (hasWeights()) {
                solveAndDistributeSpace(iArr);
            } else {
                solve(iArr);
            }
            if (!this.orderPreserved) {
                int i = 0;
                int i2 = iArr[0];
                int length = iArr.length;
                while (i < length) {
                    iArr[i] = iArr[i] - i2;
                    i++;
                }
            }
        }

        public int[] getLocations() {
            if (this.locations == null) {
                this.locations = new int[(getCount() + 1)];
            }
            if (!this.locationsValid) {
                computeLocations(this.locations);
                this.locationsValid = true;
            }
            return this.locations;
        }

        private int size(int[] iArr) {
            return iArr[getCount()];
        }

        private void setParentConstraints(int i, int i2) {
            this.parentMin.value = i;
            this.parentMax.value = -i2;
            this.locationsValid = false;
        }

        private int getMeasure(int i, int i2) {
            setParentConstraints(i, i2);
            return size(getLocations());
        }

        public int getMeasure(int i) {
            int mode = MeasureSpec.getMode(i);
            i = MeasureSpec.getSize(i);
            if (mode == Integer.MIN_VALUE) {
                return getMeasure(0, i);
            }
            if (mode == 0) {
                return getMeasure(0, 100000);
            }
            if (mode != NUM) {
                return 0;
            }
            return getMeasure(i, i);
        }

        public void layout(int i) {
            setParentConstraints(i, i);
            getLocations();
        }

        public void invalidateStructure() {
            this.maxIndex = Integer.MIN_VALUE;
            this.groupBounds = null;
            this.forwardLinks = null;
            this.backwardLinks = null;
            this.leadingMargins = null;
            this.trailingMargins = null;
            this.arcs = null;
            this.locations = null;
            this.deltas = null;
            this.hasWeightsValid = false;
            invalidateValues();
        }

        public void invalidateValues() {
            this.groupBoundsValid = false;
            this.forwardLinksValid = false;
            this.backwardLinksValid = false;
            this.leadingMarginsValid = false;
            this.trailingMarginsValid = false;
            this.arcsValid = false;
            this.locationsValid = false;
        }
    }

    static class Bounds {
        public int after;
        public int before;
        public int flexibility;

        /* synthetic */ Bounds(AnonymousClass1 anonymousClass1) {
            this();
        }

        private Bounds() {
            reset();
        }

        /* Access modifiers changed, original: protected */
        public void reset() {
            this.before = Integer.MIN_VALUE;
            this.after = Integer.MIN_VALUE;
            this.flexibility = 2;
        }

        /* Access modifiers changed, original: protected */
        public void include(int i, int i2) {
            this.before = Math.max(this.before, i);
            this.after = Math.max(this.after, i2);
        }

        /* Access modifiers changed, original: protected */
        public int size(boolean z) {
            if (z || !TableLayout.canStretch(this.flexibility)) {
                return this.before + this.after;
            }
            return 100000;
        }

        /* Access modifiers changed, original: protected */
        public int getOffset(TableLayout tableLayout, Child child, Alignment alignment, int i, boolean z) {
            return this.before - alignment.getAlignmentValue(child, i);
        }

        /* Access modifiers changed, original: protected|final */
        public final void include(TableLayout tableLayout, Child child, Spec spec, Axis axis, int i) {
            this.flexibility &= spec.getFlexibility();
            int alignmentValue = spec.getAbsoluteAlignment(axis.horizontal).getAlignmentValue(child, i);
            include(alignmentValue, i - alignmentValue);
        }
    }

    public class Child {
        private TL_pageTableCell cell;
        private int fixedHeight;
        private int index;
        private LayoutParams layoutParams;
        private int measuredHeight;
        private int measuredWidth;
        public int rowspan;
        private int selectionIndex = -1;
        public int textHeight;
        public DrawingText textLayout;
        public int textLeft;
        public int textWidth;
        public int textX;
        public int textY;
        public int x;
        public int y;

        public Child(int i) {
            this.index = i;
        }

        public LayoutParams getLayoutParams() {
            return this.layoutParams;
        }

        public int getMeasuredWidth() {
            return this.measuredWidth;
        }

        public int getMeasuredHeight() {
            return this.measuredHeight;
        }

        /* JADX WARNING: Missing block: B:22:0x004e, code skipped:
            if (r2.align_right == false) goto L_0x0077;
     */
        public void measure(int r2, int r3, boolean r4) {
            /*
            r1 = this;
            r1.measuredWidth = r2;
            r1.measuredHeight = r3;
            if (r4 == 0) goto L_0x000a;
        L_0x0006:
            r2 = r1.measuredHeight;
            r1.fixedHeight = r2;
        L_0x000a:
            r2 = r1.cell;
            if (r2 == 0) goto L_0x00c0;
        L_0x000e:
            r3 = r2.valign_middle;
            if (r3 == 0) goto L_0x001c;
        L_0x0012:
            r2 = r1.measuredHeight;
            r3 = r1.textHeight;
            r2 = r2 - r3;
            r2 = r2 / 2;
            r1.textY = r2;
            goto L_0x0037;
        L_0x001c:
            r2 = r2.valign_bottom;
            if (r2 == 0) goto L_0x002f;
        L_0x0020:
            r2 = r1.measuredHeight;
            r3 = r1.textHeight;
            r2 = r2 - r3;
            r3 = org.telegram.ui.Components.TableLayout.this;
            r3 = r3.itemPaddingTop;
            r2 = r2 - r3;
            r1.textY = r2;
            goto L_0x0037;
        L_0x002f:
            r2 = org.telegram.ui.Components.TableLayout.this;
            r2 = r2.itemPaddingTop;
            r1.textY = r2;
        L_0x0037:
            r2 = r1.textLayout;
            if (r2 == 0) goto L_0x00c0;
        L_0x003b:
            r2 = r2.getLineCount();
            if (r4 != 0) goto L_0x0077;
        L_0x0041:
            r3 = 1;
            if (r2 > r3) goto L_0x0050;
        L_0x0044:
            if (r2 <= 0) goto L_0x0077;
        L_0x0046:
            r2 = r1.cell;
            r3 = r2.align_center;
            if (r3 != 0) goto L_0x0050;
        L_0x004c:
            r2 = r2.align_right;
            if (r2 == 0) goto L_0x0077;
        L_0x0050:
            r2 = org.telegram.ui.Components.TableLayout.this;
            r2 = r2.delegate;
            r3 = r1.cell;
            r4 = r1.measuredWidth;
            r0 = org.telegram.ui.Components.TableLayout.this;
            r0 = r0.itemPaddingLeft;
            r0 = r0 * 2;
            r4 = r4 - r0;
            r2 = r2.createTextLayout(r3, r4);
            r1.setTextLayout(r2);
            r2 = r1.textHeight;
            r3 = org.telegram.ui.Components.TableLayout.this;
            r3 = r3.itemPaddingTop;
            r3 = r3 * 2;
            r2 = r2 + r3;
            r1.fixedHeight = r2;
        L_0x0077:
            r2 = r1.textLeft;
            if (r2 == 0) goto L_0x00b8;
        L_0x007b:
            r2 = -r2;
            r1.textX = r2;
            r2 = r1.cell;
            r3 = r2.align_right;
            if (r3 == 0) goto L_0x0096;
        L_0x0084:
            r2 = r1.textX;
            r3 = r1.measuredWidth;
            r4 = r1.textWidth;
            r3 = r3 - r4;
            r4 = org.telegram.ui.Components.TableLayout.this;
            r4 = r4.itemPaddingLeft;
            r3 = r3 - r4;
            r2 = r2 + r3;
            r1.textX = r2;
            goto L_0x00c0;
        L_0x0096:
            r2 = r2.align_center;
            if (r2 == 0) goto L_0x00ac;
        L_0x009a:
            r2 = r1.textX;
            r3 = r1.measuredWidth;
            r4 = r1.textWidth;
            r3 = r3 - r4;
            r3 = r3 / 2;
            r3 = (float) r3;
            r3 = java.lang.Math.round(r3);
            r2 = r2 + r3;
            r1.textX = r2;
            goto L_0x00c0;
        L_0x00ac:
            r2 = r1.textX;
            r3 = org.telegram.ui.Components.TableLayout.this;
            r3 = r3.itemPaddingLeft;
            r2 = r2 + r3;
            r1.textX = r2;
            goto L_0x00c0;
        L_0x00b8:
            r2 = org.telegram.ui.Components.TableLayout.this;
            r2 = r2.itemPaddingLeft;
            r1.textX = r2;
        L_0x00c0:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TableLayout$Child.measure(int, int, boolean):void");
        }

        public void setTextLayout(DrawingText drawingText) {
            this.textLayout = drawingText;
            int i = 0;
            if (drawingText != null) {
                this.textWidth = 0;
                this.textLeft = 0;
                int lineCount = drawingText.getLineCount();
                while (i < lineCount) {
                    float lineLeft = drawingText.getLineLeft(i);
                    this.textLeft = i == 0 ? (int) Math.ceil((double) lineLeft) : Math.min(this.textLeft, (int) Math.ceil((double) lineLeft));
                    this.textWidth = (int) Math.ceil((double) Math.max(drawingText.getLineWidth(i), (float) this.textWidth));
                    i++;
                }
                this.textHeight = drawingText.getHeight();
                return;
            }
            this.textLeft = 0;
            this.textWidth = 0;
            this.textHeight = 0;
        }

        public void layout(int i, int i2, int i3, int i4) {
            this.x = i;
            this.y = i2;
        }

        public int getTextX() {
            return this.x + this.textX;
        }

        public int getTextY() {
            return this.y + this.textY;
        }

        public void setFixedHeight(int i) {
            this.measuredHeight = this.fixedHeight;
            TL_pageTableCell tL_pageTableCell = this.cell;
            if (tL_pageTableCell.valign_middle) {
                this.textY = (this.measuredHeight - this.textHeight) / 2;
            } else if (tL_pageTableCell.valign_bottom) {
                this.textY = (this.measuredHeight - this.textHeight) - TableLayout.this.itemPaddingTop;
            }
        }

        public void draw(Canvas canvas) {
            if (this.cell != null) {
                float f;
                float f2;
                float f3;
                int i;
                float f4;
                int i2;
                float f5;
                int i3 = 0;
                Object obj = this.x + this.measuredWidth == TableLayout.this.getMeasuredWidth() ? 1 : null;
                Object obj2 = this.y + this.measuredHeight == TableLayout.this.getMeasuredHeight() ? 1 : null;
                int dp = AndroidUtilities.dp(3.0f);
                if (this.cell.header || (TableLayout.this.isStriped && this.layoutParams.rowSpec.span.min % 2 == 0)) {
                    float[] access$500;
                    float[] access$5002;
                    if (this.x == 0 && this.y == 0) {
                        access$5002 = TableLayout.this.radii;
                        f = (float) dp;
                        TableLayout.this.radii[1] = f;
                        access$5002[0] = f;
                        i3 = 1;
                    } else {
                        access$5002 = TableLayout.this.radii;
                        TableLayout.this.radii[1] = 0.0f;
                        access$5002[0] = 0.0f;
                    }
                    if (obj == null || this.y != 0) {
                        float[] access$5003 = TableLayout.this.radii;
                        TableLayout.this.radii[3] = 0.0f;
                        access$5003[2] = 0.0f;
                    } else {
                        access$500 = TableLayout.this.radii;
                        f = (float) dp;
                        TableLayout.this.radii[3] = f;
                        access$500[2] = f;
                        i3 = 1;
                    }
                    if (obj == null || obj2 == null) {
                        float[] access$5004 = TableLayout.this.radii;
                        TableLayout.this.radii[5] = 0.0f;
                        access$5004[4] = 0.0f;
                    } else {
                        access$500 = TableLayout.this.radii;
                        f2 = (float) dp;
                        TableLayout.this.radii[5] = f2;
                        access$500[4] = f2;
                        i3 = 1;
                    }
                    if (this.x != 0 || obj2 == null) {
                        float[] access$5005 = TableLayout.this.radii;
                        TableLayout.this.radii[7] = 0.0f;
                        access$5005[6] = 0.0f;
                    } else {
                        access$500 = TableLayout.this.radii;
                        f3 = (float) dp;
                        TableLayout.this.radii[7] = f3;
                        access$500[6] = f3;
                        i3 = 1;
                    }
                    if (i3 != 0) {
                        RectF access$600 = TableLayout.this.rect;
                        i = this.x;
                        f4 = (float) i;
                        i2 = this.y;
                        access$600.set(f4, (float) i2, (float) (i + this.measuredWidth), (float) (i2 + this.measuredHeight));
                        TableLayout.this.backgroundPath.reset();
                        TableLayout.this.backgroundPath.addRoundRect(TableLayout.this.rect, TableLayout.this.radii, Direction.CW);
                        if (this.cell.header) {
                            canvas.drawPath(TableLayout.this.backgroundPath, TableLayout.this.delegate.getHeaderPaint());
                        } else {
                            canvas.drawPath(TableLayout.this.backgroundPath, TableLayout.this.delegate.getStripPaint());
                        }
                    } else if (this.cell.header) {
                        i3 = this.x;
                        f5 = (float) i3;
                        i = this.y;
                        canvas.drawRect(f5, (float) i, (float) (i3 + this.measuredWidth), (float) (i + this.measuredHeight), TableLayout.this.delegate.getHeaderPaint());
                    } else {
                        i3 = this.x;
                        f5 = (float) i3;
                        i = this.y;
                        canvas.drawRect(f5, (float) i, (float) (i3 + this.measuredWidth), (float) (i + this.measuredHeight), TableLayout.this.delegate.getStripPaint());
                    }
                }
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) getTextX(), (float) getTextY());
                    if (this.selectionIndex >= 0) {
                        TableLayout.this.textSelectionHelper.draw(canvas, (ArticleSelectableView) TableLayout.this.getParent().getParent(), this.selectionIndex);
                    }
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                if (TableLayout.this.drawLines) {
                    float f6;
                    int i4;
                    float f7;
                    int i5;
                    RectF access$6002;
                    int i6;
                    int i7;
                    float f8;
                    RectF access$6003;
                    int i8;
                    float f9;
                    int i9;
                    Paint linePaint = TableLayout.this.delegate.getLinePaint();
                    Paint linePaint2 = TableLayout.this.delegate.getLinePaint();
                    f4 = linePaint.getStrokeWidth() / 2.0f;
                    float strokeWidth = linePaint2.getStrokeWidth() / 2.0f;
                    i2 = this.x;
                    if (i2 == 0) {
                        i2 = this.y;
                        f5 = (float) i2;
                        f = (float) (this.measuredHeight + i2);
                        if (i2 == 0) {
                            f5 += (float) dp;
                        }
                        f2 = f5;
                        if (f == ((float) TableLayout.this.getMeasuredHeight())) {
                            f -= (float) dp;
                        }
                        f6 = f;
                        i2 = this.x;
                        canvas.drawLine(((float) i2) + f4, f2, ((float) i2) + f4, f6, linePaint);
                    } else {
                        f5 = ((float) i2) - strokeWidth;
                        i4 = this.y;
                        canvas.drawLine(f5, (float) i4, ((float) i2) - strokeWidth, (float) (i4 + this.measuredHeight), linePaint2);
                    }
                    i2 = this.y;
                    if (i2 == 0) {
                        i = this.x;
                        f3 = (float) i;
                        f5 = (float) (this.measuredWidth + i);
                        if (i == 0) {
                            f3 += (float) dp;
                        }
                        f7 = f3;
                        if (f5 == ((float) TableLayout.this.getMeasuredWidth())) {
                            f5 -= (float) dp;
                        }
                        f2 = f5;
                        i2 = this.y;
                        canvas.drawLine(f7, ((float) i2) + f4, f2, ((float) i2) + f4, linePaint);
                    } else {
                        i5 = this.x;
                        canvas.drawLine((float) i5, ((float) i2) - strokeWidth, (float) (i5 + this.measuredWidth), ((float) i2) - strokeWidth, linePaint2);
                    }
                    if (obj != null) {
                        i = this.y;
                        if (i == 0) {
                            f7 = (float) (i + dp);
                            f = f7;
                            if (obj != null || obj2 == null) {
                                f7 = ((float) (this.y + this.measuredHeight)) - f4;
                            } else {
                                f7 = (float) ((this.y + this.measuredHeight) - dp);
                            }
                            f6 = f7;
                            i = this.x;
                            i2 = this.measuredWidth;
                            canvas.drawLine(((float) (i + i2)) - f4, f, ((float) (i + i2)) - f4, f6, linePaint);
                            i = this.x;
                            if (i == 0 || obj2 == null) {
                                f7 = ((float) this.x) - f4;
                            } else {
                                f7 = (float) (i + dp);
                            }
                            f5 = f7;
                            if (obj != null || obj2 == null) {
                                f7 = ((float) (this.x + this.measuredWidth)) - f4;
                            } else {
                                f7 = (float) ((this.x + this.measuredWidth) - dp);
                            }
                            f2 = f7;
                            i = this.y;
                            i2 = this.measuredHeight;
                            canvas.drawLine(f5, ((float) (i + i2)) - f4, f2, ((float) (i + i2)) - f4, linePaint);
                            if (this.x == 0 && this.y == 0) {
                                access$6002 = TableLayout.this.rect;
                                i2 = this.x;
                                f5 = ((float) i2) + f4;
                                i4 = this.y;
                                f6 = (float) (dp * 2);
                                access$6002.set(f5, ((float) i4) + f4, (((float) i2) + f4) + f6, (((float) i4) + f4) + f6);
                                canvas.drawArc(TableLayout.this.rect, -180.0f, 90.0f, false, linePaint);
                            }
                            if (obj != null && this.y == 0) {
                                access$6002 = TableLayout.this.rect;
                                i2 = this.x;
                                i5 = this.measuredWidth;
                                f2 = (float) (dp * 2);
                                f = (((float) (i2 + i5)) - f4) - f2;
                                i6 = this.y;
                                access$6002.set(f, ((float) i6) + f4, ((float) (i2 + i5)) - f4, (((float) i6) + f4) + f2);
                                canvas.drawArc(TableLayout.this.rect, 0.0f, -90.0f, false, linePaint);
                            }
                            if (this.x == 0 && obj2 != null) {
                                access$6002 = TableLayout.this.rect;
                                i2 = this.x;
                                f5 = ((float) i2) + f4;
                                i4 = this.y;
                                i7 = this.measuredHeight;
                                f8 = (float) (dp * 2);
                                access$6002.set(f5, (((float) (i4 + i7)) - f4) - f8, (((float) i2) + f4) + f8, ((float) (i4 + i7)) - f4);
                                canvas.drawArc(TableLayout.this.rect, 180.0f, -90.0f, false, linePaint);
                            }
                            if (!(obj == null || obj2 == null)) {
                                access$6003 = TableLayout.this.rect;
                                i8 = this.x;
                                i = this.measuredWidth;
                                f9 = (float) (dp * 2);
                                f3 = (((float) (i8 + i)) - f4) - f9;
                                i9 = this.y;
                                i5 = this.measuredHeight;
                                access$6003.set(f3, (((float) (i9 + i5)) - f4) - f9, ((float) (i8 + i)) - f4, ((float) (i9 + i5)) - f4);
                                canvas.drawArc(TableLayout.this.rect, 0.0f, 90.0f, false, linePaint);
                            }
                        }
                    }
                    f7 = ((float) this.y) - f4;
                    f = f7;
                    if (obj != null) {
                    }
                    f7 = ((float) (this.y + this.measuredHeight)) - f4;
                    f6 = f7;
                    i = this.x;
                    i2 = this.measuredWidth;
                    canvas.drawLine(((float) (i + i2)) - f4, f, ((float) (i + i2)) - f4, f6, linePaint);
                    i = this.x;
                    if (i == 0) {
                    }
                    f7 = ((float) this.x) - f4;
                    f5 = f7;
                    if (obj != null) {
                    }
                    f7 = ((float) (this.x + this.measuredWidth)) - f4;
                    f2 = f7;
                    i = this.y;
                    i2 = this.measuredHeight;
                    canvas.drawLine(f5, ((float) (i + i2)) - f4, f2, ((float) (i + i2)) - f4, linePaint);
                    access$6002 = TableLayout.this.rect;
                    i2 = this.x;
                    f5 = ((float) i2) + f4;
                    i4 = this.y;
                    f6 = (float) (dp * 2);
                    access$6002.set(f5, ((float) i4) + f4, (((float) i2) + f4) + f6, (((float) i4) + f4) + f6);
                    canvas.drawArc(TableLayout.this.rect, -180.0f, 90.0f, false, linePaint);
                    access$6002 = TableLayout.this.rect;
                    i2 = this.x;
                    i5 = this.measuredWidth;
                    f2 = (float) (dp * 2);
                    f = (((float) (i2 + i5)) - f4) - f2;
                    i6 = this.y;
                    access$6002.set(f, ((float) i6) + f4, ((float) (i2 + i5)) - f4, (((float) i6) + f4) + f2);
                    canvas.drawArc(TableLayout.this.rect, 0.0f, -90.0f, false, linePaint);
                    access$6002 = TableLayout.this.rect;
                    i2 = this.x;
                    f5 = ((float) i2) + f4;
                    i4 = this.y;
                    i7 = this.measuredHeight;
                    f8 = (float) (dp * 2);
                    access$6002.set(f5, (((float) (i4 + i7)) - f4) - f8, (((float) i2) + f4) + f8, ((float) (i4 + i7)) - f4);
                    canvas.drawArc(TableLayout.this.rect, 180.0f, -90.0f, false, linePaint);
                    access$6003 = TableLayout.this.rect;
                    i8 = this.x;
                    i = this.measuredWidth;
                    f9 = (float) (dp * 2);
                    f3 = (((float) (i8 + i)) - f4) - f9;
                    i9 = this.y;
                    i5 = this.measuredHeight;
                    access$6003.set(f3, (((float) (i9 + i5)) - f4) - f9, ((float) (i8 + i)) - f4, ((float) (i9 + i5)) - f4);
                    canvas.drawArc(TableLayout.this.rect, 0.0f, 90.0f, false, linePaint);
                }
            }
        }

        public void setSelectionIndex(int i) {
            this.selectionIndex = i;
        }

        public int getRow() {
            return this.rowspan + 10;
        }
    }

    static final class Interval {
        public final int max;
        public final int min;

        public Interval(int i, int i2) {
            this.min = i;
            this.max = i2;
        }

        /* Access modifiers changed, original: 0000 */
        public int size() {
            return this.max - this.min;
        }

        /* Access modifiers changed, original: 0000 */
        public Interval inverse() {
            return new Interval(this.max, this.min);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || Interval.class != obj.getClass()) {
                return false;
            }
            Interval interval = (Interval) obj;
            return this.max == interval.max && this.min == interval.min;
        }

        public int hashCode() {
            return (this.min * 31) + this.max;
        }
    }

    public static class LayoutParams extends MarginLayoutParams {
        private static final int DEFAULT_HEIGHT = -2;
        private static final int DEFAULT_MARGIN = Integer.MIN_VALUE;
        private static final Interval DEFAULT_SPAN = new Interval(Integer.MIN_VALUE, -NUM);
        private static final int DEFAULT_SPAN_SIZE = DEFAULT_SPAN.size();
        private static final int DEFAULT_WIDTH = -2;
        public Spec columnSpec;
        public Spec rowSpec;

        private LayoutParams(int i, int i2, int i3, int i4, int i5, int i6, Spec spec, Spec spec2) {
            super(i, i2);
            Spec spec3 = Spec.UNDEFINED;
            this.rowSpec = spec3;
            this.columnSpec = spec3;
            setMargins(i3, i4, i5, i6);
            this.rowSpec = spec;
            this.columnSpec = spec2;
        }

        public LayoutParams(Spec spec, Spec spec2) {
            this(-2, -2, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, spec, spec2);
        }

        public LayoutParams() {
            Spec spec = Spec.UNDEFINED;
            this(spec, spec);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            Spec spec = Spec.UNDEFINED;
            this.rowSpec = spec;
            this.columnSpec = spec;
        }

        public LayoutParams(MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
            Spec spec = Spec.UNDEFINED;
            this.rowSpec = spec;
            this.columnSpec = spec;
        }

        public LayoutParams(LayoutParams layoutParams) {
            super(layoutParams);
            Spec spec = Spec.UNDEFINED;
            this.rowSpec = spec;
            this.columnSpec = spec;
            this.rowSpec = layoutParams.rowSpec;
            this.columnSpec = layoutParams.columnSpec;
        }

        public void setGravity(int i) {
            this.rowSpec = this.rowSpec.copyWriteAlignment(TableLayout.getAlignment(i, false));
            this.columnSpec = this.columnSpec.copyWriteAlignment(TableLayout.getAlignment(i, true));
        }

        /* Access modifiers changed, original: final */
        public final void setRowSpecSpan(Interval interval) {
            this.rowSpec = this.rowSpec.copyWriteSpan(interval);
        }

        /* Access modifiers changed, original: final */
        public final void setColumnSpecSpan(Interval interval) {
            this.columnSpec = this.columnSpec.copyWriteSpan(interval);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || LayoutParams.class != obj.getClass()) {
                return false;
            }
            LayoutParams layoutParams = (LayoutParams) obj;
            return this.columnSpec.equals(layoutParams.columnSpec) && this.rowSpec.equals(layoutParams.rowSpec);
        }

        public int hashCode() {
            return (this.rowSpec.hashCode() * 31) + this.columnSpec.hashCode();
        }
    }

    static final class MutableInt {
        public int value;

        public MutableInt() {
            reset();
        }

        public MutableInt(int i) {
            this.value = i;
        }

        public void reset() {
            this.value = Integer.MIN_VALUE;
        }
    }

    static final class PackedMap<K, V> {
        public final int[] index;
        public final K[] keys;
        public final V[] values;

        /* synthetic */ PackedMap(Object[] objArr, Object[] objArr2, AnonymousClass1 anonymousClass1) {
            this(objArr, objArr2);
        }

        private PackedMap(K[] kArr, V[] vArr) {
            this.index = createIndex(kArr);
            this.keys = compact(kArr, this.index);
            this.values = compact(vArr, this.index);
        }

        public V getValue(int i) {
            return this.values[this.index[i]];
        }

        private static <K> int[] createIndex(K[] kArr) {
            int length = kArr.length;
            int[] iArr = new int[length];
            HashMap hashMap = new HashMap();
            for (int i = 0; i < length; i++) {
                Object obj = kArr[i];
                Integer num = (Integer) hashMap.get(obj);
                if (num == null) {
                    num = Integer.valueOf(hashMap.size());
                    hashMap.put(obj, num);
                }
                iArr[i] = num.intValue();
            }
            return iArr;
        }

        private static <K> K[] compact(K[] kArr, int[] iArr) {
            int length = kArr.length;
            Object[] objArr = (Object[]) Array.newInstance(kArr.getClass().getComponentType(), TableLayout.max2(iArr, -1) + 1);
            for (int i = 0; i < length; i++) {
                objArr[iArr[i]] = kArr[i];
            }
            return objArr;
        }
    }

    public static class Spec {
        static final float DEFAULT_WEIGHT = 0.0f;
        static final Spec UNDEFINED = TableLayout.spec(Integer.MIN_VALUE);
        final Alignment alignment;
        final Interval span;
        final boolean startDefined;
        float weight;

        /* synthetic */ Spec(boolean z, int i, int i2, Alignment alignment, float f, AnonymousClass1 anonymousClass1) {
            this(z, i, i2, alignment, f);
        }

        private Spec(boolean z, Interval interval, Alignment alignment, float f) {
            this.startDefined = z;
            this.span = interval;
            this.alignment = alignment;
            this.weight = f;
        }

        private Spec(boolean z, int i, int i2, Alignment alignment, float f) {
            this(z, new Interval(i, i2 + i), alignment, f);
        }

        private Alignment getAbsoluteAlignment(boolean z) {
            Alignment alignment = this.alignment;
            if (alignment != TableLayout.UNDEFINED_ALIGNMENT) {
                return alignment;
            }
            if (this.weight != 0.0f) {
                return TableLayout.FILL;
            }
            return z ? TableLayout.START : TableLayout.BASELINE;
        }

        /* Access modifiers changed, original: final */
        public final Spec copyWriteSpan(Interval interval) {
            return new Spec(this.startDefined, interval, this.alignment, this.weight);
        }

        /* Access modifiers changed, original: final */
        public final Spec copyWriteAlignment(Alignment alignment) {
            return new Spec(this.startDefined, this.span, alignment, this.weight);
        }

        /* Access modifiers changed, original: final */
        public final int getFlexibility() {
            return (this.alignment == TableLayout.UNDEFINED_ALIGNMENT && this.weight == 0.0f) ? 0 : 2;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || Spec.class != obj.getClass()) {
                return false;
            }
            Spec spec = (Spec) obj;
            return this.alignment.equals(spec.alignment) && this.span.equals(spec.span);
        }

        public int hashCode() {
            return (this.span.hashCode() * 31) + this.alignment.hashCode();
        }
    }

    public interface TableLayoutDelegate {
        DrawingText createTextLayout(TL_pageTableCell tL_pageTableCell, int i);

        Paint getHalfLinePaint();

        Paint getHeaderPaint();

        Paint getLinePaint();

        Paint getStripPaint();

        void onLayoutChild(DrawingText drawingText, int i, int i2);
    }

    static boolean canStretch(int i) {
        return (i & 2) != 0;
    }

    public void addChild(int i, int i2, int i3, int i4) {
        int i5 = i;
        int i6 = i2;
        Child child = new Child(this.childrens.size());
        LayoutParams layoutParams = new LayoutParams();
        layoutParams.rowSpec = new Spec(false, new Interval(i6, i6 + i4), FILL, 0.0f, null);
        layoutParams.columnSpec = new Spec(false, new Interval(i5, i5 + i3), FILL, 0.0f, null);
        child.layoutParams = layoutParams;
        child.rowspan = i6;
        this.childrens.add(child);
        invalidateStructure();
    }

    public void addChild(TL_pageTableCell tL_pageTableCell, int i, int i2, int i3) {
        TL_pageTableCell tL_pageTableCell2 = tL_pageTableCell;
        int i4 = i;
        int i5 = i2;
        int i6 = i3 == 0 ? 1 : i3;
        Child child = new Child(this.childrens.size());
        child.cell = tL_pageTableCell2;
        LayoutParams layoutParams = new LayoutParams();
        int i7 = tL_pageTableCell2.rowspan;
        if (i7 == 0) {
            i7 = 1;
        }
        layoutParams.rowSpec = new Spec(false, new Interval(i5, i7 + i5), FILL, 0.0f, null);
        layoutParams.columnSpec = new Spec(false, new Interval(i4, i6 + i4), FILL, 1.0f, null);
        child.layoutParams = layoutParams;
        child.rowspan = i5;
        this.childrens.add(child);
        int i8 = tL_pageTableCell2.rowspan;
        if (i8 > 1) {
            this.rowSpans.add(new Point((float) i5, (float) (i8 + i5)));
        }
        invalidateStructure();
    }

    public void setDrawLines(boolean z) {
        this.drawLines = z;
    }

    public void setStriped(boolean z) {
        this.isStriped = z;
    }

    public void setRtl(boolean z) {
        this.isRtl = z;
    }

    public void removeAllChildrens() {
        this.childrens.clear();
        this.rowSpans.clear();
        invalidateStructure();
    }

    public int getChildCount() {
        return this.childrens.size();
    }

    public Child getChildAt(int i) {
        return (i < 0 || i >= this.childrens.size()) ? null : (Child) this.childrens.get(i);
    }

    public TableLayout(Context context, TableLayoutDelegate tableLayoutDelegate, ArticleTextSelectionHelper articleTextSelectionHelper) {
        super(context);
        this.textSelectionHelper = articleTextSelectionHelper;
        setRowCount(Integer.MIN_VALUE);
        setColumnCount(Integer.MIN_VALUE);
        setOrientation(0);
        setUseDefaultMargins(false);
        setAlignmentMode(1);
        setRowOrderPreserved(true);
        setColumnOrderPreserved(true);
        this.delegate = tableLayoutDelegate;
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public void setOrientation(int i) {
        if (this.mOrientation != i) {
            this.mOrientation = i;
            invalidateStructure();
            requestLayout();
        }
    }

    public int getRowCount() {
        return this.mVerticalAxis.getCount();
    }

    public void setRowCount(int i) {
        this.mVerticalAxis.setCount(i);
        invalidateStructure();
        requestLayout();
    }

    public int getColumnCount() {
        return this.mHorizontalAxis.getCount();
    }

    public void setColumnCount(int i) {
        this.mHorizontalAxis.setCount(i);
        invalidateStructure();
        requestLayout();
    }

    public boolean getUseDefaultMargins() {
        return this.mUseDefaultMargins;
    }

    public void setUseDefaultMargins(boolean z) {
        this.mUseDefaultMargins = z;
        requestLayout();
    }

    public int getAlignmentMode() {
        return this.mAlignmentMode;
    }

    public void setAlignmentMode(int i) {
        this.mAlignmentMode = i;
        requestLayout();
    }

    public boolean isRowOrderPreserved() {
        return this.mVerticalAxis.isOrderPreserved();
    }

    public void setRowOrderPreserved(boolean z) {
        this.mVerticalAxis.setOrderPreserved(z);
        invalidateStructure();
        requestLayout();
    }

    public boolean isColumnOrderPreserved() {
        return this.mHorizontalAxis.isOrderPreserved();
    }

    public void setColumnOrderPreserved(boolean z) {
        this.mHorizontalAxis.setOrderPreserved(z);
        invalidateStructure();
        requestLayout();
    }

    static int max2(int[] iArr, int i) {
        for (int max : iArr) {
            i = Math.max(i, max);
        }
        return i;
    }

    static <T> T[] append(T[] tArr, T[] tArr2) {
        Object[] objArr = (Object[]) Array.newInstance(tArr.getClass().getComponentType(), tArr.length + tArr2.length);
        System.arraycopy(tArr, 0, objArr, 0, tArr.length);
        System.arraycopy(tArr2, 0, objArr, tArr.length, tArr2.length);
        return objArr;
    }

    static Alignment getAlignment(int i, boolean z) {
        i = (i & (z ? 7 : 112)) >> (z ? 0 : 4);
        if (i == 1) {
            return CENTER;
        }
        if (i == 3) {
            return z ? LEFT : TOP;
        } else if (i == 5) {
            return z ? RIGHT : BOTTOM;
        } else if (i == 7) {
            return FILL;
        } else {
            if (i == 8388611) {
                return START;
            }
            if (i != 8388613) {
                return UNDEFINED_ALIGNMENT;
            }
            return END;
        }
    }

    private int getDefaultMargin(Child child, boolean z, boolean z2) {
        return this.mDefaultGap / 2;
    }

    private int getDefaultMargin(Child child, boolean z, boolean z2, boolean z3) {
        return getDefaultMargin(child, z2, z3);
    }

    private int getDefaultMargin(Child child, LayoutParams layoutParams, boolean z, boolean z2) {
        boolean z3 = false;
        if (!this.mUseDefaultMargins) {
            return 0;
        }
        Spec spec = z ? layoutParams.columnSpec : layoutParams.rowSpec;
        Axis axis = z ? this.mHorizontalAxis : this.mVerticalAxis;
        Interval interval = spec.span;
        boolean z4 = z && this.isRtl;
        if ((z4 != z2 ? 1 : null) == null ? interval.max != axis.getCount() : interval.min != 0) {
            z3 = true;
        }
        return getDefaultMargin(child, z3, z, z2);
    }

    /* Access modifiers changed, original: 0000 */
    public int getMargin1(Child child, boolean z, boolean z2) {
        LayoutParams layoutParams = child.getLayoutParams();
        int i = z ? z2 ? layoutParams.leftMargin : layoutParams.rightMargin : z2 ? layoutParams.topMargin : layoutParams.bottomMargin;
        return i == Integer.MIN_VALUE ? getDefaultMargin(child, layoutParams, z, z2) : i;
    }

    private int getMargin(Child child, boolean z, boolean z2) {
        if (this.mAlignmentMode == 1) {
            return getMargin1(child, z, z2);
        }
        Axis axis = z ? this.mHorizontalAxis : this.mVerticalAxis;
        int[] leadingMargins = z2 ? axis.getLeadingMargins() : axis.getTrailingMargins();
        LayoutParams layoutParams = child.getLayoutParams();
        Interval interval = (z ? layoutParams.columnSpec : layoutParams.rowSpec).span;
        return leadingMargins[z2 ? interval.min : interval.max];
    }

    private int getTotalMargin(Child child, boolean z) {
        return getMargin(child, z, true) + getMargin(child, z, false);
    }

    private static boolean fits(int[] iArr, int i, int i2, int i3) {
        if (i3 > iArr.length) {
            return false;
        }
        while (i2 < i3) {
            if (iArr[i2] > i) {
                return false;
            }
            i2++;
        }
        return true;
    }

    private static void procrusteanFill(int[] iArr, int i, int i2, int i3) {
        int length = iArr.length;
        Arrays.fill(iArr, Math.min(i, length), Math.min(i2, length), i3);
    }

    private static void setCellGroup(LayoutParams layoutParams, int i, int i2, int i3, int i4) {
        layoutParams.setRowSpecSpan(new Interval(i, i2 + i));
        layoutParams.setColumnSpecSpan(new Interval(i3, i4 + i3));
    }

    private static int clip(Interval interval, boolean z, int i) {
        int size = interval.size();
        if (i == 0) {
            return size;
        }
        return Math.min(size, i - (z ? Math.min(interval.min, i) : 0));
    }

    private void validateLayoutParams() {
        Object obj = this.mOrientation == 0 ? 1 : null;
        int i = (obj != null ? this.mHorizontalAxis : this.mVerticalAxis).definedCount;
        if (i == Integer.MIN_VALUE) {
            i = 0;
        }
        int[] iArr = new int[i];
        int childCount = getChildCount();
        int i2 = 0;
        int i3 = 0;
        for (int i4 = 0; i4 < childCount; i4++) {
            LayoutParams layoutParams = getChildAt(i4).getLayoutParams();
            Spec spec = obj != null ? layoutParams.rowSpec : layoutParams.columnSpec;
            Interval interval = spec.span;
            boolean z = spec.startDefined;
            int size = interval.size();
            if (z) {
                i2 = interval.min;
            }
            Spec spec2 = obj != null ? layoutParams.columnSpec : layoutParams.rowSpec;
            Interval interval2 = spec2.span;
            boolean z2 = spec2.startDefined;
            int clip = clip(interval2, z2, i);
            if (z2) {
                i3 = interval2.min;
            }
            if (i != 0) {
                if (!z || !z2) {
                    while (true) {
                        int i5 = i3 + clip;
                        if (fits(iArr, i2, i3, i5)) {
                            break;
                        } else if (z2) {
                            i2++;
                        } else if (i5 <= i) {
                            i3++;
                        } else {
                            i2++;
                            i3 = 0;
                        }
                    }
                }
                procrusteanFill(iArr, i3, i3 + clip, i2 + size);
            }
            if (obj != null) {
                setCellGroup(layoutParams, i2, size, i3, clip);
            } else {
                setCellGroup(layoutParams, i3, clip, i2, size);
            }
            i3 += clip;
        }
    }

    private void invalidateStructure() {
        this.mLastLayoutParamsHashCode = 0;
        this.mHorizontalAxis.invalidateStructure();
        this.mVerticalAxis.invalidateStructure();
        invalidateValues();
    }

    private void invalidateValues() {
        Axis axis = this.mHorizontalAxis;
        if (axis != null && this.mVerticalAxis != null) {
            axis.invalidateValues();
            this.mVerticalAxis.invalidateValues();
        }
    }

    private static void handleInvalidParams(String str) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str);
        stringBuilder.append(". ");
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    private void checkLayoutParams(LayoutParams layoutParams, boolean z) {
        StringBuilder stringBuilder;
        String str = z ? "column" : "row";
        Interval interval = (z ? layoutParams.columnSpec : layoutParams.rowSpec).span;
        int i = interval.min;
        if (i != Integer.MIN_VALUE && i < 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append(" indices must be positive");
            handleInvalidParams(stringBuilder.toString());
        }
        int i2 = (z ? this.mHorizontalAxis : this.mVerticalAxis).definedCount;
        if (i2 != Integer.MIN_VALUE) {
            String str2 = " count";
            if (interval.max > i2) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(" indices (start + span) mustn't exceed the ");
                stringBuilder.append(str);
                stringBuilder.append(str2);
                handleInvalidParams(stringBuilder.toString());
            }
            if (interval.size() > i2) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(str);
                stringBuilder2.append(" span mustn't exceed the ");
                stringBuilder2.append(str);
                stringBuilder2.append(str2);
                handleInvalidParams(stringBuilder2.toString());
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).draw(canvas);
        }
    }

    private int computeLayoutParamsHashCode() {
        int i = 1;
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            i = (i * 31) + getChildAt(i2).getLayoutParams().hashCode();
        }
        return i;
    }

    private void consistencyCheck() {
        int i = this.mLastLayoutParamsHashCode;
        if (i == 0) {
            validateLayoutParams();
            this.mLastLayoutParamsHashCode = computeLayoutParamsHashCode();
        } else if (i != computeLayoutParamsHashCode()) {
            invalidateStructure();
            consistencyCheck();
        }
    }

    private void measureChildWithMargins2(Child child, int i, int i2, int i3, int i4, boolean z) {
        child.measure(getTotalMargin(child, true) + i3, getTotalMargin(child, false) + i4, z);
    }

    private void measureChildrenWithMargins(int i, int i2, boolean z) {
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            Child childAt = getChildAt(i3);
            LayoutParams layoutParams = childAt.getLayoutParams();
            if (z) {
                int size = MeasureSpec.getSize(i);
                childAt.setTextLayout(this.delegate.createTextLayout(childAt.cell, this.colCount == 2 ? ((int) (((float) size) / 2.0f)) - (this.itemPaddingLeft * 4) : (int) (((float) size) / 1.5f)));
                if (childAt.textLayout != null) {
                    layoutParams.width = childAt.textWidth + (this.itemPaddingLeft * 2);
                    layoutParams.height = childAt.textHeight + (this.itemPaddingTop * 2);
                } else {
                    layoutParams.width = 0;
                    layoutParams.height = 0;
                }
                measureChildWithMargins2(childAt, i, i2, layoutParams.width, layoutParams.height, true);
            } else {
                boolean z2 = this.mOrientation == 0;
                Spec spec = z2 ? layoutParams.columnSpec : layoutParams.rowSpec;
                if (spec.getAbsoluteAlignment(z2) == FILL) {
                    Interval interval = spec.span;
                    int[] locations = (z2 ? this.mHorizontalAxis : this.mVerticalAxis).getLocations();
                    int totalMargin = (locations[interval.max] - locations[interval.min]) - getTotalMargin(childAt, z2);
                    if (z2) {
                        measureChildWithMargins2(childAt, i, i2, totalMargin, layoutParams.height, false);
                    } else {
                        measureChildWithMargins2(childAt, i, i2, layoutParams.width, totalMargin, false);
                    }
                }
            }
        }
    }

    static int adjust(int i, int i2) {
        return MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i2 + i), MeasureSpec.getMode(i));
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        int i3;
        int measurement;
        int measurement2;
        int gravityOffset;
        int i4;
        Child child;
        int i5 = i;
        int i6 = i2;
        consistencyCheck();
        invalidateValues();
        boolean z = false;
        this.colCount = 0;
        int childCount = getChildCount();
        for (i3 = 0; i3 < childCount; i3++) {
            this.colCount = Math.max(this.colCount, getChildAt(i3).layoutParams.columnSpec.span.max);
        }
        boolean z2 = true;
        measureChildrenWithMargins(i5, i6, true);
        if (this.mOrientation == 0) {
            childCount = this.mHorizontalAxis.getMeasure(i5);
            measureChildrenWithMargins(i5, i6, false);
            int i7 = childCount;
            childCount = this.mVerticalAxis.getMeasure(i6);
            i6 = i7;
        } else {
            childCount = this.mVerticalAxis.getMeasure(i6);
            measureChildrenWithMargins(i5, i6, false);
            i6 = this.mHorizontalAxis.getMeasure(i5);
        }
        i5 = Math.max(i6, MeasureSpec.getSize(i));
        int max = Math.max(childCount, getSuggestedMinimumHeight());
        setMeasuredDimension(i5, max);
        this.mHorizontalAxis.layout(i5);
        this.mVerticalAxis.layout(max);
        int[] locations = this.mHorizontalAxis.getLocations();
        int[] locations2 = this.mVerticalAxis.getLocations();
        this.cellsToFixHeight.clear();
        int i8 = locations[locations.length - 1];
        int childCount2 = getChildCount();
        int i9 = 0;
        while (i9 < childCount2) {
            Child childAt = getChildAt(i9);
            LayoutParams layoutParams = childAt.getLayoutParams();
            Spec spec = layoutParams.columnSpec;
            Spec spec2 = layoutParams.rowSpec;
            Interval interval = spec.span;
            Interval interval2 = spec2.span;
            int i10 = locations[interval.min];
            int i11 = locations2[interval2.min];
            int i12 = locations[interval.max] - i10;
            int i13 = locations2[interval2.max] - i11;
            measurement = getMeasurement(childAt, z2);
            measurement2 = getMeasurement(childAt, z);
            Alignment access$1300 = spec.getAbsoluteAlignment(z2);
            Alignment access$13002 = spec2.getAbsoluteAlignment(z);
            Bounds bounds = (Bounds) this.mHorizontalAxis.getGroupBounds().getValue(i9);
            Bounds bounds2 = (Bounds) this.mVerticalAxis.getGroupBounds().getValue(i9);
            gravityOffset = access$1300.getGravityOffset(childAt, i12 - bounds.size(z2));
            Bounds bounds3 = bounds2;
            int gravityOffset2 = access$13002.getGravityOffset(childAt, i13 - bounds2.size(z2));
            int margin = getMargin(childAt, z2, z2);
            int margin2 = getMargin(childAt, false, z2);
            int margin3 = getMargin(childAt, z2, false);
            int i14 = margin + margin3;
            int margin4 = margin2 + getMargin(childAt, false, false);
            Bounds bounds4 = bounds3;
            Alignment alignment = access$13002;
            Child child2 = childAt;
            Alignment alignment2 = access$1300;
            int i15 = measurement2;
            i4 = max;
            max = measurement;
            int offset = bounds.getOffset(this, child2, access$1300, measurement + i14, true);
            access$1300 = alignment;
            i5 = bounds4.getOffset(this, child2, access$1300, i15 + margin4, false);
            i6 = alignment2.getSizeInCell(childAt, max, i12 - i14);
            childCount = access$1300.getSizeInCell(childAt, i15, i13 - margin4);
            i10 = (i10 + gravityOffset) + offset;
            i3 = !this.isRtl ? margin + i10 : ((i8 - i6) - margin3) - i10;
            i5 = ((i11 + gravityOffset2) + i5) + margin2;
            if (childAt.cell != null) {
                if (i6 != childAt.getMeasuredWidth() || childCount != childAt.getMeasuredHeight()) {
                    childAt.measure(i6, childCount, false);
                }
                if (!(childAt.fixedHeight == 0 || childAt.fixedHeight == childCount || childAt.layoutParams.rowSpec.span.max - childAt.layoutParams.rowSpec.span.min > 1)) {
                    Object obj;
                    measurement = this.rowSpans.size();
                    for (gravityOffset = 0; gravityOffset < measurement; gravityOffset++) {
                        Point point = (Point) this.rowSpans.get(gravityOffset);
                        if (point.x <= ((float) childAt.layoutParams.rowSpec.span.min) && point.y > ((float) childAt.layoutParams.rowSpec.span.min)) {
                            obj = 1;
                            break;
                        }
                    }
                    obj = null;
                    if (obj == null) {
                        this.cellsToFixHeight.add(childAt);
                    }
                }
            }
            childAt.layout(i3, i5, i6 + i3, childCount + i5);
            i9++;
            max = i4;
            z = false;
            z2 = true;
        }
        i4 = max;
        i6 = this.cellsToFixHeight.size();
        childCount = i4;
        i5 = 0;
        while (i5 < i6) {
            Child child3;
            Object obj2;
            child = (Child) this.cellsToFixHeight.get(i5);
            measurement2 = child.measuredHeight - child.fixedHeight;
            measurement = child.index + 1;
            gravityOffset = this.childrens.size();
            while (measurement < gravityOffset) {
                child3 = (Child) this.childrens.get(measurement);
                if (child.layoutParams.rowSpec.span.min != child3.layoutParams.rowSpec.span.min) {
                    break;
                } else if (child.fixedHeight < child3.fixedHeight) {
                    obj2 = 1;
                    break;
                } else {
                    max = child3.measuredHeight - child3.fixedHeight;
                    if (max > 0) {
                        measurement2 = Math.min(measurement2, max);
                    }
                    measurement++;
                }
            }
            obj2 = null;
            if (obj2 == null) {
                measurement = child.index - 1;
                while (measurement >= 0) {
                    Child child4 = (Child) this.childrens.get(measurement);
                    if (child.layoutParams.rowSpec.span.min != child4.layoutParams.rowSpec.span.min) {
                        break;
                    } else if (child.fixedHeight < child4.fixedHeight) {
                        obj2 = 1;
                        break;
                    } else {
                        max = child4.measuredHeight - child4.fixedHeight;
                        if (max > 0) {
                            measurement2 = Math.min(measurement2, max);
                        }
                        measurement--;
                    }
                }
            }
            if (obj2 == null) {
                child.setFixedHeight(child.fixedHeight);
                childCount -= measurement2;
                measurement = this.childrens.size();
                gravityOffset = i6;
                i6 = i5;
                for (i5 = 0; i5 < measurement; i5++) {
                    child3 = (Child) this.childrens.get(i5);
                    if (child != child3) {
                        if (child.layoutParams.rowSpec.span.min == child3.layoutParams.rowSpec.span.min) {
                            if (child3.fixedHeight != child3.measuredHeight) {
                                this.cellsToFixHeight.remove(child3);
                                if (child3.index < child.index) {
                                    i6--;
                                }
                                gravityOffset--;
                            }
                            child3.measuredHeight = child3.measuredHeight - measurement2;
                            child3.measure(child3.measuredWidth, child3.measuredHeight, true);
                        } else if (child.layoutParams.rowSpec.span.min < child3.layoutParams.rowSpec.span.min) {
                            child3.y -= measurement2;
                        }
                    }
                }
                i5 = i6;
                i6 = gravityOffset;
            }
            i5++;
        }
        i5 = getChildCount();
        for (i6 = 0; i6 < i5; i6++) {
            child = getChildAt(i6);
            this.delegate.onLayoutChild(child.textLayout, child.getTextX(), child.getTextY());
        }
        setMeasuredDimension(i8, childCount);
    }

    private int getMeasurement(Child child, boolean z) {
        return z ? child.getMeasuredWidth() : child.getMeasuredHeight();
    }

    /* Access modifiers changed, original: final */
    public final int getMeasurementIncludingMargin(Child child, boolean z) {
        return getMeasurement(child, z) + getTotalMargin(child, z);
    }

    public void requestLayout() {
        super.requestLayout();
        invalidateValues();
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        consistencyCheck();
    }

    public static Spec spec(int i, int i2, Alignment alignment, float f) {
        return new Spec(i != Integer.MIN_VALUE, i, i2, alignment, f, null);
    }

    public static Spec spec(int i, Alignment alignment, float f) {
        return spec(i, 1, alignment, f);
    }

    public static Spec spec(int i, int i2, float f) {
        return spec(i, i2, UNDEFINED_ALIGNMENT, f);
    }

    public static Spec spec(int i, float f) {
        return spec(i, 1, f);
    }

    public static Spec spec(int i, int i2, Alignment alignment) {
        return spec(i, i2, alignment, 0.0f);
    }

    public static Spec spec(int i, Alignment alignment) {
        return spec(i, 1, alignment);
    }

    public static Spec spec(int i, int i2) {
        return spec(i, i2, UNDEFINED_ALIGNMENT);
    }

    public static Spec spec(int i) {
        return spec(i, 1);
    }

    static {
        Alignment alignment = LEADING;
        TOP = alignment;
        Alignment alignment2 = TRAILING;
        BOTTOM = alignment2;
        START = alignment;
        END = alignment2;
    }

    private static Alignment createSwitchingAlignment(final Alignment alignment) {
        return new Alignment() {
            /* Access modifiers changed, original: 0000 */
            public int getGravityOffset(Child child, int i) {
                return alignment.getGravityOffset(child, i);
            }

            public int getAlignmentValue(Child child, int i) {
                return alignment.getAlignmentValue(child, i);
            }
        };
    }
}
