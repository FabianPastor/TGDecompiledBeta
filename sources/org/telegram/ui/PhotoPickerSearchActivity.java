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
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PickerBottomLayout;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScrollSlidingTextTabStrip;
import org.telegram.ui.Components.ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate;
import org.telegram.ui.PhotoPickerActivity.PhotoPickerActivityDelegate;

public class PhotoPickerSearchActivity extends BaseFragment {
    private static final Interpolator interpolator = -$$Lambda$PhotoPickerSearchActivity$jqQaX9Gg6oQw1X3auh_sMyxFvar_.INSTANCE;
    private static final int search_button = 0;
    private boolean animatingForward;
    private boolean backAnimation;
    private Paint backgroundPaint = new Paint();
    private ChatActivity chatActivity;
    private PhotoPickerActivity gifsSearch;
    private PhotoPickerActivity imagesSearch;
    private int maximumVelocity;
    private ScrollSlidingTextTabStrip scrollSlidingTextTabStrip;
    private ActionBarMenuItem searchItem;
    private int selectPhotoType;
    private boolean sendPressed;
    private AnimatorSet tabsAnimation;
    private boolean tabsAnimationInProgress;
    private ViewPage[] viewPages = new ViewPage[2];

    private class ViewPage extends FrameLayout {
        private ActionBar actionBar;
        private FrameLayout fragmentView;
        private RecyclerListView listView;
        private BaseFragment parentFragment;
        private int selectedType;

        public ViewPage(Context context) {
            super(context);
        }
    }

    public PhotoPickerSearchActivity(HashMap<Object, Object> hashMap, ArrayList<Object> arrayList, ArrayList<SearchImage> arrayList2, int i, boolean z, ChatActivity chatActivity) {
        this.imagesSearch = new PhotoPickerActivity(0, null, hashMap, arrayList, arrayList2, i, z, chatActivity);
        this.gifsSearch = new PhotoPickerActivity(1, null, hashMap, arrayList, arrayList2, i, z, chatActivity);
    }

