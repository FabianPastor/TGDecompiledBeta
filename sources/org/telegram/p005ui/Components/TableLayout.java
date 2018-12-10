package org.telegram.p005ui.Components;

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
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.p005ui.ArticleViewer.DrawingText;
import org.telegram.tgnet.TLRPC.TL_pageTableCell;

/* renamed from: org.telegram.ui.Components.TableLayout */
public class TableLayout extends View {
    public static final int ALIGN_BOUNDS = 0;
    public static final int ALIGN_MARGINS = 1;
    public static final Alignment BASELINE = new CLASSNAME();
    public static final Alignment BOTTOM = TRAILING;
    private static final int CAN_STRETCH = 2;
    public static final Alignment CENTER = new CLASSNAME();
    private static final int DEFAULT_ALIGNMENT_MODE = 1;
    private static final int DEFAULT_COUNT = Integer.MIN_VALUE;
    private static final boolean DEFAULT_ORDER_PRESERVED = true;
    private static final int DEFAULT_ORIENTATION = 0;
    private static final boolean DEFAULT_USE_DEFAULT_MARGINS = false;
    public static final Alignment END = TRAILING;
    public static final Alignment FILL = new CLASSNAME();
    public static final int HORIZONTAL = 0;
    private static final int INFLEXIBLE = 0;
    private static final Alignment LEADING = new CLASSNAME();
    public static final Alignment LEFT = TableLayout.createSwitchingAlignment(START);
    static final int MAX_SIZE = 100000;
    public static final Alignment RIGHT = TableLayout.createSwitchingAlignment(END);
    public static final Alignment START = LEADING;
    public static final Alignment TOP = LEADING;
    private static final Alignment TRAILING = new CLASSNAME();
    public static final int UNDEFINED = Integer.MIN_VALUE;
    static final Alignment UNDEFINED_ALIGNMENT = new CLASSNAME();
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
    private int itemPaddingLeft = AndroidUtilities.m9dp(8.0f);
    private int itemPaddingTop = AndroidUtilities.m9dp(7.0f);
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

    /* renamed from: org.telegram.ui.Components.TableLayout$Alignment */
    public static abstract class Alignment {
        abstract int getAlignmentValue(Child child, int i);

        abstract int getGravityOffset(Child child, int i);

        Alignment() {
        }

        int getSizeInCell(Child view, int viewSize, int cellSize) {
            return viewSize;
        }

        Bounds getBounds() {
            return new Bounds();
        }
    }

    /* renamed from: org.telegram.ui.Components.TableLayout$Arc */
    static final class Arc {
        public final Interval span;
        public boolean valid = true;
        public final MutableInt value;

        public Arc(Interval span, MutableInt value) {
            this.span = span;
            this.value = value;
        }
    }

    /* renamed from: org.telegram.ui.Components.TableLayout$Assoc */
    static final class Assoc<K, V> extends ArrayList<Pair<K, V>> {
        private final Class<K> keyType;
        private final Class<V> valueType;

        private Assoc(Class<K> keyType, Class<V> valueType) {
            this.keyType = keyType;
            this.valueType = valueType;
        }

        /* renamed from: of */
        public static <K, V> Assoc<K, V> m25of(Class<K> keyType, Class<V> valueType) {
            return new Assoc(keyType, valueType);
        }

        public void put(K key, V value) {
            add(Pair.create(key, value));
        }

        public PackedMap<K, V> pack() {
            int N = size();
            Object[] keys = (Object[]) ((Object[]) Array.newInstance(this.keyType, N));
            Object[] values = (Object[]) ((Object[]) Array.newInstance(this.valueType, N));
            for (int i = 0; i < N; i++) {
                keys[i] = ((Pair) get(i)).first;
                values[i] = ((Pair) get(i)).second;
            }
            return new PackedMap(keys, values, null);
        }
    }

    /* renamed from: org.telegram.ui.Components.TableLayout$Axis */
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

        /* synthetic */ Axis(TableLayout x0, boolean x1, CLASSNAME x2) {
            this(x1);
        }

        private Axis(boolean horizontal) {
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
            this.horizontal = horizontal;
        }

