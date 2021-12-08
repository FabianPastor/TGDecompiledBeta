package org.telegram.ui.Components;

import android.content.Context;
import android.util.SparseIntArray;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.AndroidUtilities;

public class ExtendedGridLayoutManager extends GridLayoutManager {
    private int calculatedWidth;
    private int firstRowMax;
    private SparseIntArray itemSpans;
    private SparseIntArray itemsToRow;
    private final boolean lastRowFullWidth;
    private int rowsCount;

    public ExtendedGridLayoutManager(Context context, int spanCount) {
        this(context, spanCount, false);
    }

    public ExtendedGridLayoutManager(Context context, int spanCount, boolean lastRowFullWidth2) {
        super(context, spanCount);
        this.itemSpans = new SparseIntArray();
        this.itemsToRow = new SparseIntArray();
        this.lastRowFullWidth = lastRowFullWidth2;
    }

    public boolean supportsPredictiveItemAnimations() {
        return false;
    }

    private void prepareLayout(float viewPortAvailableSize) {
        float viewPortAvailableSize2;
        int requiredSpan;
        boolean moveToNewRow;
        float viewPortAvailableSize3;
        float viewPortAvailableSize4;
        if (viewPortAvailableSize == 0.0f) {
            viewPortAvailableSize2 = 100.0f;
        } else {
            viewPortAvailableSize2 = viewPortAvailableSize;
        }
        this.itemSpans.clear();
        this.itemsToRow.clear();
        this.rowsCount = 0;
        this.firstRowMax = 0;
        int itemsCount = getFlowItemCount();
        if (itemsCount != 0) {
            int preferredRowSize = AndroidUtilities.dp(100.0f);
            int spanCount = getSpanCount();
            int spanLeft = spanCount;
            int currentItemsInRow = 0;
            int currentItemsSpanAmount = 0;
            int a = 0;
            int N = (this.lastRowFullWidth ? 1 : 0) + itemsCount;
            while (true) {
                if (a >= N) {
                    break;
                }
                Size size = a < itemsCount ? sizeForItem(a) : null;
                if (size == null) {
                    moveToNewRow = currentItemsInRow != 0;
                    requiredSpan = spanCount;
                } else {
                    requiredSpan = Math.min(spanCount, (int) Math.floor((double) (((float) spanCount) * (((size.width / size.height) * ((float) preferredRowSize)) / viewPortAvailableSize2))));
                    moveToNewRow = spanLeft < requiredSpan || (requiredSpan > 33 && spanLeft < requiredSpan + -15);
                }
                if (moveToNewRow) {
                    if (spanLeft != 0) {
                        int spanPerItem = spanLeft / currentItemsInRow;
                        int start = a - currentItemsInRow;
                        int b = start;
                        while (b < start + currentItemsInRow) {
                            if (b == (start + currentItemsInRow) - 1) {
                                SparseIntArray sparseIntArray = this.itemSpans;
                                viewPortAvailableSize4 = viewPortAvailableSize2;
                                sparseIntArray.put(b, sparseIntArray.get(b) + spanLeft);
                            } else {
                                viewPortAvailableSize4 = viewPortAvailableSize2;
                                SparseIntArray sparseIntArray2 = this.itemSpans;
                                sparseIntArray2.put(b, sparseIntArray2.get(b) + spanPerItem);
                            }
                            spanLeft -= spanPerItem;
                            b++;
                            viewPortAvailableSize2 = viewPortAvailableSize4;
                        }
                        viewPortAvailableSize3 = viewPortAvailableSize2;
                        this.itemsToRow.put(a - 1, this.rowsCount);
                    } else {
                        viewPortAvailableSize3 = viewPortAvailableSize2;
                    }
                    if (a == itemsCount) {
                        break;
                    }
                    this.rowsCount++;
                    currentItemsSpanAmount = 0;
                    currentItemsInRow = 0;
                    spanLeft = spanCount;
                } else {
                    viewPortAvailableSize3 = viewPortAvailableSize2;
                    if (spanLeft < requiredSpan) {
                        requiredSpan = spanLeft;
                    }
                }
                if (this.rowsCount == 0) {
                    this.firstRowMax = Math.max(this.firstRowMax, a);
                }
                if (a == itemsCount - 1 && !this.lastRowFullWidth) {
                    this.itemsToRow.put(a, this.rowsCount);
                }
                currentItemsSpanAmount += requiredSpan;
                currentItemsInRow++;
                spanLeft -= requiredSpan;
                this.itemSpans.put(a, requiredSpan);
                a++;
                viewPortAvailableSize2 = viewPortAvailableSize3;
            }
            this.rowsCount++;
        }
    }

    private Size sizeForItem(int i) {
        return fixSize(getSizeForItem(i));
    }

    /* access modifiers changed from: protected */
    public Size fixSize(Size size) {
        if (size == null) {
            return null;
        }
        if (size.width == 0.0f) {
            size.width = 100.0f;
        }
        if (size.height == 0.0f) {
            size.height = 100.0f;
        }
        float aspect = size.width / size.height;
        if (aspect > 4.0f || aspect < 0.2f) {
            float max = Math.max(size.width, size.height);
            size.width = max;
            size.height = max;
        }
        return size;
    }

    /* access modifiers changed from: protected */
    public Size getSizeForItem(int i) {
        return new Size(100.0f, 100.0f);
    }

    private void checkLayout() {
        if (this.itemSpans.size() != getFlowItemCount() || this.calculatedWidth != getWidth()) {
            this.calculatedWidth = getWidth();
            prepareLayout((float) getWidth());
        }
    }

    public int getSpanSizeForItem(int i) {
        checkLayout();
        return this.itemSpans.get(i);
    }

    public int getRowsCount(int width) {
        if (this.rowsCount == 0) {
            prepareLayout((float) width);
        }
        return this.rowsCount;
    }

    public boolean isLastInRow(int i) {
        checkLayout();
        return this.itemsToRow.get(i, Integer.MAX_VALUE) != Integer.MAX_VALUE;
    }

    public boolean isFirstRow(int i) {
        checkLayout();
        return i <= this.firstRowMax;
    }

    /* access modifiers changed from: protected */
    public int getFlowItemCount() {
        return getItemCount();
    }

    public int getRowCountForAccessibility(RecyclerView.Recycler recycler, RecyclerView.State state) {
        return state.getItemCount();
    }

    public int getColumnCountForAccessibility(RecyclerView.Recycler recycler, RecyclerView.State state) {
        return 1;
    }
}
