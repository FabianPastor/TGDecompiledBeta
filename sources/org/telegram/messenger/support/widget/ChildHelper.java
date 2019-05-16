package org.telegram.messenger.support.widget;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;

class ChildHelper {
    private static final boolean DEBUG = false;
    private static final String TAG = "ChildrenHelper";
    final Bucket mBucket = new Bucket();
    final Callback mCallback;
    final List<View> mHiddenViews = new ArrayList();

    static class Bucket {
        static final int BITS_PER_WORD = 64;
        static final long LAST_BIT = Long.MIN_VALUE;
        long mData = 0;
        Bucket mNext;

        Bucket() {
        }

        /* Access modifiers changed, original: 0000 */
        public void set(int i) {
            if (i >= 64) {
                ensureNext();
                this.mNext.set(i - 64);
                return;
            }
            this.mData |= 1 << i;
        }

        private void ensureNext() {
            if (this.mNext == null) {
                this.mNext = new Bucket();
            }
        }

        /* Access modifiers changed, original: 0000 */
        public void clear(int i) {
            if (i >= 64) {
                Bucket bucket = this.mNext;
                if (bucket != null) {
                    bucket.clear(i - 64);
                    return;
                }
                return;
            }
            this.mData &= (1 << i) ^ -1;
        }

        /* Access modifiers changed, original: 0000 */
        public boolean get(int i) {
            if (i >= 64) {
                ensureNext();
                return this.mNext.get(i - 64);
            }
            return (this.mData & (1 << i)) != 0;
        }

        /* Access modifiers changed, original: 0000 */
        public void reset() {
            this.mData = 0;
            Bucket bucket = this.mNext;
            if (bucket != null) {
                bucket.reset();
            }
        }

        /* Access modifiers changed, original: 0000 */
        public void insert(int i, boolean z) {
            if (i >= 64) {
                ensureNext();
                this.mNext.insert(i - 64, z);
                return;
            }
            boolean z2 = (this.mData & Long.MIN_VALUE) != 0;
            long j = (1 << i) - 1;
            long j2 = this.mData;
            this.mData = ((j2 & (j ^ -1)) << 1) | (j2 & j);
            if (z) {
                set(i);
            } else {
                clear(i);
            }
            if (z2 || this.mNext != null) {
                ensureNext();
                this.mNext.insert(0, z2);
            }
        }

        /* Access modifiers changed, original: 0000 */
        public boolean remove(int i) {
            if (i >= 64) {
                ensureNext();
                return this.mNext.remove(i - 64);
            }
            long j = 1 << i;
            boolean z = (this.mData & j) != 0;
            this.mData &= j ^ -1;
            j--;
            long j2 = this.mData;
            this.mData = Long.rotateRight(j2 & (j ^ -1), 1) | (j2 & j);
            Bucket bucket = this.mNext;
            if (bucket != null) {
                if (bucket.get(0)) {
                    set(63);
                }
                this.mNext.remove(0);
            }
            return z;
        }

        /* Access modifiers changed, original: 0000 */
        public int countOnesBefore(int i) {
            Bucket bucket = this.mNext;
            if (bucket == null) {
                if (i >= 64) {
                    return Long.bitCount(this.mData);
                }
                return Long.bitCount(this.mData & ((1 << i) - 1));
            } else if (i < 64) {
                return Long.bitCount(this.mData & ((1 << i) - 1));
            } else {
                return bucket.countOnesBefore(i - 64) + Long.bitCount(this.mData);
            }
        }

        public String toString() {
            if (this.mNext == null) {
                return Long.toBinaryString(this.mData);
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.mNext.toString());
            stringBuilder.append("xx");
            stringBuilder.append(Long.toBinaryString(this.mData));
            return stringBuilder.toString();
        }
    }

    interface Callback {
        void addView(View view, int i);

        void attachViewToParent(View view, int i, LayoutParams layoutParams);

        void detachViewFromParent(int i);

        View getChildAt(int i);

        int getChildCount();

        ViewHolder getChildViewHolder(View view);

        int indexOfChild(View view);

        void onEnteredHiddenState(View view);

        void onLeftHiddenState(View view);

        void removeAllViews();

        void removeViewAt(int i);
    }

    ChildHelper(Callback callback) {
        this.mCallback = callback;
    }

    private void hideViewInternal(View view) {
        this.mHiddenViews.add(view);
        this.mCallback.onEnteredHiddenState(view);
    }

    private boolean unhideViewInternal(View view) {
        if (!this.mHiddenViews.remove(view)) {
            return false;
        }
        this.mCallback.onLeftHiddenState(view);
        return true;
    }

    /* Access modifiers changed, original: protected */
    public int getHiddenChildCount() {
        return this.mHiddenViews.size();
    }

    public View getHiddenChildAt(int i) {
        return (i < 0 || i >= this.mHiddenViews.size()) ? null : (View) this.mHiddenViews.get(i);
    }

    /* Access modifiers changed, original: 0000 */
    public void addView(View view, boolean z) {
        addView(view, -1, z);
    }

    /* Access modifiers changed, original: 0000 */
    public void addView(View view, int i, boolean z) {
        if (i < 0) {
            i = this.mCallback.getChildCount();
        } else {
            i = getOffset(i);
        }
        this.mBucket.insert(i, z);
        if (z) {
            hideViewInternal(view);
        }
        this.mCallback.addView(view, i);
    }

