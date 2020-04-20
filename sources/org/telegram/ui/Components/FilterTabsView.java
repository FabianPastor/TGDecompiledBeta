package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.SystemClock;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Property;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
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
import org.telegram.messenger.FileLog;
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
            if (FilterTabsView.this.aBackgroundColorKey != null) {
                filterTabsView.setBackgroundColor(ColorUtils.blendARGB(Theme.getColor(FilterTabsView.this.backgroundColorKey), Theme.getColor(FilterTabsView.this.aBackgroundColorKey), f));
            }
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
    private boolean commitCrossfade;
    /* access modifiers changed from: private */
    public Paint counterPaint = new Paint(1);
    private float crossfadeAlpha;
    private Bitmap crossfadeBitmap;
    private Paint crossfadePaint = new Paint();
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
        private int currentPosition;
        /* access modifiers changed from: private */
        public Tab currentTab;
        private String currentText;
        /* access modifiers changed from: private */
        public RectF rect = new RectF();
        /* access modifiers changed from: private */
        public int tabWidth;
        private int textHeight;
        private StaticLayout textLayout;
        private int textOffsetX;

        public TabView(Context context) {
            super(context);
        }

        public void setTab(Tab tab, int i) {
            this.currentTab = tab;
            this.currentPosition = i;
            requestLayout();
        }

        public int getId() {
            return this.currentTab.id;
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(this.currentTab.getWidth(false) + AndroidUtilities.dp(32.0f) + FilterTabsView.this.additionalTabWidth, View.MeasureSpec.getSize(i2));
        }

        /* access modifiers changed from: protected */
        @SuppressLint({"DrawAllocation"})
        public void onDraw(Canvas canvas) {
            int i;
            int i2;
            String str;
            String str2;
            String str3;
            String str4;
            int i3;
            int i4;
            String str5;
            int i5;
            int i6;
            int i7;
            int i8;
            Canvas canvas2 = canvas;
            if (!(this.currentTab.id == Integer.MAX_VALUE || FilterTabsView.this.editingAnimationProgress == 0.0f)) {
                canvas.save();
                float access$400 = FilterTabsView.this.editingAnimationProgress * (this.currentPosition % 2 == 0 ? 1.0f : -1.0f);
                canvas2.translate(((float) AndroidUtilities.dp(0.66f)) * access$400, 0.0f);
                canvas2.rotate(access$400, (float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2));
            }
            if (FilterTabsView.this.manualScrollingToId != -1) {
                i2 = FilterTabsView.this.manualScrollingToId;
                i = FilterTabsView.this.selectedTabId;
            } else {
                i2 = FilterTabsView.this.selectedTabId;
                i = FilterTabsView.this.previousId;
            }
            String str6 = "chats_tabUnreadActiveBackground";
            String str7 = "chats_tabUnreadUnactiveBackground";
            if (this.currentTab.id == i2) {
                str4 = FilterTabsView.this.activeTextColorKey;
                str3 = FilterTabsView.this.aActiveTextColorKey;
                str2 = FilterTabsView.this.unactiveTextColorKey;
                str = FilterTabsView.this.aUnactiveTextColorKey;
            } else {
                str4 = FilterTabsView.this.unactiveTextColorKey;
                str3 = FilterTabsView.this.aUnactiveTextColorKey;
                str2 = FilterTabsView.this.activeTextColorKey;
                str = FilterTabsView.this.aUnactiveTextColorKey;
                String str8 = str7;
                str7 = str6;
                str6 = str8;
            }
            if (str3 != null) {
                int color = Theme.getColor(str4);
                int color2 = Theme.getColor(str3);
                if ((FilterTabsView.this.animatingIndicator || FilterTabsView.this.manualScrollingToPosition != -1) && ((i7 = this.currentTab.id) == i2 || i7 == i)) {
                    FilterTabsView.this.textPaint.setColor(ColorUtils.blendARGB(ColorUtils.blendARGB(Theme.getColor(str2), Theme.getColor(str), FilterTabsView.this.animationValue), ColorUtils.blendARGB(color, color2, FilterTabsView.this.animationValue), FilterTabsView.this.animatingIndicatorProgress));
                } else {
                    FilterTabsView.this.textPaint.setColor(ColorUtils.blendARGB(color, color2, FilterTabsView.this.animationValue));
                }
            } else if ((FilterTabsView.this.animatingIndicator || FilterTabsView.this.manualScrollingToId != -1) && ((i8 = this.currentTab.id) == i2 || i8 == i)) {
                FilterTabsView.this.textPaint.setColor(ColorUtils.blendARGB(Theme.getColor(str2), Theme.getColor(str4), FilterTabsView.this.animatingIndicatorProgress));
            } else {
                FilterTabsView.this.textPaint.setColor(Theme.getColor(str4));
            }
            int i9 = this.currentTab.counter;
            if (i9 > 0) {
                str5 = String.format("%d", new Object[]{Integer.valueOf(i9)});
                i4 = (int) Math.ceil((double) FilterTabsView.this.textCounterPaint.measureText(str5));
                i3 = Math.max(AndroidUtilities.dp(10.0f), i4) + AndroidUtilities.dp(10.0f);
            } else {
                str5 = null;
                i4 = 0;
                i3 = 0;
            }
            if (this.currentTab.id != Integer.MAX_VALUE && (FilterTabsView.this.isEditing || FilterTabsView.this.editingStartAnimationProgress != 0.0f)) {
                i3 = (int) (((float) i3) + (((float) (AndroidUtilities.dp(20.0f) - i3)) * FilterTabsView.this.editingStartAnimationProgress));
            }
            int i10 = this.currentTab.titleWidth;
            if (i3 != 0) {
                i5 = AndroidUtilities.dp((str5 != null ? 1.0f : FilterTabsView.this.editingStartAnimationProgress) * 6.0f) + i3;
            } else {
                i5 = 0;
            }
            this.tabWidth = i10 + i5;
            int measuredWidth = (getMeasuredWidth() - this.tabWidth) / 2;
            if (!TextUtils.equals(this.currentTab.title, this.currentText)) {
                String str9 = this.currentTab.title;
                this.currentText = str9;
                StaticLayout staticLayout = new StaticLayout(Emoji.replaceEmoji(str9, FilterTabsView.this.textPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false), FilterTabsView.this.textPaint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.textLayout = staticLayout;
                this.textHeight = staticLayout.getHeight();
                this.textOffsetX = (int) (-this.textLayout.getLineLeft(0));
            }
            if (this.textLayout != null) {
                canvas.save();
                canvas2.translate((float) (this.textOffsetX + measuredWidth), (float) (((getMeasuredHeight() - this.textHeight) / 2) + 1));
                this.textLayout.draw(canvas2);
                canvas.restore();
            }
            if (str5 != null || (this.currentTab.id != Integer.MAX_VALUE && (FilterTabsView.this.isEditing || FilterTabsView.this.editingStartAnimationProgress != 0.0f))) {
                if (FilterTabsView.this.aBackgroundColorKey == null) {
                    FilterTabsView.this.textCounterPaint.setColor(Theme.getColor(FilterTabsView.this.backgroundColorKey));
                } else {
                    FilterTabsView.this.textCounterPaint.setColor(ColorUtils.blendARGB(Theme.getColor(FilterTabsView.this.backgroundColorKey), Theme.getColor(FilterTabsView.this.aBackgroundColorKey), FilterTabsView.this.animationValue));
                }
                if (!Theme.hasThemeKey(str6) || !Theme.hasThemeKey(str7)) {
                    FilterTabsView.this.counterPaint.setColor(FilterTabsView.this.textPaint.getColor());
                } else {
                    int color3 = Theme.getColor(str6);
                    if ((FilterTabsView.this.animatingIndicator || FilterTabsView.this.manualScrollingToPosition != -1) && ((i6 = this.currentTab.id) == i2 || i6 == i)) {
                        FilterTabsView.this.counterPaint.setColor(ColorUtils.blendARGB(Theme.getColor(str7), color3, FilterTabsView.this.animatingIndicatorProgress));
                    } else {
                        FilterTabsView.this.counterPaint.setColor(color3);
                    }
                }
                int dp = measuredWidth + this.currentTab.titleWidth + AndroidUtilities.dp(6.0f);
                int measuredHeight = (getMeasuredHeight() - AndroidUtilities.dp(20.0f)) / 2;
                if (this.currentTab.id == Integer.MAX_VALUE || ((!FilterTabsView.this.isEditing && FilterTabsView.this.editingStartAnimationProgress == 0.0f) || str5 != null)) {
                    FilterTabsView.this.counterPaint.setAlpha(255);
                } else {
                    FilterTabsView.this.counterPaint.setAlpha((int) (FilterTabsView.this.editingStartAnimationProgress * 255.0f));
                }
                this.rect.set((float) dp, (float) measuredHeight, (float) (dp + i3), (float) (AndroidUtilities.dp(20.0f) + measuredHeight));
                RectF rectF = this.rect;
                float f = AndroidUtilities.density;
                canvas2.drawRoundRect(rectF, f * 11.5f, f * 11.5f, FilterTabsView.this.counterPaint);
                if (str5 != null) {
                    if (this.currentTab.id != Integer.MAX_VALUE) {
                        FilterTabsView.this.textCounterPaint.setAlpha((int) ((1.0f - FilterTabsView.this.editingStartAnimationProgress) * 255.0f));
                    }
                    RectF rectF2 = this.rect;
                    canvas2.drawText(str5, rectF2.left + ((rectF2.width() - ((float) i4)) / 2.0f), (float) (measuredHeight + AndroidUtilities.dp(14.5f)), FilterTabsView.this.textCounterPaint);
                }
                if (this.currentTab.id != Integer.MAX_VALUE && (FilterTabsView.this.isEditing || FilterTabsView.this.editingStartAnimationProgress != 0.0f)) {
                    FilterTabsView.this.deletePaint.setColor(FilterTabsView.this.textCounterPaint.getColor());
                    FilterTabsView.this.deletePaint.setAlpha((int) (FilterTabsView.this.editingStartAnimationProgress * 255.0f));
                    float dp2 = (float) AndroidUtilities.dp(3.0f);
                    canvas.drawLine(this.rect.centerX() - dp2, this.rect.centerY() - dp2, this.rect.centerX() + dp2, this.rect.centerY() + dp2, FilterTabsView.this.deletePaint);
                    canvas.drawLine(this.rect.centerX() - dp2, this.rect.centerY() + dp2, this.rect.centerX() + dp2, this.rect.centerY() - dp2, FilterTabsView.this.deletePaint);
                }
            }
            if (this.currentTab.id != Integer.MAX_VALUE && FilterTabsView.this.editingAnimationProgress != 0.0f) {
                canvas.restore();
            }
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
        AnonymousClass3 r2 = new RecyclerListView(context) {
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
        this.listView = r2;
        ((DefaultItemAnimator) r2.getItemAnimator()).setDelayAnimations(false);
        this.listView.setSelectorType(7);
        this.listView.setSelectorDrawableColor(Theme.getColor(this.selectorColorKey));
        RecyclerListView recyclerListView = this.listView;
        AnonymousClass4 r3 = new LinearLayoutManager(context, 0, false) {
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i) {
                AnonymousClass1 r2 = new LinearSmoothScroller(recyclerView.getContext()) {
                    /* access modifiers changed from: protected */
                    public void onTargetFound(View view, RecyclerView.State state, RecyclerView.SmoothScroller.Action action) {
                        int calculateDxToMakeVisible = calculateDxToMakeVisible(view, getHorizontalSnapPreference());
                        if (calculateDxToMakeVisible > 0 || (calculateDxToMakeVisible == 0 && view.getLeft() - AndroidUtilities.dp(8.0f) < 0)) {
                            calculateDxToMakeVisible += AndroidUtilities.dp(60.0f);
                        } else if (calculateDxToMakeVisible < 0 || (calculateDxToMakeVisible == 0 && view.getRight() + AndroidUtilities.dp(8.0f) > FilterTabsView.this.getMeasuredWidth())) {
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
        this.layoutManager = r3;
        recyclerListView.setLayoutManager(r3);
        new ItemTouchHelper(new TouchHelperCallback()).attachToRecyclerView(this.listView);
        this.listView.setPadding(AndroidUtilities.dp(7.0f), 0, AndroidUtilities.dp(7.0f), 0);
        this.listView.setClipToPadding(false);
        this.listView.setDrawSelectorBehind(true);
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
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
        boolean z = this.currentPosition < i2;
        this.scrollingToChild = -1;
        this.previousPosition = this.currentPosition;
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

    public void beginCrossfade() {
        try {
            Bitmap createBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            draw(new Canvas(createBitmap));
            this.crossfadeBitmap = createBitmap;
            this.crossfadeAlpha = 1.0f;
            this.commitCrossfade = false;
            this.listView.invalidate();
            invalidate();
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public void commitCrossfade() {
        if (this.crossfadeBitmap != null) {
            this.commitCrossfade = true;
            this.listView.invalidate();
            invalidate();
        }
    }

    public void addTab(int i, String str) {
        int size = this.tabs.size();
        if (size == 0 && this.selectedTabId == -1) {
            this.selectedTabId = i;
        }
        this.positionToId.put(size, i);
        this.idToPosition.put(i, size);
        int i2 = this.selectedTabId;
        if (i2 != -1 && i2 == i) {
            this.currentPosition = size;
        }
        Tab tab = new Tab(i, str);
        this.allTabsWidth += tab.getWidth(true) + AndroidUtilities.dp(32.0f);
        this.tabs.add(tab);
    }

    public void finishAddingTabs() {
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
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00c7  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00de  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0196  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x01ae  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x01b1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean drawChild(android.graphics.Canvas r11, android.view.View r12, long r13) {
        /*
            r10 = this;
            boolean r13 = super.drawChild(r11, r12, r13)
            org.telegram.ui.Components.RecyclerListView r14 = r10.listView
            r0 = 0
            r1 = 0
            if (r12 != r14) goto L_0x00ef
            int r12 = r10.getMeasuredHeight()
            android.graphics.drawable.GradientDrawable r14 = r10.selectorDrawable
            org.telegram.ui.Components.RecyclerListView r2 = r10.listView
            float r2 = r2.getAlpha()
            r3 = 1132396544(0x437var_, float:255.0)
            float r2 = r2 * r3
            int r2 = (int) r2
            r14.setAlpha(r2)
            boolean r14 = r10.animatingIndicator
            r2 = -1
            if (r14 != 0) goto L_0x0056
            int r14 = r10.manualScrollingToPosition
            if (r14 == r2) goto L_0x0028
            goto L_0x0056
        L_0x0028:
            org.telegram.ui.Components.RecyclerListView r14 = r10.listView
            int r2 = r10.currentPosition
            androidx.recyclerview.widget.RecyclerView$ViewHolder r14 = r14.findViewHolderForAdapterPosition(r2)
            if (r14 == 0) goto L_0x0053
            android.view.View r14 = r14.itemView
            org.telegram.ui.Components.FilterTabsView$TabView r14 = (org.telegram.ui.Components.FilterTabsView.TabView) r14
            r2 = 1109393408(0x42200000, float:40.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r4 = r14.tabWidth
            int r2 = java.lang.Math.max(r2, r4)
            float r4 = r14.getX()
            int r14 = r14.getMeasuredWidth()
            int r14 = r14 - r2
            int r14 = r14 / 2
            float r14 = (float) r14
            float r4 = r4 + r14
            int r14 = (int) r4
            goto L_0x00c5
        L_0x0053:
            r14 = 0
            r2 = 0
            goto L_0x00c5
        L_0x0056:
            androidx.recyclerview.widget.LinearLayoutManager r14 = r10.layoutManager
            int r14 = r14.findFirstVisibleItemPosition()
            if (r14 == r2) goto L_0x0053
            org.telegram.ui.Components.RecyclerListView r2 = r10.listView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r2 = r2.findViewHolderForAdapterPosition(r14)
            if (r2 == 0) goto L_0x0053
            boolean r4 = r10.animatingIndicator
            if (r4 == 0) goto L_0x006f
            int r4 = r10.previousPosition
            int r5 = r10.currentPosition
            goto L_0x0073
        L_0x006f:
            int r4 = r10.currentPosition
            int r5 = r10.manualScrollingToPosition
        L_0x0073:
            android.util.SparseIntArray r6 = r10.positionToX
            int r6 = r6.get(r4)
            android.util.SparseIntArray r7 = r10.positionToX
            int r7 = r7.get(r5)
            android.util.SparseIntArray r8 = r10.positionToWidth
            int r4 = r8.get(r4)
            android.util.SparseIntArray r8 = r10.positionToWidth
            int r5 = r8.get(r5)
            int r8 = r10.additionalTabWidth
            r9 = 1098907648(0x41800000, float:16.0)
            if (r8 == 0) goto L_0x00a0
            float r14 = (float) r6
            int r7 = r7 - r6
            float r2 = (float) r7
            float r6 = r10.animatingIndicatorProgress
            float r2 = r2 * r6
            float r14 = r14 + r2
            int r14 = (int) r14
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r14 = r14 + r2
            goto L_0x00bc
        L_0x00a0:
            android.util.SparseIntArray r8 = r10.positionToX
            int r14 = r8.get(r14)
            float r8 = (float) r6
            int r7 = r7 - r6
            float r6 = (float) r7
            float r7 = r10.animatingIndicatorProgress
            float r6 = r6 * r7
            float r8 = r8 + r6
            int r6 = (int) r8
            android.view.View r2 = r2.itemView
            int r2 = r2.getLeft()
            int r14 = r14 - r2
            int r6 = r6 - r14
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r14 = r14 + r6
        L_0x00bc:
            float r2 = (float) r4
            int r5 = r5 - r4
            float r4 = (float) r5
            float r5 = r10.animatingIndicatorProgress
            float r4 = r4 * r5
            float r2 = r2 + r4
            int r2 = (int) r2
        L_0x00c5:
            if (r2 == 0) goto L_0x00da
            android.graphics.drawable.GradientDrawable r4 = r10.selectorDrawable
            r5 = 1082130432(0x40800000, float:4.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dpr(r5)
            int r5 = r12 - r5
            int r2 = r2 + r14
            r4.setBounds(r14, r5, r2, r12)
            android.graphics.drawable.GradientDrawable r12 = r10.selectorDrawable
            r12.draw(r11)
        L_0x00da:
            android.graphics.Bitmap r12 = r10.crossfadeBitmap
            if (r12 == 0) goto L_0x00ef
            android.graphics.Paint r12 = r10.crossfadePaint
            float r14 = r10.crossfadeAlpha
            float r14 = r14 * r3
            int r14 = (int) r14
            r12.setAlpha(r14)
            android.graphics.Bitmap r12 = r10.crossfadeBitmap
            android.graphics.Paint r14 = r10.crossfadePaint
            r11.drawBitmap(r12, r1, r1, r14)
        L_0x00ef:
            long r11 = android.os.SystemClock.elapsedRealtime()
            r2 = 17
            long r4 = r10.lastEditingAnimationTime
            long r4 = r11 - r4
            long r2 = java.lang.Math.min(r2, r4)
            r10.lastEditingAnimationTime = r11
            boolean r11 = r10.isEditing
            r12 = 1065353216(0x3var_, float:1.0)
            r14 = 1
            if (r11 != 0) goto L_0x010f
            float r11 = r10.editingAnimationProgress
            int r11 = (r11 > r1 ? 1 : (r11 == r1 ? 0 : -1))
            if (r11 == 0) goto L_0x010d
            goto L_0x010f
        L_0x010d:
            r11 = 0
            goto L_0x0165
        L_0x010f:
            boolean r11 = r10.editingForwardAnimation
            r4 = 1123024896(0x42var_, float:120.0)
            if (r11 == 0) goto L_0x013c
            float r11 = r10.editingAnimationProgress
            int r11 = (r11 > r1 ? 1 : (r11 == r1 ? 0 : -1))
            if (r11 > 0) goto L_0x011d
            r11 = 1
            goto L_0x011e
        L_0x011d:
            r11 = 0
        L_0x011e:
            float r5 = r10.editingAnimationProgress
            float r6 = (float) r2
            float r6 = r6 / r4
            float r5 = r5 + r6
            r10.editingAnimationProgress = r5
            boolean r4 = r10.isEditing
            if (r4 != 0) goto L_0x0131
            if (r11 == 0) goto L_0x0131
            int r11 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r11 < 0) goto L_0x0131
            r10.editingAnimationProgress = r1
        L_0x0131:
            float r11 = r10.editingAnimationProgress
            int r11 = (r11 > r12 ? 1 : (r11 == r12 ? 0 : -1))
            if (r11 < 0) goto L_0x0164
            r10.editingAnimationProgress = r12
            r10.editingForwardAnimation = r0
            goto L_0x0164
        L_0x013c:
            float r11 = r10.editingAnimationProgress
            int r11 = (r11 > r1 ? 1 : (r11 == r1 ? 0 : -1))
            if (r11 < 0) goto L_0x0144
            r11 = 1
            goto L_0x0145
        L_0x0144:
            r11 = 0
        L_0x0145:
            float r5 = r10.editingAnimationProgress
            float r6 = (float) r2
            float r6 = r6 / r4
            float r5 = r5 - r6
            r10.editingAnimationProgress = r5
            boolean r4 = r10.isEditing
            if (r4 != 0) goto L_0x0158
            if (r11 == 0) goto L_0x0158
            int r11 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r11 > 0) goto L_0x0158
            r10.editingAnimationProgress = r1
        L_0x0158:
            float r11 = r10.editingAnimationProgress
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
            int r11 = (r11 > r4 ? 1 : (r11 == r4 ? 0 : -1))
            if (r11 > 0) goto L_0x0164
            r10.editingAnimationProgress = r4
            r10.editingForwardAnimation = r14
        L_0x0164:
            r11 = 1
        L_0x0165:
            boolean r4 = r10.isEditing
            r5 = 1127481344(0x43340000, float:180.0)
            if (r4 == 0) goto L_0x017e
            float r4 = r10.editingStartAnimationProgress
            int r6 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1))
            if (r6 >= 0) goto L_0x0192
            float r11 = (float) r2
            float r11 = r11 / r5
            float r4 = r4 + r11
            r10.editingStartAnimationProgress = r4
            int r11 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1))
            if (r11 <= 0) goto L_0x017c
            r10.editingStartAnimationProgress = r12
        L_0x017c:
            r11 = 1
            goto L_0x0192
        L_0x017e:
            if (r4 != 0) goto L_0x0192
            float r12 = r10.editingStartAnimationProgress
            int r4 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r4 <= 0) goto L_0x0192
            float r11 = (float) r2
            float r11 = r11 / r5
            float r12 = r12 - r11
            r10.editingStartAnimationProgress = r12
            int r11 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r11 >= 0) goto L_0x017c
            r10.editingStartAnimationProgress = r1
            goto L_0x017c
        L_0x0192:
            boolean r12 = r10.commitCrossfade
            if (r12 == 0) goto L_0x01ae
            float r11 = r10.crossfadeAlpha
            float r12 = (float) r2
            float r12 = r12 / r5
            float r11 = r11 - r12
            r10.crossfadeAlpha = r11
            int r11 = (r11 > r1 ? 1 : (r11 == r1 ? 0 : -1))
            if (r11 >= 0) goto L_0x01af
            r10.commitCrossfade = r0
            android.graphics.Bitmap r11 = r10.crossfadeBitmap
            if (r11 == 0) goto L_0x01af
            r11.recycle()
            r11 = 0
            r10.crossfadeBitmap = r11
            goto L_0x01af
        L_0x01ae:
            r14 = r11
        L_0x01af:
            if (r14 == 0) goto L_0x01b9
            org.telegram.ui.Components.RecyclerListView r11 = r10.listView
            r11.invalidateViews()
            r10.invalidate()
        L_0x01b9:
            return r13
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FilterTabsView.drawChild(android.graphics.Canvas, android.view.View, long):boolean");
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (!this.tabs.isEmpty()) {
            int size = (View.MeasureSpec.getSize(i) - AndroidUtilities.dp(7.0f)) - AndroidUtilities.dp(7.0f);
            Tab tab = this.tabs.get(0);
            int i3 = NUM;
            String str = "FilterAllChats";
            tab.setTitle(LocaleController.getString(str, NUM));
            int width = tab.getWidth(false);
            if (this.allTabsWidth > size) {
                i3 = NUM;
                str = "FilterAllChatsShort";
            }
            tab.setTitle(LocaleController.getString(str, i3));
            int width2 = (this.allTabsWidth - width) + tab.getWidth(false);
            int i4 = this.additionalTabWidth;
            int size2 = width2 < size ? (size - width2) / this.tabs.size() : 0;
            this.additionalTabWidth = size2;
            if (i4 != size2) {
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
            ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(tLRPC$TL_messages_updateDialogFiltersOrder, $$Lambda$FilterTabsView$oWsODGqFhLoSTELjrJH5FUevIoE.INSTANCE);
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
            if (tab.counter != this.delegate.getTabCounter(tab.id)) {
                if (this.positionToWidth.get(i) != tab.getWidth(true) || this.invalidated) {
                    this.invalidated = true;
                    requestLayout();
                    this.adapter.notifyDataSetChanged();
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
        this.adapter.notifyDataSetChanged();
        this.allTabsWidth = 0;
        this.tabs.get(0).setTitle(LocaleController.getString("FilterAllChats", NUM));
        for (int i2 = 0; i2 < size; i2++) {
            this.allTabsWidth += this.tabs.get(i2).getWidth(true) + AndroidUtilities.dp(32.0f);
        }
        z = true;
        if (z) {
            this.listView.invalidateViews();
        }
    }

    public void notifyTabCounterChanged(int i) {
        int i2 = this.idToPosition.get(i, -1);
        if (i2 >= 0 && i2 < this.tabs.size()) {
            Tab tab = this.tabs.get(i2);
            if (tab.counter != this.delegate.getTabCounter(tab.id)) {
                this.listView.invalidateViews();
                if (this.positionToWidth.get(i2) != tab.getWidth(true) || this.invalidated) {
                    this.invalidated = true;
                    requestLayout();
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

        public long getItemId(int i) {
            return (long) i;
        }

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

        public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float f, float f2, int i, boolean z) {
            super.onChildDraw(canvas, recyclerView, viewHolder, f, f2, i, z);
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