    public View createView(Context context) {
        this.actionBar.setBackgroundColor(Theme.getColor("dialogBackground"));
        String str = "dialogTextBlack";
        this.actionBar.setTitleColor(Theme.getColor(str));
        boolean z = false;
        this.actionBar.setItemsColor(Theme.getColor(str), false);
        String str2 = "dialogButtonSelector";
        this.actionBar.setItemsBackgroundColor(Theme.getColor(str2), false);
        this.actionBar.setBackButtonImage(NUM);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setExtraHeight(AndroidUtilities.dp(44.0f));
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setAddToContainer(false);
        this.actionBar.setClipContent(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    PhotoPickerSearchActivity.this.finishFragment();
                }
            }
        });
        this.hasOwnBackground = true;
        this.searchItem = this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                String str = "";
                PhotoPickerSearchActivity.this.imagesSearch.getActionBar().openSearchField(str, false);
                PhotoPickerSearchActivity.this.gifsSearch.getActionBar().openSearchField(str, false);
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
        this.searchItem.setSearchFieldHint(LocaleController.getString("SearchImagesTitle", NUM));
        EditTextBoldCursor searchField = this.searchItem.getSearchField();
        searchField.setTextColor(Theme.getColor(str));
        searchField.setCursorColor(Theme.getColor(str));
        searchField.setHintTextColor(Theme.getColor("chat_messagePanelHint"));
        this.scrollSlidingTextTabStrip = new ScrollSlidingTextTabStrip(context);
        this.scrollSlidingTextTabStrip.setUseSameWidth(true);
        str = "chat_attachActiveTab";
        this.scrollSlidingTextTabStrip.setColors(str, str, "chat_attachUnactiveTab", str2);
        this.actionBar.addView(this.scrollSlidingTextTabStrip, LayoutHelper.createFrame(-1, 44, 83));
        this.scrollSlidingTextTabStrip.setDelegate(new ScrollSlidingTabStripDelegate() {
            public void onPageSelected(int i, boolean z) {
                if (PhotoPickerSearchActivity.this.viewPages[0].selectedType != i) {
                    PhotoPickerSearchActivity photoPickerSearchActivity = PhotoPickerSearchActivity.this;
                    photoPickerSearchActivity.swipeBackEnabled = i == photoPickerSearchActivity.scrollSlidingTextTabStrip.getFirstTabId();
                    PhotoPickerSearchActivity.this.viewPages[1].selectedType = i;
                    PhotoPickerSearchActivity.this.viewPages[1].setVisibility(0);
                    PhotoPickerSearchActivity.this.switchToCurrentSelectedMode(true);
                    PhotoPickerSearchActivity.this.animatingForward = z;
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
        AnonymousClass4 anonymousClass4 = new FrameLayout(context) {
            private boolean globalIgnoreLayout;
            private boolean maybeStartTracking;
            private boolean startedTracking;
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
                PhotoPickerSearchActivity.this.viewPages[1].selectedType = nextPageId;
                PhotoPickerSearchActivity.this.viewPages[1].setVisibility(0);
                PhotoPickerSearchActivity.this.animatingForward = z;
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

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                setMeasuredDimension(MeasureSpec.getSize(i), MeasureSpec.getSize(i2));
                measureChildWithMargins(PhotoPickerSearchActivity.this.actionBar, i, 0, i2, 0);
                int measuredHeight = PhotoPickerSearchActivity.this.actionBar.getMeasuredHeight();
                this.globalIgnoreLayout = true;
                int i3 = 0;
                int i4 = 0;
                while (i4 < PhotoPickerSearchActivity.this.viewPages.length) {
                    if (!(PhotoPickerSearchActivity.this.viewPages[i4] == null || PhotoPickerSearchActivity.this.viewPages[i4].listView == null)) {
                        PhotoPickerSearchActivity.this.viewPages[i4].listView.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f) + measuredHeight, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f));
                    }
                    i4++;
                }
                this.globalIgnoreLayout = false;
                measuredHeight = getChildCount();
                while (i3 < measuredHeight) {
                    View childAt = getChildAt(i3);
                    if (!(childAt == null || childAt.getVisibility() == 8 || childAt == PhotoPickerSearchActivity.this.actionBar)) {
                        measureChildWithMargins(childAt, i, 0, i2, 0);
                    }
                    i3++;
                }
            }

            /* Access modifiers changed, original: protected */
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
            /* JADX WARNING: Removed duplicated region for block: B:18:0x00a0  */
            public boolean checkTabsAnimationInProgress() {
                /*
                r7 = this;
                r0 = org.telegram.ui.PhotoPickerSearchActivity.this;
                r0 = r0.tabsAnimationInProgress;
                r1 = 0;
                if (r0 == 0) goto L_0x00c3;
            L_0x0009:
                r0 = org.telegram.ui.PhotoPickerSearchActivity.this;
                r0 = r0.backAnimation;
                r2 = -1;
                r3 = 0;
                r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
                r5 = 1;
                if (r0 == 0) goto L_0x0059;
            L_0x0016:
                r0 = org.telegram.ui.PhotoPickerSearchActivity.this;
                r0 = r0.viewPages;
                r0 = r0[r1];
                r0 = r0.getTranslationX();
                r0 = java.lang.Math.abs(r0);
                r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
                if (r0 >= 0) goto L_0x009d;
            L_0x002a:
                r0 = org.telegram.ui.PhotoPickerSearchActivity.this;
                r0 = r0.viewPages;
                r0 = r0[r1];
                r0.setTranslationX(r3);
                r0 = org.telegram.ui.PhotoPickerSearchActivity.this;
                r0 = r0.viewPages;
                r0 = r0[r5];
                r3 = org.telegram.ui.PhotoPickerSearchActivity.this;
                r3 = r3.viewPages;
                r3 = r3[r1];
                r3 = r3.getMeasuredWidth();
                r4 = org.telegram.ui.PhotoPickerSearchActivity.this;
                r4 = r4.animatingForward;
                if (r4 == 0) goto L_0x0052;
            L_0x0051:
                r2 = 1;
            L_0x0052:
                r3 = r3 * r2;
                r2 = (float) r3;
                r0.setTranslationX(r2);
                goto L_0x009e;
            L_0x0059:
                r0 = org.telegram.ui.PhotoPickerSearchActivity.this;
                r0 = r0.viewPages;
                r0 = r0[r5];
                r0 = r0.getTranslationX();
                r0 = java.lang.Math.abs(r0);
                r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
                if (r0 >= 0) goto L_0x009d;
            L_0x006d:
                r0 = org.telegram.ui.PhotoPickerSearchActivity.this;
                r0 = r0.viewPages;
                r0 = r0[r1];
                r4 = org.telegram.ui.PhotoPickerSearchActivity.this;
                r4 = r4.viewPages;
                r4 = r4[r1];
                r4 = r4.getMeasuredWidth();
                r6 = org.telegram.ui.PhotoPickerSearchActivity.this;
                r6 = r6.animatingForward;
                if (r6 == 0) goto L_0x008a;
            L_0x0089:
                goto L_0x008b;
            L_0x008a:
                r2 = 1;
            L_0x008b:
                r4 = r4 * r2;
                r2 = (float) r4;
                r0.setTranslationX(r2);
                r0 = org.telegram.ui.PhotoPickerSearchActivity.this;
                r0 = r0.viewPages;
                r0 = r0[r5];
                r0.setTranslationX(r3);
                goto L_0x009e;
            L_0x009d:
                r5 = 0;
            L_0x009e:
                if (r5 == 0) goto L_0x00bc;
            L_0x00a0:
                r0 = org.telegram.ui.PhotoPickerSearchActivity.this;
                r0 = r0.tabsAnimation;
                if (r0 == 0) goto L_0x00b7;
            L_0x00a8:
                r0 = org.telegram.ui.PhotoPickerSearchActivity.this;
                r0 = r0.tabsAnimation;
                r0.cancel();
                r0 = org.telegram.ui.PhotoPickerSearchActivity.this;
                r2 = 0;
                r0.tabsAnimation = r2;
            L_0x00b7:
                r0 = org.telegram.ui.PhotoPickerSearchActivity.this;
                r0.tabsAnimationInProgress = r1;
            L_0x00bc:
                r0 = org.telegram.ui.PhotoPickerSearchActivity.this;
                r0 = r0.tabsAnimationInProgress;
                return r0;
            L_0x00c3:
                return r1;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoPickerSearchActivity$AnonymousClass4.checkTabsAnimationInProgress():boolean");
            }

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return checkTabsAnimationInProgress() || PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.isAnimatingIndicator() || onTouchEvent(motionEvent);
            }

            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                PhotoPickerSearchActivity.this.backgroundPaint.setColor(Theme.getColor("windowBackgroundGray"));
                canvas.drawRect(0.0f, ((float) PhotoPickerSearchActivity.this.actionBar.getMeasuredHeight()) + PhotoPickerSearchActivity.this.actionBar.getTranslationY(), (float) getMeasuredWidth(), (float) getMeasuredHeight(), PhotoPickerSearchActivity.this.backgroundPaint);
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (PhotoPickerSearchActivity.this.parentLayout.checkTransitionAnimation() || checkTabsAnimationInProgress()) {
                    return false;
                }
                boolean z = true;
                VelocityTracker velocityTracker;
                if (motionEvent != null && motionEvent.getAction() == 0 && !this.startedTracking && !this.maybeStartTracking) {
                    this.startedTrackingPointerId = motionEvent.getPointerId(0);
                    this.maybeStartTracking = true;
                    this.startedTrackingX = (int) motionEvent.getX();
                    this.startedTrackingY = (int) motionEvent.getY();
                    velocityTracker = this.velocityTracker;
                    if (velocityTracker != null) {
                        velocityTracker.clear();
                    }
                } else if (motionEvent != null && motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    int x = (int) (motionEvent.getX() - ((float) this.startedTrackingX));
                    int abs = Math.abs(((int) motionEvent.getY()) - this.startedTrackingY);
                    this.velocityTracker.addMovement(motionEvent);
                    if (this.startedTracking && ((PhotoPickerSearchActivity.this.animatingForward && x > 0) || (!PhotoPickerSearchActivity.this.animatingForward && x < 0))) {
                        if (!prepareForMoving(motionEvent, x < 0)) {
                            this.maybeStartTracking = true;
                            this.startedTracking = false;
                        }
                    }
                    if (this.maybeStartTracking && !this.startedTracking) {
                        if (((float) Math.abs(x)) >= AndroidUtilities.getPixelsInCM(0.3f, true) && Math.abs(x) / 3 > abs) {
                            if (x >= 0) {
                                z = false;
                            }
                            prepareForMoving(motionEvent, z);
                        }
                    } else if (this.startedTracking) {
                        if (PhotoPickerSearchActivity.this.animatingForward) {
                            PhotoPickerSearchActivity.this.viewPages[0].setTranslationX((float) x);
                            PhotoPickerSearchActivity.this.viewPages[1].setTranslationX((float) (PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth() + x));
                        } else {
                            PhotoPickerSearchActivity.this.viewPages[0].setTranslationX((float) x);
                            PhotoPickerSearchActivity.this.viewPages[1].setTranslationX((float) (x - PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()));
                        }
                        PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.selectTabWithId(PhotoPickerSearchActivity.this.viewPages[1].selectedType, ((float) Math.abs(x)) / ((float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()));
                    }
                } else if (motionEvent != null && motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6)) {
                    float xVelocity;
                    float yVelocity;
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    this.velocityTracker.computeCurrentVelocity(1000, (float) PhotoPickerSearchActivity.this.maximumVelocity);
                    if (!this.startedTracking) {
                        xVelocity = this.velocityTracker.getXVelocity();
                        yVelocity = this.velocityTracker.getYVelocity();
                        if (Math.abs(xVelocity) >= 3000.0f && Math.abs(xVelocity) > Math.abs(yVelocity)) {
                            prepareForMoving(motionEvent, xVelocity < 0.0f);
                        }
                    }
                    if (this.startedTracking) {
                        int round;
                        float x2 = PhotoPickerSearchActivity.this.viewPages[0].getX();
                        PhotoPickerSearchActivity.this.tabsAnimation = new AnimatorSet();
                        xVelocity = this.velocityTracker.getXVelocity();
                        yVelocity = this.velocityTracker.getYVelocity();
                        PhotoPickerSearchActivity photoPickerSearchActivity = PhotoPickerSearchActivity.this;
                        boolean z2 = Math.abs(x2) < ((float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(xVelocity) < 3500.0f || Math.abs(xVelocity) < Math.abs(yVelocity));
                        photoPickerSearchActivity.backAnimation = z2;
                        AnimatorSet access$2000;
                        Animator[] animatorArr;
                        if (PhotoPickerSearchActivity.this.backAnimation) {
                            x2 = Math.abs(x2);
                            if (PhotoPickerSearchActivity.this.animatingForward) {
                                access$2000 = PhotoPickerSearchActivity.this.tabsAnimation;
                                animatorArr = new Animator[2];
                                animatorArr[0] = ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{0.0f});
                                animatorArr[1] = ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{(float) PhotoPickerSearchActivity.this.viewPages[1].getMeasuredWidth()});
                                access$2000.playTogether(animatorArr);
                            } else {
                                access$2000 = PhotoPickerSearchActivity.this.tabsAnimation;
                                animatorArr = new Animator[2];
                                animatorArr[0] = ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{0.0f});
                                animatorArr[1] = ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{(float) (-PhotoPickerSearchActivity.this.viewPages[1].getMeasuredWidth())});
                                access$2000.playTogether(animatorArr);
                            }
                        } else {
                            x2 = ((float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()) - Math.abs(x2);
                            if (PhotoPickerSearchActivity.this.animatingForward) {
                                access$2000 = PhotoPickerSearchActivity.this.tabsAnimation;
                                animatorArr = new Animator[2];
                                animatorArr[0] = ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{(float) (-PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth())});
                                animatorArr[1] = ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{0.0f});
                                access$2000.playTogether(animatorArr);
                            } else {
                                access$2000 = PhotoPickerSearchActivity.this.tabsAnimation;
                                animatorArr = new Animator[2];
                                animatorArr[0] = ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{(float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()});
                                animatorArr[1] = ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{0.0f});
                                access$2000.playTogether(animatorArr);
                            }
                        }
                        PhotoPickerSearchActivity.this.tabsAnimation.setInterpolator(PhotoPickerSearchActivity.interpolator);
                        int measuredWidth = getMeasuredWidth();
                        float f = (float) (measuredWidth / 2);
                        f += AndroidUtilities.distanceInfluenceForSnapDuration(Math.min(1.0f, (x2 * 1.0f) / ((float) measuredWidth))) * f;
                        float abs2 = Math.abs(xVelocity);
                        if (abs2 > 0.0f) {
                            round = Math.round(Math.abs(f / abs2) * 1000.0f) * 4;
                        } else {
                            round = (int) (((x2 / ((float) getMeasuredWidth())) + 1.0f) * 100.0f);
                        }
                        PhotoPickerSearchActivity.this.tabsAnimation.setDuration((long) Math.max(150, Math.min(round, 600)));
                        PhotoPickerSearchActivity.this.tabsAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                PhotoPickerSearchActivity.this.tabsAnimation = null;
                                if (PhotoPickerSearchActivity.this.backAnimation) {
                                    PhotoPickerSearchActivity.this.viewPages[1].setVisibility(8);
                                } else {
                                    ViewPage viewPage = PhotoPickerSearchActivity.this.viewPages[0];
                                    PhotoPickerSearchActivity.this.viewPages[0] = PhotoPickerSearchActivity.this.viewPages[1];
                                    PhotoPickerSearchActivity.this.viewPages[1] = viewPage;
                                    PhotoPickerSearchActivity.this.viewPages[1].setVisibility(8);
                                    PhotoPickerSearchActivity photoPickerSearchActivity = PhotoPickerSearchActivity.this;
                                    photoPickerSearchActivity.swipeBackEnabled = photoPickerSearchActivity.viewPages[0].selectedType == PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.getFirstTabId();
                                    PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.selectTabWithId(PhotoPickerSearchActivity.this.viewPages[0].selectedType, 1.0f);
                                }
                                PhotoPickerSearchActivity.this.tabsAnimationInProgress = false;
                                AnonymousClass4.this.maybeStartTracking = false;
                                AnonymousClass4.this.startedTracking = false;
                                PhotoPickerSearchActivity.this.actionBar.setEnabled(true);
                                PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
                            }
                        });
                        PhotoPickerSearchActivity.this.tabsAnimation.start();
                        PhotoPickerSearchActivity.this.tabsAnimationInProgress = true;
                    } else {
                        this.maybeStartTracking = false;
                        this.startedTracking = false;
                        PhotoPickerSearchActivity.this.actionBar.setEnabled(true);
                        PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
                    }
                    velocityTracker = this.velocityTracker;
                    if (velocityTracker != null) {
                        velocityTracker.recycle();
                        this.velocityTracker = null;
                    }
                }
                return this.startedTracking;
            }
        };
        this.fragmentView = anonymousClass4;
        anonymousClass4.setWillNotDraw(false);
        this.imagesSearch.setParentFragment(this);
        PickerBottomLayout pickerBottomLayout = this.imagesSearch.getPickerBottomLayout();
        ((ViewGroup) pickerBottomLayout.getParent()).removeView(pickerBottomLayout);
        this.gifsSearch.setPickerBottomLayout(pickerBottomLayout);
        this.gifsSearch.setParentFragment(this);
        int i = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (i >= viewPageArr.length) {
                break;
            }
            viewPageArr[i] = new ViewPage(context) {
                public void setTranslationX(float f) {
                    super.setTranslationX(f);
                    if (PhotoPickerSearchActivity.this.tabsAnimationInProgress && PhotoPickerSearchActivity.this.viewPages[0] == this) {
                        PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.selectTabWithId(PhotoPickerSearchActivity.this.viewPages[1].selectedType, Math.abs(PhotoPickerSearchActivity.this.viewPages[0].getTranslationX()) / ((float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()));
                    }
                }
            };
            anonymousClass4.addView(this.viewPages[i], LayoutHelper.createFrame(-1, -1.0f));
            if (i == 0) {
                this.viewPages[i].parentFragment = this.imagesSearch;
                this.viewPages[i].listView = this.imagesSearch.getListView();
            } else if (i == 1) {
                this.viewPages[i].parentFragment = this.gifsSearch;
                this.viewPages[i].listView = this.gifsSearch.getListView();
                this.viewPages[i].setVisibility(8);
            }
            ViewPage[] viewPageArr2 = this.viewPages;
            viewPageArr2[i].fragmentView = (FrameLayout) viewPageArr2[i].parentFragment.getFragmentView();
            this.viewPages[i].listView.setClipToPadding(false);
            viewPageArr2 = this.viewPages;
            viewPageArr2[i].actionBar = viewPageArr2[i].parentFragment.getActionBar();
            viewPageArr2 = this.viewPages;
            viewPageArr2[i].addView(viewPageArr2[i].fragmentView, LayoutHelper.createFrame(-1, -1.0f));
            ViewPage[] viewPageArr3 = this.viewPages;
            viewPageArr3[i].addView(viewPageArr3[i].actionBar, LayoutHelper.createFrame(-1, -2.0f));
            this.viewPages[i].actionBar.setVisibility(8);
            final OnScrollListener onScrollListener = this.viewPages[i].listView.getOnScrollListener();
            this.viewPages[i].listView.setOnScrollListener(new OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                    onScrollListener.onScrollStateChanged(recyclerView, i);
                    if (i != 1) {
                        int i2 = (int) (-PhotoPickerSearchActivity.this.actionBar.getTranslationY());
                        i = ActionBar.getCurrentActionBarHeight();
                        if (i2 != 0 && i2 != i) {
                            if (i2 < i / 2) {
                                PhotoPickerSearchActivity.this.viewPages[0].listView.smoothScrollBy(0, -i2);
                            } else {
                                PhotoPickerSearchActivity.this.viewPages[0].listView.smoothScrollBy(0, i - i2);
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
            i++;
        }
        anonymousClass4.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        View view = new View(context);
        view.setBackgroundColor(Theme.getColor("dialogShadowLine"));
        anonymousClass4.addView(view, LayoutHelper.createFrame(-1, 1.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        anonymousClass4.addView(pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 83));
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
            getParentActivity().getWindow().setSoftInputMode(32);
        }
        PhotoPickerActivity photoPickerActivity = this.imagesSearch;
        if (photoPickerActivity != null) {
            photoPickerActivity.onResume();
        }
        photoPickerActivity = this.gifsSearch;
        if (photoPickerActivity != null) {
            photoPickerActivity.onResume();
        }
    }

    public void onPause() {
        super.onPause();
        PhotoPickerActivity photoPickerActivity = this.imagesSearch;
        if (photoPickerActivity != null) {
            photoPickerActivity.onPause();
        }
        photoPickerActivity = this.gifsSearch;
        if (photoPickerActivity != null) {
            photoPickerActivity.onPause();
        }
    }

    public void onFragmentDestroy() {
        PhotoPickerActivity photoPickerActivity = this.imagesSearch;
        if (photoPickerActivity != null) {
            photoPickerActivity.onFragmentDestroy();
        }
        photoPickerActivity = this.gifsSearch;
        if (photoPickerActivity != null) {
            photoPickerActivity.onFragmentDestroy();
        }
        super.onFragmentDestroy();
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        PhotoPickerActivity photoPickerActivity = this.imagesSearch;
        if (photoPickerActivity != null) {
            photoPickerActivity.onConfigurationChanged(configuration);
        }
        photoPickerActivity = this.gifsSearch;
        if (photoPickerActivity != null) {
            photoPickerActivity.onConfigurationChanged(configuration);
        }
    }

    private void setScrollY(float f) {
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

    public void setDelegate(PhotoPickerActivityDelegate photoPickerActivityDelegate) {
        this.imagesSearch.setDelegate(photoPickerActivityDelegate);
        this.gifsSearch.setDelegate(photoPickerActivityDelegate);
    }

    public void setMaxSelectedPhotos(int i, boolean z) {
        this.imagesSearch.setMaxSelectedPhotos(i, z);
        this.gifsSearch.setMaxSelectedPhotos(i, z);
    }

    private void updateTabs() {
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip = this.scrollSlidingTextTabStrip;
        if (scrollSlidingTextTabStrip != null) {
            scrollSlidingTextTabStrip.addTextTab(0, LocaleController.getString("ImagesTab", NUM));
            this.scrollSlidingTextTabStrip.addTextTab(1, LocaleController.getString("GifsTab", NUM));
            this.scrollSlidingTextTabStrip.setVisibility(0);
            this.actionBar.setExtraHeight(AndroidUtilities.dp(44.0f));
            int currentTabId = this.scrollSlidingTextTabStrip.getCurrentTabId();
            if (currentTabId >= 0) {
                this.viewPages[0].selectedType = currentTabId;
            }
            this.scrollSlidingTextTabStrip.finishAddingTabs();
        }
    }

    private void switchToCurrentSelectedMode(boolean z) {
        ViewPage[] viewPageArr;
        int i = 0;
        while (true) {
            viewPageArr = this.viewPages;
            if (i >= viewPageArr.length) {
                break;
            }
            viewPageArr[i].listView.stopScroll();
            i++;
        }
        viewPageArr[z].listView.getAdapter();
        this.viewPages[z].listView.setPinnedHeaderShadowDrawable(null);
        if (this.actionBar.getTranslationY() != 0.0f) {
            ((LinearLayoutManager) this.viewPages[z].listView.getLayoutManager()).scrollToPositionWithOffset(0, (int) this.actionBar.getTranslationY());
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "dialogButtonSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "chat_messagePanelHint"));
        arrayList.add(new ThemeDescription(this.searchItem.getSearchField(), ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextView.class}, null, null, null, "chat_attachActiveTab"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, "chat_attachUnactiveTab"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{TextView.class}, null, null, null, "dialogButtonSelector"));
        arrayList.add(new ThemeDescription(null, 0, null, null, new Drawable[]{this.scrollSlidingTextTabStrip.getSelectorDrawable()}, null, "chat_attachActiveTab"));
        Collections.addAll(arrayList, this.imagesSearch.getThemeDescriptions());
        Collections.addAll(arrayList, this.gifsSearch.getThemeDescriptions());
        return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[0]);
    }
}
