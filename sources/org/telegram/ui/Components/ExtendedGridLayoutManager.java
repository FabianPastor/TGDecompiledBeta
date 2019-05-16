package org.telegram.ui.Components;

import android.content.Context;
import android.util.SparseIntArray;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.support.widget.GridLayoutManager;

public class ExtendedGridLayoutManager extends GridLayoutManager {
    private int calculatedWidth;
    private int firstRowMax;
    private SparseIntArray itemSpans = new SparseIntArray();
    private SparseIntArray itemsToRow = new SparseIntArray();
    private int rowsCount;

    public boolean supportsPredictiveItemAnimations() {
        return false;
    }

    public ExtendedGridLayoutManager(Context context, int i) {
        super(context, i);
    }

    private void prepareLayout(float f) {
        if (f == 0.0f) {
            f = 100.0f;
        }
        this.itemSpans.clear();
        this.itemsToRow.clear();
        this.rowsCount = 0;
        this.firstRowMax = 0;
        int dp = AndroidUtilities.dp(100.0f);
        int flowItemCount = getFlowItemCount();
        int spanCount = getSpanCount();
        int i = spanCount;
        int i2 = 0;
        for (int i3 = 0; i3 < flowItemCount; i3++) {
            Size sizeForItem = sizeForItem(i3);
            int min = Math.min(spanCount, (int) Math.floor((double) (((float) spanCount) * (((sizeForItem.width / sizeForItem.height) * ((float) dp)) / f))));
            Object obj = (i < min || (min > 33 && i < min - 15)) ? 1 : null;
            if (obj != null) {
                if (i != 0) {
                    int i4 = i / i2;
                    int i5 = i3 - i2;
                    int i6 = i;
                    i = i5;
                    while (true) {
                        int i7 = i5 + i2;
                        if (i >= i7) {
                            break;
                        }
                        SparseIntArray sparseIntArray;
                        if (i == i7 - 1) {
                            sparseIntArray = this.itemSpans;
                            sparseIntArray.put(i, sparseIntArray.get(i) + i6);
                        } else {
                            sparseIntArray = this.itemSpans;
                            sparseIntArray.put(i, sparseIntArray.get(i) + i4);
                        }
                        i6 -= i4;
                        i++;
                    }
                    this.itemsToRow.put(i3 - 1, this.rowsCount);
                }
                this.rowsCount++;
                i = spanCount;
                i2 = 0;
            } else if (i < min) {
                min = i;
            }
            if (this.rowsCount == 0) {
                this.firstRowMax = Math.max(this.firstRowMax, i3);
            }
            if (i3 == flowItemCount - 1) {
                this.itemsToRow.put(i3, this.rowsCount);
            }
            i2++;
            i -= min;
            this.itemSpans.put(i3, min);
        }
        if (flowItemCount != 0) {
            this.rowsCount++;
        }
    }

    private Size sizeForItem(int i) {
        Size sizeForItem = getSizeForItem(i);
        if (sizeForItem.width == 0.0f) {
            sizeForItem.width = 100.0f;
        }
        if (sizeForItem.height == 0.0f) {
            sizeForItem.height = 100.0f;
        }
        float f = sizeForItem.width / sizeForItem.height;
        if (f > 4.0f || f < 0.2f) {
            f = Math.max(sizeForItem.width, sizeForItem.height);
            sizeForItem.width = f;
            sizeForItem.height = f;
        }
        return sizeForItem;
    }

    /* Access modifiers changed, original: protected */
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

    public int getRowsCount(int i) {
        if (this.rowsCount == 0) {
            prepareLayout((float) i);
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

    /* Access modifiers changed, original: protected */
    public int getFlowItemCount() {
        return getItemCount();
    }
}
