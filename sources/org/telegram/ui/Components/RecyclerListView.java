package org.telegram.ui.Components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.SparseIntArray;
import android.util.StateSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.AdapterDataObserver;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnItemTouchListener;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.Theme;

public class RecyclerListView extends RecyclerView {
    private static int[] attributes;
    private static boolean gotAttributes;
    private Runnable clickRunnable;
    private int currentChildPosition;
    private View currentChildView;
    private int currentFirst = -1;
    private int currentVisible = -1;
    private boolean disallowInterceptTouchEvents;
    private View emptyView;
    private FastScroll fastScroll;
    private GestureDetector gestureDetector;
    private ArrayList<View> headers;
    private ArrayList<View> headersCache;
    private boolean hiddenByEmptyView;
    private boolean ignoreOnScroll;
    private boolean instantClick;
    private boolean interceptedByChild;
    private boolean isChildViewEnabled;
    private AdapterDataObserver observer = new C20781();
    private OnInterceptTouchListener onInterceptTouchListener;
    private OnItemClickListener onItemClickListener;
    private OnItemClickListenerExtended onItemClickListenerExtended;
    private OnItemLongClickListener onItemLongClickListener;
    private OnItemLongClickListenerExtended onItemLongClickListenerExtended;
    private OnScrollListener onScrollListener;
    private View pinnedHeader;
    private boolean scrollEnabled = true;
    private SectionsAdapter sectionsAdapter;
    private int sectionsCount;
    private int sectionsType;
    private Runnable selectChildRunnable;
    private Drawable selectorDrawable;
    private int selectorPosition;
    private Rect selectorRect = new Rect();
    private boolean selfOnLayout;
    private int startSection;
    private boolean wasPressed;

    private class FastScroll extends View {
        private float bubbleProgress;
        private int[] colors = new int[6];
        private String currentLetter;
        private long lastUpdateTime;
        private float lastY;
        private StaticLayout letterLayout;
        private TextPaint letterPaint = new TextPaint(1);
        private StaticLayout oldLetterLayout;
        private Paint paint = new Paint(1);
        private Path path = new Path();
        private boolean pressed;
        private float progress;
        private float[] radii = new float[8];
        private RectF rect = new RectF();
        private int scrollX;
        private float startDy;
        private float textX;
        private float textY;

        public FastScroll(Context context) {
            RecyclerListView recyclerListView;
            super(context);
            this.letterPaint.setTextSize((float) AndroidUtilities.dp(45.0f));
            for (context = null; context < 8; context++) {
                this.radii[context] = (float) AndroidUtilities.dp(44.0f);
            }
            if (LocaleController.isRTL != null) {
                recyclerListView = NUM;
            } else {
                int i = NUM;
            }
            this.scrollX = AndroidUtilities.dp(recyclerListView);
            updateColors();
        }

        private void updateColors() {
            int color = Theme.getColor(Theme.key_fastScrollInactive);
            int color2 = Theme.getColor(Theme.key_fastScrollActive);
            this.paint.setColor(color);
            this.letterPaint.setColor(Theme.getColor(Theme.key_fastScrollText));
            this.colors[0] = Color.red(color);
            this.colors[1] = Color.red(color2);
            this.colors[2] = Color.green(color);
            this.colors[3] = Color.green(color2);
            this.colors[4] = Color.blue(color);
            this.colors[5] = Color.blue(color2);
            invalidate();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            float x;
            switch (motionEvent.getAction()) {
                case 0:
                    x = motionEvent.getX();
                    this.lastY = motionEvent.getY();
                    motionEvent = ((float) Math.ceil((double) (((float) (getMeasuredHeight() - AndroidUtilities.dp(54.0f))) * this.progress))) + ((float) AndroidUtilities.dp(12.0f));
                    if ((!LocaleController.isRTL || x <= ((float) AndroidUtilities.dp(25.0f))) && ((LocaleController.isRTL || x >= ((float) AndroidUtilities.dp(107.0f))) && this.lastY >= motionEvent)) {
                        if (this.lastY <= ((float) AndroidUtilities.dp(30.0f)) + motionEvent) {
                            this.startDy = this.lastY - motionEvent;
                            this.pressed = true;
                            this.lastUpdateTime = System.currentTimeMillis();
                            getCurrentLetter();
                            invalidate();
                            return true;
                        }
                    }
                    return false;
                case 1:
                case 3:
                    this.pressed = false;
                    this.lastUpdateTime = System.currentTimeMillis();
                    invalidate();
                    return true;
                case 2:
                    if (!this.pressed) {
                        return true;
                    }
                    motionEvent = motionEvent.getY();
                    x = ((float) AndroidUtilities.dp(12.0f)) + this.startDy;
                    float measuredHeight = ((float) (getMeasuredHeight() - AndroidUtilities.dp(42.0f))) + this.startDy;
                    if (motionEvent < x) {
                        motionEvent = x;
                    } else if (motionEvent > measuredHeight) {
                        motionEvent = measuredHeight;
                    }
                    x = motionEvent - this.lastY;
                    this.lastY = motionEvent;
                    this.progress += x / ((float) (getMeasuredHeight() - AndroidUtilities.dp(54.0f)));
                    if (this.progress < null) {
                        this.progress = 0.0f;
                    } else if (this.progress > NUM) {
                        this.progress = 1.0f;
                    }
                    getCurrentLetter();
                    invalidate();
                    return true;
                default:
                    return super.onTouchEvent(motionEvent);
            }
        }

