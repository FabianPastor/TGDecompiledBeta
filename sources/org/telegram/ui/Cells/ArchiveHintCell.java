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
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BottomPagesView;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes3.dex */
public class ArchiveHintCell extends FrameLayout {
    private BottomPagesView bottomPages;
    private ViewPager viewPager;

    public ArchiveHintCell(Context context) {
        super(context);
        ViewPager viewPager = new ViewPager(this, context) { // from class: org.telegram.ui.Cells.ArchiveHintCell.1
            @Override // androidx.viewpager.widget.ViewPager, android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onInterceptTouchEvent(motionEvent);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // androidx.viewpager.widget.ViewPager, android.view.ViewGroup, android.view.View
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                requestLayout();
            }
        };
        this.viewPager = viewPager;
        AndroidUtilities.setViewPagerEdgeEffectColor(viewPager, Theme.getColor("actionBarDefaultArchived"));
        this.viewPager.setAdapter(new Adapter());
        this.viewPager.setPageMargin(0);
        this.viewPager.setOffscreenPageLimit(1);
        addView(this.viewPager, LayoutHelper.createFrame(-1, -1.0f));
        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: org.telegram.ui.Cells.ArchiveHintCell.2
            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrollStateChanged(int i) {
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageSelected(int i) {
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrolled(int i, float f, int i2) {
                ArchiveHintCell.this.bottomPages.setPageOffset(i, f);
            }
        });
        BottomPagesView bottomPagesView = new BottomPagesView(context, this.viewPager, 3);
        this.bottomPages = bottomPagesView;
        bottomPagesView.setColor("chats_unreadCounterMuted", "chats_actionBackground");
        addView(this.bottomPages, LayoutHelper.createFrame(33, 5.0f, 81, 0.0f, 0.0f, 0.0f, 19.0f));
    }

    @Override // android.view.View
    public void invalidate() {
        super.invalidate();
        this.bottomPages.invalidate();
    }

    public ViewPager getViewPager() {
        return this.viewPager;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(204.0f), NUM));
    }

    /* loaded from: classes3.dex */
    private class Adapter extends PagerAdapter {
        @Override // androidx.viewpager.widget.PagerAdapter
        public int getCount() {
            return 3;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public void restoreState(Parcelable parcelable, ClassLoader classLoader) {
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public Parcelable saveState() {
            return null;
        }

        private Adapter() {
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        /* renamed from: instantiateItem */
        public Object mo1624instantiateItem(ViewGroup viewGroup, int i) {
            ArchiveHintInnerCell archiveHintInnerCell = new ArchiveHintInnerCell(viewGroup.getContext(), i);
            if (archiveHintInnerCell.getParent() != null) {
                ((ViewGroup) archiveHintInnerCell.getParent()).removeView(archiveHintInnerCell);
            }
            viewGroup.addView(archiveHintInnerCell, 0);
            return archiveHintInnerCell;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) obj);
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public void setPrimaryItem(ViewGroup viewGroup, int i, Object obj) {
            super.setPrimaryItem(viewGroup, i, obj);
            ArchiveHintCell.this.bottomPages.setCurrentPage(i);
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public boolean isViewFromObject(View view, Object obj) {
            return view.equals(obj);
        }
    }
}
