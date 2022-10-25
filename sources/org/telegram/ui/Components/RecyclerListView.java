package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.SparseIntArray;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.util.Consumer;
import androidx.core.util.ObjectsCompat$$ExternalSyntheticBackport0;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.GestureDetectorFixDoubleTap;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes3.dex */
public class RecyclerListView extends RecyclerView {
    private static int[] attributes;
    private static boolean gotAttributes;
    private View.AccessibilityDelegate accessibilityDelegate;
    private boolean accessibilityEnabled;
    private boolean allowItemsInteractionDuringAnimation;
    private boolean animateEmptyView;
    private Runnable clickRunnable;
    private int currentChildPosition;
    private View currentChildView;
    private int currentFirst;
    int currentSelectedPosition;
    private int currentVisible;
    private boolean disableHighlightState;
    private boolean disallowInterceptTouchEvents;
    private boolean drawSelectorBehind;
    private View emptyView;
    int emptyViewAnimateToVisibility;
    private int emptyViewAnimationType;
    private FastScroll fastScroll;
    public boolean fastScrollAnimationRunning;
    private GestureDetectorFixDoubleTap gestureDetector;
    private ArrayList<View> headers;
    private ArrayList<View> headersCache;
    private boolean hiddenByEmptyView;
    private boolean hideIfEmpty;
    private boolean instantClick;
    private boolean interceptedByChild;
    private boolean isChildViewEnabled;
    private boolean isHidden;
    RecyclerItemsEnterAnimator itemsEnterAnimator;
    private long lastAlphaAnimationTime;
    float lastX;
    float lastY;
    int[] listPaddings;
    private boolean longPressCalled;
    boolean multiSelectionGesture;
    boolean multiSelectionGestureStarted;
    onMultiSelectionChanged multiSelectionListener;
    boolean multiselectScrollRunning;
    boolean multiselectScrollToTop;
    private RecyclerView.AdapterDataObserver observer;
    private OnInterceptTouchListener onInterceptTouchListener;
    private OnItemClickListener onItemClickListener;
    private OnItemClickListenerExtended onItemClickListenerExtended;
    private OnItemLongClickListener onItemLongClickListener;
    private OnItemLongClickListenerExtended onItemLongClickListenerExtended;
    private RecyclerView.OnScrollListener onScrollListener;
    private FrameLayout overlayContainer;
    private IntReturnCallback pendingHighlightPosition;
    private View pinnedHeader;
    private float pinnedHeaderShadowAlpha;
    private Drawable pinnedHeaderShadowDrawable;
    private float pinnedHeaderShadowTargetAlpha;
    private Runnable removeHighlighSelectionRunnable;
    private boolean resetSelectorOnChanged;
    protected final Theme.ResourcesProvider resourcesProvider;
    private boolean scrollEnabled;
    public boolean scrolledByUserOnce;
    Runnable scroller;
    public boolean scrollingByUser;
    private int sectionOffset;
    private SectionsAdapter sectionsAdapter;
    private int sectionsCount;
    private int sectionsType;
    private Runnable selectChildRunnable;
    protected Drawable selectorDrawable;
    protected int selectorPosition;
    private int selectorRadius;
    protected android.graphics.Rect selectorRect;
    protected Consumer<Canvas> selectorTransformer;
    private int selectorType;
    protected View selectorView;
    private boolean selfOnLayout;
    private int startSection;
    int startSelectionFrom;
    private int topBottomSelectorRadius;
    private int touchSlop;
    boolean useRelativePositions;

    /* loaded from: classes3.dex */
    public interface IntReturnCallback {
        int run();
    }

    /* loaded from: classes3.dex */
    public interface OnInterceptTouchListener {
        boolean onInterceptTouchEvent(MotionEvent motionEvent);
    }

    /* loaded from: classes3.dex */
    public interface OnItemClickListener {
        void onItemClick(View view, int i);
    }

    /* loaded from: classes3.dex */
    public interface OnItemClickListenerExtended {

        /* renamed from: org.telegram.ui.Components.RecyclerListView$OnItemClickListenerExtended$-CC  reason: invalid class name */
        /* loaded from: classes3.dex */
        public final /* synthetic */ class CC {
            public static boolean $default$hasDoubleTap(OnItemClickListenerExtended onItemClickListenerExtended, View view, int i) {
                return false;
            }

            public static void $default$onDoubleTap(OnItemClickListenerExtended onItemClickListenerExtended, View view, int i, float f, float f2) {
            }
        }

        boolean hasDoubleTap(View view, int i);

        void onDoubleTap(View view, int i, float f, float f2);

        void onItemClick(View view, int i, float f, float f2);
    }

    /* loaded from: classes3.dex */
    public interface OnItemLongClickListener {
        boolean onItemClick(View view, int i);
    }

    /* loaded from: classes3.dex */
    public interface OnItemLongClickListenerExtended {

        /* renamed from: org.telegram.ui.Components.RecyclerListView$OnItemLongClickListenerExtended$-CC  reason: invalid class name */
        /* loaded from: classes3.dex */
        public final /* synthetic */ class CC {
            public static void $default$onLongClickRelease(OnItemLongClickListenerExtended onItemLongClickListenerExtended) {
            }

            public static void $default$onMove(OnItemLongClickListenerExtended onItemLongClickListenerExtended, float f, float f2) {
            }
        }

        boolean onItemClick(View view, int i, float f, float f2);

        void onLongClickRelease();

        void onMove(float f, float f2);
    }

    /* loaded from: classes3.dex */
    public static abstract class SelectionAdapter extends RecyclerView.Adapter {
        public int getSelectionBottomPadding(View view) {
            return 0;
        }

        public abstract boolean isEnabled(RecyclerView.ViewHolder viewHolder);
    }

    /* loaded from: classes3.dex */
    public interface onMultiSelectionChanged {
        boolean canSelect(int i);

        int checkPosition(int i, boolean z);

        void getPaddings(int[] iArr);

        boolean limitReached();

        void onSelectionChanged(int i, boolean z, float f, float f2);

        void scrollBy(int i);
    }

