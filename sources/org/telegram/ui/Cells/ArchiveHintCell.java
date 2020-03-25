package org.telegram.ui.Cells;

import android.content.Context;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BottomPagesView;
import org.telegram.ui.Components.LayoutHelper;

public class ArchiveHintCell extends FrameLayout {
    /* access modifiers changed from: private */
    public BottomPagesView bottomPages;
    private ViewPager viewPager;

    public ArchiveHintCell(Context context) {
        super(context);
        AnonymousClass1 r0 = new ViewPager(this, context) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onInterceptTouchEvent(motionEvent);
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
            public void onPageScrolled(int i, float f, int i2) {
                ArchiveHintCell.this.bottomPages.setPageOffset(i, f);
            }

            public void onPageSelected(int i) {
                FileLog.d("test1");
            }

            public void onPageScrollStateChanged(int i) {
                FileLog.d("test1");
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
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(204.0f), NUM));
    }

    private class Adapter extends PagerAdapter {
        public int getCount() {
            return 3;
        }

        public void restoreState(Parcelable parcelable, ClassLoader classLoader) {
        }

        public Parcelable saveState() {
            return null;
        }

        private Adapter() {
        }

        public Object instantiateItem(ViewGroup viewGroup, int i) {
            ArchiveHintInnerCell archiveHintInnerCell = new ArchiveHintInnerCell(viewGroup.getContext(), i);
            if (archiveHintInnerCell.getParent() != null) {
                ((ViewGroup) archiveHintInnerCell.getParent()).removeView(archiveHintInnerCell);
            }
            viewGroup.addView(archiveHintInnerCell, 0);
            return archiveHintInnerCell;
        }

        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) obj);
        }

        public void setPrimaryItem(ViewGroup viewGroup, int i, Object obj) {
            super.setPrimaryItem(viewGroup, i, obj);
            ArchiveHintCell.this.bottomPages.setCurrentPage(i);
        }

        public boolean isViewFromObject(View view, Object obj) {
            return view.equals(obj);
        }
    }
}
