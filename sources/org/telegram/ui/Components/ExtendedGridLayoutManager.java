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

    public ExtendedGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public boolean supportsPredictiveItemAnimations() {
        return false;
    }

    private void prepareLayout(float viewPortAvailableSize) {
        int a;
        int preferredRowSize;
        this.itemSpans.clear();
        this.itemsToRow.clear();
        int preferredRowSize2 = AndroidUtilities.dp(100.0f);
        int itemsCount = getFlowItemCount();
        int[] weights = new int[itemsCount];
        int a2 = 0;
        float totalItemSize = 0.0f;
        for (a = 0; a < itemsCount; a++) {
            Size size = sizeForItem(a);
            totalItemSize += (size.width / size.height) * ((float) preferredRowSize2);
            weights[a] = Math.round((size.width / size.height) * 100.0f);
        }
        int numberOfRows = Math.max(Math.round(totalItemSize / viewPortAvailableSize), 1);
        r0.rows = getLinearPartitionForSequence(weights, numberOfRows);
        int i = 0;
        while (a2 < r0.rows.size()) {
            int rowSize;
            ArrayList<Integer> row = (ArrayList) r0.rows.get(a2);
            float summedRatios = 0.0f;
            int j = i;
            int n = row.size() + i;
            while (j < n) {
                Size preferredSize = sizeForItem(j);
                summedRatios += preferredSize.width / preferredSize.height;
                j++;
                numberOfRows = numberOfRows;
            }
            int numberOfRows2 = numberOfRows;
            numberOfRows = viewPortAvailableSize;
            if (r0.rows.size() == 1 && a2 == r0.rows.size() - 1) {
                if (row.size() < 2) {
                    numberOfRows = (float) Math.floor((double) (viewPortAvailableSize / 3.0f));
                } else if (row.size() < 3) {
                    numberOfRows = (float) Math.floor((double) ((2.0f * viewPortAvailableSize) / 3.0f));
                }
            }
            a = getSpanCount();
            n = i;
            int n2 = row.size() + i;
            while (n < n2) {
                int itemSpan;
                Size preferredSize2 = sizeForItem(n);
                rowSize = numberOfRows;
                preferredRowSize = preferredRowSize2;
                numberOfRows = Math.round((preferredSize2.width / preferredSize2.height) * (numberOfRows / summedRatios));
                if (itemsCount >= 3) {
                    if (n == n2 - 1) {
                        r0.itemsToRow.put(n, a2);
                        itemSpan = a;
                        r0.itemSpans.put(n, itemSpan);
                        n++;
                        numberOfRows = rowSize;
                        preferredRowSize2 = preferredRowSize;
                    }
                }
                itemSpan = (int) ((((float) numberOfRows) / viewPortAvailableSize) * ((float) getSpanCount()));
                a -= itemSpan;
                r0.itemSpans.put(n, itemSpan);
                n++;
                numberOfRows = rowSize;
                preferredRowSize2 = preferredRowSize;
            }
            rowSize = numberOfRows;
            preferredRowSize = preferredRowSize2;
            i += row.size();
            a2++;
            numberOfRows = numberOfRows2;
        }
        preferredRowSize = preferredRowSize2;
    }

    private int[] getLinearPartitionTable(int[] sequence, int numPartitions) {
        int j;
        int n = sequence.length;
        int[] tmpTable = new int[(n * numPartitions)];
        int[] solution = new int[((n - 1) * (numPartitions - 1))];
        int i = 0;
        while (i < n) {
            tmpTable[i * numPartitions] = sequence[i] + (i != 0 ? tmpTable[(i - 1) * numPartitions] : 0);
            i++;
        }
        for (j = 0; j < numPartitions; j++) {
            tmpTable[j] = sequence[0];
        }
        for (i = 1; i < n; i++) {
            for (j = 1; j < numPartitions; j++) {
                int minX = ConnectionsManager.DEFAULT_DATACENTER_ID;
                int currentMin = 0;
                for (int x = 0; x < i; x++) {
                    int cost = Math.max(tmpTable[(x * numPartitions) + (j - 1)], tmpTable[i * numPartitions] - tmpTable[x * numPartitions]);
                    if (x == 0 || cost < currentMin) {
                        currentMin = cost;
                        minX = x;
                    }
                }
                tmpTable[(i * numPartitions) + j] = currentMin;
                solution[((i - 1) * (numPartitions - 1)) + (j - 1)] = minX;
            }
        }
        return solution;
    }

    private ArrayList<ArrayList<Integer>> getLinearPartitionForSequence(int[] sequence, int numberOfPartitions) {
        int n = sequence.length;
        int k = numberOfPartitions;
        if (k <= 0) {
            return new ArrayList();
        }
        int i = 0;
        if (k < n) {
            if (n != 1) {
                int i2;
                int[] solution = getLinearPartitionTable(sequence, numberOfPartitions);
                int solutionRowSize = numberOfPartitions - 1;
                n--;
                ArrayList<ArrayList<Integer>> answer = new ArrayList();
                for (k -= 2; k >= 0; k--) {
                    if (n < 1) {
                        answer.add(0, new ArrayList());
                    } else {
                        ArrayList<Integer> currentAnswer = new ArrayList();
                        int range = n + 1;
                        for (i2 = solution[((n - 1) * solutionRowSize) + k] + 1; i2 < range; i2++) {
                            currentAnswer.add(Integer.valueOf(sequence[i2]));
                        }
                        answer.add(0, currentAnswer);
                        n = solution[((n - 1) * solutionRowSize) + k];
                    }
                }
                ArrayList<Integer> currentAnswer2 = new ArrayList();
                i2 = n + 1;
                for (int i3 = 0; i3 < i2; i3++) {
                    currentAnswer2.add(Integer.valueOf(sequence[i3]));
                }
                answer.add(0, currentAnswer2);
                return answer;
            }
        }
        ArrayList<ArrayList<Integer>> partition = new ArrayList(sequence.length);
        while (i < sequence.length) {
            ArrayList<Integer> arrayList = new ArrayList(1);
            arrayList.add(Integer.valueOf(sequence[i]));
            partition.add(arrayList);
            i++;
        }
        return partition;
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
        if (this.rows == null) {
            prepareLayout((float) width);
        }
        return this.rows != null ? this.rows.size() : 0;
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
