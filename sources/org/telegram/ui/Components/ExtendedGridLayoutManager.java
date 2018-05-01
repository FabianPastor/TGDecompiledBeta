package org.telegram.ui.Components;

import android.content.Context;
import android.util.SparseIntArray;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.support.widget.GridLayoutManager;
import org.telegram.tgnet.ConnectionsManager;

public class ExtendedGridLayoutManager extends GridLayoutManager {
    private int calculatedWidth;
    private SparseIntArray itemSpans = new SparseIntArray();
    private SparseIntArray itemsToRow = new SparseIntArray();
    private ArrayList<ArrayList<Integer>> rows;

    public boolean supportsPredictiveItemAnimations() {
        return false;
    }

    public ExtendedGridLayoutManager(Context context, int i) {
        super(context, i);
    }

    private void prepareLayout(float f) {
        int i;
        this.itemSpans.clear();
        this.itemsToRow.clear();
        int dp = AndroidUtilities.dp(100.0f);
        int flowItemCount = getFlowItemCount();
        int[] iArr = new int[flowItemCount];
        int i2 = 0;
        float f2 = 0.0f;
        for (i = 0; i < flowItemCount; i++) {
            Size sizeForItem = sizeForItem(i);
            f2 += (sizeForItem.width / sizeForItem.height) * ((float) dp);
            iArr[i] = Math.round((sizeForItem.width / sizeForItem.height) * 100.0f);
        }
        dp = 1;
        r0.rows = getLinearPartitionForSequence(iArr, Math.max(Math.round(f2 / f), 1));
        int i3 = 0;
        while (i2 < r0.rows.size()) {
            float floor;
            int size;
            int spanCount;
            int i4;
            ArrayList arrayList = (ArrayList) r0.rows.get(i2);
            i = arrayList.size() + i3;
            float f3 = 0.0f;
            for (int i5 = i3; i5 < i; i5++) {
                Size sizeForItem2 = sizeForItem(i5);
                f3 += sizeForItem2.width / sizeForItem2.height;
            }
            if (r0.rows.size() == dp && i2 == r0.rows.size() - dp) {
                if (arrayList.size() < 2) {
                    floor = (float) Math.floor((double) (f / 3.0f));
                } else if (arrayList.size() < 3) {
                    floor = (float) Math.floor((double) ((2.0f * f) / 3.0f));
                }
                size = arrayList.size() + i3;
                spanCount = getSpanCount();
                i4 = i3;
                while (i4 < size) {
                    Size sizeForItem3 = sizeForItem(i4);
                    dp = Math.round((floor / f3) * (sizeForItem3.width / sizeForItem3.height));
                    if (flowItemCount >= 3) {
                        if (i4 != size - 1) {
                            r0.itemsToRow.put(i4, i2);
                            dp = spanCount;
                            r0.itemSpans.put(i4, spanCount);
                            i4++;
                            spanCount = dp;
                        }
                    }
                    dp = (int) ((((float) dp) / f) * ((float) getSpanCount()));
                    int i6 = spanCount - dp;
                    spanCount = dp;
                    dp = i6;
                    r0.itemSpans.put(i4, spanCount);
                    i4++;
                    spanCount = dp;
                }
                i3 += arrayList.size();
                i2++;
                dp = 1;
            }
            floor = f;
            size = arrayList.size() + i3;
            spanCount = getSpanCount();
            i4 = i3;
            while (i4 < size) {
                Size sizeForItem32 = sizeForItem(i4);
                dp = Math.round((floor / f3) * (sizeForItem32.width / sizeForItem32.height));
                if (flowItemCount >= 3) {
                    if (i4 != size - 1) {
                        r0.itemsToRow.put(i4, i2);
                        dp = spanCount;
                        r0.itemSpans.put(i4, spanCount);
                        i4++;
                        spanCount = dp;
                    }
                }
                dp = (int) ((((float) dp) / f) * ((float) getSpanCount()));
                int i62 = spanCount - dp;
                spanCount = dp;
                dp = i62;
                r0.itemSpans.put(i4, spanCount);
                i4++;
                spanCount = dp;
            }
            i3 += arrayList.size();
            i2++;
            dp = 1;
        }
    }

