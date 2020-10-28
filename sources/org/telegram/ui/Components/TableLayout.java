package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import j$.lang.Iterable;
import j$.util.Collection;
import j$.util.List;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.Predicate;
import j$.util.function.UnaryOperator;
import j$.util.stream.Stream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.TLRPC$TL_pageTableCell;
import org.telegram.ui.ArticleViewer;
import org.telegram.ui.Cells.TextSelectionHelper;

public class TableLayout extends View {
    public static final Alignment BASELINE = new Alignment() {
        public int getAlignmentValue(Child child, int i) {
            return Integer.MIN_VALUE;
        }

        /* access modifiers changed from: package-private */
        public int getGravityOffset(Child child, int i) {
            return 0;
        }

        public Bounds getBounds() {
            return new Bounds(this) {
                private int size;

                /* access modifiers changed from: protected */
                public void reset() {
                    super.reset();
                    this.size = Integer.MIN_VALUE;
                }

                /* access modifiers changed from: protected */
                public void include(int i, int i2) {
                    super.include(i, i2);
                    this.size = Math.max(this.size, i + i2);
                }

                /* access modifiers changed from: protected */
                public int size(boolean z) {
                    return Math.max(super.size(z), this.size);
                }

                /* access modifiers changed from: protected */
                public int getOffset(TableLayout tableLayout, Child child, Alignment alignment, int i, boolean z) {
                    return Math.max(0, super.getOffset(tableLayout, child, alignment, i, z));
                }
            };
        }
    };
    public static final Alignment END;
    public static final Alignment FILL = new Alignment() {
        public int getAlignmentValue(Child child, int i) {
            return Integer.MIN_VALUE;
        }

        /* access modifiers changed from: package-private */
        public int getGravityOffset(Child child, int i) {
            return 0;
        }

        public int getSizeInCell(Child child, int i, int i2) {
            return i2;
        }
    };
    private static final Alignment LEADING;
    public static final Alignment START;
    private static final Alignment TRAILING;
    static final Alignment UNDEFINED_ALIGNMENT = new Alignment() {
        public int getAlignmentValue(Child child, int i) {
            return Integer.MIN_VALUE;
        }

        /* access modifiers changed from: package-private */
        public int getGravityOffset(Child child, int i) {
            return Integer.MIN_VALUE;
        }
    };
    /* access modifiers changed from: private */
    public Path backgroundPath;
    private ArrayList<Child> cellsToFixHeight = new ArrayList<>();
    private ArrayList<Child> childrens;
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
    private int mAlignmentMode = 1;
    private int mDefaultGap;
    private final Axis mHorizontalAxis = new Axis(true);
    private int mLastLayoutParamsHashCode = 0;
    private int mOrientation = 0;
    private boolean mUseDefaultMargins = false;
    private final Axis mVerticalAxis = new Axis(false);
    /* access modifiers changed from: private */
    public float[] radii;
    /* access modifiers changed from: private */
    public RectF rect;
    private ArrayList<Point> rowSpans = new ArrayList<>();
    /* access modifiers changed from: private */
    public TextSelectionHelper.ArticleTextSelectionHelper textSelectionHelper;

    public interface TableLayoutDelegate {
        ArticleViewer.DrawingText createTextLayout(TLRPC$TL_pageTableCell tLRPC$TL_pageTableCell, int i);

        Paint getHeaderPaint();

        Paint getLinePaint();

        Paint getStripPaint();

        void onLayoutChild(ArticleViewer.DrawingText drawingText, int i, int i2);
    }

    static boolean canStretch(int i) {
        return (i & 2) != 0;
    }

    static /* synthetic */ void access$1800(String str) {
        handleInvalidParams(str);
        throw null;
    }

    public class Child {
        /* access modifiers changed from: private */
        public TLRPC$TL_pageTableCell cell;
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

