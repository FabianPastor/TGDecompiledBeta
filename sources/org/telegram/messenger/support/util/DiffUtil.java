package org.telegram.messenger.support.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;

public class DiffUtil {
    private static final Comparator<Snake> SNAKE_COMPARATOR = new Comparator<Snake>() {
        public int compare(Snake snake, Snake snake2) {
            int i = snake.x - snake2.x;
            return i == 0 ? snake.y - snake2.y : i;
        }
    };

    public static abstract class Callback {
        public abstract boolean areContentsTheSame(int i, int i2);

        public abstract boolean areItemsTheSame(int i, int i2);

        public Object getChangePayload(int i, int i2) {
            return null;
        }

        public abstract int getNewListSize();

        public abstract int getOldListSize();
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

        DiffResult(Callback callback, List<Snake> list, int[] iArr, int[] iArr2, boolean z) {
            this.mSnakes = list;
            this.mOldItemStatuses = iArr;
            this.mNewItemStatuses = iArr2;
            Arrays.fill(this.mOldItemStatuses, 0);
            Arrays.fill(this.mNewItemStatuses, 0);
            this.mCallback = callback;
            this.mOldListSize = callback.getOldListSize();
            this.mNewListSize = callback.getNewListSize();
            this.mDetectMoves = z;
            addRootSnake();
            findMatchingItems();
        }

        private void addRootSnake() {
            Snake snake = this.mSnakes.isEmpty() ? null : (Snake) this.mSnakes.get(0);
            if (snake == null || snake.x != 0 || snake.y != 0) {
                snake = new Snake();
                snake.x = 0;
                snake.y = 0;
                snake.removal = false;
                snake.size = 0;
                snake.reverse = false;
                this.mSnakes.add(0, snake);
            }
        }

        private void findMatchingItems() {
            int i = this.mOldListSize;
            int i2 = this.mNewListSize;
            for (int size = this.mSnakes.size() - 1; size >= 0; size--) {
                Snake snake = (Snake) this.mSnakes.get(size);
                int i3 = snake.x;
                int i4 = snake.size;
                i3 += i4;
                int i5 = snake.y + i4;
                if (this.mDetectMoves) {
                    while (i > i3) {
                        findAddition(i, i2, size);
                        i--;
                    }
                    while (i2 > i5) {
                        findRemoval(i, i2, size);
                        i2--;
                    }
                }
                for (i = 0; i < snake.size; i++) {
                    i2 = snake.x + i;
                    i3 = snake.y + i;
                    i4 = this.mCallback.areContentsTheSame(i2, i3) ? 1 : 2;
                    this.mOldItemStatuses[i2] = (i3 << 5) | i4;
                    this.mNewItemStatuses[i3] = (i2 << 5) | i4;
                }
                i = snake.x;
                i2 = snake.y;
            }
        }

        private void findAddition(int i, int i2, int i3) {
            if (this.mOldItemStatuses[i - 1] == 0) {
                findMatchingItem(i, i2, i3, false);
            }
        }

        private void findRemoval(int i, int i2, int i3) {
            if (this.mNewItemStatuses[i2 - 1] == 0) {
                findMatchingItem(i, i2, i3, true);
            }
        }

        private boolean findMatchingItem(int i, int i2, int i3, boolean z) {
            int i4;
            int i5;
            if (z) {
                i2--;
                i4 = i;
                i5 = i2;
            } else {
                i4 = i - 1;
                i5 = i4;
            }
            while (i3 >= 0) {
                Snake snake = (Snake) this.mSnakes.get(i3);
                int i6 = snake.x;
                int i7 = snake.size;
                i6 += i7;
                int i8 = snake.y + i7;
                i7 = 8;
                if (z) {
                    for (i4--; i4 >= i6; i4--) {
                        if (this.mCallback.areItemsTheSame(i4, i5)) {
                            if (!this.mCallback.areContentsTheSame(i4, i5)) {
                                i7 = 4;
                            }
                            this.mNewItemStatuses[i5] = (i4 << 5) | 16;
                            this.mOldItemStatuses[i4] = (i5 << 5) | i7;
                            return true;
                        }
                    }
                    continue;
                } else {
                    for (i2--; i2 >= i8; i2--) {
                        if (this.mCallback.areItemsTheSame(i5, i2)) {
                            if (!this.mCallback.areContentsTheSame(i5, i2)) {
                                i7 = 4;
                            }
                            i--;
                            this.mOldItemStatuses[i] = (i2 << 5) | 16;
                            this.mNewItemStatuses[i2] = (i << 5) | i7;
                            return true;
                        }
                    }
                    continue;
                }
                i4 = snake.x;
                i2 = snake.y;
                i3--;
            }
            return false;
        }

