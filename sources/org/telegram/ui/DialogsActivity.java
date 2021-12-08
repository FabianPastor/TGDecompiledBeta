package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Property;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
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
import org.telegram.messenger.FilesMigrationService;
import org.telegram.messenger.ImageLoader;
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
import org.telegram.ui.Components.JoinGroupAlert;
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
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.ViewPagerFixed;
import org.telegram.ui.GroupCreateFinalActivity;

public class DialogsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int ARCHIVE_ITEM_STATE_HIDDEN = 2;
    private static final int ARCHIVE_ITEM_STATE_PINNED = 0;
    private static final int ARCHIVE_ITEM_STATE_SHOWED = 1;
    private static final int add_to_folder = 109;
    private static final int archive = 105;
    private static final int archive2 = 107;
    private static final int block = 106;
    private static final int clear = 103;
    private static final int delete = 102;
    public static boolean[] dialogsLoaded = new boolean[3];
    /* access modifiers changed from: private */
    public static final Interpolator interpolator = DialogsActivity$$ExternalSyntheticLambda13.INSTANCE;
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
    public ArrayList<TLRPC.Dialog> frozenDialogsList;
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
    private int otherwiseReloginDays;
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
    private boolean showSetPasswordConfirm;
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

        static /* synthetic */ int access$10608(ViewPage x0) {
            int i = x0.lastItemsCount;
            x0.lastItemsCount = i + 1;
            return i;
        }

        static /* synthetic */ int access$10610(ViewPage x0) {
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
                canvas.drawRect(0.0f, (float) top, (float) getMeasuredWidth(), (float) (top + actionBarHeight), DialogsActivity.this.searchAnimationProgress == 1.0f ? this.actionBarSearchPaint : DialogsActivity.this.actionBarDefaultPaint);
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
                        canvas2.drawCircle(cX, ((float) statusBarH) + (((float) (DialogsActivity.this.actionBar.getMeasuredHeight() - statusBarH)) / 2.0f), ((float) getMeasuredWidth()) * 1.3f * DialogsActivity.this.searchAnimationProgress, this.actionBarSearchPaint);
                        canvas.restore();
                    } else {
                        canvas.drawRect(0.0f, (float) top, (float) getMeasuredWidth(), (float) (top + actionBarHeight), this.actionBarSearchPaint);
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
                    canvas.drawRect(0.0f, (float) top, (float) getMeasuredWidth(), (float) (top + actionBarHeight), this.actionBarSearchPaint);
                } else {
                    canvas.drawRect(0.0f, (float) top, (float) getMeasuredWidth(), (float) (top + actionBarHeight), DialogsActivity.this.actionBarDefaultPaint);
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
            int i2 = 0;
            while (i2 < count2) {
                View child = getChildAt(i2);
                if (child.getVisibility() == 8) {
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
                    DialogsActivity.this.filterTabsView.selectTabWithId(DialogsActivity.this.viewPages[1].selectedType, ((float) Math.abs(dx2)) / ((float) DialogsActivity.this.viewPages[0].getMeasuredWidth()));
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
                    if (DialogsActivity.this.additionalOffset == 0.0f) {
                        boolean unused14 = DialogsActivity.this.backAnimation = Math.abs(x) < ((float) DialogsActivity.this.viewPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(velX) < 3500.0f || Math.abs(velX) < Math.abs(velY));
                    } else if (Math.abs(velX) > 1500.0f) {
                        DialogsActivity dialogsActivity5 = DialogsActivity.this;
                        boolean unused15 = dialogsActivity5.backAnimation = !dialogsActivity5.animatingForward ? velX < 0.0f : velX > 0.0f;
                    } else if (DialogsActivity.this.animatingForward) {
                        DialogsActivity dialogsActivity6 = DialogsActivity.this;
                        boolean unused16 = dialogsActivity6.backAnimation = dialogsActivity6.viewPages[1].getX() > ((float) (DialogsActivity.this.viewPages[0].getMeasuredWidth() >> 1));
                    } else {
                        DialogsActivity dialogsActivity7 = DialogsActivity.this;
                        boolean unused17 = dialogsActivity7.backAnimation = dialogsActivity7.viewPages[0].getX() < ((float) (DialogsActivity.this.viewPages[0].getMeasuredWidth() >> 1));
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

        public boolean hasOverlappingRendering() {
            return false;
        }
    }

    public class DialogsRecyclerView extends RecyclerListView {
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
                PullForegroundDrawable access$10000 = this.parentPage.pullForegroundDrawable;
                if (this.parentPage.archivePullViewState != 0) {
                    z = true;
                }
                access$10000.setWillDraw(z);
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
        public /* synthetic */ void m2886xa454faae(ValueAnimator animation) {
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
        public /* synthetic */ void m2888x3var_d65(TLRPC.Dialog dialog, int count, int position) {
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
                    ViewPage.access$10610(this.parentPage);
                    this.parentPage.dialogsAdapter.notifyItemRemoved(i);
                    int unused2 = DialogsActivity.this.dialogRemoveFinished = 2;
                    return;
                }
                int added = DialogsActivity.this.getMessagesController().addDialogToFolder(dialog2.id, DialogsActivity.this.folderId == 0 ? 1 : 0, -1, 0);
                if (!(added == 2 && i == 0)) {
                    this.parentPage.dialogsItemAnimator.prepareForRemove();
                    ViewPage.access$10610(this.parentPage);
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
                            ViewPage.access$10608(this.parentPage);
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
        public /* synthetic */ void m2887x3fb9d364(TLRPC.Dialog dialog, int pinnedNum) {
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
                    ViewPage.access$10608(this.parentPage);
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
                    ViewPage.access$10610(this.parentPage);
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
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.appUpdateAvailable);
        }
        getNotificationCenter().addObserver(this, NotificationCenter.messagesDeleted);
        getNotificationCenter().addObserver(this, NotificationCenter.onDatabaseMigration);
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
        getNotificationCenter().removeObserver(this, NotificationCenter.onDatabaseMigration);
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v10, resolved type: org.telegram.ui.Components.CombinedDrawable} */
    /* JADX WARNING: type inference failed for: r12v1, types: [int, boolean] */
    /* JADX WARNING: type inference failed for: r12v9 */
    /* JADX WARNING: type inference failed for: r12v10 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r35) {
        /*
            r34 = this;
            r10 = r34
            r11 = r35
            r12 = 0
            r10.searching = r12
            r10.searchWas = r12
            r13 = 0
            r10.pacmanAnimation = r13
            java.util.ArrayList<java.lang.Long> r0 = r10.selectedDialogs
            r0.clear()
            android.view.ViewConfiguration r0 = android.view.ViewConfiguration.get(r35)
            int r0 = r0.getScaledMaximumFlingVelocity()
            r10.maximumVelocity = r0
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda14 r0 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda14
            r0.<init>(r11)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r14 = r0.createMenu()
            boolean r0 = r10.onlySelect
            r15 = 2131625321(0x7f0e0569, float:1.8877847E38)
            java.lang.String r9 = "Done"
            r8 = 0
            r7 = 2
            r6 = 8
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
            r1 = r35
            r13 = r4
            r4 = r16
            r12 = 1
            r5 = r17
            r0.<init>((android.content.Context) r1, (org.telegram.ui.ActionBar.ActionBarMenu) r2, (int) r3, (int) r4, (boolean) r5)
            r10.doneItem = r13
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r9, r15)
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
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda8 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda8
            r1.<init>(r10)
            r0.setOnClickListener(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r10.doneItem
            r0.setAlpha(r8)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r10.doneItem
            r0.setVisibility(r6)
            org.telegram.ui.Components.ProxyDrawable r0 = new org.telegram.ui.Components.ProxyDrawable
            r0.<init>(r11)
            r10.proxyDrawable = r0
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r14.addItem((int) r7, (android.graphics.drawable.Drawable) r0)
            r10.proxyItem = r0
            r1 = 2131627373(0x7f0e0d6d, float:1.8882009E38)
            java.lang.String r2 = "ProxySettings"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setContentDescription(r1)
            org.telegram.ui.Components.RLottieDrawable r0 = new org.telegram.ui.Components.RLottieDrawable
            r19 = 2131558473(0x7f0d0049, float:1.8742263E38)
            r1 = 1105199104(0x41e00000, float:28.0)
            int r21 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r22 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r23 = 1
            r24 = 0
            java.lang.String r20 = "passcode_lock_close"
            r18 = r0
            r18.<init>(r19, r20, r21, r22, r23, r24)
            r10.passcodeDrawable = r0
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r14.addItem((int) r12, (android.graphics.drawable.Drawable) r0)
            r10.passcodeItem = r0
            r1 = 2131624006(0x7f0e0046, float:1.887518E38)
            java.lang.String r2 = "AccDescrPasscodeLock"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setContentDescription(r1)
            r34.updatePasscodeButton()
            r0 = 0
            r10.updateProxyButton(r0)
            goto L_0x00e7
        L_0x00e5:
            r0 = 0
            r12 = 1
        L_0x00e7:
            r1 = 2131165495(0x7var_, float:1.7945209E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r14.addItem((int) r0, (int) r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r1.setIsSearchField(r12, r12)
            org.telegram.ui.DialogsActivity$3 r1 = new org.telegram.ui.DialogsActivity$3
            r1.<init>()
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.setActionBarMenuItemSearchListener(r1)
            r10.searchItem = r0
            java.lang.String r1 = "Search"
            r2 = 2131627616(0x7f0e0e60, float:1.8882501E38)
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
            r1 = 2131165485(0x7var_d, float:1.7945188E38)
            r0.setBackButtonImage(r1)
            int r0 = r10.initialDialogsType
            if (r0 != r4) goto L_0x013a
            java.lang.String r1 = r10.selectAlertString
            if (r1 != 0) goto L_0x013a
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r1 = 2131625711(0x7f0e06ef, float:1.8878638E38)
            java.lang.String r2 = "ForwardTo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x0159
        L_0x013a:
            if (r0 != r5) goto L_0x014b
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r1 = 2131627682(0x7f0e0ea2, float:1.8882635E38)
            java.lang.String r2 = "SelectChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x0159
        L_0x014b:
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r1 = 2131627681(0x7f0e0ea1, float:1.8882633E38)
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
            r1 = 2131624333(0x7f0e018d, float:1.8875843E38)
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
            r1 = 2131624300(0x7f0e016c, float:1.8875776E38)
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
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda15 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda15
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
            r0.setVisibility(r6)
            r0 = 0
            r10.canShowFilterTabsView = r0
            org.telegram.ui.Components.FilterTabsView r0 = r10.filterTabsView
            org.telegram.ui.DialogsActivity$6 r1 = new org.telegram.ui.DialogsActivity$6
            r1.<init>()
            r0.setDelegate(r1)
        L_0x021a:
            boolean r0 = r10.allowSwitchAccount
            r17 = 1113587712(0x42600000, float:56.0)
            if (r0 == 0) goto L_0x02bb
            int r0 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()
            if (r0 <= r12) goto L_0x02bb
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r1 = 0
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r14.addItemWithWidth(r12, r1, r0)
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
            r5 = 36
            r15 = 17
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r3, (int) r5, (int) r15)
            r2.addView(r1, r3)
            org.telegram.messenger.UserConfig r2 = r34.getUserConfig()
            org.telegram.tgnet.TLRPC$User r2 = r2.getCurrentUser()
            r0.setInfo((org.telegram.tgnet.TLRPC.User) r2)
            org.telegram.messenger.ImageReceiver r3 = r1.getImageReceiver()
            int r5 = r10.currentAccount
            r3.setCurrentAccount(r5)
            org.telegram.messenger.ImageLocation r19 = org.telegram.messenger.ImageLocation.getForUserOrChat(r2, r12)
            org.telegram.messenger.ImageLocation r21 = org.telegram.messenger.ImageLocation.getForUserOrChat(r2, r7)
            java.lang.String r20 = "50_50"
            java.lang.String r22 = "50_50"
            r18 = r1
            r23 = r0
            r24 = r2
            r18.setImage((org.telegram.messenger.ImageLocation) r19, (java.lang.String) r20, (org.telegram.messenger.ImageLocation) r21, (java.lang.String) r22, (android.graphics.drawable.Drawable) r23, (java.lang.Object) r24)
            r3 = 0
        L_0x0286:
            if (r3 >= r4) goto L_0x02bb
            org.telegram.messenger.AccountInstance r5 = org.telegram.messenger.AccountInstance.getInstance(r3)
            org.telegram.messenger.UserConfig r5 = r5.getUserConfig()
            org.telegram.tgnet.TLRPC$User r5 = r5.getCurrentUser()
            if (r5 == 0) goto L_0x02b3
            org.telegram.ui.Cells.AccountSelectCell r15 = new org.telegram.ui.Cells.AccountSelectCell
            r4 = 0
            r15.<init>(r11, r4)
            r4 = r15
            r4.setAccount(r3, r12)
            org.telegram.ui.ActionBar.ActionBarMenuItem r15 = r10.switchItem
            int r7 = r3 + 10
            r20 = 1130758144(0x43660000, float:230.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r20 = 1111490560(0x42400000, float:48.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r15.addSubItem((int) r7, (android.view.View) r4, (int) r8, (int) r6)
        L_0x02b3:
            int r3 = r3 + 1
            r4 = 3
            r6 = 8
            r7 = 2
            r8 = 0
            goto L_0x0286
        L_0x02bb:
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r0.setAllowOverlayTitle(r12)
            androidx.recyclerview.widget.RecyclerView r0 = r10.sideMenu
            if (r0 == 0) goto L_0x02e1
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
        L_0x02e1:
            r0 = 0
            r10.createActionMode(r0)
            org.telegram.ui.DialogsActivity$ContentView r0 = new org.telegram.ui.DialogsActivity$ContentView
            r0.<init>(r11)
            r15 = r0
            r10.fragmentView = r15
            int r0 = r10.folderId
            if (r0 != 0) goto L_0x02fb
            int r0 = r10.initialDialogsType
            if (r0 != 0) goto L_0x02fb
            boolean r0 = r10.onlySelect
            if (r0 != 0) goto L_0x02fb
            r5 = 2
            goto L_0x02fc
        L_0x02fb:
            r5 = 1
        L_0x02fc:
            r8 = r5
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = new org.telegram.ui.DialogsActivity.ViewPage[r8]
            r10.viewPages = r0
            r0 = 0
            r7 = r0
        L_0x0303:
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
            r5 = -2
            r4 = -1
            if (r7 >= r8) goto L_0x050c
            org.telegram.ui.DialogsActivity$7 r0 = new org.telegram.ui.DialogsActivity$7
            r0.<init>(r11)
            r3 = r0
            android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r6)
            r15.addView(r3, r0)
            int r0 = r10.initialDialogsType
            int unused = r3.dialogsType = r0
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r10.viewPages
            r0[r7] = r3
            org.telegram.ui.Components.FlickerLoadingView r0 = new org.telegram.ui.Components.FlickerLoadingView
            r0.<init>(r11)
            org.telegram.ui.Components.FlickerLoadingView unused = r3.progressView = r0
            org.telegram.ui.Components.FlickerLoadingView r0 = r3.progressView
            r1 = 7
            r0.setViewType(r1)
            org.telegram.ui.Components.FlickerLoadingView r0 = r3.progressView
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.Components.FlickerLoadingView r0 = r3.progressView
            r1 = 17
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r5, (int) r5, (int) r1)
            r3.addView(r0, r1)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = new org.telegram.ui.DialogsActivity$DialogsRecyclerView
            r0.<init>(r11, r3)
            org.telegram.ui.DialogsActivity.DialogsRecyclerView unused = r3.listView = r0
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r3.listView
            r1 = 0
            r0.setAnimateEmptyView(r12, r1)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r3.listView
            r0.setClipToPadding(r1)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r3.listView
            r5 = 0
            r0.setPivotY(r5)
            org.telegram.ui.DialogsActivity$8 r0 = new org.telegram.ui.DialogsActivity$8
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r1 = r3.listView
            r0.<init>(r1, r3)
            org.telegram.ui.Components.DialogsItemAnimator unused = r3.dialogsItemAnimator = r0
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r3.listView
            org.telegram.ui.Components.DialogsItemAnimator r1 = r3.dialogsItemAnimator
            r0.setItemAnimator(r1)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r3.listView
            r0.setVerticalScrollBarEnabled(r12)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r3.listView
            r0.setInstantClick(r12)
            org.telegram.ui.DialogsActivity$9 r0 = new org.telegram.ui.DialogsActivity$9
            r0.<init>(r11, r3)
            androidx.recyclerview.widget.LinearLayoutManager unused = r3.layoutManager = r0
            androidx.recyclerview.widget.LinearLayoutManager r0 = r3.layoutManager
            r0.setOrientation(r12)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r3.listView
            androidx.recyclerview.widget.LinearLayoutManager r1 = r3.layoutManager
            r0.setLayoutManager(r1)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r3.listView
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x03ad
            r1 = 1
            goto L_0x03ae
        L_0x03ad:
            r1 = 2
        L_0x03ae:
            r0.setVerticalScrollbarPosition(r1)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r3.listView
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r6)
            r3.addView(r0, r1)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r3.listView
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda37 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda37
            r1.<init>(r10, r3)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r3.listView
            org.telegram.ui.DialogsActivity$11 r1 = new org.telegram.ui.DialogsActivity$11
            r1.<init>(r3)
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended) r1)
            org.telegram.ui.DialogsActivity$SwipeController r0 = new org.telegram.ui.DialogsActivity$SwipeController
            r0.<init>(r3)
            org.telegram.ui.DialogsActivity.SwipeController unused = r3.swipeController = r0
            org.telegram.ui.Components.RecyclerItemsEnterAnimator r0 = new org.telegram.ui.Components.RecyclerItemsEnterAnimator
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r1 = r3.listView
            r4 = 0
            r0.<init>(r1, r4)
            org.telegram.ui.Components.RecyclerItemsEnterAnimator unused = r3.recyclerItemsEnterAnimator = r0
            androidx.recyclerview.widget.ItemTouchHelper r0 = new androidx.recyclerview.widget.ItemTouchHelper
            org.telegram.ui.DialogsActivity$SwipeController r1 = r3.swipeController
            r0.<init>(r1)
            androidx.recyclerview.widget.ItemTouchHelper unused = r3.itemTouchhelper = r0
            androidx.recyclerview.widget.ItemTouchHelper r0 = r3.itemTouchhelper
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r1 = r3.listView
            r0.attachToRecyclerView(r1)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r3.listView
            org.telegram.ui.DialogsActivity$12 r1 = new org.telegram.ui.DialogsActivity$12
            r1.<init>(r3)
            r0.setOnScrollListener(r1)
            boolean r0 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r0 == 0) goto L_0x0412
            r0 = 2
            goto L_0x0413
        L_0x0412:
            r0 = 0
        L_0x0413:
            int unused = r3.archivePullViewState = r0
            org.telegram.ui.Components.PullForegroundDrawable r0 = r3.pullForegroundDrawable
            if (r0 != 0) goto L_0x045f
            int r0 = r10.folderId
            if (r0 != 0) goto L_0x045f
            org.telegram.ui.DialogsActivity$13 r0 = new org.telegram.ui.DialogsActivity$13
            r1 = 2131624076(0x7f0e008c, float:1.8875321E38)
            java.lang.String r4 = "AccSwipeForArchive"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r4 = 2131624075(0x7f0e008b, float:1.887532E38)
            java.lang.String r6 = "AccReleaseForArchive"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r0.<init>(r1, r4, r3)
            org.telegram.ui.Components.PullForegroundDrawable unused = r3.pullForegroundDrawable = r0
            boolean r0 = r34.hasHiddenArchive()
            if (r0 == 0) goto L_0x0448
            org.telegram.ui.Components.PullForegroundDrawable r0 = r3.pullForegroundDrawable
            r0.showHidden()
            goto L_0x044f
        L_0x0448:
            org.telegram.ui.Components.PullForegroundDrawable r0 = r3.pullForegroundDrawable
            r0.doNotShow()
        L_0x044f:
            org.telegram.ui.Components.PullForegroundDrawable r0 = r3.pullForegroundDrawable
            int r1 = r3.archivePullViewState
            if (r1 == 0) goto L_0x045b
            r1 = 1
            goto L_0x045c
        L_0x045b:
            r1 = 0
        L_0x045c:
            r0.setWillDraw(r1)
        L_0x045f:
            org.telegram.ui.DialogsActivity$14 r6 = new org.telegram.ui.DialogsActivity$14
            int r4 = r3.dialogsType
            int r1 = r10.folderId
            boolean r0 = r10.onlySelect
            java.util.ArrayList<java.lang.Long> r12 = r10.selectedDialogs
            r21 = r8
            int r8 = r10.currentAccount
            r22 = r0
            r0 = r6
            r23 = r1
            r1 = r34
            r24 = 8
            r2 = r34
            r25 = r3
            r3 = r35
            r26 = r14
            r14 = 3
            r14 = 10
            r18 = 0
            r5 = r23
            r14 = r6
            r6 = r22
            r19 = r7
            r22 = r13
            r13 = 2
            r7 = r12
            r18 = r21
            r12 = 0
            r12 = r9
            r9 = r25
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9)
            r0 = r25
            org.telegram.ui.Adapters.DialogsAdapter unused = r0.dialogsAdapter = r14
            org.telegram.ui.Adapters.DialogsAdapter r1 = r0.dialogsAdapter
            boolean r2 = r10.afterSignup
            r1.setForceShowEmptyCell(r2)
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 == 0) goto L_0x04be
            long r1 = r10.openedDialogId
            r3 = 0
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x04be
            org.telegram.ui.Adapters.DialogsAdapter r1 = r0.dialogsAdapter
            long r2 = r10.openedDialogId
            r1.setOpenedDialogId(r2)
        L_0x04be:
            org.telegram.ui.Adapters.DialogsAdapter r1 = r0.dialogsAdapter
            org.telegram.ui.Components.PullForegroundDrawable r2 = r0.pullForegroundDrawable
            r1.setArchivedPullDrawable(r2)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r1 = r0.listView
            org.telegram.ui.Adapters.DialogsAdapter r2 = r0.dialogsAdapter
            r1.setAdapter(r2)
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r1 = r0.listView
            int r2 = r10.folderId
            if (r2 != 0) goto L_0x04e1
            org.telegram.ui.Components.FlickerLoadingView r2 = r0.progressView
            goto L_0x04e2
        L_0x04e1:
            r2 = 0
        L_0x04e2:
            r1.setEmptyView(r2)
            org.telegram.ui.Components.RecyclerAnimationScrollHelper r1 = new org.telegram.ui.Components.RecyclerAnimationScrollHelper
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r2 = r0.listView
            androidx.recyclerview.widget.LinearLayoutManager r3 = r0.layoutManager
            r1.<init>(r2, r3)
            org.telegram.ui.Components.RecyclerAnimationScrollHelper unused = r0.scrollHelper = r1
            if (r19 == 0) goto L_0x0500
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r10.viewPages
            r1 = r1[r19]
            r2 = 8
            r1.setVisibility(r2)
        L_0x0500:
            int r7 = r19 + 1
            r9 = r12
            r8 = r18
            r13 = r22
            r14 = r26
            r12 = 1
            goto L_0x0303
        L_0x050c:
            r19 = r7
            r18 = r8
            r12 = r9
            r22 = r13
            r26 = r14
            r13 = 2
            r0 = 0
            java.lang.String r1 = r10.searchString
            if (r1 == 0) goto L_0x051e
            r0 = 2
            r7 = r0
            goto L_0x0526
        L_0x051e:
            boolean r1 = r10.onlySelect
            if (r1 != 0) goto L_0x0525
            r0 = 1
            r7 = r0
            goto L_0x0526
        L_0x0525:
            r7 = r0
        L_0x0526:
            org.telegram.ui.Components.SearchViewPager r8 = new org.telegram.ui.Components.SearchViewPager
            int r9 = r10.initialDialogsType
            int r14 = r10.folderId
            org.telegram.ui.DialogsActivity$15 r3 = new org.telegram.ui.DialogsActivity$15
            r3.<init>()
            r0 = r8
            r1 = r35
            r2 = r34
            r19 = r3
            r3 = r7
            r13 = -1
            r4 = r9
            r9 = -2
            r5 = r14
            r14 = -1082130432(0xffffffffbvar_, float:-1.0)
            r6 = r19
            r0.<init>(r1, r2, r3, r4, r5, r6)
            r10.searchViewPager = r8
            r15.addView(r8)
            org.telegram.ui.Components.SearchViewPager r0 = r10.searchViewPager
            org.telegram.ui.Adapters.DialogsSearchAdapter r0 = r0.dialogsSearchAdapter
            org.telegram.ui.DialogsActivity$16 r1 = new org.telegram.ui.DialogsActivity$16
            r1.<init>()
            r0.setDelegate(r1)
            org.telegram.ui.Components.SearchViewPager r0 = r10.searchViewPager
            org.telegram.ui.Components.RecyclerListView r0 = r0.searchListView
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda35 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda35
            r1.<init>(r10)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            org.telegram.ui.Components.SearchViewPager r0 = r10.searchViewPager
            org.telegram.ui.Components.RecyclerListView r0 = r0.searchListView
            org.telegram.ui.DialogsActivity$17 r1 = new org.telegram.ui.DialogsActivity$17
            r1.<init>()
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended) r1)
            org.telegram.ui.Components.SearchViewPager r0 = r10.searchViewPager
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda38 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda38
            r1.<init>(r10)
            r0.setFilteredSearchViewDelegate(r1)
            org.telegram.ui.Components.SearchViewPager r0 = r10.searchViewPager
            r1 = 8
            r0.setVisibility(r1)
            org.telegram.ui.Adapters.FiltersView r0 = new org.telegram.ui.Adapters.FiltersView
            android.app.Activity r1 = r34.getParentActivity()
            r2 = 0
            r0.<init>(r1, r2)
            r10.filtersView = r0
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda36 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda36
            r1.<init>(r10)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            org.telegram.ui.Adapters.FiltersView r0 = r10.filtersView
            r1 = 48
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r13, (int) r9, (int) r1)
            r15.addView(r0, r1)
            org.telegram.ui.Adapters.FiltersView r0 = r10.filtersView
            r1 = 8
            r0.setVisibility(r1)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r11)
            r10.floatingButtonContainer = r0
            boolean r1 = r10.onlySelect
            if (r1 == 0) goto L_0x05b5
            int r1 = r10.initialDialogsType
            r2 = 10
            if (r1 != r2) goto L_0x05b9
        L_0x05b5:
            int r1 = r10.folderId
            if (r1 == 0) goto L_0x05bc
        L_0x05b9:
            r6 = 8
            goto L_0x05bd
        L_0x05bc:
            r6 = 0
        L_0x05bd:
            r0.setVisibility(r6)
            android.widget.FrameLayout r0 = r10.floatingButtonContainer
            int r1 = android.os.Build.VERSION.SDK_INT
            r6 = 21
            if (r1 < r6) goto L_0x05cb
            r1 = 56
            goto L_0x05cd
        L_0x05cb:
            r1 = 60
        L_0x05cd:
            int r27 = r1 + 20
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r6) goto L_0x05d6
            r1 = 56
            goto L_0x05d8
        L_0x05d6:
            r1 = 60
        L_0x05d8:
            int r1 = r1 + 20
            float r1 = (float) r1
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x05e1
            r4 = 3
            goto L_0x05e2
        L_0x05e1:
            r4 = 5
        L_0x05e2:
            r29 = r4 | 80
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            r8 = 1082130432(0x40800000, float:4.0)
            if (r4 == 0) goto L_0x05ed
            r30 = 1082130432(0x40800000, float:4.0)
            goto L_0x05ef
        L_0x05ed:
            r30 = 0
        L_0x05ef:
            r31 = 0
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x05f8
            r32 = 0
            goto L_0x05fa
        L_0x05f8:
            r32 = 1082130432(0x40800000, float:4.0)
        L_0x05fa:
            r33 = 0
            r28 = r1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r15.addView(r0, r1)
            android.widget.FrameLayout r0 = r10.floatingButtonContainer
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda9 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda9
            r1.<init>(r10)
            r0.setOnClickListener(r1)
            org.telegram.ui.Components.RLottieImageView r0 = new org.telegram.ui.Components.RLottieImageView
            r0.<init>(r11)
            r10.floatingButton = r0
            android.widget.ImageView$ScaleType r1 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r1)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            java.lang.String r1 = "chats_actionBackground"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            java.lang.String r4 = "chats_actionPressedBackground"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r0, r1, r4)
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 >= r6) goto L_0x0662
            android.content.res.Resources r1 = r35.getResources()
            r4 = 2131165435(0x7var_fb, float:1.7945087E38)
            android.graphics.drawable.Drawable r1 = r1.getDrawable(r4)
            android.graphics.drawable.Drawable r1 = r1.mutate()
            android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
            r5 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r2 = android.graphics.PorterDuff.Mode.MULTIPLY
            r4.<init>(r5, r2)
            r1.setColorFilter(r4)
            org.telegram.ui.Components.CombinedDrawable r2 = new org.telegram.ui.Components.CombinedDrawable
            r4 = 0
            r2.<init>(r1, r0, r4, r4)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r2.setIconSize(r4, r5)
            r0 = r2
            r5 = r0
            goto L_0x0663
        L_0x0662:
            r5 = r0
        L_0x0663:
            org.telegram.ui.Components.RLottieImageView r0 = r10.floatingButton
            r0.setBackgroundDrawable(r5)
            org.telegram.ui.Components.RLottieImageView r0 = r10.floatingButton
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            java.lang.String r2 = "chats_actionIcon"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            android.graphics.PorterDuff$Mode r4 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r2, r4)
            r0.setColorFilter(r1)
            int r0 = r10.initialDialogsType
            r1 = 10
            if (r0 != r1) goto L_0x0695
            org.telegram.ui.Components.RLottieImageView r0 = r10.floatingButton
            r1 = 2131165432(0x7var_f8, float:1.794508E38)
            r0.setImageResource(r1)
            android.widget.FrameLayout r0 = r10.floatingButtonContainer
            r1 = 2131625321(0x7f0e0569, float:1.8877847E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r12, r1)
            r0.setContentDescription(r1)
            goto L_0x06af
        L_0x0695:
            org.telegram.ui.Components.RLottieImageView r0 = r10.floatingButton
            r1 = 2131558555(0x7f0d009b, float:1.874243E38)
            r2 = 52
            r4 = 52
            r0.setAnimation(r1, r2, r4)
            android.widget.FrameLayout r0 = r10.floatingButtonContainer
            r1 = 2131626472(0x7f0e09e8, float:1.8880181E38)
            java.lang.String r2 = "NewMessageTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setContentDescription(r1)
        L_0x06af:
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r6) goto L_0x071d
            android.animation.StateListAnimator r0 = new android.animation.StateListAnimator
            r0.<init>()
            r1 = 1
            int[] r2 = new int[r1]
            r1 = 16842919(0x10100a7, float:2.3694026E-38)
            r4 = 0
            r2[r4] = r1
            org.telegram.ui.Components.RLottieImageView r1 = r10.floatingButton
            android.util.Property r12 = android.view.View.TRANSLATION_Z
            r3 = 2
            float[] r14 = new float[r3]
            r3 = 1073741824(0x40000000, float:2.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            r14[r4] = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r3 = (float) r3
            r16 = 1
            r14[r16] = r3
            android.animation.ObjectAnimator r1 = android.animation.ObjectAnimator.ofFloat(r1, r12, r14)
            r13 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r1 = r1.setDuration(r13)
            r0.addState(r2, r1)
            int[] r1 = new int[r4]
            org.telegram.ui.Components.RLottieImageView r2 = r10.floatingButton
            android.util.Property r3 = android.view.View.TRANSLATION_Z
            r13 = 2
            float[] r14 = new float[r13]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r8 = (float) r8
            r14[r4] = r8
            r4 = 1073741824(0x40000000, float:2.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r8 = 1
            r14[r8] = r4
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r3, r14)
            r3 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r2 = r2.setDuration(r3)
            r0.addState(r1, r2)
            org.telegram.ui.Components.RLottieImageView r1 = r10.floatingButton
            r1.setStateListAnimator(r0)
            org.telegram.ui.Components.RLottieImageView r1 = r10.floatingButton
            org.telegram.ui.DialogsActivity$18 r2 = new org.telegram.ui.DialogsActivity$18
            r2.<init>()
            r1.setOutlineProvider(r2)
        L_0x071d:
            android.widget.FrameLayout r0 = r10.floatingButtonContainer
            org.telegram.ui.Components.RLottieImageView r1 = r10.floatingButton
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 < r6) goto L_0x0728
            r27 = 56
            goto L_0x072a
        L_0x0728:
            r27 = 60
        L_0x072a:
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 < r6) goto L_0x0731
            r2 = 56
            goto L_0x0733
        L_0x0731:
            r2 = 60
        L_0x0733:
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
            if (r0 != 0) goto L_0x0798
            int r0 = r10.initialDialogsType
            if (r0 != 0) goto L_0x0798
            org.telegram.ui.Components.FragmentContextView r0 = new org.telegram.ui.Components.FragmentContextView
            r1 = 1
            r0.<init>(r11, r10, r1)
            r10.fragmentLocationContextView = r0
            r27 = -1
            r28 = 1108869120(0x42180000, float:38.0)
            r29 = 51
            r30 = 0
            r31 = -1039138816(0xffffffffCLASSNAME, float:-36.0)
            r32 = 0
            r33 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r0.setLayoutParams(r1)
            org.telegram.ui.Components.FragmentContextView r0 = r10.fragmentLocationContextView
            r15.addView(r0)
            org.telegram.ui.DialogsActivity$19 r0 = new org.telegram.ui.DialogsActivity$19
            r1 = 0
            r0.<init>(r11, r10, r1)
            r10.fragmentContextView = r0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r0.setLayoutParams(r1)
            org.telegram.ui.Components.FragmentContextView r0 = r10.fragmentContextView
            r15.addView(r0)
            org.telegram.ui.Components.FragmentContextView r0 = r10.fragmentContextView
            org.telegram.ui.Components.FragmentContextView r1 = r10.fragmentLocationContextView
            r0.setAdditionalContextView(r1)
            org.telegram.ui.Components.FragmentContextView r0 = r10.fragmentLocationContextView
            org.telegram.ui.Components.FragmentContextView r1 = r10.fragmentContextView
            r0.setAdditionalContextView(r1)
            r14 = r5
            goto L_0x07e0
        L_0x0798:
            int r0 = r10.initialDialogsType
            r1 = 3
            if (r0 != r1) goto L_0x07df
            org.telegram.ui.Components.ChatActivityEnterView r0 = r10.commentView
            if (r0 == 0) goto L_0x07a4
            r0.onDestroy()
        L_0x07a4:
            org.telegram.ui.DialogsActivity$20 r8 = new org.telegram.ui.DialogsActivity$20
            android.app.Activity r2 = r34.getParentActivity()
            r4 = 0
            r13 = 0
            r0 = r8
            r1 = r34
            r3 = r15
            r14 = r5
            r5 = r13
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
            r2 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r2, (int) r9, (int) r1)
            r15.addView(r0, r1)
            org.telegram.ui.Components.ChatActivityEnterView r0 = r10.commentView
            org.telegram.ui.DialogsActivity$21 r1 = new org.telegram.ui.DialogsActivity$21
            r1.<init>()
            r0.setDelegate(r1)
            goto L_0x07e0
        L_0x07df:
            r14 = r5
        L_0x07e0:
            org.telegram.ui.Components.FilterTabsView r0 = r10.filterTabsView
            if (r0 == 0) goto L_0x07ef
            r1 = 1110441984(0x42300000, float:44.0)
            r2 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r1)
            r15.addView(r0, r1)
            goto L_0x07f0
        L_0x07ef:
            r2 = -1
        L_0x07f0:
            boolean r0 = r10.onlySelect
            if (r0 != 0) goto L_0x080b
            r0 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r0)
            boolean r1 = r10.inPreviewMode
            if (r1 == 0) goto L_0x0806
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r6) goto L_0x0806
            int r1 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            r0.topMargin = r1
        L_0x0806:
            org.telegram.ui.ActionBar.ActionBar r1 = r10.actionBar
            r15.addView(r1, r0)
        L_0x080b:
            java.lang.String r0 = r10.searchString
            if (r0 != 0) goto L_0x08d6
            int r0 = r10.initialDialogsType
            if (r0 != 0) goto L_0x08d6
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
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r6) goto L_0x0844
            android.widget.FrameLayout r0 = r10.updateLayout
            java.lang.String r1 = "listSelectorSDK21"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r2 = 0
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable((int) r1, (java.lang.String) r2)
            r0.setBackground(r1)
        L_0x0844:
            android.widget.FrameLayout r0 = r10.updateLayout
            r1 = 48
            r2 = 83
            r3 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r3, (int) r1, (int) r2)
            r15.addView(r0, r1)
            android.widget.FrameLayout r0 = r10.updateLayout
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda10 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda10
            r1.<init>(r10)
            r0.setOnClickListener(r1)
            org.telegram.ui.Components.RadialProgress2 r0 = new org.telegram.ui.Components.RadialProgress2
            android.widget.FrameLayout r1 = r10.updateLayout
            r0.<init>(r1)
            r10.updateLayoutIcon = r0
            r1 = -1
            r0.setColors((int) r1, (int) r1, (int) r1, (int) r1)
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
            r1 = 2131624306(0x7f0e0172, float:1.8875788E38)
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
            r27 = -2
            r28 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r29 = 17
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r0.addView(r1, r2)
        L_0x08d6:
            r0 = 0
        L_0x08d7:
            r1 = 2
            if (r0 >= r1) goto L_0x08ff
            org.telegram.ui.Components.UndoView[] r2 = r10.undoView
            org.telegram.ui.DialogsActivity$23 r3 = new org.telegram.ui.DialogsActivity$23
            r3.<init>(r11)
            r2[r0] = r3
            org.telegram.ui.Components.UndoView[] r2 = r10.undoView
            r2 = r2[r0]
            r27 = -1
            r28 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r29 = 83
            r30 = 1090519040(0x41000000, float:8.0)
            r31 = 0
            r32 = 1090519040(0x41000000, float:8.0)
            r33 = 1090519040(0x41000000, float:8.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r15.addView(r2, r3)
            int r0 = r0 + 1
            goto L_0x08d7
        L_0x08ff:
            int r0 = r10.folderId
            if (r0 == 0) goto L_0x094e
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
        L_0x094e:
            boolean r0 = r10.onlySelect
            if (r0 != 0) goto L_0x096e
            int r0 = r10.initialDialogsType
            if (r0 != 0) goto L_0x096e
            org.telegram.ui.DialogsActivity$24 r0 = new org.telegram.ui.DialogsActivity$24
            r0.<init>(r11)
            r10.blurredView = r0
            r1 = 8
            r0.setVisibility(r1)
            android.view.View r0 = r10.blurredView
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            r2 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r1)
            r15.addView(r0, r1)
        L_0x096e:
            android.graphics.Paint r0 = r10.actionBarDefaultPaint
            int r1 = r10.folderId
            if (r1 != 0) goto L_0x0977
            r1 = r22
            goto L_0x0979
        L_0x0977:
            java.lang.String r1 = "actionBarDefaultArchived"
        L_0x0979:
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setColor(r1)
            boolean r0 = r10.inPreviewMode
            if (r0 == 0) goto L_0x0a00
            org.telegram.messenger.UserConfig r0 = r34.getUserConfig()
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
            org.telegram.ui.Components.ChatAvatarContainer r1 = r10.avatarContainer
            r2 = 0
            r1.setOccupyStatusBar(r2)
            org.telegram.ui.Components.ChatAvatarContainer r1 = r10.avatarContainer
            r2 = 1092616192(0x41200000, float:10.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setLeftPadding(r2)
            org.telegram.ui.ActionBar.ActionBar r1 = r10.actionBar
            org.telegram.ui.Components.ChatAvatarContainer r2 = r10.avatarContainer
            r27 = -2
            r28 = -1082130432(0xffffffffbvar_, float:-1.0)
            r29 = 51
            r30 = 0
            r31 = 0
            r32 = 1109393408(0x42200000, float:40.0)
            r33 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r4 = 0
            r1.addView(r2, r4, r3)
            org.telegram.ui.Components.RLottieImageView r1 = r10.floatingButton
            r2 = 4
            r1.setVisibility(r2)
            org.telegram.ui.ActionBar.ActionBar r1 = r10.actionBar
            r1.setOccupyStatusBar(r4)
            org.telegram.ui.ActionBar.ActionBar r1 = r10.actionBar
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r22)
            r1.setBackgroundColor(r2)
            org.telegram.ui.Components.FragmentContextView r1 = r10.fragmentContextView
            if (r1 == 0) goto L_0x09f9
            r15.removeView(r1)
        L_0x09f9:
            org.telegram.ui.Components.FragmentContextView r1 = r10.fragmentLocationContextView
            if (r1 == 0) goto L_0x0a00
            r15.removeView(r1)
        L_0x0a00:
            r0 = 0
            r10.searchIsShowed = r0
            r10.updateFilterTabs(r0, r0)
            java.lang.String r1 = r10.searchString
            if (r1 == 0) goto L_0x0a16
            r1 = 1
            r10.showSearch(r1, r0)
            org.telegram.ui.ActionBar.ActionBar r1 = r10.actionBar
            java.lang.String r2 = r10.searchString
            r1.openSearchField(r2, r0)
            goto L_0x0a3c
        L_0x0a16:
            r1 = 1
            java.lang.String r2 = r10.initialSearchString
            if (r2 == 0) goto L_0x0a38
            r10.showSearch(r1, r0)
            org.telegram.ui.ActionBar.ActionBar r1 = r10.actionBar
            java.lang.String r2 = r10.initialSearchString
            r1.openSearchField(r2, r0)
            r0 = 0
            r10.initialSearchString = r0
            org.telegram.ui.Components.FilterTabsView r0 = r10.filterTabsView
            if (r0 == 0) goto L_0x0a3c
            r1 = 1110441984(0x42300000, float:44.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = -r1
            float r1 = (float) r1
            r0.setTranslationY(r1)
            goto L_0x0a3c
        L_0x0a38:
            r0 = 0
            r10.showSearch(r0, r0)
        L_0x0a3c:
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 30
            if (r0 < r1) goto L_0x0a45
            org.telegram.messenger.FilesMigrationService.checkBottomSheet(r34)
        L_0x0a45:
            r0 = 0
            r10.updateMenuButton(r0)
            android.view.View r0 = r10.fragmentView
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.createView(android.content.Context):android.view.View");
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2838lambda$createView$2$orgtelegramuiDialogsActivity(View v) {
        this.filterTabsView.setIsEditing(false);
        showDoneItem(false);
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2839lambda$createView$3$orgtelegramuiDialogsActivity() {
        if (this.initialDialogsType != 10) {
            hideFloatingButton(false);
        }
        scrollToTop();
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2840lambda$createView$4$orgtelegramuiDialogsActivity(ViewPage viewPage, View view, int position) {
        int i = this.initialDialogsType;
        if (i == 10) {
            onItemLongClick(view, position, 0.0f, 0.0f, viewPage.dialogsType, viewPage.dialogsAdapter);
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
                    DialogsActivity.this.removeSelfFromStack();
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

    /* renamed from: lambda$createView$5$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2841lambda$createView$5$orgtelegramuiDialogsActivity(View view, int position) {
        if (this.initialDialogsType == 10) {
            onItemLongClick(view, position, 0.0f, 0.0f, -1, this.searchViewPager.dialogsSearchAdapter);
            return;
        }
        onItemClick(view, position, this.searchViewPager.dialogsSearchAdapter);
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2842lambda$createView$6$orgtelegramuiDialogsActivity(boolean showMediaFilters, ArrayList users, ArrayList dates, boolean archive3) {
        updateFiltersView(showMediaFilters, users, dates, archive3, true);
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2843lambda$createView$7$orgtelegramuiDialogsActivity(View view, int position) {
        this.filtersView.cancelClickRunnables(true);
        addSearchFilter(this.filtersView.getFilterAt(position));
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2844lambda$createView$8$orgtelegramuiDialogsActivity(View v) {
        if (this.initialDialogsType != 10) {
            Bundle args = new Bundle();
            args.putBoolean("destroyAfterSelect", true);
            presentFragment(new ContactsActivity(args));
        } else if (this.delegate != null && !this.selectedDialogs.isEmpty()) {
            this.delegate.didSelectDialogs(this, this.selectedDialogs, (CharSequence) null, false);
        }
    }

    /* renamed from: lambda$createView$9$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2845lambda$createView$9$orgtelegramuiDialogsActivity(View v) {
        if (SharedConfig.isAppUpdateAvailable()) {
            AndroidUtilities.openForView(SharedConfig.pendingAppUpdate.document, true, getParentActivity());
        }
    }

    private void updateAppUpdateViews(boolean animated) {
        boolean show;
        if (this.updateLayout != null) {
            if (SharedConfig.isAppUpdateAvailable()) {
                String attachFileName = FileLoader.getAttachFileName(SharedConfig.pendingAppUpdate.document);
                show = FileLoader.getPathToAttach(SharedConfig.pendingAppUpdate.document, true).exists();
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
            actionMode.setBackground((Drawable) null);
            NumberTextView numberTextView = new NumberTextView(actionMode.getContext());
            this.selectedDialogsCountTextView = numberTextView;
            numberTextView.setTextSize(18);
            this.selectedDialogsCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.selectedDialogsCountTextView.setTextColor(Theme.getColor("actionBarActionModeDefaultIcon"));
            actionMode.addView(this.selectedDialogsCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 72, 0, 0, 0));
            this.selectedDialogsCountTextView.setOnTouchListener(DialogsActivity$$ExternalSyntheticLambda12.INSTANCE);
            this.pinItem = actionMode.addItemWithWidth(100, NUM, AndroidUtilities.dp(54.0f));
            this.muteItem = actionMode.addItemWithWidth(104, NUM, AndroidUtilities.dp(54.0f));
            this.archive2Item = actionMode.addItemWithWidth(107, NUM, AndroidUtilities.dp(54.0f));
            this.deleteItem = actionMode.addItemWithWidth(102, NUM, AndroidUtilities.dp(54.0f), LocaleController.getString("Delete", NUM));
            ActionBarMenuItem otherItem = actionMode.addItemWithWidth(0, NUM, AndroidUtilities.dp(54.0f), LocaleController.getString("AccDescrMoreOptions", NUM));
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
                        if (i == 201 || (i == 200 && DialogsActivity.this.searchViewPager != null)) {
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
                                ((LaunchActivity) DialogsActivity.this.getParentActivity()).showPasscodeActivity(false, true, position[0] + (DialogsActivity.this.passcodeItem.getMeasuredWidth() / 2), position[1] + (DialogsActivity.this.passcodeItem.getMeasuredHeight() / 2), new DialogsActivity$27$$ExternalSyntheticLambda0(this), new DialogsActivity$27$$ExternalSyntheticLambda1(this));
                                DialogsActivity.this.updatePasscodeButton();
                            }
                        } else if (i == 2) {
                            DialogsActivity.this.presentFragment(new ProxyListActivity());
                        } else if (i < 10 || i >= 13) {
                            if (i == 109) {
                                DialogsActivity dialogsActivity = DialogsActivity.this;
                                FiltersListBottomSheet sheet = new FiltersListBottomSheet(dialogsActivity, dialogsActivity.selectedDialogs);
                                sheet.setDelegate(new DialogsActivity$27$$ExternalSyntheticLambda2(this));
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

                    /* renamed from: lambda$onItemClick$0$org-telegram-ui-DialogsActivity$27  reason: not valid java name */
                    public /* synthetic */ void m2880lambda$onItemClick$0$orgtelegramuiDialogsActivity$27() {
                        DialogsActivity.this.passcodeItem.setAlpha(1.0f);
                    }

                    /* renamed from: lambda$onItemClick$1$org-telegram-ui-DialogsActivity$27  reason: not valid java name */
                    public /* synthetic */ void m2881lambda$onItemClick$1$orgtelegramuiDialogsActivity$27() {
                        DialogsActivity.this.passcodeItem.setAlpha(0.0f);
                    }

                    /* renamed from: lambda$onItemClick$2$org-telegram-ui-DialogsActivity$27  reason: not valid java name */
                    public /* synthetic */ void m2882lambda$onItemClick$2$orgtelegramuiDialogsActivity$27(MessagesController.DialogFilter filter) {
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
                        if (currentCount + alwaysShow3.size() > 100) {
                            DialogsActivity dialogsActivity2 = DialogsActivity.this;
                            dialogsActivity2.showDialog(AlertsCreator.createSimpleAlert(dialogsActivity2.getParentActivity(), LocaleController.getString("FilterAddToAlertFullTitle", NUM), LocaleController.getString("FilterRemoveFromAlertFullText", NUM)).create());
                            return;
                        }
                        if (dialogFilter != null) {
                            if (!alwaysShow3.isEmpty()) {
                                for (int a = 0; a < alwaysShow3.size(); a++) {
                                    dialogFilter.neverShow.remove(alwaysShow3.get(a));
                                }
                                dialogFilter.alwaysShow.addAll(alwaysShow3);
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
                    }
                });
            }
        }
    }

    static /* synthetic */ boolean lambda$createActionMode$10(View v, MotionEvent event) {
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
        int i = 0;
        if (this.viewPages[a2].selectedType == Integer.MAX_VALUE) {
            int unused = this.viewPages[a2].dialogsType = 0;
            this.viewPages[a2].listView.updatePullState();
        } else {
            MessagesController.DialogFilter filter = getMessagesController().dialogFilters.get(this.viewPages[a2].selectedType);
            if (this.viewPages[a2 == 0 ? (char) 1 : 0].dialogsType == 7) {
                int unused2 = this.viewPages[a2].dialogsType = 8;
            } else {
                int unused3 = this.viewPages[a2].dialogsType = 7;
            }
            this.viewPages[a2].listView.setScrollEnabled(true);
            getMessagesController().selectDialogFilter(filter, this.viewPages[a2].dialogsType == 8 ? 1 : 0);
        }
        this.viewPages[a2].dialogsAdapter.setDialogsType(this.viewPages[a2].dialogsType);
        LinearLayoutManager access$10900 = this.viewPages[a2].layoutManager;
        if (this.viewPages[a2].dialogsType == 0 && hasHiddenArchive()) {
            i = 1;
        }
        access$10900.scrollToPositionWithOffset(i, (int) this.actionBar.getTranslationY());
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
        if (this.filterTabsView != null && !this.inPreviewMode && !this.searchIsShowed) {
            ActionBarPopupWindow actionBarPopupWindow = this.scrimPopupWindow;
            if (actionBarPopupWindow != null) {
                actionBarPopupWindow.dismiss();
                this.scrimPopupWindow = null;
            }
            ArrayList<MessagesController.DialogFilter> filters = getMessagesController().dialogFilters;
            SharedPreferences mainSettings = MessagesController.getMainSettings(this.currentAccount);
            boolean z = true;
            if (filters.isEmpty()) {
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
                    updateFilterTabsVisibility(animated);
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
                updateFilterTabsVisibility(animated);
                int id = this.filterTabsView.getCurrentTabId();
                if (id != Integer.MAX_VALUE && id >= filters.size()) {
                    this.filterTabsView.resetTabId();
                }
                this.filterTabsView.removeTabs();
                this.filterTabsView.addTab(Integer.MAX_VALUE, 0, LocaleController.getString("FilterAllChats", NUM));
                int N = filters.size();
                for (int a2 = 0; a2 < N; a2++) {
                    this.filterTabsView.addTab(a2, filters.get(a2).localId, filters.get(a2).name);
                }
                int id2 = this.filterTabsView.getCurrentTabId();
                boolean updateCurrentTab = false;
                if (id2 >= 0 && this.viewPages[0].selectedType != id2) {
                    updateCurrentTab = true;
                    int unused5 = this.viewPages[0].selectedType = id2;
                }
                int a3 = 0;
                while (true) {
                    ViewPage[] viewPageArr3 = this.viewPages;
                    if (a3 >= viewPageArr3.length) {
                        break;
                    }
                    if (viewPageArr3[a3].selectedType != Integer.MAX_VALUE && this.viewPages[a3].selectedType >= filters.size()) {
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
                    if (id2 != this.filterTabsView.getFirstTabId() && SharedConfig.getChatSwipeAction(this.currentAccount) == 5) {
                        z = false;
                    }
                    drawerLayoutContainer.setAllowOpenDrawerBySwipe(z);
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
            this.afterSignup = false;
        }
        if (tosAccepted && this.checkPermission && !this.onlySelect && Build.VERSION.SDK_INT >= 23) {
            Activity activity = getParentActivity();
            if (activity != null) {
                this.checkPermission = false;
                boolean hasNotContactsPermission = activity.checkSelfPermission("android.permission.READ_CONTACTS") != 0;
                boolean hasNotStoragePermission = (Build.VERSION.SDK_INT <= 28 || BuildVars.NO_SCOPED_STORAGE) && activity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0;
                if (hasNotContactsPermission || hasNotStoragePermission) {
                    this.askingForPermissions = true;
                    if (hasNotContactsPermission && this.askAboutContacts && getUserConfig().syncContacts && activity.shouldShowRequestPermissionRationale("android.permission.READ_CONTACTS")) {
                        AlertDialog create = AlertsCreator.createContactsPermissionDialog(activity, new DialogsActivity$$ExternalSyntheticLambda30(this)).create();
                        this.permissionDialog = create;
                        showDialog(create);
                    } else if (!hasNotStoragePermission || !activity.shouldShowRequestPermissionRationale("android.permission.WRITE_EXTERNAL_STORAGE")) {
                        askForPermissons(true);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
                        builder.setTitle(LocaleController.getString("AppName", NUM));
                        builder.setMessage(LocaleController.getString("PermissionStorage", NUM));
                        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                        AlertDialog create2 = builder.create();
                        this.permissionDialog = create2;
                        showDialog(create2);
                    }
                }
            }
        } else if (!this.onlySelect && XiaomiUtilities.isMIUI() && Build.VERSION.SDK_INT >= 19 && !XiaomiUtilities.isCustomPermissionGranted(10020)) {
            if (getParentActivity() != null && !MessagesController.getGlobalNotificationsSettings().getBoolean("askedAboutMiuiLockscreen", false)) {
                showDialog(new AlertDialog.Builder((Context) getParentActivity()).setTitle(LocaleController.getString("AppName", NUM)).setMessage(LocaleController.getString("PermissionXiaomiLockscreen", NUM)).setPositiveButton(LocaleController.getString("PermissionOpenSettings", NUM), new DialogsActivity$$ExternalSyntheticLambda42(this)).setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", NUM), DialogsActivity$$ExternalSyntheticLambda5.INSTANCE).create());
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
    }

    /* renamed from: lambda$onResume$11$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2858lambda$onResume$11$orgtelegramuiDialogsActivity(int param) {
        this.askAboutContacts = param != 0;
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askAboutContacts", this.askAboutContacts).commit();
        askForPermissons(false);
    }

    /* renamed from: lambda$onResume$12$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2859lambda$onResume$12$orgtelegramuiDialogsActivity(DialogInterface dialog, int which) {
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
        showSearch(true, animated);
        this.actionBar.openSearchField(query, false);
    }

    /* access modifiers changed from: private */
    public void showSearch(boolean show, boolean animated) {
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
                addSearchFilter(new FiltersView.MediaFilterData(NUM, NUM, LocaleController.getString("ArchiveSearchFilter", NUM), (TLRPC.MessagesFilter) null, 7));
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
            valueAnimator.addUpdateListener(new DialogsActivity$$ExternalSyntheticLambda39(this));
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
                        DialogsActivity.this.setSearchAnimationProgress(z ? 1.0f : 0.0f);
                        DialogsActivity.this.viewPages[0].listView.setVerticalScrollBarEnabled(true);
                        DialogsActivity.this.searchViewPager.setBackground((Drawable) null);
                        AnimatorSet unused4 = DialogsActivity.this.searchAnimator = null;
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
            if (z) {
                f2 = 1.0f;
            }
            setSearchAnimationProgress(f2);
            this.fragmentView.invalidate();
        }
        int i4 = this.initialSearchType;
        if (i4 >= 0) {
            this.searchViewPager.setPosition(i4);
        }
        if (!z) {
            this.initialSearchType = -1;
        }
    }

    /* renamed from: lambda$showSearch$14$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2873lambda$showSearch$14$orgtelegramuiDialogsActivity(ValueAnimator valueAnimator1) {
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
                this.filtersTabAnimator.addUpdateListener(new DialogsActivity$$ExternalSyntheticLambda40(this, visible, animateFromScrollY));
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

    /* renamed from: lambda$updateFilterTabsVisibility$15$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2876x63c8e59a(boolean visible, float animateFromScrollY, ValueAnimator valueAnimator) {
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
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda27 r14 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda27
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

    /* renamed from: lambda$checkListLoad$16$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2837lambda$checkListLoad$16$orgtelegramuiDialogsActivity(boolean loadFinal, boolean loadFromCacheFinal, boolean loadArchivedFinal, boolean loadArchivedFromCacheFinal) {
        if (loadFinal) {
            getMessagesController().loadDialogs(this.folderId, -1, 100, loadFromCacheFinal);
        }
        if (loadArchivedFinal) {
            getMessagesController().loadDialogs(1, -1, 100, loadArchivedFromCacheFinal);
        }
    }

    private void onItemClick(View view, int position, RecyclerView.Adapter adapter) {
        TLRPC.Document sticker;
        TLRPC.Chat chat;
        long dialogId;
        int filterId;
        View view2 = view;
        int i = position;
        RecyclerView.Adapter adapter2 = adapter;
        if (getParentActivity() != null) {
            long dialogId2 = 0;
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
                    dialogId2 = ((TLRPC.User) object).id;
                } else if (object instanceof TLRPC.Dialog) {
                    TLRPC.Dialog dialog = (TLRPC.Dialog) object;
                    int folderId3 = dialog.folder_id;
                    if ((dialog instanceof TLRPC.TL_dialogFolder) == 0) {
                        dialogId2 = dialog.id;
                        if (this.actionBar.isActionModeShowed((String) null)) {
                            showOrUpdateActionMode(dialogId2, view2);
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
                    DialogsAdapter dialogsAdapter3 = dialogsAdapter;
                    if (object instanceof TLRPC.TL_recentMeUrlChat) {
                        dialogId2 = -((TLRPC.TL_recentMeUrlChat) object).chat_id;
                    } else if (object instanceof TLRPC.TL_recentMeUrlUser) {
                        dialogId2 = ((TLRPC.TL_recentMeUrlUser) object).user_id;
                    } else if (object instanceof TLRPC.TL_recentMeUrlChatInvite) {
                        TLRPC.TL_recentMeUrlChatInvite chatInvite = (TLRPC.TL_recentMeUrlChatInvite) object;
                        TLRPC.ChatInvite invite = chatInvite.chat_invite;
                        if (invite.chat != null || (invite.channel && !invite.megagroup)) {
                            if (invite.chat == null) {
                            } else if (ChatObject.isChannel(invite.chat) && !invite.chat.megagroup) {
                                TLRPC.TL_recentMeUrlChatInvite tL_recentMeUrlChatInvite = chatInvite;
                            }
                            if (invite.chat != null) {
                                dialogId2 = -invite.chat.id;
                            } else {
                                return;
                            }
                        }
                        String hash = chatInvite.url;
                        int index = hash.indexOf(47);
                        if (index > 0) {
                            hash = hash.substring(index + 1);
                        }
                        TLRPC.TL_recentMeUrlChatInvite tL_recentMeUrlChatInvite2 = chatInvite;
                        showDialog(new JoinGroupAlert(getParentActivity(), invite, hash, this));
                        return;
                    } else if (object instanceof TLRPC.TL_recentMeUrlStickerSet) {
                        TLRPC.StickerSet stickerSet = ((TLRPC.TL_recentMeUrlStickerSet) object).set.set;
                        TLRPC.TL_inputStickerSetID set = new TLRPC.TL_inputStickerSetID();
                        set.id = stickerSet.id;
                        set.access_hash = stickerSet.access_hash;
                        DialogsAdapter dialogsAdapter4 = dialogsAdapter3;
                        StickersAlert stickersAlert = r0;
                        TLObject tLObject = object;
                        TLRPC.TL_inputStickerSetID tL_inputStickerSetID = set;
                        TLRPC.StickerSet stickerSet2 = stickerSet;
                        StickersAlert stickersAlert2 = new StickersAlert((Context) getParentActivity(), (BaseFragment) this, (TLRPC.InputStickerSet) set, (TLRPC.TL_messages_stickerSet) null, (StickersAlert.StickersAlertDelegate) null);
                        showDialog(stickersAlert);
                        return;
                    } else {
                        DialogsAdapter dialogsAdapter5 = dialogsAdapter3;
                        boolean z = object instanceof TLRPC.TL_recentMeUrlUnknown;
                        return;
                    }
                }
                filterId2 = filterId;
            } else if (adapter2 == this.searchViewPager.dialogsSearchAdapter) {
                Object obj = this.searchViewPager.dialogsSearchAdapter.getItem(i);
                isGlobalSearch = this.searchViewPager.dialogsSearchAdapter.isGlobalSearch(i);
                if (obj instanceof TLRPC.User) {
                    dialogId = ((TLRPC.User) obj).id;
                    if (!this.onlySelect) {
                        this.searchDialogId = dialogId;
                        this.searchObject = (TLRPC.User) obj;
                    }
                } else if (obj instanceof TLRPC.Chat) {
                    dialogId = -((TLRPC.Chat) obj).id;
                    if (!this.onlySelect) {
                        this.searchDialogId = dialogId;
                        this.searchObject = (TLRPC.Chat) obj;
                    }
                } else if (obj instanceof TLRPC.EncryptedChat) {
                    dialogId = DialogObject.makeEncryptedDialogId((long) ((TLRPC.EncryptedChat) obj).id);
                    if (!this.onlySelect) {
                        this.searchDialogId = dialogId;
                        this.searchObject = (TLRPC.EncryptedChat) obj;
                    }
                } else {
                    if (obj instanceof MessageObject) {
                        MessageObject messageObject = (MessageObject) obj;
                        long dialogId3 = messageObject.getDialogId();
                        int message_id2 = messageObject.getId();
                        this.searchViewPager.dialogsSearchAdapter.addHashtagsFromMessage(this.searchViewPager.dialogsSearchAdapter.getLastSearchString());
                        message_id = message_id2;
                        dialogId2 = dialogId3;
                    } else if (obj instanceof String) {
                        String str = (String) obj;
                        if (this.searchViewPager.dialogsSearchAdapter.isHashtagSearch()) {
                            this.actionBar.openSearchField(str, false);
                        } else if (!str.equals("section")) {
                            NewContactActivity activity = new NewContactActivity();
                            activity.setInitialPhoneNumber(str, true);
                            presentFragment(activity);
                        }
                    }
                    if (dialogId2 != 0 && this.actionBar.isActionModeShowed()) {
                        if (this.actionBar.isActionModeShowed("search_dialogs_action_mode") && message_id == 0 && !isGlobalSearch) {
                            showOrUpdateActionMode(dialogId2, view2);
                            return;
                        }
                        return;
                    }
                }
                dialogId2 = dialogId;
                if (this.actionBar.isActionModeShowed("search_dialogs_action_mode")) {
                    return;
                }
                return;
            }
            if (dialogId2 != 0) {
                if (!this.onlySelect) {
                    Bundle args2 = new Bundle();
                    if (DialogObject.isEncryptedDialog(dialogId2)) {
                        args2.putInt("enc_id", DialogObject.getEncryptedChatId(dialogId2));
                    } else if (DialogObject.isUserDialog(dialogId2)) {
                        args2.putLong("user_id", dialogId2);
                    } else {
                        long did = dialogId2;
                        if (!(message_id == 0 || (chat = getMessagesController().getChat(Long.valueOf(-did))) == null || chat.migrated_to == null)) {
                            args2.putLong("migrated_to", did);
                            did = -chat.migrated_to.channel_id;
                        }
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
                        if (this.openedDialogId != dialogId2 || adapter2 == this.searchViewPager.dialogsSearchAdapter) {
                            if (this.viewPages != null) {
                                int a = 0;
                                while (true) {
                                    ViewPage[] viewPageArr = this.viewPages;
                                    if (a >= viewPageArr.length) {
                                        break;
                                    }
                                    DialogsAdapter access$8800 = viewPageArr[a].dialogsAdapter;
                                    this.openedDialogId = dialogId2;
                                    access$8800.setOpenedDialogId(dialogId2);
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
                    if (this.searchString != null) {
                        if (getMessagesController().checkCanOpenChat(args2, this)) {
                            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                            presentFragment(new ChatActivity(args2));
                        }
                    } else if (getMessagesController().checkCanOpenChat(args2, this)) {
                        ChatActivity chatActivity = new ChatActivity(args2);
                        if ((adapter2 instanceof DialogsAdapter) && DialogObject.isUserDialog(dialogId2) && getMessagesController().dialogs_dict.get(dialogId2) == null && (sticker = getMediaDataController().getGreetingsSticker()) != null) {
                            chatActivity.setPreloadedSticker(sticker, true);
                        }
                        presentFragment(chatActivity);
                    }
                } else if (validateSlowModeDialog(dialogId2)) {
                    if (!this.selectedDialogs.isEmpty()) {
                        boolean checked = addOrRemoveSelectedDialog(dialogId2, view2);
                        if (adapter2 == this.searchViewPager.dialogsSearchAdapter) {
                            this.actionBar.closeSearchField();
                            findAndUpdateCheckBox(dialogId2, checked);
                        }
                        updateSelectedCount();
                        return;
                    }
                    didSelectResult(dialogId2, true, false);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean onItemLongClick(View view, int position, float x, float y, int dialogsType, RecyclerView.Adapter adapter) {
        String str;
        TLRPC.Dialog dialog;
        String str2;
        int i;
        long did;
        View view2 = view;
        int i2 = position;
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
            Object item = this.searchViewPager.dialogsSearchAdapter.getItem(i2);
            if (this.searchViewPager.dialogsSearchAdapter.isRecentSearchDisplayed()) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("ClearSearchSingleAlertTitle", NUM));
                if (item instanceof TLRPC.Chat) {
                    TLRPC.Chat chat = (TLRPC.Chat) item;
                    builder.setMessage(LocaleController.formatString("ClearSearchSingleChatAlertText", NUM, chat.title));
                    did = -chat.id;
                } else if (item instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) item;
                    String str3 = "ClearSearchSingleUserAlertText";
                    String str4 = "ClearSearchSingleChatAlertText";
                    if (user.id == getUserConfig().clientUserId) {
                        builder.setMessage(LocaleController.formatString(str4, NUM, LocaleController.getString("SavedMessages", NUM)));
                    } else {
                        builder.setMessage(LocaleController.formatString(str3, NUM, ContactsController.formatName(user.first_name, user.last_name)));
                    }
                    did = user.id;
                } else {
                    String str5 = "ClearSearchSingleUserAlertText";
                    if (!(item instanceof TLRPC.EncryptedChat)) {
                        return false;
                    }
                    TLRPC.EncryptedChat encryptedChat = (TLRPC.EncryptedChat) item;
                    TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(encryptedChat.user_id));
                    builder.setMessage(LocaleController.formatString(str5, NUM, ContactsController.formatName(user2.first_name, user2.last_name)));
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
            str = null;
        } else {
            str = null;
        }
        if (adapter2 == this.searchViewPager.dialogsSearchAdapter) {
            long dialogId = 0;
            if ((view2 instanceof ProfileSearchCell) && !this.searchViewPager.dialogsSearchAdapter.isGlobalSearch(i2)) {
                dialogId = ((ProfileSearchCell) view2).getDialogId();
            }
            if (dialogId == 0) {
                return false;
            }
            showOrUpdateActionMode(dialogId, view2);
            return true;
        }
        ArrayList<TLRPC.Dialog> dialogs = getDialogsArray(this.currentAccount, dialogsType, this.folderId, this.dialogsListFrozen);
        int position2 = ((DialogsAdapter) adapter2).fixPosition(i2);
        if (position2 < 0 || position2 >= dialogs.size() || (dialog = dialogs.get(position2)) == null) {
            return false;
        }
        if (this.onlySelect) {
            int i3 = this.initialDialogsType;
            if ((i3 != 3 && i3 != 10) || !validateSlowModeDialog(dialog.id)) {
                return false;
            }
            addOrRemoveSelectedDialog(dialog.id, view2);
            updateSelectedCount();
        } else if (dialog instanceof TLRPC.TL_dialogFolder) {
            view2.performHapticFeedback(0, 2);
            BottomSheet.Builder builder2 = new BottomSheet.Builder(getParentActivity());
            boolean hasUnread = getMessagesStorage().getArchiveUnreadCount() != 0;
            int[] icons = new int[2];
            icons[0] = hasUnread ? NUM : 0;
            icons[1] = SharedConfig.archiveHidden ? NUM : NUM;
            CharSequence[] items = new CharSequence[2];
            if (hasUnread) {
                str = LocaleController.getString("MarkAllAsRead", NUM);
            }
            items[0] = str;
            if (SharedConfig.archiveHidden) {
                i = NUM;
                str2 = "PinInTheList";
            } else {
                i = NUM;
                str2 = "HideAboveTheList";
            }
            items[1] = LocaleController.getString(str2, i);
            builder2.setItems(items, icons, new DialogsActivity$$ExternalSyntheticLambda41(this));
            showDialog(builder2.create());
            return false;
        } else if (this.actionBar.isActionModeShowed() && isDialogPinned(dialog)) {
            return false;
        } else {
            showOrUpdateActionMode(dialog.id, view2);
        }
        return true;
    }

    /* renamed from: lambda$onItemLongClick$17$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2856lambda$onItemLongClick$17$orgtelegramuiDialogsActivity(long did, DialogInterface dialogInterface, int i) {
        this.searchViewPager.dialogsSearchAdapter.removeRecentSearch(did);
    }

    /* renamed from: lambda$onItemLongClick$18$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2857lambda$onItemLongClick$18$orgtelegramuiDialogsActivity(DialogInterface d, int which) {
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

    /* access modifiers changed from: private */
    public boolean showChatPreview(DialogCell cell) {
        TLRPC.Chat chat;
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
        if (this.searchString != null) {
            if (!getMessagesController().checkCanOpenChat(args, this)) {
                return true;
            }
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
            prepareBlurBitmap();
            presentFragmentAsPreview(new ChatActivity(args));
            return true;
        } else if (!getMessagesController().checkCanOpenChat(args, this)) {
            return true;
        } else {
            prepareBlurBitmap();
            presentFragmentAsPreview(new ChatActivity(args));
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
        AndroidUtilities.runOnUIThread(new DialogsActivity$$ExternalSyntheticLambda16(this));
    }

    /* renamed from: lambda$onDialogAnimationFinished$19$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2855xb9d33b1a() {
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
        ofFloat.addUpdateListener(new DialogsActivity$$ExternalSyntheticLambda11(this));
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

    /* renamed from: lambda$hideActionMode$20$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2853lambda$hideActionMode$20$orgtelegramuiDialogsActivity(ValueAnimator valueAnimator) {
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v11, resolved type: org.telegram.tgnet.TLRPC$TL_userEmpty} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v31, resolved type: org.telegram.tgnet.TLRPC$TL_userEmpty} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v34, resolved type: org.telegram.tgnet.TLRPC$TL_userEmpty} */
    /* JADX WARNING: type inference failed for: r13v1, types: [boolean] */
    /* JADX WARNING: type inference failed for: r13v2 */
    /* JADX WARNING: type inference failed for: r13v33 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:122:0x02f0, code lost:
        if (r13 == 108) goto L_0x02f5;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x02ea  */
    /* JADX WARNING: Removed duplicated region for block: B:130:0x0313  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x031f  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0704  */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x071b  */
    /* JADX WARNING: Removed duplicated region for block: B:284:0x0720  */
    /* JADX WARNING: Removed duplicated region for block: B:287:0x072a  */
    /* JADX WARNING: Removed duplicated region for block: B:289:0x072e  */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x0767  */
    /* JADX WARNING: Removed duplicated region for block: B:293:0x077a  */
    /* JADX WARNING: Removed duplicated region for block: B:299:0x0790  */
    /* JADX WARNING: Removed duplicated region for block: B:309:0x07aa  */
    /* JADX WARNING: Removed duplicated region for block: B:310:0x07ac  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void performSelectedDialogsAction(java.util.ArrayList<java.lang.Long> r45, int r46, boolean r47) {
        /*
            r44 = this;
            r14 = r44
            r15 = r45
            r13 = r46
            android.app.Activity r0 = r44.getParentActivity()
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
            r4 = 1
            if (r0 == r1) goto L_0x0027
            org.telegram.ui.DialogsActivity$ViewPage[] r0 = r14.viewPages
            r0 = r0[r5]
            int r0 = r0.dialogsType
            if (r0 != r2) goto L_0x0038
        L_0x0027:
            org.telegram.ui.ActionBar.ActionBar r0 = r14.actionBar
            boolean r0 = r0.isActionModeShowed()
            if (r0 == 0) goto L_0x003a
            org.telegram.ui.ActionBar.ActionBar r0 = r14.actionBar
            boolean r0 = r0.isActionModeShowed(r3)
            if (r0 == 0) goto L_0x0038
            goto L_0x003a
        L_0x0038:
            r0 = 0
            goto L_0x003b
        L_0x003a:
            r0 = 1
        L_0x003b:
            r16 = r0
            if (r16 == 0) goto L_0x0056
            org.telegram.messenger.MessagesController r0 = r44.getMessagesController()
            org.telegram.messenger.MessagesController$DialogFilter[] r0 = r0.selectedDialogFilter
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r14.viewPages
            r1 = r1[r5]
            int r1 = r1.dialogsType
            if (r1 != r2) goto L_0x0051
            r1 = 1
            goto L_0x0052
        L_0x0051:
            r1 = 0
        L_0x0052:
            r0 = r0[r1]
            r12 = r0
            goto L_0x0058
        L_0x0056:
            r0 = 0
            r12 = r0
        L_0x0058:
            int r11 = r45.size()
            r0 = 0
            r1 = 105(0x69, float:1.47E-43)
            if (r13 == r1) goto L_0x07b1
            r1 = 107(0x6b, float:1.5E-43)
            if (r13 != r1) goto L_0x0070
            r3 = r0
            r25 = r11
            r15 = r12
            r2 = r13
            r0 = 4
            r1 = 2
            r4 = 3
            r13 = 0
            goto L_0x07ba
        L_0x0070:
            java.lang.String r8 = "Cancel"
            r6 = 108(0x6c, float:1.51E-43)
            r2 = 100
            if (r13 == r2) goto L_0x007e
            if (r13 != r6) goto L_0x007b
            goto L_0x007e
        L_0x007b:
            r3 = r0
            goto L_0x01da
        L_0x007e:
            int r10 = r14.canPinCount
            if (r10 == 0) goto L_0x01d9
            r10 = 0
            r20 = 0
            r21 = 0
            r22 = 0
            org.telegram.messenger.MessagesController r6 = r44.getMessagesController()
            int r1 = r14.folderId
            java.util.ArrayList r1 = r6.getDialogs(r1)
            r6 = 0
            int r7 = r1.size()
        L_0x0098:
            if (r6 >= r7) goto L_0x00dd
            java.lang.Object r26 = r1.get(r6)
            r9 = r26
            org.telegram.tgnet.TLRPC$Dialog r9 = (org.telegram.tgnet.TLRPC.Dialog) r9
            boolean r3 = r9 instanceof org.telegram.tgnet.TLRPC.TL_dialogFolder
            if (r3 == 0) goto L_0x00aa
            r3 = r0
            r28 = r1
            goto L_0x00d4
        L_0x00aa:
            boolean r3 = r14.isDialogPinned(r9)
            if (r3 == 0) goto L_0x00c4
            long r2 = r9.id
            boolean r2 = org.telegram.messenger.DialogObject.isEncryptedDialog(r2)
            if (r2 == 0) goto L_0x00be
            int r20 = r20 + 1
            r3 = r0
            r28 = r1
            goto L_0x00d4
        L_0x00be:
            int r10 = r10 + 1
            r3 = r0
            r28 = r1
            goto L_0x00d4
        L_0x00c4:
            org.telegram.messenger.MessagesController r2 = r44.getMessagesController()
            r3 = r0
            r28 = r1
            long r0 = r9.id
            boolean r0 = r2.isPromoDialog(r0, r5)
            if (r0 != 0) goto L_0x00d4
            goto L_0x00e0
        L_0x00d4:
            int r6 = r6 + 1
            r0 = r3
            r1 = r28
            r2 = 100
            r3 = 0
            goto L_0x0098
        L_0x00dd:
            r3 = r0
            r28 = r1
        L_0x00e0:
            r0 = 0
            r1 = 0
        L_0x00e2:
            if (r1 >= r11) goto L_0x0122
            java.lang.Object r2 = r15.get(r1)
            java.lang.Long r2 = (java.lang.Long) r2
            long r6 = r2.longValue()
            org.telegram.messenger.MessagesController r2 = r44.getMessagesController()
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r2 = r2.dialogs_dict
            java.lang.Object r2 = r2.get(r6)
            org.telegram.tgnet.TLRPC$Dialog r2 = (org.telegram.tgnet.TLRPC.Dialog) r2
            if (r2 == 0) goto L_0x011e
            boolean r9 = r14.isDialogPinned(r2)
            if (r9 == 0) goto L_0x0103
            goto L_0x011e
        L_0x0103:
            boolean r9 = org.telegram.messenger.DialogObject.isEncryptedDialog(r6)
            if (r9 == 0) goto L_0x010c
            int r22 = r22 + 1
            goto L_0x010e
        L_0x010c:
            int r21 = r21 + 1
        L_0x010e:
            if (r12 == 0) goto L_0x011e
            java.util.ArrayList<java.lang.Long> r9 = r12.alwaysShow
            java.lang.Long r5 = java.lang.Long.valueOf(r6)
            boolean r5 = r9.contains(r5)
            if (r5 == 0) goto L_0x011e
            int r0 = r0 + 1
        L_0x011e:
            int r1 = r1 + 1
            r5 = 0
            goto L_0x00e2
        L_0x0122:
            if (r16 == 0) goto L_0x012f
            java.util.ArrayList<java.lang.Long> r1 = r12.alwaysShow
            int r1 = r1.size()
            r2 = 100
            int r1 = 100 - r1
            goto L_0x0143
        L_0x012f:
            int r1 = r14.folderId
            if (r1 != 0) goto L_0x013d
            if (r12 == 0) goto L_0x0136
            goto L_0x013d
        L_0x0136:
            org.telegram.messenger.MessagesController r1 = r44.getMessagesController()
            int r1 = r1.maxPinnedDialogsCount
            goto L_0x0143
        L_0x013d:
            org.telegram.messenger.MessagesController r1 = r44.getMessagesController()
            int r1 = r1.maxFolderPinnedDialogsCount
        L_0x0143:
            int r2 = r22 + r20
            if (r2 > r1) goto L_0x014f
            int r2 = r21 + r10
            int r2 = r2 - r0
            if (r2 <= r1) goto L_0x014d
            goto L_0x014f
        L_0x014d:
            goto L_0x02e5
        L_0x014f:
            int r2 = r14.folderId
            java.lang.String r5 = "Chats"
            if (r2 != 0) goto L_0x01a9
            if (r12 == 0) goto L_0x0158
            goto L_0x01a9
        L_0x0158:
            org.telegram.ui.ActionBar.AlertDialog$Builder r2 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r6 = r44.getParentActivity()
            r2.<init>((android.content.Context) r6)
            r6 = 2131624300(0x7f0e016c, float:1.8875776E38)
            java.lang.String r7 = "AppName"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r2.setTitle(r6)
            r6 = 2131627188(0x7f0e0cb4, float:1.8881633E38)
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r5, r1)
            r7 = 0
            r4[r7] = r5
            java.lang.String r5 = "PinToTopLimitReached2"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r5, r6, r4)
            r2.setMessage(r4)
            r4 = 2131625674(0x7f0e06ca, float:1.8878563E38)
            java.lang.String r5 = "FiltersSetupPinAlert"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda43 r5 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda43
            r5.<init>(r14)
            r2.setNegativeButton(r4, r5)
            r4 = 2131626751(0x7f0e0aff, float:1.8880747E38)
            java.lang.String r5 = "OK"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r5 = 0
            r2.setPositiveButton(r4, r5)
            org.telegram.ui.ActionBar.AlertDialog r4 = r2.create()
            r14.showDialog(r4)
            r6 = 0
            goto L_0x01be
        L_0x01a9:
            r2 = 2131627178(0x7f0e0caa, float:1.8881613E38)
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r5, r1)
            r6 = 0
            r4[r6] = r5
            java.lang.String r5 = "PinFolderLimitReached"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r5, r2, r4)
            org.telegram.ui.Components.AlertsCreator.showSimpleAlert(r14, r2)
        L_0x01be:
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r14.pinItem
            r4 = 1073741824(0x40000000, float:2.0)
            org.telegram.messenger.AndroidUtilities.shakeView(r2, r4, r6)
            android.app.Activity r2 = r44.getParentActivity()
            java.lang.String r4 = "vibrator"
            java.lang.Object r2 = r2.getSystemService(r4)
            android.os.Vibrator r2 = (android.os.Vibrator) r2
            if (r2 == 0) goto L_0x01d8
            r4 = 200(0xc8, double:9.9E-322)
            r2.vibrate(r4)
        L_0x01d8:
            return
        L_0x01d9:
            r3 = r0
        L_0x01da:
            r0 = 102(0x66, float:1.43E-43)
            if (r13 == r0) goto L_0x01e2
            r0 = 103(0x67, float:1.44E-43)
            if (r13 != r0) goto L_0x02b4
        L_0x01e2:
            if (r11 <= r4) goto L_0x02b4
            if (r47 == 0) goto L_0x02b4
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r1 = r44.getParentActivity()
            r0.<init>((android.content.Context) r1)
            r1 = 102(0x66, float:1.43E-43)
            if (r13 != r1) goto L_0x0217
            r1 = 2131625224(0x7f0e0508, float:1.887765E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            java.lang.String r4 = "ChatsSelected"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r11)
            r5 = 0
            r2[r5] = r4
            java.lang.String r4 = "DeleteFewChatsTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r4, r1, r2)
            r0.setTitle(r1)
            r1 = 2131624363(0x7f0e01ab, float:1.8875904E38)
            java.lang.String r2 = "AreYouSureDeleteFewChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setMessage(r1)
            goto L_0x0262
        L_0x0217:
            int r1 = r14.canClearCacheCount
            if (r1 == 0) goto L_0x023f
            r1 = 2131624984(0x7f0e0418, float:1.8877163E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            java.lang.String r4 = "ChatsSelectedClearCache"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r11)
            r5 = 0
            r2[r5] = r4
            java.lang.String r4 = "ClearCacheFewChatsTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r4, r1, r2)
            r0.setTitle(r1)
            r1 = 2131624351(0x7f0e019f, float:1.887588E38)
            java.lang.String r2 = "AreYouSureClearHistoryCacheFewChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setMessage(r1)
            goto L_0x0262
        L_0x023f:
            r1 = 2131624986(0x7f0e041a, float:1.8877167E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            java.lang.String r4 = "ChatsSelectedClear"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r11)
            r5 = 0
            r2[r5] = r4
            java.lang.String r4 = "ClearFewChatsTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r4, r1, r2)
            r0.setTitle(r1)
            r1 = 2131624353(0x7f0e01a1, float:1.8875883E38)
            java.lang.String r2 = "AreYouSureClearHistoryFewChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setMessage(r1)
        L_0x0262:
            r1 = 102(0x66, float:1.43E-43)
            if (r13 != r1) goto L_0x0270
            r1 = 2131625188(0x7f0e04e4, float:1.8877577E38)
            java.lang.String r2 = "Delete"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x0287
        L_0x0270:
            int r1 = r14.canClearCacheCount
            if (r1 == 0) goto L_0x027e
            r1 = 2131624988(0x7f0e041c, float:1.8877171E38)
            java.lang.String r2 = "ClearHistoryCache"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x0287
        L_0x027e:
            r1 = 2131624987(0x7f0e041b, float:1.887717E38)
            java.lang.String r2 = "ClearHistory"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
        L_0x0287:
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda4 r2 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda4
            r2.<init>(r14, r15, r13)
            r0.setPositiveButton(r1, r2)
            r1 = 2131624692(0x7f0e02f4, float:1.887657E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r8, r1)
            r2 = 0
            r0.setNegativeButton(r1, r2)
            org.telegram.ui.ActionBar.AlertDialog r1 = r0.create()
            r14.showDialog(r1)
            r2 = -1
            android.view.View r2 = r1.getButton(r2)
            android.widget.TextView r2 = (android.widget.TextView) r2
            if (r2 == 0) goto L_0x02b3
            java.lang.String r4 = "dialogTextRed2"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r2.setTextColor(r4)
        L_0x02b3:
            return
        L_0x02b4:
            r0 = 106(0x6a, float:1.49E-43)
            if (r13 != r0) goto L_0x02e5
            if (r47 == 0) goto L_0x02e5
            if (r11 != r4) goto L_0x02d4
            r0 = 0
            java.lang.Object r1 = r15.get(r0)
            java.lang.Long r1 = (java.lang.Long) r1
            long r0 = r1.longValue()
            org.telegram.messenger.MessagesController r2 = r44.getMessagesController()
            java.lang.Long r5 = java.lang.Long.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r2.getUser(r5)
            goto L_0x02d5
        L_0x02d4:
            r0 = 0
        L_0x02d5:
            int r1 = r14.canReportSpamCount
            if (r1 == 0) goto L_0x02db
            r5 = 1
            goto L_0x02dc
        L_0x02db:
            r5 = 0
        L_0x02dc:
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda34 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda34
            r1.<init>(r14, r15)
            org.telegram.ui.Components.AlertsCreator.createBlockDialogAlert(r14, r11, r5, r0, r1)
            return
        L_0x02e5:
            r0 = 2147483647(0x7fffffff, float:NaN)
            if (r12 == 0) goto L_0x0313
            r1 = 100
            if (r13 == r1) goto L_0x02f3
            r1 = 108(0x6c, float:1.51E-43)
            if (r13 != r1) goto L_0x0315
            goto L_0x02f5
        L_0x02f3:
            r1 = 108(0x6c, float:1.51E-43)
        L_0x02f5:
            int r2 = r14.canPinCount
            if (r2 == 0) goto L_0x0315
            r2 = 0
            org.telegram.messenger.support.LongSparseIntArray r5 = r12.pinnedDialogs
            int r5 = r5.size()
        L_0x0300:
            if (r2 >= r5) goto L_0x030f
            org.telegram.messenger.support.LongSparseIntArray r6 = r12.pinnedDialogs
            int r6 = r6.valueAt(r2)
            int r0 = java.lang.Math.min(r0, r6)
            int r2 = r2 + 1
            goto L_0x0300
        L_0x030f:
            int r2 = r14.canPinCount
            int r0 = r0 - r2
            goto L_0x0315
        L_0x0313:
            r1 = 108(0x6c, float:1.51E-43)
        L_0x0315:
            r20 = 0
            r2 = 0
            r22 = r0
            r10 = r2
            r21 = r3
        L_0x031d:
            if (r10 >= r11) goto L_0x06fb
            java.lang.Object r2 = r15.get(r10)
            java.lang.Long r2 = (java.lang.Long) r2
            long r2 = r2.longValue()
            org.telegram.messenger.MessagesController r5 = r44.getMessagesController()
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r5 = r5.dialogs_dict
            java.lang.Object r5 = r5.get(r2)
            r9 = r5
            org.telegram.tgnet.TLRPC$Dialog r9 = (org.telegram.tgnet.TLRPC.Dialog) r9
            if (r9 != 0) goto L_0x0346
            r25 = r8
            r27 = r10
            r5 = r11
            r6 = r13
            r0 = 4
            r1 = 2
            r4 = 3
            r19 = 103(0x67, float:1.44E-43)
            r13 = r12
            goto L_0x06d1
        L_0x0346:
            r5 = 0
            r6 = 0
            boolean r7 = org.telegram.messenger.DialogObject.isEncryptedDialog(r2)
            if (r7 == 0) goto L_0x0383
            org.telegram.messenger.MessagesController r7 = r44.getMessagesController()
            int r23 = org.telegram.messenger.DialogObject.getEncryptedChatId(r2)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r23)
            org.telegram.tgnet.TLRPC$EncryptedChat r6 = r7.getEncryptedChat(r1)
            r1 = 0
            if (r6 == 0) goto L_0x0376
            org.telegram.messenger.MessagesController r7 = r44.getMessagesController()
            r30 = r1
            long r0 = r6.user_id
            java.lang.Long r0 = java.lang.Long.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r5 = r7.getUser(r0)
            r7 = r5
            r4 = r6
            r41 = r30
            goto L_0x03ad
        L_0x0376:
            r30 = r1
            org.telegram.tgnet.TLRPC$TL_userEmpty r0 = new org.telegram.tgnet.TLRPC$TL_userEmpty
            r0.<init>()
            r5 = r0
            r7 = r5
            r4 = r6
            r41 = r30
            goto L_0x03ad
        L_0x0383:
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r2)
            if (r0 == 0) goto L_0x039b
            org.telegram.messenger.MessagesController r0 = r44.getMessagesController()
            java.lang.Long r1 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r5 = r0.getUser(r1)
            r1 = 0
            r41 = r1
            r7 = r5
            r4 = r6
            goto L_0x03ad
        L_0x039b:
            org.telegram.messenger.MessagesController r0 = r44.getMessagesController()
            r1 = r5
            long r4 = -r2
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r4)
            r41 = r0
            r7 = r1
            r4 = r6
        L_0x03ad:
            if (r41 != 0) goto L_0x03bf
            if (r7 != 0) goto L_0x03bf
            r25 = r8
            r27 = r10
            r5 = r11
            r6 = r13
            r0 = 4
            r1 = 2
            r4 = 3
            r19 = 103(0x67, float:1.44E-43)
            r13 = r12
            goto L_0x06d1
        L_0x03bf:
            if (r7 == 0) goto L_0x03cd
            boolean r0 = r7.bot
            if (r0 == 0) goto L_0x03cd
            boolean r0 = org.telegram.messenger.MessagesController.isSupportUser(r7)
            if (r0 != 0) goto L_0x03cd
            r6 = 1
            goto L_0x03ce
        L_0x03cd:
            r6 = 0
        L_0x03ce:
            r5 = 108(0x6c, float:1.51E-43)
            r0 = 100
            if (r13 == r0) goto L_0x0654
            if (r13 != r5) goto L_0x03ec
            r29 = r2
            r15 = r4
            r23 = r6
            r2 = r7
            r25 = r8
            r3 = r9
            r27 = r10
            r5 = r11
            r17 = r12
            r6 = r13
            r0 = 4
            r1 = 2
            r4 = 3
            r19 = 103(0x67, float:1.44E-43)
            goto L_0x0668
        L_0x03ec:
            r0 = 101(0x65, float:1.42E-43)
            if (r13 != r0) goto L_0x04b4
            int r0 = r14.canReadCount
            if (r0 == 0) goto L_0x0497
            r0 = 2
            r14.debugLastUpdateAction = r0
            r0 = -1
            if (r12 == 0) goto L_0x0440
            int r1 = r12.flags
            int r23 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ
            r1 = r1 & r23
            if (r1 == 0) goto L_0x0440
            int r1 = r14.currentAccount
            boolean r1 = r12.alwaysShow(r1, r9)
            if (r1 != 0) goto L_0x0440
            r1 = 1
            r14.setDialogsListFrozen(r1)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r1 = r14.frozenDialogsList
            if (r1 == 0) goto L_0x043d
            r1 = 0
        L_0x0413:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r5 = r14.frozenDialogsList
            int r5 = r5.size()
            if (r1 >= r5) goto L_0x0434
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r5 = r14.frozenDialogsList
            java.lang.Object r5 = r5.get(r1)
            org.telegram.tgnet.TLRPC$Dialog r5 = (org.telegram.tgnet.TLRPC.Dialog) r5
            r42 = r4
            long r4 = r5.id
            int r23 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r23 != 0) goto L_0x042d
            r0 = r1
            goto L_0x0436
        L_0x042d:
            int r1 = r1 + 1
            r4 = r42
            r5 = 108(0x6c, float:1.51E-43)
            goto L_0x0413
        L_0x0434:
            r42 = r4
        L_0x0436:
            if (r0 >= 0) goto L_0x0442
            r1 = 0
            r14.setDialogsListFrozen(r1, r1)
            goto L_0x0442
        L_0x043d:
            r42 = r4
            goto L_0x0442
        L_0x0440:
            r42 = r4
        L_0x0442:
            org.telegram.messenger.MessagesController r1 = r44.getMessagesController()
            r1.markMentionsAsRead(r2)
            org.telegram.messenger.MessagesController r30 = r44.getMessagesController()
            int r1 = r9.top_message
            int r4 = r9.top_message
            int r5 = r9.last_message_date
            r36 = 0
            r37 = 0
            r38 = 0
            r39 = 1
            r40 = 0
            r31 = r2
            r33 = r1
            r34 = r4
            r35 = r5
            r30.markDialogAsRead(r31, r33, r34, r35, r36, r37, r38, r39, r40)
            if (r0 < 0) goto L_0x0489
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r1 = r14.frozenDialogsList
            r1.remove(r0)
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r14.viewPages
            r4 = 0
            r1 = r1[r4]
            org.telegram.ui.Components.DialogsItemAnimator r1 = r1.dialogsItemAnimator
            r1.prepareForRemove()
            org.telegram.ui.DialogsActivity$ViewPage[] r1 = r14.viewPages
            r1 = r1[r4]
            org.telegram.ui.Adapters.DialogsAdapter r1 = r1.dialogsAdapter
            r1.notifyItemRemoved(r0)
            r1 = 2
            r14.dialogRemoveFinished = r1
        L_0x0489:
            r25 = r8
            r27 = r10
            r5 = r11
            r6 = r13
            r0 = 4
            r1 = 2
            r4 = 3
            r19 = 103(0x67, float:1.44E-43)
            r13 = r12
            goto L_0x06d1
        L_0x0497:
            r42 = r4
            org.telegram.messenger.MessagesController r30 = r44.getMessagesController()
            r33 = 0
            r34 = 0
            r31 = r2
            r30.markDialogAsUnread(r31, r33, r34)
            r25 = r8
            r27 = r10
            r5 = r11
            r6 = r13
            r0 = 4
            r1 = 2
            r4 = 3
            r19 = 103(0x67, float:1.44E-43)
            r13 = r12
            goto L_0x06d1
        L_0x04b4:
            r42 = r4
            r0 = 102(0x66, float:1.43E-43)
            if (r13 == r0) goto L_0x054a
            r0 = 103(0x67, float:1.44E-43)
            if (r13 != r0) goto L_0x04c2
            r1 = 4
            r4 = 3
            goto L_0x054c
        L_0x04c2:
            r0 = 104(0x68, float:1.46E-43)
            if (r13 != r0) goto L_0x053b
            r0 = 1
            if (r11 != r0) goto L_0x04db
            int r1 = r14.canMuteCount
            if (r1 != r0) goto L_0x04db
            r0 = 0
            org.telegram.ui.ActionBar.BottomSheet r0 = org.telegram.ui.Components.AlertsCreator.createMuteAlert(r14, r2, r0)
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda6 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda6
            r1.<init>(r14)
            r14.showDialog(r0, r1)
            return
        L_0x04db:
            int r0 = r14.canUnmuteCount
            if (r0 == 0) goto L_0x050d
            org.telegram.messenger.MessagesController r0 = r44.getMessagesController()
            boolean r0 = r0.isDialogMuted(r2)
            if (r0 != 0) goto L_0x04f7
            r25 = r8
            r27 = r10
            r5 = r11
            r6 = r13
            r0 = 4
            r1 = 2
            r4 = 3
            r19 = 103(0x67, float:1.44E-43)
            r13 = r12
            goto L_0x06d1
        L_0x04f7:
            org.telegram.messenger.NotificationsController r0 = r44.getNotificationsController()
            r1 = 4
            r0.setDialogNotificationsSettings(r2, r1)
            r25 = r8
            r27 = r10
            r5 = r11
            r6 = r13
            r0 = 4
            r1 = 2
            r4 = 3
            r19 = 103(0x67, float:1.44E-43)
            r13 = r12
            goto L_0x06d1
        L_0x050d:
            r1 = 4
            org.telegram.messenger.MessagesController r0 = r44.getMessagesController()
            boolean r0 = r0.isDialogMuted(r2)
            if (r0 == 0) goto L_0x0526
            r25 = r8
            r27 = r10
            r5 = r11
            r6 = r13
            r0 = 4
            r1 = 2
            r4 = 3
            r19 = 103(0x67, float:1.44E-43)
            r13 = r12
            goto L_0x06d1
        L_0x0526:
            org.telegram.messenger.NotificationsController r0 = r44.getNotificationsController()
            r4 = 3
            r0.setDialogNotificationsSettings(r2, r4)
            r25 = r8
            r27 = r10
            r5 = r11
            r6 = r13
            r0 = 4
            r1 = 2
            r19 = 103(0x67, float:1.44E-43)
            r13 = r12
            goto L_0x06d1
        L_0x053b:
            r1 = 4
            r4 = 3
            r25 = r8
            r27 = r10
            r5 = r11
            r6 = r13
            r0 = 4
            r1 = 2
            r19 = 103(0x67, float:1.44E-43)
            r13 = r12
            goto L_0x06d1
        L_0x054a:
            r1 = 4
            r4 = 3
        L_0x054c:
            r0 = 1
            if (r11 != r0) goto L_0x05e5
            r0 = 102(0x66, float:1.43E-43)
            if (r13 != r0) goto L_0x05a7
            boolean r0 = r14.canDeletePsaSelected
            if (r0 == 0) goto L_0x05a7
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r1 = r44.getParentActivity()
            r0.<init>((android.content.Context) r1)
            r1 = 2131627376(0x7f0e0d70, float:1.8882015E38)
            java.lang.String r4 = "PsaHideChatAlertTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r0.setTitle(r1)
            r1 = 2131627375(0x7f0e0d6f, float:1.8882013E38)
            java.lang.String r4 = "PsaHideChatAlertText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r0.setMessage(r1)
            r1 = 2131627374(0x7f0e0d6e, float:1.888201E38)
            java.lang.String r4 = "PsaHide"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda44 r4 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda44
            r4.<init>(r14)
            r0.setPositiveButton(r1, r4)
            r5 = 2131624692(0x7f0e02f4, float:1.887657E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r8, r5)
            r4 = 0
            r0.setNegativeButton(r1, r4)
            org.telegram.ui.ActionBar.AlertDialog r1 = r0.create()
            r14.showDialog(r1)
            r23 = r2
            r2 = r7
            r3 = r9
            r27 = r10
            r0 = r11
            r1 = r12
            r4 = r13
            r15 = r42
            goto L_0x05e4
        L_0x05a7:
            r0 = 103(0x67, float:1.44E-43)
            if (r13 != r0) goto L_0x05ad
            r8 = 1
            goto L_0x05ae
        L_0x05ad:
            r8 = 0
        L_0x05ae:
            long r0 = r9.id
            boolean r17 = org.telegram.messenger.DialogObject.isEncryptedDialog(r0)
            r0 = 102(0x66, float:1.43E-43)
            if (r13 != r0) goto L_0x05bb
            r29 = 1
            goto L_0x05bd
        L_0x05bb:
            r29 = 0
        L_0x05bd:
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda28 r18 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda28
            r0 = r18
            r1 = r44
            r4 = r2
            r2 = r46
            r3 = r41
            r23 = r4
            r15 = r42
            r0.<init>(r1, r2, r3, r4, r6)
            r2 = r7
            r7 = r44
            r3 = r9
            r9 = r41
            r27 = r10
            r10 = r2
            r0 = r11
            r11 = r17
            r1 = r12
            r12 = r29
            r4 = r13
            r13 = r18
            org.telegram.ui.Components.AlertsCreator.createClearOrDeleteDialogAlert(r7, r8, r9, r10, r11, r12, r13)
        L_0x05e4:
            return
        L_0x05e5:
            r23 = r2
            r2 = r7
            r3 = r9
            r27 = r10
            r15 = r42
            r0 = 102(0x66, float:1.43E-43)
            r5 = 2131624692(0x7f0e02f4, float:1.887657E38)
            r43 = r12
            r12 = r11
            r11 = r43
            org.telegram.messenger.MessagesController r7 = r44.getMessagesController()
            r9 = r23
            r0 = 1
            boolean r7 = r7.isPromoDialog(r9, r0)
            if (r7 == 0) goto L_0x0616
            org.telegram.messenger.MessagesController r0 = r44.getMessagesController()
            r0.hidePromoDialog()
            r25 = r8
            r5 = r12
            r6 = r13
            r0 = 4
            r1 = 2
            r19 = 103(0x67, float:1.44E-43)
            r13 = r11
            goto L_0x06d1
        L_0x0616:
            r0 = 103(0x67, float:1.44E-43)
            if (r13 != r0) goto L_0x0632
            int r7 = r14.canClearCacheCount
            if (r7 == 0) goto L_0x0632
            org.telegram.messenger.MessagesController r7 = r44.getMessagesController()
            r0 = 2
            r5 = 0
            r7.deleteDialog(r9, r0, r5)
            r25 = r8
            r5 = r12
            r6 = r13
            r0 = 4
            r1 = 2
            r19 = 103(0x67, float:1.44E-43)
            r13 = r11
            goto L_0x06d1
        L_0x0632:
            r0 = 2
            r5 = 0
            r18 = 0
            r19 = 103(0x67, float:1.44E-43)
            r7 = r44
            r25 = r8
            r8 = r46
            r29 = r9
            r0 = 4
            r1 = 2
            r17 = r11
            r11 = r41
            r5 = r12
            r12 = r6
            r23 = r6
            r6 = r13
            r13 = r18
            r7.m2866x27af7b27(r8, r9, r11, r12, r13)
            r13 = r17
            goto L_0x06d1
        L_0x0654:
            r29 = r2
            r15 = r4
            r23 = r6
            r2 = r7
            r25 = r8
            r3 = r9
            r27 = r10
            r5 = r11
            r17 = r12
            r6 = r13
            r0 = 4
            r1 = 2
            r4 = 3
            r19 = 103(0x67, float:1.44E-43)
        L_0x0668:
            int r7 = r14.canPinCount
            if (r7 == 0) goto L_0x06c8
            boolean r7 = r14.isDialogPinned(r3)
            if (r7 == 0) goto L_0x0675
            r13 = r17
            goto L_0x06d1
        L_0x0675:
            int r21 = r21 + 1
            r10 = 1
            r7 = 1
            if (r5 != r7) goto L_0x067d
            r13 = 1
            goto L_0x067e
        L_0x067d:
            r13 = 0
        L_0x067e:
            r7 = r44
            r8 = r29
            r11 = r17
            r12 = r22
            r7.pinDialog(r8, r10, r11, r12, r13)
            r13 = r17
            if (r13 == 0) goto L_0x06c6
            int r22 = r22 + 1
            if (r15 == 0) goto L_0x06ab
            java.util.ArrayList<java.lang.Long> r7 = r13.alwaysShow
            long r8 = r15.user_id
            java.lang.Long r8 = java.lang.Long.valueOf(r8)
            boolean r7 = r7.contains(r8)
            if (r7 != 0) goto L_0x06c4
            java.util.ArrayList<java.lang.Long> r7 = r13.alwaysShow
            long r8 = r15.user_id
            java.lang.Long r8 = java.lang.Long.valueOf(r8)
            r7.add(r8)
            goto L_0x06c4
        L_0x06ab:
            java.util.ArrayList<java.lang.Long> r7 = r13.alwaysShow
            long r8 = r3.id
            java.lang.Long r8 = java.lang.Long.valueOf(r8)
            boolean r7 = r7.contains(r8)
            if (r7 != 0) goto L_0x06c4
            java.util.ArrayList<java.lang.Long> r7 = r13.alwaysShow
            long r8 = r3.id
            java.lang.Long r8 = java.lang.Long.valueOf(r8)
            r7.add(r8)
        L_0x06c4:
            r15 = r13
            goto L_0x06ed
        L_0x06c6:
            r15 = r13
            goto L_0x06ed
        L_0x06c8:
            r13 = r17
            boolean r7 = r14.isDialogPinned(r3)
            if (r7 != 0) goto L_0x06d3
        L_0x06d1:
            r15 = r13
            goto L_0x06ed
        L_0x06d3:
            int r21 = r21 + 1
            r10 = 0
            r7 = 1
            if (r5 != r7) goto L_0x06dc
            r17 = 1
            goto L_0x06de
        L_0x06dc:
            r17 = 0
        L_0x06de:
            r7 = r44
            r8 = r29
            r11 = r13
            r12 = r22
            r42 = r15
            r15 = r13
            r13 = r17
            r7.pinDialog(r8, r10, r11, r12, r13)
        L_0x06ed:
            int r10 = r27 + 1
            r11 = r5
            r13 = r6
            r12 = r15
            r8 = r25
            r1 = 108(0x6c, float:1.51E-43)
            r4 = 1
            r15 = r45
            goto L_0x031d
        L_0x06fb:
            r27 = r10
            r5 = r11
            r15 = r12
            r6 = r13
            r0 = 104(0x68, float:1.46E-43)
            if (r6 != r0) goto L_0x071b
            r4 = 1
            if (r5 != r4) goto L_0x070b
            int r0 = r14.canMuteCount
            if (r0 == r4) goto L_0x071c
        L_0x070b:
            int r0 = r14.canUnmuteCount
            if (r0 != 0) goto L_0x0711
            r7 = 1
            goto L_0x0712
        L_0x0711:
            r7 = 0
        L_0x0712:
            r0 = 0
            org.telegram.ui.Components.Bulletin r1 = org.telegram.ui.Components.BulletinFactory.createMuteBulletin((org.telegram.ui.ActionBar.BaseFragment) r14, (boolean) r7, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
            r1.show()
            goto L_0x071c
        L_0x071b:
            r4 = 1
        L_0x071c:
            r2 = 100
            if (r6 == r2) goto L_0x072a
            r7 = 108(0x6c, float:1.51E-43)
            if (r6 != r7) goto L_0x0725
            goto L_0x072c
        L_0x0725:
            r25 = r5
            r13 = 0
            goto L_0x078e
        L_0x072a:
            r7 = 108(0x6c, float:1.51E-43)
        L_0x072c:
            if (r15 == 0) goto L_0x0767
            int r1 = r15.flags
            java.lang.String r3 = r15.name
            java.util.ArrayList<java.lang.Long> r8 = r15.alwaysShow
            java.util.ArrayList<java.lang.Long> r9 = r15.neverShow
            org.telegram.messenger.support.LongSparseIntArray r10 = r15.pinnedDialogs
            r11 = 0
            r17 = 0
            r18 = 1
            r19 = 1
            r23 = 0
            r24 = 0
            r0 = r15
            r13 = 100
            r2 = r3
            r3 = r8
            r12 = 1
            r4 = r9
            r25 = r5
            r8 = 108(0x6c, float:1.51E-43)
            r9 = 0
            r5 = r10
            r10 = r6
            r6 = r11
            r7 = r17
            r11 = 108(0x6c, float:1.51E-43)
            r8 = r18
            r13 = 0
            r9 = r19
            r10 = r23
            r11 = r44
            r12 = r24
            org.telegram.ui.FilterCreateActivity.saveFilterToServer(r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            r2 = 0
            goto L_0x0776
        L_0x0767:
            r25 = r5
            r13 = 0
            org.telegram.messenger.MessagesController r0 = r44.getMessagesController()
            int r1 = r14.folderId
            r2 = 0
            r4 = 0
            r0.reorderPinnedDialogs(r1, r4, r2)
        L_0x0776:
            boolean r0 = r14.searchIsShowed
            if (r0 == 0) goto L_0x078e
            org.telegram.ui.Components.UndoView r0 = r44.getUndoView()
            int r1 = r14.canPinCount
            if (r1 == 0) goto L_0x0785
            r1 = 78
            goto L_0x0787
        L_0x0785:
            r1 = 79
        L_0x0787:
            java.lang.Integer r4 = java.lang.Integer.valueOf(r21)
            r0.showWithAction((long) r2, (int) r1, (java.lang.Object) r4)
        L_0x078e:
            if (r20 == 0) goto L_0x079c
            int r0 = r14.initialDialogsType
            r1 = 10
            if (r0 == r1) goto L_0x0799
            r14.hideFloatingButton(r13)
        L_0x0799:
            r44.scrollToTop()
        L_0x079c:
            r2 = r46
            r0 = 108(0x6c, float:1.51E-43)
            if (r2 == r0) goto L_0x07ac
            r0 = 100
            if (r2 == r0) goto L_0x07ac
            r0 = 102(0x66, float:1.43E-43)
            if (r2 == r0) goto L_0x07ac
            r5 = 1
            goto L_0x07ad
        L_0x07ac:
            r5 = 0
        L_0x07ad:
            r14.hideActionMode(r5)
            return
        L_0x07b1:
            r3 = r0
            r25 = r11
            r15 = r12
            r2 = r13
            r0 = 4
            r1 = 2
            r4 = 3
            r13 = 0
        L_0x07ba:
            java.util.ArrayList r5 = new java.util.ArrayList
            r6 = r45
            r5.<init>(r6)
            org.telegram.messenger.MessagesController r17 = r44.getMessagesController()
            int r7 = r14.canUnarchiveCount
            if (r7 != 0) goto L_0x07cc
            r19 = 1
            goto L_0x07ce
        L_0x07cc:
            r19 = 0
        L_0x07ce:
            r20 = -1
            r21 = 0
            r22 = 0
            r18 = r5
            r17.addDialogToFolder(r18, r19, r20, r21, r22)
            int r7 = r14.canUnarchiveCount
            if (r7 != 0) goto L_0x082a
            android.content.SharedPreferences r7 = org.telegram.messenger.MessagesController.getGlobalMainSettings()
            java.lang.String r8 = "archivehint_l"
            boolean r9 = r7.getBoolean(r8, r13)
            if (r9 != 0) goto L_0x07f0
            boolean r9 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r9 == 0) goto L_0x07ee
            goto L_0x07f0
        L_0x07ee:
            r9 = 0
            goto L_0x07f1
        L_0x07f0:
            r9 = 1
        L_0x07f1:
            if (r9 != 0) goto L_0x0800
            android.content.SharedPreferences$Editor r10 = r7.edit()
            r11 = 1
            android.content.SharedPreferences$Editor r8 = r10.putBoolean(r8, r11)
            r8.commit()
            goto L_0x0801
        L_0x0800:
            r11 = 1
        L_0x0801:
            if (r9 == 0) goto L_0x080c
            int r4 = r5.size()
            if (r4 <= r11) goto L_0x080a
            goto L_0x080b
        L_0x080a:
            r0 = 2
        L_0x080b:
            goto L_0x0815
        L_0x080c:
            int r0 = r5.size()
            if (r0 <= r11) goto L_0x0814
            r0 = 5
            goto L_0x0815
        L_0x0814:
            r0 = 3
        L_0x0815:
            org.telegram.ui.Components.UndoView r17 = r44.getUndoView()
            r18 = 0
            r21 = 0
            org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda24 r1 = new org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda24
            r1.<init>(r14, r5)
            r20 = r0
            r22 = r1
            r17.showWithAction(r18, r20, r21, r22)
            goto L_0x0858
        L_0x082a:
            org.telegram.messenger.MessagesController r1 = r44.getMessagesController()
            int r4 = r14.folderId
            java.util.ArrayList r1 = r1.getDialogs(r4)
            org.telegram.ui.DialogsActivity$ViewPage[] r4 = r14.viewPages
            if (r4 == 0) goto L_0x0858
            boolean r4 = r1.isEmpty()
            if (r4 == 0) goto L_0x0858
            org.telegram.ui.DialogsActivity$ViewPage[] r4 = r14.viewPages
            r4 = r4[r13]
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r4 = r4.listView
            r7 = 0
            r4.setEmptyView(r7)
            org.telegram.ui.DialogsActivity$ViewPage[] r4 = r14.viewPages
            r4 = r4[r13]
            org.telegram.ui.Components.FlickerLoadingView r4 = r4.progressView
            r4.setVisibility(r0)
            r44.finishFragment()
        L_0x0858:
            r14.hideActionMode(r13)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.performSelectedDialogsAction(java.util.ArrayList, int, boolean):void");
    }

    /* renamed from: lambda$performSelectedDialogsAction$21$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2860x34a11321(ArrayList copy) {
        getMessagesController().addDialogToFolder(copy, this.folderId == 0 ? 0 : 1, -1, (ArrayList<TLRPC.TL_inputFolderPeer>) null, 0);
    }

    /* renamed from: lambda$performSelectedDialogsAction$22$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2861xb278cvar_(DialogInterface dialog, int which) {
        presentFragment(new FiltersSetupActivity());
    }

    /* renamed from: lambda$performSelectedDialogsAction$24$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2863xae284724(ArrayList selectedDialogs2, int action, DialogInterface dialog1, int which) {
        if (!selectedDialogs2.isEmpty()) {
            ArrayList<Long> didsCopy = new ArrayList<>(selectedDialogs2);
            getUndoView().showWithAction(didsCopy, action == 102 ? 27 : 26, (Object) null, (Object) null, (Runnable) new DialogsActivity$$ExternalSyntheticLambda21(this, action, didsCopy), (Runnable) null);
            hideActionMode(action == 103);
        }
    }

    /* renamed from: lambda$performSelectedDialogsAction$23$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2862x30508b23(int action, ArrayList didsCopy) {
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

    /* renamed from: lambda$performSelectedDialogsAction$25$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2864x2CLASSNAME(ArrayList selectedDialogs2, boolean report, boolean delete2) {
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

    /* renamed from: lambda$performSelectedDialogsAction$26$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2865xa9d7bvar_(DialogInterface dialog1, int which) {
        getMessagesController().hidePromoDialog();
        hideActionMode(false);
    }

    /* renamed from: lambda$performSelectedDialogsAction$28$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2867xa5873728(int action, TLRPC.Chat chat, long selectedDialog, boolean isBot, boolean param) {
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
        DialogsActivity$$ExternalSyntheticLambda20 dialogsActivity$$ExternalSyntheticLambda20 = r0;
        int selectedDialogIndex2 = selectedDialogIndex;
        DialogsActivity$$ExternalSyntheticLambda20 dialogsActivity$$ExternalSyntheticLambda202 = new DialogsActivity$$ExternalSyntheticLambda20(this, action, selectedDialog, chat, isBot, param);
        undoView2.showWithAction(j, i3, (Runnable) dialogsActivity$$ExternalSyntheticLambda20);
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

    /* renamed from: lambda$performSelectedDialogsAction$29$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2868x235evar_(DialogInterface dialog12) {
        hideActionMode(true);
    }

    /* access modifiers changed from: private */
    /* renamed from: performDeleteOrClearDialogAction */
    public void m2866x27af7b27(int action, long selectedDialog, TLRPC.Chat chat, boolean isBot, boolean revoke) {
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
                    AndroidUtilities.runOnUIThread(new DialogsActivity$$ExternalSyntheticLambda17(this), 200);
                }
            }
            if (!animate) {
                setDialogsListFrozen(false);
            }
        }
    }

    /* renamed from: lambda$pinDialog$30$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2869lambda$pinDialog$30$orgtelegramuiDialogsActivity() {
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
            r9 = 2131624988(0x7f0e041c, float:1.8877171E38)
            java.lang.String r11 = "ClearHistoryCache"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r8.setText(r9)
            goto L_0x0242
        L_0x0234:
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r8 = r0.clearItem
            r9 = 2131624987(0x7f0e041b, float:1.887717E38)
            java.lang.String r11 = "ClearHistory"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r8.setText(r9)
        L_0x0242:
            int r8 = r0.canUnarchiveCount
            if (r8 == 0) goto L_0x0283
            r8 = 2131628200(0x7f0e10a8, float:1.8883686E38)
            java.lang.String r9 = "Unarchive"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r9 = r0.archiveItem
            r11 = 2131165880(0x7var_b8, float:1.794599E38)
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
            r8 = 2131624317(0x7f0e017d, float:1.887581E38)
            java.lang.String r9 = "Archive"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r9 = r0.archiveItem
            r11 = 2131165735(0x7var_, float:1.7945696E38)
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
            r9 = 2147483647(0x7fffffff, float:NaN)
            if (r8 == 0) goto L_0x0328
            int r8 = r8.getVisibility()
            if (r8 != 0) goto L_0x0328
            org.telegram.ui.Components.FilterTabsView r8 = r0.filterTabsView
            int r8 = r8.getCurrentTabId()
            if (r8 != r9) goto L_0x0321
            goto L_0x0328
        L_0x0321:
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r8 = r0.removeFromFolderItem
            r11 = 0
            r8.setVisibility(r11)
            goto L_0x032d
        L_0x0328:
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r8 = r0.removeFromFolderItem
            r8.setVisibility(r5)
        L_0x032d:
            org.telegram.ui.Components.FilterTabsView r8 = r0.filterTabsView
            if (r8 == 0) goto L_0x0352
            int r8 = r8.getVisibility()
            if (r8 != 0) goto L_0x0352
            org.telegram.ui.Components.FilterTabsView r8 = r0.filterTabsView
            int r8 = r8.getCurrentTabId()
            if (r8 != r9) goto L_0x0352
            java.util.ArrayList<java.lang.Long> r8 = r0.selectedDialogs
            java.util.ArrayList r8 = org.telegram.ui.Components.FiltersListBottomSheet.getCanAddDialogFilters(r0, r8)
            boolean r8 = r8.isEmpty()
            if (r8 != 0) goto L_0x0352
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r5 = r0.addToFolderItem
            r8 = 0
            r5.setVisibility(r8)
            goto L_0x0357
        L_0x0352:
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r8 = r0.addToFolderItem
            r8.setVisibility(r5)
        L_0x0357:
            int r5 = r0.canUnmuteCount
            if (r5 == 0) goto L_0x0372
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.muteItem
            r8 = 2131165882(0x7var_ba, float:1.7945994E38)
            r5.setIcon((int) r8)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.muteItem
            r8 = 2131624942(0x7f0e03ee, float:1.8877078E38)
            java.lang.String r9 = "ChatsUnmute"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r5.setContentDescription(r8)
            goto L_0x0388
        L_0x0372:
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.muteItem
            r8 = 2131165811(0x7var_, float:1.794585E38)
            r5.setIcon((int) r8)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.muteItem
            r8 = 2131624922(0x7f0e03da, float:1.8877037E38)
            java.lang.String r9 = "ChatsMute"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r5.setContentDescription(r8)
        L_0x0388:
            int r5 = r0.canReadCount
            if (r5 == 0) goto L_0x039e
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r5 = r0.readItem
            r8 = 2131626239(0x7f0e08ff, float:1.8879709E38)
            java.lang.String r9 = "MarkAsRead"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r9 = 2131165795(0x7var_, float:1.7945817E38)
            r5.setTextAndIcon(r8, r9)
            goto L_0x03af
        L_0x039e:
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r5 = r0.readItem
            r8 = 2131626240(0x7f0e0900, float:1.887971E38)
            java.lang.String r9 = "MarkAsUnread"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r9 = 2131165796(0x7var_, float:1.794582E38)
            r5.setTextAndIcon(r8, r9)
        L_0x03af:
            int r5 = r0.canPinCount
            if (r5 == 0) goto L_0x03d8
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.pinItem
            r8 = 2131165823(0x7var_f, float:1.7945874E38)
            r5.setIcon((int) r8)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.pinItem
            r8 = 2131627187(0x7f0e0cb3, float:1.8881631E38)
            java.lang.String r9 = "PinToTop"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r5.setContentDescription(r8)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r5 = r0.pin2Item
            r8 = 2131625271(0x7f0e0537, float:1.8877745E38)
            java.lang.String r9 = "DialogPin"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r5.setText(r8)
            goto L_0x03fc
        L_0x03d8:
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.pinItem
            r8 = 2131165883(0x7var_bb, float:1.7945996E38)
            r5.setIcon((int) r8)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.pinItem
            r8 = 2131628215(0x7f0e10b7, float:1.8883716E38)
            java.lang.String r9 = "UnpinFromTop"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r5.setContentDescription(r8)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r5 = r0.pin2Item
            r8 = 2131625272(0x7f0e0538, float:1.8877747E38)
            java.lang.String r9 = "DialogUnpin"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r5.setText(r8)
        L_0x03fc:
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
            ofFloat.addUpdateListener(new DialogsActivity$$ExternalSyntheticLambda33(this));
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

    /* renamed from: lambda$showOrUpdateActionMode$31$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2872lambda$showOrUpdateActionMode$31$orgtelegramuiDialogsActivity(ValueAnimator valueAnimator) {
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

    private void updateProxyButton(boolean animated) {
        ActionBarMenuItem actionBarMenuItem;
        if (this.proxyDrawable != null) {
            ActionBarMenuItem actionBarMenuItem2 = this.doneItem;
            if (actionBarMenuItem2 == null || actionBarMenuItem2.getVisibility() != 0) {
                boolean z = false;
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                boolean z2 = preferences.getBoolean("proxy_enabled", false) && !TextUtils.isEmpty(preferences.getString("proxy_ip", ""));
                boolean proxyEnabled = z2;
                if (z2 || (getMessagesController().blockedCountry && !SharedConfig.proxyList.isEmpty())) {
                    if (!this.actionBar.isSearchFieldVisible() && ((actionBarMenuItem = this.doneItem) == null || actionBarMenuItem.getVisibility() != 0)) {
                        this.proxyItem.setVisibility(0);
                    }
                    this.proxyItemVisible = true;
                    ProxyDrawable proxyDrawable2 = this.proxyDrawable;
                    int i = this.currentConnectionState;
                    if (i == 3 || i == 5) {
                        z = true;
                    }
                    proxyDrawable2.setConnected(proxyEnabled, z, animated);
                    return;
                }
                this.proxyItemVisible = false;
                this.proxyItem.setVisibility(8);
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
            }
            ArrayList<Animator> arrayList = new ArrayList<>();
            ActionBarMenuItem actionBarMenuItem4 = this.doneItem;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            float f = 1.0f;
            fArr[0] = show ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem4, property, fArr));
            if (this.proxyItemVisible) {
                ActionBarMenuItem actionBarMenuItem5 = this.proxyItem;
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                fArr2[0] = show ? 0.0f : 1.0f;
                arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem5, property2, fArr2));
            }
            if (this.passcodeItemVisible) {
                ActionBarMenuItem actionBarMenuItem6 = this.passcodeItem;
                Property property3 = View.ALPHA;
                float[] fArr3 = new float[1];
                fArr3[0] = show ? 0.0f : 1.0f;
                arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem6, property3, fArr3));
            }
            ActionBarMenuItem actionBarMenuItem7 = this.searchItem;
            Property property4 = View.ALPHA;
            float[] fArr4 = new float[1];
            if (show) {
                f = 0.0f;
            }
            fArr4[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem7, property4, fArr4));
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
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.commentView, View.TRANSLATION_Y, new float[]{0.0f, (float) this.commentView.getMeasuredHeight()})});
                    animatorSet.setDuration(180);
                    animatorSet.setInterpolator(new DecelerateInterpolator());
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
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
                animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.commentView, View.TRANSLATION_Y, new float[]{(float) this.commentView.getMeasuredHeight(), 0.0f})});
                animatorSet2.setDuration(180);
                animatorSet2.setInterpolator(new DecelerateInterpolator());
                animatorSet2.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
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

    private void askForPermissons(boolean alert) {
        Activity activity = getParentActivity();
        if (activity != null) {
            ArrayList<String> permissons = new ArrayList<>();
            if (getUserConfig().syncContacts && this.askAboutContacts && activity.checkSelfPermission("android.permission.READ_CONTACTS") != 0) {
                if (alert) {
                    AlertDialog create = AlertsCreator.createContactsPermissionDialog(activity, new DialogsActivity$$ExternalSyntheticLambda29(this)).create();
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

    /* renamed from: lambda$askForPermissons$32$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2836lambda$askForPermissons$32$orgtelegramuiDialogsActivity(int param) {
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

    public void didReceivedNotification(int id, int account, Object... args) {
        int i;
        int i2;
        int i3 = id;
        Object[] objArr = args;
        if (i3 == NotificationCenter.dialogsNeedReload) {
            if (this.viewPages != null && !this.dialogsListFrozen) {
                ArrayList<TLRPC.Dialog> dialogs = AccountInstance.getInstance(this.currentAccount).getMessagesController().getDialogs(this.folderId);
                int a = 0;
                while (true) {
                    ViewPage[] viewPageArr = this.viewPages;
                    if (a >= viewPageArr.length) {
                        break;
                    }
                    if (viewPageArr[a].getVisibility() == 0) {
                        int oldItemCount = this.viewPages[a].dialogsAdapter.getCurrentCount();
                        if (this.viewPages[a].dialogsType == 0 && hasHiddenArchive() && this.viewPages[a].listView.getChildCount() == 0) {
                            ((LinearLayoutManager) this.viewPages[a].listView.getLayoutManager()).scrollToPositionWithOffset(1, 0);
                        }
                        if (this.viewPages[a].dialogsAdapter.isDataSetChanged() || objArr.length > 0) {
                            this.viewPages[a].dialogsAdapter.notifyDataSetChanged();
                            if (!(this.viewPages[a].dialogsAdapter.getItemCount() <= oldItemCount || (i = this.initialDialogsType) == 11 || i == 12 || i == 13)) {
                                this.viewPages[a].recyclerItemsEnterAnimator.showItemsAnimated(oldItemCount);
                            }
                        } else {
                            updateVisibleRows(MessagesController.UPDATE_MASK_NEW_MESSAGE);
                            if (!(this.viewPages[a].dialogsAdapter.getItemCount() <= oldItemCount || (i2 = this.initialDialogsType) == 11 || i2 == 12 || i2 == 13)) {
                                this.viewPages[a].recyclerItemsEnterAnimator.showItemsAnimated(oldItemCount);
                            }
                        }
                        try {
                            this.viewPages[a].listView.setEmptyView(this.folderId == 0 ? this.viewPages[a].progressView : null);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                        checkListLoad(this.viewPages[a]);
                    }
                    a++;
                }
                FilterTabsView filterTabsView2 = this.filterTabsView;
                if (filterTabsView2 != null && filterTabsView2.getVisibility() == 0) {
                    this.filterTabsView.checkTabsCounter();
                }
            }
        } else if (i3 == NotificationCenter.dialogsUnreadCounterChanged) {
            FilterTabsView filterTabsView3 = this.filterTabsView;
            if (filterTabsView3 != null && filterTabsView3.getVisibility() == 0) {
                this.filterTabsView.notifyTabCounterChanged(Integer.MAX_VALUE);
            }
        } else if (i3 == NotificationCenter.emojiLoaded) {
            updateVisibleRows(0);
            FilterTabsView filterTabsView4 = this.filterTabsView;
            if (filterTabsView4 != null) {
                filterTabsView4.getTabsContainer().invalidateViews();
            }
        } else if (i3 == NotificationCenter.closeSearchByActiveAction) {
            if (this.actionBar != null) {
                this.actionBar.closeSearchField();
            }
        } else if (i3 == NotificationCenter.proxySettingsChanged) {
            updateProxyButton(false);
        } else if (i3 == NotificationCenter.updateInterfaces) {
            Integer mask = (Integer) objArr[0];
            updateVisibleRows(mask.intValue());
            FilterTabsView filterTabsView5 = this.filterTabsView;
            if (!(filterTabsView5 == null || filterTabsView5.getVisibility() != 0 || (mask.intValue() & MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE) == 0)) {
                this.filterTabsView.checkTabsCounter();
            }
            if (this.viewPages != null) {
                for (int a2 = 0; a2 < this.viewPages.length; a2++) {
                    if ((mask.intValue() & MessagesController.UPDATE_MASK_STATUS) != 0) {
                        this.viewPages[a2].dialogsAdapter.sortOnlineContacts(true);
                    }
                }
            }
        } else if (i3 == NotificationCenter.appDidLogout) {
            dialogsLoaded[this.currentAccount] = false;
        } else if (i3 == NotificationCenter.encryptedChatUpdated) {
            updateVisibleRows(0);
        } else if (i3 == NotificationCenter.contactsDidLoad) {
            if (this.viewPages != null && !this.dialogsListFrozen) {
                boolean updateVisibleRows = false;
                int a3 = 0;
                while (true) {
                    ViewPage[] viewPageArr2 = this.viewPages;
                    if (a3 >= viewPageArr2.length) {
                        break;
                    }
                    if (!viewPageArr2[a3].isDefaultDialogType() || getMessagesController().getDialogs(this.folderId).size() > 10) {
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
        } else if (i3 == NotificationCenter.openedChatChanged) {
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
        } else if (i3 == NotificationCenter.notificationsSettingsUpdated) {
            updateVisibleRows(0);
        } else if (i3 == NotificationCenter.messageReceivedByAck || i3 == NotificationCenter.messageReceivedByServer || i3 == NotificationCenter.messageSendError) {
            updateVisibleRows(MessagesController.UPDATE_MASK_SEND_STATE);
        } else if (i3 == NotificationCenter.didSetPasscode) {
            updatePasscodeButton();
        } else if (i3 == NotificationCenter.needReloadRecentDialogsSearch) {
            SearchViewPager searchViewPager2 = this.searchViewPager;
            if (searchViewPager2 != null && searchViewPager2.dialogsSearchAdapter != null) {
                this.searchViewPager.dialogsSearchAdapter.loadRecentSearch();
            }
        } else if (i3 == NotificationCenter.replyMessagesDidLoad) {
            updateVisibleRows(MessagesController.UPDATE_MASK_MESSAGE_TEXT);
        } else if (i3 == NotificationCenter.reloadHints) {
            SearchViewPager searchViewPager3 = this.searchViewPager;
            if (searchViewPager3 != null && searchViewPager3.dialogsSearchAdapter != null) {
                this.searchViewPager.dialogsSearchAdapter.notifyDataSetChanged();
            }
        } else if (i3 == NotificationCenter.didUpdateConnectionState) {
            int state = AccountInstance.getInstance(account).getConnectionsManager().getConnectionState();
            if (this.currentConnectionState != state) {
                this.currentConnectionState = state;
                updateProxyButton(true);
            }
        } else if (i3 == NotificationCenter.needDeleteDialog) {
            if (this.fragmentView != null && !this.isPaused) {
                long dialogId = ((Long) objArr[0]).longValue();
                Runnable deleteRunnable = new DialogsActivity$$ExternalSyntheticLambda25(this, (TLRPC.Chat) objArr[2], dialogId, ((Boolean) objArr[3]).booleanValue(), (TLRPC.User) objArr[1]);
                if (this.undoView[0] != null) {
                    getUndoView().showWithAction(dialogId, 1, deleteRunnable);
                } else {
                    deleteRunnable.run();
                }
            }
        } else if (i3 == NotificationCenter.folderBecomeEmpty) {
            int fid = ((Integer) objArr[0]).intValue();
            int i4 = this.folderId;
            if (i4 == fid && i4 != 0) {
                finishFragment();
            }
        } else if (i3 == NotificationCenter.dialogFiltersUpdated) {
            updateFilterTabs(true, true);
        } else if (i3 == NotificationCenter.filterSettingsUpdated) {
            showFiltersHint();
        } else if (i3 == NotificationCenter.newSuggestionsAvailable) {
            showNextSupportedSuggestion();
        } else if (i3 == NotificationCenter.messagesDeleted) {
            if (this.searchIsShowed && this.searchViewPager != null) {
                this.searchViewPager.messagesDeleted(((Long) objArr[1]).longValue(), (ArrayList) objArr[0]);
            }
        } else if (i3 == NotificationCenter.didClearDatabase) {
            if (this.viewPages != null) {
                int a5 = 0;
                while (true) {
                    ViewPage[] viewPageArr4 = this.viewPages;
                    if (a5 < viewPageArr4.length) {
                        viewPageArr4[a5].dialogsAdapter.didDatabaseCleared();
                        a5++;
                    } else {
                        return;
                    }
                }
            }
        } else if (i3 == NotificationCenter.appUpdateAvailable) {
            updateMenuButton(true);
        } else if (i3 == NotificationCenter.fileLoaded || i3 == NotificationCenter.fileLoadFailed || i3 == NotificationCenter.fileLoadProgressChanged) {
            String name = (String) objArr[0];
            if (SharedConfig.isAppUpdateAvailable() && FileLoader.getAttachFileName(SharedConfig.pendingAppUpdate.document).equals(name)) {
                updateMenuButton(true);
            }
        } else if (i3 == NotificationCenter.onDatabaseMigration) {
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
        }
    }

    /* renamed from: lambda$didReceivedNotification$33$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2846xe53var_(TLRPC.Chat chat, long dialogId, boolean revoke, TLRPC.User user) {
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

    /* renamed from: lambda$showSuggestion$34$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2874lambda$showSuggestion$34$orgtelegramuiDialogsActivity(DialogInterface dialog, int which) {
        presentFragment(new PrivacySettingsActivity());
        AndroidUtilities.scrollToFragmentRow(this.parentLayout, "newChatsRow");
    }

    /* renamed from: lambda$showSuggestion$35$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2875lambda$showSuggestion$35$orgtelegramuiDialogsActivity(DialogInterface dialog) {
        onSuggestionDismiss();
    }

    private void showFiltersHint() {
        if (!this.askingForPermissions && getMessagesController().dialogFiltersLoaded && getMessagesController().showFiltersTooltip && this.filterTabsView != null && getMessagesController().dialogFilters.isEmpty() && !this.isPaused && getUserConfig().filtersLoaded && !this.inPreviewMode) {
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            if (!preferences.getBoolean("filterhint", false)) {
                preferences.edit().putBoolean("filterhint", true).commit();
                AndroidUtilities.runOnUIThread(new DialogsActivity$$ExternalSyntheticLambda19(this), 1000);
            }
        }
    }

    /* renamed from: lambda$showFiltersHint$36$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2870lambda$showFiltersHint$36$orgtelegramuiDialogsActivity() {
        presentFragment(new FiltersSetupActivity());
    }

    /* renamed from: lambda$showFiltersHint$37$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2871lambda$showFiltersHint$37$orgtelegramuiDialogsActivity() {
        getUndoView().showWithAction(0, 15, (Runnable) null, new DialogsActivity$$ExternalSyntheticLambda18(this));
    }

    private void setDialogsListFrozen(boolean frozen, boolean notify) {
        if (this.viewPages != null && this.dialogsListFrozen != frozen) {
            if (frozen) {
                this.frozenDialogsList = new ArrayList<>(getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, false));
            } else {
                this.frozenDialogsList = null;
            }
            this.dialogsListFrozen = frozen;
            this.viewPages[0].dialogsAdapter.setDialogsListFrozen(frozen);
            if (!frozen && notify) {
                this.viewPages[0].dialogsAdapter.notifyDataSetChanged();
            }
        }
    }

    /* access modifiers changed from: private */
    public void setDialogsListFrozen(boolean frozen) {
        setDialogsListFrozen(frozen, true);
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
            return messagesController.dialogsCanAddUsers;
        }
        if (dialogsType == 3) {
            return messagesController.dialogsForward;
        }
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
            valueAnimator.addUpdateListener(new DialogsActivity$$ExternalSyntheticLambda22(this));
            animatorSet.playTogether(new Animator[]{valueAnimator});
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(this.floatingInterpolator);
            this.floatingButtonContainer.setClickable(!hide);
            animatorSet.start();
        }
    }

    /* renamed from: lambda$hideFloatingButton$38$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2854lambda$hideFloatingButton$38$orgtelegramuiDialogsActivity(ValueAnimator animation) {
        this.floatingButtonHideProgress = ((Float) animation.getAnimatedValue()).floatValue();
        this.floatingButtonTranslation = ((float) AndroidUtilities.dp(100.0f)) * this.floatingButtonHideProgress;
        updateFloatingButtonOffset();
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

    /* access modifiers changed from: private */
    public void didSelectResult(long dialogId, boolean useAlert, boolean param) {
        TLRPC.Chat chat;
        TLRPC.User user;
        String buttonText;
        String message;
        String title;
        long j = dialogId;
        if (this.addToGroupAlertString == null && this.checkCanWrite) {
            if (DialogObject.isChatDialog(dialogId)) {
                TLRPC.Chat chat2 = getMessagesController().getChat(Long.valueOf(-j));
                if (ChatObject.isChannel(chat2) && !chat2.megagroup && (this.cantSendToChannels || !ChatObject.isCanWriteToChannel(-j, this.currentAccount) || this.hasPoll == 2)) {
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
            } else if (DialogObject.isEncryptedDialog(dialogId) && (this.hasPoll != 0 || this.hasInvoice)) {
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
            AlertDialog.Builder builder3 = new AlertDialog.Builder((Context) getParentActivity());
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
                TLRPC.Chat chat3 = getMessagesController().getChat(Long.valueOf(-j));
                if (chat3 != null) {
                    if (this.addToGroupAlertString != null) {
                        title = LocaleController.getString("AddToTheGroupAlertTitle", NUM);
                        message = LocaleController.formatStringSimple(this.addToGroupAlertString, chat3.title);
                        buttonText = LocaleController.getString("Add", NUM);
                    } else {
                        title = LocaleController.getString("SendMessageTitle", NUM);
                        message = LocaleController.formatStringSimple(this.selectAlertStringGroup, chat3.title);
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
            builder3.setTitle(title);
            builder3.setMessage(AndroidUtilities.replaceTags(message));
            builder3.setPositiveButton(buttonText, new DialogsActivity$$ExternalSyntheticLambda2(this, j));
            builder3.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder3.create());
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
                TLRPC.Chat chat4 = getMessagesController().getChat(Long.valueOf(-j));
                if (!ChatObject.hasAdminRights(chat4) || !ChatObject.canChangeChatInfo(chat4)) {
                    getUndoView().showWithAction(j, 46, (Runnable) null);
                    return;
                } else {
                    user = null;
                    chat = chat4;
                }
            }
            AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
            TLRPC.TL_messages_checkHistoryImportPeer req = new TLRPC.TL_messages_checkHistoryImportPeer();
            req.peer = getMessagesController().getInputPeer(j);
            ConnectionsManager connectionsManager = getConnectionsManager();
            DialogsActivity$$ExternalSyntheticLambda31 dialogsActivity$$ExternalSyntheticLambda31 = r1;
            TLRPC.TL_messages_checkHistoryImportPeer req2 = req;
            AlertDialog progressDialog2 = progressDialog;
            DialogsActivity$$ExternalSyntheticLambda31 dialogsActivity$$ExternalSyntheticLambda312 = new DialogsActivity$$ExternalSyntheticLambda31(this, progressDialog, user, chat, dialogId, param, req2);
            connectionsManager.sendRequest(req2, dialogsActivity$$ExternalSyntheticLambda31);
            try {
                progressDialog2.showDelayed(300);
            } catch (Exception e) {
            }
        }
    }

    /* renamed from: lambda$didSelectResult$41$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2849lambda$didSelectResult$41$orgtelegramuiDialogsActivity(AlertDialog progressDialog, TLRPC.User user, TLRPC.Chat chat, long dialogId, boolean param, TLRPC.TL_messages_checkHistoryImportPeer req, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new DialogsActivity$$ExternalSyntheticLambda26(this, progressDialog, response, user, chat, dialogId, param, error, req));
    }

    /* renamed from: lambda$didSelectResult$40$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2848lambda$didSelectResult$40$orgtelegramuiDialogsActivity(AlertDialog progressDialog, TLObject response, TLRPC.User user, TLRPC.Chat chat, long dialogId, boolean param, TLRPC.TL_error error, TLRPC.TL_messages_checkHistoryImportPeer req) {
        TLRPC.TL_error tL_error = error;
        TLRPC.TL_messages_checkHistoryImportPeer tL_messages_checkHistoryImportPeer = req;
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        this.checkingImportDialog = false;
        if (response != null) {
            AlertsCreator.createImportDialogAlert(this, this.arguments.getString("importTitle"), ((TLRPC.TL_messages_checkedHistoryImportPeer) response).confirm_text, user, chat, new DialogsActivity$$ExternalSyntheticLambda23(this, dialogId, param));
            return;
        }
        long j = dialogId;
        boolean z = param;
        AlertsCreator.processError(this.currentAccount, tL_error, this, tL_messages_checkHistoryImportPeer, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(dialogId), tL_messages_checkHistoryImportPeer, tL_error);
    }

    /* renamed from: lambda$didSelectResult$39$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2847lambda$didSelectResult$39$orgtelegramuiDialogsActivity(long dialogId, boolean param) {
        setDialogsListFrozen(true);
        ArrayList<Long> dids = new ArrayList<>();
        dids.add(Long.valueOf(dialogId));
        this.delegate.didSelectDialogs(this, dids, (CharSequence) null, param);
    }

    /* renamed from: lambda$didSelectResult$42$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2850lambda$didSelectResult$42$orgtelegramuiDialogsActivity(long dialogId, DialogInterface dialogInterface, int i) {
        didSelectResult(dialogId, false, false);
    }

    public RLottieImageView getFloatingButton() {
        return this.floatingButton;
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        RecyclerListView list;
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new DialogsActivity$$ExternalSyntheticLambda32(this);
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
                arrayList.add(new ThemeDescription(list, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_groupDrawable, Theme.dialogs_broadcastDrawable, Theme.dialogs_botDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_nameIcon"));
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
            arrayList.add(new ThemeDescription((View) this.commentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"sendButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelSend"));
        }
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate5 = cellDelegate;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate5, "actionBarTipBackground"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate6 = cellDelegate;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate6, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate5, "player_time"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate6, "chat_messagePanelCursor"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate5, "avatar_actionBarIconBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate6, "groupcreate_spanBackground"));
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

    /* renamed from: lambda$getThemeDescriptions$43$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2852lambda$getThemeDescriptions$43$orgtelegramuiDialogsActivity() {
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
                actionBarMenuSubItemArr[a4].setColors(Theme.getColor("actionBarDefaultSubmenuItem"), Theme.getColor("actionBarDefaultSubmenuItemIcon"));
                this.scrimPopupWindowItems[a4].setSelectorColor(Theme.getColor("dialogButtonSelector"));
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
        setSearchAnimationProgress(this.searchAnimationProgress);
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
        ofFloat2.addUpdateListener(new DialogsActivity$$ExternalSyntheticLambda0(this));
        this.slideBackTransitionAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.slideBackTransitionAnimator.setDuration((long) ((int) (((float) Math.max((int) ((200.0f / ((float) getLayoutContainer().getMeasuredWidth())) * distanceToMove), 80)) * 1.2f)));
        this.slideBackTransitionAnimator.start();
        return this.slideBackTransitionAnimator;
    }

    /* renamed from: lambda$getCustomSlideTransition$44$org-telegram-ui-DialogsActivity  reason: not valid java name */
    public /* synthetic */ void m2851x6544f4a8(ValueAnimator valueAnimator) {
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
        if (this.searchViewPager.getTabsView().getCurrentTabId() != i) {
            this.searchViewPager.getTabsView().scrollToTab(i, i);
        }
    }
}
