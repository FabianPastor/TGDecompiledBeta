package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
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
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.GroupCallTextCell;
import org.telegram.ui.Cells.GroupCallUserCell;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.UsersAlertBase;
/* loaded from: classes3.dex */
public class UsersAlertBase extends BottomSheet {
    private int backgroundColor;
    private float colorProgress;
    protected StickerEmptyView emptyView;
    protected FlickerLoadingView flickerLoadingView;
    protected FrameLayout frameLayout;
    protected boolean isEmptyViewVisible;
    protected String keyActionBarUnscrolled;
    protected String keyInviteMembersBackground;
    protected String keyLastSeenText;
    protected String keyLastSeenTextUnscrolled;
    protected String keyListSelector;
    protected String keyListViewBackground;
    protected String keyNameText;
    protected String keyScrollUp;
    protected String keySearchBackground;
    protected String keySearchIcon;
    protected String keySearchIconUnscrolled;
    protected String keySearchPlaceholder;
    protected String keySearchText;
    protected final FillLastLinearLayoutManager layoutManager;
    protected RecyclerListView listView;
    protected RecyclerView.Adapter listViewAdapter;
    protected boolean needSnapToTop;
    private RectF rect;
    protected int scrollOffsetY;
    protected RecyclerView.Adapter searchListViewAdapter;
    protected SearchField searchView;
    protected View shadow;
    protected AnimatorSet shadowAnimation;
    protected Drawable shadowDrawable;

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithSwipe() {
        return false;
    }

    protected void onSearchViewTouched(MotionEvent motionEvent, EditTextBoldCursor editTextBoldCursor) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void search(String str) {
    }

    protected void updateColorKeys() {
    }

    public UsersAlertBase(Context context, boolean z, int i, Theme.ResourcesProvider resourcesProvider) {
        super(context, z, resourcesProvider);
        this.rect = new RectF();
        this.needSnapToTop = true;
        this.isEmptyViewVisible = true;
        this.keyScrollUp = "key_sheet_scrollUp";
        this.keyListSelector = "listSelectorSDK21";
        this.keySearchBackground = "dialogSearchBackground";
        this.keyInviteMembersBackground = "windowBackgroundWhite";
        this.keyListViewBackground = "windowBackgroundWhite";
        this.keyActionBarUnscrolled = "windowBackgroundWhite";
        this.keyNameText = "windowBackgroundWhiteBlackText";
        this.keyLastSeenText = "windowBackgroundWhiteGrayText";
        this.keyLastSeenTextUnscrolled = "windowBackgroundWhiteGrayText";
        this.keySearchPlaceholder = "dialogSearchHint";
        this.keySearchText = "dialogSearchText";
        this.keySearchIcon = "dialogSearchIcon";
        this.keySearchIconUnscrolled = "dialogSearchIcon";
        updateColorKeys();
        setDimBehindAlpha(75);
        this.currentAccount = i;
        this.shadowDrawable = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
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
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
        this.flickerLoadingView = flickerLoadingView;
        flickerLoadingView.setViewType(6);
        this.flickerLoadingView.showDate(false);
        this.flickerLoadingView.setUseHeaderOffset(true);
        this.flickerLoadingView.setColors(this.keyInviteMembersBackground, this.keySearchBackground, this.keyActionBarUnscrolled);
        StickerEmptyView stickerEmptyView = new StickerEmptyView(context, this.flickerLoadingView, 1);
        this.emptyView = stickerEmptyView;
        stickerEmptyView.addView(this.flickerLoadingView, 0, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 2.0f, 0.0f, 0.0f));
        this.emptyView.title.setText(LocaleController.getString("NoResult", R.string.NoResult));
        this.emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitle2", R.string.SearchEmptyViewFilteredSubtitle2));
        this.emptyView.setVisibility(8);
        this.emptyView.setAnimateLayoutChange(true);
        this.emptyView.showProgress(true, false);
        this.emptyView.setColors(this.keyNameText, this.keyLastSeenText, this.keyInviteMembersBackground, this.keySearchBackground);
        this.containerView.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 62.0f, 0.0f, 0.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.Components.UsersAlertBase.1
            @Override // org.telegram.ui.Components.RecyclerListView
            protected boolean allowSelectChildAtPosition(float f, float f2) {
                return UsersAlertBase.this.isAllowSelectChildAtPosition(f, f2);
            }

