package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ArticleViewer;
import org.telegram.ui.Cells.TextSelectionHelper;

public class TableLayout extends View {
    public static final int ALIGN_BOUNDS = 0;
    public static final int ALIGN_MARGINS = 1;
    public static final Alignment BASELINE = new Alignment() {
        /* access modifiers changed from: package-private */
        public int getGravityOffset(Child view, int cellDelta) {
            return 0;
        }

        public int getAlignmentValue(Child view, int viewSize) {
            return Integer.MIN_VALUE;
        }

        public Bounds getBounds() {
            return new Bounds() {
                private int size;

                /* access modifiers changed from: protected */
                public void reset() {
                    super.reset();
                    this.size = Integer.MIN_VALUE;
                }

                /* access modifiers changed from: protected */
                public void include(int before, int after) {
                    super.include(before, after);
                    this.size = Math.max(this.size, before + after);
                }

                /* access modifiers changed from: protected */
                public int size(boolean min) {
                    return Math.max(super.size(min), this.size);
                }

                /* access modifiers changed from: protected */
                public int getOffset(TableLayout gl, Child c, Alignment a, int size2, boolean hrz) {
                    return Math.max(0, super.getOffset(gl, c, a, size2, hrz));
                }
            };
        }
    };
    public static final Alignment BOTTOM;
    private static final int CAN_STRETCH = 2;
    public static final Alignment CENTER = new Alignment() {
        /* access modifiers changed from: package-private */
        public int getGravityOffset(Child view, int cellDelta) {
            return cellDelta >> 1;
        }

        public int getAlignmentValue(Child view, int viewSize) {
            return viewSize >> 1;
        }
    };
    private static final int DEFAULT_ALIGNMENT_MODE = 1;
    private static final int DEFAULT_COUNT = Integer.MIN_VALUE;
    private static final boolean DEFAULT_ORDER_PRESERVED = true;
    private static final int DEFAULT_ORIENTATION = 0;
    private static final boolean DEFAULT_USE_DEFAULT_MARGINS = false;
    public static final Alignment END;
    public static final Alignment FILL = new Alignment() {
        /* access modifiers changed from: package-private */
        public int getGravityOffset(Child view, int cellDelta) {
            return 0;
        }

        public int getAlignmentValue(Child view, int viewSize) {
            return Integer.MIN_VALUE;
        }

        public int getSizeInCell(Child view, int viewSize, int cellSize) {
            return cellSize;
        }
    };
    public static final int HORIZONTAL = 0;
    private static final int INFLEXIBLE = 0;
    private static final Alignment LEADING;
    public static final Alignment LEFT;
    static final int MAX_SIZE = 100000;
    public static final Alignment RIGHT;
    public static final Alignment START;
    public static final Alignment TOP;
    private static final Alignment TRAILING;
    public static final int UNDEFINED = Integer.MIN_VALUE;
    static final Alignment UNDEFINED_ALIGNMENT = new Alignment() {
        /* access modifiers changed from: package-private */
        public int getGravityOffset(Child view, int cellDelta) {
            return Integer.MIN_VALUE;
        }

        public int getAlignmentValue(Child view, int viewSize) {
            return Integer.MIN_VALUE;
        }
    };
    static final int UNINITIALIZED_HASH = 0;
    public static final int VERTICAL = 1;
    /* access modifiers changed from: private */
    public Path backgroundPath = new Path();
    private ArrayList<Child> cellsToFixHeight = new ArrayList<>();
    private ArrayList<Child> childrens = new ArrayList<>();
    private int colCount;
    /* access modifiers changed from: private */
    public TableLayoutDelegate delegate;
    /* access modifiers changed from: private */
    public boolean drawLines;
    private boolean isRtl;
    /* access modifiers changed from: private */
    public boolean isStriped;
    /* access modifiers changed from: private */
    public int itemPaddingLeft = AndroidUtilities.dp(8.0f);
    /* access modifiers changed from: private */
    public int itemPaddingTop = AndroidUtilities.dp(7.0f);
    private Path linePath = new Path();
    private int mAlignmentMode = 1;
    private int mDefaultGap;
    private final Axis mHorizontalAxis = new Axis(true);
    private int mLastLayoutParamsHashCode = 0;
    private int mOrientation = 0;
    private boolean mUseDefaultMargins = false;
    private final Axis mVerticalAxis = new Axis(false);
    /* access modifiers changed from: private */
    public float[] radii = new float[8];
    /* access modifiers changed from: private */
    public RectF rect = new RectF();
    private ArrayList<Point> rowSpans = new ArrayList<>();
    /* access modifiers changed from: private */
    public TextSelectionHelper.ArticleTextSelectionHelper textSelectionHelper;

    public interface TableLayoutDelegate {
        ArticleViewer.DrawingText createTextLayout(TLRPC.TL_pageTableCell tL_pageTableCell, int i);

        Paint getHalfLinePaint();

        Paint getHeaderPaint();

        Paint getLinePaint();

        Paint getStripPaint();

        void onLayoutChild(ArticleViewer.DrawingText drawingText, int i, int i2);
    }

    public class Child {
        /* access modifiers changed from: private */
        public TLRPC.TL_pageTableCell cell;
        /* access modifiers changed from: private */
        public int fixedHeight;
        /* access modifiers changed from: private */
        public int index;
        /* access modifiers changed from: private */
        public LayoutParams layoutParams;
        /* access modifiers changed from: private */
        public int measuredHeight;
        /* access modifiers changed from: private */
        public int measuredWidth;
        public int rowspan;
        private int selectionIndex = -1;
        public int textHeight;
        public ArticleViewer.DrawingText textLayout;
        public int textLeft;
        public int textWidth;
        public int textX;
        public int textY;
        public int x;
        public int y;

