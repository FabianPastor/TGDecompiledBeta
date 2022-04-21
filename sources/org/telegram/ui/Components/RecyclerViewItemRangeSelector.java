package org.telegram.ui.Components;

import android.view.MotionEvent;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.AndroidUtilities;

public class RecyclerViewItemRangeSelector implements RecyclerView.OnItemTouchListener {
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
    private boolean isAutoScrolling;
    private int lastDraggedIndex = -1;
    /* access modifiers changed from: private */
    public RecyclerView recyclerView;

    public interface RecyclerViewItemRangeSelectorDelegate {
        int getItemCount();

        boolean isIndexSelectable(int i);

        boolean isSelected(int i);

        void onStartStopSelection(boolean z);

        void setSelected(View view, int i, boolean z);
    }

    public RecyclerViewItemRangeSelector(RecyclerViewItemRangeSelectorDelegate recyclerViewItemRangeSelectorDelegate) {
        this.delegate = recyclerViewItemRangeSelectorDelegate;
    }

    private void disableAutoScroll() {
        this.hotspotHeight = -1;
        this.hotspotOffsetTop = -1;
        this.hotspotOffsetBottom = -1;
    }

    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        boolean result = false;
        boolean adapterIsEmpty = rv.getAdapter() == null || rv.getAdapter().getItemCount() == 0;
        if (this.dragSelectActive && !adapterIsEmpty) {
            result = true;
        }
        if (result) {
            this.recyclerView = rv;
            int i = this.hotspotHeight;
            if (i > -1) {
                int i2 = this.hotspotOffsetTop;
                this.hotspotTopBoundStart = i2;
                this.hotspotTopBoundEnd = i2 + i;
                this.hotspotBottomBoundStart = (rv.getMeasuredHeight() - this.hotspotHeight) - this.hotspotOffsetBottom;
                this.hotspotBottomBoundEnd = rv.getMeasuredHeight() - this.hotspotOffsetBottom;
            }
        }
        if (result && e.getAction() == 1) {
            onDragSelectionStop();
        }
        return result;
    }

    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        int itemPosition;
        View v = rv.findChildViewUnder(e.getX(), e.getY());
        if (v != null) {
            itemPosition = rv.getChildAdapterPosition(v);
        } else {
            itemPosition = -1;
        }
        float y = e.getY();
        switch (e.getAction()) {
            case 1:
                onDragSelectionStop();
                return;
            case 2:
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
                        this.autoScrollVelocity = ((int) ((((float) i3) + y) - ((float) (this.hotspotBottomBoundStart + i3)))) / 2;
                    } else if (this.inTopHotspot || this.inBottomHotspot) {
                        AndroidUtilities.cancelRunOnUIThread(this.autoScrollRunnable);
                        this.inTopHotspot = false;
                        this.inBottomHotspot = false;
                    }
                }
                if (itemPosition != -1 && this.lastDraggedIndex != itemPosition) {
                    this.lastDraggedIndex = itemPosition;
                    RecyclerViewItemRangeSelectorDelegate recyclerViewItemRangeSelectorDelegate = this.delegate;
                    recyclerViewItemRangeSelectorDelegate.setSelected(v, itemPosition, !recyclerViewItemRangeSelectorDelegate.isSelected(itemPosition));
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    public boolean setIsActive(View view, boolean active, int selection, boolean select) {
        if (active && this.dragSelectActive) {
            return false;
        }
        this.lastDraggedIndex = -1;
        AndroidUtilities.cancelRunOnUIThread(this.autoScrollRunnable);
        this.inTopHotspot = false;
        this.inBottomHotspot = false;
        if (!active) {
            this.initialSelection = -1;
            return false;
        } else if (!this.delegate.isIndexSelectable(selection)) {
            this.dragSelectActive = false;
            this.initialSelection = -1;
            return false;
        } else {
            this.delegate.onStartStopSelection(true);
            this.delegate.setSelected(view, this.initialSelection, select);
            this.dragSelectActive = active;
            this.initialSelection = selection;
            this.lastDraggedIndex = selection;
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