            @Override // org.telegram.ui.Components.RecyclerListView, android.view.View
            public void setTranslationY(float f) {
                super.setTranslationY(f);
                getLocationInWindow(new int[2]);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.Components.RecyclerListView
            public boolean emptyViewIsVisible() {
                return getAdapter() != null && UsersAlertBase.this.isEmptyViewVisible && getAdapter().getItemCount() <= 2;
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setTag(13);
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
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.UsersAlertBase.2
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int i3, int i4) {
                UsersAlertBase.this.updateLayout();
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int i3) {
                RecyclerListView.Holder holder;
                if (i3 == 0) {
                    UsersAlertBase usersAlertBase = UsersAlertBase.this;
                    if (!usersAlertBase.needSnapToTop || usersAlertBase.scrollOffsetY + ((BottomSheet) usersAlertBase).backgroundPaddingTop + AndroidUtilities.dp(13.0f) >= AndroidUtilities.statusBarHeight * 2 || !UsersAlertBase.this.listView.canScrollVertically(1) || (holder = (RecyclerListView.Holder) UsersAlertBase.this.listView.findViewHolderForAdapterPosition(0)) == null || holder.itemView.getTop() <= 0) {
                        return;
                    }
                    UsersAlertBase.this.listView.smoothScrollBy(0, holder.itemView.getTop());
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

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        AndroidUtilities.statusBarHeight = AndroidUtilities.getStatusBarHeight(getContext());
    }

    protected ContainerView createContainerView(Context context) {
        return new ContainerView(context);
    }

    protected boolean isAllowSelectChildAtPosition(float f, float f2) {
        return f2 >= ((float) (AndroidUtilities.dp(58.0f) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes3.dex */
    public class SearchField extends FrameLayout {
        private final ImageView clearSearchImageView;
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
            imageView.setImageResource(R.drawable.smiles_inputsearch);
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(UsersAlertBase.this.keySearchPlaceholder), PorterDuff.Mode.MULTIPLY));
            addView(imageView, LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 11.0f, 0.0f, 0.0f));
            ImageView imageView2 = new ImageView(context);
            this.clearSearchImageView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2(UsersAlertBase.this) { // from class: org.telegram.ui.Components.UsersAlertBase.SearchField.1
                @Override // org.telegram.ui.Components.CloseProgressDrawable2
                protected int getCurrentColor() {
                    return Theme.getColor(UsersAlertBase.this.keySearchPlaceholder);
                }
            };
            this.progressDrawable = closeProgressDrawable2;
            imageView2.setImageDrawable(closeProgressDrawable2);
            closeProgressDrawable2.setSide(AndroidUtilities.dp(7.0f));
            imageView2.setScaleX(0.1f);
            imageView2.setScaleY(0.1f);
            imageView2.setAlpha(0.0f);
            addView(imageView2, LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 11.0f, 14.0f, 0.0f));
            imageView2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.UsersAlertBase$SearchField$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    UsersAlertBase.SearchField.this.lambda$new$0(view2);
                }
            });
            EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context, UsersAlertBase.this) { // from class: org.telegram.ui.Components.UsersAlertBase.SearchField.2
                @Override // org.telegram.ui.Components.EditTextEffects, android.view.View
                public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                    MotionEvent obtain = MotionEvent.obtain(motionEvent);
                    obtain.setLocation(obtain.getRawX(), obtain.getRawY() - ((BottomSheet) UsersAlertBase.this).containerView.getTranslationY());
                    if (obtain.getAction() == 1) {
                        obtain.setAction(3);
                    }
                    UsersAlertBase.this.listView.dispatchTouchEvent(obtain);
                    obtain.recycle();
                    return super.dispatchTouchEvent(motionEvent);
                }
            };
            this.searchEditText = editTextBoldCursor;
            editTextBoldCursor.setTextSize(1, 16.0f);
            this.searchEditText.setHintTextColor(Theme.getColor(UsersAlertBase.this.keySearchPlaceholder));
            this.searchEditText.setTextColor(Theme.getColor(UsersAlertBase.this.keySearchText));
            this.searchEditText.setBackgroundDrawable(null);
            this.searchEditText.setPadding(0, 0, 0, 0);
            this.searchEditText.setMaxLines(1);
            this.searchEditText.setLines(1);
            this.searchEditText.setSingleLine(true);
            this.searchEditText.setImeOptions(NUM);
            this.searchEditText.setHint(LocaleController.getString("VoipGroupSearchMembers", R.string.VoipGroupSearchMembers));
            this.searchEditText.setCursorColor(Theme.getColor(UsersAlertBase.this.keySearchText));
            this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
            this.searchEditText.setCursorWidth(1.5f);
            addView(this.searchEditText, LayoutHelper.createFrame(-1, 40.0f, 51, 54.0f, 9.0f, 46.0f, 0.0f));
            this.searchEditText.addTextChangedListener(new TextWatcher(UsersAlertBase.this) { // from class: org.telegram.ui.Components.UsersAlertBase.SearchField.3
                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override // android.text.TextWatcher
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
                        ViewPropertyAnimator scaleX = animate.alpha(f).setDuration(150L).scaleX(z ? 1.0f : 0.1f);
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
            this.searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.Components.UsersAlertBase$SearchField$$ExternalSyntheticLambda1
                @Override // android.widget.TextView.OnEditorActionListener
                public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    boolean lambda$new$1;
                    lambda$new$1 = UsersAlertBase.SearchField.this.lambda$new$1(textView, i, keyEvent);
                    return lambda$new$1;
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            this.searchEditText.setText("");
            AndroidUtilities.showKeyboard(this.searchEditText);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ boolean lambda$new$1(TextView textView, int i, KeyEvent keyEvent) {
            if (keyEvent != null) {
                if ((keyEvent.getAction() != 1 || keyEvent.getKeyCode() != 84) && (keyEvent.getAction() != 0 || keyEvent.getKeyCode() != 66)) {
                    return false;
                }
                AndroidUtilities.hideKeyboard(this.searchEditText);
                return false;
            }
            return false;
        }

        @Override // android.view.ViewGroup
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
        new AnimationProperties.FloatProperty<UsersAlertBase>("colorProgress") { // from class: org.telegram.ui.Components.UsersAlertBase.3
            @Override // org.telegram.ui.Components.AnimationProperties.FloatProperty
            public void setValue(UsersAlertBase usersAlertBase, float f) {
                usersAlertBase.setColorProgress(f);
            }

            @Override // android.util.Property
            public Float get(UsersAlertBase usersAlertBase) {
                return Float.valueOf(usersAlertBase.getColorProgress());
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getColorProgress() {
        return this.colorProgress;
    }

    /* JADX INFO: Access modifiers changed from: protected */
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

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        AndroidUtilities.hideKeyboard(this.searchView.searchEditText);
        super.dismiss();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SuppressLint({"NewApi"})
    public void updateLayout() {
        if (this.listView.getChildCount() <= 0) {
            return;
        }
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(0);
        int top = findViewHolderForAdapterPosition != null ? findViewHolderForAdapterPosition.itemView.getTop() - AndroidUtilities.dp(8.0f) : 0;
        int i = (top <= 0 || findViewHolderForAdapterPosition == null || findViewHolderForAdapterPosition.getAdapterPosition() != 0) ? 0 : top;
        if (top >= 0 && findViewHolderForAdapterPosition != null && findViewHolderForAdapterPosition.getAdapterPosition() == 0) {
            runShadowAnimation(false);
        } else {
            runShadowAnimation(true);
            top = i;
        }
        if (this.scrollOffsetY == top) {
            return;
        }
        this.scrollOffsetY = top;
        setTranslationY(top);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setTranslationY(int i) {
        this.listView.setTopGlowOffset(i);
        float f = i;
        this.frameLayout.setTranslationY(f);
        this.emptyView.setTranslationY(f);
        this.containerView.invalidate();
    }

    private void runShadowAnimation(final boolean z) {
        if ((!z || this.shadow.getTag() == null) && (z || this.shadow.getTag() != null)) {
            return;
        }
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
        this.shadowAnimation.setDuration(150L);
        this.shadowAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.UsersAlertBase.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                AnimatorSet animatorSet3 = UsersAlertBase.this.shadowAnimation;
                if (animatorSet3 == null || !animatorSet3.equals(animator)) {
                    return;
                }
                if (!z) {
                    UsersAlertBase.this.shadow.setVisibility(4);
                }
                UsersAlertBase.this.shadowAnimation = null;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                AnimatorSet animatorSet3 = UsersAlertBase.this.shadowAnimation;
                if (animatorSet3 == null || !animatorSet3.equals(animator)) {
                    return;
                }
                UsersAlertBase.this.shadowAnimation = null;
            }
        });
        this.shadowAnimation.start();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void showItemsAnimated(final int i) {
        if (!isShowing()) {
            return;
        }
        this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.Components.UsersAlertBase.5
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                UsersAlertBase.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                int childCount = UsersAlertBase.this.listView.getChildCount();
                AnimatorSet animatorSet = new AnimatorSet();
                for (int i2 = 0; i2 < childCount; i2++) {
                    View childAt = UsersAlertBase.this.listView.getChildAt(i2);
                    int childAdapterPosition = UsersAlertBase.this.listView.getChildAdapterPosition(childAt);
                    if (childAdapterPosition >= i) {
                        if (childAdapterPosition == 1 && UsersAlertBase.this.listView.getAdapter() == UsersAlertBase.this.searchListViewAdapter && (childAt instanceof GraySectionCell)) {
                            childAt = ((GraySectionCell) childAt).getTextView();
                        }
                        childAt.setAlpha(0.0f);
                        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(childAt, View.ALPHA, 0.0f, 1.0f);
                        ofFloat.setStartDelay((int) ((Math.min(UsersAlertBase.this.listView.getMeasuredHeight(), Math.max(0, childAt.getTop())) / UsersAlertBase.this.listView.getMeasuredHeight()) * 100.0f));
                        ofFloat.setDuration(200L);
                        animatorSet.playTogether(ofFloat);
                    }
                }
                animatorSet.start();
                return true;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes3.dex */
    public class ContainerView extends FrameLayout {
        private boolean ignoreLayout;
        float snapToTopOffset;
        ValueAnimator valueAnimator;

        public ContainerView(Context context) {
            super(context);
            this.ignoreLayout = false;
        }

        @Override // android.view.View
        public void setTranslationY(float f) {
            super.setTranslationY(f);
            invalidate();
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            int dp;
            int size = View.MeasureSpec.getSize(i2);
            if (Build.VERSION.SDK_INT >= 21) {
                this.ignoreLayout = true;
                setPadding(((BottomSheet) UsersAlertBase.this).backgroundPaddingLeft, AndroidUtilities.statusBarHeight, ((BottomSheet) UsersAlertBase.this).backgroundPaddingLeft, 0);
                this.ignoreLayout = false;
            }
            int paddingTop = size - getPaddingTop();
            if (((BottomSheet) UsersAlertBase.this).keyboardVisible) {
                dp = AndroidUtilities.dp(8.0f);
                UsersAlertBase.this.setAllowNestedScroll(false);
                int i3 = UsersAlertBase.this.scrollOffsetY;
                if (i3 != 0) {
                    float f = i3;
                    this.snapToTopOffset = f;
                    setTranslationY(f);
                    ValueAnimator valueAnimator = this.valueAnimator;
                    if (valueAnimator != null) {
                        valueAnimator.removeAllListeners();
                        this.valueAnimator.cancel();
                    }
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(this.snapToTopOffset, 0.0f);
                    this.valueAnimator = ofFloat;
                    ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.UsersAlertBase$ContainerView$$ExternalSyntheticLambda0
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                            UsersAlertBase.ContainerView.this.lambda$onMeasure$0(valueAnimator2);
                        }
                    });
                    this.valueAnimator.setDuration(250L);
                    this.valueAnimator.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                    this.valueAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.UsersAlertBase.ContainerView.1
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
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
                dp = (paddingTop - ((paddingTop / 5) * 3)) + AndroidUtilities.dp(8.0f);
                UsersAlertBase.this.setAllowNestedScroll(true);
            }
            if (UsersAlertBase.this.listView.getPaddingTop() != dp) {
                this.ignoreLayout = true;
                UsersAlertBase.this.listView.setPadding(0, dp, 0, 0);
                this.ignoreLayout = false;
            }
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(size, NUM));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onMeasure$0(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.snapToTopOffset = floatValue;
            setTranslationY(floatValue);
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            UsersAlertBase.this.updateLayout();
        }

        @Override // android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0) {
                float y = motionEvent.getY();
                UsersAlertBase usersAlertBase = UsersAlertBase.this;
                if (y < usersAlertBase.scrollOffsetY) {
                    usersAlertBase.dismiss();
                    return true;
                }
            }
            return super.onInterceptTouchEvent(motionEvent);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            return !UsersAlertBase.this.isDismissed() && super.onTouchEvent(motionEvent);
        }

        @Override // android.view.View, android.view.ViewParent
        public void requestLayout() {
            if (this.ignoreLayout) {
                return;
            }
            super.requestLayout();
        }

        /* JADX WARN: Removed duplicated region for block: B:15:0x00c3  */
        /* JADX WARN: Removed duplicated region for block: B:18:0x016a  */
        @Override // android.view.View
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        protected void onDraw(android.graphics.Canvas r14) {
            /*
                Method dump skipped, instructions count: 466
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.UsersAlertBase.ContainerView.onDraw(android.graphics.Canvas):void");
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.ViewGroup, android.view.View
        public void dispatchDraw(Canvas canvas) {
            canvas.save();
            canvas.clipRect(0, getPaddingTop(), getMeasuredWidth(), getMeasuredHeight());
            super.dispatchDraw(canvas);
            canvas.restore();
        }
    }
}
