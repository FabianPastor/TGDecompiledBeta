package org.telegram.ui.Components;

import android.view.MotionEvent;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener;
import org.telegram.messenger.AndroidUtilities;

public class RecyclerViewItemRangeSelector implements OnItemTouchListener {
    private static final int AUTO_SCROLL_DELAY = 15;
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
    private int autoScrollVelocity;
    private RecyclerViewItemRangeSelectorDelegate delegate;
    private boolean dragSelectActive;
    private int hotspotBottomBoundEnd;
    private int hotspotBottomBoundStart;
    private int hotspotHeight = AndroidUtilities.dp(80.0f);
    private int hotspotOffsetBottom;
    private int hotspotOffsetTop;
    private int hotspotTopBoundEnd;
    private int hotspotTopBoundStart;
    private boolean inBottomHotspot;
    private boolean inTopHotspot;
    private int initialSelection;
    private boolean isAutoScrolling;
    private int lastDraggedIndex = -1;
    private RecyclerView recyclerView;

    public interface RecyclerViewItemRangeSelectorDelegate {
        int getItemCount();

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

    private void disableAutoScroll() {
        this.hotspotHeight = -1;
        this.hotspotOffsetTop = -1;
        this.hotspotOffsetBottom = -1;
    }

    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        boolean z = false;
        Object obj = (recyclerView.getAdapter() == null || recyclerView.getAdapter().getItemCount() == 0) ? 1 : null;
        if (this.dragSelectActive && obj == null) {
            z = true;
        }
        if (z) {
            this.recyclerView = recyclerView;
            int i = this.hotspotHeight;
            if (i > -1) {
                int i2 = this.hotspotOffsetTop;
                this.hotspotTopBoundStart = i2;
                this.hotspotTopBoundEnd = i2 + i;
                this.hotspotBottomBoundStart = (recyclerView.getMeasuredHeight() - this.hotspotHeight) - this.hotspotOffsetBottom;
                this.hotspotBottomBoundEnd = recyclerView.getMeasuredHeight() - this.hotspotOffsetBottom;
            }
        }
        if (z && motionEvent.getAction() == 1) {
            onDragSelectionStop();
        }
        return z;
    }

    public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        View findChildViewUnder = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        int childAdapterPosition = findChildViewUnder != null ? recyclerView.getChildAdapterPosition(findChildViewUnder) : -1;
        float y = motionEvent.getY();
        int action = motionEvent.getAction();
        if (action != 1) {
            if (action == 2) {
                if (this.hotspotHeight > -1) {
                    if (y >= ((float) this.hotspotTopBoundStart) && y <= ((float) this.hotspotTopBoundEnd)) {
                        this.inBottomHotspot = false;
                        if (!this.inTopHotspot) {
                            this.inTopHotspot = true;
                            AndroidUtilities.cancelRunOnUIThread(this.autoScrollRunnable);
                            AndroidUtilities.runOnUIThread(this.autoScrollRunnable);
                        }
                        action = this.hotspotTopBoundEnd;
                        int i = this.hotspotTopBoundStart;
                        this.autoScrollVelocity = ((int) (((float) (action - i)) - (y - ((float) i)))) / 2;
                    } else if (y >= ((float) this.hotspotBottomBoundStart) && y <= ((float) this.hotspotBottomBoundEnd)) {
                        this.inTopHotspot = false;
                        if (!this.inBottomHotspot) {
                            this.inBottomHotspot = true;
                            AndroidUtilities.cancelRunOnUIThread(this.autoScrollRunnable);
                            AndroidUtilities.runOnUIThread(this.autoScrollRunnable);
                        }
                        action = this.hotspotBottomBoundEnd;
                        this.autoScrollVelocity = ((int) ((y + ((float) action)) - ((float) (this.hotspotBottomBoundStart + action)))) / 2;
                    } else if (this.inTopHotspot || this.inBottomHotspot) {
                        AndroidUtilities.cancelRunOnUIThread(this.autoScrollRunnable);
                        this.inTopHotspot = false;
                        this.inBottomHotspot = false;
                    }
                }
                if (childAdapterPosition != -1 && this.lastDraggedIndex != childAdapterPosition) {
                    this.lastDraggedIndex = childAdapterPosition;
                    RecyclerViewItemRangeSelectorDelegate recyclerViewItemRangeSelectorDelegate = this.delegate;
                    action = this.lastDraggedIndex;
                    recyclerViewItemRangeSelectorDelegate.setSelected(findChildViewUnder, action, recyclerViewItemRangeSelectorDelegate.isSelected(action) ^ 1);
                } else {
                    return;
                }
            }
            return;
        }
        onDragSelectionStop();
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
        } else if (this.delegate.isIndexSelectable(i)) {
            this.delegate.onStartStopSelection(true);
            this.delegate.setSelected(view, this.initialSelection, z2);
            this.dragSelectActive = z;
            this.initialSelection = i;
            this.lastDraggedIndex = i;
            return true;
        } else {
            this.dragSelectActive = false;
            this.initialSelection = -1;
            return false;
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
