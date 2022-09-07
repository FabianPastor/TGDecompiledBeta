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
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Property;
import android.util.StateSet;
import android.view.KeyEvent;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
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
import org.telegram.messenger.FilesMigrationService;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.XiaomiUtilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$EmojiStatus;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$InputDialogPeer;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$TL_account_updateEmojiStatus;
import org.telegram.tgnet.TLRPC$TL_boolTrue;
import org.telegram.tgnet.TLRPC$TL_dialogFolder;
import org.telegram.tgnet.TLRPC$TL_emojiStatus;
import org.telegram.tgnet.TLRPC$TL_emojiStatusEmpty;
import org.telegram.tgnet.TLRPC$TL_emojiStatusUntil;
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
import org.telegram.ui.ActionBar.MenuDrawable;
import org.telegram.ui.ActionBar.SimpleTextView;
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
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.HintDialogCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.BlurredRecyclerView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.CombinedDrawable;
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
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.ProxyDrawable;
import org.telegram.ui.Components.PullForegroundDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.RecyclerAnimationScrollHelper;
import org.telegram.ui.Components.RecyclerItemsEnterAnimator;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SearchViewPager;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.ViewPagerFixed;
import org.telegram.ui.GroupCreateFinalActivity;
import org.telegram.ui.SelectAnimatedEmojiDialog;