    private int[] getLinearPartitionTable(int[] iArr, int i) {
        int length = iArr.length;
        int[] iArr2 = new int[(length * i)];
        int i2 = i - 1;
        int[] iArr3 = new int[((length - 1) * i2)];
        int i3 = 0;
        while (i3 < length) {
            iArr2[i3 * i] = iArr[i3] + (i3 != 0 ? iArr2[(i3 - 1) * i] : 0);
            i3++;
        }
        for (i3 = 0; i3 < i; i3++) {
            iArr2[i3] = iArr[0];
        }
        for (i3 = 1; i3 < length; i3++) {
            for (int i4 = 1; i4 < i; i4++) {
                int i5 = 0;
                int i6 = Integer.MAX_VALUE;
                for (int i7 = i5; i7 < i3; i7++) {
                    int i8 = i7 * i;
                    i8 = Math.max(iArr2[(i4 - 1) + i8], iArr2[i3 * i] - iArr2[i8]);
                    if (i7 == 0 || i8 < i5) {
                        i6 = i7;
                        i5 = i8;
                    }
                }
                iArr2[(i3 * i) + i4] = i5;
                iArr3[((i3 - 1) * i2) + (i4 - 1)] = i6;
            }
        }
        return iArr3;
    }

    private ArrayList<ArrayList<Integer>> getLinearPartitionForSequence(int[] iArr, int i) {
        int length = iArr.length;
        if (i <= 0) {
            return new ArrayList();
        }
        int i2 = 0;
        if (i < length) {
            if (length != 1) {
                int[] linearPartitionTable = getLinearPartitionTable(iArr, i);
                int i3 = i - 1;
                length--;
                ArrayList<ArrayList<Integer>> arrayList = new ArrayList();
                for (i -= 2; i >= 0; i--) {
                    if (length < 1) {
                        arrayList.add(0, new ArrayList());
                    } else {
                        ArrayList arrayList2 = new ArrayList();
                        int i4 = ((length - 1) * i3) + i;
                        length++;
                        for (int i5 = linearPartitionTable[i4] + 1; i5 < length; i5++) {
                            arrayList2.add(Integer.valueOf(iArr[i5]));
                        }
                        arrayList.add(0, arrayList2);
                        length = linearPartitionTable[i4];
                    }
                }
                i = new ArrayList();
                length++;
                for (int i6 = 0; i6 < length; i6++) {
                    i.add(Integer.valueOf(iArr[i6]));
                }
                arrayList.add(0, i);
                return arrayList;
            }
        }
        i = new ArrayList(iArr.length);
        while (i2 < iArr.length) {
            ArrayList arrayList3 = new ArrayList(1);
            arrayList3.add(Integer.valueOf(iArr[i2]));
            i.add(arrayList3);
            i2++;
        }
        return i;
    }

    private Size sizeForItem(int i) {
        i = getSizeForItem(i);
        if (i.width == 0.0f) {
            i.width = 100.0f;
        }
        if (i.height == 0.0f) {
            i.height = 100.0f;
        }
        float f = i.width / i.height;
        if (f > 4.0f || f < 0.2f) {
            f = Math.max(i.width, i.height);
            i.width = f;
            i.height = f;
        }
        return i;
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

    public int getRowsCount(int i) {
        if (this.rows == null) {
            prepareLayout((float) i);
        }
        return this.rows != 0 ? this.rows.size() : 0;
    }

    public boolean isLastInRow(int i) {
        checkLayout();
        return this.itemsToRow.get(i, ConnectionsManager.DEFAULT_DATACENTER_ID) != ConnectionsManager.DEFAULT_DATACENTER_ID;
    }

    public boolean isFirstRow(int i) {
        checkLayout();
        return (this.rows == null || this.rows.isEmpty() || i >= ((ArrayList) this.rows.get(0)).size()) ? false : true;
    }

    protected int getFlowItemCount() {
        return getItemCount();
    }
}
