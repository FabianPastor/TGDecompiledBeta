package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
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
import android.view.ViewGroup;
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
import org.telegram.ui.Components.RecyclerListView;

public class UsersAlertBase extends BottomSheet {
    /* access modifiers changed from: private */
    public int backgroundColor;
    private float colorProgress;
    protected StickerEmptyView emptyView;
    protected FlickerLoadingView flickerLoadingView;
    protected FrameLayout frameLayout;
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
    protected RecyclerListView.SelectionAdapter listViewAdapter;
    protected boolean needSnapToTop = true;
    /* access modifiers changed from: private */
    public RectF rect = new RectF();
    protected int scrollOffsetY;
    protected RecyclerListView.SelectionAdapter searchListViewAdapter;
    protected SearchField searchView;
    protected View shadow;
    protected AnimatorSet shadowAnimation;
    protected Drawable shadowDrawable;

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onSearchViewTouched(MotionEvent motionEvent, EditTextBoldCursor editTextBoldCursor) {
    }

    /* access modifiers changed from: protected */
    public void search(String str) {
    }

    /* access modifiers changed from: protected */
    public void updateColorKeys() {
    }

    public UsersAlertBase(Context context, boolean z, int i, Theme.ResourcesProvider resourcesProvider) {
        super(context, z, resourcesProvider);
        updateColorKeys();
        setDimBehindAlpha(75);
        this.currentAccount = i;
        this.shadowDrawable = context.getResources().getDrawable(NUM).mutate();
        ContainerView createContainerView = createContainerView(context);
        this.containerView = createContainerView;
        createContainerView.setWillNotDraw(false);
        this.containerView.setClipChildren(false);
        ViewGroup viewGroup = this.containerView;
        int i2 = this.backgroundPaddingLeft;
        viewGroup.setPadding(i2, 0, i2, 0);
        this.frameLayout = new FrameLayout(context);
        SearchField searchField = new SearchField(context);
        this.searchView = searchField;
        this.frameLayout.addView(searchField, LayoutHelper.createFrame(-1, -1, 51));
        FlickerLoadingView flickerLoadingView2 = new FlickerLoadingView(context);
        this.flickerLoadingView = flickerLoadingView2;
        flickerLoadingView2.setViewType(6);
        this.flickerLoadingView.showDate(false);
        this.flickerLoadingView.setUseHeaderOffset(true);
        this.flickerLoadingView.setColors(this.keyInviteMembersBackground, this.keySearchBackground, this.keyActionBarUnscrolled);
        StickerEmptyView stickerEmptyView = new StickerEmptyView(context, this.flickerLoadingView, 1);
        this.emptyView = stickerEmptyView;
        stickerEmptyView.addView(this.flickerLoadingView, 0, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 2.0f, 0.0f, 0.0f));
        this.emptyView.title.setText(LocaleController.getString("NoResult", NUM));
        this.emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitle2", NUM));
        this.emptyView.setVisibility(8);
        this.emptyView.setAnimateLayoutChange(true);
        this.emptyView.showProgress(true, false);
        this.emptyView.setColors(this.keyNameText, this.keyLastSeenText, this.keyInviteMembersBackground, this.keySearchBackground);
        this.containerView.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 62.0f, 0.0f, 0.0f));
        AnonymousClass1 r13 = new RecyclerListView(this, context) {
            /* access modifiers changed from: protected */
            public boolean allowSelectChildAtPosition(float f, float f2) {
                return f2 >= ((float) (AndroidUtilities.dp(58.0f) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
            }

            public void setTranslationY(float f) {
                super.setTranslationY(f);
                getLocationInWindow(new int[2]);
            }

            /* access modifiers changed from: protected */
            public boolean emptyViewIsVisible() {
                if (getAdapter() != null && getAdapter().getItemCount() <= 2) {
                    return true;
                }
                return false;
            }
        };
        this.listView = r13;
        r13.setTag(13);
        this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
        this.listView.setClipToPadding(false);
        this.listView.setHideIfEmpty(false);
        this.listView.setSelectorDrawableColor(Theme.getColor(this.keyListSelector));
        FillLastLinearLayoutManager fillLastLinearLayoutManager = new FillLastLinearLayoutManager(getContext(), 1, false, AndroidUtilities.dp(8.0f), this.listView);
        this.layoutManager = fillLastLinearLayoutManager;
        fillLastLinearLayoutManager.setBind(false);
        this.listView.setLayoutManager(fillLastLinearLayoutManager);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                UsersAlertBase.this.updateLayout();
            }

            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                RecyclerListView.Holder holder;
                if (i == 0) {
                    UsersAlertBase usersAlertBase = UsersAlertBase.this;
                    if (usersAlertBase.needSnapToTop && usersAlertBase.scrollOffsetY + usersAlertBase.backgroundPaddingTop + AndroidUtilities.dp(13.0f) < AndroidUtilities.statusBarHeight * 2 && UsersAlertBase.this.listView.canScrollVertically(1) && (holder = (RecyclerListView.Holder) UsersAlertBase.this.listView.findViewHolderForAdapterPosition(0)) != null && holder.itemView.getTop() > 0) {
                        UsersAlertBase.this.listView.smoothScrollBy(0, holder.itemView.getTop());
                    }
                }
            }
        });
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        layoutParams.topMargin = AndroidUtilities.dp(58.0f);
        View view = new View(context);
        this.shadow = view;
        view.setBackgroundColor(Theme.getColor("dialogShadowLine"));
        this.shadow.setAlpha(0.0f);
        this.shadow.setTag(1);
        this.containerView.addView(this.shadow, layoutParams);
        this.containerView.addView(this.frameLayout, LayoutHelper.createFrame(-1, 58, 51));
        setColorProgress(0.0f);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setAnimateEmptyView(true, 0);
    }

    /* access modifiers changed from: protected */
    public ContainerView createContainerView(Context context) {
        return new ContainerView(context);
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
            CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2();
            this.progressDrawable = closeProgressDrawable2;
            imageView2.setImageDrawable(closeProgressDrawable2);
            closeProgressDrawable2.setSide(AndroidUtilities.dp(7.0f));
            imageView2.setScaleX(0.1f);
            imageView2.setScaleY(0.1f);
            imageView2.setAlpha(0.0f);
            imageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(UsersAlertBase.this.keySearchPlaceholder), PorterDuff.Mode.MULTIPLY));
            addView(imageView2, LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 11.0f, 14.0f, 0.0f));
            imageView2.setOnClickListener(new UsersAlertBase$SearchField$$ExternalSyntheticLambda0(this));
            AnonymousClass1 r0 = new EditTextBoldCursor(context, UsersAlertBase.this) {
                public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                    MotionEvent obtain = MotionEvent.obtain(motionEvent);
                    obtain.setLocation(obtain.getRawX(), obtain.getRawY() - UsersAlertBase.this.containerView.getTranslationY());
                    if (obtain.getAction() == 1) {
                        obtain.setAction(3);
                    }
                    UsersAlertBase.this.listView.dispatchTouchEvent(obtain);
                    obtain.recycle();
                    return super.dispatchTouchEvent(motionEvent);
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
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
                    RecyclerListView recyclerListView;
                    boolean z = SearchField.this.searchEditText.length() > 0;
                    float f = 0.0f;
                    if (z != (SearchField.this.clearSearchImageView.getAlpha() != 0.0f)) {
                        ViewPropertyAnimator animate = SearchField.this.clearSearchImageView.animate();
                        float f2 = 1.0f;
                        if (z) {
                            f = 1.0f;
                        }
                        ViewPropertyAnimator scaleX = animate.alpha(f).setDuration(150).scaleX(z ? 1.0f : 0.1f);
                        if (!z) {
                            f2 = 0.1f;
                        }
                        scaleX.scaleY(f2).start();
                    }
                    String obj = SearchField.this.searchEditText.getText().toString();
                    int itemCount = UsersAlertBase.this.listView.getAdapter() == null ? 0 : UsersAlertBase.this.listView.getAdapter().getItemCount();
                    UsersAlertBase.this.search(obj);
                    if (TextUtils.isEmpty(obj) && (recyclerListView = UsersAlertBase.this.listView) != null) {
                        RecyclerView.Adapter adapter = recyclerListView.getAdapter();
                        UsersAlertBase usersAlertBase = UsersAlertBase.this;
                        if (adapter != usersAlertBase.listViewAdapter) {
                            usersAlertBase.listView.setAnimateEmptyView(false, 0);
                            UsersAlertBase usersAlertBase2 = UsersAlertBase.this;
                            usersAlertBase2.listView.setAdapter(usersAlertBase2.listViewAdapter);
                            UsersAlertBase.this.listView.setAnimateEmptyView(true, 0);
                            if (itemCount == 0) {
                                UsersAlertBase.this.showItemsAnimated(0);
                            }
                        }
                    }
                    UsersAlertBase.this.flickerLoadingView.setVisibility(0);
                }
            });
            this.searchEditText.setOnEditorActionListener(new UsersAlertBase$SearchField$$ExternalSyntheticLambda1(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            this.searchEditText.setText("");
            AndroidUtilities.showKeyboard(this.searchEditText);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$new$1(TextView textView, int i, KeyEvent keyEvent) {
            if (keyEvent == null) {
                return false;
            }
            if ((keyEvent.getAction() != 1 || keyEvent.getKeyCode() != 84) && (keyEvent.getAction() != 0 || keyEvent.getKeyCode() != 66)) {
                return false;
            }
            AndroidUtilities.hideKeyboard(this.searchEditText);
            return false;
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            UsersAlertBase.this.onSearchViewTouched(motionEvent, this.searchEditText);
            return super.onInterceptTouchEvent(motionEvent);
        }

        public void closeSearch() {
            this.clearSearchImageView.callOnClick();
            AndroidUtilities.hideKeyboard(this.searchEditText);
        }
    }

    static {
        new AnimationProperties.FloatProperty<UsersAlertBase>("colorProgress") {
            public void setValue(UsersAlertBase usersAlertBase, float f) {
                usersAlertBase.setColorProgress(f);
            }

            public Float get(UsersAlertBase usersAlertBase) {
                return Float.valueOf(usersAlertBase.getColorProgress());
            }
        };
    }

    /* access modifiers changed from: private */
    public float getColorProgress() {
        return this.colorProgress;
    }

    /* access modifiers changed from: protected */
    public void setColorProgress(float f) {
        this.colorProgress = f;
        this.backgroundColor = AndroidUtilities.getOffsetColor(Theme.getColor(this.keyInviteMembersBackground), Theme.getColor(this.keyListViewBackground), f, 1.0f);
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(this.backgroundColor, PorterDuff.Mode.MULTIPLY));
        this.frameLayout.setBackgroundColor(this.backgroundColor);
        int i = this.backgroundColor;
        this.navBarColor = i;
        this.listView.setGlowColor(i);
        int offsetColor = AndroidUtilities.getOffsetColor(Theme.getColor(this.keyLastSeenTextUnscrolled), Theme.getColor(this.keyLastSeenText), f, 1.0f);
        int offsetColor2 = AndroidUtilities.getOffsetColor(Theme.getColor(this.keySearchIconUnscrolled), Theme.getColor(this.keySearchIcon), f, 1.0f);
        int childCount = this.listView.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = this.listView.getChildAt(i2);
            if (childAt instanceof GroupCallTextCell) {
                ((GroupCallTextCell) childAt).setColors(offsetColor, offsetColor);
            } else if (childAt instanceof GroupCallUserCell) {
                ((GroupCallUserCell) childAt).setGrayIconColor(this.shadow.getTag() != null ? this.keySearchIcon : this.keySearchIconUnscrolled, offsetColor2);
            }
        }
        this.containerView.invalidate();
        this.listView.invalidate();
        this.container.invalidate();
    }

    public void dismiss() {
        AndroidUtilities.hideKeyboard(this.searchView.searchEditText);
        super.dismiss();
    }

    /* access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    public void updateLayout() {
        if (this.listView.getChildCount() > 0) {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(0);
            int top = findViewHolderForAdapterPosition != null ? findViewHolderForAdapterPosition.itemView.getTop() - AndroidUtilities.dp(8.0f) : 0;
            int i = (top <= 0 || findViewHolderForAdapterPosition == null || findViewHolderForAdapterPosition.getAdapterPosition() != 0) ? 0 : top;
            if (top < 0 || findViewHolderForAdapterPosition == null || findViewHolderForAdapterPosition.getAdapterPosition() != 0) {
                runShadowAnimation(true);
                top = i;
            } else {
                runShadowAnimation(false);
            }
            if (this.scrollOffsetY != top) {
                RecyclerListView recyclerListView = this.listView;
                this.scrollOffsetY = top;
                recyclerListView.setTopGlowOffset(top);
                this.frameLayout.setTranslationY((float) this.scrollOffsetY);
                this.emptyView.setTranslationY((float) this.scrollOffsetY);
                this.containerView.invalidate();
            }
        }
    }

    private void runShadowAnimation(final boolean z) {
        if ((z && this.shadow.getTag() != null) || (!z && this.shadow.getTag() == null)) {
            this.shadow.setTag(z ? null : 1);
            if (z) {
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
            fArr[0] = z ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet2.playTogether(animatorArr);
            this.shadowAnimation.setDuration(150);
            this.shadowAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AnimatorSet animatorSet = UsersAlertBase.this.shadowAnimation;
                    if (animatorSet != null && animatorSet.equals(animator)) {
                        if (!z) {
                            UsersAlertBase.this.shadow.setVisibility(4);
                        }
                        UsersAlertBase.this.shadowAnimation = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    AnimatorSet animatorSet = UsersAlertBase.this.shadowAnimation;
                    if (animatorSet != null && animatorSet.equals(animator)) {
                        UsersAlertBase.this.shadowAnimation = null;
                    }
                }
            });
            this.shadowAnimation.start();
        }
    }

    /* access modifiers changed from: protected */
    public void showItemsAnimated(final int i) {
        if (isShowing()) {
            this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    UsersAlertBase.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    int childCount = UsersAlertBase.this.listView.getChildCount();
                    AnimatorSet animatorSet = new AnimatorSet();
                    for (int i = 0; i < childCount; i++) {
                        View childAt = UsersAlertBase.this.listView.getChildAt(i);
                        int childAdapterPosition = UsersAlertBase.this.listView.getChildAdapterPosition(childAt);
                        if (childAdapterPosition >= i) {
                            if (childAdapterPosition == 1 && UsersAlertBase.this.listView.getAdapter() == UsersAlertBase.this.searchListViewAdapter && (childAt instanceof GraySectionCell)) {
                                childAt = ((GraySectionCell) childAt).getTextView();
                            }
                            childAt.setAlpha(0.0f);
                            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(childAt, View.ALPHA, new float[]{0.0f, 1.0f});
                            ofFloat.setStartDelay((long) ((int) ((((float) Math.min(UsersAlertBase.this.listView.getMeasuredHeight(), Math.max(0, childAt.getTop()))) / ((float) UsersAlertBase.this.listView.getMeasuredHeight())) * 100.0f)));
                            ofFloat.setDuration(200);
                            animatorSet.playTogether(new Animator[]{ofFloat});
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

        public void setTranslationY(float f) {
            super.setTranslationY(f);
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            int i3;
            int size = View.MeasureSpec.getSize(i2);
            if (Build.VERSION.SDK_INT >= 21) {
                this.ignoreLayout = true;
                setPadding(UsersAlertBase.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, UsersAlertBase.this.backgroundPaddingLeft, 0);
                this.ignoreLayout = false;
            }
            int paddingTop = size - getPaddingTop();
            if (UsersAlertBase.this.keyboardVisible) {
                i3 = AndroidUtilities.dp(8.0f);
                UsersAlertBase.this.setAllowNestedScroll(false);
                int i4 = UsersAlertBase.this.scrollOffsetY;
                if (i4 != 0) {
                    float f = (float) i4;
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
                        public void onAnimationEnd(Animator animator) {
                            super.onAnimationEnd(animator);
                            ContainerView containerView = ContainerView.this;
                            containerView.snapToTopOffset = 0.0f;
                            containerView.setTranslationY(0.0f);
                            ContainerView.this.valueAnimator = null;
                        }
                    });
                    this.valueAnimator.start();
                } else if (this.valueAnimator != null) {
                    setTranslationY(this.snapToTopOffset);
                }
            } else {
                i3 = (paddingTop - ((paddingTop / 5) * 3)) + AndroidUtilities.dp(8.0f);
                UsersAlertBase.this.setAllowNestedScroll(true);
            }
            if (UsersAlertBase.this.listView.getPaddingTop() != i3) {
                this.ignoreLayout = true;
                UsersAlertBase.this.listView.setPadding(0, i3, 0, 0);
                this.ignoreLayout = false;
            }
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(size, NUM));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onMeasure$0(ValueAnimator valueAnimator2) {
            float floatValue = ((Float) valueAnimator2.getAnimatedValue()).floatValue();
            this.snapToTopOffset = floatValue;
            setTranslationY(floatValue);
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            UsersAlertBase.this.updateLayout();
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0) {
                float y = motionEvent.getY();
                UsersAlertBase usersAlertBase = UsersAlertBase.this;
                if (y < ((float) usersAlertBase.scrollOffsetY)) {
                    usersAlertBase.dismiss();
                    return true;
                }
            }
            return super.onInterceptTouchEvent(motionEvent);
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return !UsersAlertBase.this.isDismissed() && super.onTouchEvent(motionEvent);
        }

        public void requestLayout() {
            if (!this.ignoreLayout) {
                super.requestLayout();
            }
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:13:0x00c3  */
        /* JADX WARNING: Removed duplicated region for block: B:16:0x016a  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onDraw(android.graphics.Canvas r14) {
            /*
                r13 = this;
                r14.save()
                org.telegram.ui.Components.UsersAlertBase r0 = org.telegram.ui.Components.UsersAlertBase.this
                int r1 = r0.scrollOffsetY
                int r0 = r0.backgroundPaddingTop
                int r1 = r1 - r0
                r0 = 1086324736(0x40CLASSNAME, float:6.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                int r1 = r1 + r0
                org.telegram.ui.Components.UsersAlertBase r0 = org.telegram.ui.Components.UsersAlertBase.this
                int r2 = r0.scrollOffsetY
                int r0 = r0.backgroundPaddingTop
                int r2 = r2 - r0
                r0 = 1095761920(0x41500000, float:13.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                int r2 = r2 - r0
                int r0 = r13.getMeasuredHeight()
                r3 = 1112014848(0x42480000, float:50.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r0 = r0 + r3
                org.telegram.ui.Components.UsersAlertBase r3 = org.telegram.ui.Components.UsersAlertBase.this
                int r3 = r3.backgroundPaddingTop
                int r0 = r0 + r3
                int r3 = android.os.Build.VERSION.SDK_INT
                r4 = 0
                r5 = 1065353216(0x3var_, float:1.0)
                r6 = 21
                if (r3 < r6) goto L_0x00aa
                int r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                int r2 = r2 + r3
                int r1 = r1 + r3
                int r0 = r0 - r3
                org.telegram.ui.Components.UsersAlertBase r3 = org.telegram.ui.Components.UsersAlertBase.this
                int r3 = r3.backgroundPaddingTop
                int r3 = r3 + r2
                float r3 = (float) r3
                float r6 = r13.getTranslationY()
                float r3 = r3 + r6
                int r6 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                int r7 = r6 * 2
                float r7 = (float) r7
                int r3 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
                if (r3 >= 0) goto L_0x007f
                float r3 = (float) r6
                int r6 = r6 * 2
                int r6 = r6 - r2
                org.telegram.ui.Components.UsersAlertBase r7 = org.telegram.ui.Components.UsersAlertBase.this
                int r7 = r7.backgroundPaddingTop
                int r6 = r6 - r7
                float r6 = (float) r6
                float r7 = r13.getTranslationY()
                float r6 = r6 - r7
                float r3 = java.lang.Math.min(r3, r6)
                int r3 = (int) r3
                int r2 = r2 - r3
                int r0 = r0 + r3
                int r3 = r3 * 2
                float r3 = (float) r3
                int r6 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                float r6 = (float) r6
                float r3 = r3 / r6
                float r3 = java.lang.Math.min(r5, r3)
                float r3 = r5 - r3
                goto L_0x0081
            L_0x007f:
                r3 = 1065353216(0x3var_, float:1.0)
            L_0x0081:
                org.telegram.ui.Components.UsersAlertBase r6 = org.telegram.ui.Components.UsersAlertBase.this
                int r6 = r6.backgroundPaddingTop
                int r6 = r6 + r2
                float r6 = (float) r6
                float r7 = r13.getTranslationY()
                float r6 = r6 + r7
                int r7 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                float r8 = (float) r7
                int r6 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
                if (r6 >= 0) goto L_0x00ac
                float r6 = (float) r7
                int r7 = r7 - r2
                org.telegram.ui.Components.UsersAlertBase r8 = org.telegram.ui.Components.UsersAlertBase.this
                int r8 = r8.backgroundPaddingTop
                int r7 = r7 - r8
                float r7 = (float) r7
                float r8 = r13.getTranslationY()
                float r7 = r7 - r8
                float r6 = java.lang.Math.min(r6, r7)
                int r6 = (int) r6
                goto L_0x00ad
            L_0x00aa:
                r3 = 1065353216(0x3var_, float:1.0)
            L_0x00ac:
                r6 = 0
            L_0x00ad:
                org.telegram.ui.Components.UsersAlertBase r7 = org.telegram.ui.Components.UsersAlertBase.this
                android.graphics.drawable.Drawable r7 = r7.shadowDrawable
                int r8 = r13.getMeasuredWidth()
                r7.setBounds(r4, r2, r8, r0)
                org.telegram.ui.Components.UsersAlertBase r0 = org.telegram.ui.Components.UsersAlertBase.this
                android.graphics.drawable.Drawable r0 = r0.shadowDrawable
                r0.draw(r14)
                int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r0 == 0) goto L_0x011c
                android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                org.telegram.ui.Components.UsersAlertBase r4 = org.telegram.ui.Components.UsersAlertBase.this
                int r4 = r4.backgroundColor
                r0.setColor(r4)
                org.telegram.ui.Components.UsersAlertBase r0 = org.telegram.ui.Components.UsersAlertBase.this
                android.graphics.RectF r0 = r0.rect
                org.telegram.ui.Components.UsersAlertBase r4 = org.telegram.ui.Components.UsersAlertBase.this
                int r4 = r4.backgroundPaddingLeft
                float r4 = (float) r4
                org.telegram.ui.Components.UsersAlertBase r5 = org.telegram.ui.Components.UsersAlertBase.this
                int r5 = r5.backgroundPaddingTop
                int r5 = r5 + r2
                float r5 = (float) r5
                int r7 = r13.getMeasuredWidth()
                org.telegram.ui.Components.UsersAlertBase r8 = org.telegram.ui.Components.UsersAlertBase.this
                int r8 = r8.backgroundPaddingLeft
                int r7 = r7 - r8
                float r7 = (float) r7
                org.telegram.ui.Components.UsersAlertBase r8 = org.telegram.ui.Components.UsersAlertBase.this
                int r8 = r8.backgroundPaddingTop
                int r8 = r8 + r2
                r2 = 1103101952(0x41CLASSNAME, float:24.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r8 = r8 + r2
                float r2 = (float) r8
                r0.set(r4, r5, r7, r2)
                org.telegram.ui.Components.UsersAlertBase r0 = org.telegram.ui.Components.UsersAlertBase.this
                android.graphics.RectF r0 = r0.rect
                r2 = 1094713344(0x41400000, float:12.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r4 = (float) r4
                float r4 = r4 * r3
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r2 = (float) r2
                float r2 = r2 * r3
                android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                r14.drawRoundRect(r0, r4, r2, r3)
            L_0x011c:
                r0 = 1108344832(0x42100000, float:36.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                org.telegram.ui.Components.UsersAlertBase r2 = org.telegram.ui.Components.UsersAlertBase.this
                android.graphics.RectF r2 = r2.rect
                int r3 = r13.getMeasuredWidth()
                int r3 = r3 - r0
                int r3 = r3 / 2
                float r3 = (float) r3
                float r4 = (float) r1
                int r5 = r13.getMeasuredWidth()
                int r5 = r5 + r0
                int r5 = r5 / 2
                float r0 = (float) r5
                r5 = 1082130432(0x40800000, float:4.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r1 = r1 + r5
                float r1 = (float) r1
                r2.set(r3, r4, r0, r1)
                android.graphics.Paint r0 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                org.telegram.ui.Components.UsersAlertBase r1 = org.telegram.ui.Components.UsersAlertBase.this
                java.lang.String r1 = r1.keyScrollUp
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r0.setColor(r1)
                org.telegram.ui.Components.UsersAlertBase r0 = org.telegram.ui.Components.UsersAlertBase.this
                android.graphics.RectF r0 = r0.rect
                r1 = 1073741824(0x40000000, float:2.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
                float r2 = (float) r2
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                float r1 = (float) r1
                android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                r14.drawRoundRect(r0, r2, r1, r3)
                if (r6 <= 0) goto L_0x01ce
                r0 = 255(0xff, float:3.57E-43)
                org.telegram.ui.Components.UsersAlertBase r1 = org.telegram.ui.Components.UsersAlertBase.this
                int r1 = r1.backgroundColor
                int r1 = android.graphics.Color.red(r1)
                float r1 = (float) r1
                r2 = 1061997773(0x3f4ccccd, float:0.8)
                float r1 = r1 * r2
                int r1 = (int) r1
                org.telegram.ui.Components.UsersAlertBase r3 = org.telegram.ui.Components.UsersAlertBase.this
                int r3 = r3.backgroundColor
                int r3 = android.graphics.Color.green(r3)
                float r3 = (float) r3
                float r3 = r3 * r2
                int r3 = (int) r3
                org.telegram.ui.Components.UsersAlertBase r4 = org.telegram.ui.Components.UsersAlertBase.this
                int r4 = r4.backgroundColor
                int r4 = android.graphics.Color.blue(r4)
                float r4 = (float) r4
                float r4 = r4 * r2
                int r2 = (int) r4
                int r0 = android.graphics.Color.argb(r0, r1, r3, r2)
                android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                r1.setColor(r0)
                org.telegram.ui.Components.UsersAlertBase r0 = org.telegram.ui.Components.UsersAlertBase.this
                int r0 = r0.backgroundPaddingLeft
                float r8 = (float) r0
                int r0 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                int r0 = r0 - r6
                float r0 = (float) r0
                float r1 = r13.getTranslationY()
                float r9 = r0 - r1
                int r0 = r13.getMeasuredWidth()
                org.telegram.ui.Components.UsersAlertBase r1 = org.telegram.ui.Components.UsersAlertBase.this
                int r1 = r1.backgroundPaddingLeft
                int r0 = r0 - r1
                float r10 = (float) r0
                int r0 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                float r0 = (float) r0
                float r1 = r13.getTranslationY()
                float r11 = r0 - r1
                android.graphics.Paint r12 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                r7 = r14
                r7.drawRect(r8, r9, r10, r11, r12)
            L_0x01ce:
                r14.restore()
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.UsersAlertBase.ContainerView.onDraw(android.graphics.Canvas):void");
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