    private int getOffset(int i) {
        if (i < 0) {
            return -1;
        }
        int childCount = this.mCallback.getChildCount();
        int i2 = i;
        while (i2 < childCount) {
            int countOnesBefore = i - (i2 - this.mBucket.countOnesBefore(i2));
            if (countOnesBefore == 0) {
                while (this.mBucket.get(i2)) {
                    i2++;
                }
                return i2;
            }
            i2 += countOnesBefore;
        }
        return -1;
    }

    /* Access modifiers changed, original: 0000 */
    public void removeView(View view) {
        int indexOfChild = this.mCallback.indexOfChild(view);
        if (indexOfChild >= 0) {
            if (this.mBucket.remove(indexOfChild)) {
                unhideViewInternal(view);
            }
            this.mCallback.removeViewAt(indexOfChild);
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void removeViewAt(int i) {
        i = getOffset(i);
        View childAt = this.mCallback.getChildAt(i);
        if (childAt != null) {
            if (this.mBucket.remove(i)) {
                unhideViewInternal(childAt);
            }
            this.mCallback.removeViewAt(i);
        }
    }

    /* Access modifiers changed, original: 0000 */
    public View getChildAt(int i) {
        return this.mCallback.getChildAt(getOffset(i));
    }

    /* Access modifiers changed, original: 0000 */
    public void removeAllViewsUnfiltered() {
        this.mBucket.reset();
        for (int size = this.mHiddenViews.size() - 1; size >= 0; size--) {
            this.mCallback.onLeftHiddenState((View) this.mHiddenViews.get(size));
            this.mHiddenViews.remove(size);
        }
        this.mCallback.removeAllViews();
    }

    /* Access modifiers changed, original: 0000 */
    public View findHiddenNonRemovedView(int i) {
        int size = this.mHiddenViews.size();
        for (int i2 = 0; i2 < size; i2++) {
            View view = (View) this.mHiddenViews.get(i2);
            ViewHolder childViewHolder = this.mCallback.getChildViewHolder(view);
            if (childViewHolder.getLayoutPosition() == i && !childViewHolder.isInvalid() && !childViewHolder.isRemoved()) {
                return view;
            }
        }
        return null;
    }

    /* Access modifiers changed, original: 0000 */
    public void attachViewToParent(View view, int i, LayoutParams layoutParams, boolean z) {
        if (i < 0) {
            i = this.mCallback.getChildCount();
        } else {
            i = getOffset(i);
        }
        this.mBucket.insert(i, z);
        if (z) {
            hideViewInternal(view);
        }
        this.mCallback.attachViewToParent(view, i, layoutParams);
    }

    /* Access modifiers changed, original: 0000 */
    public int getChildCount() {
        return this.mCallback.getChildCount() - this.mHiddenViews.size();
    }

    /* Access modifiers changed, original: 0000 */
    public int getUnfilteredChildCount() {
        return this.mCallback.getChildCount();
    }

    /* Access modifiers changed, original: 0000 */
    public View getUnfilteredChildAt(int i) {
        return this.mCallback.getChildAt(i);
    }

    /* Access modifiers changed, original: 0000 */
    public void detachViewFromParent(int i) {
        i = getOffset(i);
        this.mBucket.remove(i);
        this.mCallback.detachViewFromParent(i);
    }

    /* Access modifiers changed, original: 0000 */
    public int indexOfChild(View view) {
        int indexOfChild = this.mCallback.indexOfChild(view);
        if (indexOfChild == -1 || this.mBucket.get(indexOfChild)) {
            return -1;
        }
        return indexOfChild - this.mBucket.countOnesBefore(indexOfChild);
    }

    /* Access modifiers changed, original: 0000 */
    public boolean isHidden(View view) {
        return this.mHiddenViews.contains(view);
    }

    /* Access modifiers changed, original: 0000 */
    public void hide(View view) {
        int indexOfChild = this.mCallback.indexOfChild(view);
        if (indexOfChild >= 0) {
            this.mBucket.set(indexOfChild);
            hideViewInternal(view);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("view is not a child, cannot hide ");
        stringBuilder.append(view);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    /* Access modifiers changed, original: 0000 */
    public void unhide(View view) {
        int indexOfChild = this.mCallback.indexOfChild(view);
        StringBuilder stringBuilder;
        if (indexOfChild < 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("view is not a child, cannot hide ");
            stringBuilder.append(view);
            throw new IllegalArgumentException(stringBuilder.toString());
        } else if (this.mBucket.get(indexOfChild)) {
            this.mBucket.clear(indexOfChild);
            unhideViewInternal(view);
        } else {
            stringBuilder = new StringBuilder();
            stringBuilder.append("trying to unhide a view that was not hidden");
            stringBuilder.append(view);
            throw new RuntimeException(stringBuilder.toString());
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.mBucket.toString());
        stringBuilder.append(", hidden list:");
        stringBuilder.append(this.mHiddenViews.size());
        return stringBuilder.toString();
    }

    /* Access modifiers changed, original: 0000 */
    public boolean removeViewIfHidden(View view) {
        int indexOfChild = this.mCallback.indexOfChild(view);
        if (indexOfChild == -1) {
            unhideViewInternal(view);
            return true;
        } else if (!this.mBucket.get(indexOfChild)) {
            return false;
        } else {
            this.mBucket.remove(indexOfChild);
            unhideViewInternal(view);
            this.mCallback.removeViewAt(indexOfChild);
            return true;
        }
    }
}
