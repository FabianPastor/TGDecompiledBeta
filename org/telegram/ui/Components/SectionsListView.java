package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Adapters.BaseSectionsAdapter;

public class SectionsListView extends ListView implements OnScrollListener {
    private int currentStartSection = -1;
    private BaseSectionsAdapter mAdapter;
    private OnScrollListener mOnScrollListener;
    private View pinnedHeader;

    public SectionsListView(Context context) {
        super(context);
        super.setOnScrollListener(this);
    }

    public SectionsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setOnScrollListener(this);
    }

    public SectionsListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        super.setOnScrollListener(this);
    }

    public void setAdapter(ListAdapter adapter) {
        if (this.mAdapter != adapter) {
            this.pinnedHeader = null;
            if (adapter instanceof BaseSectionsAdapter) {
                this.mAdapter = (BaseSectionsAdapter) adapter;
            } else {
                this.mAdapter = null;
            }
            super.setAdapter(adapter);
        }
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (this.mOnScrollListener != null) {
            this.mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
        if (this.mAdapter != null && this.mAdapter.getCount() != 0) {
            int startSection = this.mAdapter.getSectionForPosition(firstVisibleItem);
            if (this.currentStartSection != startSection || this.pinnedHeader == null) {
                this.pinnedHeader = getSectionHeaderView(startSection, this.pinnedHeader);
                this.currentStartSection = startSection;
            }
            if (this.mAdapter.getPositionInSectionForPosition(firstVisibleItem) == this.mAdapter.getCountForSection(startSection) - 1) {
                View child = getChildAt(0);
                int headerHeight = this.pinnedHeader.getHeight();
                int headerTop = 0;
                if (child != null) {
                    int available = child.getTop() + child.getHeight();
                    if (available < headerHeight) {
                        headerTop = available - headerHeight;
                    }
                } else {
                    headerTop = -AndroidUtilities.dp(100.0f);
                }
                if (headerTop < 0) {
                    this.pinnedHeader.setTag(Integer.valueOf(headerTop));
                } else {
                    this.pinnedHeader.setTag(Integer.valueOf(0));
                }
            } else {
                this.pinnedHeader.setTag(Integer.valueOf(0));
            }
            invalidate();
        }
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (this.mOnScrollListener != null) {
            this.mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    private View getSectionHeaderView(int section, View oldView) {
        boolean shouldLayout;
        if (oldView == null) {
            shouldLayout = true;
        } else {
            shouldLayout = false;
        }
        View view = this.mAdapter.getSectionHeaderView(section, oldView, this);
        if (shouldLayout) {
            ensurePinnedHeaderLayout(view, false);
        }
        return view;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (this.mAdapter != null && this.pinnedHeader != null) {
            ensurePinnedHeaderLayout(this.pinnedHeader, true);
        }
    }

    private void ensurePinnedHeaderLayout(View header, boolean forceLayout) {
        if (header.isLayoutRequested() || forceLayout) {
            try {
                header.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), NUM), MeasureSpec.makeMeasureSpec(0, 0));
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
            }
            header.layout(0, 0, header.getMeasuredWidth(), header.getMeasuredHeight());
        }
    }

    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.mAdapter != null && this.pinnedHeader != null) {
            int saveCount = canvas.save();
            canvas.translate(LocaleController.isRTL ? (float) (getWidth() - this.pinnedHeader.getWidth()) : 0.0f, (float) ((Integer) this.pinnedHeader.getTag()).intValue());
            canvas.clipRect(0, 0, getWidth(), this.pinnedHeader.getMeasuredHeight());
            this.pinnedHeader.draw(canvas);
            canvas.restoreToCount(saveCount);
        }
    }

    public void setOnScrollListener(OnScrollListener l) {
        this.mOnScrollListener = l;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        super.setOnItemClickListener(listener);
    }
}
