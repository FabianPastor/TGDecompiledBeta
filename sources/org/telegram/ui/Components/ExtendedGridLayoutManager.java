package org.telegram.ui.Components;

import android.content.Context;
import android.util.SparseIntArray;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.support.widget.GridLayoutManager;
import org.telegram.tgnet.ConnectionsManager;

public class ExtendedGridLayoutManager extends GridLayoutManager {
    private int calculatedWidth;
    private int firstRowMax;
    private SparseIntArray itemSpans = new SparseIntArray();
    private SparseIntArray itemsToRow = new SparseIntArray();
    private int rowsCount;

    public ExtendedGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public boolean supportsPredictiveItemAnimations() {
        return false;
    }

    private void prepareLayout(float viewPortAvailableSize) {
        if (viewPortAvailableSize == 0.0f) {
            viewPortAvailableSize = 100.0f;
        }
        this.itemSpans.clear();
        this.itemsToRow.clear();
        this.rowsCount = 0;
        this.firstRowMax = 0;
        int preferredRowSize = AndroidUtilities.dp(100.0f);
        int itemsCount = getFlowItemCount();
        int spanCount = getSpanCount();
        int spanLeft = spanCount;
        int currentItemsInRow = 0;
        int currentItemsSpanAmount = 0;
        for (int a = 0; a < itemsCount; a++) {
            Size size = sizeForItem(a);
            int requiredSpan = Math.min(spanCount, (int) Math.floor((double) (((float) spanCount) * (((size.width / size.height) * ((float) preferredRowSize)) / viewPortAvailableSize))));
            boolean moveToNewRow = spanLeft < requiredSpan || (requiredSpan > 33 && spanLeft < requiredSpan - 15);
            if (moveToNewRow) {
                if (spanLeft != 0) {
                    int spanPerItem = spanLeft / currentItemsInRow;
                    int start = a - currentItemsInRow;
                    for (int b = start; b < start + currentItemsInRow; b++) {
                        if (b == (start + currentItemsInRow) - 1) {
                            this.itemSpans.put(b, this.itemSpans.get(b) + spanLeft);
                        } else {
                            this.itemSpans.put(b, this.itemSpans.get(b) + spanPerItem);
                        }
                        spanLeft -= spanPerItem;
                    }
                    this.itemsToRow.put(a - 1, this.rowsCount);
                }
                this.rowsCount++;
                currentItemsSpanAmount = 0;
                currentItemsInRow = 0;
                spanLeft = spanCount;
            } else if (spanLeft < requiredSpan) {
                requiredSpan = spanLeft;
            }
            if (this.rowsCount == 0) {
                this.firstRowMax = Math.max(this.firstRowMax, a);
            }
            if (a == itemsCount - 1) {
                this.itemsToRow.put(a, this.rowsCount);
            }
            currentItemsSpanAmount += requiredSpan;
            currentItemsInRow++;
            spanLeft -= requiredSpan;
            this.itemSpans.put(a, requiredSpan);
        }
        if (itemsCount != 0) {
            this.rowsCount++;
        }
    }

    private Size sizeForItem(int i) {
        Size size = getSizeForItem(i);
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

    protected Size getSizeForItem(int i) {
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
        return this.itemsToRow.get(i, ConnectionsManager.DEFAULT_DATACENTER_ID) != ConnectionsManager.DEFAULT_DATACENTER_ID;
    }

    public boolean isFirstRow(int i) {
        checkLayout();
        return i <= this.firstRowMax;
    }

    protected int getFlowItemCount() {
        return getItemCount();
    }
}
