package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Property;
import android.util.StateSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScrollerCustom;
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
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.XiaomiUtilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
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
import org.telegram.ui.Cells.AccountSelectCell;
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
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
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
import org.telegram.ui.Components.JoinGroupAlert;
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
import org.telegram.ui.Components.RecyclerAnimationScrollHelper;
import org.telegram.ui.Components.RecyclerItemsEnterAnimator;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SearchViewPager;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.ViewPagerFixed;
import org.telegram.ui.GroupCreateFinalActivity;

public class DialogsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int ARCHIVE_ITEM_STATE_HIDDEN = 2;
    private static final int ARCHIVE_ITEM_STATE_PINNED = 0;
    private static final int ARCHIVE_ITEM_STATE_SHOWED = 1;
    public static final int DIALOGS_TYPE_START_ATTACH_BOT = 14;
    private static final int add_to_folder = 109;
    private static final int archive = 105;
    private static final int archive2 = 107;
    private static final int block = 106;
    private static final int clear = 103;
    private static final int delete = 102;
    public static boolean[] dialogsLoaded = new boolean[4];
    /* access modifiers changed from: private */
    public static final Interpolator interpolator = DialogsActivity$$ExternalSyntheticLambda21.INSTANCE;
    private static final int mute = 104;
    private static final int pin = 100;
    private static final int pin2 = 108;
    private static final int read = 101;
    private static final int remove_from_folder = 110;
    public static float viewOffset = 0.0f;
    private final String ACTION_MODE_SEARCH_DIALOGS_TAG = "search_dialogs_action_mode";
    public final Property<DialogsActivity, Float> SCROLL_Y = new AnimationProperties.FloatProperty<DialogsActivity>("animationValue") {
        public void setValue(DialogsActivity object, float value) {
            object.setScrollY(value);
        }

        public Float get(DialogsActivity object) {
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
    private final boolean commentViewAnimated = false;
    private AnimatorSet commentViewAnimator;
    /* access modifiers changed from: private */
    public View commentViewBg;
    private boolean commentViewIgnoreTopUpdate = false;
    private int commentViewPreviousTop = -1;
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
    public ArrayList<TLRPC.Dialog> frozenDialogsList;
    private boolean hasInvoice;
    private int hasPoll;
    /* access modifiers changed from: private */
    public int initialDialogsType;
    private String initialSearchString;
    private int initialSearchType = -1;
    boolean isDrawerTransition;
    private boolean isNextButton = false;
    boolean isSlideBackTransition;
    private ValueAnimator keyboardAnimator;
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
    private int otherwiseReloginDays;
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
    public View selectedCountView;
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

        static /* synthetic */ int access$11208(ViewPage x0) {
            int i = x0.lastItemsCount;
            x0.lastItemsCount = i + 1;
            return i;
        }

        static /* synthetic */ int access$11210(ViewPage x0) {
            int i = x0.lastItemsCount;
            x0.lastItemsCount = i - 1;
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

    static /* synthetic */ float lambda$static$0(float t) {
        float t2 = t - 1.0f;
        return (t2 * t2 * t2 * t2 * t2) + 1.0f;
    }

    private class ContentView extends SizeNotifierFrameLayout {
        private Paint actionBarSearchPaint = new Paint(1);
        private boolean globalIgnoreLayout;
        private int inputFieldHeight;
        private int[] pos = new int[2];
        private int startedTrackingPointerId;
        private int startedTrackingX;
        private int startedTrackingY;
        private VelocityTracker velocityTracker;
        private Paint windowBackgroundPaint = new Paint();

        public ContentView(Context context) {
            super(context);
            this.needBlur = true;
            this.blurBehindViews.add(this);
        }

        private boolean prepareForMoving(MotionEvent ev, boolean forward) {
            int id = DialogsActivity.this.filterTabsView.getNextPageId(forward);
            if (id < 0) {
                return false;
            }
            getParent().requestDisallowInterceptTouchEvent(true);
            boolean unused = DialogsActivity.this.maybeStartTracking = false;
            boolean unused2 = DialogsActivity.this.startedTracking = true;
            this.startedTrackingX = (int) (ev.getX() + DialogsActivity.this.additionalOffset);
            DialogsActivity.this.actionBar.setEnabled(false);
            DialogsActivity.this.filterTabsView.setEnabled(false);
            int unused3 = DialogsActivity.this.viewPages[1].selectedType = id;
            DialogsActivity.this.viewPages[1].setVisibility(0);
            boolean unused4 = DialogsActivity.this.animatingForward = forward;
            DialogsActivity.this.showScrollbars(false);
            DialogsActivity.this.switchToCurrentSelectedMode(true);
            if (forward) {
                DialogsActivity.this.viewPages[1].setTranslationX((float) DialogsActivity.this.viewPages[0].getMeasuredWidth());
            } else {
                DialogsActivity.this.viewPages[1].setTranslationX((float) (-DialogsActivity.this.viewPages[0].getMeasuredWidth()));
            }
            return true;
        }

        public void setPadding(int left, int top, int right, int bottom) {
            int unused = DialogsActivity.this.topPadding = top;
            DialogsActivity.this.updateContextViewPosition();
            if (!DialogsActivity.this.whiteActionBar || DialogsActivity.this.searchViewPager == null) {
                requestLayout();
            } else {
                DialogsActivity.this.searchViewPager.setTranslationY((float) (DialogsActivity.this.topPadding - DialogsActivity.this.lastMeasuredTopPadding));
            }
        }

        public boolean checkTabsAnimationInProgress() {
            if (!DialogsActivity.this.tabsAnimationInProgress) {
                return false;
            }
            boolean cancel = false;
            int i = -1;
            if (DialogsActivity.this.backAnimation) {
                if (Math.abs(DialogsActivity.this.viewPages[0].getTranslationX()) < 1.0f) {
                    DialogsActivity.this.viewPages[0].setTranslationX(0.0f);
                    ViewPage viewPage = DialogsActivity.this.viewPages[1];
                    int measuredWidth = DialogsActivity.this.viewPages[0].getMeasuredWidth();
                    if (DialogsActivity.this.animatingForward) {
                        i = 1;
                    }
                    viewPage.setTranslationX((float) (measuredWidth * i));
                    cancel = true;
                }
            } else if (Math.abs(DialogsActivity.this.viewPages[1].getTranslationX()) < 1.0f) {
                ViewPage viewPage2 = DialogsActivity.this.viewPages[0];
                int measuredWidth2 = DialogsActivity.this.viewPages[0].getMeasuredWidth();
                if (!DialogsActivity.this.animatingForward) {
                    i = 1;
                }
                viewPage2.setTranslationX((float) (measuredWidth2 * i));
                DialogsActivity.this.viewPages[1].setTranslationX(0.0f);
                cancel = true;
            }
            if (cancel) {
                DialogsActivity.this.showScrollbars(true);
                if (DialogsActivity.this.tabsAnimation != null) {
                    DialogsActivity.this.tabsAnimation.cancel();
                    AnimatorSet unused = DialogsActivity.this.tabsAnimation = null;
                }
                boolean unused2 = DialogsActivity.this.tabsAnimationInProgress = false;
            }
            return DialogsActivity.this.tabsAnimationInProgress;
        }

        public int getActionBarFullHeight() {
            float h = (float) DialogsActivity.this.actionBar.getHeight();
            float filtersTabsHeight = 0.0f;
            if (!(DialogsActivity.this.filterTabsView == null || DialogsActivity.this.filterTabsView.getVisibility() == 8)) {
                filtersTabsHeight = ((float) DialogsActivity.this.filterTabsView.getMeasuredHeight()) - ((1.0f - DialogsActivity.this.filterTabsProgress) * ((float) DialogsActivity.this.filterTabsView.getMeasuredHeight()));
            }
            float searchTabsHeight = 0.0f;
            if (!(DialogsActivity.this.searchTabsView == null || DialogsActivity.this.searchTabsView.getVisibility() == 8)) {
                searchTabsHeight = (float) DialogsActivity.this.searchTabsView.getMeasuredHeight();
            }
            return (int) (h + ((1.0f - DialogsActivity.this.searchAnimationProgress) * filtersTabsHeight) + (DialogsActivity.this.searchAnimationProgress * searchTabsHeight));
        }

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View child, long drawingTime) {
            boolean result;
            if ((child == DialogsActivity.this.fragmentContextView && DialogsActivity.this.fragmentContextView.isCallStyle()) || child == DialogsActivity.this.blurredView) {
                return true;
            }
            int i = 0;
            if (child == DialogsActivity.this.viewPages[0] || ((DialogsActivity.this.viewPages.length > 1 && child == DialogsActivity.this.viewPages[1]) || child == DialogsActivity.this.fragmentContextView || child == DialogsActivity.this.fragmentLocationContextView || child == DialogsActivity.this.searchViewPager)) {
                canvas.save();
                canvas.clipRect(0.0f, (-getY()) + DialogsActivity.this.actionBar.getY() + ((float) getActionBarFullHeight()), (float) getMeasuredWidth(), (float) getMeasuredHeight());
                if (DialogsActivity.this.slideFragmentProgress != 1.0f) {
                    float s = 1.0f - ((1.0f - DialogsActivity.this.slideFragmentProgress) * 0.05f);
                    canvas.translate(((float) (DialogsActivity.this.isDrawerTransition ? AndroidUtilities.dp(4.0f) : -AndroidUtilities.dp(4.0f))) * (1.0f - DialogsActivity.this.slideFragmentProgress), 0.0f);
                    canvas.scale(s, s, DialogsActivity.this.isDrawerTransition ? (float) getMeasuredWidth() : 0.0f, (-getY()) + DialogsActivity.this.actionBar.getY() + ((float) getActionBarFullHeight()));
                }
                result = super.drawChild(canvas, child, drawingTime);
                canvas.restore();
            } else if (child != DialogsActivity.this.actionBar || DialogsActivity.this.slideFragmentProgress == 1.0f) {
                result = super.drawChild(canvas, child, drawingTime);
            } else {
                canvas.save();
                float s2 = 1.0f - ((1.0f - DialogsActivity.this.slideFragmentProgress) * 0.05f);
                canvas.translate(((float) (DialogsActivity.this.isDrawerTransition ? AndroidUtilities.dp(4.0f) : -AndroidUtilities.dp(4.0f))) * (1.0f - DialogsActivity.this.slideFragmentProgress), 0.0f);
                float measuredWidth = DialogsActivity.this.isDrawerTransition ? (float) getMeasuredWidth() : 0.0f;
                if (DialogsActivity.this.actionBar.getOccupyStatusBar()) {
                    i = AndroidUtilities.statusBarHeight;
                }
                canvas.scale(s2, s2, measuredWidth, ((float) i) + (((float) ActionBar.getCurrentActionBarHeight()) / 2.0f));
                result = super.drawChild(canvas, child, drawingTime);
                canvas.restore();
            }
            if (child == DialogsActivity.this.actionBar && DialogsActivity.this.parentLayout != null) {
                int y = (int) (DialogsActivity.this.actionBar.getY() + ((float) getActionBarFullHeight()));
                DialogsActivity.this.parentLayout.drawHeaderShadow(canvas, (int) ((1.0f - DialogsActivity.this.searchAnimationProgress) * 255.0f), y);
                if (DialogsActivity.this.searchAnimationProgress > 0.0f) {
                    if (DialogsActivity.this.searchAnimationProgress < 1.0f) {
                        int a = Theme.dividerPaint.getAlpha();
                        Theme.dividerPaint.setAlpha((int) (((float) a) * DialogsActivity.this.searchAnimationProgress));
                        canvas.drawLine(0.0f, (float) y, (float) getMeasuredWidth(), (float) y, Theme.dividerPaint);
                        Theme.dividerPaint.setAlpha(a);
                    } else {
                        canvas.drawLine(0.0f, (float) y, (float) getMeasuredWidth(), (float) y, Theme.dividerPaint);
                    }
                }
            }
            return result;
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            int top;
            float alpha;
            Canvas canvas2 = canvas;
            int actionBarHeight = getActionBarFullHeight();
            if (DialogsActivity.this.inPreviewMode) {
                top = AndroidUtilities.statusBarHeight;
            } else {
                top = (int) ((-getY()) + DialogsActivity.this.actionBar.getY());
            }
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
                AndroidUtilities.rectTmp2.set(0, top, getMeasuredWidth(), top + actionBarHeight);
                drawBlurRect(canvas, 0.0f, AndroidUtilities.rectTmp2, DialogsActivity.this.searchAnimationProgress == 1.0f ? this.actionBarSearchPaint : DialogsActivity.this.actionBarDefaultPaint, true);
                if (DialogsActivity.this.searchAnimationProgress > 0.0f && DialogsActivity.this.searchAnimationProgress < 1.0f) {
                    Paint paint = this.actionBarSearchPaint;
                    if (DialogsActivity.this.folderId != 0) {
                        str = "actionBarDefaultArchived";
                    }
                    paint.setColor(ColorUtils.blendARGB(Theme.getColor(str), Theme.getColor("windowBackgroundWhite"), DialogsActivity.this.searchAnimationProgress));
                    if (DialogsActivity.this.searchIsShowed || !DialogsActivity.this.searchWasFullyShowed) {
                        canvas.save();
                        canvas2.clipRect(0, top, getMeasuredWidth(), top + actionBarHeight);
                        float cX = (float) (getMeasuredWidth() - AndroidUtilities.dp(24.0f));
                        int statusBarH = DialogsActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0;
                        int i = statusBarH;
                        float f = cX;
                        drawBlurCircle(canvas, 0.0f, cX, ((float) statusBarH) + (((float) (DialogsActivity.this.actionBar.getMeasuredHeight() - statusBarH)) / 2.0f), ((float) getMeasuredWidth()) * 1.3f * DialogsActivity.this.searchAnimationProgress, this.actionBarSearchPaint, true);
                        canvas.restore();
                    } else {
                        AndroidUtilities.rectTmp2.set(0, top, getMeasuredWidth(), top + actionBarHeight);
                        drawBlurRect(canvas, 0.0f, AndroidUtilities.rectTmp2, this.actionBarSearchPaint, true);
                    }
                    if (DialogsActivity.this.filterTabsView != null && DialogsActivity.this.filterTabsView.getVisibility() == 0) {
                        DialogsActivity.this.filterTabsView.setTranslationY((float) (actionBarHeight - (DialogsActivity.this.actionBar.getHeight() + DialogsActivity.this.filterTabsView.getMeasuredHeight())));
                    }
                    if (DialogsActivity.this.searchTabsView != null) {
                        float y = (float) (actionBarHeight - (DialogsActivity.this.actionBar.getHeight() + DialogsActivity.this.searchTabsView.getMeasuredHeight()));
                        if (DialogsActivity.this.searchAnimationTabsDelayedCrossfade) {
                            alpha = DialogsActivity.this.searchAnimationProgress < 0.5f ? 0.0f : (DialogsActivity.this.searchAnimationProgress - 0.5f) / 0.5f;
                        } else {
                            alpha = DialogsActivity.this.searchAnimationProgress;
                        }
                        DialogsActivity.this.searchTabsView.setTranslationY(y);
                        DialogsActivity.this.searchTabsView.setAlpha(alpha);
                        if (DialogsActivity.this.filtersView != null) {
                            DialogsActivity.this.filtersView.setTranslationY(y);
                            DialogsActivity.this.filtersView.setAlpha(alpha);
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
                    AndroidUtilities.rectTmp2.set(0, top, getMeasuredWidth(), top + actionBarHeight);
                    drawBlurRect(canvas, 0.0f, AndroidUtilities.rectTmp2, this.actionBarSearchPaint, true);
                } else {
                    AndroidUtilities.rectTmp2.set(0, top, getMeasuredWidth(), top + actionBarHeight);
                    drawBlurRect(canvas, 0.0f, AndroidUtilities.rectTmp2, DialogsActivity.this.actionBarDefaultPaint, true);
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
                canvas.drawRect(0.0f, (float) (top + actionBarHeight), (float) getMeasuredWidth(), (float) (DialogsActivity.this.actionBar.getMeasuredHeight() + top + DialogsActivity.this.searchTabsView.getMeasuredHeight()), this.windowBackgroundPaint);
            }
            if (DialogsActivity.this.fragmentContextView != null && DialogsActivity.this.fragmentContextView.isCallStyle()) {
                canvas.save();
                canvas2.translate(DialogsActivity.this.fragmentContextView.getX(), DialogsActivity.this.fragmentContextView.getY());
                if (DialogsActivity.this.slideFragmentProgress != 1.0f) {
                    float s = 1.0f - ((1.0f - DialogsActivity.this.slideFragmentProgress) * 0.05f);
                    canvas2.translate(((float) (DialogsActivity.this.isDrawerTransition ? AndroidUtilities.dp(4.0f) : -AndroidUtilities.dp(4.0f))) * (1.0f - DialogsActivity.this.slideFragmentProgress), 0.0f);
                    canvas2.scale(s, 1.0f, DialogsActivity.this.isDrawerTransition ? (float) getMeasuredWidth() : 0.0f, DialogsActivity.this.fragmentContextView.getY());
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
                    Rect selectorBounds = selectorDrawable.getBounds();
                    canvas2.translate((float) (-selectorBounds.left), (float) (-selectorBounds.top));
                    selectorDrawable.draw(canvas2);
                    canvas.restore();
                }
                DialogsActivity.this.scrimView.draw(canvas2);
                if (DialogsActivity.this.scrimViewSelected) {
                    Drawable drawable = DialogsActivity.this.filterTabsView.getSelectorDrawable();
                    canvas2.translate((float) (-DialogsActivity.this.scrimViewLocation[0]), (float) ((-drawable.getIntrinsicHeight()) - 1));
                    drawable.draw(canvas2);
                }
                canvas.restore();
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int h;
            int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
            setMeasuredDimension(widthSize, heightSize);
            int heightSize2 = heightSize - getPaddingTop();
            if (DialogsActivity.this.doneItem != null) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) DialogsActivity.this.doneItem.getLayoutParams();
                layoutParams.topMargin = DialogsActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0;
                layoutParams.height = ActionBar.getCurrentActionBarHeight();
            }
            measureChildWithMargins(DialogsActivity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
            int keyboardSize = measureKeyboardHeight();
            int childCount = getChildCount();
            float f = 0.0f;
            if (DialogsActivity.this.commentView != null) {
                measureChildWithMargins(DialogsActivity.this.commentView, widthMeasureSpec, 0, heightMeasureSpec, 0);
                Object tag = DialogsActivity.this.commentView.getTag();
                if (tag == null || !tag.equals(2)) {
                    this.inputFieldHeight = 0;
                } else {
                    if (keyboardSize <= AndroidUtilities.dp(20.0f) && !AndroidUtilities.isInMultiwindow) {
                        heightSize2 -= DialogsActivity.this.commentView.getEmojiPadding();
                    }
                    this.inputFieldHeight = DialogsActivity.this.commentView.getMeasuredHeight();
                }
                if (SharedConfig.smoothKeyboard && DialogsActivity.this.commentView.isPopupShowing()) {
                    DialogsActivity.this.fragmentView.setTranslationY(0.0f);
                    for (int a = 0; a < DialogsActivity.this.viewPages.length; a++) {
                        if (DialogsActivity.this.viewPages[a] != null) {
                            DialogsActivity.this.viewPages[a].setTranslationY(0.0f);
                        }
                    }
                    if (!DialogsActivity.this.onlySelect) {
                        DialogsActivity.this.actionBar.setTranslationY(0.0f);
                    }
                    DialogsActivity.this.searchViewPager.setTranslationY(0.0f);
                }
            }
            int i = 0;
            while (i < childCount) {
                View child = getChildAt(i);
                if (child != null && child.getVisibility() != 8 && child != DialogsActivity.this.commentView) {
                    if (child != DialogsActivity.this.actionBar) {
                        if (child instanceof DatabaseMigrationHint) {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, NUM), View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), (((View.MeasureSpec.getSize(heightMeasureSpec) + keyboardSize) - this.inputFieldHeight) + AndroidUtilities.dp(2.0f)) - DialogsActivity.this.actionBar.getMeasuredHeight()), NUM));
                        } else if (child instanceof ViewPage) {
                            int contentWidthSpec = View.MeasureSpec.makeMeasureSpec(widthSize, NUM);
                            if (DialogsActivity.this.filterTabsView == null || DialogsActivity.this.filterTabsView.getVisibility() != 0) {
                                h = (((heightSize2 - this.inputFieldHeight) + AndroidUtilities.dp(2.0f)) - (DialogsActivity.this.onlySelect ? 0 : DialogsActivity.this.actionBar.getMeasuredHeight())) - DialogsActivity.this.topPadding;
                            } else {
                                h = (((heightSize2 - this.inputFieldHeight) + AndroidUtilities.dp(2.0f)) - AndroidUtilities.dp(44.0f)) - DialogsActivity.this.topPadding;
                            }
                            if (DialogsActivity.this.filtersTabAnimator == null || DialogsActivity.this.filterTabsView == null || DialogsActivity.this.filterTabsView.getVisibility() != 0) {
                                child.setTranslationY(f);
                            } else {
                                h = (int) (((float) h) + DialogsActivity.this.filterTabsMoveFrom);
                            }
                            int transitionPadding = (DialogsActivity.this.isSlideBackTransition || DialogsActivity.this.isDrawerTransition) ? (int) (((float) h) * 0.05f) : 0;
                            child.setPadding(child.getPaddingLeft(), child.getPaddingTop(), child.getPaddingRight(), transitionPadding);
                            child.measure(contentWidthSpec, View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), h + transitionPadding), NUM));
                            child.setPivotX((float) (child.getMeasuredWidth() / 2));
                        } else if (child == DialogsActivity.this.searchViewPager) {
                            DialogsActivity.this.searchViewPager.setTranslationY(0.0f);
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, NUM), View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), ((((View.MeasureSpec.getSize(heightMeasureSpec) + keyboardSize) - this.inputFieldHeight) + AndroidUtilities.dp(2.0f)) - (DialogsActivity.this.onlySelect ? 0 : DialogsActivity.this.actionBar.getMeasuredHeight())) - DialogsActivity.this.topPadding) - (DialogsActivity.this.searchTabsView == null ? 0 : AndroidUtilities.dp(44.0f)), NUM));
                            child.setPivotX((float) (child.getMeasuredWidth() / 2));
                        } else if (DialogsActivity.this.commentView == null || !DialogsActivity.this.commentView.isPopupView(child)) {
                            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                        } else if (!AndroidUtilities.isInMultiwindow) {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, NUM), View.MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, NUM));
                        } else if (AndroidUtilities.isTablet()) {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, NUM), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(320.0f), ((heightSize2 - this.inputFieldHeight) - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                        } else {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, NUM), View.MeasureSpec.makeMeasureSpec(((heightSize2 - this.inputFieldHeight) - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                        }
                    }
                }
                i++;
                f = 0.0f;
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            int paddingBottom;
            int count;
            int childLeft;
            int childTop;
            int count2 = getChildCount();
            Object tag = DialogsActivity.this.commentView != null ? DialogsActivity.this.commentView.getTag() : null;
            int keyboardSize = measureKeyboardHeight();
            int i = 2;
            if (tag == null || !tag.equals(2)) {
                paddingBottom = 0;
            } else {
                paddingBottom = (keyboardSize > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow) ? 0 : DialogsActivity.this.commentView.getEmojiPadding();
            }
            setBottomClip(paddingBottom);
            DialogsActivity dialogsActivity = DialogsActivity.this;
            int unused = dialogsActivity.lastMeasuredTopPadding = dialogsActivity.topPadding;
            int i2 = -1;
            while (i2 < count2) {
                View child = i2 == -1 ? DialogsActivity.this.commentView : getChildAt(i2);
                if (child == null) {
                    count = count2;
                } else if (child.getVisibility() == 8) {
                    count = count2;
                } else {
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
                            childLeft = ((((r - l) - width) / i) + lp.leftMargin) - lp.rightMargin;
                            break;
                        case 5:
                            childLeft = (r - width) - lp.rightMargin;
                            break;
                        default:
                            childLeft = lp.leftMargin;
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
                    if (DialogsActivity.this.commentView == null || !DialogsActivity.this.commentView.isPopupView(child)) {
                        if (child == DialogsActivity.this.filterTabsView || child == DialogsActivity.this.searchTabsView) {
                            count = count2;
                        } else if (child == DialogsActivity.this.filtersView) {
                            count = count2;
                        } else if (child == DialogsActivity.this.searchViewPager) {
                            count = count2;
                            childTop = (DialogsActivity.this.onlySelect ? 0 : DialogsActivity.this.actionBar.getMeasuredHeight()) + DialogsActivity.this.topPadding + (DialogsActivity.this.searchTabsView == null ? 0 : AndroidUtilities.dp(44.0f));
                        } else {
                            count = count2;
                            if ((child instanceof DatabaseMigrationHint) != 0) {
                                childTop = DialogsActivity.this.actionBar.getMeasuredHeight();
                            } else if (child instanceof ViewPage) {
                                if (!DialogsActivity.this.onlySelect) {
                                    if (DialogsActivity.this.filterTabsView == null || DialogsActivity.this.filterTabsView.getVisibility() != 0) {
                                        childTop = DialogsActivity.this.actionBar.getMeasuredHeight();
                                    } else {
                                        childTop = AndroidUtilities.dp(44.0f);
                                    }
                                }
                                childTop += DialogsActivity.this.topPadding;
                            } else if (child instanceof FragmentContextView) {
                                childTop += DialogsActivity.this.actionBar.getMeasuredHeight();
                            }
                        }
                        childTop = DialogsActivity.this.actionBar.getMeasuredHeight();
                    } else if (AndroidUtilities.isInMultiwindow) {
                        childTop = (DialogsActivity.this.commentView.getTop() - child.getMeasuredHeight()) + AndroidUtilities.dp(1.0f);
                        count = count2;
                    } else {
                        childTop = DialogsActivity.this.commentView.getBottom();
                        count = count2;
                    }
                    child.layout(childLeft, childTop, childLeft + width, childTop + height);
                }
                i2++;
                count2 = count;
                i = 2;
            }
            DialogsActivity.this.searchViewPager.setKeyboardHeight(keyboardSize);
            notifyHeightChanged();
            DialogsActivity.this.updateContextViewPosition();
            DialogsActivity.this.updateCommentView();
        }

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            int action = ev.getActionMasked();
            if ((action == 1 || action == 3) && DialogsActivity.this.actionBar.isActionModeShowed()) {
                boolean unused = DialogsActivity.this.allowMoving = true;
            }
            if (checkTabsAnimationInProgress()) {
                return true;
            }
            if ((DialogsActivity.this.filterTabsView == null || !DialogsActivity.this.filterTabsView.isAnimatingIndicator()) && !onTouchEvent(ev)) {
                return false;
            }
            return true;
        }

        public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            if (DialogsActivity.this.maybeStartTracking && !DialogsActivity.this.startedTracking) {
                onTouchEvent((MotionEvent) null);
            }
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
        }

        public boolean onTouchEvent(MotionEvent ev) {
            float velY;
            float velX;
            float dx;
            int duration;
            boolean z = false;
            if (DialogsActivity.this.parentLayout == null || DialogsActivity.this.filterTabsView == null || DialogsActivity.this.filterTabsView.isEditing() || DialogsActivity.this.searching || DialogsActivity.this.parentLayout.checkTransitionAnimation() || DialogsActivity.this.parentLayout.isInPreviewMode() || DialogsActivity.this.parentLayout.isPreviewOpenAnimationInProgress() || DialogsActivity.this.parentLayout.getDrawerLayoutContainer().isDrawerOpened() || ((ev != null && !DialogsActivity.this.startedTracking && ev.getY() <= ((float) DialogsActivity.this.actionBar.getMeasuredHeight()) + DialogsActivity.this.actionBar.getTranslationY()) || SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) != 5)) {
                return false;
            }
            if (ev != null) {
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                this.velocityTracker.addMovement(ev);
            }
            if (ev != null && ev.getAction() == 0 && checkTabsAnimationInProgress()) {
                boolean unused = DialogsActivity.this.startedTracking = true;
                this.startedTrackingPointerId = ev.getPointerId(0);
                this.startedTrackingX = (int) ev.getX();
                DialogsActivity.this.parentLayout.getDrawerLayoutContainer().setAllowOpenDrawerBySwipe(false);
                if (DialogsActivity.this.animatingForward) {
                    if (((float) this.startedTrackingX) < ((float) DialogsActivity.this.viewPages[0].getMeasuredWidth()) + DialogsActivity.this.viewPages[0].getTranslationX()) {
                        DialogsActivity dialogsActivity = DialogsActivity.this;
                        float unused2 = dialogsActivity.additionalOffset = dialogsActivity.viewPages[0].getTranslationX();
                    } else {
                        ViewPage page = DialogsActivity.this.viewPages[0];
                        DialogsActivity.this.viewPages[0] = DialogsActivity.this.viewPages[1];
                        DialogsActivity.this.viewPages[1] = page;
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
                    ViewPage page2 = DialogsActivity.this.viewPages[0];
                    DialogsActivity.this.viewPages[0] = DialogsActivity.this.viewPages[1];
                    DialogsActivity.this.viewPages[1] = page2;
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
            } else if (ev != null && ev.getAction() == 0) {
                float unused9 = DialogsActivity.this.additionalOffset = 0.0f;
            }
            if (ev != null && ev.getAction() == 0 && !DialogsActivity.this.startedTracking && !DialogsActivity.this.maybeStartTracking && DialogsActivity.this.filterTabsView.getVisibility() == 0) {
                this.startedTrackingPointerId = ev.getPointerId(0);
                boolean unused10 = DialogsActivity.this.maybeStartTracking = true;
                this.startedTrackingX = (int) ev.getX();
                this.startedTrackingY = (int) ev.getY();
                this.velocityTracker.clear();
            } else if (ev != null && ev.getAction() == 2 && ev.getPointerId(0) == this.startedTrackingPointerId) {
                int dx2 = (int) ((ev.getX() - ((float) this.startedTrackingX)) + DialogsActivity.this.additionalOffset);
                int dy = Math.abs(((int) ev.getY()) - this.startedTrackingY);
                if (DialogsActivity.this.startedTracking && ((DialogsActivity.this.animatingForward && dx2 > 0) || (!DialogsActivity.this.animatingForward && dx2 < 0))) {
                    if (!prepareForMoving(ev, dx2 < 0)) {
                        boolean unused11 = DialogsActivity.this.maybeStartTracking = true;
                        boolean unused12 = DialogsActivity.this.startedTracking = false;
                        DialogsActivity.this.viewPages[0].setTranslationX(0.0f);
                        DialogsActivity.this.viewPages[1].setTranslationX((float) (DialogsActivity.this.animatingForward ? DialogsActivity.this.viewPages[0].getMeasuredWidth() : -DialogsActivity.this.viewPages[0].getMeasuredWidth()));
                        DialogsActivity.this.filterTabsView.selectTabWithId(DialogsActivity.this.viewPages[1].selectedType, 0.0f);
                    }
                }
                if (DialogsActivity.this.maybeStartTracking && !DialogsActivity.this.startedTracking) {
                    float touchSlop = AndroidUtilities.getPixelsInCM(0.3f, true);
                    int dxLocal = (int) (ev.getX() - ((float) this.startedTrackingX));
                    if (((float) Math.abs(dxLocal)) >= touchSlop && Math.abs(dxLocal) > dy) {
                        if (dx2 < 0) {
                            z = true;
                        }
                        prepareForMoving(ev, z);
                    }
                } else if (DialogsActivity.this.startedTracking) {
                    DialogsActivity.this.viewPages[0].setTranslationX((float) dx2);
                    if (DialogsActivity.this.animatingForward) {
                        DialogsActivity.this.viewPages[1].setTranslationX((float) (DialogsActivity.this.viewPages[0].getMeasuredWidth() + dx2));
                    } else {
                        DialogsActivity.this.viewPages[1].setTranslationX((float) (dx2 - DialogsActivity.this.viewPages[0].getMeasuredWidth()));
                    }
                    float scrollProgress = ((float) Math.abs(dx2)) / ((float) DialogsActivity.this.viewPages[0].getMeasuredWidth());
                    if (!DialogsActivity.this.viewPages[1].isLocked || scrollProgress <= 0.3f) {
                        DialogsActivity.this.filterTabsView.selectTabWithId(DialogsActivity.this.viewPages[1].selectedType, scrollProgress);
                    } else {
                        dispatchTouchEvent(MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0));
                        DialogsActivity.this.filterTabsView.shakeLock(DialogsActivity.this.viewPages[1].selectedType);
                        AndroidUtilities.runOnUIThread(new DialogsActivity$ContentView$$ExternalSyntheticLambda0(this), 200);
                        return false;
                    }
                }
            } else if (ev == null || (ev.getPointerId(0) == this.startedTrackingPointerId && (ev.getAction() == 3 || ev.getAction() == 1 || ev.getAction() == 6))) {
                this.velocityTracker.computeCurrentVelocity(1000, (float) DialogsActivity.this.maximumVelocity);
                if (ev == null || ev.getAction() == 3) {
                    velX = 0.0f;
                    velY = 0.0f;
                } else {
                    velX = this.velocityTracker.getXVelocity();
                    velY = this.velocityTracker.getYVelocity();
                    if (!DialogsActivity.this.startedTracking && Math.abs(velX) >= 3000.0f && Math.abs(velX) > Math.abs(velY)) {
                        prepareForMoving(ev, velX < 0.0f);
                    }
                }
                if (DialogsActivity.this.startedTracking) {
                    float x = DialogsActivity.this.viewPages[0].getX();
                    AnimatorSet unused13 = DialogsActivity.this.tabsAnimation = new AnimatorSet();
                    if (DialogsActivity.this.viewPages[1].isLocked) {
                        boolean unused14 = DialogsActivity.this.backAnimation = true;
                    } else if (DialogsActivity.this.additionalOffset == 0.0f) {
                        boolean unused15 = DialogsActivity.this.backAnimation = Math.abs(x) < ((float) DialogsActivity.this.viewPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(velX) < 3500.0f || Math.abs(velX) < Math.abs(velY));
                    } else if (Math.abs(velX) > 1500.0f) {
                        DialogsActivity dialogsActivity5 = DialogsActivity.this;
                        boolean unused16 = dialogsActivity5.backAnimation = !dialogsActivity5.animatingForward ? velX < 0.0f : velX > 0.0f;
                    } else if (DialogsActivity.this.animatingForward) {
                        DialogsActivity dialogsActivity6 = DialogsActivity.this;
                        boolean unused17 = dialogsActivity6.backAnimation = dialogsActivity6.viewPages[1].getX() > ((float) (DialogsActivity.this.viewPages[0].getMeasuredWidth() >> 1));
                    } else {
                        DialogsActivity dialogsActivity7 = DialogsActivity.this;
                        boolean unused18 = dialogsActivity7.backAnimation = dialogsActivity7.viewPages[0].getX() < ((float) (DialogsActivity.this.viewPages[0].getMeasuredWidth() >> 1));
                    }
                    if (DialogsActivity.this.backAnimation) {
                        dx = Math.abs(x);
                        if (DialogsActivity.this.animatingForward) {
                            DialogsActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(DialogsActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(DialogsActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{(float) DialogsActivity.this.viewPages[1].getMeasuredWidth()})});
                        } else {
                            DialogsActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(DialogsActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(DialogsActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{(float) (-DialogsActivity.this.viewPages[1].getMeasuredWidth())})});
                        }
                    } else {
                        dx = ((float) DialogsActivity.this.viewPages[0].getMeasuredWidth()) - Math.abs(x);
                        if (DialogsActivity.this.animatingForward) {
                            DialogsActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(DialogsActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{(float) (-DialogsActivity.this.viewPages[0].getMeasuredWidth())}), ObjectAnimator.ofFloat(DialogsActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{0.0f})});
                        } else {
                            DialogsActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(DialogsActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{(float) DialogsActivity.this.viewPages[0].getMeasuredWidth()}), ObjectAnimator.ofFloat(DialogsActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{0.0f})});
                        }
                    }
                    DialogsActivity.this.tabsAnimation.setInterpolator(DialogsActivity.interpolator);
                    int width = getMeasuredWidth();
                    int halfWidth = width / 2;
                    float distance = ((float) halfWidth) + (((float) halfWidth) * AndroidUtilities.distanceInfluenceForSnapDuration(Math.min(1.0f, (dx * 1.0f) / ((float) width))));
                    float velX2 = Math.abs(velX);
                    if (velX2 > 0.0f) {
                        duration = Math.round(Math.abs(distance / velX2) * 1000.0f) * 4;
                    } else {
                        duration = (int) ((1.0f + (dx / ((float) getMeasuredWidth()))) * 100.0f);
                    }
                    DialogsActivity.this.tabsAnimation.setDuration((long) Math.max(150, Math.min(duration, 600)));
                    DialogsActivity.this.tabsAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            AnimatorSet unused = DialogsActivity.this.tabsAnimation = null;
                            if (!DialogsActivity.this.backAnimation) {
                                ViewPage tempPage = DialogsActivity.this.viewPages[0];
                                DialogsActivity.this.viewPages[0] = DialogsActivity.this.viewPages[1];
                                DialogsActivity.this.viewPages[1] = tempPage;
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
                            DialogsActivity.this.checkListLoad(DialogsActivity.this.viewPages[0]);
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

        /* renamed from: lambda$onTouchEvent$0$org-telegram-ui-DialogsActivity$ContentView  reason: not valid java name */
        public /* synthetic */ void m3434x3054591d() {
            DialogsActivity.this.showDialog(new LimitReachedBottomSheet(DialogsActivity.this, getContext(), 3, DialogsActivity.this.currentAccount));
        }

        public boolean hasOverlappingRendering() {
            return false;
        }

        /* access modifiers changed from: protected */
        public void drawList(Canvas blurCanvas, boolean top) {
            if (!DialogsActivity.this.searchIsShowed) {
                for (int i = 0; i < DialogsActivity.this.viewPages.length; i++) {
                    if (DialogsActivity.this.viewPages[i] != null && DialogsActivity.this.viewPages[i].getVisibility() == 0) {
                        for (int j = 0; j < DialogsActivity.this.viewPages[i].listView.getChildCount(); j++) {
                            View child = DialogsActivity.this.viewPages[i].listView.getChildAt(j);
                            if (child.getY() < ((float) (DialogsActivity.this.viewPages[i].listView.blurTopPadding + AndroidUtilities.dp(100.0f)))) {
                                int restore = blurCanvas.save();
                                blurCanvas.translate(DialogsActivity.this.viewPages[i].getX(), DialogsActivity.this.viewPages[i].getY() + DialogsActivity.this.viewPages[i].listView.getY() + child.getY());
                                if (child instanceof DialogCell) {
                                    DialogCell cell = (DialogCell) child;
                                    cell.drawingForBlur = true;
                                    cell.draw(blurCanvas);
                                    cell.drawingForBlur = false;
                                } else {
                                    child.draw(blurCanvas);
                                }
                                blurCanvas.restoreToCount(restore);
                            }
                        }
                    }
                }
            } else if (DialogsActivity.this.searchViewPager != null && DialogsActivity.this.searchViewPager.getVisibility() == 0) {
                DialogsActivity.this.searchViewPager.drawForBlur(blurCanvas);
            }
        }
    }

    public class DialogsRecyclerView extends BlurredRecyclerView {
        private int appliedPaddingTop;
        private boolean firstLayout = true;
        private boolean ignoreLayout;
        private int lastListPadding;
        private int lastTop;
        Paint paint = new Paint();
        private final ViewPage parentPage;
        RectF rectF = new RectF();

        public DialogsRecyclerView(Context context, ViewPage page) {
            super(context);
            this.parentPage = page;
            this.additionalClipBottom = AndroidUtilities.dp(200.0f);
        }

        /* access modifiers changed from: protected */
        public boolean updateEmptyViewAnimated() {
            return true;
        }

        public void setViewsOffset(float viewOffset) {
            View v;
            DialogsActivity.viewOffset = viewOffset;
            int n = getChildCount();
            for (int i = 0; i < n; i++) {
                getChildAt(i).setTranslationY(viewOffset);
            }
            if (!(this.selectorPosition == -1 || (v = getLayoutManager().findViewByPosition(this.selectorPosition)) == null)) {
                this.selectorRect.set(v.getLeft(), (int) (((float) v.getTop()) + viewOffset), v.getRight(), (int) (((float) v.getBottom()) + viewOffset));
                this.selectorDrawable.setBounds(this.selectorRect);
            }
            invalidate();
        }

        public float getViewOffset() {
            return DialogsActivity.viewOffset;
        }

        public void addView(View child, int index, ViewGroup.LayoutParams params) {
            super.addView(child, index, params);
            child.setTranslationY(DialogsActivity.viewOffset);
            child.setTranslationX(0.0f);
            child.setAlpha(1.0f);
        }

        public void removeView(View view) {
            super.removeView(view);
            view.setTranslationY(0.0f);
            view.setTranslationX(0.0f);
            view.setAlpha(1.0f);
        }

        public void onDraw(Canvas canvas) {
            if (!(this.parentPage.pullForegroundDrawable == null || DialogsActivity.viewOffset == 0.0f)) {
                int pTop = getPaddingTop();
                if (pTop != 0) {
                    canvas.save();
                    canvas.translate(0.0f, (float) pTop);
                }
                this.parentPage.pullForegroundDrawable.drawOverScroll(canvas);
                if (pTop != 0) {
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
                    View view = getChildAt(i);
                    if (((view instanceof DialogCell) && ((DialogCell) view).isMoving()) || ((view instanceof DialogsAdapter.LastEmptyView) && ((DialogsAdapter.LastEmptyView) view).moving)) {
                        if (view.getAlpha() != 1.0f) {
                            this.rectF.set(view.getX(), view.getY(), view.getX() + ((float) view.getMeasuredWidth()), view.getY() + ((float) view.getMeasuredHeight()));
                            canvas.saveLayerAlpha(this.rectF, (int) (view.getAlpha() * 255.0f), 31);
                        } else {
                            canvas.save();
                        }
                        canvas.translate(view.getX(), view.getY());
                        canvas.drawRect(0.0f, 0.0f, (float) view.getMeasuredWidth(), (float) view.getMeasuredHeight(), this.paint);
                        view.draw(canvas);
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

        public boolean drawChild(Canvas canvas, View child, long drawingTime) {
            if (!drawMovingViewsOverlayed() || !(child instanceof DialogCell) || !((DialogCell) child).isMoving()) {
                return super.drawChild(canvas, child, drawingTime);
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
        public void onMeasure(int widthSpec, int heightSpec) {
            int t;
            RecyclerView.ViewHolder holder;
            int t2 = 0;
            if (!DialogsActivity.this.onlySelect) {
                if (DialogsActivity.this.filterTabsView == null || DialogsActivity.this.filterTabsView.getVisibility() != 0) {
                    t2 = DialogsActivity.this.actionBar.getMeasuredHeight();
                } else {
                    t2 = AndroidUtilities.dp(44.0f);
                }
            }
            int pos = this.parentPage.layoutManager.findFirstVisibleItemPosition();
            if (pos != -1 && !DialogsActivity.this.dialogsListFrozen && this.parentPage.itemTouchhelper.isIdle() && (holder = this.parentPage.listView.findViewHolderForAdapterPosition(pos)) != null) {
                int top = holder.itemView.getTop();
                this.ignoreLayout = true;
                this.parentPage.layoutManager.scrollToPositionWithOffset(pos, (int) (((float) (top - this.lastListPadding)) + DialogsActivity.this.scrollAdditionalOffset));
                this.ignoreLayout = false;
            }
            if (!DialogsActivity.this.onlySelect) {
                this.ignoreLayout = true;
                if (DialogsActivity.this.filterTabsView == null || DialogsActivity.this.filterTabsView.getVisibility() != 0) {
                    t = (!DialogsActivity.this.inPreviewMode || Build.VERSION.SDK_INT < 21) ? 0 : AndroidUtilities.statusBarHeight;
                } else {
                    t = ActionBar.getCurrentActionBarHeight() + (DialogsActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
                }
                setTopGlowOffset(t);
                setPadding(0, t, 0, 0);
                this.parentPage.progressView.setPaddingTop(t);
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
            super.onMeasure(widthSpec, heightSpec);
            if (!DialogsActivity.this.onlySelect && this.appliedPaddingTop != t && DialogsActivity.this.viewPages != null && DialogsActivity.this.viewPages.length > 1) {
                DialogsActivity.this.viewPages[1].setTranslationX((float) DialogsActivity.this.viewPages[0].getMeasuredWidth());
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            this.lastListPadding = getPaddingTop();
            this.lastTop = t;
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
        public void toggleArchiveHidden(boolean action, DialogCell dialogCell) {
            SharedConfig.toggleArchiveHidden();
            if (SharedConfig.archiveHidden) {
                if (dialogCell != null) {
                    boolean unused = DialogsActivity.this.disableActionBarScrolling = true;
                    boolean unused2 = DialogsActivity.this.waitingForScrollFinished = true;
                    smoothScrollBy(0, dialogCell.getMeasuredHeight() + (dialogCell.getTop() - getPaddingTop()), CubicBezierInterpolator.EASE_OUT);
                    if (action) {
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
            if (action && dialogCell != null) {
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

        public boolean onTouchEvent(MotionEvent e) {
            if (this.fastScrollAnimationRunning || DialogsActivity.this.waitingForScrollFinished || DialogsActivity.this.dialogRemoveFinished != 0 || DialogsActivity.this.dialogInsertFinished != 0 || DialogsActivity.this.dialogChangeFinished != 0) {
                return false;
            }
            int action = e.getAction();
            if (action == 0) {
                setOverScrollMode(0);
            }
            if ((action == 1 || action == 3) && !this.parentPage.itemTouchhelper.isIdle() && this.parentPage.swipeController.swipingFolder) {
                boolean unused = this.parentPage.swipeController.swipeFolderBack = true;
                if (!(this.parentPage.itemTouchhelper.checkHorizontalSwipe((RecyclerView.ViewHolder) null, 4) == 0 || this.parentPage.swipeController.currentItemViewHolder == null)) {
                    RecyclerView.ViewHolder viewHolder = this.parentPage.swipeController.currentItemViewHolder;
                    if (viewHolder.itemView instanceof DialogCell) {
                        DialogCell dialogCell = (DialogCell) viewHolder.itemView;
                        long dialogId = dialogCell.getDialogId();
                        if (DialogObject.isFolderDialogId(dialogId)) {
                            toggleArchiveHidden(false, dialogCell);
                        } else {
                            DialogsActivity dialogsActivity = DialogsActivity.this;
                            TLRPC.Dialog dialog = dialogsActivity.getDialogsArray(dialogsActivity.currentAccount, this.parentPage.dialogsType, DialogsActivity.this.folderId, false).get(dialogCell.getDialogIndex());
                            if (SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 1) {
                                ArrayList<Long> selectedDialogs = new ArrayList<>();
                                selectedDialogs.add(Long.valueOf(dialogId));
                                int unused2 = DialogsActivity.this.canReadCount = (dialog.unread_count > 0 || dialog.unread_mark) ? 1 : 0;
                                DialogsActivity.this.performSelectedDialogsAction(selectedDialogs, 101, true);
                            } else if (SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 3) {
                                if (!DialogsActivity.this.getMessagesController().isDialogMuted(dialogId)) {
                                    NotificationsController.getInstance(UserConfig.selectedAccount).setDialogNotificationsSettings(dialogId, 3);
                                    if (BulletinFactory.canShowBulletin(DialogsActivity.this)) {
                                        BulletinFactory.createMuteBulletin(DialogsActivity.this, 3).show();
                                    }
                                } else {
                                    ArrayList<Long> selectedDialogs2 = new ArrayList<>();
                                    selectedDialogs2.add(Long.valueOf(dialogId));
                                    DialogsActivity dialogsActivity2 = DialogsActivity.this;
                                    int unused3 = dialogsActivity2.canMuteCount = MessagesController.getInstance(dialogsActivity2.currentAccount).isDialogMuted(dialogId) ^ true ? 1 : 0;
                                    DialogsActivity dialogsActivity3 = DialogsActivity.this;
                                    int unused4 = dialogsActivity3.canUnmuteCount = dialogsActivity3.canMuteCount > 0 ? 0 : 1;
                                    DialogsActivity.this.performSelectedDialogsAction(selectedDialogs2, 104, true);
                                }
                            } else if (SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 0) {
                                ArrayList<Long> selectedDialogs3 = new ArrayList<>();
                                selectedDialogs3.add(Long.valueOf(dialogId));
                                int unused5 = DialogsActivity.this.canPinCount = DialogsActivity.this.isDialogPinned(dialog) ^ true ? 1 : 0;
                                DialogsActivity.this.performSelectedDialogsAction(selectedDialogs3, 100, true);
                            } else if (SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 4) {
                                ArrayList<Long> selectedDialogs4 = new ArrayList<>();
                                selectedDialogs4.add(Long.valueOf(dialogId));
                                DialogsActivity.this.performSelectedDialogsAction(selectedDialogs4, 102, true);
                            }
                        }
                    }
                }
            }
            boolean result = super.onTouchEvent(e);
            if (this.parentPage.dialogsType == 0 && ((action == 1 || action == 3) && this.parentPage.archivePullViewState == 2 && DialogsActivity.this.hasHiddenArchive())) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
                int currentPosition = layoutManager.findFirstVisibleItemPosition();
                if (currentPosition == 0) {
                    int pTop = getPaddingTop();
                    View view = layoutManager.findViewByPosition(currentPosition);
                    int height = (int) (((float) AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f)) * 0.85f);
                    int diff = (view.getTop() - pTop) + view.getMeasuredHeight();
                    if (view != null) {
                        long pullingTime = System.currentTimeMillis() - DialogsActivity.this.startArchivePullingTime;
                        if (diff < height || pullingTime < 200) {
                            boolean unused6 = DialogsActivity.this.disableActionBarScrolling = true;
                            smoothScrollBy(0, diff, CubicBezierInterpolator.EASE_OUT_QUINT);
                            int unused7 = this.parentPage.archivePullViewState = 2;
                        } else if (this.parentPage.archivePullViewState != 1) {
                            if (getViewOffset() == 0.0f) {
                                boolean unused8 = DialogsActivity.this.disableActionBarScrolling = true;
                                smoothScrollBy(0, view.getTop() - pTop, CubicBezierInterpolator.EASE_OUT_QUINT);
                            }
                            if (!DialogsActivity.this.canShowHiddenArchive) {
                                boolean unused9 = DialogsActivity.this.canShowHiddenArchive = true;
                                performHapticFeedback(3, 2);
                                if (this.parentPage.pullForegroundDrawable != null) {
                                    this.parentPage.pullForegroundDrawable.colorize(true);
                                }
                            }
                            ((DialogCell) view).startOutAnimation();
                            int unused10 = this.parentPage.archivePullViewState = 1;
                        }
                        if (getViewOffset() != 0.0f) {
                            ValueAnimator valueAnimator = ValueAnimator.ofFloat(new float[]{getViewOffset(), 0.0f});
                            valueAnimator.addUpdateListener(new DialogsActivity$DialogsRecyclerView$$ExternalSyntheticLambda0(this));
                            ValueAnimator valueAnimator2 = valueAnimator;
                            int i = currentPosition;
                            int i2 = pTop;
                            valueAnimator2.setDuration(Math.max(100, (long) (350.0f - ((getViewOffset() / ((float) PullForegroundDrawable.getMaxOverscroll())) * 120.0f))));
                            valueAnimator2.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                            setScrollEnabled(false);
                            valueAnimator2.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    DialogsRecyclerView.this.setScrollEnabled(true);
                                }
                            });
                            valueAnimator2.start();
                        } else {
                            int i3 = pTop;
                        }
                    } else {
                        int i4 = pTop;
                    }
                }
            }
            return result;
        }

        /* renamed from: lambda$onTouchEvent$0$org-telegram-ui-DialogsActivity$DialogsRecyclerView  reason: not valid java name */
        public /* synthetic */ void m3435xa454faae(ValueAnimator animation) {
            setViewsOffset(((Float) animation.getAnimatedValue()).floatValue());
        }

        public boolean onInterceptTouchEvent(MotionEvent e) {
            if (this.fastScrollAnimationRunning || DialogsActivity.this.waitingForScrollFinished || DialogsActivity.this.dialogRemoveFinished != 0 || DialogsActivity.this.dialogInsertFinished != 0 || DialogsActivity.this.dialogChangeFinished != 0) {
                return false;
            }
            if (e.getAction() == 0) {
                DialogsActivity dialogsActivity = DialogsActivity.this;
                boolean unused = dialogsActivity.allowSwipeDuringCurrentTouch = !dialogsActivity.actionBar.isActionModeShowed();
                checkIfAdapterValid();
            }
            return super.onInterceptTouchEvent(e);
        }

        /* access modifiers changed from: protected */
        public boolean allowSelectChildAtPosition(View child) {
            if (!(child instanceof HeaderCell) || child.isClickable()) {
                return true;
            }
            return false;
        }
    }

    private class SwipeController extends ItemTouchHelper.Callback {
        private RectF buttonInstance;
        /* access modifiers changed from: private */
        public RecyclerView.ViewHolder currentItemViewHolder;
        private ViewPage parentPage;
        /* access modifiers changed from: private */
        public boolean swipeFolderBack;
        /* access modifiers changed from: private */
        public boolean swipingFolder;

        public SwipeController(ViewPage page) {
            this.parentPage = page;
        }

        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            TLRPC.Dialog dialog;
            if (DialogsActivity.this.waitingForDialogsAnimationEnd(this.parentPage) || (DialogsActivity.this.parentLayout != null && DialogsActivity.this.parentLayout.isInPreviewMode())) {
                return 0;
            }
            if (this.swipingFolder && this.swipeFolderBack) {
                if (viewHolder.itemView instanceof DialogCell) {
                    ((DialogCell) viewHolder.itemView).swipeCanceled = true;
                }
                this.swipingFolder = false;
                return 0;
            } else if (DialogsActivity.this.onlySelect || !this.parentPage.isDefaultDialogType() || DialogsActivity.this.slidingView != null || !(viewHolder.itemView instanceof DialogCell)) {
                return 0;
            } else {
                DialogCell dialogCell = (DialogCell) viewHolder.itemView;
                long dialogId = dialogCell.getDialogId();
                if (DialogsActivity.this.actionBar.isActionModeShowed((String) null)) {
                    TLRPC.Dialog dialog2 = DialogsActivity.this.getMessagesController().dialogs_dict.get(dialogId);
                    if (!DialogsActivity.this.allowMoving || dialog2 == null || !DialogsActivity.this.isDialogPinned(dialog2) || DialogObject.isFolderDialogId(dialogId)) {
                        return 0;
                    }
                    DialogCell unused = DialogsActivity.this.movingView = (DialogCell) viewHolder.itemView;
                    DialogsActivity.this.movingView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    this.swipeFolderBack = false;
                    return makeMovementFlags(3, 0);
                } else if ((DialogsActivity.this.filterTabsView != null && DialogsActivity.this.filterTabsView.getVisibility() == 0 && SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 5) || !DialogsActivity.this.allowSwipeDuringCurrentTouch || (((dialogId == DialogsActivity.this.getUserConfig().clientUserId || dialogId == 777000) && SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 2) || (DialogsActivity.this.getMessagesController().isPromoDialog(dialogId, false) && DialogsActivity.this.getMessagesController().promoDialogType != MessagesController.PROMO_TYPE_PSA))) {
                    return 0;
                } else {
                    boolean canSwipeBack = DialogsActivity.this.folderId == 0 && (SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 3 || SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 1 || SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 0 || SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 4);
                    if (SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 1) {
                        MessagesController.DialogFilter filter = null;
                        if (DialogsActivity.this.viewPages[0].dialogsType == 7 || DialogsActivity.this.viewPages[0].dialogsType == 8) {
                            filter = DialogsActivity.this.getMessagesController().selectedDialogFilter[DialogsActivity.this.viewPages[0].dialogsType == 8 ? (char) 1 : 0];
                        }
                        if (!(filter == null || (filter.flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ) == 0 || (dialog = DialogsActivity.this.getMessagesController().dialogs_dict.get(dialogId)) == null || filter.alwaysShow(DialogsActivity.this.currentAccount, dialog) || (dialog.unread_count <= 0 && !dialog.unread_mark))) {
                            canSwipeBack = false;
                        }
                    }
                    this.swipeFolderBack = false;
                    this.swipingFolder = (canSwipeBack && !DialogObject.isFolderDialogId(dialogCell.getDialogId())) || (SharedConfig.archiveHidden && DialogObject.isFolderDialogId(dialogCell.getDialogId()));
                    dialogCell.setSliding(true);
                    return makeMovementFlags(0, 4);
                }
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:3:0x0008, code lost:
            r2 = ((org.telegram.ui.Cells.DialogCell) r14.itemView).getDialogId();
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onMove(androidx.recyclerview.widget.RecyclerView r12, androidx.recyclerview.widget.RecyclerView.ViewHolder r13, androidx.recyclerview.widget.RecyclerView.ViewHolder r14) {
            /*
                r11 = this;
                android.view.View r0 = r14.itemView
                boolean r0 = r0 instanceof org.telegram.ui.Cells.DialogCell
                r1 = 0
                if (r0 != 0) goto L_0x0008
                return r1
            L_0x0008:
                android.view.View r0 = r14.itemView
                org.telegram.ui.Cells.DialogCell r0 = (org.telegram.ui.Cells.DialogCell) r0
                long r2 = r0.getDialogId()
                org.telegram.ui.DialogsActivity r4 = org.telegram.ui.DialogsActivity.this
                org.telegram.messenger.MessagesController r4 = r4.getMessagesController()
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r4 = r4.dialogs_dict
                java.lang.Object r4 = r4.get(r2)
                org.telegram.tgnet.TLRPC$Dialog r4 = (org.telegram.tgnet.TLRPC.Dialog) r4
                if (r4 == 0) goto L_0x009c
                org.telegram.ui.DialogsActivity r5 = org.telegram.ui.DialogsActivity.this
                boolean r5 = r5.isDialogPinned(r4)
                if (r5 == 0) goto L_0x009c
                boolean r5 = org.telegram.messenger.DialogObject.isFolderDialogId(r2)
                if (r5 == 0) goto L_0x002f
                goto L_0x009c
            L_0x002f:
                int r5 = r13.getAdapterPosition()
                int r6 = r14.getAdapterPosition()
                org.telegram.ui.DialogsActivity$ViewPage r7 = r11.parentPage
                org.telegram.ui.Adapters.DialogsAdapter r7 = r7.dialogsAdapter
                r7.notifyItemMoved(r5, r6)
                org.telegram.ui.DialogsActivity r7 = org.telegram.ui.DialogsActivity.this
                r7.updateDialogIndices()
                org.telegram.ui.DialogsActivity r7 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.DialogsActivity$ViewPage[] r7 = r7.viewPages
                r7 = r7[r1]
                int r7 = r7.dialogsType
                r8 = 7
                r9 = 8
                r10 = 1
                if (r7 == r8) goto L_0x006c
                org.telegram.ui.DialogsActivity r7 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.DialogsActivity$ViewPage[] r7 = r7.viewPages
                r7 = r7[r1]
                int r7 = r7.dialogsType
                if (r7 != r9) goto L_0x0066
                goto L_0x006c
            L_0x0066:
                org.telegram.ui.DialogsActivity r1 = org.telegram.ui.DialogsActivity.this
                boolean unused = r1.movingWas = r10
                goto L_0x009b
            L_0x006c:
                org.telegram.ui.DialogsActivity r7 = org.telegram.ui.DialogsActivity.this
                org.telegram.messenger.MessagesController r7 = r7.getMessagesController()
                org.telegram.messenger.MessagesController$DialogFilter[] r7 = r7.selectedDialogFilter
                org.telegram.ui.DialogsActivity r8 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.DialogsActivity$ViewPage[] r8 = r8.viewPages
                r8 = r8[r1]
                int r8 = r8.dialogsType
                if (r8 != r9) goto L_0x0083
                r1 = 1
            L_0x0083:
                r1 = r7[r1]
                org.telegram.ui.DialogsActivity r7 = org.telegram.ui.DialogsActivity.this
                java.util.ArrayList r7 = r7.movingDialogFilters
                boolean r7 = r7.contains(r1)
                if (r7 != 0) goto L_0x009a
                org.telegram.ui.DialogsActivity r7 = org.telegram.ui.DialogsActivity.this
                java.util.ArrayList r7 = r7.movingDialogFilters
                r7.add(r1)
            L_0x009a:
            L_0x009b:
                return r10
            L_0x009c:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.SwipeController.onMove(androidx.recyclerview.widget.RecyclerView, androidx.recyclerview.widget.RecyclerView$ViewHolder, androidx.recyclerview.widget.RecyclerView$ViewHolder):boolean");
        }

        public int convertToAbsoluteDirection(int flags, int layoutDirection) {
            if (this.swipeFolderBack) {
                return 0;
            }
            return super.convertToAbsoluteDirection(flags, layoutDirection);
        }

        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            if (viewHolder != null) {
                DialogCell dialogCell = (DialogCell) viewHolder.itemView;
                long dialogId = dialogCell.getDialogId();
                int i = 0;
                if (DialogObject.isFolderDialogId(dialogId)) {
                    this.parentPage.listView.toggleArchiveHidden(false, dialogCell);
                    return;
                }
                TLRPC.Dialog dialog = DialogsActivity.this.getMessagesController().dialogs_dict.get(dialogId);
                if (dialog != null) {
                    if (!DialogsActivity.this.getMessagesController().isPromoDialog(dialogId, false) && DialogsActivity.this.folderId == 0 && SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 1) {
                        ArrayList<Long> selectedDialogs = new ArrayList<>();
                        selectedDialogs.add(Long.valueOf(dialogId));
                        DialogsActivity dialogsActivity = DialogsActivity.this;
                        if (dialog.unread_count > 0 || dialog.unread_mark) {
                            i = 1;
                        }
                        int unused = dialogsActivity.canReadCount = i;
                        DialogsActivity.this.performSelectedDialogsAction(selectedDialogs, 101, true);
                        return;
                    }
                    DialogCell unused2 = DialogsActivity.this.slidingView = dialogCell;
                    Runnable finishRunnable = new DialogsActivity$SwipeController$$ExternalSyntheticLambda2(this, dialog, this.parentPage.dialogsAdapter.getItemCount(), viewHolder.getAdapterPosition());
                    DialogsActivity.this.setDialogsListFrozen(true);
                    if (Utilities.random.nextInt(1000) == 1) {
                        if (DialogsActivity.this.pacmanAnimation == null) {
                            PacmanAnimation unused3 = DialogsActivity.this.pacmanAnimation = new PacmanAnimation(this.parentPage.listView);
                        }
                        DialogsActivity.this.pacmanAnimation.setFinishRunnable(finishRunnable);
                        DialogsActivity.this.pacmanAnimation.start();
                        return;
                    }
                    finishRunnable.run();
                    return;
                }
                return;
            }
            DialogCell unused4 = DialogsActivity.this.slidingView = null;
        }

        /* renamed from: lambda$onSwiped$1$org-telegram-ui-DialogsActivity$SwipeController  reason: not valid java name */
        public /* synthetic */ void m3437x3var_d65(TLRPC.Dialog dialog, int count, int position) {
            RecyclerView.ViewHolder holder;
            TLRPC.Dialog dialog2 = dialog;
            int i = position;
            if (DialogsActivity.this.frozenDialogsList != null) {
                DialogsActivity.this.frozenDialogsList.remove(dialog2);
                int pinnedNum = dialog2.pinnedNum;
                DialogCell unused = DialogsActivity.this.slidingView = null;
                this.parentPage.listView.invalidate();
                int lastItemPosition = this.parentPage.layoutManager.findLastVisibleItemPosition();
                if (lastItemPosition == count - 1) {
                    this.parentPage.layoutManager.findViewByPosition(lastItemPosition).requestLayout();
                }
                boolean hintShowed = false;
                if (DialogsActivity.this.getMessagesController().isPromoDialog(dialog2.id, false)) {
                    DialogsActivity.this.getMessagesController().hidePromoDialog();
                    this.parentPage.dialogsItemAnimator.prepareForRemove();
                    ViewPage.access$11210(this.parentPage);
                    this.parentPage.dialogsAdapter.notifyItemRemoved(i);
                    int unused2 = DialogsActivity.this.dialogRemoveFinished = 2;
                    return;
                }
                int added = DialogsActivity.this.getMessagesController().addDialogToFolder(dialog2.id, DialogsActivity.this.folderId == 0 ? 1 : 0, -1, 0);
                if (!(added == 2 && i == 0)) {
                    this.parentPage.dialogsItemAnimator.prepareForRemove();
                    ViewPage.access$11210(this.parentPage);
                    this.parentPage.dialogsAdapter.notifyItemRemoved(i);
                    int unused3 = DialogsActivity.this.dialogRemoveFinished = 2;
                }
                if (DialogsActivity.this.folderId == 0) {
                    if (added == 2) {
                        this.parentPage.dialogsItemAnimator.prepareForRemove();
                        if (i == 0) {
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
                    } else if (added == 1 && (holder = this.parentPage.listView.findViewHolderForAdapterPosition(0)) != null && (holder.itemView instanceof DialogCell)) {
                        DialogCell cell = (DialogCell) holder.itemView;
                        cell.checkCurrentDialogIndex(true);
                        cell.animateArchiveAvatar();
                    }
                    SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                    if (preferences.getBoolean("archivehint_l", false) || SharedConfig.archiveHidden) {
                        hintShowed = true;
                    }
                    if (!hintShowed) {
                        preferences.edit().putBoolean("archivehint_l", true).commit();
                    }
                    DialogsActivity.this.getUndoView().showWithAction(dialog2.id, hintShowed ? 2 : 3, (Runnable) null, new DialogsActivity$SwipeController$$ExternalSyntheticLambda1(this, dialog2, pinnedNum));
                }
                if (DialogsActivity.this.folderId != 0 && DialogsActivity.this.frozenDialogsList.isEmpty()) {
                    this.parentPage.listView.setEmptyView((View) null);
                    this.parentPage.progressView.setVisibility(4);
                }
            }
        }

        /* renamed from: lambda$onSwiped$0$org-telegram-ui-DialogsActivity$SwipeController  reason: not valid java name */
        public /* synthetic */ void m3436x3fb9d364(TLRPC.Dialog dialog, int pinnedNum) {
            boolean unused = DialogsActivity.this.dialogsListFrozen = true;
            DialogsActivity.this.getMessagesController().addDialogToFolder(dialog.id, 0, pinnedNum, 0);
            boolean unused2 = DialogsActivity.this.dialogsListFrozen = false;
            ArrayList<TLRPC.Dialog> dialogs = DialogsActivity.this.getMessagesController().getDialogs(0);
            int index = dialogs.indexOf(dialog);
            if (index >= 0) {
                ArrayList<TLRPC.Dialog> archivedDialogs = DialogsActivity.this.getMessagesController().getDialogs(1);
                if (!archivedDialogs.isEmpty() || index != 1) {
                    int unused3 = DialogsActivity.this.dialogInsertFinished = 2;
                    DialogsActivity.this.setDialogsListFrozen(true);
                    this.parentPage.dialogsItemAnimator.prepareForRemove();
                    ViewPage.access$11208(this.parentPage);
                    this.parentPage.dialogsAdapter.notifyItemInserted(index);
                }
                if (archivedDialogs.isEmpty()) {
                    dialogs.remove(0);
                    if (index == 1) {
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

        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (viewHolder != null) {
                this.parentPage.listView.hideSelector(false);
            }
            this.currentItemViewHolder = viewHolder;
            if (viewHolder != null && (viewHolder.itemView instanceof DialogCell)) {
                ((DialogCell) viewHolder.itemView).swipeCanceled = false;
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        public long getAnimationDuration(RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
            if (animationType == 4) {
                return 200;
            }
            if (animationType == 8 && DialogsActivity.this.movingView != null) {
                AndroidUtilities.runOnUIThread(new DialogsActivity$SwipeController$$ExternalSyntheticLambda0(DialogsActivity.this.movingView), this.parentPage.dialogsItemAnimator.getMoveDuration());
                DialogCell unused = DialogsActivity.this.movingView = null;
            }
            return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
        }

        public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
            return 0.45f;
        }

        public float getSwipeEscapeVelocity(float defaultValue) {
            return 3500.0f;
        }

        public float getSwipeVelocityThreshold(float defaultValue) {
            return Float.MAX_VALUE;
        }
    }

    public DialogsActivity(Bundle args) {
        super(args);
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
            this.otherwiseReloginDays = this.arguments.getInt("otherwiseRelogin");
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
            View localView = this.databaseMigrationHint;
            if (localView.getParent() != null) {
                ((ViewGroup) localView.getParent()).removeView(localView);
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
            AndroidUtilities.runOnUIThread(new DialogsActivity$$ExternalSyntheticLambda24(accountInstance), 200);
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
            getNotificationCenter().removeObserver(this, NotificationCenter.onDownloadingFilesChanged);
            getNotificationCenter().removeObserver(this, NotificationCenter.needDeleteDialog);
            getNotificationCenter().removeObserver(this, NotificationCenter.folderBecomeEmpty);
            getNotificationCenter().removeObserver(this, NotificationCenter.newSuggestionsAvailable);
            getNotificationCenter().removeObserver(this, NotificationCenter.fileLoaded);
            getNotificationCenter().removeObserver(this, NotificationCenter.fileLoadFailed);
            getNotificationCenter().removeObserver(this, NotificationCenter.fileLoadProgressChanged);
            getNotificationCenter().removeObserver(this, NotificationCenter.dialogsUnreadReactionsCounterChanged);
            getNotificationCenter().removeObserver(this, NotificationCenter.forceImportContactsStart);
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
        ActionBar actionBar = new ActionBar(context) {
            public void setTranslationY(float translationY) {
                if (!(translationY == getTranslationY() || DialogsActivity.this.fragmentView == null)) {
                    DialogsActivity.this.fragmentView.invalidate();
                }
                super.setTranslationY(translationY);
            }

            /* access modifiers changed from: protected */
            public boolean shouldClipChild(View child) {
                return super.shouldClipChild(child) || child == DialogsActivity.this.doneItem;
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (!DialogsActivity.this.inPreviewMode || DialogsActivity.this.avatarContainer == null || child == DialogsActivity.this.avatarContainer) {
                    return super.drawChild(canvas, child, drawingTime);
                }
                return false;
            }
        };
        actionBar.setItemsBackgroundColor(Theme.getColor("actionBarDefaultSelector"), false);
        actionBar.setItemsBackgroundColor(Theme.getColor("actionBarActionModeDefaultSelector"), true);
        actionBar.setItemsColor(Theme.getColor("actionBarDefaultIcon"), false);
        actionBar.setItemsColor(Theme.getColor("actionBarActionModeDefaultIcon"), true);
        if (this.inPreviewMode || (AndroidUtilities.isTablet() && this.folderId != 0)) {
            actionBar.setOccupyStatusBar(false);
        }
        return actionBar;
    }

    public View createView(Context context) {
        int type;
        boolean z;
        Drawable drawable;
        boolean z2;
        int i;
        int i2;
        String str;
        final Context context2 = context;
        boolean z3 = false;
        this.searching = false;
        this.searchWas = false;
        this.pacmanAnimation = null;
        this.selectedDialogs.clear();
        this.maximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        AndroidUtilities.runOnUIThread(new DialogsActivity$$ExternalSyntheticLambda23(context2));
        ActionBarMenu menu = this.actionBar.createMenu();
        int i3 = 8;
        boolean z4 = true;
        if (!this.onlySelect && this.searchString == null && this.folderId == 0) {
            ActionBarMenuItem actionBarMenuItem = r0;
            ActionBarMenuItem actionBarMenuItem2 = new ActionBarMenuItem(context, (ActionBarMenu) null, Theme.getColor("actionBarDefaultSelector"), Theme.getColor("actionBarDefaultIcon"), true);
            this.doneItem = actionBarMenuItem;
            actionBarMenuItem.setText(LocaleController.getString("Done", NUM).toUpperCase());
            this.actionBar.addView(this.doneItem, LayoutHelper.createFrame(-2, -2.0f, 53, 0.0f, 0.0f, 10.0f, 0.0f));
            this.doneItem.setOnClickListener(new DialogsActivity$$ExternalSyntheticLambda12(this));
            this.doneItem.setAlpha(0.0f);
            this.doneItem.setVisibility(8);
            ProxyDrawable proxyDrawable2 = new ProxyDrawable(context2);
            this.proxyDrawable = proxyDrawable2;
            ActionBarMenuItem addItem = menu.addItem(2, (Drawable) proxyDrawable2);
            this.proxyItem = addItem;
            addItem.setContentDescription(LocaleController.getString("ProxySettings", NUM));
            RLottieDrawable rLottieDrawable = new RLottieDrawable(NUM, "passcode_lock_close", AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), true, (int[]) null);
            this.passcodeDrawable = rLottieDrawable;
            ActionBarMenuItem addItem2 = menu.addItem(1, (Drawable) rLottieDrawable);
            this.passcodeItem = addItem2;
            addItem2.setContentDescription(LocaleController.getString("AccDescrPasscodeLock", NUM));
            ActionBarMenuItem addItem3 = menu.addItem(3, (Drawable) new ColorDrawable(0));
            this.downloadsItem = addItem3;
            addItem3.addView(new DownloadProgressIcon(this.currentAccount, context2));
            this.downloadsItem.setContentDescription(LocaleController.getString("DownloadsTabs", NUM));
            this.downloadsItem.setVisibility(8);
            updatePasscodeButton();
            updateProxyButton(false, false);
        }
        ActionBarMenuItem actionBarMenuItemSearchListener = menu.addItem(0, NUM).setIsSearchField(true, true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                boolean unused = DialogsActivity.this.searching = true;
                if (DialogsActivity.this.switchItem != null) {
                    DialogsActivity.this.switchItem.setVisibility(8);
                }
                if (DialogsActivity.this.proxyItem != null && DialogsActivity.this.proxyItemVisible) {
                    DialogsActivity.this.proxyItem.setVisibility(8);
                }
                if (DialogsActivity.this.downloadsItem != null && DialogsActivity.this.downloadsItemVisible) {
                    DialogsActivity.this.downloadsItem.setVisibility(8);
                }
                if (DialogsActivity.this.viewPages[0] != null) {
                    if (DialogsActivity.this.searchString != null) {
                        DialogsActivity.this.viewPages[0].listView.hide();
                        if (DialogsActivity.this.searchViewPager != null) {
                            DialogsActivity.this.searchViewPager.searchListView.show();
                        }
                    }
                    if (!DialogsActivity.this.onlySelect) {
                        DialogsActivity.this.floatingButtonContainer.setVisibility(8);
                    }
                }
                DialogsActivity.this.setScrollY(0.0f);
                DialogsActivity.this.updatePasscodeButton();
                DialogsActivity.this.updateProxyButton(false, false);
                DialogsActivity.this.actionBar.setBackButtonContentDescription(LocaleController.getString("AccDescrGoBack", NUM));
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, new Object[0]);
                ((SizeNotifierFrameLayout) DialogsActivity.this.fragmentView).invalidateBlur();
            }

            public boolean canCollapseSearch() {
                if (DialogsActivity.this.switchItem != null) {
                    DialogsActivity.this.switchItem.setVisibility(0);
                }
                if (DialogsActivity.this.proxyItem != null && DialogsActivity.this.proxyItemVisible) {
                    DialogsActivity.this.proxyItem.setVisibility(0);
                }
                if (DialogsActivity.this.downloadsItem != null && DialogsActivity.this.downloadsItemVisible) {
                    DialogsActivity.this.downloadsItem.setVisibility(0);
                }
                if (DialogsActivity.this.searchString == null) {
                    return true;
                }
                DialogsActivity.this.finishFragment();
                return false;
            }

            public void onSearchCollapse() {
                boolean unused = DialogsActivity.this.searching = false;
                boolean unused2 = DialogsActivity.this.searchWas = false;
                if (DialogsActivity.this.viewPages[0] != null) {
                    DialogsActivity.this.viewPages[0].listView.setEmptyView(DialogsActivity.this.folderId == 0 ? DialogsActivity.this.viewPages[0].progressView : null);
                    if (!DialogsActivity.this.onlySelect) {
                        DialogsActivity.this.floatingButtonContainer.setVisibility(0);
                        boolean unused3 = DialogsActivity.this.floatingHidden = true;
                        float unused4 = DialogsActivity.this.floatingButtonTranslation = (float) AndroidUtilities.dp(100.0f);
                        float unused5 = DialogsActivity.this.floatingButtonHideProgress = 1.0f;
                        DialogsActivity.this.updateFloatingButtonOffset();
                    }
                    DialogsActivity.this.showSearch(false, false, true);
                }
                DialogsActivity.this.updateProxyButton(false, false);
                DialogsActivity.this.updatePasscodeButton();
                if (DialogsActivity.this.menuDrawable != null) {
                    if (DialogsActivity.this.actionBar.getBackButton().getDrawable() != DialogsActivity.this.menuDrawable) {
                        DialogsActivity.this.actionBar.setBackButtonDrawable(DialogsActivity.this.menuDrawable);
                        DialogsActivity.this.menuDrawable.setRotation(0.0f, true);
                    }
                    DialogsActivity.this.actionBar.setBackButtonContentDescription(LocaleController.getString("AccDescrOpenMenu", NUM));
                }
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, true);
                ((SizeNotifierFrameLayout) DialogsActivity.this.fragmentView).invalidateBlur();
            }

            public void onTextChanged(EditText editText) {
                String text = editText.getText().toString();
                if (text.length() != 0 || ((DialogsActivity.this.searchViewPager.dialogsSearchAdapter != null && DialogsActivity.this.searchViewPager.dialogsSearchAdapter.hasRecentSearch()) || DialogsActivity.this.searchFiltersWasShowed)) {
                    boolean unused = DialogsActivity.this.searchWas = true;
                    if (!DialogsActivity.this.searchIsShowed) {
                        DialogsActivity.this.showSearch(true, false, true);
                    }
                }
                DialogsActivity.this.searchViewPager.onTextChanged(text);
            }

            public void onSearchFilterCleared(FiltersView.MediaFilterData filterData) {
                if (DialogsActivity.this.searchIsShowed) {
                    DialogsActivity.this.searchViewPager.removeSearchFilter(filterData);
                    DialogsActivity.this.searchViewPager.onTextChanged(DialogsActivity.this.searchItem.getSearchField().getText().toString());
                    DialogsActivity.this.updateFiltersView(true, (ArrayList<Object>) null, (ArrayList<FiltersView.DateData>) null, false, true);
                }
            }

            public boolean canToggleSearch() {
                return !DialogsActivity.this.actionBar.isActionModeShowed() && DialogsActivity.this.databaseMigrationHint == null;
            }
        });
        this.searchItem = actionBarMenuItemSearchListener;
        int i4 = this.initialDialogsType;
        if (i4 == 2 || i4 == 14) {
            actionBarMenuItemSearchListener.setVisibility(8);
        }
        this.searchItem.setSearchFieldHint(LocaleController.getString("Search", NUM));
        this.searchItem.setContentDescription(LocaleController.getString("Search", NUM));
        if (this.onlySelect) {
            this.actionBar.setBackButtonImage(NUM);
            int i5 = this.initialDialogsType;
            if (i5 == 3 && this.selectAlertString == null) {
                this.actionBar.setTitle(LocaleController.getString("ForwardTo", NUM));
            } else if (i5 == 10) {
                this.actionBar.setTitle(LocaleController.getString("SelectChats", NUM));
            } else {
                this.actionBar.setTitle(LocaleController.getString("SelectChat", NUM));
            }
            this.actionBar.setBackgroundColor(Theme.getColor("actionBarDefault"));
        } else {
            if (this.searchString == null && this.folderId == 0) {
                ActionBar actionBar = this.actionBar;
                MenuDrawable menuDrawable2 = new MenuDrawable();
                this.menuDrawable = menuDrawable2;
                actionBar.setBackButtonDrawable(menuDrawable2);
                this.actionBar.setBackButtonContentDescription(LocaleController.getString("AccDescrOpenMenu", NUM));
            } else {
                ActionBar actionBar2 = this.actionBar;
                BackDrawable backDrawable2 = new BackDrawable(false);
                this.backDrawable = backDrawable2;
                actionBar2.setBackButtonDrawable(backDrawable2);
            }
            if (this.folderId != 0) {
                this.actionBar.setTitle(LocaleController.getString("ArchivedChats", NUM));
            } else if (BuildVars.DEBUG_VERSION) {
                this.actionBar.setTitle(LocaleController.getString("AppNameBeta", NUM));
            } else {
                this.actionBar.setTitle(LocaleController.getString("AppName", NUM));
            }
            if (this.folderId == 0) {
                this.actionBar.setSupportsHolidayImage(true);
            }
        }
        if (!this.onlySelect) {
            this.actionBar.setAddToContainer(false);
            this.actionBar.setCastShadows(false);
            this.actionBar.setClipContent(true);
        }
        this.actionBar.setTitleActionRunnable(new DialogsActivity$$ExternalSyntheticLambda25(this));
        if (this.initialDialogsType == 0 && this.folderId == 0 && !this.onlySelect && TextUtils.isEmpty(this.searchString)) {
            this.scrimPaint = new Paint() {
                public void setAlpha(int a) {
                    super.setAlpha(a);
                    if (DialogsActivity.this.fragmentView != null) {
                        DialogsActivity.this.fragmentView.invalidate();
                    }
                }
            };
            AnonymousClass5 r0 = new FilterTabsView(context2) {
                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    boolean unused = DialogsActivity.this.maybeStartTracking = false;
                    return super.onInterceptTouchEvent(ev);
                }

                public void setTranslationY(float translationY) {
                    if (getTranslationY() != translationY) {
                        super.setTranslationY(translationY);
                        DialogsActivity.this.updateContextViewPosition();
                        if (DialogsActivity.this.fragmentView != null) {
                            DialogsActivity.this.fragmentView.invalidate();
                        }
                    }
                }

                /* access modifiers changed from: protected */
                public void onLayout(boolean changed, int l, int t, int r, int b) {
                    super.onLayout(changed, l, t, r, b);
                    if (DialogsActivity.this.scrimView != null) {
                        DialogsActivity.this.scrimView.getLocationInWindow(DialogsActivity.this.scrimViewLocation);
                        DialogsActivity.this.fragmentView.invalidate();
                    }
                }
            };
            this.filterTabsView = r0;
            r0.setVisibility(8);
            this.canShowFilterTabsView = false;
            this.filterTabsView.setDelegate(new FilterTabsView.FilterTabsViewDelegate() {
                private void showDeleteAlert(MessagesController.DialogFilter dialogFilter) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) DialogsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("FilterDelete", NUM));
                    builder.setMessage(LocaleController.getString("FilterDeleteAlert", NUM));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    builder.setPositiveButton(LocaleController.getString("Delete", NUM), new DialogsActivity$6$$ExternalSyntheticLambda0(this, dialogFilter));
                    AlertDialog alertDialog = builder.create();
                    DialogsActivity.this.showDialog(alertDialog);
                    TextView button = (TextView) alertDialog.getButton(-1);
                    if (button != null) {
                        button.setTextColor(Theme.getColor("dialogTextRed2"));
                    }
                }

                /* renamed from: lambda$showDeleteAlert$2$org-telegram-ui-DialogsActivity$6  reason: not valid java name */
                public /* synthetic */ void m3433lambda$showDeleteAlert$2$orgtelegramuiDialogsActivity$6(MessagesController.DialogFilter dialogFilter, DialogInterface dialog2, int which2) {
                    TLRPC.TL_messages_updateDialogFilter req = new TLRPC.TL_messages_updateDialogFilter();
                    req.id = dialogFilter.id;
                    DialogsActivity.this.getConnectionsManager().sendRequest(req, DialogsActivity$6$$ExternalSyntheticLambda3.INSTANCE);
                    DialogsActivity.this.getMessagesController().removeFilter(dialogFilter);
                    DialogsActivity.this.getMessagesStorage().deleteDialogFilter(dialogFilter);
                }

                static /* synthetic */ void lambda$showDeleteAlert$0() {
                }

                public void onSamePageSelected() {
                    DialogsActivity.this.scrollToTop();
                }

                public void onPageReorder(int fromId, int toId) {
                    for (int a = 0; a < DialogsActivity.this.viewPages.length; a++) {
                        if (DialogsActivity.this.viewPages[a].selectedType == fromId) {
                            int unused = DialogsActivity.this.viewPages[a].selectedType = toId;
                        } else if (DialogsActivity.this.viewPages[a].selectedType == toId) {
                            int unused2 = DialogsActivity.this.viewPages[a].selectedType = fromId;
                        }
                    }
                }

                public void onPageSelected(FilterTabsView.Tab tab, boolean forward) {
                    if (DialogsActivity.this.viewPages[0].selectedType != tab.id) {
                        if (tab.isLocked) {
                            DialogsActivity.this.filterTabsView.shakeLock(tab.id);
                            DialogsActivity dialogsActivity = DialogsActivity.this;
                            DialogsActivity dialogsActivity2 = DialogsActivity.this;
                            dialogsActivity.showDialog(new LimitReachedBottomSheet(dialogsActivity2, context2, 3, dialogsActivity2.currentAccount));
                            return;
                        }
                        ArrayList<MessagesController.DialogFilter> dialogFilters = DialogsActivity.this.getMessagesController().dialogFilters;
                        if (tab.isDefault || (tab.id >= 0 && tab.id < dialogFilters.size())) {
                            if (DialogsActivity.this.parentLayout != null) {
                                DialogsActivity.this.parentLayout.getDrawerLayoutContainer().setAllowOpenDrawerBySwipe(tab.id == DialogsActivity.this.filterTabsView.getFirstTabId() || SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) != 5);
                            }
                            int unused = DialogsActivity.this.viewPages[1].selectedType = tab.id;
                            DialogsActivity.this.viewPages[1].setVisibility(0);
                            DialogsActivity.this.viewPages[1].setTranslationX((float) DialogsActivity.this.viewPages[0].getMeasuredWidth());
                            DialogsActivity.this.showScrollbars(false);
                            DialogsActivity.this.switchToCurrentSelectedMode(true);
                            boolean unused2 = DialogsActivity.this.animatingForward = forward;
                        }
                    }
                }

                public boolean canPerformActions() {
                    return !DialogsActivity.this.searching;
                }

                public void onPageScrolled(float progress) {
                    if (progress != 1.0f || DialogsActivity.this.viewPages[1].getVisibility() == 0 || DialogsActivity.this.searching) {
                        if (DialogsActivity.this.animatingForward) {
                            DialogsActivity.this.viewPages[0].setTranslationX((-progress) * ((float) DialogsActivity.this.viewPages[0].getMeasuredWidth()));
                            DialogsActivity.this.viewPages[1].setTranslationX(((float) DialogsActivity.this.viewPages[0].getMeasuredWidth()) - (((float) DialogsActivity.this.viewPages[0].getMeasuredWidth()) * progress));
                        } else {
                            DialogsActivity.this.viewPages[0].setTranslationX(((float) DialogsActivity.this.viewPages[0].getMeasuredWidth()) * progress);
                            DialogsActivity.this.viewPages[1].setTranslationX((((float) DialogsActivity.this.viewPages[0].getMeasuredWidth()) * progress) - ((float) DialogsActivity.this.viewPages[0].getMeasuredWidth()));
                        }
                        if (progress == 1.0f) {
                            ViewPage tempPage = DialogsActivity.this.viewPages[0];
                            DialogsActivity.this.viewPages[0] = DialogsActivity.this.viewPages[1];
                            DialogsActivity.this.viewPages[1] = tempPage;
                            DialogsActivity.this.viewPages[1].setVisibility(8);
                            DialogsActivity.this.showScrollbars(true);
                            DialogsActivity.this.updateCounters(false);
                            DialogsActivity dialogsActivity = DialogsActivity.this;
                            dialogsActivity.checkListLoad(dialogsActivity.viewPages[0]);
                            DialogsActivity.this.viewPages[0].dialogsAdapter.resume();
                            DialogsActivity.this.viewPages[1].dialogsAdapter.pause();
                        }
                    }
                }

                public int getTabCounter(int tabId) {
                    if (tabId == DialogsActivity.this.filterTabsView.getDefaultTabId()) {
                        return DialogsActivity.this.getMessagesStorage().getMainUnreadCount();
                    }
                    ArrayList<MessagesController.DialogFilter> dialogFilters = DialogsActivity.this.getMessagesController().dialogFilters;
                    if (tabId < 0 || tabId >= dialogFilters.size()) {
                        return 0;
                    }
                    return DialogsActivity.this.getMessagesController().dialogFilters.get(tabId).unreadCount;
                }

                public boolean didSelectTab(FilterTabsView.TabView tabView, boolean selected) {
                    MessagesController.DialogFilter dialogFilter;
                    ScrollView scrollView;
                    FilterTabsView.TabView tabView2 = tabView;
                    if (DialogsActivity.this.actionBar.isActionModeShowed()) {
                        return false;
                    }
                    if (DialogsActivity.this.scrimPopupWindow != null) {
                        DialogsActivity.this.scrimPopupWindow.dismiss();
                        ActionBarPopupWindow unused = DialogsActivity.this.scrimPopupWindow = null;
                        ActionBarMenuSubItem[] unused2 = DialogsActivity.this.scrimPopupWindowItems = null;
                        return false;
                    }
                    final Rect rect = new Rect();
                    if (tabView.getId() == DialogsActivity.this.filterTabsView.getDefaultTabId()) {
                        dialogFilter = null;
                    } else {
                        dialogFilter = DialogsActivity.this.getMessagesController().dialogFilters.get(tabView.getId());
                    }
                    ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(DialogsActivity.this.getParentActivity());
                    popupLayout.setOnTouchListener(new View.OnTouchListener() {
                        private int[] pos = new int[2];

                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getActionMasked() == 0) {
                                if (DialogsActivity.this.scrimPopupWindow != null && DialogsActivity.this.scrimPopupWindow.isShowing()) {
                                    View contentView = DialogsActivity.this.scrimPopupWindow.getContentView();
                                    contentView.getLocationInWindow(this.pos);
                                    Rect rect = rect;
                                    int[] iArr = this.pos;
                                    rect.set(iArr[0], iArr[1], iArr[0] + contentView.getMeasuredWidth(), this.pos[1] + contentView.getMeasuredHeight());
                                    if (!rect.contains((int) event.getX(), (int) event.getY())) {
                                        DialogsActivity.this.scrimPopupWindow.dismiss();
                                    }
                                }
                            } else if (event.getActionMasked() == 4 && DialogsActivity.this.scrimPopupWindow != null && DialogsActivity.this.scrimPopupWindow.isShowing()) {
                                DialogsActivity.this.scrimPopupWindow.dismiss();
                            }
                            return false;
                        }
                    });
                    popupLayout.setDispatchKeyEventListener(new DialogsActivity$6$$ExternalSyntheticLambda4(this));
                    Rect backgroundPaddings = new Rect();
                    Drawable shadowDrawable = DialogsActivity.this.getParentActivity().getResources().getDrawable(NUM).mutate();
                    shadowDrawable.getPadding(backgroundPaddings);
                    popupLayout.setBackgroundDrawable(shadowDrawable);
                    popupLayout.setBackgroundColor(Theme.getColor("actionBarDefaultSubmenuBackground"));
                    LinearLayout linearLayout = new LinearLayout(DialogsActivity.this.getParentActivity());
                    if (Build.VERSION.SDK_INT >= 21) {
                        final LinearLayout linearLayout2 = linearLayout;
                        scrollView = new ScrollView(DialogsActivity.this.getParentActivity(), (AttributeSet) null, 0, NUM) {
                            /* access modifiers changed from: protected */
                            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                                setMeasuredDimension(linearLayout2.getMeasuredWidth(), getMeasuredHeight());
                            }
                        };
                    } else {
                        scrollView = new ScrollView(DialogsActivity.this.getParentActivity());
                    }
                    scrollView.setClipToPadding(false);
                    popupLayout.addView(scrollView, LayoutHelper.createFrame(-2, -2.0f));
                    linearLayout.setMinimumWidth(AndroidUtilities.dp(200.0f));
                    linearLayout.setOrientation(1);
                    ActionBarMenuSubItem[] unused3 = DialogsActivity.this.scrimPopupWindowItems = new ActionBarMenuSubItem[3];
                    int a = 0;
                    int N = tabView.getId() == DialogsActivity.this.filterTabsView.getDefaultTabId() ? 2 : 3;
                    while (a < N) {
                        ActionBarMenuSubItem cell = new ActionBarMenuSubItem(DialogsActivity.this.getParentActivity(), a == 0, a == N + -1);
                        if (a == 0) {
                            if (DialogsActivity.this.getMessagesController().dialogFilters.size() <= 1) {
                                a++;
                            } else {
                                cell.setTextAndIcon(LocaleController.getString("FilterReorder", NUM), NUM);
                            }
                        } else if (a != 1) {
                            cell.setTextAndIcon(LocaleController.getString("FilterDeleteItem", NUM), NUM);
                        } else if (N == 2) {
                            cell.setTextAndIcon(LocaleController.getString("FilterEditAll", NUM), NUM);
                        } else {
                            cell.setTextAndIcon(LocaleController.getString("FilterEdit", NUM), NUM);
                        }
                        DialogsActivity.this.scrimPopupWindowItems[a] = cell;
                        linearLayout.addView(cell);
                        cell.setOnClickListener(new DialogsActivity$6$$ExternalSyntheticLambda1(this, a, N, dialogFilter));
                        a++;
                    }
                    scrollView.addView(linearLayout, LayoutHelper.createScroll(-2, -2, 51));
                    ActionBarPopupWindow unused4 = DialogsActivity.this.scrimPopupWindow = new ActionBarPopupWindow(popupLayout, -2, -2) {
                        public void dismiss() {
                            super.dismiss();
                            if (DialogsActivity.this.scrimPopupWindow == this) {
                                ActionBarPopupWindow unused = DialogsActivity.this.scrimPopupWindow = null;
                                ActionBarMenuSubItem[] unused2 = DialogsActivity.this.scrimPopupWindowItems = null;
                                if (DialogsActivity.this.scrimAnimatorSet != null) {
                                    DialogsActivity.this.scrimAnimatorSet.cancel();
                                    AnimatorSet unused3 = DialogsActivity.this.scrimAnimatorSet = null;
                                }
                                AnimatorSet unused4 = DialogsActivity.this.scrimAnimatorSet = new AnimatorSet();
                                boolean unused5 = DialogsActivity.this.scrimViewAppearing = false;
                                ArrayList<Animator> animators = new ArrayList<>();
                                animators.add(ObjectAnimator.ofInt(DialogsActivity.this.scrimPaint, AnimationProperties.PAINT_ALPHA, new int[]{0}));
                                DialogsActivity.this.scrimAnimatorSet.playTogether(animators);
                                DialogsActivity.this.scrimAnimatorSet.setDuration(220);
                                DialogsActivity.this.scrimAnimatorSet.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animation) {
                                        if (DialogsActivity.this.scrimView != null) {
                                            DialogsActivity.this.scrimView.setBackground((Drawable) null);
                                            View unused = DialogsActivity.this.scrimView = null;
                                        }
                                        if (DialogsActivity.this.fragmentView != null) {
                                            DialogsActivity.this.fragmentView.invalidate();
                                        }
                                    }
                                });
                                DialogsActivity.this.scrimAnimatorSet.start();
                                if (Build.VERSION.SDK_INT >= 19) {
                                    DialogsActivity.this.getParentActivity().getWindow().getDecorView().setImportantForAccessibility(0);
                                }
                            }
                        }
                    };
                    Drawable unused5 = DialogsActivity.this.scrimViewBackground = Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), 0, Theme.getColor("actionBarDefault"));
                    DialogsActivity.this.scrimPopupWindow.setDismissAnimationDuration(220);
                    DialogsActivity.this.scrimPopupWindow.setOutsideTouchable(true);
                    DialogsActivity.this.scrimPopupWindow.setClippingEnabled(true);
                    DialogsActivity.this.scrimPopupWindow.setAnimationStyle(NUM);
                    DialogsActivity.this.scrimPopupWindow.setFocusable(true);
                    popupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
                    DialogsActivity.this.scrimPopupWindow.setInputMethodMode(2);
                    DialogsActivity.this.scrimPopupWindow.setSoftInputMode(0);
                    DialogsActivity.this.scrimPopupWindow.getContentView().setFocusableInTouchMode(true);
                    tabView2.getLocationInWindow(DialogsActivity.this.scrimViewLocation);
                    int popupX = (DialogsActivity.this.scrimViewLocation[0] + backgroundPaddings.left) - AndroidUtilities.dp(16.0f);
                    if (popupX < AndroidUtilities.dp(6.0f)) {
                        popupX = AndroidUtilities.dp(6.0f);
                    } else if (popupX > (DialogsActivity.this.fragmentView.getMeasuredWidth() - AndroidUtilities.dp(6.0f)) - popupLayout.getMeasuredWidth()) {
                        popupX = (DialogsActivity.this.fragmentView.getMeasuredWidth() - AndroidUtilities.dp(6.0f)) - popupLayout.getMeasuredWidth();
                    }
                    DialogsActivity.this.scrimPopupWindow.showAtLocation(DialogsActivity.this.fragmentView, 51, popupX, (DialogsActivity.this.scrimViewLocation[1] + tabView.getMeasuredHeight()) - AndroidUtilities.dp(12.0f));
                    View unused6 = DialogsActivity.this.scrimView = tabView2;
                    boolean unused7 = DialogsActivity.this.scrimViewSelected = selected;
                    DialogsActivity.this.fragmentView.invalidate();
                    if (DialogsActivity.this.scrimAnimatorSet != null) {
                        DialogsActivity.this.scrimAnimatorSet.cancel();
                    }
                    AnimatorSet unused8 = DialogsActivity.this.scrimAnimatorSet = new AnimatorSet();
                    boolean unused9 = DialogsActivity.this.scrimViewAppearing = true;
                    ArrayList<Animator> animators = new ArrayList<>();
                    animators.add(ObjectAnimator.ofInt(DialogsActivity.this.scrimPaint, AnimationProperties.PAINT_ALPHA, new int[]{0, 50}));
                    DialogsActivity.this.scrimAnimatorSet.playTogether(animators);
                    ArrayList<Animator> arrayList = animators;
                    int i = popupX;
                    DialogsActivity.this.scrimAnimatorSet.setDuration(150);
                    DialogsActivity.this.scrimAnimatorSet.start();
                    return true;
                }

                /* renamed from: lambda$didSelectTab$3$org-telegram-ui-DialogsActivity$6  reason: not valid java name */
                public /* synthetic */ void m3431lambda$didSelectTab$3$orgtelegramuiDialogsActivity$6(KeyEvent keyEvent) {
                    if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && DialogsActivity.this.scrimPopupWindow != null && DialogsActivity.this.scrimPopupWindow.isShowing()) {
                        DialogsActivity.this.scrimPopupWindow.dismiss();
                    }
                }

                /* renamed from: lambda$didSelectTab$4$org-telegram-ui-DialogsActivity$6  reason: not valid java name */
                public /* synthetic */ void m3432lambda$didSelectTab$4$orgtelegramuiDialogsActivity$6(int i, int N, MessagesController.DialogFilter dialogFilter, View v1) {
                    if (i == 0) {
                        DialogsActivity.this.resetScroll();
                        DialogsActivity.this.filterTabsView.setIsEditing(true);
                        DialogsActivity.this.showDoneItem(true);
                    } else if (i == 1) {
                        if (N == 2) {
                            DialogsActivity.this.presentFragment(new FiltersSetupActivity());
                        } else {
                            DialogsActivity.this.presentFragment(new FilterCreateActivity(dialogFilter));
                        }
                    } else if (i == 2) {
                        showDeleteAlert(dialogFilter);
                    }
                    if (DialogsActivity.this.scrimPopupWindow != null) {
                        DialogsActivity.this.scrimPopupWindow.dismiss();
                    }
                }

                public boolean isTabMenuVisible() {
                    return DialogsActivity.this.scrimPopupWindow != null && DialogsActivity.this.scrimPopupWindow.isShowing();
                }

                public void onDeletePressed(int id) {
                    showDeleteAlert(DialogsActivity.this.getMessagesController().dialogFilters.get(id));
                }
            });
        }
        int i6 = 4;
        if (this.allowSwitchAccount && UserConfig.getActivatedAccountsCount() > 1) {
            this.switchItem = menu.addItemWithWidth(1, 0, AndroidUtilities.dp(56.0f));
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
            BackupImageView imageView = new BackupImageView(context2);
            imageView.setRoundRadius(AndroidUtilities.dp(18.0f));
            this.switchItem.addView(imageView, LayoutHelper.createFrame(36, 36, 17));
            TLRPC.User user = getUserConfig().getCurrentUser();
            avatarDrawable.setInfo(user);
            imageView.getImageReceiver().setCurrentAccount(this.currentAccount);
            imageView.setImage(ImageLocation.getForUserOrChat(user, 1), "50_50", ImageLocation.getForUserOrChat(user, 2), "50_50", (user == null || user.photo == null || user.photo.strippedBitmap == null) ? avatarDrawable : user.photo.strippedBitmap, (Object) user);
            int a = 0;
            while (a < i6) {
                if (AccountInstance.getInstance(a).getUserConfig().getCurrentUser() != null) {
                    AccountSelectCell cell = new AccountSelectCell(context2, z3);
                    cell.setAccount(a, true);
                    this.switchItem.addSubItem(a + 10, (View) cell, AndroidUtilities.dp(230.0f), AndroidUtilities.dp(48.0f));
                }
                a++;
                i6 = 4;
                z3 = false;
            }
        }
        this.actionBar.setAllowOverlayTitle(true);
        RecyclerView recyclerView = this.sideMenu;
        if (recyclerView != null) {
            recyclerView.setBackgroundColor(Theme.getColor("chats_menuBackground"));
            this.sideMenu.setGlowColor(Theme.getColor("chats_menuBackground"));
            this.sideMenu.getAdapter().notifyDataSetChanged();
        }
        createActionMode((String) null);
        final ContentView contentView = new ContentView(context2);
        this.fragmentView = contentView;
        int pagesCount = (this.folderId == 0 && this.initialDialogsType == 0 && !this.onlySelect) ? 2 : 1;
        this.viewPages = new ViewPage[pagesCount];
        int a2 = 0;
        while (a2 < pagesCount) {
            final ViewPage viewPage = new ViewPage(context2) {
                public void setTranslationX(float translationX) {
                    if (getTranslationX() != translationX) {
                        super.setTranslationX(translationX);
                        if (DialogsActivity.this.tabsAnimationInProgress && DialogsActivity.this.viewPages[0] == this) {
                            DialogsActivity.this.filterTabsView.selectTabWithId(DialogsActivity.this.viewPages[1].selectedType, Math.abs(DialogsActivity.this.viewPages[0].getTranslationX()) / ((float) DialogsActivity.this.viewPages[0].getMeasuredWidth()));
                        }
                        contentView.invalidateBlur();
                    }
                }
            };
            contentView.addView(viewPage, LayoutHelper.createFrame(-1, -1.0f));
            int unused = viewPage.dialogsType = this.initialDialogsType;
            this.viewPages[a2] = viewPage;
            FlickerLoadingView unused2 = viewPage.progressView = new FlickerLoadingView(context2);
            viewPage.progressView.setViewType(7);
            viewPage.progressView.setVisibility(i3);
            viewPage.addView(viewPage.progressView, LayoutHelper.createFrame(-2, -2, 17));
            DialogsRecyclerView unused3 = viewPage.listView = new DialogsRecyclerView(context2, viewPage);
            viewPage.listView.setAccessibilityEnabled(false);
            viewPage.listView.setAnimateEmptyView(z4, 0);
            viewPage.listView.setClipToPadding(false);
            viewPage.listView.setPivotY(0.0f);
            DialogsItemAnimator unused4 = viewPage.dialogsItemAnimator = new DialogsItemAnimator(viewPage.listView) {
                public void onRemoveStarting(RecyclerView.ViewHolder item) {
                    super.onRemoveStarting(item);
                    if (viewPage.layoutManager.findFirstVisibleItemPosition() == 0) {
                        View v = viewPage.layoutManager.findViewByPosition(0);
                        if (v != null) {
                            v.invalidate();
                        }
                        if (viewPage.archivePullViewState == 2) {
                            int unused = viewPage.archivePullViewState = 1;
                        }
                        if (viewPage.pullForegroundDrawable != null) {
                            viewPage.pullForegroundDrawable.doNotShow();
                        }
                    }
                }

                public void onRemoveFinished(RecyclerView.ViewHolder item) {
                    if (DialogsActivity.this.dialogRemoveFinished == 2) {
                        int unused = DialogsActivity.this.dialogRemoveFinished = 1;
                    }
                }

                public void onAddFinished(RecyclerView.ViewHolder item) {
                    if (DialogsActivity.this.dialogInsertFinished == 2) {
                        int unused = DialogsActivity.this.dialogInsertFinished = 1;
                    }
                }

                public void onChangeFinished(RecyclerView.ViewHolder item, boolean oldItem) {
                    if (DialogsActivity.this.dialogChangeFinished == 2) {
                        int unused = DialogsActivity.this.dialogChangeFinished = 1;
                    }
                }

                /* access modifiers changed from: protected */
                public void onAllAnimationsDone() {
                    if (DialogsActivity.this.dialogRemoveFinished == 1 || DialogsActivity.this.dialogInsertFinished == 1 || DialogsActivity.this.dialogChangeFinished == 1) {
                        DialogsActivity.this.onDialogAnimationFinished();
                    }
                }
            };
            viewPage.listView.setItemAnimator(viewPage.dialogsItemAnimator);
            viewPage.listView.setVerticalScrollBarEnabled(z4);
            viewPage.listView.setInstantClick(z4);
            LinearLayoutManager unused5 = viewPage.layoutManager = new LinearLayoutManager(context2) {
                private boolean fixOffset;

                public void scrollToPositionWithOffset(int position, int offset) {
                    if (this.fixOffset) {
                        offset -= viewPage.listView.getPaddingTop();
                    }
                    super.scrollToPositionWithOffset(position, offset);
                }

                public void prepareForDrop(View view, View target, int x, int y) {
                    this.fixOffset = true;
                    super.prepareForDrop(view, target, x, y);
                    this.fixOffset = false;
                }

                public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                    if (!DialogsActivity.this.hasHiddenArchive() || position != 1) {
                        LinearSmoothScrollerCustom linearSmoothScroller = new LinearSmoothScrollerCustom(recyclerView.getContext(), 0);
                        linearSmoothScroller.setTargetPosition(position);
                        startSmoothScroll(linearSmoothScroller);
                        return;
                    }
                    super.smoothScrollToPosition(recyclerView, state, position);
                }

                public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
                    int measuredDy;
                    View view;
                    int i = dy;
                    RecyclerView.Recycler recycler2 = recycler;
                    RecyclerView.State state2 = state;
                    if (viewPage.listView.fastScrollAnimationRunning) {
                        return 0;
                    }
                    boolean isDragging = viewPage.listView.getScrollState() == 1;
                    int measuredDy2 = dy;
                    int pTop = viewPage.listView.getPaddingTop();
                    if (viewPage.dialogsType == 0 && !DialogsActivity.this.onlySelect && DialogsActivity.this.folderId == 0 && i < 0 && DialogsActivity.this.getMessagesController().hasHiddenArchive() && viewPage.archivePullViewState == 2) {
                        viewPage.listView.setOverScrollMode(0);
                        int currentPosition = viewPage.layoutManager.findFirstVisibleItemPosition();
                        if (currentPosition == 0 && (view = viewPage.layoutManager.findViewByPosition(currentPosition)) != null && view.getBottom() - pTop <= AndroidUtilities.dp(1.0f)) {
                            currentPosition = 1;
                        }
                        if (!isDragging) {
                            View view2 = viewPage.layoutManager.findViewByPosition(currentPosition);
                            if (view2 != null) {
                                int canScrollDy = (-(view2.getTop() - pTop)) + ((currentPosition - 1) * (AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f) + 1));
                                if (canScrollDy < Math.abs(dy)) {
                                    measuredDy2 = -canScrollDy;
                                }
                            }
                        } else if (currentPosition == 0) {
                            View v = viewPage.layoutManager.findViewByPosition(currentPosition);
                            float k = (((float) (v.getTop() - pTop)) / ((float) v.getMeasuredHeight())) + 1.0f;
                            if (k > 1.0f) {
                                k = 1.0f;
                            }
                            viewPage.listView.setOverScrollMode(2);
                            measuredDy2 = (int) (((float) measuredDy2) * (0.45f - (0.25f * k)));
                            if (measuredDy2 > -1) {
                                measuredDy2 = -1;
                            }
                            if (DialogsActivity.this.undoView[0].getVisibility() == 0) {
                                DialogsActivity.this.undoView[0].hide(true, 1);
                            }
                        }
                    }
                    if (viewPage.dialogsType == 0 && viewPage.listView.getViewOffset() != 0.0f && i > 0 && isDragging) {
                        float ty = ((float) ((int) viewPage.listView.getViewOffset())) - ((float) i);
                        if (ty < 0.0f) {
                            measuredDy = (int) ty;
                            ty = 0.0f;
                        } else {
                            measuredDy = 0;
                        }
                        viewPage.listView.setViewsOffset(ty);
                    }
                    if (viewPage.dialogsType != 0 || viewPage.archivePullViewState == 0 || !DialogsActivity.this.hasHiddenArchive()) {
                        return super.scrollVerticallyBy(measuredDy2, recycler2, state2);
                    }
                    int usedDy = super.scrollVerticallyBy(measuredDy2, recycler2, state2);
                    if (viewPage.pullForegroundDrawable != null) {
                        viewPage.pullForegroundDrawable.scrollDy = usedDy;
                    }
                    int currentPosition2 = viewPage.layoutManager.findFirstVisibleItemPosition();
                    View firstView = null;
                    if (currentPosition2 == 0) {
                        firstView = viewPage.layoutManager.findViewByPosition(currentPosition2);
                    }
                    if (currentPosition2 != 0 || firstView == null || firstView.getBottom() - pTop < AndroidUtilities.dp(4.0f)) {
                        long unused = DialogsActivity.this.startArchivePullingTime = 0;
                        boolean unused2 = DialogsActivity.this.canShowHiddenArchive = false;
                        int unused3 = viewPage.archivePullViewState = 2;
                        if (viewPage.pullForegroundDrawable != null) {
                            viewPage.pullForegroundDrawable.resetText();
                            viewPage.pullForegroundDrawable.pullProgress = 0.0f;
                            viewPage.pullForegroundDrawable.setListView(viewPage.listView);
                        }
                    } else {
                        if (DialogsActivity.this.startArchivePullingTime == 0) {
                            long unused4 = DialogsActivity.this.startArchivePullingTime = System.currentTimeMillis();
                        }
                        if (viewPage.archivePullViewState == 2 && viewPage.pullForegroundDrawable != null) {
                            viewPage.pullForegroundDrawable.showHidden();
                        }
                        float k2 = (((float) (firstView.getTop() - pTop)) / ((float) firstView.getMeasuredHeight())) + 1.0f;
                        if (k2 > 1.0f) {
                            k2 = 1.0f;
                        }
                        boolean canShowInternal = k2 > 0.85f && System.currentTimeMillis() - DialogsActivity.this.startArchivePullingTime > 220;
                        if (DialogsActivity.this.canShowHiddenArchive != canShowInternal) {
                            boolean unused5 = DialogsActivity.this.canShowHiddenArchive = canShowInternal;
                            if (viewPage.archivePullViewState == 2) {
                                int i2 = pTop;
                                viewPage.listView.performHapticFeedback(3, 2);
                                if (viewPage.pullForegroundDrawable != null) {
                                    viewPage.pullForegroundDrawable.colorize(canShowInternal);
                                }
                            }
                        }
                        if (viewPage.archivePullViewState == 2 && measuredDy2 - usedDy != 0 && i < 0 && isDragging) {
                            viewPage.listView.setViewsOffset(viewPage.listView.getViewOffset() - ((((float) i) * 0.2f) * (1.0f - (viewPage.listView.getViewOffset() / ((float) PullForegroundDrawable.getMaxOverscroll())))));
                        }
                        if (viewPage.pullForegroundDrawable != null) {
                            viewPage.pullForegroundDrawable.pullProgress = k2;
                            viewPage.pullForegroundDrawable.setListView(viewPage.listView);
                        }
                    }
                    if (firstView != null) {
                        firstView.invalidate();
                    }
                    return usedDy;
                }

                public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                    if (BuildVars.DEBUG_PRIVATE_VERSION) {
                        try {
                            super.onLayoutChildren(recycler, state);
                        } catch (IndexOutOfBoundsException e) {
                            throw new RuntimeException("Inconsistency detected. dialogsListIsFrozen=" + DialogsActivity.this.dialogsListFrozen + " lastUpdateAction=" + DialogsActivity.this.debugLastUpdateAction);
                        }
                    } else {
                        try {
                            super.onLayoutChildren(recycler, state);
                        } catch (IndexOutOfBoundsException e2) {
                            FileLog.e((Throwable) e2);
                            AndroidUtilities.runOnUIThread(new DialogsActivity$9$$ExternalSyntheticLambda0(viewPage));
                        }
                    }
                }
            };
            viewPage.layoutManager.setOrientation(z4 ? 1 : 0);
            viewPage.listView.setLayoutManager(viewPage.layoutManager);
            viewPage.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
            viewPage.addView(viewPage.listView, LayoutHelper.createFrame(-1, -1.0f));
            viewPage.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new DialogsActivity$$ExternalSyntheticLambda52(this, viewPage));
            viewPage.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListenerExtended) new RecyclerListView.OnItemLongClickListenerExtended() {
                public boolean onItemClick(View view, int position, float x, float y) {
                    if (DialogsActivity.this.filterTabsView != null && DialogsActivity.this.filterTabsView.getVisibility() == 0 && DialogsActivity.this.filterTabsView.isEditing()) {
                        return false;
                    }
                    return DialogsActivity.this.onItemLongClick(viewPage.listView, view, position, x, y, viewPage.dialogsType, viewPage.dialogsAdapter);
                }

                public void onMove(float dx, float dy) {
                    if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                        DialogsActivity.this.movePreviewFragment(dy);
                    }
                }

                public void onLongClickRelease() {
                    if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                        DialogsActivity.this.finishPreviewFragment();
                    }
                }
            });
            SwipeController unused6 = viewPage.swipeController = new SwipeController(viewPage);
            RecyclerItemsEnterAnimator unused7 = viewPage.recyclerItemsEnterAnimator = new RecyclerItemsEnterAnimator(viewPage.listView, false);
            ItemTouchHelper unused8 = viewPage.itemTouchhelper = new ItemTouchHelper(viewPage.swipeController);
            viewPage.itemTouchhelper.attachToRecyclerView(viewPage.listView);
            viewPage.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                private boolean wasManualScroll;

                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == 1) {
                        this.wasManualScroll = true;
                        boolean unused = DialogsActivity.this.scrollingManually = true;
                    } else {
                        boolean unused2 = DialogsActivity.this.scrollingManually = false;
                    }
                    if (newState == 0) {
                        this.wasManualScroll = false;
                        boolean unused3 = DialogsActivity.this.disableActionBarScrolling = false;
                        if (DialogsActivity.this.waitingForScrollFinished) {
                            boolean unused4 = DialogsActivity.this.waitingForScrollFinished = false;
                            if (DialogsActivity.this.updatePullAfterScroll) {
                                viewPage.listView.updatePullState();
                                boolean unused5 = DialogsActivity.this.updatePullAfterScroll = false;
                            }
                            viewPage.dialogsAdapter.notifyDataSetChanged();
                        }
                        if (DialogsActivity.this.filterTabsView != null && DialogsActivity.this.filterTabsView.getVisibility() == 0 && DialogsActivity.this.viewPages[0].listView == recyclerView) {
                            int scrollY = (int) (-DialogsActivity.this.actionBar.getTranslationY());
                            int actionBarHeight = ActionBar.getCurrentActionBarHeight();
                            if (scrollY != 0 && scrollY != actionBarHeight) {
                                if (scrollY < actionBarHeight / 2) {
                                    recyclerView.smoothScrollBy(0, -scrollY);
                                } else if (DialogsActivity.this.viewPages[0].listView.canScrollVertically(1)) {
                                    recyclerView.smoothScrollBy(0, actionBarHeight - scrollY);
                                }
                            }
                        }
                    }
                }

                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    View child;
                    int firstVisibleItem;
                    boolean goingDown;
                    viewPage.dialogsItemAnimator.onListScroll(-dy);
                    DialogsActivity.this.checkListLoad(viewPage);
                    if (!(DialogsActivity.this.initialDialogsType == 10 || !this.wasManualScroll || DialogsActivity.this.floatingButtonContainer.getVisibility() == 8 || recyclerView.getChildCount() <= 0 || (firstVisibleItem = viewPage.layoutManager.findFirstVisibleItemPosition()) == -1)) {
                        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(firstVisibleItem);
                        if (!DialogsActivity.this.hasHiddenArchive() || !(holder == null || holder.getAdapterPosition() == 0)) {
                            int firstViewTop = 0;
                            if (holder != null) {
                                firstViewTop = holder.itemView.getTop();
                            }
                            boolean changed = true;
                            if (DialogsActivity.this.prevPosition == firstVisibleItem) {
                                int topDelta = DialogsActivity.this.prevTop - firstViewTop;
                                goingDown = firstViewTop < DialogsActivity.this.prevTop;
                                changed = Math.abs(topDelta) > 1;
                            } else {
                                goingDown = firstVisibleItem > DialogsActivity.this.prevPosition;
                            }
                            if (changed && DialogsActivity.this.scrollUpdated && (goingDown || DialogsActivity.this.scrollingManually)) {
                                DialogsActivity.this.hideFloatingButton(goingDown);
                            }
                            int unused = DialogsActivity.this.prevPosition = firstVisibleItem;
                            int unused2 = DialogsActivity.this.prevTop = firstViewTop;
                            boolean unused3 = DialogsActivity.this.scrollUpdated = true;
                        }
                    }
                    if (DialogsActivity.this.filterTabsView != null && DialogsActivity.this.filterTabsView.getVisibility() == 0 && recyclerView == DialogsActivity.this.viewPages[0].listView && !DialogsActivity.this.searching && !DialogsActivity.this.actionBar.isActionModeShowed() && !DialogsActivity.this.disableActionBarScrolling && DialogsActivity.this.filterTabsViewIsVisible) {
                        if (dy > 0 && DialogsActivity.this.hasHiddenArchive() && DialogsActivity.this.viewPages[0].dialogsType == 0 && (child = recyclerView.getChildAt(0)) != null && recyclerView.getChildViewHolder(child).getAdapterPosition() == 0) {
                            int visiblePartAfterScroll = child.getMeasuredHeight() + (child.getTop() - recyclerView.getPaddingTop());
                            if (visiblePartAfterScroll + dy > 0) {
                                if (visiblePartAfterScroll < 0) {
                                    dy = -visiblePartAfterScroll;
                                } else {
                                    return;
                                }
                            }
                        }
                        float currentTranslation = DialogsActivity.this.actionBar.getTranslationY();
                        float newTranslation = currentTranslation - ((float) dy);
                        if (newTranslation < ((float) (-ActionBar.getCurrentActionBarHeight()))) {
                            newTranslation = (float) (-ActionBar.getCurrentActionBarHeight());
                        } else if (newTranslation > 0.0f) {
                            newTranslation = 0.0f;
                        }
                        if (newTranslation != currentTranslation) {
                            DialogsActivity.this.setScrollY(newTranslation);
                        }
                    }
                    if (DialogsActivity.this.fragmentView != null) {
                        ((SizeNotifierFrameLayout) DialogsActivity.this.fragmentView).invalidateBlur();
                    }
                }
            });
            int unused9 = viewPage.archivePullViewState = SharedConfig.archiveHidden ? 2 : 0;
            if (viewPage.pullForegroundDrawable == null && this.folderId == 0) {
                PullForegroundDrawable unused10 = viewPage.pullForegroundDrawable = new PullForegroundDrawable(LocaleController.getString("AccSwipeForArchive", NUM), LocaleController.getString("AccReleaseForArchive", NUM)) {
                    /* access modifiers changed from: protected */
                    public float getViewOffset() {
                        return viewPage.listView.getViewOffset();
                    }
                };
                if (hasHiddenArchive()) {
                    viewPage.pullForegroundDrawable.showHidden();
                } else {
                    viewPage.pullForegroundDrawable.doNotShow();
                }
                viewPage.pullForegroundDrawable.setWillDraw(viewPage.archivePullViewState != 0);
            }
            ViewPage viewPage2 = viewPage;
            ActionBarMenu menu2 = menu;
            AnonymousClass14 r14 = r0;
            int pagesCount2 = pagesCount;
            int a3 = a2;
            final ViewPage viewPage3 = viewPage2;
            AnonymousClass14 r02 = new DialogsAdapter(this, this, context, viewPage.dialogsType, this.folderId, this.onlySelect, this.selectedDialogs, this.currentAccount) {
                final /* synthetic */ DialogsActivity this$0;

                {
                    this.this$0 = this$0;
                }

                public void notifyDataSetChanged() {
                    int unused = viewPage3.lastItemsCount = getItemCount();
                    try {
                        super.notifyDataSetChanged();
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
            };
            ViewPage viewPage4 = viewPage2;
            DialogsAdapter unused11 = viewPage4.dialogsAdapter = r14;
            viewPage4.dialogsAdapter.setForceShowEmptyCell(this.afterSignup);
            if (AndroidUtilities.isTablet() && this.openedDialogId != 0) {
                viewPage4.dialogsAdapter.setOpenedDialogId(this.openedDialogId);
            }
            viewPage4.dialogsAdapter.setArchivedPullDrawable(viewPage4.pullForegroundDrawable);
            viewPage4.listView.setAdapter(viewPage4.dialogsAdapter);
            viewPage4.listView.setEmptyView(this.folderId == 0 ? viewPage4.progressView : null);
            RecyclerAnimationScrollHelper unused12 = viewPage4.scrollHelper = new RecyclerAnimationScrollHelper(viewPage4.listView, viewPage4.layoutManager);
            if (a3 != 0) {
                this.viewPages[a3].setVisibility(8);
            }
            a2 = a3 + 1;
            menu = menu2;
            pagesCount = pagesCount2;
            z4 = true;
            i3 = 8;
        }
        int i7 = a2;
        ActionBarMenu actionBarMenu = menu;
        int i8 = pagesCount;
        if (this.searchString != null) {
            type = 2;
        } else if (!this.onlySelect) {
            type = 1;
        } else {
            type = 0;
        }
        SearchViewPager searchViewPager2 = new SearchViewPager(context, this, type, this.initialDialogsType, this.folderId, new SearchViewPager.ChatPreviewDelegate() {
            public void startChatPreview(RecyclerListView listView, DialogCell cell) {
                DialogsActivity.this.showChatPreview(cell);
            }

            public void move(float dy) {
                if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                    DialogsActivity.this.movePreviewFragment(dy);
                }
            }

            public void finish() {
                if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                    DialogsActivity.this.finishPreviewFragment();
                }
            }
        });
        this.searchViewPager = searchViewPager2;
        contentView.addView(searchViewPager2);
        this.searchViewPager.dialogsSearchAdapter.setDelegate(new DialogsSearchAdapter.DialogsSearchAdapterDelegate() {
            public void searchStateChanged(boolean search, boolean animated) {
                if (DialogsActivity.this.searchViewPager.emptyView.getVisibility() == 0) {
                    animated = true;
                }
                if (DialogsActivity.this.searching && DialogsActivity.this.searchWas && DialogsActivity.this.searchViewPager.emptyView != null) {
                    if (search || DialogsActivity.this.searchViewPager.dialogsSearchAdapter.getItemCount() != 0) {
                        DialogsActivity.this.searchViewPager.emptyView.showProgress(true, animated);
                    } else {
                        DialogsActivity.this.searchViewPager.emptyView.showProgress(false, animated);
                    }
                }
                if (search && DialogsActivity.this.searchViewPager.dialogsSearchAdapter.getItemCount() == 0) {
                    DialogsActivity.this.searchViewPager.cancelEnterAnimation();
                }
            }

            public void didPressedOnSubDialog(long did) {
                if (!DialogsActivity.this.onlySelect) {
                    Bundle args = new Bundle();
                    if (DialogObject.isUserDialog(did)) {
                        args.putLong("user_id", did);
                    } else {
                        args.putLong("chat_id", -did);
                    }
                    DialogsActivity.this.closeSearch();
                    if (AndroidUtilities.isTablet() && DialogsActivity.this.viewPages != null) {
                        for (ViewPage access$9100 : DialogsActivity.this.viewPages) {
                            access$9100.dialogsAdapter.setOpenedDialogId(DialogsActivity.this.openedDialogId = did);
                        }
                        DialogsActivity.this.updateVisibleRows(MessagesController.UPDATE_MASK_SELECT_DIALOG);
                    }
                    if (DialogsActivity.this.searchString != null) {
                        if (DialogsActivity.this.getMessagesController().checkCanOpenChat(args, DialogsActivity.this)) {
                            DialogsActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                            DialogsActivity.this.presentFragment(new ChatActivity(args));
                        }
                    } else if (DialogsActivity.this.getMessagesController().checkCanOpenChat(args, DialogsActivity.this)) {
                        DialogsActivity.this.presentFragment(new ChatActivity(args));
                    }
                } else if (DialogsActivity.this.validateSlowModeDialog(did)) {
                    if (!DialogsActivity.this.selectedDialogs.isEmpty()) {
                        DialogsActivity.this.findAndUpdateCheckBox(did, DialogsActivity.this.addOrRemoveSelectedDialog(did, (View) null));
                        DialogsActivity.this.updateSelectedCount();
                        DialogsActivity.this.actionBar.closeSearchField();
                        return;
                    }
                    DialogsActivity.this.didSelectResult(did, true, false);
                }
            }

            public void needRemoveHint(long did) {
                TLRPC.User user;
                if (DialogsActivity.this.getParentActivity() != null && (user = DialogsActivity.this.getMessagesController().getUser(Long.valueOf(did))) != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) DialogsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("ChatHintsDeleteAlertTitle", NUM));
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("ChatHintsDeleteAlert", NUM, ContactsController.formatName(user.first_name, user.last_name))));
                    builder.setPositiveButton(LocaleController.getString("StickersRemove", NUM), new DialogsActivity$16$$ExternalSyntheticLambda2(this, did));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    AlertDialog dialog = builder.create();
                    DialogsActivity.this.showDialog(dialog);
                    TextView button = (TextView) dialog.getButton(-1);
                    if (button != null) {
                        button.setTextColor(Theme.getColor("dialogTextRed2"));
                    }
                }
            }

            /* renamed from: lambda$needRemoveHint$0$org-telegram-ui-DialogsActivity$16  reason: not valid java name */
            public /* synthetic */ void m3426lambda$needRemoveHint$0$orgtelegramuiDialogsActivity$16(long did, DialogInterface dialogInterface, int i) {
                DialogsActivity.this.getMediaDataController().removePeer(did);
            }

            public void needClearList() {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) DialogsActivity.this.getParentActivity());
                if (!DialogsActivity.this.searchViewPager.dialogsSearchAdapter.isSearchWas() || !DialogsActivity.this.searchViewPager.dialogsSearchAdapter.isRecentSearchDisplayed()) {
                    builder.setTitle(LocaleController.getString("ClearSearchAlertTitle", NUM));
                    builder.setMessage(LocaleController.getString("ClearSearchAlert", NUM));
                    builder.setPositiveButton(LocaleController.getString("ClearButton", NUM).toUpperCase(), new DialogsActivity$16$$ExternalSyntheticLambda1(this));
                } else {
                    builder.setTitle(LocaleController.getString("ClearSearchAlertPartialTitle", NUM));
                    builder.setMessage(LocaleController.formatPluralString("ClearSearchAlertPartial", DialogsActivity.this.searchViewPager.dialogsSearchAdapter.getRecentResultsCount(), new Object[0]));
                    builder.setPositiveButton(LocaleController.getString("Clear", NUM).toUpperCase(), new DialogsActivity$16$$ExternalSyntheticLambda0(this));
                }
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                AlertDialog dialog = builder.create();
                DialogsActivity.this.showDialog(dialog);
                TextView button = (TextView) dialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            }

            /* renamed from: lambda$needClearList$1$org-telegram-ui-DialogsActivity$16  reason: not valid java name */
            public /* synthetic */ void m3424lambda$needClearList$1$orgtelegramuiDialogsActivity$16(DialogInterface dialogInterface, int i) {
                DialogsActivity.this.searchViewPager.dialogsSearchAdapter.clearRecentSearch();
            }

            /* renamed from: lambda$needClearList$2$org-telegram-ui-DialogsActivity$16  reason: not valid java name */
            public /* synthetic */ void m3425lambda$needClearList$2$orgtelegramuiDialogsActivity$16(DialogInterface dialogInterface, int i) {
                if (DialogsActivity.this.searchViewPager.dialogsSearchAdapter.isRecentSearchDisplayed()) {
                    DialogsActivity.this.searchViewPager.dialogsSearchAdapter.clearRecentSearch();
                } else {
                    DialogsActivity.this.searchViewPager.dialogsSearchAdapter.clearRecentHashtags();
                }
            }

            public void runResultsEnterAnimation() {
                if (DialogsActivity.this.searchViewPager != null) {
                    DialogsActivity.this.searchViewPager.runResultsEnterAnimation();
                }
            }

            public boolean isSelected(long dialogId) {
                return DialogsActivity.this.selectedDialogs.contains(Long.valueOf(dialogId));
            }
        });
        this.searchViewPager.searchListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new DialogsActivity$$ExternalSyntheticLambda50(this));
        this.searchViewPager.searchListView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListenerExtended) new RecyclerListView.OnItemLongClickListenerExtended() {
            public boolean onItemClick(View view, int position, float x, float y) {
                DialogsActivity dialogsActivity = DialogsActivity.this;
                return dialogsActivity.onItemLongClick(dialogsActivity.searchViewPager.searchListView, view, position, x, y, -1, DialogsActivity.this.searchViewPager.dialogsSearchAdapter);
            }

            public void onMove(float dx, float dy) {
                if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                    DialogsActivity.this.movePreviewFragment(dy);
                }
            }

            public void onLongClickRelease() {
                if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                    DialogsActivity.this.finishPreviewFragment();
                }
            }
        });
        this.searchViewPager.setFilteredSearchViewDelegate(new DialogsActivity$$ExternalSyntheticLambda53(this));
        this.searchViewPager.setVisibility(8);
        FiltersView filtersView2 = new FiltersView(getParentActivity(), (Theme.ResourcesProvider) null);
        this.filtersView = filtersView2;
        filtersView2.setOnItemClickListener((RecyclerListView.OnItemClickListener) new DialogsActivity$$ExternalSyntheticLambda51(this));
        contentView.addView(this.filtersView, LayoutHelper.createFrame(-1, -2, 48));
        this.filtersView.setVisibility(8);
        FrameLayout frameLayout = new FrameLayout(context2);
        this.floatingButtonContainer = frameLayout;
        frameLayout.setVisibility(((!this.onlySelect || this.initialDialogsType == 10) && this.folderId == 0) ? 0 : 8);
        contentView.addView(this.floatingButtonContainer, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, (float) (Build.VERSION.SDK_INT >= 21 ? 56 : 60), (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 14.0f));
        this.floatingButtonContainer.setOnClickListener(new DialogsActivity$$ExternalSyntheticLambda13(this));
        RLottieImageView rLottieImageView = new RLottieImageView(context2);
        this.floatingButton = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), PorterDuff.Mode.MULTIPLY));
        if (this.initialDialogsType == 10) {
            this.floatingButton.setImageResource(NUM);
            this.floatingButtonContainer.setContentDescription(LocaleController.getString("Done", NUM));
        } else {
            this.floatingButton.setAnimation(NUM, 52, 52);
            this.floatingButtonContainer.setContentDescription(LocaleController.getString("NewMessageTitle", NUM));
        }
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            z = true;
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButtonContainer, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButtonContainer, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.floatingButtonContainer.setStateListAnimator(animator);
            this.floatingButtonContainer.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        } else {
            z = true;
        }
        Drawable drawable2 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(NUM).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable2, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        } else {
            drawable = drawable2;
        }
        updateFloatingButtonColor();
        this.floatingButtonContainer.addView(this.floatingButton, LayoutHelper.createFrame(-1, -1.0f));
        RadialProgressView radialProgressView = new RadialProgressView(context2);
        this.floatingProgressView = radialProgressView;
        radialProgressView.setProgressColor(Theme.getColor("chats_actionIcon"));
        this.floatingProgressView.setScaleX(0.1f);
        this.floatingProgressView.setScaleY(0.1f);
        this.floatingProgressView.setAlpha(0.0f);
        this.floatingProgressView.setVisibility(8);
        this.floatingProgressView.setSize(AndroidUtilities.dp(22.0f));
        this.floatingButtonContainer.addView(this.floatingProgressView, LayoutHelper.createFrame(-1, -1.0f));
        this.searchTabsView = null;
        if (!this.onlySelect && this.initialDialogsType == 0) {
            FragmentContextView fragmentContextView2 = new FragmentContextView(context2, this, z);
            this.fragmentLocationContextView = fragmentContextView2;
            fragmentContextView2.setLayoutParams(LayoutHelper.createFrame(-1, 38.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
            contentView.addView(this.fragmentLocationContextView);
            AnonymousClass19 r03 = new FragmentContextView(context2, this, false) {
                /* access modifiers changed from: protected */
                public void playbackSpeedChanged(float value) {
                    if (Math.abs(value - 1.0f) > 0.001f || Math.abs(value - 1.8f) > 0.001f) {
                        DialogsActivity.this.getUndoView().showWithAction(0, Math.abs(value - 1.0f) > 0.001f ? 50 : 51, Float.valueOf(value), (Runnable) null, (Runnable) null);
                    }
                }
            };
            this.fragmentContextView = r03;
            r03.setLayoutParams(LayoutHelper.createFrame(-1, 38.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
            contentView.addView(this.fragmentContextView);
            this.fragmentContextView.setAdditionalContextView(this.fragmentLocationContextView);
            this.fragmentLocationContextView.setAdditionalContextView(this.fragmentContextView);
            i = 21;
            z2 = true;
        } else if (this.initialDialogsType == 3) {
            ChatActivityEnterView chatActivityEnterView = this.commentView;
            if (chatActivityEnterView != null) {
                chatActivityEnterView.onDestroy();
            }
            z2 = true;
            i = 21;
            this.commentView = new ChatActivityEnterView(getParentActivity(), contentView, (ChatActivity) null, false) {
                public boolean dispatchTouchEvent(MotionEvent ev) {
                    if (ev.getAction() == 0) {
                        AndroidUtilities.requestAdjustResize(DialogsActivity.this.getParentActivity(), DialogsActivity.this.classGuid);
                    }
                    return super.dispatchTouchEvent(ev);
                }

                public void setTranslationY(float translationY) {
                    super.setTranslationY(translationY);
                }
            };
            contentView.setClipChildren(false);
            contentView.setClipToPadding(false);
            this.commentView.allowBlur = false;
            this.commentView.forceSmoothKeyboard(true);
            this.commentView.setAllowStickersAndGifs(false, false);
            this.commentView.setForceShowSendButton(true, false);
            this.commentView.setPadding(0, 0, AndroidUtilities.dp(20.0f), 0);
            this.commentView.setVisibility(8);
            this.commentView.getSendButton().setAlpha(0.0f);
            View view = new View(getParentActivity());
            this.commentViewBg = view;
            view.setBackgroundColor(getThemedColor("chat_messagePanelBackground"));
            contentView.addView(this.commentViewBg, LayoutHelper.createFrame(-1, 1600.0f, 87, 0.0f, 0.0f, 0.0f, -1600.0f));
            contentView.addView(this.commentView, LayoutHelper.createFrame(-1, -2, 83));
            this.commentView.setDelegate(new ChatActivityEnterView.ChatActivityEnterViewDelegate() {
                public /* synthetic */ int getContentViewHeight() {
                    return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$getContentViewHeight(this);
                }

                public /* synthetic */ TLRPC.TL_channels_sendAsPeers getSendAsPeers() {
                    return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$getSendAsPeers(this);
                }

                public /* synthetic */ boolean hasForwardingMessages() {
                    return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$hasForwardingMessages(this);
                }

                public /* synthetic */ boolean hasScheduledMessages() {
                    return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$hasScheduledMessages(this);
                }

                public /* synthetic */ int measureKeyboardHeight() {
                    return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$measureKeyboardHeight(this);
                }

                public /* synthetic */ void onTrendingStickersShowed(boolean z) {
                    ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$onTrendingStickersShowed(this, z);
                }

                public /* synthetic */ void openScheduledMessages() {
                    ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$openScheduledMessages(this);
                }

                public /* synthetic */ void prepareMessageSending() {
                    ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$prepareMessageSending(this);
                }

                public /* synthetic */ void scrollToSendingMessage() {
                    ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$scrollToSendingMessage(this);
                }

                public void onMessageSend(CharSequence message, boolean notify, int scheduleDate) {
                    if (DialogsActivity.this.delegate != null && !DialogsActivity.this.selectedDialogs.isEmpty()) {
                        DialogsActivityDelegate access$25400 = DialogsActivity.this.delegate;
                        DialogsActivity dialogsActivity = DialogsActivity.this;
                        access$25400.didSelectDialogs(dialogsActivity, dialogsActivity.selectedDialogs, message, false);
                    }
                }

                public void onSwitchRecordMode(boolean video) {
                }

                public void onTextSelectionChanged(int start, int end) {
                }

                public void bottomPanelTranslationYChanged(float translation) {
                }

                public void onStickersExpandedChange() {
                }

                public void onPreAudioVideoRecord() {
                }

                public void onTextChanged(CharSequence text, boolean bigChange) {
                }

                public void onTextSpansChanged(CharSequence text) {
                }

                public void needSendTyping() {
                }

                public void onAttachButtonHidden() {
                }

                public void onAttachButtonShow() {
                }

                public void onMessageEditEnd(boolean loading) {
                }

                public void onWindowSizeChanged(int size) {
                }

                public void onStickersTab(boolean opened) {
                }

                public void didPressAttachButton() {
                }

                public void needStartRecordVideo(int state, boolean notify, int scheduleDate) {
                }

                public void needChangeVideoPreviewState(int state, float seekProgress) {
                }

                public void needStartRecordAudio(int state) {
                }

                public void needShowMediaBanHint() {
                }

                public void onUpdateSlowModeButton(View button, boolean show, CharSequence time) {
                }

                public void onSendLongClick() {
                }

                public void onAudioVideoInterfaceUpdated() {
                }
            });
            AnonymousClass22 r04 = new FrameLayout(context2) {
                public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                    super.onInitializeAccessibilityNodeInfo(info);
                    info.setText(LocaleController.formatPluralString("AccDescrShareInChats", DialogsActivity.this.selectedDialogs.size(), new Object[0]));
                    info.setClassName(Button.class.getName());
                    info.setLongClickable(true);
                    info.setClickable(true);
                }
            };
            this.writeButtonContainer = r04;
            r04.setFocusable(true);
            this.writeButtonContainer.setFocusableInTouchMode(true);
            this.writeButtonContainer.setVisibility(4);
            this.writeButtonContainer.setScaleX(0.2f);
            this.writeButtonContainer.setScaleY(0.2f);
            this.writeButtonContainer.setAlpha(0.0f);
            contentView.addView(this.writeButtonContainer, LayoutHelper.createFrame(60, 60.0f, 85, 0.0f, 0.0f, 6.0f, 10.0f));
            this.textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
            this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            AnonymousClass23 r05 = new View(context2) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    String text = String.format("%d", new Object[]{Integer.valueOf(Math.max(1, DialogsActivity.this.selectedDialogs.size()))});
                    int textSize = (int) Math.ceil((double) DialogsActivity.this.textPaint.measureText(text));
                    int size = Math.max(AndroidUtilities.dp(16.0f) + textSize, AndroidUtilities.dp(24.0f));
                    int cx = getMeasuredWidth() / 2;
                    int measuredHeight = getMeasuredHeight() / 2;
                    DialogsActivity.this.textPaint.setColor(DialogsActivity.this.getThemedColor("dialogRoundCheckBoxCheck"));
                    DialogsActivity.this.paint.setColor(DialogsActivity.this.getThemedColor(Theme.isCurrentThemeDark() ? "voipgroup_inviteMembersBackground" : "dialogBackground"));
                    DialogsActivity.this.rect.set((float) (cx - (size / 2)), 0.0f, (float) ((size / 2) + cx), (float) getMeasuredHeight());
                    canvas.drawRoundRect(DialogsActivity.this.rect, (float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(12.0f), DialogsActivity.this.paint);
                    DialogsActivity.this.paint.setColor(DialogsActivity.this.getThemedColor("dialogRoundCheckBox"));
                    DialogsActivity.this.rect.set((float) ((cx - (size / 2)) + AndroidUtilities.dp(2.0f)), (float) AndroidUtilities.dp(2.0f), (float) (((size / 2) + cx) - AndroidUtilities.dp(2.0f)), (float) (getMeasuredHeight() - AndroidUtilities.dp(2.0f)));
                    canvas.drawRoundRect(DialogsActivity.this.rect, (float) AndroidUtilities.dp(10.0f), (float) AndroidUtilities.dp(10.0f), DialogsActivity.this.paint);
                    canvas.drawText(text, (float) (cx - (textSize / 2)), (float) AndroidUtilities.dp(16.2f), DialogsActivity.this.textPaint);
                }
            };
            this.selectedCountView = r05;
            r05.setAlpha(0.0f);
            this.selectedCountView.setScaleX(0.2f);
            this.selectedCountView.setScaleY(0.2f);
            contentView.addView(this.selectedCountView, LayoutHelper.createFrame(42, 24.0f, 85, 0.0f, 0.0f, -8.0f, 9.0f));
            FrameLayout writeButtonBackground = new FrameLayout(context2);
            Drawable writeButtonDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), getThemedColor("dialogFloatingButton"), getThemedColor(Build.VERSION.SDK_INT >= 21 ? "dialogFloatingButtonPressed" : "dialogFloatingButton"));
            if (Build.VERSION.SDK_INT < 21) {
                Drawable shadowDrawable2 = context.getResources().getDrawable(NUM).mutate();
                shadowDrawable2.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable2 = new CombinedDrawable(shadowDrawable2, drawable, 0, 0);
                combinedDrawable2.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                writeButtonDrawable = combinedDrawable2;
            }
            writeButtonBackground.setBackgroundDrawable(writeButtonDrawable);
            writeButtonBackground.setImportantForAccessibility(2);
            if (Build.VERSION.SDK_INT >= 21) {
                writeButtonBackground.setOutlineProvider(new ViewOutlineProvider() {
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    }
                });
            }
            writeButtonBackground.setOnClickListener(new DialogsActivity$$ExternalSyntheticLambda8(this));
            writeButtonBackground.setOnLongClickListener(new DialogsActivity$$ExternalSyntheticLambda19(this, writeButtonBackground));
            this.writeButton = new ImageView[2];
            int a4 = 0;
            for (int i9 = 2; a4 < i9; i9 = 2) {
                this.writeButton[a4] = new ImageView(context2);
                this.writeButton[a4].setImageResource(a4 == 1 ? NUM : NUM);
                this.writeButton[a4].setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogFloatingIcon"), PorterDuff.Mode.MULTIPLY));
                this.writeButton[a4].setScaleType(ImageView.ScaleType.CENTER);
                writeButtonBackground.addView(this.writeButton[a4], LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56 : 60, 17));
                a4++;
            }
            AndroidUtilities.updateViewVisibilityAnimated(this.writeButton[0], true, 0.5f, false);
            AndroidUtilities.updateViewVisibilityAnimated(this.writeButton[1], false, 0.5f, false);
            this.writeButtonContainer.addView(writeButtonBackground, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 51, Build.VERSION.SDK_INT >= 21 ? 2.0f : 0.0f, 0.0f, 0.0f, 0.0f));
        } else {
            i = 21;
            z2 = true;
        }
        FilterTabsView filterTabsView2 = this.filterTabsView;
        if (filterTabsView2 != null) {
            i2 = -1;
            contentView.addView(filterTabsView2, LayoutHelper.createFrame(-1, 44.0f));
        } else {
            i2 = -1;
        }
        if (!this.onlySelect) {
            FrameLayout.LayoutParams layoutParams = LayoutHelper.createFrame(i2, -2.0f);
            if (this.inPreviewMode && Build.VERSION.SDK_INT >= i) {
                layoutParams.topMargin = AndroidUtilities.statusBarHeight;
            }
            contentView.addView(this.actionBar, layoutParams);
        }
        if (this.searchString == null && this.initialDialogsType == 0) {
            AnonymousClass25 r06 = new FrameLayout(context2) {
                private int lastGradientWidth;
                private Matrix matrix = new Matrix();
                private Paint paint = new Paint();
                private LinearGradient updateGradient;

                public void draw(Canvas canvas) {
                    if (this.updateGradient != null) {
                        this.paint.setColor(-1);
                        this.paint.setShader(this.updateGradient);
                        this.updateGradient.setLocalMatrix(this.matrix);
                        canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.paint);
                        DialogsActivity.this.updateLayoutIcon.setBackgroundGradientDrawable(this.updateGradient);
                        DialogsActivity.this.updateLayoutIcon.draw(canvas);
                    }
                    super.draw(canvas);
                }

                /* access modifiers changed from: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    int width = View.MeasureSpec.getSize(widthMeasureSpec);
                    if (this.lastGradientWidth != width) {
                        this.updateGradient = new LinearGradient(0.0f, 0.0f, (float) width, 0.0f, new int[]{-9846926, -11291731}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
                        this.lastGradientWidth = width;
                    }
                    int x = (getMeasuredWidth() - DialogsActivity.this.updateTextView.getMeasuredWidth()) / 2;
                    DialogsActivity.this.updateLayoutIcon.setProgressRect(x, AndroidUtilities.dp(13.0f), AndroidUtilities.dp(22.0f) + x, AndroidUtilities.dp(35.0f));
                }

                public void setTranslationY(float translationY) {
                    super.setTranslationY(translationY);
                    float unused = DialogsActivity.this.additionalFloatingTranslation2 = ((float) AndroidUtilities.dp(48.0f)) - translationY;
                    if (DialogsActivity.this.additionalFloatingTranslation2 < 0.0f) {
                        float unused2 = DialogsActivity.this.additionalFloatingTranslation2 = 0.0f;
                    }
                    if (!DialogsActivity.this.floatingHidden) {
                        DialogsActivity.this.updateFloatingButtonOffset();
                    }
                }
            };
            this.updateLayout = r06;
            r06.setWillNotDraw(false);
            this.updateLayout.setVisibility(4);
            this.updateLayout.setTranslationY((float) AndroidUtilities.dp(48.0f));
            if (Build.VERSION.SDK_INT >= i) {
                this.updateLayout.setBackground(Theme.getSelectorDrawable(NUM, false));
            }
            contentView.addView(this.updateLayout, LayoutHelper.createFrame(-1, 48, 83));
            this.updateLayout.setOnClickListener(new DialogsActivity$$ExternalSyntheticLambda9(this));
            RadialProgress2 radialProgress2 = new RadialProgress2(this.updateLayout);
            this.updateLayoutIcon = radialProgress2;
            radialProgress2.setColors(-1, -1, -1, -1);
            this.updateLayoutIcon.setCircleRadius(AndroidUtilities.dp(11.0f));
            this.updateLayoutIcon.setAsMini();
            this.updateLayoutIcon.setIcon(15, z2, false);
            TextView textView = new TextView(context2);
            this.updateTextView = textView;
            textView.setTextSize(z2 ? 1 : 0, 15.0f);
            this.updateTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.updateTextView.setText(LocaleController.getString("AppUpdateNow", NUM).toUpperCase());
            this.updateTextView.setTextColor(-1);
            this.updateTextView.setPadding(AndroidUtilities.dp(30.0f), 0, 0, 0);
            this.updateLayout.addView(this.updateTextView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 0.0f));
        }
        for (int a5 = 0; a5 < 2; a5++) {
            this.undoView[a5] = new UndoView(context2) {
                public void setTranslationY(float translationY) {
                    super.setTranslationY(translationY);
                    if (this == DialogsActivity.this.undoView[0] && DialogsActivity.this.undoView[1].getVisibility() != 0) {
                        float unused = DialogsActivity.this.additionalFloatingTranslation = ((float) (getMeasuredHeight() + AndroidUtilities.dp(8.0f))) - translationY;
                        if (DialogsActivity.this.additionalFloatingTranslation < 0.0f) {
                            float unused2 = DialogsActivity.this.additionalFloatingTranslation = 0.0f;
                        }
                        if (!DialogsActivity.this.floatingHidden) {
                            DialogsActivity.this.updateFloatingButtonOffset();
                        }
                    }
                }

                /* access modifiers changed from: protected */
                public boolean canUndo() {
                    for (ViewPage access$12400 : DialogsActivity.this.viewPages) {
                        if (access$12400.dialogsItemAnimator.isRunning()) {
                            return false;
                        }
                    }
                    return true;
                }

                /* access modifiers changed from: protected */
                public void onRemoveDialogAction(long currentDialogId, int action) {
                    if (action == 1 || action == 27) {
                        int unused = DialogsActivity.this.debugLastUpdateAction = 1;
                        DialogsActivity.this.setDialogsListFrozen(true);
                        if (DialogsActivity.this.frozenDialogsList != null) {
                            int selectedIndex = -1;
                            int i = 0;
                            while (true) {
                                if (i >= DialogsActivity.this.frozenDialogsList.size()) {
                                    break;
                                } else if (((TLRPC.Dialog) DialogsActivity.this.frozenDialogsList.get(i)).id == currentDialogId) {
                                    selectedIndex = i;
                                    break;
                                } else {
                                    i++;
                                }
                            }
                            if (selectedIndex >= 0) {
                                DialogsActivity.this.viewPages[0].dialogsAdapter.notifyDataSetChanged();
                                AndroidUtilities.runOnUIThread(new DialogsActivity$26$$ExternalSyntheticLambda0(this, selectedIndex, (TLRPC.Dialog) DialogsActivity.this.frozenDialogsList.remove(selectedIndex)));
                                return;
                            }
                            DialogsActivity.this.setDialogsListFrozen(false);
                        }
                    }
                }

                /* renamed from: lambda$onRemoveDialogAction$0$org-telegram-ui-DialogsActivity$26  reason: not valid java name */
                public /* synthetic */ void m3427lambda$onRemoveDialogAction$0$orgtelegramuiDialogsActivity$26(int finalSelectedIndex, TLRPC.Dialog dialog) {
                    if (DialogsActivity.this.frozenDialogsList != null) {
                        DialogsActivity.this.frozenDialogsList.add(finalSelectedIndex, dialog);
                        DialogsActivity.this.viewPages[0].dialogsAdapter.notifyItemInserted(finalSelectedIndex);
                        int unused = DialogsActivity.this.dialogInsertFinished = 2;
                    }
                }
            };
            contentView.addView(this.undoView[a5], LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        }
        if (this.folderId != 0) {
            this.viewPages[0].listView.setGlowColor(Theme.getColor("actionBarDefaultArchived"));
            this.actionBar.setTitleColor(Theme.getColor("actionBarDefaultArchivedTitle"));
            this.actionBar.setItemsColor(Theme.getColor("actionBarDefaultArchivedIcon"), false);
            this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarDefaultArchivedSelector"), false);
            this.actionBar.setSearchTextColor(Theme.getColor("actionBarDefaultArchivedSearch"), false);
            this.actionBar.setSearchTextColor(Theme.getColor("actionBarDefaultSearchArchivedPlaceholder"), z2);
        }
        if (!this.onlySelect && this.initialDialogsType == 0) {
            this.blurredView = new View(context2) {
                public void setAlpha(float alpha) {
                    super.setAlpha(alpha);
                    if (DialogsActivity.this.fragmentView != null) {
                        DialogsActivity.this.fragmentView.invalidate();
                    }
                }
            };
            if (Build.VERSION.SDK_INT >= 23) {
                this.blurredView.setForeground(new ColorDrawable(ColorUtils.setAlphaComponent(getThemedColor("windowBackgroundWhite"), 100)));
            }
            this.blurredView.setFocusable(false);
            this.blurredView.setImportantForAccessibility(2);
            this.blurredView.setOnClickListener(new DialogsActivity$$ExternalSyntheticLambda10(this));
            this.blurredView.setVisibility(8);
            this.blurredView.setFitsSystemWindows(z2);
            contentView.addView(this.blurredView, LayoutHelper.createFrame(-1, -1.0f));
        }
        Paint paint2 = this.actionBarDefaultPaint;
        if (this.folderId == 0) {
            str = "actionBarDefault";
        } else {
            str = "actionBarDefaultArchived";
        }
        paint2.setColor(Theme.getColor(str));
        if (this.inPreviewMode) {
            TLRPC.User currentUser = getUserConfig().getCurrentUser();
            ChatAvatarContainer chatAvatarContainer = new ChatAvatarContainer(this.actionBar.getContext(), (ChatActivity) null, false);
            this.avatarContainer = chatAvatarContainer;
            chatAvatarContainer.setTitle(UserObject.getUserName(currentUser));
            this.avatarContainer.setSubtitle(LocaleController.formatUserStatus(this.currentAccount, currentUser));
            this.avatarContainer.setUserAvatar(currentUser, z2);
            this.avatarContainer.setOccupyStatusBar(false);
            this.avatarContainer.setLeftPadding(AndroidUtilities.dp(10.0f));
            this.actionBar.addView(this.avatarContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, 0.0f, 0.0f, 40.0f, 0.0f));
            this.floatingButton.setVisibility(4);
            this.actionBar.setOccupyStatusBar(false);
            this.actionBar.setBackgroundColor(Theme.getColor("actionBarDefault"));
            FragmentContextView fragmentContextView3 = this.fragmentContextView;
            if (fragmentContextView3 != null) {
                contentView.removeView(fragmentContextView3);
            }
            FragmentContextView fragmentContextView4 = this.fragmentLocationContextView;
            if (fragmentContextView4 != null) {
                contentView.removeView(fragmentContextView4);
            }
        }
        this.searchIsShowed = false;
        updateFilterTabs(false, false);
        if (this.searchString != null) {
            showSearch(z2, false, false);
            this.actionBar.openSearchField(this.searchString, false);
        } else if (this.initialSearchString != null) {
            showSearch(z2, false, false);
            this.actionBar.openSearchField(this.initialSearchString, false);
            this.initialSearchString = null;
            FilterTabsView filterTabsView3 = this.filterTabsView;
            if (filterTabsView3 != null) {
                filterTabsView3.setTranslationY((float) (-AndroidUtilities.dp(44.0f)));
            }
        } else {
            showSearch(false, false, false);
        }
        if (Build.VERSION.SDK_INT >= 30) {
            FilesMigrationService.checkBottomSheet(this);
        }
        updateMenuButton(false);
        this.actionBar.setDrawBlurBackground(contentView);
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3377lambda$createView$3$orgtelegramuiDialogsActivity(View v) {
        this.filterTabsView.setIsEditing(false);
        showDoneItem(false);
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3378lambda$createView$4$orgtelegramuiDialogsActivity() {
        if (this.initialDialogsType != 10) {
            hideFloatingButton(false);
        }
        scrollToTop();
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3379lambda$createView$5$orgtelegramuiDialogsActivity(ViewPage viewPage, View view, int position) {
        int i = this.initialDialogsType;
        if (i == 10) {
            onItemLongClick(viewPage.listView, view, position, 0.0f, 0.0f, viewPage.dialogsType, viewPage.dialogsAdapter);
        } else if ((i == 11 || i == 13) && position == 1) {
            Bundle args = new Bundle();
            args.putBoolean("forImport", true);
            args.putLongArray("result", new long[]{getUserConfig().getClientUserId()});
            args.putInt("chatType", 4);
            String title = this.arguments.getString("importTitle");
            if (title != null) {
                args.putString("title", title);
            }
            GroupCreateFinalActivity activity = new GroupCreateFinalActivity(args);
            activity.setDelegate(new GroupCreateFinalActivity.GroupCreateFinalActivityDelegate() {
                public void didStartChatCreation() {
                }

                public void didFinishChatCreation(GroupCreateFinalActivity fragment, long chatId) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(Long.valueOf(-chatId));
                    DialogsActivityDelegate dialogsActivityDelegate = DialogsActivity.this.delegate;
                    if (DialogsActivity.this.closeFragment) {
                        DialogsActivity.this.removeSelfFromStack();
                    }
                    dialogsActivityDelegate.didSelectDialogs(DialogsActivity.this, arrayList, (CharSequence) null, true);
                }

                public void didFailChatCreation() {
                }
            });
            presentFragment(activity);
        } else {
            onItemClick(view, position, viewPage.dialogsAdapter);
        }
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3380lambda$createView$6$orgtelegramuiDialogsActivity(View view, int position) {
        if (this.initialDialogsType == 10) {
            onItemLongClick(this.searchViewPager.searchListView, view, position, 0.0f, 0.0f, -1, this.searchViewPager.dialogsSearchAdapter);
            return;
        }
        onItemClick(view, position, this.searchViewPager.dialogsSearchAdapter);
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3381lambda$createView$7$orgtelegramuiDialogsActivity(boolean showMediaFilters, ArrayList users, ArrayList dates, boolean archive3) {
        updateFiltersView(showMediaFilters, users, dates, archive3, true);
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3382lambda$createView$8$orgtelegramuiDialogsActivity(View view, int position) {
        this.filtersView.cancelClickRunnables(true);
        addSearchFilter(this.filtersView.getFilterAt(position));
    }

    /* renamed from: lambda$createView$9$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3383lambda$createView$9$orgtelegramuiDialogsActivity(View v) {
        if (this.parentLayout != null && this.parentLayout.isInPreviewMode()) {
            finishPreviewFragment();
        } else if (this.initialDialogsType == 10) {
            if (this.delegate != null && !this.selectedDialogs.isEmpty()) {
                this.delegate.didSelectDialogs(this, this.selectedDialogs, (CharSequence) null, false);
            }
        } else if (this.floatingButton.getVisibility() == 0) {
            Bundle args = new Bundle();
            args.putBoolean("destroyAfterSelect", true);
            presentFragment(new ContactsActivity(args));
        }
    }

    /* renamed from: lambda$createView$10$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3373lambda$createView$10$orgtelegramuiDialogsActivity(View v) {
        if (this.delegate != null && !this.selectedDialogs.isEmpty()) {
            this.delegate.didSelectDialogs(this, this.selectedDialogs, this.commentView.getFieldText(), false);
        }
    }

    /* renamed from: lambda$createView$11$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ boolean m3374lambda$createView$11$orgtelegramuiDialogsActivity(FrameLayout writeButtonBackground, View v) {
        if (this.isNextButton) {
            return false;
        }
        onSendLongClick(writeButtonBackground);
        return true;
    }

    /* renamed from: lambda$createView$12$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3375lambda$createView$12$orgtelegramuiDialogsActivity(View v) {
        if (SharedConfig.isAppUpdateAvailable()) {
            AndroidUtilities.openForView(SharedConfig.pendingAppUpdate.document, true, getParentActivity());
        }
    }

    /* renamed from: lambda$createView$13$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3376lambda$createView$13$orgtelegramuiDialogsActivity(View e) {
        finishPreviewFragment();
    }

    /* access modifiers changed from: private */
    public void updateCommentView() {
    }

    private /* synthetic */ void lambda$updateCommentView$14(ValueAnimator a) {
        this.commentView.setTranslationY(((Float) a.getAnimatedValue()).floatValue());
    }

    private void updateAppUpdateViews(boolean animated) {
        boolean show;
        if (this.updateLayout != null) {
            if (SharedConfig.isAppUpdateAvailable()) {
                String attachFileName = FileLoader.getAttachFileName(SharedConfig.pendingAppUpdate.document);
                show = getFileLoader().getPathToAttach(SharedConfig.pendingAppUpdate.document, true).exists();
            } else {
                show = false;
            }
            if (show) {
                if (this.updateLayout.getTag() == null) {
                    AnimatorSet animatorSet = this.updateLayoutAnimator;
                    if (animatorSet != null) {
                        animatorSet.cancel();
                    }
                    this.updateLayout.setVisibility(0);
                    this.updateLayout.setTag(1);
                    if (animated) {
                        AnimatorSet animatorSet2 = new AnimatorSet();
                        this.updateLayoutAnimator = animatorSet2;
                        animatorSet2.setDuration(180);
                        this.updateLayoutAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                        this.updateLayoutAnimator.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.updateLayout, View.TRANSLATION_Y, new float[]{0.0f})});
                        this.updateLayoutAnimator.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
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
                if (animated) {
                    AnimatorSet animatorSet3 = new AnimatorSet();
                    this.updateLayoutAnimator = animatorSet3;
                    animatorSet3.setDuration(180);
                    this.updateLayoutAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    this.updateLayoutAnimator.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.updateLayout, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(48.0f)})});
                    this.updateLayoutAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
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
        float filtersTabsHeight = 0.0f;
        FilterTabsView filterTabsView2 = this.filterTabsView;
        if (!(filterTabsView2 == null || filterTabsView2.getVisibility() == 8)) {
            filtersTabsHeight = (float) this.filterTabsView.getMeasuredHeight();
        }
        float searchTabsHeight = 0.0f;
        ViewPagerFixed.TabsView tabsView = this.searchTabsView;
        if (!(tabsView == null || tabsView.getVisibility() == 8)) {
            searchTabsHeight = (float) this.searchTabsView.getMeasuredHeight();
        }
        if (this.fragmentContextView != null) {
            float from = 0.0f;
            FragmentContextView fragmentContextView2 = this.fragmentLocationContextView;
            if (fragmentContextView2 != null && fragmentContextView2.getVisibility() == 0) {
                from = 0.0f + ((float) AndroidUtilities.dp(36.0f));
            }
            FragmentContextView fragmentContextView3 = this.fragmentContextView;
            float topPadding2 = fragmentContextView3.getTopPadding() + from + this.actionBar.getTranslationY();
            float f = this.searchAnimationProgress;
            fragmentContextView3.setTranslationY(topPadding2 + ((1.0f - f) * filtersTabsHeight) + (f * searchTabsHeight) + this.tabsYOffset);
        }
        if (this.fragmentLocationContextView != null) {
            float from2 = 0.0f;
            FragmentContextView fragmentContextView4 = this.fragmentContextView;
            if (fragmentContextView4 != null && fragmentContextView4.getVisibility() == 0) {
                from2 = 0.0f + ((float) AndroidUtilities.dp((float) this.fragmentContextView.getStyleHeight())) + this.fragmentContextView.getTopPadding();
            }
            FragmentContextView fragmentContextView5 = this.fragmentLocationContextView;
            float topPadding3 = fragmentContextView5.getTopPadding() + from2 + this.actionBar.getTranslationY();
            float f2 = this.searchAnimationProgress;
            fragmentContextView5.setTranslationY(topPadding3 + ((1.0f - f2) * filtersTabsHeight) + (f2 * searchTabsHeight) + this.tabsYOffset);
        }
    }

    /* access modifiers changed from: private */
    public void updateFiltersView(boolean showMediaFilters, ArrayList<Object> users, ArrayList<FiltersView.DateData> dates, boolean archive3, boolean animated) {
        boolean archive4;
        if (this.searchIsShowed && !this.onlySelect) {
            boolean hasMediaFilter = false;
            boolean hasUserFilter = false;
            boolean hasDateFilter = false;
            boolean hasArchiveFilter = false;
            ArrayList<FiltersView.MediaFilterData> currentSearchFilters = this.searchViewPager.getCurrentSearchFilters();
            for (int i = 0; i < currentSearchFilters.size(); i++) {
                if (currentSearchFilters.get(i).isMedia()) {
                    hasMediaFilter = true;
                } else if (currentSearchFilters.get(i).filterType == 4) {
                    hasUserFilter = true;
                } else if (currentSearchFilters.get(i).filterType == 6) {
                    hasDateFilter = true;
                } else if (currentSearchFilters.get(i).filterType == 7) {
                    hasArchiveFilter = true;
                }
            }
            if (hasArchiveFilter) {
                archive4 = false;
            } else {
                archive4 = archive3;
            }
            boolean visible = false;
            boolean hasUsersOrDates = (users != null && !users.isEmpty()) || (dates != null && !dates.isEmpty()) || archive4;
            if ((hasMediaFilter || hasUsersOrDates || !showMediaFilters) && hasUsersOrDates) {
                ArrayList<Object> finalUsers = (users == null || users.isEmpty() || hasUserFilter) ? null : users;
                ArrayList<FiltersView.DateData> finalDates = (dates == null || dates.isEmpty() || hasDateFilter) ? null : dates;
                if (!(finalUsers == null && finalDates == null && !archive4)) {
                    visible = true;
                    this.filtersView.setUsersAndDates(finalUsers, finalDates, archive4);
                }
            }
            if (!visible) {
                this.filtersView.setUsersAndDates((ArrayList<Object>) null, (ArrayList<FiltersView.DateData>) null, false);
            }
            if (!animated) {
                this.filtersView.getAdapter().notifyDataSetChanged();
            }
            ViewPagerFixed.TabsView tabsView = this.searchTabsView;
            if (tabsView != null) {
                tabsView.hide(visible, true);
            }
            this.filtersView.setEnabled(visible);
            this.filtersView.setVisibility(0);
        }
    }

    private void addSearchFilter(FiltersView.MediaFilterData filter) {
        if (this.searchIsShowed) {
            ArrayList<FiltersView.MediaFilterData> currentSearchFilters = this.searchViewPager.getCurrentSearchFilters();
            if (!currentSearchFilters.isEmpty()) {
                int i = 0;
                while (i < currentSearchFilters.size()) {
                    if (!filter.isSameType(currentSearchFilters.get(i))) {
                        i++;
                    } else {
                        return;
                    }
                }
            }
            currentSearchFilters.add(filter);
            this.actionBar.setSearchFilter(filter);
            this.actionBar.setSearchFieldText("");
            updateFiltersView(true, (ArrayList<Object>) null, (ArrayList<FiltersView.DateData>) null, false, true);
        }
    }

    private void createActionMode(String tag) {
        if (!this.actionBar.actionModeIsExist(tag)) {
            ActionBarMenu actionMode = this.actionBar.createActionMode(false, tag);
            actionMode.setBackgroundColor(0);
            actionMode.drawBlur = false;
            NumberTextView numberTextView = new NumberTextView(actionMode.getContext());
            this.selectedDialogsCountTextView = numberTextView;
            numberTextView.setTextSize(18);
            this.selectedDialogsCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.selectedDialogsCountTextView.setTextColor(Theme.getColor("actionBarActionModeDefaultIcon"));
            actionMode.addView(this.selectedDialogsCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 72, 0, 0, 0));
            this.selectedDialogsCountTextView.setOnTouchListener(DialogsActivity$$ExternalSyntheticLambda20.INSTANCE);
            this.pinItem = actionMode.addItemWithWidth(100, NUM, AndroidUtilities.dp(54.0f));
            this.muteItem = actionMode.addItemWithWidth(104, NUM, AndroidUtilities.dp(54.0f));
            this.archive2Item = actionMode.addItemWithWidth(107, NUM, AndroidUtilities.dp(54.0f));
            this.deleteItem = actionMode.addItemWithWidth(102, NUM, AndroidUtilities.dp(54.0f), (CharSequence) LocaleController.getString("Delete", NUM));
            ActionBarMenuItem otherItem = actionMode.addItemWithWidth(0, NUM, AndroidUtilities.dp(54.0f), (CharSequence) LocaleController.getString("AccDescrMoreOptions", NUM));
            this.archiveItem = otherItem.addSubItem(105, NUM, (CharSequence) LocaleController.getString("Archive", NUM));
            this.pin2Item = otherItem.addSubItem(108, NUM, (CharSequence) LocaleController.getString("DialogPin", NUM));
            this.addToFolderItem = otherItem.addSubItem(109, NUM, (CharSequence) LocaleController.getString("FilterAddTo", NUM));
            this.removeFromFolderItem = otherItem.addSubItem(110, NUM, (CharSequence) LocaleController.getString("FilterRemoveFrom", NUM));
            this.readItem = otherItem.addSubItem(101, NUM, (CharSequence) LocaleController.getString("MarkAsRead", NUM));
            this.clearItem = otherItem.addSubItem(103, NUM, (CharSequence) LocaleController.getString("ClearHistory", NUM));
            this.blockItem = otherItem.addSubItem(106, NUM, (CharSequence) LocaleController.getString("BlockUser", NUM));
            this.actionModeViews.add(this.pinItem);
            this.actionModeViews.add(this.archive2Item);
            this.actionModeViews.add(this.muteItem);
            this.actionModeViews.add(this.deleteItem);
            this.actionModeViews.add(otherItem);
            if (tag == null) {
                this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
                    public void onItemClick(int id) {
                        int currentCount;
                        ArrayList<Long> neverShow;
                        long did;
                        ArrayList<Long> neverShow2;
                        int i = id;
                        if ((i == 201 || i == 200 || i == 202) && DialogsActivity.this.searchViewPager != null) {
                            DialogsActivity.this.searchViewPager.onActionBarItemClick(i);
                        } else if (i == -1) {
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
                        } else if (i == 1) {
                            if (DialogsActivity.this.getParentActivity() != null) {
                                SharedConfig.appLocked = true;
                                SharedConfig.saveConfig();
                                int[] position = new int[2];
                                DialogsActivity.this.passcodeItem.getLocationInWindow(position);
                                ((LaunchActivity) DialogsActivity.this.getParentActivity()).showPasscodeActivity(false, true, position[0] + (DialogsActivity.this.passcodeItem.getMeasuredWidth() / 2), position[1] + (DialogsActivity.this.passcodeItem.getMeasuredHeight() / 2), new DialogsActivity$30$$ExternalSyntheticLambda0(this), new DialogsActivity$30$$ExternalSyntheticLambda1(this));
                                DialogsActivity.this.updatePasscodeButton();
                            }
                        } else if (i == 2) {
                            DialogsActivity.this.presentFragment(new ProxyListActivity());
                        } else if (i == 3) {
                            DialogsActivity.this.showSearch(true, true, true);
                            DialogsActivity.this.actionBar.openSearchField(true);
                        } else if (i < 10 || i >= 14) {
                            if (i == 109) {
                                DialogsActivity dialogsActivity = DialogsActivity.this;
                                FiltersListBottomSheet sheet = new FiltersListBottomSheet(dialogsActivity, dialogsActivity.selectedDialogs);
                                sheet.setDelegate(new DialogsActivity$30$$ExternalSyntheticLambda2(this));
                                DialogsActivity.this.showDialog(sheet);
                            } else if (i == 110) {
                                MessagesController.DialogFilter filter = DialogsActivity.this.getMessagesController().dialogFilters.get(DialogsActivity.this.viewPages[0].selectedType);
                                DialogsActivity dialogsActivity2 = DialogsActivity.this;
                                ArrayList<Long> neverShow3 = FiltersListBottomSheet.getDialogsCount(dialogsActivity2, filter, dialogsActivity2.selectedDialogs, false, false);
                                if (filter != null) {
                                    currentCount = filter.neverShow.size();
                                } else {
                                    currentCount = 0;
                                }
                                if (currentCount + neverShow3.size() > 100) {
                                    DialogsActivity dialogsActivity3 = DialogsActivity.this;
                                    dialogsActivity3.showDialog(AlertsCreator.createSimpleAlert(dialogsActivity3.getParentActivity(), LocaleController.getString("FilterAddToAlertFullTitle", NUM), LocaleController.getString("FilterAddToAlertFullText", NUM)).create());
                                    return;
                                }
                                if (!neverShow3.isEmpty()) {
                                    filter.neverShow.addAll(neverShow3);
                                    for (int a = 0; a < neverShow3.size(); a++) {
                                        Long did2 = neverShow3.get(a);
                                        filter.alwaysShow.remove(did2);
                                        filter.pinnedDialogs.delete(did2.longValue());
                                    }
                                    neverShow = neverShow3;
                                    FilterCreateActivity.saveFilterToServer(filter, filter.flags, filter.name, filter.alwaysShow, filter.neverShow, filter.pinnedDialogs, false, false, true, false, false, DialogsActivity.this, (Runnable) null);
                                } else {
                                    neverShow = neverShow3;
                                }
                                if (neverShow.size() == 1) {
                                    neverShow2 = neverShow;
                                    did = neverShow2.get(0).longValue();
                                } else {
                                    neverShow2 = neverShow;
                                    did = 0;
                                }
                                DialogsActivity.this.getUndoView().showWithAction(did, 21, (Object) Integer.valueOf(neverShow2.size()), (Object) filter, (Runnable) null, (Runnable) null);
                                DialogsActivity.this.hideActionMode(false);
                            } else if (i == 100 || i == 101 || i == 102 || i == 103 || i == 104 || i == 105 || i == 106 || i == 107 || i == 108) {
                                DialogsActivity dialogsActivity4 = DialogsActivity.this;
                                dialogsActivity4.performSelectedDialogsAction(dialogsActivity4.selectedDialogs, i, true);
                            }
                        } else if (DialogsActivity.this.getParentActivity() != null) {
                            DialogsActivityDelegate oldDelegate = DialogsActivity.this.delegate;
                            LaunchActivity launchActivity = (LaunchActivity) DialogsActivity.this.getParentActivity();
                            launchActivity.switchToAccount(i - 10, true);
                            DialogsActivity dialogsActivity5 = new DialogsActivity(DialogsActivity.this.arguments);
                            dialogsActivity5.setDelegate(oldDelegate);
                            launchActivity.presentFragment(dialogsActivity5, false, true);
                        }
                    }

                    /* renamed from: lambda$onItemClick$0$org-telegram-ui-DialogsActivity$30  reason: not valid java name */
                    public /* synthetic */ void m3428lambda$onItemClick$0$orgtelegramuiDialogsActivity$30() {
                        DialogsActivity.this.passcodeItem.setAlpha(1.0f);
                    }

                    /* renamed from: lambda$onItemClick$1$org-telegram-ui-DialogsActivity$30  reason: not valid java name */
                    public /* synthetic */ void m3429lambda$onItemClick$1$orgtelegramuiDialogsActivity$30() {
                        DialogsActivity.this.passcodeItem.setAlpha(0.0f);
                    }

                    /* renamed from: lambda$onItemClick$2$org-telegram-ui-DialogsActivity$30  reason: not valid java name */
                    public /* synthetic */ void m3430lambda$onItemClick$2$orgtelegramuiDialogsActivity$30(MessagesController.DialogFilter filter) {
                        int currentCount;
                        ArrayList<Long> alwaysShow;
                        long did;
                        ArrayList<Long> alwaysShow2;
                        MessagesController.DialogFilter dialogFilter = filter;
                        DialogsActivity dialogsActivity = DialogsActivity.this;
                        ArrayList<Long> alwaysShow3 = FiltersListBottomSheet.getDialogsCount(dialogsActivity, dialogFilter, dialogsActivity.selectedDialogs, true, false);
                        if (dialogFilter != null) {
                            currentCount = dialogFilter.alwaysShow.size();
                        } else {
                            currentCount = 0;
                        }
                        int totalCount = currentCount + alwaysShow3.size();
                        if ((totalCount <= DialogsActivity.this.getMessagesController().dialogFiltersChatsLimitDefault || DialogsActivity.this.getUserConfig().isPremium()) && totalCount <= DialogsActivity.this.getMessagesController().dialogFiltersChatsLimitPremium) {
                            if (dialogFilter != null) {
                                if (!alwaysShow3.isEmpty()) {
                                    for (int a = 0; a < alwaysShow3.size(); a++) {
                                        dialogFilter.neverShow.remove(alwaysShow3.get(a));
                                    }
                                    dialogFilter.alwaysShow.addAll(alwaysShow3);
                                    int i = totalCount;
                                    alwaysShow = alwaysShow3;
                                    FilterCreateActivity.saveFilterToServer(filter, dialogFilter.flags, dialogFilter.name, dialogFilter.alwaysShow, dialogFilter.neverShow, dialogFilter.pinnedDialogs, false, false, true, true, false, DialogsActivity.this, (Runnable) null);
                                } else {
                                    alwaysShow = alwaysShow3;
                                }
                                if (alwaysShow.size() == 1) {
                                    alwaysShow2 = alwaysShow;
                                    did = alwaysShow2.get(0).longValue();
                                } else {
                                    alwaysShow2 = alwaysShow;
                                    did = 0;
                                }
                                DialogsActivity.this.getUndoView().showWithAction(did, 20, (Object) Integer.valueOf(alwaysShow2.size()), (Object) filter, (Runnable) null, (Runnable) null);
                            } else {
                                DialogsActivity.this.presentFragment(new FilterCreateActivity((MessagesController.DialogFilter) null, alwaysShow3));
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

    static /* synthetic */ boolean lambda$createActionMode$15(View v, MotionEvent event) {
        return true;
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
        MessagesController.DialogFilter filter = getMessagesController().dialogFilters.get(this.viewPages[a2].selectedType);
        int i = 0;
        if (filter.isDefault()) {
            int unused = this.viewPages[a2].dialogsType = 0;
            this.viewPages[a2].listView.updatePullState();
        } else {
            if (this.viewPages[a2 == 0 ? (char) 1 : 0].dialogsType == 7) {
                int unused2 = this.viewPages[a2].dialogsType = 8;
            } else {
                int unused3 = this.viewPages[a2].dialogsType = 7;
            }
            this.viewPages[a2].listView.setScrollEnabled(true);
            getMessagesController().selectDialogFilter(filter, this.viewPages[a2].dialogsType == 8 ? 1 : 0);
        }
        boolean unused4 = this.viewPages[1].isLocked = filter.locked;
        this.viewPages[a2].dialogsAdapter.setDialogsType(this.viewPages[a2].dialogsType);
        LinearLayoutManager access$11500 = this.viewPages[a2].layoutManager;
        if (this.viewPages[a2].dialogsType == 0 && hasHiddenArchive()) {
            i = 1;
        }
        access$11500.scrollToPositionWithOffset(i, (int) this.actionBar.getTranslationY());
        checkListLoad(this.viewPages[a2]);
    }

    /* access modifiers changed from: private */
    public void showScrollbars(boolean show) {
        if (this.viewPages != null && this.scrollBarVisible != show) {
            this.scrollBarVisible = show;
            int a = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (a < viewPageArr.length) {
                    if (show) {
                        viewPageArr[a].listView.setScrollbarFadingEnabled(false);
                    }
                    this.viewPages[a].listView.setVerticalScrollBarEnabled(show);
                    if (show) {
                        this.viewPages[a].listView.setScrollbarFadingEnabled(true);
                    }
                    a++;
                } else {
                    return;
                }
            }
        }
    }

    private void scrollToFilterTab(int index) {
        if (this.filterTabsView != null && this.viewPages[0].selectedType != index) {
            this.filterTabsView.selectTabWithId(index, 1.0f);
            this.parentLayout.getDrawerLayoutContainer().setAllowOpenDrawerBySwipe(false);
            ViewPage[] viewPageArr = this.viewPages;
            int unused = viewPageArr[1].selectedType = viewPageArr[0].selectedType;
            int unused2 = this.viewPages[0].selectedType = index;
            switchToCurrentSelectedMode(false);
            switchToCurrentSelectedMode(true);
            updateCounters(false);
        }
    }

    private void updateFilterTabs(boolean force, boolean animated) {
        int p;
        boolean z = animated;
        if (this.filterTabsView != null && !this.inPreviewMode && !this.searchIsShowed) {
            ActionBarPopupWindow actionBarPopupWindow = this.scrimPopupWindow;
            if (actionBarPopupWindow != null) {
                actionBarPopupWindow.dismiss();
                this.scrimPopupWindow = null;
            }
            ArrayList<MessagesController.DialogFilter> filters = getMessagesController().dialogFilters;
            SharedPreferences mainSettings = MessagesController.getMainSettings(this.currentAccount);
            boolean z2 = true;
            if (filters.size() <= 1) {
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
                    updateFilterTabsVisibility(z);
                    int a = 0;
                    while (true) {
                        ViewPage[] viewPageArr2 = this.viewPages;
                        if (a >= viewPageArr2.length) {
                            break;
                        }
                        if (viewPageArr2[a].dialogsType == 0 && this.viewPages[a].archivePullViewState == 2 && hasHiddenArchive() && ((p = this.viewPages[a].layoutManager.findFirstVisibleItemPosition()) == 0 || p == 1)) {
                            this.viewPages[a].layoutManager.scrollToPositionWithOffset(1, 0);
                        }
                        this.viewPages[a].listView.setScrollingTouchSlop(0);
                        this.viewPages[a].listView.requestLayout();
                        this.viewPages[a].requestLayout();
                        a++;
                    }
                }
                if (this.parentLayout != null) {
                    this.parentLayout.getDrawerLayoutContainer().setAllowOpenDrawerBySwipe(true);
                }
            } else if (force || this.filterTabsView.getVisibility() != 0) {
                boolean animatedUpdateItems = animated;
                if (this.filterTabsView.getVisibility() != 0) {
                    animatedUpdateItems = false;
                }
                this.canShowFilterTabsView = true;
                boolean updateCurrentTab = this.filterTabsView.isEmpty();
                updateFilterTabsVisibility(z);
                int id = this.filterTabsView.getCurrentTabId();
                int stableId = this.filterTabsView.getCurrentTabStableId();
                if (id != this.filterTabsView.getDefaultTabId() && id >= filters.size()) {
                    this.filterTabsView.resetTabId();
                }
                this.filterTabsView.removeTabs();
                int N = filters.size();
                for (int a2 = 0; a2 < N; a2++) {
                    if (filters.get(a2).isDefault()) {
                        this.filterTabsView.addTab(a2, 0, LocaleController.getString("FilterAllChats", NUM), true, filters.get(a2).locked);
                    } else {
                        this.filterTabsView.addTab(a2, filters.get(a2).localId, filters.get(a2).name, false, filters.get(a2).locked);
                    }
                }
                if (stableId >= 0 && this.filterTabsView.getStableId(this.viewPages[0].selectedType) != stableId) {
                    updateCurrentTab = true;
                    int unused5 = this.viewPages[0].selectedType = id;
                }
                int a3 = 0;
                while (true) {
                    ViewPage[] viewPageArr3 = this.viewPages;
                    if (a3 >= viewPageArr3.length) {
                        break;
                    }
                    if (viewPageArr3[a3].selectedType >= filters.size()) {
                        int unused6 = this.viewPages[a3].selectedType = filters.size() - 1;
                    }
                    this.viewPages[a3].listView.setScrollingTouchSlop(1);
                    a3++;
                }
                this.filterTabsView.finishAddingTabs(animatedUpdateItems);
                if (updateCurrentTab) {
                    switchToCurrentSelectedMode(false);
                }
                if (this.parentLayout != null) {
                    DrawerLayoutContainer drawerLayoutContainer = this.parentLayout.getDrawerLayoutContainer();
                    if (id != this.filterTabsView.getFirstTabId() && SharedConfig.getChatSwipeAction(this.currentAccount) == 5) {
                        z2 = false;
                    }
                    drawerLayoutContainer.setAllowOpenDrawerBySwipe(z2);
                }
                FilterTabsView filterTabsView2 = this.filterTabsView;
                if (filterTabsView2.isLocked(filterTabsView2.getCurrentTabId())) {
                    this.filterTabsView.selectFirstTab();
                }
            }
            updateCounters(false);
        }
    }

    /* access modifiers changed from: protected */
    public void onPanTranslationUpdate(float y) {
        if (this.viewPages != null) {
            ChatActivityEnterView chatActivityEnterView = this.commentView;
            if (chatActivityEnterView == null || !chatActivityEnterView.isPopupShowing()) {
                int a = 0;
                while (true) {
                    ViewPage[] viewPageArr = this.viewPages;
                    if (a >= viewPageArr.length) {
                        break;
                    }
                    viewPageArr[a].setTranslationY(y);
                    a++;
                }
                if (this.onlySelect == 0) {
                    this.actionBar.setTranslationY(y);
                }
                this.searchViewPager.setTranslationY(y);
                return;
            }
            this.fragmentView.setTranslationY(y);
            int a2 = 0;
            while (true) {
                ViewPage[] viewPageArr2 = this.viewPages;
                if (a2 >= viewPageArr2.length) {
                    break;
                }
                viewPageArr2[a2].setTranslationY(0.0f);
                a2++;
            }
            if (this.onlySelect == 0) {
                this.actionBar.setTranslationY(0.0f);
            }
            this.searchViewPager.setTranslationY(0.0f);
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
        boolean tosAccepted;
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
            int a = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (a >= viewPageArr.length) {
                    break;
                }
                viewPageArr[a].dialogsAdapter.notifyDataSetChanged();
                a++;
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
        if (!this.afterSignup) {
            tosAccepted = getUserConfig().unacceptedTermsOfService == null;
        } else {
            tosAccepted = true;
        }
        if (tosAccepted && this.checkPermission && !this.onlySelect && Build.VERSION.SDK_INT >= 23) {
            Activity activity = getParentActivity();
            if (activity != null) {
                this.checkPermission = false;
                boolean hasNotContactsPermission = activity.checkSelfPermission("android.permission.READ_CONTACTS") != 0;
                AndroidUtilities.runOnUIThread(new DialogsActivity$$ExternalSyntheticLambda40(this, hasNotContactsPermission, (Build.VERSION.SDK_INT <= 28 || BuildVars.NO_SCOPED_STORAGE) && activity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0, activity), (!this.afterSignup || !hasNotContactsPermission) ? 0 : 4000);
            }
        } else if (!this.onlySelect && XiaomiUtilities.isMIUI() && Build.VERSION.SDK_INT >= 19 && !XiaomiUtilities.isCustomPermissionGranted(10020)) {
            if (getParentActivity() != null && !MessagesController.getGlobalNotificationsSettings().getBoolean("askedAboutMiuiLockscreen", false)) {
                showDialog(new AlertDialog.Builder((Context) getParentActivity()).setTopAnimation(NUM, 72, false, Theme.getColor("dialogTopBackground")).setMessage(LocaleController.getString("PermissionXiaomiLockscreen", NUM)).setPositiveButton(LocaleController.getString("PermissionOpenSettings", NUM), new DialogsActivity$$ExternalSyntheticLambda57(this)).setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", NUM), DialogsActivity$$ExternalSyntheticLambda5.INSTANCE).create());
            } else {
                return;
            }
        }
        showFiltersHint();
        if (this.viewPages != null) {
            int a2 = 0;
            while (true) {
                ViewPage[] viewPageArr2 = this.viewPages;
                if (a2 >= viewPageArr2.length) {
                    break;
                }
                if (viewPageArr2[a2].dialogsType == 0 && this.viewPages[a2].archivePullViewState == 2 && this.viewPages[a2].layoutManager.findFirstVisibleItemPosition() == 0 && hasHiddenArchive()) {
                    this.viewPages[a2].layoutManager.scrollToPositionWithOffset(1, 0);
                }
                if (a2 == 0) {
                    this.viewPages[a2].dialogsAdapter.resume();
                } else {
                    this.viewPages[a2].dialogsAdapter.pause();
                }
                a2++;
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

            public void onOffsetChange(float offset) {
                if (DialogsActivity.this.undoView[0] == null || DialogsActivity.this.undoView[0].getVisibility() != 0) {
                    float unused = DialogsActivity.this.additionalFloatingTranslation = offset;
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

    /* renamed from: lambda$onResume$17$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3399lambda$onResume$17$orgtelegramuiDialogsActivity(boolean hasNotContactsPermission, boolean hasNotStoragePermission, Activity activity) {
        this.afterSignup = false;
        if (hasNotContactsPermission || hasNotStoragePermission) {
            this.askingForPermissions = true;
            if (hasNotContactsPermission && this.askAboutContacts && getUserConfig().syncContacts && activity.shouldShowRequestPermissionRationale("android.permission.READ_CONTACTS")) {
                AlertDialog create = AlertsCreator.createContactsPermissionDialog(activity, new DialogsActivity$$ExternalSyntheticLambda45(this)).create();
                this.permissionDialog = create;
                showDialog(create);
            } else if (!hasNotStoragePermission || !activity.shouldShowRequestPermissionRationale("android.permission.WRITE_EXTERNAL_STORAGE")) {
                askForPermissons(true);
            } else if (activity instanceof BasePermissionsActivity) {
                AlertDialog createPermissionErrorAlert = ((BasePermissionsActivity) activity).createPermissionErrorAlert(NUM, LocaleController.getString(NUM));
                this.permissionDialog = createPermissionErrorAlert;
                showDialog(createPermissionErrorAlert);
            }
        }
    }

    /* renamed from: lambda$onResume$16$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3398lambda$onResume$16$orgtelegramuiDialogsActivity(int param) {
        this.askAboutContacts = param != 0;
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askAboutContacts", this.askAboutContacts).apply();
        askForPermissons(false);
    }

    /* renamed from: lambda$onResume$18$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3400lambda$onResume$18$orgtelegramuiDialogsActivity(DialogInterface dialog, int which) {
        Intent intent = XiaomiUtilities.getPermissionManagerIntent();
        if (intent != null) {
            try {
                getParentActivity().startActivity(intent);
            } catch (Exception e) {
                try {
                    Intent intent2 = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent2.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                    getParentActivity().startActivity(intent2);
                } catch (Exception xx) {
                    FileLog.e((Throwable) xx);
                }
            }
        }
    }

    public boolean presentFragment(BaseFragment fragment) {
        boolean b = super.presentFragment(fragment);
        if (b && this.viewPages != null) {
            int a = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (a >= viewPageArr.length) {
                    break;
                }
                viewPageArr[a].dialogsAdapter.pause();
                a++;
            }
        }
        return b;
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
        if (this.viewPages != null) {
            int a = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (a < viewPageArr.length) {
                    viewPageArr[a].dialogsAdapter.pause();
                    a++;
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
        if (filterTabsView2 != null && filterTabsView2.isEditing()) {
            this.filterTabsView.setIsEditing(false);
            showDoneItem(false);
            return false;
        } else if (this.actionBar == null || !this.actionBar.isActionModeShowed()) {
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
        } else {
            if (this.searchViewPager.getVisibility() == 0) {
                this.searchViewPager.hideActionMode();
                hideActionMode(true);
            } else {
                hideActionMode(true);
            }
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public void onBecomeFullyHidden() {
        if (this.closeSearchFieldOnHide) {
            if (this.actionBar != null) {
                this.actionBar.closeSearchField();
            }
            if (this.searchObject != null) {
                this.searchViewPager.dialogsSearchAdapter.putRecentSearch(this.searchDialogId, this.searchObject);
                this.searchObject = null;
            }
            this.closeSearchFieldOnHide = false;
        }
        FilterTabsView filterTabsView2 = this.filterTabsView;
        if (filterTabsView2 != null && filterTabsView2.getVisibility() == 0 && this.filterTabsViewIsVisible) {
            int scrollY = (int) (-this.actionBar.getTranslationY());
            int actionBarHeight = ActionBar.getCurrentActionBarHeight();
            if (!(scrollY == 0 || scrollY == actionBarHeight)) {
                if (scrollY < actionBarHeight / 2) {
                    setScrollY(0.0f);
                } else if (this.viewPages[0].listView.canScrollVertically(1)) {
                    setScrollY((float) (-actionBarHeight));
                }
            }
        }
        UndoView[] undoViewArr = this.undoView;
        if (undoViewArr[0] != null) {
            undoViewArr[0].hide(true, 0);
        }
    }

    /* access modifiers changed from: protected */
    public void setInPreviewMode(boolean value) {
        super.setInPreviewMode(value);
        if (!value && this.avatarContainer != null) {
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

    public boolean addOrRemoveSelectedDialog(long did, View cell) {
        if (this.selectedDialogs.contains(Long.valueOf(did))) {
            this.selectedDialogs.remove(Long.valueOf(did));
            if (cell instanceof DialogCell) {
                ((DialogCell) cell).setChecked(false, true);
            } else if (cell instanceof ProfileSearchCell) {
                ((ProfileSearchCell) cell).setChecked(false, true);
            }
            return false;
        }
        this.selectedDialogs.add(Long.valueOf(did));
        if (cell instanceof DialogCell) {
            ((DialogCell) cell).setChecked(true, true);
        } else if (cell instanceof ProfileSearchCell) {
            ((ProfileSearchCell) cell).setChecked(true, true);
        }
        return true;
    }

    public void search(String query, boolean animated) {
        showSearch(true, false, animated);
        this.actionBar.openSearchField(query, false);
    }

    /* access modifiers changed from: private */
    public void showSearch(boolean show, boolean startFromDownloads, boolean animated) {
        boolean animated2;
        FilterTabsView filterTabsView2;
        boolean onlyDialogsAdapter;
        final boolean z = show;
        int i = this.initialDialogsType;
        if (i == 0 || i == 3) {
            animated2 = animated;
        } else {
            animated2 = false;
        }
        AnimatorSet animatorSet = this.searchAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.searchAnimator = null;
        }
        Animator animator = this.tabsAlphaAnimator;
        if (animator != null) {
            animator.cancel();
            this.tabsAlphaAnimator = null;
        }
        this.searchIsShowed = z;
        ((SizeNotifierFrameLayout) this.fragmentView).invalidateBlur();
        boolean z2 = true;
        int i2 = 0;
        if (z) {
            if (this.searchFiltersWasShowed) {
                onlyDialogsAdapter = false;
            } else {
                onlyDialogsAdapter = onlyDialogsAdapter();
            }
            this.searchViewPager.showOnlyDialogsAdapter(onlyDialogsAdapter);
            boolean z3 = !onlyDialogsAdapter;
            this.whiteActionBar = z3;
            if (z3) {
                this.searchFiltersWasShowed = true;
            }
            ContentView contentView = (ContentView) this.fragmentView;
            ViewPagerFixed.TabsView tabsView = this.searchTabsView;
            if (tabsView == null && !onlyDialogsAdapter) {
                this.searchTabsView = this.searchViewPager.createTabsView();
                int filtersViewPosition = -1;
                if (this.filtersView != null) {
                    int i3 = 0;
                    while (true) {
                        if (i3 >= contentView.getChildCount()) {
                            break;
                        } else if (contentView.getChildAt(i3) == this.filtersView) {
                            filtersViewPosition = i3;
                            break;
                        } else {
                            i3++;
                        }
                    }
                }
                if (filtersViewPosition > 0) {
                    contentView.addView(this.searchTabsView, filtersViewPosition, LayoutHelper.createFrame(-1, 44.0f));
                } else {
                    contentView.addView(this.searchTabsView, LayoutHelper.createFrame(-1, 44.0f));
                }
            } else if (tabsView != null && onlyDialogsAdapter) {
                ViewParent parent = tabsView.getParent();
                if (parent instanceof ViewGroup) {
                    ((ViewGroup) parent).removeView(this.searchTabsView);
                }
                this.searchTabsView = null;
            }
            EditTextBoldCursor editText = this.searchItem.getSearchField();
            if (this.whiteActionBar) {
                editText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                editText.setHintTextColor(Theme.getColor("player_time"));
                editText.setCursorColor(Theme.getColor("chat_messagePanelCursor"));
            } else {
                editText.setCursorColor(Theme.getColor("actionBarDefaultSearch"));
                editText.setHintTextColor(Theme.getColor("actionBarDefaultSearchPlaceholder"));
                editText.setTextColor(Theme.getColor("actionBarDefaultSearch"));
            }
            this.searchViewPager.setKeyboardHeight(((ContentView) this.fragmentView).getKeyboardHeight());
            this.parentLayout.getDrawerLayoutContainer().setAllowOpenDrawerBySwipe(true);
            this.searchViewPager.clear();
            if (this.folderId != 0) {
                addSearchFilter(new FiltersView.MediaFilterData(NUM, LocaleController.getString("ArchiveSearchFilter", NUM), (TLRPC.MessagesFilter) null, 7));
            }
        } else if (!(this.filterTabsView == null || this.parentLayout == null)) {
            this.parentLayout.getDrawerLayoutContainer().setAllowOpenDrawerBySwipe(this.viewPages[0].selectedType == this.filterTabsView.getFirstTabId() || SharedConfig.getChatSwipeAction(this.currentAccount) != 5);
        }
        if (!animated2 || !this.searchViewPager.dialogsSearchAdapter.hasRecentSearch()) {
            AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        } else {
            AndroidUtilities.setAdjustResizeToNothing(getParentActivity(), this.classGuid);
        }
        if (!z && (filterTabsView2 = this.filterTabsView) != null && this.canShowFilterTabsView) {
            filterTabsView2.setVisibility(0);
        }
        float f = 0.9f;
        float f2 = 0.0f;
        if (animated2) {
            if (z) {
                this.searchViewPager.setVisibility(0);
                this.searchViewPager.reset();
                updateFiltersView(true, (ArrayList<Object>) null, (ArrayList<FiltersView.DateData>) null, false, false);
                ViewPagerFixed.TabsView tabsView2 = this.searchTabsView;
                if (tabsView2 != null) {
                    tabsView2.hide(false, false);
                    this.searchTabsView.setVisibility(0);
                }
            } else {
                this.viewPages[0].listView.setVisibility(0);
                this.viewPages[0].setVisibility(0);
            }
            setDialogsListFrozen(true);
            this.viewPages[0].listView.setVerticalScrollBarEnabled(false);
            this.searchViewPager.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.searchAnimator = new AnimatorSet();
            ArrayList<Animator> animators = new ArrayList<>();
            ViewPage viewPage = this.viewPages[0];
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = z ? 0.0f : 1.0f;
            animators.add(ObjectAnimator.ofFloat(viewPage, property, fArr));
            ViewPage viewPage2 = this.viewPages[0];
            Property property2 = View.SCALE_X;
            float[] fArr2 = new float[1];
            fArr2[0] = z ? 0.9f : 1.0f;
            animators.add(ObjectAnimator.ofFloat(viewPage2, property2, fArr2));
            ViewPage viewPage3 = this.viewPages[0];
            Property property3 = View.SCALE_Y;
            float[] fArr3 = new float[1];
            if (!z) {
                f = 1.0f;
            }
            fArr3[0] = f;
            animators.add(ObjectAnimator.ofFloat(viewPage3, property3, fArr3));
            SearchViewPager searchViewPager2 = this.searchViewPager;
            Property property4 = View.ALPHA;
            float[] fArr4 = new float[1];
            fArr4[0] = z ? 1.0f : 0.0f;
            animators.add(ObjectAnimator.ofFloat(searchViewPager2, property4, fArr4));
            SearchViewPager searchViewPager3 = this.searchViewPager;
            Property property5 = View.SCALE_X;
            float[] fArr5 = new float[1];
            float f3 = 1.05f;
            fArr5[0] = z ? 1.0f : 1.05f;
            animators.add(ObjectAnimator.ofFloat(searchViewPager3, property5, fArr5));
            SearchViewPager searchViewPager4 = this.searchViewPager;
            Property property6 = View.SCALE_Y;
            float[] fArr6 = new float[1];
            if (z) {
                f3 = 1.0f;
            }
            fArr6[0] = f3;
            animators.add(ObjectAnimator.ofFloat(searchViewPager4, property6, fArr6));
            ActionBarMenuItem actionBarMenuItem = this.passcodeItem;
            if (actionBarMenuItem != null) {
                RLottieImageView iconView = actionBarMenuItem.getIconView();
                Property property7 = View.ALPHA;
                float[] fArr7 = new float[1];
                fArr7[0] = z ? 0.0f : 1.0f;
                animators.add(ObjectAnimator.ofFloat(iconView, property7, fArr7));
            }
            ActionBarMenuItem actionBarMenuItem2 = this.downloadsItem;
            if (actionBarMenuItem2 != null) {
                if (z) {
                    actionBarMenuItem2.setAlpha(0.0f);
                } else {
                    animators.add(ObjectAnimator.ofFloat(actionBarMenuItem2, View.ALPHA, new float[]{1.0f}));
                }
                updateProxyButton(false, false);
            }
            FilterTabsView filterTabsView3 = this.filterTabsView;
            if (filterTabsView3 != null && filterTabsView3.getVisibility() == 0) {
                RecyclerListView tabsContainer = this.filterTabsView.getTabsContainer();
                Property property8 = View.ALPHA;
                float[] fArr8 = new float[1];
                fArr8[0] = z ? 0.0f : 1.0f;
                ObjectAnimator duration = ObjectAnimator.ofFloat(tabsContainer, property8, fArr8).setDuration(100);
                this.tabsAlphaAnimator = duration;
                duration.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        Animator unused = DialogsActivity.this.tabsAlphaAnimator = null;
                    }
                });
            }
            float[] fArr9 = new float[2];
            fArr9[0] = this.searchAnimationProgress;
            if (z) {
                f2 = 1.0f;
            }
            fArr9[1] = f2;
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(fArr9);
            valueAnimator.addUpdateListener(new DialogsActivity$$ExternalSyntheticLambda54(this));
            animators.add(valueAnimator);
            this.searchAnimator.playTogether(animators);
            this.searchAnimator.setDuration(z ? 200 : 180);
            this.searchAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            if (this.filterTabsViewIsVisible) {
                int backgroundColor1 = Theme.getColor(this.folderId == 0 ? "actionBarDefault" : "actionBarDefaultArchived");
                int backgroundColor2 = Theme.getColor("windowBackgroundWhite");
                if (((float) ((Math.abs(Color.red(backgroundColor1) - Color.red(backgroundColor2)) + Math.abs(Color.green(backgroundColor1) - Color.green(backgroundColor2))) + Math.abs(Color.blue(backgroundColor1) - Color.blue(backgroundColor2)))) / 255.0f <= 0.3f) {
                    z2 = false;
                }
                this.searchAnimationTabsDelayedCrossfade = z2;
            } else {
                this.searchAnimationTabsDelayedCrossfade = true;
            }
            if (!z) {
                this.searchAnimator.setStartDelay(20);
                Animator animator2 = this.tabsAlphaAnimator;
                if (animator2 != null) {
                    if (this.searchAnimationTabsDelayedCrossfade) {
                        animator2.setStartDelay(80);
                        this.tabsAlphaAnimator.setDuration(100);
                    } else {
                        animator2.setDuration(z ? 200 : 180);
                    }
                }
            }
            this.searchAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    DialogsActivity.this.getNotificationCenter().onAnimationFinish(DialogsActivity.this.animationIndex);
                    if (DialogsActivity.this.searchAnimator == animation) {
                        DialogsActivity.this.setDialogsListFrozen(false);
                        if (z) {
                            DialogsActivity.this.viewPages[0].listView.hide();
                            if (DialogsActivity.this.filterTabsView != null) {
                                DialogsActivity.this.filterTabsView.setVisibility(8);
                            }
                            boolean unused = DialogsActivity.this.searchWasFullyShowed = true;
                            AndroidUtilities.requestAdjustResize(DialogsActivity.this.getParentActivity(), DialogsActivity.this.classGuid);
                            DialogsActivity.this.searchItem.setVisibility(8);
                        } else {
                            DialogsActivity.this.searchItem.collapseSearchFilters();
                            boolean unused2 = DialogsActivity.this.whiteActionBar = false;
                            DialogsActivity.this.searchViewPager.setVisibility(8);
                            if (DialogsActivity.this.searchTabsView != null) {
                                DialogsActivity.this.searchTabsView.setVisibility(8);
                            }
                            DialogsActivity.this.searchItem.clearSearchFilters();
                            DialogsActivity.this.searchViewPager.clear();
                            DialogsActivity.this.filtersView.setVisibility(8);
                            DialogsActivity.this.viewPages[0].listView.show();
                            if (!DialogsActivity.this.onlySelect) {
                                DialogsActivity.this.hideFloatingButton(false);
                            }
                            boolean unused3 = DialogsActivity.this.searchWasFullyShowed = false;
                        }
                        if (DialogsActivity.this.fragmentView != null) {
                            DialogsActivity.this.fragmentView.requestLayout();
                        }
                        float f = 1.0f;
                        DialogsActivity.this.setSearchAnimationProgress(z ? 1.0f : 0.0f);
                        DialogsActivity.this.viewPages[0].listView.setVerticalScrollBarEnabled(true);
                        DialogsActivity.this.searchViewPager.setBackground((Drawable) null);
                        AnimatorSet unused4 = DialogsActivity.this.searchAnimator = null;
                        if (DialogsActivity.this.downloadsItem != null) {
                            ActionBarMenuItem access$17800 = DialogsActivity.this.downloadsItem;
                            if (z) {
                                f = 0.0f;
                            }
                            access$17800.setAlpha(f);
                        }
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    DialogsActivity.this.getNotificationCenter().onAnimationFinish(DialogsActivity.this.animationIndex);
                    if (DialogsActivity.this.searchAnimator == animation) {
                        if (z) {
                            DialogsActivity.this.viewPages[0].listView.hide();
                        } else {
                            DialogsActivity.this.viewPages[0].listView.show();
                        }
                        AnimatorSet unused = DialogsActivity.this.searchAnimator = null;
                    }
                }
            });
            this.animationIndex = getNotificationCenter().setAnimationInProgress(this.animationIndex, (int[]) null);
            this.searchAnimator.start();
            Animator animator3 = this.tabsAlphaAnimator;
            if (animator3 != null) {
                animator3.start();
            }
        } else {
            setDialogsListFrozen(false);
            if (z) {
                this.viewPages[0].listView.hide();
            } else {
                this.viewPages[0].listView.show();
            }
            this.viewPages[0].setAlpha(z ? 0.0f : 1.0f);
            this.viewPages[0].setScaleX(z ? 0.9f : 1.0f);
            ViewPage viewPage4 = this.viewPages[0];
            if (!z) {
                f = 1.0f;
            }
            viewPage4.setScaleY(f);
            this.searchViewPager.setAlpha(z ? 1.0f : 0.0f);
            this.filtersView.setAlpha(z ? 1.0f : 0.0f);
            float f4 = 1.1f;
            this.searchViewPager.setScaleX(z ? 1.0f : 1.1f);
            SearchViewPager searchViewPager5 = this.searchViewPager;
            if (z) {
                f4 = 1.0f;
            }
            searchViewPager5.setScaleY(f4);
            FilterTabsView filterTabsView4 = this.filterTabsView;
            if (filterTabsView4 != null && filterTabsView4.getVisibility() == 0) {
                this.filterTabsView.setTranslationY(z ? (float) (-AndroidUtilities.dp(44.0f)) : 0.0f);
                this.filterTabsView.getTabsContainer().setAlpha(z ? 0.0f : 1.0f);
            }
            FilterTabsView filterTabsView5 = this.filterTabsView;
            if (filterTabsView5 != null) {
                if (!this.canShowFilterTabsView || z) {
                    filterTabsView5.setVisibility(8);
                } else {
                    filterTabsView5.setVisibility(0);
                }
            }
            SearchViewPager searchViewPager6 = this.searchViewPager;
            if (!z) {
                i2 = 8;
            }
            searchViewPager6.setVisibility(i2);
            setSearchAnimationProgress(z ? 1.0f : 0.0f);
            this.fragmentView.invalidate();
            ActionBarMenuItem actionBarMenuItem3 = this.downloadsItem;
            if (actionBarMenuItem3 != null) {
                if (!z) {
                    f2 = 1.0f;
                }
                actionBarMenuItem3.setAlpha(f2);
            }
        }
        int i4 = this.initialSearchType;
        if (i4 >= 0) {
            SearchViewPager searchViewPager7 = this.searchViewPager;
            searchViewPager7.setPosition(searchViewPager7.getPositionForType(i4));
        }
        if (!z) {
            this.initialSearchType = -1;
        }
        if (z && startFromDownloads) {
            this.searchViewPager.showDownloads();
        }
    }

    /* renamed from: lambda$showSearch$20$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3420lambda$showSearch$20$orgtelegramuiDialogsActivity(ValueAnimator valueAnimator1) {
        setSearchAnimationProgress(((Float) valueAnimator1.getAnimatedValue()).floatValue());
    }

    public boolean onlyDialogsAdapter() {
        return this.onlySelect || !this.searchViewPager.dialogsSearchAdapter.hasRecentSearch() || getMessagesController().getTotalDialogsCount() <= 10;
    }

    private void updateFilterTabsVisibility(boolean animated) {
        if (this.isPaused || this.databaseMigrationHint != null) {
            animated = false;
        }
        float f = 1.0f;
        if (this.searchIsShowed) {
            ValueAnimator valueAnimator = this.filtersTabAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            boolean z = this.canShowFilterTabsView;
            this.filterTabsViewIsVisible = z;
            if (!z) {
                f = 0.0f;
            }
            this.filterTabsProgress = f;
            return;
        }
        final boolean visible = this.canShowFilterTabsView;
        if (this.filterTabsViewIsVisible != visible) {
            ValueAnimator valueAnimator2 = this.filtersTabAnimator;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
            }
            this.filterTabsViewIsVisible = visible;
            int i = 0;
            if (animated) {
                if (visible) {
                    if (this.filterTabsView.getVisibility() != 0) {
                        this.filterTabsView.setVisibility(0);
                    }
                    this.filtersTabAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                    this.filterTabsMoveFrom = (float) AndroidUtilities.dp(44.0f);
                } else {
                    this.filtersTabAnimator = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
                    this.filterTabsMoveFrom = Math.max(0.0f, ((float) AndroidUtilities.dp(44.0f)) + this.actionBar.getTranslationY());
                }
                float animateFromScrollY = this.actionBar.getTranslationY();
                this.filtersTabAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        ValueAnimator unused = DialogsActivity.this.filtersTabAnimator = null;
                        float unused2 = DialogsActivity.this.scrollAdditionalOffset = ((float) AndroidUtilities.dp(44.0f)) - DialogsActivity.this.filterTabsMoveFrom;
                        if (!visible) {
                            DialogsActivity.this.filterTabsView.setVisibility(8);
                        }
                        if (DialogsActivity.this.fragmentView != null) {
                            DialogsActivity.this.fragmentView.requestLayout();
                        }
                        DialogsActivity.this.getNotificationCenter().onAnimationFinish(DialogsActivity.this.animationIndex);
                    }
                });
                this.filtersTabAnimator.addUpdateListener(new DialogsActivity$$ExternalSyntheticLambda55(this, visible, animateFromScrollY));
                this.filtersTabAnimator.setDuration(220);
                this.filtersTabAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.animationIndex = getNotificationCenter().setAnimationInProgress(this.animationIndex, (int[]) null);
                this.filtersTabAnimator.start();
                this.fragmentView.requestLayout();
                return;
            }
            if (!visible) {
                f = 0.0f;
            }
            this.filterTabsProgress = f;
            FilterTabsView filterTabsView2 = this.filterTabsView;
            if (!visible) {
                i = 8;
            }
            filterTabsView2.setVisibility(i);
            if (this.fragmentView != null) {
                this.fragmentView.invalidate();
            }
        }
    }

    /* renamed from: lambda$updateFilterTabsVisibility$21$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3423xa989b9b5(boolean visible, float animateFromScrollY, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.filterTabsProgress = floatValue;
        if (!visible) {
            setScrollY(floatValue * animateFromScrollY);
        }
        if (this.fragmentView != null) {
            this.fragmentView.invalidate();
        }
    }

    /* access modifiers changed from: private */
    public void setSearchAnimationProgress(float progress) {
        this.searchAnimationProgress = progress;
        if (this.whiteActionBar) {
            this.actionBar.setItemsColor(ColorUtils.blendARGB(Theme.getColor(this.folderId != 0 ? "actionBarDefaultArchivedIcon" : "actionBarDefaultIcon"), Theme.getColor("windowBackgroundWhiteGrayText2"), this.searchAnimationProgress), false);
            this.actionBar.setItemsColor(ColorUtils.blendARGB(Theme.getColor("actionBarActionModeDefaultIcon"), Theme.getColor("windowBackgroundWhiteGrayText2"), this.searchAnimationProgress), true);
            this.actionBar.setItemsBackgroundColor(ColorUtils.blendARGB(Theme.getColor(this.folderId != 0 ? "actionBarDefaultArchivedSelector" : "actionBarDefaultSelector"), Theme.getColor("actionBarActionModeDefaultSelector"), this.searchAnimationProgress), false);
        }
        if (this.fragmentView != null) {
            this.fragmentView.invalidate();
        }
        updateContextViewPosition();
    }

    /* access modifiers changed from: private */
    public void findAndUpdateCheckBox(long dialogId, boolean checked) {
        if (this.viewPages != null) {
            int b = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (b < viewPageArr.length) {
                    int count = viewPageArr[b].listView.getChildCount();
                    int a = 0;
                    while (true) {
                        if (a >= count) {
                            break;
                        }
                        View child = this.viewPages[b].listView.getChildAt(a);
                        if (child instanceof DialogCell) {
                            DialogCell dialogCell = (DialogCell) child;
                            if (dialogCell.getDialogId() == dialogId) {
                                dialogCell.setChecked(checked, true);
                                break;
                            }
                        }
                        a++;
                    }
                    b++;
                } else {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0130  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0133  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x013b A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void checkListLoad(org.telegram.ui.DialogsActivity.ViewPage r17) {
        /*
            r16 = this;
            r6 = r16
            boolean r0 = r6.tabsAnimationInProgress
            if (r0 != 0) goto L_0x014d
            boolean r0 = r6.startedTracking
            if (r0 != 0) goto L_0x014d
            org.telegram.ui.Components.FilterTabsView r0 = r6.filterTabsView
            if (r0 == 0) goto L_0x001e
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x001e
            org.telegram.ui.Components.FilterTabsView r0 = r6.filterTabsView
            boolean r0 = r0.isAnimatingIndicator()
            if (r0 == 0) goto L_0x001e
            goto L_0x014d
        L_0x001e:
            androidx.recyclerview.widget.LinearLayoutManager r0 = r17.layoutManager
            int r7 = r0.findFirstVisibleItemPosition()
            androidx.recyclerview.widget.LinearLayoutManager r0 = r17.layoutManager
            int r8 = r0.findLastVisibleItemPosition()
            androidx.recyclerview.widget.LinearLayoutManager r0 = r17.layoutManager
            int r0 = r0.findLastVisibleItemPosition()
            int r0 = r0 - r7
            int r0 = java.lang.Math.abs(r0)
            r1 = 1
            int r9 = r0 + 1
            r0 = -1
            r2 = 0
            if (r8 == r0) goto L_0x005f
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r17.listView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findViewHolderForAdapterPosition(r8)
            if (r0 == 0) goto L_0x0056
            int r3 = r0.getItemViewType()
            r4 = 11
            if (r3 != r4) goto L_0x0056
            r3 = 1
            goto L_0x0057
        L_0x0056:
            r3 = 0
        L_0x0057:
            r6.floatingForceVisible = r3
            if (r3 == 0) goto L_0x005e
            r6.hideFloatingButton(r2)
        L_0x005e:
            goto L_0x0061
        L_0x005f:
            r6.floatingForceVisible = r2
        L_0x0061:
            r0 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            int r5 = r17.dialogsType
            r10 = 8
            r11 = 7
            if (r5 == r11) goto L_0x0074
            int r5 = r17.dialogsType
            if (r5 != r10) goto L_0x00e1
        L_0x0074:
            org.telegram.messenger.MessagesController r5 = r16.getMessagesController()
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r5 = r5.dialogFilters
            int r12 = r17.selectedType
            if (r12 < 0) goto L_0x00e1
            int r12 = r17.selectedType
            int r13 = r5.size()
            if (r12 >= r13) goto L_0x00e1
            org.telegram.messenger.MessagesController r12 = r16.getMessagesController()
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r12 = r12.dialogFilters
            int r13 = r17.selectedType
            java.lang.Object r12 = r12.get(r13)
            org.telegram.messenger.MessagesController$DialogFilter r12 = (org.telegram.messenger.MessagesController.DialogFilter) r12
            int r13 = r12.flags
            int r14 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED
            r13 = r13 & r14
            if (r13 != 0) goto L_0x00e1
            if (r9 <= 0) goto L_0x00b7
            int r13 = r6.currentAccount
            int r14 = r17.dialogsType
            boolean r15 = r6.dialogsListFrozen
            java.util.ArrayList r13 = r6.getDialogsArray(r13, r14, r1, r15)
            int r13 = r13.size()
            int r13 = r13 + -10
            if (r8 >= r13) goto L_0x00c3
        L_0x00b7:
            if (r9 != 0) goto L_0x00e1
            org.telegram.messenger.MessagesController r13 = r16.getMessagesController()
            boolean r13 = r13.isDialogsEndReached(r1)
            if (r13 != 0) goto L_0x00e1
        L_0x00c3:
            org.telegram.messenger.MessagesController r13 = r16.getMessagesController()
            boolean r13 = r13.isDialogsEndReached(r1)
            r13 = r13 ^ r1
            r2 = r13
            if (r2 != 0) goto L_0x00dd
            org.telegram.messenger.MessagesController r13 = r16.getMessagesController()
            boolean r13 = r13.isServerDialogsEndReached(r1)
            if (r13 != 0) goto L_0x00da
            goto L_0x00dd
        L_0x00da:
            r12 = r0
            r13 = r2
            goto L_0x00e3
        L_0x00dd:
            r0 = 1
            r12 = r0
            r13 = r2
            goto L_0x00e3
        L_0x00e1:
            r12 = r0
            r13 = r2
        L_0x00e3:
            if (r9 <= 0) goto L_0x00fb
            int r0 = r6.currentAccount
            int r2 = r17.dialogsType
            int r5 = r6.folderId
            boolean r14 = r6.dialogsListFrozen
            java.util.ArrayList r0 = r6.getDialogsArray(r0, r2, r5, r14)
            int r0 = r0.size()
            int r0 = r0 + -10
            if (r8 >= r0) goto L_0x0115
        L_0x00fb:
            if (r9 != 0) goto L_0x0137
            int r0 = r17.dialogsType
            if (r0 == r11) goto L_0x0109
            int r0 = r17.dialogsType
            if (r0 != r10) goto L_0x0137
        L_0x0109:
            org.telegram.messenger.MessagesController r0 = r16.getMessagesController()
            int r2 = r6.folderId
            boolean r0 = r0.isDialogsEndReached(r2)
            if (r0 != 0) goto L_0x0137
        L_0x0115:
            org.telegram.messenger.MessagesController r0 = r16.getMessagesController()
            int r2 = r6.folderId
            boolean r0 = r0.isDialogsEndReached(r2)
            r0 = r0 ^ r1
            r4 = r0
            if (r4 != 0) goto L_0x0133
            org.telegram.messenger.MessagesController r0 = r16.getMessagesController()
            int r1 = r6.folderId
            boolean r0 = r0.isServerDialogsEndReached(r1)
            if (r0 != 0) goto L_0x0130
            goto L_0x0133
        L_0x0130:
            r10 = r3
            r11 = r4
            goto L_0x0139
        L_0x0133:
            r3 = 1
            r10 = r3
            r11 = r4
            goto L_0x0139
        L_0x0137:
            r10 = r3
            r11 = r4
        L_0x0139:
            if (r10 != 0) goto L_0x013d
            if (r12 == 0) goto L_0x014c
        L_0x013d:
            r2 = r10
            r3 = r11
            r4 = r12
            r5 = r13
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda41 r14 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda41
            r0 = r14
            r1 = r16
            r0.<init>(r1, r2, r3, r4, r5)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r14)
        L_0x014c:
            return
        L_0x014d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.checkListLoad(org.telegram.ui.DialogsActivity$ViewPage):void");
    }

    /* renamed from: lambda$checkListLoad$22$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3372lambda$checkListLoad$22$orgtelegramuiDialogsActivity(boolean loadFinal, boolean loadFromCacheFinal, boolean loadArchivedFinal, boolean loadArchivedFromCacheFinal) {
        if (loadFinal) {
            getMessagesController().loadDialogs(this.folderId, -1, 100, loadFromCacheFinal);
        }
        if (loadArchivedFinal) {
            getMessagesController().loadDialogs(1, -1, 100, loadArchivedFromCacheFinal);
        }
    }

    private void onItemClick(View view, int position, RecyclerView.Adapter adapter) {
        long dialogId;
        TLRPC.Document sticker;
        long did;
        long did2;
        long dialogId2;
        long dialogId3;
        int filterId;
        String hash;
        TLRPC.ChatInvite invite;
        View view2 = view;
        int i = position;
        RecyclerView.Adapter adapter2 = adapter;
        if (getParentActivity() != null) {
            int message_id = 0;
            boolean isGlobalSearch = false;
            int folderId2 = 0;
            int filterId2 = 0;
            if (adapter2 instanceof DialogsAdapter) {
                DialogsAdapter dialogsAdapter = (DialogsAdapter) adapter2;
                int dialogsType = dialogsAdapter.getDialogsType();
                if (dialogsType == 7 || dialogsType == 8) {
                    filterId = getMessagesController().selectedDialogFilter[dialogsType == 7 ? (char) 0 : 1].id;
                } else {
                    filterId = 0;
                }
                TLObject object = dialogsAdapter.getItem(i);
                if (object instanceof TLRPC.User) {
                    dialogId = ((TLRPC.User) object).id;
                } else if (object instanceof TLRPC.Dialog) {
                    TLRPC.Dialog dialog = (TLRPC.Dialog) object;
                    int folderId3 = dialog.folder_id;
                    if ((dialog instanceof TLRPC.TL_dialogFolder) == 0) {
                        dialogId = dialog.id;
                        if (this.actionBar.isActionModeShowed((String) null)) {
                            showOrUpdateActionMode(dialogId, view2);
                            return;
                        }
                        folderId2 = folderId3;
                    } else if (!this.actionBar.isActionModeShowed((String) null)) {
                        Bundle args = new Bundle();
                        DialogsAdapter dialogsAdapter2 = dialogsAdapter;
                        args.putInt("folderId", ((TLRPC.TL_dialogFolder) dialog).folder.id);
                        presentFragment(new DialogsActivity(args));
                        return;
                    } else {
                        return;
                    }
                } else {
                    if (object instanceof TLRPC.TL_recentMeUrlChat) {
                        dialogId = -((TLRPC.TL_recentMeUrlChat) object).chat_id;
                    } else if (object instanceof TLRPC.TL_recentMeUrlUser) {
                        dialogId = ((TLRPC.TL_recentMeUrlUser) object).user_id;
                    } else if (object instanceof TLRPC.TL_recentMeUrlChatInvite) {
                        TLRPC.TL_recentMeUrlChatInvite chatInvite = (TLRPC.TL_recentMeUrlChatInvite) object;
                        TLRPC.ChatInvite invite2 = chatInvite.chat_invite;
                        if (invite2.chat != null || (invite2.channel && !invite2.megagroup)) {
                            if (invite2.chat == null) {
                                invite = invite2;
                                TLRPC.TL_recentMeUrlChatInvite tL_recentMeUrlChatInvite = chatInvite;
                                TLObject tLObject = object;
                            } else if (ChatObject.isChannel(invite2.chat) && !invite2.chat.megagroup) {
                                invite = invite2;
                                TLRPC.TL_recentMeUrlChatInvite tL_recentMeUrlChatInvite2 = chatInvite;
                                TLObject tLObject2 = object;
                            }
                            TLRPC.ChatInvite invite3 = invite;
                            if (invite3.chat != null) {
                                dialogId = -invite3.chat.id;
                            } else {
                                return;
                            }
                        }
                        String hash2 = chatInvite.url;
                        int index = hash2.indexOf(47);
                        if (index > 0) {
                            hash = hash2.substring(index + 1);
                        } else {
                            hash = hash2;
                        }
                        TLRPC.TL_recentMeUrlChatInvite tL_recentMeUrlChatInvite3 = chatInvite;
                        TLObject tLObject3 = object;
                        JoinGroupAlert joinGroupAlert = r0;
                        JoinGroupAlert joinGroupAlert2 = new JoinGroupAlert(getParentActivity(), invite2, hash, this, (Theme.ResourcesProvider) null);
                        showDialog(joinGroupAlert);
                        return;
                    } else {
                        TLObject object2 = object;
                        if (object2 instanceof TLRPC.TL_recentMeUrlStickerSet) {
                            TLRPC.StickerSet stickerSet = ((TLRPC.TL_recentMeUrlStickerSet) object2).set.set;
                            TLRPC.TL_inputStickerSetID set = new TLRPC.TL_inputStickerSetID();
                            set.id = stickerSet.id;
                            set.access_hash = stickerSet.access_hash;
                            TLRPC.StickerSet stickerSet2 = stickerSet;
                            StickersAlert stickersAlert = r0;
                            TLRPC.TL_inputStickerSetID tL_inputStickerSetID = set;
                            StickersAlert stickersAlert2 = new StickersAlert((Context) getParentActivity(), (BaseFragment) this, (TLRPC.InputStickerSet) set, (TLRPC.TL_messages_stickerSet) null, (StickersAlert.StickersAlertDelegate) null);
                            showDialog(stickersAlert);
                            return;
                        }
                        boolean z = object2 instanceof TLRPC.TL_recentMeUrlUnknown;
                        return;
                    }
                }
                filterId2 = filterId;
            } else if (adapter2 == this.searchViewPager.dialogsSearchAdapter) {
                Object obj = this.searchViewPager.dialogsSearchAdapter.getItem(i);
                isGlobalSearch = this.searchViewPager.dialogsSearchAdapter.isGlobalSearch(i);
                if (obj instanceof TLRPC.User) {
                    dialogId3 = ((TLRPC.User) obj).id;
                    if (!this.onlySelect) {
                        this.searchDialogId = dialogId3;
                        this.searchObject = (TLRPC.User) obj;
                    }
                } else if (obj instanceof TLRPC.Chat) {
                    dialogId3 = -((TLRPC.Chat) obj).id;
                    if (!this.onlySelect) {
                        this.searchDialogId = dialogId3;
                        this.searchObject = (TLRPC.Chat) obj;
                    }
                } else if (obj instanceof TLRPC.EncryptedChat) {
                    dialogId3 = DialogObject.makeEncryptedDialogId((long) ((TLRPC.EncryptedChat) obj).id);
                    if (!this.onlySelect) {
                        this.searchDialogId = dialogId3;
                        this.searchObject = (TLRPC.EncryptedChat) obj;
                    }
                } else {
                    if (obj instanceof MessageObject) {
                        MessageObject messageObject = (MessageObject) obj;
                        dialogId2 = messageObject.getDialogId();
                        int message_id2 = messageObject.getId();
                        this.searchViewPager.dialogsSearchAdapter.addHashtagsFromMessage(this.searchViewPager.dialogsSearchAdapter.getLastSearchString());
                        message_id = message_id2;
                    } else {
                        if (obj instanceof String) {
                            String str = (String) obj;
                            if (this.searchViewPager.dialogsSearchAdapter.isHashtagSearch()) {
                                this.actionBar.openSearchField(str, false);
                            } else if (!str.equals("section")) {
                                NewContactActivity activity = new NewContactActivity();
                                activity.setInitialPhoneNumber(str, true);
                                presentFragment(activity);
                            }
                        }
                        dialogId2 = 0;
                    }
                    if (dialogId != 0 && this.actionBar.isActionModeShowed()) {
                        if (this.actionBar.isActionModeShowed("search_dialogs_action_mode") && message_id == 0 && !isGlobalSearch) {
                            showOrUpdateActionMode(dialogId, view2);
                            return;
                        }
                        return;
                    }
                }
                dialogId2 = dialogId3;
                if (this.actionBar.isActionModeShowed("search_dialogs_action_mode")) {
                    return;
                }
                return;
            } else {
                dialogId = 0;
            }
            if (dialogId != 0) {
                if (!this.onlySelect) {
                    Bundle args2 = new Bundle();
                    if (DialogObject.isEncryptedDialog(dialogId)) {
                        args2.putInt("enc_id", DialogObject.getEncryptedChatId(dialogId));
                    } else if (DialogObject.isUserDialog(dialogId)) {
                        args2.putLong("user_id", dialogId);
                    } else {
                        long did3 = dialogId;
                        if (message_id != 0) {
                            TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(-did3));
                            if (chat == null || chat.migrated_to == null) {
                                did2 = did3;
                            } else {
                                args2.putLong("migrated_to", did3);
                                long j = did3;
                                did = -chat.migrated_to.channel_id;
                                args2.putLong("chat_id", -did);
                            }
                        } else {
                            did2 = did3;
                        }
                        did = did2;
                        args2.putLong("chat_id", -did);
                    }
                    if (message_id != 0) {
                        args2.putInt("message_id", message_id);
                    } else if (!isGlobalSearch) {
                        closeSearch();
                    } else if (this.searchObject != null) {
                        this.searchViewPager.dialogsSearchAdapter.putRecentSearch(this.searchDialogId, this.searchObject);
                        this.searchObject = null;
                    }
                    args2.putInt("dialog_folder_id", folderId2);
                    args2.putInt("dialog_filter_id", filterId2);
                    if (AndroidUtilities.isTablet()) {
                        if (this.openedDialogId != dialogId || adapter2 == this.searchViewPager.dialogsSearchAdapter) {
                            if (this.viewPages != null) {
                                int a = 0;
                                while (true) {
                                    ViewPage[] viewPageArr = this.viewPages;
                                    if (a >= viewPageArr.length) {
                                        break;
                                    }
                                    DialogsAdapter access$9100 = viewPageArr[a].dialogsAdapter;
                                    this.openedDialogId = dialogId;
                                    access$9100.setOpenedDialogId(dialogId);
                                    a++;
                                }
                            }
                            updateVisibleRows(MessagesController.UPDATE_MASK_SELECT_DIALOG);
                        } else {
                            return;
                        }
                    }
                    if (this.searchViewPager.actionModeShowing()) {
                        this.searchViewPager.hideActionMode();
                    }
                    if (this.searchString == null) {
                        this.slowedReloadAfterDialogClick = true;
                        if (getMessagesController().checkCanOpenChat(args2, this)) {
                            ChatActivity chatActivity = new ChatActivity(args2);
                            if ((adapter2 instanceof DialogsAdapter) && DialogObject.isUserDialog(dialogId) && getMessagesController().dialogs_dict.get(dialogId) == null && (sticker = getMediaDataController().getGreetingsSticker()) != null) {
                                chatActivity.setPreloadedSticker(sticker, true);
                            }
                            presentFragment(chatActivity);
                        }
                    } else if (getMessagesController().checkCanOpenChat(args2, this)) {
                        getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        presentFragment(new ChatActivity(args2));
                    }
                } else if (validateSlowModeDialog(dialogId)) {
                    if (this.selectedDialogs.isEmpty() && (this.initialDialogsType != 3 || this.selectAlertString == null)) {
                        didSelectResult(dialogId, true, false);
                    } else if (this.selectedDialogs.contains(Long.valueOf(dialogId)) || checkCanWrite(dialogId)) {
                        boolean checked = addOrRemoveSelectedDialog(dialogId, view2);
                        if (adapter2 == this.searchViewPager.dialogsSearchAdapter) {
                            this.actionBar.closeSearchField();
                            findAndUpdateCheckBox(dialogId, checked);
                        }
                        updateSelectedCount();
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean onItemLongClick(RecyclerListView listView, View view, int position, float x, float y, int dialogsType, RecyclerView.Adapter adapter) {
        TLRPC.Dialog dialog;
        long did;
        View view2 = view;
        int i = position;
        RecyclerView.Adapter adapter2 = adapter;
        if (getParentActivity() == null) {
            return false;
        }
        if (this.actionBar.isActionModeShowed() || AndroidUtilities.isTablet() || this.onlySelect || !(view2 instanceof DialogCell)) {
            float f = x;
            float f2 = y;
        } else {
            DialogCell cell = (DialogCell) view2;
            if (cell.isPointInsideAvatar(x, y)) {
                return showChatPreview(cell);
            }
        }
        if (adapter2 == this.searchViewPager.dialogsSearchAdapter) {
            Object item = this.searchViewPager.dialogsSearchAdapter.getItem(i);
            if (!this.searchViewPager.dialogsSearchAdapter.isSearchWas()) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("ClearSearchSingleAlertTitle", NUM));
                if (item instanceof TLRPC.Chat) {
                    TLRPC.Chat chat = (TLRPC.Chat) item;
                    builder.setMessage(LocaleController.formatString("ClearSearchSingleChatAlertText", NUM, chat.title));
                    did = -chat.id;
                } else if (item instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) item;
                    String str = "ClearSearchSingleUserAlertText";
                    String str2 = "ClearSearchSingleChatAlertText";
                    if (user.id == getUserConfig().clientUserId) {
                        builder.setMessage(LocaleController.formatString(str2, NUM, LocaleController.getString("SavedMessages", NUM)));
                    } else {
                        builder.setMessage(LocaleController.formatString(str, NUM, ContactsController.formatName(user.first_name, user.last_name)));
                    }
                    did = user.id;
                } else {
                    String str3 = "ClearSearchSingleUserAlertText";
                    if (!(item instanceof TLRPC.EncryptedChat)) {
                        return false;
                    }
                    TLRPC.EncryptedChat encryptedChat = (TLRPC.EncryptedChat) item;
                    TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(encryptedChat.user_id));
                    builder.setMessage(LocaleController.formatString(str3, NUM, ContactsController.formatName(user2.first_name, user2.last_name)));
                    did = DialogObject.makeEncryptedDialogId((long) encryptedChat.id);
                }
                builder.setPositiveButton(LocaleController.getString("ClearSearchRemove", NUM).toUpperCase(), new DialogsActivity$$ExternalSyntheticLambda3(this, did));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                AlertDialog alertDialog = builder.create();
                showDialog(alertDialog);
                TextView button = (TextView) alertDialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor("dialogTextRed2"));
                }
                return true;
            }
        }
        if (adapter2 != this.searchViewPager.dialogsSearchAdapter) {
            ArrayList<TLRPC.Dialog> dialogs = getDialogsArray(this.currentAccount, dialogsType, this.folderId, this.dialogsListFrozen);
            int position2 = ((DialogsAdapter) adapter2).fixPosition(i);
            if (position2 < 0 || position2 >= dialogs.size() || (dialog = dialogs.get(position2)) == null) {
                return false;
            }
            if (this.onlySelect) {
                int i2 = this.initialDialogsType;
                if ((i2 != 3 && i2 != 10) || !validateSlowModeDialog(dialog.id)) {
                    return false;
                }
                addOrRemoveSelectedDialog(dialog.id, view2);
                updateSelectedCount();
                return true;
            } else if (dialog instanceof TLRPC.TL_dialogFolder) {
                onArchiveLongPress(view2);
                return false;
            } else if (this.actionBar.isActionModeShowed() && isDialogPinned(dialog)) {
                return false;
            } else {
                showOrUpdateActionMode(dialog.id, view2);
                return true;
            }
        } else if (this.onlySelect) {
            onItemClick(view2, i, adapter2);
            return false;
        } else {
            long dialogId = 0;
            if ((view2 instanceof ProfileSearchCell) && !this.searchViewPager.dialogsSearchAdapter.isGlobalSearch(i)) {
                dialogId = ((ProfileSearchCell) view2).getDialogId();
            }
            if (dialogId == 0) {
                return false;
            }
            showOrUpdateActionMode(dialogId, view2);
            return true;
        }
    }

    /* renamed from: lambda$onItemLongClick$23$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3396lambda$onItemLongClick$23$orgtelegramuiDialogsActivity(long did, DialogInterface dialogInterface, int i) {
        this.searchViewPager.dialogsSearchAdapter.removeRecentSearch(did);
    }

    private void onArchiveLongPress(View view) {
        String str;
        int i;
        view.performHapticFeedback(0, 2);
        BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
        boolean hasUnread = getMessagesStorage().getArchiveUnreadCount() != 0;
        int[] icons = new int[2];
        icons[0] = hasUnread ? NUM : 0;
        icons[1] = SharedConfig.archiveHidden ? NUM : NUM;
        CharSequence[] items = new CharSequence[2];
        items[0] = hasUnread ? LocaleController.getString("MarkAllAsRead", NUM) : null;
        if (SharedConfig.archiveHidden) {
            i = NUM;
            str = "PinInTheList";
        } else {
            i = NUM;
            str = "HideAboveTheList";
        }
        items[1] = LocaleController.getString(str, i);
        builder.setItems(items, icons, new DialogsActivity$$ExternalSyntheticLambda56(this));
        showDialog(builder.create());
    }

    /* renamed from: lambda$onArchiveLongPress$24$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3394lambda$onArchiveLongPress$24$orgtelegramuiDialogsActivity(DialogInterface d, int which) {
        if (which == 0) {
            getMessagesStorage().readAllDialogs(1);
        } else if (which == 1 && this.viewPages != null) {
            int a = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (a < viewPageArr.length) {
                    if (viewPageArr[a].dialogsType == 0 && this.viewPages[a].getVisibility() == 0) {
                        View child = this.viewPages[a].listView.getChildAt(0);
                        DialogCell dialogCell = null;
                        if ((child instanceof DialogCell) && ((DialogCell) child).isFolderCell()) {
                            dialogCell = (DialogCell) child;
                        }
                        this.viewPages[a].listView.toggleArchiveHidden(true, dialogCell);
                    }
                    a++;
                } else {
                    return;
                }
            }
        }
    }

    /* JADX WARNING: type inference failed for: r5v1, types: [boolean] */
    /* JADX WARNING: type inference failed for: r5v4 */
    /* JADX WARNING: type inference failed for: r5v6 */
    public boolean showChatPreview(DialogCell cell) {
        MessagesController.DialogFilter dialogFilter;
        ChatActivity[] chatActivity;
        int flags;
        ActionBarMenuSubItem markAsUnreadItem;
        ? r5;
        ChatActivity[] chatActivity2;
        boolean z;
        int maxPinnedCount;
        TLRPC.Chat chat;
        if (cell.isDialogFolder()) {
            if (cell.getCurrentDialogFolderId() == 1) {
                onArchiveLongPress(cell);
            }
            return false;
        }
        long dialogId = cell.getDialogId();
        Bundle args = new Bundle();
        int message_id = cell.getMessageId();
        if (DialogObject.isEncryptedDialog(dialogId)) {
            return false;
        }
        if (DialogObject.isUserDialog(dialogId)) {
            args.putLong("user_id", dialogId);
        } else {
            long did = dialogId;
            if (!(message_id == 0 || (chat = getMessagesController().getChat(Long.valueOf(-did))) == null || chat.migrated_to == null)) {
                args.putLong("migrated_to", did);
                did = -chat.migrated_to.channel_id;
            }
            args.putLong("chat_id", -did);
        }
        if (message_id != 0) {
            args.putInt("message_id", message_id);
        }
        ArrayList<Long> dialogIdArray = new ArrayList<>();
        dialogIdArray.add(Long.valueOf(dialogId));
        int flags2 = 2;
        ChatActivity[] chatActivity3 = new ChatActivity[1];
        ActionBarPopupWindow.ActionBarPopupWindowLayout[] previewMenu = {new ActionBarPopupWindow.ActionBarPopupWindowLayout(getParentActivity(), NUM, getResourceProvider(), 2)};
        ActionBarMenuSubItem markAsUnreadItem2 = new ActionBarMenuSubItem(getParentActivity(), true, false);
        if (cell.getHasUnread()) {
            markAsUnreadItem2.setTextAndIcon(LocaleController.getString("MarkAsRead", NUM), NUM);
        } else {
            markAsUnreadItem2.setTextAndIcon(LocaleController.getString("MarkAsUnread", NUM), NUM);
        }
        markAsUnreadItem2.setMinimumWidth(160);
        markAsUnreadItem2.setOnClickListener(new DialogsActivity$$ExternalSyntheticLambda18(this, cell, dialogId));
        previewMenu[0].addView(markAsUnreadItem2);
        boolean[] hasPinAction = {true};
        TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(dialogId);
        boolean z2 = (this.viewPages[0].dialogsType == 7 || this.viewPages[0].dialogsType == 8) && (!this.actionBar.isActionModeShowed() || this.actionBar.isActionModeShowed((String) null));
        boolean containsFilter = z2;
        if (z2) {
            dialogFilter = getMessagesController().selectedDialogFilter[this.viewPages[0].dialogsType == 8 ? (char) 1 : 0];
        } else {
            dialogFilter = null;
        }
        MessagesController.DialogFilter filter = dialogFilter;
        if (!isDialogPinned(dialog)) {
            int newPinnedCount = 0;
            int newPinnedSecretCount = 0;
            int pinnedCount = 0;
            ArrayList<TLRPC.Dialog> dialogs = getMessagesController().getDialogs(this.folderId);
            int N = dialogs.size();
            markAsUnreadItem = markAsUnreadItem2;
            int a = 0;
            int pinnedSecretCount = 0;
            while (true) {
                if (a >= N) {
                    int i = N;
                    flags = flags2;
                    chatActivity = chatActivity3;
                    break;
                }
                ArrayList<TLRPC.Dialog> dialogs2 = dialogs;
                TLRPC.Dialog dialog1 = dialogs.get(a);
                int N2 = N;
                if ((dialog1 instanceof TLRPC.TL_dialogFolder) != 0) {
                    flags = flags2;
                    chatActivity = chatActivity3;
                } else if (isDialogPinned(dialog1)) {
                    flags = flags2;
                    chatActivity = chatActivity3;
                    if (DialogObject.isEncryptedDialog(dialog1.id)) {
                        pinnedSecretCount++;
                    } else {
                        pinnedCount++;
                    }
                } else {
                    flags = flags2;
                    chatActivity = chatActivity3;
                    TLRPC.Dialog dialog2 = dialog1;
                    if (!getMessagesController().isPromoDialog(dialog1.id, false)) {
                        break;
                    }
                }
                a++;
                N = N2;
                dialogs = dialogs2;
                flags2 = flags;
                chatActivity3 = chatActivity;
            }
            int alreadyAdded = 0;
            if (dialog != null && !isDialogPinned(dialog)) {
                if (DialogObject.isEncryptedDialog(dialogId)) {
                    newPinnedSecretCount = 0 + 1;
                } else {
                    newPinnedCount = 0 + 1;
                }
                if (filter != null && filter.alwaysShow.contains(Long.valueOf(dialogId))) {
                    alreadyAdded = 0 + 1;
                }
            }
            if (containsFilter && filter != null) {
                maxPinnedCount = 100 - filter.alwaysShow.size();
            } else if (this.folderId == 0 && filter == null) {
                maxPinnedCount = getMessagesController().maxPinnedDialogsCount;
            } else {
                maxPinnedCount = getMessagesController().maxFolderPinnedDialogsCount;
            }
            r5 = 0;
            hasPinAction[0] = newPinnedSecretCount + pinnedSecretCount <= maxPinnedCount && (newPinnedCount + pinnedCount) - alreadyAdded <= maxPinnedCount;
        } else {
            markAsUnreadItem = markAsUnreadItem2;
            flags = 2;
            chatActivity = chatActivity3;
            r5 = 0;
        }
        if (hasPinAction[r5]) {
            ActionBarMenuSubItem unpinItem = new ActionBarMenuSubItem(getParentActivity(), r5, r5);
            if (isDialogPinned(dialog)) {
                unpinItem.setTextAndIcon(LocaleController.getString("UnpinMessage", NUM), NUM);
            } else {
                unpinItem.setTextAndIcon(LocaleController.getString("PinMessage", NUM), NUM);
            }
            unpinItem.setMinimumWidth(160);
            boolean[] zArr = hasPinAction;
            ActionBarMenuSubItem actionBarMenuSubItem = markAsUnreadItem;
            boolean z3 = containsFilter;
            int i2 = flags;
            chatActivity2 = chatActivity;
            DialogsActivity$$ExternalSyntheticLambda17 dialogsActivity$$ExternalSyntheticLambda17 = r1;
            DialogsActivity$$ExternalSyntheticLambda17 dialogsActivity$$ExternalSyntheticLambda172 = new DialogsActivity$$ExternalSyntheticLambda17(this, filter, dialog, dialogId);
            unpinItem.setOnClickListener(dialogsActivity$$ExternalSyntheticLambda17);
            previewMenu[0].addView(unpinItem);
        } else {
            boolean[] zArr2 = hasPinAction;
            boolean z4 = containsFilter;
            ActionBarMenuSubItem actionBarMenuSubItem2 = markAsUnreadItem;
            int i3 = flags;
            chatActivity2 = chatActivity;
        }
        if (!DialogObject.isUserDialog(dialogId) || !UserObject.isUserSelf(getMessagesController().getUser(Long.valueOf(dialogId)))) {
            ActionBarMenuSubItem muteItem2 = new ActionBarMenuSubItem(getParentActivity(), false, false);
            if (!getMessagesController().isDialogMuted(dialogId)) {
                muteItem2.setTextAndIcon(LocaleController.getString("Mute", NUM), NUM);
            } else {
                muteItem2.setTextAndIcon(LocaleController.getString("Unmute", NUM), NUM);
            }
            muteItem2.setMinimumWidth(160);
            muteItem2.setOnClickListener(new DialogsActivity$$ExternalSyntheticLambda15(this, dialogId));
            z = false;
            previewMenu[0].addView(muteItem2);
        } else {
            z = false;
        }
        ActionBarMenuSubItem deleteItem2 = new ActionBarMenuSubItem(getParentActivity(), z, true);
        deleteItem2.setIconColor(getThemedColor("dialogRedIcon"));
        deleteItem2.setTextColor(getThemedColor("dialogTextRed"));
        deleteItem2.setTextAndIcon(LocaleController.getString("Delete", NUM), NUM);
        deleteItem2.setMinimumWidth(160);
        deleteItem2.setOnClickListener(new DialogsActivity$$ExternalSyntheticLambda16(this, dialogIdArray));
        previewMenu[0].addView(deleteItem2);
        if (!getMessagesController().checkCanOpenChat(args, this)) {
            return false;
        }
        if (this.searchString != null) {
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
        }
        prepareBlurBitmap();
        this.parentLayout.highlightActionButtons = true;
        if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
            ChatActivity chatActivity4 = new ChatActivity(args);
            chatActivity2[0] = chatActivity4;
            presentFragmentAsPreview(chatActivity4);
            return true;
        }
        ChatActivity chatActivity5 = new ChatActivity(args);
        chatActivity2[0] = chatActivity5;
        presentFragmentAsPreviewWithMenu(chatActivity5, previewMenu[0]);
        if (chatActivity2[0] == null) {
            return true;
        }
        chatActivity2[0].allowExpandPreviewByClick = true;
        try {
            chatActivity2[0].getAvatarContainer().getAvatarImageView().performAccessibilityAction(64, (Bundle) null);
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    /* renamed from: lambda$showChatPreview$25$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3412lambda$showChatPreview$25$orgtelegramuiDialogsActivity(DialogCell cell, long dialogId, View e) {
        if (cell.getHasUnread()) {
            markAsRead(dialogId);
        } else {
            markAsUnread(dialogId);
        }
        finishPreviewFragment();
    }

    /* renamed from: lambda$showChatPreview$27$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3414lambda$showChatPreview$27$orgtelegramuiDialogsActivity(MessagesController.DialogFilter filter, TLRPC.Dialog dialog, long dialogId, View e) {
        finishPreviewFragment();
        AndroidUtilities.runOnUIThread(new DialogsActivity$$ExternalSyntheticLambda36(this, filter, dialog, dialogId), 100);
    }

    /* renamed from: lambda$showChatPreview$26$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3413lambda$showChatPreview$26$orgtelegramuiDialogsActivity(MessagesController.DialogFilter filter, TLRPC.Dialog dialog, long dialogId) {
        int minPinnedNum;
        TLRPC.EncryptedChat encryptedChat;
        MessagesController.DialogFilter dialogFilter = filter;
        TLRPC.Dialog dialog2 = dialog;
        int minPinnedNum2 = Integer.MAX_VALUE;
        if (dialogFilter == null || !isDialogPinned(dialog2)) {
            minPinnedNum = Integer.MAX_VALUE;
        } else {
            int N = dialogFilter.pinnedDialogs.size();
            for (int c = 0; c < N; c++) {
                minPinnedNum2 = Math.min(minPinnedNum2, dialogFilter.pinnedDialogs.valueAt(c));
            }
            minPinnedNum = minPinnedNum2 - this.canPinCount;
        }
        if (DialogObject.isEncryptedDialog(dialogId)) {
            encryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(dialogId)));
        } else {
            encryptedChat = null;
        }
        if (!isDialogPinned(dialog2)) {
            pinDialog(dialogId, true, filter, minPinnedNum, true);
            getUndoView().showWithAction(0, 78, (Object) 1, (Object) 1600, (Runnable) null, (Runnable) null);
            if (dialogFilter != null) {
                if (encryptedChat != null) {
                    if (!dialogFilter.alwaysShow.contains(Long.valueOf(encryptedChat.user_id))) {
                        dialogFilter.alwaysShow.add(Long.valueOf(encryptedChat.user_id));
                    }
                } else if (!dialogFilter.alwaysShow.contains(Long.valueOf(dialogId))) {
                    dialogFilter.alwaysShow.add(Long.valueOf(dialogId));
                }
            }
        } else {
            pinDialog(dialogId, false, filter, minPinnedNum, true);
            getUndoView().showWithAction(0, 79, (Object) 1, (Object) 1600, (Runnable) null, (Runnable) null);
        }
        if (dialogFilter != null) {
            TLRPC.EncryptedChat encryptedChat2 = encryptedChat;
            FilterCreateActivity.saveFilterToServer(filter, dialogFilter.flags, dialogFilter.name, dialogFilter.alwaysShow, dialogFilter.neverShow, dialogFilter.pinnedDialogs, false, false, true, true, false, this, (Runnable) null);
        }
        getMessagesController().reorderPinnedDialogs(this.folderId, (ArrayList<TLRPC.InputDialogPeer>) null, 0);
        updateCounters(true);
        if (this.viewPages != null) {
            int a = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (a >= viewPageArr.length) {
                    break;
                }
                viewPageArr[a].dialogsAdapter.onReorderStateChanged(false);
                a++;
            }
        }
        updateVisibleRows(MessagesController.UPDATE_MASK_REORDER | MessagesController.UPDATE_MASK_CHECK);
    }

    /* renamed from: lambda$showChatPreview$28$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3415lambda$showChatPreview$28$orgtelegramuiDialogsActivity(long dialogId, View e) {
        boolean isMuted = getMessagesController().isDialogMuted(dialogId);
        if (!isMuted) {
            getNotificationsController().setDialogNotificationsSettings(dialogId, 3);
        } else {
            getNotificationsController().setDialogNotificationsSettings(dialogId, 4);
        }
        BulletinFactory.createMuteBulletin(this, !isMuted, (Theme.ResourcesProvider) null).show();
        finishPreviewFragment();
    }

    /* renamed from: lambda$showChatPreview$29$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3416lambda$showChatPreview$29$orgtelegramuiDialogsActivity(ArrayList dialogIdArray, View e) {
        performSelectedDialogsAction(dialogIdArray, 102, false);
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
        AndroidUtilities.runOnUIThread(new DialogsActivity$$ExternalSyntheticLambda26(this));
    }

    /* renamed from: lambda$onDialogAnimationFinished$30$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3395xCLASSNAMEd274f() {
        ArrayList<TLRPC.Dialog> arrayList;
        if (!(this.viewPages == null || this.folderId == 0 || ((arrayList = this.frozenDialogsList) != null && !arrayList.isEmpty()))) {
            int a = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (a >= viewPageArr.length) {
                    break;
                }
                viewPageArr[a].listView.setEmptyView((View) null);
                this.viewPages[a].progressView.setVisibility(4);
                a++;
            }
            finishFragment();
        }
        setDialogsListFrozen(false);
        updateDialogIndices();
    }

    /* access modifiers changed from: private */
    public void setScrollY(float value) {
        View view = this.scrimView;
        if (view != null) {
            view.getLocationInWindow(this.scrimViewLocation);
        }
        this.actionBar.setTranslationY(value);
        FilterTabsView filterTabsView2 = this.filterTabsView;
        if (filterTabsView2 != null) {
            filterTabsView2.setTranslationY(value);
        }
        updateContextViewPosition();
        if (this.viewPages != null) {
            int a = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (a >= viewPageArr.length) {
                    break;
                }
                viewPageArr[a].listView.setTopGlowOffset(this.viewPages[a].listView.getPaddingTop() + ((int) value));
                a++;
            }
        }
        this.fragmentView.invalidate();
    }

    private void prepareBlurBitmap() {
        if (this.blurredView != null) {
            int w = (int) (((float) this.fragmentView.getMeasuredWidth()) / 6.0f);
            int h = (int) (((float) this.fragmentView.getMeasuredHeight()) / 6.0f);
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.scale(0.16666667f, 0.16666667f);
            this.fragmentView.draw(canvas);
            Utilities.stackBlurBitmap(bitmap, Math.max(7, Math.max(w, h) / 180));
            this.blurredView.setBackground(new BitmapDrawable(bitmap));
            this.blurredView.setAlpha(0.0f);
            this.blurredView.setVisibility(0);
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationProgress(boolean isOpen, float progress) {
        View view = this.blurredView;
        if (view != null && view.getVisibility() == 0) {
            if (isOpen) {
                this.blurredView.setAlpha(1.0f - progress);
            } else {
                this.blurredView.setAlpha(progress);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        View view;
        if (isOpen && (view = this.blurredView) != null && view.getVisibility() == 0) {
            this.blurredView.setVisibility(8);
            this.blurredView.setBackground((Drawable) null);
        }
        if (isOpen && this.afterSignup) {
            try {
                this.fragmentView.performHapticFeedback(3, 2);
            } catch (Exception e) {
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
    public void hideActionMode(boolean animateCheck) {
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
        ofFloat.addUpdateListener(new DialogsActivity$$ExternalSyntheticLambda22(this));
        this.actionBarColorAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.actionBarColorAnimator.setDuration(200);
        this.actionBarColorAnimator.start();
        this.allowMoving = false;
        if (!this.movingDialogFilters.isEmpty()) {
            int N = this.movingDialogFilters.size();
            int a = 0;
            while (a < N) {
                MessagesController.DialogFilter filter = this.movingDialogFilters.get(a);
                MessagesController.DialogFilter dialogFilter = filter;
                FilterCreateActivity.saveFilterToServer(filter, filter.flags, filter.name, filter.alwaysShow, filter.neverShow, filter.pinnedDialogs, false, false, true, true, false, this, (Runnable) null);
                a++;
                N = N;
            }
            int i2 = a;
            int i3 = N;
            this.movingDialogFilters.clear();
        }
        if (this.movingWas) {
            getMessagesController().reorderPinnedDialogs(this.folderId, (ArrayList<TLRPC.InputDialogPeer>) null, 0);
            this.movingWas = false;
        }
        updateCounters(true);
        if (this.viewPages != null) {
            int a2 = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (a2 >= viewPageArr.length) {
                    break;
                }
                viewPageArr[a2].dialogsAdapter.onReorderStateChanged(false);
                a2++;
            }
        }
        int i4 = MessagesController.UPDATE_MASK_REORDER | MessagesController.UPDATE_MASK_CHECK;
        if (animateCheck) {
            i = MessagesController.UPDATE_MASK_CHAT;
        }
        updateVisibleRows(i4 | i);
    }

    /* renamed from: lambda$hideActionMode$31$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3392lambda$hideActionMode$31$orgtelegramuiDialogsActivity(ValueAnimator valueAnimator) {
        this.progressToActionMode = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        for (int i = 0; i < this.actionBar.getChildCount(); i++) {
            if (!(this.actionBar.getChildAt(i).getVisibility() != 0 || this.actionBar.getChildAt(i) == this.actionBar.getActionMode() || this.actionBar.getChildAt(i) == this.actionBar.getBackButton())) {
                this.actionBar.getChildAt(i).setAlpha(1.0f - this.progressToActionMode);
            }
        }
        if (this.fragmentView != null) {
            this.fragmentView.invalidate();
        }
    }

    private int getPinnedCount() {
        ArrayList<TLRPC.Dialog> dialogs;
        int pinnedCount = 0;
        if ((this.viewPages[0].dialogsType == 7 || this.viewPages[0].dialogsType == 8) && (!this.actionBar.isActionModeShowed() || this.actionBar.isActionModeShowed((String) null))) {
            dialogs = getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, this.dialogsListFrozen);
        } else {
            dialogs = getMessagesController().getDialogs(this.folderId);
        }
        int N = dialogs.size();
        for (int a = 0; a < N; a++) {
            TLRPC.Dialog dialog = dialogs.get(a);
            if (!(dialog instanceof TLRPC.TL_dialogFolder)) {
                if (isDialogPinned(dialog)) {
                    pinnedCount++;
                } else if (!getMessagesController().isPromoDialog(dialog.id, false)) {
                    break;
                }
            }
        }
        return pinnedCount;
    }

    /* access modifiers changed from: private */
    public boolean isDialogPinned(TLRPC.Dialog dialog) {
        MessagesController.DialogFilter filter;
        if ((this.viewPages[0].dialogsType == 7 || this.viewPages[0].dialogsType == 8) && (!this.actionBar.isActionModeShowed() || this.actionBar.isActionModeShowed((String) null))) {
            filter = getMessagesController().selectedDialogFilter[this.viewPages[0].dialogsType == 8 ? (char) 1 : 0];
        } else {
            filter = null;
        }
        if (filter == null) {
            return dialog.pinned;
        }
        if (filter.pinnedDialogs.indexOfKey(dialog.id) >= 0) {
            return true;
        }
        return false;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v9, resolved type: org.telegram.tgnet.TLRPC$TL_userEmpty} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v29, resolved type: org.telegram.tgnet.TLRPC$TL_userEmpty} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v33, resolved type: org.telegram.tgnet.TLRPC$TL_userEmpty} */
    /* JADX WARNING: type inference failed for: r13v1, types: [boolean] */
    /* JADX WARNING: type inference failed for: r13v2 */
    /* JADX WARNING: type inference failed for: r13v33 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:126:0x02b9, code lost:
        if (r13 == 108) goto L_0x02be;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x02b3  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x02dc  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x02ea  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0646  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x065d  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x0662  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x066c  */
    /* JADX WARNING: Removed duplicated region for block: B:270:0x0670  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x06ac  */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x06bf  */
    /* JADX WARNING: Removed duplicated region for block: B:280:0x06d5  */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x06ef  */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x06f1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void performSelectedDialogsAction(java.util.ArrayList<java.lang.Long> r36, int r37, boolean r38) {
        /*
            r35 = this;
            r14 = r35
            r15 = r36
            r13 = r37
            android.app.Activity r0 = r35.getParentActivity()
            if (r0 != 0) goto L_0x000d
            return
        L_0x000d:
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r14.viewPages
            r5 = 0
            r0 = r0[r5]
            int r0 = r0.dialogsType
            r1 = 7
            r2 = 8
            r3 = 0
            if (r0 == r1) goto L_0x0026
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r14.viewPages
            r0 = r0[r5]
            int r0 = r0.dialogsType
            if (r0 != r2) goto L_0x0037
        L_0x0026:
            org.telegram.ui.ActionBar.ActionBar r0 = r14.actionBar
            boolean r0 = r0.isActionModeShowed()
            if (r0 == 0) goto L_0x0039
            org.telegram.ui.ActionBar.ActionBar r0 = r14.actionBar
            boolean r0 = r0.isActionModeShowed(r3)
            if (r0 == 0) goto L_0x0037
            goto L_0x0039
        L_0x0037:
            r0 = 0
            goto L_0x003a
        L_0x0039:
            r0 = 1
        L_0x003a:
            r16 = r0
            if (r16 == 0) goto L_0x0055
            org.telegram.messenger.MessagesController r0 = r35.getMessagesController()
            org.telegram.messenger.MessagesController$DialogFilter[] r0 = r0.selectedDialogFilter
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r14.viewPages
            r1 = r1[r5]
            int r1 = r1.dialogsType
            if (r1 != r2) goto L_0x0050
            r1 = 1
            goto L_0x0051
        L_0x0050:
            r1 = 0
        L_0x0051:
            r0 = r0[r1]
            r12 = r0
            goto L_0x0057
        L_0x0055:
            r0 = 0
            r12 = r0
        L_0x0057:
            int r11 = r36.size()
            r0 = 0
            r1 = 105(0x69, float:1.47E-43)
            if (r13 == r1) goto L_0x06f6
            r1 = 107(0x6b, float:1.5E-43)
            if (r13 != r1) goto L_0x0070
            r3 = r0
            r25 = r11
            r15 = r12
            r0 = r13
            r1 = 2
            r4 = 4
            r13 = 0
            r17 = 3
            goto L_0x0700
        L_0x0070:
            java.lang.String r8 = "Cancel"
            r6 = 108(0x6c, float:1.51E-43)
            r2 = 100
            if (r13 == r2) goto L_0x007e
            if (r13 != r6) goto L_0x007b
            goto L_0x007e
        L_0x007b:
            r3 = r0
            goto L_0x0198
        L_0x007e:
            int r10 = r14.canPinCount
            if (r10 == 0) goto L_0x0197
            r10 = 0
            r20 = 0
            r21 = 0
            r22 = 0
            org.telegram.messenger.MessagesController r6 = r35.getMessagesController()
            int r3 = r14.folderId
            java.util.ArrayList r3 = r6.getDialogs(r3)
            r6 = 0
            int r1 = r3.size()
        L_0x0098:
            if (r6 >= r1) goto L_0x00e0
            java.lang.Object r26 = r3.get(r6)
            r7 = r26
            org.telegram.tgnet.TLRPC$Dialog r7 = (org.telegram.tgnet.TLRPC.Dialog) r7
            boolean r9 = r7 instanceof org.telegram.tgnet.TLRPC.TL_dialogFolder
            if (r9 == 0) goto L_0x00ab
            r28 = r1
            r9 = r3
            r3 = r0
            goto L_0x00d7
        L_0x00ab:
            boolean r9 = r14.isDialogPinned(r7)
            if (r9 == 0) goto L_0x00c6
            r9 = r3
            long r2 = r7.id
            boolean r2 = org.telegram.messenger.DialogObject.isEncryptedDialog(r2)
            if (r2 == 0) goto L_0x00c0
            int r20 = r20 + 1
            r3 = r0
            r28 = r1
            goto L_0x00d7
        L_0x00c0:
            int r10 = r10 + 1
            r3 = r0
            r28 = r1
            goto L_0x00d7
        L_0x00c6:
            r9 = r3
            org.telegram.messenger.MessagesController r2 = r35.getMessagesController()
            r3 = r0
            r28 = r1
            long r0 = r7.id
            boolean r0 = r2.isPromoDialog(r0, r5)
            if (r0 != 0) goto L_0x00d7
            goto L_0x00e4
        L_0x00d7:
            int r6 = r6 + 1
            r0 = r3
            r3 = r9
            r1 = r28
            r2 = 100
            goto L_0x0098
        L_0x00e0:
            r28 = r1
            r9 = r3
            r3 = r0
        L_0x00e4:
            r0 = 0
            r1 = 0
        L_0x00e6:
            if (r1 >= r11) goto L_0x0126
            java.lang.Object r2 = r15.get(r1)
            java.lang.Long r2 = (java.lang.Long) r2
            long r6 = r2.longValue()
            org.telegram.messenger.MessagesController r2 = r35.getMessagesController()
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r2 = r2.dialogs_dict
            java.lang.Object r2 = r2.get(r6)
            org.telegram.tgnet.TLRPC$Dialog r2 = (org.telegram.tgnet.TLRPC.Dialog) r2
            if (r2 == 0) goto L_0x0122
            boolean r28 = r14.isDialogPinned(r2)
            if (r28 == 0) goto L_0x0107
            goto L_0x0122
        L_0x0107:
            boolean r28 = org.telegram.messenger.DialogObject.isEncryptedDialog(r6)
            if (r28 == 0) goto L_0x0110
            int r22 = r22 + 1
            goto L_0x0112
        L_0x0110:
            int r21 = r21 + 1
        L_0x0112:
            if (r12 == 0) goto L_0x0122
            java.util.ArrayList<java.lang.Long> r4 = r12.alwaysShow
            java.lang.Long r5 = java.lang.Long.valueOf(r6)
            boolean r4 = r4.contains(r5)
            if (r4 == 0) goto L_0x0122
            int r0 = r0 + 1
        L_0x0122:
            int r1 = r1 + 1
            r5 = 0
            goto L_0x00e6
        L_0x0126:
            if (r16 == 0) goto L_0x0133
            java.util.ArrayList<java.lang.Long> r1 = r12.alwaysShow
            int r1 = r1.size()
            r2 = 100
            int r1 = 100 - r1
            goto L_0x0158
        L_0x0133:
            int r1 = r14.folderId
            if (r1 != 0) goto L_0x0152
            if (r12 == 0) goto L_0x013a
            goto L_0x0152
        L_0x013a:
            org.telegram.messenger.UserConfig r1 = r35.getUserConfig()
            boolean r1 = r1.isPremium()
            if (r1 == 0) goto L_0x014b
            org.telegram.messenger.MessagesController r1 = r35.getMessagesController()
            int r1 = r1.dialogFiltersPinnedLimitPremium
            goto L_0x0151
        L_0x014b:
            org.telegram.messenger.MessagesController r1 = r35.getMessagesController()
            int r1 = r1.dialogFiltersPinnedLimitDefault
        L_0x0151:
            goto L_0x0158
        L_0x0152:
            org.telegram.messenger.MessagesController r1 = r35.getMessagesController()
            int r1 = r1.maxFolderPinnedDialogsCount
        L_0x0158:
            int r2 = r22 + r20
            if (r2 > r1) goto L_0x0164
            int r2 = r21 + r10
            int r2 = r2 - r0
            if (r2 <= r1) goto L_0x0162
            goto L_0x0164
        L_0x0162:
            goto L_0x02ae
        L_0x0164:
            int r2 = r14.folderId
            if (r2 != 0) goto L_0x017c
            if (r12 == 0) goto L_0x016c
            r6 = 0
            goto L_0x017d
        L_0x016c:
            org.telegram.ui.Components.Premium.LimitReachedBottomSheet r2 = new org.telegram.ui.Components.Premium.LimitReachedBottomSheet
            android.app.Activity r4 = r35.getParentActivity()
            int r5 = r14.currentAccount
            r6 = 0
            r2.<init>(r14, r4, r6, r5)
            r14.showDialog(r2)
            goto L_0x0196
        L_0x017c:
            r6 = 0
        L_0x017d:
            r2 = 2131627528(0x7f0e0e08, float:1.8882323E38)
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.Object[] r5 = new java.lang.Object[r6]
            java.lang.String r7 = "Chats"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r7, r1, r5)
            r4[r6] = r5
            java.lang.String r5 = "PinFolderLimitReached"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r5, r2, r4)
            org.telegram.ui.Components.AlertsCreator.showSimpleAlert(r14, r2)
        L_0x0196:
            return
        L_0x0197:
            r3 = r0
        L_0x0198:
            r0 = 102(0x66, float:1.43E-43)
            if (r13 == r0) goto L_0x01a0
            r0 = 103(0x67, float:1.44E-43)
            if (r13 != r0) goto L_0x027c
        L_0x01a0:
            r0 = 1
            if (r11 <= r0) goto L_0x027c
            if (r38 == 0) goto L_0x027c
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r1 = r35.getParentActivity()
            r0.<init>((android.content.Context) r1)
            r1 = 102(0x66, float:1.43E-43)
            if (r13 != r1) goto L_0x01d9
            r1 = 2131625405(0x7f0e05bd, float:1.8878017E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            java.lang.Object[] r5 = new java.lang.Object[r4]
            java.lang.String r6 = "ChatsSelected"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r6, r11, r5)
            r2[r4] = r5
            java.lang.String r4 = "DeleteFewChatsTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r4, r1, r2)
            r0.setTitle(r1)
            r1 = 2131624439(0x7f0e01f7, float:1.8876058E38)
            java.lang.String r2 = "AreYouSureDeleteFewChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setMessage(r1)
            goto L_0x022a
        L_0x01d9:
            int r1 = r14.canClearCacheCount
            if (r1 == 0) goto L_0x0204
            r1 = 2131625134(0x7f0e04ae, float:1.8877467E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            java.lang.Object[] r5 = new java.lang.Object[r4]
            java.lang.String r6 = "ChatsSelectedClearCache"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r6, r11, r5)
            r2[r4] = r5
            java.lang.String r4 = "ClearCacheFewChatsTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r4, r1, r2)
            r0.setTitle(r1)
            r1 = 2131624426(0x7f0e01ea, float:1.8876031E38)
            java.lang.String r2 = "AreYouSureClearHistoryCacheFewChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setMessage(r1)
            goto L_0x022a
        L_0x0204:
            r1 = 2131625137(0x7f0e04b1, float:1.8877473E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            java.lang.Object[] r5 = new java.lang.Object[r4]
            java.lang.String r6 = "ChatsSelectedClear"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r6, r11, r5)
            r2[r4] = r5
            java.lang.String r4 = "ClearFewChatsTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r4, r1, r2)
            r0.setTitle(r1)
            r1 = 2131624428(0x7f0e01ec, float:1.8876035E38)
            java.lang.String r2 = "AreYouSureClearHistoryFewChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setMessage(r1)
        L_0x022a:
            r1 = 102(0x66, float:1.43E-43)
            if (r13 != r1) goto L_0x0238
            r1 = 2131625368(0x7f0e0598, float:1.8877942E38)
            java.lang.String r2 = "Delete"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x024f
        L_0x0238:
            int r1 = r14.canClearCacheCount
            if (r1 == 0) goto L_0x0246
            r1 = 2131625141(0x7f0e04b5, float:1.8877482E38)
            java.lang.String r2 = "ClearHistoryCache"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x024f
        L_0x0246:
            r1 = 2131625140(0x7f0e04b4, float:1.887748E38)
            java.lang.String r2 = "ClearHistory"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
        L_0x024f:
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda4 r2 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda4
            r2.<init>(r14, r15, r13)
            r0.setPositiveButton(r1, r2)
            r1 = 2131624819(0x7f0e0373, float:1.8876828E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r8, r1)
            r2 = 0
            r0.setNegativeButton(r1, r2)
            org.telegram.ui.ActionBar.AlertDialog r1 = r0.create()
            r14.showDialog(r1)
            r2 = -1
            android.view.View r2 = r1.getButton(r2)
            android.widget.TextView r2 = (android.widget.TextView) r2
            if (r2 == 0) goto L_0x027b
            java.lang.String r4 = "dialogTextRed2"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r2.setTextColor(r4)
        L_0x027b:
            return
        L_0x027c:
            r0 = 106(0x6a, float:1.49E-43)
            if (r13 != r0) goto L_0x02ae
            if (r38 == 0) goto L_0x02ae
            r0 = 1
            if (r11 != r0) goto L_0x029d
            r0 = 0
            java.lang.Object r1 = r15.get(r0)
            java.lang.Long r1 = (java.lang.Long) r1
            long r0 = r1.longValue()
            org.telegram.messenger.MessagesController r2 = r35.getMessagesController()
            java.lang.Long r4 = java.lang.Long.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r2.getUser(r4)
            goto L_0x029e
        L_0x029d:
            r0 = 0
        L_0x029e:
            int r1 = r14.canReportSpamCount
            if (r1 == 0) goto L_0x02a4
            r5 = 1
            goto L_0x02a5
        L_0x02a4:
            r5 = 0
        L_0x02a5:
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda49 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda49
            r1.<init>(r14, r15)
            org.telegram.ui.Components.AlertsCreator.createBlockDialogAlert(r14, r11, r5, r0, r1)
            return
        L_0x02ae:
            r0 = 2147483647(0x7fffffff, float:NaN)
            if (r12 == 0) goto L_0x02dc
            r1 = 100
            if (r13 == r1) goto L_0x02bc
            r1 = 108(0x6c, float:1.51E-43)
            if (r13 != r1) goto L_0x02de
            goto L_0x02be
        L_0x02bc:
            r1 = 108(0x6c, float:1.51E-43)
        L_0x02be:
            int r2 = r14.canPinCount
            if (r2 == 0) goto L_0x02de
            r2 = 0
            org.telegram.messenger.support.LongSparseIntArray r4 = r12.pinnedDialogs
            int r4 = r4.size()
        L_0x02c9:
            if (r2 >= r4) goto L_0x02d8
            org.telegram.messenger.support.LongSparseIntArray r5 = r12.pinnedDialogs
            int r5 = r5.valueAt(r2)
            int r0 = java.lang.Math.min(r0, r5)
            int r2 = r2 + 1
            goto L_0x02c9
        L_0x02d8:
            int r2 = r14.canPinCount
            int r0 = r0 - r2
            goto L_0x02de
        L_0x02dc:
            r1 = 108(0x6c, float:1.51E-43)
        L_0x02de:
            r20 = 0
            r2 = 0
            r22 = r0
            r10 = r2
            r21 = r3
        L_0x02e6:
            r0 = 104(0x68, float:1.46E-43)
            if (r10 >= r11) goto L_0x063d
            java.lang.Object r2 = r15.get(r10)
            java.lang.Long r2 = (java.lang.Long) r2
            long r4 = r2.longValue()
            org.telegram.messenger.MessagesController r2 = r35.getMessagesController()
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r2 = r2.dialogs_dict
            java.lang.Object r2 = r2.get(r4)
            r9 = r2
            org.telegram.tgnet.TLRPC$Dialog r9 = (org.telegram.tgnet.TLRPC.Dialog) r9
            if (r9 != 0) goto L_0x0314
            r29 = r8
            r5 = r10
            r0 = r11
            r6 = r13
            r1 = 2
            r2 = 0
            r17 = 3
            r18 = 2131624819(0x7f0e0373, float:1.8876828E38)
            r27 = 103(0x67, float:1.44E-43)
            r13 = r12
            goto L_0x0614
        L_0x0314:
            r2 = 0
            r3 = 0
            boolean r6 = org.telegram.messenger.DialogObject.isEncryptedDialog(r4)
            if (r6 == 0) goto L_0x034f
            org.telegram.messenger.MessagesController r6 = r35.getMessagesController()
            int r7 = org.telegram.messenger.DialogObject.getEncryptedChatId(r4)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = r6.getEncryptedChat(r7)
            r6 = 0
            if (r3 == 0) goto L_0x0343
            org.telegram.messenger.MessagesController r7 = r35.getMessagesController()
            r30 = r2
            long r1 = r3.user_id
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r2 = r7.getUser(r1)
            r7 = r2
            r30 = r6
            goto L_0x0379
        L_0x0343:
            r30 = r2
            org.telegram.tgnet.TLRPC$TL_userEmpty r1 = new org.telegram.tgnet.TLRPC$TL_userEmpty
            r1.<init>()
            r2 = r1
            r7 = r2
            r30 = r6
            goto L_0x0379
        L_0x034f:
            r30 = r2
            boolean r1 = org.telegram.messenger.DialogObject.isUserDialog(r4)
            if (r1 == 0) goto L_0x0368
            org.telegram.messenger.MessagesController r1 = r35.getMessagesController()
            java.lang.Long r2 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r2 = r1.getUser(r2)
            r6 = 0
            r7 = r2
            r30 = r6
            goto L_0x0379
        L_0x0368:
            org.telegram.messenger.MessagesController r1 = r35.getMessagesController()
            long r6 = -r4
            java.lang.Long r2 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r6 = r1.getChat(r2)
            r7 = r30
            r30 = r6
        L_0x0379:
            if (r30 != 0) goto L_0x038e
            if (r7 != 0) goto L_0x038e
            r29 = r8
            r5 = r10
            r0 = r11
            r6 = r13
            r1 = 2
            r2 = 0
            r17 = 3
            r18 = 2131624819(0x7f0e0373, float:1.8876828E38)
            r27 = 103(0x67, float:1.44E-43)
            r13 = r12
            goto L_0x0614
        L_0x038e:
            if (r7 == 0) goto L_0x039c
            boolean r1 = r7.bot
            if (r1 == 0) goto L_0x039c
            boolean r1 = org.telegram.messenger.MessagesController.isSupportUser(r7)
            if (r1 != 0) goto L_0x039c
            r6 = 1
            goto L_0x039d
        L_0x039c:
            r6 = 0
        L_0x039d:
            r2 = 108(0x6c, float:1.51E-43)
            r1 = 100
            if (r13 == r1) goto L_0x0594
            if (r13 != r2) goto L_0x03be
            r15 = r3
            r31 = r4
            r33 = r6
            r3 = r7
            r29 = r8
            r4 = r9
            r5 = r10
            r0 = r11
            r19 = r12
            r6 = r13
            r1 = 2
            r2 = 0
            r17 = 3
            r18 = 2131624819(0x7f0e0373, float:1.8876828E38)
            r27 = 103(0x67, float:1.44E-43)
            goto L_0x05ab
        L_0x03be:
            r1 = 101(0x65, float:1.42E-43)
            if (r13 != r1) goto L_0x03ee
            int r0 = r14.canReadCount
            if (r0 == 0) goto L_0x03da
            r14.markAsRead(r4)
            r29 = r8
            r5 = r10
            r0 = r11
            r6 = r13
            r1 = 2
            r2 = 0
            r17 = 3
            r18 = 2131624819(0x7f0e0373, float:1.8876828E38)
            r27 = 103(0x67, float:1.44E-43)
            r13 = r12
            goto L_0x0614
        L_0x03da:
            r14.markAsUnread(r4)
            r29 = r8
            r5 = r10
            r0 = r11
            r6 = r13
            r1 = 2
            r2 = 0
            r17 = 3
            r18 = 2131624819(0x7f0e0373, float:1.8876828E38)
            r27 = 103(0x67, float:1.44E-43)
            r13 = r12
            goto L_0x0614
        L_0x03ee:
            r1 = 102(0x66, float:1.43E-43)
            if (r13 == r1) goto L_0x048f
            r1 = 103(0x67, float:1.44E-43)
            if (r13 != r1) goto L_0x03f9
            r1 = 3
            goto L_0x0490
        L_0x03f9:
            if (r13 != r0) goto L_0x047d
            r0 = 1
            if (r11 != r0) goto L_0x0410
            int r1 = r14.canMuteCount
            if (r1 != r0) goto L_0x0410
            r0 = 0
            org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.Components.AlertsCreator.createMuteAlert(r14, r4, r0)
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda6 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda6
            r1.<init>(r14)
            r14.showDialog(r0, r1)
            return
        L_0x0410:
            int r0 = r14.canUnmuteCount
            if (r0 == 0) goto L_0x0448
            org.telegram.messenger.MessagesController r0 = r35.getMessagesController()
            boolean r0 = r0.isDialogMuted(r4)
            if (r0 != 0) goto L_0x042f
            r29 = r8
            r5 = r10
            r0 = r11
            r6 = r13
            r1 = 2
            r2 = 0
            r17 = 3
            r18 = 2131624819(0x7f0e0373, float:1.8876828E38)
            r27 = 103(0x67, float:1.44E-43)
            r13 = r12
            goto L_0x0614
        L_0x042f:
            org.telegram.messenger.NotificationsController r0 = r35.getNotificationsController()
            r1 = 4
            r0.setDialogNotificationsSettings(r4, r1)
            r29 = r8
            r5 = r10
            r0 = r11
            r6 = r13
            r1 = 2
            r2 = 0
            r17 = 3
            r18 = 2131624819(0x7f0e0373, float:1.8876828E38)
            r27 = 103(0x67, float:1.44E-43)
            r13 = r12
            goto L_0x0614
        L_0x0448:
            r1 = 4
            org.telegram.messenger.MessagesController r0 = r35.getMessagesController()
            boolean r0 = r0.isDialogMuted(r4)
            if (r0 == 0) goto L_0x0464
            r29 = r8
            r5 = r10
            r0 = r11
            r6 = r13
            r1 = 2
            r2 = 0
            r17 = 3
            r18 = 2131624819(0x7f0e0373, float:1.8876828E38)
            r27 = 103(0x67, float:1.44E-43)
            r13 = r12
            goto L_0x0614
        L_0x0464:
            org.telegram.messenger.NotificationsController r0 = r35.getNotificationsController()
            r1 = 3
            r0.setDialogNotificationsSettings(r4, r1)
            r29 = r8
            r5 = r10
            r0 = r11
            r6 = r13
            r1 = 2
            r2 = 0
            r17 = 3
            r18 = 2131624819(0x7f0e0373, float:1.8876828E38)
            r27 = 103(0x67, float:1.44E-43)
            r13 = r12
            goto L_0x0614
        L_0x047d:
            r1 = 3
            r29 = r8
            r5 = r10
            r0 = r11
            r6 = r13
            r1 = 2
            r2 = 0
            r17 = 3
            r18 = 2131624819(0x7f0e0373, float:1.8876828E38)
            r27 = 103(0x67, float:1.44E-43)
            r13 = r12
            goto L_0x0614
        L_0x048f:
            r1 = 3
        L_0x0490:
            r0 = 1
            if (r11 != r0) goto L_0x0524
            r0 = 102(0x66, float:1.43E-43)
            if (r13 != r0) goto L_0x04e9
            boolean r0 = r14.canDeletePsaSelected
            if (r0 == 0) goto L_0x04e9
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r1 = r35.getParentActivity()
            r0.<init>((android.content.Context) r1)
            r1 = 2131627755(0x7f0e0eeb, float:1.8882783E38)
            java.lang.String r2 = "PsaHideChatAlertTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            r1 = 2131627754(0x7f0e0eea, float:1.8882781E38)
            java.lang.String r2 = "PsaHideChatAlertText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setMessage(r1)
            r1 = 2131627753(0x7f0e0ee9, float:1.888278E38)
            java.lang.String r2 = "PsaHide"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda58 r2 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda58
            r2.<init>(r14)
            r0.setPositiveButton(r1, r2)
            r1 = 2131624819(0x7f0e0373, float:1.8876828E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r8, r1)
            r2 = 0
            r0.setNegativeButton(r1, r2)
            org.telegram.ui.ActionBar.AlertDialog r1 = r0.create()
            r14.showDialog(r1)
            r15 = r3
            r25 = r4
            r3 = r7
            r4 = r9
            r5 = r10
            r0 = r11
            r1 = r12
            r2 = r13
            goto L_0x0523
        L_0x04e9:
            r0 = 103(0x67, float:1.44E-43)
            if (r13 != r0) goto L_0x04ef
            r8 = 1
            goto L_0x04f0
        L_0x04ef:
            r8 = 0
        L_0x04f0:
            long r0 = r9.id
            boolean r17 = org.telegram.messenger.DialogObject.isEncryptedDialog(r0)
            r0 = 102(0x66, float:1.43E-43)
            if (r13 != r0) goto L_0x04fd
            r28 = 1
            goto L_0x04ff
        L_0x04fd:
            r28 = 0
        L_0x04ff:
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda42 r18 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda42
            r0 = r18
            r1 = r35
            r2 = r37
            r15 = r3
            r3 = r30
            r25 = r4
            r0.<init>(r1, r2, r3, r4, r6)
            r3 = r7
            r7 = r35
            r4 = r9
            r9 = r30
            r5 = r10
            r10 = r3
            r0 = r11
            r11 = r17
            r1 = r12
            r12 = r28
            r2 = r13
            r13 = r18
            org.telegram.ui.Components.AlertsCreator.createClearOrDeleteDialogAlert(r7, r8, r9, r10, r11, r12, r13)
        L_0x0523:
            return
        L_0x0524:
            r15 = r3
            r25 = r4
            r3 = r7
            r4 = r9
            r5 = r10
            r0 = 102(0x66, float:1.43E-43)
            r18 = 2131624819(0x7f0e0373, float:1.8876828E38)
            r34 = r12
            r12 = r11
            r11 = r34
            org.telegram.messenger.MessagesController r7 = r35.getMessagesController()
            r9 = r25
            r0 = 1
            boolean r7 = r7.isPromoDialog(r9, r0)
            if (r7 == 0) goto L_0x0555
            org.telegram.messenger.MessagesController r0 = r35.getMessagesController()
            r0.hidePromoDialog()
            r29 = r8
            r0 = r12
            r6 = r13
            r1 = 2
            r2 = 0
            r17 = 3
            r27 = 103(0x67, float:1.44E-43)
            r13 = r11
            goto L_0x0614
        L_0x0555:
            r0 = 103(0x67, float:1.44E-43)
            if (r13 != r0) goto L_0x0571
            int r7 = r14.canClearCacheCount
            if (r7 == 0) goto L_0x0571
            org.telegram.messenger.MessagesController r7 = r35.getMessagesController()
            r1 = 2
            r2 = 0
            r7.deleteDialog(r9, r1, r2)
            r29 = r8
            r0 = r12
            r6 = r13
            r17 = 3
            r27 = 103(0x67, float:1.44E-43)
            r13 = r11
            goto L_0x0614
        L_0x0571:
            r1 = 2
            r2 = 0
            r25 = 0
            r27 = 103(0x67, float:1.44E-43)
            r7 = r35
            r29 = r8
            r8 = r37
            r31 = r9
            r0 = 4
            r17 = 3
            r19 = r11
            r11 = r30
            r0 = r12
            r12 = r6
            r33 = r6
            r6 = r13
            r13 = r25
            r7.m3408x64cf3var_(r8, r9, r11, r12, r13)
            r13 = r19
            goto L_0x0614
        L_0x0594:
            r15 = r3
            r31 = r4
            r33 = r6
            r3 = r7
            r29 = r8
            r4 = r9
            r5 = r10
            r0 = r11
            r19 = r12
            r6 = r13
            r1 = 2
            r2 = 0
            r17 = 3
            r18 = 2131624819(0x7f0e0373, float:1.8876828E38)
            r27 = 103(0x67, float:1.44E-43)
        L_0x05ab:
            int r7 = r14.canPinCount
            if (r7 == 0) goto L_0x060b
            boolean r7 = r14.isDialogPinned(r4)
            if (r7 == 0) goto L_0x05b8
            r13 = r19
            goto L_0x0614
        L_0x05b8:
            int r21 = r21 + 1
            r10 = 1
            r7 = 1
            if (r0 != r7) goto L_0x05c0
            r13 = 1
            goto L_0x05c1
        L_0x05c0:
            r13 = 0
        L_0x05c1:
            r7 = r35
            r8 = r31
            r11 = r19
            r12 = r22
            r7.pinDialog(r8, r10, r11, r12, r13)
            r13 = r19
            if (r13 == 0) goto L_0x0609
            int r22 = r22 + 1
            if (r15 == 0) goto L_0x05ee
            java.util.ArrayList<java.lang.Long> r7 = r13.alwaysShow
            long r8 = r15.user_id
            java.lang.Long r8 = java.lang.Long.valueOf(r8)
            boolean r7 = r7.contains(r8)
            if (r7 != 0) goto L_0x0607
            java.util.ArrayList<java.lang.Long> r7 = r13.alwaysShow
            long r8 = r15.user_id
            java.lang.Long r8 = java.lang.Long.valueOf(r8)
            r7.add(r8)
            goto L_0x0607
        L_0x05ee:
            java.util.ArrayList<java.lang.Long> r7 = r13.alwaysShow
            long r8 = r4.id
            java.lang.Long r8 = java.lang.Long.valueOf(r8)
            boolean r7 = r7.contains(r8)
            if (r7 != 0) goto L_0x0607
            java.util.ArrayList<java.lang.Long> r7 = r13.alwaysShow
            long r8 = r4.id
            java.lang.Long r8 = java.lang.Long.valueOf(r8)
            r7.add(r8)
        L_0x0607:
            r15 = r13
            goto L_0x0630
        L_0x0609:
            r15 = r13
            goto L_0x0630
        L_0x060b:
            r13 = r19
            boolean r7 = r14.isDialogPinned(r4)
            if (r7 != 0) goto L_0x0616
        L_0x0614:
            r15 = r13
            goto L_0x0630
        L_0x0616:
            int r21 = r21 + 1
            r10 = 0
            r7 = 1
            if (r0 != r7) goto L_0x061f
            r19 = 1
            goto L_0x0621
        L_0x061f:
            r19 = 0
        L_0x0621:
            r7 = r35
            r8 = r31
            r11 = r13
            r12 = r22
            r25 = r15
            r15 = r13
            r13 = r19
            r7.pinDialog(r8, r10, r11, r12, r13)
        L_0x0630:
            int r10 = r5 + 1
            r11 = r0
            r13 = r6
            r12 = r15
            r8 = r29
            r1 = 108(0x6c, float:1.51E-43)
            r15 = r36
            goto L_0x02e6
        L_0x063d:
            r5 = r10
            r0 = r11
            r15 = r12
            r6 = r13
            r2 = 0
            r3 = 104(0x68, float:1.46E-43)
            if (r6 != r3) goto L_0x065d
            r4 = 1
            if (r0 != r4) goto L_0x064d
            int r1 = r14.canMuteCount
            if (r1 == r4) goto L_0x065e
        L_0x064d:
            int r1 = r14.canUnmuteCount
            if (r1 != 0) goto L_0x0653
            r1 = 1
            goto L_0x0654
        L_0x0653:
            r1 = 0
        L_0x0654:
            r3 = 0
            org.telegram.ui.Components.Bulletin r1 = org.telegram.ui.Components.BulletinFactory.createMuteBulletin(r14, r1, r3)
            r1.show()
            goto L_0x065e
        L_0x065d:
            r4 = 1
        L_0x065e:
            r3 = 100
            if (r6 == r3) goto L_0x066c
            r5 = 108(0x6c, float:1.51E-43)
            if (r6 != r5) goto L_0x0667
            goto L_0x066e
        L_0x0667:
            r25 = r0
            r13 = 0
            goto L_0x06d3
        L_0x066c:
            r5 = 108(0x6c, float:1.51E-43)
        L_0x066e:
            if (r15 == 0) goto L_0x06ac
            int r1 = r15.flags
            java.lang.String r7 = r15.name
            java.util.ArrayList<java.lang.Long> r8 = r15.alwaysShow
            java.util.ArrayList<java.lang.Long> r9 = r15.neverShow
            org.telegram.messenger.support.LongSparseIntArray r10 = r15.pinnedDialogs
            r11 = 0
            r17 = 0
            r18 = 1
            r19 = 1
            r23 = 0
            r24 = 0
            r25 = r0
            r0 = r15
            r26 = 0
            r2 = r7
            r7 = 100
            r3 = r8
            r8 = 1
            r4 = r9
            r9 = 0
            r13 = 108(0x6c, float:1.51E-43)
            r5 = r10
            r12 = r6
            r6 = r11
            r11 = 100
            r7 = r17
            r10 = 1
            r8 = r18
            r13 = 0
            r9 = r19
            r10 = r23
            r11 = r35
            r12 = r24
            org.telegram.ui.FilterCreateActivity.saveFilterToServer(r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            r2 = 0
            goto L_0x06bb
        L_0x06ac:
            r25 = r0
            r13 = 0
            org.telegram.messenger.MessagesController r0 = r35.getMessagesController()
            int r1 = r14.folderId
            r2 = 0
            r4 = 0
            r0.reorderPinnedDialogs(r1, r4, r2)
        L_0x06bb:
            boolean r0 = r14.searchIsShowed
            if (r0 == 0) goto L_0x06d3
            org.telegram.ui.Components.UndoView r0 = r35.getUndoView()
            int r1 = r14.canPinCount
            if (r1 == 0) goto L_0x06ca
            r1 = 78
            goto L_0x06cc
        L_0x06ca:
            r1 = 79
        L_0x06cc:
            java.lang.Integer r4 = java.lang.Integer.valueOf(r21)
            r0.showWithAction((long) r2, (int) r1, (java.lang.Object) r4)
        L_0x06d3:
            if (r20 == 0) goto L_0x06e1
            int r0 = r14.initialDialogsType
            r1 = 10
            if (r0 == r1) goto L_0x06de
            r14.hideFloatingButton(r13)
        L_0x06de:
            r35.scrollToTop()
        L_0x06e1:
            r0 = r37
            r1 = 108(0x6c, float:1.51E-43)
            if (r0 == r1) goto L_0x06f1
            r1 = 100
            if (r0 == r1) goto L_0x06f1
            r1 = 102(0x66, float:1.43E-43)
            if (r0 == r1) goto L_0x06f1
            r5 = 1
            goto L_0x06f2
        L_0x06f1:
            r5 = 0
        L_0x06f2:
            r14.hideActionMode(r5)
            return
        L_0x06f6:
            r3 = r0
            r25 = r11
            r15 = r12
            r0 = r13
            r1 = 2
            r4 = 4
            r13 = 0
            r17 = 3
        L_0x0700:
            java.util.ArrayList r2 = new java.util.ArrayList
            r5 = r36
            r2.<init>(r5)
            org.telegram.messenger.MessagesController r6 = r35.getMessagesController()
            int r7 = r14.canUnarchiveCount
            if (r7 != 0) goto L_0x0711
            r8 = 1
            goto L_0x0712
        L_0x0711:
            r8 = 0
        L_0x0712:
            r9 = -1
            r10 = 0
            r11 = 0
            r7 = r2
            r6.addDialogToFolder(r7, r8, r9, r10, r11)
            int r6 = r14.canUnarchiveCount
            if (r6 != 0) goto L_0x076b
            android.content.SharedPreferences r6 = org.telegram.messenger.MessagesController.getGlobalMainSettings()
            java.lang.String r7 = "archivehint_l"
            boolean r8 = r6.getBoolean(r7, r13)
            if (r8 != 0) goto L_0x0731
            boolean r8 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r8 == 0) goto L_0x072f
            goto L_0x0731
        L_0x072f:
            r8 = 0
            goto L_0x0732
        L_0x0731:
            r8 = 1
        L_0x0732:
            if (r8 != 0) goto L_0x0741
            android.content.SharedPreferences$Editor r9 = r6.edit()
            r10 = 1
            android.content.SharedPreferences$Editor r7 = r9.putBoolean(r7, r10)
            r7.commit()
            goto L_0x0742
        L_0x0741:
            r10 = 1
        L_0x0742:
            if (r8 == 0) goto L_0x074c
            int r7 = r2.size()
            if (r7 <= r10) goto L_0x074b
            r1 = 4
        L_0x074b:
            goto L_0x0756
        L_0x074c:
            int r1 = r2.size()
            if (r1 <= r10) goto L_0x0754
            r9 = 5
            goto L_0x0755
        L_0x0754:
            r9 = 3
        L_0x0755:
            r1 = r9
        L_0x0756:
            org.telegram.ui.Components.UndoView r17 = r35.getUndoView()
            r18 = 0
            r21 = 0
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda35 r4 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda35
            r4.<init>(r14, r2)
            r20 = r1
            r22 = r4
            r17.showWithAction(r18, r20, r21, r22)
            goto L_0x0799
        L_0x076b:
            org.telegram.messenger.MessagesController r1 = r35.getMessagesController()
            int r6 = r14.folderId
            java.util.ArrayList r1 = r1.getDialogs(r6)
            org.telegram.ui.DialogsActivity$ViewPage[] r6 = r14.viewPages
            if (r6 == 0) goto L_0x0799
            boolean r6 = r1.isEmpty()
            if (r6 == 0) goto L_0x0799
            org.telegram.ui.DialogsActivity$ViewPage[] r6 = r14.viewPages
            r6 = r6[r13]
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r6 = r6.listView
            r7 = 0
            r6.setEmptyView(r7)
            org.telegram.ui.DialogsActivity$ViewPage[] r6 = r14.viewPages
            r6 = r6[r13]
            org.telegram.ui.Components.FlickerLoadingView r6 = r6.progressView
            r6.setVisibility(r4)
            r35.finishFragment()
        L_0x0799:
            r14.hideActionMode(r13)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.performSelectedDialogsAction(java.util.ArrayList, int, boolean):void");
    }

    /* renamed from: lambda$performSelectedDialogsAction$32$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3403xevar_(ArrayList copy) {
        getMessagesController().addDialogToFolder(copy, this.folderId == 0 ? 0 : 1, -1, (ArrayList<TLRPC.TL_inputFolderPeer>) null, 0);
    }

    /* renamed from: lambda$performSelectedDialogsAction$34$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3405xeb480b43(ArrayList selectedDialogs2, int action, DialogInterface dialog1, int which) {
        if (!selectedDialogs2.isEmpty()) {
            ArrayList<Long> didsCopy = new ArrayList<>(selectedDialogs2);
            getUndoView().showWithAction(didsCopy, action == 102 ? 27 : 26, (Object) null, (Object) null, (Runnable) new DialogsActivity$$ExternalSyntheticLambda32(this, action, didsCopy), (Runnable) null);
            hideActionMode(action == 103);
        }
    }

    /* renamed from: lambda$performSelectedDialogsAction$33$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3404x6d704var_(int action, ArrayList didsCopy) {
        if (action == 102) {
            getMessagesController().setDialogsInTransaction(true);
            performSelectedDialogsAction(didsCopy, action, false);
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
        performSelectedDialogsAction(didsCopy, action, false);
    }

    /* renamed from: lambda$performSelectedDialogsAction$35$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3406x691fCLASSNAME(ArrayList selectedDialogs2, boolean report, boolean delete2) {
        int N = selectedDialogs2.size();
        for (int a = 0; a < N; a++) {
            ArrayList arrayList = selectedDialogs2;
            long did = ((Long) selectedDialogs2.get(a)).longValue();
            if (report) {
                getMessagesController().reportSpam(did, getMessagesController().getUser(Long.valueOf(did)), (TLRPC.Chat) null, (TLRPC.EncryptedChat) null, false);
            }
            if (delete2) {
                getMessagesController().deleteDialog(did, 0, true);
            }
            getMessagesController().blockPeer(did);
        }
        ArrayList arrayList2 = selectedDialogs2;
        hideActionMode(false);
    }

    /* renamed from: lambda$performSelectedDialogsAction$36$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3407xe6var_(DialogInterface dialog1, int which) {
        getMessagesController().hidePromoDialog();
        hideActionMode(false);
    }

    /* renamed from: lambda$performSelectedDialogsAction$38$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3409xe2a6fb47(int action, TLRPC.Chat chat, long selectedDialog, boolean isBot, boolean param) {
        int selectedDialogIndex;
        ArrayList<TLRPC.Dialog> arrayList;
        int i = action;
        TLRPC.Chat chat2 = chat;
        long j = selectedDialog;
        hideActionMode(false);
        if (i != 103 || !ChatObject.isChannel(chat)) {
            boolean z = param;
        } else if (!chat2.megagroup || !TextUtils.isEmpty(chat2.username)) {
            getMessagesController().deleteDialog(j, 2, param);
            return;
        } else {
            boolean z2 = param;
        }
        if (i == 102 && this.folderId != 0 && getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, false).size() == 1) {
            this.viewPages[0].progressView.setVisibility(4);
        }
        this.debugLastUpdateAction = 3;
        if (i == 102) {
            setDialogsListFrozen(true);
            if (this.frozenDialogsList != null) {
                int i2 = 0;
                while (true) {
                    if (i2 >= this.frozenDialogsList.size()) {
                        break;
                    } else if (this.frozenDialogsList.get(i2).id == j) {
                        selectedDialogIndex = i2;
                        break;
                    } else {
                        i2++;
                    }
                }
            }
        }
        selectedDialogIndex = -1;
        UndoView undoView2 = getUndoView();
        int i3 = i == 103 ? 0 : 1;
        DialogsActivity$$ExternalSyntheticLambda31 dialogsActivity$$ExternalSyntheticLambda31 = r0;
        int selectedDialogIndex2 = selectedDialogIndex;
        DialogsActivity$$ExternalSyntheticLambda31 dialogsActivity$$ExternalSyntheticLambda312 = new DialogsActivity$$ExternalSyntheticLambda31(this, action, selectedDialog, chat, isBot, param);
        undoView2.showWithAction(j, i3, (Runnable) dialogsActivity$$ExternalSyntheticLambda31);
        ArrayList<TLRPC.Dialog> currentDialogs = new ArrayList<>(getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, false));
        int currentDialogIndex = -1;
        int i4 = 0;
        while (true) {
            if (i4 >= currentDialogs.size()) {
                break;
            } else if (currentDialogs.get(i4).id == j) {
                currentDialogIndex = i4;
                break;
            } else {
                i4++;
            }
        }
        if (i != 102) {
        } else if (selectedDialogIndex2 < 0 || currentDialogIndex >= 0 || (arrayList = this.frozenDialogsList) == null) {
            setDialogsListFrozen(false);
        } else {
            int selectedDialogIndex3 = selectedDialogIndex2;
            arrayList.remove(selectedDialogIndex3);
            this.viewPages[0].dialogsItemAnimator.prepareForRemove();
            this.viewPages[0].dialogsAdapter.notifyItemRemoved(selectedDialogIndex3);
            this.dialogRemoveFinished = 2;
        }
    }

    /* renamed from: lambda$performSelectedDialogsAction$39$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3410x607eb748(DialogInterface dialog12) {
        hideActionMode(true);
    }

    private void markAsRead(long did) {
        MessagesController.DialogFilter filter;
        long j = did;
        TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(j);
        if ((this.viewPages[0].dialogsType == 7 || this.viewPages[0].dialogsType == 8) && (!this.actionBar.isActionModeShowed() || this.actionBar.isActionModeShowed((String) null))) {
            filter = getMessagesController().selectedDialogFilter[this.viewPages[0].dialogsType == 8 ? (char) 1 : 0];
        } else {
            filter = null;
        }
        this.debugLastUpdateAction = 2;
        int selectedDialogIndex = -1;
        if (!(filter == null || (filter.flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ) == 0 || filter.alwaysShow(this.currentAccount, dialog))) {
            setDialogsListFrozen(true);
            if (this.frozenDialogsList != null) {
                int i = 0;
                while (true) {
                    if (i >= this.frozenDialogsList.size()) {
                        break;
                    } else if (this.frozenDialogsList.get(i).id == j) {
                        selectedDialogIndex = i;
                        break;
                    } else {
                        i++;
                    }
                }
                if (selectedDialogIndex < 0) {
                    setDialogsListFrozen(false, false);
                }
            }
        }
        getMessagesController().markMentionsAsRead(j);
        int selectedDialogIndex2 = selectedDialogIndex;
        MessagesController.DialogFilter dialogFilter = filter;
        getMessagesController().markDialogAsRead(did, dialog.top_message, dialog.top_message, dialog.last_message_date, false, 0, 0, true, 0);
        if (selectedDialogIndex2 >= 0) {
            this.frozenDialogsList.remove(selectedDialogIndex2);
            this.viewPages[0].dialogsItemAnimator.prepareForRemove();
            this.viewPages[0].dialogsAdapter.notifyItemRemoved(selectedDialogIndex2);
            this.dialogRemoveFinished = 2;
        }
    }

    private void markAsUnread(long did) {
        getMessagesController().markDialogAsUnread(did, (TLRPC.InputPeer) null, 0);
    }

    /* access modifiers changed from: private */
    /* renamed from: performDeleteOrClearDialogAction */
    public void m3408x64cf3var_(int action, long selectedDialog, TLRPC.Chat chat, boolean isBot, boolean revoke) {
        long j = selectedDialog;
        boolean z = revoke;
        if (action == 103) {
            getMessagesController().deleteDialog(j, 1, z);
            return;
        }
        if (chat == null) {
            getMessagesController().deleteDialog(j, 0, z);
            if (isBot) {
                getMessagesController().blockPeer((long) ((int) j));
            }
        } else if (ChatObject.isNotInChat(chat)) {
            getMessagesController().deleteDialog(j, 0, z);
        } else {
            getMessagesController().deleteParticipantFromChat((long) ((int) (-j)), getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId())), (TLRPC.Chat) null, (TLRPC.ChatFull) null, revoke, false);
        }
        if (AndroidUtilities.isTablet()) {
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, Long.valueOf(selectedDialog));
        }
        getMessagesController().checkIfFolderEmpty(this.folderId);
    }

    private void pinDialog(long selectedDialog, boolean pin3, MessagesController.DialogFilter filter, int minPinnedNum, boolean animated) {
        boolean needScroll;
        int selectedDialogIndex;
        int selectedDialogIndex2;
        boolean updated;
        long j = selectedDialog;
        MessagesController.DialogFilter dialogFilter = filter;
        int currentDialogIndex = -1;
        int scrollToPosition = (this.viewPages[0].dialogsType != 0 || !hasHiddenArchive()) ? 0 : 1;
        int currentPosition = this.viewPages[0].layoutManager.findFirstVisibleItemPosition();
        if (dialogFilter != null) {
            int index = dialogFilter.pinnedDialogs.get(j, Integer.MIN_VALUE);
            if (!pin3 && index == Integer.MIN_VALUE) {
                return;
            }
        }
        this.debugLastUpdateAction = pin3 ? 4 : 5;
        if (currentPosition > scrollToPosition || !animated) {
            selectedDialogIndex = -1;
            needScroll = true;
        } else {
            setDialogsListFrozen(true);
            if (this.frozenDialogsList != null) {
                int i = 0;
                while (true) {
                    if (i >= this.frozenDialogsList.size()) {
                        break;
                    } else if (this.frozenDialogsList.get(i).id == j) {
                        selectedDialogIndex = i;
                        needScroll = false;
                        break;
                    } else {
                        i++;
                    }
                }
            }
            selectedDialogIndex = -1;
            needScroll = false;
        }
        if (dialogFilter != null) {
            if (pin3) {
                dialogFilter.pinnedDialogs.put(j, minPinnedNum);
            } else {
                int i2 = minPinnedNum;
                dialogFilter.pinnedDialogs.delete(j);
            }
            if (animated) {
                getMessagesController().onFilterUpdate(dialogFilter);
            }
            updated = true;
            selectedDialogIndex2 = selectedDialogIndex;
        } else {
            int i3 = minPinnedNum;
            selectedDialogIndex2 = selectedDialogIndex;
            updated = getMessagesController().pinDialog(selectedDialog, pin3, (TLRPC.InputPeer) null, -1);
        }
        if (updated) {
            if (needScroll) {
                if (this.initialDialogsType != 10) {
                    hideFloatingButton(false);
                }
                scrollToTop();
            } else {
                ArrayList<TLRPC.Dialog> currentDialogs = getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, false);
                int i4 = 0;
                while (true) {
                    if (i4 >= currentDialogs.size()) {
                        break;
                    } else if (currentDialogs.get(i4).id == j) {
                        currentDialogIndex = i4;
                        break;
                    } else {
                        i4++;
                    }
                }
            }
        }
        if (!needScroll) {
            boolean animate = false;
            if (selectedDialogIndex2 >= 0) {
                ArrayList<TLRPC.Dialog> arrayList = this.frozenDialogsList;
                if (arrayList != null && currentDialogIndex >= 0 && selectedDialogIndex2 != currentDialogIndex) {
                    arrayList.add(currentDialogIndex, arrayList.remove(selectedDialogIndex2));
                    this.viewPages[0].dialogsItemAnimator.prepareForRemove();
                    this.viewPages[0].dialogsAdapter.notifyItemRemoved(selectedDialogIndex2);
                    this.viewPages[0].dialogsAdapter.notifyItemInserted(currentDialogIndex);
                    this.dialogRemoveFinished = 2;
                    this.dialogInsertFinished = 2;
                    this.viewPages[0].layoutManager.scrollToPositionWithOffset((this.viewPages[0].dialogsType != 0 || !hasHiddenArchive()) ? 0 : 1, (int) this.actionBar.getTranslationY());
                    animate = true;
                } else if (currentDialogIndex >= 0 && selectedDialogIndex2 == currentDialogIndex) {
                    animate = true;
                    AndroidUtilities.runOnUIThread(new DialogsActivity$$ExternalSyntheticLambda28(this), 200);
                }
            }
            if (!animate) {
                setDialogsListFrozen(false);
            }
        }
    }

    /* renamed from: lambda$pinDialog$40$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3411lambda$pinDialog$40$orgtelegramuiDialogsActivity() {
        setDialogsListFrozen(false);
    }

    /* access modifiers changed from: private */
    public void scrollToTop() {
        int scrollDistance = this.viewPages[0].layoutManager.findFirstVisibleItemPosition() * AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f);
        int position = (this.viewPages[0].dialogsType != 0 || !hasHiddenArchive()) ? 0 : 1;
        RecyclerView.ItemAnimator itemAnimator = this.viewPages[0].listView.getItemAnimator();
        if (((float) scrollDistance) >= ((float) this.viewPages[0].listView.getMeasuredHeight()) * 1.2f) {
            this.viewPages[0].scrollHelper.setScrollDirection(1);
            this.viewPages[0].scrollHelper.scrollToPosition(position, 0, false, true);
            resetScroll();
            return;
        }
        this.viewPages[0].listView.smoothScrollToPosition(position);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00fe  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0162  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateCounters(boolean r23) {
        /*
            r22 = this;
            r0 = r22
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r0.canDeletePsaSelected = r5
            r0.canUnarchiveCount = r5
            r0.canUnmuteCount = r5
            r0.canMuteCount = r5
            r0.canPinCount = r5
            r0.canReadCount = r5
            r0.canClearCacheCount = r5
            r6 = 0
            r0.canReportSpamCount = r5
            if (r23 == 0) goto L_0x001b
            return
        L_0x001b:
            java.util.ArrayList<java.lang.Long> r7 = r0.selectedDialogs
            int r7 = r7.size()
            org.telegram.messenger.UserConfig r8 = r22.getUserConfig()
            long r8 = r8.getClientUserId()
            android.content.SharedPreferences r10 = r22.getNotificationsSettings()
            r11 = 0
        L_0x002e:
            if (r11 >= r7) goto L_0x01f5
            org.telegram.messenger.MessagesController r12 = r22.getMessagesController()
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r12 = r12.dialogs_dict
            java.util.ArrayList<java.lang.Long> r13 = r0.selectedDialogs
            java.lang.Object r13 = r13.get(r11)
            java.lang.Long r13 = (java.lang.Long) r13
            long r13 = r13.longValue()
            java.lang.Object r12 = r12.get(r13)
            org.telegram.tgnet.TLRPC$Dialog r12 = (org.telegram.tgnet.TLRPC.Dialog) r12
            if (r12 != 0) goto L_0x0052
            r16 = r7
            r20 = r8
            r17 = r11
            goto L_0x01ec
        L_0x0052:
            long r13 = r12.id
            boolean r15 = r0.isDialogPinned(r12)
            int r5 = r12.unread_count
            r16 = r7
            if (r5 != 0) goto L_0x0065
            boolean r5 = r12.unread_mark
            if (r5 == 0) goto L_0x0063
            goto L_0x0065
        L_0x0063:
            r5 = 0
            goto L_0x0066
        L_0x0065:
            r5 = 1
        L_0x0066:
            org.telegram.messenger.MessagesController r7 = r22.getMessagesController()
            boolean r7 = r7.isDialogMuted(r13)
            if (r7 == 0) goto L_0x0079
            int r7 = r0.canUnmuteCount
            r17 = r11
            r11 = 1
            int r7 = r7 + r11
            r0.canUnmuteCount = r7
            goto L_0x0081
        L_0x0079:
            r17 = r11
            r11 = 1
            int r7 = r0.canMuteCount
            int r7 = r7 + r11
            r0.canMuteCount = r7
        L_0x0081:
            if (r5 == 0) goto L_0x0088
            int r7 = r0.canReadCount
            int r7 = r7 + r11
            r0.canReadCount = r7
        L_0x0088:
            int r7 = r0.folderId
            if (r7 == r11) goto L_0x00aa
            int r7 = r12.folder_id
            if (r7 != r11) goto L_0x0091
            goto L_0x00aa
        L_0x0091:
            int r7 = (r13 > r8 ? 1 : (r13 == r8 ? 0 : -1))
            if (r7 == 0) goto L_0x00b0
            r18 = 777000(0xbdb28, double:3.83889E-318)
            int r7 = (r13 > r18 ? 1 : (r13 == r18 ? 0 : -1))
            if (r7 == 0) goto L_0x00b0
            org.telegram.messenger.MessagesController r7 = r22.getMessagesController()
            r11 = 0
            boolean r7 = r7.isPromoDialog(r13, r11)
            if (r7 != 0) goto L_0x00b0
            int r4 = r4 + 1
            goto L_0x00b0
        L_0x00aa:
            int r7 = r0.canUnarchiveCount
            r11 = 1
            int r7 = r7 + r11
            r0.canUnarchiveCount = r7
        L_0x00b0:
            boolean r7 = org.telegram.messenger.DialogObject.isUserDialog(r13)
            if (r7 == 0) goto L_0x00f4
            int r7 = (r13 > r8 ? 1 : (r13 == r8 ? 0 : -1))
            if (r7 != 0) goto L_0x00bd
            r18 = r4
            goto L_0x00f6
        L_0x00bd:
            org.telegram.messenger.MessagesController r7 = r22.getMessagesController()
            java.lang.Long r11 = java.lang.Long.valueOf(r13)
            org.telegram.tgnet.TLRPC$User r7 = r7.getUser(r11)
            boolean r11 = org.telegram.messenger.MessagesController.isSupportUser(r7)
            if (r11 == 0) goto L_0x00d4
            int r6 = r6 + 1
            r18 = r4
            goto L_0x00f8
        L_0x00d4:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r18 = r4
            java.lang.String r4 = "dialog_bar_report"
            r11.append(r4)
            r11.append(r13)
            java.lang.String r4 = r11.toString()
            r11 = 1
            boolean r4 = r10.getBoolean(r4, r11)
            if (r4 == 0) goto L_0x00f8
            int r4 = r0.canReportSpamCount
            int r4 = r4 + r11
            r0.canReportSpamCount = r4
            goto L_0x00f8
        L_0x00f4:
            r18 = r4
        L_0x00f6:
            int r6 = r6 + 1
        L_0x00f8:
            boolean r4 = org.telegram.messenger.DialogObject.isChannel(r12)
            if (r4 == 0) goto L_0x0162
            org.telegram.messenger.MessagesController r4 = r22.getMessagesController()
            r7 = r5
            r11 = r6
            long r5 = -r13
            java.lang.Long r5 = java.lang.Long.valueOf(r5)
            org.telegram.tgnet.TLRPC$Chat r4 = r4.getChat(r5)
            org.telegram.messenger.MessagesController r5 = r22.getMessagesController()
            r19 = r7
            long r6 = r12.id
            r20 = r8
            r8 = 1
            boolean r5 = r5.isPromoDialog(r6, r8)
            if (r5 == 0) goto L_0x0132
            int r5 = r0.canClearCacheCount
            int r5 = r5 + r8
            r0.canClearCacheCount = r5
            org.telegram.messenger.MessagesController r5 = r22.getMessagesController()
            int r5 = r5.promoDialogType
            int r6 = org.telegram.messenger.MessagesController.PROMO_TYPE_PSA
            if (r5 != r6) goto L_0x015d
            int r2 = r2 + 1
            r0.canDeletePsaSelected = r8
            goto L_0x015d
        L_0x0132:
            if (r15 == 0) goto L_0x0137
            int r3 = r3 + 1
            goto L_0x013d
        L_0x0137:
            int r5 = r0.canPinCount
            r6 = 1
            int r5 = r5 + r6
            r0.canPinCount = r5
        L_0x013d:
            if (r4 == 0) goto L_0x0155
            boolean r5 = r4.megagroup
            if (r5 == 0) goto L_0x0155
            java.lang.String r5 = r4.username
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 == 0) goto L_0x014e
            int r1 = r1 + 1
            goto L_0x015b
        L_0x014e:
            int r5 = r0.canClearCacheCount
            r6 = 1
            int r5 = r5 + r6
            r0.canClearCacheCount = r5
            goto L_0x015b
        L_0x0155:
            r6 = 1
            int r5 = r0.canClearCacheCount
            int r5 = r5 + r6
            r0.canClearCacheCount = r5
        L_0x015b:
            int r2 = r2 + 1
        L_0x015d:
            r6 = r11
            r4 = r18
            goto L_0x01ec
        L_0x0162:
            r19 = r5
            r11 = r6
            r20 = r8
            long r4 = r12.id
            boolean r4 = org.telegram.messenger.DialogObject.isChatDialog(r4)
            r5 = 0
            if (r4 == 0) goto L_0x0180
            org.telegram.messenger.MessagesController r6 = r22.getMessagesController()
            long r7 = r12.id
            long r7 = -r7
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r7)
            goto L_0x0181
        L_0x0180:
            r6 = r5
        L_0x0181:
            long r7 = r12.id
            boolean r7 = org.telegram.messenger.DialogObject.isEncryptedDialog(r7)
            if (r7 == 0) goto L_0x01b2
            org.telegram.messenger.MessagesController r5 = r22.getMessagesController()
            long r7 = r12.id
            int r7 = org.telegram.messenger.DialogObject.getEncryptedChatId(r7)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$EncryptedChat r5 = r5.getEncryptedChat(r7)
            if (r5 == 0) goto L_0x01ac
            org.telegram.messenger.MessagesController r7 = r22.getMessagesController()
            long r8 = r5.user_id
            java.lang.Long r8 = java.lang.Long.valueOf(r8)
            org.telegram.tgnet.TLRPC$User r7 = r7.getUser(r8)
            goto L_0x01b1
        L_0x01ac:
            org.telegram.tgnet.TLRPC$TL_userEmpty r7 = new org.telegram.tgnet.TLRPC$TL_userEmpty
            r7.<init>()
        L_0x01b1:
            goto L_0x01cb
        L_0x01b2:
            if (r4 != 0) goto L_0x01ca
            long r7 = r12.id
            boolean r7 = org.telegram.messenger.DialogObject.isUserDialog(r7)
            if (r7 == 0) goto L_0x01ca
            org.telegram.messenger.MessagesController r5 = r22.getMessagesController()
            long r7 = r12.id
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r7)
        L_0x01ca:
            r7 = r5
        L_0x01cb:
            if (r7 == 0) goto L_0x01d9
            boolean r5 = r7.bot
            if (r5 == 0) goto L_0x01d9
            boolean r5 = org.telegram.messenger.MessagesController.isSupportUser(r7)
            if (r5 != 0) goto L_0x01d9
            r5 = 1
            goto L_0x01da
        L_0x01d9:
            r5 = 0
        L_0x01da:
            if (r15 == 0) goto L_0x01df
            int r3 = r3 + 1
            goto L_0x01e5
        L_0x01df:
            int r8 = r0.canPinCount
            r9 = 1
            int r8 = r8 + r9
            r0.canPinCount = r8
        L_0x01e5:
            int r1 = r1 + 1
            int r2 = r2 + 1
            r6 = r11
            r4 = r18
        L_0x01ec:
            int r11 = r17 + 1
            r7 = r16
            r8 = r20
            r5 = 0
            goto L_0x002e
        L_0x01f5:
            r16 = r7
            r20 = r8
            r17 = r11
            r5 = 8
            if (r2 == r7) goto L_0x0205
            org.telegram.ui.ActionBar.ActionBarMenuItem r8 = r0.deleteItem
            r8.setVisibility(r5)
            goto L_0x020b
        L_0x0205:
            org.telegram.ui.ActionBar.ActionBarMenuItem r8 = r0.deleteItem
            r9 = 0
            r8.setVisibility(r9)
        L_0x020b:
            int r8 = r0.canClearCacheCount
            if (r8 == 0) goto L_0x0211
            if (r8 != r7) goto L_0x0215
        L_0x0211:
            if (r1 == 0) goto L_0x021b
            if (r1 == r7) goto L_0x021b
        L_0x0215:
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r8 = r0.clearItem
            r8.setVisibility(r5)
            goto L_0x0242
        L_0x021b:
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r8 = r0.clearItem
            r9 = 0
            r8.setVisibility(r9)
            int r8 = r0.canClearCacheCount
            if (r8 == 0) goto L_0x0234
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r8 = r0.clearItem
            r9 = 2131625141(0x7f0e04b5, float:1.8877482E38)
            java.lang.String r11 = "ClearHistoryCache"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r8.setText(r9)
            goto L_0x0242
        L_0x0234:
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r8 = r0.clearItem
            r9 = 2131625140(0x7f0e04b4, float:1.887748E38)
            java.lang.String r11 = "ClearHistory"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r8.setText(r9)
        L_0x0242:
            int r8 = r0.canUnarchiveCount
            if (r8 == 0) goto L_0x0283
            r8 = 2131628721(0x7f0e12b1, float:1.8884743E38)
            java.lang.String r9 = "Unarchive"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r9 = r0.archiveItem
            r11 = 2131165965(0x7var_d, float:1.7946162E38)
            r9.setTextAndIcon(r8, r11)
            org.telegram.ui.ActionBar.ActionBarMenuItem r9 = r0.archive2Item
            r9.setIcon((int) r11)
            org.telegram.ui.ActionBar.ActionBarMenuItem r9 = r0.archive2Item
            r9.setContentDescription(r8)
            org.telegram.ui.Components.FilterTabsView r9 = r0.filterTabsView
            if (r9 == 0) goto L_0x0277
            int r9 = r9.getVisibility()
            if (r9 != 0) goto L_0x0277
            org.telegram.ui.ActionBar.ActionBarMenuItem r9 = r0.archive2Item
            r11 = 0
            r9.setVisibility(r11)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r9 = r0.archiveItem
            r9.setVisibility(r5)
            goto L_0x0282
        L_0x0277:
            r11 = 0
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r9 = r0.archiveItem
            r9.setVisibility(r11)
            org.telegram.ui.ActionBar.ActionBarMenuItem r9 = r0.archive2Item
            r9.setVisibility(r5)
        L_0x0282:
            goto L_0x02cc
        L_0x0283:
            if (r4 == 0) goto L_0x02c2
            r8 = 2131624392(0x7f0e01c8, float:1.8875962E38)
            java.lang.String r9 = "Archive"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r9 = r0.archiveItem
            r11 = 2131165637(0x7var_c5, float:1.7945497E38)
            r9.setTextAndIcon(r8, r11)
            org.telegram.ui.ActionBar.ActionBarMenuItem r9 = r0.archive2Item
            r9.setIcon((int) r11)
            org.telegram.ui.ActionBar.ActionBarMenuItem r9 = r0.archive2Item
            r9.setContentDescription(r8)
            org.telegram.ui.Components.FilterTabsView r9 = r0.filterTabsView
            if (r9 == 0) goto L_0x02b6
            int r9 = r9.getVisibility()
            if (r9 != 0) goto L_0x02b6
            org.telegram.ui.ActionBar.ActionBarMenuItem r9 = r0.archive2Item
            r11 = 0
            r9.setVisibility(r11)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r9 = r0.archiveItem
            r9.setVisibility(r5)
            goto L_0x02c1
        L_0x02b6:
            r11 = 0
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r9 = r0.archiveItem
            r9.setVisibility(r11)
            org.telegram.ui.ActionBar.ActionBarMenuItem r9 = r0.archive2Item
            r9.setVisibility(r5)
        L_0x02c1:
            goto L_0x02cc
        L_0x02c2:
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r8 = r0.archiveItem
            r8.setVisibility(r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r8 = r0.archive2Item
            r8.setVisibility(r5)
        L_0x02cc:
            int r8 = r0.canPinCount
            int r8 = r8 + r3
            if (r8 == r7) goto L_0x02dc
            org.telegram.ui.ActionBar.ActionBarMenuItem r8 = r0.pinItem
            r8.setVisibility(r5)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r8 = r0.pin2Item
            r8.setVisibility(r5)
            goto L_0x02fd
        L_0x02dc:
            org.telegram.ui.Components.FilterTabsView r8 = r0.filterTabsView
            if (r8 == 0) goto L_0x02f2
            int r8 = r8.getVisibility()
            if (r8 != 0) goto L_0x02f2
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r8 = r0.pin2Item
            r9 = 0
            r8.setVisibility(r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r8 = r0.pinItem
            r8.setVisibility(r5)
            goto L_0x02fd
        L_0x02f2:
            r9 = 0
            org.telegram.ui.ActionBar.ActionBarMenuItem r8 = r0.pinItem
            r8.setVisibility(r9)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r8 = r0.pin2Item
            r8.setVisibility(r5)
        L_0x02fd:
            if (r6 == 0) goto L_0x0305
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r8 = r0.blockItem
            r8.setVisibility(r5)
            goto L_0x030b
        L_0x0305:
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r8 = r0.blockItem
            r9 = 0
            r8.setVisibility(r9)
        L_0x030b:
            org.telegram.ui.Components.FilterTabsView r8 = r0.filterTabsView
            if (r8 == 0) goto L_0x0325
            int r8 = r8.getVisibility()
            if (r8 != 0) goto L_0x0325
            org.telegram.ui.Components.FilterTabsView r8 = r0.filterTabsView
            boolean r8 = r8.currentTabIsDefault()
            if (r8 == 0) goto L_0x031e
            goto L_0x0325
        L_0x031e:
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r8 = r0.removeFromFolderItem
            r9 = 0
            r8.setVisibility(r9)
            goto L_0x032a
        L_0x0325:
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r8 = r0.removeFromFolderItem
            r8.setVisibility(r5)
        L_0x032a:
            org.telegram.ui.Components.FilterTabsView r8 = r0.filterTabsView
            if (r8 == 0) goto L_0x034f
            int r8 = r8.getVisibility()
            if (r8 != 0) goto L_0x034f
            org.telegram.ui.Components.FilterTabsView r8 = r0.filterTabsView
            boolean r8 = r8.currentTabIsDefault()
            if (r8 == 0) goto L_0x034f
            java.util.ArrayList<java.lang.Long> r8 = r0.selectedDialogs
            java.util.ArrayList r8 = org.telegram.ui.Components.FiltersListBottomSheet.getCanAddDialogFilters(r0, r8)
            boolean r8 = r8.isEmpty()
            if (r8 != 0) goto L_0x034f
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r5 = r0.addToFolderItem
            r8 = 0
            r5.setVisibility(r8)
            goto L_0x0354
        L_0x034f:
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r8 = r0.addToFolderItem
            r8.setVisibility(r5)
        L_0x0354:
            int r5 = r0.canUnmuteCount
            if (r5 == 0) goto L_0x036f
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.muteItem
            r8 = 2131165967(0x7var_f, float:1.7946166E38)
            r5.setIcon((int) r8)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.muteItem
            r8 = 2131625085(0x7f0e047d, float:1.8877368E38)
            java.lang.String r9 = "ChatsUnmute"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r5.setContentDescription(r8)
            goto L_0x0385
        L_0x036f:
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.muteItem
            r8 = 2131165816(0x7var_, float:1.794586E38)
            r5.setIcon((int) r8)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.muteItem
            r8 = 2131625065(0x7f0e0469, float:1.8877327E38)
            java.lang.String r9 = "ChatsMute"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r5.setContentDescription(r8)
        L_0x0385:
            int r5 = r0.canReadCount
            if (r5 == 0) goto L_0x039b
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r5 = r0.readItem
            r8 = 2131626542(0x7f0e0a2e, float:1.8880323E38)
            java.lang.String r9 = "MarkAsRead"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r9 = 2131165794(0x7var_, float:1.7945815E38)
            r5.setTextAndIcon(r8, r9)
            goto L_0x03ac
        L_0x039b:
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r5 = r0.readItem
            r8 = 2131626543(0x7f0e0a2f, float:1.8880325E38)
            java.lang.String r9 = "MarkAsUnread"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r9 = 2131165795(0x7var_, float:1.7945817E38)
            r5.setTextAndIcon(r8, r9)
        L_0x03ac:
            int r5 = r0.canPinCount
            if (r5 == 0) goto L_0x03d5
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.pinItem
            r8 = 2131165859(0x7var_a3, float:1.7945947E38)
            r5.setIcon((int) r8)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.pinItem
            r8 = 2131627537(0x7f0e0e11, float:1.8882341E38)
            java.lang.String r9 = "PinToTop"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r5.setContentDescription(r8)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r5 = r0.pin2Item
            r8 = 2131625466(0x7f0e05fa, float:1.887814E38)
            java.lang.String r9 = "DialogPin"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r5.setText(r8)
            goto L_0x03f9
        L_0x03d5:
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.pinItem
            r8 = 2131165968(0x7var_, float:1.7946168E38)
            r5.setIcon((int) r8)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.pinItem
            r8 = 2131628742(0x7f0e12c6, float:1.8884785E38)
            java.lang.String r9 = "UnpinFromTop"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r5.setContentDescription(r8)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r5 = r0.pin2Item
            r8 = 2131625467(0x7f0e05fb, float:1.8878143E38)
            java.lang.String r9 = "DialogUnpin"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r5.setText(r8)
        L_0x03f9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.updateCounters(boolean):void");
    }

    /* access modifiers changed from: private */
    public boolean validateSlowModeDialog(long dialogId) {
        TLRPC.Chat chat;
        ChatActivityEnterView chatActivityEnterView;
        if ((this.messagesCount <= 1 && ((chatActivityEnterView = this.commentView) == null || chatActivityEnterView.getVisibility() != 0 || TextUtils.isEmpty(this.commentView.getFieldText()))) || !DialogObject.isChatDialog(dialogId) || (chat = getMessagesController().getChat(Long.valueOf(-dialogId))) == null || ChatObject.hasAdminRights(chat) || !chat.slowmode_enabled) {
            return true;
        }
        AlertsCreator.showSimpleAlert(this, LocaleController.getString("Slowmode", NUM), LocaleController.getString("SlowmodeSendError", NUM));
        return false;
    }

    private void showOrUpdateActionMode(long dialogId, View cell) {
        addOrRemoveSelectedDialog(dialogId, cell);
        boolean updateAnimated = false;
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
                    int a = 0;
                    while (true) {
                        ViewPage[] viewPageArr = this.viewPages;
                        if (a >= viewPageArr.length) {
                            break;
                        }
                        viewPageArr[a].dialogsAdapter.onReorderStateChanged(true);
                        a++;
                    }
                }
                updateVisibleRows(MessagesController.UPDATE_MASK_REORDER);
            }
            if (!this.searchIsShowed) {
                AnimatorSet animatorSet = new AnimatorSet();
                ArrayList<Animator> animators = new ArrayList<>();
                for (int a2 = 0; a2 < this.actionModeViews.size(); a2++) {
                    View view = this.actionModeViews.get(a2);
                    view.setPivotY((float) (ActionBar.getCurrentActionBarHeight() / 2));
                    AndroidUtilities.clearDrawableAnimation(view);
                    animators.add(ObjectAnimator.ofFloat(view, View.SCALE_Y, new float[]{0.1f, 1.0f}));
                }
                animatorSet.playTogether(animators);
                animatorSet.setDuration(200);
                animatorSet.start();
            }
            ValueAnimator valueAnimator = this.actionBarColorAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.progressToActionMode, 1.0f});
            this.actionBarColorAnimator = ofFloat;
            ofFloat.addUpdateListener(new DialogsActivity$$ExternalSyntheticLambda44(this));
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
        } else if (this.selectedDialogs.isEmpty()) {
            hideActionMode(true);
            return;
        } else {
            updateAnimated = true;
        }
        updateCounters(false);
        this.selectedDialogsCountTextView.setNumber(this.selectedDialogs.size(), updateAnimated);
    }

    /* renamed from: lambda$showOrUpdateActionMode$41$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3419lambda$showOrUpdateActionMode$41$orgtelegramuiDialogsActivity(ValueAnimator valueAnimator) {
        this.progressToActionMode = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        for (int i = 0; i < this.actionBar.getChildCount(); i++) {
            if (!(this.actionBar.getChildAt(i).getVisibility() != 0 || this.actionBar.getChildAt(i) == this.actionBar.getActionMode() || this.actionBar.getChildAt(i) == this.actionBar.getBackButton())) {
                this.actionBar.getChildAt(i).setAlpha(1.0f - this.progressToActionMode);
            }
        }
        if (this.fragmentView != null) {
            this.fragmentView.invalidate();
        }
    }

    /* access modifiers changed from: private */
    public void closeSearch() {
        if (AndroidUtilities.isTablet()) {
            if (this.actionBar != null) {
                this.actionBar.closeSearchField();
            }
            if (this.searchObject != null) {
                this.searchViewPager.dialogsSearchAdapter.putRecentSearch(this.searchDialogId, this.searchObject);
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
            UndoView old = undoViewArr[0];
            undoViewArr[0] = undoViewArr[1];
            undoViewArr[1] = old;
            old.hide(true, 2);
            ContentView contentView = (ContentView) this.fragmentView;
            contentView.removeView(this.undoView[0]);
            contentView.addView(this.undoView[0]);
        }
        return this.undoView[0];
    }

    /* access modifiers changed from: private */
    public void updateProxyButton(boolean animated, boolean force) {
        ActionBarMenuItem actionBarMenuItem;
        if (this.proxyDrawable != null) {
            ActionBarMenuItem actionBarMenuItem2 = this.doneItem;
            if (actionBarMenuItem2 == null || actionBarMenuItem2.getVisibility() != 0) {
                boolean showDownloads = false;
                int i = 0;
                while (true) {
                    if (i >= getDownloadController().downloadingFiles.size()) {
                        break;
                    } else if (getFileLoader().isLoadingFile(getDownloadController().downloadingFiles.get(i).getFileName())) {
                        showDownloads = true;
                        break;
                    } else {
                        i++;
                    }
                }
                boolean z = true;
                if (this.searching != 0 || (!getDownloadController().hasUnviewedDownloads() && !showDownloads && !(this.downloadsItem.getVisibility() == 0 && this.downloadsItem.getAlpha() == 1.0f && !force))) {
                    this.downloadsItem.setVisibility(8);
                    this.downloadsItemVisible = false;
                } else {
                    this.downloadsItemVisible = true;
                    this.downloadsItem.setVisibility(0);
                }
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                String proxyAddress = preferences.getString("proxy_ip", "");
                boolean proxyEnabled = preferences.getBoolean("proxy_enabled", false);
                if ((this.downloadsItemVisible || this.searching || !proxyEnabled || TextUtils.isEmpty(proxyAddress)) && (!getMessagesController().blockedCountry || SharedConfig.proxyList.isEmpty())) {
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
                if (!(i2 == 3 || i2 == 5)) {
                    z = false;
                }
                proxyDrawable2.setConnected(proxyEnabled, z, animated);
            }
        }
    }

    /* access modifiers changed from: private */
    public void showDoneItem(final boolean show) {
        if (this.doneItem != null) {
            AnimatorSet animatorSet = this.doneItemAnimator;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.doneItemAnimator = null;
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.doneItemAnimator = animatorSet2;
            animatorSet2.setDuration(180);
            if (show) {
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
            ArrayList<Animator> arrayList = new ArrayList<>();
            ActionBarMenuItem actionBarMenuItem5 = this.doneItem;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            float f = 1.0f;
            fArr[0] = show ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem5, property, fArr));
            if (this.proxyItemVisible) {
                ActionBarMenuItem actionBarMenuItem6 = this.proxyItem;
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                fArr2[0] = show ? 0.0f : 1.0f;
                arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem6, property2, fArr2));
            }
            if (this.passcodeItemVisible) {
                ActionBarMenuItem actionBarMenuItem7 = this.passcodeItem;
                Property property3 = View.ALPHA;
                float[] fArr3 = new float[1];
                fArr3[0] = show ? 0.0f : 1.0f;
                arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem7, property3, fArr3));
            }
            ActionBarMenuItem actionBarMenuItem8 = this.searchItem;
            Property property4 = View.ALPHA;
            float[] fArr4 = new float[1];
            if (show) {
                f = 0.0f;
            }
            fArr4[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem8, property4, fArr4));
            this.doneItemAnimator.playTogether(arrayList);
            this.doneItemAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AnimatorSet unused = DialogsActivity.this.doneItemAnimator = null;
                    if (show) {
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
                    this.actionBar.setTitle(LocaleController.getString("ForwardTo", NUM));
                } else {
                    this.actionBar.setTitle(LocaleController.getString("SelectChat", NUM));
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
                    this.commentViewAnimator.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.commentView, View.TRANSLATION_Y, new float[]{(float) this.commentView.getMeasuredHeight()}), ObjectAnimator.ofFloat(this.writeButtonContainer, View.SCALE_X, new float[]{0.2f}), ObjectAnimator.ofFloat(this.writeButtonContainer, View.SCALE_Y, new float[]{0.2f}), ObjectAnimator.ofFloat(this.writeButtonContainer, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.selectedCountView, View.SCALE_X, new float[]{0.2f}), ObjectAnimator.ofFloat(this.selectedCountView, View.SCALE_Y, new float[]{0.2f}), ObjectAnimator.ofFloat(this.selectedCountView, View.ALPHA, new float[]{0.0f})});
                    this.commentViewAnimator.setDuration(180);
                    this.commentViewAnimator.setInterpolator(new DecelerateInterpolator());
                    this.commentViewAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
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
                    AnimatorSet animatorSet2 = this.commentViewAnimator;
                    if (animatorSet2 != null) {
                        animatorSet2.cancel();
                    }
                    this.commentView.setVisibility(0);
                    this.writeButtonContainer.setVisibility(0);
                    AnimatorSet animatorSet3 = new AnimatorSet();
                    this.commentViewAnimator = animatorSet3;
                    animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.commentView, View.TRANSLATION_Y, new float[]{(float) this.commentView.getMeasuredHeight(), 0.0f}), ObjectAnimator.ofFloat(this.writeButtonContainer, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.writeButtonContainer, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.writeButtonContainer, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.selectedCountView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.selectedCountView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.selectedCountView, View.ALPHA, new float[]{1.0f})});
                    this.commentViewAnimator.setDuration(180);
                    this.commentViewAnimator.setInterpolator(new DecelerateInterpolator());
                    this.commentViewAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
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
        ChatActivityEnterView chatActivityEnterView = this.commentView;
        if (chatActivityEnterView != null) {
            charSequence = chatActivityEnterView.getFieldText();
        }
        boolean shouldShowNextButton = shouldShowNextButton(this, arrayList, charSequence, false);
        this.isNextButton = shouldShowNextButton;
        AndroidUtilities.updateViewVisibilityAnimated(this.writeButton[0], !shouldShowNextButton, 0.5f, true);
        AndroidUtilities.updateViewVisibilityAnimated(this.writeButton[1], this.isNextButton, 0.5f, true);
    }

    private void askForPermissons(boolean alert) {
        Activity activity = getParentActivity();
        if (activity != null) {
            ArrayList<String> permissons = new ArrayList<>();
            if (getUserConfig().syncContacts && this.askAboutContacts && activity.checkSelfPermission("android.permission.READ_CONTACTS") != 0) {
                if (alert) {
                    AlertDialog create = AlertsCreator.createContactsPermissionDialog(activity, new DialogsActivity$$ExternalSyntheticLambda43(this)).create();
                    this.permissionDialog = create;
                    showDialog(create);
                    return;
                }
                permissons.add("android.permission.READ_CONTACTS");
                permissons.add("android.permission.WRITE_CONTACTS");
                permissons.add("android.permission.GET_ACCOUNTS");
            }
            if ((Build.VERSION.SDK_INT <= 28 || BuildVars.NO_SCOPED_STORAGE) && activity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                permissons.add("android.permission.READ_EXTERNAL_STORAGE");
                permissons.add("android.permission.WRITE_EXTERNAL_STORAGE");
            }
            if (!permissons.isEmpty()) {
                try {
                    activity.requestPermissions((String[]) permissons.toArray(new String[0]), 1);
                } catch (Exception e) {
                }
            } else if (this.askingForPermissions) {
                this.askingForPermissions = false;
                showFiltersHint();
            }
        }
    }

    /* renamed from: lambda$askForPermissons$42$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3371lambda$askForPermissons$42$orgtelegramuiDialogsActivity(int param) {
        this.askAboutContacts = param != 0;
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

    public void onConfigurationChanged(Configuration newConfig) {
        FrameLayout frameLayout;
        super.onConfigurationChanged(newConfig);
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

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int a = 0; a < permissions.length; a++) {
                if (grantResults.length > a) {
                    String str = permissions[a];
                    char c = 65535;
                    switch (str.hashCode()) {
                        case 1365911975:
                            if (str.equals("android.permission.WRITE_EXTERNAL_STORAGE")) {
                                c = 1;
                                break;
                            }
                            break;
                        case 1977429404:
                            if (str.equals("android.permission.READ_CONTACTS")) {
                                c = 0;
                                break;
                            }
                            break;
                    }
                    switch (c) {
                        case 0:
                            if (grantResults[a] != 0) {
                                SharedPreferences.Editor edit = MessagesController.getGlobalNotificationsSettings().edit();
                                this.askAboutContacts = false;
                                edit.putBoolean("askAboutContacts", false).commit();
                                break;
                            } else {
                                AndroidUtilities.runOnUIThread(new DialogsActivity$$ExternalSyntheticLambda27(this));
                                getContactsController().forceImportContacts();
                                break;
                            }
                        case 1:
                            if (grantResults[a] != 0) {
                                break;
                            } else {
                                ImageLoader.getInstance().checkMediaPaths();
                                break;
                            }
                    }
                }
            }
            if (this.askingForPermissions) {
                this.askingForPermissions = false;
                showFiltersHint();
            }
        } else if (requestCode == 4) {
            boolean allGranted = true;
            int a2 = 0;
            while (true) {
                if (a2 >= grantResults.length) {
                    break;
                } else if (grantResults[a2] != 0) {
                    allGranted = false;
                    break;
                } else {
                    a2++;
                }
            }
            if (allGranted && Build.VERSION.SDK_INT >= 30 && FilesMigrationService.filesMigrationBottomSheet != null) {
                FilesMigrationService.filesMigrationBottomSheet.migrateOldFolder();
            }
        }
    }

    /* renamed from: lambda$onRequestPermissionsResultFragment$43$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3397x3var_cc5() {
        getNotificationCenter().postNotificationName(NotificationCenter.forceImportContactsStart, new Object[0]);
    }

    private void reloadViewPageDialogs(ViewPage viewPage, boolean newMessage) {
        int i;
        int i2;
        if (viewPage.getVisibility() == 0) {
            int oldItemCount = viewPage.dialogsAdapter.getCurrentCount();
            if (viewPage.dialogsType == 0 && hasHiddenArchive() && viewPage.listView.getChildCount() == 0) {
                ((LinearLayoutManager) viewPage.listView.getLayoutManager()).scrollToPositionWithOffset(1, 0);
            }
            if (viewPage.dialogsAdapter.isDataSetChanged() || newMessage) {
                viewPage.dialogsAdapter.updateHasHints();
                int newItemCount = viewPage.dialogsAdapter.getItemCount();
                if (newItemCount != 1 || oldItemCount != 1 || viewPage.dialogsAdapter.getItemViewType(0) != 5) {
                    viewPage.dialogsAdapter.notifyDataSetChanged();
                    if (!(newItemCount <= oldItemCount || (i = this.initialDialogsType) == 11 || i == 12 || i == 13)) {
                        viewPage.recyclerItemsEnterAnimator.showItemsAnimated(oldItemCount);
                    }
                } else if (viewPage.dialogsAdapter.lastDialogsEmptyType != viewPage.dialogsAdapter.dialogsEmptyType()) {
                    viewPage.dialogsAdapter.notifyItemChanged(0);
                }
            } else {
                updateVisibleRows(MessagesController.UPDATE_MASK_NEW_MESSAGE);
                if (!(viewPage.dialogsAdapter.getItemCount() <= oldItemCount || (i2 = this.initialDialogsType) == 11 || i2 == 12 || i2 == 13)) {
                    viewPage.recyclerItemsEnterAnimator.showItemsAnimated(oldItemCount);
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

    public void didReceivedNotification(int id, int account, Object... args) {
        int i = id;
        Object[] objArr = args;
        if (i == NotificationCenter.dialogsNeedReload) {
            if (this.viewPages != null && !this.dialogsListFrozen) {
                int a = 0;
                while (true) {
                    ViewPage[] viewPageArr = this.viewPages;
                    if (a >= viewPageArr.length) {
                        break;
                    }
                    ViewPage viewPage = viewPageArr[a];
                    MessagesController.DialogFilter filter = null;
                    if (viewPageArr[0].dialogsType == 7 || this.viewPages[0].dialogsType == 8) {
                        filter = getMessagesController().selectedDialogFilter[this.viewPages[0].dialogsType == 8 ? (char) 1 : 0];
                    }
                    boolean isUnread = (filter == null || (filter.flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ) == 0) ? false : true;
                    if (!this.slowedReloadAfterDialogClick || !isUnread) {
                        reloadViewPageDialogs(viewPage, objArr.length > 0);
                    } else {
                        AndroidUtilities.runOnUIThread(new DialogsActivity$$ExternalSyntheticLambda39(this, viewPage, objArr), 160);
                    }
                    a++;
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
            if (this.actionBar != null) {
                this.actionBar.closeSearchField();
            }
        } else if (i == NotificationCenter.proxySettingsChanged) {
            updateProxyButton(false, false);
        } else if (i == NotificationCenter.updateInterfaces) {
            Integer mask = (Integer) objArr[0];
            updateVisibleRows(mask.intValue());
            FilterTabsView filterTabsView6 = this.filterTabsView;
            if (!(filterTabsView6 == null || filterTabsView6.getVisibility() != 0 || (mask.intValue() & MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE) == 0)) {
                this.filterTabsView.checkTabsCounter();
            }
            if (this.viewPages != null) {
                for (int a2 = 0; a2 < this.viewPages.length; a2++) {
                    if ((mask.intValue() & MessagesController.UPDATE_MASK_STATUS) != 0) {
                        this.viewPages[a2].dialogsAdapter.sortOnlineContacts(true);
                    }
                }
            }
        } else if (i == NotificationCenter.appDidLogout) {
            dialogsLoaded[this.currentAccount] = false;
        } else if (i == NotificationCenter.encryptedChatUpdated) {
            updateVisibleRows(0);
        } else if (i == NotificationCenter.contactsDidLoad) {
            if (this.viewPages != null && !this.dialogsListFrozen) {
                boolean wasVisible = this.floatingProgressVisible;
                setFloatingProgressVisible(false, true);
                for (ViewPage page : this.viewPages) {
                    page.dialogsAdapter.setForceUpdatingContacts(false);
                }
                if (wasVisible) {
                    setContactsAlpha(0.0f);
                    animateContactsAlpha(1.0f);
                }
                boolean updateVisibleRows = false;
                int a3 = 0;
                while (true) {
                    ViewPage[] viewPageArr2 = this.viewPages;
                    if (a3 >= viewPageArr2.length) {
                        break;
                    }
                    if (!viewPageArr2[a3].isDefaultDialogType() || getMessagesController().getAllFoldersDialogsCount() > 10) {
                        updateVisibleRows = true;
                    } else {
                        this.viewPages[a3].dialogsAdapter.notifyDataSetChanged();
                    }
                    a3++;
                }
                if (updateVisibleRows) {
                    updateVisibleRows(0);
                }
            }
        } else if (i == NotificationCenter.openedChatChanged) {
            if (this.viewPages != null) {
                int a4 = 0;
                while (true) {
                    ViewPage[] viewPageArr3 = this.viewPages;
                    if (a4 < viewPageArr3.length) {
                        if (viewPageArr3[a4].isDefaultDialogType() && AndroidUtilities.isTablet()) {
                            boolean close = ((Boolean) objArr[1]).booleanValue();
                            long dialog_id = ((Long) objArr[0]).longValue();
                            if (!close) {
                                this.openedDialogId = dialog_id;
                            } else if (dialog_id == this.openedDialogId) {
                                this.openedDialogId = 0;
                            }
                            this.viewPages[a4].dialogsAdapter.setOpenedDialogId(this.openedDialogId);
                        }
                        a4++;
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
            if (searchViewPager2 != null && searchViewPager2.dialogsSearchAdapter != null) {
                this.searchViewPager.dialogsSearchAdapter.loadRecentSearch();
            }
        } else if (i == NotificationCenter.replyMessagesDidLoad) {
            updateVisibleRows(MessagesController.UPDATE_MASK_MESSAGE_TEXT);
        } else if (i == NotificationCenter.reloadHints) {
            SearchViewPager searchViewPager3 = this.searchViewPager;
            if (searchViewPager3 != null && searchViewPager3.dialogsSearchAdapter != null) {
                this.searchViewPager.dialogsSearchAdapter.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.didUpdateConnectionState) {
            int state = AccountInstance.getInstance(account).getConnectionsManager().getConnectionState();
            if (this.currentConnectionState != state) {
                this.currentConnectionState = state;
                updateProxyButton(true, false);
            }
        } else if (i == NotificationCenter.onDownloadingFilesChanged) {
            updateProxyButton(true, false);
        } else if (i == NotificationCenter.needDeleteDialog) {
            if (this.fragmentView != null && !this.isPaused) {
                long dialogId = ((Long) objArr[0]).longValue();
                Runnable deleteRunnable = new DialogsActivity$$ExternalSyntheticLambda37(this, (TLRPC.Chat) objArr[2], dialogId, ((Boolean) objArr[3]).booleanValue(), (TLRPC.User) objArr[1]);
                if (this.undoView[0] != null) {
                    getUndoView().showWithAction(dialogId, 1, deleteRunnable);
                } else {
                    deleteRunnable.run();
                }
            }
        } else if (i == NotificationCenter.folderBecomeEmpty) {
            int fid = ((Integer) objArr[0]).intValue();
            int i2 = this.folderId;
            if (i2 == fid && i2 != 0) {
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
            for (ViewPage page2 : this.viewPages) {
                page2.dialogsAdapter.setForceShowEmptyCell(false);
                page2.dialogsAdapter.setForceUpdatingContacts(true);
                page2.dialogsAdapter.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.messagesDeleted) {
            if (this.searchIsShowed && this.searchViewPager != null) {
                this.searchViewPager.messagesDeleted(((Long) objArr[1]).longValue(), (ArrayList) objArr[0]);
            }
        } else if (i == NotificationCenter.didClearDatabase) {
            if (this.viewPages != null) {
                int a5 = 0;
                while (true) {
                    ViewPage[] viewPageArr4 = this.viewPages;
                    if (a5 >= viewPageArr4.length) {
                        break;
                    }
                    viewPageArr4[a5].dialogsAdapter.didDatabaseCleared();
                    a5++;
                }
            }
            SuggestClearDatabaseBottomSheet.dismissDialog();
        } else if (i == NotificationCenter.appUpdateAvailable) {
            updateMenuButton(true);
        } else if (i == NotificationCenter.fileLoaded || i == NotificationCenter.fileLoadFailed || i == NotificationCenter.fileLoadProgressChanged) {
            String name = (String) objArr[0];
            if (SharedConfig.isAppUpdateAvailable() && FileLoader.getAttachFileName(SharedConfig.pendingAppUpdate.document).equals(name)) {
                updateMenuButton(true);
            }
        } else if (i == NotificationCenter.onDatabaseMigration) {
            boolean startMigration = ((Boolean) objArr[0]).booleanValue();
            if (this.fragmentView == null) {
                return;
            }
            if (startMigration) {
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
                final View localView = this.databaseMigrationHint;
                localView.animate().setListener((Animator.AnimatorListener) null).cancel();
                localView.animate().setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (localView.getParent() != null) {
                            ((ViewGroup) localView.getParent()).removeView(localView);
                        }
                        DialogsActivity.this.databaseMigrationHint = null;
                    }
                }).alpha(0.0f).setStartDelay(0).setDuration(150).start();
                this.databaseMigrationHint.setTag((Object) null);
            }
        } else if (i == NotificationCenter.onDatabaseOpened) {
            checkSuggestClearDatabase();
        }
    }

    /* renamed from: lambda$didReceivedNotification$44$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3384xa0368376(ViewPage viewPage, Object[] args) {
        reloadViewPageDialogs(viewPage, args.length > 0);
        FilterTabsView filterTabsView2 = this.filterTabsView;
        if (filterTabsView2 != null && filterTabsView2.getVisibility() == 0) {
            this.filterTabsView.checkTabsCounter();
        }
    }

    /* renamed from: lambda$didReceivedNotification$45$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3385x1e0e3var_(TLRPC.Chat chat, long dialogId, boolean revoke, TLRPC.User user) {
        if (chat == null) {
            getMessagesController().deleteDialog(dialogId, 0, revoke);
            if (user != null && user.bot) {
                getMessagesController().blockPeer(user.id);
            }
        } else if (ChatObject.isNotInChat(chat)) {
            getMessagesController().deleteDialog(dialogId, 0, revoke);
        } else {
            getMessagesController().deleteParticipantFromChat(-dialogId, getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId())), (TLRPC.Chat) null, (TLRPC.ChatFull) null, revoke, revoke);
        }
        getMessagesController().checkIfFolderEmpty(this.folderId);
    }

    private void checkSuggestClearDatabase() {
        if (getMessagesStorage().showClearDatabaseAlert) {
            getMessagesStorage().showClearDatabaseAlert = false;
            SuggestClearDatabaseBottomSheet.show(this);
        }
    }

    private void updateMenuButton(boolean animated) {
        float downloadProgress;
        int type;
        if (this.menuDrawable != null && this.updateLayout != null) {
            if (SharedConfig.isAppUpdateAvailable()) {
                String fileName = FileLoader.getAttachFileName(SharedConfig.pendingAppUpdate.document);
                if (getFileLoader().isLoadingFile(fileName)) {
                    type = MenuDrawable.TYPE_UDPATE_DOWNLOADING;
                    Float p = ImageLoader.getInstance().getFileProgress(fileName);
                    downloadProgress = p != null ? p.floatValue() : 0.0f;
                } else {
                    type = MenuDrawable.TYPE_UDPATE_AVAILABLE;
                    downloadProgress = 0.0f;
                }
            } else {
                type = MenuDrawable.TYPE_DEFAULT;
                downloadProgress = 0.0f;
            }
            updateAppUpdateViews(animated);
            this.menuDrawable.setType(type, animated);
            this.menuDrawable.setUpdateDownloadProgress(downloadProgress, animated);
        }
    }

    private void showNextSupportedSuggestion() {
        if (this.showingSuggestion == null) {
            for (String suggestion : getMessagesController().pendingSuggestions) {
                if (showSuggestion(suggestion)) {
                    this.showingSuggestion = suggestion;
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

    private boolean showSuggestion(String suggestion) {
        if (!"AUTOARCHIVE_POPULAR".equals(suggestion)) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("HideNewChatsAlertTitle", NUM));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("HideNewChatsAlertText", NUM)));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(LocaleController.getString("GoToSettings", NUM), new DialogsActivity$$ExternalSyntheticLambda1(this));
        showDialog(builder.create(), new DialogsActivity$$ExternalSyntheticLambda7(this));
        return true;
    }

    /* renamed from: lambda$showSuggestion$46$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3421lambda$showSuggestion$46$orgtelegramuiDialogsActivity(DialogInterface dialog, int which) {
        presentFragment(new PrivacySettingsActivity());
        AndroidUtilities.scrollToFragmentRow(this.parentLayout, "newChatsRow");
    }

    /* renamed from: lambda$showSuggestion$47$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3422lambda$showSuggestion$47$orgtelegramuiDialogsActivity(DialogInterface dialog) {
        onSuggestionDismiss();
    }

    private void showFiltersHint() {
        if (!this.askingForPermissions && getMessagesController().dialogFiltersLoaded && getMessagesController().showFiltersTooltip && this.filterTabsView != null && getMessagesController().dialogFilters.isEmpty() && !this.isPaused && getUserConfig().filtersLoaded && !this.inPreviewMode) {
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            if (!preferences.getBoolean("filterhint", false)) {
                preferences.edit().putBoolean("filterhint", true).commit();
                AndroidUtilities.runOnUIThread(new DialogsActivity$$ExternalSyntheticLambda30(this), 1000);
            }
        }
    }

    /* renamed from: lambda$showFiltersHint$48$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3417lambda$showFiltersHint$48$orgtelegramuiDialogsActivity() {
        presentFragment(new FiltersSetupActivity());
    }

    /* renamed from: lambda$showFiltersHint$49$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3418lambda$showFiltersHint$49$orgtelegramuiDialogsActivity() {
        getUndoView().showWithAction(0, 15, (Runnable) null, new DialogsActivity$$ExternalSyntheticLambda29(this));
    }

    private void setDialogsListFrozen(boolean frozen, boolean notify2) {
        if (this.viewPages != null && this.dialogsListFrozen != frozen) {
            if (frozen) {
                this.frozenDialogsList = new ArrayList<>(getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, false));
            } else {
                this.frozenDialogsList = null;
            }
            this.dialogsListFrozen = frozen;
            this.viewPages[0].dialogsAdapter.setDialogsListFrozen(frozen);
            if (!frozen && notify2) {
                this.viewPages[0].dialogsAdapter.notifyDataSetChanged();
            }
        }
    }

    /* access modifiers changed from: private */
    public void setDialogsListFrozen(boolean frozen) {
        setDialogsListFrozen(frozen, true);
    }

    public class DialogsHeader extends TLRPC.Dialog {
        public static final int HEADER_TYPE_GROUPS = 2;
        public static final int HEADER_TYPE_MY_CHANNELS = 0;
        public static final int HEADER_TYPE_MY_GROUPS = 1;
        public int headerType;

        public DialogsHeader(int type) {
            this.headerType = type;
        }
    }

    public ArrayList<TLRPC.Dialog> getDialogsArray(int currentAccount, int dialogsType, int folderId2, boolean frozen) {
        ArrayList<TLRPC.Dialog> arrayList;
        if (frozen && (arrayList = this.frozenDialogsList) != null) {
            return arrayList;
        }
        MessagesController messagesController = AccountInstance.getInstance(currentAccount).getMessagesController();
        if (dialogsType == 0) {
            return messagesController.getDialogs(folderId2);
        }
        char c = 1;
        if (dialogsType == 1 || dialogsType == 10 || dialogsType == 13) {
            return messagesController.dialogsServerOnly;
        }
        if (dialogsType == 2) {
            ArrayList<TLRPC.Dialog> dialogs = new ArrayList<>(messagesController.dialogsCanAddUsers.size() + messagesController.dialogsMyChannels.size() + messagesController.dialogsMyGroups.size() + 2);
            if (messagesController.dialogsMyChannels.size() > 0 && this.allowChannels) {
                dialogs.add(new DialogsHeader(0));
                dialogs.addAll(messagesController.dialogsMyChannels);
            }
            if (messagesController.dialogsMyGroups.size() > 0 && this.allowGroups) {
                dialogs.add(new DialogsHeader(1));
                dialogs.addAll(messagesController.dialogsMyGroups);
            }
            if (messagesController.dialogsCanAddUsers.size() > 0) {
                int count = messagesController.dialogsCanAddUsers.size();
                boolean first = true;
                for (int i = 0; i < count; i++) {
                    TLRPC.Dialog dialog = messagesController.dialogsCanAddUsers.get(i);
                    if ((this.allowChannels && ChatObject.isChannelAndNotMegaGroup(-dialog.id, currentAccount)) || (this.allowGroups && (ChatObject.isMegagroup(currentAccount, -dialog.id) || !ChatObject.isChannel(-dialog.id, currentAccount)))) {
                        if (first) {
                            dialogs.add(new DialogsHeader(2));
                            first = false;
                        }
                        dialogs.add(dialog);
                    }
                }
            }
            return dialogs;
        } else if (dialogsType == 3) {
            return messagesController.dialogsForward;
        } else {
            if (dialogsType == 4 || dialogsType == 12) {
                return messagesController.dialogsUsersOnly;
            }
            if (dialogsType == 5) {
                return messagesController.dialogsChannelsOnly;
            }
            if (dialogsType == 6 || dialogsType == 11) {
                return messagesController.dialogsGroupsOnly;
            }
            if (dialogsType == 7 || dialogsType == 8) {
                MessagesController.DialogFilter[] dialogFilterArr = messagesController.selectedDialogFilter;
                if (dialogsType == 7) {
                    c = 0;
                }
                MessagesController.DialogFilter dialogFilter = dialogFilterArr[c];
                if (dialogFilter == null) {
                    return messagesController.getDialogs(folderId2);
                }
                return dialogFilter.dialogs;
            } else if (dialogsType == 9) {
                return messagesController.dialogsForBlock;
            } else {
                if (dialogsType != 14) {
                    return new ArrayList<>();
                }
                ArrayList<TLRPC.Dialog> dialogs2 = new ArrayList<>();
                if (this.allowUsers || this.allowBots) {
                    Iterator<TLRPC.Dialog> it = messagesController.dialogsUsersOnly.iterator();
                    while (it.hasNext()) {
                        TLRPC.Dialog d = it.next();
                        if (messagesController.getUser(Long.valueOf(d.id)).bot) {
                            if (!this.allowBots) {
                            }
                        } else if (!this.allowUsers) {
                        }
                        dialogs2.add(d);
                    }
                }
                if (this.allowGroups) {
                    dialogs2.addAll(messagesController.dialogsGroupsOnly);
                }
                if (this.allowChannels) {
                    dialogs2.addAll(messagesController.dialogsChannelsOnly);
                }
                return dialogs2;
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

    private void setFloatingProgressVisible(final boolean visible, boolean animate) {
        if (this.floatingButton != null && this.floatingProgressView != null) {
            float f = 0.0f;
            float f2 = 0.1f;
            if (!animate) {
                AnimatorSet animatorSet = this.floatingProgressAnimator;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                this.floatingProgressVisible = visible;
                if (visible) {
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
            } else if (visible != this.floatingProgressVisible) {
                AnimatorSet animatorSet2 = this.floatingProgressAnimator;
                if (animatorSet2 != null) {
                    animatorSet2.cancel();
                }
                this.floatingProgressVisible = visible;
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.floatingProgressAnimator = animatorSet3;
                Animator[] animatorArr = new Animator[6];
                RLottieImageView rLottieImageView = this.floatingButton;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = visible ? 0.0f : 1.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(rLottieImageView, property, fArr);
                RLottieImageView rLottieImageView2 = this.floatingButton;
                Property property2 = View.SCALE_X;
                float[] fArr2 = new float[1];
                fArr2[0] = visible ? 0.1f : 1.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(rLottieImageView2, property2, fArr2);
                RLottieImageView rLottieImageView3 = this.floatingButton;
                Property property3 = View.SCALE_Y;
                float[] fArr3 = new float[1];
                fArr3[0] = visible ? 0.1f : 1.0f;
                animatorArr[2] = ObjectAnimator.ofFloat(rLottieImageView3, property3, fArr3);
                RadialProgressView radialProgressView = this.floatingProgressView;
                Property property4 = View.ALPHA;
                float[] fArr4 = new float[1];
                if (visible) {
                    f = 1.0f;
                }
                fArr4[0] = f;
                animatorArr[3] = ObjectAnimator.ofFloat(radialProgressView, property4, fArr4);
                RadialProgressView radialProgressView2 = this.floatingProgressView;
                Property property5 = View.SCALE_X;
                float[] fArr5 = new float[1];
                fArr5[0] = visible ? 1.0f : 0.1f;
                animatorArr[4] = ObjectAnimator.ofFloat(radialProgressView2, property5, fArr5);
                RadialProgressView radialProgressView3 = this.floatingProgressView;
                Property property6 = View.SCALE_Y;
                float[] fArr6 = new float[1];
                if (visible) {
                    f2 = 1.0f;
                }
                fArr6[0] = f2;
                animatorArr[5] = ObjectAnimator.ofFloat(radialProgressView3, property6, fArr6);
                animatorSet3.playTogether(animatorArr);
                this.floatingProgressAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animation) {
                        DialogsActivity.this.floatingProgressView.setVisibility(0);
                        DialogsActivity.this.floatingButton.setVisibility(0);
                    }

                    public void onAnimationEnd(Animator animation) {
                        if (animation == DialogsActivity.this.floatingProgressAnimator) {
                            if (visible) {
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
    public void hideFloatingButton(boolean hide) {
        if (this.floatingHidden == hide) {
            return;
        }
        if (!hide || !this.floatingForceVisible) {
            this.floatingHidden = hide;
            AnimatorSet animatorSet = new AnimatorSet();
            float[] fArr = new float[2];
            fArr[0] = this.floatingButtonHideProgress;
            fArr[1] = this.floatingHidden ? 1.0f : 0.0f;
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(fArr);
            valueAnimator.addUpdateListener(new DialogsActivity$$ExternalSyntheticLambda33(this));
            animatorSet.playTogether(new Animator[]{valueAnimator});
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(this.floatingInterpolator);
            this.floatingButtonContainer.setClickable(!hide);
            animatorSet.start();
        }
    }

    /* renamed from: lambda$hideFloatingButton$50$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3393lambda$hideFloatingButton$50$orgtelegramuiDialogsActivity(ValueAnimator animation) {
        this.floatingButtonHideProgress = ((Float) animation.getAnimatedValue()).floatValue();
        this.floatingButtonTranslation = ((float) AndroidUtilities.dp(100.0f)) * this.floatingButtonHideProgress;
        updateFloatingButtonOffset();
    }

    public float getContactsAlpha() {
        return this.contactsAlpha;
    }

    public void animateContactsAlpha(float alpha) {
        ValueAnimator valueAnimator = this.contactsAlphaAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator duration = ValueAnimator.ofFloat(new float[]{this.contactsAlpha, alpha}).setDuration(250);
        this.contactsAlphaAnimator = duration;
        duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.contactsAlphaAnimator.addUpdateListener(new DialogsActivity$$ExternalSyntheticLambda0(this));
        this.contactsAlphaAnimator.start();
    }

    /* renamed from: lambda$animateContactsAlpha$51$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3370lambda$animateContactsAlpha$51$orgtelegramuiDialogsActivity(ValueAnimator animation) {
        setContactsAlpha(((Float) animation.getAnimatedValue()).floatValue());
    }

    public void setContactsAlpha(float alpha) {
        this.contactsAlpha = alpha;
        for (ViewPage p : this.viewPages) {
            RecyclerListView listView = p.listView;
            for (int i = 0; i < listView.getChildCount(); i++) {
                View v = listView.getChildAt(i);
                if (listView.getChildAdapterPosition(v) >= p.dialogsAdapter.getDialogsCount() + 1) {
                    v.setAlpha(alpha);
                }
            }
        }
    }

    public void setScrollDisabled(boolean disable) {
        for (ViewPage p : this.viewPages) {
            ((LinearLayoutManager) p.listView.getLayoutManager()).setScrollDisabled(disable);
        }
    }

    /* access modifiers changed from: private */
    public void updateDialogIndices() {
        int index;
        if (this.viewPages != null) {
            int b = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (b < viewPageArr.length) {
                    if (viewPageArr[b].getVisibility() == 0) {
                        ArrayList<TLRPC.Dialog> dialogs = getDialogsArray(this.currentAccount, this.viewPages[b].dialogsType, this.folderId, false);
                        int count = this.viewPages[b].listView.getChildCount();
                        for (int a = 0; a < count; a++) {
                            View child = this.viewPages[b].listView.getChildAt(a);
                            if (child instanceof DialogCell) {
                                DialogCell dialogCell = (DialogCell) child;
                                TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(dialogCell.getDialogId());
                                if (dialog != null && (index = dialogs.indexOf(dialog)) >= 0) {
                                    dialogCell.setDialogIndex(index);
                                }
                            }
                        }
                    }
                    b++;
                } else {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateVisibleRows(int mask) {
        updateVisibleRows(mask, true);
    }

    private void updateVisibleRows(int mask, boolean animated) {
        RecyclerListView list;
        if ((!this.dialogsListFrozen || (MessagesController.UPDATE_MASK_REORDER & mask) != 0) && !this.isPaused) {
            for (int c = 0; c < 3; c++) {
                RecyclerListView recyclerListView = null;
                if (c == 2) {
                    SearchViewPager searchViewPager2 = this.searchViewPager;
                    if (searchViewPager2 != null) {
                        recyclerListView = searchViewPager2.searchListView;
                    }
                    list = recyclerListView;
                } else {
                    ViewPage[] viewPageArr = this.viewPages;
                    if (viewPageArr != null) {
                        if (c < viewPageArr.length) {
                            recyclerListView = viewPageArr[c].listView;
                        }
                        list = recyclerListView;
                        if (!(list == null || this.viewPages[c].getVisibility() == 0)) {
                        }
                    }
                }
                if (list != null) {
                    int count = list.getChildCount();
                    for (int a = 0; a < count; a++) {
                        View child = list.getChildAt(a);
                        if ((child instanceof DialogCell) && list.getAdapter() != this.searchViewPager.dialogsSearchAdapter) {
                            DialogCell cell = (DialogCell) child;
                            boolean z = true;
                            if ((MessagesController.UPDATE_MASK_REORDER & mask) != 0) {
                                cell.onReorderStateChanged(this.actionBar.isActionModeShowed(), true);
                                if (this.dialogsListFrozen) {
                                }
                            }
                            if ((MessagesController.UPDATE_MASK_CHECK & mask) != 0) {
                                if ((MessagesController.UPDATE_MASK_CHAT & mask) == 0) {
                                    z = false;
                                }
                                cell.setChecked(false, z);
                            } else {
                                if ((MessagesController.UPDATE_MASK_NEW_MESSAGE & mask) != 0) {
                                    cell.checkCurrentDialogIndex(this.dialogsListFrozen);
                                    if (this.viewPages[c].isDefaultDialogType() && AndroidUtilities.isTablet()) {
                                        if (cell.getDialogId() != this.openedDialogId) {
                                            z = false;
                                        }
                                        cell.setDialogSelected(z);
                                    }
                                } else if ((MessagesController.UPDATE_MASK_SELECT_DIALOG & mask) == 0) {
                                    cell.update(mask, animated);
                                } else if (this.viewPages[c].isDefaultDialogType() && AndroidUtilities.isTablet()) {
                                    if (cell.getDialogId() != this.openedDialogId) {
                                        z = false;
                                    }
                                    cell.setDialogSelected(z);
                                }
                                ArrayList<Long> arrayList = this.selectedDialogs;
                                if (arrayList != null) {
                                    cell.setChecked(arrayList.contains(Long.valueOf(cell.getDialogId())), false);
                                }
                            }
                        }
                        if (child instanceof UserCell) {
                            ((UserCell) child).update(mask);
                        } else if (child instanceof ProfileSearchCell) {
                            ProfileSearchCell cell2 = (ProfileSearchCell) child;
                            cell2.update(mask);
                            ArrayList<Long> arrayList2 = this.selectedDialogs;
                            if (arrayList2 != null) {
                                cell2.setChecked(arrayList2.contains(Long.valueOf(cell2.getDialogId())), false);
                            }
                        }
                        if (!this.dialogsListFrozen && (child instanceof RecyclerListView)) {
                            RecyclerListView innerListView = (RecyclerListView) child;
                            int count2 = innerListView.getChildCount();
                            for (int b = 0; b < count2; b++) {
                                View child2 = innerListView.getChildAt(b);
                                if (child2 instanceof HintDialogCell) {
                                    ((HintDialogCell) child2).update(mask);
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

    public boolean shouldShowNextButton(DialogsActivity fragment, ArrayList<Long> arrayList, CharSequence message, boolean param) {
        return false;
    }

    public void setSearchString(String string) {
        this.searchString = string;
    }

    public void setInitialSearchString(String initialSearchString2) {
        this.initialSearchString = initialSearchString2;
    }

    public boolean isMainDialogList() {
        return this.delegate == null && this.searchString == null;
    }

    public void setInitialSearchType(int type) {
        this.initialSearchType = type;
    }

    private boolean checkCanWrite(long dialogId) {
        if (this.addToGroupAlertString != null || !this.checkCanWrite) {
            return true;
        }
        if (DialogObject.isChatDialog(dialogId)) {
            TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(-dialogId));
            if (!ChatObject.isChannel(chat) || chat.megagroup) {
                return true;
            }
            if (!this.cantSendToChannels && ChatObject.isCanWriteToChannel(-dialogId, this.currentAccount) && this.hasPoll != 2) {
                return true;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("SendMessageTitle", NUM));
            if (this.hasPoll == 2) {
                builder.setMessage(LocaleController.getString("PublicPollCantForward", NUM));
            } else {
                builder.setMessage(LocaleController.getString("ChannelCantSendMessage", NUM));
            }
            builder.setNegativeButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
            return false;
        } else if (!DialogObject.isEncryptedDialog(dialogId)) {
            return true;
        } else {
            if (this.hasPoll == 0 && !this.hasInvoice) {
                return true;
            }
            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
            builder2.setTitle(LocaleController.getString("SendMessageTitle", NUM));
            if (this.hasPoll != 0) {
                builder2.setMessage(LocaleController.getString("PollCantForwardSecretChat", NUM));
            } else {
                builder2.setMessage(LocaleController.getString("InvoiceCantForwardSecretChat", NUM));
            }
            builder2.setNegativeButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder2.create());
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void didSelectResult(long dialogId, boolean useAlert, boolean param) {
        TLRPC.Chat chat;
        TLRPC.User user;
        String message;
        String title;
        String buttonText;
        long j = dialogId;
        if (checkCanWrite(dialogId)) {
            int i = this.initialDialogsType;
            if (i == 11 || i == 12) {
                boolean z = param;
            } else if (i == 13) {
                boolean z2 = param;
            } else if (!useAlert || ((this.selectAlertString == null || this.selectAlertStringGroup == null) && this.addToGroupAlertString == null)) {
                if (this.delegate != null) {
                    ArrayList<Long> dids = new ArrayList<>();
                    dids.add(Long.valueOf(dialogId));
                    this.delegate.didSelectDialogs(this, dids, (CharSequence) null, param);
                    if (this.resetDelegate) {
                        this.delegate = null;
                        return;
                    }
                    return;
                }
                boolean z3 = param;
                finishFragment();
                return;
            } else if (getParentActivity() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                if (DialogObject.isEncryptedDialog(dialogId)) {
                    TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(dialogId))).user_id));
                    if (user2 != null) {
                        title = LocaleController.getString("SendMessageTitle", NUM);
                        message = LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(user2));
                        buttonText = LocaleController.getString("Send", NUM);
                    } else {
                        return;
                    }
                } else if (!DialogObject.isUserDialog(dialogId)) {
                    TLRPC.Chat chat2 = getMessagesController().getChat(Long.valueOf(-j));
                    if (chat2 != null) {
                        if (this.addToGroupAlertString != null) {
                            title = LocaleController.getString("AddToTheGroupAlertTitle", NUM);
                            message = LocaleController.formatStringSimple(this.addToGroupAlertString, chat2.title);
                            buttonText = LocaleController.getString("Add", NUM);
                        } else {
                            title = LocaleController.getString("SendMessageTitle", NUM);
                            message = LocaleController.formatStringSimple(this.selectAlertStringGroup, chat2.title);
                            buttonText = LocaleController.getString("Send", NUM);
                        }
                    } else {
                        return;
                    }
                } else if (j == getUserConfig().getClientUserId()) {
                    title = LocaleController.getString("SendMessageTitle", NUM);
                    message = LocaleController.formatStringSimple(this.selectAlertStringGroup, LocaleController.getString("SavedMessages", NUM));
                    buttonText = LocaleController.getString("Send", NUM);
                } else {
                    TLRPC.User user3 = getMessagesController().getUser(Long.valueOf(dialogId));
                    if (user3 != null && this.selectAlertString != null) {
                        title = LocaleController.getString("SendMessageTitle", NUM);
                        message = LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(user3));
                        buttonText = LocaleController.getString("Send", NUM);
                    } else {
                        return;
                    }
                }
                builder.setTitle(title);
                builder.setMessage(AndroidUtilities.replaceTags(message));
                builder.setPositiveButton(buttonText, new DialogsActivity$$ExternalSyntheticLambda2(this, j));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                showDialog(builder.create());
                return;
            } else {
                return;
            }
            if (!this.checkingImportDialog) {
                if (DialogObject.isUserDialog(dialogId)) {
                    TLRPC.User user4 = getMessagesController().getUser(Long.valueOf(dialogId));
                    if (!user4.mutual_contact) {
                        getUndoView().showWithAction(j, 45, (Runnable) null);
                        return;
                    } else {
                        user = user4;
                        chat = null;
                    }
                } else {
                    TLRPC.Chat chat3 = getMessagesController().getChat(Long.valueOf(-j));
                    if (!ChatObject.hasAdminRights(chat3) || !ChatObject.canChangeChatInfo(chat3)) {
                        getUndoView().showWithAction(j, 46, (Runnable) null);
                        return;
                    } else {
                        user = null;
                        chat = chat3;
                    }
                }
                AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
                TLRPC.TL_messages_checkHistoryImportPeer req = new TLRPC.TL_messages_checkHistoryImportPeer();
                req.peer = getMessagesController().getInputPeer(j);
                ConnectionsManager connectionsManager = getConnectionsManager();
                DialogsActivity$$ExternalSyntheticLambda46 dialogsActivity$$ExternalSyntheticLambda46 = r1;
                TLRPC.TL_messages_checkHistoryImportPeer req2 = req;
                AlertDialog progressDialog2 = progressDialog;
                DialogsActivity$$ExternalSyntheticLambda46 dialogsActivity$$ExternalSyntheticLambda462 = new DialogsActivity$$ExternalSyntheticLambda46(this, progressDialog, user, chat, dialogId, param, req2);
                connectionsManager.sendRequest(req2, dialogsActivity$$ExternalSyntheticLambda46);
                try {
                    progressDialog2.showDelayed(300);
                } catch (Exception e) {
                }
            }
        }
    }

    /* renamed from: lambda$didSelectResult$54$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3388lambda$didSelectResult$54$orgtelegramuiDialogsActivity(AlertDialog progressDialog, TLRPC.User user, TLRPC.Chat chat, long dialogId, boolean param, TLRPC.TL_messages_checkHistoryImportPeer req, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new DialogsActivity$$ExternalSyntheticLambda38(this, progressDialog, response, user, chat, dialogId, param, error, req));
    }

    /* renamed from: lambda$didSelectResult$53$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3387lambda$didSelectResult$53$orgtelegramuiDialogsActivity(AlertDialog progressDialog, TLObject response, TLRPC.User user, TLRPC.Chat chat, long dialogId, boolean param, TLRPC.TL_error error, TLRPC.TL_messages_checkHistoryImportPeer req) {
        TLRPC.TL_error tL_error = error;
        TLRPC.TL_messages_checkHistoryImportPeer tL_messages_checkHistoryImportPeer = req;
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        this.checkingImportDialog = false;
        if (response != null) {
            AlertsCreator.createImportDialogAlert(this, this.arguments.getString("importTitle"), ((TLRPC.TL_messages_checkedHistoryImportPeer) response).confirm_text, user, chat, new DialogsActivity$$ExternalSyntheticLambda34(this, dialogId, param));
            return;
        }
        long j = dialogId;
        boolean z = param;
        AlertsCreator.processError(this.currentAccount, tL_error, this, tL_messages_checkHistoryImportPeer, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(dialogId), tL_messages_checkHistoryImportPeer, tL_error);
    }

    /* renamed from: lambda$didSelectResult$52$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3386lambda$didSelectResult$52$orgtelegramuiDialogsActivity(long dialogId, boolean param) {
        setDialogsListFrozen(true);
        ArrayList<Long> dids = new ArrayList<>();
        dids.add(Long.valueOf(dialogId));
        this.delegate.didSelectDialogs(this, dids, (CharSequence) null, param);
    }

    /* renamed from: lambda$didSelectResult$55$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3389lambda$didSelectResult$55$orgtelegramuiDialogsActivity(long dialogId, DialogInterface dialogInterface, int i) {
        didSelectResult(dialogId, false, false);
    }

    public RLottieImageView getFloatingButton() {
        return this.floatingButton;
    }

    private boolean onSendLongClick(View view) {
        Activity parentActivity = getParentActivity();
        Theme.ResourcesProvider resourcesProvider = getResourceProvider();
        if (parentActivity == null) {
            return false;
        }
        LinearLayout layout = new LinearLayout(parentActivity);
        layout.setOrientation(1);
        ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout2 = new ActionBarPopupWindow.ActionBarPopupWindowLayout(parentActivity, resourcesProvider);
        sendPopupLayout2.setAnimationEnabled(false);
        sendPopupLayout2.setOnTouchListener(new View.OnTouchListener() {
            private Rect popupRect = new Rect();

            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() != 0 || DialogsActivity.this.sendPopupWindow == null || !DialogsActivity.this.sendPopupWindow.isShowing()) {
                    return false;
                }
                v.getHitRect(this.popupRect);
                if (this.popupRect.contains((int) event.getX(), (int) event.getY())) {
                    return false;
                }
                DialogsActivity.this.sendPopupWindow.dismiss();
                return false;
            }
        });
        sendPopupLayout2.setDispatchKeyEventListener(new DialogsActivity$$ExternalSyntheticLambda47(this));
        sendPopupLayout2.setShownFromBottom(false);
        sendPopupLayout2.setupRadialSelectors(getThemedColor("dialogButtonSelector"));
        ActionBarMenuSubItem sendWithoutSound = new ActionBarMenuSubItem((Context) parentActivity, true, true, resourcesProvider);
        sendWithoutSound.setTextAndIcon(LocaleController.getString("SendWithoutSound", NUM), NUM);
        sendWithoutSound.setMinimumWidth(AndroidUtilities.dp(196.0f));
        sendPopupLayout2.addView(sendWithoutSound, LayoutHelper.createLinear(-1, 48));
        sendWithoutSound.setOnClickListener(new DialogsActivity$$ExternalSyntheticLambda14(this));
        layout.addView(sendPopupLayout2, LayoutHelper.createLinear(-1, -2));
        ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(layout, -2, -2);
        this.sendPopupWindow = actionBarPopupWindow;
        actionBarPopupWindow.setAnimationEnabled(false);
        this.sendPopupWindow.setAnimationStyle(NUM);
        this.sendPopupWindow.setOutsideTouchable(true);
        this.sendPopupWindow.setClippingEnabled(true);
        this.sendPopupWindow.setInputMethodMode(2);
        this.sendPopupWindow.setSoftInputMode(0);
        this.sendPopupWindow.getContentView().setFocusableInTouchMode(true);
        SharedConfig.removeScheduledOrNoSoundHint();
        layout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        this.sendPopupWindow.setFocusable(true);
        int[] location = new int[2];
        view.getLocationInWindow(location);
        this.sendPopupWindow.showAtLocation(view, 51, ((location[0] + view.getMeasuredWidth()) - layout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), (location[1] - layout.getMeasuredHeight()) - AndroidUtilities.dp(2.0f));
        this.sendPopupWindow.dimBehind();
        view.performHapticFeedback(3, 2);
        return false;
    }

    /* renamed from: lambda$onSendLongClick$56$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3401lambda$onSendLongClick$56$orgtelegramuiDialogsActivity(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    /* renamed from: lambda$onSendLongClick$57$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3402lambda$onSendLongClick$57$orgtelegramuiDialogsActivity(View v) {
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
        RecyclerListView list;
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new DialogsActivity$$ExternalSyntheticLambda48(this);
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
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, cellDelegate, "actionBarDefaultIcon"));
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
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "actionBarDefaultSubmenuBackground"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "actionBarDefaultSubmenuItemIcon"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "dialogButtonSelector"));
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
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionPressedBackground"));
        int a = 0;
        while (true) {
            list = null;
            if (a >= 3) {
                break;
            }
            if (a == 2) {
                SearchViewPager searchViewPager2 = this.searchViewPager;
                if (searchViewPager2 == null) {
                    a++;
                } else {
                    list = searchViewPager2.searchListView;
                }
            } else {
                ViewPage[] viewPageArr = this.viewPages;
                if (viewPageArr == null) {
                    a++;
                } else if (a < viewPageArr.length) {
                    list = viewPageArr[a].listView;
                }
            }
            if (list != null) {
                RecyclerListView recyclerListView = list;
                arrayList.add(new ThemeDescription(recyclerListView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
                arrayList.add(new ThemeDescription(recyclerListView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
                arrayList.add(new ThemeDescription(recyclerListView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
                arrayList.add(new ThemeDescription(recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounter"));
                arrayList.add(new ThemeDescription(recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countGrayPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounterMuted"));
                arrayList.add(new ThemeDescription(recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounterText"));
                arrayList.add(new ThemeDescription(list, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_lockDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_secretIcon"));
                arrayList.add(new ThemeDescription(list, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_scamDrawable, Theme.dialogs_fakeDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_draft"));
                arrayList.add(new ThemeDescription(list, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_pinnedDrawable, Theme.dialogs_reorderDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_pinnedIcon"));
                RecyclerListView recyclerListView2 = list;
                arrayList.add(new ThemeDescription((View) recyclerListView2, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (String[]) null, new Paint[]{Theme.dialogs_namePaint[0], Theme.dialogs_namePaint[1], Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_name"));
                arrayList.add(new ThemeDescription((View) recyclerListView2, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (String[]) null, new Paint[]{Theme.dialogs_nameEncryptedPaint[0], Theme.dialogs_nameEncryptedPaint[1], Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_secretName"));
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
                GraySectionCell.createThemeDescriptions(arrayList, list);
                arrayList.add(new ThemeDescription(recyclerListView2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{HashtagSearchCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(recyclerListView2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
                arrayList.add(new ThemeDescription(recyclerListView2, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
                arrayList.add(new ThemeDescription(recyclerListView2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
                arrayList.add(new ThemeDescription(recyclerListView2, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
                arrayList.add(new ThemeDescription((View) recyclerListView2, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
                arrayList.add(new ThemeDescription((View) recyclerListView2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText2"));
            }
            a++;
        }
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate3 = cellDelegate;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate3, "avatar_backgroundRed"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate4 = cellDelegate;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate4, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate3, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate4, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate3, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate4, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate3, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate4, "avatar_backgroundSaved"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate3, "avatar_backgroundArchived"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate4, "avatar_backgroundArchivedHidden"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate3, "chats_nameMessage"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate4, "chats_draft"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate3, "chats_attachMessage"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate4, "chats_nameArchived"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate3, "chats_nameMessageArchived"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate4, "chats_nameMessageArchived_threeLines"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate3, "chats_messageArchived"));
        if (this.viewPages != null) {
            for (int a2 = 0; a2 < this.viewPages.length; a2++) {
                if (this.folderId == 0) {
                    arrayList.add(new ThemeDescription(this.viewPages[a2].listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
                } else {
                    arrayList.add(new ThemeDescription(this.viewPages[a2].listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultArchived"));
                }
                arrayList.add(new ThemeDescription((View) this.viewPages[a2].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DialogsEmptyCell.class}, new String[]{"emptyTextView1"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_nameMessage_threeLines"));
                arrayList.add(new ThemeDescription((View) this.viewPages[a2].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DialogsEmptyCell.class}, new String[]{"emptyTextView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_message"));
                if (SharedConfig.archiveHidden) {
                    arrayList.add(new ThemeDescription((View) this.viewPages[a2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Arrow1", "avatar_backgroundArchivedHidden"));
                    arrayList.add(new ThemeDescription((View) this.viewPages[a2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Arrow2", "avatar_backgroundArchivedHidden"));
                } else {
                    arrayList.add(new ThemeDescription((View) this.viewPages[a2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Arrow1", "avatar_backgroundArchived"));
                    arrayList.add(new ThemeDescription((View) this.viewPages[a2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Arrow2", "avatar_backgroundArchived"));
                }
                arrayList.add(new ThemeDescription((View) this.viewPages[a2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Box2", "avatar_text"));
                arrayList.add(new ThemeDescription((View) this.viewPages[a2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Box1", "avatar_text"));
                arrayList.add(new ThemeDescription((View) this.viewPages[a2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_pinArchiveDrawable}, "Arrow", "chats_archiveIcon"));
                arrayList.add(new ThemeDescription((View) this.viewPages[a2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_pinArchiveDrawable}, "Line", "chats_archiveIcon"));
                arrayList.add(new ThemeDescription((View) this.viewPages[a2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unpinArchiveDrawable}, "Arrow", "chats_archiveIcon"));
                arrayList.add(new ThemeDescription((View) this.viewPages[a2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unpinArchiveDrawable}, "Line", "chats_archiveIcon"));
                arrayList.add(new ThemeDescription((View) this.viewPages[a2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveDrawable}, "Arrow", "chats_archiveBackground"));
                arrayList.add(new ThemeDescription((View) this.viewPages[a2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveDrawable}, "Box2", "chats_archiveIcon"));
                arrayList.add(new ThemeDescription((View) this.viewPages[a2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveDrawable}, "Box1", "chats_archiveIcon"));
                arrayList.add(new ThemeDescription((View) this.viewPages[a2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_hidePsaDrawable}, "Line 1", "chats_archiveBackground"));
                arrayList.add(new ThemeDescription((View) this.viewPages[a2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_hidePsaDrawable}, "Line 2", "chats_archiveBackground"));
                arrayList.add(new ThemeDescription((View) this.viewPages[a2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_hidePsaDrawable}, "Line 3", "chats_archiveBackground"));
                arrayList.add(new ThemeDescription((View) this.viewPages[a2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_hidePsaDrawable}, "Cup Red", "chats_archiveIcon"));
                arrayList.add(new ThemeDescription((View) this.viewPages[a2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_hidePsaDrawable}, "Box", "chats_archiveIcon"));
                arrayList.add(new ThemeDescription((View) this.viewPages[a2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unarchiveDrawable}, "Arrow1", "chats_archiveIcon"));
                arrayList.add(new ThemeDescription((View) this.viewPages[a2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unarchiveDrawable}, "Arrow2", "chats_archivePinBackground"));
                arrayList.add(new ThemeDescription((View) this.viewPages[a2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unarchiveDrawable}, "Box2", "chats_archiveIcon"));
                arrayList.add(new ThemeDescription((View) this.viewPages[a2].listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unarchiveDrawable}, "Box1", "chats_archiveIcon"));
                arrayList.add(new ThemeDescription((View) this.viewPages[a2].listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription((View) this.viewPages[a2].listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, "windowBackgroundWhiteGrayText"));
                arrayList.add(new ThemeDescription((View) this.viewPages[a2].listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, "windowBackgroundWhiteBlueText"));
                arrayList.add(new ThemeDescription((View) this.viewPages[a2].listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
                arrayList.add(new ThemeDescription((View) this.viewPages[a2].listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
                arrayList.add(new ThemeDescription(this.viewPages[a2].progressView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
                ViewPager archiveHintCellPager = this.viewPages[a2].dialogsAdapter.getArchiveHintCellPager();
                arrayList.add(new ThemeDescription((View) archiveHintCellPager, 0, new Class[]{ArchiveHintInnerCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_nameMessage_threeLines"));
                arrayList.add(new ThemeDescription((View) archiveHintCellPager, 0, new Class[]{ArchiveHintInnerCell.class}, new String[]{"imageView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounter"));
                arrayList.add(new ThemeDescription((View) archiveHintCellPager, 0, new Class[]{ArchiveHintInnerCell.class}, new String[]{"headerTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_nameMessage_threeLines"));
                arrayList.add(new ThemeDescription((View) archiveHintCellPager, 0, new Class[]{ArchiveHintInnerCell.class}, new String[]{"messageTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_message"));
                arrayList.add(new ThemeDescription(archiveHintCellPager, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultArchived"));
            }
        }
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, cellDelegate, "chats_archivePullDownBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, cellDelegate, "chats_archivePullDownBackgroundActive"));
        arrayList.add(new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuBackground"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuName"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuPhone"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuPhoneCats"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuCloudBackgroundCats"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_serviceBackground"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuTopShadow"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuTopShadowCats"));
        arrayList.add(new ThemeDescription((View) this.sideMenu, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{DrawerProfileCell.class}, new String[]{"darkThemeView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_menuName"));
        arrayList.add(new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, cellDelegate, "chats_menuTopBackgroundCats"));
        arrayList.add(new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, cellDelegate, "chats_menuTopBackground"));
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
            arrayList.add(new ThemeDescription(this.searchViewPager.dialogsSearchAdapter != null ? this.searchViewPager.dialogsSearchAdapter.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounter"));
            arrayList.add(new ThemeDescription(this.searchViewPager.dialogsSearchAdapter != null ? this.searchViewPager.dialogsSearchAdapter.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countGrayPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounterMuted"));
            arrayList.add(new ThemeDescription(this.searchViewPager.dialogsSearchAdapter != null ? this.searchViewPager.dialogsSearchAdapter.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounterText"));
            arrayList.add(new ThemeDescription(this.searchViewPager.dialogsSearchAdapter != null ? this.searchViewPager.dialogsSearchAdapter.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_archiveTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_archiveText"));
            arrayList.add(new ThemeDescription((View) this.searchViewPager.dialogsSearchAdapter != null ? this.searchViewPager.dialogsSearchAdapter.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            if (this.searchViewPager.dialogsSearchAdapter != null) {
                list = this.searchViewPager.dialogsSearchAdapter.getInnerListView();
            }
            arrayList.add(new ThemeDescription(list, 0, new Class[]{HintDialogCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_onlineCircle"));
        }
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerBackground"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"playButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerPlayPause"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerTitle"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_FASTSCROLL, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerPerformer"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"closeButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerClose"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "returnToCallBackground"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "returnToCallText"));
        for (int a3 = 0; a3 < this.undoView.length; a3++) {
            arrayList.add(new ThemeDescription(this.undoView[a3], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_background"));
            arrayList.add(new ThemeDescription((View) this.undoView[a3], 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[a3], 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[a3], 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[a3], 0, new Class[]{UndoView.class}, new String[]{"subinfoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[a3], 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[a3], 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[a3], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "info1", "undo_background"));
            arrayList.add(new ThemeDescription((View) this.undoView[a3], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "info2", "undo_background"));
            arrayList.add(new ThemeDescription((View) this.undoView[a3], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luCLASSNAME", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[a3], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luCLASSNAME", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[a3], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luCLASSNAME", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[a3], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc9", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[a3], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc8", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[a3], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc7", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[a3], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc6", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[a3], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc5", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[a3], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc4", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[a3], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc3", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[a3], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc2", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[a3], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc1", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[a3], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Oval", "undo_infoColor"));
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
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, cellDelegate, "actionBarTipBackground"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate5 = cellDelegate;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate5, "windowBackgroundWhiteBlackText"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate6 = cellDelegate;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate6, "player_time"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate5, "chat_messagePanelCursor"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate6, "avatar_actionBarIconBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate5, "groupcreate_spanBackground"));
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

    /* renamed from: lambda$getThemeDescriptions$58$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3391lambda$getThemeDescriptions$58$orgtelegramuiDialogsActivity() {
        RecyclerListView recyclerListView;
        RecyclerListView list;
        int b = 0;
        while (b < 3) {
            if (b == 2) {
                SearchViewPager searchViewPager2 = this.searchViewPager;
                if (searchViewPager2 == null) {
                    b++;
                } else {
                    list = searchViewPager2.searchListView;
                }
            } else {
                ViewPage[] viewPageArr = this.viewPages;
                if (viewPageArr == null) {
                    b++;
                } else {
                    list = b < viewPageArr.length ? viewPageArr[b].listView : null;
                }
            }
            if (list != null) {
                int count = list.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = list.getChildAt(a);
                    if (child instanceof ProfileSearchCell) {
                        ((ProfileSearchCell) child).update(0);
                    } else if (child instanceof DialogCell) {
                        ((DialogCell) child).update(0);
                    } else if (child instanceof UserCell) {
                        ((UserCell) child).update(0);
                    }
                }
            }
            b++;
        }
        SearchViewPager searchViewPager3 = this.searchViewPager;
        if (!(searchViewPager3 == null || searchViewPager3.dialogsSearchAdapter == null || (recyclerListView = this.searchViewPager.dialogsSearchAdapter.getInnerListView()) == null)) {
            int count2 = recyclerListView.getChildCount();
            for (int a2 = 0; a2 < count2; a2++) {
                View child2 = recyclerListView.getChildAt(a2);
                if (child2 instanceof HintDialogCell) {
                    ((HintDialogCell) child2).update();
                }
            }
        }
        RecyclerView recyclerView = this.sideMenu;
        if (recyclerView != null) {
            View child3 = recyclerView.getChildAt(0);
            if (child3 instanceof DrawerProfileCell) {
                DrawerProfileCell profileCell = (DrawerProfileCell) child3;
                profileCell.applyBackground(true);
                profileCell.updateColors();
            }
        }
        if (this.viewPages != null) {
            int a3 = 0;
            while (true) {
                ViewPage[] viewPageArr2 = this.viewPages;
                if (a3 >= viewPageArr2.length) {
                    break;
                }
                if (viewPageArr2[a3].pullForegroundDrawable != null) {
                    this.viewPages[a3].pullForegroundDrawable.updateColors();
                }
                a3++;
            }
        }
        if (this.actionBar != null) {
            this.actionBar.setPopupBackgroundColor(Theme.getColor("actionBarDefaultSubmenuBackground"), true);
            this.actionBar.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItem"), false, true);
            this.actionBar.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItemIcon"), true, true);
            this.actionBar.setPopupItemsSelectorColor(Theme.getColor("dialogButtonSelector"), true);
        }
        if (this.scrimPopupWindowItems != null) {
            int a4 = 0;
            while (true) {
                ActionBarMenuSubItem[] actionBarMenuSubItemArr = this.scrimPopupWindowItems;
                if (a4 >= actionBarMenuSubItemArr.length) {
                    break;
                }
                if (actionBarMenuSubItemArr[a4] != null) {
                    actionBarMenuSubItemArr[a4].setColors(Theme.getColor("actionBarDefaultSubmenuItem"), Theme.getColor("actionBarDefaultSubmenuItemIcon"));
                    this.scrimPopupWindowItems[a4].setSelectorColor(Theme.getColor("dialogButtonSelector"));
                }
                a4++;
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
        if (this.blurredView != null && Build.VERSION.SDK_INT >= 23) {
            this.blurredView.setForeground(new ColorDrawable(ColorUtils.setAlphaComponent(getThemedColor("windowBackgroundWhite"), 100)));
        }
        ActionBarMenuItem actionBarMenuItem2 = this.searchItem;
        if (actionBarMenuItem2 != null) {
            EditTextBoldCursor editText = actionBarMenuItem2.getSearchField();
            if (this.whiteActionBar) {
                editText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                editText.setHintTextColor(Theme.getColor("player_time"));
                editText.setCursorColor(Theme.getColor("chat_messagePanelCursor"));
            } else {
                editText.setCursorColor(Theme.getColor("actionBarDefaultSearch"));
                editText.setHintTextColor(Theme.getColor("actionBarDefaultSearchPlaceholder"));
                editText.setTextColor(Theme.getColor("actionBarDefaultSearch"));
            }
            this.searchItem.updateColor();
        }
        updateFloatingButtonColor();
        setSearchAnimationProgress(this.searchAnimationProgress);
    }

    private void updateFloatingButtonColor() {
        if (getParentActivity() != null && this.floatingButtonContainer != null) {
            Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
            if (Build.VERSION.SDK_INT < 21) {
                Drawable shadowDrawable = ContextCompat.getDrawable(getParentActivity(), NUM).mutate();
                shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                drawable = combinedDrawable;
            }
            this.floatingButtonContainer.setBackground(drawable);
        }
    }

    /* access modifiers changed from: protected */
    public Animator getCustomSlideTransition(boolean topFragment, boolean backAnimation2, float distanceToMove) {
        if (backAnimation2) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.slideFragmentProgress, 1.0f});
            this.slideBackTransitionAnimator = ofFloat;
            return ofFloat;
        }
        ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{this.slideFragmentProgress, 1.0f});
        this.slideBackTransitionAnimator = ofFloat2;
        ofFloat2.addUpdateListener(new DialogsActivity$$ExternalSyntheticLambda11(this));
        this.slideBackTransitionAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.slideBackTransitionAnimator.setDuration((long) ((int) (((float) Math.max((int) ((200.0f / ((float) getLayoutContainer().getMeasuredWidth())) * distanceToMove), 80)) * 1.2f)));
        this.slideBackTransitionAnimator.start();
        return this.slideBackTransitionAnimator;
    }

    /* renamed from: lambda$getCustomSlideTransition$59$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m3390x179b64cc(ValueAnimator valueAnimator) {
        setSlideTransitionProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    /* access modifiers changed from: protected */
    public void prepareFragmentToSlide(boolean topFragment, boolean beginSlide) {
        if (topFragment || !beginSlide) {
            this.slideBackTransitionAnimator = null;
            this.isSlideBackTransition = false;
            setFragmentIsSliding(false);
            setSlideTransitionProgress(1.0f);
            return;
        }
        this.isSlideBackTransition = true;
        setFragmentIsSliding(true);
    }

    private void setFragmentIsSliding(boolean sliding) {
        if (SharedConfig.getDevicePerformanceClass() != 0) {
            if (sliding) {
                ViewPage[] viewPageArr = this.viewPages;
                if (!(viewPageArr == null || viewPageArr[0] == null)) {
                    viewPageArr[0].setLayerType(2, (Paint) null);
                    this.viewPages[0].setClipChildren(false);
                    this.viewPages[0].setClipToPadding(false);
                    this.viewPages[0].listView.setClipChildren(false);
                }
                if (this.actionBar != null) {
                    this.actionBar.setLayerType(2, (Paint) null);
                }
                FilterTabsView filterTabsView2 = this.filterTabsView;
                if (filterTabsView2 != null) {
                    filterTabsView2.getListView().setLayerType(2, (Paint) null);
                }
                if (this.fragmentView != null) {
                    ((ViewGroup) this.fragmentView).setClipChildren(false);
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
                ViewPage page = viewPageArr2[i];
                if (page != null) {
                    page.setLayerType(0, (Paint) null);
                    page.setClipChildren(true);
                    page.setClipToPadding(true);
                    page.listView.setClipChildren(true);
                }
                i++;
            }
            if (this.actionBar != null) {
                this.actionBar.setLayerType(0, (Paint) null);
            }
            FilterTabsView filterTabsView3 = this.filterTabsView;
            if (filterTabsView3 != null) {
                filterTabsView3.getListView().setLayerType(0, (Paint) null);
            }
            if (this.fragmentView != null) {
                ((ViewGroup) this.fragmentView).setClipChildren(true);
                this.fragmentView.requestLayout();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onSlideProgress(boolean isOpen, float progress) {
        if (SharedConfig.getDevicePerformanceClass() != 0 && this.isSlideBackTransition && this.slideBackTransitionAnimator == null) {
            setSlideTransitionProgress(progress);
        }
    }

    private void setSlideTransitionProgress(float progress) {
        if (SharedConfig.getDevicePerformanceClass() != 0) {
            this.slideFragmentProgress = progress;
            if (this.fragmentView != null) {
                this.fragmentView.invalidate();
            }
            FilterTabsView filterTabsView2 = this.filterTabsView;
            if (filterTabsView2 != null) {
                float s = 1.0f - ((1.0f - this.slideFragmentProgress) * 0.05f);
                filterTabsView2.getListView().setScaleX(s);
                this.filterTabsView.getListView().setScaleY(s);
                this.filterTabsView.getListView().setTranslationX(((float) (this.isDrawerTransition ? AndroidUtilities.dp(4.0f) : -AndroidUtilities.dp(4.0f))) * (1.0f - this.slideFragmentProgress));
                this.filterTabsView.getListView().setPivotX(this.isDrawerTransition ? (float) this.filterTabsView.getMeasuredWidth() : 0.0f);
                this.filterTabsView.getListView().setPivotY(0.0f);
                this.filterTabsView.invalidate();
            }
        }
    }

    public void setProgressToDrawerOpened(float progress) {
        if (SharedConfig.getDevicePerformanceClass() != 0 && !this.isSlideBackTransition) {
            boolean drawerTransition = progress > 0.0f;
            if (this.searchIsShowed) {
                drawerTransition = false;
                progress = 0.0f;
            }
            if (drawerTransition != this.isDrawerTransition) {
                this.isDrawerTransition = drawerTransition;
                if (drawerTransition) {
                    setFragmentIsSliding(true);
                } else {
                    setFragmentIsSliding(false);
                }
                if (this.fragmentView != null) {
                    this.fragmentView.requestLayout();
                }
            }
            setSlideTransitionProgress(1.0f - progress);
        }
    }

    public void setShowSearch(String query, int i) {
        if (!this.searching) {
            this.initialSearchType = i;
            this.actionBar.openSearchField(query, false);
            return;
        }
        if (!this.searchItem.getSearchField().getText().toString().equals(query)) {
            this.searchItem.getSearchField().setText(query);
        }
        int p = this.searchViewPager.getPositionForType(i);
        if (p >= 0 && this.searchViewPager.getTabsView().getCurrentTabId() != p) {
            this.searchViewPager.getTabsView().scrollToTab(p, p);
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
