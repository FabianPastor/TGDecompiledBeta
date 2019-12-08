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
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import java.util.ArrayList;
import java.util.Collections;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScrollSlidingTextTabStrip;
import org.telegram.ui.Components.ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate;

public class DialogOrContactPickerActivity extends BaseFragment {
    private static final Interpolator interpolator = -$$Lambda$DialogOrContactPickerActivity$VAmOOWLwronwq33cK0iXRproy5M.INSTANCE;
    private static final int search_button = 0;
    private boolean animatingForward;
    private boolean backAnimation;
    private Paint backgroundPaint = new Paint();
    private ContactsActivity contactsActivity;
    private DialogsActivity dialogsActivity;
    private int maximumVelocity;
    private ScrollSlidingTextTabStrip scrollSlidingTextTabStrip;
    private ActionBarMenuItem searchItem;
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

    public DialogOrContactPickerActivity() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("onlySelect", true);
        bundle.putBoolean("checkCanWrite", false);
        String str = "resetDelegate";
        bundle.putBoolean(str, false);
        bundle.putInt("dialogsType", 4);
        this.dialogsActivity = new DialogsActivity(bundle);
        this.dialogsActivity.setDelegate(new -$$Lambda$DialogOrContactPickerActivity$zujU07QvHFPfKO6oq0VB-AE68gM(this));
        this.dialogsActivity.onFragmentCreate();
        bundle = new Bundle();
        bundle.putBoolean("onlyUsers", true);
        bundle.putBoolean("destroyAfterSelect", true);
        bundle.putBoolean("returnAsResult", true);
        bundle.putBoolean("disableSections", true);
        bundle.putBoolean("needFinishFragment", false);
        bundle.putBoolean(str, false);
        this.contactsActivity = new ContactsActivity(bundle);
        this.contactsActivity.setDelegate(new -$$Lambda$DialogOrContactPickerActivity$YMiYnz58wvar_gzOnJojoou4oa4s(this));
        this.contactsActivity.onFragmentCreate();
    }

    public /* synthetic */ void lambda$new$1$DialogOrContactPickerActivity(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        if (!arrayList.isEmpty()) {
            long longValue = ((Long) arrayList.get(0)).longValue();
            int i = (int) longValue;
            if (longValue > 0) {
                showBlockAlert(getMessagesController().getUser(Integer.valueOf(i)));
            }
        }
    }

    public /* synthetic */ void lambda$new$2$DialogOrContactPickerActivity(User user, String str, ContactsActivity contactsActivity) {
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
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    DialogOrContactPickerActivity.this.finishFragment();
                }
            }
        });
        this.hasOwnBackground = true;
        this.searchItem = this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                String str = "";
                DialogOrContactPickerActivity.this.dialogsActivity.getActionBar().openSearchField(str, false);
                DialogOrContactPickerActivity.this.contactsActivity.getActionBar().openSearchField(str, false);
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
        this.scrollSlidingTextTabStrip.setDelegate(new ScrollSlidingTabStripDelegate() {
            public void onPageSelected(int i, boolean z) {
                if (DialogOrContactPickerActivity.this.viewPages[0].selectedType != i) {
                    DialogOrContactPickerActivity dialogOrContactPickerActivity = DialogOrContactPickerActivity.this;
                    dialogOrContactPickerActivity.swipeBackEnabled = i == dialogOrContactPickerActivity.scrollSlidingTextTabStrip.getFirstTabId();
                    DialogOrContactPickerActivity.this.viewPages[1].selectedType = i;
                    DialogOrContactPickerActivity.this.viewPages[1].setVisibility(0);
                    DialogOrContactPickerActivity.this.switchToCurrentSelectedMode(true);
                    DialogOrContactPickerActivity.this.animatingForward = z;
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
        AnonymousClass4 anonymousClass4 = new FrameLayout(context) {
            private boolean globalIgnoreLayout;
            private boolean maybeStartTracking;
            private boolean startedTracking;
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
                DialogOrContactPickerActivity.this.viewPages[1].selectedType = nextPageId;
                DialogOrContactPickerActivity.this.viewPages[1].setVisibility(0);
                DialogOrContactPickerActivity.this.animatingForward = z;
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

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                setMeasuredDimension(MeasureSpec.getSize(i), MeasureSpec.getSize(i2));
                measureChildWithMargins(DialogOrContactPickerActivity.this.actionBar, i, 0, i2, 0);
                int measuredHeight = DialogOrContactPickerActivity.this.actionBar.getMeasuredHeight();
                this.globalIgnoreLayout = true;
                int i3 = 0;
                int i4 = 0;
                while (i4 < DialogOrContactPickerActivity.this.viewPages.length) {
                    if (!(DialogOrContactPickerActivity.this.viewPages[i4] == null || DialogOrContactPickerActivity.this.viewPages[i4].listView == null)) {
                        DialogOrContactPickerActivity.this.viewPages[i4].listView.setPadding(0, measuredHeight, 0, 0);
                    }
                    i4++;
                }
                this.globalIgnoreLayout = false;
                measuredHeight = getChildCount();
                while (i3 < measuredHeight) {
                    View childAt = getChildAt(i3);
                    if (!(childAt == null || childAt.getVisibility() == 8 || childAt == DialogOrContactPickerActivity.this.actionBar)) {
                        measureChildWithMargins(childAt, i, 0, i2, 0);
                    }
                    i3++;
                }
            }

            /* Access modifiers changed, original: protected */
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
            /* JADX WARNING: Removed duplicated region for block: B:18:0x00a0  */
            public boolean checkTabsAnimationInProgress() {
                /*
                r7 = this;
                r0 = org.telegram.ui.DialogOrContactPickerActivity.this;
                r0 = r0.tabsAnimationInProgress;
                r1 = 0;
                if (r0 == 0) goto L_0x00c3;
            L_0x0009:
                r0 = org.telegram.ui.DialogOrContactPickerActivity.this;
                r0 = r0.backAnimation;
                r2 = -1;
                r3 = 0;
                r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
                r5 = 1;
                if (r0 == 0) goto L_0x0059;
            L_0x0016:
                r0 = org.telegram.ui.DialogOrContactPickerActivity.this;
                r0 = r0.viewPages;
                r0 = r0[r1];
                r0 = r0.getTranslationX();
                r0 = java.lang.Math.abs(r0);
                r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
                if (r0 >= 0) goto L_0x009d;
            L_0x002a:
                r0 = org.telegram.ui.DialogOrContactPickerActivity.this;
                r0 = r0.viewPages;
                r0 = r0[r1];
                r0.setTranslationX(r3);
                r0 = org.telegram.ui.DialogOrContactPickerActivity.this;
                r0 = r0.viewPages;
                r0 = r0[r5];
                r3 = org.telegram.ui.DialogOrContactPickerActivity.this;
                r3 = r3.viewPages;
                r3 = r3[r1];
                r3 = r3.getMeasuredWidth();
                r4 = org.telegram.ui.DialogOrContactPickerActivity.this;
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
                r0 = org.telegram.ui.DialogOrContactPickerActivity.this;
                r0 = r0.viewPages;
                r0 = r0[r5];
                r0 = r0.getTranslationX();
                r0 = java.lang.Math.abs(r0);
                r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
                if (r0 >= 0) goto L_0x009d;
            L_0x006d:
                r0 = org.telegram.ui.DialogOrContactPickerActivity.this;
                r0 = r0.viewPages;
                r0 = r0[r1];
                r4 = org.telegram.ui.DialogOrContactPickerActivity.this;
                r4 = r4.viewPages;
                r4 = r4[r1];
                r4 = r4.getMeasuredWidth();
                r6 = org.telegram.ui.DialogOrContactPickerActivity.this;
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
                r0 = org.telegram.ui.DialogOrContactPickerActivity.this;
                r0 = r0.viewPages;
                r0 = r0[r5];
                r0.setTranslationX(r3);
                goto L_0x009e;
            L_0x009d:
                r5 = 0;
            L_0x009e:
                if (r5 == 0) goto L_0x00bc;
            L_0x00a0:
                r0 = org.telegram.ui.DialogOrContactPickerActivity.this;
                r0 = r0.tabsAnimation;
                if (r0 == 0) goto L_0x00b7;
            L_0x00a8:
                r0 = org.telegram.ui.DialogOrContactPickerActivity.this;
                r0 = r0.tabsAnimation;
                r0.cancel();
                r0 = org.telegram.ui.DialogOrContactPickerActivity.this;
                r2 = 0;
                r0.tabsAnimation = r2;
            L_0x00b7:
                r0 = org.telegram.ui.DialogOrContactPickerActivity.this;
                r0.tabsAnimationInProgress = r1;
            L_0x00bc:
                r0 = org.telegram.ui.DialogOrContactPickerActivity.this;
                r0 = r0.tabsAnimationInProgress;
                return r0;
            L_0x00c3:
                return r1;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogOrContactPickerActivity$AnonymousClass4.checkTabsAnimationInProgress():boolean");
            }

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return checkTabsAnimationInProgress() || DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.isAnimatingIndicator() || onTouchEvent(motionEvent);
            }

            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                DialogOrContactPickerActivity.this.backgroundPaint.setColor(Theme.getColor("windowBackgroundGray"));
                canvas.drawRect(0.0f, ((float) DialogOrContactPickerActivity.this.actionBar.getMeasuredHeight()) + DialogOrContactPickerActivity.this.actionBar.getTranslationY(), (float) getMeasuredWidth(), (float) getMeasuredHeight(), DialogOrContactPickerActivity.this.backgroundPaint);
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (DialogOrContactPickerActivity.this.parentLayout.checkTransitionAnimation() || checkTabsAnimationInProgress()) {
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
                    float xVelocity;
                    float yVelocity;
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    this.velocityTracker.computeCurrentVelocity(1000, (float) DialogOrContactPickerActivity.this.maximumVelocity);
                    if (!this.startedTracking) {
                        xVelocity = this.velocityTracker.getXVelocity();
                        yVelocity = this.velocityTracker.getYVelocity();
                        if (Math.abs(xVelocity) >= 3000.0f && Math.abs(xVelocity) > Math.abs(yVelocity)) {
                            prepareForMoving(motionEvent, xVelocity < 0.0f);
                        }
                    }
                    if (this.startedTracking) {
                        int round;
                        float x2 = DialogOrContactPickerActivity.this.viewPages[0].getX();
                        DialogOrContactPickerActivity.this.tabsAnimation = new AnimatorSet();
                        xVelocity = this.velocityTracker.getXVelocity();
                        yVelocity = this.velocityTracker.getYVelocity();
                        DialogOrContactPickerActivity dialogOrContactPickerActivity = DialogOrContactPickerActivity.this;
                        boolean z2 = Math.abs(x2) < ((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(xVelocity) < 3500.0f || Math.abs(xVelocity) < Math.abs(yVelocity));
                        dialogOrContactPickerActivity.backAnimation = z2;
                        AnimatorSet access$2000;
                        Animator[] animatorArr;
                        if (DialogOrContactPickerActivity.this.backAnimation) {
                            x2 = Math.abs(x2);
                            if (DialogOrContactPickerActivity.this.animatingForward) {
                                access$2000 = DialogOrContactPickerActivity.this.tabsAnimation;
                                animatorArr = new Animator[2];
                                animatorArr[0] = ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{0.0f});
                                animatorArr[1] = ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{(float) DialogOrContactPickerActivity.this.viewPages[1].getMeasuredWidth()});
                                access$2000.playTogether(animatorArr);
                            } else {
                                access$2000 = DialogOrContactPickerActivity.this.tabsAnimation;
                                animatorArr = new Animator[2];
                                animatorArr[0] = ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{0.0f});
                                animatorArr[1] = ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{(float) (-DialogOrContactPickerActivity.this.viewPages[1].getMeasuredWidth())});
                                access$2000.playTogether(animatorArr);
                            }
                        } else {
                            x2 = ((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()) - Math.abs(x2);
                            if (DialogOrContactPickerActivity.this.animatingForward) {
                                access$2000 = DialogOrContactPickerActivity.this.tabsAnimation;
                                animatorArr = new Animator[2];
                                animatorArr[0] = ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{(float) (-DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth())});
                                animatorArr[1] = ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{0.0f});
                                access$2000.playTogether(animatorArr);
                            } else {
                                access$2000 = DialogOrContactPickerActivity.this.tabsAnimation;
                                animatorArr = new Animator[2];
                                animatorArr[0] = ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{(float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()});
                                animatorArr[1] = ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{0.0f});
                                access$2000.playTogether(animatorArr);
                            }
                        }
                        DialogOrContactPickerActivity.this.tabsAnimation.setInterpolator(DialogOrContactPickerActivity.interpolator);
                        int measuredWidth = getMeasuredWidth();
                        float f = (float) (measuredWidth / 2);
                        f += AndroidUtilities.distanceInfluenceForSnapDuration(Math.min(1.0f, (x2 * 1.0f) / ((float) measuredWidth))) * f;
                        float abs2 = Math.abs(xVelocity);
                        if (abs2 > 0.0f) {
                            round = Math.round(Math.abs(f / abs2) * 1000.0f) * 4;
                        } else {
                            round = (int) (((x2 / ((float) getMeasuredWidth())) + 1.0f) * 100.0f);
                        }
                        DialogOrContactPickerActivity.this.tabsAnimation.setDuration((long) Math.max(150, Math.min(round, 600)));
                        DialogOrContactPickerActivity.this.tabsAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                DialogOrContactPickerActivity.this.tabsAnimation = null;
                                if (DialogOrContactPickerActivity.this.backAnimation) {
                                    DialogOrContactPickerActivity.this.viewPages[1].setVisibility(8);
                                } else {
                                    ViewPage viewPage = DialogOrContactPickerActivity.this.viewPages[0];
                                    DialogOrContactPickerActivity.this.viewPages[0] = DialogOrContactPickerActivity.this.viewPages[1];
                                    DialogOrContactPickerActivity.this.viewPages[1] = viewPage;
                                    DialogOrContactPickerActivity.this.viewPages[1].setVisibility(8);
                                    DialogOrContactPickerActivity dialogOrContactPickerActivity = DialogOrContactPickerActivity.this;
                                    dialogOrContactPickerActivity.swipeBackEnabled = dialogOrContactPickerActivity.viewPages[0].selectedType == DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.getFirstTabId();
                                    DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DialogOrContactPickerActivity.this.viewPages[0].selectedType, 1.0f);
                                }
                                DialogOrContactPickerActivity.this.tabsAnimationInProgress = false;
                                AnonymousClass4.this.maybeStartTracking = false;
                                AnonymousClass4.this.startedTracking = false;
                                DialogOrContactPickerActivity.this.actionBar.setEnabled(true);
                                DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
                            }
                        });
                        DialogOrContactPickerActivity.this.tabsAnimation.start();
                        DialogOrContactPickerActivity.this.tabsAnimationInProgress = true;
                    } else {
                        this.maybeStartTracking = false;
                        this.startedTracking = false;
                        DialogOrContactPickerActivity.this.actionBar.setEnabled(true);
                        DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
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
            anonymousClass4.addView(this.viewPages[i], LayoutHelper.createFrame(-1, -1.0f));
            if (i == 0) {
                this.viewPages[i].parentFragment = this.dialogsActivity;
                this.viewPages[i].listView = this.dialogsActivity.getListView();
            } else if (i == 1) {
                this.viewPages[i].parentFragment = this.contactsActivity;
                this.viewPages[i].listView = this.contactsActivity.getListView();
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
                        int i2 = (int) (-DialogOrContactPickerActivity.this.actionBar.getTranslationY());
                        i = ActionBar.getCurrentActionBarHeight();
                        if (i2 != 0 && i2 != i) {
                            if (i2 < i / 2) {
                                DialogOrContactPickerActivity.this.viewPages[0].listView.smoothScrollBy(0, -i2);
                            } else {
                                DialogOrContactPickerActivity.this.viewPages[0].listView.smoothScrollBy(0, i - i2);
                            }
                        }
                    }
                }

                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    onScrollListener.onScrolled(recyclerView, i, i2);
                    if (recyclerView == DialogOrContactPickerActivity.this.viewPages[0].listView) {
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
            i++;
        }
        anonymousClass4.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
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
        DialogsActivity dialogsActivity = this.dialogsActivity;
        if (dialogsActivity != null) {
            dialogsActivity.onResume();
        }
        ContactsActivity contactsActivity = this.contactsActivity;
        if (contactsActivity != null) {
            contactsActivity.onResume();
        }
    }

    public void onPause() {
        super.onPause();
        DialogsActivity dialogsActivity = this.dialogsActivity;
        if (dialogsActivity != null) {
            dialogsActivity.onPause();
        }
        ContactsActivity contactsActivity = this.contactsActivity;
        if (contactsActivity != null) {
            contactsActivity.onPause();
        }
    }

    public void onFragmentDestroy() {
        DialogsActivity dialogsActivity = this.dialogsActivity;
        if (dialogsActivity != null) {
            dialogsActivity.onFragmentDestroy();
        }
        ContactsActivity contactsActivity = this.contactsActivity;
        if (contactsActivity != null) {
            contactsActivity.onFragmentDestroy();
        }
        super.onFragmentDestroy();
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

    private void showBlockAlert(User user) {
        if (user != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("BlockUser", NUM));
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureBlockContact2", NUM, ContactsController.formatName(user.first_name, user.last_name))));
            builder.setPositiveButton(LocaleController.getString("BlockContact", NUM), new -$$Lambda$DialogOrContactPickerActivity$XGjY0fzix6uDjlZVM_E2r47WlnA(this, user));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            AlertDialog create = builder.create();
            showDialog(create);
            TextView textView = (TextView) create.getButton(-1);
            if (textView != null) {
                textView.setTextColor(Theme.getColor("dialogTextRed2"));
            }
        }
    }

    public /* synthetic */ void lambda$showBlockAlert$3$DialogOrContactPickerActivity(User user, DialogInterface dialogInterface, int i) {
        if (MessagesController.isSupportUser(user)) {
            AlertsCreator.showSimpleToast(this, LocaleController.getString("ErrorOccurred", NUM));
        } else {
            MessagesController.getInstance(this.currentAccount).blockUser(user.id);
            AlertsCreator.showSimpleToast(this, LocaleController.getString("UserBlocked", NUM));
        }
        finishFragment();
    }

    private void updateTabs() {
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip = this.scrollSlidingTextTabStrip;
        if (scrollSlidingTextTabStrip != null) {
            scrollSlidingTextTabStrip.addTextTab(0, LocaleController.getString("BlockUserChatsTitle", NUM));
            this.scrollSlidingTextTabStrip.addTextTab(1, LocaleController.getString("BlockUserContactsTitle", NUM));
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
        arrayList.add(new ThemeDescription(this.fragmentView, 0, null, null, null, null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, "actionBarTabActiveText"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, "actionBarTabUnactiveText"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{TextView.class}, null, null, null, "actionBarTabLine"));
        arrayList.add(new ThemeDescription(null, 0, null, null, new Drawable[]{this.scrollSlidingTextTabStrip.getSelectorDrawable()}, null, "actionBarTabSelector"));
        Collections.addAll(arrayList, this.dialogsActivity.getThemeDescriptions());
        Collections.addAll(arrayList, this.contactsActivity.getThemeDescriptions());
        return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[0]);
    }
}
