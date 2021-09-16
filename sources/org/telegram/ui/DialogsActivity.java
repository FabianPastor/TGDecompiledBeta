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
import android.widget.TextView;
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
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
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
import org.telegram.ui.ActionBar.BottomSheet;
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
import org.telegram.ui.Components.BulletinFactory;
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
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.Components.RecyclerAnimationScrollHelper;
import org.telegram.ui.Components.RecyclerItemsEnterAnimator;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SearchViewPager;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.ViewPagerFixed;
import org.telegram.ui.GroupCreateFinalActivity;

public class DialogsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    public static boolean[] dialogsLoaded = new boolean[3];
    /* access modifiers changed from: private */
    public static final Interpolator interpolator = DialogsActivity$$ExternalSyntheticLambda21.INSTANCE;
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
    public float additionalFloatingTranslation2;
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
    View databaseMigrationHint;
    /* access modifiers changed from: private */
    public int debugLastUpdateAction = -1;
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
    private boolean hasInvoice;
    private int hasPoll;
    /* access modifiers changed from: private */
    public int initialDialogsType;
    private String initialSearchString;
    private int initialSearchType = -1;
    boolean isDrawerTransition;
    boolean isSlideBackTransition;
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
    ValueAnimator slideBackTransitionAnimator;
    float slideFragmentProgress = 1.0f;
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
    public FrameLayout updateLayout;
    /* access modifiers changed from: private */
    public AnimatorSet updateLayoutAnimator;
    /* access modifiers changed from: private */
    public RadialProgress2 updateLayoutIcon;
    /* access modifiers changed from: private */
    public boolean updatePullAfterScroll;
    /* access modifiers changed from: private */
    public TextView updateTextView;
    /* access modifiers changed from: private */
    public ViewPage[] viewPages;
    /* access modifiers changed from: private */
    public boolean waitingForScrollFinished;
    /* access modifiers changed from: private */
    public boolean whiteActionBar;

    public interface DialogsActivityDelegate {
        void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList<Long> arrayList, CharSequence charSequence, boolean z);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createActionMode$10(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ float lambda$static$0(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2 * f2 * f2) + 1.0f;
    }

    private static class ViewPage extends FrameLayout {
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

        static /* synthetic */ int access$10508(ViewPage viewPage) {
            int i = viewPage.lastItemsCount;
            viewPage.lastItemsCount = i + 1;
            return i;
        }

        static /* synthetic */ int access$10510(ViewPage viewPage) {
            int i = viewPage.lastItemsCount;
            viewPage.lastItemsCount = i - 1;
            return i;
        }

        public ViewPage(Context context) {
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
            int i = 0;
            if (view == DialogsActivity.this.viewPages[0] || ((DialogsActivity.this.viewPages.length > 1 && view == DialogsActivity.this.viewPages[1]) || view == DialogsActivity.this.fragmentContextView || view == DialogsActivity.this.fragmentLocationContextView || view == DialogsActivity.this.searchViewPager)) {
                canvas.save();
                canvas.clipRect(0.0f, (-getY()) + DialogsActivity.this.actionBar.getY() + ((float) getActionBarFullHeight()), (float) getMeasuredWidth(), (float) getMeasuredHeight());
                DialogsActivity dialogsActivity = DialogsActivity.this;
                float f = dialogsActivity.slideFragmentProgress;
                if (f != 1.0f) {
                    float f2 = 1.0f - ((1.0f - f) * 0.05f);
                    canvas.translate(((float) (dialogsActivity.isDrawerTransition ? AndroidUtilities.dp(4.0f) : -AndroidUtilities.dp(4.0f))) * (1.0f - DialogsActivity.this.slideFragmentProgress), 0.0f);
                    canvas.scale(f2, f2, DialogsActivity.this.isDrawerTransition ? (float) getMeasuredWidth() : 0.0f, (-getY()) + DialogsActivity.this.actionBar.getY() + ((float) getActionBarFullHeight()));
                }
                z = super.drawChild(canvas, view, j);
                canvas.restore();
            } else if (view != DialogsActivity.this.actionBar || DialogsActivity.this.slideFragmentProgress == 1.0f) {
                z = super.drawChild(canvas, view, j);
            } else {
                canvas.save();
                DialogsActivity dialogsActivity2 = DialogsActivity.this;
                float f3 = 1.0f - ((1.0f - dialogsActivity2.slideFragmentProgress) * 0.05f);
                canvas.translate(((float) (dialogsActivity2.isDrawerTransition ? AndroidUtilities.dp(4.0f) : -AndroidUtilities.dp(4.0f))) * (1.0f - DialogsActivity.this.slideFragmentProgress), 0.0f);
                float measuredWidth = DialogsActivity.this.isDrawerTransition ? (float) getMeasuredWidth() : 0.0f;
                if (DialogsActivity.this.actionBar.getOccupyStatusBar()) {
                    i = AndroidUtilities.statusBarHeight;
                }
                canvas.scale(f3, f3, measuredWidth, ((float) i) + (((float) ActionBar.getCurrentActionBarHeight()) / 2.0f));
                z = super.drawChild(canvas, view, j);
                canvas.restore();
            }
            if (view == DialogsActivity.this.actionBar && DialogsActivity.this.parentLayout != null) {
                int y = (int) (DialogsActivity.this.actionBar.getY() + ((float) getActionBarFullHeight()));
                DialogsActivity.this.parentLayout.drawHeaderShadow(canvas, (int) ((1.0f - DialogsActivity.this.searchAnimationProgress) * 255.0f), y);
                if (DialogsActivity.this.searchAnimationProgress > 0.0f) {
                    if (DialogsActivity.this.searchAnimationProgress < 1.0f) {
                        int alpha = Theme.dividerPaint.getAlpha();
                        Theme.dividerPaint.setAlpha((int) (((float) alpha) * DialogsActivity.this.searchAnimationProgress));
                        float f4 = (float) y;
                        canvas.drawLine(0.0f, f4, (float) getMeasuredWidth(), f4, Theme.dividerPaint);
                        Theme.dividerPaint.setAlpha(alpha);
                    } else {
                        float f5 = (float) y;
                        canvas.drawLine(0.0f, f5, (float) getMeasuredWidth(), f5, Theme.dividerPaint);
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
                DialogsActivity dialogsActivity2 = DialogsActivity.this;
                float f5 = dialogsActivity2.slideFragmentProgress;
                if (f5 != 1.0f) {
                    float f6 = 1.0f - ((1.0f - f5) * 0.05f);
                    canvas2.translate(((float) (dialogsActivity2.isDrawerTransition ? AndroidUtilities.dp(4.0f) : -AndroidUtilities.dp(4.0f))) * (1.0f - DialogsActivity.this.slideFragmentProgress), 0.0f);
                    canvas2.scale(f6, 1.0f, DialogsActivity.this.isDrawerTransition ? (float) getMeasuredWidth() : 0.0f, DialogsActivity.this.fragmentContextView.getY());
                }
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
            float f = 0.0f;
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
            int i5 = 0;
            while (i5 < childCount) {
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
                            childAt.setTranslationY(f);
                        } else {
                            i3 = (int) (((float) i3) + DialogsActivity.this.filterTabsMoveFrom);
                        }
                        DialogsActivity dialogsActivity = DialogsActivity.this;
                        int i6 = (dialogsActivity.isSlideBackTransition || dialogsActivity.isDrawerTransition) ? (int) (((float) i3) * 0.05f) : 0;
                        childAt.setPadding(childAt.getPaddingLeft(), childAt.getPaddingTop(), childAt.getPaddingRight(), i6);
                        childAt.measure(makeMeasureSpec, View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), i3 + i6), NUM));
                        childAt.setPivotX((float) (childAt.getMeasuredWidth() / 2));
                    } else {
                        if (childAt == DialogsActivity.this.searchViewPager) {
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
                        i5++;
                        f = 0.0f;
                    }
                }
                i5++;
                f = 0.0f;
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
            if (DialogsActivity.this.parentLayout == null || DialogsActivity.this.filterTabsView == null || DialogsActivity.this.filterTabsView.isEditing() || DialogsActivity.this.searching || DialogsActivity.this.parentLayout.checkTransitionAnimation() || DialogsActivity.this.parentLayout.isInPreviewMode() || DialogsActivity.this.parentLayout.isPreviewOpenAnimationInProgress() || DialogsActivity.this.parentLayout.getDrawerLayoutContainer().isDrawerOpened() || ((motionEvent != null && !DialogsActivity.this.startedTracking && motionEvent.getY() <= ((float) DialogsActivity.this.actionBar.getMeasuredHeight()) + DialogsActivity.this.actionBar.getTranslationY()) || SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) != 5)) {
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
                                DialogsActivity.this.parentLayout.getDrawerLayoutContainer().setAllowOpenDrawerBySwipe(DialogsActivity.this.viewPages[0].selectedType == DialogsActivity.this.filterTabsView.getFirstTabId() || DialogsActivity.this.searchIsShowed || SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) != 5);
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
                    DialogsActivity.this.parentLayout.getDrawerLayoutContainer().setAllowOpenDrawerBySwipe(DialogsActivity.this.viewPages[0].selectedType == DialogsActivity.this.filterTabsView.getFirstTabId() || DialogsActivity.this.searchIsShowed || SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) != 5);
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
        private final ViewPage parentPage;
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
            ViewPage viewPage = this.parentPage;
            if (viewPage != null && viewPage.recyclerItemsEnterAnimator != null) {
                this.parentPage.recyclerItemsEnterAnimator.onDetached();
            }
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
                PullForegroundDrawable access$9800 = this.parentPage.pullForegroundDrawable;
                if (this.parentPage.archivePullViewState != 0) {
                    z = true;
                }
                access$9800.setWillDraw(z);
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
                                DialogsActivity.this.performSelectedDialogsAction(arrayList, 101, true);
                            } else if (SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 3) {
                                if (!DialogsActivity.this.getMessagesController().isDialogMuted(dialogId)) {
                                    NotificationsController.getInstance(UserConfig.selectedAccount).setDialogNotificationsSettings(dialogId, 3);
                                    if (BulletinFactory.canShowBulletin(DialogsActivity.this)) {
                                        BulletinFactory.createMuteBulletin(DialogsActivity.this, 3).show();
                                    }
                                } else {
                                    ArrayList arrayList2 = new ArrayList();
                                    arrayList2.add(Long.valueOf(dialogId));
                                    DialogsActivity dialogsActivity2 = DialogsActivity.this;
                                    int unused3 = dialogsActivity2.canMuteCount = MessagesController.getInstance(dialogsActivity2.currentAccount).isDialogMuted(dialogId) ^ true ? 1 : 0;
                                    DialogsActivity dialogsActivity3 = DialogsActivity.this;
                                    int unused4 = dialogsActivity3.canUnmuteCount = dialogsActivity3.canMuteCount > 0 ? 0 : 1;
                                    DialogsActivity.this.performSelectedDialogsAction(arrayList2, 104, true);
                                }
                            } else if (SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 0) {
                                ArrayList arrayList3 = new ArrayList();
                                arrayList3.add(Long.valueOf(dialogId));
                                int unused5 = DialogsActivity.this.canPinCount = DialogsActivity.this.isDialogPinned(tLRPC$Dialog) ^ true ? 1 : 0;
                                DialogsActivity.this.performSelectedDialogsAction(arrayList3, 100, true);
                            } else if (SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 4) {
                                ArrayList arrayList4 = new ArrayList();
                                arrayList4.add(Long.valueOf(dialogId));
                                DialogsActivity.this.performSelectedDialogsAction(arrayList4, 102, true);
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
                    ofFloat.addUpdateListener(new DialogsActivity$DialogsRecyclerView$$ExternalSyntheticLambda0(this));
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
            return onTouchEvent;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onTouchEvent$0(ValueAnimator valueAnimator) {
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
                        MessagesController.DialogFilter dialogFilter = null;
                        if (DialogsActivity.this.actionBar.isActionModeShowed((String) null)) {
                            TLRPC$Dialog tLRPC$Dialog2 = DialogsActivity.this.getMessagesController().dialogs_dict.get(dialogId);
                            if (!DialogsActivity.this.allowMoving || tLRPC$Dialog2 == null || !DialogsActivity.this.isDialogPinned(tLRPC$Dialog2) || DialogObject.isFolderDialogId(dialogId)) {
                                return 0;
                            }
                            DialogCell unused = DialogsActivity.this.movingView = (DialogCell) viewHolder.itemView;
                            DialogsActivity.this.movingView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                            this.swipeFolderBack = false;
                            return ItemTouchHelper.Callback.makeMovementFlags(3, 0);
                        } else if (!(DialogsActivity.this.filterTabsView != null && DialogsActivity.this.filterTabsView.getVisibility() == 0 && SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 5) && DialogsActivity.this.allowSwipeDuringCurrentTouch && (!((dialogId == DialogsActivity.this.getUserConfig().clientUserId || dialogId == 777000) && SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 2) && (!DialogsActivity.this.getMessagesController().isPromoDialog(dialogId, false) || DialogsActivity.this.getMessagesController().promoDialogType == MessagesController.PROMO_TYPE_PSA))) {
                            boolean z = DialogsActivity.this.folderId == 0 && (SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 3 || SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 1 || SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 0 || SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 4);
                            if (SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 1) {
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
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r5 = r5.dialogs_dict
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
                    if (!DialogsActivity.this.getMessagesController().isPromoDialog(dialogId, false) && DialogsActivity.this.folderId == 0 && SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 1) {
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(Long.valueOf(dialogId));
                        DialogsActivity dialogsActivity = DialogsActivity.this;
                        if (tLRPC$Dialog.unread_count > 0 || tLRPC$Dialog.unread_mark) {
                            i2 = 1;
                        }
                        int unused = dialogsActivity.canReadCount = i2;
                        DialogsActivity.this.performSelectedDialogsAction(arrayList, 101, true);
                        return;
                    }
                    DialogCell unused2 = DialogsActivity.this.slidingView = dialogCell;
                    DialogsActivity$SwipeController$$ExternalSyntheticLambda2 dialogsActivity$SwipeController$$ExternalSyntheticLambda2 = new DialogsActivity$SwipeController$$ExternalSyntheticLambda2(this, tLRPC$Dialog, this.parentPage.dialogsAdapter.getItemCount(), viewHolder.getAdapterPosition());
                    DialogsActivity.this.setDialogsListFrozen(true);
                    if (Utilities.random.nextInt(1000) == 1) {
                        if (DialogsActivity.this.pacmanAnimation == null) {
                            PacmanAnimation unused3 = DialogsActivity.this.pacmanAnimation = new PacmanAnimation(this.parentPage.listView);
                        }
                        DialogsActivity.this.pacmanAnimation.setFinishRunnable(dialogsActivity$SwipeController$$ExternalSyntheticLambda2);
                        DialogsActivity.this.pacmanAnimation.start();
                        return;
                    }
                    dialogsActivity$SwipeController$$ExternalSyntheticLambda2.run();
                    return;
                }
                return;
            }
            DialogCell unused4 = DialogsActivity.this.slidingView = null;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onSwiped$1(TLRPC$Dialog tLRPC$Dialog, int i, int i2) {
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
                    ViewPage.access$10510(this.parentPage);
                    this.parentPage.dialogsAdapter.notifyItemRemoved(i2);
                    int unused2 = DialogsActivity.this.dialogRemoveFinished = 2;
                    return;
                }
                int addDialogToFolder = DialogsActivity.this.getMessagesController().addDialogToFolder(tLRPC$Dialog.id, DialogsActivity.this.folderId == 0 ? 1 : 0, -1, 0);
                if (!(addDialogToFolder == 2 && i2 == 0)) {
                    this.parentPage.dialogsItemAnimator.prepareForRemove();
                    ViewPage.access$10510(this.parentPage);
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
                            ViewPage.access$10508(this.parentPage);
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
                    DialogsActivity.this.getUndoView().showWithAction(tLRPC$Dialog.id, z ? 2 : 3, (Runnable) null, new DialogsActivity$SwipeController$$ExternalSyntheticLambda1(this, tLRPC$Dialog, i3));
                }
                if (DialogsActivity.this.folderId != 0 && DialogsActivity.this.frozenDialogsList.isEmpty()) {
                    this.parentPage.listView.setEmptyView((View) null);
                    this.parentPage.progressView.setVisibility(4);
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onSwiped$0(TLRPC$Dialog tLRPC$Dialog, int i) {
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
                    ViewPage.access$10508(this.parentPage);
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
                    ViewPage.access$10510(this.parentPage);
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
                AndroidUtilities.runOnUIThread(new DialogsActivity$SwipeController$$ExternalSyntheticLambda0(DialogsActivity.this.movingView), this.parentPage.dialogsItemAnimator.getMoveDuration());
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
            this.hasInvoice = this.arguments.getBoolean("hasInvoice", false);
        }
        if (this.initialDialogsType == 0) {
            this.askAboutContacts = MessagesController.getGlobalNotificationsSettings().getBoolean("askAboutContacts", true);
            SharedConfig.loadProxyList();
        }
        if (this.searchString == null) {
            this.currentConnectionState = getConnectionsManager().getConnectionState();
            getNotificationCenter().addObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
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
            getNotificationCenter().addObserver(this, NotificationCenter.fileLoaded);
            getNotificationCenter().addObserver(this, NotificationCenter.fileLoadFailed);
            getNotificationCenter().addObserver(this, NotificationCenter.fileLoadProgressChanged);
            getNotificationCenter().addObserver(this, NotificationCenter.onDatabaseMigration);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.appUpdateAvailable);
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
            accountInstance.getMediaDataController().loadRecents(3, false, true, false);
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
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
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
            getNotificationCenter().removeObserver(this, NotificationCenter.fileLoaded);
            getNotificationCenter().removeObserver(this, NotificationCenter.fileLoadFailed);
            getNotificationCenter().removeObserver(this, NotificationCenter.fileLoadProgressChanged);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.appUpdateAvailable);
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
                if (!(f == getTranslationY() || DialogsActivity.this.fragmentView == null)) {
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

    /* JADX WARNING: type inference failed for: r12v1, types: [boolean, int] */
    /* JADX WARNING: type inference failed for: r12v7 */
    /* JADX WARNING: type inference failed for: r12v8 */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x07b2  */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x07bf  */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x07fb  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x089a A[LOOP:2: B:188:0x0898->B:189:0x089a, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x08c1  */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x0932  */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x0935  */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x0942  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x09c3  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x09cf  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r36) {
        /*
            r35 = this;
            r10 = r35
            r11 = r36
            r12 = 0
            r10.searching = r12
            r10.searchWas = r12
            r13 = 0
            r10.pacmanAnimation = r13
            java.util.ArrayList<java.lang.Long> r0 = r10.selectedDialogs
            r0.clear()
            android.view.ViewConfiguration r0 = android.view.ViewConfiguration.get(r36)
            int r0 = r0.getScaledMaximumFlingVelocity()
            r10.maximumVelocity = r0
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda22 r0 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda22
            r0.<init>(r11)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r6 = r0.createMenu()
            boolean r0 = r10.onlySelect
            r14 = 2131625273(0x7f0e0539, float:1.887775E38)
            java.lang.String r15 = "Done"
            r9 = 0
            r8 = 2
            r7 = 8
            r5 = 1
            if (r0 != 0) goto L_0x00e5
            java.lang.String r0 = r10.searchString
            if (r0 != 0) goto L_0x00e5
            int r0 = r10.folderId
            if (r0 != 0) goto L_0x00e5
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            r2 = 0
            java.lang.String r0 = "actionBarDefaultSelector"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            java.lang.String r0 = "actionBarDefaultIcon"
            int r16 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            r17 = 1
            r0 = r4
            r1 = r36
            r13 = r4
            r4 = r16
            r12 = 1
            r5 = r17
            r0.<init>((android.content.Context) r1, (org.telegram.ui.ActionBar.ActionBarMenu) r2, (int) r3, (int) r4, (boolean) r5)
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
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda18 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda18
            r1.<init>(r10)
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
            r1 = 2131627262(0x7f0e0cfe, float:1.8881783E38)
            java.lang.String r2 = "ProxySettings"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setContentDescription(r1)
            org.telegram.ui.Components.RLottieDrawable r0 = new org.telegram.ui.Components.RLottieDrawable
            r19 = 2131558463(0x7f0d003f, float:1.8742243E38)
            r1 = 1105199104(0x41e00000, float:28.0)
            int r21 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r22 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r23 = 1
            r24 = 0
            java.lang.String r20 = "passcode_lock_close"
            r18 = r0
            r18.<init>((int) r19, (java.lang.String) r20, (int) r21, (int) r22, (boolean) r23, (int[]) r24)
            r10.passcodeDrawable = r0
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.addItem((int) r12, (android.graphics.drawable.Drawable) r0)
            r10.passcodeItem = r0
            r1 = 2131624006(0x7f0e0046, float:1.887518E38)
            java.lang.String r2 = "AccDescrPasscodeLock"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setContentDescription(r1)
            r35.updatePasscodeButton()
            r0 = 0
            r10.updateProxyButton(r0)
            goto L_0x00e7
        L_0x00e5:
            r0 = 0
            r12 = 1
        L_0x00e7:
            r1 = 2131165478(0x7var_, float:1.7945174E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r6.addItem((int) r0, (int) r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r1.setIsSearchField(r12, r12)
            org.telegram.ui.DialogsActivity$3 r1 = new org.telegram.ui.DialogsActivity$3
            r1.<init>()
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.setActionBarMenuItemSearchListener(r1)
            r10.searchItem = r0
            java.lang.String r1 = "Search"
            r2 = 2131627490(0x7f0e0de2, float:1.8882246E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r1, r2)
            r0.setSearchFieldHint(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r10.searchItem
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)
            r0.setContentDescription(r1)
            boolean r0 = r10.onlySelect
            java.lang.String r13 = "actionBarDefault"
            r5 = 10
            r4 = 3
            if (r0 == 0) goto L_0x0163
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r1 = 2131165468(0x7var_c, float:1.7945154E38)
            r0.setBackButtonImage(r1)
            int r0 = r10.initialDialogsType
            if (r0 != r4) goto L_0x013a
            java.lang.String r1 = r10.selectAlertString
            if (r1 != 0) goto L_0x013a
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r1 = 2131625652(0x7f0e06b4, float:1.8878518E38)
            java.lang.String r2 = "ForwardTo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x0159
        L_0x013a:
            if (r0 != r5) goto L_0x014b
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r1 = 2131627555(0x7f0e0e23, float:1.8882378E38)
            java.lang.String r2 = "SelectChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x0159
        L_0x014b:
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r1 = 2131627554(0x7f0e0e22, float:1.8882376E38)
            java.lang.String r2 = "SelectChat"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
        L_0x0159:
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r0.setBackgroundColor(r1)
            goto L_0x01ca
        L_0x0163:
            java.lang.String r0 = r10.searchString
            if (r0 != 0) goto L_0x0187
            int r0 = r10.folderId
            if (r0 == 0) goto L_0x016c
            goto L_0x0187
        L_0x016c:
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            org.telegram.ui.ActionBar.MenuDrawable r1 = new org.telegram.ui.ActionBar.MenuDrawable
            r1.<init>()
            r10.menuDrawable = r1
            r0.setBackButtonDrawable(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r1 = 2131624003(0x7f0e0043, float:1.8875173E38)
            java.lang.String r2 = "AccDescrOpenMenu"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setBackButtonContentDescription(r1)
            goto L_0x0194
        L_0x0187:
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            org.telegram.ui.ActionBar.BackDrawable r1 = new org.telegram.ui.ActionBar.BackDrawable
            r2 = 0
            r1.<init>(r2)
            r10.backDrawable = r1
            r0.setBackButtonDrawable(r1)
        L_0x0194:
            int r0 = r10.folderId
            if (r0 == 0) goto L_0x01a7
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r1 = 2131624318(0x7f0e017e, float:1.8875812E38)
            java.lang.String r2 = "ArchivedChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x01c1
        L_0x01a7:
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r0 == 0) goto L_0x01b3
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            java.lang.String r1 = "Telegram Beta"
            r0.setTitle(r1)
            goto L_0x01c1
        L_0x01b3:
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r1 = 2131624288(0x7f0e0160, float:1.8875751E38)
            java.lang.String r2 = "AppName"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
        L_0x01c1:
            int r0 = r10.folderId
            if (r0 != 0) goto L_0x01ca
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r0.setSupportsHolidayImage(r12)
        L_0x01ca:
            boolean r0 = r10.onlySelect
            if (r0 != 0) goto L_0x01de
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r1 = 0
            r0.setAddToContainer(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r0.setCastShadows(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r0.setClipContent(r12)
        L_0x01de:
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda25 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda25
            r1.<init>(r10)
            r0.setTitleActionRunnable(r1)
            int r0 = r10.initialDialogsType
            if (r0 != 0) goto L_0x021a
            int r0 = r10.folderId
            if (r0 != 0) goto L_0x021a
            boolean r0 = r10.onlySelect
            if (r0 != 0) goto L_0x021a
            java.lang.String r0 = r10.searchString
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x021a
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
        L_0x021a:
            boolean r0 = r10.allowSwitchAccount
            r17 = 1113587712(0x42600000, float:56.0)
            if (r0 == 0) goto L_0x02b5
            int r0 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()
            if (r0 <= r12) goto L_0x02b5
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
            org.telegram.messenger.UserConfig r2 = r35.getUserConfig()
            org.telegram.tgnet.TLRPC$User r2 = r2.getCurrentUser()
            r0.setInfo((org.telegram.tgnet.TLRPC$User) r2)
            org.telegram.messenger.ImageReceiver r3 = r1.getImageReceiver()
            int r5 = r10.currentAccount
            r3.setCurrentAccount(r5)
            org.telegram.messenger.ImageLocation r19 = org.telegram.messenger.ImageLocation.getForUserOrChat(r2, r12)
            org.telegram.messenger.ImageLocation r21 = org.telegram.messenger.ImageLocation.getForUserOrChat(r2, r8)
            java.lang.String r20 = "50_50"
            java.lang.String r22 = "50_50"
            r18 = r1
            r23 = r0
            r24 = r2
            r18.setImage((org.telegram.messenger.ImageLocation) r19, (java.lang.String) r20, (org.telegram.messenger.ImageLocation) r21, (java.lang.String) r22, (android.graphics.drawable.Drawable) r23, (java.lang.Object) r24)
            r0 = 0
        L_0x0286:
            if (r0 >= r4) goto L_0x02b5
            org.telegram.messenger.AccountInstance r1 = org.telegram.messenger.AccountInstance.getInstance(r0)
            org.telegram.messenger.UserConfig r1 = r1.getUserConfig()
            org.telegram.tgnet.TLRPC$User r1 = r1.getCurrentUser()
            if (r1 == 0) goto L_0x02b2
            org.telegram.ui.Cells.AccountSelectCell r1 = new org.telegram.ui.Cells.AccountSelectCell
            r2 = 0
            r1.<init>(r11, r2)
            r1.setAccount(r0, r12)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r10.switchItem
            int r3 = r0 + 10
            r5 = 1130758144(0x43660000, float:230.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r6 = 1111490560(0x42400000, float:48.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r2.addSubItem((int) r3, (android.view.View) r1, (int) r5, (int) r6)
        L_0x02b2:
            int r0 = r0 + 1
            goto L_0x0286
        L_0x02b5:
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r0.setAllowOverlayTitle(r12)
            androidx.recyclerview.widget.RecyclerView r0 = r10.sideMenu
            if (r0 == 0) goto L_0x02db
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
        L_0x02db:
            r0 = 0
            r10.createActionMode(r0)
            org.telegram.ui.DialogsActivity$ContentView r6 = new org.telegram.ui.DialogsActivity$ContentView
            r6.<init>(r11)
            r10.fragmentView = r6
            int r0 = r10.folderId
            if (r0 != 0) goto L_0x02f4
            int r0 = r10.initialDialogsType
            if (r0 != 0) goto L_0x02f4
            boolean r0 = r10.onlySelect
            if (r0 != 0) goto L_0x02f4
            r5 = 2
            goto L_0x02f5
        L_0x02f4:
            r5 = 1
        L_0x02f5:
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = new org.telegram.ui.DialogsActivity.ViewPage[r5]
            r10.viewPages = r0
            r3 = 0
        L_0x02fa:
            r2 = -1082130432(0xffffffffbvar_, float:-1.0)
            r1 = -2
            r0 = -1
            if (r3 >= r5) goto L_0x04fd
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
            if (r4 == 0) goto L_0x03a0
            r4 = 1
            goto L_0x03a1
        L_0x03a0:
            r4 = 2
        L_0x03a1:
            r1.setVerticalScrollbarPosition(r4)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r1 = r14.listView
            android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r0, r2)
            r14.addView(r1, r0)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r14.listView
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda43 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda43
            r1.<init>(r10, r14)
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
            r2 = 0
            r0.<init>(r1, r2)
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
            if (r0 == 0) goto L_0x0405
            r0 = 2
            goto L_0x0406
        L_0x0405:
            r0 = 0
        L_0x0406:
            int unused = r14.archivePullViewState = r0
            org.telegram.ui.Components.PullForegroundDrawable r0 = r14.pullForegroundDrawable
            if (r0 != 0) goto L_0x0452
            int r0 = r10.folderId
            if (r0 != 0) goto L_0x0452
            org.telegram.ui.DialogsActivity$13 r0 = new org.telegram.ui.DialogsActivity$13
            r1 = 2131624076(0x7f0e008c, float:1.8875321E38)
            java.lang.String r2 = "AccSwipeForArchive"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 2131624075(0x7f0e008b, float:1.887532E38)
            java.lang.String r4 = "AccReleaseForArchive"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r0.<init>(r10, r1, r2, r14)
            org.telegram.ui.Components.PullForegroundDrawable unused = r14.pullForegroundDrawable = r0
            boolean r0 = r35.hasHiddenArchive()
            if (r0 == 0) goto L_0x043b
            org.telegram.ui.Components.PullForegroundDrawable r0 = r14.pullForegroundDrawable
            r0.showHidden()
            goto L_0x0442
        L_0x043b:
            org.telegram.ui.Components.PullForegroundDrawable r0 = r14.pullForegroundDrawable
            r0.doNotShow()
        L_0x0442:
            org.telegram.ui.Components.PullForegroundDrawable r0 = r14.pullForegroundDrawable
            int r1 = r14.archivePullViewState
            if (r1 == 0) goto L_0x044e
            r1 = 1
            goto L_0x044f
        L_0x044e:
            r1 = 0
        L_0x044f:
            r0.setWillDraw(r1)
        L_0x0452:
            org.telegram.ui.DialogsActivity$14 r8 = new org.telegram.ui.DialogsActivity$14
            int r4 = r14.dialogsType
            int r2 = r10.folderId
            boolean r1 = r10.onlySelect
            java.util.ArrayList<java.lang.Long> r0 = r10.selectedDialogs
            int r9 = r10.currentAccount
            r22 = r0
            r0 = r8
            r23 = r1
            r1 = r35
            r24 = r2
            r2 = r35
            r25 = r3
            r3 = r36
            r19 = r5
            r12 = 10
            r5 = r24
            r12 = r6
            r6 = r23
            r23 = r13
            r13 = 8
            r7 = r22
            r13 = r8
            r8 = r9
            r20 = 0
            r9 = r14
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9)
            org.telegram.ui.Adapters.DialogsAdapter unused = r14.dialogsAdapter = r13
            org.telegram.ui.Adapters.DialogsAdapter r0 = r14.dialogsAdapter
            boolean r1 = r10.afterSignup
            r0.setForceShowEmptyCell(r1)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x04a9
            long r0 = r10.openedDialogId
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x04a9
            org.telegram.ui.Adapters.DialogsAdapter r0 = r14.dialogsAdapter
            long r1 = r10.openedDialogId
            r0.setOpenedDialogId(r1)
        L_0x04a9:
            org.telegram.ui.Adapters.DialogsAdapter r0 = r14.dialogsAdapter
            org.telegram.ui.Components.PullForegroundDrawable r1 = r14.pullForegroundDrawable
            r0.setArchivedPullDrawable(r1)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r14.listView
            org.telegram.ui.Adapters.DialogsAdapter r1 = r14.dialogsAdapter
            r0.setAdapter(r1)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r14.listView
            int r1 = r10.folderId
            if (r1 != 0) goto L_0x04cc
            org.telegram.ui.Components.FlickerLoadingView r1 = r14.progressView
            goto L_0x04cd
        L_0x04cc:
            r1 = 0
        L_0x04cd:
            r0.setEmptyView(r1)
            org.telegram.ui.Components.RecyclerAnimationScrollHelper r0 = new org.telegram.ui.Components.RecyclerAnimationScrollHelper
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r1 = r14.listView
            androidx.recyclerview.widget.LinearLayoutManager r2 = r14.layoutManager
            r0.<init>(r1, r2)
            org.telegram.ui.Components.RecyclerAnimationScrollHelper unused = r14.scrollHelper = r0
            if (r25 == 0) goto L_0x04eb
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r10.viewPages
            r0 = r0[r25]
            r1 = 8
            r0.setVisibility(r1)
        L_0x04eb:
            int r3 = r25 + 1
            r6 = r12
            r5 = r19
            r13 = r23
            r4 = 3
            r7 = 8
            r8 = 2
            r9 = 0
            r12 = 1
            r14 = 2131625273(0x7f0e0539, float:1.887775E38)
            goto L_0x02fa
        L_0x04fd:
            r12 = r6
            r23 = r13
            r20 = 0
            java.lang.String r3 = r10.searchString
            if (r3 == 0) goto L_0x0508
            r3 = 2
            goto L_0x050f
        L_0x0508:
            boolean r3 = r10.onlySelect
            if (r3 != 0) goto L_0x050e
            r3 = 1
            goto L_0x050f
        L_0x050e:
            r3 = 0
        L_0x050f:
            org.telegram.ui.Components.SearchViewPager r7 = new org.telegram.ui.Components.SearchViewPager
            int r4 = r10.initialDialogsType
            int r5 = r10.folderId
            org.telegram.ui.DialogsActivity$15 r6 = new org.telegram.ui.DialogsActivity$15
            r6.<init>()
            r8 = -1
            r0 = r7
            r9 = -2
            r1 = r36
            r13 = -1082130432(0xffffffffbvar_, float:-1.0)
            r2 = r35
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
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda42 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda42
            r1.<init>(r10)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            org.telegram.ui.Components.SearchViewPager r0 = r10.searchViewPager
            org.telegram.ui.Components.RecyclerListView r0 = r0.searchListView
            org.telegram.ui.DialogsActivity$17 r1 = new org.telegram.ui.DialogsActivity$17
            r1.<init>()
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended) r1)
            org.telegram.ui.Components.SearchViewPager r0 = r10.searchViewPager
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda44 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda44
            r1.<init>(r10)
            r0.setFilteredSearchViewDelegate(r1)
            org.telegram.ui.Components.SearchViewPager r0 = r10.searchViewPager
            r1 = 8
            r0.setVisibility(r1)
            org.telegram.ui.Adapters.FiltersView r0 = new org.telegram.ui.Adapters.FiltersView
            android.app.Activity r1 = r35.getParentActivity()
            r2 = 0
            r0.<init>(r1, r2)
            r10.filtersView = r0
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda41 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda41
            r1.<init>(r10)
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
            int r28 = r3 + 20
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
            r30 = r5 | 80
            r5 = 1082130432(0x40800000, float:4.0)
            if (r4 == 0) goto L_0x05cb
            r31 = 1082130432(0x40800000, float:4.0)
            goto L_0x05cd
        L_0x05cb:
            r31 = 0
        L_0x05cd:
            r32 = 0
            if (r4 == 0) goto L_0x05d4
            r33 = 0
            goto L_0x05d6
        L_0x05d4:
            r33 = 1082130432(0x40800000, float:4.0)
        L_0x05d6:
            r34 = 0
            r29 = r3
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r28, r29, r30, r31, r32, r33, r34)
            r12.addView(r0, r3)
            android.widget.FrameLayout r0 = r10.floatingButtonContainer
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda17 r3 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda17
            r3.<init>(r10)
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
            android.content.res.Resources r3 = r36.getResources()
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
            r1 = 2131625273(0x7f0e0539, float:1.887775E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r15, r1)
            r0.setContentDescription(r1)
            goto L_0x0686
        L_0x066c:
            org.telegram.ui.Components.RLottieImageView r0 = r10.floatingButton
            r1 = 2131558541(0x7f0d008d, float:1.87424E38)
            r3 = 52
            r4 = 52
            r0.setAnimation(r1, r3, r4)
            android.widget.FrameLayout r0 = r10.floatingButtonContainer
            r1 = 2131626367(0x7f0e097f, float:1.8879968E38)
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
            r27 = 56
            goto L_0x06fc
        L_0x06fa:
            r27 = 60
        L_0x06fc:
            if (r6 < r7) goto L_0x0701
            r2 = 56
            goto L_0x0703
        L_0x0701:
            r2 = 60
        L_0x0703:
            float r2 = (float) r2
            r29 = 51
            r30 = 1092616192(0x41200000, float:10.0)
            r31 = 1086324736(0x40CLASSNAME, float:6.0)
            r32 = 1092616192(0x41200000, float:10.0)
            r33 = 0
            r28 = r2
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
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
            android.app.Activity r2 = r35.getParentActivity()
            r4 = 0
            r5 = 0
            r0 = r8
            r1 = r35
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
            java.lang.String r0 = r10.searchString
            if (r0 != 0) goto L_0x0897
            int r0 = r10.initialDialogsType
            if (r0 != 0) goto L_0x0897
            org.telegram.ui.DialogsActivity$22 r0 = new org.telegram.ui.DialogsActivity$22
            r0.<init>(r11)
            r10.updateLayout = r0
            r1 = 0
            r0.setWillNotDraw(r1)
            android.widget.FrameLayout r0 = r10.updateLayout
            r1 = 4
            r0.setVisibility(r1)
            android.widget.FrameLayout r0 = r10.updateLayout
            r1 = 1111490560(0x42400000, float:48.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r0.setTranslationY(r1)
            if (r6 < r7) goto L_0x080b
            android.widget.FrameLayout r0 = r10.updateLayout
            java.lang.String r1 = "listSelectorSDK21"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r2 = 0
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable((int) r1, (java.lang.String) r2)
            r0.setBackground(r1)
        L_0x080b:
            android.widget.FrameLayout r0 = r10.updateLayout
            r1 = 48
            r2 = 83
            r3 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r1, r2)
            r12.addView(r0, r1)
            android.widget.FrameLayout r0 = r10.updateLayout
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda19 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda19
            r1.<init>(r10)
            r0.setOnClickListener(r1)
            org.telegram.ui.Components.RadialProgress2 r0 = new org.telegram.ui.Components.RadialProgress2
            android.widget.FrameLayout r1 = r10.updateLayout
            r0.<init>(r1)
            r10.updateLayoutIcon = r0
            r0.setColors((int) r3, (int) r3, (int) r3, (int) r3)
            org.telegram.ui.Components.RadialProgress2 r0 = r10.updateLayoutIcon
            r1 = 1093664768(0x41300000, float:11.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setCircleRadius(r1)
            org.telegram.ui.Components.RadialProgress2 r0 = r10.updateLayoutIcon
            r0.setAsMini()
            org.telegram.ui.Components.RadialProgress2 r0 = r10.updateLayoutIcon
            r1 = 15
            r2 = 1
            r3 = 0
            r0.setIcon(r1, r2, r3)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r11)
            r10.updateTextView = r0
            r1 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r2, r1)
            android.widget.TextView r0 = r10.updateTextView
            java.lang.String r1 = "fonts/rmedium.ttf"
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r1)
            r0.setTypeface(r1)
            android.widget.TextView r0 = r10.updateTextView
            r1 = 2131624294(0x7f0e0166, float:1.8875764E38)
            java.lang.String r2 = "AppUpdateNow"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.String r1 = r1.toUpperCase()
            r0.setText(r1)
            android.widget.TextView r0 = r10.updateTextView
            r1 = -1
            r0.setTextColor(r1)
            android.widget.TextView r0 = r10.updateTextView
            r1 = 1106247680(0x41var_, float:30.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r2 = 0
            r0.setPadding(r1, r2, r2, r2)
            android.widget.FrameLayout r0 = r10.updateLayout
            android.widget.TextView r1 = r10.updateTextView
            r2 = -2
            r3 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r4 = 17
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3, r4, r5, r6, r7, r8)
            r0.addView(r1, r2)
        L_0x0897:
            r0 = 0
        L_0x0898:
            if (r0 >= r15) goto L_0x08bd
            org.telegram.ui.Components.UndoView[] r1 = r10.undoView
            org.telegram.ui.DialogsActivity$23 r2 = new org.telegram.ui.DialogsActivity$23
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
            goto L_0x0898
        L_0x08bd:
            int r0 = r10.folderId
            if (r0 == 0) goto L_0x090c
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
        L_0x090c:
            boolean r0 = r10.onlySelect
            if (r0 != 0) goto L_0x092c
            int r0 = r10.initialDialogsType
            if (r0 != 0) goto L_0x092c
            org.telegram.ui.DialogsActivity$24 r0 = new org.telegram.ui.DialogsActivity$24
            r0.<init>(r11)
            r10.blurredView = r0
            r1 = 8
            r0.setVisibility(r1)
            android.view.View r0 = r10.blurredView
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            r2 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r1)
            r12.addView(r0, r1)
        L_0x092c:
            android.graphics.Paint r0 = r10.actionBarDefaultPaint
            int r1 = r10.folderId
            if (r1 != 0) goto L_0x0935
            r1 = r23
            goto L_0x0937
        L_0x0935:
            java.lang.String r1 = "actionBarDefaultArchived"
        L_0x0937:
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setColor(r1)
            boolean r0 = r10.inPreviewMode
            if (r0 == 0) goto L_0x09b9
            org.telegram.messenger.UserConfig r0 = r35.getUserConfig()
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
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r23)
            r0.setBackgroundColor(r1)
            org.telegram.ui.Components.FragmentContextView r0 = r10.fragmentContextView
            if (r0 == 0) goto L_0x09b2
            r12.removeView(r0)
        L_0x09b2:
            org.telegram.ui.Components.FragmentContextView r0 = r10.fragmentLocationContextView
            if (r0 == 0) goto L_0x09b9
            r12.removeView(r0)
        L_0x09b9:
            r0 = 0
            r10.searchIsShowed = r0
            r10.updateFilterTabs(r0, r0)
            java.lang.String r1 = r10.searchString
            if (r1 == 0) goto L_0x09cf
            r1 = 1
            r10.showSearch(r1, r0)
            org.telegram.ui.ActionBar.ActionBar r1 = r10.actionBar
            java.lang.String r2 = r10.searchString
            r1.openSearchField(r2, r0)
            goto L_0x09f4
        L_0x09cf:
            r1 = 1
            java.lang.String r2 = r10.initialSearchString
            if (r2 == 0) goto L_0x09f1
            r10.showSearch(r1, r0)
            org.telegram.ui.ActionBar.ActionBar r1 = r10.actionBar
            java.lang.String r2 = r10.initialSearchString
            r1.openSearchField(r2, r0)
            r1 = 0
            r10.initialSearchString = r1
            org.telegram.ui.Components.FilterTabsView r1 = r10.filterTabsView
            if (r1 == 0) goto L_0x09f4
            r2 = 1110441984(0x42300000, float:44.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = -r2
            float r2 = (float) r2
            r1.setTranslationY(r2)
            goto L_0x09f4
        L_0x09f1:
            r10.showSearch(r0, r0)
        L_0x09f4:
            r10.updateMenuButton(r0)
            android.view.View r0 = r10.fragmentView
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.createView(android.content.Context):android.view.View");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(View view) {
        this.filterTabsView.setIsEditing(false);
        showDoneItem(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3() {
        if (this.initialDialogsType != 10) {
            hideFloatingButton(false);
        }
        scrollToTop();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4(ViewPage viewPage, View view, int i) {
        int i2 = this.initialDialogsType;
        if (i2 == 10) {
            onItemLongClick(view, i, 0.0f, 0.0f, viewPage.dialogsType, viewPage.dialogsAdapter);
        } else if ((i2 == 11 || i2 == 13) && i == 1) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("forImport", true);
            bundle.putLongArray("result", new long[]{getUserConfig().getClientUserId()});
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

                public void didFinishChatCreation(GroupCreateFinalActivity groupCreateFinalActivity, long j) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(Long.valueOf(-j));
                    DialogsActivityDelegate access$23700 = DialogsActivity.this.delegate;
                    DialogsActivity.this.removeSelfFromStack();
                    access$23700.didSelectDialogs(DialogsActivity.this, arrayList, (CharSequence) null, true);
                }
            });
            presentFragment(groupCreateFinalActivity);
        } else {
            onItemClick(view, i, viewPage.dialogsAdapter);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$5(View view, int i) {
        if (this.initialDialogsType == 10) {
            onItemLongClick(view, i, 0.0f, 0.0f, -1, this.searchViewPager.dialogsSearchAdapter);
            return;
        }
        onItemClick(view, i, this.searchViewPager.dialogsSearchAdapter);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$6(boolean z, ArrayList arrayList, ArrayList arrayList2, boolean z2) {
        updateFiltersView(z, arrayList, arrayList2, z2, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$7(View view, int i) {
        this.filtersView.cancelClickRunnables(true);
        addSearchFilter(this.filtersView.getFilterAt(i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$8(View view) {
        if (this.initialDialogsType != 10) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("destroyAfterSelect", true);
            presentFragment(new ContactsActivity(bundle));
        } else if (this.delegate != null && !this.selectedDialogs.isEmpty()) {
            this.delegate.didSelectDialogs(this, this.selectedDialogs, (CharSequence) null, false);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$9(View view) {
        if (SharedConfig.isAppUpdateAvailable()) {
            AndroidUtilities.openForView(SharedConfig.pendingAppUpdate.document, true, getParentActivity());
        }
    }

    private void updateAppUpdateViews(boolean z) {
        boolean z2;
        if (this.updateLayout != null) {
            if (SharedConfig.isAppUpdateAvailable()) {
                FileLoader.getAttachFileName(SharedConfig.pendingAppUpdate.document);
                z2 = FileLoader.getPathToAttach(SharedConfig.pendingAppUpdate.document, true).exists();
            } else {
                z2 = false;
            }
            if (z2) {
                if (this.updateLayout.getTag() == null) {
                    AnimatorSet animatorSet = this.updateLayoutAnimator;
                    if (animatorSet != null) {
                        animatorSet.cancel();
                    }
                    this.updateLayout.setVisibility(0);
                    this.updateLayout.setTag(1);
                    if (z) {
                        AnimatorSet animatorSet2 = new AnimatorSet();
                        this.updateLayoutAnimator = animatorSet2;
                        animatorSet2.setDuration(180);
                        this.updateLayoutAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                        this.updateLayoutAnimator.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.updateLayout, View.TRANSLATION_Y, new float[]{0.0f})});
                        this.updateLayoutAnimator.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                AnimatorSet unused = DialogsActivity.this.updateLayoutAnimator = null;
                            }
                        });
                        this.updateLayoutAnimator.start();
                        return;
                    }
                    this.updateLayout.setTranslationY(0.0f);
                }
            } else if (this.updateLayout.getTag() != null) {
                this.updateLayout.setTag((Object) null);
                if (z) {
                    AnimatorSet animatorSet3 = new AnimatorSet();
                    this.updateLayoutAnimator = animatorSet3;
                    animatorSet3.setDuration(180);
                    this.updateLayoutAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    this.updateLayoutAnimator.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.updateLayout, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(48.0f)})});
                    this.updateLayoutAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (DialogsActivity.this.updateLayout.getTag() == null) {
                                DialogsActivity.this.updateLayout.setVisibility(4);
                            }
                            AnimatorSet unused = DialogsActivity.this.updateLayoutAnimator = null;
                        }
                    });
                    this.updateLayoutAnimator.start();
                    return;
                }
                this.updateLayout.setTranslationY((float) AndroidUtilities.dp(48.0f));
                this.updateLayout.setVisibility(4);
            }
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
    /* JADX WARNING: Removed duplicated region for block: B:52:0x009e  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00a5  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x00b2  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateFiltersView(boolean r11, java.util.ArrayList<java.lang.Object> r12, java.util.ArrayList<org.telegram.ui.Adapters.FiltersView.DateData> r13, boolean r14, boolean r15) {
        /*
            r10 = this;
            boolean r0 = r10.searchIsShowed
            if (r0 == 0) goto L_0x00bf
            boolean r0 = r10.onlySelect
            if (r0 == 0) goto L_0x000a
            goto L_0x00bf
        L_0x000a:
            org.telegram.ui.Components.SearchViewPager r0 = r10.searchViewPager
            java.util.ArrayList r0 = r0.getCurrentSearchFilters()
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
        L_0x0016:
            int r7 = r0.size()
            r8 = 1
            if (r2 >= r7) goto L_0x0054
            java.lang.Object r7 = r0.get(r2)
            org.telegram.ui.Adapters.FiltersView$MediaFilterData r7 = (org.telegram.ui.Adapters.FiltersView.MediaFilterData) r7
            boolean r7 = r7.isMedia()
            if (r7 == 0) goto L_0x002b
            r4 = 1
            goto L_0x0051
        L_0x002b:
            java.lang.Object r7 = r0.get(r2)
            org.telegram.ui.Adapters.FiltersView$MediaFilterData r7 = (org.telegram.ui.Adapters.FiltersView.MediaFilterData) r7
            int r7 = r7.filterType
            r9 = 4
            if (r7 != r9) goto L_0x0038
            r5 = 1
            goto L_0x0051
        L_0x0038:
            java.lang.Object r7 = r0.get(r2)
            org.telegram.ui.Adapters.FiltersView$MediaFilterData r7 = (org.telegram.ui.Adapters.FiltersView.MediaFilterData) r7
            int r7 = r7.filterType
            r9 = 6
            if (r7 != r9) goto L_0x0045
            r6 = 1
            goto L_0x0051
        L_0x0045:
            java.lang.Object r7 = r0.get(r2)
            org.telegram.ui.Adapters.FiltersView$MediaFilterData r7 = (org.telegram.ui.Adapters.FiltersView.MediaFilterData) r7
            int r7 = r7.filterType
            r9 = 7
            if (r7 != r9) goto L_0x0051
            r3 = 1
        L_0x0051:
            int r2 = r2 + 1
            goto L_0x0016
        L_0x0054:
            if (r3 == 0) goto L_0x0057
            r14 = 0
        L_0x0057:
            if (r12 == 0) goto L_0x005f
            boolean r0 = r12.isEmpty()
            if (r0 == 0) goto L_0x0069
        L_0x005f:
            if (r13 == 0) goto L_0x0067
            boolean r0 = r13.isEmpty()
            if (r0 == 0) goto L_0x0069
        L_0x0067:
            if (r14 == 0) goto L_0x006b
        L_0x0069:
            r0 = 1
            goto L_0x006c
        L_0x006b:
            r0 = 0
        L_0x006c:
            r2 = 0
            if (r4 != 0) goto L_0x0074
            if (r0 != 0) goto L_0x0074
            if (r11 == 0) goto L_0x0074
            goto L_0x009b
        L_0x0074:
            if (r0 == 0) goto L_0x009b
            if (r12 == 0) goto L_0x0081
            boolean r11 = r12.isEmpty()
            if (r11 != 0) goto L_0x0081
            if (r5 != 0) goto L_0x0081
            goto L_0x0082
        L_0x0081:
            r12 = r2
        L_0x0082:
            if (r13 == 0) goto L_0x008d
            boolean r11 = r13.isEmpty()
            if (r11 != 0) goto L_0x008d
            if (r6 != 0) goto L_0x008d
            goto L_0x008e
        L_0x008d:
            r13 = r2
        L_0x008e:
            if (r12 != 0) goto L_0x0094
            if (r13 != 0) goto L_0x0094
            if (r14 == 0) goto L_0x009b
        L_0x0094:
            org.telegram.ui.Adapters.FiltersView r11 = r10.filtersView
            r11.setUsersAndDates(r12, r13, r14)
            r11 = 1
            goto L_0x009c
        L_0x009b:
            r11 = 0
        L_0x009c:
            if (r11 != 0) goto L_0x00a3
            org.telegram.ui.Adapters.FiltersView r12 = r10.filtersView
            r12.setUsersAndDates(r2, r2, r1)
        L_0x00a3:
            if (r15 != 0) goto L_0x00ae
            org.telegram.ui.Adapters.FiltersView r12 = r10.filtersView
            androidx.recyclerview.widget.RecyclerView$Adapter r12 = r12.getAdapter()
            r12.notifyDataSetChanged()
        L_0x00ae:
            org.telegram.ui.Components.ViewPagerFixed$TabsView r12 = r10.searchTabsView
            if (r12 == 0) goto L_0x00b5
            r12.hide(r11, r8)
        L_0x00b5:
            org.telegram.ui.Adapters.FiltersView r12 = r10.filtersView
            r12.setEnabled(r11)
            org.telegram.ui.Adapters.FiltersView r11 = r10.filtersView
            r11.setVisibility(r1)
        L_0x00bf:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.updateFiltersView(boolean, java.util.ArrayList, java.util.ArrayList, boolean, boolean):void");
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
            updateFiltersView(true, (ArrayList<Object>) null, (ArrayList<FiltersView.DateData>) null, false, true);
        }
    }

    private void createActionMode(String str) {
        if (!this.actionBar.actionModeIsExist(str)) {
            ActionBarMenu createActionMode = this.actionBar.createActionMode(false, str);
            createActionMode.setBackground((Drawable) null);
            NumberTextView numberTextView = new NumberTextView(createActionMode.getContext());
            this.selectedDialogsCountTextView = numberTextView;
            numberTextView.setTextSize(18);
            this.selectedDialogsCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.selectedDialogsCountTextView.setTextColor(Theme.getColor("actionBarActionModeDefaultIcon"));
            createActionMode.addView(this.selectedDialogsCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 72, 0, 0, 0));
            this.selectedDialogsCountTextView.setOnTouchListener(DialogsActivity$$ExternalSyntheticLambda20.INSTANCE);
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
            if (str == null) {
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
                                if (DialogsActivity.this.searchViewPager == null || DialogsActivity.this.searchViewPager.getVisibility() != 0 || !DialogsActivity.this.searchViewPager.actionModeShowing()) {
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
                            if (DialogsActivity.this.getParentActivity() != null) {
                                SharedConfig.appLocked = true;
                                SharedConfig.saveConfig();
                                int[] iArr = new int[2];
                                DialogsActivity.this.passcodeItem.getLocationInWindow(iArr);
                                ((LaunchActivity) DialogsActivity.this.getParentActivity()).showPasscodeActivity(false, true, iArr[0] + (DialogsActivity.this.passcodeItem.getMeasuredWidth() / 2), iArr[1] + (DialogsActivity.this.passcodeItem.getMeasuredHeight() / 2), new DialogsActivity$27$$ExternalSyntheticLambda1(this), new DialogsActivity$27$$ExternalSyntheticLambda0(this));
                                DialogsActivity.this.updatePasscodeButton();
                            }
                        } else if (i2 == 2) {
                            DialogsActivity.this.presentFragment(new ProxyListActivity());
                        } else if (i2 < 10 || i2 >= 13) {
                            if (i2 == 109) {
                                DialogsActivity dialogsActivity = DialogsActivity.this;
                                FiltersListBottomSheet filtersListBottomSheet = new FiltersListBottomSheet(dialogsActivity, dialogsActivity.selectedDialogs);
                                filtersListBottomSheet.setDelegate(new DialogsActivity$27$$ExternalSyntheticLambda2(this));
                                DialogsActivity.this.showDialog(filtersListBottomSheet);
                            } else if (i2 == 110) {
                                MessagesController.DialogFilter dialogFilter = DialogsActivity.this.getMessagesController().dialogFilters.get(DialogsActivity.this.viewPages[0].selectedType);
                                DialogsActivity dialogsActivity2 = DialogsActivity.this;
                                ArrayList<Long> dialogsCount = FiltersListBottomSheet.getDialogsCount(dialogsActivity2, dialogFilter, dialogsActivity2.selectedDialogs, false, false);
                                if ((dialogFilter != null ? dialogFilter.neverShow.size() : 0) + dialogsCount.size() > 100) {
                                    DialogsActivity dialogsActivity3 = DialogsActivity.this;
                                    dialogsActivity3.showDialog(AlertsCreator.createSimpleAlert(dialogsActivity3.getParentActivity(), LocaleController.getString("FilterAddToAlertFullTitle", NUM), LocaleController.getString("FilterAddToAlertFullText", NUM)).create());
                                    return;
                                }
                                if (!dialogsCount.isEmpty()) {
                                    dialogFilter.neverShow.addAll(dialogsCount);
                                    for (int i3 = 0; i3 < dialogsCount.size(); i3++) {
                                        Long l = dialogsCount.get(i3);
                                        dialogFilter.alwaysShow.remove(l);
                                        dialogFilter.pinnedDialogs.delete(l.longValue());
                                    }
                                    FilterCreateActivity.saveFilterToServer(dialogFilter, dialogFilter.flags, dialogFilter.name, dialogFilter.alwaysShow, dialogFilter.neverShow, dialogFilter.pinnedDialogs, false, false, true, false, false, DialogsActivity.this, (Runnable) null);
                                }
                                DialogsActivity.this.getUndoView().showWithAction(dialogsCount.size() == 1 ? dialogsCount.get(0).longValue() : 0, 21, (Object) Integer.valueOf(dialogsCount.size()), (Object) dialogFilter, (Runnable) null, (Runnable) null);
                                DialogsActivity.this.hideActionMode(false);
                            } else if (i2 == 100 || i2 == 101 || i2 == 102 || i2 == 103 || i2 == 104 || i2 == 105 || i2 == 106 || i2 == 107 || i2 == 108) {
                                DialogsActivity dialogsActivity4 = DialogsActivity.this;
                                dialogsActivity4.performSelectedDialogsAction(dialogsActivity4.selectedDialogs, i2, true);
                            }
                        } else if (DialogsActivity.this.getParentActivity() != null) {
                            DialogsActivityDelegate access$23700 = DialogsActivity.this.delegate;
                            LaunchActivity launchActivity = (LaunchActivity) DialogsActivity.this.getParentActivity();
                            launchActivity.switchToAccount(i2 - 10, true);
                            DialogsActivity dialogsActivity5 = new DialogsActivity(DialogsActivity.this.arguments);
                            dialogsActivity5.setDelegate(access$23700);
                            launchActivity.presentFragment(dialogsActivity5, false, true);
                        }
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$onItemClick$0() {
                        DialogsActivity.this.passcodeItem.setAlpha(1.0f);
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$onItemClick$1() {
                        DialogsActivity.this.passcodeItem.setAlpha(0.0f);
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$onItemClick$2(MessagesController.DialogFilter dialogFilter) {
                        ArrayList<Long> arrayList;
                        long j;
                        ArrayList<Long> arrayList2;
                        MessagesController.DialogFilter dialogFilter2 = dialogFilter;
                        DialogsActivity dialogsActivity = DialogsActivity.this;
                        ArrayList<Long> dialogsCount = FiltersListBottomSheet.getDialogsCount(dialogsActivity, dialogFilter2, dialogsActivity.selectedDialogs, true, false);
                        if ((dialogFilter2 != null ? dialogFilter2.alwaysShow.size() : 0) + dialogsCount.size() > 100) {
                            DialogsActivity dialogsActivity2 = DialogsActivity.this;
                            dialogsActivity2.showDialog(AlertsCreator.createSimpleAlert(dialogsActivity2.getParentActivity(), LocaleController.getString("FilterAddToAlertFullTitle", NUM), LocaleController.getString("FilterRemoveFromAlertFullText", NUM)).create());
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
                                j = arrayList2.get(0).longValue();
                            } else {
                                arrayList2 = arrayList;
                                j = 0;
                            }
                            DialogsActivity.this.getUndoView().showWithAction(j, 20, (Object) Integer.valueOf(arrayList2.size()), (Object) dialogFilter, (Runnable) null, (Runnable) null);
                        } else {
                            DialogsActivity.this.presentFragment(new FilterCreateActivity((MessagesController.DialogFilter) null, dialogsCount));
                        }
                        DialogsActivity.this.hideActionMode(true);
                    }
                });
            }
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
                    if (currentTabId2 != this.filterTabsView.getFirstTabId() && SharedConfig.getChatSwipeAction(this.currentAccount) == 5) {
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

    /* JADX WARNING: Removed duplicated region for block: B:117:0x021e  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00b3  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0142  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x01bb  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onResume() {
        /*
            r11 = this;
            super.onResume()
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r11.parentLayout
            boolean r0 = r0.isInPreviewMode()
            r1 = 0
            if (r0 != 0) goto L_0x0022
            android.view.View r0 = r11.blurredView
            if (r0 == 0) goto L_0x0022
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x0022
            android.view.View r0 = r11.blurredView
            r2 = 8
            r0.setVisibility(r2)
            android.view.View r0 = r11.blurredView
            r0.setBackground(r1)
        L_0x0022:
            org.telegram.ui.Components.FilterTabsView r0 = r11.filterTabsView
            r2 = 1
            r3 = 0
            if (r0 == 0) goto L_0x0058
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x0058
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r11.parentLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r0 = r0.getDrawerLayoutContainer()
            org.telegram.ui.DialogsActivity$ViewPage[] r4 = r11.viewPages
            r4 = r4[r3]
            int r4 = r4.selectedType
            org.telegram.ui.Components.FilterTabsView r5 = r11.filterTabsView
            int r5 = r5.getFirstTabId()
            if (r4 == r5) goto L_0x0054
            boolean r4 = r11.searchIsShowed
            if (r4 != 0) goto L_0x0054
            int r4 = r11.currentAccount
            int r4 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r4)
            r5 = 5
            if (r4 == r5) goto L_0x0052
            goto L_0x0054
        L_0x0052:
            r4 = 0
            goto L_0x0055
        L_0x0054:
            r4 = 1
        L_0x0055:
            r0.setAllowOpenDrawerBySwipe(r4)
        L_0x0058:
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r11.viewPages
            if (r0 == 0) goto L_0x006e
            r0 = 0
        L_0x005d:
            org.telegram.ui.DialogsActivity$ViewPage[] r4 = r11.viewPages
            int r5 = r4.length
            if (r0 >= r5) goto L_0x006e
            r4 = r4[r0]
            org.telegram.ui.Adapters.DialogsAdapter r4 = r4.dialogsAdapter
            r4.notifyDataSetChanged()
            int r0 = r0 + 1
            goto L_0x005d
        L_0x006e:
            org.telegram.ui.Components.ChatActivityEnterView r0 = r11.commentView
            if (r0 == 0) goto L_0x0075
            r0.onResume()
        L_0x0075:
            boolean r0 = r11.onlySelect
            if (r0 != 0) goto L_0x0085
            int r0 = r11.folderId
            if (r0 != 0) goto L_0x0085
            org.telegram.messenger.MediaDataController r0 = r11.getMediaDataController()
            r4 = 4
            r0.checkStickers(r4)
        L_0x0085:
            org.telegram.ui.Components.SearchViewPager r0 = r11.searchViewPager
            if (r0 == 0) goto L_0x008c
            r0.onResume()
        L_0x008c:
            boolean r0 = r11.afterSignup
            if (r0 != 0) goto L_0x009b
            org.telegram.messenger.UserConfig r0 = r11.getUserConfig()
            org.telegram.tgnet.TLRPC$TL_help_termsOfService r0 = r0.unacceptedTermsOfService
            if (r0 != 0) goto L_0x0099
            goto L_0x009d
        L_0x0099:
            r0 = 0
            goto L_0x009e
        L_0x009b:
            r11.afterSignup = r3
        L_0x009d:
            r0 = 1
        L_0x009e:
            r4 = 2131624288(0x7f0e0160, float:1.8875751E38)
            java.lang.String r5 = "AppName"
            if (r0 == 0) goto L_0x0142
            boolean r0 = r11.checkPermission
            if (r0 == 0) goto L_0x0142
            boolean r0 = r11.onlySelect
            if (r0 != 0) goto L_0x0142
            int r0 = android.os.Build.VERSION.SDK_INT
            r6 = 23
            if (r0 < r6) goto L_0x0142
            android.app.Activity r6 = r11.getParentActivity()
            if (r6 == 0) goto L_0x01b4
            r11.checkPermission = r3
            java.lang.String r7 = "android.permission.READ_CONTACTS"
            int r8 = r6.checkSelfPermission(r7)
            if (r8 == 0) goto L_0x00c5
            r8 = 1
            goto L_0x00c6
        L_0x00c5:
            r8 = 0
        L_0x00c6:
            r9 = 28
            java.lang.String r10 = "android.permission.WRITE_EXTERNAL_STORAGE"
            if (r0 <= r9) goto L_0x00d0
            boolean r0 = org.telegram.messenger.BuildVars.NO_SCOPED_STORAGE
            if (r0 == 0) goto L_0x00d8
        L_0x00d0:
            int r0 = r6.checkSelfPermission(r10)
            if (r0 == 0) goto L_0x00d8
            r0 = 1
            goto L_0x00d9
        L_0x00d8:
            r0 = 0
        L_0x00d9:
            if (r8 != 0) goto L_0x00dd
            if (r0 == 0) goto L_0x01b4
        L_0x00dd:
            r11.askingForPermissions = r2
            if (r8 == 0) goto L_0x0107
            boolean r8 = r11.askAboutContacts
            if (r8 == 0) goto L_0x0107
            org.telegram.messenger.UserConfig r8 = r11.getUserConfig()
            boolean r8 = r8.syncContacts
            if (r8 == 0) goto L_0x0107
            boolean r7 = r6.shouldShowRequestPermissionRationale(r7)
            if (r7 == 0) goto L_0x0107
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda37 r0 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda37
            r0.<init>(r11)
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = org.telegram.ui.Components.AlertsCreator.createContactsPermissionDialog(r6, r0)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r11.permissionDialog = r0
            r11.showDialog(r0)
            goto L_0x01b4
        L_0x0107:
            if (r0 == 0) goto L_0x013e
            boolean r0 = r6.shouldShowRequestPermissionRationale(r10)
            if (r0 == 0) goto L_0x013e
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r0.<init>((android.content.Context) r6)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setTitle(r4)
            r4 = 2131627028(0x7f0e0CLASSNAME, float:1.8881309E38)
            java.lang.String r5 = "PermissionStorage"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setMessage(r4)
            r4 = 2131626641(0x7f0e0a91, float:1.8880524E38)
            java.lang.String r5 = "OK"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setPositiveButton(r4, r1)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r11.permissionDialog = r0
            r11.showDialog(r0)
            goto L_0x01b4
        L_0x013e:
            r11.askForPermissons(r2)
            goto L_0x01b4
        L_0x0142:
            boolean r0 = r11.onlySelect
            if (r0 != 0) goto L_0x01b4
            boolean r0 = org.telegram.messenger.XiaomiUtilities.isMIUI()
            if (r0 == 0) goto L_0x01b4
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 19
            if (r0 < r1) goto L_0x01b4
            r0 = 10020(0x2724, float:1.4041E-41)
            boolean r0 = org.telegram.messenger.XiaomiUtilities.isCustomPermissionGranted(r0)
            if (r0 != 0) goto L_0x01b4
            android.app.Activity r0 = r11.getParentActivity()
            if (r0 != 0) goto L_0x0161
            return
        L_0x0161:
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalNotificationsSettings()
            java.lang.String r1 = "askedAboutMiuiLockscreen"
            boolean r0 = r0.getBoolean(r1, r3)
            if (r0 == 0) goto L_0x016e
            return
        L_0x016e:
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r1 = r11.getParentActivity()
            r0.<init>((android.content.Context) r1)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = r0.setTitle(r1)
            r1 = 2131627029(0x7f0e0CLASSNAME, float:1.888131E38)
            java.lang.String r4 = "PermissionXiaomiLockscreen"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = r0.setMessage(r1)
            r1 = 2131627027(0x7f0e0CLASSNAME, float:1.8881307E38)
            java.lang.String r4 = "PermissionOpenSettings"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda9 r4 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda9
            r4.<init>(r11)
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = r0.setPositiveButton(r1, r4)
            r1 = 2131625042(0x7f0e0452, float:1.887728E38)
            java.lang.String r4 = "ContactsPermissionAlertNotNow"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda14 r4 = org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda14.INSTANCE
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = r0.setNegativeButton(r1, r4)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r11.showDialog(r0)
        L_0x01b4:
            r11.showFiltersHint()
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r11.viewPages
            if (r0 == 0) goto L_0x020f
            r0 = 0
        L_0x01bc:
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r11.viewPages
            int r4 = r1.length
            if (r0 >= r4) goto L_0x020f
            r1 = r1[r0]
            int r1 = r1.dialogsType
            if (r1 != 0) goto L_0x01f3
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r11.viewPages
            r1 = r1[r0]
            int r1 = r1.archivePullViewState
            r4 = 2
            if (r1 != r4) goto L_0x01f3
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r11.viewPages
            r1 = r1[r0]
            androidx.recyclerview.widget.LinearLayoutManager r1 = r1.layoutManager
            int r1 = r1.findFirstVisibleItemPosition()
            if (r1 != 0) goto L_0x01f3
            boolean r1 = r11.hasHiddenArchive()
            if (r1 == 0) goto L_0x01f3
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r11.viewPages
            r1 = r1[r0]
            androidx.recyclerview.widget.LinearLayoutManager r1 = r1.layoutManager
            r1.scrollToPositionWithOffset(r2, r3)
        L_0x01f3:
            if (r0 != 0) goto L_0x0201
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r11.viewPages
            r1 = r1[r0]
            org.telegram.ui.Adapters.DialogsAdapter r1 = r1.dialogsAdapter
            r1.resume()
            goto L_0x020c
        L_0x0201:
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r11.viewPages
            r1 = r1[r0]
            org.telegram.ui.Adapters.DialogsAdapter r1 = r1.dialogsAdapter
            r1.pause()
        L_0x020c:
            int r0 = r0 + 1
            goto L_0x01bc
        L_0x020f:
            r11.showNextSupportedSuggestion()
            org.telegram.ui.DialogsActivity$28 r0 = new org.telegram.ui.DialogsActivity$28
            r0.<init>()
            org.telegram.ui.Components.Bulletin.addDelegate((org.telegram.ui.ActionBar.BaseFragment) r11, (org.telegram.ui.Components.Bulletin.Delegate) r0)
            boolean r0 = r11.searchIsShowed
            if (r0 == 0) goto L_0x0227
            android.app.Activity r0 = r11.getParentActivity()
            int r1 = r11.classGuid
            org.telegram.messenger.AndroidUtilities.requestAdjustResize(r0, r1)
        L_0x0227:
            r11.updateVisibleRows(r3, r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.onResume():void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onResume$11(int i) {
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
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$onResume$12(android.content.DialogInterface r2, int r3) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.lambda$onResume$12(android.content.DialogInterface, int):void");
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
                hideActionMode(true);
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
            } else if (view instanceof ProfileSearchCell) {
                ((ProfileSearchCell) view).setChecked(false, true);
            }
            return false;
        }
        this.selectedDialogs.add(Long.valueOf(j));
        if (view instanceof DialogCell) {
            ((DialogCell) view).setChecked(true, true);
        } else if (view instanceof ProfileSearchCell) {
            ((ProfileSearchCell) view).setChecked(true, true);
        }
        return true;
    }

    public void search(String str, boolean z) {
        showSearch(true, z);
        this.actionBar.openSearchField(str, false);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v3, resolved type: boolean} */
    /* JADX WARNING: type inference failed for: r8v0 */
    /* JADX WARNING: type inference failed for: r8v2 */
    /* JADX WARNING: type inference failed for: r8v4 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showSearch(boolean r19, boolean r20) {
        /*
            r18 = this;
            r6 = r18
            r7 = r19
            int r0 = r6.initialDialogsType
            r8 = 0
            if (r0 == 0) goto L_0x000e
            r1 = 3
            if (r0 == r1) goto L_0x000e
            r0 = 0
            goto L_0x0010
        L_0x000e:
            r0 = r20
        L_0x0010:
            android.animation.AnimatorSet r1 = r6.searchAnimator
            r9 = 0
            if (r1 == 0) goto L_0x001a
            r1.cancel()
            r6.searchAnimator = r9
        L_0x001a:
            android.animation.Animator r1 = r6.tabsAlphaAnimator
            if (r1 == 0) goto L_0x0023
            r1.cancel()
            r6.tabsAlphaAnimator = r9
        L_0x0023:
            r6.searchIsShowed = r7
            r1 = 1110441984(0x42300000, float:44.0)
            r10 = -1
            r11 = 1
            if (r7 == 0) goto L_0x0112
            boolean r2 = r6.searchFiltersWasShowed
            if (r2 == 0) goto L_0x0031
            r2 = 0
            goto L_0x0035
        L_0x0031:
            boolean r2 = r18.onlyDialogsAdapter()
        L_0x0035:
            org.telegram.ui.Components.SearchViewPager r3 = r6.searchViewPager
            r3.showOnlyDialogsAdapter(r2)
            r3 = r2 ^ 1
            r6.whiteActionBar = r3
            if (r3 == 0) goto L_0x0042
            r6.searchFiltersWasShowed = r11
        L_0x0042:
            android.view.View r3 = r6.fragmentView
            org.telegram.ui.DialogsActivity$ContentView r3 = (org.telegram.ui.DialogsActivity.ContentView) r3
            org.telegram.ui.Components.ViewPagerFixed$TabsView r4 = r6.searchTabsView
            if (r4 != 0) goto L_0x0082
            if (r2 != 0) goto L_0x0082
            org.telegram.ui.Components.SearchViewPager r2 = r6.searchViewPager
            org.telegram.ui.Components.ViewPagerFixed$TabsView r2 = r2.createTabsView()
            r6.searchTabsView = r2
            org.telegram.ui.Adapters.FiltersView r2 = r6.filtersView
            if (r2 == 0) goto L_0x006b
            r2 = 0
        L_0x0059:
            int r4 = r3.getChildCount()
            if (r2 >= r4) goto L_0x006b
            android.view.View r4 = r3.getChildAt(r2)
            org.telegram.ui.Adapters.FiltersView r5 = r6.filtersView
            if (r4 != r5) goto L_0x0068
            goto L_0x006c
        L_0x0068:
            int r2 = r2 + 1
            goto L_0x0059
        L_0x006b:
            r2 = -1
        L_0x006c:
            if (r2 <= 0) goto L_0x0078
            org.telegram.ui.Components.ViewPagerFixed$TabsView r4 = r6.searchTabsView
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r1)
            r3.addView(r4, r2, r5)
            goto L_0x0097
        L_0x0078:
            org.telegram.ui.Components.ViewPagerFixed$TabsView r2 = r6.searchTabsView
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r1)
            r3.addView(r2, r4)
            goto L_0x0097
        L_0x0082:
            if (r4 == 0) goto L_0x0097
            if (r2 == 0) goto L_0x0097
            android.view.ViewParent r2 = r4.getParent()
            boolean r3 = r2 instanceof android.view.ViewGroup
            if (r3 == 0) goto L_0x0095
            android.view.ViewGroup r2 = (android.view.ViewGroup) r2
            org.telegram.ui.Components.ViewPagerFixed$TabsView r3 = r6.searchTabsView
            r2.removeView(r3)
        L_0x0095:
            r6.searchTabsView = r9
        L_0x0097:
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r6.searchItem
            org.telegram.ui.Components.EditTextBoldCursor r2 = r2.getSearchField()
            boolean r3 = r6.whiteActionBar
            if (r3 == 0) goto L_0x00bd
            java.lang.String r3 = "windowBackgroundWhiteBlackText"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setTextColor(r3)
            java.lang.String r3 = "player_time"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setHintTextColor(r3)
            java.lang.String r3 = "chat_messagePanelCursor"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setCursorColor(r3)
            goto L_0x00d6
        L_0x00bd:
            java.lang.String r3 = "actionBarDefaultSearch"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setCursorColor(r4)
            java.lang.String r4 = "actionBarDefaultSearchPlaceholder"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r2.setHintTextColor(r4)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setTextColor(r3)
        L_0x00d6:
            org.telegram.ui.Components.SearchViewPager r2 = r6.searchViewPager
            android.view.View r3 = r6.fragmentView
            org.telegram.ui.DialogsActivity$ContentView r3 = (org.telegram.ui.DialogsActivity.ContentView) r3
            int r3 = r3.getKeyboardHeight()
            r2.setKeyboardHeight(r3)
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r6.parentLayout
            org.telegram.ui.ActionBar.DrawerLayoutContainer r2 = r2.getDrawerLayoutContainer()
            r2.setAllowOpenDrawerBySwipe(r11)
            org.telegram.ui.Components.SearchViewPager r2 = r6.searchViewPager
            r2.clear()
            int r2 = r6.folderId
            if (r2 == 0) goto L_0x013e
            org.telegram.ui.Adapters.FiltersView$MediaFilterData r2 = new org.telegram.ui.Adapters.FiltersView$MediaFilterData
            r13 = 2131165343(0x7var_f, float:1.79449E38)
            r14 = 2131165343(0x7var_f, float:1.79449E38)
            r3 = 2131624315(0x7f0e017b, float:1.8875806E38)
            java.lang.String r4 = "ArchiveSearchFilter"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r16 = 0
            r17 = 7
            r12 = r2
            r12.<init>(r13, r14, r15, r16, r17)
            r6.addSearchFilter(r2)
            goto L_0x013e
        L_0x0112:
            org.telegram.ui.Components.FilterTabsView r2 = r6.filterTabsView
            if (r2 == 0) goto L_0x013e
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r6.parentLayout
            if (r2 == 0) goto L_0x013e
            org.telegram.ui.ActionBar.DrawerLayoutContainer r2 = r2.getDrawerLayoutContainer()
            org.telegram.ui.DialogsActivity$ViewPage[] r3 = r6.viewPages
            r3 = r3[r8]
            int r3 = r3.selectedType
            org.telegram.ui.Components.FilterTabsView r4 = r6.filterTabsView
            int r4 = r4.getFirstTabId()
            if (r3 == r4) goto L_0x013a
            int r3 = r6.currentAccount
            int r3 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r3)
            r4 = 5
            if (r3 == r4) goto L_0x0138
            goto L_0x013a
        L_0x0138:
            r3 = 0
            goto L_0x013b
        L_0x013a:
            r3 = 1
        L_0x013b:
            r2.setAllowOpenDrawerBySwipe(r3)
        L_0x013e:
            if (r0 == 0) goto L_0x0154
            org.telegram.ui.Components.SearchViewPager r2 = r6.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r2 = r2.dialogsSearchAdapter
            boolean r2 = r2.hasRecentSearch()
            if (r2 == 0) goto L_0x0154
            android.app.Activity r2 = r18.getParentActivity()
            int r3 = r6.classGuid
            org.telegram.messenger.AndroidUtilities.setAdjustResizeToNothing(r2, r3)
            goto L_0x015d
        L_0x0154:
            android.app.Activity r2 = r18.getParentActivity()
            int r3 = r6.classGuid
            org.telegram.messenger.AndroidUtilities.requestAdjustResize(r2, r3)
        L_0x015d:
            if (r7 != 0) goto L_0x016a
            org.telegram.ui.Components.FilterTabsView r2 = r6.filterTabsView
            if (r2 == 0) goto L_0x016a
            boolean r3 = r6.canShowFilterTabsView
            if (r3 == 0) goto L_0x016a
            r2.setVisibility(r8)
        L_0x016a:
            r12 = 1063675494(0x3var_, float:0.9)
            r13 = 0
            r14 = 1065353216(0x3var_, float:1.0)
            if (r0 == 0) goto L_0x036c
            if (r7 == 0) goto L_0x0195
            org.telegram.ui.Components.SearchViewPager r0 = r6.searchViewPager
            r0.setVisibility(r8)
            org.telegram.ui.Components.SearchViewPager r0 = r6.searchViewPager
            r0.reset()
            r1 = 1
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r0 = r18
            r0.updateFiltersView(r1, r2, r3, r4, r5)
            org.telegram.ui.Components.ViewPagerFixed$TabsView r0 = r6.searchTabsView
            if (r0 == 0) goto L_0x01a7
            r0.hide(r8, r8)
            org.telegram.ui.Components.ViewPagerFixed$TabsView r0 = r6.searchTabsView
            r0.setVisibility(r8)
            goto L_0x01a7
        L_0x0195:
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r6.viewPages
            r0 = r0[r8]
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r0.listView
            r0.setVisibility(r8)
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r6.viewPages
            r0 = r0[r8]
            r0.setVisibility(r8)
        L_0x01a7:
            r6.setDialogsListFrozen(r11)
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r6.viewPages
            r0 = r0[r8]
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r0.listView
            r0.setVerticalScrollBarEnabled(r8)
            org.telegram.ui.Components.SearchViewPager r0 = r6.searchViewPager
            java.lang.String r1 = "windowBackgroundWhite"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setBackgroundColor(r2)
            android.animation.AnimatorSet r0 = new android.animation.AnimatorSet
            r0.<init>()
            r6.searchAnimator = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r6.viewPages
            r2 = r2[r8]
            android.util.Property r3 = android.view.View.ALPHA
            float[] r4 = new float[r11]
            if (r7 == 0) goto L_0x01d8
            r5 = 0
            goto L_0x01da
        L_0x01d8:
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x01da:
            r4[r8] = r5
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r3, r4)
            r0.add(r2)
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r6.viewPages
            r2 = r2[r8]
            android.util.Property r3 = android.view.View.SCALE_X
            float[] r4 = new float[r11]
            if (r7 == 0) goto L_0x01f1
            r5 = 1063675494(0x3var_, float:0.9)
            goto L_0x01f3
        L_0x01f1:
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x01f3:
            r4[r8] = r5
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r3, r4)
            r0.add(r2)
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r6.viewPages
            r2 = r2[r8]
            android.util.Property r3 = android.view.View.SCALE_Y
            float[] r4 = new float[r11]
            if (r7 == 0) goto L_0x0207
            goto L_0x0209
        L_0x0207:
            r12 = 1065353216(0x3var_, float:1.0)
        L_0x0209:
            r4[r8] = r12
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r3, r4)
            r0.add(r2)
            org.telegram.ui.Components.SearchViewPager r2 = r6.searchViewPager
            android.util.Property r3 = android.view.View.ALPHA
            float[] r4 = new float[r11]
            if (r7 == 0) goto L_0x021d
            r5 = 1065353216(0x3var_, float:1.0)
            goto L_0x021e
        L_0x021d:
            r5 = 0
        L_0x021e:
            r4[r8] = r5
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r3, r4)
            r0.add(r2)
            org.telegram.ui.Components.SearchViewPager r2 = r6.searchViewPager
            android.util.Property r3 = android.view.View.SCALE_X
            float[] r4 = new float[r11]
            r5 = 1065772646(0x3var_, float:1.05)
            if (r7 == 0) goto L_0x0235
            r12 = 1065353216(0x3var_, float:1.0)
            goto L_0x0238
        L_0x0235:
            r12 = 1065772646(0x3var_, float:1.05)
        L_0x0238:
            r4[r8] = r12
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r3, r4)
            r0.add(r2)
            org.telegram.ui.Components.SearchViewPager r2 = r6.searchViewPager
            android.util.Property r3 = android.view.View.SCALE_Y
            float[] r4 = new float[r11]
            if (r7 == 0) goto L_0x024b
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x024b:
            r4[r8] = r5
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r3, r4)
            r0.add(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r6.passcodeItem
            if (r2 == 0) goto L_0x026f
            org.telegram.ui.Components.RLottieImageView r2 = r2.getIconView()
            android.util.Property r3 = android.view.View.ALPHA
            float[] r4 = new float[r11]
            if (r7 == 0) goto L_0x0264
            r5 = 0
            goto L_0x0266
        L_0x0264:
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x0266:
            r4[r8] = r5
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r3, r4)
            r0.add(r2)
        L_0x026f:
            org.telegram.ui.Components.FilterTabsView r2 = r6.filterTabsView
            r3 = 100
            if (r2 == 0) goto L_0x029f
            int r2 = r2.getVisibility()
            if (r2 != 0) goto L_0x029f
            org.telegram.ui.Components.FilterTabsView r2 = r6.filterTabsView
            org.telegram.ui.Components.RecyclerListView r2 = r2.getTabsContainer()
            android.util.Property r5 = android.view.View.ALPHA
            float[] r12 = new float[r11]
            if (r7 == 0) goto L_0x0289
            r15 = 0
            goto L_0x028b
        L_0x0289:
            r15 = 1065353216(0x3var_, float:1.0)
        L_0x028b:
            r12[r8] = r15
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r5, r12)
            android.animation.ObjectAnimator r2 = r2.setDuration(r3)
            r6.tabsAlphaAnimator = r2
            org.telegram.ui.DialogsActivity$29 r5 = new org.telegram.ui.DialogsActivity$29
            r5.<init>()
            r2.addListener(r5)
        L_0x029f:
            r2 = 2
            float[] r2 = new float[r2]
            float r5 = r6.searchAnimationProgress
            r2[r8] = r5
            if (r7 == 0) goto L_0x02aa
            r13 = 1065353216(0x3var_, float:1.0)
        L_0x02aa:
            r2[r11] = r13
            android.animation.ValueAnimator r2 = android.animation.ValueAnimator.ofFloat(r2)
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda3 r5 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda3
            r5.<init>(r6)
            r2.addUpdateListener(r5)
            r0.add(r2)
            android.animation.AnimatorSet r2 = r6.searchAnimator
            r2.playTogether(r0)
            android.animation.AnimatorSet r0 = r6.searchAnimator
            r14 = 180(0xb4, double:8.9E-322)
            if (r7 == 0) goto L_0x02c9
            r12 = 200(0xc8, double:9.9E-322)
            goto L_0x02ca
        L_0x02c9:
            r12 = r14
        L_0x02ca:
            r0.setDuration(r12)
            android.animation.AnimatorSet r0 = r6.searchAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
            r0.setInterpolator(r2)
            boolean r0 = r6.filterTabsViewIsVisible
            if (r0 == 0) goto L_0x0321
            int r0 = r6.folderId
            if (r0 != 0) goto L_0x02df
            java.lang.String r0 = "actionBarDefault"
            goto L_0x02e1
        L_0x02df:
            java.lang.String r0 = "actionBarDefaultArchived"
        L_0x02e1:
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            int r2 = android.graphics.Color.red(r0)
            int r5 = android.graphics.Color.red(r1)
            int r2 = r2 - r5
            int r2 = java.lang.Math.abs(r2)
            int r5 = android.graphics.Color.green(r0)
            int r12 = android.graphics.Color.green(r1)
            int r5 = r5 - r12
            int r5 = java.lang.Math.abs(r5)
            int r2 = r2 + r5
            int r0 = android.graphics.Color.blue(r0)
            int r1 = android.graphics.Color.blue(r1)
            int r0 = r0 - r1
            int r0 = java.lang.Math.abs(r0)
            int r2 = r2 + r0
            float r0 = (float) r2
            r1 = 1132396544(0x437var_, float:255.0)
            float r0 = r0 / r1
            r1 = 1050253722(0x3e99999a, float:0.3)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x031e
            r8 = 1
        L_0x031e:
            r6.searchAnimationTabsDelayedCrossfade = r8
            goto L_0x0323
        L_0x0321:
            r6.searchAnimationTabsDelayedCrossfade = r11
        L_0x0323:
            if (r7 != 0) goto L_0x0348
            android.animation.AnimatorSet r0 = r6.searchAnimator
            r1 = 20
            r0.setStartDelay(r1)
            android.animation.Animator r0 = r6.tabsAlphaAnimator
            if (r0 == 0) goto L_0x0348
            boolean r1 = r6.searchAnimationTabsDelayedCrossfade
            if (r1 == 0) goto L_0x033f
            r1 = 80
            r0.setStartDelay(r1)
            android.animation.Animator r0 = r6.tabsAlphaAnimator
            r0.setDuration(r3)
            goto L_0x0348
        L_0x033f:
            if (r7 == 0) goto L_0x0344
            r12 = 200(0xc8, double:9.9E-322)
            goto L_0x0345
        L_0x0344:
            r12 = r14
        L_0x0345:
            r0.setDuration(r12)
        L_0x0348:
            android.animation.AnimatorSet r0 = r6.searchAnimator
            org.telegram.ui.DialogsActivity$30 r1 = new org.telegram.ui.DialogsActivity$30
            r1.<init>(r7)
            r0.addListener(r1)
            org.telegram.messenger.NotificationCenter r0 = r18.getNotificationCenter()
            int r1 = r6.animationIndex
            int r0 = r0.setAnimationInProgress(r1, r9)
            r6.animationIndex = r0
            android.animation.AnimatorSet r0 = r6.searchAnimator
            r0.start()
            android.animation.Animator r0 = r6.tabsAlphaAnimator
            if (r0 == 0) goto L_0x0430
            r0.start()
            goto L_0x0430
        L_0x036c:
            r6.setDialogsListFrozen(r8)
            if (r7 == 0) goto L_0x037d
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r6.viewPages
            r0 = r0[r8]
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r0.listView
            r0.hide()
            goto L_0x0388
        L_0x037d:
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r6.viewPages
            r0 = r0[r8]
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r0.listView
            r0.show()
        L_0x0388:
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r6.viewPages
            r0 = r0[r8]
            if (r7 == 0) goto L_0x0390
            r2 = 0
            goto L_0x0392
        L_0x0390:
            r2 = 1065353216(0x3var_, float:1.0)
        L_0x0392:
            r0.setAlpha(r2)
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r6.viewPages
            r0 = r0[r8]
            if (r7 == 0) goto L_0x039f
            r2 = 1063675494(0x3var_, float:0.9)
            goto L_0x03a1
        L_0x039f:
            r2 = 1065353216(0x3var_, float:1.0)
        L_0x03a1:
            r0.setScaleX(r2)
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r6.viewPages
            r0 = r0[r8]
            if (r7 == 0) goto L_0x03ab
            goto L_0x03ad
        L_0x03ab:
            r12 = 1065353216(0x3var_, float:1.0)
        L_0x03ad:
            r0.setScaleY(r12)
            org.telegram.ui.Components.SearchViewPager r0 = r6.searchViewPager
            if (r7 == 0) goto L_0x03b7
            r2 = 1065353216(0x3var_, float:1.0)
            goto L_0x03b8
        L_0x03b7:
            r2 = 0
        L_0x03b8:
            r0.setAlpha(r2)
            org.telegram.ui.Adapters.FiltersView r0 = r6.filtersView
            if (r7 == 0) goto L_0x03c2
            r2 = 1065353216(0x3var_, float:1.0)
            goto L_0x03c3
        L_0x03c2:
            r2 = 0
        L_0x03c3:
            r0.setAlpha(r2)
            org.telegram.ui.Components.SearchViewPager r0 = r6.searchViewPager
            r2 = 1066192077(0x3f8ccccd, float:1.1)
            if (r7 == 0) goto L_0x03d0
            r3 = 1065353216(0x3var_, float:1.0)
            goto L_0x03d3
        L_0x03d0:
            r3 = 1066192077(0x3f8ccccd, float:1.1)
        L_0x03d3:
            r0.setScaleX(r3)
            org.telegram.ui.Components.SearchViewPager r0 = r6.searchViewPager
            if (r7 == 0) goto L_0x03dc
            r2 = 1065353216(0x3var_, float:1.0)
        L_0x03dc:
            r0.setScaleY(r2)
            org.telegram.ui.Components.FilterTabsView r0 = r6.filterTabsView
            if (r0 == 0) goto L_0x0407
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x0407
            org.telegram.ui.Components.FilterTabsView r0 = r6.filterTabsView
            if (r7 == 0) goto L_0x03f4
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = -r1
            float r1 = (float) r1
            goto L_0x03f5
        L_0x03f4:
            r1 = 0
        L_0x03f5:
            r0.setTranslationY(r1)
            org.telegram.ui.Components.FilterTabsView r0 = r6.filterTabsView
            org.telegram.ui.Components.RecyclerListView r0 = r0.getTabsContainer()
            if (r7 == 0) goto L_0x0402
            r1 = 0
            goto L_0x0404
        L_0x0402:
            r1 = 1065353216(0x3var_, float:1.0)
        L_0x0404:
            r0.setAlpha(r1)
        L_0x0407:
            org.telegram.ui.Components.FilterTabsView r0 = r6.filterTabsView
            r1 = 8
            if (r0 == 0) goto L_0x041a
            boolean r2 = r6.canShowFilterTabsView
            if (r2 == 0) goto L_0x0417
            if (r7 != 0) goto L_0x0417
            r0.setVisibility(r8)
            goto L_0x041a
        L_0x0417:
            r0.setVisibility(r1)
        L_0x041a:
            org.telegram.ui.Components.SearchViewPager r0 = r6.searchViewPager
            if (r7 == 0) goto L_0x041f
            goto L_0x0421
        L_0x041f:
            r8 = 8
        L_0x0421:
            r0.setVisibility(r8)
            if (r7 == 0) goto L_0x0428
            r13 = 1065353216(0x3var_, float:1.0)
        L_0x0428:
            r6.setSearchAnimationProgress(r13)
            android.view.View r0 = r6.fragmentView
            r0.invalidate()
        L_0x0430:
            int r0 = r6.initialSearchType
            if (r0 < 0) goto L_0x0439
            org.telegram.ui.Components.SearchViewPager r1 = r6.searchViewPager
            r1.setPosition(r0)
        L_0x0439:
            if (r7 != 0) goto L_0x043d
            r6.initialSearchType = r10
        L_0x043d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.showSearch(boolean, boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showSearch$14(ValueAnimator valueAnimator) {
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
                this.filtersTabAnimator.addUpdateListener(new DialogsActivity$$ExternalSyntheticLambda5(this, z3, translationY));
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
    public /* synthetic */ void lambda$updateFilterTabsVisibility$15(boolean z, float f, ValueAnimator valueAnimator) {
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
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda34 r15 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda34
            r8 = r15
            r9 = r14
            r8.<init>(r9, r10, r11, r12, r13)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r15)
        L_0x013c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.checkListLoad(org.telegram.ui.DialogsActivity$ViewPage):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkListLoad$16(boolean z, boolean z2, boolean z3, boolean z4) {
        if (z) {
            getMessagesController().loadDialogs(this.folderId, -1, 100, z2);
        }
        if (z3) {
            getMessagesController().loadDialogs(1, -1, 100, z4);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:103:0x01af  */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x01c1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void onItemClick(android.view.View r17, int r18, androidx.recyclerview.widget.RecyclerView.Adapter r19) {
        /*
            r16 = this;
            r6 = r16
            r0 = r17
            r1 = r18
            r2 = r19
            android.app.Activity r3 = r16.getParentActivity()
            if (r3 != 0) goto L_0x000f
            return
        L_0x000f:
            boolean r3 = r2 instanceof org.telegram.ui.Adapters.DialogsAdapter
            r4 = 0
            r7 = 0
            r5 = 1
            r9 = 0
            if (r3 == 0) goto L_0x010f
            r10 = r2
            org.telegram.ui.Adapters.DialogsAdapter r10 = (org.telegram.ui.Adapters.DialogsAdapter) r10
            int r11 = r10.getDialogsType()
            r12 = 7
            if (r11 == r12) goto L_0x0029
            r13 = 8
            if (r11 != r13) goto L_0x0027
            goto L_0x0029
        L_0x0027:
            r11 = 0
            goto L_0x0038
        L_0x0029:
            org.telegram.messenger.MessagesController r13 = r16.getMessagesController()
            org.telegram.messenger.MessagesController$DialogFilter[] r13 = r13.selectedDialogFilter
            if (r11 != r12) goto L_0x0033
            r11 = 0
            goto L_0x0034
        L_0x0033:
            r11 = 1
        L_0x0034:
            r11 = r13[r11]
            int r11 = r11.id
        L_0x0038:
            org.telegram.tgnet.TLObject r1 = r10.getItem(r1)
            boolean r10 = r1 instanceof org.telegram.tgnet.TLRPC$User
            if (r10 == 0) goto L_0x0047
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            long r12 = r1.id
        L_0x0044:
            r10 = 0
            goto L_0x00db
        L_0x0047:
            boolean r10 = r1 instanceof org.telegram.tgnet.TLRPC$Dialog
            if (r10 == 0) goto L_0x0083
            org.telegram.tgnet.TLRPC$Dialog r1 = (org.telegram.tgnet.TLRPC$Dialog) r1
            int r10 = r1.folder_id
            boolean r12 = r1 instanceof org.telegram.tgnet.TLRPC$TL_dialogFolder
            if (r12 == 0) goto L_0x0075
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            boolean r0 = r0.isActionModeShowed(r4)
            if (r0 == 0) goto L_0x005c
            return
        L_0x005c:
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
        L_0x0075:
            long r12 = r1.id
            org.telegram.ui.ActionBar.ActionBar r1 = r6.actionBar
            boolean r1 = r1.isActionModeShowed(r4)
            if (r1 == 0) goto L_0x00db
            r6.showOrUpdateActionMode(r12, r0)
            return
        L_0x0083:
            boolean r10 = r1 instanceof org.telegram.tgnet.TLRPC$TL_recentMeUrlChat
            if (r10 == 0) goto L_0x008d
            org.telegram.tgnet.TLRPC$TL_recentMeUrlChat r1 = (org.telegram.tgnet.TLRPC$TL_recentMeUrlChat) r1
            long r12 = r1.chat_id
        L_0x008b:
            long r12 = -r12
            goto L_0x0044
        L_0x008d:
            boolean r10 = r1 instanceof org.telegram.tgnet.TLRPC$TL_recentMeUrlUser
            if (r10 == 0) goto L_0x0096
            org.telegram.tgnet.TLRPC$TL_recentMeUrlUser r1 = (org.telegram.tgnet.TLRPC$TL_recentMeUrlUser) r1
            long r12 = r1.user_id
            goto L_0x0044
        L_0x0096:
            boolean r10 = r1 instanceof org.telegram.tgnet.TLRPC$TL_recentMeUrlChatInvite
            if (r10 == 0) goto L_0x00e3
            org.telegram.tgnet.TLRPC$TL_recentMeUrlChatInvite r1 = (org.telegram.tgnet.TLRPC$TL_recentMeUrlChatInvite) r1
            org.telegram.tgnet.TLRPC$ChatInvite r10 = r1.chat_invite
            org.telegram.tgnet.TLRPC$Chat r12 = r10.chat
            if (r12 != 0) goto L_0x00aa
            boolean r13 = r10.channel
            if (r13 == 0) goto L_0x00b8
            boolean r13 = r10.megagroup
            if (r13 != 0) goto L_0x00b8
        L_0x00aa:
            if (r12 == 0) goto L_0x00d4
            boolean r12 = org.telegram.messenger.ChatObject.isChannel(r12)
            if (r12 == 0) goto L_0x00b8
            org.telegram.tgnet.TLRPC$Chat r12 = r10.chat
            boolean r12 = r12.megagroup
            if (r12 == 0) goto L_0x00d4
        L_0x00b8:
            java.lang.String r0 = r1.url
            r1 = 47
            int r1 = r0.indexOf(r1)
            if (r1 <= 0) goto L_0x00c7
            int r1 = r1 + r5
            java.lang.String r0 = r0.substring(r1)
        L_0x00c7:
            org.telegram.ui.Components.JoinGroupAlert r1 = new org.telegram.ui.Components.JoinGroupAlert
            android.app.Activity r2 = r16.getParentActivity()
            r1.<init>(r2, r10, r0, r6)
            r6.showDialog(r1)
            return
        L_0x00d4:
            org.telegram.tgnet.TLRPC$Chat r1 = r10.chat
            if (r1 == 0) goto L_0x00e2
            long r12 = r1.id
            goto L_0x008b
        L_0x00db:
            r13 = r12
            r1 = 0
            r12 = r11
            r11 = r10
            r10 = 0
            goto L_0x01c8
        L_0x00e2:
            return
        L_0x00e3:
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_recentMeUrlStickerSet
            if (r0 == 0) goto L_0x010c
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
            android.app.Activity r1 = r16.getParentActivity()
            r4 = 0
            r5 = 0
            r0 = r7
            r2 = r16
            r0.<init>((android.content.Context) r1, (org.telegram.ui.ActionBar.BaseFragment) r2, (org.telegram.tgnet.TLRPC$InputStickerSet) r3, (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r4, (org.telegram.ui.Components.StickersAlert.StickersAlertDelegate) r5)
            r6.showDialog(r7)
            return
        L_0x010c:
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_recentMeUrlUnknown
            return
        L_0x010f:
            org.telegram.ui.Components.SearchViewPager r10 = r6.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r10 = r10.dialogsSearchAdapter
            if (r2 != r10) goto L_0x01c3
            java.lang.Object r10 = r10.getItem(r1)
            org.telegram.ui.Components.SearchViewPager r11 = r6.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r11 = r11.dialogsSearchAdapter
            boolean r1 = r11.isGlobalSearch(r1)
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC$User
            if (r11 == 0) goto L_0x0135
            org.telegram.tgnet.TLRPC$User r10 = (org.telegram.tgnet.TLRPC$User) r10
            long r11 = r10.id
            boolean r13 = r6.onlySelect
            if (r13 != 0) goto L_0x0131
            r6.searchDialogId = r11
            r6.searchObject = r10
        L_0x0131:
            r12 = r11
        L_0x0132:
            r10 = 0
            goto L_0x01a3
        L_0x0135:
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r11 == 0) goto L_0x0147
            org.telegram.tgnet.TLRPC$Chat r10 = (org.telegram.tgnet.TLRPC$Chat) r10
            long r11 = r10.id
            long r11 = -r11
            boolean r13 = r6.onlySelect
            if (r13 != 0) goto L_0x0131
            r6.searchDialogId = r11
            r6.searchObject = r10
            goto L_0x0131
        L_0x0147:
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC$EncryptedChat
            if (r11 == 0) goto L_0x015d
            org.telegram.tgnet.TLRPC$EncryptedChat r10 = (org.telegram.tgnet.TLRPC$EncryptedChat) r10
            int r11 = r10.id
            long r11 = (long) r11
            long r11 = org.telegram.messenger.DialogObject.makeEncryptedDialogId(r11)
            boolean r13 = r6.onlySelect
            if (r13 != 0) goto L_0x0131
            r6.searchDialogId = r11
            r6.searchObject = r10
            goto L_0x0131
        L_0x015d:
            boolean r11 = r10 instanceof org.telegram.messenger.MessageObject
            if (r11 == 0) goto L_0x0178
            org.telegram.messenger.MessageObject r10 = (org.telegram.messenger.MessageObject) r10
            long r11 = r10.getDialogId()
            int r10 = r10.getId()
            org.telegram.ui.Components.SearchViewPager r13 = r6.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r13 = r13.dialogsSearchAdapter
            java.lang.String r14 = r13.getLastSearchString()
            r13.addHashtagsFromMessage(r14)
            r12 = r11
            goto L_0x01a3
        L_0x0178:
            boolean r11 = r10 instanceof java.lang.String
            if (r11 == 0) goto L_0x01a1
            java.lang.String r10 = (java.lang.String) r10
            org.telegram.ui.Components.SearchViewPager r11 = r6.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r11 = r11.dialogsSearchAdapter
            boolean r11 = r11.isHashtagSearch()
            if (r11 == 0) goto L_0x018e
            org.telegram.ui.ActionBar.ActionBar r11 = r6.actionBar
            r11.openSearchField(r10, r9)
            goto L_0x01a1
        L_0x018e:
            java.lang.String r11 = "section"
            boolean r11 = r10.equals(r11)
            if (r11 != 0) goto L_0x01a1
            org.telegram.ui.NewContactActivity r11 = new org.telegram.ui.NewContactActivity
            r11.<init>()
            r11.setInitialPhoneNumber(r10, r5)
            r6.presentFragment(r11)
        L_0x01a1:
            r12 = r7
            goto L_0x0132
        L_0x01a3:
            int r11 = (r12 > r7 ? 1 : (r12 == r7 ? 0 : -1))
            if (r11 == 0) goto L_0x01c1
            org.telegram.ui.ActionBar.ActionBar r11 = r6.actionBar
            boolean r11 = r11.isActionModeShowed()
            if (r11 == 0) goto L_0x01c1
            org.telegram.ui.ActionBar.ActionBar r2 = r6.actionBar
            java.lang.String r3 = "search_dialogs_action_mode"
            boolean r2 = r2.isActionModeShowed(r3)
            if (r2 == 0) goto L_0x01c0
            if (r10 != 0) goto L_0x01c0
            if (r1 != 0) goto L_0x01c0
            r6.showOrUpdateActionMode(r12, r0)
        L_0x01c0:
            return
        L_0x01c1:
            r13 = r12
            goto L_0x01c6
        L_0x01c3:
            r13 = r7
            r1 = 0
            r10 = 0
        L_0x01c6:
            r11 = 0
            r12 = 0
        L_0x01c8:
            int r15 = (r13 > r7 ? 1 : (r13 == r7 ? 0 : -1))
            if (r15 != 0) goto L_0x01cd
            return
        L_0x01cd:
            boolean r7 = r6.onlySelect
            if (r7 == 0) goto L_0x01fc
            boolean r1 = r6.validateSlowModeDialog(r13)
            if (r1 != 0) goto L_0x01d8
            return
        L_0x01d8:
            java.util.ArrayList<java.lang.Long> r1 = r6.selectedDialogs
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x01f7
            boolean r0 = r6.addOrRemoveSelectedDialog(r13, r0)
            org.telegram.ui.Components.SearchViewPager r1 = r6.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r1 = r1.dialogsSearchAdapter
            if (r2 != r1) goto L_0x01f2
            org.telegram.ui.ActionBar.ActionBar r1 = r6.actionBar
            r1.closeSearchField()
            r6.findAndUpdateCheckBox(r13, r0)
        L_0x01f2:
            r16.updateSelectedCount()
            goto L_0x0300
        L_0x01f7:
            r6.didSelectResult(r13, r5, r9)
            goto L_0x0300
        L_0x01fc:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            boolean r7 = org.telegram.messenger.DialogObject.isEncryptedDialog(r13)
            if (r7 == 0) goto L_0x0211
            int r7 = org.telegram.messenger.DialogObject.getEncryptedChatId(r13)
            java.lang.String r8 = "enc_id"
            r0.putInt(r8, r7)
            goto L_0x0244
        L_0x0211:
            boolean r7 = org.telegram.messenger.DialogObject.isUserDialog(r13)
            if (r7 == 0) goto L_0x021d
            java.lang.String r7 = "user_id"
            r0.putLong(r7, r13)
            goto L_0x0244
        L_0x021d:
            if (r10 == 0) goto L_0x023d
            org.telegram.messenger.MessagesController r7 = r16.getMessagesController()
            long r4 = -r13
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r4 = r7.getChat(r4)
            if (r4 == 0) goto L_0x023d
            org.telegram.tgnet.TLRPC$InputChannel r5 = r4.migrated_to
            if (r5 == 0) goto L_0x023d
            java.lang.String r5 = "migrated_to"
            r0.putLong(r5, r13)
            org.telegram.tgnet.TLRPC$InputChannel r4 = r4.migrated_to
            long r4 = r4.channel_id
            long r4 = -r4
            goto L_0x023e
        L_0x023d:
            r4 = r13
        L_0x023e:
            long r4 = -r4
            java.lang.String r7 = "chat_id"
            r0.putLong(r7, r4)
        L_0x0244:
            if (r10 == 0) goto L_0x024c
            java.lang.String r1 = "message_id"
            r0.putInt(r1, r10)
            goto L_0x0262
        L_0x024c:
            if (r1 != 0) goto L_0x0252
            r16.closeSearch()
            goto L_0x0262
        L_0x0252:
            org.telegram.tgnet.TLObject r1 = r6.searchObject
            if (r1 == 0) goto L_0x0262
            org.telegram.ui.Components.SearchViewPager r4 = r6.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r4 = r4.dialogsSearchAdapter
            long r8 = r6.searchDialogId
            r4.putRecentSearch(r8, r1)
            r1 = 0
            r6.searchObject = r1
        L_0x0262:
            java.lang.String r1 = "dialog_folder_id"
            r0.putInt(r1, r11)
            java.lang.String r1 = "dialog_filter_id"
            r0.putInt(r1, r12)
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 == 0) goto L_0x029c
            long r7 = r6.openedDialogId
            int r1 = (r7 > r13 ? 1 : (r7 == r13 ? 0 : -1))
            if (r1 != 0) goto L_0x027f
            org.telegram.ui.Components.SearchViewPager r1 = r6.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r1 = r1.dialogsSearchAdapter
            if (r2 == r1) goto L_0x027f
            return
        L_0x027f:
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r6.viewPages
            if (r1 == 0) goto L_0x0297
            r1 = 0
        L_0x0284:
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r6.viewPages
            int r4 = r2.length
            if (r1 >= r4) goto L_0x0297
            r2 = r2[r1]
            org.telegram.ui.Adapters.DialogsAdapter r2 = r2.dialogsAdapter
            r6.openedDialogId = r13
            r2.setOpenedDialogId(r13)
            int r1 = r1 + 1
            goto L_0x0284
        L_0x0297:
            int r1 = org.telegram.messenger.MessagesController.UPDATE_MASK_SELECT_DIALOG
            r6.updateVisibleRows(r1)
        L_0x029c:
            org.telegram.ui.Components.SearchViewPager r1 = r6.searchViewPager
            boolean r1 = r1.actionModeShowing()
            if (r1 == 0) goto L_0x02a9
            org.telegram.ui.Components.SearchViewPager r1 = r6.searchViewPager
            r1.hideActionMode()
        L_0x02a9:
            java.lang.String r1 = r6.searchString
            if (r1 == 0) goto L_0x02cc
            org.telegram.messenger.MessagesController r1 = r16.getMessagesController()
            boolean r1 = r1.checkCanOpenChat(r0, r6)
            if (r1 == 0) goto L_0x0300
            org.telegram.messenger.NotificationCenter r1 = r16.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.closeChats
            r3 = 0
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r1.postNotificationName(r2, r3)
            org.telegram.ui.ChatActivity r1 = new org.telegram.ui.ChatActivity
            r1.<init>(r0)
            r6.presentFragment(r1)
            goto L_0x0300
        L_0x02cc:
            org.telegram.messenger.MessagesController r1 = r16.getMessagesController()
            boolean r1 = r1.checkCanOpenChat(r0, r6)
            if (r1 == 0) goto L_0x0300
            org.telegram.ui.ChatActivity r1 = new org.telegram.ui.ChatActivity
            r1.<init>(r0)
            if (r3 == 0) goto L_0x02fd
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r13)
            if (r0 == 0) goto L_0x02fd
            org.telegram.messenger.MessagesController r0 = r16.getMessagesController()
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r0 = r0.dialogs_dict
            java.lang.Object r0 = r0.get(r13)
            if (r0 != 0) goto L_0x02fd
            org.telegram.messenger.MediaDataController r0 = r16.getMediaDataController()
            org.telegram.tgnet.TLRPC$Document r0 = r0.getGreetingsSticker()
            if (r0 == 0) goto L_0x02fd
            r2 = 1
            r1.setPreloadedSticker(r0, r2)
        L_0x02fd:
            r6.presentFragment(r1)
        L_0x0300:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.onItemClick(android.view.View, int, androidx.recyclerview.widget.RecyclerView$Adapter):void");
    }

    /* access modifiers changed from: private */
    public boolean onItemLongClick(View view, int i, float f, float f2, int i2, RecyclerView.Adapter adapter) {
        TLRPC$Dialog tLRPC$Dialog;
        String str;
        int i3;
        long j;
        if (getParentActivity() == null) {
            return false;
        }
        if (!this.actionBar.isActionModeShowed() && !AndroidUtilities.isTablet() && !this.onlySelect && (view instanceof DialogCell)) {
            DialogCell dialogCell = (DialogCell) view;
            if (dialogCell.isPointInsideAvatar(f, f2)) {
                return showChatPreview(dialogCell);
            }
        }
        DialogsSearchAdapter dialogsSearchAdapter = this.searchViewPager.dialogsSearchAdapter;
        String str2 = null;
        if (adapter == dialogsSearchAdapter) {
            Object item = dialogsSearchAdapter.getItem(i);
            if (this.searchViewPager.dialogsSearchAdapter.isRecentSearchDisplayed()) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("ClearSearchSingleAlertTitle", NUM));
                if (item instanceof TLRPC$Chat) {
                    TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) item;
                    builder.setMessage(LocaleController.formatString("ClearSearchSingleChatAlertText", NUM, tLRPC$Chat.title));
                    j = -tLRPC$Chat.id;
                } else if (item instanceof TLRPC$User) {
                    TLRPC$User tLRPC$User = (TLRPC$User) item;
                    if (tLRPC$User.id == getUserConfig().clientUserId) {
                        builder.setMessage(LocaleController.formatString("ClearSearchSingleChatAlertText", NUM, LocaleController.getString("SavedMessages", NUM)));
                    } else {
                        builder.setMessage(LocaleController.formatString("ClearSearchSingleUserAlertText", NUM, ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name)));
                    }
                    j = tLRPC$User.id;
                } else if (!(item instanceof TLRPC$EncryptedChat)) {
                    return false;
                } else {
                    TLRPC$EncryptedChat tLRPC$EncryptedChat = (TLRPC$EncryptedChat) item;
                    TLRPC$User user = getMessagesController().getUser(Long.valueOf(tLRPC$EncryptedChat.user_id));
                    builder.setMessage(LocaleController.formatString("ClearSearchSingleUserAlertText", NUM, ContactsController.formatName(user.first_name, user.last_name)));
                    j = DialogObject.makeEncryptedDialogId((long) tLRPC$EncryptedChat.id);
                }
                builder.setPositiveButton(LocaleController.getString("ClearSearchRemove", NUM).toUpperCase(), new DialogsActivity$$ExternalSyntheticLambda12(this, j));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                AlertDialog create = builder.create();
                showDialog(create);
                TextView textView = (TextView) create.getButton(-1);
                if (textView != null) {
                    textView.setTextColor(Theme.getColor("dialogTextRed2"));
                }
                return true;
            }
        }
        DialogsSearchAdapter dialogsSearchAdapter2 = this.searchViewPager.dialogsSearchAdapter;
        if (adapter == dialogsSearchAdapter2) {
            long dialogId = (!(view instanceof ProfileSearchCell) || dialogsSearchAdapter2.isGlobalSearch(i)) ? 0 : ((ProfileSearchCell) view).getDialogId();
            if (dialogId == 0) {
                return false;
            }
            showOrUpdateActionMode(dialogId, view);
            return true;
        }
        ArrayList<TLRPC$Dialog> dialogsArray = getDialogsArray(this.currentAccount, i2, this.folderId, this.dialogsListFrozen);
        int fixPosition = ((DialogsAdapter) adapter).fixPosition(i);
        if (fixPosition < 0 || fixPosition >= dialogsArray.size() || (tLRPC$Dialog = dialogsArray.get(fixPosition)) == null) {
            return false;
        }
        if (this.onlySelect) {
            int i4 = this.initialDialogsType;
            if ((i4 != 3 && i4 != 10) || !validateSlowModeDialog(tLRPC$Dialog.id)) {
                return false;
            }
            addOrRemoveSelectedDialog(tLRPC$Dialog.id, view);
            updateSelectedCount();
        } else if (tLRPC$Dialog instanceof TLRPC$TL_dialogFolder) {
            view.performHapticFeedback(0, 2);
            BottomSheet.Builder builder2 = new BottomSheet.Builder(getParentActivity());
            boolean z = getMessagesStorage().getArchiveUnreadCount() != 0;
            int[] iArr = new int[2];
            iArr[0] = z ? NUM : 0;
            iArr[1] = SharedConfig.archiveHidden ? NUM : NUM;
            CharSequence[] charSequenceArr = new CharSequence[2];
            if (z) {
                str2 = LocaleController.getString("MarkAllAsRead", NUM);
            }
            charSequenceArr[0] = str2;
            if (SharedConfig.archiveHidden) {
                i3 = NUM;
                str = "PinInTheList";
            } else {
                i3 = NUM;
                str = "HideAboveTheList";
            }
            charSequenceArr[1] = LocaleController.getString(str, i3);
            builder2.setItems(charSequenceArr, iArr, new DialogsActivity$$ExternalSyntheticLambda6(this));
            showDialog(builder2.create());
            return false;
        } else if (this.actionBar.isActionModeShowed() && isDialogPinned(tLRPC$Dialog)) {
            return false;
        } else {
            showOrUpdateActionMode(tLRPC$Dialog.id, view);
        }
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onItemLongClick$17(long j, DialogInterface dialogInterface, int i) {
        this.searchViewPager.dialogsSearchAdapter.removeRecentSearch(j);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onItemLongClick$18(DialogInterface dialogInterface, int i) {
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
        int messageId = dialogCell.getMessageId();
        if (DialogObject.isEncryptedDialog(dialogId)) {
            return false;
        }
        if (DialogObject.isUserDialog(dialogId)) {
            bundle.putLong("user_id", dialogId);
        } else {
            if (!(messageId == 0 || (chat = getMessagesController().getChat(Long.valueOf(-dialogId))) == null || chat.migrated_to == null)) {
                bundle.putLong("migrated_to", dialogId);
                dialogId = -chat.migrated_to.channel_id;
            }
            bundle.putLong("chat_id", -dialogId);
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
        this.floatingButtonContainer.setTranslationY(this.floatingButtonTranslation - (Math.max(this.additionalFloatingTranslation, this.additionalFloatingTranslation2) * (1.0f - this.floatingButtonHideProgress)));
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
        AndroidUtilities.runOnUIThread(new DialogsActivity$$ExternalSyntheticLambda26(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDialogAnimationFinished$19() {
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
        ofFloat.addUpdateListener(new DialogsActivity$$ExternalSyntheticLambda4(this));
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
        int i4 = MessagesController.UPDATE_MASK_REORDER | MessagesController.UPDATE_MASK_CHECK;
        if (z) {
            i = MessagesController.UPDATE_MASK_CHAT;
        }
        updateVisibleRows(i4 | i);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$hideActionMode$20(ValueAnimator valueAnimator) {
        this.progressToActionMode = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        for (int i = 0; i < this.actionBar.getChildCount(); i++) {
            if (!(this.actionBar.getChildAt(i).getVisibility() != 0 || this.actionBar.getChildAt(i) == this.actionBar.getActionMode() || this.actionBar.getChildAt(i) == this.actionBar.getBackButton())) {
                this.actionBar.getChildAt(i).setAlpha(1.0f - this.progressToActionMode);
            }
        }
        View view = this.fragmentView;
        if (view != null) {
            view.invalidate();
        }
    }

    private int getPinnedCount() {
        ArrayList<TLRPC$Dialog> arrayList;
        if ((this.viewPages[0].dialogsType == 7 || this.viewPages[0].dialogsType == 8) && (!this.actionBar.isActionModeShowed() || this.actionBar.isActionModeShowed((String) null))) {
            arrayList = getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, this.dialogsListFrozen);
        } else {
            arrayList = getMessagesController().getDialogs(this.folderId);
        }
        int size = arrayList.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            TLRPC$Dialog tLRPC$Dialog = arrayList.get(i2);
            if (!(tLRPC$Dialog instanceof TLRPC$TL_dialogFolder)) {
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
        MessagesController.DialogFilter dialogFilter = null;
        if ((this.viewPages[0].dialogsType == 7 || this.viewPages[0].dialogsType == 8) && (!this.actionBar.isActionModeShowed() || this.actionBar.isActionModeShowed((String) null))) {
            dialogFilter = getMessagesController().selectedDialogFilter[this.viewPages[0].dialogsType == 8 ? (char) 1 : 0];
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
    /* JADX WARNING: Removed duplicated region for block: B:182:0x0416  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void performSelectedDialogsAction(java.util.ArrayList<java.lang.Long> r40, int r41, boolean r42) {
        /*
            r39 = this;
            r13 = r39
            r7 = r40
            r14 = r41
            android.app.Activity r0 = r39.getParentActivity()
            if (r0 != 0) goto L_0x000d
            return
        L_0x000d:
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r13.viewPages
            r15 = 0
            r0 = r0[r15]
            int r0 = r0.dialogsType
            r1 = 7
            r2 = 8
            r8 = 0
            r12 = 1
            if (r0 == r1) goto L_0x0027
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r13.viewPages
            r0 = r0[r15]
            int r0 = r0.dialogsType
            if (r0 != r2) goto L_0x0038
        L_0x0027:
            org.telegram.ui.ActionBar.ActionBar r0 = r13.actionBar
            boolean r0 = r0.isActionModeShowed()
            if (r0 == 0) goto L_0x003a
            org.telegram.ui.ActionBar.ActionBar r0 = r13.actionBar
            boolean r0 = r0.isActionModeShowed(r8)
            if (r0 == 0) goto L_0x0038
            goto L_0x003a
        L_0x0038:
            r0 = 0
            goto L_0x003b
        L_0x003a:
            r0 = 1
        L_0x003b:
            if (r0 == 0) goto L_0x0054
            org.telegram.messenger.MessagesController r1 = r39.getMessagesController()
            org.telegram.messenger.MessagesController$DialogFilter[] r1 = r1.selectedDialogFilter
            org.telegram.ui.DialogsActivity$ViewPage[] r3 = r13.viewPages
            r3 = r3[r15]
            int r3 = r3.dialogsType
            if (r3 != r2) goto L_0x004f
            r2 = 1
            goto L_0x0050
        L_0x004f:
            r2 = 0
        L_0x0050:
            r1 = r1[r2]
            r9 = r1
            goto L_0x0055
        L_0x0054:
            r9 = r8
        L_0x0055:
            int r10 = r40.size()
            r1 = 105(0x69, float:1.47E-43)
            if (r14 == r1) goto L_0x0697
            r1 = 107(0x6b, float:1.5E-43)
            if (r14 != r1) goto L_0x0063
            goto L_0x0697
        L_0x0063:
            java.lang.String r3 = "Cancel"
            r11 = 108(0x6c, float:1.51E-43)
            r6 = 100
            if (r14 == r6) goto L_0x006d
            if (r14 != r11) goto L_0x01c5
        L_0x006d:
            int r11 = r13.canPinCount
            if (r11 == 0) goto L_0x01c5
            org.telegram.messenger.MessagesController r11 = r39.getMessagesController()
            int r4 = r13.folderId
            java.util.ArrayList r4 = r11.getDialogs(r4)
            int r11 = r4.size()
            r2 = 0
            r22 = 0
            r23 = 0
        L_0x0084:
            if (r2 >= r11) goto L_0x00c0
            java.lang.Object r24 = r4.get(r2)
            r1 = r24
            org.telegram.tgnet.TLRPC$Dialog r1 = (org.telegram.tgnet.TLRPC$Dialog) r1
            boolean r5 = r1 instanceof org.telegram.tgnet.TLRPC$TL_dialogFolder
            if (r5 == 0) goto L_0x0095
            r26 = r9
            goto L_0x00ba
        L_0x0095:
            boolean r5 = r13.isDialogPinned(r1)
            if (r5 == 0) goto L_0x00ab
            r26 = r9
            long r8 = r1.id
            boolean r1 = org.telegram.messenger.DialogObject.isEncryptedDialog(r8)
            if (r1 == 0) goto L_0x00a8
            int r23 = r23 + 1
            goto L_0x00ba
        L_0x00a8:
            int r22 = r22 + 1
            goto L_0x00ba
        L_0x00ab:
            r26 = r9
            org.telegram.messenger.MessagesController r5 = r39.getMessagesController()
            long r8 = r1.id
            boolean r1 = r5.isPromoDialog(r8, r15)
            if (r1 != 0) goto L_0x00ba
            goto L_0x00c2
        L_0x00ba:
            int r2 = r2 + 1
            r9 = r26
            r8 = 0
            goto L_0x0084
        L_0x00c0:
            r26 = r9
        L_0x00c2:
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 0
        L_0x00c6:
            if (r1 >= r10) goto L_0x010d
            java.lang.Object r8 = r7.get(r1)
            java.lang.Long r8 = (java.lang.Long) r8
            long r8 = r8.longValue()
            org.telegram.messenger.MessagesController r11 = r39.getMessagesController()
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r11 = r11.dialogs_dict
            java.lang.Object r11 = r11.get(r8)
            org.telegram.tgnet.TLRPC$Dialog r11 = (org.telegram.tgnet.TLRPC$Dialog) r11
            if (r11 == 0) goto L_0x0105
            boolean r11 = r13.isDialogPinned(r11)
            if (r11 == 0) goto L_0x00e7
            goto L_0x0105
        L_0x00e7:
            boolean r11 = org.telegram.messenger.DialogObject.isEncryptedDialog(r8)
            if (r11 == 0) goto L_0x00f0
            int r2 = r2 + 1
            goto L_0x00f2
        L_0x00f0:
            int r4 = r4 + 1
        L_0x00f2:
            if (r26 == 0) goto L_0x0105
            r11 = r26
            java.util.ArrayList<java.lang.Long> r15 = r11.alwaysShow
            java.lang.Long r8 = java.lang.Long.valueOf(r8)
            boolean r8 = r15.contains(r8)
            if (r8 == 0) goto L_0x0107
            int r5 = r5 + 1
            goto L_0x0107
        L_0x0105:
            r11 = r26
        L_0x0107:
            int r1 = r1 + 1
            r26 = r11
            r15 = 0
            goto L_0x00c6
        L_0x010d:
            r11 = r26
            if (r0 == 0) goto L_0x011a
            java.util.ArrayList<java.lang.Long> r0 = r11.alwaysShow
            int r0 = r0.size()
            int r0 = 100 - r0
            goto L_0x012e
        L_0x011a:
            int r0 = r13.folderId
            if (r0 != 0) goto L_0x0128
            if (r11 == 0) goto L_0x0121
            goto L_0x0128
        L_0x0121:
            org.telegram.messenger.MessagesController r0 = r39.getMessagesController()
            int r0 = r0.maxPinnedDialogsCount
            goto L_0x012e
        L_0x0128:
            org.telegram.messenger.MessagesController r0 = r39.getMessagesController()
            int r0 = r0.maxFolderPinnedDialogsCount
        L_0x012e:
            int r2 = r2 + r23
            if (r2 > r0) goto L_0x013b
            int r4 = r4 + r22
            int r4 = r4 - r5
            if (r4 <= r0) goto L_0x0138
            goto L_0x013b
        L_0x0138:
            r4 = -1
            goto L_0x02d1
        L_0x013b:
            int r1 = r13.folderId
            java.lang.String r2 = "Chats"
            if (r1 != 0) goto L_0x0195
            if (r11 == 0) goto L_0x0144
            goto L_0x0195
        L_0x0144:
            org.telegram.ui.ActionBar.AlertDialog$Builder r1 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r3 = r39.getParentActivity()
            r1.<init>((android.content.Context) r3)
            r3 = 2131624288(0x7f0e0160, float:1.8875751E38)
            java.lang.String r4 = "AppName"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setTitle(r3)
            r3 = 2131627077(0x7f0e0CLASSNAME, float:1.8881408E38)
            java.lang.Object[] r4 = new java.lang.Object[r12]
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            r2 = 0
            r4[r2] = r0
            java.lang.String r0 = "PinToTopLimitReached2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            r1.setMessage(r0)
            r0 = 2131625623(0x7f0e0697, float:1.887846E38)
            java.lang.String r2 = "FiltersSetupPinAlert"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda8 r2 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda8
            r2.<init>(r13)
            r1.setNegativeButton(r0, r2)
            r0 = 2131626641(0x7f0e0a91, float:1.8880524E38)
            java.lang.String r2 = "OK"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 0
            r1.setPositiveButton(r0, r2)
            org.telegram.ui.ActionBar.AlertDialog r0 = r1.create()
            r13.showDialog(r0)
            r2 = 0
            goto L_0x01aa
        L_0x0195:
            r1 = 2131627067(0x7f0e0c3b, float:1.8881388E38)
            java.lang.Object[] r3 = new java.lang.Object[r12]
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            r2 = 0
            r3[r2] = r0
            java.lang.String r0 = "PinFolderLimitReached"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            org.telegram.ui.Components.AlertsCreator.showSimpleAlert(r13, r0)
        L_0x01aa:
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r13.pinItem
            r1 = 1073741824(0x40000000, float:2.0)
            org.telegram.messenger.AndroidUtilities.shakeView(r0, r1, r2)
            android.app.Activity r0 = r39.getParentActivity()
            java.lang.String r1 = "vibrator"
            java.lang.Object r0 = r0.getSystemService(r1)
            android.os.Vibrator r0 = (android.os.Vibrator) r0
            if (r0 == 0) goto L_0x01c4
            r1 = 200(0xc8, double:9.9E-322)
            r0.vibrate(r1)
        L_0x01c4:
            return
        L_0x01c5:
            r11 = r9
            r0 = 102(0x66, float:1.43E-43)
            if (r14 == r0) goto L_0x01ce
            r1 = 103(0x67, float:1.44E-43)
            if (r14 != r1) goto L_0x029f
        L_0x01ce:
            if (r10 <= r12) goto L_0x029f
            if (r42 == 0) goto L_0x029f
            org.telegram.ui.ActionBar.AlertDialog$Builder r1 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r2 = r39.getParentActivity()
            r1.<init>((android.content.Context) r2)
            if (r14 != r0) goto L_0x0203
            r0 = 2131625180(0x7f0e04dc, float:1.887756E38)
            java.lang.Object[] r2 = new java.lang.Object[r12]
            java.lang.String r4 = "ChatsSelected"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r10)
            r5 = 0
            r2[r5] = r4
            java.lang.String r4 = "DeleteFewChatsTitle"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r0, r2)
            r1.setTitle(r0)
            r0 = 2131624348(0x7f0e019c, float:1.8875873E38)
            java.lang.String r2 = "AreYouSureDeleteFewChats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r1.setMessage(r0)
        L_0x0200:
            r0 = 102(0x66, float:1.43E-43)
            goto L_0x024f
        L_0x0203:
            int r0 = r13.canClearCacheCount
            if (r0 == 0) goto L_0x022b
            r0 = 2131624950(0x7f0e03f6, float:1.8877094E38)
            java.lang.Object[] r2 = new java.lang.Object[r12]
            java.lang.String r4 = "ChatsSelectedClearCache"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r10)
            r5 = 0
            r2[r5] = r4
            java.lang.String r4 = "ClearCacheFewChatsTitle"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r0, r2)
            r1.setTitle(r0)
            r0 = 2131624336(0x7f0e0190, float:1.8875849E38)
            java.lang.String r2 = "AreYouSureClearHistoryCacheFewChats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r1.setMessage(r0)
            goto L_0x0200
        L_0x022b:
            r0 = 2131624952(0x7f0e03f8, float:1.8877098E38)
            java.lang.Object[] r2 = new java.lang.Object[r12]
            java.lang.String r4 = "ChatsSelectedClear"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r10)
            r5 = 0
            r2[r5] = r4
            java.lang.String r4 = "ClearFewChatsTitle"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r0, r2)
            r1.setTitle(r0)
            r0 = 2131624338(0x7f0e0192, float:1.8875853E38)
            java.lang.String r2 = "AreYouSureClearHistoryFewChats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r1.setMessage(r0)
            goto L_0x0200
        L_0x024f:
            if (r14 != r0) goto L_0x025b
            r0 = 2131625150(0x7f0e04be, float:1.88775E38)
            java.lang.String r2 = "Delete"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0272
        L_0x025b:
            int r0 = r13.canClearCacheCount
            if (r0 == 0) goto L_0x0269
            r0 = 2131624954(0x7f0e03fa, float:1.8877102E38)
            java.lang.String r2 = "ClearHistoryCache"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x0272
        L_0x0269:
            r0 = 2131624953(0x7f0e03f9, float:1.88771E38)
            java.lang.String r2 = "ClearHistory"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x0272:
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda13 r2 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda13
            r2.<init>(r13, r7, r14)
            r1.setPositiveButton(r0, r2)
            r0 = 2131624663(0x7f0e02d7, float:1.8876512E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            r2 = 0
            r1.setNegativeButton(r0, r2)
            org.telegram.ui.ActionBar.AlertDialog r0 = r1.create()
            r13.showDialog(r0)
            r4 = -1
            android.view.View r0 = r0.getButton(r4)
            android.widget.TextView r0 = (android.widget.TextView) r0
            if (r0 == 0) goto L_0x029e
            java.lang.String r1 = "dialogTextRed2"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTextColor(r1)
        L_0x029e:
            return
        L_0x029f:
            r4 = -1
            r0 = 106(0x6a, float:1.49E-43)
            if (r14 != r0) goto L_0x02d1
            if (r42 == 0) goto L_0x02d1
            if (r10 != r12) goto L_0x02c0
            r0 = 0
            java.lang.Object r1 = r7.get(r0)
            java.lang.Long r1 = (java.lang.Long) r1
            long r0 = r1.longValue()
            org.telegram.messenger.MessagesController r2 = r39.getMessagesController()
            java.lang.Long r0 = java.lang.Long.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r8 = r2.getUser(r0)
            goto L_0x02c1
        L_0x02c0:
            r8 = 0
        L_0x02c1:
            int r0 = r13.canReportSpamCount
            if (r0 == 0) goto L_0x02c7
            r15 = 1
            goto L_0x02c8
        L_0x02c7:
            r15 = 0
        L_0x02c8:
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda40 r0 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda40
            r0.<init>(r13, r7)
            org.telegram.ui.Components.AlertsCreator.createBlockDialogAlert(r13, r10, r15, r8, r0)
            return
        L_0x02d1:
            r0 = 2147483647(0x7fffffff, float:NaN)
            if (r11 == 0) goto L_0x02f9
            if (r14 == r6) goto L_0x02dc
            r1 = 108(0x6c, float:1.51E-43)
            if (r14 != r1) goto L_0x02f9
        L_0x02dc:
            int r1 = r13.canPinCount
            if (r1 == 0) goto L_0x02f9
            org.telegram.messenger.support.LongSparseIntArray r1 = r11.pinnedDialogs
            int r1 = r1.size()
            r2 = 0
        L_0x02e7:
            if (r2 >= r1) goto L_0x02f6
            org.telegram.messenger.support.LongSparseIntArray r5 = r11.pinnedDialogs
            int r5 = r5.valueAt(r2)
            int r0 = java.lang.Math.min(r0, r5)
            int r2 = r2 + 1
            goto L_0x02e7
        L_0x02f6:
            int r1 = r13.canPinCount
            int r0 = r0 - r1
        L_0x02f9:
            r8 = r0
            r9 = 0
            r15 = 0
        L_0x02fc:
            if (r9 >= r10) goto L_0x060a
            java.lang.Object r1 = r7.get(r9)
            java.lang.Long r1 = (java.lang.Long) r1
            long r1 = r1.longValue()
            org.telegram.messenger.MessagesController r5 = r39.getMessagesController()
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r5 = r5.dialogs_dict
            java.lang.Object r5 = r5.get(r1)
            org.telegram.tgnet.TLRPC$Dialog r5 = (org.telegram.tgnet.TLRPC$Dialog) r5
            if (r5 != 0) goto L_0x0324
            r29 = r3
            r12 = r13
        L_0x0319:
            r16 = 3
        L_0x031b:
            r20 = -1
            r21 = 2131624663(0x7f0e02d7, float:1.8876512E38)
            r25 = 103(0x67, float:1.44E-43)
            goto L_0x05ff
        L_0x0324:
            boolean r20 = org.telegram.messenger.DialogObject.isEncryptedDialog(r1)
            if (r20 == 0) goto L_0x0354
            org.telegram.messenger.MessagesController r4 = r39.getMessagesController()
            int r22 = org.telegram.messenger.DialogObject.getEncryptedChatId(r1)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r22)
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r4.getEncryptedChat(r0)
            if (r0 == 0) goto L_0x034b
            org.telegram.messenger.MessagesController r4 = r39.getMessagesController()
            long r12 = r0.user_id
            java.lang.Long r12 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r12)
            goto L_0x0350
        L_0x034b:
            org.telegram.tgnet.TLRPC$TL_userEmpty r4 = new org.telegram.tgnet.TLRPC$TL_userEmpty
            r4.<init>()
        L_0x0350:
            r12 = r4
            r13 = 0
            r4 = r0
            goto L_0x037a
        L_0x0354:
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r1)
            if (r0 == 0) goto L_0x036a
            org.telegram.messenger.MessagesController r0 = r39.getMessagesController()
            java.lang.Long r4 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r4)
            r12 = r0
            r4 = 0
            r13 = 0
            goto L_0x037a
        L_0x036a:
            org.telegram.messenger.MessagesController r0 = r39.getMessagesController()
            long r12 = -r1
            java.lang.Long r4 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r4)
            r13 = r0
            r4 = 0
            r12 = 0
        L_0x037a:
            if (r13 != 0) goto L_0x038d
            if (r12 != 0) goto L_0x038d
            r16 = 3
            r20 = -1
            r21 = 2131624663(0x7f0e02d7, float:1.8876512E38)
            r25 = 103(0x67, float:1.44E-43)
            r12 = r39
            r29 = r3
            goto L_0x05ff
        L_0x038d:
            if (r12 == 0) goto L_0x039c
            boolean r0 = r12.bot
            if (r0 == 0) goto L_0x039c
            boolean r0 = org.telegram.messenger.MessagesController.isSupportUser(r12)
            if (r0 != 0) goto L_0x039c
            r23 = 1
            goto L_0x039e
        L_0x039c:
            r23 = 0
        L_0x039e:
            if (r14 == r6) goto L_0x0578
            r0 = 108(0x6c, float:1.51E-43)
            if (r14 != r0) goto L_0x03a6
            goto L_0x0578
        L_0x03a6:
            r0 = 101(0x65, float:1.42E-43)
            if (r14 != r0) goto L_0x0448
            r4 = r39
            int r0 = r4.canReadCount
            if (r0 == 0) goto L_0x0436
            r0 = 2
            r4.debugLastUpdateAction = r0
            if (r11 == 0) goto L_0x03ef
            int r0 = r11.flags
            int r12 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ
            r0 = r0 & r12
            if (r0 == 0) goto L_0x03ef
            int r0 = r4.currentAccount
            boolean r0 = r11.alwaysShow(r0, r5)
            if (r0 != 0) goto L_0x03ef
            r0 = 1
            r4.setDialogsListFrozen(r0)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r0 = r4.frozenDialogsList
            if (r0 == 0) goto L_0x03ef
            r0 = 0
        L_0x03cd:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r12 = r4.frozenDialogsList
            int r12 = r12.size()
            if (r0 >= r12) goto L_0x03e7
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r12 = r4.frozenDialogsList
            java.lang.Object r12 = r12.get(r0)
            org.telegram.tgnet.TLRPC$Dialog r12 = (org.telegram.tgnet.TLRPC$Dialog) r12
            long r12 = r12.id
            int r23 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r23 != 0) goto L_0x03e4
            goto L_0x03e8
        L_0x03e4:
            int r0 = r0 + 1
            goto L_0x03cd
        L_0x03e7:
            r0 = -1
        L_0x03e8:
            if (r0 >= 0) goto L_0x03f0
            r12 = 0
            r4.setDialogsListFrozen(r12, r12)
            goto L_0x03f0
        L_0x03ef:
            r0 = -1
        L_0x03f0:
            org.telegram.messenger.MessagesController r12 = r39.getMessagesController()
            r12.markMentionsAsRead(r1)
            org.telegram.messenger.MessagesController r27 = r39.getMessagesController()
            int r12 = r5.top_message
            int r5 = r5.last_message_date
            r33 = 0
            r34 = 0
            r35 = 0
            r36 = 1
            r37 = 0
            r28 = r1
            r30 = r12
            r31 = r12
            r32 = r5
            r27.markDialogAsRead(r28, r30, r31, r32, r33, r34, r35, r36, r37)
            if (r0 < 0) goto L_0x0443
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r1 = r4.frozenDialogsList
            r1.remove(r0)
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r4.viewPages
            r2 = 0
            r1 = r1[r2]
            org.telegram.ui.Components.DialogsItemAnimator r1 = r1.dialogsItemAnimator
            r1.prepareForRemove()
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r4.viewPages
            r1 = r1[r2]
            org.telegram.ui.Adapters.DialogsAdapter r1 = r1.dialogsAdapter
            r1.notifyItemRemoved(r0)
            r0 = 2
            r4.dialogRemoveFinished = r0
            goto L_0x0443
        L_0x0436:
            org.telegram.messenger.MessagesController r27 = r39.getMessagesController()
            r30 = 0
            r31 = 0
            r28 = r1
            r27.markDialogAsUnread(r28, r30, r31)
        L_0x0443:
            r29 = r3
            r12 = r4
            goto L_0x0319
        L_0x0448:
            r0 = 102(0x66, float:1.43E-43)
            r4 = r39
            if (r14 == r0) goto L_0x049e
            r0 = 103(0x67, float:1.44E-43)
            if (r14 != r0) goto L_0x0458
            r0 = 1
            r16 = 3
            r17 = 4
            goto L_0x04a3
        L_0x0458:
            r0 = 104(0x68, float:1.46E-43)
            if (r14 != r0) goto L_0x0443
            r0 = 1
            if (r10 != r0) goto L_0x0471
            int r5 = r4.canMuteCount
            if (r5 != r0) goto L_0x0471
            r0 = 0
            org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.Components.AlertsCreator.createMuteAlert(r4, r1, r0)
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda15 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda15
            r1.<init>(r4)
            r4.showDialog(r0, r1)
            return
        L_0x0471:
            int r0 = r4.canUnmuteCount
            if (r0 == 0) goto L_0x0489
            org.telegram.messenger.MessagesController r0 = r39.getMessagesController()
            boolean r0 = r0.isDialogMuted(r1)
            if (r0 != 0) goto L_0x0480
            goto L_0x0443
        L_0x0480:
            org.telegram.messenger.NotificationsController r0 = r39.getNotificationsController()
            r5 = 4
            r0.setDialogNotificationsSettings(r1, r5)
            goto L_0x0443
        L_0x0489:
            r5 = 4
            org.telegram.messenger.MessagesController r0 = r39.getMessagesController()
            boolean r0 = r0.isDialogMuted(r1)
            if (r0 == 0) goto L_0x0495
            goto L_0x0443
        L_0x0495:
            org.telegram.messenger.NotificationsController r0 = r39.getNotificationsController()
            r12 = 3
            r0.setDialogNotificationsSettings(r1, r12)
            goto L_0x0443
        L_0x049e:
            r16 = 3
            r17 = 4
            r0 = 1
        L_0x04a3:
            if (r10 != r0) goto L_0x0526
            r0 = 102(0x66, float:1.43E-43)
            if (r14 != r0) goto L_0x04f3
            boolean r0 = r4.canDeletePsaSelected
            if (r0 == 0) goto L_0x04f3
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r1 = r39.getParentActivity()
            r0.<init>((android.content.Context) r1)
            r1 = 2131627265(0x7f0e0d01, float:1.888179E38)
            java.lang.String r2 = "PsaHideChatAlertTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            r1 = 2131627264(0x7f0e0d00, float:1.8881788E38)
            java.lang.String r2 = "PsaHideChatAlertText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setMessage(r1)
            r1 = 2131627263(0x7f0e0cff, float:1.8881785E38)
            java.lang.String r2 = "PsaHide"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda10 r2 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda10
            r2.<init>(r4)
            r0.setPositiveButton(r1, r2)
            r2 = 2131624663(0x7f0e02d7, float:1.8876512E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r2 = 0
            r0.setNegativeButton(r1, r2)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r4.showDialog(r0)
            r10 = r4
            goto L_0x0525
        L_0x04f3:
            r0 = 103(0x67, float:1.44E-43)
            if (r14 != r0) goto L_0x04f9
            r7 = 1
            goto L_0x04fa
        L_0x04f9:
            r7 = 0
        L_0x04fa:
            long r5 = r5.id
            boolean r8 = org.telegram.messenger.DialogObject.isEncryptedDialog(r5)
            r5 = 102(0x66, float:1.43E-43)
            if (r14 != r5) goto L_0x0507
            r22 = 1
            goto L_0x0509
        L_0x0507:
            r22 = 0
        L_0x0509:
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda35 r9 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda35
            r0 = r9
            r5 = r1
            r1 = r39
            r2 = r41
            r3 = r13
            r10 = r4
            r4 = r5
            r6 = r23
            r0.<init>(r1, r2, r3, r4, r6)
            r0 = r39
            r1 = r7
            r2 = r13
            r3 = r12
            r4 = r8
            r5 = r22
            r6 = r9
            org.telegram.ui.Components.AlertsCreator.createClearOrDeleteDialogAlert(r0, r1, r2, r3, r4, r5, r6)
        L_0x0525:
            return
        L_0x0526:
            r0 = r1
            r12 = r4
            r2 = 2131624663(0x7f0e02d7, float:1.8876512E38)
            r5 = 102(0x66, float:1.43E-43)
            org.telegram.messenger.MessagesController r4 = r39.getMessagesController()
            r2 = 1
            boolean r4 = r4.isPromoDialog(r0, r2)
            if (r4 == 0) goto L_0x0543
            org.telegram.messenger.MessagesController r0 = r39.getMessagesController()
            r0.hidePromoDialog()
        L_0x053f:
            r29 = r3
            goto L_0x031b
        L_0x0543:
            r2 = 103(0x67, float:1.44E-43)
            if (r14 != r2) goto L_0x0555
            int r4 = r12.canClearCacheCount
            if (r4 == 0) goto L_0x0555
            org.telegram.messenger.MessagesController r4 = r39.getMessagesController()
            r2 = 0
            r13 = 2
            r4.deleteDialog(r0, r13, r2)
            goto L_0x053f
        L_0x0555:
            r18 = 2
            r24 = 0
            r27 = r0
            r0 = r39
            r25 = 103(0x67, float:1.44E-43)
            r1 = r41
            r29 = r3
            r21 = 2131624663(0x7f0e02d7, float:1.8876512E38)
            r2 = r27
            r20 = -1
            r4 = r13
            r13 = 102(0x66, float:1.43E-43)
            r5 = r23
            r13 = 100
            r6 = r24
            r0.lambda$performSelectedDialogsAction$27(r1, r2, r4, r5, r6)
            goto L_0x05ff
        L_0x0578:
            r12 = r39
            r27 = r1
            r29 = r3
            r13 = 100
            r16 = 3
            r20 = -1
            r21 = 2131624663(0x7f0e02d7, float:1.8876512E38)
            r25 = 103(0x67, float:1.44E-43)
            int r0 = r12.canPinCount
            if (r0 == 0) goto L_0x05e6
            boolean r0 = r12.isDialogPinned(r5)
            if (r0 == 0) goto L_0x0595
            goto L_0x05ff
        L_0x0595:
            int r15 = r15 + 1
            r3 = 1
            r0 = 1
            if (r10 != r0) goto L_0x059d
            r6 = 1
            goto L_0x059e
        L_0x059d:
            r6 = 0
        L_0x059e:
            r0 = r39
            r1 = r27
            r13 = r4
            r4 = r11
            r38 = r5
            r5 = r8
            r0.pinDialog(r1, r3, r4, r5, r6)
            if (r11 == 0) goto L_0x05ff
            int r8 = r8 + 1
            if (r13 == 0) goto L_0x05ca
            java.util.ArrayList<java.lang.Long> r0 = r11.alwaysShow
            long r1 = r13.user_id
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            boolean r0 = r0.contains(r1)
            if (r0 != 0) goto L_0x05ff
            java.util.ArrayList<java.lang.Long> r0 = r11.alwaysShow
            long r1 = r13.user_id
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            r0.add(r1)
            goto L_0x05ff
        L_0x05ca:
            java.util.ArrayList<java.lang.Long> r0 = r11.alwaysShow
            r5 = r38
            long r1 = r5.id
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            boolean r0 = r0.contains(r1)
            if (r0 != 0) goto L_0x05ff
            java.util.ArrayList<java.lang.Long> r0 = r11.alwaysShow
            long r1 = r5.id
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            r0.add(r1)
            goto L_0x05ff
        L_0x05e6:
            boolean r0 = r12.isDialogPinned(r5)
            if (r0 != 0) goto L_0x05ed
            goto L_0x05ff
        L_0x05ed:
            int r15 = r15 + 1
            r3 = 0
            r0 = 1
            if (r10 != r0) goto L_0x05f5
            r6 = 1
            goto L_0x05f6
        L_0x05f5:
            r6 = 0
        L_0x05f6:
            r0 = r39
            r1 = r27
            r4 = r11
            r5 = r8
            r0.pinDialog(r1, r3, r4, r5, r6)
        L_0x05ff:
            int r9 = r9 + 1
            r13 = r12
            r3 = r29
            r4 = -1
            r6 = 100
            r12 = 1
            goto L_0x02fc
        L_0x060a:
            r12 = r13
            r0 = 104(0x68, float:1.46E-43)
            r13 = 1
            if (r14 != r0) goto L_0x0625
            if (r10 != r13) goto L_0x0616
            int r0 = r12.canMuteCount
            if (r0 == r13) goto L_0x0625
        L_0x0616:
            int r0 = r12.canUnmuteCount
            if (r0 != 0) goto L_0x061c
            r0 = 1
            goto L_0x061d
        L_0x061c:
            r0 = 0
        L_0x061d:
            r1 = 0
            org.telegram.ui.Components.Bulletin r0 = org.telegram.ui.Components.BulletinFactory.createMuteBulletin((org.telegram.ui.ActionBar.BaseFragment) r12, (boolean) r0, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
            r0.show()
        L_0x0625:
            r0 = 100
            r10 = 108(0x6c, float:1.51E-43)
            if (r14 == r0) goto L_0x0630
            if (r14 != r10) goto L_0x062e
            goto L_0x0630
        L_0x062e:
            r13 = r12
            goto L_0x0682
        L_0x0630:
            r8 = 0
            if (r11 == 0) goto L_0x065b
            int r1 = r11.flags
            java.lang.String r2 = r11.name
            java.util.ArrayList<java.lang.Long> r3 = r11.alwaysShow
            java.util.ArrayList<java.lang.Long> r4 = r11.neverShow
            org.telegram.messenger.support.LongSparseIntArray r5 = r11.pinnedDialogs
            r6 = 0
            r7 = 0
            r16 = 1
            r18 = 1
            r19 = 0
            r20 = 0
            r0 = r11
            r13 = r8
            r8 = r16
            r9 = r18
            r11 = 108(0x6c, float:1.51E-43)
            r10 = r19
            r11 = r39
            r13 = r12
            r12 = r20
            org.telegram.ui.FilterCreateActivity.saveFilterToServer(r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            goto L_0x0668
        L_0x065b:
            r13 = r12
            org.telegram.messenger.MessagesController r0 = r39.getMessagesController()
            int r1 = r13.folderId
            r2 = 0
            r4 = 0
            r0.reorderPinnedDialogs(r1, r4, r2)
        L_0x0668:
            boolean r0 = r13.searchIsShowed
            if (r0 == 0) goto L_0x0682
            org.telegram.ui.Components.UndoView r0 = r39.getUndoView()
            int r1 = r13.canPinCount
            if (r1 == 0) goto L_0x0677
            r1 = 78
            goto L_0x0679
        L_0x0677:
            r1 = 79
        L_0x0679:
            java.lang.Integer r2 = java.lang.Integer.valueOf(r15)
            r3 = 0
            r0.showWithAction((long) r3, (int) r1, (java.lang.Object) r2)
        L_0x0682:
            r0 = r41
            r1 = 108(0x6c, float:1.51E-43)
            if (r0 == r1) goto L_0x0692
            r1 = 100
            if (r0 == r1) goto L_0x0692
            r1 = 102(0x66, float:1.43E-43)
            if (r0 == r1) goto L_0x0692
            r15 = 1
            goto L_0x0693
        L_0x0692:
            r15 = 0
        L_0x0693:
            r13.hideActionMode(r15)
            return
        L_0x0697:
            r16 = 3
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>(r7)
            org.telegram.messenger.MessagesController r0 = r39.getMessagesController()
            int r1 = r13.canUnarchiveCount
            if (r1 != 0) goto L_0x06a8
            r2 = 1
            goto L_0x06a9
        L_0x06a8:
            r2 = 0
        L_0x06a9:
            r3 = -1
            r4 = 0
            r5 = 0
            r1 = r8
            r0.addDialogToFolder(r1, r2, r3, r4, r5)
            int r0 = r13.canUnarchiveCount
            if (r0 != 0) goto L_0x0701
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings()
            java.lang.String r1 = "archivehint_l"
            r2 = 0
            boolean r3 = r0.getBoolean(r1, r2)
            if (r3 != 0) goto L_0x06c9
            boolean r2 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r2 == 0) goto L_0x06c7
            goto L_0x06c9
        L_0x06c7:
            r12 = 0
            goto L_0x06ca
        L_0x06c9:
            r12 = 1
        L_0x06ca:
            if (r12 != 0) goto L_0x06d9
            android.content.SharedPreferences$Editor r0 = r0.edit()
            r2 = 1
            android.content.SharedPreferences$Editor r0 = r0.putBoolean(r1, r2)
            r0.commit()
            goto L_0x06da
        L_0x06d9:
            r2 = 1
        L_0x06da:
            if (r12 == 0) goto L_0x06e7
            int r0 = r8.size()
            if (r0 <= r2) goto L_0x06e4
            r6 = 4
            goto L_0x06e5
        L_0x06e4:
            r6 = 2
        L_0x06e5:
            r3 = r6
            goto L_0x06f1
        L_0x06e7:
            int r0 = r8.size()
            if (r0 <= r2) goto L_0x06ef
            r11 = 5
            goto L_0x06f0
        L_0x06ef:
            r11 = 3
        L_0x06f0:
            r3 = r11
        L_0x06f1:
            org.telegram.ui.Components.UndoView r0 = r39.getUndoView()
            r1 = 0
            r4 = 0
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda31 r5 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda31
            r5.<init>(r13, r8)
            r0.showWithAction(r1, r3, r4, r5)
            goto L_0x0732
        L_0x0701:
            org.telegram.messenger.MessagesController r0 = r39.getMessagesController()
            int r1 = r13.folderId
            java.util.ArrayList r0 = r0.getDialogs(r1)
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r13.viewPages
            if (r1 == 0) goto L_0x0732
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0732
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
            r39.finishFragment()
            goto L_0x0733
        L_0x0732:
            r1 = 0
        L_0x0733:
            r13.hideActionMode(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.performSelectedDialogsAction(java.util.ArrayList, int, boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSelectedDialogsAction$21(ArrayList arrayList) {
        getMessagesController().addDialogToFolder(arrayList, this.folderId == 0 ? 0 : 1, -1, (ArrayList<TLRPC$TL_inputFolderPeer>) null, 0);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSelectedDialogsAction$22(DialogInterface dialogInterface, int i) {
        presentFragment(new FiltersSetupActivity());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSelectedDialogsAction$24(ArrayList arrayList, int i, DialogInterface dialogInterface, int i2) {
        if (!arrayList.isEmpty()) {
            ArrayList arrayList2 = new ArrayList(arrayList);
            getUndoView().showWithAction((ArrayList<Long>) arrayList2, i == 102 ? 27 : 26, (Object) null, (Object) null, (Runnable) new DialogsActivity$$ExternalSyntheticLambda29(this, i, arrayList2), (Runnable) null);
            hideActionMode(i == 103);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSelectedDialogsAction$23(int i, ArrayList arrayList) {
        if (i == 102) {
            getMessagesController().setDialogsInTransaction(true);
            performSelectedDialogsAction(arrayList, i, false);
            getMessagesController().setDialogsInTransaction(false);
            getMessagesController().checkIfFolderEmpty(this.folderId);
            if (this.folderId != 0 && getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, false).size() == 0) {
                this.viewPages[0].listView.setEmptyView((View) null);
                this.viewPages[0].progressView.setVisibility(4);
                finishFragment();
                return;
            }
            return;
        }
        performSelectedDialogsAction(arrayList, i, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSelectedDialogsAction$25(ArrayList arrayList, boolean z, boolean z2) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            ArrayList arrayList2 = arrayList;
            long longValue = ((Long) arrayList.get(i)).longValue();
            if (z) {
                getMessagesController().reportSpam(longValue, getMessagesController().getUser(Long.valueOf(longValue)), (TLRPC$Chat) null, (TLRPC$EncryptedChat) null, false);
            }
            if (z2) {
                getMessagesController().deleteDialog(longValue, 0, true);
            }
            getMessagesController().blockPeer(longValue);
        }
        hideActionMode(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSelectedDialogsAction$26(DialogInterface dialogInterface, int i) {
        getMessagesController().hidePromoDialog();
        hideActionMode(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSelectedDialogsAction$28(int i, TLRPC$Chat tLRPC$Chat, long j, boolean z, boolean z2) {
        int i2;
        int i3;
        int i4;
        ArrayList<TLRPC$Dialog> arrayList;
        int i5 = i;
        TLRPC$Chat tLRPC$Chat2 = tLRPC$Chat;
        long j2 = j;
        hideActionMode(false);
        if (i5 != 103 || !ChatObject.isChannel(tLRPC$Chat) || (tLRPC$Chat2.megagroup && TextUtils.isEmpty(tLRPC$Chat2.username))) {
            boolean z3 = z2;
            if (i5 == 102 && this.folderId != 0 && getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, false).size() == 1) {
                this.viewPages[0].progressView.setVisibility(4);
            }
            this.debugLastUpdateAction = 3;
            if (i5 == 102) {
                setDialogsListFrozen(true);
                if (this.frozenDialogsList != null) {
                    int i6 = 0;
                    while (true) {
                        if (i6 >= this.frozenDialogsList.size()) {
                            break;
                        } else if (this.frozenDialogsList.get(i6).id == j2) {
                            i2 = i6;
                            break;
                        } else {
                            i6++;
                        }
                    }
                }
            }
            i2 = -1;
            UndoView undoView2 = getUndoView();
            int i7 = i5 == 103 ? 0 : 1;
            DialogsActivity$$ExternalSyntheticLambda28 dialogsActivity$$ExternalSyntheticLambda28 = r0;
            int i8 = i2;
            DialogsActivity$$ExternalSyntheticLambda28 dialogsActivity$$ExternalSyntheticLambda282 = new DialogsActivity$$ExternalSyntheticLambda28(this, i, j, tLRPC$Chat, z, z2);
            undoView2.showWithAction(j2, i7, (Runnable) dialogsActivity$$ExternalSyntheticLambda28);
            ArrayList arrayList2 = new ArrayList(getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, false));
            int i9 = 0;
            while (true) {
                if (i9 >= arrayList2.size()) {
                    i3 = 102;
                    i4 = -1;
                    break;
                } else if (((TLRPC$Dialog) arrayList2.get(i9)).id == j2) {
                    i4 = i9;
                    i3 = 102;
                    break;
                } else {
                    i9++;
                }
            }
            if (i5 == i3) {
                int i10 = i8;
                if (i10 < 0 || i4 >= 0 || (arrayList = this.frozenDialogsList) == null) {
                    setDialogsListFrozen(false);
                    return;
                }
                arrayList.remove(i10);
                this.viewPages[0].dialogsItemAnimator.prepareForRemove();
                this.viewPages[0].dialogsAdapter.notifyItemRemoved(i10);
                this.dialogRemoveFinished = 2;
                return;
            }
            return;
        }
        getMessagesController().deleteDialog(j2, 2, z2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSelectedDialogsAction$29(DialogInterface dialogInterface) {
        hideActionMode(true);
    }

    /* access modifiers changed from: private */
    /* renamed from: performDeleteOrClearDialogAction */
    public void lambda$performSelectedDialogsAction$27(int i, long j, TLRPC$Chat tLRPC$Chat, boolean z, boolean z2) {
        if (i == 103) {
            getMessagesController().deleteDialog(j, 1, z2);
            return;
        }
        if (tLRPC$Chat == null) {
            getMessagesController().deleteDialog(j, 0, z2);
            if (z) {
                getMessagesController().blockPeer((long) ((int) j));
            }
        } else if (ChatObject.isNotInChat(tLRPC$Chat)) {
            getMessagesController().deleteDialog(j, 0, z2);
        } else {
            getMessagesController().deleteParticipantFromChat((long) ((int) (-j)), getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId())), (TLRPC$Chat) null, (TLRPC$ChatFull) null, z2, false);
        }
        if (AndroidUtilities.isTablet()) {
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, Long.valueOf(j));
        }
        getMessagesController().checkIfFolderEmpty(this.folderId);
    }

    /* JADX WARNING: Removed duplicated region for block: B:69:0x0141  */
    /* JADX WARNING: Removed duplicated region for block: B:76:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void pinDialog(long r16, boolean r18, org.telegram.messenger.MessagesController.DialogFilter r19, int r20, boolean r21) {
        /*
            r15 = this;
            r0 = r15
            r8 = r16
            r1 = r19
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r0.viewPages
            r10 = 0
            r2 = r2[r10]
            int r2 = r2.dialogsType
            r11 = 1
            if (r2 != 0) goto L_0x0019
            boolean r2 = r15.hasHiddenArchive()
            if (r2 == 0) goto L_0x0019
            r2 = 1
            goto L_0x001a
        L_0x0019:
            r2 = 0
        L_0x001a:
            org.telegram.ui.DialogsActivity$ViewPage[] r3 = r0.viewPages
            r3 = r3[r10]
            androidx.recyclerview.widget.LinearLayoutManager r3 = r3.layoutManager
            int r3 = r3.findFirstVisibleItemPosition()
            if (r1 == 0) goto L_0x0035
            org.telegram.messenger.support.LongSparseIntArray r4 = r1.pinnedDialogs
            r5 = -2147483648(0xfffffffvar_, float:-0.0)
            int r4 = r4.get(r8, r5)
            if (r18 != 0) goto L_0x0035
            if (r4 != r5) goto L_0x0035
            return
        L_0x0035:
            if (r18 == 0) goto L_0x0039
            r4 = 4
            goto L_0x003a
        L_0x0039:
            r4 = 5
        L_0x003a:
            r0.debugLastUpdateAction = r4
            r12 = -1
            if (r3 > r2) goto L_0x0068
            if (r21 != 0) goto L_0x0042
            goto L_0x0068
        L_0x0042:
            r15.setDialogsListFrozen(r11)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r2 = r0.frozenDialogsList
            if (r2 == 0) goto L_0x0065
            r2 = 0
        L_0x004a:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r3 = r0.frozenDialogsList
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x0065
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r3 = r0.frozenDialogsList
            java.lang.Object r3 = r3.get(r2)
            org.telegram.tgnet.TLRPC$Dialog r3 = (org.telegram.tgnet.TLRPC$Dialog) r3
            long r3 = r3.id
            int r5 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r5 != 0) goto L_0x0062
            r13 = r2
            goto L_0x0066
        L_0x0062:
            int r2 = r2 + 1
            goto L_0x004a
        L_0x0065:
            r13 = -1
        L_0x0066:
            r14 = 0
            goto L_0x006a
        L_0x0068:
            r13 = -1
            r14 = 1
        L_0x006a:
            if (r1 == 0) goto L_0x0086
            if (r18 == 0) goto L_0x0076
            org.telegram.messenger.support.LongSparseIntArray r2 = r1.pinnedDialogs
            r3 = r20
            r2.put(r8, r3)
            goto L_0x007b
        L_0x0076:
            org.telegram.messenger.support.LongSparseIntArray r2 = r1.pinnedDialogs
            r2.delete(r8)
        L_0x007b:
            if (r21 == 0) goto L_0x0084
            org.telegram.messenger.MessagesController r2 = r15.getMessagesController()
            r2.onFilterUpdate(r1)
        L_0x0084:
            r1 = 1
            goto L_0x0095
        L_0x0086:
            org.telegram.messenger.MessagesController r1 = r15.getMessagesController()
            r5 = 0
            r6 = -1
            r2 = r16
            r4 = r18
            boolean r1 = r1.pinDialog(r2, r4, r5, r6)
        L_0x0095:
            if (r1 == 0) goto L_0x00ce
            if (r14 == 0) goto L_0x00a6
            int r1 = r0.initialDialogsType
            r2 = 10
            if (r1 == r2) goto L_0x00a2
            r15.hideFloatingButton(r10)
        L_0x00a2:
            r15.scrollToTop()
            goto L_0x00ce
        L_0x00a6:
            int r1 = r0.currentAccount
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r0.viewPages
            r2 = r2[r10]
            int r2 = r2.dialogsType
            int r3 = r0.folderId
            java.util.ArrayList r1 = r15.getDialogsArray(r1, r2, r3, r10)
            r2 = 0
        L_0x00b7:
            int r3 = r1.size()
            if (r2 >= r3) goto L_0x00ce
            java.lang.Object r3 = r1.get(r2)
            org.telegram.tgnet.TLRPC$Dialog r3 = (org.telegram.tgnet.TLRPC$Dialog) r3
            long r3 = r3.id
            int r5 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r5 != 0) goto L_0x00cb
            r12 = r2
            goto L_0x00ce
        L_0x00cb:
            int r2 = r2 + 1
            goto L_0x00b7
        L_0x00ce:
            if (r14 != 0) goto L_0x0144
            if (r13 < 0) goto L_0x013e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r1 = r0.frozenDialogsList
            if (r1 == 0) goto L_0x012f
            if (r12 < 0) goto L_0x012f
            if (r13 == r12) goto L_0x012f
            java.lang.Object r2 = r1.remove(r13)
            org.telegram.tgnet.TLRPC$Dialog r2 = (org.telegram.tgnet.TLRPC$Dialog) r2
            r1.add(r12, r2)
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r0.viewPages
            r1 = r1[r10]
            org.telegram.ui.Components.DialogsItemAnimator r1 = r1.dialogsItemAnimator
            r1.prepareForRemove()
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r0.viewPages
            r1 = r1[r10]
            org.telegram.ui.Adapters.DialogsAdapter r1 = r1.dialogsAdapter
            r1.notifyItemRemoved(r13)
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r0.viewPages
            r1 = r1[r10]
            org.telegram.ui.Adapters.DialogsAdapter r1 = r1.dialogsAdapter
            r1.notifyItemInserted(r12)
            r1 = 2
            r0.dialogRemoveFinished = r1
            r0.dialogInsertFinished = r1
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r0.viewPages
            r1 = r1[r10]
            androidx.recyclerview.widget.LinearLayoutManager r1 = r1.layoutManager
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r0.viewPages
            r2 = r2[r10]
            int r2 = r2.dialogsType
            if (r2 != 0) goto L_0x0123
            boolean r2 = r15.hasHiddenArchive()
            if (r2 == 0) goto L_0x0123
            r2 = 1
            goto L_0x0124
        L_0x0123:
            r2 = 0
        L_0x0124:
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            float r3 = r3.getTranslationY()
            int r3 = (int) r3
            r1.scrollToPositionWithOffset(r2, r3)
            goto L_0x013f
        L_0x012f:
            if (r12 < 0) goto L_0x013e
            if (r13 != r12) goto L_0x013e
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda24 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda24
            r1.<init>(r15)
            r2 = 200(0xc8, double:9.9E-322)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1, r2)
            goto L_0x013f
        L_0x013e:
            r11 = 0
        L_0x013f:
            if (r11 != 0) goto L_0x0144
            r15.setDialogsListFrozen(r10)
        L_0x0144:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.pinDialog(long, boolean, org.telegram.messenger.MessagesController$DialogFilter, int, boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$pinDialog$30() {
        setDialogsListFrozen(false);
    }

    /* access modifiers changed from: private */
    public void scrollToTop() {
        int findFirstVisibleItemPosition = this.viewPages[0].layoutManager.findFirstVisibleItemPosition() * AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f);
        int i = (this.viewPages[0].dialogsType != 0 || !hasHiddenArchive()) ? 0 : 1;
        this.viewPages[0].listView.getItemAnimator();
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
        int i4;
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
            long clientUserId = getUserConfig().getClientUserId();
            SharedPreferences notificationsSettings = getNotificationsSettings();
            int i5 = 0;
            int i6 = 0;
            int i7 = 0;
            int i8 = 0;
            int i9 = 0;
            int i10 = 0;
            while (i5 < size) {
                TLRPC$Dialog tLRPC$Dialog = getMessagesController().dialogs_dict.get(this.selectedDialogs.get(i5).longValue());
                if (tLRPC$Dialog == null) {
                    i2 = size;
                    i3 = i5;
                } else {
                    long j = tLRPC$Dialog.id;
                    boolean isDialogPinned = isDialogPinned(tLRPC$Dialog);
                    i2 = size;
                    boolean z2 = tLRPC$Dialog.unread_count != 0 || tLRPC$Dialog.unread_mark;
                    if (getMessagesController().isDialogMuted(j)) {
                        i3 = i5;
                        i4 = 1;
                        this.canUnmuteCount++;
                    } else {
                        i3 = i5;
                        i4 = 1;
                        this.canMuteCount++;
                    }
                    if (z2) {
                        this.canReadCount += i4;
                    }
                    if (this.folderId == i4 || tLRPC$Dialog.folder_id == i4) {
                        this.canUnarchiveCount++;
                    } else if (!(j == clientUserId || j == 777000 || getMessagesController().isPromoDialog(j, false))) {
                        i8++;
                    }
                    if (!DialogObject.isUserDialog(j) || j == clientUserId || MessagesController.isSupportUser(getMessagesController().getUser(Long.valueOf(j)))) {
                        i10++;
                    } else {
                        if (notificationsSettings.getBoolean("dialog_bar_report" + j, true)) {
                            this.canReportSpamCount++;
                        }
                    }
                    if (DialogObject.isChannel(tLRPC$Dialog)) {
                        TLRPC$Chat chat = getMessagesController().getChat(Long.valueOf(-j));
                        if (getMessagesController().isPromoDialog(tLRPC$Dialog.id, true)) {
                            this.canClearCacheCount++;
                            if (getMessagesController().promoDialogType == MessagesController.PROMO_TYPE_PSA) {
                                i6++;
                                this.canDeletePsaSelected = true;
                            }
                        } else {
                            if (isDialogPinned) {
                                i9++;
                            } else {
                                this.canPinCount++;
                            }
                            if (chat == null || !chat.megagroup) {
                                this.canClearCacheCount++;
                                i6++;
                            } else if (!TextUtils.isEmpty(chat.username)) {
                                this.canClearCacheCount++;
                                i6++;
                            }
                        }
                    } else {
                        boolean isChatDialog = DialogObject.isChatDialog(tLRPC$Dialog.id);
                        if (isChatDialog) {
                            getMessagesController().getChat(Long.valueOf(-tLRPC$Dialog.id));
                        }
                        if (DialogObject.isEncryptedDialog(tLRPC$Dialog.id)) {
                            TLRPC$EncryptedChat encryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(tLRPC$Dialog.id)));
                            tLRPC$User = encryptedChat != null ? getMessagesController().getUser(Long.valueOf(encryptedChat.user_id)) : new TLRPC$TL_userEmpty();
                        } else {
                            tLRPC$User = (isChatDialog || !DialogObject.isUserDialog(tLRPC$Dialog.id)) ? null : getMessagesController().getUser(Long.valueOf(tLRPC$Dialog.id));
                        }
                        if (tLRPC$User != null && tLRPC$User.bot) {
                            boolean isSupportUser = MessagesController.isSupportUser(tLRPC$User);
                        }
                        if (isDialogPinned) {
                            i9++;
                        } else {
                            this.canPinCount++;
                        }
                    }
                    i7++;
                    i6++;
                }
                i5 = i3 + 1;
                size = i2;
            }
            int i11 = size;
            if (i6 != size) {
                this.deleteItem.setVisibility(8);
            } else {
                this.deleteItem.setVisibility(0);
            }
            int i12 = this.canClearCacheCount;
            if ((i12 == 0 || i12 == size) && (i7 == 0 || i7 == size)) {
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
            } else if (i8 != 0) {
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
            if (this.canPinCount + i9 != size) {
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
            if (i10 != 0) {
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
        TLRPC$Chat chat;
        ChatActivityEnterView chatActivityEnterView;
        if ((this.messagesCount <= 1 && ((chatActivityEnterView = this.commentView) == null || chatActivityEnterView.getVisibility() != 0 || TextUtils.isEmpty(this.commentView.getFieldText()))) || !DialogObject.isChatDialog(j) || (chat = getMessagesController().getChat(Long.valueOf(-j))) == null || ChatObject.hasAdminRights(chat) || !chat.slowmode_enabled) {
            return true;
        }
        AlertsCreator.showSimpleAlert(this, LocaleController.getString("Slowmode", NUM), LocaleController.getString("SlowmodeSendError", NUM));
        return false;
    }

    private void showOrUpdateActionMode(long j, View view) {
        addOrRemoveSelectedDialog(j, view);
        boolean z = true;
        if (!this.actionBar.isActionModeShowed()) {
            if (this.searchIsShowed) {
                createActionMode("search_dialogs_action_mode");
                if (this.actionBar.getBackButton().getDrawable() instanceof MenuDrawable) {
                    this.actionBar.setBackButtonDrawable(new BackDrawable(false));
                }
            } else {
                createActionMode((String) null);
            }
            AndroidUtilities.hideKeyboard(this.fragmentView.findFocus());
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
                updateVisibleRows(MessagesController.UPDATE_MASK_REORDER);
            }
            if (!this.searchIsShowed) {
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
            }
            ValueAnimator valueAnimator = this.actionBarColorAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.progressToActionMode, 1.0f});
            this.actionBarColorAnimator = ofFloat;
            ofFloat.addUpdateListener(new DialogsActivity$$ExternalSyntheticLambda2(this));
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
    public /* synthetic */ void lambda$showOrUpdateActionMode$31(ValueAnimator valueAnimator) {
        this.progressToActionMode = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        for (int i = 0; i < this.actionBar.getChildCount(); i++) {
            if (!(this.actionBar.getChildAt(i).getVisibility() != 0 || this.actionBar.getChildAt(i) == this.actionBar.getActionMode() || this.actionBar.getChildAt(i) == this.actionBar.getBackButton())) {
                this.actionBar.getChildAt(i).setAlpha(1.0f - this.progressToActionMode);
            }
        }
        View view = this.fragmentView;
        if (view != null) {
            view.invalidate();
        }
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
                    AlertDialog create = AlertsCreator.createContactsPermissionDialog(parentActivity, new DialogsActivity$$ExternalSyntheticLambda36(this)).create();
                    this.permissionDialog = create;
                    showDialog(create);
                    return;
                }
                arrayList.add("android.permission.READ_CONTACTS");
                arrayList.add("android.permission.WRITE_CONTACTS");
                arrayList.add("android.permission.GET_ACCOUNTS");
            }
            if ((Build.VERSION.SDK_INT <= 28 || BuildVars.NO_SCOPED_STORAGE) && parentActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
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
    public /* synthetic */ void lambda$askForPermissons$32(int i) {
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
                            updateVisibleRows(MessagesController.UPDATE_MASK_NEW_MESSAGE);
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
        } else if (i == NotificationCenter.emojiLoaded) {
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
            if (!(filterTabsView5 == null || filterTabsView5.getVisibility() != 0 || (num.intValue() & MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE) == 0)) {
                this.filterTabsView.checkTabsCounter();
            }
            if (this.viewPages != null) {
                while (i5 < this.viewPages.length) {
                    if ((num.intValue() & MessagesController.UPDATE_MASK_STATUS) != 0) {
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
                        updateVisibleRows(MessagesController.UPDATE_MASK_SELECT_DIALOG);
                        return;
                    }
                }
            }
        } else if (i == NotificationCenter.notificationsSettingsUpdated) {
            updateVisibleRows(0);
        } else if (i == NotificationCenter.messageReceivedByAck || i == NotificationCenter.messageReceivedByServer || i == NotificationCenter.messageSendError) {
            updateVisibleRows(MessagesController.UPDATE_MASK_SEND_STATE);
        } else if (i == NotificationCenter.didSetPasscode) {
            updatePasscodeButton();
        } else if (i == NotificationCenter.needReloadRecentDialogsSearch) {
            SearchViewPager searchViewPager2 = this.searchViewPager;
            if (searchViewPager2 != null && (dialogsSearchAdapter2 = searchViewPager2.dialogsSearchAdapter) != null) {
                dialogsSearchAdapter2.loadRecentSearch();
            }
        } else if (i == NotificationCenter.replyMessagesDidLoad) {
            updateVisibleRows(MessagesController.UPDATE_MASK_MESSAGE_TEXT);
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
                DialogsActivity$$ExternalSyntheticLambda32 dialogsActivity$$ExternalSyntheticLambda32 = new DialogsActivity$$ExternalSyntheticLambda32(this, objArr[2], longValue2, objArr[3].booleanValue(), objArr[1]);
                if (this.undoView[0] != null) {
                    getUndoView().showWithAction(longValue2, 1, (Runnable) dialogsActivity$$ExternalSyntheticLambda32);
                } else {
                    dialogsActivity$$ExternalSyntheticLambda32.run();
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
                this.searchViewPager.messagesDeleted(objArr[1].longValue(), objArr[0]);
            }
        } else if (i == NotificationCenter.didClearDatabase) {
            if (this.viewPages != null) {
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
        } else if (i == NotificationCenter.appUpdateAvailable) {
            updateMenuButton(true);
        } else if (i == NotificationCenter.fileLoaded || i == NotificationCenter.fileLoadFailed || i == NotificationCenter.fileLoadProgressChanged) {
            String str = objArr[0];
            if (SharedConfig.isAppUpdateAvailable() && FileLoader.getAttachFileName(SharedConfig.pendingAppUpdate.document).equals(str)) {
                updateMenuButton(true);
            }
        } else if (i != NotificationCenter.onDatabaseMigration) {
        } else {
            if (!objArr[0].booleanValue()) {
                final View view = this.databaseMigrationHint;
                if (view != null) {
                    view.animate().setListener((Animator.AnimatorListener) null).cancel();
                    view.animate().setListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (view.getParent() != null) {
                                ((ContentView) DialogsActivity.this.fragmentView).removeView(view);
                            }
                        }
                    }).alpha(0.0f).setDuration(150).start();
                    this.databaseMigrationHint = null;
                }
            } else if (this.databaseMigrationHint == null) {
                DatabaseMigrationHint databaseMigrationHint2 = new DatabaseMigrationHint(this.fragmentView.getContext(), this.currentAccount);
                this.databaseMigrationHint = databaseMigrationHint2;
                databaseMigrationHint2.setAlpha(0.0f);
                ((ContentView) this.fragmentView).addView(this.databaseMigrationHint);
                this.databaseMigrationHint.animate().alpha(1.0f).setDuration(300).setStartDelay(1000).start();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$33(TLRPC$Chat tLRPC$Chat, long j, boolean z, TLRPC$User tLRPC$User) {
        if (tLRPC$Chat == null) {
            getMessagesController().deleteDialog(j, 0, z);
            if (tLRPC$User != null && tLRPC$User.bot) {
                getMessagesController().blockPeer(tLRPC$User.id);
            }
        } else if (ChatObject.isNotInChat(tLRPC$Chat)) {
            getMessagesController().deleteDialog(j, 0, z);
        } else {
            getMessagesController().deleteParticipantFromChat(-j, getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId())), (TLRPC$Chat) null, (TLRPC$ChatFull) null, z, z);
        }
        getMessagesController().checkIfFolderEmpty(this.folderId);
    }

    private void updateMenuButton(boolean z) {
        int i;
        if (this.menuDrawable != null && this.updateLayout != null) {
            float f = 0.0f;
            if (SharedConfig.isAppUpdateAvailable()) {
                String attachFileName = FileLoader.getAttachFileName(SharedConfig.pendingAppUpdate.document);
                if (getFileLoader().isLoadingFile(attachFileName)) {
                    i = MenuDrawable.TYPE_UDPATE_DOWNLOADING;
                    Float fileProgress = ImageLoader.getInstance().getFileProgress(attachFileName);
                    if (fileProgress != null) {
                        f = fileProgress.floatValue();
                    }
                } else {
                    i = MenuDrawable.TYPE_UDPATE_AVAILABLE;
                }
            } else {
                i = MenuDrawable.TYPE_DEFAULT;
            }
            updateAppUpdateViews(z);
            this.menuDrawable.setType(i, z);
            this.menuDrawable.setUpdateDownloadProgress(f, z);
        }
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
        builder.setPositiveButton(LocaleController.getString("GoToSettings", NUM), new DialogsActivity$$ExternalSyntheticLambda7(this));
        showDialog(builder.create(), new DialogsActivity$$ExternalSyntheticLambda16(this));
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showSuggestion$34(DialogInterface dialogInterface, int i) {
        presentFragment(new PrivacySettingsActivity());
        AndroidUtilities.scrollToFragmentRow(this.parentLayout, "newChatsRow");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showSuggestion$35(DialogInterface dialogInterface) {
        onSuggestionDismiss();
    }

    private void showFiltersHint() {
        if (!this.askingForPermissions && getMessagesController().dialogFiltersLoaded && getMessagesController().showFiltersTooltip && this.filterTabsView != null && getMessagesController().dialogFilters.isEmpty() && !this.isPaused && getUserConfig().filtersLoaded && !this.inPreviewMode) {
            SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
            if (!globalMainSettings.getBoolean("filterhint", false)) {
                globalMainSettings.edit().putBoolean("filterhint", true).commit();
                AndroidUtilities.runOnUIThread(new DialogsActivity$$ExternalSyntheticLambda27(this), 1000);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showFiltersHint$36() {
        presentFragment(new FiltersSetupActivity());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showFiltersHint$37() {
        getUndoView().showWithAction(0, 15, (Runnable) null, new DialogsActivity$$ExternalSyntheticLambda23(this));
    }

    private void setDialogsListFrozen(boolean z, boolean z2) {
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
            this.passcodeItem.setIcon((Drawable) this.passcodeDrawable);
            this.passcodeItemVisible = true;
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
            ofFloat.addUpdateListener(new DialogsActivity$$ExternalSyntheticLambda1(this));
            animatorSet.playTogether(new Animator[]{ofFloat});
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(this.floatingInterpolator);
            this.floatingButtonContainer.setClickable(!z);
            animatorSet.start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$hideFloatingButton$38(ValueAnimator valueAnimator) {
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
        updateVisibleRows(i, true);
    }

    private void updateVisibleRows(int i, boolean z) {
        if ((!this.dialogsListFrozen || (MessagesController.UPDATE_MASK_REORDER & i) != 0) && !this.isPaused) {
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
                            boolean z2 = true;
                            if ((MessagesController.UPDATE_MASK_REORDER & i) != 0) {
                                dialogCell.onReorderStateChanged(this.actionBar.isActionModeShowed(), true);
                                if (this.dialogsListFrozen) {
                                }
                            }
                            if ((MessagesController.UPDATE_MASK_CHECK & i) != 0) {
                                if ((MessagesController.UPDATE_MASK_CHAT & i) == 0) {
                                    z2 = false;
                                }
                                dialogCell.setChecked(false, z2);
                            } else {
                                if ((MessagesController.UPDATE_MASK_NEW_MESSAGE & i) != 0) {
                                    dialogCell.checkCurrentDialogIndex(this.dialogsListFrozen);
                                    if (this.viewPages[i2].isDefaultDialogType() && AndroidUtilities.isTablet()) {
                                        if (dialogCell.getDialogId() != this.openedDialogId) {
                                            z2 = false;
                                        }
                                        dialogCell.setDialogSelected(z2);
                                    }
                                } else if ((MessagesController.UPDATE_MASK_SELECT_DIALOG & i) == 0) {
                                    dialogCell.update(i, z);
                                } else if (this.viewPages[i2].isDefaultDialogType() && AndroidUtilities.isTablet()) {
                                    if (dialogCell.getDialogId() != this.openedDialogId) {
                                        z2 = false;
                                    }
                                    dialogCell.setDialogSelected(z2);
                                }
                                ArrayList<Long> arrayList = this.selectedDialogs;
                                if (arrayList != null) {
                                    dialogCell.setChecked(arrayList.contains(Long.valueOf(dialogCell.getDialogId())), false);
                                }
                            }
                        }
                        if (childAt instanceof UserCell) {
                            ((UserCell) childAt).update(i);
                        } else if (childAt instanceof ProfileSearchCell) {
                            ProfileSearchCell profileSearchCell = (ProfileSearchCell) childAt;
                            profileSearchCell.update(i);
                            ArrayList<Long> arrayList2 = this.selectedDialogs;
                            if (arrayList2 != null) {
                                profileSearchCell.setChecked(arrayList2.contains(Long.valueOf(profileSearchCell.getDialogId())), false);
                            }
                        }
                        if (!this.dialogsListFrozen && (childAt instanceof RecyclerListView)) {
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
            if (DialogObject.isChatDialog(j)) {
                long j3 = -j2;
                TLRPC$Chat chat = getMessagesController().getChat(Long.valueOf(j3));
                if (ChatObject.isChannel(chat) && !chat.megagroup && (this.cantSendToChannels || !ChatObject.isCanWriteToChannel(j3, this.currentAccount) || this.hasPoll == 2)) {
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
            } else if (DialogObject.isEncryptedDialog(j) && (this.hasPoll != 0 || this.hasInvoice)) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                builder2.setTitle(LocaleController.getString("SendMessageTitle", NUM));
                if (this.hasPoll != 0) {
                    builder2.setMessage(LocaleController.getString("PollCantForwardSecretChat", NUM));
                } else {
                    builder2.setMessage(LocaleController.getString("InvoiceCantForwardSecretChat", NUM));
                }
                builder2.setNegativeButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                showDialog(builder2.create());
                return;
            }
        }
        int i = this.initialDialogsType;
        if (i == 11 || i == 12 || i == 13) {
            boolean z3 = z2;
            if (!this.checkingImportDialog) {
                if (DialogObject.isUserDialog(j)) {
                    TLRPC$User user = getMessagesController().getUser(Long.valueOf(j));
                    if (!user.mutual_contact) {
                        getUndoView().showWithAction(j2, 45, (Runnable) null);
                        return;
                    } else {
                        tLRPC$Chat = null;
                        tLRPC$User = user;
                    }
                } else {
                    TLRPC$Chat chat2 = getMessagesController().getChat(Long.valueOf(-j2));
                    if (!ChatObject.hasAdminRights(chat2) || !ChatObject.canChangeChatInfo(chat2)) {
                        getUndoView().showWithAction(j2, 46, (Runnable) null);
                        return;
                    }
                    tLRPC$Chat = chat2;
                }
                AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
                TLRPC$TL_messages_checkHistoryImportPeer tLRPC$TL_messages_checkHistoryImportPeer = new TLRPC$TL_messages_checkHistoryImportPeer();
                tLRPC$TL_messages_checkHistoryImportPeer.peer = getMessagesController().getInputPeer(j2);
                getConnectionsManager().sendRequest(tLRPC$TL_messages_checkHistoryImportPeer, new DialogsActivity$$ExternalSyntheticLambda38(this, alertDialog, tLRPC$User, tLRPC$Chat, j, z2, tLRPC$TL_messages_checkHistoryImportPeer));
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
            if (DialogObject.isEncryptedDialog(j)) {
                TLRPC$User user2 = getMessagesController().getUser(Long.valueOf(getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(j))).user_id));
                if (user2 != null) {
                    str3 = LocaleController.getString("SendMessageTitle", NUM);
                    str2 = LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(user2));
                    str = LocaleController.getString("Send", NUM);
                } else {
                    return;
                }
            } else if (!DialogObject.isUserDialog(j)) {
                TLRPC$Chat chat3 = getMessagesController().getChat(Long.valueOf(-j2));
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
            } else if (j2 == getUserConfig().getClientUserId()) {
                str3 = LocaleController.getString("SendMessageTitle", NUM);
                str2 = LocaleController.formatStringSimple(this.selectAlertStringGroup, LocaleController.getString("SavedMessages", NUM));
                str = LocaleController.getString("Send", NUM);
            } else {
                TLRPC$User user3 = getMessagesController().getUser(Long.valueOf(j));
                if (user3 != null && this.selectAlertString != null) {
                    str3 = LocaleController.getString("SendMessageTitle", NUM);
                    str2 = LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(user3));
                    str = LocaleController.getString("Send", NUM);
                } else {
                    return;
                }
            }
            builder3.setTitle(str3);
            builder3.setMessage(AndroidUtilities.replaceTags(str2));
            builder3.setPositiveButton(str, new DialogsActivity$$ExternalSyntheticLambda11(this, j2));
            builder3.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder3.create());
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didSelectResult$41(AlertDialog alertDialog, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, long j, boolean z, TLRPC$TL_messages_checkHistoryImportPeer tLRPC$TL_messages_checkHistoryImportPeer, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new DialogsActivity$$ExternalSyntheticLambda33(this, alertDialog, tLObject, tLRPC$User, tLRPC$Chat, j, z, tLRPC$TL_error, tLRPC$TL_messages_checkHistoryImportPeer));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didSelectResult$40(AlertDialog alertDialog, TLObject tLObject, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, long j, boolean z, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_messages_checkHistoryImportPeer tLRPC$TL_messages_checkHistoryImportPeer) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        this.checkingImportDialog = false;
        if (tLObject != null) {
            AlertsCreator.createImportDialogAlert(this, this.arguments.getString("importTitle"), ((TLRPC$TL_messages_checkedHistoryImportPeer) tLObject).confirm_text, tLRPC$User, tLRPC$Chat, new DialogsActivity$$ExternalSyntheticLambda30(this, j, z));
            return;
        }
        AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, this, tLRPC$TL_messages_checkHistoryImportPeer, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(j), tLRPC$TL_messages_checkHistoryImportPeer, tLRPC$TL_error);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didSelectResult$39(long j, boolean z) {
        setDialogsListFrozen(true);
        ArrayList arrayList = new ArrayList();
        arrayList.add(Long.valueOf(j));
        this.delegate.didSelectDialogs(this, arrayList, (CharSequence) null, z);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didSelectResult$42(long j, DialogInterface dialogInterface, int i) {
        didSelectResult(j, false, false);
    }

    public RLottieImageView getFloatingButton() {
        return this.floatingButton;
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        RecyclerListView recyclerListView;
        DialogsActivity$$ExternalSyntheticLambda39 dialogsActivity$$ExternalSyntheticLambda39 = new DialogsActivity$$ExternalSyntheticLambda39(this);
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
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda39, "actionBarDefaultIcon"));
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
        DialogsActivity$$ExternalSyntheticLambda39 dialogsActivity$$ExternalSyntheticLambda392 = dialogsActivity$$ExternalSyntheticLambda39;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda392, "actionBarDefaultSubmenuBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda392, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda392, "actionBarDefaultSubmenuItemIcon"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda392, "dialogButtonSelector"));
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
        DialogsActivity$$ExternalSyntheticLambda39 dialogsActivity$$ExternalSyntheticLambda393 = dialogsActivity$$ExternalSyntheticLambda39;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda393, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda393, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda393, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda393, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda393, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda393, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda393, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda393, "avatar_backgroundSaved"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda393, "avatar_backgroundArchived"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda393, "avatar_backgroundArchivedHidden"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda393, "chats_nameMessage"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda393, "chats_draft"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda393, "chats_attachMessage"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda393, "chats_nameArchived"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda393, "chats_nameMessageArchived"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda393, "chats_nameMessageArchived_threeLines"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda393, "chats_messageArchived"));
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
                DialogsActivity$$ExternalSyntheticLambda39 dialogsActivity$$ExternalSyntheticLambda394 = dialogsActivity$$ExternalSyntheticLambda39;
                int i3 = i2;
                ThemeDescription themeDescription2 = new ThemeDescription((View) this.viewPages[i2].listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) dialogsActivity$$ExternalSyntheticLambda394, "windowBackgroundWhiteGrayText");
                arrayList.add(themeDescription);
                arrayList.add(new ThemeDescription((View) this.viewPages[i3].listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) dialogsActivity$$ExternalSyntheticLambda394, "windowBackgroundWhiteBlueText"));
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
        DialogsActivity$$ExternalSyntheticLambda39 dialogsActivity$$ExternalSyntheticLambda395 = dialogsActivity$$ExternalSyntheticLambda39;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda395, "chats_archivePullDownBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda395, "chats_archivePullDownBackgroundActive"));
        arrayList.add(new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuBackground"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuName"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuPhone"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuPhoneCats"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuCloudBackgroundCats"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_serviceBackground"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuTopShadow"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuTopShadowCats"));
        arrayList.add(new ThemeDescription((View) this.sideMenu, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{DrawerProfileCell.class}, new String[]{"darkThemeView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuName"));
        arrayList.add(new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda395, "chats_menuTopBackgroundCats"));
        arrayList.add(new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda395, "chats_menuTopBackground"));
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
        DialogsActivity$$ExternalSyntheticLambda39 dialogsActivity$$ExternalSyntheticLambda396 = dialogsActivity$$ExternalSyntheticLambda39;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda396, "actionBarTipBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda396, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda396, "player_time"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda396, "chat_messagePanelCursor"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda396, "avatar_actionBarIconBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda396, "groupcreate_spanBackground"));
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
    public /* synthetic */ void lambda$getThemeDescriptions$43() {
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

    /* access modifiers changed from: protected */
    public Animator getCustomSlideTransition(boolean z, boolean z2, float f) {
        if (z2) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.slideFragmentProgress, 1.0f});
            this.slideBackTransitionAnimator = ofFloat;
            return ofFloat;
        }
        ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{this.slideFragmentProgress, 1.0f});
        this.slideBackTransitionAnimator = ofFloat2;
        ofFloat2.addUpdateListener(new DialogsActivity$$ExternalSyntheticLambda0(this));
        this.slideBackTransitionAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.slideBackTransitionAnimator.setDuration((long) ((int) (((float) Math.max((int) ((200.0f / ((float) getLayoutContainer().getMeasuredWidth())) * f), 80)) * 1.2f)));
        this.slideBackTransitionAnimator.start();
        return this.slideBackTransitionAnimator;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getCustomSlideTransition$44(ValueAnimator valueAnimator) {
        setSlideTransitionProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    /* access modifiers changed from: protected */
    public void prepareFragmentToSlide(boolean z, boolean z2) {
        if (z || !z2) {
            this.slideBackTransitionAnimator = null;
            this.isSlideBackTransition = false;
            setFragmentIsSliding(false);
            setSlideTransitionProgress(1.0f);
            return;
        }
        this.isSlideBackTransition = true;
        setFragmentIsSliding(true);
    }

    private void setFragmentIsSliding(boolean z) {
        if (SharedConfig.getDevicePerformanceClass() != 0) {
            if (z) {
                ViewPage[] viewPageArr = this.viewPages;
                if (!(viewPageArr == null || viewPageArr[0] == null)) {
                    viewPageArr[0].setLayerType(2, (Paint) null);
                    this.viewPages[0].setClipChildren(false);
                    this.viewPages[0].setClipToPadding(false);
                    this.viewPages[0].listView.setClipChildren(false);
                }
                ActionBar actionBar = this.actionBar;
                if (actionBar != null) {
                    actionBar.setLayerType(2, (Paint) null);
                }
                FilterTabsView filterTabsView2 = this.filterTabsView;
                if (filterTabsView2 != null) {
                    filterTabsView2.getListView().setLayerType(2, (Paint) null);
                }
                View view = this.fragmentView;
                if (view != null) {
                    ((ViewGroup) view).setClipChildren(false);
                    this.fragmentView.requestLayout();
                    return;
                }
                return;
            }
            int i = 0;
            while (true) {
                ViewPage[] viewPageArr2 = this.viewPages;
                if (i >= viewPageArr2.length) {
                    break;
                }
                ViewPage viewPage = viewPageArr2[i];
                if (viewPage != null) {
                    viewPage.setLayerType(0, (Paint) null);
                    viewPage.setClipChildren(true);
                    viewPage.setClipToPadding(true);
                    viewPage.listView.setClipChildren(true);
                }
                i++;
            }
            ActionBar actionBar2 = this.actionBar;
            if (actionBar2 != null) {
                actionBar2.setLayerType(0, (Paint) null);
            }
            FilterTabsView filterTabsView3 = this.filterTabsView;
            if (filterTabsView3 != null) {
                filterTabsView3.getListView().setLayerType(0, (Paint) null);
            }
            View view2 = this.fragmentView;
            if (view2 != null) {
                ((ViewGroup) view2).setClipChildren(true);
                this.fragmentView.requestLayout();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onSlideProgress(boolean z, float f) {
        if (SharedConfig.getDevicePerformanceClass() != 0 && this.isSlideBackTransition && this.slideBackTransitionAnimator == null) {
            setSlideTransitionProgress(f);
        }
    }

    private void setSlideTransitionProgress(float f) {
        if (SharedConfig.getDevicePerformanceClass() != 0) {
            this.slideFragmentProgress = f;
            View view = this.fragmentView;
            if (view != null) {
                view.invalidate();
            }
            FilterTabsView filterTabsView2 = this.filterTabsView;
            if (filterTabsView2 != null) {
                float f2 = 1.0f - ((1.0f - this.slideFragmentProgress) * 0.05f);
                filterTabsView2.getListView().setScaleX(f2);
                this.filterTabsView.getListView().setScaleY(f2);
                this.filterTabsView.getListView().setTranslationX(((float) (this.isDrawerTransition ? AndroidUtilities.dp(4.0f) : -AndroidUtilities.dp(4.0f))) * (1.0f - this.slideFragmentProgress));
                this.filterTabsView.getListView().setPivotX(this.isDrawerTransition ? (float) this.filterTabsView.getMeasuredWidth() : 0.0f);
                this.filterTabsView.getListView().setPivotY(0.0f);
                this.filterTabsView.invalidate();
            }
        }
    }

    public void setProgressToDrawerOpened(float f) {
        if (SharedConfig.getDevicePerformanceClass() != 0 && !this.isSlideBackTransition) {
            boolean z = f > 0.0f;
            if (this.searchIsShowed) {
                f = 0.0f;
                z = false;
            }
            if (z != this.isDrawerTransition) {
                this.isDrawerTransition = z;
                if (z) {
                    setFragmentIsSliding(true);
                } else {
                    setFragmentIsSliding(false);
                }
                View view = this.fragmentView;
                if (view != null) {
                    view.requestLayout();
                }
            }
            setSlideTransitionProgress(1.0f - f);
        }
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
}