        private void getCurrentLetter() {
            LayoutManager layoutManager = RecyclerListView.this.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                if (linearLayoutManager.getOrientation() == 1) {
                    Adapter adapter = RecyclerListView.this.getAdapter();
                    if (adapter instanceof FastScrollAdapter) {
                        FastScrollAdapter fastScrollAdapter = (FastScrollAdapter) adapter;
                        int positionForScrollProgress = fastScrollAdapter.getPositionForScrollProgress(this.progress);
                        linearLayoutManager.scrollToPositionWithOffset(positionForScrollProgress, 0);
                        CharSequence letter = fastScrollAdapter.getLetter(positionForScrollProgress);
                        if (letter == null) {
                            if (this.letterLayout != null) {
                                this.oldLetterLayout = this.letterLayout;
                            }
                            this.letterLayout = null;
                        } else if (!letter.equals(this.currentLetter)) {
                            this.letterLayout = new StaticLayout(letter, this.letterPaint, 1000, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                            this.oldLetterLayout = null;
                            if (this.letterLayout.getLineCount() > 0) {
                                this.letterLayout.getLineWidth(0);
                                this.letterLayout.getLineLeft(0);
                                if (LocaleController.isRTL) {
                                    this.textX = (((float) AndroidUtilities.dp(10.0f)) + ((((float) AndroidUtilities.dp(88.0f)) - this.letterLayout.getLineWidth(0)) / 2.0f)) - this.letterLayout.getLineLeft(0);
                                } else {
                                    this.textX = ((((float) AndroidUtilities.dp(88.0f)) - this.letterLayout.getLineWidth(0)) / 2.0f) - this.letterLayout.getLineLeft(0);
                                }
                                this.textY = (float) ((AndroidUtilities.dp(88.0f) - this.letterLayout.getHeight()) / 2);
                            }
                        }
                    }
                }
            }
        }

        protected void onMeasure(int i, int i2) {
            setMeasuredDimension(AndroidUtilities.dp(NUM), MeasureSpec.getSize(i2));
        }

