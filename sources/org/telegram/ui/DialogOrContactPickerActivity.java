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
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
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
import org.telegram.ui.ContactsActivity;
import org.telegram.ui.DialogsActivity;

public class DialogOrContactPickerActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public static final Interpolator interpolator = $$Lambda$DialogOrContactPickerActivity$VAmOOWLwronwq33cK0iXRproy5M.INSTANCE;
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

    static /* synthetic */ float lambda$static$0(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2 * f2 * f2) + 1.0f;
    }

    private class ViewPage extends FrameLayout {
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

    public DialogOrContactPickerActivity() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("onlySelect", true);
        bundle.putBoolean("checkCanWrite", false);
        bundle.putBoolean("resetDelegate", false);
        bundle.putInt("dialogsType", 4);
        this.dialogsActivity = new DialogsActivity(bundle);
        this.dialogsActivity.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
            public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                DialogOrContactPickerActivity.this.lambda$new$1$DialogOrContactPickerActivity(dialogsActivity, arrayList, charSequence, z);
            }
        });
        this.dialogsActivity.onFragmentCreate();
        Bundle bundle2 = new Bundle();
        bundle2.putBoolean("onlyUsers", true);
        bundle2.putBoolean("destroyAfterSelect", true);
        bundle2.putBoolean("returnAsResult", true);
        bundle2.putBoolean("disableSections", true);
        bundle2.putBoolean("needFinishFragment", false);
        bundle2.putBoolean("resetDelegate", false);
        bundle2.putBoolean("allowSelf", false);
        this.contactsActivity = new ContactsActivity(bundle2);
        this.contactsActivity.setDelegate(new ContactsActivity.ContactsActivityDelegate() {
            public final void didSelectContact(TLRPC.User user, String str, ContactsActivity contactsActivity) {
                DialogOrContactPickerActivity.this.lambda$new$2$DialogOrContactPickerActivity(user, str, contactsActivity);
            }
        });
        this.contactsActivity.onFragmentCreate();
    }

    public /* synthetic */ void lambda$new$1$DialogOrContactPickerActivity(DialogsActivity dialogsActivity2, ArrayList arrayList, CharSequence charSequence, boolean z) {
        if (!arrayList.isEmpty()) {
            long longValue = ((Long) arrayList.get(0)).longValue();
            int i = (int) longValue;
            if (longValue > 0) {
                showBlockAlert(getMessagesController().getUser(Integer.valueOf(i)));
            }
        }
    }

    public /* synthetic */ void lambda$new$2$DialogOrContactPickerActivity(TLRPC.User user, String str, ContactsActivity contactsActivity2) {
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
            public void onItemClick(int i) {
                if (i == -1) {
                    DialogOrContactPickerActivity.this.finishFragment();
                }
            }
        });
        this.hasOwnBackground = true;
        this.searchItem = this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
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
        this.scrollSlidingTextTabStrip = new ScrollSlidingTextTabStrip(context);
        this.scrollSlidingTextTabStrip.setUseSameWidth(true);
        this.actionBar.addView(this.scrollSlidingTextTabStrip, LayoutHelper.createFrame(-1, 44, 83));
        this.scrollSlidingTextTabStrip.setDelegate(new ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate() {
            public /* synthetic */ void onSamePageSelected() {
                ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate.CC.$default$onSamePageSelected(this);
            }

            public void onPageSelected(int i, boolean z) {
                if (DialogOrContactPickerActivity.this.viewPages[0].selectedType != i) {
                    DialogOrContactPickerActivity dialogOrContactPickerActivity = DialogOrContactPickerActivity.this;
                    boolean unused = dialogOrContactPickerActivity.swipeBackEnabled = i == dialogOrContactPickerActivity.scrollSlidingTextTabStrip.getFirstTabId();
                    int unused2 = DialogOrContactPickerActivity.this.viewPages[1].selectedType = i;
                    DialogOrContactPickerActivity.this.viewPages[1].setVisibility(0);
                    DialogOrContactPickerActivity.this.switchToCurrentSelectedMode(true);
                    boolean unused3 = DialogOrContactPickerActivity.this.animatingForward = z;
                }
            }

            public void onPageScrolled(float f) {
                if (f != 1.0f || DialogOrContactPickerActivity.this.viewPages[1].getVisibility() == 0) {
                    if (DialogOrContactPickerActivity.this.animatingForward) {
                        DialogOrContactPickerActivity.this.viewPages[0].setTranslationX((-f) * ((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()));
                        DialogOrContactPickerActivity.this.viewPages[1].setTranslationX(((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()) - (((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()) * f));
                    } else {
                        DialogOrContactPickerActivity.this.viewPages[0].setTranslationX(((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()) * f);
                        DialogOrContactPickerActivity.this.viewPages[1].setTranslationX((((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()) * f) - ((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()));
                    }
                    if (f == 1.0f) {
                        ViewPage viewPage = DialogOrContactPickerActivity.this.viewPages[0];
                        DialogOrContactPickerActivity.this.viewPages[0] = DialogOrContactPickerActivity.this.viewPages[1];
                        DialogOrContactPickerActivity.this.viewPages[1] = viewPage;
                        DialogOrContactPickerActivity.this.viewPages[1].setVisibility(8);
                    }
                }
            }
        });
        this.maximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        AnonymousClass4 r0 = new FrameLayout(context) {
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
                int nextPageId = DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.getNextPageId(z);
                if (nextPageId < 0) {
                    return false;
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                this.maybeStartTracking = false;
                this.startedTracking = true;
                this.startedTrackingX = (int) motionEvent.getX();
                DialogOrContactPickerActivity.this.actionBar.setEnabled(false);
                DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.setEnabled(false);
                int unused = DialogOrContactPickerActivity.this.viewPages[1].selectedType = nextPageId;
                DialogOrContactPickerActivity.this.viewPages[1].setVisibility(0);
                boolean unused2 = DialogOrContactPickerActivity.this.animatingForward = z;
                DialogOrContactPickerActivity.this.switchToCurrentSelectedMode(true);
                if (z) {
                    DialogOrContactPickerActivity.this.viewPages[1].setTranslationX((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth());
                } else {
                    DialogOrContactPickerActivity.this.viewPages[1].setTranslationX((float) (-DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()));
                }
                return true;
            }

            public void forceHasOverlappingRendering(boolean z) {
                super.forceHasOverlappingRendering(z);
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
                measureChildWithMargins(DialogOrContactPickerActivity.this.actionBar, i, 0, i2, 0);
                int measuredHeight = DialogOrContactPickerActivity.this.actionBar.getMeasuredHeight();
                this.globalIgnoreLayout = true;
                for (int i3 = 0; i3 < DialogOrContactPickerActivity.this.viewPages.length; i3++) {
                    if (DialogOrContactPickerActivity.this.viewPages[i3] != null) {
                        if (DialogOrContactPickerActivity.this.viewPages[i3].listView != null) {
                            DialogOrContactPickerActivity.this.viewPages[i3].listView.setPadding(0, measuredHeight, 0, 0);
                        }
                        if (DialogOrContactPickerActivity.this.viewPages[i3].listView2 != null) {
                            DialogOrContactPickerActivity.this.viewPages[i3].listView2.setPadding(0, measuredHeight, 0, 0);
                        }
                    }
                }
                this.globalIgnoreLayout = false;
                int childCount = getChildCount();
                for (int i4 = 0; i4 < childCount; i4++) {
                    View childAt = getChildAt(i4);
                    if (!(childAt == null || childAt.getVisibility() == 8 || childAt == DialogOrContactPickerActivity.this.actionBar)) {
                        measureChildWithMargins(childAt, i, 0, i2, 0);
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

            /* JADX WARNING: Removed duplicated region for block: B:18:0x00a0  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public boolean checkTabsAnimationInProgress() {
                /*
                    r7 = this;
                    org.telegram.ui.DialogOrContactPickerActivity r0 = org.telegram.ui.DialogOrContactPickerActivity.this
                    boolean r0 = r0.tabsAnimationInProgress
                    r1 = 0
                    if (r0 == 0) goto L_0x00c3
                    org.telegram.ui.DialogOrContactPickerActivity r0 = org.telegram.ui.DialogOrContactPickerActivity.this
                    boolean r0 = r0.backAnimation
                    r2 = -1
                    r3 = 0
                    r4 = 1065353216(0x3var_, float:1.0)
                    r5 = 1
                    if (r0 == 0) goto L_0x0059
                    org.telegram.ui.DialogOrContactPickerActivity r0 = org.telegram.ui.DialogOrContactPickerActivity.this
                    org.telegram.ui.DialogOrContactPickerActivity$ViewPage[] r0 = r0.viewPages
                    r0 = r0[r1]
                    float r0 = r0.getTranslationX()
                    float r0 = java.lang.Math.abs(r0)
                    int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
                    if (r0 >= 0) goto L_0x009d
                    org.telegram.ui.DialogOrContactPickerActivity r0 = org.telegram.ui.DialogOrContactPickerActivity.this
                    org.telegram.ui.DialogOrContactPickerActivity$ViewPage[] r0 = r0.viewPages
                    r0 = r0[r1]
                    r0.setTranslationX(r3)
                    org.telegram.ui.DialogOrContactPickerActivity r0 = org.telegram.ui.DialogOrContactPickerActivity.this
                    org.telegram.ui.DialogOrContactPickerActivity$ViewPage[] r0 = r0.viewPages
                    r0 = r0[r5]
                    org.telegram.ui.DialogOrContactPickerActivity r3 = org.telegram.ui.DialogOrContactPickerActivity.this
                    org.telegram.ui.DialogOrContactPickerActivity$ViewPage[] r3 = r3.viewPages
                    r3 = r3[r1]
                    int r3 = r3.getMeasuredWidth()
                    org.telegram.ui.DialogOrContactPickerActivity r4 = org.telegram.ui.DialogOrContactPickerActivity.this
                    boolean r4 = r4.animatingForward
                    if (r4 == 0) goto L_0x0052
                    r2 = 1
                L_0x0052:
                    int r3 = r3 * r2
                    float r2 = (float) r3
                    r0.setTranslationX(r2)
                    goto L_0x009e
                L_0x0059:
                    org.telegram.ui.DialogOrContactPickerActivity r0 = org.telegram.ui.DialogOrContactPickerActivity.this
                    org.telegram.ui.DialogOrContactPickerActivity$ViewPage[] r0 = r0.viewPages
                    r0 = r0[r5]
                    float r0 = r0.getTranslationX()
                    float r0 = java.lang.Math.abs(r0)
                    int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
                    if (r0 >= 0) goto L_0x009d
                    org.telegram.ui.DialogOrContactPickerActivity r0 = org.telegram.ui.DialogOrContactPickerActivity.this
                    org.telegram.ui.DialogOrContactPickerActivity$ViewPage[] r0 = r0.viewPages
                    r0 = r0[r1]
                    org.telegram.ui.DialogOrContactPickerActivity r4 = org.telegram.ui.DialogOrContactPickerActivity.this
                    org.telegram.ui.DialogOrContactPickerActivity$ViewPage[] r4 = r4.viewPages
                    r4 = r4[r1]
                    int r4 = r4.getMeasuredWidth()
                    org.telegram.ui.DialogOrContactPickerActivity r6 = org.telegram.ui.DialogOrContactPickerActivity.this
                    boolean r6 = r6.animatingForward
                    if (r6 == 0) goto L_0x008a
                    goto L_0x008b
                L_0x008a:
                    r2 = 1
                L_0x008b:
                    int r4 = r4 * r2
                    float r2 = (float) r4
                    r0.setTranslationX(r2)
                    org.telegram.ui.DialogOrContactPickerActivity r0 = org.telegram.ui.DialogOrContactPickerActivity.this
                    org.telegram.ui.DialogOrContactPickerActivity$ViewPage[] r0 = r0.viewPages
                    r0 = r0[r5]
                    r0.setTranslationX(r3)
                    goto L_0x009e
                L_0x009d:
                    r5 = 0
                L_0x009e:
                    if (r5 == 0) goto L_0x00bc
                    org.telegram.ui.DialogOrContactPickerActivity r0 = org.telegram.ui.DialogOrContactPickerActivity.this
                    android.animation.AnimatorSet r0 = r0.tabsAnimation
                    if (r0 == 0) goto L_0x00b7
                    org.telegram.ui.DialogOrContactPickerActivity r0 = org.telegram.ui.DialogOrContactPickerActivity.this
                    android.animation.AnimatorSet r0 = r0.tabsAnimation
                    r0.cancel()
                    org.telegram.ui.DialogOrContactPickerActivity r0 = org.telegram.ui.DialogOrContactPickerActivity.this
                    r2 = 0
                    android.animation.AnimatorSet unused = r0.tabsAnimation = r2
                L_0x00b7:
                    org.telegram.ui.DialogOrContactPickerActivity r0 = org.telegram.ui.DialogOrContactPickerActivity.this
                    boolean unused = r0.tabsAnimationInProgress = r1
                L_0x00bc:
                    org.telegram.ui.DialogOrContactPickerActivity r0 = org.telegram.ui.DialogOrContactPickerActivity.this
                    boolean r0 = r0.tabsAnimationInProgress
                    return r0
                L_0x00c3:
                    return r1
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogOrContactPickerActivity.AnonymousClass4.checkTabsAnimationInProgress():boolean");
            }

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return checkTabsAnimationInProgress() || DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.isAnimatingIndicator() || onTouchEvent(motionEvent);
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                DialogOrContactPickerActivity.this.backgroundPaint.setColor(Theme.getColor("windowBackgroundGray"));
                canvas.drawRect(0.0f, ((float) DialogOrContactPickerActivity.this.actionBar.getMeasuredHeight()) + DialogOrContactPickerActivity.this.actionBar.getTranslationY(), (float) getMeasuredWidth(), (float) getMeasuredHeight(), DialogOrContactPickerActivity.this.backgroundPaint);
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                float f;
                int i;
                if (DialogOrContactPickerActivity.this.parentLayout.checkTransitionAnimation() || checkTabsAnimationInProgress()) {
                    return false;
                }
                boolean z = true;
                if (motionEvent != null && motionEvent.getAction() == 0 && !this.startedTracking && !this.maybeStartTracking) {
                    this.startedTrackingPointerId = motionEvent.getPointerId(0);
                    this.maybeStartTracking = true;
                    this.startedTrackingX = (int) motionEvent.getX();
                    this.startedTrackingY = (int) motionEvent.getY();
                    VelocityTracker velocityTracker2 = this.velocityTracker;
                    if (velocityTracker2 != null) {
                        velocityTracker2.clear();
                    }
                } else if (motionEvent != null && motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    int x = (int) (motionEvent.getX() - ((float) this.startedTrackingX));
                    int abs = Math.abs(((int) motionEvent.getY()) - this.startedTrackingY);
                    this.velocityTracker.addMovement(motionEvent);
                    if (this.startedTracking && ((DialogOrContactPickerActivity.this.animatingForward && x > 0) || (!DialogOrContactPickerActivity.this.animatingForward && x < 0))) {
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
                        if (DialogOrContactPickerActivity.this.animatingForward) {
                            DialogOrContactPickerActivity.this.viewPages[0].setTranslationX((float) x);
                            DialogOrContactPickerActivity.this.viewPages[1].setTranslationX((float) (DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth() + x));
                        } else {
                            DialogOrContactPickerActivity.this.viewPages[0].setTranslationX((float) x);
                            DialogOrContactPickerActivity.this.viewPages[1].setTranslationX((float) (x - DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()));
                        }
                        DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DialogOrContactPickerActivity.this.viewPages[1].selectedType, ((float) Math.abs(x)) / ((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()));
                    }
                } else if (motionEvent != null && motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6)) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    this.velocityTracker.computeCurrentVelocity(1000, (float) DialogOrContactPickerActivity.this.maximumVelocity);
                    if (!this.startedTracking) {
                        float xVelocity = this.velocityTracker.getXVelocity();
                        float yVelocity = this.velocityTracker.getYVelocity();
                        if (Math.abs(xVelocity) >= 3000.0f && Math.abs(xVelocity) > Math.abs(yVelocity)) {
                            prepareForMoving(motionEvent, xVelocity < 0.0f);
                        }
                    }
                    if (this.startedTracking) {
                        float x2 = DialogOrContactPickerActivity.this.viewPages[0].getX();
                        AnimatorSet unused = DialogOrContactPickerActivity.this.tabsAnimation = new AnimatorSet();
                        float xVelocity2 = this.velocityTracker.getXVelocity();
                        boolean unused2 = DialogOrContactPickerActivity.this.backAnimation = Math.abs(x2) < ((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(xVelocity2) < 3500.0f || Math.abs(xVelocity2) < Math.abs(this.velocityTracker.getYVelocity()));
                        if (DialogOrContactPickerActivity.this.backAnimation) {
                            f = Math.abs(x2);
                            if (DialogOrContactPickerActivity.this.animatingForward) {
                                DialogOrContactPickerActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{(float) DialogOrContactPickerActivity.this.viewPages[1].getMeasuredWidth()})});
                            } else {
                                DialogOrContactPickerActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{(float) (-DialogOrContactPickerActivity.this.viewPages[1].getMeasuredWidth())})});
                            }
                        } else {
                            f = ((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()) - Math.abs(x2);
                            if (DialogOrContactPickerActivity.this.animatingForward) {
                                DialogOrContactPickerActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{(float) (-DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth())}), ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{0.0f})});
                            } else {
                                DialogOrContactPickerActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{(float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()}), ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{0.0f})});
                            }
                        }
                        DialogOrContactPickerActivity.this.tabsAnimation.setInterpolator(DialogOrContactPickerActivity.interpolator);
                        int measuredWidth = getMeasuredWidth();
                        float f2 = (float) (measuredWidth / 2);
                        float distanceInfluenceForSnapDuration = f2 + (AndroidUtilities.distanceInfluenceForSnapDuration(Math.min(1.0f, (f * 1.0f) / ((float) measuredWidth))) * f2);
                        float abs2 = Math.abs(xVelocity2);
                        if (abs2 > 0.0f) {
                            i = Math.round(Math.abs(distanceInfluenceForSnapDuration / abs2) * 1000.0f) * 4;
                        } else {
                            i = (int) (((f / ((float) getMeasuredWidth())) + 1.0f) * 100.0f);
                        }
                        DialogOrContactPickerActivity.this.tabsAnimation.setDuration((long) Math.max(150, Math.min(i, 600)));
                        DialogOrContactPickerActivity.this.tabsAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                AnimatorSet unused = DialogOrContactPickerActivity.this.tabsAnimation = null;
                                if (DialogOrContactPickerActivity.this.backAnimation) {
                                    DialogOrContactPickerActivity.this.viewPages[1].setVisibility(8);
                                } else {
                                    ViewPage viewPage = DialogOrContactPickerActivity.this.viewPages[0];
                                    DialogOrContactPickerActivity.this.viewPages[0] = DialogOrContactPickerActivity.this.viewPages[1];
                                    DialogOrContactPickerActivity.this.viewPages[1] = viewPage;
                                    DialogOrContactPickerActivity.this.viewPages[1].setVisibility(8);
                                    DialogOrContactPickerActivity dialogOrContactPickerActivity = DialogOrContactPickerActivity.this;
                                    boolean unused2 = dialogOrContactPickerActivity.swipeBackEnabled = dialogOrContactPickerActivity.viewPages[0].selectedType == DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.getFirstTabId();
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
                    } else {
                        this.maybeStartTracking = false;
                        this.startedTracking = false;
                        DialogOrContactPickerActivity.this.actionBar.setEnabled(true);
                        DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
                    }
                    VelocityTracker velocityTracker3 = this.velocityTracker;
                    if (velocityTracker3 != null) {
                        velocityTracker3.recycle();
                        this.velocityTracker = null;
                    }
                }
                return this.startedTracking;
            }
        };
        this.fragmentView = r0;
        r0.setWillNotDraw(false);
        this.dialogsActivity.setParentFragment(this);
        this.contactsActivity.setParentFragment(this);
        int i = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (i >= viewPageArr.length) {
                break;
            }
            viewPageArr[i] = new ViewPage(context) {
                public void setTranslationX(float f) {
                    super.setTranslationX(f);
                    if (DialogOrContactPickerActivity.this.tabsAnimationInProgress && DialogOrContactPickerActivity.this.viewPages[0] == this) {
                        DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DialogOrContactPickerActivity.this.viewPages[1].selectedType, Math.abs(DialogOrContactPickerActivity.this.viewPages[0].getTranslationX()) / ((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()));
                    }
                }
            };
            r0.addView(this.viewPages[i], LayoutHelper.createFrame(-1, -1.0f));
            if (i == 0) {
                BaseFragment unused = this.viewPages[i].parentFragment = this.dialogsActivity;
                RecyclerListView unused2 = this.viewPages[i].listView = this.dialogsActivity.getListView();
                RecyclerListView unused3 = this.viewPages[i].listView2 = this.dialogsActivity.getSearchListView();
            } else if (i == 1) {
                BaseFragment unused4 = this.viewPages[i].parentFragment = this.contactsActivity;
                RecyclerListView unused5 = this.viewPages[i].listView = this.contactsActivity.getListView();
                this.viewPages[i].setVisibility(8);
            }
            ViewPage[] viewPageArr2 = this.viewPages;
            FrameLayout unused6 = viewPageArr2[i].fragmentView = (FrameLayout) viewPageArr2[i].parentFragment.getFragmentView();
            ViewPage[] viewPageArr3 = this.viewPages;
            ActionBar unused7 = viewPageArr3[i].actionBar = viewPageArr3[i].parentFragment.getActionBar();
            ViewPage[] viewPageArr4 = this.viewPages;
            viewPageArr4[i].addView(viewPageArr4[i].fragmentView, LayoutHelper.createFrame(-1, -1.0f));
            ViewPage[] viewPageArr5 = this.viewPages;
            viewPageArr5[i].addView(viewPageArr5[i].actionBar, LayoutHelper.createFrame(-1, -2.0f));
            this.viewPages[i].actionBar.setVisibility(8);
            int i2 = 0;
            while (i2 < 2) {
                ViewPage[] viewPageArr6 = this.viewPages;
                RecyclerListView access$1200 = i2 == 0 ? viewPageArr6[i].listView : viewPageArr6[i].listView2;
                if (access$1200 != null) {
                    access$1200.setClipToPadding(false);
                    final RecyclerView.OnScrollListener onScrollListener = access$1200.getOnScrollListener();
                    access$1200.setOnScrollListener(new RecyclerView.OnScrollListener() {
                        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                            onScrollListener.onScrollStateChanged(recyclerView, i);
                            if (i != 1) {
                                int i2 = (int) (-DialogOrContactPickerActivity.this.actionBar.getTranslationY());
                                int currentActionBarHeight = ActionBar.getCurrentActionBarHeight();
                                if (i2 != 0 && i2 != currentActionBarHeight) {
                                    if (i2 < currentActionBarHeight / 2) {
                                        int i3 = -i2;
                                        DialogOrContactPickerActivity.this.viewPages[0].listView.smoothScrollBy(0, i3);
                                        if (DialogOrContactPickerActivity.this.viewPages[0].listView2 != null) {
                                            DialogOrContactPickerActivity.this.viewPages[0].listView2.smoothScrollBy(0, i3);
                                            return;
                                        }
                                        return;
                                    }
                                    int i4 = currentActionBarHeight - i2;
                                    DialogOrContactPickerActivity.this.viewPages[0].listView.smoothScrollBy(0, i4);
                                    if (DialogOrContactPickerActivity.this.viewPages[0].listView2 != null) {
                                        DialogOrContactPickerActivity.this.viewPages[0].listView2.smoothScrollBy(0, i4);
                                    }
                                }
                            }
                        }

                        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                            onScrollListener.onScrolled(recyclerView, i, i2);
                            if (recyclerView == DialogOrContactPickerActivity.this.viewPages[0].listView || recyclerView == DialogOrContactPickerActivity.this.viewPages[0].listView2) {
                                float translationY = DialogOrContactPickerActivity.this.actionBar.getTranslationY();
                                float f = translationY - ((float) i2);
                                if (f < ((float) (-ActionBar.getCurrentActionBarHeight()))) {
                                    f = (float) (-ActionBar.getCurrentActionBarHeight());
                                } else if (f > 0.0f) {
                                    f = 0.0f;
                                }
                                if (f != translationY) {
                                    DialogOrContactPickerActivity.this.setScrollY(f);
                                }
                            }
                        }
                    });
                }
                i2++;
            }
            i++;
        }
        r0.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
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

    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
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
    public void setScrollY(float f) {
        this.actionBar.setTranslationY(f);
        int i = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (i < viewPageArr.length) {
                int i2 = (int) f;
                viewPageArr[i].listView.setPinnedSectionOffsetY(i2);
                if (this.viewPages[i].listView2 != null) {
                    this.viewPages[i].listView2.setPinnedSectionOffsetY(i2);
                }
                i++;
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
            builder.setPositiveButton(LocaleController.getString("BlockContact", NUM), new DialogInterface.OnClickListener(user) {
                private final /* synthetic */ TLRPC.User f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    DialogOrContactPickerActivity.this.lambda$showBlockAlert$3$DialogOrContactPickerActivity(this.f$1, dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            AlertDialog create = builder.create();
            showDialog(create);
            TextView textView = (TextView) create.getButton(-1);
            if (textView != null) {
                textView.setTextColor(Theme.getColor("dialogTextRed2"));
            }
        }
    }

    public /* synthetic */ void lambda$showBlockAlert$3$DialogOrContactPickerActivity(TLRPC.User user, DialogInterface dialogInterface, int i) {
        if (MessagesController.isSupportUser(user)) {
            AlertsCreator.showSimpleToast(this, LocaleController.getString("ErrorOccurred", NUM));
        } else {
            MessagesController.getInstance(this.currentAccount).blockUser(user.id);
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
            int currentTabId = this.scrollSlidingTextTabStrip.getCurrentTabId();
            if (currentTabId >= 0) {
                int unused = this.viewPages[0].selectedType = currentTabId;
            }
            this.scrollSlidingTextTabStrip.finishAddingTabs();
        }
    }

    /* JADX WARNING: type inference failed for: r6v0, types: [boolean] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void switchToCurrentSelectedMode(boolean r6) {
        /*
            r5 = this;
            r0 = 0
            r1 = 0
        L_0x0002:
            org.telegram.ui.DialogOrContactPickerActivity$ViewPage[] r2 = r5.viewPages
            int r3 = r2.length
            if (r1 >= r3) goto L_0x0028
            r2 = r2[r1]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            r2.stopScroll()
            org.telegram.ui.DialogOrContactPickerActivity$ViewPage[] r2 = r5.viewPages
            r2 = r2[r1]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView2
            if (r2 == 0) goto L_0x0025
            org.telegram.ui.DialogOrContactPickerActivity$ViewPage[] r2 = r5.viewPages
            r2 = r2[r1]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView2
            r2.stopScroll()
        L_0x0025:
            int r1 = r1 + 1
            goto L_0x0002
        L_0x0028:
            r1 = 0
        L_0x0029:
            r2 = 2
            if (r1 >= r2) goto L_0x0065
            org.telegram.ui.DialogOrContactPickerActivity$ViewPage[] r2 = r5.viewPages
            if (r1 != 0) goto L_0x0037
            r2 = r2[r6]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            goto L_0x003d
        L_0x0037:
            r2 = r2[r6]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView2
        L_0x003d:
            if (r2 != 0) goto L_0x0040
            goto L_0x0062
        L_0x0040:
            r2.getAdapter()
            r3 = 0
            r2.setPinnedHeaderShadowDrawable(r3)
            org.telegram.ui.ActionBar.ActionBar r3 = r5.actionBar
            float r3 = r3.getTranslationY()
            r4 = 0
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 == 0) goto L_0x0062
            androidx.recyclerview.widget.RecyclerView$LayoutManager r2 = r2.getLayoutManager()
            androidx.recyclerview.widget.LinearLayoutManager r2 = (androidx.recyclerview.widget.LinearLayoutManager) r2
            org.telegram.ui.ActionBar.ActionBar r3 = r5.actionBar
            float r3 = r3.getTranslationY()
            int r3 = (int) r3
            r2.scrollToPositionWithOffset(r0, r3)
        L_0x0062:
            int r1 = r1 + 1
            goto L_0x0029
        L_0x0065:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogOrContactPickerActivity.switchToCurrentSelectedMode(boolean):void");
    }

    public ThemeDescription[] getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.fragmentView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarTabActiveText"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarTabUnactiveText"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarTabLine"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, new Drawable[]{this.scrollSlidingTextTabStrip.getSelectorDrawable()}, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarTabSelector"));
        Collections.addAll(arrayList, this.dialogsActivity.getThemeDescriptions());
        Collections.addAll(arrayList, this.contactsActivity.getThemeDescriptions());
        return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[0]);
    }
}