        public void dispatchUpdatesTo(Adapter adapter) {
            dispatchUpdatesTo(new AdapterListUpdateCallback(adapter));
        }

        public void dispatchUpdatesTo(ListUpdateCallback listUpdateCallback) {
            BatchingListUpdateCallback batchingListUpdateCallback;
            if (listUpdateCallback instanceof BatchingListUpdateCallback) {
                batchingListUpdateCallback = (BatchingListUpdateCallback) listUpdateCallback;
            } else {
                batchingListUpdateCallback = new BatchingListUpdateCallback(listUpdateCallback);
            }
            ArrayList arrayList = new ArrayList();
            int i = this.mOldListSize;
            int i2 = this.mNewListSize;
            for (int size = this.mSnakes.size() - 1; size >= 0; size--) {
                Snake snake = (Snake) this.mSnakes.get(size);
                int i3 = snake.size;
                int i4 = snake.x + i3;
                int i5 = snake.y + i3;
                if (i4 < i) {
                    dispatchRemovals(arrayList, batchingListUpdateCallback, i4, i - i4, i4);
                }
                if (i5 < i2) {
                    dispatchAdditions(arrayList, batchingListUpdateCallback, i4, i2 - i5, i5);
                }
                for (i3--; i3 >= 0; i3--) {
                    int[] iArr = this.mOldItemStatuses;
                    int i6 = snake.x;
                    if ((iArr[i6 + i3] & 31) == 2) {
                        batchingListUpdateCallback.onChanged(i6 + i3, 1, this.mCallback.getChangePayload(i6 + i3, snake.y + i3));
                    }
                }
                i = snake.x;
                i2 = snake.y;
            }
            batchingListUpdateCallback.dispatchLastEvent();
        }

        private static PostponedUpdate removePostponedUpdate(List<PostponedUpdate> list, int i, boolean z) {
            int size = list.size() - 1;
            while (size >= 0) {
                PostponedUpdate postponedUpdate = (PostponedUpdate) list.get(size);
                if (postponedUpdate.posInOwnerList == i && postponedUpdate.removal == z) {
                    list.remove(size);
                    while (size < list.size()) {
                        PostponedUpdate postponedUpdate2 = (PostponedUpdate) list.get(size);
                        postponedUpdate2.currentPos += z ? 1 : -1;
                        size++;
                    }
                    return postponedUpdate;
                }
                size--;
            }
            return null;
        }

        private void dispatchAdditions(List<PostponedUpdate> list, ListUpdateCallback listUpdateCallback, int i, int i2, int i3) {
            if (this.mDetectMoves) {
                for (i2--; i2 >= 0; i2--) {
                    int i4 = i3 + i2;
                    int i5 = this.mNewItemStatuses[i4] & 31;
                    if (i5 == 0) {
                        listUpdateCallback.onInserted(i, 1);
                        for (PostponedUpdate postponedUpdate : list) {
                            postponedUpdate.currentPos++;
                        }
                    } else if (i5 == 4 || i5 == 8) {
                        int i6 = this.mNewItemStatuses[i4] >> 5;
                        listUpdateCallback.onMoved(removePostponedUpdate(list, i6, true).currentPos, i);
                        if (i5 == 4) {
                            listUpdateCallback.onChanged(i, 1, this.mCallback.getChangePayload(i6, i4));
                        }
                    } else if (i5 == 16) {
                        list.add(new PostponedUpdate(i4, i, false));
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("unknown flag for pos ");
                        stringBuilder.append(i4);
                        stringBuilder.append(" ");
                        stringBuilder.append(Long.toBinaryString((long) i5));
                        throw new IllegalStateException(stringBuilder.toString());
                    }
                }
                return;
            }
            listUpdateCallback.onInserted(i, i2);
        }