        private int calculateMaxIndex() {
            int result = -1;
            int N = TableLayout.this.getChildCount();
            for (int i = 0; i < N; i++) {
                LayoutParams params = TableLayout.this.getChildAt(i).getLayoutParams();
                Interval span = (this.horizontal ? params.columnSpec : params.rowSpec).span;
                result = Math.max(Math.max(Math.max(result, span.min), span.max), span.size());
            }
            return result == -1 ? Integer.MIN_VALUE : result;
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

        public void setCount(int count) {
            if (count != Integer.MIN_VALUE && count < getMaxIndex()) {
                TableLayout.handleInvalidParams((this.horizontal ? "column" : "row") + "Count must be greater than or equal to the maximum of all grid indices (and spans) defined in the LayoutParams of each child");
            }
            this.definedCount = count;
        }

        public boolean isOrderPreserved() {
            return this.orderPreserved;
        }

        public void setOrderPreserved(boolean orderPreserved) {
            this.orderPreserved = orderPreserved;
            invalidateStructure();
        }

        private PackedMap<Spec, Bounds> createGroupBounds() {
            Assoc<Spec, Bounds> assoc = Assoc.m25of(Spec.class, Bounds.class);
            int N = TableLayout.this.getChildCount();
            for (int i = 0; i < N; i++) {
                LayoutParams lp = TableLayout.this.getChildAt(i).getLayoutParams();
                Spec spec = this.horizontal ? lp.columnSpec : lp.rowSpec;
                assoc.put(spec, spec.getAbsoluteAlignment(this.horizontal).getBounds());
            }
            return assoc.pack();
        }

        private void computeGroupBounds() {
            Bounds[] values = this.groupBounds.values;
            for (Bounds reset : values) {
                reset.reset();
            }
            int i = 0;
            int N = TableLayout.this.getChildCount();
            while (i < N) {
                Child c = TableLayout.this.getChildAt(i);
                LayoutParams lp = c.getLayoutParams();
                Spec spec = this.horizontal ? lp.columnSpec : lp.rowSpec;
                ((Bounds) this.groupBounds.getValue(i)).include(TableLayout.this, c, spec, this, TableLayout.this.getMeasurementIncludingMargin(c, this.horizontal) + (spec.weight == 0.0f ? 0 : this.deltas[i]));
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

        private PackedMap<Interval, MutableInt> createLinks(boolean min) {
            Assoc<Interval, MutableInt> result = Assoc.m25of(Interval.class, MutableInt.class);
            Spec[] keys = getGroupBounds().keys;
            int N = keys.length;
            for (int i = 0; i < N; i++) {
                result.put(min ? keys[i].span : keys[i].span.inverse(), new MutableInt());
            }
            return result.pack();
        }

        private void computeLinks(PackedMap<Interval, MutableInt> links, boolean min) {
            int i;
            MutableInt[] spans = links.values;
            for (MutableInt reset : spans) {
                reset.reset();
            }
            Bounds[] bounds = getGroupBounds().values;
            for (i = 0; i < bounds.length; i++) {
                int size = bounds[i].size(min);
                MutableInt valueHolder = (MutableInt) links.getValue(i);
                int i2 = valueHolder.value;
                if (!min) {
                    size = -size;
                }
                valueHolder.value = Math.max(i2, size);
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

        private void include(List<Arc> arcs, Interval key, MutableInt size, boolean ignoreIfAlreadyPresent) {
            if (key.size() != 0) {
                if (ignoreIfAlreadyPresent) {
                    for (Arc arc : arcs) {
                        if (arc.span.equals(key)) {
                            return;
                        }
                    }
                }
                arcs.add(new Arc(key, size));
            }
        }

        private void include(List<Arc> arcs, Interval key, MutableInt size) {
            include(arcs, key, size, true);
        }

        Arc[][] groupArcsByFirstVertex(Arc[] arcs) {
            int i;
            Arc arc;
            int i2;
            int i3 = 0;
            int N = getCount() + 1;
            Arc[][] result = new Arc[N][];
            int[] sizes = new int[N];
            for (Arc arc2 : arcs) {
                i = arc2.span.min;
                sizes[i] = sizes[i] + 1;
            }
            for (i2 = 0; i2 < sizes.length; i2++) {
                result[i2] = new Arc[sizes[i2]];
            }
            Arrays.fill(sizes, 0);
            int length = arcs.length;
            while (i3 < length) {
                arc2 = arcs[i3];
                i2 = arc2.span.min;
                Arc[] arcArr = result[i2];
                i = sizes[i2];
                sizes[i2] = i + 1;
                arcArr[i] = arc2;
                i3++;
            }
            return result;
        }

        private Arc[] topologicalSort(final Arc[] arcs) {
            return new Object() {
                Arc[][] arcsByVertex = Axis.this.groupArcsByFirstVertex(arcs);
                int cursor = (this.result.length - 1);
                Arc[] result = new Arc[arcs.length];
                int[] visited = new int[(Axis.this.getCount() + 1)];

                void walk(int loc) {
                    switch (this.visited[loc]) {
                        case 0:
                            this.visited[loc] = 1;
                            for (Arc arc : this.arcsByVertex[loc]) {
                                walk(arc.span.max);
                                Arc[] arcArr = this.result;
                                int i = this.cursor;
                                this.cursor = i - 1;
                                arcArr[i] = arc;
                            }
                            this.visited[loc] = 2;
                            return;
                        default:
                            return;
                    }
                }

                Arc[] sort() {
                    int N = this.arcsByVertex.length;
                    for (int loc = 0; loc < N; loc++) {
                        walk(loc);
                    }
                    return this.result;
                }
            }.sort();
        }

        private Arc[] topologicalSort(List<Arc> arcs) {
            return topologicalSort((Arc[]) arcs.toArray(new Arc[arcs.size()]));
        }

        private void addComponentSizes(List<Arc> result, PackedMap<Interval, MutableInt> links) {
            for (int i = 0; i < ((Interval[]) links.keys).length; i++) {
                include(result, ((Interval[]) links.keys)[i], ((MutableInt[]) links.values)[i], false);
            }
        }

        private Arc[] createArcs() {
            List mins = new ArrayList();
            List maxs = new ArrayList();
            addComponentSizes(mins, getForwardLinks());
            addComponentSizes(maxs, getBackwardLinks());
            if (this.orderPreserved) {
                for (int i = 0; i < getCount(); i++) {
                    include(mins, new Interval(i, i + 1), new MutableInt(0));
                }
            }
            int N = getCount();
            include(mins, new Interval(0, N), this.parentMin, false);
            include(maxs, new Interval(N, 0), this.parentMax, false);
            return (Arc[]) TableLayout.append(topologicalSort(mins), topologicalSort(maxs));
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

        private boolean relax(int[] locations, Arc entry) {
            if (!entry.valid) {
                return false;
            }
            Interval span = entry.span;
            int u = span.min;
            int v = span.max;
            int candidate = locations[u] + entry.value.value;
            if (candidate <= locations[v]) {
                return false;
            }
            locations[v] = candidate;
            return true;
        }

        private void init(int[] locations) {
            Arrays.fill(locations, 0);
        }

        private boolean solve(Arc[] arcs, int[] locations) {
            return solve(arcs, locations, true);
        }

        private boolean solve(Arc[] arcs, int[] locations, boolean modifyOnError) {
            int N = getCount() + 1;
            for (int p = 0; p < arcs.length; p++) {
                int i;
                int j;
                init(locations);
                for (i = 0; i < N; i++) {
                    boolean changed = false;
                    for (Arc relax : arcs) {
                        changed |= relax(locations, relax);
                    }
                    if (!changed) {
                        return true;
                    }
                }
                if (!modifyOnError) {
                    return false;
                }
                boolean[] culprits = new boolean[arcs.length];
                for (i = 0; i < N; i++) {
                    int length = arcs.length;
                    for (j = 0; j < length; j++) {
                        culprits[j] = culprits[j] | relax(locations, arcs[j]);
                    }
                }
                for (i = 0; i < arcs.length; i++) {
                    if (culprits[i]) {
                        Arc arc = arcs[i];
                        if (arc.span.min >= arc.span.max) {
                            arc.valid = false;
                            break;
                        }
                    }
                }
            }
            return true;
        }

        private void computeMargins(boolean leading) {
            int[] margins = leading ? this.leadingMargins : this.trailingMargins;
            int N = TableLayout.this.getChildCount();
            for (int i = 0; i < N; i++) {
                Child c = TableLayout.this.getChildAt(i);
                LayoutParams lp = c.getLayoutParams();
                Interval span = (this.horizontal ? lp.columnSpec : lp.rowSpec).span;
                int index = leading ? span.min : span.max;
                margins[index] = Math.max(margins[index], TableLayout.this.getMargin1(c, this.horizontal, leading));
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

        private boolean solve(int[] a) {
            return solve(getArcs(), a);
        }

        private boolean computeHasWeights() {
            int N = TableLayout.this.getChildCount();
            for (int i = 0; i < N; i++) {
                LayoutParams lp = TableLayout.this.getChildAt(i).getLayoutParams();
                if ((this.horizontal ? lp.columnSpec : lp.rowSpec).weight != 0.0f) {
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

        private void shareOutDelta(int totalDelta, float totalWeight) {
            Arrays.fill(this.deltas, 0);
            int N = TableLayout.this.getChildCount();
            for (int i = 0; i < N; i++) {
                LayoutParams lp = TableLayout.this.getChildAt(i).getLayoutParams();
                float weight = (this.horizontal ? lp.columnSpec : lp.rowSpec).weight;
                if (weight != 0.0f) {
                    int delta = Math.round((((float) totalDelta) * weight) / totalWeight);
                    this.deltas[i] = delta;
                    totalDelta -= delta;
                    totalWeight -= weight;
                }
            }
        }

        private void solveAndDistributeSpace(int[] a) {
            Arrays.fill(getDeltas(), 0);
            solve(a);
            int deltaMax = (this.parentMin.value * TableLayout.this.getChildCount()) + 1;
            if (deltaMax >= 2) {
                int deltaMin = 0;
                float totalWeight = calculateTotalWeight();
                int validDelta = -1;
                boolean validSolution = true;
                while (deltaMin < deltaMax) {
                    int delta = (int) ((((long) deltaMin) + ((long) deltaMax)) / 2);
                    invalidateValues();
                    shareOutDelta(delta, totalWeight);
                    validSolution = solve(getArcs(), a, false);
                    if (validSolution) {
                        validDelta = delta;
                        deltaMin = delta + 1;
                    } else {
                        deltaMax = delta;
                    }
                }
                if (validDelta > 0 && !validSolution) {
                    invalidateValues();
                    shareOutDelta(validDelta, totalWeight);
                    solve(a);
                }
            }
        }

        private float calculateTotalWeight() {
            float totalWeight = 0.0f;
            int N = TableLayout.this.getChildCount();
            for (int i = 0; i < N; i++) {
                LayoutParams lp = TableLayout.this.getChildAt(i).getLayoutParams();
                totalWeight += (this.horizontal ? lp.columnSpec : lp.rowSpec).weight;
            }
            return totalWeight;
        }

        private void computeLocations(int[] a) {
            if (hasWeights()) {
                solveAndDistributeSpace(a);
            } else {
                solve(a);
            }
            if (!this.orderPreserved) {
                int a0 = a[0];
                int N = a.length;
                for (int i = 0; i < N; i++) {
                    a[i] = a[i] - a0;
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

        private int size(int[] locations) {
            return locations[getCount()];
        }

        private void setParentConstraints(int min, int max) {
            this.parentMin.value = min;
            this.parentMax.value = -max;
            this.locationsValid = false;
        }

        private int getMeasure(int min, int max) {
            setParentConstraints(min, max);
            return size(getLocations());
        }

        public int getMeasure(int measureSpec) {
            int mode = MeasureSpec.getMode(measureSpec);
            int size = MeasureSpec.getSize(measureSpec);
            switch (mode) {
                case Integer.MIN_VALUE:
                    return getMeasure(0, size);
                case 0:
                    return getMeasure(0, 100000);
                case 1073741824:
                    return getMeasure(size, size);
                default:
                    return 0;
            }
        }

        public void layout(int size) {
            setParentConstraints(size, size);
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

    /* renamed from: org.telegram.ui.Components.TableLayout$Bounds */
    static class Bounds {
        public int after;
        public int before;
        public int flexibility;

        /* synthetic */ Bounds(CLASSNAME x0) {
            this();
        }

        private Bounds() {
            reset();
        }

        protected void reset() {
            this.before = Integer.MIN_VALUE;
            this.after = Integer.MIN_VALUE;
            this.flexibility = 2;
        }

        protected void include(int before, int after) {
            this.before = Math.max(this.before, before);
            this.after = Math.max(this.after, after);
        }

        protected int size(boolean min) {
            if (min || !TableLayout.canStretch(this.flexibility)) {
                return this.before + this.after;
            }
            return 100000;
        }

        protected int getOffset(TableLayout gl, Child c, Alignment a, int size, boolean horizontal) {
            return this.before - a.getAlignmentValue(c, size);
        }

        protected final void include(TableLayout gl, Child c, Spec spec, Axis axis, int size) {
            this.flexibility &= spec.getFlexibility();
            boolean horizontal = axis.horizontal;
            int before = spec.getAbsoluteAlignment(axis.horizontal).getAlignmentValue(c, size);
            include(before, size - before);
        }
    }

    /* renamed from: org.telegram.ui.Components.TableLayout$Child */
    public class Child {
        private TL_pageTableCell cell;
        private int fixedHeight;
        private int index;
        private LayoutParams layoutParams;
        private int measuredHeight;
        private int measuredWidth;
        public int textHeight;
        public DrawingText textLayout;
        public int textLeft;
        public int textWidth;
        public int textX;
        public int textY;
        /* renamed from: x */
        public int f248x;
        /* renamed from: y */
        public int f249y;

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

        public void measure(int width, int height, boolean first) {
            this.measuredWidth = width;
            this.measuredHeight = height;
            if (first) {
                this.fixedHeight = this.measuredHeight;
            }
            if (this.cell != null) {
                if (this.cell.valign_middle) {
                    this.textY = (this.measuredHeight - this.textHeight) / 2;
                } else if (this.cell.valign_bottom) {
                    this.textY = (this.measuredHeight - this.textHeight) - TableLayout.this.itemPaddingTop;
                } else {
                    this.textY = TableLayout.this.itemPaddingTop;
                }
                if (this.textLayout != null) {
                    int lineCount = this.textLayout.getLineCount();
                    if (!first && (lineCount > 1 || (lineCount > 0 && (this.cell.align_center || this.cell.align_right)))) {
                        setTextLayout(TableLayout.this.delegate.createTextLayout(this.cell, this.measuredWidth - (TableLayout.this.itemPaddingLeft * 2)));
                        this.fixedHeight = this.textHeight + (TableLayout.this.itemPaddingTop * 2);
                    }
                    if (this.textLeft != 0) {
                        this.textX = -this.textLeft;
                        if (this.cell.align_right) {
                            this.textX += (this.measuredWidth - this.textWidth) - TableLayout.this.itemPaddingLeft;
                            return;
                        } else if (this.cell.align_center) {
                            this.textX += Math.round((float) ((this.measuredWidth - this.textWidth) / 2));
                            return;
                        } else {
                            this.textX += TableLayout.this.itemPaddingLeft;
                            return;
                        }
                    }
                    this.textX = TableLayout.this.itemPaddingLeft;
                }
            }
        }

        public void setTextLayout(DrawingText layout) {
            this.textLayout = layout;
            if (layout != null) {
                this.textWidth = 0;
                this.textLeft = 0;
                int a = 0;
                int N = layout.getLineCount();
                while (a < N) {
                    float lineLeft = layout.getLineLeft(a);
                    this.textLeft = a == 0 ? (int) Math.ceil((double) lineLeft) : Math.min(this.textLeft, (int) Math.ceil((double) lineLeft));
                    this.textWidth = (int) Math.ceil((double) Math.max(layout.getLineWidth(a), (float) this.textWidth));
                    a++;
                }
                this.textHeight = layout.getHeight();
                return;
            }
            this.textLeft = 0;
            this.textWidth = 0;
            this.textHeight = 0;
        }

        public void layout(int left, int top, int right, int bottom) {
            this.f248x = left;
            this.f249y = top;
        }

        public int getTextX() {
            return this.f248x + this.textX;
        }

        public int getTextY() {
            return this.f249y + this.textY;
        }

        public void setFixedHeight(int value) {
            this.measuredHeight = this.fixedHeight;
            if (this.cell.valign_middle) {
                this.textY = (this.measuredHeight - this.textHeight) / 2;
            } else if (this.cell.valign_bottom) {
                this.textY = (this.measuredHeight - this.textHeight) - TableLayout.this.itemPaddingTop;
            }
        }

        public void draw(Canvas canvas) {
            if (this.cell != null) {
                boolean isLastX = this.f248x + this.measuredWidth == TableLayout.this.getMeasuredWidth();
                boolean isLastY = this.f249y + this.measuredHeight == TableLayout.this.getMeasuredHeight();
                int rad = AndroidUtilities.m9dp(3.0f);
                if (this.cell.header || (TableLayout.this.isStriped && this.layoutParams.rowSpec.span.min % 2 == 0)) {
                    float[] access$500;
                    float f;
                    boolean hasCorners = false;
                    if (this.f248x == 0 && this.f249y == 0) {
                        access$500 = TableLayout.this.radii;
                        f = (float) rad;
                        TableLayout.this.radii[1] = f;
                        access$500[0] = f;
                        hasCorners = true;
                    } else {
                        access$500 = TableLayout.this.radii;
                        TableLayout.this.radii[1] = 0.0f;
                        access$500[0] = 0.0f;
                    }
                    if (isLastX && this.f249y == 0) {
                        access$500 = TableLayout.this.radii;
                        f = (float) rad;
                        TableLayout.this.radii[3] = f;
                        access$500[2] = f;
                        hasCorners = true;
                    } else {
                        access$500 = TableLayout.this.radii;
                        TableLayout.this.radii[3] = 0.0f;
                        access$500[2] = 0.0f;
                    }
                    if (isLastX && isLastY) {
                        access$500 = TableLayout.this.radii;
                        f = (float) rad;
                        TableLayout.this.radii[5] = f;
                        access$500[4] = f;
                        hasCorners = true;
                    } else {
                        access$500 = TableLayout.this.radii;
                        TableLayout.this.radii[5] = 0.0f;
                        access$500[4] = 0.0f;
                    }
                    if (this.f248x == 0 && isLastY) {
                        access$500 = TableLayout.this.radii;
                        f = (float) rad;
                        TableLayout.this.radii[7] = f;
                        access$500[6] = f;
                        hasCorners = true;
                    } else {
                        access$500 = TableLayout.this.radii;
                        TableLayout.this.radii[7] = 0.0f;
                        access$500[6] = 0.0f;
                    }
                    if (hasCorners) {
                        TableLayout.this.rect.set((float) this.f248x, (float) this.f249y, (float) (this.f248x + this.measuredWidth), (float) (this.f249y + this.measuredHeight));
                        TableLayout.this.backgroundPath.reset();
                        TableLayout.this.backgroundPath.addRoundRect(TableLayout.this.rect, TableLayout.this.radii, Direction.CW);
                        if (this.cell.header) {
                            canvas.drawPath(TableLayout.this.backgroundPath, TableLayout.this.delegate.getHeaderPaint());
                        } else {
                            canvas.drawPath(TableLayout.this.backgroundPath, TableLayout.this.delegate.getStripPaint());
                        }
                    } else if (this.cell.header) {
                        canvas.drawRect((float) this.f248x, (float) this.f249y, (float) (this.f248x + this.measuredWidth), (float) (this.f249y + this.measuredHeight), TableLayout.this.delegate.getHeaderPaint());
                    } else {
                        canvas.drawRect((float) this.f248x, (float) this.f249y, (float) (this.f248x + this.measuredWidth), (float) (this.f249y + this.measuredHeight), TableLayout.this.delegate.getStripPaint());
                    }
                }
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) getTextX(), (float) getTextY());
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                if (TableLayout.this.drawLines) {
                    float start;
                    float end;
                    Paint linePaint = TableLayout.this.delegate.getLinePaint();
                    Paint halfLinePaint = TableLayout.this.delegate.getLinePaint();
                    float strokeWidth = linePaint.getStrokeWidth() / 2.0f;
                    float halfStrokeWidth = halfLinePaint.getStrokeWidth() / 2.0f;
                    if (this.f248x == 0) {
                        start = (float) this.f249y;
                        end = (float) (this.f249y + this.measuredHeight);
                        if (this.f249y == 0) {
                            start += (float) rad;
                        }
                        if (end == ((float) TableLayout.this.getMeasuredHeight())) {
                            end -= (float) rad;
                        }
                        canvas.drawLine(((float) this.f248x) + strokeWidth, start, ((float) this.f248x) + strokeWidth, end, linePaint);
                    } else {
                        canvas.drawLine(((float) this.f248x) - halfStrokeWidth, (float) this.f249y, ((float) this.f248x) - halfStrokeWidth, (float) (this.f249y + this.measuredHeight), halfLinePaint);
                    }
                    if (this.f249y == 0) {
                        start = (float) this.f248x;
                        end = (float) (this.f248x + this.measuredWidth);
                        if (this.f248x == 0) {
                            start += (float) rad;
                        }
                        if (end == ((float) TableLayout.this.getMeasuredWidth())) {
                            end -= (float) rad;
                        }
                        canvas.drawLine(start, ((float) this.f249y) + strokeWidth, end, ((float) this.f249y) + strokeWidth, linePaint);
                    } else {
                        canvas.drawLine((float) this.f248x, ((float) this.f249y) - halfStrokeWidth, (float) (this.f248x + this.measuredWidth), ((float) this.f249y) - halfStrokeWidth, halfLinePaint);
                    }
                    if (isLastX && this.f249y == 0) {
                        start = (float) (this.f249y + rad);
                    } else {
                        start = ((float) this.f249y) - strokeWidth;
                    }
                    if (isLastX && isLastY) {
                        end = (float) ((this.f249y + this.measuredHeight) - rad);
                    } else {
                        end = ((float) (this.f249y + this.measuredHeight)) - strokeWidth;
                    }
                    canvas.drawLine(((float) (this.f248x + this.measuredWidth)) - strokeWidth, start, ((float) (this.f248x + this.measuredWidth)) - strokeWidth, end, linePaint);
                    if (this.f248x == 0 && isLastY) {
                        start = (float) (this.f248x + rad);
                    } else {
                        start = ((float) this.f248x) - strokeWidth;
                    }
                    if (isLastX && isLastY) {
                        end = (float) ((this.f248x + this.measuredWidth) - rad);
                    } else {
                        end = ((float) (this.f248x + this.measuredWidth)) - strokeWidth;
                    }
                    canvas.drawLine(start, ((float) (this.f249y + this.measuredHeight)) - strokeWidth, end, ((float) (this.f249y + this.measuredHeight)) - strokeWidth, linePaint);
                    if (this.f248x == 0 && this.f249y == 0) {
                        TableLayout.this.rect.set(((float) this.f248x) + strokeWidth, ((float) this.f249y) + strokeWidth, (((float) this.f248x) + strokeWidth) + ((float) (rad * 2)), (((float) this.f249y) + strokeWidth) + ((float) (rad * 2)));
                        canvas.drawArc(TableLayout.this.rect, -180.0f, 90.0f, false, linePaint);
                    }
                    if (isLastX && this.f249y == 0) {
                        TableLayout.this.rect.set((((float) (this.f248x + this.measuredWidth)) - strokeWidth) - ((float) (rad * 2)), ((float) this.f249y) + strokeWidth, ((float) (this.f248x + this.measuredWidth)) - strokeWidth, (((float) this.f249y) + strokeWidth) + ((float) (rad * 2)));
                        canvas.drawArc(TableLayout.this.rect, 0.0f, -90.0f, false, linePaint);
                    }
                    if (this.f248x == 0 && isLastY) {
                        TableLayout.this.rect.set(((float) this.f248x) + strokeWidth, (((float) (this.f249y + this.measuredHeight)) - strokeWidth) - ((float) (rad * 2)), (((float) this.f248x) + strokeWidth) + ((float) (rad * 2)), ((float) (this.f249y + this.measuredHeight)) - strokeWidth);
                        canvas.drawArc(TableLayout.this.rect, 180.0f, -90.0f, false, linePaint);
                    }
                    if (isLastX && isLastY) {
                        TableLayout.this.rect.set((((float) (this.f248x + this.measuredWidth)) - strokeWidth) - ((float) (rad * 2)), (((float) (this.f249y + this.measuredHeight)) - strokeWidth) - ((float) (rad * 2)), ((float) (this.f248x + this.measuredWidth)) - strokeWidth, ((float) (this.f249y + this.measuredHeight)) - strokeWidth);
                        canvas.drawArc(TableLayout.this.rect, 0.0f, 90.0f, false, linePaint);
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.TableLayout$Interval */
    static final class Interval {
        public final int max;
        public final int min;

        public Interval(int min, int max) {
            this.min = min;
            this.max = max;
        }

        int size() {
            return this.max - this.min;
        }

        Interval inverse() {
            return new Interval(this.max, this.min);
        }

        public boolean equals(Object that) {
            if (this == that) {
                return true;
            }
            if (that == null || getClass() != that.getClass()) {
                return false;
            }
            Interval interval = (Interval) that;
            if (this.max != interval.max) {
                return false;
            }
            if (this.min != interval.min) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return (this.min * 31) + this.max;
        }
    }

    /* renamed from: org.telegram.ui.Components.TableLayout$LayoutParams */
    public static class LayoutParams extends MarginLayoutParams {
        private static final int DEFAULT_HEIGHT = -2;
        private static final int DEFAULT_MARGIN = Integer.MIN_VALUE;
        private static final Interval DEFAULT_SPAN = new Interval(Integer.MIN_VALUE, -NUM);
        private static final int DEFAULT_SPAN_SIZE = DEFAULT_SPAN.size();
        private static final int DEFAULT_WIDTH = -2;
        public Spec columnSpec;
        public Spec rowSpec;

        private LayoutParams(int width, int height, int left, int top, int right, int bottom, Spec rowSpec, Spec columnSpec) {
            super(width, height);
            this.rowSpec = Spec.UNDEFINED;
            this.columnSpec = Spec.UNDEFINED;
            setMargins(left, top, right, bottom);
            this.rowSpec = rowSpec;
            this.columnSpec = columnSpec;
        }

        public LayoutParams(Spec rowSpec, Spec columnSpec) {
            this(-2, -2, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, rowSpec, columnSpec);
        }

        public LayoutParams() {
            this(Spec.UNDEFINED, Spec.UNDEFINED);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams params) {
            super(params);
            this.rowSpec = Spec.UNDEFINED;
            this.columnSpec = Spec.UNDEFINED;
        }

        public LayoutParams(MarginLayoutParams params) {
            super(params);
            this.rowSpec = Spec.UNDEFINED;
            this.columnSpec = Spec.UNDEFINED;
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            this.rowSpec = Spec.UNDEFINED;
            this.columnSpec = Spec.UNDEFINED;
            this.rowSpec = source.rowSpec;
            this.columnSpec = source.columnSpec;
        }

        public void setGravity(int gravity) {
            this.rowSpec = this.rowSpec.copyWriteAlignment(TableLayout.getAlignment(gravity, false));
            this.columnSpec = this.columnSpec.copyWriteAlignment(TableLayout.getAlignment(gravity, true));
        }

        final void setRowSpecSpan(Interval span) {
            this.rowSpec = this.rowSpec.copyWriteSpan(span);
        }

        final void setColumnSpecSpan(Interval span) {
            this.columnSpec = this.columnSpec.copyWriteSpan(span);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            LayoutParams that = (LayoutParams) o;
            if (!this.columnSpec.equals(that.columnSpec)) {
                return false;
            }
            if (this.rowSpec.equals(that.rowSpec)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return (this.rowSpec.hashCode() * 31) + this.columnSpec.hashCode();
        }
    }

    /* renamed from: org.telegram.ui.Components.TableLayout$MutableInt */
    static final class MutableInt {
        public int value;

        public MutableInt() {
            reset();
        }

        public MutableInt(int value) {
            this.value = value;
        }

        public void reset() {
            this.value = Integer.MIN_VALUE;
        }
    }

    /* renamed from: org.telegram.ui.Components.TableLayout$PackedMap */
    static final class PackedMap<K, V> {
        public final int[] index;
        public final K[] keys;
        public final V[] values;

        /* synthetic */ PackedMap(Object[] x0, Object[] x1, CLASSNAME x2) {
            this(x0, x1);
        }

        private PackedMap(K[] keys, V[] values) {
            this.index = PackedMap.createIndex(keys);
            this.keys = PackedMap.compact(keys, this.index);
            this.values = PackedMap.compact(values, this.index);
        }

        public V getValue(int i) {
            return this.values[this.index[i]];
        }

        private static <K> int[] createIndex(K[] keys) {
            int size = keys.length;
            int[] result = new int[size];
            Map<K, Integer> keyToIndex = new HashMap();
            for (int i = 0; i < size; i++) {
                K key = keys[i];
                Integer index = (Integer) keyToIndex.get(key);
                if (index == null) {
                    index = Integer.valueOf(keyToIndex.size());
                    keyToIndex.put(key, index);
                }
                result[i] = index.intValue();
            }
            return result;
        }

        private static <K> K[] compact(K[] a, int[] index) {
            int size = a.length;
            Object[] result = (Object[]) ((Object[]) Array.newInstance(a.getClass().getComponentType(), TableLayout.max2(index, -1) + 1));
            for (int i = 0; i < size; i++) {
                result[index[i]] = a[i];
            }
            return result;
        }
    }

    /* renamed from: org.telegram.ui.Components.TableLayout$Spec */
    public static class Spec {
        static final float DEFAULT_WEIGHT = 0.0f;
        static final Spec UNDEFINED = TableLayout.spec(Integer.MIN_VALUE);
        final Alignment alignment;
        final Interval span;
        final boolean startDefined;
        float weight;

        /* synthetic */ Spec(boolean x0, int x1, int x2, Alignment x3, float x4, CLASSNAME x5) {
            this(x0, x1, x2, x3, x4);
        }

        private Spec(boolean startDefined, Interval span, Alignment alignment, float weight) {
            this.startDefined = startDefined;
            this.span = span;
            this.alignment = alignment;
            this.weight = weight;
        }

        private Spec(boolean startDefined, int start, int size, Alignment alignment, float weight) {
            this(startDefined, new Interval(start, start + size), alignment, weight);
        }

        private Alignment getAbsoluteAlignment(boolean horizontal) {
            if (this.alignment != TableLayout.UNDEFINED_ALIGNMENT) {
                return this.alignment;
            }
            if (this.weight == 0.0f) {
                return horizontal ? TableLayout.START : TableLayout.BASELINE;
            } else {
                return TableLayout.FILL;
            }
        }

        final Spec copyWriteSpan(Interval span) {
            return new Spec(this.startDefined, span, this.alignment, this.weight);
        }

        final Spec copyWriteAlignment(Alignment alignment) {
            return new Spec(this.startDefined, this.span, alignment, this.weight);
        }

        final int getFlexibility() {
            return (this.alignment == TableLayout.UNDEFINED_ALIGNMENT && this.weight == 0.0f) ? 0 : 2;
        }

        public boolean equals(Object that) {
            if (this == that) {
                return true;
            }
            if (that == null || getClass() != that.getClass()) {
                return false;
            }
            Spec spec = (Spec) that;
            if (!this.alignment.equals(spec.alignment)) {
                return false;
            }
            if (this.span.equals(spec.span)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return (this.span.hashCode() * 31) + this.alignment.hashCode();
        }
    }

    /* renamed from: org.telegram.ui.Components.TableLayout$TableLayoutDelegate */
    public interface TableLayoutDelegate {
        DrawingText createTextLayout(TL_pageTableCell tL_pageTableCell, int i);

        Paint getHalfLinePaint();

        Paint getHeaderPaint();

        Paint getLinePaint();

        Paint getStripPaint();
    }

    /* renamed from: org.telegram.ui.Components.TableLayout$1 */
    static class CLASSNAME extends Alignment {
        CLASSNAME() {
        }

        int getGravityOffset(Child view, int cellDelta) {
            return Integer.MIN_VALUE;
        }

        public int getAlignmentValue(Child view, int viewSize) {
            return Integer.MIN_VALUE;
        }
    }

    /* renamed from: org.telegram.ui.Components.TableLayout$2 */
    static class CLASSNAME extends Alignment {
        CLASSNAME() {
        }

        int getGravityOffset(Child view, int cellDelta) {
            return 0;
        }

        public int getAlignmentValue(Child view, int viewSize) {
            return 0;
        }
    }

    /* renamed from: org.telegram.ui.Components.TableLayout$3 */
    static class CLASSNAME extends Alignment {
        CLASSNAME() {
        }

        int getGravityOffset(Child view, int cellDelta) {
            return cellDelta;
        }

        public int getAlignmentValue(Child view, int viewSize) {
            return viewSize;
        }
    }

    /* renamed from: org.telegram.ui.Components.TableLayout$5 */
    static class CLASSNAME extends Alignment {
        CLASSNAME() {
        }

        int getGravityOffset(Child view, int cellDelta) {
            return cellDelta >> 1;
        }

        public int getAlignmentValue(Child view, int viewSize) {
            return viewSize >> 1;
        }
    }

    /* renamed from: org.telegram.ui.Components.TableLayout$6 */
    static class CLASSNAME extends Alignment {

        /* renamed from: org.telegram.ui.Components.TableLayout$6$1 */
        class CLASSNAME extends Bounds {
            private int size;

            CLASSNAME() {
                super();
            }

            protected void reset() {
                super.reset();
                this.size = Integer.MIN_VALUE;
            }

            protected void include(int before, int after) {
                super.include(before, after);
                this.size = Math.max(this.size, before + after);
            }

            protected int size(boolean min) {
                return Math.max(super.size(min), this.size);
            }

            protected int getOffset(TableLayout gl, Child c, Alignment a, int size, boolean hrz) {
                return Math.max(0, super.getOffset(gl, c, a, size, hrz));
            }
        }

        CLASSNAME() {
        }

        int getGravityOffset(Child view, int cellDelta) {
            return 0;
        }

        public int getAlignmentValue(Child view, int viewSize) {
            return Integer.MIN_VALUE;
        }

        public Bounds getBounds() {
            return new CLASSNAME();
        }
    }

    /* renamed from: org.telegram.ui.Components.TableLayout$7 */
    static class CLASSNAME extends Alignment {
        CLASSNAME() {
        }

        int getGravityOffset(Child view, int cellDelta) {
            return 0;
        }

        public int getAlignmentValue(Child view, int viewSize) {
            return Integer.MIN_VALUE;
        }

        public int getSizeInCell(Child view, int viewSize, int cellSize) {
            return cellSize;
        }
    }

    public void addChild(int x, int y, int colspan, int rowspan) {
        Child child = new Child(this.childrens.size());
        LayoutParams layoutParams = new LayoutParams();
        layoutParams.rowSpec = new Spec(false, new Interval(y, y + rowspan), FILL, 0.0f, null);
        layoutParams.columnSpec = new Spec(false, new Interval(x, x + colspan), FILL, 0.0f, null);
        child.layoutParams = layoutParams;
        this.childrens.add(child);
        invalidateStructure();
    }

    public void addChild(TL_pageTableCell cell, int x, int y, int colspan) {
        if (colspan == 0) {
            colspan = 1;
        }
        Child child = new Child(this.childrens.size());
        child.cell = cell;
        LayoutParams layoutParams = new LayoutParams();
        layoutParams.rowSpec = new Spec(false, new Interval(y, (cell.rowspan != 0 ? cell.rowspan : 1) + y), FILL, 0.0f, null);
        layoutParams.columnSpec = new Spec(false, new Interval(x, x + colspan), FILL, 1.0f, null);
        child.layoutParams = layoutParams;
        this.childrens.add(child);
        if (cell.rowspan > 1) {
            this.rowSpans.add(new Point((float) y, (float) (cell.rowspan + y)));
        }
        invalidateStructure();
    }

    public void setDrawLines(boolean value) {
        this.drawLines = value;
    }

    public void setStriped(boolean value) {
        this.isStriped = value;
    }

    public void setRtl(boolean value) {
        this.isRtl = value;
    }

    public void removeAllChildrens() {
        this.childrens.clear();
        this.rowSpans.clear();
        invalidateStructure();
    }

    public int getChildCount() {
        return this.childrens.size();
    }

    public Child getChildAt(int index) {
        if (index < 0 || index >= this.childrens.size()) {
            return null;
        }
        return (Child) this.childrens.get(index);
    }

    public TableLayout(Context context, TableLayoutDelegate tableLayoutDelegate) {
        super(context);
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

    public void setOrientation(int orientation) {
        if (this.mOrientation != orientation) {
            this.mOrientation = orientation;
            invalidateStructure();
            requestLayout();
        }
    }

    public int getRowCount() {
        return this.mVerticalAxis.getCount();
    }

    public void setRowCount(int rowCount) {
        this.mVerticalAxis.setCount(rowCount);
        invalidateStructure();
        requestLayout();
    }

    public int getColumnCount() {
        return this.mHorizontalAxis.getCount();
    }

    public void setColumnCount(int columnCount) {
        this.mHorizontalAxis.setCount(columnCount);
        invalidateStructure();
        requestLayout();
    }

    public boolean getUseDefaultMargins() {
        return this.mUseDefaultMargins;
    }

    public void setUseDefaultMargins(boolean useDefaultMargins) {
        this.mUseDefaultMargins = useDefaultMargins;
        requestLayout();
    }

    public int getAlignmentMode() {
        return this.mAlignmentMode;
    }

    public void setAlignmentMode(int alignmentMode) {
        this.mAlignmentMode = alignmentMode;
        requestLayout();
    }

    public boolean isRowOrderPreserved() {
        return this.mVerticalAxis.isOrderPreserved();
    }

    public void setRowOrderPreserved(boolean rowOrderPreserved) {
        this.mVerticalAxis.setOrderPreserved(rowOrderPreserved);
        invalidateStructure();
        requestLayout();
    }

    public boolean isColumnOrderPreserved() {
        return this.mHorizontalAxis.isOrderPreserved();
    }

    public void setColumnOrderPreserved(boolean columnOrderPreserved) {
        this.mHorizontalAxis.setOrderPreserved(columnOrderPreserved);
        invalidateStructure();
        requestLayout();
    }

    static int max2(int[] a, int valueIfEmpty) {
        int result = valueIfEmpty;
        for (int max : a) {
            result = Math.max(result, max);
        }
        return result;
    }

    static <T> T[] append(T[] a, T[] b) {
        Object[] result = (Object[]) ((Object[]) Array.newInstance(a.getClass().getComponentType(), a.length + b.length));
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    static Alignment getAlignment(int gravity, boolean horizontal) {
        switch ((gravity & (horizontal ? 7 : 112)) >> (horizontal ? 0 : 4)) {
            case 1:
                return CENTER;
            case 3:
                return horizontal ? LEFT : TOP;
            case 5:
                return horizontal ? RIGHT : BOTTOM;
            case 7:
                return FILL;
            case 8388611:
                return START;
            case 8388613:
                return END;
            default:
                return UNDEFINED_ALIGNMENT;
        }
    }

    private int getDefaultMargin(Child c, boolean horizontal, boolean leading) {
        return this.mDefaultGap / 2;
    }

    private int getDefaultMargin(Child c, boolean isAtEdge, boolean horizontal, boolean leading) {
        return getDefaultMargin(c, horizontal, leading);
    }

    private int getDefaultMargin(Child c, LayoutParams p, boolean horizontal, boolean leading) {
        boolean isAtEdge = true;
        if (!this.mUseDefaultMargins) {
            return 0;
        }
        boolean z;
        Spec spec = horizontal ? p.columnSpec : p.rowSpec;
        Axis axis = horizontal ? this.mHorizontalAxis : this.mVerticalAxis;
        Interval span = spec.span;
        if (horizontal && this.isRtl) {
            z = true;
        } else {
            z = false;
        }
        if (z != leading) {
            if (span.min != 0) {
                isAtEdge = false;
            }
        } else if (span.max != axis.getCount()) {
            isAtEdge = false;
        }
        return getDefaultMargin(c, isAtEdge, horizontal, leading);
    }

    int getMargin1(Child view, boolean horizontal, boolean leading) {
        LayoutParams lp = view.getLayoutParams();
        int margin = horizontal ? leading ? lp.leftMargin : lp.rightMargin : leading ? lp.topMargin : lp.bottomMargin;
        return margin == Integer.MIN_VALUE ? getDefaultMargin(view, lp, horizontal, leading) : margin;
    }

    private int getMargin(Child view, boolean horizontal, boolean leading) {
        if (this.mAlignmentMode == 1) {
            return getMargin1(view, horizontal, leading);
        }
        Axis axis = horizontal ? this.mHorizontalAxis : this.mVerticalAxis;
        int[] margins = leading ? axis.getLeadingMargins() : axis.getTrailingMargins();
        LayoutParams lp = view.getLayoutParams();
        Spec spec = horizontal ? lp.columnSpec : lp.rowSpec;
        return margins[leading ? spec.span.min : spec.span.max];
    }

    private int getTotalMargin(Child child, boolean horizontal) {
        return getMargin(child, horizontal, true) + getMargin(child, horizontal, false);
    }

    private static boolean fits(int[] a, int value, int start, int end) {
        if (end > a.length) {
            return false;
        }
        for (int i = start; i < end; i++) {
            if (a[i] > value) {
                return false;
            }
        }
        return true;
    }

    private static void procrusteanFill(int[] a, int start, int end, int value) {
        int length = a.length;
        Arrays.fill(a, Math.min(start, length), Math.min(end, length), value);
    }

    private static void setCellGroup(LayoutParams lp, int row, int rowSpan, int col, int colSpan) {
        lp.setRowSpecSpan(new Interval(row, row + rowSpan));
        lp.setColumnSpecSpan(new Interval(col, col + colSpan));
    }

    private static int clip(Interval minorRange, boolean minorWasDefined, int count) {
        int size = minorRange.size();
        if (count == 0) {
            return size;
        }
        return Math.min(size, count - (minorWasDefined ? Math.min(minorRange.min, count) : 0));
    }

    private void validateLayoutParams() {
        boolean horizontal = this.mOrientation == 0;
        Axis axis = horizontal ? this.mHorizontalAxis : this.mVerticalAxis;
        int count = axis.definedCount != Integer.MIN_VALUE ? axis.definedCount : 0;
        int major = 0;
        int minor = 0;
        int[] maxSizes = new int[count];
        int N = getChildCount();
        for (int i = 0; i < N; i++) {
            LayoutParams lp = getChildAt(i).getLayoutParams();
            Spec majorSpec = horizontal ? lp.rowSpec : lp.columnSpec;
            Interval majorRange = majorSpec.span;
            boolean majorWasDefined = majorSpec.startDefined;
            int majorSpan = majorRange.size();
            if (majorWasDefined) {
                major = majorRange.min;
            }
            Spec minorSpec = horizontal ? lp.columnSpec : lp.rowSpec;
            Interval minorRange = minorSpec.span;
            boolean minorWasDefined = minorSpec.startDefined;
            int minorSpan = TableLayout.clip(minorRange, minorWasDefined, count);
            if (minorWasDefined) {
                minor = minorRange.min;
            }
            if (count != 0) {
                if (!(majorWasDefined && minorWasDefined)) {
                    while (!TableLayout.fits(maxSizes, major, minor, minor + minorSpan)) {
                        if (minorWasDefined) {
                            major++;
                        } else if (minor + minorSpan <= count) {
                            minor++;
                        } else {
                            minor = 0;
                            major++;
                        }
                    }
                }
                TableLayout.procrusteanFill(maxSizes, minor, minor + minorSpan, major + majorSpan);
            }
            if (horizontal) {
                TableLayout.setCellGroup(lp, major, majorSpan, minor, minorSpan);
            } else {
                TableLayout.setCellGroup(lp, minor, minorSpan, major, majorSpan);
            }
            minor += minorSpan;
        }
    }

    private void invalidateStructure() {
        this.mLastLayoutParamsHashCode = 0;
        this.mHorizontalAxis.invalidateStructure();
        this.mVerticalAxis.invalidateStructure();
        invalidateValues();
    }

    private void invalidateValues() {
        if (this.mHorizontalAxis != null && this.mVerticalAxis != null) {
            this.mHorizontalAxis.invalidateValues();
            this.mVerticalAxis.invalidateValues();
        }
    }

    private static void handleInvalidParams(String msg) {
        throw new IllegalArgumentException(msg + ". ");
    }

    private void checkLayoutParams(LayoutParams lp, boolean horizontal) {
        String groupName = horizontal ? "column" : "row";
        Interval span = (horizontal ? lp.columnSpec : lp.rowSpec).span;
        if (span.min != Integer.MIN_VALUE && span.min < 0) {
            TableLayout.handleInvalidParams(groupName + " indices must be positive");
        }
        int count = (horizontal ? this.mHorizontalAxis : this.mVerticalAxis).definedCount;
        if (count != Integer.MIN_VALUE) {
            if (span.max > count) {
                TableLayout.handleInvalidParams(groupName + " indices (start + span) mustn't exceed the " + groupName + " count");
            }
            if (span.size() > count) {
                TableLayout.handleInvalidParams(groupName + " span mustn't exceed the " + groupName + " count");
            }
        }
    }

    protected void onDraw(Canvas canvas) {
        int N = getChildCount();
        for (int i = 0; i < N; i++) {
            getChildAt(i).draw(canvas);
        }
    }

    private int computeLayoutParamsHashCode() {
        int result = 1;
        for (int i = 0; i < getChildCount(); i++) {
            result = (result * 31) + getChildAt(i).getLayoutParams().hashCode();
        }
        return result;
    }

    private void consistencyCheck() {
        if (this.mLastLayoutParamsHashCode == 0) {
            validateLayoutParams();
            this.mLastLayoutParamsHashCode = computeLayoutParamsHashCode();
        } else if (this.mLastLayoutParamsHashCode != computeLayoutParamsHashCode()) {
            invalidateStructure();
            consistencyCheck();
        }
    }

    private void measureChildWithMargins2(Child child, int parentWidthSpec, int parentHeightSpec, int childWidth, int childHeight, boolean first) {
        child.measure(getTotalMargin(child, true) + childWidth, getTotalMargin(child, false) + childHeight, first);
    }

    private void measureChildrenWithMargins(int widthSpec, int heightSpec, boolean firstPass) {
        int N = getChildCount();
        for (int i = 0; i < N; i++) {
            Child c = getChildAt(i);
            LayoutParams lp = c.getLayoutParams();
            if (firstPass) {
                int maxCellWidth;
                int width = MeasureSpec.getSize(widthSpec);
                if (this.colCount == 2) {
                    maxCellWidth = ((int) (((float) width) / 2.0f)) - (this.itemPaddingLeft * 4);
                } else {
                    maxCellWidth = (int) (((float) width) / 1.5f);
                }
                c.setTextLayout(this.delegate.createTextLayout(c.cell, maxCellWidth));
                if (c.textLayout != null) {
                    lp.width = c.textWidth + (this.itemPaddingLeft * 2);
                    lp.height = c.textHeight + (this.itemPaddingTop * 2);
                } else {
                    lp.width = 0;
                    lp.height = 0;
                }
                measureChildWithMargins2(c, widthSpec, heightSpec, lp.width, lp.height, true);
            } else {
                boolean horizontal = this.mOrientation == 0;
                Spec spec = horizontal ? lp.columnSpec : lp.rowSpec;
                if (spec.getAbsoluteAlignment(horizontal) == FILL) {
                    Interval span = spec.span;
                    int[] locations = (horizontal ? this.mHorizontalAxis : this.mVerticalAxis).getLocations();
                    int viewSize = (locations[span.max] - locations[span.min]) - getTotalMargin(c, horizontal);
                    if (horizontal) {
                        measureChildWithMargins2(c, widthSpec, heightSpec, viewSize, lp.height, false);
                    } else {
                        measureChildWithMargins2(c, widthSpec, heightSpec, lp.width, viewSize, false);
                    }
                }
            }
        }
    }

    static int adjust(int measureSpec, int delta) {
        return MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(measureSpec + delta), MeasureSpec.getMode(measureSpec));
    }

    protected void onMeasure(int widthSpec, int heightSpec) {
        int a;
        int widthSansPadding;
        int heightSansPadding;
        int i;
        int size;
        consistencyCheck();
        invalidateValues();
        this.colCount = 0;
        int N = getChildCount();
        for (a = 0; a < N; a++) {
            this.colCount = Math.max(this.colCount, getChildAt(a).layoutParams.columnSpec.span.max);
        }
        measureChildrenWithMargins(widthSpec, heightSpec, true);
        if (this.mOrientation == 0) {
            widthSansPadding = this.mHorizontalAxis.getMeasure(widthSpec);
            measureChildrenWithMargins(widthSpec, heightSpec, false);
            heightSansPadding = this.mVerticalAxis.getMeasure(heightSpec);
        } else {
            heightSansPadding = this.mVerticalAxis.getMeasure(heightSpec);
            measureChildrenWithMargins(widthSpec, heightSpec, false);
            widthSansPadding = this.mHorizontalAxis.getMeasure(widthSpec);
        }
        int measuredWidth = Math.max(widthSansPadding, MeasureSpec.getSize(widthSpec));
        int measuredHeight = Math.max(heightSansPadding, getSuggestedMinimumHeight());
        setMeasuredDimension(measuredWidth, measuredHeight);
        this.mHorizontalAxis.layout(measuredWidth);
        this.mVerticalAxis.layout(measuredHeight);
        int[] hLocations = this.mHorizontalAxis.getLocations();
        int[] vLocations = this.mVerticalAxis.getLocations();
        int fixedHeight = measuredHeight;
        this.cellsToFixHeight.clear();
        measuredWidth = hLocations[hLocations.length - 1];
        N = getChildCount();
        for (i = 0; i < N; i++) {
            Child c = getChildAt(i);
            LayoutParams lp = c.getLayoutParams();
            Spec columnSpec = lp.columnSpec;
            Spec rowSpec = lp.rowSpec;
            Interval colSpan = columnSpec.span;
            Interval rowSpan = rowSpec.span;
            int x1 = hLocations[colSpan.min];
            int y1 = vLocations[rowSpan.min];
            int cellWidth = hLocations[colSpan.max] - x1;
            int cellHeight = vLocations[rowSpan.max] - y1;
            int pWidth = getMeasurement(c, true);
            int pHeight = getMeasurement(c, false);
            Alignment hAlign = columnSpec.getAbsoluteAlignment(true);
            Alignment vAlign = rowSpec.getAbsoluteAlignment(false);
            Bounds boundsX = (Bounds) this.mHorizontalAxis.getGroupBounds().getValue(i);
            Bounds boundsY = (Bounds) this.mVerticalAxis.getGroupBounds().getValue(i);
            int gravityOffsetX = hAlign.getGravityOffset(c, cellWidth - boundsX.size(true));
            int gravityOffsetY = vAlign.getGravityOffset(c, cellHeight - boundsY.size(true));
            int leftMargin = getMargin(c, true, true);
            int topMargin = getMargin(c, false, true);
            int rightMargin = getMargin(c, true, false);
            int sumMarginsX = leftMargin + rightMargin;
            int sumMarginsY = topMargin + getMargin(c, false, false);
            int alignmentOffsetX = boundsX.getOffset(this, c, hAlign, pWidth + sumMarginsX, true);
            int alignmentOffsetY = boundsY.getOffset(this, c, vAlign, pHeight + sumMarginsY, false);
            int width = hAlign.getSizeInCell(c, pWidth, cellWidth - sumMarginsX);
            int height = vAlign.getSizeInCell(c, pHeight, cellHeight - sumMarginsY);
            int dx = (x1 + gravityOffsetX) + alignmentOffsetX;
            int cx = !this.isRtl ? leftMargin + dx : ((measuredWidth - width) - rightMargin) - dx;
            int cy = ((y1 + gravityOffsetY) + alignmentOffsetY) + topMargin;
            if (c.cell != null) {
                if (!(width == c.getMeasuredWidth() && height == c.getMeasuredHeight())) {
                    c.measure(width, height, false);
                }
                if (!(c.fixedHeight == 0 || c.fixedHeight == height || c.layoutParams.rowSpec.span.max - c.layoutParams.rowSpec.span.min > 1)) {
                    boolean found = false;
                    size = this.rowSpans.size();
                    for (a = 0; a < size; a++) {
                        Point p = (Point) this.rowSpans.get(a);
                        if (p.f240x <= ((float) c.layoutParams.rowSpec.span.min) && p.f241y > ((float) c.layoutParams.rowSpec.span.min)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        this.cellsToFixHeight.add(c);
                    }
                }
            }
            c.layout(cx, cy, cx + width, cy + height);
        }
        a = 0;
        N = this.cellsToFixHeight.size();
        while (a < N) {
            Child next;
            int diff;
            Child child = (Child) this.cellsToFixHeight.get(a);
            boolean skip = false;
            int heightDiff = child.measuredHeight - child.fixedHeight;
            i = child.index + 1;
            size = this.childrens.size();
            while (i < size) {
                next = (Child) this.childrens.get(i);
                if (child.layoutParams.rowSpec.span.min != next.layoutParams.rowSpec.span.min) {
                    break;
                } else if (child.fixedHeight < next.fixedHeight) {
                    skip = true;
                    break;
                } else {
                    diff = next.measuredHeight - next.fixedHeight;
                    if (diff > 0) {
                        heightDiff = Math.min(heightDiff, diff);
                    }
                    i++;
                }
            }
            if (!skip) {
                i = child.index - 1;
                while (i >= 0) {
                    next = (Child) this.childrens.get(i);
                    if (child.layoutParams.rowSpec.span.min != next.layoutParams.rowSpec.span.min) {
                        break;
                    } else if (child.fixedHeight < next.fixedHeight) {
                        skip = true;
                        break;
                    } else {
                        diff = next.measuredHeight - next.fixedHeight;
                        if (diff > 0) {
                            heightDiff = Math.min(heightDiff, diff);
                        }
                        i--;
                    }
                }
            }
            if (!skip) {
                child.setFixedHeight(child.fixedHeight);
                fixedHeight -= heightDiff;
                size = this.childrens.size();
                for (i = 0; i < size; i++) {
                    next = (Child) this.childrens.get(i);
                    if (child != next) {
                        if (child.layoutParams.rowSpec.span.min == next.layoutParams.rowSpec.span.min) {
                            if (next.fixedHeight != next.measuredHeight) {
                                this.cellsToFixHeight.remove(next);
                                if (next.index < child.index) {
                                    a--;
                                }
                                N--;
                            }
                            next.measuredHeight = next.measuredHeight - heightDiff;
                            next.measure(next.measuredWidth, next.measuredHeight, true);
                        } else if (child.layoutParams.rowSpec.span.min < next.layoutParams.rowSpec.span.min) {
                            next.f249y -= heightDiff;
                        }
                    }
                }
            }
            a++;
        }
        setMeasuredDimension(measuredWidth, fixedHeight);
    }

    private int getMeasurement(Child c, boolean horizontal) {
        return horizontal ? c.getMeasuredWidth() : c.getMeasuredHeight();
    }

    final int getMeasurementIncludingMargin(Child c, boolean horizontal) {
        return getMeasurement(c, horizontal) + getTotalMargin(c, horizontal);
    }

    public void requestLayout() {
        super.requestLayout();
        invalidateValues();
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        consistencyCheck();
    }

    public static Spec spec(int start, int size, Alignment alignment, float weight) {
        return new Spec(start != Integer.MIN_VALUE, start, size, alignment, weight, null);
    }

    public static Spec spec(int start, Alignment alignment, float weight) {
        return TableLayout.spec(start, 1, alignment, weight);
    }

    public static Spec spec(int start, int size, float weight) {
        return TableLayout.spec(start, size, UNDEFINED_ALIGNMENT, weight);
    }

    public static Spec spec(int start, float weight) {
        return TableLayout.spec(start, 1, weight);
    }

    public static Spec spec(int start, int size, Alignment alignment) {
        return TableLayout.spec(start, size, alignment, 0.0f);
    }

    public static Spec spec(int start, Alignment alignment) {
        return TableLayout.spec(start, 1, alignment);
    }

    public static Spec spec(int start, int size) {
        return TableLayout.spec(start, size, UNDEFINED_ALIGNMENT);
    }

    public static Spec spec(int start) {
        return TableLayout.spec(start, 1);
    }

    private static Alignment createSwitchingAlignment(final Alignment ltr) {
        return new Alignment() {
            int getGravityOffset(Child view, int cellDelta) {
                return ltr.getGravityOffset(view, cellDelta);
            }

            public int getAlignmentValue(Child view, int viewSize) {
                return ltr.getAlignmentValue(view, viewSize);
            }
        };
    }

    static boolean canStretch(int flexibility) {
        return (flexibility & 2) != 0;
    }
}