        protected void onDraw(Canvas canvas) {
            Canvas canvas2 = canvas;
            this.paint.setColor(Color.argb(255, this.colors[0] + ((int) (((float) (this.colors[1] - this.colors[0])) * this.bubbleProgress)), this.colors[2] + ((int) (((float) (this.colors[3] - this.colors[2])) * this.bubbleProgress)), this.colors[4] + ((int) (((float) (this.colors[5] - this.colors[4])) * this.bubbleProgress))));
            int ceil = (int) Math.ceil((double) (((float) (getMeasuredHeight() - AndroidUtilities.dp(54.0f))) * this.progress));
            this.rect.set((float) this.scrollX, (float) (AndroidUtilities.dp(12.0f) + ceil), (float) (this.scrollX + AndroidUtilities.dp(5.0f)), (float) (AndroidUtilities.dp(42.0f) + ceil));
            canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), this.paint);
            if (this.pressed || r0.bubbleProgress != 0.0f) {
                float dp;
                float dp2;
                float dp3;
                r0.paint.setAlpha((int) (255.0f * r0.bubbleProgress));
                int dp4 = AndroidUtilities.dp(30.0f) + ceil;
                ceil -= AndroidUtilities.dp(46.0f);
                if (ceil <= AndroidUtilities.dp(12.0f)) {
                    dp = (float) (AndroidUtilities.dp(12.0f) - ceil);
                    ceil = AndroidUtilities.dp(12.0f);
                } else {
                    dp = 0.0f;
                }
                canvas2.translate((float) AndroidUtilities.dp(10.0f), (float) ceil);
                if (dp <= ((float) AndroidUtilities.dp(29.0f))) {
                    dp2 = (float) AndroidUtilities.dp(44.0f);
                    dp3 = ((float) AndroidUtilities.dp(4.0f)) + ((dp / ((float) AndroidUtilities.dp(29.0f))) * ((float) AndroidUtilities.dp(40.0f)));
                } else {
                    dp3 = (float) AndroidUtilities.dp(44.0f);
                    dp2 = ((1.0f - ((dp - ((float) AndroidUtilities.dp(29.0f))) / ((float) AndroidUtilities.dp(29.0f)))) * ((float) AndroidUtilities.dp(40.0f))) + ((float) AndroidUtilities.dp(4.0f));
                }
                if ((LocaleController.isRTL && !(r0.radii[0] == dp2 && r0.radii[6] == dp3)) || !(LocaleController.isRTL || (r0.radii[2] == dp2 && r0.radii[4] == dp3))) {
                    float[] fArr;
                    if (LocaleController.isRTL) {
                        float[] fArr2 = r0.radii;
                        r0.radii[1] = dp2;
                        fArr2[0] = dp2;
                        fArr = r0.radii;
                        r0.radii[7] = dp3;
                        fArr[6] = dp3;
                    } else {
                        fArr = r0.radii;
                        r0.radii[3] = dp2;
                        fArr[2] = dp2;
                        fArr = r0.radii;
                        r0.radii[5] = dp3;
                        fArr[4] = dp3;
                    }
                    r0.path.reset();
                    r0.rect.set(LocaleController.isRTL ? (float) AndroidUtilities.dp(10.0f) : 0.0f, 0.0f, (float) AndroidUtilities.dp(LocaleController.isRTL ? 98.0f : 88.0f), (float) AndroidUtilities.dp(88.0f));
                    r0.path.addRoundRect(r0.rect, r0.radii, Direction.CW);
                    r0.path.close();
                }
                StaticLayout staticLayout = r0.letterLayout != null ? r0.letterLayout : r0.oldLetterLayout;
                if (staticLayout != null) {
                    canvas.save();
                    canvas2.scale(r0.bubbleProgress, r0.bubbleProgress, (float) r0.scrollX, (float) (dp4 - ceil));
                    canvas2.drawPath(r0.path, r0.paint);
                    canvas2.translate(r0.textX, r0.textY);
                    staticLayout.draw(canvas2);
                    canvas.restore();
                }
            }
            if ((r0.pressed && r0.letterLayout != null && r0.bubbleProgress < 1.0f) || ((!r0.pressed || r0.letterLayout == null) && r0.bubbleProgress > 0.0f)) {
                long currentTimeMillis = System.currentTimeMillis();
                long j = currentTimeMillis - r0.lastUpdateTime;
                if (j < 0 || j > 17) {
                    j = 17;
                }
                r0.lastUpdateTime = currentTimeMillis;
                invalidate();
                if (!r0.pressed || r0.letterLayout == null) {
                    r0.bubbleProgress -= ((float) j) / 120.0f;
                    if (r0.bubbleProgress < 0.0f) {
                        r0.bubbleProgress = 0.0f;
                        return;
                    }
                    return;
                }
                r0.bubbleProgress += ((float) j) / 120.0f;
                if (r0.bubbleProgress > 1.0f) {
                    r0.bubbleProgress = 1.0f;
                }
            }
        }

        public void layout(int i, int i2, int i3, int i4) {
            if (RecyclerListView.this.selfOnLayout) {
                super.layout(i, i2, i3, i4);
            }
        }

        private void setProgress(float f) {
            this.progress = f;
            invalidate();
        }
    }

    public interface OnInterceptTouchListener {
        boolean onInterceptTouchEvent(MotionEvent motionEvent);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int i);
    }

    public interface OnItemClickListenerExtended {
        void onItemClick(View view, int i, float f, float f2);
    }

    public interface OnItemLongClickListener {
        boolean onItemClick(View view, int i);
    }

    public interface OnItemLongClickListenerExtended {
        boolean onItemClick(View view, int i, float f, float f2);
    }

    /* renamed from: org.telegram.ui.Components.RecyclerListView$1 */
    class C20781 extends AdapterDataObserver {
        C20781() {
        }

        public void onChanged() {
            RecyclerListView.this.checkIfEmpty();
            RecyclerListView.this.selectorRect.setEmpty();
            RecyclerListView.this.invalidate();
        }

        public void onItemRangeInserted(int i, int i2) {
            RecyclerListView.this.checkIfEmpty();
        }

        public void onItemRangeRemoved(int i, int i2) {
            RecyclerListView.this.checkIfEmpty();
        }
    }

    /* renamed from: org.telegram.ui.Components.RecyclerListView$2 */
    class C20792 extends OnScrollListener {
        boolean scrollingByUser;

        C20792() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (!(i == 0 || RecyclerListView.this.currentChildView == null)) {
                if (RecyclerListView.this.selectChildRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                    RecyclerListView.this.selectChildRunnable = null;
                }
                MotionEvent obtain = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                try {
                    RecyclerListView.this.gestureDetector.onTouchEvent(obtain);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                RecyclerListView.this.currentChildView.onTouchEvent(obtain);
                obtain.recycle();
                View access$200 = RecyclerListView.this.currentChildView;
                RecyclerListView.this.onChildPressed(RecyclerListView.this.currentChildView, false);
                RecyclerListView.this.currentChildView = null;
                RecyclerListView.this.removeSelection(access$200, null);
                RecyclerListView.this.interceptedByChild = false;
            }
            if (RecyclerListView.this.onScrollListener != null) {
                RecyclerListView.this.onScrollListener.onScrollStateChanged(recyclerView, i);
            }
            recyclerView = true;
            if (i != 1) {
                if (i != 2) {
                    recyclerView = null;
                }
            }
            this.scrollingByUser = recyclerView;
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            if (RecyclerListView.this.onScrollListener != null) {
                RecyclerListView.this.onScrollListener.onScrolled(recyclerView, i, i2);
            }
            if (RecyclerListView.this.selectorPosition != -1) {
                RecyclerListView.this.selectorRect.offset(-i, -i2);
                RecyclerListView.this.selectorDrawable.setBounds(RecyclerListView.this.selectorRect);
                RecyclerListView.this.invalidate();
            } else {
                RecyclerListView.this.selectorRect.setEmpty();
            }
            if (!((this.scrollingByUser == null || RecyclerListView.this.fastScroll == null) && (RecyclerListView.this.sectionsType == null || RecyclerListView.this.sectionsAdapter == null))) {
                recyclerView = RecyclerListView.this.getLayoutManager();
                if ((recyclerView instanceof LinearLayoutManager) != 0) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView;
                    if (linearLayoutManager.getOrientation() == 1) {
                        i = linearLayoutManager.findFirstVisibleItemPosition();
                        if (i != -1) {
                            if (this.scrollingByUser && RecyclerListView.this.fastScroll != null) {
                                Adapter adapter = RecyclerListView.this.getAdapter();
                                if (adapter instanceof FastScrollAdapter) {
                                    RecyclerListView.this.fastScroll.setProgress(((float) i) / ((float) adapter.getItemCount()));
                                }
                            }
                            if (RecyclerListView.this.sectionsAdapter != null) {
                                if (RecyclerListView.this.sectionsType == 1) {
                                    recyclerView = Math.abs(linearLayoutManager.findLastVisibleItemPosition() - i) + 1;
                                    RecyclerListView.this.headersCache.addAll(RecyclerListView.this.headers);
                                    RecyclerListView.this.headers.clear();
                                    if (RecyclerListView.this.sectionsAdapter.getItemCount() != 0) {
                                        if (!(RecyclerListView.this.currentFirst == i && RecyclerListView.this.currentVisible == recyclerView)) {
                                            RecyclerListView.this.currentFirst = i;
                                            RecyclerListView.this.currentVisible = recyclerView;
                                            RecyclerListView.this.sectionsCount = 1;
                                            RecyclerListView.this.startSection = RecyclerListView.this.sectionsAdapter.getSectionForPosition(i);
                                            i2 = (RecyclerListView.this.sectionsAdapter.getCountForSection(RecyclerListView.this.startSection) + i) - RecyclerListView.this.sectionsAdapter.getPositionInSectionForPosition(i);
                                            while (i2 < i + recyclerView) {
                                                i2 += RecyclerListView.this.sectionsAdapter.getCountForSection(RecyclerListView.this.startSection + RecyclerListView.this.sectionsCount);
                                                RecyclerListView.this.sectionsCount = RecyclerListView.this.sectionsCount + 1;
                                            }
                                        }
                                        i2 = i;
                                        for (recyclerView = RecyclerListView.this.startSection; recyclerView < RecyclerListView.this.startSection + RecyclerListView.this.sectionsCount; recyclerView++) {
                                            View view = null;
                                            if (!RecyclerListView.this.headersCache.isEmpty()) {
                                                view = (View) RecyclerListView.this.headersCache.get(0);
                                                RecyclerListView.this.headersCache.remove(0);
                                            }
                                            view = RecyclerListView.this.getSectionHeaderView(recyclerView, view);
                                            RecyclerListView.this.headers.add(view);
                                            int countForSection = RecyclerListView.this.sectionsAdapter.getCountForSection(recyclerView);
                                            View childAt;
                                            if (recyclerView == RecyclerListView.this.startSection) {
                                                int positionInSectionForPosition = RecyclerListView.this.sectionsAdapter.getPositionInSectionForPosition(i2);
                                                if (positionInSectionForPosition == countForSection - 1) {
                                                    view.setTag(Integer.valueOf(-view.getHeight()));
                                                } else if (positionInSectionForPosition == countForSection - 2) {
                                                    childAt = RecyclerListView.this.getChildAt(i2 - i);
                                                    if (childAt != null) {
                                                        positionInSectionForPosition = childAt.getTop();
                                                    } else {
                                                        positionInSectionForPosition = -AndroidUtilities.dp(100.0f);
                                                    }
                                                    if (positionInSectionForPosition < 0) {
                                                        view.setTag(Integer.valueOf(positionInSectionForPosition));
                                                    } else {
                                                        view.setTag(Integer.valueOf(0));
                                                    }
                                                } else {
                                                    view.setTag(Integer.valueOf(0));
                                                }
                                                i2 += countForSection - RecyclerListView.this.sectionsAdapter.getPositionInSectionForPosition(i);
                                            } else {
                                                childAt = RecyclerListView.this.getChildAt(i2 - i);
                                                if (childAt != null) {
                                                    view.setTag(Integer.valueOf(childAt.getTop()));
                                                } else {
                                                    view.setTag(Integer.valueOf(-AndroidUtilities.dp(100.0f)));
                                                }
                                                i2 += countForSection;
                                            }
                                        }
                                    }
                                } else if (RecyclerListView.this.sectionsType == 2 && RecyclerListView.this.sectionsAdapter.getItemCount() != null) {
                                    recyclerView = RecyclerListView.this.sectionsAdapter.getSectionForPosition(i);
                                    if (RecyclerListView.this.currentFirst != recyclerView || RecyclerListView.this.pinnedHeader == null) {
                                        RecyclerListView.this.pinnedHeader = RecyclerListView.this.getSectionHeaderView(recyclerView, RecyclerListView.this.pinnedHeader);
                                        RecyclerListView.this.currentFirst = recyclerView;
                                    }
                                    if (RecyclerListView.this.sectionsAdapter.getPositionInSectionForPosition(i) == RecyclerListView.this.sectionsAdapter.getCountForSection(recyclerView) - 1) {
                                        recyclerView = RecyclerListView.this.getChildAt(0);
                                        i = RecyclerListView.this.pinnedHeader.getHeight();
                                        if (recyclerView != null) {
                                            i2 = recyclerView.getTop() + recyclerView.getHeight();
                                            recyclerView = i2 < i ? i2 - i : null;
                                        } else {
                                            recyclerView = -AndroidUtilities.dp(100.0f);
                                        }
                                        if (recyclerView < null) {
                                            RecyclerListView.this.pinnedHeader.setTag(Integer.valueOf(recyclerView));
                                        } else {
                                            RecyclerListView.this.pinnedHeader.setTag(Integer.valueOf(0));
                                        }
                                    } else {
                                        RecyclerListView.this.pinnedHeader.setTag(Integer.valueOf(0));
                                    }
                                    RecyclerListView.this.invalidate();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static class Holder extends ViewHolder {
        public Holder(View view) {
            super(view);
        }
    }

    private class RecyclerListViewItemClickListener implements OnItemTouchListener {

        /* renamed from: org.telegram.ui.Components.RecyclerListView$RecyclerListViewItemClickListener$2 */
        class C12852 implements Runnable {
            C12852() {
            }

            public void run() {
                if (RecyclerListView.this.selectChildRunnable != null && RecyclerListView.this.currentChildView != null) {
                    RecyclerListView.this.onChildPressed(RecyclerListView.this.currentChildView, true);
                    RecyclerListView.this.selectChildRunnable = null;
                }
            }
        }

        public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        }

        public RecyclerListViewItemClickListener(Context context) {
            RecyclerListView.this.gestureDetector = new GestureDetector(context, new SimpleOnGestureListener(RecyclerListView.this) {
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    if (!(RecyclerListView.this.currentChildView == null || (RecyclerListView.this.onItemClickListener == null && RecyclerListView.this.onItemClickListenerExtended == null))) {
                        RecyclerListView.this.onChildPressed(RecyclerListView.this.currentChildView, true);
                        final View access$200 = RecyclerListView.this.currentChildView;
                        final int access$500 = RecyclerListView.this.currentChildPosition;
                        final float x = motionEvent.getX();
                        final float y = motionEvent.getY();
                        if (RecyclerListView.this.instantClick && access$500 != -1) {
                            access$200.playSoundEffect(0);
                            if (RecyclerListView.this.onItemClickListener != null) {
                                RecyclerListView.this.onItemClickListener.onItemClick(access$200, access$500);
                            } else if (RecyclerListView.this.onItemClickListenerExtended != null) {
                                RecyclerListView.this.onItemClickListenerExtended.onItemClick(access$200, access$500, x, y);
                            }
                        }
                        AndroidUtilities.runOnUIThread(RecyclerListView.this.clickRunnable = new Runnable() {
                            public void run() {
                                if (this == RecyclerListView.this.clickRunnable) {
                                    RecyclerListView.this.clickRunnable = null;
                                }
                                if (access$200 != null) {
                                    RecyclerListView.this.onChildPressed(access$200, false);
                                    if (!RecyclerListView.this.instantClick) {
                                        access$200.playSoundEffect(0);
                                        if (access$500 == -1) {
                                            return;
                                        }
                                        if (RecyclerListView.this.onItemClickListener != null) {
                                            RecyclerListView.this.onItemClickListener.onItemClick(access$200, access$500);
                                        } else if (RecyclerListView.this.onItemClickListenerExtended != null) {
                                            RecyclerListView.this.onItemClickListenerExtended.onItemClick(access$200, access$500, x, y);
                                        }
                                    }
                                }
                            }
                        }, (long) ViewConfiguration.getPressedStateDuration());
                        if (RecyclerListView.this.selectChildRunnable != null) {
                            View access$2002 = RecyclerListView.this.currentChildView;
                            AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                            RecyclerListView.this.selectChildRunnable = null;
                            RecyclerListView.this.currentChildView = null;
                            RecyclerListView.this.interceptedByChild = false;
                            RecyclerListView.this.removeSelection(access$2002, motionEvent);
                        }
                    }
                    return true;
                }

                public void onLongPress(MotionEvent motionEvent) {
                    if (!(RecyclerListView.this.currentChildView == null || RecyclerListView.this.currentChildPosition == -1)) {
                        if (RecyclerListView.this.onItemLongClickListener != null || RecyclerListView.this.onItemLongClickListenerExtended != null) {
                            View access$200 = RecyclerListView.this.currentChildView;
                            if (RecyclerListView.this.onItemLongClickListener != null) {
                                if (RecyclerListView.this.onItemLongClickListener.onItemClick(RecyclerListView.this.currentChildView, RecyclerListView.this.currentChildPosition) != null) {
                                    access$200.performHapticFeedback(0);
                                }
                            } else if (!(RecyclerListView.this.onItemLongClickListenerExtended == null || RecyclerListView.this.onItemLongClickListenerExtended.onItemClick(RecyclerListView.this.currentChildView, RecyclerListView.this.currentChildPosition, motionEvent.getX(), motionEvent.getY()) == null)) {
                                access$200.performHapticFeedback(0);
                            }
                        }
                    }
                }
            });
        }

        public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            RecyclerView recyclerView2 = recyclerView;
            MotionEvent motionEvent2 = motionEvent;
            int actionMasked = motionEvent.getActionMasked();
            boolean z = RecyclerListView.this.getScrollState() == 0;
            if ((actionMasked == 0 || actionMasked == 5) && RecyclerListView.this.currentChildView == null && z) {
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                if (RecyclerListView.this.allowSelectChildAtPosition(x, y)) {
                    RecyclerListView.this.currentChildView = recyclerView2.findChildViewUnder(x, y);
                }
                if (RecyclerListView.this.currentChildView instanceof ViewGroup) {
                    x = motionEvent.getX() - ((float) RecyclerListView.this.currentChildView.getLeft());
                    y = motionEvent.getY() - ((float) RecyclerListView.this.currentChildView.getTop());
                    ViewGroup viewGroup = (ViewGroup) RecyclerListView.this.currentChildView;
                    for (int childCount = viewGroup.getChildCount() - 1; childCount >= 0; childCount--) {
                        View childAt = viewGroup.getChildAt(childCount);
                        if (x >= ((float) childAt.getLeft()) && x <= ((float) childAt.getRight()) && y >= ((float) childAt.getTop()) && y <= ((float) childAt.getBottom()) && childAt.isClickable()) {
                            RecyclerListView.this.currentChildView = null;
                            break;
                        }
                    }
                }
                RecyclerListView.this.currentChildPosition = -1;
                if (RecyclerListView.this.currentChildView != null) {
                    RecyclerListView.this.currentChildPosition = recyclerView2.getChildPosition(RecyclerListView.this.currentChildView);
                    MotionEvent obtain = MotionEvent.obtain(0, 0, motionEvent.getActionMasked(), motionEvent.getX() - ((float) RecyclerListView.this.currentChildView.getLeft()), motionEvent.getY() - ((float) RecyclerListView.this.currentChildView.getTop()), 0);
                    if (RecyclerListView.this.currentChildView.onTouchEvent(obtain)) {
                        RecyclerListView.this.interceptedByChild = true;
                    }
                    obtain.recycle();
                }
            }
            if (!(RecyclerListView.this.currentChildView == null || RecyclerListView.this.interceptedByChild || motionEvent2 == null)) {
                try {
                    RecyclerListView.this.gestureDetector.onTouchEvent(motionEvent2);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
            if (actionMasked != 0) {
                if (actionMasked != 5) {
                    if ((actionMasked == 1 || actionMasked == 6 || actionMasked == 3 || !z) && RecyclerListView.this.currentChildView != null) {
                        if (RecyclerListView.this.selectChildRunnable != null) {
                            AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                            RecyclerListView.this.selectChildRunnable = null;
                        }
                        View access$200 = RecyclerListView.this.currentChildView;
                        RecyclerListView.this.onChildPressed(RecyclerListView.this.currentChildView, false);
                        RecyclerListView.this.currentChildView = null;
                        RecyclerListView.this.interceptedByChild = false;
                        RecyclerListView.this.removeSelection(access$200, motionEvent2);
                    }
                    return false;
                }
            }
            if (!(RecyclerListView.this.interceptedByChild || RecyclerListView.this.currentChildView == null)) {
                RecyclerListView.this.selectChildRunnable = new C12852();
                AndroidUtilities.runOnUIThread(RecyclerListView.this.selectChildRunnable, (long) ViewConfiguration.getTapTimeout());
                if (RecyclerListView.this.currentChildView.isEnabled()) {
                    RecyclerListView.this.positionSelector(RecyclerListView.this.currentChildPosition, RecyclerListView.this.currentChildView);
                    if (RecyclerListView.this.selectorDrawable != null) {
                        Drawable current = RecyclerListView.this.selectorDrawable.getCurrent();
                        if (current != null && (current instanceof TransitionDrawable)) {
                            if (RecyclerListView.this.onItemLongClickListener == null) {
                                if (RecyclerListView.this.onItemClickListenerExtended == null) {
                                    ((TransitionDrawable) current).resetTransition();
                                }
                            }
                            ((TransitionDrawable) current).startTransition(ViewConfiguration.getLongPressTimeout());
                        }
                        if (VERSION.SDK_INT >= 21) {
                            RecyclerListView.this.selectorDrawable.setHotspot(motionEvent.getX(), motionEvent.getY());
                        }
                    }
                    RecyclerListView.this.updateSelectorState();
                } else {
                    RecyclerListView.this.selectorRect.setEmpty();
                }
            }
            return false;
        }

        public void onRequestDisallowInterceptTouchEvent(boolean z) {
            RecyclerListView.this.cancelClickRunnables(true);
        }
    }

    public static abstract class SelectionAdapter extends Adapter {
        public abstract boolean isEnabled(ViewHolder viewHolder);
    }

    public static abstract class FastScrollAdapter extends SelectionAdapter {
        public abstract String getLetter(int i);

        public abstract int getPositionForScrollProgress(float f);
    }

    public static abstract class SectionsAdapter extends FastScrollAdapter {
        private int count;
        private SparseIntArray sectionCache;
        private int sectionCount;
        private SparseIntArray sectionCountCache;
        private SparseIntArray sectionPositionCache;

        public abstract int getCountForSection(int i);

        public abstract Object getItem(int i, int i2);

        public abstract int getItemViewType(int i, int i2);

        public abstract int getSectionCount();

        public abstract View getSectionHeaderView(int i, View view);

        public abstract boolean isEnabled(int i, int i2);

        public abstract void onBindViewHolder(int i, int i2, ViewHolder viewHolder);

        private void cleanupCache() {
            this.sectionCache = new SparseIntArray();
            this.sectionPositionCache = new SparseIntArray();
            this.sectionCountCache = new SparseIntArray();
            this.count = -1;
            this.sectionCount = -1;
        }

        public SectionsAdapter() {
            cleanupCache();
        }

        public void notifyDataSetChanged() {
            cleanupCache();
            super.notifyDataSetChanged();
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            viewHolder = viewHolder.getAdapterPosition();
            return isEnabled(getSectionForPosition(viewHolder), getPositionInSectionForPosition(viewHolder));
        }

        public final int getItemCount() {
            if (this.count >= 0) {
                return this.count;
            }
            int i = 0;
            this.count = 0;
            while (i < internalGetSectionCount()) {
                this.count += internalGetCountForSection(i);
                i++;
            }
            return this.count;
        }

        public final Object getItem(int i) {
            return getItem(getSectionForPosition(i), getPositionInSectionForPosition(i));
        }

        public final int getItemViewType(int i) {
            return getItemViewType(getSectionForPosition(i), getPositionInSectionForPosition(i));
        }

        public final void onBindViewHolder(ViewHolder viewHolder, int i) {
            onBindViewHolder(getSectionForPosition(i), getPositionInSectionForPosition(i), viewHolder);
        }

        private int internalGetCountForSection(int i) {
            int i2 = this.sectionCountCache.get(i, ConnectionsManager.DEFAULT_DATACENTER_ID);
            if (i2 != ConnectionsManager.DEFAULT_DATACENTER_ID) {
                return i2;
            }
            i2 = getCountForSection(i);
            this.sectionCountCache.put(i, i2);
            return i2;
        }

        private int internalGetSectionCount() {
            if (this.sectionCount >= 0) {
                return this.sectionCount;
            }
            this.sectionCount = getSectionCount();
            return this.sectionCount;
        }

        public final int getSectionForPosition(int i) {
            int i2 = this.sectionCache.get(i, ConnectionsManager.DEFAULT_DATACENTER_ID);
            if (i2 != ConnectionsManager.DEFAULT_DATACENTER_ID) {
                return i2;
            }
            i2 = 0;
            int i3 = 0;
            while (i2 < internalGetSectionCount()) {
                int internalGetCountForSection = internalGetCountForSection(i2) + i3;
                if (i < i3 || i >= internalGetCountForSection) {
                    i2++;
                    i3 = internalGetCountForSection;
                } else {
                    this.sectionCache.put(i, i2);
                    return i2;
                }
            }
            return -1;
        }

        public int getPositionInSectionForPosition(int i) {
            int i2 = this.sectionPositionCache.get(i, ConnectionsManager.DEFAULT_DATACENTER_ID);
            if (i2 != ConnectionsManager.DEFAULT_DATACENTER_ID) {
                return i2;
            }
            i2 = 0;
            int i3 = 0;
            while (i2 < internalGetSectionCount()) {
                int internalGetCountForSection = internalGetCountForSection(i2) + i3;
                if (i < i3 || i >= internalGetCountForSection) {
                    i2++;
                    i3 = internalGetCountForSection;
                } else {
                    i2 = i - i3;
                    this.sectionPositionCache.put(i, i2);
                    return i2;
                }
            }
            return -1;
        }
    }

    protected boolean allowSelectChildAtPosition(float f, float f2) {
        return true;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    protected View getPressedChildView() {
        return this.currentChildView;
    }

    protected void onChildPressed(View view, boolean z) {
        view.setPressed(z);
    }

    private void removeSelection(View view, MotionEvent motionEvent) {
        if (view != null) {
            if (view == null || !view.isEnabled()) {
                this.selectorRect.setEmpty();
            } else {
                positionSelector(this.currentChildPosition, view);
                if (this.selectorDrawable != null) {
                    view = this.selectorDrawable.getCurrent();
                    if (view != null && (view instanceof TransitionDrawable)) {
                        ((TransitionDrawable) view).resetTransition();
                    }
                    if (motionEvent != null && VERSION.SDK_INT >= 21) {
                        this.selectorDrawable.setHotspot(motionEvent.getX(), motionEvent.getY());
                    }
                }
            }
            updateSelectorState();
        }
    }

    public void cancelClickRunnables(boolean z) {
        if (this.selectChildRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.selectChildRunnable);
            this.selectChildRunnable = null;
        }
        if (this.currentChildView != null) {
            View view = this.currentChildView;
            if (z) {
                onChildPressed(this.currentChildView, false);
            }
            this.currentChildView = null;
            removeSelection(view, null);
        }
        if (this.clickRunnable) {
            AndroidUtilities.cancelRunOnUIThread(this.clickRunnable);
            this.clickRunnable = null;
        }
        this.interceptedByChild = false;
    }

    public int[] getResourceDeclareStyleableIntArray(java.lang.String r3, java.lang.String r4) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r2 = this;
        r0 = 0;
        r1 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0023 }
        r1.<init>();	 Catch:{ Throwable -> 0x0023 }
        r1.append(r3);	 Catch:{ Throwable -> 0x0023 }
        r3 = ".R$styleable";	 Catch:{ Throwable -> 0x0023 }
        r1.append(r3);	 Catch:{ Throwable -> 0x0023 }
        r3 = r1.toString();	 Catch:{ Throwable -> 0x0023 }
        r3 = java.lang.Class.forName(r3);	 Catch:{ Throwable -> 0x0023 }
        r3 = r3.getField(r4);	 Catch:{ Throwable -> 0x0023 }
        if (r3 == 0) goto L_0x0023;	 Catch:{ Throwable -> 0x0023 }
    L_0x001c:
        r3 = r3.get(r0);	 Catch:{ Throwable -> 0x0023 }
        r3 = (int[]) r3;	 Catch:{ Throwable -> 0x0023 }
        return r3;
    L_0x0023:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.RecyclerListView.getResourceDeclareStyleableIntArray(java.lang.String, java.lang.String):int[]");
    }

    public RecyclerListView(Context context) {
        super(context);
        setGlowColor(Theme.getColor(Theme.key_actionBarDefault));
        this.selectorDrawable = Theme.getSelectorDrawable(false);
        this.selectorDrawable.setCallback(this);
        try {
            if (!gotAttributes) {
                attributes = getResourceDeclareStyleableIntArray("com.android.internal", "View");
                gotAttributes = true;
            }
            TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributes);
            View.class.getDeclaredMethod("initializeScrollbars", new Class[]{TypedArray.class}).invoke(this, new Object[]{obtainStyledAttributes});
            obtainStyledAttributes.recycle();
        } catch (Throwable th) {
            FileLog.m3e(th);
        }
        super.setOnScrollListener(new C20792());
        addOnItemTouchListener(new RecyclerListViewItemClickListener(context));
    }

    public void setVerticalScrollBarEnabled(boolean z) {
        if (attributes != null) {
            super.setVerticalScrollBarEnabled(z);
        }
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.fastScroll != 0) {
            this.fastScroll.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(NUM), NUM), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.fastScroll) {
            this.selfOnLayout = true;
            if (LocaleController.isRTL) {
                this.fastScroll.layout(0, i2, this.fastScroll.getMeasuredWidth(), this.fastScroll.getMeasuredHeight() + i2);
            } else {
                z = getMeasuredWidth() - this.fastScroll.getMeasuredWidth();
                this.fastScroll.layout(z, i2, this.fastScroll.getMeasuredWidth() + z, this.fastScroll.getMeasuredHeight() + i2);
            }
            this.selfOnLayout = false;
        }
    }

    public void setListSelectorColor(int i) {
        Theme.setSelectorDrawableColor(this.selectorDrawable, i, true);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListenerExtended onItemClickListenerExtended) {
        this.onItemClickListenerExtended = onItemClickListenerExtended;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListenerExtended onItemLongClickListenerExtended) {
        this.onItemLongClickListenerExtended = onItemLongClickListenerExtended;
    }

    public void setEmptyView(View view) {
        if (this.emptyView != view) {
            this.emptyView = view;
            checkIfEmpty();
        }
    }

    public View getEmptyView() {
        return this.emptyView;
    }

    public void invalidateViews() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).invalidate();
        }
    }

    public void updateFastScrollColors() {
        if (this.fastScroll != null) {
            this.fastScroll.updateColors();
        }
    }

    public boolean canScrollVertically(int i) {
        return this.scrollEnabled && super.canScrollVertically(i) != 0;
    }

    public void setScrollEnabled(boolean z) {
        this.scrollEnabled = z;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        boolean z = false;
        if (!isEnabled()) {
            return false;
        }
        if (this.disallowInterceptTouchEvents) {
            requestDisallowInterceptTouchEvent(true);
        }
        if ((this.onInterceptTouchListener != null && this.onInterceptTouchListener.onInterceptTouchEvent(motionEvent)) || super.onInterceptTouchEvent(motionEvent) != null) {
            z = true;
        }
        return z;
    }

    private void checkIfEmpty() {
        int i = 0;
        if (getAdapter() != null) {
            if (this.emptyView != null) {
                boolean z = getAdapter().getItemCount() == 0;
                this.emptyView.setVisibility(z ? 0 : 8);
                if (z) {
                    i = 4;
                }
                setVisibility(i);
                this.hiddenByEmptyView = true;
                return;
            }
        }
        if (this.hiddenByEmptyView && getVisibility() != 0) {
            setVisibility(0);
            this.hiddenByEmptyView = false;
        }
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i != 0) {
            this.hiddenByEmptyView = false;
        }
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public void setOnInterceptTouchListener(OnInterceptTouchListener onInterceptTouchListener) {
        this.onInterceptTouchListener = onInterceptTouchListener;
    }

    public void setInstantClick(boolean z) {
        this.instantClick = z;
    }

    public void setDisallowInterceptTouchEvents(boolean z) {
        this.disallowInterceptTouchEvents = z;
    }

    public void setFastScrollEnabled() {
        this.fastScroll = new FastScroll(getContext());
        if (getParent() != null) {
            ((ViewGroup) getParent()).addView(this.fastScroll);
        }
    }

    public void setFastScrollVisible(boolean z) {
        if (this.fastScroll != null) {
            this.fastScroll.setVisibility(z ? false : true);
        }
    }

    public void setSectionsType(int i) {
        this.sectionsType = i;
        if (this.sectionsType == 1) {
            this.headers = new ArrayList();
            this.headersCache = new ArrayList();
        }
    }

    private void positionSelector(int i, View view) {
        positionSelector(i, view, false, -1.0f, -1.0f);
    }

    private void positionSelector(int i, View view, boolean z, float f, float f2) {
        if (this.selectorDrawable != null) {
            boolean z2 = i != this.selectorPosition;
            if (i != -1) {
                this.selectorPosition = i;
            }
            this.selectorRect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            i = view.isEnabled();
            if (this.isChildViewEnabled != i) {
                this.isChildViewEnabled = i;
            }
            if (z2) {
                this.selectorDrawable.setVisible(false, false);
                this.selectorDrawable.setState(StateSet.NOTHING);
            }
            this.selectorDrawable.setBounds(this.selectorRect);
            if (z2 && getVisibility() == 0) {
                this.selectorDrawable.setVisible(true, false);
            }
            if (VERSION.SDK_INT >= 21 && z) {
                this.selectorDrawable.setHotspot(f, f2);
            }
        }
    }

    private void updateSelectorState() {
        if (this.selectorDrawable != null && this.selectorDrawable.isStateful()) {
            if (this.currentChildView == null) {
                this.selectorDrawable.setState(StateSet.NOTHING);
            } else if (this.selectorDrawable.setState(getDrawableStateForSelector())) {
                invalidateDrawable(this.selectorDrawable);
            }
        }
    }

    private int[] getDrawableStateForSelector() {
        int[] onCreateDrawableState = onCreateDrawableState(1);
        onCreateDrawableState[onCreateDrawableState.length - 1] = 16842919;
        return onCreateDrawableState;
    }

    public void onChildAttachedToWindow(View view) {
        if (getAdapter() instanceof SelectionAdapter) {
            ViewHolder findContainingViewHolder = findContainingViewHolder(view);
            if (findContainingViewHolder != null) {
                view.setEnabled(((SelectionAdapter) getAdapter()).isEnabled(findContainingViewHolder));
            }
        } else {
            view.setEnabled(false);
        }
        super.onChildAttachedToWindow(view);
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        updateSelectorState();
    }

    public boolean verifyDrawable(Drawable drawable) {
        if (this.selectorDrawable != drawable) {
            if (super.verifyDrawable(drawable) == null) {
                return null;
            }
        }
        return true;
    }

    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (this.selectorDrawable != null) {
            this.selectorDrawable.jumpToCurrentState();
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.fastScroll != null && this.fastScroll.getParent() != getParent()) {
            ViewGroup viewGroup = (ViewGroup) this.fastScroll.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(this.fastScroll);
            }
            ((ViewGroup) getParent()).addView(this.fastScroll);
        }
    }

    public void setAdapter(Adapter adapter) {
        Adapter adapter2 = getAdapter();
        if (adapter2 != null) {
            adapter2.unregisterAdapterDataObserver(this.observer);
        }
        if (this.headers != null) {
            this.headers.clear();
            this.headersCache.clear();
        }
        this.selectorPosition = -1;
        this.selectorRect.setEmpty();
        this.pinnedHeader = null;
        if (adapter instanceof SectionsAdapter) {
            this.sectionsAdapter = (SectionsAdapter) adapter;
        } else {
            this.sectionsAdapter = null;
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(this.observer);
        }
        checkIfEmpty();
    }

    public void stopScroll() {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r0 = this;
        super.stopScroll();	 Catch:{ NullPointerException -> 0x0003 }
    L_0x0003:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.RecyclerListView.stopScroll():void");
    }

    private View getSectionHeaderView(int i, View view) {
        boolean z = view == null;
        i = this.sectionsAdapter.getSectionHeaderView(i, view);
        if (z) {
            ensurePinnedHeaderLayout(i, false);
        }
        return i;
    }

    private void ensurePinnedHeaderLayout(View view, boolean z) {
        if (view.isLayoutRequested() || z) {
            if (this.sectionsType) {
                z = view.getLayoutParams();
                try {
                    view.measure(MeasureSpec.makeMeasureSpec(z.width, NUM), MeasureSpec.makeMeasureSpec(z.height, NUM));
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            } else if (this.sectionsType) {
                try {
                    view.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), NUM), MeasureSpec.makeMeasureSpec(0, 0));
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
                }
            }
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (this.sectionsType == 1) {
            if (this.sectionsAdapter != 0) {
                if (this.headers.isEmpty() == 0) {
                    for (i = 0; i < this.headers.size(); i++) {
                        ensurePinnedHeaderLayout((View) this.headers.get(i), true);
                    }
                }
            }
            return;
        }
        if (this.sectionsType == 2 && this.sectionsAdapter != 0) {
            if (this.pinnedHeader != 0) {
                ensurePinnedHeaderLayout(this.pinnedHeader, true);
            }
        }
    }

    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        float f = 0.0f;
        int i;
        if (this.sectionsType == 1) {
            if (this.sectionsAdapter != null) {
                if (!this.headers.isEmpty()) {
                    for (i = 0; i < this.headers.size(); i++) {
                        View view = (View) this.headers.get(i);
                        int save = canvas.save();
                        canvas.translate(LocaleController.isRTL ? (float) (getWidth() - view.getWidth()) : 0.0f, (float) ((Integer) view.getTag()).intValue());
                        canvas.clipRect(0, 0, getWidth(), view.getMeasuredHeight());
                        view.draw(canvas);
                        canvas.restoreToCount(save);
                    }
                }
            }
            return;
        } else if (this.sectionsType == 2) {
            if (this.sectionsAdapter != null) {
                if (this.pinnedHeader != null) {
                    i = canvas.save();
                    int intValue = ((Integer) this.pinnedHeader.getTag()).intValue();
                    if (LocaleController.isRTL) {
                        f = (float) (getWidth() - this.pinnedHeader.getWidth());
                    }
                    canvas.translate(f, (float) intValue);
                    canvas.clipRect(0, 0, getWidth(), this.pinnedHeader.getMeasuredHeight());
                    this.pinnedHeader.draw(canvas);
                    canvas.restoreToCount(i);
                }
            }
            return;
        }
        if (!this.selectorRect.isEmpty()) {
            this.selectorDrawable.setBounds(this.selectorRect);
            this.selectorDrawable.draw(canvas);
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.selectorPosition = -1;
        this.selectorRect.setEmpty();
    }

    public ArrayList<View> getHeaders() {
        return this.headers;
    }

    public ArrayList<View> getHeadersCache() {
        return this.headersCache;
    }

    public View getPinnedHeader() {
        return this.pinnedHeader;
    }
}