        private void dispatchRemovals(List<PostponedUpdate> list, ListUpdateCallback listUpdateCallback, int i, int i2, int i3) {
            if (this.mDetectMoves) {
                for (i2--; i2 >= 0; i2--) {
                    int i4 = i3 + i2;
                    int i5 = this.mOldItemStatuses[i4] & 31;
                    if (i5 == 0) {
                        listUpdateCallback.onRemoved(i + i2, 1);
                        for (PostponedUpdate postponedUpdate : list) {
                            postponedUpdate.currentPos--;
                        }
                    } else if (i5 == 4 || i5 == 8) {
                        int i6 = this.mOldItemStatuses[i4] >> 5;
                        PostponedUpdate removePostponedUpdate = removePostponedUpdate(list, i6, false);
                        listUpdateCallback.onMoved(i + i2, removePostponedUpdate.currentPos - 1);
                        if (i5 == 4) {
                            listUpdateCallback.onChanged(removePostponedUpdate.currentPos - 1, 1, this.mCallback.getChangePayload(i4, i6));
                        }
                    } else if (i5 == 16) {
                        list.add(new PostponedUpdate(i4, i + i2, true));
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("unknown flag for pos ");
                        stringBuilder.append(i4);
                        stringBuilder.append(" ");
                        stringBuilder.append(Long.toBinaryString((long) i5));
                        throw new IllegalStateException(stringBuilder.toString());
                    }
                }
                return;
            }
            listUpdateCallback.onRemoved(i, i2);
        }

        /* Access modifiers changed, original: 0000 */
        public List<Snake> getSnakes() {
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

        public PostponedUpdate(int i, int i2, boolean z) {
            this.posInOwnerList = i;
            this.currentPos = i2;
            this.removal = z;
        }
    }

    static class Range {
        int newListEnd;
        int newListStart;
        int oldListEnd;
        int oldListStart;

        public Range(int i, int i2, int i3, int i4) {
            this.oldListStart = i;
            this.oldListEnd = i2;
            this.newListStart = i3;
            this.newListEnd = i4;
        }
    }

    static class Snake {
        boolean removal;
        boolean reverse;
        int size;
        int x;
        int y;

        Snake() {
        }
    }

    private DiffUtil() {
    }

    public static DiffResult calculateDiff(Callback callback) {
        return calculateDiff(callback, true);
    }

    public static DiffResult calculateDiff(Callback callback, boolean z) {
        int oldListSize = callback.getOldListSize();
        int newListSize = callback.getNewListSize();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(new Range(0, oldListSize, 0, newListSize));
        oldListSize = Math.abs(oldListSize - newListSize) + (oldListSize + newListSize);
        newListSize = oldListSize * 2;
        int[] iArr = new int[newListSize];
        int[] iArr2 = new int[newListSize];
        ArrayList arrayList3 = new ArrayList();
        while (!arrayList2.isEmpty()) {
            Range range = (Range) arrayList2.remove(arrayList2.size() - 1);
            Snake diffPartial = diffPartial(callback, range.oldListStart, range.oldListEnd, range.newListStart, range.newListEnd, iArr, iArr2, oldListSize);
            if (diffPartial != null) {
                if (diffPartial.size > 0) {
                    arrayList.add(diffPartial);
                }
                diffPartial.x += range.oldListStart;
                diffPartial.y += range.newListStart;
                Range range2 = arrayList3.isEmpty() ? new Range() : (Range) arrayList3.remove(arrayList3.size() - 1);
                range2.oldListStart = range.oldListStart;
                range2.newListStart = range.newListStart;
                if (diffPartial.reverse) {
                    range2.oldListEnd = diffPartial.x;
                    range2.newListEnd = diffPartial.y;
                } else if (diffPartial.removal) {
                    range2.oldListEnd = diffPartial.x - 1;
                    range2.newListEnd = diffPartial.y;
                } else {
                    range2.oldListEnd = diffPartial.x;
                    range2.newListEnd = diffPartial.y - 1;
                }
                arrayList2.add(range2);
                int i;
                int i2;
                if (!diffPartial.reverse) {
                    i = diffPartial.x;
                    i2 = diffPartial.size;
                    range.oldListStart = i + i2;
                    range.newListStart = diffPartial.y + i2;
                } else if (diffPartial.removal) {
                    i = diffPartial.x;
                    i2 = diffPartial.size;
                    range.oldListStart = (i + i2) + 1;
                    range.newListStart = diffPartial.y + i2;
                } else {
                    i = diffPartial.x;
                    i2 = diffPartial.size;
                    range.oldListStart = i + i2;
                    range.newListStart = (diffPartial.y + i2) + 1;
                }
                arrayList2.add(range);
            } else {
                arrayList3.add(range);
            }
        }
        Collections.sort(arrayList, SNAKE_COMPARATOR);
        return new DiffResult(callback, arrayList, iArr, iArr2, z);
    }

