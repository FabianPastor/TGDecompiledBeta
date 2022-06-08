package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Property;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_updateDialogFiltersOrder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.RecyclerListView;

public class FilterTabsView extends FrameLayout {
    private final Property<FilterTabsView, Float> COLORS = new AnimationProperties.FloatProperty<FilterTabsView>("animationValue") {
        public void setValue(FilterTabsView filterTabsView, float f) {
            float unused = FilterTabsView.this.animationValue = f;
            FilterTabsView.this.selectorDrawable.setColor(ColorUtils.blendARGB(Theme.getColor(FilterTabsView.this.tabLineColorKey), Theme.getColor(FilterTabsView.this.aTabLineColorKey), f));
            FilterTabsView.this.listView.invalidateViews();
            FilterTabsView.this.listView.invalidate();
            filterTabsView.invalidate();
        }

        public Float get(FilterTabsView filterTabsView) {
            return Float.valueOf(FilterTabsView.this.animationValue);
        }
    };
    /* access modifiers changed from: private */
    public String aActiveTextColorKey;
    /* access modifiers changed from: private */
    public String aBackgroundColorKey;
    /* access modifiers changed from: private */
    public String aTabLineColorKey;
    /* access modifiers changed from: private */
    public String aUnactiveTextColorKey;
    /* access modifiers changed from: private */
    public String activeTextColorKey = "actionBarTabActiveText";
    /* access modifiers changed from: private */
    public ListAdapter adapter;
    /* access modifiers changed from: private */
    public int additionalTabWidth;
    private int allTabsWidth;
    /* access modifiers changed from: private */
    public boolean animatingIndicator;
    /* access modifiers changed from: private */
    public float animatingIndicatorProgress;
    /* access modifiers changed from: private */
    public Runnable animationRunnable = new Runnable() {
        public void run() {
            if (FilterTabsView.this.animatingIndicator) {
                long elapsedRealtime = SystemClock.elapsedRealtime() - FilterTabsView.this.lastAnimationTime;
                if (elapsedRealtime > 17) {
                    elapsedRealtime = 17;
                }
                FilterTabsView.access$2616(FilterTabsView.this, ((float) elapsedRealtime) / 200.0f);
                FilterTabsView filterTabsView = FilterTabsView.this;
                filterTabsView.setAnimationIdicatorProgress(filterTabsView.interpolator.getInterpolation(FilterTabsView.this.animationTime));
                if (FilterTabsView.this.animationTime > 1.0f) {
                    float unused = FilterTabsView.this.animationTime = 1.0f;
                }
                if (FilterTabsView.this.animationTime < 1.0f) {
                    AndroidUtilities.runOnUIThread(FilterTabsView.this.animationRunnable);
                    return;
                }
                boolean unused2 = FilterTabsView.this.animatingIndicator = false;
                FilterTabsView.this.setEnabled(true);
                if (FilterTabsView.this.delegate != null) {
                    FilterTabsView.this.delegate.onPageScrolled(1.0f);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public float animationTime;
    /* access modifiers changed from: private */
    public float animationValue;
    /* access modifiers changed from: private */
    public String backgroundColorKey = "actionBarDefault";
    private AnimatorSet colorChangeAnimator;
    /* access modifiers changed from: private */
    public Paint counterPaint = new Paint(1);
    /* access modifiers changed from: private */
    public int currentPosition;
    /* access modifiers changed from: private */
    public FilterTabsViewDelegate delegate;
    /* access modifiers changed from: private */
    public Paint deletePaint = new TextPaint(1);
    /* access modifiers changed from: private */
    public float editingAnimationProgress;
    private boolean editingForwardAnimation;
    /* access modifiers changed from: private */
    public float editingStartAnimationProgress;
    private SparseIntArray idToPosition = new SparseIntArray(5);
    private boolean ignoreLayout;
    /* access modifiers changed from: private */
    public CubicBezierInterpolator interpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
    private boolean invalidated;
    /* access modifiers changed from: private */
    public boolean isEditing;
    DefaultItemAnimator itemAnimator;
    /* access modifiers changed from: private */
    public long lastAnimationTime;
    private long lastEditingAnimationTime;
    private LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public Drawable lockDrawable;
    /* access modifiers changed from: private */
    public int lockDrawableColor;
    /* access modifiers changed from: private */
    public int manualScrollingToId = -1;
    /* access modifiers changed from: private */
    public int manualScrollingToPosition = -1;
    /* access modifiers changed from: private */
    public boolean orderChanged;
    private SparseIntArray positionToId = new SparseIntArray(5);
    /* access modifiers changed from: private */
    public SparseIntArray positionToStableId = new SparseIntArray(5);
    private SparseIntArray positionToWidth = new SparseIntArray(5);
    private SparseIntArray positionToX = new SparseIntArray(5);
    private int prevLayoutWidth;
    /* access modifiers changed from: private */
    public int previousId;
    /* access modifiers changed from: private */
    public int previousPosition;
    private int scrollingToChild = -1;
    /* access modifiers changed from: private */
    public int selectedTabId = -1;
    private String selectorColorKey = "actionBarTabSelector";
    /* access modifiers changed from: private */
    public GradientDrawable selectorDrawable;
    /* access modifiers changed from: private */
    public String tabLineColorKey = "actionBarTabLine";
    /* access modifiers changed from: private */
    public ArrayList<Tab> tabs = new ArrayList<>();
    /* access modifiers changed from: private */
    public TextPaint textCounterPaint = new TextPaint(1);
    /* access modifiers changed from: private */
    public TextPaint textPaint = new TextPaint(1);
    /* access modifiers changed from: private */
    public String unactiveTextColorKey = "actionBarTabUnactiveText";

    public interface FilterTabsViewDelegate {
        boolean canPerformActions();

        boolean didSelectTab(TabView tabView, boolean z);

        int getTabCounter(int i);

        boolean isTabMenuVisible();

        void onDeletePressed(int i);

        void onPageReorder(int i, int i2);

        void onPageScrolled(float f);

        void onPageSelected(Tab tab, boolean z);

        void onSamePageSelected();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$setIsEditing$2(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    static /* synthetic */ float access$2616(FilterTabsView filterTabsView, float f) {
        float f2 = filterTabsView.animationTime + f;
        filterTabsView.animationTime = f2;
        return f2;
    }

    public int getCurrentTabStableId() {
        return this.positionToStableId.get(this.currentPosition, -1);
    }

    public int getStableId(int i) {
        return this.positionToStableId.get(i, -1);
    }

    public class Tab {
        public int counter;
        public int id;
        public boolean isDefault;
        public boolean isLocked;
        public String title;
        public int titleWidth;

        public Tab(int i, String str) {
            this.id = i;
            this.title = str;
        }

        public int getWidth(boolean z) {
            int i;
            int ceil = (int) Math.ceil((double) FilterTabsView.this.textPaint.measureText(this.title));
            this.titleWidth = ceil;
            if (z) {
                i = FilterTabsView.this.delegate.getTabCounter(this.id);
                if (i < 0) {
                    i = 0;
                }
                if (z) {
                    this.counter = i;
                }
            } else {
                i = this.counter;
            }
            if (i > 0) {
                String format = String.format("%d", new Object[]{Integer.valueOf(i)});
                ceil += Math.max(AndroidUtilities.dp(10.0f), (int) Math.ceil((double) FilterTabsView.this.textCounterPaint.measureText(format))) + AndroidUtilities.dp(10.0f) + AndroidUtilities.dp(6.0f);
            }
            return Math.max(AndroidUtilities.dp(40.0f), ceil);
        }

        public boolean setTitle(String str) {
            if (TextUtils.equals(this.title, str)) {
                return false;
            }
            this.title = str;
            return true;
        }
    }

    public class TabView extends View {
        public boolean animateChange;
        private float animateFromCountWidth;
        private float animateFromCounterWidth;
        int animateFromTabCount;
        /* access modifiers changed from: private */
        public float animateFromTabWidth;
        float animateFromTextX;
        private int animateFromTitleWidth;
        /* access modifiers changed from: private */
        public float animateFromWidth;
        boolean animateTabCounter;
        /* access modifiers changed from: private */
        public boolean animateTabWidth;
        private boolean animateTextChange;
        private boolean animateTextChangeOut;
        boolean animateTextX;
        public ValueAnimator changeAnimator;
        public float changeProgress;
        private int currentPosition;
        /* access modifiers changed from: private */
        public Tab currentTab;
        private String currentText;
        StaticLayout inCounter;
        private int lastCountWidth;
        private float lastCounterWidth;
        int lastTabCount = -1;
        private float lastTabWidth;
        float lastTextX;
        String lastTitle;
        private int lastTitleWidth;
        private float lastWidth;
        /* access modifiers changed from: private */
        public float locIconXOffset;
        StaticLayout outCounter;
        /* access modifiers changed from: private */
        public float progressToLocked;
        /* access modifiers changed from: private */
        public RectF rect = new RectF();
        StaticLayout stableCounter;
        /* access modifiers changed from: private */
        public int tabWidth;
        private int textHeight;
        private StaticLayout textLayout;
        private int textOffsetX;
        private StaticLayout titleAnimateInLayout;
        private StaticLayout titleAnimateOutLayout;
        private StaticLayout titleAnimateStableLayout;
        private float titleXOffset;

        public TabView(Context context) {
            super(context);
        }

        public void setTab(Tab tab, int i) {
            this.currentTab = tab;
            this.currentPosition = i;
            setContentDescription(tab.title);
            requestLayout();
        }

        public int getId() {
            return this.currentTab.id;
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.animateChange = false;
            this.animateTabCounter = false;
            this.animateTextChange = false;
            this.animateTextX = false;
            this.animateTabWidth = false;
            ValueAnimator valueAnimator = this.changeAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.changeAnimator.removeAllUpdateListeners();
                this.changeAnimator.cancel();
                this.changeAnimator = null;
            }
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(this.currentTab.getWidth(false) + AndroidUtilities.dp(32.0f) + FilterTabsView.this.additionalTabWidth, View.MeasureSpec.getSize(i2));
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:177:0x0519  */
        /* JADX WARNING: Removed duplicated region for block: B:183:0x0544  */
        /* JADX WARNING: Removed duplicated region for block: B:187:0x056c  */
        /* JADX WARNING: Removed duplicated region for block: B:198:0x05bc  */
        /* JADX WARNING: Removed duplicated region for block: B:199:0x05c7  */
        /* JADX WARNING: Removed duplicated region for block: B:202:0x05cd  */
        /* JADX WARNING: Removed duplicated region for block: B:205:0x060b  */
        /* JADX WARNING: Removed duplicated region for block: B:208:0x064a  */
        /* JADX WARNING: Removed duplicated region for block: B:210:0x067c  */
        /* JADX WARNING: Removed duplicated region for block: B:231:0x0794  */
        /* JADX WARNING: Removed duplicated region for block: B:243:0x07e5  */
        /* JADX WARNING: Removed duplicated region for block: B:246:0x0801  */
        /* JADX WARNING: Removed duplicated region for block: B:249:0x085f  */
        /* JADX WARNING: Removed duplicated region for block: B:250:0x0892  */
        /* JADX WARNING: Removed duplicated region for block: B:252:? A[RETURN, SYNTHETIC] */
        @android.annotation.SuppressLint({"DrawAllocation"})
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onDraw(android.graphics.Canvas r35) {
            /*
                r34 = this;
                r0 = r34
                r7 = r35
                org.telegram.ui.Components.FilterTabsView$Tab r1 = r0.currentTab
                boolean r1 = r1.isDefault
                r2 = 1
                r3 = 0
                if (r1 == 0) goto L_0x001b
                int r1 = org.telegram.messenger.UserConfig.selectedAccount
                org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
                boolean r1 = r1.isPremium()
                if (r1 == 0) goto L_0x0019
                goto L_0x001b
            L_0x0019:
                r8 = 0
                goto L_0x001c
            L_0x001b:
                r8 = 1
            L_0x001c:
                org.telegram.ui.Components.FilterTabsView$Tab r1 = r0.currentTab
                boolean r1 = r1.isDefault
                if (r1 != 0) goto L_0x0026
                if (r8 == 0) goto L_0x0026
                r1 = 1
                goto L_0x0027
            L_0x0026:
                r1 = 0
            L_0x0027:
                r9 = 0
                r10 = 1073741824(0x40000000, float:2.0)
                if (r8 == 0) goto L_0x0068
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                float r4 = r4.editingAnimationProgress
                int r4 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
                if (r4 == 0) goto L_0x0068
                r35.save()
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                float r4 = r4.editingAnimationProgress
                int r5 = r0.currentPosition
                int r5 = r5 % 2
                if (r5 != 0) goto L_0x0048
                r5 = 1065353216(0x3var_, float:1.0)
                goto L_0x004a
            L_0x0048:
                r5 = -1082130432(0xffffffffbvar_, float:-1.0)
            L_0x004a:
                float r4 = r4 * r5
                r5 = 1059648963(0x3var_f5c3, float:0.66)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                float r5 = (float) r5
                float r5 = r5 * r4
                r7.translate(r5, r9)
                int r5 = r34.getMeasuredWidth()
                float r5 = (float) r5
                float r5 = r5 / r10
                int r6 = r34.getMeasuredHeight()
                float r6 = (float) r6
                float r6 = r6 / r10
                r7.rotate(r4, r5, r6)
            L_0x0068:
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                int r4 = r4.manualScrollingToId
                r5 = -1
                if (r4 == r5) goto L_0x007e
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                int r4 = r4.manualScrollingToId
                org.telegram.ui.Components.FilterTabsView r6 = org.telegram.ui.Components.FilterTabsView.this
                int r6 = r6.selectedTabId
                goto L_0x008a
            L_0x007e:
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                int r4 = r4.selectedTabId
                org.telegram.ui.Components.FilterTabsView r6 = org.telegram.ui.Components.FilterTabsView.this
                int r6 = r6.previousId
            L_0x008a:
                org.telegram.ui.Components.FilterTabsView$Tab r12 = r0.currentTab
                int r12 = r12.id
                java.lang.String r13 = "chats_tabUnreadActiveBackground"
                java.lang.String r14 = "chats_tabUnreadUnactiveBackground"
                if (r12 != r4) goto L_0x00ad
                org.telegram.ui.Components.FilterTabsView r12 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r12 = r12.activeTextColorKey
                org.telegram.ui.Components.FilterTabsView r15 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r15 = r15.aActiveTextColorKey
                org.telegram.ui.Components.FilterTabsView r11 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r11 = r11.unactiveTextColorKey
                org.telegram.ui.Components.FilterTabsView r10 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r10 = r10.aUnactiveTextColorKey
                goto L_0x00ca
            L_0x00ad:
                org.telegram.ui.Components.FilterTabsView r10 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r12 = r10.unactiveTextColorKey
                org.telegram.ui.Components.FilterTabsView r10 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r15 = r10.aUnactiveTextColorKey
                org.telegram.ui.Components.FilterTabsView r10 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r11 = r10.activeTextColorKey
                org.telegram.ui.Components.FilterTabsView r10 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r10 = r10.aUnactiveTextColorKey
                r33 = r14
                r14 = r13
                r13 = r33
            L_0x00ca:
                if (r15 != 0) goto L_0x0110
                org.telegram.ui.Components.FilterTabsView r10 = org.telegram.ui.Components.FilterTabsView.this
                boolean r10 = r10.animatingIndicator
                if (r10 != 0) goto L_0x00dc
                org.telegram.ui.Components.FilterTabsView r10 = org.telegram.ui.Components.FilterTabsView.this
                int r10 = r10.manualScrollingToId
                if (r10 == r5) goto L_0x00e5
            L_0x00dc:
                org.telegram.ui.Components.FilterTabsView$Tab r10 = r0.currentTab
                int r10 = r10.id
                if (r10 == r4) goto L_0x00f4
                if (r10 != r6) goto L_0x00e5
                goto L_0x00f4
            L_0x00e5:
                org.telegram.ui.Components.FilterTabsView r10 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r10 = r10.textPaint
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r12)
                r10.setColor(r11)
                goto L_0x0174
            L_0x00f4:
                org.telegram.ui.Components.FilterTabsView r10 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r10 = r10.textPaint
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
                int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
                org.telegram.ui.Components.FilterTabsView r15 = org.telegram.ui.Components.FilterTabsView.this
                float r15 = r15.animatingIndicatorProgress
                int r11 = androidx.core.graphics.ColorUtils.blendARGB(r11, r12, r15)
                r10.setColor(r11)
                goto L_0x0174
            L_0x0110:
                int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
                int r15 = org.telegram.ui.ActionBar.Theme.getColor(r15)
                org.telegram.ui.Components.FilterTabsView r9 = org.telegram.ui.Components.FilterTabsView.this
                boolean r9 = r9.animatingIndicator
                if (r9 != 0) goto L_0x0128
                org.telegram.ui.Components.FilterTabsView r9 = org.telegram.ui.Components.FilterTabsView.this
                int r9 = r9.manualScrollingToPosition
                if (r9 == r5) goto L_0x0131
            L_0x0128:
                org.telegram.ui.Components.FilterTabsView$Tab r9 = r0.currentTab
                int r9 = r9.id
                if (r9 == r4) goto L_0x0145
                if (r9 != r6) goto L_0x0131
                goto L_0x0145
            L_0x0131:
                org.telegram.ui.Components.FilterTabsView r9 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r9 = r9.textPaint
                org.telegram.ui.Components.FilterTabsView r10 = org.telegram.ui.Components.FilterTabsView.this
                float r10 = r10.animationValue
                int r10 = androidx.core.graphics.ColorUtils.blendARGB(r12, r15, r10)
                r9.setColor(r10)
                goto L_0x0174
            L_0x0145:
                int r9 = org.telegram.ui.ActionBar.Theme.getColor(r11)
                int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
                org.telegram.ui.Components.FilterTabsView r11 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r11 = r11.textPaint
                org.telegram.ui.Components.FilterTabsView r5 = org.telegram.ui.Components.FilterTabsView.this
                float r5 = r5.animationValue
                int r5 = androidx.core.graphics.ColorUtils.blendARGB(r9, r10, r5)
                org.telegram.ui.Components.FilterTabsView r9 = org.telegram.ui.Components.FilterTabsView.this
                float r9 = r9.animationValue
                int r9 = androidx.core.graphics.ColorUtils.blendARGB(r12, r15, r9)
                org.telegram.ui.Components.FilterTabsView r10 = org.telegram.ui.Components.FilterTabsView.this
                float r10 = r10.animatingIndicatorProgress
                int r5 = androidx.core.graphics.ColorUtils.blendARGB(r5, r9, r10)
                r11.setColor(r5)
            L_0x0174:
                int r5 = r0.animateFromTabCount
                if (r5 != 0) goto L_0x017e
                boolean r9 = r0.animateTabCounter
                if (r9 == 0) goto L_0x017e
                r9 = 1
                goto L_0x017f
            L_0x017e:
                r9 = 0
            L_0x017f:
                if (r5 <= 0) goto L_0x018d
                org.telegram.ui.Components.FilterTabsView$Tab r10 = r0.currentTab
                int r10 = r10.counter
                if (r10 != 0) goto L_0x018d
                boolean r10 = r0.animateTabCounter
                if (r10 == 0) goto L_0x018d
                r10 = 1
                goto L_0x018e
            L_0x018d:
                r10 = 0
            L_0x018e:
                if (r5 <= 0) goto L_0x019c
                org.telegram.ui.Components.FilterTabsView$Tab r11 = r0.currentTab
                int r11 = r11.counter
                if (r11 <= 0) goto L_0x019c
                boolean r11 = r0.animateTabCounter
                if (r11 == 0) goto L_0x019c
                r11 = 1
                goto L_0x019d
            L_0x019c:
                r11 = 0
            L_0x019d:
                org.telegram.ui.Components.FilterTabsView$Tab r12 = r0.currentTab
                int r12 = r12.counter
                if (r12 > 0) goto L_0x01aa
                if (r10 == 0) goto L_0x01a6
                goto L_0x01aa
            L_0x01a6:
                r2 = 0
                r15 = r4
                r4 = 0
                goto L_0x01f0
            L_0x01aa:
                java.lang.String r15 = "%d"
                if (r10 == 0) goto L_0x01bb
                java.lang.Object[] r2 = new java.lang.Object[r2]
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                r2[r3] = r5
                java.lang.String r2 = java.lang.String.format(r15, r2)
                goto L_0x01c7
            L_0x01bb:
                java.lang.Object[] r2 = new java.lang.Object[r2]
                java.lang.Integer r5 = java.lang.Integer.valueOf(r12)
                r2[r3] = r5
                java.lang.String r2 = java.lang.String.format(r15, r2)
            L_0x01c7:
                org.telegram.ui.Components.FilterTabsView r5 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r5 = r5.textCounterPaint
                float r5 = r5.measureText(r2)
                r15 = r4
                double r3 = (double) r5
                double r3 = java.lang.Math.ceil(r3)
                int r3 = (int) r3
                float r3 = (float) r3
                r4 = 1092616192(0x41200000, float:10.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
                float r5 = (float) r5
                float r5 = java.lang.Math.max(r5, r3)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                float r4 = (float) r4
                float r5 = r5 + r4
                int r4 = (int) r5
                r33 = r4
                r4 = r3
                r3 = r33
            L_0x01f0:
                r5 = 1101004800(0x41a00000, float:20.0)
                if (r1 == 0) goto L_0x021a
                org.telegram.ui.Components.FilterTabsView r12 = org.telegram.ui.Components.FilterTabsView.this
                boolean r12 = r12.isEditing
                if (r12 != 0) goto L_0x0208
                org.telegram.ui.Components.FilterTabsView r12 = org.telegram.ui.Components.FilterTabsView.this
                float r12 = r12.editingStartAnimationProgress
                r17 = 0
                int r12 = (r12 > r17 ? 1 : (r12 == r17 ? 0 : -1))
                if (r12 == 0) goto L_0x021a
            L_0x0208:
                float r12 = (float) r3
                int r19 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r3 = r19 - r3
                float r3 = (float) r3
                org.telegram.ui.Components.FilterTabsView r5 = org.telegram.ui.Components.FilterTabsView.this
                float r5 = r5.editingStartAnimationProgress
                float r3 = r3 * r5
                float r12 = r12 + r3
                int r3 = (int) r12
            L_0x021a:
                r5 = r3
                org.telegram.ui.Components.FilterTabsView$Tab r3 = r0.currentTab
                int r3 = r3.titleWidth
                r20 = 1086324736(0x40CLASSNAME, float:6.0)
                if (r5 == 0) goto L_0x0238
                if (r10 != 0) goto L_0x0238
                if (r2 == 0) goto L_0x022a
                r12 = 1065353216(0x3var_, float:1.0)
                goto L_0x0230
            L_0x022a:
                org.telegram.ui.Components.FilterTabsView r12 = org.telegram.ui.Components.FilterTabsView.this
                float r12 = r12.editingStartAnimationProgress
            L_0x0230:
                float r12 = r12 * r20
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
                int r12 = r12 + r5
                goto L_0x0239
            L_0x0238:
                r12 = 0
            L_0x0239:
                int r3 = r3 + r12
                r0.tabWidth = r3
                int r3 = r34.getMeasuredWidth()
                int r12 = r0.tabWidth
                int r3 = r3 - r12
                float r3 = (float) r3
                r12 = 1073741824(0x40000000, float:2.0)
                float r3 = r3 / r12
                boolean r12 = r0.animateTextX
                if (r12 == 0) goto L_0x025b
                float r12 = r0.changeProgress
                float r3 = r3 * r12
                r21 = r15
                float r15 = r0.animateFromTextX
                r16 = 1065353216(0x3var_, float:1.0)
                float r12 = r16 - r12
                float r15 = r15 * r12
                float r3 = r3 + r15
                goto L_0x025d
            L_0x025b:
                r21 = r15
            L_0x025d:
                r15 = r3
                org.telegram.ui.Components.FilterTabsView$Tab r3 = r0.currentTab
                java.lang.String r3 = r3.title
                java.lang.String r12 = r0.currentText
                boolean r3 = android.text.TextUtils.equals(r3, r12)
                r22 = 1097859072(0x41700000, float:15.0)
                if (r3 != 0) goto L_0x02b8
                org.telegram.ui.Components.FilterTabsView$Tab r3 = r0.currentTab
                java.lang.String r3 = r3.title
                r0.currentText = r3
                org.telegram.ui.Components.FilterTabsView r12 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r12 = r12.textPaint
                android.graphics.Paint$FontMetricsInt r12 = r12.getFontMetricsInt()
                r23 = r8
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r22)
                r24 = r10
                r10 = 0
                java.lang.CharSequence r26 = org.telegram.messenger.Emoji.replaceEmoji(r3, r12, r8, r10)
                android.text.StaticLayout r3 = new android.text.StaticLayout
                org.telegram.ui.Components.FilterTabsView r8 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r27 = r8.textPaint
                r8 = 1137180672(0x43CLASSNAME, float:400.0)
                int r28 = org.telegram.messenger.AndroidUtilities.dp(r8)
                android.text.Layout$Alignment r29 = android.text.Layout.Alignment.ALIGN_NORMAL
                r30 = 1065353216(0x3var_, float:1.0)
                r31 = 0
                r32 = 0
                r25 = r3
                r25.<init>(r26, r27, r28, r29, r30, r31, r32)
                r0.textLayout = r3
                int r3 = r3.getHeight()
                r0.textHeight = r3
                android.text.StaticLayout r3 = r0.textLayout
                r8 = 0
                float r3 = r3.getLineLeft(r8)
                float r3 = -r3
                int r3 = (int) r3
                r0.textOffsetX = r3
                goto L_0x02bc
            L_0x02b8:
                r23 = r8
                r24 = r10
            L_0x02bc:
                boolean r3 = r0.animateTextChange
                if (r3 == 0) goto L_0x03a0
                float r3 = r0.titleXOffset
                boolean r8 = r0.animateTextChangeOut
                if (r8 == 0) goto L_0x02c9
                float r8 = r0.changeProgress
                goto L_0x02cf
            L_0x02c9:
                float r8 = r0.changeProgress
                r10 = 1065353216(0x3var_, float:1.0)
                float r8 = r10 - r8
            L_0x02cf:
                float r3 = r3 * r8
                android.text.StaticLayout r8 = r0.titleAnimateStableLayout
                if (r8 == 0) goto L_0x02f6
                r35.save()
                int r8 = r0.textOffsetX
                float r8 = (float) r8
                float r8 = r8 + r15
                float r8 = r8 + r3
                int r10 = r34.getMeasuredHeight()
                int r12 = r0.textHeight
                int r10 = r10 - r12
                float r10 = (float) r10
                r12 = 1073741824(0x40000000, float:2.0)
                float r10 = r10 / r12
                r12 = 1065353216(0x3var_, float:1.0)
                float r10 = r10 + r12
                r7.translate(r8, r10)
                android.text.StaticLayout r8 = r0.titleAnimateStableLayout
                r8.draw(r7)
                r35.restore()
            L_0x02f6:
                android.text.StaticLayout r8 = r0.titleAnimateInLayout
                if (r8 == 0) goto L_0x034b
                r35.save()
                org.telegram.ui.Components.FilterTabsView r8 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r8 = r8.textPaint
                int r8 = r8.getAlpha()
                org.telegram.ui.Components.FilterTabsView r10 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r10 = r10.textPaint
                float r12 = (float) r8
                r25 = r4
                boolean r4 = r0.animateTextChangeOut
                if (r4 == 0) goto L_0x031b
                float r4 = r0.changeProgress
                r16 = 1065353216(0x3var_, float:1.0)
                float r4 = r16 - r4
                goto L_0x031d
            L_0x031b:
                float r4 = r0.changeProgress
            L_0x031d:
                float r12 = r12 * r4
                int r4 = (int) r12
                r10.setAlpha(r4)
                int r4 = r0.textOffsetX
                float r4 = (float) r4
                float r4 = r4 + r15
                float r4 = r4 + r3
                int r10 = r34.getMeasuredHeight()
                int r12 = r0.textHeight
                int r10 = r10 - r12
                float r10 = (float) r10
                r12 = 1073741824(0x40000000, float:2.0)
                float r10 = r10 / r12
                r12 = 1065353216(0x3var_, float:1.0)
                float r10 = r10 + r12
                r7.translate(r4, r10)
                android.text.StaticLayout r4 = r0.titleAnimateInLayout
                r4.draw(r7)
                r35.restore()
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r4 = r4.textPaint
                r4.setAlpha(r8)
                goto L_0x034d
            L_0x034b:
                r25 = r4
            L_0x034d:
                android.text.StaticLayout r4 = r0.titleAnimateOutLayout
                if (r4 == 0) goto L_0x03c7
                r35.save()
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r4 = r4.textPaint
                int r4 = r4.getAlpha()
                org.telegram.ui.Components.FilterTabsView r8 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r8 = r8.textPaint
                float r10 = (float) r4
                boolean r12 = r0.animateTextChangeOut
                if (r12 == 0) goto L_0x036c
                float r12 = r0.changeProgress
                goto L_0x0372
            L_0x036c:
                float r12 = r0.changeProgress
                r16 = 1065353216(0x3var_, float:1.0)
                float r12 = r16 - r12
            L_0x0372:
                float r10 = r10 * r12
                int r10 = (int) r10
                r8.setAlpha(r10)
                int r8 = r0.textOffsetX
                float r8 = (float) r8
                float r8 = r8 + r15
                float r8 = r8 + r3
                int r10 = r34.getMeasuredHeight()
                int r12 = r0.textHeight
                int r10 = r10 - r12
                float r10 = (float) r10
                r12 = 1073741824(0x40000000, float:2.0)
                float r10 = r10 / r12
                r12 = 1065353216(0x3var_, float:1.0)
                float r10 = r10 + r12
                r7.translate(r8, r10)
                android.text.StaticLayout r8 = r0.titleAnimateOutLayout
                r8.draw(r7)
                r35.restore()
                org.telegram.ui.Components.FilterTabsView r8 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r8 = r8.textPaint
                r8.setAlpha(r4)
                goto L_0x03c7
            L_0x03a0:
                r25 = r4
                android.text.StaticLayout r3 = r0.textLayout
                if (r3 == 0) goto L_0x03c6
                r35.save()
                int r3 = r0.textOffsetX
                float r3 = (float) r3
                float r3 = r3 + r15
                int r4 = r34.getMeasuredHeight()
                int r8 = r0.textHeight
                int r4 = r4 - r8
                float r4 = (float) r4
                r8 = 1073741824(0x40000000, float:2.0)
                float r4 = r4 / r8
                r8 = 1065353216(0x3var_, float:1.0)
                float r4 = r4 + r8
                r7.translate(r3, r4)
                android.text.StaticLayout r3 = r0.textLayout
                r3.draw(r7)
                r35.restore()
            L_0x03c6:
                r3 = 0
            L_0x03c7:
                if (r9 != 0) goto L_0x03e6
                if (r2 != 0) goto L_0x03e6
                if (r1 == 0) goto L_0x03e1
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                boolean r4 = r4.isEditing
                if (r4 != 0) goto L_0x03e6
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                float r4 = r4.editingStartAnimationProgress
                r8 = 0
                int r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
                if (r4 == 0) goto L_0x03e1
                goto L_0x03e6
            L_0x03e1:
                r10 = r5
            L_0x03e2:
                r4 = r25
                goto L_0x0757
            L_0x03e6:
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r4 = r4.aBackgroundColorKey
                if (r4 != 0) goto L_0x0402
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r4 = r4.textCounterPaint
                org.telegram.ui.Components.FilterTabsView r8 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r8 = r8.backgroundColorKey
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                r4.setColor(r8)
                goto L_0x0429
            L_0x0402:
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r4 = r4.backgroundColorKey
                int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
                org.telegram.ui.Components.FilterTabsView r8 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r8 = r8.aBackgroundColorKey
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                org.telegram.ui.Components.FilterTabsView r10 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r10 = r10.textCounterPaint
                org.telegram.ui.Components.FilterTabsView r12 = org.telegram.ui.Components.FilterTabsView.this
                float r12 = r12.animationValue
                int r4 = androidx.core.graphics.ColorUtils.blendARGB(r4, r8, r12)
                r10.setColor(r4)
            L_0x0429:
                boolean r4 = org.telegram.ui.ActionBar.Theme.hasThemeKey(r13)
                if (r4 == 0) goto L_0x0477
                boolean r4 = org.telegram.ui.ActionBar.Theme.hasThemeKey(r14)
                if (r4 == 0) goto L_0x0477
                int r4 = org.telegram.ui.ActionBar.Theme.getColor(r13)
                org.telegram.ui.Components.FilterTabsView r8 = org.telegram.ui.Components.FilterTabsView.this
                boolean r8 = r8.animatingIndicator
                if (r8 != 0) goto L_0x044a
                org.telegram.ui.Components.FilterTabsView r8 = org.telegram.ui.Components.FilterTabsView.this
                int r8 = r8.manualScrollingToPosition
                r10 = -1
                if (r8 == r10) goto L_0x0455
            L_0x044a:
                org.telegram.ui.Components.FilterTabsView$Tab r8 = r0.currentTab
                int r8 = r8.id
                r10 = r21
                if (r8 == r10) goto L_0x045f
                if (r8 != r6) goto L_0x0455
                goto L_0x045f
            L_0x0455:
                org.telegram.ui.Components.FilterTabsView r6 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.Paint r6 = r6.counterPaint
                r6.setColor(r4)
                goto L_0x048a
            L_0x045f:
                int r6 = org.telegram.ui.ActionBar.Theme.getColor(r14)
                org.telegram.ui.Components.FilterTabsView r8 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.Paint r8 = r8.counterPaint
                org.telegram.ui.Components.FilterTabsView r10 = org.telegram.ui.Components.FilterTabsView.this
                float r10 = r10.animatingIndicatorProgress
                int r4 = androidx.core.graphics.ColorUtils.blendARGB(r6, r4, r10)
                r8.setColor(r4)
                goto L_0x048a
            L_0x0477:
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.Paint r4 = r4.counterPaint
                org.telegram.ui.Components.FilterTabsView r6 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r6 = r6.textPaint
                int r6 = r6.getColor()
                r4.setColor(r6)
            L_0x048a:
                org.telegram.ui.Components.FilterTabsView$Tab r4 = r0.currentTab
                int r4 = r4.titleWidth
                float r6 = (float) r4
                boolean r8 = r0.animateTextChange
                if (r8 == 0) goto L_0x04a2
                int r6 = r0.animateFromTitleWidth
                float r6 = (float) r6
                float r10 = r0.changeProgress
                r12 = 1065353216(0x3var_, float:1.0)
                float r13 = r12 - r10
                float r6 = r6 * r13
                float r4 = (float) r4
                float r4 = r4 * r10
                float r6 = r6 + r4
            L_0x04a2:
                if (r8 == 0) goto L_0x04b5
                android.text.StaticLayout r4 = r0.titleAnimateOutLayout
                if (r4 != 0) goto L_0x04b5
                float r4 = r0.titleXOffset
                float r4 = r15 - r4
                float r4 = r4 + r3
                float r4 = r4 + r6
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r20)
                float r3 = (float) r3
                float r4 = r4 + r3
                goto L_0x04bd
            L_0x04b5:
                float r6 = r6 + r15
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r20)
                float r3 = (float) r3
                float r4 = r6 + r3
            L_0x04bd:
                int r3 = r34.getMeasuredHeight()
                r6 = 1101004800(0x41a00000, float:20.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r6)
                int r3 = r3 - r8
                int r3 = r3 / 2
                r6 = 255(0xff, float:3.57E-43)
                r8 = 1132396544(0x437var_, float:255.0)
                if (r1 == 0) goto L_0x04f8
                org.telegram.ui.Components.FilterTabsView r10 = org.telegram.ui.Components.FilterTabsView.this
                boolean r10 = r10.isEditing
                if (r10 != 0) goto L_0x04e3
                org.telegram.ui.Components.FilterTabsView r10 = org.telegram.ui.Components.FilterTabsView.this
                float r10 = r10.editingStartAnimationProgress
                r12 = 0
                int r10 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
                if (r10 == 0) goto L_0x04f8
            L_0x04e3:
                if (r2 != 0) goto L_0x04f8
                org.telegram.ui.Components.FilterTabsView r10 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.Paint r10 = r10.counterPaint
                org.telegram.ui.Components.FilterTabsView r12 = org.telegram.ui.Components.FilterTabsView.this
                float r12 = r12.editingStartAnimationProgress
                float r12 = r12 * r8
                int r12 = (int) r12
                r10.setAlpha(r12)
                goto L_0x0501
            L_0x04f8:
                org.telegram.ui.Components.FilterTabsView r10 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.Paint r10 = r10.counterPaint
                r10.setAlpha(r6)
            L_0x0501:
                if (r11 == 0) goto L_0x0516
                float r10 = r0.animateFromCountWidth
                float r12 = (float) r5
                int r13 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
                if (r13 == 0) goto L_0x0516
                float r13 = r0.changeProgress
                r14 = 1065353216(0x3var_, float:1.0)
                float r18 = r14 - r13
                float r10 = r10 * r18
                float r12 = r12 * r13
                float r10 = r10 + r12
                goto L_0x0517
            L_0x0516:
                float r10 = (float) r5
            L_0x0517:
                if (r11 == 0) goto L_0x0528
                float r12 = r0.animateFromCounterWidth
                float r13 = r0.changeProgress
                r14 = 1065353216(0x3var_, float:1.0)
                float r18 = r14 - r13
                float r12 = r12 * r18
                float r13 = r13 * r25
                float r12 = r12 + r13
                r25 = r12
            L_0x0528:
                android.graphics.RectF r12 = r0.rect
                float r13 = (float) r3
                float r10 = r10 + r4
                r14 = 1101004800(0x41a00000, float:20.0)
                int r18 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r14 = r3 + r18
                float r14 = (float) r14
                r12.set(r4, r13, r10, r14)
                if (r9 != 0) goto L_0x053c
                if (r24 == 0) goto L_0x0557
            L_0x053c:
                r35.save()
                float r4 = r0.changeProgress
                if (r9 == 0) goto L_0x0544
                goto L_0x0548
            L_0x0544:
                r10 = 1065353216(0x3var_, float:1.0)
                float r4 = r10 - r4
            L_0x0548:
                android.graphics.RectF r10 = r0.rect
                float r10 = r10.centerX()
                android.graphics.RectF r12 = r0.rect
                float r12 = r12.centerY()
                r7.scale(r4, r4, r10, r12)
            L_0x0557:
                android.graphics.RectF r4 = r0.rect
                float r10 = org.telegram.messenger.AndroidUtilities.density
                r12 = 1094189056(0x41380000, float:11.5)
                float r14 = r10 * r12
                float r10 = r10 * r12
                org.telegram.ui.Components.FilterTabsView r12 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.Paint r12 = r12.counterPaint
                r7.drawRoundRect(r4, r14, r10, r12)
                if (r11 == 0) goto L_0x067c
                android.text.StaticLayout r2 = r0.inCounter
                if (r2 == 0) goto L_0x058b
                r2 = 1101004800(0x41a00000, float:20.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                android.text.StaticLayout r3 = r0.inCounter
                r4 = 0
                int r3 = r3.getLineBottom(r4)
                android.text.StaticLayout r10 = r0.inCounter
                int r4 = r10.getLineTop(r4)
            L_0x0583:
                int r3 = r3 - r4
                int r2 = r2 - r3
                float r2 = (float) r2
                r3 = 1073741824(0x40000000, float:2.0)
                float r2 = r2 / r3
                float r13 = r13 + r2
                goto L_0x05ba
            L_0x058b:
                r4 = 0
                android.text.StaticLayout r2 = r0.outCounter
                if (r2 == 0) goto L_0x05a3
                r2 = 1101004800(0x41a00000, float:20.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                android.text.StaticLayout r3 = r0.outCounter
                int r3 = r3.getLineBottom(r4)
                android.text.StaticLayout r10 = r0.outCounter
                int r4 = r10.getLineTop(r4)
                goto L_0x0583
            L_0x05a3:
                android.text.StaticLayout r2 = r0.stableCounter
                if (r2 == 0) goto L_0x05ba
                r2 = 1101004800(0x41a00000, float:20.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                android.text.StaticLayout r3 = r0.stableCounter
                int r3 = r3.getLineBottom(r4)
                android.text.StaticLayout r10 = r0.stableCounter
                int r4 = r10.getLineTop(r4)
                goto L_0x0583
            L_0x05ba:
                if (r1 == 0) goto L_0x05c7
                org.telegram.ui.Components.FilterTabsView r2 = org.telegram.ui.Components.FilterTabsView.this
                float r2 = r2.editingStartAnimationProgress
                r3 = 1065353216(0x3var_, float:1.0)
                float r2 = r3 - r2
                goto L_0x05c9
            L_0x05c7:
                r2 = 1065353216(0x3var_, float:1.0)
            L_0x05c9:
                android.text.StaticLayout r3 = r0.inCounter
                if (r3 == 0) goto L_0x0607
                r35.save()
                org.telegram.ui.Components.FilterTabsView r3 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r3 = r3.textCounterPaint
                float r4 = r2 * r8
                float r10 = r0.changeProgress
                float r4 = r4 * r10
                int r4 = (int) r4
                r3.setAlpha(r4)
                android.graphics.RectF r3 = r0.rect
                float r4 = r3.left
                float r3 = r3.width()
                float r3 = r3 - r25
                r10 = 1073741824(0x40000000, float:2.0)
                float r3 = r3 / r10
                float r4 = r4 + r3
                float r3 = r0.changeProgress
                r10 = 1065353216(0x3var_, float:1.0)
                float r11 = r10 - r3
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r22)
                float r3 = (float) r3
                float r11 = r11 * r3
                float r11 = r11 + r13
                r7.translate(r4, r11)
                android.text.StaticLayout r3 = r0.inCounter
                r3.draw(r7)
                r35.restore()
            L_0x0607:
                android.text.StaticLayout r3 = r0.outCounter
                if (r3 == 0) goto L_0x0646
                r35.save()
                org.telegram.ui.Components.FilterTabsView r3 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r3 = r3.textCounterPaint
                float r4 = r2 * r8
                float r10 = r0.changeProgress
                r11 = 1065353216(0x3var_, float:1.0)
                float r10 = r11 - r10
                float r4 = r4 * r10
                int r4 = (int) r4
                r3.setAlpha(r4)
                android.graphics.RectF r3 = r0.rect
                float r4 = r3.left
                float r3 = r3.width()
                float r3 = r3 - r25
                r10 = 1073741824(0x40000000, float:2.0)
                float r3 = r3 / r10
                float r4 = r4 + r3
                float r3 = r0.changeProgress
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r22)
                int r10 = -r10
                float r10 = (float) r10
                float r3 = r3 * r10
                float r3 = r3 + r13
                r7.translate(r4, r3)
                android.text.StaticLayout r3 = r0.outCounter
                r3.draw(r7)
                r35.restore()
            L_0x0646:
                android.text.StaticLayout r3 = r0.stableCounter
                if (r3 == 0) goto L_0x0672
                r35.save()
                org.telegram.ui.Components.FilterTabsView r3 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r3 = r3.textCounterPaint
                float r2 = r2 * r8
                int r2 = (int) r2
                r3.setAlpha(r2)
                android.graphics.RectF r2 = r0.rect
                float r3 = r2.left
                float r2 = r2.width()
                float r2 = r2 - r25
                r4 = 1073741824(0x40000000, float:2.0)
                float r2 = r2 / r4
                float r3 = r3 + r2
                r7.translate(r3, r13)
                android.text.StaticLayout r2 = r0.stableCounter
                r2.draw(r7)
                r35.restore()
            L_0x0672:
                org.telegram.ui.Components.FilterTabsView r2 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r2 = r2.textCounterPaint
                r2.setAlpha(r6)
                goto L_0x06b5
            L_0x067c:
                if (r2 == 0) goto L_0x06b5
                if (r1 == 0) goto L_0x0696
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r4 = r4.textCounterPaint
                org.telegram.ui.Components.FilterTabsView r6 = org.telegram.ui.Components.FilterTabsView.this
                float r6 = r6.editingStartAnimationProgress
                r10 = 1065353216(0x3var_, float:1.0)
                float r11 = r10 - r6
                float r11 = r11 * r8
                int r6 = (int) r11
                r4.setAlpha(r6)
            L_0x0696:
                android.graphics.RectF r4 = r0.rect
                float r6 = r4.left
                float r4 = r4.width()
                float r4 = r4 - r25
                r10 = 1073741824(0x40000000, float:2.0)
                float r4 = r4 / r10
                float r6 = r6 + r4
                r4 = 1097334784(0x41680000, float:14.5)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                int r3 = r3 + r4
                float r3 = (float) r3
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r4 = r4.textCounterPaint
                r7.drawText(r2, r6, r3, r4)
            L_0x06b5:
                if (r9 != 0) goto L_0x06b9
                if (r24 == 0) goto L_0x06bc
            L_0x06b9:
                r35.restore()
            L_0x06bc:
                if (r1 == 0) goto L_0x03e1
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                boolean r1 = r1.isEditing
                if (r1 != 0) goto L_0x06d1
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                float r1 = r1.editingStartAnimationProgress
                r2 = 0
                int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r1 == 0) goto L_0x03e1
            L_0x06d1:
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.Paint r1 = r1.deletePaint
                org.telegram.ui.Components.FilterTabsView r2 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r2 = r2.textCounterPaint
                int r2 = r2.getColor()
                r1.setColor(r2)
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.Paint r1 = r1.deletePaint
                org.telegram.ui.Components.FilterTabsView r2 = org.telegram.ui.Components.FilterTabsView.this
                float r2 = r2.editingStartAnimationProgress
                float r2 = r2 * r8
                int r2 = (int) r2
                r1.setAlpha(r2)
                r1 = 1077936128(0x40400000, float:3.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                android.graphics.RectF r2 = r0.rect
                float r2 = r2.centerX()
                float r8 = (float) r1
                float r2 = r2 - r8
                android.graphics.RectF r1 = r0.rect
                float r1 = r1.centerY()
                float r3 = r1 - r8
                android.graphics.RectF r1 = r0.rect
                float r1 = r1.centerX()
                float r4 = r1 + r8
                android.graphics.RectF r1 = r0.rect
                float r1 = r1.centerY()
                float r6 = r1 + r8
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.Paint r9 = r1.deletePaint
                r1 = r35
                r10 = r5
                r5 = r6
                r6 = r9
                r1.drawLine(r2, r3, r4, r5, r6)
                android.graphics.RectF r1 = r0.rect
                float r1 = r1.centerX()
                float r2 = r1 - r8
                android.graphics.RectF r1 = r0.rect
                float r1 = r1.centerY()
                float r3 = r1 + r8
                android.graphics.RectF r1 = r0.rect
                float r1 = r1.centerX()
                float r4 = r1 + r8
                android.graphics.RectF r1 = r0.rect
                float r1 = r1.centerY()
                float r5 = r1 - r8
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.Paint r6 = r1.deletePaint
                r1 = r35
                r1.drawLine(r2, r3, r4, r5, r6)
                goto L_0x03e2
            L_0x0757:
                if (r23 == 0) goto L_0x0767
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                float r1 = r1.editingAnimationProgress
                r2 = 0
                int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r1 == 0) goto L_0x0767
                r35.restore()
            L_0x0767:
                r0.lastTextX = r15
                org.telegram.ui.Components.FilterTabsView$Tab r1 = r0.currentTab
                int r2 = r1.counter
                r0.lastTabCount = r2
                java.lang.String r2 = r0.currentText
                r0.lastTitle = r2
                int r1 = r1.titleWidth
                r0.lastTitleWidth = r1
                r0.lastCountWidth = r10
                r0.lastCounterWidth = r4
                int r1 = r0.tabWidth
                float r1 = (float) r1
                r0.lastTabWidth = r1
                int r1 = r34.getMeasuredWidth()
                float r1 = (float) r1
                r0.lastWidth = r1
                org.telegram.ui.Components.FilterTabsView$Tab r1 = r0.currentTab
                boolean r1 = r1.isLocked
                if (r1 != 0) goto L_0x0794
                float r1 = r0.progressToLocked
                r2 = 0
                int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r1 == 0) goto L_0x089b
            L_0x0794:
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.drawable.Drawable r1 = r1.lockDrawable
                if (r1 != 0) goto L_0x07ac
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                android.content.Context r2 = r34.getContext()
                r3 = 2131166023(0x7var_, float:1.794628E38)
                android.graphics.drawable.Drawable r2 = androidx.core.content.ContextCompat.getDrawable(r2, r3)
                android.graphics.drawable.Drawable unused = r1.lockDrawable = r2
            L_0x07ac:
                org.telegram.ui.Components.FilterTabsView$Tab r1 = r0.currentTab
                boolean r1 = r1.isLocked
                r2 = 1037726734(0x3dda740e, float:0.10666667)
                if (r1 == 0) goto L_0x07c1
                float r3 = r0.progressToLocked
                r4 = 1065353216(0x3var_, float:1.0)
                int r5 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
                if (r5 == 0) goto L_0x07c1
                float r3 = r3 + r2
                r0.progressToLocked = r3
                goto L_0x07c8
            L_0x07c1:
                if (r1 != 0) goto L_0x07c8
                float r1 = r0.progressToLocked
                float r1 = r1 - r2
                r0.progressToLocked = r1
            L_0x07c8:
                float r1 = r0.progressToLocked
                r2 = 0
                r3 = 1065353216(0x3var_, float:1.0)
                float r1 = org.telegram.messenger.Utilities.clamp(r1, r3, r2)
                r0.progressToLocked = r1
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r1 = r1.unactiveTextColorKey
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                org.telegram.ui.Components.FilterTabsView r2 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r2 = r2.aUnactiveTextColorKey
                if (r2 == 0) goto L_0x07f9
                org.telegram.ui.Components.FilterTabsView r2 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r2 = r2.aUnactiveTextColorKey
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                org.telegram.ui.Components.FilterTabsView r3 = org.telegram.ui.Components.FilterTabsView.this
                float r3 = r3.animationValue
                int r1 = androidx.core.graphics.ColorUtils.blendARGB(r1, r2, r3)
            L_0x07f9:
                org.telegram.ui.Components.FilterTabsView r2 = org.telegram.ui.Components.FilterTabsView.this
                int r2 = r2.lockDrawableColor
                if (r2 == r1) goto L_0x0816
                org.telegram.ui.Components.FilterTabsView r2 = org.telegram.ui.Components.FilterTabsView.this
                int unused = r2.lockDrawableColor = r1
                org.telegram.ui.Components.FilterTabsView r2 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.drawable.Drawable r2 = r2.lockDrawable
                android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
                android.graphics.PorterDuff$Mode r4 = android.graphics.PorterDuff.Mode.MULTIPLY
                r3.<init>(r1, r4)
                r2.setColorFilter(r3)
            L_0x0816:
                int r1 = r34.getMeasuredWidth()
                org.telegram.ui.Components.FilterTabsView r2 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.drawable.Drawable r2 = r2.lockDrawable
                int r2 = r2.getIntrinsicWidth()
                int r1 = r1 - r2
                float r1 = (float) r1
                r2 = 1073741824(0x40000000, float:2.0)
                float r1 = r1 / r2
                float r2 = r0.locIconXOffset
                float r1 = r1 + r2
                int r1 = (int) r1
                int r2 = r34.getMeasuredHeight()
                r3 = 1094713344(0x41400000, float:12.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r2 = r2 - r3
                org.telegram.ui.Components.FilterTabsView r3 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.drawable.Drawable r3 = r3.lockDrawable
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.drawable.Drawable r4 = r4.lockDrawable
                int r4 = r4.getIntrinsicWidth()
                int r4 = r4 + r1
                org.telegram.ui.Components.FilterTabsView r5 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.drawable.Drawable r5 = r5.lockDrawable
                int r5 = r5.getIntrinsicHeight()
                int r5 = r5 + r2
                r3.setBounds(r1, r2, r4, r5)
                float r1 = r0.progressToLocked
                r2 = 1065353216(0x3var_, float:1.0)
                int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r1 == 0) goto L_0x0892
                r35.save()
                float r1 = r0.progressToLocked
                org.telegram.ui.Components.FilterTabsView r2 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.drawable.Drawable r2 = r2.lockDrawable
                android.graphics.Rect r2 = r2.getBounds()
                int r2 = r2.centerX()
                float r2 = (float) r2
                org.telegram.ui.Components.FilterTabsView r3 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.drawable.Drawable r3 = r3.lockDrawable
                android.graphics.Rect r3 = r3.getBounds()
                int r3 = r3.centerY()
                float r3 = (float) r3
                r7.scale(r1, r1, r2, r3)
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.drawable.Drawable r1 = r1.lockDrawable
                r1.draw(r7)
                r35.restore()
                goto L_0x089b
            L_0x0892:
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.drawable.Drawable r1 = r1.lockDrawable
                r1.draw(r7)
            L_0x089b:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FilterTabsView.TabView.onDraw(android.graphics.Canvas):void");
        }

        public boolean animateChange() {
            boolean z;
            int i;
            String str;
            int i2;
            boolean z2;
            String str2;
            String str3;
            int i3 = this.currentTab.counter;
            int i4 = this.lastTabCount;
            if (i3 != i4) {
                this.animateTabCounter = true;
                this.animateFromTabCount = i4;
                this.animateFromCountWidth = (float) this.lastCountWidth;
                this.animateFromCounterWidth = this.lastCounterWidth;
                if (i4 > 0 && i3 > 0) {
                    String valueOf = String.valueOf(i4);
                    String valueOf2 = String.valueOf(this.currentTab.counter);
                    if (valueOf.length() == valueOf2.length()) {
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(valueOf);
                        SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(valueOf2);
                        SpannableStringBuilder spannableStringBuilder3 = new SpannableStringBuilder(valueOf2);
                        for (int i5 = 0; i5 < valueOf.length(); i5++) {
                            if (valueOf.charAt(i5) == valueOf2.charAt(i5)) {
                                int i6 = i5 + 1;
                                spannableStringBuilder.setSpan(new EmptyStubSpan(), i5, i6, 0);
                                spannableStringBuilder2.setSpan(new EmptyStubSpan(), i5, i6, 0);
                            } else {
                                spannableStringBuilder3.setSpan(new EmptyStubSpan(), i5, i5 + 1, 0);
                            }
                        }
                        int ceil = (int) Math.ceil((double) Theme.dialogs_countTextPaint.measureText(valueOf));
                        this.outCounter = new StaticLayout(spannableStringBuilder, FilterTabsView.this.textCounterPaint, ceil, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                        int i7 = ceil;
                        this.stableCounter = new StaticLayout(spannableStringBuilder3, FilterTabsView.this.textCounterPaint, i7, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                        this.inCounter = new StaticLayout(spannableStringBuilder2, FilterTabsView.this.textCounterPaint, i7, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                    } else {
                        this.outCounter = new StaticLayout(valueOf, FilterTabsView.this.textCounterPaint, (int) Math.ceil((double) Theme.dialogs_countTextPaint.measureText(valueOf)), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                        this.inCounter = new StaticLayout(valueOf2, FilterTabsView.this.textCounterPaint, (int) Math.ceil((double) Theme.dialogs_countTextPaint.measureText(valueOf2)), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                    }
                }
                z = true;
            } else {
                z = false;
            }
            int i8 = this.currentTab.counter;
            if (i8 > 0) {
                str = String.format("%d", new Object[]{Integer.valueOf(i8)});
                i = Math.max(AndroidUtilities.dp(10.0f), (int) Math.ceil((double) FilterTabsView.this.textCounterPaint.measureText(str))) + AndroidUtilities.dp(10.0f);
            } else {
                str = null;
                i = 0;
            }
            int i9 = this.currentTab.titleWidth;
            if (i != 0) {
                i2 = i + AndroidUtilities.dp((str != null ? 1.0f : FilterTabsView.this.editingStartAnimationProgress) * 6.0f);
            } else {
                i2 = 0;
            }
            int i10 = i9 + i2;
            float f = this.lastTextX;
            if (((float) ((getMeasuredWidth() - i10) / 2)) != f) {
                this.animateTextX = true;
                this.animateFromTextX = f;
                z = true;
            }
            String str4 = this.lastTitle;
            if (str4 != null && !this.currentTab.title.equals(str4)) {
                if (this.lastTitle.length() > this.currentTab.title.length()) {
                    str3 = this.lastTitle;
                    str2 = this.currentTab.title;
                    z2 = true;
                } else {
                    str3 = this.currentTab.title;
                    str2 = this.lastTitle;
                    z2 = false;
                }
                int indexOf = str3.indexOf(str2);
                float f2 = 0.0f;
                if (indexOf >= 0) {
                    CharSequence replaceEmoji = Emoji.replaceEmoji(str3, FilterTabsView.this.textPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false);
                    SpannableStringBuilder spannableStringBuilder4 = new SpannableStringBuilder(replaceEmoji);
                    SpannableStringBuilder spannableStringBuilder5 = new SpannableStringBuilder(replaceEmoji);
                    if (indexOf != 0) {
                        spannableStringBuilder5.setSpan(new EmptyStubSpan(), 0, indexOf, 0);
                    }
                    if (str2.length() + indexOf != str3.length()) {
                        spannableStringBuilder5.setSpan(new EmptyStubSpan(), str2.length() + indexOf, str3.length(), 0);
                    }
                    spannableStringBuilder4.setSpan(new EmptyStubSpan(), indexOf, str2.length() + indexOf, 0);
                    this.titleAnimateInLayout = new StaticLayout(spannableStringBuilder4, FilterTabsView.this.textPaint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    StaticLayout staticLayout = new StaticLayout(spannableStringBuilder5, FilterTabsView.this.textPaint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.titleAnimateStableLayout = staticLayout;
                    this.animateTextChange = true;
                    this.animateTextChangeOut = z2;
                    if (indexOf != 0) {
                        f2 = -staticLayout.getPrimaryHorizontal(indexOf);
                    }
                    this.titleXOffset = f2;
                    this.animateFromTitleWidth = this.lastTitleWidth;
                    this.titleAnimateOutLayout = null;
                } else {
                    this.titleAnimateInLayout = new StaticLayout(this.currentTab.title, FilterTabsView.this.textPaint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.titleAnimateOutLayout = new StaticLayout(this.lastTitle, FilterTabsView.this.textPaint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.titleAnimateStableLayout = null;
                    this.animateTextChange = true;
                    this.titleXOffset = 0.0f;
                    this.animateFromTitleWidth = this.lastTitleWidth;
                }
                z = true;
            }
            if (((float) i10) == this.lastTabWidth && ((float) getMeasuredWidth()) == this.lastWidth) {
                return z;
            }
            this.animateTabWidth = true;
            this.animateFromTabWidth = this.lastTabWidth;
            this.animateFromWidth = this.lastWidth;
            return true;
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setSelected((this.currentTab == null || FilterTabsView.this.selectedTabId == -1 || this.currentTab.id != FilterTabsView.this.selectedTabId) ? false : true);
            accessibilityNodeInfo.addAction(16);
            if (Build.VERSION.SDK_INT >= 21) {
                accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(32, LocaleController.getString("AccDescrOpenMenu2", NUM)));
            } else {
                accessibilityNodeInfo.addAction(32);
            }
            if (this.currentTab != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(this.currentTab.title);
                Tab tab = this.currentTab;
                int i = tab != null ? tab.counter : 0;
                if (i > 0) {
                    sb.append("\n");
                    sb.append(LocaleController.formatPluralString("AccDescrUnreadCount", i, new Object[0]));
                }
                accessibilityNodeInfo.setContentDescription(sb);
            }
        }

        public void clearTransitionParams() {
            this.animateChange = false;
            this.animateTabCounter = false;
            this.animateTextChange = false;
            this.animateTextX = false;
            this.animateTabWidth = false;
            this.changeAnimator = null;
            invalidate();
        }

        public void shakeLockIcon(final float f, final int i) {
            if (i == 6) {
                this.locIconXOffset = 0.0f;
                return;
            }
            AnimatorSet animatorSet = new AnimatorSet();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, (float) AndroidUtilities.dp(f)});
            ofFloat.addUpdateListener(new FilterTabsView$TabView$$ExternalSyntheticLambda0(this));
            animatorSet.playTogether(new Animator[]{ofFloat});
            animatorSet.setDuration(50);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    TabView tabView = TabView.this;
                    int i = i;
                    tabView.shakeLockIcon(i == 5 ? 0.0f : -f, i + 1);
                    float unused = TabView.this.locIconXOffset = 0.0f;
                    TabView.this.invalidate();
                }
            });
            animatorSet.start();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$shakeLockIcon$0(ValueAnimator valueAnimator) {
            this.locIconXOffset = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidate();
        }
    }

    public FilterTabsView(Context context) {
        super(context);
        this.textCounterPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        this.textCounterPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.deletePaint.setStyle(Paint.Style.STROKE);
        this.deletePaint.setStrokeCap(Paint.Cap.ROUND);
        this.deletePaint.setStrokeWidth((float) AndroidUtilities.dp(1.5f));
        this.selectorDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, (int[]) null);
        float dpf2 = AndroidUtilities.dpf2(3.0f);
        this.selectorDrawable.setCornerRadii(new float[]{dpf2, dpf2, dpf2, dpf2, 0.0f, 0.0f, 0.0f, 0.0f});
        this.selectorDrawable.setColor(Theme.getColor(this.tabLineColorKey));
        setHorizontalScrollBarEnabled(false);
        AnonymousClass3 r3 = new RecyclerListView(context) {
            public void setAlpha(float f) {
                super.setAlpha(f);
                FilterTabsView.this.invalidate();
            }

            /* access modifiers changed from: protected */
            public boolean allowSelectChildAtPosition(View view) {
                return FilterTabsView.this.isEnabled() && FilterTabsView.this.delegate.canPerformActions();
            }

            /* access modifiers changed from: protected */
            public boolean canHighlightChildAt(View view, float f, float f2) {
                if (FilterTabsView.this.isEditing) {
                    TabView tabView = (TabView) view;
                    float dp = (float) AndroidUtilities.dp(6.0f);
                    if (tabView.rect.left - dp < f && tabView.rect.right + dp > f) {
                        return false;
                    }
                }
                return super.canHighlightChildAt(view, f, f2);
            }
        };
        this.listView = r3;
        r3.setClipChildren(false);
        AnonymousClass4 r32 = new DefaultItemAnimator() {
            public void runPendingAnimations() {
                boolean z = !this.mPendingRemovals.isEmpty();
                boolean z2 = !this.mPendingMoves.isEmpty();
                boolean z3 = !this.mPendingChanges.isEmpty();
                boolean z4 = !this.mPendingAdditions.isEmpty();
                if (z || z2 || z4 || z3) {
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.1f});
                    ofFloat.addUpdateListener(new FilterTabsView$4$$ExternalSyntheticLambda0(this));
                    ofFloat.setDuration(getMoveDuration());
                    ofFloat.start();
                }
                super.runPendingAnimations();
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$runPendingAnimations$0(ValueAnimator valueAnimator) {
                FilterTabsView.this.listView.invalidate();
                FilterTabsView.this.invalidate();
            }

            public boolean animateMove(RecyclerView.ViewHolder viewHolder, RecyclerView.ItemAnimator.ItemHolderInfo itemHolderInfo, int i, int i2, int i3, int i4) {
                View view = viewHolder.itemView;
                if (!(view instanceof TabView)) {
                    return super.animateMove(viewHolder, itemHolderInfo, i, i2, i3, i4);
                }
                int translationX = i + ((int) view.getTranslationX());
                int translationY = i2 + ((int) viewHolder.itemView.getTranslationY());
                resetAnimation(viewHolder);
                int i5 = i3 - translationX;
                int i6 = i4 - translationY;
                if (i5 != 0) {
                    view.setTranslationX((float) (-i5));
                }
                if (i6 != 0) {
                    view.setTranslationY((float) (-i6));
                }
                TabView tabView = (TabView) viewHolder.itemView;
                boolean animateChange = tabView.animateChange();
                if (animateChange) {
                    tabView.changeProgress = 0.0f;
                    tabView.animateChange = true;
                    FilterTabsView.this.invalidate();
                }
                if (i5 == 0 && i6 == 0 && !animateChange) {
                    dispatchMoveFinished(viewHolder);
                    return false;
                }
                this.mPendingMoves.add(new DefaultItemAnimator.MoveInfo(viewHolder, translationX, translationY, i3, i4));
                return true;
            }

            /* access modifiers changed from: protected */
            public void animateMoveImpl(RecyclerView.ViewHolder viewHolder, DefaultItemAnimator.MoveInfo moveInfo) {
                super.animateMoveImpl(viewHolder, moveInfo);
                View view = viewHolder.itemView;
                if (view instanceof TabView) {
                    final TabView tabView = (TabView) view;
                    if (tabView.animateChange) {
                        ValueAnimator valueAnimator = tabView.changeAnimator;
                        if (valueAnimator != null) {
                            valueAnimator.removeAllListeners();
                            tabView.changeAnimator.removeAllUpdateListeners();
                            tabView.changeAnimator.cancel();
                        }
                        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                        ofFloat.addUpdateListener(new FilterTabsView$4$$ExternalSyntheticLambda1(tabView));
                        ofFloat.addListener(new AnimatorListenerAdapter(this) {
                            public void onAnimationEnd(Animator animator) {
                                tabView.clearTransitionParams();
                            }
                        });
                        tabView.changeAnimator = ofFloat;
                        ofFloat.setDuration(getMoveDuration());
                        ofFloat.start();
                    }
                }
            }

            /* access modifiers changed from: private */
            public static /* synthetic */ void lambda$animateMoveImpl$1(TabView tabView, ValueAnimator valueAnimator) {
                tabView.changeProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                tabView.invalidate();
            }

            public void onMoveFinished(RecyclerView.ViewHolder viewHolder) {
                super.onMoveFinished(viewHolder);
                viewHolder.itemView.setTranslationX(0.0f);
                View view = viewHolder.itemView;
                if (view instanceof TabView) {
                    ((TabView) view).clearTransitionParams();
                }
            }

            public void endAnimation(RecyclerView.ViewHolder viewHolder) {
                super.endAnimation(viewHolder);
                viewHolder.itemView.setTranslationX(0.0f);
                View view = viewHolder.itemView;
                if (view instanceof TabView) {
                    ((TabView) view).clearTransitionParams();
                }
            }
        };
        this.itemAnimator = r32;
        r32.setDelayAnimations(false);
        this.listView.setItemAnimator(this.itemAnimator);
        this.listView.setSelectorType(8);
        this.listView.setSelectorRadius(6);
        this.listView.setSelectorDrawableColor(Theme.getColor(this.selectorColorKey));
        RecyclerListView recyclerListView = this.listView;
        AnonymousClass5 r33 = new LinearLayoutManager(context, 0, false) {
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }

            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i) {
                AnonymousClass1 r2 = new LinearSmoothScroller(recyclerView.getContext()) {
                    /* access modifiers changed from: protected */
                    public void onTargetFound(View view, RecyclerView.State state, RecyclerView.SmoothScroller.Action action) {
                        int calculateDxToMakeVisible = calculateDxToMakeVisible(view, getHorizontalSnapPreference());
                        if (calculateDxToMakeVisible > 0 || (calculateDxToMakeVisible == 0 && view.getLeft() - AndroidUtilities.dp(21.0f) < 0)) {
                            calculateDxToMakeVisible += AndroidUtilities.dp(60.0f);
                        } else if (calculateDxToMakeVisible < 0 || (calculateDxToMakeVisible == 0 && view.getRight() + AndroidUtilities.dp(21.0f) > FilterTabsView.this.getMeasuredWidth())) {
                            calculateDxToMakeVisible -= AndroidUtilities.dp(60.0f);
                        }
                        int calculateDyToMakeVisible = calculateDyToMakeVisible(view, getVerticalSnapPreference());
                        int max = Math.max(180, calculateTimeForDeceleration((int) Math.sqrt((double) ((calculateDxToMakeVisible * calculateDxToMakeVisible) + (calculateDyToMakeVisible * calculateDyToMakeVisible)))));
                        if (max > 0) {
                            action.update(-calculateDxToMakeVisible, -calculateDyToMakeVisible, max, this.mDecelerateInterpolator);
                        }
                    }
                };
                r2.setTargetPosition(i);
                startSmoothScroll(r2);
            }

            public int scrollHorizontallyBy(int i, RecyclerView.Recycler recycler, RecyclerView.State state) {
                if (FilterTabsView.this.delegate.isTabMenuVisible()) {
                    i = 0;
                }
                return super.scrollHorizontallyBy(i, recycler, state);
            }
        };
        this.layoutManager = r33;
        recyclerListView.setLayoutManager(r33);
        new ItemTouchHelper(new TouchHelperCallback()).attachToRecyclerView(this.listView);
        this.listView.setPadding(AndroidUtilities.dp(7.0f), 0, AndroidUtilities.dp(7.0f), 0);
        this.listView.setClipToPadding(false);
        this.listView.setDrawSelectorBehind(true);
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        listAdapter.setHasStableIds(true);
        this.listView.setAdapter(this.adapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new FilterTabsView$$ExternalSyntheticLambda1(this));
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new FilterTabsView$$ExternalSyntheticLambda2(this));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                FilterTabsView.this.invalidate();
            }
        });
        addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view, int i, float f, float f2) {
        FilterTabsViewDelegate filterTabsViewDelegate;
        if (this.delegate.canPerformActions()) {
            TabView tabView = (TabView) view;
            if (this.isEditing) {
                if (i != 0) {
                    float dp = (float) AndroidUtilities.dp(6.0f);
                    if (tabView.rect.left - dp < f && tabView.rect.right + dp > f) {
                        this.delegate.onDeletePressed(tabView.currentTab.id);
                    }
                }
            } else if (i != this.currentPosition || (filterTabsViewDelegate = this.delegate) == null) {
                scrollToTab(tabView.currentTab, i);
            } else {
                filterTabsViewDelegate.onSamePageSelected();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$1(View view, int i) {
        if (this.delegate.canPerformActions() && !this.isEditing) {
            if (this.delegate.didSelectTab((TabView) view, i == this.currentPosition)) {
                this.listView.hideSelector(true);
                return true;
            }
        }
        return false;
    }

    public void setDelegate(FilterTabsViewDelegate filterTabsViewDelegate) {
        this.delegate = filterTabsViewDelegate;
    }

    public boolean isAnimatingIndicator() {
        return this.animatingIndicator;
    }

    private void scrollToTab(Tab tab, int i) {
        if (tab.isLocked) {
            FilterTabsViewDelegate filterTabsViewDelegate = this.delegate;
            if (filterTabsViewDelegate != null) {
                filterTabsViewDelegate.onPageSelected(tab, false);
                return;
            }
            return;
        }
        int i2 = this.currentPosition;
        boolean z = i2 < i;
        this.scrollingToChild = -1;
        this.previousPosition = i2;
        this.previousId = this.selectedTabId;
        this.currentPosition = i;
        this.selectedTabId = tab.id;
        if (this.animatingIndicator) {
            AndroidUtilities.cancelRunOnUIThread(this.animationRunnable);
            this.animatingIndicator = false;
        }
        this.animationTime = 0.0f;
        this.animatingIndicatorProgress = 0.0f;
        this.animatingIndicator = true;
        setEnabled(false);
        AndroidUtilities.runOnUIThread(this.animationRunnable, 16);
        FilterTabsViewDelegate filterTabsViewDelegate2 = this.delegate;
        if (filterTabsViewDelegate2 != null) {
            filterTabsViewDelegate2.onPageSelected(tab, z);
        }
        scrollToChild(i);
    }

    public void selectFirstTab() {
        if (!this.tabs.isEmpty()) {
            scrollToTab(this.tabs.get(0), 0);
        }
    }

    public void setAnimationIdicatorProgress(float f) {
        this.animatingIndicatorProgress = f;
        this.listView.invalidateViews();
        invalidate();
        FilterTabsViewDelegate filterTabsViewDelegate = this.delegate;
        if (filterTabsViewDelegate != null) {
            filterTabsViewDelegate.onPageScrolled(f);
        }
    }

    public Drawable getSelectorDrawable() {
        return this.selectorDrawable;
    }

    public RecyclerListView getTabsContainer() {
        return this.listView;
    }

    public int getNextPageId(boolean z) {
        return this.positionToId.get(this.currentPosition + (z ? 1 : -1), -1);
    }

    public void removeTabs() {
        this.tabs.clear();
        this.positionToId.clear();
        this.idToPosition.clear();
        this.positionToWidth.clear();
        this.positionToX.clear();
        this.allTabsWidth = 0;
    }

    public void resetTabId() {
        this.selectedTabId = -1;
    }

    public void addTab(int i, int i2, String str, boolean z, boolean z2) {
        int size = this.tabs.size();
        if (size == 0 && this.selectedTabId == -1) {
            this.selectedTabId = i;
        }
        this.positionToId.put(size, i);
        this.positionToStableId.put(size, i2);
        this.idToPosition.put(i, size);
        int i3 = this.selectedTabId;
        if (i3 != -1 && i3 == i) {
            this.currentPosition = size;
        }
        Tab tab = new Tab(i, str);
        tab.isDefault = z;
        tab.isLocked = z2;
        this.allTabsWidth += tab.getWidth(true) + AndroidUtilities.dp(32.0f);
        this.tabs.add(tab);
    }

    public void finishAddingTabs(boolean z) {
        this.listView.setItemAnimator(z ? this.itemAnimator : null);
        this.adapter.notifyDataSetChanged();
    }

    public void animateColorsTo(String str, String str2, String str3, String str4, String str5) {
        AnimatorSet animatorSet = this.colorChangeAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.aTabLineColorKey = str;
        this.aActiveTextColorKey = str2;
        this.aUnactiveTextColorKey = str3;
        this.aBackgroundColorKey = str5;
        this.selectorColorKey = str4;
        this.listView.setSelectorDrawableColor(Theme.getColor(str4));
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.colorChangeAnimator = animatorSet2;
        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, this.COLORS, new float[]{0.0f, 1.0f})});
        this.colorChangeAnimator.setDuration(200);
        this.colorChangeAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                FilterTabsView filterTabsView = FilterTabsView.this;
                String unused = filterTabsView.tabLineColorKey = filterTabsView.aTabLineColorKey;
                FilterTabsView filterTabsView2 = FilterTabsView.this;
                String unused2 = filterTabsView2.backgroundColorKey = filterTabsView2.aBackgroundColorKey;
                FilterTabsView filterTabsView3 = FilterTabsView.this;
                String unused3 = filterTabsView3.activeTextColorKey = filterTabsView3.aActiveTextColorKey;
                FilterTabsView filterTabsView4 = FilterTabsView.this;
                String unused4 = filterTabsView4.unactiveTextColorKey = filterTabsView4.aUnactiveTextColorKey;
                String unused5 = FilterTabsView.this.aTabLineColorKey = null;
                String unused6 = FilterTabsView.this.aActiveTextColorKey = null;
                String unused7 = FilterTabsView.this.aUnactiveTextColorKey = null;
                String unused8 = FilterTabsView.this.aBackgroundColorKey = null;
            }
        });
        this.colorChangeAnimator.start();
    }

    public int getCurrentTabId() {
        return this.selectedTabId;
    }

    public int getFirstTabId() {
        return this.positionToId.get(0, 0);
    }

    public String getSelectorColorKey() {
        return this.selectorColorKey;
    }

    /* access modifiers changed from: private */
    public void updateTabsWidths() {
        this.positionToX.clear();
        this.positionToWidth.clear();
        int dp = AndroidUtilities.dp(7.0f);
        int size = this.tabs.size();
        for (int i = 0; i < size; i++) {
            int width = this.tabs.get(i).getWidth(false);
            this.positionToWidth.put(i, width);
            this.positionToX.put(i, (this.additionalTabWidth / 2) + dp);
            dp += width + AndroidUtilities.dp(32.0f) + this.additionalTabWidth;
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0108  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x01dd  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean drawChild(android.graphics.Canvas r10, android.view.View r11, long r12) {
        /*
            r9 = this;
            boolean r12 = super.drawChild(r10, r11, r12)
            org.telegram.ui.Components.RecyclerListView r13 = r9.listView
            r0 = 1065353216(0x3var_, float:1.0)
            r1 = 0
            if (r11 != r13) goto L_0x0148
            int r11 = r9.getMeasuredHeight()
            android.graphics.drawable.GradientDrawable r13 = r9.selectorDrawable
            r2 = 1132396544(0x437var_, float:255.0)
            org.telegram.ui.Components.RecyclerListView r3 = r9.listView
            float r3 = r3.getAlpha()
            float r3 = r3 * r2
            int r2 = (int) r3
            r13.setAlpha(r2)
            boolean r13 = r9.animatingIndicator
            r2 = -1
            if (r13 != 0) goto L_0x0092
            int r13 = r9.manualScrollingToPosition
            if (r13 == r2) goto L_0x0029
            goto L_0x0092
        L_0x0029:
            org.telegram.ui.Components.RecyclerListView r13 = r9.listView
            int r2 = r9.currentPosition
            androidx.recyclerview.widget.RecyclerView$ViewHolder r13 = r13.findViewHolderForAdapterPosition(r2)
            if (r13 == 0) goto L_0x008f
            android.view.View r13 = r13.itemView
            org.telegram.ui.Components.FilterTabsView$TabView r13 = (org.telegram.ui.Components.FilterTabsView.TabView) r13
            r2 = 1109393408(0x42200000, float:40.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            boolean r3 = r13.animateTabWidth
            if (r3 == 0) goto L_0x0059
            float r3 = r13.animateFromTabWidth
            float r4 = r13.changeProgress
            float r4 = r0 - r4
            float r3 = r3 * r4
            int r4 = r13.tabWidth
            float r4 = (float) r4
            float r5 = r13.changeProgress
            float r4 = r4 * r5
            float r3 = r3 + r4
            goto L_0x005e
        L_0x0059:
            int r3 = r13.tabWidth
            float r3 = (float) r3
        L_0x005e:
            float r2 = java.lang.Math.max(r2, r3)
            boolean r3 = r13.animateTabWidth
            if (r3 == 0) goto L_0x007d
            float r3 = r13.animateFromWidth
            float r4 = r13.changeProgress
            float r4 = r0 - r4
            float r3 = r3 * r4
            int r4 = r13.getMeasuredWidth()
            float r4 = (float) r4
            float r5 = r13.changeProgress
            float r4 = r4 * r5
            float r3 = r3 + r4
            goto L_0x0082
        L_0x007d:
            int r3 = r13.getMeasuredWidth()
            float r3 = (float) r3
        L_0x0082:
            float r13 = r13.getX()
            float r3 = r3 - r2
            r4 = 1073741824(0x40000000, float:2.0)
            float r3 = r3 / r4
            float r13 = r13 + r3
            int r13 = (int) r13
            float r13 = (float) r13
            goto L_0x0104
        L_0x008f:
            r13 = 0
            r2 = 0
            goto L_0x0104
        L_0x0092:
            androidx.recyclerview.widget.LinearLayoutManager r13 = r9.layoutManager
            int r13 = r13.findFirstVisibleItemPosition()
            if (r13 == r2) goto L_0x008f
            org.telegram.ui.Components.RecyclerListView r2 = r9.listView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r2 = r2.findViewHolderForAdapterPosition(r13)
            if (r2 == 0) goto L_0x008f
            boolean r3 = r9.animatingIndicator
            if (r3 == 0) goto L_0x00ab
            int r3 = r9.previousPosition
            int r4 = r9.currentPosition
            goto L_0x00af
        L_0x00ab:
            int r3 = r9.currentPosition
            int r4 = r9.manualScrollingToPosition
        L_0x00af:
            android.util.SparseIntArray r5 = r9.positionToX
            int r5 = r5.get(r3)
            android.util.SparseIntArray r6 = r9.positionToX
            int r6 = r6.get(r4)
            android.util.SparseIntArray r7 = r9.positionToWidth
            int r3 = r7.get(r3)
            android.util.SparseIntArray r7 = r9.positionToWidth
            int r4 = r7.get(r4)
            int r7 = r9.additionalTabWidth
            r8 = 1098907648(0x41800000, float:16.0)
            if (r7 == 0) goto L_0x00dd
            float r13 = (float) r5
            int r6 = r6 - r5
            float r2 = (float) r6
            float r5 = r9.animatingIndicatorProgress
            float r2 = r2 * r5
            float r13 = r13 + r2
            int r13 = (int) r13
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r13 = r13 + r2
            float r13 = (float) r13
            goto L_0x00fa
        L_0x00dd:
            android.util.SparseIntArray r7 = r9.positionToX
            int r13 = r7.get(r13)
            float r7 = (float) r5
            int r6 = r6 - r5
            float r5 = (float) r6
            float r6 = r9.animatingIndicatorProgress
            float r5 = r5 * r6
            float r7 = r7 + r5
            int r5 = (int) r7
            android.view.View r2 = r2.itemView
            int r2 = r2.getLeft()
            int r13 = r13 - r2
            int r5 = r5 - r13
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r5 = r5 + r13
            float r13 = (float) r5
        L_0x00fa:
            float r2 = (float) r3
            int r4 = r4 - r3
            float r3 = (float) r4
            float r4 = r9.animatingIndicatorProgress
            float r3 = r3 * r4
            float r2 = r2 + r3
            int r2 = (int) r2
            float r2 = (float) r2
        L_0x0104:
            int r3 = (r2 > r1 ? 1 : (r2 == r1 ? 0 : -1))
            if (r3 == 0) goto L_0x0148
            r10.save()
            org.telegram.ui.Components.RecyclerListView r3 = r9.listView
            float r3 = r3.getTranslationX()
            r10.translate(r3, r1)
            org.telegram.ui.Components.RecyclerListView r3 = r9.listView
            float r3 = r3.getScaleX()
            org.telegram.ui.Components.RecyclerListView r4 = r9.listView
            float r4 = r4.getPivotX()
            org.telegram.ui.Components.RecyclerListView r5 = r9.listView
            float r5 = r5.getX()
            float r4 = r4 + r5
            org.telegram.ui.Components.RecyclerListView r5 = r9.listView
            float r5 = r5.getPivotY()
            r10.scale(r3, r0, r4, r5)
            android.graphics.drawable.GradientDrawable r3 = r9.selectorDrawable
            int r4 = (int) r13
            r5 = 1082130432(0x40800000, float:4.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dpr(r5)
            int r5 = r11 - r5
            float r13 = r13 + r2
            int r13 = (int) r13
            r3.setBounds(r4, r5, r13, r11)
            android.graphics.drawable.GradientDrawable r11 = r9.selectorDrawable
            r11.draw(r10)
            r10.restore()
        L_0x0148:
            long r10 = android.os.SystemClock.elapsedRealtime()
            r2 = 17
            long r4 = r9.lastEditingAnimationTime
            long r4 = r10 - r4
            long r2 = java.lang.Math.min(r2, r4)
            r9.lastEditingAnimationTime = r10
            boolean r10 = r9.isEditing
            r11 = 0
            r13 = 1
            if (r10 != 0) goto L_0x0164
            float r4 = r9.editingAnimationProgress
            int r4 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r4 == 0) goto L_0x01b0
        L_0x0164:
            boolean r4 = r9.editingForwardAnimation
            r5 = 1123024896(0x42var_, float:120.0)
            if (r4 == 0) goto L_0x018d
            float r4 = r9.editingAnimationProgress
            int r6 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r6 > 0) goto L_0x0172
            r6 = 1
            goto L_0x0173
        L_0x0172:
            r6 = 0
        L_0x0173:
            float r7 = (float) r2
            float r7 = r7 / r5
            float r4 = r4 + r7
            r9.editingAnimationProgress = r4
            if (r10 != 0) goto L_0x0182
            if (r6 == 0) goto L_0x0182
            int r4 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r4 < 0) goto L_0x0182
            r9.editingAnimationProgress = r1
        L_0x0182:
            float r4 = r9.editingAnimationProgress
            int r4 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1))
            if (r4 < 0) goto L_0x01af
            r9.editingAnimationProgress = r0
            r9.editingForwardAnimation = r11
            goto L_0x01af
        L_0x018d:
            float r4 = r9.editingAnimationProgress
            int r6 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r6 < 0) goto L_0x0194
            r11 = 1
        L_0x0194:
            float r6 = (float) r2
            float r6 = r6 / r5
            float r4 = r4 - r6
            r9.editingAnimationProgress = r4
            if (r10 != 0) goto L_0x01a3
            if (r11 == 0) goto L_0x01a3
            int r11 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r11 > 0) goto L_0x01a3
            r9.editingAnimationProgress = r1
        L_0x01a3:
            float r11 = r9.editingAnimationProgress
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
            int r11 = (r11 > r4 ? 1 : (r11 == r4 ? 0 : -1))
            if (r11 > 0) goto L_0x01af
            r9.editingAnimationProgress = r4
            r9.editingForwardAnimation = r13
        L_0x01af:
            r11 = 1
        L_0x01b0:
            r4 = 1127481344(0x43340000, float:180.0)
            if (r10 == 0) goto L_0x01c6
            float r10 = r9.editingStartAnimationProgress
            int r1 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
            if (r1 >= 0) goto L_0x01da
            float r11 = (float) r2
            float r11 = r11 / r4
            float r10 = r10 + r11
            r9.editingStartAnimationProgress = r10
            int r10 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
            if (r10 <= 0) goto L_0x01db
            r9.editingStartAnimationProgress = r0
            goto L_0x01db
        L_0x01c6:
            if (r10 != 0) goto L_0x01da
            float r10 = r9.editingStartAnimationProgress
            int r0 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x01da
            float r11 = (float) r2
            float r11 = r11 / r4
            float r10 = r10 - r11
            r9.editingStartAnimationProgress = r10
            int r10 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1))
            if (r10 >= 0) goto L_0x01db
            r9.editingStartAnimationProgress = r1
            goto L_0x01db
        L_0x01da:
            r13 = r11
        L_0x01db:
            if (r13 == 0) goto L_0x01e5
            org.telegram.ui.Components.RecyclerListView r10 = r9.listView
            r10.invalidateViews()
            r9.invalidate()
        L_0x01e5:
            return r12
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FilterTabsView.drawChild(android.graphics.Canvas, android.view.View, long):boolean");
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (!this.tabs.isEmpty()) {
            int size = (View.MeasureSpec.getSize(i) - AndroidUtilities.dp(7.0f)) - AndroidUtilities.dp(7.0f);
            Tab findDefaultTab = findDefaultTab();
            findDefaultTab.setTitle(LocaleController.getString("FilterAllChats", NUM));
            int width = findDefaultTab.getWidth(false);
            findDefaultTab.setTitle(this.allTabsWidth > size ? LocaleController.getString("FilterAllChatsShort", NUM) : LocaleController.getString("FilterAllChats", NUM));
            int width2 = (this.allTabsWidth - width) + findDefaultTab.getWidth(false);
            int i3 = this.additionalTabWidth;
            int size2 = width2 < size ? (size - width2) / this.tabs.size() : 0;
            this.additionalTabWidth = size2;
            if (i3 != size2) {
                this.ignoreLayout = true;
                RecyclerView.ItemAnimator itemAnimator2 = this.listView.getItemAnimator();
                this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
                this.adapter.notifyDataSetChanged();
                this.listView.setItemAnimator(itemAnimator2);
                this.ignoreLayout = false;
            }
            updateTabsWidths();
            this.invalidated = false;
        }
        super.onMeasure(i, i2);
    }

    private Tab findDefaultTab() {
        for (int i = 0; i < this.tabs.size(); i++) {
            if (this.tabs.get(i).isDefault) {
                return this.tabs.get(i);
            }
        }
        return null;
    }

    public void requestLayout() {
        if (!this.ignoreLayout) {
            super.requestLayout();
        }
    }

    private void scrollToChild(int i) {
        if (!this.tabs.isEmpty() && this.scrollingToChild != i && i >= 0 && i < this.tabs.size()) {
            this.scrollingToChild = i;
            this.listView.smoothScrollToPosition(i);
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        int i5 = i3 - i;
        if (this.prevLayoutWidth != i5) {
            this.prevLayoutWidth = i5;
            this.scrollingToChild = -1;
            if (this.animatingIndicator) {
                AndroidUtilities.cancelRunOnUIThread(this.animationRunnable);
                this.animatingIndicator = false;
                setEnabled(true);
                FilterTabsViewDelegate filterTabsViewDelegate = this.delegate;
                if (filterTabsViewDelegate != null) {
                    filterTabsViewDelegate.onPageScrolled(1.0f);
                }
            }
        }
    }

    public void selectTabWithId(int i, float f) {
        int i2 = this.idToPosition.get(i, -1);
        if (i2 >= 0) {
            if (f < 0.0f) {
                f = 0.0f;
            } else if (f > 1.0f) {
                f = 1.0f;
            }
            if (f > 0.0f) {
                this.manualScrollingToPosition = i2;
                this.manualScrollingToId = i;
            } else {
                this.manualScrollingToPosition = -1;
                this.manualScrollingToId = -1;
            }
            this.animatingIndicatorProgress = f;
            this.listView.invalidateViews();
            invalidate();
            scrollToChild(i2);
            if (f >= 1.0f) {
                this.manualScrollingToPosition = -1;
                this.manualScrollingToId = -1;
                this.currentPosition = i2;
                this.selectedTabId = i;
            }
        }
    }

    public boolean isEditing() {
        return this.isEditing;
    }

    public void setIsEditing(boolean z) {
        this.isEditing = z;
        this.editingForwardAnimation = true;
        this.listView.invalidateViews();
        invalidate();
        if (!this.isEditing && this.orderChanged) {
            MessagesStorage.getInstance(UserConfig.selectedAccount).saveDialogFiltersOrder();
            TLRPC$TL_messages_updateDialogFiltersOrder tLRPC$TL_messages_updateDialogFiltersOrder = new TLRPC$TL_messages_updateDialogFiltersOrder();
            ArrayList<MessagesController.DialogFilter> arrayList = MessagesController.getInstance(UserConfig.selectedAccount).dialogFilters;
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                MessagesController.DialogFilter dialogFilter = arrayList.get(i);
                if (dialogFilter.isDefault()) {
                    tLRPC$TL_messages_updateDialogFiltersOrder.order.add(0);
                } else {
                    tLRPC$TL_messages_updateDialogFiltersOrder.order.add(Integer.valueOf(dialogFilter.id));
                }
            }
            MessagesController.getInstance(UserConfig.selectedAccount).lockFiltersInternal();
            ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(tLRPC$TL_messages_updateDialogFiltersOrder, FilterTabsView$$ExternalSyntheticLambda0.INSTANCE);
            this.orderChanged = false;
        }
    }

    public void checkTabsCounter() {
        int size = this.tabs.size();
        int i = 0;
        boolean z = false;
        while (true) {
            if (i >= size) {
                break;
            }
            Tab tab = this.tabs.get(i);
            if (tab.counter != this.delegate.getTabCounter(tab.id) && this.delegate.getTabCounter(tab.id) >= 0) {
                if (this.positionToWidth.get(i) != tab.getWidth(true) || this.invalidated) {
                    this.invalidated = true;
                    requestLayout();
                    this.allTabsWidth = 0;
                    findDefaultTab().setTitle(LocaleController.getString("FilterAllChats", NUM));
                } else {
                    z = true;
                }
            }
            i++;
        }
        this.invalidated = true;
        requestLayout();
        this.allTabsWidth = 0;
        findDefaultTab().setTitle(LocaleController.getString("FilterAllChats", NUM));
        for (int i2 = 0; i2 < size; i2++) {
            this.allTabsWidth += this.tabs.get(i2).getWidth(true) + AndroidUtilities.dp(32.0f);
        }
        z = true;
        if (z) {
            this.listView.setItemAnimator(this.itemAnimator);
            this.adapter.notifyDataSetChanged();
        }
    }

    public void notifyTabCounterChanged(int i) {
        int i2 = this.idToPosition.get(i, -1);
        if (i2 >= 0 && i2 < this.tabs.size()) {
            Tab tab = this.tabs.get(i2);
            if (tab.counter != this.delegate.getTabCounter(tab.id) && this.delegate.getTabCounter(tab.id) >= 0) {
                this.listView.invalidateViews();
                if (this.positionToWidth.get(i2) != tab.getWidth(true) || this.invalidated) {
                    this.invalidated = true;
                    requestLayout();
                    this.listView.setItemAnimator(this.itemAnimator);
                    this.adapter.notifyDataSetChanged();
                    this.allTabsWidth = 0;
                    findDefaultTab().setTitle(LocaleController.getString("FilterAllChats", NUM));
                    int size = this.tabs.size();
                    for (int i3 = 0; i3 < size; i3++) {
                        this.allTabsWidth += this.tabs.get(i3).getWidth(true) + AndroidUtilities.dp(32.0f);
                    }
                }
            }
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return FilterTabsView.this.tabs.size();
        }

        public long getItemId(int i) {
            return (long) FilterTabsView.this.positionToStableId.get(i);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new RecyclerListView.Holder(new TabView(this.mContext));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            TabView tabView = (TabView) viewHolder.itemView;
            int id = tabView.currentTab != null ? tabView.getId() : -1;
            tabView.setTab((Tab) FilterTabsView.this.tabs.get(i), i);
            if (id != tabView.getId()) {
                float unused = tabView.progressToLocked = tabView.currentTab.isLocked ? 1.0f : 0.0f;
            }
        }

        public void swapElements(int i, int i2) {
            int size = FilterTabsView.this.tabs.size();
            if (i >= 0 && i2 >= 0 && i < size && i2 < size) {
                ArrayList<MessagesController.DialogFilter> arrayList = MessagesController.getInstance(UserConfig.selectedAccount).dialogFilters;
                MessagesController.DialogFilter dialogFilter = arrayList.get(i);
                MessagesController.DialogFilter dialogFilter2 = arrayList.get(i2);
                int i3 = dialogFilter.order;
                dialogFilter.order = dialogFilter2.order;
                dialogFilter2.order = i3;
                arrayList.set(i, dialogFilter2);
                arrayList.set(i2, dialogFilter);
                Tab tab = (Tab) FilterTabsView.this.tabs.get(i);
                Tab tab2 = (Tab) FilterTabsView.this.tabs.get(i2);
                int i4 = tab.id;
                tab.id = tab2.id;
                tab2.id = i4;
                int i5 = FilterTabsView.this.positionToStableId.get(i);
                FilterTabsView.this.positionToStableId.put(i, FilterTabsView.this.positionToStableId.get(i2));
                FilterTabsView.this.positionToStableId.put(i2, i5);
                FilterTabsView.this.delegate.onPageReorder(tab2.id, tab.id);
                if (FilterTabsView.this.currentPosition == i) {
                    int unused = FilterTabsView.this.currentPosition = i2;
                    int unused2 = FilterTabsView.this.selectedTabId = tab.id;
                } else if (FilterTabsView.this.currentPosition == i2) {
                    int unused3 = FilterTabsView.this.currentPosition = i;
                    int unused4 = FilterTabsView.this.selectedTabId = tab2.id;
                }
                if (FilterTabsView.this.previousPosition == i) {
                    int unused5 = FilterTabsView.this.previousPosition = i2;
                    int unused6 = FilterTabsView.this.previousId = tab.id;
                } else if (FilterTabsView.this.previousPosition == i2) {
                    int unused7 = FilterTabsView.this.previousPosition = i;
                    int unused8 = FilterTabsView.this.previousId = tab2.id;
                }
                FilterTabsView.this.tabs.set(i, tab2);
                FilterTabsView.this.tabs.set(i2, tab);
                FilterTabsView.this.updateTabsWidths();
                boolean unused9 = FilterTabsView.this.orderChanged = true;
                FilterTabsView.this.listView.setItemAnimator(FilterTabsView.this.itemAnimator);
                notifyItemMoved(i, i2);
            }
        }
    }

    public class TouchHelperCallback extends ItemTouchHelper.Callback {
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        }

        public TouchHelperCallback() {
        }

        public boolean isLongPressDragEnabled() {
            return FilterTabsView.this.isEditing;
        }

        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (!FilterTabsView.this.isEditing || (viewHolder.getAdapterPosition() == 0 && ((Tab) FilterTabsView.this.tabs.get(0)).isDefault && !UserConfig.getInstance(UserConfig.selectedAccount).isPremium())) {
                return ItemTouchHelper.Callback.makeMovementFlags(0, 0);
            }
            return ItemTouchHelper.Callback.makeMovementFlags(12, 0);
        }

        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
            if ((viewHolder.getAdapterPosition() == 0 || viewHolder2.getAdapterPosition() == 0) && !UserConfig.getInstance(UserConfig.selectedAccount).isPremium()) {
                return false;
            }
            FilterTabsView.this.adapter.swapElements(viewHolder.getAdapterPosition(), viewHolder2.getAdapterPosition());
            return true;
        }

        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i) {
            if (i != 0) {
                FilterTabsView.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
                viewHolder.itemView.setBackgroundColor(Theme.getColor(FilterTabsView.this.backgroundColorKey));
            }
            super.onSelectedChanged(viewHolder, i);
        }

        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setPressed(false);
            viewHolder.itemView.setBackground((Drawable) null);
        }
    }

    public RecyclerListView getListView() {
        return this.listView;
    }

    public boolean currentTabIsDefault() {
        Tab findDefaultTab = findDefaultTab();
        if (findDefaultTab != null && findDefaultTab.id == this.selectedTabId) {
            return true;
        }
        return false;
    }

    public int getDefaultTabId() {
        Tab findDefaultTab = findDefaultTab();
        if (findDefaultTab == null) {
            return -1;
        }
        return findDefaultTab.id;
    }

    public boolean isEmpty() {
        return this.tabs.isEmpty();
    }

    public boolean isFirstTabSelected() {
        if (!this.tabs.isEmpty() && this.selectedTabId != this.tabs.get(0).id) {
            return false;
        }
        return true;
    }

    public boolean isLocked(int i) {
        for (int i2 = 0; i2 < this.tabs.size(); i2++) {
            if (this.tabs.get(i2).id == i) {
                return this.tabs.get(i2).isLocked;
            }
        }
        return false;
    }

    public void shakeLock(int i) {
        for (int i2 = 0; i2 < this.listView.getChildCount(); i2++) {
            if (this.listView.getChildAt(i2) instanceof TabView) {
                TabView tabView = (TabView) this.listView.getChildAt(i2);
                if (tabView.currentTab.id == i) {
                    tabView.shakeLockIcon(1.0f, 0);
                    tabView.performHapticFeedback(3);
                    return;
                }
            }
        }
    }
}
