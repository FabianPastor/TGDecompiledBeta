package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Property;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$InputDialogPeer;
import org.telegram.tgnet.TLRPC$TL_dialogFolder;
import org.telegram.tgnet.TLRPC$TL_inputFolderPeer;
import org.telegram.tgnet.TLRPC$TL_userEmpty;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.MenuDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.DialogsAdapter;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Cells.ArchiveHintInnerCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.DialogsEmptyCell;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.DrawerActionCell;
import org.telegram.ui.Cells.DrawerAddCell;
import org.telegram.ui.Cells.DrawerProfileCell;
import org.telegram.ui.Cells.DrawerUserCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HashtagSearchCell;
import org.telegram.ui.Cells.HintDialogCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.DialogsItemAnimator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.FilterTabsView;
import org.telegram.ui.Components.FiltersListBottomSheet;
import org.telegram.ui.Components.FragmentContextView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.PacmanAnimation;
import org.telegram.ui.Components.ProxyDrawable;
import org.telegram.ui.Components.PullForegroundDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerAnimationScrollHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SearchViewPager;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.ViewPagerFixed;

public class DialogsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    public static boolean[] dialogsLoaded = new boolean[3];
    /* access modifiers changed from: private */
    public static ArrayList<TLRPC$Dialog> frozenDialogsList;
    /* access modifiers changed from: private */
    public static final Interpolator interpolator = $$Lambda$DialogsActivity$xOEuGfWASgHbeYQu20ZPCmNt81M.INSTANCE;
    public static float viewOffset = 0.0f;
    public final Property<DialogsActivity, Float> SCROLL_Y = new AnimationProperties.FloatProperty<DialogsActivity>("animationValue") {
        public void setValue(DialogsActivity dialogsActivity, float f) {
            dialogsActivity.setScrollY(f);
        }

        public Float get(DialogsActivity dialogsActivity) {
            return Float.valueOf(DialogsActivity.this.actionBar.getTranslationY());
        }
    };
    ValueAnimator actionBarColorAnimator;
    /* access modifiers changed from: private */
    public Paint actionBarDefaultPaint = new Paint();
    private ArrayList<View> actionModeViews = new ArrayList<>();
    private ActionBarMenuSubItem addToFolderItem;
    private String addToGroupAlertString;
    /* access modifiers changed from: private */
    public float additionalFloatingTranslation;
    /* access modifiers changed from: private */
    public float additionalOffset;
    private boolean afterSignup;
    /* access modifiers changed from: private */
    public boolean allowMoving;
    /* access modifiers changed from: private */
    public boolean allowSwipeDuringCurrentTouch;
    private boolean allowSwitchAccount;
    /* access modifiers changed from: private */
    public boolean animatingForward;
    /* access modifiers changed from: private */
    public int animationIndex = -1;
    private ActionBarMenuItem archive2Item;
    private ActionBarMenuSubItem archiveItem;
    private boolean askAboutContacts = true;
    private boolean askingForPermissions;
    /* access modifiers changed from: private */
    public ChatAvatarContainer avatarContainer;
    /* access modifiers changed from: private */
    public boolean backAnimation;
    private BackDrawable backDrawable;
    private ActionBarMenuSubItem blockItem;
    private View blurredView;
    private int canClearCacheCount;
    private boolean canDeletePsaSelected;
    private int canMuteCount;
    private int canPinCount;
    private int canReadCount;
    private int canReportSpamCount;
    /* access modifiers changed from: private */
    public boolean canShowHiddenArchive;
    private int canUnarchiveCount;
    private int canUnmuteCount;
    private boolean cantSendToChannels;
    private boolean checkCanWrite;
    private boolean checkPermission = true;
    private ActionBarMenuSubItem clearItem;
    private boolean closeSearchFieldOnHide;
    /* access modifiers changed from: private */
    public ChatActivityEnterView commentView;
    private int currentConnectionState;
    /* access modifiers changed from: private */
    public DialogsActivityDelegate delegate;
    private ActionBarMenuItem deleteItem;
    /* access modifiers changed from: private */
    public int dialogChangeFinished;
    /* access modifiers changed from: private */
    public int dialogInsertFinished;
    /* access modifiers changed from: private */
    public int dialogRemoveFinished;
    /* access modifiers changed from: private */
    public boolean dialogsListFrozen;
    /* access modifiers changed from: private */
    public boolean disableActionBarScrolling;
    /* access modifiers changed from: private */
    public ActionBarMenuItem doneItem;
    /* access modifiers changed from: private */
    public AnimatorSet doneItemAnimator;
    /* access modifiers changed from: private */
    public FilterTabsView filterTabsView;
    /* access modifiers changed from: private */
    public FiltersView filtersView;
    private ImageView floatingButton;
    /* access modifiers changed from: private */
    public FrameLayout floatingButtonContainer;
    /* access modifiers changed from: private */
    public float floatingButtonHideProgress;
    /* access modifiers changed from: private */
    public float floatingButtonTranslation;
    /* access modifiers changed from: private */
    public boolean floatingHidden;
    private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();
    /* access modifiers changed from: private */
    public int folderId;
    /* access modifiers changed from: private */
    public FragmentContextView fragmentContextView;
    /* access modifiers changed from: private */
    public FragmentContextView fragmentLocationContextView;
    private int hasPoll;
    private int initialDialogsType;
    private String initialSearchString;
    /* access modifiers changed from: private */
    public int lastMeasuredTopPadding;
    /* access modifiers changed from: private */
    public int maximumVelocity;
    /* access modifiers changed from: private */
    public boolean maybeStartTracking;
    /* access modifiers changed from: private */
    public MenuDrawable menuDrawable;
    private int messagesCount;
    /* access modifiers changed from: private */
    public ArrayList<MessagesController.DialogFilter> movingDialogFilters = new ArrayList<>();
    /* access modifiers changed from: private */
    public DialogCell movingView;
    /* access modifiers changed from: private */
    public boolean movingWas;
    private ActionBarMenuItem muteItem;
    /* access modifiers changed from: private */
    public boolean onlySelect;
    /* access modifiers changed from: private */
    public long openedDialogId;
    /* access modifiers changed from: private */
    public PacmanAnimation pacmanAnimation;
    /* access modifiers changed from: private */
    public ActionBarMenuItem passcodeItem;
    /* access modifiers changed from: private */
    public boolean passcodeItemVisible;
    private AlertDialog permissionDialog;
    private ActionBarMenuSubItem pin2Item;
    private ActionBarMenuItem pinItem;
    /* access modifiers changed from: private */
    public int prevPosition;
    /* access modifiers changed from: private */
    public int prevTop;
    /* access modifiers changed from: private */
    public float progressToActionMode;
    private ProxyDrawable proxyDrawable;
    /* access modifiers changed from: private */
    public ActionBarMenuItem proxyItem;
    /* access modifiers changed from: private */
    public boolean proxyItemVisible;
    private ActionBarMenuSubItem readItem;
    private ActionBarMenuSubItem removeFromFolderItem;
    private boolean resetDelegate = true;
    /* access modifiers changed from: private */
    public AnimatorSet scrimAnimatorSet;
    /* access modifiers changed from: private */
    public Paint scrimPaint;
    /* access modifiers changed from: private */
    public ActionBarPopupWindow scrimPopupWindow;
    /* access modifiers changed from: private */
    public ActionBarMenuSubItem[] scrimPopupWindowItems;
    /* access modifiers changed from: private */
    public View scrimView;
    /* access modifiers changed from: private */
    public int[] scrimViewLocation = new int[2];
    /* access modifiers changed from: private */
    public boolean scrimViewSelected;
    private boolean scrollBarVisible = true;
    /* access modifiers changed from: private */
    public boolean scrollUpdated;
    /* access modifiers changed from: private */
    public boolean scrollingManually;
    /* access modifiers changed from: private */
    public float searchAnimationProgress;
    /* access modifiers changed from: private */
    public AnimatorSet searchAnimator;
    private long searchDialogId;
    /* access modifiers changed from: private */
    public boolean searchIsShowed;
    /* access modifiers changed from: private */
    public ActionBarMenuItem searchItem;
    private TLObject searchObject;
    /* access modifiers changed from: private */
    public String searchString;
    /* access modifiers changed from: private */
    public ViewPagerFixed.TabsView searchTabsView;
    /* access modifiers changed from: private */
    public SearchViewPager searchViewPager;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searchWasFullyShowed;
    /* access modifiers changed from: private */
    public boolean searching;
    private String selectAlertString;
    private String selectAlertStringGroup;
    /* access modifiers changed from: private */
    public ArrayList<Long> selectedDialogs = new ArrayList<>();
    private NumberTextView selectedDialogsCountTextView;
    private String showingSuggestion;
    private RecyclerView sideMenu;
    /* access modifiers changed from: private */
    public DialogCell slidingView;
    /* access modifiers changed from: private */
    public long startArchivePullingTime;
    /* access modifiers changed from: private */
    public boolean startedTracking;
    /* access modifiers changed from: private */
    public ActionBarMenuItem switchItem;
    /* access modifiers changed from: private */
    public Animator tabsAlphaAnimator;
    /* access modifiers changed from: private */
    public AnimatorSet tabsAnimation;
    /* access modifiers changed from: private */
    public boolean tabsAnimationInProgress;
    /* access modifiers changed from: private */
    public int topPadding;
    /* access modifiers changed from: private */
    public UndoView[] undoView = new UndoView[2];
    /* access modifiers changed from: private */
    public boolean updatePullAfterScroll;
    /* access modifiers changed from: private */
    public ViewPage[] viewPages;
    /* access modifiers changed from: private */
    public boolean waitingForScrollFinished;
    /* access modifiers changed from: private */
    public boolean whiteActionBar;

    public interface DialogsActivityDelegate {
        void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList<Long> arrayList, CharSequence charSequence, boolean z);
    }

    static /* synthetic */ boolean lambda$createActionMode$9(View view, MotionEvent motionEvent) {
        return true;
    }

    static /* synthetic */ float lambda$static$0(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2 * f2 * f2) + 1.0f;
    }

    private class ViewPage extends FrameLayout {
        /* access modifiers changed from: private */
        public int archivePullViewState;
        /* access modifiers changed from: private */
        public DialogsAdapter dialogsAdapter;
        /* access modifiers changed from: private */
        public DialogsItemAnimator dialogsItemAnimator;
        /* access modifiers changed from: private */
        public int dialogsType;
        /* access modifiers changed from: private */
        public ItemTouchHelper itemTouchhelper;
        /* access modifiers changed from: private */
        public int lastItemsCount;
        /* access modifiers changed from: private */
        public LinearLayoutManager layoutManager;
        /* access modifiers changed from: private */
        public DialogsRecyclerView listView;
        /* access modifiers changed from: private */
        public RadialProgressView progressView;
        /* access modifiers changed from: private */
        public PullForegroundDrawable pullForegroundDrawable;
        /* access modifiers changed from: private */
        public RecyclerAnimationScrollHelper scrollHelper;
        /* access modifiers changed from: private */
        public int selectedType;
        /* access modifiers changed from: private */
        public SwipeController swipeController;

        static /* synthetic */ int access$8508(ViewPage viewPage) {
            int i = viewPage.lastItemsCount;
            viewPage.lastItemsCount = i + 1;
            return i;
        }

        static /* synthetic */ int access$8510(ViewPage viewPage) {
            int i = viewPage.lastItemsCount;
            viewPage.lastItemsCount = i - 1;
            return i;
        }

        public ViewPage(DialogsActivity dialogsActivity, Context context) {
            super(context);
        }

        public boolean isDefaultDialogType() {
            int i = this.dialogsType;
            return i == 0 || i == 7 || i == 8;
        }
    }

    private class ContentView extends SizeNotifierFrameLayout {
        private Paint actionBarSearchPaint = new Paint(1);
        private int inputFieldHeight;
        private int[] pos = new int[2];
        private int startedTrackingPointerId;
        private int startedTrackingX;
        private int startedTrackingY;
        private VelocityTracker velocityTracker;
        private Paint windowBackgroundPaint = new Paint();

        public boolean hasOverlappingRendering() {
            return false;
        }

        public ContentView(Context context) {
            super(context);
        }

        private boolean prepareForMoving(MotionEvent motionEvent, boolean z) {
            int nextPageId = DialogsActivity.this.filterTabsView.getNextPageId(z);
            if (nextPageId < 0) {
                return false;
            }
            getParent().requestDisallowInterceptTouchEvent(true);
            boolean unused = DialogsActivity.this.maybeStartTracking = false;
            boolean unused2 = DialogsActivity.this.startedTracking = true;
            this.startedTrackingX = (int) (motionEvent.getX() + DialogsActivity.this.additionalOffset);
            DialogsActivity.this.actionBar.setEnabled(false);
            DialogsActivity.this.filterTabsView.setEnabled(false);
            int unused3 = DialogsActivity.this.viewPages[1].selectedType = nextPageId;
            DialogsActivity.this.viewPages[1].setVisibility(0);
            boolean unused4 = DialogsActivity.this.animatingForward = z;
            DialogsActivity.this.showScrollbars(false);
            DialogsActivity.this.switchToCurrentSelectedMode(true);
            if (z) {
                DialogsActivity.this.viewPages[1].setTranslationX((float) DialogsActivity.this.viewPages[0].getMeasuredWidth());
            } else {
                DialogsActivity.this.viewPages[1].setTranslationX((float) (-DialogsActivity.this.viewPages[0].getMeasuredWidth()));
            }
            return true;
        }

        public void setPadding(int i, int i2, int i3, int i4) {
            int unused = DialogsActivity.this.topPadding = i2;
            DialogsActivity.this.updateContextViewPosition();
            if (!DialogsActivity.this.whiteActionBar || DialogsActivity.this.searchViewPager == null) {
                requestLayout();
            } else {
                DialogsActivity.this.searchViewPager.setTranslationY((float) (DialogsActivity.this.topPadding - DialogsActivity.this.lastMeasuredTopPadding));
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:19:0x00a1  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean checkTabsAnimationInProgress() {
            /*
                r7 = this;
                org.telegram.ui.DialogsActivity r0 = org.telegram.ui.DialogsActivity.this
                boolean r0 = r0.tabsAnimationInProgress
                r1 = 0
                if (r0 == 0) goto L_0x00c9
                org.telegram.ui.DialogsActivity r0 = org.telegram.ui.DialogsActivity.this
                boolean r0 = r0.backAnimation
                r2 = -1
                r3 = 0
                r4 = 1065353216(0x3var_, float:1.0)
                r5 = 1
                if (r0 == 0) goto L_0x0059
                org.telegram.ui.DialogsActivity r0 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.DialogsActivity$ViewPage[] r0 = r0.viewPages
                r0 = r0[r1]
                float r0 = r0.getTranslationX()
                float r0 = java.lang.Math.abs(r0)
                int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
                if (r0 >= 0) goto L_0x009e
                org.telegram.ui.DialogsActivity r0 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.DialogsActivity$ViewPage[] r0 = r0.viewPages
                r0 = r0[r1]
                r0.setTranslationX(r3)
                org.telegram.ui.DialogsActivity r0 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.DialogsActivity$ViewPage[] r0 = r0.viewPages
                r0 = r0[r5]
                org.telegram.ui.DialogsActivity r3 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.DialogsActivity$ViewPage[] r3 = r3.viewPages
                r3 = r3[r1]
                int r3 = r3.getMeasuredWidth()
                org.telegram.ui.DialogsActivity r4 = org.telegram.ui.DialogsActivity.this
                boolean r4 = r4.animatingForward
                if (r4 == 0) goto L_0x0052
                r2 = 1
            L_0x0052:
                int r3 = r3 * r2
                float r2 = (float) r3
                r0.setTranslationX(r2)
                goto L_0x009c
            L_0x0059:
                org.telegram.ui.DialogsActivity r0 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.DialogsActivity$ViewPage[] r0 = r0.viewPages
                r0 = r0[r5]
                float r0 = r0.getTranslationX()
                float r0 = java.lang.Math.abs(r0)
                int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
                if (r0 >= 0) goto L_0x009e
                org.telegram.ui.DialogsActivity r0 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.DialogsActivity$ViewPage[] r0 = r0.viewPages
                r0 = r0[r1]
                org.telegram.ui.DialogsActivity r4 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.DialogsActivity$ViewPage[] r4 = r4.viewPages
                r4 = r4[r1]
                int r4 = r4.getMeasuredWidth()
                org.telegram.ui.DialogsActivity r6 = org.telegram.ui.DialogsActivity.this
                boolean r6 = r6.animatingForward
                if (r6 == 0) goto L_0x008a
                goto L_0x008b
            L_0x008a:
                r2 = 1
            L_0x008b:
                int r4 = r4 * r2
                float r2 = (float) r4
                r0.setTranslationX(r2)
                org.telegram.ui.DialogsActivity r0 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.DialogsActivity$ViewPage[] r0 = r0.viewPages
                r0 = r0[r5]
                r0.setTranslationX(r3)
            L_0x009c:
                r0 = 1
                goto L_0x009f
            L_0x009e:
                r0 = 0
            L_0x009f:
                if (r0 == 0) goto L_0x00c2
                org.telegram.ui.DialogsActivity r0 = org.telegram.ui.DialogsActivity.this
                r0.showScrollbars(r5)
                org.telegram.ui.DialogsActivity r0 = org.telegram.ui.DialogsActivity.this
                android.animation.AnimatorSet r0 = r0.tabsAnimation
                if (r0 == 0) goto L_0x00bd
                org.telegram.ui.DialogsActivity r0 = org.telegram.ui.DialogsActivity.this
                android.animation.AnimatorSet r0 = r0.tabsAnimation
                r0.cancel()
                org.telegram.ui.DialogsActivity r0 = org.telegram.ui.DialogsActivity.this
                r2 = 0
                android.animation.AnimatorSet unused = r0.tabsAnimation = r2
            L_0x00bd:
                org.telegram.ui.DialogsActivity r0 = org.telegram.ui.DialogsActivity.this
                boolean unused = r0.tabsAnimationInProgress = r1
            L_0x00c2:
                org.telegram.ui.DialogsActivity r0 = org.telegram.ui.DialogsActivity.this
                boolean r0 = r0.tabsAnimationInProgress
                return r0
            L_0x00c9:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.ContentView.checkTabsAnimationInProgress():boolean");
        }

        public int getActionBarFullHeight() {
            float height = (float) DialogsActivity.this.actionBar.getHeight();
            float f = 0.0f;
            float measuredHeight = (DialogsActivity.this.filterTabsView == null || DialogsActivity.this.filterTabsView.getVisibility() == 8) ? 0.0f : (float) DialogsActivity.this.filterTabsView.getMeasuredHeight();
            if (!(DialogsActivity.this.searchTabsView == null || DialogsActivity.this.searchTabsView.getVisibility() == 8)) {
                f = (float) DialogsActivity.this.searchTabsView.getMeasuredHeight();
            }
            return (int) (height + (measuredHeight * (1.0f - DialogsActivity.this.searchAnimationProgress)) + (f * DialogsActivity.this.searchAnimationProgress));
        }

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View view, long j) {
            boolean z;
            if (view == DialogsActivity.this.viewPages[0] || ((DialogsActivity.this.viewPages.length > 1 && view == DialogsActivity.this.viewPages[1]) || view == DialogsActivity.this.fragmentContextView || view == DialogsActivity.this.fragmentLocationContextView)) {
                canvas.save();
                canvas.clipRect(0.0f, (-getY()) + DialogsActivity.this.actionBar.getY() + ((float) getActionBarFullHeight()), (float) getMeasuredWidth(), (float) getMeasuredHeight());
                z = super.drawChild(canvas, view, j);
                canvas.restore();
            } else {
                z = super.drawChild(canvas, view, j);
            }
            if (view == DialogsActivity.this.actionBar && DialogsActivity.this.parentLayout != null) {
                int y = (int) (DialogsActivity.this.actionBar.getY() + ((float) getActionBarFullHeight()));
                DialogsActivity.this.parentLayout.drawHeaderShadow(canvas, (int) ((1.0f - DialogsActivity.this.searchAnimationProgress) * 255.0f), y);
                if (DialogsActivity.this.searchAnimationProgress > 0.0f) {
                    if (DialogsActivity.this.searchAnimationProgress < 1.0f) {
                        int alpha = Theme.dividerPaint.getAlpha();
                        Theme.dividerPaint.setAlpha((int) (((float) alpha) * DialogsActivity.this.searchAnimationProgress));
                        float f = (float) y;
                        canvas.drawLine(0.0f, f, (float) getMeasuredWidth(), f, Theme.dividerPaint);
                        Theme.dividerPaint.setAlpha(alpha);
                    } else {
                        float f2 = (float) y;
                        canvas.drawLine(0.0f, f2, (float) getMeasuredWidth(), f2, Theme.dividerPaint);
                    }
                }
            }
            return z;
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            int i;
            Canvas canvas2 = canvas;
            int actionBarFullHeight = getActionBarFullHeight();
            if (DialogsActivity.this.inPreviewMode) {
                i = AndroidUtilities.statusBarHeight;
            } else {
                i = (int) ((-getY()) + DialogsActivity.this.actionBar.getY());
            }
            int i2 = i;
            String str = "actionBarDefault";
            if (DialogsActivity.this.whiteActionBar) {
                if (DialogsActivity.this.searchAnimationProgress == 1.0f) {
                    this.actionBarSearchPaint.setColor(Theme.getColor("windowBackgroundWhite"));
                    if (DialogsActivity.this.searchTabsView != null) {
                        DialogsActivity.this.searchTabsView.setTranslationY(0.0f);
                        DialogsActivity.this.searchTabsView.setAlpha(1.0f);
                        if (DialogsActivity.this.filtersView != null) {
                            DialogsActivity.this.filtersView.setTranslationY(0.0f);
                            DialogsActivity.this.filtersView.setAlpha(1.0f);
                        }
                    }
                } else if (DialogsActivity.this.searchAnimationProgress == 0.0f && DialogsActivity.this.filterTabsView != null && DialogsActivity.this.filterTabsView.getVisibility() == 0) {
                    DialogsActivity.this.filterTabsView.setTranslationY(DialogsActivity.this.actionBar.getTranslationY());
                }
                float f = (float) i2;
                int i3 = i2 + actionBarFullHeight;
                float f2 = (float) i3;
                int i4 = i3;
                float f3 = f;
                canvas.drawRect(0.0f, f, (float) getMeasuredWidth(), f2, DialogsActivity.this.searchAnimationProgress == 1.0f ? this.actionBarSearchPaint : DialogsActivity.this.actionBarDefaultPaint);
                if (DialogsActivity.this.searchAnimationProgress > 0.0f && DialogsActivity.this.searchAnimationProgress < 1.0f) {
                    Paint paint = this.actionBarSearchPaint;
                    if (DialogsActivity.this.folderId != 0) {
                        str = "actionBarDefaultArchived";
                    }
                    paint.setColor(ColorUtils.blendARGB(Theme.getColor(str), Theme.getColor("windowBackgroundWhite"), DialogsActivity.this.searchAnimationProgress));
                    if (DialogsActivity.this.searchIsShowed || !DialogsActivity.this.searchWasFullyShowed) {
                        canvas.save();
                        canvas2.clipRect(0, i2, getMeasuredWidth(), i4);
                        float measuredWidth = (float) (getMeasuredWidth() - AndroidUtilities.dp(24.0f));
                        int i5 = DialogsActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0;
                        canvas2.drawCircle(measuredWidth, ((float) i5) + (((float) (DialogsActivity.this.actionBar.getMeasuredHeight() - i5)) / 2.0f), ((float) getMeasuredWidth()) * 1.3f * DialogsActivity.this.searchAnimationProgress, this.actionBarSearchPaint);
                        canvas.restore();
                    } else {
                        canvas.drawRect(0.0f, f3, (float) getMeasuredWidth(), f2, this.actionBarSearchPaint);
                    }
                    if (DialogsActivity.this.filterTabsView != null && DialogsActivity.this.filterTabsView.getVisibility() == 0) {
                        DialogsActivity.this.filterTabsView.setTranslationY((float) (actionBarFullHeight - (DialogsActivity.this.actionBar.getHeight() + DialogsActivity.this.filterTabsView.getMeasuredHeight())));
                    }
                    if (DialogsActivity.this.searchTabsView != null) {
                        float height = (float) (actionBarFullHeight - (DialogsActivity.this.actionBar.getHeight() + DialogsActivity.this.searchTabsView.getMeasuredHeight()));
                        float access$2200 = DialogsActivity.this.searchAnimationProgress < 0.5f ? 0.0f : (DialogsActivity.this.searchAnimationProgress - 0.5f) / 0.5f;
                        DialogsActivity.this.searchTabsView.setTranslationY(height);
                        DialogsActivity.this.searchTabsView.setAlpha(access$2200);
                        if (DialogsActivity.this.filtersView != null) {
                            DialogsActivity.this.filtersView.setTranslationY(height);
                            DialogsActivity.this.filtersView.setAlpha(access$2200);
                        }
                    }
                }
            } else if (!DialogsActivity.this.inPreviewMode) {
                if (DialogsActivity.this.progressToActionMode > 0.0f) {
                    Paint paint2 = this.actionBarSearchPaint;
                    if (DialogsActivity.this.folderId != 0) {
                        str = "actionBarDefaultArchived";
                    }
                    paint2.setColor(ColorUtils.blendARGB(Theme.getColor(str), Theme.getColor("windowBackgroundWhite"), DialogsActivity.this.progressToActionMode));
                    canvas.drawRect(0.0f, (float) i2, (float) getMeasuredWidth(), (float) (i2 + actionBarFullHeight), this.actionBarSearchPaint);
                } else {
                    canvas.drawRect(0.0f, (float) i2, (float) getMeasuredWidth(), (float) (i2 + actionBarFullHeight), DialogsActivity.this.actionBarDefaultPaint);
                }
            }
            super.dispatchDraw(canvas);
            if (DialogsActivity.this.whiteActionBar && DialogsActivity.this.searchAnimationProgress > 0.0f && DialogsActivity.this.searchAnimationProgress < 1.0f && DialogsActivity.this.searchTabsView != null) {
                this.windowBackgroundPaint.setColor(Theme.getColor("windowBackgroundWhite"));
                Paint paint3 = this.windowBackgroundPaint;
                paint3.setAlpha((int) (((float) paint3.getAlpha()) * DialogsActivity.this.searchAnimationProgress));
                canvas.drawRect(0.0f, (float) (actionBarFullHeight + i2), (float) getMeasuredWidth(), (float) (i2 + DialogsActivity.this.actionBar.getMeasuredHeight() + DialogsActivity.this.searchTabsView.getMeasuredHeight()), this.windowBackgroundPaint);
            }
            if (DialogsActivity.this.scrimView != null) {
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), DialogsActivity.this.scrimPaint);
                canvas.save();
                getLocationInWindow(this.pos);
                canvas2.translate((float) (DialogsActivity.this.scrimViewLocation[0] - this.pos[0]), (float) (DialogsActivity.this.scrimViewLocation[1] - (Build.VERSION.SDK_INT < 21 ? AndroidUtilities.statusBarHeight : 0)));
                DialogsActivity.this.scrimView.draw(canvas2);
                if (DialogsActivity.this.scrimViewSelected) {
                    Drawable selectorDrawable = DialogsActivity.this.filterTabsView.getSelectorDrawable();
                    canvas2.translate((float) (-DialogsActivity.this.scrimViewLocation[0]), (float) ((-selectorDrawable.getIntrinsicHeight()) - 1));
                    selectorDrawable.draw(canvas2);
                }
                canvas.restore();
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            int i3;
            int size = View.MeasureSpec.getSize(i);
            int size2 = View.MeasureSpec.getSize(i2);
            setMeasuredDimension(size, size2);
            int paddingTop = size2 - getPaddingTop();
            if (DialogsActivity.this.doneItem != null) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) DialogsActivity.this.doneItem.getLayoutParams();
                layoutParams.topMargin = DialogsActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0;
                layoutParams.height = ActionBar.getCurrentActionBarHeight();
            }
            measureChildWithMargins(DialogsActivity.this.actionBar, i, 0, i2, 0);
            int measureKeyboardHeight = measureKeyboardHeight();
            int childCount = getChildCount();
            if (DialogsActivity.this.commentView != null) {
                measureChildWithMargins(DialogsActivity.this.commentView, i, 0, i2, 0);
                Object tag = DialogsActivity.this.commentView.getTag();
                if (tag == null || !tag.equals(2)) {
                    this.inputFieldHeight = 0;
                } else {
                    if (measureKeyboardHeight <= AndroidUtilities.dp(20.0f) && !AndroidUtilities.isInMultiwindow) {
                        paddingTop -= DialogsActivity.this.commentView.getEmojiPadding();
                    }
                    this.inputFieldHeight = DialogsActivity.this.commentView.getMeasuredHeight();
                }
                if (SharedConfig.smoothKeyboard && DialogsActivity.this.commentView.isPopupShowing()) {
                    DialogsActivity.this.fragmentView.setTranslationY(0.0f);
                    for (int i4 = 0; i4 < DialogsActivity.this.viewPages.length; i4++) {
                        if (DialogsActivity.this.viewPages[i4] != null) {
                            DialogsActivity.this.viewPages[i4].setTranslationY(0.0f);
                        }
                    }
                    if (!DialogsActivity.this.onlySelect) {
                        DialogsActivity.this.actionBar.setTranslationY(0.0f);
                    }
                    DialogsActivity.this.searchViewPager.setTranslationY(0.0f);
                }
            }
            for (int i5 = 0; i5 < childCount; i5++) {
                View childAt = getChildAt(i5);
                if (!(childAt == null || childAt.getVisibility() == 8 || childAt == DialogsActivity.this.commentView || childAt == DialogsActivity.this.actionBar)) {
                    if (childAt instanceof ViewPage) {
                        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(size, NUM);
                        if (DialogsActivity.this.filterTabsView == null || DialogsActivity.this.filterTabsView.getVisibility() != 0) {
                            i3 = (((paddingTop - this.inputFieldHeight) + AndroidUtilities.dp(2.0f)) - (DialogsActivity.this.onlySelect ? 0 : DialogsActivity.this.actionBar.getMeasuredHeight())) - DialogsActivity.this.topPadding;
                        } else {
                            i3 = (((paddingTop - this.inputFieldHeight) + AndroidUtilities.dp(2.0f)) - AndroidUtilities.dp(44.0f)) - DialogsActivity.this.topPadding;
                        }
                        childAt.measure(makeMeasureSpec, View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), i3), NUM));
                        childAt.setPivotX((float) (childAt.getMeasuredWidth() / 2));
                    } else if (childAt == DialogsActivity.this.searchViewPager) {
                        DialogsActivity.this.searchViewPager.setTranslationY(0.0f);
                        childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), ((((View.MeasureSpec.getSize(i2) + measureKeyboardHeight) - this.inputFieldHeight) + AndroidUtilities.dp(2.0f)) - (DialogsActivity.this.onlySelect ? 0 : DialogsActivity.this.actionBar.getMeasuredHeight())) - DialogsActivity.this.topPadding) - (DialogsActivity.this.searchTabsView == null ? 0 : AndroidUtilities.dp(44.0f)), NUM));
                        childAt.setPivotX((float) (childAt.getMeasuredWidth() / 2));
                    } else if (DialogsActivity.this.commentView == null || !DialogsActivity.this.commentView.isPopupView(childAt)) {
                        measureChildWithMargins(childAt, i, 0, i2, 0);
                    } else if (!AndroidUtilities.isInMultiwindow) {
                        childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, NUM));
                    } else if (AndroidUtilities.isTablet()) {
                        childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(320.0f), ((paddingTop - this.inputFieldHeight) - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                    } else {
                        childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(((paddingTop - this.inputFieldHeight) - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:31:0x0097  */
        /* JADX WARNING: Removed duplicated region for block: B:38:0x00b2  */
        /* JADX WARNING: Removed duplicated region for block: B:44:0x00d3  */
        /* JADX WARNING: Removed duplicated region for block: B:49:0x00fb  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onLayout(boolean r15, int r16, int r17, int r18, int r19) {
            /*
                r14 = this;
                r0 = r14
                int r1 = r14.getChildCount()
                org.telegram.ui.DialogsActivity r2 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.ChatActivityEnterView r2 = r2.commentView
                if (r2 == 0) goto L_0x0018
                org.telegram.ui.DialogsActivity r2 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.ChatActivityEnterView r2 = r2.commentView
                java.lang.Object r2 = r2.getTag()
                goto L_0x0019
            L_0x0018:
                r2 = 0
            L_0x0019:
                int r3 = r14.measureKeyboardHeight()
                r4 = 2
                r5 = 0
                if (r2 == 0) goto L_0x0042
                java.lang.Integer r6 = java.lang.Integer.valueOf(r4)
                boolean r2 = r2.equals(r6)
                if (r2 == 0) goto L_0x0042
                r2 = 1101004800(0x41a00000, float:20.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                if (r3 > r2) goto L_0x0042
                boolean r2 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
                if (r2 != 0) goto L_0x0042
                org.telegram.ui.DialogsActivity r2 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.ChatActivityEnterView r2 = r2.commentView
                int r2 = r2.getEmojiPadding()
                goto L_0x0043
            L_0x0042:
                r2 = 0
            L_0x0043:
                r14.setBottomClip(r2)
                org.telegram.ui.DialogsActivity r6 = org.telegram.ui.DialogsActivity.this
                int r7 = r6.topPadding
                int unused = r6.lastMeasuredTopPadding = r7
                r6 = 0
            L_0x0050:
                if (r6 >= r1) goto L_0x01a3
                android.view.View r7 = r14.getChildAt(r6)
                int r8 = r7.getVisibility()
                r9 = 8
                if (r8 != r9) goto L_0x0060
                goto L_0x019f
            L_0x0060:
                android.view.ViewGroup$LayoutParams r8 = r7.getLayoutParams()
                android.widget.FrameLayout$LayoutParams r8 = (android.widget.FrameLayout.LayoutParams) r8
                int r9 = r7.getMeasuredWidth()
                int r10 = r7.getMeasuredHeight()
                int r11 = r8.gravity
                r12 = -1
                if (r11 != r12) goto L_0x0075
                r11 = 51
            L_0x0075:
                r12 = r11 & 7
                r11 = r11 & 112(0x70, float:1.57E-43)
                r12 = r12 & 7
                r13 = 1
                if (r12 == r13) goto L_0x0089
                r13 = 5
                if (r12 == r13) goto L_0x0084
                int r12 = r8.leftMargin
                goto L_0x0093
            L_0x0084:
                int r12 = r18 - r9
                int r13 = r8.rightMargin
                goto L_0x0092
            L_0x0089:
                int r12 = r18 - r16
                int r12 = r12 - r9
                int r12 = r12 / r4
                int r13 = r8.leftMargin
                int r12 = r12 + r13
                int r13 = r8.rightMargin
            L_0x0092:
                int r12 = r12 - r13
            L_0x0093:
                r13 = 16
                if (r11 == r13) goto L_0x00b2
                r13 = 48
                if (r11 == r13) goto L_0x00aa
                r13 = 80
                if (r11 == r13) goto L_0x00a2
                int r8 = r8.topMargin
                goto L_0x00bf
            L_0x00a2:
                int r11 = r19 - r2
                int r11 = r11 - r17
                int r11 = r11 - r10
                int r8 = r8.bottomMargin
                goto L_0x00bd
            L_0x00aa:
                int r8 = r8.topMargin
                int r11 = r14.getPaddingTop()
                int r8 = r8 + r11
                goto L_0x00bf
            L_0x00b2:
                int r11 = r19 - r2
                int r11 = r11 - r17
                int r11 = r11 - r10
                int r11 = r11 / r4
                int r13 = r8.topMargin
                int r11 = r11 + r13
                int r8 = r8.bottomMargin
            L_0x00bd:
                int r8 = r11 - r8
            L_0x00bf:
                org.telegram.ui.DialogsActivity r11 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.ChatActivityEnterView r11 = r11.commentView
                if (r11 == 0) goto L_0x00fb
                org.telegram.ui.DialogsActivity r11 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.ChatActivityEnterView r11 = r11.commentView
                boolean r11 = r11.isPopupView(r7)
                if (r11 == 0) goto L_0x00fb
                boolean r8 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
                if (r8 == 0) goto L_0x00ef
                org.telegram.ui.DialogsActivity r8 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.ChatActivityEnterView r8 = r8.commentView
                int r8 = r8.getTop()
                int r11 = r7.getMeasuredHeight()
                int r8 = r8 - r11
                r11 = 1065353216(0x3var_, float:1.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            L_0x00ec:
                int r8 = r8 + r11
                goto L_0x019a
            L_0x00ef:
                org.telegram.ui.DialogsActivity r8 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.ChatActivityEnterView r8 = r8.commentView
                int r8 = r8.getBottom()
                goto L_0x019a
            L_0x00fb:
                org.telegram.ui.DialogsActivity r11 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.FilterTabsView r11 = r11.filterTabsView
                if (r7 == r11) goto L_0x0190
                org.telegram.ui.DialogsActivity r11 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.ViewPagerFixed$TabsView r11 = r11.searchTabsView
                if (r7 == r11) goto L_0x0190
                org.telegram.ui.DialogsActivity r11 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Adapters.FiltersView r11 = r11.filtersView
                if (r7 != r11) goto L_0x0115
                goto L_0x0190
            L_0x0115:
                org.telegram.ui.DialogsActivity r11 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.SearchViewPager r11 = r11.searchViewPager
                r13 = 1110441984(0x42300000, float:44.0)
                if (r7 != r11) goto L_0x0149
                org.telegram.ui.DialogsActivity r8 = org.telegram.ui.DialogsActivity.this
                boolean r8 = r8.onlySelect
                if (r8 == 0) goto L_0x0129
                r8 = 0
                goto L_0x0133
            L_0x0129:
                org.telegram.ui.DialogsActivity r8 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.ActionBar.ActionBar r8 = r8.actionBar
                int r8 = r8.getMeasuredHeight()
            L_0x0133:
                org.telegram.ui.DialogsActivity r11 = org.telegram.ui.DialogsActivity.this
                int r11 = r11.topPadding
                int r8 = r8 + r11
                org.telegram.ui.DialogsActivity r11 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.ViewPagerFixed$TabsView r11 = r11.searchTabsView
                if (r11 != 0) goto L_0x0144
                r11 = 0
                goto L_0x00ec
            L_0x0144:
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r13)
                goto L_0x00ec
            L_0x0149:
                boolean r11 = r7 instanceof org.telegram.ui.DialogsActivity.ViewPage
                if (r11 == 0) goto L_0x0180
                org.telegram.ui.DialogsActivity r11 = org.telegram.ui.DialogsActivity.this
                boolean r11 = r11.onlySelect
                if (r11 != 0) goto L_0x0178
                org.telegram.ui.DialogsActivity r8 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.FilterTabsView r8 = r8.filterTabsView
                if (r8 == 0) goto L_0x016e
                org.telegram.ui.DialogsActivity r8 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.FilterTabsView r8 = r8.filterTabsView
                int r8 = r8.getVisibility()
                if (r8 != 0) goto L_0x016e
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r13)
                goto L_0x0178
            L_0x016e:
                org.telegram.ui.DialogsActivity r8 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.ActionBar.ActionBar r8 = r8.actionBar
                int r8 = r8.getMeasuredHeight()
            L_0x0178:
                org.telegram.ui.DialogsActivity r11 = org.telegram.ui.DialogsActivity.this
                int r11 = r11.topPadding
                goto L_0x00ec
            L_0x0180:
                boolean r11 = r7 instanceof org.telegram.ui.Components.FragmentContextView
                if (r11 == 0) goto L_0x019a
                org.telegram.ui.DialogsActivity r11 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.ActionBar.ActionBar r11 = r11.actionBar
                int r11 = r11.getMeasuredHeight()
                goto L_0x00ec
            L_0x0190:
                org.telegram.ui.DialogsActivity r8 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.ActionBar.ActionBar r8 = r8.actionBar
                int r8 = r8.getMeasuredHeight()
            L_0x019a:
                int r9 = r9 + r12
                int r10 = r10 + r8
                r7.layout(r12, r8, r9, r10)
            L_0x019f:
                int r6 = r6 + 1
                goto L_0x0050
            L_0x01a3:
                org.telegram.ui.DialogsActivity r1 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.SearchViewPager r1 = r1.searchViewPager
                r1.setKeyboardHeight(r3)
                r14.notifyHeightChanged()
                org.telegram.ui.DialogsActivity r1 = org.telegram.ui.DialogsActivity.this
                r1.updateContextViewPosition()
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.ContentView.onLayout(boolean, int, int, int, int):void");
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            int actionMasked = motionEvent.getActionMasked();
            if ((actionMasked == 1 || actionMasked == 3) && DialogsActivity.this.actionBar.isActionModeShowed()) {
                boolean unused = DialogsActivity.this.allowMoving = true;
            }
            if (checkTabsAnimationInProgress()) {
                return true;
            }
            if ((DialogsActivity.this.filterTabsView == null || !DialogsActivity.this.filterTabsView.isAnimatingIndicator()) && !onTouchEvent(motionEvent)) {
                return false;
            }
            return true;
        }

        public void requestDisallowInterceptTouchEvent(boolean z) {
            if (DialogsActivity.this.maybeStartTracking && !DialogsActivity.this.startedTracking) {
                onTouchEvent((MotionEvent) null);
            }
            super.requestDisallowInterceptTouchEvent(z);
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            float f;
            float f2;
            float f3;
            int i;
            boolean z = false;
            if (DialogsActivity.this.filterTabsView == null || DialogsActivity.this.filterTabsView.isEditing() || DialogsActivity.this.searching || DialogsActivity.this.parentLayout.checkTransitionAnimation() || DialogsActivity.this.parentLayout.isInPreviewMode() || DialogsActivity.this.parentLayout.isPreviewOpenAnimationInProgress() || DialogsActivity.this.parentLayout.getDrawerLayoutContainer().isDrawerOpened() || (motionEvent != null && !DialogsActivity.this.startedTracking && motionEvent.getY() <= ((float) DialogsActivity.this.actionBar.getMeasuredHeight()) + DialogsActivity.this.actionBar.getTranslationY())) {
                return false;
            }
            if (motionEvent != null) {
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                this.velocityTracker.addMovement(motionEvent);
            }
            if (motionEvent != null && motionEvent.getAction() == 0 && checkTabsAnimationInProgress()) {
                boolean unused = DialogsActivity.this.startedTracking = true;
                this.startedTrackingPointerId = motionEvent.getPointerId(0);
                this.startedTrackingX = (int) motionEvent.getX();
                DialogsActivity.this.parentLayout.getDrawerLayoutContainer().setAllowOpenDrawerBySwipe(false);
                if (DialogsActivity.this.animatingForward) {
                    if (((float) this.startedTrackingX) < ((float) DialogsActivity.this.viewPages[0].getMeasuredWidth()) + DialogsActivity.this.viewPages[0].getTranslationX()) {
                        DialogsActivity dialogsActivity = DialogsActivity.this;
                        float unused2 = dialogsActivity.additionalOffset = dialogsActivity.viewPages[0].getTranslationX();
                    } else {
                        ViewPage viewPage = DialogsActivity.this.viewPages[0];
                        DialogsActivity.this.viewPages[0] = DialogsActivity.this.viewPages[1];
                        DialogsActivity.this.viewPages[1] = viewPage;
                        boolean unused3 = DialogsActivity.this.animatingForward = false;
                        DialogsActivity dialogsActivity2 = DialogsActivity.this;
                        float unused4 = dialogsActivity2.additionalOffset = dialogsActivity2.viewPages[0].getTranslationX();
                        DialogsActivity.this.filterTabsView.selectTabWithId(DialogsActivity.this.viewPages[0].selectedType, 1.0f);
                        DialogsActivity.this.filterTabsView.selectTabWithId(DialogsActivity.this.viewPages[1].selectedType, DialogsActivity.this.additionalOffset / ((float) DialogsActivity.this.viewPages[0].getMeasuredWidth()));
                        DialogsActivity.this.switchToCurrentSelectedMode(true);
                    }
                } else if (((float) this.startedTrackingX) < ((float) DialogsActivity.this.viewPages[1].getMeasuredWidth()) + DialogsActivity.this.viewPages[1].getTranslationX()) {
                    ViewPage viewPage2 = DialogsActivity.this.viewPages[0];
                    DialogsActivity.this.viewPages[0] = DialogsActivity.this.viewPages[1];
                    DialogsActivity.this.viewPages[1] = viewPage2;
                    boolean unused5 = DialogsActivity.this.animatingForward = true;
                    DialogsActivity dialogsActivity3 = DialogsActivity.this;
                    float unused6 = dialogsActivity3.additionalOffset = dialogsActivity3.viewPages[0].getTranslationX();
                    DialogsActivity.this.filterTabsView.selectTabWithId(DialogsActivity.this.viewPages[0].selectedType, 1.0f);
                    DialogsActivity.this.filterTabsView.selectTabWithId(DialogsActivity.this.viewPages[1].selectedType, (-DialogsActivity.this.additionalOffset) / ((float) DialogsActivity.this.viewPages[0].getMeasuredWidth()));
                    DialogsActivity.this.switchToCurrentSelectedMode(true);
                } else {
                    DialogsActivity dialogsActivity4 = DialogsActivity.this;
                    float unused7 = dialogsActivity4.additionalOffset = dialogsActivity4.viewPages[0].getTranslationX();
                }
                DialogsActivity.this.tabsAnimation.removeAllListeners();
                DialogsActivity.this.tabsAnimation.cancel();
                boolean unused8 = DialogsActivity.this.tabsAnimationInProgress = false;
            } else if (motionEvent != null && motionEvent.getAction() == 0) {
                float unused9 = DialogsActivity.this.additionalOffset = 0.0f;
            }
            if (motionEvent != null && motionEvent.getAction() == 0 && !DialogsActivity.this.startedTracking && !DialogsActivity.this.maybeStartTracking && DialogsActivity.this.filterTabsView.getVisibility() == 0) {
                this.startedTrackingPointerId = motionEvent.getPointerId(0);
                boolean unused10 = DialogsActivity.this.maybeStartTracking = true;
                this.startedTrackingX = (int) motionEvent.getX();
                this.startedTrackingY = (int) motionEvent.getY();
                this.velocityTracker.clear();
            } else if (motionEvent != null && motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
                int x = (int) ((motionEvent.getX() - ((float) this.startedTrackingX)) + DialogsActivity.this.additionalOffset);
                int abs = Math.abs(((int) motionEvent.getY()) - this.startedTrackingY);
                if (DialogsActivity.this.startedTracking && ((DialogsActivity.this.animatingForward && x > 0) || (!DialogsActivity.this.animatingForward && x < 0))) {
                    if (!prepareForMoving(motionEvent, x < 0)) {
                        boolean unused11 = DialogsActivity.this.maybeStartTracking = true;
                        boolean unused12 = DialogsActivity.this.startedTracking = false;
                        DialogsActivity.this.viewPages[0].setTranslationX(0.0f);
                        DialogsActivity.this.viewPages[1].setTranslationX((float) (DialogsActivity.this.animatingForward ? DialogsActivity.this.viewPages[0].getMeasuredWidth() : -DialogsActivity.this.viewPages[0].getMeasuredWidth()));
                        DialogsActivity.this.filterTabsView.selectTabWithId(DialogsActivity.this.viewPages[1].selectedType, 0.0f);
                    }
                }
                if (DialogsActivity.this.maybeStartTracking && !DialogsActivity.this.startedTracking) {
                    float pixelsInCM = AndroidUtilities.getPixelsInCM(0.3f, true);
                    int x2 = (int) (motionEvent.getX() - ((float) this.startedTrackingX));
                    if (((float) Math.abs(x2)) >= pixelsInCM && Math.abs(x2) > abs) {
                        if (x < 0) {
                            z = true;
                        }
                        prepareForMoving(motionEvent, z);
                    }
                } else if (DialogsActivity.this.startedTracking) {
                    DialogsActivity.this.viewPages[0].setTranslationX((float) x);
                    if (DialogsActivity.this.animatingForward) {
                        DialogsActivity.this.viewPages[1].setTranslationX((float) (DialogsActivity.this.viewPages[0].getMeasuredWidth() + x));
                    } else {
                        DialogsActivity.this.viewPages[1].setTranslationX((float) (x - DialogsActivity.this.viewPages[0].getMeasuredWidth()));
                    }
                    DialogsActivity.this.filterTabsView.selectTabWithId(DialogsActivity.this.viewPages[1].selectedType, ((float) Math.abs(x)) / ((float) DialogsActivity.this.viewPages[0].getMeasuredWidth()));
                }
            } else if (motionEvent == null || (motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6))) {
                this.velocityTracker.computeCurrentVelocity(1000, (float) DialogsActivity.this.maximumVelocity);
                if (motionEvent == null || motionEvent.getAction() == 3) {
                    f2 = 0.0f;
                    f = 0.0f;
                } else {
                    f2 = this.velocityTracker.getXVelocity();
                    f = this.velocityTracker.getYVelocity();
                    if (!DialogsActivity.this.startedTracking && Math.abs(f2) >= 3000.0f && Math.abs(f2) > Math.abs(f)) {
                        prepareForMoving(motionEvent, f2 < 0.0f);
                    }
                }
                if (DialogsActivity.this.startedTracking) {
                    float x3 = DialogsActivity.this.viewPages[0].getX();
                    AnimatorSet unused13 = DialogsActivity.this.tabsAnimation = new AnimatorSet();
                    if (DialogsActivity.this.additionalOffset == 0.0f) {
                        boolean unused14 = DialogsActivity.this.backAnimation = Math.abs(x3) < ((float) DialogsActivity.this.viewPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(f2) < 3500.0f || Math.abs(f2) < Math.abs(f));
                    } else if (Math.abs(f2) > 1500.0f) {
                        DialogsActivity dialogsActivity5 = DialogsActivity.this;
                        boolean unused15 = dialogsActivity5.backAnimation = !dialogsActivity5.animatingForward ? f2 < 0.0f : f2 > 0.0f;
                    } else if (DialogsActivity.this.animatingForward) {
                        DialogsActivity dialogsActivity6 = DialogsActivity.this;
                        boolean unused16 = dialogsActivity6.backAnimation = dialogsActivity6.viewPages[1].getX() > ((float) (DialogsActivity.this.viewPages[0].getMeasuredWidth() >> 1));
                    } else {
                        DialogsActivity dialogsActivity7 = DialogsActivity.this;
                        boolean unused17 = dialogsActivity7.backAnimation = dialogsActivity7.viewPages[0].getX() < ((float) (DialogsActivity.this.viewPages[0].getMeasuredWidth() >> 1));
                    }
                    if (DialogsActivity.this.backAnimation) {
                        f3 = Math.abs(x3);
                        if (DialogsActivity.this.animatingForward) {
                            DialogsActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(DialogsActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(DialogsActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{(float) DialogsActivity.this.viewPages[1].getMeasuredWidth()})});
                        } else {
                            DialogsActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(DialogsActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(DialogsActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{(float) (-DialogsActivity.this.viewPages[1].getMeasuredWidth())})});
                        }
                    } else {
                        f3 = ((float) DialogsActivity.this.viewPages[0].getMeasuredWidth()) - Math.abs(x3);
                        if (DialogsActivity.this.animatingForward) {
                            DialogsActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(DialogsActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{(float) (-DialogsActivity.this.viewPages[0].getMeasuredWidth())}), ObjectAnimator.ofFloat(DialogsActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{0.0f})});
                        } else {
                            DialogsActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(DialogsActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{(float) DialogsActivity.this.viewPages[0].getMeasuredWidth()}), ObjectAnimator.ofFloat(DialogsActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{0.0f})});
                        }
                    }
                    DialogsActivity.this.tabsAnimation.setInterpolator(DialogsActivity.interpolator);
                    int measuredWidth = getMeasuredWidth();
                    float f4 = (float) (measuredWidth / 2);
                    float distanceInfluenceForSnapDuration = f4 + (AndroidUtilities.distanceInfluenceForSnapDuration(Math.min(1.0f, (f3 * 1.0f) / ((float) measuredWidth))) * f4);
                    float abs2 = Math.abs(f2);
                    if (abs2 > 0.0f) {
                        i = Math.round(Math.abs(distanceInfluenceForSnapDuration / abs2) * 1000.0f) * 4;
                    } else {
                        i = (int) (((f3 / ((float) getMeasuredWidth())) + 1.0f) * 100.0f);
                    }
                    DialogsActivity.this.tabsAnimation.setDuration((long) Math.max(150, Math.min(i, 600)));
                    DialogsActivity.this.tabsAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            AnimatorSet unused = DialogsActivity.this.tabsAnimation = null;
                            if (!DialogsActivity.this.backAnimation) {
                                ViewPage viewPage = DialogsActivity.this.viewPages[0];
                                DialogsActivity.this.viewPages[0] = DialogsActivity.this.viewPages[1];
                                DialogsActivity.this.viewPages[1] = viewPage;
                                DialogsActivity.this.filterTabsView.selectTabWithId(DialogsActivity.this.viewPages[0].selectedType, 1.0f);
                                DialogsActivity.this.updateCounters(false);
                            }
                            if (DialogsActivity.this.parentLayout != null) {
                                DialogsActivity.this.parentLayout.getDrawerLayoutContainer().setAllowOpenDrawerBySwipe(DialogsActivity.this.viewPages[0].selectedType == DialogsActivity.this.filterTabsView.getFirstTabId() || DialogsActivity.this.searchIsShowed);
                            }
                            DialogsActivity.this.viewPages[1].setVisibility(8);
                            DialogsActivity.this.showScrollbars(true);
                            boolean unused2 = DialogsActivity.this.tabsAnimationInProgress = false;
                            boolean unused3 = DialogsActivity.this.maybeStartTracking = false;
                            DialogsActivity.this.actionBar.setEnabled(true);
                            DialogsActivity.this.filterTabsView.setEnabled(true);
                            DialogsActivity dialogsActivity = DialogsActivity.this;
                            dialogsActivity.checkListLoad(dialogsActivity.viewPages[0]);
                        }
                    });
                    DialogsActivity.this.tabsAnimation.start();
                    boolean unused18 = DialogsActivity.this.tabsAnimationInProgress = true;
                    boolean unused19 = DialogsActivity.this.startedTracking = false;
                } else {
                    DialogsActivity.this.parentLayout.getDrawerLayoutContainer().setAllowOpenDrawerBySwipe(DialogsActivity.this.viewPages[0].selectedType == DialogsActivity.this.filterTabsView.getFirstTabId() || DialogsActivity.this.searchIsShowed);
                    boolean unused20 = DialogsActivity.this.maybeStartTracking = false;
                    DialogsActivity.this.actionBar.setEnabled(true);
                    DialogsActivity.this.filterTabsView.setEnabled(true);
                }
                VelocityTracker velocityTracker2 = this.velocityTracker;
                if (velocityTracker2 != null) {
                    velocityTracker2.recycle();
                    this.velocityTracker = null;
                }
            }
            return DialogsActivity.this.startedTracking;
        }
    }

    public class DialogsRecyclerView extends RecyclerListView {
        private int appliedPaddingTop;
        private boolean firstLayout = true;
        private boolean ignoreLayout;
        private ViewPage parentPage;

        public DialogsRecyclerView(Context context, ViewPage viewPage) {
            super(context);
            this.parentPage = viewPage;
        }

        public void setViewsOffset(float f) {
            View findViewByPosition;
            DialogsActivity.viewOffset = f;
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                getChildAt(i).setTranslationY(f);
            }
            if (!(this.selectorPosition == -1 || (findViewByPosition = getLayoutManager().findViewByPosition(this.selectorPosition)) == null)) {
                this.selectorRect.set(findViewByPosition.getLeft(), (int) (((float) findViewByPosition.getTop()) + f), findViewByPosition.getRight(), (int) (((float) findViewByPosition.getBottom()) + f));
                this.selectorDrawable.setBounds(this.selectorRect);
            }
            invalidate();
        }

        public float getViewOffset() {
            return DialogsActivity.viewOffset;
        }

        public void addView(View view, int i, ViewGroup.LayoutParams layoutParams) {
            super.addView(view, i, layoutParams);
            view.setTranslationY(DialogsActivity.viewOffset);
        }

        public void removeView(View view) {
            super.removeView(view);
            view.setTranslationY(0.0f);
        }

        public void onDraw(Canvas canvas) {
            if (!(this.parentPage.pullForegroundDrawable == null || DialogsActivity.viewOffset == 0.0f)) {
                int paddingTop = getPaddingTop();
                if (paddingTop != 0) {
                    canvas.save();
                    canvas.translate(0.0f, (float) paddingTop);
                }
                this.parentPage.pullForegroundDrawable.drawOverScroll(canvas);
                if (paddingTop != 0) {
                    canvas.restore();
                }
            }
            super.onDraw(canvas);
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            if (DialogsActivity.this.slidingView != null && DialogsActivity.this.pacmanAnimation != null) {
                DialogsActivity.this.pacmanAnimation.draw(canvas, DialogsActivity.this.slidingView.getTop() + (DialogsActivity.this.slidingView.getMeasuredHeight() / 2));
            }
        }

        public void setAdapter(RecyclerView.Adapter adapter) {
            super.setAdapter(adapter);
            this.firstLayout = true;
        }

        private void checkIfAdapterValid() {
            RecyclerView.Adapter adapter = getAdapter();
            if (this.parentPage.lastItemsCount != adapter.getItemCount()) {
                this.ignoreLayout = true;
                adapter.notifyDataSetChanged();
                this.ignoreLayout = false;
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            int i3;
            int findFirstVisibleItemPosition;
            RecyclerView.ViewHolder findViewHolderForAdapterPosition;
            if (!DialogsActivity.this.onlySelect) {
                this.ignoreLayout = true;
                if (DialogsActivity.this.filterTabsView == null || DialogsActivity.this.filterTabsView.getVisibility() != 0) {
                    i3 = (!DialogsActivity.this.inPreviewMode || Build.VERSION.SDK_INT < 21) ? 0 : AndroidUtilities.statusBarHeight;
                } else {
                    i3 = ActionBar.getCurrentActionBarHeight() + (DialogsActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
                }
                setTopGlowOffset(i3);
                setPadding(0, i3, 0, 0);
                if (!(this.appliedPaddingTop == i3 || (findFirstVisibleItemPosition = this.parentPage.layoutManager.findFirstVisibleItemPosition()) == -1 || (findViewHolderForAdapterPosition = this.parentPage.listView.findViewHolderForAdapterPosition(findFirstVisibleItemPosition)) == null)) {
                    this.parentPage.layoutManager.scrollToPositionWithOffset(findFirstVisibleItemPosition, findViewHolderForAdapterPosition.itemView.getTop() - this.appliedPaddingTop);
                }
                this.ignoreLayout = false;
            } else {
                i3 = 0;
            }
            if (this.firstLayout && DialogsActivity.this.getMessagesController().dialogsLoaded) {
                if (this.parentPage.dialogsType == 0 && DialogsActivity.this.hasHiddenArchive()) {
                    this.ignoreLayout = true;
                    ((LinearLayoutManager) getLayoutManager()).scrollToPositionWithOffset(1, (int) DialogsActivity.this.actionBar.getTranslationY());
                    this.ignoreLayout = false;
                }
                this.firstLayout = false;
            }
            checkIfAdapterValid();
            super.onMeasure(i, i2);
            if (!DialogsActivity.this.onlySelect && this.appliedPaddingTop != i3 && DialogsActivity.this.viewPages != null && DialogsActivity.this.viewPages.length > 1) {
                DialogsActivity.this.viewPages[1].setTranslationX((float) DialogsActivity.this.viewPages[0].getMeasuredWidth());
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            this.appliedPaddingTop = getPaddingTop();
            if (!(DialogsActivity.this.dialogRemoveFinished == 0 && DialogsActivity.this.dialogInsertFinished == 0 && DialogsActivity.this.dialogChangeFinished == 0) && !this.parentPage.dialogsItemAnimator.isRunning()) {
                DialogsActivity.this.onDialogAnimationFinished();
            }
        }

        public void requestLayout() {
            if (!this.ignoreLayout) {
                super.requestLayout();
            }
        }

        /* access modifiers changed from: private */
        public void toggleArchiveHidden(boolean z, DialogCell dialogCell) {
            SharedConfig.toggleArchiveHidden();
            if (SharedConfig.archiveHidden) {
                boolean unused = DialogsActivity.this.waitingForScrollFinished = true;
                if (dialogCell != null) {
                    boolean unused2 = DialogsActivity.this.disableActionBarScrolling = true;
                    smoothScrollBy(0, dialogCell.getMeasuredHeight() + (dialogCell.getTop() - getPaddingTop()), CubicBezierInterpolator.EASE_OUT);
                    if (z) {
                        boolean unused3 = DialogsActivity.this.updatePullAfterScroll = true;
                    } else {
                        updatePullState();
                    }
                }
                DialogsActivity.this.getUndoView().showWithAction(0, 6, (Runnable) null, (Runnable) null);
                return;
            }
            DialogsActivity.this.getUndoView().showWithAction(0, 7, (Runnable) null, (Runnable) null);
            updatePullState();
            if (z && dialogCell != null) {
                dialogCell.resetPinnedArchiveState();
                dialogCell.invalidate();
            }
        }

        /* access modifiers changed from: private */
        public void updatePullState() {
            boolean z = false;
            int unused = this.parentPage.archivePullViewState = SharedConfig.archiveHidden ? 2 : 0;
            if (this.parentPage.pullForegroundDrawable != null) {
                PullForegroundDrawable access$8200 = this.parentPage.pullForegroundDrawable;
                if (this.parentPage.archivePullViewState != 0) {
                    z = true;
                }
                access$8200.setWillDraw(z);
            }
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            LinearLayoutManager linearLayoutManager;
            int findFirstVisibleItemPosition;
            if (this.fastScrollAnimationRunning || DialogsActivity.this.waitingForScrollFinished || DialogsActivity.this.dialogRemoveFinished != 0 || DialogsActivity.this.dialogInsertFinished != 0 || DialogsActivity.this.dialogChangeFinished != 0) {
                return false;
            }
            int action = motionEvent.getAction();
            if (action == 0) {
                setOverScrollMode(0);
            }
            if ((action == 1 || action == 3) && !this.parentPage.itemTouchhelper.isIdle() && this.parentPage.swipeController.swipingFolder) {
                boolean unused = this.parentPage.swipeController.swipeFolderBack = true;
                if (this.parentPage.itemTouchhelper.checkHorizontalSwipe((RecyclerView.ViewHolder) null, 4) != 0) {
                    toggleArchiveHidden(false, (DialogCell) null);
                }
            }
            boolean onTouchEvent = super.onTouchEvent(motionEvent);
            if (this.parentPage.dialogsType == 0 && ((action == 1 || action == 3) && this.parentPage.archivePullViewState == 2 && DialogsActivity.this.hasHiddenArchive() && (findFirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()) == 0)) {
                int paddingTop = getPaddingTop();
                View findViewByPosition = (linearLayoutManager = (LinearLayoutManager) getLayoutManager()).findViewByPosition(findFirstVisibleItemPosition);
                int dp = (int) (((float) AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f)) * 0.85f);
                int top = (findViewByPosition.getTop() - paddingTop) + findViewByPosition.getMeasuredHeight();
                if (findViewByPosition != null) {
                    long currentTimeMillis = System.currentTimeMillis() - DialogsActivity.this.startArchivePullingTime;
                    if (top < dp || currentTimeMillis < 200) {
                        boolean unused2 = DialogsActivity.this.disableActionBarScrolling = true;
                        smoothScrollBy(0, top, CubicBezierInterpolator.EASE_OUT_QUINT);
                        int unused3 = this.parentPage.archivePullViewState = 2;
                    } else if (this.parentPage.archivePullViewState != 1) {
                        if (getViewOffset() == 0.0f) {
                            boolean unused4 = DialogsActivity.this.disableActionBarScrolling = true;
                            smoothScrollBy(0, findViewByPosition.getTop() - paddingTop, CubicBezierInterpolator.EASE_OUT_QUINT);
                        }
                        if (!DialogsActivity.this.canShowHiddenArchive) {
                            boolean unused5 = DialogsActivity.this.canShowHiddenArchive = true;
                            performHapticFeedback(3, 2);
                            if (this.parentPage.pullForegroundDrawable != null) {
                                this.parentPage.pullForegroundDrawable.colorize(true);
                            }
                        }
                        ((DialogCell) findViewByPosition).startOutAnimation();
                        int unused6 = this.parentPage.archivePullViewState = 1;
                    }
                    if (getViewOffset() != 0.0f) {
                        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{getViewOffset(), 0.0f});
                        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                DialogsActivity.DialogsRecyclerView.this.lambda$onTouchEvent$0$DialogsActivity$DialogsRecyclerView(valueAnimator);
                            }
                        });
                        ofFloat.setDuration(Math.max(100, (long) (350.0f - ((getViewOffset() / ((float) PullForegroundDrawable.getMaxOverscroll())) * 120.0f))));
                        ofFloat.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                        setScrollEnabled(false);
                        ofFloat.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                super.onAnimationEnd(animator);
                                DialogsRecyclerView.this.setScrollEnabled(true);
                            }
                        });
                        ofFloat.start();
                    }
                }
            }
            return onTouchEvent;
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onTouchEvent$0 */
        public /* synthetic */ void lambda$onTouchEvent$0$DialogsActivity$DialogsRecyclerView(ValueAnimator valueAnimator) {
            setViewsOffset(((Float) valueAnimator.getAnimatedValue()).floatValue());
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (this.fastScrollAnimationRunning || DialogsActivity.this.waitingForScrollFinished || DialogsActivity.this.dialogRemoveFinished != 0 || DialogsActivity.this.dialogInsertFinished != 0 || DialogsActivity.this.dialogChangeFinished != 0) {
                return false;
            }
            if (motionEvent.getAction() == 0) {
                DialogsActivity dialogsActivity = DialogsActivity.this;
                boolean unused = dialogsActivity.allowSwipeDuringCurrentTouch = !dialogsActivity.actionBar.isActionModeShowed();
                checkIfAdapterValid();
            }
            return super.onInterceptTouchEvent(motionEvent);
        }
    }

    private class SwipeController extends ItemTouchHelper.Callback {
        private ViewPage parentPage;
        /* access modifiers changed from: private */
        public boolean swipeFolderBack;
        /* access modifiers changed from: private */
        public boolean swipingFolder;

        public float getSwipeEscapeVelocity(float f) {
            return 3500.0f;
        }

        public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
            return 0.3f;
        }

        public float getSwipeVelocityThreshold(float f) {
            return Float.MAX_VALUE;
        }

        public SwipeController(ViewPage viewPage) {
            this.parentPage = viewPage;
        }

        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (!DialogsActivity.this.waitingForDialogsAnimationEnd(this.parentPage) && (DialogsActivity.this.parentLayout == null || !DialogsActivity.this.parentLayout.isInPreviewMode())) {
                if (this.swipingFolder && this.swipeFolderBack) {
                    this.swipingFolder = false;
                    return 0;
                } else if (!DialogsActivity.this.onlySelect && this.parentPage.isDefaultDialogType() && DialogsActivity.this.slidingView == null) {
                    View view = viewHolder.itemView;
                    if (view instanceof DialogCell) {
                        DialogCell dialogCell = (DialogCell) view;
                        long dialogId = dialogCell.getDialogId();
                        if (DialogsActivity.this.actionBar.isActionModeShowed()) {
                            TLRPC$Dialog tLRPC$Dialog = DialogsActivity.this.getMessagesController().dialogs_dict.get(dialogId);
                            if (!DialogsActivity.this.allowMoving || tLRPC$Dialog == null || !DialogsActivity.this.isDialogPinned(tLRPC$Dialog) || DialogObject.isFolderDialogId(dialogId)) {
                                return 0;
                            }
                            DialogCell unused = DialogsActivity.this.movingView = (DialogCell) viewHolder.itemView;
                            DialogsActivity.this.movingView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                            return ItemTouchHelper.Callback.makeMovementFlags(3, 0);
                        } else if ((DialogsActivity.this.filterTabsView == null || DialogsActivity.this.filterTabsView.getVisibility() != 0) && DialogsActivity.this.allowSwipeDuringCurrentTouch && dialogId != ((long) DialogsActivity.this.getUserConfig().clientUserId) && dialogId != 777000 && (!DialogsActivity.this.getMessagesController().isPromoDialog(dialogId, false) || DialogsActivity.this.getMessagesController().promoDialogType == 1)) {
                            this.swipeFolderBack = false;
                            this.swipingFolder = SharedConfig.archiveHidden && DialogObject.isFolderDialogId(dialogCell.getDialogId());
                            dialogCell.setSliding(true);
                            return ItemTouchHelper.Callback.makeMovementFlags(0, 4);
                        }
                    }
                }
            }
            return 0;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:3:0x0008, code lost:
            r2 = ((org.telegram.ui.Cells.DialogCell) r5).getDialogId();
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onMove(androidx.recyclerview.widget.RecyclerView r5, androidx.recyclerview.widget.RecyclerView.ViewHolder r6, androidx.recyclerview.widget.RecyclerView.ViewHolder r7) {
            /*
                r4 = this;
                android.view.View r5 = r7.itemView
                boolean r0 = r5 instanceof org.telegram.ui.Cells.DialogCell
                r1 = 0
                if (r0 != 0) goto L_0x0008
                return r1
            L_0x0008:
                org.telegram.ui.Cells.DialogCell r5 = (org.telegram.ui.Cells.DialogCell) r5
                long r2 = r5.getDialogId()
                org.telegram.ui.DialogsActivity r5 = org.telegram.ui.DialogsActivity.this
                org.telegram.messenger.MessagesController r5 = r5.getMessagesController()
                android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r5 = r5.dialogs_dict
                java.lang.Object r5 = r5.get(r2)
                org.telegram.tgnet.TLRPC$Dialog r5 = (org.telegram.tgnet.TLRPC$Dialog) r5
                if (r5 == 0) goto L_0x0099
                org.telegram.ui.DialogsActivity r0 = org.telegram.ui.DialogsActivity.this
                boolean r5 = r0.isDialogPinned(r5)
                if (r5 == 0) goto L_0x0099
                boolean r5 = org.telegram.messenger.DialogObject.isFolderDialogId(r2)
                if (r5 == 0) goto L_0x002d
                goto L_0x0099
            L_0x002d:
                int r5 = r6.getAdapterPosition()
                int r6 = r7.getAdapterPosition()
                org.telegram.ui.DialogsActivity$ViewPage r7 = r4.parentPage
                org.telegram.ui.Adapters.DialogsAdapter r7 = r7.dialogsAdapter
                r7.notifyItemMoved(r5, r6)
                org.telegram.ui.DialogsActivity r5 = org.telegram.ui.DialogsActivity.this
                r5.updateDialogIndices()
                org.telegram.ui.DialogsActivity r5 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.DialogsActivity$ViewPage[] r5 = r5.viewPages
                r5 = r5[r1]
                int r5 = r5.dialogsType
                r6 = 7
                r7 = 8
                r0 = 1
                if (r5 == r6) goto L_0x006a
                org.telegram.ui.DialogsActivity r5 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.DialogsActivity$ViewPage[] r5 = r5.viewPages
                r5 = r5[r1]
                int r5 = r5.dialogsType
                if (r5 != r7) goto L_0x0064
                goto L_0x006a
            L_0x0064:
                org.telegram.ui.DialogsActivity r5 = org.telegram.ui.DialogsActivity.this
                boolean unused = r5.movingWas = r0
                goto L_0x0098
            L_0x006a:
                org.telegram.ui.DialogsActivity r5 = org.telegram.ui.DialogsActivity.this
                org.telegram.messenger.MessagesController r5 = r5.getMessagesController()
                org.telegram.messenger.MessagesController$DialogFilter[] r5 = r5.selectedDialogFilter
                org.telegram.ui.DialogsActivity r6 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.DialogsActivity$ViewPage[] r6 = r6.viewPages
                r6 = r6[r1]
                int r6 = r6.dialogsType
                if (r6 != r7) goto L_0x0081
                r1 = 1
            L_0x0081:
                r5 = r5[r1]
                org.telegram.ui.DialogsActivity r6 = org.telegram.ui.DialogsActivity.this
                java.util.ArrayList r6 = r6.movingDialogFilters
                boolean r6 = r6.contains(r5)
                if (r6 != 0) goto L_0x0098
                org.telegram.ui.DialogsActivity r6 = org.telegram.ui.DialogsActivity.this
                java.util.ArrayList r6 = r6.movingDialogFilters
                r6.add(r5)
            L_0x0098:
                return r0
            L_0x0099:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.SwipeController.onMove(androidx.recyclerview.widget.RecyclerView, androidx.recyclerview.widget.RecyclerView$ViewHolder, androidx.recyclerview.widget.RecyclerView$ViewHolder):boolean");
        }

        public int convertToAbsoluteDirection(int i, int i2) {
            if (this.swipeFolderBack) {
                return 0;
            }
            return super.convertToAbsoluteDirection(i, i2);
        }

        public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder != null) {
                DialogCell dialogCell = (DialogCell) viewHolder.itemView;
                if (DialogObject.isFolderDialogId(dialogCell.getDialogId())) {
                    this.parentPage.listView.toggleArchiveHidden(false, dialogCell);
                    return;
                }
                DialogCell unused = DialogsActivity.this.slidingView = dialogCell;
                int adapterPosition = viewHolder.getAdapterPosition();
                $$Lambda$DialogsActivity$SwipeController$wYBOBGwSUvXnv0AFR6ND02LscM r1 = new Runnable(this.parentPage.dialogsAdapter.fixPosition(adapterPosition), this.parentPage.dialogsAdapter.getItemCount(), adapterPosition) {
                    public final /* synthetic */ int f$1;
                    public final /* synthetic */ int f$2;
                    public final /* synthetic */ int f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void run() {
                        DialogsActivity.SwipeController.this.lambda$onSwiped$1$DialogsActivity$SwipeController(this.f$1, this.f$2, this.f$3);
                    }
                };
                DialogsActivity.this.setDialogsListFrozen(true);
                if (Utilities.random.nextInt(1000) == 1) {
                    if (DialogsActivity.this.pacmanAnimation == null) {
                        PacmanAnimation unused2 = DialogsActivity.this.pacmanAnimation = new PacmanAnimation(this.parentPage.listView);
                    }
                    DialogsActivity.this.pacmanAnimation.setFinishRunnable(r1);
                    DialogsActivity.this.pacmanAnimation.start();
                    return;
                }
                r1.run();
                return;
            }
            DialogCell unused3 = DialogsActivity.this.slidingView = null;
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onSwiped$1 */
        public /* synthetic */ void lambda$onSwiped$1$DialogsActivity$SwipeController(int i, int i2, int i3) {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition;
            if (DialogsActivity.frozenDialogsList != null) {
                TLRPC$Dialog tLRPC$Dialog = (TLRPC$Dialog) DialogsActivity.frozenDialogsList.remove(i);
                int i4 = tLRPC$Dialog.pinnedNum;
                DialogCell unused = DialogsActivity.this.slidingView = null;
                this.parentPage.listView.invalidate();
                int findLastVisibleItemPosition = this.parentPage.layoutManager.findLastVisibleItemPosition();
                if (findLastVisibleItemPosition == i2 - 1) {
                    this.parentPage.layoutManager.findViewByPosition(findLastVisibleItemPosition).requestLayout();
                }
                boolean z = false;
                if (DialogsActivity.this.getMessagesController().isPromoDialog(tLRPC$Dialog.id, false)) {
                    DialogsActivity.this.getMessagesController().hidePromoDialog();
                    this.parentPage.dialogsItemAnimator.prepareForRemove();
                    ViewPage.access$8510(this.parentPage);
                    this.parentPage.dialogsAdapter.notifyItemRemoved(i3);
                    int unused2 = DialogsActivity.this.dialogRemoveFinished = 2;
                    return;
                }
                int addDialogToFolder = DialogsActivity.this.getMessagesController().addDialogToFolder(tLRPC$Dialog.id, DialogsActivity.this.folderId == 0 ? 1 : 0, -1, 0);
                if (!(addDialogToFolder == 2 && i3 == 0)) {
                    this.parentPage.dialogsItemAnimator.prepareForRemove();
                    ViewPage.access$8510(this.parentPage);
                    this.parentPage.dialogsAdapter.notifyItemRemoved(i3);
                    int unused3 = DialogsActivity.this.dialogRemoveFinished = 2;
                }
                if (DialogsActivity.this.folderId == 0) {
                    if (addDialogToFolder == 2) {
                        this.parentPage.dialogsItemAnimator.prepareForRemove();
                        if (i3 == 0) {
                            int unused4 = DialogsActivity.this.dialogChangeFinished = 2;
                            DialogsActivity.this.setDialogsListFrozen(true);
                            this.parentPage.dialogsAdapter.notifyItemChanged(0);
                        } else {
                            ViewPage.access$8508(this.parentPage);
                            this.parentPage.dialogsAdapter.notifyItemInserted(0);
                            if (!SharedConfig.archiveHidden && this.parentPage.layoutManager.findFirstVisibleItemPosition() == 0) {
                                boolean unused5 = DialogsActivity.this.disableActionBarScrolling = true;
                                this.parentPage.listView.smoothScrollBy(0, -AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f));
                            }
                        }
                        DialogsActivity.frozenDialogsList.add(0, DialogsActivity.getDialogsArray(DialogsActivity.this.currentAccount, this.parentPage.dialogsType, DialogsActivity.this.folderId, false).get(0));
                    } else if (addDialogToFolder == 1 && (findViewHolderForAdapterPosition = this.parentPage.listView.findViewHolderForAdapterPosition(0)) != null) {
                        View view = findViewHolderForAdapterPosition.itemView;
                        if (view instanceof DialogCell) {
                            DialogCell dialogCell = (DialogCell) view;
                            dialogCell.checkCurrentDialogIndex(true);
                            dialogCell.animateArchiveAvatar();
                        }
                    }
                    SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                    if (globalMainSettings.getBoolean("archivehint_l", false) || SharedConfig.archiveHidden) {
                        z = true;
                    }
                    if (!z) {
                        globalMainSettings.edit().putBoolean("archivehint_l", true).commit();
                    }
                    DialogsActivity.this.getUndoView().showWithAction(tLRPC$Dialog.id, z ? 2 : 3, (Runnable) null, new Runnable(tLRPC$Dialog, i4) {
                        public final /* synthetic */ TLRPC$Dialog f$1;
                        public final /* synthetic */ int f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run() {
                            DialogsActivity.SwipeController.this.lambda$null$0$DialogsActivity$SwipeController(this.f$1, this.f$2);
                        }
                    });
                }
                if (DialogsActivity.this.folderId != 0 && DialogsActivity.frozenDialogsList.isEmpty()) {
                    this.parentPage.listView.setEmptyView((View) null);
                    this.parentPage.progressView.setVisibility(4);
                }
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$null$0 */
        public /* synthetic */ void lambda$null$0$DialogsActivity$SwipeController(TLRPC$Dialog tLRPC$Dialog, int i) {
            boolean unused = DialogsActivity.this.dialogsListFrozen = true;
            DialogsActivity.this.getMessagesController().addDialogToFolder(tLRPC$Dialog.id, 0, i, 0);
            boolean unused2 = DialogsActivity.this.dialogsListFrozen = false;
            ArrayList<TLRPC$Dialog> dialogs = DialogsActivity.this.getMessagesController().getDialogs(0);
            int indexOf = dialogs.indexOf(tLRPC$Dialog);
            if (indexOf >= 0) {
                ArrayList<TLRPC$Dialog> dialogs2 = DialogsActivity.this.getMessagesController().getDialogs(1);
                if (!dialogs2.isEmpty() || indexOf != 1) {
                    int unused3 = DialogsActivity.this.dialogInsertFinished = 2;
                    DialogsActivity.this.setDialogsListFrozen(true);
                    this.parentPage.dialogsItemAnimator.prepareForRemove();
                    ViewPage.access$8508(this.parentPage);
                    this.parentPage.dialogsAdapter.notifyItemInserted(indexOf);
                }
                if (dialogs2.isEmpty()) {
                    dialogs.remove(0);
                    if (indexOf == 1) {
                        int unused4 = DialogsActivity.this.dialogChangeFinished = 2;
                        DialogsActivity.this.setDialogsListFrozen(true);
                        this.parentPage.dialogsAdapter.notifyItemChanged(0);
                        return;
                    }
                    DialogsActivity.frozenDialogsList.remove(0);
                    this.parentPage.dialogsItemAnimator.prepareForRemove();
                    ViewPage.access$8510(this.parentPage);
                    this.parentPage.dialogsAdapter.notifyItemRemoved(0);
                    return;
                }
                return;
            }
            this.parentPage.dialogsAdapter.notifyDataSetChanged();
        }

        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder != null) {
                this.parentPage.listView.hideSelector(false);
            }
            super.onSelectedChanged(viewHolder, i);
        }

        public long getAnimationDuration(RecyclerView recyclerView, int i, float f, float f2) {
            if (i == 4) {
                return 200;
            }
            if (i == 8 && DialogsActivity.this.movingView != null) {
                AndroidUtilities.runOnUIThread(new Runnable(DialogsActivity.this.movingView) {
                    public final /* synthetic */ View f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void run() {
                        this.f$0.setBackgroundDrawable((Drawable) null);
                    }
                }, this.parentPage.dialogsItemAnimator.getMoveDuration());
                DialogCell unused = DialogsActivity.this.movingView = null;
            }
            return super.getAnimationDuration(recyclerView, i, f, f2);
        }
    }

    public DialogsActivity(Bundle bundle) {
        super(bundle);
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        if (getArguments() != null) {
            this.onlySelect = this.arguments.getBoolean("onlySelect", false);
            this.cantSendToChannels = this.arguments.getBoolean("cantSendToChannels", false);
            this.initialDialogsType = this.arguments.getInt("dialogsType", 0);
            this.selectAlertString = this.arguments.getString("selectAlertString");
            this.selectAlertStringGroup = this.arguments.getString("selectAlertStringGroup");
            this.addToGroupAlertString = this.arguments.getString("addToGroupAlertString");
            this.allowSwitchAccount = this.arguments.getBoolean("allowSwitchAccount");
            this.checkCanWrite = this.arguments.getBoolean("checkCanWrite", true);
            this.afterSignup = this.arguments.getBoolean("afterSignup", false);
            this.folderId = this.arguments.getInt("folderId", 0);
            this.resetDelegate = this.arguments.getBoolean("resetDelegate", true);
            this.messagesCount = this.arguments.getInt("messagesCount", 0);
            this.hasPoll = this.arguments.getInt("hasPoll", 0);
        }
        if (this.initialDialogsType == 0) {
            this.askAboutContacts = MessagesController.getGlobalNotificationsSettings().getBoolean("askAboutContacts", true);
            SharedConfig.loadProxyList();
        }
        if (this.searchString == null) {
            this.currentConnectionState = getConnectionsManager().getConnectionState();
            getNotificationCenter().addObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
            if (!this.onlySelect) {
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.closeSearchByActiveAction);
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxySettingsChanged);
                getNotificationCenter().addObserver(this, NotificationCenter.filterSettingsUpdated);
                getNotificationCenter().addObserver(this, NotificationCenter.dialogFiltersUpdated);
                getNotificationCenter().addObserver(this, NotificationCenter.dialogsUnreadCounterChanged);
            }
            getNotificationCenter().addObserver(this, NotificationCenter.updateInterfaces);
            getNotificationCenter().addObserver(this, NotificationCenter.encryptedChatUpdated);
            getNotificationCenter().addObserver(this, NotificationCenter.contactsDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.appDidLogout);
            getNotificationCenter().addObserver(this, NotificationCenter.openedChatChanged);
            getNotificationCenter().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
            getNotificationCenter().addObserver(this, NotificationCenter.messageReceivedByAck);
            getNotificationCenter().addObserver(this, NotificationCenter.messageReceivedByServer);
            getNotificationCenter().addObserver(this, NotificationCenter.messageSendError);
            getNotificationCenter().addObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            getNotificationCenter().addObserver(this, NotificationCenter.replyMessagesDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.reloadHints);
            getNotificationCenter().addObserver(this, NotificationCenter.didUpdateConnectionState);
            getNotificationCenter().addObserver(this, NotificationCenter.needDeleteDialog);
            getNotificationCenter().addObserver(this, NotificationCenter.folderBecomeEmpty);
            getNotificationCenter().addObserver(this, NotificationCenter.newSuggestionsAvailable);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetPasscode);
        }
        getNotificationCenter().addObserver(this, NotificationCenter.messagesDeleted);
        if (!dialogsLoaded[this.currentAccount]) {
            MessagesController messagesController = getMessagesController();
            messagesController.loadGlobalNotificationsSettings();
            messagesController.loadDialogs(this.folderId, 0, 100, true);
            messagesController.loadHintDialogs();
            messagesController.loadUserInfo(UserConfig.getInstance(this.currentAccount).getCurrentUser(), false, this.classGuid);
            getContactsController().checkInviteText();
            getMediaDataController().loadRecents(2, false, true, false);
            getMediaDataController().checkFeaturedStickers();
            Iterator<String> it = messagesController.diceEmojies.iterator();
            while (it.hasNext()) {
                getMediaDataController().loadStickersByEmojiOrName(it.next(), true, true);
            }
            dialogsLoaded[this.currentAccount] = true;
        }
        getMessagesController().loadPinnedDialogs(this.folderId, 0, (ArrayList<Long>) null);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.searchString == null) {
            getNotificationCenter().removeObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
            if (!this.onlySelect) {
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.closeSearchByActiveAction);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxySettingsChanged);
                getNotificationCenter().removeObserver(this, NotificationCenter.filterSettingsUpdated);
                getNotificationCenter().removeObserver(this, NotificationCenter.dialogFiltersUpdated);
                getNotificationCenter().removeObserver(this, NotificationCenter.dialogsUnreadCounterChanged);
            }
            getNotificationCenter().removeObserver(this, NotificationCenter.updateInterfaces);
            getNotificationCenter().removeObserver(this, NotificationCenter.encryptedChatUpdated);
            getNotificationCenter().removeObserver(this, NotificationCenter.contactsDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.appDidLogout);
            getNotificationCenter().removeObserver(this, NotificationCenter.openedChatChanged);
            getNotificationCenter().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
            getNotificationCenter().removeObserver(this, NotificationCenter.messageReceivedByAck);
            getNotificationCenter().removeObserver(this, NotificationCenter.messageReceivedByServer);
            getNotificationCenter().removeObserver(this, NotificationCenter.messageSendError);
            getNotificationCenter().removeObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            getNotificationCenter().removeObserver(this, NotificationCenter.replyMessagesDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.reloadHints);
            getNotificationCenter().removeObserver(this, NotificationCenter.didUpdateConnectionState);
            getNotificationCenter().removeObserver(this, NotificationCenter.needDeleteDialog);
            getNotificationCenter().removeObserver(this, NotificationCenter.folderBecomeEmpty);
            getNotificationCenter().removeObserver(this, NotificationCenter.newSuggestionsAvailable);
            getNotificationCenter().removeObserver(this, NotificationCenter.messagesDeleted);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetPasscode);
        }
        ChatActivityEnterView chatActivityEnterView = this.commentView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.onDestroy();
        }
        UndoView[] undoViewArr = this.undoView;
        if (undoViewArr[0] != null) {
            undoViewArr[0].hide(true, 0);
        }
        this.delegate = null;
    }

    /* access modifiers changed from: protected */
    public ActionBar createActionBar(Context context) {
        AnonymousClass2 r0 = new ActionBar(context) {
            /* access modifiers changed from: protected */
            public boolean shouldClipChild(View view) {
                return super.shouldClipChild(view) || view == DialogsActivity.this.doneItem;
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                if (!DialogsActivity.this.inPreviewMode || DialogsActivity.this.avatarContainer == null || view == DialogsActivity.this.avatarContainer) {
                    return super.drawChild(canvas, view, j);
                }
                return false;
            }
        };
        r0.setItemsBackgroundColor(Theme.getColor("actionBarDefaultSelector"), false);
        r0.setItemsBackgroundColor(Theme.getColor("actionBarActionModeDefaultSelector"), true);
        r0.setItemsColor(Theme.getColor("actionBarDefaultIcon"), false);
        r0.setItemsColor(Theme.getColor("actionBarActionModeDefaultIcon"), true);
        if (this.inPreviewMode || (AndroidUtilities.isTablet() && this.folderId != 0)) {
            r0.setOccupyStatusBar(false);
        }
        return r0;
    }

    /* JADX WARNING: Removed duplicated region for block: B:172:0x0726  */
    /* JADX WARNING: Removed duplicated region for block: B:175:0x0733  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x074c A[LOOP:2: B:181:0x074a->B:182:0x074c, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x0775  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x07e3  */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x07e6  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x07f3  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x0870  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r36) {
        /*
            r35 = this;
            r9 = r35
            r10 = r36
            r11 = 0
            r9.searching = r11
            r9.searchWas = r11
            r12 = 0
            r9.pacmanAnimation = r12
            java.util.ArrayList<java.lang.Long> r0 = r9.selectedDialogs
            r0.clear()
            android.view.ViewConfiguration r0 = android.view.ViewConfiguration.get(r36)
            int r0 = r0.getScaledMaximumFlingVelocity()
            r9.maximumVelocity = r0
            org.telegram.ui.-$$Lambda$DialogsActivity$Qx9c2SGTz8VEtmWATYZYK47zjCk r0 = new org.telegram.ui.-$$Lambda$DialogsActivity$Qx9c2SGTz8VEtmWATYZYK47zjCk
            r0.<init>(r10)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            org.telegram.ui.ActionBar.ActionBar r0 = r9.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r6 = r0.createMenu()
            boolean r0 = r9.onlySelect
            r13 = 0
            r14 = 2
            r15 = 8
            r8 = 1
            if (r0 != 0) goto L_0x00b7
            java.lang.String r0 = r9.searchString
            if (r0 != 0) goto L_0x00b7
            int r0 = r9.folderId
            if (r0 != 0) goto L_0x00b7
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            r2 = 0
            java.lang.String r0 = "actionBarDefaultSelector"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            java.lang.String r0 = "actionBarDefaultIcon"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            r5 = 1
            r0 = r7
            r1 = r36
            r0.<init>(r1, r2, r3, r4, r5)
            r9.doneItem = r7
            r0 = 2131625115(0x7f0e049b, float:1.8877429E38)
            java.lang.String r1 = "Done"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.String r0 = r0.toUpperCase()
            r7.setText(r0)
            org.telegram.ui.ActionBar.ActionBar r0 = r9.actionBar
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r9.doneItem
            r16 = -2
            r17 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r18 = 53
            r19 = 0
            r20 = 0
            r21 = 1092616192(0x41200000, float:10.0)
            r22 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r0.addView(r1, r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r9.doneItem
            org.telegram.ui.-$$Lambda$DialogsActivity$SueaA71vXmo31QzmfxdDgaRyj5I r1 = new org.telegram.ui.-$$Lambda$DialogsActivity$SueaA71vXmo31QzmfxdDgaRyj5I
            r1.<init>()
            r0.setOnClickListener(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r9.doneItem
            r0.setAlpha(r13)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r9.doneItem
            r0.setVisibility(r15)
            org.telegram.ui.Components.ProxyDrawable r0 = new org.telegram.ui.Components.ProxyDrawable
            r0.<init>(r10)
            r9.proxyDrawable = r0
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.addItem((int) r14, (android.graphics.drawable.Drawable) r0)
            r9.proxyItem = r0
            r1 = 2131626756(0x7f0e0b04, float:1.8880757E38)
            java.lang.String r2 = "ProxySettings"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setContentDescription(r1)
            r0 = 2131165597(0x7var_d, float:1.7945416E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.addItem((int) r8, (int) r0)
            r9.passcodeItem = r0
            r35.updatePasscodeButton()
            r9.updateProxyButton(r11)
        L_0x00b7:
            r0 = 2131165476(0x7var_, float:1.794517E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.addItem((int) r11, (int) r0)
            r0.setIsSearchField(r8, r8)
            org.telegram.ui.DialogsActivity$3 r1 = new org.telegram.ui.DialogsActivity$3
            r1.<init>()
            r0.setActionBarMenuItemSearchListener(r1)
            r9.searchItem = r0
            r0.setClearsTextOnSearchCollapse(r11)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r9.searchItem
            java.lang.String r1 = "Search"
            r2 = 2131626950(0x7f0e0bc6, float:1.888115E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r1, r2)
            r0.setSearchFieldHint(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r9.searchItem
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)
            r0.setContentDescription(r1)
            boolean r0 = r9.onlySelect
            java.lang.String r16 = "actionBarDefault"
            r7 = 3
            if (r0 == 0) goto L_0x0123
            org.telegram.ui.ActionBar.ActionBar r0 = r9.actionBar
            r1 = 2131165466(0x7var_a, float:1.794515E38)
            r0.setBackButtonImage(r1)
            int r0 = r9.initialDialogsType
            if (r0 != r7) goto L_0x010b
            java.lang.String r0 = r9.selectAlertString
            if (r0 != 0) goto L_0x010b
            org.telegram.ui.ActionBar.ActionBar r0 = r9.actionBar
            r1 = 2131625449(0x7f0e05e9, float:1.8878106E38)
            java.lang.String r2 = "ForwardTo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x0119
        L_0x010b:
            org.telegram.ui.ActionBar.ActionBar r0 = r9.actionBar
            r1 = 2131627006(0x7f0e0bfe, float:1.8881264E38)
            java.lang.String r2 = "SelectChat"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
        L_0x0119:
            org.telegram.ui.ActionBar.ActionBar r0 = r9.actionBar
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r0.setBackgroundColor(r1)
            goto L_0x0189
        L_0x0123:
            java.lang.String r0 = r9.searchString
            if (r0 != 0) goto L_0x0147
            int r0 = r9.folderId
            if (r0 == 0) goto L_0x012c
            goto L_0x0147
        L_0x012c:
            org.telegram.ui.ActionBar.ActionBar r0 = r9.actionBar
            org.telegram.ui.ActionBar.MenuDrawable r1 = new org.telegram.ui.ActionBar.MenuDrawable
            r1.<init>()
            r9.menuDrawable = r1
            r0.setBackButtonDrawable(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r9.actionBar
            r1 = 2131623999(0x7f0e003f, float:1.8875165E38)
            java.lang.String r2 = "AccDescrOpenMenu"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setBackButtonContentDescription(r1)
            goto L_0x0153
        L_0x0147:
            org.telegram.ui.ActionBar.ActionBar r0 = r9.actionBar
            org.telegram.ui.ActionBar.BackDrawable r1 = new org.telegram.ui.ActionBar.BackDrawable
            r1.<init>(r11)
            r9.backDrawable = r1
            r0.setBackButtonDrawable(r1)
        L_0x0153:
            int r0 = r9.folderId
            if (r0 == 0) goto L_0x0166
            org.telegram.ui.ActionBar.ActionBar r0 = r9.actionBar
            r1 = 2131624272(0x7f0e0150, float:1.887572E38)
            java.lang.String r2 = "ArchivedChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x0180
        L_0x0166:
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r0 == 0) goto L_0x0172
            org.telegram.ui.ActionBar.ActionBar r0 = r9.actionBar
            java.lang.String r1 = "Telegram Beta"
            r0.setTitle(r1)
            goto L_0x0180
        L_0x0172:
            org.telegram.ui.ActionBar.ActionBar r0 = r9.actionBar
            r1 = 2131624250(0x7f0e013a, float:1.8875674E38)
            java.lang.String r2 = "AppName"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
        L_0x0180:
            int r0 = r9.folderId
            if (r0 != 0) goto L_0x0189
            org.telegram.ui.ActionBar.ActionBar r0 = r9.actionBar
            r0.setSupportsHolidayImage(r8)
        L_0x0189:
            boolean r0 = r9.onlySelect
            if (r0 != 0) goto L_0x019c
            org.telegram.ui.ActionBar.ActionBar r0 = r9.actionBar
            r0.setAddToContainer(r11)
            org.telegram.ui.ActionBar.ActionBar r0 = r9.actionBar
            r0.setCastShadows(r11)
            org.telegram.ui.ActionBar.ActionBar r0 = r9.actionBar
            r0.setClipContent(r8)
        L_0x019c:
            org.telegram.ui.ActionBar.ActionBar r0 = r9.actionBar
            org.telegram.ui.-$$Lambda$DialogsActivity$JSCaSgR2t00VtHN1RM1I7hZoKos r1 = new org.telegram.ui.-$$Lambda$DialogsActivity$JSCaSgR2t00VtHN1RM1I7hZoKos
            r1.<init>()
            r0.setTitleActionRunnable(r1)
            int r0 = r9.initialDialogsType
            if (r0 != 0) goto L_0x01da
            int r0 = r9.folderId
            if (r0 != 0) goto L_0x01da
            boolean r0 = r9.onlySelect
            if (r0 != 0) goto L_0x01da
            java.lang.String r0 = r9.searchString
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x01da
            org.telegram.ui.DialogsActivity$4 r0 = new org.telegram.ui.DialogsActivity$4
            r0.<init>()
            r9.scrimPaint = r0
            org.telegram.ui.DialogsActivity$5 r0 = new org.telegram.ui.DialogsActivity$5
            r0.<init>(r10)
            r9.filterTabsView = r0
            r0.setVisibility(r15)
            org.telegram.ui.Components.FilterTabsView r0 = r9.filterTabsView
            r0.setTag(r12)
            org.telegram.ui.Components.FilterTabsView r0 = r9.filterTabsView
            org.telegram.ui.DialogsActivity$6 r1 = new org.telegram.ui.DialogsActivity$6
            r1.<init>()
            r0.setDelegate(r1)
        L_0x01da:
            boolean r0 = r9.allowSwitchAccount
            r5 = 17
            r17 = 1113587712(0x42600000, float:56.0)
            if (r0 == 0) goto L_0x0265
            int r0 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()
            if (r0 <= r8) goto L_0x0265
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.addItemWithWidth(r8, r11, r0)
            r9.switchItem = r0
            org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable
            r0.<init>()
            r1 = 1094713344(0x41400000, float:12.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setTextSize(r1)
            org.telegram.ui.Components.BackupImageView r1 = new org.telegram.ui.Components.BackupImageView
            r1.<init>(r10)
            r2 = 1099956224(0x41900000, float:18.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setRoundRadius(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r9.switchItem
            r3 = 36
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r3, r5)
            r2.addView(r1, r3)
            org.telegram.messenger.UserConfig r2 = r35.getUserConfig()
            org.telegram.tgnet.TLRPC$User r2 = r2.getCurrentUser()
            r0.setInfo((org.telegram.tgnet.TLRPC$User) r2)
            org.telegram.messenger.ImageReceiver r3 = r1.getImageReceiver()
            int r4 = r9.currentAccount
            r3.setCurrentAccount(r4)
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForUser(r2, r11)
            java.lang.String r4 = "50_50"
            r1.setImage((org.telegram.messenger.ImageLocation) r3, (java.lang.String) r4, (android.graphics.drawable.Drawable) r0, (java.lang.Object) r2)
            r0 = 0
        L_0x0237:
            if (r0 >= r7) goto L_0x0265
            org.telegram.messenger.AccountInstance r1 = org.telegram.messenger.AccountInstance.getInstance(r0)
            org.telegram.messenger.UserConfig r1 = r1.getUserConfig()
            org.telegram.tgnet.TLRPC$User r1 = r1.getCurrentUser()
            if (r1 == 0) goto L_0x0262
            org.telegram.ui.Cells.AccountSelectCell r1 = new org.telegram.ui.Cells.AccountSelectCell
            r1.<init>(r10)
            r1.setAccount(r0, r8)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r9.switchItem
            int r3 = r0 + 10
            r4 = 1130758144(0x43660000, float:230.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r6 = 1111490560(0x42400000, float:48.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r2.addSubItem((int) r3, (android.view.View) r1, (int) r4, (int) r6)
        L_0x0262:
            int r0 = r0 + 1
            goto L_0x0237
        L_0x0265:
            org.telegram.ui.ActionBar.ActionBar r0 = r9.actionBar
            r0.setAllowOverlayTitle(r8)
            androidx.recyclerview.widget.RecyclerView r0 = r9.sideMenu
            if (r0 == 0) goto L_0x0289
            java.lang.String r1 = "chats_menuBackground"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setBackgroundColor(r2)
            androidx.recyclerview.widget.RecyclerView r0 = r9.sideMenu
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setGlowColor(r1)
            androidx.recyclerview.widget.RecyclerView r0 = r9.sideMenu
            androidx.recyclerview.widget.RecyclerView$Adapter r0 = r0.getAdapter()
            r0.notifyDataSetChanged()
        L_0x0289:
            r35.createActionMode()
            org.telegram.ui.DialogsActivity$ContentView r6 = new org.telegram.ui.DialogsActivity$ContentView
            r6.<init>(r10)
            r9.fragmentView = r6
            int r0 = r9.folderId
            if (r0 != 0) goto L_0x02a1
            int r0 = r9.initialDialogsType
            if (r0 != 0) goto L_0x02a1
            boolean r0 = r9.onlySelect
            if (r0 != 0) goto L_0x02a1
            r4 = 2
            goto L_0x02a2
        L_0x02a1:
            r4 = 1
        L_0x02a2:
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = new org.telegram.ui.DialogsActivity.ViewPage[r4]
            r9.viewPages = r0
            r3 = 0
        L_0x02a7:
            r2 = -1082130432(0xffffffffbvar_, float:-1.0)
            r1 = -2
            r0 = -1
            if (r3 >= r4) goto L_0x0478
            org.telegram.ui.DialogsActivity$7 r12 = new org.telegram.ui.DialogsActivity$7
            r12.<init>(r10)
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r0, r2)
            r6.addView(r12, r7)
            int r7 = r9.initialDialogsType
            int unused = r12.dialogsType = r7
            org.telegram.ui.DialogsActivity$ViewPage[] r7 = r9.viewPages
            r7[r3] = r12
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r7 = new org.telegram.ui.DialogsActivity$DialogsRecyclerView
            r7.<init>(r10, r12)
            org.telegram.ui.DialogsActivity.DialogsRecyclerView unused = r12.listView = r7
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r7 = r12.listView
            r7.setClipToPadding(r11)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r7 = r12.listView
            r7.setPivotY(r13)
            org.telegram.ui.DialogsActivity$8 r7 = new org.telegram.ui.DialogsActivity$8
            r7.<init>(r12)
            org.telegram.ui.Components.DialogsItemAnimator unused = r12.dialogsItemAnimator = r7
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r7 = r12.listView
            org.telegram.ui.Components.DialogsItemAnimator r14 = r12.dialogsItemAnimator
            r7.setItemAnimator(r14)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r7 = r12.listView
            r7.setVerticalScrollBarEnabled(r8)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r7 = r12.listView
            r7.setInstantClick(r8)
            org.telegram.ui.DialogsActivity$9 r7 = new org.telegram.ui.DialogsActivity$9
            r7.<init>(r10, r12)
            androidx.recyclerview.widget.LinearLayoutManager unused = r12.layoutManager = r7
            androidx.recyclerview.widget.LinearLayoutManager r7 = r12.layoutManager
            r7.setOrientation(r8)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r7 = r12.listView
            androidx.recyclerview.widget.LinearLayoutManager r14 = r12.layoutManager
            r7.setLayoutManager(r14)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r7 = r12.listView
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 == 0) goto L_0x031d
            r14 = 1
            goto L_0x031e
        L_0x031d:
            r14 = 2
        L_0x031e:
            r7.setVerticalScrollbarPosition(r14)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r7 = r12.listView
            android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r0, r2)
            r12.addView(r7, r0)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r12.listView
            org.telegram.ui.-$$Lambda$DialogsActivity$9g5JEnTqVRFmA3Hm-rAwcqvWjKE r2 = new org.telegram.ui.-$$Lambda$DialogsActivity$9g5JEnTqVRFmA3Hm-rAwcqvWjKE
            r2.<init>(r12)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r2)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r12.listView
            org.telegram.ui.DialogsActivity$10 r2 = new org.telegram.ui.DialogsActivity$10
            r2.<init>(r12)
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended) r2)
            org.telegram.ui.DialogsActivity$SwipeController r0 = new org.telegram.ui.DialogsActivity$SwipeController
            r0.<init>(r12)
            org.telegram.ui.DialogsActivity.SwipeController unused = r12.swipeController = r0
            androidx.recyclerview.widget.ItemTouchHelper r0 = new androidx.recyclerview.widget.ItemTouchHelper
            org.telegram.ui.DialogsActivity$SwipeController r2 = r12.swipeController
            r0.<init>(r2)
            androidx.recyclerview.widget.ItemTouchHelper unused = r12.itemTouchhelper = r0
            androidx.recyclerview.widget.ItemTouchHelper r0 = r12.itemTouchhelper
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r2 = r12.listView
            r0.attachToRecyclerView(r2)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r12.listView
            org.telegram.ui.DialogsActivity$11 r2 = new org.telegram.ui.DialogsActivity$11
            r2.<init>(r12)
            r0.setOnScrollListener(r2)
            org.telegram.ui.Components.RadialProgressView r0 = new org.telegram.ui.Components.RadialProgressView
            r0.<init>(r10)
            org.telegram.ui.Components.RadialProgressView unused = r12.progressView = r0
            org.telegram.ui.Components.RadialProgressView r0 = r12.progressView
            r0.setPivotY(r13)
            org.telegram.ui.Components.RadialProgressView r0 = r12.progressView
            r0.setVisibility(r15)
            org.telegram.ui.Components.RadialProgressView r0 = r12.progressView
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r1, r1, r5)
            r12.addView(r0, r1)
            boolean r0 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r0 == 0) goto L_0x0396
            r0 = 2
            goto L_0x0397
        L_0x0396:
            r0 = 0
        L_0x0397:
            int unused = r12.archivePullViewState = r0
            org.telegram.ui.Components.PullForegroundDrawable r0 = r12.pullForegroundDrawable
            if (r0 != 0) goto L_0x03e3
            int r0 = r9.folderId
            if (r0 != 0) goto L_0x03e3
            org.telegram.ui.DialogsActivity$12 r0 = new org.telegram.ui.DialogsActivity$12
            r1 = 2131624067(0x7f0e0083, float:1.8875303E38)
            java.lang.String r2 = "AccSwipeForArchive"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 2131624066(0x7f0e0082, float:1.8875301E38)
            java.lang.String r7 = "AccReleaseForArchive"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r7, r2)
            r0.<init>(r9, r1, r2, r12)
            org.telegram.ui.Components.PullForegroundDrawable unused = r12.pullForegroundDrawable = r0
            boolean r0 = r35.hasHiddenArchive()
            if (r0 == 0) goto L_0x03cc
            org.telegram.ui.Components.PullForegroundDrawable r0 = r12.pullForegroundDrawable
            r0.showHidden()
            goto L_0x03d3
        L_0x03cc:
            org.telegram.ui.Components.PullForegroundDrawable r0 = r12.pullForegroundDrawable
            r0.doNotShow()
        L_0x03d3:
            org.telegram.ui.Components.PullForegroundDrawable r0 = r12.pullForegroundDrawable
            int r1 = r12.archivePullViewState
            if (r1 == 0) goto L_0x03df
            r1 = 1
            goto L_0x03e0
        L_0x03df:
            r1 = 0
        L_0x03e0:
            r0.setWillDraw(r1)
        L_0x03e3:
            org.telegram.ui.DialogsActivity$13 r14 = new org.telegram.ui.DialogsActivity$13
            int r7 = r12.dialogsType
            int r2 = r9.folderId
            boolean r1 = r9.onlySelect
            java.util.ArrayList<java.lang.Long> r0 = r9.selectedDialogs
            int r8 = r9.currentAccount
            r22 = r0
            r0 = r14
            r23 = r1
            r1 = r35
            r24 = r2
            r2 = r36
            r25 = r3
            r3 = r7
            r26 = r4
            r4 = r24
            r24 = 17
            r5 = r23
            r7 = r6
            r6 = r22
            r13 = r7
            r7 = r8
            r8 = r12
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8)
            org.telegram.ui.Adapters.DialogsAdapter unused = r12.dialogsAdapter = r14
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x042a
            long r0 = r9.openedDialogId
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x042a
            org.telegram.ui.Adapters.DialogsAdapter r0 = r12.dialogsAdapter
            long r1 = r9.openedDialogId
            r0.setOpenedDialogId(r1)
        L_0x042a:
            org.telegram.ui.Adapters.DialogsAdapter r0 = r12.dialogsAdapter
            org.telegram.ui.Components.PullForegroundDrawable r1 = r12.pullForegroundDrawable
            r0.setArchivedPullDrawable(r1)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r12.listView
            org.telegram.ui.Adapters.DialogsAdapter r1 = r12.dialogsAdapter
            r0.setAdapter(r1)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r12.listView
            int r1 = r9.folderId
            if (r1 != 0) goto L_0x044d
            org.telegram.ui.Components.RadialProgressView r1 = r12.progressView
            goto L_0x044e
        L_0x044d:
            r1 = 0
        L_0x044e:
            r0.setEmptyView(r1)
            org.telegram.ui.Components.RecyclerAnimationScrollHelper r0 = new org.telegram.ui.Components.RecyclerAnimationScrollHelper
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r1 = r12.listView
            androidx.recyclerview.widget.LinearLayoutManager r2 = r12.layoutManager
            r0.<init>(r1, r2)
            org.telegram.ui.Components.RecyclerAnimationScrollHelper unused = r12.scrollHelper = r0
            if (r25 == 0) goto L_0x046a
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r9.viewPages
            r0 = r0[r25]
            r0.setVisibility(r15)
        L_0x046a:
            int r3 = r25 + 1
            r6 = r13
            r4 = r26
            r5 = 17
            r7 = 3
            r8 = 1
            r12 = 0
            r13 = 0
            r14 = 2
            goto L_0x02a7
        L_0x0478:
            r13 = r6
            java.lang.String r3 = r9.searchString
            if (r3 == 0) goto L_0x047f
            r3 = 2
            goto L_0x0486
        L_0x047f:
            boolean r3 = r9.onlySelect
            if (r3 != 0) goto L_0x0485
            r3 = 1
            goto L_0x0486
        L_0x0485:
            r3 = 0
        L_0x0486:
            org.telegram.ui.Components.SearchViewPager r7 = new org.telegram.ui.Components.SearchViewPager
            int r4 = r9.initialDialogsType
            int r5 = r9.folderId
            org.telegram.ui.DialogsActivity$14 r6 = new org.telegram.ui.DialogsActivity$14
            r6.<init>()
            r8 = -1
            r0 = r7
            r12 = -2
            r1 = r36
            r14 = -1082130432(0xffffffffbvar_, float:-1.0)
            r2 = r35
            r0.<init>(r1, r2, r3, r4, r5, r6)
            r9.searchViewPager = r7
            r13.addView(r7)
            org.telegram.ui.Components.SearchViewPager r0 = r9.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r0 = r0.dialogsSearchAdapter
            org.telegram.ui.DialogsActivity$15 r1 = new org.telegram.ui.DialogsActivity$15
            r1.<init>()
            r0.setDelegate(r1)
            org.telegram.ui.Components.SearchViewPager r0 = r9.searchViewPager
            org.telegram.ui.Components.RecyclerListView r0 = r0.searchListView
            org.telegram.ui.-$$Lambda$DialogsActivity$pBCq63prZRS518_dH45NxiBpYLg r1 = new org.telegram.ui.-$$Lambda$DialogsActivity$pBCq63prZRS518_dH45NxiBpYLg
            r1.<init>()
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            org.telegram.ui.Components.SearchViewPager r0 = r9.searchViewPager
            org.telegram.ui.Components.RecyclerListView r0 = r0.searchListView
            org.telegram.ui.DialogsActivity$16 r1 = new org.telegram.ui.DialogsActivity$16
            r1.<init>()
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended) r1)
            org.telegram.ui.Components.SearchViewPager r0 = r9.searchViewPager
            org.telegram.ui.-$$Lambda$DialogsActivity$9BZKks1l13cel3V-cpOskDp_uvY r1 = new org.telegram.ui.-$$Lambda$DialogsActivity$9BZKks1l13cel3V-cpOskDp_uvY
            r1.<init>()
            r0.setFilteredSearchViewDelegate(r1)
            org.telegram.ui.Components.SearchViewPager r0 = r9.searchViewPager
            r0.setVisibility(r15)
            org.telegram.ui.Adapters.FiltersView r0 = new org.telegram.ui.Adapters.FiltersView
            android.app.Activity r1 = r35.getParentActivity()
            r0.<init>(r1)
            r9.filtersView = r0
            org.telegram.ui.-$$Lambda$DialogsActivity$P9xYAyGRu_Y4WiebF4gZ35grh_U r1 = new org.telegram.ui.-$$Lambda$DialogsActivity$P9xYAyGRu_Y4WiebF4gZ35grh_U
            r1.<init>()
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            org.telegram.ui.Adapters.FiltersView r0 = r9.filtersView
            r1 = 48
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r12, r1)
            r13.addView(r0, r1)
            org.telegram.ui.Adapters.FiltersView r0 = r9.filtersView
            r0.setVisibility(r15)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r10)
            r9.floatingButtonContainer = r0
            boolean r1 = r9.onlySelect
            if (r1 != 0) goto L_0x050a
            int r1 = r9.folderId
            if (r1 == 0) goto L_0x0508
            goto L_0x050a
        L_0x0508:
            r1 = 0
            goto L_0x050c
        L_0x050a:
            r1 = 8
        L_0x050c:
            r0.setVisibility(r1)
            android.widget.FrameLayout r0 = r9.floatingButtonContainer
            int r1 = android.os.Build.VERSION.SDK_INT
            r4 = 21
            if (r1 < r4) goto L_0x051a
            r5 = 56
            goto L_0x051c
        L_0x051a:
            r5 = 60
        L_0x051c:
            int r28 = r5 + 20
            if (r1 < r4) goto L_0x0523
            r5 = 56
            goto L_0x0525
        L_0x0523:
            r5 = 60
        L_0x0525:
            int r5 = r5 + 14
            float r5 = (float) r5
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x052e
            r7 = 3
            goto L_0x052f
        L_0x052e:
            r7 = 5
        L_0x052f:
            r30 = r7 | 80
            r7 = 1082130432(0x40800000, float:4.0)
            if (r6 == 0) goto L_0x0538
            r31 = 1082130432(0x40800000, float:4.0)
            goto L_0x053a
        L_0x0538:
            r31 = 0
        L_0x053a:
            r32 = 0
            if (r6 == 0) goto L_0x0541
            r33 = 0
            goto L_0x0543
        L_0x0541:
            r33 = 1082130432(0x40800000, float:4.0)
        L_0x0543:
            r34 = 0
            r29 = r5
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r28, r29, r30, r31, r32, r33, r34)
            r13.addView(r0, r5)
            android.widget.FrameLayout r0 = r9.floatingButtonContainer
            org.telegram.ui.-$$Lambda$DialogsActivity$IytOkq_hVLb_L5wWv2AeJatOt8Q r5 = new org.telegram.ui.-$$Lambda$DialogsActivity$IytOkq_hVLb_L5wWv2AeJatOt8Q
            r5.<init>()
            r0.setOnClickListener(r5)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r10)
            r9.floatingButton = r0
            android.widget.ImageView$ScaleType r5 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            java.lang.String r5 = "chats_actionBackground"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            java.lang.String r6 = "chats_actionPressedBackground"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r0, r5, r6)
            if (r1 >= r4) goto L_0x05a6
            android.content.res.Resources r5 = r36.getResources()
            r6 = 2131165415(0x7var_e7, float:1.7945046E38)
            android.graphics.drawable.Drawable r5 = r5.getDrawable(r6)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            android.graphics.PorterDuffColorFilter r6 = new android.graphics.PorterDuffColorFilter
            r2 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r3 = android.graphics.PorterDuff.Mode.MULTIPLY
            r6.<init>(r2, r3)
            r5.setColorFilter(r6)
            org.telegram.ui.Components.CombinedDrawable r2 = new org.telegram.ui.Components.CombinedDrawable
            r2.<init>(r5, r0, r11, r11)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r2.setIconSize(r0, r3)
            r0 = r2
        L_0x05a6:
            android.widget.ImageView r2 = r9.floatingButton
            r2.setBackgroundDrawable(r0)
            android.widget.ImageView r0 = r9.floatingButton
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            java.lang.String r3 = "chats_actionIcon"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r3, r5)
            r0.setColorFilter(r2)
            android.widget.ImageView r0 = r9.floatingButton
            r2 = 2131165414(0x7var_e6, float:1.7945044E38)
            r0.setImageResource(r2)
            if (r1 < r4) goto L_0x062c
            android.animation.StateListAnimator r0 = new android.animation.StateListAnimator
            r0.<init>()
            r2 = 1
            int[] r3 = new int[r2]
            r5 = 16842919(0x10100a7, float:2.3694026E-38)
            r3[r11] = r5
            android.widget.ImageView r5 = r9.floatingButton
            android.util.Property r6 = android.view.View.TRANSLATION_Z
            r14 = 2
            float[] r8 = new float[r14]
            r14 = 1073741824(0x40000000, float:2.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r12 = (float) r12
            r8[r11] = r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r12 = (float) r12
            r8[r2] = r12
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r8)
            r14 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r5 = r5.setDuration(r14)
            r0.addState(r3, r5)
            int[] r3 = new int[r11]
            android.widget.ImageView r5 = r9.floatingButton
            r14 = 2
            float[] r15 = new float[r14]
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            r15[r11] = r7
            r7 = 1073741824(0x40000000, float:2.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            r15[r2] = r7
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r15)
            r6 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r5 = r5.setDuration(r6)
            r0.addState(r3, r5)
            android.widget.ImageView r3 = r9.floatingButton
            r3.setStateListAnimator(r0)
            android.widget.ImageView r0 = r9.floatingButton
            org.telegram.ui.DialogsActivity$17 r3 = new org.telegram.ui.DialogsActivity$17
            r3.<init>(r9)
            r0.setOutlineProvider(r3)
            goto L_0x062d
        L_0x062c:
            r2 = 1
        L_0x062d:
            android.widget.FrameLayout r0 = r9.floatingButtonContainer
            r3 = 2131625998(0x7f0e080e, float:1.887922E38)
            java.lang.String r5 = "NewMessageTitle"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r0.setContentDescription(r3)
            android.widget.FrameLayout r0 = r9.floatingButtonContainer
            android.widget.ImageView r3 = r9.floatingButton
            if (r1 < r4) goto L_0x0644
            r25 = 56
            goto L_0x0646
        L_0x0644:
            r25 = 60
        L_0x0646:
            if (r1 < r4) goto L_0x064b
            r5 = 56
            goto L_0x064d
        L_0x064b:
            r5 = 60
        L_0x064d:
            float r5 = (float) r5
            r27 = 51
            r28 = 1092616192(0x41200000, float:10.0)
            r29 = 0
            r30 = 1092616192(0x41200000, float:10.0)
            r31 = 0
            r26 = r5
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r0.addView(r3, r5)
            r0 = 0
            r9.searchTabsView = r0
            java.lang.String r0 = r9.searchString
            if (r0 == 0) goto L_0x0673
            r9.showSearch(r2, r11)
            org.telegram.ui.ActionBar.ActionBar r0 = r9.actionBar
            java.lang.String r3 = r9.searchString
            r0.openSearchField(r3, r11)
            goto L_0x0697
        L_0x0673:
            java.lang.String r0 = r9.initialSearchString
            if (r0 == 0) goto L_0x0694
            r9.showSearch(r2, r11)
            org.telegram.ui.ActionBar.ActionBar r0 = r9.actionBar
            java.lang.String r3 = r9.initialSearchString
            r0.openSearchField(r3, r11)
            r0 = 0
            r9.initialSearchString = r0
            org.telegram.ui.Components.FilterTabsView r0 = r9.filterTabsView
            if (r0 == 0) goto L_0x0697
            r3 = 1110441984(0x42300000, float:44.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = -r3
            float r3 = (float) r3
            r0.setTranslationY(r3)
            goto L_0x0697
        L_0x0694:
            r9.showSearch(r11, r11)
        L_0x0697:
            boolean r0 = r9.onlySelect
            if (r0 != 0) goto L_0x06e2
            int r0 = r9.initialDialogsType
            if (r0 != 0) goto L_0x06e2
            org.telegram.ui.Components.FragmentContextView r0 = new org.telegram.ui.Components.FragmentContextView
            r0.<init>(r10, r9, r2)
            r9.fragmentLocationContextView = r0
            r24 = -1
            r25 = 1108869120(0x42180000, float:38.0)
            r26 = 51
            r27 = 0
            r28 = -1039138816(0xffffffffCLASSNAME, float:-36.0)
            r29 = 0
            r30 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r0.setLayoutParams(r3)
            org.telegram.ui.Components.FragmentContextView r0 = r9.fragmentLocationContextView
            r13.addView(r0)
            org.telegram.ui.Components.FragmentContextView r0 = new org.telegram.ui.Components.FragmentContextView
            r0.<init>(r10, r9, r11)
            r9.fragmentContextView = r0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r0.setLayoutParams(r3)
            org.telegram.ui.Components.FragmentContextView r0 = r9.fragmentContextView
            r13.addView(r0)
            org.telegram.ui.Components.FragmentContextView r0 = r9.fragmentContextView
            org.telegram.ui.Components.FragmentContextView r3 = r9.fragmentLocationContextView
            r0.setAdditionalContextView(r3)
            org.telegram.ui.Components.FragmentContextView r0 = r9.fragmentLocationContextView
            org.telegram.ui.Components.FragmentContextView r3 = r9.fragmentContextView
            r0.setAdditionalContextView(r3)
            goto L_0x0721
        L_0x06e2:
            int r0 = r9.initialDialogsType
            r3 = 3
            if (r0 != r3) goto L_0x0721
            org.telegram.ui.Components.ChatActivityEnterView r0 = r9.commentView
            if (r0 == 0) goto L_0x06ee
            r0.onDestroy()
        L_0x06ee:
            org.telegram.ui.Components.ChatActivityEnterView r0 = new org.telegram.ui.Components.ChatActivityEnterView
            android.app.Activity r3 = r35.getParentActivity()
            r5 = 0
            r0.<init>(r3, r13, r5, r11)
            r9.commentView = r0
            r0.setAllowStickersAndGifs(r11, r11)
            org.telegram.ui.Components.ChatActivityEnterView r0 = r9.commentView
            r0.setForceShowSendButton(r2, r11)
            org.telegram.ui.Components.ChatActivityEnterView r0 = r9.commentView
            r3 = 8
            r0.setVisibility(r3)
            org.telegram.ui.Components.ChatActivityEnterView r0 = r9.commentView
            r3 = 83
            r5 = -2
            r6 = -1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r5, r3)
            r13.addView(r0, r3)
            org.telegram.ui.Components.ChatActivityEnterView r0 = r9.commentView
            org.telegram.ui.DialogsActivity$18 r3 = new org.telegram.ui.DialogsActivity$18
            r3.<init>()
            r0.setDelegate(r3)
            goto L_0x0722
        L_0x0721:
            r6 = -1
        L_0x0722:
            org.telegram.ui.Components.FilterTabsView r0 = r9.filterTabsView
            if (r0 == 0) goto L_0x072f
            r3 = 1110441984(0x42300000, float:44.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r3)
            r13.addView(r0, r3)
        L_0x072f:
            boolean r0 = r9.onlySelect
            if (r0 != 0) goto L_0x0748
            r0 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r0)
            boolean r3 = r9.inPreviewMode
            if (r3 == 0) goto L_0x0743
            if (r1 < r4) goto L_0x0743
            int r1 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            r0.topMargin = r1
        L_0x0743:
            org.telegram.ui.ActionBar.ActionBar r1 = r9.actionBar
            r13.addView(r1, r0)
        L_0x0748:
            r0 = 0
            r1 = 2
        L_0x074a:
            if (r0 >= r1) goto L_0x0771
            org.telegram.ui.Components.UndoView[] r3 = r9.undoView
            org.telegram.ui.DialogsActivity$19 r4 = new org.telegram.ui.DialogsActivity$19
            r4.<init>(r10)
            r3[r0] = r4
            org.telegram.ui.Components.UndoView[] r3 = r9.undoView
            r3 = r3[r0]
            r24 = -1
            r25 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r26 = 83
            r27 = 1090519040(0x41000000, float:8.0)
            r28 = 0
            r29 = 1090519040(0x41000000, float:8.0)
            r30 = 1090519040(0x41000000, float:8.0)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r13.addView(r3, r4)
            int r0 = r0 + 1
            goto L_0x074a
        L_0x0771:
            int r0 = r9.folderId
            if (r0 == 0) goto L_0x07bd
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r9.viewPages
            r0 = r0[r11]
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r0.listView
            java.lang.String r1 = "actionBarDefaultArchived"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setGlowColor(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r9.actionBar
            java.lang.String r1 = "actionBarDefaultArchivedTitle"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTitleColor(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r9.actionBar
            java.lang.String r1 = "actionBarDefaultArchivedIcon"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setItemsColor(r1, r11)
            org.telegram.ui.ActionBar.ActionBar r0 = r9.actionBar
            java.lang.String r1 = "actionBarDefaultArchivedSelector"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setItemsBackgroundColor(r1, r11)
            org.telegram.ui.ActionBar.ActionBar r0 = r9.actionBar
            java.lang.String r1 = "actionBarDefaultArchivedSearch"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setSearchTextColor(r1, r11)
            org.telegram.ui.ActionBar.ActionBar r0 = r9.actionBar
            java.lang.String r1 = "actionBarDefaultSearchArchivedPlaceholder"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setSearchTextColor(r1, r2)
        L_0x07bd:
            boolean r0 = r9.onlySelect
            if (r0 != 0) goto L_0x07dd
            int r0 = r9.initialDialogsType
            if (r0 != 0) goto L_0x07dd
            android.view.View r0 = new android.view.View
            r0.<init>(r10)
            r9.blurredView = r0
            r1 = 8
            r0.setVisibility(r1)
            android.view.View r0 = r9.blurredView
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            r3 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r1)
            r13.addView(r0, r1)
        L_0x07dd:
            android.graphics.Paint r0 = r9.actionBarDefaultPaint
            int r1 = r9.folderId
            if (r1 != 0) goto L_0x07e6
            r1 = r16
            goto L_0x07e8
        L_0x07e6:
            java.lang.String r1 = "actionBarDefaultArchived"
        L_0x07e8:
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setColor(r1)
            boolean r0 = r9.inPreviewMode
            if (r0 == 0) goto L_0x0869
            int r0 = r9.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            org.telegram.tgnet.TLRPC$User r0 = r0.getCurrentUser()
            org.telegram.ui.Components.ChatAvatarContainer r1 = new org.telegram.ui.Components.ChatAvatarContainer
            org.telegram.ui.ActionBar.ActionBar r3 = r9.actionBar
            android.content.Context r3 = r3.getContext()
            r4 = 0
            r1.<init>(r3, r4, r11)
            r9.avatarContainer = r1
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r0)
            r1.setTitle(r3)
            org.telegram.ui.Components.ChatAvatarContainer r1 = r9.avatarContainer
            int r3 = r9.currentAccount
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatUserStatus(r3, r0)
            r1.setSubtitle(r3)
            org.telegram.ui.Components.ChatAvatarContainer r1 = r9.avatarContainer
            r1.setUserAvatar(r0, r2)
            org.telegram.ui.Components.ChatAvatarContainer r0 = r9.avatarContainer
            r0.setOccupyStatusBar(r11)
            org.telegram.ui.Components.ChatAvatarContainer r0 = r9.avatarContainer
            r1 = 1092616192(0x41200000, float:10.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setLeftPadding(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r9.actionBar
            org.telegram.ui.Components.ChatAvatarContainer r1 = r9.avatarContainer
            r2 = -2
            r3 = -1082130432(0xffffffffbvar_, float:-1.0)
            r4 = 51
            r5 = 0
            r6 = 0
            r7 = 1109393408(0x42200000, float:40.0)
            r8 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3, r4, r5, r6, r7, r8)
            r0.addView(r1, r11, r2)
            android.widget.ImageView r0 = r9.floatingButton
            r1 = 4
            r0.setVisibility(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r9.actionBar
            r0.setOccupyStatusBar(r11)
            org.telegram.ui.ActionBar.ActionBar r0 = r9.actionBar
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r0.setBackgroundColor(r1)
            org.telegram.ui.Components.FragmentContextView r0 = r9.fragmentContextView
            if (r0 == 0) goto L_0x0862
            r13.removeView(r0)
        L_0x0862:
            org.telegram.ui.Components.FragmentContextView r0 = r9.fragmentLocationContextView
            if (r0 == 0) goto L_0x0869
            r13.removeView(r0)
        L_0x0869:
            r9.updateFilterTabs(r11)
            int r0 = r9.folderId
            if (r0 == 0) goto L_0x0893
            org.telegram.ui.Adapters.FiltersView$MediaFilterData r0 = new org.telegram.ui.Adapters.FiltersView$MediaFilterData
            r2 = 2131165340(0x7var_c, float:1.7944894E38)
            r3 = 2131165340(0x7var_c, float:1.7944894E38)
            r1 = 2131624257(0x7f0e0141, float:1.8875689E38)
            java.lang.String r4 = "Archive"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r5 = 0
            r6 = 7
            r1 = r0
            r1.<init>(r2, r3, r4, r5, r6)
            r0.removable = r11
            org.telegram.ui.ActionBar.ActionBar r1 = r9.actionBar
            r1.setSearchFilter(r0)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r9.searchItem
            r0.collapseSearchFilters()
        L_0x0893:
            android.view.View r0 = r9.fragmentView
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.createView(android.content.Context):android.view.View");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$2 */
    public /* synthetic */ void lambda$createView$2$DialogsActivity(View view) {
        this.filterTabsView.setIsEditing(false);
        showDoneItem(false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$3 */
    public /* synthetic */ void lambda$createView$3$DialogsActivity() {
        hideFloatingButton(false);
        scrollToTop();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$4 */
    public /* synthetic */ void lambda$createView$4$DialogsActivity(ViewPage viewPage, View view, int i) {
        onItemClick(view, i, viewPage.dialogsAdapter);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$5 */
    public /* synthetic */ void lambda$createView$5$DialogsActivity(View view, int i) {
        onItemClick(view, i, this.searchViewPager.dialogsSearchAdapter);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$6 */
    public /* synthetic */ void lambda$createView$6$DialogsActivity(boolean z, ArrayList arrayList, ArrayList arrayList2) {
        updateFiltersView(z, arrayList, arrayList2, true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$7 */
    public /* synthetic */ void lambda$createView$7$DialogsActivity(View view, int i) {
        this.filtersView.cancelClickRunnables(true);
        addSearchFilter(this.filtersView.getFilterAt(i));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$8 */
    public /* synthetic */ void lambda$createView$8$DialogsActivity(View view) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("destroyAfterSelect", true);
        presentFragment(new ContactsActivity(bundle));
    }

    /* access modifiers changed from: private */
    public void updateContextViewPosition() {
        FilterTabsView filterTabsView2 = this.filterTabsView;
        float f = 0.0f;
        float measuredHeight = (filterTabsView2 == null || filterTabsView2.getVisibility() == 8) ? 0.0f : (float) this.filterTabsView.getMeasuredHeight();
        ViewPagerFixed.TabsView tabsView = this.searchTabsView;
        float measuredHeight2 = (tabsView == null || tabsView.getVisibility() == 8) ? 0.0f : (float) this.searchTabsView.getMeasuredHeight();
        if (this.fragmentContextView != null) {
            FragmentContextView fragmentContextView2 = this.fragmentLocationContextView;
            float dp = (fragmentContextView2 == null || fragmentContextView2.getVisibility() != 0) ? 0.0f : ((float) AndroidUtilities.dp(36.0f)) + 0.0f;
            FragmentContextView fragmentContextView3 = this.fragmentContextView;
            float topPadding2 = dp + fragmentContextView3.getTopPadding() + this.actionBar.getTranslationY();
            float f2 = this.searchAnimationProgress;
            fragmentContextView3.setTranslationY(topPadding2 + ((1.0f - f2) * measuredHeight) + (f2 * measuredHeight2));
        }
        if (this.fragmentLocationContextView != null) {
            FragmentContextView fragmentContextView4 = this.fragmentContextView;
            if (fragmentContextView4 != null) {
                f = 0.0f + fragmentContextView4.getTopPadding();
            }
            FragmentContextView fragmentContextView5 = this.fragmentLocationContextView;
            float topPadding3 = f + fragmentContextView5.getTopPadding() + this.actionBar.getTranslationY();
            float f3 = this.searchAnimationProgress;
            fragmentContextView5.setTranslationY(topPadding3 + (measuredHeight * (1.0f - f3)) + (measuredHeight2 * f3));
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0089  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0090  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x009d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateFiltersView(boolean r10, java.util.ArrayList<org.telegram.tgnet.TLObject> r11, java.util.ArrayList<org.telegram.ui.Adapters.FiltersView.DateData> r12, boolean r13) {
        /*
            r9 = this;
            boolean r0 = r9.searchIsShowed
            if (r0 == 0) goto L_0x00aa
            boolean r0 = r9.onlySelect
            if (r0 == 0) goto L_0x000a
            goto L_0x00aa
        L_0x000a:
            org.telegram.ui.Components.SearchViewPager r0 = r9.searchViewPager
            java.util.ArrayList r0 = r0.getCurrentSearchFilters()
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
        L_0x0015:
            int r6 = r0.size()
            r7 = 1
            if (r2 >= r6) goto L_0x0046
            java.lang.Object r6 = r0.get(r2)
            org.telegram.ui.Adapters.FiltersView$MediaFilterData r6 = (org.telegram.ui.Adapters.FiltersView.MediaFilterData) r6
            boolean r6 = r6.isMedia()
            if (r6 == 0) goto L_0x002a
            r3 = 1
            goto L_0x0043
        L_0x002a:
            java.lang.Object r6 = r0.get(r2)
            org.telegram.ui.Adapters.FiltersView$MediaFilterData r6 = (org.telegram.ui.Adapters.FiltersView.MediaFilterData) r6
            int r6 = r6.filterType
            r8 = 4
            if (r6 != r8) goto L_0x0037
            r4 = 1
            goto L_0x0043
        L_0x0037:
            java.lang.Object r6 = r0.get(r2)
            org.telegram.ui.Adapters.FiltersView$MediaFilterData r6 = (org.telegram.ui.Adapters.FiltersView.MediaFilterData) r6
            int r6 = r6.filterType
            r8 = 6
            if (r6 != r8) goto L_0x0043
            r5 = 1
        L_0x0043:
            int r2 = r2 + 1
            goto L_0x0015
        L_0x0046:
            if (r11 == 0) goto L_0x004e
            boolean r0 = r11.isEmpty()
            if (r0 == 0) goto L_0x0056
        L_0x004e:
            if (r12 == 0) goto L_0x0058
            boolean r0 = r12.isEmpty()
            if (r0 != 0) goto L_0x0058
        L_0x0056:
            r0 = 1
            goto L_0x0059
        L_0x0058:
            r0 = 0
        L_0x0059:
            r2 = 0
            if (r3 != 0) goto L_0x0061
            if (r0 != 0) goto L_0x0061
            if (r10 == 0) goto L_0x0061
            goto L_0x0086
        L_0x0061:
            if (r0 == 0) goto L_0x0086
            if (r11 == 0) goto L_0x006e
            boolean r10 = r11.isEmpty()
            if (r10 != 0) goto L_0x006e
            if (r4 != 0) goto L_0x006e
            goto L_0x006f
        L_0x006e:
            r11 = r2
        L_0x006f:
            if (r12 == 0) goto L_0x007a
            boolean r10 = r12.isEmpty()
            if (r10 != 0) goto L_0x007a
            if (r5 != 0) goto L_0x007a
            goto L_0x007b
        L_0x007a:
            r12 = r2
        L_0x007b:
            if (r11 != 0) goto L_0x007f
            if (r12 == 0) goto L_0x0086
        L_0x007f:
            org.telegram.ui.Adapters.FiltersView r10 = r9.filtersView
            r10.setUsersAndDates(r11, r12)
            r10 = 1
            goto L_0x0087
        L_0x0086:
            r10 = 0
        L_0x0087:
            if (r10 != 0) goto L_0x008e
            org.telegram.ui.Adapters.FiltersView r11 = r9.filtersView
            r11.setUsersAndDates(r2, r2)
        L_0x008e:
            if (r13 != 0) goto L_0x0099
            org.telegram.ui.Adapters.FiltersView r11 = r9.filtersView
            androidx.recyclerview.widget.RecyclerView$Adapter r11 = r11.getAdapter()
            r11.notifyDataSetChanged()
        L_0x0099:
            org.telegram.ui.ViewPagerFixed$TabsView r11 = r9.searchTabsView
            if (r11 == 0) goto L_0x00a0
            r11.hide(r10, r7)
        L_0x00a0:
            org.telegram.ui.Adapters.FiltersView r11 = r9.filtersView
            r11.setEnabled(r10)
            org.telegram.ui.Adapters.FiltersView r10 = r9.filtersView
            r10.setVisibility(r1)
        L_0x00aa:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.updateFiltersView(boolean, java.util.ArrayList, java.util.ArrayList, boolean):void");
    }

    private void addSearchFilter(FiltersView.MediaFilterData mediaFilterData) {
        if (this.searchIsShowed) {
            ArrayList<FiltersView.MediaFilterData> currentSearchFilters = this.searchViewPager.getCurrentSearchFilters();
            if (!currentSearchFilters.isEmpty()) {
                int i = 0;
                while (i < currentSearchFilters.size()) {
                    if (!mediaFilterData.isSameType(currentSearchFilters.get(i))) {
                        i++;
                    } else {
                        return;
                    }
                }
            }
            currentSearchFilters.add(mediaFilterData);
            this.actionBar.setSearchFilter(mediaFilterData);
            this.actionBar.setSearchFieldText("");
            updateFiltersView(true, (ArrayList<TLObject>) null, (ArrayList<FiltersView.DateData>) null, true);
        }
    }

    private void createActionMode() {
        if (!this.actionBar.actionModeIsExist((String) null)) {
            ActionBarMenu createActionMode = this.actionBar.createActionMode();
            createActionMode.setBackground((Drawable) null);
            NumberTextView numberTextView = new NumberTextView(createActionMode.getContext());
            this.selectedDialogsCountTextView = numberTextView;
            numberTextView.setTextSize(18);
            this.selectedDialogsCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.selectedDialogsCountTextView.setTextColor(Theme.getColor("actionBarActionModeDefaultIcon"));
            createActionMode.addView(this.selectedDialogsCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 72, 0, 0, 0));
            this.selectedDialogsCountTextView.setOnTouchListener($$Lambda$DialogsActivity$_46UWFkMiHPNOe0V5byjIcXuRA.INSTANCE);
            this.pinItem = createActionMode.addItemWithWidth(100, NUM, AndroidUtilities.dp(54.0f));
            this.muteItem = createActionMode.addItemWithWidth(104, NUM, AndroidUtilities.dp(54.0f));
            this.archive2Item = createActionMode.addItemWithWidth(107, NUM, AndroidUtilities.dp(54.0f));
            this.deleteItem = createActionMode.addItemWithWidth(102, NUM, AndroidUtilities.dp(54.0f), LocaleController.getString("Delete", NUM));
            ActionBarMenuItem addItemWithWidth = createActionMode.addItemWithWidth(0, NUM, AndroidUtilities.dp(54.0f), LocaleController.getString("AccDescrMoreOptions", NUM));
            this.archiveItem = addItemWithWidth.addSubItem(105, NUM, LocaleController.getString("Archive", NUM));
            this.pin2Item = addItemWithWidth.addSubItem(108, NUM, LocaleController.getString("DialogPin", NUM));
            this.addToFolderItem = addItemWithWidth.addSubItem(109, NUM, LocaleController.getString("FilterAddTo", NUM));
            this.removeFromFolderItem = addItemWithWidth.addSubItem(110, NUM, LocaleController.getString("FilterRemoveFrom", NUM));
            this.readItem = addItemWithWidth.addSubItem(101, NUM, LocaleController.getString("MarkAsRead", NUM));
            this.clearItem = addItemWithWidth.addSubItem(103, NUM, LocaleController.getString("ClearHistory", NUM));
            this.blockItem = addItemWithWidth.addSubItem(106, NUM, LocaleController.getString("BlockUser", NUM));
            this.actionModeViews.add(this.pinItem);
            this.actionModeViews.add(this.archive2Item);
            this.actionModeViews.add(this.muteItem);
            this.actionModeViews.add(this.deleteItem);
            this.actionModeViews.add(addItemWithWidth);
            this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
                public void onItemClick(int i) {
                    int i2 = i;
                    if (i2 == 201 || (i2 == 200 && DialogsActivity.this.searchViewPager != null)) {
                        DialogsActivity.this.searchViewPager.onActionBarItemClick(i2);
                    } else if (i2 == -1) {
                        if (DialogsActivity.this.filterTabsView != null && DialogsActivity.this.filterTabsView.isEditing()) {
                            DialogsActivity.this.filterTabsView.setIsEditing(false);
                            DialogsActivity.this.showDoneItem(false);
                        } else if (DialogsActivity.this.actionBar.isActionModeShowed()) {
                            if (DialogsActivity.this.searchViewPager == null || DialogsActivity.this.searchViewPager.getVisibility() != 0) {
                                DialogsActivity.this.hideActionMode(true);
                            } else {
                                DialogsActivity.this.searchViewPager.hideActionMode();
                            }
                        } else if (DialogsActivity.this.onlySelect || DialogsActivity.this.folderId != 0) {
                            DialogsActivity.this.finishFragment();
                        } else if (DialogsActivity.this.parentLayout != null) {
                            DialogsActivity.this.parentLayout.getDrawerLayoutContainer().openDrawer(false);
                        }
                    } else if (i2 == 1) {
                        SharedConfig.appLocked = !SharedConfig.appLocked;
                        SharedConfig.saveConfig();
                        DialogsActivity.this.updatePasscodeButton();
                    } else if (i2 == 2) {
                        DialogsActivity.this.presentFragment(new ProxyListActivity());
                    } else if (i2 < 10 || i2 >= 13) {
                        if (i2 == 109) {
                            DialogsActivity dialogsActivity = DialogsActivity.this;
                            FiltersListBottomSheet filtersListBottomSheet = new FiltersListBottomSheet(dialogsActivity, dialogsActivity.selectedDialogs);
                            filtersListBottomSheet.setDelegate(new FiltersListBottomSheet.FiltersListBottomSheetDelegate() {
                                public final void didSelectFilter(MessagesController.DialogFilter dialogFilter) {
                                    DialogsActivity.AnonymousClass20.this.lambda$onItemClick$0$DialogsActivity$20(dialogFilter);
                                }
                            });
                            DialogsActivity.this.showDialog(filtersListBottomSheet);
                        } else if (i2 == 110) {
                            MessagesController.DialogFilter dialogFilter = DialogsActivity.this.getMessagesController().dialogFilters.get(DialogsActivity.this.viewPages[0].selectedType);
                            DialogsActivity dialogsActivity2 = DialogsActivity.this;
                            ArrayList<Integer> dialogsCount = FiltersListBottomSheet.getDialogsCount(dialogsActivity2, dialogFilter, dialogsActivity2.selectedDialogs, false, false);
                            if ((dialogFilter != null ? dialogFilter.neverShow.size() : 0) + dialogsCount.size() > 100) {
                                DialogsActivity dialogsActivity3 = DialogsActivity.this;
                                dialogsActivity3.showDialog(AlertsCreator.createSimpleAlert(dialogsActivity3.getParentActivity(), LocaleController.getString("FilterAddToAlertFullTitle", NUM), LocaleController.getString("FilterRemoveFromAlertFullText", NUM)).create());
                                return;
                            }
                            if (!dialogsCount.isEmpty()) {
                                dialogFilter.neverShow.addAll(dialogsCount);
                                for (int i3 = 0; i3 < dialogsCount.size(); i3++) {
                                    Integer num = dialogsCount.get(i3);
                                    dialogFilter.alwaysShow.remove(num);
                                    dialogFilter.pinnedDialogs.remove((long) num.intValue());
                                }
                                FilterCreateActivity.saveFilterToServer(dialogFilter, dialogFilter.flags, dialogFilter.name, dialogFilter.alwaysShow, dialogFilter.neverShow, dialogFilter.pinnedDialogs, false, false, true, false, false, DialogsActivity.this, (Runnable) null);
                            }
                            DialogsActivity.this.getUndoView().showWithAction(dialogsCount.size() == 1 ? (long) dialogsCount.get(0).intValue() : 0, 21, Integer.valueOf(dialogsCount.size()), dialogFilter, (Runnable) null, (Runnable) null);
                            DialogsActivity.this.hideActionMode(false);
                        } else if (i2 == 100 || i2 == 101 || i2 == 102 || i2 == 103 || i2 == 104 || i2 == 105 || i2 == 106 || i2 == 107 || i2 == 108) {
                            DialogsActivity.this.perfromSelectedDialogsAction(i2, true);
                        }
                    } else if (DialogsActivity.this.getParentActivity() != null) {
                        DialogsActivityDelegate access$19100 = DialogsActivity.this.delegate;
                        LaunchActivity launchActivity = (LaunchActivity) DialogsActivity.this.getParentActivity();
                        launchActivity.switchToAccount(i2 - 10, true);
                        DialogsActivity dialogsActivity4 = new DialogsActivity(DialogsActivity.this.arguments);
                        dialogsActivity4.setDelegate(access$19100);
                        launchActivity.presentFragment(dialogsActivity4, false, true);
                    }
                }

                /* access modifiers changed from: private */
                /* renamed from: lambda$onItemClick$0 */
                public /* synthetic */ void lambda$onItemClick$0$DialogsActivity$20(MessagesController.DialogFilter dialogFilter) {
                    ArrayList<Integer> arrayList;
                    long j;
                    ArrayList<Integer> arrayList2;
                    MessagesController.DialogFilter dialogFilter2 = dialogFilter;
                    DialogsActivity dialogsActivity = DialogsActivity.this;
                    ArrayList<Integer> dialogsCount = FiltersListBottomSheet.getDialogsCount(dialogsActivity, dialogFilter2, dialogsActivity.selectedDialogs, true, false);
                    if ((dialogFilter2 != null ? dialogFilter2.alwaysShow.size() : 0) + dialogsCount.size() > 100) {
                        DialogsActivity dialogsActivity2 = DialogsActivity.this;
                        dialogsActivity2.showDialog(AlertsCreator.createSimpleAlert(dialogsActivity2.getParentActivity(), LocaleController.getString("FilterAddToAlertFullTitle", NUM), LocaleController.getString("FilterAddToAlertFullText", NUM)).create());
                        return;
                    }
                    if (dialogFilter2 != null) {
                        if (!dialogsCount.isEmpty()) {
                            for (int i = 0; i < dialogsCount.size(); i++) {
                                dialogFilter2.neverShow.remove(dialogsCount.get(i));
                            }
                            dialogFilter2.alwaysShow.addAll(dialogsCount);
                            arrayList = dialogsCount;
                            FilterCreateActivity.saveFilterToServer(dialogFilter, dialogFilter2.flags, dialogFilter2.name, dialogFilter2.alwaysShow, dialogFilter2.neverShow, dialogFilter2.pinnedDialogs, false, false, true, true, false, DialogsActivity.this, (Runnable) null);
                        } else {
                            arrayList = dialogsCount;
                        }
                        if (arrayList.size() == 1) {
                            arrayList2 = arrayList;
                            j = (long) arrayList2.get(0).intValue();
                        } else {
                            arrayList2 = arrayList;
                            j = 0;
                        }
                        DialogsActivity.this.getUndoView().showWithAction(j, 20, Integer.valueOf(arrayList2.size()), dialogFilter, (Runnable) null, (Runnable) null);
                    } else {
                        DialogsActivity.this.presentFragment(new FilterCreateActivity((MessagesController.DialogFilter) null, dialogsCount));
                    }
                    DialogsActivity.this.hideActionMode(true);
                }
            });
        }
    }

    /* JADX WARNING: type inference failed for: r7v0, types: [boolean] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void switchToCurrentSelectedMode(boolean r7) {
        /*
            r6 = this;
            r0 = 0
            r1 = 0
        L_0x0002:
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r6.viewPages
            int r3 = r2.length
            if (r1 >= r3) goto L_0x0013
            r2 = r2[r1]
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r2 = r2.listView
            r2.stopScroll()
            int r1 = r1 + 1
            goto L_0x0002
        L_0x0013:
            r1 = r2[r7]
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r1 = r1.listView
            r1.getAdapter()
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r6.viewPages
            r1 = r1[r7]
            int r1 = r1.selectedType
            r2 = 2147483647(0x7fffffff, float:NaN)
            r3 = 1
            if (r1 != r2) goto L_0x003d
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r6.viewPages
            r1 = r1[r7]
            int unused = r1.dialogsType = r0
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r6.viewPages
            r1 = r1[r7]
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r1 = r1.listView
            r1.updatePullState()
            goto L_0x008e
        L_0x003d:
            org.telegram.messenger.MessagesController r1 = r6.getMessagesController()
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r1 = r1.dialogFilters
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r6.viewPages
            r2 = r2[r7]
            int r2 = r2.selectedType
            java.lang.Object r1 = r1.get(r2)
            org.telegram.messenger.MessagesController$DialogFilter r1 = (org.telegram.messenger.MessagesController.DialogFilter) r1
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r6.viewPages
            r4 = r7 ^ 1
            r2 = r2[r4]
            int r2 = r2.dialogsType
            r4 = 8
            r5 = 7
            if (r2 != r5) goto L_0x0068
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r6.viewPages
            r2 = r2[r7]
            int unused = r2.dialogsType = r4
            goto L_0x006f
        L_0x0068:
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r6.viewPages
            r2 = r2[r7]
            int unused = r2.dialogsType = r5
        L_0x006f:
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r6.viewPages
            r2 = r2[r7]
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r2 = r2.listView
            r2.setScrollEnabled(r3)
            org.telegram.messenger.MessagesController r2 = r6.getMessagesController()
            org.telegram.ui.DialogsActivity$ViewPage[] r5 = r6.viewPages
            r5 = r5[r7]
            int r5 = r5.dialogsType
            if (r5 != r4) goto L_0x008a
            r4 = 1
            goto L_0x008b
        L_0x008a:
            r4 = 0
        L_0x008b:
            r2.selectDialogFilter(r1, r4)
        L_0x008e:
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r6.viewPages
            r1 = r1[r7]
            org.telegram.ui.Adapters.DialogsAdapter r1 = r1.dialogsAdapter
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r6.viewPages
            r2 = r2[r7]
            int r2 = r2.dialogsType
            r1.setDialogsType(r2)
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r6.viewPages
            r1 = r1[r7]
            androidx.recyclerview.widget.LinearLayoutManager r1 = r1.layoutManager
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r6.viewPages
            r2 = r2[r7]
            int r2 = r2.dialogsType
            if (r2 != 0) goto L_0x00ba
            boolean r2 = r6.hasHiddenArchive()
            if (r2 == 0) goto L_0x00ba
            r0 = 1
        L_0x00ba:
            org.telegram.ui.ActionBar.ActionBar r2 = r6.actionBar
            float r2 = r2.getTranslationY()
            int r2 = (int) r2
            r1.scrollToPositionWithOffset(r0, r2)
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r6.viewPages
            r7 = r0[r7]
            r6.checkListLoad(r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.switchToCurrentSelectedMode(boolean):void");
    }

    /* access modifiers changed from: private */
    public void showScrollbars(boolean z) {
        if (this.viewPages != null && this.scrollBarVisible != z) {
            this.scrollBarVisible = z;
            int i = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (i < viewPageArr.length) {
                    if (z) {
                        viewPageArr[i].listView.setScrollbarFadingEnabled(false);
                    }
                    this.viewPages[i].listView.setVerticalScrollBarEnabled(z);
                    if (z) {
                        this.viewPages[i].listView.setScrollbarFadingEnabled(true);
                    }
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    private void updateFilterTabs(boolean z) {
        int findFirstVisibleItemPosition;
        if (this.filterTabsView != null && !this.inPreviewMode && !this.searchIsShowed) {
            ActionBarPopupWindow actionBarPopupWindow = this.scrimPopupWindow;
            if (actionBarPopupWindow != null) {
                actionBarPopupWindow.dismiss();
                this.scrimPopupWindow = null;
            }
            ArrayList<MessagesController.DialogFilter> arrayList = getMessagesController().dialogFilters;
            MessagesController.getMainSettings(this.currentAccount);
            boolean z2 = true;
            if (arrayList.isEmpty()) {
                if (this.filterTabsView.getVisibility() != 8) {
                    this.filterTabsView.setIsEditing(false);
                    showDoneItem(false);
                    this.maybeStartTracking = false;
                    if (this.startedTracking) {
                        this.startedTracking = false;
                        this.viewPages[0].setTranslationX(0.0f);
                        ViewPage[] viewPageArr = this.viewPages;
                        viewPageArr[1].setTranslationX((float) viewPageArr[0].getMeasuredWidth());
                    }
                    if (this.viewPages[0].selectedType != Integer.MAX_VALUE) {
                        int unused = this.viewPages[0].selectedType = Integer.MAX_VALUE;
                        this.viewPages[0].dialogsAdapter.setDialogsType(0);
                        int unused2 = this.viewPages[0].dialogsType = 0;
                        this.viewPages[0].dialogsAdapter.notifyDataSetChanged();
                    }
                    this.viewPages[1].setVisibility(8);
                    int unused3 = this.viewPages[1].selectedType = Integer.MAX_VALUE;
                    this.viewPages[1].dialogsAdapter.setDialogsType(0);
                    int unused4 = this.viewPages[1].dialogsType = 0;
                    this.viewPages[1].dialogsAdapter.notifyDataSetChanged();
                    this.filterTabsView.setVisibility(8);
                    this.filterTabsView.setTag((Object) null);
                    int i = 0;
                    while (true) {
                        ViewPage[] viewPageArr2 = this.viewPages;
                        if (i >= viewPageArr2.length) {
                            break;
                        }
                        if (viewPageArr2[i].dialogsType == 0 && this.viewPages[i].archivePullViewState == 2 && hasHiddenArchive() && ((findFirstVisibleItemPosition = this.viewPages[i].layoutManager.findFirstVisibleItemPosition()) == 0 || findFirstVisibleItemPosition == 1)) {
                            this.viewPages[i].layoutManager.scrollToPositionWithOffset(findFirstVisibleItemPosition, 0);
                        }
                        this.viewPages[i].listView.setScrollingTouchSlop(0);
                        this.viewPages[i].listView.requestLayout();
                        this.viewPages[i].requestLayout();
                        i++;
                    }
                }
                ActionBarLayout actionBarLayout = this.parentLayout;
                if (actionBarLayout != null) {
                    actionBarLayout.getDrawerLayoutContainer().setAllowOpenDrawerBySwipe(true);
                }
            } else if (z || this.filterTabsView.getVisibility() != 0) {
                this.filterTabsView.setVisibility(0);
                this.filterTabsView.setTag(1);
                int currentTabId = this.filterTabsView.getCurrentTabId();
                if (currentTabId != Integer.MAX_VALUE && currentTabId >= arrayList.size()) {
                    this.filterTabsView.resetTabId();
                }
                this.filterTabsView.removeTabs();
                this.filterTabsView.addTab(Integer.MAX_VALUE, LocaleController.getString("FilterAllChats", NUM));
                int size = arrayList.size();
                for (int i2 = 0; i2 < size; i2++) {
                    this.filterTabsView.addTab(i2, arrayList.get(i2).name);
                }
                int currentTabId2 = this.filterTabsView.getCurrentTabId();
                if (currentTabId2 >= 0) {
                    int unused5 = this.viewPages[0].selectedType = currentTabId2;
                }
                int i3 = 0;
                while (true) {
                    ViewPage[] viewPageArr3 = this.viewPages;
                    if (i3 >= viewPageArr3.length) {
                        break;
                    }
                    if (viewPageArr3[i3].selectedType != Integer.MAX_VALUE && this.viewPages[i3].selectedType >= arrayList.size()) {
                        int unused6 = this.viewPages[i3].selectedType = arrayList.size() - 1;
                    }
                    this.viewPages[i3].listView.setScrollingTouchSlop(1);
                    i3++;
                }
                this.filterTabsView.finishAddingTabs();
                switchToCurrentSelectedMode(false);
                ActionBarLayout actionBarLayout2 = this.parentLayout;
                if (actionBarLayout2 != null) {
                    DrawerLayoutContainer drawerLayoutContainer = actionBarLayout2.getDrawerLayoutContainer();
                    if (currentTabId2 != this.filterTabsView.getFirstTabId()) {
                        z2 = false;
                    }
                    drawerLayoutContainer.setAllowOpenDrawerBySwipe(z2);
                }
            }
            updateCounters(false);
        }
    }

    public void finishFragment() {
        super.finishFragment();
        ActionBarPopupWindow actionBarPopupWindow = this.scrimPopupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.dismiss();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:110:0x01f2  */
    /* JADX WARNING: Removed duplicated region for block: B:119:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00af  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0135  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x01a8  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onResume() {
        /*
            r10 = this;
            int r0 = android.os.Build.VERSION.SDK_INT
            super.onResume()
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r10.parentLayout
            boolean r1 = r1.isInPreviewMode()
            r2 = 0
            if (r1 != 0) goto L_0x0024
            android.view.View r1 = r10.blurredView
            if (r1 == 0) goto L_0x0024
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x0024
            android.view.View r1 = r10.blurredView
            r3 = 8
            r1.setVisibility(r3)
            android.view.View r1 = r10.blurredView
            r1.setBackground(r2)
        L_0x0024:
            org.telegram.ui.Components.FilterTabsView r1 = r10.filterTabsView
            r3 = 1
            r4 = 0
            if (r1 == 0) goto L_0x0051
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x0051
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r10.parentLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r1 = r1.getDrawerLayoutContainer()
            org.telegram.ui.DialogsActivity$ViewPage[] r5 = r10.viewPages
            r5 = r5[r4]
            int r5 = r5.selectedType
            org.telegram.ui.Components.FilterTabsView r6 = r10.filterTabsView
            int r6 = r6.getFirstTabId()
            if (r5 == r6) goto L_0x004d
            boolean r5 = r10.searchIsShowed
            if (r5 == 0) goto L_0x004b
            goto L_0x004d
        L_0x004b:
            r5 = 0
            goto L_0x004e
        L_0x004d:
            r5 = 1
        L_0x004e:
            r1.setAllowOpenDrawerBySwipe(r5)
        L_0x0051:
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r10.viewPages
            if (r1 == 0) goto L_0x006b
            boolean r1 = r10.dialogsListFrozen
            if (r1 != 0) goto L_0x006b
            r1 = 0
        L_0x005a:
            org.telegram.ui.DialogsActivity$ViewPage[] r5 = r10.viewPages
            int r6 = r5.length
            if (r1 >= r6) goto L_0x006b
            r5 = r5[r1]
            org.telegram.ui.Adapters.DialogsAdapter r5 = r5.dialogsAdapter
            r5.notifyDataSetChanged()
            int r1 = r1 + 1
            goto L_0x005a
        L_0x006b:
            org.telegram.ui.Components.ChatActivityEnterView r1 = r10.commentView
            if (r1 == 0) goto L_0x0072
            r1.onResume()
        L_0x0072:
            boolean r1 = r10.onlySelect
            if (r1 != 0) goto L_0x0082
            int r1 = r10.folderId
            if (r1 != 0) goto L_0x0082
            org.telegram.messenger.MediaDataController r1 = r10.getMediaDataController()
            r5 = 4
            r1.checkStickers(r5)
        L_0x0082:
            org.telegram.ui.Components.SearchViewPager r1 = r10.searchViewPager
            if (r1 == 0) goto L_0x0089
            r1.onResume()
        L_0x0089:
            boolean r1 = r10.afterSignup
            if (r1 != 0) goto L_0x0099
            int r1 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            org.telegram.tgnet.TLRPC$TL_help_termsOfService r1 = r1.unacceptedTermsOfService
            if (r1 != 0) goto L_0x009b
            r1 = 1
            goto L_0x009c
        L_0x0099:
            r10.afterSignup = r4
        L_0x009b:
            r1 = 0
        L_0x009c:
            r5 = 2131624250(0x7f0e013a, float:1.8875674E38)
            java.lang.String r6 = "AppName"
            if (r1 == 0) goto L_0x0135
            boolean r1 = r10.checkPermission
            if (r1 == 0) goto L_0x0135
            boolean r1 = r10.onlySelect
            if (r1 != 0) goto L_0x0135
            r1 = 23
            if (r0 < r1) goto L_0x0135
            android.app.Activity r0 = r10.getParentActivity()
            if (r0 == 0) goto L_0x01a1
            r10.checkPermission = r4
            java.lang.String r1 = "android.permission.READ_CONTACTS"
            int r7 = r0.checkSelfPermission(r1)
            if (r7 == 0) goto L_0x00c1
            r7 = 1
            goto L_0x00c2
        L_0x00c1:
            r7 = 0
        L_0x00c2:
            java.lang.String r8 = "android.permission.WRITE_EXTERNAL_STORAGE"
            int r9 = r0.checkSelfPermission(r8)
            if (r9 == 0) goto L_0x00cc
            r9 = 1
            goto L_0x00cd
        L_0x00cc:
            r9 = 0
        L_0x00cd:
            if (r7 != 0) goto L_0x00d1
            if (r9 == 0) goto L_0x01a1
        L_0x00d1:
            r10.askingForPermissions = r3
            if (r7 == 0) goto L_0x00fb
            boolean r7 = r10.askAboutContacts
            if (r7 == 0) goto L_0x00fb
            org.telegram.messenger.UserConfig r7 = r10.getUserConfig()
            boolean r7 = r7.syncContacts
            if (r7 == 0) goto L_0x00fb
            boolean r1 = r0.shouldShowRequestPermissionRationale(r1)
            if (r1 == 0) goto L_0x00fb
            org.telegram.ui.-$$Lambda$DialogsActivity$NDjkpRJiaIxLmVxhQl2rcKvq5v4 r1 = new org.telegram.ui.-$$Lambda$DialogsActivity$NDjkpRJiaIxLmVxhQl2rcKvq5v4
            r1.<init>()
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = org.telegram.ui.Components.AlertsCreator.createContactsPermissionDialog(r0, r1)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r10.permissionDialog = r0
            r10.showDialog(r0)
            goto L_0x01a1
        L_0x00fb:
            if (r9 == 0) goto L_0x0131
            boolean r1 = r0.shouldShowRequestPermissionRationale(r8)
            if (r1 == 0) goto L_0x0131
            org.telegram.ui.ActionBar.AlertDialog$Builder r1 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r1.<init>((android.content.Context) r0)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r1.setTitle(r0)
            r0 = 2131626606(0x7f0e0a6e, float:1.8880453E38)
            java.lang.String r5 = "PermissionStorage"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r0)
            r1.setMessage(r0)
            r0 = 2131626258(0x7f0e0912, float:1.8879747E38)
            java.lang.String r5 = "OK"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r0)
            r1.setPositiveButton(r0, r2)
            org.telegram.ui.ActionBar.AlertDialog r0 = r1.create()
            r10.permissionDialog = r0
            r10.showDialog(r0)
            goto L_0x01a1
        L_0x0131:
            r10.askForPermissons(r3)
            goto L_0x01a1
        L_0x0135:
            boolean r1 = r10.onlySelect
            if (r1 != 0) goto L_0x01a1
            boolean r1 = org.telegram.messenger.XiaomiUtilities.isMIUI()
            if (r1 == 0) goto L_0x01a1
            r1 = 19
            if (r0 < r1) goto L_0x01a1
            r0 = 10020(0x2724, float:1.4041E-41)
            boolean r0 = org.telegram.messenger.XiaomiUtilities.isCustomPermissionGranted(r0)
            if (r0 != 0) goto L_0x01a1
            android.app.Activity r0 = r10.getParentActivity()
            if (r0 != 0) goto L_0x0152
            return
        L_0x0152:
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalNotificationsSettings()
            java.lang.String r1 = "askedAboutMiuiLockscreen"
            boolean r0 = r0.getBoolean(r1, r4)
            if (r0 == 0) goto L_0x015f
            return
        L_0x015f:
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r1 = r10.getParentActivity()
            r0.<init>((android.content.Context) r1)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r0.setTitle(r1)
            r1 = 2131626607(0x7f0e0a6f, float:1.8880455E38)
            java.lang.String r2 = "PermissionXiaomiLockscreen"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setMessage(r1)
            r1 = 2131626605(0x7f0e0a6d, float:1.888045E38)
            java.lang.String r2 = "PermissionOpenSettings"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.-$$Lambda$DialogsActivity$P7XKAmT26raLlvar_AlrL62XLpQ8 r2 = new org.telegram.ui.-$$Lambda$DialogsActivity$P7XKAmT26raLlvar_AlrL62XLpQ8
            r2.<init>()
            r0.setPositiveButton(r1, r2)
            r1 = 2131624914(0x7f0e03d2, float:1.8877021E38)
            java.lang.String r2 = "ContactsPermissionAlertNotNow"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.-$$Lambda$DialogsActivity$tmX8hHmbkMGLu5yCBS8o2Ti7cuA r2 = org.telegram.ui.$$Lambda$DialogsActivity$tmX8hHmbkMGLu5yCBS8o2Ti7cuA.INSTANCE
            r0.setNegativeButton(r1, r2)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r10.showDialog(r0)
        L_0x01a1:
            r10.showFiltersHint()
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r10.viewPages
            if (r0 == 0) goto L_0x01e3
            r0 = 0
        L_0x01a9:
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r10.viewPages
            int r2 = r1.length
            if (r0 >= r2) goto L_0x01e3
            r1 = r1[r0]
            int r1 = r1.dialogsType
            if (r1 != 0) goto L_0x01e0
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r10.viewPages
            r1 = r1[r0]
            int r1 = r1.archivePullViewState
            r2 = 2
            if (r1 != r2) goto L_0x01e0
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r10.viewPages
            r1 = r1[r0]
            androidx.recyclerview.widget.LinearLayoutManager r1 = r1.layoutManager
            int r1 = r1.findFirstVisibleItemPosition()
            if (r1 != 0) goto L_0x01e0
            boolean r1 = r10.hasHiddenArchive()
            if (r1 == 0) goto L_0x01e0
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r10.viewPages
            r1 = r1[r0]
            androidx.recyclerview.widget.LinearLayoutManager r1 = r1.layoutManager
            r1.scrollToPositionWithOffset(r3, r4)
        L_0x01e0:
            int r0 = r0 + 1
            goto L_0x01a9
        L_0x01e3:
            r10.showNextSupportedSuggestion()
            org.telegram.ui.DialogsActivity$21 r0 = new org.telegram.ui.DialogsActivity$21
            r0.<init>()
            org.telegram.ui.Components.Bulletin.addDelegate((org.telegram.ui.ActionBar.BaseFragment) r10, (org.telegram.ui.Components.Bulletin.Delegate) r0)
            boolean r0 = r10.searchIsShowed
            if (r0 == 0) goto L_0x01fb
            android.app.Activity r0 = r10.getParentActivity()
            int r1 = r10.classGuid
            org.telegram.messenger.AndroidUtilities.requestAdjustResize(r0, r1)
        L_0x01fb:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.onResume():void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onResume$10 */
    public /* synthetic */ void lambda$onResume$10$DialogsActivity(int i) {
        this.askAboutContacts = i != 0;
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askAboutContacts", this.askAboutContacts).commit();
        askForPermissons(false);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:?, code lost:
        return;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:4:0x000e */
    /* renamed from: lambda$onResume$11 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$onResume$11$DialogsActivity(android.content.DialogInterface r2, int r3) {
        /*
            r1 = this;
            android.content.Intent r2 = org.telegram.messenger.XiaomiUtilities.getPermissionManagerIntent()
            if (r2 == 0) goto L_0x003f
            android.app.Activity r3 = r1.getParentActivity()     // Catch:{ Exception -> 0x000e }
            r3.startActivity(r2)     // Catch:{ Exception -> 0x000e }
            goto L_0x003f
        L_0x000e:
            android.content.Intent r2 = new android.content.Intent     // Catch:{ Exception -> 0x003b }
            java.lang.String r3 = "android.settings.APPLICATION_DETAILS_SETTINGS"
            r2.<init>(r3)     // Catch:{ Exception -> 0x003b }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x003b }
            r3.<init>()     // Catch:{ Exception -> 0x003b }
            java.lang.String r0 = "package:"
            r3.append(r0)     // Catch:{ Exception -> 0x003b }
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x003b }
            java.lang.String r0 = r0.getPackageName()     // Catch:{ Exception -> 0x003b }
            r3.append(r0)     // Catch:{ Exception -> 0x003b }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x003b }
            android.net.Uri r3 = android.net.Uri.parse(r3)     // Catch:{ Exception -> 0x003b }
            r2.setData(r3)     // Catch:{ Exception -> 0x003b }
            android.app.Activity r3 = r1.getParentActivity()     // Catch:{ Exception -> 0x003b }
            r3.startActivity(r2)     // Catch:{ Exception -> 0x003b }
            goto L_0x003f
        L_0x003b:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x003f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.lambda$onResume$11$DialogsActivity(android.content.DialogInterface, int):void");
    }

    public void onPause() {
        super.onPause();
        ActionBarPopupWindow actionBarPopupWindow = this.scrimPopupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.dismiss();
        }
        ChatActivityEnterView chatActivityEnterView = this.commentView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.onResume();
        }
        UndoView[] undoViewArr = this.undoView;
        if (undoViewArr[0] != null) {
            undoViewArr[0].hide(true, 0);
        }
        Bulletin.removeDelegate((BaseFragment) this);
    }

    public boolean onBackPressed() {
        ActionBarPopupWindow actionBarPopupWindow = this.scrimPopupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.dismiss();
            return false;
        }
        FilterTabsView filterTabsView2 = this.filterTabsView;
        if (filterTabsView2 == null || !filterTabsView2.isEditing()) {
            ActionBar actionBar = this.actionBar;
            if (actionBar == null || !actionBar.isActionModeShowed()) {
                FilterTabsView filterTabsView3 = this.filterTabsView;
                if (filterTabsView3 == null || filterTabsView3.getVisibility() != 0 || this.tabsAnimationInProgress || this.filterTabsView.isAnimatingIndicator() || this.filterTabsView.getCurrentTabId() == Integer.MAX_VALUE || this.startedTracking) {
                    ChatActivityEnterView chatActivityEnterView = this.commentView;
                    if (chatActivityEnterView == null || !chatActivityEnterView.isPopupShowing()) {
                        return super.onBackPressed();
                    }
                    this.commentView.hidePopup(true);
                    return false;
                }
                this.filterTabsView.selectFirstTab();
                return false;
            }
            if (this.searchViewPager.getVisibility() == 0) {
                this.searchViewPager.hideActionMode();
            } else {
                hideActionMode(true);
            }
            return false;
        }
        this.filterTabsView.setIsEditing(false);
        showDoneItem(false);
        return false;
    }

    /* access modifiers changed from: protected */
    public void onBecomeFullyHidden() {
        if (this.closeSearchFieldOnHide) {
            ActionBar actionBar = this.actionBar;
            if (actionBar != null) {
                actionBar.closeSearchField();
            }
            TLObject tLObject = this.searchObject;
            if (tLObject != null) {
                this.searchViewPager.dialogsSearchAdapter.putRecentSearch(this.searchDialogId, tLObject);
                this.searchObject = null;
            }
            this.closeSearchFieldOnHide = false;
        }
        FilterTabsView filterTabsView2 = this.filterTabsView;
        if (filterTabsView2 != null && filterTabsView2.getVisibility() == 0) {
            int i = (int) (-this.actionBar.getTranslationY());
            int currentActionBarHeight = ActionBar.getCurrentActionBarHeight();
            if (!(i == 0 || i == currentActionBarHeight)) {
                if (i < currentActionBarHeight / 2) {
                    setScrollY(0.0f);
                } else if (this.viewPages[0].listView.canScrollVertically(1)) {
                    setScrollY((float) (-currentActionBarHeight));
                }
            }
        }
        UndoView[] undoViewArr = this.undoView;
        if (undoViewArr[0] != null) {
            undoViewArr[0].hide(true, 0);
        }
    }

    /* access modifiers changed from: protected */
    public void setInPreviewMode(boolean z) {
        super.setInPreviewMode(z);
        if (!z && this.avatarContainer != null) {
            this.actionBar.setBackground((Drawable) null);
            ((ViewGroup.MarginLayoutParams) this.actionBar.getLayoutParams()).topMargin = 0;
            this.actionBar.removeView(this.avatarContainer);
            this.avatarContainer = null;
            updateFilterTabs(false);
            this.floatingButton.setVisibility(0);
            ContentView contentView = (ContentView) this.fragmentView;
            FragmentContextView fragmentContextView2 = this.fragmentContextView;
            if (fragmentContextView2 != null) {
                contentView.addView(fragmentContextView2);
            }
            FragmentContextView fragmentContextView3 = this.fragmentLocationContextView;
            if (fragmentContextView3 != null) {
                contentView.addView(fragmentContextView3);
            }
        }
    }

    public boolean addOrRemoveSelectedDialog(long j, View view) {
        if (this.selectedDialogs.contains(Long.valueOf(j))) {
            this.selectedDialogs.remove(Long.valueOf(j));
            if (view instanceof DialogCell) {
                ((DialogCell) view).setChecked(false, true);
            }
            return false;
        }
        this.selectedDialogs.add(Long.valueOf(j));
        if (view instanceof DialogCell) {
            ((DialogCell) view).setChecked(true, true);
        }
        return true;
    }

    public void search(String str, boolean z) {
        showSearch(true, z);
        this.actionBar.openSearchField(str, false);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0074  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x007e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showSearch(final boolean r11, boolean r12) {
        /*
            r10 = this;
            int r0 = r10.initialDialogsType
            r1 = 0
            if (r0 == 0) goto L_0x0006
            r12 = 0
        L_0x0006:
            android.animation.AnimatorSet r0 = r10.searchAnimator
            r2 = 0
            if (r0 == 0) goto L_0x0010
            r0.cancel()
            r10.searchAnimator = r2
        L_0x0010:
            android.animation.Animator r0 = r10.tabsAlphaAnimator
            if (r0 == 0) goto L_0x0019
            r0.cancel()
            r10.tabsAlphaAnimator = r2
        L_0x0019:
            r10.searchIsShowed = r11
            r0 = 1110441984(0x42300000, float:44.0)
            r3 = 1
            if (r11 == 0) goto L_0x00f4
            org.telegram.messenger.MessagesController r4 = r10.getMessagesController()
            int r4 = r4.getTotalDialogsCount()
            boolean r5 = r10.onlySelect
            if (r5 != 0) goto L_0x003d
            org.telegram.ui.Components.SearchViewPager r5 = r10.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r5 = r5.dialogsSearchAdapter
            boolean r5 = r5.hasRecentSearch()
            if (r5 == 0) goto L_0x003d
            r5 = 10
            if (r4 > r5) goto L_0x003b
            goto L_0x003d
        L_0x003b:
            r4 = 0
            goto L_0x003e
        L_0x003d:
            r4 = 1
        L_0x003e:
            org.telegram.ui.Components.SearchViewPager r5 = r10.searchViewPager
            r5.showOnlyDialogsAdapter(r4)
            r5 = r4 ^ 1
            r10.whiteActionBar = r5
            android.view.View r5 = r10.fragmentView
            org.telegram.ui.DialogsActivity$ContentView r5 = (org.telegram.ui.DialogsActivity.ContentView) r5
            org.telegram.ui.ViewPagerFixed$TabsView r6 = r10.searchTabsView
            if (r6 != 0) goto L_0x0088
            if (r4 != 0) goto L_0x0088
            org.telegram.ui.Components.SearchViewPager r4 = r10.searchViewPager
            org.telegram.ui.ViewPagerFixed$TabsView r4 = r4.createTabsView()
            r10.searchTabsView = r4
            org.telegram.ui.Adapters.FiltersView r4 = r10.filtersView
            r6 = -1
            if (r4 == 0) goto L_0x0071
            r4 = 0
        L_0x005f:
            int r7 = r5.getChildCount()
            if (r4 >= r7) goto L_0x0071
            android.view.View r7 = r5.getChildAt(r4)
            org.telegram.ui.Adapters.FiltersView r8 = r10.filtersView
            if (r7 != r8) goto L_0x006e
            goto L_0x0072
        L_0x006e:
            int r4 = r4 + 1
            goto L_0x005f
        L_0x0071:
            r4 = -1
        L_0x0072:
            if (r4 <= 0) goto L_0x007e
            org.telegram.ui.ViewPagerFixed$TabsView r7 = r10.searchTabsView
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r0)
            r5.addView(r7, r4, r6)
            goto L_0x009d
        L_0x007e:
            org.telegram.ui.ViewPagerFixed$TabsView r4 = r10.searchTabsView
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r0)
            r5.addView(r4, r6)
            goto L_0x009d
        L_0x0088:
            if (r6 == 0) goto L_0x009d
            if (r4 == 0) goto L_0x009d
            android.view.ViewParent r4 = r6.getParent()
            boolean r5 = r4 instanceof android.view.ViewGroup
            if (r5 == 0) goto L_0x009b
            android.view.ViewGroup r4 = (android.view.ViewGroup) r4
            org.telegram.ui.ViewPagerFixed$TabsView r5 = r10.searchTabsView
            r4.removeView(r5)
        L_0x009b:
            r10.searchTabsView = r2
        L_0x009d:
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r10.searchItem
            org.telegram.ui.Components.EditTextBoldCursor r4 = r4.getSearchField()
            boolean r5 = r10.whiteActionBar
            if (r5 == 0) goto L_0x00c4
            java.lang.String r5 = "windowBackgroundWhiteBlackText"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setTextColor(r5)
            java.lang.String r5 = "player_time"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setHintTextColor(r5)
            java.lang.String r5 = "chat_messagePanelCursor"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setCursorColor(r5)
            goto L_0x00dd
        L_0x00c4:
            java.lang.String r5 = "actionBarDefaultSearch"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setCursorColor(r6)
            java.lang.String r6 = "actionBarDefaultSearchPlaceholder"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r4.setHintTextColor(r6)
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setTextColor(r5)
        L_0x00dd:
            org.telegram.ui.Components.SearchViewPager r4 = r10.searchViewPager
            android.view.View r5 = r10.fragmentView
            org.telegram.ui.DialogsActivity$ContentView r5 = (org.telegram.ui.DialogsActivity.ContentView) r5
            int r5 = r5.getKeyboardHeight()
            r4.setKeyboardHeight(r5)
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r10.parentLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r4 = r4.getDrawerLayoutContainer()
            r4.setAllowOpenDrawerBySwipe(r3)
            goto L_0x0114
        L_0x00f4:
            org.telegram.ui.Components.FilterTabsView r4 = r10.filterTabsView
            if (r4 == 0) goto L_0x0114
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r10.parentLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r4 = r4.getDrawerLayoutContainer()
            org.telegram.ui.DialogsActivity$ViewPage[] r5 = r10.viewPages
            r5 = r5[r1]
            int r5 = r5.selectedType
            org.telegram.ui.Components.FilterTabsView r6 = r10.filterTabsView
            int r6 = r6.getFirstTabId()
            if (r5 != r6) goto L_0x0110
            r5 = 1
            goto L_0x0111
        L_0x0110:
            r5 = 0
        L_0x0111:
            r4.setAllowOpenDrawerBySwipe(r5)
        L_0x0114:
            if (r12 == 0) goto L_0x012a
            org.telegram.ui.Components.SearchViewPager r4 = r10.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r4 = r4.dialogsSearchAdapter
            boolean r4 = r4.hasRecentSearch()
            if (r4 == 0) goto L_0x012a
            android.app.Activity r4 = r10.getParentActivity()
            int r5 = r10.classGuid
            org.telegram.messenger.AndroidUtilities.setAdjustResizeToNothing(r4, r5)
            goto L_0x0133
        L_0x012a:
            android.app.Activity r4 = r10.getParentActivity()
            int r5 = r10.classGuid
            org.telegram.messenger.AndroidUtilities.requestAdjustResize(r4, r5)
        L_0x0133:
            if (r11 != 0) goto L_0x0144
            org.telegram.ui.Components.FilterTabsView r4 = r10.filterTabsView
            if (r4 == 0) goto L_0x0144
            java.lang.Object r4 = r4.getTag()
            if (r4 == 0) goto L_0x0144
            org.telegram.ui.Components.FilterTabsView r4 = r10.filterTabsView
            r4.setVisibility(r1)
        L_0x0144:
            r4 = 1063675494(0x3var_, float:0.9)
            r5 = 0
            r6 = 1065353216(0x3var_, float:1.0)
            if (r12 == 0) goto L_0x031a
            if (r11 == 0) goto L_0x0171
            org.telegram.ui.Components.SearchViewPager r12 = r10.searchViewPager
            r12.setVisibility(r1)
            org.telegram.ui.Components.SearchViewPager r12 = r10.searchViewPager
            r12.reset()
            r10.updateFiltersView(r3, r2, r2, r1)
            org.telegram.ui.ViewPagerFixed$TabsView r12 = r10.searchTabsView
            if (r12 == 0) goto L_0x0167
            r12.hide(r1, r1)
            org.telegram.ui.ViewPagerFixed$TabsView r12 = r10.searchTabsView
            r12.setVisibility(r1)
        L_0x0167:
            org.telegram.ui.ActionBar.ActionBarMenuItem r12 = r10.searchItem
            android.widget.FrameLayout r12 = r12.getSearchContainer()
            r12.setAlpha(r5)
            goto L_0x0183
        L_0x0171:
            org.telegram.ui.DialogsActivity$ViewPage[] r12 = r10.viewPages
            r12 = r12[r1]
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r12 = r12.listView
            r12.setVisibility(r1)
            org.telegram.ui.DialogsActivity$ViewPage[] r12 = r10.viewPages
            r12 = r12[r1]
            r12.setVisibility(r1)
        L_0x0183:
            r10.setDialogsListFrozen(r3)
            org.telegram.ui.DialogsActivity$ViewPage[] r12 = r10.viewPages
            r12 = r12[r1]
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r12 = r12.listView
            r12.setVerticalScrollBarEnabled(r1)
            org.telegram.ui.Components.SearchViewPager r12 = r10.searchViewPager
            java.lang.String r0 = "windowBackgroundWhite"
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            r12.setBackgroundColor(r0)
            android.animation.AnimatorSet r12 = new android.animation.AnimatorSet
            r12.<init>()
            r10.searchAnimator = r12
            java.util.ArrayList r12 = new java.util.ArrayList
            r12.<init>()
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r10.viewPages
            r0 = r0[r1]
            android.util.Property r7 = android.view.View.ALPHA
            float[] r8 = new float[r3]
            if (r11 == 0) goto L_0x01b5
            r9 = 0
            goto L_0x01b7
        L_0x01b5:
            r9 = 1065353216(0x3var_, float:1.0)
        L_0x01b7:
            r8[r1] = r9
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r7, r8)
            r12.add(r0)
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r10.viewPages
            r0 = r0[r1]
            android.util.Property r7 = android.view.View.SCALE_X
            float[] r8 = new float[r3]
            if (r11 == 0) goto L_0x01ce
            r9 = 1063675494(0x3var_, float:0.9)
            goto L_0x01d0
        L_0x01ce:
            r9 = 1065353216(0x3var_, float:1.0)
        L_0x01d0:
            r8[r1] = r9
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r7, r8)
            r12.add(r0)
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r10.viewPages
            r0 = r0[r1]
            android.util.Property r7 = android.view.View.SCALE_Y
            float[] r8 = new float[r3]
            if (r11 == 0) goto L_0x01e4
            goto L_0x01e6
        L_0x01e4:
            r4 = 1065353216(0x3var_, float:1.0)
        L_0x01e6:
            r8[r1] = r4
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r7, r8)
            r12.add(r0)
            org.telegram.ui.Components.SearchViewPager r0 = r10.searchViewPager
            android.util.Property r4 = android.view.View.ALPHA
            float[] r7 = new float[r3]
            if (r11 == 0) goto L_0x01fa
            r8 = 1065353216(0x3var_, float:1.0)
            goto L_0x01fb
        L_0x01fa:
            r8 = 0
        L_0x01fb:
            r7[r1] = r8
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r4, r7)
            r12.add(r0)
            org.telegram.ui.Components.SearchViewPager r0 = r10.searchViewPager
            android.util.Property r4 = android.view.View.SCALE_X
            float[] r7 = new float[r3]
            r8 = 1065772646(0x3var_, float:1.05)
            if (r11 == 0) goto L_0x0212
            r9 = 1065353216(0x3var_, float:1.0)
            goto L_0x0215
        L_0x0212:
            r9 = 1065772646(0x3var_, float:1.05)
        L_0x0215:
            r7[r1] = r9
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r4, r7)
            r12.add(r0)
            org.telegram.ui.Components.SearchViewPager r0 = r10.searchViewPager
            android.util.Property r4 = android.view.View.SCALE_Y
            float[] r7 = new float[r3]
            if (r11 == 0) goto L_0x0228
            r8 = 1065353216(0x3var_, float:1.0)
        L_0x0228:
            r7[r1] = r8
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r4, r7)
            r12.add(r0)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r10.searchItem
            android.widget.ImageView r0 = r0.getIconView()
            android.util.Property r4 = android.view.View.ALPHA
            float[] r7 = new float[r3]
            if (r11 == 0) goto L_0x023f
            r8 = 0
            goto L_0x0241
        L_0x023f:
            r8 = 1065353216(0x3var_, float:1.0)
        L_0x0241:
            r7[r1] = r8
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r4, r7)
            r12.add(r0)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r10.passcodeItem
            if (r0 == 0) goto L_0x0265
            android.widget.ImageView r0 = r0.getIconView()
            android.util.Property r4 = android.view.View.ALPHA
            float[] r7 = new float[r3]
            if (r11 == 0) goto L_0x025a
            r8 = 0
            goto L_0x025c
        L_0x025a:
            r8 = 1065353216(0x3var_, float:1.0)
        L_0x025c:
            r7[r1] = r8
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r4, r7)
            r12.add(r0)
        L_0x0265:
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r10.searchItem
            android.widget.FrameLayout r0 = r0.getSearchContainer()
            android.util.Property r4 = android.view.View.ALPHA
            float[] r7 = new float[r3]
            if (r11 == 0) goto L_0x0274
            r8 = 1065353216(0x3var_, float:1.0)
            goto L_0x0275
        L_0x0274:
            r8 = 0
        L_0x0275:
            r7[r1] = r8
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r4, r7)
            r12.add(r0)
            org.telegram.ui.Components.FilterTabsView r0 = r10.filterTabsView
            if (r0 == 0) goto L_0x02ae
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x02ae
            org.telegram.ui.Components.FilterTabsView r0 = r10.filterTabsView
            org.telegram.ui.Components.RecyclerListView r0 = r0.getTabsContainer()
            android.util.Property r4 = android.view.View.ALPHA
            float[] r7 = new float[r3]
            if (r11 == 0) goto L_0x0296
            r8 = 0
            goto L_0x0298
        L_0x0296:
            r8 = 1065353216(0x3var_, float:1.0)
        L_0x0298:
            r7[r1] = r8
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r4, r7)
            r7 = 100
            android.animation.ObjectAnimator r0 = r0.setDuration(r7)
            r10.tabsAlphaAnimator = r0
            org.telegram.ui.DialogsActivity$22 r4 = new org.telegram.ui.DialogsActivity$22
            r4.<init>()
            r0.addListener(r4)
        L_0x02ae:
            r0 = 2
            float[] r0 = new float[r0]
            float r4 = r10.searchAnimationProgress
            r0[r1] = r4
            if (r11 == 0) goto L_0x02b9
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x02b9:
            r0[r3] = r5
            android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r0)
            org.telegram.ui.-$$Lambda$DialogsActivity$45OcTdZUTTV2reEKNqEj5e_B8Mk r1 = new org.telegram.ui.-$$Lambda$DialogsActivity$45OcTdZUTTV2reEKNqEj5e_B8Mk
            r1.<init>()
            r0.addUpdateListener(r1)
            r12.add(r0)
            android.animation.AnimatorSet r0 = r10.searchAnimator
            r0.playTogether(r12)
            android.animation.AnimatorSet r12 = r10.searchAnimator
            if (r11 == 0) goto L_0x02d6
            r0 = 200(0xc8, double:9.9E-322)
            goto L_0x02d8
        L_0x02d6:
            r0 = 180(0xb4, double:8.9E-322)
        L_0x02d8:
            r12.setDuration(r0)
            android.animation.AnimatorSet r12 = r10.searchAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r0 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
            r12.setInterpolator(r0)
            if (r11 != 0) goto L_0x02f4
            android.animation.AnimatorSet r12 = r10.searchAnimator
            r0 = 20
            r12.setStartDelay(r0)
            android.animation.Animator r12 = r10.tabsAlphaAnimator
            if (r12 == 0) goto L_0x02f4
            r0 = 80
            r12.setStartDelay(r0)
        L_0x02f4:
            android.animation.AnimatorSet r12 = r10.searchAnimator
            org.telegram.ui.DialogsActivity$23 r0 = new org.telegram.ui.DialogsActivity$23
            r0.<init>(r11)
            r12.addListener(r0)
            int r11 = r10.currentAccount
            org.telegram.messenger.NotificationCenter r11 = org.telegram.messenger.NotificationCenter.getInstance(r11)
            int r12 = r10.animationIndex
            int r11 = r11.setAnimationInProgress(r12, r2)
            r10.animationIndex = r11
            android.animation.AnimatorSet r11 = r10.searchAnimator
            r11.start()
            android.animation.Animator r11 = r10.tabsAlphaAnimator
            if (r11 == 0) goto L_0x03f3
            r11.start()
            goto L_0x03f3
        L_0x031a:
            r10.setDialogsListFrozen(r1)
            if (r11 == 0) goto L_0x032b
            org.telegram.ui.DialogsActivity$ViewPage[] r12 = r10.viewPages
            r12 = r12[r1]
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r12 = r12.listView
            r12.hide()
            goto L_0x0336
        L_0x032b:
            org.telegram.ui.DialogsActivity$ViewPage[] r12 = r10.viewPages
            r12 = r12[r1]
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r12 = r12.listView
            r12.show()
        L_0x0336:
            org.telegram.ui.DialogsActivity$ViewPage[] r12 = r10.viewPages
            r12 = r12[r1]
            if (r11 == 0) goto L_0x033e
            r2 = 0
            goto L_0x0340
        L_0x033e:
            r2 = 1065353216(0x3var_, float:1.0)
        L_0x0340:
            r12.setAlpha(r2)
            org.telegram.ui.DialogsActivity$ViewPage[] r12 = r10.viewPages
            r12 = r12[r1]
            if (r11 == 0) goto L_0x034d
            r2 = 1063675494(0x3var_, float:0.9)
            goto L_0x034f
        L_0x034d:
            r2 = 1065353216(0x3var_, float:1.0)
        L_0x034f:
            r12.setScaleX(r2)
            org.telegram.ui.DialogsActivity$ViewPage[] r12 = r10.viewPages
            r12 = r12[r1]
            if (r11 == 0) goto L_0x0359
            goto L_0x035b
        L_0x0359:
            r4 = 1065353216(0x3var_, float:1.0)
        L_0x035b:
            r12.setScaleY(r4)
            org.telegram.ui.Components.SearchViewPager r12 = r10.searchViewPager
            if (r11 == 0) goto L_0x0365
            r2 = 1065353216(0x3var_, float:1.0)
            goto L_0x0366
        L_0x0365:
            r2 = 0
        L_0x0366:
            r12.setAlpha(r2)
            org.telegram.ui.Adapters.FiltersView r12 = r10.filtersView
            if (r11 == 0) goto L_0x0370
            r2 = 1065353216(0x3var_, float:1.0)
            goto L_0x0371
        L_0x0370:
            r2 = 0
        L_0x0371:
            r12.setAlpha(r2)
            org.telegram.ui.Components.SearchViewPager r12 = r10.searchViewPager
            r2 = 1066192077(0x3f8ccccd, float:1.1)
            if (r11 == 0) goto L_0x037e
            r3 = 1065353216(0x3var_, float:1.0)
            goto L_0x0381
        L_0x037e:
            r3 = 1066192077(0x3f8ccccd, float:1.1)
        L_0x0381:
            r12.setScaleX(r3)
            org.telegram.ui.Components.SearchViewPager r12 = r10.searchViewPager
            if (r11 == 0) goto L_0x038a
            r2 = 1065353216(0x3var_, float:1.0)
        L_0x038a:
            r12.setScaleY(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r12 = r10.searchItem
            android.widget.FrameLayout r12 = r12.getSearchContainer()
            if (r11 == 0) goto L_0x0398
            r2 = 1065353216(0x3var_, float:1.0)
            goto L_0x0399
        L_0x0398:
            r2 = 0
        L_0x0399:
            r12.setAlpha(r2)
            org.telegram.ui.Components.FilterTabsView r12 = r10.filterTabsView
            if (r12 == 0) goto L_0x03c4
            int r12 = r12.getVisibility()
            if (r12 != 0) goto L_0x03c4
            org.telegram.ui.Components.FilterTabsView r12 = r10.filterTabsView
            if (r11 == 0) goto L_0x03b1
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = -r0
            float r0 = (float) r0
            goto L_0x03b2
        L_0x03b1:
            r0 = 0
        L_0x03b2:
            r12.setTranslationY(r0)
            org.telegram.ui.Components.FilterTabsView r12 = r10.filterTabsView
            org.telegram.ui.Components.RecyclerListView r12 = r12.getTabsContainer()
            if (r11 == 0) goto L_0x03bf
            r0 = 0
            goto L_0x03c1
        L_0x03bf:
            r0 = 1065353216(0x3var_, float:1.0)
        L_0x03c1:
            r12.setAlpha(r0)
        L_0x03c4:
            org.telegram.ui.Components.FilterTabsView r12 = r10.filterTabsView
            r0 = 8
            if (r12 == 0) goto L_0x03dd
            java.lang.Object r12 = r12.getTag()
            if (r12 == 0) goto L_0x03d8
            if (r11 != 0) goto L_0x03d8
            org.telegram.ui.Components.FilterTabsView r12 = r10.filterTabsView
            r12.setVisibility(r1)
            goto L_0x03dd
        L_0x03d8:
            org.telegram.ui.Components.FilterTabsView r12 = r10.filterTabsView
            r12.setVisibility(r0)
        L_0x03dd:
            org.telegram.ui.Components.SearchViewPager r12 = r10.searchViewPager
            if (r11 == 0) goto L_0x03e2
            goto L_0x03e4
        L_0x03e2:
            r1 = 8
        L_0x03e4:
            r12.setVisibility(r1)
            if (r11 == 0) goto L_0x03eb
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x03eb:
            r10.setSearchAnimationProgress(r5)
            android.view.View r11 = r10.fragmentView
            r11.invalidate()
        L_0x03f3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.showSearch(boolean, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showSearch$13 */
    public /* synthetic */ void lambda$showSearch$13$DialogsActivity(ValueAnimator valueAnimator) {
        setSearchAnimationProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    private void setSearchAnimationProgress(float f) {
        this.searchAnimationProgress = f;
        if (this.whiteActionBar) {
            this.actionBar.setItemsColor(ColorUtils.blendARGB(Theme.getColor(this.folderId != 0 ? "actionBarDefaultArchivedIcon" : "actionBarDefaultIcon"), Theme.getColor("windowBackgroundWhiteGrayText2"), this.searchAnimationProgress), false);
            this.actionBar.setItemsColor(ColorUtils.blendARGB(Theme.getColor("actionBarActionModeDefaultIcon"), Theme.getColor("windowBackgroundWhiteGrayText2"), this.searchAnimationProgress), true);
            this.actionBar.setItemsBackgroundColor(ColorUtils.blendARGB(Theme.getColor(this.folderId != 0 ? "actionBarDefaultArchivedSelector" : "actionBarDefaultSelector"), Theme.getColor("actionBarActionModeDefaultSelector"), this.searchAnimationProgress), false);
        }
        View view = this.fragmentView;
        if (view != null) {
            view.invalidate();
        }
        updateContextViewPosition();
    }

    /* access modifiers changed from: private */
    public void findAndUpdateCheckBox(long j, boolean z) {
        if (this.viewPages != null) {
            int i = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (i < viewPageArr.length) {
                    int childCount = viewPageArr[i].listView.getChildCount();
                    int i2 = 0;
                    while (true) {
                        if (i2 >= childCount) {
                            break;
                        }
                        View childAt = this.viewPages[i].listView.getChildAt(i2);
                        if (childAt instanceof DialogCell) {
                            DialogCell dialogCell = (DialogCell) childAt;
                            if (dialogCell.getDialogId() == j) {
                                dialogCell.setChecked(z, true);
                                break;
                            }
                        }
                        i2++;
                    }
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0110  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0113  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x011a A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void checkListLoad(org.telegram.ui.DialogsActivity.ViewPage r14) {
        /*
            r13 = this;
            boolean r0 = r13.tabsAnimationInProgress
            if (r0 != 0) goto L_0x0126
            boolean r0 = r13.startedTracking
            if (r0 != 0) goto L_0x0126
            org.telegram.ui.Components.FilterTabsView r0 = r13.filterTabsView
            if (r0 == 0) goto L_0x001c
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x001c
            org.telegram.ui.Components.FilterTabsView r0 = r13.filterTabsView
            boolean r0 = r0.isAnimatingIndicator()
            if (r0 == 0) goto L_0x001c
            goto L_0x0126
        L_0x001c:
            androidx.recyclerview.widget.LinearLayoutManager r0 = r14.layoutManager
            int r0 = r0.findFirstVisibleItemPosition()
            androidx.recyclerview.widget.LinearLayoutManager r1 = r14.layoutManager
            int r1 = r1.findLastVisibleItemPosition()
            int r1 = r1 - r0
            int r0 = java.lang.Math.abs(r1)
            r1 = 1
            int r0 = r0 + r1
            int r2 = r14.dialogsType
            r3 = 8
            r4 = 7
            r5 = 0
            if (r2 == r4) goto L_0x0043
            int r2 = r14.dialogsType
            if (r2 != r3) goto L_0x00b8
        L_0x0043:
            org.telegram.messenger.MessagesController r2 = r13.getMessagesController()
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            int r6 = r14.selectedType
            if (r6 < 0) goto L_0x00b8
            int r6 = r14.selectedType
            int r2 = r2.size()
            if (r6 >= r2) goto L_0x00b8
            org.telegram.messenger.MessagesController r2 = r13.getMessagesController()
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
            int r6 = r14.selectedType
            java.lang.Object r2 = r2.get(r6)
            org.telegram.messenger.MessagesController$DialogFilter r2 = (org.telegram.messenger.MessagesController.DialogFilter) r2
            int r2 = r2.flags
            int r6 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED
            r2 = r2 & r6
            if (r2 != 0) goto L_0x00b8
            if (r0 <= 0) goto L_0x008e
            androidx.recyclerview.widget.LinearLayoutManager r2 = r14.layoutManager
            int r2 = r2.findLastVisibleItemPosition()
            int r6 = r13.currentAccount
            int r7 = r14.dialogsType
            boolean r8 = r13.dialogsListFrozen
            java.util.ArrayList r6 = getDialogsArray(r6, r7, r1, r8)
            int r6 = r6.size()
            int r6 = r6 + -10
            if (r2 >= r6) goto L_0x009c
        L_0x008e:
            if (r0 != 0) goto L_0x00b8
            int r2 = r13.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            boolean r2 = r2.isDialogsEndReached(r1)
            if (r2 != 0) goto L_0x00b8
        L_0x009c:
            org.telegram.messenger.MessagesController r2 = r13.getMessagesController()
            boolean r2 = r2.isDialogsEndReached(r1)
            r2 = r2 ^ r1
            if (r2 != 0) goto L_0x00b5
            org.telegram.messenger.MessagesController r6 = r13.getMessagesController()
            boolean r6 = r6.isServerDialogsEndReached(r1)
            if (r6 != 0) goto L_0x00b2
            goto L_0x00b5
        L_0x00b2:
            r12 = r2
            r11 = 0
            goto L_0x00ba
        L_0x00b5:
            r12 = r2
            r11 = 1
            goto L_0x00ba
        L_0x00b8:
            r11 = 0
            r12 = 0
        L_0x00ba:
            if (r0 <= 0) goto L_0x00da
            androidx.recyclerview.widget.LinearLayoutManager r2 = r14.layoutManager
            int r2 = r2.findLastVisibleItemPosition()
            int r6 = r13.currentAccount
            int r7 = r14.dialogsType
            int r8 = r13.folderId
            boolean r9 = r13.dialogsListFrozen
            java.util.ArrayList r6 = getDialogsArray(r6, r7, r8, r9)
            int r6 = r6.size()
            int r6 = r6 + -10
            if (r2 >= r6) goto L_0x00f6
        L_0x00da:
            if (r0 != 0) goto L_0x0116
            int r0 = r14.dialogsType
            if (r0 == r4) goto L_0x00e8
            int r14 = r14.dialogsType
            if (r14 != r3) goto L_0x0116
        L_0x00e8:
            int r14 = r13.currentAccount
            org.telegram.messenger.MessagesController r14 = org.telegram.messenger.MessagesController.getInstance(r14)
            int r0 = r13.folderId
            boolean r14 = r14.isDialogsEndReached(r0)
            if (r14 != 0) goto L_0x0116
        L_0x00f6:
            org.telegram.messenger.MessagesController r14 = r13.getMessagesController()
            int r0 = r13.folderId
            boolean r14 = r14.isDialogsEndReached(r0)
            r14 = r14 ^ r1
            if (r14 != 0) goto L_0x0113
            org.telegram.messenger.MessagesController r0 = r13.getMessagesController()
            int r2 = r13.folderId
            boolean r0 = r0.isServerDialogsEndReached(r2)
            if (r0 != 0) goto L_0x0110
            goto L_0x0113
        L_0x0110:
            r10 = r14
            r9 = 0
            goto L_0x0118
        L_0x0113:
            r10 = r14
            r9 = 1
            goto L_0x0118
        L_0x0116:
            r9 = 0
            r10 = 0
        L_0x0118:
            if (r9 != 0) goto L_0x011c
            if (r11 == 0) goto L_0x0126
        L_0x011c:
            org.telegram.ui.-$$Lambda$DialogsActivity$JQLuopKDtJrBpjx-kT8XfkS-Jd4 r14 = new org.telegram.ui.-$$Lambda$DialogsActivity$JQLuopKDtJrBpjx-kT8XfkS-Jd4
            r7 = r14
            r8 = r13
            r7.<init>(r9, r10, r11, r12)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r14)
        L_0x0126:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.checkListLoad(org.telegram.ui.DialogsActivity$ViewPage):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkListLoad$14 */
    public /* synthetic */ void lambda$checkListLoad$14$DialogsActivity(boolean z, boolean z2, boolean z3, boolean z4) {
        if (z) {
            getMessagesController().loadDialogs(this.folderId, -1, 100, z2);
        }
        if (z3) {
            getMessagesController().loadDialogs(1, -1, 100, z4);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:92:0x017a A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x017b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void onItemClick(android.view.View r11, int r12, androidx.recyclerview.widget.RecyclerView.Adapter r13) {
        /*
            r10 = this;
            android.app.Activity r0 = r10.getParentActivity()
            if (r0 != 0) goto L_0x0007
            return
        L_0x0007:
            boolean r0 = r13 instanceof org.telegram.ui.Adapters.DialogsAdapter
            r1 = 32
            r2 = 0
            r4 = 1
            r5 = 0
            if (r0 == 0) goto L_0x00e3
            r0 = r13
            org.telegram.ui.Adapters.DialogsAdapter r0 = (org.telegram.ui.Adapters.DialogsAdapter) r0
            org.telegram.tgnet.TLObject r12 = r0.getItem(r12)
            boolean r0 = r12 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x0023
            org.telegram.tgnet.TLRPC$User r12 = (org.telegram.tgnet.TLRPC$User) r12
            int r12 = r12.id
        L_0x0020:
            long r6 = (long) r12
            goto L_0x0174
        L_0x0023:
            boolean r0 = r12 instanceof org.telegram.tgnet.TLRPC$Dialog
            if (r0 == 0) goto L_0x005d
            org.telegram.tgnet.TLRPC$Dialog r12 = (org.telegram.tgnet.TLRPC$Dialog) r12
            boolean r0 = r12 instanceof org.telegram.tgnet.TLRPC$TL_dialogFolder
            if (r0 == 0) goto L_0x004f
            org.telegram.ui.ActionBar.ActionBar r11 = r10.actionBar
            boolean r11 = r11.isActionModeShowed()
            if (r11 == 0) goto L_0x0036
            return
        L_0x0036:
            org.telegram.tgnet.TLRPC$TL_dialogFolder r12 = (org.telegram.tgnet.TLRPC$TL_dialogFolder) r12
            android.os.Bundle r11 = new android.os.Bundle
            r11.<init>()
            org.telegram.tgnet.TLRPC$TL_folder r12 = r12.folder
            int r12 = r12.id
            java.lang.String r13 = "folderId"
            r11.putInt(r13, r12)
            org.telegram.ui.DialogsActivity r12 = new org.telegram.ui.DialogsActivity
            r12.<init>(r11)
            r10.presentFragment(r12)
            return
        L_0x004f:
            long r6 = r12.id
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            boolean r0 = r0.isActionModeShowed()
            if (r0 == 0) goto L_0x0174
            r10.showOrUpdateActionMode(r12, r11)
            return
        L_0x005d:
            boolean r0 = r12 instanceof org.telegram.tgnet.TLRPC$TL_recentMeUrlChat
            if (r0 == 0) goto L_0x0067
            org.telegram.tgnet.TLRPC$TL_recentMeUrlChat r12 = (org.telegram.tgnet.TLRPC$TL_recentMeUrlChat) r12
            int r12 = r12.chat_id
        L_0x0065:
            int r12 = -r12
            goto L_0x0020
        L_0x0067:
            boolean r0 = r12 instanceof org.telegram.tgnet.TLRPC$TL_recentMeUrlUser
            if (r0 == 0) goto L_0x0070
            org.telegram.tgnet.TLRPC$TL_recentMeUrlUser r12 = (org.telegram.tgnet.TLRPC$TL_recentMeUrlUser) r12
            int r12 = r12.user_id
            goto L_0x0020
        L_0x0070:
            boolean r0 = r12 instanceof org.telegram.tgnet.TLRPC$TL_recentMeUrlChatInvite
            if (r0 == 0) goto L_0x00b6
            org.telegram.tgnet.TLRPC$TL_recentMeUrlChatInvite r12 = (org.telegram.tgnet.TLRPC$TL_recentMeUrlChatInvite) r12
            org.telegram.tgnet.TLRPC$ChatInvite r0 = r12.chat_invite
            org.telegram.tgnet.TLRPC$Chat r6 = r0.chat
            if (r6 != 0) goto L_0x0084
            boolean r7 = r0.channel
            if (r7 == 0) goto L_0x0092
            boolean r7 = r0.megagroup
            if (r7 != 0) goto L_0x0092
        L_0x0084:
            if (r6 == 0) goto L_0x00ae
            boolean r6 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r6 == 0) goto L_0x0092
            org.telegram.tgnet.TLRPC$Chat r6 = r0.chat
            boolean r6 = r6.megagroup
            if (r6 == 0) goto L_0x00ae
        L_0x0092:
            java.lang.String r11 = r12.url
            r12 = 47
            int r12 = r11.indexOf(r12)
            if (r12 <= 0) goto L_0x00a1
            int r12 = r12 + r4
            java.lang.String r11 = r11.substring(r12)
        L_0x00a1:
            org.telegram.ui.Components.JoinGroupAlert r12 = new org.telegram.ui.Components.JoinGroupAlert
            android.app.Activity r13 = r10.getParentActivity()
            r12.<init>(r13, r0, r11, r10)
            r10.showDialog(r12)
            return
        L_0x00ae:
            org.telegram.tgnet.TLRPC$Chat r12 = r0.chat
            if (r12 == 0) goto L_0x00b5
            int r12 = r12.id
            goto L_0x0065
        L_0x00b5:
            return
        L_0x00b6:
            boolean r11 = r12 instanceof org.telegram.tgnet.TLRPC$TL_recentMeUrlStickerSet
            if (r11 == 0) goto L_0x00de
            org.telegram.tgnet.TLRPC$TL_recentMeUrlStickerSet r12 = (org.telegram.tgnet.TLRPC$TL_recentMeUrlStickerSet) r12
            org.telegram.tgnet.TLRPC$StickerSetCovered r11 = r12.set
            org.telegram.tgnet.TLRPC$StickerSet r11 = r11.set
            org.telegram.tgnet.TLRPC$TL_inputStickerSetID r3 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetID
            r3.<init>()
            long r12 = r11.id
            r3.id = r12
            long r11 = r11.access_hash
            r3.access_hash = r11
            org.telegram.ui.Components.StickersAlert r11 = new org.telegram.ui.Components.StickersAlert
            android.app.Activity r1 = r10.getParentActivity()
            r4 = 0
            r5 = 0
            r0 = r11
            r2 = r10
            r0.<init>(r1, r2, r3, r4, r5)
            r10.showDialog(r11)
            return
        L_0x00de:
            boolean r11 = r12 instanceof org.telegram.tgnet.TLRPC$TL_recentMeUrlUnknown
            if (r11 == 0) goto L_0x00e2
        L_0x00e2:
            return
        L_0x00e3:
            org.telegram.ui.Components.SearchViewPager r0 = r10.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r0 = r0.dialogsSearchAdapter
            if (r13 != r0) goto L_0x0173
            java.lang.Object r0 = r0.getItem(r12)
            org.telegram.ui.Components.SearchViewPager r6 = r10.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r6 = r6.dialogsSearchAdapter
            boolean r12 = r6.isGlobalSearch(r12)
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$User
            if (r6 == 0) goto L_0x0108
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            int r6 = r0.id
            long r6 = (long) r6
            boolean r8 = r10.onlySelect
            if (r8 != 0) goto L_0x0175
            r10.searchDialogId = r6
            r10.searchObject = r0
            goto L_0x0175
        L_0x0108:
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r6 == 0) goto L_0x011b
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            int r6 = r0.id
            int r6 = -r6
            long r6 = (long) r6
            boolean r8 = r10.onlySelect
            if (r8 != 0) goto L_0x0175
            r10.searchDialogId = r6
            r10.searchObject = r0
            goto L_0x0175
        L_0x011b:
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$EncryptedChat
            if (r6 == 0) goto L_0x012e
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = (org.telegram.tgnet.TLRPC$EncryptedChat) r0
            int r6 = r0.id
            long r6 = (long) r6
            long r6 = r6 << r1
            boolean r8 = r10.onlySelect
            if (r8 != 0) goto L_0x0175
            r10.searchDialogId = r6
            r10.searchObject = r0
            goto L_0x0175
        L_0x012e:
            boolean r6 = r0 instanceof org.telegram.messenger.MessageObject
            if (r6 == 0) goto L_0x0148
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            long r6 = r0.getDialogId()
            int r0 = r0.getId()
            org.telegram.ui.Components.SearchViewPager r8 = r10.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r8 = r8.dialogsSearchAdapter
            java.lang.String r9 = r8.getLastSearchString()
            r8.addHashtagsFromMessage(r9)
            goto L_0x0176
        L_0x0148:
            boolean r6 = r0 instanceof java.lang.String
            if (r6 == 0) goto L_0x0171
            java.lang.String r0 = (java.lang.String) r0
            org.telegram.ui.Components.SearchViewPager r6 = r10.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r6 = r6.dialogsSearchAdapter
            boolean r6 = r6.isHashtagSearch()
            if (r6 == 0) goto L_0x015e
            org.telegram.ui.ActionBar.ActionBar r6 = r10.actionBar
            r6.openSearchField(r0, r5)
            goto L_0x0171
        L_0x015e:
            java.lang.String r6 = "section"
            boolean r6 = r0.equals(r6)
            if (r6 != 0) goto L_0x0171
            org.telegram.ui.NewContactActivity r6 = new org.telegram.ui.NewContactActivity
            r6.<init>()
            r6.setInitialPhoneNumber(r0, r4)
            r10.presentFragment(r6)
        L_0x0171:
            r6 = r2
            goto L_0x0175
        L_0x0173:
            r6 = r2
        L_0x0174:
            r12 = 0
        L_0x0175:
            r0 = 0
        L_0x0176:
            int r8 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r8 != 0) goto L_0x017b
            return
        L_0x017b:
            boolean r2 = r10.onlySelect
            if (r2 == 0) goto L_0x01aa
            boolean r12 = r10.validateSlowModeDialog(r6)
            if (r12 != 0) goto L_0x0186
            return
        L_0x0186:
            java.util.ArrayList<java.lang.Long> r12 = r10.selectedDialogs
            boolean r12 = r12.isEmpty()
            if (r12 != 0) goto L_0x01a5
            boolean r11 = r10.addOrRemoveSelectedDialog(r6, r11)
            org.telegram.ui.Components.SearchViewPager r12 = r10.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r12 = r12.dialogsSearchAdapter
            if (r13 != r12) goto L_0x01a0
            org.telegram.ui.ActionBar.ActionBar r12 = r10.actionBar
            r12.closeSearchField()
            r10.findAndUpdateCheckBox(r6, r11)
        L_0x01a0:
            r10.updateSelectedCount()
            goto L_0x027a
        L_0x01a5:
            r10.didSelectResult(r6, r4, r5)
            goto L_0x027a
        L_0x01aa:
            android.os.Bundle r11 = new android.os.Bundle
            r11.<init>()
            int r2 = (int) r6
            long r3 = r6 >> r1
            int r1 = (int) r3
            if (r2 == 0) goto L_0x01e6
            if (r2 <= 0) goto L_0x01be
            java.lang.String r1 = "user_id"
            r11.putInt(r1, r2)
            goto L_0x01eb
        L_0x01be:
            if (r2 >= 0) goto L_0x01eb
            if (r0 == 0) goto L_0x01df
            org.telegram.messenger.MessagesController r1 = r10.getMessagesController()
            int r3 = -r2
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r3)
            if (r1 == 0) goto L_0x01df
            org.telegram.tgnet.TLRPC$InputChannel r3 = r1.migrated_to
            if (r3 == 0) goto L_0x01df
            java.lang.String r3 = "migrated_to"
            r11.putInt(r3, r2)
            org.telegram.tgnet.TLRPC$InputChannel r1 = r1.migrated_to
            int r1 = r1.channel_id
            int r2 = -r1
        L_0x01df:
            int r1 = -r2
            java.lang.String r2 = "chat_id"
            r11.putInt(r2, r1)
            goto L_0x01eb
        L_0x01e6:
            java.lang.String r2 = "enc_id"
            r11.putInt(r2, r1)
        L_0x01eb:
            if (r0 == 0) goto L_0x01f3
            java.lang.String r12 = "message_id"
            r11.putInt(r12, r0)
            goto L_0x0209
        L_0x01f3:
            if (r12 != 0) goto L_0x01f9
            r10.closeSearch()
            goto L_0x0209
        L_0x01f9:
            org.telegram.tgnet.TLObject r12 = r10.searchObject
            if (r12 == 0) goto L_0x0209
            org.telegram.ui.Components.SearchViewPager r0 = r10.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r0 = r0.dialogsSearchAdapter
            long r1 = r10.searchDialogId
            r0.putRecentSearch(r1, r12)
            r12 = 0
            r10.searchObject = r12
        L_0x0209:
            boolean r12 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r12 == 0) goto L_0x0239
            long r0 = r10.openedDialogId
            int r12 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r12 != 0) goto L_0x021c
            org.telegram.ui.Components.SearchViewPager r12 = r10.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r12 = r12.dialogsSearchAdapter
            if (r13 == r12) goto L_0x021c
            return
        L_0x021c:
            org.telegram.ui.DialogsActivity$ViewPage[] r12 = r10.viewPages
            if (r12 == 0) goto L_0x0234
            r12 = 0
        L_0x0221:
            org.telegram.ui.DialogsActivity$ViewPage[] r13 = r10.viewPages
            int r0 = r13.length
            if (r12 >= r0) goto L_0x0234
            r13 = r13[r12]
            org.telegram.ui.Adapters.DialogsAdapter r13 = r13.dialogsAdapter
            r10.openedDialogId = r6
            r13.setOpenedDialogId(r6)
            int r12 = r12 + 1
            goto L_0x0221
        L_0x0234:
            r12 = 512(0x200, float:7.175E-43)
            r10.updateVisibleRows(r12)
        L_0x0239:
            org.telegram.ui.Components.SearchViewPager r12 = r10.searchViewPager
            boolean r12 = r12.actionModeShowing()
            if (r12 == 0) goto L_0x0246
            org.telegram.ui.Components.SearchViewPager r12 = r10.searchViewPager
            r12.hideActionMode()
        L_0x0246:
            java.lang.String r12 = r10.searchString
            if (r12 == 0) goto L_0x0268
            org.telegram.messenger.MessagesController r12 = r10.getMessagesController()
            boolean r12 = r12.checkCanOpenChat(r11, r10)
            if (r12 == 0) goto L_0x027a
            org.telegram.messenger.NotificationCenter r12 = r10.getNotificationCenter()
            int r13 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r0 = new java.lang.Object[r5]
            r12.postNotificationName(r13, r0)
            org.telegram.ui.ChatActivity r12 = new org.telegram.ui.ChatActivity
            r12.<init>(r11)
            r10.presentFragment(r12)
            goto L_0x027a
        L_0x0268:
            org.telegram.messenger.MessagesController r12 = r10.getMessagesController()
            boolean r12 = r12.checkCanOpenChat(r11, r10)
            if (r12 == 0) goto L_0x027a
            org.telegram.ui.ChatActivity r12 = new org.telegram.ui.ChatActivity
            r12.<init>(r11)
            r10.presentFragment(r12)
        L_0x027a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.onItemClick(android.view.View, int, androidx.recyclerview.widget.RecyclerView$Adapter):void");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0112  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onItemLongClick(android.view.View r6, int r7, float r8, float r9, int r10, androidx.recyclerview.widget.RecyclerView.Adapter r11) {
        /*
            r5 = this;
            android.app.Activity r0 = r5.getParentActivity()
            r1 = 0
            if (r0 != 0) goto L_0x0008
            return r1
        L_0x0008:
            org.telegram.ui.ActionBar.ActionBar r0 = r5.actionBar
            boolean r0 = r0.isActionModeShowed()
            if (r0 != 0) goto L_0x002c
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 != 0) goto L_0x002c
            boolean r0 = r5.onlySelect
            if (r0 != 0) goto L_0x002c
            boolean r0 = r6 instanceof org.telegram.ui.Cells.DialogCell
            if (r0 == 0) goto L_0x002c
            r0 = r6
            org.telegram.ui.Cells.DialogCell r0 = (org.telegram.ui.Cells.DialogCell) r0
            boolean r8 = r0.isPointInsideAvatar(r8, r9)
            if (r8 == 0) goto L_0x002c
            boolean r6 = r5.showChatPreview(r0)
            return r6
        L_0x002c:
            org.telegram.ui.Components.SearchViewPager r8 = r5.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r8 = r8.dialogsSearchAdapter
            r9 = 0
            r0 = 1
            if (r11 != r8) goto L_0x011d
            java.lang.Object r6 = r8.getItem(r7)
            org.telegram.ui.Components.SearchViewPager r7 = r5.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r7 = r7.dialogsSearchAdapter
            boolean r7 = r7.isRecentSearchDisplayed()
            if (r7 == 0) goto L_0x011c
            org.telegram.ui.ActionBar.AlertDialog$Builder r7 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r8 = r5.getParentActivity()
            r7.<init>((android.content.Context) r8)
            r8 = 2131624838(0x7f0e0386, float:1.8876867E38)
            java.lang.String r10 = "ClearSearchSingleAlertTitle"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
            r7.setTitle(r8)
            boolean r8 = r6 instanceof org.telegram.tgnet.TLRPC$Chat
            r10 = 2131624839(0x7f0e0387, float:1.887687E38)
            java.lang.String r11 = "ClearSearchSingleChatAlertText"
            if (r8 == 0) goto L_0x0074
            org.telegram.tgnet.TLRPC$Chat r6 = (org.telegram.tgnet.TLRPC$Chat) r6
            java.lang.Object[] r8 = new java.lang.Object[r0]
            java.lang.String r2 = r6.title
            r8[r1] = r2
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatString(r11, r10, r8)
            r7.setMessage(r8)
            int r6 = r6.id
            int r6 = -r6
        L_0x0072:
            long r10 = (long) r6
            goto L_0x00e1
        L_0x0074:
            boolean r8 = r6 instanceof org.telegram.tgnet.TLRPC$User
            r2 = 2131624840(0x7f0e0388, float:1.8876871E38)
            java.lang.String r3 = "ClearSearchSingleUserAlertText"
            if (r8 == 0) goto L_0x00b4
            org.telegram.tgnet.TLRPC$User r6 = (org.telegram.tgnet.TLRPC$User) r6
            int r8 = r6.id
            org.telegram.messenger.UserConfig r4 = r5.getUserConfig()
            int r4 = r4.clientUserId
            if (r8 != r4) goto L_0x009e
            java.lang.Object[] r8 = new java.lang.Object[r0]
            r2 = 2131626938(0x7f0e0bba, float:1.8881126E38)
            java.lang.String r3 = "SavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r8[r1] = r2
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatString(r11, r10, r8)
            r7.setMessage(r8)
            goto L_0x00b1
        L_0x009e:
            java.lang.Object[] r8 = new java.lang.Object[r0]
            java.lang.String r10 = r6.first_name
            java.lang.String r11 = r6.last_name
            java.lang.String r10 = org.telegram.messenger.ContactsController.formatName(r10, r11)
            r8[r1] = r10
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatString(r3, r2, r8)
            r7.setMessage(r8)
        L_0x00b1:
            int r6 = r6.id
            goto L_0x0072
        L_0x00b4:
            boolean r8 = r6 instanceof org.telegram.tgnet.TLRPC$EncryptedChat
            if (r8 == 0) goto L_0x011c
            org.telegram.tgnet.TLRPC$EncryptedChat r6 = (org.telegram.tgnet.TLRPC$EncryptedChat) r6
            org.telegram.messenger.MessagesController r8 = r5.getMessagesController()
            int r10 = r6.user_id
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r8 = r8.getUser(r10)
            java.lang.Object[] r10 = new java.lang.Object[r0]
            java.lang.String r11 = r8.first_name
            java.lang.String r8 = r8.last_name
            java.lang.String r8 = org.telegram.messenger.ContactsController.formatName(r11, r8)
            r10[r1] = r8
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatString(r3, r2, r10)
            r7.setMessage(r8)
            int r6 = r6.id
            long r10 = (long) r6
            r6 = 32
            long r10 = r10 << r6
        L_0x00e1:
            r6 = 2131624837(0x7f0e0385, float:1.8876865E38)
            java.lang.String r8 = "ClearSearchRemove"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r8, r6)
            java.lang.String r6 = r6.toUpperCase()
            org.telegram.ui.-$$Lambda$DialogsActivity$tVqynFYHwwRGlwLbDtMo96t0YTw r8 = new org.telegram.ui.-$$Lambda$DialogsActivity$tVqynFYHwwRGlwLbDtMo96t0YTw
            r8.<init>(r10)
            r7.setPositiveButton(r6, r8)
            r6 = 2131624575(0x7f0e027f, float:1.8876334E38)
            java.lang.String r8 = "Cancel"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r8, r6)
            r7.setNegativeButton(r6, r9)
            org.telegram.ui.ActionBar.AlertDialog r6 = r7.create()
            r5.showDialog(r6)
            r7 = -1
            android.view.View r6 = r6.getButton(r7)
            android.widget.TextView r6 = (android.widget.TextView) r6
            if (r6 == 0) goto L_0x011b
            java.lang.String r7 = "dialogTextRed2"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r6.setTextColor(r7)
        L_0x011b:
            return r0
        L_0x011c:
            return r1
        L_0x011d:
            org.telegram.ui.ActionBar.ActionBar r8 = r5.actionBar
            boolean r8 = r8.isSearchFieldVisible()
            if (r8 == 0) goto L_0x0126
            return r1
        L_0x0126:
            org.telegram.ui.Adapters.DialogsAdapter r11 = (org.telegram.ui.Adapters.DialogsAdapter) r11
            int r8 = r5.currentAccount
            int r2 = r5.folderId
            boolean r3 = r5.dialogsListFrozen
            java.util.ArrayList r8 = getDialogsArray(r8, r10, r2, r3)
            int r7 = r11.fixPosition(r7)
            if (r7 < 0) goto L_0x01dd
            int r10 = r8.size()
            if (r7 < r10) goto L_0x0140
            goto L_0x01dd
        L_0x0140:
            java.lang.Object r7 = r8.get(r7)
            org.telegram.tgnet.TLRPC$Dialog r7 = (org.telegram.tgnet.TLRPC$Dialog) r7
            boolean r8 = r5.onlySelect
            if (r8 == 0) goto L_0x0163
            int r8 = r5.initialDialogsType
            r9 = 3
            if (r8 == r9) goto L_0x0150
            return r1
        L_0x0150:
            long r8 = r7.id
            boolean r8 = r5.validateSlowModeDialog(r8)
            if (r8 != 0) goto L_0x0159
            return r1
        L_0x0159:
            long r7 = r7.id
            r5.addOrRemoveSelectedDialog(r7, r6)
            r5.updateSelectedCount()
            goto L_0x01dc
        L_0x0163:
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_dialogFolder
            if (r8 == 0) goto L_0x01ca
            org.telegram.ui.ActionBar.BottomSheet$Builder r6 = new org.telegram.ui.ActionBar.BottomSheet$Builder
            android.app.Activity r7 = r5.getParentActivity()
            r6.<init>(r7)
            org.telegram.messenger.MessagesStorage r7 = r5.getMessagesStorage()
            int r7 = r7.getArchiveUnreadCount()
            if (r7 == 0) goto L_0x017c
            r7 = 1
            goto L_0x017d
        L_0x017c:
            r7 = 0
        L_0x017d:
            r8 = 2
            int[] r10 = new int[r8]
            if (r7 == 0) goto L_0x0186
            r11 = 2131165674(0x7var_ea, float:1.7945572E38)
            goto L_0x0187
        L_0x0186:
            r11 = 0
        L_0x0187:
            r10[r1] = r11
            boolean r11 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r11 == 0) goto L_0x0191
            r11 = 2131165349(0x7var_a5, float:1.7944913E38)
            goto L_0x0194
        L_0x0191:
            r11 = 2131165354(0x7var_aa, float:1.7944923E38)
        L_0x0194:
            r10[r0] = r11
            java.lang.CharSequence[] r8 = new java.lang.CharSequence[r8]
            if (r7 == 0) goto L_0x01a3
            r7 = 2131625824(0x7f0e0760, float:1.8878867E38)
            java.lang.String r9 = "MarkAllAsRead"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r9, r7)
        L_0x01a3:
            r8[r1] = r9
            boolean r7 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r7 == 0) goto L_0x01af
            r7 = 2131626646(0x7f0e0a96, float:1.8880534E38)
            java.lang.String r9 = "PinInTheList"
            goto L_0x01b4
        L_0x01af:
            r7 = 2131625595(0x7f0e067b, float:1.8878402E38)
            java.lang.String r9 = "HideAboveTheList"
        L_0x01b4:
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
            r8[r0] = r7
            org.telegram.ui.-$$Lambda$DialogsActivity$Y1mxZnrmYPoNEMQAQ31XIX6rJGk r7 = new org.telegram.ui.-$$Lambda$DialogsActivity$Y1mxZnrmYPoNEMQAQ31XIX6rJGk
            r7.<init>()
            r6.setItems(r8, r10, r7)
            org.telegram.ui.ActionBar.BottomSheet r6 = r6.create()
            r5.showDialog(r6)
            return r1
        L_0x01ca:
            org.telegram.ui.ActionBar.ActionBar r8 = r5.actionBar
            boolean r8 = r8.isActionModeShowed()
            if (r8 == 0) goto L_0x01d9
            boolean r8 = r5.isDialogPinned(r7)
            if (r8 == 0) goto L_0x01d9
            return r1
        L_0x01d9:
            r5.showOrUpdateActionMode(r7, r6)
        L_0x01dc:
            return r0
        L_0x01dd:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.onItemLongClick(android.view.View, int, float, float, int, androidx.recyclerview.widget.RecyclerView$Adapter):boolean");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onItemLongClick$15 */
    public /* synthetic */ void lambda$onItemLongClick$15$DialogsActivity(long j, DialogInterface dialogInterface, int i) {
        this.searchViewPager.dialogsSearchAdapter.removeRecentSearch(j);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onItemLongClick$16 */
    public /* synthetic */ void lambda$onItemLongClick$16$DialogsActivity(DialogInterface dialogInterface, int i) {
        if (i == 0) {
            getMessagesStorage().readAllDialogs(1);
        } else if (i == 1 && this.viewPages != null) {
            int i2 = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (i2 < viewPageArr.length) {
                    if (viewPageArr[i2].dialogsType == 0 && this.viewPages[i2].getVisibility() == 0) {
                        View childAt = this.viewPages[i2].listView.getChildAt(0);
                        DialogCell dialogCell = null;
                        if (childAt instanceof DialogCell) {
                            DialogCell dialogCell2 = (DialogCell) childAt;
                            if (dialogCell2.isFolderCell()) {
                                dialogCell = dialogCell2;
                            }
                        }
                        this.viewPages[i2].listView.toggleArchiveHidden(true, dialogCell);
                    }
                    i2++;
                } else {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean showChatPreview(DialogCell dialogCell) {
        TLRPC$Chat chat;
        long dialogId = dialogCell.getDialogId();
        Bundle bundle = new Bundle();
        int i = (int) dialogId;
        int messageId = dialogCell.getMessageId();
        if (i == 0) {
            return false;
        }
        if (i > 0) {
            bundle.putInt("user_id", i);
        } else if (i < 0) {
            if (!(messageId == 0 || (chat = getMessagesController().getChat(Integer.valueOf(-i))) == null || chat.migrated_to == null)) {
                bundle.putInt("migrated_to", i);
                i = -chat.migrated_to.channel_id;
            }
            bundle.putInt("chat_id", -i);
        }
        if (messageId != 0) {
            bundle.putInt("message_id", messageId);
        }
        if (this.searchString != null) {
            if (!getMessagesController().checkCanOpenChat(bundle, this)) {
                return true;
            }
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
            prepareBlurBitmap();
            presentFragmentAsPreview(new ChatActivity(bundle));
            return true;
        } else if (!getMessagesController().checkCanOpenChat(bundle, this)) {
            return true;
        } else {
            prepareBlurBitmap();
            presentFragmentAsPreview(new ChatActivity(bundle));
            return true;
        }
    }

    /* access modifiers changed from: private */
    public void updateFloatingButtonOffset() {
        this.floatingButtonContainer.setTranslationY(this.floatingButtonTranslation - (this.additionalFloatingTranslation * (1.0f - this.floatingButtonHideProgress)));
    }

    /* access modifiers changed from: private */
    public boolean hasHiddenArchive() {
        return !this.onlySelect && this.initialDialogsType == 0 && this.folderId == 0 && getMessagesController().hasHiddenArchive();
    }

    /* access modifiers changed from: private */
    public boolean waitingForDialogsAnimationEnd(ViewPage viewPage) {
        return (!viewPage.dialogsItemAnimator.isRunning() && this.dialogRemoveFinished == 0 && this.dialogInsertFinished == 0 && this.dialogChangeFinished == 0) ? false : true;
    }

    /* access modifiers changed from: private */
    public void onDialogAnimationFinished() {
        this.dialogRemoveFinished = 0;
        this.dialogInsertFinished = 0;
        this.dialogChangeFinished = 0;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                DialogsActivity.this.lambda$onDialogAnimationFinished$17$DialogsActivity();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onDialogAnimationFinished$17 */
    public /* synthetic */ void lambda$onDialogAnimationFinished$17$DialogsActivity() {
        ArrayList<TLRPC$Dialog> arrayList;
        if (!(this.viewPages == null || this.folderId == 0 || ((arrayList = frozenDialogsList) != null && !arrayList.isEmpty()))) {
            int i = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (i >= viewPageArr.length) {
                    break;
                }
                viewPageArr[i].listView.setEmptyView((View) null);
                this.viewPages[i].progressView.setVisibility(4);
                i++;
            }
            finishFragment();
        }
        setDialogsListFrozen(false);
        updateDialogIndices();
    }

    /* access modifiers changed from: private */
    public void setScrollY(float f) {
        View view = this.scrimView;
        if (view != null) {
            view.getLocationInWindow(this.scrimViewLocation);
        }
        this.actionBar.setTranslationY(f);
        FilterTabsView filterTabsView2 = this.filterTabsView;
        if (filterTabsView2 != null) {
            filterTabsView2.setTranslationY(f);
        }
        updateContextViewPosition();
        if (this.viewPages != null) {
            int i = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (i >= viewPageArr.length) {
                    break;
                }
                viewPageArr[i].listView.setTopGlowOffset(this.viewPages[i].listView.getPaddingTop() + ((int) f));
                i++;
            }
        }
        this.fragmentView.invalidate();
    }

    private void prepareBlurBitmap() {
        if (this.blurredView != null) {
            int measuredWidth = (int) (((float) this.fragmentView.getMeasuredWidth()) / 6.0f);
            int measuredHeight = (int) (((float) this.fragmentView.getMeasuredHeight()) / 6.0f);
            Bitmap createBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            canvas.scale(0.16666667f, 0.16666667f);
            this.fragmentView.draw(canvas);
            Utilities.stackBlurBitmap(createBitmap, Math.max(7, Math.max(measuredWidth, measuredHeight) / 180));
            this.blurredView.setBackground(new BitmapDrawable(createBitmap));
            this.blurredView.setAlpha(0.0f);
            this.blurredView.setVisibility(0);
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationProgress(boolean z, float f) {
        View view = this.blurredView;
        if (view != null && view.getVisibility() == 0) {
            if (z) {
                this.blurredView.setAlpha(1.0f - f);
            } else {
                this.blurredView.setAlpha(f);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        View view;
        if (z && (view = this.blurredView) != null && view.getVisibility() == 0) {
            this.blurredView.setVisibility(8);
            this.blurredView.setBackground((Drawable) null);
        }
    }

    /* access modifiers changed from: private */
    public void resetScroll() {
        if (this.actionBar.getTranslationY() != 0.0f) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, this.SCROLL_Y, new float[]{0.0f})});
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.setDuration(180);
            animatorSet.start();
        }
    }

    /* access modifiers changed from: private */
    public void hideActionMode(boolean z) {
        this.actionBar.hideActionMode();
        if (this.menuDrawable != null) {
            this.actionBar.setBackButtonContentDescription(LocaleController.getString("AccDescrOpenMenu", NUM));
        }
        this.selectedDialogs.clear();
        MenuDrawable menuDrawable2 = this.menuDrawable;
        if (menuDrawable2 != null) {
            menuDrawable2.setRotation(0.0f, true);
        } else {
            BackDrawable backDrawable2 = this.backDrawable;
            if (backDrawable2 != null) {
                backDrawable2.setRotation(0.0f, true);
            }
        }
        FilterTabsView filterTabsView2 = this.filterTabsView;
        if (filterTabsView2 != null) {
            filterTabsView2.animateColorsTo("actionBarTabLine", "actionBarTabActiveText", "actionBarTabUnactiveText", "actionBarTabSelector", "actionBarDefault");
        }
        ValueAnimator valueAnimator = this.actionBarColorAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        int i = 0;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.progressToActionMode, 0.0f});
        this.actionBarColorAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                DialogsActivity.this.lambda$hideActionMode$18$DialogsActivity(valueAnimator);
            }
        });
        this.actionBarColorAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.actionBarColorAnimator.setDuration(200);
        this.actionBarColorAnimator.start();
        this.allowMoving = false;
        if (!this.movingDialogFilters.isEmpty()) {
            int i2 = 0;
            for (int size = this.movingDialogFilters.size(); i2 < size; size = size) {
                MessagesController.DialogFilter dialogFilter = this.movingDialogFilters.get(i2);
                FilterCreateActivity.saveFilterToServer(dialogFilter, dialogFilter.flags, dialogFilter.name, dialogFilter.alwaysShow, dialogFilter.neverShow, dialogFilter.pinnedDialogs, false, false, true, true, false, this, (Runnable) null);
                i2++;
            }
            this.movingDialogFilters.clear();
        }
        if (this.movingWas) {
            getMessagesController().reorderPinnedDialogs(this.folderId, (ArrayList<TLRPC$InputDialogPeer>) null, 0);
            this.movingWas = false;
        }
        updateCounters(true);
        if (this.viewPages != null) {
            int i3 = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (i3 >= viewPageArr.length) {
                    break;
                }
                viewPageArr[i3].dialogsAdapter.onReorderStateChanged(false);
                i3++;
            }
        }
        if (z) {
            i = 8192;
        }
        updateVisibleRows(196608 | i);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$hideActionMode$18 */
    public /* synthetic */ void lambda$hideActionMode$18$DialogsActivity(ValueAnimator valueAnimator) {
        this.progressToActionMode = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        for (int i = 0; i < this.actionBar.getChildCount(); i++) {
            if (!(this.actionBar.getChildAt(i).getVisibility() != 0 || this.actionBar.getChildAt(i) == this.actionBar.getActionMode() || this.actionBar.getChildAt(i) == this.actionBar.getBackButton())) {
                this.actionBar.getChildAt(i).setAlpha(1.0f - this.progressToActionMode);
            }
        }
        this.fragmentView.invalidate();
    }

    private int getPinnedCount() {
        ArrayList<TLRPC$Dialog> arrayList;
        if (this.viewPages[0].dialogsType == 7 || this.viewPages[0].dialogsType == 8) {
            arrayList = getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, this.dialogsListFrozen);
        } else {
            arrayList = getMessagesController().getDialogs(this.folderId);
        }
        int size = arrayList.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            TLRPC$Dialog tLRPC$Dialog = arrayList.get(i2);
            if (!(tLRPC$Dialog instanceof TLRPC$TL_dialogFolder)) {
                long j = tLRPC$Dialog.id;
                if (isDialogPinned(tLRPC$Dialog)) {
                    i++;
                } else if (!getMessagesController().isPromoDialog(tLRPC$Dialog.id, false)) {
                    break;
                }
            }
        }
        return i;
    }

    /* access modifiers changed from: private */
    public boolean isDialogPinned(TLRPC$Dialog tLRPC$Dialog) {
        MessagesController.DialogFilter dialogFilter;
        if (this.viewPages[0].dialogsType == 7 || this.viewPages[0].dialogsType == 8) {
            dialogFilter = getMessagesController().selectedDialogFilter[this.viewPages[0].dialogsType == 8 ? (char) 1 : 0];
        } else {
            dialogFilter = null;
        }
        if (dialogFilter == null) {
            return tLRPC$Dialog.pinned;
        }
        if (dialogFilter.pinnedDialogs.indexOfKey(tLRPC$Dialog.id) >= 0) {
            return true;
        }
        return false;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v10, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v4, resolved type: org.telegram.tgnet.TLRPC$EncryptedChat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v17, resolved type: org.telegram.tgnet.TLRPC$EncryptedChat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v14, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v17, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v22, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v20, resolved type: org.telegram.tgnet.TLRPC$EncryptedChat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v24, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v18, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v23, resolved type: org.telegram.tgnet.TLRPC$EncryptedChat} */
    /* JADX WARNING: type inference failed for: r12v7, types: [org.telegram.tgnet.TLRPC$Chat] */
    /* JADX WARNING: type inference failed for: r12v16 */
    /* JADX WARNING: type inference failed for: r12v25 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void perfromSelectedDialogsAction(int r34, boolean r35) {
        /*
            r33 = this;
            r13 = r33
            r14 = r34
            android.app.Activity r0 = r33.getParentActivity()
            if (r0 != 0) goto L_0x000b
            return
        L_0x000b:
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r13.viewPages
            r15 = 0
            r0 = r0[r15]
            int r0 = r0.dialogsType
            r1 = 7
            r2 = 8
            r3 = 0
            if (r0 == r1) goto L_0x0027
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r13.viewPages
            r0 = r0[r15]
            int r0 = r0.dialogsType
            if (r0 != r2) goto L_0x0025
            goto L_0x0027
        L_0x0025:
            r0 = r3
            goto L_0x003c
        L_0x0027:
            org.telegram.messenger.MessagesController r0 = r33.getMessagesController()
            org.telegram.messenger.MessagesController$DialogFilter[] r0 = r0.selectedDialogFilter
            org.telegram.ui.DialogsActivity$ViewPage[] r4 = r13.viewPages
            r4 = r4[r15]
            int r4 = r4.dialogsType
            if (r4 != r2) goto L_0x0039
            r4 = 1
            goto L_0x003a
        L_0x0039:
            r4 = 0
        L_0x003a:
            r0 = r0[r4]
        L_0x003c:
            java.util.ArrayList<java.lang.Long> r4 = r13.selectedDialogs
            int r4 = r4.size()
            r5 = 105(0x69, float:1.47E-43)
            if (r14 == r5) goto L_0x0647
            r5 = 107(0x6b, float:1.5E-43)
            if (r14 != r5) goto L_0x004c
            goto L_0x0647
        L_0x004c:
            java.lang.String r9 = "Cancel"
            r6 = 108(0x6c, float:1.51E-43)
            r7 = 100
            if (r14 == r7) goto L_0x0056
            if (r14 != r6) goto L_0x01ad
        L_0x0056:
            int r8 = r13.canPinCount
            if (r8 == 0) goto L_0x01ad
            org.telegram.messenger.MessagesController r8 = r33.getMessagesController()
            int r6 = r13.folderId
            java.util.ArrayList r6 = r8.getDialogs(r6)
            int r8 = r6.size()
            r5 = 0
            r18 = 0
            r19 = 0
        L_0x006d:
            if (r5 >= r8) goto L_0x00a5
            java.lang.Object r20 = r6.get(r5)
            r10 = r20
            org.telegram.tgnet.TLRPC$Dialog r10 = (org.telegram.tgnet.TLRPC$Dialog) r10
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC$TL_dialogFolder
            if (r11 == 0) goto L_0x007e
            r35 = r8
            goto L_0x009e
        L_0x007e:
            r35 = r8
            long r7 = r10.id
            int r8 = (int) r7
            boolean r7 = r13.isDialogPinned(r10)
            if (r7 == 0) goto L_0x0091
            if (r8 != 0) goto L_0x008e
            int r19 = r19 + 1
            goto L_0x009e
        L_0x008e:
            int r18 = r18 + 1
            goto L_0x009e
        L_0x0091:
            org.telegram.messenger.MessagesController r7 = r33.getMessagesController()
            long r11 = r10.id
            boolean r7 = r7.isPromoDialog(r11, r15)
            if (r7 != 0) goto L_0x009e
            goto L_0x00a5
        L_0x009e:
            int r5 = r5 + 1
            r8 = r35
            r7 = 100
            goto L_0x006d
        L_0x00a5:
            r5 = 0
            r6 = 0
            r7 = 0
            r10 = 0
        L_0x00a9:
            if (r5 >= r4) goto L_0x00e7
            java.util.ArrayList<java.lang.Long> r11 = r13.selectedDialogs
            java.lang.Object r11 = r11.get(r5)
            java.lang.Long r11 = (java.lang.Long) r11
            long r11 = r11.longValue()
            org.telegram.messenger.MessagesController r8 = r33.getMessagesController()
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r8 = r8.dialogs_dict
            java.lang.Object r8 = r8.get(r11)
            org.telegram.tgnet.TLRPC$Dialog r8 = (org.telegram.tgnet.TLRPC$Dialog) r8
            if (r8 == 0) goto L_0x00e4
            boolean r8 = r13.isDialogPinned(r8)
            if (r8 == 0) goto L_0x00cc
            goto L_0x00e4
        L_0x00cc:
            int r8 = (int) r11
            if (r8 != 0) goto L_0x00d2
            int r7 = r7 + 1
            goto L_0x00d4
        L_0x00d2:
            int r6 = r6 + 1
        L_0x00d4:
            if (r0 == 0) goto L_0x00e4
            java.util.ArrayList<java.lang.Integer> r11 = r0.alwaysShow
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            boolean r8 = r11.contains(r8)
            if (r8 == 0) goto L_0x00e4
            int r10 = r10 + 1
        L_0x00e4:
            int r5 = r5 + 1
            goto L_0x00a9
        L_0x00e7:
            org.telegram.ui.DialogsActivity$ViewPage[] r5 = r13.viewPages
            r5 = r5[r15]
            int r5 = r5.dialogsType
            if (r5 == r1) goto L_0x0111
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r13.viewPages
            r1 = r1[r15]
            int r1 = r1.dialogsType
            if (r1 != r2) goto L_0x00fc
            goto L_0x0111
        L_0x00fc:
            int r1 = r13.folderId
            if (r1 != 0) goto L_0x010a
            if (r0 == 0) goto L_0x0103
            goto L_0x010a
        L_0x0103:
            org.telegram.messenger.MessagesController r1 = r33.getMessagesController()
            int r1 = r1.maxPinnedDialogsCount
            goto L_0x011b
        L_0x010a:
            org.telegram.messenger.MessagesController r1 = r33.getMessagesController()
            int r1 = r1.maxFolderPinnedDialogsCount
            goto L_0x011b
        L_0x0111:
            java.util.ArrayList<java.lang.Integer> r1 = r0.alwaysShow
            int r1 = r1.size()
            r2 = 100
            int r1 = 100 - r1
        L_0x011b:
            int r7 = r7 + r19
            if (r7 > r1) goto L_0x0124
            int r6 = r6 + r18
            int r6 = r6 - r10
            if (r6 <= r1) goto L_0x02bb
        L_0x0124:
            int r2 = r13.folderId
            java.lang.String r4 = "Chats"
            if (r2 != 0) goto L_0x017c
            if (r0 == 0) goto L_0x012d
            goto L_0x017c
        L_0x012d:
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r2 = r33.getParentActivity()
            r0.<init>((android.content.Context) r2)
            r2 = 2131624250(0x7f0e013a, float:1.8875674E38)
            java.lang.String r5 = "AppName"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
            r0.setTitle(r2)
            r2 = 2131626655(0x7f0e0a9f, float:1.8880552E38)
            r5 = 1
            java.lang.Object[] r5 = new java.lang.Object[r5]
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r4, r1)
            r5[r15] = r1
            java.lang.String r1 = "PinToTopLimitReached2"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)
            r0.setMessage(r1)
            r1 = 2131625428(0x7f0e05d4, float:1.8878064E38)
            java.lang.String r2 = "FiltersSetupPinAlert"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.-$$Lambda$DialogsActivity$h_ynCLvAb5sEP-CQ-yfc4Q6wUF0 r2 = new org.telegram.ui.-$$Lambda$DialogsActivity$h_ynCLvAb5sEP-CQ-yfc4Q6wUF0
            r2.<init>()
            r0.setNegativeButton(r1, r2)
            r1 = 2131626258(0x7f0e0912, float:1.8879747E38)
            java.lang.String r2 = "OK"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setPositiveButton(r1, r3)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r13.showDialog(r0)
            goto L_0x0191
        L_0x017c:
            r0 = 2131626645(0x7f0e0a95, float:1.8880532E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r4, r1)
            r2[r15] = r1
            java.lang.String r1 = "PinFolderLimitReached"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            org.telegram.ui.Components.AlertsCreator.showSimpleAlert(r13, r0)
        L_0x0191:
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r13.pinItem
            r1 = 1073741824(0x40000000, float:2.0)
            org.telegram.messenger.AndroidUtilities.shakeView(r0, r1, r15)
            android.app.Activity r0 = r33.getParentActivity()
            java.lang.String r1 = "vibrator"
            java.lang.Object r0 = r0.getSystemService(r1)
            android.os.Vibrator r0 = (android.os.Vibrator) r0
            if (r0 == 0) goto L_0x01ac
            r1 = 200(0xc8, double:9.9E-322)
            r0.vibrate(r1)
        L_0x01ac:
            return
        L_0x01ad:
            r1 = 102(0x66, float:1.43E-43)
            if (r14 == r1) goto L_0x01b5
            r2 = 103(0x67, float:1.44E-43)
            if (r14 != r2) goto L_0x028b
        L_0x01b5:
            r2 = 1
            if (r4 <= r2) goto L_0x028b
            if (r35 == 0) goto L_0x028b
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r5 = r33.getParentActivity()
            r0.<init>((android.content.Context) r5)
            if (r14 != r1) goto L_0x01fc
            r1 = 2131625028(0x7f0e0444, float:1.8877252E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r5 = "ChatsSelected"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r5, r4)
            r2[r15] = r4
            java.lang.String r4 = "DeleteFewChatsTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r4, r1, r2)
            r0.setTitle(r1)
            r1 = 2131624301(0x7f0e016d, float:1.8875778E38)
            java.lang.String r2 = "AreYouSureDeleteFewChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setMessage(r1)
            r1 = 2131625005(0x7f0e042d, float:1.8877206E38)
            java.lang.String r2 = "Delete"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.-$$Lambda$DialogsActivity$l-HTByMzLae_JFBx5BN8WgBZi6M r2 = new org.telegram.ui.-$$Lambda$DialogsActivity$l-HTByMzLae_JFBx5BN8WgBZi6M
            r2.<init>(r14)
            r0.setPositiveButton(r1, r2)
        L_0x01f8:
            r1 = 2131624575(0x7f0e027f, float:1.8876334E38)
            goto L_0x026a
        L_0x01fc:
            int r1 = r13.canClearCacheCount
            if (r1 == 0) goto L_0x0235
            r1 = 2131624822(0x7f0e0376, float:1.8876835E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r5 = "ChatsSelectedClearCache"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r5, r4)
            r2[r15] = r4
            java.lang.String r4 = "ClearCacheFewChatsTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r4, r1, r2)
            r0.setTitle(r1)
            r1 = 2131624290(0x7f0e0162, float:1.8875756E38)
            java.lang.String r2 = "AreYouSureClearHistoryCacheFewChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setMessage(r1)
            r1 = 2131624826(0x7f0e037a, float:1.8876843E38)
            java.lang.String r2 = "ClearHistoryCache"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.-$$Lambda$DialogsActivity$LsmKgeBSUuBzVLnvtNmfumOd5UU r2 = new org.telegram.ui.-$$Lambda$DialogsActivity$LsmKgeBSUuBzVLnvtNmfumOd5UU
            r2.<init>(r14)
            r0.setPositiveButton(r1, r2)
            goto L_0x01f8
        L_0x0235:
            r1 = 2131624824(0x7f0e0378, float:1.8876839E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r5 = "ChatsSelectedClear"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r5, r4)
            r2[r15] = r4
            java.lang.String r4 = "ClearFewChatsTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r4, r1, r2)
            r0.setTitle(r1)
            r1 = 2131624292(0x7f0e0164, float:1.887576E38)
            java.lang.String r2 = "AreYouSureClearHistoryFewChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setMessage(r1)
            r1 = 2131624825(0x7f0e0379, float:1.887684E38)
            java.lang.String r2 = "ClearHistory"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.-$$Lambda$DialogsActivity$Vom0PrsJKSRyyNBQJMUiqiNGbVo r2 = new org.telegram.ui.-$$Lambda$DialogsActivity$Vom0PrsJKSRyyNBQJMUiqiNGbVo
            r2.<init>(r14)
            r0.setPositiveButton(r1, r2)
            goto L_0x01f8
        L_0x026a:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r9, r1)
            r0.setNegativeButton(r1, r3)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r13.showDialog(r0)
            r1 = -1
            android.view.View r0 = r0.getButton(r1)
            android.widget.TextView r0 = (android.widget.TextView) r0
            if (r0 == 0) goto L_0x028a
            java.lang.String r1 = "dialogTextRed2"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTextColor(r1)
        L_0x028a:
            return
        L_0x028b:
            r1 = 106(0x6a, float:1.49E-43)
            if (r14 != r1) goto L_0x02bb
            if (r35 == 0) goto L_0x02bb
            r1 = 1
            if (r4 != r1) goto L_0x02ad
            java.util.ArrayList<java.lang.Long> r0 = r13.selectedDialogs
            java.lang.Object r0 = r0.get(r15)
            java.lang.Long r0 = (java.lang.Long) r0
            long r0 = r0.longValue()
            org.telegram.messenger.MessagesController r2 = r33.getMessagesController()
            int r1 = (int) r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r3 = r2.getUser(r0)
        L_0x02ad:
            int r0 = r13.canReportSpamCount
            if (r0 == 0) goto L_0x02b2
            r15 = 1
        L_0x02b2:
            org.telegram.ui.-$$Lambda$DialogsActivity$8bIOdd3yvk5k2d44YoaHQ6iidjM r0 = new org.telegram.ui.-$$Lambda$DialogsActivity$8bIOdd3yvk5k2d44YoaHQ6iidjM
            r0.<init>()
            org.telegram.ui.Components.AlertsCreator.createBlockDialogAlert(r13, r4, r15, r3, r0)
            return
        L_0x02bb:
            r1 = 2147483647(0x7fffffff, float:NaN)
            if (r0 == 0) goto L_0x02eb
            r2 = 100
            if (r14 == r2) goto L_0x02c8
            r2 = 108(0x6c, float:1.51E-43)
            if (r14 != r2) goto L_0x02eb
        L_0x02c8:
            int r2 = r13.canPinCount
            if (r2 == 0) goto L_0x02eb
            android.util.LongSparseArray<java.lang.Integer> r2 = r0.pinnedDialogs
            int r2 = r2.size()
            r5 = 0
        L_0x02d3:
            if (r5 >= r2) goto L_0x02e8
            android.util.LongSparseArray<java.lang.Integer> r6 = r0.pinnedDialogs
            java.lang.Object r6 = r6.valueAt(r5)
            java.lang.Integer r6 = (java.lang.Integer) r6
            int r6 = r6.intValue()
            int r1 = java.lang.Math.min(r1, r6)
            int r5 = r5 + 1
            goto L_0x02d3
        L_0x02e8:
            int r2 = r13.canPinCount
            int r1 = r1 - r2
        L_0x02eb:
            r2 = 0
            r18 = 0
        L_0x02ee:
            r5 = 104(0x68, float:1.46E-43)
            if (r2 >= r4) goto L_0x05ce
            java.util.ArrayList<java.lang.Long> r6 = r13.selectedDialogs
            java.lang.Object r6 = r6.get(r2)
            java.lang.Long r6 = (java.lang.Long) r6
            long r6 = r6.longValue()
            org.telegram.messenger.MessagesController r8 = r33.getMessagesController()
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r8 = r8.dialogs_dict
            java.lang.Object r8 = r8.get(r6)
            org.telegram.tgnet.TLRPC$Dialog r8 = (org.telegram.tgnet.TLRPC$Dialog) r8
            if (r8 != 0) goto L_0x0317
        L_0x030c:
            r5 = 102(0x66, float:1.43E-43)
            r11 = 103(0x67, float:1.44E-43)
            r15 = 2131624575(0x7f0e027f, float:1.8876334E38)
        L_0x0313:
            r16 = 2
            goto L_0x05c8
        L_0x0317:
            int r10 = (int) r6
            r12 = 32
            long r11 = r6 >> r12
            int r12 = (int) r11
            if (r10 == 0) goto L_0x0342
            if (r10 <= 0) goto L_0x0331
            org.telegram.messenger.MessagesController r11 = r33.getMessagesController()
            java.lang.Integer r12 = java.lang.Integer.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r11 = r11.getUser(r12)
            r12 = r3
            r15 = r11
            r11 = r12
            goto L_0x0366
        L_0x0331:
            org.telegram.messenger.MessagesController r11 = r33.getMessagesController()
            int r12 = -r10
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            org.telegram.tgnet.TLRPC$Chat r11 = r11.getChat(r12)
            r15 = r3
            r12 = r11
            r11 = r15
            goto L_0x0366
        L_0x0342:
            org.telegram.messenger.MessagesController r11 = r33.getMessagesController()
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            org.telegram.tgnet.TLRPC$EncryptedChat r11 = r11.getEncryptedChat(r12)
            if (r11 == 0) goto L_0x035f
            org.telegram.messenger.MessagesController r12 = r33.getMessagesController()
            int r15 = r11.user_id
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            org.telegram.tgnet.TLRPC$User r12 = r12.getUser(r15)
            goto L_0x0364
        L_0x035f:
            org.telegram.tgnet.TLRPC$TL_userEmpty r12 = new org.telegram.tgnet.TLRPC$TL_userEmpty
            r12.<init>()
        L_0x0364:
            r15 = r12
            r12 = r3
        L_0x0366:
            if (r12 != 0) goto L_0x036b
            if (r15 != 0) goto L_0x036b
            goto L_0x030c
        L_0x036b:
            if (r15 == 0) goto L_0x037c
            boolean r3 = r15.bot
            if (r3 == 0) goto L_0x037c
            boolean r3 = org.telegram.messenger.MessagesController.isSupportUser(r15)
            if (r3 != 0) goto L_0x037c
            r3 = 100
            r21 = 1
            goto L_0x0380
        L_0x037c:
            r3 = 100
            r21 = 0
        L_0x0380:
            if (r14 == r3) goto L_0x0528
            r3 = r11
            r11 = 108(0x6c, float:1.51E-43)
            if (r14 != r11) goto L_0x0389
            goto L_0x0529
        L_0x0389:
            r3 = 101(0x65, float:1.42E-43)
            if (r14 != r3) goto L_0x03c6
            int r3 = r13.canReadCount
            if (r3 == 0) goto L_0x03b7
            org.telegram.messenger.MessagesController r3 = r33.getMessagesController()
            r3.markMentionsAsRead(r6)
            org.telegram.messenger.MessagesController r21 = r33.getMessagesController()
            int r3 = r8.top_message
            int r5 = r8.last_message_date
            r27 = 0
            r28 = 0
            r30 = 0
            r31 = 1
            r32 = 0
            r22 = r6
            r24 = r3
            r25 = r3
            r26 = r5
            r21.markDialogAsRead(r22, r24, r25, r26, r27, r28, r30, r31, r32)
            goto L_0x030c
        L_0x03b7:
            org.telegram.messenger.MessagesController r21 = r33.getMessagesController()
            r24 = 0
            r25 = 0
            r22 = r6
            r21.markDialogAsUnread(r22, r24, r25)
            goto L_0x030c
        L_0x03c6:
            r3 = 102(0x66, float:1.43E-43)
            if (r14 == r3) goto L_0x0417
            r3 = 103(0x67, float:1.44E-43)
            if (r14 != r3) goto L_0x03d1
            r3 = 1
            r11 = 3
            goto L_0x0419
        L_0x03d1:
            if (r14 != r5) goto L_0x030c
            r3 = 1
            if (r4 != r3) goto L_0x03e7
            int r5 = r13.canMuteCount
            if (r5 != r3) goto L_0x03e7
            org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.Components.AlertsCreator.createMuteAlert(r13, r6)
            org.telegram.ui.-$$Lambda$DialogsActivity$qHal3VBoJNpnO_DSSmW5BbpRq0A r1 = new org.telegram.ui.-$$Lambda$DialogsActivity$qHal3VBoJNpnO_DSSmW5BbpRq0A
            r1.<init>()
            r13.showDialog(r0, r1)
            return
        L_0x03e7:
            int r3 = r13.canUnmuteCount
            if (r3 == 0) goto L_0x0401
            org.telegram.messenger.MessagesController r3 = r33.getMessagesController()
            boolean r3 = r3.isDialogMuted(r6)
            if (r3 != 0) goto L_0x03f7
            goto L_0x030c
        L_0x03f7:
            org.telegram.messenger.NotificationsController r3 = r33.getNotificationsController()
            r5 = 4
            r3.setDialogNotificationsSettings(r6, r5)
            goto L_0x030c
        L_0x0401:
            org.telegram.messenger.MessagesController r3 = r33.getMessagesController()
            boolean r3 = r3.isDialogMuted(r6)
            if (r3 == 0) goto L_0x040d
            goto L_0x030c
        L_0x040d:
            org.telegram.messenger.NotificationsController r3 = r33.getNotificationsController()
            r11 = 3
            r3.setDialogNotificationsSettings(r6, r11)
            goto L_0x030c
        L_0x0417:
            r11 = 3
            r3 = 1
        L_0x0419:
            if (r4 != r3) goto L_0x0490
            r5 = 102(0x66, float:1.43E-43)
            if (r14 != r5) goto L_0x0468
            boolean r0 = r13.canDeletePsaSelected
            if (r0 == 0) goto L_0x0468
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r1 = r33.getParentActivity()
            r0.<init>((android.content.Context) r1)
            r1 = 2131626759(0x7f0e0b07, float:1.8880763E38)
            java.lang.String r2 = "PsaHideChatAlertTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            r1 = 2131626758(0x7f0e0b06, float:1.8880761E38)
            java.lang.String r2 = "PsaHideChatAlertText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setMessage(r1)
            r1 = 2131626757(0x7f0e0b05, float:1.888076E38)
            java.lang.String r2 = "PsaHide"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.-$$Lambda$DialogsActivity$i5ktFozilU1q5HLQmy4EqmxpJvM r2 = new org.telegram.ui.-$$Lambda$DialogsActivity$i5ktFozilU1q5HLQmy4EqmxpJvM
            r2.<init>()
            r0.setPositiveButton(r1, r2)
            r15 = 2131624575(0x7f0e027f, float:1.8876334E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r9, r15)
            r2 = 0
            r0.setNegativeButton(r1, r2)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r13.showDialog(r0)
            goto L_0x048f
        L_0x0468:
            r0 = 103(0x67, float:1.44E-43)
            if (r14 != r0) goto L_0x046e
            r8 = 1
            goto L_0x046f
        L_0x046e:
            r8 = 0
        L_0x046f:
            if (r10 != 0) goto L_0x0474
            r19 = 1
            goto L_0x0476
        L_0x0474:
            r19 = 0
        L_0x0476:
            org.telegram.ui.-$$Lambda$DialogsActivity$IczsTATDa0fjGBiZ1QODAPJwRwE r9 = new org.telegram.ui.-$$Lambda$DialogsActivity$IczsTATDa0fjGBiZ1QODAPJwRwE
            r0 = r9
            r1 = r33
            r2 = r34
            r3 = r12
            r4 = r6
            r6 = r21
            r0.<init>(r2, r3, r4, r6)
            r0 = r33
            r1 = r8
            r2 = r12
            r3 = r15
            r4 = r19
            r5 = r9
            org.telegram.ui.Components.AlertsCreator.createClearOrDeleteDialogAlert(r0, r1, r2, r3, r4, r5)
        L_0x048f:
            return
        L_0x0490:
            r5 = 102(0x66, float:1.43E-43)
            r15 = 2131624575(0x7f0e027f, float:1.8876334E38)
            org.telegram.messenger.MessagesController r3 = r33.getMessagesController()
            r8 = 1
            boolean r3 = r3.isPromoDialog(r6, r8)
            if (r3 == 0) goto L_0x04ab
            org.telegram.messenger.MessagesController r3 = r33.getMessagesController()
            r3.hidePromoDialog()
        L_0x04a7:
            r11 = 103(0x67, float:1.44E-43)
            goto L_0x0313
        L_0x04ab:
            r3 = 103(0x67, float:1.44E-43)
            if (r14 != r3) goto L_0x04bd
            int r8 = r13.canClearCacheCount
            if (r8 == 0) goto L_0x04bd
            org.telegram.messenger.MessagesController r8 = r33.getMessagesController()
            r10 = 2
            r12 = 0
            r8.deleteDialog(r6, r10, r12)
            goto L_0x04a7
        L_0x04bd:
            r8 = 0
            r16 = 2
            if (r14 != r3) goto L_0x04ce
            org.telegram.messenger.MessagesController r10 = r33.getMessagesController()
            r12 = 1
            r10.deleteDialog(r6, r12, r8)
        L_0x04ca:
            r11 = 103(0x67, float:1.44E-43)
            goto L_0x05c8
        L_0x04ce:
            if (r12 == 0) goto L_0x04fd
            boolean r10 = org.telegram.messenger.ChatObject.isNotInChat(r12)
            if (r10 == 0) goto L_0x04de
            org.telegram.messenger.MessagesController r10 = r33.getMessagesController()
            r10.deleteDialog(r6, r8, r8)
            goto L_0x050e
        L_0x04de:
            org.telegram.messenger.MessagesController r8 = r33.getMessagesController()
            org.telegram.messenger.UserConfig r10 = r33.getUserConfig()
            int r10 = r10.getClientUserId()
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r8 = r8.getUser(r10)
            org.telegram.messenger.MessagesController r10 = r33.getMessagesController()
            long r11 = -r6
            int r12 = (int) r11
            r11 = 0
            r10.deleteUserFromChat(r12, r8, r11)
            goto L_0x050e
        L_0x04fd:
            org.telegram.messenger.MessagesController r8 = r33.getMessagesController()
            r11 = 0
            r8.deleteDialog(r6, r11, r11)
            if (r21 == 0) goto L_0x050e
            org.telegram.messenger.MessagesController r8 = r33.getMessagesController()
            r8.blockPeer(r10)
        L_0x050e:
            boolean r8 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r8 == 0) goto L_0x04ca
            org.telegram.messenger.NotificationCenter r8 = r33.getNotificationCenter()
            int r10 = org.telegram.messenger.NotificationCenter.closeChats
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            r7 = 0
            r12[r7] = r6
            r8.postNotificationName(r10, r12)
            goto L_0x04ca
        L_0x0528:
            r3 = r11
        L_0x0529:
            r5 = 102(0x66, float:1.43E-43)
            r11 = 103(0x67, float:1.44E-43)
            r15 = 2131624575(0x7f0e027f, float:1.8876334E38)
            r16 = 2
            int r12 = r13.canPinCount
            if (r12 == 0) goto L_0x0590
            boolean r8 = r13.isDialogPinned(r8)
            if (r8 == 0) goto L_0x053e
            goto L_0x05c8
        L_0x053e:
            if (r0 == 0) goto L_0x057d
            android.util.LongSparseArray<java.lang.Integer> r8 = r0.pinnedDialogs
            java.lang.Integer r12 = java.lang.Integer.valueOf(r1)
            r8.put(r6, r12)
            int r1 = r1 + 1
            if (r3 == 0) goto L_0x0567
            java.util.ArrayList<java.lang.Integer> r6 = r0.alwaysShow
            int r7 = r3.user_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            boolean r6 = r6.contains(r7)
            if (r6 != 0) goto L_0x05c8
            java.util.ArrayList<java.lang.Integer> r6 = r0.alwaysShow
            int r3 = r3.user_id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r6.add(r3)
            goto L_0x05c8
        L_0x0567:
            java.util.ArrayList<java.lang.Integer> r3 = r0.alwaysShow
            java.lang.Integer r6 = java.lang.Integer.valueOf(r10)
            boolean r3 = r3.contains(r6)
            if (r3 != 0) goto L_0x05c8
            java.util.ArrayList<java.lang.Integer> r3 = r0.alwaysShow
            java.lang.Integer r6 = java.lang.Integer.valueOf(r10)
            r3.add(r6)
            goto L_0x05c8
        L_0x057d:
            org.telegram.messenger.MessagesController r21 = r33.getMessagesController()
            r24 = 1
            r25 = 0
            r26 = -1
            r22 = r6
            boolean r3 = r21.pinDialog(r22, r24, r25, r26)
            if (r3 == 0) goto L_0x05c8
            goto L_0x05c6
        L_0x0590:
            boolean r3 = r13.isDialogPinned(r8)
            if (r3 != 0) goto L_0x0597
            goto L_0x05c8
        L_0x0597:
            if (r0 == 0) goto L_0x05b4
            android.util.LongSparseArray<java.lang.Integer> r3 = r0.pinnedDialogs
            r8 = -2147483648(0xfffffffvar_, float:-0.0)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r8)
            java.lang.Object r3 = r3.get(r6, r10)
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            if (r3 != r8) goto L_0x05ae
            goto L_0x05c8
        L_0x05ae:
            android.util.LongSparseArray<java.lang.Integer> r3 = r0.pinnedDialogs
            r3.remove(r6)
            goto L_0x05c8
        L_0x05b4:
            org.telegram.messenger.MessagesController r21 = r33.getMessagesController()
            r24 = 0
            r25 = 0
            r26 = -1
            r22 = r6
            boolean r3 = r21.pinDialog(r22, r24, r25, r26)
            if (r3 == 0) goto L_0x05c8
        L_0x05c6:
            r18 = 1
        L_0x05c8:
            int r2 = r2 + 1
            r3 = 0
            r15 = 0
            goto L_0x02ee
        L_0x05ce:
            r12 = 102(0x66, float:1.43E-43)
            r15 = 1
            if (r14 != r5) goto L_0x05e7
            if (r4 != r15) goto L_0x05d9
            int r1 = r13.canMuteCount
            if (r1 == r15) goto L_0x05e7
        L_0x05d9:
            int r1 = r13.canUnmuteCount
            if (r1 != 0) goto L_0x05df
            r5 = 1
            goto L_0x05e0
        L_0x05df:
            r5 = 0
        L_0x05e0:
            org.telegram.ui.Components.Bulletin r1 = org.telegram.ui.Components.BulletinFactory.createMuteBulletin((org.telegram.ui.ActionBar.BaseFragment) r13, (boolean) r5)
            r1.show()
        L_0x05e7:
            r7 = 100
            r6 = 108(0x6c, float:1.51E-43)
            if (r14 == r7) goto L_0x05f3
            if (r14 != r6) goto L_0x05f0
            goto L_0x05f3
        L_0x05f0:
            r15 = 108(0x6c, float:1.51E-43)
            goto L_0x062d
        L_0x05f3:
            if (r0 == 0) goto L_0x061f
            int r1 = r0.flags
            java.lang.String r2 = r0.name
            java.util.ArrayList<java.lang.Integer> r3 = r0.alwaysShow
            java.util.ArrayList<java.lang.Integer> r4 = r0.neverShow
            android.util.LongSparseArray<java.lang.Integer> r5 = r0.pinnedDialogs
            r8 = 0
            r9 = 0
            r10 = 1
            r11 = 1
            r16 = 0
            r17 = 0
            r15 = 108(0x6c, float:1.51E-43)
            r6 = r8
            r8 = 100
            r7 = r9
            r9 = 100
            r8 = r10
            r10 = 100
            r9 = r11
            r11 = 100
            r10 = r16
            r11 = r33
            r12 = r17
            org.telegram.ui.FilterCreateActivity.saveFilterToServer(r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            goto L_0x062d
        L_0x061f:
            r15 = 108(0x6c, float:1.51E-43)
            org.telegram.messenger.MessagesController r0 = r33.getMessagesController()
            int r1 = r13.folderId
            r2 = 0
            r4 = 0
            r0.reorderPinnedDialogs(r1, r4, r2)
        L_0x062d:
            if (r18 == 0) goto L_0x0636
            r0 = 0
            r13.hideFloatingButton(r0)
            r33.scrollToTop()
        L_0x0636:
            if (r14 == r15) goto L_0x0642
            r0 = 100
            if (r14 == r0) goto L_0x0642
            r0 = 102(0x66, float:1.43E-43)
            if (r14 == r0) goto L_0x0642
            r15 = 1
            goto L_0x0643
        L_0x0642:
            r15 = 0
        L_0x0643:
            r13.hideActionMode(r15)
            return
        L_0x0647:
            r16 = 2
            java.util.ArrayList r7 = new java.util.ArrayList
            java.util.ArrayList<java.lang.Long> r0 = r13.selectedDialogs
            r7.<init>(r0)
            org.telegram.messenger.MessagesController r0 = r33.getMessagesController()
            int r1 = r13.canUnarchiveCount
            if (r1 != 0) goto L_0x065a
            r2 = 1
            goto L_0x065b
        L_0x065a:
            r2 = 0
        L_0x065b:
            r3 = -1
            r4 = 0
            r5 = 0
            r1 = r7
            r0.addDialogToFolder(r1, r2, r3, r4, r5)
            int r0 = r13.canUnarchiveCount
            if (r0 != 0) goto L_0x06b3
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings()
            java.lang.String r1 = "archivehint_l"
            r2 = 0
            boolean r3 = r0.getBoolean(r1, r2)
            if (r3 != 0) goto L_0x067b
            boolean r2 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r2 == 0) goto L_0x0679
            goto L_0x067b
        L_0x0679:
            r12 = 0
            goto L_0x067c
        L_0x067b:
            r12 = 1
        L_0x067c:
            if (r12 != 0) goto L_0x068b
            android.content.SharedPreferences$Editor r0 = r0.edit()
            r2 = 1
            android.content.SharedPreferences$Editor r0 = r0.putBoolean(r1, r2)
            r0.commit()
            goto L_0x068c
        L_0x068b:
            r2 = 1
        L_0x068c:
            if (r12 == 0) goto L_0x0699
            int r0 = r7.size()
            if (r0 <= r2) goto L_0x0696
            r6 = 4
            goto L_0x0697
        L_0x0696:
            r6 = 2
        L_0x0697:
            r4 = r6
            goto L_0x06a3
        L_0x0699:
            int r0 = r7.size()
            if (r0 <= r2) goto L_0x06a1
            r0 = 5
            goto L_0x06a2
        L_0x06a1:
            r0 = 3
        L_0x06a2:
            r4 = r0
        L_0x06a3:
            org.telegram.ui.Components.UndoView r1 = r33.getUndoView()
            r2 = 0
            r5 = 0
            org.telegram.ui.-$$Lambda$DialogsActivity$UHZ8fosC5bvGXJ1-oBmU3jaTxDY r6 = new org.telegram.ui.-$$Lambda$DialogsActivity$UHZ8fosC5bvGXJ1-oBmU3jaTxDY
            r6.<init>(r7)
            r1.showWithAction(r2, r4, r5, r6)
            goto L_0x06e4
        L_0x06b3:
            org.telegram.messenger.MessagesController r0 = r33.getMessagesController()
            int r1 = r13.folderId
            java.util.ArrayList r0 = r0.getDialogs(r1)
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r13.viewPages
            if (r1 == 0) goto L_0x06e4
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x06e4
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r13.viewPages
            r1 = 0
            r0 = r0[r1]
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r0.listView
            r2 = 0
            r0.setEmptyView(r2)
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r13.viewPages
            r0 = r0[r1]
            org.telegram.ui.Components.RadialProgressView r0 = r0.progressView
            r2 = 4
            r0.setVisibility(r2)
            r33.finishFragment()
            goto L_0x06e5
        L_0x06e4:
            r1 = 0
        L_0x06e5:
            r13.hideActionMode(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.perfromSelectedDialogsAction(int, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$perfromSelectedDialogsAction$19 */
    public /* synthetic */ void lambda$perfromSelectedDialogsAction$19$DialogsActivity(ArrayList arrayList) {
        getMessagesController().addDialogToFolder(arrayList, this.folderId == 0 ? 0 : 1, -1, (ArrayList<TLRPC$TL_inputFolderPeer>) null, 0);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$perfromSelectedDialogsAction$20 */
    public /* synthetic */ void lambda$perfromSelectedDialogsAction$20$DialogsActivity(DialogInterface dialogInterface, int i) {
        presentFragment(new FiltersSetupActivity());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$perfromSelectedDialogsAction$21 */
    public /* synthetic */ void lambda$perfromSelectedDialogsAction$21$DialogsActivity(int i, DialogInterface dialogInterface, int i2) {
        getMessagesController().setDialogsInTransaction(true);
        perfromSelectedDialogsAction(i, false);
        getMessagesController().setDialogsInTransaction(false);
        MessagesController.getInstance(this.currentAccount).checkIfFolderEmpty(this.folderId);
        if (this.folderId != 0 && getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, false).size() == 0) {
            this.viewPages[0].listView.setEmptyView((View) null);
            this.viewPages[0].progressView.setVisibility(4);
            finishFragment();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$perfromSelectedDialogsAction$22 */
    public /* synthetic */ void lambda$perfromSelectedDialogsAction$22$DialogsActivity(int i, DialogInterface dialogInterface, int i2) {
        perfromSelectedDialogsAction(i, false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$perfromSelectedDialogsAction$23 */
    public /* synthetic */ void lambda$perfromSelectedDialogsAction$23$DialogsActivity(int i, DialogInterface dialogInterface, int i2) {
        perfromSelectedDialogsAction(i, false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$perfromSelectedDialogsAction$24 */
    public /* synthetic */ void lambda$perfromSelectedDialogsAction$24$DialogsActivity(boolean z, boolean z2) {
        int size = this.selectedDialogs.size();
        for (int i = 0; i < size; i++) {
            long longValue = this.selectedDialogs.get(i).longValue();
            int i2 = (int) longValue;
            if (z) {
                getMessagesController().reportSpam(longValue, getMessagesController().getUser(Integer.valueOf(i2)), (TLRPC$Chat) null, (TLRPC$EncryptedChat) null, false);
            }
            if (z2) {
                getMessagesController().deleteDialog(longValue, 0, true);
            }
            getMessagesController().blockPeer(i2);
        }
        hideActionMode(false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$perfromSelectedDialogsAction$25 */
    public /* synthetic */ void lambda$perfromSelectedDialogsAction$25$DialogsActivity(DialogInterface dialogInterface, int i) {
        getMessagesController().hidePromoDialog();
        hideActionMode(false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$perfromSelectedDialogsAction$27 */
    public /* synthetic */ void lambda$perfromSelectedDialogsAction$27$DialogsActivity(int i, TLRPC$Chat tLRPC$Chat, long j, boolean z, boolean z2) {
        int i2 = i;
        TLRPC$Chat tLRPC$Chat2 = tLRPC$Chat;
        long j2 = j;
        hideActionMode(false);
        if (i2 != 103 || !ChatObject.isChannel(tLRPC$Chat) || (tLRPC$Chat2.megagroup && TextUtils.isEmpty(tLRPC$Chat2.username))) {
            boolean z3 = z2;
            if (i2 == 102 && this.folderId != 0 && getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, false).size() == 1) {
                this.viewPages[0].progressView.setVisibility(4);
            }
            getUndoView().showWithAction(j2, i2 == 103 ? 0 : 1, (Runnable) new Runnable(i, j, z2, tLRPC$Chat, z) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ long f$2;
                public final /* synthetic */ boolean f$3;
                public final /* synthetic */ TLRPC$Chat f$4;
                public final /* synthetic */ boolean f$5;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r5;
                    this.f$4 = r6;
                    this.f$5 = r7;
                }

                public final void run() {
                    DialogsActivity.this.lambda$null$26$DialogsActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                }
            });
            return;
        }
        getMessagesController().deleteDialog(j2, 2, z2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$26 */
    public /* synthetic */ void lambda$null$26$DialogsActivity(int i, long j, boolean z, TLRPC$Chat tLRPC$Chat, boolean z2) {
        if (i == 103) {
            getMessagesController().deleteDialog(j, 1, z);
            return;
        }
        if (tLRPC$Chat == null) {
            getMessagesController().deleteDialog(j, 0, z);
            if (z2) {
                getMessagesController().blockPeer((int) j);
            }
        } else if (ChatObject.isNotInChat(tLRPC$Chat)) {
            getMessagesController().deleteDialog(j, 0, z);
        } else {
            getMessagesController().deleteUserFromChat((int) (-j), getMessagesController().getUser(Integer.valueOf(getUserConfig().getClientUserId())), (TLRPC$ChatFull) null);
        }
        if (AndroidUtilities.isTablet()) {
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, Long.valueOf(j));
        }
        MessagesController.getInstance(this.currentAccount).checkIfFolderEmpty(this.folderId);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$perfromSelectedDialogsAction$28 */
    public /* synthetic */ void lambda$perfromSelectedDialogsAction$28$DialogsActivity(DialogInterface dialogInterface) {
        hideActionMode(true);
    }

    /* access modifiers changed from: private */
    public void scrollToTop() {
        int findFirstVisibleItemPosition = this.viewPages[0].layoutManager.findFirstVisibleItemPosition() * AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f);
        int i = (this.viewPages[0].dialogsType != 0 || !hasHiddenArchive()) ? 0 : 1;
        if (((float) findFirstVisibleItemPosition) >= ((float) this.viewPages[0].listView.getMeasuredHeight()) * 1.2f) {
            this.viewPages[0].scrollHelper.setScrollDirection(1);
            this.viewPages[0].scrollHelper.scrollToPosition(i, 0, false, true);
            resetScroll();
            return;
        }
        this.viewPages[0].listView.smoothScrollToPosition(i);
    }

    /* access modifiers changed from: private */
    public void updateCounters(boolean z) {
        int i;
        int i2;
        int i3;
        TLRPC$User tLRPC$User;
        this.canDeletePsaSelected = false;
        this.canUnarchiveCount = 0;
        this.canUnmuteCount = 0;
        this.canMuteCount = 0;
        this.canPinCount = 0;
        this.canReadCount = 0;
        this.canClearCacheCount = 0;
        this.canReportSpamCount = 0;
        if (!z) {
            int size = this.selectedDialogs.size();
            int clientUserId = getUserConfig().getClientUserId();
            SharedPreferences notificationsSettings = getNotificationsSettings();
            int i4 = 0;
            int i5 = 0;
            int i6 = 0;
            int i7 = 0;
            int i8 = 0;
            int i9 = 0;
            while (i4 < size) {
                TLRPC$Dialog tLRPC$Dialog = getMessagesController().dialogs_dict.get(this.selectedDialogs.get(i4).longValue());
                if (tLRPC$Dialog == null) {
                    i2 = size;
                } else {
                    long j = tLRPC$Dialog.id;
                    boolean isDialogPinned = isDialogPinned(tLRPC$Dialog);
                    boolean z2 = tLRPC$Dialog.unread_count != 0 || tLRPC$Dialog.unread_mark;
                    if (getMessagesController().isDialogMuted(j)) {
                        i2 = size;
                        i3 = 1;
                        this.canUnmuteCount++;
                    } else {
                        i2 = size;
                        i3 = 1;
                        this.canMuteCount++;
                    }
                    if (z2) {
                        this.canReadCount += i3;
                    }
                    if (this.folderId == i3 || tLRPC$Dialog.folder_id == i3) {
                        this.canUnarchiveCount++;
                    } else if (!(j == ((long) clientUserId) || j == 777000 || getMessagesController().isPromoDialog(j, false))) {
                        i7++;
                    }
                    int i10 = (int) j;
                    int i11 = i6;
                    int i12 = i7;
                    int i13 = (int) (j >> 32);
                    if (i10 <= 0 || i10 == clientUserId || MessagesController.isSupportUser(getMessagesController().getUser(Integer.valueOf(i10)))) {
                        i9++;
                    } else {
                        if (notificationsSettings.getBoolean("dialog_bar_report" + j, true)) {
                            this.canReportSpamCount++;
                        }
                    }
                    if (DialogObject.isChannel(tLRPC$Dialog)) {
                        TLRPC$Chat chat = getMessagesController().getChat(Integer.valueOf(-i10));
                        if (getMessagesController().isPromoDialog(tLRPC$Dialog.id, true)) {
                            this.canClearCacheCount++;
                            if (getMessagesController().promoDialogType == 1) {
                                i5++;
                                this.canDeletePsaSelected = true;
                            }
                            i6 = i11;
                        } else {
                            if (isDialogPinned) {
                                i8++;
                            } else {
                                this.canPinCount++;
                            }
                            if (chat == null || !chat.megagroup) {
                                this.canClearCacheCount++;
                            } else if (TextUtils.isEmpty(chat.username)) {
                                i6 = i11 + 1;
                                i5++;
                            } else {
                                this.canClearCacheCount++;
                            }
                            i6 = i11;
                            i5++;
                        }
                    } else {
                        boolean z3 = i10 < 0 && i13 != 1;
                        if (z3) {
                            getMessagesController().getChat(Integer.valueOf(-i10));
                        }
                        if (i10 == 0) {
                            TLRPC$EncryptedChat encryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf(i13));
                            tLRPC$User = encryptedChat != null ? getMessagesController().getUser(Integer.valueOf(encryptedChat.user_id)) : new TLRPC$TL_userEmpty();
                        } else {
                            tLRPC$User = (z3 || i10 <= 0 || i13 == 1) ? null : getMessagesController().getUser(Integer.valueOf(i10));
                        }
                        if (tLRPC$User != null && tLRPC$User.bot) {
                            boolean isSupportUser = MessagesController.isSupportUser(tLRPC$User);
                        }
                        if (isDialogPinned) {
                            i8++;
                        } else {
                            this.canPinCount++;
                        }
                        i6 = i11 + 1;
                        i5++;
                    }
                    i7 = i12;
                }
                i4++;
                size = i2;
            }
            int i14 = size;
            int i15 = i6;
            if (i5 != size) {
                this.deleteItem.setVisibility(8);
            } else {
                this.deleteItem.setVisibility(0);
            }
            int i16 = this.canClearCacheCount;
            if ((i16 == 0 || i16 == size) && (i15 == 0 || i15 == size)) {
                this.clearItem.setVisibility(0);
                if (this.canClearCacheCount != 0) {
                    this.clearItem.setText(LocaleController.getString("ClearHistoryCache", NUM));
                } else {
                    this.clearItem.setText(LocaleController.getString("ClearHistory", NUM));
                }
            } else {
                this.clearItem.setVisibility(8);
            }
            if (this.canUnarchiveCount != 0) {
                String string = LocaleController.getString("Unarchive", NUM);
                this.archiveItem.setTextAndIcon(string, NUM);
                this.archive2Item.setIcon(NUM);
                this.archive2Item.setContentDescription(string);
                FilterTabsView filterTabsView2 = this.filterTabsView;
                if (filterTabsView2 == null || filterTabsView2.getVisibility() != 0) {
                    this.archiveItem.setVisibility(0);
                    this.archive2Item.setVisibility(8);
                } else {
                    this.archive2Item.setVisibility(0);
                    this.archiveItem.setVisibility(8);
                }
            } else if (i7 != 0) {
                String string2 = LocaleController.getString("Archive", NUM);
                this.archiveItem.setTextAndIcon(string2, NUM);
                this.archive2Item.setIcon(NUM);
                this.archive2Item.setContentDescription(string2);
                FilterTabsView filterTabsView3 = this.filterTabsView;
                if (filterTabsView3 == null || filterTabsView3.getVisibility() != 0) {
                    this.archiveItem.setVisibility(0);
                    this.archive2Item.setVisibility(8);
                } else {
                    this.archive2Item.setVisibility(0);
                    this.archiveItem.setVisibility(8);
                }
            } else {
                this.archiveItem.setVisibility(8);
                this.archive2Item.setVisibility(8);
            }
            if (this.canPinCount + i8 != size) {
                this.pinItem.setVisibility(8);
                this.pin2Item.setVisibility(8);
                i = 0;
            } else {
                FilterTabsView filterTabsView4 = this.filterTabsView;
                if (filterTabsView4 == null || filterTabsView4.getVisibility() != 0) {
                    i = 0;
                    this.pinItem.setVisibility(0);
                    this.pin2Item.setVisibility(8);
                } else {
                    i = 0;
                    this.pin2Item.setVisibility(0);
                    this.pinItem.setVisibility(8);
                }
            }
            if (i9 != 0) {
                this.blockItem.setVisibility(8);
            } else {
                this.blockItem.setVisibility(i);
            }
            FilterTabsView filterTabsView5 = this.filterTabsView;
            if (filterTabsView5 == null || filterTabsView5.getVisibility() != 0 || this.filterTabsView.getCurrentTabId() == Integer.MAX_VALUE) {
                this.removeFromFolderItem.setVisibility(8);
            } else {
                this.removeFromFolderItem.setVisibility(0);
            }
            FilterTabsView filterTabsView6 = this.filterTabsView;
            if (filterTabsView6 == null || filterTabsView6.getVisibility() != 0 || this.filterTabsView.getCurrentTabId() != Integer.MAX_VALUE || FiltersListBottomSheet.getCanAddDialogFilters(this, this.selectedDialogs).isEmpty()) {
                this.addToFolderItem.setVisibility(8);
            } else {
                this.addToFolderItem.setVisibility(0);
            }
            if (this.canUnmuteCount != 0) {
                this.muteItem.setIcon(NUM);
                this.muteItem.setContentDescription(LocaleController.getString("ChatsUnmute", NUM));
            } else {
                this.muteItem.setIcon(NUM);
                this.muteItem.setContentDescription(LocaleController.getString("ChatsMute", NUM));
            }
            if (this.canReadCount != 0) {
                this.readItem.setTextAndIcon(LocaleController.getString("MarkAsRead", NUM), NUM);
            } else {
                this.readItem.setTextAndIcon(LocaleController.getString("MarkAsUnread", NUM), NUM);
            }
            if (this.canPinCount != 0) {
                this.pinItem.setIcon(NUM);
                this.pinItem.setContentDescription(LocaleController.getString("PinToTop", NUM));
                this.pin2Item.setText(LocaleController.getString("DialogPin", NUM));
                return;
            }
            this.pinItem.setIcon(NUM);
            this.pinItem.setContentDescription(LocaleController.getString("UnpinFromTop", NUM));
            this.pin2Item.setText(LocaleController.getString("DialogUnpin", NUM));
        }
    }

    /* access modifiers changed from: private */
    public boolean validateSlowModeDialog(long j) {
        int i;
        TLRPC$Chat chat;
        ChatActivityEnterView chatActivityEnterView;
        if ((this.messagesCount <= 1 && ((chatActivityEnterView = this.commentView) == null || chatActivityEnterView.getVisibility() != 0 || TextUtils.isEmpty(this.commentView.getFieldText()))) || (i = (int) j) >= 0 || (chat = getMessagesController().getChat(Integer.valueOf(-i))) == null || ChatObject.hasAdminRights(chat) || !chat.slowmode_enabled) {
            return true;
        }
        AlertsCreator.showSimpleAlert(this, LocaleController.getString("Slowmode", NUM), LocaleController.getString("SlowmodeSendError", NUM));
        return false;
    }

    private void showOrUpdateActionMode(TLRPC$Dialog tLRPC$Dialog, View view) {
        addOrRemoveSelectedDialog(tLRPC$Dialog.id, view);
        boolean z = true;
        if (!this.actionBar.isActionModeShowed()) {
            createActionMode();
            this.actionBar.showActionMode();
            resetScroll();
            if (this.menuDrawable != null) {
                this.actionBar.setBackButtonContentDescription(LocaleController.getString("AccDescrGoBack", NUM));
            }
            if (getPinnedCount() > 1) {
                if (this.viewPages != null) {
                    int i = 0;
                    while (true) {
                        ViewPage[] viewPageArr = this.viewPages;
                        if (i >= viewPageArr.length) {
                            break;
                        }
                        viewPageArr[i].dialogsAdapter.onReorderStateChanged(true);
                        i++;
                    }
                }
                updateVisibleRows(131072);
            }
            AnimatorSet animatorSet = new AnimatorSet();
            ArrayList arrayList = new ArrayList();
            for (int i2 = 0; i2 < this.actionModeViews.size(); i2++) {
                View view2 = this.actionModeViews.get(i2);
                view2.setPivotY((float) (ActionBar.getCurrentActionBarHeight() / 2));
                AndroidUtilities.clearDrawableAnimation(view2);
                arrayList.add(ObjectAnimator.ofFloat(view2, View.SCALE_Y, new float[]{0.1f, 1.0f}));
            }
            animatorSet.playTogether(arrayList);
            animatorSet.setDuration(200);
            animatorSet.start();
            ValueAnimator valueAnimator = this.actionBarColorAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.progressToActionMode, 1.0f});
            this.actionBarColorAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    DialogsActivity.this.lambda$showOrUpdateActionMode$29$DialogsActivity(valueAnimator);
                }
            });
            this.actionBarColorAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.actionBarColorAnimator.setDuration(200);
            this.actionBarColorAnimator.start();
            FilterTabsView filterTabsView2 = this.filterTabsView;
            if (filterTabsView2 != null) {
                filterTabsView2.animateColorsTo("profile_tabSelectedLine", "profile_tabSelectedText", "profile_tabText", "profile_tabSelector", "actionBarActionModeDefault");
            }
            MenuDrawable menuDrawable2 = this.menuDrawable;
            if (menuDrawable2 != null) {
                menuDrawable2.setRotateToBack(false);
                this.menuDrawable.setRotation(1.0f, true);
            } else {
                BackDrawable backDrawable2 = this.backDrawable;
                if (backDrawable2 != null) {
                    backDrawable2.setRotation(1.0f, true);
                }
            }
            z = false;
        } else if (this.selectedDialogs.isEmpty()) {
            hideActionMode(true);
            return;
        }
        updateCounters(false);
        this.selectedDialogsCountTextView.setNumber(this.selectedDialogs.size(), z);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showOrUpdateActionMode$29 */
    public /* synthetic */ void lambda$showOrUpdateActionMode$29$DialogsActivity(ValueAnimator valueAnimator) {
        this.progressToActionMode = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        for (int i = 0; i < this.actionBar.getChildCount(); i++) {
            if (!(this.actionBar.getChildAt(i).getVisibility() != 0 || this.actionBar.getChildAt(i) == this.actionBar.getActionMode() || this.actionBar.getChildAt(i) == this.actionBar.getBackButton())) {
                this.actionBar.getChildAt(i).setAlpha(1.0f - this.progressToActionMode);
            }
        }
        this.fragmentView.invalidate();
    }

    /* access modifiers changed from: private */
    public void closeSearch() {
        if (AndroidUtilities.isTablet()) {
            ActionBar actionBar = this.actionBar;
            if (actionBar != null) {
                actionBar.closeSearchField();
            }
            TLObject tLObject = this.searchObject;
            if (tLObject != null) {
                this.searchViewPager.dialogsSearchAdapter.putRecentSearch(this.searchDialogId, tLObject);
                this.searchObject = null;
                return;
            }
            return;
        }
        this.closeSearchFieldOnHide = true;
    }

    /* access modifiers changed from: protected */
    public RecyclerListView getListView() {
        return this.viewPages[0].listView;
    }

    /* access modifiers changed from: protected */
    public RecyclerListView getSearchListView() {
        return this.searchViewPager.searchListView;
    }

    /* access modifiers changed from: private */
    public UndoView getUndoView() {
        if (this.undoView[0].getVisibility() == 0) {
            UndoView[] undoViewArr = this.undoView;
            UndoView undoView2 = undoViewArr[0];
            undoViewArr[0] = undoViewArr[1];
            undoViewArr[1] = undoView2;
            undoView2.hide(true, 2);
            ContentView contentView = (ContentView) this.fragmentView;
            contentView.removeView(this.undoView[0]);
            contentView.addView(this.undoView[0]);
        }
        return this.undoView[0];
    }

    private void updateProxyButton(boolean z) {
        ActionBarMenuItem actionBarMenuItem;
        if (this.proxyDrawable != null) {
            ActionBarMenuItem actionBarMenuItem2 = this.doneItem;
            if (actionBarMenuItem2 == null || actionBarMenuItem2.getVisibility() != 0) {
                boolean z2 = false;
                SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                boolean z3 = sharedPreferences.getBoolean("proxy_enabled", false) && !TextUtils.isEmpty(sharedPreferences.getString("proxy_ip", ""));
                if (z3 || (getMessagesController().blockedCountry && !SharedConfig.proxyList.isEmpty())) {
                    if (!this.actionBar.isSearchFieldVisible() && ((actionBarMenuItem = this.doneItem) == null || actionBarMenuItem.getVisibility() != 0)) {
                        this.proxyItem.setVisibility(0);
                    }
                    this.proxyItemVisible = true;
                    ProxyDrawable proxyDrawable2 = this.proxyDrawable;
                    int i = this.currentConnectionState;
                    if (i == 3 || i == 5) {
                        z2 = true;
                    }
                    proxyDrawable2.setConnected(z3, z2, z);
                    return;
                }
                this.proxyItemVisible = false;
                this.proxyItem.setVisibility(8);
            }
        }
    }

    /* access modifiers changed from: private */
    public void showDoneItem(final boolean z) {
        if (this.doneItem != null) {
            AnimatorSet animatorSet = this.doneItemAnimator;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.doneItemAnimator = null;
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.doneItemAnimator = animatorSet2;
            animatorSet2.setDuration(180);
            if (z) {
                this.doneItem.setVisibility(0);
            } else {
                this.doneItem.setSelected(false);
                Drawable background = this.doneItem.getBackground();
                if (background != null) {
                    background.setState(StateSet.NOTHING);
                    background.jumpToCurrentState();
                }
                ActionBarMenuItem actionBarMenuItem = this.searchItem;
                if (actionBarMenuItem != null) {
                    actionBarMenuItem.setVisibility(0);
                }
                ActionBarMenuItem actionBarMenuItem2 = this.proxyItem;
                if (actionBarMenuItem2 != null && this.proxyItemVisible) {
                    actionBarMenuItem2.setVisibility(0);
                }
                ActionBarMenuItem actionBarMenuItem3 = this.passcodeItem;
                if (actionBarMenuItem3 != null && this.passcodeItemVisible) {
                    actionBarMenuItem3.setVisibility(0);
                }
            }
            ArrayList arrayList = new ArrayList();
            ActionBarMenuItem actionBarMenuItem4 = this.doneItem;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            float f = 1.0f;
            fArr[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem4, property, fArr));
            if (this.proxyItemVisible) {
                ActionBarMenuItem actionBarMenuItem5 = this.proxyItem;
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                fArr2[0] = z ? 0.0f : 1.0f;
                arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem5, property2, fArr2));
            }
            if (this.passcodeItemVisible) {
                ActionBarMenuItem actionBarMenuItem6 = this.passcodeItem;
                Property property3 = View.ALPHA;
                float[] fArr3 = new float[1];
                fArr3[0] = z ? 0.0f : 1.0f;
                arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem6, property3, fArr3));
            }
            ActionBarMenuItem actionBarMenuItem7 = this.searchItem;
            Property property4 = View.ALPHA;
            float[] fArr4 = new float[1];
            if (z) {
                f = 0.0f;
            }
            fArr4[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem7, property4, fArr4));
            this.doneItemAnimator.playTogether(arrayList);
            this.doneItemAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AnimatorSet unused = DialogsActivity.this.doneItemAnimator = null;
                    if (z) {
                        if (DialogsActivity.this.searchItem != null) {
                            DialogsActivity.this.searchItem.setVisibility(4);
                        }
                        if (DialogsActivity.this.proxyItem != null && DialogsActivity.this.proxyItemVisible) {
                            DialogsActivity.this.proxyItem.setVisibility(4);
                        }
                        if (DialogsActivity.this.passcodeItem != null && DialogsActivity.this.passcodeItemVisible) {
                            DialogsActivity.this.passcodeItem.setVisibility(4);
                        }
                    } else if (DialogsActivity.this.doneItem != null) {
                        DialogsActivity.this.doneItem.setVisibility(8);
                    }
                }
            });
            this.doneItemAnimator.start();
        }
    }

    /* access modifiers changed from: private */
    public void updateSelectedCount() {
        if (this.commentView != null) {
            if (this.selectedDialogs.isEmpty()) {
                if (this.initialDialogsType == 3 && this.selectAlertString == null) {
                    this.actionBar.setTitle(LocaleController.getString("ForwardTo", NUM));
                } else {
                    this.actionBar.setTitle(LocaleController.getString("SelectChat", NUM));
                }
                if (this.commentView.getTag() != null) {
                    this.commentView.hidePopup(false);
                    this.commentView.closeKeyboard();
                    AnimatorSet animatorSet = new AnimatorSet();
                    ChatActivityEnterView chatActivityEnterView = this.commentView;
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(chatActivityEnterView, View.TRANSLATION_Y, new float[]{0.0f, (float) chatActivityEnterView.getMeasuredHeight()})});
                    animatorSet.setDuration(180);
                    animatorSet.setInterpolator(new DecelerateInterpolator());
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            DialogsActivity.this.commentView.setVisibility(8);
                        }
                    });
                    animatorSet.start();
                    this.commentView.setTag((Object) null);
                    this.fragmentView.requestLayout();
                    return;
                }
                return;
            }
            if (this.commentView.getTag() == null) {
                this.commentView.setFieldText("");
                this.commentView.setVisibility(0);
                AnimatorSet animatorSet2 = new AnimatorSet();
                ChatActivityEnterView chatActivityEnterView2 = this.commentView;
                animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(chatActivityEnterView2, View.TRANSLATION_Y, new float[]{(float) chatActivityEnterView2.getMeasuredHeight(), 0.0f})});
                animatorSet2.setDuration(180);
                animatorSet2.setInterpolator(new DecelerateInterpolator());
                animatorSet2.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        DialogsActivity.this.commentView.setTag(2);
                        DialogsActivity.this.commentView.requestLayout();
                    }
                });
                animatorSet2.start();
                this.commentView.setTag(1);
            }
            this.actionBar.setTitle(LocaleController.formatPluralString("Recipient", this.selectedDialogs.size()));
        }
    }

    @TargetApi(23)
    private void askForPermissons(boolean z) {
        Activity parentActivity = getParentActivity();
        if (parentActivity != null) {
            ArrayList arrayList = new ArrayList();
            if (getUserConfig().syncContacts && this.askAboutContacts && parentActivity.checkSelfPermission("android.permission.READ_CONTACTS") != 0) {
                if (z) {
                    AlertDialog create = AlertsCreator.createContactsPermissionDialog(parentActivity, new MessagesStorage.IntCallback() {
                        public final void run(int i) {
                            DialogsActivity.this.lambda$askForPermissons$30$DialogsActivity(i);
                        }
                    }).create();
                    this.permissionDialog = create;
                    showDialog(create);
                    return;
                }
                arrayList.add("android.permission.READ_CONTACTS");
                arrayList.add("android.permission.WRITE_CONTACTS");
                arrayList.add("android.permission.GET_ACCOUNTS");
            }
            if (parentActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                arrayList.add("android.permission.READ_EXTERNAL_STORAGE");
                arrayList.add("android.permission.WRITE_EXTERNAL_STORAGE");
            }
            if (!arrayList.isEmpty()) {
                try {
                    parentActivity.requestPermissions((String[]) arrayList.toArray(new String[0]), 1);
                } catch (Exception unused) {
                }
            } else if (this.askingForPermissions) {
                this.askingForPermissions = false;
                showFiltersHint();
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$askForPermissons$30 */
    public /* synthetic */ void lambda$askForPermissons$30$DialogsActivity(int i) {
        this.askAboutContacts = i != 0;
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askAboutContacts", this.askAboutContacts).commit();
        askForPermissons(false);
    }

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
        AlertDialog alertDialog = this.permissionDialog;
        if (alertDialog != null && dialog == alertDialog && getParentActivity() != null) {
            askForPermissons(false);
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        FrameLayout frameLayout;
        super.onConfigurationChanged(configuration);
        ActionBarPopupWindow actionBarPopupWindow = this.scrimPopupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.dismiss();
        }
        if (!this.onlySelect && (frameLayout = this.floatingButtonContainer) != null) {
            frameLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    DialogsActivity dialogsActivity = DialogsActivity.this;
                    float unused = dialogsActivity.floatingButtonTranslation = dialogsActivity.floatingHidden ? (float) AndroidUtilities.dp(100.0f) : 0.0f;
                    DialogsActivity.this.updateFloatingButtonOffset();
                    DialogsActivity.this.floatingButtonContainer.setClickable(!DialogsActivity.this.floatingHidden);
                    if (DialogsActivity.this.floatingButtonContainer != null) {
                        DialogsActivity.this.floatingButtonContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i == 1) {
            for (int i2 = 0; i2 < strArr.length; i2++) {
                if (iArr.length > i2) {
                    String str = strArr[i2];
                    str.hashCode();
                    if (!str.equals("android.permission.WRITE_EXTERNAL_STORAGE")) {
                        if (str.equals("android.permission.READ_CONTACTS")) {
                            if (iArr[i2] == 0) {
                                getContactsController().forceImportContacts();
                            } else {
                                SharedPreferences.Editor edit = MessagesController.getGlobalNotificationsSettings().edit();
                                this.askAboutContacts = false;
                                edit.putBoolean("askAboutContacts", false).commit();
                            }
                        }
                    } else if (iArr[i2] == 0) {
                        ImageLoader.getInstance().checkMediaPaths();
                    }
                }
            }
            if (this.askingForPermissions) {
                this.askingForPermissions = false;
                showFiltersHint();
            }
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        DialogsSearchAdapter dialogsSearchAdapter;
        DialogsSearchAdapter dialogsSearchAdapter2;
        int i3 = 0;
        if (i == NotificationCenter.dialogsNeedReload) {
            if (this.viewPages != null && !this.dialogsListFrozen) {
                while (true) {
                    ViewPage[] viewPageArr = this.viewPages;
                    if (i3 >= viewPageArr.length) {
                        break;
                    }
                    if (viewPageArr[i3].getVisibility() == 0) {
                        if (this.viewPages[i3].dialogsAdapter.isDataSetChanged() || objArr.length > 0) {
                            this.viewPages[i3].dialogsAdapter.notifyDataSetChanged();
                        } else {
                            updateVisibleRows(2048);
                        }
                        try {
                            this.viewPages[i3].listView.setEmptyView(this.folderId == 0 ? this.viewPages[i3].progressView : null);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                        checkListLoad(this.viewPages[i3]);
                    }
                    i3++;
                }
                FilterTabsView filterTabsView2 = this.filterTabsView;
                if (filterTabsView2 != null && filterTabsView2.getVisibility() == 0) {
                    this.filterTabsView.checkTabsCounter();
                }
            }
        } else if (i == NotificationCenter.dialogsUnreadCounterChanged) {
            FilterTabsView filterTabsView3 = this.filterTabsView;
            if (filterTabsView3 != null && filterTabsView3.getVisibility() == 0) {
                this.filterTabsView.notifyTabCounterChanged(Integer.MAX_VALUE);
            }
        } else if (i == NotificationCenter.emojiDidLoad) {
            updateVisibleRows(0);
            FilterTabsView filterTabsView4 = this.filterTabsView;
            if (filterTabsView4 != null) {
                filterTabsView4.getTabsContainer().invalidateViews();
            }
        } else if (i == NotificationCenter.closeSearchByActiveAction) {
            ActionBar actionBar = this.actionBar;
            if (actionBar != null) {
                actionBar.closeSearchField();
            }
        } else if (i == NotificationCenter.proxySettingsChanged) {
            updateProxyButton(false);
        } else if (i == NotificationCenter.updateInterfaces) {
            Integer num = objArr[0];
            updateVisibleRows(num.intValue());
            FilterTabsView filterTabsView5 = this.filterTabsView;
            if (!(filterTabsView5 == null || filterTabsView5.getVisibility() != 0 || (num.intValue() & 256) == 0)) {
                this.filterTabsView.checkTabsCounter();
            }
            if (this.viewPages != null) {
                while (i3 < this.viewPages.length) {
                    if ((num.intValue() & 4) != 0) {
                        this.viewPages[i3].dialogsAdapter.sortOnlineContacts(true);
                    }
                    i3++;
                }
            }
        } else if (i == NotificationCenter.appDidLogout) {
            dialogsLoaded[this.currentAccount] = false;
        } else if (i == NotificationCenter.encryptedChatUpdated) {
            updateVisibleRows(0);
        } else if (i == NotificationCenter.contactsDidLoad) {
            if (this.viewPages != null && !this.dialogsListFrozen) {
                int i4 = 0;
                boolean z = false;
                while (true) {
                    ViewPage[] viewPageArr2 = this.viewPages;
                    if (i4 >= viewPageArr2.length) {
                        break;
                    }
                    if (!viewPageArr2[i4].isDefaultDialogType() || !getMessagesController().getDialogs(this.folderId).isEmpty()) {
                        z = true;
                    } else {
                        this.viewPages[i4].dialogsAdapter.notifyDataSetChanged();
                    }
                    i4++;
                }
                if (z) {
                    updateVisibleRows(0);
                }
            }
        } else if (i == NotificationCenter.openedChatChanged) {
            if (this.viewPages != null) {
                int i5 = 0;
                while (true) {
                    ViewPage[] viewPageArr3 = this.viewPages;
                    if (i5 < viewPageArr3.length) {
                        if (viewPageArr3[i5].isDefaultDialogType() && AndroidUtilities.isTablet()) {
                            boolean booleanValue = objArr[1].booleanValue();
                            long longValue = objArr[0].longValue();
                            if (!booleanValue) {
                                this.openedDialogId = longValue;
                            } else if (longValue == this.openedDialogId) {
                                this.openedDialogId = 0;
                            }
                            this.viewPages[i5].dialogsAdapter.setOpenedDialogId(this.openedDialogId);
                        }
                        i5++;
                    } else {
                        updateVisibleRows(512);
                        return;
                    }
                }
            }
        } else if (i == NotificationCenter.notificationsSettingsUpdated) {
            updateVisibleRows(0);
        } else if (i == NotificationCenter.messageReceivedByAck || i == NotificationCenter.messageReceivedByServer || i == NotificationCenter.messageSendError) {
            updateVisibleRows(4096);
        } else if (i == NotificationCenter.didSetPasscode) {
            updatePasscodeButton();
        } else if (i == NotificationCenter.needReloadRecentDialogsSearch) {
            SearchViewPager searchViewPager2 = this.searchViewPager;
            if (searchViewPager2 != null && (dialogsSearchAdapter2 = searchViewPager2.dialogsSearchAdapter) != null) {
                dialogsSearchAdapter2.loadRecentSearch();
            }
        } else if (i == NotificationCenter.replyMessagesDidLoad) {
            updateVisibleRows(32768);
        } else if (i == NotificationCenter.reloadHints) {
            SearchViewPager searchViewPager3 = this.searchViewPager;
            if (searchViewPager3 != null && (dialogsSearchAdapter = searchViewPager3.dialogsSearchAdapter) != null) {
                dialogsSearchAdapter.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.didUpdateConnectionState) {
            int connectionState = AccountInstance.getInstance(i2).getConnectionsManager().getConnectionState();
            if (this.currentConnectionState != connectionState) {
                this.currentConnectionState = connectionState;
                updateProxyButton(true);
            }
        } else if (i == NotificationCenter.needDeleteDialog) {
            if (this.fragmentView != null && !this.isPaused) {
                long longValue2 = objArr[0].longValue();
                $$Lambda$DialogsActivity$pw7vgq4RhS9eFzQZ1aQZh6GxJ4 r3 = new Runnable(objArr[2], longValue2, objArr[3].booleanValue(), objArr[1]) {
                    public final /* synthetic */ TLRPC$Chat f$1;
                    public final /* synthetic */ long f$2;
                    public final /* synthetic */ boolean f$3;
                    public final /* synthetic */ TLRPC$User f$4;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r5;
                        this.f$4 = r6;
                    }

                    public final void run() {
                        DialogsActivity.this.lambda$didReceivedNotification$31$DialogsActivity(this.f$1, this.f$2, this.f$3, this.f$4);
                    }
                };
                if (this.undoView[0] != null) {
                    getUndoView().showWithAction(longValue2, 1, (Runnable) r3);
                } else {
                    r3.run();
                }
            }
        } else if (i == NotificationCenter.folderBecomeEmpty) {
            int intValue = objArr[0].intValue();
            int i6 = this.folderId;
            if (i6 == intValue && i6 != 0) {
                finishFragment();
            }
        } else if (i == NotificationCenter.dialogFiltersUpdated) {
            updateFilterTabs(true);
        } else if (i == NotificationCenter.filterSettingsUpdated) {
            showFiltersHint();
        } else if (i == NotificationCenter.newSuggestionsAvailable) {
            showNextSupportedSuggestion();
        } else if (i == NotificationCenter.messagesDeleted && this.searchIsShowed && this.searchViewPager != null) {
            this.searchViewPager.messagesDeleted(objArr[1].intValue(), objArr[0]);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$31 */
    public /* synthetic */ void lambda$didReceivedNotification$31$DialogsActivity(TLRPC$Chat tLRPC$Chat, long j, boolean z, TLRPC$User tLRPC$User) {
        if (tLRPC$Chat == null) {
            getMessagesController().deleteDialog(j, 0, z);
            if (tLRPC$User != null && tLRPC$User.bot) {
                getMessagesController().blockPeer(tLRPC$User.id);
            }
        } else if (ChatObject.isNotInChat(tLRPC$Chat)) {
            getMessagesController().deleteDialog(j, 0, z);
        } else {
            getMessagesController().deleteUserFromChat((int) (-j), getMessagesController().getUser(Integer.valueOf(getUserConfig().getClientUserId())), (TLRPC$ChatFull) null, false, z);
        }
        MessagesController.getInstance(this.currentAccount).checkIfFolderEmpty(this.folderId);
    }

    private void showNextSupportedSuggestion() {
        if (this.showingSuggestion == null) {
            for (String next : getMessagesController().pendingSuggestions) {
                if (showSuggestion(next)) {
                    this.showingSuggestion = next;
                    return;
                }
            }
        }
    }

    private void onSuggestionDismiss() {
        if (this.showingSuggestion != null) {
            getMessagesController().removeSuggestion(this.showingSuggestion);
            this.showingSuggestion = null;
            showNextSupportedSuggestion();
        }
    }

    private boolean showSuggestion(String str) {
        if (!"AUTOARCHIVE_POPULAR".equals(str)) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("HideNewChatsAlertTitle", NUM));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("HideNewChatsAlertText", NUM)));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(LocaleController.getString("GoToSettings", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                DialogsActivity.this.lambda$showSuggestion$32$DialogsActivity(dialogInterface, i);
            }
        });
        showDialog(builder.create(), new DialogInterface.OnDismissListener() {
            public final void onDismiss(DialogInterface dialogInterface) {
                DialogsActivity.this.lambda$showSuggestion$33$DialogsActivity(dialogInterface);
            }
        });
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showSuggestion$32 */
    public /* synthetic */ void lambda$showSuggestion$32$DialogsActivity(DialogInterface dialogInterface, int i) {
        presentFragment(new PrivacySettingsActivity());
        AndroidUtilities.scrollToFragmentRow(this.parentLayout, "newChatsRow");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showSuggestion$33 */
    public /* synthetic */ void lambda$showSuggestion$33$DialogsActivity(DialogInterface dialogInterface) {
        onSuggestionDismiss();
    }

    private void showFiltersHint() {
        if (!this.askingForPermissions && getMessagesController().dialogFiltersLoaded && getMessagesController().showFiltersTooltip && this.filterTabsView != null && getMessagesController().dialogFilters.isEmpty() && !this.isPaused && getUserConfig().filtersLoaded && !this.inPreviewMode) {
            SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
            if (!globalMainSettings.getBoolean("filterhint", false)) {
                globalMainSettings.edit().putBoolean("filterhint", true).commit();
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        DialogsActivity.this.lambda$showFiltersHint$35$DialogsActivity();
                    }
                }, 1000);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$34 */
    public /* synthetic */ void lambda$null$34$DialogsActivity() {
        presentFragment(new FiltersSetupActivity());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showFiltersHint$35 */
    public /* synthetic */ void lambda$showFiltersHint$35$DialogsActivity() {
        getUndoView().showWithAction(0, 15, (Runnable) null, new Runnable() {
            public final void run() {
                DialogsActivity.this.lambda$null$34$DialogsActivity();
            }
        });
    }

    /* access modifiers changed from: private */
    public void setDialogsListFrozen(boolean z) {
        if (this.viewPages != null && this.dialogsListFrozen != z) {
            if (z) {
                frozenDialogsList = new ArrayList<>(getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, false));
            } else {
                frozenDialogsList = null;
            }
            this.dialogsListFrozen = z;
            this.viewPages[0].dialogsAdapter.setDialogsListFrozen(z);
            if (!z) {
                this.viewPages[0].dialogsAdapter.notifyDataSetChanged();
            }
        }
    }

    public static ArrayList<TLRPC$Dialog> getDialogsArray(int i, int i2, int i3, boolean z) {
        ArrayList<TLRPC$Dialog> arrayList;
        if (z && (arrayList = frozenDialogsList) != null) {
            return arrayList;
        }
        MessagesController messagesController = AccountInstance.getInstance(i).getMessagesController();
        if (i2 == 0) {
            return messagesController.getDialogs(i3);
        }
        char c = 1;
        if (i2 == 1) {
            return messagesController.dialogsServerOnly;
        }
        if (i2 == 2) {
            return messagesController.dialogsCanAddUsers;
        }
        if (i2 == 3) {
            return messagesController.dialogsForward;
        }
        if (i2 == 4) {
            return messagesController.dialogsUsersOnly;
        }
        if (i2 == 5) {
            return messagesController.dialogsChannelsOnly;
        }
        if (i2 == 6) {
            return messagesController.dialogsGroupsOnly;
        }
        if (i2 == 7 || i2 == 8) {
            MessagesController.DialogFilter[] dialogFilterArr = messagesController.selectedDialogFilter;
            if (i2 == 7) {
                c = 0;
            }
            MessagesController.DialogFilter dialogFilter = dialogFilterArr[c];
            if (dialogFilter == null) {
                return messagesController.getDialogs(i3);
            }
            return dialogFilter.dialogs;
        } else if (i2 == 9) {
            return messagesController.dialogsForBlock;
        } else {
            return null;
        }
    }

    public void setSideMenu(RecyclerView recyclerView) {
        this.sideMenu = recyclerView;
        recyclerView.setBackgroundColor(Theme.getColor("chats_menuBackground"));
        this.sideMenu.setGlowColor(Theme.getColor("chats_menuBackground"));
    }

    /* access modifiers changed from: private */
    public void updatePasscodeButton() {
        if (this.passcodeItem != null) {
            if (SharedConfig.passcodeHash.length() == 0 || this.searching) {
                this.passcodeItem.setVisibility(8);
                this.passcodeItemVisible = false;
                return;
            }
            ActionBarMenuItem actionBarMenuItem = this.doneItem;
            if (actionBarMenuItem == null || actionBarMenuItem.getVisibility() != 0) {
                this.passcodeItem.setVisibility(0);
            }
            this.passcodeItemVisible = true;
            if (SharedConfig.appLocked) {
                this.passcodeItem.setIcon(NUM);
                this.passcodeItem.setContentDescription(LocaleController.getString("AccDescrPasscodeUnlock", NUM));
                return;
            }
            this.passcodeItem.setIcon(NUM);
            this.passcodeItem.setContentDescription(LocaleController.getString("AccDescrPasscodeLock", NUM));
        }
    }

    /* access modifiers changed from: private */
    public void hideFloatingButton(boolean z) {
        if (this.floatingHidden != z) {
            this.floatingHidden = z;
            AnimatorSet animatorSet = new AnimatorSet();
            float[] fArr = new float[2];
            fArr[0] = this.floatingButtonHideProgress;
            fArr[1] = this.floatingHidden ? 1.0f : 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    DialogsActivity.this.lambda$hideFloatingButton$36$DialogsActivity(valueAnimator);
                }
            });
            animatorSet.playTogether(new Animator[]{ofFloat});
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(this.floatingInterpolator);
            this.floatingButtonContainer.setClickable(!z);
            animatorSet.start();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$hideFloatingButton$36 */
    public /* synthetic */ void lambda$hideFloatingButton$36$DialogsActivity(ValueAnimator valueAnimator) {
        this.floatingButtonHideProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.floatingButtonTranslation = ((float) AndroidUtilities.dp(100.0f)) * this.floatingButtonHideProgress;
        updateFloatingButtonOffset();
    }

    /* access modifiers changed from: private */
    public void updateDialogIndices() {
        int indexOf;
        if (this.viewPages != null) {
            int i = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (i < viewPageArr.length) {
                    if (viewPageArr[i].getVisibility() == 0) {
                        ArrayList<TLRPC$Dialog> dialogsArray = getDialogsArray(this.currentAccount, this.viewPages[i].dialogsType, this.folderId, false);
                        int childCount = this.viewPages[i].listView.getChildCount();
                        for (int i2 = 0; i2 < childCount; i2++) {
                            View childAt = this.viewPages[i].listView.getChildAt(i2);
                            if (childAt instanceof DialogCell) {
                                DialogCell dialogCell = (DialogCell) childAt;
                                TLRPC$Dialog tLRPC$Dialog = getMessagesController().dialogs_dict.get(dialogCell.getDialogId());
                                if (tLRPC$Dialog != null && (indexOf = dialogsArray.indexOf(tLRPC$Dialog)) >= 0) {
                                    dialogCell.setDialogIndex(indexOf);
                                }
                            }
                        }
                    }
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateVisibleRows(int i) {
        if (!this.dialogsListFrozen) {
            for (int i2 = 0; i2 < 3; i2++) {
                RecyclerView recyclerView = null;
                if (i2 == 2) {
                    SearchViewPager searchViewPager2 = this.searchViewPager;
                    if (searchViewPager2 != null) {
                        recyclerView = searchViewPager2.searchListView;
                    }
                } else {
                    ViewPage[] viewPageArr = this.viewPages;
                    if (viewPageArr != null) {
                        if (i2 < viewPageArr.length) {
                            recyclerView = viewPageArr[i2].listView;
                        }
                        if (!(recyclerView == null || this.viewPages[i2].getVisibility() == 0)) {
                        }
                    }
                }
                if (recyclerView != null) {
                    int childCount = recyclerView.getChildCount();
                    for (int i3 = 0; i3 < childCount; i3++) {
                        View childAt = recyclerView.getChildAt(i3);
                        if (childAt instanceof DialogCell) {
                            if (recyclerView.getAdapter() != this.searchViewPager.dialogsSearchAdapter) {
                                DialogCell dialogCell = (DialogCell) childAt;
                                boolean z = true;
                                if ((131072 & i) != 0) {
                                    dialogCell.onReorderStateChanged(this.actionBar.isActionModeShowed(), true);
                                }
                                if ((65536 & i) != 0) {
                                    if ((i & 8192) == 0) {
                                        z = false;
                                    }
                                    dialogCell.setChecked(false, z);
                                } else {
                                    if ((i & 2048) != 0) {
                                        dialogCell.checkCurrentDialogIndex(this.dialogsListFrozen);
                                        if (this.viewPages[i2].isDefaultDialogType() && AndroidUtilities.isTablet()) {
                                            if (dialogCell.getDialogId() != this.openedDialogId) {
                                                z = false;
                                            }
                                            dialogCell.setDialogSelected(z);
                                        }
                                    } else if ((i & 512) == 0) {
                                        dialogCell.update(i);
                                    } else if (this.viewPages[i2].isDefaultDialogType() && AndroidUtilities.isTablet()) {
                                        if (dialogCell.getDialogId() != this.openedDialogId) {
                                            z = false;
                                        }
                                        dialogCell.setDialogSelected(z);
                                    }
                                    ArrayList<Long> arrayList = this.selectedDialogs;
                                    if (arrayList != null) {
                                        dialogCell.setChecked(arrayList.contains(Long.valueOf(dialogCell.getDialogId())), false);
                                    }
                                }
                            }
                        } else if (childAt instanceof UserCell) {
                            ((UserCell) childAt).update(i);
                        } else if (childAt instanceof ProfileSearchCell) {
                            ((ProfileSearchCell) childAt).update(i);
                        } else if (childAt instanceof RecyclerListView) {
                            RecyclerListView recyclerListView = (RecyclerListView) childAt;
                            int childCount2 = recyclerListView.getChildCount();
                            for (int i4 = 0; i4 < childCount2; i4++) {
                                View childAt2 = recyclerListView.getChildAt(i4);
                                if (childAt2 instanceof HintDialogCell) {
                                    ((HintDialogCell) childAt2).update(i);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void setDelegate(DialogsActivityDelegate dialogsActivityDelegate) {
        this.delegate = dialogsActivityDelegate;
    }

    public void setSearchString(String str) {
        this.searchString = str;
    }

    public void setInitialSearchString(String str) {
        this.initialSearchString = str;
    }

    public boolean isMainDialogList() {
        return this.delegate == null && this.searchString == null;
    }

    /* access modifiers changed from: private */
    public void didSelectResult(long j, boolean z, boolean z2) {
        String str;
        String str2;
        String str3;
        String str4;
        String str5;
        if (this.addToGroupAlertString == null && this.checkCanWrite) {
            int i = (int) j;
            if (i < 0) {
                int i2 = -i;
                TLRPC$Chat chat = getMessagesController().getChat(Integer.valueOf(i2));
                if (ChatObject.isChannel(chat) && !chat.megagroup && (this.cantSendToChannels || !ChatObject.isCanWriteToChannel(i2, this.currentAccount) || this.hasPoll == 2)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setTitle(LocaleController.getString("SendMessageTitle", NUM));
                    if (this.hasPoll == 2) {
                        builder.setMessage(LocaleController.getString("PublicPollCantForward", NUM));
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelCantSendMessage", NUM));
                    }
                    builder.setNegativeButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                    showDialog(builder.create());
                    return;
                }
            } else if (i == 0 && this.hasPoll != 0) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                builder2.setTitle(LocaleController.getString("SendMessageTitle", NUM));
                builder2.setMessage(LocaleController.getString("PollCantForwardSecretChat", NUM));
                builder2.setNegativeButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                showDialog(builder2.create());
                return;
            }
        }
        if (!z || ((this.selectAlertString == null || this.selectAlertStringGroup == null) && this.addToGroupAlertString == null)) {
            if (this.delegate != null) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(Long.valueOf(j));
                this.delegate.didSelectDialogs(this, arrayList, (CharSequence) null, z2);
                if (this.resetDelegate) {
                    this.delegate = null;
                    return;
                }
                return;
            }
            finishFragment();
        } else if (getParentActivity() != null) {
            AlertDialog.Builder builder3 = new AlertDialog.Builder((Context) getParentActivity());
            int i3 = (int) j;
            int i4 = (int) (j >> 32);
            if (i3 == 0) {
                TLRPC$User user = getMessagesController().getUser(Integer.valueOf(getMessagesController().getEncryptedChat(Integer.valueOf(i4)).user_id));
                if (user != null) {
                    str5 = LocaleController.getString("SendMessageTitle", NUM);
                    str4 = LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(user));
                    str2 = LocaleController.getString("Send", NUM);
                } else {
                    return;
                }
            } else if (i3 == getUserConfig().getClientUserId()) {
                str = LocaleController.getString("SendMessageTitle", NUM);
                str3 = LocaleController.formatStringSimple(this.selectAlertStringGroup, LocaleController.getString("SavedMessages", NUM));
                str2 = LocaleController.getString("Send", NUM);
                builder3.setTitle(str);
                builder3.setMessage(AndroidUtilities.replaceTags(str3));
                builder3.setPositiveButton(str2, new DialogInterface.OnClickListener(j) {
                    public final /* synthetic */ long f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        DialogsActivity.this.lambda$didSelectResult$37$DialogsActivity(this.f$1, dialogInterface, i);
                    }
                });
                builder3.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                showDialog(builder3.create());
            } else if (i3 > 0) {
                TLRPC$User user2 = getMessagesController().getUser(Integer.valueOf(i3));
                if (user2 != null && this.selectAlertString != null) {
                    str5 = LocaleController.getString("SendMessageTitle", NUM);
                    str4 = LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(user2));
                    str2 = LocaleController.getString("Send", NUM);
                } else {
                    return;
                }
            } else {
                TLRPC$Chat chat2 = getMessagesController().getChat(Integer.valueOf(-i3));
                if (chat2 != null) {
                    if (this.addToGroupAlertString != null) {
                        str5 = LocaleController.getString("AddToTheGroupAlertTitle", NUM);
                        str4 = LocaleController.formatStringSimple(this.addToGroupAlertString, chat2.title);
                        str2 = LocaleController.getString("Add", NUM);
                    } else {
                        str5 = LocaleController.getString("SendMessageTitle", NUM);
                        str4 = LocaleController.formatStringSimple(this.selectAlertStringGroup, chat2.title);
                        str2 = LocaleController.getString("Send", NUM);
                    }
                } else {
                    return;
                }
            }
            String str6 = str5;
            str3 = str4;
            str = str6;
            builder3.setTitle(str);
            builder3.setMessage(AndroidUtilities.replaceTags(str3));
            builder3.setPositiveButton(str2, new DialogInterface.OnClickListener(j) {
                public final /* synthetic */ long f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    DialogsActivity.this.lambda$didSelectResult$37$DialogsActivity(this.f$1, dialogInterface, i);
                }
            });
            builder3.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder3.create());
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didSelectResult$37 */
    public /* synthetic */ void lambda$didSelectResult$37$DialogsActivity(long j, DialogInterface dialogInterface, int i) {
        didSelectResult(j, false, false);
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        RecyclerListView recyclerListView;
        RecyclerListView recyclerListView2;
        $$Lambda$DialogsActivity$HwlnMR3KRyF6sYQZyorPA1MSynY r10 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                DialogsActivity.this.lambda$getThemeDescriptions$38$DialogsActivity();
            }
        };
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        if (this.movingView != null) {
            arrayList.add(new ThemeDescription(this.movingView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        }
        if (this.doneItem != null) {
            arrayList.add(new ThemeDescription(this.doneItem, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        }
        char c = 0;
        if (this.folderId == 0) {
            if (this.onlySelect) {
                arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
            }
            arrayList.add(new ThemeDescription(this.fragmentView, 0, (Class[]) null, this.actionBarDefaultPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
            if (this.searchViewPager != null) {
                arrayList.add(new ThemeDescription(this.searchViewPager.searchListView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
            }
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, r10, "actionBarDefaultIcon"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, new Drawable[]{Theme.dialogs_holidayDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearch"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearchPlaceholder"));
        } else {
            arrayList.add(new ThemeDescription(this.fragmentView, 0, (Class[]) null, this.actionBarDefaultPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultArchived"));
            if (this.searchViewPager != null) {
                arrayList.add(new ThemeDescription(this.searchViewPager.searchListView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultArchived"));
            }
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultArchivedIcon"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, new Drawable[]{Theme.dialogs_holidayDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultArchivedTitle"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultArchivedSelector"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultArchivedSearch"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearchArchivedPlaceholder"));
        }
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarActionModeDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_TOPBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarActionModeDefaultTop"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarActionModeDefaultSelector"));
        arrayList.add(new ThemeDescription(this.selectedDialogsCountTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarActionModeDefaultIcon"));
        $$Lambda$DialogsActivity$HwlnMR3KRyF6sYQZyorPA1MSynY r7 = r10;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "actionBarDefaultSubmenuBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "actionBarDefaultSubmenuItemIcon"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "dialogButtonSelector"));
        if (this.filterTabsView != null) {
            if (this.actionBar.isActionModeShowed()) {
                arrayList.add(new ThemeDescription((View) this.filterTabsView, 0, new Class[]{FilterTabsView.class}, new String[]{"selectorDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_tabSelectedLine"));
                arrayList.add(new ThemeDescription(this.filterTabsView.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FilterTabsView.TabView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_tabSelectedText"));
                arrayList.add(new ThemeDescription(this.filterTabsView.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FilterTabsView.TabView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_tabText"));
                arrayList.add(new ThemeDescription(this.filterTabsView.getTabsContainer(), ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{FilterTabsView.TabView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_tabSelector"));
            } else {
                arrayList.add(new ThemeDescription((View) this.filterTabsView, 0, new Class[]{FilterTabsView.class}, new String[]{"selectorDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarTabLine"));
                arrayList.add(new ThemeDescription(this.filterTabsView.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FilterTabsView.TabView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarTabActiveText"));
                arrayList.add(new ThemeDescription(this.filterTabsView.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FilterTabsView.TabView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarTabUnactiveText"));
                arrayList.add(new ThemeDescription(this.filterTabsView.getTabsContainer(), ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarTabSelector"));
            }
            arrayList.add(new ThemeDescription(this.filterTabsView.getTabsContainer(), 0, new Class[]{FilterTabsView.TabView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_tabUnreadActiveBackground"));
            arrayList.add(new ThemeDescription(this.filterTabsView.getTabsContainer(), 0, new Class[]{FilterTabsView.TabView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_tabUnreadUnactiveBackground"));
        }
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionIcon"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionBackground"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionPressedBackground"));
        int i = 0;
        while (true) {
            recyclerListView = null;
            if (i >= 3) {
                break;
            }
            if (i == 2) {
                SearchViewPager searchViewPager2 = this.searchViewPager;
                if (searchViewPager2 == null) {
                    i++;
                } else {
                    recyclerListView2 = searchViewPager2.searchListView;
                }
            } else {
                ViewPage[] viewPageArr = this.viewPages;
                if (viewPageArr == null) {
                    i++;
                } else {
                    if (i < viewPageArr.length) {
                        recyclerListView = viewPageArr[i].listView;
                    }
                    recyclerListView2 = recyclerListView;
                }
            }
            if (recyclerListView2 != null) {
                RecyclerListView recyclerListView3 = recyclerListView2;
                arrayList.add(new ThemeDescription(recyclerListView3, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{DialogCell.class}, Theme.dialogs_countPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounter"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{DialogCell.class}, Theme.dialogs_countGrayPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounterMuted"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{DialogCell.class}, Theme.dialogs_countTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounterText"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_lockDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_secretIcon"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_groupDrawable, Theme.dialogs_broadcastDrawable, Theme.dialogs_botDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_nameIcon"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_scamDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_draft"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_pinnedDrawable, Theme.dialogs_reorderDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_pinnedIcon"));
                TextPaint[] textPaintArr = Theme.dialogs_namePaint;
                arrayList.add(new ThemeDescription((View) recyclerListView3, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (String[]) null, new Paint[]{textPaintArr[0], textPaintArr[1], Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_name"));
                TextPaint[] textPaintArr2 = Theme.dialogs_nameEncryptedPaint;
                arrayList.add(new ThemeDescription((View) recyclerListView3, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (String[]) null, new Paint[]{textPaintArr2[0], textPaintArr2[1], Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_secretName"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePaint[1], (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_message_threeLines"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePaint[0], (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_message"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{DialogCell.class}, Theme.dialogs_messageNamePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_nameMessage_threeLines"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{DialogCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_draft"));
                arrayList.add(new ThemeDescription((View) recyclerListView3, 0, new Class[]{DialogCell.class}, (String[]) null, (Paint[]) Theme.dialogs_messagePrintingPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionMessage"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{DialogCell.class}, Theme.dialogs_timePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_date"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{DialogCell.class}, Theme.dialogs_pinnedPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_pinnedOverlay"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{DialogCell.class}, Theme.dialogs_tabletSeletedPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_tabletSelectedOverlay"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_checkDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_sentCheck"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_checkReadDrawable, Theme.dialogs_halfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_sentReadCheck"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_clockDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_sentClock"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{DialogCell.class}, Theme.dialogs_errorPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_sentError"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_errorDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_sentErrorIcon"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_verifiedCheck"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_verifiedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_verifiedBackground"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_muteDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_muteIcon"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_mentionDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_mentionIcon"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{DialogCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_archivePinBackground"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{DialogCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_archiveBackground"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{DialogCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_onlineCircle"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{DialogCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription((View) recyclerListView3, ThemeDescription.FLAG_CHECKBOX, new Class[]{DialogCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription((View) recyclerListView3, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{DialogCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
                arrayList.add(new ThemeDescription((View) recyclerListView3, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_offlinePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
                arrayList.add(new ThemeDescription(recyclerListView3, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_onlinePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText3"));
                GraySectionCell.createThemeDescriptions(arrayList, recyclerListView2);
                arrayList.add(new ThemeDescription(recyclerListView3, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{HashtagSearchCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(recyclerListView3, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
                arrayList.add(new ThemeDescription(recyclerListView3, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
                arrayList.add(new ThemeDescription((View) recyclerListView3, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText2"));
            }
            i++;
        }
        $$Lambda$DialogsActivity$HwlnMR3KRyF6sYQZyorPA1MSynY r72 = r10;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "avatar_backgroundSaved"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "avatar_backgroundArchived"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "avatar_backgroundArchivedHidden"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "chats_nameMessage"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "chats_draft"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "chats_attachMessage"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "chats_nameArchived"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "chats_nameMessageArchived"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "chats_nameMessageArchived_threeLines"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "chats_messageArchived"));
        if (this.viewPages != null) {
            int i2 = 0;
            while (i2 < this.viewPages.length) {
                if (this.folderId == 0) {
                    arrayList.add(new ThemeDescription(this.viewPages[i2].listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
                } else {
                    arrayList.add(new ThemeDescription(this.viewPages[i2].listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultArchived"));
                }
                DialogsRecyclerView access$8900 = this.viewPages[i2].listView;
                int i3 = ThemeDescription.FLAG_TEXTCOLOR;
                Class[] clsArr = new Class[1];
                clsArr[c] = DialogsEmptyCell.class;
                String[] strArr = new String[1];
                strArr[c] = "emptyTextView1";
                arrayList.add(new ThemeDescription((View) access$8900, i3, clsArr, strArr, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_nameMessage_threeLines"));
                DialogsRecyclerView access$89002 = this.viewPages[i2].listView;
                int i4 = ThemeDescription.FLAG_TEXTCOLOR;
                Class[] clsArr2 = new Class[1];
                clsArr2[c] = DialogsEmptyCell.class;
                String[] strArr2 = new String[1];
                strArr2[c] = "emptyTextView2";
                arrayList.add(new ThemeDescription((View) access$89002, i4, clsArr2, strArr2, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_message"));
                if (SharedConfig.archiveHidden) {
                    DialogsRecyclerView access$89003 = this.viewPages[i2].listView;
                    Class[] clsArr3 = new Class[1];
                    clsArr3[c] = DialogCell.class;
                    RLottieDrawable[] rLottieDrawableArr = new RLottieDrawable[1];
                    rLottieDrawableArr[c] = Theme.dialogs_archiveAvatarDrawable;
                    arrayList.add(new ThemeDescription((View) access$89003, 0, clsArr3, rLottieDrawableArr, "Arrow1", "avatar_backgroundArchivedHidden"));
                    DialogsRecyclerView access$89004 = this.viewPages[i2].listView;
                    Class[] clsArr4 = new Class[1];
                    clsArr4[c] = DialogCell.class;
                    RLottieDrawable[] rLottieDrawableArr2 = new RLottieDrawable[1];
                    rLottieDrawableArr2[c] = Theme.dialogs_archiveAvatarDrawable;
                    arrayList.add(new ThemeDescription((View) access$89004, 0, clsArr4, rLottieDrawableArr2, "Arrow2", "avatar_backgroundArchivedHidden"));
                } else {
                    DialogsRecyclerView access$89005 = this.viewPages[i2].listView;
                    Class[] clsArr5 = new Class[1];
                    clsArr5[c] = DialogCell.class;
                    RLottieDrawable[] rLottieDrawableArr3 = new RLottieDrawable[1];
                    rLottieDrawableArr3[c] = Theme.dialogs_archiveAvatarDrawable;
                    arrayList.add(new ThemeDescription((View) access$89005, 0, clsArr5, rLottieDrawableArr3, "Arrow1", "avatar_backgroundArchived"));
                    DialogsRecyclerView access$89006 = this.viewPages[i2].listView;
                    Class[] clsArr6 = new Class[1];
                    clsArr6[c] = DialogCell.class;
                    RLottieDrawable[] rLottieDrawableArr4 = new RLottieDrawable[1];
                    rLottieDrawableArr4[c] = Theme.dialogs_archiveAvatarDrawable;
                    arrayList.add(new ThemeDescription((View) access$89006, 0, clsArr6, rLottieDrawableArr4, "Arrow2", "avatar_backgroundArchived"));
                }
                DialogsRecyclerView access$89007 = this.viewPages[i2].listView;
                Class[] clsArr7 = new Class[1];
                clsArr7[c] = DialogCell.class;
                RLottieDrawable[] rLottieDrawableArr5 = new RLottieDrawable[1];
                rLottieDrawableArr5[c] = Theme.dialogs_archiveAvatarDrawable;
                arrayList.add(new ThemeDescription((View) access$89007, 0, clsArr7, rLottieDrawableArr5, "Box2", "avatar_text"));
                DialogsRecyclerView access$89008 = this.viewPages[i2].listView;
                Class[] clsArr8 = new Class[1];
                clsArr8[c] = DialogCell.class;
                RLottieDrawable[] rLottieDrawableArr6 = new RLottieDrawable[1];
                rLottieDrawableArr6[c] = Theme.dialogs_archiveAvatarDrawable;
                arrayList.add(new ThemeDescription((View) access$89008, 0, clsArr8, rLottieDrawableArr6, "Box1", "avatar_text"));
                DialogsRecyclerView access$89009 = this.viewPages[i2].listView;
                Class[] clsArr9 = new Class[1];
                clsArr9[c] = DialogCell.class;
                RLottieDrawable[] rLottieDrawableArr7 = new RLottieDrawable[1];
                rLottieDrawableArr7[c] = Theme.dialogs_pinArchiveDrawable;
                arrayList.add(new ThemeDescription((View) access$89009, 0, clsArr9, rLottieDrawableArr7, "Arrow", "chats_archiveIcon"));
                DialogsRecyclerView access$890010 = this.viewPages[i2].listView;
                Class[] clsArr10 = new Class[1];
                clsArr10[c] = DialogCell.class;
                RLottieDrawable[] rLottieDrawableArr8 = new RLottieDrawable[1];
                rLottieDrawableArr8[c] = Theme.dialogs_pinArchiveDrawable;
                arrayList.add(new ThemeDescription((View) access$890010, 0, clsArr10, rLottieDrawableArr8, "Line", "chats_archiveIcon"));
                DialogsRecyclerView access$890011 = this.viewPages[i2].listView;
                Class[] clsArr11 = new Class[1];
                clsArr11[c] = DialogCell.class;
                RLottieDrawable[] rLottieDrawableArr9 = new RLottieDrawable[1];
                rLottieDrawableArr9[c] = Theme.dialogs_unpinArchiveDrawable;
                arrayList.add(new ThemeDescription((View) access$890011, 0, clsArr11, rLottieDrawableArr9, "Arrow", "chats_archiveIcon"));
                DialogsRecyclerView access$890012 = this.viewPages[i2].listView;
                Class[] clsArr12 = new Class[1];
                clsArr12[c] = DialogCell.class;
                RLottieDrawable[] rLottieDrawableArr10 = new RLottieDrawable[1];
                rLottieDrawableArr10[c] = Theme.dialogs_unpinArchiveDrawable;
                arrayList.add(new ThemeDescription((View) access$890012, 0, clsArr12, rLottieDrawableArr10, "Line", "chats_archiveIcon"));
                DialogsRecyclerView access$890013 = this.viewPages[i2].listView;
                Class[] clsArr13 = new Class[1];
                clsArr13[c] = DialogCell.class;
                RLottieDrawable[] rLottieDrawableArr11 = new RLottieDrawable[1];
                rLottieDrawableArr11[c] = Theme.dialogs_archiveDrawable;
                arrayList.add(new ThemeDescription((View) access$890013, 0, clsArr13, rLottieDrawableArr11, "Arrow", "chats_archiveBackground"));
                DialogsRecyclerView access$890014 = this.viewPages[i2].listView;
                Class[] clsArr14 = new Class[1];
                clsArr14[c] = DialogCell.class;
                RLottieDrawable[] rLottieDrawableArr12 = new RLottieDrawable[1];
                rLottieDrawableArr12[c] = Theme.dialogs_archiveDrawable;
                arrayList.add(new ThemeDescription((View) access$890014, 0, clsArr14, rLottieDrawableArr12, "Box2", "chats_archiveIcon"));
                DialogsRecyclerView access$890015 = this.viewPages[i2].listView;
                Class[] clsArr15 = new Class[1];
                clsArr15[c] = DialogCell.class;
                RLottieDrawable[] rLottieDrawableArr13 = new RLottieDrawable[1];
                rLottieDrawableArr13[c] = Theme.dialogs_archiveDrawable;
                arrayList.add(new ThemeDescription((View) access$890015, 0, clsArr15, rLottieDrawableArr13, "Box1", "chats_archiveIcon"));
                DialogsRecyclerView access$890016 = this.viewPages[i2].listView;
                Class[] clsArr16 = new Class[1];
                clsArr16[c] = DialogCell.class;
                RLottieDrawable[] rLottieDrawableArr14 = new RLottieDrawable[1];
                rLottieDrawableArr14[c] = Theme.dialogs_hidePsaDrawable;
                arrayList.add(new ThemeDescription((View) access$890016, 0, clsArr16, rLottieDrawableArr14, "Line 1", "chats_archiveBackground"));
                DialogsRecyclerView access$890017 = this.viewPages[i2].listView;
                Class[] clsArr17 = new Class[1];
                clsArr17[c] = DialogCell.class;
                RLottieDrawable[] rLottieDrawableArr15 = new RLottieDrawable[1];
                rLottieDrawableArr15[c] = Theme.dialogs_hidePsaDrawable;
                arrayList.add(new ThemeDescription((View) access$890017, 0, clsArr17, rLottieDrawableArr15, "Line 2", "chats_archiveBackground"));
                DialogsRecyclerView access$890018 = this.viewPages[i2].listView;
                Class[] clsArr18 = new Class[1];
                clsArr18[c] = DialogCell.class;
                RLottieDrawable[] rLottieDrawableArr16 = new RLottieDrawable[1];
                rLottieDrawableArr16[c] = Theme.dialogs_hidePsaDrawable;
                arrayList.add(new ThemeDescription((View) access$890018, 0, clsArr18, rLottieDrawableArr16, "Line 3", "chats_archiveBackground"));
                DialogsRecyclerView access$890019 = this.viewPages[i2].listView;
                Class[] clsArr19 = new Class[1];
                clsArr19[c] = DialogCell.class;
                RLottieDrawable[] rLottieDrawableArr17 = new RLottieDrawable[1];
                rLottieDrawableArr17[c] = Theme.dialogs_hidePsaDrawable;
                arrayList.add(new ThemeDescription((View) access$890019, 0, clsArr19, rLottieDrawableArr17, "Cup Red", "chats_archiveIcon"));
                DialogsRecyclerView access$890020 = this.viewPages[i2].listView;
                Class[] clsArr20 = new Class[1];
                clsArr20[c] = DialogCell.class;
                RLottieDrawable[] rLottieDrawableArr18 = new RLottieDrawable[1];
                rLottieDrawableArr18[c] = Theme.dialogs_hidePsaDrawable;
                arrayList.add(new ThemeDescription((View) access$890020, 0, clsArr20, rLottieDrawableArr18, "Box", "chats_archiveIcon"));
                DialogsRecyclerView access$890021 = this.viewPages[i2].listView;
                Class[] clsArr21 = new Class[1];
                clsArr21[c] = DialogCell.class;
                RLottieDrawable[] rLottieDrawableArr19 = new RLottieDrawable[1];
                rLottieDrawableArr19[c] = Theme.dialogs_unarchiveDrawable;
                arrayList.add(new ThemeDescription((View) access$890021, 0, clsArr21, rLottieDrawableArr19, "Arrow1", "chats_archiveIcon"));
                DialogsRecyclerView access$890022 = this.viewPages[i2].listView;
                Class[] clsArr22 = new Class[1];
                clsArr22[c] = DialogCell.class;
                RLottieDrawable[] rLottieDrawableArr20 = new RLottieDrawable[1];
                rLottieDrawableArr20[c] = Theme.dialogs_unarchiveDrawable;
                arrayList.add(new ThemeDescription((View) access$890022, 0, clsArr22, rLottieDrawableArr20, "Arrow2", "chats_archivePinBackground"));
                DialogsRecyclerView access$890023 = this.viewPages[i2].listView;
                Class[] clsArr23 = new Class[1];
                clsArr23[c] = DialogCell.class;
                RLottieDrawable[] rLottieDrawableArr21 = new RLottieDrawable[1];
                rLottieDrawableArr21[c] = Theme.dialogs_unarchiveDrawable;
                arrayList.add(new ThemeDescription((View) access$890023, 0, clsArr23, rLottieDrawableArr21, "Box2", "chats_archiveIcon"));
                DialogsRecyclerView access$890024 = this.viewPages[i2].listView;
                Class[] clsArr24 = new Class[1];
                clsArr24[c] = DialogCell.class;
                RLottieDrawable[] rLottieDrawableArr22 = new RLottieDrawable[1];
                rLottieDrawableArr22[c] = Theme.dialogs_unarchiveDrawable;
                arrayList.add(new ThemeDescription((View) access$890024, 0, clsArr24, rLottieDrawableArr22, "Box1", "chats_archiveIcon"));
                DialogsRecyclerView access$890025 = this.viewPages[i2].listView;
                Class[] clsArr25 = new Class[1];
                clsArr25[c] = UserCell.class;
                String[] strArr3 = new String[1];
                strArr3[c] = "nameTextView";
                arrayList.add(new ThemeDescription((View) access$890025, 0, clsArr25, strArr3, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                DialogsRecyclerView access$890026 = this.viewPages[i2].listView;
                Class[] clsArr26 = new Class[1];
                clsArr26[c] = UserCell.class;
                String[] strArr4 = new String[1];
                strArr4[c] = "statusColor";
                ThemeDescription themeDescription = r1;
                int i5 = i2;
                ThemeDescription themeDescription2 = new ThemeDescription((View) access$890026, 0, clsArr26, strArr4, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r10, "windowBackgroundWhiteGrayText");
                arrayList.add(themeDescription);
                arrayList.add(new ThemeDescription((View) this.viewPages[i5].listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r10, "windowBackgroundWhiteBlueText"));
                arrayList.add(new ThemeDescription(this.viewPages[i5].progressView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
                ViewPager archiveHintCellPager = this.viewPages[i5].dialogsAdapter.getArchiveHintCellPager();
                ViewPager viewPager = archiveHintCellPager;
                arrayList.add(new ThemeDescription((View) viewPager, 0, new Class[]{ArchiveHintInnerCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_nameMessage_threeLines"));
                arrayList.add(new ThemeDescription((View) viewPager, 0, new Class[]{ArchiveHintInnerCell.class}, new String[]{"imageView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounter"));
                arrayList.add(new ThemeDescription((View) viewPager, 0, new Class[]{ArchiveHintInnerCell.class}, new String[]{"headerTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_nameMessage_threeLines"));
                arrayList.add(new ThemeDescription((View) viewPager, 0, new Class[]{ArchiveHintInnerCell.class}, new String[]{"messageTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_message"));
                arrayList.add(new ThemeDescription(archiveHintCellPager, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultArchived"));
                i2 = i5 + 1;
                c = 0;
            }
        }
        $$Lambda$DialogsActivity$HwlnMR3KRyF6sYQZyorPA1MSynY r73 = r10;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r73, "chats_archivePullDownBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r73, "chats_archivePullDownBackgroundActive"));
        arrayList.add(new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuBackground"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuName"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuPhone"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuPhoneCats"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuCloudBackgroundCats"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_serviceBackground"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuTopShadow"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuTopShadowCats"));
        arrayList.add(new ThemeDescription((View) this.sideMenu, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{DrawerProfileCell.class}, new String[]{"darkThemeView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuName"));
        arrayList.add(new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, r73, "chats_menuTopBackgroundCats"));
        arrayList.add(new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, r73, "chats_menuTopBackground"));
        arrayList.add(new ThemeDescription((View) this.sideMenu, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{DrawerActionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuItemIcon"));
        arrayList.add(new ThemeDescription((View) this.sideMenu, 0, new Class[]{DrawerActionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuItemText"));
        arrayList.add(new ThemeDescription((View) this.sideMenu, 0, new Class[]{DrawerUserCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuItemText"));
        arrayList.add(new ThemeDescription((View) this.sideMenu, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DrawerUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounterText"));
        arrayList.add(new ThemeDescription((View) this.sideMenu, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DrawerUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounter"));
        arrayList.add(new ThemeDescription((View) this.sideMenu, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DrawerUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuBackground"));
        arrayList.add(new ThemeDescription((View) this.sideMenu, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{DrawerAddCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuItemIcon"));
        arrayList.add(new ThemeDescription((View) this.sideMenu, 0, new Class[]{DrawerAddCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuItemText"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DividerCell.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        if (this.searchViewPager != null) {
            DialogsSearchAdapter dialogsSearchAdapter = this.searchViewPager.dialogsSearchAdapter;
            arrayList.add(new ThemeDescription(dialogsSearchAdapter != null ? dialogsSearchAdapter.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounter"));
            DialogsSearchAdapter dialogsSearchAdapter2 = this.searchViewPager.dialogsSearchAdapter;
            arrayList.add(new ThemeDescription(dialogsSearchAdapter2 != null ? dialogsSearchAdapter2.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countGrayPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounterMuted"));
            DialogsSearchAdapter dialogsSearchAdapter3 = this.searchViewPager.dialogsSearchAdapter;
            arrayList.add(new ThemeDescription(dialogsSearchAdapter3 != null ? dialogsSearchAdapter3.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounterText"));
            DialogsSearchAdapter dialogsSearchAdapter4 = this.searchViewPager.dialogsSearchAdapter;
            arrayList.add(new ThemeDescription(dialogsSearchAdapter4 != null ? dialogsSearchAdapter4.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_archiveTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_archiveText"));
            DialogsSearchAdapter dialogsSearchAdapter5 = this.searchViewPager.dialogsSearchAdapter;
            arrayList.add(new ThemeDescription((View) dialogsSearchAdapter5 != null ? dialogsSearchAdapter5.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            DialogsSearchAdapter dialogsSearchAdapter6 = this.searchViewPager.dialogsSearchAdapter;
            if (dialogsSearchAdapter6 != null) {
                recyclerListView = dialogsSearchAdapter6.getInnerListView();
            }
            arrayList.add(new ThemeDescription(recyclerListView, 0, new Class[]{HintDialogCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_onlineCircle"));
        }
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerBackground"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"playButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerPlayPause"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerTitle"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_FASTSCROLL, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerPerformer"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"closeButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerClose"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "returnToCallBackground"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "returnToCallText"));
        for (int i6 = 0; i6 < this.undoView.length; i6++) {
            arrayList.add(new ThemeDescription(this.undoView[i6], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_background"));
            arrayList.add(new ThemeDescription((View) this.undoView[i6], 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i6], 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i6], 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i6], 0, new Class[]{UndoView.class}, new String[]{"subinfoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i6], 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i6], 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i6], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "info1", "undo_background"));
            arrayList.add(new ThemeDescription((View) this.undoView[i6], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "info2", "undo_background"));
            arrayList.add(new ThemeDescription((View) this.undoView[i6], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luCLASSNAME", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i6], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luCLASSNAME", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i6], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luCLASSNAME", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i6], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc9", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i6], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc8", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i6], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc7", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i6], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc6", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i6], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc5", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i6], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc4", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i6], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc3", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i6], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc2", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i6], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc1", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i6], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Oval", "undo_infoColor"));
        }
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackgroundGray"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextLink"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogLinkSelection"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlue2"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlue3"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlue4"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextRed2"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextGray"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextGray2"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextGray3"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextGray4"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogIcon"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogRedIcon"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextHint"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogInputField"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogInputFieldActivated"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogCheckboxSquareBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogCheckboxSquareCheck"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogCheckboxSquareUnchecked"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogCheckboxSquareDisabled"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogRadioBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogRadioBackgroundChecked"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogProgressCircle"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogButton"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogButtonSelector"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogScrollGlow"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogRoundCheckBox"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogRoundCheckBoxCheck"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBadgeBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBadgeText"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogLineProgress"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogLineProgressBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogGrayLine"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialog_inlineProgressBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialog_inlineProgress"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchHint"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchIcon"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchText"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogFloatingButton"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogFloatingIcon"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogShadowLine"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_sheet_scrollUp"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_sheet_other"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBar"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarSelector"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarTitle"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarTop"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarSubtitle"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarItems"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_background"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_time"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progressBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_player_progressCachedBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progress"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_button"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_buttonActive"));
        if (this.commentView != null) {
            arrayList.add(new ThemeDescription(this.commentView, 0, (Class[]) null, Theme.chat_composeBackgroundPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelBackground"));
            arrayList.add(new ThemeDescription(this.commentView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_composeShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelShadow"));
            arrayList.add(new ThemeDescription((View) this.commentView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"messageEditText"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelText"));
            arrayList.add(new ThemeDescription((View) this.commentView, ThemeDescription.FLAG_CURSORCOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"messageEditText"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelCursor"));
            arrayList.add(new ThemeDescription((View) this.commentView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"messageEditText"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelHint"));
            arrayList.add(new ThemeDescription((View) this.commentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"sendButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelSend"));
        }
        $$Lambda$DialogsActivity$HwlnMR3KRyF6sYQZyorPA1MSynY r74 = r10;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r74, "actionBarTipBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r74, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r74, "player_time"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r74, "chat_messagePanelCursor"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r74, "avatar_actionBarIconBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r74, "groupcreate_spanBackground"));
        FiltersView filtersView2 = this.filtersView;
        if (filtersView2 != null) {
            arrayList.addAll(filtersView2.getThemeDescriptions());
            this.filtersView.updateColors();
        }
        SearchViewPager searchViewPager3 = this.searchViewPager;
        if (searchViewPager3 != null) {
            searchViewPager3.getThemeDescriptors(arrayList);
        }
        return arrayList;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getThemeDescriptions$38 */
    public /* synthetic */ void lambda$getThemeDescriptions$38$DialogsActivity() {
        DialogsSearchAdapter dialogsSearchAdapter;
        RecyclerListView innerListView;
        ViewGroup viewGroup;
        int i = 0;
        int i2 = 0;
        while (i2 < 3) {
            if (i2 == 2) {
                SearchViewPager searchViewPager2 = this.searchViewPager;
                if (searchViewPager2 == null) {
                    i2++;
                } else {
                    viewGroup = searchViewPager2.searchListView;
                }
            } else {
                ViewPage[] viewPageArr = this.viewPages;
                if (viewPageArr == null) {
                    i2++;
                } else {
                    viewGroup = i2 < viewPageArr.length ? viewPageArr[i2].listView : null;
                }
            }
            if (viewGroup != null) {
                int childCount = viewGroup.getChildCount();
                for (int i3 = 0; i3 < childCount; i3++) {
                    View childAt = viewGroup.getChildAt(i3);
                    if (childAt instanceof ProfileSearchCell) {
                        ((ProfileSearchCell) childAt).update(0);
                    } else if (childAt instanceof DialogCell) {
                        ((DialogCell) childAt).update(0);
                    } else if (childAt instanceof UserCell) {
                        ((UserCell) childAt).update(0);
                    }
                }
            }
            i2++;
        }
        SearchViewPager searchViewPager3 = this.searchViewPager;
        if (!(searchViewPager3 == null || (dialogsSearchAdapter = searchViewPager3.dialogsSearchAdapter) == null || (innerListView = dialogsSearchAdapter.getInnerListView()) == null)) {
            int childCount2 = innerListView.getChildCount();
            for (int i4 = 0; i4 < childCount2; i4++) {
                View childAt2 = innerListView.getChildAt(i4);
                if (childAt2 instanceof HintDialogCell) {
                    ((HintDialogCell) childAt2).update();
                }
            }
        }
        RecyclerView recyclerView = this.sideMenu;
        if (recyclerView != null) {
            View childAt3 = recyclerView.getChildAt(0);
            if (childAt3 instanceof DrawerProfileCell) {
                DrawerProfileCell drawerProfileCell = (DrawerProfileCell) childAt3;
                drawerProfileCell.applyBackground(true);
                drawerProfileCell.updateColors();
            }
        }
        if (this.viewPages != null) {
            int i5 = 0;
            while (true) {
                ViewPage[] viewPageArr2 = this.viewPages;
                if (i5 >= viewPageArr2.length) {
                    break;
                }
                if (viewPageArr2[i5].pullForegroundDrawable != null) {
                    this.viewPages[i5].pullForegroundDrawable.updateColors();
                }
                i5++;
            }
        }
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.setPopupBackgroundColor(Theme.getColor("actionBarDefaultSubmenuBackground"), true);
            this.actionBar.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItem"), false, true);
            this.actionBar.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItemIcon"), true, true);
            this.actionBar.setPopupItemsSelectorColor(Theme.getColor("dialogButtonSelector"), true);
        }
        if (this.scrimPopupWindowItems != null) {
            while (true) {
                ActionBarMenuSubItem[] actionBarMenuSubItemArr = this.scrimPopupWindowItems;
                if (i >= actionBarMenuSubItemArr.length) {
                    break;
                }
                actionBarMenuSubItemArr[i].setColors(Theme.getColor("actionBarDefaultSubmenuItem"), Theme.getColor("actionBarDefaultSubmenuItemIcon"));
                this.scrimPopupWindowItems[i].setSelectorColor(Theme.getColor("dialogButtonSelector"));
                i++;
            }
        }
        ActionBarPopupWindow actionBarPopupWindow = this.scrimPopupWindow;
        if (actionBarPopupWindow != null) {
            View contentView = actionBarPopupWindow.getContentView();
            contentView.setBackgroundColor(Theme.getColor("actionBarDefaultSubmenuBackground"));
            contentView.invalidate();
        }
        ActionBarMenuItem actionBarMenuItem = this.doneItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setIconColor(Theme.getColor("actionBarDefaultIcon"));
        }
        ChatActivityEnterView chatActivityEnterView = this.commentView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.updateColors();
        }
        FiltersView filtersView2 = this.filtersView;
        if (filtersView2 != null) {
            filtersView2.updateColors();
        }
        SearchViewPager searchViewPager4 = this.searchViewPager;
        if (searchViewPager4 != null) {
            searchViewPager4.updateColors();
        }
        ViewPagerFixed.TabsView tabsView = this.searchTabsView;
        if (tabsView != null) {
            tabsView.updateColors();
        }
        ActionBarMenuItem actionBarMenuItem2 = this.searchItem;
        if (actionBarMenuItem2 != null) {
            EditTextBoldCursor searchField = actionBarMenuItem2.getSearchField();
            if (this.whiteActionBar) {
                searchField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                searchField.setHintTextColor(Theme.getColor("player_time"));
                searchField.setCursorColor(Theme.getColor("chat_messagePanelCursor"));
            } else {
                searchField.setCursorColor(Theme.getColor("actionBarDefaultSearch"));
                searchField.setHintTextColor(Theme.getColor("actionBarDefaultSearchPlaceholder"));
                searchField.setTextColor(Theme.getColor("actionBarDefaultSearch"));
            }
            this.searchItem.updateColor();
        }
        setSearchAnimationProgress(this.searchAnimationProgress);
    }
}
