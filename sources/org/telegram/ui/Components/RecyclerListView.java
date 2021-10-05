package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
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
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.util.Log;
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
    private FastScroll fastScroll;
    public boolean fastScrollAnimationRunning;
    /* access modifiers changed from: private */
    public GestureDetector gestureDetector;
    private ArrayList<View> headers;
    private ArrayList<View> headersCache;
    private boolean hiddenByEmptyView;
    private boolean hideIfEmpty;
    /* access modifiers changed from: private */
    public boolean instantClick;
    /* access modifiers changed from: private */
    public boolean interceptedByChild;
    private boolean isChildViewEnabled;
    private boolean isHidden;
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

    public static abstract class SelectionAdapter extends RecyclerView.Adapter {
        public int getSelectionBottomPadding(View view) {
            return 0;
        }

        public abstract boolean isEnabled(RecyclerView.ViewHolder viewHolder);
    }

    public interface onMultiSelectionChanged {
        boolean canSelect(int i);

        int checkPosition(int i, boolean z);

        void getPaddings(int[] iArr);

        boolean limitReached();

        void onSelectionChanged(int i, boolean z, float f, float f2);

        void scrollBy(int i);
    }

    /* access modifiers changed from: protected */
    public boolean allowSelectChildAtPosition(float f, float f2) {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean allowSelectChildAtPosition(View view) {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean canHighlightChildAt(View view, float f, float f2) {
        return true;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public FastScroll getFastScroll() {
        return this.fastScroll;
    }

    public static abstract class FastScrollAdapter extends SelectionAdapter {
        public abstract String getLetter(int i);

        public abstract int getPositionForScrollProgress(float f);

        public void onFinishFastScroll(RecyclerListView recyclerListView) {
        }

        public void onStartFastScroll() {
        }

        public int getTotalItemsCount() {
            return getItemCount();
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

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return isEnabled(viewHolder, getSectionForPosition(adapterPosition), getPositionInSectionForPosition(adapterPosition));
        }

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
            return getItem(getSectionForPosition(i), getPositionInSectionForPosition(i));
        }

        public final int getItemViewType(int i) {
            return getItemViewType(getSectionForPosition(i), getPositionInSectionForPosition(i));
        }

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
            int sectionCount2 = getSectionCount();
            this.sectionCount = sectionCount2;
            return sectionCount2;
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
                if (i < i4 || i >= internalGetCountForSection) {
                    i3++;
                    i4 = internalGetCountForSection;
                } else {
                    this.sectionCache.put(i, i3);
                    return i3;
                }
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
                if (i < i4 || i >= internalGetCountForSection) {
                    i3++;
                    i4 = internalGetCountForSection;
                } else {
                    int i5 = i - i4;
                    this.sectionPositionCache.put(i, i5);
                    return i5;
                }
            }
            return -1;
        }
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public Holder(View view) {
            super(view);
        }
    }

    public class FastScroll extends View {
        private int activeColor;
        private float bubbleProgress;
        private String currentLetter;
        private int inactiveColor;
        private long lastUpdateTime;
        private float lastY;
        private StaticLayout letterLayout;
        private TextPaint letterPaint = new TextPaint(1);
        private StaticLayout oldLetterLayout;
        private Paint paint = new Paint(1);
        private Paint paint2 = new Paint(1);
        private Path path = new Path();
        /* access modifiers changed from: private */
        public boolean pressed;
        private float progress;
        private float[] radii = new float[8];
        private RectF rect = new RectF();
        private int scrollX;
        private float startDy;
        private float textX;
        private float textY;
        private int type;

        public FastScroll(Context context, int i) {
            super(context);
            float f;
            this.type = i;
            if (i == 0) {
                this.letterPaint.setTextSize((float) AndroidUtilities.dp(45.0f));
            } else {
                this.letterPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
                this.letterPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.paint2.setColor(Theme.getColor("windowBackgroundWhite"));
            }
            for (int i2 = 0; i2 < 8; i2++) {
                this.radii[i2] = (float) AndroidUtilities.dp(44.0f);
            }
            if (LocaleController.isRTL) {
                f = 10.0f;
            } else {
                f = (float) ((i == 0 ? 132 : 240) - 15);
            }
            this.scrollX = AndroidUtilities.dp(f);
            updateColors();
            setFocusableInTouchMode(true);
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

        public boolean onTouchEvent(MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            if (action != 0) {
                if (action != 1) {
                    if (action != 2) {
                        if (action != 3) {
                            return this.pressed;
                        }
                    } else if (!this.pressed) {
                        return true;
                    } else {
                        float y = motionEvent.getY();
                        float dp = ((float) AndroidUtilities.dp(12.0f)) + this.startDy;
                        float measuredHeight = ((float) (getMeasuredHeight() - AndroidUtilities.dp(42.0f))) + this.startDy;
                        if (y < dp) {
                            y = dp;
                        } else if (y > measuredHeight) {
                            y = measuredHeight;
                        }
                        float f = y - this.lastY;
                        this.lastY = y;
                        float measuredHeight2 = this.progress + (f / ((float) (getMeasuredHeight() - AndroidUtilities.dp(54.0f))));
                        this.progress = measuredHeight2;
                        if (measuredHeight2 < 0.0f) {
                            this.progress = 0.0f;
                        } else if (measuredHeight2 > 1.0f) {
                            this.progress = 1.0f;
                        }
                        getCurrentLetter(true);
                        invalidate();
                        return true;
                    }
                }
                this.pressed = false;
                this.lastUpdateTime = System.currentTimeMillis();
                invalidate();
                RecyclerView.Adapter adapter = RecyclerListView.this.getAdapter();
                if (adapter instanceof FastScrollAdapter) {
                    ((FastScrollAdapter) adapter).onFinishFastScroll(RecyclerListView.this);
                }
                return true;
            }
            float x = motionEvent.getX();
            this.lastY = motionEvent.getY();
            float ceil = ((float) Math.ceil((double) (((float) (getMeasuredHeight() - AndroidUtilities.dp(54.0f))) * this.progress))) + ((float) AndroidUtilities.dp(12.0f));
            if ((!LocaleController.isRTL || x <= ((float) AndroidUtilities.dp(25.0f))) && (LocaleController.isRTL || x >= ((float) AndroidUtilities.dp(107.0f)))) {
                float f2 = this.lastY;
                if (f2 >= ceil && f2 <= ((float) AndroidUtilities.dp(30.0f)) + ceil) {
                    this.startDy = this.lastY - ceil;
                    this.pressed = true;
                    this.lastUpdateTime = System.currentTimeMillis();
                    getCurrentLetter(true);
                    invalidate();
                    RecyclerView.Adapter adapter2 = RecyclerListView.this.getAdapter();
                    if (adapter2 instanceof FastScrollAdapter) {
                        ((FastScrollAdapter) adapter2).onStartFastScroll();
                    }
                    return true;
                }
            }
            return false;
        }

        /* access modifiers changed from: private */
        public void getCurrentLetter(boolean z) {
            RecyclerView.LayoutManager layoutManager = RecyclerListView.this.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                if (linearLayoutManager.getOrientation() == 1) {
                    RecyclerView.Adapter adapter = RecyclerListView.this.getAdapter();
                    if (adapter instanceof FastScrollAdapter) {
                        FastScrollAdapter fastScrollAdapter = (FastScrollAdapter) adapter;
                        int positionForScrollProgress = fastScrollAdapter.getPositionForScrollProgress(this.progress);
                        if (z) {
                            linearLayoutManager.scrollToPositionWithOffset(positionForScrollProgress, RecyclerListView.this.sectionOffset);
                        }
                        String letter = fastScrollAdapter.getLetter(positionForScrollProgress);
                        if (letter == null) {
                            StaticLayout staticLayout = this.letterLayout;
                            if (staticLayout != null) {
                                this.oldLetterLayout = staticLayout;
                            }
                            this.letterLayout = null;
                        } else if (!letter.equals(this.currentLetter)) {
                            if (this.type == 0) {
                                this.letterLayout = new StaticLayout(letter, this.letterPaint, 1000, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                            } else {
                                this.letterLayout = new StaticLayout(letter, this.letterPaint, ((int) this.letterPaint.measureText(letter)) + 1, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                            }
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

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(AndroidUtilities.dp(this.type == 0 ? 132.0f : 240.0f), View.MeasureSpec.getSize(i2));
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x010c, code lost:
            if (r15[6] == r12) goto L_0x010e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:24:0x011c, code lost:
            if (r15[4] == r12) goto L_0x0172;
         */
        /* JADX WARNING: Removed duplicated region for block: B:26:0x0120  */
        /* JADX WARNING: Removed duplicated region for block: B:27:0x012c  */
        /* JADX WARNING: Removed duplicated region for block: B:30:0x0143  */
        /* JADX WARNING: Removed duplicated region for block: B:31:0x0149  */
        /* JADX WARNING: Removed duplicated region for block: B:34:0x0150  */
        /* JADX WARNING: Removed duplicated region for block: B:35:0x0153  */
        /* JADX WARNING: Removed duplicated region for block: B:39:0x0177  */
        /* JADX WARNING: Removed duplicated region for block: B:41:0x017b  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onDraw(android.graphics.Canvas r19) {
            /*
                r18 = this;
                r0 = r18
                r1 = r19
                android.graphics.Paint r2 = r0.paint
                int r3 = r0.inactiveColor
                int r4 = r0.activeColor
                float r5 = r0.bubbleProgress
                int r3 = androidx.core.graphics.ColorUtils.blendARGB(r3, r4, r5)
                r2.setColor(r3)
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
                android.graphics.RectF r3 = r0.rect
                r4 = 1073741824(0x40000000, float:2.0)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
                float r6 = (float) r6
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r4)
                float r7 = (float) r7
                android.graphics.Paint r8 = r0.paint
                r1.drawRoundRect(r3, r6, r7, r8)
                int r3 = r0.type
                r6 = 1106247680(0x41var_, float:30.0)
                r7 = 1
                r8 = 1065353216(0x3var_, float:1.0)
                r9 = 0
                if (r3 != 0) goto L_0x019e
                boolean r3 = r0.pressed
                if (r3 != 0) goto L_0x0072
                float r3 = r0.bubbleProgress
                int r3 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
                if (r3 == 0) goto L_0x0232
            L_0x0072:
                android.graphics.Paint r3 = r0.paint
                r4 = 1132396544(0x437var_, float:255.0)
                float r10 = r0.bubbleProgress
                float r10 = r10 * r4
                int r4 = (int) r10
                r3.setAlpha(r4)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r6)
                int r3 = r3 + r2
                r4 = 1110966272(0x42380000, float:46.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                int r2 = r2 - r4
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
                if (r2 > r4) goto L_0x00a0
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r4 = r4 - r2
                float r2 = (float) r4
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
                r17 = r4
                r4 = r2
                r2 = r17
                goto L_0x00a1
            L_0x00a0:
                r4 = 0
            L_0x00a1:
                r5 = 1092616192(0x41200000, float:10.0)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
                float r6 = (float) r6
                float r10 = (float) r2
                r1.translate(r6, r10)
                r6 = 1105723392(0x41e80000, float:29.0)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r10 = (float) r10
                r11 = 1109393408(0x42200000, float:40.0)
                r12 = 1082130432(0x40800000, float:4.0)
                r13 = 1110441984(0x42300000, float:44.0)
                int r10 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
                if (r10 > 0) goto L_0x00d6
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r13)
                float r10 = (float) r10
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
                float r12 = (float) r12
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r6 = (float) r6
                float r4 = r4 / r6
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r11)
                float r6 = (float) r6
                float r4 = r4 * r6
                float r12 = r12 + r4
                goto L_0x00f8
            L_0x00d6:
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r10 = (float) r10
                float r4 = r4 - r10
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r13)
                float r10 = (float) r10
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
                float r12 = (float) r12
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r6 = (float) r6
                float r4 = r4 / r6
                float r4 = r8 - r4
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r11)
                float r6 = (float) r6
                float r4 = r4 * r6
                float r4 = r4 + r12
                r12 = r10
                r10 = r4
            L_0x00f8:
                boolean r4 = org.telegram.messenger.LocaleController.isRTL
                r6 = 4
                r11 = 6
                r13 = 2
                r14 = 0
                if (r4 == 0) goto L_0x010e
                float[] r15 = r0.radii
                r16 = r15[r14]
                int r16 = (r16 > r10 ? 1 : (r16 == r10 ? 0 : -1))
                if (r16 != 0) goto L_0x011e
                r15 = r15[r11]
                int r15 = (r15 > r12 ? 1 : (r15 == r12 ? 0 : -1))
                if (r15 != 0) goto L_0x011e
            L_0x010e:
                if (r4 != 0) goto L_0x0172
                float[] r15 = r0.radii
                r16 = r15[r13]
                int r16 = (r16 > r10 ? 1 : (r16 == r10 ? 0 : -1))
                if (r16 != 0) goto L_0x011e
                r15 = r15[r6]
                int r15 = (r15 > r12 ? 1 : (r15 == r12 ? 0 : -1))
                if (r15 == 0) goto L_0x0172
            L_0x011e:
                if (r4 == 0) goto L_0x012c
                float[] r4 = r0.radii
                r4[r7] = r10
                r4[r14] = r10
                r6 = 7
                r4[r6] = r12
                r4[r11] = r12
                goto L_0x0138
            L_0x012c:
                float[] r4 = r0.radii
                r7 = 3
                r4[r7] = r10
                r4[r13] = r10
                r7 = 5
                r4[r7] = r12
                r4[r6] = r12
            L_0x0138:
                android.graphics.Path r4 = r0.path
                r4.reset()
                android.graphics.RectF r4 = r0.rect
                boolean r6 = org.telegram.messenger.LocaleController.isRTL
                if (r6 == 0) goto L_0x0149
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                float r5 = (float) r5
                goto L_0x014a
            L_0x0149:
                r5 = 0
            L_0x014a:
                boolean r6 = org.telegram.messenger.LocaleController.isRTL
                r7 = 1118830592(0x42b00000, float:88.0)
                if (r6 == 0) goto L_0x0153
                r6 = 1120141312(0x42CLASSNAME, float:98.0)
                goto L_0x0155
            L_0x0153:
                r6 = 1118830592(0x42b00000, float:88.0)
            L_0x0155:
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r6 = (float) r6
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r7 = (float) r7
                r4.set(r5, r9, r6, r7)
                android.graphics.Path r4 = r0.path
                android.graphics.RectF r5 = r0.rect
                float[] r6 = r0.radii
                android.graphics.Path$Direction r7 = android.graphics.Path.Direction.CW
                r4.addRoundRect(r5, r6, r7)
                android.graphics.Path r4 = r0.path
                r4.close()
            L_0x0172:
                android.text.StaticLayout r4 = r0.letterLayout
                if (r4 == 0) goto L_0x0177
                goto L_0x0179
            L_0x0177:
                android.text.StaticLayout r4 = r0.oldLetterLayout
            L_0x0179:
                if (r4 == 0) goto L_0x0232
                r19.save()
                float r5 = r0.bubbleProgress
                int r6 = r0.scrollX
                float r6 = (float) r6
                int r3 = r3 - r2
                float r2 = (float) r3
                r1.scale(r5, r5, r6, r2)
                android.graphics.Path r2 = r0.path
                android.graphics.Paint r3 = r0.paint
                r1.drawPath(r2, r3)
                float r2 = r0.textX
                float r3 = r0.textY
                r1.translate(r2, r3)
                r4.draw(r1)
                r19.restore()
                goto L_0x0232
            L_0x019e:
                if (r3 != r7) goto L_0x0232
                android.text.StaticLayout r2 = r0.letterLayout
                if (r2 == 0) goto L_0x0232
                android.graphics.RectF r2 = r0.rect
                float r2 = r2.centerY()
                android.graphics.RectF r3 = r0.rect
                float r3 = r3.left
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r6 = (float) r6
                float r7 = r0.bubbleProgress
                float r6 = r6 * r7
                float r3 = r3 - r6
                android.text.StaticLayout r6 = r0.letterLayout
                int r6 = r6.getHeight()
                float r6 = (float) r6
                float r6 = r6 / r4
                r7 = 1086324736(0x40CLASSNAME, float:6.0)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r10 = (float) r10
                float r6 = r6 + r10
                android.graphics.RectF r10 = r0.rect
                android.text.StaticLayout r11 = r0.letterLayout
                int r11 = r11.getWidth()
                float r11 = (float) r11
                float r11 = r3 - r11
                r12 = 1108344832(0x42100000, float:36.0)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
                float r12 = (float) r12
                float r11 = r11 - r12
                android.text.StaticLayout r12 = r0.letterLayout
                int r12 = r12.getHeight()
                float r12 = (float) r12
                float r12 = r12 / r4
                float r12 = r2 - r12
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r13 = (float) r13
                float r12 = r12 - r13
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                float r5 = (float) r5
                float r5 = r3 - r5
                android.text.StaticLayout r13 = r0.letterLayout
                int r13 = r13.getHeight()
                float r13 = (float) r13
                float r13 = r13 / r4
                float r13 = r13 + r2
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r7 = (float) r7
                float r13 = r13 + r7
                r10.set(r11, r12, r5, r13)
                android.graphics.RectF r5 = r0.rect
                android.graphics.Paint r7 = r0.paint2
                r1.drawRoundRect(r5, r6, r6, r7)
                r19.save()
                android.text.StaticLayout r5 = r0.letterLayout
                int r5 = r5.getWidth()
                float r5 = (float) r5
                float r3 = r3 - r5
                r5 = 1103101952(0x41CLASSNAME, float:24.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                float r5 = (float) r5
                float r3 = r3 - r5
                android.text.StaticLayout r5 = r0.letterLayout
                int r5 = r5.getHeight()
                float r5 = (float) r5
                float r5 = r5 / r4
                float r2 = r2 - r5
                r1.translate(r3, r2)
                android.text.StaticLayout r2 = r0.letterLayout
                r2.draw(r1)
                r19.restore()
            L_0x0232:
                boolean r1 = r0.pressed
                if (r1 == 0) goto L_0x0240
                android.text.StaticLayout r2 = r0.letterLayout
                if (r2 == 0) goto L_0x0240
                float r2 = r0.bubbleProgress
                int r2 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
                if (r2 < 0) goto L_0x024c
            L_0x0240:
                if (r1 == 0) goto L_0x0246
                android.text.StaticLayout r1 = r0.letterLayout
                if (r1 != 0) goto L_0x028b
            L_0x0246:
                float r1 = r0.bubbleProgress
                int r1 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1))
                if (r1 <= 0) goto L_0x028b
            L_0x024c:
                long r1 = java.lang.System.currentTimeMillis()
                long r3 = r0.lastUpdateTime
                long r3 = r1 - r3
                r5 = 0
                r10 = 17
                int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r7 < 0) goto L_0x0260
                int r5 = (r3 > r10 ? 1 : (r3 == r10 ? 0 : -1))
                if (r5 <= 0) goto L_0x0261
            L_0x0260:
                r3 = r10
            L_0x0261:
                r0.lastUpdateTime = r1
                r18.invalidate()
                boolean r1 = r0.pressed
                r2 = 1123024896(0x42var_, float:120.0)
                if (r1 == 0) goto L_0x027e
                android.text.StaticLayout r1 = r0.letterLayout
                if (r1 == 0) goto L_0x027e
                float r1 = r0.bubbleProgress
                float r3 = (float) r3
                float r3 = r3 / r2
                float r1 = r1 + r3
                r0.bubbleProgress = r1
                int r1 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1))
                if (r1 <= 0) goto L_0x028b
                r0.bubbleProgress = r8
                goto L_0x028b
            L_0x027e:
                float r1 = r0.bubbleProgress
                float r3 = (float) r3
                float r3 = r3 / r2
                float r1 = r1 - r3
                r0.bubbleProgress = r1
                int r1 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1))
                if (r1 >= 0) goto L_0x028b
                r0.bubbleProgress = r9
            L_0x028b:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.RecyclerListView.FastScroll.onDraw(android.graphics.Canvas):void");
        }

        public void layout(int i, int i2, int i3, int i4) {
            if (RecyclerListView.this.selfOnLayout) {
                super.layout(i, i2, i3, i4);
            }
        }

        public void setProgress(float f) {
            this.progress = f;
            invalidate();
        }

        public boolean isPressed() {
            return this.pressed;
        }
    }

    private class RecyclerListViewItemClickListener implements RecyclerView.OnItemTouchListener {
        public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        }

        public RecyclerListViewItemClickListener(Context context) {
            GestureDetector unused = RecyclerListView.this.gestureDetector = new GestureDetector(context, new GestureDetector.OnGestureListener(RecyclerListView.this) {
                public boolean onDown(MotionEvent motionEvent) {
                    return false;
                }

                public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                    return false;
                }

                public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                    return false;
                }

                public void onShowPress(MotionEvent motionEvent) {
                }

                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    if (!(RecyclerListView.this.currentChildView == null || (RecyclerListView.this.onItemClickListener == null && RecyclerListView.this.onItemClickListenerExtended == null))) {
                        final float x = motionEvent.getX();
                        final float y = motionEvent.getY();
                        RecyclerListView recyclerListView = RecyclerListView.this;
                        recyclerListView.onChildPressed(recyclerListView.currentChildView, x, y, true);
                        final View access$300 = RecyclerListView.this.currentChildView;
                        final int access$600 = RecyclerListView.this.currentChildPosition;
                        if (RecyclerListView.this.instantClick && access$600 != -1) {
                            access$300.playSoundEffect(0);
                            access$300.sendAccessibilityEvent(1);
                            if (RecyclerListView.this.onItemClickListener != null) {
                                RecyclerListView.this.onItemClickListener.onItemClick(access$300, access$600);
                            } else if (RecyclerListView.this.onItemClickListenerExtended != null) {
                                RecyclerListView.this.onItemClickListenerExtended.onItemClick(access$300, access$600, x - access$300.getX(), y - access$300.getY());
                            }
                        }
                        AndroidUtilities.runOnUIThread(RecyclerListView.this.clickRunnable = new Runnable() {
                            public void run() {
                                if (this == RecyclerListView.this.clickRunnable) {
                                    Runnable unused = RecyclerListView.this.clickRunnable = null;
                                }
                                View view = access$300;
                                if (view != null) {
                                    RecyclerListView.this.onChildPressed(view, 0.0f, 0.0f, false);
                                    if (!RecyclerListView.this.instantClick) {
                                        access$300.playSoundEffect(0);
                                        access$300.sendAccessibilityEvent(1);
                                        if (access$600 == -1) {
                                            return;
                                        }
                                        if (RecyclerListView.this.onItemClickListener != null) {
                                            RecyclerListView.this.onItemClickListener.onItemClick(access$300, access$600);
                                        } else if (RecyclerListView.this.onItemClickListenerExtended != null) {
                                            OnItemClickListenerExtended access$500 = RecyclerListView.this.onItemClickListenerExtended;
                                            View view2 = access$300;
                                            access$500.onItemClick(view2, access$600, x - view2.getX(), y - access$300.getY());
                                        }
                                    }
                                }
                            }
                        }, (long) ViewConfiguration.getPressedStateDuration());
                        if (RecyclerListView.this.selectChildRunnable != null) {
                            View access$3002 = RecyclerListView.this.currentChildView;
                            AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                            Runnable unused = RecyclerListView.this.selectChildRunnable = null;
                            View unused2 = RecyclerListView.this.currentChildView = null;
                            boolean unused3 = RecyclerListView.this.interceptedByChild = false;
                            RecyclerListView.this.removeSelection(access$3002, motionEvent);
                        }
                    }
                    return true;
                }

                public void onLongPress(MotionEvent motionEvent) {
                    if (RecyclerListView.this.currentChildView != null && RecyclerListView.this.currentChildPosition != -1) {
                        if (RecyclerListView.this.onItemLongClickListener != null || RecyclerListView.this.onItemLongClickListenerExtended != null) {
                            View access$300 = RecyclerListView.this.currentChildView;
                            if (RecyclerListView.this.onItemLongClickListener != null) {
                                if (RecyclerListView.this.onItemLongClickListener.onItemClick(RecyclerListView.this.currentChildView, RecyclerListView.this.currentChildPosition)) {
                                    access$300.performHapticFeedback(0);
                                    access$300.sendAccessibilityEvent(2);
                                }
                            } else if (RecyclerListView.this.onItemLongClickListenerExtended.onItemClick(RecyclerListView.this.currentChildView, RecyclerListView.this.currentChildPosition, motionEvent.getX() - RecyclerListView.this.currentChildView.getX(), motionEvent.getY() - RecyclerListView.this.currentChildView.getY())) {
                                access$300.performHapticFeedback(0);
                                access$300.sendAccessibilityEvent(2);
                                boolean unused = RecyclerListView.this.longPressCalled = true;
                            }
                        }
                    }
                }
            });
            RecyclerListView.this.gestureDetector.setIsLongpressEnabled(false);
        }

        public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            View findChildViewUnder;
            MotionEvent motionEvent2 = motionEvent;
            int actionMasked = motionEvent.getActionMasked();
            boolean z = RecyclerListView.this.getScrollState() == 0;
            if ((actionMasked == 0 || actionMasked == 5) && RecyclerListView.this.currentChildView == null && z) {
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                boolean unused = RecyclerListView.this.longPressCalled = false;
                RecyclerView.ItemAnimator itemAnimator = RecyclerListView.this.getItemAnimator();
                if ((RecyclerListView.this.allowItemsInteractionDuringAnimation || itemAnimator == null || !itemAnimator.isRunning()) && RecyclerListView.this.allowSelectChildAtPosition(x, y) && (findChildViewUnder = RecyclerListView.this.findChildViewUnder(x, y)) != null && RecyclerListView.this.allowSelectChildAtPosition(findChildViewUnder)) {
                    View unused2 = RecyclerListView.this.currentChildView = findChildViewUnder;
                }
                if (RecyclerListView.this.currentChildView instanceof ViewGroup) {
                    float x2 = motionEvent.getX() - ((float) RecyclerListView.this.currentChildView.getLeft());
                    float y2 = motionEvent.getY() - ((float) RecyclerListView.this.currentChildView.getTop());
                    ViewGroup viewGroup = (ViewGroup) RecyclerListView.this.currentChildView;
                    int childCount = viewGroup.getChildCount() - 1;
                    while (true) {
                        if (childCount < 0) {
                            break;
                        }
                        View childAt = viewGroup.getChildAt(childCount);
                        if (x2 >= ((float) childAt.getLeft()) && x2 <= ((float) childAt.getRight()) && y2 >= ((float) childAt.getTop()) && y2 <= ((float) childAt.getBottom()) && childAt.isClickable()) {
                            View unused3 = RecyclerListView.this.currentChildView = null;
                            break;
                        }
                        childCount--;
                    }
                }
                int unused4 = RecyclerListView.this.currentChildPosition = -1;
                if (RecyclerListView.this.currentChildView != null) {
                    RecyclerListView recyclerListView = RecyclerListView.this;
                    int unused5 = recyclerListView.currentChildPosition = recyclerView.getChildPosition(recyclerListView.currentChildView);
                    MotionEvent obtain = MotionEvent.obtain(0, 0, motionEvent.getActionMasked(), motionEvent.getX() - ((float) RecyclerListView.this.currentChildView.getLeft()), motionEvent.getY() - ((float) RecyclerListView.this.currentChildView.getTop()), 0);
                    if (RecyclerListView.this.currentChildView.onTouchEvent(obtain)) {
                        boolean unused6 = RecyclerListView.this.interceptedByChild = true;
                    }
                    obtain.recycle();
                }
            }
            if (RecyclerListView.this.currentChildView != null && !RecyclerListView.this.interceptedByChild) {
                try {
                    RecyclerListView.this.gestureDetector.onTouchEvent(motionEvent2);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            if (actionMasked == 0 || actionMasked == 5) {
                if (RecyclerListView.this.interceptedByChild || RecyclerListView.this.currentChildView == null) {
                    RecyclerListView.this.selectorRect.setEmpty();
                } else {
                    float x3 = motionEvent.getX();
                    float y3 = motionEvent.getY();
                    Runnable unused7 = RecyclerListView.this.selectChildRunnable = new RecyclerListView$RecyclerListViewItemClickListener$$ExternalSyntheticLambda0(this, x3, y3);
                    AndroidUtilities.runOnUIThread(RecyclerListView.this.selectChildRunnable, (long) ViewConfiguration.getTapTimeout());
                    if (RecyclerListView.this.currentChildView.isEnabled()) {
                        RecyclerListView recyclerListView2 = RecyclerListView.this;
                        if (recyclerListView2.canHighlightChildAt(recyclerListView2.currentChildView, x3 - RecyclerListView.this.currentChildView.getX(), y3 - RecyclerListView.this.currentChildView.getY())) {
                            RecyclerListView recyclerListView3 = RecyclerListView.this;
                            recyclerListView3.positionSelector(recyclerListView3.currentChildPosition, RecyclerListView.this.currentChildView);
                            Drawable drawable = RecyclerListView.this.selectorDrawable;
                            if (drawable != null) {
                                Drawable current = drawable.getCurrent();
                                if (current instanceof TransitionDrawable) {
                                    if (RecyclerListView.this.onItemLongClickListener == null && RecyclerListView.this.onItemClickListenerExtended == null) {
                                        ((TransitionDrawable) current).resetTransition();
                                    } else {
                                        ((TransitionDrawable) current).startTransition(ViewConfiguration.getLongPressTimeout());
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
                }
            } else if ((actionMasked == 1 || actionMasked == 6 || actionMasked == 3 || !z) && RecyclerListView.this.currentChildView != null) {
                if (RecyclerListView.this.selectChildRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                    Runnable unused8 = RecyclerListView.this.selectChildRunnable = null;
                }
                View access$300 = RecyclerListView.this.currentChildView;
                RecyclerListView recyclerListView4 = RecyclerListView.this;
                recyclerListView4.onChildPressed(recyclerListView4.currentChildView, 0.0f, 0.0f, false);
                View unused9 = RecyclerListView.this.currentChildView = null;
                boolean unused10 = RecyclerListView.this.interceptedByChild = false;
                RecyclerListView.this.removeSelection(access$300, motionEvent2);
                if ((actionMasked == 1 || actionMasked == 6 || actionMasked == 3) && RecyclerListView.this.onItemLongClickListenerExtended != null && RecyclerListView.this.longPressCalled) {
                    RecyclerListView.this.onItemLongClickListenerExtended.onLongClickRelease();
                    boolean unused11 = RecyclerListView.this.longPressCalled = false;
                }
            }
            return false;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onInterceptTouchEvent$0(float f, float f2) {
            if (RecyclerListView.this.selectChildRunnable != null && RecyclerListView.this.currentChildView != null) {
                RecyclerListView recyclerListView = RecyclerListView.this;
                recyclerListView.onChildPressed(recyclerListView.currentChildView, f, f2, true);
                Runnable unused = RecyclerListView.this.selectChildRunnable = null;
            }
        }

        public void onRequestDisallowInterceptTouchEvent(boolean z) {
            RecyclerListView.this.cancelClickRunnables(true);
        }
    }

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
                if (f >= ((float) childAt.getLeft()) + translationX && f <= ((float) childAt.getRight()) + translationX && f2 >= ((float) childAt.getTop()) + f3 && f2 <= ((float) childAt.getBottom()) + f3) {
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

    /* access modifiers changed from: protected */
    public View getPressedChildView() {
        return this.currentChildView;
    }

    /* access modifiers changed from: protected */
    public void onChildPressed(View view, float f, float f2, boolean z) {
        if (!this.disableHighlightState && view != null) {
            view.setPressed(z);
        }
    }

    /* access modifiers changed from: private */
    public void removeSelection(View view, MotionEvent motionEvent) {
        if (view != null && !this.selectorRect.isEmpty()) {
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
            removeSelection(view, (MotionEvent) null);
        }
        this.selectorRect.setEmpty();
        Runnable runnable2 = this.clickRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            this.clickRunnable = null;
        }
        this.interceptedByChild = false;
    }

    public int[] getResourceDeclareStyleableIntArray(String str, String str2) {
        try {
            Field field = Class.forName(str + ".R$styleable").getField(str2);
            if (field != null) {
                return (int[]) field.get((Object) null);
            }
        } catch (Throwable unused) {
        }
        return null;
    }

    public RecyclerListView(Context context) {
        this(context, (Theme.ResourcesProvider) null);
    }

    @SuppressLint({"PrivateApi"})
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

            public void onItemRangeInserted(int i, int i2) {
                RecyclerListView.this.checkIfEmpty(true);
                if (RecyclerListView.this.pinnedHeader != null && RecyclerListView.this.pinnedHeader.getAlpha() == 0.0f) {
                    int unused = RecyclerListView.this.currentFirst = -1;
                    RecyclerListView.this.invalidateViews();
                }
            }

            public void onItemRangeRemoved(int i, int i2) {
                RecyclerListView.this.checkIfEmpty(true);
            }
        };
        this.scroller = new Runnable() {
            public void run() {
                int i;
                RecyclerListView recyclerListView = RecyclerListView.this;
                recyclerListView.multiSelectionListener.getPaddings(recyclerListView.listPaddings);
                if (RecyclerListView.this.multiselectScrollToTop) {
                    i = -AndroidUtilities.dp(12.0f);
                    RecyclerListView recyclerListView2 = RecyclerListView.this;
                    boolean unused = recyclerListView2.chekMultiselect(0.0f, (float) recyclerListView2.listPaddings[0]);
                } else {
                    i = AndroidUtilities.dp(12.0f);
                    RecyclerListView recyclerListView3 = RecyclerListView.this;
                    boolean unused2 = recyclerListView3.chekMultiselect(0.0f, (float) (recyclerListView3.getMeasuredHeight() - RecyclerListView.this.listPaddings[1]));
                }
                RecyclerListView.this.multiSelectionListener.scrollBy(i);
                RecyclerListView recyclerListView4 = RecyclerListView.this;
                if (recyclerListView4.multiselectScrollRunning) {
                    AndroidUtilities.runOnUIThread(recyclerListView4.scroller);
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
                attributes = getResourceDeclareStyleableIntArray("com.android.internal", "View");
                gotAttributes = true;
            }
            TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributes);
            View.class.getDeclaredMethod("initializeScrollbars", new Class[]{TypedArray.class}).invoke(this, new Object[]{obtainStyledAttributes});
            obtainStyledAttributes.recycle();
        } catch (Throwable th) {
            FileLog.e(th);
        }
        super.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                boolean z = false;
                if (!(i == 0 || RecyclerListView.this.currentChildView == null)) {
                    if (RecyclerListView.this.selectChildRunnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                        Runnable unused = RecyclerListView.this.selectChildRunnable = null;
                    }
                    MotionEvent obtain = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                    try {
                        RecyclerListView.this.gestureDetector.onTouchEvent(obtain);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    RecyclerListView.this.currentChildView.onTouchEvent(obtain);
                    obtain.recycle();
                    View access$300 = RecyclerListView.this.currentChildView;
                    RecyclerListView recyclerListView = RecyclerListView.this;
                    recyclerListView.onChildPressed(recyclerListView.currentChildView, 0.0f, 0.0f, false);
                    View unused2 = RecyclerListView.this.currentChildView = null;
                    RecyclerListView.this.removeSelection(access$300, (MotionEvent) null);
                    boolean unused3 = RecyclerListView.this.interceptedByChild = false;
                }
                if (RecyclerListView.this.onScrollListener != null) {
                    RecyclerListView.this.onScrollListener.onScrollStateChanged(recyclerView, i);
                }
                RecyclerListView recyclerListView2 = RecyclerListView.this;
                if (i == 1 || i == 2) {
                    z = true;
                }
                recyclerListView2.scrollingByUser = z;
            }

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
            }
        });
        addOnItemTouchListener(new RecyclerListViewItemClickListener(context));
    }

    public void setVerticalScrollBarEnabled(boolean z) {
        if (attributes != null) {
            super.setVerticalScrollBarEnabled(z);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.fastScroll != null) {
            int measuredHeight = (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom();
            this.fastScroll.getLayoutParams().height = measuredHeight;
            this.fastScroll.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(132.0f), NUM), View.MeasureSpec.makeMeasureSpec(measuredHeight, NUM));
        }
        this.touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.fastScroll != null) {
            this.selfOnLayout = true;
            int paddingTop = i2 + getPaddingTop();
            if (LocaleController.isRTL) {
                FastScroll fastScroll2 = this.fastScroll;
                fastScroll2.layout(0, paddingTop, fastScroll2.getMeasuredWidth(), this.fastScroll.getMeasuredHeight() + paddingTop);
            } else {
                int measuredWidth = getMeasuredWidth() - this.fastScroll.getMeasuredWidth();
                FastScroll fastScroll3 = this.fastScroll;
                fastScroll3.layout(measuredWidth, paddingTop, fastScroll3.getMeasuredWidth() + measuredWidth, this.fastScroll.getMeasuredHeight() + paddingTop);
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
            drawable.setCallback((Drawable.Callback) null);
        }
        int i2 = this.topBottomSelectorRadius;
        if (i2 > 0) {
            this.selectorDrawable = Theme.createRadSelectorDrawable(i, i2, i2);
        } else {
            int i3 = this.selectorRadius;
            if (i3 > 0) {
                this.selectorDrawable = Theme.createSimpleSelectorRoundRectDrawable(i3, 0, i, -16777216);
            } else {
                int i4 = this.selectorType;
                if (i4 == 2) {
                    this.selectorDrawable = Theme.getSelectorDrawable(i, false);
                } else {
                    this.selectorDrawable = Theme.createSelectorDrawable(i, i4);
                }
            }
        }
        this.selectorDrawable.setCallback(this);
    }

    public void checkSection(boolean z) {
        FastScroll fastScroll2;
        RecyclerView.ViewHolder childViewHolder;
        int adapterPosition;
        int sectionForPosition;
        RecyclerView.ViewHolder childViewHolder2;
        View view;
        int i;
        FastScroll fastScroll3;
        if (((this.scrollingByUser || z) && this.fastScroll != null) || !(this.sectionsType == 0 || this.sectionsAdapter == null)) {
            RecyclerView.LayoutManager layoutManager = getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                if (linearLayoutManager.getOrientation() == 1) {
                    int i2 = 0;
                    if (this.sectionsAdapter != null) {
                        int paddingTop = getPaddingTop();
                        int i3 = this.sectionsType;
                        int i4 = Integer.MAX_VALUE;
                        if (i3 == 1) {
                            int childCount = getChildCount();
                            int i5 = Integer.MAX_VALUE;
                            View view2 = null;
                            int i6 = 0;
                            for (int i7 = 0; i7 < childCount; i7++) {
                                View childAt = getChildAt(i7);
                                int bottom = childAt.getBottom();
                                if (bottom > this.sectionOffset + paddingTop) {
                                    if (bottom < i4) {
                                        view2 = childAt;
                                        i4 = bottom;
                                    }
                                    i6 = Math.max(i6, bottom);
                                    if (bottom >= this.sectionOffset + paddingTop + AndroidUtilities.dp(32.0f) && bottom < i5) {
                                        i5 = bottom;
                                    }
                                }
                            }
                            if (view2 != null && (childViewHolder2 = getChildViewHolder(view2)) != null) {
                                int adapterPosition2 = childViewHolder2.getAdapterPosition();
                                int abs = Math.abs(linearLayoutManager.findLastVisibleItemPosition() - adapterPosition2) + 1;
                                if ((this.scrollingByUser || z) && (fastScroll3 = this.fastScroll) != null && !fastScroll3.isPressed() && (getAdapter() instanceof FastScrollAdapter)) {
                                    this.fastScroll.setProgress(Math.min(1.0f, ((float) adapterPosition2) / ((float) ((this.sectionsAdapter.getTotalItemsCount() - abs) + 1))));
                                }
                                this.headersCache.addAll(this.headers);
                                this.headers.clear();
                                if (this.sectionsAdapter.getItemCount() != 0) {
                                    if (!(this.currentFirst == adapterPosition2 && this.currentVisible == abs)) {
                                        this.currentFirst = adapterPosition2;
                                        this.currentVisible = abs;
                                        this.sectionsCount = 1;
                                        int sectionForPosition2 = this.sectionsAdapter.getSectionForPosition(adapterPosition2);
                                        this.startSection = sectionForPosition2;
                                        int countForSection = (this.sectionsAdapter.getCountForSection(sectionForPosition2) + adapterPosition2) - this.sectionsAdapter.getPositionInSectionForPosition(adapterPosition2);
                                        while (countForSection < adapterPosition2 + abs) {
                                            countForSection += this.sectionsAdapter.getCountForSection(this.startSection + this.sectionsCount);
                                            this.sectionsCount++;
                                        }
                                    }
                                    int i8 = adapterPosition2;
                                    for (int i9 = this.startSection; i9 < this.startSection + this.sectionsCount; i9++) {
                                        if (!this.headersCache.isEmpty()) {
                                            view = this.headersCache.get(0);
                                            this.headersCache.remove(0);
                                        } else {
                                            view = null;
                                        }
                                        View sectionHeaderView = getSectionHeaderView(i9, view);
                                        this.headers.add(sectionHeaderView);
                                        int countForSection2 = this.sectionsAdapter.getCountForSection(i9);
                                        if (i9 == this.startSection) {
                                            int positionInSectionForPosition = this.sectionsAdapter.getPositionInSectionForPosition(i8);
                                            if (positionInSectionForPosition == countForSection2 - 1) {
                                                sectionHeaderView.setTag(Integer.valueOf((-sectionHeaderView.getHeight()) + paddingTop));
                                            } else if (positionInSectionForPosition == countForSection2 - 2) {
                                                View childAt2 = getChildAt(i8 - adapterPosition2);
                                                if (childAt2 != null) {
                                                    i = childAt2.getTop() + paddingTop;
                                                } else {
                                                    i = -AndroidUtilities.dp(100.0f);
                                                }
                                                sectionHeaderView.setTag(Integer.valueOf(Math.min(i, 0)));
                                            } else {
                                                sectionHeaderView.setTag(0);
                                            }
                                            countForSection2 -= this.sectionsAdapter.getPositionInSectionForPosition(adapterPosition2);
                                        } else {
                                            View childAt3 = getChildAt(i8 - adapterPosition2);
                                            if (childAt3 != null) {
                                                sectionHeaderView.setTag(Integer.valueOf(childAt3.getTop() + paddingTop));
                                            } else {
                                                sectionHeaderView.setTag(Integer.valueOf(-AndroidUtilities.dp(100.0f)));
                                            }
                                        }
                                        i8 += countForSection2;
                                    }
                                }
                            }
                        } else if (i3 == 2) {
                            this.pinnedHeaderShadowTargetAlpha = 0.0f;
                            if (this.sectionsAdapter.getItemCount() != 0) {
                                int childCount2 = getChildCount();
                                int i10 = Integer.MAX_VALUE;
                                View view3 = null;
                                int i11 = 0;
                                View view4 = null;
                                for (int i12 = 0; i12 < childCount2; i12++) {
                                    View childAt4 = getChildAt(i12);
                                    int bottom2 = childAt4.getBottom();
                                    if (bottom2 > this.sectionOffset + paddingTop) {
                                        if (bottom2 < i4) {
                                            view3 = childAt4;
                                            i4 = bottom2;
                                        }
                                        i11 = Math.max(i11, bottom2);
                                        if (bottom2 >= this.sectionOffset + paddingTop + AndroidUtilities.dp(32.0f) && bottom2 < i10) {
                                            view4 = childAt4;
                                            i10 = bottom2;
                                        }
                                    }
                                }
                                if (view3 != null && (childViewHolder = getChildViewHolder(view3)) != null && (sectionForPosition = this.sectionsAdapter.getSectionForPosition(adapterPosition)) >= 0) {
                                    if (this.currentFirst != sectionForPosition || this.pinnedHeader == null) {
                                        View sectionHeaderView2 = getSectionHeaderView(sectionForPosition, this.pinnedHeader);
                                        this.pinnedHeader = sectionHeaderView2;
                                        sectionHeaderView2.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), NUM), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 0));
                                        View view5 = this.pinnedHeader;
                                        view5.layout(0, 0, view5.getMeasuredWidth(), this.pinnedHeader.getMeasuredHeight());
                                        this.currentFirst = sectionForPosition;
                                    }
                                    if (!(this.pinnedHeader == null || view4 == null || view4.getClass() == this.pinnedHeader.getClass())) {
                                        this.pinnedHeaderShadowTargetAlpha = 1.0f;
                                    }
                                    int countForSection3 = this.sectionsAdapter.getCountForSection(sectionForPosition);
                                    int positionInSectionForPosition2 = this.sectionsAdapter.getPositionInSectionForPosition((adapterPosition = childViewHolder.getAdapterPosition()));
                                    if (i11 == 0 || i11 >= getMeasuredHeight() - getPaddingBottom()) {
                                        i2 = this.sectionOffset;
                                    }
                                    if (positionInSectionForPosition2 == countForSection3 - 1) {
                                        int height = this.pinnedHeader.getHeight();
                                        int top = ((view3.getTop() - paddingTop) - this.sectionOffset) + view3.getHeight();
                                        int i13 = top < height ? top - height : paddingTop;
                                        if (i13 < 0) {
                                            this.pinnedHeader.setTag(Integer.valueOf(paddingTop + i2 + i13));
                                        } else {
                                            this.pinnedHeader.setTag(Integer.valueOf(paddingTop + i2));
                                        }
                                    } else {
                                        this.pinnedHeader.setTag(Integer.valueOf(paddingTop + i2));
                                    }
                                    invalidate();
                                }
                            }
                        }
                    } else {
                        int findFirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                        Math.abs(linearLayoutManager.findLastVisibleItemPosition() - findFirstVisibleItemPosition);
                        if (findFirstVisibleItemPosition != -1) {
                            if ((this.scrollingByUser || z) && (fastScroll2 = this.fastScroll) != null && !fastScroll2.isPressed()) {
                                RecyclerView.Adapter adapter = getAdapter();
                                if (adapter instanceof FastScrollAdapter) {
                                    float computeVerticalScrollOffset = ((float) computeVerticalScrollOffset()) / ((float) ((((FastScrollAdapter) adapter).getTotalItemsCount() * getChildAt(0).getMeasuredHeight()) - computeVerticalScrollExtent()));
                                    Log.d("kek", "scroll progress" + computeVerticalScrollOffset);
                                    this.fastScroll.setProgress(Math.min(1.0f, computeVerticalScrollOffset));
                                    this.fastScroll.getCurrentLetter(false);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void setListSelectorColor(int i) {
        Theme.setSelectorDrawableColor(this.selectorDrawable, i, true);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener2) {
        this.onItemClickListener = onItemClickListener2;
    }

    public void setOnItemClickListener(OnItemClickListenerExtended onItemClickListenerExtended2) {
        this.onItemClickListenerExtended = onItemClickListenerExtended2;
    }

    public OnItemClickListener getOnItemClickListener() {
        return this.onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener2) {
        this.onItemLongClickListener = onItemLongClickListener2;
        this.gestureDetector.setIsLongpressEnabled(onItemLongClickListener2 != null);
    }

    public void setOnItemLongClickListener(OnItemLongClickListenerExtended onItemLongClickListenerExtended2) {
        this.onItemLongClickListenerExtended = onItemLongClickListenerExtended2;
        this.gestureDetector.setIsLongpressEnabled(onItemLongClickListenerExtended2 != null);
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
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).invalidate();
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
        if (findViewHolderForAdapterPosition != null) {
            positionSelector(findViewHolderForAdapterPosition.getLayoutPosition(), findViewHolderForAdapterPosition.itemView);
            Drawable drawable = this.selectorDrawable;
            if (drawable != null) {
                Drawable current = drawable.getCurrent();
                if (current instanceof TransitionDrawable) {
                    if (this.onItemLongClickListener == null && this.onItemClickListenerExtended == null) {
                        ((TransitionDrawable) current).resetTransition();
                    } else {
                        ((TransitionDrawable) current).startTransition(ViewConfiguration.getLongPressTimeout());
                    }
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    this.selectorDrawable.setHotspot((float) (findViewHolderForAdapterPosition.itemView.getMeasuredWidth() / 2), (float) (findViewHolderForAdapterPosition.itemView.getMeasuredHeight() / 2));
                }
            }
            Drawable drawable2 = this.selectorDrawable;
            if (drawable2 != null && drawable2.isStateful() && this.selectorDrawable.setState(getDrawableStateForSelector())) {
                invalidateDrawable(this.selectorDrawable);
            }
            RecyclerListView$$ExternalSyntheticLambda0 recyclerListView$$ExternalSyntheticLambda0 = new RecyclerListView$$ExternalSyntheticLambda0(this);
            this.removeHighlighSelectionRunnable = recyclerListView$$ExternalSyntheticLambda0;
            AndroidUtilities.runOnUIThread(recyclerListView$$ExternalSyntheticLambda0, 700);
        } else if (z) {
            this.pendingHighlightPosition = intReturnCallback;
        }
    }

    /* access modifiers changed from: private */
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
        if (drawable2 != null && drawable2.isStateful()) {
            this.selectorDrawable.setState(StateSet.NOTHING);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return false;
        }
        if (this.disallowInterceptTouchEvents) {
            requestDisallowInterceptTouchEvent(true);
        }
        OnInterceptTouchListener onInterceptTouchListener2 = this.onInterceptTouchListener;
        if ((onInterceptTouchListener2 == null || !onInterceptTouchListener2.onInterceptTouchEvent(motionEvent)) && !super.onInterceptTouchEvent(motionEvent)) {
            return false;
        }
        return true;
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        View view;
        if (this.sectionsAdapter == null || (view = this.pinnedHeader) == null || view.getAlpha() == 0.0f || !this.pinnedHeader.dispatchTouchEvent(motionEvent)) {
            return super.dispatchTouchEvent(motionEvent);
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void checkIfEmpty(boolean z) {
        if (!this.isHidden) {
            int i = 0;
            if (getAdapter() != null && this.emptyView != null) {
                boolean emptyViewIsVisible = emptyViewIsVisible();
                int i2 = emptyViewIsVisible ? 0 : 8;
                if (!this.animateEmptyView) {
                    z = false;
                }
                if (!z) {
                    this.emptyViewAnimateToVisibility = i2;
                    this.emptyView.setVisibility(i2);
                    this.emptyView.setAlpha(1.0f);
                } else if (this.emptyViewAnimateToVisibility != i2) {
                    this.emptyViewAnimateToVisibility = i2;
                    if (i2 == 0) {
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
                        ViewPropertyAnimator alpha = this.emptyView.animate().alpha(0.0f);
                        if (this.emptyViewAnimationType == 1) {
                            alpha.scaleY(0.7f).scaleX(0.7f);
                        }
                        alpha.setDuration(150).setListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (RecyclerListView.this.emptyView != null) {
                                    RecyclerListView.this.emptyView.setVisibility(8);
                                }
                            }
                        }).start();
                    }
                }
                if (this.hideIfEmpty) {
                    if (emptyViewIsVisible) {
                        i = 4;
                    }
                    if (getVisibility() != i) {
                        setVisibility(i);
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

    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i != 0) {
            this.hiddenByEmptyView = false;
        }
    }

    public void setOnScrollListener(RecyclerView.OnScrollListener onScrollListener2) {
        this.onScrollListener = onScrollListener2;
    }

    public void setHideIfEmpty(boolean z) {
        this.hideIfEmpty = z;
    }

    public RecyclerView.OnScrollListener getOnScrollListener() {
        return this.onScrollListener;
    }

    public void setOnInterceptTouchListener(OnInterceptTouchListener onInterceptTouchListener2) {
        this.onInterceptTouchListener = onInterceptTouchListener2;
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
        FastScroll fastScroll2 = this.fastScroll;
        if (fastScroll2 != null) {
            fastScroll2.setVisibility(z ? 0 : 8);
        }
    }

    public void setSectionsType(int i) {
        this.sectionsType = i;
        if (i == 1) {
            this.headers = new ArrayList<>();
            this.headersCache = new ArrayList<>();
        }
    }

    public void setPinnedSectionOffsetY(int i) {
        this.sectionOffset = i;
        invalidate();
    }

    /* access modifiers changed from: private */
    public void positionSelector(int i, View view) {
        positionSelector(i, view, false, -1.0f, -1.0f);
    }

    private void positionSelector(int i, View view, boolean z, float f, float f2) {
        Runnable runnable = this.removeHighlighSelectionRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.removeHighlighSelectionRunnable = null;
            this.pendingHighlightPosition = null;
        }
        if (this.selectorDrawable != null) {
            boolean z2 = i != this.selectorPosition;
            int selectionBottomPadding = getAdapter() instanceof SelectionAdapter ? ((SelectionAdapter) getAdapter()).getSelectionBottomPadding(view) : 0;
            if (i != -1) {
                this.selectorPosition = i;
            }
            if (this.topBottomSelectorRadius > 0 && getAdapter() != null) {
                Theme.setMaskDrawableRad(this.selectorDrawable, i == 0 ? this.topBottomSelectorRadius : 0, i == getAdapter().getItemCount() + -2 ? this.topBottomSelectorRadius : 0);
            }
            this.selectorRect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom() - selectionBottomPadding);
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
            if (Build.VERSION.SDK_INT >= 21 && z) {
                this.selectorDrawable.setHotspot(f, f2);
            }
        }
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
                removeSelection(view, (MotionEvent) null);
            }
        }
        if (!z) {
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
        int[] onCreateDrawableState = onCreateDrawableState(1);
        onCreateDrawableState[onCreateDrawableState.length - 1] = 16842919;
        return onCreateDrawableState;
    }

    public void onChildAttachedToWindow(View view) {
        if (getAdapter() instanceof SelectionAdapter) {
            RecyclerView.ViewHolder findContainingViewHolder = findContainingViewHolder(view);
            if (findContainingViewHolder != null) {
                view.setEnabled(((SelectionAdapter) getAdapter()).isEnabled(findContainingViewHolder));
            }
        } else {
            view.setEnabled(false);
        }
        super.onChildAttachedToWindow(view);
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
            ViewGroup viewGroup = (ViewGroup) this.fastScroll.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(this.fastScroll);
            }
            ((ViewGroup) getParent()).addView(this.fastScroll);
        }
    }

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
        } catch (NullPointerException unused) {
        }
    }

    public boolean dispatchNestedPreScroll(int i, int i2, int[] iArr, int[] iArr2, int i3) {
        if (!this.longPressCalled) {
            return super.dispatchNestedPreScroll(i, i2, iArr, iArr2, i3);
        }
        OnItemLongClickListenerExtended onItemLongClickListenerExtended2 = this.onItemLongClickListenerExtended;
        if (onItemLongClickListenerExtended2 != null) {
            onItemLongClickListenerExtended2.onMove((float) i, (float) i2);
        }
        iArr[0] = i;
        iArr[1] = i2;
        return true;
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
        if (view != null) {
            if (view.isLayoutRequested() || z) {
                int i = this.sectionsType;
                if (i == 1) {
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    try {
                        view.measure(View.MeasureSpec.makeMeasureSpec(layoutParams.width, NUM), View.MeasureSpec.makeMeasureSpec(layoutParams.height, NUM));
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                } else if (i == 2) {
                    try {
                        view.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                }
                view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        View view;
        super.onSizeChanged(i, i2, i3, i4);
        FrameLayout frameLayout = this.overlayContainer;
        if (frameLayout != null) {
            frameLayout.requestLayout();
        }
        int i5 = this.sectionsType;
        if (i5 == 1) {
            if (this.sectionsAdapter != null && !this.headers.isEmpty()) {
                for (int i6 = 0; i6 < this.headers.size(); i6++) {
                    ensurePinnedHeaderLayout(this.headers.get(i6), true);
                }
            }
        } else if (i5 == 2 && this.sectionsAdapter != null && (view = this.pinnedHeader) != null) {
            ensurePinnedHeaderLayout(view, true);
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        View view;
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
                for (int i2 = 0; i2 < this.headers.size(); i2++) {
                    View view2 = this.headers.get(i2);
                    int save = canvas.save();
                    canvas.translate(LocaleController.isRTL ? (float) (getWidth() - view2.getWidth()) : 0.0f, (float) ((Integer) view2.getTag()).intValue());
                    canvas.clipRect(0, 0, getWidth(), view2.getMeasuredHeight());
                    view2.draw(canvas);
                    canvas.restoreToCount(save);
                }
            }
        } else if (i == 2 && this.sectionsAdapter != null && (view = this.pinnedHeader) != null && view.getAlpha() != 0.0f) {
            int save2 = canvas.save();
            int intValue = ((Integer) this.pinnedHeader.getTag()).intValue();
            if (LocaleController.isRTL) {
                f = (float) (getWidth() - this.pinnedHeader.getWidth());
            }
            canvas.translate(f, (float) intValue);
            Drawable drawable = this.pinnedHeaderShadowDrawable;
            if (drawable != null) {
                drawable.setBounds(0, this.pinnedHeader.getMeasuredHeight(), getWidth(), this.pinnedHeader.getMeasuredHeight() + this.pinnedHeaderShadowDrawable.getIntrinsicHeight());
                this.pinnedHeaderShadowDrawable.setAlpha((int) (this.pinnedHeaderShadowAlpha * 255.0f));
                this.pinnedHeaderShadowDrawable.draw(canvas);
                long elapsedRealtime = SystemClock.elapsedRealtime();
                long min = Math.min(20, elapsedRealtime - this.lastAlphaAnimationTime);
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

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.selectorPosition = -1;
        this.selectorRect.setEmpty();
    }

    public void addOverlayView(View view, FrameLayout.LayoutParams layoutParams) {
        if (this.overlayContainer == null) {
            this.overlayContainer = new FrameLayout(getContext()) {
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

    public void requestLayout() {
        if (!this.fastScrollAnimationRunning) {
            super.requestLayout();
        }
    }

    public void setAnimateEmptyView(boolean z, int i) {
        this.animateEmptyView = z;
        this.emptyViewAnimationType = i;
    }

    public static class FoucsableOnTouchListener implements View.OnTouchListener {
        private boolean onFocus;
        private float x;
        private float y;

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
                float x2 = this.x - motionEvent.getX();
                float y2 = this.y - motionEvent.getY();
                float scaledTouchSlop = (float) ViewConfiguration.get(view.getContext()).getScaledTouchSlop();
                if (this.onFocus && Math.sqrt((double) ((x2 * x2) + (y2 * y2))) > ((double) scaledTouchSlop)) {
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

    public void setTranslationY(float f) {
        super.setTranslationY(f);
        FastScroll fastScroll2 = this.fastScroll;
        if (fastScroll2 != null) {
            fastScroll2.setTranslationY(f);
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

    public boolean onTouchEvent(MotionEvent motionEvent) {
        FastScroll fastScroll2 = this.fastScroll;
        if (fastScroll2 != null && fastScroll2.pressed) {
            return false;
        }
        if (!this.multiSelectionGesture || motionEvent.getAction() == 0 || motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            this.lastX = Float.MAX_VALUE;
            this.lastY = Float.MAX_VALUE;
            this.multiSelectionGesture = false;
            this.multiSelectionGestureStarted = false;
            getParent().requestDisallowInterceptTouchEvent(false);
            cancelMultiselectScroll();
            return super.onTouchEvent(motionEvent);
        }
        if (this.lastX == Float.MAX_VALUE && this.lastY == Float.MAX_VALUE) {
            this.lastX = motionEvent.getX();
            this.lastY = motionEvent.getY();
        }
        if (!this.multiSelectionGestureStarted && Math.abs(motionEvent.getY() - this.lastY) > ((float) this.touchSlop)) {
            this.multiSelectionGestureStarted = true;
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        if (this.multiSelectionGestureStarted) {
            chekMultiselect(motionEvent.getX(), motionEvent.getY());
            this.multiSelectionListener.getPaddings(this.listPaddings);
            if (motionEvent.getY() > ((float) ((getMeasuredHeight() - AndroidUtilities.dp(56.0f)) - this.listPaddings[1])) && (this.currentSelectedPosition >= this.startSelectionFrom || !this.multiSelectionListener.limitReached())) {
                startMultiselectScroll(false);
            } else if (motionEvent.getY() >= ((float) (AndroidUtilities.dp(56.0f) + this.listPaddings[0])) || (this.currentSelectedPosition > this.startSelectionFrom && this.multiSelectionListener.limitReached())) {
                cancelMultiselectScroll();
            } else {
                startMultiselectScroll(true);
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public boolean chekMultiselect(float f, float f2) {
        int measuredHeight = getMeasuredHeight();
        int[] iArr = this.listPaddings;
        float min = Math.min((float) (measuredHeight - iArr[1]), Math.max(f2, (float) iArr[0]));
        float min2 = Math.min((float) getMeasuredWidth(), Math.max(f, 0.0f));
        int i = 0;
        while (true) {
            if (i >= getChildCount()) {
                break;
            }
            this.multiSelectionListener.getPaddings(this.listPaddings);
            if (!this.useRelativePositions) {
                View childAt = getChildAt(i);
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set((float) childAt.getLeft(), (float) childAt.getTop(), (float) (childAt.getLeft() + childAt.getMeasuredWidth()), (float) (childAt.getTop() + childAt.getMeasuredHeight()));
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

    /* access modifiers changed from: protected */
    public int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }

    /* access modifiers changed from: protected */
    public Drawable getThemedDrawable(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Drawable drawable = resourcesProvider2 != null ? resourcesProvider2.getDrawable(str) : null;
        return drawable != null ? drawable : Theme.getThemeDrawable(str);
    }

    /* access modifiers changed from: protected */
    public Paint getThemedPaint(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Paint paint = resourcesProvider2 != null ? resourcesProvider2.getPaint(str) : null;
        return paint != null ? paint : Theme.getThemePaint(str);
    }
}
