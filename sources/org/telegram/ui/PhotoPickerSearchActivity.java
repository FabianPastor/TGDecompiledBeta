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
import androidx.recyclerview.widget.LinearLayoutManager;
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
    public static final Interpolator interpolator = PhotoPickerSearchActivity$$ExternalSyntheticLambda0.INSTANCE;
    private static final int search_button = 0;
    /* access modifiers changed from: private */
    public boolean animatingForward;
    /* access modifiers changed from: private */
    public boolean backAnimation;
    /* access modifiers changed from: private */
    public Paint backgroundPaint = new Paint();
    private ChatActivity chatActivity;
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
    private int selectPhotoType;
    private boolean sendPressed;
    /* access modifiers changed from: private */
    public boolean swipeBackEnabled = true;
    /* access modifiers changed from: private */
    public AnimatorSet tabsAnimation;
    /* access modifiers changed from: private */
    public boolean tabsAnimationInProgress;
    /* access modifiers changed from: private */
    public ViewPage[] viewPages = new ViewPage[2];

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

    static /* synthetic */ float lambda$static$0(float t) {
        float t2 = t - 1.0f;
        return (t2 * t2 * t2 * t2 * t2) + 1.0f;
    }

    public PhotoPickerSearchActivity(HashMap<Object, Object> selectedPhotos, ArrayList<Object> selectedPhotosOrder, int selectPhotoType2, boolean allowCaption, ChatActivity chatActivity2) {
        this.imagesSearch = new PhotoPickerActivity(0, (MediaController.AlbumEntry) null, selectedPhotos, selectedPhotosOrder, selectPhotoType2, allowCaption, chatActivity2, false);
        this.gifsSearch = new PhotoPickerActivity(1, (MediaController.AlbumEntry) null, selectedPhotos, selectedPhotosOrder, selectPhotoType2, allowCaption, chatActivity2, false);
    }

    public View createView(Context context) {
        View view;
        Context context2 = context;
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
            public void onItemClick(int id) {
                if (id == -1) {
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
        EditTextBoldCursor editText = this.searchItem.getSearchField();
        editText.setTextColor(Theme.getColor("dialogTextBlack"));
        editText.setCursorColor(Theme.getColor("dialogTextBlack"));
        editText.setHintTextColor(Theme.getColor("chat_messagePanelHint"));
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip2 = new ScrollSlidingTextTabStrip(context2);
        this.scrollSlidingTextTabStrip = scrollSlidingTextTabStrip2;
        scrollSlidingTextTabStrip2.setUseSameWidth(true);
        this.scrollSlidingTextTabStrip.setColors("chat_attachActiveTab", "chat_attachActiveTab", "chat_attachUnactiveTab", "dialogButtonSelector");
        this.actionBar.addView(this.scrollSlidingTextTabStrip, LayoutHelper.createFrame(-1, 44, 83));
        this.scrollSlidingTextTabStrip.setDelegate(new ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate() {
            public /* synthetic */ void onSamePageSelected() {
                ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate.CC.$default$onSamePageSelected(this);
            }

            public void onPageSelected(int id, boolean forward) {
                if (PhotoPickerSearchActivity.this.viewPages[0].selectedType != id) {
                    PhotoPickerSearchActivity photoPickerSearchActivity = PhotoPickerSearchActivity.this;
                    boolean unused = photoPickerSearchActivity.swipeBackEnabled = id == photoPickerSearchActivity.scrollSlidingTextTabStrip.getFirstTabId();
                    int unused2 = PhotoPickerSearchActivity.this.viewPages[1].selectedType = id;
                    PhotoPickerSearchActivity.this.viewPages[1].setVisibility(0);
                    PhotoPickerSearchActivity.this.switchToCurrentSelectedMode(true);
                    boolean unused3 = PhotoPickerSearchActivity.this.animatingForward = forward;
                    if (id == 0) {
                        PhotoPickerSearchActivity.this.searchItem.setSearchFieldHint(LocaleController.getString("SearchImagesTitle", NUM));
                    } else {
                        PhotoPickerSearchActivity.this.searchItem.setSearchFieldHint(LocaleController.getString("SearchGifsTitle", NUM));
                    }
                }
            }

            public void onPageScrolled(float progress) {
                if (progress != 1.0f || PhotoPickerSearchActivity.this.viewPages[1].getVisibility() == 0) {
                    if (PhotoPickerSearchActivity.this.animatingForward) {
                        PhotoPickerSearchActivity.this.viewPages[0].setTranslationX((-progress) * ((float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()));
                        PhotoPickerSearchActivity.this.viewPages[1].setTranslationX(((float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()) - (((float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()) * progress));
                    } else {
                        PhotoPickerSearchActivity.this.viewPages[0].setTranslationX(((float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()) * progress);
                        PhotoPickerSearchActivity.this.viewPages[1].setTranslationX((((float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()) * progress) - ((float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()));
                    }
                    if (progress == 1.0f) {
                        ViewPage tempPage = PhotoPickerSearchActivity.this.viewPages[0];
                        PhotoPickerSearchActivity.this.viewPages[0] = PhotoPickerSearchActivity.this.viewPages[1];
                        PhotoPickerSearchActivity.this.viewPages[1] = tempPage;
                        PhotoPickerSearchActivity.this.viewPages[1].setVisibility(8);
                    }
                }
            }
        });
        this.maximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        SizeNotifierFrameLayout r4 = new SizeNotifierFrameLayout(context2) {
            private boolean globalIgnoreLayout;
            /* access modifiers changed from: private */
            public boolean maybeStartTracking;
            /* access modifiers changed from: private */
            public boolean startedTracking;
            private int startedTrackingPointerId;
            private int startedTrackingX;
            private int startedTrackingY;
            private VelocityTracker velocityTracker;

            private boolean prepareForMoving(MotionEvent ev, boolean forward) {
                int id = PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.getNextPageId(forward);
                if (id < 0) {
                    return false;
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                this.maybeStartTracking = false;
                this.startedTracking = true;
                this.startedTrackingX = (int) ev.getX();
                PhotoPickerSearchActivity.this.actionBar.setEnabled(false);
                PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.setEnabled(false);
                int unused = PhotoPickerSearchActivity.this.viewPages[1].selectedType = id;
                PhotoPickerSearchActivity.this.viewPages[1].setVisibility(0);
                boolean unused2 = PhotoPickerSearchActivity.this.animatingForward = forward;
                PhotoPickerSearchActivity.this.switchToCurrentSelectedMode(true);
                if (forward) {
                    PhotoPickerSearchActivity.this.viewPages[1].setTranslationX((float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth());
                } else {
                    PhotoPickerSearchActivity.this.viewPages[1].setTranslationX((float) (-PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()));
                }
                return true;
            }

            public void forceHasOverlappingRendering(boolean hasOverlappingRendering) {
                super.forceHasOverlappingRendering(hasOverlappingRendering);
            }

            /* access modifiers changed from: protected */
            /* JADX WARNING: Removed duplicated region for block: B:14:0x0071  */
            /* JADX WARNING: Removed duplicated region for block: B:22:0x00b9  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onMeasure(int r17, int r18) {
                /*
                    r16 = this;
                    r6 = r16
                    int r7 = android.view.View.MeasureSpec.getSize(r17)
                    int r8 = android.view.View.MeasureSpec.getSize(r18)
                    r6.setMeasuredDimension(r7, r8)
                    org.telegram.ui.PhotoPickerSearchActivity r0 = org.telegram.ui.PhotoPickerSearchActivity.this
                    org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
                    r3 = 0
                    r5 = 0
                    r0 = r16
                    r2 = r17
                    r4 = r18
                    r0.measureChildWithMargins(r1, r2, r3, r4, r5)
                    boolean r0 = org.telegram.messenger.SharedConfig.smoothKeyboard
                    r1 = 0
                    if (r0 == 0) goto L_0x0025
                    r0 = 0
                    goto L_0x0029
                L_0x0025:
                    int r0 = r16.measureKeyboardHeight()
                L_0x0029:
                    r9 = r0
                    r0 = 1101004800(0x41a00000, float:20.0)
                    int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                    r2 = 1
                    r10 = 1073741824(0x40000000, float:2.0)
                    if (r9 > r0) goto L_0x004b
                    boolean r0 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
                    if (r0 != 0) goto L_0x0058
                    org.telegram.ui.PhotoPickerSearchActivity r0 = org.telegram.ui.PhotoPickerSearchActivity.this
                    org.telegram.ui.Components.EditTextEmoji r0 = r0.commentTextView
                    int r0 = r0.getEmojiPadding()
                    int r8 = r8 - r0
                    int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r8, r10)
                    r11 = r8
                    r8 = r0
                    goto L_0x005b
                L_0x004b:
                    r6.globalIgnoreLayout = r2
                    org.telegram.ui.PhotoPickerSearchActivity r0 = org.telegram.ui.PhotoPickerSearchActivity.this
                    org.telegram.ui.Components.EditTextEmoji r0 = r0.commentTextView
                    r0.hideEmojiView()
                    r6.globalIgnoreLayout = r1
                L_0x0058:
                    r11 = r8
                    r8 = r18
                L_0x005b:
                    org.telegram.ui.PhotoPickerSearchActivity r0 = org.telegram.ui.PhotoPickerSearchActivity.this
                    org.telegram.ui.ActionBar.ActionBar r0 = r0.actionBar
                    int r12 = r0.getMeasuredHeight()
                    r6.globalIgnoreLayout = r2
                    r0 = 0
                L_0x0068:
                    org.telegram.ui.PhotoPickerSearchActivity r2 = org.telegram.ui.PhotoPickerSearchActivity.this
                    org.telegram.ui.PhotoPickerSearchActivity$ViewPage[] r2 = r2.viewPages
                    int r2 = r2.length
                    if (r0 >= r2) goto L_0x00af
                    org.telegram.ui.PhotoPickerSearchActivity r2 = org.telegram.ui.PhotoPickerSearchActivity.this
                    org.telegram.ui.PhotoPickerSearchActivity$ViewPage[] r2 = r2.viewPages
                    r2 = r2[r0]
                    if (r2 != 0) goto L_0x007c
                    goto L_0x00ac
                L_0x007c:
                    org.telegram.ui.PhotoPickerSearchActivity r2 = org.telegram.ui.PhotoPickerSearchActivity.this
                    org.telegram.ui.PhotoPickerSearchActivity$ViewPage[] r2 = r2.viewPages
                    r2 = r2[r0]
                    org.telegram.ui.Components.RecyclerListView r2 = r2.listView
                    if (r2 == 0) goto L_0x00ac
                    org.telegram.ui.PhotoPickerSearchActivity r2 = org.telegram.ui.PhotoPickerSearchActivity.this
                    org.telegram.ui.PhotoPickerSearchActivity$ViewPage[] r2 = r2.viewPages
                    r2 = r2[r0]
                    org.telegram.ui.Components.RecyclerListView r2 = r2.listView
                    r3 = 1082130432(0x40800000, float:4.0)
                    int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
                    int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
                    int r5 = r5 + r12
                    int r13 = org.telegram.messenger.AndroidUtilities.dp(r3)
                    int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                    r2.setPadding(r4, r5, r13, r3)
                L_0x00ac:
                    int r0 = r0 + 1
                    goto L_0x0068
                L_0x00af:
                    r6.globalIgnoreLayout = r1
                    int r13 = r16.getChildCount()
                    r0 = 0
                    r14 = r0
                L_0x00b7:
                    if (r14 >= r13) goto L_0x0154
                    android.view.View r15 = r6.getChildAt(r14)
                    if (r15 == 0) goto L_0x0150
                    int r0 = r15.getVisibility()
                    r1 = 8
                    if (r0 == r1) goto L_0x0150
                    org.telegram.ui.PhotoPickerSearchActivity r0 = org.telegram.ui.PhotoPickerSearchActivity.this
                    org.telegram.ui.ActionBar.ActionBar r0 = r0.actionBar
                    if (r15 != r0) goto L_0x00d1
                    goto L_0x0150
                L_0x00d1:
                    org.telegram.ui.PhotoPickerSearchActivity r0 = org.telegram.ui.PhotoPickerSearchActivity.this
                    org.telegram.ui.Components.EditTextEmoji r0 = r0.commentTextView
                    if (r0 == 0) goto L_0x0145
                    org.telegram.ui.PhotoPickerSearchActivity r0 = org.telegram.ui.PhotoPickerSearchActivity.this
                    org.telegram.ui.Components.EditTextEmoji r0 = r0.commentTextView
                    boolean r0 = r0.isPopupView(r15)
                    if (r0 == 0) goto L_0x0145
                    boolean r0 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
                    if (r0 != 0) goto L_0x0102
                    boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
                    if (r0 == 0) goto L_0x00f0
                    goto L_0x0102
                L_0x00f0:
                    int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r7, r10)
                    android.view.ViewGroup$LayoutParams r1 = r15.getLayoutParams()
                    int r1 = r1.height
                    int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r10)
                    r15.measure(r0, r1)
                    goto L_0x0150
                L_0x0102:
                    boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
                    if (r0 == 0) goto L_0x0130
                    int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r7, r10)
                    boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
                    if (r1 == 0) goto L_0x0115
                    r1 = 1128792064(0x43480000, float:200.0)
                    goto L_0x0117
                L_0x0115:
                    r1 = 1134559232(0x43a00000, float:320.0)
                L_0x0117:
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    int r2 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    int r2 = r11 - r2
                    int r3 = r16.getPaddingTop()
                    int r2 = r2 + r3
                    int r1 = java.lang.Math.min(r1, r2)
                    int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r10)
                    r15.measure(r0, r1)
                    goto L_0x0150
                L_0x0130:
                    int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r7, r10)
                    int r1 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    int r1 = r11 - r1
                    int r2 = r16.getPaddingTop()
                    int r1 = r1 + r2
                    int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r10)
                    r15.measure(r0, r1)
                    goto L_0x0150
                L_0x0145:
                    r3 = 0
                    r5 = 0
                    r0 = r16
                    r1 = r15
                    r2 = r17
                    r4 = r8
                    r0.measureChildWithMargins(r1, r2, r3, r4, r5)
                L_0x0150:
                    int r14 = r14 + 1
                    goto L_0x00b7
                L_0x0154:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoPickerSearchActivity.AnonymousClass4.onMeasure(int, int):void");
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                int childLeft;
                int childTop;
                int count = getChildCount();
                int paddingBottom = 0;
                int keyboardSize = SharedConfig.smoothKeyboard ? 0 : measureKeyboardHeight();
                if (keyboardSize <= AndroidUtilities.dp(20.0f) && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                    paddingBottom = PhotoPickerSearchActivity.this.commentTextView.getEmojiPadding();
                }
                setBottomClip(paddingBottom);
                for (int i = 0; i < count; i++) {
                    View child = getChildAt(i);
                    if (child.getVisibility() != 8) {
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                        int width = child.getMeasuredWidth();
                        int height = child.getMeasuredHeight();
                        int gravity = lp.gravity;
                        if (gravity == -1) {
                            gravity = 51;
                        }
                        int verticalGravity = gravity & 112;
                        switch (gravity & 7 & 7) {
                            case 1:
                                childLeft = ((((r - l) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                                break;
                            case 5:
                                childLeft = (((r - l) - width) - lp.rightMargin) - getPaddingRight();
                                break;
                            default:
                                childLeft = lp.leftMargin + getPaddingLeft();
                                break;
                        }
                        switch (verticalGravity) {
                            case 16:
                                childTop = (((((b - paddingBottom) - t) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                                break;
                            case 48:
                                childTop = lp.topMargin + getPaddingTop();
                                break;
                            case 80:
                                childTop = (((b - paddingBottom) - t) - height) - lp.bottomMargin;
                                break;
                            default:
                                childTop = lp.topMargin;
                                break;
                        }
                        if (PhotoPickerSearchActivity.this.commentTextView != null && PhotoPickerSearchActivity.this.commentTextView.isPopupView(child)) {
                            if (AndroidUtilities.isTablet()) {
                                childTop = getMeasuredHeight() - child.getMeasuredHeight();
                            } else {
                                childTop = (getMeasuredHeight() + keyboardSize) - child.getMeasuredHeight();
                            }
                        }
                        child.layout(childLeft, childTop, childLeft + width, childTop + height);
                    }
                }
                notifyHeightChanged();
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

            public boolean checkTabsAnimationInProgress() {
                if (!PhotoPickerSearchActivity.this.tabsAnimationInProgress) {
                    return false;
                }
                boolean cancel = false;
                int i = -1;
                if (PhotoPickerSearchActivity.this.backAnimation) {
                    if (Math.abs(PhotoPickerSearchActivity.this.viewPages[0].getTranslationX()) < 1.0f) {
                        PhotoPickerSearchActivity.this.viewPages[0].setTranslationX(0.0f);
                        ViewPage viewPage = PhotoPickerSearchActivity.this.viewPages[1];
                        int measuredWidth = PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth();
                        if (PhotoPickerSearchActivity.this.animatingForward) {
                            i = 1;
                        }
                        viewPage.setTranslationX((float) (measuredWidth * i));
                        cancel = true;
                    }
                } else if (Math.abs(PhotoPickerSearchActivity.this.viewPages[1].getTranslationX()) < 1.0f) {
                    ViewPage viewPage2 = PhotoPickerSearchActivity.this.viewPages[0];
                    int measuredWidth2 = PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth();
                    if (!PhotoPickerSearchActivity.this.animatingForward) {
                        i = 1;
                    }
                    viewPage2.setTranslationX((float) (measuredWidth2 * i));
                    PhotoPickerSearchActivity.this.viewPages[1].setTranslationX(0.0f);
                    cancel = true;
                }
                if (cancel) {
                    if (PhotoPickerSearchActivity.this.tabsAnimation != null) {
                        PhotoPickerSearchActivity.this.tabsAnimation.cancel();
                        AnimatorSet unused = PhotoPickerSearchActivity.this.tabsAnimation = null;
                    }
                    boolean unused2 = PhotoPickerSearchActivity.this.tabsAnimationInProgress = false;
                }
                return PhotoPickerSearchActivity.this.tabsAnimationInProgress;
            }

            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return checkTabsAnimationInProgress() || PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.isAnimatingIndicator() || onTouchEvent(ev);
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                PhotoPickerSearchActivity.this.backgroundPaint.setColor(Theme.getColor("windowBackgroundGray"));
                canvas.drawRect(0.0f, ((float) PhotoPickerSearchActivity.this.actionBar.getMeasuredHeight()) + PhotoPickerSearchActivity.this.actionBar.getTranslationY(), (float) getMeasuredWidth(), (float) getMeasuredHeight(), PhotoPickerSearchActivity.this.backgroundPaint);
            }

            public boolean onTouchEvent(MotionEvent ev) {
                float velY;
                float velX;
                float dx;
                int duration;
                boolean z = false;
                if (PhotoPickerSearchActivity.this.parentLayout.checkTransitionAnimation() || checkTabsAnimationInProgress()) {
                    return false;
                }
                if (ev != null) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    this.velocityTracker.addMovement(ev);
                }
                if (ev != null && ev.getAction() == 0 && !this.startedTracking && !this.maybeStartTracking) {
                    this.startedTrackingPointerId = ev.getPointerId(0);
                    this.maybeStartTracking = true;
                    this.startedTrackingX = (int) ev.getX();
                    this.startedTrackingY = (int) ev.getY();
                    this.velocityTracker.clear();
                } else if (ev != null && ev.getAction() == 2 && ev.getPointerId(0) == this.startedTrackingPointerId) {
                    int dx2 = (int) (ev.getX() - ((float) this.startedTrackingX));
                    int dy = Math.abs(((int) ev.getY()) - this.startedTrackingY);
                    if (this.startedTracking && ((PhotoPickerSearchActivity.this.animatingForward && dx2 > 0) || (!PhotoPickerSearchActivity.this.animatingForward && dx2 < 0))) {
                        if (!prepareForMoving(ev, dx2 < 0)) {
                            this.maybeStartTracking = true;
                            this.startedTracking = false;
                            PhotoPickerSearchActivity.this.viewPages[0].setTranslationX(0.0f);
                            PhotoPickerSearchActivity.this.viewPages[1].setTranslationX((float) (PhotoPickerSearchActivity.this.animatingForward ? PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth() : -PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()));
                            PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.selectTabWithId(PhotoPickerSearchActivity.this.viewPages[1].selectedType, 0.0f);
                        }
                    }
                    if (this.maybeStartTracking && !this.startedTracking) {
                        if (((float) Math.abs(dx2)) >= AndroidUtilities.getPixelsInCM(0.3f, true) && Math.abs(dx2) > dy) {
                            if (dx2 < 0) {
                                z = true;
                            }
                            prepareForMoving(ev, z);
                        }
                    } else if (this.startedTracking) {
                        PhotoPickerSearchActivity.this.viewPages[0].setTranslationX((float) dx2);
                        if (PhotoPickerSearchActivity.this.animatingForward) {
                            PhotoPickerSearchActivity.this.viewPages[1].setTranslationX((float) (PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth() + dx2));
                        } else {
                            PhotoPickerSearchActivity.this.viewPages[1].setTranslationX((float) (dx2 - PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()));
                        }
                        PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.selectTabWithId(PhotoPickerSearchActivity.this.viewPages[1].selectedType, ((float) Math.abs(dx2)) / ((float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()));
                    }
                } else if (ev == null || (ev.getPointerId(0) == this.startedTrackingPointerId && (ev.getAction() == 3 || ev.getAction() == 1 || ev.getAction() == 6))) {
                    this.velocityTracker.computeCurrentVelocity(1000, (float) PhotoPickerSearchActivity.this.maximumVelocity);
                    if (ev == null || ev.getAction() == 3) {
                        velX = 0.0f;
                        velY = 0.0f;
                    } else {
                        velX = this.velocityTracker.getXVelocity();
                        velY = this.velocityTracker.getYVelocity();
                        if (!this.startedTracking && Math.abs(velX) >= 3000.0f && Math.abs(velX) > Math.abs(velY)) {
                            prepareForMoving(ev, velX < 0.0f);
                        }
                    }
                    if (this.startedTracking) {
                        float x = PhotoPickerSearchActivity.this.viewPages[0].getX();
                        AnimatorSet unused = PhotoPickerSearchActivity.this.tabsAnimation = new AnimatorSet();
                        boolean unused2 = PhotoPickerSearchActivity.this.backAnimation = Math.abs(x) < ((float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(velX) < 3500.0f || Math.abs(velX) < Math.abs(velY));
                        if (PhotoPickerSearchActivity.this.backAnimation) {
                            dx = Math.abs(x);
                            if (PhotoPickerSearchActivity.this.animatingForward) {
                                PhotoPickerSearchActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{(float) PhotoPickerSearchActivity.this.viewPages[1].getMeasuredWidth()})});
                            } else {
                                PhotoPickerSearchActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{(float) (-PhotoPickerSearchActivity.this.viewPages[1].getMeasuredWidth())})});
                            }
                        } else {
                            dx = ((float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()) - Math.abs(x);
                            if (PhotoPickerSearchActivity.this.animatingForward) {
                                PhotoPickerSearchActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{(float) (-PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth())}), ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{0.0f})});
                            } else {
                                PhotoPickerSearchActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{(float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()}), ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{0.0f})});
                            }
                        }
                        PhotoPickerSearchActivity.this.tabsAnimation.setInterpolator(PhotoPickerSearchActivity.interpolator);
                        int width = getMeasuredWidth();
                        int halfWidth = width / 2;
                        float distance = ((float) halfWidth) + (((float) halfWidth) * AndroidUtilities.distanceInfluenceForSnapDuration(Math.min(1.0f, (dx * 1.0f) / ((float) width))));
                        float velX2 = Math.abs(velX);
                        if (velX2 > 0.0f) {
                            duration = Math.round(Math.abs(distance / velX2) * 1000.0f) * 4;
                        } else {
                            duration = (int) ((1.0f + (dx / ((float) getMeasuredWidth()))) * 100.0f);
                        }
                        PhotoPickerSearchActivity.this.tabsAnimation.setDuration((long) Math.max(150, Math.min(duration, 600)));
                        PhotoPickerSearchActivity.this.tabsAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                AnimatorSet unused = PhotoPickerSearchActivity.this.tabsAnimation = null;
                                if (PhotoPickerSearchActivity.this.backAnimation) {
                                    PhotoPickerSearchActivity.this.viewPages[1].setVisibility(8);
                                } else {
                                    ViewPage tempPage = PhotoPickerSearchActivity.this.viewPages[0];
                                    PhotoPickerSearchActivity.this.viewPages[0] = PhotoPickerSearchActivity.this.viewPages[1];
                                    PhotoPickerSearchActivity.this.viewPages[1] = tempPage;
                                    PhotoPickerSearchActivity.this.viewPages[1].setVisibility(8);
                                    boolean unused2 = PhotoPickerSearchActivity.this.swipeBackEnabled = PhotoPickerSearchActivity.this.viewPages[0].selectedType == PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.getFirstTabId();
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
        SizeNotifierFrameLayout sizeNotifierFrameLayout = r4;
        this.fragmentView = r4;
        sizeNotifierFrameLayout.setWillNotDraw(false);
        this.imagesSearch.setParentFragment(this);
        EditTextEmoji editTextEmoji = this.imagesSearch.commentTextView;
        this.commentTextView = editTextEmoji;
        editTextEmoji.setSizeNotifierLayout(sizeNotifierFrameLayout);
        for (int a = 0; a < 4; a++) {
            switch (a) {
                case 0:
                    view = this.imagesSearch.frameLayout2;
                    break;
                case 1:
                    view = this.imagesSearch.writeButtonContainer;
                    break;
                case 2:
                    view = this.imagesSearch.selectedCountView;
                    break;
                default:
                    view = this.imagesSearch.shadow;
                    break;
            }
            ((ViewGroup) view.getParent()).removeView(view);
        }
        this.gifsSearch.setLayoutViews(this.imagesSearch.frameLayout2, this.imagesSearch.writeButtonContainer, this.imagesSearch.selectedCountView, this.imagesSearch.shadow, this.imagesSearch.commentTextView);
        this.gifsSearch.setParentFragment(this);
        int a2 = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (a2 < viewPageArr.length) {
                viewPageArr[a2] = new ViewPage(context2) {
                    public void setTranslationX(float translationX) {
                        super.setTranslationX(translationX);
                        if (PhotoPickerSearchActivity.this.tabsAnimationInProgress && PhotoPickerSearchActivity.this.viewPages[0] == this) {
                            PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.selectTabWithId(PhotoPickerSearchActivity.this.viewPages[1].selectedType, Math.abs(PhotoPickerSearchActivity.this.viewPages[0].getTranslationX()) / ((float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()));
                        }
                    }
                };
                sizeNotifierFrameLayout.addView(this.viewPages[a2], LayoutHelper.createFrame(-1, -1.0f));
                if (a2 == 0) {
                    BaseFragment unused = this.viewPages[a2].parentFragment = this.imagesSearch;
                    RecyclerListView unused2 = this.viewPages[a2].listView = this.imagesSearch.getListView();
                } else if (a2 == 1) {
                    BaseFragment unused3 = this.viewPages[a2].parentFragment = this.gifsSearch;
                    RecyclerListView unused4 = this.viewPages[a2].listView = this.gifsSearch.getListView();
                    this.viewPages[a2].setVisibility(8);
                }
                this.viewPages[a2].listView.setScrollingTouchSlop(1);
                ViewPage[] viewPageArr2 = this.viewPages;
                FrameLayout unused5 = viewPageArr2[a2].fragmentView = (FrameLayout) viewPageArr2[a2].parentFragment.getFragmentView();
                this.viewPages[a2].listView.setClipToPadding(false);
                ViewPage[] viewPageArr3 = this.viewPages;
                ActionBar unused6 = viewPageArr3[a2].actionBar = viewPageArr3[a2].parentFragment.getActionBar();
                ViewPage[] viewPageArr4 = this.viewPages;
                viewPageArr4[a2].addView(viewPageArr4[a2].fragmentView, LayoutHelper.createFrame(-1, -1.0f));
                ViewPage[] viewPageArr5 = this.viewPages;
                viewPageArr5[a2].addView(viewPageArr5[a2].actionBar, LayoutHelper.createFrame(-1, -2.0f));
                this.viewPages[a2].actionBar.setVisibility(8);
                final RecyclerView.OnScrollListener onScrollListener = this.viewPages[a2].listView.getOnScrollListener();
                this.viewPages[a2].listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        onScrollListener.onScrollStateChanged(recyclerView, newState);
                        if (newState != 1) {
                            int scrollY = (int) (-PhotoPickerSearchActivity.this.actionBar.getTranslationY());
                            int actionBarHeight = ActionBar.getCurrentActionBarHeight();
                            if (scrollY != 0 && scrollY != actionBarHeight) {
                                if (scrollY < actionBarHeight / 2) {
                                    PhotoPickerSearchActivity.this.viewPages[0].listView.smoothScrollBy(0, -scrollY);
                                } else {
                                    PhotoPickerSearchActivity.this.viewPages[0].listView.smoothScrollBy(0, actionBarHeight - scrollY);
                                }
                            }
                        }
                    }

                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        onScrollListener.onScrolled(recyclerView, dx, dy);
                        if (recyclerView == PhotoPickerSearchActivity.this.viewPages[0].listView) {
                            float currentTranslation = PhotoPickerSearchActivity.this.actionBar.getTranslationY();
                            float newTranslation = currentTranslation - ((float) dy);
                            if (newTranslation < ((float) (-ActionBar.getCurrentActionBarHeight()))) {
                                newTranslation = (float) (-ActionBar.getCurrentActionBarHeight());
                            } else if (newTranslation > 0.0f) {
                                newTranslation = 0.0f;
                            }
                            if (newTranslation != currentTranslation) {
                                PhotoPickerSearchActivity.this.setScrollY(newTranslation);
                            }
                        }
                    }
                });
                a2++;
            } else {
                sizeNotifierFrameLayout.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
                sizeNotifierFrameLayout.addView(this.imagesSearch.shadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
                sizeNotifierFrameLayout.addView(this.imagesSearch.frameLayout2, LayoutHelper.createFrame(-1, 48, 83));
                sizeNotifierFrameLayout.addView(this.imagesSearch.writeButtonContainer, LayoutHelper.createFrame(60, 60.0f, 85, 0.0f, 0.0f, 12.0f, 10.0f));
                sizeNotifierFrameLayout.addView(this.imagesSearch.selectedCountView, LayoutHelper.createFrame(42, 24.0f, 85, 0.0f, 0.0f, -2.0f, 9.0f));
                updateTabs();
                switchToCurrentSelectedMode(false);
                if (this.scrollSlidingTextTabStrip.getCurrentTabId() == this.scrollSlidingTextTabStrip.getFirstTabId()) {
                    z = true;
                }
                this.swipeBackEnabled = z;
                return this.fragmentView;
            }
        }
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

    public void setCaption(CharSequence text) {
        PhotoPickerActivity photoPickerActivity = this.imagesSearch;
        if (photoPickerActivity != null) {
            photoPickerActivity.setCaption(text);
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

    public boolean isSwipeBackEnabled(MotionEvent event) {
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

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        PhotoPickerActivity photoPickerActivity = this.imagesSearch;
        if (photoPickerActivity != null) {
            photoPickerActivity.onConfigurationChanged(newConfig);
        }
        PhotoPickerActivity photoPickerActivity2 = this.gifsSearch;
        if (photoPickerActivity2 != null) {
            photoPickerActivity2.onConfigurationChanged(newConfig);
        }
    }

    /* access modifiers changed from: private */
    public void setScrollY(float value) {
        this.actionBar.setTranslationY(value);
        int a = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (a < viewPageArr.length) {
                viewPageArr[a].listView.setPinnedSectionOffsetY((int) value);
                a++;
            } else {
                this.fragmentView.invalidate();
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    public void searchText(String text) {
        this.searchItem.getSearchField().setText(text);
        this.searchItem.getSearchField().setSelection(text.length());
        this.actionBar.onSearchPressed();
    }

    public void setDelegate(PhotoPickerActivity.PhotoPickerActivityDelegate delegate) {
        this.imagesSearch.setDelegate(delegate);
        this.gifsSearch.setDelegate(delegate);
        this.imagesSearch.setSearchDelegate(new PhotoPickerActivity.PhotoPickerActivitySearchDelegate() {
            public void shouldSearchText(String text) {
                PhotoPickerSearchActivity.this.searchText(text);
            }

            public void shouldClearRecentSearch() {
                PhotoPickerSearchActivity.this.imagesSearch.clearRecentSearch();
                PhotoPickerSearchActivity.this.gifsSearch.clearRecentSearch();
            }
        });
        this.gifsSearch.setSearchDelegate(new PhotoPickerActivity.PhotoPickerActivitySearchDelegate() {
            public void shouldSearchText(String text) {
                PhotoPickerSearchActivity.this.searchText(text);
            }

            public void shouldClearRecentSearch() {
                PhotoPickerSearchActivity.this.imagesSearch.clearRecentSearch();
                PhotoPickerSearchActivity.this.gifsSearch.clearRecentSearch();
            }
        });
    }

    public void setMaxSelectedPhotos(int value, boolean order) {
        this.imagesSearch.setMaxSelectedPhotos(value, order);
        this.gifsSearch.setMaxSelectedPhotos(value, order);
    }

    private void updateTabs() {
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip2 = this.scrollSlidingTextTabStrip;
        if (scrollSlidingTextTabStrip2 != null) {
            scrollSlidingTextTabStrip2.addTextTab(0, LocaleController.getString("ImagesTab2", NUM));
            this.scrollSlidingTextTabStrip.addTextTab(1, LocaleController.getString("GifsTab2", NUM));
            this.scrollSlidingTextTabStrip.setVisibility(0);
            this.actionBar.setExtraHeight(AndroidUtilities.dp(44.0f));
            int id = this.scrollSlidingTextTabStrip.getCurrentTabId();
            if (id >= 0) {
                int unused = this.viewPages[0].selectedType = id;
            }
            this.scrollSlidingTextTabStrip.finishAddingTabs();
        }
    }

    /* access modifiers changed from: private */
    public void switchToCurrentSelectedMode(boolean animated) {
        ViewPage[] viewPageArr;
        int a = 0;
        while (true) {
            viewPageArr = this.viewPages;
            if (a >= viewPageArr.length) {
                break;
            }
            viewPageArr[a].listView.stopScroll();
            a++;
        }
        int a2 = animated;
        RecyclerView.Adapter adapter = viewPageArr[a2].listView.getAdapter();
        this.viewPages[a2].listView.setPinnedHeaderShadowDrawable((Drawable) null);
        if (this.actionBar.getTranslationY() != 0.0f) {
            ((LinearLayoutManager) this.viewPages[a2].listView.getLayoutManager()).scrollToPositionWithOffset(0, (int) this.actionBar.getTranslationY());
        }
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
