package org.telegram.ui.Components;

import android.view.MotionEvent;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.AndroidUtilities;

public class RecyclerViewItemRangeSelector implements RecyclerView.OnItemTouchListener {
    private Runnable autoScrollRunnable = new Runnable() {
        public void run() {
            if (RecyclerViewItemRangeSelector.this.recyclerView != null) {
                if (RecyclerViewItemRangeSelector.this.inTopHotspot) {
                    RecyclerViewItemRangeSelector.this.recyclerView.scrollBy(0, -RecyclerViewItemRangeSelector.this.autoScrollVelocity);
                    AndroidUtilities.runOnUIThread(this);
                } else if (RecyclerViewItemRangeSelector.this.inBottomHotspot) {
                    RecyclerViewItemRangeSelector.this.recyclerView.scrollBy(0, RecyclerViewItemRangeSelector.this.autoScrollVelocity);
                    AndroidUtilities.runOnUIThread(this);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public int autoScrollVelocity;
    private RecyclerViewItemRangeSelectorDelegate delegate;
    private boolean dragSelectActive;
    private int hotspotBottomBoundEnd;
    private int hotspotBottomBoundStart;
    private int hotspotHeight = AndroidUtilities.dp(80.0f);
    private int hotspotOffsetBottom;
    private int hotspotOffsetTop;
    private int hotspotTopBoundEnd;
    private int hotspotTopBoundStart;
    /* access modifiers changed from: private */
    public boolean inBottomHotspot;
    /* access modifiers changed from: private */
    public boolean inTopHotspot;
    private int initialSelection;
    private int lastDraggedIndex = -1;
    /* access modifiers changed from: private */
    public RecyclerView recyclerView;

    public interface RecyclerViewItemRangeSelectorDelegate {
        boolean isIndexSelectable(int i);

        boolean isSelected(int i);

        void onStartStopSelection(boolean z);

        void setSelected(View view, int i, boolean z);
    }

    public void onRequestDisallowInterceptTouchEvent(boolean z) {
    }

    public RecyclerViewItemRangeSelector(RecyclerViewItemRangeSelectorDelegate recyclerViewItemRangeSelectorDelegate) {
        this.delegate = recyclerViewItemRangeSelectorDelegate;
    }

    public boolean onInterceptTouchEvent(RecyclerView recyclerView2, MotionEvent motionEvent) {
        boolean z = false;
        boolean z2 = recyclerView2.getAdapter() == null || recyclerView2.getAdapter().getItemCount() == 0;
        if (this.dragSelectActive && !z2) {
            z = true;
        }
        if (z) {
            this.recyclerView = recyclerView2;
            int i = this.hotspotHeight;
            if (i > -1) {
                int i2 = this.hotspotOffsetTop;
                this.hotspotTopBoundStart = i2;
                this.hotspotTopBoundEnd = i2 + i;
                this.hotspotBottomBoundStart = (recyclerView2.getMeasuredHeight() - this.hotspotHeight) - this.hotspotOffsetBottom;
                this.hotspotBottomBoundEnd = recyclerView2.getMeasuredHeight() - this.hotspotOffsetBottom;
            }
        }
        if (z && motionEvent.getAction() == 1) {
            onDragSelectionStop();
        }
        return z;
    }

    public void onTouchEvent(RecyclerView recyclerView2, MotionEvent motionEvent) {
        View findChildViewUnder = recyclerView2.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        int childAdapterPosition = findChildViewUnder != null ? recyclerView2.getChildAdapterPosition(findChildViewUnder) : -1;
        float y = motionEvent.getY();
        int action = motionEvent.getAction();
        if (action == 1) {
            onDragSelectionStop();
        } else if (action == 2) {
            if (this.hotspotHeight > -1) {
                if (y >= ((float) this.hotspotTopBoundStart) && y <= ((float) this.hotspotTopBoundEnd)) {
                    this.inBottomHotspot = false;
                    if (!this.inTopHotspot) {
                        this.inTopHotspot = true;
                        AndroidUtilities.cancelRunOnUIThread(this.autoScrollRunnable);
                        AndroidUtilities.runOnUIThread(this.autoScrollRunnable);
                    }
                    int i = this.hotspotTopBoundEnd;
                    int i2 = this.hotspotTopBoundStart;
                    this.autoScrollVelocity = ((int) (((float) (i - i2)) - (y - ((float) i2)))) / 2;
                } else if (y >= ((float) this.hotspotBottomBoundStart) && y <= ((float) this.hotspotBottomBoundEnd)) {
                    this.inTopHotspot = false;
                    if (!this.inBottomHotspot) {
                        this.inBottomHotspot = true;
                        AndroidUtilities.cancelRunOnUIThread(this.autoScrollRunnable);
                        AndroidUtilities.runOnUIThread(this.autoScrollRunnable);
                    }
                    int i3 = this.hotspotBottomBoundEnd;
                    this.autoScrollVelocity = ((int) ((y + ((float) i3)) - ((float) (this.hotspotBottomBoundStart + i3)))) / 2;
                } else if (this.inTopHotspot || this.inBottomHotspot) {
                    AndroidUtilities.cancelRunOnUIThread(this.autoScrollRunnable);
                    this.inTopHotspot = false;
                    this.inBottomHotspot = false;
                }
            }
            if (childAdapterPosition != -1 && this.lastDraggedIndex != childAdapterPosition) {
                this.lastDraggedIndex = childAdapterPosition;
                RecyclerViewItemRangeSelectorDelegate recyclerViewItemRangeSelectorDelegate = this.delegate;
                recyclerViewItemRangeSelectorDelegate.setSelected(findChildViewUnder, childAdapterPosition, !recyclerViewItemRangeSelectorDelegate.isSelected(childAdapterPosition));
            }
        }
    }

    public boolean setIsActive(View view, boolean z, int i, boolean z2) {
        if (z && this.dragSelectActive) {
            return false;
        }
        this.lastDraggedIndex = -1;
        AndroidUtilities.cancelRunOnUIThread(this.autoScrollRunnable);
        this.inTopHotspot = false;
        this.inBottomHotspot = false;
        if (!z) {
            this.initialSelection = -1;
            return false;
        } else if (!this.delegate.isIndexSelectable(i)) {
            this.dragSelectActive = false;
            this.initialSelection = -1;
            return false;
        } else {
            this.delegate.onStartStopSelection(true);
            this.delegate.setSelected(view, this.initialSelection, z2);
            this.dragSelectActive = z;
            this.initialSelection = i;
            this.lastDraggedIndex = i;
            return true;
        }
    }

    private void onDragSelectionStop() {
        this.dragSelectActive = false;
        this.inTopHotspot = false;
        this.inBottomHotspot = false;
        AndroidUtilities.cancelRunOnUIThread(this.autoScrollRunnable);
        this.delegate.onStartStopSelection(false);
    }
}
