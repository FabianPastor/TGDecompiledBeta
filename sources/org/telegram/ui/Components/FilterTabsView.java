package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
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
import android.widget.TextView;
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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.RecyclerListView;

public class FilterTabsView extends FrameLayout {
    private final Property<FilterTabsView, Float> COLORS = new AnimationProperties.FloatProperty<FilterTabsView>("animationValue") {
        public void setValue(FilterTabsView object, float value) {
            float unused = FilterTabsView.this.animationValue = value;
            FilterTabsView.this.selectorDrawable.setColor(ColorUtils.blendARGB(Theme.getColor(FilterTabsView.this.tabLineColorKey), Theme.getColor(FilterTabsView.this.aTabLineColorKey), value));
            FilterTabsView.this.listView.invalidateViews();
            FilterTabsView.this.listView.invalidate();
            object.invalidate();
        }

        public Float get(FilterTabsView object) {
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
                long dt = SystemClock.elapsedRealtime() - FilterTabsView.this.lastAnimationTime;
                if (dt > 17) {
                    dt = 17;
                }
                FilterTabsView.access$2616(FilterTabsView.this, ((float) dt) / 200.0f);
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
    private boolean animationRunning;
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

    static /* synthetic */ float access$2616(FilterTabsView x0, float x1) {
        float f = x0.animationTime + x1;
        x0.animationTime = f;
        return f;
    }

    public int getCurrentTabStableId() {
        return this.positionToStableId.get(this.currentPosition, -1);
    }

    public int getStableId(int selectedType) {
        return this.positionToStableId.get(selectedType, -1);
    }

    public class Tab {
        public int counter;
        public int id;
        public boolean isDefault;
        public boolean isLocked;
        public String title;
        public int titleWidth;

        public Tab(int i, String t) {
            this.id = i;
            this.title = t;
        }

        public int getWidth(boolean store) {
            int c;
            int width = (int) Math.ceil((double) FilterTabsView.this.textPaint.measureText(this.title));
            this.titleWidth = width;
            if (store) {
                c = FilterTabsView.this.delegate.getTabCounter(this.id);
                if (c < 0) {
                    c = 0;
                }
                if (store) {
                    this.counter = c;
                }
            } else {
                c = this.counter;
            }
            if (c > 0) {
                String counterText = String.format("%d", new Object[]{Integer.valueOf(c)});
                width += AndroidUtilities.dp(6.0f) + Math.max(AndroidUtilities.dp(10.0f), (int) Math.ceil((double) FilterTabsView.this.textCounterPaint.measureText(counterText))) + AndroidUtilities.dp(10.0f);
            }
            return Math.max(AndroidUtilities.dp(40.0f), width);
        }

        public boolean setTitle(String newTitle) {
            if (TextUtils.equals(this.title, newTitle)) {
                return false;
            }
            this.title = newTitle;
            return true;
        }
    }

    public class TabView extends View {
        public boolean animateChange;
        public boolean animateCounterChange;
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
        StaticLayout lastTitleLayout;
        private int lastTitleWidth;
        private float lastWidth;
        /* access modifiers changed from: private */
        public float locIconXOffset;
        StaticLayout outCounter;
        /* access modifiers changed from: private */
        public float progressToLocked;
        /* access modifiers changed from: private */
        public RectF rect = new RectF();
        private float rotation;
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

        public void setTab(Tab tab, int position) {
            this.currentTab = tab;
            this.currentPosition = position;
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
            this.animateCounterChange = false;
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
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(this.currentTab.getWidth(false) + AndroidUtilities.dp(32.0f) + FilterTabsView.this.additionalTabWidth, View.MeasureSpec.getSize(heightMeasureSpec));
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:181:0x0555  */
        /* JADX WARNING: Removed duplicated region for block: B:187:0x0571  */
        /* JADX WARNING: Removed duplicated region for block: B:188:0x0582  */
        /* JADX WARNING: Removed duplicated region for block: B:194:0x05a3  */
        /* JADX WARNING: Removed duplicated region for block: B:198:0x05cd  */
        /* JADX WARNING: Removed duplicated region for block: B:221:0x0700  */
        /* JADX WARNING: Removed duplicated region for block: B:230:0x0748  */
        /* JADX WARNING: Removed duplicated region for block: B:236:0x07fd  */
        /* JADX WARNING: Removed duplicated region for block: B:259:0x08a2  */
        /* JADX WARNING: Removed duplicated region for block: B:262:0x08be  */
        /* JADX WARNING: Removed duplicated region for block: B:265:0x091c  */
        /* JADX WARNING: Removed duplicated region for block: B:266:0x094f  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onDraw(android.graphics.Canvas r39) {
            /*
                r38 = this;
                r0 = r38
                r7 = r39
                org.telegram.ui.Components.FilterTabsView$Tab r1 = r0.currentTab
                boolean r1 = r1.isDefault
                r2 = 1
                if (r1 == 0) goto L_0x001a
                int r1 = org.telegram.messenger.UserConfig.selectedAccount
                org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
                boolean r1 = r1.isPremium()
                if (r1 == 0) goto L_0x0018
                goto L_0x001a
            L_0x0018:
                r1 = 0
                goto L_0x001b
            L_0x001a:
                r1 = 1
            L_0x001b:
                r8 = r1
                org.telegram.ui.Components.FilterTabsView$Tab r1 = r0.currentTab
                boolean r1 = r1.isDefault
                if (r1 != 0) goto L_0x0026
                if (r8 == 0) goto L_0x0026
                r1 = 1
                goto L_0x0027
            L_0x0026:
                r1 = 0
            L_0x0027:
                r9 = r1
                r10 = 0
                r11 = 1073741824(0x40000000, float:2.0)
                if (r8 == 0) goto L_0x0069
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                float r1 = r1.editingAnimationProgress
                int r1 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
                if (r1 == 0) goto L_0x0069
                r39.save()
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                float r1 = r1.editingAnimationProgress
                int r4 = r0.currentPosition
                int r4 = r4 % 2
                if (r4 != 0) goto L_0x0049
                r4 = 1065353216(0x3var_, float:1.0)
                goto L_0x004b
            L_0x0049:
                r4 = -1082130432(0xffffffffbvar_, float:-1.0)
            L_0x004b:
                float r1 = r1 * r4
                r4 = 1059648963(0x3var_f5c3, float:0.66)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                float r4 = (float) r4
                float r4 = r4 * r1
                r7.translate(r4, r10)
                int r4 = r38.getMeasuredWidth()
                float r4 = (float) r4
                float r4 = r4 / r11
                int r5 = r38.getMeasuredHeight()
                float r5 = (float) r5
                float r5 = r5 / r11
                r7.rotate(r1, r4, r5)
            L_0x0069:
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                int r1 = r1.manualScrollingToId
                r4 = -1
                if (r1 == r4) goto L_0x0081
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                int r1 = r1.manualScrollingToId
                org.telegram.ui.Components.FilterTabsView r5 = org.telegram.ui.Components.FilterTabsView.this
                int r5 = r5.selectedTabId
                r13 = r1
                r14 = r5
                goto L_0x008f
            L_0x0081:
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                int r1 = r1.selectedTabId
                org.telegram.ui.Components.FilterTabsView r5 = org.telegram.ui.Components.FilterTabsView.this
                int r5 = r5.previousId
                r13 = r1
                r14 = r5
            L_0x008f:
                org.telegram.ui.Components.FilterTabsView$Tab r1 = r0.currentTab
                int r1 = r1.id
                if (r1 != r13) goto L_0x00bd
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r1 = r1.activeTextColorKey
                org.telegram.ui.Components.FilterTabsView r5 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r5 = r5.aActiveTextColorKey
                org.telegram.ui.Components.FilterTabsView r6 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r6 = r6.unactiveTextColorKey
                org.telegram.ui.Components.FilterTabsView r15 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r15 = r15.aUnactiveTextColorKey
                java.lang.String r16 = "chats_tabUnreadActiveBackground"
                java.lang.String r17 = "chats_tabUnreadUnactiveBackground"
                r18 = r15
                r19 = r16
                r20 = r17
                r15 = r1
                r16 = r5
                r17 = r6
                goto L_0x00e4
            L_0x00bd:
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r1 = r1.unactiveTextColorKey
                org.telegram.ui.Components.FilterTabsView r5 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r5 = r5.aUnactiveTextColorKey
                org.telegram.ui.Components.FilterTabsView r6 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r6 = r6.activeTextColorKey
                org.telegram.ui.Components.FilterTabsView r15 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r15 = r15.aUnactiveTextColorKey
                java.lang.String r16 = "chats_tabUnreadUnactiveBackground"
                java.lang.String r17 = "chats_tabUnreadActiveBackground"
                r18 = r15
                r19 = r16
                r20 = r17
                r15 = r1
                r16 = r5
                r17 = r6
            L_0x00e4:
                if (r16 != 0) goto L_0x012e
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                boolean r1 = r1.animatingIndicator
                if (r1 != 0) goto L_0x00f6
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                int r1 = r1.manualScrollingToId
                if (r1 == r4) goto L_0x0103
            L_0x00f6:
                org.telegram.ui.Components.FilterTabsView$Tab r1 = r0.currentTab
                int r1 = r1.id
                if (r1 == r13) goto L_0x0112
                org.telegram.ui.Components.FilterTabsView$Tab r1 = r0.currentTab
                int r1 = r1.id
                if (r1 != r14) goto L_0x0103
                goto L_0x0112
            L_0x0103:
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r1 = r1.textPaint
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r15)
                r1.setColor(r5)
                goto L_0x0197
            L_0x0112:
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r1 = r1.textPaint
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r17)
                int r6 = org.telegram.ui.ActionBar.Theme.getColor(r15)
                org.telegram.ui.Components.FilterTabsView r12 = org.telegram.ui.Components.FilterTabsView.this
                float r12 = r12.animatingIndicatorProgress
                int r5 = androidx.core.graphics.ColorUtils.blendARGB(r5, r6, r12)
                r1.setColor(r5)
                goto L_0x0197
            L_0x012e:
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r15)
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r16)
                org.telegram.ui.Components.FilterTabsView r6 = org.telegram.ui.Components.FilterTabsView.this
                boolean r6 = r6.animatingIndicator
                if (r6 != 0) goto L_0x0146
                org.telegram.ui.Components.FilterTabsView r6 = org.telegram.ui.Components.FilterTabsView.this
                int r6 = r6.manualScrollingToPosition
                if (r6 == r4) goto L_0x0153
            L_0x0146:
                org.telegram.ui.Components.FilterTabsView$Tab r6 = r0.currentTab
                int r6 = r6.id
                if (r6 == r13) goto L_0x0167
                org.telegram.ui.Components.FilterTabsView$Tab r6 = r0.currentTab
                int r6 = r6.id
                if (r6 != r14) goto L_0x0153
                goto L_0x0167
            L_0x0153:
                org.telegram.ui.Components.FilterTabsView r6 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r6 = r6.textPaint
                org.telegram.ui.Components.FilterTabsView r12 = org.telegram.ui.Components.FilterTabsView.this
                float r12 = r12.animationValue
                int r12 = androidx.core.graphics.ColorUtils.blendARGB(r1, r5, r12)
                r6.setColor(r12)
                goto L_0x0197
            L_0x0167:
                int r6 = org.telegram.ui.ActionBar.Theme.getColor(r17)
                int r12 = org.telegram.ui.ActionBar.Theme.getColor(r18)
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r4 = r4.textPaint
                org.telegram.ui.Components.FilterTabsView r11 = org.telegram.ui.Components.FilterTabsView.this
                float r11 = r11.animationValue
                int r11 = androidx.core.graphics.ColorUtils.blendARGB(r6, r12, r11)
                org.telegram.ui.Components.FilterTabsView r10 = org.telegram.ui.Components.FilterTabsView.this
                float r10 = r10.animationValue
                int r10 = androidx.core.graphics.ColorUtils.blendARGB(r1, r5, r10)
                org.telegram.ui.Components.FilterTabsView r3 = org.telegram.ui.Components.FilterTabsView.this
                float r3 = r3.animatingIndicatorProgress
                int r3 = androidx.core.graphics.ColorUtils.blendARGB(r11, r10, r3)
                r4.setColor(r3)
            L_0x0197:
                int r1 = r0.animateFromTabCount
                if (r1 != 0) goto L_0x01a1
                boolean r3 = r0.animateTabCounter
                if (r3 == 0) goto L_0x01a1
                r3 = 1
                goto L_0x01a2
            L_0x01a1:
                r3 = 0
            L_0x01a2:
                r10 = r3
                if (r1 <= 0) goto L_0x01b1
                org.telegram.ui.Components.FilterTabsView$Tab r1 = r0.currentTab
                int r1 = r1.counter
                if (r1 != 0) goto L_0x01b1
                boolean r1 = r0.animateTabCounter
                if (r1 == 0) goto L_0x01b1
                r1 = 1
                goto L_0x01b2
            L_0x01b1:
                r1 = 0
            L_0x01b2:
                r11 = r1
                int r1 = r0.animateFromTabCount
                if (r1 <= 0) goto L_0x01c3
                org.telegram.ui.Components.FilterTabsView$Tab r1 = r0.currentTab
                int r1 = r1.counter
                if (r1 <= 0) goto L_0x01c3
                boolean r1 = r0.animateTabCounter
                if (r1 == 0) goto L_0x01c3
                r1 = 1
                goto L_0x01c4
            L_0x01c3:
                r1 = 0
            L_0x01c4:
                r12 = r1
                org.telegram.ui.Components.FilterTabsView$Tab r1 = r0.currentTab
                int r1 = r1.counter
                if (r1 > 0) goto L_0x01d3
                if (r11 == 0) goto L_0x01ce
                goto L_0x01d3
            L_0x01ce:
                r1 = 0
                r2 = 0
                r3 = 0
                r6 = r1
                goto L_0x021c
            L_0x01d3:
                java.lang.String r1 = "%d"
                if (r11 == 0) goto L_0x01e7
                java.lang.Object[] r2 = new java.lang.Object[r2]
                int r3 = r0.animateFromTabCount
                java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                r4 = 0
                r2[r4] = r3
                java.lang.String r1 = java.lang.String.format(r1, r2)
                goto L_0x01f8
            L_0x01e7:
                r4 = 0
                java.lang.Object[] r2 = new java.lang.Object[r2]
                org.telegram.ui.Components.FilterTabsView$Tab r3 = r0.currentTab
                int r3 = r3.counter
                java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                r2[r4] = r3
                java.lang.String r1 = java.lang.String.format(r1, r2)
            L_0x01f8:
                org.telegram.ui.Components.FilterTabsView r2 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r2 = r2.textCounterPaint
                float r2 = r2.measureText(r1)
                double r2 = (double) r2
                double r2 = java.lang.Math.ceil(r2)
                int r2 = (int) r2
                float r2 = (float) r2
                r3 = 1092616192(0x41200000, float:10.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
                float r4 = (float) r4
                float r4 = java.lang.Math.max(r4, r2)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                float r4 = r4 + r3
                int r3 = (int) r4
                r6 = r1
            L_0x021c:
                r1 = 1101004800(0x41a00000, float:20.0)
                if (r9 == 0) goto L_0x0246
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                boolean r4 = r4.isEditing
                if (r4 != 0) goto L_0x0233
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                float r4 = r4.editingStartAnimationProgress
                r5 = 0
                int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
                if (r4 == 0) goto L_0x0246
            L_0x0233:
                float r4 = (float) r3
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r5 = r5 - r3
                float r5 = (float) r5
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                float r1 = r1.editingStartAnimationProgress
                float r5 = r5 * r1
                float r4 = r4 + r5
                int r3 = (int) r4
                r5 = r3
                goto L_0x0247
            L_0x0246:
                r5 = r3
            L_0x0247:
                org.telegram.ui.Components.FilterTabsView$Tab r1 = r0.currentTab
                int r1 = r1.titleWidth
                r3 = 1086324736(0x40CLASSNAME, float:6.0)
                if (r5 == 0) goto L_0x0264
                if (r11 != 0) goto L_0x0264
                if (r6 == 0) goto L_0x0256
                r4 = 1065353216(0x3var_, float:1.0)
                goto L_0x025c
            L_0x0256:
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                float r4 = r4.editingStartAnimationProgress
            L_0x025c:
                float r4 = r4 * r3
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                int r4 = r4 + r5
                goto L_0x0265
            L_0x0264:
                r4 = 0
            L_0x0265:
                int r1 = r1 + r4
                r0.tabWidth = r1
                int r1 = r38.getMeasuredWidth()
                int r4 = r0.tabWidth
                int r1 = r1 - r4
                float r1 = (float) r1
                r4 = 1073741824(0x40000000, float:2.0)
                float r1 = r1 / r4
                boolean r4 = r0.animateTextX
                if (r4 == 0) goto L_0x0287
                float r4 = r0.changeProgress
                float r26 = r1 * r4
                float r3 = r0.animateFromTextX
                r21 = 1065353216(0x3var_, float:1.0)
                float r4 = r21 - r4
                float r3 = r3 * r4
                float r1 = r26 + r3
                r4 = r1
                goto L_0x0288
            L_0x0287:
                r4 = r1
            L_0x0288:
                org.telegram.ui.Components.FilterTabsView$Tab r1 = r0.currentTab
                java.lang.String r1 = r1.title
                java.lang.String r3 = r0.currentText
                boolean r1 = android.text.TextUtils.equals(r1, r3)
                if (r1 != 0) goto L_0x02e4
                org.telegram.ui.Components.FilterTabsView$Tab r1 = r0.currentTab
                java.lang.String r1 = r1.title
                r0.currentText = r1
                org.telegram.ui.Components.FilterTabsView r3 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r3 = r3.textPaint
                android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
                r28 = r15
                r26 = 1097859072(0x41700000, float:15.0)
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r26)
                r29 = r8
                r8 = 0
                java.lang.CharSequence r1 = org.telegram.messenger.Emoji.replaceEmoji(r1, r3, r15, r8)
                android.text.StaticLayout r3 = new android.text.StaticLayout
                org.telegram.ui.Components.FilterTabsView r8 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r32 = r8.textPaint
                r8 = 1137180672(0x43CLASSNAME, float:400.0)
                int r33 = org.telegram.messenger.AndroidUtilities.dp(r8)
                android.text.Layout$Alignment r34 = android.text.Layout.Alignment.ALIGN_NORMAL
                r35 = 1065353216(0x3var_, float:1.0)
                r36 = 0
                r37 = 0
                r30 = r3
                r31 = r1
                r30.<init>(r31, r32, r33, r34, r35, r36, r37)
                r0.textLayout = r3
                int r3 = r3.getHeight()
                r0.textHeight = r3
                android.text.StaticLayout r3 = r0.textLayout
                r8 = 0
                float r3 = r3.getLineLeft(r8)
                float r3 = -r3
                int r3 = (int) r3
                r0.textOffsetX = r3
                goto L_0x02e8
            L_0x02e4:
                r29 = r8
                r28 = r15
            L_0x02e8:
                r1 = 0
                boolean r3 = r0.animateTextChange
                if (r3 == 0) goto L_0x03cd
                float r3 = r0.titleXOffset
                boolean r8 = r0.animateTextChangeOut
                if (r8 == 0) goto L_0x02f6
                float r8 = r0.changeProgress
                goto L_0x02fc
            L_0x02f6:
                float r8 = r0.changeProgress
                r15 = 1065353216(0x3var_, float:1.0)
                float r8 = r15 - r8
            L_0x02fc:
                float r1 = r3 * r8
                android.text.StaticLayout r3 = r0.titleAnimateStableLayout
                if (r3 == 0) goto L_0x0323
                r39.save()
                int r3 = r0.textOffsetX
                float r3 = (float) r3
                float r3 = r3 + r4
                float r3 = r3 + r1
                int r8 = r38.getMeasuredHeight()
                int r15 = r0.textHeight
                int r8 = r8 - r15
                float r8 = (float) r8
                r15 = 1073741824(0x40000000, float:2.0)
                float r8 = r8 / r15
                r15 = 1065353216(0x3var_, float:1.0)
                float r8 = r8 + r15
                r7.translate(r3, r8)
                android.text.StaticLayout r3 = r0.titleAnimateStableLayout
                r3.draw(r7)
                r39.restore()
            L_0x0323:
                android.text.StaticLayout r3 = r0.titleAnimateInLayout
                if (r3 == 0) goto L_0x0378
                r39.save()
                org.telegram.ui.Components.FilterTabsView r3 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r3 = r3.textPaint
                int r3 = r3.getAlpha()
                org.telegram.ui.Components.FilterTabsView r8 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r8 = r8.textPaint
                float r15 = (float) r3
                r30 = r11
                boolean r11 = r0.animateTextChangeOut
                if (r11 == 0) goto L_0x0348
                float r11 = r0.changeProgress
                r21 = 1065353216(0x3var_, float:1.0)
                float r11 = r21 - r11
                goto L_0x034a
            L_0x0348:
                float r11 = r0.changeProgress
            L_0x034a:
                float r15 = r15 * r11
                int r11 = (int) r15
                r8.setAlpha(r11)
                int r8 = r0.textOffsetX
                float r8 = (float) r8
                float r8 = r8 + r4
                float r8 = r8 + r1
                int r11 = r38.getMeasuredHeight()
                int r15 = r0.textHeight
                int r11 = r11 - r15
                float r11 = (float) r11
                r15 = 1073741824(0x40000000, float:2.0)
                float r11 = r11 / r15
                r15 = 1065353216(0x3var_, float:1.0)
                float r11 = r11 + r15
                r7.translate(r8, r11)
                android.text.StaticLayout r8 = r0.titleAnimateInLayout
                r8.draw(r7)
                r39.restore()
                org.telegram.ui.Components.FilterTabsView r8 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r8 = r8.textPaint
                r8.setAlpha(r3)
                goto L_0x037a
            L_0x0378:
                r30 = r11
            L_0x037a:
                android.text.StaticLayout r3 = r0.titleAnimateOutLayout
                if (r3 == 0) goto L_0x03f3
                r39.save()
                org.telegram.ui.Components.FilterTabsView r3 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r3 = r3.textPaint
                int r3 = r3.getAlpha()
                org.telegram.ui.Components.FilterTabsView r8 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r8 = r8.textPaint
                float r11 = (float) r3
                boolean r15 = r0.animateTextChangeOut
                if (r15 == 0) goto L_0x0399
                float r15 = r0.changeProgress
                goto L_0x039f
            L_0x0399:
                float r15 = r0.changeProgress
                r21 = 1065353216(0x3var_, float:1.0)
                float r15 = r21 - r15
            L_0x039f:
                float r11 = r11 * r15
                int r11 = (int) r11
                r8.setAlpha(r11)
                int r8 = r0.textOffsetX
                float r8 = (float) r8
                float r8 = r8 + r4
                float r8 = r8 + r1
                int r11 = r38.getMeasuredHeight()
                int r15 = r0.textHeight
                int r11 = r11 - r15
                float r11 = (float) r11
                r15 = 1073741824(0x40000000, float:2.0)
                float r11 = r11 / r15
                r15 = 1065353216(0x3var_, float:1.0)
                float r11 = r11 + r15
                r7.translate(r8, r11)
                android.text.StaticLayout r8 = r0.titleAnimateOutLayout
                r8.draw(r7)
                r39.restore()
                org.telegram.ui.Components.FilterTabsView r8 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r8 = r8.textPaint
                r8.setAlpha(r3)
                goto L_0x03f3
            L_0x03cd:
                r30 = r11
                android.text.StaticLayout r3 = r0.textLayout
                if (r3 == 0) goto L_0x03f3
                r39.save()
                int r3 = r0.textOffsetX
                float r3 = (float) r3
                float r3 = r3 + r4
                int r8 = r38.getMeasuredHeight()
                int r11 = r0.textHeight
                int r8 = r8 - r11
                float r8 = (float) r8
                r11 = 1073741824(0x40000000, float:2.0)
                float r8 = r8 / r11
                r11 = 1065353216(0x3var_, float:1.0)
                float r8 = r8 + r11
                r7.translate(r3, r8)
                android.text.StaticLayout r3 = r0.textLayout
                r3.draw(r7)
                r39.restore()
            L_0x03f3:
                r8 = r1
                if (r10 != 0) goto L_0x0419
                if (r6 != 0) goto L_0x0419
                if (r9 == 0) goto L_0x040e
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                boolean r1 = r1.isEditing
                if (r1 != 0) goto L_0x0419
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                float r1 = r1.editingStartAnimationProgress
                r3 = 0
                int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
                if (r1 == 0) goto L_0x040e
                goto L_0x0419
            L_0x040e:
                r11 = r6
                r32 = r8
                r26 = r9
                r27 = r10
                r9 = r4
                r10 = r5
                goto L_0x080a
            L_0x0419:
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r1 = r1.aBackgroundColorKey
                if (r1 != 0) goto L_0x0435
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r1 = r1.textCounterPaint
                org.telegram.ui.Components.FilterTabsView r3 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r3 = r3.backgroundColorKey
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r1.setColor(r3)
                goto L_0x045c
            L_0x0435:
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r1 = r1.backgroundColorKey
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                org.telegram.ui.Components.FilterTabsView r3 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r3 = r3.aBackgroundColorKey
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                org.telegram.ui.Components.FilterTabsView r11 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r11 = r11.textCounterPaint
                org.telegram.ui.Components.FilterTabsView r15 = org.telegram.ui.Components.FilterTabsView.this
                float r15 = r15.animationValue
                int r15 = androidx.core.graphics.ColorUtils.blendARGB(r1, r3, r15)
                r11.setColor(r15)
            L_0x045c:
                boolean r1 = org.telegram.ui.ActionBar.Theme.hasThemeKey(r19)
                if (r1 == 0) goto L_0x04ad
                boolean r1 = org.telegram.ui.ActionBar.Theme.hasThemeKey(r20)
                if (r1 == 0) goto L_0x04ad
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r19)
                org.telegram.ui.Components.FilterTabsView r3 = org.telegram.ui.Components.FilterTabsView.this
                boolean r3 = r3.animatingIndicator
                if (r3 != 0) goto L_0x047d
                org.telegram.ui.Components.FilterTabsView r3 = org.telegram.ui.Components.FilterTabsView.this
                int r3 = r3.manualScrollingToPosition
                r11 = -1
                if (r3 == r11) goto L_0x048a
            L_0x047d:
                org.telegram.ui.Components.FilterTabsView$Tab r3 = r0.currentTab
                int r3 = r3.id
                if (r3 == r13) goto L_0x0494
                org.telegram.ui.Components.FilterTabsView$Tab r3 = r0.currentTab
                int r3 = r3.id
                if (r3 != r14) goto L_0x048a
                goto L_0x0494
            L_0x048a:
                org.telegram.ui.Components.FilterTabsView r3 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.Paint r3 = r3.counterPaint
                r3.setColor(r1)
                goto L_0x04ac
            L_0x0494:
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r20)
                org.telegram.ui.Components.FilterTabsView r11 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.Paint r11 = r11.counterPaint
                org.telegram.ui.Components.FilterTabsView r15 = org.telegram.ui.Components.FilterTabsView.this
                float r15 = r15.animatingIndicatorProgress
                int r15 = androidx.core.graphics.ColorUtils.blendARGB(r3, r1, r15)
                r11.setColor(r15)
            L_0x04ac:
                goto L_0x04c0
            L_0x04ad:
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.Paint r1 = r1.counterPaint
                org.telegram.ui.Components.FilterTabsView r3 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r3 = r3.textPaint
                int r3 = r3.getColor()
                r1.setColor(r3)
            L_0x04c0:
                org.telegram.ui.Components.FilterTabsView$Tab r1 = r0.currentTab
                int r1 = r1.titleWidth
                float r1 = (float) r1
                boolean r3 = r0.animateTextChange
                if (r3 == 0) goto L_0x04e1
                int r3 = r0.animateFromTitleWidth
                float r3 = (float) r3
                float r11 = r0.changeProgress
                r15 = 1065353216(0x3var_, float:1.0)
                float r11 = r15 - r11
                float r3 = r3 * r11
                org.telegram.ui.Components.FilterTabsView$Tab r11 = r0.currentTab
                int r11 = r11.titleWidth
                float r11 = (float) r11
                float r15 = r0.changeProgress
                float r11 = r11 * r15
                float r1 = r3 + r11
                r11 = r1
                goto L_0x04e2
            L_0x04e1:
                r11 = r1
            L_0x04e2:
                boolean r1 = r0.animateTextChange
                if (r1 == 0) goto L_0x04fa
                android.text.StaticLayout r1 = r0.titleAnimateOutLayout
                if (r1 != 0) goto L_0x04fa
                float r1 = r0.titleXOffset
                float r1 = r4 - r1
                float r1 = r1 + r8
                float r1 = r1 + r11
                r3 = 1086324736(0x40CLASSNAME, float:6.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                float r1 = r1 + r3
                r15 = r1
                goto L_0x0505
            L_0x04fa:
                r3 = 1086324736(0x40CLASSNAME, float:6.0)
                float r1 = r4 + r11
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                float r1 = r1 + r3
                r15 = r1
            L_0x0505:
                int r1 = r38.getMeasuredHeight()
                r3 = 1101004800(0x41a00000, float:20.0)
                int r22 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r1 = r1 - r22
                int r3 = r1 / 2
                r22 = 1132396544(0x437var_, float:255.0)
                if (r9 == 0) goto L_0x0546
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                boolean r1 = r1.isEditing
                if (r1 != 0) goto L_0x052f
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                float r1 = r1.editingStartAnimationProgress
                r23 = 0
                int r1 = (r1 > r23 ? 1 : (r1 == r23 ? 0 : -1))
                if (r1 == 0) goto L_0x052c
                goto L_0x052f
            L_0x052c:
                r31 = r4
                goto L_0x0548
            L_0x052f:
                if (r6 != 0) goto L_0x0546
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.Paint r1 = r1.counterPaint
                r31 = r4
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                float r4 = r4.editingStartAnimationProgress
                float r4 = r4 * r22
                int r4 = (int) r4
                r1.setAlpha(r4)
                goto L_0x0553
            L_0x0546:
                r31 = r4
            L_0x0548:
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.Paint r1 = r1.counterPaint
                r4 = 255(0xff, float:3.57E-43)
                r1.setAlpha(r4)
            L_0x0553:
                if (r12 == 0) goto L_0x056b
                float r1 = r0.animateFromCountWidth
                float r4 = (float) r5
                int r4 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
                if (r4 == 0) goto L_0x056b
                float r4 = r0.changeProgress
                r21 = 1065353216(0x3var_, float:1.0)
                float r32 = r21 - r4
                float r1 = r1 * r32
                r32 = r8
                float r8 = (float) r5
                float r8 = r8 * r4
                float r1 = r1 + r8
                goto L_0x056e
            L_0x056b:
                r32 = r8
                float r1 = (float) r5
            L_0x056e:
                r8 = r1
                if (r12 == 0) goto L_0x0582
                float r1 = r0.animateFromCounterWidth
                float r4 = r0.changeProgress
                r21 = 1065353216(0x3var_, float:1.0)
                float r33 = r21 - r4
                float r1 = r1 * r33
                float r4 = r4 * r2
                float r2 = r1 + r4
                r33 = r2
                goto L_0x0584
            L_0x0582:
                r33 = r2
            L_0x0584:
                android.graphics.RectF r1 = r0.rect
                float r2 = (float) r3
                float r4 = r15 + r8
                r25 = 1101004800(0x41a00000, float:20.0)
                int r34 = org.telegram.messenger.AndroidUtilities.dp(r25)
                r35 = r5
                int r5 = r3 + r34
                float r5 = (float) r5
                r1.set(r15, r2, r4, r5)
                if (r10 != 0) goto L_0x059b
                if (r30 == 0) goto L_0x05b6
            L_0x059b:
                r39.save()
                float r1 = r0.changeProgress
                if (r10 == 0) goto L_0x05a3
                goto L_0x05a7
            L_0x05a3:
                r2 = 1065353216(0x3var_, float:1.0)
                float r1 = r2 - r1
            L_0x05a7:
                android.graphics.RectF r2 = r0.rect
                float r2 = r2.centerX()
                android.graphics.RectF r4 = r0.rect
                float r4 = r4.centerY()
                r7.scale(r1, r1, r2, r4)
            L_0x05b6:
                android.graphics.RectF r1 = r0.rect
                float r2 = org.telegram.messenger.AndroidUtilities.density
                r4 = 1094189056(0x41380000, float:11.5)
                float r2 = r2 * r4
                float r5 = org.telegram.messenger.AndroidUtilities.density
                float r5 = r5 * r4
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.Paint r4 = r4.counterPaint
                r7.drawRoundRect(r1, r2, r5, r4)
                if (r12 == 0) goto L_0x0700
                float r1 = (float) r3
                android.text.StaticLayout r2 = r0.inCounter
                if (r2 == 0) goto L_0x05ef
                r2 = 1101004800(0x41a00000, float:20.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                android.text.StaticLayout r4 = r0.inCounter
                r5 = 0
                int r4 = r4.getLineBottom(r5)
                r34 = r8
                android.text.StaticLayout r8 = r0.inCounter
                int r5 = r8.getLineTop(r5)
                int r4 = r4 - r5
                int r2 = r2 - r4
                float r2 = (float) r2
                r4 = 1073741824(0x40000000, float:2.0)
                float r2 = r2 / r4
                float r1 = r1 + r2
                goto L_0x062e
            L_0x05ef:
                r34 = r8
                android.text.StaticLayout r2 = r0.outCounter
                if (r2 == 0) goto L_0x0610
                r2 = 1101004800(0x41a00000, float:20.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                android.text.StaticLayout r4 = r0.outCounter
                r5 = 0
                int r4 = r4.getLineBottom(r5)
                android.text.StaticLayout r8 = r0.outCounter
                int r5 = r8.getLineTop(r5)
                int r4 = r4 - r5
                int r2 = r2 - r4
                float r2 = (float) r2
                r4 = 1073741824(0x40000000, float:2.0)
                float r2 = r2 / r4
                float r1 = r1 + r2
                goto L_0x062e
            L_0x0610:
                android.text.StaticLayout r2 = r0.stableCounter
                if (r2 == 0) goto L_0x062e
                r2 = 1101004800(0x41a00000, float:20.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                android.text.StaticLayout r4 = r0.stableCounter
                r5 = 0
                int r4 = r4.getLineBottom(r5)
                android.text.StaticLayout r8 = r0.stableCounter
                int r5 = r8.getLineTop(r5)
                int r4 = r4 - r5
                int r2 = r2 - r4
                float r2 = (float) r2
                r4 = 1073741824(0x40000000, float:2.0)
                float r2 = r2 / r4
                float r1 = r1 + r2
            L_0x062e:
                r2 = 1065353216(0x3var_, float:1.0)
                if (r9 == 0) goto L_0x063c
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                float r4 = r4.editingStartAnimationProgress
                r5 = 1065353216(0x3var_, float:1.0)
                float r2 = r5 - r4
            L_0x063c:
                android.text.StaticLayout r4 = r0.inCounter
                if (r4 == 0) goto L_0x0681
                r39.save()
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r4 = r4.textCounterPaint
                float r5 = r2 * r22
                float r8 = r0.changeProgress
                float r5 = r5 * r8
                int r5 = (int) r5
                r4.setAlpha(r5)
                android.graphics.RectF r4 = r0.rect
                float r4 = r4.left
                android.graphics.RectF r5 = r0.rect
                float r5 = r5.width()
                float r5 = r5 - r33
                r8 = 1073741824(0x40000000, float:2.0)
                float r5 = r5 / r8
                float r4 = r4 + r5
                float r5 = r0.changeProgress
                r8 = 1065353216(0x3var_, float:1.0)
                float r5 = r8 - r5
                r24 = r11
                r8 = 1097859072(0x41700000, float:15.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r8)
                float r8 = (float) r11
                float r5 = r5 * r8
                float r5 = r5 + r1
                r7.translate(r4, r5)
                android.text.StaticLayout r4 = r0.inCounter
                r4.draw(r7)
                r39.restore()
                goto L_0x0683
            L_0x0681:
                r24 = r11
            L_0x0683:
                android.text.StaticLayout r4 = r0.outCounter
                if (r4 == 0) goto L_0x06c6
                r39.save()
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r4 = r4.textCounterPaint
                float r5 = r2 * r22
                float r8 = r0.changeProgress
                r11 = 1065353216(0x3var_, float:1.0)
                float r8 = r11 - r8
                float r5 = r5 * r8
                int r5 = (int) r5
                r4.setAlpha(r5)
                android.graphics.RectF r4 = r0.rect
                float r4 = r4.left
                android.graphics.RectF r5 = r0.rect
                float r5 = r5.width()
                float r5 = r5 - r33
                r8 = 1073741824(0x40000000, float:2.0)
                float r5 = r5 / r8
                float r4 = r4 + r5
                float r5 = r0.changeProgress
                r8 = 1097859072(0x41700000, float:15.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                int r8 = -r8
                float r8 = (float) r8
                float r5 = r5 * r8
                float r5 = r5 + r1
                r7.translate(r4, r5)
                android.text.StaticLayout r4 = r0.outCounter
                r4.draw(r7)
                r39.restore()
            L_0x06c6:
                android.text.StaticLayout r4 = r0.stableCounter
                if (r4 == 0) goto L_0x06f4
                r39.save()
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r4 = r4.textCounterPaint
                float r5 = r2 * r22
                int r5 = (int) r5
                r4.setAlpha(r5)
                android.graphics.RectF r4 = r0.rect
                float r4 = r4.left
                android.graphics.RectF r5 = r0.rect
                float r5 = r5.width()
                float r5 = r5 - r33
                r8 = 1073741824(0x40000000, float:2.0)
                float r5 = r5 / r8
                float r4 = r4 + r5
                r7.translate(r4, r1)
                android.text.StaticLayout r4 = r0.stableCounter
                r4.draw(r7)
                r39.restore()
            L_0x06f4:
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r4 = r4.textCounterPaint
                r5 = 255(0xff, float:3.57E-43)
                r4.setAlpha(r5)
                goto L_0x073f
            L_0x0700:
                r34 = r8
                r24 = r11
                if (r6 == 0) goto L_0x073f
                if (r9 == 0) goto L_0x071e
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r1 = r1.textCounterPaint
                org.telegram.ui.Components.FilterTabsView r2 = org.telegram.ui.Components.FilterTabsView.this
                float r2 = r2.editingStartAnimationProgress
                r4 = 1065353216(0x3var_, float:1.0)
                float r2 = r4 - r2
                float r2 = r2 * r22
                int r2 = (int) r2
                r1.setAlpha(r2)
            L_0x071e:
                android.graphics.RectF r1 = r0.rect
                float r1 = r1.left
                android.graphics.RectF r2 = r0.rect
                float r2 = r2.width()
                float r2 = r2 - r33
                r4 = 1073741824(0x40000000, float:2.0)
                float r2 = r2 / r4
                float r1 = r1 + r2
                r2 = 1097334784(0x41680000, float:14.5)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r2 = r2 + r3
                float r2 = (float) r2
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                android.text.TextPaint r4 = r4.textCounterPaint
                r7.drawText(r6, r1, r2, r4)
            L_0x073f:
                if (r10 != 0) goto L_0x0743
                if (r30 == 0) goto L_0x0746
            L_0x0743:
                r39.restore()
            L_0x0746:
                if (r9 == 0) goto L_0x07fd
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                boolean r1 = r1.isEditing
                if (r1 != 0) goto L_0x0767
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                float r1 = r1.editingStartAnimationProgress
                r2 = 0
                int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r1 == 0) goto L_0x075c
                goto L_0x0767
            L_0x075c:
                r11 = r6
                r26 = r9
                r27 = r10
                r9 = r31
                r10 = r35
                goto L_0x0808
            L_0x0767:
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
                float r2 = r2 * r22
                int r2 = (int) r2
                r1.setAlpha(r2)
                r1 = 1077936128(0x40400000, float:3.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r1)
                android.graphics.RectF r1 = r0.rect
                float r1 = r1.centerX()
                float r2 = (float) r8
                float r2 = r1 - r2
                android.graphics.RectF r1 = r0.rect
                float r1 = r1.centerY()
                float r4 = (float) r8
                float r4 = r1 - r4
                android.graphics.RectF r1 = r0.rect
                float r1 = r1.centerX()
                float r5 = (float) r8
                float r5 = r5 + r1
                android.graphics.RectF r1 = r0.rect
                float r1 = r1.centerY()
                float r11 = (float) r8
                float r11 = r11 + r1
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.Paint r22 = r1.deletePaint
                r1 = r39
                r25 = r3
                r3 = r4
                r26 = r9
                r9 = r31
                r4 = r5
                r27 = r10
                r10 = r35
                r5 = r11
                r11 = r6
                r6 = r22
                r1.drawLine(r2, r3, r4, r5, r6)
                android.graphics.RectF r1 = r0.rect
                float r1 = r1.centerX()
                float r2 = (float) r8
                float r2 = r1 - r2
                android.graphics.RectF r1 = r0.rect
                float r1 = r1.centerY()
                float r3 = (float) r8
                float r3 = r3 + r1
                android.graphics.RectF r1 = r0.rect
                float r1 = r1.centerX()
                float r4 = (float) r8
                float r4 = r4 + r1
                android.graphics.RectF r1 = r0.rect
                float r1 = r1.centerY()
                float r5 = (float) r8
                float r5 = r1 - r5
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.Paint r6 = r1.deletePaint
                r1 = r39
                r1.drawLine(r2, r3, r4, r5, r6)
                goto L_0x0808
            L_0x07fd:
                r25 = r3
                r11 = r6
                r26 = r9
                r27 = r10
                r9 = r31
                r10 = r35
            L_0x0808:
                r2 = r33
            L_0x080a:
                if (r29 == 0) goto L_0x081a
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                float r1 = r1.editingAnimationProgress
                r3 = 0
                int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
                if (r1 == 0) goto L_0x081a
                r39.restore()
            L_0x081a:
                r0.lastTextX = r9
                org.telegram.ui.Components.FilterTabsView$Tab r1 = r0.currentTab
                int r1 = r1.counter
                r0.lastTabCount = r1
                android.text.StaticLayout r1 = r0.textLayout
                r0.lastTitleLayout = r1
                java.lang.String r1 = r0.currentText
                r0.lastTitle = r1
                org.telegram.ui.Components.FilterTabsView$Tab r1 = r0.currentTab
                int r1 = r1.titleWidth
                r0.lastTitleWidth = r1
                r0.lastCountWidth = r10
                r0.lastCounterWidth = r2
                int r1 = r0.tabWidth
                float r1 = (float) r1
                r0.lastTabWidth = r1
                int r1 = r38.getMeasuredWidth()
                float r1 = (float) r1
                r0.lastWidth = r1
                org.telegram.ui.Components.FilterTabsView$Tab r1 = r0.currentTab
                boolean r1 = r1.isLocked
                if (r1 != 0) goto L_0x084d
                float r1 = r0.progressToLocked
                r3 = 0
                int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
                if (r1 == 0) goto L_0x0958
            L_0x084d:
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.drawable.Drawable r1 = r1.lockDrawable
                if (r1 != 0) goto L_0x0865
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                android.content.Context r3 = r38.getContext()
                r4 = 2131166024(0x7var_, float:1.7946282E38)
                android.graphics.drawable.Drawable r3 = androidx.core.content.ContextCompat.getDrawable(r3, r4)
                android.graphics.drawable.Drawable unused = r1.lockDrawable = r3
            L_0x0865:
                org.telegram.ui.Components.FilterTabsView$Tab r1 = r0.currentTab
                boolean r1 = r1.isLocked
                r3 = 1037726734(0x3dda740e, float:0.10666667)
                if (r1 == 0) goto L_0x087a
                float r1 = r0.progressToLocked
                r4 = 1065353216(0x3var_, float:1.0)
                int r5 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
                if (r5 == 0) goto L_0x087a
                float r1 = r1 + r3
                r0.progressToLocked = r1
                goto L_0x0885
            L_0x087a:
                org.telegram.ui.Components.FilterTabsView$Tab r1 = r0.currentTab
                boolean r1 = r1.isLocked
                if (r1 != 0) goto L_0x0885
                float r1 = r0.progressToLocked
                float r1 = r1 - r3
                r0.progressToLocked = r1
            L_0x0885:
                float r1 = r0.progressToLocked
                r3 = 0
                r4 = 1065353216(0x3var_, float:1.0)
                float r1 = org.telegram.messenger.Utilities.clamp(r1, r4, r3)
                r0.progressToLocked = r1
                org.telegram.ui.Components.FilterTabsView r1 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r1 = r1.unactiveTextColorKey
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                org.telegram.ui.Components.FilterTabsView r3 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r3 = r3.aUnactiveTextColorKey
                if (r3 == 0) goto L_0x08b6
                org.telegram.ui.Components.FilterTabsView r3 = org.telegram.ui.Components.FilterTabsView.this
                java.lang.String r3 = r3.aUnactiveTextColorKey
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                float r4 = r4.animationValue
                int r1 = androidx.core.graphics.ColorUtils.blendARGB(r1, r3, r4)
            L_0x08b6:
                org.telegram.ui.Components.FilterTabsView r3 = org.telegram.ui.Components.FilterTabsView.this
                int r3 = r3.lockDrawableColor
                if (r3 == r1) goto L_0x08d3
                org.telegram.ui.Components.FilterTabsView r3 = org.telegram.ui.Components.FilterTabsView.this
                int unused = r3.lockDrawableColor = r1
                org.telegram.ui.Components.FilterTabsView r3 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.drawable.Drawable r3 = r3.lockDrawable
                android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
                android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
                r4.<init>(r1, r5)
                r3.setColorFilter(r4)
            L_0x08d3:
                int r3 = r38.getMeasuredWidth()
                org.telegram.ui.Components.FilterTabsView r4 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.drawable.Drawable r4 = r4.lockDrawable
                int r4 = r4.getIntrinsicWidth()
                int r3 = r3 - r4
                float r3 = (float) r3
                r4 = 1073741824(0x40000000, float:2.0)
                float r3 = r3 / r4
                float r4 = r0.locIconXOffset
                float r3 = r3 + r4
                int r3 = (int) r3
                int r4 = r38.getMeasuredHeight()
                r5 = 1094713344(0x41400000, float:12.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r4 = r4 - r5
                org.telegram.ui.Components.FilterTabsView r5 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.drawable.Drawable r5 = r5.lockDrawable
                org.telegram.ui.Components.FilterTabsView r6 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.drawable.Drawable r6 = r6.lockDrawable
                int r6 = r6.getIntrinsicWidth()
                int r6 = r6 + r3
                org.telegram.ui.Components.FilterTabsView r8 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.drawable.Drawable r8 = r8.lockDrawable
                int r8 = r8.getIntrinsicHeight()
                int r8 = r8 + r4
                r5.setBounds(r3, r4, r6, r8)
                float r5 = r0.progressToLocked
                r6 = 1065353216(0x3var_, float:1.0)
                int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
                if (r5 == 0) goto L_0x094f
                r39.save()
                float r5 = r0.progressToLocked
                org.telegram.ui.Components.FilterTabsView r6 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.drawable.Drawable r6 = r6.lockDrawable
                android.graphics.Rect r6 = r6.getBounds()
                int r6 = r6.centerX()
                float r6 = (float) r6
                org.telegram.ui.Components.FilterTabsView r8 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.drawable.Drawable r8 = r8.lockDrawable
                android.graphics.Rect r8 = r8.getBounds()
                int r8 = r8.centerY()
                float r8 = (float) r8
                r7.scale(r5, r5, r6, r8)
                org.telegram.ui.Components.FilterTabsView r5 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.drawable.Drawable r5 = r5.lockDrawable
                r5.draw(r7)
                r39.restore()
                goto L_0x0958
            L_0x094f:
                org.telegram.ui.Components.FilterTabsView r5 = org.telegram.ui.Components.FilterTabsView.this
                android.graphics.drawable.Drawable r5 = r5.lockDrawable
                r5.draw(r7)
            L_0x0958:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FilterTabsView.TabView.onDraw(android.graphics.Canvas):void");
        }

        public boolean animateChange() {
            int countWidth;
            int i;
            boolean changed;
            String substring;
            String maxStr;
            boolean animateOut;
            boolean changed2;
            boolean changed3 = false;
            int i2 = this.currentTab.counter;
            int i3 = this.lastTabCount;
            if (i2 != i3) {
                this.animateTabCounter = true;
                this.animateFromTabCount = i3;
                this.animateFromCountWidth = (float) this.lastCountWidth;
                this.animateFromCounterWidth = this.lastCounterWidth;
                if (i3 > 0 && this.currentTab.counter > 0) {
                    String oldStr = String.valueOf(this.animateFromTabCount);
                    String newStr = String.valueOf(this.currentTab.counter);
                    if (oldStr.length() == newStr.length()) {
                        SpannableStringBuilder oldSpannableStr = new SpannableStringBuilder(oldStr);
                        SpannableStringBuilder newSpannableStr = new SpannableStringBuilder(newStr);
                        SpannableStringBuilder stableStr = new SpannableStringBuilder(newStr);
                        for (int i4 = 0; i4 < oldStr.length(); i4++) {
                            if (oldStr.charAt(i4) == newStr.charAt(i4)) {
                                oldSpannableStr.setSpan(new EmptyStubSpan(), i4, i4 + 1, 0);
                                newSpannableStr.setSpan(new EmptyStubSpan(), i4, i4 + 1, 0);
                            } else {
                                stableStr.setSpan(new EmptyStubSpan(), i4, i4 + 1, 0);
                            }
                        }
                        int countOldWidth = (int) Math.ceil((double) Theme.dialogs_countTextPaint.measureText(oldStr));
                        StaticLayout staticLayout = r7;
                        int countOldWidth2 = countOldWidth;
                        StaticLayout staticLayout2 = new StaticLayout(oldSpannableStr, FilterTabsView.this.textCounterPaint, countOldWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                        this.outCounter = staticLayout;
                        this.stableCounter = new StaticLayout(stableStr, FilterTabsView.this.textCounterPaint, countOldWidth2, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                        SpannableStringBuilder spannableStringBuilder = newSpannableStr;
                        this.inCounter = new StaticLayout(newSpannableStr, FilterTabsView.this.textCounterPaint, countOldWidth2, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                    } else {
                        this.outCounter = new StaticLayout(oldStr, FilterTabsView.this.textCounterPaint, (int) Math.ceil((double) Theme.dialogs_countTextPaint.measureText(oldStr)), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                        this.inCounter = new StaticLayout(newStr, FilterTabsView.this.textCounterPaint, (int) Math.ceil((double) Theme.dialogs_countTextPaint.measureText(newStr)), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                    }
                }
                changed3 = true;
            }
            String counterText = null;
            if (this.currentTab.counter > 0) {
                counterText = String.format("%d", new Object[]{Integer.valueOf(this.currentTab.counter)});
                countWidth = Math.max(AndroidUtilities.dp(10.0f), (int) Math.ceil((double) FilterTabsView.this.textCounterPaint.measureText(counterText))) + AndroidUtilities.dp(10.0f);
            } else {
                countWidth = 0;
            }
            int i5 = this.currentTab.titleWidth;
            if (countWidth != 0) {
                i = AndroidUtilities.dp((counterText != null ? 1.0f : FilterTabsView.this.editingStartAnimationProgress) * 6.0f) + countWidth;
            } else {
                i = 0;
            }
            int tabWidth2 = i5 + i;
            float f = this.lastTextX;
            if (((float) ((getMeasuredWidth() - tabWidth2) / 2)) != f) {
                this.animateTextX = true;
                this.animateFromTextX = f;
                changed3 = true;
            }
            if (this.lastTitle == null || this.currentTab.title.equals(this.lastTitle)) {
                changed = changed3;
            } else {
                if (this.lastTitle.length() > this.currentTab.title.length()) {
                    animateOut = true;
                    maxStr = this.lastTitle;
                    substring = this.currentTab.title;
                } else {
                    animateOut = false;
                    maxStr = this.currentTab.title;
                    substring = this.lastTitle;
                }
                int startFrom = maxStr.indexOf(substring);
                if (startFrom >= 0) {
                    CharSequence text = Emoji.replaceEmoji(maxStr, FilterTabsView.this.textPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false);
                    SpannableStringBuilder inStr = new SpannableStringBuilder(text);
                    SpannableStringBuilder stabeStr = new SpannableStringBuilder(text);
                    if (startFrom != 0) {
                        stabeStr.setSpan(new EmptyStubSpan(), 0, startFrom, 0);
                    }
                    if (substring.length() + startFrom != maxStr.length()) {
                        boolean z = changed3;
                        changed2 = false;
                        stabeStr.setSpan(new EmptyStubSpan(), substring.length() + startFrom, maxStr.length(), 0);
                    } else {
                        changed2 = false;
                    }
                    inStr.setSpan(new EmptyStubSpan(), startFrom, substring.length() + startFrom, changed2);
                    this.titleAnimateInLayout = new StaticLayout(inStr, FilterTabsView.this.textPaint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    StaticLayout staticLayout3 = new StaticLayout(stabeStr, FilterTabsView.this.textPaint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.titleAnimateStableLayout = staticLayout3;
                    this.animateTextChange = true;
                    this.animateTextChangeOut = animateOut;
                    this.titleXOffset = startFrom == 0 ? 0.0f : -staticLayout3.getPrimaryHorizontal(startFrom);
                    this.animateFromTitleWidth = this.lastTitleWidth;
                    this.titleAnimateOutLayout = null;
                    changed = true;
                } else {
                    boolean z2 = changed3;
                    this.titleAnimateInLayout = new StaticLayout(this.currentTab.title, FilterTabsView.this.textPaint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.titleAnimateOutLayout = new StaticLayout(this.lastTitle, FilterTabsView.this.textPaint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.titleAnimateStableLayout = null;
                    this.animateTextChange = true;
                    this.titleXOffset = 0.0f;
                    this.animateFromTitleWidth = this.lastTitleWidth;
                    changed = true;
                }
            }
            if (((float) tabWidth2) == this.lastTabWidth && ((float) getMeasuredWidth()) == this.lastWidth) {
                return changed;
            }
            this.animateTabWidth = true;
            this.animateFromTabWidth = this.lastTabWidth;
            this.animateFromWidth = this.lastWidth;
            return true;
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setSelected((this.currentTab == null || FilterTabsView.this.selectedTabId == -1 || this.currentTab.id != FilterTabsView.this.selectedTabId) ? false : true);
            info.addAction(16);
            if (Build.VERSION.SDK_INT >= 21) {
                info.addAction(new AccessibilityNodeInfo.AccessibilityAction(32, LocaleController.getString("AccDescrOpenMenu2", NUM)));
            } else {
                info.addAction(32);
            }
            if (this.currentTab != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(this.currentTab.title);
                Tab tab = this.currentTab;
                int unread = tab != null ? tab.counter : 0;
                if (unread > 0) {
                    sb.append("\n");
                    sb.append(LocaleController.formatPluralString("AccDescrUnreadCount", unread, new Object[0]));
                }
                info.setContentDescription(sb);
            }
        }

        public void clearTransitionParams() {
            this.animateChange = false;
            this.animateTabCounter = false;
            this.animateCounterChange = false;
            this.animateTextChange = false;
            this.animateTextX = false;
            this.animateTabWidth = false;
            this.changeAnimator = null;
            invalidate();
        }

        public void shakeLockIcon(final float x, final int num) {
            if (num == 6) {
                this.locIconXOffset = 0.0f;
                return;
            }
            AnimatorSet animatorSet = new AnimatorSet();
            ValueAnimator animator = ValueAnimator.ofFloat(new float[]{0.0f, (float) AndroidUtilities.dp(x)});
            animator.addUpdateListener(new FilterTabsView$TabView$$ExternalSyntheticLambda0(this));
            animatorSet.playTogether(new Animator[]{animator});
            animatorSet.setDuration(50);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    TabView tabView = TabView.this;
                    int i = num;
                    tabView.shakeLockIcon(i == 5 ? 0.0f : -x, i + 1);
                    float unused = TabView.this.locIconXOffset = 0.0f;
                    TabView.this.invalidate();
                }
            });
            animatorSet.start();
        }

        /* renamed from: lambda$shakeLockIcon$0$org-telegram-ui-Components-FilterTabsView$TabView  reason: not valid java name */
        public /* synthetic */ void m970xe6a17167(ValueAnimator animation) {
            this.locIconXOffset = ((Float) animation.getAnimatedValue()).floatValue();
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
        float rad = AndroidUtilities.dpf2(3.0f);
        this.selectorDrawable.setCornerRadii(new float[]{rad, rad, rad, rad, 0.0f, 0.0f, 0.0f, 0.0f});
        this.selectorDrawable.setColor(Theme.getColor(this.tabLineColorKey));
        setHorizontalScrollBarEnabled(false);
        AnonymousClass3 r4 = new RecyclerListView(context) {
            public void setAlpha(float alpha) {
                super.setAlpha(alpha);
                FilterTabsView.this.invalidate();
            }

            /* access modifiers changed from: protected */
            public boolean allowSelectChildAtPosition(View child) {
                return FilterTabsView.this.isEnabled() && FilterTabsView.this.delegate.canPerformActions();
            }

            /* access modifiers changed from: protected */
            public boolean canHighlightChildAt(View child, float x, float y) {
                if (FilterTabsView.this.isEditing) {
                    TabView tabView = (TabView) child;
                    int side = AndroidUtilities.dp(6.0f);
                    if (tabView.rect.left - ((float) side) < x && tabView.rect.right + ((float) side) > x) {
                        return false;
                    }
                }
                return super.canHighlightChildAt(child, x, y);
            }
        };
        this.listView = r4;
        r4.setClipChildren(false);
        AnonymousClass4 r42 = new DefaultItemAnimator() {
            public void runPendingAnimations() {
                boolean removalsPending = !this.mPendingRemovals.isEmpty();
                boolean movesPending = !this.mPendingMoves.isEmpty();
                boolean changesPending = !this.mPendingChanges.isEmpty();
                boolean additionsPending = !this.mPendingAdditions.isEmpty();
                if (removalsPending || movesPending || additionsPending || changesPending) {
                    ValueAnimator valueAnimator = ValueAnimator.ofFloat(new float[]{0.1f});
                    valueAnimator.addUpdateListener(new FilterTabsView$4$$ExternalSyntheticLambda0(this));
                    valueAnimator.setDuration(getMoveDuration());
                    valueAnimator.start();
                }
                super.runPendingAnimations();
            }

            /* renamed from: lambda$runPendingAnimations$0$org-telegram-ui-Components-FilterTabsView$4  reason: not valid java name */
            public /* synthetic */ void m969xd36768ec(ValueAnimator valueAnimator12) {
                FilterTabsView.this.listView.invalidate();
                FilterTabsView.this.invalidate();
            }

            public boolean animateMove(RecyclerView.ViewHolder holder, RecyclerView.ItemAnimator.ItemHolderInfo info, int fromX, int fromY, int toX, int toY) {
                RecyclerView.ViewHolder viewHolder = holder;
                if (!(viewHolder.itemView instanceof TabView)) {
                    return super.animateMove(holder, info, fromX, fromY, toX, toY);
                }
                View view = viewHolder.itemView;
                int fromX2 = fromX + ((int) viewHolder.itemView.getTranslationX());
                int fromY2 = fromY + ((int) viewHolder.itemView.getTranslationY());
                resetAnimation(holder);
                int deltaX = toX - fromX2;
                int deltaY = toY - fromY2;
                if (deltaX != 0) {
                    view.setTranslationX((float) (-deltaX));
                }
                if (deltaY != 0) {
                    view.setTranslationY((float) (-deltaY));
                }
                TabView tabView = (TabView) viewHolder.itemView;
                boolean animateChange = tabView.animateChange();
                if (animateChange) {
                    tabView.changeProgress = 0.0f;
                    tabView.animateChange = true;
                    FilterTabsView.this.invalidate();
                }
                if (deltaX == 0 && deltaY == 0 && !animateChange) {
                    dispatchMoveFinished(holder);
                    return false;
                }
                DefaultItemAnimator.MoveInfo moveInfo = r1;
                ArrayList arrayList = this.mPendingMoves;
                DefaultItemAnimator.MoveInfo moveInfo2 = new DefaultItemAnimator.MoveInfo(holder, fromX2, fromY2, toX, toY);
                arrayList.add(moveInfo);
                return true;
            }

            /* access modifiers changed from: protected */
            public void animateMoveImpl(RecyclerView.ViewHolder holder, DefaultItemAnimator.MoveInfo moveInfo) {
                super.animateMoveImpl(holder, moveInfo);
                if (holder.itemView instanceof TabView) {
                    final TabView tabView = (TabView) holder.itemView;
                    if (tabView.animateChange) {
                        if (tabView.changeAnimator != null) {
                            tabView.changeAnimator.removeAllListeners();
                            tabView.changeAnimator.removeAllUpdateListeners();
                            tabView.changeAnimator.cancel();
                        }
                        ValueAnimator valueAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                        valueAnimator.addUpdateListener(new FilterTabsView$4$$ExternalSyntheticLambda1(tabView));
                        valueAnimator.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                tabView.clearTransitionParams();
                            }
                        });
                        tabView.changeAnimator = valueAnimator;
                        valueAnimator.setDuration(getMoveDuration());
                        valueAnimator.start();
                    }
                }
            }

            static /* synthetic */ void lambda$animateMoveImpl$1(TabView tabView, ValueAnimator valueAnimator1) {
                tabView.changeProgress = ((Float) valueAnimator1.getAnimatedValue()).floatValue();
                tabView.invalidate();
            }

            public void onMoveFinished(RecyclerView.ViewHolder item) {
                super.onMoveFinished(item);
                item.itemView.setTranslationX(0.0f);
                if (item.itemView instanceof TabView) {
                    ((TabView) item.itemView).clearTransitionParams();
                }
            }

            public void endAnimation(RecyclerView.ViewHolder item) {
                super.endAnimation(item);
                item.itemView.setTranslationX(0.0f);
                if (item.itemView instanceof TabView) {
                    ((TabView) item.itemView).clearTransitionParams();
                }
            }
        };
        this.itemAnimator = r42;
        r42.setDelayAnimations(false);
        this.listView.setItemAnimator(this.itemAnimator);
        this.listView.setSelectorType(8);
        this.listView.setSelectorRadius(6);
        this.listView.setSelectorDrawableColor(Theme.getColor(this.selectorColorKey));
        RecyclerListView recyclerListView = this.listView;
        AnonymousClass5 r43 = new LinearLayoutManager(context, 0, false) {
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }

            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
                    /* access modifiers changed from: protected */
                    public void onTargetFound(View targetView, RecyclerView.State state, RecyclerView.SmoothScroller.Action action) {
                        int dx = calculateDxToMakeVisible(targetView, getHorizontalSnapPreference());
                        if (dx > 0 || (dx == 0 && targetView.getLeft() - AndroidUtilities.dp(21.0f) < 0)) {
                            dx += AndroidUtilities.dp(60.0f);
                        } else if (dx < 0 || (dx == 0 && targetView.getRight() + AndroidUtilities.dp(21.0f) > FilterTabsView.this.getMeasuredWidth())) {
                            dx -= AndroidUtilities.dp(60.0f);
                        }
                        int dy = calculateDyToMakeVisible(targetView, getVerticalSnapPreference());
                        int time = Math.max(180, calculateTimeForDeceleration((int) Math.sqrt((double) ((dx * dx) + (dy * dy)))));
                        if (time > 0) {
                            action.update(-dx, -dy, time, this.mDecelerateInterpolator);
                        }
                    }
                };
                linearSmoothScroller.setTargetPosition(position);
                startSmoothScroll(linearSmoothScroller);
            }

