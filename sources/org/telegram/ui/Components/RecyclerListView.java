package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.SparseIntArray;
import android.util.StateSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;

public class RecyclerListView extends RecyclerView {
    private static int[] attributes;
    private static boolean gotAttributes;
    /* access modifiers changed from: private */
    public boolean allowItemsInteractionDuringAnimation;
    private boolean animateEmptyView;
    /* access modifiers changed from: private */
    public Runnable clickRunnable;
    /* access modifiers changed from: private */
    public int currentChildPosition;
    /* access modifiers changed from: private */
    public View currentChildView;
    /* access modifiers changed from: private */
    public int currentFirst;
    int currentSelectedPosition;
    private int currentVisible;
    private boolean disableHighlightState;
    private boolean disallowInterceptTouchEvents;
    private boolean drawSelectorBehind;
    /* access modifiers changed from: private */
    public View emptyView;
    int emptyViewAnimateToVisibility;
    private int emptyViewAnimationType;
    /* access modifiers changed from: private */
    public FastScroll fastScroll;
    public boolean fastScrollAnimationRunning;
    /* access modifiers changed from: private */
    public GestureDetector gestureDetector;
    private ArrayList<View> headers;
    private ArrayList<View> headersCache;
    private boolean hiddenByEmptyView;
    private boolean hideIfEmpty;
    private boolean ignoreOnScroll;
    /* access modifiers changed from: private */
    public boolean instantClick;
    /* access modifiers changed from: private */
    public boolean interceptedByChild;
    private boolean isChildViewEnabled;
    private boolean isHidden;
    RecyclerItemsEnterAnimator itemsEnterAnimator;
    private long lastAlphaAnimationTime;
    float lastX;
    float lastY;
    int[] listPaddings;
    /* access modifiers changed from: private */
    public boolean longPressCalled;
    boolean multiSelectionGesture;
    boolean multiSelectionGestureStarted;
    onMultiSelectionChanged multiSelectionListener;
    boolean multiselectScrollRunning;
    boolean multiselectScrollToTop;
    private RecyclerView.AdapterDataObserver observer;
    private OnInterceptTouchListener onInterceptTouchListener;
    /* access modifiers changed from: private */
    public OnItemClickListener onItemClickListener;
    /* access modifiers changed from: private */
    public OnItemClickListenerExtended onItemClickListenerExtended;
    /* access modifiers changed from: private */
    public OnItemLongClickListener onItemLongClickListener;
    /* access modifiers changed from: private */
    public OnItemLongClickListenerExtended onItemLongClickListenerExtended;
    /* access modifiers changed from: private */
    public RecyclerView.OnScrollListener onScrollListener;
    /* access modifiers changed from: private */
    public FrameLayout overlayContainer;
    private IntReturnCallback pendingHighlightPosition;
    /* access modifiers changed from: private */
    public View pinnedHeader;
    private float pinnedHeaderShadowAlpha;
    private Drawable pinnedHeaderShadowDrawable;
    private float pinnedHeaderShadowTargetAlpha;
    /* access modifiers changed from: private */
    public Runnable removeHighlighSelectionRunnable;
    /* access modifiers changed from: protected */
    public final Theme.ResourcesProvider resourcesProvider;
    private boolean scrollEnabled;
    Runnable scroller;
    public boolean scrollingByUser;
    /* access modifiers changed from: private */
    public int sectionOffset;
    private SectionsAdapter sectionsAdapter;
    private int sectionsCount;
    private int sectionsType;
    /* access modifiers changed from: private */
    public Runnable selectChildRunnable;
    HashSet<Integer> selectedPositions;
    protected Drawable selectorDrawable;
    protected int selectorPosition;
    private int selectorRadius;
    protected Rect selectorRect;
    private int selectorType;
    /* access modifiers changed from: private */
    public boolean selfOnLayout;
    private int startSection;
    int startSelectionFrom;
    private int topBottomSelectorRadius;
    private int touchSlop;
    boolean useRelativePositions;
    private boolean wasPressed;

    public interface IntReturnCallback {
        int run();
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

        void onLongClickRelease();

        void onMove(float f, float f2);
    }

    public interface onMultiSelectionChanged {
        boolean canSelect(int i);

        int checkPosition(int i, boolean z);

        void getPaddings(int[] iArr);

        boolean limitReached();

        void onSelectionChanged(int i, boolean z, float f, float f2);

        void scrollBy(int i);
    }

    public FastScroll getFastScroll() {
        return this.fastScroll;
    }

    public static abstract class SelectionAdapter extends RecyclerView.Adapter {
        public abstract boolean isEnabled(RecyclerView.ViewHolder viewHolder);

        public int getSelectionBottomPadding(View view) {
            return 0;
        }
    }

    public static abstract class FastScrollAdapter extends SelectionAdapter {
        public abstract String getLetter(int i);

        public abstract void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr);

        public void onStartFastScroll() {
        }

        public void onFinishFastScroll(RecyclerListView listView) {
        }

        public int getTotalItemsCount() {
            return getItemCount();
        }

        public float getScrollProgress(RecyclerListView listView) {
            return ((float) listView.computeVerticalScrollOffset()) / ((((float) getTotalItemsCount()) * ((float) listView.getChildAt(0).getMeasuredHeight())) - ((float) listView.getMeasuredHeight()));
        }

        public boolean fastScrollIsVisible(RecyclerListView listView) {
            return true;
        }

        public void onFastScrollSingleTap() {
        }
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

        public abstract boolean isEnabled(RecyclerView.ViewHolder viewHolder, int i, int i2);

        public abstract void onBindViewHolder(int i, int i2, RecyclerView.ViewHolder viewHolder);

        private void cleanupCache() {
            SparseIntArray sparseIntArray = this.sectionCache;
            if (sparseIntArray == null) {
                this.sectionCache = new SparseIntArray();
                this.sectionPositionCache = new SparseIntArray();
                this.sectionCountCache = new SparseIntArray();
            } else {
                sparseIntArray.clear();
                this.sectionPositionCache.clear();
                this.sectionCountCache.clear();
            }
            this.count = -1;
            this.sectionCount = -1;
        }

        public void notifySectionsChanged() {
            cleanupCache();
        }

        public SectionsAdapter() {
            cleanupCache();
        }

        public void notifyDataSetChanged() {
            cleanupCache();
            super.notifyDataSetChanged();
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return isEnabled(holder, getSectionForPosition(position), getPositionInSectionForPosition(position));
        }

        public int getItemCount() {
            int i = this.count;
            if (i >= 0) {
                return i;
            }
            this.count = 0;
            int N = internalGetSectionCount();
            for (int i2 = 0; i2 < N; i2++) {
                this.count += internalGetCountForSection(i2);
            }
            return this.count;
        }

        public final Object getItem(int position) {
            return getItem(getSectionForPosition(position), getPositionInSectionForPosition(position));
        }

        public final int getItemViewType(int position) {
            return getItemViewType(getSectionForPosition(position), getPositionInSectionForPosition(position));
        }

