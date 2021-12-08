package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScrollSlidingTextTabStrip;

public class DialogOrContactPickerActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public static final Interpolator interpolator = DialogOrContactPickerActivity$$ExternalSyntheticLambda1.INSTANCE;
    private static final int search_button = 0;
    /* access modifiers changed from: private */
    public boolean animatingForward;
    /* access modifiers changed from: private */
    public boolean backAnimation;
    /* access modifiers changed from: private */
    public Paint backgroundPaint = new Paint();
    /* access modifiers changed from: private */
    public ContactsActivity contactsActivity;
    /* access modifiers changed from: private */
    public DialogsActivity dialogsActivity;
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

    private static class ViewPage extends FrameLayout {
        /* access modifiers changed from: private */
        public ActionBar actionBar;
        /* access modifiers changed from: private */
        public FrameLayout fragmentView;
        /* access modifiers changed from: private */
        public RecyclerListView listView;
        /* access modifiers changed from: private */
        public RecyclerListView listView2;
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

    public DialogOrContactPickerActivity() {
        Bundle args = new Bundle();
        args.putBoolean("onlySelect", true);
        args.putBoolean("checkCanWrite", false);
        args.putBoolean("resetDelegate", false);
        args.putInt("dialogsType", 9);
        DialogsActivity dialogsActivity2 = new DialogsActivity(args);
        this.dialogsActivity = dialogsActivity2;
        dialogsActivity2.setDelegate(new DialogOrContactPickerActivity$$ExternalSyntheticLambda3(this));
        this.dialogsActivity.onFragmentCreate();
        Bundle args2 = new Bundle();
        args2.putBoolean("onlyUsers", true);
        args2.putBoolean("destroyAfterSelect", true);
        args2.putBoolean("returnAsResult", true);
        args2.putBoolean("disableSections", true);
        args2.putBoolean("needFinishFragment", false);
        args2.putBoolean("resetDelegate", false);
        args2.putBoolean("allowSelf", false);
        ContactsActivity contactsActivity2 = new ContactsActivity(args2);
        this.contactsActivity = contactsActivity2;
        contactsActivity2.setDelegate(new DialogOrContactPickerActivity$$ExternalSyntheticLambda2(this));
        this.contactsActivity.onFragmentCreate();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-DialogOrContactPickerActivity  reason: not valid java name */
    public /* synthetic */ void m2833lambda$new$1$orgtelegramuiDialogOrContactPickerActivity(DialogsActivity fragment, ArrayList dids, CharSequence message, boolean param) {
        if (!dids.isEmpty()) {
            long did = ((Long) dids.get(0)).longValue();
            if (DialogObject.isUserDialog(did)) {
                showBlockAlert(getMessagesController().getUser(Long.valueOf(did)));
            }
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-DialogOrContactPickerActivity  reason: not valid java name */
    public /* synthetic */ void m2834lambda$new$2$orgtelegramuiDialogOrContactPickerActivity(TLRPC.User user, String param, ContactsActivity activity) {
        showBlockAlert(user);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setTitle(LocaleController.getString("BlockUserMultiTitle", NUM));
        boolean z = false;
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
                    DialogOrContactPickerActivity.this.finishFragment();
                }
            }
        });
        this.hasOwnBackground = true;
        ActionBarMenuItem actionBarMenuItemSearchListener = this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                DialogOrContactPickerActivity.this.dialogsActivity.getActionBar().openSearchField("", false);
                DialogOrContactPickerActivity.this.contactsActivity.getActionBar().openSearchField("", false);
                DialogOrContactPickerActivity.this.searchItem.getSearchField().requestFocus();
            }

            public void onSearchCollapse() {
                DialogOrContactPickerActivity.this.dialogsActivity.getActionBar().closeSearchField(false);
                DialogOrContactPickerActivity.this.contactsActivity.getActionBar().closeSearchField(false);
            }

            public void onTextChanged(EditText editText) {
                DialogOrContactPickerActivity.this.dialogsActivity.getActionBar().setSearchFieldText(editText.getText().toString());
                DialogOrContactPickerActivity.this.contactsActivity.getActionBar().setSearchFieldText(editText.getText().toString());
            }
        });
        this.searchItem = actionBarMenuItemSearchListener;
        actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString("Search", NUM));
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip2 = new ScrollSlidingTextTabStrip(context);
        this.scrollSlidingTextTabStrip = scrollSlidingTextTabStrip2;
        scrollSlidingTextTabStrip2.setUseSameWidth(true);
        this.actionBar.addView(this.scrollSlidingTextTabStrip, LayoutHelper.createFrame(-1, 44, 83));
        this.scrollSlidingTextTabStrip.setDelegate(new ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate() {
            public /* synthetic */ void onSamePageSelected() {
                ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate.CC.$default$onSamePageSelected(this);
            }

            public void onPageSelected(int id, boolean forward) {
                if (DialogOrContactPickerActivity.this.viewPages[0].selectedType != id) {
                    DialogOrContactPickerActivity dialogOrContactPickerActivity = DialogOrContactPickerActivity.this;
                    boolean unused = dialogOrContactPickerActivity.swipeBackEnabled = id == dialogOrContactPickerActivity.scrollSlidingTextTabStrip.getFirstTabId();
                    int unused2 = DialogOrContactPickerActivity.this.viewPages[1].selectedType = id;
                    DialogOrContactPickerActivity.this.viewPages[1].setVisibility(0);
                    DialogOrContactPickerActivity.this.switchToCurrentSelectedMode(true);
                    boolean unused3 = DialogOrContactPickerActivity.this.animatingForward = forward;
                }
            }

            public void onPageScrolled(float progress) {
                if (progress != 1.0f || DialogOrContactPickerActivity.this.viewPages[1].getVisibility() == 0) {
                    if (DialogOrContactPickerActivity.this.animatingForward) {
                        DialogOrContactPickerActivity.this.viewPages[0].setTranslationX((-progress) * ((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()));
                        DialogOrContactPickerActivity.this.viewPages[1].setTranslationX(((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()) - (((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()) * progress));
                    } else {
                        DialogOrContactPickerActivity.this.viewPages[0].setTranslationX(((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()) * progress);
                        DialogOrContactPickerActivity.this.viewPages[1].setTranslationX((((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()) * progress) - ((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()));
                    }
                    if (progress == 1.0f) {
                        ViewPage tempPage = DialogOrContactPickerActivity.this.viewPages[0];
                        DialogOrContactPickerActivity.this.viewPages[0] = DialogOrContactPickerActivity.this.viewPages[1];
                        DialogOrContactPickerActivity.this.viewPages[1] = tempPage;
                        DialogOrContactPickerActivity.this.viewPages[1].setVisibility(8);
                    }
                }
            }
        });
        this.maximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        FrameLayout r4 = new FrameLayout(context) {
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
                int id = DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.getNextPageId(forward);
                if (id < 0) {
                    return false;
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                this.maybeStartTracking = false;
                this.startedTracking = true;
                this.startedTrackingX = (int) ev.getX();
                DialogOrContactPickerActivity.this.actionBar.setEnabled(false);
                DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.setEnabled(false);
                int unused = DialogOrContactPickerActivity.this.viewPages[1].selectedType = id;
                DialogOrContactPickerActivity.this.viewPages[1].setVisibility(0);
                boolean unused2 = DialogOrContactPickerActivity.this.animatingForward = forward;
                DialogOrContactPickerActivity.this.switchToCurrentSelectedMode(true);
                if (forward) {
                    DialogOrContactPickerActivity.this.viewPages[1].setTranslationX((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth());
                } else {
                    DialogOrContactPickerActivity.this.viewPages[1].setTranslationX((float) (-DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()));
                }
                return true;
            }

            public void forceHasOverlappingRendering(boolean hasOverlappingRendering) {
                super.forceHasOverlappingRendering(hasOverlappingRendering);
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
                measureChildWithMargins(DialogOrContactPickerActivity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
                int actionBarHeight = DialogOrContactPickerActivity.this.actionBar.getMeasuredHeight();
                this.globalIgnoreLayout = true;
                for (int a = 0; a < DialogOrContactPickerActivity.this.viewPages.length; a++) {
                    if (DialogOrContactPickerActivity.this.viewPages[a] != null) {
                        if (DialogOrContactPickerActivity.this.viewPages[a].listView != null) {
                            DialogOrContactPickerActivity.this.viewPages[a].listView.setPadding(0, actionBarHeight, 0, 0);
                        }
                        if (DialogOrContactPickerActivity.this.viewPages[a].listView2 != null) {
                            DialogOrContactPickerActivity.this.viewPages[a].listView2.setPadding(0, actionBarHeight, 0, 0);
                        }
                    }
                }
                this.globalIgnoreLayout = false;
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (!(child == null || child.getVisibility() == 8 || child == DialogOrContactPickerActivity.this.actionBar)) {
                        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    }
                }
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (DialogOrContactPickerActivity.this.parentLayout != null) {
                    DialogOrContactPickerActivity.this.parentLayout.drawHeaderShadow(canvas, DialogOrContactPickerActivity.this.actionBar.getMeasuredHeight() + ((int) DialogOrContactPickerActivity.this.actionBar.getTranslationY()));
                }
            }

            public void requestLayout() {
                if (!this.globalIgnoreLayout) {
                    super.requestLayout();
                }
            }

            public boolean checkTabsAnimationInProgress() {
                if (!DialogOrContactPickerActivity.this.tabsAnimationInProgress) {
                    return false;
                }
                boolean cancel = false;
                int i = -1;
                if (DialogOrContactPickerActivity.this.backAnimation) {
                    if (Math.abs(DialogOrContactPickerActivity.this.viewPages[0].getTranslationX()) < 1.0f) {
                        DialogOrContactPickerActivity.this.viewPages[0].setTranslationX(0.0f);
                        ViewPage viewPage = DialogOrContactPickerActivity.this.viewPages[1];
                        int measuredWidth = DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth();
                        if (DialogOrContactPickerActivity.this.animatingForward) {
                            i = 1;
                        }
                        viewPage.setTranslationX((float) (measuredWidth * i));
                        cancel = true;
                    }
                } else if (Math.abs(DialogOrContactPickerActivity.this.viewPages[1].getTranslationX()) < 1.0f) {
                    ViewPage viewPage2 = DialogOrContactPickerActivity.this.viewPages[0];
                    int measuredWidth2 = DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth();
                    if (!DialogOrContactPickerActivity.this.animatingForward) {
                        i = 1;
                    }
                    viewPage2.setTranslationX((float) (measuredWidth2 * i));
                    DialogOrContactPickerActivity.this.viewPages[1].setTranslationX(0.0f);
                    cancel = true;
                }
                if (cancel) {
                    if (DialogOrContactPickerActivity.this.tabsAnimation != null) {
                        DialogOrContactPickerActivity.this.tabsAnimation.cancel();
                        AnimatorSet unused = DialogOrContactPickerActivity.this.tabsAnimation = null;
                    }
                    boolean unused2 = DialogOrContactPickerActivity.this.tabsAnimationInProgress = false;
                }
                return DialogOrContactPickerActivity.this.tabsAnimationInProgress;
            }

            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return checkTabsAnimationInProgress() || DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.isAnimatingIndicator() || onTouchEvent(ev);
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                DialogOrContactPickerActivity.this.backgroundPaint.setColor(Theme.getColor("windowBackgroundWhite"));
                canvas.drawRect(0.0f, ((float) DialogOrContactPickerActivity.this.actionBar.getMeasuredHeight()) + DialogOrContactPickerActivity.this.actionBar.getTranslationY(), (float) getMeasuredWidth(), (float) getMeasuredHeight(), DialogOrContactPickerActivity.this.backgroundPaint);
            }

            public boolean onTouchEvent(MotionEvent ev) {
                float velY;
                float velX;
                float dx;
                int duration;
                boolean z = false;
                if (DialogOrContactPickerActivity.this.parentLayout.checkTransitionAnimation() || checkTabsAnimationInProgress()) {
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
                    if (this.startedTracking && ((DialogOrContactPickerActivity.this.animatingForward && dx2 > 0) || (!DialogOrContactPickerActivity.this.animatingForward && dx2 < 0))) {
                        if (!prepareForMoving(ev, dx2 < 0)) {
                            this.maybeStartTracking = true;
                            this.startedTracking = false;
                            DialogOrContactPickerActivity.this.viewPages[0].setTranslationX(0.0f);
                            DialogOrContactPickerActivity.this.viewPages[1].setTranslationX((float) (DialogOrContactPickerActivity.this.animatingForward ? DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth() : -DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()));
                            DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DialogOrContactPickerActivity.this.viewPages[1].selectedType, 0.0f);
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
                        DialogOrContactPickerActivity.this.viewPages[0].setTranslationX((float) dx2);
                        if (DialogOrContactPickerActivity.this.animatingForward) {
                            DialogOrContactPickerActivity.this.viewPages[1].setTranslationX((float) (DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth() + dx2));
                        } else {
                            DialogOrContactPickerActivity.this.viewPages[1].setTranslationX((float) (dx2 - DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()));
                        }
                        DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DialogOrContactPickerActivity.this.viewPages[1].selectedType, ((float) Math.abs(dx2)) / ((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()));
                    }
                } else if (ev == null || (ev.getPointerId(0) == this.startedTrackingPointerId && (ev.getAction() == 3 || ev.getAction() == 1 || ev.getAction() == 6))) {
                    this.velocityTracker.computeCurrentVelocity(1000, (float) DialogOrContactPickerActivity.this.maximumVelocity);
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
                        float x = DialogOrContactPickerActivity.this.viewPages[0].getX();
                        AnimatorSet unused = DialogOrContactPickerActivity.this.tabsAnimation = new AnimatorSet();
                        boolean unused2 = DialogOrContactPickerActivity.this.backAnimation = Math.abs(x) < ((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(velX) < 3500.0f || Math.abs(velX) < Math.abs(velY));
                        if (DialogOrContactPickerActivity.this.backAnimation) {
                            dx = Math.abs(x);
                            if (DialogOrContactPickerActivity.this.animatingForward) {
                                DialogOrContactPickerActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{(float) DialogOrContactPickerActivity.this.viewPages[1].getMeasuredWidth()})});
                            } else {
                                DialogOrContactPickerActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{(float) (-DialogOrContactPickerActivity.this.viewPages[1].getMeasuredWidth())})});
                            }
                        } else {
                            dx = ((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()) - Math.abs(x);
                            if (DialogOrContactPickerActivity.this.animatingForward) {
                                DialogOrContactPickerActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{(float) (-DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth())}), ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{0.0f})});
                            } else {
                                DialogOrContactPickerActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{(float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()}), ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{0.0f})});
                            }
                        }
                        DialogOrContactPickerActivity.this.tabsAnimation.setInterpolator(DialogOrContactPickerActivity.interpolator);
                        int width = getMeasuredWidth();
                        int halfWidth = width / 2;
                        float distance = ((float) halfWidth) + (((float) halfWidth) * AndroidUtilities.distanceInfluenceForSnapDuration(Math.min(1.0f, (dx * 1.0f) / ((float) width))));
                        float velX2 = Math.abs(velX);
                        if (velX2 > 0.0f) {
                            duration = Math.round(Math.abs(distance / velX2) * 1000.0f) * 4;
                        } else {
                            duration = (int) ((1.0f + (dx / ((float) getMeasuredWidth()))) * 100.0f);
                        }
                        DialogOrContactPickerActivity.this.tabsAnimation.setDuration((long) Math.max(150, Math.min(duration, 600)));
                        DialogOrContactPickerActivity.this.tabsAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                AnimatorSet unused = DialogOrContactPickerActivity.this.tabsAnimation = null;
                                if (DialogOrContactPickerActivity.this.backAnimation) {
                                    DialogOrContactPickerActivity.this.viewPages[1].setVisibility(8);
                                } else {
                                    ViewPage tempPage = DialogOrContactPickerActivity.this.viewPages[0];
                                    DialogOrContactPickerActivity.this.viewPages[0] = DialogOrContactPickerActivity.this.viewPages[1];
                                    DialogOrContactPickerActivity.this.viewPages[1] = tempPage;
                                    DialogOrContactPickerActivity.this.viewPages[1].setVisibility(8);
                                    boolean unused2 = DialogOrContactPickerActivity.this.swipeBackEnabled = DialogOrContactPickerActivity.this.viewPages[0].selectedType == DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.getFirstTabId();
                                    DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DialogOrContactPickerActivity.this.viewPages[0].selectedType, 1.0f);
                                }
                                boolean unused3 = DialogOrContactPickerActivity.this.tabsAnimationInProgress = false;
                                boolean unused4 = AnonymousClass4.this.maybeStartTracking = false;
                                boolean unused5 = AnonymousClass4.this.startedTracking = false;
                                DialogOrContactPickerActivity.this.actionBar.setEnabled(true);
                                DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
                            }
                        });
                        DialogOrContactPickerActivity.this.tabsAnimation.start();
                        boolean unused3 = DialogOrContactPickerActivity.this.tabsAnimationInProgress = true;
                        this.startedTracking = false;
                    } else {
                        this.maybeStartTracking = false;
                        DialogOrContactPickerActivity.this.actionBar.setEnabled(true);
                        DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
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
        FrameLayout frameLayout = r4;
        this.fragmentView = r4;
        frameLayout.setWillNotDraw(false);
        this.dialogsActivity.setParentFragment(this);
        this.contactsActivity.setParentFragment(this);
        int a = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (a >= viewPageArr.length) {
                break;
            }
            viewPageArr[a] = new ViewPage(context) {
                public void setTranslationX(float translationX) {
                    super.setTranslationX(translationX);
                    if (DialogOrContactPickerActivity.this.tabsAnimationInProgress && DialogOrContactPickerActivity.this.viewPages[0] == this) {
                        DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DialogOrContactPickerActivity.this.viewPages[1].selectedType, Math.abs(DialogOrContactPickerActivity.this.viewPages[0].getTranslationX()) / ((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()));
                    }
                }
            };
            frameLayout.addView(this.viewPages[a], LayoutHelper.createFrame(-1, -1.0f));
            if (a == 0) {
                BaseFragment unused = this.viewPages[a].parentFragment = this.dialogsActivity;
                RecyclerListView unused2 = this.viewPages[a].listView = this.dialogsActivity.getListView();
                RecyclerListView unused3 = this.viewPages[a].listView2 = this.dialogsActivity.getSearchListView();
            } else if (a == 1) {
                BaseFragment unused4 = this.viewPages[a].parentFragment = this.contactsActivity;
                RecyclerListView unused5 = this.viewPages[a].listView = this.contactsActivity.getListView();
                this.viewPages[a].setVisibility(8);
            }
            this.viewPages[a].listView.setScrollingTouchSlop(1);
            ViewPage[] viewPageArr2 = this.viewPages;
            FrameLayout unused6 = viewPageArr2[a].fragmentView = (FrameLayout) viewPageArr2[a].parentFragment.getFragmentView();
            ViewPage[] viewPageArr3 = this.viewPages;
            ActionBar unused7 = viewPageArr3[a].actionBar = viewPageArr3[a].parentFragment.getActionBar();
            ViewPage[] viewPageArr4 = this.viewPages;
            viewPageArr4[a].addView(viewPageArr4[a].fragmentView, LayoutHelper.createFrame(-1, -1.0f));
            ViewPage[] viewPageArr5 = this.viewPages;
            viewPageArr5[a].addView(viewPageArr5[a].actionBar, LayoutHelper.createFrame(-1, -2.0f));
            this.viewPages[a].actionBar.setVisibility(8);
            int i = 0;
            while (i < 2) {
                ViewPage[] viewPageArr6 = this.viewPages;
                RecyclerListView listView = i == 0 ? viewPageArr6[a].listView : viewPageArr6[a].listView2;
                if (listView != null) {
                    listView.setClipToPadding(false);
                    final RecyclerView.OnScrollListener onScrollListener = listView.getOnScrollListener();
                    listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            onScrollListener.onScrollStateChanged(recyclerView, newState);
                            if (newState != 1) {
                                int scrollY = (int) (-DialogOrContactPickerActivity.this.actionBar.getTranslationY());
                                int actionBarHeight = ActionBar.getCurrentActionBarHeight();
                                if (scrollY != 0 && scrollY != actionBarHeight) {
                                    if (scrollY < actionBarHeight / 2) {
                                        DialogOrContactPickerActivity.this.viewPages[0].listView.smoothScrollBy(0, -scrollY);
                                        if (DialogOrContactPickerActivity.this.viewPages[0].listView2 != null) {
                                            DialogOrContactPickerActivity.this.viewPages[0].listView2.smoothScrollBy(0, -scrollY);
                                            return;
                                        }
                                        return;
                                    }
                                    DialogOrContactPickerActivity.this.viewPages[0].listView.smoothScrollBy(0, actionBarHeight - scrollY);
                                    if (DialogOrContactPickerActivity.this.viewPages[0].listView2 != null) {
                                        DialogOrContactPickerActivity.this.viewPages[0].listView2.smoothScrollBy(0, actionBarHeight - scrollY);
                                    }
                                }
                            }
                        }

                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            onScrollListener.onScrolled(recyclerView, dx, dy);
                            if (recyclerView == DialogOrContactPickerActivity.this.viewPages[0].listView || recyclerView == DialogOrContactPickerActivity.this.viewPages[0].listView2) {
                                float currentTranslation = DialogOrContactPickerActivity.this.actionBar.getTranslationY();
                                float newTranslation = currentTranslation - ((float) dy);
                                if (newTranslation < ((float) (-ActionBar.getCurrentActionBarHeight()))) {
                                    newTranslation = (float) (-ActionBar.getCurrentActionBarHeight());
                                } else if (newTranslation > 0.0f) {
                                    newTranslation = 0.0f;
                                }
                                if (newTranslation != currentTranslation) {
                                    DialogOrContactPickerActivity.this.setScrollY(newTranslation);
                                }
                            }
                        }
                    });
                }
                i++;
            }
            a++;
        }
        frameLayout.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
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
        DialogsActivity dialogsActivity2 = this.dialogsActivity;
        if (dialogsActivity2 != null) {
            dialogsActivity2.onResume();
        }
        ContactsActivity contactsActivity2 = this.contactsActivity;
        if (contactsActivity2 != null) {
            contactsActivity2.onResume();
        }
    }

    public void onPause() {
        super.onPause();
        DialogsActivity dialogsActivity2 = this.dialogsActivity;
        if (dialogsActivity2 != null) {
            dialogsActivity2.onPause();
        }
        ContactsActivity contactsActivity2 = this.contactsActivity;
        if (contactsActivity2 != null) {
            contactsActivity2.onPause();
        }
    }

    public boolean isSwipeBackEnabled(MotionEvent event) {
        return this.swipeBackEnabled;
    }

    public void onFragmentDestroy() {
        DialogsActivity dialogsActivity2 = this.dialogsActivity;
        if (dialogsActivity2 != null) {
            dialogsActivity2.onFragmentDestroy();
        }
        ContactsActivity contactsActivity2 = this.contactsActivity;
        if (contactsActivity2 != null) {
            contactsActivity2.onFragmentDestroy();
        }
        super.onFragmentDestroy();
    }

    /* access modifiers changed from: private */
    public void setScrollY(float value) {
        this.actionBar.setTranslationY(value);
        int a = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (a < viewPageArr.length) {
                viewPageArr[a].listView.setPinnedSectionOffsetY((int) value);
                if (this.viewPages[a].listView2 != null) {
                    this.viewPages[a].listView2.setPinnedSectionOffsetY((int) value);
                }
                a++;
            } else {
                this.fragmentView.invalidate();
                return;
            }
        }
    }

    private void showBlockAlert(TLRPC.User user) {
        if (user != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("BlockUser", NUM));
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureBlockContact2", NUM, ContactsController.formatName(user.first_name, user.last_name))));
            builder.setPositiveButton(LocaleController.getString("BlockContact", NUM), new DialogOrContactPickerActivity$$ExternalSyntheticLambda0(this, user));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            AlertDialog dialog = builder.create();
            showDialog(dialog);
            TextView button = (TextView) dialog.getButton(-1);
            if (button != null) {
                button.setTextColor(Theme.getColor("dialogTextRed2"));
            }
        }
    }

    /* renamed from: lambda$showBlockAlert$3$org-telegram-ui-DialogOrContactPickerActivity  reason: not valid java name */
    public /* synthetic */ void m2835x7CLASSNAMECLASSNAME(TLRPC.User user, DialogInterface dialogInterface, int i) {
        if (MessagesController.isSupportUser(user)) {
            AlertsCreator.showSimpleToast(this, LocaleController.getString("ErrorOccurred", NUM));
        } else {
            MessagesController.getInstance(this.currentAccount).blockPeer(user.id);
            AlertsCreator.showSimpleToast(this, LocaleController.getString("UserBlocked", NUM));
        }
        finishFragment();
    }

    private void updateTabs() {
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip2 = this.scrollSlidingTextTabStrip;
        if (scrollSlidingTextTabStrip2 != null) {
            scrollSlidingTextTabStrip2.addTextTab(0, LocaleController.getString("BlockUserChatsTitle", NUM));
            this.scrollSlidingTextTabStrip.addTextTab(1, LocaleController.getString("BlockUserContactsTitle", NUM));
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
        int a = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (a >= viewPageArr.length) {
                break;
            }
            viewPageArr[a].listView.stopScroll();
            if (this.viewPages[a].listView2 != null) {
                this.viewPages[a].listView2.stopScroll();
            }
            a++;
        }
        int a2 = animated;
        int i = 0;
        while (i < 2) {
            ViewPage[] viewPageArr2 = this.viewPages;
            RecyclerListView listView = i == 0 ? viewPageArr2[a2].listView : viewPageArr2[a2].listView2;
            if (listView != null) {
                RecyclerView.Adapter adapter = listView.getAdapter();
                listView.setPinnedHeaderShadowDrawable((Drawable) null);
                if (this.actionBar.getTranslationY() != 0.0f) {
                    ((LinearLayoutManager) listView.getLayoutManager()).scrollToPositionWithOffset(0, (int) this.actionBar.getTranslationY());
                }
            }
            i++;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.fragmentView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarTabActiveText"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarTabUnactiveText"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarTabLine"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, new Drawable[]{this.scrollSlidingTextTabStrip.getSelectorDrawable()}, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarTabSelector"));
        arrayList.addAll(this.dialogsActivity.getThemeDescriptions());
        arrayList.addAll(this.contactsActivity.getThemeDescriptions());
        return arrayList;
    }
}
