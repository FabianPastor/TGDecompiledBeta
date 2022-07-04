package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.GroupCallTextCell;
import org.telegram.ui.Cells.GroupCallUserCell;
import org.telegram.ui.Components.AnimationProperties;

public class UsersAlertBase extends BottomSheet {
    public static final Property<UsersAlertBase, Float> COLOR_PROGRESS = new AnimationProperties.FloatProperty<UsersAlertBase>("colorProgress") {
        public void setValue(UsersAlertBase object, float value) {
            object.setColorProgress(value);
        }

        public Float get(UsersAlertBase object) {
            return Float.valueOf(object.getColorProgress());
        }
    };
    /* access modifiers changed from: private */
    public int backgroundColor;
    private float colorProgress;
    protected StickerEmptyView emptyView;
    protected FlickerLoadingView flickerLoadingView;
    protected FrameLayout frameLayout;
    protected boolean isEmptyViewVisible = true;
    protected String keyActionBarUnscrolled = "windowBackgroundWhite";
    protected String keyInviteMembersBackground = "windowBackgroundWhite";
    protected String keyLastSeenText = "windowBackgroundWhiteGrayText";
    protected String keyLastSeenTextUnscrolled = "windowBackgroundWhiteGrayText";
    protected String keyListSelector = "listSelectorSDK21";
    protected String keyListViewBackground = "windowBackgroundWhite";
    protected String keyNameText = "windowBackgroundWhiteBlackText";
    protected String keyScrollUp = "key_sheet_scrollUp";
    protected String keySearchBackground = "dialogSearchBackground";
    protected String keySearchIcon = "dialogSearchIcon";
    protected String keySearchIconUnscrolled = "dialogSearchIcon";
    protected String keySearchPlaceholder = "dialogSearchHint";
    protected String keySearchText = "dialogSearchText";
    protected final FillLastLinearLayoutManager layoutManager;
    protected RecyclerListView listView;
    protected RecyclerView.Adapter listViewAdapter;
    protected boolean needSnapToTop = true;
    /* access modifiers changed from: private */
    public RectF rect = new RectF();
    protected int scrollOffsetY;
    protected RecyclerView.Adapter searchListViewAdapter;
    protected SearchField searchView;
    protected View shadow;
    protected AnimatorSet shadowAnimation;
    protected Drawable shadowDrawable;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public UsersAlertBase(android.content.Context r19, boolean r20, int r21, org.telegram.ui.ActionBar.Theme.ResourcesProvider r22) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            r2 = r20
            r3 = r22
            r0.<init>(r1, r2, r3)
            android.graphics.RectF r4 = new android.graphics.RectF
            r4.<init>()
            r0.rect = r4
            r4 = 1
            r0.needSnapToTop = r4
            r0.isEmptyViewVisible = r4
            java.lang.String r5 = "key_sheet_scrollUp"
            r0.keyScrollUp = r5
            java.lang.String r5 = "listSelectorSDK21"
            r0.keyListSelector = r5
            java.lang.String r5 = "dialogSearchBackground"
            r0.keySearchBackground = r5
            java.lang.String r5 = "windowBackgroundWhite"
            r0.keyInviteMembersBackground = r5
            r0.keyListViewBackground = r5
            r0.keyActionBarUnscrolled = r5
            java.lang.String r5 = "windowBackgroundWhiteBlackText"
            r0.keyNameText = r5
            java.lang.String r5 = "windowBackgroundWhiteGrayText"
            r0.keyLastSeenText = r5
            r0.keyLastSeenTextUnscrolled = r5
            java.lang.String r5 = "dialogSearchHint"
            r0.keySearchPlaceholder = r5
            java.lang.String r5 = "dialogSearchText"
            r0.keySearchText = r5
            java.lang.String r5 = "dialogSearchIcon"
            r0.keySearchIcon = r5
            r0.keySearchIconUnscrolled = r5
            r18.updateColorKeys()
            r5 = 75
            r0.setDimBehindAlpha(r5)
            r5 = r21
            r0.currentAccount = r5
            android.content.res.Resources r6 = r19.getResources()
            r7 = 2131166138(0x7var_ba, float:1.7946513E38)
            android.graphics.drawable.Drawable r6 = r6.getDrawable(r7)
            android.graphics.drawable.Drawable r6 = r6.mutate()
            r0.shadowDrawable = r6
            org.telegram.ui.Components.UsersAlertBase$ContainerView r6 = r18.createContainerView(r19)
            r0.containerView = r6
            android.view.ViewGroup r6 = r0.containerView
            r7 = 0
            r6.setWillNotDraw(r7)
            android.view.ViewGroup r6 = r0.containerView
            r6.setClipChildren(r7)
            android.view.ViewGroup r6 = r0.containerView
            int r8 = r0.backgroundPaddingLeft
            int r9 = r0.backgroundPaddingLeft
            r6.setPadding(r8, r7, r9, r7)
            android.widget.FrameLayout r6 = new android.widget.FrameLayout
            r6.<init>(r1)
            r0.frameLayout = r6
            org.telegram.ui.Components.UsersAlertBase$SearchField r6 = new org.telegram.ui.Components.UsersAlertBase$SearchField
            r6.<init>(r1)
            r0.searchView = r6
            android.widget.FrameLayout r8 = r0.frameLayout
            r9 = -1
            r10 = 51
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r9, (int) r9, (int) r10)
            r8.addView(r6, r11)
            org.telegram.ui.Components.FlickerLoadingView r6 = new org.telegram.ui.Components.FlickerLoadingView
            r6.<init>(r1)
            r0.flickerLoadingView = r6
            r8 = 6
            r6.setViewType(r8)
            org.telegram.ui.Components.FlickerLoadingView r6 = r0.flickerLoadingView
            r6.showDate(r7)
            org.telegram.ui.Components.FlickerLoadingView r6 = r0.flickerLoadingView
            r6.setUseHeaderOffset(r4)
            org.telegram.ui.Components.FlickerLoadingView r6 = r0.flickerLoadingView
            java.lang.String r8 = r0.keyInviteMembersBackground
            java.lang.String r11 = r0.keySearchBackground
            java.lang.String r12 = r0.keyActionBarUnscrolled
            r6.setColors(r8, r11, r12)
            org.telegram.ui.Components.StickerEmptyView r6 = new org.telegram.ui.Components.StickerEmptyView
            org.telegram.ui.Components.FlickerLoadingView r8 = r0.flickerLoadingView
            r6.<init>(r1, r8, r4)
            r0.emptyView = r6
            org.telegram.ui.Components.FlickerLoadingView r8 = r0.flickerLoadingView
            r11 = -1
            r12 = -1082130432(0xffffffffbvar_, float:-1.0)
            r13 = 0
            r14 = 0
            r15 = 1073741824(0x40000000, float:2.0)
            r16 = 0
            r17 = 0
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r6.addView(r8, r7, r11)
            org.telegram.ui.Components.StickerEmptyView r6 = r0.emptyView
            android.widget.TextView r6 = r6.title
            java.lang.String r8 = "NoResult"
            r11 = 2131626858(0x7f0e0b6a, float:1.8880964E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r11)
            r6.setText(r8)
            org.telegram.ui.Components.StickerEmptyView r6 = r0.emptyView
            android.widget.TextView r6 = r6.subtitle
            java.lang.String r8 = "SearchEmptyViewFilteredSubtitle2"
            r11 = 2131628098(0x7f0e1042, float:1.888348E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r11)
            r6.setText(r8)
            org.telegram.ui.Components.StickerEmptyView r6 = r0.emptyView
            r8 = 8
            r6.setVisibility(r8)
            org.telegram.ui.Components.StickerEmptyView r6 = r0.emptyView
            r6.setAnimateLayoutChange(r4)
            org.telegram.ui.Components.StickerEmptyView r6 = r0.emptyView
            r6.showProgress(r4, r7)
            org.telegram.ui.Components.StickerEmptyView r6 = r0.emptyView
            java.lang.String r8 = r0.keyNameText
            java.lang.String r11 = r0.keyLastSeenText
            java.lang.String r12 = r0.keyInviteMembersBackground
            java.lang.String r13 = r0.keySearchBackground
            r6.setColors(r8, r11, r12, r13)
            android.view.ViewGroup r6 = r0.containerView
            org.telegram.ui.Components.StickerEmptyView r8 = r0.emptyView
            r11 = -1
            r12 = -1082130432(0xffffffffbvar_, float:-1.0)
            r13 = 51
            r15 = 1115160576(0x42780000, float:62.0)
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r6.addView(r8, r11)
            org.telegram.ui.Components.UsersAlertBase$1 r6 = new org.telegram.ui.Components.UsersAlertBase$1
            r6.<init>(r1)
            r0.listView = r6
            r8 = 13
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r6.setTag(r8)
            org.telegram.ui.Components.RecyclerListView r6 = r0.listView
            r8 = 1111490560(0x42400000, float:48.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r6.setPadding(r7, r7, r7, r8)
            org.telegram.ui.Components.RecyclerListView r6 = r0.listView
            r6.setClipToPadding(r7)
            org.telegram.ui.Components.RecyclerListView r6 = r0.listView
            r6.setHideIfEmpty(r7)
            org.telegram.ui.Components.RecyclerListView r6 = r0.listView
            java.lang.String r8 = r0.keyListSelector
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r6.setSelectorDrawableColor(r8)
            org.telegram.ui.Components.FillLastLinearLayoutManager r6 = new org.telegram.ui.Components.FillLastLinearLayoutManager
            android.content.Context r12 = r18.getContext()
            r8 = 1090519040(0x41000000, float:8.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            r13 = 1
            r14 = 0
            r11 = r6
            r16 = r8
            r11.<init>(r12, r13, r14, r15, r16)
            r0.layoutManager = r6
            r6.setBind(r7)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            r8.setLayoutManager(r6)
            org.telegram.ui.Components.RecyclerListView r6 = r0.listView
            r6.setHorizontalScrollBarEnabled(r7)
            org.telegram.ui.Components.RecyclerListView r6 = r0.listView
            r6.setVerticalScrollBarEnabled(r7)
            android.view.ViewGroup r6 = r0.containerView
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            r11 = -1
            r12 = -1082130432(0xffffffffbvar_, float:-1.0)
            r13 = 51
            r14 = 0
            r15 = 0
            r16 = 0
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r6.addView(r8, r11)
            org.telegram.ui.Components.RecyclerListView r6 = r0.listView
            org.telegram.ui.Components.UsersAlertBase$2 r8 = new org.telegram.ui.Components.UsersAlertBase$2
            r8.<init>()
            r6.setOnScrollListener(r8)
            android.widget.FrameLayout$LayoutParams r6 = new android.widget.FrameLayout$LayoutParams
            int r8 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r6.<init>(r9, r8, r10)
            r8 = 1114112000(0x42680000, float:58.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r6.topMargin = r8
            android.view.View r8 = new android.view.View
            r8.<init>(r1)
            r0.shadow = r8
            java.lang.String r11 = "dialogShadowLine"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r8.setBackgroundColor(r11)
            android.view.View r8 = r0.shadow
            r11 = 0
            r8.setAlpha(r11)
            android.view.View r8 = r0.shadow
            java.lang.Integer r12 = java.lang.Integer.valueOf(r4)
            r8.setTag(r12)
            android.view.ViewGroup r8 = r0.containerView
            android.view.View r12 = r0.shadow
            r8.addView(r12, r6)
            android.view.ViewGroup r8 = r0.containerView
            android.widget.FrameLayout r12 = r0.frameLayout
            r13 = 58
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r9, (int) r13, (int) r10)
            r8.addView(r12, r9)
            r0.setColorProgress(r11)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            org.telegram.ui.Components.StickerEmptyView r9 = r0.emptyView
            r8.setEmptyView(r9)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            r8.setAnimateEmptyView(r4, r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.UsersAlertBase.<init>(android.content.Context, boolean, int, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AndroidUtilities.statusBarHeight = AndroidUtilities.getStatusBarHeight(getContext());
    }

    /* access modifiers changed from: protected */
    public ContainerView createContainerView(Context context) {
        return new ContainerView(context);
    }

    /* access modifiers changed from: protected */
    public boolean isAllowSelectChildAtPosition(float x, float y) {
        return y >= ((float) (AndroidUtilities.dp(58.0f) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
    }

    /* access modifiers changed from: protected */
    public void updateColorKeys() {
    }

    protected class SearchField extends FrameLayout {
        /* access modifiers changed from: private */
        public final ImageView clearSearchImageView;
        private final CloseProgressDrawable2 progressDrawable;
        private final View searchBackground;
        protected EditTextBoldCursor searchEditText;
        private final ImageView searchIconImageView;

        public SearchField(Context context) {
            super(context);
            View view = new View(context);
            this.searchBackground = view;
            view.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), Theme.getColor(UsersAlertBase.this.keySearchBackground)));
            addView(view, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 11.0f, 14.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.searchIconImageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setImageResource(NUM);
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(UsersAlertBase.this.keySearchPlaceholder), PorterDuff.Mode.MULTIPLY));
            addView(imageView, LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 11.0f, 0.0f, 0.0f));
            ImageView imageView2 = new ImageView(context);
            this.clearSearchImageView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            AnonymousClass1 r1 = new CloseProgressDrawable2(UsersAlertBase.this) {
                /* access modifiers changed from: protected */
                public int getCurrentColor() {
                    return Theme.getColor(UsersAlertBase.this.keySearchPlaceholder);
                }
            };
            this.progressDrawable = r1;
            imageView2.setImageDrawable(r1);
            r1.setSide(AndroidUtilities.dp(7.0f));
            imageView2.setScaleX(0.1f);
            imageView2.setScaleY(0.1f);
            imageView2.setAlpha(0.0f);
            addView(imageView2, LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 11.0f, 14.0f, 0.0f));
            imageView2.setOnClickListener(new UsersAlertBase$SearchField$$ExternalSyntheticLambda0(this));
            AnonymousClass2 r0 = new EditTextBoldCursor(context, UsersAlertBase.this) {
                public boolean dispatchTouchEvent(MotionEvent event) {
                    MotionEvent e = MotionEvent.obtain(event);
                    e.setLocation(e.getRawX(), e.getRawY() - UsersAlertBase.this.containerView.getTranslationY());
                    if (e.getAction() == 1) {
                        e.setAction(3);
                    }
                    UsersAlertBase.this.listView.dispatchTouchEvent(e);
                    e.recycle();
                    return super.dispatchTouchEvent(event);
                }
            };
            this.searchEditText = r0;
            r0.setTextSize(1, 16.0f);
            this.searchEditText.setHintTextColor(Theme.getColor(UsersAlertBase.this.keySearchPlaceholder));
            this.searchEditText.setTextColor(Theme.getColor(UsersAlertBase.this.keySearchText));
            this.searchEditText.setBackgroundDrawable((Drawable) null);
            this.searchEditText.setPadding(0, 0, 0, 0);
            this.searchEditText.setMaxLines(1);
            this.searchEditText.setLines(1);
            this.searchEditText.setSingleLine(true);
            this.searchEditText.setImeOptions(NUM);
            this.searchEditText.setHint(LocaleController.getString("VoipGroupSearchMembers", NUM));
            this.searchEditText.setCursorColor(Theme.getColor(UsersAlertBase.this.keySearchText));
            this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
            this.searchEditText.setCursorWidth(1.5f);
            addView(this.searchEditText, LayoutHelper.createFrame(-1, 40.0f, 51, 54.0f, 9.0f, 46.0f, 0.0f));
            this.searchEditText.addTextChangedListener(new TextWatcher(UsersAlertBase.this) {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void afterTextChanged(Editable s) {
                    boolean show = SearchField.this.searchEditText.length() > 0;
                    float f = 0.0f;
                    if (show != (SearchField.this.clearSearchImageView.getAlpha() != 0.0f)) {
                        ViewPropertyAnimator animate = SearchField.this.clearSearchImageView.animate();
                        float f2 = 1.0f;
                        if (show) {
                            f = 1.0f;
                        }
                        ViewPropertyAnimator scaleX = animate.alpha(f).setDuration(150).scaleX(show ? 1.0f : 0.1f);
                        if (!show) {
                            f2 = 0.1f;
                        }
                        scaleX.scaleY(f2).start();
                    }
                    String text = SearchField.this.searchEditText.getText().toString();
                    int oldItemsCount = UsersAlertBase.this.listView.getAdapter() == null ? 0 : UsersAlertBase.this.listView.getAdapter().getItemCount();
                    UsersAlertBase.this.search(text);
                    if (!(!TextUtils.isEmpty(text) || UsersAlertBase.this.listView == null || UsersAlertBase.this.listView.getAdapter() == UsersAlertBase.this.listViewAdapter)) {
                        UsersAlertBase.this.listView.setAnimateEmptyView(false, 0);
                        UsersAlertBase.this.listView.setAdapter(UsersAlertBase.this.listViewAdapter);
                        UsersAlertBase.this.listView.setAnimateEmptyView(true, 0);
                        if (oldItemsCount == 0) {
                            UsersAlertBase.this.showItemsAnimated(0);
                        }
                    }
                    UsersAlertBase.this.flickerLoadingView.setVisibility(0);
                }
            });
            this.searchEditText.setOnEditorActionListener(new UsersAlertBase$SearchField$$ExternalSyntheticLambda1(this));
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-UsersAlertBase$SearchField  reason: not valid java name */
        public /* synthetic */ void m1545x1c6cec7f(View v) {
            this.searchEditText.setText("");
            AndroidUtilities.showKeyboard(this.searchEditText);
        }

        /* renamed from: lambda$new$1$org-telegram-ui-Components-UsersAlertBase$SearchField  reason: not valid java name */
        public /* synthetic */ boolean m1546x45CLASSNAMEc0(TextView v, int actionId, KeyEvent event) {
            if (event == null) {
                return false;
            }
            if ((event.getAction() != 1 || event.getKeyCode() != 84) && (event.getAction() != 0 || event.getKeyCode() != 66)) {
                return false;
            }
            AndroidUtilities.hideKeyboard(this.searchEditText);
            return false;
        }

        public void hideKeyboard() {
            AndroidUtilities.hideKeyboard(this.searchEditText);
        }

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            UsersAlertBase.this.onSearchViewTouched(ev, this.searchEditText);
            return super.onInterceptTouchEvent(ev);
        }

        public void closeSearch() {
            this.clearSearchImageView.callOnClick();
            AndroidUtilities.hideKeyboard(this.searchEditText);
        }
    }

    /* access modifiers changed from: protected */
    public void onSearchViewTouched(MotionEvent ev, EditTextBoldCursor searchEditText) {
    }

    /* access modifiers changed from: protected */
    public void search(String text) {
    }

    /* access modifiers changed from: private */
    public float getColorProgress() {
        return this.colorProgress;
    }

    /* access modifiers changed from: protected */
    public void setColorProgress(float progress) {
        this.colorProgress = progress;
        this.backgroundColor = AndroidUtilities.getOffsetColor(Theme.getColor(this.keyInviteMembersBackground), Theme.getColor(this.keyListViewBackground), progress, 1.0f);
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(this.backgroundColor, PorterDuff.Mode.MULTIPLY));
        this.frameLayout.setBackgroundColor(this.backgroundColor);
        this.navBarColor = this.backgroundColor;
        this.listView.setGlowColor(this.backgroundColor);
        int color = AndroidUtilities.getOffsetColor(Theme.getColor(this.keyLastSeenTextUnscrolled), Theme.getColor(this.keyLastSeenText), progress, 1.0f);
        int color2 = AndroidUtilities.getOffsetColor(Theme.getColor(this.keySearchIconUnscrolled), Theme.getColor(this.keySearchIcon), progress, 1.0f);
        int N = this.listView.getChildCount();
        for (int a = 0; a < N; a++) {
            View child = this.listView.getChildAt(a);
            if (child instanceof GroupCallTextCell) {
                ((GroupCallTextCell) child).setColors(color, color);
            } else if (child instanceof GroupCallUserCell) {
                ((GroupCallUserCell) child).setGrayIconColor(this.shadow.getTag() != null ? this.keySearchIcon : this.keySearchIconUnscrolled, color2);
            }
        }
        this.containerView.invalidate();
        this.listView.invalidate();
        this.container.invalidate();
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public void dismiss() {
        AndroidUtilities.hideKeyboard(this.searchView.searchEditText);
        super.dismiss();
    }

    /* access modifiers changed from: protected */
    public void updateLayout() {
        int top;
        if (this.listView.getChildCount() > 0) {
            RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(0);
            if (holder != null) {
                top = holder.itemView.getTop() - AndroidUtilities.dp(8.0f);
            } else {
                top = 0;
            }
            int newOffset = (top <= 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : top;
            if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
                runShadowAnimation(true);
            } else {
                newOffset = top;
                runShadowAnimation(false);
            }
            if (this.scrollOffsetY != newOffset) {
                this.scrollOffsetY = newOffset;
                setTranslationY(newOffset);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void setTranslationY(int newOffset) {
        this.listView.setTopGlowOffset(newOffset);
        this.frameLayout.setTranslationY((float) newOffset);
        this.emptyView.setTranslationY((float) newOffset);
        this.containerView.invalidate();
    }

    private void runShadowAnimation(final boolean show) {
        if ((show && this.shadow.getTag() != null) || (!show && this.shadow.getTag() == null)) {
            this.shadow.setTag(show ? null : 1);
            if (show) {
                this.shadow.setVisibility(0);
            }
            AnimatorSet animatorSet = this.shadowAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.shadowAnimation = animatorSet2;
            Animator[] animatorArr = new Animator[1];
            View view = this.shadow;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet2.playTogether(animatorArr);
            this.shadowAnimation.setDuration(150);
            this.shadowAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (UsersAlertBase.this.shadowAnimation != null && UsersAlertBase.this.shadowAnimation.equals(animation)) {
                        if (!show) {
                            UsersAlertBase.this.shadow.setVisibility(4);
                        }
                        UsersAlertBase.this.shadowAnimation = null;
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (UsersAlertBase.this.shadowAnimation != null && UsersAlertBase.this.shadowAnimation.equals(animation)) {
                        UsersAlertBase.this.shadowAnimation = null;
                    }
                }
            });
            this.shadowAnimation.start();
        }
    }

    /* access modifiers changed from: protected */
    public void showItemsAnimated(final int from) {
        if (isShowing()) {
            this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    UsersAlertBase.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    int n = UsersAlertBase.this.listView.getChildCount();
                    AnimatorSet animatorSet = new AnimatorSet();
                    for (int i = 0; i < n; i++) {
                        View child = UsersAlertBase.this.listView.getChildAt(i);
                        int position = UsersAlertBase.this.listView.getChildAdapterPosition(child);
                        if (position >= from) {
                            if (position == 1 && UsersAlertBase.this.listView.getAdapter() == UsersAlertBase.this.searchListViewAdapter && (child instanceof GraySectionCell)) {
                                child = ((GraySectionCell) child).getTextView();
                            }
                            child.setAlpha(0.0f);
                            ObjectAnimator a = ObjectAnimator.ofFloat(child, View.ALPHA, new float[]{0.0f, 1.0f});
                            a.setStartDelay((long) ((int) ((((float) Math.min(UsersAlertBase.this.listView.getMeasuredHeight(), Math.max(0, child.getTop()))) / ((float) UsersAlertBase.this.listView.getMeasuredHeight())) * 100.0f)));
                            a.setDuration(200);
                            animatorSet.playTogether(new Animator[]{a});
                        }
                    }
                    animatorSet.start();
                    return true;
                }
            });
        }
    }

    protected class ContainerView extends FrameLayout {
        private boolean ignoreLayout = false;
        float snapToTopOffset;
        ValueAnimator valueAnimator;

        public ContainerView(Context context) {
            super(context);
        }

        public void setTranslationY(float translationY) {
            super.setTranslationY(translationY);
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int padding;
            int totalHeight = View.MeasureSpec.getSize(heightMeasureSpec);
            if (Build.VERSION.SDK_INT >= 21) {
                this.ignoreLayout = true;
                setPadding(UsersAlertBase.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, UsersAlertBase.this.backgroundPaddingLeft, 0);
                this.ignoreLayout = false;
            }
            int availableHeight = totalHeight - getPaddingTop();
            if (UsersAlertBase.this.keyboardVisible) {
                padding = AndroidUtilities.dp(8.0f);
                UsersAlertBase.this.setAllowNestedScroll(false);
                if (UsersAlertBase.this.scrollOffsetY != 0) {
                    float f = (float) UsersAlertBase.this.scrollOffsetY;
                    this.snapToTopOffset = f;
                    setTranslationY(f);
                    ValueAnimator valueAnimator2 = this.valueAnimator;
                    if (valueAnimator2 != null) {
                        valueAnimator2.removeAllListeners();
                        this.valueAnimator.cancel();
                    }
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.snapToTopOffset, 0.0f});
                    this.valueAnimator = ofFloat;
                    ofFloat.addUpdateListener(new UsersAlertBase$ContainerView$$ExternalSyntheticLambda0(this));
                    this.valueAnimator.setDuration(250);
                    this.valueAnimator.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                    this.valueAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            ContainerView.this.snapToTopOffset = 0.0f;
                            ContainerView.this.setTranslationY(0.0f);
                            ContainerView.this.valueAnimator = null;
                        }
                    });
                    this.valueAnimator.start();
                } else if (this.valueAnimator != null) {
                    setTranslationY(this.snapToTopOffset);
                }
            } else {
                padding = (availableHeight - ((availableHeight / 5) * 3)) + AndroidUtilities.dp(8.0f);
                UsersAlertBase.this.setAllowNestedScroll(true);
            }
            if (UsersAlertBase.this.listView.getPaddingTop() != padding) {
                this.ignoreLayout = true;
                UsersAlertBase.this.listView.setPadding(0, padding, 0, 0);
                this.ignoreLayout = false;
            }
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(totalHeight, NUM));
        }

        /* renamed from: lambda$onMeasure$0$org-telegram-ui-Components-UsersAlertBase$ContainerView  reason: not valid java name */
        public /* synthetic */ void m1544x41a7edb2(ValueAnimator valueAnimator2) {
            float floatValue = ((Float) valueAnimator2.getAnimatedValue()).floatValue();
            this.snapToTopOffset = floatValue;
            setTranslationY(floatValue);
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            UsersAlertBase.this.updateLayout();
        }

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (ev.getAction() != 0 || ev.getY() >= ((float) UsersAlertBase.this.scrollOffsetY)) {
                return super.onInterceptTouchEvent(ev);
            }
            UsersAlertBase.this.dismiss();
            return true;
        }

        public boolean onTouchEvent(MotionEvent e) {
            return !UsersAlertBase.this.isDismissed() && super.onTouchEvent(e);
        }

        public void requestLayout() {
            if (!this.ignoreLayout) {
                super.requestLayout();
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            canvas.save();
            int y = (UsersAlertBase.this.scrollOffsetY - UsersAlertBase.this.backgroundPaddingTop) + AndroidUtilities.dp(6.0f);
            int top = (UsersAlertBase.this.scrollOffsetY - UsersAlertBase.this.backgroundPaddingTop) - AndroidUtilities.dp(13.0f);
            int height = getMeasuredHeight() + AndroidUtilities.dp(50.0f) + UsersAlertBase.this.backgroundPaddingTop;
            int statusBarHeight = 0;
            float radProgress = 1.0f;
            if (Build.VERSION.SDK_INT >= 21) {
                top += AndroidUtilities.statusBarHeight;
                y += AndroidUtilities.statusBarHeight;
                height -= AndroidUtilities.statusBarHeight;
                if (((float) (UsersAlertBase.this.backgroundPaddingTop + top)) + getTranslationY() < ((float) (AndroidUtilities.statusBarHeight * 2))) {
                    int diff = (int) Math.min((float) AndroidUtilities.statusBarHeight, ((float) (((AndroidUtilities.statusBarHeight * 2) - top) - UsersAlertBase.this.backgroundPaddingTop)) - getTranslationY());
                    top -= diff;
                    height += diff;
                    radProgress = 1.0f - Math.min(1.0f, ((float) (diff * 2)) / ((float) AndroidUtilities.statusBarHeight));
                }
                if (((float) (UsersAlertBase.this.backgroundPaddingTop + top)) + getTranslationY() < ((float) AndroidUtilities.statusBarHeight)) {
                    statusBarHeight = (int) Math.min((float) AndroidUtilities.statusBarHeight, ((float) ((AndroidUtilities.statusBarHeight - top) - UsersAlertBase.this.backgroundPaddingTop)) - getTranslationY());
                }
            }
            UsersAlertBase.this.shadowDrawable.setBounds(0, top, getMeasuredWidth(), height);
            UsersAlertBase.this.shadowDrawable.draw(canvas);
            if (radProgress != 1.0f) {
                Theme.dialogs_onlineCirclePaint.setColor(UsersAlertBase.this.backgroundColor);
                UsersAlertBase.this.rect.set((float) UsersAlertBase.this.backgroundPaddingLeft, (float) (UsersAlertBase.this.backgroundPaddingTop + top), (float) (getMeasuredWidth() - UsersAlertBase.this.backgroundPaddingLeft), (float) (UsersAlertBase.this.backgroundPaddingTop + top + AndroidUtilities.dp(24.0f)));
                canvas.drawRoundRect(UsersAlertBase.this.rect, ((float) AndroidUtilities.dp(12.0f)) * radProgress, ((float) AndroidUtilities.dp(12.0f)) * radProgress, Theme.dialogs_onlineCirclePaint);
            }
            int w = AndroidUtilities.dp(36.0f);
            UsersAlertBase.this.rect.set((float) ((getMeasuredWidth() - w) / 2), (float) y, (float) ((getMeasuredWidth() + w) / 2), (float) (AndroidUtilities.dp(4.0f) + y));
            Theme.dialogs_onlineCirclePaint.setColor(Theme.getColor(UsersAlertBase.this.keyScrollUp));
            canvas.drawRoundRect(UsersAlertBase.this.rect, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
            if (statusBarHeight > 0) {
                Theme.dialogs_onlineCirclePaint.setColor(Color.argb(255, (int) (((float) Color.red(UsersAlertBase.this.backgroundColor)) * 0.8f), (int) (((float) Color.green(UsersAlertBase.this.backgroundColor)) * 0.8f), (int) (((float) Color.blue(UsersAlertBase.this.backgroundColor)) * 0.8f)));
                canvas.drawRect((float) UsersAlertBase.this.backgroundPaddingLeft, ((float) (AndroidUtilities.statusBarHeight - statusBarHeight)) - getTranslationY(), (float) (getMeasuredWidth() - UsersAlertBase.this.backgroundPaddingLeft), ((float) AndroidUtilities.statusBarHeight) - getTranslationY(), Theme.dialogs_onlineCirclePaint);
            }
            canvas.restore();
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            canvas.save();
            canvas.clipRect(0, getPaddingTop(), getMeasuredWidth(), getMeasuredHeight());
            super.dispatchDraw(canvas);
            canvas.restore();
        }
    }
}
