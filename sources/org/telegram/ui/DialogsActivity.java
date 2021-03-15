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
import android.graphics.RectF;
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
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$InputDialogPeer;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$TL_dialogFolder;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputFolderPeer;
import org.telegram.tgnet.TLRPC$TL_messages_checkHistoryImportPeer;
import org.telegram.tgnet.TLRPC$TL_messages_checkedHistoryImportPeer;
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
import org.telegram.ui.Cells.TextInfoPrivacyCell;
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
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.FragmentContextView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.PacmanAnimation;
import org.telegram.ui.Components.ProxyDrawable;
import org.telegram.ui.Components.PullForegroundDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerAnimationScrollHelper;
import org.telegram.ui.Components.RecyclerItemsEnterAnimator;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SearchViewPager;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.ViewPagerFixed;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.GroupCreateFinalActivity;

public class DialogsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    public static boolean[] dialogsLoaded = new boolean[3];
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
    private ValueAnimator actionBarColorAnimator;
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
    /* access modifiers changed from: private */
    public View blurredView;
    private int canClearCacheCount;
    private boolean canDeletePsaSelected;
    /* access modifiers changed from: private */
    public int canMuteCount;
    /* access modifiers changed from: private */
    public int canPinCount;
    /* access modifiers changed from: private */
    public int canReadCount;
    private int canReportSpamCount;
    private boolean canShowFilterTabsView;
    /* access modifiers changed from: private */
    public boolean canShowHiddenArchive;
    private int canUnarchiveCount;
    /* access modifiers changed from: private */
    public int canUnmuteCount;
    private boolean cantSendToChannels;
    private boolean checkCanWrite;
    private boolean checkPermission = true;
    private boolean checkingImportDialog;
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
    public float filterTabsMoveFrom;
    /* access modifiers changed from: private */
    public float filterTabsProgress;
    /* access modifiers changed from: private */
    public FilterTabsView filterTabsView;
    /* access modifiers changed from: private */
    public boolean filterTabsViewIsVisible;
    /* access modifiers changed from: private */
    public ValueAnimator filtersTabAnimator;
    /* access modifiers changed from: private */
    public FiltersView filtersView;
    private RLottieImageView floatingButton;
    /* access modifiers changed from: private */
    public FrameLayout floatingButtonContainer;
    /* access modifiers changed from: private */
    public float floatingButtonHideProgress;
    /* access modifiers changed from: private */
    public float floatingButtonTranslation;
    private boolean floatingForceVisible;
    /* access modifiers changed from: private */
    public boolean floatingHidden;
    private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();
    /* access modifiers changed from: private */
    public int folderId;
    /* access modifiers changed from: private */
    public FragmentContextView fragmentContextView;
    /* access modifiers changed from: private */
    public FragmentContextView fragmentLocationContextView;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$Dialog> frozenDialogsList;
    private int hasPoll;
    /* access modifiers changed from: private */
    public int initialDialogsType;
    private String initialSearchString;
    private int initialSearchType = -1;
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
    private RLottieDrawable passcodeDrawable;
    private RLottieDrawable passcodeDrawable2;
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
    /* access modifiers changed from: private */
    public float scrollAdditionalOffset;
    private boolean scrollBarVisible = true;
    /* access modifiers changed from: private */
    public boolean scrollUpdated;
    /* access modifiers changed from: private */
    public boolean scrollingManually;
    /* access modifiers changed from: private */
    public float searchAnimationProgress;
    /* access modifiers changed from: private */
    public boolean searchAnimationTabsDelayedCrossfade;
    /* access modifiers changed from: private */
    public AnimatorSet searchAnimator;
    private long searchDialogId;
    /* access modifiers changed from: private */
    public boolean searchFiltersWasShowed;
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
    public float tabsYOffset;
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

    public void setShowSearch(String str, int i) {
        if (!this.searching) {
            this.initialSearchType = i;
            this.actionBar.openSearchField(str, false);
            return;
        }
        if (!this.searchItem.getSearchField().getText().toString().equals(str)) {
            this.searchItem.getSearchField().setText(str);
        }
        if (this.searchViewPager.getTabsView().getCurrentTabId() != i) {
            this.searchViewPager.getTabsView().scrollToTab(i, i);
        }
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
        public FlickerLoadingView progressView;
        /* access modifiers changed from: private */
        public PullForegroundDrawable pullForegroundDrawable;
        /* access modifiers changed from: private */
        public RecyclerItemsEnterAnimator recyclerItemsEnterAnimator;
        /* access modifiers changed from: private */
        public RecyclerAnimationScrollHelper scrollHelper;
        /* access modifiers changed from: private */
        public int selectedType;
        /* access modifiers changed from: private */
        public SwipeController swipeController;

        static /* synthetic */ int access$9908(ViewPage viewPage) {
            int i = viewPage.lastItemsCount;
            viewPage.lastItemsCount = i + 1;
            return i;
        }

        static /* synthetic */ int access$9910(ViewPage viewPage) {
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
            float measuredHeight = (DialogsActivity.this.filterTabsView == null || DialogsActivity.this.filterTabsView.getVisibility() == 8) ? 0.0f : ((float) DialogsActivity.this.filterTabsView.getMeasuredHeight()) - ((1.0f - DialogsActivity.this.filterTabsProgress) * ((float) DialogsActivity.this.filterTabsView.getMeasuredHeight()));
            if (!(DialogsActivity.this.searchTabsView == null || DialogsActivity.this.searchTabsView.getVisibility() == 8)) {
                f = (float) DialogsActivity.this.searchTabsView.getMeasuredHeight();
            }
            return (int) (height + (measuredHeight * (1.0f - DialogsActivity.this.searchAnimationProgress)) + (f * DialogsActivity.this.searchAnimationProgress));
        }

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View view, long j) {
            boolean z;
            if ((view == DialogsActivity.this.fragmentContextView && DialogsActivity.this.fragmentContextView.isCallStyle()) || view == DialogsActivity.this.blurredView) {
                return true;
            }
            if (view == DialogsActivity.this.viewPages[0] || ((DialogsActivity.this.viewPages.length > 1 && view == DialogsActivity.this.viewPages[1]) || view == DialogsActivity.this.fragmentContextView || view == DialogsActivity.this.fragmentLocationContextView || view == DialogsActivity.this.searchViewPager)) {
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
            float f;
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
                float f2 = (float) i2;
                int i3 = i2 + actionBarFullHeight;
                float f3 = (float) i3;
                int i4 = i3;
                float f4 = f2;
                canvas.drawRect(0.0f, f2, (float) getMeasuredWidth(), f3, DialogsActivity.this.searchAnimationProgress == 1.0f ? this.actionBarSearchPaint : DialogsActivity.this.actionBarDefaultPaint);
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
                        canvas.drawRect(0.0f, f4, (float) getMeasuredWidth(), f3, this.actionBarSearchPaint);
                    }
                    if (DialogsActivity.this.filterTabsView != null && DialogsActivity.this.filterTabsView.getVisibility() == 0) {
                        DialogsActivity.this.filterTabsView.setTranslationY((float) (actionBarFullHeight - (DialogsActivity.this.actionBar.getHeight() + DialogsActivity.this.filterTabsView.getMeasuredHeight())));
                    }
                    if (DialogsActivity.this.searchTabsView != null) {
                        float height = (float) (actionBarFullHeight - (DialogsActivity.this.actionBar.getHeight() + DialogsActivity.this.searchTabsView.getMeasuredHeight()));
                        if (DialogsActivity.this.searchAnimationTabsDelayedCrossfade) {
                            f = DialogsActivity.this.searchAnimationProgress < 0.5f ? 0.0f : (DialogsActivity.this.searchAnimationProgress - 0.5f) / 0.5f;
                        } else {
                            f = DialogsActivity.this.searchAnimationProgress;
                        }
                        DialogsActivity.this.searchTabsView.setTranslationY(height);
                        DialogsActivity.this.searchTabsView.setAlpha(f);
                        if (DialogsActivity.this.filtersView != null) {
                            DialogsActivity.this.filtersView.setTranslationY(height);
                            DialogsActivity.this.filtersView.setAlpha(f);
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
            float unused = DialogsActivity.this.tabsYOffset = 0.0f;
            if (DialogsActivity.this.filtersTabAnimator != null && DialogsActivity.this.filterTabsView != null && DialogsActivity.this.filterTabsView.getVisibility() == 0) {
                DialogsActivity dialogsActivity = DialogsActivity.this;
                float unused2 = dialogsActivity.tabsYOffset = (-(1.0f - dialogsActivity.filterTabsProgress)) * ((float) DialogsActivity.this.filterTabsView.getMeasuredHeight());
                DialogsActivity.this.filterTabsView.setTranslationY(DialogsActivity.this.actionBar.getTranslationY() + DialogsActivity.this.tabsYOffset);
                DialogsActivity.this.filterTabsView.setAlpha(DialogsActivity.this.filterTabsProgress);
                DialogsActivity.this.viewPages[0].setTranslationY((-(1.0f - DialogsActivity.this.filterTabsProgress)) * DialogsActivity.this.filterTabsMoveFrom);
            } else if (DialogsActivity.this.filterTabsView != null && DialogsActivity.this.filterTabsView.getVisibility() == 0) {
                DialogsActivity.this.filterTabsView.setTranslationY(DialogsActivity.this.actionBar.getTranslationY());
                DialogsActivity.this.filterTabsView.setAlpha(1.0f);
            }
            DialogsActivity.this.updateContextViewPosition();
            super.dispatchDraw(canvas);
            if (DialogsActivity.this.whiteActionBar && DialogsActivity.this.searchAnimationProgress > 0.0f && DialogsActivity.this.searchAnimationProgress < 1.0f && DialogsActivity.this.searchTabsView != null) {
                this.windowBackgroundPaint.setColor(Theme.getColor("windowBackgroundWhite"));
                Paint paint3 = this.windowBackgroundPaint;
                paint3.setAlpha((int) (((float) paint3.getAlpha()) * DialogsActivity.this.searchAnimationProgress));
                canvas.drawRect(0.0f, (float) (actionBarFullHeight + i2), (float) getMeasuredWidth(), (float) (i2 + DialogsActivity.this.actionBar.getMeasuredHeight() + DialogsActivity.this.searchTabsView.getMeasuredHeight()), this.windowBackgroundPaint);
            }
            if (DialogsActivity.this.fragmentContextView != null && DialogsActivity.this.fragmentContextView.isCallStyle()) {
                canvas.save();
                canvas2.translate(DialogsActivity.this.fragmentContextView.getX(), DialogsActivity.this.fragmentContextView.getY());
                DialogsActivity.this.fragmentContextView.setDrawOverlay(true);
                DialogsActivity.this.fragmentContextView.draw(canvas2);
                DialogsActivity.this.fragmentContextView.setDrawOverlay(false);
                canvas.restore();
            }
            if (DialogsActivity.this.blurredView != null && DialogsActivity.this.blurredView.getVisibility() == 0) {
                if (DialogsActivity.this.blurredView.getAlpha() == 1.0f) {
                    DialogsActivity.this.blurredView.draw(canvas2);
                } else if (DialogsActivity.this.blurredView.getAlpha() != 0.0f) {
                    canvas.saveLayerAlpha((float) DialogsActivity.this.blurredView.getLeft(), (float) DialogsActivity.this.blurredView.getTop(), (float) DialogsActivity.this.blurredView.getRight(), (float) DialogsActivity.this.blurredView.getBottom(), (int) (DialogsActivity.this.blurredView.getAlpha() * 255.0f), 31);
                    canvas2.translate((float) DialogsActivity.this.blurredView.getLeft(), (float) DialogsActivity.this.blurredView.getTop());
                    DialogsActivity.this.blurredView.draw(canvas2);
                    canvas.restore();
                }
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
                        if (DialogsActivity.this.filtersTabAnimator == null || DialogsActivity.this.filterTabsView == null || DialogsActivity.this.filterTabsView.getVisibility() != 0) {
                            childAt.setTranslationY(0.0f);
                        } else {
                            i3 = (int) (((float) i3) + DialogsActivity.this.filterTabsMoveFrom);
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
                org.telegram.ui.Components.ViewPagerFixed$TabsView r11 = r11.searchTabsView
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
                org.telegram.ui.Components.ViewPagerFixed$TabsView r11 = r11.searchTabsView
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
            if (DialogsActivity.this.filterTabsView == null || DialogsActivity.this.filterTabsView.isEditing() || DialogsActivity.this.searching || DialogsActivity.this.parentLayout.checkTransitionAnimation() || DialogsActivity.this.parentLayout.isInPreviewMode() || DialogsActivity.this.parentLayout.isPreviewOpenAnimationInProgress() || DialogsActivity.this.parentLayout.getDrawerLayoutContainer().isDrawerOpened() || ((motionEvent != null && !DialogsActivity.this.startedTracking && motionEvent.getY() <= ((float) DialogsActivity.this.actionBar.getMeasuredHeight()) + DialogsActivity.this.actionBar.getTranslationY()) || SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) != 5)) {
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
                        DialogsActivity.this.viewPages[0].dialogsAdapter.resume();
                        DialogsActivity.this.viewPages[1].dialogsAdapter.pause();
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
                    DialogsActivity.this.viewPages[0].dialogsAdapter.resume();
                    DialogsActivity.this.viewPages[1].dialogsAdapter.pause();
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
                                DialogsActivity.this.viewPages[0].dialogsAdapter.resume();
                                DialogsActivity.this.viewPages[1].dialogsAdapter.pause();
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
        private int lastListPadding;
        Paint paint = new Paint();
        private ViewPage parentPage;
        RectF rectF = new RectF();

        /* access modifiers changed from: protected */
        public boolean updateEmptyViewAnimated() {
            return true;
        }

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
            view.setTranslationX(0.0f);
            view.setAlpha(1.0f);
        }

        public void removeView(View view) {
            super.removeView(view);
            view.setTranslationY(0.0f);
            view.setTranslationX(0.0f);
            view.setAlpha(1.0f);
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
            this.parentPage.recyclerItemsEnterAnimator.dispatchDraw();
            super.dispatchDraw(canvas);
            if (drawMovingViewsOverlayed()) {
                this.paint.setColor(Theme.getColor("windowBackgroundWhite"));
                for (int i = 0; i < getChildCount(); i++) {
                    View childAt = getChildAt(i);
                    if (((childAt instanceof DialogCell) && ((DialogCell) childAt).isMoving()) || ((childAt instanceof DialogsAdapter.LastEmptyView) && ((DialogsAdapter.LastEmptyView) childAt).moving)) {
                        if (childAt.getAlpha() != 1.0f) {
                            this.rectF.set(childAt.getX(), childAt.getY(), childAt.getX() + ((float) childAt.getMeasuredWidth()), childAt.getY() + ((float) childAt.getMeasuredHeight()));
                            canvas.saveLayerAlpha(this.rectF, (int) (childAt.getAlpha() * 255.0f), 31);
                        } else {
                            canvas.save();
                        }
                        canvas.translate(childAt.getX(), childAt.getY());
                        canvas.drawRect(0.0f, 0.0f, (float) childAt.getMeasuredWidth(), (float) childAt.getMeasuredHeight(), this.paint);
                        childAt.draw(canvas);
                        canvas.restore();
                    }
                }
                invalidate();
            }
            if (DialogsActivity.this.slidingView != null && DialogsActivity.this.pacmanAnimation != null) {
                DialogsActivity.this.pacmanAnimation.draw(canvas, DialogsActivity.this.slidingView.getTop() + (DialogsActivity.this.slidingView.getMeasuredHeight() / 2));
            }
        }

        private boolean drawMovingViewsOverlayed() {
            return (getItemAnimator() == null || !getItemAnimator().isRunning() || (DialogsActivity.this.dialogRemoveFinished == 0 && DialogsActivity.this.dialogInsertFinished == 0 && DialogsActivity.this.dialogChangeFinished == 0)) ? false : true;
        }

        public boolean drawChild(Canvas canvas, View view, long j) {
            if (!drawMovingViewsOverlayed() || !(view instanceof DialogCell) || !((DialogCell) view).isMoving()) {
                return super.drawChild(canvas, view, j);
            }
            return true;
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.parentPage.recyclerItemsEnterAnimator.onDetached();
        }

        public void setAdapter(RecyclerView.Adapter adapter) {
            super.setAdapter(adapter);
            this.firstLayout = true;
        }

        private void checkIfAdapterValid() {
            RecyclerView.Adapter adapter = getAdapter();
            if (this.parentPage.lastItemsCount != adapter.getItemCount() && !DialogsActivity.this.dialogsListFrozen) {
                this.ignoreLayout = true;
                adapter.notifyDataSetChanged();
                this.ignoreLayout = false;
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            int i3;
            RecyclerView.ViewHolder findViewHolderForAdapterPosition;
            int measuredHeight = !DialogsActivity.this.onlySelect ? (DialogsActivity.this.filterTabsView == null || DialogsActivity.this.filterTabsView.getVisibility() != 0) ? DialogsActivity.this.actionBar.getMeasuredHeight() : AndroidUtilities.dp(44.0f) : 0;
            int findFirstVisibleItemPosition = this.parentPage.layoutManager.findFirstVisibleItemPosition();
            if (findFirstVisibleItemPosition != -1 && !DialogsActivity.this.dialogsListFrozen && this.parentPage.itemTouchhelper.isIdle() && (findViewHolderForAdapterPosition = this.parentPage.listView.findViewHolderForAdapterPosition(findFirstVisibleItemPosition)) != null) {
                int top = findViewHolderForAdapterPosition.itemView.getTop();
                this.ignoreLayout = true;
                this.parentPage.layoutManager.scrollToPositionWithOffset(findFirstVisibleItemPosition, (int) (((float) (top - this.lastListPadding)) + DialogsActivity.this.scrollAdditionalOffset));
                this.ignoreLayout = false;
            }
            if (!DialogsActivity.this.onlySelect) {
                this.ignoreLayout = true;
                if (DialogsActivity.this.filterTabsView == null || DialogsActivity.this.filterTabsView.getVisibility() != 0) {
                    i3 = (!DialogsActivity.this.inPreviewMode || Build.VERSION.SDK_INT < 21) ? 0 : AndroidUtilities.statusBarHeight;
                } else {
                    i3 = ActionBar.getCurrentActionBarHeight() + (DialogsActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
                }
                setTopGlowOffset(measuredHeight);
                setPadding(0, measuredHeight, 0, 0);
                this.parentPage.progressView.setPaddingTop(measuredHeight);
                this.ignoreLayout = false;
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
            if (!DialogsActivity.this.onlySelect && this.appliedPaddingTop != measuredHeight && DialogsActivity.this.viewPages != null && DialogsActivity.this.viewPages.length > 1) {
                DialogsActivity.this.viewPages[1].setTranslationX((float) DialogsActivity.this.viewPages[0].getMeasuredWidth());
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            this.lastListPadding = getPaddingTop();
            float unused = DialogsActivity.this.scrollAdditionalOffset = 0.0f;
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
                if (dialogCell != null) {
                    boolean unused = DialogsActivity.this.disableActionBarScrolling = true;
                    boolean unused2 = DialogsActivity.this.waitingForScrollFinished = true;
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
                PullForegroundDrawable access$9200 = this.parentPage.pullForegroundDrawable;
                if (this.parentPage.archivePullViewState != 0) {
                    z = true;
                }
                access$9200.setWillDraw(z);
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
                if (!(this.parentPage.itemTouchhelper.checkHorizontalSwipe((RecyclerView.ViewHolder) null, 4) == 0 || this.parentPage.swipeController.currentItemViewHolder == null)) {
                    View view = this.parentPage.swipeController.currentItemViewHolder.itemView;
                    if (view instanceof DialogCell) {
                        DialogCell dialogCell = (DialogCell) view;
                        long dialogId = dialogCell.getDialogId();
                        if (DialogObject.isFolderDialogId(dialogId)) {
                            toggleArchiveHidden(false, dialogCell);
                        } else {
                            DialogsActivity dialogsActivity = DialogsActivity.this;
                            TLRPC$Dialog tLRPC$Dialog = dialogsActivity.getDialogsArray(dialogsActivity.currentAccount, this.parentPage.dialogsType, DialogsActivity.this.folderId, false).get(dialogCell.getDialogIndex());
                            if (SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 1) {
                                ArrayList arrayList = new ArrayList();
                                arrayList.add(Long.valueOf(dialogId));
                                int unused2 = DialogsActivity.this.canReadCount = (tLRPC$Dialog.unread_count > 0 || tLRPC$Dialog.unread_mark) ? 1 : 0;
                                DialogsActivity.this.perfromSelectedDialogsAction(arrayList, 101, true);
                            } else if (SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 3) {
                                ArrayList arrayList2 = new ArrayList();
                                arrayList2.add(Long.valueOf(dialogId));
                                DialogsActivity dialogsActivity2 = DialogsActivity.this;
                                int unused3 = dialogsActivity2.canMuteCount = MessagesController.getInstance(dialogsActivity2.currentAccount).isDialogMuted(dialogId) ^ true ? 1 : 0;
                                DialogsActivity dialogsActivity3 = DialogsActivity.this;
                                int unused4 = dialogsActivity3.canUnmuteCount = dialogsActivity3.canMuteCount > 0 ? 0 : 1;
                                DialogsActivity.this.perfromSelectedDialogsAction(arrayList2, 104, true);
                            } else if (SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 0) {
                                ArrayList arrayList3 = new ArrayList();
                                arrayList3.add(Long.valueOf(dialogId));
                                int unused5 = DialogsActivity.this.canPinCount = DialogsActivity.this.isDialogPinned(tLRPC$Dialog) ^ true ? 1 : 0;
                                DialogsActivity.this.perfromSelectedDialogsAction(arrayList3, 100, true);
                            } else if (SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 4) {
                                ArrayList arrayList4 = new ArrayList();
                                arrayList4.add(Long.valueOf(dialogId));
                                DialogsActivity.this.perfromSelectedDialogsAction(arrayList4, 102, true);
                            }
                        }
                    }
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
                        boolean unused6 = DialogsActivity.this.disableActionBarScrolling = true;
                        smoothScrollBy(0, top, CubicBezierInterpolator.EASE_OUT_QUINT);
                        int unused7 = this.parentPage.archivePullViewState = 2;
                    } else if (this.parentPage.archivePullViewState != 1) {
                        if (getViewOffset() == 0.0f) {
                            boolean unused8 = DialogsActivity.this.disableActionBarScrolling = true;
                            smoothScrollBy(0, findViewByPosition.getTop() - paddingTop, CubicBezierInterpolator.EASE_OUT_QUINT);
                        }
                        if (!DialogsActivity.this.canShowHiddenArchive) {
                            boolean unused9 = DialogsActivity.this.canShowHiddenArchive = true;
                            performHapticFeedback(3, 2);
                            if (this.parentPage.pullForegroundDrawable != null) {
                                this.parentPage.pullForegroundDrawable.colorize(true);
                            }
                        }
                        ((DialogCell) findViewByPosition).startOutAnimation();
                        int unused10 = this.parentPage.archivePullViewState = 1;
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
        /* access modifiers changed from: private */
        public RecyclerView.ViewHolder currentItemViewHolder;
        private ViewPage parentPage;
        /* access modifiers changed from: private */
        public boolean swipeFolderBack;
        /* access modifiers changed from: private */
        public boolean swipingFolder;

        public float getSwipeEscapeVelocity(float f) {
            return 3500.0f;
        }

        public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
            return 0.45f;
        }

        public float getSwipeVelocityThreshold(float f) {
            return Float.MAX_VALUE;
        }

        public SwipeController(ViewPage viewPage) {
            this.parentPage = viewPage;
        }

        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            TLRPC$Dialog tLRPC$Dialog;
            if (!DialogsActivity.this.waitingForDialogsAnimationEnd(this.parentPage) && (DialogsActivity.this.parentLayout == null || !DialogsActivity.this.parentLayout.isInPreviewMode())) {
                if (this.swipingFolder && this.swipeFolderBack) {
                    View view = viewHolder.itemView;
                    if (view instanceof DialogCell) {
                        ((DialogCell) view).swipeCanceled = true;
                    }
                    this.swipingFolder = false;
                    return 0;
                } else if (!DialogsActivity.this.onlySelect && this.parentPage.isDefaultDialogType() && DialogsActivity.this.slidingView == null) {
                    View view2 = viewHolder.itemView;
                    if (view2 instanceof DialogCell) {
                        DialogCell dialogCell = (DialogCell) view2;
                        long dialogId = dialogCell.getDialogId();
                        if (DialogsActivity.this.actionBar.isActionModeShowed()) {
                            TLRPC$Dialog tLRPC$Dialog2 = DialogsActivity.this.getMessagesController().dialogs_dict.get(dialogId);
                            if (!DialogsActivity.this.allowMoving || tLRPC$Dialog2 == null || !DialogsActivity.this.isDialogPinned(tLRPC$Dialog2) || DialogObject.isFolderDialogId(dialogId)) {
                                return 0;
                            }
                            DialogCell unused = DialogsActivity.this.movingView = (DialogCell) viewHolder.itemView;
                            DialogsActivity.this.movingView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                            this.swipeFolderBack = false;
                            return ItemTouchHelper.Callback.makeMovementFlags(3, 0);
                        } else if (!((DialogsActivity.this.filterTabsView != null && DialogsActivity.this.filterTabsView.getVisibility() == 0 && SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 5) || !DialogsActivity.this.allowSwipeDuringCurrentTouch || ((dialogId == ((long) DialogsActivity.this.getUserConfig().clientUserId) && SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 2) || dialogId == 777000 || (DialogsActivity.this.getMessagesController().isPromoDialog(dialogId, false) && DialogsActivity.this.getMessagesController().promoDialogType != 1)))) {
                            boolean z = DialogsActivity.this.folderId == 0 && (SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 3 || SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 1 || SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 0 || SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 4);
                            if (SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 1) {
                                MessagesController.DialogFilter dialogFilter = null;
                                if (DialogsActivity.this.viewPages[0].dialogsType == 7 || DialogsActivity.this.viewPages[0].dialogsType == 8) {
                                    dialogFilter = DialogsActivity.this.getMessagesController().selectedDialogFilter[DialogsActivity.this.viewPages[0].dialogsType == 8 ? (char) 1 : 0];
                                }
                                if (!(dialogFilter == null || (dialogFilter.flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ) == 0 || (tLRPC$Dialog = DialogsActivity.this.getMessagesController().dialogs_dict.get(dialogId)) == null || dialogFilter.alwaysShow(DialogsActivity.this.currentAccount, tLRPC$Dialog) || (tLRPC$Dialog.unread_count <= 0 && !tLRPC$Dialog.unread_mark))) {
                                    z = false;
                                }
                            }
                            this.swipeFolderBack = false;
                            this.swipingFolder = (z && !DialogObject.isFolderDialogId(dialogCell.getDialogId())) || (SharedConfig.archiveHidden && DialogObject.isFolderDialogId(dialogCell.getDialogId()));
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
                long dialogId = dialogCell.getDialogId();
                int i2 = 0;
                if (DialogObject.isFolderDialogId(dialogId)) {
                    this.parentPage.listView.toggleArchiveHidden(false, dialogCell);
                    return;
                }
                TLRPC$Dialog tLRPC$Dialog = DialogsActivity.this.getMessagesController().dialogs_dict.get(dialogId);
                if (tLRPC$Dialog != null) {
                    if (SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 1) {
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(Long.valueOf(dialogId));
                        DialogsActivity dialogsActivity = DialogsActivity.this;
                        if (tLRPC$Dialog.unread_count > 0 || tLRPC$Dialog.unread_mark) {
                            i2 = 1;
                        }
                        int unused = dialogsActivity.canReadCount = i2;
                        DialogsActivity.this.perfromSelectedDialogsAction(arrayList, 101, true);
                        return;
                    }
                    DialogCell unused2 = DialogsActivity.this.slidingView = dialogCell;
                    $$Lambda$DialogsActivity$SwipeController$cBM6A0hxg2lmI90ckb2t8qchGWA r0 = new Runnable(tLRPC$Dialog, this.parentPage.dialogsAdapter.getItemCount(), viewHolder.getAdapterPosition()) {
                        public final /* synthetic */ TLRPC$Dialog f$1;
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
                            PacmanAnimation unused3 = DialogsActivity.this.pacmanAnimation = new PacmanAnimation(this.parentPage.listView);
                        }
                        DialogsActivity.this.pacmanAnimation.setFinishRunnable(r0);
                        DialogsActivity.this.pacmanAnimation.start();
                        return;
                    }
                    r0.run();
                    return;
                }
                return;
            }
            DialogCell unused4 = DialogsActivity.this.slidingView = null;
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onSwiped$1 */
        public /* synthetic */ void lambda$onSwiped$1$DialogsActivity$SwipeController(TLRPC$Dialog tLRPC$Dialog, int i, int i2) {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition;
            if (DialogsActivity.this.frozenDialogsList != null) {
                DialogsActivity.this.frozenDialogsList.remove(tLRPC$Dialog);
                int i3 = tLRPC$Dialog.pinnedNum;
                DialogCell unused = DialogsActivity.this.slidingView = null;
                this.parentPage.listView.invalidate();
                int findLastVisibleItemPosition = this.parentPage.layoutManager.findLastVisibleItemPosition();
                if (findLastVisibleItemPosition == i - 1) {
                    this.parentPage.layoutManager.findViewByPosition(findLastVisibleItemPosition).requestLayout();
                }
                boolean z = false;
                if (DialogsActivity.this.getMessagesController().isPromoDialog(tLRPC$Dialog.id, false)) {
                    DialogsActivity.this.getMessagesController().hidePromoDialog();
                    this.parentPage.dialogsItemAnimator.prepareForRemove();
                    ViewPage.access$9910(this.parentPage);
                    this.parentPage.dialogsAdapter.notifyItemRemoved(i2);
                    int unused2 = DialogsActivity.this.dialogRemoveFinished = 2;
                    return;
                }
                int addDialogToFolder = DialogsActivity.this.getMessagesController().addDialogToFolder(tLRPC$Dialog.id, DialogsActivity.this.folderId == 0 ? 1 : 0, -1, 0);
                if (!(addDialogToFolder == 2 && i2 == 0)) {
                    this.parentPage.dialogsItemAnimator.prepareForRemove();
                    ViewPage.access$9910(this.parentPage);
                    this.parentPage.dialogsAdapter.notifyItemRemoved(i2);
                    int unused3 = DialogsActivity.this.dialogRemoveFinished = 2;
                }
                if (DialogsActivity.this.folderId == 0) {
                    if (addDialogToFolder == 2) {
                        this.parentPage.dialogsItemAnimator.prepareForRemove();
                        if (i2 == 0) {
                            int unused4 = DialogsActivity.this.dialogChangeFinished = 2;
                            DialogsActivity.this.setDialogsListFrozen(true);
                            this.parentPage.dialogsAdapter.notifyItemChanged(0);
                        } else {
                            ViewPage.access$9908(this.parentPage);
                            this.parentPage.dialogsAdapter.notifyItemInserted(0);
                            if (!SharedConfig.archiveHidden && this.parentPage.layoutManager.findFirstVisibleItemPosition() == 0) {
                                boolean unused5 = DialogsActivity.this.disableActionBarScrolling = true;
                                this.parentPage.listView.smoothScrollBy(0, -AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f));
                            }
                        }
                        DialogsActivity dialogsActivity = DialogsActivity.this;
                        DialogsActivity.this.frozenDialogsList.add(0, dialogsActivity.getDialogsArray(dialogsActivity.currentAccount, this.parentPage.dialogsType, DialogsActivity.this.folderId, false).get(0));
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
                    DialogsActivity.this.getUndoView().showWithAction(tLRPC$Dialog.id, z ? 2 : 3, (Runnable) null, new Runnable(tLRPC$Dialog, i3) {
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
                if (DialogsActivity.this.folderId != 0 && DialogsActivity.this.frozenDialogsList.isEmpty()) {
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
                    ViewPage.access$9908(this.parentPage);
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
                    DialogsActivity.this.frozenDialogsList.remove(0);
                    this.parentPage.dialogsItemAnimator.prepareForRemove();
                    ViewPage.access$9910(this.parentPage);
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
            this.currentItemViewHolder = viewHolder;
            if (viewHolder != null) {
                View view = viewHolder.itemView;
                if (view instanceof DialogCell) {
                    ((DialogCell) view).swipeCanceled = false;
                }
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
        getNotificationCenter().addObserver(this, NotificationCenter.didClearDatabase);
        loadDialogs(getAccountInstance());
        getMessagesController().loadPinnedDialogs(this.folderId, 0, (ArrayList<Long>) null);
        return true;
    }

    public static void loadDialogs(AccountInstance accountInstance) {
        int currentAccount = accountInstance.getCurrentAccount();
        if (!dialogsLoaded[currentAccount]) {
            MessagesController messagesController = accountInstance.getMessagesController();
            messagesController.loadGlobalNotificationsSettings();
            messagesController.loadDialogs(0, 0, 100, true);
            messagesController.loadHintDialogs();
            messagesController.loadUserInfo(accountInstance.getUserConfig().getCurrentUser(), false, 0);
            accountInstance.getContactsController().checkInviteText();
            accountInstance.getMediaDataController().loadRecents(2, false, true, false);
            accountInstance.getMediaDataController().checkFeaturedStickers();
            Iterator<String> it = messagesController.diceEmojies.iterator();
            while (it.hasNext()) {
                accountInstance.getMediaDataController().loadStickersByEmojiOrName(it.next(), true, true);
            }
            dialogsLoaded[currentAccount] = true;
        }
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
        getNotificationCenter().removeObserver(this, NotificationCenter.didClearDatabase);
        ChatActivityEnterView chatActivityEnterView = this.commentView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.onDestroy();
        }
        UndoView[] undoViewArr = this.undoView;
        if (undoViewArr[0] != null) {
            undoViewArr[0].hide(true, 0);
        }
        getNotificationCenter().onAnimationFinish(this.animationIndex);
        this.delegate = null;
    }

    /* access modifiers changed from: protected */
    public ActionBar createActionBar(Context context) {
        AnonymousClass2 r0 = new ActionBar(context) {
            public void setTranslationY(float f) {
                if (f != getTranslationY()) {
                    DialogsActivity.this.fragmentView.invalidate();
                }
                super.setTranslationY(f);
            }

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

    /* JADX WARNING: type inference failed for: r12v1, types: [int, boolean] */
    /* JADX WARNING: type inference failed for: r12v7 */
    /* JADX WARNING: type inference failed for: r12v8 */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x07b2  */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x07bf  */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x07d7 A[LOOP:2: B:180:0x07d5->B:181:0x07d7, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:184:0x07fe  */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x086f  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x0872  */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x087f  */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x0900  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x090c  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x0935  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r40) {
        /*
            r39 = this;
            r10 = r39
            r11 = r40
            r12 = 0
            r10.searching = r12
            r10.searchWas = r12
            r13 = 0
            r10.pacmanAnimation = r13
            java.util.ArrayList<java.lang.Long> r0 = r10.selectedDialogs
            r0.clear()
            android.view.ViewConfiguration r0 = android.view.ViewConfiguration.get(r40)
            int r0 = r0.getScaledMaximumFlingVelocity()
            r10.maximumVelocity = r0
            org.telegram.ui.-$$Lambda$DialogsActivity$Qx9c2SGTz8VEtmWATYZYK47zjCk r0 = new org.telegram.ui.-$$Lambda$DialogsActivity$Qx9c2SGTz8VEtmWATYZYK47zjCk
            r0.<init>(r11)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r6 = r0.createMenu()
            boolean r0 = r10.onlySelect
            r14 = 2131625203(0x7f0e04f3, float:1.8877607E38)
            java.lang.String r15 = "Done"
            r9 = 0
            r8 = 2
            r7 = 8
            r5 = 1
            if (r0 != 0) goto L_0x00f5
            java.lang.String r0 = r10.searchString
            if (r0 != 0) goto L_0x00f5
            int r0 = r10.folderId
            if (r0 != 0) goto L_0x00f5
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            r2 = 0
            java.lang.String r0 = "actionBarDefaultSelector"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            java.lang.String r0 = "actionBarDefaultIcon"
            int r16 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            r17 = 1
            r0 = r4
            r1 = r40
            r13 = r4
            r4 = r16
            r12 = 1
            r5 = r17
            r0.<init>(r1, r2, r3, r4, r5)
            r10.doneItem = r13
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r14)
            java.lang.String r0 = r0.toUpperCase()
            r13.setText(r0)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r10.doneItem
            r18 = -2
            r19 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r20 = 53
            r21 = 0
            r22 = 0
            r23 = 1092616192(0x41200000, float:10.0)
            r24 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r18, r19, r20, r21, r22, r23, r24)
            r0.addView(r1, r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r10.doneItem
            org.telegram.ui.-$$Lambda$DialogsActivity$SueaA71vXmo31QzmfxdDgaRyj5I r1 = new org.telegram.ui.-$$Lambda$DialogsActivity$SueaA71vXmo31QzmfxdDgaRyj5I
            r1.<init>()
            r0.setOnClickListener(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r10.doneItem
            r0.setAlpha(r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r10.doneItem
            r0.setVisibility(r7)
            org.telegram.ui.Components.ProxyDrawable r0 = new org.telegram.ui.Components.ProxyDrawable
            r0.<init>(r11)
            r10.proxyDrawable = r0
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.addItem((int) r8, (android.graphics.drawable.Drawable) r0)
            r10.proxyItem = r0
            r1 = 2131627016(0x7f0e0CLASSNAME, float:1.8881285E38)
            java.lang.String r2 = "ProxySettings"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setContentDescription(r1)
            org.telegram.ui.Components.RLottieDrawable r0 = new org.telegram.ui.Components.RLottieDrawable
            r19 = 2131558456(0x7f0d0038, float:1.8742228E38)
            r1 = 1105199104(0x41e00000, float:28.0)
            int r21 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r22 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r23 = 1
            r24 = 0
            java.lang.String r20 = "passcode_lock_open"
            r18 = r0
            r18.<init>((int) r19, (java.lang.String) r20, (int) r21, (int) r22, (boolean) r23, (int[]) r24)
            r10.passcodeDrawable = r0
            org.telegram.ui.Components.RLottieDrawable r0 = new org.telegram.ui.Components.RLottieDrawable
            r26 = 2131558455(0x7f0d0037, float:1.8742226E38)
            int r28 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r29 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r30 = 1
            r31 = 0
            java.lang.String r27 = "passcode_lock_close"
            r25 = r0
            r25.<init>((int) r26, (java.lang.String) r27, (int) r28, (int) r29, (boolean) r30, (int[]) r31)
            r10.passcodeDrawable2 = r0
            org.telegram.ui.Components.RLottieDrawable r0 = r10.passcodeDrawable
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.addItem((int) r12, (android.graphics.drawable.Drawable) r0)
            r10.passcodeItem = r0
            r0 = 0
            r10.updatePasscodeButton(r0)
            r10.updateProxyButton(r0)
            goto L_0x00f7
        L_0x00f5:
            r0 = 0
            r12 = 1
        L_0x00f7:
            r1 = 2131165478(0x7var_, float:1.7945174E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r6.addItem((int) r0, (int) r1)
            r1.setIsSearchField(r12, r12)
            org.telegram.ui.DialogsActivity$3 r0 = new org.telegram.ui.DialogsActivity$3
            r0.<init>()
            r1.setActionBarMenuItemSearchListener(r0)
            r10.searchItem = r1
            java.lang.String r0 = "Search"
            r2 = 2131627231(0x7f0e0cdf, float:1.888172E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r0, r2)
            r1.setSearchFieldHint(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r10.searchItem
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r2)
            r1.setContentDescription(r0)
            boolean r0 = r10.onlySelect
            java.lang.String r13 = "actionBarDefault"
            r5 = 10
            r4 = 3
            if (r0 == 0) goto L_0x0171
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r1 = 2131165468(0x7var_c, float:1.7945154E38)
            r0.setBackButtonImage(r1)
            int r0 = r10.initialDialogsType
            if (r0 != r4) goto L_0x0148
            java.lang.String r1 = r10.selectAlertString
            if (r1 != 0) goto L_0x0148
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r1 = 2131625564(0x7f0e065c, float:1.887834E38)
            java.lang.String r2 = "ForwardTo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x0167
        L_0x0148:
            if (r0 != r5) goto L_0x0159
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r1 = 2131627289(0x7f0e0d19, float:1.8881838E38)
            java.lang.String r2 = "SelectChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x0167
        L_0x0159:
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r1 = 2131627288(0x7f0e0d18, float:1.8881836E38)
            java.lang.String r2 = "SelectChat"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
        L_0x0167:
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r0.setBackgroundColor(r1)
            goto L_0x01d8
        L_0x0171:
            java.lang.String r0 = r10.searchString
            if (r0 != 0) goto L_0x0195
            int r0 = r10.folderId
            if (r0 == 0) goto L_0x017a
            goto L_0x0195
        L_0x017a:
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            org.telegram.ui.ActionBar.MenuDrawable r1 = new org.telegram.ui.ActionBar.MenuDrawable
            r1.<init>()
            r10.menuDrawable = r1
            r0.setBackButtonDrawable(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r1 = 2131624002(0x7f0e0042, float:1.8875171E38)
            java.lang.String r2 = "AccDescrOpenMenu"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setBackButtonContentDescription(r1)
            goto L_0x01a2
        L_0x0195:
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            org.telegram.ui.ActionBar.BackDrawable r1 = new org.telegram.ui.ActionBar.BackDrawable
            r2 = 0
            r1.<init>(r2)
            r10.backDrawable = r1
            r0.setBackButtonDrawable(r1)
        L_0x01a2:
            int r0 = r10.folderId
            if (r0 == 0) goto L_0x01b5
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r1 = 2131624304(0x7f0e0170, float:1.8875784E38)
            java.lang.String r2 = "ArchivedChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x01cf
        L_0x01b5:
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r0 == 0) goto L_0x01c1
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            java.lang.String r1 = "Telegram Beta"
            r0.setTitle(r1)
            goto L_0x01cf
        L_0x01c1:
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r1 = 2131624281(0x7f0e0159, float:1.8875737E38)
            java.lang.String r2 = "AppName"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
        L_0x01cf:
            int r0 = r10.folderId
            if (r0 != 0) goto L_0x01d8
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r0.setSupportsHolidayImage(r12)
        L_0x01d8:
            boolean r0 = r10.onlySelect
            if (r0 != 0) goto L_0x01ec
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r1 = 0
            r0.setAddToContainer(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r0.setCastShadows(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r0.setClipContent(r12)
        L_0x01ec:
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            org.telegram.ui.-$$Lambda$DialogsActivity$JSCaSgR2t00VtHN1RM1I7hZoKos r1 = new org.telegram.ui.-$$Lambda$DialogsActivity$JSCaSgR2t00VtHN1RM1I7hZoKos
            r1.<init>()
            r0.setTitleActionRunnable(r1)
            int r0 = r10.initialDialogsType
            if (r0 != 0) goto L_0x0228
            int r0 = r10.folderId
            if (r0 != 0) goto L_0x0228
            boolean r0 = r10.onlySelect
            if (r0 != 0) goto L_0x0228
            java.lang.String r0 = r10.searchString
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0228
            org.telegram.ui.DialogsActivity$4 r0 = new org.telegram.ui.DialogsActivity$4
            r0.<init>()
            r10.scrimPaint = r0
            org.telegram.ui.DialogsActivity$5 r0 = new org.telegram.ui.DialogsActivity$5
            r0.<init>(r11)
            r10.filterTabsView = r0
            r0.setVisibility(r7)
            r0 = 0
            r10.canShowFilterTabsView = r0
            org.telegram.ui.Components.FilterTabsView r0 = r10.filterTabsView
            org.telegram.ui.DialogsActivity$6 r1 = new org.telegram.ui.DialogsActivity$6
            r1.<init>()
            r0.setDelegate(r1)
        L_0x0228:
            boolean r0 = r10.allowSwitchAccount
            r17 = 1113587712(0x42600000, float:56.0)
            if (r0 == 0) goto L_0x02b8
            int r0 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()
            if (r0 <= r12) goto L_0x02b8
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r1 = 0
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.addItemWithWidth(r12, r1, r0)
            r10.switchItem = r0
            org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable
            r0.<init>()
            r1 = 1094713344(0x41400000, float:12.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setTextSize(r1)
            org.telegram.ui.Components.BackupImageView r1 = new org.telegram.ui.Components.BackupImageView
            r1.<init>(r11)
            r2 = 1099956224(0x41900000, float:18.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setRoundRadius(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r10.switchItem
            r3 = 36
            r6 = 36
            r5 = 17
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r6, r5)
            r2.addView(r1, r3)
            org.telegram.messenger.UserConfig r2 = r39.getUserConfig()
            org.telegram.tgnet.TLRPC$User r2 = r2.getCurrentUser()
            r0.setInfo((org.telegram.tgnet.TLRPC$User) r2)
            org.telegram.messenger.ImageReceiver r3 = r1.getImageReceiver()
            int r5 = r10.currentAccount
            r3.setCurrentAccount(r5)
            r3 = 0
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForUser(r2, r3)
            java.lang.String r6 = "50_50"
            r1.setImage((org.telegram.messenger.ImageLocation) r5, (java.lang.String) r6, (android.graphics.drawable.Drawable) r0, (java.lang.Object) r2)
            r0 = 0
        L_0x0289:
            if (r0 >= r4) goto L_0x02b8
            org.telegram.messenger.AccountInstance r1 = org.telegram.messenger.AccountInstance.getInstance(r0)
            org.telegram.messenger.UserConfig r1 = r1.getUserConfig()
            org.telegram.tgnet.TLRPC$User r1 = r1.getCurrentUser()
            if (r1 == 0) goto L_0x02b4
            org.telegram.ui.Cells.AccountSelectCell r1 = new org.telegram.ui.Cells.AccountSelectCell
            r1.<init>(r11, r3)
            r1.setAccount(r0, r12)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r10.switchItem
            int r3 = r0 + 10
            r5 = 1130758144(0x43660000, float:230.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r6 = 1111490560(0x42400000, float:48.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r2.addSubItem((int) r3, (android.view.View) r1, (int) r5, (int) r6)
        L_0x02b4:
            int r0 = r0 + 1
            r3 = 0
            goto L_0x0289
        L_0x02b8:
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r0.setAllowOverlayTitle(r12)
            androidx.recyclerview.widget.RecyclerView r0 = r10.sideMenu
            if (r0 == 0) goto L_0x02de
            java.lang.String r1 = "chats_menuBackground"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setBackgroundColor(r1)
            androidx.recyclerview.widget.RecyclerView r0 = r10.sideMenu
            java.lang.String r1 = "chats_menuBackground"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setGlowColor(r1)
            androidx.recyclerview.widget.RecyclerView r0 = r10.sideMenu
            androidx.recyclerview.widget.RecyclerView$Adapter r0 = r0.getAdapter()
            r0.notifyDataSetChanged()
        L_0x02de:
            r39.createActionMode()
            org.telegram.ui.DialogsActivity$ContentView r6 = new org.telegram.ui.DialogsActivity$ContentView
            r6.<init>(r11)
            r10.fragmentView = r6
            int r0 = r10.folderId
            if (r0 != 0) goto L_0x02f6
            int r0 = r10.initialDialogsType
            if (r0 != 0) goto L_0x02f6
            boolean r0 = r10.onlySelect
            if (r0 != 0) goto L_0x02f6
            r5 = 2
            goto L_0x02f7
        L_0x02f6:
            r5 = 1
        L_0x02f7:
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = new org.telegram.ui.DialogsActivity.ViewPage[r5]
            r10.viewPages = r0
            r3 = 0
        L_0x02fc:
            r2 = -1082130432(0xffffffffbvar_, float:-1.0)
            r1 = -2
            r0 = -1
            if (r3 >= r5) goto L_0x04fe
            org.telegram.ui.DialogsActivity$7 r14 = new org.telegram.ui.DialogsActivity$7
            r14.<init>(r11)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r0, r2)
            r6.addView(r14, r4)
            int r4 = r10.initialDialogsType
            int unused = r14.dialogsType = r4
            org.telegram.ui.DialogsActivity$ViewPage[] r4 = r10.viewPages
            r4[r3] = r14
            org.telegram.ui.Components.FlickerLoadingView r4 = new org.telegram.ui.Components.FlickerLoadingView
            r4.<init>(r11)
            org.telegram.ui.Components.FlickerLoadingView unused = r14.progressView = r4
            org.telegram.ui.Components.FlickerLoadingView r4 = r14.progressView
            r8 = 7
            r4.setViewType(r8)
            org.telegram.ui.Components.FlickerLoadingView r4 = r14.progressView
            r4.setVisibility(r7)
            org.telegram.ui.Components.FlickerLoadingView r4 = r14.progressView
            r8 = 17
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r1, r1, r8)
            r14.addView(r4, r1)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r1 = new org.telegram.ui.DialogsActivity$DialogsRecyclerView
            r1.<init>(r11, r14)
            org.telegram.ui.DialogsActivity.DialogsRecyclerView unused = r14.listView = r1
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r1 = r14.listView
            r4 = 0
            r1.setAnimateEmptyView(r12, r4)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r1 = r14.listView
            r1.setClipToPadding(r4)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r1 = r14.listView
            r1.setPivotY(r9)
            org.telegram.ui.DialogsActivity$8 r1 = new org.telegram.ui.DialogsActivity$8
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r4 = r14.listView
            r1.<init>(r4, r14)
            org.telegram.ui.Components.DialogsItemAnimator unused = r14.dialogsItemAnimator = r1
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r1 = r14.listView
            org.telegram.ui.Components.DialogsItemAnimator r4 = r14.dialogsItemAnimator
            r1.setItemAnimator(r4)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r1 = r14.listView
            r1.setVerticalScrollBarEnabled(r12)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r1 = r14.listView
            r1.setInstantClick(r12)
            org.telegram.ui.DialogsActivity$9 r1 = new org.telegram.ui.DialogsActivity$9
            r1.<init>(r11, r14)
            androidx.recyclerview.widget.LinearLayoutManager unused = r14.layoutManager = r1
            androidx.recyclerview.widget.LinearLayoutManager r1 = r14.layoutManager
            r1.setOrientation(r12)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r1 = r14.listView
            androidx.recyclerview.widget.LinearLayoutManager r4 = r14.layoutManager
            r1.setLayoutManager(r4)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r1 = r14.listView
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x03a2
            r4 = 1
            goto L_0x03a3
        L_0x03a2:
            r4 = 2
        L_0x03a3:
            r1.setVerticalScrollbarPosition(r4)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r1 = r14.listView
            android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r0, r2)
            r14.addView(r1, r0)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r14.listView
            org.telegram.ui.-$$Lambda$DialogsActivity$9g5JEnTqVRFmA3Hm-rAwcqvWjKE r1 = new org.telegram.ui.-$$Lambda$DialogsActivity$9g5JEnTqVRFmA3Hm-rAwcqvWjKE
            r1.<init>(r14)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r14.listView
            org.telegram.ui.DialogsActivity$11 r1 = new org.telegram.ui.DialogsActivity$11
            r1.<init>(r14)
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended) r1)
            org.telegram.ui.DialogsActivity$SwipeController r0 = new org.telegram.ui.DialogsActivity$SwipeController
            r0.<init>(r14)
            org.telegram.ui.DialogsActivity.SwipeController unused = r14.swipeController = r0
            org.telegram.ui.Components.RecyclerItemsEnterAnimator r0 = new org.telegram.ui.Components.RecyclerItemsEnterAnimator
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r1 = r14.listView
            r0.<init>(r1)
            org.telegram.ui.Components.RecyclerItemsEnterAnimator unused = r14.recyclerItemsEnterAnimator = r0
            androidx.recyclerview.widget.ItemTouchHelper r0 = new androidx.recyclerview.widget.ItemTouchHelper
            org.telegram.ui.DialogsActivity$SwipeController r1 = r14.swipeController
            r0.<init>(r1)
            androidx.recyclerview.widget.ItemTouchHelper unused = r14.itemTouchhelper = r0
            androidx.recyclerview.widget.ItemTouchHelper r0 = r14.itemTouchhelper
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r1 = r14.listView
            r0.attachToRecyclerView(r1)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r14.listView
            org.telegram.ui.DialogsActivity$12 r1 = new org.telegram.ui.DialogsActivity$12
            r1.<init>(r14)
            r0.setOnScrollListener(r1)
            boolean r0 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r0 == 0) goto L_0x0406
            r0 = 2
            goto L_0x0407
        L_0x0406:
            r0 = 0
        L_0x0407:
            int unused = r14.archivePullViewState = r0
            org.telegram.ui.Components.PullForegroundDrawable r0 = r14.pullForegroundDrawable
            if (r0 != 0) goto L_0x0453
            int r0 = r10.folderId
            if (r0 != 0) goto L_0x0453
            org.telegram.ui.DialogsActivity$13 r0 = new org.telegram.ui.DialogsActivity$13
            r1 = 2131624075(0x7f0e008b, float:1.887532E38)
            java.lang.String r2 = "AccSwipeForArchive"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 2131624074(0x7f0e008a, float:1.8875317E38)
            java.lang.String r4 = "AccReleaseForArchive"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r0.<init>(r10, r1, r2, r14)
            org.telegram.ui.Components.PullForegroundDrawable unused = r14.pullForegroundDrawable = r0
            boolean r0 = r39.hasHiddenArchive()
            if (r0 == 0) goto L_0x043c
            org.telegram.ui.Components.PullForegroundDrawable r0 = r14.pullForegroundDrawable
            r0.showHidden()
            goto L_0x0443
        L_0x043c:
            org.telegram.ui.Components.PullForegroundDrawable r0 = r14.pullForegroundDrawable
            r0.doNotShow()
        L_0x0443:
            org.telegram.ui.Components.PullForegroundDrawable r0 = r14.pullForegroundDrawable
            int r1 = r14.archivePullViewState
            if (r1 == 0) goto L_0x044f
            r1 = 1
            goto L_0x0450
        L_0x044f:
            r1 = 0
        L_0x0450:
            r0.setWillDraw(r1)
        L_0x0453:
            org.telegram.ui.DialogsActivity$14 r8 = new org.telegram.ui.DialogsActivity$14
            int r4 = r14.dialogsType
            int r2 = r10.folderId
            boolean r1 = r10.onlySelect
            java.util.ArrayList<java.lang.Long> r0 = r10.selectedDialogs
            int r9 = r10.currentAccount
            r23 = r0
            r0 = r8
            r24 = r1
            r1 = r39
            r25 = r2
            r2 = r39
            r26 = r3
            r3 = r40
            r18 = r5
            r12 = 10
            r5 = r25
            r12 = r6
            r6 = r24
            r24 = r13
            r13 = 8
            r7 = r23
            r13 = r8
            r8 = r9
            r21 = 0
            r9 = r14
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9)
            org.telegram.ui.Adapters.DialogsAdapter unused = r14.dialogsAdapter = r13
            org.telegram.ui.Adapters.DialogsAdapter r0 = r14.dialogsAdapter
            boolean r1 = r10.afterSignup
            r0.setForceShowEmptyCell(r1)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x04aa
            long r0 = r10.openedDialogId
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x04aa
            org.telegram.ui.Adapters.DialogsAdapter r0 = r14.dialogsAdapter
            long r1 = r10.openedDialogId
            r0.setOpenedDialogId(r1)
        L_0x04aa:
            org.telegram.ui.Adapters.DialogsAdapter r0 = r14.dialogsAdapter
            org.telegram.ui.Components.PullForegroundDrawable r1 = r14.pullForegroundDrawable
            r0.setArchivedPullDrawable(r1)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r14.listView
            org.telegram.ui.Adapters.DialogsAdapter r1 = r14.dialogsAdapter
            r0.setAdapter(r1)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r14.listView
            int r1 = r10.folderId
            if (r1 != 0) goto L_0x04cd
            org.telegram.ui.Components.FlickerLoadingView r1 = r14.progressView
            goto L_0x04ce
        L_0x04cd:
            r1 = 0
        L_0x04ce:
            r0.setEmptyView(r1)
            org.telegram.ui.Components.RecyclerAnimationScrollHelper r0 = new org.telegram.ui.Components.RecyclerAnimationScrollHelper
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r1 = r14.listView
            androidx.recyclerview.widget.LinearLayoutManager r2 = r14.layoutManager
            r0.<init>(r1, r2)
            org.telegram.ui.Components.RecyclerAnimationScrollHelper unused = r14.scrollHelper = r0
            if (r26 == 0) goto L_0x04ec
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r10.viewPages
            r0 = r0[r26]
            r1 = 8
            r0.setVisibility(r1)
        L_0x04ec:
            int r3 = r26 + 1
            r6 = r12
            r5 = r18
            r13 = r24
            r4 = 3
            r7 = 8
            r8 = 2
            r9 = 0
            r12 = 1
            r14 = 2131625203(0x7f0e04f3, float:1.8877607E38)
            goto L_0x02fc
        L_0x04fe:
            r12 = r6
            r24 = r13
            r21 = 0
            java.lang.String r3 = r10.searchString
            if (r3 == 0) goto L_0x0509
            r3 = 2
            goto L_0x0510
        L_0x0509:
            boolean r3 = r10.onlySelect
            if (r3 != 0) goto L_0x050f
            r3 = 1
            goto L_0x0510
        L_0x050f:
            r3 = 0
        L_0x0510:
            org.telegram.ui.Components.SearchViewPager r7 = new org.telegram.ui.Components.SearchViewPager
            int r4 = r10.initialDialogsType
            int r5 = r10.folderId
            org.telegram.ui.DialogsActivity$15 r6 = new org.telegram.ui.DialogsActivity$15
            r6.<init>()
            r8 = -1
            r0 = r7
            r9 = -2
            r1 = r40
            r13 = -1082130432(0xffffffffbvar_, float:-1.0)
            r2 = r39
            r0.<init>(r1, r2, r3, r4, r5, r6)
            r10.searchViewPager = r7
            r12.addView(r7)
            org.telegram.ui.Components.SearchViewPager r0 = r10.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r0 = r0.dialogsSearchAdapter
            org.telegram.ui.DialogsActivity$16 r1 = new org.telegram.ui.DialogsActivity$16
            r1.<init>()
            r0.setDelegate(r1)
            org.telegram.ui.Components.SearchViewPager r0 = r10.searchViewPager
            org.telegram.ui.Components.RecyclerListView r0 = r0.searchListView
            org.telegram.ui.-$$Lambda$DialogsActivity$pBCq63prZRS518_dH45NxiBpYLg r1 = new org.telegram.ui.-$$Lambda$DialogsActivity$pBCq63prZRS518_dH45NxiBpYLg
            r1.<init>()
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            org.telegram.ui.Components.SearchViewPager r0 = r10.searchViewPager
            org.telegram.ui.Components.RecyclerListView r0 = r0.searchListView
            org.telegram.ui.DialogsActivity$17 r1 = new org.telegram.ui.DialogsActivity$17
            r1.<init>()
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended) r1)
            org.telegram.ui.Components.SearchViewPager r0 = r10.searchViewPager
            org.telegram.ui.-$$Lambda$DialogsActivity$9BZKks1l13cel3V-cpOskDp_uvY r1 = new org.telegram.ui.-$$Lambda$DialogsActivity$9BZKks1l13cel3V-cpOskDp_uvY
            r1.<init>()
            r0.setFilteredSearchViewDelegate(r1)
            org.telegram.ui.Components.SearchViewPager r0 = r10.searchViewPager
            r1 = 8
            r0.setVisibility(r1)
            org.telegram.ui.Adapters.FiltersView r0 = new org.telegram.ui.Adapters.FiltersView
            android.app.Activity r1 = r39.getParentActivity()
            r0.<init>(r1)
            r10.filtersView = r0
            org.telegram.ui.-$$Lambda$DialogsActivity$P9xYAyGRu_Y4WiebF4gZ35grh_U r1 = new org.telegram.ui.-$$Lambda$DialogsActivity$P9xYAyGRu_Y4WiebF4gZ35grh_U
            r1.<init>()
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            org.telegram.ui.Adapters.FiltersView r0 = r10.filtersView
            r1 = 48
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9, r1)
            r12.addView(r0, r1)
            org.telegram.ui.Adapters.FiltersView r0 = r10.filtersView
            r1 = 8
            r0.setVisibility(r1)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r11)
            r10.floatingButtonContainer = r0
            boolean r1 = r10.onlySelect
            if (r1 == 0) goto L_0x0597
            int r1 = r10.initialDialogsType
            r2 = 10
            if (r1 != r2) goto L_0x059b
        L_0x0597:
            int r1 = r10.folderId
            if (r1 == 0) goto L_0x059e
        L_0x059b:
            r7 = 8
            goto L_0x059f
        L_0x059e:
            r7 = 0
        L_0x059f:
            r0.setVisibility(r7)
            android.widget.FrameLayout r0 = r10.floatingButtonContainer
            int r6 = android.os.Build.VERSION.SDK_INT
            r7 = 21
            if (r6 < r7) goto L_0x05ad
            r3 = 56
            goto L_0x05af
        L_0x05ad:
            r3 = 60
        L_0x05af:
            int r32 = r3 + 20
            if (r6 < r7) goto L_0x05b6
            r3 = 56
            goto L_0x05b8
        L_0x05b6:
            r3 = 60
        L_0x05b8:
            int r3 = r3 + 20
            float r3 = (float) r3
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x05c1
            r5 = 3
            goto L_0x05c2
        L_0x05c1:
            r5 = 5
        L_0x05c2:
            r34 = r5 | 80
            r5 = 1082130432(0x40800000, float:4.0)
            if (r4 == 0) goto L_0x05cb
            r35 = 1082130432(0x40800000, float:4.0)
            goto L_0x05cd
        L_0x05cb:
            r35 = 0
        L_0x05cd:
            r36 = 0
            if (r4 == 0) goto L_0x05d4
            r37 = 0
            goto L_0x05d6
        L_0x05d4:
            r37 = 1082130432(0x40800000, float:4.0)
        L_0x05d6:
            r38 = 0
            r33 = r3
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r32, r33, r34, r35, r36, r37, r38)
            r12.addView(r0, r3)
            android.widget.FrameLayout r0 = r10.floatingButtonContainer
            org.telegram.ui.-$$Lambda$DialogsActivity$IytOkq_hVLb_L5wWv2AeJatOt8Q r3 = new org.telegram.ui.-$$Lambda$DialogsActivity$IytOkq_hVLb_L5wWv2AeJatOt8Q
            r3.<init>()
            r0.setOnClickListener(r3)
            org.telegram.ui.Components.RLottieImageView r0 = new org.telegram.ui.Components.RLottieImageView
            r0.<init>(r11)
            r10.floatingButton = r0
            android.widget.ImageView$ScaleType r3 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r3)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            java.lang.String r3 = "chats_actionBackground"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            java.lang.String r4 = "chats_actionPressedBackground"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r0, r3, r4)
            if (r6 >= r7) goto L_0x063a
            android.content.res.Resources r3 = r40.getResources()
            r4 = 2131165418(0x7var_ea, float:1.7945053E38)
            android.graphics.drawable.Drawable r3 = r3.getDrawable(r4)
            android.graphics.drawable.Drawable r3 = r3.mutate()
            android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
            r14 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r1 = android.graphics.PorterDuff.Mode.MULTIPLY
            r4.<init>(r14, r1)
            r3.setColorFilter(r4)
            org.telegram.ui.Components.CombinedDrawable r1 = new org.telegram.ui.Components.CombinedDrawable
            r4 = 0
            r1.<init>(r3, r0, r4, r4)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r1.setIconSize(r0, r3)
            r0 = r1
        L_0x063a:
            org.telegram.ui.Components.RLottieImageView r1 = r10.floatingButton
            r1.setBackgroundDrawable(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r10.floatingButton
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            java.lang.String r3 = "chats_actionIcon"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            android.graphics.PorterDuff$Mode r4 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r3, r4)
            r0.setColorFilter(r1)
            int r0 = r10.initialDialogsType
            r1 = 10
            if (r0 != r1) goto L_0x066c
            org.telegram.ui.Components.RLottieImageView r0 = r10.floatingButton
            r1 = 2131165415(0x7var_e7, float:1.7945046E38)
            r0.setImageResource(r1)
            android.widget.FrameLayout r0 = r10.floatingButtonContainer
            r1 = 2131625203(0x7f0e04f3, float:1.8877607E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r15, r1)
            r0.setContentDescription(r1)
            goto L_0x0686
        L_0x066c:
            org.telegram.ui.Components.RLottieImageView r0 = r10.floatingButton
            r1 = 2131558520(0x7f0d0078, float:1.8742358E38)
            r3 = 52
            r4 = 52
            r0.setAnimation(r1, r3, r4)
            android.widget.FrameLayout r0 = r10.floatingButtonContainer
            r1 = 2131626211(0x7f0e08e3, float:1.8879652E38)
            java.lang.String r3 = "NewMessageTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setContentDescription(r1)
        L_0x0686:
            if (r6 < r7) goto L_0x06f0
            android.animation.StateListAnimator r0 = new android.animation.StateListAnimator
            r0.<init>()
            r1 = 1
            int[] r3 = new int[r1]
            r1 = 16842919(0x10100a7, float:2.3694026E-38)
            r4 = 0
            r3[r4] = r1
            org.telegram.ui.Components.RLottieImageView r1 = r10.floatingButton
            android.util.Property r14 = android.view.View.TRANSLATION_Z
            r15 = 2
            float[] r2 = new float[r15]
            r16 = 1073741824(0x40000000, float:2.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r13 = (float) r13
            r2[r4] = r13
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r13 = (float) r13
            r16 = 1
            r2[r16] = r13
            android.animation.ObjectAnimator r1 = android.animation.ObjectAnimator.ofFloat(r1, r14, r2)
            r8 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r1 = r1.setDuration(r8)
            r0.addState(r3, r1)
            int[] r1 = new int[r4]
            org.telegram.ui.Components.RLottieImageView r2 = r10.floatingButton
            float[] r3 = new float[r15]
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            r3[r4] = r5
            r4 = 1073741824(0x40000000, float:2.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r5 = 1
            r3[r5] = r4
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r14, r3)
            r3 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r2 = r2.setDuration(r3)
            r0.addState(r1, r2)
            org.telegram.ui.Components.RLottieImageView r1 = r10.floatingButton
            r1.setStateListAnimator(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r10.floatingButton
            org.telegram.ui.DialogsActivity$18 r1 = new org.telegram.ui.DialogsActivity$18
            r1.<init>(r10)
            r0.setOutlineProvider(r1)
            goto L_0x06f1
        L_0x06f0:
            r15 = 2
        L_0x06f1:
            android.widget.FrameLayout r0 = r10.floatingButtonContainer
            org.telegram.ui.Components.RLottieImageView r1 = r10.floatingButton
            if (r6 < r7) goto L_0x06fa
            r25 = 56
            goto L_0x06fc
        L_0x06fa:
            r25 = 60
        L_0x06fc:
            if (r6 < r7) goto L_0x0701
            r2 = 56
            goto L_0x0703
        L_0x0701:
            r2 = 60
        L_0x0703:
            float r2 = (float) r2
            r27 = 51
            r28 = 1092616192(0x41200000, float:10.0)
            r29 = 1086324736(0x40CLASSNAME, float:6.0)
            r30 = 1092616192(0x41200000, float:10.0)
            r31 = 0
            r26 = r2
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r0.addView(r1, r2)
            r0 = 0
            r10.searchTabsView = r0
            boolean r0 = r10.onlySelect
            if (r0 != 0) goto L_0x0767
            int r0 = r10.initialDialogsType
            if (r0 != 0) goto L_0x0767
            org.telegram.ui.Components.FragmentContextView r0 = new org.telegram.ui.Components.FragmentContextView
            r1 = 1
            r0.<init>(r11, r10, r1)
            r10.fragmentLocationContextView = r0
            r25 = -1
            r26 = 1108869120(0x42180000, float:38.0)
            r27 = 51
            r28 = 0
            r29 = -1039138816(0xffffffffCLASSNAME, float:-36.0)
            r30 = 0
            r31 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r0.setLayoutParams(r1)
            org.telegram.ui.Components.FragmentContextView r0 = r10.fragmentLocationContextView
            r12.addView(r0)
            org.telegram.ui.DialogsActivity$19 r0 = new org.telegram.ui.DialogsActivity$19
            r1 = 0
            r0.<init>(r11, r10, r1)
            r10.fragmentContextView = r0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r0.setLayoutParams(r1)
            org.telegram.ui.Components.FragmentContextView r0 = r10.fragmentContextView
            r12.addView(r0)
            org.telegram.ui.Components.FragmentContextView r0 = r10.fragmentContextView
            org.telegram.ui.Components.FragmentContextView r1 = r10.fragmentLocationContextView
            r0.setAdditionalContextView(r1)
            org.telegram.ui.Components.FragmentContextView r0 = r10.fragmentLocationContextView
            org.telegram.ui.Components.FragmentContextView r1 = r10.fragmentContextView
            r0.setAdditionalContextView(r1)
            goto L_0x07ad
        L_0x0767:
            int r0 = r10.initialDialogsType
            r1 = 3
            if (r0 != r1) goto L_0x07ad
            org.telegram.ui.Components.ChatActivityEnterView r0 = r10.commentView
            if (r0 == 0) goto L_0x0773
            r0.onDestroy()
        L_0x0773:
            org.telegram.ui.DialogsActivity$20 r8 = new org.telegram.ui.DialogsActivity$20
            android.app.Activity r2 = r39.getParentActivity()
            r4 = 0
            r5 = 0
            r0 = r8
            r1 = r39
            r3 = r12
            r0.<init>(r2, r3, r4, r5)
            r10.commentView = r8
            r0 = 0
            r8.setAllowStickersAndGifs(r0, r0)
            org.telegram.ui.Components.ChatActivityEnterView r1 = r10.commentView
            r2 = 1
            r1.setForceShowSendButton(r2, r0)
            org.telegram.ui.Components.ChatActivityEnterView r0 = r10.commentView
            r1 = 8
            r0.setVisibility(r1)
            org.telegram.ui.Components.ChatActivityEnterView r0 = r10.commentView
            r1 = 83
            r2 = -2
            r3 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r2, r1)
            r12.addView(r0, r1)
            org.telegram.ui.Components.ChatActivityEnterView r0 = r10.commentView
            org.telegram.ui.DialogsActivity$21 r1 = new org.telegram.ui.DialogsActivity$21
            r1.<init>()
            r0.setDelegate(r1)
            goto L_0x07ae
        L_0x07ad:
            r3 = -1
        L_0x07ae:
            org.telegram.ui.Components.FilterTabsView r0 = r10.filterTabsView
            if (r0 == 0) goto L_0x07bb
            r1 = 1110441984(0x42300000, float:44.0)
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r1)
            r12.addView(r0, r1)
        L_0x07bb:
            boolean r0 = r10.onlySelect
            if (r0 != 0) goto L_0x07d4
            r0 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r0)
            boolean r1 = r10.inPreviewMode
            if (r1 == 0) goto L_0x07cf
            if (r6 < r7) goto L_0x07cf
            int r1 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            r0.topMargin = r1
        L_0x07cf:
            org.telegram.ui.ActionBar.ActionBar r1 = r10.actionBar
            r12.addView(r1, r0)
        L_0x07d4:
            r0 = 0
        L_0x07d5:
            if (r0 >= r15) goto L_0x07fa
            org.telegram.ui.Components.UndoView[] r1 = r10.undoView
            org.telegram.ui.DialogsActivity$22 r2 = new org.telegram.ui.DialogsActivity$22
            r2.<init>(r11)
            r1[r0] = r2
            org.telegram.ui.Components.UndoView[] r1 = r10.undoView
            r1 = r1[r0]
            r2 = -1
            r3 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r4 = 83
            r5 = 1090519040(0x41000000, float:8.0)
            r6 = 0
            r7 = 1090519040(0x41000000, float:8.0)
            r8 = 1090519040(0x41000000, float:8.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3, r4, r5, r6, r7, r8)
            r12.addView(r1, r2)
            int r0 = r0 + 1
            goto L_0x07d5
        L_0x07fa:
            int r0 = r10.folderId
            if (r0 == 0) goto L_0x0849
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r10.viewPages
            r1 = 0
            r0 = r0[r1]
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r0.listView
            java.lang.String r1 = "actionBarDefaultArchived"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setGlowColor(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            java.lang.String r1 = "actionBarDefaultArchivedTitle"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTitleColor(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            java.lang.String r1 = "actionBarDefaultArchivedIcon"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r2 = 0
            r0.setItemsColor(r1, r2)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            java.lang.String r1 = "actionBarDefaultArchivedSelector"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setItemsBackgroundColor(r1, r2)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            java.lang.String r1 = "actionBarDefaultArchivedSearch"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setSearchTextColor(r1, r2)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            java.lang.String r1 = "actionBarDefaultSearchArchivedPlaceholder"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r2 = 1
            r0.setSearchTextColor(r1, r2)
        L_0x0849:
            boolean r0 = r10.onlySelect
            if (r0 != 0) goto L_0x0869
            int r0 = r10.initialDialogsType
            if (r0 != 0) goto L_0x0869
            org.telegram.ui.DialogsActivity$23 r0 = new org.telegram.ui.DialogsActivity$23
            r0.<init>(r11)
            r10.blurredView = r0
            r1 = 8
            r0.setVisibility(r1)
            android.view.View r0 = r10.blurredView
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            r2 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r1)
            r12.addView(r0, r1)
        L_0x0869:
            android.graphics.Paint r0 = r10.actionBarDefaultPaint
            int r1 = r10.folderId
            if (r1 != 0) goto L_0x0872
            r1 = r24
            goto L_0x0874
        L_0x0872:
            java.lang.String r1 = "actionBarDefaultArchived"
        L_0x0874:
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setColor(r1)
            boolean r0 = r10.inPreviewMode
            if (r0 == 0) goto L_0x08f6
            org.telegram.messenger.UserConfig r0 = r39.getUserConfig()
            org.telegram.tgnet.TLRPC$User r0 = r0.getCurrentUser()
            org.telegram.ui.Components.ChatAvatarContainer r1 = new org.telegram.ui.Components.ChatAvatarContainer
            org.telegram.ui.ActionBar.ActionBar r2 = r10.actionBar
            android.content.Context r2 = r2.getContext()
            r3 = 0
            r4 = 0
            r1.<init>(r2, r3, r4)
            r10.avatarContainer = r1
            java.lang.String r2 = org.telegram.messenger.UserObject.getUserName(r0)
            r1.setTitle(r2)
            org.telegram.ui.Components.ChatAvatarContainer r1 = r10.avatarContainer
            int r2 = r10.currentAccount
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatUserStatus(r2, r0)
            r1.setSubtitle(r2)
            org.telegram.ui.Components.ChatAvatarContainer r1 = r10.avatarContainer
            r2 = 1
            r1.setUserAvatar(r0, r2)
            org.telegram.ui.Components.ChatAvatarContainer r0 = r10.avatarContainer
            r0.setOccupyStatusBar(r4)
            org.telegram.ui.Components.ChatAvatarContainer r0 = r10.avatarContainer
            r1 = 1092616192(0x41200000, float:10.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setLeftPadding(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            org.telegram.ui.Components.ChatAvatarContainer r1 = r10.avatarContainer
            r2 = -2
            r3 = -1082130432(0xffffffffbvar_, float:-1.0)
            r4 = 51
            r5 = 0
            r6 = 0
            r7 = 1109393408(0x42200000, float:40.0)
            r8 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3, r4, r5, r6, r7, r8)
            r3 = 0
            r0.addView(r1, r3, r2)
            org.telegram.ui.Components.RLottieImageView r0 = r10.floatingButton
            r1 = 4
            r0.setVisibility(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r0.setOccupyStatusBar(r3)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r24)
            r0.setBackgroundColor(r1)
            org.telegram.ui.Components.FragmentContextView r0 = r10.fragmentContextView
            if (r0 == 0) goto L_0x08ef
            r12.removeView(r0)
        L_0x08ef:
            org.telegram.ui.Components.FragmentContextView r0 = r10.fragmentLocationContextView
            if (r0 == 0) goto L_0x08f6
            r12.removeView(r0)
        L_0x08f6:
            r0 = 0
            r10.searchIsShowed = r0
            r10.updateFilterTabs(r0, r0)
            java.lang.String r1 = r10.searchString
            if (r1 == 0) goto L_0x090c
            r1 = 1
            r10.showSearch(r1, r0)
            org.telegram.ui.ActionBar.ActionBar r1 = r10.actionBar
            java.lang.String r2 = r10.searchString
            r1.openSearchField(r2, r0)
            goto L_0x0931
        L_0x090c:
            r1 = 1
            java.lang.String r2 = r10.initialSearchString
            if (r2 == 0) goto L_0x092e
            r10.showSearch(r1, r0)
            org.telegram.ui.ActionBar.ActionBar r1 = r10.actionBar
            java.lang.String r2 = r10.initialSearchString
            r1.openSearchField(r2, r0)
            r1 = 0
            r10.initialSearchString = r1
            org.telegram.ui.Components.FilterTabsView r1 = r10.filterTabsView
            if (r1 == 0) goto L_0x0931
            r2 = 1110441984(0x42300000, float:44.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = -r2
            float r2 = (float) r2
            r1.setTranslationY(r2)
            goto L_0x0931
        L_0x092e:
            r10.showSearch(r0, r0)
        L_0x0931:
            int r0 = r10.folderId
            if (r0 == 0) goto L_0x0959
            org.telegram.ui.Adapters.FiltersView$MediaFilterData r0 = new org.telegram.ui.Adapters.FiltersView$MediaFilterData
            r2 = 2131165341(0x7var_d, float:1.7944896E38)
            r3 = 2131165341(0x7var_d, float:1.7944896E38)
            r1 = 2131624301(0x7f0e016d, float:1.8875778E38)
            java.lang.String r4 = "ArchiveSearchFilter"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r5 = 0
            r6 = 7
            r1 = r0
            r1.<init>(r2, r3, r4, r5, r6)
            r1 = 0
            r0.removable = r1
            org.telegram.ui.ActionBar.ActionBar r1 = r10.actionBar
            r1.setSearchFilter(r0)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r10.searchItem
            r0.collapseSearchFilters()
        L_0x0959:
            android.view.View r0 = r10.fragmentView
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
        if (this.initialDialogsType != 10) {
            hideFloatingButton(false);
        }
        scrollToTop();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$4 */
    public /* synthetic */ void lambda$createView$4$DialogsActivity(ViewPage viewPage, View view, int i) {
        int i2 = this.initialDialogsType;
        if (i2 == 10) {
            onItemLongClick(view, i, 0.0f, 0.0f, viewPage.dialogsType, viewPage.dialogsAdapter);
        } else if ((i2 == 11 || i2 == 13) && i == 1) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("forImport", true);
            ArrayList arrayList = new ArrayList();
            arrayList.add(Integer.valueOf(getUserConfig().getClientUserId()));
            bundle.putIntegerArrayList("result", arrayList);
            bundle.putInt("chatType", 4);
            String string = this.arguments.getString("importTitle");
            if (string != null) {
                bundle.putString("title", string);
            }
            GroupCreateFinalActivity groupCreateFinalActivity = new GroupCreateFinalActivity(bundle);
            groupCreateFinalActivity.setDelegate(new GroupCreateFinalActivity.GroupCreateFinalActivityDelegate() {
                public void didFailChatCreation() {
                }

                public void didStartChatCreation() {
                }

                public void didFinishChatCreation(GroupCreateFinalActivity groupCreateFinalActivity, int i) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(Long.valueOf((long) (-i)));
                    DialogsActivityDelegate access$22800 = DialogsActivity.this.delegate;
                    DialogsActivity.this.removeSelfFromStack();
                    access$22800.didSelectDialogs(DialogsActivity.this, arrayList, (CharSequence) null, true);
                }
            });
            presentFragment(groupCreateFinalActivity);
        } else {
            onItemClick(view, i, viewPage.dialogsAdapter);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$5 */
    public /* synthetic */ void lambda$createView$5$DialogsActivity(View view, int i) {
        if (this.initialDialogsType == 10) {
            onItemLongClick(view, i, 0.0f, 0.0f, -1, this.searchViewPager.dialogsSearchAdapter);
            return;
        }
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
        if (this.initialDialogsType != 10) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("destroyAfterSelect", true);
            presentFragment(new ContactsActivity(bundle));
        } else if (this.delegate != null && !this.selectedDialogs.isEmpty()) {
            this.delegate.didSelectDialogs(this, this.selectedDialogs, (CharSequence) null, false);
        }
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
            fragmentContextView3.setTranslationY(topPadding2 + ((1.0f - f2) * measuredHeight) + (f2 * measuredHeight2) + this.tabsYOffset);
        }
        if (this.fragmentLocationContextView != null) {
            FragmentContextView fragmentContextView4 = this.fragmentContextView;
            if (fragmentContextView4 != null && fragmentContextView4.getVisibility() == 0) {
                f = 0.0f + ((float) AndroidUtilities.dp((float) this.fragmentContextView.getStyleHeight())) + this.fragmentContextView.getTopPadding();
            }
            FragmentContextView fragmentContextView5 = this.fragmentLocationContextView;
            float topPadding3 = f + fragmentContextView5.getTopPadding() + this.actionBar.getTranslationY();
            float f3 = this.searchAnimationProgress;
            fragmentContextView5.setTranslationY(topPadding3 + (measuredHeight * (1.0f - f3)) + (measuredHeight2 * f3) + this.tabsYOffset);
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
            org.telegram.ui.Components.ViewPagerFixed$TabsView r11 = r9.searchTabsView
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
                        DialogsActivity.this.updatePasscodeButton(true);
                    } else if (i2 == 2) {
                        DialogsActivity.this.presentFragment(new ProxyListActivity());
                    } else if (i2 < 10 || i2 >= 13) {
                        if (i2 == 109) {
                            DialogsActivity dialogsActivity = DialogsActivity.this;
                            FiltersListBottomSheet filtersListBottomSheet = new FiltersListBottomSheet(dialogsActivity, dialogsActivity.selectedDialogs);
                            filtersListBottomSheet.setDelegate(new FiltersListBottomSheet.FiltersListBottomSheetDelegate() {
                                public final void didSelectFilter(MessagesController.DialogFilter dialogFilter) {
                                    DialogsActivity.AnonymousClass24.this.lambda$onItemClick$0$DialogsActivity$24(dialogFilter);
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
                            DialogsActivity dialogsActivity4 = DialogsActivity.this;
                            dialogsActivity4.perfromSelectedDialogsAction(dialogsActivity4.selectedDialogs, i2, true);
                        }
                    } else if (DialogsActivity.this.getParentActivity() != null) {
                        DialogsActivityDelegate access$22800 = DialogsActivity.this.delegate;
                        LaunchActivity launchActivity = (LaunchActivity) DialogsActivity.this.getParentActivity();
                        launchActivity.switchToAccount(i2 - 10, true);
                        DialogsActivity dialogsActivity5 = new DialogsActivity(DialogsActivity.this.arguments);
                        dialogsActivity5.setDelegate(access$22800);
                        launchActivity.presentFragment(dialogsActivity5, false, true);
                    }
                }

                /* access modifiers changed from: private */
                /* renamed from: lambda$onItemClick$0 */
                public /* synthetic */ void lambda$onItemClick$0$DialogsActivity$24(MessagesController.DialogFilter dialogFilter) {
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

    private void updateFilterTabs(boolean z, boolean z2) {
        int findFirstVisibleItemPosition;
        boolean z3;
        if (this.filterTabsView != null && !this.inPreviewMode && !this.searchIsShowed) {
            ActionBarPopupWindow actionBarPopupWindow = this.scrimPopupWindow;
            if (actionBarPopupWindow != null) {
                actionBarPopupWindow.dismiss();
                this.scrimPopupWindow = null;
            }
            ArrayList<MessagesController.DialogFilter> arrayList = getMessagesController().dialogFilters;
            MessagesController.getMainSettings(this.currentAccount);
            boolean z4 = true;
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
                    this.canShowFilterTabsView = false;
                    updateFilterTabsVisibility(z2);
                    int i = 0;
                    while (true) {
                        ViewPage[] viewPageArr2 = this.viewPages;
                        if (i >= viewPageArr2.length) {
                            break;
                        }
                        if (viewPageArr2[i].dialogsType == 0 && this.viewPages[i].archivePullViewState == 2 && hasHiddenArchive() && ((findFirstVisibleItemPosition = this.viewPages[i].layoutManager.findFirstVisibleItemPosition()) == 0 || findFirstVisibleItemPosition == 1)) {
                            this.viewPages[i].layoutManager.scrollToPositionWithOffset(1, 0);
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
                boolean z5 = this.filterTabsView.getVisibility() != 0 ? false : z2;
                this.canShowFilterTabsView = true;
                updateFilterTabsVisibility(z2);
                int currentTabId = this.filterTabsView.getCurrentTabId();
                if (currentTabId != Integer.MAX_VALUE && currentTabId >= arrayList.size()) {
                    this.filterTabsView.resetTabId();
                }
                this.filterTabsView.removeTabs();
                this.filterTabsView.addTab(Integer.MAX_VALUE, 0, LocaleController.getString("FilterAllChats", NUM));
                int size = arrayList.size();
                for (int i2 = 0; i2 < size; i2++) {
                    this.filterTabsView.addTab(i2, arrayList.get(i2).localId, arrayList.get(i2).name);
                }
                int currentTabId2 = this.filterTabsView.getCurrentTabId();
                if (currentTabId2 < 0 || this.viewPages[0].selectedType == currentTabId2) {
                    z3 = false;
                } else {
                    int unused5 = this.viewPages[0].selectedType = currentTabId2;
                    z3 = true;
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
                this.filterTabsView.finishAddingTabs(z5);
                if (z3) {
                    switchToCurrentSelectedMode(false);
                }
                ActionBarLayout actionBarLayout2 = this.parentLayout;
                if (actionBarLayout2 != null) {
                    DrawerLayoutContainer drawerLayoutContainer = actionBarLayout2.getDrawerLayoutContainer();
                    if (currentTabId2 != this.filterTabsView.getFirstTabId()) {
                        z4 = false;
                    }
                    drawerLayoutContainer.setAllowOpenDrawerBySwipe(z4);
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

    /* JADX WARNING: Removed duplicated region for block: B:111:0x0208  */
    /* JADX WARNING: Removed duplicated region for block: B:117:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00aa  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0130  */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x01a5  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onResume() {
        /*
            r10 = this;
            super.onResume()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r10.parentLayout
            boolean r0 = r0.isInPreviewMode()
            r1 = 0
            if (r0 != 0) goto L_0x0022
            android.view.View r0 = r10.blurredView
            if (r0 == 0) goto L_0x0022
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x0022
            android.view.View r0 = r10.blurredView
            r2 = 8
            r0.setVisibility(r2)
            android.view.View r0 = r10.blurredView
            r0.setBackground(r1)
        L_0x0022:
            org.telegram.ui.Components.FilterTabsView r0 = r10.filterTabsView
            r2 = 1
            r3 = 0
            if (r0 == 0) goto L_0x004f
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x004f
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r10.parentLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r0.getDrawerLayoutContainer()
            org.telegram.ui.DialogsActivity$ViewPage[] r4 = r10.viewPages
            r4 = r4[r3]
            int r4 = r4.selectedType
            org.telegram.ui.Components.FilterTabsView r5 = r10.filterTabsView
            int r5 = r5.getFirstTabId()
            if (r4 == r5) goto L_0x004b
            boolean r4 = r10.searchIsShowed
            if (r4 == 0) goto L_0x0049
            goto L_0x004b
        L_0x0049:
            r4 = 0
            goto L_0x004c
        L_0x004b:
            r4 = 1
        L_0x004c:
            r0.setAllowOpenDrawerBySwipe(r4)
        L_0x004f:
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r10.viewPages
            if (r0 == 0) goto L_0x0065
            r0 = 0
        L_0x0054:
            org.telegram.ui.DialogsActivity$ViewPage[] r4 = r10.viewPages
            int r5 = r4.length
            if (r0 >= r5) goto L_0x0065
            r4 = r4[r0]
            org.telegram.ui.Adapters.DialogsAdapter r4 = r4.dialogsAdapter
            r4.notifyDataSetChanged()
            int r0 = r0 + 1
            goto L_0x0054
        L_0x0065:
            org.telegram.ui.Components.ChatActivityEnterView r0 = r10.commentView
            if (r0 == 0) goto L_0x006c
            r0.onResume()
        L_0x006c:
            boolean r0 = r10.onlySelect
            if (r0 != 0) goto L_0x007c
            int r0 = r10.folderId
            if (r0 != 0) goto L_0x007c
            org.telegram.messenger.MediaDataController r0 = r10.getMediaDataController()
            r4 = 4
            r0.checkStickers(r4)
        L_0x007c:
            org.telegram.ui.Components.SearchViewPager r0 = r10.searchViewPager
            if (r0 == 0) goto L_0x0083
            r0.onResume()
        L_0x0083:
            boolean r0 = r10.afterSignup
            if (r0 != 0) goto L_0x0092
            org.telegram.messenger.UserConfig r0 = r10.getUserConfig()
            org.telegram.tgnet.TLRPC$TL_help_termsOfService r0 = r0.unacceptedTermsOfService
            if (r0 != 0) goto L_0x0090
            goto L_0x0094
        L_0x0090:
            r0 = 0
            goto L_0x0095
        L_0x0092:
            r10.afterSignup = r3
        L_0x0094:
            r0 = 1
        L_0x0095:
            r4 = 2131624281(0x7f0e0159, float:1.8875737E38)
            java.lang.String r5 = "AppName"
            if (r0 == 0) goto L_0x0130
            boolean r0 = r10.checkPermission
            if (r0 == 0) goto L_0x0130
            boolean r0 = r10.onlySelect
            if (r0 != 0) goto L_0x0130
            int r0 = android.os.Build.VERSION.SDK_INT
            r6 = 23
            if (r0 < r6) goto L_0x0130
            android.app.Activity r0 = r10.getParentActivity()
            if (r0 == 0) goto L_0x019e
            r10.checkPermission = r3
            java.lang.String r6 = "android.permission.READ_CONTACTS"
            int r7 = r0.checkSelfPermission(r6)
            if (r7 == 0) goto L_0x00bc
            r7 = 1
            goto L_0x00bd
        L_0x00bc:
            r7 = 0
        L_0x00bd:
            java.lang.String r8 = "android.permission.WRITE_EXTERNAL_STORAGE"
            int r9 = r0.checkSelfPermission(r8)
            if (r9 == 0) goto L_0x00c7
            r9 = 1
            goto L_0x00c8
        L_0x00c7:
            r9 = 0
        L_0x00c8:
            if (r7 != 0) goto L_0x00cc
            if (r9 == 0) goto L_0x019e
        L_0x00cc:
            r10.askingForPermissions = r2
            if (r7 == 0) goto L_0x00f6
            boolean r7 = r10.askAboutContacts
            if (r7 == 0) goto L_0x00f6
            org.telegram.messenger.UserConfig r7 = r10.getUserConfig()
            boolean r7 = r7.syncContacts
            if (r7 == 0) goto L_0x00f6
            boolean r6 = r0.shouldShowRequestPermissionRationale(r6)
            if (r6 == 0) goto L_0x00f6
            org.telegram.ui.-$$Lambda$DialogsActivity$NDjkpRJiaIxLmVxhQl2rcKvq5v4 r1 = new org.telegram.ui.-$$Lambda$DialogsActivity$NDjkpRJiaIxLmVxhQl2rcKvq5v4
            r1.<init>()
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = org.telegram.ui.Components.AlertsCreator.createContactsPermissionDialog(r0, r1)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r10.permissionDialog = r0
            r10.showDialog(r0)
            goto L_0x019e
        L_0x00f6:
            if (r9 == 0) goto L_0x012c
            boolean r6 = r0.shouldShowRequestPermissionRationale(r8)
            if (r6 == 0) goto L_0x012c
            org.telegram.ui.ActionBar.AlertDialog$Builder r6 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r6.<init>((android.content.Context) r0)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r6.setTitle(r0)
            r0 = 2131626860(0x7f0e0b6c, float:1.8880968E38)
            java.lang.String r4 = "PermissionStorage"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            r6.setMessage(r0)
            r0 = 2131626483(0x7f0e09f3, float:1.8880203E38)
            java.lang.String r4 = "OK"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            r6.setPositiveButton(r0, r1)
            org.telegram.ui.ActionBar.AlertDialog r0 = r6.create()
            r10.permissionDialog = r0
            r10.showDialog(r0)
            goto L_0x019e
        L_0x012c:
            r10.askForPermissons(r2)
            goto L_0x019e
        L_0x0130:
            boolean r0 = r10.onlySelect
            if (r0 != 0) goto L_0x019e
            boolean r0 = org.telegram.messenger.XiaomiUtilities.isMIUI()
            if (r0 == 0) goto L_0x019e
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 19
            if (r0 < r1) goto L_0x019e
            r0 = 10020(0x2724, float:1.4041E-41)
            boolean r0 = org.telegram.messenger.XiaomiUtilities.isCustomPermissionGranted(r0)
            if (r0 != 0) goto L_0x019e
            android.app.Activity r0 = r10.getParentActivity()
            if (r0 != 0) goto L_0x014f
            return
        L_0x014f:
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalNotificationsSettings()
            java.lang.String r1 = "askedAboutMiuiLockscreen"
            boolean r0 = r0.getBoolean(r1, r3)
            if (r0 == 0) goto L_0x015c
            return
        L_0x015c:
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r1 = r10.getParentActivity()
            r0.<init>((android.content.Context) r1)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setTitle(r1)
            r1 = 2131626861(0x7f0e0b6d, float:1.888097E38)
            java.lang.String r4 = "PermissionXiaomiLockscreen"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r0.setMessage(r1)
            r1 = 2131626859(0x7f0e0b6b, float:1.8880966E38)
            java.lang.String r4 = "PermissionOpenSettings"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            org.telegram.ui.-$$Lambda$DialogsActivity$P7XKAmT26raLlvar_AlrL62XLpQ8 r4 = new org.telegram.ui.-$$Lambda$DialogsActivity$P7XKAmT26raLlvar_AlrL62XLpQ8
            r4.<init>()
            r0.setPositiveButton(r1, r4)
            r1 = 2131624981(0x7f0e0415, float:1.8877157E38)
            java.lang.String r4 = "ContactsPermissionAlertNotNow"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            org.telegram.ui.-$$Lambda$DialogsActivity$tmX8hHmbkMGLu5yCBS8o2Ti7cuA r4 = org.telegram.ui.$$Lambda$DialogsActivity$tmX8hHmbkMGLu5yCBS8o2Ti7cuA.INSTANCE
            r0.setNegativeButton(r1, r4)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r10.showDialog(r0)
        L_0x019e:
            r10.showFiltersHint()
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r10.viewPages
            if (r0 == 0) goto L_0x01f9
            r0 = 0
        L_0x01a6:
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r10.viewPages
            int r4 = r1.length
            if (r0 >= r4) goto L_0x01f9
            r1 = r1[r0]
            int r1 = r1.dialogsType
            if (r1 != 0) goto L_0x01dd
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r10.viewPages
            r1 = r1[r0]
            int r1 = r1.archivePullViewState
            r4 = 2
            if (r1 != r4) goto L_0x01dd
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r10.viewPages
            r1 = r1[r0]
            androidx.recyclerview.widget.LinearLayoutManager r1 = r1.layoutManager
            int r1 = r1.findFirstVisibleItemPosition()
            if (r1 != 0) goto L_0x01dd
            boolean r1 = r10.hasHiddenArchive()
            if (r1 == 0) goto L_0x01dd
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r10.viewPages
            r1 = r1[r0]
            androidx.recyclerview.widget.LinearLayoutManager r1 = r1.layoutManager
            r1.scrollToPositionWithOffset(r2, r3)
        L_0x01dd:
            if (r0 != 0) goto L_0x01eb
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r10.viewPages
            r1 = r1[r0]
            org.telegram.ui.Adapters.DialogsAdapter r1 = r1.dialogsAdapter
            r1.resume()
            goto L_0x01f6
        L_0x01eb:
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r10.viewPages
            r1 = r1[r0]
            org.telegram.ui.Adapters.DialogsAdapter r1 = r1.dialogsAdapter
            r1.pause()
        L_0x01f6:
            int r0 = r0 + 1
            goto L_0x01a6
        L_0x01f9:
            r10.showNextSupportedSuggestion()
            org.telegram.ui.DialogsActivity$25 r0 = new org.telegram.ui.DialogsActivity$25
            r0.<init>()
            org.telegram.ui.Components.Bulletin.addDelegate((org.telegram.ui.ActionBar.BaseFragment) r10, (org.telegram.ui.Components.Bulletin.Delegate) r0)
            boolean r0 = r10.searchIsShowed
            if (r0 == 0) goto L_0x0211
            android.app.Activity r0 = r10.getParentActivity()
            int r1 = r10.classGuid
            org.telegram.messenger.AndroidUtilities.requestAdjustResize(r0, r1)
        L_0x0211:
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

    public boolean presentFragment(BaseFragment baseFragment) {
        boolean presentFragment = super.presentFragment(baseFragment);
        if (presentFragment && this.viewPages != null) {
            int i = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (i >= viewPageArr.length) {
                    break;
                }
                viewPageArr[i].dialogsAdapter.pause();
                i++;
            }
        }
        return presentFragment;
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
        int i = 0;
        if (undoViewArr[0] != null) {
            undoViewArr[0].hide(true, 0);
        }
        Bulletin.removeDelegate((BaseFragment) this);
        if (this.viewPages != null) {
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (i < viewPageArr.length) {
                    viewPageArr[i].dialogsAdapter.pause();
                    i++;
                } else {
                    return;
                }
            }
        }
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
        if (filterTabsView2 != null && filterTabsView2.getVisibility() == 0 && this.filterTabsViewIsVisible) {
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
            updateFilterTabs(false, false);
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v9, resolved type: boolean} */
    /* JADX WARNING: type inference failed for: r3v0 */
    /* JADX WARNING: type inference failed for: r3v3 */
    /* JADX WARNING: type inference failed for: r3v10 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showSearch(boolean r17, boolean r18) {
        /*
            r16 = this;
            r0 = r16
            r1 = r17
            int r2 = r0.initialDialogsType
            r3 = 0
            if (r2 == 0) goto L_0x000e
            r4 = 3
            if (r2 == r4) goto L_0x000e
            r2 = 0
            goto L_0x0010
        L_0x000e:
            r2 = r18
        L_0x0010:
            android.animation.AnimatorSet r4 = r0.searchAnimator
            r5 = 0
            if (r4 == 0) goto L_0x001a
            r4.cancel()
            r0.searchAnimator = r5
        L_0x001a:
            android.animation.Animator r4 = r0.tabsAlphaAnimator
            if (r4 == 0) goto L_0x0023
            r4.cancel()
            r0.tabsAlphaAnimator = r5
        L_0x0023:
            r0.searchIsShowed = r1
            r4 = 1110441984(0x42300000, float:44.0)
            r6 = -1
            r7 = 1
            if (r1 == 0) goto L_0x00ee
            boolean r8 = r0.searchFiltersWasShowed
            if (r8 == 0) goto L_0x0031
            r8 = 0
            goto L_0x0035
        L_0x0031:
            boolean r8 = r16.onlyDialogsAdapter()
        L_0x0035:
            org.telegram.ui.Components.SearchViewPager r9 = r0.searchViewPager
            r9.showOnlyDialogsAdapter(r8)
            r9 = r8 ^ 1
            r0.whiteActionBar = r9
            if (r9 == 0) goto L_0x0042
            r0.searchFiltersWasShowed = r7
        L_0x0042:
            android.view.View r9 = r0.fragmentView
            org.telegram.ui.DialogsActivity$ContentView r9 = (org.telegram.ui.DialogsActivity.ContentView) r9
            org.telegram.ui.Components.ViewPagerFixed$TabsView r10 = r0.searchTabsView
            if (r10 != 0) goto L_0x0082
            if (r8 != 0) goto L_0x0082
            org.telegram.ui.Components.SearchViewPager r8 = r0.searchViewPager
            org.telegram.ui.Components.ViewPagerFixed$TabsView r8 = r8.createTabsView()
            r0.searchTabsView = r8
            org.telegram.ui.Adapters.FiltersView r8 = r0.filtersView
            if (r8 == 0) goto L_0x006b
            r8 = 0
        L_0x0059:
            int r10 = r9.getChildCount()
            if (r8 >= r10) goto L_0x006b
            android.view.View r10 = r9.getChildAt(r8)
            org.telegram.ui.Adapters.FiltersView r11 = r0.filtersView
            if (r10 != r11) goto L_0x0068
            goto L_0x006c
        L_0x0068:
            int r8 = r8 + 1
            goto L_0x0059
        L_0x006b:
            r8 = -1
        L_0x006c:
            if (r8 <= 0) goto L_0x0078
            org.telegram.ui.Components.ViewPagerFixed$TabsView r10 = r0.searchTabsView
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r4)
            r9.addView(r10, r8, r11)
            goto L_0x0097
        L_0x0078:
            org.telegram.ui.Components.ViewPagerFixed$TabsView r8 = r0.searchTabsView
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r4)
            r9.addView(r8, r10)
            goto L_0x0097
        L_0x0082:
            if (r10 == 0) goto L_0x0097
            if (r8 == 0) goto L_0x0097
            android.view.ViewParent r8 = r10.getParent()
            boolean r9 = r8 instanceof android.view.ViewGroup
            if (r9 == 0) goto L_0x0095
            android.view.ViewGroup r8 = (android.view.ViewGroup) r8
            org.telegram.ui.Components.ViewPagerFixed$TabsView r9 = r0.searchTabsView
            r8.removeView(r9)
        L_0x0095:
            r0.searchTabsView = r5
        L_0x0097:
            org.telegram.ui.ActionBar.ActionBarMenuItem r8 = r0.searchItem
            org.telegram.ui.Components.EditTextBoldCursor r8 = r8.getSearchField()
            boolean r9 = r0.whiteActionBar
            if (r9 == 0) goto L_0x00be
            java.lang.String r9 = "windowBackgroundWhiteBlackText"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r8.setTextColor(r9)
            java.lang.String r9 = "player_time"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r8.setHintTextColor(r9)
            java.lang.String r9 = "chat_messagePanelCursor"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r8.setCursorColor(r9)
            goto L_0x00d7
        L_0x00be:
            java.lang.String r9 = "actionBarDefaultSearch"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r8.setCursorColor(r10)
            java.lang.String r10 = "actionBarDefaultSearchPlaceholder"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r8.setHintTextColor(r10)
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r8.setTextColor(r9)
        L_0x00d7:
            org.telegram.ui.Components.SearchViewPager r8 = r0.searchViewPager
            android.view.View r9 = r0.fragmentView
            org.telegram.ui.DialogsActivity$ContentView r9 = (org.telegram.ui.DialogsActivity.ContentView) r9
            int r9 = r9.getKeyboardHeight()
            r8.setKeyboardHeight(r9)
            org.telegram.ui.ActionBar.ActionBarLayout r8 = r0.parentLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r8 = r8.getDrawerLayoutContainer()
            r8.setAllowOpenDrawerBySwipe(r7)
            goto L_0x010e
        L_0x00ee:
            org.telegram.ui.Components.FilterTabsView r8 = r0.filterTabsView
            if (r8 == 0) goto L_0x010e
            org.telegram.ui.ActionBar.ActionBarLayout r8 = r0.parentLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r8 = r8.getDrawerLayoutContainer()
            org.telegram.ui.DialogsActivity$ViewPage[] r9 = r0.viewPages
            r9 = r9[r3]
            int r9 = r9.selectedType
            org.telegram.ui.Components.FilterTabsView r10 = r0.filterTabsView
            int r10 = r10.getFirstTabId()
            if (r9 != r10) goto L_0x010a
            r9 = 1
            goto L_0x010b
        L_0x010a:
            r9 = 0
        L_0x010b:
            r8.setAllowOpenDrawerBySwipe(r9)
        L_0x010e:
            if (r2 == 0) goto L_0x0124
            org.telegram.ui.Components.SearchViewPager r8 = r0.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r8 = r8.dialogsSearchAdapter
            boolean r8 = r8.hasRecentSearch()
            if (r8 == 0) goto L_0x0124
            android.app.Activity r8 = r16.getParentActivity()
            int r9 = r0.classGuid
            org.telegram.messenger.AndroidUtilities.setAdjustResizeToNothing(r8, r9)
            goto L_0x012d
        L_0x0124:
            android.app.Activity r8 = r16.getParentActivity()
            int r9 = r0.classGuid
            org.telegram.messenger.AndroidUtilities.requestAdjustResize(r8, r9)
        L_0x012d:
            if (r1 != 0) goto L_0x013a
            org.telegram.ui.Components.FilterTabsView r8 = r0.filterTabsView
            if (r8 == 0) goto L_0x013a
            boolean r9 = r0.canShowFilterTabsView
            if (r9 == 0) goto L_0x013a
            r8.setVisibility(r3)
        L_0x013a:
            r8 = 1063675494(0x3var_, float:0.9)
            r9 = 0
            r10 = 1065353216(0x3var_, float:1.0)
            if (r2 == 0) goto L_0x0336
            if (r1 == 0) goto L_0x015e
            org.telegram.ui.Components.SearchViewPager r2 = r0.searchViewPager
            r2.setVisibility(r3)
            org.telegram.ui.Components.SearchViewPager r2 = r0.searchViewPager
            r2.reset()
            r0.updateFiltersView(r7, r5, r5, r3)
            org.telegram.ui.Components.ViewPagerFixed$TabsView r2 = r0.searchTabsView
            if (r2 == 0) goto L_0x0170
            r2.hide(r3, r3)
            org.telegram.ui.Components.ViewPagerFixed$TabsView r2 = r0.searchTabsView
            r2.setVisibility(r3)
            goto L_0x0170
        L_0x015e:
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r0.viewPages
            r2 = r2[r3]
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r2 = r2.listView
            r2.setVisibility(r3)
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r0.viewPages
            r2 = r2[r3]
            r2.setVisibility(r3)
        L_0x0170:
            r0.setDialogsListFrozen(r7)
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r0.viewPages
            r2 = r2[r3]
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r2 = r2.listView
            r2.setVerticalScrollBarEnabled(r3)
            org.telegram.ui.Components.SearchViewPager r2 = r0.searchViewPager
            java.lang.String r4 = "windowBackgroundWhite"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r2.setBackgroundColor(r11)
            android.animation.AnimatorSet r2 = new android.animation.AnimatorSet
            r2.<init>()
            r0.searchAnimator = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            org.telegram.ui.DialogsActivity$ViewPage[] r11 = r0.viewPages
            r11 = r11[r3]
            android.util.Property r12 = android.view.View.ALPHA
            float[] r13 = new float[r7]
            if (r1 == 0) goto L_0x01a2
            r14 = 0
            goto L_0x01a4
        L_0x01a2:
            r14 = 1065353216(0x3var_, float:1.0)
        L_0x01a4:
            r13[r3] = r14
            android.animation.ObjectAnimator r11 = android.animation.ObjectAnimator.ofFloat(r11, r12, r13)
            r2.add(r11)
            org.telegram.ui.DialogsActivity$ViewPage[] r11 = r0.viewPages
            r11 = r11[r3]
            android.util.Property r12 = android.view.View.SCALE_X
            float[] r13 = new float[r7]
            if (r1 == 0) goto L_0x01bb
            r14 = 1063675494(0x3var_, float:0.9)
            goto L_0x01bd
        L_0x01bb:
            r14 = 1065353216(0x3var_, float:1.0)
        L_0x01bd:
            r13[r3] = r14
            android.animation.ObjectAnimator r11 = android.animation.ObjectAnimator.ofFloat(r11, r12, r13)
            r2.add(r11)
            org.telegram.ui.DialogsActivity$ViewPage[] r11 = r0.viewPages
            r11 = r11[r3]
            android.util.Property r12 = android.view.View.SCALE_Y
            float[] r13 = new float[r7]
            if (r1 == 0) goto L_0x01d1
            goto L_0x01d3
        L_0x01d1:
            r8 = 1065353216(0x3var_, float:1.0)
        L_0x01d3:
            r13[r3] = r8
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r11, r12, r13)
            r2.add(r8)
            org.telegram.ui.Components.SearchViewPager r8 = r0.searchViewPager
            android.util.Property r11 = android.view.View.ALPHA
            float[] r12 = new float[r7]
            if (r1 == 0) goto L_0x01e7
            r13 = 1065353216(0x3var_, float:1.0)
            goto L_0x01e8
        L_0x01e7:
            r13 = 0
        L_0x01e8:
            r12[r3] = r13
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r11, r12)
            r2.add(r8)
            org.telegram.ui.Components.SearchViewPager r8 = r0.searchViewPager
            android.util.Property r11 = android.view.View.SCALE_X
            float[] r12 = new float[r7]
            r13 = 1065772646(0x3var_, float:1.05)
            if (r1 == 0) goto L_0x01ff
            r14 = 1065353216(0x3var_, float:1.0)
            goto L_0x0202
        L_0x01ff:
            r14 = 1065772646(0x3var_, float:1.05)
        L_0x0202:
            r12[r3] = r14
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r11, r12)
            r2.add(r8)
            org.telegram.ui.Components.SearchViewPager r8 = r0.searchViewPager
            android.util.Property r11 = android.view.View.SCALE_Y
            float[] r12 = new float[r7]
            if (r1 == 0) goto L_0x0215
            r13 = 1065353216(0x3var_, float:1.0)
        L_0x0215:
            r12[r3] = r13
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r11, r12)
            r2.add(r8)
            org.telegram.ui.ActionBar.ActionBarMenuItem r8 = r0.passcodeItem
            if (r8 == 0) goto L_0x0239
            org.telegram.ui.Components.RLottieImageView r8 = r8.getIconView()
            android.util.Property r11 = android.view.View.ALPHA
            float[] r12 = new float[r7]
            if (r1 == 0) goto L_0x022e
            r13 = 0
            goto L_0x0230
        L_0x022e:
            r13 = 1065353216(0x3var_, float:1.0)
        L_0x0230:
            r12[r3] = r13
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r11, r12)
            r2.add(r8)
        L_0x0239:
            org.telegram.ui.Components.FilterTabsView r8 = r0.filterTabsView
            r11 = 100
            if (r8 == 0) goto L_0x0269
            int r8 = r8.getVisibility()
            if (r8 != 0) goto L_0x0269
            org.telegram.ui.Components.FilterTabsView r8 = r0.filterTabsView
            org.telegram.ui.Components.RecyclerListView r8 = r8.getTabsContainer()
            android.util.Property r13 = android.view.View.ALPHA
            float[] r14 = new float[r7]
            if (r1 == 0) goto L_0x0253
            r15 = 0
            goto L_0x0255
        L_0x0253:
            r15 = 1065353216(0x3var_, float:1.0)
        L_0x0255:
            r14[r3] = r15
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r13, r14)
            android.animation.ObjectAnimator r8 = r8.setDuration(r11)
            r0.tabsAlphaAnimator = r8
            org.telegram.ui.DialogsActivity$26 r13 = new org.telegram.ui.DialogsActivity$26
            r13.<init>()
            r8.addListener(r13)
        L_0x0269:
            r8 = 2
            float[] r8 = new float[r8]
            float r13 = r0.searchAnimationProgress
            r8[r3] = r13
            if (r1 == 0) goto L_0x0274
            r9 = 1065353216(0x3var_, float:1.0)
        L_0x0274:
            r8[r7] = r9
            android.animation.ValueAnimator r8 = android.animation.ValueAnimator.ofFloat(r8)
            org.telegram.ui.-$$Lambda$DialogsActivity$45OcTdZUTTV2reEKNqEj5e_B8Mk r9 = new org.telegram.ui.-$$Lambda$DialogsActivity$45OcTdZUTTV2reEKNqEj5e_B8Mk
            r9.<init>()
            r8.addUpdateListener(r9)
            r2.add(r8)
            android.animation.AnimatorSet r8 = r0.searchAnimator
            r8.playTogether(r2)
            android.animation.AnimatorSet r2 = r0.searchAnimator
            r13 = 180(0xb4, double:8.9E-322)
            if (r1 == 0) goto L_0x0293
            r8 = 200(0xc8, double:9.9E-322)
            goto L_0x0294
        L_0x0293:
            r8 = r13
        L_0x0294:
            r2.setDuration(r8)
            android.animation.AnimatorSet r2 = r0.searchAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r8 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
            r2.setInterpolator(r8)
            boolean r2 = r0.filterTabsViewIsVisible
            if (r2 == 0) goto L_0x02eb
            int r2 = r0.folderId
            if (r2 != 0) goto L_0x02a9
            java.lang.String r2 = "actionBarDefault"
            goto L_0x02ab
        L_0x02a9:
            java.lang.String r2 = "actionBarDefaultArchived"
        L_0x02ab:
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            int r8 = android.graphics.Color.red(r2)
            int r9 = android.graphics.Color.red(r4)
            int r8 = r8 - r9
            int r8 = java.lang.Math.abs(r8)
            int r9 = android.graphics.Color.green(r2)
            int r10 = android.graphics.Color.green(r4)
            int r9 = r9 - r10
            int r9 = java.lang.Math.abs(r9)
            int r8 = r8 + r9
            int r2 = android.graphics.Color.blue(r2)
            int r4 = android.graphics.Color.blue(r4)
            int r2 = r2 - r4
            int r2 = java.lang.Math.abs(r2)
            int r8 = r8 + r2
            float r2 = (float) r8
            r4 = 1132396544(0x437var_, float:255.0)
            float r2 = r2 / r4
            r4 = 1050253722(0x3e99999a, float:0.3)
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 <= 0) goto L_0x02e8
            r3 = 1
        L_0x02e8:
            r0.searchAnimationTabsDelayedCrossfade = r3
            goto L_0x02ed
        L_0x02eb:
            r0.searchAnimationTabsDelayedCrossfade = r7
        L_0x02ed:
            if (r1 != 0) goto L_0x0312
            android.animation.AnimatorSet r2 = r0.searchAnimator
            r3 = 20
            r2.setStartDelay(r3)
            android.animation.Animator r2 = r0.tabsAlphaAnimator
            if (r2 == 0) goto L_0x0312
            boolean r3 = r0.searchAnimationTabsDelayedCrossfade
            if (r3 == 0) goto L_0x0309
            r3 = 80
            r2.setStartDelay(r3)
            android.animation.Animator r2 = r0.tabsAlphaAnimator
            r2.setDuration(r11)
            goto L_0x0312
        L_0x0309:
            if (r1 == 0) goto L_0x030e
            r8 = 200(0xc8, double:9.9E-322)
            goto L_0x030f
        L_0x030e:
            r8 = r13
        L_0x030f:
            r2.setDuration(r8)
        L_0x0312:
            android.animation.AnimatorSet r2 = r0.searchAnimator
            org.telegram.ui.DialogsActivity$27 r3 = new org.telegram.ui.DialogsActivity$27
            r3.<init>(r1)
            r2.addListener(r3)
            org.telegram.messenger.NotificationCenter r2 = r16.getNotificationCenter()
            int r3 = r0.animationIndex
            int r2 = r2.setAnimationInProgress(r3, r5)
            r0.animationIndex = r2
            android.animation.AnimatorSet r2 = r0.searchAnimator
            r2.start()
            android.animation.Animator r2 = r0.tabsAlphaAnimator
            if (r2 == 0) goto L_0x03fa
            r2.start()
            goto L_0x03fa
        L_0x0336:
            r0.setDialogsListFrozen(r3)
            if (r1 == 0) goto L_0x0347
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r0.viewPages
            r2 = r2[r3]
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r2 = r2.listView
            r2.hide()
            goto L_0x0352
        L_0x0347:
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r0.viewPages
            r2 = r2[r3]
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r2 = r2.listView
            r2.show()
        L_0x0352:
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r0.viewPages
            r2 = r2[r3]
            if (r1 == 0) goto L_0x035a
            r5 = 0
            goto L_0x035c
        L_0x035a:
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x035c:
            r2.setAlpha(r5)
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r0.viewPages
            r2 = r2[r3]
            if (r1 == 0) goto L_0x0369
            r5 = 1063675494(0x3var_, float:0.9)
            goto L_0x036b
        L_0x0369:
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x036b:
            r2.setScaleX(r5)
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r0.viewPages
            r2 = r2[r3]
            if (r1 == 0) goto L_0x0375
            goto L_0x0377
        L_0x0375:
            r8 = 1065353216(0x3var_, float:1.0)
        L_0x0377:
            r2.setScaleY(r8)
            org.telegram.ui.Components.SearchViewPager r2 = r0.searchViewPager
            if (r1 == 0) goto L_0x0381
            r5 = 1065353216(0x3var_, float:1.0)
            goto L_0x0382
        L_0x0381:
            r5 = 0
        L_0x0382:
            r2.setAlpha(r5)
            org.telegram.ui.Adapters.FiltersView r2 = r0.filtersView
            if (r1 == 0) goto L_0x038c
            r5 = 1065353216(0x3var_, float:1.0)
            goto L_0x038d
        L_0x038c:
            r5 = 0
        L_0x038d:
            r2.setAlpha(r5)
            org.telegram.ui.Components.SearchViewPager r2 = r0.searchViewPager
            r5 = 1066192077(0x3f8ccccd, float:1.1)
            if (r1 == 0) goto L_0x039a
            r7 = 1065353216(0x3var_, float:1.0)
            goto L_0x039d
        L_0x039a:
            r7 = 1066192077(0x3f8ccccd, float:1.1)
        L_0x039d:
            r2.setScaleX(r7)
            org.telegram.ui.Components.SearchViewPager r2 = r0.searchViewPager
            if (r1 == 0) goto L_0x03a6
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x03a6:
            r2.setScaleY(r5)
            org.telegram.ui.Components.FilterTabsView r2 = r0.filterTabsView
            if (r2 == 0) goto L_0x03d1
            int r2 = r2.getVisibility()
            if (r2 != 0) goto L_0x03d1
            org.telegram.ui.Components.FilterTabsView r2 = r0.filterTabsView
            if (r1 == 0) goto L_0x03be
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = -r4
            float r4 = (float) r4
            goto L_0x03bf
        L_0x03be:
            r4 = 0
        L_0x03bf:
            r2.setTranslationY(r4)
            org.telegram.ui.Components.FilterTabsView r2 = r0.filterTabsView
            org.telegram.ui.Components.RecyclerListView r2 = r2.getTabsContainer()
            if (r1 == 0) goto L_0x03cc
            r4 = 0
            goto L_0x03ce
        L_0x03cc:
            r4 = 1065353216(0x3var_, float:1.0)
        L_0x03ce:
            r2.setAlpha(r4)
        L_0x03d1:
            org.telegram.ui.Components.FilterTabsView r2 = r0.filterTabsView
            r4 = 8
            if (r2 == 0) goto L_0x03e4
            boolean r5 = r0.canShowFilterTabsView
            if (r5 == 0) goto L_0x03e1
            if (r1 != 0) goto L_0x03e1
            r2.setVisibility(r3)
            goto L_0x03e4
        L_0x03e1:
            r2.setVisibility(r4)
        L_0x03e4:
            org.telegram.ui.Components.SearchViewPager r2 = r0.searchViewPager
            if (r1 == 0) goto L_0x03e9
            goto L_0x03eb
        L_0x03e9:
            r3 = 8
        L_0x03eb:
            r2.setVisibility(r3)
            if (r1 == 0) goto L_0x03f2
            r9 = 1065353216(0x3var_, float:1.0)
        L_0x03f2:
            r0.setSearchAnimationProgress(r9)
            android.view.View r2 = r0.fragmentView
            r2.invalidate()
        L_0x03fa:
            int r2 = r0.initialSearchType
            if (r2 < 0) goto L_0x0403
            org.telegram.ui.Components.SearchViewPager r3 = r0.searchViewPager
            r3.setPosition(r2)
        L_0x0403:
            if (r1 != 0) goto L_0x0407
            r0.initialSearchType = r6
        L_0x0407:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.showSearch(boolean, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showSearch$13 */
    public /* synthetic */ void lambda$showSearch$13$DialogsActivity(ValueAnimator valueAnimator) {
        setSearchAnimationProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    public boolean onlyDialogsAdapter() {
        return this.onlySelect || !this.searchViewPager.dialogsSearchAdapter.hasRecentSearch() || getMessagesController().getTotalDialogsCount() <= 10;
    }

    private void updateFilterTabsVisibility(boolean z) {
        int i = 0;
        if (this.isPaused) {
            z = false;
        }
        float f = 1.0f;
        if (this.searchIsShowed) {
            ValueAnimator valueAnimator = this.filtersTabAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            boolean z2 = this.canShowFilterTabsView;
            this.filterTabsViewIsVisible = z2;
            if (!z2) {
                f = 0.0f;
            }
            this.filterTabsProgress = f;
            return;
        }
        final boolean z3 = this.canShowFilterTabsView;
        if (this.filterTabsViewIsVisible != z3) {
            ValueAnimator valueAnimator2 = this.filtersTabAnimator;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
            }
            this.filterTabsViewIsVisible = z3;
            if (z) {
                if (z3) {
                    if (this.filterTabsView.getVisibility() != 0) {
                        this.filterTabsView.setVisibility(0);
                    }
                    this.filtersTabAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                    this.filterTabsMoveFrom = (float) AndroidUtilities.dp(44.0f);
                } else {
                    this.filtersTabAnimator = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
                    this.filterTabsMoveFrom = Math.max(0.0f, ((float) AndroidUtilities.dp(44.0f)) + this.actionBar.getTranslationY());
                }
                float translationY = this.actionBar.getTranslationY();
                this.filtersTabAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        ValueAnimator unused = DialogsActivity.this.filtersTabAnimator = null;
                        float unused2 = DialogsActivity.this.scrollAdditionalOffset = ((float) AndroidUtilities.dp(44.0f)) - DialogsActivity.this.filterTabsMoveFrom;
                        if (!z3) {
                            DialogsActivity.this.filterTabsView.setVisibility(8);
                        }
                        if (DialogsActivity.this.fragmentView != null) {
                            DialogsActivity.this.fragmentView.requestLayout();
                        }
                        DialogsActivity.this.getNotificationCenter().onAnimationFinish(DialogsActivity.this.animationIndex);
                    }
                });
                this.filtersTabAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(z3, translationY) {
                    public final /* synthetic */ boolean f$1;
                    public final /* synthetic */ float f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        DialogsActivity.this.lambda$updateFilterTabsVisibility$14$DialogsActivity(this.f$1, this.f$2, valueAnimator);
                    }
                });
                this.filtersTabAnimator.setDuration(220);
                this.filtersTabAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.animationIndex = getNotificationCenter().setAnimationInProgress(this.animationIndex, (int[]) null);
                this.filtersTabAnimator.start();
                this.fragmentView.requestLayout();
                return;
            }
            if (!z3) {
                f = 0.0f;
            }
            this.filterTabsProgress = f;
            FilterTabsView filterTabsView2 = this.filterTabsView;
            if (!z3) {
                i = 8;
            }
            filterTabsView2.setVisibility(i);
            View view = this.fragmentView;
            if (view != null) {
                view.invalidate();
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateFilterTabsVisibility$14 */
    public /* synthetic */ void lambda$updateFilterTabsVisibility$14$DialogsActivity(boolean z, float f, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.filterTabsProgress = floatValue;
        if (!z) {
            setScrollY(f * floatValue);
        }
        View view = this.fragmentView;
        if (view != null) {
            view.invalidate();
        }
    }

    /* access modifiers changed from: private */
    public void setSearchAnimationProgress(float f) {
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
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0126  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0129  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0130 A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void checkListLoad(org.telegram.ui.DialogsActivity.ViewPage r15) {
        /*
            r14 = this;
            boolean r0 = r14.tabsAnimationInProgress
            if (r0 != 0) goto L_0x013c
            boolean r0 = r14.startedTracking
            if (r0 != 0) goto L_0x013c
            org.telegram.ui.Components.FilterTabsView r0 = r14.filterTabsView
            if (r0 == 0) goto L_0x001c
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x001c
            org.telegram.ui.Components.FilterTabsView r0 = r14.filterTabsView
            boolean r0 = r0.isAnimatingIndicator()
            if (r0 == 0) goto L_0x001c
            goto L_0x013c
        L_0x001c:
            androidx.recyclerview.widget.LinearLayoutManager r0 = r15.layoutManager
            int r0 = r0.findFirstVisibleItemPosition()
            androidx.recyclerview.widget.LinearLayoutManager r1 = r15.layoutManager
            int r1 = r1.findLastVisibleItemPosition()
            androidx.recyclerview.widget.LinearLayoutManager r2 = r15.layoutManager
            int r2 = r2.findLastVisibleItemPosition()
            int r2 = r2 - r0
            int r0 = java.lang.Math.abs(r2)
            r2 = 1
            int r0 = r0 + r2
            r3 = -1
            r4 = 0
            if (r1 == r3) goto L_0x005c
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r3 = r15.listView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r3 = r3.findViewHolderForAdapterPosition(r1)
            if (r3 == 0) goto L_0x0053
            int r3 = r3.getItemViewType()
            r5 = 11
            if (r3 != r5) goto L_0x0053
            r3 = 1
            goto L_0x0054
        L_0x0053:
            r3 = 0
        L_0x0054:
            r14.floatingForceVisible = r3
            if (r3 == 0) goto L_0x005e
            r14.hideFloatingButton(r4)
            goto L_0x005e
        L_0x005c:
            r14.floatingForceVisible = r4
        L_0x005e:
            int r3 = r15.dialogsType
            r5 = 8
            r6 = 7
            if (r3 == r6) goto L_0x006d
            int r3 = r15.dialogsType
            if (r3 != r5) goto L_0x00d8
        L_0x006d:
            org.telegram.messenger.MessagesController r3 = r14.getMessagesController()
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r3 = r3.dialogFilters
            int r7 = r15.selectedType
            if (r7 < 0) goto L_0x00d8
            int r7 = r15.selectedType
            int r3 = r3.size()
            if (r7 >= r3) goto L_0x00d8
            org.telegram.messenger.MessagesController r3 = r14.getMessagesController()
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r3 = r3.dialogFilters
            int r7 = r15.selectedType
            java.lang.Object r3 = r3.get(r7)
            org.telegram.messenger.MessagesController$DialogFilter r3 = (org.telegram.messenger.MessagesController.DialogFilter) r3
            int r3 = r3.flags
            int r7 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED
            r3 = r3 & r7
            if (r3 != 0) goto L_0x00d8
            if (r0 <= 0) goto L_0x00b0
            int r3 = r14.currentAccount
            int r7 = r15.dialogsType
            boolean r8 = r14.dialogsListFrozen
            java.util.ArrayList r3 = r14.getDialogsArray(r3, r7, r2, r8)
            int r3 = r3.size()
            int r3 = r3 + -10
            if (r1 >= r3) goto L_0x00bc
        L_0x00b0:
            if (r0 != 0) goto L_0x00d8
            org.telegram.messenger.MessagesController r3 = r14.getMessagesController()
            boolean r3 = r3.isDialogsEndReached(r2)
            if (r3 != 0) goto L_0x00d8
        L_0x00bc:
            org.telegram.messenger.MessagesController r3 = r14.getMessagesController()
            boolean r3 = r3.isDialogsEndReached(r2)
            r3 = r3 ^ r2
            if (r3 != 0) goto L_0x00d5
            org.telegram.messenger.MessagesController r7 = r14.getMessagesController()
            boolean r7 = r7.isServerDialogsEndReached(r2)
            if (r7 != 0) goto L_0x00d2
            goto L_0x00d5
        L_0x00d2:
            r13 = r3
            r12 = 0
            goto L_0x00da
        L_0x00d5:
            r13 = r3
            r12 = 1
            goto L_0x00da
        L_0x00d8:
            r12 = 0
            r13 = 0
        L_0x00da:
            if (r0 <= 0) goto L_0x00f2
            int r3 = r14.currentAccount
            int r7 = r15.dialogsType
            int r8 = r14.folderId
            boolean r9 = r14.dialogsListFrozen
            java.util.ArrayList r3 = r14.getDialogsArray(r3, r7, r8, r9)
            int r3 = r3.size()
            int r3 = r3 + -10
            if (r1 >= r3) goto L_0x010c
        L_0x00f2:
            if (r0 != 0) goto L_0x012c
            int r0 = r15.dialogsType
            if (r0 == r6) goto L_0x0100
            int r15 = r15.dialogsType
            if (r15 != r5) goto L_0x012c
        L_0x0100:
            org.telegram.messenger.MessagesController r15 = r14.getMessagesController()
            int r0 = r14.folderId
            boolean r15 = r15.isDialogsEndReached(r0)
            if (r15 != 0) goto L_0x012c
        L_0x010c:
            org.telegram.messenger.MessagesController r15 = r14.getMessagesController()
            int r0 = r14.folderId
            boolean r15 = r15.isDialogsEndReached(r0)
            r15 = r15 ^ r2
            if (r15 != 0) goto L_0x0129
            org.telegram.messenger.MessagesController r0 = r14.getMessagesController()
            int r1 = r14.folderId
            boolean r0 = r0.isServerDialogsEndReached(r1)
            if (r0 != 0) goto L_0x0126
            goto L_0x0129
        L_0x0126:
            r11 = r15
            r10 = 0
            goto L_0x012e
        L_0x0129:
            r11 = r15
            r10 = 1
            goto L_0x012e
        L_0x012c:
            r10 = 0
            r11 = 0
        L_0x012e:
            if (r10 != 0) goto L_0x0132
            if (r12 == 0) goto L_0x013c
        L_0x0132:
            org.telegram.ui.-$$Lambda$DialogsActivity$2fZnUzi7QumW83eBA6HmZeI5dOY r15 = new org.telegram.ui.-$$Lambda$DialogsActivity$2fZnUzi7QumW83eBA6HmZeI5dOY
            r8 = r15
            r9 = r14
            r8.<init>(r10, r11, r12, r13)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r15)
        L_0x013c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.checkListLoad(org.telegram.ui.DialogsActivity$ViewPage):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkListLoad$15 */
    public /* synthetic */ void lambda$checkListLoad$15$DialogsActivity(boolean z, boolean z2, boolean z3, boolean z4) {
        if (z) {
            getMessagesController().loadDialogs(this.folderId, -1, 100, z2);
        }
        if (z3) {
            getMessagesController().loadDialogs(1, -1, 100, z4);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:93:0x0187 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x0188  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void onItemClick(android.view.View r18, int r19, androidx.recyclerview.widget.RecyclerView.Adapter r20) {
        /*
            r17 = this;
            r6 = r17
            r0 = r18
            r1 = r19
            r2 = r20
            android.app.Activity r3 = r17.getParentActivity()
            if (r3 != 0) goto L_0x000f
            return
        L_0x000f:
            boolean r3 = r2 instanceof org.telegram.ui.Adapters.DialogsAdapter
            r4 = 32
            r7 = 0
            r5 = 1
            r9 = 0
            if (r3 == 0) goto L_0x00ec
            r10 = r2
            org.telegram.ui.Adapters.DialogsAdapter r10 = (org.telegram.ui.Adapters.DialogsAdapter) r10
            org.telegram.tgnet.TLObject r1 = r10.getItem(r1)
            boolean r10 = r1 instanceof org.telegram.tgnet.TLRPC$User
            if (r10 == 0) goto L_0x002b
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            int r1 = r1.id
        L_0x0028:
            long r10 = (long) r1
            goto L_0x0181
        L_0x002b:
            boolean r10 = r1 instanceof org.telegram.tgnet.TLRPC$Dialog
            if (r10 == 0) goto L_0x0065
            org.telegram.tgnet.TLRPC$Dialog r1 = (org.telegram.tgnet.TLRPC$Dialog) r1
            boolean r10 = r1 instanceof org.telegram.tgnet.TLRPC$TL_dialogFolder
            if (r10 == 0) goto L_0x0057
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            boolean r0 = r0.isActionModeShowed()
            if (r0 == 0) goto L_0x003e
            return
        L_0x003e:
            org.telegram.tgnet.TLRPC$TL_dialogFolder r1 = (org.telegram.tgnet.TLRPC$TL_dialogFolder) r1
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_folder r1 = r1.folder
            int r1 = r1.id
            java.lang.String r2 = "folderId"
            r0.putInt(r2, r1)
            org.telegram.ui.DialogsActivity r1 = new org.telegram.ui.DialogsActivity
            r1.<init>(r0)
            r6.presentFragment(r1)
            return
        L_0x0057:
            long r10 = r1.id
            org.telegram.ui.ActionBar.ActionBar r12 = r6.actionBar
            boolean r12 = r12.isActionModeShowed()
            if (r12 == 0) goto L_0x0181
            r6.showOrUpdateActionMode(r1, r0)
            return
        L_0x0065:
            boolean r10 = r1 instanceof org.telegram.tgnet.TLRPC$TL_recentMeUrlChat
            if (r10 == 0) goto L_0x006f
            org.telegram.tgnet.TLRPC$TL_recentMeUrlChat r1 = (org.telegram.tgnet.TLRPC$TL_recentMeUrlChat) r1
            int r1 = r1.chat_id
        L_0x006d:
            int r1 = -r1
            goto L_0x0028
        L_0x006f:
            boolean r10 = r1 instanceof org.telegram.tgnet.TLRPC$TL_recentMeUrlUser
            if (r10 == 0) goto L_0x0078
            org.telegram.tgnet.TLRPC$TL_recentMeUrlUser r1 = (org.telegram.tgnet.TLRPC$TL_recentMeUrlUser) r1
            int r1 = r1.user_id
            goto L_0x0028
        L_0x0078:
            boolean r10 = r1 instanceof org.telegram.tgnet.TLRPC$TL_recentMeUrlChatInvite
            if (r10 == 0) goto L_0x00be
            org.telegram.tgnet.TLRPC$TL_recentMeUrlChatInvite r1 = (org.telegram.tgnet.TLRPC$TL_recentMeUrlChatInvite) r1
            org.telegram.tgnet.TLRPC$ChatInvite r10 = r1.chat_invite
            org.telegram.tgnet.TLRPC$Chat r11 = r10.chat
            if (r11 != 0) goto L_0x008c
            boolean r12 = r10.channel
            if (r12 == 0) goto L_0x009a
            boolean r12 = r10.megagroup
            if (r12 != 0) goto L_0x009a
        L_0x008c:
            if (r11 == 0) goto L_0x00b6
            boolean r11 = org.telegram.messenger.ChatObject.isChannel(r11)
            if (r11 == 0) goto L_0x009a
            org.telegram.tgnet.TLRPC$Chat r11 = r10.chat
            boolean r11 = r11.megagroup
            if (r11 == 0) goto L_0x00b6
        L_0x009a:
            java.lang.String r0 = r1.url
            r1 = 47
            int r1 = r0.indexOf(r1)
            if (r1 <= 0) goto L_0x00a9
            int r1 = r1 + r5
            java.lang.String r0 = r0.substring(r1)
        L_0x00a9:
            org.telegram.ui.Components.JoinGroupAlert r1 = new org.telegram.ui.Components.JoinGroupAlert
            android.app.Activity r2 = r17.getParentActivity()
            r1.<init>(r2, r10, r0, r6)
            r6.showDialog(r1)
            return
        L_0x00b6:
            org.telegram.tgnet.TLRPC$Chat r1 = r10.chat
            if (r1 == 0) goto L_0x00bd
            int r1 = r1.id
            goto L_0x006d
        L_0x00bd:
            return
        L_0x00be:
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_recentMeUrlStickerSet
            if (r0 == 0) goto L_0x00e7
            org.telegram.tgnet.TLRPC$TL_recentMeUrlStickerSet r1 = (org.telegram.tgnet.TLRPC$TL_recentMeUrlStickerSet) r1
            org.telegram.tgnet.TLRPC$StickerSetCovered r0 = r1.set
            org.telegram.tgnet.TLRPC$StickerSet r0 = r0.set
            org.telegram.tgnet.TLRPC$TL_inputStickerSetID r3 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetID
            r3.<init>()
            long r1 = r0.id
            r3.id = r1
            long r0 = r0.access_hash
            r3.access_hash = r0
            org.telegram.ui.Components.StickersAlert r7 = new org.telegram.ui.Components.StickersAlert
            android.app.Activity r1 = r17.getParentActivity()
            r4 = 0
            r5 = 0
            r0 = r7
            r2 = r17
            r0.<init>(r1, r2, r3, r4, r5)
            r6.showDialog(r7)
            return
        L_0x00e7:
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_recentMeUrlUnknown
            if (r0 == 0) goto L_0x00eb
        L_0x00eb:
            return
        L_0x00ec:
            org.telegram.ui.Components.SearchViewPager r10 = r6.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r10 = r10.dialogsSearchAdapter
            if (r2 != r10) goto L_0x0180
            java.lang.Object r10 = r10.getItem(r1)
            org.telegram.ui.Components.SearchViewPager r11 = r6.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r11 = r11.dialogsSearchAdapter
            boolean r1 = r11.isGlobalSearch(r1)
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC$User
            if (r11 == 0) goto L_0x0112
            org.telegram.tgnet.TLRPC$User r10 = (org.telegram.tgnet.TLRPC$User) r10
            int r11 = r10.id
            long r11 = (long) r11
            boolean r13 = r6.onlySelect
            if (r13 != 0) goto L_0x010f
            r6.searchDialogId = r11
            r6.searchObject = r10
        L_0x010f:
            r10 = r11
            goto L_0x0182
        L_0x0112:
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r11 == 0) goto L_0x0125
            org.telegram.tgnet.TLRPC$Chat r10 = (org.telegram.tgnet.TLRPC$Chat) r10
            int r11 = r10.id
            int r11 = -r11
            long r11 = (long) r11
            boolean r13 = r6.onlySelect
            if (r13 != 0) goto L_0x010f
            r6.searchDialogId = r11
            r6.searchObject = r10
            goto L_0x010f
        L_0x0125:
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC$EncryptedChat
            if (r11 == 0) goto L_0x0138
            org.telegram.tgnet.TLRPC$EncryptedChat r10 = (org.telegram.tgnet.TLRPC$EncryptedChat) r10
            int r11 = r10.id
            long r11 = (long) r11
            long r11 = r11 << r4
            boolean r13 = r6.onlySelect
            if (r13 != 0) goto L_0x010f
            r6.searchDialogId = r11
            r6.searchObject = r10
            goto L_0x010f
        L_0x0138:
            boolean r11 = r10 instanceof org.telegram.messenger.MessageObject
            if (r11 == 0) goto L_0x0155
            org.telegram.messenger.MessageObject r10 = (org.telegram.messenger.MessageObject) r10
            long r11 = r10.getDialogId()
            int r10 = r10.getId()
            org.telegram.ui.Components.SearchViewPager r13 = r6.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r13 = r13.dialogsSearchAdapter
            java.lang.String r14 = r13.getLastSearchString()
            r13.addHashtagsFromMessage(r14)
            r15 = r11
            r12 = r10
            r10 = r15
            goto L_0x0183
        L_0x0155:
            boolean r11 = r10 instanceof java.lang.String
            if (r11 == 0) goto L_0x017e
            java.lang.String r10 = (java.lang.String) r10
            org.telegram.ui.Components.SearchViewPager r11 = r6.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r11 = r11.dialogsSearchAdapter
            boolean r11 = r11.isHashtagSearch()
            if (r11 == 0) goto L_0x016b
            org.telegram.ui.ActionBar.ActionBar r11 = r6.actionBar
            r11.openSearchField(r10, r9)
            goto L_0x017e
        L_0x016b:
            java.lang.String r11 = "section"
            boolean r11 = r10.equals(r11)
            if (r11 != 0) goto L_0x017e
            org.telegram.ui.NewContactActivity r11 = new org.telegram.ui.NewContactActivity
            r11.<init>()
            r11.setInitialPhoneNumber(r10, r5)
            r6.presentFragment(r11)
        L_0x017e:
            r10 = r7
            goto L_0x0182
        L_0x0180:
            r10 = r7
        L_0x0181:
            r1 = 0
        L_0x0182:
            r12 = 0
        L_0x0183:
            int r13 = (r10 > r7 ? 1 : (r10 == r7 ? 0 : -1))
            if (r13 != 0) goto L_0x0188
            return
        L_0x0188:
            boolean r7 = r6.onlySelect
            if (r7 == 0) goto L_0x01b7
            boolean r1 = r6.validateSlowModeDialog(r10)
            if (r1 != 0) goto L_0x0193
            return
        L_0x0193:
            java.util.ArrayList<java.lang.Long> r1 = r6.selectedDialogs
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x01b2
            boolean r0 = r6.addOrRemoveSelectedDialog(r10, r0)
            org.telegram.ui.Components.SearchViewPager r1 = r6.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r1 = r1.dialogsSearchAdapter
            if (r2 != r1) goto L_0x01ad
            org.telegram.ui.ActionBar.ActionBar r1 = r6.actionBar
            r1.closeSearchField()
            r6.findAndUpdateCheckBox(r10, r0)
        L_0x01ad:
            r17.updateSelectedCount()
            goto L_0x02b6
        L_0x01b2:
            r6.didSelectResult(r10, r5, r9)
            goto L_0x02b6
        L_0x01b7:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            int r7 = (int) r10
            long r13 = r10 >> r4
            int r4 = (int) r13
            if (r7 == 0) goto L_0x01f1
            if (r7 <= 0) goto L_0x01cb
            java.lang.String r4 = "user_id"
            r0.putInt(r4, r7)
            goto L_0x01f6
        L_0x01cb:
            if (r12 == 0) goto L_0x01ea
            org.telegram.messenger.MessagesController r4 = r17.getMessagesController()
            int r8 = -r7
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            org.telegram.tgnet.TLRPC$Chat r4 = r4.getChat(r8)
            if (r4 == 0) goto L_0x01ea
            org.telegram.tgnet.TLRPC$InputChannel r8 = r4.migrated_to
            if (r8 == 0) goto L_0x01ea
            java.lang.String r8 = "migrated_to"
            r0.putInt(r8, r7)
            org.telegram.tgnet.TLRPC$InputChannel r4 = r4.migrated_to
            int r4 = r4.channel_id
            int r7 = -r4
        L_0x01ea:
            int r4 = -r7
            java.lang.String r8 = "chat_id"
            r0.putInt(r8, r4)
            goto L_0x01f6
        L_0x01f1:
            java.lang.String r8 = "enc_id"
            r0.putInt(r8, r4)
        L_0x01f6:
            if (r12 == 0) goto L_0x01fe
            java.lang.String r1 = "message_id"
            r0.putInt(r1, r12)
            goto L_0x0214
        L_0x01fe:
            if (r1 != 0) goto L_0x0204
            r17.closeSearch()
            goto L_0x0214
        L_0x0204:
            org.telegram.tgnet.TLObject r1 = r6.searchObject
            if (r1 == 0) goto L_0x0214
            org.telegram.ui.Components.SearchViewPager r4 = r6.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r4 = r4.dialogsSearchAdapter
            long r12 = r6.searchDialogId
            r4.putRecentSearch(r12, r1)
            r1 = 0
            r6.searchObject = r1
        L_0x0214:
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 == 0) goto L_0x0244
            long r12 = r6.openedDialogId
            int r1 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1))
            if (r1 != 0) goto L_0x0227
            org.telegram.ui.Components.SearchViewPager r1 = r6.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r1 = r1.dialogsSearchAdapter
            if (r2 == r1) goto L_0x0227
            return
        L_0x0227:
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r6.viewPages
            if (r1 == 0) goto L_0x023f
            r1 = 0
        L_0x022c:
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r6.viewPages
            int r4 = r2.length
            if (r1 >= r4) goto L_0x023f
            r2 = r2[r1]
            org.telegram.ui.Adapters.DialogsAdapter r2 = r2.dialogsAdapter
            r6.openedDialogId = r10
            r2.setOpenedDialogId(r10)
            int r1 = r1 + 1
            goto L_0x022c
        L_0x023f:
            r1 = 512(0x200, float:7.175E-43)
            r6.updateVisibleRows(r1)
        L_0x0244:
            org.telegram.ui.Components.SearchViewPager r1 = r6.searchViewPager
            boolean r1 = r1.actionModeShowing()
            if (r1 == 0) goto L_0x0251
            org.telegram.ui.Components.SearchViewPager r1 = r6.searchViewPager
            r1.hideActionMode()
        L_0x0251:
            java.lang.String r1 = r6.searchString
            if (r1 == 0) goto L_0x0273
            org.telegram.messenger.MessagesController r1 = r17.getMessagesController()
            boolean r1 = r1.checkCanOpenChat(r0, r6)
            if (r1 == 0) goto L_0x02b6
            org.telegram.messenger.NotificationCenter r1 = r17.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r3 = new java.lang.Object[r9]
            r1.postNotificationName(r2, r3)
            org.telegram.ui.ChatActivity r1 = new org.telegram.ui.ChatActivity
            r1.<init>(r0)
            r6.presentFragment(r1)
            goto L_0x02b6
        L_0x0273:
            org.telegram.messenger.MessagesController r1 = r17.getMessagesController()
            boolean r1 = r1.checkCanOpenChat(r0, r6)
            if (r1 == 0) goto L_0x02b6
            org.telegram.ui.ChatActivity r1 = new org.telegram.ui.ChatActivity
            r1.<init>(r0)
            int r0 = r6.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r0 = r0.dialogMessage
            java.lang.Object r0 = r0.get(r10)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            if (r3 == 0) goto L_0x02b3
            if (r7 <= 0) goto L_0x02b3
            org.telegram.messenger.MessagesController r2 = r17.getMessagesController()
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r2 = r2.dialogs_dict
            java.lang.Object r2 = r2.get(r10)
            if (r2 == 0) goto L_0x02a6
            boolean r0 = org.telegram.messenger.MessageObject.isSystemSignUp(r0)
            if (r0 == 0) goto L_0x02b3
        L_0x02a6:
            org.telegram.messenger.MessagesController r0 = r17.getMessagesController()
            org.telegram.tgnet.TLRPC$Document r0 = r0.getPreloadedSticker()
            if (r0 == 0) goto L_0x02b3
            r1.setPreloadedSticker(r0, r5)
        L_0x02b3:
            r6.presentFragment(r1)
        L_0x02b6:
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
            r8 = 2131624905(0x7f0e03c9, float:1.8877003E38)
            java.lang.String r10 = "ClearSearchSingleAlertTitle"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
            r7.setTitle(r8)
            boolean r8 = r6 instanceof org.telegram.tgnet.TLRPC$Chat
            r10 = 2131624906(0x7f0e03ca, float:1.8877005E38)
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
            r2 = 2131624907(0x7f0e03cb, float:1.8877007E38)
            java.lang.String r3 = "ClearSearchSingleUserAlertText"
            if (r8 == 0) goto L_0x00b4
            org.telegram.tgnet.TLRPC$User r6 = (org.telegram.tgnet.TLRPC$User) r6
            int r8 = r6.id
            org.telegram.messenger.UserConfig r4 = r5.getUserConfig()
            int r4 = r4.clientUserId
            if (r8 != r4) goto L_0x009e
            java.lang.Object[] r8 = new java.lang.Object[r0]
            r2 = 2131627219(0x7f0e0cd3, float:1.8881696E38)
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
            r6 = 2131624904(0x7f0e03c8, float:1.8877E38)
            java.lang.String r8 = "ClearSearchRemove"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r8, r6)
            java.lang.String r6 = r6.toUpperCase()
            org.telegram.ui.-$$Lambda$DialogsActivity$IyAX8fYw6Iiuw3CGEFmvN2MOHd4 r8 = new org.telegram.ui.-$$Lambda$DialogsActivity$IyAX8fYw6Iiuw3CGEFmvN2MOHd4
            r8.<init>(r10)
            r7.setPositiveButton(r6, r8)
            r6 = 2131624638(0x7f0e02be, float:1.8876461E38)
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
            java.util.ArrayList r8 = r5.getDialogsArray(r8, r10, r2, r3)
            int r7 = r11.fixPosition(r7)
            if (r7 < 0) goto L_0x01e4
            int r10 = r8.size()
            if (r7 < r10) goto L_0x0140
            goto L_0x01e4
        L_0x0140:
            java.lang.Object r7 = r8.get(r7)
            org.telegram.tgnet.TLRPC$Dialog r7 = (org.telegram.tgnet.TLRPC$Dialog) r7
            boolean r8 = r5.onlySelect
            if (r8 == 0) goto L_0x0167
            int r8 = r5.initialDialogsType
            r9 = 3
            if (r8 == r9) goto L_0x0154
            r9 = 10
            if (r8 == r9) goto L_0x0154
            return r1
        L_0x0154:
            long r8 = r7.id
            boolean r8 = r5.validateSlowModeDialog(r8)
            if (r8 != 0) goto L_0x015d
            return r1
        L_0x015d:
            long r7 = r7.id
            r5.addOrRemoveSelectedDialog(r7, r6)
            r5.updateSelectedCount()
            goto L_0x01e3
        L_0x0167:
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_dialogFolder
            if (r8 == 0) goto L_0x01d1
            r7 = 2
            r6.performHapticFeedback(r1, r7)
            org.telegram.ui.ActionBar.BottomSheet$Builder r6 = new org.telegram.ui.ActionBar.BottomSheet$Builder
            android.app.Activity r8 = r5.getParentActivity()
            r6.<init>(r8)
            org.telegram.messenger.MessagesStorage r8 = r5.getMessagesStorage()
            int r8 = r8.getArchiveUnreadCount()
            if (r8 == 0) goto L_0x0184
            r8 = 1
            goto L_0x0185
        L_0x0184:
            r8 = 0
        L_0x0185:
            int[] r10 = new int[r7]
            if (r8 == 0) goto L_0x018d
            r11 = 2131165678(0x7var_ee, float:1.794558E38)
            goto L_0x018e
        L_0x018d:
            r11 = 0
        L_0x018e:
            r10[r1] = r11
            boolean r11 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r11 == 0) goto L_0x0198
            r11 = 2131165350(0x7var_a6, float:1.7944915E38)
            goto L_0x019b
        L_0x0198:
            r11 = 2131165355(0x7var_ab, float:1.7944925E38)
        L_0x019b:
            r10[r0] = r11
            java.lang.CharSequence[] r7 = new java.lang.CharSequence[r7]
            if (r8 == 0) goto L_0x01aa
            r8 = 2131626028(0x7f0e082c, float:1.887928E38)
            java.lang.String r9 = "MarkAllAsRead"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r9, r8)
        L_0x01aa:
            r7[r1] = r9
            boolean r8 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r8 == 0) goto L_0x01b6
            r8 = 2131626900(0x7f0e0b94, float:1.888105E38)
            java.lang.String r9 = "PinInTheList"
            goto L_0x01bb
        L_0x01b6:
            r8 = 2131625733(0x7f0e0705, float:1.8878682E38)
            java.lang.String r9 = "HideAboveTheList"
        L_0x01bb:
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r7[r0] = r8
            org.telegram.ui.-$$Lambda$DialogsActivity$zKp89BPYgR6VryTBsugo3e3SclI r8 = new org.telegram.ui.-$$Lambda$DialogsActivity$zKp89BPYgR6VryTBsugo3e3SclI
            r8.<init>()
            r6.setItems(r7, r10, r8)
            org.telegram.ui.ActionBar.BottomSheet r6 = r6.create()
            r5.showDialog(r6)
            return r1
        L_0x01d1:
            org.telegram.ui.ActionBar.ActionBar r8 = r5.actionBar
            boolean r8 = r8.isActionModeShowed()
            if (r8 == 0) goto L_0x01e0
            boolean r8 = r5.isDialogPinned(r7)
            if (r8 == 0) goto L_0x01e0
            return r1
        L_0x01e0:
            r5.showOrUpdateActionMode(r7, r6)
        L_0x01e3:
            return r0
        L_0x01e4:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.onItemLongClick(android.view.View, int, float, float, int, androidx.recyclerview.widget.RecyclerView$Adapter):boolean");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onItemLongClick$16 */
    public /* synthetic */ void lambda$onItemLongClick$16$DialogsActivity(long j, DialogInterface dialogInterface, int i) {
        this.searchViewPager.dialogsSearchAdapter.removeRecentSearch(j);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onItemLongClick$17 */
    public /* synthetic */ void lambda$onItemLongClick$17$DialogsActivity(DialogInterface dialogInterface, int i) {
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
                DialogsActivity.this.lambda$onDialogAnimationFinished$18$DialogsActivity();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onDialogAnimationFinished$18 */
    public /* synthetic */ void lambda$onDialogAnimationFinished$18$DialogsActivity() {
        ArrayList<TLRPC$Dialog> arrayList;
        if (!(this.viewPages == null || this.folderId == 0 || ((arrayList = this.frozenDialogsList) != null && !arrayList.isEmpty()))) {
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
                DialogsActivity.this.lambda$hideActionMode$19$DialogsActivity(valueAnimator);
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
    /* renamed from: lambda$hideActionMode$19 */
    public /* synthetic */ void lambda$hideActionMode$19$DialogsActivity(ValueAnimator valueAnimator) {
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

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:165:0x040e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void perfromSelectedDialogsAction(java.util.ArrayList<java.lang.Long> r39, int r40, boolean r41) {
        /*
            r38 = this;
            r13 = r38
            r7 = r39
            r14 = r40
            android.app.Activity r0 = r38.getParentActivity()
            if (r0 != 0) goto L_0x000d
            return
        L_0x000d:
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r13.viewPages
            r15 = 0
            r0 = r0[r15]
            int r0 = r0.dialogsType
            r1 = 7
            r2 = 8
            r12 = 1
            if (r0 == r1) goto L_0x0029
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r13.viewPages
            r0 = r0[r15]
            int r0 = r0.dialogsType
            if (r0 != r2) goto L_0x0027
            goto L_0x0029
        L_0x0027:
            r9 = 0
            goto L_0x003f
        L_0x0029:
            org.telegram.messenger.MessagesController r0 = r38.getMessagesController()
            org.telegram.messenger.MessagesController$DialogFilter[] r0 = r0.selectedDialogFilter
            org.telegram.ui.DialogsActivity$ViewPage[] r3 = r13.viewPages
            r3 = r3[r15]
            int r3 = r3.dialogsType
            if (r3 != r2) goto L_0x003b
            r3 = 1
            goto L_0x003c
        L_0x003b:
            r3 = 0
        L_0x003c:
            r0 = r0[r3]
            r9 = r0
        L_0x003f:
            int r10 = r39.size()
            r0 = 105(0x69, float:1.47E-43)
            if (r14 == r0) goto L_0x06d8
            r0 = 107(0x6b, float:1.5E-43)
            if (r14 != r0) goto L_0x004d
            goto L_0x06d8
        L_0x004d:
            java.lang.String r0 = "Cancel"
            r5 = 108(0x6c, float:1.51E-43)
            r4 = 100
            if (r14 == r4) goto L_0x0057
            if (r14 != r5) goto L_0x01b5
        L_0x0057:
            int r5 = r13.canPinCount
            if (r5 == 0) goto L_0x01b5
            org.telegram.messenger.MessagesController r5 = r38.getMessagesController()
            int r3 = r13.folderId
            java.util.ArrayList r3 = r5.getDialogs(r3)
            int r5 = r3.size()
            r11 = 0
            r23 = 0
            r24 = 0
        L_0x006e:
            if (r11 >= r5) goto L_0x00a6
            java.lang.Object r25 = r3.get(r11)
            r6 = r25
            org.telegram.tgnet.TLRPC$Dialog r6 = (org.telegram.tgnet.TLRPC$Dialog) r6
            boolean r8 = r6 instanceof org.telegram.tgnet.TLRPC$TL_dialogFolder
            if (r8 == 0) goto L_0x007f
            r41 = r5
            goto L_0x009f
        L_0x007f:
            r41 = r5
            long r4 = r6.id
            int r5 = (int) r4
            boolean r4 = r13.isDialogPinned(r6)
            if (r4 == 0) goto L_0x0092
            if (r5 != 0) goto L_0x008f
            int r24 = r24 + 1
            goto L_0x009f
        L_0x008f:
            int r23 = r23 + 1
            goto L_0x009f
        L_0x0092:
            org.telegram.messenger.MessagesController r4 = r38.getMessagesController()
            long r5 = r6.id
            boolean r4 = r4.isPromoDialog(r5, r15)
            if (r4 != 0) goto L_0x009f
            goto L_0x00a6
        L_0x009f:
            int r11 = r11 + 1
            r5 = r41
            r4 = 100
            goto L_0x006e
        L_0x00a6:
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
        L_0x00aa:
            if (r3 >= r10) goto L_0x00e9
            java.lang.Object r11 = r7.get(r3)
            java.lang.Long r11 = (java.lang.Long) r11
            long r1 = r11.longValue()
            org.telegram.messenger.MessagesController r11 = r38.getMessagesController()
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r11 = r11.dialogs_dict
            java.lang.Object r11 = r11.get(r1)
            org.telegram.tgnet.TLRPC$Dialog r11 = (org.telegram.tgnet.TLRPC$Dialog) r11
            if (r11 == 0) goto L_0x00e3
            boolean r11 = r13.isDialogPinned(r11)
            if (r11 == 0) goto L_0x00cb
            goto L_0x00e3
        L_0x00cb:
            int r2 = (int) r1
            if (r2 != 0) goto L_0x00d1
            int r5 = r5 + 1
            goto L_0x00d3
        L_0x00d1:
            int r4 = r4 + 1
        L_0x00d3:
            if (r9 == 0) goto L_0x00e3
            java.util.ArrayList<java.lang.Integer> r1 = r9.alwaysShow
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            boolean r1 = r1.contains(r2)
            if (r1 == 0) goto L_0x00e3
            int r6 = r6 + 1
        L_0x00e3:
            int r3 = r3 + 1
            r1 = 7
            r2 = 8
            goto L_0x00aa
        L_0x00e9:
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r13.viewPages
            r1 = r1[r15]
            int r1 = r1.dialogsType
            r2 = 7
            if (r1 == r2) goto L_0x0116
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r13.viewPages
            r1 = r1[r15]
            int r1 = r1.dialogsType
            r2 = 8
            if (r1 != r2) goto L_0x0101
            goto L_0x0116
        L_0x0101:
            int r1 = r13.folderId
            if (r1 != 0) goto L_0x010f
            if (r9 == 0) goto L_0x0108
            goto L_0x010f
        L_0x0108:
            org.telegram.messenger.MessagesController r1 = r38.getMessagesController()
            int r1 = r1.maxPinnedDialogsCount
            goto L_0x0120
        L_0x010f:
            org.telegram.messenger.MessagesController r1 = r38.getMessagesController()
            int r1 = r1.maxFolderPinnedDialogsCount
            goto L_0x0120
        L_0x0116:
            java.util.ArrayList<java.lang.Integer> r1 = r9.alwaysShow
            int r1 = r1.size()
            r2 = 100
            int r1 = 100 - r1
        L_0x0120:
            int r5 = r5 + r24
            if (r5 > r1) goto L_0x012d
            int r4 = r4 + r23
            int r4 = r4 - r6
            if (r4 <= r1) goto L_0x012a
            goto L_0x012d
        L_0x012a:
            r4 = -1
            goto L_0x02c1
        L_0x012d:
            int r0 = r13.folderId
            java.lang.String r2 = "Chats"
            if (r0 != 0) goto L_0x0185
            if (r9 == 0) goto L_0x0136
            goto L_0x0185
        L_0x0136:
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r3 = r38.getParentActivity()
            r0.<init>((android.content.Context) r3)
            r3 = 2131624281(0x7f0e0159, float:1.8875737E38)
            java.lang.String r4 = "AppName"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setTitle(r3)
            r3 = 2131626909(0x7f0e0b9d, float:1.8881068E38)
            java.lang.Object[] r4 = new java.lang.Object[r12]
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r2, r1)
            r4[r15] = r1
            java.lang.String r1 = "PinToTopLimitReached2"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4)
            r0.setMessage(r1)
            r1 = 2131625543(0x7f0e0647, float:1.8878297E38)
            java.lang.String r2 = "FiltersSetupPinAlert"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.-$$Lambda$DialogsActivity$rbcvPmoCwYWKH17gIXkUJmFVgcI r2 = new org.telegram.ui.-$$Lambda$DialogsActivity$rbcvPmoCwYWKH17gIXkUJmFVgcI
            r2.<init>()
            r0.setNegativeButton(r1, r2)
            r1 = 2131626483(0x7f0e09f3, float:1.8880203E38)
            java.lang.String r2 = "OK"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 0
            r0.setPositiveButton(r1, r2)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r13.showDialog(r0)
            goto L_0x0199
        L_0x0185:
            r0 = 2131626899(0x7f0e0b93, float:1.8881047E38)
            java.lang.Object[] r3 = new java.lang.Object[r12]
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r2, r1)
            r3[r15] = r1
            java.lang.String r1 = "PinFolderLimitReached"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3)
            org.telegram.ui.Components.AlertsCreator.showSimpleAlert(r13, r0)
        L_0x0199:
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r13.pinItem
            r1 = 1073741824(0x40000000, float:2.0)
            org.telegram.messenger.AndroidUtilities.shakeView(r0, r1, r15)
            android.app.Activity r0 = r38.getParentActivity()
            java.lang.String r1 = "vibrator"
            java.lang.Object r0 = r0.getSystemService(r1)
            android.os.Vibrator r0 = (android.os.Vibrator) r0
            if (r0 == 0) goto L_0x01b4
            r1 = 200(0xc8, double:9.9E-322)
            r0.vibrate(r1)
        L_0x01b4:
            return
        L_0x01b5:
            r1 = 102(0x66, float:1.43E-43)
            if (r14 == r1) goto L_0x01bd
            r2 = 103(0x67, float:1.44E-43)
            if (r14 != r2) goto L_0x0291
        L_0x01bd:
            if (r10 <= r12) goto L_0x0291
            if (r41 == 0) goto L_0x0291
            org.telegram.ui.ActionBar.AlertDialog$Builder r2 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r3 = r38.getParentActivity()
            r2.<init>((android.content.Context) r3)
            if (r14 != r1) goto L_0x0203
            r1 = 2131625111(0x7f0e0497, float:1.887742E38)
            java.lang.Object[] r3 = new java.lang.Object[r12]
            java.lang.String r4 = "ChatsSelected"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r10)
            r3[r15] = r4
            java.lang.String r4 = "DeleteFewChatsTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r4, r1, r3)
            r2.setTitle(r1)
            r1 = 2131624334(0x7f0e018e, float:1.8875845E38)
            java.lang.String r3 = "AreYouSureDeleteFewChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r2.setMessage(r1)
            r1 = 2131625081(0x7f0e0479, float:1.887736E38)
            java.lang.String r3 = "Delete"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            org.telegram.ui.-$$Lambda$DialogsActivity$cG96tNBt-pFMm5_FY7kn-3-84RI r3 = new org.telegram.ui.-$$Lambda$DialogsActivity$cG96tNBt-pFMm5_FY7kn-3-84RI
            r3.<init>(r7, r14)
            r2.setPositiveButton(r1, r3)
        L_0x01ff:
            r1 = 2131624638(0x7f0e02be, float:1.8876461E38)
            goto L_0x026f
        L_0x0203:
            int r1 = r13.canClearCacheCount
            if (r1 == 0) goto L_0x023b
            r1 = 2131624889(0x7f0e03b9, float:1.887697E38)
            java.lang.Object[] r3 = new java.lang.Object[r12]
            java.lang.String r4 = "ChatsSelectedClearCache"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r10)
            r3[r15] = r4
            java.lang.String r4 = "ClearCacheFewChatsTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r4, r1, r3)
            r2.setTitle(r1)
            r1 = 2131624322(0x7f0e0182, float:1.887582E38)
            java.lang.String r3 = "AreYouSureClearHistoryCacheFewChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r2.setMessage(r1)
            r1 = 2131624893(0x7f0e03bd, float:1.8876979E38)
            java.lang.String r3 = "ClearHistoryCache"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            org.telegram.ui.-$$Lambda$DialogsActivity$Q4f8IfXVbjBGp-OJEwMH2dpG9xI r3 = new org.telegram.ui.-$$Lambda$DialogsActivity$Q4f8IfXVbjBGp-OJEwMH2dpG9xI
            r3.<init>(r7, r14)
            r2.setPositiveButton(r1, r3)
            goto L_0x01ff
        L_0x023b:
            r1 = 2131624891(0x7f0e03bb, float:1.8876975E38)
            java.lang.Object[] r3 = new java.lang.Object[r12]
            java.lang.String r4 = "ChatsSelectedClear"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r10)
            r3[r15] = r4
            java.lang.String r4 = "ClearFewChatsTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r4, r1, r3)
            r2.setTitle(r1)
            r1 = 2131624324(0x7f0e0184, float:1.8875824E38)
            java.lang.String r3 = "AreYouSureClearHistoryFewChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r2.setMessage(r1)
            r1 = 2131624892(0x7f0e03bc, float:1.8876977E38)
            java.lang.String r3 = "ClearHistory"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            org.telegram.ui.-$$Lambda$DialogsActivity$9G8EMqqXKxqv5mT2kZ50FISGA_8 r3 = new org.telegram.ui.-$$Lambda$DialogsActivity$9G8EMqqXKxqv5mT2kZ50FISGA_8
            r3.<init>(r7, r14)
            r2.setPositiveButton(r1, r3)
            goto L_0x01ff
        L_0x026f:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r1)
            r1 = 0
            r2.setNegativeButton(r0, r1)
            org.telegram.ui.ActionBar.AlertDialog r0 = r2.create()
            r13.showDialog(r0)
            r4 = -1
            android.view.View r0 = r0.getButton(r4)
            android.widget.TextView r0 = (android.widget.TextView) r0
            if (r0 == 0) goto L_0x0290
            java.lang.String r1 = "dialogTextRed2"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTextColor(r1)
        L_0x0290:
            return
        L_0x0291:
            r4 = -1
            r1 = 106(0x6a, float:1.49E-43)
            if (r14 != r1) goto L_0x02c1
            if (r41 == 0) goto L_0x02c1
            if (r10 != r12) goto L_0x02b2
            java.lang.Object r0 = r7.get(r15)
            java.lang.Long r0 = (java.lang.Long) r0
            long r0 = r0.longValue()
            org.telegram.messenger.MessagesController r2 = r38.getMessagesController()
            int r1 = (int) r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r8 = r2.getUser(r0)
            goto L_0x02b3
        L_0x02b2:
            r8 = 0
        L_0x02b3:
            int r0 = r13.canReportSpamCount
            if (r0 == 0) goto L_0x02b8
            r15 = 1
        L_0x02b8:
            org.telegram.ui.-$$Lambda$DialogsActivity$UupvtsF4qpvz52M_gjvpzQUqRYw r0 = new org.telegram.ui.-$$Lambda$DialogsActivity$UupvtsF4qpvz52M_gjvpzQUqRYw
            r0.<init>(r7)
            org.telegram.ui.Components.AlertsCreator.createBlockDialogAlert(r13, r10, r15, r8, r0)
            return
        L_0x02c1:
            r1 = 2147483647(0x7fffffff, float:NaN)
            if (r9 == 0) goto L_0x02f1
            r2 = 100
            if (r14 == r2) goto L_0x02ce
            r2 = 108(0x6c, float:1.51E-43)
            if (r14 != r2) goto L_0x02f1
        L_0x02ce:
            int r2 = r13.canPinCount
            if (r2 == 0) goto L_0x02f1
            android.util.LongSparseArray<java.lang.Integer> r2 = r9.pinnedDialogs
            int r2 = r2.size()
            r3 = 0
        L_0x02d9:
            if (r3 >= r2) goto L_0x02ee
            android.util.LongSparseArray<java.lang.Integer> r5 = r9.pinnedDialogs
            java.lang.Object r5 = r5.valueAt(r3)
            java.lang.Integer r5 = (java.lang.Integer) r5
            int r5 = r5.intValue()
            int r1 = java.lang.Math.min(r1, r5)
            int r3 = r3 + 1
            goto L_0x02d9
        L_0x02ee:
            int r2 = r13.canPinCount
            int r1 = r1 - r2
        L_0x02f1:
            r11 = r1
            r6 = 0
        L_0x02f3:
            if (r6 >= r10) goto L_0x066a
            java.lang.Object r2 = r7.get(r6)
            java.lang.Long r2 = (java.lang.Long) r2
            long r2 = r2.longValue()
            org.telegram.messenger.MessagesController r5 = r38.getMessagesController()
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r5 = r5.dialogs_dict
            java.lang.Object r5 = r5.get(r2)
            org.telegram.tgnet.TLRPC$Dialog r5 = (org.telegram.tgnet.TLRPC$Dialog) r5
            if (r5 != 0) goto L_0x0320
        L_0x030d:
            r19 = r0
            r18 = r6
            r7 = 108(0x6c, float:1.51E-43)
            r15 = 100
            r17 = 2
        L_0x0317:
            r21 = 2131624638(0x7f0e02be, float:1.8876461E38)
            r22 = -1
            r23 = 103(0x67, float:1.44E-43)
            goto L_0x065f
        L_0x0320:
            int r8 = (int) r2
            r19 = 32
            r24 = r5
            long r4 = r2 >> r19
            int r5 = (int) r4
            if (r8 == 0) goto L_0x034e
            if (r8 <= 0) goto L_0x033c
            org.telegram.messenger.MessagesController r4 = r38.getMessagesController()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r8)
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r5)
            r5 = 0
            r26 = 0
            goto L_0x0374
        L_0x033c:
            org.telegram.messenger.MessagesController r4 = r38.getMessagesController()
            int r5 = -r8
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$Chat r4 = r4.getChat(r5)
            r26 = r4
            r4 = 0
            r5 = 0
            goto L_0x0374
        L_0x034e:
            org.telegram.messenger.MessagesController r4 = r38.getMessagesController()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$EncryptedChat r4 = r4.getEncryptedChat(r5)
            if (r4 == 0) goto L_0x036b
            org.telegram.messenger.MessagesController r5 = r38.getMessagesController()
            int r1 = r4.user_id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r1 = r5.getUser(r1)
            goto L_0x0370
        L_0x036b:
            org.telegram.tgnet.TLRPC$TL_userEmpty r1 = new org.telegram.tgnet.TLRPC$TL_userEmpty
            r1.<init>()
        L_0x0370:
            r5 = r4
            r26 = 0
            r4 = r1
        L_0x0374:
            if (r26 != 0) goto L_0x0379
            if (r4 != 0) goto L_0x0379
            goto L_0x030d
        L_0x0379:
            if (r4 == 0) goto L_0x038a
            boolean r1 = r4.bot
            if (r1 == 0) goto L_0x038a
            boolean r1 = org.telegram.messenger.MessagesController.isSupportUser(r4)
            if (r1 != 0) goto L_0x038a
            r1 = 100
            r27 = 1
            goto L_0x038e
        L_0x038a:
            r1 = 100
            r27 = 0
        L_0x038e:
            if (r14 == r1) goto L_0x05c1
            r15 = 108(0x6c, float:1.51E-43)
            if (r14 != r15) goto L_0x039c
            r1 = r2
            r3 = r24
            r12 = 3
            r17 = 2
            goto L_0x05cb
        L_0x039c:
            r5 = 101(0x65, float:1.42E-43)
            if (r14 != r5) goto L_0x043e
            int r4 = r13.canReadCount
            if (r4 == 0) goto L_0x042f
            if (r9 == 0) goto L_0x03e5
            int r4 = r9.flags
            int r5 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ
            r4 = r4 & r5
            if (r4 == 0) goto L_0x03e5
            int r4 = r13.currentAccount
            r5 = r24
            boolean r4 = r9.alwaysShow(r4, r5)
            if (r4 != 0) goto L_0x03e7
            r13.setDialogsListFrozen(r12)
            r4 = 0
        L_0x03bb:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r8 = r13.frozenDialogsList
            int r8 = r8.size()
            if (r4 >= r8) goto L_0x03d8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r8 = r13.frozenDialogsList
            java.lang.Object r8 = r8.get(r4)
            org.telegram.tgnet.TLRPC$Dialog r8 = (org.telegram.tgnet.TLRPC$Dialog) r8
            long r12 = r8.id
            int r8 = (r12 > r2 ? 1 : (r12 == r2 ? 0 : -1))
            if (r8 != 0) goto L_0x03d2
            goto L_0x03d9
        L_0x03d2:
            int r4 = r4 + 1
            r12 = 1
            r13 = r38
            goto L_0x03bb
        L_0x03d8:
            r4 = -1
        L_0x03d9:
            if (r4 >= 0) goto L_0x03e2
            r8 = 0
            r13 = r38
            r13.setDialogsListFrozen(r8, r8)
            goto L_0x03e8
        L_0x03e2:
            r13 = r38
            goto L_0x03e8
        L_0x03e5:
            r5 = r24
        L_0x03e7:
            r4 = -1
        L_0x03e8:
            org.telegram.messenger.MessagesController r8 = r38.getMessagesController()
            r8.markMentionsAsRead(r2)
            org.telegram.messenger.MessagesController r26 = r38.getMessagesController()
            int r8 = r5.top_message
            int r5 = r5.last_message_date
            r32 = 0
            r33 = 0
            r35 = 0
            r36 = 1
            r37 = 0
            r27 = r2
            r29 = r8
            r30 = r8
            r31 = r5
            r26.markDialogAsRead(r27, r29, r30, r31, r32, r33, r35, r36, r37)
            if (r4 < 0) goto L_0x030d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r2 = r13.frozenDialogsList
            r2.remove(r4)
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r13.viewPages
            r3 = 0
            r2 = r2[r3]
            org.telegram.ui.Components.DialogsItemAnimator r2 = r2.dialogsItemAnimator
            r2.prepareForRemove()
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r13.viewPages
            r2 = r2[r3]
            org.telegram.ui.Adapters.DialogsAdapter r2 = r2.dialogsAdapter
            r2.notifyItemRemoved(r4)
            r2 = 2
            r13.dialogRemoveFinished = r2
            goto L_0x030d
        L_0x042f:
            org.telegram.messenger.MessagesController r26 = r38.getMessagesController()
            r29 = 0
            r30 = 0
            r27 = r2
            r26.markDialogAsUnread(r27, r29, r30)
            goto L_0x030d
        L_0x043e:
            r5 = 102(0x66, float:1.43E-43)
            if (r14 == r5) goto L_0x0493
            r5 = 103(0x67, float:1.44E-43)
            if (r14 != r5) goto L_0x044a
            r1 = 1
            r5 = 3
            r12 = 4
            goto L_0x0496
        L_0x044a:
            r5 = 104(0x68, float:1.46E-43)
            if (r14 != r5) goto L_0x030d
            r4 = 1
            if (r10 != r4) goto L_0x0462
            int r5 = r13.canMuteCount
            if (r5 != r4) goto L_0x0462
            org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.Components.AlertsCreator.createMuteAlert(r13, r2)
            org.telegram.ui.-$$Lambda$DialogsActivity$ZB8AWUpkbuPAOK4bRNdLp1j-uZo r1 = new org.telegram.ui.-$$Lambda$DialogsActivity$ZB8AWUpkbuPAOK4bRNdLp1j-uZo
            r1.<init>()
            r13.showDialog(r0, r1)
            return
        L_0x0462:
            int r4 = r13.canUnmuteCount
            if (r4 == 0) goto L_0x047c
            org.telegram.messenger.MessagesController r4 = r38.getMessagesController()
            boolean r4 = r4.isDialogMuted(r2)
            if (r4 != 0) goto L_0x0472
            goto L_0x030d
        L_0x0472:
            org.telegram.messenger.NotificationsController r4 = r38.getNotificationsController()
            r12 = 4
            r4.setDialogNotificationsSettings(r2, r12)
            goto L_0x030d
        L_0x047c:
            r12 = 4
            org.telegram.messenger.MessagesController r4 = r38.getMessagesController()
            boolean r4 = r4.isDialogMuted(r2)
            if (r4 == 0) goto L_0x0489
            goto L_0x030d
        L_0x0489:
            org.telegram.messenger.NotificationsController r4 = r38.getNotificationsController()
            r5 = 3
            r4.setDialogNotificationsSettings(r2, r5)
            goto L_0x030d
        L_0x0493:
            r5 = 3
            r12 = 4
            r1 = 1
        L_0x0496:
            if (r10 != r1) goto L_0x051b
            r1 = 102(0x66, float:1.43E-43)
            if (r14 != r1) goto L_0x04e5
            boolean r1 = r13.canDeletePsaSelected
            if (r1 == 0) goto L_0x04e5
            org.telegram.ui.ActionBar.AlertDialog$Builder r1 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r2 = r38.getParentActivity()
            r1.<init>((android.content.Context) r2)
            r2 = 2131627019(0x7f0e0c0b, float:1.888129E38)
            java.lang.String r3 = "PsaHideChatAlertTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setTitle(r2)
            r2 = 2131627018(0x7f0e0c0a, float:1.8881289E38)
            java.lang.String r3 = "PsaHideChatAlertText"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setMessage(r2)
            r2 = 2131627017(0x7f0e0CLASSNAME, float:1.8881287E38)
            java.lang.String r3 = "PsaHide"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.-$$Lambda$DialogsActivity$7kClodAM7xplpYvcvZbezXcnudE r3 = new org.telegram.ui.-$$Lambda$DialogsActivity$7kClodAM7xplpYvcvZbezXcnudE
            r3.<init>()
            r1.setPositiveButton(r2, r3)
            r3 = 2131624638(0x7f0e02be, float:1.8876461E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r3)
            r2 = 0
            r1.setNegativeButton(r0, r2)
            org.telegram.ui.ActionBar.AlertDialog r0 = r1.create()
            r13.showDialog(r0)
            goto L_0x051a
        L_0x04e5:
            r0 = 103(0x67, float:1.44E-43)
            if (r14 != r0) goto L_0x04eb
            r7 = 1
            goto L_0x04ec
        L_0x04eb:
            r7 = 0
        L_0x04ec:
            r1 = 102(0x66, float:1.43E-43)
            if (r8 != 0) goto L_0x04f2
            r8 = 1
            goto L_0x04f3
        L_0x04f2:
            r8 = 0
        L_0x04f3:
            if (r14 != r1) goto L_0x04f8
            r20 = 1
            goto L_0x04fa
        L_0x04f8:
            r20 = 0
        L_0x04fa:
            org.telegram.ui.-$$Lambda$DialogsActivity$A_sA0CqHJs1cp856RoU7FxbeJDQ r9 = new org.telegram.ui.-$$Lambda$DialogsActivity$A_sA0CqHJs1cp856RoU7FxbeJDQ
            r0 = r9
            r1 = r38
            r5 = r2
            r2 = r40
            r3 = r26
            r25 = r4
            r4 = r5
            r6 = r27
            r0.<init>(r2, r3, r4, r6)
            r0 = r38
            r1 = r7
            r2 = r26
            r3 = r25
            r4 = r8
            r5 = r20
            r6 = r9
            org.telegram.ui.Components.AlertsCreator.createClearOrDeleteDialogAlert(r0, r1, r2, r3, r4, r5, r6)
        L_0x051a:
            return
        L_0x051b:
            r1 = r2
            r3 = 2131624638(0x7f0e02be, float:1.8876461E38)
            org.telegram.messenger.MessagesController r4 = r38.getMessagesController()
            r3 = 1
            boolean r4 = r4.isPromoDialog(r1, r3)
            if (r4 == 0) goto L_0x0533
            org.telegram.messenger.MessagesController r1 = r38.getMessagesController()
            r1.hidePromoDialog()
            goto L_0x030d
        L_0x0533:
            r4 = 103(0x67, float:1.44E-43)
            if (r14 != r4) goto L_0x0546
            int r3 = r13.canClearCacheCount
            if (r3 == 0) goto L_0x0546
            org.telegram.messenger.MessagesController r3 = r38.getMessagesController()
            r5 = 0
            r8 = 2
            r3.deleteDialog(r1, r8, r5)
            goto L_0x030d
        L_0x0546:
            r5 = 0
            r17 = 2
            if (r14 != r4) goto L_0x0555
            org.telegram.messenger.MessagesController r3 = r38.getMessagesController()
            r8 = 1
            r3.deleteDialog(r1, r8, r5)
            goto L_0x05d5
        L_0x0555:
            if (r26 == 0) goto L_0x0584
            boolean r3 = org.telegram.messenger.ChatObject.isNotInChat(r26)
            if (r3 == 0) goto L_0x0565
            org.telegram.messenger.MessagesController r3 = r38.getMessagesController()
            r3.deleteDialog(r1, r5, r5)
            goto L_0x0595
        L_0x0565:
            org.telegram.messenger.MessagesController r3 = r38.getMessagesController()
            org.telegram.messenger.UserConfig r5 = r38.getUserConfig()
            int r5 = r5.getClientUserId()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r5)
            org.telegram.messenger.MessagesController r5 = r38.getMessagesController()
            long r12 = -r1
            int r8 = (int) r12
            r12 = 0
            r5.deleteUserFromChat(r8, r3, r12)
            goto L_0x0595
        L_0x0584:
            org.telegram.messenger.MessagesController r3 = r38.getMessagesController()
            r5 = 0
            r3.deleteDialog(r1, r5, r5)
            if (r27 == 0) goto L_0x0595
            org.telegram.messenger.MessagesController r3 = r38.getMessagesController()
            r3.blockPeer(r8)
        L_0x0595:
            boolean r3 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r3 == 0) goto L_0x05ae
            org.telegram.messenger.NotificationCenter r3 = r38.getNotificationCenter()
            int r5 = org.telegram.messenger.NotificationCenter.closeChats
            r8 = 1
            java.lang.Object[] r12 = new java.lang.Object[r8]
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            r2 = 0
            r12[r2] = r1
            r3.postNotificationName(r5, r12)
        L_0x05ae:
            r19 = r0
            r18 = r6
            r7 = 108(0x6c, float:1.51E-43)
            r15 = 100
            r21 = 2131624638(0x7f0e02be, float:1.8876461E38)
            r22 = -1
            r23 = 103(0x67, float:1.44E-43)
            r13 = r38
            goto L_0x065f
        L_0x05c1:
            r1 = r2
            r3 = r24
            r12 = 3
            r15 = 108(0x6c, float:1.51E-43)
            r17 = 2
            r13 = r38
        L_0x05cb:
            int r4 = r13.canPinCount
            if (r4 == 0) goto L_0x063b
            boolean r3 = r13.isDialogPinned(r3)
            if (r3 == 0) goto L_0x05df
        L_0x05d5:
            r19 = r0
            r18 = r6
            r7 = 108(0x6c, float:1.51E-43)
            r15 = 100
            goto L_0x0317
        L_0x05df:
            r3 = 1
            r4 = 1
            r19 = r0
            if (r10 != r4) goto L_0x05e8
            r16 = 1
            goto L_0x05ea
        L_0x05e8:
            r16 = 0
        L_0x05ea:
            r0 = r38
            r4 = 100
            r24 = 102(0x66, float:1.43E-43)
            r21 = 2131624638(0x7f0e02be, float:1.8876461E38)
            r15 = 100
            r22 = -1
            r23 = 103(0x67, float:1.44E-43)
            r4 = r9
            r12 = r5
            r7 = 108(0x6c, float:1.51E-43)
            r5 = r11
            r18 = r6
            r6 = r16
            r0.pinDialog(r1, r3, r4, r5, r6)
            if (r9 == 0) goto L_0x065f
            int r11 = r11 + 1
            if (r12 == 0) goto L_0x0625
            java.util.ArrayList<java.lang.Integer> r0 = r9.alwaysShow
            int r1 = r12.user_id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            boolean r0 = r0.contains(r1)
            if (r0 != 0) goto L_0x065f
            java.util.ArrayList<java.lang.Integer> r0 = r9.alwaysShow
            int r1 = r12.user_id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r0.add(r1)
            goto L_0x065f
        L_0x0625:
            java.util.ArrayList<java.lang.Integer> r0 = r9.alwaysShow
            java.lang.Integer r1 = java.lang.Integer.valueOf(r8)
            boolean r0 = r0.contains(r1)
            if (r0 != 0) goto L_0x065f
            java.util.ArrayList<java.lang.Integer> r0 = r9.alwaysShow
            java.lang.Integer r1 = java.lang.Integer.valueOf(r8)
            r0.add(r1)
            goto L_0x065f
        L_0x063b:
            r19 = r0
            r18 = r6
            r7 = 108(0x6c, float:1.51E-43)
            r15 = 100
            r21 = 2131624638(0x7f0e02be, float:1.8876461E38)
            r22 = -1
            r23 = 103(0x67, float:1.44E-43)
            boolean r0 = r13.isDialogPinned(r3)
            if (r0 != 0) goto L_0x0651
            goto L_0x065f
        L_0x0651:
            r3 = 0
            r0 = 1
            if (r10 != r0) goto L_0x0657
            r6 = 1
            goto L_0x0658
        L_0x0657:
            r6 = 0
        L_0x0658:
            r0 = r38
            r4 = r9
            r5 = r11
            r0.pinDialog(r1, r3, r4, r5, r6)
        L_0x065f:
            int r6 = r18 + 1
            r7 = r39
            r0 = r19
            r4 = -1
            r12 = 1
            r15 = 0
            goto L_0x02f3
        L_0x066a:
            r0 = 104(0x68, float:1.46E-43)
            r7 = 108(0x6c, float:1.51E-43)
            r15 = 100
            r12 = 1
            if (r14 != r0) goto L_0x0687
            if (r10 != r12) goto L_0x0679
            int r0 = r13.canMuteCount
            if (r0 == r12) goto L_0x0687
        L_0x0679:
            int r0 = r13.canUnmuteCount
            if (r0 != 0) goto L_0x067f
            r0 = 1
            goto L_0x0680
        L_0x067f:
            r0 = 0
        L_0x0680:
            org.telegram.ui.Components.Bulletin r0 = org.telegram.ui.Components.BulletinFactory.createMuteBulletin((org.telegram.ui.ActionBar.BaseFragment) r13, (boolean) r0)
            r0.show()
        L_0x0687:
            if (r14 == r15) goto L_0x068f
            if (r14 != r7) goto L_0x068c
            goto L_0x068f
        L_0x068c:
            r15 = 108(0x6c, float:1.51E-43)
            goto L_0x06c7
        L_0x068f:
            if (r9 == 0) goto L_0x06b9
            int r1 = r9.flags
            java.lang.String r2 = r9.name
            java.util.ArrayList<java.lang.Integer> r3 = r9.alwaysShow
            java.util.ArrayList<java.lang.Integer> r4 = r9.neverShow
            android.util.LongSparseArray<java.lang.Integer> r5 = r9.pinnedDialogs
            r6 = 0
            r8 = 0
            r10 = 1
            r11 = 1
            r16 = 0
            r17 = 0
            r0 = r9
            r9 = 108(0x6c, float:1.51E-43)
            r7 = r8
            r8 = r10
            r10 = 108(0x6c, float:1.51E-43)
            r9 = r11
            r11 = 108(0x6c, float:1.51E-43)
            r10 = r16
            r15 = 108(0x6c, float:1.51E-43)
            r11 = r38
            r12 = r17
            org.telegram.ui.FilterCreateActivity.saveFilterToServer(r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            goto L_0x06c7
        L_0x06b9:
            r15 = 108(0x6c, float:1.51E-43)
            org.telegram.messenger.MessagesController r0 = r38.getMessagesController()
            int r1 = r13.folderId
            r2 = 0
            r4 = 0
            r0.reorderPinnedDialogs(r1, r4, r2)
        L_0x06c7:
            if (r14 == r15) goto L_0x06d3
            r0 = 100
            if (r14 == r0) goto L_0x06d3
            r0 = 102(0x66, float:1.43E-43)
            if (r14 == r0) goto L_0x06d3
            r15 = 1
            goto L_0x06d4
        L_0x06d3:
            r15 = 0
        L_0x06d4:
            r13.hideActionMode(r15)
            return
        L_0x06d8:
            r17 = 2
            java.util.ArrayList r7 = new java.util.ArrayList
            r0 = r39
            r7.<init>(r0)
            org.telegram.messenger.MessagesController r0 = r38.getMessagesController()
            int r1 = r13.canUnarchiveCount
            if (r1 != 0) goto L_0x06eb
            r2 = 1
            goto L_0x06ec
        L_0x06eb:
            r2 = 0
        L_0x06ec:
            r3 = -1
            r4 = 0
            r5 = 0
            r1 = r7
            r0.addDialogToFolder(r1, r2, r3, r4, r5)
            int r0 = r13.canUnarchiveCount
            if (r0 != 0) goto L_0x0744
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings()
            java.lang.String r1 = "archivehint_l"
            r2 = 0
            boolean r3 = r0.getBoolean(r1, r2)
            if (r3 != 0) goto L_0x070c
            boolean r2 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r2 == 0) goto L_0x070a
            goto L_0x070c
        L_0x070a:
            r12 = 0
            goto L_0x070d
        L_0x070c:
            r12 = 1
        L_0x070d:
            if (r12 != 0) goto L_0x071c
            android.content.SharedPreferences$Editor r0 = r0.edit()
            r2 = 1
            android.content.SharedPreferences$Editor r0 = r0.putBoolean(r1, r2)
            r0.commit()
            goto L_0x071d
        L_0x071c:
            r2 = 1
        L_0x071d:
            if (r12 == 0) goto L_0x072a
            int r0 = r7.size()
            if (r0 <= r2) goto L_0x0727
            r6 = 4
            goto L_0x0728
        L_0x0727:
            r6 = 2
        L_0x0728:
            r3 = r6
            goto L_0x0734
        L_0x072a:
            int r0 = r7.size()
            if (r0 <= r2) goto L_0x0732
            r11 = 5
            goto L_0x0733
        L_0x0732:
            r11 = 3
        L_0x0733:
            r3 = r11
        L_0x0734:
            org.telegram.ui.Components.UndoView r0 = r38.getUndoView()
            r1 = 0
            r4 = 0
            org.telegram.ui.-$$Lambda$DialogsActivity$qqy7snf7UwUqOC0du2S0FkqX1tc r5 = new org.telegram.ui.-$$Lambda$DialogsActivity$qqy7snf7UwUqOC0du2S0FkqX1tc
            r5.<init>(r7)
            r0.showWithAction(r1, r3, r4, r5)
            goto L_0x0775
        L_0x0744:
            org.telegram.messenger.MessagesController r0 = r38.getMessagesController()
            int r1 = r13.folderId
            java.util.ArrayList r0 = r0.getDialogs(r1)
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r13.viewPages
            if (r1 == 0) goto L_0x0775
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0775
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r13.viewPages
            r1 = 0
            r0 = r0[r1]
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r0.listView
            r2 = 0
            r0.setEmptyView(r2)
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r13.viewPages
            r0 = r0[r1]
            org.telegram.ui.Components.FlickerLoadingView r0 = r0.progressView
            r2 = 4
            r0.setVisibility(r2)
            r38.finishFragment()
            goto L_0x0776
        L_0x0775:
            r1 = 0
        L_0x0776:
            r13.hideActionMode(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.perfromSelectedDialogsAction(java.util.ArrayList, int, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$perfromSelectedDialogsAction$20 */
    public /* synthetic */ void lambda$perfromSelectedDialogsAction$20$DialogsActivity(ArrayList arrayList) {
        getMessagesController().addDialogToFolder(arrayList, this.folderId == 0 ? 0 : 1, -1, (ArrayList<TLRPC$TL_inputFolderPeer>) null, 0);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$perfromSelectedDialogsAction$21 */
    public /* synthetic */ void lambda$perfromSelectedDialogsAction$21$DialogsActivity(DialogInterface dialogInterface, int i) {
        presentFragment(new FiltersSetupActivity());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$perfromSelectedDialogsAction$22 */
    public /* synthetic */ void lambda$perfromSelectedDialogsAction$22$DialogsActivity(ArrayList arrayList, int i, DialogInterface dialogInterface, int i2) {
        getMessagesController().setDialogsInTransaction(true);
        perfromSelectedDialogsAction(arrayList, i, false);
        getMessagesController().setDialogsInTransaction(false);
        getMessagesController().checkIfFolderEmpty(this.folderId);
        if (this.folderId != 0 && getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, false).size() == 0) {
            this.viewPages[0].listView.setEmptyView((View) null);
            this.viewPages[0].progressView.setVisibility(4);
            finishFragment();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$perfromSelectedDialogsAction$23 */
    public /* synthetic */ void lambda$perfromSelectedDialogsAction$23$DialogsActivity(ArrayList arrayList, int i, DialogInterface dialogInterface, int i2) {
        perfromSelectedDialogsAction(arrayList, i, false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$perfromSelectedDialogsAction$24 */
    public /* synthetic */ void lambda$perfromSelectedDialogsAction$24$DialogsActivity(ArrayList arrayList, int i, DialogInterface dialogInterface, int i2) {
        perfromSelectedDialogsAction(arrayList, i, false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$perfromSelectedDialogsAction$25 */
    public /* synthetic */ void lambda$perfromSelectedDialogsAction$25$DialogsActivity(ArrayList arrayList, boolean z, boolean z2) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            ArrayList arrayList2 = arrayList;
            long longValue = ((Long) arrayList.get(i)).longValue();
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
    /* renamed from: lambda$perfromSelectedDialogsAction$26 */
    public /* synthetic */ void lambda$perfromSelectedDialogsAction$26$DialogsActivity(DialogInterface dialogInterface, int i) {
        getMessagesController().hidePromoDialog();
        hideActionMode(false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$perfromSelectedDialogsAction$28 */
    public /* synthetic */ void lambda$perfromSelectedDialogsAction$28$DialogsActivity(int i, TLRPC$Chat tLRPC$Chat, long j, boolean z, boolean z2) {
        int i2;
        int i3 = i;
        TLRPC$Chat tLRPC$Chat2 = tLRPC$Chat;
        long j2 = j;
        hideActionMode(false);
        if (i3 != 103 || !ChatObject.isChannel(tLRPC$Chat) || (tLRPC$Chat2.megagroup && TextUtils.isEmpty(tLRPC$Chat2.username))) {
            boolean z3 = z2;
            if (i3 == 102 && this.folderId != 0 && getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, false).size() == 1) {
                this.viewPages[0].progressView.setVisibility(4);
            }
            int i4 = -1;
            if (i3 == 102) {
                setDialogsListFrozen(true);
                int i5 = 0;
                while (true) {
                    if (i5 >= this.frozenDialogsList.size()) {
                        break;
                    } else if (this.frozenDialogsList.get(i5).id == j2) {
                        i2 = i5;
                        break;
                    } else {
                        i5++;
                    }
                }
            }
            i2 = -1;
            UndoView undoView2 = getUndoView();
            int i6 = i3 == 103 ? 0 : 1;
            $$Lambda$DialogsActivity$NhjayaBpBDhSPbDjvqJHKORzXQg r15 = r0;
            int i7 = i2;
            $$Lambda$DialogsActivity$NhjayaBpBDhSPbDjvqJHKORzXQg r0 = new Runnable(i, j, z2, tLRPC$Chat, z) {
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
                    DialogsActivity.this.lambda$null$27$DialogsActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                }
            };
            undoView2.showWithAction(j2, i6, (Runnable) r15);
            ArrayList arrayList = new ArrayList(getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, false));
            int i8 = 0;
            while (true) {
                if (i8 >= arrayList.size()) {
                    break;
                } else if (((TLRPC$Dialog) arrayList.get(i8)).id == j2) {
                    i4 = i8;
                    break;
                } else {
                    i8++;
                }
            }
            if (i3 == 102) {
                int i9 = i7;
                if (i9 < 0 || i4 >= 0) {
                    setDialogsListFrozen(true, false);
                    return;
                }
                this.frozenDialogsList.remove(i9);
                this.viewPages[0].dialogsItemAnimator.prepareForRemove();
                this.viewPages[0].dialogsAdapter.notifyItemRemoved(i9);
                this.dialogRemoveFinished = 2;
                return;
            }
            return;
        }
        getMessagesController().deleteDialog(j2, 2, z2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$27 */
    public /* synthetic */ void lambda$null$27$DialogsActivity(int i, long j, boolean z, TLRPC$Chat tLRPC$Chat, boolean z2) {
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
            getMessagesController().deleteUserFromChat((int) (-j), getMessagesController().getUser(Integer.valueOf(getUserConfig().getClientUserId())), (TLRPC$ChatFull) null, z, false);
        }
        if (AndroidUtilities.isTablet()) {
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, Long.valueOf(j));
        }
        getMessagesController().checkIfFolderEmpty(this.folderId);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$perfromSelectedDialogsAction$29 */
    public /* synthetic */ void lambda$perfromSelectedDialogsAction$29$DialogsActivity(DialogInterface dialogInterface) {
        hideActionMode(true);
    }

    private void pinDialog(long j, boolean z, MessagesController.DialogFilter dialogFilter, int i, boolean z2) {
        boolean z3;
        int i2;
        boolean z4;
        boolean z5;
        long j2 = j;
        MessagesController.DialogFilter dialogFilter2 = dialogFilter;
        boolean z6 = true;
        int i3 = (this.viewPages[0].dialogsType != 0 || !hasHiddenArchive()) ? 0 : 1;
        int findFirstVisibleItemPosition = this.viewPages[0].layoutManager.findFirstVisibleItemPosition();
        if (dialogFilter2 != null) {
            int intValue = dialogFilter2.pinnedDialogs.get(j2, Integer.MIN_VALUE).intValue();
            if (!z && intValue == Integer.MIN_VALUE) {
                return;
            }
        }
        int i4 = -1;
        if (findFirstVisibleItemPosition > i3 || !z2) {
            i2 = -1;
            z3 = true;
        } else {
            setDialogsListFrozen(true);
            int i5 = 0;
            while (true) {
                if (i5 >= this.frozenDialogsList.size()) {
                    i2 = -1;
                    break;
                } else if (this.frozenDialogsList.get(i5).id == j2) {
                    i2 = i5;
                    break;
                } else {
                    i5++;
                }
            }
            z3 = false;
        }
        if (dialogFilter2 != null) {
            if (z) {
                dialogFilter2.pinnedDialogs.put(j2, Integer.valueOf(i));
            } else {
                dialogFilter2.pinnedDialogs.remove(j2);
            }
            if (z2) {
                getMessagesController().onFilterUpdate(dialogFilter2);
            }
            z4 = true;
        } else {
            z4 = getMessagesController().pinDialog(j, z, (TLRPC$InputPeer) null, -1);
        }
        if (z4) {
            if (z3) {
                if (this.initialDialogsType != 10) {
                    hideFloatingButton(false);
                }
                scrollToTop();
            } else {
                ArrayList<TLRPC$Dialog> dialogsArray = getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, false);
                int i6 = 0;
                while (true) {
                    if (i6 >= dialogsArray.size()) {
                        break;
                    } else if (dialogsArray.get(i6).id == j2) {
                        i4 = i6;
                        break;
                    } else {
                        i6++;
                    }
                }
            }
        }
        if (!z3) {
            if (i2 >= 0) {
                ArrayList<TLRPC$Dialog> arrayList = this.frozenDialogsList;
                if (arrayList == null || i4 < 0 || i2 == i4) {
                    z5 = false;
                } else {
                    arrayList.add(i4, arrayList.remove(i2));
                    this.viewPages[0].dialogsItemAnimator.prepareForRemove();
                    this.viewPages[0].dialogsAdapter.notifyItemRemoved(i2);
                    this.viewPages[0].dialogsAdapter.notifyItemInserted(i4);
                    this.dialogRemoveFinished = 2;
                    this.dialogInsertFinished = 2;
                    this.viewPages[0].layoutManager.scrollToPositionWithOffset((this.viewPages[0].dialogsType != 0 || !hasHiddenArchive()) ? 0 : 1, (int) this.actionBar.getTranslationY());
                    z5 = true;
                }
                if (i4 < 0 || i2 != i4) {
                    z6 = z5;
                } else {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public final void run() {
                            DialogsActivity.this.lambda$pinDialog$30$DialogsActivity();
                        }
                    }, 200);
                }
            } else {
                z6 = false;
            }
            if (!z6) {
                setDialogsListFrozen(false, false);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$pinDialog$30 */
    public /* synthetic */ void lambda$pinDialog$30$DialogsActivity() {
        setDialogsListFrozen(false, true);
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
            this.actionBar.setActionModeOverrideColor(Theme.getColor("windowBackgroundWhite"));
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
                    DialogsActivity.this.lambda$showOrUpdateActionMode$31$DialogsActivity(valueAnimator);
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
    /* renamed from: lambda$showOrUpdateActionMode$31 */
    public /* synthetic */ void lambda$showOrUpdateActionMode$31$DialogsActivity(ValueAnimator valueAnimator) {
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
        } else if (this.initialDialogsType == 10) {
            hideFloatingButton(this.selectedDialogs.isEmpty());
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
                            DialogsActivity.this.lambda$askForPermissons$32$DialogsActivity(i);
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
    /* renamed from: lambda$askForPermissons$32 */
    public /* synthetic */ void lambda$askForPermissons$32$DialogsActivity(int i) {
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
        int i3;
        int i4;
        int i5 = 0;
        if (i == NotificationCenter.dialogsNeedReload) {
            if (this.viewPages != null && !this.dialogsListFrozen) {
                AccountInstance.getInstance(this.currentAccount).getMessagesController().getDialogs(this.folderId);
                int i6 = 0;
                while (true) {
                    ViewPage[] viewPageArr = this.viewPages;
                    if (i6 >= viewPageArr.length) {
                        break;
                    }
                    if (viewPageArr[i6].getVisibility() == 0) {
                        int currentCount = this.viewPages[i6].dialogsAdapter.getCurrentCount();
                        if (this.viewPages[i6].dialogsType == 0 && hasHiddenArchive() && this.viewPages[i6].listView.getChildCount() == 0) {
                            ((LinearLayoutManager) this.viewPages[i6].listView.getLayoutManager()).scrollToPositionWithOffset(1, 0);
                        }
                        if (this.viewPages[i6].dialogsAdapter.isDataSetChanged() || objArr.length > 0) {
                            this.viewPages[i6].dialogsAdapter.notifyDataSetChanged();
                            if (!(this.viewPages[i6].dialogsAdapter.getItemCount() <= currentCount || (i3 = this.initialDialogsType) == 11 || i3 == 12 || i3 == 13)) {
                                this.viewPages[i6].recyclerItemsEnterAnimator.showItemsAnimated(currentCount);
                            }
                        } else {
                            updateVisibleRows(2048);
                            if (!(this.viewPages[i6].dialogsAdapter.getItemCount() <= currentCount || (i4 = this.initialDialogsType) == 11 || i4 == 12 || i4 == 13)) {
                                this.viewPages[i6].recyclerItemsEnterAnimator.showItemsAnimated(currentCount);
                            }
                        }
                        try {
                            this.viewPages[i6].listView.setEmptyView(this.folderId == 0 ? this.viewPages[i6].progressView : null);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                        checkListLoad(this.viewPages[i6]);
                    }
                    i6++;
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
                while (i5 < this.viewPages.length) {
                    if ((num.intValue() & 4) != 0) {
                        this.viewPages[i5].dialogsAdapter.sortOnlineContacts(true);
                    }
                    i5++;
                }
            }
        } else if (i == NotificationCenter.appDidLogout) {
            dialogsLoaded[this.currentAccount] = false;
        } else if (i == NotificationCenter.encryptedChatUpdated) {
            updateVisibleRows(0);
        } else if (i == NotificationCenter.contactsDidLoad) {
            if (this.viewPages != null && !this.dialogsListFrozen) {
                int i7 = 0;
                boolean z = false;
                while (true) {
                    ViewPage[] viewPageArr2 = this.viewPages;
                    if (i7 >= viewPageArr2.length) {
                        break;
                    }
                    if (!viewPageArr2[i7].isDefaultDialogType() || getMessagesController().getDialogs(this.folderId).size() > 10) {
                        z = true;
                    } else {
                        this.viewPages[i7].dialogsAdapter.notifyDataSetChanged();
                    }
                    i7++;
                }
                if (z) {
                    updateVisibleRows(0);
                }
            }
        } else if (i == NotificationCenter.openedChatChanged) {
            if (this.viewPages != null) {
                int i8 = 0;
                while (true) {
                    ViewPage[] viewPageArr3 = this.viewPages;
                    if (i8 < viewPageArr3.length) {
                        if (viewPageArr3[i8].isDefaultDialogType() && AndroidUtilities.isTablet()) {
                            boolean booleanValue = objArr[1].booleanValue();
                            long longValue = objArr[0].longValue();
                            if (!booleanValue) {
                                this.openedDialogId = longValue;
                            } else if (longValue == this.openedDialogId) {
                                this.openedDialogId = 0;
                            }
                            this.viewPages[i8].dialogsAdapter.setOpenedDialogId(this.openedDialogId);
                        }
                        i8++;
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
            updatePasscodeButton(true);
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
                $$Lambda$DialogsActivity$52c0GLLCogKjOD19Lda8tHbfc3M r3 = new Runnable(objArr[2], longValue2, objArr[3].booleanValue(), objArr[1]) {
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
                        DialogsActivity.this.lambda$didReceivedNotification$33$DialogsActivity(this.f$1, this.f$2, this.f$3, this.f$4);
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
            int i9 = this.folderId;
            if (i9 == intValue && i9 != 0) {
                finishFragment();
            }
        } else if (i == NotificationCenter.dialogFiltersUpdated) {
            updateFilterTabs(true, true);
        } else if (i == NotificationCenter.filterSettingsUpdated) {
            showFiltersHint();
        } else if (i == NotificationCenter.newSuggestionsAvailable) {
            showNextSupportedSuggestion();
        } else if (i == NotificationCenter.messagesDeleted) {
            if (this.searchIsShowed && this.searchViewPager != null) {
                this.searchViewPager.messagesDeleted(objArr[1].intValue(), objArr[0]);
            }
        } else if (i == NotificationCenter.didClearDatabase && this.viewPages != null) {
            while (true) {
                ViewPage[] viewPageArr4 = this.viewPages;
                if (i5 < viewPageArr4.length) {
                    viewPageArr4[i5].dialogsAdapter.didDatabaseCleared();
                    i5++;
                } else {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$33 */
    public /* synthetic */ void lambda$didReceivedNotification$33$DialogsActivity(TLRPC$Chat tLRPC$Chat, long j, boolean z, TLRPC$User tLRPC$User) {
        if (tLRPC$Chat == null) {
            getMessagesController().deleteDialog(j, 0, z);
            if (tLRPC$User != null && tLRPC$User.bot) {
                getMessagesController().blockPeer(tLRPC$User.id);
            }
        } else if (ChatObject.isNotInChat(tLRPC$Chat)) {
            getMessagesController().deleteDialog(j, 0, z);
        } else {
            getMessagesController().deleteUserFromChat((int) (-j), getMessagesController().getUser(Integer.valueOf(getUserConfig().getClientUserId())), (TLRPC$ChatFull) null, z, z);
        }
        getMessagesController().checkIfFolderEmpty(this.folderId);
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
            getMessagesController().removeSuggestion(0, this.showingSuggestion);
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
                DialogsActivity.this.lambda$showSuggestion$34$DialogsActivity(dialogInterface, i);
            }
        });
        showDialog(builder.create(), new DialogInterface.OnDismissListener() {
            public final void onDismiss(DialogInterface dialogInterface) {
                DialogsActivity.this.lambda$showSuggestion$35$DialogsActivity(dialogInterface);
            }
        });
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showSuggestion$34 */
    public /* synthetic */ void lambda$showSuggestion$34$DialogsActivity(DialogInterface dialogInterface, int i) {
        presentFragment(new PrivacySettingsActivity());
        AndroidUtilities.scrollToFragmentRow(this.parentLayout, "newChatsRow");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showSuggestion$35 */
    public /* synthetic */ void lambda$showSuggestion$35$DialogsActivity(DialogInterface dialogInterface) {
        onSuggestionDismiss();
    }

    private void showFiltersHint() {
        if (!this.askingForPermissions && getMessagesController().dialogFiltersLoaded && getMessagesController().showFiltersTooltip && this.filterTabsView != null && getMessagesController().dialogFilters.isEmpty() && !this.isPaused && getUserConfig().filtersLoaded && !this.inPreviewMode) {
            SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
            if (!globalMainSettings.getBoolean("filterhint", false)) {
                globalMainSettings.edit().putBoolean("filterhint", true).commit();
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        DialogsActivity.this.lambda$showFiltersHint$37$DialogsActivity();
                    }
                }, 1000);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$36 */
    public /* synthetic */ void lambda$null$36$DialogsActivity() {
        presentFragment(new FiltersSetupActivity());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showFiltersHint$37 */
    public /* synthetic */ void lambda$showFiltersHint$37$DialogsActivity() {
        getUndoView().showWithAction(0, 15, (Runnable) null, new Runnable() {
            public final void run() {
                DialogsActivity.this.lambda$null$36$DialogsActivity();
            }
        });
    }

    /* access modifiers changed from: private */
    public void setDialogsListFrozen(boolean z, boolean z2) {
        if (this.viewPages != null && this.dialogsListFrozen != z) {
            if (z) {
                this.frozenDialogsList = new ArrayList<>(getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, false));
            } else {
                this.frozenDialogsList = null;
            }
            this.dialogsListFrozen = z;
            this.viewPages[0].dialogsAdapter.setDialogsListFrozen(z);
            if (!z && z2) {
                this.viewPages[0].dialogsAdapter.notifyDataSetChanged();
            }
        }
    }

    /* access modifiers changed from: private */
    public void setDialogsListFrozen(boolean z) {
        setDialogsListFrozen(z, true);
    }

    public ArrayList<TLRPC$Dialog> getDialogsArray(int i, int i2, int i3, boolean z) {
        ArrayList<TLRPC$Dialog> arrayList;
        if (z && (arrayList = this.frozenDialogsList) != null) {
            return arrayList;
        }
        MessagesController messagesController = AccountInstance.getInstance(i).getMessagesController();
        if (i2 == 0) {
            return messagesController.getDialogs(i3);
        }
        char c = 1;
        if (i2 == 1 || i2 == 10 || i2 == 13) {
            return messagesController.dialogsServerOnly;
        }
        if (i2 == 2) {
            return messagesController.dialogsCanAddUsers;
        }
        if (i2 == 3) {
            return messagesController.dialogsForward;
        }
        if (i2 == 4 || i2 == 12) {
            return messagesController.dialogsUsersOnly;
        }
        if (i2 == 5) {
            return messagesController.dialogsChannelsOnly;
        }
        if (i2 == 6 || i2 == 11) {
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
            return new ArrayList<>();
        }
    }

    public void setSideMenu(RecyclerView recyclerView) {
        this.sideMenu = recyclerView;
        recyclerView.setBackgroundColor(Theme.getColor("chats_menuBackground"));
        this.sideMenu.setGlowColor(Theme.getColor("chats_menuBackground"));
    }

    /* access modifiers changed from: private */
    public void updatePasscodeButton(boolean z) {
        if (this.passcodeItem != null) {
            if (this.isPaused) {
                z = false;
            }
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
                this.passcodeItem.setContentDescription(LocaleController.getString("AccDescrPasscodeUnlock", NUM));
                this.passcodeItem.setIcon((Drawable) this.passcodeDrawable2);
                if (z) {
                    this.passcodeDrawable2.setCurrentFrame(0, false);
                    this.passcodeItem.getIconView().playAnimation();
                    return;
                }
                this.passcodeDrawable2.setCurrentFrame(38, false);
                return;
            }
            this.passcodeItem.setContentDescription(LocaleController.getString("AccDescrPasscodeLock", NUM));
            this.passcodeItem.setIcon((Drawable) this.passcodeDrawable);
            if (z) {
                this.passcodeDrawable.setCurrentFrame(0, false);
                this.passcodeItem.getIconView().playAnimation();
                return;
            }
            this.passcodeDrawable.setCurrentFrame(31, false);
        }
    }

    /* access modifiers changed from: private */
    public void hideFloatingButton(boolean z) {
        if (this.floatingHidden == z) {
            return;
        }
        if (!z || !this.floatingForceVisible) {
            this.floatingHidden = z;
            AnimatorSet animatorSet = new AnimatorSet();
            float[] fArr = new float[2];
            fArr[0] = this.floatingButtonHideProgress;
            fArr[1] = this.floatingHidden ? 1.0f : 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    DialogsActivity.this.lambda$hideFloatingButton$38$DialogsActivity(valueAnimator);
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
    /* renamed from: lambda$hideFloatingButton$38 */
    public /* synthetic */ void lambda$hideFloatingButton$38$DialogsActivity(ValueAnimator valueAnimator) {
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
        if (!this.dialogsListFrozen || (i & 131072) != 0) {
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
                        if ((childAt instanceof DialogCell) && recyclerView.getAdapter() != this.searchViewPager.dialogsSearchAdapter) {
                            DialogCell dialogCell = (DialogCell) childAt;
                            boolean z = true;
                            if ((i & 131072) != 0) {
                                dialogCell.onReorderStateChanged(this.actionBar.isActionModeShowed(), true);
                                if (this.dialogsListFrozen) {
                                }
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
                        if (!this.dialogsListFrozen) {
                            if (childAt instanceof UserCell) {
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

    public void setInitialSearchType(int i) {
        this.initialSearchType = i;
    }

    /* access modifiers changed from: private */
    public void didSelectResult(long j, boolean z, boolean z2) {
        TLRPC$Chat tLRPC$Chat;
        String str;
        String str2;
        String str3;
        long j2 = j;
        TLRPC$User tLRPC$User = null;
        if (this.addToGroupAlertString == null && this.checkCanWrite) {
            int i = (int) j2;
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
        int i3 = this.initialDialogsType;
        if (i3 == 11 || i3 == 12 || i3 == 13) {
            boolean z3 = z2;
            if (!this.checkingImportDialog) {
                int i4 = (int) j2;
                if (i4 > 0) {
                    TLRPC$User user = getMessagesController().getUser(Integer.valueOf(i4));
                    if (!user.mutual_contact) {
                        getUndoView().showWithAction(j2, 45, (Runnable) null);
                        return;
                    } else {
                        tLRPC$Chat = null;
                        tLRPC$User = user;
                    }
                } else {
                    TLRPC$Chat chat2 = getMessagesController().getChat(Integer.valueOf(-i4));
                    if (!ChatObject.hasAdminRights(chat2) || !ChatObject.canChangeChatInfo(chat2)) {
                        getUndoView().showWithAction(j2, 46, (Runnable) null);
                        return;
                    }
                    tLRPC$Chat = chat2;
                }
                AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
                TLRPC$TL_messages_checkHistoryImportPeer tLRPC$TL_messages_checkHistoryImportPeer = new TLRPC$TL_messages_checkHistoryImportPeer();
                tLRPC$TL_messages_checkHistoryImportPeer.peer = getMessagesController().getInputPeer(i4);
                getConnectionsManager().sendRequest(tLRPC$TL_messages_checkHistoryImportPeer, new RequestDelegate(alertDialog, tLRPC$User, tLRPC$Chat, j, z2, tLRPC$TL_messages_checkHistoryImportPeer) {
                    public final /* synthetic */ AlertDialog f$1;
                    public final /* synthetic */ TLRPC$User f$2;
                    public final /* synthetic */ TLRPC$Chat f$3;
                    public final /* synthetic */ long f$4;
                    public final /* synthetic */ boolean f$5;
                    public final /* synthetic */ TLRPC$TL_messages_checkHistoryImportPeer f$6;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                        this.f$5 = r7;
                        this.f$6 = r8;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        DialogsActivity.this.lambda$didSelectResult$41$DialogsActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, tLObject, tLRPC$TL_error);
                    }
                });
                try {
                    alertDialog.showDelayed(300);
                } catch (Exception unused) {
                }
            }
        } else if (!z || ((this.selectAlertString == null || this.selectAlertStringGroup == null) && this.addToGroupAlertString == null)) {
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
            int i5 = (int) j2;
            int i6 = (int) (j2 >> 32);
            if (i5 == 0) {
                TLRPC$User user2 = getMessagesController().getUser(Integer.valueOf(getMessagesController().getEncryptedChat(Integer.valueOf(i6)).user_id));
                if (user2 != null) {
                    str3 = LocaleController.getString("SendMessageTitle", NUM);
                    str2 = LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(user2));
                    str = LocaleController.getString("Send", NUM);
                } else {
                    return;
                }
            } else if (i5 == getUserConfig().getClientUserId()) {
                str3 = LocaleController.getString("SendMessageTitle", NUM);
                str2 = LocaleController.formatStringSimple(this.selectAlertStringGroup, LocaleController.getString("SavedMessages", NUM));
                str = LocaleController.getString("Send", NUM);
            } else if (i5 > 0) {
                TLRPC$User user3 = getMessagesController().getUser(Integer.valueOf(i5));
                if (user3 != null && this.selectAlertString != null) {
                    str3 = LocaleController.getString("SendMessageTitle", NUM);
                    str2 = LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(user3));
                    str = LocaleController.getString("Send", NUM);
                } else {
                    return;
                }
            } else {
                TLRPC$Chat chat3 = getMessagesController().getChat(Integer.valueOf(-i5));
                if (chat3 != null) {
                    if (this.addToGroupAlertString != null) {
                        str3 = LocaleController.getString("AddToTheGroupAlertTitle", NUM);
                        str2 = LocaleController.formatStringSimple(this.addToGroupAlertString, chat3.title);
                        str = LocaleController.getString("Add", NUM);
                    } else {
                        str3 = LocaleController.getString("SendMessageTitle", NUM);
                        str2 = LocaleController.formatStringSimple(this.selectAlertStringGroup, chat3.title);
                        str = LocaleController.getString("Send", NUM);
                    }
                } else {
                    return;
                }
            }
            builder3.setTitle(str3);
            builder3.setMessage(AndroidUtilities.replaceTags(str2));
            builder3.setPositiveButton(str, new DialogInterface.OnClickListener(j2) {
                public final /* synthetic */ long f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    DialogsActivity.this.lambda$didSelectResult$42$DialogsActivity(this.f$1, dialogInterface, i);
                }
            });
            builder3.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder3.create());
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didSelectResult$41 */
    public /* synthetic */ void lambda$didSelectResult$41$DialogsActivity(AlertDialog alertDialog, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, long j, boolean z, TLRPC$TL_messages_checkHistoryImportPeer tLRPC$TL_messages_checkHistoryImportPeer, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tLObject, tLRPC$User, tLRPC$Chat, j, z, tLRPC$TL_error, tLRPC$TL_messages_checkHistoryImportPeer) {
            public final /* synthetic */ AlertDialog f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ TLRPC$User f$3;
            public final /* synthetic */ TLRPC$Chat f$4;
            public final /* synthetic */ long f$5;
            public final /* synthetic */ boolean f$6;
            public final /* synthetic */ TLRPC$TL_error f$7;
            public final /* synthetic */ TLRPC$TL_messages_checkHistoryImportPeer f$8;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r8;
                this.f$7 = r9;
                this.f$8 = r10;
            }

            public final void run() {
                DialogsActivity.this.lambda$null$40$DialogsActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$40 */
    public /* synthetic */ void lambda$null$40$DialogsActivity(AlertDialog alertDialog, TLObject tLObject, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, long j, boolean z, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_messages_checkHistoryImportPeer tLRPC$TL_messages_checkHistoryImportPeer) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        this.checkingImportDialog = false;
        if (tLObject != null) {
            AlertsCreator.createImportDialogAlert(this, this.arguments.getString("importTitle"), ((TLRPC$TL_messages_checkedHistoryImportPeer) tLObject).confirm_text, tLRPC$User, tLRPC$Chat, new Runnable(j, z) {
                public final /* synthetic */ long f$1;
                public final /* synthetic */ boolean f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r4;
                }

                public final void run() {
                    DialogsActivity.this.lambda$null$39$DialogsActivity(this.f$1, this.f$2);
                }
            });
            return;
        }
        AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, this, tLRPC$TL_messages_checkHistoryImportPeer, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(j), tLRPC$TL_messages_checkHistoryImportPeer, tLRPC$TL_error);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$39 */
    public /* synthetic */ void lambda$null$39$DialogsActivity(long j, boolean z) {
        setDialogsListFrozen(true);
        ArrayList arrayList = new ArrayList();
        arrayList.add(Long.valueOf(j));
        this.delegate.didSelectDialogs(this, arrayList, (CharSequence) null, z);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didSelectResult$42 */
    public /* synthetic */ void lambda$didSelectResult$42$DialogsActivity(long j, DialogInterface dialogInterface, int i) {
        didSelectResult(j, false, false);
    }

    public RLottieImageView getFloatingButton() {
        return this.floatingButton;
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        RecyclerListView recyclerListView;
        $$Lambda$DialogsActivity$xquEdLxCvYu7uGPA1kg_YZkyUAA r10 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                DialogsActivity.this.lambda$getThemeDescriptions$43$DialogsActivity();
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
        $$Lambda$DialogsActivity$xquEdLxCvYu7uGPA1kg_YZkyUAA r7 = r10;
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
        while (i < 3) {
            if (i == 2) {
                SearchViewPager searchViewPager2 = this.searchViewPager;
                if (searchViewPager2 == null) {
                    i++;
                } else {
                    recyclerListView = searchViewPager2.searchListView;
                }
            } else {
                ViewPage[] viewPageArr = this.viewPages;
                if (viewPageArr == null) {
                    i++;
                } else {
                    recyclerListView = i < viewPageArr.length ? viewPageArr[i].listView : null;
                }
            }
            if (recyclerListView != null) {
                RecyclerListView recyclerListView2 = recyclerListView;
                arrayList.add(new ThemeDescription(recyclerListView2, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, Theme.dialogs_countPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounter"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, Theme.dialogs_countGrayPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounterMuted"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, Theme.dialogs_countTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounterText"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_lockDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_secretIcon"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_groupDrawable, Theme.dialogs_broadcastDrawable, Theme.dialogs_botDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_nameIcon"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_scamDrawable, Theme.dialogs_fakeDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_draft"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_pinnedDrawable, Theme.dialogs_reorderDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_pinnedIcon"));
                TextPaint[] textPaintArr = Theme.dialogs_namePaint;
                arrayList.add(new ThemeDescription((View) recyclerListView2, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (String[]) null, new Paint[]{textPaintArr[0], textPaintArr[1], Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_name"));
                TextPaint[] textPaintArr2 = Theme.dialogs_nameEncryptedPaint;
                arrayList.add(new ThemeDescription((View) recyclerListView2, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (String[]) null, new Paint[]{textPaintArr2[0], textPaintArr2[1], Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_secretName"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePaint[1], (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_message_threeLines"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePaint[0], (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_message"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, Theme.dialogs_messageNamePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_nameMessage_threeLines"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_draft"));
                arrayList.add(new ThemeDescription((View) recyclerListView2, 0, new Class[]{DialogCell.class}, (String[]) null, (Paint[]) Theme.dialogs_messagePrintingPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionMessage"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, Theme.dialogs_timePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_date"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, Theme.dialogs_pinnedPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_pinnedOverlay"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, Theme.dialogs_tabletSeletedPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_tabletSelectedOverlay"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_checkDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_sentCheck"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_checkReadDrawable, Theme.dialogs_halfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_sentReadCheck"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_clockDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_sentClock"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, Theme.dialogs_errorPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_sentError"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_errorDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_sentErrorIcon"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_verifiedCheck"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_verifiedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_verifiedBackground"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_muteDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_muteIcon"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_mentionDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_mentionIcon"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_archivePinBackground"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_archiveBackground"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_onlineCircle"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{DialogCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription((View) recyclerListView2, ThemeDescription.FLAG_CHECKBOX, new Class[]{DialogCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription((View) recyclerListView2, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{DialogCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
                arrayList.add(new ThemeDescription((View) recyclerListView2, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_offlinePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
                arrayList.add(new ThemeDescription(recyclerListView2, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_onlinePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText3"));
                GraySectionCell.createThemeDescriptions(arrayList, recyclerListView);
                arrayList.add(new ThemeDescription(recyclerListView2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{HashtagSearchCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(recyclerListView2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
                arrayList.add(new ThemeDescription(recyclerListView2, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
                arrayList.add(new ThemeDescription(recyclerListView2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
                arrayList.add(new ThemeDescription(recyclerListView2, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
                arrayList.add(new ThemeDescription((View) recyclerListView2, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
                arrayList.add(new ThemeDescription((View) recyclerListView2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText2"));
            }
            i++;
        }
        $$Lambda$DialogsActivity$xquEdLxCvYu7uGPA1kg_YZkyUAA r72 = r10;
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
                arrayList.add(new ThemeDescription((View) this.viewPages[i2].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DialogsEmptyCell.class}, new String[]{"emptyTextView1"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_nameMessage_threeLines"));
                arrayList.add(new ThemeDescription((View) this.viewPages[i2].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DialogsEmptyCell.class}, new String[]{"emptyTextView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_message"));
                if (SharedConfig.archiveHidden) {
                    arrayList.add(new ThemeDescription((View) this.viewPages[i2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Arrow1", "avatar_backgroundArchivedHidden"));
                    arrayList.add(new ThemeDescription((View) this.viewPages[i2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Arrow2", "avatar_backgroundArchivedHidden"));
                } else {
                    arrayList.add(new ThemeDescription((View) this.viewPages[i2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Arrow1", "avatar_backgroundArchived"));
                    arrayList.add(new ThemeDescription((View) this.viewPages[i2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Arrow2", "avatar_backgroundArchived"));
                }
                arrayList.add(new ThemeDescription((View) this.viewPages[i2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Box2", "avatar_text"));
                arrayList.add(new ThemeDescription((View) this.viewPages[i2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Box1", "avatar_text"));
                arrayList.add(new ThemeDescription((View) this.viewPages[i2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_pinArchiveDrawable}, "Arrow", "chats_archiveIcon"));
                arrayList.add(new ThemeDescription((View) this.viewPages[i2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_pinArchiveDrawable}, "Line", "chats_archiveIcon"));
                arrayList.add(new ThemeDescription((View) this.viewPages[i2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unpinArchiveDrawable}, "Arrow", "chats_archiveIcon"));
                arrayList.add(new ThemeDescription((View) this.viewPages[i2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unpinArchiveDrawable}, "Line", "chats_archiveIcon"));
                arrayList.add(new ThemeDescription((View) this.viewPages[i2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveDrawable}, "Arrow", "chats_archiveBackground"));
                arrayList.add(new ThemeDescription((View) this.viewPages[i2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveDrawable}, "Box2", "chats_archiveIcon"));
                arrayList.add(new ThemeDescription((View) this.viewPages[i2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveDrawable}, "Box1", "chats_archiveIcon"));
                arrayList.add(new ThemeDescription((View) this.viewPages[i2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_hidePsaDrawable}, "Line 1", "chats_archiveBackground"));
                arrayList.add(new ThemeDescription((View) this.viewPages[i2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_hidePsaDrawable}, "Line 2", "chats_archiveBackground"));
                arrayList.add(new ThemeDescription((View) this.viewPages[i2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_hidePsaDrawable}, "Line 3", "chats_archiveBackground"));
                arrayList.add(new ThemeDescription((View) this.viewPages[i2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_hidePsaDrawable}, "Cup Red", "chats_archiveIcon"));
                arrayList.add(new ThemeDescription((View) this.viewPages[i2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_hidePsaDrawable}, "Box", "chats_archiveIcon"));
                arrayList.add(new ThemeDescription((View) this.viewPages[i2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unarchiveDrawable}, "Arrow1", "chats_archiveIcon"));
                arrayList.add(new ThemeDescription((View) this.viewPages[i2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unarchiveDrawable}, "Arrow2", "chats_archivePinBackground"));
                arrayList.add(new ThemeDescription((View) this.viewPages[i2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unarchiveDrawable}, "Box2", "chats_archiveIcon"));
                arrayList.add(new ThemeDescription((View) this.viewPages[i2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unarchiveDrawable}, "Box1", "chats_archiveIcon"));
                arrayList.add(new ThemeDescription((View) this.viewPages[i2].listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                ThemeDescription themeDescription = r1;
                $$Lambda$DialogsActivity$xquEdLxCvYu7uGPA1kg_YZkyUAA r8 = r10;
                int i3 = i2;
                ThemeDescription themeDescription2 = new ThemeDescription((View) this.viewPages[i2].listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "windowBackgroundWhiteGrayText");
                arrayList.add(themeDescription);
                arrayList.add(new ThemeDescription((View) this.viewPages[i3].listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "windowBackgroundWhiteBlueText"));
                arrayList.add(new ThemeDescription((View) this.viewPages[i3].listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
                arrayList.add(new ThemeDescription((View) this.viewPages[i3].listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
                arrayList.add(new ThemeDescription(this.viewPages[i3].progressView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
                ViewPager archiveHintCellPager = this.viewPages[i3].dialogsAdapter.getArchiveHintCellPager();
                arrayList.add(new ThemeDescription((View) archiveHintCellPager, 0, new Class[]{ArchiveHintInnerCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_nameMessage_threeLines"));
                arrayList.add(new ThemeDescription((View) archiveHintCellPager, 0, new Class[]{ArchiveHintInnerCell.class}, new String[]{"imageView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounter"));
                arrayList.add(new ThemeDescription((View) archiveHintCellPager, 0, new Class[]{ArchiveHintInnerCell.class}, new String[]{"headerTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_nameMessage_threeLines"));
                arrayList.add(new ThemeDescription((View) archiveHintCellPager, 0, new Class[]{ArchiveHintInnerCell.class}, new String[]{"messageTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_message"));
                arrayList.add(new ThemeDescription(archiveHintCellPager, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultArchived"));
                i2 = i3 + 1;
            }
        }
        $$Lambda$DialogsActivity$xquEdLxCvYu7uGPA1kg_YZkyUAA r73 = r10;
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
            arrayList.add(new ThemeDescription(dialogsSearchAdapter6 != null ? dialogsSearchAdapter6.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_onlineCircle"));
        }
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerBackground"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"playButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerPlayPause"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerTitle"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_FASTSCROLL, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerPerformer"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"closeButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerClose"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "returnToCallBackground"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "returnToCallText"));
        for (int i4 = 0; i4 < this.undoView.length; i4++) {
            arrayList.add(new ThemeDescription(this.undoView[i4], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_background"));
            arrayList.add(new ThemeDescription((View) this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"subinfoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "info1", "undo_background"));
            arrayList.add(new ThemeDescription((View) this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "info2", "undo_background"));
            arrayList.add(new ThemeDescription((View) this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luCLASSNAME", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luCLASSNAME", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luCLASSNAME", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc9", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc8", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc7", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc6", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc5", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc4", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc3", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc2", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc1", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Oval", "undo_infoColor"));
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
        $$Lambda$DialogsActivity$xquEdLxCvYu7uGPA1kg_YZkyUAA r74 = r10;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r74, "actionBarTipBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r74, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r74, "player_time"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r74, "chat_messagePanelCursor"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r74, "avatar_actionBarIconBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r74, "groupcreate_spanBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "voipgroup_overlayGreen1"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "voipgroup_overlayGreen2"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "voipgroup_overlayBlue1"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "voipgroup_overlayBlue2"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "voipgroup_topPanelGreen1"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "voipgroup_topPanelGreen2"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "voipgroup_topPanelBlue1"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "voipgroup_topPanelBlue2"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "voipgroup_topPanelGray"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "voipgroup_overlayAlertGradientMuted"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "voipgroup_overlayAlertGradientMuted2"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "voipgroup_overlayAlertGradientUnmuted"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "voipgroup_overlayAlertGradientUnmuted2"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "voipgroup_mutedByAdminGradient"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "voipgroup_mutedByAdminGradient2"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "voipgroup_mutedByAdminGradient3"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "voipgroup_overlayAlertMutedByAdmin"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "kvoipgroup_overlayAlertMutedByAdmin2"));
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
    /* renamed from: lambda$getThemeDescriptions$43 */
    public /* synthetic */ void lambda$getThemeDescriptions$43$DialogsActivity() {
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
