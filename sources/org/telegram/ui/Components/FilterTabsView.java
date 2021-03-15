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
import org.telegram.ui.Components.FilterTabsView;
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
                FilterTabsView filterTabsView = FilterTabsView.this;
                float unused = filterTabsView.animationTime = filterTabsView.animationTime + (((float) elapsedRealtime) / 200.0f);
                FilterTabsView filterTabsView2 = FilterTabsView.this;
                filterTabsView2.setAnimationIdicatorProgress(filterTabsView2.interpolator.getInterpolation(FilterTabsView.this.animationTime));
                if (FilterTabsView.this.animationTime > 1.0f) {
                    float unused2 = FilterTabsView.this.animationTime = 1.0f;
                }
                if (FilterTabsView.this.animationTime < 1.0f) {
                    AndroidUtilities.runOnUIThread(FilterTabsView.this.animationRunnable);
                    return;
                }
                boolean unused3 = FilterTabsView.this.animatingIndicator = false;
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

        void onPageSelected(int i, boolean z);

        void onSamePageSelected();
    }

    static /* synthetic */ void lambda$setIsEditing$2(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    private class Tab {
        public int counter;
        public int id;
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
        StaticLayout outCounter;
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
        /* JADX WARNING: Removed duplicated region for block: B:165:0x04ff  */
        /* JADX WARNING: Removed duplicated region for block: B:166:0x050d  */
        /* JADX WARNING: Removed duplicated region for block: B:172:0x0528  */
        /* JADX WARNING: Removed duplicated region for block: B:176:0x0550  */
        /* JADX WARNING: Removed duplicated region for block: B:188:0x05a1  */
        /* JADX WARNING: Removed duplicated region for block: B:189:0x05ac  */
        /* JADX WARNING: Removed duplicated region for block: B:192:0x05b2  */
        /* JADX WARNING: Removed duplicated region for block: B:195:0x05f1  */
        /* JADX WARNING: Removed duplicated region for block: B:198:0x0631  */
        /* JADX WARNING: Removed duplicated region for block: B:200:0x0662  */
        @android.annotation.SuppressLint({"DrawAllocation"})
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onDraw(android.graphics.Canvas r30) {
            /*
                r29 = this;
                r0 = r29
                r7 = r30
                org.telegram.ui.Components.FilterTabsView$Tab r1 = r0.currentTab
                int r1 = r1.id
                r8 = 2147483647(0x7fffffff, float:NaN)
                r9 = 0
                if (r1 == r8) goto L_0x004c
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                float r1 = r1.editingAnimationProgress
                int r1 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1))
                if (r1 == 0) goto L_0x004c
                r30.save()
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                float r1 = r1.editingAnimationProgress
                int r3 = r0.currentPosition
                int r3 = r3 % 2
                if (r3 != 0) goto L_0x002a
                r3 = 1065353216(0x3var_, float:1.0)
                goto L_0x002c
            L_0x002a:
                r3 = -1082130432(0xffffffffbvar_, float:-1.0)
            L_0x002c:
                float r1 = r1 * r3
                r3 = 1059648963(0x3var_f5c3, float:0.66)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                float r3 = r3 * r1
                r7.translate(r3, r9)
                int r3 = r29.getMeasuredWidth()
                int r3 = r3 / 2
                float r3 = (float) r3
                int r4 = r29.getMeasuredHeight()
                int r4 = r4 / 2
                float r4 = (float) r4
                r7.rotate(r1, r3, r4)
            L_0x004c:
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                int r1 = r1.manualScrollingToId
                r3 = -1
                if (r1 == r3) goto L_0x0062
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                int r1 = r1.manualScrollingToId
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                int r4 = r4.selectedTabId
                goto L_0x006e
            L_0x0062:
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                int r1 = r1.selectedTabId
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                int r4 = r4.previousId
            L_0x006e:
                org.telegram.ui.Components.FilterTabsView$Tab r5 = r0.currentTab
                int r5 = r5.id
                java.lang.String r6 = "chats_tabUnreadActiveBackground"
                java.lang.String r10 = "chats_tabUnreadUnactiveBackground"
                if (r5 != r1) goto L_0x0091
                org.telegram.ui.Components.FilterTabsView r5 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r5 = r5.activeTextColorKey
                org.telegram.ui.Components.FilterTabsView r11 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r11 = r11.aActiveTextColorKey
                org.telegram.ui.Components.FilterTabsView r12 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r12 = r12.unactiveTextColorKey
                org.telegram.ui.Components.FilterTabsView r13 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r13 = r13.aUnactiveTextColorKey
                goto L_0x00ae
            L_0x0091:
                org.telegram.ui.Components.FilterTabsView r5 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r5 = r5.unactiveTextColorKey
                org.telegram.ui.Components.FilterTabsView r11 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r11 = r11.aUnactiveTextColorKey
                org.telegram.ui.Components.FilterTabsView r12 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r12 = r12.activeTextColorKey
                org.telegram.ui.Components.FilterTabsView r13 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r13 = r13.aUnactiveTextColorKey
                r28 = r10
                r10 = r6
                r6 = r28
            L_0x00ae:
                if (r11 != 0) goto L_0x00f4
                org.telegram.ui.Components.FilterTabsView r11 = org.telegram.ui.Components.FilterTabsView.this
                boolean r11 = r11.animatingIndicator
                if (r11 != 0) goto L_0x00c0
                org.telegram.ui.Components.FilterTabsView r11 = org.telegram.ui.Components.FilterTabsView.this
                int r11 = r11.manualScrollingToId
                if (r11 == r3) goto L_0x00c9
            L_0x00c0:
                org.telegram.ui.Components.FilterTabsView$Tab r11 = r0.currentTab
                int r11 = r11.id
                if (r11 == r1) goto L_0x00d8
                if (r11 != r4) goto L_0x00c9
                goto L_0x00d8
            L_0x00c9:
                org.telegram.ui.Components.FilterTabsView r11 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r11 = r11.textPaint
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r11.setColor(r5)
                goto L_0x0158
            L_0x00d8:
                org.telegram.ui.Components.FilterTabsView r11 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r11 = r11.textPaint
                int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                org.telegram.ui.Components.FilterTabsView r13 = org.telegram.ui.Components.FilterTabsView.this
                float r13 = r13.animatingIndicatorProgress
                int r5 = androidx.core.graphics.ColorUtils.blendARGB(r12, r5, r13)
                r11.setColor(r5)
                goto L_0x0158
            L_0x00f4:
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
                org.telegram.ui.Components.FilterTabsView r14 = org.telegram.ui.Components.FilterTabsView.this
                boolean r14 = r14.animatingIndicator
                if (r14 != 0) goto L_0x010c
                org.telegram.ui.Components.FilterTabsView r14 = org.telegram.ui.Components.FilterTabsView.this
                int r14 = r14.manualScrollingToPosition
                if (r14 == r3) goto L_0x0115
            L_0x010c:
                org.telegram.ui.Components.FilterTabsView$Tab r14 = r0.currentTab
                int r14 = r14.id
                if (r14 == r1) goto L_0x0129
                if (r14 != r4) goto L_0x0115
                goto L_0x0129
            L_0x0115:
                org.telegram.ui.Components.FilterTabsView r12 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r12 = r12.textPaint
                org.telegram.ui.Components.FilterTabsView r13 = org.telegram.ui.Components.FilterTabsView.this
                float r13 = r13.animationValue
                int r5 = androidx.core.graphics.ColorUtils.blendARGB(r5, r11, r13)
                r12.setColor(r5)
                goto L_0x0158
            L_0x0129:
                int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r13)
                org.telegram.ui.Components.FilterTabsView r14 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r14 = r14.textPaint
                org.telegram.ui.Components.FilterTabsView r15 = org.telegram.ui.Components.FilterTabsView.this
                float r15 = r15.animationValue
                int r12 = androidx.core.graphics.ColorUtils.blendARGB(r12, r13, r15)
                org.telegram.ui.Components.FilterTabsView r13 = org.telegram.ui.Components.FilterTabsView.this
                float r13 = r13.animationValue
                int r5 = androidx.core.graphics.ColorUtils.blendARGB(r5, r11, r13)
                org.telegram.ui.Components.FilterTabsView r11 = org.telegram.ui.Components.FilterTabsView.this
                float r11 = r11.animatingIndicatorProgress
                int r5 = androidx.core.graphics.ColorUtils.blendARGB(r12, r5, r11)
                r14.setColor(r5)
            L_0x0158:
                int r5 = r0.animateFromTabCount
                r11 = 1
                r12 = 0
                if (r5 != 0) goto L_0x0164
                boolean r13 = r0.animateTabCounter
                if (r13 == 0) goto L_0x0164
                r13 = 1
                goto L_0x0165
            L_0x0164:
                r13 = 0
            L_0x0165:
                if (r5 <= 0) goto L_0x0173
                org.telegram.ui.Components.FilterTabsView$Tab r14 = r0.currentTab
                int r14 = r14.counter
                if (r14 != 0) goto L_0x0173
                boolean r14 = r0.animateTabCounter
                if (r14 == 0) goto L_0x0173
                r14 = 1
                goto L_0x0174
            L_0x0173:
                r14 = 0
            L_0x0174:
                if (r5 <= 0) goto L_0x0182
                org.telegram.ui.Components.FilterTabsView$Tab r15 = r0.currentTab
                int r15 = r15.counter
                if (r15 <= 0) goto L_0x0182
                boolean r15 = r0.animateTabCounter
                if (r15 == 0) goto L_0x0182
                r15 = 1
                goto L_0x0183
            L_0x0182:
                r15 = 0
            L_0x0183:
                org.telegram.ui.Components.FilterTabsView$Tab r3 = r0.currentTab
                int r3 = r3.counter
                if (r3 > 0) goto L_0x0190
                if (r14 == 0) goto L_0x018c
                goto L_0x0190
            L_0x018c:
                r3 = 0
                r11 = r13
                r2 = 0
                goto L_0x01d2
            L_0x0190:
                java.lang.String r2 = "%d"
                if (r14 == 0) goto L_0x01a1
                java.lang.Object[] r3 = new java.lang.Object[r11]
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                r3[r12] = r5
                java.lang.String r2 = java.lang.String.format(r2, r3)
                goto L_0x01ad
            L_0x01a1:
                java.lang.Object[] r5 = new java.lang.Object[r11]
                java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                r5[r12] = r3
                java.lang.String r2 = java.lang.String.format(r2, r5)
            L_0x01ad:
                r3 = r2
                org.telegram.ui.Components.FilterTabsView r2 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r2 = r2.textCounterPaint
                float r2 = r2.measureText(r3)
                r11 = r13
                double r12 = (double) r2
                double r12 = java.lang.Math.ceil(r12)
                int r2 = (int) r12
                float r2 = (float) r2
                r12 = 1092616192(0x41200000, float:10.0)
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
                float r13 = (float) r13
                float r13 = java.lang.Math.max(r13, r2)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
                float r12 = (float) r12
                float r13 = r13 + r12
                int r12 = (int) r13
            L_0x01d2:
                org.telegram.ui.Components.FilterTabsView$Tab r13 = r0.currentTab
                int r13 = r13.id
                r17 = 1101004800(0x41a00000, float:20.0)
                if (r13 == r8) goto L_0x01fe
                org.telegram.ui.Components.FilterTabsView r13 = org.telegram.ui.Components.FilterTabsView.this
                boolean r13 = r13.isEditing
                if (r13 != 0) goto L_0x01ec
                org.telegram.ui.Components.FilterTabsView r13 = org.telegram.ui.Components.FilterTabsView.this
                float r13 = r13.editingStartAnimationProgress
                int r13 = (r13 > r9 ? 1 : (r13 == r9 ? 0 : -1))
                if (r13 == 0) goto L_0x01fe
            L_0x01ec:
                float r13 = (float) r12
                int r18 = org.telegram.messenger.AndroidUtilities.dp(r17)
                int r12 = r18 - r12
                float r12 = (float) r12
                org.telegram.ui.Components.FilterTabsView r5 = org.telegram.ui.Components.FilterTabsView.this
                float r5 = r5.editingStartAnimationProgress
                float r12 = r12 * r5
                float r13 = r13 + r12
                int r12 = (int) r13
            L_0x01fe:
                org.telegram.ui.Components.FilterTabsView$Tab r5 = r0.currentTab
                int r5 = r5.titleWidth
                r13 = 1086324736(0x40CLASSNAME, float:6.0)
                if (r12 == 0) goto L_0x021b
                if (r14 != 0) goto L_0x021b
                if (r3 == 0) goto L_0x020d
                r9 = 1065353216(0x3var_, float:1.0)
                goto L_0x0213
            L_0x020d:
                org.telegram.ui.Components.FilterTabsView r9 = org.telegram.ui.Components.FilterTabsView.this
                float r9 = r9.editingStartAnimationProgress
            L_0x0213:
                float r9 = r9 * r13
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
                int r9 = r9 + r12
                goto L_0x021c
            L_0x021b:
                r9 = 0
            L_0x021c:
                int r5 = r5 + r9
                r0.tabWidth = r5
                int r5 = r29.getMeasuredWidth()
                int r9 = r0.tabWidth
                int r5 = r5 - r9
                float r5 = (float) r5
                r9 = 1073741824(0x40000000, float:2.0)
                float r5 = r5 / r9
                boolean r13 = r0.animateTextX
                if (r13 == 0) goto L_0x023b
                float r13 = r0.changeProgress
                float r5 = r5 * r13
                float r8 = r0.animateFromTextX
                r16 = 1065353216(0x3var_, float:1.0)
                float r13 = r16 - r13
                float r8 = r8 * r13
                float r5 = r5 + r8
            L_0x023b:
                r8 = r5
                org.telegram.ui.Components.FilterTabsView$Tab r5 = r0.currentTab
                java.lang.String r5 = r5.title
                java.lang.String r13 = r0.currentText
                boolean r5 = android.text.TextUtils.equals(r5, r13)
                r13 = 1097859072(0x41700000, float:15.0)
                if (r5 != 0) goto L_0x0294
                org.telegram.ui.Components.FilterTabsView$Tab r5 = r0.currentTab
                java.lang.String r5 = r5.title
                r0.currentText = r5
                org.telegram.ui.Components.FilterTabsView r9 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r9 = r9.textPaint
                android.graphics.Paint$FontMetricsInt r9 = r9.getFontMetricsInt()
                r19 = r14
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r13)
                r13 = 0
                java.lang.CharSequence r21 = org.telegram.messenger.Emoji.replaceEmoji(r5, r9, r14, r13)
                android.text.StaticLayout r9 = new android.text.StaticLayout
                org.telegram.ui.Components.FilterTabsView r13 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r22 = r13.textPaint
                r13 = 1137180672(0x43CLASSNAME, float:400.0)
                int r23 = org.telegram.messenger.AndroidUtilities.dp(r13)
                android.text.Layout$Alignment r24 = android.text.Layout.Alignment.ALIGN_NORMAL
                r25 = 1065353216(0x3var_, float:1.0)
                r26 = 0
                r27 = 0
                r20 = r9
                r20.<init>(r21, r22, r23, r24, r25, r26, r27)
                r0.textLayout = r9
                int r9 = r9.getHeight()
                r0.textHeight = r9
                android.text.StaticLayout r9 = r0.textLayout
                r5 = 0
                float r9 = r9.getLineLeft(r5)
                float r9 = -r9
                int r9 = (int) r9
                r0.textOffsetX = r9
                goto L_0x0296
            L_0x0294:
                r19 = r14
            L_0x0296:
                boolean r9 = r0.animateTextChange
                if (r9 == 0) goto L_0x037a
                float r9 = r0.titleXOffset
                boolean r13 = r0.animateTextChangeOut
                if (r13 == 0) goto L_0x02a3
                float r13 = r0.changeProgress
                goto L_0x02a9
            L_0x02a3:
                float r13 = r0.changeProgress
                r14 = 1065353216(0x3var_, float:1.0)
                float r13 = r14 - r13
            L_0x02a9:
                float r9 = r9 * r13
                android.text.StaticLayout r13 = r0.titleAnimateStableLayout
                if (r13 == 0) goto L_0x02d0
                r30.save()
                int r13 = r0.textOffsetX
                float r13 = (float) r13
                float r13 = r13 + r8
                float r13 = r13 + r9
                int r14 = r29.getMeasuredHeight()
                int r5 = r0.textHeight
                int r14 = r14 - r5
                float r5 = (float) r14
                r14 = 1073741824(0x40000000, float:2.0)
                float r5 = r5 / r14
                r14 = 1065353216(0x3var_, float:1.0)
                float r5 = r5 + r14
                r7.translate(r13, r5)
                android.text.StaticLayout r5 = r0.titleAnimateStableLayout
                r5.draw(r7)
                r30.restore()
            L_0x02d0:
                android.text.StaticLayout r5 = r0.titleAnimateInLayout
                if (r5 == 0) goto L_0x0325
                r30.save()
                org.telegram.ui.Components.FilterTabsView r5 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r5 = r5.textPaint
                int r5 = r5.getAlpha()
                org.telegram.ui.Components.FilterTabsView r13 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r13 = r13.textPaint
                float r14 = (float) r5
                r20 = r2
                boolean r2 = r0.animateTextChangeOut
                if (r2 == 0) goto L_0x02f5
                float r2 = r0.changeProgress
                r16 = 1065353216(0x3var_, float:1.0)
                float r2 = r16 - r2
                goto L_0x02f7
            L_0x02f5:
                float r2 = r0.changeProgress
            L_0x02f7:
                float r14 = r14 * r2
                int r2 = (int) r14
                r13.setAlpha(r2)
                int r2 = r0.textOffsetX
                float r2 = (float) r2
                float r2 = r2 + r8
                float r2 = r2 + r9
                int r13 = r29.getMeasuredHeight()
                int r14 = r0.textHeight
                int r13 = r13 - r14
                float r13 = (float) r13
                r14 = 1073741824(0x40000000, float:2.0)
                float r13 = r13 / r14
                r14 = 1065353216(0x3var_, float:1.0)
                float r13 = r13 + r14
                r7.translate(r2, r13)
                android.text.StaticLayout r2 = r0.titleAnimateInLayout
                r2.draw(r7)
                r30.restore()
                org.telegram.ui.Components.FilterTabsView r2 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r2 = r2.textPaint
                r2.setAlpha(r5)
                goto L_0x0327
            L_0x0325:
                r20 = r2
            L_0x0327:
                android.text.StaticLayout r2 = r0.titleAnimateOutLayout
                if (r2 == 0) goto L_0x03a1
                r30.save()
                org.telegram.ui.Components.FilterTabsView r2 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r2 = r2.textPaint
                int r2 = r2.getAlpha()
                org.telegram.ui.Components.FilterTabsView r5 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r5 = r5.textPaint
                float r13 = (float) r2
                boolean r14 = r0.animateTextChangeOut
                if (r14 == 0) goto L_0x0346
                float r14 = r0.changeProgress
                goto L_0x034c
            L_0x0346:
                float r14 = r0.changeProgress
                r16 = 1065353216(0x3var_, float:1.0)
                float r14 = r16 - r14
            L_0x034c:
                float r13 = r13 * r14
                int r13 = (int) r13
                r5.setAlpha(r13)
                int r5 = r0.textOffsetX
                float r5 = (float) r5
                float r5 = r5 + r8
                float r5 = r5 + r9
                int r13 = r29.getMeasuredHeight()
                int r14 = r0.textHeight
                int r13 = r13 - r14
                float r13 = (float) r13
                r14 = 1073741824(0x40000000, float:2.0)
                float r13 = r13 / r14
                r14 = 1065353216(0x3var_, float:1.0)
                float r13 = r13 + r14
                r7.translate(r5, r13)
                android.text.StaticLayout r5 = r0.titleAnimateOutLayout
                r5.draw(r7)
                r30.restore()
                org.telegram.ui.Components.FilterTabsView r5 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r5 = r5.textPaint
                r5.setAlpha(r2)
                goto L_0x03a1
            L_0x037a:
                r20 = r2
                android.text.StaticLayout r2 = r0.textLayout
                if (r2 == 0) goto L_0x03a0
                r30.save()
                int r2 = r0.textOffsetX
                float r2 = (float) r2
                float r2 = r2 + r8
                int r5 = r29.getMeasuredHeight()
                int r9 = r0.textHeight
                int r5 = r5 - r9
                float r5 = (float) r5
                r9 = 1073741824(0x40000000, float:2.0)
                float r5 = r5 / r9
                r9 = 1065353216(0x3var_, float:1.0)
                float r5 = r5 + r9
                r7.translate(r2, r5)
                android.text.StaticLayout r2 = r0.textLayout
                r2.draw(r7)
                r30.restore()
            L_0x03a0:
                r9 = 0
            L_0x03a1:
                if (r11 != 0) goto L_0x03c6
                if (r3 != 0) goto L_0x03c6
                org.telegram.ui.Components.FilterTabsView$Tab r2 = r0.currentTab
                int r2 = r2.id
                r5 = 2147483647(0x7fffffff, float:NaN)
                if (r2 == r5) goto L_0x03c2
                org.telegram.ui.Components.FilterTabsView r2 = org.telegram.ui.Components.FilterTabsView.this
                boolean r2 = r2.isEditing
                if (r2 != 0) goto L_0x03c6
                org.telegram.ui.Components.FilterTabsView r2 = org.telegram.ui.Components.FilterTabsView.this
                float r2 = r2.editingStartAnimationProgress
                r5 = 0
                int r2 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
                if (r2 == 0) goto L_0x03c2
                goto L_0x03c6
            L_0x03c2:
                r2 = r20
                goto L_0x0746
            L_0x03c6:
                org.telegram.ui.Components.FilterTabsView r2 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r2 = r2.aBackgroundColorKey
                if (r2 != 0) goto L_0x03e2
                org.telegram.ui.Components.FilterTabsView r2 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r2 = r2.textCounterPaint
                org.telegram.ui.Components.FilterTabsView r5 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r5 = r5.backgroundColorKey
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r2.setColor(r5)
                goto L_0x0409
            L_0x03e2:
                org.telegram.ui.Components.FilterTabsView r2 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r2 = r2.backgroundColorKey
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                org.telegram.ui.Components.FilterTabsView r5 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r5 = r5.aBackgroundColorKey
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                org.telegram.ui.Components.FilterTabsView r13 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r13 = r13.textCounterPaint
                org.telegram.ui.Components.FilterTabsView r14 = org.telegram.ui.Components.FilterTabsView.this
                float r14 = r14.animationValue
                int r2 = androidx.core.graphics.ColorUtils.blendARGB(r2, r5, r14)
                r13.setColor(r2)
            L_0x0409:
                boolean r2 = org.telegram.ui.ActionBar.Theme.hasThemeKey(r6)
                if (r2 == 0) goto L_0x0455
                boolean r2 = org.telegram.ui.ActionBar.Theme.hasThemeKey(r10)
                if (r2 == 0) goto L_0x0455
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r6)
                org.telegram.ui.Components.FilterTabsView r5 = org.telegram.ui.Components.FilterTabsView.this
                boolean r5 = r5.animatingIndicator
                if (r5 != 0) goto L_0x042a
                org.telegram.ui.Components.FilterTabsView r5 = org.telegram.ui.Components.FilterTabsView.this
                int r5 = r5.manualScrollingToPosition
                r6 = -1
                if (r5 == r6) goto L_0x0433
            L_0x042a:
                org.telegram.ui.Components.FilterTabsView$Tab r5 = r0.currentTab
                int r5 = r5.id
                if (r5 == r1) goto L_0x043d
                if (r5 != r4) goto L_0x0433
                goto L_0x043d
            L_0x0433:
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.Paint r1 = r1.counterPaint
                r1.setColor(r2)
                goto L_0x0468
            L_0x043d:
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r10)
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.Paint r4 = r4.counterPaint
                org.telegram.ui.Components.FilterTabsView r5 = org.telegram.ui.Components.FilterTabsView.this
                float r5 = r5.animatingIndicatorProgress
                int r1 = androidx.core.graphics.ColorUtils.blendARGB(r1, r2, r5)
                r4.setColor(r1)
                goto L_0x0468
            L_0x0455:
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.Paint r1 = r1.counterPaint
                org.telegram.ui.Components.FilterTabsView r2 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r2 = r2.textPaint
                int r2 = r2.getColor()
                r1.setColor(r2)
            L_0x0468:
                org.telegram.ui.Components.FilterTabsView$Tab r1 = r0.currentTab
                int r1 = r1.titleWidth
                float r2 = (float) r1
                boolean r4 = r0.animateTextChange
                if (r4 == 0) goto L_0x0480
                int r2 = r0.animateFromTitleWidth
                float r2 = (float) r2
                float r5 = r0.changeProgress
                r6 = 1065353216(0x3var_, float:1.0)
                float r10 = r6 - r5
                float r2 = r2 * r10
                float r1 = (float) r1
                float r1 = r1 * r5
                float r2 = r2 + r1
            L_0x0480:
                if (r4 == 0) goto L_0x0495
                android.text.StaticLayout r1 = r0.titleAnimateOutLayout
                if (r1 != 0) goto L_0x0495
                float r1 = r0.titleXOffset
                float r1 = r8 - r1
                float r1 = r1 + r9
                float r1 = r1 + r2
                r4 = 1086324736(0x40CLASSNAME, float:6.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r4)
                float r2 = (float) r2
                float r1 = r1 + r2
                goto L_0x049e
            L_0x0495:
                r4 = 1086324736(0x40CLASSNAME, float:6.0)
                float r2 = r2 + r8
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r4)
                float r1 = (float) r1
                float r1 = r1 + r2
            L_0x049e:
                int r2 = r29.getMeasuredHeight()
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
                int r2 = r2 - r4
                int r2 = r2 / 2
                org.telegram.ui.Components.FilterTabsView$Tab r4 = r0.currentTab
                int r4 = r4.id
                r5 = 255(0xff, float:3.57E-43)
                r6 = 1132396544(0x437var_, float:255.0)
                r9 = 2147483647(0x7fffffff, float:NaN)
                if (r4 == r9) goto L_0x04de
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                boolean r4 = r4.isEditing
                if (r4 != 0) goto L_0x04c9
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                float r4 = r4.editingStartAnimationProgress
                r9 = 0
                int r4 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
                if (r4 == 0) goto L_0x04de
            L_0x04c9:
                if (r3 != 0) goto L_0x04de
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.Paint r4 = r4.counterPaint
                org.telegram.ui.Components.FilterTabsView r9 = org.telegram.ui.Components.FilterTabsView.this
                float r9 = r9.editingStartAnimationProgress
                float r9 = r9 * r6
                int r9 = (int) r9
                r4.setAlpha(r9)
                goto L_0x04e7
            L_0x04de:
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.Paint r4 = r4.counterPaint
                r4.setAlpha(r5)
            L_0x04e7:
                if (r15 == 0) goto L_0x04fc
                float r4 = r0.animateFromCountWidth
                float r9 = (float) r12
                int r10 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
                if (r10 == 0) goto L_0x04fc
                float r10 = r0.changeProgress
                r13 = 1065353216(0x3var_, float:1.0)
                float r14 = r13 - r10
                float r4 = r4 * r14
                float r9 = r9 * r10
                float r4 = r4 + r9
                goto L_0x04fd
            L_0x04fc:
                float r4 = (float) r12
            L_0x04fd:
                if (r15 == 0) goto L_0x050d
                float r9 = r0.animateFromCounterWidth
                float r10 = r0.changeProgress
                r13 = 1065353216(0x3var_, float:1.0)
                float r14 = r13 - r10
                float r9 = r9 * r14
                float r10 = r10 * r20
                float r9 = r9 + r10
                goto L_0x050f
            L_0x050d:
                r9 = r20
            L_0x050f:
                android.graphics.RectF r10 = r0.rect
                float r13 = (float) r2
                float r4 = r4 + r1
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r17)
                int r14 = r14 + r2
                float r14 = (float) r14
                r10.set(r1, r13, r4, r14)
                if (r11 != 0) goto L_0x0520
                if (r19 == 0) goto L_0x053b
            L_0x0520:
                r30.save()
                float r1 = r0.changeProgress
                if (r11 == 0) goto L_0x0528
                goto L_0x052c
            L_0x0528:
                r4 = 1065353216(0x3var_, float:1.0)
                float r1 = r4 - r1
            L_0x052c:
                android.graphics.RectF r4 = r0.rect
                float r4 = r4.centerX()
                android.graphics.RectF r10 = r0.rect
                float r10 = r10.centerY()
                r7.scale(r1, r1, r4, r10)
            L_0x053b:
                android.graphics.RectF r1 = r0.rect
                float r4 = org.telegram.messenger.AndroidUtilities.density
                r10 = 1094189056(0x41380000, float:11.5)
                float r14 = r4 * r10
                float r4 = r4 * r10
                org.telegram.ui.Components.FilterTabsView r10 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.Paint r10 = r10.counterPaint
                r7.drawRoundRect(r1, r14, r4, r10)
                if (r15 == 0) goto L_0x0662
                android.text.StaticLayout r1 = r0.inCounter
                if (r1 == 0) goto L_0x056d
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
                android.text.StaticLayout r2 = r0.inCounter
                r3 = 0
                int r2 = r2.getLineBottom(r3)
                android.text.StaticLayout r4 = r0.inCounter
                int r3 = r4.getLineTop(r3)
            L_0x0565:
                int r2 = r2 - r3
                int r1 = r1 - r2
                float r1 = (float) r1
                r2 = 1073741824(0x40000000, float:2.0)
                float r1 = r1 / r2
                float r13 = r13 + r1
                goto L_0x0598
            L_0x056d:
                r3 = 0
                android.text.StaticLayout r1 = r0.outCounter
                if (r1 == 0) goto L_0x0583
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
                android.text.StaticLayout r2 = r0.outCounter
                int r2 = r2.getLineBottom(r3)
                android.text.StaticLayout r4 = r0.outCounter
                int r3 = r4.getLineTop(r3)
                goto L_0x0565
            L_0x0583:
                android.text.StaticLayout r1 = r0.stableCounter
                if (r1 == 0) goto L_0x0598
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
                android.text.StaticLayout r2 = r0.stableCounter
                int r2 = r2.getLineBottom(r3)
                android.text.StaticLayout r4 = r0.stableCounter
                int r3 = r4.getLineTop(r3)
                goto L_0x0565
            L_0x0598:
                org.telegram.ui.Components.FilterTabsView$Tab r1 = r0.currentTab
                int r1 = r1.id
                r2 = 2147483647(0x7fffffff, float:NaN)
                if (r1 == r2) goto L_0x05ac
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                float r1 = r1.editingStartAnimationProgress
                r2 = 1065353216(0x3var_, float:1.0)
                float r1 = r2 - r1
                goto L_0x05ae
            L_0x05ac:
                r1 = 1065353216(0x3var_, float:1.0)
            L_0x05ae:
                android.text.StaticLayout r2 = r0.inCounter
                if (r2 == 0) goto L_0x05ed
                r30.save()
                org.telegram.ui.Components.FilterTabsView r2 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r2 = r2.textCounterPaint
                float r3 = r1 * r6
                float r4 = r0.changeProgress
                float r3 = r3 * r4
                int r3 = (int) r3
                r2.setAlpha(r3)
                android.graphics.RectF r2 = r0.rect
                float r3 = r2.left
                float r2 = r2.width()
                float r2 = r2 - r9
                r4 = 1073741824(0x40000000, float:2.0)
                float r2 = r2 / r4
                float r3 = r3 + r2
                float r2 = r0.changeProgress
                r4 = 1065353216(0x3var_, float:1.0)
                float r2 = r4 - r2
                r4 = 1097859072(0x41700000, float:15.0)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r4)
                float r4 = (float) r10
                float r2 = r2 * r4
                float r2 = r2 + r13
                r7.translate(r3, r2)
                android.text.StaticLayout r2 = r0.inCounter
                r2.draw(r7)
                r30.restore()
            L_0x05ed:
                android.text.StaticLayout r2 = r0.outCounter
                if (r2 == 0) goto L_0x062d
                r30.save()
                org.telegram.ui.Components.FilterTabsView r2 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r2 = r2.textCounterPaint
                float r3 = r1 * r6
                float r4 = r0.changeProgress
                r10 = 1065353216(0x3var_, float:1.0)
                float r4 = r10 - r4
                float r3 = r3 * r4
                int r3 = (int) r3
                r2.setAlpha(r3)
                android.graphics.RectF r2 = r0.rect
                float r3 = r2.left
                float r2 = r2.width()
                float r2 = r2 - r9
                r4 = 1073741824(0x40000000, float:2.0)
                float r2 = r2 / r4
                float r3 = r3 + r2
                float r2 = r0.changeProgress
                r4 = 1097859072(0x41700000, float:15.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                int r4 = -r4
                float r4 = (float) r4
                float r2 = r2 * r4
                float r2 = r2 + r13
                r7.translate(r3, r2)
                android.text.StaticLayout r2 = r0.outCounter
                r2.draw(r7)
                r30.restore()
            L_0x062d:
                android.text.StaticLayout r2 = r0.stableCounter
                if (r2 == 0) goto L_0x0658
                r30.save()
                org.telegram.ui.Components.FilterTabsView r2 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r2 = r2.textCounterPaint
                float r1 = r1 * r6
                int r1 = (int) r1
                r2.setAlpha(r1)
                android.graphics.RectF r1 = r0.rect
                float r2 = r1.left
                float r1 = r1.width()
                float r1 = r1 - r9
                r3 = 1073741824(0x40000000, float:2.0)
                float r1 = r1 / r3
                float r2 = r2 + r1
                r7.translate(r2, r13)
                android.text.StaticLayout r1 = r0.stableCounter
                r1.draw(r7)
                r30.restore()
            L_0x0658:
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r1 = r1.textCounterPaint
                r1.setAlpha(r5)
                goto L_0x06a1
            L_0x0662:
                if (r3 == 0) goto L_0x06a1
                org.telegram.ui.Components.FilterTabsView$Tab r1 = r0.currentTab
                int r1 = r1.id
                r4 = 2147483647(0x7fffffff, float:NaN)
                if (r1 == r4) goto L_0x0683
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r1 = r1.textCounterPaint
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                float r4 = r4.editingStartAnimationProgress
                r5 = 1065353216(0x3var_, float:1.0)
                float r4 = r5 - r4
                float r4 = r4 * r6
                int r4 = (int) r4
                r1.setAlpha(r4)
            L_0x0683:
                android.graphics.RectF r1 = r0.rect
                float r4 = r1.left
                float r1 = r1.width()
                float r1 = r1 - r9
                r5 = 1073741824(0x40000000, float:2.0)
                float r1 = r1 / r5
                float r4 = r4 + r1
                r1 = 1097334784(0x41680000, float:14.5)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r2 = r2 + r1
                float r1 = (float) r2
                org.telegram.ui.Components.FilterTabsView r2 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r2 = r2.textCounterPaint
                r7.drawText(r3, r4, r1, r2)
            L_0x06a1:
                if (r11 != 0) goto L_0x06a5
                if (r19 == 0) goto L_0x06a8
            L_0x06a5:
                r30.restore()
            L_0x06a8:
                org.telegram.ui.Components.FilterTabsView$Tab r1 = r0.currentTab
                int r1 = r1.id
                r2 = 2147483647(0x7fffffff, float:NaN)
                if (r1 == r2) goto L_0x0745
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                boolean r1 = r1.isEditing
                if (r1 != 0) goto L_0x06c4
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                float r1 = r1.editingStartAnimationProgress
                r2 = 0
                int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r1 == 0) goto L_0x0745
            L_0x06c4:
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
                float r2 = r2 * r6
                int r2 = (int) r2
                r1.setAlpha(r2)
                r1 = 1077936128(0x40400000, float:3.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                android.graphics.RectF r2 = r0.rect
                float r2 = r2.centerX()
                float r10 = (float) r1
                float r2 = r2 - r10
                android.graphics.RectF r1 = r0.rect
                float r1 = r1.centerY()
                float r3 = r1 - r10
                android.graphics.RectF r1 = r0.rect
                float r1 = r1.centerX()
                float r4 = r1 + r10
                android.graphics.RectF r1 = r0.rect
                float r1 = r1.centerY()
                float r5 = r1 + r10
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.Paint r6 = r1.deletePaint
                r1 = r30
                r1.drawLine(r2, r3, r4, r5, r6)
                android.graphics.RectF r1 = r0.rect
                float r1 = r1.centerX()
                float r2 = r1 - r10
                android.graphics.RectF r1 = r0.rect
                float r1 = r1.centerY()
                float r3 = r1 + r10
                android.graphics.RectF r1 = r0.rect
                float r1 = r1.centerX()
                float r4 = r1 + r10
                android.graphics.RectF r1 = r0.rect
                float r1 = r1.centerY()
                float r5 = r1 - r10
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.Paint r6 = r1.deletePaint
                r1 = r30
                r1.drawLine(r2, r3, r4, r5, r6)
            L_0x0745:
                r2 = r9
            L_0x0746:
                org.telegram.ui.Components.FilterTabsView$Tab r1 = r0.currentTab
                int r1 = r1.id
                r3 = 2147483647(0x7fffffff, float:NaN)
                if (r1 == r3) goto L_0x075d
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                float r1 = r1.editingAnimationProgress
                r3 = 0
                int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
                if (r1 == 0) goto L_0x075d
                r30.restore()
            L_0x075d:
                r0.lastTextX = r8
                org.telegram.ui.Components.FilterTabsView$Tab r1 = r0.currentTab
                int r3 = r1.counter
                r0.lastTabCount = r3
                java.lang.String r3 = r0.currentText
                r0.lastTitle = r3
                int r1 = r1.titleWidth
                r0.lastTitleWidth = r1
                r0.lastCountWidth = r12
                r0.lastCounterWidth = r2
                int r1 = r0.tabWidth
                float r1 = (float) r1
                r0.lastTabWidth = r1
                int r1 = r29.getMeasuredWidth()
                float r1 = (float) r1
                r0.lastWidth = r1
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
                    ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            FilterTabsView.AnonymousClass4.this.lambda$runPendingAnimations$0$FilterTabsView$4(valueAnimator);
                        }
                    });
                    ofFloat.setDuration(getMoveDuration());
                    ofFloat.start();
                }
                super.runPendingAnimations();
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$runPendingAnimations$0 */
            public /* synthetic */ void lambda$runPendingAnimations$0$FilterTabsView$4(ValueAnimator valueAnimator) {
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
                        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                FilterTabsView.AnonymousClass4.lambda$animateMoveImpl$1(FilterTabsView.TabView.this, valueAnimator);
                            }
                        });
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

            static /* synthetic */ void lambda$animateMoveImpl$1(TabView tabView, ValueAnimator valueAnimator) {
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
        this.listView.setSelectorType(7);
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
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new RecyclerListView.OnItemClickListenerExtended() {
            public final void onItemClick(View view, int i, float f, float f2) {
                FilterTabsView.this.lambda$new$0$FilterTabsView(view, i, f, f2);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            public final boolean onItemClick(View view, int i) {
                return FilterTabsView.this.lambda$new$1$FilterTabsView(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                FilterTabsView.this.invalidate();
            }
        });
        addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$FilterTabsView(View view, int i, float f, float f2) {
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
                scrollToTab(tabView.currentTab.id, i);
            } else {
                filterTabsViewDelegate.onSamePageSelected();
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ boolean lambda$new$1$FilterTabsView(View view, int i) {
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

    private void scrollToTab(int i, int i2) {
        int i3 = this.currentPosition;
        boolean z = i3 < i2;
        this.scrollingToChild = -1;
        this.previousPosition = i3;
        this.previousId = this.selectedTabId;
        this.currentPosition = i2;
        this.selectedTabId = i;
        if (this.animatingIndicator) {
            AndroidUtilities.cancelRunOnUIThread(this.animationRunnable);
            this.animatingIndicator = false;
        }
        this.animationTime = 0.0f;
        this.animatingIndicatorProgress = 0.0f;
        this.animatingIndicator = true;
        setEnabled(false);
        AndroidUtilities.runOnUIThread(this.animationRunnable, 16);
        FilterTabsViewDelegate filterTabsViewDelegate = this.delegate;
        if (filterTabsViewDelegate != null) {
            filterTabsViewDelegate.onPageSelected(i, z);
        }
        scrollToChild(i2);
    }

    public void selectFirstTab() {
        scrollToTab(Integer.MAX_VALUE, 0);
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

    public void addTab(int i, int i2, String str) {
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
    /* JADX WARNING: Removed duplicated region for block: B:81:0x01b2  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean drawChild(android.graphics.Canvas r10, android.view.View r11, long r12) {
        /*
            r9 = this;
            boolean r12 = super.drawChild(r10, r11, r12)
            org.telegram.ui.Components.RecyclerListView r13 = r9.listView
            r0 = 1065353216(0x3var_, float:1.0)
            r1 = 0
            if (r11 != r13) goto L_0x011d
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
            if (r3 == 0) goto L_0x011d
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
        L_0x011d:
            long r10 = android.os.SystemClock.elapsedRealtime()
            r2 = 17
            long r4 = r9.lastEditingAnimationTime
            long r4 = r10 - r4
            long r2 = java.lang.Math.min(r2, r4)
            r9.lastEditingAnimationTime = r10
            boolean r10 = r9.isEditing
            r11 = 0
            r13 = 1
            if (r10 != 0) goto L_0x0139
            float r4 = r9.editingAnimationProgress
            int r4 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r4 == 0) goto L_0x0185
        L_0x0139:
            boolean r4 = r9.editingForwardAnimation
            r5 = 1123024896(0x42var_, float:120.0)
            if (r4 == 0) goto L_0x0162
            float r4 = r9.editingAnimationProgress
            int r6 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r6 > 0) goto L_0x0147
            r6 = 1
            goto L_0x0148
        L_0x0147:
            r6 = 0
        L_0x0148:
            float r7 = (float) r2
            float r7 = r7 / r5
            float r4 = r4 + r7
            r9.editingAnimationProgress = r4
            if (r10 != 0) goto L_0x0157
            if (r6 == 0) goto L_0x0157
            int r4 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r4 < 0) goto L_0x0157
            r9.editingAnimationProgress = r1
        L_0x0157:
            float r4 = r9.editingAnimationProgress
            int r4 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1))
            if (r4 < 0) goto L_0x0184
            r9.editingAnimationProgress = r0
            r9.editingForwardAnimation = r11
            goto L_0x0184
        L_0x0162:
            float r4 = r9.editingAnimationProgress
            int r6 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r6 < 0) goto L_0x0169
            r11 = 1
        L_0x0169:
            float r6 = (float) r2
            float r6 = r6 / r5
            float r4 = r4 - r6
            r9.editingAnimationProgress = r4
            if (r10 != 0) goto L_0x0178
            if (r11 == 0) goto L_0x0178
            int r11 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r11 > 0) goto L_0x0178
            r9.editingAnimationProgress = r1
        L_0x0178:
            float r11 = r9.editingAnimationProgress
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
            int r11 = (r11 > r4 ? 1 : (r11 == r4 ? 0 : -1))
            if (r11 > 0) goto L_0x0184
            r9.editingAnimationProgress = r4
            r9.editingForwardAnimation = r13
        L_0x0184:
            r11 = 1
        L_0x0185:
            r4 = 1127481344(0x43340000, float:180.0)
            if (r10 == 0) goto L_0x019b
            float r10 = r9.editingStartAnimationProgress
            int r1 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
            if (r1 >= 0) goto L_0x01af
            float r11 = (float) r2
            float r11 = r11 / r4
            float r10 = r10 + r11
            r9.editingStartAnimationProgress = r10
            int r10 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
            if (r10 <= 0) goto L_0x01b0
            r9.editingStartAnimationProgress = r0
            goto L_0x01b0
        L_0x019b:
            if (r10 != 0) goto L_0x01af
            float r10 = r9.editingStartAnimationProgress
            int r0 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x01af
            float r11 = (float) r2
            float r11 = r11 / r4
            float r10 = r10 - r11
            r9.editingStartAnimationProgress = r10
            int r10 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1))
            if (r10 >= 0) goto L_0x01b0
            r9.editingStartAnimationProgress = r1
            goto L_0x01b0
        L_0x01af:
            r13 = r11
        L_0x01b0:
            if (r13 == 0) goto L_0x01ba
            org.telegram.ui.Components.RecyclerListView r10 = r9.listView
            r10.invalidateViews()
            r9.invalidate()
        L_0x01ba:
            return r12
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FilterTabsView.drawChild(android.graphics.Canvas, android.view.View, long):boolean");
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (!this.tabs.isEmpty()) {
            int size = (View.MeasureSpec.getSize(i) - AndroidUtilities.dp(7.0f)) - AndroidUtilities.dp(7.0f);
            Tab tab = this.tabs.get(0);
            tab.setTitle(LocaleController.getString("FilterAllChats", NUM));
            int width = tab.getWidth(false);
            tab.setTitle(this.allTabsWidth > size ? LocaleController.getString("FilterAllChatsShort", NUM) : LocaleController.getString("FilterAllChats", NUM));
            int width2 = (this.allTabsWidth - width) + tab.getWidth(false);
            int i3 = this.additionalTabWidth;
            int size2 = width2 < size ? (size - width2) / this.tabs.size() : 0;
            this.additionalTabWidth = size2;
            if (i3 != size2) {
                this.ignoreLayout = true;
                this.adapter.notifyDataSetChanged();
                this.ignoreLayout = false;
            }
            updateTabsWidths();
            this.invalidated = false;
        }
        super.onMeasure(i, i2);
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
                tLRPC$TL_messages_updateDialogFiltersOrder.order.add(Integer.valueOf(arrayList.get(i).id));
            }
            ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(tLRPC$TL_messages_updateDialogFiltersOrder, $$Lambda$FilterTabsView$0gw0M2Mpnl7SUImCutpjvb1JvBw.INSTANCE);
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
                    this.tabs.get(0).setTitle(LocaleController.getString("FilterAllChats", NUM));
                } else {
                    z = true;
                }
            }
            i++;
        }
        this.invalidated = true;
        requestLayout();
        this.allTabsWidth = 0;
        this.tabs.get(0).setTitle(LocaleController.getString("FilterAllChats", NUM));
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
                    this.tabs.get(0).setTitle(LocaleController.getString("FilterAllChats", NUM));
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
            ((TabView) viewHolder.itemView).setTab((Tab) FilterTabsView.this.tabs.get(i), i);
        }

        public void swapElements(int i, int i2) {
            int i3 = i - 1;
            int i4 = i2 - 1;
            int size = FilterTabsView.this.tabs.size() - 1;
            if (i3 >= 0 && i4 >= 0 && i3 < size && i4 < size) {
                ArrayList<MessagesController.DialogFilter> arrayList = MessagesController.getInstance(UserConfig.selectedAccount).dialogFilters;
                MessagesController.DialogFilter dialogFilter = arrayList.get(i3);
                MessagesController.DialogFilter dialogFilter2 = arrayList.get(i4);
                int i5 = dialogFilter.order;
                dialogFilter.order = dialogFilter2.order;
                dialogFilter2.order = i5;
                arrayList.set(i3, dialogFilter2);
                arrayList.set(i4, dialogFilter);
                Tab tab = (Tab) FilterTabsView.this.tabs.get(i);
                Tab tab2 = (Tab) FilterTabsView.this.tabs.get(i2);
                int i6 = tab.id;
                tab.id = tab2.id;
                tab2.id = i6;
                int i7 = FilterTabsView.this.positionToStableId.get(i);
                FilterTabsView.this.positionToStableId.put(i, FilterTabsView.this.positionToStableId.get(i2));
                FilterTabsView.this.positionToStableId.put(i2, i7);
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
            if (!FilterTabsView.this.isEditing || viewHolder.getAdapterPosition() == 0) {
                return ItemTouchHelper.Callback.makeMovementFlags(0, 0);
            }
            return ItemTouchHelper.Callback.makeMovementFlags(12, 0);
        }

        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
            if (viewHolder.getAdapterPosition() == 0 || viewHolder2.getAdapterPosition() == 0) {
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
}
