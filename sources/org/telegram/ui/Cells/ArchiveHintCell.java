package org.telegram.ui.Cells;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BottomPagesView;
import org.telegram.ui.Components.LayoutHelper;

public class ArchiveHintCell extends FrameLayout {
    /* access modifiers changed from: private */
    public BottomPagesView bottomPages;
    private ViewPager viewPager;

    public ArchiveHintCell(Context context) {
        super(context);
        AnonymousClass1 r0 = new ViewPager(context) {
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onInterceptTouchEvent(ev);
            }

            /* access modifiers changed from: protected */
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                requestLayout();
            }
        };
        this.viewPager = r0;
        AndroidUtilities.setViewPagerEdgeEffectColor(r0, Theme.getColor("actionBarDefaultArchived"));
        this.viewPager.setAdapter(new Adapter());
        this.viewPager.setPageMargin(0);
        this.viewPager.setOffscreenPageLimit(1);
        addView(this.viewPager, LayoutHelper.createFrame(-1, -1.0f));
        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                ArchiveHintCell.this.bottomPages.setPageOffset(position, positionOffset);
            }

            public void onPageSelected(int i) {
            }

            public void onPageScrollStateChanged(int i) {
            }
        });
        BottomPagesView bottomPagesView = new BottomPagesView(context, this.viewPager, 3);
        this.bottomPages = bottomPagesView;
        bottomPagesView.setColor("chats_unreadCounterMuted", "chats_actionBackground");
        addView(this.bottomPages, LayoutHelper.createFrame(33, 5.0f, 81, 0.0f, 0.0f, 0.0f, 19.0f));
    }

    public void invalidate() {
        super.invalidate();
        this.bottomPages.invalidate();
    }

    public ViewPager getViewPager() {
        return this.viewPager;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(204.0f), NUM));
    }

    private class Adapter extends PagerAdapter {
        private Adapter() {
        }

        public int getCount() {
            return 3;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            ArchiveHintInnerCell innerCell = new ArchiveHintInnerCell(container.getContext(), position);
            if (innerCell.getParent() != null) {
                ((ViewGroup) innerCell.getParent()).removeView(innerCell);
            }
            container.addView(innerCell, 0);
            return innerCell;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            ArchiveHintCell.this.bottomPages.setCurrentPage(position);
        }

        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        public Parcelable saveState() {
            return null;
        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                super.unregisterDataSetObserver(observer);
            }
        }
    }
}