public class DialogsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    public static boolean[] dialogsLoaded = new boolean[4];
    /* access modifiers changed from: private */
    public static final Interpolator interpolator = DialogsActivity$$ExternalSyntheticLambda32.INSTANCE;
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
    private boolean allowBots;
    private boolean allowChannels;
    private boolean allowGroups;
    /* access modifiers changed from: private */
    public boolean allowMoving;
    /* access modifiers changed from: private */
    public boolean allowSwipeDuringCurrentTouch;
    private boolean allowSwitchAccount;
    private boolean allowUsers;
    /* access modifiers changed from: private */
    public DrawerProfileCell.AnimatedStatusView animatedStatusView;
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
    /* access modifiers changed from: private */
    public boolean closeFragment;
    private boolean closeSearchFieldOnHide;
    /* access modifiers changed from: private */
    public ChatActivityEnterView commentView;
    private AnimatorSet commentViewAnimator;
    private View commentViewBg;
    private float contactsAlpha = 1.0f;
    private ValueAnimator contactsAlphaAnimator;
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
    public ActionBarMenuItem downloadsItem;
    /* access modifiers changed from: private */
    public boolean downloadsItemVisible;
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
    /* access modifiers changed from: private */
    public RLottieImageView floatingButton;
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
    public AnimatorSet floatingProgressAnimator;
    /* access modifiers changed from: private */
    public RadialProgressView floatingProgressView;
    private boolean floatingProgressVisible;
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
    private boolean isNextButton = false;
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
    public boolean notify = true;
    /* access modifiers changed from: private */
    public boolean onlySelect;
    /* access modifiers changed from: private */
    public long openedDialogId;
    /* access modifiers changed from: private */
    public PacmanAnimation pacmanAnimation;
    /* access modifiers changed from: private */
    public Paint paint = new Paint(1);
    private RLottieDrawable passcodeDrawable;
    /* access modifiers changed from: private */
    public ActionBarMenuItem passcodeItem;
    /* access modifiers changed from: private */
    public boolean passcodeItemVisible;
    private AlertDialog permissionDialog;
    private ActionBarMenuSubItem pin2Item;
    private ActionBarMenuItem pinItem;
    private Drawable premiumStar;
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
    /* access modifiers changed from: private */
    public RectF rect = new RectF();
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
    public boolean scrimViewAppearing;
    /* access modifiers changed from: private */
    public Drawable scrimViewBackground;
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
    public SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow selectAnimatedEmojiDialog;
    private View selectedCountView;
    /* access modifiers changed from: private */
    public ArrayList<Long> selectedDialogs = new ArrayList<>();
    private NumberTextView selectedDialogsCountTextView;
    /* access modifiers changed from: private */
    public ActionBarPopupWindow sendPopupWindow;
    private boolean showSetPasswordConfirm;
    private String showingSuggestion;
    private RecyclerView sideMenu;
    ValueAnimator slideBackTransitionAnimator;
    float slideFragmentProgress = 1.0f;
    /* access modifiers changed from: private */
    public DialogCell slidingView;
    private boolean slowedReloadAfterDialogClick;
    /* access modifiers changed from: private */
    public long startArchivePullingTime;
    /* access modifiers changed from: private */
    public boolean startedTracking;
    private AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable statusDrawable;
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
    public TextPaint textPaint = new TextPaint(1);
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
    private ImageView[] writeButton;
    /* access modifiers changed from: private */
    public FrameLayout writeButtonContainer;

    public interface DialogsActivityDelegate {
        void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList<Long> arrayList, CharSequence charSequence, boolean z);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createActionMode$18(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ float lambda$static$0(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2 * f2 * f2) + 1.0f;
    }

    /* access modifiers changed from: private */
    public void updateCommentView() {
    }

    public boolean shouldShowNextButton(DialogsActivity dialogsActivity, ArrayList<Long> arrayList, CharSequence charSequence, boolean z) {
        return false;
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
        public boolean isLocked;
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

        static /* synthetic */ int access$11208(ViewPage viewPage) {
            int i = viewPage.lastItemsCount;
            viewPage.lastItemsCount = i + 1;
            return i;
        }

        static /* synthetic */ int access$11210(ViewPage viewPage) {
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
            this.needBlur = true;
            this.blurBehindViews.add(this);
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
                Rect rect = AndroidUtilities.rectTmp2;
                int i3 = i2 + actionBarFullHeight;
                rect.set(0, i2, getMeasuredWidth(), i3);
                int i4 = i3;
                Rect rect2 = rect;
                drawBlurRect(canvas, 0.0f, rect, DialogsActivity.this.searchAnimationProgress == 1.0f ? this.actionBarSearchPaint : DialogsActivity.this.actionBarDefaultPaint, true);
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
                        drawBlurCircle(canvas, 0.0f, measuredWidth, ((float) i5) + (((float) (DialogsActivity.this.actionBar.getMeasuredHeight() - i5)) / 2.0f), ((float) getMeasuredWidth()) * 1.3f * DialogsActivity.this.searchAnimationProgress, this.actionBarSearchPaint, true);
                        canvas.restore();
                    } else {
                        Rect rect3 = rect2;
                        rect3.set(0, i2, getMeasuredWidth(), i4);
                        drawBlurRect(canvas, 0.0f, rect3, this.actionBarSearchPaint, true);
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
                    Rect rect4 = AndroidUtilities.rectTmp2;
                    rect4.set(0, i2, getMeasuredWidth(), i2 + actionBarFullHeight);
                    drawBlurRect(canvas, 0.0f, rect4, this.actionBarSearchPaint, true);
                } else {
                    Rect rect5 = AndroidUtilities.rectTmp2;
                    rect5.set(0, i2, getMeasuredWidth(), i2 + actionBarFullHeight);
                    drawBlurRect(canvas, 0.0f, rect5, DialogsActivity.this.actionBarDefaultPaint, true);
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
                float f2 = dialogsActivity2.slideFragmentProgress;
                if (f2 != 1.0f) {
                    float f3 = 1.0f - ((1.0f - f2) * 0.05f);
                    canvas2.translate(((float) (dialogsActivity2.isDrawerTransition ? AndroidUtilities.dp(4.0f) : -AndroidUtilities.dp(4.0f))) * (1.0f - DialogsActivity.this.slideFragmentProgress), 0.0f);
                    canvas2.scale(f3, 1.0f, DialogsActivity.this.isDrawerTransition ? (float) getMeasuredWidth() : 0.0f, DialogsActivity.this.fragmentContextView.getY());
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
                if (DialogsActivity.this.scrimViewBackground != null) {
                    DialogsActivity.this.scrimViewBackground.setAlpha(DialogsActivity.this.scrimViewAppearing ? 255 : (int) ((((float) DialogsActivity.this.scrimPaint.getAlpha()) / 50.0f) * 255.0f));
                    DialogsActivity.this.scrimViewBackground.setBounds(0, 0, DialogsActivity.this.scrimView.getWidth(), DialogsActivity.this.scrimView.getHeight());
                    DialogsActivity.this.scrimViewBackground.draw(canvas2);
                }
                Drawable selectorDrawable = DialogsActivity.this.filterTabsView.getListView().getSelectorDrawable();
                if (DialogsActivity.this.scrimViewAppearing && selectorDrawable != null) {
                    canvas.save();
                    Rect bounds = selectorDrawable.getBounds();
                    canvas2.translate((float) (-bounds.left), (float) (-bounds.top));
                    selectorDrawable.draw(canvas2);
                    canvas.restore();
                }
                DialogsActivity.this.scrimView.draw(canvas2);
                if (DialogsActivity.this.scrimViewSelected) {
                    Drawable selectorDrawable2 = DialogsActivity.this.filterTabsView.getSelectorDrawable();
                    canvas2.translate((float) (-DialogsActivity.this.scrimViewLocation[0]), (float) ((-selectorDrawable2.getIntrinsicHeight()) - 1));
                    selectorDrawable2.draw(canvas2);
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
                    if (childAt instanceof DatabaseMigrationHint) {
                        childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), (((View.MeasureSpec.getSize(i2) + measureKeyboardHeight) - this.inputFieldHeight) + AndroidUtilities.dp(2.0f)) - DialogsActivity.this.actionBar.getMeasuredHeight()), NUM));
                    } else if (childAt instanceof ViewPage) {
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
        /* JADX WARNING: Removed duplicated region for block: B:35:0x00a2  */
        /* JADX WARNING: Removed duplicated region for block: B:42:0x00bd  */
        /* JADX WARNING: Removed duplicated region for block: B:48:0x00de  */
        /* JADX WARNING: Removed duplicated region for block: B:53:0x0106  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onLayout(boolean r16, int r17, int r18, int r19, int r20) {
            /*
                r15 = this;
                r0 = r15
                int r1 = r15.getChildCount()
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
                int r3 = r15.measureKeyboardHeight()
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
                r15.setBottomClip(r2)
                org.telegram.ui.DialogsActivity r6 = org.telegram.ui.DialogsActivity.this
                int r7 = r6.topPadding
                int unused = r6.lastMeasuredTopPadding = r7
                r6 = -1
                r7 = -1
            L_0x0051:
                if (r7 >= r1) goto L_0x01bd
                if (r7 != r6) goto L_0x005c
                org.telegram.ui.DialogsActivity r8 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.ChatActivityEnterView r8 = r8.commentView
                goto L_0x0060
            L_0x005c:
                android.view.View r8 = r15.getChildAt(r7)
            L_0x0060:
                if (r8 == 0) goto L_0x01b9
                int r9 = r8.getVisibility()
                r10 = 8
                if (r9 != r10) goto L_0x006c
                goto L_0x01b9
            L_0x006c:
                android.view.ViewGroup$LayoutParams r9 = r8.getLayoutParams()
                android.widget.FrameLayout$LayoutParams r9 = (android.widget.FrameLayout.LayoutParams) r9
                int r10 = r8.getMeasuredWidth()
                int r11 = r8.getMeasuredHeight()
                int r12 = r9.gravity
                if (r12 != r6) goto L_0x0080
                r12 = 51
            L_0x0080:
                r13 = r12 & 7
                r12 = r12 & 112(0x70, float:1.57E-43)
                r13 = r13 & 7
                r14 = 1
                if (r13 == r14) goto L_0x0094
                r14 = 5
                if (r13 == r14) goto L_0x008f
                int r13 = r9.leftMargin
                goto L_0x009e
            L_0x008f:
                int r13 = r19 - r10
                int r14 = r9.rightMargin
                goto L_0x009d
            L_0x0094:
                int r13 = r19 - r17
                int r13 = r13 - r10
                int r13 = r13 / r4
                int r14 = r9.leftMargin
                int r13 = r13 + r14
                int r14 = r9.rightMargin
            L_0x009d:
                int r13 = r13 - r14
            L_0x009e:
                r14 = 16
                if (r12 == r14) goto L_0x00bd
                r14 = 48
                if (r12 == r14) goto L_0x00b5
                r14 = 80
                if (r12 == r14) goto L_0x00ad
                int r9 = r9.topMargin
                goto L_0x00ca
            L_0x00ad:
                int r12 = r20 - r2
                int r12 = r12 - r18
                int r12 = r12 - r11
                int r9 = r9.bottomMargin
                goto L_0x00c8
            L_0x00b5:
                int r9 = r9.topMargin
                int r12 = r15.getPaddingTop()
                int r9 = r9 + r12
                goto L_0x00ca
            L_0x00bd:
                int r12 = r20 - r2
                int r12 = r12 - r18
                int r12 = r12 - r11
                int r12 = r12 / r4
                int r14 = r9.topMargin
                int r12 = r12 + r14
                int r9 = r9.bottomMargin
            L_0x00c8:
                int r9 = r12 - r9
            L_0x00ca:
                org.telegram.ui.DialogsActivity r12 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.ChatActivityEnterView r12 = r12.commentView
                if (r12 == 0) goto L_0x0106
                org.telegram.ui.DialogsActivity r12 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.ChatActivityEnterView r12 = r12.commentView
                boolean r12 = r12.isPopupView(r8)
                if (r12 == 0) goto L_0x0106
                boolean r9 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
                if (r9 == 0) goto L_0x00fa
                org.telegram.ui.DialogsActivity r9 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.ChatActivityEnterView r9 = r9.commentView
                int r9 = r9.getTop()
                int r12 = r8.getMeasuredHeight()
                int r9 = r9 - r12
                r12 = 1065353216(0x3var_, float:1.0)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            L_0x00f7:
                int r9 = r9 + r12
                goto L_0x01b4
            L_0x00fa:
                org.telegram.ui.DialogsActivity r9 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.ChatActivityEnterView r9 = r9.commentView
                int r9 = r9.getBottom()
                goto L_0x01b4
            L_0x0106:
                org.telegram.ui.DialogsActivity r12 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.FilterTabsView r12 = r12.filterTabsView
                if (r8 == r12) goto L_0x01aa
                org.telegram.ui.DialogsActivity r12 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.ViewPagerFixed$TabsView r12 = r12.searchTabsView
                if (r8 == r12) goto L_0x01aa
                org.telegram.ui.DialogsActivity r12 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Adapters.FiltersView r12 = r12.filtersView
                if (r8 != r12) goto L_0x0120
                goto L_0x01aa
            L_0x0120:
                org.telegram.ui.DialogsActivity r12 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.SearchViewPager r12 = r12.searchViewPager
                r14 = 1110441984(0x42300000, float:44.0)
                if (r8 != r12) goto L_0x0154
                org.telegram.ui.DialogsActivity r9 = org.telegram.ui.DialogsActivity.this
                boolean r9 = r9.onlySelect
                if (r9 == 0) goto L_0x0134
                r9 = 0
                goto L_0x013e
            L_0x0134:
                org.telegram.ui.DialogsActivity r9 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.ActionBar.ActionBar r9 = r9.actionBar
                int r9 = r9.getMeasuredHeight()
            L_0x013e:
                org.telegram.ui.DialogsActivity r12 = org.telegram.ui.DialogsActivity.this
                int r12 = r12.topPadding
                int r9 = r9 + r12
                org.telegram.ui.DialogsActivity r12 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.ViewPagerFixed$TabsView r12 = r12.searchTabsView
                if (r12 != 0) goto L_0x014f
                r12 = 0
                goto L_0x00f7
            L_0x014f:
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r14)
                goto L_0x00f7
            L_0x0154:
                boolean r12 = r8 instanceof org.telegram.ui.DatabaseMigrationHint
                if (r12 == 0) goto L_0x0163
                org.telegram.ui.DialogsActivity r9 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.ActionBar.ActionBar r9 = r9.actionBar
                int r9 = r9.getMeasuredHeight()
                goto L_0x01b4
            L_0x0163:
                boolean r12 = r8 instanceof org.telegram.ui.DialogsActivity.ViewPage
                if (r12 == 0) goto L_0x019a
                org.telegram.ui.DialogsActivity r12 = org.telegram.ui.DialogsActivity.this
                boolean r12 = r12.onlySelect
                if (r12 != 0) goto L_0x0192
                org.telegram.ui.DialogsActivity r9 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.FilterTabsView r9 = r9.filterTabsView
                if (r9 == 0) goto L_0x0188
                org.telegram.ui.DialogsActivity r9 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.FilterTabsView r9 = r9.filterTabsView
                int r9 = r9.getVisibility()
                if (r9 != 0) goto L_0x0188
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r14)
                goto L_0x0192
            L_0x0188:
                org.telegram.ui.DialogsActivity r9 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.ActionBar.ActionBar r9 = r9.actionBar
                int r9 = r9.getMeasuredHeight()
            L_0x0192:
                org.telegram.ui.DialogsActivity r12 = org.telegram.ui.DialogsActivity.this
                int r12 = r12.topPadding
                goto L_0x00f7
            L_0x019a:
                boolean r12 = r8 instanceof org.telegram.ui.Components.FragmentContextView
                if (r12 == 0) goto L_0x01b4
                org.telegram.ui.DialogsActivity r12 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.ActionBar.ActionBar r12 = r12.actionBar
                int r12 = r12.getMeasuredHeight()
                goto L_0x00f7
            L_0x01aa:
                org.telegram.ui.DialogsActivity r9 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.ActionBar.ActionBar r9 = r9.actionBar
                int r9 = r9.getMeasuredHeight()
            L_0x01b4:
                int r10 = r10 + r13
                int r11 = r11 + r9
                r8.layout(r13, r9, r10, r11)
            L_0x01b9:
                int r7 = r7 + 1
                goto L_0x0051
            L_0x01bd:
                org.telegram.ui.DialogsActivity r1 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.SearchViewPager r1 = r1.searchViewPager
                r1.setKeyboardHeight(r3)
                r15.notifyHeightChanged()
                org.telegram.ui.DialogsActivity r1 = org.telegram.ui.DialogsActivity.this
                r1.updateContextViewPosition()
                org.telegram.ui.DialogsActivity r1 = org.telegram.ui.DialogsActivity.this
                r1.updateCommentView()
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
                    float abs2 = ((float) Math.abs(x)) / ((float) DialogsActivity.this.viewPages[0].getMeasuredWidth());
                    if (!DialogsActivity.this.viewPages[1].isLocked || abs2 <= 0.3f) {
                        DialogsActivity.this.filterTabsView.selectTabWithId(DialogsActivity.this.viewPages[1].selectedType, abs2);
                    } else {
                        dispatchTouchEvent(MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0));
                        DialogsActivity.this.filterTabsView.shakeLock(DialogsActivity.this.viewPages[1].selectedType);
                        AndroidUtilities.runOnUIThread(new DialogsActivity$ContentView$$ExternalSyntheticLambda0(this), 200);
                        return false;
                    }
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
                    if (DialogsActivity.this.viewPages[1].isLocked) {
                        boolean unused14 = DialogsActivity.this.backAnimation = true;
                    } else if (DialogsActivity.this.additionalOffset == 0.0f) {
                        boolean unused15 = DialogsActivity.this.backAnimation = Math.abs(x3) < ((float) DialogsActivity.this.viewPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(f2) < 3500.0f || Math.abs(f2) < Math.abs(f));
                    } else if (Math.abs(f2) > 1500.0f) {
                        DialogsActivity dialogsActivity5 = DialogsActivity.this;
                        boolean unused16 = dialogsActivity5.backAnimation = !dialogsActivity5.animatingForward ? f2 < 0.0f : f2 > 0.0f;
                    } else if (DialogsActivity.this.animatingForward) {
                        DialogsActivity dialogsActivity6 = DialogsActivity.this;
                        boolean unused17 = dialogsActivity6.backAnimation = dialogsActivity6.viewPages[1].getX() > ((float) (DialogsActivity.this.viewPages[0].getMeasuredWidth() >> 1));
                    } else {
                        DialogsActivity dialogsActivity7 = DialogsActivity.this;
                        boolean unused18 = dialogsActivity7.backAnimation = dialogsActivity7.viewPages[0].getX() < ((float) (DialogsActivity.this.viewPages[0].getMeasuredWidth() >> 1));
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
                    float abs3 = Math.abs(f2);
                    if (abs3 > 0.0f) {
                        i = Math.round(Math.abs(distanceInfluenceForSnapDuration / abs3) * 1000.0f) * 4;
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
                    boolean unused19 = DialogsActivity.this.tabsAnimationInProgress = true;
                    boolean unused20 = DialogsActivity.this.startedTracking = false;
                } else {
                    DialogsActivity.this.parentLayout.getDrawerLayoutContainer().setAllowOpenDrawerBySwipe(DialogsActivity.this.viewPages[0].selectedType == DialogsActivity.this.filterTabsView.getFirstTabId() || DialogsActivity.this.searchIsShowed || SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) != 5);
                    boolean unused21 = DialogsActivity.this.maybeStartTracking = false;
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

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onTouchEvent$0() {
            DialogsActivity.this.showDialog(new LimitReachedBottomSheet(DialogsActivity.this, getContext(), 3, DialogsActivity.this.currentAccount));
        }

        /* access modifiers changed from: protected */
        public void drawList(Canvas canvas, boolean z) {
            if (!DialogsActivity.this.searchIsShowed) {
                for (int i = 0; i < DialogsActivity.this.viewPages.length; i++) {
                    if (DialogsActivity.this.viewPages[i] != null && DialogsActivity.this.viewPages[i].getVisibility() == 0) {
                        for (int i2 = 0; i2 < DialogsActivity.this.viewPages[i].listView.getChildCount(); i2++) {
                            View childAt = DialogsActivity.this.viewPages[i].listView.getChildAt(i2);
                            if (childAt.getY() < ((float) (DialogsActivity.this.viewPages[i].listView.blurTopPadding + AndroidUtilities.dp(100.0f)))) {
                                int save = canvas.save();
                                canvas.translate(DialogsActivity.this.viewPages[i].getX(), DialogsActivity.this.viewPages[i].getY() + DialogsActivity.this.viewPages[i].listView.getY() + childAt.getY());
                                if (childAt instanceof DialogCell) {
                                    DialogCell dialogCell = (DialogCell) childAt;
                                    dialogCell.drawingForBlur = true;
                                    dialogCell.draw(canvas);
                                    dialogCell.drawingForBlur = false;
                                } else {
                                    childAt.draw(canvas);
                                }
                                canvas.restoreToCount(save);
                            }
                        }
                    }
                }
            } else if (DialogsActivity.this.searchViewPager != null && DialogsActivity.this.searchViewPager.getVisibility() == 0) {
                DialogsActivity.this.searchViewPager.drawForBlur(canvas);
            }
        }
    }

    public class DialogsRecyclerView extends BlurredRecyclerView {
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
            this.additionalClipBottom = AndroidUtilities.dp(200.0f);
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
                PullForegroundDrawable access$10600 = this.parentPage.pullForegroundDrawable;
                if (this.parentPage.archivePullViewState != 0) {
                    z = true;
                }
                access$10600.setWillDraw(z);
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

        /* access modifiers changed from: protected */
        public boolean allowSelectChildAtPosition(View view) {
            return !(view instanceof HeaderCell) || view.isClickable();
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
                    ViewPage.access$11210(this.parentPage);
                    this.parentPage.dialogsAdapter.notifyItemRemoved(i2);
                    int unused2 = DialogsActivity.this.dialogRemoveFinished = 2;
                    return;
                }
                int addDialogToFolder = DialogsActivity.this.getMessagesController().addDialogToFolder(tLRPC$Dialog.id, DialogsActivity.this.folderId == 0 ? 1 : 0, -1, 0);
                if (!(addDialogToFolder == 2 && i2 == 0)) {
                    this.parentPage.dialogsItemAnimator.prepareForRemove();
                    ViewPage.access$11210(this.parentPage);
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
                            ViewPage.access$11208(this.parentPage);
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
                    ViewPage.access$11208(this.parentPage);
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
                    ViewPage.access$11210(this.parentPage);
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
            this.showSetPasswordConfirm = this.arguments.getBoolean("showSetPasswordConfirm", this.showSetPasswordConfirm);
            this.arguments.getInt("otherwiseRelogin");
            this.allowGroups = this.arguments.getBoolean("allowGroups", true);
            this.allowChannels = this.arguments.getBoolean("allowChannels", true);
            this.allowUsers = this.arguments.getBoolean("allowUsers", true);
            this.allowBots = this.arguments.getBoolean("allowBots", true);
            this.closeFragment = this.arguments.getBoolean("closeFragment", true);
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
            getNotificationCenter().addObserver(this, NotificationCenter.onDownloadingFilesChanged);
            getNotificationCenter().addObserver(this, NotificationCenter.needDeleteDialog);
            getNotificationCenter().addObserver(this, NotificationCenter.folderBecomeEmpty);
            getNotificationCenter().addObserver(this, NotificationCenter.newSuggestionsAvailable);
            getNotificationCenter().addObserver(this, NotificationCenter.fileLoaded);
            getNotificationCenter().addObserver(this, NotificationCenter.fileLoadFailed);
            getNotificationCenter().addObserver(this, NotificationCenter.fileLoadProgressChanged);
            getNotificationCenter().addObserver(this, NotificationCenter.dialogsUnreadReactionsCounterChanged);
            getNotificationCenter().addObserver(this, NotificationCenter.forceImportContactsStart);
            getNotificationCenter().addObserver(this, NotificationCenter.userEmojiStatusUpdated);
            getNotificationCenter().addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.appUpdateAvailable);
        }
        getNotificationCenter().addObserver(this, NotificationCenter.messagesDeleted);
        getNotificationCenter().addObserver(this, NotificationCenter.onDatabaseMigration);
        getNotificationCenter().addObserver(this, NotificationCenter.onDatabaseOpened);
        getNotificationCenter().addObserver(this, NotificationCenter.didClearDatabase);
        loadDialogs(getAccountInstance());
        getMessagesController().loadPinnedDialogs(this.folderId, 0, (ArrayList<Long>) null);
        if (this.databaseMigrationHint != null && !getMessagesStorage().isDatabaseMigrationInProgress()) {
            View view = this.databaseMigrationHint;
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            this.databaseMigrationHint = null;
        }
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
            accountInstance.getMediaDataController().chekAllMedia(false);
            AndroidUtilities.runOnUIThread(new DialogsActivity$$ExternalSyntheticLambda34(accountInstance), 200);
            Iterator<String> it = messagesController.diceEmojies.iterator();
            while (it.hasNext()) {
                accountInstance.getMediaDataController().loadStickersByEmojiOrName(it.next(), true, true);
            }
            dialogsLoaded[currentAccount] = true;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:25:0x00cc  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00df  */
    /* JADX WARNING: Removed duplicated region for block: B:34:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateStatus(org.telegram.tgnet.TLRPC$User r7, boolean r8) {
        /*
            r6 = this;
            org.telegram.ui.Components.AnimatedEmojiDrawable$SwapAnimatedEmojiDrawable r0 = r6.statusDrawable
            if (r0 == 0) goto L_0x00f2
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            if (r0 != 0) goto L_0x000a
            goto L_0x00f2
        L_0x000a:
            java.lang.String r0 = "profile_verifiedBackground"
            if (r7 == 0) goto L_0x0039
            org.telegram.tgnet.TLRPC$EmojiStatus r1 = r7.emoji_status
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_emojiStatusUntil
            if (r2 == 0) goto L_0x0039
            org.telegram.tgnet.TLRPC$TL_emojiStatusUntil r1 = (org.telegram.tgnet.TLRPC$TL_emojiStatusUntil) r1
            int r1 = r1.until
            long r2 = java.lang.System.currentTimeMillis()
            r4 = 1000(0x3e8, double:4.94E-321)
            long r2 = r2 / r4
            int r3 = (int) r2
            if (r1 <= r3) goto L_0x0039
            org.telegram.ui.Components.AnimatedEmojiDrawable$SwapAnimatedEmojiDrawable r1 = r6.statusDrawable
            org.telegram.tgnet.TLRPC$EmojiStatus r7 = r7.emoji_status
            org.telegram.tgnet.TLRPC$TL_emojiStatusUntil r7 = (org.telegram.tgnet.TLRPC$TL_emojiStatusUntil) r7
            long r2 = r7.document_id
            r1.set((long) r2, (boolean) r8)
            org.telegram.ui.ActionBar.ActionBar r7 = r6.actionBar
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda25 r8 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda25
            r8.<init>(r6)
            r7.setRightDrawableOnClick(r8)
            goto L_0x00bb
        L_0x0039:
            if (r7 == 0) goto L_0x0055
            org.telegram.tgnet.TLRPC$EmojiStatus r1 = r7.emoji_status
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_emojiStatus
            if (r2 == 0) goto L_0x0055
            org.telegram.ui.Components.AnimatedEmojiDrawable$SwapAnimatedEmojiDrawable r7 = r6.statusDrawable
            org.telegram.tgnet.TLRPC$TL_emojiStatus r1 = (org.telegram.tgnet.TLRPC$TL_emojiStatus) r1
            long r1 = r1.document_id
            r7.set((long) r1, (boolean) r8)
            org.telegram.ui.ActionBar.ActionBar r7 = r6.actionBar
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda19 r8 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda19
            r8.<init>(r6)
            r7.setRightDrawableOnClick(r8)
            goto L_0x00bb
        L_0x0055:
            if (r7 == 0) goto L_0x00b0
            int r1 = r6.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            boolean r7 = r1.isPremiumUser(r7)
            if (r7 == 0) goto L_0x00b0
            android.graphics.drawable.Drawable r7 = r6.premiumStar
            if (r7 != 0) goto L_0x008e
            android.content.Context r7 = r6.getContext()
            android.content.res.Resources r7 = r7.getResources()
            int r1 = org.telegram.messenger.R.drawable.msg_premium_liststar
            android.graphics.drawable.Drawable r7 = r7.getDrawable(r1)
            android.graphics.drawable.Drawable r7 = r7.mutate()
            r6.premiumStar = r7
            org.telegram.ui.DialogsActivity$2 r7 = new org.telegram.ui.DialogsActivity$2
            android.graphics.drawable.Drawable r1 = r6.premiumStar
            r2 = 1099956224(0x41900000, float:18.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r7.<init>(r6, r1, r3, r2)
            r6.premiumStar = r7
        L_0x008e:
            android.graphics.drawable.Drawable r7 = r6.premiumStar
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            android.graphics.PorterDuff$Mode r3 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r2, r3)
            r7.setColorFilter(r1)
            org.telegram.ui.Components.AnimatedEmojiDrawable$SwapAnimatedEmojiDrawable r7 = r6.statusDrawable
            android.graphics.drawable.Drawable r1 = r6.premiumStar
            r7.set((android.graphics.drawable.Drawable) r1, (boolean) r8)
            org.telegram.ui.ActionBar.ActionBar r7 = r6.actionBar
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda24 r8 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda24
            r8.<init>(r6)
            r7.setRightDrawableOnClick(r8)
            goto L_0x00bb
        L_0x00b0:
            org.telegram.ui.Components.AnimatedEmojiDrawable$SwapAnimatedEmojiDrawable r7 = r6.statusDrawable
            r1 = 0
            r7.set((android.graphics.drawable.Drawable) r1, (boolean) r8)
            org.telegram.ui.ActionBar.ActionBar r7 = r6.actionBar
            r7.setRightDrawableOnClick(r1)
        L_0x00bb:
            org.telegram.ui.Components.AnimatedEmojiDrawable$SwapAnimatedEmojiDrawable r7 = r6.statusDrawable
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r7.setColor(r8)
            org.telegram.ui.Cells.DrawerProfileCell$AnimatedStatusView r7 = r6.animatedStatusView
            if (r7 == 0) goto L_0x00d3
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            r7.setColor(r8)
        L_0x00d3:
            org.telegram.ui.SelectAnimatedEmojiDialog$SelectAnimatedEmojiDialogWindow r7 = r6.selectAnimatedEmojiDialog
            if (r7 == 0) goto L_0x00f2
            android.view.View r7 = r7.getContentView()
            boolean r7 = r7 instanceof org.telegram.ui.SelectAnimatedEmojiDialog
            if (r7 == 0) goto L_0x00f2
            org.telegram.ui.SelectAnimatedEmojiDialog$SelectAnimatedEmojiDialogWindow r7 = r6.selectAnimatedEmojiDialog
            android.view.View r7 = r7.getContentView()
            org.telegram.ui.SelectAnimatedEmojiDialog r7 = (org.telegram.ui.SelectAnimatedEmojiDialog) r7
            org.telegram.ui.Components.AnimatedEmojiDrawable$SwapAnimatedEmojiDrawable r8 = r6.statusDrawable
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            org.telegram.ui.ActionBar.SimpleTextView r0 = r0.getTitleTextView()
            r7.setScrimDrawable(r8, r0)
        L_0x00f2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.updateStatus(org.telegram.tgnet.TLRPC$User, boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateStatus$2(View view) {
        showSelectStatusDialog();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateStatus$3(View view) {
        showSelectStatusDialog();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateStatus$4(View view) {
        showSelectStatusDialog();
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
            getNotificationCenter().removeObserver(this, NotificationCenter.onDownloadingFilesChanged);
            getNotificationCenter().removeObserver(this, NotificationCenter.needDeleteDialog);
            getNotificationCenter().removeObserver(this, NotificationCenter.folderBecomeEmpty);
            getNotificationCenter().removeObserver(this, NotificationCenter.newSuggestionsAvailable);
            getNotificationCenter().removeObserver(this, NotificationCenter.fileLoaded);
            getNotificationCenter().removeObserver(this, NotificationCenter.fileLoadFailed);
            getNotificationCenter().removeObserver(this, NotificationCenter.fileLoadProgressChanged);
            getNotificationCenter().removeObserver(this, NotificationCenter.dialogsUnreadReactionsCounterChanged);
            getNotificationCenter().removeObserver(this, NotificationCenter.forceImportContactsStart);
            getNotificationCenter().removeObserver(this, NotificationCenter.userEmojiStatusUpdated);
            getNotificationCenter().removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.appUpdateAvailable);
        }
        getNotificationCenter().removeObserver(this, NotificationCenter.messagesDeleted);
        getNotificationCenter().removeObserver(this, NotificationCenter.onDatabaseMigration);
        getNotificationCenter().removeObserver(this, NotificationCenter.onDatabaseOpened);
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
        SuggestClearDatabaseBottomSheet.dismissDialog();
    }

    /* access modifiers changed from: protected */
    public ActionBar createActionBar(Context context) {
        AnonymousClass3 r0 = new ActionBar(context) {
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

    /* JADX WARNING: Code restructure failed: missing block: B:60:0x02c0, code lost:
        r5 = (r5 = r2.photo).strippedBitmap;
     */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x09f6  */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x0a03  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x0a56  */
    /* JADX WARNING: Removed duplicated region for block: B:233:0x0af4 A[LOOP:3: B:231:0x0af1->B:233:0x0af4, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x0b1d  */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x0b7c  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x0bc1  */
    /* JADX WARNING: Removed duplicated region for block: B:248:0x0bc4  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x0bd1  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x0c5f  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x0CLASSNAME  */
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
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda33 r0 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda33
            r0.<init>(r11)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r6 = r0.createMenu()
            boolean r0 = r10.onlySelect
            r14 = 3
            r15 = 0
            r9 = 8
            r8 = 2
            r7 = 1
            if (r0 != 0) goto L_0x0104
            java.lang.String r0 = r10.searchString
            if (r0 != 0) goto L_0x0104
            int r0 = r10.folderId
            if (r0 != 0) goto L_0x0104
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            r2 = 0
            java.lang.String r0 = "actionBarDefaultSelector"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            java.lang.String r0 = "actionBarDefaultIcon"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            r16 = 1
            r0 = r5
            r1 = r36
            r13 = r5
            r5 = r16
            r0.<init>((android.content.Context) r1, (org.telegram.ui.ActionBar.ActionBarMenu) r2, (int) r3, (int) r4, (boolean) r5)
            r10.doneItem = r13
            int r0 = org.telegram.messenger.R.string.Done
            java.lang.String r1 = "Done"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.String r0 = r0.toUpperCase()
            r13.setText(r0)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r10.doneItem
            r17 = -2
            r18 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r19 = 53
            r20 = 0
            r21 = 0
            r22 = 1092616192(0x41200000, float:10.0)
            r23 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r0.addView(r1, r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r10.doneItem
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda23 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda23
            r1.<init>(r10)
            r0.setOnClickListener(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r10.doneItem
            r0.setAlpha(r15)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r10.doneItem
            r0.setVisibility(r9)
            org.telegram.ui.Components.ProxyDrawable r0 = new org.telegram.ui.Components.ProxyDrawable
            r0.<init>(r11)
            r10.proxyDrawable = r0
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.addItem((int) r8, (android.graphics.drawable.Drawable) r0)
            r10.proxyItem = r0
            int r1 = org.telegram.messenger.R.string.ProxySettings
            java.lang.String r2 = "ProxySettings"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setContentDescription(r1)
            org.telegram.ui.Components.RLottieDrawable r0 = new org.telegram.ui.Components.RLottieDrawable
            int r18 = org.telegram.messenger.R.raw.passcode_lock_close
            r1 = 1105199104(0x41e00000, float:28.0)
            int r20 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r21 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r22 = 1
            r23 = 0
            java.lang.String r19 = "passcode_lock_close"
            r17 = r0
            r17.<init>(r18, r19, r20, r21, r22, r23)
            r10.passcodeDrawable = r0
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.addItem((int) r7, (android.graphics.drawable.Drawable) r0)
            r10.passcodeItem = r0
            int r1 = org.telegram.messenger.R.string.AccDescrPasscodeLock
            java.lang.String r2 = "AccDescrPasscodeLock"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setContentDescription(r1)
            android.graphics.drawable.ColorDrawable r0 = new android.graphics.drawable.ColorDrawable
            r0.<init>(r12)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.addItem((int) r14, (android.graphics.drawable.Drawable) r0)
            r10.downloadsItem = r0
            org.telegram.ui.DownloadProgressIcon r1 = new org.telegram.ui.DownloadProgressIcon
            int r2 = r10.currentAccount
            r1.<init>(r2, r11)
            r0.addView(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r10.downloadsItem
            int r1 = org.telegram.messenger.R.string.DownloadsTabs
            java.lang.String r2 = "DownloadsTabs"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setContentDescription(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r10.downloadsItem
            r0.setVisibility(r9)
            r35.updatePasscodeButton()
            r10.updateProxyButton(r12, r12)
        L_0x0104:
            int r0 = org.telegram.messenger.R.drawable.ic_ab_search
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.addItem((int) r12, (int) r0)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.setIsSearchField(r7, r7)
            org.telegram.ui.DialogsActivity$4 r1 = new org.telegram.ui.DialogsActivity$4
            r1.<init>()
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.setActionBarMenuItemSearchListener(r1)
            r10.searchItem = r0
            int r1 = r10.initialDialogsType
            if (r1 == r8) goto L_0x0121
            r2 = 14
            if (r1 != r2) goto L_0x0124
        L_0x0121:
            r0.setVisibility(r9)
        L_0x0124:
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r10.searchItem
            int r1 = org.telegram.messenger.R.string.Search
            java.lang.String r2 = "Search"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setSearchFieldHint(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r10.searchItem
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setContentDescription(r1)
            boolean r0 = r10.onlySelect
            java.lang.String r13 = "actionBarDefault"
            r5 = 10
            if (r0 == 0) goto L_0x0187
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            int r1 = org.telegram.messenger.R.drawable.ic_ab_back
            r0.setBackButtonImage(r1)
            int r0 = r10.initialDialogsType
            if (r0 != r14) goto L_0x015f
            java.lang.String r1 = r10.selectAlertString
            if (r1 != 0) goto L_0x015f
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            int r1 = org.telegram.messenger.R.string.ForwardTo
            java.lang.String r2 = "ForwardTo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x017c
        L_0x015f:
            if (r0 != r5) goto L_0x016f
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            int r1 = org.telegram.messenger.R.string.SelectChats
            java.lang.String r2 = "SelectChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x017c
        L_0x016f:
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            int r1 = org.telegram.messenger.R.string.SelectChat
            java.lang.String r2 = "SelectChat"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
        L_0x017c:
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r0.setBackgroundColor(r1)
            goto L_0x0216
        L_0x0187:
            java.lang.String r0 = r10.searchString
            if (r0 != 0) goto L_0x01af
            int r0 = r10.folderId
            if (r0 == 0) goto L_0x0190
            goto L_0x01af
        L_0x0190:
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            org.telegram.ui.ActionBar.MenuDrawable r1 = new org.telegram.ui.ActionBar.MenuDrawable
            r1.<init>()
            r10.menuDrawable = r1
            r0.setBackButtonDrawable(r1)
            org.telegram.ui.ActionBar.MenuDrawable r0 = r10.menuDrawable
            r0.setRoundCap()
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            int r1 = org.telegram.messenger.R.string.AccDescrOpenMenu
            java.lang.String r2 = "AccDescrOpenMenu"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setBackButtonContentDescription(r1)
            goto L_0x01bb
        L_0x01af:
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            org.telegram.ui.ActionBar.BackDrawable r1 = new org.telegram.ui.ActionBar.BackDrawable
            r1.<init>(r12)
            r10.backDrawable = r1
            r0.setBackButtonDrawable(r1)
        L_0x01bb:
            int r0 = r10.folderId
            if (r0 == 0) goto L_0x01cd
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            int r1 = org.telegram.messenger.R.string.ArchivedChats
            java.lang.String r2 = "ArchivedChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x020d
        L_0x01cd:
            org.telegram.ui.Components.AnimatedEmojiDrawable$SwapAnimatedEmojiDrawable r0 = new org.telegram.ui.Components.AnimatedEmojiDrawable$SwapAnimatedEmojiDrawable
            r1 = 1104150528(0x41d00000, float:26.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r2 = 0
            r0.<init>(r2, r1)
            r10.statusDrawable = r0
            r0.center = r7
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r0 == 0) goto L_0x01f1
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            int r1 = org.telegram.messenger.R.string.AppNameBeta
            java.lang.String r2 = "AppNameBeta"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.Components.AnimatedEmojiDrawable$SwapAnimatedEmojiDrawable r2 = r10.statusDrawable
            r0.setTitle(r1, r2)
            goto L_0x0200
        L_0x01f1:
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            int r1 = org.telegram.messenger.R.string.AppName
            java.lang.String r2 = "AppName"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.Components.AnimatedEmojiDrawable$SwapAnimatedEmojiDrawable r2 = r10.statusDrawable
            r0.setTitle(r1, r2)
        L_0x0200:
            int r0 = r10.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            org.telegram.tgnet.TLRPC$User r0 = r0.getCurrentUser()
            r10.updateStatus(r0, r12)
        L_0x020d:
            int r0 = r10.folderId
            if (r0 != 0) goto L_0x0216
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r0.setSupportsHolidayImage(r7)
        L_0x0216:
            boolean r0 = r10.onlySelect
            if (r0 != 0) goto L_0x0229
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r0.setAddToContainer(r12)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r0.setCastShadows(r12)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r0.setClipContent(r7)
        L_0x0229:
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda39 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda39
            r1.<init>(r10)
            r0.setTitleActionRunnable(r1)
            int r0 = r10.initialDialogsType
            if (r0 != 0) goto L_0x0264
            int r0 = r10.folderId
            if (r0 != 0) goto L_0x0264
            boolean r0 = r10.onlySelect
            if (r0 != 0) goto L_0x0264
            java.lang.String r0 = r10.searchString
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0264
            org.telegram.ui.DialogsActivity$5 r0 = new org.telegram.ui.DialogsActivity$5
            r0.<init>()
            r10.scrimPaint = r0
            org.telegram.ui.DialogsActivity$6 r0 = new org.telegram.ui.DialogsActivity$6
            r0.<init>(r11)
            r10.filterTabsView = r0
            r0.setVisibility(r9)
            r10.canShowFilterTabsView = r12
            org.telegram.ui.Components.FilterTabsView r0 = r10.filterTabsView
            org.telegram.ui.DialogsActivity$7 r1 = new org.telegram.ui.DialogsActivity$7
            r1.<init>(r11)
            r0.setDelegate(r1)
        L_0x0264:
            boolean r0 = r10.allowSwitchAccount
            r4 = 17
            r3 = 4
            r16 = 1113587712(0x42600000, float:56.0)
            if (r0 == 0) goto L_0x030c
            int r0 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()
            if (r0 <= r7) goto L_0x030c
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.addItemWithWidth(r7, r12, r0)
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
            r6 = 36
            r5 = 36
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r6, (int) r5, (int) r4)
            r2.addView(r1, r5)
            org.telegram.messenger.UserConfig r2 = r35.getUserConfig()
            org.telegram.tgnet.TLRPC$User r2 = r2.getCurrentUser()
            r0.setInfo((org.telegram.tgnet.TLRPC$User) r2)
            org.telegram.messenger.ImageReceiver r5 = r1.getImageReceiver()
            int r6 = r10.currentAccount
            r5.setCurrentAccount(r6)
            if (r2 == 0) goto L_0x02c7
            org.telegram.tgnet.TLRPC$UserProfilePhoto r5 = r2.photo
            if (r5 == 0) goto L_0x02c7
            android.graphics.drawable.BitmapDrawable r5 = r5.strippedBitmap
            if (r5 == 0) goto L_0x02c7
            r22 = r5
            goto L_0x02c9
        L_0x02c7:
            r22 = r0
        L_0x02c9:
            org.telegram.messenger.ImageLocation r18 = org.telegram.messenger.ImageLocation.getForUserOrChat(r2, r7)
            org.telegram.messenger.ImageLocation r20 = org.telegram.messenger.ImageLocation.getForUserOrChat(r2, r8)
            java.lang.String r19 = "50_50"
            java.lang.String r21 = "50_50"
            r17 = r1
            r23 = r2
            r17.setImage((org.telegram.messenger.ImageLocation) r18, (java.lang.String) r19, (org.telegram.messenger.ImageLocation) r20, (java.lang.String) r21, (android.graphics.drawable.Drawable) r22, (java.lang.Object) r23)
            r0 = 0
        L_0x02dd:
            if (r0 >= r3) goto L_0x030c
            org.telegram.messenger.AccountInstance r1 = org.telegram.messenger.AccountInstance.getInstance(r0)
            org.telegram.messenger.UserConfig r1 = r1.getUserConfig()
            org.telegram.tgnet.TLRPC$User r1 = r1.getCurrentUser()
            if (r1 == 0) goto L_0x0308
            org.telegram.ui.Cells.AccountSelectCell r1 = new org.telegram.ui.Cells.AccountSelectCell
            r1.<init>(r11, r12)
            r1.setAccount(r0, r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r10.switchItem
            int r5 = r0 + 10
            r6 = 1130758144(0x43660000, float:230.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r17 = 1111490560(0x42400000, float:48.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r2.addSubItem((int) r5, (android.view.View) r1, (int) r6, (int) r3)
        L_0x0308:
            int r0 = r0 + 1
            r3 = 4
            goto L_0x02dd
        L_0x030c:
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r0.setAllowOverlayTitle(r7)
            androidx.recyclerview.widget.RecyclerView r0 = r10.sideMenu
            if (r0 == 0) goto L_0x0332
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
        L_0x0332:
            r0 = 0
            r10.createActionMode(r0)
            org.telegram.ui.DialogsActivity$ContentView r6 = new org.telegram.ui.DialogsActivity$ContentView
            r6.<init>(r11)
            r10.fragmentView = r6
            int r0 = r10.folderId
            if (r0 != 0) goto L_0x034b
            int r0 = r10.initialDialogsType
            if (r0 != 0) goto L_0x034b
            boolean r0 = r10.onlySelect
            if (r0 != 0) goto L_0x034b
            r5 = 2
            goto L_0x034c
        L_0x034b:
            r5 = 1
        L_0x034c:
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = new org.telegram.ui.DialogsActivity.ViewPage[r5]
            r10.viewPages = r0
            r3 = 0
        L_0x0351:
            r2 = -2
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            r0 = -1
            if (r3 >= r5) goto L_0x0555
            org.telegram.ui.DialogsActivity$8 r14 = new org.telegram.ui.DialogsActivity$8
            r14.<init>(r11, r6)
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r0, r1)
            r6.addView(r14, r8)
            int r8 = r10.initialDialogsType
            int unused = r14.dialogsType = r8
            org.telegram.ui.DialogsActivity$ViewPage[] r8 = r10.viewPages
            r8[r3] = r14
            org.telegram.ui.Components.FlickerLoadingView r8 = new org.telegram.ui.Components.FlickerLoadingView
            r8.<init>(r11)
            org.telegram.ui.Components.FlickerLoadingView unused = r14.progressView = r8
            org.telegram.ui.Components.FlickerLoadingView r8 = r14.progressView
            r0 = 7
            r8.setViewType(r0)
            org.telegram.ui.Components.FlickerLoadingView r0 = r14.progressView
            r0.setVisibility(r9)
            org.telegram.ui.Components.FlickerLoadingView r0 = r14.progressView
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r2, (int) r2, (int) r4)
            r14.addView(r0, r2)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = new org.telegram.ui.DialogsActivity$DialogsRecyclerView
            r0.<init>(r11, r14)
            org.telegram.ui.DialogsActivity.DialogsRecyclerView unused = r14.listView = r0
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r14.listView
            r0.setAccessibilityEnabled(r12)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r14.listView
            r0.setAnimateEmptyView(r7, r12)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r14.listView
            r0.setClipToPadding(r12)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r14.listView
            r0.setPivotY(r15)
            org.telegram.ui.DialogsActivity$9 r0 = new org.telegram.ui.DialogsActivity$9
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r2 = r14.listView
            r0.<init>(r2, r14)
            org.telegram.ui.Components.DialogsItemAnimator unused = r14.dialogsItemAnimator = r0
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r14.listView
            org.telegram.ui.Components.DialogsItemAnimator r2 = r14.dialogsItemAnimator
            r0.setItemAnimator(r2)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r14.listView
            r0.setVerticalScrollBarEnabled(r7)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r14.listView
            r0.setInstantClick(r7)
            org.telegram.ui.DialogsActivity$10 r0 = new org.telegram.ui.DialogsActivity$10
            r0.<init>(r11, r14)
            androidx.recyclerview.widget.LinearLayoutManager unused = r14.layoutManager = r0
            androidx.recyclerview.widget.LinearLayoutManager r0 = r14.layoutManager
            r0.setOrientation(r7)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r14.listView
            androidx.recyclerview.widget.LinearLayoutManager r2 = r14.layoutManager
            r0.setLayoutManager(r2)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r14.listView
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x03fb
            r2 = 1
            goto L_0x03fc
        L_0x03fb:
            r2 = 2
        L_0x03fc:
            r0.setVerticalScrollbarPosition(r2)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r14.listView
            r8 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r1)
            r14.addView(r0, r1)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r14.listView
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda60 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda60
            r1.<init>(r10, r14)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r14.listView
            org.telegram.ui.DialogsActivity$12 r1 = new org.telegram.ui.DialogsActivity$12
            r1.<init>(r14)
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended) r1)
            org.telegram.ui.DialogsActivity$SwipeController r0 = new org.telegram.ui.DialogsActivity$SwipeController
            r0.<init>(r14)
            org.telegram.ui.DialogsActivity.SwipeController unused = r14.swipeController = r0
            org.telegram.ui.Components.RecyclerItemsEnterAnimator r0 = new org.telegram.ui.Components.RecyclerItemsEnterAnimator
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r1 = r14.listView
            r0.<init>(r1, r12)
            org.telegram.ui.Components.RecyclerItemsEnterAnimator unused = r14.recyclerItemsEnterAnimator = r0
            androidx.recyclerview.widget.ItemTouchHelper r0 = new androidx.recyclerview.widget.ItemTouchHelper
            org.telegram.ui.DialogsActivity$SwipeController r1 = r14.swipeController
            r0.<init>(r1)
            androidx.recyclerview.widget.ItemTouchHelper unused = r14.itemTouchhelper = r0
            androidx.recyclerview.widget.ItemTouchHelper r0 = r14.itemTouchhelper
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r1 = r14.listView
            r0.attachToRecyclerView(r1)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r14.listView
            org.telegram.ui.DialogsActivity$13 r1 = new org.telegram.ui.DialogsActivity$13
            r1.<init>(r14)
            r0.setOnScrollListener(r1)
            boolean r0 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r0 == 0) goto L_0x0460
            r0 = 2
            goto L_0x0461
        L_0x0460:
            r0 = 0
        L_0x0461:
            int unused = r14.archivePullViewState = r0
            org.telegram.ui.Components.PullForegroundDrawable r0 = r14.pullForegroundDrawable
            if (r0 != 0) goto L_0x04ab
            int r0 = r10.folderId
            if (r0 != 0) goto L_0x04ab
            org.telegram.ui.DialogsActivity$14 r0 = new org.telegram.ui.DialogsActivity$14
            int r1 = org.telegram.messenger.R.string.AccSwipeForArchive
            java.lang.String r2 = "AccSwipeForArchive"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            int r2 = org.telegram.messenger.R.string.AccReleaseForArchive
            java.lang.String r8 = "AccReleaseForArchive"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r8, r2)
            r0.<init>(r10, r1, r2, r14)
            org.telegram.ui.Components.PullForegroundDrawable unused = r14.pullForegroundDrawable = r0
            boolean r0 = r35.hasHiddenArchive()
            if (r0 == 0) goto L_0x0494
            org.telegram.ui.Components.PullForegroundDrawable r0 = r14.pullForegroundDrawable
            r0.showHidden()
            goto L_0x049b
        L_0x0494:
            org.telegram.ui.Components.PullForegroundDrawable r0 = r14.pullForegroundDrawable
            r0.doNotShow()
        L_0x049b:
            org.telegram.ui.Components.PullForegroundDrawable r0 = r14.pullForegroundDrawable
            int r1 = r14.archivePullViewState
            if (r1 == 0) goto L_0x04a7
            r1 = 1
            goto L_0x04a8
        L_0x04a7:
            r1 = 0
        L_0x04a8:
            r0.setWillDraw(r1)
        L_0x04ab:
            org.telegram.ui.DialogsActivity$15 r8 = new org.telegram.ui.DialogsActivity$15
            int r20 = r14.dialogsType
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
            r18 = r3
            r3 = r36
            r4 = r20
            r20 = r5
            r15 = 10
            r5 = r24
            r12 = r6
            r6 = r23
            r7 = r22
            r15 = r8
            r19 = r13
            r13 = 2
            r8 = r9
            r13 = 8
            r9 = r14
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9)
            org.telegram.ui.Adapters.DialogsAdapter unused = r14.dialogsAdapter = r15
            org.telegram.ui.Adapters.DialogsAdapter r0 = r14.dialogsAdapter
            boolean r1 = r10.afterSignup
            r0.setForceShowEmptyCell(r1)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x0503
            long r0 = r10.openedDialogId
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x0503
            org.telegram.ui.Adapters.DialogsAdapter r0 = r14.dialogsAdapter
            long r1 = r10.openedDialogId
            r0.setOpenedDialogId(r1)
        L_0x0503:
            org.telegram.ui.Adapters.DialogsAdapter r0 = r14.dialogsAdapter
            org.telegram.ui.Components.PullForegroundDrawable r1 = r14.pullForegroundDrawable
            r0.setArchivedPullDrawable(r1)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r14.listView
            org.telegram.ui.Adapters.DialogsAdapter r1 = r14.dialogsAdapter
            r0.setAdapter(r1)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r14.listView
            int r1 = r10.folderId
            if (r1 != 0) goto L_0x0526
            org.telegram.ui.Components.FlickerLoadingView r1 = r14.progressView
            goto L_0x0527
        L_0x0526:
            r1 = 0
        L_0x0527:
            r0.setEmptyView(r1)
            org.telegram.ui.Components.RecyclerAnimationScrollHelper r0 = new org.telegram.ui.Components.RecyclerAnimationScrollHelper
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r1 = r14.listView
            androidx.recyclerview.widget.LinearLayoutManager r2 = r14.layoutManager
            r0.<init>(r1, r2)
            org.telegram.ui.Components.RecyclerAnimationScrollHelper unused = r14.scrollHelper = r0
            if (r18 == 0) goto L_0x0543
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r10.viewPages
            r0 = r0[r18]
            r0.setVisibility(r13)
        L_0x0543:
            int r3 = r18 + 1
            r6 = r12
            r13 = r19
            r5 = r20
            r4 = 17
            r7 = 1
            r8 = 2
            r9 = 8
            r12 = 0
            r14 = 3
            r15 = 0
            goto L_0x0351
        L_0x0555:
            r12 = r6
            r19 = r13
            r8 = -1
            r13 = 8
            java.lang.String r0 = r10.searchString
            if (r0 == 0) goto L_0x0561
            r3 = 2
            goto L_0x0568
        L_0x0561:
            boolean r0 = r10.onlySelect
            if (r0 != 0) goto L_0x0567
            r3 = 1
            goto L_0x0568
        L_0x0567:
            r3 = 0
        L_0x0568:
            org.telegram.ui.Components.SearchViewPager r7 = new org.telegram.ui.Components.SearchViewPager
            int r4 = r10.initialDialogsType
            int r5 = r10.folderId
            org.telegram.ui.DialogsActivity$16 r6 = new org.telegram.ui.DialogsActivity$16
            r6.<init>()
            r0 = r7
            r9 = -1082130432(0xffffffffbvar_, float:-1.0)
            r1 = r36
            r14 = -2
            r2 = r35
            r0.<init>(r1, r2, r3, r4, r5, r6)
            r10.searchViewPager = r7
            r12.addView(r7)
            org.telegram.ui.Components.SearchViewPager r0 = r10.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r0 = r0.dialogsSearchAdapter
            org.telegram.ui.DialogsActivity$17 r1 = new org.telegram.ui.DialogsActivity$17
            r1.<init>()
            r0.setDelegate(r1)
            org.telegram.ui.Components.SearchViewPager r0 = r10.searchViewPager
            org.telegram.ui.Components.RecyclerListView r0 = r0.searchListView
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda59 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda59
            r1.<init>(r10)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            org.telegram.ui.Components.SearchViewPager r0 = r10.searchViewPager
            org.telegram.ui.Components.RecyclerListView r0 = r0.searchListView
            org.telegram.ui.DialogsActivity$18 r1 = new org.telegram.ui.DialogsActivity$18
            r1.<init>()
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended) r1)
            org.telegram.ui.Components.SearchViewPager r0 = r10.searchViewPager
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda61 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda61
            r1.<init>(r10)
            r0.setFilteredSearchViewDelegate(r1)
            org.telegram.ui.Components.SearchViewPager r0 = r10.searchViewPager
            r0.setVisibility(r13)
            org.telegram.ui.Adapters.FiltersView r0 = new org.telegram.ui.Adapters.FiltersView
            android.app.Activity r1 = r35.getParentActivity()
            r2 = 0
            r0.<init>(r1, r2)
            r10.filtersView = r0
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda58 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda58
            r1.<init>(r10)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            org.telegram.ui.Adapters.FiltersView r0 = r10.filtersView
            r1 = 48
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r8, (int) r14, (int) r1)
            r12.addView(r0, r1)
            org.telegram.ui.Adapters.FiltersView r0 = r10.filtersView
            r0.setVisibility(r13)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r11)
            r10.floatingButtonContainer = r0
            boolean r1 = r10.onlySelect
            if (r1 == 0) goto L_0x05eb
            int r1 = r10.initialDialogsType
            r2 = 10
            if (r1 != r2) goto L_0x05ef
        L_0x05eb:
            int r1 = r10.folderId
            if (r1 == 0) goto L_0x05f2
        L_0x05ef:
            r1 = 8
            goto L_0x05f3
        L_0x05f2:
            r1 = 0
        L_0x05f3:
            r0.setVisibility(r1)
            android.widget.FrameLayout r0 = r10.floatingButtonContainer
            int r6 = android.os.Build.VERSION.SDK_INT
            r5 = 21
            if (r6 < r5) goto L_0x0601
            r28 = 56
            goto L_0x0603
        L_0x0601:
            r28 = 60
        L_0x0603:
            if (r6 < r5) goto L_0x0608
            r1 = 56
            goto L_0x060a
        L_0x0608:
            r1 = 60
        L_0x060a:
            float r1 = (float) r1
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x0611
            r3 = 3
            goto L_0x0612
        L_0x0611:
            r3 = 5
        L_0x0612:
            r30 = r3 | 80
            if (r2 == 0) goto L_0x061b
            r3 = 1096810496(0x41600000, float:14.0)
            r31 = 1096810496(0x41600000, float:14.0)
            goto L_0x061d
        L_0x061b:
            r31 = 0
        L_0x061d:
            r32 = 0
            if (r2 == 0) goto L_0x0624
            r33 = 0
            goto L_0x0628
        L_0x0624:
            r2 = 1096810496(0x41600000, float:14.0)
            r33 = 1096810496(0x41600000, float:14.0)
        L_0x0628:
            r34 = 1096810496(0x41600000, float:14.0)
            r29 = r1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r28, r29, r30, r31, r32, r33, r34)
            r12.addView(r0, r1)
            android.widget.FrameLayout r0 = r10.floatingButtonContainer
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda22 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda22
            r1.<init>(r10)
            r0.setOnClickListener(r1)
            org.telegram.ui.Components.RLottieImageView r0 = new org.telegram.ui.Components.RLottieImageView
            r0.<init>(r11)
            r10.floatingButton = r0
            android.widget.ImageView$ScaleType r1 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r10.floatingButton
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            java.lang.String r2 = "chats_actionIcon"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            android.graphics.PorterDuff$Mode r3 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r2, r3)
            r0.setColorFilter(r1)
            int r0 = r10.initialDialogsType
            r1 = 10
            if (r0 != r1) goto L_0x0676
            org.telegram.ui.Components.RLottieImageView r0 = r10.floatingButton
            int r1 = org.telegram.messenger.R.drawable.floating_check
            r0.setImageResource(r1)
            android.widget.FrameLayout r0 = r10.floatingButtonContainer
            int r1 = org.telegram.messenger.R.string.Done
            java.lang.String r2 = "Done"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setContentDescription(r1)
            goto L_0x068e
        L_0x0676:
            org.telegram.ui.Components.RLottieImageView r0 = r10.floatingButton
            int r1 = org.telegram.messenger.R.raw.write_contacts_fab_icon
            r2 = 52
            r3 = 52
            r0.setAnimation((int) r1, (int) r2, (int) r3)
            android.widget.FrameLayout r0 = r10.floatingButtonContainer
            int r1 = org.telegram.messenger.R.string.NewMessageTitle
            java.lang.String r2 = "NewMessageTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setContentDescription(r1)
        L_0x068e:
            r18 = 1073741824(0x40000000, float:2.0)
            if (r6 < r5) goto L_0x06f8
            android.animation.StateListAnimator r0 = new android.animation.StateListAnimator
            r0.<init>()
            r4 = 1
            int[] r1 = new int[r4]
            r2 = 16842919(0x10100a7, float:2.3694026E-38)
            r3 = 0
            r1[r3] = r2
            android.widget.FrameLayout r2 = r10.floatingButtonContainer
            android.util.Property r7 = android.view.View.TRANSLATION_Z
            r15 = 2
            float[] r14 = new float[r15]
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r15 = (float) r15
            r14[r3] = r15
            r15 = 1082130432(0x40800000, float:4.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r15 = (float) r15
            r14[r4] = r15
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r7, r14)
            r14 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r2 = r2.setDuration(r14)
            r0.addState(r1, r2)
            int[] r1 = new int[r3]
            android.widget.FrameLayout r2 = r10.floatingButtonContainer
            r14 = 2
            float[] r15 = new float[r14]
            r14 = 1082130432(0x40800000, float:4.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r14 = (float) r14
            r15[r3] = r14
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r3 = (float) r3
            r15[r4] = r3
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r7, r15)
            r14 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r2 = r2.setDuration(r14)
            r0.addState(r1, r2)
            android.widget.FrameLayout r1 = r10.floatingButtonContainer
            r1.setStateListAnimator(r0)
            android.widget.FrameLayout r0 = r10.floatingButtonContainer
            org.telegram.ui.DialogsActivity$19 r1 = new org.telegram.ui.DialogsActivity$19
            r1.<init>(r10)
            r0.setOutlineProvider(r1)
            goto L_0x06f9
        L_0x06f8:
            r4 = 1
        L_0x06f9:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            java.lang.String r1 = "chats_actionBackground"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            java.lang.String r2 = "chats_actionPressedBackground"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r0, r1, r2)
            if (r6 >= r5) goto L_0x073c
            android.content.res.Resources r1 = r36.getResources()
            int r2 = org.telegram.messenger.R.drawable.floating_shadow
            android.graphics.drawable.Drawable r1 = r1.getDrawable(r2)
            android.graphics.drawable.Drawable r1 = r1.mutate()
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            r3 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r7 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r3, r7)
            r1.setColorFilter(r2)
            org.telegram.ui.Components.CombinedDrawable r2 = new org.telegram.ui.Components.CombinedDrawable
            r3 = 0
            r2.<init>(r1, r0, r3, r3)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r2.setIconSize(r0, r1)
            r7 = r2
            goto L_0x073d
        L_0x073c:
            r7 = r0
        L_0x073d:
            r35.updateFloatingButtonColor()
            android.widget.FrameLayout r0 = r10.floatingButtonContainer
            org.telegram.ui.Components.RLottieImageView r1 = r10.floatingButton
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9)
            r0.addView(r1, r2)
            org.telegram.ui.Components.RadialProgressView r0 = new org.telegram.ui.Components.RadialProgressView
            r0.<init>(r11)
            r10.floatingProgressView = r0
            java.lang.String r1 = "chats_actionIcon"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setProgressColor(r1)
            org.telegram.ui.Components.RadialProgressView r0 = r10.floatingProgressView
            r1 = 1036831949(0x3dcccccd, float:0.1)
            r0.setScaleX(r1)
            org.telegram.ui.Components.RadialProgressView r0 = r10.floatingProgressView
            r0.setScaleY(r1)
            org.telegram.ui.Components.RadialProgressView r0 = r10.floatingProgressView
            r1 = 0
            r0.setAlpha(r1)
            org.telegram.ui.Components.RadialProgressView r0 = r10.floatingProgressView
            r0.setVisibility(r13)
            org.telegram.ui.Components.RadialProgressView r0 = r10.floatingProgressView
            r1 = 1102053376(0x41b00000, float:22.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setSize(r1)
            android.widget.FrameLayout r0 = r10.floatingButtonContainer
            org.telegram.ui.Components.RadialProgressView r1 = r10.floatingProgressView
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9)
            r0.addView(r1, r2)
            r0 = 0
            r10.searchTabsView = r0
            boolean r0 = r10.onlySelect
            if (r0 != 0) goto L_0x07d9
            int r0 = r10.initialDialogsType
            if (r0 != 0) goto L_0x07d9
            org.telegram.ui.Components.FragmentContextView r0 = new org.telegram.ui.Components.FragmentContextView
            r0.<init>(r11, r10, r4)
            r10.fragmentLocationContextView = r0
            r26 = -1
            r27 = 1108869120(0x42180000, float:38.0)
            r28 = 51
            r29 = 0
            r30 = -1039138816(0xffffffffCLASSNAME, float:-36.0)
            r31 = 0
            r32 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r26, r27, r28, r29, r30, r31, r32)
            r0.setLayoutParams(r1)
            org.telegram.ui.Components.FragmentContextView r0 = r10.fragmentLocationContextView
            r12.addView(r0)
            org.telegram.ui.DialogsActivity$20 r0 = new org.telegram.ui.DialogsActivity$20
            r1 = 0
            r0.<init>(r11, r10, r1)
            r10.fragmentContextView = r0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r26, r27, r28, r29, r30, r31, r32)
            r0.setLayoutParams(r1)
            org.telegram.ui.Components.FragmentContextView r0 = r10.fragmentContextView
            r12.addView(r0)
            org.telegram.ui.Components.FragmentContextView r0 = r10.fragmentContextView
            org.telegram.ui.Components.FragmentContextView r1 = r10.fragmentLocationContextView
            r0.setAdditionalContextView(r1)
            org.telegram.ui.Components.FragmentContextView r0 = r10.fragmentLocationContextView
            org.telegram.ui.Components.FragmentContextView r1 = r10.fragmentContextView
            r0.setAdditionalContextView(r1)
            goto L_0x09ee
        L_0x07d9:
            int r0 = r10.initialDialogsType
            r1 = 3
            if (r0 != r1) goto L_0x09ee
            org.telegram.ui.Components.ChatActivityEnterView r0 = r10.commentView
            if (r0 == 0) goto L_0x07e5
            r0.onDestroy()
        L_0x07e5:
            org.telegram.ui.DialogsActivity$21 r14 = new org.telegram.ui.DialogsActivity$21
            android.app.Activity r2 = r35.getParentActivity()
            r15 = 0
            r17 = 0
            r0 = r14
            r1 = r35
            r3 = r12
            r9 = 1
            r4 = r15
            r15 = 21
            r5 = r17
            r0.<init>(r2, r3, r4, r5)
            r10.commentView = r14
            r0 = 0
            r12.setClipChildren(r0)
            r12.setClipToPadding(r0)
            org.telegram.ui.Components.ChatActivityEnterView r1 = r10.commentView
            r1.allowBlur = r0
            r1.forceSmoothKeyboard(r9)
            org.telegram.ui.Components.ChatActivityEnterView r1 = r10.commentView
            r1.setAllowStickersAndGifs(r9, r0, r0)
            org.telegram.ui.Components.ChatActivityEnterView r1 = r10.commentView
            r1.setForceShowSendButton(r9, r0)
            org.telegram.ui.Components.ChatActivityEnterView r1 = r10.commentView
            r2 = 1101004800(0x41a00000, float:20.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setPadding(r0, r0, r2, r0)
            org.telegram.ui.Components.ChatActivityEnterView r0 = r10.commentView
            r0.setVisibility(r13)
            org.telegram.ui.Components.ChatActivityEnterView r0 = r10.commentView
            android.view.View r0 = r0.getSendButton()
            r1 = 0
            r0.setAlpha(r1)
            android.view.View r0 = new android.view.View
            android.app.Activity r1 = r35.getParentActivity()
            r0.<init>(r1)
            r10.commentViewBg = r0
            java.lang.String r1 = "chat_messagePanelBackground"
            int r1 = r10.getThemedColor(r1)
            r0.setBackgroundColor(r1)
            android.view.View r0 = r10.commentViewBg
            r28 = -1
            r29 = 1153957888(0x44CLASSNAME, float:1600.0)
            r30 = 87
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = -993525760(0xffffffffc4CLASSNAME, float:-1600.0)
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r28, r29, r30, r31, r32, r33, r34)
            r12.addView(r0, r1)
            org.telegram.ui.Components.ChatActivityEnterView r0 = r10.commentView
            r1 = 83
            r2 = -2
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r8, (int) r2, (int) r1)
            r12.addView(r0, r1)
            org.telegram.ui.Components.ChatActivityEnterView r0 = r10.commentView
            org.telegram.ui.DialogsActivity$22 r1 = new org.telegram.ui.DialogsActivity$22
            r1.<init>()
            r0.setDelegate(r1)
            org.telegram.ui.DialogsActivity$23 r0 = new org.telegram.ui.DialogsActivity$23
            r0.<init>(r11)
            r10.writeButtonContainer = r0
            r0.setFocusable(r9)
            android.widget.FrameLayout r0 = r10.writeButtonContainer
            r0.setFocusableInTouchMode(r9)
            android.widget.FrameLayout r0 = r10.writeButtonContainer
            r1 = 4
            r0.setVisibility(r1)
            android.widget.FrameLayout r0 = r10.writeButtonContainer
            r2 = 1045220557(0x3e4ccccd, float:0.2)
            r0.setScaleX(r2)
            android.widget.FrameLayout r0 = r10.writeButtonContainer
            r0.setScaleY(r2)
            android.widget.FrameLayout r0 = r10.writeButtonContainer
            r3 = 0
            r0.setAlpha(r3)
            android.widget.FrameLayout r0 = r10.writeButtonContainer
            r28 = 60
            r29 = 1114636288(0x42700000, float:60.0)
            r30 = 85
            r33 = 1086324736(0x40CLASSNAME, float:6.0)
            r34 = 1092616192(0x41200000, float:10.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r28, r29, r30, r31, r32, r33, r34)
            r12.addView(r0, r3)
            android.text.TextPaint r0 = r10.textPaint
            r3 = 1094713344(0x41400000, float:12.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            r0.setTextSize(r3)
            android.text.TextPaint r0 = r10.textPaint
            java.lang.String r3 = "fonts/rmedium.ttf"
            android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3)
            r0.setTypeface(r3)
            org.telegram.ui.DialogsActivity$24 r0 = new org.telegram.ui.DialogsActivity$24
            r0.<init>(r11)
            r10.selectedCountView = r0
            r3 = 0
            r0.setAlpha(r3)
            android.view.View r0 = r10.selectedCountView
            r0.setScaleX(r2)
            android.view.View r0 = r10.selectedCountView
            r0.setScaleY(r2)
            android.view.View r0 = r10.selectedCountView
            r28 = 42
            r29 = 1103101952(0x41CLASSNAME, float:24.0)
            r33 = -1056964608(0xffffffffCLASSNAME, float:-8.0)
            r34 = 1091567616(0x41100000, float:9.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r28, r29, r30, r31, r32, r33, r34)
            r12.addView(r0, r2)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r11)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            java.lang.String r4 = "dialogFloatingButton"
            int r4 = r10.getThemedColor(r4)
            if (r6 < r15) goto L_0x08fc
            java.lang.String r5 = "dialogFloatingButtonPressed"
            goto L_0x08fe
        L_0x08fc:
            java.lang.String r5 = "dialogFloatingButton"
        L_0x08fe:
            int r5 = r10.getThemedColor(r5)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r2, r4, r5)
            if (r6 >= r15) goto L_0x0934
            android.content.res.Resources r2 = r36.getResources()
            int r4 = org.telegram.messenger.R.drawable.floating_shadow_profile
            android.graphics.drawable.Drawable r2 = r2.getDrawable(r4)
            android.graphics.drawable.Drawable r2 = r2.mutate()
            android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
            r5 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r14 = android.graphics.PorterDuff.Mode.MULTIPLY
            r4.<init>(r5, r14)
            r2.setColorFilter(r4)
            org.telegram.ui.Components.CombinedDrawable r4 = new org.telegram.ui.Components.CombinedDrawable
            r5 = 0
            r4.<init>(r2, r7, r5, r5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r4.setIconSize(r2, r5)
            r2 = r4
        L_0x0934:
            r0.setBackgroundDrawable(r2)
            r2 = 2
            r0.setImportantForAccessibility(r2)
            if (r6 < r15) goto L_0x0945
            org.telegram.ui.DialogsActivity$25 r2 = new org.telegram.ui.DialogsActivity$25
            r2.<init>(r10)
            r0.setOutlineProvider(r2)
        L_0x0945:
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda20 r2 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda20
            r2.<init>(r10)
            r0.setOnClickListener(r2)
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda30 r2 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda30
            r2.<init>(r10, r0)
            r0.setOnLongClickListener(r2)
            r2 = 2
            android.widget.ImageView[] r4 = new android.widget.ImageView[r2]
            r10.writeButton = r4
            r4 = 0
        L_0x095b:
            if (r4 >= r2) goto L_0x09b2
            android.widget.ImageView[] r2 = r10.writeButton
            android.widget.ImageView r5 = new android.widget.ImageView
            r5.<init>(r11)
            r2[r4] = r5
            android.widget.ImageView[] r2 = r10.writeButton
            r2 = r2[r4]
            if (r4 != r9) goto L_0x096f
            int r5 = org.telegram.messenger.R.drawable.msg_arrow_forward
            goto L_0x0971
        L_0x096f:
            int r5 = org.telegram.messenger.R.drawable.attach_send
        L_0x0971:
            r2.setImageResource(r5)
            android.widget.ImageView[] r2 = r10.writeButton
            r2 = r2[r4]
            android.graphics.PorterDuffColorFilter r5 = new android.graphics.PorterDuffColorFilter
            java.lang.String r6 = "dialogFloatingIcon"
            int r6 = r10.getThemedColor(r6)
            android.graphics.PorterDuff$Mode r7 = android.graphics.PorterDuff.Mode.MULTIPLY
            r5.<init>(r6, r7)
            r2.setColorFilter(r5)
            android.widget.ImageView[] r2 = r10.writeButton
            r2 = r2[r4]
            android.widget.ImageView$ScaleType r5 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r5)
            android.widget.ImageView[] r2 = r10.writeButton
            r2 = r2[r4]
            int r5 = android.os.Build.VERSION.SDK_INT
            if (r5 < r15) goto L_0x099c
            r6 = 56
            goto L_0x099e
        L_0x099c:
            r6 = 60
        L_0x099e:
            if (r5 < r15) goto L_0x09a3
            r5 = 56
            goto L_0x09a5
        L_0x09a3:
            r5 = 60
        L_0x09a5:
            r7 = 17
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r6, (int) r5, (int) r7)
            r0.addView(r2, r5)
            int r4 = r4 + 1
            r2 = 2
            goto L_0x095b
        L_0x09b2:
            android.widget.ImageView[] r2 = r10.writeButton
            r4 = 0
            r2 = r2[r4]
            r5 = 1056964608(0x3var_, float:0.5)
            org.telegram.messenger.AndroidUtilities.updateViewVisibilityAnimated(r2, r9, r5, r4)
            android.widget.ImageView[] r2 = r10.writeButton
            r2 = r2[r9]
            org.telegram.messenger.AndroidUtilities.updateViewVisibilityAnimated(r2, r4, r5, r4)
            android.widget.FrameLayout r2 = r10.writeButtonContainer
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 < r15) goto L_0x09cc
            r28 = 56
            goto L_0x09ce
        L_0x09cc:
            r28 = 60
        L_0x09ce:
            if (r4 < r15) goto L_0x09d3
            r29 = 1113587712(0x42600000, float:56.0)
            goto L_0x09d7
        L_0x09d3:
            r16 = 1114636288(0x42700000, float:60.0)
            r29 = 1114636288(0x42700000, float:60.0)
        L_0x09d7:
            r30 = 51
            if (r4 < r15) goto L_0x09de
            r31 = 1073741824(0x40000000, float:2.0)
            goto L_0x09e0
        L_0x09de:
            r31 = 0
        L_0x09e0:
            r32 = 0
            r33 = 0
            r34 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r28, r29, r30, r31, r32, r33, r34)
            r2.addView(r0, r3)
            goto L_0x09f2
        L_0x09ee:
            r1 = 4
            r9 = 1
            r15 = 21
        L_0x09f2:
            org.telegram.ui.Components.FilterTabsView r0 = r10.filterTabsView
            if (r0 == 0) goto L_0x09ff
            r2 = 1110441984(0x42300000, float:44.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r2)
            r12.addView(r0, r2)
        L_0x09ff:
            boolean r0 = r10.onlySelect
            if (r0 != 0) goto L_0x0a2e
            r0 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r0)
            boolean r2 = r10.inPreviewMode
            if (r2 == 0) goto L_0x0a15
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 < r15) goto L_0x0a15
            int r2 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            r0.topMargin = r2
        L_0x0a15:
            org.telegram.ui.ActionBar.ActionBar r2 = r10.actionBar
            r12.addView(r2, r0)
            org.telegram.ui.Cells.DrawerProfileCell$AnimatedStatusView r0 = new org.telegram.ui.Cells.DrawerProfileCell$AnimatedStatusView
            r2 = 20
            r3 = 60
            r0.<init>(r11, r2, r3)
            r10.animatedStatusView = r0
            r3 = 51
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r2, (int) r2, (int) r3)
            r12.addView(r0, r2)
        L_0x0a2e:
            java.lang.String r0 = r10.searchString
            if (r0 != 0) goto L_0x0af0
            int r0 = r10.initialDialogsType
            if (r0 != 0) goto L_0x0af0
            org.telegram.ui.DialogsActivity$26 r0 = new org.telegram.ui.DialogsActivity$26
            r0.<init>(r11)
            r10.updateLayout = r0
            r2 = 0
            r0.setWillNotDraw(r2)
            android.widget.FrameLayout r0 = r10.updateLayout
            r0.setVisibility(r1)
            android.widget.FrameLayout r0 = r10.updateLayout
            r2 = 1111490560(0x42400000, float:48.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r0.setTranslationY(r2)
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r15) goto L_0x0a63
            android.widget.FrameLayout r0 = r10.updateLayout
            r2 = 1090519039(0x40ffffff, float:7.9999995)
            r3 = 0
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable((int) r2, (boolean) r3)
            r0.setBackground(r2)
        L_0x0a63:
            android.widget.FrameLayout r0 = r10.updateLayout
            r2 = 48
            r3 = 83
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r8, (int) r2, (int) r3)
            r12.addView(r0, r2)
            android.widget.FrameLayout r0 = r10.updateLayout
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda18 r2 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda18
            r2.<init>(r10)
            r0.setOnClickListener(r2)
            org.telegram.ui.Components.RadialProgress2 r0 = new org.telegram.ui.Components.RadialProgress2
            android.widget.FrameLayout r2 = r10.updateLayout
            r0.<init>(r2)
            r10.updateLayoutIcon = r0
            r0.setColors((int) r8, (int) r8, (int) r8, (int) r8)
            org.telegram.ui.Components.RadialProgress2 r0 = r10.updateLayoutIcon
            r2 = 1093664768(0x41300000, float:11.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setCircleRadius(r2)
            org.telegram.ui.Components.RadialProgress2 r0 = r10.updateLayoutIcon
            r0.setAsMini()
            org.telegram.ui.Components.RadialProgress2 r0 = r10.updateLayoutIcon
            r2 = 15
            r3 = 0
            r0.setIcon(r2, r9, r3)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r11)
            r10.updateTextView = r0
            r2 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r9, r2)
            android.widget.TextView r0 = r10.updateTextView
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r0.setTypeface(r2)
            android.widget.TextView r0 = r10.updateTextView
            int r2 = org.telegram.messenger.R.string.AppUpdateNow
            java.lang.String r3 = "AppUpdateNow"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r2 = r2.toUpperCase()
            r0.setText(r2)
            android.widget.TextView r0 = r10.updateTextView
            r0.setTextColor(r8)
            android.widget.TextView r0 = r10.updateTextView
            r2 = 1106247680(0x41var_, float:30.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r3 = 0
            r0.setPadding(r2, r3, r3, r3)
            android.widget.FrameLayout r0 = r10.updateLayout
            android.widget.TextView r2 = r10.updateTextView
            r28 = -2
            r29 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r30 = 17
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r28, r29, r30, r31, r32, r33, r34)
            r0.addView(r2, r3)
        L_0x0af0:
            r0 = 0
        L_0x0af1:
            r2 = 2
            if (r0 >= r2) goto L_0x0b19
            org.telegram.ui.Components.UndoView[] r2 = r10.undoView
            org.telegram.ui.DialogsActivity$27 r3 = new org.telegram.ui.DialogsActivity$27
            r3.<init>(r11)
            r2[r0] = r3
            org.telegram.ui.Components.UndoView[] r2 = r10.undoView
            r2 = r2[r0]
            r28 = -1
            r29 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r30 = 83
            r31 = 1090519040(0x41000000, float:8.0)
            r32 = 0
            r33 = 1090519040(0x41000000, float:8.0)
            r34 = 1090519040(0x41000000, float:8.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r28, r29, r30, r31, r32, r33, r34)
            r12.addView(r2, r3)
            int r0 = r0 + 1
            goto L_0x0af1
        L_0x0b19:
            int r0 = r10.folderId
            if (r0 == 0) goto L_0x0b67
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r10.viewPages
            r2 = 0
            r0 = r0[r2]
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r0.listView
            java.lang.String r2 = "actionBarDefaultArchived"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setGlowColor(r2)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            java.lang.String r2 = "actionBarDefaultArchivedTitle"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setTitleColor(r2)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            java.lang.String r2 = "actionBarDefaultArchivedIcon"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r3 = 0
            r0.setItemsColor(r2, r3)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            java.lang.String r2 = "actionBarDefaultArchivedSelector"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setItemsBackgroundColor(r2, r3)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            java.lang.String r2 = "actionBarDefaultArchivedSearch"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setSearchTextColor(r2, r3)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            java.lang.String r2 = "actionBarDefaultSearchArchivedPlaceholder"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setSearchTextColor(r2, r9)
        L_0x0b67:
            boolean r0 = r10.onlySelect
            if (r0 != 0) goto L_0x0bbb
            int r0 = r10.initialDialogsType
            if (r0 != 0) goto L_0x0bbb
            org.telegram.ui.DialogsActivity$28 r0 = new org.telegram.ui.DialogsActivity$28
            r0.<init>(r11)
            r10.blurredView = r0
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 23
            if (r2 < r3) goto L_0x0b90
            android.graphics.drawable.ColorDrawable r2 = new android.graphics.drawable.ColorDrawable
            java.lang.String r3 = "windowBackgroundWhite"
            int r3 = r10.getThemedColor(r3)
            r4 = 100
            int r3 = androidx.core.graphics.ColorUtils.setAlphaComponent(r3, r4)
            r2.<init>(r3)
            r0.setForeground(r2)
        L_0x0b90:
            android.view.View r0 = r10.blurredView
            r2 = 0
            r0.setFocusable(r2)
            android.view.View r0 = r10.blurredView
            r2 = 2
            r0.setImportantForAccessibility(r2)
            android.view.View r0 = r10.blurredView
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda17 r2 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda17
            r2.<init>(r10)
            r0.setOnClickListener(r2)
            android.view.View r0 = r10.blurredView
            r0.setVisibility(r13)
            android.view.View r0 = r10.blurredView
            r0.setFitsSystemWindows(r9)
            android.view.View r0 = r10.blurredView
            r2 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r2)
            r12.addView(r0, r2)
        L_0x0bbb:
            android.graphics.Paint r0 = r10.actionBarDefaultPaint
            int r2 = r10.folderId
            if (r2 != 0) goto L_0x0bc4
            r2 = r19
            goto L_0x0bc6
        L_0x0bc4:
            java.lang.String r2 = "actionBarDefaultArchived"
        L_0x0bc6:
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setColor(r2)
            boolean r0 = r10.inPreviewMode
            if (r0 == 0) goto L_0x0c4a
            org.telegram.messenger.UserConfig r0 = r35.getUserConfig()
            org.telegram.tgnet.TLRPC$User r0 = r0.getCurrentUser()
            org.telegram.ui.Components.ChatAvatarContainer r2 = new org.telegram.ui.Components.ChatAvatarContainer
            org.telegram.ui.ActionBar.ActionBar r3 = r10.actionBar
            android.content.Context r3 = r3.getContext()
            r4 = 0
            r5 = 0
            r2.<init>(r3, r4, r5)
            r10.avatarContainer = r2
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r0)
            r2.setTitle(r3)
            org.telegram.ui.Components.ChatAvatarContainer r2 = r10.avatarContainer
            int r3 = r10.currentAccount
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatUserStatus(r3, r0)
            r2.setSubtitle(r3)
            org.telegram.ui.Components.ChatAvatarContainer r2 = r10.avatarContainer
            r2.setUserAvatar(r0, r9)
            org.telegram.ui.Components.ChatAvatarContainer r0 = r10.avatarContainer
            r0.setOccupyStatusBar(r5)
            org.telegram.ui.Components.ChatAvatarContainer r0 = r10.avatarContainer
            r2 = 1092616192(0x41200000, float:10.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setLeftPadding(r2)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            org.telegram.ui.Components.ChatAvatarContainer r2 = r10.avatarContainer
            r25 = -2
            r26 = -1082130432(0xffffffffbvar_, float:-1.0)
            r27 = 51
            r28 = 0
            r29 = 0
            r30 = 1109393408(0x42200000, float:40.0)
            r31 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r4 = 0
            r0.addView(r2, r4, r3)
            org.telegram.ui.Components.RLottieImageView r0 = r10.floatingButton
            r0.setVisibility(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r0.setOccupyStatusBar(r4)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r19)
            r0.setBackgroundColor(r1)
            org.telegram.ui.Components.FragmentContextView r0 = r10.fragmentContextView
            if (r0 == 0) goto L_0x0CLASSNAME
            r12.removeView(r0)
        L_0x0CLASSNAME:
            org.telegram.ui.Components.FragmentContextView r0 = r10.fragmentLocationContextView
            if (r0 == 0) goto L_0x0c4a
            r12.removeView(r0)
        L_0x0c4a:
            r0 = 0
            r10.searchIsShowed = r0
            r10.updateFilterTabs(r0, r0)
            java.lang.String r1 = r10.searchString
            if (r1 == 0) goto L_0x0c5f
            r10.showSearch(r9, r0, r0)
            org.telegram.ui.ActionBar.ActionBar r1 = r10.actionBar
            java.lang.String r2 = r10.searchString
            r1.openSearchField(r2, r0)
            goto L_0x0CLASSNAME
        L_0x0c5f:
            java.lang.String r1 = r10.initialSearchString
            if (r1 == 0) goto L_0x0CLASSNAME
            r10.showSearch(r9, r0, r0)
            org.telegram.ui.ActionBar.ActionBar r1 = r10.actionBar
            java.lang.String r2 = r10.initialSearchString
            r1.openSearchField(r2, r0)
            r1 = 0
            r10.initialSearchString = r1
            org.telegram.ui.Components.FilterTabsView r1 = r10.filterTabsView
            if (r1 == 0) goto L_0x0CLASSNAME
            r2 = 1110441984(0x42300000, float:44.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = -r2
            float r2 = (float) r2
            r1.setTranslationY(r2)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r10.showSearch(r0, r0, r0)
        L_0x0CLASSNAME:
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 30
            if (r1 < r2) goto L_0x0c8c
            org.telegram.messenger.FilesMigrationService.checkBottomSheet(r35)
        L_0x0c8c:
            r10.updateMenuButton(r0)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r0.setDrawBlurBackground(r12)
            android.view.View r0 = r10.fragmentView
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.createView(android.content.Context):android.view.View");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$6(View view) {
        this.filterTabsView.setIsEditing(false);
        showDoneItem(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$7() {
        if (this.initialDialogsType != 10) {
            hideFloatingButton(false);
        }
        scrollToTop();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$8(ViewPage viewPage, View view, int i) {
        int i2 = this.initialDialogsType;
        if (i2 == 10) {
            onItemLongClick(viewPage.listView, view, i, 0.0f, 0.0f, viewPage.dialogsType, viewPage.dialogsAdapter);
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
                    DialogsActivityDelegate access$25400 = DialogsActivity.this.delegate;
                    if (DialogsActivity.this.closeFragment) {
                        DialogsActivity.this.removeSelfFromStack();
                    }
                    access$25400.didSelectDialogs(DialogsActivity.this, arrayList, (CharSequence) null, true);
                }
            });
            presentFragment(groupCreateFinalActivity);
        } else {
            onItemClick(view, i, viewPage.dialogsAdapter);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$9(View view, int i) {
        if (this.initialDialogsType == 10) {
            SearchViewPager searchViewPager2 = this.searchViewPager;
            onItemLongClick(searchViewPager2.searchListView, view, i, 0.0f, 0.0f, -1, searchViewPager2.dialogsSearchAdapter);
            return;
        }
        onItemClick(view, i, this.searchViewPager.dialogsSearchAdapter);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$10(boolean z, ArrayList arrayList, ArrayList arrayList2, boolean z2) {
        updateFiltersView(z, arrayList, arrayList2, z2, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$11(View view, int i) {
        this.filtersView.cancelClickRunnables(true);
        addSearchFilter(this.filtersView.getFilterAt(i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$12(View view) {
        ActionBarLayout actionBarLayout = this.parentLayout;
        if (actionBarLayout != null && actionBarLayout.isInPreviewMode()) {
            finishPreviewFragment();
        } else if (this.initialDialogsType == 10) {
            if (this.delegate != null && !this.selectedDialogs.isEmpty()) {
                this.delegate.didSelectDialogs(this, this.selectedDialogs, (CharSequence) null, false);
            }
        } else if (this.floatingButton.getVisibility() == 0) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("destroyAfterSelect", true);
            presentFragment(new ContactsActivity(bundle));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$13(View view) {
        if (this.delegate != null && !this.selectedDialogs.isEmpty()) {
            this.delegate.didSelectDialogs(this, this.selectedDialogs, this.commentView.getFieldText(), false);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$14(FrameLayout frameLayout, View view) {
        if (this.isNextButton) {
            return false;
        }
        onSendLongClick(frameLayout);
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$15(View view) {
        if (SharedConfig.isAppUpdateAvailable()) {
            AndroidUtilities.openForView(SharedConfig.pendingAppUpdate.document, true, getParentActivity());
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$16(View view) {
        finishPreviewFragment();
    }

    public void showSelectStatusDialog() {
        int i;
        int i2;
        if (this.selectAnimatedEmojiDialog == null) {
            SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[] selectAnimatedEmojiDialogWindowArr = new SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[1];
            TLRPC$User currentUser = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
            SimpleTextView titleTextView = this.actionBar.getTitleTextView();
            if (titleTextView == null || titleTextView.getRightDrawable() == null) {
                i2 = 0;
                i = 0;
            } else {
                this.statusDrawable.play();
                boolean z = this.statusDrawable.getDrawable() instanceof AnimatedEmojiDrawable;
                Rect rect2 = AndroidUtilities.rectTmp2;
                rect2.set(titleTextView.getRightDrawable().getBounds());
                rect2.offset((int) titleTextView.getX(), (int) titleTextView.getY());
                int dp = (-(this.actionBar.getHeight() - rect2.centerY())) - AndroidUtilities.dp(16.0f);
                i2 = rect2.centerX() - AndroidUtilities.dp(16.0f);
                DrawerProfileCell.AnimatedStatusView animatedStatusView2 = this.animatedStatusView;
                if (animatedStatusView2 != null) {
                    animatedStatusView2.translate((float) rect2.centerX(), (float) rect2.centerY());
                }
                i = dp;
            }
            AnonymousClass29 r14 = r0;
            int i3 = i;
            final SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[] selectAnimatedEmojiDialogWindowArr2 = selectAnimatedEmojiDialogWindowArr;
            AnonymousClass29 r0 = new SelectAnimatedEmojiDialog(this, getContext(), true, Integer.valueOf(i2), 0, getResourceProvider()) {
                /* access modifiers changed from: protected */
                public void onEmojiSelected(View view, Long l, TLRPC$Document tLRPC$Document, Integer num) {
                    TLRPC$TL_account_updateEmojiStatus tLRPC$TL_account_updateEmojiStatus = new TLRPC$TL_account_updateEmojiStatus();
                    if (l == null) {
                        tLRPC$TL_account_updateEmojiStatus.emoji_status = new TLRPC$TL_emojiStatusEmpty();
                    } else if (num != null) {
                        TLRPC$TL_emojiStatusUntil tLRPC$TL_emojiStatusUntil = new TLRPC$TL_emojiStatusUntil();
                        tLRPC$TL_account_updateEmojiStatus.emoji_status = tLRPC$TL_emojiStatusUntil;
                        tLRPC$TL_emojiStatusUntil.document_id = l.longValue();
                        ((TLRPC$TL_emojiStatusUntil) tLRPC$TL_account_updateEmojiStatus.emoji_status).until = num.intValue();
                    } else {
                        TLRPC$TL_emojiStatus tLRPC$TL_emojiStatus = new TLRPC$TL_emojiStatus();
                        tLRPC$TL_account_updateEmojiStatus.emoji_status = tLRPC$TL_emojiStatus;
                        tLRPC$TL_emojiStatus.document_id = l.longValue();
                    }
                    TLRPC$User user = MessagesController.getInstance(DialogsActivity.this.currentAccount).getUser(Long.valueOf(UserConfig.getInstance(DialogsActivity.this.currentAccount).getClientUserId()));
                    if (user != null) {
                        user.emoji_status = tLRPC$TL_account_updateEmojiStatus.emoji_status;
                        NotificationCenter.getInstance(DialogsActivity.this.currentAccount).postNotificationName(NotificationCenter.userEmojiStatusUpdated, user);
                        DialogsActivity.this.getMessagesController().updateEmojiStatusUntilUpdate(user.id, user.emoji_status);
                    }
                    if (l != null) {
                        DialogsActivity.this.animatedStatusView.animateChange(ReactionsLayoutInBubble.VisibleReaction.fromCustomEmoji(l));
                    }
                    ConnectionsManager.getInstance(DialogsActivity.this.currentAccount).sendRequest(tLRPC$TL_account_updateEmojiStatus, DialogsActivity$29$$ExternalSyntheticLambda0.INSTANCE);
                    if (selectAnimatedEmojiDialogWindowArr2[0] != null) {
                        SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow unused = DialogsActivity.this.selectAnimatedEmojiDialog = null;
                        selectAnimatedEmojiDialogWindowArr2[0].dismiss();
                    }
                }

                /* access modifiers changed from: private */
                public static /* synthetic */ void lambda$onEmojiSelected$0(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    boolean z = tLObject instanceof TLRPC$TL_boolTrue;
                }
            };
            if (currentUser != null) {
                TLRPC$EmojiStatus tLRPC$EmojiStatus = currentUser.emoji_status;
                if ((tLRPC$EmojiStatus instanceof TLRPC$TL_emojiStatusUntil) && ((TLRPC$TL_emojiStatusUntil) tLRPC$EmojiStatus).until > ((int) (System.currentTimeMillis() / 1000))) {
                    r14.setExpireDateHint(((TLRPC$TL_emojiStatusUntil) currentUser.emoji_status).until);
                }
            }
            r14.setSelected(this.statusDrawable.getDrawable() instanceof AnimatedEmojiDrawable ? Long.valueOf(((AnimatedEmojiDrawable) this.statusDrawable.getDrawable()).getDocumentId()) : null);
            r14.setSaveState(1);
            r14.setScrimDrawable(this.statusDrawable, titleTextView);
            AnonymousClass30 r02 = new SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow(r14, -2, -2) {
                public void dismiss() {
                    super.dismiss();
                    SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow unused = DialogsActivity.this.selectAnimatedEmojiDialog = null;
                }
            };
            this.selectAnimatedEmojiDialog = r02;
            selectAnimatedEmojiDialogWindowArr[0] = r02;
            selectAnimatedEmojiDialogWindowArr[0].showAsDropDown(this.actionBar, AndroidUtilities.dp(16.0f), i3, 48);
            selectAnimatedEmojiDialogWindowArr[0].dimBehind();
        }
    }

    private void updateAppUpdateViews(boolean z) {
        boolean z2;
        if (this.updateLayout != null) {
            if (SharedConfig.isAppUpdateAvailable()) {
                FileLoader.getAttachFileName(SharedConfig.pendingAppUpdate.document);
                z2 = getFileLoader().getPathToAttach(SharedConfig.pendingAppUpdate.document, true).exists();
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
            createActionMode.setBackgroundColor(0);
            createActionMode.drawBlur = false;
            NumberTextView numberTextView = new NumberTextView(createActionMode.getContext());
            this.selectedDialogsCountTextView = numberTextView;
            numberTextView.setTextSize(18);
            this.selectedDialogsCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.selectedDialogsCountTextView.setTextColor(Theme.getColor("actionBarActionModeDefaultIcon"));
            createActionMode.addView(this.selectedDialogsCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 72, 0, 0, 0));
            this.selectedDialogsCountTextView.setOnTouchListener(DialogsActivity$$ExternalSyntheticLambda31.INSTANCE);
            int i = R.drawable.msg_pin;
            this.pinItem = createActionMode.addItemWithWidth(100, i, AndroidUtilities.dp(54.0f));
            this.muteItem = createActionMode.addItemWithWidth(104, R.drawable.msg_mute, AndroidUtilities.dp(54.0f));
            int i2 = R.drawable.msg_archive;
            this.archive2Item = createActionMode.addItemWithWidth(107, i2, AndroidUtilities.dp(54.0f));
            this.deleteItem = createActionMode.addItemWithWidth(102, R.drawable.msg_delete, AndroidUtilities.dp(54.0f), (CharSequence) LocaleController.getString("Delete", R.string.Delete));
            ActionBarMenuItem addItemWithWidth = createActionMode.addItemWithWidth(0, R.drawable.ic_ab_other, AndroidUtilities.dp(54.0f), (CharSequence) LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
            this.archiveItem = addItemWithWidth.addSubItem(105, i2, LocaleController.getString("Archive", R.string.Archive));
            this.pin2Item = addItemWithWidth.addSubItem(108, i, LocaleController.getString("DialogPin", R.string.DialogPin));
            this.addToFolderItem = addItemWithWidth.addSubItem(109, R.drawable.msg_addfolder, LocaleController.getString("FilterAddTo", R.string.FilterAddTo));
            this.removeFromFolderItem = addItemWithWidth.addSubItem(110, R.drawable.msg_removefolder, LocaleController.getString("FilterRemoveFrom", R.string.FilterRemoveFrom));
            this.readItem = addItemWithWidth.addSubItem(101, R.drawable.msg_markread, LocaleController.getString("MarkAsRead", R.string.MarkAsRead));
            this.clearItem = addItemWithWidth.addSubItem(103, R.drawable.msg_clear, LocaleController.getString("ClearHistory", R.string.ClearHistory));
            this.blockItem = addItemWithWidth.addSubItem(106, R.drawable.msg_block, LocaleController.getString("BlockUser", R.string.BlockUser));
            this.actionModeViews.add(this.pinItem);
            this.actionModeViews.add(this.archive2Item);
            this.actionModeViews.add(this.muteItem);
            this.actionModeViews.add(this.deleteItem);
            this.actionModeViews.add(addItemWithWidth);
            if (str == null) {
                this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
                    public void onItemClick(int i) {
                        int i2 = i;
                        if ((i2 == 201 || i2 == 200 || i2 == 202) && DialogsActivity.this.searchViewPager != null) {
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
                                ((LaunchActivity) DialogsActivity.this.getParentActivity()).showPasscodeActivity(false, true, iArr[0] + (DialogsActivity.this.passcodeItem.getMeasuredWidth() / 2), iArr[1] + (DialogsActivity.this.passcodeItem.getMeasuredHeight() / 2), new DialogsActivity$33$$ExternalSyntheticLambda0(this), new DialogsActivity$33$$ExternalSyntheticLambda1(this));
                                DialogsActivity.this.updatePasscodeButton();
                            }
                        } else if (i2 == 2) {
                            DialogsActivity.this.presentFragment(new ProxyListActivity());
                        } else if (i2 == 3) {
                            DialogsActivity.this.showSearch(true, true, true);
                            DialogsActivity.this.actionBar.openSearchField(true);
                        } else if (i2 < 10 || i2 >= 14) {
                            if (i2 == 109) {
                                DialogsActivity dialogsActivity = DialogsActivity.this;
                                FiltersListBottomSheet filtersListBottomSheet = new FiltersListBottomSheet(dialogsActivity, dialogsActivity.selectedDialogs);
                                filtersListBottomSheet.setDelegate(new DialogsActivity$33$$ExternalSyntheticLambda2(this));
                                DialogsActivity.this.showDialog(filtersListBottomSheet);
                            } else if (i2 == 110) {
                                MessagesController.DialogFilter dialogFilter = DialogsActivity.this.getMessagesController().dialogFilters.get(DialogsActivity.this.viewPages[0].selectedType);
                                DialogsActivity dialogsActivity2 = DialogsActivity.this;
                                ArrayList<Long> dialogsCount = FiltersListBottomSheet.getDialogsCount(dialogsActivity2, dialogFilter, dialogsActivity2.selectedDialogs, false, false);
                                if ((dialogFilter != null ? dialogFilter.neverShow.size() : 0) + dialogsCount.size() > 100) {
                                    DialogsActivity dialogsActivity3 = DialogsActivity.this;
                                    dialogsActivity3.showDialog(AlertsCreator.createSimpleAlert(dialogsActivity3.getParentActivity(), LocaleController.getString("FilterAddToAlertFullTitle", R.string.FilterAddToAlertFullTitle), LocaleController.getString("FilterAddToAlertFullText", R.string.FilterAddToAlertFullText)).create());
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
                            DialogsActivityDelegate access$25400 = DialogsActivity.this.delegate;
                            LaunchActivity launchActivity = (LaunchActivity) DialogsActivity.this.getParentActivity();
                            launchActivity.switchToAccount(i2 - 10, true);
                            DialogsActivity dialogsActivity5 = new DialogsActivity(DialogsActivity.this.arguments);
                            dialogsActivity5.setDelegate(access$25400);
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
                        int size = (dialogFilter2 != null ? dialogFilter2.alwaysShow.size() : 0) + dialogsCount.size();
                        if ((size <= DialogsActivity.this.getMessagesController().dialogFiltersChatsLimitDefault || DialogsActivity.this.getUserConfig().isPremium()) && size <= DialogsActivity.this.getMessagesController().dialogFiltersChatsLimitPremium) {
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
                            return;
                        }
                        DialogsActivity dialogsActivity2 = DialogsActivity.this;
                        DialogsActivity dialogsActivity3 = DialogsActivity.this;
                        dialogsActivity2.showDialog(new LimitReachedBottomSheet(dialogsActivity3, dialogsActivity3.fragmentView.getContext(), 4, DialogsActivity.this.currentAccount));
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
            org.telegram.messenger.MessagesController r1 = r6.getMessagesController()
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r1 = r1.dialogFilters
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r6.viewPages
            r2 = r2[r7]
            int r2 = r2.selectedType
            java.lang.Object r1 = r1.get(r2)
            org.telegram.messenger.MessagesController$DialogFilter r1 = (org.telegram.messenger.MessagesController.DialogFilter) r1
            boolean r2 = r1.isDefault()
            r3 = 1
            if (r2 == 0) goto L_0x004a
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r6.viewPages
            r2 = r2[r7]
            int unused = r2.dialogsType = r0
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r6.viewPages
            r2 = r2[r7]
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r2 = r2.listView
            r2.updatePullState()
            goto L_0x0087
        L_0x004a:
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r6.viewPages
            r4 = r7 ^ 1
            r2 = r2[r4]
            int r2 = r2.dialogsType
            r4 = 8
            r5 = 7
            if (r2 != r5) goto L_0x0061
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r6.viewPages
            r2 = r2[r7]
            int unused = r2.dialogsType = r4
            goto L_0x0068
        L_0x0061:
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r6.viewPages
            r2 = r2[r7]
            int unused = r2.dialogsType = r5
        L_0x0068:
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r6.viewPages
            r2 = r2[r7]
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r2 = r2.listView
            r2.setScrollEnabled(r3)
            org.telegram.messenger.MessagesController r2 = r6.getMessagesController()
            org.telegram.ui.DialogsActivity$ViewPage[] r5 = r6.viewPages
            r5 = r5[r7]
            int r5 = r5.dialogsType
            if (r5 != r4) goto L_0x0083
            r4 = 1
            goto L_0x0084
        L_0x0083:
            r4 = 0
        L_0x0084:
            r2.selectDialogFilter(r1, r4)
        L_0x0087:
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r6.viewPages
            r2 = r2[r3]
            boolean r1 = r1.locked
            boolean unused = r2.isLocked = r1
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
            if (r2 != 0) goto L_0x00bc
            boolean r2 = r6.hasHiddenArchive()
            if (r2 == 0) goto L_0x00bc
            r0 = 1
        L_0x00bc:
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
        int i;
        int i2;
        boolean z4 = z2;
        if (this.filterTabsView != null && !this.inPreviewMode && !this.searchIsShowed) {
            ActionBarPopupWindow actionBarPopupWindow = this.scrimPopupWindow;
            if (actionBarPopupWindow != null) {
                actionBarPopupWindow.dismiss();
                this.scrimPopupWindow = null;
            }
            ArrayList<MessagesController.DialogFilter> arrayList = getMessagesController().dialogFilters;
            MessagesController.getMainSettings(this.currentAccount);
            if (arrayList.size() <= 1) {
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
                    if (this.viewPages[0].selectedType != this.filterTabsView.getDefaultTabId()) {
                        int unused = this.viewPages[0].selectedType = this.filterTabsView.getDefaultTabId();
                        this.viewPages[0].dialogsAdapter.setDialogsType(0);
                        int unused2 = this.viewPages[0].dialogsType = 0;
                        this.viewPages[0].dialogsAdapter.notifyDataSetChanged();
                    }
                    this.viewPages[1].setVisibility(8);
                    int unused3 = this.viewPages[1].selectedType = 0;
                    this.viewPages[1].dialogsAdapter.setDialogsType(0);
                    int unused4 = this.viewPages[1].dialogsType = 0;
                    this.viewPages[1].dialogsAdapter.notifyDataSetChanged();
                    this.canShowFilterTabsView = false;
                    updateFilterTabsVisibility(z4);
                    int i3 = 0;
                    while (true) {
                        ViewPage[] viewPageArr2 = this.viewPages;
                        if (i3 >= viewPageArr2.length) {
                            break;
                        }
                        if (viewPageArr2[i3].dialogsType == 0 && this.viewPages[i3].archivePullViewState == 2 && hasHiddenArchive() && ((findFirstVisibleItemPosition = this.viewPages[i3].layoutManager.findFirstVisibleItemPosition()) == 0 || findFirstVisibleItemPosition == 1)) {
                            this.viewPages[i3].layoutManager.scrollToPositionWithOffset(1, 0);
                        }
                        this.viewPages[i3].listView.setScrollingTouchSlop(0);
                        this.viewPages[i3].listView.requestLayout();
                        this.viewPages[i3].requestLayout();
                        i3++;
                    }
                }
                ActionBarLayout actionBarLayout = this.parentLayout;
                if (actionBarLayout != null) {
                    actionBarLayout.getDrawerLayoutContainer().setAllowOpenDrawerBySwipe(true);
                }
            } else if (z || this.filterTabsView.getVisibility() != 0) {
                boolean z5 = this.filterTabsView.getVisibility() != 0 ? false : z4;
                this.canShowFilterTabsView = true;
                boolean isEmpty = this.filterTabsView.isEmpty();
                updateFilterTabsVisibility(z4);
                int currentTabId = this.filterTabsView.getCurrentTabId();
                int currentTabStableId = this.filterTabsView.getCurrentTabStableId();
                if (currentTabId == this.filterTabsView.getDefaultTabId() || currentTabId < arrayList.size()) {
                    z3 = false;
                } else {
                    this.filterTabsView.resetTabId();
                    z3 = true;
                }
                this.filterTabsView.removeTabs();
                int size = arrayList.size();
                int i4 = 0;
                while (i4 < size) {
                    if (arrayList.get(i4).isDefault()) {
                        i2 = i4;
                        this.filterTabsView.addTab(i4, 0, LocaleController.getString("FilterAllChats", R.string.FilterAllChats), true, arrayList.get(i4).locked);
                    } else {
                        i2 = i4;
                        this.filterTabsView.addTab(i2, arrayList.get(i2).localId, arrayList.get(i2).name, false, arrayList.get(i2).locked);
                    }
                    i4 = i2 + 1;
                }
                if (currentTabStableId >= 0) {
                    if (this.filterTabsView.getStableId(this.viewPages[0].selectedType) != currentTabStableId) {
                        int unused5 = this.viewPages[0].selectedType = currentTabId;
                        isEmpty = true;
                    }
                    if (z3) {
                        this.filterTabsView.selectTabWithStableId(currentTabStableId);
                    }
                }
                int i5 = 0;
                while (true) {
                    ViewPage[] viewPageArr3 = this.viewPages;
                    if (i5 >= viewPageArr3.length) {
                        break;
                    }
                    if (viewPageArr3[i5].selectedType >= arrayList.size()) {
                        i = 1;
                        int unused6 = this.viewPages[i5].selectedType = arrayList.size() - 1;
                    } else {
                        i = 1;
                    }
                    this.viewPages[i5].listView.setScrollingTouchSlop(i);
                    i5++;
                }
                this.filterTabsView.finishAddingTabs(z5);
                if (isEmpty) {
                    switchToCurrentSelectedMode(false);
                }
                ActionBarLayout actionBarLayout2 = this.parentLayout;
                if (actionBarLayout2 != null) {
                    actionBarLayout2.getDrawerLayoutContainer().setAllowOpenDrawerBySwipe(currentTabId == this.filterTabsView.getFirstTabId() || SharedConfig.getChatSwipeAction(this.currentAccount) != 5);
                }
                FilterTabsView filterTabsView2 = this.filterTabsView;
                if (filterTabsView2.isLocked(filterTabsView2.getCurrentTabId())) {
                    this.filterTabsView.selectFirstTab();
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

    public void onResume() {
        int i;
        View view;
        super.onResume();
        if (!this.parentLayout.isInPreviewMode() && (view = this.blurredView) != null && view.getVisibility() == 0) {
            this.blurredView.setVisibility(8);
            this.blurredView.setBackground((Drawable) null);
        }
        FilterTabsView filterTabsView2 = this.filterTabsView;
        if (filterTabsView2 != null && filterTabsView2.getVisibility() == 0) {
            this.parentLayout.getDrawerLayoutContainer().setAllowOpenDrawerBySwipe(this.viewPages[0].selectedType == this.filterTabsView.getFirstTabId() || this.searchIsShowed || SharedConfig.getChatSwipeAction(this.currentAccount) != 5);
        }
        if (this.viewPages != null) {
            int i2 = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (i2 >= viewPageArr.length) {
                    break;
                }
                viewPageArr[i2].dialogsAdapter.notifyDataSetChanged();
                i2++;
            }
        }
        ChatActivityEnterView chatActivityEnterView = this.commentView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.onResume();
        }
        if (!this.onlySelect && this.folderId == 0) {
            getMediaDataController().checkStickers(4);
        }
        SearchViewPager searchViewPager2 = this.searchViewPager;
        if (searchViewPager2 != null) {
            searchViewPager2.onResume();
        }
        if ((this.afterSignup || getUserConfig().unacceptedTermsOfService == null) && this.checkPermission && !this.onlySelect && (i = Build.VERSION.SDK_INT) >= 23) {
            Activity parentActivity = getParentActivity();
            if (parentActivity != null) {
                this.checkPermission = false;
                boolean z = parentActivity.checkSelfPermission("android.permission.READ_CONTACTS") != 0;
                AndroidUtilities.runOnUIThread(new DialogsActivity$$ExternalSyntheticLambda49(this, z, (i <= 28 || BuildVars.NO_SCOPED_STORAGE) && parentActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0, parentActivity), (!this.afterSignup || !z) ? 0 : 4000);
            }
        } else if (!this.onlySelect && XiaomiUtilities.isMIUI() && Build.VERSION.SDK_INT >= 19 && !XiaomiUtilities.isCustomPermissionGranted(10020)) {
            if (getParentActivity() != null && !MessagesController.getGlobalNotificationsSettings().getBoolean("askedAboutMiuiLockscreen", false)) {
                showDialog(new AlertDialog.Builder((Context) getParentActivity()).setTopAnimation(R.raw.permission_request_apk, 72, false, Theme.getColor("dialogTopBackground")).setMessage(LocaleController.getString("PermissionXiaomiLockscreen", R.string.PermissionXiaomiLockscreen)).setPositiveButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogsActivity$$ExternalSyntheticLambda8(this)).setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", R.string.ContactsPermissionAlertNotNow), DialogsActivity$$ExternalSyntheticLambda14.INSTANCE).create());
            } else {
                return;
            }
        }
        showFiltersHint();
        if (this.viewPages != null) {
            int i3 = 0;
            while (true) {
                ViewPage[] viewPageArr2 = this.viewPages;
                if (i3 >= viewPageArr2.length) {
                    break;
                }
                if (viewPageArr2[i3].dialogsType == 0 && this.viewPages[i3].archivePullViewState == 2 && this.viewPages[i3].layoutManager.findFirstVisibleItemPosition() == 0 && hasHiddenArchive()) {
                    this.viewPages[i3].layoutManager.scrollToPositionWithOffset(1, 0);
                }
                if (i3 == 0) {
                    this.viewPages[i3].dialogsAdapter.resume();
                } else {
                    this.viewPages[i3].dialogsAdapter.pause();
                }
                i3++;
            }
        }
        showNextSupportedSuggestion();
        Bulletin.addDelegate((BaseFragment) this, (Bulletin.Delegate) new Bulletin.Delegate() {
            public /* synthetic */ int getBottomOffset(int i) {
                return Bulletin.Delegate.CC.$default$getBottomOffset(this, i);
            }

            public /* synthetic */ void onHide(Bulletin bulletin) {
                Bulletin.Delegate.CC.$default$onHide(this, bulletin);
            }

            public void onOffsetChange(float f) {
                if (DialogsActivity.this.undoView[0] == null || DialogsActivity.this.undoView[0].getVisibility() != 0) {
                    float unused = DialogsActivity.this.additionalFloatingTranslation = f;
                    if (DialogsActivity.this.additionalFloatingTranslation < 0.0f) {
                        float unused2 = DialogsActivity.this.additionalFloatingTranslation = 0.0f;
                    }
                    if (!DialogsActivity.this.floatingHidden) {
                        DialogsActivity.this.updateFloatingButtonOffset();
                    }
                }
            }

            public void onShow(Bulletin bulletin) {
                if (DialogsActivity.this.undoView[0] != null && DialogsActivity.this.undoView[0].getVisibility() == 0) {
                    DialogsActivity.this.undoView[0].hide(true, 2);
                }
            }
        });
        if (this.searchIsShowed) {
            AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        }
        updateVisibleRows(0, false);
        updateProxyButton(false, true);
        checkSuggestClearDatabase();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onResume$20(boolean z, boolean z2, Activity activity) {
        this.afterSignup = false;
        if (z || z2) {
            this.askingForPermissions = true;
            if (z && this.askAboutContacts && getUserConfig().syncContacts && activity.shouldShowRequestPermissionRationale("android.permission.READ_CONTACTS")) {
                AlertDialog create = AlertsCreator.createContactsPermissionDialog(activity, new DialogsActivity$$ExternalSyntheticLambda52(this)).create();
                this.permissionDialog = create;
                showDialog(create);
            } else if (!z2 || !activity.shouldShowRequestPermissionRationale("android.permission.WRITE_EXTERNAL_STORAGE")) {
                askForPermissons(true);
            } else if (activity instanceof BasePermissionsActivity) {
                AlertDialog createPermissionErrorAlert = ((BasePermissionsActivity) activity).createPermissionErrorAlert(R.raw.permission_request_folder, LocaleController.getString(R.string.PermissionStorageWithHint));
                this.permissionDialog = createPermissionErrorAlert;
                showDialog(createPermissionErrorAlert);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onResume$19(int i) {
        this.askAboutContacts = i != 0;
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askAboutContacts", this.askAboutContacts).apply();
        askForPermissons(false);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:?, code lost:
        return;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:4:0x000e */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$onResume$21(android.content.DialogInterface r2, int r3) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.lambda$onResume$21(android.content.DialogInterface, int):void");
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
                if (filterTabsView3 == null || filterTabsView3.getVisibility() != 0 || this.tabsAnimationInProgress || this.filterTabsView.isAnimatingIndicator() || this.startedTracking || this.filterTabsView.isFirstTabSelected()) {
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
        showSearch(true, false, z);
        this.actionBar.openSearchField(str, false);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v3, resolved type: boolean} */
    /* JADX WARNING: type inference failed for: r8v0 */
    /* JADX WARNING: type inference failed for: r8v2 */
    /* JADX WARNING: type inference failed for: r8v4 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0075  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x007f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showSearch(boolean r17, boolean r18, boolean r19) {
        /*
            r16 = this;
            r6 = r16
            r7 = r17
            int r0 = r6.initialDialogsType
            r8 = 0
            if (r0 == 0) goto L_0x000e
            r1 = 3
            if (r0 == r1) goto L_0x000e
            r0 = 0
            goto L_0x0010
        L_0x000e:
            r0 = r19
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
            android.view.View r1 = r6.fragmentView
            org.telegram.ui.Components.SizeNotifierFrameLayout r1 = (org.telegram.ui.Components.SizeNotifierFrameLayout) r1
            r1.invalidateBlur()
            r1 = 1110441984(0x42300000, float:44.0)
            r10 = -1
            r11 = 1
            if (r7 == 0) goto L_0x0110
            boolean r2 = r6.searchFiltersWasShowed
            if (r2 == 0) goto L_0x0038
            r2 = 0
            goto L_0x003c
        L_0x0038:
            boolean r2 = r16.onlyDialogsAdapter()
        L_0x003c:
            org.telegram.ui.Components.SearchViewPager r3 = r6.searchViewPager
            r3.showOnlyDialogsAdapter(r2)
            r3 = r2 ^ 1
            r6.whiteActionBar = r3
            if (r3 == 0) goto L_0x0049
            r6.searchFiltersWasShowed = r11
        L_0x0049:
            android.view.View r3 = r6.fragmentView
            org.telegram.ui.DialogsActivity$ContentView r3 = (org.telegram.ui.DialogsActivity.ContentView) r3
            org.telegram.ui.Components.ViewPagerFixed$TabsView r4 = r6.searchTabsView
            if (r4 != 0) goto L_0x0089
            if (r2 != 0) goto L_0x0089
            org.telegram.ui.Components.SearchViewPager r2 = r6.searchViewPager
            org.telegram.ui.Components.ViewPagerFixed$TabsView r2 = r2.createTabsView()
            r6.searchTabsView = r2
            org.telegram.ui.Adapters.FiltersView r2 = r6.filtersView
            if (r2 == 0) goto L_0x0072
            r2 = 0
        L_0x0060:
            int r4 = r3.getChildCount()
            if (r2 >= r4) goto L_0x0072
            android.view.View r4 = r3.getChildAt(r2)
            org.telegram.ui.Adapters.FiltersView r5 = r6.filtersView
            if (r4 != r5) goto L_0x006f
            goto L_0x0073
        L_0x006f:
            int r2 = r2 + 1
            goto L_0x0060
        L_0x0072:
            r2 = -1
        L_0x0073:
            if (r2 <= 0) goto L_0x007f
            org.telegram.ui.Components.ViewPagerFixed$TabsView r4 = r6.searchTabsView
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r1)
            r3.addView(r4, r2, r5)
            goto L_0x009e
        L_0x007f:
            org.telegram.ui.Components.ViewPagerFixed$TabsView r2 = r6.searchTabsView
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r1)
            r3.addView(r2, r4)
            goto L_0x009e
        L_0x0089:
            if (r4 == 0) goto L_0x009e
            if (r2 == 0) goto L_0x009e
            android.view.ViewParent r2 = r4.getParent()
            boolean r3 = r2 instanceof android.view.ViewGroup
            if (r3 == 0) goto L_0x009c
            android.view.ViewGroup r2 = (android.view.ViewGroup) r2
            org.telegram.ui.Components.ViewPagerFixed$TabsView r3 = r6.searchTabsView
            r2.removeView(r3)
        L_0x009c:
            r6.searchTabsView = r9
        L_0x009e:
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r6.searchItem
            org.telegram.ui.Components.EditTextBoldCursor r2 = r2.getSearchField()
            boolean r3 = r6.whiteActionBar
            if (r3 == 0) goto L_0x00c4
            java.lang.String r3 = "windowBackgroundWhiteBlackText"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setTextColor(r3)
            java.lang.String r3 = "player_time"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setHintTextColor(r3)
            java.lang.String r3 = "chat_messagePanelCursor"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setCursorColor(r3)
            goto L_0x00dd
        L_0x00c4:
            java.lang.String r3 = "actionBarDefaultSearch"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setCursorColor(r4)
            java.lang.String r4 = "actionBarDefaultSearchPlaceholder"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r2.setHintTextColor(r4)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setTextColor(r3)
        L_0x00dd:
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
            if (r2 == 0) goto L_0x013c
            org.telegram.ui.Adapters.FiltersView$MediaFilterData r2 = new org.telegram.ui.Adapters.FiltersView$MediaFilterData
            int r3 = org.telegram.messenger.R.drawable.chats_archive
            int r4 = org.telegram.messenger.R.string.ArchiveSearchFilter
            java.lang.String r5 = "ArchiveSearchFilter"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r5 = 7
            r2.<init>(r3, r4, r9, r5)
            r6.addSearchFilter(r2)
            goto L_0x013c
        L_0x0110:
            org.telegram.ui.Components.FilterTabsView r2 = r6.filterTabsView
            if (r2 == 0) goto L_0x013c
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r6.parentLayout
            if (r2 == 0) goto L_0x013c
            org.telegram.ui.ActionBar.DrawerLayoutContainer r2 = r2.getDrawerLayoutContainer()
            org.telegram.ui.DialogsActivity$ViewPage[] r3 = r6.viewPages
            r3 = r3[r8]
            int r3 = r3.selectedType
            org.telegram.ui.Components.FilterTabsView r4 = r6.filterTabsView
            int r4 = r4.getFirstTabId()
            if (r3 == r4) goto L_0x0138
            int r3 = r6.currentAccount
            int r3 = org.telegram.messenger.SharedConfig.getChatSwipeAction(r3)
            r4 = 5
            if (r3 == r4) goto L_0x0136
            goto L_0x0138
        L_0x0136:
            r3 = 0
            goto L_0x0139
        L_0x0138:
            r3 = 1
        L_0x0139:
            r2.setAllowOpenDrawerBySwipe(r3)
        L_0x013c:
            if (r0 == 0) goto L_0x0152
            org.telegram.ui.Components.SearchViewPager r2 = r6.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r2 = r2.dialogsSearchAdapter
            boolean r2 = r2.hasRecentSearch()
            if (r2 == 0) goto L_0x0152
            android.app.Activity r2 = r16.getParentActivity()
            int r3 = r6.classGuid
            org.telegram.messenger.AndroidUtilities.setAdjustResizeToNothing(r2, r3)
            goto L_0x015b
        L_0x0152:
            android.app.Activity r2 = r16.getParentActivity()
            int r3 = r6.classGuid
            org.telegram.messenger.AndroidUtilities.requestAdjustResize((android.app.Activity) r2, (int) r3)
        L_0x015b:
            if (r7 != 0) goto L_0x0168
            org.telegram.ui.Components.FilterTabsView r2 = r6.filterTabsView
            if (r2 == 0) goto L_0x0168
            boolean r3 = r6.canShowFilterTabsView
            if (r3 == 0) goto L_0x0168
            r2.setVisibility(r8)
        L_0x0168:
            r12 = 1063675494(0x3var_, float:0.9)
            r13 = 0
            r14 = 1065353216(0x3var_, float:1.0)
            if (r0 == 0) goto L_0x0384
            if (r7 == 0) goto L_0x0193
            org.telegram.ui.Components.SearchViewPager r0 = r6.searchViewPager
            r0.setVisibility(r8)
            org.telegram.ui.Components.SearchViewPager r0 = r6.searchViewPager
            r0.reset()
            r1 = 1
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r0 = r16
            r0.updateFiltersView(r1, r2, r3, r4, r5)
            org.telegram.ui.Components.ViewPagerFixed$TabsView r0 = r6.searchTabsView
            if (r0 == 0) goto L_0x01a5
            r0.hide(r8, r8)
            org.telegram.ui.Components.ViewPagerFixed$TabsView r0 = r6.searchTabsView
            r0.setVisibility(r8)
            goto L_0x01a5
        L_0x0193:
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r6.viewPages
            r0 = r0[r8]
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r0.listView
            r0.setVisibility(r8)
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r6.viewPages
            r0 = r0[r8]
            r0.setVisibility(r8)
        L_0x01a5:
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
            if (r7 == 0) goto L_0x01d6
            r5 = 0
            goto L_0x01d8
        L_0x01d6:
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x01d8:
            r4[r8] = r5
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r3, r4)
            r0.add(r2)
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r6.viewPages
            r2 = r2[r8]
            android.util.Property r3 = android.view.View.SCALE_X
            float[] r4 = new float[r11]
            if (r7 == 0) goto L_0x01ef
            r5 = 1063675494(0x3var_, float:0.9)
            goto L_0x01f1
        L_0x01ef:
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x01f1:
            r4[r8] = r5
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r3, r4)
            r0.add(r2)
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r6.viewPages
            r2 = r2[r8]
            android.util.Property r3 = android.view.View.SCALE_Y
            float[] r4 = new float[r11]
            if (r7 == 0) goto L_0x0205
            goto L_0x0207
        L_0x0205:
            r12 = 1065353216(0x3var_, float:1.0)
        L_0x0207:
            r4[r8] = r12
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r3, r4)
            r0.add(r2)
            org.telegram.ui.Components.SearchViewPager r2 = r6.searchViewPager
            android.util.Property r3 = android.view.View.ALPHA
            float[] r4 = new float[r11]
            if (r7 == 0) goto L_0x021b
            r5 = 1065353216(0x3var_, float:1.0)
            goto L_0x021c
        L_0x021b:
            r5 = 0
        L_0x021c:
            r4[r8] = r5
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r3, r4)
            r0.add(r2)
            org.telegram.ui.Components.SearchViewPager r2 = r6.searchViewPager
            android.util.Property r3 = android.view.View.SCALE_X
            float[] r4 = new float[r11]
            r5 = 1065772646(0x3var_, float:1.05)
            if (r7 == 0) goto L_0x0233
            r12 = 1065353216(0x3var_, float:1.0)
            goto L_0x0236
        L_0x0233:
            r12 = 1065772646(0x3var_, float:1.05)
        L_0x0236:
            r4[r8] = r12
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r3, r4)
            r0.add(r2)
            org.telegram.ui.Components.SearchViewPager r2 = r6.searchViewPager
            android.util.Property r3 = android.view.View.SCALE_Y
            float[] r4 = new float[r11]
            if (r7 == 0) goto L_0x0249
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x0249:
            r4[r8] = r5
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r3, r4)
            r0.add(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r6.passcodeItem
            if (r2 == 0) goto L_0x026d
            org.telegram.ui.Components.RLottieImageView r2 = r2.getIconView()
            android.util.Property r3 = android.view.View.ALPHA
            float[] r4 = new float[r11]
            if (r7 == 0) goto L_0x0262
            r5 = 0
            goto L_0x0264
        L_0x0262:
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x0264:
            r4[r8] = r5
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r3, r4)
            r0.add(r2)
        L_0x026d:
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r6.downloadsItem
            if (r2 == 0) goto L_0x0287
            if (r7 == 0) goto L_0x0277
            r2.setAlpha(r13)
            goto L_0x0284
        L_0x0277:
            android.util.Property r3 = android.view.View.ALPHA
            float[] r4 = new float[r11]
            r4[r8] = r14
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r3, r4)
            r0.add(r2)
        L_0x0284:
            r6.updateProxyButton(r8, r8)
        L_0x0287:
            org.telegram.ui.Components.FilterTabsView r2 = r6.filterTabsView
            r3 = 100
            if (r2 == 0) goto L_0x02b7
            int r2 = r2.getVisibility()
            if (r2 != 0) goto L_0x02b7
            org.telegram.ui.Components.FilterTabsView r2 = r6.filterTabsView
            org.telegram.ui.Components.RecyclerListView r2 = r2.getTabsContainer()
            android.util.Property r5 = android.view.View.ALPHA
            float[] r12 = new float[r11]
            if (r7 == 0) goto L_0x02a1
            r15 = 0
            goto L_0x02a3
        L_0x02a1:
            r15 = 1065353216(0x3var_, float:1.0)
        L_0x02a3:
            r12[r8] = r15
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r5, r12)
            android.animation.ObjectAnimator r2 = r2.setDuration(r3)
            r6.tabsAlphaAnimator = r2
            org.telegram.ui.DialogsActivity$35 r5 = new org.telegram.ui.DialogsActivity$35
            r5.<init>()
            r2.addListener(r5)
        L_0x02b7:
            r2 = 2
            float[] r2 = new float[r2]
            float r5 = r6.searchAnimationProgress
            r2[r8] = r5
            if (r7 == 0) goto L_0x02c2
            r13 = 1065353216(0x3var_, float:1.0)
        L_0x02c2:
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
            if (r7 == 0) goto L_0x02e1
            r12 = 200(0xc8, double:9.9E-322)
            goto L_0x02e2
        L_0x02e1:
            r12 = r14
        L_0x02e2:
            r0.setDuration(r12)
            android.animation.AnimatorSet r0 = r6.searchAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
            r0.setInterpolator(r2)
            boolean r0 = r6.filterTabsViewIsVisible
            if (r0 == 0) goto L_0x0339
            int r0 = r6.folderId
            if (r0 != 0) goto L_0x02f7
            java.lang.String r0 = "actionBarDefault"
            goto L_0x02f9
        L_0x02f7:
            java.lang.String r0 = "actionBarDefaultArchived"
        L_0x02f9:
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
            if (r0 <= 0) goto L_0x0336
            r8 = 1
        L_0x0336:
            r6.searchAnimationTabsDelayedCrossfade = r8
            goto L_0x033b
        L_0x0339:
            r6.searchAnimationTabsDelayedCrossfade = r11
        L_0x033b:
            if (r7 != 0) goto L_0x0360
            android.animation.AnimatorSet r0 = r6.searchAnimator
            r1 = 20
            r0.setStartDelay(r1)
            android.animation.Animator r0 = r6.tabsAlphaAnimator
            if (r0 == 0) goto L_0x0360
            boolean r1 = r6.searchAnimationTabsDelayedCrossfade
            if (r1 == 0) goto L_0x0357
            r1 = 80
            r0.setStartDelay(r1)
            android.animation.Animator r0 = r6.tabsAlphaAnimator
            r0.setDuration(r3)
            goto L_0x0360
        L_0x0357:
            if (r7 == 0) goto L_0x035c
            r12 = 200(0xc8, double:9.9E-322)
            goto L_0x035d
        L_0x035c:
            r12 = r14
        L_0x035d:
            r0.setDuration(r12)
        L_0x0360:
            android.animation.AnimatorSet r0 = r6.searchAnimator
            org.telegram.ui.DialogsActivity$36 r1 = new org.telegram.ui.DialogsActivity$36
            r1.<init>(r7)
            r0.addListener(r1)
            org.telegram.messenger.NotificationCenter r0 = r16.getNotificationCenter()
            int r1 = r6.animationIndex
            int r0 = r0.setAnimationInProgress(r1, r9)
            r6.animationIndex = r0
            android.animation.AnimatorSet r0 = r6.searchAnimator
            r0.start()
            android.animation.Animator r0 = r6.tabsAlphaAnimator
            if (r0 == 0) goto L_0x0456
            r0.start()
            goto L_0x0456
        L_0x0384:
            r6.setDialogsListFrozen(r8)
            if (r7 == 0) goto L_0x0395
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r6.viewPages
            r0 = r0[r8]
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r0.listView
            r0.hide()
            goto L_0x03a0
        L_0x0395:
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r6.viewPages
            r0 = r0[r8]
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r0.listView
            r0.show()
        L_0x03a0:
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r6.viewPages
            r0 = r0[r8]
            if (r7 == 0) goto L_0x03a8
            r2 = 0
            goto L_0x03aa
        L_0x03a8:
            r2 = 1065353216(0x3var_, float:1.0)
        L_0x03aa:
            r0.setAlpha(r2)
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r6.viewPages
            r0 = r0[r8]
            if (r7 == 0) goto L_0x03b7
            r2 = 1063675494(0x3var_, float:0.9)
            goto L_0x03b9
        L_0x03b7:
            r2 = 1065353216(0x3var_, float:1.0)
        L_0x03b9:
            r0.setScaleX(r2)
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r6.viewPages
            r0 = r0[r8]
            if (r7 == 0) goto L_0x03c3
            goto L_0x03c5
        L_0x03c3:
            r12 = 1065353216(0x3var_, float:1.0)
        L_0x03c5:
            r0.setScaleY(r12)
            org.telegram.ui.Components.SearchViewPager r0 = r6.searchViewPager
            if (r7 == 0) goto L_0x03cf
            r2 = 1065353216(0x3var_, float:1.0)
            goto L_0x03d0
        L_0x03cf:
            r2 = 0
        L_0x03d0:
            r0.setAlpha(r2)
            org.telegram.ui.Adapters.FiltersView r0 = r6.filtersView
            if (r7 == 0) goto L_0x03da
            r2 = 1065353216(0x3var_, float:1.0)
            goto L_0x03db
        L_0x03da:
            r2 = 0
        L_0x03db:
            r0.setAlpha(r2)
            org.telegram.ui.Components.SearchViewPager r0 = r6.searchViewPager
            r2 = 1066192077(0x3f8ccccd, float:1.1)
            if (r7 == 0) goto L_0x03e8
            r3 = 1065353216(0x3var_, float:1.0)
            goto L_0x03eb
        L_0x03e8:
            r3 = 1066192077(0x3f8ccccd, float:1.1)
        L_0x03eb:
            r0.setScaleX(r3)
            org.telegram.ui.Components.SearchViewPager r0 = r6.searchViewPager
            if (r7 == 0) goto L_0x03f4
            r2 = 1065353216(0x3var_, float:1.0)
        L_0x03f4:
            r0.setScaleY(r2)
            org.telegram.ui.Components.FilterTabsView r0 = r6.filterTabsView
            if (r0 == 0) goto L_0x041f
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x041f
            org.telegram.ui.Components.FilterTabsView r0 = r6.filterTabsView
            if (r7 == 0) goto L_0x040c
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = -r1
            float r1 = (float) r1
            goto L_0x040d
        L_0x040c:
            r1 = 0
        L_0x040d:
            r0.setTranslationY(r1)
            org.telegram.ui.Components.FilterTabsView r0 = r6.filterTabsView
            org.telegram.ui.Components.RecyclerListView r0 = r0.getTabsContainer()
            if (r7 == 0) goto L_0x041a
            r1 = 0
            goto L_0x041c
        L_0x041a:
            r1 = 1065353216(0x3var_, float:1.0)
        L_0x041c:
            r0.setAlpha(r1)
        L_0x041f:
            org.telegram.ui.Components.FilterTabsView r0 = r6.filterTabsView
            r1 = 8
            if (r0 == 0) goto L_0x0432
            boolean r2 = r6.canShowFilterTabsView
            if (r2 == 0) goto L_0x042f
            if (r7 != 0) goto L_0x042f
            r0.setVisibility(r8)
            goto L_0x0432
        L_0x042f:
            r0.setVisibility(r1)
        L_0x0432:
            org.telegram.ui.Components.SearchViewPager r0 = r6.searchViewPager
            if (r7 == 0) goto L_0x0437
            goto L_0x0439
        L_0x0437:
            r8 = 8
        L_0x0439:
            r0.setVisibility(r8)
            if (r7 == 0) goto L_0x0441
            r0 = 1065353216(0x3var_, float:1.0)
            goto L_0x0442
        L_0x0441:
            r0 = 0
        L_0x0442:
            r6.setSearchAnimationProgress(r0)
            android.view.View r0 = r6.fragmentView
            r0.invalidate()
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.downloadsItem
            if (r0 == 0) goto L_0x0456
            if (r7 == 0) goto L_0x0451
            goto L_0x0453
        L_0x0451:
            r13 = 1065353216(0x3var_, float:1.0)
        L_0x0453:
            r0.setAlpha(r13)
        L_0x0456:
            int r0 = r6.initialSearchType
            if (r0 < 0) goto L_0x0463
            org.telegram.ui.Components.SearchViewPager r1 = r6.searchViewPager
            int r0 = r1.getPositionForType(r0)
            r1.setPosition(r0)
        L_0x0463:
            if (r7 != 0) goto L_0x0467
            r6.initialSearchType = r10
        L_0x0467:
            if (r7 == 0) goto L_0x0470
            if (r18 == 0) goto L_0x0470
            org.telegram.ui.Components.SearchViewPager r0 = r6.searchViewPager
            r0.showDownloads()
        L_0x0470:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.showSearch(boolean, boolean, boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showSearch$23(ValueAnimator valueAnimator) {
        setSearchAnimationProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    public boolean onlyDialogsAdapter() {
        return this.onlySelect || !this.searchViewPager.dialogsSearchAdapter.hasRecentSearch() || getMessagesController().getTotalDialogsCount() <= 10;
    }

    private void updateFilterTabsVisibility(boolean z) {
        int i = 0;
        if (this.isPaused || this.databaseMigrationHint != null) {
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
                this.filtersTabAnimator.addUpdateListener(new DialogsActivity$$ExternalSyntheticLambda6(this, z3, translationY));
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
    public /* synthetic */ void lambda$updateFilterTabsVisibility$24(boolean z, float f, ValueAnimator valueAnimator) {
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
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda50 r15 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda50
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
    public /* synthetic */ void lambda$checkListLoad$25(boolean z, boolean z2, boolean z3, boolean z4) {
        if (z) {
            getMessagesController().loadDialogs(this.folderId, -1, 100, z2);
        }
        if (z3) {
            getMessagesController().loadDialogs(1, -1, 100, z4);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:103:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x01c7  */
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
            if (r3 == 0) goto L_0x0115
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
            goto L_0x00e1
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
            if (r1 == 0) goto L_0x00e1
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
            if (r10 == 0) goto L_0x00e9
            org.telegram.tgnet.TLRPC$TL_recentMeUrlChatInvite r1 = (org.telegram.tgnet.TLRPC$TL_recentMeUrlChatInvite) r1
            org.telegram.tgnet.TLRPC$ChatInvite r10 = r1.chat_invite
            org.telegram.tgnet.TLRPC$Chat r12 = r10.chat
            if (r12 != 0) goto L_0x00aa
            boolean r13 = r10.channel
            if (r13 == 0) goto L_0x00b8
            boolean r13 = r10.megagroup
            if (r13 != 0) goto L_0x00b8
        L_0x00aa:
            if (r12 == 0) goto L_0x00da
            boolean r12 = org.telegram.messenger.ChatObject.isChannel(r12)
            if (r12 == 0) goto L_0x00b8
            org.telegram.tgnet.TLRPC$Chat r12 = r10.chat
            boolean r12 = r12.megagroup
            if (r12 == 0) goto L_0x00da
        L_0x00b8:
            java.lang.String r0 = r1.url
            r1 = 47
            int r1 = r0.indexOf(r1)
            if (r1 <= 0) goto L_0x00c7
            int r1 = r1 + r5
            java.lang.String r0 = r0.substring(r1)
        L_0x00c7:
            r3 = r0
            org.telegram.ui.Components.JoinGroupAlert r7 = new org.telegram.ui.Components.JoinGroupAlert
            android.app.Activity r1 = r16.getParentActivity()
            r5 = 0
            r0 = r7
            r2 = r10
            r4 = r16
            r0.<init>(r1, r2, r3, r4, r5)
            r6.showDialog(r7)
            return
        L_0x00da:
            org.telegram.tgnet.TLRPC$Chat r1 = r10.chat
            if (r1 == 0) goto L_0x00e8
            long r12 = r1.id
            goto L_0x008b
        L_0x00e1:
            r13 = r12
            r1 = 0
            r12 = r11
            r11 = r10
            r10 = 0
            goto L_0x01ce
        L_0x00e8:
            return
        L_0x00e9:
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_recentMeUrlStickerSet
            if (r0 == 0) goto L_0x0112
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
        L_0x0112:
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_recentMeUrlUnknown
            return
        L_0x0115:
            org.telegram.ui.Components.SearchViewPager r10 = r6.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r10 = r10.dialogsSearchAdapter
            if (r2 != r10) goto L_0x01c9
            java.lang.Object r10 = r10.getItem(r1)
            org.telegram.ui.Components.SearchViewPager r11 = r6.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r11 = r11.dialogsSearchAdapter
            boolean r1 = r11.isGlobalSearch(r1)
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC$User
            if (r11 == 0) goto L_0x013b
            org.telegram.tgnet.TLRPC$User r10 = (org.telegram.tgnet.TLRPC$User) r10
            long r11 = r10.id
            boolean r13 = r6.onlySelect
            if (r13 != 0) goto L_0x0137
            r6.searchDialogId = r11
            r6.searchObject = r10
        L_0x0137:
            r12 = r11
        L_0x0138:
            r10 = 0
            goto L_0x01a9
        L_0x013b:
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r11 == 0) goto L_0x014d
            org.telegram.tgnet.TLRPC$Chat r10 = (org.telegram.tgnet.TLRPC$Chat) r10
            long r11 = r10.id
            long r11 = -r11
            boolean r13 = r6.onlySelect
            if (r13 != 0) goto L_0x0137
            r6.searchDialogId = r11
            r6.searchObject = r10
            goto L_0x0137
        L_0x014d:
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC$EncryptedChat
            if (r11 == 0) goto L_0x0163
            org.telegram.tgnet.TLRPC$EncryptedChat r10 = (org.telegram.tgnet.TLRPC$EncryptedChat) r10
            int r11 = r10.id
            long r11 = (long) r11
            long r11 = org.telegram.messenger.DialogObject.makeEncryptedDialogId(r11)
            boolean r13 = r6.onlySelect
            if (r13 != 0) goto L_0x0137
            r6.searchDialogId = r11
            r6.searchObject = r10
            goto L_0x0137
        L_0x0163:
            boolean r11 = r10 instanceof org.telegram.messenger.MessageObject
            if (r11 == 0) goto L_0x017e
            org.telegram.messenger.MessageObject r10 = (org.telegram.messenger.MessageObject) r10
            long r11 = r10.getDialogId()
            int r10 = r10.getId()
            org.telegram.ui.Components.SearchViewPager r13 = r6.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r13 = r13.dialogsSearchAdapter
            java.lang.String r14 = r13.getLastSearchString()
            r13.addHashtagsFromMessage(r14)
            r12 = r11
            goto L_0x01a9
        L_0x017e:
            boolean r11 = r10 instanceof java.lang.String
            if (r11 == 0) goto L_0x01a7
            java.lang.String r10 = (java.lang.String) r10
            org.telegram.ui.Components.SearchViewPager r11 = r6.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r11 = r11.dialogsSearchAdapter
            boolean r11 = r11.isHashtagSearch()
            if (r11 == 0) goto L_0x0194
            org.telegram.ui.ActionBar.ActionBar r11 = r6.actionBar
            r11.openSearchField(r10, r9)
            goto L_0x01a7
        L_0x0194:
            java.lang.String r11 = "section"
            boolean r11 = r10.equals(r11)
            if (r11 != 0) goto L_0x01a7
            org.telegram.ui.NewContactActivity r11 = new org.telegram.ui.NewContactActivity
            r11.<init>()
            r11.setInitialPhoneNumber(r10, r5)
            r6.presentFragment(r11)
        L_0x01a7:
            r12 = r7
            goto L_0x0138
        L_0x01a9:
            int r11 = (r12 > r7 ? 1 : (r12 == r7 ? 0 : -1))
            if (r11 == 0) goto L_0x01c7
            org.telegram.ui.ActionBar.ActionBar r11 = r6.actionBar
            boolean r11 = r11.isActionModeShowed()
            if (r11 == 0) goto L_0x01c7
            org.telegram.ui.ActionBar.ActionBar r2 = r6.actionBar
            java.lang.String r3 = "search_dialogs_action_mode"
            boolean r2 = r2.isActionModeShowed(r3)
            if (r2 == 0) goto L_0x01c6
            if (r10 != 0) goto L_0x01c6
            if (r1 != 0) goto L_0x01c6
            r6.showOrUpdateActionMode(r12, r0)
        L_0x01c6:
            return
        L_0x01c7:
            r13 = r12
            goto L_0x01cc
        L_0x01c9:
            r13 = r7
            r1 = 0
            r10 = 0
        L_0x01cc:
            r11 = 0
            r12 = 0
        L_0x01ce:
            int r15 = (r13 > r7 ? 1 : (r13 == r7 ? 0 : -1))
            if (r15 != 0) goto L_0x01d3
            return
        L_0x01d3:
            boolean r7 = r6.onlySelect
            if (r7 == 0) goto L_0x021f
            boolean r1 = r6.validateSlowModeDialog(r13)
            if (r1 != 0) goto L_0x01de
            return
        L_0x01de:
            java.util.ArrayList<java.lang.Long> r1 = r6.selectedDialogs
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x01f5
            int r1 = r6.initialDialogsType
            r3 = 3
            if (r1 != r3) goto L_0x01f0
            java.lang.String r1 = r6.selectAlertString
            if (r1 == 0) goto L_0x01f0
            goto L_0x01f5
        L_0x01f0:
            r6.didSelectResult(r13, r5, r9)
            goto L_0x0326
        L_0x01f5:
            java.util.ArrayList<java.lang.Long> r1 = r6.selectedDialogs
            java.lang.Long r3 = java.lang.Long.valueOf(r13)
            boolean r1 = r1.contains(r3)
            if (r1 != 0) goto L_0x0208
            boolean r1 = r6.checkCanWrite(r13)
            if (r1 != 0) goto L_0x0208
            return
        L_0x0208:
            boolean r0 = r6.addOrRemoveSelectedDialog(r13, r0)
            org.telegram.ui.Components.SearchViewPager r1 = r6.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r1 = r1.dialogsSearchAdapter
            if (r2 != r1) goto L_0x021a
            org.telegram.ui.ActionBar.ActionBar r1 = r6.actionBar
            r1.closeSearchField()
            r6.findAndUpdateCheckBox(r13, r0)
        L_0x021a:
            r16.updateSelectedCount()
            goto L_0x0326
        L_0x021f:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            boolean r7 = org.telegram.messenger.DialogObject.isEncryptedDialog(r13)
            if (r7 == 0) goto L_0x0234
            int r7 = org.telegram.messenger.DialogObject.getEncryptedChatId(r13)
            java.lang.String r8 = "enc_id"
            r0.putInt(r8, r7)
            goto L_0x0267
        L_0x0234:
            boolean r7 = org.telegram.messenger.DialogObject.isUserDialog(r13)
            if (r7 == 0) goto L_0x0240
            java.lang.String r7 = "user_id"
            r0.putLong(r7, r13)
            goto L_0x0267
        L_0x0240:
            if (r10 == 0) goto L_0x0260
            org.telegram.messenger.MessagesController r7 = r16.getMessagesController()
            long r4 = -r13
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r4 = r7.getChat(r4)
            if (r4 == 0) goto L_0x0260
            org.telegram.tgnet.TLRPC$InputChannel r5 = r4.migrated_to
            if (r5 == 0) goto L_0x0260
            java.lang.String r5 = "migrated_to"
            r0.putLong(r5, r13)
            org.telegram.tgnet.TLRPC$InputChannel r4 = r4.migrated_to
            long r4 = r4.channel_id
            long r4 = -r4
            goto L_0x0261
        L_0x0260:
            r4 = r13
        L_0x0261:
            long r4 = -r4
            java.lang.String r7 = "chat_id"
            r0.putLong(r7, r4)
        L_0x0267:
            if (r10 == 0) goto L_0x026f
            java.lang.String r1 = "message_id"
            r0.putInt(r1, r10)
            goto L_0x0285
        L_0x026f:
            if (r1 != 0) goto L_0x0275
            r16.closeSearch()
            goto L_0x0285
        L_0x0275:
            org.telegram.tgnet.TLObject r1 = r6.searchObject
            if (r1 == 0) goto L_0x0285
            org.telegram.ui.Components.SearchViewPager r4 = r6.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r4 = r4.dialogsSearchAdapter
            long r8 = r6.searchDialogId
            r4.putRecentSearch(r8, r1)
            r1 = 0
            r6.searchObject = r1
        L_0x0285:
            java.lang.String r1 = "dialog_folder_id"
            r0.putInt(r1, r11)
            java.lang.String r1 = "dialog_filter_id"
            r0.putInt(r1, r12)
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 == 0) goto L_0x02bf
            long r7 = r6.openedDialogId
            int r1 = (r7 > r13 ? 1 : (r7 == r13 ? 0 : -1))
            if (r1 != 0) goto L_0x02a2
            org.telegram.ui.Components.SearchViewPager r1 = r6.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r1 = r1.dialogsSearchAdapter
            if (r2 == r1) goto L_0x02a2
            return
        L_0x02a2:
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r6.viewPages
            if (r1 == 0) goto L_0x02ba
            r1 = 0
        L_0x02a7:
            org.telegram.ui.DialogsActivity$ViewPage[] r2 = r6.viewPages
            int r4 = r2.length
            if (r1 >= r4) goto L_0x02ba
            r2 = r2[r1]
            org.telegram.ui.Adapters.DialogsAdapter r2 = r2.dialogsAdapter
            r6.openedDialogId = r13
            r2.setOpenedDialogId(r13)
            int r1 = r1 + 1
            goto L_0x02a7
        L_0x02ba:
            int r1 = org.telegram.messenger.MessagesController.UPDATE_MASK_SELECT_DIALOG
            r6.updateVisibleRows(r1)
        L_0x02bf:
            org.telegram.ui.Components.SearchViewPager r1 = r6.searchViewPager
            boolean r1 = r1.actionModeShowing()
            if (r1 == 0) goto L_0x02cc
            org.telegram.ui.Components.SearchViewPager r1 = r6.searchViewPager
            r1.hideActionMode()
        L_0x02cc:
            java.lang.String r1 = r6.searchString
            if (r1 == 0) goto L_0x02ef
            org.telegram.messenger.MessagesController r1 = r16.getMessagesController()
            boolean r1 = r1.checkCanOpenChat(r0, r6)
            if (r1 == 0) goto L_0x0326
            org.telegram.messenger.NotificationCenter r1 = r16.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.closeChats
            r3 = 0
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r1.postNotificationName(r2, r3)
            org.telegram.ui.ChatActivity r1 = new org.telegram.ui.ChatActivity
            r1.<init>(r0)
            r6.presentFragment(r1)
            goto L_0x0326
        L_0x02ef:
            r1 = 1
            r6.slowedReloadAfterDialogClick = r1
            org.telegram.messenger.MessagesController r1 = r16.getMessagesController()
            boolean r1 = r1.checkCanOpenChat(r0, r6)
            if (r1 == 0) goto L_0x0326
            org.telegram.ui.ChatActivity r1 = new org.telegram.ui.ChatActivity
            r1.<init>(r0)
            if (r3 == 0) goto L_0x0323
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r13)
            if (r0 == 0) goto L_0x0323
            org.telegram.messenger.MessagesController r0 = r16.getMessagesController()
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r0 = r0.dialogs_dict
            java.lang.Object r0 = r0.get(r13)
            if (r0 != 0) goto L_0x0323
            org.telegram.messenger.MediaDataController r0 = r16.getMediaDataController()
            org.telegram.tgnet.TLRPC$Document r0 = r0.getGreetingsSticker()
            if (r0 == 0) goto L_0x0323
            r2 = 1
            r1.setPreloadedSticker(r0, r2)
        L_0x0323:
            r6.presentFragment(r1)
        L_0x0326:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.onItemClick(android.view.View, int, androidx.recyclerview.widget.RecyclerView$Adapter):void");
    }

    /* access modifiers changed from: private */
    public boolean onItemLongClick(RecyclerListView recyclerListView, View view, int i, float f, float f2, int i2, RecyclerView.Adapter adapter) {
        TLRPC$Dialog tLRPC$Dialog;
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
        if (adapter == dialogsSearchAdapter) {
            Object item = dialogsSearchAdapter.getItem(i);
            if (!this.searchViewPager.dialogsSearchAdapter.isSearchWas()) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("ClearSearchSingleAlertTitle", R.string.ClearSearchSingleAlertTitle));
                if (item instanceof TLRPC$Chat) {
                    TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) item;
                    builder.setMessage(LocaleController.formatString("ClearSearchSingleChatAlertText", R.string.ClearSearchSingleChatAlertText, tLRPC$Chat.title));
                    j = -tLRPC$Chat.id;
                } else if (item instanceof TLRPC$User) {
                    TLRPC$User tLRPC$User = (TLRPC$User) item;
                    if (tLRPC$User.id == getUserConfig().clientUserId) {
                        builder.setMessage(LocaleController.formatString("ClearSearchSingleChatAlertText", R.string.ClearSearchSingleChatAlertText, LocaleController.getString("SavedMessages", R.string.SavedMessages)));
                    } else {
                        builder.setMessage(LocaleController.formatString("ClearSearchSingleUserAlertText", R.string.ClearSearchSingleUserAlertText, ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name)));
                    }
                    j = tLRPC$User.id;
                } else if (!(item instanceof TLRPC$EncryptedChat)) {
                    return false;
                } else {
                    TLRPC$EncryptedChat tLRPC$EncryptedChat = (TLRPC$EncryptedChat) item;
                    TLRPC$User user = getMessagesController().getUser(Long.valueOf(tLRPC$EncryptedChat.user_id));
                    builder.setMessage(LocaleController.formatString("ClearSearchSingleUserAlertText", R.string.ClearSearchSingleUserAlertText, ContactsController.formatName(user.first_name, user.last_name)));
                    j = DialogObject.makeEncryptedDialogId((long) tLRPC$EncryptedChat.id);
                }
                builder.setPositiveButton(LocaleController.getString("ClearSearchRemove", R.string.ClearSearchRemove).toUpperCase(), new DialogsActivity$$ExternalSyntheticLambda11(this, j));
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
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
        if (adapter != dialogsSearchAdapter2) {
            ArrayList<TLRPC$Dialog> dialogsArray = getDialogsArray(this.currentAccount, i2, this.folderId, this.dialogsListFrozen);
            int fixPosition = ((DialogsAdapter) adapter).fixPosition(i);
            if (fixPosition < 0 || fixPosition >= dialogsArray.size() || (tLRPC$Dialog = dialogsArray.get(fixPosition)) == null) {
                return false;
            }
            if (this.onlySelect) {
                int i3 = this.initialDialogsType;
                if ((i3 != 3 && i3 != 10) || !validateSlowModeDialog(tLRPC$Dialog.id)) {
                    return false;
                }
                addOrRemoveSelectedDialog(tLRPC$Dialog.id, view);
                updateSelectedCount();
                return true;
            } else if (tLRPC$Dialog instanceof TLRPC$TL_dialogFolder) {
                onArchiveLongPress(view);
                return false;
            } else if (this.actionBar.isActionModeShowed() && isDialogPinned(tLRPC$Dialog)) {
                return false;
            } else {
                showOrUpdateActionMode(tLRPC$Dialog.id, view);
                return true;
            }
        } else if (this.onlySelect) {
            onItemClick(view, i, adapter);
            return false;
        } else {
            long dialogId = (!(view instanceof ProfileSearchCell) || dialogsSearchAdapter2.isGlobalSearch(i)) ? 0 : ((ProfileSearchCell) view).getDialogId();
            if (dialogId == 0) {
                return false;
            }
            showOrUpdateActionMode(dialogId, view);
            return true;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onItemLongClick$26(long j, DialogInterface dialogInterface, int i) {
        this.searchViewPager.dialogsSearchAdapter.removeRecentSearch(j);
    }

    private void onArchiveLongPress(View view) {
        String str;
        int i;
        view.performHapticFeedback(0, 2);
        BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
        boolean z = getMessagesStorage().getArchiveUnreadCount() != 0;
        int[] iArr = new int[2];
        iArr[0] = z ? R.drawable.msg_markread : 0;
        iArr[1] = SharedConfig.archiveHidden ? R.drawable.chats_pin : R.drawable.chats_unpin;
        CharSequence[] charSequenceArr = new CharSequence[2];
        charSequenceArr[0] = z ? LocaleController.getString("MarkAllAsRead", R.string.MarkAllAsRead) : null;
        if (SharedConfig.archiveHidden) {
            i = R.string.PinInTheList;
            str = "PinInTheList";
        } else {
            i = R.string.HideAboveTheList;
            str = "HideAboveTheList";
        }
        charSequenceArr[1] = LocaleController.getString(str, i);
        builder.setItems(charSequenceArr, iArr, new DialogsActivity$$ExternalSyntheticLambda10(this));
        showDialog(builder.create());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onArchiveLongPress$27(DialogInterface dialogInterface, int i) {
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

    /* JADX WARNING: type inference failed for: r1v14, types: [boolean] */
    /* JADX WARNING: type inference failed for: r1v48 */
    /* JADX WARNING: type inference failed for: r1v84 */
    public boolean showChatPreview(DialogCell dialogCell) {
        MessagesController.DialogFilter dialogFilter;
        ChatActivity[] chatActivityArr;
        ? r1;
        Bundle bundle;
        boolean z;
        int i;
        int i2;
        int i3;
        int i4;
        boolean z2;
        char c;
        int i5;
        ArrayList<TLRPC$Dialog> arrayList;
        long j;
        TLRPC$Chat chat;
        if (dialogCell.isDialogFolder()) {
            if (dialogCell.getCurrentDialogFolderId() == 1) {
                onArchiveLongPress(dialogCell);
            }
            return false;
        }
        long dialogId = dialogCell.getDialogId();
        Bundle bundle2 = new Bundle();
        int messageId = dialogCell.getMessageId();
        if (DialogObject.isEncryptedDialog(dialogId)) {
            return false;
        }
        if (DialogObject.isUserDialog(dialogId)) {
            bundle2.putLong("user_id", dialogId);
        } else {
            if (messageId == 0 || (chat = getMessagesController().getChat(Long.valueOf(-dialogId))) == null || chat.migrated_to == null) {
                j = dialogId;
            } else {
                bundle2.putLong("migrated_to", dialogId);
                j = -chat.migrated_to.channel_id;
            }
            bundle2.putLong("chat_id", -j);
        }
        if (messageId != 0) {
            bundle2.putInt("message_id", messageId);
        }
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(Long.valueOf(dialogId));
        ChatActivity[] chatActivityArr2 = new ChatActivity[1];
        ActionBarPopupWindow.ActionBarPopupWindowLayout[] actionBarPopupWindowLayoutArr = {new ActionBarPopupWindow.ActionBarPopupWindowLayout(getParentActivity(), R.drawable.popup_fixed_alert, getResourceProvider(), 2)};
        ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(getParentActivity(), true, false);
        if (dialogCell.getHasUnread()) {
            actionBarMenuSubItem.setTextAndIcon(LocaleController.getString("MarkAsRead", R.string.MarkAsRead), R.drawable.msg_markread);
        } else {
            actionBarMenuSubItem.setTextAndIcon(LocaleController.getString("MarkAsUnread", R.string.MarkAsUnread), R.drawable.msg_markunread);
        }
        actionBarMenuSubItem.setMinimumWidth(160);
        actionBarMenuSubItem.setOnClickListener(new DialogsActivity$$ExternalSyntheticLambda29(this, dialogCell, dialogId));
        actionBarPopupWindowLayoutArr[0].addView(actionBarMenuSubItem);
        boolean[] zArr = {true};
        TLRPC$Dialog tLRPC$Dialog = getMessagesController().dialogs_dict.get(dialogId);
        boolean z3 = (this.viewPages[0].dialogsType == 7 || this.viewPages[0].dialogsType == 8) && (!this.actionBar.isActionModeShowed() || this.actionBar.isActionModeShowed((String) null));
        if (z3) {
            dialogFilter = getMessagesController().selectedDialogFilter[this.viewPages[0].dialogsType == 8 ? (char) 1 : 0];
        } else {
            dialogFilter = null;
        }
        if (!isDialogPinned(tLRPC$Dialog)) {
            ArrayList<TLRPC$Dialog> dialogs = getMessagesController().getDialogs(this.folderId);
            int size = dialogs.size();
            int i6 = 0;
            int i7 = 0;
            int i8 = 0;
            while (true) {
                if (i6 >= size) {
                    chatActivityArr = chatActivityArr2;
                    break;
                }
                TLRPC$Dialog tLRPC$Dialog2 = dialogs.get(i6);
                if (tLRPC$Dialog2 instanceof TLRPC$TL_dialogFolder) {
                    arrayList = dialogs;
                    i5 = size;
                } else if (isDialogPinned(tLRPC$Dialog2)) {
                    arrayList = dialogs;
                    i5 = size;
                    if (DialogObject.isEncryptedDialog(tLRPC$Dialog2.id)) {
                        i8++;
                    } else {
                        i7++;
                    }
                } else {
                    arrayList = dialogs;
                    i5 = size;
                    chatActivityArr = chatActivityArr2;
                    if (!getMessagesController().isPromoDialog(tLRPC$Dialog2.id, false)) {
                        break;
                    }
                    i6++;
                    dialogs = arrayList;
                    size = i5;
                    chatActivityArr2 = chatActivityArr;
                }
                chatActivityArr = chatActivityArr2;
                i6++;
                dialogs = arrayList;
                size = i5;
                chatActivityArr2 = chatActivityArr;
            }
            if (tLRPC$Dialog == null || isDialogPinned(tLRPC$Dialog)) {
                i3 = 0;
                i2 = 0;
                i = 0;
            } else {
                int isEncryptedDialog = DialogObject.isEncryptedDialog(dialogId);
                int i9 = isEncryptedDialog ^ 1;
                if (dialogFilter == null || !dialogFilter.alwaysShow.contains(Long.valueOf(dialogId))) {
                    i = i9;
                    i2 = isEncryptedDialog;
                    i3 = 0;
                } else {
                    i = i9;
                    i2 = isEncryptedDialog;
                    i3 = 1;
                }
            }
            if (z3 && dialogFilter != null) {
                i4 = 100 - dialogFilter.alwaysShow.size();
            } else if (this.folderId == 0 && dialogFilter == null) {
                i4 = getMessagesController().maxPinnedDialogsCount;
            } else {
                i4 = getMessagesController().maxFolderPinnedDialogsCount;
            }
            if (i2 + i8 > i4 || (i + i7) - i3 > i4) {
                c = 0;
                z2 = false;
            } else {
                c = 0;
                z2 = true;
            }
            zArr[c] = z2;
            r1 = c;
        } else {
            chatActivityArr = chatActivityArr2;
            r1 = 0;
        }
        if (zArr[r1]) {
            ActionBarMenuSubItem actionBarMenuSubItem2 = new ActionBarMenuSubItem(getParentActivity(), r1, r1);
            if (isDialogPinned(tLRPC$Dialog)) {
                actionBarMenuSubItem2.setTextAndIcon(LocaleController.getString("UnpinMessage", R.string.UnpinMessage), R.drawable.msg_unpin);
            } else {
                actionBarMenuSubItem2.setTextAndIcon(LocaleController.getString("PinMessage", R.string.PinMessage), R.drawable.msg_pin);
            }
            actionBarMenuSubItem2.setMinimumWidth(160);
            bundle = null;
            actionBarMenuSubItem2.setOnClickListener(new DialogsActivity$$ExternalSyntheticLambda28(this, dialogFilter, tLRPC$Dialog, dialogId));
            actionBarPopupWindowLayoutArr[0].addView(actionBarMenuSubItem2);
        } else {
            bundle = null;
        }
        if (!DialogObject.isUserDialog(dialogId) || !UserObject.isUserSelf(getMessagesController().getUser(Long.valueOf(dialogId)))) {
            ActionBarMenuSubItem actionBarMenuSubItem3 = new ActionBarMenuSubItem(getParentActivity(), false, false);
            if (!getMessagesController().isDialogMuted(dialogId)) {
                actionBarMenuSubItem3.setTextAndIcon(LocaleController.getString("Mute", R.string.Mute), R.drawable.msg_mute);
            } else {
                actionBarMenuSubItem3.setTextAndIcon(LocaleController.getString("Unmute", R.string.Unmute), R.drawable.msg_unmute);
            }
            actionBarMenuSubItem3.setMinimumWidth(160);
            actionBarMenuSubItem3.setOnClickListener(new DialogsActivity$$ExternalSyntheticLambda26(this, dialogId));
            z = false;
            actionBarPopupWindowLayoutArr[0].addView(actionBarMenuSubItem3);
        } else {
            z = false;
        }
        ActionBarMenuSubItem actionBarMenuSubItem4 = new ActionBarMenuSubItem(getParentActivity(), z, true);
        actionBarMenuSubItem4.setIconColor(getThemedColor("dialogRedIcon"));
        actionBarMenuSubItem4.setTextColor(getThemedColor("dialogTextRed"));
        actionBarMenuSubItem4.setTextAndIcon(LocaleController.getString("Delete", R.string.Delete), R.drawable.msg_delete);
        actionBarMenuSubItem4.setMinimumWidth(160);
        actionBarMenuSubItem4.setOnClickListener(new DialogsActivity$$ExternalSyntheticLambda27(this, arrayList2));
        actionBarPopupWindowLayoutArr[0].addView(actionBarMenuSubItem4);
        if (!getMessagesController().checkCanOpenChat(bundle2, this)) {
            return false;
        }
        if (this.searchString != null) {
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
        }
        prepareBlurBitmap();
        this.parentLayout.highlightActionButtons = true;
        Point point = AndroidUtilities.displaySize;
        if (point.x > point.y) {
            ChatActivity chatActivity = new ChatActivity(bundle2);
            chatActivityArr[0] = chatActivity;
            presentFragmentAsPreview(chatActivity);
            return true;
        }
        ChatActivity chatActivity2 = new ChatActivity(bundle2);
        chatActivityArr[0] = chatActivity2;
        presentFragmentAsPreviewWithMenu(chatActivity2, actionBarPopupWindowLayoutArr[0]);
        if (chatActivityArr[0] == null) {
            return true;
        }
        chatActivityArr[0].allowExpandPreviewByClick = true;
        try {
            chatActivityArr[0].getAvatarContainer().getAvatarImageView().performAccessibilityAction(64, bundle);
            return true;
        } catch (Exception unused) {
            return true;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showChatPreview$28(DialogCell dialogCell, long j, View view) {
        if (dialogCell.getHasUnread()) {
            markAsRead(j);
        } else {
            markAsUnread(j);
        }
        finishPreviewFragment();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showChatPreview$30(MessagesController.DialogFilter dialogFilter, TLRPC$Dialog tLRPC$Dialog, long j, View view) {
        finishPreviewFragment();
        AndroidUtilities.runOnUIThread(new DialogsActivity$$ExternalSyntheticLambda45(this, dialogFilter, tLRPC$Dialog, j), 100);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showChatPreview$29(MessagesController.DialogFilter dialogFilter, TLRPC$Dialog tLRPC$Dialog, long j) {
        int i;
        boolean z;
        MessagesController.DialogFilter dialogFilter2 = dialogFilter;
        TLRPC$Dialog tLRPC$Dialog2 = tLRPC$Dialog;
        int i2 = Integer.MAX_VALUE;
        if (dialogFilter2 == null || !isDialogPinned(tLRPC$Dialog2)) {
            i = Integer.MAX_VALUE;
        } else {
            int size = dialogFilter2.pinnedDialogs.size();
            for (int i3 = 0; i3 < size; i3++) {
                i2 = Math.min(i2, dialogFilter2.pinnedDialogs.valueAt(i3));
            }
            i = i2 - this.canPinCount;
        }
        TLRPC$EncryptedChat encryptedChat = DialogObject.isEncryptedDialog(j) ? getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(j))) : null;
        if (!isDialogPinned(tLRPC$Dialog2)) {
            pinDialog(j, true, dialogFilter, i, true);
            getUndoView().showWithAction(0, 78, (Object) 1, (Object) 1600, (Runnable) null, (Runnable) null);
            if (dialogFilter2 != null) {
                if (encryptedChat != null) {
                    if (!dialogFilter2.alwaysShow.contains(Long.valueOf(encryptedChat.user_id))) {
                        dialogFilter2.alwaysShow.add(Long.valueOf(encryptedChat.user_id));
                    }
                } else if (!dialogFilter2.alwaysShow.contains(Long.valueOf(j))) {
                    dialogFilter2.alwaysShow.add(Long.valueOf(j));
                }
            }
        } else {
            pinDialog(j, false, dialogFilter, i, true);
            getUndoView().showWithAction(0, 79, (Object) 1, (Object) 1600, (Runnable) null, (Runnable) null);
        }
        if (dialogFilter2 != null) {
            z = true;
            FilterCreateActivity.saveFilterToServer(dialogFilter, dialogFilter2.flags, dialogFilter2.name, dialogFilter2.alwaysShow, dialogFilter2.neverShow, dialogFilter2.pinnedDialogs, false, false, true, true, false, this, (Runnable) null);
        } else {
            z = true;
        }
        getMessagesController().reorderPinnedDialogs(this.folderId, (ArrayList<TLRPC$InputDialogPeer>) null, 0);
        updateCounters(z);
        if (this.viewPages != null) {
            int i4 = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (i4 >= viewPageArr.length) {
                    break;
                }
                viewPageArr[i4].dialogsAdapter.onReorderStateChanged(false);
                i4++;
            }
        }
        updateVisibleRows(MessagesController.UPDATE_MASK_REORDER | MessagesController.UPDATE_MASK_CHECK);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showChatPreview$31(long j, View view) {
        boolean isDialogMuted = getMessagesController().isDialogMuted(j);
        if (!isDialogMuted) {
            getNotificationsController().setDialogNotificationsSettings(j, 3);
        } else {
            getNotificationsController().setDialogNotificationsSettings(j, 4);
        }
        BulletinFactory.createMuteBulletin(this, !isDialogMuted, (Theme.ResourcesProvider) null).show();
        finishPreviewFragment();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showChatPreview$32(ArrayList arrayList, View view) {
        performSelectedDialogsAction(arrayList, 102, false);
        finishPreviewFragment();
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
        AndroidUtilities.runOnUIThread(new DialogsActivity$$ExternalSyntheticLambda37(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDialogAnimationFinished$33() {
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
        DrawerProfileCell.AnimatedStatusView animatedStatusView2 = this.animatedStatusView;
        if (animatedStatusView2 != null) {
            animatedStatusView2.translateY2((float) ((int) f));
            this.animatedStatusView.setAlpha(1.0f - ((-f) / ((float) ActionBar.getCurrentActionBarHeight())));
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
        if (z && this.afterSignup) {
            try {
                this.fragmentView.performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
            if (getParentActivity() instanceof LaunchActivity) {
                ((LaunchActivity) getParentActivity()).getFireworksOverlay().start();
            }
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
            this.actionBar.setBackButtonContentDescription(LocaleController.getString("AccDescrOpenMenu", R.string.AccDescrOpenMenu));
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
        ofFloat.addUpdateListener(new DialogsActivity$$ExternalSyntheticLambda1(this));
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
    public /* synthetic */ void lambda$hideActionMode$34(ValueAnimator valueAnimator) {
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
    /* JADX WARNING: Removed duplicated region for block: B:148:0x0321  */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x0326  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x0332  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x046a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void performSelectedDialogsAction(java.util.ArrayList<java.lang.Long> r27, int r28, boolean r29) {
        /*
            r26 = this;
            r13 = r26
            r7 = r27
            r14 = r28
            android.app.Activity r0 = r26.getParentActivity()
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
            if (r0 == r1) goto L_0x0026
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r13.viewPages
            r0 = r0[r15]
            int r0 = r0.dialogsType
            if (r0 != r2) goto L_0x0037
        L_0x0026:
            org.telegram.ui.ActionBar.ActionBar r0 = r13.actionBar
            boolean r0 = r0.isActionModeShowed()
            if (r0 == 0) goto L_0x0039
            org.telegram.ui.ActionBar.ActionBar r0 = r13.actionBar
            boolean r0 = r0.isActionModeShowed(r8)
            if (r0 == 0) goto L_0x0037
            goto L_0x0039
        L_0x0037:
            r0 = 0
            goto L_0x003a
        L_0x0039:
            r0 = 1
        L_0x003a:
            if (r0 == 0) goto L_0x0053
            org.telegram.messenger.MessagesController r1 = r26.getMessagesController()
            org.telegram.messenger.MessagesController$DialogFilter[] r1 = r1.selectedDialogFilter
            org.telegram.ui.DialogsActivity$ViewPage[] r3 = r13.viewPages
            r3 = r3[r15]
            int r3 = r3.dialogsType
            if (r3 != r2) goto L_0x004e
            r2 = 1
            goto L_0x004f
        L_0x004e:
            r2 = 0
        L_0x004f:
            r1 = r1[r2]
            r9 = r1
            goto L_0x0054
        L_0x0053:
            r9 = r8
        L_0x0054:
            int r10 = r27.size()
            r1 = 105(0x69, float:1.47E-43)
            if (r14 == r1) goto L_0x0584
            r1 = 107(0x6b, float:1.5E-43)
            if (r14 != r1) goto L_0x0062
            goto L_0x0584
        L_0x0062:
            java.lang.String r4 = "Cancel"
            r3 = 108(0x6c, float:1.51E-43)
            r1 = 100
            if (r14 == r1) goto L_0x006c
            if (r14 != r3) goto L_0x0162
        L_0x006c:
            int r6 = r13.canPinCount
            if (r6 == 0) goto L_0x0162
            org.telegram.messenger.MessagesController r6 = r26.getMessagesController()
            int r5 = r13.folderId
            java.util.ArrayList r5 = r6.getDialogs(r5)
            int r6 = r5.size()
            r3 = 0
            r20 = 0
            r21 = 0
        L_0x0083:
            if (r3 >= r6) goto L_0x00b7
            java.lang.Object r22 = r5.get(r3)
            r8 = r22
            org.telegram.tgnet.TLRPC$Dialog r8 = (org.telegram.tgnet.TLRPC$Dialog) r8
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC$TL_dialogFolder
            if (r2 == 0) goto L_0x0092
            goto L_0x00b3
        L_0x0092:
            boolean r2 = r13.isDialogPinned(r8)
            if (r2 == 0) goto L_0x00a6
            long r11 = r8.id
            boolean r2 = org.telegram.messenger.DialogObject.isEncryptedDialog(r11)
            if (r2 == 0) goto L_0x00a3
            int r21 = r21 + 1
            goto L_0x00b3
        L_0x00a3:
            int r20 = r20 + 1
            goto L_0x00b3
        L_0x00a6:
            org.telegram.messenger.MessagesController r2 = r26.getMessagesController()
            long r11 = r8.id
            boolean r2 = r2.isPromoDialog(r11, r15)
            if (r2 != 0) goto L_0x00b3
            goto L_0x00b7
        L_0x00b3:
            int r3 = r3 + 1
            r8 = 0
            goto L_0x0083
        L_0x00b7:
            r2 = 0
            r3 = 0
            r5 = 0
            r6 = 0
        L_0x00bb:
            if (r2 >= r10) goto L_0x00fa
            java.lang.Object r8 = r7.get(r2)
            java.lang.Long r8 = (java.lang.Long) r8
            long r11 = r8.longValue()
            org.telegram.messenger.MessagesController r8 = r26.getMessagesController()
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r8 = r8.dialogs_dict
            java.lang.Object r8 = r8.get(r11)
            org.telegram.tgnet.TLRPC$Dialog r8 = (org.telegram.tgnet.TLRPC$Dialog) r8
            if (r8 == 0) goto L_0x00f7
            boolean r8 = r13.isDialogPinned(r8)
            if (r8 == 0) goto L_0x00dc
            goto L_0x00f7
        L_0x00dc:
            boolean r8 = org.telegram.messenger.DialogObject.isEncryptedDialog(r11)
            if (r8 == 0) goto L_0x00e5
            int r5 = r5 + 1
            goto L_0x00e7
        L_0x00e5:
            int r3 = r3 + 1
        L_0x00e7:
            if (r9 == 0) goto L_0x00f7
            java.util.ArrayList<java.lang.Long> r8 = r9.alwaysShow
            java.lang.Long r11 = java.lang.Long.valueOf(r11)
            boolean r8 = r8.contains(r11)
            if (r8 == 0) goto L_0x00f7
            int r6 = r6 + 1
        L_0x00f7:
            int r2 = r2 + 1
            goto L_0x00bb
        L_0x00fa:
            if (r0 == 0) goto L_0x0105
            java.util.ArrayList<java.lang.Long> r0 = r9.alwaysShow
            int r0 = r0.size()
            int r0 = 100 - r0
            goto L_0x012a
        L_0x0105:
            int r0 = r13.folderId
            if (r0 != 0) goto L_0x0124
            if (r9 == 0) goto L_0x010c
            goto L_0x0124
        L_0x010c:
            org.telegram.messenger.UserConfig r0 = r26.getUserConfig()
            boolean r0 = r0.isPremium()
            if (r0 == 0) goto L_0x011d
            org.telegram.messenger.MessagesController r0 = r26.getMessagesController()
            int r0 = r0.dialogFiltersPinnedLimitPremium
            goto L_0x012a
        L_0x011d:
            org.telegram.messenger.MessagesController r0 = r26.getMessagesController()
            int r0 = r0.dialogFiltersPinnedLimitDefault
            goto L_0x012a
        L_0x0124:
            org.telegram.messenger.MessagesController r0 = r26.getMessagesController()
            int r0 = r0.maxFolderPinnedDialogsCount
        L_0x012a:
            int r5 = r5 + r21
            if (r5 > r0) goto L_0x0133
            int r3 = r3 + r20
            int r3 = r3 - r6
            if (r3 <= r0) goto L_0x0266
        L_0x0133:
            int r1 = r13.folderId
            if (r1 != 0) goto L_0x0149
            if (r9 == 0) goto L_0x013a
            goto L_0x0149
        L_0x013a:
            org.telegram.ui.Components.Premium.LimitReachedBottomSheet r0 = new org.telegram.ui.Components.Premium.LimitReachedBottomSheet
            android.app.Activity r1 = r26.getParentActivity()
            int r2 = r13.currentAccount
            r0.<init>(r13, r1, r15, r2)
            r13.showDialog(r0)
            goto L_0x0161
        L_0x0149:
            int r1 = org.telegram.messenger.R.string.PinFolderLimitReached
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.Object[] r3 = new java.lang.Object[r15]
            java.lang.String r4 = "Chats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r4, r0, r3)
            r2[r15] = r0
            java.lang.String r0 = "PinFolderLimitReached"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            org.telegram.ui.Components.AlertsCreator.showSimpleAlert(r13, r0)
        L_0x0161:
            return
        L_0x0162:
            r0 = 102(0x66, float:1.43E-43)
            if (r14 == r0) goto L_0x016a
            r2 = 103(0x67, float:1.44E-43)
            if (r14 != r2) goto L_0x0237
        L_0x016a:
            r2 = 1
            if (r10 <= r2) goto L_0x0237
            if (r29 == 0) goto L_0x0237
            org.telegram.ui.ActionBar.AlertDialog$Builder r1 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r3 = r26.getParentActivity()
            r1.<init>((android.content.Context) r3)
            if (r14 != r0) goto L_0x019f
            int r0 = org.telegram.messenger.R.string.DeleteFewChatsTitle
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.Object[] r3 = new java.lang.Object[r15]
            java.lang.String r5 = "ChatsSelected"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r5, r10, r3)
            r2[r15] = r3
            java.lang.String r3 = "DeleteFewChatsTitle"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            r1.setTitle(r0)
            int r0 = org.telegram.messenger.R.string.AreYouSureDeleteFewChats
            java.lang.String r2 = "AreYouSureDeleteFewChats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r1.setMessage(r0)
        L_0x019c:
            r0 = 102(0x66, float:1.43E-43)
            goto L_0x01eb
        L_0x019f:
            int r0 = r13.canClearCacheCount
            if (r0 == 0) goto L_0x01c7
            int r0 = org.telegram.messenger.R.string.ClearCacheFewChatsTitle
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.Object[] r3 = new java.lang.Object[r15]
            java.lang.String r5 = "ChatsSelectedClearCache"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r5, r10, r3)
            r2[r15] = r3
            java.lang.String r3 = "ClearCacheFewChatsTitle"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            r1.setTitle(r0)
            int r0 = org.telegram.messenger.R.string.AreYouSureClearHistoryCacheFewChats
            java.lang.String r2 = "AreYouSureClearHistoryCacheFewChats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r1.setMessage(r0)
            goto L_0x019c
        L_0x01c7:
            int r0 = org.telegram.messenger.R.string.ClearFewChatsTitle
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.Object[] r3 = new java.lang.Object[r15]
            java.lang.String r5 = "ChatsSelectedClear"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r5, r10, r3)
            r2[r15] = r3
            java.lang.String r3 = "ClearFewChatsTitle"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            r1.setTitle(r0)
            int r0 = org.telegram.messenger.R.string.AreYouSureClearHistoryFewChats
            java.lang.String r2 = "AreYouSureClearHistoryFewChats"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r1.setMessage(r0)
            goto L_0x019c
        L_0x01eb:
            if (r14 != r0) goto L_0x01f6
            int r0 = org.telegram.messenger.R.string.Delete
            java.lang.String r2 = "Delete"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x020b
        L_0x01f6:
            int r0 = r13.canClearCacheCount
            if (r0 == 0) goto L_0x0203
            int r0 = org.telegram.messenger.R.string.ClearHistoryCache
            java.lang.String r2 = "ClearHistoryCache"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x020b
        L_0x0203:
            int r0 = org.telegram.messenger.R.string.ClearHistory
            java.lang.String r2 = "ClearHistory"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x020b:
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda13 r2 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda13
            r2.<init>(r13, r7, r14)
            r1.setPositiveButton(r0, r2)
            int r0 = org.telegram.messenger.R.string.Cancel
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            r2 = 0
            r1.setNegativeButton(r0, r2)
            org.telegram.ui.ActionBar.AlertDialog r0 = r1.create()
            r13.showDialog(r0)
            r1 = -1
            android.view.View r0 = r0.getButton(r1)
            android.widget.TextView r0 = (android.widget.TextView) r0
            if (r0 == 0) goto L_0x0236
            java.lang.String r1 = "dialogTextRed2"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTextColor(r1)
        L_0x0236:
            return
        L_0x0237:
            r0 = 106(0x6a, float:1.49E-43)
            if (r14 != r0) goto L_0x0266
            if (r29 == 0) goto L_0x0266
            r0 = 1
            if (r10 != r0) goto L_0x0257
            java.lang.Object r0 = r7.get(r15)
            java.lang.Long r0 = (java.lang.Long) r0
            long r0 = r0.longValue()
            org.telegram.messenger.MessagesController r2 = r26.getMessagesController()
            java.lang.Long r0 = java.lang.Long.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r8 = r2.getUser(r0)
            goto L_0x0258
        L_0x0257:
            r8 = 0
        L_0x0258:
            int r0 = r13.canReportSpamCount
            if (r0 == 0) goto L_0x025d
            r15 = 1
        L_0x025d:
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda57 r0 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda57
            r0.<init>(r13, r7)
            org.telegram.ui.Components.AlertsCreator.createBlockDialogAlert(r13, r10, r15, r8, r0)
            return
        L_0x0266:
            r0 = 2147483647(0x7fffffff, float:NaN)
            if (r9 == 0) goto L_0x028e
            if (r14 == r1) goto L_0x0271
            r2 = 108(0x6c, float:1.51E-43)
            if (r14 != r2) goto L_0x028e
        L_0x0271:
            int r2 = r13.canPinCount
            if (r2 == 0) goto L_0x028e
            org.telegram.messenger.support.LongSparseIntArray r2 = r9.pinnedDialogs
            int r2 = r2.size()
            r3 = 0
        L_0x027c:
            if (r3 >= r2) goto L_0x028b
            org.telegram.messenger.support.LongSparseIntArray r5 = r9.pinnedDialogs
            int r5 = r5.valueAt(r3)
            int r0 = java.lang.Math.min(r0, r5)
            int r3 = r3 + 1
            goto L_0x027c
        L_0x028b:
            int r2 = r13.canPinCount
            int r0 = r0 - r2
        L_0x028e:
            r8 = r0
            r11 = 0
            r20 = 0
        L_0x0292:
            if (r11 >= r10) goto L_0x04f8
            java.lang.Object r2 = r7.get(r11)
            java.lang.Long r2 = (java.lang.Long) r2
            long r5 = r2.longValue()
            org.telegram.messenger.MessagesController r2 = r26.getMessagesController()
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r2 = r2.dialogs_dict
            java.lang.Object r2 = r2.get(r5)
            r12 = r2
            org.telegram.tgnet.TLRPC$Dialog r12 = (org.telegram.tgnet.TLRPC$Dialog) r12
            if (r12 != 0) goto L_0x02b9
        L_0x02ad:
            r25 = r4
            r7 = 100
            r16 = 102(0x66, float:1.43E-43)
            r17 = 3
        L_0x02b5:
            r19 = 103(0x67, float:1.44E-43)
            goto L_0x04ed
        L_0x02b9:
            boolean r2 = org.telegram.messenger.DialogObject.isEncryptedDialog(r5)
            if (r2 == 0) goto L_0x02e8
            org.telegram.messenger.MessagesController r2 = r26.getMessagesController()
            int r3 = org.telegram.messenger.DialogObject.getEncryptedChatId(r5)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$EncryptedChat r2 = r2.getEncryptedChat(r3)
            if (r2 == 0) goto L_0x02e0
            org.telegram.messenger.MessagesController r3 = r26.getMessagesController()
            long r0 = r2.user_id
            java.lang.Long r0 = java.lang.Long.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r3.getUser(r0)
            goto L_0x02e5
        L_0x02e0:
            org.telegram.tgnet.TLRPC$TL_userEmpty r0 = new org.telegram.tgnet.TLRPC$TL_userEmpty
            r0.<init>()
        L_0x02e5:
            r3 = r0
            r1 = r2
            goto L_0x02fc
        L_0x02e8:
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r5)
            if (r0 == 0) goto L_0x02ff
            org.telegram.messenger.MessagesController r0 = r26.getMessagesController()
            java.lang.Long r1 = java.lang.Long.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r1)
            r3 = r0
            r1 = 0
        L_0x02fc:
            r24 = 0
            goto L_0x0310
        L_0x02ff:
            org.telegram.messenger.MessagesController r0 = r26.getMessagesController()
            long r1 = -r5
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            r24 = r0
            r1 = 0
            r3 = 0
        L_0x0310:
            if (r24 != 0) goto L_0x0315
            if (r3 != 0) goto L_0x0315
            goto L_0x02ad
        L_0x0315:
            if (r3 == 0) goto L_0x0326
            boolean r0 = r3.bot
            if (r0 == 0) goto L_0x0326
            boolean r0 = org.telegram.messenger.MessagesController.isSupportUser(r3)
            if (r0 != 0) goto L_0x0326
            r2 = 100
            r21 = 1
            goto L_0x032a
        L_0x0326:
            r2 = 100
            r21 = 0
        L_0x032a:
            if (r14 == r2) goto L_0x046a
            r0 = 108(0x6c, float:1.51E-43)
            if (r14 != r0) goto L_0x0332
            goto L_0x046a
        L_0x0332:
            r1 = 101(0x65, float:1.42E-43)
            if (r14 != r1) goto L_0x0344
            int r1 = r13.canReadCount
            if (r1 == 0) goto L_0x033f
            r13.markAsRead(r5)
            goto L_0x02ad
        L_0x033f:
            r13.markAsUnread(r5)
            goto L_0x02ad
        L_0x0344:
            r1 = 102(0x66, float:1.43E-43)
            if (r14 == r1) goto L_0x039c
            r1 = 103(0x67, float:1.44E-43)
            if (r14 != r1) goto L_0x0352
            r1 = 1
            r17 = 3
            r18 = 4
            goto L_0x03a1
        L_0x0352:
            r1 = 104(0x68, float:1.46E-43)
            if (r14 != r1) goto L_0x02ad
            r1 = 1
            if (r10 != r1) goto L_0x036b
            int r3 = r13.canMuteCount
            if (r3 != r1) goto L_0x036b
            r1 = 0
            org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.Components.AlertsCreator.createMuteAlert(r13, r5, r1)
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda15 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda15
            r1.<init>(r13)
            r13.showDialog(r0, r1)
            return
        L_0x036b:
            int r1 = r13.canUnmuteCount
            if (r1 == 0) goto L_0x0385
            org.telegram.messenger.MessagesController r1 = r26.getMessagesController()
            boolean r1 = r1.isDialogMuted(r5)
            if (r1 != 0) goto L_0x037b
            goto L_0x02ad
        L_0x037b:
            org.telegram.messenger.NotificationsController r1 = r26.getNotificationsController()
            r3 = 4
            r1.setDialogNotificationsSettings(r5, r3)
            goto L_0x02ad
        L_0x0385:
            r3 = 4
            org.telegram.messenger.MessagesController r1 = r26.getMessagesController()
            boolean r1 = r1.isDialogMuted(r5)
            if (r1 == 0) goto L_0x0392
            goto L_0x02ad
        L_0x0392:
            org.telegram.messenger.NotificationsController r1 = r26.getNotificationsController()
            r12 = 3
            r1.setDialogNotificationsSettings(r5, r12)
            goto L_0x02ad
        L_0x039c:
            r17 = 3
            r18 = 4
            r1 = 1
        L_0x03a1:
            if (r10 != r1) goto L_0x041d
            r1 = 102(0x66, float:1.43E-43)
            if (r14 != r1) goto L_0x03ec
            boolean r0 = r13.canDeletePsaSelected
            if (r0 == 0) goto L_0x03ec
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r1 = r26.getParentActivity()
            r0.<init>((android.content.Context) r1)
            int r1 = org.telegram.messenger.R.string.PsaHideChatAlertTitle
            java.lang.String r2 = "PsaHideChatAlertTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            int r1 = org.telegram.messenger.R.string.PsaHideChatAlertText
            java.lang.String r2 = "PsaHideChatAlertText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setMessage(r1)
            int r1 = org.telegram.messenger.R.string.PsaHide
            java.lang.String r2 = "PsaHide"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda9 r2 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda9
            r2.<init>(r13)
            r0.setPositiveButton(r1, r2)
            int r1 = org.telegram.messenger.R.string.Cancel
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r2 = 0
            r0.setNegativeButton(r1, r2)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r13.showDialog(r0)
            goto L_0x041c
        L_0x03ec:
            r0 = 103(0x67, float:1.44E-43)
            if (r14 != r0) goto L_0x03f2
            r7 = 1
            goto L_0x03f3
        L_0x03f2:
            r7 = 0
        L_0x03f3:
            long r0 = r12.id
            boolean r8 = org.telegram.messenger.DialogObject.isEncryptedDialog(r0)
            r12 = 102(0x66, float:1.43E-43)
            if (r14 != r12) goto L_0x03fe
            r15 = 1
        L_0x03fe:
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda51 r9 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda51
            r0 = r9
            r1 = r26
            r2 = r28
            r23 = r3
            r3 = r24
            r4 = r5
            r6 = r21
            r0.<init>(r1, r2, r3, r4, r6)
            r0 = r26
            r1 = r7
            r2 = r24
            r3 = r23
            r4 = r8
            r5 = r15
            r6 = r9
            org.telegram.ui.Components.AlertsCreator.createClearOrDeleteDialogAlert(r0, r1, r2, r3, r4, r5, r6)
        L_0x041c:
            return
        L_0x041d:
            r12 = 102(0x66, float:1.43E-43)
            org.telegram.messenger.MessagesController r1 = r26.getMessagesController()
            r3 = 1
            boolean r1 = r1.isPromoDialog(r5, r3)
            if (r1 == 0) goto L_0x0439
            org.telegram.messenger.MessagesController r1 = r26.getMessagesController()
            r1.hidePromoDialog()
        L_0x0431:
            r25 = r4
            r7 = 100
            r16 = 102(0x66, float:1.43E-43)
            goto L_0x02b5
        L_0x0439:
            r3 = 103(0x67, float:1.44E-43)
            if (r14 != r3) goto L_0x044a
            int r1 = r13.canClearCacheCount
            if (r1 == 0) goto L_0x044a
            org.telegram.messenger.MessagesController r1 = r26.getMessagesController()
            r7 = 2
            r1.deleteDialog(r5, r7, r15)
            goto L_0x0431
        L_0x044a:
            r7 = 2
            r16 = 0
            r19 = 108(0x6c, float:1.51E-43)
            r0 = r26
            r1 = r28
            r7 = 100
            r15 = 108(0x6c, float:1.51E-43)
            r19 = 103(0x67, float:1.44E-43)
            r2 = r5
            r25 = r4
            r4 = r24
            r6 = 4
            r5 = r21
            r6 = r16
            r0.lambda$performSelectedDialogsAction$40(r1, r2, r4, r5, r6)
            r16 = 102(0x66, float:1.43E-43)
            goto L_0x04ed
        L_0x046a:
            r25 = r4
            r7 = 100
            r15 = 108(0x6c, float:1.51E-43)
            r16 = 102(0x66, float:1.43E-43)
            r17 = 3
            r19 = 103(0x67, float:1.44E-43)
            int r0 = r13.canPinCount
            if (r0 == 0) goto L_0x04d4
            boolean r0 = r13.isDialogPinned(r12)
            if (r0 == 0) goto L_0x0482
            goto L_0x04ed
        L_0x0482:
            int r20 = r20 + 1
            r3 = 1
            r0 = 1
            if (r10 != r0) goto L_0x048b
            r18 = 1
            goto L_0x048d
        L_0x048b:
            r18 = 0
        L_0x048d:
            r0 = r26
            r4 = r1
            r1 = r5
            r6 = r4
            r4 = r9
            r5 = r8
            r15 = r6
            r6 = r18
            r0.pinDialog(r1, r3, r4, r5, r6)
            if (r9 == 0) goto L_0x04ed
            int r8 = r8 + 1
            if (r15 == 0) goto L_0x04ba
            java.util.ArrayList<java.lang.Long> r0 = r9.alwaysShow
            long r1 = r15.user_id
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            boolean r0 = r0.contains(r1)
            if (r0 != 0) goto L_0x04ed
            java.util.ArrayList<java.lang.Long> r0 = r9.alwaysShow
            long r1 = r15.user_id
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            r0.add(r1)
            goto L_0x04ed
        L_0x04ba:
            java.util.ArrayList<java.lang.Long> r0 = r9.alwaysShow
            long r1 = r12.id
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            boolean r0 = r0.contains(r1)
            if (r0 != 0) goto L_0x04ed
            java.util.ArrayList<java.lang.Long> r0 = r9.alwaysShow
            long r1 = r12.id
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            r0.add(r1)
            goto L_0x04ed
        L_0x04d4:
            boolean r0 = r13.isDialogPinned(r12)
            if (r0 != 0) goto L_0x04db
            goto L_0x04ed
        L_0x04db:
            int r20 = r20 + 1
            r3 = 0
            r0 = 1
            if (r10 != r0) goto L_0x04e3
            r12 = 1
            goto L_0x04e4
        L_0x04e3:
            r12 = 0
        L_0x04e4:
            r0 = r26
            r1 = r5
            r4 = r9
            r5 = r8
            r6 = r12
            r0.pinDialog(r1, r3, r4, r5, r6)
        L_0x04ed:
            int r11 = r11 + 1
            r7 = r27
            r4 = r25
            r1 = 100
            r15 = 0
            goto L_0x0292
        L_0x04f8:
            r0 = 104(0x68, float:1.46E-43)
            r7 = 100
            r16 = 102(0x66, float:1.43E-43)
            r12 = 1
            if (r14 != r0) goto L_0x0517
            if (r10 != r12) goto L_0x0507
            int r0 = r13.canMuteCount
            if (r0 == r12) goto L_0x0517
        L_0x0507:
            int r0 = r13.canUnmuteCount
            if (r0 != 0) goto L_0x050e
            r0 = 0
            r2 = 1
            goto L_0x0510
        L_0x050e:
            r0 = 0
            r2 = 0
        L_0x0510:
            org.telegram.ui.Components.Bulletin r1 = org.telegram.ui.Components.BulletinFactory.createMuteBulletin(r13, r2, r0)
            r1.show()
        L_0x0517:
            if (r14 == r7) goto L_0x0521
            r0 = 108(0x6c, float:1.51E-43)
            if (r14 != r0) goto L_0x051e
            goto L_0x0521
        L_0x051e:
            r15 = 100
            goto L_0x0573
        L_0x0521:
            r10 = 0
            if (r9 == 0) goto L_0x054d
            int r1 = r9.flags
            java.lang.String r2 = r9.name
            java.util.ArrayList<java.lang.Long> r3 = r9.alwaysShow
            java.util.ArrayList<java.lang.Long> r4 = r9.neverShow
            org.telegram.messenger.support.LongSparseIntArray r5 = r9.pinnedDialogs
            r6 = 0
            r8 = 0
            r15 = 1
            r17 = 1
            r18 = 0
            r19 = 0
            r0 = r9
            r9 = 100
            r7 = r8
            r8 = r15
            r15 = 100
            r9 = r17
            r10 = r18
            r11 = r26
            r12 = r19
            org.telegram.ui.FilterCreateActivity.saveFilterToServer(r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            r2 = 0
            goto L_0x055b
        L_0x054d:
            r15 = 100
            org.telegram.messenger.MessagesController r0 = r26.getMessagesController()
            int r1 = r13.folderId
            r2 = 0
            r4 = 0
            r0.reorderPinnedDialogs(r1, r4, r2)
        L_0x055b:
            boolean r0 = r13.searchIsShowed
            if (r0 == 0) goto L_0x0573
            org.telegram.ui.Components.UndoView r0 = r26.getUndoView()
            int r1 = r13.canPinCount
            if (r1 == 0) goto L_0x056a
            r1 = 78
            goto L_0x056c
        L_0x056a:
            r1 = 79
        L_0x056c:
            java.lang.Integer r4 = java.lang.Integer.valueOf(r20)
            r0.showWithAction((long) r2, (int) r1, (java.lang.Object) r4)
        L_0x0573:
            r0 = 108(0x6c, float:1.51E-43)
            if (r14 == r0) goto L_0x057f
            if (r14 == r15) goto L_0x057f
            r0 = 102(0x66, float:1.43E-43)
            if (r14 == r0) goto L_0x057f
            r15 = 1
            goto L_0x0580
        L_0x057f:
            r15 = 0
        L_0x0580:
            r13.hideActionMode(r15)
            return
        L_0x0584:
            r17 = 3
            java.util.ArrayList r7 = new java.util.ArrayList
            r0 = r27
            r8 = 2
            r7.<init>(r0)
            org.telegram.messenger.MessagesController r0 = r26.getMessagesController()
            int r1 = r13.canUnarchiveCount
            if (r1 != 0) goto L_0x0598
            r2 = 1
            goto L_0x0599
        L_0x0598:
            r2 = 0
        L_0x0599:
            r3 = -1
            r4 = 0
            r5 = 0
            r1 = r7
            r0.addDialogToFolder(r1, r2, r3, r4, r5)
            int r0 = r13.canUnarchiveCount
            if (r0 != 0) goto L_0x05f1
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings()
            java.lang.String r1 = "archivehint_l"
            r2 = 0
            boolean r3 = r0.getBoolean(r1, r2)
            if (r3 != 0) goto L_0x05b9
            boolean r2 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r2 == 0) goto L_0x05b7
            goto L_0x05b9
        L_0x05b7:
            r12 = 0
            goto L_0x05ba
        L_0x05b9:
            r12 = 1
        L_0x05ba:
            if (r12 != 0) goto L_0x05c9
            android.content.SharedPreferences$Editor r0 = r0.edit()
            r2 = 1
            android.content.SharedPreferences$Editor r0 = r0.putBoolean(r1, r2)
            r0.commit()
            goto L_0x05ca
        L_0x05c9:
            r2 = 1
        L_0x05ca:
            if (r12 == 0) goto L_0x05d7
            int r0 = r7.size()
            if (r0 <= r2) goto L_0x05d4
            r11 = 4
            goto L_0x05d5
        L_0x05d4:
            r11 = 2
        L_0x05d5:
            r3 = r11
            goto L_0x05e1
        L_0x05d7:
            int r0 = r7.size()
            if (r0 <= r2) goto L_0x05df
            r6 = 5
            goto L_0x05e0
        L_0x05df:
            r6 = 3
        L_0x05e0:
            r3 = r6
        L_0x05e1:
            org.telegram.ui.Components.UndoView r0 = r26.getUndoView()
            r1 = 0
            r4 = 0
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda44 r5 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda44
            r5.<init>(r13, r7)
            r0.showWithAction(r1, r3, r4, r5)
            goto L_0x0622
        L_0x05f1:
            org.telegram.messenger.MessagesController r0 = r26.getMessagesController()
            int r1 = r13.folderId
            java.util.ArrayList r0 = r0.getDialogs(r1)
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r13.viewPages
            if (r1 == 0) goto L_0x0622
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0622
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
            r26.finishFragment()
            goto L_0x0623
        L_0x0622:
            r1 = 0
        L_0x0623:
            r13.hideActionMode(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.performSelectedDialogsAction(java.util.ArrayList, int, boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSelectedDialogsAction$35(ArrayList arrayList) {
        getMessagesController().addDialogToFolder(arrayList, this.folderId == 0 ? 0 : 1, -1, (ArrayList<TLRPC$TL_inputFolderPeer>) null, 0);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSelectedDialogsAction$37(ArrayList arrayList, int i, DialogInterface dialogInterface, int i2) {
        if (!arrayList.isEmpty()) {
            ArrayList arrayList2 = new ArrayList(arrayList);
            getUndoView().showWithAction((ArrayList<Long>) arrayList2, i == 102 ? 27 : 26, (Object) null, (Object) null, (Runnable) new DialogsActivity$$ExternalSyntheticLambda42(this, i, arrayList2), (Runnable) null);
            hideActionMode(i == 103);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSelectedDialogsAction$36(int i, ArrayList arrayList) {
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
    public /* synthetic */ void lambda$performSelectedDialogsAction$38(ArrayList arrayList, boolean z, boolean z2) {
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
    public /* synthetic */ void lambda$performSelectedDialogsAction$39(DialogInterface dialogInterface, int i) {
        getMessagesController().hidePromoDialog();
        hideActionMode(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSelectedDialogsAction$41(int i, TLRPC$Chat tLRPC$Chat, long j, boolean z, boolean z2) {
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
            DialogsActivity$$ExternalSyntheticLambda41 dialogsActivity$$ExternalSyntheticLambda41 = r0;
            int i8 = i2;
            DialogsActivity$$ExternalSyntheticLambda41 dialogsActivity$$ExternalSyntheticLambda412 = new DialogsActivity$$ExternalSyntheticLambda41(this, i, j, tLRPC$Chat, z, z2);
            undoView2.showWithAction(j2, i7, (Runnable) dialogsActivity$$ExternalSyntheticLambda41);
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
    public /* synthetic */ void lambda$performSelectedDialogsAction$42(DialogInterface dialogInterface) {
        hideActionMode(true);
    }

    /* JADX WARNING: Removed duplicated region for block: B:37:0x00b1  */
    /* JADX WARNING: Removed duplicated region for block: B:41:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void markAsRead(long r16) {
        /*
            r15 = this;
            r0 = r15
            r2 = r16
            org.telegram.messenger.MessagesController r1 = r15.getMessagesController()
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r1 = r1.dialogs_dict
            java.lang.Object r1 = r1.get(r2)
            org.telegram.tgnet.TLRPC$Dialog r1 = (org.telegram.tgnet.TLRPC$Dialog) r1
            org.telegram.ui.DialogsActivity$ViewPage[] r4 = r0.viewPages
            r12 = 0
            r4 = r4[r12]
            int r4 = r4.dialogsType
            r5 = 0
            r6 = 8
            r7 = 1
            r8 = 7
            if (r4 == r8) goto L_0x0029
            org.telegram.ui.DialogsActivity$ViewPage[] r4 = r0.viewPages
            r4 = r4[r12]
            int r4 = r4.dialogsType
            if (r4 != r6) goto L_0x003a
        L_0x0029:
            org.telegram.ui.ActionBar.ActionBar r4 = r0.actionBar
            boolean r4 = r4.isActionModeShowed()
            if (r4 == 0) goto L_0x003c
            org.telegram.ui.ActionBar.ActionBar r4 = r0.actionBar
            boolean r4 = r4.isActionModeShowed(r5)
            if (r4 == 0) goto L_0x003a
            goto L_0x003c
        L_0x003a:
            r4 = 0
            goto L_0x003d
        L_0x003c:
            r4 = 1
        L_0x003d:
            if (r4 == 0) goto L_0x0054
            org.telegram.messenger.MessagesController r4 = r15.getMessagesController()
            org.telegram.messenger.MessagesController$DialogFilter[] r4 = r4.selectedDialogFilter
            org.telegram.ui.DialogsActivity$ViewPage[] r5 = r0.viewPages
            r5 = r5[r12]
            int r5 = r5.dialogsType
            if (r5 != r6) goto L_0x0051
            r5 = 1
            goto L_0x0052
        L_0x0051:
            r5 = 0
        L_0x0052:
            r5 = r4[r5]
        L_0x0054:
            r13 = 2
            r0.debugLastUpdateAction = r13
            r4 = -1
            if (r5 == 0) goto L_0x0093
            int r6 = r5.flags
            int r8 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ
            r6 = r6 & r8
            if (r6 == 0) goto L_0x0093
            int r6 = r0.currentAccount
            boolean r5 = r5.alwaysShow(r6, r1)
            if (r5 != 0) goto L_0x0093
            r15.setDialogsListFrozen(r7)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r5 = r0.frozenDialogsList
            if (r5 == 0) goto L_0x0093
            r5 = 0
        L_0x0071:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r6 = r0.frozenDialogsList
            int r6 = r6.size()
            if (r5 >= r6) goto L_0x008c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r6 = r0.frozenDialogsList
            java.lang.Object r6 = r6.get(r5)
            org.telegram.tgnet.TLRPC$Dialog r6 = (org.telegram.tgnet.TLRPC$Dialog) r6
            long r6 = r6.id
            int r8 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r8 != 0) goto L_0x0089
            r4 = r5
            goto L_0x008c
        L_0x0089:
            int r5 = r5 + 1
            goto L_0x0071
        L_0x008c:
            if (r4 >= 0) goto L_0x0091
            r15.setDialogsListFrozen(r12, r12)
        L_0x0091:
            r14 = r4
            goto L_0x0094
        L_0x0093:
            r14 = -1
        L_0x0094:
            org.telegram.messenger.MessagesController r4 = r15.getMessagesController()
            r4.markMentionsAsRead(r2)
            org.telegram.messenger.MessagesController r4 = r15.getMessagesController()
            int r5 = r1.top_message
            int r6 = r1.last_message_date
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 1
            r11 = 0
            r1 = r4
            r2 = r16
            r4 = r5
            r1.markDialogAsRead(r2, r4, r5, r6, r7, r8, r9, r10, r11)
            if (r14 < 0) goto L_0x00ce
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r1 = r0.frozenDialogsList
            r1.remove(r14)
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r0.viewPages
            r1 = r1[r12]
            org.telegram.ui.Components.DialogsItemAnimator r1 = r1.dialogsItemAnimator
            r1.prepareForRemove()
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r0.viewPages
            r1 = r1[r12]
            org.telegram.ui.Adapters.DialogsAdapter r1 = r1.dialogsAdapter
            r1.notifyItemRemoved(r14)
            r0.dialogRemoveFinished = r13
        L_0x00ce:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.markAsRead(long):void");
    }

    private void markAsUnread(long j) {
        getMessagesController().markDialogAsUnread(j, (TLRPC$InputPeer) null, 0);
    }

    /* access modifiers changed from: private */
    /* renamed from: performDeleteOrClearDialogAction */
    public void lambda$performSelectedDialogsAction$40(int i, long j, TLRPC$Chat tLRPC$Chat, boolean z, boolean z2) {
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
            getMessagesController().deleteParticipantFromChat((long) ((int) (-j)), getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId())), (TLRPC$Chat) null, z2, false);
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
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda35 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda35
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
    public /* synthetic */ void lambda$pinDialog$43() {
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
                    this.clearItem.setText(LocaleController.getString("ClearHistoryCache", R.string.ClearHistoryCache));
                } else {
                    this.clearItem.setText(LocaleController.getString("ClearHistory", R.string.ClearHistory));
                }
            } else {
                this.clearItem.setVisibility(8);
            }
            if (this.canUnarchiveCount != 0) {
                String string = LocaleController.getString("Unarchive", R.string.Unarchive);
                ActionBarMenuSubItem actionBarMenuSubItem = this.archiveItem;
                int i13 = R.drawable.msg_unarchive;
                actionBarMenuSubItem.setTextAndIcon(string, i13);
                this.archive2Item.setIcon(i13);
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
                String string2 = LocaleController.getString("Archive", R.string.Archive);
                ActionBarMenuSubItem actionBarMenuSubItem2 = this.archiveItem;
                int i14 = R.drawable.msg_archive;
                actionBarMenuSubItem2.setTextAndIcon(string2, i14);
                this.archive2Item.setIcon(i14);
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
            if (filterTabsView5 == null || filterTabsView5.getVisibility() != 0 || this.filterTabsView.currentTabIsDefault()) {
                this.removeFromFolderItem.setVisibility(8);
            } else {
                this.removeFromFolderItem.setVisibility(i);
            }
            FilterTabsView filterTabsView6 = this.filterTabsView;
            if (filterTabsView6 == null || filterTabsView6.getVisibility() != 0 || !this.filterTabsView.currentTabIsDefault() || FiltersListBottomSheet.getCanAddDialogFilters(this, this.selectedDialogs).isEmpty()) {
                this.addToFolderItem.setVisibility(8);
            } else {
                this.addToFolderItem.setVisibility(0);
            }
            if (this.canUnmuteCount != 0) {
                this.muteItem.setIcon(R.drawable.msg_unmute);
                this.muteItem.setContentDescription(LocaleController.getString("ChatsUnmute", R.string.ChatsUnmute));
            } else {
                this.muteItem.setIcon(R.drawable.msg_mute);
                this.muteItem.setContentDescription(LocaleController.getString("ChatsMute", R.string.ChatsMute));
            }
            if (this.canReadCount != 0) {
                this.readItem.setTextAndIcon(LocaleController.getString("MarkAsRead", R.string.MarkAsRead), R.drawable.msg_markread);
            } else {
                this.readItem.setTextAndIcon(LocaleController.getString("MarkAsUnread", R.string.MarkAsUnread), R.drawable.msg_markunread);
            }
            if (this.canPinCount != 0) {
                this.pinItem.setIcon(R.drawable.msg_pin);
                this.pinItem.setContentDescription(LocaleController.getString("PinToTop", R.string.PinToTop));
                this.pin2Item.setText(LocaleController.getString("DialogPin", R.string.DialogPin));
                return;
            }
            this.pinItem.setIcon(R.drawable.msg_unpin);
            this.pinItem.setContentDescription(LocaleController.getString("UnpinFromTop", R.string.UnpinFromTop));
            this.pin2Item.setText(LocaleController.getString("DialogUnpin", R.string.DialogUnpin));
        }
    }

    /* access modifiers changed from: private */
    public boolean validateSlowModeDialog(long j) {
        TLRPC$Chat chat;
        ChatActivityEnterView chatActivityEnterView;
        if ((this.messagesCount <= 1 && ((chatActivityEnterView = this.commentView) == null || chatActivityEnterView.getVisibility() != 0 || TextUtils.isEmpty(this.commentView.getFieldText()))) || !DialogObject.isChatDialog(j) || (chat = getMessagesController().getChat(Long.valueOf(-j))) == null || ChatObject.hasAdminRights(chat) || !chat.slowmode_enabled) {
            return true;
        }
        AlertsCreator.showSimpleAlert(this, LocaleController.getString("Slowmode", R.string.Slowmode), LocaleController.getString("SlowmodeSendError", R.string.SlowmodeSendError));
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
                this.actionBar.setBackButtonContentDescription(LocaleController.getString("AccDescrGoBack", R.string.AccDescrGoBack));
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
    public /* synthetic */ void lambda$showOrUpdateActionMode$44(ValueAnimator valueAnimator) {
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

    /* access modifiers changed from: private */
    public void updateProxyButton(boolean z, boolean z2) {
        boolean z3;
        ActionBarMenuItem actionBarMenuItem;
        if (this.proxyDrawable != null) {
            ActionBarMenuItem actionBarMenuItem2 = this.doneItem;
            if (actionBarMenuItem2 == null || actionBarMenuItem2.getVisibility() != 0) {
                boolean z4 = false;
                int i = 0;
                while (true) {
                    if (i >= getDownloadController().downloadingFiles.size()) {
                        z3 = false;
                        break;
                    } else if (getFileLoader().isLoadingFile(getDownloadController().downloadingFiles.get(i).getFileName())) {
                        z3 = true;
                        break;
                    } else {
                        i++;
                    }
                }
                if (this.searching || (!getDownloadController().hasUnviewedDownloads() && !z3 && !(this.downloadsItem.getVisibility() == 0 && this.downloadsItem.getAlpha() == 1.0f && !z2))) {
                    this.downloadsItem.setVisibility(8);
                    this.downloadsItemVisible = false;
                } else {
                    this.downloadsItemVisible = true;
                    this.downloadsItem.setVisibility(0);
                }
                SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                String string = sharedPreferences.getString("proxy_ip", "");
                boolean z5 = sharedPreferences.getBoolean("proxy_enabled", false);
                if ((this.downloadsItemVisible || this.searching || !z5 || TextUtils.isEmpty(string)) && (!getMessagesController().blockedCountry || SharedConfig.proxyList.isEmpty())) {
                    this.proxyItemVisible = false;
                    this.proxyItem.setVisibility(8);
                    return;
                }
                if (!this.actionBar.isSearchFieldVisible() && ((actionBarMenuItem = this.doneItem) == null || actionBarMenuItem.getVisibility() != 0)) {
                    this.proxyItem.setVisibility(0);
                }
                this.proxyItemVisible = true;
                ProxyDrawable proxyDrawable2 = this.proxyDrawable;
                int i2 = this.currentConnectionState;
                if (i2 == 3 || i2 == 5) {
                    z4 = true;
                }
                proxyDrawable2.setConnected(z5, z4, z);
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
                ActionBarMenuItem actionBarMenuItem4 = this.downloadsItem;
                if (actionBarMenuItem4 != null && this.downloadsItemVisible) {
                    actionBarMenuItem4.setVisibility(0);
                }
            }
            ArrayList arrayList = new ArrayList();
            ActionBarMenuItem actionBarMenuItem5 = this.doneItem;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            float f = 1.0f;
            fArr[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem5, property, fArr));
            if (this.proxyItemVisible) {
                ActionBarMenuItem actionBarMenuItem6 = this.proxyItem;
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                fArr2[0] = z ? 0.0f : 1.0f;
                arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem6, property2, fArr2));
            }
            if (this.passcodeItemVisible) {
                ActionBarMenuItem actionBarMenuItem7 = this.passcodeItem;
                Property property3 = View.ALPHA;
                float[] fArr3 = new float[1];
                fArr3[0] = z ? 0.0f : 1.0f;
                arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem7, property3, fArr3));
            }
            ActionBarMenuItem actionBarMenuItem8 = this.searchItem;
            Property property4 = View.ALPHA;
            float[] fArr4 = new float[1];
            if (z) {
                f = 0.0f;
            }
            fArr4[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem8, property4, fArr4));
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
                        if (DialogsActivity.this.downloadsItem != null && DialogsActivity.this.downloadsItemVisible) {
                            DialogsActivity.this.downloadsItem.setVisibility(4);
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
        CharSequence charSequence = "";
        if (this.commentView != null) {
            if (this.selectedDialogs.isEmpty()) {
                if (this.initialDialogsType == 3 && this.selectAlertString == null) {
                    this.actionBar.setTitle(LocaleController.getString("ForwardTo", R.string.ForwardTo));
                } else {
                    this.actionBar.setTitle(LocaleController.getString("SelectChat", R.string.SelectChat));
                }
                if (this.commentView.getTag() != null) {
                    this.commentView.hidePopup(false);
                    this.commentView.closeKeyboard();
                    AnimatorSet animatorSet = this.commentViewAnimator;
                    if (animatorSet != null) {
                        animatorSet.cancel();
                    }
                    this.commentViewAnimator = new AnimatorSet();
                    this.commentView.setTranslationY(0.0f);
                    AnimatorSet animatorSet2 = this.commentViewAnimator;
                    ChatActivityEnterView chatActivityEnterView = this.commentView;
                    animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(chatActivityEnterView, View.TRANSLATION_Y, new float[]{(float) chatActivityEnterView.getMeasuredHeight()}), ObjectAnimator.ofFloat(this.writeButtonContainer, View.SCALE_X, new float[]{0.2f}), ObjectAnimator.ofFloat(this.writeButtonContainer, View.SCALE_Y, new float[]{0.2f}), ObjectAnimator.ofFloat(this.writeButtonContainer, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.selectedCountView, View.SCALE_X, new float[]{0.2f}), ObjectAnimator.ofFloat(this.selectedCountView, View.SCALE_Y, new float[]{0.2f}), ObjectAnimator.ofFloat(this.selectedCountView, View.ALPHA, new float[]{0.0f})});
                    this.commentViewAnimator.setDuration(180);
                    this.commentViewAnimator.setInterpolator(new DecelerateInterpolator());
                    this.commentViewAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            DialogsActivity.this.commentView.setVisibility(8);
                            DialogsActivity.this.writeButtonContainer.setVisibility(8);
                        }
                    });
                    this.commentViewAnimator.start();
                    this.commentView.setTag((Object) null);
                    this.fragmentView.requestLayout();
                }
            } else {
                this.selectedCountView.invalidate();
                if (this.commentView.getTag() == null) {
                    this.commentView.setFieldText(charSequence);
                    AnimatorSet animatorSet3 = this.commentViewAnimator;
                    if (animatorSet3 != null) {
                        animatorSet3.cancel();
                    }
                    this.commentView.setVisibility(0);
                    this.writeButtonContainer.setVisibility(0);
                    AnimatorSet animatorSet4 = new AnimatorSet();
                    this.commentViewAnimator = animatorSet4;
                    ChatActivityEnterView chatActivityEnterView2 = this.commentView;
                    animatorSet4.playTogether(new Animator[]{ObjectAnimator.ofFloat(chatActivityEnterView2, View.TRANSLATION_Y, new float[]{(float) chatActivityEnterView2.getMeasuredHeight(), 0.0f}), ObjectAnimator.ofFloat(this.writeButtonContainer, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.writeButtonContainer, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.writeButtonContainer, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.selectedCountView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.selectedCountView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.selectedCountView, View.ALPHA, new float[]{1.0f})});
                    this.commentViewAnimator.setDuration(180);
                    this.commentViewAnimator.setInterpolator(new DecelerateInterpolator());
                    this.commentViewAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            DialogsActivity.this.commentView.setTag(2);
                            DialogsActivity.this.commentView.requestLayout();
                        }
                    });
                    this.commentViewAnimator.start();
                    this.commentView.setTag(1);
                }
                this.actionBar.setTitle(LocaleController.formatPluralString("Recipient", this.selectedDialogs.size(), new Object[0]));
            }
        } else if (this.initialDialogsType == 10) {
            hideFloatingButton(this.selectedDialogs.isEmpty());
        }
        ArrayList<Long> arrayList = this.selectedDialogs;
        ChatActivityEnterView chatActivityEnterView3 = this.commentView;
        if (chatActivityEnterView3 != null) {
            charSequence = chatActivityEnterView3.getFieldText();
        }
        boolean shouldShowNextButton = shouldShowNextButton(this, arrayList, charSequence, false);
        this.isNextButton = shouldShowNextButton;
        AndroidUtilities.updateViewVisibilityAnimated(this.writeButton[0], !shouldShowNextButton, 0.5f, true);
        AndroidUtilities.updateViewVisibilityAnimated(this.writeButton[1], this.isNextButton, 0.5f, true);
    }

    @TargetApi(23)
    private void askForPermissons(boolean z) {
        Activity parentActivity = getParentActivity();
        if (parentActivity != null) {
            ArrayList arrayList = new ArrayList();
            if (getUserConfig().syncContacts && this.askAboutContacts && parentActivity.checkSelfPermission("android.permission.READ_CONTACTS") != 0) {
                if (z) {
                    AlertDialog create = AlertsCreator.createContactsPermissionDialog(parentActivity, new DialogsActivity$$ExternalSyntheticLambda53(this)).create();
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
    public /* synthetic */ void lambda$askForPermissons$45(int i) {
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
        FilesMigrationService.FilesMigrationBottomSheet filesMigrationBottomSheet;
        boolean z = true;
        if (i == 1) {
            for (int i2 = 0; i2 < strArr.length; i2++) {
                if (iArr.length > i2) {
                    String str = strArr[i2];
                    str.hashCode();
                    if (!str.equals("android.permission.WRITE_EXTERNAL_STORAGE")) {
                        if (str.equals("android.permission.READ_CONTACTS")) {
                            if (iArr[i2] == 0) {
                                AndroidUtilities.runOnUIThread(new DialogsActivity$$ExternalSyntheticLambda40(this));
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
        } else if (i == 4) {
            int i3 = 0;
            while (true) {
                if (i3 >= iArr.length) {
                    break;
                } else if (iArr[i3] != 0) {
                    z = false;
                    break;
                } else {
                    i3++;
                }
            }
            if (z && Build.VERSION.SDK_INT >= 30 && (filesMigrationBottomSheet = FilesMigrationService.filesMigrationBottomSheet) != null) {
                filesMigrationBottomSheet.migrateOldFolder();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onRequestPermissionsResultFragment$46() {
        getNotificationCenter().postNotificationName(NotificationCenter.forceImportContactsStart, new Object[0]);
    }

    private void reloadViewPageDialogs(ViewPage viewPage, boolean z) {
        int i;
        int i2;
        if (viewPage.getVisibility() == 0) {
            int currentCount = viewPage.dialogsAdapter.getCurrentCount();
            if (viewPage.dialogsType == 0 && hasHiddenArchive() && viewPage.listView.getChildCount() == 0) {
                ((LinearLayoutManager) viewPage.listView.getLayoutManager()).scrollToPositionWithOffset(1, 0);
            }
            if (viewPage.dialogsAdapter.isDataSetChanged() || z) {
                viewPage.dialogsAdapter.updateHasHints();
                int itemCount = viewPage.dialogsAdapter.getItemCount();
                if (itemCount != 1 || currentCount != 1 || viewPage.dialogsAdapter.getItemViewType(0) != 5) {
                    viewPage.dialogsAdapter.notifyDataSetChanged();
                    if (!(itemCount <= currentCount || (i = this.initialDialogsType) == 11 || i == 12 || i == 13)) {
                        viewPage.recyclerItemsEnterAnimator.showItemsAnimated(currentCount);
                    }
                } else if (viewPage.dialogsAdapter.lastDialogsEmptyType != viewPage.dialogsAdapter.dialogsEmptyType()) {
                    viewPage.dialogsAdapter.notifyItemChanged(0);
                }
            } else {
                updateVisibleRows(MessagesController.UPDATE_MASK_NEW_MESSAGE);
                if (!(viewPage.dialogsAdapter.getItemCount() <= currentCount || (i2 = this.initialDialogsType) == 11 || i2 == 12 || i2 == 13)) {
                    viewPage.recyclerItemsEnterAnimator.showItemsAnimated(currentCount);
                }
            }
            try {
                viewPage.listView.setEmptyView(this.folderId == 0 ? viewPage.progressView : null);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            checkListLoad(viewPage);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        DialogsSearchAdapter dialogsSearchAdapter;
        DialogsSearchAdapter dialogsSearchAdapter2;
        MessagesController.DialogFilter dialogFilter;
        int i3 = 0;
        if (i == NotificationCenter.dialogsNeedReload) {
            if (this.viewPages != null && !this.dialogsListFrozen) {
                int i4 = 0;
                while (true) {
                    ViewPage[] viewPageArr = this.viewPages;
                    if (i4 >= viewPageArr.length) {
                        break;
                    }
                    ViewPage viewPage = viewPageArr[i4];
                    if (viewPageArr[0].dialogsType == 7 || this.viewPages[0].dialogsType == 8) {
                        dialogFilter = getMessagesController().selectedDialogFilter[this.viewPages[0].dialogsType == 8 ? (char) 1 : 0];
                    } else {
                        dialogFilter = null;
                    }
                    boolean z = (dialogFilter == null || (dialogFilter.flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ) == 0) ? false : true;
                    if (!this.slowedReloadAfterDialogClick || !z) {
                        reloadViewPageDialogs(viewPage, objArr.length > 0);
                    } else {
                        AndroidUtilities.runOnUIThread(new DialogsActivity$$ExternalSyntheticLambda48(this, viewPage, objArr), 160);
                    }
                    i4++;
                }
                FilterTabsView filterTabsView2 = this.filterTabsView;
                if (filterTabsView2 != null && filterTabsView2.getVisibility() == 0) {
                    this.filterTabsView.checkTabsCounter();
                }
                this.slowedReloadAfterDialogClick = false;
            }
        } else if (i == NotificationCenter.dialogsUnreadCounterChanged) {
            FilterTabsView filterTabsView3 = this.filterTabsView;
            if (filterTabsView3 != null && filterTabsView3.getVisibility() == 0) {
                FilterTabsView filterTabsView4 = this.filterTabsView;
                filterTabsView4.notifyTabCounterChanged(filterTabsView4.getDefaultTabId());
            }
        } else if (i == NotificationCenter.dialogsUnreadReactionsCounterChanged) {
            updateVisibleRows(0);
        } else if (i == NotificationCenter.emojiLoaded) {
            updateVisibleRows(0);
            FilterTabsView filterTabsView5 = this.filterTabsView;
            if (filterTabsView5 != null) {
                filterTabsView5.getTabsContainer().invalidateViews();
            }
        } else if (i == NotificationCenter.closeSearchByActiveAction) {
            ActionBar actionBar = this.actionBar;
            if (actionBar != null) {
                actionBar.closeSearchField();
            }
        } else if (i == NotificationCenter.proxySettingsChanged) {
            updateProxyButton(false, false);
        } else if (i == NotificationCenter.updateInterfaces) {
            Integer num = objArr[0];
            updateVisibleRows(num.intValue());
            FilterTabsView filterTabsView6 = this.filterTabsView;
            if (!(filterTabsView6 == null || filterTabsView6.getVisibility() != 0 || (num.intValue() & MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE) == 0)) {
                this.filterTabsView.checkTabsCounter();
            }
            if (this.viewPages != null) {
                while (i3 < this.viewPages.length) {
                    if ((num.intValue() & MessagesController.UPDATE_MASK_STATUS) != 0) {
                        this.viewPages[i3].dialogsAdapter.sortOnlineContacts(true);
                    }
                    i3++;
                }
            }
            updateStatus(UserConfig.getInstance(i2).getCurrentUser(), true);
        } else if (i == NotificationCenter.appDidLogout) {
            dialogsLoaded[this.currentAccount] = false;
        } else if (i == NotificationCenter.encryptedChatUpdated) {
            updateVisibleRows(0);
        } else if (i == NotificationCenter.contactsDidLoad) {
            if (this.viewPages != null && !this.dialogsListFrozen) {
                boolean z2 = this.floatingProgressVisible;
                setFloatingProgressVisible(false, true);
                for (ViewPage access$9100 : this.viewPages) {
                    access$9100.dialogsAdapter.setForceUpdatingContacts(false);
                }
                if (z2) {
                    setContactsAlpha(0.0f);
                    animateContactsAlpha(1.0f);
                }
                int i5 = 0;
                boolean z3 = false;
                while (true) {
                    ViewPage[] viewPageArr2 = this.viewPages;
                    if (i5 >= viewPageArr2.length) {
                        break;
                    }
                    if (!viewPageArr2[i5].isDefaultDialogType() || getMessagesController().getAllFoldersDialogsCount() > 10) {
                        z3 = true;
                    } else {
                        this.viewPages[i5].dialogsAdapter.notifyDataSetChanged();
                    }
                    i5++;
                }
                if (z3) {
                    updateVisibleRows(0);
                }
            }
        } else if (i == NotificationCenter.openedChatChanged) {
            if (this.viewPages != null) {
                int i6 = 0;
                while (true) {
                    ViewPage[] viewPageArr3 = this.viewPages;
                    if (i6 < viewPageArr3.length) {
                        if (viewPageArr3[i6].isDefaultDialogType() && AndroidUtilities.isTablet()) {
                            boolean booleanValue = objArr[1].booleanValue();
                            long longValue = objArr[0].longValue();
                            if (!booleanValue) {
                                this.openedDialogId = longValue;
                            } else if (longValue == this.openedDialogId) {
                                this.openedDialogId = 0;
                            }
                            this.viewPages[i6].dialogsAdapter.setOpenedDialogId(this.openedDialogId);
                        }
                        i6++;
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
                updateProxyButton(true, false);
            }
        } else if (i == NotificationCenter.onDownloadingFilesChanged) {
            updateProxyButton(true, false);
        } else if (i == NotificationCenter.needDeleteDialog) {
            if (this.fragmentView != null && !this.isPaused) {
                long longValue2 = objArr[0].longValue();
                DialogsActivity$$ExternalSyntheticLambda46 dialogsActivity$$ExternalSyntheticLambda46 = new DialogsActivity$$ExternalSyntheticLambda46(this, objArr[2], longValue2, objArr[3].booleanValue(), objArr[1]);
                if (this.undoView[0] != null) {
                    getUndoView().showWithAction(longValue2, 1, (Runnable) dialogsActivity$$ExternalSyntheticLambda46);
                } else {
                    dialogsActivity$$ExternalSyntheticLambda46.run();
                }
            }
        } else if (i == NotificationCenter.folderBecomeEmpty) {
            int intValue = objArr[0].intValue();
            int i7 = this.folderId;
            if (i7 == intValue && i7 != 0) {
                finishFragment();
            }
        } else if (i == NotificationCenter.dialogFiltersUpdated) {
            updateFilterTabs(true, true);
        } else if (i == NotificationCenter.filterSettingsUpdated) {
            showFiltersHint();
        } else if (i == NotificationCenter.newSuggestionsAvailable) {
            showNextSupportedSuggestion();
        } else if (i == NotificationCenter.forceImportContactsStart) {
            setFloatingProgressVisible(true, true);
            for (ViewPage viewPage2 : this.viewPages) {
                viewPage2.dialogsAdapter.setForceShowEmptyCell(false);
                viewPage2.dialogsAdapter.setForceUpdatingContacts(true);
                viewPage2.dialogsAdapter.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.messagesDeleted) {
            if (this.searchIsShowed && this.searchViewPager != null) {
                this.searchViewPager.messagesDeleted(objArr[1].longValue(), objArr[0]);
            }
        } else if (i == NotificationCenter.didClearDatabase) {
            if (this.viewPages != null) {
                while (true) {
                    ViewPage[] viewPageArr4 = this.viewPages;
                    if (i3 >= viewPageArr4.length) {
                        break;
                    }
                    viewPageArr4[i3].dialogsAdapter.didDatabaseCleared();
                    i3++;
                }
            }
            SuggestClearDatabaseBottomSheet.dismissDialog();
        } else if (i == NotificationCenter.appUpdateAvailable) {
            updateMenuButton(true);
        } else if (i == NotificationCenter.fileLoaded || i == NotificationCenter.fileLoadFailed || i == NotificationCenter.fileLoadProgressChanged) {
            String str = objArr[0];
            if (SharedConfig.isAppUpdateAvailable() && FileLoader.getAttachFileName(SharedConfig.pendingAppUpdate.document).equals(str)) {
                updateMenuButton(true);
            }
        } else if (i == NotificationCenter.onDatabaseMigration) {
            boolean booleanValue2 = objArr[0].booleanValue();
            if (this.fragmentView == null) {
                return;
            }
            if (booleanValue2) {
                if (this.databaseMigrationHint == null) {
                    DatabaseMigrationHint databaseMigrationHint2 = new DatabaseMigrationHint(this.fragmentView.getContext(), this.currentAccount);
                    this.databaseMigrationHint = databaseMigrationHint2;
                    databaseMigrationHint2.setAlpha(0.0f);
                    ((ContentView) this.fragmentView).addView(this.databaseMigrationHint);
                    this.databaseMigrationHint.animate().alpha(1.0f).setDuration(300).setStartDelay(1000).start();
                }
                this.databaseMigrationHint.setTag(1);
                return;
            }
            View view = this.databaseMigrationHint;
            if (view != null && view.getTag() != null) {
                final View view2 = this.databaseMigrationHint;
                view2.animate().setListener((Animator.AnimatorListener) null).cancel();
                view2.animate().setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (view2.getParent() != null) {
                            ((ViewGroup) view2.getParent()).removeView(view2);
                        }
                        DialogsActivity.this.databaseMigrationHint = null;
                    }
                }).alpha(0.0f).setStartDelay(0).setDuration(150).start();
                this.databaseMigrationHint.setTag((Object) null);
            }
        } else if (i == NotificationCenter.onDatabaseOpened) {
            checkSuggestClearDatabase();
        } else if (i == NotificationCenter.userEmojiStatusUpdated) {
            updateStatus(objArr[0], true);
        } else if (i == NotificationCenter.currentUserPremiumStatusChanged) {
            updateStatus(UserConfig.getInstance(i2).getCurrentUser(), true);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$47(ViewPage viewPage, Object[] objArr) {
        reloadViewPageDialogs(viewPage, objArr.length > 0);
        FilterTabsView filterTabsView2 = this.filterTabsView;
        if (filterTabsView2 != null && filterTabsView2.getVisibility() == 0) {
            this.filterTabsView.checkTabsCounter();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$48(TLRPC$Chat tLRPC$Chat, long j, boolean z, TLRPC$User tLRPC$User) {
        if (tLRPC$Chat == null) {
            getMessagesController().deleteDialog(j, 0, z);
            if (tLRPC$User != null && tLRPC$User.bot) {
                getMessagesController().blockPeer(tLRPC$User.id);
            }
        } else if (ChatObject.isNotInChat(tLRPC$Chat)) {
            getMessagesController().deleteDialog(j, 0, z);
        } else {
            getMessagesController().deleteParticipantFromChat(-j, getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId())), (TLRPC$Chat) null, z, z);
        }
        getMessagesController().checkIfFolderEmpty(this.folderId);
    }

    private void checkSuggestClearDatabase() {
        if (getMessagesStorage().showClearDatabaseAlert) {
            getMessagesStorage().showClearDatabaseAlert = false;
            SuggestClearDatabaseBottomSheet.show(this);
        }
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
        builder.setTitle(LocaleController.getString("HideNewChatsAlertTitle", R.string.HideNewChatsAlertTitle));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("HideNewChatsAlertText", R.string.HideNewChatsAlertText)));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(LocaleController.getString("GoToSettings", R.string.GoToSettings), new DialogsActivity$$ExternalSyntheticLambda7(this));
        showDialog(builder.create(), new DialogsActivity$$ExternalSyntheticLambda16(this));
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showSuggestion$49(DialogInterface dialogInterface, int i) {
        presentFragment(new PrivacySettingsActivity());
        AndroidUtilities.scrollToFragmentRow(this.parentLayout, "newChatsRow");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showSuggestion$50(DialogInterface dialogInterface) {
        onSuggestionDismiss();
    }

    private void showFiltersHint() {
        if (!this.askingForPermissions && getMessagesController().dialogFiltersLoaded && getMessagesController().showFiltersTooltip && this.filterTabsView != null && getMessagesController().dialogFilters.isEmpty() && !this.isPaused && getUserConfig().filtersLoaded && !this.inPreviewMode) {
            SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
            if (!globalMainSettings.getBoolean("filterhint", false)) {
                globalMainSettings.edit().putBoolean("filterhint", true).commit();
                AndroidUtilities.runOnUIThread(new DialogsActivity$$ExternalSyntheticLambda36(this), 1000);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showFiltersHint$51() {
        presentFragment(new FiltersSetupActivity());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showFiltersHint$52() {
        getUndoView().showWithAction(0, 15, (Runnable) null, new DialogsActivity$$ExternalSyntheticLambda38(this));
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

    public class DialogsHeader extends TLRPC$Dialog {
        public int headerType;

        public DialogsHeader(DialogsActivity dialogsActivity, int i) {
            this.headerType = i;
        }
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
            ArrayList<TLRPC$Dialog> arrayList2 = new ArrayList<>(messagesController.dialogsCanAddUsers.size() + messagesController.dialogsMyChannels.size() + messagesController.dialogsMyGroups.size() + 2);
            if (messagesController.dialogsMyChannels.size() > 0 && this.allowChannels) {
                arrayList2.add(new DialogsHeader(this, 0));
                arrayList2.addAll(messagesController.dialogsMyChannels);
            }
            if (messagesController.dialogsMyGroups.size() > 0 && this.allowGroups) {
                arrayList2.add(new DialogsHeader(this, 1));
                arrayList2.addAll(messagesController.dialogsMyGroups);
            }
            if (messagesController.dialogsCanAddUsers.size() > 0) {
                int size = messagesController.dialogsCanAddUsers.size();
                for (int i4 = 0; i4 < size; i4++) {
                    TLRPC$Dialog tLRPC$Dialog = messagesController.dialogsCanAddUsers.get(i4);
                    if ((this.allowChannels && ChatObject.isChannelAndNotMegaGroup(-tLRPC$Dialog.id, i)) || (this.allowGroups && (ChatObject.isMegagroup(i, -tLRPC$Dialog.id) || !ChatObject.isChannel(-tLRPC$Dialog.id, i)))) {
                        if (c != 0) {
                            arrayList2.add(new DialogsHeader(this, 2));
                            c = 0;
                        }
                        arrayList2.add(tLRPC$Dialog);
                    }
                }
            }
            return arrayList2;
        } else if (i2 == 3) {
            return messagesController.dialogsForward;
        } else {
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
                if (i2 != 14) {
                    return new ArrayList<>();
                }
                ArrayList<TLRPC$Dialog> arrayList3 = new ArrayList<>();
                if (this.allowUsers || this.allowBots) {
                    Iterator<TLRPC$Dialog> it = messagesController.dialogsUsersOnly.iterator();
                    while (it.hasNext()) {
                        TLRPC$Dialog next = it.next();
                        if (messagesController.getUser(Long.valueOf(next.id)).bot) {
                            if (!this.allowBots) {
                            }
                        } else if (!this.allowUsers) {
                        }
                        arrayList3.add(next);
                    }
                }
                if (this.allowGroups) {
                    arrayList3.addAll(messagesController.dialogsGroupsOnly);
                }
                if (this.allowChannels) {
                    arrayList3.addAll(messagesController.dialogsChannelsOnly);
                }
                return arrayList3;
            }
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

    private void setFloatingProgressVisible(final boolean z, boolean z2) {
        if (this.floatingButton != null && this.floatingProgressView != null) {
            float f = 0.0f;
            float f2 = 0.1f;
            if (!z2) {
                AnimatorSet animatorSet = this.floatingProgressAnimator;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                this.floatingProgressVisible = z;
                if (z) {
                    this.floatingButton.setAlpha(0.0f);
                    this.floatingButton.setScaleX(0.1f);
                    this.floatingButton.setScaleY(0.1f);
                    this.floatingButton.setVisibility(8);
                    this.floatingProgressView.setAlpha(1.0f);
                    this.floatingProgressView.setScaleX(1.0f);
                    this.floatingProgressView.setScaleY(1.0f);
                    this.floatingProgressView.setVisibility(0);
                    return;
                }
                this.floatingButton.setAlpha(1.0f);
                this.floatingButton.setScaleX(1.0f);
                this.floatingButton.setScaleY(1.0f);
                this.floatingButton.setVisibility(0);
                this.floatingProgressView.setAlpha(0.0f);
                this.floatingProgressView.setScaleX(0.1f);
                this.floatingProgressView.setScaleY(0.1f);
                this.floatingProgressView.setVisibility(8);
            } else if (z != this.floatingProgressVisible) {
                AnimatorSet animatorSet2 = this.floatingProgressAnimator;
                if (animatorSet2 != null) {
                    animatorSet2.cancel();
                }
                this.floatingProgressVisible = z;
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.floatingProgressAnimator = animatorSet3;
                Animator[] animatorArr = new Animator[6];
                RLottieImageView rLottieImageView = this.floatingButton;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = z ? 0.0f : 1.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(rLottieImageView, property, fArr);
                RLottieImageView rLottieImageView2 = this.floatingButton;
                Property property2 = View.SCALE_X;
                float[] fArr2 = new float[1];
                fArr2[0] = z ? 0.1f : 1.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(rLottieImageView2, property2, fArr2);
                RLottieImageView rLottieImageView3 = this.floatingButton;
                Property property3 = View.SCALE_Y;
                float[] fArr3 = new float[1];
                fArr3[0] = z ? 0.1f : 1.0f;
                animatorArr[2] = ObjectAnimator.ofFloat(rLottieImageView3, property3, fArr3);
                RadialProgressView radialProgressView = this.floatingProgressView;
                Property property4 = View.ALPHA;
                float[] fArr4 = new float[1];
                if (z) {
                    f = 1.0f;
                }
                fArr4[0] = f;
                animatorArr[3] = ObjectAnimator.ofFloat(radialProgressView, property4, fArr4);
                RadialProgressView radialProgressView2 = this.floatingProgressView;
                Property property5 = View.SCALE_X;
                float[] fArr5 = new float[1];
                fArr5[0] = z ? 1.0f : 0.1f;
                animatorArr[4] = ObjectAnimator.ofFloat(radialProgressView2, property5, fArr5);
                RadialProgressView radialProgressView3 = this.floatingProgressView;
                Property property6 = View.SCALE_Y;
                float[] fArr6 = new float[1];
                if (z) {
                    f2 = 1.0f;
                }
                fArr6[0] = f2;
                animatorArr[5] = ObjectAnimator.ofFloat(radialProgressView3, property6, fArr6);
                animatorSet3.playTogether(animatorArr);
                this.floatingProgressAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animator) {
                        DialogsActivity.this.floatingProgressView.setVisibility(0);
                        DialogsActivity.this.floatingButton.setVisibility(0);
                    }

                    public void onAnimationEnd(Animator animator) {
                        if (animator == DialogsActivity.this.floatingProgressAnimator) {
                            if (z) {
                                if (DialogsActivity.this.floatingButton != null) {
                                    DialogsActivity.this.floatingButton.setVisibility(8);
                                }
                            } else if (DialogsActivity.this.floatingButton != null) {
                                DialogsActivity.this.floatingProgressView.setVisibility(8);
                            }
                            AnimatorSet unused = DialogsActivity.this.floatingProgressAnimator = null;
                        }
                    }
                });
                this.floatingProgressAnimator.setDuration(150);
                this.floatingProgressAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.floatingProgressAnimator.start();
            }
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
            ofFloat.addUpdateListener(new DialogsActivity$$ExternalSyntheticLambda4(this));
            animatorSet.playTogether(new Animator[]{ofFloat});
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(this.floatingInterpolator);
            this.floatingButtonContainer.setClickable(!z);
            animatorSet.start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$hideFloatingButton$53(ValueAnimator valueAnimator) {
        this.floatingButtonHideProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.floatingButtonTranslation = ((float) AndroidUtilities.dp(100.0f)) * this.floatingButtonHideProgress;
        updateFloatingButtonOffset();
    }

    public float getContactsAlpha() {
        return this.contactsAlpha;
    }

    public void animateContactsAlpha(float f) {
        ValueAnimator valueAnimator = this.contactsAlphaAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator duration = ValueAnimator.ofFloat(new float[]{this.contactsAlpha, f}).setDuration(250);
        this.contactsAlphaAnimator = duration;
        duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.contactsAlphaAnimator.addUpdateListener(new DialogsActivity$$ExternalSyntheticLambda5(this));
        this.contactsAlphaAnimator.start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateContactsAlpha$54(ValueAnimator valueAnimator) {
        setContactsAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    public void setContactsAlpha(float f) {
        this.contactsAlpha = f;
        for (ViewPage viewPage : this.viewPages) {
            DialogsRecyclerView access$10400 = viewPage.listView;
            for (int i = 0; i < access$10400.getChildCount(); i++) {
                View childAt = access$10400.getChildAt(i);
                if (access$10400.getChildAdapterPosition(childAt) >= viewPage.dialogsAdapter.getDialogsCount() + 1) {
                    childAt.setAlpha(f);
                }
            }
        }
    }

    public void setScrollDisabled(boolean z) {
        for (ViewPage access$10400 : this.viewPages) {
            ((LinearLayoutManager) access$10400.listView.getLayoutManager()).setScrollDisabled(z);
        }
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

    private boolean checkCanWrite(long j) {
        if (this.addToGroupAlertString != null || !this.checkCanWrite) {
            return true;
        }
        if (DialogObject.isChatDialog(j)) {
            long j2 = -j;
            TLRPC$Chat chat = getMessagesController().getChat(Long.valueOf(j2));
            if (!ChatObject.isChannel(chat) || chat.megagroup) {
                return true;
            }
            if (!this.cantSendToChannels && ChatObject.isCanWriteToChannel(j2, this.currentAccount) && this.hasPoll != 2) {
                return true;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("SendMessageTitle", R.string.SendMessageTitle));
            if (this.hasPoll == 2) {
                builder.setMessage(LocaleController.getString("PublicPollCantForward", R.string.PublicPollCantForward));
            } else {
                builder.setMessage(LocaleController.getString("ChannelCantSendMessage", R.string.ChannelCantSendMessage));
            }
            builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
            return false;
        } else if (!DialogObject.isEncryptedDialog(j)) {
            return true;
        } else {
            if (this.hasPoll == 0 && !this.hasInvoice) {
                return true;
            }
            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
            builder2.setTitle(LocaleController.getString("SendMessageTitle", R.string.SendMessageTitle));
            if (this.hasPoll != 0) {
                builder2.setMessage(LocaleController.getString("PollCantForwardSecretChat", R.string.PollCantForwardSecretChat));
            } else {
                builder2.setMessage(LocaleController.getString("InvoiceCantForwardSecretChat", R.string.InvoiceCantForwardSecretChat));
            }
            builder2.setNegativeButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            showDialog(builder2.create());
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void didSelectResult(long j, boolean z, boolean z2) {
        TLRPC$Chat tLRPC$Chat;
        TLRPC$User tLRPC$User;
        String str;
        String str2;
        String str3;
        long j2 = j;
        if (checkCanWrite(j)) {
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
                            tLRPC$User = user;
                            tLRPC$Chat = null;
                        }
                    } else {
                        TLRPC$Chat chat = getMessagesController().getChat(Long.valueOf(-j2));
                        if (!ChatObject.hasAdminRights(chat) || !ChatObject.canChangeChatInfo(chat)) {
                            getUndoView().showWithAction(j2, 46, (Runnable) null);
                            return;
                        } else {
                            tLRPC$Chat = chat;
                            tLRPC$User = null;
                        }
                    }
                    AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
                    TLRPC$TL_messages_checkHistoryImportPeer tLRPC$TL_messages_checkHistoryImportPeer = new TLRPC$TL_messages_checkHistoryImportPeer();
                    tLRPC$TL_messages_checkHistoryImportPeer.peer = getMessagesController().getInputPeer(j2);
                    getConnectionsManager().sendRequest(tLRPC$TL_messages_checkHistoryImportPeer, new DialogsActivity$$ExternalSyntheticLambda54(this, alertDialog, tLRPC$User, tLRPC$Chat, j, z2, tLRPC$TL_messages_checkHistoryImportPeer));
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
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                if (DialogObject.isEncryptedDialog(j)) {
                    TLRPC$User user2 = getMessagesController().getUser(Long.valueOf(getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(j))).user_id));
                    if (user2 != null) {
                        str = LocaleController.getString("SendMessageTitle", R.string.SendMessageTitle);
                        str3 = LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(user2));
                        str2 = LocaleController.getString("Send", R.string.Send);
                    } else {
                        return;
                    }
                } else if (!DialogObject.isUserDialog(j)) {
                    TLRPC$Chat chat2 = getMessagesController().getChat(Long.valueOf(-j2));
                    if (chat2 != null) {
                        if (this.addToGroupAlertString != null) {
                            str = LocaleController.getString("AddToTheGroupAlertTitle", R.string.AddToTheGroupAlertTitle);
                            str3 = LocaleController.formatStringSimple(this.addToGroupAlertString, chat2.title);
                            str2 = LocaleController.getString("Add", R.string.Add);
                        } else {
                            str = LocaleController.getString("SendMessageTitle", R.string.SendMessageTitle);
                            str3 = LocaleController.formatStringSimple(this.selectAlertStringGroup, chat2.title);
                            str2 = LocaleController.getString("Send", R.string.Send);
                        }
                    } else {
                        return;
                    }
                } else if (j2 == getUserConfig().getClientUserId()) {
                    str = LocaleController.getString("SendMessageTitle", R.string.SendMessageTitle);
                    str3 = LocaleController.formatStringSimple(this.selectAlertStringGroup, LocaleController.getString("SavedMessages", R.string.SavedMessages));
                    str2 = LocaleController.getString("Send", R.string.Send);
                } else {
                    TLRPC$User user3 = getMessagesController().getUser(Long.valueOf(j));
                    if (user3 != null && this.selectAlertString != null) {
                        str = LocaleController.getString("SendMessageTitle", R.string.SendMessageTitle);
                        str3 = LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(user3));
                        str2 = LocaleController.getString("Send", R.string.Send);
                    } else {
                        return;
                    }
                }
                builder.setTitle(str);
                builder.setMessage(AndroidUtilities.replaceTags(str3));
                builder.setPositiveButton(str2, new DialogsActivity$$ExternalSyntheticLambda12(this, j2));
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                showDialog(builder.create());
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didSelectResult$57(AlertDialog alertDialog, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, long j, boolean z, TLRPC$TL_messages_checkHistoryImportPeer tLRPC$TL_messages_checkHistoryImportPeer, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new DialogsActivity$$ExternalSyntheticLambda47(this, alertDialog, tLObject, tLRPC$User, tLRPC$Chat, j, z, tLRPC$TL_error, tLRPC$TL_messages_checkHistoryImportPeer));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didSelectResult$56(AlertDialog alertDialog, TLObject tLObject, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, long j, boolean z, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_messages_checkHistoryImportPeer tLRPC$TL_messages_checkHistoryImportPeer) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        this.checkingImportDialog = false;
        if (tLObject != null) {
            AlertsCreator.createImportDialogAlert(this, this.arguments.getString("importTitle"), ((TLRPC$TL_messages_checkedHistoryImportPeer) tLObject).confirm_text, tLRPC$User, tLRPC$Chat, new DialogsActivity$$ExternalSyntheticLambda43(this, j, z));
            return;
        }
        AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, this, tLRPC$TL_messages_checkHistoryImportPeer, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(j), tLRPC$TL_messages_checkHistoryImportPeer, tLRPC$TL_error);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didSelectResult$55(long j, boolean z) {
        setDialogsListFrozen(true);
        ArrayList arrayList = new ArrayList();
        arrayList.add(Long.valueOf(j));
        this.delegate.didSelectDialogs(this, arrayList, (CharSequence) null, z);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didSelectResult$58(long j, DialogInterface dialogInterface, int i) {
        didSelectResult(j, false, false);
    }

    public RLottieImageView getFloatingButton() {
        return this.floatingButton;
    }

    private boolean onSendLongClick(View view) {
        Activity parentActivity = getParentActivity();
        Theme.ResourcesProvider resourceProvider = getResourceProvider();
        if (parentActivity == null) {
            return false;
        }
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(parentActivity, resourceProvider);
        actionBarPopupWindowLayout.setAnimationEnabled(false);
        actionBarPopupWindowLayout.setOnTouchListener(new View.OnTouchListener() {
            private Rect popupRect = new Rect();

            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() != 0 || DialogsActivity.this.sendPopupWindow == null || !DialogsActivity.this.sendPopupWindow.isShowing()) {
                    return false;
                }
                view.getHitRect(this.popupRect);
                if (this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                    return false;
                }
                DialogsActivity.this.sendPopupWindow.dismiss();
                return false;
            }
        });
        actionBarPopupWindowLayout.setDispatchKeyEventListener(new DialogsActivity$$ExternalSyntheticLambda55(this));
        actionBarPopupWindowLayout.setShownFromBottom(false);
        actionBarPopupWindowLayout.setupRadialSelectors(getThemedColor("dialogButtonSelector"));
        ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem((Context) parentActivity, true, true, resourceProvider);
        actionBarMenuSubItem.setTextAndIcon(LocaleController.getString("SendWithoutSound", R.string.SendWithoutSound), R.drawable.input_notify_off);
        actionBarMenuSubItem.setMinimumWidth(AndroidUtilities.dp(196.0f));
        actionBarPopupWindowLayout.addView(actionBarMenuSubItem, LayoutHelper.createLinear(-1, 48));
        actionBarMenuSubItem.setOnClickListener(new DialogsActivity$$ExternalSyntheticLambda21(this));
        linearLayout.addView(actionBarPopupWindowLayout, LayoutHelper.createLinear(-1, -2));
        ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(linearLayout, -2, -2);
        this.sendPopupWindow = actionBarPopupWindow;
        actionBarPopupWindow.setAnimationEnabled(false);
        this.sendPopupWindow.setAnimationStyle(R.style.PopupContextAnimation2);
        this.sendPopupWindow.setOutsideTouchable(true);
        this.sendPopupWindow.setClippingEnabled(true);
        this.sendPopupWindow.setInputMethodMode(2);
        this.sendPopupWindow.setSoftInputMode(0);
        this.sendPopupWindow.getContentView().setFocusableInTouchMode(true);
        SharedConfig.removeScheduledOrNoSoundHint();
        linearLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        this.sendPopupWindow.setFocusable(true);
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        this.sendPopupWindow.showAtLocation(view, 51, ((iArr[0] + view.getMeasuredWidth()) - linearLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), (iArr[1] - linearLayout.getMeasuredHeight()) - AndroidUtilities.dp(2.0f));
        this.sendPopupWindow.dimBehind();
        view.performHapticFeedback(3, 2);
        return false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onSendLongClick$59(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onSendLongClick$60(View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        this.notify = false;
        if (this.delegate != null && !this.selectedDialogs.isEmpty()) {
            this.delegate.didSelectDialogs(this, this.selectedDialogs, this.commentView.getFieldText(), false);
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        RecyclerListView recyclerListView;
        DialogsActivity$$ExternalSyntheticLambda56 dialogsActivity$$ExternalSyntheticLambda56 = new DialogsActivity$$ExternalSyntheticLambda56(this);
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
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda56, "actionBarDefaultIcon"));
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
        DialogsActivity$$ExternalSyntheticLambda56 dialogsActivity$$ExternalSyntheticLambda562 = dialogsActivity$$ExternalSyntheticLambda56;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda562, "actionBarDefaultSubmenuBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda562, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda562, "actionBarDefaultSubmenuItemIcon"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda562, "dialogButtonSelector"));
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
        DialogsActivity$$ExternalSyntheticLambda56 dialogsActivity$$ExternalSyntheticLambda563 = dialogsActivity$$ExternalSyntheticLambda56;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda563, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda563, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda563, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda563, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda563, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda563, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda563, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda563, "avatar_backgroundSaved"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda563, "avatar_backgroundArchived"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda563, "avatar_backgroundArchivedHidden"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda563, "chats_nameMessage"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda563, "chats_draft"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda563, "chats_attachMessage"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda563, "chats_nameArchived"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda563, "chats_nameMessageArchived"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda563, "chats_nameMessageArchived_threeLines"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda563, "chats_messageArchived"));
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
                DialogsActivity$$ExternalSyntheticLambda56 dialogsActivity$$ExternalSyntheticLambda564 = dialogsActivity$$ExternalSyntheticLambda56;
                int i3 = i2;
                ThemeDescription themeDescription2 = new ThemeDescription((View) this.viewPages[i2].listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) dialogsActivity$$ExternalSyntheticLambda564, "windowBackgroundWhiteGrayText");
                arrayList.add(themeDescription);
                arrayList.add(new ThemeDescription((View) this.viewPages[i3].listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) dialogsActivity$$ExternalSyntheticLambda564, "windowBackgroundWhiteBlueText"));
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
        DialogsActivity$$ExternalSyntheticLambda56 dialogsActivity$$ExternalSyntheticLambda565 = dialogsActivity$$ExternalSyntheticLambda56;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda565, "chats_archivePullDownBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda565, "chats_archivePullDownBackgroundActive"));
        arrayList.add(new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuBackground"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuName"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuPhone"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuPhoneCats"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuCloudBackgroundCats"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_serviceBackground"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuTopShadow"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuTopShadowCats"));
        arrayList.add(new ThemeDescription((View) this.sideMenu, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{DrawerProfileCell.class}, new String[]{"darkThemeView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuName"));
        arrayList.add(new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda565, "chats_menuTopBackgroundCats"));
        arrayList.add(new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda565, "chats_menuTopBackground"));
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
        }
        DialogsActivity$$ExternalSyntheticLambda56 dialogsActivity$$ExternalSyntheticLambda566 = dialogsActivity$$ExternalSyntheticLambda56;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda566, "actionBarTipBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda566, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda566, "player_time"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda566, "chat_messagePanelCursor"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda566, "avatar_actionBarIconBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, dialogsActivity$$ExternalSyntheticLambda566, "groupcreate_spanBackground"));
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
            searchViewPager3.getThemeDescriptions(arrayList);
        }
        return arrayList;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$61() {
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
        if (this.statusDrawable != null) {
            updateStatus(UserConfig.getInstance(this.currentAccount).getCurrentUser(), false);
        }
        if (this.scrimPopupWindowItems != null) {
            while (true) {
                ActionBarMenuSubItem[] actionBarMenuSubItemArr = this.scrimPopupWindowItems;
                if (i >= actionBarMenuSubItemArr.length) {
                    break;
                }
                if (actionBarMenuSubItemArr[i] != null) {
                    actionBarMenuSubItemArr[i].setColors(Theme.getColor("actionBarDefaultSubmenuItem"), Theme.getColor("actionBarDefaultSubmenuItemIcon"));
                    this.scrimPopupWindowItems[i].setSelectorColor(Theme.getColor("dialogButtonSelector"));
                }
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
        View view = this.blurredView;
        if (view != null && Build.VERSION.SDK_INT >= 23) {
            view.setForeground(new ColorDrawable(ColorUtils.setAlphaComponent(getThemedColor("windowBackgroundWhite"), 100)));
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
        updateFloatingButtonColor();
        setSearchAnimationProgress(this.searchAnimationProgress);
    }

    private void updateFloatingButtonColor() {
        if (getParentActivity() != null && this.floatingButtonContainer != null) {
            Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
            if (Build.VERSION.SDK_INT < 21) {
                Drawable mutate = ContextCompat.getDrawable(getParentActivity(), R.drawable.floating_shadow).mutate();
                mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                createSimpleSelectorCircleDrawable = combinedDrawable;
            }
            this.floatingButtonContainer.setBackground(createSimpleSelectorCircleDrawable);
        }
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
    public /* synthetic */ void lambda$getCustomSlideTransition$62(ValueAnimator valueAnimator) {
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
        int positionForType = this.searchViewPager.getPositionForType(i);
        if (positionForType >= 0 && this.searchViewPager.getTabsView().getCurrentTabId() != positionForType) {
            this.searchViewPager.getTabsView().scrollToTab(positionForType, positionForType);
        }
    }

    public boolean isLightStatusBar() {
        int color = Theme.getColor((!this.searching || !this.whiteActionBar) ? this.folderId == 0 ? "actionBarDefault" : "actionBarDefaultArchived" : "windowBackgroundWhite");
        if (this.actionBar.isActionModeShowed()) {
            color = Theme.getColor("actionBarActionModeDefault");
        }
        return ColorUtils.calculateLuminance(color) > 0.699999988079071d;
    }
}