            public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
                if (FilterTabsView.this.delegate.isTabMenuVisible()) {
                    dx = 0;
                }
                return super.scrollHorizontallyBy(dx, recycler, state);
            }
        };
        this.layoutManager = r43;
        recyclerListView.setLayoutManager(r43);
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
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                FilterTabsView.this.invalidate();
            }
        });
        addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-FilterTabsView  reason: not valid java name */
    public /* synthetic */ void m967lambda$new$0$orgtelegramuiComponentsFilterTabsView(View view, int position, float x, float y) {
        FilterTabsViewDelegate filterTabsViewDelegate;
        if (this.delegate.canPerformActions()) {
            TabView tabView = (TabView) view;
            if (this.isEditing) {
                if (position != 0) {
                    int side = AndroidUtilities.dp(6.0f);
                    if (tabView.rect.left - ((float) side) < x && tabView.rect.right + ((float) side) > x) {
                        this.delegate.onDeletePressed(tabView.currentTab.id);
                    }
                }
            } else if (position != this.currentPosition || (filterTabsViewDelegate = this.delegate) == null) {
                scrollToTab(tabView.currentTab, position);
            } else {
                filterTabsViewDelegate.onSamePageSelected();
            }
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-FilterTabsView  reason: not valid java name */
    public /* synthetic */ boolean m968lambda$new$1$orgtelegramuiComponentsFilterTabsView(View view, int position) {
        if (this.delegate.canPerformActions() && !this.isEditing) {
            if (this.delegate.didSelectTab((TabView) view, position == this.currentPosition)) {
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

    private void scrollToTab(Tab tab, int position) {
        if (tab.isLocked) {
            FilterTabsViewDelegate filterTabsViewDelegate = this.delegate;
            if (filterTabsViewDelegate != null) {
                filterTabsViewDelegate.onPageSelected(tab, false);
                return;
            }
            return;
        }
        int i = this.currentPosition;
        boolean scrollingForward = i < position;
        this.scrollingToChild = -1;
        this.previousPosition = i;
        this.previousId = this.selectedTabId;
        this.currentPosition = position;
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
            filterTabsViewDelegate2.onPageSelected(tab, scrollingForward);
        }
        scrollToChild(position);
    }

    public void selectFirstTab() {
        if (!this.tabs.isEmpty()) {
            scrollToTab(this.tabs.get(0), 0);
        }
    }

    public void setAnimationIdicatorProgress(float value) {
        this.animatingIndicatorProgress = value;
        this.listView.invalidateViews();
        invalidate();
        FilterTabsViewDelegate filterTabsViewDelegate = this.delegate;
        if (filterTabsViewDelegate != null) {
            filterTabsViewDelegate.onPageScrolled(value);
        }
    }

    public Drawable getSelectorDrawable() {
        return this.selectorDrawable;
    }

    public RecyclerListView getTabsContainer() {
        return this.listView;
    }

    public int getNextPageId(boolean forward) {
        return this.positionToId.get(this.currentPosition + (forward ? 1 : -1), -1);
    }

    public void removeTabs() {
        this.tabs.clear();
        this.positionToId.clear();
        this.idToPosition.clear();
        this.positionToWidth.clear();
        this.positionToX.clear();
        this.allTabsWidth = 0;
    }

    public boolean hasTab(int id) {
        return this.idToPosition.get(id, -1) != -1;
    }

    public void resetTabId() {
        this.selectedTabId = -1;
    }

    public void addTab(int id, int stableId, String text, boolean isDefault, boolean isLocked) {
        int position = this.tabs.size();
        if (position == 0 && this.selectedTabId == -1) {
            this.selectedTabId = id;
        }
        this.positionToId.put(position, id);
        this.positionToStableId.put(position, stableId);
        this.idToPosition.put(id, position);
        int i = this.selectedTabId;
        if (i != -1 && i == id) {
            this.currentPosition = position;
        }
        Tab tab = new Tab(id, text);
        tab.isDefault = isDefault;
        tab.isLocked = isLocked;
        this.allTabsWidth += tab.getWidth(true) + AndroidUtilities.dp(32.0f);
        this.tabs.add(tab);
    }

    public void finishAddingTabs(boolean animated) {
        this.listView.setItemAnimator(animated ? this.itemAnimator : null);
        this.adapter.notifyDataSetChanged();
    }

    public void animateColorsTo(String line, String active, String unactive, String selector, String background) {
        AnimatorSet animatorSet = this.colorChangeAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.aTabLineColorKey = line;
        this.aActiveTextColorKey = active;
        this.aUnactiveTextColorKey = unactive;
        this.aBackgroundColorKey = background;
        this.selectorColorKey = selector;
        this.listView.setSelectorDrawableColor(Theme.getColor(selector));
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.colorChangeAnimator = animatorSet2;
        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, this.COLORS, new float[]{0.0f, 1.0f})});
        this.colorChangeAnimator.setDuration(200);
        this.colorChangeAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
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
        int xOffset = AndroidUtilities.dp(7.0f);
        int N = this.tabs.size();
        for (int a = 0; a < N; a++) {
            int tabWidth = this.tabs.get(a).getWidth(false);
            this.positionToWidth.put(a, tabWidth);
            this.positionToX.put(a, (this.additionalTabWidth / 2) + xOffset);
            xOffset += AndroidUtilities.dp(32.0f) + tabWidth + this.additionalTabWidth;
        }
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        float indicatorWidth;
        float indicatorX;
        int idx2;
        int idx1;
        Canvas canvas2 = canvas;
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (child == this.listView) {
            int height = getMeasuredHeight();
            this.selectorDrawable.setAlpha((int) (this.listView.getAlpha() * 255.0f));
            float indicatorX2 = 0.0f;
            float indicatorWidth2 = 0.0f;
            if (this.animatingIndicator || this.manualScrollingToPosition != -1) {
                int position = this.layoutManager.findFirstVisibleItemPosition();
                if (position != -1) {
                    RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(position);
                    if (holder != null) {
                        if (this.animatingIndicator) {
                            idx1 = this.previousPosition;
                            idx2 = this.currentPosition;
                        } else {
                            idx1 = this.currentPosition;
                            idx2 = this.manualScrollingToPosition;
                        }
                        int prevX = this.positionToX.get(idx1);
                        int newX = this.positionToX.get(idx2);
                        int prevW = this.positionToWidth.get(idx1);
                        int newW = this.positionToWidth.get(idx2);
                        if (this.additionalTabWidth != 0) {
                            indicatorX2 = (float) (((int) (((float) prevX) + (((float) (newX - prevX)) * this.animatingIndicatorProgress))) + AndroidUtilities.dp(16.0f));
                        } else {
                            indicatorX2 = (float) ((((int) (((float) prevX) + (((float) (newX - prevX)) * this.animatingIndicatorProgress))) - (this.positionToX.get(position) - holder.itemView.getLeft())) + AndroidUtilities.dp(16.0f));
                        }
                        indicatorWidth2 = (float) ((int) (((float) prevW) + (((float) (newW - prevW)) * this.animatingIndicatorProgress)));
                    } else {
                        indicatorX = 0.0f;
                        indicatorWidth = 0.0f;
                    }
                } else {
                    indicatorX = 0.0f;
                    indicatorWidth = 0.0f;
                }
                indicatorX2 = indicatorX;
                indicatorWidth2 = indicatorWidth;
            } else {
                RecyclerView.ViewHolder holder2 = this.listView.findViewHolderForAdapterPosition(this.currentPosition);
                if (holder2 != null) {
                    TabView tabView = (TabView) holder2.itemView;
                    indicatorWidth2 = Math.max((float) AndroidUtilities.dp(40.0f), tabView.animateTabWidth ? (tabView.animateFromTabWidth * (1.0f - tabView.changeProgress)) + (((float) tabView.tabWidth) * tabView.changeProgress) : (float) tabView.tabWidth);
                    indicatorX2 = (float) ((int) (tabView.getX() + (((tabView.animateTabWidth ? (tabView.animateFromWidth * (1.0f - tabView.changeProgress)) + (((float) tabView.getMeasuredWidth()) * tabView.changeProgress) : (float) tabView.getMeasuredWidth()) - indicatorWidth2) / 2.0f)));
                }
            }
            if (indicatorWidth2 != 0.0f) {
                canvas.save();
                canvas2.translate(this.listView.getTranslationX(), 0.0f);
                canvas2.scale(this.listView.getScaleX(), 1.0f, this.listView.getPivotX() + this.listView.getX(), this.listView.getPivotY());
                this.selectorDrawable.setBounds((int) indicatorX2, height - AndroidUtilities.dpr(4.0f), (int) (indicatorX2 + indicatorWidth2), height);
                this.selectorDrawable.draw(canvas2);
                canvas.restore();
            }
        }
        long newTime = SystemClock.elapsedRealtime();
        long dt = Math.min(17, newTime - this.lastEditingAnimationTime);
        this.lastEditingAnimationTime = newTime;
        boolean invalidate = false;
        boolean z = this.isEditing;
        if (z || this.editingAnimationProgress != 0.0f) {
            boolean lessZero = true;
            boolean greaterZero = false;
            if (this.editingForwardAnimation) {
                float f = this.editingAnimationProgress;
                if (f > 0.0f) {
                    lessZero = false;
                }
                float f2 = f + (((float) dt) / 120.0f);
                this.editingAnimationProgress = f2;
                if (!z && lessZero && f2 >= 0.0f) {
                    this.editingAnimationProgress = 0.0f;
                }
                if (this.editingAnimationProgress >= 1.0f) {
                    this.editingAnimationProgress = 1.0f;
                    this.editingForwardAnimation = false;
                }
            } else {
                float f3 = this.editingAnimationProgress;
                if (f3 >= 0.0f) {
                    greaterZero = true;
                }
                float f4 = f3 - (((float) dt) / 120.0f);
                this.editingAnimationProgress = f4;
                if (!z && greaterZero && f4 <= 0.0f) {
                    this.editingAnimationProgress = 0.0f;
                }
                if (this.editingAnimationProgress <= -1.0f) {
                    this.editingAnimationProgress = -1.0f;
                    this.editingForwardAnimation = true;
                }
            }
            invalidate = true;
        }
        if (z) {
            float f5 = this.editingStartAnimationProgress;
            if (f5 < 1.0f) {
                float f6 = f5 + (((float) dt) / 180.0f);
                this.editingStartAnimationProgress = f6;
                if (f6 > 1.0f) {
                    this.editingStartAnimationProgress = 1.0f;
                }
                invalidate = true;
            }
        } else if (!z) {
            float f7 = this.editingStartAnimationProgress;
            if (f7 > 0.0f) {
                float f8 = f7 - (((float) dt) / 180.0f);
                this.editingStartAnimationProgress = f8;
                if (f8 < 0.0f) {
                    this.editingStartAnimationProgress = 0.0f;
                }
                invalidate = true;
            }
        }
        if (invalidate) {
            this.listView.invalidateViews();
            invalidate();
        }
        return result;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!this.tabs.isEmpty()) {
            int width = (View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(7.0f)) - AndroidUtilities.dp(7.0f);
            Tab firstTab = findDefaultTab();
            firstTab.setTitle(LocaleController.getString("FilterAllChats", NUM));
            int tabWith = firstTab.getWidth(false);
            firstTab.setTitle(this.allTabsWidth > width ? LocaleController.getString("FilterAllChatsShort", NUM) : LocaleController.getString("FilterAllChats", NUM));
            int trueTabsWidth = (this.allTabsWidth - tabWith) + firstTab.getWidth(false);
            int prevWidth = this.additionalTabWidth;
            int size = trueTabsWidth < width ? (width - trueTabsWidth) / this.tabs.size() : 0;
            this.additionalTabWidth = size;
            if (prevWidth != size) {
                this.ignoreLayout = true;
                RecyclerView.ItemAnimator animator = this.listView.getItemAnimator();
                this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
                this.adapter.notifyDataSetChanged();
                this.listView.setItemAnimator(animator);
                this.ignoreLayout = false;
            }
            updateTabsWidths();
            this.invalidated = false;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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

    private void scrollToChild(int position) {
        if (!this.tabs.isEmpty() && this.scrollingToChild != position && position >= 0 && position < this.tabs.size()) {
            this.scrollingToChild = position;
            this.listView.smoothScrollToPosition(position);
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (this.prevLayoutWidth != r - l) {
            this.prevLayoutWidth = r - l;
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

    public void selectTabWithId(int id, float progress) {
        int position = this.idToPosition.get(id, -1);
        if (position >= 0) {
            if (progress < 0.0f) {
                progress = 0.0f;
            } else if (progress > 1.0f) {
                progress = 1.0f;
            }
            if (progress > 0.0f) {
                this.manualScrollingToPosition = position;
                this.manualScrollingToId = id;
            } else {
                this.manualScrollingToPosition = -1;
                this.manualScrollingToId = -1;
            }
            this.animatingIndicatorProgress = progress;
            this.listView.invalidateViews();
            invalidate();
            scrollToChild(position);
            if (progress >= 1.0f) {
                this.manualScrollingToPosition = -1;
                this.manualScrollingToId = -1;
                this.currentPosition = position;
                this.selectedTabId = id;
            }
        }
    }

    private int getChildWidth(TextView child) {
        Layout layout = child.getLayout();
        if (layout == null) {
            return child.getMeasuredWidth();
        }
        int w = ((int) Math.ceil((double) layout.getLineWidth(0))) + AndroidUtilities.dp(2.0f);
        if (child.getCompoundDrawables()[2] != null) {
            return w + child.getCompoundDrawables()[2].getIntrinsicWidth() + AndroidUtilities.dp(6.0f);
        }
        return w;
    }

    public void onPageScrolled(int position, int first) {
        if (this.currentPosition != position) {
            this.currentPosition = position;
            if (position < this.tabs.size()) {
                if (first != position || position <= 1) {
                    scrollToChild(position);
                } else {
                    scrollToChild(position - 1);
                }
                invalidate();
            }
        }
    }

    public boolean isEditing() {
        return this.isEditing;
    }

    public void setIsEditing(boolean value) {
        this.isEditing = value;
        this.editingForwardAnimation = true;
        this.listView.invalidateViews();
        invalidate();
        if (!this.isEditing && this.orderChanged) {
            MessagesStorage.getInstance(UserConfig.selectedAccount).saveDialogFiltersOrder();
            TLRPC.TL_messages_updateDialogFiltersOrder req = new TLRPC.TL_messages_updateDialogFiltersOrder();
            ArrayList<MessagesController.DialogFilter> filters = MessagesController.getInstance(UserConfig.selectedAccount).dialogFilters;
            int N = filters.size();
            for (int a = 0; a < N; a++) {
                MessagesController.DialogFilter filter = filters.get(a);
                if (filter.isDefault()) {
                    req.order.add(0);
                } else {
                    req.order.add(Integer.valueOf(filter.id));
                }
            }
            MessagesController.getInstance(UserConfig.selectedAccount).lockFiltersInternal();
            ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(req, FilterTabsView$$ExternalSyntheticLambda0.INSTANCE);
            this.orderChanged = false;
        }
    }

    static /* synthetic */ void lambda$setIsEditing$2(TLObject response, TLRPC.TL_error error) {
    }

    public void checkTabsCounter() {
        boolean changed = false;
        int a = 0;
        int N = this.tabs.size();
        while (true) {
            if (a >= N) {
                break;
            }
            Tab tab = this.tabs.get(a);
            if (tab.counter != this.delegate.getTabCounter(tab.id) && this.delegate.getTabCounter(tab.id) >= 0) {
                changed = true;
                if (this.positionToWidth.get(a) != tab.getWidth(true) || this.invalidated) {
                    this.invalidated = true;
                    requestLayout();
                    this.allTabsWidth = 0;
                    findDefaultTab().setTitle(LocaleController.getString("FilterAllChats", NUM));
                }
            }
            a++;
        }
        this.invalidated = true;
        requestLayout();
        this.allTabsWidth = 0;
        findDefaultTab().setTitle(LocaleController.getString("FilterAllChats", NUM));
        for (int b = 0; b < N; b++) {
            this.allTabsWidth += this.tabs.get(b).getWidth(true) + AndroidUtilities.dp(32.0f);
        }
        if (changed) {
            this.listView.setItemAnimator(this.itemAnimator);
            this.adapter.notifyDataSetChanged();
        }
    }

    public void notifyTabCounterChanged(int id) {
        int position = this.idToPosition.get(id, -1);
        if (position >= 0 && position < this.tabs.size()) {
            Tab tab = this.tabs.get(position);
            if (tab.counter != this.delegate.getTabCounter(tab.id) && this.delegate.getTabCounter(tab.id) >= 0) {
                this.listView.invalidateViews();
                if (this.positionToWidth.get(position) != tab.getWidth(true) || this.invalidated) {
                    this.invalidated = true;
                    requestLayout();
                    this.listView.setItemAnimator(this.itemAnimator);
                    this.adapter.notifyDataSetChanged();
                    this.allTabsWidth = 0;
                    findDefaultTab().setTitle(LocaleController.getString("FilterAllChats", NUM));
                    int N = this.tabs.size();
                    for (int b = 0; b < N; b++) {
                        this.allTabsWidth += this.tabs.get(b).getWidth(true) + AndroidUtilities.dp(32.0f);
                    }
                }
            }
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return FilterTabsView.this.tabs.size();
        }

        public long getItemId(int position) {
            return (long) FilterTabsView.this.positionToStableId.get(position);
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerListView.Holder(new TabView(this.mContext));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TabView tabView = (TabView) holder.itemView;
            int oldId = tabView.currentTab != null ? tabView.getId() : -1;
            tabView.setTab((Tab) FilterTabsView.this.tabs.get(position), position);
            if (oldId != tabView.getId()) {
                float unused = tabView.progressToLocked = tabView.currentTab.isLocked ? 1.0f : 0.0f;
            }
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public void swapElements(int fromIndex, int toIndex) {
            int i = fromIndex;
            int i2 = toIndex;
            int idx1 = fromIndex;
            int idx2 = toIndex;
            int count = FilterTabsView.this.tabs.size();
            if (idx1 < 0 || idx2 < 0 || idx1 >= count) {
            } else if (idx2 >= count) {
                int i3 = idx1;
            } else {
                ArrayList<MessagesController.DialogFilter> filters = MessagesController.getInstance(UserConfig.selectedAccount).dialogFilters;
                MessagesController.DialogFilter filter1 = filters.get(idx1);
                MessagesController.DialogFilter filter2 = filters.get(idx2);
                int temp = filter1.order;
                filter1.order = filter2.order;
                filter2.order = temp;
                filters.set(idx1, filter2);
                filters.set(idx2, filter1);
                Tab tab1 = (Tab) FilterTabsView.this.tabs.get(i);
                Tab tab2 = (Tab) FilterTabsView.this.tabs.get(i2);
                int temp2 = tab1.id;
                tab1.id = tab2.id;
                tab2.id = temp2;
                int fromStableId = FilterTabsView.this.positionToStableId.get(i);
                FilterTabsView.this.positionToStableId.put(i, FilterTabsView.this.positionToStableId.get(i2));
                FilterTabsView.this.positionToStableId.put(i2, fromStableId);
                int i4 = idx1;
                FilterTabsView.this.delegate.onPageReorder(tab2.id, tab1.id);
                if (FilterTabsView.this.currentPosition == i) {
                    int unused = FilterTabsView.this.currentPosition = i2;
                    int unused2 = FilterTabsView.this.selectedTabId = tab1.id;
                } else if (FilterTabsView.this.currentPosition == i2) {
                    int unused3 = FilterTabsView.this.currentPosition = i;
                    int unused4 = FilterTabsView.this.selectedTabId = tab2.id;
                }
                if (FilterTabsView.this.previousPosition == i) {
                    int unused5 = FilterTabsView.this.previousPosition = i2;
                    int unused6 = FilterTabsView.this.previousId = tab1.id;
                } else if (FilterTabsView.this.previousPosition == i2) {
                    int unused7 = FilterTabsView.this.previousPosition = i;
                    int unused8 = FilterTabsView.this.previousId = tab2.id;
                }
                FilterTabsView.this.tabs.set(i, tab2);
                FilterTabsView.this.tabs.set(i2, tab1);
                FilterTabsView.this.updateTabsWidths();
                boolean unused9 = FilterTabsView.this.orderChanged = true;
                FilterTabsView.this.listView.setItemAnimator(FilterTabsView.this.itemAnimator);
                notifyItemMoved(fromIndex, toIndex);
            }
        }
    }

    public class TouchHelperCallback extends ItemTouchHelper.Callback {
        public TouchHelperCallback() {
        }

        public boolean isLongPressDragEnabled() {
            return FilterTabsView.this.isEditing;
        }

        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (!FilterTabsView.this.isEditing || (viewHolder.getAdapterPosition() == 0 && ((Tab) FilterTabsView.this.tabs.get(0)).isDefault && !UserConfig.getInstance(UserConfig.selectedAccount).isPremium())) {
                return makeMovementFlags(0, 0);
            }
            return makeMovementFlags(12, 0);
        }

        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
            if ((source.getAdapterPosition() == 0 || target.getAdapterPosition() == 0) && !UserConfig.getInstance(UserConfig.selectedAccount).isPremium()) {
                return false;
            }
            FilterTabsView.this.adapter.swapElements(source.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != 0) {
                FilterTabsView.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
                viewHolder.itemView.setBackgroundColor(Theme.getColor(FilterTabsView.this.backgroundColorKey));
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
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
        Tab defaultTab = findDefaultTab();
        if (defaultTab != null && defaultTab.id == this.selectedTabId) {
            return true;
        }
        return false;
    }

    public int getDefaultTabId() {
        Tab defaultTab = findDefaultTab();
        if (defaultTab == null) {
            return -1;
        }
        return defaultTab.id;
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

    public boolean isLocked(int id) {
        for (int i = 0; i < this.tabs.size(); i++) {
            if (this.tabs.get(i).id == id) {
                return this.tabs.get(i).isLocked;
            }
        }
        return false;
    }

    public void shakeLock(int id) {
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            if (this.listView.getChildAt(i) instanceof TabView) {
                TabView tabView = (TabView) this.listView.getChildAt(i);
                if (tabView.currentTab.id == id) {
                    tabView.shakeLockIcon(1.0f, 0);
                    tabView.performHapticFeedback(3);
                    return;
                }
            }
        }
    }
}