        static /* synthetic */ int access$1520(Child x0, int x1) {
            int i = x0.measuredHeight - x1;
            x0.measuredHeight = i;
            return i;
        }

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
                this.fixedHeight = height;
            }
            TLRPC.TL_pageTableCell tL_pageTableCell = this.cell;
            if (tL_pageTableCell != null) {
                if (tL_pageTableCell.valign_middle) {
                    this.textY = (this.measuredHeight - this.textHeight) / 2;
                } else if (this.cell.valign_bottom) {
                    this.textY = (this.measuredHeight - this.textHeight) - TableLayout.this.itemPaddingTop;
                } else {
                    this.textY = TableLayout.this.itemPaddingTop;
                }
                ArticleViewer.DrawingText drawingText = this.textLayout;
                if (drawingText != null) {
                    int lineCount = drawingText.getLineCount();
                    if (!first && (lineCount > 1 || (lineCount > 0 && (this.cell.align_center || this.cell.align_right)))) {
                        setTextLayout(TableLayout.this.delegate.createTextLayout(this.cell, this.measuredWidth - (TableLayout.this.itemPaddingLeft * 2)));
                        this.fixedHeight = this.textHeight + (TableLayout.this.itemPaddingTop * 2);
                    }
                    int i = this.textLeft;
                    if (i != 0) {
                        this.textX = -i;
                        if (this.cell.align_right) {
                            this.textX += (this.measuredWidth - this.textWidth) - TableLayout.this.itemPaddingLeft;
                        } else if (this.cell.align_center) {
                            this.textX += Math.round((float) ((this.measuredWidth - this.textWidth) / 2));
                        } else {
                            this.textX += TableLayout.this.itemPaddingLeft;
                        }
                    } else {
                        this.textX = TableLayout.this.itemPaddingLeft;
                    }
                }
            }
        }

        public void setTextLayout(ArticleViewer.DrawingText layout) {
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
            this.x = left;
            this.y = top;
        }

        public int getTextX() {
            return this.x + this.textX;
        }

        public int getTextY() {
            return this.y + this.textY;
        }

        public void setFixedHeight(int value) {
            this.measuredHeight = this.fixedHeight;
            if (this.cell.valign_middle) {
                this.textY = (this.measuredHeight - this.textHeight) / 2;
            } else if (this.cell.valign_bottom) {
                this.textY = (this.measuredHeight - this.textHeight) - TableLayout.this.itemPaddingTop;
            }
        }

        public void draw(Canvas canvas, View view) {
            float start;
            float end;
            float start2;
            float end2;
            int i;
            float start3;
            float end3;
            float start4;
            float end4;
            boolean hasCorners;
            Canvas canvas2 = canvas;
            if (this.cell != null) {
                boolean isLastX = this.x + this.measuredWidth == TableLayout.this.getMeasuredWidth();
                boolean isLastY = this.y + this.measuredHeight == TableLayout.this.getMeasuredHeight();
                int rad = AndroidUtilities.dp(3.0f);
                if (this.cell.header || (TableLayout.this.isStriped && this.layoutParams.rowSpec.span.min % 2 == 0)) {
                    boolean hasCorners2 = false;
                    if (this.x == 0 && this.y == 0) {
                        float[] access$500 = TableLayout.this.radii;
                        float f = (float) rad;
                        TableLayout.this.radii[1] = f;
                        access$500[0] = f;
                        hasCorners2 = true;
                    } else {
                        float[] access$5002 = TableLayout.this.radii;
                        TableLayout.this.radii[1] = 0.0f;
                        access$5002[0] = 0.0f;
                    }
                    if (!isLastX || this.y != 0) {
                        float[] access$5003 = TableLayout.this.radii;
                        TableLayout.this.radii[3] = 0.0f;
                        access$5003[2] = 0.0f;
                    } else {
                        float[] access$5004 = TableLayout.this.radii;
                        float f2 = (float) rad;
                        TableLayout.this.radii[3] = f2;
                        access$5004[2] = f2;
                        hasCorners2 = true;
                    }
                    if (!isLastX || !isLastY) {
                        float[] access$5005 = TableLayout.this.radii;
                        TableLayout.this.radii[5] = 0.0f;
                        access$5005[4] = 0.0f;
                    } else {
                        float[] access$5006 = TableLayout.this.radii;
                        float f3 = (float) rad;
                        TableLayout.this.radii[5] = f3;
                        access$5006[4] = f3;
                        hasCorners2 = true;
                    }
                    if (this.x != 0 || !isLastY) {
                        float[] access$5007 = TableLayout.this.radii;
                        TableLayout.this.radii[7] = 0.0f;
                        access$5007[6] = 0.0f;
                        hasCorners = hasCorners2;
                    } else {
                        float[] access$5008 = TableLayout.this.radii;
                        float f4 = (float) rad;
                        TableLayout.this.radii[7] = f4;
                        access$5008[6] = f4;
                        hasCorners = true;
                    }
                    if (hasCorners) {
                        RectF access$600 = TableLayout.this.rect;
                        int i2 = this.x;
                        int i3 = this.y;
                        access$600.set((float) i2, (float) i3, (float) (i2 + this.measuredWidth), (float) (i3 + this.measuredHeight));
                        TableLayout.this.backgroundPath.reset();
                        TableLayout.this.backgroundPath.addRoundRect(TableLayout.this.rect, TableLayout.this.radii, Path.Direction.CW);
                        if (this.cell.header) {
                            canvas2.drawPath(TableLayout.this.backgroundPath, TableLayout.this.delegate.getHeaderPaint());
                        } else {
                            canvas2.drawPath(TableLayout.this.backgroundPath, TableLayout.this.delegate.getStripPaint());
                        }
                    } else if (this.cell.header) {
                        int i4 = this.x;
                        int i5 = this.y;
                        canvas.drawRect((float) i4, (float) i5, (float) (i4 + this.measuredWidth), (float) (i5 + this.measuredHeight), TableLayout.this.delegate.getHeaderPaint());
                    } else {
                        int i6 = this.x;
                        int i7 = this.y;
                        canvas.drawRect((float) i6, (float) i7, (float) (i6 + this.measuredWidth), (float) (i7 + this.measuredHeight), TableLayout.this.delegate.getStripPaint());
                    }
                }
                if (this.textLayout != null) {
                    canvas.save();
                    canvas2.translate((float) getTextX(), (float) getTextY());
                    if (this.selectionIndex >= 0) {
                        TableLayout.this.textSelectionHelper.draw(canvas2, (TextSelectionHelper.ArticleSelectableView) TableLayout.this.getParent().getParent(), this.selectionIndex);
                    }
                    this.textLayout.draw(canvas2, view);
                    canvas.restore();
                } else {
                    View view2 = view;
                }
                if (TableLayout.this.drawLines) {
                    Paint linePaint = TableLayout.this.delegate.getLinePaint();
                    Paint halfLinePaint = TableLayout.this.delegate.getLinePaint();
                    float strokeWidth = linePaint.getStrokeWidth() / 2.0f;
                    float halfStrokeWidth = halfLinePaint.getStrokeWidth() / 2.0f;
                    int i8 = this.x;
                    if (i8 == 0) {
                        int i9 = this.y;
                        float start5 = (float) i9;
                        float end5 = (float) (this.measuredHeight + i9);
                        if (i9 == 0) {
                            start4 = start5 + ((float) rad);
                        } else {
                            start4 = start5;
                        }
                        if (end5 == ((float) TableLayout.this.getMeasuredHeight())) {
                            end4 = end5 - ((float) rad);
                        } else {
                            end4 = end5;
                        }
                        int i10 = this.x;
                        canvas.drawLine(((float) i10) + strokeWidth, start4, ((float) i10) + strokeWidth, end4, linePaint);
                    } else {
                        int i11 = this.y;
                        canvas.drawLine(((float) i8) - halfStrokeWidth, (float) i11, ((float) i8) - halfStrokeWidth, (float) (i11 + this.measuredHeight), halfLinePaint);
                    }
                    int i12 = this.y;
                    if (i12 == 0) {
                        int i13 = this.x;
                        float start6 = (float) i13;
                        float end6 = (float) (this.measuredWidth + i13);
                        if (i13 == 0) {
                            start3 = start6 + ((float) rad);
                        } else {
                            start3 = start6;
                        }
                        if (end6 == ((float) TableLayout.this.getMeasuredWidth())) {
                            end3 = end6 - ((float) rad);
                        } else {
                            end3 = end6;
                        }
                        int i14 = this.y;
                        canvas.drawLine(start3, ((float) i14) + strokeWidth, end3, ((float) i14) + strokeWidth, linePaint);
                    } else {
                        int i15 = this.x;
                        canvas.drawLine((float) i15, ((float) i12) - halfStrokeWidth, (float) (i15 + this.measuredWidth), ((float) i12) - halfStrokeWidth, halfLinePaint);
                    }
                    if (!isLastX || (i = this.y) != 0) {
                        start = ((float) this.y) - strokeWidth;
                    } else {
                        start = (float) (i + rad);
                    }
                    if (!isLastX || !isLastY) {
                        end = ((float) (this.y + this.measuredHeight)) - strokeWidth;
                    } else {
                        end = (float) ((this.y + this.measuredHeight) - rad);
                    }
                    int i16 = this.x;
                    int i17 = this.measuredWidth;
                    canvas.drawLine(((float) (i16 + i17)) - strokeWidth, start, ((float) (i16 + i17)) - strokeWidth, end, linePaint);
                    int i18 = this.x;
                    if (i18 != 0 || !isLastY) {
                        start2 = ((float) i18) - strokeWidth;
                    } else {
                        start2 = (float) (i18 + rad);
                    }
                    if (!isLastX || !isLastY) {
                        end2 = ((float) (i18 + this.measuredWidth)) - strokeWidth;
                    } else {
                        end2 = (float) ((i18 + this.measuredWidth) - rad);
                    }
                    int i19 = this.y;
                    int i20 = this.measuredHeight;
                    canvas.drawLine(start2, ((float) (i19 + i20)) - strokeWidth, end2, ((float) (i19 + i20)) - strokeWidth, linePaint);
                    if (this.x == 0 && this.y == 0) {
                        RectF access$6002 = TableLayout.this.rect;
                        int i21 = this.x;
                        int i22 = this.y;
                        access$6002.set(((float) i21) + strokeWidth, ((float) i22) + strokeWidth, ((float) i21) + strokeWidth + ((float) (rad * 2)), ((float) i22) + strokeWidth + ((float) (rad * 2)));
                        canvas.drawArc(TableLayout.this.rect, -180.0f, 90.0f, false, linePaint);
                    }
                    if (isLastX && this.y == 0) {
                        RectF access$6003 = TableLayout.this.rect;
                        int i23 = this.x;
                        int i24 = this.measuredWidth;
                        int i25 = this.y;
                        access$6003.set((((float) (i23 + i24)) - strokeWidth) - ((float) (rad * 2)), ((float) i25) + strokeWidth, ((float) (i23 + i24)) - strokeWidth, ((float) i25) + strokeWidth + ((float) (rad * 2)));
                        canvas.drawArc(TableLayout.this.rect, 0.0f, -90.0f, false, linePaint);
                    }
                    if (this.x == 0 && isLastY) {
                        RectF access$6004 = TableLayout.this.rect;
                        int i26 = this.x;
                        int i27 = this.y;
                        int i28 = this.measuredHeight;
                        access$6004.set(((float) i26) + strokeWidth, (((float) (i27 + i28)) - strokeWidth) - ((float) (rad * 2)), ((float) i26) + strokeWidth + ((float) (rad * 2)), ((float) (i27 + i28)) - strokeWidth);
                        canvas.drawArc(TableLayout.this.rect, 180.0f, -90.0f, false, linePaint);
                    }
                    if (!isLastX || !isLastY) {
                        return;
                    }
                    RectF access$6005 = TableLayout.this.rect;
                    int i29 = this.x;
                    int i30 = this.measuredWidth;
                    int i31 = this.y;
                    int i32 = this.measuredHeight;
                    boolean z = isLastX;
                    access$6005.set((((float) (i29 + i30)) - strokeWidth) - ((float) (rad * 2)), (((float) (i31 + i32)) - strokeWidth) - ((float) (rad * 2)), ((float) (i29 + i30)) - strokeWidth, ((float) (i31 + i32)) - strokeWidth);
                    canvas.drawArc(TableLayout.this.rect, 0.0f, 90.0f, false, linePaint);
                    return;
                }
            }
        }

        public void setSelectionIndex(int selectionIndex2) {
            this.selectionIndex = selectionIndex2;
        }

        public int getRow() {
            return this.rowspan + 10;
        }
    }

    public void addChild(int x, int y, int colspan, int rowspan) {
        int i = x;
        int i2 = y;
        Child child = new Child(this.childrens.size());
        LayoutParams layoutParams = new LayoutParams();
        Interval interval = new Interval(i2, i2 + rowspan);
        Alignment alignment = FILL;
        layoutParams.rowSpec = new Spec(false, interval, alignment, 0.0f);
        layoutParams.columnSpec = new Spec(false, new Interval(i, i + colspan), alignment, 0.0f);
        LayoutParams unused = child.layoutParams = layoutParams;
        child.rowspan = i2;
        this.childrens.add(child);
        invalidateStructure();
    }

    public void addChild(TLRPC.TL_pageTableCell cell, int x, int y, int colspan) {
        int colspan2;
        TLRPC.TL_pageTableCell tL_pageTableCell = cell;
        int i = x;
        int i2 = y;
        if (colspan == 0) {
            colspan2 = 1;
        } else {
            colspan2 = colspan;
        }
        Child child = new Child(this.childrens.size());
        TLRPC.TL_pageTableCell unused = child.cell = tL_pageTableCell;
        LayoutParams layoutParams = new LayoutParams();
        Interval interval = new Interval(i2, (tL_pageTableCell.rowspan != 0 ? tL_pageTableCell.rowspan : 1) + i2);
        Alignment alignment = FILL;
        layoutParams.rowSpec = new Spec(false, interval, alignment, 0.0f);
        layoutParams.columnSpec = new Spec(false, new Interval(i, i + colspan2), alignment, 1.0f);
        LayoutParams unused2 = child.layoutParams = layoutParams;
        child.rowspan = i2;
        this.childrens.add(child);
        if (tL_pageTableCell.rowspan > 1) {
            this.rowSpans.add(new Point((float) i2, (float) (tL_pageTableCell.rowspan + i2)));
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
        return this.childrens.get(index);
    }

    public TableLayout(Context context, TableLayoutDelegate tableLayoutDelegate, TextSelectionHelper.ArticleTextSelectionHelper textSelectionHelper2) {
        super(context);
        this.textSelectionHelper = textSelectionHelper2;
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
        T[] result = (Object[]) Array.newInstance(a.getClass().getComponentType(), a.length + b.length);
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
        boolean isAtEdge = false;
        if (!this.mUseDefaultMargins) {
            return 0;
        }
        Spec spec = horizontal ? p.columnSpec : p.rowSpec;
        Axis axis = horizontal ? this.mHorizontalAxis : this.mVerticalAxis;
        Interval span = spec.span;
        if (!((horizontal && this.isRtl) != leading) ? span.max == axis.getCount() : span.min == 0) {
            isAtEdge = true;
        }
        return getDefaultMargin(c, isAtEdge, horizontal, leading);
    }

    /* access modifiers changed from: package-private */
    public int getMargin1(Child view, boolean horizontal, boolean leading) {
        int margin;
        LayoutParams lp = view.getLayoutParams();
        if (horizontal) {
            margin = leading ? lp.leftMargin : lp.rightMargin;
        } else {
            margin = leading ? lp.topMargin : lp.bottomMargin;
        }
        return margin == Integer.MIN_VALUE ? getDefaultMargin(view, lp, horizontal, leading) : margin;
    }

    private int getMargin(Child view, boolean horizontal, boolean leading) {
        if (this.mAlignmentMode == 1) {
            return getMargin1(view, horizontal, leading);
        }
        Axis axis = horizontal ? this.mHorizontalAxis : this.mVerticalAxis;
        int[] margins = leading ? axis.getLeadingMargins() : axis.getTrailingMargins();
        LayoutParams lp = view.getLayoutParams();
        Interval interval = (horizontal ? lp.columnSpec : lp.rowSpec).span;
        return margins[leading ? interval.min : interval.max];
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
        int N;
        int N2;
        TableLayout tableLayout = this;
        int count = 0;
        boolean horizontal = tableLayout.mOrientation == 0;
        Axis axis = horizontal ? tableLayout.mHorizontalAxis : tableLayout.mVerticalAxis;
        if (axis.definedCount != Integer.MIN_VALUE) {
            count = axis.definedCount;
        }
        int major = 0;
        int minor = 0;
        int[] maxSizes = new int[count];
        int i = 0;
        int N3 = getChildCount();
        while (i < N) {
            LayoutParams lp = tableLayout.getChildAt(i).getLayoutParams();
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
            Axis axis2 = axis;
            int minorSpan = clip(minorRange, minorWasDefined, count);
            if (minorWasDefined) {
                minor = minorRange.min;
            }
            if (count != 0) {
                if (!majorWasDefined || !minorWasDefined) {
                    while (true) {
                        N2 = N;
                        if (fits(maxSizes, major, minor, minor + minorSpan)) {
                            break;
                        } else if (minorWasDefined) {
                            major++;
                            N = N2;
                        } else if (minor + minorSpan <= count) {
                            minor++;
                            N = N2;
                        } else {
                            minor = 0;
                            major++;
                            N = N2;
                        }
                    }
                } else {
                    N2 = N;
                }
                boolean z = minorWasDefined;
                procrusteanFill(maxSizes, minor, minor + minorSpan, major + majorSpan);
            } else {
                N2 = N;
            }
            if (horizontal) {
                setCellGroup(lp, major, majorSpan, minor, minorSpan);
            } else {
                setCellGroup(lp, minor, minorSpan, major, majorSpan);
            }
            minor += minorSpan;
            i++;
            tableLayout = this;
            axis = axis2;
            N3 = N2;
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

    /* access modifiers changed from: private */
    public static void handleInvalidParams(String msg) {
        throw new IllegalArgumentException(msg + ". ");
    }

    private void checkLayoutParams(LayoutParams lp, boolean horizontal) {
        String groupName = horizontal ? "column" : "row";
        Interval span = (horizontal ? lp.columnSpec : lp.rowSpec).span;
        if (span.min != Integer.MIN_VALUE && span.min < 0) {
            handleInvalidParams(groupName + " indices must be positive");
        }
        int count = (horizontal ? this.mHorizontalAxis : this.mVerticalAxis).definedCount;
        if (count != Integer.MIN_VALUE) {
            if (span.max > count) {
                handleInvalidParams(groupName + " indices (start + span) mustn't exceed the " + groupName + " count");
            }
            if (span.size() > count) {
                handleInvalidParams(groupName + " span mustn't exceed the " + groupName + " count");
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int N = getChildCount();
        for (int i = 0; i < N; i++) {
            getChildAt(i).draw(canvas, this);
        }
    }

    private int computeLayoutParamsHashCode() {
        int result = 1;
        int N = getChildCount();
        for (int i = 0; i < N; i++) {
            result = (result * 31) + getChildAt(i).getLayoutParams().hashCode();
        }
        return result;
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

    private void measureChildWithMargins2(Child child, int parentWidthSpec, int parentHeightSpec, int childWidth, int childHeight, boolean first) {
        child.measure(getTotalMargin(child, true) + childWidth, getTotalMargin(child, false) + childHeight, first);
    }

    private void measureChildrenWithMargins(int widthSpec, int heightSpec, boolean firstPass) {
        int maxCellWidth;
        int N = getChildCount();
        for (int i = 0; i < N; i++) {
            Child c = getChildAt(i);
            LayoutParams lp = c.getLayoutParams();
            boolean z = false;
            if (firstPass) {
                int width = View.MeasureSpec.getSize(widthSpec);
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
                if (this.mOrientation == 0) {
                    z = true;
                }
                boolean horizontal = z;
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
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec + delta), View.MeasureSpec.getMode(measureSpec));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthSpec, int heightSpec) {
        int heightSansPadding;
        int widthSansPadding;
        int a;
        Point p;
        int i = widthSpec;
        int i2 = heightSpec;
        consistencyCheck();
        invalidateValues();
        this.colCount = 0;
        int N = getChildCount();
        for (int a2 = 0; a2 < N; a2++) {
            this.colCount = Math.max(this.colCount, getChildAt(a2).layoutParams.columnSpec.span.max);
        }
        measureChildrenWithMargins(i, i2, true);
        if (this.mOrientation == 0) {
            int widthSansPadding2 = this.mHorizontalAxis.getMeasure(i);
            measureChildrenWithMargins(i, i2, false);
            widthSansPadding = widthSansPadding2;
            heightSansPadding = this.mVerticalAxis.getMeasure(i2);
        } else {
            int heightSansPadding2 = this.mVerticalAxis.getMeasure(i2);
            measureChildrenWithMargins(i, i2, false);
            widthSansPadding = this.mHorizontalAxis.getMeasure(i);
            heightSansPadding = heightSansPadding2;
        }
        int measuredWidth = Math.max(widthSansPadding, View.MeasureSpec.getSize(widthSpec));
        int measuredHeight = Math.max(heightSansPadding, getSuggestedMinimumHeight());
        setMeasuredDimension(measuredWidth, measuredHeight);
        this.mHorizontalAxis.layout(measuredWidth);
        this.mVerticalAxis.layout(measuredHeight);
        int[] hLocations = this.mHorizontalAxis.getLocations();
        int[] vLocations = this.mVerticalAxis.getLocations();
        int fixedHeight = measuredHeight;
        this.cellsToFixHeight.clear();
        int measuredWidth2 = hLocations[hLocations.length - 1];
        int N2 = getChildCount();
        int height = 0;
        while (height < N2) {
            Child c = getChildAt(height);
            LayoutParams lp = c.getLayoutParams();
            Spec columnSpec = lp.columnSpec;
            int measuredWidth3 = measuredWidth2;
            Spec rowSpec = lp.rowSpec;
            Interval colSpan = columnSpec.span;
            Interval rowSpan = rowSpec.span;
            LayoutParams layoutParams = lp;
            int x1 = hLocations[colSpan.min];
            int y1 = vLocations[rowSpan.min];
            int cellWidth = hLocations[colSpan.max] - x1;
            int cellHeight = vLocations[rowSpan.max] - y1;
            int pWidth = getMeasurement(c, true);
            int pHeight = getMeasurement(c, false);
            Interval interval = colSpan;
            Alignment hAlign = columnSpec.getAbsoluteAlignment(true);
            Interval interval2 = rowSpan;
            Alignment vAlign = rowSpec.getAbsoluteAlignment(false);
            Bounds boundsX = this.mHorizontalAxis.getGroupBounds().getValue(height);
            Spec spec = columnSpec;
            Bounds boundsY = this.mVerticalAxis.getGroupBounds().getValue(height);
            int i3 = height;
            int gravityOffsetX = hAlign.getGravityOffset(c, cellWidth - boundsX.size(true));
            int gravityOffsetY = vAlign.getGravityOffset(c, cellHeight - boundsY.size(true));
            int leftMargin = getMargin(c, true, true);
            Bounds boundsY2 = boundsY;
            int topMargin = getMargin(c, false, true);
            int rightMargin = getMargin(c, true, false);
            int sumMarginsX = leftMargin + rightMargin;
            int sumMarginsY = topMargin + getMargin(c, false, false);
            Bounds bounds = boundsX;
            Bounds bounds2 = boundsX;
            int N3 = N2;
            int widthSansPadding3 = widthSansPadding;
            int measuredWidth4 = measuredWidth3;
            Spec spec2 = rowSpec;
            int alignmentOffsetX = bounds.getOffset(this, c, hAlign, pWidth + sumMarginsX, true);
            Child c2 = c;
            int alignmentOffsetY = boundsY2.getOffset(this, c2, vAlign, pHeight + sumMarginsY, false);
            int width = hAlign.getSizeInCell(c2, pWidth, cellWidth - sumMarginsX);
            int height2 = vAlign.getSizeInCell(c2, pHeight, cellHeight - sumMarginsY);
            int dx = x1 + gravityOffsetX + alignmentOffsetX;
            int cx = !this.isRtl ? leftMargin + dx : ((measuredWidth4 - width) - rightMargin) - dx;
            int i4 = alignmentOffsetY;
            int alignmentOffsetY2 = y1 + gravityOffsetY + alignmentOffsetY + topMargin;
            if (c2.cell != null) {
                int i5 = dx;
                if (width != c2.getMeasuredWidth() || height2 != c2.getMeasuredHeight()) {
                    c2.measure(width, height2, false);
                }
                if (c2.fixedHeight == 0 || c2.fixedHeight == height2) {
                    int i6 = pHeight;
                    Alignment alignment = hAlign;
                } else {
                    int i7 = pWidth;
                    if (c2.layoutParams.rowSpec.span.max - c2.layoutParams.rowSpec.span.min <= 1) {
                        int a3 = 0;
                        int size = this.rowSpans.size();
                        while (true) {
                            if (a3 >= size) {
                                int i8 = pHeight;
                                Alignment alignment2 = hAlign;
                                p = null;
                                break;
                            }
                            int size2 = size;
                            Point p2 = this.rowSpans.get(a3);
                            int pHeight2 = pHeight;
                            Alignment hAlign2 = hAlign;
                            if (p2.x <= ((float) c2.layoutParams.rowSpec.span.min) && p2.y > ((float) c2.layoutParams.rowSpec.span.min)) {
                                p = 1;
                                break;
                            }
                            a3++;
                            size = size2;
                            pHeight = pHeight2;
                            hAlign = hAlign2;
                        }
                        if (p == null) {
                            this.cellsToFixHeight.add(c2);
                        }
                    } else {
                        Alignment alignment3 = hAlign;
                    }
                }
            } else {
                int i9 = pWidth;
                int i10 = pHeight;
                Alignment alignment4 = hAlign;
            }
            c2.layout(cx, alignmentOffsetY2, cx + width, alignmentOffsetY2 + height2);
            height = i3 + 1;
            int i11 = widthSpec;
            int i12 = heightSpec;
            measuredWidth2 = measuredWidth4;
            widthSansPadding = widthSansPadding3;
            N2 = N3;
        }
        int i13 = height;
        int i14 = N2;
        int i15 = widthSansPadding;
        int measuredWidth5 = measuredWidth2;
        int a4 = 0;
        int N4 = this.cellsToFixHeight.size();
        int fixedHeight2 = fixedHeight;
        while (a4 < N4) {
            Child child = this.cellsToFixHeight.get(a4);
            boolean skip = false;
            int heightDiff = child.measuredHeight - child.fixedHeight;
            int i16 = child.index + 1;
            int size3 = this.childrens.size();
            while (true) {
                if (i16 >= size3) {
                    a = a4;
                    break;
                }
                Child next = this.childrens.get(i16);
                a = a4;
                if (child.layoutParams.rowSpec.span.min != next.layoutParams.rowSpec.span.min) {
                    break;
                } else if (child.fixedHeight < next.fixedHeight) {
                    skip = true;
                    break;
                } else {
                    int diff = next.measuredHeight - next.fixedHeight;
                    if (diff > 0) {
                        heightDiff = Math.min(heightDiff, diff);
                    }
                    i16++;
                    a4 = a;
                }
            }
            if (!skip) {
                int i17 = child.index - 1;
                while (true) {
                    if (i17 < 0) {
                        break;
                    }
                    Child next2 = this.childrens.get(i17);
                    if (child.layoutParams.rowSpec.span.min != next2.layoutParams.rowSpec.span.min) {
                        break;
                    } else if (child.fixedHeight < next2.fixedHeight) {
                        skip = true;
                        break;
                    } else {
                        int diff2 = next2.measuredHeight - next2.fixedHeight;
                        if (diff2 > 0) {
                            heightDiff = Math.min(heightDiff, diff2);
                        }
                        i17--;
                    }
                }
            }
            if (!skip) {
                child.setFixedHeight(child.fixedHeight);
                fixedHeight2 -= heightDiff;
                int size4 = this.childrens.size();
                for (int i18 = 0; i18 < size4; i18++) {
                    Child next3 = this.childrens.get(i18);
                    if (child != next3) {
                        if (child.layoutParams.rowSpec.span.min == next3.layoutParams.rowSpec.span.min) {
                            if (next3.fixedHeight != next3.measuredHeight) {
                                this.cellsToFixHeight.remove(next3);
                                if (next3.index < child.index) {
                                    a--;
                                }
                                N4--;
                            }
                            Child.access$1520(next3, heightDiff);
                            next3.measure(next3.measuredWidth, next3.measuredHeight, true);
                            N4 = N4;
                        } else if (child.layoutParams.rowSpec.span.min < next3.layoutParams.rowSpec.span.min) {
                            next3.y -= heightDiff;
                        }
                    }
                }
            }
            a4 = a + 1;
        }
        int i19 = a4;
        int N5 = getChildCount();
        for (int i20 = 0; i20 < N5; i20++) {
            Child c3 = getChildAt(i20);
            this.delegate.onLayoutChild(c3.textLayout, c3.getTextX(), c3.getTextY());
        }
        setMeasuredDimension(measuredWidth5, fixedHeight2);
    }

    private int getMeasurement(Child c, boolean horizontal) {
        return horizontal ? c.getMeasuredWidth() : c.getMeasuredHeight();
    }

    /* access modifiers changed from: package-private */
    public final int getMeasurementIncludingMargin(Child c, boolean horizontal) {
        return getMeasurement(c, horizontal) + getTotalMargin(c, horizontal);
    }

    public void requestLayout() {
        super.requestLayout();
        invalidateValues();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        consistencyCheck();
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

        private Axis(boolean horizontal2) {
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
            this.horizontal = horizontal2;
        }

        private int calculateMaxIndex() {
            int result = -1;
            int N = TableLayout.this.getChildCount();
            for (int i = 0; i < N; i++) {
                LayoutParams params = TableLayout.this.getChildAt(i).getLayoutParams();
                Interval span = (this.horizontal ? params.columnSpec : params.rowSpec).span;
                result = Math.max(Math.max(Math.max(result, span.min), span.max), span.size());
            }
            if (result == -1) {
                return Integer.MIN_VALUE;
            }
            return result;
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
                StringBuilder sb = new StringBuilder();
                sb.append(this.horizontal ? "column" : "row");
                sb.append("Count must be greater than or equal to the maximum of all grid indices (and spans) defined in the LayoutParams of each child");
                TableLayout.handleInvalidParams(sb.toString());
            }
            this.definedCount = count;
        }

        public boolean isOrderPreserved() {
            return this.orderPreserved;
        }

        public void setOrderPreserved(boolean orderPreserved2) {
            this.orderPreserved = orderPreserved2;
            invalidateStructure();
        }

        private PackedMap<Spec, Bounds> createGroupBounds() {
            Assoc<Spec, Bounds> assoc = Assoc.of(Spec.class, Bounds.class);
            int N = TableLayout.this.getChildCount();
            for (int i = 0; i < N; i++) {
                LayoutParams lp = TableLayout.this.getChildAt(i).getLayoutParams();
                Spec spec = this.horizontal ? lp.columnSpec : lp.rowSpec;
                assoc.put(spec, spec.getAbsoluteAlignment(this.horizontal).getBounds());
            }
            return assoc.pack();
        }

        private void computeGroupBounds() {
            Bounds[] values = (Bounds[]) this.groupBounds.values;
            for (Bounds reset : values) {
                reset.reset();
            }
            int N = TableLayout.this.getChildCount();
            for (int i = 0; i < N; i++) {
                Child c = TableLayout.this.getChildAt(i);
                LayoutParams lp = c.getLayoutParams();
                Spec spec = this.horizontal ? lp.columnSpec : lp.rowSpec;
                this.groupBounds.getValue(i).include(TableLayout.this, c, spec, this, TableLayout.this.getMeasurementIncludingMargin(c, this.horizontal) + (spec.weight == 0.0f ? 0 : this.deltas[i]));
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
            Assoc<Interval, MutableInt> result = Assoc.of(Interval.class, MutableInt.class);
            Spec[] keys = (Spec[]) getGroupBounds().keys;
            int N = keys.length;
            for (int i = 0; i < N; i++) {
                result.put(min ? keys[i].span : keys[i].span.inverse(), new MutableInt());
            }
            return result.pack();
        }

        private void computeLinks(PackedMap<Interval, MutableInt> links, boolean min) {
            MutableInt[] spans = (MutableInt[]) links.values;
            for (MutableInt reset : spans) {
                reset.reset();
            }
            Bounds[] bounds = (Bounds[]) getGroupBounds().values;
            for (int i = 0; i < bounds.length; i++) {
                int size = bounds[i].size(min);
                MutableInt valueHolder = links.getValue(i);
                valueHolder.value = Math.max(valueHolder.value, min ? size : -size);
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

        private void include(List<Arc> arcs2, Interval key, MutableInt size, boolean ignoreIfAlreadyPresent) {
            if (key.size() != 0) {
                if (ignoreIfAlreadyPresent) {
                    for (Arc arc : arcs2) {
                        if (arc.span.equals(key)) {
                            return;
                        }
                    }
                }
                arcs2.add(new Arc(key, size));
            }
        }

        private void include(List<Arc> arcs2, Interval key, MutableInt size) {
            include(arcs2, key, size, true);
        }

        /* access modifiers changed from: package-private */
        public Arc[][] groupArcsByFirstVertex(Arc[] arcs2) {
            int N = getCount() + 1;
            Arc[][] result = new Arc[N][];
            int[] sizes = new int[N];
            for (Arc arc : arcs2) {
                int i = arc.span.min;
                sizes[i] = sizes[i] + 1;
            }
            for (int i2 = 0; i2 < sizes.length; i2++) {
                result[i2] = new Arc[sizes[i2]];
            }
            Arrays.fill(sizes, 0);
            for (Arc arc2 : arcs2) {
                int i3 = arc2.span.min;
                Arc[] arcArr = result[i3];
                int i4 = sizes[i3];
                sizes[i3] = i4 + 1;
                arcArr[i4] = arc2;
            }
            return result;
        }

        private Arc[] topologicalSort(Arc[] arcs2) {
            return new Object(arcs2) {
                Arc[][] arcsByVertex;
                int cursor;
                Arc[] result;
                final /* synthetic */ Arc[] val$arcs;
                int[] visited;

                {
                    this.val$arcs = r3;
                    Arc[] arcArr = new Arc[r3.length];
                    this.result = arcArr;
                    this.cursor = arcArr.length - 1;
                    this.arcsByVertex = Axis.this.groupArcsByFirstVertex(r3);
                    this.visited = new int[(Axis.this.getCount() + 1)];
                }

                /* access modifiers changed from: package-private */
                public void walk(int loc) {
                    int[] iArr = this.visited;
                    switch (iArr[loc]) {
                        case 0:
                            iArr[loc] = 1;
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

                /* access modifiers changed from: package-private */
                public Arc[] sort() {
                    int N = this.arcsByVertex.length;
                    for (int loc = 0; loc < N; loc++) {
                        walk(loc);
                    }
                    return this.result;
                }
            }.sort();
        }

        private Arc[] topologicalSort(List<Arc> arcs2) {
            return topologicalSort((Arc[]) arcs2.toArray(new Arc[0]));
        }

        private void addComponentSizes(List<Arc> result, PackedMap<Interval, MutableInt> links) {
            for (int i = 0; i < ((Interval[]) links.keys).length; i++) {
                include(result, ((Interval[]) links.keys)[i], ((MutableInt[]) links.values)[i], false);
            }
        }

        private Arc[] createArcs() {
            List<Arc> mins = new ArrayList<>();
            List<Arc> maxs = new ArrayList<>();
            addComponentSizes(mins, getForwardLinks());
            addComponentSizes(maxs, getBackwardLinks());
            if (this.orderPreserved) {
                for (int i = 0; i < getCount(); i++) {
                    include(mins, new Interval(i, i + 1), new MutableInt(0));
                }
            }
            int i2 = getCount();
            include(mins, new Interval(0, i2), this.parentMin, false);
            include(maxs, new Interval(i2, 0), this.parentMax, false);
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

        private boolean relax(int[] locations2, Arc entry) {
            if (!entry.valid) {
                return false;
            }
            Interval span = entry.span;
            int u = span.min;
            int v = span.max;
            int candidate = locations2[u] + entry.value.value;
            if (candidate <= locations2[v]) {
                return false;
            }
            locations2[v] = candidate;
            return true;
        }

        private void init(int[] locations2) {
            Arrays.fill(locations2, 0);
        }

        private boolean solve(Arc[] arcs2, int[] locations2) {
            return solve(arcs2, locations2, true);
        }

        private boolean solve(Arc[] arcs2, int[] locations2, boolean modifyOnError) {
            int N = getCount() + 1;
            for (int p = 0; p < arcs2.length; p++) {
                init(locations2);
                for (int i = 0; i < N; i++) {
                    boolean changed = false;
                    for (Arc relax : arcs2) {
                        changed |= relax(locations2, relax);
                    }
                    if (!changed) {
                        return true;
                    }
                }
                if (!modifyOnError) {
                    return false;
                }
                boolean[] culprits = new boolean[arcs2.length];
                for (int i2 = 0; i2 < N; i2++) {
                    int length = arcs2.length;
                    for (int j = 0; j < length; j++) {
                        culprits[j] = culprits[j] | relax(locations2, arcs2[j]);
                    }
                }
                int i3 = 0;
                while (true) {
                    if (i3 >= arcs2.length) {
                        break;
                    }
                    if (culprits[i3]) {
                        Arc arc = arcs2[i3];
                        if (arc.span.min >= arc.span.max) {
                            arc.valid = false;
                            break;
                        }
                    }
                    i3++;
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
            if (!hasWeights()) {
                solve(a);
            } else {
                solveAndDistributeSpace(a);
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
            if (this.locationsValid == 0) {
                computeLocations(this.locations);
                this.locationsValid = true;
            }
            return this.locations;
        }

        private int size(int[] locations2) {
            return locations2[getCount()];
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
            int mode = View.MeasureSpec.getMode(measureSpec);
            int size = View.MeasureSpec.getSize(measureSpec);
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

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        private static final int DEFAULT_HEIGHT = -2;
        private static final int DEFAULT_MARGIN = Integer.MIN_VALUE;
        private static final Interval DEFAULT_SPAN;
        private static final int DEFAULT_SPAN_SIZE;
        private static final int DEFAULT_WIDTH = -2;
        public Spec columnSpec;
        public Spec rowSpec;

        static {
            Interval interval = new Interval(Integer.MIN_VALUE, -NUM);
            DEFAULT_SPAN = interval;
            DEFAULT_SPAN_SIZE = interval.size();
        }

        private LayoutParams(int width, int height, int left, int top, int right, int bottom, Spec rowSpec2, Spec columnSpec2) {
            super(width, height);
            this.rowSpec = Spec.UNDEFINED;
            this.columnSpec = Spec.UNDEFINED;
            setMargins(left, top, right, bottom);
            this.rowSpec = rowSpec2;
            this.columnSpec = columnSpec2;
        }

        public LayoutParams(Spec rowSpec2, Spec columnSpec2) {
            this(-2, -2, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, rowSpec2, columnSpec2);
        }

        public LayoutParams() {
            this(Spec.UNDEFINED, Spec.UNDEFINED);
        }

        public LayoutParams(ViewGroup.LayoutParams params) {
            super(params);
            this.rowSpec = Spec.UNDEFINED;
            this.columnSpec = Spec.UNDEFINED;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams params) {
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

        /* access modifiers changed from: package-private */
        public final void setRowSpecSpan(Interval span) {
            this.rowSpec = this.rowSpec.copyWriteSpan(span);
        }

        /* access modifiers changed from: package-private */
        public final void setColumnSpecSpan(Interval span) {
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
            if (this.columnSpec.equals(that.columnSpec) && this.rowSpec.equals(that.rowSpec)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return (this.rowSpec.hashCode() * 31) + this.columnSpec.hashCode();
        }
    }

    static final class Arc {
        public final Interval span;
        public boolean valid = true;
        public final MutableInt value;

        public Arc(Interval span2, MutableInt value2) {
            this.span = span2;
            this.value = value2;
        }
    }

    static final class MutableInt {
        public int value;

        public MutableInt() {
            reset();
        }

        public MutableInt(int value2) {
            this.value = value2;
        }

        public void reset() {
            this.value = Integer.MIN_VALUE;
        }
    }

    static final class Assoc<K, V> extends ArrayList<Pair<K, V>> {
        private final Class<K> keyType;
        private final Class<V> valueType;

        private Assoc(Class<K> keyType2, Class<V> valueType2) {
            this.keyType = keyType2;
            this.valueType = valueType2;
        }

        public static <K, V> Assoc<K, V> of(Class<K> keyType2, Class<V> valueType2) {
            return new Assoc<>(keyType2, valueType2);
        }

        public void put(K key, V value) {
            add(Pair.create(key, value));
        }

        public PackedMap<K, V> pack() {
            int N = size();
            K[] keys = (Object[]) Array.newInstance(this.keyType, N);
            V[] values = (Object[]) Array.newInstance(this.valueType, N);
            for (int i = 0; i < N; i++) {
                keys[i] = ((Pair) get(i)).first;
                values[i] = ((Pair) get(i)).second;
            }
            return new PackedMap<>(keys, values);
        }
    }

    static final class PackedMap<K, V> {
        public final int[] index;
        public final K[] keys;
        public final V[] values;

        private PackedMap(K[] keys2, V[] values2) {
            int[] createIndex = createIndex(keys2);
            this.index = createIndex;
            this.keys = compact(keys2, createIndex);
            this.values = compact(values2, createIndex);
        }

        public V getValue(int i) {
            return this.values[this.index[i]];
        }

        private static <K> int[] createIndex(K[] keys2) {
            int size = keys2.length;
            int[] result = new int[size];
            Map<K, Integer> keyToIndex = new HashMap<>();
            for (int i = 0; i < size; i++) {
                K key = keys2[i];
                Integer index2 = keyToIndex.get(key);
                if (index2 == null) {
                    index2 = Integer.valueOf(keyToIndex.size());
                    keyToIndex.put(key, index2);
                }
                result[i] = index2.intValue();
            }
            return result;
        }

        private static <K> K[] compact(K[] a, int[] index2) {
            int size = a.length;
            K[] result = (Object[]) Array.newInstance(a.getClass().getComponentType(), TableLayout.max2(index2, -1) + 1);
            for (int i = 0; i < size; i++) {
                result[index2[i]] = a[i];
            }
            return result;
        }
    }

    static class Bounds {
        public int after;
        public int before;
        public int flexibility;

        private Bounds() {
            reset();
        }

        /* access modifiers changed from: protected */
        public void reset() {
            this.before = Integer.MIN_VALUE;
            this.after = Integer.MIN_VALUE;
            this.flexibility = 2;
        }

        /* access modifiers changed from: protected */
        public void include(int before2, int after2) {
            this.before = Math.max(this.before, before2);
            this.after = Math.max(this.after, after2);
        }

        /* access modifiers changed from: protected */
        public int size(boolean min) {
            if (min || !TableLayout.canStretch(this.flexibility)) {
                return this.before + this.after;
            }
            return 100000;
        }

        /* access modifiers changed from: protected */
        public int getOffset(TableLayout gl, Child c, Alignment a, int size, boolean horizontal) {
            return this.before - a.getAlignmentValue(c, size);
        }

        /* access modifiers changed from: protected */
        public final void include(TableLayout gl, Child c, Spec spec, Axis axis, int size) {
            this.flexibility &= spec.getFlexibility();
            boolean z = axis.horizontal;
            int before2 = spec.getAbsoluteAlignment(axis.horizontal).getAlignmentValue(c, size);
            include(before2, size - before2);
        }
    }

    static final class Interval {
        public final int max;
        public final int min;

        public Interval(int min2, int max2) {
            this.min = min2;
            this.max = max2;
        }

        /* access modifiers changed from: package-private */
        public int size() {
            return this.max - this.min;
        }

        /* access modifiers changed from: package-private */
        public Interval inverse() {
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
            if (this.max == interval.max && this.min == interval.min) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return (this.min * 31) + this.max;
        }
    }

    public static class Spec {
        static final float DEFAULT_WEIGHT = 0.0f;
        static final Spec UNDEFINED = TableLayout.spec(Integer.MIN_VALUE);
        final Alignment alignment;
        final Interval span;
        final boolean startDefined;
        float weight;

        private Spec(boolean startDefined2, Interval span2, Alignment alignment2, float weight2) {
            this.startDefined = startDefined2;
            this.span = span2;
            this.alignment = alignment2;
            this.weight = weight2;
        }

        private Spec(boolean startDefined2, int start, int size, Alignment alignment2, float weight2) {
            this(startDefined2, new Interval(start, start + size), alignment2, weight2);
        }

        /* access modifiers changed from: private */
        public Alignment getAbsoluteAlignment(boolean horizontal) {
            if (this.alignment != TableLayout.UNDEFINED_ALIGNMENT) {
                return this.alignment;
            }
            if (this.weight == 0.0f) {
                return horizontal ? TableLayout.START : TableLayout.BASELINE;
            }
            return TableLayout.FILL;
        }

        /* access modifiers changed from: package-private */
        public final Spec copyWriteSpan(Interval span2) {
            return new Spec(this.startDefined, span2, this.alignment, this.weight);
        }

        /* access modifiers changed from: package-private */
        public final Spec copyWriteAlignment(Alignment alignment2) {
            return new Spec(this.startDefined, this.span, alignment2, this.weight);
        }

        /* access modifiers changed from: package-private */
        public final int getFlexibility() {
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
            if (this.alignment.equals(spec.alignment) && this.span.equals(spec.span)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return (this.span.hashCode() * 31) + this.alignment.hashCode();
        }
    }

    public static Spec spec(int start, int size, Alignment alignment, float weight) {
        return new Spec(start != Integer.MIN_VALUE, start, size, alignment, weight);
    }

    public static Spec spec(int start, Alignment alignment, float weight) {
        return spec(start, 1, alignment, weight);
    }

    public static Spec spec(int start, int size, float weight) {
        return spec(start, size, UNDEFINED_ALIGNMENT, weight);
    }

    public static Spec spec(int start, float weight) {
        return spec(start, 1, weight);
    }

    public static Spec spec(int start, int size, Alignment alignment) {
        return spec(start, size, alignment, 0.0f);
    }

    public static Spec spec(int start, Alignment alignment) {
        return spec(start, 1, alignment);
    }

    public static Spec spec(int start, int size) {
        return spec(start, size, UNDEFINED_ALIGNMENT);
    }

    public static Spec spec(int start) {
        return spec(start, 1);
    }

    public static abstract class Alignment {
        /* access modifiers changed from: package-private */
        public abstract int getAlignmentValue(Child child, int i);

        /* access modifiers changed from: package-private */
        public abstract int getGravityOffset(Child child, int i);

        Alignment() {
        }

        /* access modifiers changed from: package-private */
        public int getSizeInCell(Child view, int viewSize, int cellSize) {
            return viewSize;
        }

        /* access modifiers changed from: package-private */
        public Bounds getBounds() {
            return new Bounds();
        }
    }

    static {
        AnonymousClass2 r0 = new Alignment() {
            /* access modifiers changed from: package-private */
            public int getGravityOffset(Child view, int cellDelta) {
                return 0;
            }

            public int getAlignmentValue(Child view, int viewSize) {
                return 0;
            }
        };
        LEADING = r0;
        AnonymousClass3 r1 = new Alignment() {
            /* access modifiers changed from: package-private */
            public int getGravityOffset(Child view, int cellDelta) {
                return cellDelta;
            }

            public int getAlignmentValue(Child view, int viewSize) {
                return viewSize;
            }
        };
        TRAILING = r1;
        TOP = r0;
        BOTTOM = r1;
        START = r0;
        END = r1;
        LEFT = createSwitchingAlignment(r0);
        RIGHT = createSwitchingAlignment(r1);
    }

    private static Alignment createSwitchingAlignment(final Alignment ltr) {
        return new Alignment() {
            /* access modifiers changed from: package-private */
            public int getGravityOffset(Child view, int cellDelta) {
                return Alignment.this.getGravityOffset(view, cellDelta);
            }

            public int getAlignmentValue(Child view, int viewSize) {
                return Alignment.this.getAlignmentValue(view, viewSize);
            }
        };
    }

    static boolean canStretch(int flexibility) {
        return (flexibility & 2) != 0;
    }
}