        public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            onBindViewHolder(getSectionForPosition(position), getPositionInSectionForPosition(position), holder);
        }

        private int internalGetCountForSection(int section) {
            int cachedSectionCount = this.sectionCountCache.get(section, Integer.MAX_VALUE);
            if (cachedSectionCount != Integer.MAX_VALUE) {
                return cachedSectionCount;
            }
            int sectionCount2 = getCountForSection(section);
            this.sectionCountCache.put(section, sectionCount2);
            return sectionCount2;
        }

        private int internalGetSectionCount() {
            int i = this.sectionCount;
            if (i >= 0) {
                return i;
            }
            int sectionCount2 = getSectionCount();
            this.sectionCount = sectionCount2;
            return sectionCount2;
        }

        public final int getSectionForPosition(int position) {
            int cachedSection = this.sectionCache.get(position, Integer.MAX_VALUE);
            if (cachedSection != Integer.MAX_VALUE) {
                return cachedSection;
            }
            int sectionStart = 0;
            int i = 0;
            int N = internalGetSectionCount();
            while (i < N) {
                int sectionEnd = sectionStart + internalGetCountForSection(i);
                if (position < sectionStart || position >= sectionEnd) {
                    sectionStart = sectionEnd;
                    i++;
                } else {
                    this.sectionCache.put(position, i);
                    return i;
                }
            }
            return -1;
        }

        public int getPositionInSectionForPosition(int position) {
            int cachedPosition = this.sectionPositionCache.get(position, Integer.MAX_VALUE);
            if (cachedPosition != Integer.MAX_VALUE) {
                return cachedPosition;
            }
            int sectionStart = 0;
            int i = 0;
            int N = internalGetSectionCount();
            while (i < N) {
                int sectionEnd = sectionStart + internalGetCountForSection(i);
                if (position < sectionStart || position >= sectionEnd) {
                    sectionStart = sectionEnd;
                    i++;
                } else {
                    int positionInSection = position - sectionStart;
                    this.sectionPositionCache.put(position, positionInSection);
                    return positionInSection;
                }
            }
            return -1;
        }
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public Holder(View itemView) {
            super(itemView);
        }
    }

    public class FastScroll extends View {
        public static final int DATE_TYPE = 1;
        public static final int LETTER_TYPE = 0;
        private int activeColor;
        private Path arrowPath = new Path();
        private float bubbleProgress;
        private String currentLetter;
        Drawable fastScrollBackgroundDrawable;
        Drawable fastScrollShadowDrawable;
        private float floatingDateProgress;
        /* access modifiers changed from: private */
        public boolean floatingDateVisible;
        Runnable hideFloatingDateRunnable = new Runnable() {
            public void run() {
                if (FastScroll.this.pressed) {
                    AndroidUtilities.cancelRunOnUIThread(FastScroll.this.hideFloatingDateRunnable);
                    AndroidUtilities.runOnUIThread(FastScroll.this.hideFloatingDateRunnable, 4000);
                    return;
                }
                boolean unused = FastScroll.this.floatingDateVisible = false;
                FastScroll.this.invalidate();
            }
        };
        private int inactiveColor;
        boolean isMoving;
        boolean isRtl;
        boolean isVisible;
        private long lastUpdateTime;
        private float lastY;
        private StaticLayout letterLayout;
        private TextPaint letterPaint = new TextPaint(1);
        private StaticLayout oldLetterLayout;
        private Paint paint = new Paint(1);
        private Paint paint2 = new Paint(1);
        private Path path = new Path();
        private int[] positionWithOffset = new int[2];
        /* access modifiers changed from: private */
        public boolean pressed;
        private float progress;
        private float[] radii = new float[8];
        private RectF rect = new RectF();
        private int scrollX;
        private float startDy;
        long startTime;
        float startY;
        private float textX;
        private float textY;
        float touchSlop;
        private int type;
        float viewAlpha;
        float visibilityAlpha;

        public FastScroll(Context context, int type2) {
            super(context);
            float f;
            this.type = type2;
            if (type2 == 0) {
                this.letterPaint.setTextSize((float) AndroidUtilities.dp(45.0f));
                this.isRtl = LocaleController.isRTL;
            } else {
                this.isRtl = false;
                this.letterPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
                this.letterPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.paint2.setColor(Theme.getColor("windowBackgroundWhite"));
                Drawable mutate = ContextCompat.getDrawable(context, NUM).mutate();
                this.fastScrollBackgroundDrawable = mutate;
                mutate.setColorFilter(new PorterDuffColorFilter(ColorUtils.blendARGB(Theme.getColor("windowBackgroundWhite"), -1, 0.1f), PorterDuff.Mode.MULTIPLY));
            }
            for (int a = 0; a < 8; a++) {
                this.radii[a] = (float) AndroidUtilities.dp(44.0f);
            }
            if (this.isRtl) {
                f = 10.0f;
            } else {
                f = (float) ((type2 == 0 ? 132 : 240) - 15);
            }
            this.scrollX = AndroidUtilities.dp(f);
            updateColors();
            setFocusableInTouchMode(true);
            this.touchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
            this.fastScrollShadowDrawable = ContextCompat.getDrawable(context, NUM);
        }

        /* access modifiers changed from: private */
        public void updateColors() {
            this.inactiveColor = this.type == 0 ? Theme.getColor("fastScrollInactive") : ColorUtils.setAlphaComponent(-16777216, 102);
            this.activeColor = Theme.getColor("fastScrollActive");
            this.paint.setColor(this.inactiveColor);
            if (this.type == 0) {
                this.letterPaint.setColor(Theme.getColor("fastScrollText"));
            } else {
                this.letterPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            }
            invalidate();
        }

        /* JADX WARNING: Code restructure failed: missing block: B:71:0x0155, code lost:
            if (r2 <= (((float) org.telegram.messenger.AndroidUtilities.dp(30.0f)) + r3)) goto L_0x0158;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTouchEvent(android.view.MotionEvent r9) {
            /*
                r8 = this;
                boolean r0 = r8.isVisible
                r1 = 0
                if (r0 != 0) goto L_0x0008
                r8.pressed = r1
                return r1
            L_0x0008:
                int r0 = r9.getAction()
                r2 = 1094713344(0x41400000, float:12.0)
                r3 = 1113063424(0x42580000, float:54.0)
                r4 = 1
                switch(r0) {
                    case 0: goto L_0x00c9;
                    case 1: goto L_0x0087;
                    case 2: goto L_0x0017;
                    case 3: goto L_0x0087;
                    default: goto L_0x0014;
                }
            L_0x0014:
                boolean r0 = r8.pressed
                return r0
            L_0x0017:
                boolean r0 = r8.pressed
                if (r0 != 0) goto L_0x001c
                return r4
            L_0x001c:
                float r0 = r9.getY()
                float r1 = r8.startY
                float r0 = r0 - r1
                float r0 = java.lang.Math.abs(r0)
                float r1 = r8.touchSlop
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 <= 0) goto L_0x002f
                r8.isMoving = r4
            L_0x002f:
                boolean r0 = r8.isMoving
                if (r0 == 0) goto L_0x0086
                float r0 = r9.getY()
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r1 = (float) r1
                float r2 = r8.startDy
                float r1 = r1 + r2
                int r2 = r8.getMeasuredHeight()
                r5 = 1109917696(0x42280000, float:42.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r2 = r2 - r5
                float r2 = (float) r2
                float r5 = r8.startDy
                float r2 = r2 + r5
                int r5 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r5 >= 0) goto L_0x0054
                r0 = r1
                goto L_0x0059
            L_0x0054:
                int r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r5 <= 0) goto L_0x0059
                r0 = r2
            L_0x0059:
                float r5 = r8.lastY
                float r5 = r0 - r5
                r8.lastY = r0
                float r6 = r8.progress
                int r7 = r8.getMeasuredHeight()
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r7 = r7 - r3
                float r3 = (float) r7
                float r3 = r5 / r3
                float r6 = r6 + r3
                r8.progress = r6
                r3 = 0
                int r7 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1))
                if (r7 >= 0) goto L_0x0078
                r8.progress = r3
                goto L_0x0080
            L_0x0078:
                r3 = 1065353216(0x3var_, float:1.0)
                int r6 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1))
                if (r6 <= 0) goto L_0x0080
                r8.progress = r3
            L_0x0080:
                r8.getCurrentLetter(r4)
                r8.invalidate()
            L_0x0086:
                return r4
            L_0x0087:
                org.telegram.ui.Components.RecyclerListView r0 = org.telegram.ui.Components.RecyclerListView.this
                androidx.recyclerview.widget.RecyclerView$Adapter r0 = r0.getAdapter()
                boolean r2 = r8.pressed
                if (r2 == 0) goto L_0x00ac
                boolean r2 = r8.isMoving
                if (r2 != 0) goto L_0x00ac
                long r2 = java.lang.System.currentTimeMillis()
                long r5 = r8.startTime
                long r2 = r2 - r5
                r5 = 150(0x96, double:7.4E-322)
                int r7 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
                if (r7 >= 0) goto L_0x00ac
                boolean r2 = r0 instanceof org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
                if (r2 == 0) goto L_0x00ac
                r2 = r0
                org.telegram.ui.Components.RecyclerListView$FastScrollAdapter r2 = (org.telegram.ui.Components.RecyclerListView.FastScrollAdapter) r2
                r2.onFastScrollSingleTap()
            L_0x00ac:
                r8.isMoving = r1
                r8.pressed = r1
                long r1 = java.lang.System.currentTimeMillis()
                r8.lastUpdateTime = r1
                r8.invalidate()
                boolean r1 = r0 instanceof org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
                if (r1 == 0) goto L_0x00c5
                r1 = r0
                org.telegram.ui.Components.RecyclerListView$FastScrollAdapter r1 = (org.telegram.ui.Components.RecyclerListView.FastScrollAdapter) r1
                org.telegram.ui.Components.RecyclerListView r2 = org.telegram.ui.Components.RecyclerListView.this
                r1.onFinishFastScroll(r2)
            L_0x00c5:
                r8.showFloatingDate()
                return r4
            L_0x00c9:
                float r0 = r9.getX()
                float r5 = r9.getY()
                r8.lastY = r5
                r8.startY = r5
                int r5 = r8.getMeasuredHeight()
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r5 = r5 - r3
                float r3 = (float) r5
                float r5 = r8.progress
                float r3 = r3 * r5
                double r5 = (double) r3
                double r5 = java.lang.Math.ceil(r5)
                float r3 = (float) r5
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r2 = (float) r2
                float r3 = r3 + r2
                boolean r2 = r8.isRtl
                r5 = 1103626240(0x41CLASSNAME, float:25.0)
                if (r2 == 0) goto L_0x00fe
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
                float r2 = (float) r2
                int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r2 > 0) goto L_0x0184
            L_0x00fe:
                boolean r2 = r8.isRtl
                if (r2 != 0) goto L_0x010d
                r2 = 1121320960(0x42d60000, float:107.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r2 = (float) r2
                int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r2 < 0) goto L_0x0184
            L_0x010d:
                float r2 = r8.lastY
                int r6 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r6 < 0) goto L_0x0184
                r6 = 1106247680(0x41var_, float:30.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r7 = (float) r7
                float r7 = r7 + r3
                int r2 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
                if (r2 <= 0) goto L_0x0120
                goto L_0x0184
            L_0x0120:
                int r2 = r8.type
                if (r2 != r4) goto L_0x0158
                boolean r2 = r8.floatingDateVisible
                if (r2 != 0) goto L_0x0158
                boolean r2 = r8.isRtl
                if (r2 == 0) goto L_0x0135
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
                float r2 = (float) r2
                int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r2 > 0) goto L_0x0157
            L_0x0135:
                boolean r2 = r8.isRtl
                if (r2 != 0) goto L_0x0147
                int r2 = r8.getMeasuredWidth()
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r2 = r2 - r5
                float r2 = (float) r2
                int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r2 < 0) goto L_0x0157
            L_0x0147:
                float r2 = r8.lastY
                int r5 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r5 < 0) goto L_0x0157
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r5 = (float) r5
                float r5 = r5 + r3
                int r2 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
                if (r2 <= 0) goto L_0x0158
            L_0x0157:
                return r1
            L_0x0158:
                float r2 = r8.lastY
                float r2 = r2 - r3
                r8.startDy = r2
                long r5 = java.lang.System.currentTimeMillis()
                r8.startTime = r5
                r8.pressed = r4
                r8.isMoving = r1
                long r1 = java.lang.System.currentTimeMillis()
                r8.lastUpdateTime = r1
                r8.invalidate()
                org.telegram.ui.Components.RecyclerListView r1 = org.telegram.ui.Components.RecyclerListView.this
                androidx.recyclerview.widget.RecyclerView$Adapter r1 = r1.getAdapter()
                r8.showFloatingDate()
                boolean r2 = r1 instanceof org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
                if (r2 == 0) goto L_0x0183
                r2 = r1
                org.telegram.ui.Components.RecyclerListView$FastScrollAdapter r2 = (org.telegram.ui.Components.RecyclerListView.FastScrollAdapter) r2
                r2.onStartFastScroll()
            L_0x0183:
                return r4
            L_0x0184:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.RecyclerListView.FastScroll.onTouchEvent(android.view.MotionEvent):boolean");
        }

        /* access modifiers changed from: private */
        public void getCurrentLetter(boolean updatePosition) {
            RecyclerView.LayoutManager layoutManager = RecyclerListView.this.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                if (linearLayoutManager.getOrientation() == 1) {
                    RecyclerView.Adapter adapter = RecyclerListView.this.getAdapter();
                    if (adapter instanceof FastScrollAdapter) {
                        FastScrollAdapter fastScrollAdapter = (FastScrollAdapter) adapter;
                        fastScrollAdapter.getPositionForScrollProgress(RecyclerListView.this, this.progress, this.positionWithOffset);
                        if (updatePosition) {
                            int[] iArr = this.positionWithOffset;
                            linearLayoutManager.scrollToPositionWithOffset(iArr[0], (-iArr[1]) + RecyclerListView.this.sectionOffset);
                        }
                        String newLetter = fastScrollAdapter.getLetter(this.positionWithOffset[0]);
                        if (newLetter == null) {
                            StaticLayout staticLayout = this.letterLayout;
                            if (staticLayout != null) {
                                this.oldLetterLayout = staticLayout;
                            }
                            this.letterLayout = null;
                        } else if (!newLetter.equals(this.currentLetter)) {
                            if (this.type == 0) {
                                this.letterLayout = new StaticLayout(newLetter, this.letterPaint, 1000, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                            } else {
                                StaticLayout staticLayout2 = r8;
                                StaticLayout staticLayout3 = new StaticLayout(newLetter, this.letterPaint, 1 + ((int) this.letterPaint.measureText(newLetter)), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                                this.letterLayout = staticLayout2;
                            }
                            this.oldLetterLayout = null;
                            if (this.letterLayout.getLineCount() > 0) {
                                float lineWidth = this.letterLayout.getLineWidth(0);
                                float lineLeft = this.letterLayout.getLineLeft(0);
                                if (this.isRtl) {
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

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(AndroidUtilities.dp(this.type == 0 ? 132.0f : 240.0f), View.MeasureSpec.getSize(heightMeasureSpec));
            this.arrowPath.reset();
            this.arrowPath.setLastPoint(0.0f, 0.0f);
            this.arrowPath.lineTo((float) AndroidUtilities.dp(4.0f), (float) (-AndroidUtilities.dp(4.0f)));
            this.arrowPath.lineTo((float) (-AndroidUtilities.dp(4.0f)), (float) (-AndroidUtilities.dp(4.0f)));
            this.arrowPath.close();
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:22:0x01e0, code lost:
            if (r13[6] == r6) goto L_0x01e2;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:0x01f0, code lost:
            if (r13[4] == r6) goto L_0x0246;
         */
        /* JADX WARNING: Removed duplicated region for block: B:29:0x01f4  */
        /* JADX WARNING: Removed duplicated region for block: B:30:0x0200  */
        /* JADX WARNING: Removed duplicated region for block: B:33:0x0217  */
        /* JADX WARNING: Removed duplicated region for block: B:34:0x021d  */
        /* JADX WARNING: Removed duplicated region for block: B:37:0x0224  */
        /* JADX WARNING: Removed duplicated region for block: B:38:0x0227  */
        /* JADX WARNING: Removed duplicated region for block: B:42:0x024b  */
        /* JADX WARNING: Removed duplicated region for block: B:44:0x024f  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onDraw(android.graphics.Canvas r19) {
            /*
                r18 = this;
                r0 = r18
                r1 = r19
                int r2 = r18.getMeasuredHeight()
                r3 = 1113063424(0x42580000, float:54.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r2 = r2 - r3
                float r2 = (float) r2
                float r3 = r0.progress
                float r2 = r2 * r3
                double r2 = (double) r2
                double r2 = java.lang.Math.ceil(r2)
                int r2 = (int) r2
                android.graphics.RectF r3 = r0.rect
                int r4 = r0.scrollX
                float r4 = (float) r4
                r5 = 1094713344(0x41400000, float:12.0)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r6 = r6 + r2
                float r6 = (float) r6
                int r7 = r0.scrollX
                r8 = 1084227584(0x40a00000, float:5.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                int r7 = r7 + r8
                float r7 = (float) r7
                r8 = 1109917696(0x42280000, float:42.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                int r8 = r8 + r2
                float r8 = (float) r8
                r3.set(r4, r6, r7, r8)
                int r3 = r0.type
                r4 = 1103101952(0x41CLASSNAME, float:24.0)
                r6 = 1082130432(0x40800000, float:4.0)
                r7 = 1090519040(0x41000000, float:8.0)
                r8 = 2
                r9 = 1073741824(0x40000000, float:2.0)
                r10 = 0
                if (r3 != 0) goto L_0x006c
                android.graphics.Paint r3 = r0.paint
                int r11 = r0.inactiveColor
                int r12 = r0.activeColor
                float r13 = r0.bubbleProgress
                int r11 = androidx.core.graphics.ColorUtils.blendARGB(r11, r12, r13)
                r3.setColor(r11)
                android.graphics.RectF r3 = r0.rect
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r9)
                float r11 = (float) r11
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r9)
                float r12 = (float) r12
                android.graphics.Paint r13 = r0.paint
                r1.drawRoundRect(r3, r11, r12, r13)
                goto L_0x0135
            L_0x006c:
                android.graphics.Paint r3 = r0.paint
                java.lang.String r11 = "windowBackgroundWhite"
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
                r12 = -1
                r13 = 1036831949(0x3dcccccd, float:0.1)
                int r11 = androidx.core.graphics.ColorUtils.blendARGB(r11, r12, r13)
                r3.setColor(r11)
                r3 = 1104674816(0x41d80000, float:27.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r11 = r11 + r2
                float r11 = (float) r11
                android.graphics.drawable.Drawable r12 = r0.fastScrollShadowDrawable
                int r13 = r18.getMeasuredWidth()
                android.graphics.drawable.Drawable r14 = r0.fastScrollShadowDrawable
                int r14 = r14.getIntrinsicWidth()
                int r13 = r13 - r14
                android.graphics.drawable.Drawable r14 = r0.fastScrollShadowDrawable
                int r14 = r14.getIntrinsicHeight()
                int r14 = r14 / r8
                float r14 = (float) r14
                float r14 = r11 - r14
                int r14 = (int) r14
                int r15 = r18.getMeasuredWidth()
                android.graphics.drawable.Drawable r5 = r0.fastScrollShadowDrawable
                int r5 = r5.getIntrinsicHeight()
                int r5 = r5 / r8
                float r5 = (float) r5
                float r5 = r5 + r11
                int r5 = (int) r5
                r12.setBounds(r13, r14, r15, r5)
                android.graphics.drawable.Drawable r5 = r0.fastScrollShadowDrawable
                r5.draw(r1)
                int r5 = r0.scrollX
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r5 = r5 + r12
                float r5 = (float) r5
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r3 = r3 + r2
                float r3 = (float) r3
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r4)
                float r12 = (float) r12
                android.graphics.Paint r13 = r0.paint
                r1.drawCircle(r5, r3, r12, r13)
                android.graphics.Paint r3 = r0.paint
                java.lang.String r5 = "windowBackgroundWhiteBlackText"
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r3.setColor(r5)
                r19.save()
                int r3 = r0.scrollX
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r6)
                int r3 = r3 + r5
                float r3 = (float) r3
                r5 = 1107820544(0x42080000, float:34.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r5 = r5 + r2
                float r5 = (float) r5
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r9)
                float r12 = (float) r12
                float r13 = r0.bubbleProgress
                float r12 = r12 * r13
                float r5 = r5 + r12
                r1.translate(r3, r5)
                android.graphics.Path r3 = r0.arrowPath
                android.graphics.Paint r5 = r0.paint
                r1.drawPath(r3, r5)
                r19.restore()
                r19.save()
                int r3 = r0.scrollX
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r6)
                int r3 = r3 + r5
                float r3 = (float) r3
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
                int r5 = r5 + r2
                float r5 = (float) r5
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r9)
                float r12 = (float) r12
                float r13 = r0.bubbleProgress
                float r12 = r12 * r13
                float r5 = r5 - r12
                r1.translate(r3, r5)
                r3 = 1127481344(0x43340000, float:180.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r9)
                int r5 = -r5
                float r5 = (float) r5
                r1.rotate(r3, r10, r5)
                android.graphics.Path r3 = r0.arrowPath
                android.graphics.Paint r5 = r0.paint
                r1.drawPath(r3, r5)
                r19.restore()
            L_0x0135:
                int r3 = r0.type
                r5 = 1106247680(0x41var_, float:30.0)
                r11 = 1132396544(0x437var_, float:255.0)
                r12 = 1
                r13 = 1065353216(0x3var_, float:1.0)
                if (r3 != 0) goto L_0x0273
                boolean r3 = r0.isMoving
                if (r3 != 0) goto L_0x014a
                float r3 = r0.bubbleProgress
                int r3 = (r3 > r10 ? 1 : (r3 == r10 ? 0 : -1))
                if (r3 == 0) goto L_0x038a
            L_0x014a:
                android.graphics.Paint r3 = r0.paint
                float r4 = r0.bubbleProgress
                float r4 = r4 * r11
                int r4 = (int) r4
                r3.setAlpha(r4)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r3 = r3 + r2
                r4 = 1110966272(0x42380000, float:46.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                int r2 = r2 - r4
                r4 = 0
                r5 = 1094713344(0x41400000, float:12.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r5)
                if (r2 > r7) goto L_0x0173
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r7 = r7 - r2
                float r4 = (float) r7
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
            L_0x0173:
                r5 = 1092616192(0x41200000, float:10.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r5)
                float r7 = (float) r7
                float r9 = (float) r2
                r1.translate(r7, r9)
                r7 = 1105723392(0x41e80000, float:29.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r9 = (float) r9
                r11 = 1109393408(0x42200000, float:40.0)
                r14 = 1110441984(0x42300000, float:44.0)
                int r9 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
                if (r9 > 0) goto L_0x01a7
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r14)
                float r9 = (float) r9
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r6 = (float) r6
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r7 = (float) r7
                float r7 = r4 / r7
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
                float r11 = (float) r11
                float r7 = r7 * r11
                float r6 = r6 + r7
                goto L_0x01cd
            L_0x01a7:
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r9 = (float) r9
                float r4 = r4 - r9
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r14)
                float r9 = (float) r9
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r6 = (float) r6
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r7 = (float) r7
                float r7 = r4 / r7
                float r7 = r13 - r7
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
                float r11 = (float) r11
                float r7 = r7 * r11
                float r6 = r6 + r7
                r17 = r9
                r9 = r6
                r6 = r17
            L_0x01cd:
                boolean r7 = r0.isRtl
                r11 = 4
                r14 = 6
                r15 = 0
                if (r7 == 0) goto L_0x01e2
                float[] r13 = r0.radii
                r16 = r13[r15]
                int r16 = (r16 > r9 ? 1 : (r16 == r9 ? 0 : -1))
                if (r16 != 0) goto L_0x01f2
                r13 = r13[r14]
                int r13 = (r13 > r6 ? 1 : (r13 == r6 ? 0 : -1))
                if (r13 != 0) goto L_0x01f2
            L_0x01e2:
                if (r7 != 0) goto L_0x0246
                float[] r13 = r0.radii
                r16 = r13[r8]
                int r16 = (r16 > r9 ? 1 : (r16 == r9 ? 0 : -1))
                if (r16 != 0) goto L_0x01f2
                r13 = r13[r11]
                int r13 = (r13 > r6 ? 1 : (r13 == r6 ? 0 : -1))
                if (r13 == 0) goto L_0x0246
            L_0x01f2:
                if (r7 == 0) goto L_0x0200
                float[] r7 = r0.radii
                r7[r12] = r9
                r7[r15] = r9
                r8 = 7
                r7[r8] = r6
                r7[r14] = r6
                goto L_0x020c
            L_0x0200:
                float[] r7 = r0.radii
                r12 = 3
                r7[r12] = r9
                r7[r8] = r9
                r8 = 5
                r7[r8] = r6
                r7[r11] = r6
            L_0x020c:
                android.graphics.Path r7 = r0.path
                r7.reset()
                android.graphics.RectF r7 = r0.rect
                boolean r8 = r0.isRtl
                if (r8 == 0) goto L_0x021d
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                float r5 = (float) r5
                goto L_0x021e
            L_0x021d:
                r5 = 0
            L_0x021e:
                boolean r8 = r0.isRtl
                r11 = 1118830592(0x42b00000, float:88.0)
                if (r8 == 0) goto L_0x0227
                r8 = 1120141312(0x42CLASSNAME, float:98.0)
                goto L_0x0229
            L_0x0227:
                r8 = 1118830592(0x42b00000, float:88.0)
            L_0x0229:
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                float r8 = (float) r8
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
                float r11 = (float) r11
                r7.set(r5, r10, r8, r11)
                android.graphics.Path r5 = r0.path
                android.graphics.RectF r7 = r0.rect
                float[] r8 = r0.radii
                android.graphics.Path$Direction r11 = android.graphics.Path.Direction.CW
                r5.addRoundRect(r7, r8, r11)
                android.graphics.Path r5 = r0.path
                r5.close()
            L_0x0246:
                android.text.StaticLayout r5 = r0.letterLayout
                if (r5 == 0) goto L_0x024b
                goto L_0x024d
            L_0x024b:
                android.text.StaticLayout r5 = r0.oldLetterLayout
            L_0x024d:
                if (r5 == 0) goto L_0x0271
                r19.save()
                float r7 = r0.bubbleProgress
                int r8 = r0.scrollX
                float r8 = (float) r8
                int r11 = r3 - r2
                float r11 = (float) r11
                r1.scale(r7, r7, r8, r11)
                android.graphics.Path r7 = r0.path
                android.graphics.Paint r8 = r0.paint
                r1.drawPath(r7, r8)
                float r7 = r0.textX
                float r8 = r0.textY
                r1.translate(r7, r8)
                r5.draw(r1)
                r19.restore()
            L_0x0271:
                goto L_0x038a
            L_0x0273:
                if (r3 != r12) goto L_0x038a
                android.text.StaticLayout r3 = r0.letterLayout
                if (r3 == 0) goto L_0x038a
                float r3 = r0.floatingDateProgress
                int r3 = (r3 > r10 ? 1 : (r3 == r10 ? 0 : -1))
                if (r3 == 0) goto L_0x038a
                r19.save()
                r3 = 1060320051(0x3var_, float:0.7)
                r6 = 1050253722(0x3e99999a, float:0.3)
                float r8 = r0.floatingDateProgress
                float r8 = r8 * r6
                float r8 = r8 + r3
                android.graphics.RectF r3 = r0.rect
                float r3 = r3.right
                r6 = 1094713344(0x41400000, float:12.0)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r6 = (float) r12
                float r3 = r3 - r6
                android.graphics.RectF r6 = r0.rect
                float r6 = r6.centerY()
                r1.scale(r8, r8, r3, r6)
                android.graphics.RectF r3 = r0.rect
                float r3 = r3.centerY()
                android.graphics.RectF r6 = r0.rect
                float r6 = r6.left
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                float r5 = (float) r5
                float r12 = r0.bubbleProgress
                float r5 = r5 * r12
                float r6 = r6 - r5
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r5 = (float) r5
                float r6 = r6 - r5
                android.text.StaticLayout r5 = r0.letterLayout
                int r5 = r5.getHeight()
                float r5 = (float) r5
                float r5 = r5 / r9
                r12 = 1086324736(0x40CLASSNAME, float:6.0)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
                float r12 = (float) r12
                float r5 = r5 + r12
                android.graphics.RectF r12 = r0.rect
                android.text.StaticLayout r13 = r0.letterLayout
                int r13 = r13.getWidth()
                float r13 = (float) r13
                float r13 = r6 - r13
                r14 = 1108344832(0x42100000, float:36.0)
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
                float r14 = (float) r14
                float r13 = r13 - r14
                android.text.StaticLayout r14 = r0.letterLayout
                int r14 = r14.getHeight()
                float r14 = (float) r14
                float r14 = r14 / r9
                float r14 = r3 - r14
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r15 = (float) r15
                float r14 = r14 - r15
                r15 = 1094713344(0x41400000, float:12.0)
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
                float r15 = (float) r15
                float r15 = r6 - r15
                android.text.StaticLayout r10 = r0.letterLayout
                int r10 = r10.getHeight()
                float r10 = (float) r10
                float r10 = r10 / r9
                float r10 = r10 + r3
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r7 = (float) r7
                float r10 = r10 + r7
                r12.set(r13, r14, r15, r10)
                android.graphics.Paint r7 = r0.paint2
                int r7 = r7.getAlpha()
                android.text.TextPaint r10 = r0.letterPaint
                int r10 = r10.getAlpha()
                android.graphics.Paint r12 = r0.paint2
                float r13 = (float) r7
                float r14 = r0.floatingDateProgress
                float r13 = r13 * r14
                int r13 = (int) r13
                r12.setAlpha(r13)
                android.text.TextPaint r12 = r0.letterPaint
                float r13 = (float) r10
                float r14 = r0.floatingDateProgress
                float r13 = r13 * r14
                int r13 = (int) r13
                r12.setAlpha(r13)
                android.graphics.drawable.Drawable r12 = r0.fastScrollBackgroundDrawable
                android.graphics.RectF r13 = r0.rect
                float r13 = r13.left
                int r13 = (int) r13
                android.graphics.RectF r14 = r0.rect
                float r14 = r14.top
                int r14 = (int) r14
                android.graphics.RectF r15 = r0.rect
                float r15 = r15.right
                int r15 = (int) r15
                android.graphics.RectF r9 = r0.rect
                float r9 = r9.bottom
                int r9 = (int) r9
                r12.setBounds(r13, r14, r15, r9)
                android.graphics.drawable.Drawable r9 = r0.fastScrollBackgroundDrawable
                float r12 = r0.floatingDateProgress
                float r12 = r12 * r11
                int r11 = (int) r12
                r9.setAlpha(r11)
                android.graphics.drawable.Drawable r9 = r0.fastScrollBackgroundDrawable
                r9.draw(r1)
                r19.save()
                android.text.StaticLayout r9 = r0.letterLayout
                int r9 = r9.getWidth()
                float r9 = (float) r9
                float r9 = r6 - r9
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                float r4 = (float) r4
                float r9 = r9 - r4
                android.text.StaticLayout r4 = r0.letterLayout
                int r4 = r4.getHeight()
                float r4 = (float) r4
                r11 = 1073741824(0x40000000, float:2.0)
                float r4 = r4 / r11
                float r4 = r3 - r4
                r1.translate(r9, r4)
                android.text.StaticLayout r4 = r0.letterLayout
                r4.draw(r1)
                r19.restore()
                android.graphics.Paint r4 = r0.paint2
                r4.setAlpha(r7)
                android.text.TextPaint r4 = r0.letterPaint
                r4.setAlpha(r10)
                r19.restore()
            L_0x038a:
                long r3 = java.lang.System.currentTimeMillis()
                long r5 = r0.lastUpdateTime
                long r5 = r3 - r5
                r7 = 0
                int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
                if (r9 < 0) goto L_0x039e
                r7 = 17
                int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
                if (r9 <= 0) goto L_0x03a0
            L_0x039e:
                r5 = 17
            L_0x03a0:
                boolean r7 = r0.isMoving
                r8 = 1123024896(0x42var_, float:120.0)
                if (r7 == 0) goto L_0x03b2
                android.text.StaticLayout r9 = r0.letterLayout
                if (r9 == 0) goto L_0x03b2
                float r9 = r0.bubbleProgress
                r10 = 1065353216(0x3var_, float:1.0)
                int r9 = (r9 > r10 ? 1 : (r9 == r10 ? 0 : -1))
                if (r9 < 0) goto L_0x03bf
            L_0x03b2:
                if (r7 == 0) goto L_0x03b8
                android.text.StaticLayout r7 = r0.letterLayout
                if (r7 != 0) goto L_0x03ea
            L_0x03b8:
                float r7 = r0.bubbleProgress
                r9 = 0
                int r7 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
                if (r7 <= 0) goto L_0x03ea
            L_0x03bf:
                r0.lastUpdateTime = r3
                r18.invalidate()
                boolean r7 = r0.isMoving
                if (r7 == 0) goto L_0x03dc
                android.text.StaticLayout r7 = r0.letterLayout
                if (r7 == 0) goto L_0x03dc
                float r7 = r0.bubbleProgress
                float r9 = (float) r5
                float r9 = r9 / r8
                float r7 = r7 + r9
                r0.bubbleProgress = r7
                r9 = 1065353216(0x3var_, float:1.0)
                int r7 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
                if (r7 <= 0) goto L_0x03ea
                r0.bubbleProgress = r9
                goto L_0x03ea
            L_0x03dc:
                float r7 = r0.bubbleProgress
                float r9 = (float) r5
                float r9 = r9 / r8
                float r7 = r7 - r9
                r0.bubbleProgress = r7
                r9 = 0
                int r7 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
                if (r7 >= 0) goto L_0x03ea
                r0.bubbleProgress = r9
            L_0x03ea:
                boolean r7 = r0.floatingDateVisible
                if (r7 == 0) goto L_0x0405
                float r9 = r0.floatingDateProgress
                r10 = 1065353216(0x3var_, float:1.0)
                int r11 = (r9 > r10 ? 1 : (r9 == r10 ? 0 : -1))
                if (r11 == 0) goto L_0x0405
                float r7 = (float) r5
                float r7 = r7 / r8
                float r9 = r9 + r7
                r0.floatingDateProgress = r9
                int r7 = (r9 > r10 ? 1 : (r9 == r10 ? 0 : -1))
                if (r7 <= 0) goto L_0x0401
                r0.floatingDateProgress = r10
            L_0x0401:
                r18.invalidate()
                goto L_0x041c
            L_0x0405:
                if (r7 != 0) goto L_0x041c
                float r7 = r0.floatingDateProgress
                r9 = 0
                int r10 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
                if (r10 == 0) goto L_0x041c
                float r10 = (float) r5
                float r10 = r10 / r8
                float r7 = r7 - r10
                r0.floatingDateProgress = r7
                int r7 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
                if (r7 >= 0) goto L_0x0419
                r0.floatingDateProgress = r9
            L_0x0419:
                r18.invalidate()
            L_0x041c:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.RecyclerListView.FastScroll.onDraw(android.graphics.Canvas):void");
        }

        public void layout(int l, int t, int r, int b) {
            if (RecyclerListView.this.selfOnLayout) {
                super.layout(l, t, r, b);
            }
        }

        public void setProgress(float value) {
            this.progress = value;
            invalidate();
        }

        public boolean isPressed() {
            return this.pressed;
        }

        public void showFloatingDate() {
            if (this.type == 1) {
                if (!this.floatingDateVisible) {
                    this.floatingDateVisible = true;
                    invalidate();
                }
                AndroidUtilities.cancelRunOnUIThread(this.hideFloatingDateRunnable);
                AndroidUtilities.runOnUIThread(this.hideFloatingDateRunnable, 2000);
            }
        }

        public void setIsVisible(boolean visible) {
            if (this.isVisible != visible) {
                this.isVisible = visible;
                float f = visible ? 1.0f : 0.0f;
                this.visibilityAlpha = f;
                super.setAlpha(this.viewAlpha * f);
            }
        }

        public void setVisibilityAlpha(float v) {
            if (this.visibilityAlpha != v) {
                this.visibilityAlpha = v;
                super.setAlpha(this.viewAlpha * v);
            }
        }

        public void setAlpha(float alpha) {
            if (this.viewAlpha != alpha) {
                this.viewAlpha = alpha;
                super.setAlpha(this.visibilityAlpha * alpha);
            }
        }

        public float getAlpha() {
            return this.viewAlpha;
        }

        public int getScrollBarY() {
            return ((int) Math.ceil((double) (((float) (getMeasuredHeight() - AndroidUtilities.dp(54.0f))) * this.progress))) + AndroidUtilities.dp(17.0f);
        }

        public float getProgress() {
            return this.progress;
        }
    }

    private class RecyclerListViewItemClickListener implements RecyclerView.OnItemTouchListener {
        public RecyclerListViewItemClickListener(Context context) {
            GestureDetector unused = RecyclerListView.this.gestureDetector = new GestureDetector(context, new GestureDetector.OnGestureListener(RecyclerListView.this) {
                public boolean onSingleTapUp(MotionEvent e) {
                    if (!(RecyclerListView.this.currentChildView == null || (RecyclerListView.this.onItemClickListener == null && RecyclerListView.this.onItemClickListenerExtended == null))) {
                        float x = e.getX();
                        float y = e.getY();
                        RecyclerListView.this.onChildPressed(RecyclerListView.this.currentChildView, x, y, true);
                        View view = RecyclerListView.this.currentChildView;
                        int position = RecyclerListView.this.currentChildPosition;
                        if (RecyclerListView.this.instantClick && position != -1) {
                            view.playSoundEffect(0);
                            view.sendAccessibilityEvent(1);
                            if (RecyclerListView.this.onItemClickListener != null) {
                                RecyclerListView.this.onItemClickListener.onItemClick(view, position);
                            } else if (RecyclerListView.this.onItemClickListenerExtended != null) {
                                RecyclerListView.this.onItemClickListenerExtended.onItemClick(view, position, x - view.getX(), y - view.getY());
                            }
                        }
                        final View view2 = view;
                        final int i = position;
                        final float f = x;
                        final float f2 = y;
                        AndroidUtilities.runOnUIThread(RecyclerListView.this.clickRunnable = new Runnable() {
                            public void run() {
                                if (this == RecyclerListView.this.clickRunnable) {
                                    Runnable unused = RecyclerListView.this.clickRunnable = null;
                                }
                                if (view2 != null) {
                                    RecyclerListView.this.onChildPressed(view2, 0.0f, 0.0f, false);
                                    if (!RecyclerListView.this.instantClick) {
                                        view2.playSoundEffect(0);
                                        view2.sendAccessibilityEvent(1);
                                        if (i == -1) {
                                            return;
                                        }
                                        if (RecyclerListView.this.onItemClickListener != null) {
                                            RecyclerListView.this.onItemClickListener.onItemClick(view2, i);
                                        } else if (RecyclerListView.this.onItemClickListenerExtended != null) {
                                            OnItemClickListenerExtended access$700 = RecyclerListView.this.onItemClickListenerExtended;
                                            View view = view2;
                                            access$700.onItemClick(view, i, f - view.getX(), f2 - view2.getY());
                                        }
                                    }
                                }
                            }
                        }, (long) ViewConfiguration.getPressedStateDuration());
                        if (RecyclerListView.this.selectChildRunnable != null) {
                            View pressedChild = RecyclerListView.this.currentChildView;
                            AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                            Runnable unused = RecyclerListView.this.selectChildRunnable = null;
                            View unused2 = RecyclerListView.this.currentChildView = null;
                            boolean unused3 = RecyclerListView.this.interceptedByChild = false;
                            RecyclerListView.this.removeSelection(pressedChild, e);
                        }
                    }
                    return true;
                }

                public void onLongPress(MotionEvent event) {
                    if (RecyclerListView.this.currentChildView != null && RecyclerListView.this.currentChildPosition != -1) {
                        if (RecyclerListView.this.onItemLongClickListener != null || RecyclerListView.this.onItemLongClickListenerExtended != null) {
                            View child = RecyclerListView.this.currentChildView;
                            if (RecyclerListView.this.onItemLongClickListener != null) {
                                if (RecyclerListView.this.onItemLongClickListener.onItemClick(RecyclerListView.this.currentChildView, RecyclerListView.this.currentChildPosition)) {
                                    child.performHapticFeedback(0);
                                    child.sendAccessibilityEvent(2);
                                }
                            } else if (RecyclerListView.this.onItemLongClickListenerExtended.onItemClick(RecyclerListView.this.currentChildView, RecyclerListView.this.currentChildPosition, event.getX() - RecyclerListView.this.currentChildView.getX(), event.getY() - RecyclerListView.this.currentChildView.getY())) {
                                child.performHapticFeedback(0);
                                child.sendAccessibilityEvent(2);
                                boolean unused = RecyclerListView.this.longPressCalled = true;
                            }
                        }
                    }
                }

                public boolean onDown(MotionEvent e) {
                    return false;
                }

                public void onShowPress(MotionEvent e) {
                }

                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    return false;
                }

                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    return false;
                }
            });
            RecyclerListView.this.gestureDetector.setIsLongpressEnabled(false);
        }

        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent event) {
            View v;
            MotionEvent motionEvent = event;
            int action = event.getActionMasked();
            boolean isScrollIdle = RecyclerListView.this.getScrollState() == 0;
            if (action != 0 && action != 5) {
                RecyclerView recyclerView = view;
            } else if (RecyclerListView.this.currentChildView != null || !isScrollIdle) {
                RecyclerView recyclerView2 = view;
            } else {
                float ex = event.getX();
                float ey = event.getY();
                boolean unused = RecyclerListView.this.longPressCalled = false;
                RecyclerView.ItemAnimator animator = RecyclerListView.this.getItemAnimator();
                if ((RecyclerListView.this.allowItemsInteractionDuringAnimation || animator == null || !animator.isRunning()) && RecyclerListView.this.allowSelectChildAtPosition(ex, ey) && (v = RecyclerListView.this.findChildViewUnder(ex, ey)) != null && RecyclerListView.this.allowSelectChildAtPosition(v)) {
                    View unused2 = RecyclerListView.this.currentChildView = v;
                }
                if (RecyclerListView.this.currentChildView instanceof ViewGroup) {
                    float x = event.getX() - ((float) RecyclerListView.this.currentChildView.getLeft());
                    float y = event.getY() - ((float) RecyclerListView.this.currentChildView.getTop());
                    ViewGroup viewGroup = (ViewGroup) RecyclerListView.this.currentChildView;
                    int i = viewGroup.getChildCount() - 1;
                    while (true) {
                        if (i < 0) {
                            break;
                        }
                        View child = viewGroup.getChildAt(i);
                        if (x >= ((float) child.getLeft()) && x <= ((float) child.getRight()) && y >= ((float) child.getTop()) && y <= ((float) child.getBottom()) && child.isClickable()) {
                            View unused3 = RecyclerListView.this.currentChildView = null;
                            break;
                        }
                        i--;
                    }
                }
                int unused4 = RecyclerListView.this.currentChildPosition = -1;
                if (RecyclerListView.this.currentChildView != null) {
                    RecyclerListView recyclerListView = RecyclerListView.this;
                    int unused5 = recyclerListView.currentChildPosition = view.getChildPosition(recyclerListView.currentChildView);
                    MotionEvent childEvent = MotionEvent.obtain(0, 0, event.getActionMasked(), event.getX() - ((float) RecyclerListView.this.currentChildView.getLeft()), event.getY() - ((float) RecyclerListView.this.currentChildView.getTop()), 0);
                    if (RecyclerListView.this.currentChildView.onTouchEvent(childEvent)) {
                        boolean unused6 = RecyclerListView.this.interceptedByChild = true;
                    }
                    childEvent.recycle();
                } else {
                    RecyclerView recyclerView3 = view;
                }
            }
            if (RecyclerListView.this.currentChildView != null && !RecyclerListView.this.interceptedByChild) {
                try {
                    RecyclerListView.this.gestureDetector.onTouchEvent(motionEvent);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            if (action == 0 || action == 5) {
                if (RecyclerListView.this.interceptedByChild || RecyclerListView.this.currentChildView == null) {
                    RecyclerListView.this.selectorRect.setEmpty();
                    return false;
                }
                float x2 = event.getX();
                float y2 = event.getY();
                Runnable unused7 = RecyclerListView.this.selectChildRunnable = new RecyclerListView$RecyclerListViewItemClickListener$$ExternalSyntheticLambda0(this, x2, y2);
                AndroidUtilities.runOnUIThread(RecyclerListView.this.selectChildRunnable, (long) ViewConfiguration.getTapTimeout());
                if (RecyclerListView.this.currentChildView.isEnabled()) {
                    RecyclerListView recyclerListView2 = RecyclerListView.this;
                    if (recyclerListView2.canHighlightChildAt(recyclerListView2.currentChildView, x2 - RecyclerListView.this.currentChildView.getX(), y2 - RecyclerListView.this.currentChildView.getY())) {
                        RecyclerListView recyclerListView3 = RecyclerListView.this;
                        recyclerListView3.positionSelector(recyclerListView3.currentChildPosition, RecyclerListView.this.currentChildView);
                        if (RecyclerListView.this.selectorDrawable != null) {
                            Drawable d = RecyclerListView.this.selectorDrawable.getCurrent();
                            if (d instanceof TransitionDrawable) {
                                if (RecyclerListView.this.onItemLongClickListener == null && RecyclerListView.this.onItemClickListenerExtended == null) {
                                    ((TransitionDrawable) d).resetTransition();
                                } else {
                                    ((TransitionDrawable) d).startTransition(ViewConfiguration.getLongPressTimeout());
                                }
                            }
                            if (Build.VERSION.SDK_INT >= 21) {
                                RecyclerListView.this.selectorDrawable.setHotspot(event.getX(), event.getY());
                            }
                        }
                        RecyclerListView.this.updateSelectorState();
                        return false;
                    }
                }
                RecyclerListView.this.selectorRect.setEmpty();
                return false;
            } else if ((action != 1 && action != 6 && action != 3 && isScrollIdle) || RecyclerListView.this.currentChildView == null) {
                return false;
            } else {
                if (RecyclerListView.this.selectChildRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                    Runnable unused8 = RecyclerListView.this.selectChildRunnable = null;
                }
                View pressedChild = RecyclerListView.this.currentChildView;
                RecyclerListView recyclerListView4 = RecyclerListView.this;
                recyclerListView4.onChildPressed(recyclerListView4.currentChildView, 0.0f, 0.0f, false);
                View unused9 = RecyclerListView.this.currentChildView = null;
                boolean unused10 = RecyclerListView.this.interceptedByChild = false;
                RecyclerListView.this.removeSelection(pressedChild, motionEvent);
                if ((action != 1 && action != 6 && action != 3) || RecyclerListView.this.onItemLongClickListenerExtended == null || !RecyclerListView.this.longPressCalled) {
                    return false;
                }
                RecyclerListView.this.onItemLongClickListenerExtended.onLongClickRelease();
                boolean unused11 = RecyclerListView.this.longPressCalled = false;
                return false;
            }
        }

        /* renamed from: lambda$onInterceptTouchEvent$0$org-telegram-ui-Components-RecyclerListView$RecyclerListViewItemClickListener  reason: not valid java name */
        public /* synthetic */ void m2536x7396bf8f(float x, float y) {
            if (RecyclerListView.this.selectChildRunnable != null && RecyclerListView.this.currentChildView != null) {
                RecyclerListView recyclerListView = RecyclerListView.this;
                recyclerListView.onChildPressed(recyclerListView.currentChildView, x, y, true);
                Runnable unused = RecyclerListView.this.selectChildRunnable = null;
            }
        }

        public void onTouchEvent(RecyclerView view, MotionEvent event) {
        }

        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            RecyclerListView.this.cancelClickRunnables(true);
        }
    }

    public View findChildViewUnder(float x, float y) {
        int count = getChildCount();
        int a = 0;
        while (a < 2) {
            for (int i = count - 1; i >= 0; i--) {
                View child = getChildAt(i);
                float translationY = 0.0f;
                float translationX = a == 0 ? child.getTranslationX() : 0.0f;
                if (a == 0) {
                    translationY = child.getTranslationY();
                }
                if (x >= ((float) child.getLeft()) + translationX && x <= ((float) child.getRight()) + translationX && y >= ((float) child.getTop()) + translationY && y <= ((float) child.getBottom()) + translationY) {
                    return child;
                }
            }
            a++;
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean canHighlightChildAt(View child, float x, float y) {
        return true;
    }

    public void setDisableHighlightState(boolean value) {
        this.disableHighlightState = value;
    }

    /* access modifiers changed from: protected */
    public View getPressedChildView() {
        return this.currentChildView;
    }

    /* access modifiers changed from: protected */
    public void onChildPressed(View child, float x, float y, boolean pressed) {
        if (!this.disableHighlightState && child != null) {
            child.setPressed(pressed);
        }
    }

    /* access modifiers changed from: protected */
    public boolean allowSelectChildAtPosition(float x, float y) {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean allowSelectChildAtPosition(View child) {
        return true;
    }

    /* access modifiers changed from: private */
    public void removeSelection(View pressedChild, MotionEvent event) {
        if (pressedChild != null && !this.selectorRect.isEmpty()) {
            if (pressedChild.isEnabled()) {
                positionSelector(this.currentChildPosition, pressedChild);
                Drawable drawable = this.selectorDrawable;
                if (drawable != null) {
                    Drawable d = drawable.getCurrent();
                    if (d instanceof TransitionDrawable) {
                        ((TransitionDrawable) d).resetTransition();
                    }
                    if (event != null && Build.VERSION.SDK_INT >= 21) {
                        this.selectorDrawable.setHotspot(event.getX(), event.getY());
                    }
                }
            } else {
                this.selectorRect.setEmpty();
            }
            updateSelectorState();
        }
    }

    public void cancelClickRunnables(boolean uncheck) {
        Runnable runnable = this.selectChildRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.selectChildRunnable = null;
        }
        View view = this.currentChildView;
        if (view != null) {
            View child = this.currentChildView;
            if (uncheck) {
                onChildPressed(view, 0.0f, 0.0f, false);
            }
            this.currentChildView = null;
            removeSelection(child, (MotionEvent) null);
        }
        this.selectorRect.setEmpty();
        Runnable runnable2 = this.clickRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            this.clickRunnable = null;
        }
        this.interceptedByChild = false;
    }

    public int[] getResourceDeclareStyleableIntArray(String packageName, String name) {
        try {
            Field f = Class.forName(packageName + ".R$styleable").getField(name);
            if (f != null) {
                return (int[]) f.get((Object) null);
            }
        } catch (Throwable th) {
        }
        return null;
    }

    public RecyclerListView(Context context) {
        this(context, (Theme.ResourcesProvider) null);
    }

    public RecyclerListView(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.allowItemsInteractionDuringAnimation = true;
        this.currentFirst = -1;
        this.currentVisible = -1;
        this.hideIfEmpty = true;
        this.selectorType = 2;
        this.selectorRect = new Rect();
        this.scrollEnabled = true;
        this.lastX = Float.MAX_VALUE;
        this.lastY = Float.MAX_VALUE;
        this.observer = new RecyclerView.AdapterDataObserver() {
            public void onChanged() {
                RecyclerListView.this.checkIfEmpty(true);
                int unused = RecyclerListView.this.currentFirst = -1;
                if (RecyclerListView.this.removeHighlighSelectionRunnable == null) {
                    RecyclerListView.this.selectorRect.setEmpty();
                }
                RecyclerListView.this.invalidate();
            }

            public void onItemRangeInserted(int positionStart, int itemCount) {
                RecyclerListView.this.checkIfEmpty(true);
                if (RecyclerListView.this.pinnedHeader != null && RecyclerListView.this.pinnedHeader.getAlpha() == 0.0f) {
                    int unused = RecyclerListView.this.currentFirst = -1;
                    RecyclerListView.this.invalidateViews();
                }
            }

            public void onItemRangeRemoved(int positionStart, int itemCount) {
                RecyclerListView.this.checkIfEmpty(true);
            }
        };
        this.scroller = new Runnable() {
            public void run() {
                int dy;
                RecyclerListView.this.multiSelectionListener.getPaddings(RecyclerListView.this.listPaddings);
                if (RecyclerListView.this.multiselectScrollToTop) {
                    dy = -AndroidUtilities.dp(12.0f);
                    RecyclerListView recyclerListView = RecyclerListView.this;
                    boolean unused = recyclerListView.chekMultiselect(0.0f, (float) recyclerListView.listPaddings[0]);
                } else {
                    dy = AndroidUtilities.dp(12.0f);
                    RecyclerListView recyclerListView2 = RecyclerListView.this;
                    boolean unused2 = recyclerListView2.chekMultiselect(0.0f, (float) (recyclerListView2.getMeasuredHeight() - RecyclerListView.this.listPaddings[1]));
                }
                RecyclerListView.this.multiSelectionListener.scrollBy(dy);
                if (RecyclerListView.this.multiselectScrollRunning) {
                    AndroidUtilities.runOnUIThread(RecyclerListView.this.scroller);
                }
            }
        };
        this.resourcesProvider = resourcesProvider2;
        setGlowColor(getThemedColor("actionBarDefault"));
        Drawable selectorDrawable2 = Theme.getSelectorDrawable(getThemedColor("listSelectorSDK21"), false);
        this.selectorDrawable = selectorDrawable2;
        selectorDrawable2.setCallback(this);
        try {
            if (!gotAttributes) {
                int[] resourceDeclareStyleableIntArray = getResourceDeclareStyleableIntArray("com.android.internal", "View");
                attributes = resourceDeclareStyleableIntArray;
                if (resourceDeclareStyleableIntArray == null) {
                    attributes = new int[0];
                }
                gotAttributes = true;
            }
            TypedArray a = context.getTheme().obtainStyledAttributes(attributes);
            View.class.getDeclaredMethod("initializeScrollbars", new Class[]{TypedArray.class}).invoke(this, new Object[]{a});
            a.recycle();
        } catch (Throwable e) {
            FileLog.e(e);
        }
        super.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                boolean z = false;
                if (!(newState == 0 || RecyclerListView.this.currentChildView == null)) {
                    if (RecyclerListView.this.selectChildRunnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                        Runnable unused = RecyclerListView.this.selectChildRunnable = null;
                    }
                    MotionEvent event = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                    try {
                        RecyclerListView.this.gestureDetector.onTouchEvent(event);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    RecyclerListView.this.currentChildView.onTouchEvent(event);
                    event.recycle();
                    View child = RecyclerListView.this.currentChildView;
                    RecyclerListView recyclerListView = RecyclerListView.this;
                    recyclerListView.onChildPressed(recyclerListView.currentChildView, 0.0f, 0.0f, false);
                    View unused2 = RecyclerListView.this.currentChildView = null;
                    RecyclerListView.this.removeSelection(child, (MotionEvent) null);
                    boolean unused3 = RecyclerListView.this.interceptedByChild = false;
                }
                if (RecyclerListView.this.onScrollListener != null) {
                    RecyclerListView.this.onScrollListener.onScrollStateChanged(recyclerView, newState);
                }
                RecyclerListView recyclerListView2 = RecyclerListView.this;
                if (newState == 1 || newState == 2) {
                    z = true;
                }
                recyclerListView2.scrollingByUser = z;
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (RecyclerListView.this.onScrollListener != null) {
                    RecyclerListView.this.onScrollListener.onScrolled(recyclerView, dx, dy);
                }
                if (RecyclerListView.this.selectorPosition != -1) {
                    RecyclerListView.this.selectorRect.offset(-dx, -dy);
                    RecyclerListView.this.selectorDrawable.setBounds(RecyclerListView.this.selectorRect);
                    RecyclerListView.this.invalidate();
                } else {
                    RecyclerListView.this.selectorRect.setEmpty();
                }
                RecyclerListView.this.checkSection(false);
                if (dy != 0 && RecyclerListView.this.fastScroll != null) {
                    RecyclerListView.this.fastScroll.showFloatingDate();
                }
            }
        });
        addOnItemTouchListener(new RecyclerListViewItemClickListener(context));
    }

    public void setVerticalScrollBarEnabled(boolean verticalScrollBarEnabled) {
        if (attributes != null) {
            super.setVerticalScrollBarEnabled(verticalScrollBarEnabled);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (this.fastScroll != null) {
            int height = (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom();
            this.fastScroll.getLayoutParams().height = height;
            this.fastScroll.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(132.0f), NUM), View.MeasureSpec.makeMeasureSpec(height, NUM));
        }
        this.touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (this.fastScroll != null) {
            this.selfOnLayout = true;
            int t2 = t + getPaddingTop();
            if (this.fastScroll.isRtl) {
                FastScroll fastScroll2 = this.fastScroll;
                fastScroll2.layout(0, t2, fastScroll2.getMeasuredWidth(), this.fastScroll.getMeasuredHeight() + t2);
            } else {
                int x = getMeasuredWidth() - this.fastScroll.getMeasuredWidth();
                FastScroll fastScroll3 = this.fastScroll;
                fastScroll3.layout(x, t2, fastScroll3.getMeasuredWidth() + x, this.fastScroll.getMeasuredHeight() + t2);
            }
            this.selfOnLayout = false;
        }
        checkSection(false);
        IntReturnCallback intReturnCallback = this.pendingHighlightPosition;
        if (intReturnCallback != null) {
            highlightRowInternal(intReturnCallback, false);
        }
    }

    public void setSelectorType(int type) {
        this.selectorType = type;
    }

    public void setSelectorRadius(int radius) {
        this.selectorRadius = radius;
    }

    public void setTopBottomSelectorRadius(int radius) {
        this.topBottomSelectorRadius = radius;
    }

    public void setDrawSelectorBehind(boolean value) {
        this.drawSelectorBehind = value;
    }

    public void setSelectorDrawableColor(int color) {
        Drawable drawable = this.selectorDrawable;
        if (drawable != null) {
            drawable.setCallback((Drawable.Callback) null);
        }
        int i = this.topBottomSelectorRadius;
        if (i > 0) {
            this.selectorDrawable = Theme.createRadSelectorDrawable(color, i, i);
        } else {
            int i2 = this.selectorRadius;
            if (i2 > 0) {
                this.selectorDrawable = Theme.createSimpleSelectorRoundRectDrawable(i2, 0, color, -16777216);
            } else {
                int i3 = this.selectorType;
                if (i3 == 2) {
                    this.selectorDrawable = Theme.getSelectorDrawable(color, false);
                } else {
                    this.selectorDrawable = Theme.createSelectorDrawable(color, i3);
                }
            }
        }
        this.selectorDrawable.setCallback(this);
    }

    public void checkSection(boolean force) {
        FastScroll fastScroll2;
        RecyclerView.ViewHolder holder;
        int firstVisibleItem;
        int startSection2;
        RecyclerView.ViewHolder holder2;
        RecyclerView.ViewHolder holder3;
        int lastVisibleItem;
        int childCount;
        int headerTop;
        if (((this.scrollingByUser || force) && this.fastScroll != null) || !(this.sectionsType == 0 || this.sectionsAdapter == null)) {
            RecyclerView.LayoutManager layoutManager = getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                if (linearLayoutManager.getOrientation() != 1) {
                } else if (this.sectionsAdapter != null) {
                    int paddingTop = getPaddingTop();
                    int i = this.sectionsType;
                    if (i == 1) {
                        int childCount2 = getChildCount();
                        int maxBottom = 0;
                        int minBottom = Integer.MAX_VALUE;
                        View minChild = null;
                        int minBottomSection = Integer.MAX_VALUE;
                        for (int a = 0; a < childCount2; a++) {
                            View child = getChildAt(a);
                            int bottom = child.getBottom();
                            if (bottom > this.sectionOffset + paddingTop) {
                                if (bottom < minBottom) {
                                    minBottom = bottom;
                                    minChild = child;
                                }
                                int maxBottom2 = Math.max(maxBottom, bottom);
                                if (bottom >= this.sectionOffset + paddingTop + AndroidUtilities.dp(32.0f) && bottom < minBottomSection) {
                                    minBottomSection = bottom;
                                    maxBottom = maxBottom2;
                                } else {
                                    maxBottom = maxBottom2;
                                }
                            }
                        }
                        if (minChild != null && (holder2 = getChildViewHolder(minChild)) != null) {
                            int firstVisibleItem2 = holder2.getAdapterPosition();
                            int lastVisibleItem2 = linearLayoutManager.findLastVisibleItemPosition();
                            int visibleItemCount = Math.abs(lastVisibleItem2 - firstVisibleItem2) + 1;
                            if (this.scrollingByUser || force) {
                                FastScroll fastScroll3 = this.fastScroll;
                                if (fastScroll3 == null || fastScroll3.isPressed()) {
                                } else if (getAdapter() instanceof FastScrollAdapter) {
                                    RecyclerView.LayoutManager layoutManager2 = layoutManager;
                                    this.fastScroll.setProgress(Math.min(1.0f, ((float) firstVisibleItem2) / ((float) ((this.sectionsAdapter.getTotalItemsCount() - visibleItemCount) + 1))));
                                }
                            } else {
                                RecyclerView.LayoutManager layoutManager3 = layoutManager;
                            }
                            this.headersCache.addAll(this.headers);
                            this.headers.clear();
                            if (this.sectionsAdapter.getItemCount() != 0) {
                                if (!(this.currentFirst == firstVisibleItem2 && this.currentVisible == visibleItemCount)) {
                                    this.currentFirst = firstVisibleItem2;
                                    this.currentVisible = visibleItemCount;
                                    this.sectionsCount = 1;
                                    int sectionForPosition = this.sectionsAdapter.getSectionForPosition(firstVisibleItem2);
                                    this.startSection = sectionForPosition;
                                    int itemNum = (this.sectionsAdapter.getCountForSection(sectionForPosition) + firstVisibleItem2) - this.sectionsAdapter.getPositionInSectionForPosition(firstVisibleItem2);
                                    while (itemNum < firstVisibleItem2 + visibleItemCount) {
                                        itemNum += this.sectionsAdapter.getCountForSection(this.startSection + this.sectionsCount);
                                        this.sectionsCount++;
                                    }
                                }
                                int itemNum2 = firstVisibleItem2;
                                int a2 = this.startSection;
                                while (a2 < this.startSection + this.sectionsCount) {
                                    View header = null;
                                    if (!this.headersCache.isEmpty()) {
                                        holder3 = holder2;
                                        this.headersCache.remove(0);
                                        header = this.headersCache.get(0);
                                    } else {
                                        holder3 = holder2;
                                    }
                                    View header2 = getSectionHeaderView(a2, header);
                                    this.headers.add(header2);
                                    int count = this.sectionsAdapter.getCountForSection(a2);
                                    if (a2 == this.startSection) {
                                        int pos = this.sectionsAdapter.getPositionInSectionForPosition(itemNum2);
                                        childCount = childCount2;
                                        if (pos == count - 1) {
                                            header2.setTag(Integer.valueOf((-header2.getHeight()) + paddingTop));
                                            lastVisibleItem = lastVisibleItem2;
                                        } else if (pos == count - 2) {
                                            View child2 = getChildAt(itemNum2 - firstVisibleItem2);
                                            if (child2 != null) {
                                                View view = child2;
                                                headerTop = child2.getTop() + paddingTop;
                                            } else {
                                                View view2 = child2;
                                                headerTop = -AndroidUtilities.dp(100.0f);
                                            }
                                            lastVisibleItem = lastVisibleItem2;
                                            header2.setTag(Integer.valueOf(Math.min(headerTop, 0)));
                                        } else {
                                            lastVisibleItem = lastVisibleItem2;
                                            header2.setTag(0);
                                        }
                                        itemNum2 += count - this.sectionsAdapter.getPositionInSectionForPosition(firstVisibleItem2);
                                    } else {
                                        childCount = childCount2;
                                        lastVisibleItem = lastVisibleItem2;
                                        View child3 = getChildAt(itemNum2 - firstVisibleItem2);
                                        if (child3 != null) {
                                            header2.setTag(Integer.valueOf(child3.getTop() + paddingTop));
                                        } else {
                                            header2.setTag(Integer.valueOf(-AndroidUtilities.dp(100.0f)));
                                        }
                                        itemNum2 += count;
                                    }
                                    a2++;
                                    childCount2 = childCount;
                                    holder2 = holder3;
                                    lastVisibleItem2 = lastVisibleItem;
                                }
                                int i2 = childCount2;
                                int i3 = lastVisibleItem2;
                                return;
                            }
                            return;
                        }
                        return;
                    }
                    if (i == 2) {
                        this.pinnedHeaderShadowTargetAlpha = 0.0f;
                        if (this.sectionsAdapter.getItemCount() != 0) {
                            int childCount3 = getChildCount();
                            int maxBottom3 = 0;
                            int minBottom2 = Integer.MAX_VALUE;
                            View minChild2 = null;
                            int minBottomSection2 = Integer.MAX_VALUE;
                            View minChildSection = null;
                            for (int a3 = 0; a3 < childCount3; a3++) {
                                View child4 = getChildAt(a3);
                                int bottom2 = child4.getBottom();
                                if (bottom2 > this.sectionOffset + paddingTop) {
                                    if (bottom2 < minBottom2) {
                                        minBottom2 = bottom2;
                                        minChild2 = child4;
                                    }
                                    maxBottom3 = Math.max(maxBottom3, bottom2);
                                    if (bottom2 >= this.sectionOffset + paddingTop + AndroidUtilities.dp(32.0f) && bottom2 < minBottomSection2) {
                                        minBottomSection2 = bottom2;
                                        minChildSection = child4;
                                    }
                                }
                            }
                            if (minChild2 != null && (holder = getChildViewHolder(minChild2)) != null && (startSection2 = this.sectionsAdapter.getSectionForPosition(firstVisibleItem)) >= 0) {
                                if (this.currentFirst != startSection2 || this.pinnedHeader == null) {
                                    View sectionHeaderView = getSectionHeaderView(startSection2, this.pinnedHeader);
                                    this.pinnedHeader = sectionHeaderView;
                                    sectionHeaderView.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), NUM), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 0));
                                    View view3 = this.pinnedHeader;
                                    view3.layout(0, 0, view3.getMeasuredWidth(), this.pinnedHeader.getMeasuredHeight());
                                    this.currentFirst = startSection2;
                                }
                                if (!(this.pinnedHeader == null || minChildSection == null || minChildSection.getClass() == this.pinnedHeader.getClass())) {
                                    this.pinnedHeaderShadowTargetAlpha = 1.0f;
                                }
                                int count2 = this.sectionsAdapter.getCountForSection(startSection2);
                                int pos2 = this.sectionsAdapter.getPositionInSectionForPosition((firstVisibleItem = holder.getAdapterPosition()));
                                int sectionOffsetY = (maxBottom3 == 0 || maxBottom3 >= getMeasuredHeight() - getPaddingBottom()) ? this.sectionOffset : 0;
                                if (pos2 == count2 - 1) {
                                    int headerHeight = this.pinnedHeader.getHeight();
                                    int headerTop2 = paddingTop;
                                    if (minChild2 != null) {
                                        int i4 = childCount3;
                                        int available = ((minChild2.getTop() - paddingTop) - this.sectionOffset) + minChild2.getHeight();
                                        if (available < headerHeight) {
                                            headerTop2 = available - headerHeight;
                                        }
                                    } else {
                                        headerTop2 = -AndroidUtilities.dp(100.0f);
                                    }
                                    if (headerTop2 < 0) {
                                        int i5 = maxBottom3;
                                        this.pinnedHeader.setTag(Integer.valueOf(paddingTop + sectionOffsetY + headerTop2));
                                    } else {
                                        this.pinnedHeader.setTag(Integer.valueOf(paddingTop + sectionOffsetY));
                                    }
                                } else {
                                    int i6 = maxBottom3;
                                    this.pinnedHeader.setTag(Integer.valueOf(paddingTop + sectionOffsetY));
                                }
                                invalidate();
                            }
                        }
                    }
                } else {
                    int firstVisibleItem3 = linearLayoutManager.findFirstVisibleItemPosition();
                    int abs = Math.abs(linearLayoutManager.findLastVisibleItemPosition() - firstVisibleItem3) + 1;
                    if (firstVisibleItem3 != -1) {
                        if ((this.scrollingByUser || force) && (fastScroll2 = this.fastScroll) != null && !fastScroll2.isPressed()) {
                            RecyclerView.Adapter adapter = getAdapter();
                            if (adapter instanceof FastScrollAdapter) {
                                float p = ((FastScrollAdapter) adapter).getScrollProgress(this);
                                this.fastScroll.setIsVisible(((FastScrollAdapter) adapter).fastScrollIsVisible(this));
                                this.fastScroll.setProgress(Math.min(1.0f, p));
                                this.fastScroll.getCurrentLetter(false);
                            }
                        }
                    }
                }
            }
        }
    }

    public void setListSelectorColor(int color) {
        Theme.setSelectorDrawableColor(this.selectorDrawable, color, true);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListenerExtended listener) {
        this.onItemClickListenerExtended = listener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return this.onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
        this.gestureDetector.setIsLongpressEnabled(listener != null);
    }

    public void setOnItemLongClickListener(OnItemLongClickListenerExtended listener) {
        this.onItemLongClickListenerExtended = listener;
        this.gestureDetector.setIsLongpressEnabled(listener != null);
    }

    public void setEmptyView(View view) {
        View view2 = this.emptyView;
        if (view2 != view) {
            if (view2 != null) {
                view2.animate().setListener((Animator.AnimatorListener) null).cancel();
            }
            this.emptyView = view;
            if (this.animateEmptyView && view != null) {
                view.setVisibility(8);
            }
            if (this.isHidden) {
                View view3 = this.emptyView;
                if (view3 != null) {
                    this.emptyViewAnimateToVisibility = 8;
                    view3.setVisibility(8);
                    return;
                }
                return;
            }
            this.emptyViewAnimateToVisibility = -1;
            checkIfEmpty(updateEmptyViewAnimated());
        }
    }

    /* access modifiers changed from: protected */
    public boolean updateEmptyViewAnimated() {
        return isAttachedToWindow();
    }

    public View getEmptyView() {
        return this.emptyView;
    }

    public void invalidateViews() {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            getChildAt(a).invalidate();
        }
    }

    public void updateFastScrollColors() {
        FastScroll fastScroll2 = this.fastScroll;
        if (fastScroll2 != null) {
            fastScroll2.updateColors();
        }
    }

    public void setPinnedHeaderShadowDrawable(Drawable drawable) {
        this.pinnedHeaderShadowDrawable = drawable;
    }

    public boolean canScrollVertically(int direction) {
        return this.scrollEnabled && super.canScrollVertically(direction);
    }

    public void setScrollEnabled(boolean value) {
        this.scrollEnabled = value;
    }

    public void highlightRow(IntReturnCallback callback) {
        highlightRowInternal(callback, true);
    }

    private void highlightRowInternal(IntReturnCallback callback, boolean canHighlightLater) {
        Runnable runnable = this.removeHighlighSelectionRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.removeHighlighSelectionRunnable = null;
        }
        RecyclerView.ViewHolder holder = findViewHolderForAdapterPosition(callback.run());
        if (holder != null) {
            positionSelector(holder.getLayoutPosition(), holder.itemView);
            Drawable drawable = this.selectorDrawable;
            if (drawable != null) {
                Drawable d = drawable.getCurrent();
                if (d instanceof TransitionDrawable) {
                    if (this.onItemLongClickListener == null && this.onItemClickListenerExtended == null) {
                        ((TransitionDrawable) d).resetTransition();
                    } else {
                        ((TransitionDrawable) d).startTransition(ViewConfiguration.getLongPressTimeout());
                    }
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    this.selectorDrawable.setHotspot((float) (holder.itemView.getMeasuredWidth() / 2), (float) (holder.itemView.getMeasuredHeight() / 2));
                }
            }
            Drawable d2 = this.selectorDrawable;
            if (d2 != null && d2.isStateful() && this.selectorDrawable.setState(getDrawableStateForSelector())) {
                invalidateDrawable(this.selectorDrawable);
            }
            RecyclerListView$$ExternalSyntheticLambda0 recyclerListView$$ExternalSyntheticLambda0 = new RecyclerListView$$ExternalSyntheticLambda0(this);
            this.removeHighlighSelectionRunnable = recyclerListView$$ExternalSyntheticLambda0;
            AndroidUtilities.runOnUIThread(recyclerListView$$ExternalSyntheticLambda0, 700);
        } else if (canHighlightLater) {
            this.pendingHighlightPosition = callback;
        }
    }

    /* renamed from: lambda$highlightRowInternal$0$org-telegram-ui-Components-RecyclerListView  reason: not valid java name */
    public /* synthetic */ void m2535x6956124b() {
        this.removeHighlighSelectionRunnable = null;
        this.pendingHighlightPosition = null;
        Drawable drawable = this.selectorDrawable;
        if (drawable != null) {
            Drawable d = drawable.getCurrent();
            if (d instanceof TransitionDrawable) {
                ((TransitionDrawable) d).resetTransition();
            }
        }
        Drawable d2 = this.selectorDrawable;
        if (d2 != null && d2.isStateful()) {
            this.selectorDrawable.setState(StateSet.NOTHING);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (!isEnabled()) {
            return false;
        }
        if (this.disallowInterceptTouchEvents) {
            requestDisallowInterceptTouchEvent(true);
        }
        OnInterceptTouchListener onInterceptTouchListener2 = this.onInterceptTouchListener;
        if ((onInterceptTouchListener2 == null || !onInterceptTouchListener2.onInterceptTouchEvent(e)) && !super.onInterceptTouchEvent(e)) {
            return false;
        }
        return true;
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view;
        if (this.sectionsAdapter == null || (view = this.pinnedHeader) == null || view.getAlpha() == 0.0f || !this.pinnedHeader.dispatchTouchEvent(ev)) {
            return super.dispatchTouchEvent(ev);
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void checkIfEmpty(boolean animated) {
        if (!this.isHidden) {
            int i = 0;
            if (getAdapter() != null && this.emptyView != null) {
                boolean emptyViewVisible = emptyViewIsVisible();
                int newVisibility = emptyViewVisible ? 0 : 8;
                if (!this.animateEmptyView) {
                    animated = false;
                }
                if (!animated) {
                    this.emptyViewAnimateToVisibility = newVisibility;
                    this.emptyView.setVisibility(newVisibility);
                    this.emptyView.setAlpha(1.0f);
                } else if (this.emptyViewAnimateToVisibility != newVisibility) {
                    this.emptyViewAnimateToVisibility = newVisibility;
                    if (newVisibility == 0) {
                        this.emptyView.animate().setListener((Animator.AnimatorListener) null).cancel();
                        if (this.emptyView.getVisibility() == 8) {
                            this.emptyView.setVisibility(0);
                            this.emptyView.setAlpha(0.0f);
                            if (this.emptyViewAnimationType == 1) {
                                this.emptyView.setScaleX(0.7f);
                                this.emptyView.setScaleY(0.7f);
                            }
                        }
                        this.emptyView.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(150).start();
                    } else if (this.emptyView.getVisibility() != 8) {
                        ViewPropertyAnimator animator = this.emptyView.animate().alpha(0.0f);
                        if (this.emptyViewAnimationType == 1) {
                            animator.scaleY(0.7f).scaleX(0.7f);
                        }
                        animator.setDuration(150).setListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                if (RecyclerListView.this.emptyView != null) {
                                    RecyclerListView.this.emptyView.setVisibility(8);
                                }
                            }
                        }).start();
                    }
                }
                if (this.hideIfEmpty) {
                    if (emptyViewVisible) {
                        i = 4;
                    }
                    int newVisibility2 = i;
                    if (getVisibility() != newVisibility2) {
                        setVisibility(newVisibility2);
                    }
                    this.hiddenByEmptyView = true;
                }
            } else if (this.hiddenByEmptyView && getVisibility() != 0) {
                setVisibility(0);
                this.hiddenByEmptyView = false;
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean emptyViewIsVisible() {
        if (getAdapter() == null || isFastScrollAnimationRunning() || getAdapter().getItemCount() != 0) {
            return false;
        }
        return true;
    }

    public void hide() {
        if (!this.isHidden) {
            this.isHidden = true;
            if (getVisibility() != 8) {
                setVisibility(8);
            }
            View view = this.emptyView;
            if (view != null && view.getVisibility() != 8) {
                this.emptyView.setVisibility(8);
            }
        }
    }

    public void show() {
        if (this.isHidden) {
            this.isHidden = false;
            checkIfEmpty(false);
        }
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility != 0) {
            this.hiddenByEmptyView = false;
        }
    }

    public void setOnScrollListener(RecyclerView.OnScrollListener listener) {
        this.onScrollListener = listener;
    }

    public void setHideIfEmpty(boolean value) {
        this.hideIfEmpty = value;
    }

    public RecyclerView.OnScrollListener getOnScrollListener() {
        return this.onScrollListener;
    }

    public void setOnInterceptTouchListener(OnInterceptTouchListener listener) {
        this.onInterceptTouchListener = listener;
    }

    public void setInstantClick(boolean value) {
        this.instantClick = value;
    }

    public void setDisallowInterceptTouchEvents(boolean value) {
        this.disallowInterceptTouchEvents = value;
    }

    public void setFastScrollEnabled(int type) {
        this.fastScroll = new FastScroll(getContext(), type);
        if (getParent() != null) {
            ((ViewGroup) getParent()).addView(this.fastScroll);
        }
    }

    public void setFastScrollVisible(boolean value) {
        FastScroll fastScroll2 = this.fastScroll;
        if (fastScroll2 != null) {
            fastScroll2.setVisibility(value ? 0 : 8);
        }
    }

    public void setSectionsType(int type) {
        this.sectionsType = type;
        if (type == 1) {
            this.headers = new ArrayList<>();
            this.headersCache = new ArrayList<>();
        }
    }

    public void setPinnedSectionOffsetY(int offset) {
        this.sectionOffset = offset;
        invalidate();
    }

    /* access modifiers changed from: private */
    public void positionSelector(int position, View sel) {
        positionSelector(position, sel, false, -1.0f, -1.0f);
    }

    private void positionSelector(int position, View sel, boolean manageHotspot, float x, float y) {
        int bottomPadding;
        Runnable runnable = this.removeHighlighSelectionRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.removeHighlighSelectionRunnable = null;
            this.pendingHighlightPosition = null;
        }
        if (this.selectorDrawable != null) {
            boolean positionChanged = position != this.selectorPosition;
            if (getAdapter() instanceof SelectionAdapter) {
                bottomPadding = ((SelectionAdapter) getAdapter()).getSelectionBottomPadding(sel);
            } else {
                bottomPadding = 0;
            }
            if (position != -1) {
                this.selectorPosition = position;
            }
            if (this.topBottomSelectorRadius > 0 && getAdapter() != null) {
                Theme.setMaskDrawableRad(this.selectorDrawable, position == 0 ? this.topBottomSelectorRadius : 0, position == getAdapter().getItemCount() + -2 ? this.topBottomSelectorRadius : 0);
            }
            this.selectorRect.set(sel.getLeft(), sel.getTop(), sel.getRight(), sel.getBottom() - bottomPadding);
            boolean enabled = sel.isEnabled();
            if (this.isChildViewEnabled != enabled) {
                this.isChildViewEnabled = enabled;
            }
            if (positionChanged) {
                this.selectorDrawable.setVisible(false, false);
                this.selectorDrawable.setState(StateSet.NOTHING);
            }
            this.selectorDrawable.setBounds(this.selectorRect);
            if (positionChanged && getVisibility() == 0) {
                this.selectorDrawable.setVisible(true, false);
            }
            if (Build.VERSION.SDK_INT >= 21 && manageHotspot) {
                this.selectorDrawable.setHotspot(x, y);
            }
        }
    }

    public void setAllowItemsInteractionDuringAnimation(boolean value) {
        this.allowItemsInteractionDuringAnimation = value;
    }

    public void hideSelector(boolean animated) {
        View view = this.currentChildView;
        if (view != null) {
            View child = this.currentChildView;
            onChildPressed(view, 0.0f, 0.0f, false);
            this.currentChildView = null;
            if (animated) {
                removeSelection(child, (MotionEvent) null);
            }
        }
        if (!animated) {
            this.selectorDrawable.setState(StateSet.NOTHING);
            this.selectorRect.setEmpty();
        }
    }

    /* access modifiers changed from: private */
    public void updateSelectorState() {
        Drawable drawable = this.selectorDrawable;
        if (drawable != null && drawable.isStateful()) {
            if (this.currentChildView != null) {
                if (this.selectorDrawable.setState(getDrawableStateForSelector())) {
                    invalidateDrawable(this.selectorDrawable);
                }
            } else if (this.removeHighlighSelectionRunnable == null) {
                this.selectorDrawable.setState(StateSet.NOTHING);
            }
        }
    }

    private int[] getDrawableStateForSelector() {
        int[] state = onCreateDrawableState(1);
        state[state.length - 1] = 16842919;
        return state;
    }

    public void onChildAttachedToWindow(View child) {
        if (getAdapter() instanceof SelectionAdapter) {
            RecyclerView.ViewHolder holder = findContainingViewHolder(child);
            if (holder != null) {
                child.setEnabled(((SelectionAdapter) getAdapter()).isEnabled(holder));
            }
        } else {
            child.setEnabled(false);
        }
        super.onChildAttachedToWindow(child);
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        updateSelectorState();
    }

    public boolean verifyDrawable(Drawable drawable) {
        return this.selectorDrawable == drawable || super.verifyDrawable(drawable);
    }

    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.selectorDrawable;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        FastScroll fastScroll2 = this.fastScroll;
        if (fastScroll2 != null && fastScroll2.getParent() != getParent()) {
            ViewGroup parent = (ViewGroup) this.fastScroll.getParent();
            if (parent != null) {
                parent.removeView(this.fastScroll);
            }
            ((ViewGroup) getParent()).addView(this.fastScroll);
        }
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        RecyclerView.Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(this.observer);
        }
        ArrayList<View> arrayList = this.headers;
        if (arrayList != null) {
            arrayList.clear();
            this.headersCache.clear();
        }
        this.currentFirst = -1;
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
        checkIfEmpty(false);
    }

    public void stopScroll() {
        try {
            super.stopScroll();
        } catch (NullPointerException e) {
        }
    }

    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow, int type) {
        if (!this.longPressCalled) {
            return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
        }
        OnItemLongClickListenerExtended onItemLongClickListenerExtended2 = this.onItemLongClickListenerExtended;
        if (onItemLongClickListenerExtended2 != null) {
            onItemLongClickListenerExtended2.onMove((float) dx, (float) dy);
        }
        consumed[0] = dx;
        consumed[1] = dy;
        return true;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    private View getSectionHeaderView(int section, View oldView) {
        boolean shouldLayout = oldView == null;
        View view = this.sectionsAdapter.getSectionHeaderView(section, oldView);
        if (shouldLayout) {
            ensurePinnedHeaderLayout(view, false);
        }
        return view;
    }

    private void ensurePinnedHeaderLayout(View header, boolean forceLayout) {
        if (header != null) {
            if (header.isLayoutRequested() || forceLayout) {
                int i = this.sectionsType;
                if (i == 1) {
                    ViewGroup.LayoutParams layoutParams = header.getLayoutParams();
                    try {
                        header.measure(View.MeasureSpec.makeMeasureSpec(layoutParams.width, NUM), View.MeasureSpec.makeMeasureSpec(layoutParams.height, NUM));
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                } else if (i == 2) {
                    try {
                        header.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                }
                header.layout(0, 0, header.getMeasuredWidth(), header.getMeasuredHeight());
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        View view;
        super.onSizeChanged(w, h, oldw, oldh);
        FrameLayout frameLayout = this.overlayContainer;
        if (frameLayout != null) {
            frameLayout.requestLayout();
        }
        int i = this.sectionsType;
        if (i == 1) {
            if (this.sectionsAdapter != null && !this.headers.isEmpty()) {
                for (int a = 0; a < this.headers.size(); a++) {
                    ensurePinnedHeaderLayout(this.headers.get(a), true);
                }
            }
        } else if (i == 2 && this.sectionsAdapter != null && (view = this.pinnedHeader) != null) {
            ensurePinnedHeaderLayout(view, true);
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        View view;
        RecyclerItemsEnterAnimator recyclerItemsEnterAnimator = this.itemsEnterAnimator;
        if (recyclerItemsEnterAnimator != null) {
            recyclerItemsEnterAnimator.dispatchDraw();
        }
        if (this.drawSelectorBehind && !this.selectorRect.isEmpty()) {
            this.selectorDrawable.setBounds(this.selectorRect);
            this.selectorDrawable.draw(canvas);
        }
        super.dispatchDraw(canvas);
        if (!this.drawSelectorBehind && !this.selectorRect.isEmpty()) {
            this.selectorDrawable.setBounds(this.selectorRect);
            this.selectorDrawable.draw(canvas);
        }
        FrameLayout frameLayout = this.overlayContainer;
        if (frameLayout != null) {
            frameLayout.draw(canvas);
        }
        int i = this.sectionsType;
        float f = 0.0f;
        if (i == 1) {
            if (this.sectionsAdapter != null && !this.headers.isEmpty()) {
                for (int a = 0; a < this.headers.size(); a++) {
                    View header = this.headers.get(a);
                    int saveCount = canvas.save();
                    canvas.translate(LocaleController.isRTL ? (float) (getWidth() - header.getWidth()) : 0.0f, (float) ((Integer) header.getTag()).intValue());
                    canvas.clipRect(0, 0, getWidth(), header.getMeasuredHeight());
                    header.draw(canvas);
                    canvas.restoreToCount(saveCount);
                }
            }
        } else if (i == 2 && this.sectionsAdapter != null && (view = this.pinnedHeader) != null && view.getAlpha() != 0.0f) {
            int saveCount2 = canvas.save();
            int top = ((Integer) this.pinnedHeader.getTag()).intValue();
            if (LocaleController.isRTL) {
                f = (float) (getWidth() - this.pinnedHeader.getWidth());
            }
            canvas.translate(f, (float) top);
            Drawable drawable = this.pinnedHeaderShadowDrawable;
            if (drawable != null) {
                drawable.setBounds(0, this.pinnedHeader.getMeasuredHeight(), getWidth(), this.pinnedHeader.getMeasuredHeight() + this.pinnedHeaderShadowDrawable.getIntrinsicHeight());
                this.pinnedHeaderShadowDrawable.setAlpha((int) (this.pinnedHeaderShadowAlpha * 255.0f));
                this.pinnedHeaderShadowDrawable.draw(canvas);
                long newTime = SystemClock.elapsedRealtime();
                long dt = Math.min(20, newTime - this.lastAlphaAnimationTime);
                this.lastAlphaAnimationTime = newTime;
                float f2 = this.pinnedHeaderShadowAlpha;
                float f3 = this.pinnedHeaderShadowTargetAlpha;
                if (f2 < f3) {
                    float f4 = f2 + (((float) dt) / 180.0f);
                    this.pinnedHeaderShadowAlpha = f4;
                    if (f4 > f3) {
                        this.pinnedHeaderShadowAlpha = f3;
                    }
                    invalidate();
                } else if (f2 > f3) {
                    float f5 = f2 - (((float) dt) / 180.0f);
                    this.pinnedHeaderShadowAlpha = f5;
                    if (f5 < f3) {
                        this.pinnedHeaderShadowAlpha = f3;
                    }
                    invalidate();
                }
            }
            canvas.clipRect(0, 0, getWidth(), this.pinnedHeader.getMeasuredHeight());
            this.pinnedHeader.draw(canvas);
            canvas.restoreToCount(saveCount2);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.selectorPosition = -1;
        this.selectorRect.setEmpty();
        RecyclerItemsEnterAnimator recyclerItemsEnterAnimator = this.itemsEnterAnimator;
        if (recyclerItemsEnterAnimator != null) {
            recyclerItemsEnterAnimator.onDetached();
        }
    }

    public void addOverlayView(View view, FrameLayout.LayoutParams layoutParams) {
        if (this.overlayContainer == null) {
            this.overlayContainer = new FrameLayout(getContext()) {
                public void requestLayout() {
                    super.requestLayout();
                    try {
                        measure(View.MeasureSpec.makeMeasureSpec(RecyclerListView.this.getMeasuredWidth(), NUM), View.MeasureSpec.makeMeasureSpec(RecyclerListView.this.getMeasuredHeight(), NUM));
                        layout(0, 0, RecyclerListView.this.overlayContainer.getMeasuredWidth(), RecyclerListView.this.overlayContainer.getMeasuredHeight());
                    } catch (Exception e) {
                    }
                }
            };
        }
        this.overlayContainer.addView(view, layoutParams);
    }

    public void removeOverlayView(View view) {
        FrameLayout frameLayout = this.overlayContainer;
        if (frameLayout != null) {
            frameLayout.removeView(view);
        }
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

    public boolean isFastScrollAnimationRunning() {
        return this.fastScrollAnimationRunning;
    }

    public void requestLayout() {
        if (!this.fastScrollAnimationRunning) {
            super.requestLayout();
        }
    }

    public void setAnimateEmptyView(boolean animate, int emptyViewAnimationType2) {
        this.animateEmptyView = animate;
        this.emptyViewAnimationType = emptyViewAnimationType2;
    }

    public static class FoucsableOnTouchListener implements View.OnTouchListener {
        private boolean onFocus;
        private float x;
        private float y;

        public boolean onTouch(View v, MotionEvent event) {
            ViewParent parent = v.getParent();
            if (parent == null) {
                return false;
            }
            if (event.getAction() == 0) {
                this.x = event.getX();
                this.y = event.getY();
                this.onFocus = true;
                parent.requestDisallowInterceptTouchEvent(true);
            }
            if (event.getAction() == 2) {
                float dx = this.x - event.getX();
                float dy = this.y - event.getY();
                float touchSlop = (float) ViewConfiguration.get(v.getContext()).getScaledTouchSlop();
                if (this.onFocus && Math.sqrt((double) ((dx * dx) + (dy * dy))) > ((double) touchSlop)) {
                    this.onFocus = false;
                    parent.requestDisallowInterceptTouchEvent(false);
                }
            } else if (event.getAction() == 1 || event.getAction() == 3) {
                this.onFocus = false;
                parent.requestDisallowInterceptTouchEvent(false);
            }
            return false;
        }
    }

    public void setTranslationY(float translationY) {
        super.setTranslationY(translationY);
        FastScroll fastScroll2 = this.fastScroll;
        if (fastScroll2 != null) {
            fastScroll2.setTranslationY(translationY);
        }
    }

    public void startMultiselect(int positionFrom, boolean useRelativePositions2, onMultiSelectionChanged multiSelectionListener2) {
        if (!this.multiSelectionGesture) {
            this.listPaddings = new int[2];
            this.selectedPositions = new HashSet<>();
            getParent().requestDisallowInterceptTouchEvent(true);
            this.multiSelectionListener = multiSelectionListener2;
            this.multiSelectionGesture = true;
            this.currentSelectedPosition = positionFrom;
            this.startSelectionFrom = positionFrom;
        }
        this.useRelativePositions = useRelativePositions2;
    }

    public boolean onTouchEvent(MotionEvent e) {
        FastScroll fastScroll2 = this.fastScroll;
        if (fastScroll2 != null && fastScroll2.pressed) {
            return false;
        }
        if (!this.multiSelectionGesture || e.getAction() == 0 || e.getAction() == 1 || e.getAction() == 3) {
            this.lastX = Float.MAX_VALUE;
            this.lastY = Float.MAX_VALUE;
            this.multiSelectionGesture = false;
            this.multiSelectionGestureStarted = false;
            getParent().requestDisallowInterceptTouchEvent(false);
            cancelMultiselectScroll();
            return super.onTouchEvent(e);
        }
        if (this.lastX == Float.MAX_VALUE && this.lastY == Float.MAX_VALUE) {
            this.lastX = e.getX();
            this.lastY = e.getY();
        }
        if (!this.multiSelectionGestureStarted && Math.abs(e.getY() - this.lastY) > ((float) this.touchSlop)) {
            this.multiSelectionGestureStarted = true;
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        if (this.multiSelectionGestureStarted) {
            chekMultiselect(e.getX(), e.getY());
            this.multiSelectionListener.getPaddings(this.listPaddings);
            if (e.getY() > ((float) ((getMeasuredHeight() - AndroidUtilities.dp(56.0f)) - this.listPaddings[1])) && (this.currentSelectedPosition >= this.startSelectionFrom || !this.multiSelectionListener.limitReached())) {
                startMultiselectScroll(false);
            } else if (e.getY() >= ((float) (AndroidUtilities.dp(56.0f) + this.listPaddings[0])) || (this.currentSelectedPosition > this.startSelectionFrom && this.multiSelectionListener.limitReached())) {
                cancelMultiselectScroll();
            } else {
                startMultiselectScroll(true);
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public boolean chekMultiselect(float x, float y) {
        int measuredHeight = getMeasuredHeight();
        int[] iArr = this.listPaddings;
        float y2 = Math.min((float) (measuredHeight - iArr[1]), Math.max(y, (float) iArr[0]));
        float x2 = Math.min((float) getMeasuredWidth(), Math.max(x, 0.0f));
        int i = 0;
        while (true) {
            if (i >= getChildCount()) {
                break;
            }
            this.multiSelectionListener.getPaddings(this.listPaddings);
            if (!this.useRelativePositions) {
                View child = getChildAt(i);
                AndroidUtilities.rectTmp.set((float) child.getLeft(), (float) child.getTop(), (float) (child.getLeft() + child.getMeasuredWidth()), (float) (child.getTop() + child.getMeasuredHeight()));
                if (AndroidUtilities.rectTmp.contains(x2, y2)) {
                    int position = getChildLayoutPosition(child);
                    int i2 = this.currentSelectedPosition;
                    if (i2 != position) {
                        int i3 = this.startSelectionFrom;
                        boolean selectionFromTop = i2 > i3 || position > i3;
                        position = this.multiSelectionListener.checkPosition(position, selectionFromTop);
                        if (selectionFromTop) {
                            if (position <= this.currentSelectedPosition) {
                                for (int k = this.currentSelectedPosition; k > position; k--) {
                                    if (k != this.startSelectionFrom && this.multiSelectionListener.canSelect(k)) {
                                        this.multiSelectionListener.onSelectionChanged(k, false, x2, y2);
                                    }
                                }
                            } else if (!this.multiSelectionListener.limitReached()) {
                                for (int k2 = this.currentSelectedPosition + 1; k2 <= position; k2++) {
                                    if (k2 != this.startSelectionFrom && this.multiSelectionListener.canSelect(k2)) {
                                        this.multiSelectionListener.onSelectionChanged(k2, true, x2, y2);
                                    }
                                }
                            }
                        } else if (position > this.currentSelectedPosition) {
                            for (int k3 = this.currentSelectedPosition; k3 < position; k3++) {
                                if (k3 != this.startSelectionFrom && this.multiSelectionListener.canSelect(k3)) {
                                    this.multiSelectionListener.onSelectionChanged(k3, false, x2, y2);
                                }
                            }
                        } else if (!this.multiSelectionListener.limitReached()) {
                            for (int k4 = this.currentSelectedPosition - 1; k4 >= position; k4--) {
                                if (k4 != this.startSelectionFrom && this.multiSelectionListener.canSelect(k4)) {
                                    this.multiSelectionListener.onSelectionChanged(k4, true, x2, y2);
                                }
                            }
                        }
                    }
                    if (!this.multiSelectionListener.limitReached()) {
                        this.currentSelectedPosition = position;
                    }
                }
            }
            i++;
        }
        return true;
    }

    private void cancelMultiselectScroll() {
        this.multiselectScrollRunning = false;
        AndroidUtilities.cancelRunOnUIThread(this.scroller);
    }

    private void startMultiselectScroll(boolean top) {
        this.multiselectScrollToTop = top;
        if (!this.multiselectScrollRunning) {
            this.multiselectScrollRunning = true;
            AndroidUtilities.cancelRunOnUIThread(this.scroller);
            AndroidUtilities.runOnUIThread(this.scroller);
        }
    }

    public boolean isMultiselect() {
        return this.multiSelectionGesture;
    }

    /* access modifiers changed from: protected */
    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    /* access modifiers changed from: protected */
    public Drawable getThemedDrawable(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Drawable drawable = resourcesProvider2 != null ? resourcesProvider2.getDrawable(key) : null;
        return drawable != null ? drawable : Theme.getThemeDrawable(key);
    }

    /* access modifiers changed from: protected */
    public Paint getThemedPaint(String paintKey) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Paint paint = resourcesProvider2 != null ? resourcesProvider2.getPaint(paintKey) : null;
        return paint != null ? paint : Theme.getThemePaint(paintKey);
    }

    public void setItemsEnterAnimator(RecyclerItemsEnterAnimator itemsEnterAnimator2) {
        this.itemsEnterAnimator = itemsEnterAnimator2;
    }
}