    protected boolean allowSelectChildAtPosition(float f, float f2) {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean allowSelectChildAtPosition(View view) {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean canHighlightChildAt(View view, float f, float f2) {
        return true;
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    public void setSelectorTransformer(Consumer<Canvas> consumer) {
        this.selectorTransformer = consumer;
    }

    public FastScroll getFastScroll() {
        return this.fastScroll;
    }

    /* loaded from: classes3.dex */
    public static abstract class FastScrollAdapter extends SelectionAdapter {
        public boolean fastScrollIsVisible(RecyclerListView recyclerListView) {
            return true;
        }

        public abstract String getLetter(int i);

        public abstract void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr);

        public void onFastScrollSingleTap() {
        }

        public void onFinishFastScroll(RecyclerListView recyclerListView) {
        }

        public void onStartFastScroll() {
        }

        public int getTotalItemsCount() {
            return getItemCount();
        }

        public float getScrollProgress(RecyclerListView recyclerListView) {
            return recyclerListView.computeVerticalScrollOffset() / ((getTotalItemsCount() * recyclerListView.getChildAt(0).getMeasuredHeight()) - recyclerListView.getMeasuredHeight());
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class SectionsAdapter extends FastScrollAdapter {
        private int count;
        private ArrayList<Integer> hashes = new ArrayList<>();
        private SparseIntArray sectionCache;
        private int sectionCount;
        private SparseIntArray sectionCountCache;
        private SparseIntArray sectionPositionCache;

        public abstract int getCountForSection(int i);

        /* renamed from: getItem */
        public abstract Object mo1775getItem(int i, int i2);

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

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            update(false);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return isEnabled(viewHolder, getSectionForPosition(adapterPosition), getPositionInSectionForPosition(adapterPosition));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            int i = this.count;
            if (i >= 0) {
                return i;
            }
            this.count = 0;
            int internalGetSectionCount = internalGetSectionCount();
            for (int i2 = 0; i2 < internalGetSectionCount; i2++) {
                this.count += internalGetCountForSection(i2);
            }
            return this.count;
        }

        public final Object getItem(int i) {
            return mo1775getItem(getSectionForPosition(i), getPositionInSectionForPosition(i));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public final int getItemViewType(int i) {
            return getItemViewType(getSectionForPosition(i), getPositionInSectionForPosition(i));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public final void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            onBindViewHolder(getSectionForPosition(i), getPositionInSectionForPosition(i), viewHolder);
        }

        private int internalGetCountForSection(int i) {
            int i2 = this.sectionCountCache.get(i, Integer.MAX_VALUE);
            if (i2 != Integer.MAX_VALUE) {
                return i2;
            }
            int countForSection = getCountForSection(i);
            this.sectionCountCache.put(i, countForSection);
            return countForSection;
        }

        private int internalGetSectionCount() {
            int i = this.sectionCount;
            if (i >= 0) {
                return i;
            }
            int sectionCount = getSectionCount();
            this.sectionCount = sectionCount;
            return sectionCount;
        }

        public final int getSectionForPosition(int i) {
            int i2 = this.sectionCache.get(i, Integer.MAX_VALUE);
            if (i2 != Integer.MAX_VALUE) {
                return i2;
            }
            int internalGetSectionCount = internalGetSectionCount();
            int i3 = 0;
            int i4 = 0;
            while (i3 < internalGetSectionCount) {
                int internalGetCountForSection = internalGetCountForSection(i3) + i4;
                if (i >= i4 && i < internalGetCountForSection) {
                    this.sectionCache.put(i, i3);
                    return i3;
                }
                i3++;
                i4 = internalGetCountForSection;
            }
            return -1;
        }

        public int getPositionInSectionForPosition(int i) {
            int i2 = this.sectionPositionCache.get(i, Integer.MAX_VALUE);
            if (i2 != Integer.MAX_VALUE) {
                return i2;
            }
            int internalGetSectionCount = internalGetSectionCount();
            int i3 = 0;
            int i4 = 0;
            while (i3 < internalGetSectionCount) {
                int internalGetCountForSection = internalGetCountForSection(i3) + i4;
                if (i >= i4 && i < internalGetCountForSection) {
                    int i5 = i - i4;
                    this.sectionPositionCache.put(i, i5);
                    return i5;
                }
                i3++;
                i4 = internalGetCountForSection;
            }
            return -1;
        }

        public void update(boolean z) {
            cleanupCache();
            final ArrayList arrayList = new ArrayList(this.hashes);
            this.hashes.clear();
            int internalGetSectionCount = internalGetSectionCount();
            for (int i = 0; i < internalGetSectionCount; i++) {
                int internalGetCountForSection = internalGetCountForSection(i);
                for (int i2 = 0; i2 < internalGetCountForSection; i2++) {
                    this.hashes.add(Integer.valueOf(Arrays.hashCode(new Object[]{Integer.valueOf((-49612) * i), mo1775getItem(i, i2)})));
                }
            }
            if (z) {
                DiffUtil.calculateDiff(new DiffUtil.Callback() { // from class: org.telegram.ui.Components.RecyclerListView.SectionsAdapter.1
                    @Override // androidx.recyclerview.widget.DiffUtil.Callback
                    public int getOldListSize() {
                        return arrayList.size();
                    }

                    @Override // androidx.recyclerview.widget.DiffUtil.Callback
                    public int getNewListSize() {
                        return SectionsAdapter.this.hashes.size();
                    }

                    @Override // androidx.recyclerview.widget.DiffUtil.Callback
                    public boolean areItemsTheSame(int i3, int i4) {
                        return ObjectsCompat$$ExternalSyntheticBackport0.m(arrayList.get(i3), SectionsAdapter.this.hashes.get(i4));
                    }

                    @Override // androidx.recyclerview.widget.DiffUtil.Callback
                    public boolean areContentsTheSame(int i3, int i4) {
                        return areItemsTheSame(i3, i4);
                    }
                }, true).dispatchUpdatesTo(this);
            } else {
                super.notifyDataSetChanged();
            }
        }
    }

    /* loaded from: classes3.dex */
    public static class Holder extends RecyclerView.ViewHolder {
        public Holder(View view) {
            super(view);
        }
    }

    /* loaded from: classes3.dex */
    public class FastScroll extends View {
        private int activeColor;
        private Path arrowPath;
        private float bubbleProgress;
        private String currentLetter;
        Drawable fastScrollBackgroundDrawable;
        Drawable fastScrollShadowDrawable;
        private float floatingDateProgress;
        private boolean floatingDateVisible;
        private boolean fromTop;
        private float fromWidth;
        Runnable hideFloatingDateRunnable;
        private StaticLayout inLetterLayout;
        private int inactiveColor;
        boolean isMoving;
        boolean isRtl;
        boolean isVisible;
        private float lastLetterY;
        private long lastUpdateTime;
        private float lastY;
        private StaticLayout letterLayout;
        private TextPaint letterPaint;
        private StaticLayout oldLetterLayout;
        private StaticLayout outLetterLayout;
        private Paint paint;
        private Paint paint2;
        private Path path;
        private int[] positionWithOffset;
        private boolean pressed;
        private float progress;
        private float[] radii;
        private RectF rect;
        private float replaceLayoutProgress;
        private int scrollX;
        private StaticLayout stableLetterLayout;
        private float startDy;
        long startTime;
        float startY;
        private float textX;
        private float textY;
        float touchSlop;
        private int type;
        float viewAlpha;
        float visibilityAlpha;

        public FastScroll(Context context, int i) {
            super(context);
            this.rect = new RectF();
            this.paint = new Paint(1);
            this.paint2 = new Paint(1);
            this.replaceLayoutProgress = 1.0f;
            this.letterPaint = new TextPaint(1);
            this.path = new Path();
            this.arrowPath = new Path();
            this.radii = new float[8];
            this.positionWithOffset = new int[2];
            this.hideFloatingDateRunnable = new Runnable() { // from class: org.telegram.ui.Components.RecyclerListView.FastScroll.1
                @Override // java.lang.Runnable
                public void run() {
                    if (!FastScroll.this.pressed) {
                        FastScroll.this.floatingDateVisible = false;
                        FastScroll.this.invalidate();
                        return;
                    }
                    AndroidUtilities.cancelRunOnUIThread(FastScroll.this.hideFloatingDateRunnable);
                    AndroidUtilities.runOnUIThread(FastScroll.this.hideFloatingDateRunnable, 4000L);
                }
            };
            this.type = i;
            if (i == 0) {
                this.letterPaint.setTextSize(AndroidUtilities.dp(45.0f));
                this.isRtl = LocaleController.isRTL;
            } else {
                this.isRtl = false;
                this.letterPaint.setTextSize(AndroidUtilities.dp(13.0f));
                this.letterPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.paint2.setColor(Theme.getColor("windowBackgroundWhite"));
                Drawable mutate = ContextCompat.getDrawable(context, R.drawable.calendar_date).mutate();
                this.fastScrollBackgroundDrawable = mutate;
                mutate.setColorFilter(new PorterDuffColorFilter(ColorUtils.blendARGB(Theme.getColor("windowBackgroundWhite"), -1, 0.1f), PorterDuff.Mode.MULTIPLY));
            }
            for (int i2 = 0; i2 < 8; i2++) {
                this.radii[i2] = AndroidUtilities.dp(44.0f);
            }
            this.scrollX = AndroidUtilities.dp(this.isRtl ? 10.0f : (i == 0 ? 132 : 240) - 15);
            updateColors();
            setFocusableInTouchMode(true);
            this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
            this.fastScrollShadowDrawable = ContextCompat.getDrawable(context, R.drawable.fast_scroll_shadow);
        }

        /* JADX INFO: Access modifiers changed from: private */
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

        /* JADX WARN: Code restructure failed: missing block: B:79:0x015a, code lost:
            if (r0 <= (org.telegram.messenger.AndroidUtilities.dp(30.0f) + r8)) goto L81;
         */
        @Override // android.view.View
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public boolean onTouchEvent(android.view.MotionEvent r8) {
            /*
                Method dump skipped, instructions count: 393
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.RecyclerListView.FastScroll.onTouchEvent(android.view.MotionEvent):boolean");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void getCurrentLetter(boolean z) {
            RecyclerView.LayoutManager layoutManager = RecyclerListView.this.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                boolean z2 = true;
                if (linearLayoutManager.getOrientation() != 1) {
                    return;
                }
                RecyclerView.Adapter adapter = RecyclerListView.this.getAdapter();
                if (!(adapter instanceof FastScrollAdapter)) {
                    return;
                }
                FastScrollAdapter fastScrollAdapter = (FastScrollAdapter) adapter;
                fastScrollAdapter.getPositionForScrollProgress(RecyclerListView.this, this.progress, this.positionWithOffset);
                if (z) {
                    int[] iArr = this.positionWithOffset;
                    linearLayoutManager.scrollToPositionWithOffset(iArr[0], (-iArr[1]) + RecyclerListView.this.sectionOffset);
                }
                String letter = fastScrollAdapter.getLetter(this.positionWithOffset[0]);
                if (letter == null) {
                    StaticLayout staticLayout = this.letterLayout;
                    if (staticLayout != null) {
                        this.oldLetterLayout = staticLayout;
                    }
                    this.letterLayout = null;
                } else if (letter.equals(this.currentLetter)) {
                } else {
                    this.currentLetter = letter;
                    if (this.type == 0) {
                        this.letterLayout = new StaticLayout(letter, this.letterPaint, 1000, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    } else {
                        this.outLetterLayout = this.letterLayout;
                        int measureText = ((int) this.letterPaint.measureText(letter)) + 1;
                        this.letterLayout = new StaticLayout(letter, this.letterPaint, measureText, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        if (this.outLetterLayout != null) {
                            String[] split = letter.split(" ");
                            String[] split2 = this.outLetterLayout.getText().toString().split(" ");
                            if (split != null && split2 != null && split.length == 2 && split2.length == 2 && split[1].equals(split2[1])) {
                                String charSequence = this.outLetterLayout.getText().toString();
                                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
                                spannableStringBuilder.setSpan(new EmptyStubSpan(), split2[0].length(), charSequence.length(), 0);
                                this.outLetterLayout = new StaticLayout(spannableStringBuilder, this.letterPaint, ((int) this.letterPaint.measureText(charSequence)) + 1, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                                SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(letter);
                                spannableStringBuilder2.setSpan(new EmptyStubSpan(), split[0].length(), letter.length(), 0);
                                this.inLetterLayout = new StaticLayout(spannableStringBuilder2, this.letterPaint, measureText, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                                SpannableStringBuilder spannableStringBuilder3 = new SpannableStringBuilder(letter);
                                spannableStringBuilder3.setSpan(new EmptyStubSpan(), 0, split[0].length(), 0);
                                this.stableLetterLayout = new StaticLayout(spannableStringBuilder3, this.letterPaint, measureText, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                            } else {
                                this.inLetterLayout = this.letterLayout;
                                this.stableLetterLayout = null;
                            }
                            this.fromWidth = this.outLetterLayout.getWidth();
                            this.replaceLayoutProgress = 0.0f;
                            if (getProgress() <= this.lastLetterY) {
                                z2 = false;
                            }
                            this.fromTop = z2;
                        }
                        this.lastLetterY = getProgress();
                    }
                    this.oldLetterLayout = null;
                    if (this.letterLayout.getLineCount() <= 0) {
                        return;
                    }
                    this.letterLayout.getLineWidth(0);
                    this.letterLayout.getLineLeft(0);
                    if (this.isRtl) {
                        this.textX = (AndroidUtilities.dp(10.0f) + ((AndroidUtilities.dp(88.0f) - this.letterLayout.getLineWidth(0)) / 2.0f)) - this.letterLayout.getLineLeft(0);
                    } else {
                        this.textX = ((AndroidUtilities.dp(88.0f) - this.letterLayout.getLineWidth(0)) / 2.0f) - this.letterLayout.getLineLeft(0);
                    }
                    this.textY = (AndroidUtilities.dp(88.0f) - this.letterLayout.getHeight()) / 2;
                }
            }
        }

        @Override // android.view.View
        protected void onMeasure(int i, int i2) {
            setMeasuredDimension(AndroidUtilities.dp(this.type == 0 ? 132.0f : 240.0f), View.MeasureSpec.getSize(i2));
            this.arrowPath.reset();
            this.arrowPath.setLastPoint(0.0f, 0.0f);
            this.arrowPath.lineTo(AndroidUtilities.dp(4.0f), -AndroidUtilities.dp(4.0f));
            this.arrowPath.lineTo(-AndroidUtilities.dp(4.0f), -AndroidUtilities.dp(4.0f));
            this.arrowPath.close();
        }

        /* JADX WARN: Code restructure failed: missing block: B:25:0x01ea, code lost:
            if (r14[6] == r8) goto L85;
         */
        /* JADX WARN: Code restructure failed: missing block: B:30:0x01fb, code lost:
            if (r14[4] == r8) goto L77;
         */
        /* JADX WARN: Removed duplicated region for block: B:32:0x01ff  */
        /* JADX WARN: Removed duplicated region for block: B:33:0x020b  */
        /* JADX WARN: Removed duplicated region for block: B:36:0x0223  */
        /* JADX WARN: Removed duplicated region for block: B:37:0x0229  */
        /* JADX WARN: Removed duplicated region for block: B:40:0x022e  */
        /* JADX WARN: Removed duplicated region for block: B:41:0x0231  */
        /* JADX WARN: Removed duplicated region for block: B:46:0x0257  */
        /* JADX WARN: Removed duplicated region for block: B:48:0x025b  */
        @Override // android.view.View
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        protected void onDraw(android.graphics.Canvas r19) {
            /*
                Method dump skipped, instructions count: 1335
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.RecyclerListView.FastScroll.onDraw(android.graphics.Canvas):void");
        }

        @Override // android.view.View
        public void layout(int i, int i2, int i3, int i4) {
            if (!RecyclerListView.this.selfOnLayout) {
                return;
            }
            super.layout(i, i2, i3, i4);
        }

        public void setProgress(float f) {
            this.progress = f;
            invalidate();
        }

        @Override // android.view.View
        public boolean isPressed() {
            return this.pressed;
        }

        public void showFloatingDate() {
            if (this.type != 1) {
                return;
            }
            if (!this.floatingDateVisible) {
                this.floatingDateVisible = true;
                invalidate();
            }
            AndroidUtilities.cancelRunOnUIThread(this.hideFloatingDateRunnable);
            AndroidUtilities.runOnUIThread(this.hideFloatingDateRunnable, 2000L);
        }

        public void setIsVisible(boolean z) {
            if (this.isVisible != z) {
                this.isVisible = z;
                float f = z ? 1.0f : 0.0f;
                this.visibilityAlpha = f;
                super.setAlpha(this.viewAlpha * f);
            }
        }

        public void setVisibilityAlpha(float f) {
            if (this.visibilityAlpha != f) {
                this.visibilityAlpha = f;
                super.setAlpha(this.viewAlpha * f);
            }
        }

        @Override // android.view.View
        public void setAlpha(float f) {
            if (this.viewAlpha != f) {
                this.viewAlpha = f;
                super.setAlpha(f * this.visibilityAlpha);
            }
        }

        @Override // android.view.View
        public float getAlpha() {
            return this.viewAlpha;
        }

        public int getScrollBarY() {
            return ((int) Math.ceil((getMeasuredHeight() - AndroidUtilities.dp(54.0f)) * this.progress)) + AndroidUtilities.dp(17.0f);
        }

        public float getProgress() {
            return this.progress;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class RecyclerListViewItemClickListener implements RecyclerView.OnItemTouchListener {
        @Override // androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
        public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        }

        public RecyclerListViewItemClickListener(Context context) {
            RecyclerListView.this.gestureDetector = new GestureDetectorFixDoubleTap(context, new GestureDetectorFixDoubleTap.OnGestureListener(RecyclerListView.this) { // from class: org.telegram.ui.Components.RecyclerListView.RecyclerListViewItemClickListener.1
                private View doubleTapView;

                @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
                public boolean onDown(MotionEvent motionEvent) {
                    return false;
                }

                @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    if (RecyclerListView.this.currentChildView != null) {
                        if (RecyclerListView.this.onItemClickListenerExtended == null || !RecyclerListView.this.onItemClickListenerExtended.hasDoubleTap(RecyclerListView.this.currentChildView, RecyclerListView.this.currentChildPosition)) {
                            onPressItem(RecyclerListView.this.currentChildView, motionEvent);
                        } else {
                            this.doubleTapView = RecyclerListView.this.currentChildView;
                        }
                    }
                    return false;
                }

                @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
                public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                    if (this.doubleTapView == null || RecyclerListView.this.onItemClickListenerExtended == null || !RecyclerListView.this.onItemClickListenerExtended.hasDoubleTap(this.doubleTapView, RecyclerListView.this.currentChildPosition)) {
                        return false;
                    }
                    onPressItem(this.doubleTapView, motionEvent);
                    this.doubleTapView = null;
                    return true;
                }

                @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
                public boolean onDoubleTap(MotionEvent motionEvent) {
                    if (this.doubleTapView == null || RecyclerListView.this.onItemClickListenerExtended == null || !RecyclerListView.this.onItemClickListenerExtended.hasDoubleTap(this.doubleTapView, RecyclerListView.this.currentChildPosition)) {
                        return false;
                    }
                    RecyclerListView.this.onItemClickListenerExtended.onDoubleTap(this.doubleTapView, RecyclerListView.this.currentChildPosition, motionEvent.getX(), motionEvent.getY());
                    this.doubleTapView = null;
                    return true;
                }

                private void onPressItem(final View view, MotionEvent motionEvent) {
                    if (view != null) {
                        if (RecyclerListView.this.onItemClickListener == null && RecyclerListView.this.onItemClickListenerExtended == null) {
                            return;
                        }
                        final float x = motionEvent.getX();
                        final float y = motionEvent.getY();
                        RecyclerListView.this.onChildPressed(view, x, y, true);
                        final int i = RecyclerListView.this.currentChildPosition;
                        if (RecyclerListView.this.instantClick && i != -1) {
                            view.playSoundEffect(0);
                            view.sendAccessibilityEvent(1);
                            if (RecyclerListView.this.onItemClickListener != null) {
                                RecyclerListView.this.onItemClickListener.onItemClick(view, i);
                            } else if (RecyclerListView.this.onItemClickListenerExtended != null) {
                                RecyclerListView.this.onItemClickListenerExtended.onItemClick(view, i, x - view.getX(), y - view.getY());
                            }
                        }
                        AndroidUtilities.runOnUIThread(RecyclerListView.this.clickRunnable = new Runnable() { // from class: org.telegram.ui.Components.RecyclerListView.RecyclerListViewItemClickListener.1.1
                            @Override // java.lang.Runnable
                            public void run() {
                                if (this == RecyclerListView.this.clickRunnable) {
                                    RecyclerListView.this.clickRunnable = null;
                                }
                                View view2 = view;
                                if (view2 != null) {
                                    RecyclerListView.this.onChildPressed(view2, 0.0f, 0.0f, false);
                                    if (RecyclerListView.this.instantClick) {
                                        return;
                                    }
                                    view.playSoundEffect(0);
                                    view.sendAccessibilityEvent(1);
                                    if (i == -1) {
                                        return;
                                    }
                                    if (RecyclerListView.this.onItemClickListener != null) {
                                        RecyclerListView.this.onItemClickListener.onItemClick(view, i);
                                    } else if (RecyclerListView.this.onItemClickListenerExtended == null) {
                                    } else {
                                        OnItemClickListenerExtended onItemClickListenerExtended = RecyclerListView.this.onItemClickListenerExtended;
                                        View view3 = view;
                                        onItemClickListenerExtended.onItemClick(view3, i, x - view3.getX(), y - view.getY());
                                    }
                                }
                            }
                        }, ViewConfiguration.getPressedStateDuration());
                        if (RecyclerListView.this.selectChildRunnable == null) {
                            return;
                        }
                        AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                        RecyclerListView.this.selectChildRunnable = null;
                        RecyclerListView.this.currentChildView = null;
                        RecyclerListView.this.interceptedByChild = false;
                        RecyclerListView.this.removeSelection(view, motionEvent);
                    }
                }

                @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
                public void onLongPress(MotionEvent motionEvent) {
                    if (RecyclerListView.this.currentChildView == null || RecyclerListView.this.currentChildPosition == -1) {
                        return;
                    }
                    if (RecyclerListView.this.onItemLongClickListener == null && RecyclerListView.this.onItemLongClickListenerExtended == null) {
                        return;
                    }
                    View view = RecyclerListView.this.currentChildView;
                    if (RecyclerListView.this.onItemLongClickListener != null) {
                        if (!RecyclerListView.this.onItemLongClickListener.onItemClick(RecyclerListView.this.currentChildView, RecyclerListView.this.currentChildPosition)) {
                            return;
                        }
                        view.performHapticFeedback(0);
                        view.sendAccessibilityEvent(2);
                    } else if (!RecyclerListView.this.onItemLongClickListenerExtended.onItemClick(RecyclerListView.this.currentChildView, RecyclerListView.this.currentChildPosition, motionEvent.getX() - RecyclerListView.this.currentChildView.getX(), motionEvent.getY() - RecyclerListView.this.currentChildView.getY())) {
                    } else {
                        view.performHapticFeedback(0);
                        view.sendAccessibilityEvent(2);
                        RecyclerListView.this.longPressCalled = true;
                    }
                }

                @Override // org.telegram.ui.Components.GestureDetectorFixDoubleTap.OnGestureListener
                public boolean hasDoubleTap() {
                    return RecyclerListView.this.onItemLongClickListenerExtended != null;
                }
            });
            RecyclerListView.this.gestureDetector.setIsLongpressEnabled(false);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
        public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            View findChildViewUnder;
            int actionMasked = motionEvent.getActionMasked();
            boolean z = RecyclerListView.this.getScrollState() == 0;
            if ((actionMasked == 0 || actionMasked == 5) && RecyclerListView.this.currentChildView == null && z) {
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                RecyclerListView.this.longPressCalled = false;
                RecyclerView.ItemAnimator itemAnimator = RecyclerListView.this.getItemAnimator();
                if ((RecyclerListView.this.allowItemsInteractionDuringAnimation || itemAnimator == null || !itemAnimator.isRunning()) && RecyclerListView.this.allowSelectChildAtPosition(x, y) && (findChildViewUnder = RecyclerListView.this.findChildViewUnder(x, y)) != null && RecyclerListView.this.allowSelectChildAtPosition(findChildViewUnder)) {
                    RecyclerListView.this.currentChildView = findChildViewUnder;
                }
                if (RecyclerListView.this.currentChildView instanceof ViewGroup) {
                    float x2 = motionEvent.getX() - RecyclerListView.this.currentChildView.getLeft();
                    float y2 = motionEvent.getY() - RecyclerListView.this.currentChildView.getTop();
                    ViewGroup viewGroup = (ViewGroup) RecyclerListView.this.currentChildView;
                    int childCount = viewGroup.getChildCount() - 1;
                    while (true) {
                        if (childCount < 0) {
                            break;
                        }
                        View childAt = viewGroup.getChildAt(childCount);
                        if (x2 >= childAt.getLeft() && x2 <= childAt.getRight() && y2 >= childAt.getTop() && y2 <= childAt.getBottom() && childAt.isClickable()) {
                            RecyclerListView.this.currentChildView = null;
                            break;
                        }
                        childCount--;
                    }
                }
                RecyclerListView.this.currentChildPosition = -1;
                if (RecyclerListView.this.currentChildView != null) {
                    RecyclerListView recyclerListView = RecyclerListView.this;
                    recyclerListView.currentChildPosition = recyclerView.getChildPosition(recyclerListView.currentChildView);
                    MotionEvent obtain = MotionEvent.obtain(0L, 0L, motionEvent.getActionMasked(), motionEvent.getX() - RecyclerListView.this.currentChildView.getLeft(), motionEvent.getY() - RecyclerListView.this.currentChildView.getTop(), 0);
                    if (RecyclerListView.this.currentChildView.onTouchEvent(obtain)) {
                        RecyclerListView.this.interceptedByChild = true;
                    }
                    obtain.recycle();
                }
            }
            if (RecyclerListView.this.currentChildView != null && !RecyclerListView.this.interceptedByChild) {
                try {
                    RecyclerListView.this.gestureDetector.onTouchEvent(motionEvent);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            if (actionMasked == 0 || actionMasked == 5) {
                if (!RecyclerListView.this.interceptedByChild && RecyclerListView.this.currentChildView != null) {
                    final float x3 = motionEvent.getX();
                    final float y3 = motionEvent.getY();
                    RecyclerListView.this.selectChildRunnable = new Runnable() { // from class: org.telegram.ui.Components.RecyclerListView$RecyclerListViewItemClickListener$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            RecyclerListView.RecyclerListViewItemClickListener.this.lambda$onInterceptTouchEvent$0(x3, y3);
                        }
                    };
                    AndroidUtilities.runOnUIThread(RecyclerListView.this.selectChildRunnable, ViewConfiguration.getTapTimeout());
                    if (RecyclerListView.this.currentChildView.isEnabled()) {
                        RecyclerListView recyclerListView2 = RecyclerListView.this;
                        if (recyclerListView2.canHighlightChildAt(recyclerListView2.currentChildView, x3 - RecyclerListView.this.currentChildView.getX(), y3 - RecyclerListView.this.currentChildView.getY())) {
                            RecyclerListView recyclerListView3 = RecyclerListView.this;
                            recyclerListView3.positionSelector(recyclerListView3.currentChildPosition, RecyclerListView.this.currentChildView);
                            Drawable drawable = RecyclerListView.this.selectorDrawable;
                            if (drawable != null) {
                                Drawable current = drawable.getCurrent();
                                if (current instanceof TransitionDrawable) {
                                    if (RecyclerListView.this.onItemLongClickListener != null || RecyclerListView.this.onItemClickListenerExtended != null) {
                                        ((TransitionDrawable) current).startTransition(ViewConfiguration.getLongPressTimeout());
                                    } else {
                                        ((TransitionDrawable) current).resetTransition();
                                    }
                                }
                                if (Build.VERSION.SDK_INT >= 21) {
                                    RecyclerListView.this.selectorDrawable.setHotspot(motionEvent.getX(), motionEvent.getY());
                                }
                            }
                            RecyclerListView.this.updateSelectorState();
                        }
                    }
                    RecyclerListView.this.selectorRect.setEmpty();
                } else {
                    RecyclerListView.this.selectorRect.setEmpty();
                }
            } else if ((actionMasked == 1 || actionMasked == 6 || actionMasked == 3 || !z) && RecyclerListView.this.currentChildView != null) {
                if (RecyclerListView.this.selectChildRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                    RecyclerListView.this.selectChildRunnable = null;
                }
                View view = RecyclerListView.this.currentChildView;
                RecyclerListView recyclerListView4 = RecyclerListView.this;
                recyclerListView4.onChildPressed(recyclerListView4.currentChildView, 0.0f, 0.0f, false);
                RecyclerListView.this.currentChildView = null;
                RecyclerListView.this.interceptedByChild = false;
                RecyclerListView.this.removeSelection(view, motionEvent);
                if ((actionMasked == 1 || actionMasked == 6 || actionMasked == 3) && RecyclerListView.this.onItemLongClickListenerExtended != null && RecyclerListView.this.longPressCalled) {
                    RecyclerListView.this.onItemLongClickListenerExtended.onLongClickRelease();
                    RecyclerListView.this.longPressCalled = false;
                }
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onInterceptTouchEvent$0(float f, float f2) {
            if (RecyclerListView.this.selectChildRunnable == null || RecyclerListView.this.currentChildView == null) {
                return;
            }
            RecyclerListView recyclerListView = RecyclerListView.this;
            recyclerListView.onChildPressed(recyclerListView.currentChildView, f, f2, true);
            RecyclerListView.this.selectChildRunnable = null;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
        public void onRequestDisallowInterceptTouchEvent(boolean z) {
            RecyclerListView.this.cancelClickRunnables(true);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public View findChildViewUnder(float f, float f2) {
        int childCount = getChildCount();
        int i = 0;
        while (i < 2) {
            for (int i2 = childCount - 1; i2 >= 0; i2--) {
                View childAt = getChildAt(i2);
                float f3 = 0.0f;
                float translationX = i == 0 ? childAt.getTranslationX() : 0.0f;
                if (i == 0) {
                    f3 = childAt.getTranslationY();
                }
                if (f >= childAt.getLeft() + translationX && f <= childAt.getRight() + translationX && f2 >= childAt.getTop() + f3 && f2 <= childAt.getBottom() + f3) {
                    return childAt;
                }
            }
            i++;
        }
        return null;
    }

    public void setDisableHighlightState(boolean z) {
        this.disableHighlightState = z;
    }

    public View getPressedChildView() {
        return this.currentChildView;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onChildPressed(View view, float f, float f2, boolean z) {
        if (this.disableHighlightState || view == null) {
            return;
        }
        view.setPressed(z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeSelection(View view, MotionEvent motionEvent) {
        if (view == null || this.selectorRect.isEmpty()) {
            return;
        }
        if (view.isEnabled()) {
            positionSelector(this.currentChildPosition, view);
            Drawable drawable = this.selectorDrawable;
            if (drawable != null) {
                Drawable current = drawable.getCurrent();
                if (current instanceof TransitionDrawable) {
                    ((TransitionDrawable) current).resetTransition();
                }
                if (motionEvent != null && Build.VERSION.SDK_INT >= 21) {
                    this.selectorDrawable.setHotspot(motionEvent.getX(), motionEvent.getY());
                }
            }
        } else {
            this.selectorRect.setEmpty();
        }
        updateSelectorState();
    }

    public void cancelClickRunnables(boolean z) {
        Runnable runnable = this.selectChildRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.selectChildRunnable = null;
        }
        View view = this.currentChildView;
        if (view != null) {
            if (z) {
                onChildPressed(view, 0.0f, 0.0f, false);
            }
            this.currentChildView = null;
            removeSelection(view, null);
        }
        this.selectorRect.setEmpty();
        Runnable runnable2 = this.clickRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            this.clickRunnable = null;
        }
        this.interceptedByChild = false;
    }

    public void setResetSelectorOnChanged(boolean z) {
        this.resetSelectorOnChanged = z;
    }

    public int[] getResourceDeclareStyleableIntArray(String str, String str2) {
        try {
            Field field = Class.forName(str + ".R$styleable").getField(str2);
            if (field != null) {
                return (int[]) field.get(null);
            }
        } catch (Throwable unused) {
        }
        return null;
    }

    public RecyclerListView(Context context) {
        this(context, null);
    }

    @SuppressLint({"PrivateApi"})
    public RecyclerListView(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.allowItemsInteractionDuringAnimation = true;
        this.currentFirst = -1;
        this.currentVisible = -1;
        this.hideIfEmpty = true;
        this.selectorType = 2;
        this.selectorRect = new android.graphics.Rect();
        this.scrollEnabled = true;
        this.lastX = Float.MAX_VALUE;
        this.lastY = Float.MAX_VALUE;
        this.accessibilityEnabled = true;
        this.accessibilityDelegate = new View.AccessibilityDelegate(this) { // from class: org.telegram.ui.Components.RecyclerListView.1
            @Override // android.view.View.AccessibilityDelegate
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
                if (view.isEnabled()) {
                    accessibilityNodeInfo.addAction(16);
                }
            }
        };
        this.resetSelectorOnChanged = true;
        this.observer = new RecyclerView.AdapterDataObserver() { // from class: org.telegram.ui.Components.RecyclerListView.2
            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onChanged() {
                RecyclerListView.this.checkIfEmpty(true);
                if (RecyclerListView.this.resetSelectorOnChanged) {
                    RecyclerListView.this.currentFirst = -1;
                    if (RecyclerListView.this.removeHighlighSelectionRunnable == null) {
                        RecyclerListView.this.selectorRect.setEmpty();
                    }
                }
                RecyclerListView.this.invalidate();
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeInserted(int i, int i2) {
                RecyclerListView.this.checkIfEmpty(true);
                if (RecyclerListView.this.pinnedHeader == null || RecyclerListView.this.pinnedHeader.getAlpha() != 0.0f) {
                    return;
                }
                RecyclerListView.this.currentFirst = -1;
                RecyclerListView.this.invalidateViews();
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeRemoved(int i, int i2) {
                RecyclerListView.this.checkIfEmpty(true);
            }
        };
        this.scroller = new Runnable() { // from class: org.telegram.ui.Components.RecyclerListView.6
            @Override // java.lang.Runnable
            public void run() {
                int dp;
                RecyclerListView recyclerListView = RecyclerListView.this;
                recyclerListView.multiSelectionListener.getPaddings(recyclerListView.listPaddings);
                if (RecyclerListView.this.multiselectScrollToTop) {
                    dp = -AndroidUtilities.dp(12.0f);
                    RecyclerListView recyclerListView2 = RecyclerListView.this;
                    recyclerListView2.chekMultiselect(0.0f, recyclerListView2.listPaddings[0]);
                } else {
                    dp = AndroidUtilities.dp(12.0f);
                    RecyclerListView recyclerListView3 = RecyclerListView.this;
                    recyclerListView3.chekMultiselect(0.0f, recyclerListView3.getMeasuredHeight() - RecyclerListView.this.listPaddings[1]);
                }
                RecyclerListView.this.multiSelectionListener.scrollBy(dp);
                RecyclerListView recyclerListView4 = RecyclerListView.this;
                if (recyclerListView4.multiselectScrollRunning) {
                    AndroidUtilities.runOnUIThread(recyclerListView4.scroller);
                }
            }
        };
        this.resourcesProvider = resourcesProvider;
        setGlowColor(getThemedColor("actionBarDefault"));
        Drawable selectorDrawable = Theme.getSelectorDrawable(getThemedColor("listSelectorSDK21"), false);
        this.selectorDrawable = selectorDrawable;
        selectorDrawable.setCallback(this);
        try {
            if (!gotAttributes) {
                int[] resourceDeclareStyleableIntArray = getResourceDeclareStyleableIntArray("com.android.internal", "View");
                attributes = resourceDeclareStyleableIntArray;
                if (resourceDeclareStyleableIntArray == null) {
                    attributes = new int[0];
                }
                gotAttributes = true;
            }
            TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributes);
            View.class.getDeclaredMethod("initializeScrollbars", TypedArray.class).invoke(this, obtainStyledAttributes);
            obtainStyledAttributes.recycle();
        } catch (Throwable th) {
            FileLog.e(th);
        }
        super.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.RecyclerListView.3
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                boolean z = false;
                if (i != 0 && RecyclerListView.this.currentChildView != null) {
                    if (RecyclerListView.this.selectChildRunnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                        RecyclerListView.this.selectChildRunnable = null;
                    }
                    MotionEvent obtain = MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0);
                    try {
                        RecyclerListView.this.gestureDetector.onTouchEvent(obtain);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    RecyclerListView.this.currentChildView.onTouchEvent(obtain);
                    obtain.recycle();
                    View view = RecyclerListView.this.currentChildView;
                    RecyclerListView recyclerListView = RecyclerListView.this;
                    recyclerListView.onChildPressed(recyclerListView.currentChildView, 0.0f, 0.0f, false);
                    RecyclerListView.this.currentChildView = null;
                    RecyclerListView.this.removeSelection(view, null);
                    RecyclerListView.this.interceptedByChild = false;
                }
                if (RecyclerListView.this.onScrollListener != null) {
                    RecyclerListView.this.onScrollListener.onScrollStateChanged(recyclerView, i);
                }
                RecyclerListView recyclerListView2 = RecyclerListView.this;
                if (i == 1 || i == 2) {
                    z = true;
                }
                recyclerListView2.scrollingByUser = z;
                if (z) {
                    recyclerListView2.scrolledByUserOnce = true;
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                if (RecyclerListView.this.onScrollListener != null) {
                    RecyclerListView.this.onScrollListener.onScrolled(recyclerView, i, i2);
                }
                RecyclerListView recyclerListView = RecyclerListView.this;
                if (recyclerListView.selectorPosition != -1) {
                    recyclerListView.selectorRect.offset(-i, -i2);
                    RecyclerListView recyclerListView2 = RecyclerListView.this;
                    recyclerListView2.selectorDrawable.setBounds(recyclerListView2.selectorRect);
                    RecyclerListView.this.invalidate();
                } else {
                    recyclerListView.selectorRect.setEmpty();
                }
                RecyclerListView.this.checkSection(false);
                if (i2 == 0 || RecyclerListView.this.fastScroll == null) {
                    return;
                }
                RecyclerListView.this.fastScroll.showFloatingDate();
            }
        });
        addOnItemTouchListener(new RecyclerListViewItemClickListener(context));
    }

    @Override // android.view.View
    public void setVerticalScrollBarEnabled(boolean z) {
        if (attributes != null) {
            super.setVerticalScrollBarEnabled(z);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.recyclerview.widget.RecyclerView, android.view.View
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.fastScroll != null) {
            int measuredHeight = (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom();
            this.fastScroll.getLayoutParams().height = measuredHeight;
            this.fastScroll.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(132.0f), NUM), View.MeasureSpec.makeMeasureSpec(measuredHeight, NUM));
        }
        this.touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.fastScroll != null) {
            this.selfOnLayout = true;
            int paddingTop = i2 + getPaddingTop();
            FastScroll fastScroll = this.fastScroll;
            if (fastScroll.isRtl) {
                fastScroll.layout(0, paddingTop, fastScroll.getMeasuredWidth(), this.fastScroll.getMeasuredHeight() + paddingTop);
            } else {
                int measuredWidth = getMeasuredWidth() - this.fastScroll.getMeasuredWidth();
                FastScroll fastScroll2 = this.fastScroll;
                fastScroll2.layout(measuredWidth, paddingTop, fastScroll2.getMeasuredWidth() + measuredWidth, this.fastScroll.getMeasuredHeight() + paddingTop);
            }
            this.selfOnLayout = false;
        }
        checkSection(false);
        IntReturnCallback intReturnCallback = this.pendingHighlightPosition;
        if (intReturnCallback != null) {
            highlightRowInternal(intReturnCallback, false);
        }
    }

    public void setSelectorType(int i) {
        this.selectorType = i;
    }

    public void setSelectorRadius(int i) {
        this.selectorRadius = i;
    }

    public void setTopBottomSelectorRadius(int i) {
        this.topBottomSelectorRadius = i;
    }

    public void setDrawSelectorBehind(boolean z) {
        this.drawSelectorBehind = z;
    }

    public void setSelectorDrawableColor(int i) {
        Drawable drawable = this.selectorDrawable;
        if (drawable != null) {
            drawable.setCallback(null);
        }
        int i2 = this.selectorType;
        if (i2 == 8) {
            this.selectorDrawable = Theme.createRadSelectorDrawable(i, this.selectorRadius, 0);
        } else {
            int i3 = this.topBottomSelectorRadius;
            if (i3 > 0) {
                this.selectorDrawable = Theme.createRadSelectorDrawable(i, i3, i3);
            } else {
                int i4 = this.selectorRadius;
                if (i4 > 0) {
                    this.selectorDrawable = Theme.createSimpleSelectorRoundRectDrawable(i4, 0, i, -16777216);
                } else if (i2 == 2) {
                    this.selectorDrawable = Theme.getSelectorDrawable(i, false);
                } else {
                    this.selectorDrawable = Theme.createSelectorDrawable(i, i2);
                }
            }
        }
        this.selectorDrawable.setCallback(this);
    }

    public Drawable getSelectorDrawable() {
        return this.selectorDrawable;
    }

    public void checkSection(boolean z) {
        FastScroll fastScroll;
        RecyclerView.ViewHolder childViewHolder;
        FastScroll fastScroll2;
        View view;
        int i;
        RecyclerView.ViewHolder childViewHolder2;
        int adapterPosition;
        int sectionForPosition;
        if (((this.scrollingByUser || z) && this.fastScroll != null) || !(this.sectionsType == 0 || this.sectionsAdapter == null)) {
            RecyclerView.LayoutManager layoutManager = getLayoutManager();
            if (!(layoutManager instanceof LinearLayoutManager)) {
                return;
            }
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            if (linearLayoutManager.getOrientation() != 1) {
                return;
            }
            int i2 = 0;
            if (this.sectionsAdapter != null) {
                int paddingTop = getPaddingTop();
                int i3 = this.sectionsType;
                int i4 = Integer.MAX_VALUE;
                if (i3 != 1 && i3 != 3) {
                    if (i3 != 2) {
                        return;
                    }
                    this.pinnedHeaderShadowTargetAlpha = 0.0f;
                    if (this.sectionsAdapter.getItemCount() == 0) {
                        return;
                    }
                    int childCount = getChildCount();
                    View view2 = null;
                    int i5 = Integer.MAX_VALUE;
                    View view3 = null;
                    int i6 = 0;
                    for (int i7 = 0; i7 < childCount; i7++) {
                        View childAt = getChildAt(i7);
                        int bottom = childAt.getBottom();
                        if (bottom > this.sectionOffset + paddingTop) {
                            if (bottom < i4) {
                                view3 = childAt;
                                i4 = bottom;
                            }
                            i6 = Math.max(i6, bottom);
                            if (bottom >= this.sectionOffset + paddingTop + AndroidUtilities.dp(32.0f) && bottom < i5) {
                                view2 = childAt;
                                i5 = bottom;
                            }
                        }
                    }
                    if (view3 == null || (childViewHolder2 = getChildViewHolder(view3)) == null || (sectionForPosition = this.sectionsAdapter.getSectionForPosition((adapterPosition = childViewHolder2.getAdapterPosition()))) < 0) {
                        return;
                    }
                    if (this.currentFirst != sectionForPosition || this.pinnedHeader == null) {
                        View sectionHeaderView = getSectionHeaderView(sectionForPosition, this.pinnedHeader);
                        this.pinnedHeader = sectionHeaderView;
                        sectionHeaderView.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), NUM), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 0));
                        View view4 = this.pinnedHeader;
                        view4.layout(0, 0, view4.getMeasuredWidth(), this.pinnedHeader.getMeasuredHeight());
                        this.currentFirst = sectionForPosition;
                    }
                    if (this.pinnedHeader != null && view2 != null && view2.getClass() != this.pinnedHeader.getClass()) {
                        this.pinnedHeaderShadowTargetAlpha = 1.0f;
                    }
                    int countForSection = this.sectionsAdapter.getCountForSection(sectionForPosition);
                    int positionInSectionForPosition = this.sectionsAdapter.getPositionInSectionForPosition(adapterPosition);
                    if (i6 == 0 || i6 >= getMeasuredHeight() - getPaddingBottom()) {
                        i2 = this.sectionOffset;
                    }
                    if (positionInSectionForPosition == countForSection - 1) {
                        int height = this.pinnedHeader.getHeight();
                        int top = ((view3.getTop() - paddingTop) - this.sectionOffset) + view3.getHeight();
                        int i8 = top < height ? top - height : paddingTop;
                        if (i8 < 0) {
                            this.pinnedHeader.setTag(Integer.valueOf(paddingTop + i2 + i8));
                        } else {
                            this.pinnedHeader.setTag(Integer.valueOf(paddingTop + i2));
                        }
                    } else {
                        this.pinnedHeader.setTag(Integer.valueOf(paddingTop + i2));
                    }
                    invalidate();
                    return;
                }
                int childCount2 = getChildCount();
                int i9 = Integer.MAX_VALUE;
                View view5 = null;
                int i10 = 0;
                for (int i11 = 0; i11 < childCount2; i11++) {
                    View childAt2 = getChildAt(i11);
                    int bottom2 = childAt2.getBottom();
                    if (bottom2 > this.sectionOffset + paddingTop) {
                        if (bottom2 < i4) {
                            i4 = bottom2;
                            view5 = childAt2;
                        }
                        i10 = Math.max(i10, bottom2);
                        if (bottom2 >= this.sectionOffset + paddingTop + AndroidUtilities.dp(32.0f) && bottom2 < i9) {
                            i9 = bottom2;
                        }
                    }
                }
                if (view5 == null || (childViewHolder = getChildViewHolder(view5)) == null) {
                    return;
                }
                int adapterPosition2 = childViewHolder.getAdapterPosition();
                int abs = Math.abs(linearLayoutManager.findLastVisibleItemPosition() - adapterPosition2) + 1;
                if ((this.scrollingByUser || z) && (fastScroll2 = this.fastScroll) != null && !fastScroll2.isPressed() && (getAdapter() instanceof FastScrollAdapter)) {
                    this.fastScroll.setProgress(Math.min(1.0f, adapterPosition2 / ((this.sectionsAdapter.getTotalItemsCount() - abs) + 1)));
                }
                this.headersCache.addAll(this.headers);
                this.headers.clear();
                if (this.sectionsAdapter.getItemCount() == 0) {
                    return;
                }
                if (this.currentFirst != adapterPosition2 || this.currentVisible != abs) {
                    this.currentFirst = adapterPosition2;
                    this.currentVisible = abs;
                    this.sectionsCount = 1;
                    int sectionForPosition2 = this.sectionsAdapter.getSectionForPosition(adapterPosition2);
                    this.startSection = sectionForPosition2;
                    int countForSection2 = (this.sectionsAdapter.getCountForSection(sectionForPosition2) + adapterPosition2) - this.sectionsAdapter.getPositionInSectionForPosition(adapterPosition2);
                    while (countForSection2 < adapterPosition2 + abs) {
                        countForSection2 += this.sectionsAdapter.getCountForSection(this.startSection + this.sectionsCount);
                        this.sectionsCount++;
                    }
                }
                if (this.sectionsType == 3) {
                    return;
                }
                int i12 = adapterPosition2;
                for (int i13 = this.startSection; i13 < this.startSection + this.sectionsCount; i13++) {
                    if (!this.headersCache.isEmpty()) {
                        view = this.headersCache.get(0);
                        this.headersCache.remove(0);
                    } else {
                        view = null;
                    }
                    View sectionHeaderView2 = getSectionHeaderView(i13, view);
                    this.headers.add(sectionHeaderView2);
                    int countForSection3 = this.sectionsAdapter.getCountForSection(i13);
                    if (i13 == this.startSection) {
                        int positionInSectionForPosition2 = this.sectionsAdapter.getPositionInSectionForPosition(i12);
                        if (positionInSectionForPosition2 == countForSection3 - 1) {
                            sectionHeaderView2.setTag(Integer.valueOf((-sectionHeaderView2.getHeight()) + paddingTop));
                        } else if (positionInSectionForPosition2 == countForSection3 - 2) {
                            View childAt3 = getChildAt(i12 - adapterPosition2);
                            if (childAt3 != null) {
                                i = childAt3.getTop() + paddingTop;
                            } else {
                                i = -AndroidUtilities.dp(100.0f);
                            }
                            sectionHeaderView2.setTag(Integer.valueOf(Math.min(i, 0)));
                        } else {
                            sectionHeaderView2.setTag(0);
                        }
                        countForSection3 -= this.sectionsAdapter.getPositionInSectionForPosition(adapterPosition2);
                    } else {
                        View childAt4 = getChildAt(i12 - adapterPosition2);
                        if (childAt4 != null) {
                            sectionHeaderView2.setTag(Integer.valueOf(childAt4.getTop() + paddingTop));
                        } else {
                            sectionHeaderView2.setTag(Integer.valueOf(-AndroidUtilities.dp(100.0f)));
                        }
                    }
                    i12 += countForSection3;
                }
                return;
            }
            int findFirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            Math.abs(linearLayoutManager.findLastVisibleItemPosition() - findFirstVisibleItemPosition);
            if (findFirstVisibleItemPosition == -1) {
                return;
            }
            if ((!this.scrollingByUser && !z) || (fastScroll = this.fastScroll) == null || fastScroll.isPressed()) {
                return;
            }
            RecyclerView.Adapter adapter = getAdapter();
            if (!(adapter instanceof FastScrollAdapter)) {
                return;
            }
            FastScrollAdapter fastScrollAdapter = (FastScrollAdapter) adapter;
            float scrollProgress = fastScrollAdapter.getScrollProgress(this);
            this.fastScroll.setIsVisible(fastScrollAdapter.fastScrollIsVisible(this));
            this.fastScroll.setProgress(Math.min(1.0f, scrollProgress));
            this.fastScroll.getCurrentLetter(false);
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

    public OnItemClickListener getOnItemClickListener() {
        return this.onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        setOnItemLongClickListener(onItemLongClickListener, ViewConfiguration.getLongPressTimeout());
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener, long j) {
        this.onItemLongClickListener = onItemLongClickListener;
        this.gestureDetector.setIsLongpressEnabled(onItemLongClickListener != null);
        this.gestureDetector.setLongpressDuration(j);
    }

    public void setOnItemLongClickListener(OnItemLongClickListenerExtended onItemLongClickListenerExtended) {
        setOnItemLongClickListener(onItemLongClickListenerExtended, ViewConfiguration.getLongPressTimeout());
    }

    public void setOnItemLongClickListener(OnItemLongClickListenerExtended onItemLongClickListenerExtended, long j) {
        this.onItemLongClickListenerExtended = onItemLongClickListenerExtended;
        this.gestureDetector.setIsLongpressEnabled(onItemLongClickListenerExtended != null);
        this.gestureDetector.setLongpressDuration(j);
    }

    public void setEmptyView(View view) {
        View view2 = this.emptyView;
        if (view2 == view) {
            return;
        }
        if (view2 != null) {
            view2.animate().setListener(null).cancel();
        }
        this.emptyView = view;
        if (this.animateEmptyView && view != null) {
            view.setVisibility(8);
        }
        if (this.isHidden) {
            View view3 = this.emptyView;
            if (view3 == null) {
                return;
            }
            this.emptyViewAnimateToVisibility = 8;
            view3.setVisibility(8);
            return;
        }
        this.emptyViewAnimateToVisibility = -1;
        checkIfEmpty(updateEmptyViewAnimated());
    }

    protected boolean updateEmptyViewAnimated() {
        return isAttachedToWindow();
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
        FastScroll fastScroll = this.fastScroll;
        if (fastScroll != null) {
            fastScroll.updateColors();
        }
    }

    public void setPinnedHeaderShadowDrawable(Drawable drawable) {
        this.pinnedHeaderShadowDrawable = drawable;
    }

    @Override // android.view.View
    public boolean canScrollVertically(int i) {
        return this.scrollEnabled && super.canScrollVertically(i);
    }

    public void setScrollEnabled(boolean z) {
        this.scrollEnabled = z;
    }

    public void highlightRow(IntReturnCallback intReturnCallback) {
        highlightRowInternal(intReturnCallback, true);
    }

    private void highlightRowInternal(IntReturnCallback intReturnCallback, boolean z) {
        Runnable runnable = this.removeHighlighSelectionRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.removeHighlighSelectionRunnable = null;
        }
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = findViewHolderForAdapterPosition(intReturnCallback.run());
        if (findViewHolderForAdapterPosition == null) {
            if (!z) {
                return;
            }
            this.pendingHighlightPosition = intReturnCallback;
            return;
        }
        positionSelector(findViewHolderForAdapterPosition.getLayoutPosition(), findViewHolderForAdapterPosition.itemView);
        Drawable drawable = this.selectorDrawable;
        if (drawable != null) {
            Drawable current = drawable.getCurrent();
            if (current instanceof TransitionDrawable) {
                if (this.onItemLongClickListener != null || this.onItemClickListenerExtended != null) {
                    ((TransitionDrawable) current).startTransition(ViewConfiguration.getLongPressTimeout());
                } else {
                    ((TransitionDrawable) current).resetTransition();
                }
            }
            if (Build.VERSION.SDK_INT >= 21) {
                this.selectorDrawable.setHotspot(findViewHolderForAdapterPosition.itemView.getMeasuredWidth() / 2, findViewHolderForAdapterPosition.itemView.getMeasuredHeight() / 2);
            }
        }
        Drawable drawable2 = this.selectorDrawable;
        if (drawable2 != null && drawable2.isStateful() && this.selectorDrawable.setState(getDrawableStateForSelector())) {
            invalidateDrawable(this.selectorDrawable);
        }
        Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.RecyclerListView$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                RecyclerListView.this.lambda$highlightRowInternal$0();
            }
        };
        this.removeHighlighSelectionRunnable = runnable2;
        AndroidUtilities.runOnUIThread(runnable2, 700L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$highlightRowInternal$0() {
        this.removeHighlighSelectionRunnable = null;
        this.pendingHighlightPosition = null;
        Drawable drawable = this.selectorDrawable;
        if (drawable != null) {
            Drawable current = drawable.getCurrent();
            if (current instanceof TransitionDrawable) {
                ((TransitionDrawable) current).resetTransition();
            }
        }
        Drawable drawable2 = this.selectorDrawable;
        if (drawable2 == null || !drawable2.isStateful()) {
            return;
        }
        this.selectorDrawable.setState(StateSet.NOTHING);
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return false;
        }
        if (this.disallowInterceptTouchEvents) {
            requestDisallowInterceptTouchEvent(true);
        }
        OnInterceptTouchListener onInterceptTouchListener = this.onInterceptTouchListener;
        return (onInterceptTouchListener != null && onInterceptTouchListener.onInterceptTouchEvent(motionEvent)) || super.onInterceptTouchEvent(motionEvent);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        View view;
        FastScroll fastScroll = getFastScroll();
        if (fastScroll == null || !fastScroll.isVisible || !fastScroll.isMoving || motionEvent.getActionMasked() == 1 || motionEvent.getActionMasked() == 3) {
            if (this.sectionsAdapter != null && (view = this.pinnedHeader) != null && view.getAlpha() != 0.0f && this.pinnedHeader.dispatchTouchEvent(motionEvent)) {
                return true;
            }
            return super.dispatchTouchEvent(motionEvent);
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkIfEmpty(boolean z) {
        if (this.isHidden) {
            return;
        }
        int i = 0;
        if (getAdapter() == null || this.emptyView == null) {
            if (!this.hiddenByEmptyView || getVisibility() == 0) {
                return;
            }
            setVisibility(0);
            this.hiddenByEmptyView = false;
            return;
        }
        boolean emptyViewIsVisible = emptyViewIsVisible();
        int i2 = emptyViewIsVisible ? 0 : 8;
        if (!this.animateEmptyView || !SharedConfig.animationsEnabled()) {
            z = false;
        }
        if (z) {
            if (this.emptyViewAnimateToVisibility != i2) {
                this.emptyViewAnimateToVisibility = i2;
                if (i2 == 0) {
                    this.emptyView.animate().setListener(null).cancel();
                    if (this.emptyView.getVisibility() == 8) {
                        this.emptyView.setVisibility(0);
                        this.emptyView.setAlpha(0.0f);
                        if (this.emptyViewAnimationType == 1) {
                            this.emptyView.setScaleX(0.7f);
                            this.emptyView.setScaleY(0.7f);
                        }
                    }
                    this.emptyView.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(150L).start();
                } else if (this.emptyView.getVisibility() != 8) {
                    ViewPropertyAnimator alpha = this.emptyView.animate().alpha(0.0f);
                    if (this.emptyViewAnimationType == 1) {
                        alpha.scaleY(0.7f).scaleX(0.7f);
                    }
                    alpha.setDuration(150L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.RecyclerListView.4
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            if (RecyclerListView.this.emptyView != null) {
                                RecyclerListView.this.emptyView.setVisibility(8);
                            }
                        }
                    }).start();
                }
            }
        } else {
            this.emptyViewAnimateToVisibility = i2;
            this.emptyView.setVisibility(i2);
            this.emptyView.setAlpha(1.0f);
        }
        if (!this.hideIfEmpty) {
            return;
        }
        if (emptyViewIsVisible) {
            i = 4;
        }
        if (getVisibility() != i) {
            setVisibility(i);
        }
        this.hiddenByEmptyView = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean emptyViewIsVisible() {
        return getAdapter() != null && !isFastScrollAnimationRunning() && getAdapter().getItemCount() == 0;
    }

    public void hide() {
        if (this.isHidden) {
            return;
        }
        this.isHidden = true;
        if (getVisibility() != 8) {
            setVisibility(8);
        }
        View view = this.emptyView;
        if (view == null || view.getVisibility() == 8) {
            return;
        }
        this.emptyView.setVisibility(8);
    }

    public void show() {
        if (!this.isHidden) {
            return;
        }
        this.isHidden = false;
        checkIfEmpty(false);
    }

    @Override // android.view.View
    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i != 0) {
            this.hiddenByEmptyView = false;
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public void setOnScrollListener(RecyclerView.OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public void setHideIfEmpty(boolean z) {
        this.hideIfEmpty = z;
    }

    public RecyclerView.OnScrollListener getOnScrollListener() {
        return this.onScrollListener;
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

    public void setFastScrollEnabled(int i) {
        this.fastScroll = new FastScroll(getContext(), i);
        if (getParent() != null) {
            ((ViewGroup) getParent()).addView(this.fastScroll);
        }
    }

    public void setFastScrollVisible(boolean z) {
        FastScroll fastScroll = this.fastScroll;
        if (fastScroll == null) {
            return;
        }
        fastScroll.setVisibility(z ? 0 : 8);
        this.fastScroll.isVisible = z;
    }

    public void setSectionsType(int i) {
        this.sectionsType = i;
        if (i == 1 || i == 3) {
            this.headers = new ArrayList<>();
            this.headersCache = new ArrayList<>();
        }
    }

    public void setPinnedSectionOffsetY(int i) {
        this.sectionOffset = i;
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void positionSelector(int i, View view) {
        positionSelector(i, view, false, -1.0f, -1.0f);
    }

    public void updateSelector() {
        View view;
        int i = this.selectorPosition;
        if (i == -1 || (view = this.selectorView) == null) {
            return;
        }
        positionSelector(i, view);
        invalidate();
    }

    private void positionSelector(int i, View view, boolean z, float f, float f2) {
        Runnable runnable = this.removeHighlighSelectionRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.removeHighlighSelectionRunnable = null;
            this.pendingHighlightPosition = null;
        }
        if (this.selectorDrawable == null) {
            return;
        }
        boolean z2 = i != this.selectorPosition;
        int selectionBottomPadding = getAdapter() instanceof SelectionAdapter ? ((SelectionAdapter) getAdapter()).getSelectionBottomPadding(view) : 0;
        if (i != -1) {
            this.selectorPosition = i;
        }
        this.selectorView = view;
        if (this.selectorType == 8) {
            Theme.setMaskDrawableRad(this.selectorDrawable, this.selectorRadius, 0);
        } else if (this.topBottomSelectorRadius > 0 && getAdapter() != null) {
            Theme.setMaskDrawableRad(this.selectorDrawable, i == 0 ? this.topBottomSelectorRadius : 0, i == getAdapter().getItemCount() + (-2) ? this.topBottomSelectorRadius : 0);
        }
        this.selectorRect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom() - selectionBottomPadding);
        this.selectorRect.offset((int) view.getTranslationX(), (int) view.getTranslationY());
        boolean isEnabled = view.isEnabled();
        if (this.isChildViewEnabled != isEnabled) {
            this.isChildViewEnabled = isEnabled;
        }
        if (z2) {
            this.selectorDrawable.setVisible(false, false);
            this.selectorDrawable.setState(StateSet.NOTHING);
        }
        this.selectorDrawable.setBounds(this.selectorRect);
        if (z2 && getVisibility() == 0) {
            this.selectorDrawable.setVisible(true, false);
        }
        if (Build.VERSION.SDK_INT < 21 || !z) {
            return;
        }
        this.selectorDrawable.setHotspot(f, f2);
    }

    public void setAllowItemsInteractionDuringAnimation(boolean z) {
        this.allowItemsInteractionDuringAnimation = z;
    }

    public void hideSelector(boolean z) {
        View view = this.currentChildView;
        if (view != null) {
            onChildPressed(view, 0.0f, 0.0f, false);
            this.currentChildView = null;
            if (z) {
                removeSelection(view, null);
            }
        }
        if (!z) {
            this.selectorDrawable.setState(StateSet.NOTHING);
            this.selectorRect.setEmpty();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSelectorState() {
        Drawable drawable = this.selectorDrawable;
        if (drawable == null || !drawable.isStateful()) {
            return;
        }
        if (this.currentChildView != null) {
            if (!this.selectorDrawable.setState(getDrawableStateForSelector())) {
                return;
            }
            invalidateDrawable(this.selectorDrawable);
        } else if (this.removeHighlighSelectionRunnable != null) {
        } else {
            this.selectorDrawable.setState(StateSet.NOTHING);
        }
    }

    private int[] getDrawableStateForSelector() {
        int[] onCreateDrawableState = onCreateDrawableState(1);
        onCreateDrawableState[onCreateDrawableState.length - 1] = 16842919;
        return onCreateDrawableState;
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public void onChildAttachedToWindow(View view) {
        if (getAdapter() instanceof SelectionAdapter) {
            RecyclerView.ViewHolder findContainingViewHolder = findContainingViewHolder(view);
            if (findContainingViewHolder != null) {
                view.setEnabled(((SelectionAdapter) getAdapter()).isEnabled(findContainingViewHolder));
                if (this.accessibilityEnabled) {
                    view.setAccessibilityDelegate(this.accessibilityDelegate);
                }
            }
        } else {
            view.setEnabled(false);
            view.setAccessibilityDelegate(null);
        }
        super.onChildAttachedToWindow(view);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        updateSelectorState();
    }

    @Override // android.view.View
    public boolean verifyDrawable(Drawable drawable) {
        return this.selectorDrawable == drawable || super.verifyDrawable(drawable);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.selectorDrawable;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        FastScroll fastScroll = this.fastScroll;
        if (fastScroll == null || fastScroll.getParent() == getParent()) {
            return;
        }
        ViewGroup viewGroup = (ViewGroup) this.fastScroll.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(this.fastScroll);
        }
        ((ViewGroup) getParent()).addView(this.fastScroll);
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public void setAdapter(RecyclerView.Adapter adapter) {
        RecyclerView.Adapter adapter2 = getAdapter();
        if (adapter2 != null) {
            adapter2.unregisterAdapterDataObserver(this.observer);
        }
        ArrayList<View> arrayList = this.headers;
        if (arrayList != null) {
            arrayList.clear();
            this.headersCache.clear();
        }
        this.currentFirst = -1;
        this.selectorPosition = -1;
        this.selectorView = null;
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

    @Override // androidx.recyclerview.widget.RecyclerView
    public void stopScroll() {
        try {
            super.stopScroll();
        } catch (NullPointerException unused) {
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public boolean dispatchNestedPreScroll(int i, int i2, int[] iArr, int[] iArr2, int i3) {
        if (this.longPressCalled) {
            OnItemLongClickListenerExtended onItemLongClickListenerExtended = this.onItemLongClickListenerExtended;
            if (onItemLongClickListenerExtended != null) {
                onItemLongClickListenerExtended.onMove(i, i2);
            }
            iArr[0] = i;
            iArr[1] = i2;
            return true;
        }
        return super.dispatchNestedPreScroll(i, i2, iArr, iArr2, i3);
    }

    private View getSectionHeaderView(int i, View view) {
        boolean z = view == null;
        View sectionHeaderView = this.sectionsAdapter.getSectionHeaderView(i, view);
        if (z) {
            ensurePinnedHeaderLayout(sectionHeaderView, false);
        }
        return sectionHeaderView;
    }

    private void ensurePinnedHeaderLayout(View view, boolean z) {
        if (view == null) {
            return;
        }
        if (!view.isLayoutRequested() && !z) {
            return;
        }
        int i = this.sectionsType;
        if (i == 1) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            try {
                view.measure(View.MeasureSpec.makeMeasureSpec(layoutParams.width, NUM), View.MeasureSpec.makeMeasureSpec(layoutParams.height, NUM));
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else if (i == 2) {
            try {
                view.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.recyclerview.widget.RecyclerView, android.view.View
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        View view;
        super.onSizeChanged(i, i2, i3, i4);
        FrameLayout frameLayout = this.overlayContainer;
        if (frameLayout != null) {
            frameLayout.requestLayout();
        }
        int i5 = this.sectionsType;
        if (i5 == 1) {
            if (this.sectionsAdapter == null || this.headers.isEmpty()) {
                return;
            }
            for (int i6 = 0; i6 < this.headers.size(); i6++) {
                ensurePinnedHeaderLayout(this.headers.get(i6), true);
            }
        } else if (i5 == 2 && this.sectionsAdapter != null && (view = this.pinnedHeader) != null) {
            ensurePinnedHeaderLayout(view, true);
        }
    }

    public android.graphics.Rect getSelectorRect() {
        return this.selectorRect;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        View view;
        RecyclerItemsEnterAnimator recyclerItemsEnterAnimator = this.itemsEnterAnimator;
        if (recyclerItemsEnterAnimator != null) {
            recyclerItemsEnterAnimator.dispatchDraw();
        }
        if (this.drawSelectorBehind && !this.selectorRect.isEmpty()) {
            this.selectorDrawable.setBounds(this.selectorRect);
            canvas.save();
            Consumer<Canvas> consumer = this.selectorTransformer;
            if (consumer != null) {
                consumer.accept(canvas);
            }
            this.selectorDrawable.draw(canvas);
            canvas.restore();
        }
        super.dispatchDraw(canvas);
        if (!this.drawSelectorBehind && !this.selectorRect.isEmpty()) {
            this.selectorDrawable.setBounds(this.selectorRect);
            canvas.save();
            Consumer<Canvas> consumer2 = this.selectorTransformer;
            if (consumer2 != null) {
                consumer2.accept(canvas);
            }
            this.selectorDrawable.draw(canvas);
            canvas.restore();
        }
        FrameLayout frameLayout = this.overlayContainer;
        if (frameLayout != null) {
            frameLayout.draw(canvas);
        }
        int i = this.sectionsType;
        float f = 0.0f;
        if (i == 1) {
            if (this.sectionsAdapter == null || this.headers.isEmpty()) {
                return;
            }
            for (int i2 = 0; i2 < this.headers.size(); i2++) {
                View view2 = this.headers.get(i2);
                int save = canvas.save();
                canvas.translate(LocaleController.isRTL ? getWidth() - view2.getWidth() : 0.0f, ((Integer) view2.getTag()).intValue());
                canvas.clipRect(0, 0, getWidth(), view2.getMeasuredHeight());
                view2.draw(canvas);
                canvas.restoreToCount(save);
            }
        } else if (i == 2 && this.sectionsAdapter != null && (view = this.pinnedHeader) != null && view.getAlpha() != 0.0f) {
            int save2 = canvas.save();
            int intValue = ((Integer) this.pinnedHeader.getTag()).intValue();
            if (LocaleController.isRTL) {
                f = getWidth() - this.pinnedHeader.getWidth();
            }
            canvas.translate(f, intValue);
            Drawable drawable = this.pinnedHeaderShadowDrawable;
            if (drawable != null) {
                drawable.setBounds(0, this.pinnedHeader.getMeasuredHeight(), getWidth(), this.pinnedHeader.getMeasuredHeight() + this.pinnedHeaderShadowDrawable.getIntrinsicHeight());
                this.pinnedHeaderShadowDrawable.setAlpha((int) (this.pinnedHeaderShadowAlpha * 255.0f));
                this.pinnedHeaderShadowDrawable.draw(canvas);
                long elapsedRealtime = SystemClock.elapsedRealtime();
                long min = Math.min(20L, elapsedRealtime - this.lastAlphaAnimationTime);
                this.lastAlphaAnimationTime = elapsedRealtime;
                float f2 = this.pinnedHeaderShadowAlpha;
                float f3 = this.pinnedHeaderShadowTargetAlpha;
                if (f2 < f3) {
                    float f4 = f2 + (((float) min) / 180.0f);
                    this.pinnedHeaderShadowAlpha = f4;
                    if (f4 > f3) {
                        this.pinnedHeaderShadowAlpha = f3;
                    }
                    invalidate();
                } else if (f2 > f3) {
                    float f5 = f2 - (((float) min) / 180.0f);
                    this.pinnedHeaderShadowAlpha = f5;
                    if (f5 < f3) {
                        this.pinnedHeaderShadowAlpha = f3;
                    }
                    invalidate();
                }
            }
            canvas.clipRect(0, 0, getWidth(), this.pinnedHeader.getMeasuredHeight());
            this.pinnedHeader.draw(canvas);
            canvas.restoreToCount(save2);
        }
    }

    public void relayoutPinnedHeader() {
        View view = this.pinnedHeader;
        if (view != null) {
            view.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), NUM), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 0));
            View view2 = this.pinnedHeader;
            view2.layout(0, 0, view2.getMeasuredWidth(), this.pinnedHeader.getMeasuredHeight());
            invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.selectorPosition = -1;
        this.selectorView = null;
        this.selectorRect.setEmpty();
        RecyclerItemsEnterAnimator recyclerItemsEnterAnimator = this.itemsEnterAnimator;
        if (recyclerItemsEnterAnimator != null) {
            recyclerItemsEnterAnimator.onDetached();
        }
    }

    public void addOverlayView(View view, FrameLayout.LayoutParams layoutParams) {
        if (this.overlayContainer == null) {
            this.overlayContainer = new FrameLayout(getContext()) { // from class: org.telegram.ui.Components.RecyclerListView.5
                @Override // android.view.View, android.view.ViewParent
                public void requestLayout() {
                    super.requestLayout();
                    try {
                        measure(View.MeasureSpec.makeMeasureSpec(RecyclerListView.this.getMeasuredWidth(), NUM), View.MeasureSpec.makeMeasureSpec(RecyclerListView.this.getMeasuredHeight(), NUM));
                        layout(0, 0, RecyclerListView.this.overlayContainer.getMeasuredWidth(), RecyclerListView.this.overlayContainer.getMeasuredHeight());
                    } catch (Exception unused) {
                    }
                }
            };
        }
        this.overlayContainer.addView(view, layoutParams);
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

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.fastScrollAnimationRunning) {
            return;
        }
        super.requestLayout();
    }

    public void setAnimateEmptyView(boolean z, int i) {
        this.animateEmptyView = z;
        this.emptyViewAnimationType = i;
    }

    /* loaded from: classes3.dex */
    public static class FoucsableOnTouchListener implements View.OnTouchListener {
        private boolean onFocus;
        private float x;
        private float y;

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            ViewParent parent = view.getParent();
            if (parent == null) {
                return false;
            }
            if (motionEvent.getAction() == 0) {
                this.x = motionEvent.getX();
                this.y = motionEvent.getY();
                this.onFocus = true;
                parent.requestDisallowInterceptTouchEvent(true);
            }
            if (motionEvent.getAction() == 2) {
                float x = this.x - motionEvent.getX();
                float y = this.y - motionEvent.getY();
                float scaledTouchSlop = ViewConfiguration.get(view.getContext()).getScaledTouchSlop();
                if (this.onFocus && Math.sqrt((x * x) + (y * y)) > scaledTouchSlop) {
                    this.onFocus = false;
                    parent.requestDisallowInterceptTouchEvent(false);
                }
            } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                this.onFocus = false;
                parent.requestDisallowInterceptTouchEvent(false);
            }
            return false;
        }
    }

    @Override // android.view.View
    public void setTranslationY(float f) {
        super.setTranslationY(f);
        FastScroll fastScroll = this.fastScroll;
        if (fastScroll != null) {
            fastScroll.setTranslationY(f);
        }
    }

    public void startMultiselect(int i, boolean z, onMultiSelectionChanged onmultiselectionchanged) {
        if (!this.multiSelectionGesture) {
            this.listPaddings = new int[2];
            new HashSet();
            getParent().requestDisallowInterceptTouchEvent(true);
            this.multiSelectionListener = onmultiselectionchanged;
            this.multiSelectionGesture = true;
            this.currentSelectedPosition = i;
            this.startSelectionFrom = i;
        }
        this.useRelativePositions = z;
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        FastScroll fastScroll = this.fastScroll;
        if (fastScroll == null || !fastScroll.pressed) {
            if (this.multiSelectionGesture && motionEvent.getAction() != 0 && motionEvent.getAction() != 1 && motionEvent.getAction() != 3) {
                if (this.lastX == Float.MAX_VALUE && this.lastY == Float.MAX_VALUE) {
                    this.lastX = motionEvent.getX();
                    this.lastY = motionEvent.getY();
                }
                if (!this.multiSelectionGestureStarted && Math.abs(motionEvent.getY() - this.lastY) > this.touchSlop) {
                    this.multiSelectionGestureStarted = true;
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                if (this.multiSelectionGestureStarted) {
                    chekMultiselect(motionEvent.getX(), motionEvent.getY());
                    this.multiSelectionListener.getPaddings(this.listPaddings);
                    if (motionEvent.getY() > (getMeasuredHeight() - AndroidUtilities.dp(56.0f)) - this.listPaddings[1] && (this.currentSelectedPosition >= this.startSelectionFrom || !this.multiSelectionListener.limitReached())) {
                        startMultiselectScroll(false);
                    } else if (motionEvent.getY() < AndroidUtilities.dp(56.0f) + this.listPaddings[0] && (this.currentSelectedPosition <= this.startSelectionFrom || !this.multiSelectionListener.limitReached())) {
                        startMultiselectScroll(true);
                    } else {
                        cancelMultiselectScroll();
                    }
                }
                return true;
            }
            this.lastX = Float.MAX_VALUE;
            this.lastY = Float.MAX_VALUE;
            this.multiSelectionGesture = false;
            this.multiSelectionGestureStarted = false;
            getParent().requestDisallowInterceptTouchEvent(false);
            cancelMultiselectScroll();
            return super.onTouchEvent(motionEvent);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean chekMultiselect(float f, float f2) {
        int measuredHeight = getMeasuredHeight();
        int[] iArr = this.listPaddings;
        float min = Math.min(measuredHeight - iArr[1], Math.max(f2, iArr[0]));
        float min2 = Math.min(getMeasuredWidth(), Math.max(f, 0.0f));
        int i = 0;
        while (true) {
            if (i >= getChildCount()) {
                break;
            }
            this.multiSelectionListener.getPaddings(this.listPaddings);
            if (!this.useRelativePositions) {
                View childAt = getChildAt(i);
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(childAt.getLeft(), childAt.getTop(), childAt.getLeft() + childAt.getMeasuredWidth(), childAt.getTop() + childAt.getMeasuredHeight());
                if (rectF.contains(min2, min)) {
                    int childLayoutPosition = getChildLayoutPosition(childAt);
                    int i2 = this.currentSelectedPosition;
                    if (i2 != childLayoutPosition) {
                        int i3 = this.startSelectionFrom;
                        boolean z = i2 > i3 || childLayoutPosition > i3;
                        childLayoutPosition = this.multiSelectionListener.checkPosition(childLayoutPosition, z);
                        if (z) {
                            int i4 = this.currentSelectedPosition;
                            if (childLayoutPosition <= i4) {
                                while (i4 > childLayoutPosition) {
                                    if (i4 != this.startSelectionFrom && this.multiSelectionListener.canSelect(i4)) {
                                        this.multiSelectionListener.onSelectionChanged(i4, false, min2, min);
                                    }
                                    i4--;
                                }
                            } else if (!this.multiSelectionListener.limitReached()) {
                                for (int i5 = this.currentSelectedPosition + 1; i5 <= childLayoutPosition; i5++) {
                                    if (i5 != this.startSelectionFrom && this.multiSelectionListener.canSelect(i5)) {
                                        this.multiSelectionListener.onSelectionChanged(i5, true, min2, min);
                                    }
                                }
                            }
                        } else {
                            int i6 = this.currentSelectedPosition;
                            if (childLayoutPosition > i6) {
                                while (i6 < childLayoutPosition) {
                                    if (i6 != this.startSelectionFrom && this.multiSelectionListener.canSelect(i6)) {
                                        this.multiSelectionListener.onSelectionChanged(i6, false, min2, min);
                                    }
                                    i6++;
                                }
                            } else if (!this.multiSelectionListener.limitReached()) {
                                for (int i7 = this.currentSelectedPosition - 1; i7 >= childLayoutPosition; i7--) {
                                    if (i7 != this.startSelectionFrom && this.multiSelectionListener.canSelect(i7)) {
                                        this.multiSelectionListener.onSelectionChanged(i7, true, min2, min);
                                    }
                                }
                            }
                        }
                    }
                    if (!this.multiSelectionListener.limitReached()) {
                        this.currentSelectedPosition = childLayoutPosition;
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

    private void startMultiselectScroll(boolean z) {
        this.multiselectScrollToTop = z;
        if (!this.multiselectScrollRunning) {
            this.multiselectScrollRunning = true;
            AndroidUtilities.cancelRunOnUIThread(this.scroller);
            AndroidUtilities.runOnUIThread(this.scroller);
        }
    }

    public boolean isMultiselect() {
        return this.multiSelectionGesture;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Drawable getThemedDrawable(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Drawable drawable = resourcesProvider != null ? resourcesProvider.getDrawable(str) : null;
        return drawable != null ? drawable : Theme.getThemeDrawable(str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Paint getThemedPaint(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Paint paint = resourcesProvider != null ? resourcesProvider.getPaint(str) : null;
        return paint != null ? paint : Theme.getThemePaint(str);
    }

    public void setItemsEnterAnimator(RecyclerItemsEnterAnimator recyclerItemsEnterAnimator) {
        this.itemsEnterAnimator = recyclerItemsEnterAnimator;
    }

    public void setAccessibilityEnabled(boolean z) {
        this.accessibilityEnabled = z;
    }
}
