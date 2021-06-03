package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScrollSlidingTextTabStrip;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.PhotoPickerActivity;

public class PhotoPickerSearchActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public static final Interpolator interpolator = $$Lambda$PhotoPickerSearchActivity$qS8BSeEFoanZMKZ555xPSRdbvYs.INSTANCE;
    /* access modifiers changed from: private */
    public boolean animatingForward;
    /* access modifiers changed from: private */
    public boolean backAnimation;
    /* access modifiers changed from: private */
    public Paint backgroundPaint = new Paint();
    /* access modifiers changed from: private */
    public EditTextEmoji commentTextView;
    /* access modifiers changed from: private */
    public PhotoPickerActivity gifsSearch;
    /* access modifiers changed from: private */
    public PhotoPickerActivity imagesSearch;
    /* access modifiers changed from: private */
    public int maximumVelocity;
    /* access modifiers changed from: private */
    public ScrollSlidingTextTabStrip scrollSlidingTextTabStrip;
    /* access modifiers changed from: private */
    public ActionBarMenuItem searchItem;
    /* access modifiers changed from: private */
    public boolean swipeBackEnabled = true;
    /* access modifiers changed from: private */
    public AnimatorSet tabsAnimation;
    /* access modifiers changed from: private */
    public boolean tabsAnimationInProgress;
    /* access modifiers changed from: private */
    public ViewPage[] viewPages = new ViewPage[2];

    static /* synthetic */ float lambda$static$0(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2 * f2 * f2) + 1.0f;
    }

    private static class ViewPage extends FrameLayout {
        /* access modifiers changed from: private */
        public ActionBar actionBar;
        /* access modifiers changed from: private */
        public FrameLayout fragmentView;
        /* access modifiers changed from: private */
        public RecyclerListView listView;
        /* access modifiers changed from: private */
        public BaseFragment parentFragment;
        /* access modifiers changed from: private */
        public int selectedType;

        public ViewPage(Context context) {
            super(context);
        }
    }

    public PhotoPickerSearchActivity(HashMap<Object, Object> hashMap, ArrayList<Object> arrayList, int i, boolean z, ChatActivity chatActivity) {
        this.imagesSearch = new PhotoPickerActivity(0, (MediaController.AlbumEntry) null, hashMap, arrayList, i, z, chatActivity, false);
        this.gifsSearch = new PhotoPickerActivity(1, (MediaController.AlbumEntry) null, hashMap, arrayList, i, z, chatActivity, false);
    }

    public View createView(Context context) {
        View view;
        this.actionBar.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.actionBar.setTitleColor(Theme.getColor("dialogTextBlack"));
        boolean z = false;
        this.actionBar.setItemsColor(Theme.getColor("dialogTextBlack"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("dialogButtonSelector"), false);
        this.actionBar.setBackButtonImage(NUM);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setExtraHeight(AndroidUtilities.dp(44.0f));
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setAddToContainer(false);
        this.actionBar.setClipContent(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    PhotoPickerSearchActivity.this.finishFragment();
                }
            }
        });
        this.hasOwnBackground = true;
        ActionBarMenuItem actionBarMenuItemSearchListener = this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                PhotoPickerSearchActivity.this.imagesSearch.getActionBar().openSearchField("", false);
                PhotoPickerSearchActivity.this.gifsSearch.getActionBar().openSearchField("", false);
                PhotoPickerSearchActivity.this.searchItem.getSearchField().requestFocus();
            }

            public boolean canCollapseSearch() {
                PhotoPickerSearchActivity.this.finishFragment();
                return false;
            }

            public void onTextChanged(EditText editText) {
                PhotoPickerSearchActivity.this.imagesSearch.getActionBar().setSearchFieldText(editText.getText().toString());
                PhotoPickerSearchActivity.this.gifsSearch.getActionBar().setSearchFieldText(editText.getText().toString());
            }

            public void onSearchPressed(EditText editText) {
                PhotoPickerSearchActivity.this.imagesSearch.getActionBar().onSearchPressed();
                PhotoPickerSearchActivity.this.gifsSearch.getActionBar().onSearchPressed();
            }
        });
        this.searchItem = actionBarMenuItemSearchListener;
        actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString("SearchImagesTitle", NUM));
        EditTextBoldCursor searchField = this.searchItem.getSearchField();
        searchField.setTextColor(Theme.getColor("dialogTextBlack"));
        searchField.setCursorColor(Theme.getColor("dialogTextBlack"));
        searchField.setHintTextColor(Theme.getColor("chat_messagePanelHint"));
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip2 = new ScrollSlidingTextTabStrip(context);
        this.scrollSlidingTextTabStrip = scrollSlidingTextTabStrip2;
        scrollSlidingTextTabStrip2.setUseSameWidth(true);
        this.scrollSlidingTextTabStrip.setColors("chat_attachActiveTab", "chat_attachActiveTab", "chat_attachUnactiveTab", "dialogButtonSelector");
        this.actionBar.addView(this.scrollSlidingTextTabStrip, LayoutHelper.createFrame(-1, 44, 83));
        this.scrollSlidingTextTabStrip.setDelegate(new ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate() {
            public /* synthetic */ void onSamePageSelected() {
                ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate.CC.$default$onSamePageSelected(this);
            }

            public void onPageSelected(int i, boolean z) {
                if (PhotoPickerSearchActivity.this.viewPages[0].selectedType != i) {
                    PhotoPickerSearchActivity photoPickerSearchActivity = PhotoPickerSearchActivity.this;
                    boolean unused = photoPickerSearchActivity.swipeBackEnabled = i == photoPickerSearchActivity.scrollSlidingTextTabStrip.getFirstTabId();
                    int unused2 = PhotoPickerSearchActivity.this.viewPages[1].selectedType = i;
                    PhotoPickerSearchActivity.this.viewPages[1].setVisibility(0);
                    PhotoPickerSearchActivity.this.switchToCurrentSelectedMode(true);
                    boolean unused3 = PhotoPickerSearchActivity.this.animatingForward = z;
                    if (i == 0) {
                        PhotoPickerSearchActivity.this.searchItem.setSearchFieldHint(LocaleController.getString("SearchImagesTitle", NUM));
                    } else {
                        PhotoPickerSearchActivity.this.searchItem.setSearchFieldHint(LocaleController.getString("SearchGifsTitle", NUM));
                    }
                }
            }

            public void onPageScrolled(float f) {
                if (f != 1.0f || PhotoPickerSearchActivity.this.viewPages[1].getVisibility() == 0) {
                    if (PhotoPickerSearchActivity.this.animatingForward) {
                        PhotoPickerSearchActivity.this.viewPages[0].setTranslationX((-f) * ((float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()));
                        PhotoPickerSearchActivity.this.viewPages[1].setTranslationX(((float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()) - (((float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()) * f));
                    } else {
                        PhotoPickerSearchActivity.this.viewPages[0].setTranslationX(((float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()) * f);
                        PhotoPickerSearchActivity.this.viewPages[1].setTranslationX((((float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()) * f) - ((float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()));
                    }
                    if (f == 1.0f) {
                        ViewPage viewPage = PhotoPickerSearchActivity.this.viewPages[0];
                        PhotoPickerSearchActivity.this.viewPages[0] = PhotoPickerSearchActivity.this.viewPages[1];
                        PhotoPickerSearchActivity.this.viewPages[1] = viewPage;
                        PhotoPickerSearchActivity.this.viewPages[1].setVisibility(8);
                    }
                }
            }
        });
        this.maximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        AnonymousClass4 r0 = new SizeNotifierFrameLayout(context) {
            private boolean globalIgnoreLayout;
            /* access modifiers changed from: private */
            public boolean maybeStartTracking;
            /* access modifiers changed from: private */
            public boolean startedTracking;
            private int startedTrackingPointerId;
            private int startedTrackingX;
            private int startedTrackingY;
            private VelocityTracker velocityTracker;

            private boolean prepareForMoving(MotionEvent motionEvent, boolean z) {
                int nextPageId = PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.getNextPageId(z);
                if (nextPageId < 0) {
                    return false;
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                this.maybeStartTracking = false;
                this.startedTracking = true;
                this.startedTrackingX = (int) motionEvent.getX();
                PhotoPickerSearchActivity.this.actionBar.setEnabled(false);
                PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.setEnabled(false);
                int unused = PhotoPickerSearchActivity.this.viewPages[1].selectedType = nextPageId;
                PhotoPickerSearchActivity.this.viewPages[1].setVisibility(0);
                boolean unused2 = PhotoPickerSearchActivity.this.animatingForward = z;
                PhotoPickerSearchActivity.this.switchToCurrentSelectedMode(true);
                if (z) {
                    PhotoPickerSearchActivity.this.viewPages[1].setTranslationX((float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth());
                } else {
                    PhotoPickerSearchActivity.this.viewPages[1].setTranslationX((float) (-PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()));
                }
                return true;
            }

            public void forceHasOverlappingRendering(boolean z) {
                super.forceHasOverlappingRendering(z);
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int size = View.MeasureSpec.getSize(i);
                int size2 = View.MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                measureChildWithMargins(PhotoPickerSearchActivity.this.actionBar, i, 0, i2, 0);
                if ((SharedConfig.smoothKeyboard ? 0 : measureKeyboardHeight()) > AndroidUtilities.dp(20.0f)) {
                    this.globalIgnoreLayout = true;
                    PhotoPickerSearchActivity.this.commentTextView.hideEmojiView();
                    this.globalIgnoreLayout = false;
                } else if (!AndroidUtilities.isInMultiwindow) {
                    size2 -= PhotoPickerSearchActivity.this.commentTextView.getEmojiPadding();
                    i2 = View.MeasureSpec.makeMeasureSpec(size2, NUM);
                }
                int measuredHeight = PhotoPickerSearchActivity.this.actionBar.getMeasuredHeight();
                this.globalIgnoreLayout = true;
                for (int i3 = 0; i3 < PhotoPickerSearchActivity.this.viewPages.length; i3++) {
                    if (!(PhotoPickerSearchActivity.this.viewPages[i3] == null || PhotoPickerSearchActivity.this.viewPages[i3].listView == null)) {
                        PhotoPickerSearchActivity.this.viewPages[i3].listView.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f) + measuredHeight, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f));
                    }
                }
                this.globalIgnoreLayout = false;
                int childCount = getChildCount();
                for (int i4 = 0; i4 < childCount; i4++) {
                    View childAt = getChildAt(i4);
                    if (!(childAt == null || childAt.getVisibility() == 8 || childAt == PhotoPickerSearchActivity.this.actionBar)) {
                        if (PhotoPickerSearchActivity.this.commentTextView == null || !PhotoPickerSearchActivity.this.commentTextView.isPopupView(childAt)) {
                            measureChildWithMargins(childAt, i, 0, i2, 0);
                        } else if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                            childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, NUM));
                        } else if (AndroidUtilities.isTablet()) {
                            childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (size2 - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                        } else {
                            childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec((size2 - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                        }
                    }
                }
            }

            /* access modifiers changed from: protected */
            /* JADX WARNING: Removed duplicated region for block: B:29:0x0083  */
            /* JADX WARNING: Removed duplicated region for block: B:36:0x009d  */
            /* JADX WARNING: Removed duplicated region for block: B:44:0x00c4  */
            /* JADX WARNING: Removed duplicated region for block: B:45:0x00cd  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onLayout(boolean r11, int r12, int r13, int r14, int r15) {
                /*
                    r10 = this;
                    int r11 = r10.getChildCount()
                    boolean r0 = org.telegram.messenger.SharedConfig.smoothKeyboard
                    r1 = 0
                    if (r0 == 0) goto L_0x000b
                    r0 = 0
                    goto L_0x000f
                L_0x000b:
                    int r0 = r10.measureKeyboardHeight()
                L_0x000f:
                    r2 = 1101004800(0x41a00000, float:20.0)
                    int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                    if (r0 > r2) goto L_0x002c
                    boolean r2 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
                    if (r2 != 0) goto L_0x002c
                    boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
                    if (r2 != 0) goto L_0x002c
                    org.telegram.ui.PhotoPickerSearchActivity r2 = org.telegram.ui.PhotoPickerSearchActivity.this
                    org.telegram.ui.Components.EditTextEmoji r2 = r2.commentTextView
                    int r2 = r2.getEmojiPadding()
                    goto L_0x002d
                L_0x002c:
                    r2 = 0
                L_0x002d:
                    r10.setBottomClip(r2)
                L_0x0030:
                    if (r1 >= r11) goto L_0x00e0
                    android.view.View r3 = r10.getChildAt(r1)
                    int r4 = r3.getVisibility()
                    r5 = 8
                    if (r4 != r5) goto L_0x0040
                    goto L_0x00dc
                L_0x0040:
                    android.view.ViewGroup$LayoutParams r4 = r3.getLayoutParams()
                    android.widget.FrameLayout$LayoutParams r4 = (android.widget.FrameLayout.LayoutParams) r4
                    int r5 = r3.getMeasuredWidth()
                    int r6 = r3.getMeasuredHeight()
                    int r7 = r4.gravity
                    r8 = -1
                    if (r7 != r8) goto L_0x0055
                    r7 = 51
                L_0x0055:
                    r8 = r7 & 7
                    r7 = r7 & 112(0x70, float:1.57E-43)
                    r8 = r8 & 7
                    r9 = 1
                    if (r8 == r9) goto L_0x0074
                    r9 = 5
                    if (r8 == r9) goto L_0x0069
                    int r8 = r4.leftMargin
                    int r9 = r10.getPaddingLeft()
                    int r8 = r8 + r9
                    goto L_0x007f
                L_0x0069:
                    int r8 = r14 - r12
                    int r8 = r8 - r5
                    int r9 = r4.rightMargin
                    int r8 = r8 - r9
                    int r9 = r10.getPaddingRight()
                    goto L_0x007e
                L_0x0074:
                    int r8 = r14 - r12
                    int r8 = r8 - r5
                    int r8 = r8 / 2
                    int r9 = r4.leftMargin
                    int r8 = r8 + r9
                    int r9 = r4.rightMargin
                L_0x007e:
                    int r8 = r8 - r9
                L_0x007f:
                    r9 = 16
                    if (r7 == r9) goto L_0x009d
                    r9 = 48
                    if (r7 == r9) goto L_0x0095
                    r9 = 80
                    if (r7 == r9) goto L_0x008e
                    int r4 = r4.topMargin
                    goto L_0x00aa
                L_0x008e:
                    int r7 = r15 - r2
                    int r7 = r7 - r13
                    int r7 = r7 - r6
                    int r4 = r4.bottomMargin
                    goto L_0x00a8
                L_0x0095:
                    int r4 = r4.topMargin
                    int r7 = r10.getPaddingTop()
                    int r4 = r4 + r7
                    goto L_0x00aa
                L_0x009d:
                    int r7 = r15 - r2
                    int r7 = r7 - r13
                    int r7 = r7 - r6
                    int r7 = r7 / 2
                    int r9 = r4.topMargin
                    int r7 = r7 + r9
                    int r4 = r4.bottomMargin
                L_0x00a8:
                    int r4 = r7 - r4
                L_0x00aa:
                    org.telegram.ui.PhotoPickerSearchActivity r7 = org.telegram.ui.PhotoPickerSearchActivity.this
                    org.telegram.ui.Components.EditTextEmoji r7 = r7.commentTextView
                    if (r7 == 0) goto L_0x00d7
                    org.telegram.ui.PhotoPickerSearchActivity r7 = org.telegram.ui.PhotoPickerSearchActivity.this
                    org.telegram.ui.Components.EditTextEmoji r7 = r7.commentTextView
                    boolean r7 = r7.isPopupView(r3)
                    if (r7 == 0) goto L_0x00d7
                    boolean r4 = org.telegram.messenger.AndroidUtilities.isTablet()
                    if (r4 == 0) goto L_0x00cd
                    int r4 = r10.getMeasuredHeight()
                    int r7 = r3.getMeasuredHeight()
                    goto L_0x00d6
                L_0x00cd:
                    int r4 = r10.getMeasuredHeight()
                    int r4 = r4 + r0
                    int r7 = r3.getMeasuredHeight()
                L_0x00d6:
                    int r4 = r4 - r7
                L_0x00d7:
                    int r5 = r5 + r8
                    int r6 = r6 + r4
                    r3.layout(r8, r4, r5, r6)
                L_0x00dc:
                    int r1 = r1 + 1
                    goto L_0x0030
                L_0x00e0:
                    r10.notifyHeightChanged()
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoPickerSearchActivity.AnonymousClass4.onLayout(boolean, int, int, int, int):void");
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (PhotoPickerSearchActivity.this.parentLayout != null) {
                    PhotoPickerSearchActivity.this.parentLayout.drawHeaderShadow(canvas, PhotoPickerSearchActivity.this.actionBar.getMeasuredHeight() + ((int) PhotoPickerSearchActivity.this.actionBar.getTranslationY()));
                }
            }

            public void requestLayout() {
                if (!this.globalIgnoreLayout) {
                    super.requestLayout();
                }
            }

            /* JADX WARNING: Removed duplicated region for block: B:18:0x00a0  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public boolean checkTabsAnimationInProgress() {
                /*
                    r7 = this;
                    org.telegram.ui.PhotoPickerSearchActivity r0 = org.telegram.ui.PhotoPickerSearchActivity.this
                    boolean r0 = r0.tabsAnimationInProgress
                    r1 = 0
                    if (r0 == 0) goto L_0x00c3
                    org.telegram.ui.PhotoPickerSearchActivity r0 = org.telegram.ui.PhotoPickerSearchActivity.this
                    boolean r0 = r0.backAnimation
                    r2 = -1
                    r3 = 0
                    r4 = 1065353216(0x3var_, float:1.0)
                    r5 = 1
                    if (r0 == 0) goto L_0x0059
                    org.telegram.ui.PhotoPickerSearchActivity r0 = org.telegram.ui.PhotoPickerSearchActivity.this
                    org.telegram.ui.PhotoPickerSearchActivity$ViewPage[] r0 = r0.viewPages
                    r0 = r0[r1]
                    float r0 = r0.getTranslationX()
                    float r0 = java.lang.Math.abs(r0)
                    int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
                    if (r0 >= 0) goto L_0x009d
                    org.telegram.ui.PhotoPickerSearchActivity r0 = org.telegram.ui.PhotoPickerSearchActivity.this
                    org.telegram.ui.PhotoPickerSearchActivity$ViewPage[] r0 = r0.viewPages
                    r0 = r0[r1]
                    r0.setTranslationX(r3)
                    org.telegram.ui.PhotoPickerSearchActivity r0 = org.telegram.ui.PhotoPickerSearchActivity.this
                    org.telegram.ui.PhotoPickerSearchActivity$ViewPage[] r0 = r0.viewPages
                    r0 = r0[r5]
                    org.telegram.ui.PhotoPickerSearchActivity r3 = org.telegram.ui.PhotoPickerSearchActivity.this
                    org.telegram.ui.PhotoPickerSearchActivity$ViewPage[] r3 = r3.viewPages
                    r3 = r3[r1]
                    int r3 = r3.getMeasuredWidth()
                    org.telegram.ui.PhotoPickerSearchActivity r4 = org.telegram.ui.PhotoPickerSearchActivity.this
                    boolean r4 = r4.animatingForward
                    if (r4 == 0) goto L_0x0052
                    r2 = 1
                L_0x0052:
                    int r3 = r3 * r2
                    float r2 = (float) r3
                    r0.setTranslationX(r2)
                    goto L_0x009e
                L_0x0059:
                    org.telegram.ui.PhotoPickerSearchActivity r0 = org.telegram.ui.PhotoPickerSearchActivity.this
                    org.telegram.ui.PhotoPickerSearchActivity$ViewPage[] r0 = r0.viewPages
                    r0 = r0[r5]
                    float r0 = r0.getTranslationX()
                    float r0 = java.lang.Math.abs(r0)
                    int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
                    if (r0 >= 0) goto L_0x009d
                    org.telegram.ui.PhotoPickerSearchActivity r0 = org.telegram.ui.PhotoPickerSearchActivity.this
                    org.telegram.ui.PhotoPickerSearchActivity$ViewPage[] r0 = r0.viewPages
                    r0 = r0[r1]
                    org.telegram.ui.PhotoPickerSearchActivity r4 = org.telegram.ui.PhotoPickerSearchActivity.this
                    org.telegram.ui.PhotoPickerSearchActivity$ViewPage[] r4 = r4.viewPages
                    r4 = r4[r1]
                    int r4 = r4.getMeasuredWidth()
                    org.telegram.ui.PhotoPickerSearchActivity r6 = org.telegram.ui.PhotoPickerSearchActivity.this
                    boolean r6 = r6.animatingForward
                    if (r6 == 0) goto L_0x008a
                    goto L_0x008b
                L_0x008a:
                    r2 = 1
                L_0x008b:
                    int r4 = r4 * r2
                    float r2 = (float) r4
                    r0.setTranslationX(r2)
                    org.telegram.ui.PhotoPickerSearchActivity r0 = org.telegram.ui.PhotoPickerSearchActivity.this
                    org.telegram.ui.PhotoPickerSearchActivity$ViewPage[] r0 = r0.viewPages
                    r0 = r0[r5]
                    r0.setTranslationX(r3)
                    goto L_0x009e
                L_0x009d:
                    r5 = 0
                L_0x009e:
                    if (r5 == 0) goto L_0x00bc
                    org.telegram.ui.PhotoPickerSearchActivity r0 = org.telegram.ui.PhotoPickerSearchActivity.this
                    android.animation.AnimatorSet r0 = r0.tabsAnimation
                    if (r0 == 0) goto L_0x00b7
                    org.telegram.ui.PhotoPickerSearchActivity r0 = org.telegram.ui.PhotoPickerSearchActivity.this
                    android.animation.AnimatorSet r0 = r0.tabsAnimation
                    r0.cancel()
                    org.telegram.ui.PhotoPickerSearchActivity r0 = org.telegram.ui.PhotoPickerSearchActivity.this
                    r2 = 0
                    android.animation.AnimatorSet unused = r0.tabsAnimation = r2
                L_0x00b7:
                    org.telegram.ui.PhotoPickerSearchActivity r0 = org.telegram.ui.PhotoPickerSearchActivity.this
                    boolean unused = r0.tabsAnimationInProgress = r1
                L_0x00bc:
                    org.telegram.ui.PhotoPickerSearchActivity r0 = org.telegram.ui.PhotoPickerSearchActivity.this
                    boolean r0 = r0.tabsAnimationInProgress
                    return r0
                L_0x00c3:
                    return r1
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoPickerSearchActivity.AnonymousClass4.checkTabsAnimationInProgress():boolean");
            }

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return checkTabsAnimationInProgress() || PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.isAnimatingIndicator() || onTouchEvent(motionEvent);
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                PhotoPickerSearchActivity.this.backgroundPaint.setColor(Theme.getColor("windowBackgroundGray"));
                canvas.drawRect(0.0f, ((float) PhotoPickerSearchActivity.this.actionBar.getMeasuredHeight()) + PhotoPickerSearchActivity.this.actionBar.getTranslationY(), (float) getMeasuredWidth(), (float) getMeasuredHeight(), PhotoPickerSearchActivity.this.backgroundPaint);
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                float f;
                float f2;
                float f3;
                int i;
                boolean z = false;
                if (PhotoPickerSearchActivity.this.parentLayout.checkTransitionAnimation() || checkTabsAnimationInProgress()) {
                    return false;
                }
                if (motionEvent != null) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    this.velocityTracker.addMovement(motionEvent);
                }
                if (motionEvent != null && motionEvent.getAction() == 0 && !this.startedTracking && !this.maybeStartTracking) {
                    this.startedTrackingPointerId = motionEvent.getPointerId(0);
                    this.maybeStartTracking = true;
                    this.startedTrackingX = (int) motionEvent.getX();
                    this.startedTrackingY = (int) motionEvent.getY();
                    this.velocityTracker.clear();
                } else if (motionEvent != null && motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
                    int x = (int) (motionEvent.getX() - ((float) this.startedTrackingX));
                    int abs = Math.abs(((int) motionEvent.getY()) - this.startedTrackingY);
                    if (this.startedTracking && ((PhotoPickerSearchActivity.this.animatingForward && x > 0) || (!PhotoPickerSearchActivity.this.animatingForward && x < 0))) {
                        if (!prepareForMoving(motionEvent, x < 0)) {
                            this.maybeStartTracking = true;
                            this.startedTracking = false;
                            PhotoPickerSearchActivity.this.viewPages[0].setTranslationX(0.0f);
                            PhotoPickerSearchActivity.this.viewPages[1].setTranslationX((float) (PhotoPickerSearchActivity.this.animatingForward ? PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth() : -PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()));
                            PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.selectTabWithId(PhotoPickerSearchActivity.this.viewPages[1].selectedType, 0.0f);
                        }
                    }
                    if (this.maybeStartTracking && !this.startedTracking) {
                        if (((float) Math.abs(x)) >= AndroidUtilities.getPixelsInCM(0.3f, true) && Math.abs(x) > abs) {
                            if (x < 0) {
                                z = true;
                            }
                            prepareForMoving(motionEvent, z);
                        }
                    } else if (this.startedTracking) {
                        PhotoPickerSearchActivity.this.viewPages[0].setTranslationX((float) x);
                        if (PhotoPickerSearchActivity.this.animatingForward) {
                            PhotoPickerSearchActivity.this.viewPages[1].setTranslationX((float) (PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth() + x));
                        } else {
                            PhotoPickerSearchActivity.this.viewPages[1].setTranslationX((float) (x - PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()));
                        }
                        PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.selectTabWithId(PhotoPickerSearchActivity.this.viewPages[1].selectedType, ((float) Math.abs(x)) / ((float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()));
                    }
                } else if (motionEvent == null || (motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6))) {
                    this.velocityTracker.computeCurrentVelocity(1000, (float) PhotoPickerSearchActivity.this.maximumVelocity);
                    if (motionEvent == null || motionEvent.getAction() == 3) {
                        f2 = 0.0f;
                        f = 0.0f;
                    } else {
                        f2 = this.velocityTracker.getXVelocity();
                        f = this.velocityTracker.getYVelocity();
                        if (!this.startedTracking && Math.abs(f2) >= 3000.0f && Math.abs(f2) > Math.abs(f)) {
                            prepareForMoving(motionEvent, f2 < 0.0f);
                        }
                    }
                    if (this.startedTracking) {
                        float x2 = PhotoPickerSearchActivity.this.viewPages[0].getX();
                        AnimatorSet unused = PhotoPickerSearchActivity.this.tabsAnimation = new AnimatorSet();
                        boolean unused2 = PhotoPickerSearchActivity.this.backAnimation = Math.abs(x2) < ((float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(f2) < 3500.0f || Math.abs(f2) < Math.abs(f));
                        if (PhotoPickerSearchActivity.this.backAnimation) {
                            f3 = Math.abs(x2);
                            if (PhotoPickerSearchActivity.this.animatingForward) {
                                PhotoPickerSearchActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{(float) PhotoPickerSearchActivity.this.viewPages[1].getMeasuredWidth()})});
                            } else {
                                PhotoPickerSearchActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{(float) (-PhotoPickerSearchActivity.this.viewPages[1].getMeasuredWidth())})});
                            }
                        } else {
                            f3 = ((float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()) - Math.abs(x2);
                            if (PhotoPickerSearchActivity.this.animatingForward) {
                                PhotoPickerSearchActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{(float) (-PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth())}), ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{0.0f})});
                            } else {
                                PhotoPickerSearchActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{(float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()}), ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{0.0f})});
                            }
                        }
                        PhotoPickerSearchActivity.this.tabsAnimation.setInterpolator(PhotoPickerSearchActivity.interpolator);
                        int measuredWidth = getMeasuredWidth();
                        float f4 = (float) (measuredWidth / 2);
                        float distanceInfluenceForSnapDuration = f4 + (AndroidUtilities.distanceInfluenceForSnapDuration(Math.min(1.0f, (f3 * 1.0f) / ((float) measuredWidth))) * f4);
                        float abs2 = Math.abs(f2);
                        if (abs2 > 0.0f) {
                            i = Math.round(Math.abs(distanceInfluenceForSnapDuration / abs2) * 1000.0f) * 4;
                        } else {
                            i = (int) (((f3 / ((float) getMeasuredWidth())) + 1.0f) * 100.0f);
                        }
                        PhotoPickerSearchActivity.this.tabsAnimation.setDuration((long) Math.max(150, Math.min(i, 600)));
                        PhotoPickerSearchActivity.this.tabsAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                AnimatorSet unused = PhotoPickerSearchActivity.this.tabsAnimation = null;
                                if (PhotoPickerSearchActivity.this.backAnimation) {
                                    PhotoPickerSearchActivity.this.viewPages[1].setVisibility(8);
                                } else {
                                    ViewPage viewPage = PhotoPickerSearchActivity.this.viewPages[0];
                                    PhotoPickerSearchActivity.this.viewPages[0] = PhotoPickerSearchActivity.this.viewPages[1];
                                    PhotoPickerSearchActivity.this.viewPages[1] = viewPage;
                                    PhotoPickerSearchActivity.this.viewPages[1].setVisibility(8);
                                    PhotoPickerSearchActivity photoPickerSearchActivity = PhotoPickerSearchActivity.this;
                                    boolean unused2 = photoPickerSearchActivity.swipeBackEnabled = photoPickerSearchActivity.viewPages[0].selectedType == PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.getFirstTabId();
                                    PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.selectTabWithId(PhotoPickerSearchActivity.this.viewPages[0].selectedType, 1.0f);
                                }
                                boolean unused3 = PhotoPickerSearchActivity.this.tabsAnimationInProgress = false;
                                boolean unused4 = AnonymousClass4.this.maybeStartTracking = false;
                                boolean unused5 = AnonymousClass4.this.startedTracking = false;
                                PhotoPickerSearchActivity.this.actionBar.setEnabled(true);
                                PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
                            }
                        });
                        PhotoPickerSearchActivity.this.tabsAnimation.start();
                        boolean unused3 = PhotoPickerSearchActivity.this.tabsAnimationInProgress = true;
                        this.startedTracking = false;
                    } else {
                        this.maybeStartTracking = false;
                        PhotoPickerSearchActivity.this.actionBar.setEnabled(true);
                        PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
                    }
                    VelocityTracker velocityTracker2 = this.velocityTracker;
                    if (velocityTracker2 != null) {
                        velocityTracker2.recycle();
                        this.velocityTracker = null;
                    }
                }
                return this.startedTracking;
            }
        };
        this.fragmentView = r0;
        r0.setWillNotDraw(false);
        this.imagesSearch.setParentFragment(this);
        EditTextEmoji editTextEmoji = this.imagesSearch.commentTextView;
        this.commentTextView = editTextEmoji;
        editTextEmoji.setSizeNotifierLayout(r0);
        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                view = this.imagesSearch.frameLayout2;
            } else if (i == 1) {
                view = this.imagesSearch.writeButtonContainer;
            } else if (i != 2) {
                view = this.imagesSearch.shadow;
            } else {
                view = this.imagesSearch.selectedCountView;
            }
            ((ViewGroup) view.getParent()).removeView(view);
        }
        PhotoPickerActivity photoPickerActivity = this.gifsSearch;
        PhotoPickerActivity photoPickerActivity2 = this.imagesSearch;
        photoPickerActivity.setLayoutViews(photoPickerActivity2.frameLayout2, photoPickerActivity2.writeButtonContainer, photoPickerActivity2.selectedCountView, photoPickerActivity2.shadow, photoPickerActivity2.commentTextView);
        this.gifsSearch.setParentFragment(this);
        int i2 = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (i2 >= viewPageArr.length) {
                break;
            }
            viewPageArr[i2] = new ViewPage(context) {
                public void setTranslationX(float f) {
                    super.setTranslationX(f);
                    if (PhotoPickerSearchActivity.this.tabsAnimationInProgress && PhotoPickerSearchActivity.this.viewPages[0] == this) {
                        PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.selectTabWithId(PhotoPickerSearchActivity.this.viewPages[1].selectedType, Math.abs(PhotoPickerSearchActivity.this.viewPages[0].getTranslationX()) / ((float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()));
                    }
                }
            };
            r0.addView(this.viewPages[i2], LayoutHelper.createFrame(-1, -1.0f));
            if (i2 == 0) {
                BaseFragment unused = this.viewPages[i2].parentFragment = this.imagesSearch;
                RecyclerListView unused2 = this.viewPages[i2].listView = this.imagesSearch.getListView();
            } else if (i2 == 1) {
                BaseFragment unused3 = this.viewPages[i2].parentFragment = this.gifsSearch;
                RecyclerListView unused4 = this.viewPages[i2].listView = this.gifsSearch.getListView();
                this.viewPages[i2].setVisibility(8);
            }
            this.viewPages[i2].listView.setScrollingTouchSlop(1);
            ViewPage[] viewPageArr2 = this.viewPages;
            FrameLayout unused5 = viewPageArr2[i2].fragmentView = (FrameLayout) viewPageArr2[i2].parentFragment.getFragmentView();
            this.viewPages[i2].listView.setClipToPadding(false);
            ViewPage[] viewPageArr3 = this.viewPages;
            ActionBar unused6 = viewPageArr3[i2].actionBar = viewPageArr3[i2].parentFragment.getActionBar();
            ViewPage[] viewPageArr4 = this.viewPages;
            viewPageArr4[i2].addView(viewPageArr4[i2].fragmentView, LayoutHelper.createFrame(-1, -1.0f));
            ViewPage[] viewPageArr5 = this.viewPages;
            viewPageArr5[i2].addView(viewPageArr5[i2].actionBar, LayoutHelper.createFrame(-1, -2.0f));
            this.viewPages[i2].actionBar.setVisibility(8);
            final RecyclerView.OnScrollListener onScrollListener = this.viewPages[i2].listView.getOnScrollListener();
            this.viewPages[i2].listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                    onScrollListener.onScrollStateChanged(recyclerView, i);
                    if (i != 1) {
                        int i2 = (int) (-PhotoPickerSearchActivity.this.actionBar.getTranslationY());
                        int currentActionBarHeight = ActionBar.getCurrentActionBarHeight();
                        if (i2 != 0 && i2 != currentActionBarHeight) {
                            if (i2 < currentActionBarHeight / 2) {
                                PhotoPickerSearchActivity.this.viewPages[0].listView.smoothScrollBy(0, -i2);
                            } else {
                                PhotoPickerSearchActivity.this.viewPages[0].listView.smoothScrollBy(0, currentActionBarHeight - i2);
                            }
                        }
                    }
                }

                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    onScrollListener.onScrolled(recyclerView, i, i2);
                    if (recyclerView == PhotoPickerSearchActivity.this.viewPages[0].listView) {
                        float translationY = PhotoPickerSearchActivity.this.actionBar.getTranslationY();
                        float f = translationY - ((float) i2);
                        if (f < ((float) (-ActionBar.getCurrentActionBarHeight()))) {
                            f = (float) (-ActionBar.getCurrentActionBarHeight());
                        } else if (f > 0.0f) {
                            f = 0.0f;
                        }
                        if (f != translationY) {
                            PhotoPickerSearchActivity.this.setScrollY(f);
                        }
                    }
                }
            });
            i2++;
        }
        r0.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        r0.addView(this.imagesSearch.shadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        r0.addView(this.imagesSearch.frameLayout2, LayoutHelper.createFrame(-1, 48, 83));
        r0.addView(this.imagesSearch.writeButtonContainer, LayoutHelper.createFrame(60, 60.0f, 85, 0.0f, 0.0f, 12.0f, 10.0f));
        r0.addView(this.imagesSearch.selectedCountView, LayoutHelper.createFrame(42, 24.0f, 85, 0.0f, 0.0f, -2.0f, 9.0f));
        updateTabs();
        switchToCurrentSelectedMode(false);
        if (this.scrollSlidingTextTabStrip.getCurrentTabId() == this.scrollSlidingTextTabStrip.getFirstTabId()) {
            z = true;
        }
        this.swipeBackEnabled = z;
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.openSearch(true);
            getParentActivity().getWindow().setSoftInputMode(SharedConfig.smoothKeyboard ? 32 : 16);
        }
        PhotoPickerActivity photoPickerActivity = this.imagesSearch;
        if (photoPickerActivity != null) {
            photoPickerActivity.onResume();
        }
        PhotoPickerActivity photoPickerActivity2 = this.gifsSearch;
        if (photoPickerActivity2 != null) {
            photoPickerActivity2.onResume();
        }
    }

    public void setCaption(CharSequence charSequence) {
        PhotoPickerActivity photoPickerActivity = this.imagesSearch;
        if (photoPickerActivity != null) {
            photoPickerActivity.setCaption(charSequence);
        }
    }

    public void onPause() {
        super.onPause();
        PhotoPickerActivity photoPickerActivity = this.imagesSearch;
        if (photoPickerActivity != null) {
            photoPickerActivity.onPause();
        }
        PhotoPickerActivity photoPickerActivity2 = this.gifsSearch;
        if (photoPickerActivity2 != null) {
            photoPickerActivity2.onPause();
        }
    }

    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        return this.swipeBackEnabled;
    }

    public void onFragmentDestroy() {
        PhotoPickerActivity photoPickerActivity = this.imagesSearch;
        if (photoPickerActivity != null) {
            photoPickerActivity.onFragmentDestroy();
        }
        PhotoPickerActivity photoPickerActivity2 = this.gifsSearch;
        if (photoPickerActivity2 != null) {
            photoPickerActivity2.onFragmentDestroy();
        }
        super.onFragmentDestroy();
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        PhotoPickerActivity photoPickerActivity = this.imagesSearch;
        if (photoPickerActivity != null) {
            photoPickerActivity.onConfigurationChanged(configuration);
        }
        PhotoPickerActivity photoPickerActivity2 = this.gifsSearch;
        if (photoPickerActivity2 != null) {
            photoPickerActivity2.onConfigurationChanged(configuration);
        }
    }

    /* access modifiers changed from: private */
    public void setScrollY(float f) {
        this.actionBar.setTranslationY(f);
        int i = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (i < viewPageArr.length) {
                viewPageArr[i].listView.setPinnedSectionOffsetY((int) f);
                i++;
            } else {
                this.fragmentView.invalidate();
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    public void searchText(String str) {
        this.searchItem.getSearchField().setText(str);
        this.searchItem.getSearchField().setSelection(str.length());
        this.actionBar.onSearchPressed();
    }

    public void setDelegate(PhotoPickerActivity.PhotoPickerActivityDelegate photoPickerActivityDelegate) {
        this.imagesSearch.setDelegate(photoPickerActivityDelegate);
        this.gifsSearch.setDelegate(photoPickerActivityDelegate);
        this.imagesSearch.setSearchDelegate(new PhotoPickerActivity.PhotoPickerActivitySearchDelegate() {
            public void shouldSearchText(String str) {
                PhotoPickerSearchActivity.this.searchText(str);
            }

            public void shouldClearRecentSearch() {
                PhotoPickerSearchActivity.this.imagesSearch.clearRecentSearch();
                PhotoPickerSearchActivity.this.gifsSearch.clearRecentSearch();
            }
        });
        this.gifsSearch.setSearchDelegate(new PhotoPickerActivity.PhotoPickerActivitySearchDelegate() {
            public void shouldSearchText(String str) {
                PhotoPickerSearchActivity.this.searchText(str);
            }

            public void shouldClearRecentSearch() {
                PhotoPickerSearchActivity.this.imagesSearch.clearRecentSearch();
                PhotoPickerSearchActivity.this.gifsSearch.clearRecentSearch();
            }
        });
    }

    public void setMaxSelectedPhotos(int i, boolean z) {
        this.imagesSearch.setMaxSelectedPhotos(i, z);
        this.gifsSearch.setMaxSelectedPhotos(i, z);
    }

    private void updateTabs() {
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip2 = this.scrollSlidingTextTabStrip;
        if (scrollSlidingTextTabStrip2 != null) {
            scrollSlidingTextTabStrip2.addTextTab(0, LocaleController.getString("ImagesTab2", NUM));
            this.scrollSlidingTextTabStrip.addTextTab(1, LocaleController.getString("GifsTab2", NUM));
            this.scrollSlidingTextTabStrip.setVisibility(0);
            this.actionBar.setExtraHeight(AndroidUtilities.dp(44.0f));
            int currentTabId = this.scrollSlidingTextTabStrip.getCurrentTabId();
            if (currentTabId >= 0) {
                int unused = this.viewPages[0].selectedType = currentTabId;
            }
            this.scrollSlidingTextTabStrip.finishAddingTabs();
        }
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [boolean] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void switchToCurrentSelectedMode(boolean r5) {
        /*
            r4 = this;
            r0 = 0
            r1 = 0
        L_0x0002:
            org.telegram.ui.PhotoPickerSearchActivity$ViewPage[] r2 = r4.viewPages
            int r3 = r2.length
            if (r1 >= r3) goto L_0x0013
            r2 = r2[r1]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            r2.stopScroll()
            int r1 = r1 + 1
            goto L_0x0002
        L_0x0013:
            r1 = r2[r5]
            org.telegram.ui.Components.RecyclerListView r1 = r1.listView
            r1.getAdapter()
            org.telegram.ui.PhotoPickerSearchActivity$ViewPage[] r1 = r4.viewPages
            r1 = r1[r5]
            org.telegram.ui.Components.RecyclerListView r1 = r1.listView
            r2 = 0
            r1.setPinnedHeaderShadowDrawable(r2)
            org.telegram.ui.ActionBar.ActionBar r1 = r4.actionBar
            float r1 = r1.getTranslationY()
            r2 = 0
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x004b
            org.telegram.ui.PhotoPickerSearchActivity$ViewPage[] r1 = r4.viewPages
            r5 = r1[r5]
            org.telegram.ui.Components.RecyclerListView r5 = r5.listView
            androidx.recyclerview.widget.RecyclerView$LayoutManager r5 = r5.getLayoutManager()
            androidx.recyclerview.widget.LinearLayoutManager r5 = (androidx.recyclerview.widget.LinearLayoutManager) r5
            org.telegram.ui.ActionBar.ActionBar r1 = r4.actionBar
            float r1 = r1.getTranslationY()
            int r1 = (int) r1
            r5.scrollToPositionWithOffset(r0, r1)
        L_0x004b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoPickerSearchActivity.switchToCurrentSelectedMode(boolean):void");
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogButtonSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelHint"));
        arrayList.add(new ThemeDescription(this.searchItem.getSearchField(), ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_attachActiveTab"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_attachUnactiveTab"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogButtonSelector"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, new Drawable[]{this.scrollSlidingTextTabStrip.getSelectorDrawable()}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_attachActiveTab"));
        arrayList.addAll(this.imagesSearch.getThemeDescriptions());
        arrayList.addAll(this.gifsSearch.getThemeDescriptions());
        return arrayList;
    }
}