    /* JADX WARNING: Removed duplicated region for block: B:83:0x00ee A:{SYNTHETIC, EDGE_INSN: B:83:0x00ee->B:53:0x00ee ?: BREAK  , EDGE_INSN: B:83:0x00ee->B:53:0x00ee ?: BREAK  } */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00e3 A:{LOOP_END, LOOP:4: B:47:0x00cf->B:51:0x00e3} */
    /* JADX WARNING: Missing block: B:14:0x0042, code skipped:
            if (r1[r13 - 1] < r1[r13 + r5]) goto L_0x004d;
     */
    /* JADX WARNING: Missing block: B:41:0x00ba, code skipped:
            if (r2[r13 - 1] < r2[r13 + 1]) goto L_0x00c7;
     */
    private static org.telegram.messenger.support.util.DiffUtil.Snake diffPartial(org.telegram.messenger.support.util.DiffUtil.Callback r19, int r20, int r21, int r22, int r23, int[] r24, int[] r25, int r26) {
        /*
        r0 = r19;
        r1 = r24;
        r2 = r25;
        r3 = r21 - r20;
        r4 = r23 - r22;
        r5 = 1;
        if (r3 < r5) goto L_0x0133;
    L_0x000d:
        if (r4 >= r5) goto L_0x0011;
    L_0x000f:
        goto L_0x0133;
    L_0x0011:
        r6 = r3 - r4;
        r7 = r3 + r4;
        r7 = r7 + r5;
        r7 = r7 / 2;
        r8 = r26 - r7;
        r8 = r8 - r5;
        r9 = r26 + r7;
        r9 = r9 + r5;
        r10 = 0;
        java.util.Arrays.fill(r1, r8, r9, r10);
        r8 = r8 + r6;
        r9 = r9 + r6;
        java.util.Arrays.fill(r2, r8, r9, r3);
        r8 = r6 % 2;
        if (r8 == 0) goto L_0x002d;
    L_0x002b:
        r8 = 1;
        goto L_0x002e;
    L_0x002d:
        r8 = 0;
    L_0x002e:
        r9 = 0;
    L_0x002f:
        if (r9 > r7) goto L_0x012b;
    L_0x0031:
        r11 = -r9;
        r12 = r11;
    L_0x0033:
        if (r12 > r9) goto L_0x00a2;
    L_0x0035:
        if (r12 == r11) goto L_0x004d;
    L_0x0037:
        if (r12 == r9) goto L_0x0045;
    L_0x0039:
        r13 = r26 + r12;
        r14 = r13 + -1;
        r14 = r1[r14];
        r13 = r13 + r5;
        r13 = r1[r13];
        if (r14 >= r13) goto L_0x0045;
    L_0x0044:
        goto L_0x004d;
    L_0x0045:
        r13 = r26 + r12;
        r13 = r13 - r5;
        r13 = r1[r13];
        r13 = r13 + r5;
        r14 = 1;
        goto L_0x0053;
    L_0x004d:
        r13 = r26 + r12;
        r13 = r13 + r5;
        r13 = r1[r13];
        r14 = 0;
    L_0x0053:
        r15 = r13 - r12;
    L_0x0055:
        if (r13 >= r3) goto L_0x006a;
    L_0x0057:
        if (r15 >= r4) goto L_0x006a;
    L_0x0059:
        r10 = r20 + r13;
        r5 = r22 + r15;
        r5 = r0.areItemsTheSame(r10, r5);
        if (r5 == 0) goto L_0x006a;
    L_0x0063:
        r13 = r13 + 1;
        r15 = r15 + 1;
        r5 = 1;
        r10 = 0;
        goto L_0x0055;
    L_0x006a:
        r5 = r26 + r12;
        r1[r5] = r13;
        if (r8 == 0) goto L_0x009c;
    L_0x0070:
        r10 = r6 - r9;
        r13 = 1;
        r10 = r10 + r13;
        if (r12 < r10) goto L_0x009c;
    L_0x0076:
        r10 = r6 + r9;
        r10 = r10 - r13;
        if (r12 > r10) goto L_0x009c;
    L_0x007b:
        r10 = r1[r5];
        r13 = r2[r5];
        if (r10 < r13) goto L_0x009c;
    L_0x0081:
        r0 = new org.telegram.messenger.support.util.DiffUtil$Snake;
        r0.<init>();
        r3 = r2[r5];
        r0.x = r3;
        r3 = r0.x;
        r3 = r3 - r12;
        r0.y = r3;
        r1 = r1[r5];
        r2 = r2[r5];
        r1 = r1 - r2;
        r0.size = r1;
        r0.removal = r14;
        r5 = 0;
        r0.reverse = r5;
        return r0;
    L_0x009c:
        r5 = 0;
        r12 = r12 + 2;
        r5 = 1;
        r10 = 0;
        goto L_0x0033;
    L_0x00a2:
        r5 = 0;
        r10 = r11;
    L_0x00a4:
        if (r10 > r9) goto L_0x0120;
    L_0x00a6:
        r12 = r10 + r6;
        r13 = r9 + r6;
        if (r12 == r13) goto L_0x00c6;
    L_0x00ac:
        r13 = r11 + r6;
        if (r12 == r13) goto L_0x00bd;
    L_0x00b0:
        r13 = r26 + r12;
        r14 = r13 + -1;
        r14 = r2[r14];
        r15 = 1;
        r13 = r13 + r15;
        r13 = r2[r13];
        if (r14 >= r13) goto L_0x00be;
    L_0x00bc:
        goto L_0x00c7;
    L_0x00bd:
        r15 = 1;
    L_0x00be:
        r13 = r26 + r12;
        r13 = r13 + r15;
        r13 = r2[r13];
        r13 = r13 - r15;
        r14 = 1;
        goto L_0x00cd;
    L_0x00c6:
        r15 = 1;
    L_0x00c7:
        r13 = r26 + r12;
        r13 = r13 - r15;
        r13 = r2[r13];
        r14 = 0;
    L_0x00cd:
        r16 = r13 - r12;
    L_0x00cf:
        if (r13 <= 0) goto L_0x00ec;
    L_0x00d1:
        if (r16 <= 0) goto L_0x00ec;
    L_0x00d3:
        r17 = r20 + r13;
        r5 = r17 + -1;
        r17 = r22 + r16;
        r18 = r3;
        r3 = r17 + -1;
        r3 = r0.areItemsTheSame(r5, r3);
        if (r3 == 0) goto L_0x00ee;
    L_0x00e3:
        r13 = r13 + -1;
        r16 = r16 + -1;
        r3 = r18;
        r5 = 0;
        r15 = 1;
        goto L_0x00cf;
    L_0x00ec:
        r18 = r3;
    L_0x00ee:
        r3 = r26 + r12;
        r2[r3] = r13;
        if (r8 != 0) goto L_0x0119;
    L_0x00f4:
        if (r12 < r11) goto L_0x0119;
    L_0x00f6:
        if (r12 > r9) goto L_0x0119;
    L_0x00f8:
        r5 = r1[r3];
        r13 = r2[r3];
        if (r5 < r13) goto L_0x0119;
    L_0x00fe:
        r0 = new org.telegram.messenger.support.util.DiffUtil$Snake;
        r0.<init>();
        r4 = r2[r3];
        r0.x = r4;
        r4 = r0.x;
        r4 = r4 - r12;
        r0.y = r4;
        r1 = r1[r3];
        r2 = r2[r3];
        r1 = r1 - r2;
        r0.size = r1;
        r0.removal = r14;
        r3 = 1;
        r0.reverse = r3;
        return r0;
    L_0x0119:
        r3 = 1;
        r10 = r10 + 2;
        r3 = r18;
        r5 = 0;
        goto L_0x00a4;
    L_0x0120:
        r18 = r3;
        r3 = 1;
        r9 = r9 + 1;
        r3 = r18;
        r5 = 1;
        r10 = 0;
        goto L_0x002f;
    L_0x012b:
        r0 = new java.lang.IllegalStateException;
        r1 = "DiffUtil hit an unexpected case while trying to calculate the optimal path. Please make sure your data is not changing during the diff calculation.";
        r0.<init>(r1);
        throw r0;
    L_0x0133:
        r0 = 0;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.support.util.DiffUtil.diffPartial(org.telegram.messenger.support.util.DiffUtil$Callback, int, int, int, int, int[], int[], int):org.telegram.messenger.support.util.DiffUtil$Snake");
    }
}
