package org.telegram.messenger.support.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;

public class DiffUtil {
    private static final Comparator<Snake> SNAKE_COMPARATOR = new C06421();

    /* renamed from: org.telegram.messenger.support.util.DiffUtil$1 */
    static class C06421 implements Comparator<Snake> {
        C06421() {
        }

        public int compare(Snake o1, Snake o2) {
            int cmpX = o1.f7x - o2.f7x;
            return cmpX == 0 ? o1.f8y - o2.f8y : cmpX;
        }
    }

    public static abstract class Callback {
        public abstract boolean areContentsTheSame(int i, int i2);

        public abstract boolean areItemsTheSame(int i, int i2);

        public abstract int getNewListSize();

        public abstract int getOldListSize();

        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            return null;
        }
    }

    public static class DiffResult {
        private static final int FLAG_CHANGED = 2;
        private static final int FLAG_IGNORE = 16;
        private static final int FLAG_MASK = 31;
        private static final int FLAG_MOVED_CHANGED = 4;
        private static final int FLAG_MOVED_NOT_CHANGED = 8;
        private static final int FLAG_NOT_CHANGED = 1;
        private static final int FLAG_OFFSET = 5;
        private final Callback mCallback;
        private final boolean mDetectMoves;
        private final int[] mNewItemStatuses;
        private final int mNewListSize;
        private final int[] mOldItemStatuses;
        private final int mOldListSize;
        private final List<Snake> mSnakes;

        DiffResult(Callback callback, List<Snake> snakes, int[] oldItemStatuses, int[] newItemStatuses, boolean detectMoves) {
            this.mSnakes = snakes;
            this.mOldItemStatuses = oldItemStatuses;
            this.mNewItemStatuses = newItemStatuses;
            Arrays.fill(this.mOldItemStatuses, 0);
            Arrays.fill(this.mNewItemStatuses, 0);
            this.mCallback = callback;
            this.mOldListSize = callback.getOldListSize();
            this.mNewListSize = callback.getNewListSize();
            this.mDetectMoves = detectMoves;
            addRootSnake();
            findMatchingItems();
        }

        private void addRootSnake() {
            Snake firstSnake = this.mSnakes.isEmpty() ? null : (Snake) this.mSnakes.get(0);
            if (firstSnake == null || firstSnake.f7x != 0 || firstSnake.f8y != 0) {
                Snake root = new Snake();
                root.f7x = 0;
                root.f8y = 0;
                root.removal = false;
                root.size = 0;
                root.reverse = false;
                this.mSnakes.add(0, root);
            }
        }

        private void findMatchingItems() {
            int posOld = this.mOldListSize;
            int posNew = this.mNewListSize;
            for (int i = this.mSnakes.size() - 1; i >= 0; i--) {
                Snake snake = (Snake) this.mSnakes.get(i);
                int endX = snake.f7x + snake.size;
                int endY = snake.f8y + snake.size;
                if (this.mDetectMoves) {
                    while (posOld > endX) {
                        findAddition(posOld, posNew, i);
                        posOld--;
                    }
                    while (posNew > endY) {
                        findRemoval(posOld, posNew, i);
                        posNew--;
                    }
                }
                for (int j = 0; j < snake.size; j++) {
                    int oldItemPos = snake.f7x + j;
                    int newItemPos = snake.f8y + j;
                    int changeFlag = this.mCallback.areContentsTheSame(oldItemPos, newItemPos) ? 1 : 2;
                    this.mOldItemStatuses[oldItemPos] = (newItemPos << 5) | changeFlag;
                    this.mNewItemStatuses[newItemPos] = (oldItemPos << 5) | changeFlag;
                }
                posOld = snake.f7x;
                posNew = snake.f8y;
            }
        }

        private void findAddition(int x, int y, int snakeIndex) {
            if (this.mOldItemStatuses[x - 1] == 0) {
                findMatchingItem(x, y, snakeIndex, false);
            }
        }

        private void findRemoval(int x, int y, int snakeIndex) {
            if (this.mNewItemStatuses[y - 1] == 0) {
                findMatchingItem(x, y, snakeIndex, true);
            }
        }

        private boolean findMatchingItem(int x, int y, int snakeIndex, boolean removal) {
            int myItemPos;
            int curX;
            int curY;
            DiffResult diffResult = this;
            if (removal) {
                myItemPos = y - 1;
                curX = x;
                curY = y - 1;
            } else {
                myItemPos = x - 1;
                curX = x - 1;
                curY = y;
            }
            int curY2 = curY;
            curY = curX;
            for (curX = snakeIndex; curX >= 0; curX--) {
                Snake snake = (Snake) diffResult.mSnakes.get(curX);
                int endX = snake.f7x + snake.size;
                int endY = snake.f8y + snake.size;
                int changeFlag = 4;
                int pos;
                if (removal) {
                    for (pos = curY - 1; pos >= endX; pos--) {
                        if (diffResult.mCallback.areItemsTheSame(pos, myItemPos)) {
                            if (diffResult.mCallback.areContentsTheSame(pos, myItemPos)) {
                                changeFlag = 8;
                            }
                            diffResult.mNewItemStatuses[myItemPos] = (pos << 5) | 16;
                            diffResult.mOldItemStatuses[pos] = (myItemPos << 5) | changeFlag;
                            return true;
                        }
                    }
                    continue;
                } else {
                    for (pos = curY2 - 1; pos >= endY; pos--) {
                        if (diffResult.mCallback.areItemsTheSame(myItemPos, pos)) {
                            if (diffResult.mCallback.areContentsTheSame(myItemPos, pos)) {
                                changeFlag = 8;
                            }
                            diffResult.mOldItemStatuses[x - 1] = (pos << 5) | 16;
                            diffResult.mNewItemStatuses[pos] = ((x - 1) << 5) | changeFlag;
                            return true;
                        }
                    }
                    continue;
                }
                curY = snake.f7x;
                curY2 = snake.f8y;
            }
            return false;
        }

        public void dispatchUpdatesTo(Adapter adapter) {
            dispatchUpdatesTo(new AdapterListUpdateCallback(adapter));
        }

        public void dispatchUpdatesTo(ListUpdateCallback updateCallback) {
            BatchingListUpdateCallback batchingListUpdateCallback;
            DiffResult diffResult = this;
            ListUpdateCallback listUpdateCallback = updateCallback;
            if (listUpdateCallback instanceof BatchingListUpdateCallback) {
                batchingListUpdateCallback = (BatchingListUpdateCallback) listUpdateCallback;
            } else {
                batchingListUpdateCallback = new BatchingListUpdateCallback(listUpdateCallback);
                Object updateCallback2 = batchingListUpdateCallback;
            }
            ListUpdateCallback updateCallback3 = listUpdateCallback;
            BatchingListUpdateCallback batchingCallback = batchingListUpdateCallback;
            List<PostponedUpdate> postponedUpdates = new ArrayList();
            int posOld = diffResult.mOldListSize;
            int snakeIndex = diffResult.mSnakes.size() - 1;
            int posOld2 = posOld;
            int posNew = diffResult.mNewListSize;
            while (true) {
                int snakeIndex2 = snakeIndex;
                if (snakeIndex2 >= 0) {
                    int endY;
                    Snake snake = (Snake) diffResult.mSnakes.get(snakeIndex2);
                    int snakeSize = snake.size;
                    int endX = snake.f7x + snakeSize;
                    int endY2 = snake.f8y + snakeSize;
                    if (endX < posOld2) {
                        endY = endY2;
                        dispatchRemovals(postponedUpdates, batchingCallback, endX, posOld2 - endX, endX);
                    } else {
                        endY = endY2;
                    }
                    if (endY < posNew) {
                        posOld = snakeSize;
                        dispatchAdditions(postponedUpdates, batchingCallback, endX, posNew - endY, endY);
                    } else {
                        posOld = snakeSize;
                    }
                    snakeSize = posOld - 1;
                    while (true) {
                        int i = snakeSize;
                        if (i < 0) {
                            break;
                        }
                        if ((diffResult.mOldItemStatuses[snake.f7x + i] & FLAG_MASK) == 2) {
                            batchingCallback.onChanged(snake.f7x + i, 1, diffResult.mCallback.getChangePayload(snake.f7x + i, snake.f8y + i));
                        }
                        snakeSize = i - 1;
                    }
                    posOld2 = snake.f7x;
                    posNew = snake.f8y;
                    snakeIndex = snakeIndex2 - 1;
                    endY = 1;
                } else {
                    batchingCallback.dispatchLastEvent();
                    return;
                }
            }
        }

        private static PostponedUpdate removePostponedUpdate(List<PostponedUpdate> updates, int pos, boolean removal) {
            for (int i = updates.size() - 1; i >= 0; i--) {
                PostponedUpdate update = (PostponedUpdate) updates.get(i);
                if (update.posInOwnerList == pos && update.removal == removal) {
                    updates.remove(i);
                    for (int j = i; j < updates.size(); j++) {
                        PostponedUpdate postponedUpdate = (PostponedUpdate) updates.get(j);
                        postponedUpdate.currentPos += removal ? 1 : -1;
                    }
                    return update;
                }
            }
            return null;
        }

        private void dispatchAdditions(List<PostponedUpdate> postponedUpdates, ListUpdateCallback updateCallback, int start, int count, int globalIndex) {
            if (this.mDetectMoves) {
                for (int i = count - 1; i >= 0; i--) {
                    int status = this.mNewItemStatuses[globalIndex + i] & FLAG_MASK;
                    if (status == 0) {
                        updateCallback.onInserted(start, 1);
                        for (PostponedUpdate update : postponedUpdates) {
                            update.currentPos++;
                        }
                    } else if (status == 4 || status == 8) {
                        int pos = this.mNewItemStatuses[globalIndex + i] >> 5;
                        updateCallback.onMoved(removePostponedUpdate(postponedUpdates, pos, true).currentPos, start);
                        if (status == 4) {
                            updateCallback.onChanged(start, 1, this.mCallback.getChangePayload(pos, globalIndex + i));
                        }
                    } else if (status != 16) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("unknown flag for pos ");
                        stringBuilder.append(globalIndex + i);
                        stringBuilder.append(" ");
                        stringBuilder.append(Long.toBinaryString((long) status));
                        throw new IllegalStateException(stringBuilder.toString());
                    } else {
                        postponedUpdates.add(new PostponedUpdate(globalIndex + i, start, false));
                    }
                }
                return;
            }
            updateCallback.onInserted(start, count);
        }

        private void dispatchRemovals(List<PostponedUpdate> postponedUpdates, ListUpdateCallback updateCallback, int start, int count, int globalIndex) {
            if (this.mDetectMoves) {
                for (int i = count - 1; i >= 0; i--) {
                    int status = this.mOldItemStatuses[globalIndex + i] & FLAG_MASK;
                    if (status == 0) {
                        updateCallback.onRemoved(start + i, 1);
                        for (PostponedUpdate update : postponedUpdates) {
                            update.currentPos--;
                        }
                    } else if (status == 4 || status == 8) {
                        int pos = this.mOldItemStatuses[globalIndex + i] >> 5;
                        PostponedUpdate update2 = removePostponedUpdate(postponedUpdates, pos, null);
                        updateCallback.onMoved(start + i, update2.currentPos - 1);
                        if (status == 4) {
                            updateCallback.onChanged(update2.currentPos - 1, 1, this.mCallback.getChangePayload(globalIndex + i, pos));
                        }
                    } else if (status != 16) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("unknown flag for pos ");
                        stringBuilder.append(globalIndex + i);
                        stringBuilder.append(" ");
                        stringBuilder.append(Long.toBinaryString((long) status));
                        throw new IllegalStateException(stringBuilder.toString());
                    } else {
                        postponedUpdates.add(new PostponedUpdate(globalIndex + i, start + i, true));
                    }
                }
                return;
            }
            updateCallback.onRemoved(start, count);
        }

        List<Snake> getSnakes() {
            return this.mSnakes;
        }
    }

    public static abstract class ItemCallback<T> {
        public abstract boolean areContentsTheSame(T t, T t2);

        public abstract boolean areItemsTheSame(T t, T t2);

        public Object getChangePayload(T t, T t2) {
            return null;
        }
    }

    private static class PostponedUpdate {
        int currentPos;
        int posInOwnerList;
        boolean removal;

        public PostponedUpdate(int posInOwnerList, int currentPos, boolean removal) {
            this.posInOwnerList = posInOwnerList;
            this.currentPos = currentPos;
            this.removal = removal;
        }
    }

    static class Range {
        int newListEnd;
        int newListStart;
        int oldListEnd;
        int oldListStart;

        public Range(int oldListStart, int oldListEnd, int newListStart, int newListEnd) {
            this.oldListStart = oldListStart;
            this.oldListEnd = oldListEnd;
            this.newListStart = newListStart;
            this.newListEnd = newListEnd;
        }
    }

    static class Snake {
        boolean removal;
        boolean reverse;
        int size;
        /* renamed from: x */
        int f7x;
        /* renamed from: y */
        int f8y;

        Snake() {
        }
    }

    private DiffUtil() {
    }

    public static DiffResult calculateDiff(Callback cb) {
        return calculateDiff(cb, true);
    }

    public static DiffResult calculateDiff(Callback cb, boolean detectMoves) {
        int oldSize = cb.getOldListSize();
        int newSize = cb.getNewListSize();
        List<Snake> snakes = new ArrayList();
        ArrayList stack = new ArrayList();
        stack.add(new Range(0, oldSize, 0, newSize));
        int max = (oldSize + newSize) + Math.abs(oldSize - newSize);
        int[] forward = new int[(max * 2)];
        int[] iArr = new int[(max * 2)];
        List<Range> rangePool = new ArrayList();
        while (true) {
            List<Range> rangePool2 = rangePool;
            if (stack.isEmpty()) {
                Collections.sort(snakes, SNAKE_COMPARATOR);
                int[] backward = iArr;
                return new DiffResult(cb, snakes, forward, iArr, detectMoves);
            }
            Range range = (Range) stack.remove(stack.size() - 1);
            Snake snake = diffPartial(cb, range.oldListStart, range.oldListEnd, range.newListStart, range.newListEnd, forward, iArr, max);
            if (snake != null) {
                if (snake.size > 0) {
                    snakes.add(snake);
                }
                snake.f7x += range.oldListStart;
                snake.f8y += range.newListStart;
                Range left = rangePool2.isEmpty() ? new Range() : (Range) rangePool2.remove(rangePool2.size() - 1);
                left.oldListStart = range.oldListStart;
                left.newListStart = range.newListStart;
                if (snake.reverse) {
                    left.oldListEnd = snake.f7x;
                    left.newListEnd = snake.f8y;
                } else if (snake.removal) {
                    left.oldListEnd = snake.f7x - 1;
                    left.newListEnd = snake.f8y;
                } else {
                    left.oldListEnd = snake.f7x;
                    left.newListEnd = snake.f8y - 1;
                }
                stack.add(left);
                Range right = range;
                if (!snake.reverse) {
                    right.oldListStart = snake.f7x + snake.size;
                    right.newListStart = snake.f8y + snake.size;
                } else if (snake.removal) {
                    right.oldListStart = (snake.f7x + snake.size) + 1;
                    right.newListStart = snake.f8y + snake.size;
                } else {
                    right.oldListStart = snake.f7x + snake.size;
                    right.newListStart = (snake.f8y + snake.size) + 1;
                }
                stack.add(right);
            } else {
                rangePool2.add(range);
            }
            rangePool = rangePool2;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static Snake diffPartial(Callback cb, int startOld, int endOld, int startNew, int endNew, int[] forward, int[] backward, int kOffset) {
        Callback callback = cb;
        int[] iArr = forward;
        int[] iArr2 = backward;
        int oldSize = endOld - startOld;
        int newSize = endNew - startNew;
        int i;
        if (endOld - startOld < 1) {
            i = newSize;
        } else if (endNew - startNew < 1) {
            r18 = oldSize;
            i = newSize;
        } else {
            int delta = oldSize - newSize;
            int dLimit = ((oldSize + newSize) + 1) / 2;
            Arrays.fill(iArr, (kOffset - dLimit) - 1, (kOffset + dLimit) + 1, 0);
            Arrays.fill(iArr2, ((kOffset - dLimit) - 1) + delta, ((kOffset + dLimit) + 1) + delta, oldSize);
            boolean checkInFwd = delta % 2 != 0;
            int d = 0;
            while (d <= dLimit) {
                int i2;
                boolean z;
                int x;
                boolean removal;
                int y;
                int k = -d;
                while (k <= d) {
                    int y2;
                    int y3;
                    if (k != (-d)) {
                        if (k != d) {
                            i2 = 1;
                            if (iArr[(kOffset + k) - 1] < iArr[(kOffset + k) + 1]) {
                            }
                        } else {
                            z = true;
                        }
                        x = iArr[(kOffset + k) - z] + z;
                        removal = z;
                        y2 = x - k;
                        while (true) {
                            y3 = y2;
                            if (x < oldSize) {
                                break;
                            }
                            y = y3;
                            if (y < newSize) {
                                break;
                            }
                            r18 = oldSize;
                            i = newSize;
                            if (callback.areItemsTheSame(startOld + x, startNew + y) != 0) {
                                break;
                            }
                            x++;
                            y2 = y + 1;
                            oldSize = r18;
                            newSize = i;
                        }
                        r18 = oldSize;
                        i = newSize;
                        iArr[kOffset + k] = x;
                        if (checkInFwd || k < (delta - d) + 1 || k > (delta + d) - 1 || iArr[kOffset + k] < iArr2[kOffset + k]) {
                            k += 2;
                            oldSize = r18;
                            newSize = i;
                        } else {
                            oldSize = new Snake();
                            oldSize.f7x = iArr2[kOffset + k];
                            oldSize.f8y = oldSize.f7x - k;
                            oldSize.size = iArr[kOffset + k] - iArr2[kOffset + k];
                            oldSize.removal = removal;
                            oldSize.reverse = false;
                            return oldSize;
                        }
                    }
                    i2 = 1;
                    x = iArr[(kOffset + k) + i2];
                    removal = false;
                    y2 = x - k;
                    while (true) {
                        y3 = y2;
                        if (x < oldSize) {
                            break;
                        }
                        y = y3;
                        if (y < newSize) {
                            break;
                        }
                        r18 = oldSize;
                        i = newSize;
                        if (callback.areItemsTheSame(startOld + x, startNew + y) != 0) {
                            break;
                        }
                        x++;
                        y2 = y + 1;
                        oldSize = r18;
                        newSize = i;
                    }
                    r18 = oldSize;
                    i = newSize;
                    iArr[kOffset + k] = x;
                    if (checkInFwd) {
                    }
                    k += 2;
                    oldSize = r18;
                    newSize = i;
                }
                r18 = oldSize;
                i = newSize;
                x = -d;
                while (x <= d) {
                    y = x + delta;
                    if (y != d + delta) {
                        if (y != (-d) + delta) {
                            i2 = 1;
                            if (iArr2[(kOffset + y) - 1] < iArr2[(kOffset + y) + 1]) {
                            }
                        } else {
                            z = true;
                        }
                        oldSize = iArr2[(kOffset + y) + z] - z;
                        removal = z;
                        k = oldSize - y;
                        while (oldSize > 0 && k > 0 && callback.areItemsTheSame((startOld + oldSize) - 1, (startNew + k) - 1)) {
                            oldSize--;
                            k--;
                        }
                        iArr2[kOffset + y] = oldSize;
                        if (!checkInFwd || x + delta < (-d) || x + delta > d || iArr[kOffset + y] < iArr2[kOffset + y]) {
                            x += 2;
                        } else {
                            Snake outSnake = new Snake();
                            outSnake.f7x = iArr2[kOffset + y];
                            outSnake.f8y = outSnake.f7x - y;
                            outSnake.size = iArr[kOffset + y] - iArr2[kOffset + y];
                            outSnake.removal = removal;
                            outSnake.reverse = true;
                            return outSnake;
                        }
                    }
                    i2 = 1;
                    oldSize = iArr2[(kOffset + y) - i2];
                    removal = false;
                    k = oldSize - y;
                    while (oldSize > 0) {
                        oldSize--;
                        k--;
                    }
                    iArr2[kOffset + y] = oldSize;
                    if (checkInFwd) {
                    }
                    x += 2;
                }
                d++;
                int i3 = 1;
                oldSize = r18;
                newSize = i;
            }
            i = newSize;
            throw new IllegalStateException("DiffUtil hit an unexpected case while trying to calculate the optimal path. Please make sure your data is not changing during the diff calculation.");
        }
        return null;
    }
}