        /* JADX WARNING: Code restructure failed: missing block: B:22:0x0048, code lost:
            if (r2.align_right == false) goto L_0x0071;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void measure(int r2, int r3, boolean r4) {
            /*
                r1 = this;
                r1.measuredWidth = r2
                r1.measuredHeight = r3
                if (r4 == 0) goto L_0x0008
                r1.fixedHeight = r3
            L_0x0008:
                org.telegram.tgnet.TLRPC$TL_pageTableCell r2 = r1.cell
                if (r2 == 0) goto L_0x00b4
                boolean r0 = r2.valign_middle
                if (r0 == 0) goto L_0x0018
                int r2 = r1.textHeight
                int r3 = r3 - r2
                int r3 = r3 / 2
                r1.textY = r3
                goto L_0x0031
            L_0x0018:
                boolean r2 = r2.valign_bottom
                if (r2 == 0) goto L_0x0029
                int r2 = r1.textHeight
                int r3 = r3 - r2
                org.telegram.ui.Components.TableLayout r2 = org.telegram.ui.Components.TableLayout.this
                int r2 = r2.itemPaddingTop
                int r3 = r3 - r2
                r1.textY = r3
                goto L_0x0031
            L_0x0029:
                org.telegram.ui.Components.TableLayout r2 = org.telegram.ui.Components.TableLayout.this
                int r2 = r2.itemPaddingTop
                r1.textY = r2
            L_0x0031:
                org.telegram.ui.ArticleViewer$DrawingText r2 = r1.textLayout
                if (r2 == 0) goto L_0x00b4
                int r2 = r2.getLineCount()
                if (r4 != 0) goto L_0x0071
                r3 = 1
                if (r2 > r3) goto L_0x004a
                if (r2 <= 0) goto L_0x0071
                org.telegram.tgnet.TLRPC$TL_pageTableCell r2 = r1.cell
                boolean r3 = r2.align_center
                if (r3 != 0) goto L_0x004a
                boolean r2 = r2.align_right
                if (r2 == 0) goto L_0x0071
            L_0x004a:
                org.telegram.ui.Components.TableLayout r2 = org.telegram.ui.Components.TableLayout.this
                org.telegram.ui.Components.TableLayout$TableLayoutDelegate r2 = r2.delegate
                org.telegram.tgnet.TLRPC$TL_pageTableCell r3 = r1.cell
                int r4 = r1.measuredWidth
                org.telegram.ui.Components.TableLayout r0 = org.telegram.ui.Components.TableLayout.this
                int r0 = r0.itemPaddingLeft
                int r0 = r0 * 2
                int r4 = r4 - r0
                org.telegram.ui.ArticleViewer$DrawingText r2 = r2.createTextLayout(r3, r4)
                r1.setTextLayout(r2)
                int r2 = r1.textHeight
                org.telegram.ui.Components.TableLayout r3 = org.telegram.ui.Components.TableLayout.this
                int r3 = r3.itemPaddingTop
                int r3 = r3 * 2
                int r2 = r2 + r3
                r1.fixedHeight = r2
            L_0x0071:
                int r2 = r1.textLeft
                if (r2 == 0) goto L_0x00ac
                int r2 = -r2
                r1.textX = r2
                org.telegram.tgnet.TLRPC$TL_pageTableCell r3 = r1.cell
                boolean r4 = r3.align_right
                if (r4 == 0) goto L_0x008e
                int r3 = r1.measuredWidth
                int r4 = r1.textWidth
                int r3 = r3 - r4
                org.telegram.ui.Components.TableLayout r4 = org.telegram.ui.Components.TableLayout.this
                int r4 = r4.itemPaddingLeft
                int r3 = r3 - r4
                int r2 = r2 + r3
                r1.textX = r2
                goto L_0x00b4
            L_0x008e:
                boolean r3 = r3.align_center
                if (r3 == 0) goto L_0x00a2
                int r3 = r1.measuredWidth
                int r4 = r1.textWidth
                int r3 = r3 - r4
                int r3 = r3 / 2
                float r3 = (float) r3
                int r3 = java.lang.Math.round(r3)
                int r2 = r2 + r3
                r1.textX = r2
                goto L_0x00b4
            L_0x00a2:
                org.telegram.ui.Components.TableLayout r3 = org.telegram.ui.Components.TableLayout.this
                int r3 = r3.itemPaddingLeft
                int r2 = r2 + r3
                r1.textX = r2
                goto L_0x00b4
            L_0x00ac:
                org.telegram.ui.Components.TableLayout r2 = org.telegram.ui.Components.TableLayout.this
                int r2 = r2.itemPaddingLeft
                r1.textX = r2
            L_0x00b4:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TableLayout.Child.measure(int, int, boolean):void");
        }

        public void setTextLayout(ArticleViewer.DrawingText drawingText) {
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
            int i2 = this.fixedHeight;
            this.measuredHeight = i2;
            TLRPC$TL_pageTableCell tLRPC$TL_pageTableCell = this.cell;
            if (tLRPC$TL_pageTableCell.valign_middle) {
                this.textY = (i2 - this.textHeight) / 2;
            } else if (tLRPC$TL_pageTableCell.valign_bottom) {
                this.textY = (i2 - this.textHeight) - TableLayout.this.itemPaddingTop;
            }
        }

        public void draw(Canvas canvas) {
            float f;
            float f2;
            float f3;
            int i;
            if (this.cell != null) {
                boolean z = false;
                boolean z2 = true;
                boolean z3 = this.x + this.measuredWidth == TableLayout.this.getMeasuredWidth();
                boolean z4 = this.y + this.measuredHeight == TableLayout.this.getMeasuredHeight();
                int dp = AndroidUtilities.dp(3.0f);
                if (this.cell.header || (TableLayout.this.isStriped && this.layoutParams.rowSpec.span.min % 2 == 0)) {
                    if (this.x == 0 && this.y == 0) {
                        float[] access$500 = TableLayout.this.radii;
                        float f4 = (float) dp;
                        TableLayout.this.radii[1] = f4;
                        access$500[0] = f4;
                        z = true;
                    } else {
                        float[] access$5002 = TableLayout.this.radii;
                        TableLayout.this.radii[1] = 0.0f;
                        access$5002[0] = 0.0f;
                    }
                    if (!z3 || this.y != 0) {
                        float[] access$5003 = TableLayout.this.radii;
                        TableLayout.this.radii[3] = 0.0f;
                        access$5003[2] = 0.0f;
                    } else {
                        float[] access$5004 = TableLayout.this.radii;
                        float f5 = (float) dp;
                        TableLayout.this.radii[3] = f5;
                        access$5004[2] = f5;
                        z = true;
                    }
                    if (!z3 || !z4) {
                        float[] access$5005 = TableLayout.this.radii;
                        TableLayout.this.radii[5] = 0.0f;
                        access$5005[4] = 0.0f;
                    } else {
                        float[] access$5006 = TableLayout.this.radii;
                        float f6 = (float) dp;
                        TableLayout.this.radii[5] = f6;
                        access$5006[4] = f6;
                        z = true;
                    }
                    if (this.x != 0 || !z4) {
                        float[] access$5007 = TableLayout.this.radii;
                        TableLayout.this.radii[7] = 0.0f;
                        access$5007[6] = 0.0f;
                        z2 = z;
                    } else {
                        float[] access$5008 = TableLayout.this.radii;
                        float f7 = (float) dp;
                        TableLayout.this.radii[7] = f7;
                        access$5008[6] = f7;
                    }
                    if (z2) {
                        RectF access$600 = TableLayout.this.rect;
                        int i2 = this.x;
                        int i3 = this.y;
                        access$600.set((float) i2, (float) i3, (float) (i2 + this.measuredWidth), (float) (i3 + this.measuredHeight));
                        TableLayout.this.backgroundPath.reset();
                        TableLayout.this.backgroundPath.addRoundRect(TableLayout.this.rect, TableLayout.this.radii, Path.Direction.CW);
                        if (this.cell.header) {
                            canvas.drawPath(TableLayout.this.backgroundPath, TableLayout.this.delegate.getHeaderPaint());
                        } else {
                            canvas.drawPath(TableLayout.this.backgroundPath, TableLayout.this.delegate.getStripPaint());
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
                    canvas.translate((float) getTextX(), (float) getTextY());
                    if (this.selectionIndex >= 0) {
                        TableLayout.this.textSelectionHelper.draw(canvas, (TextSelectionHelper.ArticleSelectableView) TableLayout.this.getParent().getParent(), this.selectionIndex);
                    }
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                if (TableLayout.this.drawLines) {
                    Paint linePaint = TableLayout.this.delegate.getLinePaint();
                    Paint linePaint2 = TableLayout.this.delegate.getLinePaint();
                    float strokeWidth = linePaint.getStrokeWidth() / 2.0f;
                    float strokeWidth2 = linePaint2.getStrokeWidth() / 2.0f;
                    int i8 = this.x;
                    if (i8 == 0) {
                        int i9 = this.y;
                        float f8 = (float) i9;
                        float f9 = (float) (this.measuredHeight + i9);
                        if (i9 == 0) {
                            f8 += (float) dp;
                        }
                        float var_ = f8;
                        if (f9 == ((float) TableLayout.this.getMeasuredHeight())) {
                            f9 -= (float) dp;
                        }
                        int i10 = this.x;
                        canvas.drawLine(((float) i10) + strokeWidth, var_, ((float) i10) + strokeWidth, f9, linePaint);
                    } else {
                        int i11 = this.y;
                        canvas.drawLine(((float) i8) - strokeWidth2, (float) i11, ((float) i8) - strokeWidth2, (float) (i11 + this.measuredHeight), linePaint2);
                    }
                    int i12 = this.y;
                    if (i12 == 0) {
                        int i13 = this.x;
                        float var_ = (float) i13;
                        float var_ = (float) (this.measuredWidth + i13);
                        if (i13 == 0) {
                            var_ += (float) dp;
                        }
                        float var_ = var_;
                        if (var_ == ((float) TableLayout.this.getMeasuredWidth())) {
                            var_ -= (float) dp;
                        }
                        int i14 = this.y;
                        canvas.drawLine(var_, ((float) i14) + strokeWidth, var_, ((float) i14) + strokeWidth, linePaint);
                    } else {
                        int i15 = this.x;
                        canvas.drawLine((float) i15, ((float) i12) - strokeWidth2, (float) (i15 + this.measuredWidth), ((float) i12) - strokeWidth2, linePaint2);
                    }
                    if (!z3 || (i = this.y) != 0) {
                        f = ((float) this.y) - strokeWidth;
                    } else {
                        f = (float) (i + dp);
                    }
                    float var_ = f;
                    if (!z3 || !z4) {
                        f2 = ((float) (this.y + this.measuredHeight)) - strokeWidth;
                    } else {
                        f2 = (float) ((this.y + this.measuredHeight) - dp);
                    }
                    float var_ = f2;
                    int i16 = this.x;
                    int i17 = this.measuredWidth;
                    canvas.drawLine(((float) (i16 + i17)) - strokeWidth, var_, ((float) (i16 + i17)) - strokeWidth, var_, linePaint);
                    int i18 = this.x;
                    float var_ = (i18 != 0 || !z4) ? ((float) i18) - strokeWidth : (float) (i18 + dp);
                    if (!z3 || !z4) {
                        f3 = ((float) (i18 + this.measuredWidth)) - strokeWidth;
                    } else {
                        f3 = (float) ((i18 + this.measuredWidth) - dp);
                    }
                    float var_ = f3;
                    int i19 = this.y;
                    int i20 = this.measuredHeight;
                    canvas.drawLine(var_, ((float) (i19 + i20)) - strokeWidth, var_, ((float) (i19 + i20)) - strokeWidth, linePaint);
                    if (this.x == 0 && this.y == 0) {
                        RectF access$6002 = TableLayout.this.rect;
                        int i21 = this.x;
                        int i22 = this.y;
                        float var_ = (float) (dp * 2);
                        access$6002.set(((float) i21) + strokeWidth, ((float) i22) + strokeWidth, ((float) i21) + strokeWidth + var_, ((float) i22) + strokeWidth + var_);
                        canvas.drawArc(TableLayout.this.rect, -180.0f, 90.0f, false, linePaint);
                    }
                    if (z3 && this.y == 0) {
                        RectF access$6003 = TableLayout.this.rect;
                        int i23 = this.x;
                        int i24 = this.measuredWidth;
                        float var_ = (float) (dp * 2);
                        int i25 = this.y;
                        access$6003.set((((float) (i23 + i24)) - strokeWidth) - var_, ((float) i25) + strokeWidth, ((float) (i23 + i24)) - strokeWidth, ((float) i25) + strokeWidth + var_);
                        canvas.drawArc(TableLayout.this.rect, 0.0f, -90.0f, false, linePaint);
                    }
                    if (this.x == 0 && z4) {
                        RectF access$6004 = TableLayout.this.rect;
                        int i26 = this.x;
                        int i27 = this.y;
                        int i28 = this.measuredHeight;
                        float var_ = (float) (dp * 2);
                        access$6004.set(((float) i26) + strokeWidth, (((float) (i27 + i28)) - strokeWidth) - var_, ((float) i26) + strokeWidth + var_, ((float) (i27 + i28)) - strokeWidth);
                        canvas.drawArc(TableLayout.this.rect, 180.0f, -90.0f, false, linePaint);
                    }
                    if (z3 && z4) {
                        RectF access$6005 = TableLayout.this.rect;
                        int i29 = this.x;
                        int i30 = this.measuredWidth;
                        float var_ = (float) (dp * 2);
                        int i31 = this.y;
                        int i32 = this.measuredHeight;
                        access$6005.set((((float) (i29 + i30)) - strokeWidth) - var_, (((float) (i31 + i32)) - strokeWidth) - var_, ((float) (i29 + i30)) - strokeWidth, ((float) (i31 + i32)) - strokeWidth);
                        canvas.drawArc(TableLayout.this.rect, 0.0f, 90.0f, false, linePaint);
                    }
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

    public void addChild(int i, int i2, int i3, int i4) {
        int i5 = i;
        int i6 = i2;
        Child child = new Child(this.childrens.size());
        LayoutParams layoutParams = new LayoutParams();
        Interval interval = new Interval(i6, i6 + i4);
        Alignment alignment = FILL;
        layoutParams.rowSpec = new Spec(false, interval, alignment, 0.0f);
        layoutParams.columnSpec = new Spec(false, new Interval(i5, i5 + i3), alignment, 0.0f);
        LayoutParams unused = child.layoutParams = layoutParams;
        child.rowspan = i6;
        this.childrens.add(child);
        invalidateStructure();
    }

    public void addChild(TLRPC$TL_pageTableCell tLRPC$TL_pageTableCell, int i, int i2, int i3) {
        TLRPC$TL_pageTableCell tLRPC$TL_pageTableCell2 = tLRPC$TL_pageTableCell;
        int i4 = i;
        int i5 = i2;
        int i6 = i3 == 0 ? 1 : i3;
        Child child = new Child(this.childrens.size());
        TLRPC$TL_pageTableCell unused = child.cell = tLRPC$TL_pageTableCell2;
        LayoutParams layoutParams = new LayoutParams();
        int i7 = tLRPC$TL_pageTableCell2.rowspan;
        if (i7 == 0) {
            i7 = 1;
        }
        Interval interval = new Interval(i5, i7 + i5);
        Alignment alignment = FILL;
        layoutParams.rowSpec = new Spec(false, interval, alignment, 0.0f);
        layoutParams.columnSpec = new Spec(false, new Interval(i4, i6 + i4), alignment, 1.0f);
        LayoutParams unused2 = child.layoutParams = layoutParams;
        child.rowspan = i5;
        this.childrens.add(child);
        int i8 = tLRPC$TL_pageTableCell2.rowspan;
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
        if (i < 0 || i >= this.childrens.size()) {
            return null;
        }
        return this.childrens.get(i);
    }

    public TableLayout(Context context, TableLayoutDelegate tableLayoutDelegate, TextSelectionHelper.ArticleTextSelectionHelper articleTextSelectionHelper) {
        super(context);
        new Path();
        this.backgroundPath = new Path();
        this.rect = new RectF();
        this.radii = new float[8];
        this.childrens = new ArrayList<>();
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

    public void setRowOrderPreserved(boolean z) {
        this.mVerticalAxis.setOrderPreserved(z);
        invalidateStructure();
        requestLayout();
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
        T[] tArr3 = (Object[]) Array.newInstance(tArr.getClass().getComponentType(), tArr.length + tArr2.length);
        System.arraycopy(tArr, 0, tArr3, 0, tArr.length);
        System.arraycopy(tArr2, 0, tArr3, tArr.length, tArr2.length);
        return tArr3;
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
        if (!((z && this.isRtl) != z2) ? interval.max == axis.getCount() : interval.min == 0) {
            z3 = true;
        }
        return getDefaultMargin(child, z3, z, z2);
    }

    /* access modifiers changed from: package-private */
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
        boolean z = this.mOrientation == 0;
        int i = (z ? this.mHorizontalAxis : this.mVerticalAxis).definedCount;
        if (i == Integer.MIN_VALUE) {
            i = 0;
        }
        int[] iArr = new int[i];
        int childCount = getChildCount();
        int i2 = 0;
        int i3 = 0;
        for (int i4 = 0; i4 < childCount; i4++) {
            LayoutParams layoutParams = getChildAt(i4).getLayoutParams();
            Spec spec = z ? layoutParams.rowSpec : layoutParams.columnSpec;
            Interval interval = spec.span;
            boolean z2 = spec.startDefined;
            int size = interval.size();
            if (z2) {
                i2 = interval.min;
            }
            Spec spec2 = z ? layoutParams.columnSpec : layoutParams.rowSpec;
            Interval interval2 = spec2.span;
            boolean z3 = spec2.startDefined;
            int clip = clip(interval2, z3, i);
            if (z3) {
                i3 = interval2.min;
            }
            if (i != 0) {
                if (!z2 || !z3) {
                    while (true) {
                        int i5 = i3 + clip;
                        if (fits(iArr, i2, i3, i5)) {
                            break;
                        } else if (z3) {
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
            if (z) {
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
        throw new IllegalArgumentException(str + ". ");
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).draw(canvas);
        }
    }

    private int computeLayoutParamsHashCode() {
        int childCount = getChildCount();
        int i = 1;
        for (int i2 = 0; i2 < childCount; i2++) {
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
                int size = View.MeasureSpec.getSize(i);
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

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int i3;
        int i4;
        boolean z;
        boolean z2;
        int i5 = i;
        int i6 = i2;
        consistencyCheck();
        invalidateValues();
        boolean z3 = false;
        this.colCount = 0;
        int childCount = getChildCount();
        for (int i7 = 0; i7 < childCount; i7++) {
            this.colCount = Math.max(this.colCount, getChildAt(i7).layoutParams.columnSpec.span.max);
        }
        boolean z4 = true;
        measureChildrenWithMargins(i5, i6, true);
        if (this.mOrientation == 0) {
            i3 = this.mHorizontalAxis.getMeasure(i5);
            measureChildrenWithMargins(i5, i6, false);
            i4 = this.mVerticalAxis.getMeasure(i6);
        } else {
            int measure = this.mVerticalAxis.getMeasure(i6);
            measureChildrenWithMargins(i5, i6, false);
            int i8 = measure;
            i3 = this.mHorizontalAxis.getMeasure(i5);
            i4 = i8;
        }
        int max = Math.max(i3, View.MeasureSpec.getSize(i));
        int max2 = Math.max(i4, getSuggestedMinimumHeight());
        setMeasuredDimension(max, max2);
        this.mHorizontalAxis.layout(max);
        this.mVerticalAxis.layout(max2);
        int[] locations = this.mHorizontalAxis.getLocations();
        int[] locations2 = this.mVerticalAxis.getLocations();
        this.cellsToFixHeight.clear();
        int i9 = locations[locations.length - 1];
        int childCount2 = getChildCount();
        int i10 = 0;
        while (i10 < childCount2) {
            Child childAt = getChildAt(i10);
            LayoutParams layoutParams = childAt.getLayoutParams();
            Spec spec = layoutParams.columnSpec;
            Spec spec2 = layoutParams.rowSpec;
            Interval interval = spec.span;
            Interval interval2 = spec2.span;
            int i11 = locations[interval.min];
            int i12 = locations2[interval2.min];
            int i13 = locations[interval.max] - i11;
            int i14 = locations2[interval2.max] - i12;
            int measurement = getMeasurement(childAt, z4);
            int measurement2 = getMeasurement(childAt, z3);
            Alignment access$1300 = spec.getAbsoluteAlignment(z4);
            Alignment access$13002 = spec2.getAbsoluteAlignment(z3);
            Bounds value = this.mHorizontalAxis.getGroupBounds().getValue(i10);
            Bounds value2 = this.mVerticalAxis.getGroupBounds().getValue(i10);
            int gravityOffset = access$1300.getGravityOffset(childAt, i13 - value.size(z4));
            int gravityOffset2 = access$13002.getGravityOffset(childAt, i14 - value2.size(z4));
            int margin = getMargin(childAt, z4, z4);
            int margin2 = getMargin(childAt, false, z4);
            int margin3 = getMargin(childAt, z4, false);
            int i15 = margin + margin3;
            int margin4 = margin2 + getMargin(childAt, false, false);
            Bounds bounds = value2;
            Alignment alignment = access$13002;
            Child child = childAt;
            Alignment alignment2 = access$1300;
            int i16 = measurement2;
            int i17 = max2;
            int offset = value.getOffset(this, child, access$1300, measurement + i15, true);
            Alignment alignment3 = alignment;
            int offset2 = bounds.getOffset(this, child, alignment3, i16 + margin4, false);
            int sizeInCell = alignment2.getSizeInCell(childAt, measurement, i13 - i15);
            int sizeInCell2 = alignment3.getSizeInCell(childAt, i16, i14 - margin4);
            int i18 = i11 + gravityOffset + offset;
            int i19 = !this.isRtl ? margin + i18 : ((i9 - sizeInCell) - margin3) - i18;
            int i20 = i12 + gravityOffset2 + offset2 + margin2;
            if (childAt.cell != null) {
                if (sizeInCell != childAt.getMeasuredWidth() || sizeInCell2 != childAt.getMeasuredHeight()) {
                    childAt.measure(sizeInCell, sizeInCell2, false);
                }
                if (!(childAt.fixedHeight == 0 || childAt.fixedHeight == sizeInCell2 || childAt.layoutParams.rowSpec.span.max - childAt.layoutParams.rowSpec.span.min > 1)) {
                    int size = this.rowSpans.size();
                    int i21 = 0;
                    while (true) {
                        if (i21 >= size) {
                            z2 = false;
                            break;
                        }
                        Point point = this.rowSpans.get(i21);
                        if (point.x <= ((float) childAt.layoutParams.rowSpec.span.min) && point.y > ((float) childAt.layoutParams.rowSpec.span.min)) {
                            z2 = true;
                            break;
                        }
                        i21++;
                    }
                    if (!z2) {
                        this.cellsToFixHeight.add(childAt);
                    }
                }
            }
            childAt.layout(i19, i20, sizeInCell + i19, sizeInCell2 + i20);
            i10++;
            max2 = i17;
            z3 = false;
            z4 = true;
        }
        int i22 = max2;
        int size2 = this.cellsToFixHeight.size();
        int i23 = 0;
        while (i23 < size2) {
            Child child2 = this.cellsToFixHeight.get(i23);
            int access$1500 = child2.measuredHeight - child2.fixedHeight;
            int access$1600 = child2.index + 1;
            int size3 = this.childrens.size();
            while (true) {
                if (access$1600 >= size3) {
                    break;
                }
                Child child3 = this.childrens.get(access$1600);
                if (child2.layoutParams.rowSpec.span.min != child3.layoutParams.rowSpec.span.min) {
                    break;
                } else if (child2.fixedHeight < child3.fixedHeight) {
                    z = true;
                    break;
                } else {
                    int access$15002 = child3.measuredHeight - child3.fixedHeight;
                    if (access$15002 > 0) {
                        access$1500 = Math.min(access$1500, access$15002);
                    }
                    access$1600++;
                }
            }
            z = false;
            if (!z) {
                int access$16002 = child2.index - 1;
                while (true) {
                    if (access$16002 < 0) {
                        break;
                    }
                    Child child4 = this.childrens.get(access$16002);
                    if (child2.layoutParams.rowSpec.span.min != child4.layoutParams.rowSpec.span.min) {
                        break;
                    } else if (child2.fixedHeight < child4.fixedHeight) {
                        z = true;
                        break;
                    } else {
                        int access$15003 = child4.measuredHeight - child4.fixedHeight;
                        if (access$15003 > 0) {
                            access$1500 = Math.min(access$1500, access$15003);
                        }
                        access$16002--;
                    }
                }
            }
            if (!z) {
                child2.setFixedHeight(child2.fixedHeight);
                max2 -= access$1500;
                int size4 = this.childrens.size();
                int i24 = i23;
                for (int i25 = 0; i25 < size4; i25++) {
                    Child child5 = this.childrens.get(i25);
                    if (child2 != child5) {
                        if (child2.layoutParams.rowSpec.span.min == child5.layoutParams.rowSpec.span.min) {
                            if (child5.fixedHeight != child5.measuredHeight) {
                                this.cellsToFixHeight.remove(child5);
                                if (child5.index < child2.index) {
                                    i24--;
                                }
                                size2--;
                            }
                            int unused = child5.measuredHeight = child5.measuredHeight - access$1500;
                            child5.measure(child5.measuredWidth, child5.measuredHeight, true);
                        } else if (child2.layoutParams.rowSpec.span.min < child5.layoutParams.rowSpec.span.min) {
                            child5.y -= access$1500;
                        }
                    }
                }
                i23 = i24;
            }
            i23++;
        }
        int childCount3 = getChildCount();
        for (int i26 = 0; i26 < childCount3; i26++) {
            Child childAt2 = getChildAt(i26);
            this.delegate.onLayoutChild(childAt2.textLayout, childAt2.getTextX(), childAt2.getTextY());
        }
        setMeasuredDimension(i9, max2);
    }

    private int getMeasurement(Child child, boolean z) {
        return z ? child.getMeasuredWidth() : child.getMeasuredHeight();
    }

    /* access modifiers changed from: package-private */
    public final int getMeasurementIncludingMargin(Child child, boolean z) {
        return getMeasurement(child, z) + getTotalMargin(child, z);
    }

    public void requestLayout() {
        super.requestLayout();
        invalidateValues();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        consistencyCheck();
    }

    final class Axis {
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
            if (i == -1) {
                return Integer.MIN_VALUE;
            }
            return i;
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
            if (i == Integer.MIN_VALUE || i >= getMaxIndex()) {
                this.definedCount = i;
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(this.horizontal ? "column" : "row");
            sb.append("Count must be greater than or equal to the maximum of all grid indices (and spans) defined in the LayoutParams of each child");
            TableLayout.access$1800(sb.toString());
            throw null;
        }

        public void setOrderPreserved(boolean z) {
            this.orderPreserved = z;
            invalidateStructure();
        }

        private PackedMap<Spec, Bounds> createGroupBounds() {
            Assoc<K, V> of = Assoc.of(Spec.class, Bounds.class);
            int childCount = TableLayout.this.getChildCount();
            for (int i = 0; i < childCount; i++) {
                LayoutParams layoutParams = TableLayout.this.getChildAt(i).getLayoutParams();
                boolean z = this.horizontal;
                Spec spec = z ? layoutParams.columnSpec : layoutParams.rowSpec;
                of.put(spec, spec.getAbsoluteAlignment(z).getBounds());
            }
            return of.pack();
        }

        private void computeGroupBounds() {
            Bounds[] boundsArr = (Bounds[]) this.groupBounds.values;
            for (Bounds reset : boundsArr) {
                reset.reset();
            }
            int childCount = TableLayout.this.getChildCount();
            for (int i = 0; i < childCount; i++) {
                Child childAt = TableLayout.this.getChildAt(i);
                LayoutParams layoutParams = childAt.getLayoutParams();
                boolean z = this.horizontal;
                Spec spec = z ? layoutParams.columnSpec : layoutParams.rowSpec;
                this.groupBounds.getValue(i).include(TableLayout.this, childAt, spec, this, TableLayout.this.getMeasurementIncludingMargin(childAt, z) + (spec.weight == 0.0f ? 0 : this.deltas[i]));
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
            Assoc<K, V> of = Assoc.of(Interval.class, MutableInt.class);
            Spec[] specArr = (Spec[]) getGroupBounds().keys;
            int length = specArr.length;
            for (int i = 0; i < length; i++) {
                of.put(z ? specArr[i].span : specArr[i].span.inverse(), new MutableInt());
            }
            return of.pack();
        }

        private void computeLinks(PackedMap<Interval, MutableInt> packedMap, boolean z) {
            MutableInt[] mutableIntArr = (MutableInt[]) packedMap.values;
            for (MutableInt reset : mutableIntArr) {
                reset.reset();
            }
            Bounds[] boundsArr = (Bounds[]) getGroupBounds().values;
            for (int i = 0; i < boundsArr.length; i++) {
                int size = boundsArr[i].size(z);
                MutableInt value = packedMap.getValue(i);
                int i2 = value.value;
                if (!z) {
                    size = -size;
                }
                value.value = Math.max(i2, size);
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

        /* access modifiers changed from: package-private */
        public Arc[][] groupArcsByFirstVertex(Arc[] arcArr) {
            int count = getCount() + 1;
            Arc[][] arcArr2 = new Arc[count][];
            int[] iArr = new int[count];
            for (Arc arc : arcArr) {
                int i = arc.span.min;
                iArr[i] = iArr[i] + 1;
            }
            for (int i2 = 0; i2 < count; i2++) {
                arcArr2[i2] = new Arc[iArr[i2]];
            }
            Arrays.fill(iArr, 0);
            for (Arc arc2 : arcArr) {
                int i3 = arc2.span.min;
                Arc[] arcArr3 = arcArr2[i3];
                int i4 = iArr[i3];
                iArr[i3] = i4 + 1;
                arcArr3[i4] = arc2;
            }
            return arcArr2;
        }

        private Arc[] topologicalSort(Arc[] arcArr) {
            return new Object(arcArr) {
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

                /* access modifiers changed from: package-private */
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
            return topologicalSort((Arc[]) list.toArray(new Arc[0]));
        }

        private void addComponentSizes(List<Arc> list, PackedMap<Interval, MutableInt> packedMap) {
            int i = 0;
            while (true) {
                K[] kArr = packedMap.keys;
                if (i < ((Interval[]) kArr).length) {
                    include(list, ((Interval[]) kArr)[i], ((MutableInt[]) packedMap.values)[i], false);
                    i++;
                } else {
                    return;
                }
            }
        }

        private Arc[] createArcs() {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            addComponentSizes(arrayList, getForwardLinks());
            addComponentSizes(arrayList2, getBackwardLinks());
            if (this.orderPreserved) {
                int i = 0;
                while (i < getCount()) {
                    int i2 = i + 1;
                    include(arrayList, new Interval(i, i2), new MutableInt(0));
                    i = i2;
                }
            }
            int count = getCount();
            include(arrayList, new Interval(0, count), this.parentMin, false);
            include(arrayList2, new Interval(count, 0), this.parentMax, false);
            return (Arc[]) TableLayout.append(topologicalSort((List<Arc>) arrayList), topologicalSort((List<Arc>) arrayList2));
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
            int i3 = iArr[i] + arc.value.value;
            if (i3 <= iArr[i2]) {
                return false;
            }
            iArr[i2] = i3;
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
                init(iArr);
                for (int i2 = 0; i2 < count; i2++) {
                    boolean z2 = false;
                    for (Arc relax : arcArr) {
                        z2 |= relax(iArr, relax);
                    }
                    if (!z2) {
                        return true;
                    }
                }
                if (!z) {
                    return false;
                }
                boolean[] zArr = new boolean[arcArr.length];
                for (int i3 = 0; i3 < count; i3++) {
                    int length = arcArr.length;
                    for (int i4 = 0; i4 < length; i4++) {
                        zArr[i4] = zArr[i4] | relax(iArr, arcArr[i4]);
                    }
                }
                int i5 = 0;
                while (true) {
                    if (i5 >= arcArr.length) {
                        break;
                    }
                    if (zArr[i5]) {
                        Arc arc = arcArr[i5];
                        Interval interval = arc.span;
                        if (interval.min >= interval.max) {
                            arc.valid = false;
                            break;
                        }
                    }
                    i5++;
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
                boolean z2 = this.horizontal;
                Interval interval = (z2 ? layoutParams.columnSpec : layoutParams.rowSpec).span;
                int i2 = z ? interval.min : interval.max;
                iArr[i2] = Math.max(iArr[i2], TableLayout.this.getMargin1(childAt, z2, z));
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
            Arrays.fill(this.deltas, 0);
            int childCount = TableLayout.this.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                LayoutParams layoutParams = TableLayout.this.getChildAt(i2).getLayoutParams();
                float f2 = (this.horizontal ? layoutParams.columnSpec : layoutParams.rowSpec).weight;
                if (f2 != 0.0f) {
                    int round = Math.round((((float) i) * f2) / f);
                    this.deltas[i2] = round;
                    i -= round;
                    f -= f2;
                }
            }
        }

        private void solveAndDistributeSpace(int[] iArr) {
            Arrays.fill(getDeltas(), 0);
            solve(iArr);
            boolean z = true;
            int childCount = (this.parentMin.value * TableLayout.this.getChildCount()) + 1;
            if (childCount >= 2) {
                float calculateTotalWeight = calculateTotalWeight();
                int i = -1;
                int i2 = 0;
                while (i2 < childCount) {
                    int i3 = (int) ((((long) i2) + ((long) childCount)) / 2);
                    invalidateValues();
                    shareOutDelta(i3, calculateTotalWeight);
                    boolean solve = solve(getArcs(), iArr, false);
                    if (solve) {
                        i2 = i3 + 1;
                        i = i3;
                    } else {
                        childCount = i3;
                    }
                    z = solve;
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
            if (!hasWeights()) {
                solve(iArr);
            } else {
                solveAndDistributeSpace(iArr);
            }
            if (!this.orderPreserved) {
                int i = iArr[0];
                int length = iArr.length;
                for (int i2 = 0; i2 < length; i2++) {
                    iArr[i2] = iArr[i2] - i;
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
            int mode = View.MeasureSpec.getMode(i);
            int size = View.MeasureSpec.getSize(i);
            if (mode == Integer.MIN_VALUE) {
                return getMeasure(0, size);
            }
            if (mode == 0) {
                return getMeasure(0, 100000);
            }
            if (mode != NUM) {
                return 0;
            }
            return getMeasure(size, size);
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

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        private static final Interval DEFAULT_SPAN;
        public Spec columnSpec;
        public Spec rowSpec;

        static {
            Interval interval = new Interval(Integer.MIN_VALUE, -NUM);
            DEFAULT_SPAN = interval;
            interval.size();
        }

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

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LayoutParams() {
            /*
                r1 = this;
                org.telegram.ui.Components.TableLayout$Spec r0 = org.telegram.ui.Components.TableLayout.Spec.UNDEFINED
                r1.<init>(r0, r0)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TableLayout.LayoutParams.<init>():void");
        }

        /* access modifiers changed from: package-private */
        public final void setRowSpecSpan(Interval interval) {
            this.rowSpec = this.rowSpec.copyWriteSpan(interval);
        }

        /* access modifiers changed from: package-private */
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

    static final class Arc {
        public final Interval span;
        public boolean valid = true;
        public final MutableInt value;

        public Arc(Interval interval, MutableInt mutableInt) {
            this.span = interval;
            this.value = mutableInt;
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

    static final class Assoc<K, V> extends ArrayList<Pair<K, V>> implements j$.util.List, Collection {
        private final Class<K> keyType;
        private final Class<V> valueType;

        public /* synthetic */ void forEach(Consumer consumer) {
            Iterable.CC.$default$forEach(this, consumer);
        }

        public /* synthetic */ Stream parallelStream() {
            return Collection.CC.$default$parallelStream(this);
        }

        public /* synthetic */ boolean removeIf(Predicate predicate) {
            return Collection.CC.$default$removeIf(this, predicate);
        }

        public /* synthetic */ void replaceAll(UnaryOperator unaryOperator) {
            List.CC.$default$replaceAll(this, unaryOperator);
        }

        public /* synthetic */ void sort(Comparator comparator) {
            List.CC.$default$sort(this, comparator);
        }

        public /* synthetic */ Spliterator spliterator() {
            return List.CC.$default$spliterator(this);
        }

        public /* synthetic */ Stream stream() {
            return Collection.CC.$default$stream(this);
        }

        private Assoc(Class<K> cls, Class<V> cls2) {
            this.keyType = cls;
            this.valueType = cls2;
        }

        public static <K, V> Assoc<K, V> of(Class<K> cls, Class<V> cls2) {
            return new Assoc<>(cls, cls2);
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
            return new PackedMap<>(objArr, objArr2);
        }
    }

    static final class PackedMap<K, V> {
        public final int[] index;
        public final K[] keys;
        public final V[] values;

        private PackedMap(K[] kArr, V[] vArr) {
            int[] createIndex = createIndex(kArr);
            this.index = createIndex;
            this.keys = compact(kArr, createIndex);
            this.values = compact(vArr, createIndex);
        }

        public V getValue(int i) {
            return this.values[this.index[i]];
        }

        private static <K> int[] createIndex(K[] kArr) {
            int length = kArr.length;
            int[] iArr = new int[length];
            HashMap hashMap = new HashMap();
            for (int i = 0; i < length; i++) {
                K k = kArr[i];
                Integer num = (Integer) hashMap.get(k);
                if (num == null) {
                    num = Integer.valueOf(hashMap.size());
                    hashMap.put(k, num);
                }
                iArr[i] = num.intValue();
            }
            return iArr;
        }

        private static <K> K[] compact(K[] kArr, int[] iArr) {
            int length = kArr.length;
            K[] kArr2 = (Object[]) Array.newInstance(kArr.getClass().getComponentType(), TableLayout.max2(iArr, -1) + 1);
            for (int i = 0; i < length; i++) {
                kArr2[iArr[i]] = kArr[i];
            }
            return kArr2;
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
        public void include(int i, int i2) {
            this.before = Math.max(this.before, i);
            this.after = Math.max(this.after, i2);
        }

        /* access modifiers changed from: protected */
        public int size(boolean z) {
            if (z || !TableLayout.canStretch(this.flexibility)) {
                return this.before + this.after;
            }
            return 100000;
        }

        /* access modifiers changed from: protected */
        public int getOffset(TableLayout tableLayout, Child child, Alignment alignment, int i, boolean z) {
            return this.before - alignment.getAlignmentValue(child, i);
        }

        /* access modifiers changed from: protected */
        public final void include(TableLayout tableLayout, Child child, Spec spec, Axis axis, int i) {
            this.flexibility &= spec.getFlexibility();
            int alignmentValue = spec.getAbsoluteAlignment(axis.horizontal).getAlignmentValue(child, i);
            include(alignmentValue, i - alignmentValue);
        }
    }

    static final class Interval {
        public final int max;
        public final int min;

        public Interval(int i, int i2) {
            this.min = i;
            this.max = i2;
        }

        /* access modifiers changed from: package-private */
        public int size() {
            return this.max - this.min;
        }

        /* access modifiers changed from: package-private */
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

    public static class Spec {
        static final Spec UNDEFINED = TableLayout.spec(Integer.MIN_VALUE);
        final Alignment alignment;
        final Interval span;
        final boolean startDefined;
        float weight;

        private Spec(boolean z, Interval interval, Alignment alignment2, float f) {
            this.startDefined = z;
            this.span = interval;
            this.alignment = alignment2;
            this.weight = f;
        }

        private Spec(boolean z, int i, int i2, Alignment alignment2, float f) {
            this(z, new Interval(i, i2 + i), alignment2, f);
        }

        /* access modifiers changed from: private */
        public Alignment getAbsoluteAlignment(boolean z) {
            Alignment alignment2 = this.alignment;
            if (alignment2 != TableLayout.UNDEFINED_ALIGNMENT) {
                return alignment2;
            }
            if (this.weight == 0.0f) {
                return z ? TableLayout.START : TableLayout.BASELINE;
            }
            return TableLayout.FILL;
        }

        /* access modifiers changed from: package-private */
        public final Spec copyWriteSpan(Interval interval) {
            return new Spec(this.startDefined, interval, this.alignment, this.weight);
        }

        /* access modifiers changed from: package-private */
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

    public static Spec spec(int i, int i2, Alignment alignment, float f) {
        return new Spec(i != Integer.MIN_VALUE, i, i2, alignment, f);
    }

    public static Spec spec(int i, int i2, Alignment alignment) {
        return spec(i, i2, alignment, 0.0f);
    }

    public static Spec spec(int i, int i2) {
        return spec(i, i2, UNDEFINED_ALIGNMENT);
    }

    public static Spec spec(int i) {
        return spec(i, 1);
    }

    public static abstract class Alignment {
        /* access modifiers changed from: package-private */
        public abstract int getAlignmentValue(Child child, int i);

        /* access modifiers changed from: package-private */
        public abstract int getGravityOffset(Child child, int i);

        /* access modifiers changed from: package-private */
        public int getSizeInCell(Child child, int i, int i2) {
            return i;
        }

        Alignment() {
        }

        /* access modifiers changed from: package-private */
        public Bounds getBounds() {
            return new Bounds();
        }
    }

    static {
        AnonymousClass2 r0 = new Alignment() {
            public int getAlignmentValue(Child child, int i) {
                return 0;
            }

            /* access modifiers changed from: package-private */
            public int getGravityOffset(Child child, int i) {
                return 0;
            }
        };
        LEADING = r0;
        AnonymousClass3 r1 = new Alignment() {
            public int getAlignmentValue(Child child, int i) {
                return i;
            }

            /* access modifiers changed from: package-private */
            public int getGravityOffset(Child child, int i) {
                return i;
            }
        };
        TRAILING = r1;
        START = r0;
        END = r1;
        createSwitchingAlignment(r0);
        createSwitchingAlignment(r1);
    }

    private static Alignment createSwitchingAlignment(final Alignment alignment) {
        return new Alignment() {
            /* access modifiers changed from: package-private */
            public int getGravityOffset(Child child, int i) {
                return alignment.getGravityOffset(child, i);
            }

            public int getAlignmentValue(Child child, int i) {
                return alignment.getAlignmentValue(child, i);
            }
        };
    }
}
