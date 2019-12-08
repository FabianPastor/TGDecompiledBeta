package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ItemTouchHelper.Callback;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScrollerMiddle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.State;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.XiaomiUtilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Dialog;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.TL_dialogFolder;
import org.telegram.tgnet.TLRPC.TL_userEmpty;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.MenuDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.DialogsAdapter;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
import org.telegram.ui.Adapters.DialogsSearchAdapter.DialogsSearchAdapterDelegate;
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
import org.telegram.ui.Cells.HintDialogCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedArrowDrawable;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate;
import org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate.-CC;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.DialogsItemAnimator;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.FragmentContextView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.PacmanAnimation;
import org.telegram.ui.Components.ProxyDrawable;
import org.telegram.ui.Components.PullForegroundDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.UndoView;

public class DialogsActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int ARCHIVE_ITEM_STATE_HIDDEN = 2;
    private static final int ARCHIVE_ITEM_STATE_PINNED = 0;
    private static final int ARCHIVE_ITEM_STATE_SHOWED = 1;
    private static final int archive = 105;
    private static final int clear = 103;
    private static final int delete = 102;
    public static boolean[] dialogsLoaded = new boolean[3];
    private static ArrayList<Dialog> frozenDialogsList = null;
    private static final int mute = 104;
    private static final int pin = 100;
    private static final int read = 101;
    public static float viewOffset;
    private ArrayList<View> actionModeViews = new ArrayList();
    private String addToGroupAlertString;
    private float additionalFloatingTranslation;
    private boolean allowMoving;
    private boolean allowSwipeDuringCurrentTouch;
    private boolean allowSwitchAccount;
    private ActionBarMenuSubItem archiveItem;
    private int archivePullViewState;
    private AnimatedArrowDrawable arrowDrawable;
    private boolean askAboutContacts = true;
    private BackDrawable backDrawable;
    private int canClearCacheCount;
    private int canMuteCount;
    private int canPinCount;
    private int canReadCount;
    private boolean canShowHiddenArchive;
    private int canUnmuteCount;
    private boolean cantSendToChannels;
    private boolean checkCanWrite;
    private boolean checkPermission = true;
    private ActionBarMenuSubItem clearItem;
    private boolean closeSearchFieldOnHide;
    private ChatActivityEnterView commentView;
    private int currentConnectionState;
    private DialogsActivityDelegate delegate;
    private ActionBarMenuItem deleteItem;
    private int dialogChangeFinished;
    private int dialogInsertFinished;
    private int dialogRemoveFinished;
    private DialogsAdapter dialogsAdapter;
    private DialogsItemAnimator dialogsItemAnimator;
    private boolean dialogsListFrozen;
    private DialogsSearchAdapter dialogsSearchAdapter;
    private int dialogsType;
    private ImageView floatingButton;
    private FrameLayout floatingButtonContainer;
    private float floatingButtonHideProgress;
    private float floatingButtonTranslation;
    private boolean floatingHidden;
    private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();
    private int folderId;
    private ItemTouchHelper itemTouchhelper;
    private int lastItemsCount;
    private LinearLayoutManager layoutManager;
    private DialogsRecyclerView listView;
    private MenuDrawable menuDrawable;
    private int messagesCount;
    private DialogCell movingView;
    private boolean movingWas;
    private ActionBarMenuItem muteItem;
    private boolean onlySelect;
    private long openedDialogId;
    private PacmanAnimation pacmanAnimation;
    private ActionBarMenuItem passcodeItem;
    private AlertDialog permissionDialog;
    private ActionBarMenuItem pinItem;
    private int prevPosition;
    private int prevTop;
    private RadialProgressView progressView;
    private ProxyDrawable proxyDrawable;
    private ActionBarMenuItem proxyItem;
    private boolean proxyItemVisisble;
    public PullForegroundDrawable pullForegroundDrawable;
    private ActionBarMenuSubItem readItem;
    private boolean resetDelegate = true;
    private boolean scrollUpdated;
    private boolean scrollingManually;
    private long searchDialogId;
    private EmptyTextProgressView searchEmptyView;
    private TLObject searchObject;
    private String searchString;
    private boolean searchWas;
    private boolean searching;
    private String selectAlertString;
    private String selectAlertStringGroup;
    private NumberTextView selectedDialogsCountTextView;
    private RecyclerView sideMenu;
    private DialogCell slidingView;
    private long startArchivePullingTime;
    private SwipeController swipeController;
    private ActionBarMenuItem switchItem;
    private UndoView[] undoView = new UndoView[2];
    private boolean waitingForScrollFinished;

    public interface DialogsActivityDelegate {
        void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList<Long> arrayList, CharSequence charSequence, boolean z);
    }

    private class ContentView extends SizeNotifierFrameLayout {
        private int inputFieldHeight;

        public ContentView(Context context) {
            super(context);
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            int size = MeasureSpec.getSize(i);
            int size2 = MeasureSpec.getSize(i2);
            setMeasuredDimension(size, size2);
            size2 -= getPaddingTop();
            measureChildWithMargins(DialogsActivity.this.actionBar, i, 0, i2, 0);
            int keyboardHeight = getKeyboardHeight();
            int childCount = getChildCount();
            int i3 = 0;
            if (DialogsActivity.this.commentView != null) {
                measureChildWithMargins(DialogsActivity.this.commentView, i, 0, i2, 0);
                Object tag = DialogsActivity.this.commentView.getTag();
                if (tag == null || !tag.equals(Integer.valueOf(2))) {
                    this.inputFieldHeight = 0;
                } else {
                    if (keyboardHeight <= AndroidUtilities.dp(20.0f) && !AndroidUtilities.isInMultiwindow) {
                        size2 -= DialogsActivity.this.commentView.getEmojiPadding();
                    }
                    this.inputFieldHeight = DialogsActivity.this.commentView.getMeasuredHeight();
                }
            }
            while (i3 < childCount) {
                View childAt = getChildAt(i3);
                if (!(childAt == null || childAt.getVisibility() == 8 || childAt == DialogsActivity.this.commentView || childAt == DialogsActivity.this.actionBar)) {
                    if (childAt == DialogsActivity.this.listView || childAt == DialogsActivity.this.progressView || childAt == DialogsActivity.this.searchEmptyView) {
                        childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), (size2 - this.inputFieldHeight) + AndroidUtilities.dp(2.0f)), NUM));
                    } else if (DialogsActivity.this.commentView == null || !DialogsActivity.this.commentView.isPopupView(childAt)) {
                        measureChildWithMargins(childAt, i, 0, i2, 0);
                    } else if (!AndroidUtilities.isInMultiwindow) {
                        childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, NUM));
                    } else if (AndroidUtilities.isTablet()) {
                        childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(320.0f), ((size2 - this.inputFieldHeight) - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                    } else {
                        childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(((size2 - this.inputFieldHeight) - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                    }
                }
                i3++;
            }
        }

        /* Access modifiers changed, original: protected */
        /* JADX WARNING: Removed duplicated region for block: B:47:0x00e1  */
        /* JADX WARNING: Removed duplicated region for block: B:46:0x00ca  */
        /* JADX WARNING: Removed duplicated region for block: B:38:0x00a6  */
        /* JADX WARNING: Removed duplicated region for block: B:31:0x008c  */
        /* JADX WARNING: Removed duplicated region for block: B:46:0x00ca  */
        /* JADX WARNING: Removed duplicated region for block: B:47:0x00e1  */
        public void onLayout(boolean r11, int r12, int r13, int r14, int r15) {
            /*
            r10 = this;
            r11 = r10.getChildCount();
            r0 = org.telegram.ui.DialogsActivity.this;
            r0 = r0.commentView;
            if (r0 == 0) goto L_0x0017;
        L_0x000c:
            r0 = org.telegram.ui.DialogsActivity.this;
            r0 = r0.commentView;
            r0 = r0.getTag();
            goto L_0x0018;
        L_0x0017:
            r0 = 0;
        L_0x0018:
            r1 = 2;
            r2 = 0;
            if (r0 == 0) goto L_0x0041;
        L_0x001c:
            r3 = java.lang.Integer.valueOf(r1);
            r0 = r0.equals(r3);
            if (r0 == 0) goto L_0x0041;
        L_0x0026:
            r0 = r10.getKeyboardHeight();
            r3 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
            r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
            if (r0 > r3) goto L_0x0041;
        L_0x0032:
            r0 = org.telegram.messenger.AndroidUtilities.isInMultiwindow;
            if (r0 != 0) goto L_0x0041;
        L_0x0036:
            r0 = org.telegram.ui.DialogsActivity.this;
            r0 = r0.commentView;
            r0 = r0.getEmojiPadding();
            goto L_0x0042;
        L_0x0041:
            r0 = 0;
        L_0x0042:
            r10.setBottomClip(r0);
        L_0x0045:
            if (r2 >= r11) goto L_0x00f4;
        L_0x0047:
            r3 = r10.getChildAt(r2);
            r4 = r3.getVisibility();
            r5 = 8;
            if (r4 != r5) goto L_0x0055;
        L_0x0053:
            goto L_0x00f0;
        L_0x0055:
            r4 = r3.getLayoutParams();
            r4 = (android.widget.FrameLayout.LayoutParams) r4;
            r5 = r3.getMeasuredWidth();
            r6 = r3.getMeasuredHeight();
            r7 = r4.gravity;
            r8 = -1;
            if (r7 != r8) goto L_0x006a;
        L_0x0068:
            r7 = 51;
        L_0x006a:
            r8 = r7 & 7;
            r7 = r7 & 112;
            r8 = r8 & 7;
            r9 = 1;
            if (r8 == r9) goto L_0x007e;
        L_0x0073:
            r9 = 5;
            if (r8 == r9) goto L_0x0079;
        L_0x0076:
            r8 = r4.leftMargin;
            goto L_0x0088;
        L_0x0079:
            r8 = r14 - r5;
            r9 = r4.rightMargin;
            goto L_0x0087;
        L_0x007e:
            r8 = r14 - r12;
            r8 = r8 - r5;
            r8 = r8 / r1;
            r9 = r4.leftMargin;
            r8 = r8 + r9;
            r9 = r4.rightMargin;
        L_0x0087:
            r8 = r8 - r9;
        L_0x0088:
            r9 = 16;
            if (r7 == r9) goto L_0x00a6;
        L_0x008c:
            r9 = 48;
            if (r7 == r9) goto L_0x009e;
        L_0x0090:
            r9 = 80;
            if (r7 == r9) goto L_0x0097;
        L_0x0094:
            r4 = r4.topMargin;
            goto L_0x00b2;
        L_0x0097:
            r7 = r15 - r0;
            r7 = r7 - r13;
            r7 = r7 - r6;
            r4 = r4.bottomMargin;
            goto L_0x00b0;
        L_0x009e:
            r4 = r4.topMargin;
            r7 = r10.getPaddingTop();
            r4 = r4 + r7;
            goto L_0x00b2;
        L_0x00a6:
            r7 = r15 - r0;
            r7 = r7 - r13;
            r7 = r7 - r6;
            r7 = r7 / r1;
            r9 = r4.topMargin;
            r7 = r7 + r9;
            r4 = r4.bottomMargin;
        L_0x00b0:
            r4 = r7 - r4;
        L_0x00b2:
            r7 = org.telegram.ui.DialogsActivity.this;
            r7 = r7.commentView;
            if (r7 == 0) goto L_0x00eb;
        L_0x00ba:
            r7 = org.telegram.ui.DialogsActivity.this;
            r7 = r7.commentView;
            r7 = r7.isPopupView(r3);
            if (r7 == 0) goto L_0x00eb;
        L_0x00c6:
            r4 = org.telegram.messenger.AndroidUtilities.isInMultiwindow;
            if (r4 == 0) goto L_0x00e1;
        L_0x00ca:
            r4 = org.telegram.ui.DialogsActivity.this;
            r4 = r4.commentView;
            r4 = r4.getTop();
            r7 = r3.getMeasuredHeight();
            r4 = r4 - r7;
            r7 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
            r4 = r4 + r7;
            goto L_0x00eb;
        L_0x00e1:
            r4 = org.telegram.ui.DialogsActivity.this;
            r4 = r4.commentView;
            r4 = r4.getBottom();
        L_0x00eb:
            r5 = r5 + r8;
            r6 = r6 + r4;
            r3.layout(r8, r4, r5, r6);
        L_0x00f0:
            r2 = r2 + 1;
            goto L_0x0045;
        L_0x00f4:
            r10.notifyHeightChanged();
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity$ContentView.onLayout(boolean, int, int, int, int):void");
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            int actionMasked = motionEvent.getActionMasked();
            if ((actionMasked == 1 || actionMasked == 3) && DialogsActivity.this.actionBar.isActionModeShowed()) {
                DialogsActivity.this.allowMoving = true;
            }
            return super.onInterceptTouchEvent(motionEvent);
        }
    }

    class SwipeController extends Callback {
        private RectF buttonInstance;
        private ViewHolder currentItemViewHolder;
        private boolean swipeFolderBack;
        private boolean swipingFolder;

        public float getSwipeEscapeVelocity(float f) {
            return 3500.0f;
        }

        public float getSwipeThreshold(ViewHolder viewHolder) {
            return 0.3f;
        }

        public float getSwipeVelocityThreshold(float f) {
            return Float.MAX_VALUE;
        }

        SwipeController() {
        }

        public int getMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder) {
            if (!DialogsActivity.this.waitingForDialogsAnimationEnd() && (DialogsActivity.this.parentLayout == null || !DialogsActivity.this.parentLayout.isInPreviewMode())) {
                if (this.swipingFolder && this.swipeFolderBack) {
                    this.swipingFolder = false;
                    return 0;
                } else if (!DialogsActivity.this.onlySelect && DialogsActivity.this.dialogsType == 0 && DialogsActivity.this.slidingView == null && recyclerView.getAdapter() == DialogsActivity.this.dialogsAdapter) {
                    View view = viewHolder.itemView;
                    if (view instanceof DialogCell) {
                        DialogCell dialogCell = (DialogCell) view;
                        long dialogId = dialogCell.getDialogId();
                        if (DialogsActivity.this.actionBar.isActionModeShowed()) {
                            Dialog dialog = (Dialog) DialogsActivity.this.getMessagesController().dialogs_dict.get(dialogId);
                            if (!DialogsActivity.this.allowMoving || dialog == null || !dialog.pinned || DialogObject.isFolderDialogId(dialogId)) {
                                return 0;
                            }
                            DialogsActivity.this.movingView = (DialogCell) viewHolder.itemView;
                            DialogsActivity.this.movingView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                            return Callback.makeMovementFlags(3, 0);
                        } else if (!(!DialogsActivity.this.allowSwipeDuringCurrentTouch || dialogId == ((long) DialogsActivity.this.getUserConfig().clientUserId) || dialogId == 777000 || DialogsActivity.this.getMessagesController().isProxyDialog(dialogId, false))) {
                            this.swipeFolderBack = false;
                            boolean z = SharedConfig.archiveHidden && DialogObject.isFolderDialogId(dialogCell.getDialogId());
                            this.swipingFolder = z;
                            dialogCell.setSliding(true);
                            return Callback.makeMovementFlags(0, 4);
                        }
                    }
                }
            }
            return 0;
        }

        public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder viewHolder2) {
            View view = viewHolder2.itemView;
            if (!(view instanceof DialogCell)) {
                return false;
            }
            long dialogId = ((DialogCell) view).getDialogId();
            Dialog dialog = (Dialog) DialogsActivity.this.getMessagesController().dialogs_dict.get(dialogId);
            if (dialog == null || !dialog.pinned || DialogObject.isFolderDialogId(dialogId)) {
                return false;
            }
            DialogsActivity.this.dialogsAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), viewHolder2.getAdapterPosition());
            DialogsActivity.this.updateDialogIndices();
            DialogsActivity.this.movingWas = true;
            return true;
        }

        public int convertToAbsoluteDirection(int i, int i2) {
            if (this.swipeFolderBack) {
                return 0;
            }
            return super.convertToAbsoluteDirection(i, i2);
        }

        public void onSwiped(ViewHolder viewHolder, int i) {
            if (viewHolder != null) {
                DialogCell dialogCell = (DialogCell) viewHolder.itemView;
                DialogsActivity dialogsActivity;
                if (DialogObject.isFolderDialogId(dialogCell.getDialogId())) {
                    SharedConfig.toggleArchiveHidden();
                    boolean z = false;
                    if (SharedConfig.archiveHidden) {
                        DialogsActivity.this.waitingForScrollFinished = true;
                        DialogsActivity.this.listView.smoothScrollBy(0, dialogCell.getMeasuredHeight() + dialogCell.getTop(), CubicBezierInterpolator.EASE_OUT);
                        DialogsActivity.this.getUndoView().showWithAction(0, 6, null, null);
                    }
                    DialogsActivity.this.archivePullViewState = SharedConfig.archiveHidden ? 2 : 0;
                    dialogsActivity = DialogsActivity.this;
                    PullForegroundDrawable pullForegroundDrawable = dialogsActivity.pullForegroundDrawable;
                    if (pullForegroundDrawable != null) {
                        if (dialogsActivity.archivePullViewState != 0) {
                            z = true;
                        }
                        pullForegroundDrawable.setWillDraw(z);
                    }
                    return;
                }
                DialogsActivity.this.slidingView = dialogCell;
                int adapterPosition = viewHolder.getAdapterPosition();
                -$$Lambda$DialogsActivity$SwipeController$p1WvioWe4QIXpl_h9JruXCBarGg -__lambda_dialogsactivity_swipecontroller_p1wviowe4qixpl_h9jruxcbargg = new -$$Lambda$DialogsActivity$SwipeController$p1WvioWe4QIXpl_h9JruXCBarGg(this, DialogsActivity.this.dialogsAdapter.fixPosition(adapterPosition), DialogsActivity.this.dialogsAdapter.getItemCount(), adapterPosition);
                DialogsActivity.this.setDialogsListFrozen(true);
                if (Utilities.random.nextInt(1000) == 1) {
                    if (DialogsActivity.this.pacmanAnimation == null) {
                        dialogsActivity = DialogsActivity.this;
                        dialogsActivity.pacmanAnimation = new PacmanAnimation(dialogsActivity.listView);
                    }
                    DialogsActivity.this.pacmanAnimation.setFinishRunnable(-__lambda_dialogsactivity_swipecontroller_p1wviowe4qixpl_h9jruxcbargg);
                    DialogsActivity.this.pacmanAnimation.start();
                } else {
                    -__lambda_dialogsactivity_swipecontroller_p1wviowe4qixpl_h9jruxcbargg.run();
                }
            } else {
                DialogsActivity.this.slidingView = null;
            }
        }

        public /* synthetic */ void lambda$onSwiped$1$DialogsActivity$SwipeController(int i, int i2, int i3) {
            Dialog dialog = (Dialog) DialogsActivity.frozenDialogsList.remove(i);
            int i4 = dialog.pinnedNum;
            DialogsActivity.this.slidingView = null;
            DialogsActivity.this.listView.invalidate();
            int i5 = 0;
            int addDialogToFolder = DialogsActivity.this.getMessagesController().addDialogToFolder(dialog.id, DialogsActivity.this.folderId == 0 ? 1 : 0, -1, 0);
            int findLastVisibleItemPosition = DialogsActivity.this.layoutManager.findLastVisibleItemPosition();
            if (findLastVisibleItemPosition == i2 - 1) {
                DialogsActivity.this.layoutManager.findViewByPosition(findLastVisibleItemPosition).requestLayout();
            }
            if (!(addDialogToFolder == 2 && i3 == 0)) {
                DialogsActivity.this.dialogsItemAnimator.prepareForRemove();
                DialogsActivity.this.lastItemsCount = DialogsActivity.this.lastItemsCount - 1;
                DialogsActivity.this.dialogsAdapter.notifyItemRemoved(i3);
                DialogsActivity.this.dialogRemoveFinished = 2;
            }
            if (DialogsActivity.this.folderId == 0) {
                if (addDialogToFolder == 2) {
                    DialogsActivity.this.dialogsItemAnimator.prepareForRemove();
                    if (i3 == 0) {
                        DialogsActivity.this.dialogChangeFinished = 2;
                        DialogsActivity.this.setDialogsListFrozen(true);
                        DialogsActivity.this.dialogsAdapter.notifyItemChanged(0);
                    } else {
                        DialogsActivity.this.lastItemsCount = DialogsActivity.this.lastItemsCount + 1;
                        DialogsActivity.this.dialogsAdapter.notifyItemInserted(0);
                        if (!SharedConfig.archiveHidden && DialogsActivity.this.layoutManager.findFirstVisibleItemPosition() == 0) {
                            DialogsActivity.this.listView.smoothScrollBy(0, -AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f));
                        }
                    }
                    DialogsActivity.frozenDialogsList.add(0, DialogsActivity.getDialogsArray(DialogsActivity.this.currentAccount, DialogsActivity.this.dialogsType, DialogsActivity.this.folderId, false).get(0));
                } else if (addDialogToFolder == 1) {
                    ViewHolder findViewHolderForAdapterPosition = DialogsActivity.this.listView.findViewHolderForAdapterPosition(0);
                    if (findViewHolderForAdapterPosition != null) {
                        View view = findViewHolderForAdapterPosition.itemView;
                        if (view instanceof DialogCell) {
                            DialogCell dialogCell = (DialogCell) view;
                            dialogCell.checkCurrentDialogIndex(true);
                            dialogCell.animateArchiveAvatar();
                        }
                    }
                }
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                String str = "archivehint_l";
                if (globalMainSettings.getBoolean(str, false) || SharedConfig.archiveHidden) {
                    i5 = 1;
                }
                if (i5 == 0) {
                    globalMainSettings.edit().putBoolean(str, true).commit();
                }
                DialogsActivity.this.getUndoView().showWithAction(dialog.id, i5 != 0 ? 2 : 3, null, new -$$Lambda$DialogsActivity$SwipeController$YQ1mguBHuXIRhAK21aamsJthsAQ(this, dialog, i4));
            }
            if (DialogsActivity.this.folderId != 0 && DialogsActivity.frozenDialogsList.isEmpty()) {
                DialogsActivity.this.listView.setEmptyView(null);
                DialogsActivity.this.progressView.setVisibility(4);
            }
        }

        public /* synthetic */ void lambda$null$0$DialogsActivity$SwipeController(Dialog dialog, int i) {
            DialogsActivity.this.dialogsListFrozen = true;
            DialogsActivity.this.getMessagesController().addDialogToFolder(dialog.id, 0, i, 0);
            DialogsActivity.this.dialogsListFrozen = false;
            ArrayList dialogs = DialogsActivity.this.getMessagesController().getDialogs(0);
            int indexOf = dialogs.indexOf(dialog);
            if (indexOf >= 0) {
                ArrayList dialogs2 = DialogsActivity.this.getMessagesController().getDialogs(1);
                if (!(dialogs2.isEmpty() && indexOf == 1)) {
                    DialogsActivity.this.dialogInsertFinished = 2;
                    DialogsActivity.this.setDialogsListFrozen(true);
                    DialogsActivity.this.dialogsItemAnimator.prepareForRemove();
                    DialogsActivity.this.lastItemsCount = DialogsActivity.this.lastItemsCount + 1;
                    DialogsActivity.this.dialogsAdapter.notifyItemInserted(indexOf);
                }
                if (dialogs2.isEmpty()) {
                    dialogs.remove(0);
                    if (indexOf == 1) {
                        DialogsActivity.this.dialogChangeFinished = 2;
                        DialogsActivity.this.setDialogsListFrozen(true);
                        DialogsActivity.this.dialogsAdapter.notifyItemChanged(0);
                        return;
                    }
                    DialogsActivity.frozenDialogsList.remove(0);
                    DialogsActivity.this.dialogsItemAnimator.prepareForRemove();
                    DialogsActivity.this.lastItemsCount = DialogsActivity.this.lastItemsCount - 1;
                    DialogsActivity.this.dialogsAdapter.notifyItemRemoved(0);
                    return;
                }
                return;
            }
            DialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
        }

        public void onSelectedChanged(ViewHolder viewHolder, int i) {
            if (viewHolder != null) {
                DialogsActivity.this.listView.hideSelector(false);
            }
            super.onSelectedChanged(viewHolder, i);
        }

        public long getAnimationDuration(RecyclerView recyclerView, int i, float f, float f2) {
            if (i == 4) {
                return 200;
            }
            if (i == 8 && DialogsActivity.this.movingView != null) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$DialogsActivity$SwipeController$P5v0ux1s9L8VHZMGfvGmr58IqXA(DialogsActivity.this.movingView), DialogsActivity.this.dialogsItemAnimator.getMoveDuration());
                DialogsActivity.this.movingView = null;
            }
            return super.getAnimationDuration(recyclerView, i, f, f2);
        }
    }

    public class DialogsRecyclerView extends RecyclerListView {
        private boolean firstLayout = true;
        private boolean ignoreLayout;

        public DialogsRecyclerView(Context context) {
            super(context);
        }

        public void setViewsOffset(float f) {
            DialogsActivity.viewOffset = f;
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                getChildAt(i).setTranslationY(f);
            }
            if (this.selectorPosition != -1) {
                View findViewByPosition = DialogsActivity.this.layoutManager.findViewByPosition(this.selectorPosition);
                if (findViewByPosition != null) {
                    this.selectorRect.set(findViewByPosition.getLeft(), (int) (((float) findViewByPosition.getTop()) + f), findViewByPosition.getRight(), (int) (((float) findViewByPosition.getBottom()) + f));
                    this.selectorDrawable.setBounds(this.selectorRect);
                }
            }
            invalidate();
        }

        public float getViewOffset() {
            return DialogsActivity.viewOffset;
        }

        public void addView(View view, int i, LayoutParams layoutParams) {
            super.addView(view, i, layoutParams);
            view.setTranslationY(DialogsActivity.viewOffset);
        }

        public void removeView(View view) {
            super.removeView(view);
            view.setTranslationY(0.0f);
        }

        public void onDraw(Canvas canvas) {
            PullForegroundDrawable pullForegroundDrawable = DialogsActivity.this.pullForegroundDrawable;
            if (!(pullForegroundDrawable == null || DialogsActivity.viewOffset == 0.0f)) {
                pullForegroundDrawable.drawOverScroll(canvas);
            }
            super.onDraw(canvas);
        }

        /* Access modifiers changed, original: protected */
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            if (DialogsActivity.this.slidingView != null && DialogsActivity.this.pacmanAnimation != null) {
                DialogsActivity.this.pacmanAnimation.draw(canvas, DialogsActivity.this.slidingView.getTop() + (DialogsActivity.this.slidingView.getMeasuredHeight() / 2));
            }
        }

        public void setAdapter(Adapter adapter) {
            super.setAdapter(adapter);
            this.firstLayout = true;
        }

        private void checkIfAdapterValid() {
            if (DialogsActivity.this.listView != null && DialogsActivity.this.dialogsAdapter != null && DialogsActivity.this.listView.getAdapter() == DialogsActivity.this.dialogsAdapter && DialogsActivity.this.lastItemsCount != DialogsActivity.this.dialogsAdapter.getItemCount()) {
                this.ignoreLayout = true;
                DialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
                this.ignoreLayout = false;
            }
        }

        public void setPadding(int i, int i2, int i3, int i4) {
            super.setPadding(i, i2, i3, i4);
            if (DialogsActivity.this.searchEmptyView != null) {
                DialogsActivity.this.searchEmptyView.setPadding(i, i2, i3, i4);
            }
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            if (this.firstLayout && DialogsActivity.this.getMessagesController().dialogsLoaded) {
                if (DialogsActivity.this.hasHiddenArchive()) {
                    this.ignoreLayout = true;
                    DialogsActivity.this.layoutManager.scrollToPositionWithOffset(1, 0);
                    this.ignoreLayout = false;
                }
                this.firstLayout = false;
            }
            checkIfAdapterValid();
            super.onMeasure(i, i2);
        }

        /* Access modifiers changed, original: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            if ((DialogsActivity.this.dialogRemoveFinished != 0 || DialogsActivity.this.dialogInsertFinished != 0 || DialogsActivity.this.dialogChangeFinished != 0) && !DialogsActivity.this.dialogsItemAnimator.isRunning()) {
                DialogsActivity.this.onDialogAnimationFinished();
            }
        }

        public void requestLayout() {
            if (!this.ignoreLayout) {
                super.requestLayout();
            }
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (DialogsActivity.this.waitingForScrollFinished || DialogsActivity.this.dialogRemoveFinished != 0 || DialogsActivity.this.dialogInsertFinished != 0 || DialogsActivity.this.dialogChangeFinished != 0) {
                return false;
            }
            int action = motionEvent.getAction();
            if (action == 0) {
                DialogsActivity.this.listView.setOverScrollMode(0);
            }
            if ((action == 1 || action == 3) && !DialogsActivity.this.itemTouchhelper.isIdle() && DialogsActivity.this.swipeController.swipingFolder) {
                DialogsActivity.this.swipeController.swipeFolderBack = true;
                if (DialogsActivity.this.itemTouchhelper.checkHorizontalSwipe(null, 4) != 0) {
                    SharedConfig.toggleArchiveHidden();
                    DialogsActivity.this.getUndoView().showWithAction(0, 7, null, null);
                    DialogsActivity.this.archivePullViewState = SharedConfig.archiveHidden ? 2 : 0;
                    DialogsActivity dialogsActivity = DialogsActivity.this;
                    PullForegroundDrawable pullForegroundDrawable = dialogsActivity.pullForegroundDrawable;
                    if (pullForegroundDrawable != null) {
                        pullForegroundDrawable.setWillDraw(dialogsActivity.archivePullViewState != 0);
                    }
                }
            }
            boolean onTouchEvent = super.onTouchEvent(motionEvent);
            if ((action == 1 || action == 3) && DialogsActivity.this.archivePullViewState == 2 && DialogsActivity.this.hasHiddenArchive()) {
                action = DialogsActivity.this.layoutManager.findFirstVisibleItemPosition();
                if (action == 0) {
                    View findViewByPosition = DialogsActivity.this.layoutManager.findViewByPosition(action);
                    int dp = (int) (((float) AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f)) * 0.85f);
                    int top = findViewByPosition.getTop() + findViewByPosition.getMeasuredHeight();
                    if (findViewByPosition != null) {
                        long currentTimeMillis = System.currentTimeMillis() - DialogsActivity.this.startArchivePullingTime;
                        if (top < dp || currentTimeMillis < 200) {
                            DialogsActivity.this.listView.smoothScrollBy(0, top, CubicBezierInterpolator.EASE_OUT_QUINT);
                            DialogsActivity.this.archivePullViewState = 2;
                        } else if (DialogsActivity.this.archivePullViewState != 1) {
                            if (DialogsActivity.this.listView.getViewOffset() == 0.0f) {
                                DialogsActivity.this.listView.smoothScrollBy(0, findViewByPosition.getTop(), CubicBezierInterpolator.EASE_OUT_QUINT);
                            }
                            if (!DialogsActivity.this.canShowHiddenArchive) {
                                DialogsActivity.this.canShowHiddenArchive = true;
                                DialogsActivity.this.listView.performHapticFeedback(3, 2);
                                PullForegroundDrawable pullForegroundDrawable2 = DialogsActivity.this.pullForegroundDrawable;
                                if (pullForegroundDrawable2 != null) {
                                    pullForegroundDrawable2.colorize(true);
                                }
                            }
                            ((DialogCell) findViewByPosition).startOutAnimation();
                            DialogsActivity.this.archivePullViewState = 1;
                        }
                        if (DialogsActivity.this.listView.getViewOffset() != 0.0f) {
                            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{DialogsActivity.this.listView.getViewOffset(), 0.0f});
                            ofFloat.addUpdateListener(new -$$Lambda$DialogsActivity$DialogsRecyclerView$50BTyBCPpkWzj4s4ajQQMlb12OI(this));
                            ofFloat.setDuration((long) (350.0f - ((DialogsActivity.this.listView.getViewOffset() / ((float) PullForegroundDrawable.getMaxOverscroll())) * 120.0f)));
                            ofFloat.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                            DialogsActivity.this.listView.setScrollEnabled(false);
                            ofFloat.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    super.onAnimationEnd(animator);
                                    DialogsActivity.this.listView.setScrollEnabled(true);
                                }
                            });
                            ofFloat.start();
                        }
                    }
                }
            }
            return onTouchEvent;
        }

        public /* synthetic */ void lambda$onTouchEvent$0$DialogsActivity$DialogsRecyclerView(ValueAnimator valueAnimator) {
            DialogsActivity.this.listView.setViewsOffset(((Float) valueAnimator.getAnimatedValue()).floatValue());
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (DialogsActivity.this.waitingForScrollFinished || DialogsActivity.this.dialogRemoveFinished != 0 || DialogsActivity.this.dialogInsertFinished != 0 || DialogsActivity.this.dialogChangeFinished != 0) {
                return false;
            }
            if (motionEvent.getAction() == 0) {
                DialogsActivity dialogsActivity = DialogsActivity.this;
                dialogsActivity.allowSwipeDuringCurrentTouch = dialogsActivity.actionBar.isActionModeShowed() ^ 1;
                checkIfAdapterValid();
            }
            return super.onInterceptTouchEvent(motionEvent);
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
            this.dialogsType = this.arguments.getInt("dialogsType", 0);
            this.selectAlertString = this.arguments.getString("selectAlertString");
            this.selectAlertStringGroup = this.arguments.getString("selectAlertStringGroup");
            this.addToGroupAlertString = this.arguments.getString("addToGroupAlertString");
            this.allowSwitchAccount = this.arguments.getBoolean("allowSwitchAccount");
            this.checkCanWrite = this.arguments.getBoolean("checkCanWrite", true);
            this.folderId = this.arguments.getInt("folderId", 0);
            this.resetDelegate = this.arguments.getBoolean("resetDelegate", true);
            this.messagesCount = this.arguments.getInt("messagesCount", 0);
        }
        if (this.dialogsType == 0) {
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
            getNotificationCenter().addObserver(this, NotificationCenter.dialogsUnreadCounterChanged);
            getNotificationCenter().addObserver(this, NotificationCenter.needDeleteDialog);
            getNotificationCenter().addObserver(this, NotificationCenter.folderBecomeEmpty);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetPasscode);
        }
        if (!dialogsLoaded[this.currentAccount]) {
            getMessagesController().loadGlobalNotificationsSettings();
            getMessagesController().loadDialogs(this.folderId, 0, 100, true);
            getMessagesController().loadHintDialogs();
            getContactsController().checkInviteText();
            getMediaDataController().loadRecents(2, false, true, false);
            getMediaDataController().checkFeaturedStickers();
            dialogsLoaded[this.currentAccount] = true;
        }
        getMessagesController().loadPinnedDialogs(this.folderId, 0, null);
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
            getNotificationCenter().removeObserver(this, NotificationCenter.dialogsUnreadCounterChanged);
            getNotificationCenter().removeObserver(this, NotificationCenter.needDeleteDialog);
            getNotificationCenter().removeObserver(this, NotificationCenter.folderBecomeEmpty);
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

    public View createView(Context context) {
        Drawable avatarDrawable;
        int i;
        int i2;
        Context context2 = context;
        this.searching = false;
        this.searchWas = false;
        this.pacmanAnimation = null;
        AndroidUtilities.runOnUIThread(new -$$Lambda$DialogsActivity$Wz8eSYJLAR-xfNaDl4aQWUwW9N0(context2));
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (!this.onlySelect && this.searchString == null && this.folderId == 0) {
            this.proxyDrawable = new ProxyDrawable(context2);
            this.proxyItem = createMenu.addItem(2, this.proxyDrawable);
            this.proxyItem.setContentDescription(LocaleController.getString("ProxySettings", NUM));
            this.passcodeItem = createMenu.addItem(1, NUM);
            updatePasscodeButton();
            updateProxyButton(false);
        }
        ActionBarMenuItem actionBarMenuItemSearchListener = createMenu.addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                DialogsActivity.this.searching = true;
                if (DialogsActivity.this.switchItem != null) {
                    DialogsActivity.this.switchItem.setVisibility(8);
                }
                if (DialogsActivity.this.proxyItem != null && DialogsActivity.this.proxyItemVisisble) {
                    DialogsActivity.this.proxyItem.setVisibility(8);
                }
                if (DialogsActivity.this.listView != null) {
                    if (DialogsActivity.this.searchString != null) {
                        DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.searchEmptyView);
                        DialogsActivity.this.progressView.setVisibility(8);
                    }
                    if (!DialogsActivity.this.onlySelect) {
                        DialogsActivity.this.floatingButtonContainer.setVisibility(8);
                    }
                }
                DialogsActivity.this.updatePasscodeButton();
                DialogsActivity.this.actionBar.setBackButtonContentDescription(LocaleController.getString("AccDescrGoBack", NUM));
            }

            public boolean canCollapseSearch() {
                if (DialogsActivity.this.switchItem != null) {
                    DialogsActivity.this.switchItem.setVisibility(0);
                }
                if (DialogsActivity.this.proxyItem != null && DialogsActivity.this.proxyItemVisisble) {
                    DialogsActivity.this.proxyItem.setVisibility(0);
                }
                if (DialogsActivity.this.searchString == null) {
                    return true;
                }
                DialogsActivity.this.finishFragment();
                return false;
            }

            public void onSearchCollapse() {
                DialogsActivity.this.searching = false;
                DialogsActivity.this.searchWas = false;
                if (DialogsActivity.this.listView != null) {
                    DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.folderId == 0 ? DialogsActivity.this.progressView : null);
                    DialogsActivity.this.searchEmptyView.setVisibility(8);
                    if (!DialogsActivity.this.onlySelect) {
                        DialogsActivity.this.floatingButtonContainer.setVisibility(0);
                        DialogsActivity.this.floatingHidden = true;
                        DialogsActivity.this.floatingButtonTranslation = (float) AndroidUtilities.dp(100.0f);
                        DialogsActivity.this.floatingButtonHideProgress = 1.0f;
                        DialogsActivity.this.updateFloatingButtonOffset();
                        DialogsActivity.this.hideFloatingButton(false);
                    }
                    if (DialogsActivity.this.listView.getAdapter() != DialogsActivity.this.dialogsAdapter) {
                        DialogsActivity.this.listView.setAdapter(DialogsActivity.this.dialogsAdapter);
                        DialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
                    }
                }
                if (DialogsActivity.this.dialogsSearchAdapter != null) {
                    DialogsActivity.this.dialogsSearchAdapter.searchDialogs(null);
                }
                DialogsActivity.this.updatePasscodeButton();
                if (DialogsActivity.this.menuDrawable != null) {
                    DialogsActivity.this.actionBar.setBackButtonContentDescription(LocaleController.getString("AccDescrOpenMenu", NUM));
                }
            }

            public void onTextChanged(EditText editText) {
                String obj = editText.getText().toString();
                if (obj.length() != 0 || (DialogsActivity.this.dialogsSearchAdapter != null && DialogsActivity.this.dialogsSearchAdapter.hasRecentRearch())) {
                    DialogsActivity.this.searchWas = true;
                    if (!(DialogsActivity.this.dialogsSearchAdapter == null || DialogsActivity.this.listView.getAdapter() == DialogsActivity.this.dialogsSearchAdapter)) {
                        DialogsActivity.this.listView.setAdapter(DialogsActivity.this.dialogsSearchAdapter);
                        DialogsActivity.this.dialogsSearchAdapter.notifyDataSetChanged();
                    }
                    if (!(DialogsActivity.this.searchEmptyView == null || DialogsActivity.this.listView.getEmptyView() == DialogsActivity.this.searchEmptyView)) {
                        DialogsActivity.this.progressView.setVisibility(8);
                        DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.searchEmptyView);
                    }
                }
                if (DialogsActivity.this.dialogsSearchAdapter != null) {
                    DialogsActivity.this.dialogsSearchAdapter.searchDialogs(obj);
                }
            }
        });
        String str = "Search";
        actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString(str, NUM));
        actionBarMenuItemSearchListener.setContentDescription(LocaleController.getString(str, NUM));
        if (this.onlySelect) {
            this.actionBar.setBackButtonImage(NUM);
            if (this.dialogsType == 3 && this.selectAlertString == null) {
                this.actionBar.setTitle(LocaleController.getString("ForwardTo", NUM));
            } else {
                this.actionBar.setTitle(LocaleController.getString("SelectChat", NUM));
            }
        } else {
            ActionBar actionBar;
            if (this.searchString == null && this.folderId == 0) {
                actionBar = this.actionBar;
                MenuDrawable menuDrawable = new MenuDrawable();
                this.menuDrawable = menuDrawable;
                actionBar.setBackButtonDrawable(menuDrawable);
                this.actionBar.setBackButtonContentDescription(LocaleController.getString("AccDescrOpenMenu", NUM));
            } else {
                actionBar = this.actionBar;
                BackDrawable backDrawable = new BackDrawable(false);
                this.backDrawable = backDrawable;
                actionBar.setBackButtonDrawable(backDrawable);
            }
            if (this.folderId != 0) {
                this.actionBar.setTitle(LocaleController.getString("ArchivedChats", NUM));
            } else if (BuildVars.DEBUG_VERSION) {
                this.actionBar.setTitle("Telegram Beta");
            } else {
                this.actionBar.setTitle(LocaleController.getString("AppName", NUM));
            }
            this.actionBar.setSupportsHolidayImage(true);
        }
        this.actionBar.setTitleActionRunnable(new -$$Lambda$DialogsActivity$g_ZetuAZEmHs4itgVWSuCgX-8K4(this));
        if (this.allowSwitchAccount && UserConfig.getActivatedAccountsCount() > 1) {
            this.switchItem = createMenu.addItemWithWidth(1, 0, AndroidUtilities.dp(56.0f));
            avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
            BackupImageView backupImageView = new BackupImageView(context2);
            backupImageView.setRoundRadius(AndroidUtilities.dp(18.0f));
            this.switchItem.addView(backupImageView, LayoutHelper.createFrame(36, 36, 17));
            Object currentUser = getUserConfig().getCurrentUser();
            avatarDrawable.setInfo((User) currentUser);
            backupImageView.getImageReceiver().setCurrentAccount(this.currentAccount);
            backupImageView.setImage(ImageLocation.getForUser(currentUser, false), "50_50", avatarDrawable, currentUser);
            for (i = 0; i < 3; i++) {
                if (AccountInstance.getInstance(i).getUserConfig().getCurrentUser() != null) {
                    AccountSelectCell accountSelectCell = new AccountSelectCell(context2);
                    accountSelectCell.setAccount(i, true);
                    this.switchItem.addSubItem(i + 10, accountSelectCell, AndroidUtilities.dp(230.0f), AndroidUtilities.dp(48.0f));
                }
            }
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    if (DialogsActivity.this.actionBar.isActionModeShowed()) {
                        DialogsActivity.this.hideActionMode(true);
                    } else if (DialogsActivity.this.onlySelect || DialogsActivity.this.folderId != 0) {
                        DialogsActivity.this.finishFragment();
                    } else if (DialogsActivity.this.parentLayout != null) {
                        DialogsActivity.this.parentLayout.getDrawerLayoutContainer().openDrawer(false);
                    }
                } else if (i == 1) {
                    SharedConfig.appLocked ^= 1;
                    SharedConfig.saveConfig();
                    DialogsActivity.this.updatePasscodeButton();
                } else if (i == 2) {
                    DialogsActivity.this.presentFragment(new ProxyListActivity());
                } else if (i < 10 || i >= 13) {
                    if (i == 100 || i == 101 || i == 102 || i == 103 || i == 104 || i == 105) {
                        DialogsActivity.this.perfromSelectedDialogsAction(i, true);
                    }
                } else if (DialogsActivity.this.getParentActivity() != null) {
                    DialogsActivityDelegate access$7300 = DialogsActivity.this.delegate;
                    LaunchActivity launchActivity = (LaunchActivity) DialogsActivity.this.getParentActivity();
                    launchActivity.switchToAccount(i - 10, true);
                    DialogsActivity dialogsActivity = new DialogsActivity(DialogsActivity.this.arguments);
                    dialogsActivity.setDelegate(access$7300);
                    launchActivity.presentFragment(dialogsActivity, false, true);
                }
            }
        });
        RecyclerView recyclerView = this.sideMenu;
        if (recyclerView != null) {
            recyclerView.setBackgroundColor(Theme.getColor("chats_menuBackground"));
            this.sideMenu.setGlowColor(Theme.getColor("chats_menuBackground"));
            this.sideMenu.getAdapter().notifyDataSetChanged();
        }
        createMenu = this.actionBar.createActionMode();
        this.selectedDialogsCountTextView = new NumberTextView(createMenu.getContext());
        this.selectedDialogsCountTextView.setTextSize(18);
        this.selectedDialogsCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedDialogsCountTextView.setTextColor(Theme.getColor("actionBarActionModeDefaultIcon"));
        createMenu.addView(this.selectedDialogsCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 72, 0, 0, 0));
        this.selectedDialogsCountTextView.setOnTouchListener(-$$Lambda$DialogsActivity$pwzbQ3D6N1rpnIvtZZSb6O4Np6g.INSTANCE);
        this.pinItem = createMenu.addItemWithWidth(100, NUM, AndroidUtilities.dp(54.0f));
        this.muteItem = createMenu.addItemWithWidth(104, NUM, AndroidUtilities.dp(54.0f));
        this.deleteItem = createMenu.addItemWithWidth(102, NUM, AndroidUtilities.dp(54.0f), LocaleController.getString("Delete", NUM));
        ActionBarMenuItem addItemWithWidth = createMenu.addItemWithWidth(0, NUM, AndroidUtilities.dp(54.0f), LocaleController.getString("AccDescrMoreOptions", NUM));
        this.archiveItem = addItemWithWidth.addSubItem(105, NUM, LocaleController.getString("Archive", NUM));
        this.readItem = addItemWithWidth.addSubItem(101, NUM, LocaleController.getString("MarkAsRead", NUM));
        this.clearItem = addItemWithWidth.addSubItem(103, NUM, LocaleController.getString("ClearHistory", NUM));
        this.actionModeViews.add(this.pinItem);
        this.actionModeViews.add(this.muteItem);
        this.actionModeViews.add(this.deleteItem);
        this.actionModeViews.add(addItemWithWidth);
        ContentView contentView = new ContentView(context2);
        this.fragmentView = contentView;
        this.listView = new DialogsRecyclerView(context2);
        this.dialogsItemAnimator = new DialogsItemAnimator() {
            public void onRemoveStarting(ViewHolder viewHolder) {
                super.onRemoveStarting(viewHolder);
                if (DialogsActivity.this.layoutManager.findFirstVisibleItemPosition() == 0) {
                    View findViewByPosition = DialogsActivity.this.layoutManager.findViewByPosition(0);
                    if (findViewByPosition != null) {
                        findViewByPosition.invalidate();
                    }
                    if (DialogsActivity.this.archivePullViewState == 2) {
                        DialogsActivity.this.archivePullViewState = 1;
                    }
                    PullForegroundDrawable pullForegroundDrawable = DialogsActivity.this.pullForegroundDrawable;
                    if (pullForegroundDrawable != null) {
                        pullForegroundDrawable.doNotShow();
                    }
                }
            }

            public void onRemoveFinished(ViewHolder viewHolder) {
                if (DialogsActivity.this.dialogRemoveFinished == 2) {
                    DialogsActivity.this.dialogRemoveFinished = 1;
                }
            }

            public void onAddFinished(ViewHolder viewHolder) {
                if (DialogsActivity.this.dialogInsertFinished == 2) {
                    DialogsActivity.this.dialogInsertFinished = 1;
                }
            }

            public void onChangeFinished(ViewHolder viewHolder, boolean z) {
                if (DialogsActivity.this.dialogChangeFinished == 2) {
                    DialogsActivity.this.dialogChangeFinished = 1;
                }
            }

            /* Access modifiers changed, original: protected */
            public void onAllAnimationsDone() {
                if (DialogsActivity.this.dialogRemoveFinished == 1 || DialogsActivity.this.dialogInsertFinished == 1 || DialogsActivity.this.dialogChangeFinished == 1) {
                    DialogsActivity.this.onDialogAnimationFinished();
                }
            }
        };
        this.listView.setItemAnimator(this.dialogsItemAnimator);
        this.listView.setVerticalScrollBarEnabled(true);
        this.listView.setInstantClick(true);
        this.listView.setTag(Integer.valueOf(4));
        this.layoutManager = new LinearLayoutManager(context2) {
            public void smoothScrollToPosition(RecyclerView recyclerView, State state, int i) {
                if (DialogsActivity.this.hasHiddenArchive() && i == 1) {
                    super.smoothScrollToPosition(recyclerView, state, i);
                    return;
                }
                LinearSmoothScrollerMiddle linearSmoothScrollerMiddle = new LinearSmoothScrollerMiddle(recyclerView.getContext());
                linearSmoothScrollerMiddle.setTargetPosition(i);
                startSmoothScroll(linearSmoothScrollerMiddle);
            }

            /* JADX WARNING: Removed duplicated region for block: B:50:0x0127  */
            /* JADX WARNING: Removed duplicated region for block: B:49:0x0123  */
            /* JADX WARNING: Removed duplicated region for block: B:49:0x0123  */
            /* JADX WARNING: Removed duplicated region for block: B:50:0x0127  */
            /* JADX WARNING: Removed duplicated region for block: B:54:0x013a  */
            public int scrollVerticallyBy(int r13, androidx.recyclerview.widget.RecyclerView.Recycler r14, androidx.recyclerview.widget.RecyclerView.State r15) {
                /*
                r12 = this;
                r0 = org.telegram.ui.DialogsActivity.this;
                r0 = r0.listView;
                r0 = r0.getScrollState();
                r1 = 0;
                r2 = 1;
                if (r0 != r2) goto L_0x0010;
            L_0x000e:
                r0 = 1;
                goto L_0x0011;
            L_0x0010:
                r0 = 0;
            L_0x0011:
                r3 = org.telegram.ui.DialogsActivity.this;
                r3 = r3.listView;
                r3 = r3.getAdapter();
                r4 = org.telegram.ui.DialogsActivity.this;
                r4 = r4.dialogsAdapter;
                r5 = 2;
                r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
                if (r3 != r4) goto L_0x00fd;
            L_0x0026:
                r3 = org.telegram.ui.DialogsActivity.this;
                r3 = r3.dialogsType;
                if (r3 != 0) goto L_0x00fd;
            L_0x002e:
                r3 = org.telegram.ui.DialogsActivity.this;
                r3 = r3.onlySelect;
                if (r3 != 0) goto L_0x00fd;
            L_0x0036:
                r3 = org.telegram.ui.DialogsActivity.this;
                r3 = r3.folderId;
                if (r3 != 0) goto L_0x00fd;
            L_0x003e:
                if (r13 >= 0) goto L_0x00fd;
            L_0x0040:
                r3 = org.telegram.ui.DialogsActivity.this;
                r3 = r3.getMessagesController();
                r3 = r3.hasHiddenArchive();
                if (r3 == 0) goto L_0x00fd;
            L_0x004c:
                r3 = org.telegram.ui.DialogsActivity.this;
                r3 = r3.archivePullViewState;
                if (r3 != r5) goto L_0x00fd;
            L_0x0054:
                r3 = org.telegram.ui.DialogsActivity.this;
                r3 = r3.listView;
                r3.setOverScrollMode(r1);
                r3 = org.telegram.ui.DialogsActivity.this;
                r3 = r3.layoutManager;
                r3 = r3.findFirstVisibleItemPosition();
                if (r3 != 0) goto L_0x0080;
            L_0x0069:
                r4 = org.telegram.ui.DialogsActivity.this;
                r4 = r4.layoutManager;
                r4 = r4.findViewByPosition(r3);
                if (r4 == 0) goto L_0x0080;
            L_0x0075:
                r4 = r4.getBottom();
                r7 = org.telegram.messenger.AndroidUtilities.dp(r6);
                if (r4 > r7) goto L_0x0080;
            L_0x007f:
                r3 = 1;
            L_0x0080:
                if (r0 != 0) goto L_0x00ab;
            L_0x0082:
                r4 = org.telegram.ui.DialogsActivity.this;
                r4 = r4.layoutManager;
                r4 = r4.findViewByPosition(r3);
                r7 = org.telegram.messenger.SharedConfig.useThreeLinesLayout;
                if (r7 == 0) goto L_0x0093;
            L_0x0090:
                r7 = NUM; // 0x429CLASSNAME float:78.0 double:5.521281773E-315;
                goto L_0x0095;
            L_0x0093:
                r7 = NUM; // 0x42900000 float:72.0 double:5.517396283E-315;
            L_0x0095:
                r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
                r7 = r7 + r2;
                r4 = r4.getTop();
                r4 = -r4;
                r3 = r3 - r2;
                r3 = r3 * r7;
                r4 = r4 + r3;
                r3 = java.lang.Math.abs(r13);
                if (r4 >= r3) goto L_0x00fd;
            L_0x00a9:
                r3 = -r4;
                goto L_0x00fe;
            L_0x00ab:
                if (r3 != 0) goto L_0x00fd;
            L_0x00ad:
                r4 = org.telegram.ui.DialogsActivity.this;
                r4 = r4.layoutManager;
                r3 = r4.findViewByPosition(r3);
                r4 = r3.getTop();
                r4 = (float) r4;
                r3 = r3.getMeasuredHeight();
                r3 = (float) r3;
                r4 = r4 / r3;
                r3 = r4 + r6;
                r4 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1));
                if (r4 <= 0) goto L_0x00ca;
            L_0x00c8:
                r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            L_0x00ca:
                r4 = org.telegram.ui.DialogsActivity.this;
                r4 = r4.listView;
                r4.setOverScrollMode(r5);
                r4 = (float) r13;
                r7 = NUM; // 0x3ee66666 float:0.45 double:5.21380997E-315;
                r8 = NUM; // 0x3e800000 float:0.25 double:5.180653787E-315;
                r3 = r3 * r8;
                r7 = r7 - r3;
                r4 = r4 * r7;
                r3 = (int) r4;
                r4 = -1;
                if (r3 <= r4) goto L_0x00e3;
            L_0x00e2:
                r3 = -1;
            L_0x00e3:
                r4 = org.telegram.ui.DialogsActivity.this;
                r4 = r4.undoView;
                r4 = r4[r1];
                r4 = r4.getVisibility();
                if (r4 != 0) goto L_0x00fe;
            L_0x00f1:
                r4 = org.telegram.ui.DialogsActivity.this;
                r4 = r4.undoView;
                r4 = r4[r1];
                r4.hide(r2, r2);
                goto L_0x00fe;
            L_0x00fd:
                r3 = r13;
            L_0x00fe:
                r4 = org.telegram.ui.DialogsActivity.this;
                r4 = r4.listView;
                r4 = r4.getViewOffset();
                r7 = 0;
                r4 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1));
                if (r4 == 0) goto L_0x0132;
            L_0x010d:
                if (r13 <= 0) goto L_0x0132;
            L_0x010f:
                if (r0 == 0) goto L_0x0132;
            L_0x0111:
                r3 = org.telegram.ui.DialogsActivity.this;
                r3 = r3.listView;
                r3 = r3.getViewOffset();
                r3 = (int) r3;
                r3 = (float) r3;
                r4 = (float) r13;
                r3 = r3 - r4;
                r4 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1));
                if (r4 >= 0) goto L_0x0127;
            L_0x0123:
                r3 = (int) r3;
                r4 = r3;
                r3 = 0;
                goto L_0x0128;
            L_0x0127:
                r4 = 0;
            L_0x0128:
                r8 = org.telegram.ui.DialogsActivity.this;
                r8 = r8.listView;
                r8.setViewsOffset(r3);
                r3 = r4;
            L_0x0132:
                r4 = org.telegram.ui.DialogsActivity.this;
                r4 = r4.archivePullViewState;
                if (r4 == 0) goto L_0x0265;
            L_0x013a:
                r4 = org.telegram.ui.DialogsActivity.this;
                r4 = r4.hasHiddenArchive();
                if (r4 == 0) goto L_0x0265;
            L_0x0142:
                r14 = super.scrollVerticallyBy(r3, r14, r15);
                r15 = org.telegram.ui.DialogsActivity.this;
                r15 = r15.pullForegroundDrawable;
                if (r15 == 0) goto L_0x014e;
            L_0x014c:
                r15.scrollDy = r14;
            L_0x014e:
                r15 = org.telegram.ui.DialogsActivity.this;
                r15 = r15.layoutManager;
                r15 = r15.findFirstVisibleItemPosition();
                r4 = 0;
                if (r15 != 0) goto L_0x0165;
            L_0x015b:
                r4 = org.telegram.ui.DialogsActivity.this;
                r4 = r4.layoutManager;
                r4 = r4.findViewByPosition(r15);
            L_0x0165:
                r8 = 0;
                if (r15 != 0) goto L_0x023a;
            L_0x0169:
                if (r4 == 0) goto L_0x023a;
            L_0x016b:
                r15 = r4.getBottom();
                r10 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
                r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
                if (r15 < r10) goto L_0x023a;
            L_0x0177:
                r15 = org.telegram.ui.DialogsActivity.this;
                r10 = r15.startArchivePullingTime;
                r15 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1));
                if (r15 != 0) goto L_0x018a;
            L_0x0181:
                r15 = org.telegram.ui.DialogsActivity.this;
                r7 = java.lang.System.currentTimeMillis();
                r15.startArchivePullingTime = r7;
            L_0x018a:
                r15 = org.telegram.ui.DialogsActivity.this;
                r15 = r15.archivePullViewState;
                if (r15 != r5) goto L_0x019b;
            L_0x0192:
                r15 = org.telegram.ui.DialogsActivity.this;
                r15 = r15.pullForegroundDrawable;
                if (r15 == 0) goto L_0x019b;
            L_0x0198:
                r15.showHidden();
            L_0x019b:
                r15 = r4.getTop();
                r15 = (float) r15;
                r7 = r4.getMeasuredHeight();
                r7 = (float) r7;
                r15 = r15 / r7;
                r15 = r15 + r6;
                r7 = (r15 > r6 ? 1 : (r15 == r6 ? 0 : -1));
                if (r7 <= 0) goto L_0x01ad;
            L_0x01ab:
                r15 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            L_0x01ad:
                r7 = java.lang.System.currentTimeMillis();
                r9 = org.telegram.ui.DialogsActivity.this;
                r9 = r9.startArchivePullingTime;
                r7 = r7 - r9;
                r9 = NUM; // 0x3var_a float:0.85 double:5.25111068E-315;
                r9 = (r15 > r9 ? 1 : (r15 == r9 ? 0 : -1));
                if (r9 <= 0) goto L_0x01c6;
            L_0x01bf:
                r9 = 220; // 0xdc float:3.08E-43 double:1.087E-321;
                r11 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1));
                if (r11 <= 0) goto L_0x01c6;
            L_0x01c5:
                r1 = 1;
            L_0x01c6:
                r2 = org.telegram.ui.DialogsActivity.this;
                r2 = r2.canShowHiddenArchive;
                if (r2 == r1) goto L_0x01ee;
            L_0x01ce:
                r2 = org.telegram.ui.DialogsActivity.this;
                r2.canShowHiddenArchive = r1;
                r2 = org.telegram.ui.DialogsActivity.this;
                r2 = r2.archivePullViewState;
                if (r2 != r5) goto L_0x01ee;
            L_0x01db:
                r2 = org.telegram.ui.DialogsActivity.this;
                r2 = r2.listView;
                r7 = 3;
                r2.performHapticFeedback(r7, r5);
                r2 = org.telegram.ui.DialogsActivity.this;
                r2 = r2.pullForegroundDrawable;
                if (r2 == 0) goto L_0x01ee;
            L_0x01eb:
                r2.colorize(r1);
            L_0x01ee:
                r1 = org.telegram.ui.DialogsActivity.this;
                r1 = r1.archivePullViewState;
                if (r1 != r5) goto L_0x022a;
            L_0x01f6:
                r3 = r3 - r14;
                if (r3 == 0) goto L_0x022a;
            L_0x01f9:
                if (r13 >= 0) goto L_0x022a;
            L_0x01fb:
                if (r0 == 0) goto L_0x022a;
            L_0x01fd:
                r0 = org.telegram.ui.DialogsActivity.this;
                r0 = r0.listView;
                r0 = r0.getViewOffset();
                r1 = org.telegram.ui.Components.PullForegroundDrawable.getMaxOverscroll();
                r1 = (float) r1;
                r0 = r0 / r1;
                r6 = r6 - r0;
                r0 = org.telegram.ui.DialogsActivity.this;
                r0 = r0.listView;
                r0 = r0.getViewOffset();
                r13 = (float) r13;
                r1 = NUM; // 0x3e4ccccd float:0.2 double:5.164075695E-315;
                r13 = r13 * r1;
                r13 = r13 * r6;
                r0 = r0 - r13;
                r13 = org.telegram.ui.DialogsActivity.this;
                r13 = r13.listView;
                r13.setViewsOffset(r0);
            L_0x022a:
                r13 = org.telegram.ui.DialogsActivity.this;
                r0 = r13.pullForegroundDrawable;
                if (r0 == 0) goto L_0x025f;
            L_0x0230:
                r0.pullProgress = r15;
                r13 = r13.listView;
                r0.setListView(r13);
                goto L_0x025f;
            L_0x023a:
                r13 = org.telegram.ui.DialogsActivity.this;
                r13.startArchivePullingTime = r8;
                r13 = org.telegram.ui.DialogsActivity.this;
                r13.canShowHiddenArchive = r1;
                r13 = org.telegram.ui.DialogsActivity.this;
                r13.archivePullViewState = r5;
                r13 = org.telegram.ui.DialogsActivity.this;
                r13 = r13.pullForegroundDrawable;
                if (r13 == 0) goto L_0x025f;
            L_0x024f:
                r13.resetText();
                r13 = org.telegram.ui.DialogsActivity.this;
                r15 = r13.pullForegroundDrawable;
                r15.pullProgress = r7;
                r13 = r13.listView;
                r15.setListView(r13);
            L_0x025f:
                if (r4 == 0) goto L_0x0264;
            L_0x0261:
                r4.invalidate();
            L_0x0264:
                return r14;
            L_0x0265:
                r13 = super.scrollVerticallyBy(r3, r14, r15);
                return r13;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity$AnonymousClass4.scrollVerticallyBy(int, androidx.recyclerview.widget.RecyclerView$Recycler, androidx.recyclerview.widget.RecyclerView$State):int");
            }
        };
        this.layoutManager.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        contentView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new -$$Lambda$DialogsActivity$vVlyn12mo3e9YWraUH_TSyb3Khs(this));
        this.listView.setOnItemLongClickListener(new OnItemLongClickListenerExtended() {
            /* JADX WARNING: Missing block: B:55:0x0149, code skipped:
            return false;
     */
            public boolean onItemClick(android.view.View r6, int r7, float r8, float r9) {
                /*
                r5 = this;
                r0 = org.telegram.ui.DialogsActivity.this;
                r0 = r0.getParentActivity();
                r1 = 0;
                if (r0 != 0) goto L_0x000a;
            L_0x0009:
                return r1;
            L_0x000a:
                r0 = org.telegram.ui.DialogsActivity.this;
                r0 = r0.actionBar;
                r0 = r0.isActionModeShowed();
                r2 = 1;
                if (r0 != 0) goto L_0x00c2;
            L_0x0017:
                r0 = org.telegram.messenger.AndroidUtilities.isTablet();
                if (r0 != 0) goto L_0x00c2;
            L_0x001d:
                r0 = org.telegram.ui.DialogsActivity.this;
                r0 = r0.onlySelect;
                if (r0 != 0) goto L_0x00c2;
            L_0x0025:
                r0 = r6 instanceof org.telegram.ui.Cells.DialogCell;
                if (r0 == 0) goto L_0x00c2;
            L_0x0029:
                r0 = r6;
                r0 = (org.telegram.ui.Cells.DialogCell) r0;
                r8 = r0.isPointInsideAvatar(r8, r9);
                if (r8 == 0) goto L_0x00c2;
            L_0x0032:
                r6 = r0.getDialogId();
                r8 = new android.os.Bundle;
                r8.<init>();
                r7 = (int) r6;
                r6 = r0.getMessageId();
                if (r7 == 0) goto L_0x00c1;
            L_0x0042:
                if (r7 <= 0) goto L_0x004a;
            L_0x0044:
                r9 = "user_id";
                r8.putInt(r9, r7);
                goto L_0x0073;
            L_0x004a:
                if (r7 >= 0) goto L_0x0073;
            L_0x004c:
                if (r6 == 0) goto L_0x006d;
            L_0x004e:
                r9 = org.telegram.ui.DialogsActivity.this;
                r9 = r9.getMessagesController();
                r0 = -r7;
                r0 = java.lang.Integer.valueOf(r0);
                r9 = r9.getChat(r0);
                if (r9 == 0) goto L_0x006d;
            L_0x005f:
                r0 = r9.migrated_to;
                if (r0 == 0) goto L_0x006d;
            L_0x0063:
                r0 = "migrated_to";
                r8.putInt(r0, r7);
                r7 = r9.migrated_to;
                r7 = r7.channel_id;
                r7 = -r7;
            L_0x006d:
                r7 = -r7;
                r9 = "chat_id";
                r8.putInt(r9, r7);
            L_0x0073:
                if (r6 == 0) goto L_0x007a;
            L_0x0075:
                r7 = "message_id";
                r8.putInt(r7, r6);
            L_0x007a:
                r6 = org.telegram.ui.DialogsActivity.this;
                r6 = r6.searchString;
                if (r6 == 0) goto L_0x00a8;
            L_0x0082:
                r6 = org.telegram.ui.DialogsActivity.this;
                r6 = r6.getMessagesController();
                r7 = org.telegram.ui.DialogsActivity.this;
                r6 = r6.checkCanOpenChat(r8, r7);
                if (r6 == 0) goto L_0x00c0;
            L_0x0090:
                r6 = org.telegram.ui.DialogsActivity.this;
                r6 = r6.getNotificationCenter();
                r7 = org.telegram.messenger.NotificationCenter.closeChats;
                r9 = new java.lang.Object[r1];
                r6.postNotificationName(r7, r9);
                r6 = org.telegram.ui.DialogsActivity.this;
                r7 = new org.telegram.ui.ChatActivity;
                r7.<init>(r8);
                r6.presentFragmentAsPreview(r7);
                goto L_0x00c0;
            L_0x00a8:
                r6 = org.telegram.ui.DialogsActivity.this;
                r6 = r6.getMessagesController();
                r7 = org.telegram.ui.DialogsActivity.this;
                r6 = r6.checkCanOpenChat(r8, r7);
                if (r6 == 0) goto L_0x00c0;
            L_0x00b6:
                r6 = org.telegram.ui.DialogsActivity.this;
                r7 = new org.telegram.ui.ChatActivity;
                r7.<init>(r8);
                r6.presentFragmentAsPreview(r7);
            L_0x00c0:
                return r2;
            L_0x00c1:
                return r1;
            L_0x00c2:
                r8 = org.telegram.ui.DialogsActivity.this;
                r8 = r8.listView;
                r8 = r8.getAdapter();
                r9 = org.telegram.ui.DialogsActivity.this;
                r9 = r9.dialogsSearchAdapter;
                if (r8 != r9) goto L_0x00de;
            L_0x00d4:
                r6 = org.telegram.ui.DialogsActivity.this;
                r6 = r6.dialogsSearchAdapter;
                r6.getItem(r7);
                return r1;
            L_0x00de:
                r8 = org.telegram.ui.DialogsActivity.this;
                r8 = r8.currentAccount;
                r9 = org.telegram.ui.DialogsActivity.this;
                r9 = r9.dialogsType;
                r0 = org.telegram.ui.DialogsActivity.this;
                r0 = r0.folderId;
                r3 = org.telegram.ui.DialogsActivity.this;
                r3 = r3.dialogsListFrozen;
                r8 = org.telegram.ui.DialogsActivity.getDialogsArray(r8, r9, r0, r3);
                r9 = org.telegram.ui.DialogsActivity.this;
                r9 = r9.dialogsAdapter;
                r7 = r9.fixPosition(r7);
                if (r7 < 0) goto L_0x0166;
            L_0x0106:
                r9 = r8.size();
                if (r7 < r9) goto L_0x010d;
            L_0x010c:
                goto L_0x0166;
            L_0x010d:
                r7 = r8.get(r7);
                r7 = (org.telegram.tgnet.TLRPC.Dialog) r7;
                r8 = org.telegram.ui.DialogsActivity.this;
                r8 = r8.onlySelect;
                if (r8 == 0) goto L_0x014a;
            L_0x011b:
                r8 = org.telegram.ui.DialogsActivity.this;
                r8 = r8.dialogsType;
                r9 = 3;
                if (r8 != r9) goto L_0x0149;
            L_0x0124:
                r8 = org.telegram.ui.DialogsActivity.this;
                r8 = r8.selectAlertString;
                if (r8 == 0) goto L_0x012d;
            L_0x012c:
                goto L_0x0149;
            L_0x012d:
                r8 = org.telegram.ui.DialogsActivity.this;
                r3 = r7.id;
                r8 = r8.validateSlowModeDialog(r3);
                if (r8 != 0) goto L_0x0138;
            L_0x0137:
                return r1;
            L_0x0138:
                r8 = org.telegram.ui.DialogsActivity.this;
                r8 = r8.dialogsAdapter;
                r0 = r7.id;
                r8.addOrRemoveSelectedDialog(r0, r6);
                r6 = org.telegram.ui.DialogsActivity.this;
                r6.updateSelectedCount();
                goto L_0x0165;
            L_0x0149:
                return r1;
            L_0x014a:
                r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_dialogFolder;
                if (r8 == 0) goto L_0x014f;
            L_0x014e:
                return r1;
            L_0x014f:
                r8 = org.telegram.ui.DialogsActivity.this;
                r8 = r8.actionBar;
                r8 = r8.isActionModeShowed();
                if (r8 == 0) goto L_0x0160;
            L_0x015b:
                r8 = r7.pinned;
                if (r8 == 0) goto L_0x0160;
            L_0x015f:
                return r1;
            L_0x0160:
                r8 = org.telegram.ui.DialogsActivity.this;
                r8.showOrUpdateActionMode(r7, r6);
            L_0x0165:
                return r2;
            L_0x0166:
                return r1;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity$AnonymousClass5.onItemClick(android.view.View, int, float, float):boolean");
            }

            public void onLongClickRelease() {
                DialogsActivity.this.finishPreviewFragment();
            }

            public void onMove(float f, float f2) {
                DialogsActivity.this.movePreviewFragment(f2);
            }
        });
        this.swipeController = new SwipeController();
        this.itemTouchhelper = new ItemTouchHelper(this.swipeController);
        this.itemTouchhelper.attachToRecyclerView(this.listView);
        this.searchEmptyView = new EmptyTextProgressView(context2);
        this.searchEmptyView.setVisibility(8);
        this.searchEmptyView.setShowAtCenter(true);
        this.searchEmptyView.setTopImage(NUM);
        this.searchEmptyView.setText(LocaleController.getString("SettingsNoResults", NUM));
        contentView.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.progressView = new RadialProgressView(context2);
        this.progressView.setVisibility(8);
        contentView.addView(this.progressView, LayoutHelper.createFrame(-2, -2, 17));
        this.floatingButtonContainer = new FrameLayout(context2);
        FrameLayout frameLayout = this.floatingButtonContainer;
        int i3 = (this.onlySelect || this.folderId != 0) ? 8 : 0;
        frameLayout.setVisibility(i3);
        contentView.addView(this.floatingButtonContainer, LayoutHelper.createFrame((VERSION.SDK_INT >= 21 ? 56 : 60) + 20, (float) ((VERSION.SDK_INT >= 21 ? 56 : 60) + 14), (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 4.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 4.0f, 0.0f));
        this.floatingButtonContainer.setOnClickListener(new -$$Lambda$DialogsActivity$6X_tHzQFSQYoiAZxS6kgZUUhZCo(this));
        this.floatingButton = new ImageView(context2);
        this.floatingButton.setScaleType(ScaleType.CENTER);
        avatarDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
        if (VERSION.SDK_INT < 21) {
            Drawable mutate = context.getResources().getDrawable(NUM).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, Mode.MULTIPLY));
            Drawable combinedDrawable = new CombinedDrawable(mutate, avatarDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            avatarDrawable = combinedDrawable;
        }
        this.floatingButton.setBackgroundDrawable(avatarDrawable);
        this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), Mode.MULTIPLY));
        this.floatingButton.setImageResource(NUM);
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.floatingButton.setStateListAnimator(stateListAnimator);
            this.floatingButton.setOutlineProvider(new ViewOutlineProvider() {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        this.floatingButtonContainer.setContentDescription(LocaleController.getString("NewMessageTitle", NUM));
        this.floatingButtonContainer.addView(this.floatingButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, (float) (VERSION.SDK_INT >= 21 ? 56 : 60), 51, 10.0f, 0.0f, 10.0f, 0.0f));
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    if (DialogsActivity.this.searching && DialogsActivity.this.searchWas) {
                        AndroidUtilities.hideKeyboard(DialogsActivity.this.getParentActivity().getCurrentFocus());
                    }
                    DialogsActivity.this.scrollingManually = true;
                } else {
                    DialogsActivity.this.scrollingManually = false;
                }
                if (DialogsActivity.this.waitingForScrollFinished && i == 0) {
                    DialogsActivity.this.waitingForScrollFinished = false;
                }
            }

            /* JADX WARNING: Missing block: B:32:0x00f8, code skipped:
            if (java.lang.Math.abs(r0) > 1) goto L_0x0106;
     */
            public void onScrolled(androidx.recyclerview.widget.RecyclerView r6, int r7, int r8) {
                /*
                r5 = this;
                r7 = org.telegram.ui.DialogsActivity.this;
                r7 = r7.layoutManager;
                r7 = r7.findFirstVisibleItemPosition();
                r0 = org.telegram.ui.DialogsActivity.this;
                r0 = r0.layoutManager;
                r0 = r0.findLastVisibleItemPosition();
                r0 = r0 - r7;
                r0 = java.lang.Math.abs(r0);
                r1 = 1;
                r0 = r0 + r1;
                r2 = r6.getAdapter();
                r2 = r2.getItemCount();
                r3 = org.telegram.ui.DialogsActivity.this;
                r3 = r3.dialogsItemAnimator;
                r8 = -r8;
                r3.onListScroll(r8);
                r8 = org.telegram.ui.DialogsActivity.this;
                r8 = r8.searching;
                if (r8 == 0) goto L_0x0062;
            L_0x0035:
                r8 = org.telegram.ui.DialogsActivity.this;
                r8 = r8.searchWas;
                if (r8 == 0) goto L_0x0062;
            L_0x003d:
                if (r0 <= 0) goto L_0x0061;
            L_0x003f:
                r6 = org.telegram.ui.DialogsActivity.this;
                r6 = r6.layoutManager;
                r6 = r6.findLastVisibleItemPosition();
                r2 = r2 - r1;
                if (r6 != r2) goto L_0x0061;
            L_0x004c:
                r6 = org.telegram.ui.DialogsActivity.this;
                r6 = r6.dialogsSearchAdapter;
                r6 = r6.isMessagesSearchEndReached();
                if (r6 != 0) goto L_0x0061;
            L_0x0058:
                r6 = org.telegram.ui.DialogsActivity.this;
                r6 = r6.dialogsSearchAdapter;
                r6.loadMoreSearchMessages();
            L_0x0061:
                return;
            L_0x0062:
                if (r0 <= 0) goto L_0x00bf;
            L_0x0064:
                r8 = org.telegram.ui.DialogsActivity.this;
                r8 = r8.layoutManager;
                r8 = r8.findLastVisibleItemPosition();
                r0 = org.telegram.ui.DialogsActivity.this;
                r0 = r0.currentAccount;
                r2 = org.telegram.ui.DialogsActivity.this;
                r2 = r2.dialogsType;
                r3 = org.telegram.ui.DialogsActivity.this;
                r3 = r3.folderId;
                r4 = org.telegram.ui.DialogsActivity.this;
                r4 = r4.dialogsListFrozen;
                r0 = org.telegram.ui.DialogsActivity.getDialogsArray(r0, r2, r3, r4);
                r0 = r0.size();
                r0 = r0 + -10;
                if (r8 < r0) goto L_0x00bf;
            L_0x0092:
                r8 = org.telegram.ui.DialogsActivity.this;
                r8 = r8.getMessagesController();
                r0 = org.telegram.ui.DialogsActivity.this;
                r0 = r0.folderId;
                r8 = r8.isDialogsEndReached(r0);
                r8 = r8 ^ r1;
                if (r8 != 0) goto L_0x00b7;
            L_0x00a5:
                r0 = org.telegram.ui.DialogsActivity.this;
                r0 = r0.getMessagesController();
                r2 = org.telegram.ui.DialogsActivity.this;
                r2 = r2.folderId;
                r0 = r0.isServerDialogsEndReached(r2);
                if (r0 != 0) goto L_0x00bf;
            L_0x00b7:
                r0 = new org.telegram.ui.-$$Lambda$DialogsActivity$7$HVJsjKiGjxTuaYps58vZ5Ui0sb8;
                r0.<init>(r5, r8);
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
            L_0x00bf:
                r8 = org.telegram.ui.DialogsActivity.this;
                r8 = r8.floatingButtonContainer;
                r8 = r8.getVisibility();
                r0 = 8;
                if (r8 == r0) goto L_0x0131;
            L_0x00cd:
                r8 = 0;
                r6 = r6.getChildAt(r8);
                if (r6 == 0) goto L_0x00d9;
            L_0x00d4:
                r6 = r6.getTop();
                goto L_0x00da;
            L_0x00d9:
                r6 = 0;
            L_0x00da:
                r0 = org.telegram.ui.DialogsActivity.this;
                r0 = r0.prevPosition;
                if (r0 != r7) goto L_0x00fb;
            L_0x00e2:
                r0 = org.telegram.ui.DialogsActivity.this;
                r0 = r0.prevTop;
                r0 = r0 - r6;
                r2 = org.telegram.ui.DialogsActivity.this;
                r2 = r2.prevTop;
                if (r6 >= r2) goto L_0x00f3;
            L_0x00f1:
                r2 = 1;
                goto L_0x00f4;
            L_0x00f3:
                r2 = 0;
            L_0x00f4:
                r0 = java.lang.Math.abs(r0);
                if (r0 <= r1) goto L_0x0107;
            L_0x00fa:
                goto L_0x0106;
            L_0x00fb:
                r0 = org.telegram.ui.DialogsActivity.this;
                r0 = r0.prevPosition;
                if (r7 <= r0) goto L_0x0105;
            L_0x0103:
                r2 = 1;
                goto L_0x0106;
            L_0x0105:
                r2 = 0;
            L_0x0106:
                r8 = 1;
            L_0x0107:
                if (r8 == 0) goto L_0x0122;
            L_0x0109:
                r8 = org.telegram.ui.DialogsActivity.this;
                r8 = r8.scrollUpdated;
                if (r8 == 0) goto L_0x0122;
            L_0x0111:
                if (r2 != 0) goto L_0x011d;
            L_0x0113:
                if (r2 != 0) goto L_0x0122;
            L_0x0115:
                r8 = org.telegram.ui.DialogsActivity.this;
                r8 = r8.scrollingManually;
                if (r8 == 0) goto L_0x0122;
            L_0x011d:
                r8 = org.telegram.ui.DialogsActivity.this;
                r8.hideFloatingButton(r2);
            L_0x0122:
                r8 = org.telegram.ui.DialogsActivity.this;
                r8.prevPosition = r7;
                r7 = org.telegram.ui.DialogsActivity.this;
                r7.prevTop = r6;
                r6 = org.telegram.ui.DialogsActivity.this;
                r6.scrollUpdated = r1;
            L_0x0131:
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity$AnonymousClass7.onScrolled(androidx.recyclerview.widget.RecyclerView, int, int):void");
            }

            public /* synthetic */ void lambda$onScrolled$0$DialogsActivity$7(boolean z) {
                DialogsActivity.this.getMessagesController().loadDialogs(DialogsActivity.this.folderId, -1, 100, z);
            }
        });
        this.archivePullViewState = SharedConfig.archiveHidden ? 2 : 0;
        if (this.pullForegroundDrawable == null && this.folderId == 0) {
            this.pullForegroundDrawable = new PullForegroundDrawable(LocaleController.getString("AccSwipeForArchive", NUM), LocaleController.getString("AccReleaseForArchive", NUM)) {
                /* Access modifiers changed, original: protected */
                public float getViewOffset() {
                    return DialogsActivity.this.listView.getViewOffset();
                }
            };
            if (hasHiddenArchive()) {
                this.pullForegroundDrawable.showHidden();
            } else {
                this.pullForegroundDrawable.doNotShow();
            }
            this.pullForegroundDrawable.setWillDraw(this.archivePullViewState != 0);
        }
        if (this.searchString == null) {
            i2 = -2;
            this.dialogsAdapter = new DialogsAdapter(context, this.dialogsType, this.folderId, this.onlySelect) {
                public void notifyDataSetChanged() {
                    DialogsActivity.this.lastItemsCount = getItemCount();
                    super.notifyDataSetChanged();
                }
            };
            if (AndroidUtilities.isTablet()) {
                long j = this.openedDialogId;
                if (j != 0) {
                    this.dialogsAdapter.setOpenedDialogId(j);
                }
            }
            this.dialogsAdapter.setArchivedPullDrawable(this.pullForegroundDrawable);
            this.listView.setAdapter(this.dialogsAdapter);
        } else {
            i2 = -2;
        }
        i = this.searchString != null ? 2 : !this.onlySelect ? 1 : 0;
        this.dialogsSearchAdapter = new DialogsSearchAdapter(context2, i, this.dialogsType);
        this.dialogsSearchAdapter.setDelegate(new DialogsSearchAdapterDelegate() {
            public void searchStateChanged(boolean z) {
                if (!DialogsActivity.this.searching || !DialogsActivity.this.searchWas || DialogsActivity.this.searchEmptyView == null) {
                    return;
                }
                if (z) {
                    DialogsActivity.this.searchEmptyView.showProgress();
                } else {
                    DialogsActivity.this.searchEmptyView.showTextView();
                }
            }

            public void didPressedOnSubDialog(long j) {
                if (!DialogsActivity.this.onlySelect) {
                    int i = (int) j;
                    Bundle bundle = new Bundle();
                    if (i > 0) {
                        bundle.putInt("user_id", i);
                    } else {
                        bundle.putInt("chat_id", -i);
                    }
                    DialogsActivity.this.closeSearch();
                    if (AndroidUtilities.isTablet() && DialogsActivity.this.dialogsAdapter != null) {
                        DialogsActivity.this.dialogsAdapter.setOpenedDialogId(DialogsActivity.this.openedDialogId = j);
                        DialogsActivity.this.updateVisibleRows(512);
                    }
                    if (DialogsActivity.this.searchString != null) {
                        if (DialogsActivity.this.getMessagesController().checkCanOpenChat(bundle, DialogsActivity.this)) {
                            DialogsActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                            DialogsActivity.this.presentFragment(new ChatActivity(bundle));
                        }
                    } else if (DialogsActivity.this.getMessagesController().checkCanOpenChat(bundle, DialogsActivity.this)) {
                        DialogsActivity.this.presentFragment(new ChatActivity(bundle));
                    }
                } else if (!DialogsActivity.this.validateSlowModeDialog(j)) {
                } else {
                    if (DialogsActivity.this.dialogsAdapter.hasSelectedDialogs()) {
                        DialogsActivity.this.dialogsAdapter.addOrRemoveSelectedDialog(j, null);
                        DialogsActivity.this.updateSelectedCount();
                        DialogsActivity.this.closeSearch();
                    } else {
                        DialogsActivity.this.didSelectResult(j, true, false);
                    }
                }
            }

            public void needRemoveHint(int i) {
                if (DialogsActivity.this.getParentActivity() != null && DialogsActivity.this.getMessagesController().getUser(Integer.valueOf(i)) != null) {
                    Builder builder = new Builder(DialogsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("ChatHintsDeleteAlertTitle", NUM));
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("ChatHintsDeleteAlert", NUM, ContactsController.formatName(r0.first_name, r0.last_name))));
                    builder.setPositiveButton(LocaleController.getString("StickersRemove", NUM), new -$$Lambda$DialogsActivity$10$7JwjKBJfwtWw34NhDCOKWZDwXRQ(this, i));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                    AlertDialog create = builder.create();
                    DialogsActivity.this.showDialog(create);
                    TextView textView = (TextView) create.getButton(-1);
                    if (textView != null) {
                        textView.setTextColor(Theme.getColor("dialogTextRed2"));
                    }
                }
            }

            public /* synthetic */ void lambda$needRemoveHint$0$DialogsActivity$10(int i, DialogInterface dialogInterface, int i2) {
                DialogsActivity.this.getMediaDataController().removePeer(i);
            }

            public void needClearList() {
                Builder builder = new Builder(DialogsActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("ClearSearchAlertTitle", NUM));
                builder.setMessage(LocaleController.getString("ClearSearchAlert", NUM));
                builder.setPositiveButton(LocaleController.getString("ClearButton", NUM).toUpperCase(), new -$$Lambda$DialogsActivity$10$Q2pbWf5Q1Cqk_6Ep5ajOPUW2nyo(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                AlertDialog create = builder.create();
                DialogsActivity.this.showDialog(create);
                TextView textView = (TextView) create.getButton(-1);
                if (textView != null) {
                    textView.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            }

            public /* synthetic */ void lambda$needClearList$1$DialogsActivity$10(DialogInterface dialogInterface, int i) {
                if (DialogsActivity.this.dialogsSearchAdapter.isRecentSearchDisplayed()) {
                    DialogsActivity.this.dialogsSearchAdapter.clearRecentSearch();
                } else {
                    DialogsActivity.this.dialogsSearchAdapter.clearRecentHashtags();
                }
            }
        });
        this.listView.setEmptyView(this.folderId == 0 ? this.progressView : null);
        String str2 = this.searchString;
        if (str2 != null) {
            this.actionBar.openSearchField(str2, false);
        }
        if (!this.onlySelect && this.dialogsType == 0) {
            FragmentContextView fragmentContextView = new FragmentContextView(context2, this, true);
            contentView.addView(fragmentContextView, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
            FragmentContextView fragmentContextView2 = new FragmentContextView(context2, this, false);
            contentView.addView(fragmentContextView2, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
            fragmentContextView2.setAdditionalContextView(fragmentContextView);
            fragmentContextView.setAdditionalContextView(fragmentContextView2);
        } else if (this.dialogsType == 3 && this.selectAlertString == null) {
            ChatActivityEnterView chatActivityEnterView = this.commentView;
            if (chatActivityEnterView != null) {
                chatActivityEnterView.onDestroy();
            }
            this.commentView = new ChatActivityEnterView(getParentActivity(), contentView, null, false);
            this.commentView.setAllowStickersAndGifs(false, false);
            this.commentView.setForceShowSendButton(true, false);
            this.commentView.setVisibility(8);
            contentView.addView(this.commentView, LayoutHelper.createFrame(-1, i2, 83));
            this.commentView.setDelegate(new ChatActivityEnterViewDelegate() {
                public void didPressedAttachButton() {
                }

                public /* synthetic */ boolean hasScheduledMessages() {
                    return -CC.$default$hasScheduledMessages(this);
                }

                public void needChangeVideoPreviewState(int i, float f) {
                }

                public void needSendTyping() {
                }

                public void needShowMediaBanHint() {
                }

                public void needStartRecordAudio(int i) {
                }

                public void needStartRecordVideo(int i, boolean z, int i2) {
                }

                public void onAttachButtonHidden() {
                }

                public void onAttachButtonShow() {
                }

                public void onMessageEditEnd(boolean z) {
                }

                public void onPreAudioVideoRecord() {
                }

                public void onStickersExpandedChange() {
                }

                public void onStickersTab(boolean z) {
                }

                public void onSwitchRecordMode(boolean z) {
                }

                public void onTextChanged(CharSequence charSequence, boolean z) {
                }

                public void onTextSelectionChanged(int i, int i2) {
                }

                public void onTextSpansChanged(CharSequence charSequence) {
                }

                public void onUpdateSlowModeButton(View view, boolean z, CharSequence charSequence) {
                }

                public void onWindowSizeChanged(int i) {
                }

                public /* synthetic */ void openScheduledMessages() {
                    -CC.$default$openScheduledMessages(this);
                }

                public /* synthetic */ void scrollToSendingMessage() {
                    -CC.$default$scrollToSendingMessage(this);
                }

                public void onMessageSend(CharSequence charSequence, boolean z, int i) {
                    if (DialogsActivity.this.delegate != null) {
                        ArrayList selectedDialogs = DialogsActivity.this.dialogsAdapter.getSelectedDialogs();
                        if (!selectedDialogs.isEmpty()) {
                            DialogsActivity.this.delegate.didSelectDialogs(DialogsActivity.this, selectedDialogs, charSequence, false);
                        }
                    }
                }
            });
        }
        for (i = 0; i < 2; i++) {
            this.undoView[i] = new UndoView(context2) {
                public void setTranslationY(float f) {
                    super.setTranslationY(f);
                    if (this == DialogsActivity.this.undoView[0] && DialogsActivity.this.undoView[1].getVisibility() != 0) {
                        float measuredHeight = ((float) (getMeasuredHeight() + AndroidUtilities.dp(8.0f))) - f;
                        if (!DialogsActivity.this.floatingHidden) {
                            DialogsActivity.this.updateFloatingButtonOffset();
                        }
                        DialogsActivity.this.additionalFloatingTranslation = measuredHeight;
                    }
                }

                /* Access modifiers changed, original: protected */
                public boolean canUndo() {
                    return DialogsActivity.this.dialogsItemAnimator.isRunning() ^ 1;
                }
            };
            contentView.addView(this.undoView[i], LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        }
        if (this.folderId != 0) {
            this.actionBar.setBackgroundColor(Theme.getColor("actionBarDefaultArchived"));
            this.listView.setGlowColor(Theme.getColor("actionBarDefaultArchived"));
            this.actionBar.setTitleColor(Theme.getColor("actionBarDefaultArchivedTitle"));
            this.actionBar.setItemsColor(Theme.getColor("actionBarDefaultArchivedIcon"), false);
            this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarDefaultArchivedSelector"), false);
            this.actionBar.setSearchTextColor(Theme.getColor("actionBarDefaultArchivedSearch"), false);
            this.actionBar.setSearchTextColor(Theme.getColor("actionBarDefaultSearchArchivedPlaceholder"), true);
        }
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$DialogsActivity() {
        hideFloatingButton(false);
        this.listView.smoothScrollToPosition(hasHiddenArchive());
    }

    /* JADX WARNING: Removed duplicated region for block: B:97:0x0183  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x0182 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x0182 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x0183  */
    public /* synthetic */ void lambda$createView$3$DialogsActivity(android.view.View r12, int r13) {
        /*
        r11 = this;
        r0 = r11.listView;
        if (r0 == 0) goto L_0x0254;
    L_0x0004:
        r0 = r0.getAdapter();
        if (r0 == 0) goto L_0x0254;
    L_0x000a:
        r0 = r11.getParentActivity();
        if (r0 != 0) goto L_0x0012;
    L_0x0010:
        goto L_0x0254;
    L_0x0012:
        r0 = r11.listView;
        r0 = r0.getAdapter();
        r1 = r11.dialogsAdapter;
        r2 = 1;
        r3 = 32;
        r4 = 0;
        r6 = 0;
        if (r0 != r1) goto L_0x00f3;
    L_0x0022:
        r13 = r1.getItem(r13);
        r1 = r13 instanceof org.telegram.tgnet.TLRPC.User;
        if (r1 == 0) goto L_0x0031;
    L_0x002a:
        r13 = (org.telegram.tgnet.TLRPC.User) r13;
        r13 = r13.id;
    L_0x002e:
        r7 = (long) r13;
        goto L_0x017c;
    L_0x0031:
        r1 = r13 instanceof org.telegram.tgnet.TLRPC.Dialog;
        if (r1 == 0) goto L_0x006b;
    L_0x0035:
        r13 = (org.telegram.tgnet.TLRPC.Dialog) r13;
        r1 = r13 instanceof org.telegram.tgnet.TLRPC.TL_dialogFolder;
        if (r1 == 0) goto L_0x005d;
    L_0x003b:
        r12 = r11.actionBar;
        r12 = r12.isActionModeShowed();
        if (r12 == 0) goto L_0x0044;
    L_0x0043:
        return;
    L_0x0044:
        r13 = (org.telegram.tgnet.TLRPC.TL_dialogFolder) r13;
        r12 = new android.os.Bundle;
        r12.<init>();
        r13 = r13.folder;
        r13 = r13.id;
        r0 = "folderId";
        r12.putInt(r0, r13);
        r13 = new org.telegram.ui.DialogsActivity;
        r13.<init>(r12);
        r11.presentFragment(r13);
        return;
    L_0x005d:
        r7 = r13.id;
        r1 = r11.actionBar;
        r1 = r1.isActionModeShowed();
        if (r1 == 0) goto L_0x017c;
    L_0x0067:
        r11.showOrUpdateActionMode(r13, r12);
        return;
    L_0x006b:
        r1 = r13 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlChat;
        if (r1 == 0) goto L_0x0075;
    L_0x006f:
        r13 = (org.telegram.tgnet.TLRPC.TL_recentMeUrlChat) r13;
        r13 = r13.chat_id;
    L_0x0073:
        r13 = -r13;
        goto L_0x002e;
    L_0x0075:
        r1 = r13 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlUser;
        if (r1 == 0) goto L_0x007e;
    L_0x0079:
        r13 = (org.telegram.tgnet.TLRPC.TL_recentMeUrlUser) r13;
        r13 = r13.user_id;
        goto L_0x002e;
    L_0x007e:
        r1 = r13 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlChatInvite;
        if (r1 == 0) goto L_0x00c6;
    L_0x0082:
        r13 = (org.telegram.tgnet.TLRPC.TL_recentMeUrlChatInvite) r13;
        r1 = r13.chat_invite;
        r7 = r1.chat;
        if (r7 != 0) goto L_0x0092;
    L_0x008a:
        r7 = r1.channel;
        if (r7 == 0) goto L_0x00a2;
    L_0x008e:
        r7 = r1.megagroup;
        if (r7 != 0) goto L_0x00a2;
    L_0x0092:
        r7 = r1.chat;
        if (r7 == 0) goto L_0x00be;
    L_0x0096:
        r7 = org.telegram.messenger.ChatObject.isChannel(r7);
        if (r7 == 0) goto L_0x00a2;
    L_0x009c:
        r7 = r1.chat;
        r7 = r7.megagroup;
        if (r7 == 0) goto L_0x00be;
    L_0x00a2:
        r12 = r13.url;
        r13 = 47;
        r13 = r12.indexOf(r13);
        if (r13 <= 0) goto L_0x00b1;
    L_0x00ac:
        r13 = r13 + r2;
        r12 = r12.substring(r13);
    L_0x00b1:
        r13 = new org.telegram.ui.Components.JoinGroupAlert;
        r0 = r11.getParentActivity();
        r13.<init>(r0, r1, r12, r11);
        r11.showDialog(r13);
        return;
    L_0x00be:
        r13 = r1.chat;
        if (r13 == 0) goto L_0x00c5;
    L_0x00c2:
        r13 = r13.id;
        goto L_0x0073;
    L_0x00c5:
        return;
    L_0x00c6:
        r12 = r13 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlStickerSet;
        if (r12 == 0) goto L_0x00ee;
    L_0x00ca:
        r13 = (org.telegram.tgnet.TLRPC.TL_recentMeUrlStickerSet) r13;
        r12 = r13.set;
        r12 = r12.set;
        r3 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetID;
        r3.<init>();
        r0 = r12.id;
        r3.id = r0;
        r12 = r12.access_hash;
        r3.access_hash = r12;
        r12 = new org.telegram.ui.Components.StickersAlert;
        r1 = r11.getParentActivity();
        r4 = 0;
        r5 = 0;
        r0 = r12;
        r2 = r11;
        r0.<init>(r1, r2, r3, r4, r5);
        r11.showDialog(r12);
        return;
    L_0x00ee:
        r12 = r13 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlUnknown;
        if (r12 == 0) goto L_0x00f2;
    L_0x00f2:
        return;
    L_0x00f3:
        r1 = r11.dialogsSearchAdapter;
        if (r0 != r1) goto L_0x017b;
    L_0x00f7:
        r1 = r1.getItem(r13);
        r7 = r11.dialogsSearchAdapter;
        r13 = r7.isGlobalSearch(r13);
        r7 = r1 instanceof org.telegram.tgnet.TLRPC.User;
        if (r7 == 0) goto L_0x0114;
    L_0x0105:
        r1 = (org.telegram.tgnet.TLRPC.User) r1;
        r7 = r1.id;
        r7 = (long) r7;
        r9 = r11.onlySelect;
        if (r9 != 0) goto L_0x017d;
    L_0x010e:
        r11.searchDialogId = r7;
        r11.searchObject = r1;
        goto L_0x017d;
    L_0x0114:
        r7 = r1 instanceof org.telegram.tgnet.TLRPC.Chat;
        if (r7 == 0) goto L_0x0127;
    L_0x0118:
        r1 = (org.telegram.tgnet.TLRPC.Chat) r1;
        r7 = r1.id;
        r7 = -r7;
        r7 = (long) r7;
        r9 = r11.onlySelect;
        if (r9 != 0) goto L_0x017d;
    L_0x0122:
        r11.searchDialogId = r7;
        r11.searchObject = r1;
        goto L_0x017d;
    L_0x0127:
        r7 = r1 instanceof org.telegram.tgnet.TLRPC.EncryptedChat;
        if (r7 == 0) goto L_0x013a;
    L_0x012b:
        r1 = (org.telegram.tgnet.TLRPC.EncryptedChat) r1;
        r7 = r1.id;
        r7 = (long) r7;
        r7 = r7 << r3;
        r9 = r11.onlySelect;
        if (r9 != 0) goto L_0x017d;
    L_0x0135:
        r11.searchDialogId = r7;
        r11.searchObject = r1;
        goto L_0x017d;
    L_0x013a:
        r7 = r1 instanceof org.telegram.messenger.MessageObject;
        if (r7 == 0) goto L_0x0152;
    L_0x013e:
        r1 = (org.telegram.messenger.MessageObject) r1;
        r7 = r1.getDialogId();
        r1 = r1.getId();
        r9 = r11.dialogsSearchAdapter;
        r10 = r9.getLastSearchString();
        r9.addHashtagsFromMessage(r10);
        goto L_0x017e;
    L_0x0152:
        r7 = r1 instanceof java.lang.String;
        if (r7 == 0) goto L_0x0179;
    L_0x0156:
        r1 = (java.lang.String) r1;
        r7 = r11.dialogsSearchAdapter;
        r7 = r7.isHashtagSearch();
        if (r7 == 0) goto L_0x0166;
    L_0x0160:
        r7 = r11.actionBar;
        r7.openSearchField(r1, r6);
        goto L_0x0179;
    L_0x0166:
        r7 = "section";
        r7 = r1.equals(r7);
        if (r7 != 0) goto L_0x0179;
    L_0x016e:
        r7 = new org.telegram.ui.NewContactActivity;
        r7.<init>();
        r7.setInitialPhoneNumber(r1);
        r11.presentFragment(r7);
    L_0x0179:
        r7 = r4;
        goto L_0x017d;
    L_0x017b:
        r7 = r4;
    L_0x017c:
        r13 = 0;
    L_0x017d:
        r1 = 0;
    L_0x017e:
        r9 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1));
        if (r9 != 0) goto L_0x0183;
    L_0x0182:
        return;
    L_0x0183:
        r4 = r11.onlySelect;
        if (r4 == 0) goto L_0x01a5;
    L_0x0187:
        r13 = r11.validateSlowModeDialog(r7);
        if (r13 != 0) goto L_0x018e;
    L_0x018d:
        return;
    L_0x018e:
        r13 = r11.dialogsAdapter;
        r13 = r13.hasSelectedDialogs();
        if (r13 == 0) goto L_0x01a0;
    L_0x0196:
        r13 = r11.dialogsAdapter;
        r13.addOrRemoveSelectedDialog(r7, r12);
        r11.updateSelectedCount();
        goto L_0x0254;
    L_0x01a0:
        r11.didSelectResult(r7, r2, r6);
        goto L_0x0254;
    L_0x01a5:
        r12 = new android.os.Bundle;
        r12.<init>();
        r2 = (int) r7;
        r3 = r7 >> r3;
        r4 = (int) r3;
        if (r2 == 0) goto L_0x01e0;
    L_0x01b0:
        if (r2 <= 0) goto L_0x01b8;
    L_0x01b2:
        r3 = "user_id";
        r12.putInt(r3, r2);
        goto L_0x01e5;
    L_0x01b8:
        if (r2 >= 0) goto L_0x01e5;
    L_0x01ba:
        if (r1 == 0) goto L_0x01d9;
    L_0x01bc:
        r3 = r11.getMessagesController();
        r4 = -r2;
        r4 = java.lang.Integer.valueOf(r4);
        r3 = r3.getChat(r4);
        if (r3 == 0) goto L_0x01d9;
    L_0x01cb:
        r4 = r3.migrated_to;
        if (r4 == 0) goto L_0x01d9;
    L_0x01cf:
        r4 = "migrated_to";
        r12.putInt(r4, r2);
        r2 = r3.migrated_to;
        r2 = r2.channel_id;
        r2 = -r2;
    L_0x01d9:
        r2 = -r2;
        r3 = "chat_id";
        r12.putInt(r3, r2);
        goto L_0x01e5;
    L_0x01e0:
        r2 = "enc_id";
        r12.putInt(r2, r4);
    L_0x01e5:
        if (r1 == 0) goto L_0x01ed;
    L_0x01e7:
        r13 = "message_id";
        r12.putInt(r13, r1);
        goto L_0x0201;
    L_0x01ed:
        if (r13 != 0) goto L_0x01f3;
    L_0x01ef:
        r11.closeSearch();
        goto L_0x0201;
    L_0x01f3:
        r13 = r11.searchObject;
        if (r13 == 0) goto L_0x0201;
    L_0x01f7:
        r1 = r11.dialogsSearchAdapter;
        r2 = r11.searchDialogId;
        r1.putRecentSearch(r2, r13);
        r13 = 0;
        r11.searchObject = r13;
    L_0x0201:
        r13 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r13 == 0) goto L_0x0220;
    L_0x0207:
        r1 = r11.openedDialogId;
        r13 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1));
        if (r13 != 0) goto L_0x0212;
    L_0x020d:
        r13 = r11.dialogsSearchAdapter;
        if (r0 == r13) goto L_0x0212;
    L_0x0211:
        return;
    L_0x0212:
        r13 = r11.dialogsAdapter;
        if (r13 == 0) goto L_0x0220;
    L_0x0216:
        r11.openedDialogId = r7;
        r13.setOpenedDialogId(r7);
        r13 = 512; // 0x200 float:7.175E-43 double:2.53E-321;
        r11.updateVisibleRows(r13);
    L_0x0220:
        r13 = r11.searchString;
        if (r13 == 0) goto L_0x0242;
    L_0x0224:
        r13 = r11.getMessagesController();
        r13 = r13.checkCanOpenChat(r12, r11);
        if (r13 == 0) goto L_0x0254;
    L_0x022e:
        r13 = r11.getNotificationCenter();
        r0 = org.telegram.messenger.NotificationCenter.closeChats;
        r1 = new java.lang.Object[r6];
        r13.postNotificationName(r0, r1);
        r13 = new org.telegram.ui.ChatActivity;
        r13.<init>(r12);
        r11.presentFragment(r13);
        goto L_0x0254;
    L_0x0242:
        r13 = r11.getMessagesController();
        r13 = r13.checkCanOpenChat(r12, r11);
        if (r13 == 0) goto L_0x0254;
    L_0x024c:
        r13 = new org.telegram.ui.ChatActivity;
        r13.<init>(r12);
        r11.presentFragment(r13);
    L_0x0254:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.lambda$createView$3$DialogsActivity(android.view.View, int):void");
    }

    public /* synthetic */ void lambda$createView$4$DialogsActivity(View view) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("destroyAfterSelect", true);
        presentFragment(new ContactsActivity(bundle));
    }

    public void onResume() {
        super.onResume();
        DialogsAdapter dialogsAdapter = this.dialogsAdapter;
        if (!(dialogsAdapter == null || this.dialogsListFrozen)) {
            dialogsAdapter.notifyDataSetChanged();
        }
        ChatActivityEnterView chatActivityEnterView = this.commentView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.onResume();
        }
        if (!this.onlySelect && this.folderId == 0) {
            getMediaDataController().checkStickers(4);
        }
        DialogsSearchAdapter dialogsSearchAdapter = this.dialogsSearchAdapter;
        if (dialogsSearchAdapter != null) {
            dialogsSearchAdapter.notifyDataSetChanged();
        }
        String str = "AppName";
        if (this.checkPermission && !this.onlySelect && VERSION.SDK_INT >= 23) {
            Context parentActivity = getParentActivity();
            if (parentActivity != null) {
                this.checkPermission = false;
                String str2 = "android.permission.READ_CONTACTS";
                Object obj = parentActivity.checkSelfPermission(str2) != 0 ? 1 : null;
                String str3 = "android.permission.WRITE_EXTERNAL_STORAGE";
                Object obj2 = parentActivity.checkSelfPermission(str3) != 0 ? 1 : null;
                if (!(obj == null && obj2 == null)) {
                    AlertDialog create;
                    if (obj != null && this.askAboutContacts && getUserConfig().syncContacts && parentActivity.shouldShowRequestPermissionRationale(str2)) {
                        create = AlertsCreator.createContactsPermissionDialog(parentActivity, new -$$Lambda$DialogsActivity$h_XfCIY8uwUxVhs5pPRQujZay_s(this)).create();
                        this.permissionDialog = create;
                        showDialog(create);
                    } else if (obj2 == null || !parentActivity.shouldShowRequestPermissionRationale(str3)) {
                        askForPermissons(true);
                    } else {
                        Builder builder = new Builder(parentActivity);
                        builder.setTitle(LocaleController.getString(str, NUM));
                        builder.setMessage(LocaleController.getString("PermissionStorage", NUM));
                        builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
                        create = builder.create();
                        this.permissionDialog = create;
                        showDialog(create);
                    }
                }
            }
        } else if (!this.onlySelect && XiaomiUtilities.isMIUI() && VERSION.SDK_INT >= 19 && !XiaomiUtilities.isCustomPermissionGranted(10020)) {
            if (getParentActivity() != null && !MessagesController.getGlobalNotificationsSettings().getBoolean("askedAboutMiuiLockscreen", false)) {
                showDialog(new Builder(getParentActivity()).setTitle(LocaleController.getString(str, NUM)).setMessage(LocaleController.getString("PermissionXiaomiLockscreen", NUM)).setPositiveButton(LocaleController.getString("PermissionOpenSettings", NUM), new -$$Lambda$DialogsActivity$ad-mqRrDwcRXwvhesRJKLOMcnHI(this)).setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", NUM), -$$Lambda$DialogsActivity$om5eIuKoD-TUjWJtmdVNYsc-Woc.INSTANCE).create());
            } else {
                return;
            }
        }
        if (this.archivePullViewState == 2 && this.layoutManager.findFirstVisibleItemPosition() == 0 && hasHiddenArchive()) {
            this.layoutManager.scrollToPositionWithOffset(1, 0);
        }
    }

    public /* synthetic */ void lambda$onResume$5$DialogsActivity(int i) {
        this.askAboutContacts = i != 0;
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askAboutContacts", this.askAboutContacts).commit();
        askForPermissons(false);
    }

    /* JADX WARNING: Missing exception handler attribute for start block: B:4:0x000e */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:10:?, code skipped:
            return;
     */
    public /* synthetic */ void lambda$onResume$6$DialogsActivity(android.content.DialogInterface r2, int r3) {
        /*
        r1 = this;
        r2 = org.telegram.messenger.XiaomiUtilities.getPermissionManagerIntent();
        if (r2 == 0) goto L_0x003f;
    L_0x0006:
        r3 = r1.getParentActivity();	 Catch:{ Exception -> 0x000e }
        r3.startActivity(r2);	 Catch:{ Exception -> 0x000e }
        goto L_0x003f;
    L_0x000e:
        r2 = new android.content.Intent;	 Catch:{ Exception -> 0x003b }
        r3 = "android.settings.APPLICATION_DETAILS_SETTINGS";
        r2.<init>(r3);	 Catch:{ Exception -> 0x003b }
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x003b }
        r3.<init>();	 Catch:{ Exception -> 0x003b }
        r0 = "package:";
        r3.append(r0);	 Catch:{ Exception -> 0x003b }
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x003b }
        r0 = r0.getPackageName();	 Catch:{ Exception -> 0x003b }
        r3.append(r0);	 Catch:{ Exception -> 0x003b }
        r3 = r3.toString();	 Catch:{ Exception -> 0x003b }
        r3 = android.net.Uri.parse(r3);	 Catch:{ Exception -> 0x003b }
        r2.setData(r3);	 Catch:{ Exception -> 0x003b }
        r3 = r1.getParentActivity();	 Catch:{ Exception -> 0x003b }
        r3.startActivity(r2);	 Catch:{ Exception -> 0x003b }
        goto L_0x003f;
    L_0x003b:
        r2 = move-exception;
        org.telegram.messenger.FileLog.e(r2);
    L_0x003f:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.lambda$onResume$6$DialogsActivity(android.content.DialogInterface, int):void");
    }

    public void onPause() {
        super.onPause();
        ChatActivityEnterView chatActivityEnterView = this.commentView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.onResume();
        }
        UndoView[] undoViewArr = this.undoView;
        if (undoViewArr[0] != null) {
            undoViewArr[0].hide(true, 0);
        }
    }

    public boolean onBackPressed() {
        ActionBar actionBar = this.actionBar;
        if (actionBar == null || !actionBar.isActionModeShowed()) {
            ChatActivityEnterView chatActivityEnterView = this.commentView;
            if (chatActivityEnterView == null || !chatActivityEnterView.isPopupShowing()) {
                return super.onBackPressed();
            }
            this.commentView.hidePopup(true);
            return false;
        }
        hideActionMode(true);
        return false;
    }

    /* Access modifiers changed, original: protected */
    public void onBecomeFullyHidden() {
        if (this.closeSearchFieldOnHide) {
            ActionBar actionBar = this.actionBar;
            if (actionBar != null) {
                actionBar.closeSearchField();
            }
            TLObject tLObject = this.searchObject;
            if (tLObject != null) {
                this.dialogsSearchAdapter.putRecentSearch(this.searchDialogId, tLObject);
                this.searchObject = null;
            }
            this.closeSearchFieldOnHide = false;
        }
        UndoView[] undoViewArr = this.undoView;
        if (undoViewArr[0] != null) {
            undoViewArr[0].hide(true, 0);
        }
    }

    private void updateFloatingButtonOffset() {
        this.floatingButtonContainer.setTranslationY(this.floatingButtonTranslation - (this.additionalFloatingTranslation * (1.0f - this.floatingButtonHideProgress)));
    }

    private boolean hasHiddenArchive() {
        return this.listView.getAdapter() == this.dialogsAdapter && !this.onlySelect && this.dialogsType == 0 && this.folderId == 0 && getMessagesController().hasHiddenArchive();
    }

    private boolean waitingForDialogsAnimationEnd() {
        return (!this.dialogsItemAnimator.isRunning() && this.dialogRemoveFinished == 0 && this.dialogInsertFinished == 0 && this.dialogChangeFinished == 0) ? false : true;
    }

    private void onDialogAnimationFinished() {
        this.dialogRemoveFinished = 0;
        this.dialogInsertFinished = 0;
        this.dialogChangeFinished = 0;
        AndroidUtilities.runOnUIThread(new -$$Lambda$DialogsActivity$M7BILTEWS-s5OccwAnpFNVy9dGs(this));
    }

    public /* synthetic */ void lambda$onDialogAnimationFinished$8$DialogsActivity() {
        if (this.folderId != 0 && frozenDialogsList.isEmpty()) {
            this.listView.setEmptyView(null);
            this.progressView.setVisibility(4);
            finishFragment();
        }
        setDialogsListFrozen(false);
        updateDialogIndices();
    }

    private void hideActionMode(boolean z) {
        this.actionBar.hideActionMode();
        if (this.menuDrawable != null) {
            this.actionBar.setBackButtonContentDescription(LocaleController.getString("AccDescrOpenMenu", NUM));
        }
        this.dialogsAdapter.getSelectedDialogs().clear();
        MenuDrawable menuDrawable = this.menuDrawable;
        if (menuDrawable != null) {
            menuDrawable.setRotation(0.0f, true);
        } else {
            BackDrawable backDrawable = this.backDrawable;
            if (backDrawable != null) {
                backDrawable.setRotation(0.0f, true);
            }
        }
        int i = 0;
        this.allowMoving = false;
        if (this.movingWas) {
            getMessagesController().reorderPinnedDialogs(this.folderId, null, 0);
            this.movingWas = false;
        }
        updateCounters(true);
        this.dialogsAdapter.onReorderStateChanged(false);
        if (z) {
            i = 8192;
        }
        updateVisibleRows(i | 196608);
    }

    private int getPinnedCount() {
        ArrayList dialogs = getMessagesController().getDialogs(this.folderId);
        int size = dialogs.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            Dialog dialog = (Dialog) dialogs.get(i2);
            if (!(dialog instanceof TL_dialogFolder)) {
                long j = dialog.id;
                if (!dialog.pinned) {
                    break;
                }
                i++;
            }
        }
        return i;
    }

    /* JADX WARNING: Removed duplicated region for block: B:123:0x02f5  */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x02bc  */
    private void perfromSelectedDialogsAction(int r30, boolean r31) {
        /*
        r29 = this;
        r7 = r29;
        r2 = r30;
        r0 = r29.getParentActivity();
        if (r0 != 0) goto L_0x000b;
    L_0x000a:
        return;
    L_0x000b:
        r0 = r7.dialogsAdapter;
        r0 = r0.getSelectedDialogs();
        r1 = r0.size();
        r3 = 105; // 0x69 float:1.47E-43 double:5.2E-322;
        r6 = 4;
        r8 = 0;
        r9 = 1;
        r10 = 0;
        if (r2 != r3) goto L_0x009f;
    L_0x001d:
        r1 = new java.util.ArrayList;
        r1.<init>(r0);
        r11 = r29.getMessagesController();
        r0 = r7.folderId;
        if (r0 != 0) goto L_0x002c;
    L_0x002a:
        r13 = 1;
        goto L_0x002d;
    L_0x002c:
        r13 = 0;
    L_0x002d:
        r14 = -1;
        r15 = 0;
        r16 = 0;
        r12 = r1;
        r11.addDialogToFolder(r12, r13, r14, r15, r16);
        r7.hideActionMode(r10);
        r0 = r7.folderId;
        if (r0 != 0) goto L_0x0081;
    L_0x003c:
        r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings();
        r2 = "archivehint_l";
        r3 = r0.getBoolean(r2, r10);
        if (r3 != 0) goto L_0x004c;
    L_0x0048:
        r3 = org.telegram.messenger.SharedConfig.archiveHidden;
        if (r3 == 0) goto L_0x004d;
    L_0x004c:
        r10 = 1;
    L_0x004d:
        if (r10 != 0) goto L_0x005a;
    L_0x004f:
        r0 = r0.edit();
        r0 = r0.putBoolean(r2, r9);
        r0.commit();
    L_0x005a:
        if (r10 == 0) goto L_0x0066;
    L_0x005c:
        r0 = r1.size();
        if (r0 <= r9) goto L_0x0064;
    L_0x0062:
        r4 = 4;
        goto L_0x0070;
    L_0x0064:
        r4 = 2;
        goto L_0x0070;
    L_0x0066:
        r0 = r1.size();
        if (r0 <= r9) goto L_0x006f;
    L_0x006c:
        r5 = 5;
        r4 = 5;
        goto L_0x0070;
    L_0x006f:
        r4 = 3;
    L_0x0070:
        r11 = r4;
        r8 = r29.getUndoView();
        r9 = 0;
        r12 = 0;
        r13 = new org.telegram.ui.-$$Lambda$DialogsActivity$0CqBdEwjzLcyLDVjAtjOQrlvawc;
        r13.<init>(r7, r1);
        r8.showWithAction(r9, r11, r12, r13);
        goto L_0x009e;
    L_0x0081:
        r0 = r29.getMessagesController();
        r1 = r7.folderId;
        r0 = r0.getDialogs(r1);
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x009e;
    L_0x0091:
        r0 = r7.listView;
        r0.setEmptyView(r8);
        r0 = r7.progressView;
        r0.setVisibility(r6);
        r29.finishFragment();
    L_0x009e:
        return;
    L_0x009f:
        r3 = 102; // 0x66 float:1.43E-43 double:5.04E-322;
        r11 = 100;
        if (r2 != r11) goto L_0x0155;
    L_0x00a5:
        r13 = r7.canPinCount;
        if (r13 == 0) goto L_0x0155;
    L_0x00a9:
        r13 = r29.getMessagesController();
        r14 = r7.folderId;
        r13 = r13.getDialogs(r14);
        r14 = r13.size();
        r15 = 0;
        r16 = 0;
        r17 = 0;
    L_0x00bc:
        if (r15 >= r14) goto L_0x00de;
    L_0x00be:
        r18 = r13.get(r15);
        r4 = r18;
        r4 = (org.telegram.tgnet.TLRPC.Dialog) r4;
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_dialogFolder;
        if (r5 == 0) goto L_0x00cb;
    L_0x00ca:
        goto L_0x00d9;
    L_0x00cb:
        r11 = r4.id;
        r12 = (int) r11;
        r4 = r4.pinned;
        if (r4 == 0) goto L_0x00de;
    L_0x00d2:
        if (r12 != 0) goto L_0x00d7;
    L_0x00d4:
        r17 = r17 + 1;
        goto L_0x00d9;
    L_0x00d7:
        r16 = r16 + 1;
    L_0x00d9:
        r15 = r15 + 1;
        r11 = 100;
        goto L_0x00bc;
    L_0x00de:
        r4 = 0;
        r11 = 0;
        r12 = 0;
    L_0x00e1:
        if (r4 >= r1) goto L_0x010b;
    L_0x00e3:
        r13 = r0.get(r4);
        r13 = (java.lang.Long) r13;
        r13 = r13.longValue();
        r15 = r29.getMessagesController();
        r15 = r15.dialogs_dict;
        r15 = r15.get(r13);
        r15 = (org.telegram.tgnet.TLRPC.Dialog) r15;
        if (r15 == 0) goto L_0x0108;
    L_0x00fb:
        r15 = r15.pinned;
        if (r15 == 0) goto L_0x0100;
    L_0x00ff:
        goto L_0x0108;
    L_0x0100:
        r14 = (int) r13;
        if (r14 != 0) goto L_0x0106;
    L_0x0103:
        r11 = r11 + 1;
        goto L_0x0108;
    L_0x0106:
        r12 = r12 + 1;
    L_0x0108:
        r4 = r4 + 1;
        goto L_0x00e1;
    L_0x010b:
        r4 = r7.folderId;
        if (r4 == 0) goto L_0x0116;
    L_0x010f:
        r4 = r29.getMessagesController();
        r4 = r4.maxFolderPinnedDialogsCount;
        goto L_0x011c;
    L_0x0116:
        r4 = r29.getMessagesController();
        r4 = r4.maxPinnedDialogsCount;
    L_0x011c:
        r11 = r11 + r17;
        if (r11 > r4) goto L_0x0124;
    L_0x0120:
        r12 = r12 + r16;
        if (r12 <= r4) goto L_0x0231;
    L_0x0124:
        r0 = NUM; // 0x7f0e087e float:1.8879447E38 double:1.0531632307E-314;
        r1 = new java.lang.Object[r9];
        r2 = "Chats";
        r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r4);
        r1[r10] = r2;
        r2 = "PinToTopLimitReached";
        r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1);
        org.telegram.ui.Components.AlertsCreator.showSimpleAlert(r7, r0);
        r0 = r7.pinItem;
        r1 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        org.telegram.messenger.AndroidUtilities.shakeView(r0, r1, r10);
        r0 = r29.getParentActivity();
        r1 = "vibrator";
        r0 = r0.getSystemService(r1);
        r0 = (android.os.Vibrator) r0;
        if (r0 == 0) goto L_0x0154;
    L_0x014f:
        r1 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r0.vibrate(r1);
    L_0x0154:
        return;
    L_0x0155:
        if (r2 == r3) goto L_0x015b;
    L_0x0157:
        r4 = 103; // 0x67 float:1.44E-43 double:5.1E-322;
        if (r2 != r4) goto L_0x0231;
    L_0x015b:
        if (r1 <= r9) goto L_0x0231;
    L_0x015d:
        if (r31 == 0) goto L_0x0231;
    L_0x015f:
        if (r31 == 0) goto L_0x0231;
    L_0x0161:
        r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r4 = r29.getParentActivity();
        r0.<init>(r4);
        if (r2 != r3) goto L_0x01a0;
    L_0x016c:
        r3 = NUM; // 0x7f0e0365 float:1.88768E38 double:1.053162586E-314;
        r4 = new java.lang.Object[r9];
        r5 = "ChatsSelected";
        r1 = org.telegram.messenger.LocaleController.formatPluralString(r5, r1);
        r4[r10] = r1;
        r1 = "DeleteFewChatsTitle";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        r0.setTitle(r1);
        r1 = NUM; // 0x7f0e011f float:1.887562E38 double:1.0531622984E-314;
        r3 = "AreYouSureDeleteFewChats";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r0.setMessage(r1);
        r1 = NUM; // 0x7f0e0356 float:1.887677E38 double:1.0531625786E-314;
        r3 = "Delete";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r3 = new org.telegram.ui.-$$Lambda$DialogsActivity$jWd3WTqe_JWytlcg5OAtRHiocr4;
        r3.<init>(r7, r2);
        r0.setPositiveButton(r1, r3);
        goto L_0x020b;
    L_0x01a0:
        r3 = r7.canClearCacheCount;
        if (r3 == 0) goto L_0x01d8;
    L_0x01a4:
        r3 = NUM; // 0x7f0e02d8 float:1.8876514E38 double:1.0531625163E-314;
        r4 = new java.lang.Object[r9];
        r5 = "ChatsSelectedClearCache";
        r1 = org.telegram.messenger.LocaleController.formatPluralString(r5, r1);
        r4[r10] = r1;
        r1 = "ClearCacheFewChatsTitle";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        r0.setTitle(r1);
        r1 = NUM; // 0x7f0e0114 float:1.8875597E38 double:1.053162293E-314;
        r3 = "AreYouSureClearHistoryCacheFewChats";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r0.setMessage(r1);
        r1 = NUM; // 0x7f0e02dc float:1.8876522E38 double:1.0531625183E-314;
        r3 = "ClearHistoryCache";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r3 = new org.telegram.ui.-$$Lambda$DialogsActivity$iMQ7bU7WAaE9d3J2u7XQeCZi3Ow;
        r3.<init>(r7, r2);
        r0.setPositiveButton(r1, r3);
        goto L_0x020b;
    L_0x01d8:
        r3 = NUM; // 0x7f0e02da float:1.8876518E38 double:1.0531625173E-314;
        r4 = new java.lang.Object[r9];
        r5 = "ChatsSelectedClear";
        r1 = org.telegram.messenger.LocaleController.formatPluralString(r5, r1);
        r4[r10] = r1;
        r1 = "ClearFewChatsTitle";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        r0.setTitle(r1);
        r1 = NUM; // 0x7f0e0116 float:1.8875601E38 double:1.053162294E-314;
        r3 = "AreYouSureClearHistoryFewChats";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r0.setMessage(r1);
        r1 = NUM; // 0x7f0e02db float:1.887652E38 double:1.053162518E-314;
        r3 = "ClearHistory";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r3 = new org.telegram.ui.-$$Lambda$DialogsActivity$4jeFfbPa00Dvar_qyJvj0h0oI_X4;
        r3.<init>(r7, r2);
        r0.setPositiveButton(r1, r3);
    L_0x020b:
        r1 = NUM; // 0x7f0e01fa float:1.8876064E38 double:1.0531624066E-314;
        r2 = "Cancel";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setNegativeButton(r1, r8);
        r0 = r0.create();
        r7.showDialog(r0);
        r1 = -1;
        r0 = r0.getButton(r1);
        r0 = (android.widget.TextView) r0;
        if (r0 == 0) goto L_0x0230;
    L_0x0227:
        r1 = "dialogTextRed2";
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r0.setTextColor(r1);
    L_0x0230:
        return;
    L_0x0231:
        r4 = 0;
        r11 = 0;
    L_0x0233:
        if (r4 >= r1) goto L_0x042a;
    L_0x0235:
        r12 = r0.get(r4);
        r12 = (java.lang.Long) r12;
        r12 = r12.longValue();
        r14 = r29.getMessagesController();
        r14 = r14.dialogs_dict;
        r14 = r14.get(r12);
        r14 = (org.telegram.tgnet.TLRPC.Dialog) r14;
        if (r14 != 0) goto L_0x0254;
    L_0x024d:
        r31 = r11;
    L_0x024f:
        r5 = 103; // 0x67 float:1.44E-43 double:5.1E-322;
        r11 = 2;
        goto L_0x0421;
    L_0x0254:
        r15 = (int) r12;
        r16 = 32;
        r31 = r11;
        r10 = r12 >> r16;
        r11 = (int) r10;
        if (r15 == 0) goto L_0x027c;
    L_0x025e:
        if (r15 <= 0) goto L_0x026d;
    L_0x0260:
        r10 = r29.getMessagesController();
        r11 = java.lang.Integer.valueOf(r15);
        r10 = r10.getUser(r11);
        goto L_0x029e;
    L_0x026d:
        r10 = r29.getMessagesController();
        r11 = -r15;
        r11 = java.lang.Integer.valueOf(r11);
        r10 = r10.getChat(r11);
        r11 = r8;
        goto L_0x02a0;
    L_0x027c:
        r10 = r29.getMessagesController();
        r11 = java.lang.Integer.valueOf(r11);
        r10 = r10.getEncryptedChat(r11);
        if (r10 == 0) goto L_0x0299;
    L_0x028a:
        r11 = r29.getMessagesController();
        r10 = r10.user_id;
        r10 = java.lang.Integer.valueOf(r10);
        r10 = r11.getUser(r10);
        goto L_0x029e;
    L_0x0299:
        r10 = new org.telegram.tgnet.TLRPC$TL_userEmpty;
        r10.<init>();
    L_0x029e:
        r11 = r10;
        r10 = r8;
    L_0x02a0:
        if (r10 != 0) goto L_0x02a5;
    L_0x02a2:
        if (r11 != 0) goto L_0x02a5;
    L_0x02a4:
        goto L_0x024f;
    L_0x02a5:
        if (r11 == 0) goto L_0x02b6;
    L_0x02a7:
        r5 = r11.bot;
        if (r5 == 0) goto L_0x02b6;
    L_0x02ab:
        r5 = org.telegram.messenger.MessagesController.isSupportUser(r11);
        if (r5 != 0) goto L_0x02b6;
    L_0x02b1:
        r5 = 100;
        r16 = 1;
        goto L_0x02ba;
    L_0x02b6:
        r5 = 100;
        r16 = 0;
    L_0x02ba:
        if (r2 != r5) goto L_0x02f5;
    L_0x02bc:
        r10 = r7.canPinCount;
        if (r10 == 0) goto L_0x02d8;
    L_0x02c0:
        r10 = r14.pinned;
        if (r10 == 0) goto L_0x02c5;
    L_0x02c4:
        goto L_0x02a4;
    L_0x02c5:
        r19 = r29.getMessagesController();
        r22 = 1;
        r23 = 0;
        r24 = -1;
        r20 = r12;
        r10 = r19.pinDialog(r20, r22, r23, r24);
        if (r10 == 0) goto L_0x024f;
    L_0x02d7:
        goto L_0x02ef;
    L_0x02d8:
        r10 = r14.pinned;
        if (r10 != 0) goto L_0x02dd;
    L_0x02dc:
        goto L_0x02a4;
    L_0x02dd:
        r19 = r29.getMessagesController();
        r22 = 0;
        r23 = 0;
        r24 = -1;
        r20 = r12;
        r10 = r19.pinDialog(r20, r22, r23, r24);
        if (r10 == 0) goto L_0x024f;
    L_0x02ef:
        r5 = 103; // 0x67 float:1.44E-43 double:5.1E-322;
        r6 = 1;
        r11 = 2;
        goto L_0x0423;
    L_0x02f5:
        r5 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        if (r2 != r5) goto L_0x0330;
    L_0x02f9:
        r5 = r7.canReadCount;
        if (r5 == 0) goto L_0x0321;
    L_0x02fd:
        r5 = r29.getMessagesController();
        r5.markMentionsAsRead(r12);
        r19 = r29.getMessagesController();
        r5 = r14.top_message;
        r10 = r14.last_message_date;
        r25 = 0;
        r26 = 0;
        r27 = 1;
        r28 = 0;
        r20 = r12;
        r22 = r5;
        r23 = r5;
        r24 = r10;
        r19.markDialogAsRead(r20, r22, r23, r24, r25, r26, r27, r28);
        goto L_0x024f;
    L_0x0321:
        r19 = r29.getMessagesController();
        r22 = 0;
        r23 = 0;
        r20 = r12;
        r19.markDialogAsUnread(r20, r22, r23);
        goto L_0x024f;
    L_0x0330:
        if (r2 == r3) goto L_0x0381;
    L_0x0332:
        r5 = 103; // 0x67 float:1.44E-43 double:5.1E-322;
        if (r2 != r5) goto L_0x0337;
    L_0x0336:
        goto L_0x0381;
    L_0x0337:
        r5 = 104; // 0x68 float:1.46E-43 double:5.14E-322;
        if (r2 != r5) goto L_0x024f;
    L_0x033b:
        if (r1 != r9) goto L_0x0352;
    L_0x033d:
        r5 = r7.canMuteCount;
        if (r5 != r9) goto L_0x0352;
    L_0x0341:
        r0 = r29.getParentActivity();
        r0 = org.telegram.ui.Components.AlertsCreator.createMuteAlert(r0, r12);
        r1 = new org.telegram.ui.-$$Lambda$DialogsActivity$1NIJBQ0Gz4LgguHSbB06qjn5FYs;
        r1.<init>(r7);
        r7.showDialog(r0, r1);
        return;
    L_0x0352:
        r5 = r7.canUnmuteCount;
        if (r5 == 0) goto L_0x036b;
    L_0x0356:
        r5 = r29.getMessagesController();
        r5 = r5.isDialogMuted(r12);
        if (r5 != 0) goto L_0x0362;
    L_0x0360:
        goto L_0x02a4;
    L_0x0362:
        r5 = r29.getNotificationsController();
        r5.setDialogNotificationsSettings(r12, r6);
        goto L_0x024f;
    L_0x036b:
        r5 = r29.getMessagesController();
        r5 = r5.isDialogMuted(r12);
        if (r5 == 0) goto L_0x0377;
    L_0x0375:
        goto L_0x02a4;
    L_0x0377:
        r5 = r29.getNotificationsController();
        r14 = 3;
        r5.setDialogNotificationsSettings(r12, r14);
        goto L_0x024f;
    L_0x0381:
        r14 = 3;
        if (r1 != r9) goto L_0x03ac;
    L_0x0384:
        r5 = 103; // 0x67 float:1.44E-43 double:5.1E-322;
        if (r2 != r5) goto L_0x038a;
    L_0x0388:
        r8 = 1;
        goto L_0x038b;
    L_0x038a:
        r8 = 0;
    L_0x038b:
        if (r15 != 0) goto L_0x0390;
    L_0x038d:
        r17 = 1;
        goto L_0x0392;
    L_0x0390:
        r17 = 0;
    L_0x0392:
        r9 = new org.telegram.ui.-$$Lambda$DialogsActivity$N83TAKOOo9dN19vicqwdZcmBCMg;
        r0 = r9;
        r1 = r29;
        r2 = r30;
        r3 = r10;
        r4 = r12;
        r6 = r16;
        r0.<init>(r1, r2, r3, r4, r6);
        r0 = r29;
        r1 = r8;
        r2 = r10;
        r3 = r11;
        r4 = r17;
        r5 = r9;
        org.telegram.ui.Components.AlertsCreator.createClearOrDeleteDialogAlert(r0, r1, r2, r3, r4, r5);
        return;
    L_0x03ac:
        r5 = 103; // 0x67 float:1.44E-43 double:5.1E-322;
        if (r2 != r5) goto L_0x03be;
    L_0x03b0:
        r11 = r7.canClearCacheCount;
        if (r11 == 0) goto L_0x03be;
    L_0x03b4:
        r10 = r29.getMessagesController();
        r11 = 2;
        r15 = 0;
        r10.deleteDialog(r12, r11, r15);
        goto L_0x0421;
    L_0x03be:
        r6 = 0;
        r11 = 2;
        if (r2 != r5) goto L_0x03ca;
    L_0x03c2:
        r10 = r29.getMessagesController();
        r10.deleteDialog(r12, r9, r6);
        goto L_0x0421;
    L_0x03ca:
        if (r10 == 0) goto L_0x03f8;
    L_0x03cc:
        r10 = org.telegram.messenger.ChatObject.isNotInChat(r10);
        if (r10 == 0) goto L_0x03da;
    L_0x03d2:
        r10 = r29.getMessagesController();
        r10.deleteDialog(r12, r6, r6);
        goto L_0x0409;
    L_0x03da:
        r6 = r29.getMessagesController();
        r10 = r29.getUserConfig();
        r10 = r10.getClientUserId();
        r10 = java.lang.Integer.valueOf(r10);
        r6 = r6.getUser(r10);
        r10 = r29.getMessagesController();
        r14 = -r12;
        r15 = (int) r14;
        r10.deleteUserFromChat(r15, r6, r8);
        goto L_0x0409;
    L_0x03f8:
        r6 = r29.getMessagesController();
        r10 = 0;
        r6.deleteDialog(r12, r10, r10);
        if (r16 == 0) goto L_0x0409;
    L_0x0402:
        r6 = r29.getMessagesController();
        r6.blockUser(r15);
    L_0x0409:
        r6 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r6 == 0) goto L_0x0421;
    L_0x040f:
        r6 = r29.getNotificationCenter();
        r10 = org.telegram.messenger.NotificationCenter.closeChats;
        r14 = new java.lang.Object[r9];
        r12 = java.lang.Long.valueOf(r12);
        r13 = 0;
        r14[r13] = r12;
        r6.postNotificationName(r10, r14);
    L_0x0421:
        r6 = r31;
    L_0x0423:
        r4 = r4 + 1;
        r11 = r6;
        r6 = 4;
        r10 = 0;
        goto L_0x0233;
    L_0x042a:
        r31 = r11;
        r4 = 100;
        if (r2 != r4) goto L_0x043b;
    L_0x0430:
        r0 = r29.getMessagesController();
        r1 = r7.folderId;
        r10 = 0;
        r0.reorderPinnedDialogs(r1, r8, r10);
    L_0x043b:
        r15 = 0;
        if (r31 == 0) goto L_0x044a;
    L_0x043e:
        r7.hideFloatingButton(r15);
        r0 = r7.listView;
        r1 = r29.hasHiddenArchive();
        r0.smoothScrollToPosition(r1);
    L_0x044a:
        r0 = 100;
        if (r2 == r0) goto L_0x0451;
    L_0x044e:
        if (r2 == r3) goto L_0x0451;
    L_0x0450:
        r15 = 1;
    L_0x0451:
        r7.hideActionMode(r15);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.perfromSelectedDialogsAction(int, boolean):void");
    }

    public /* synthetic */ void lambda$perfromSelectedDialogsAction$9$DialogsActivity(ArrayList arrayList) {
        getMessagesController().addDialogToFolder(arrayList, this.folderId == 0 ? 0 : 1, -1, null, 0);
    }

    public /* synthetic */ void lambda$perfromSelectedDialogsAction$10$DialogsActivity(int i, DialogInterface dialogInterface, int i2) {
        getMessagesController().setDialogsInTransaction(true);
        perfromSelectedDialogsAction(i, false);
        getMessagesController().setDialogsInTransaction(false);
        MessagesController.getInstance(this.currentAccount).checkIfFolderEmpty(this.folderId);
        i = this.folderId;
        if (i != 0 && getDialogsArray(this.currentAccount, this.dialogsType, i, false).size() == 0) {
            this.listView.setEmptyView(null);
            this.progressView.setVisibility(4);
            finishFragment();
        }
    }

    public /* synthetic */ void lambda$perfromSelectedDialogsAction$11$DialogsActivity(int i, DialogInterface dialogInterface, int i2) {
        perfromSelectedDialogsAction(i, false);
    }

    public /* synthetic */ void lambda$perfromSelectedDialogsAction$12$DialogsActivity(int i, DialogInterface dialogInterface, int i2) {
        perfromSelectedDialogsAction(i, false);
    }

    public /* synthetic */ void lambda$perfromSelectedDialogsAction$14$DialogsActivity(int i, Chat chat, long j, boolean z, boolean z2) {
        int i2 = i;
        Chat chat2 = chat;
        long j2 = j;
        hideActionMode(false);
        if (i2 == 103 && ChatObject.isChannel(chat) && (!chat2.megagroup || !TextUtils.isEmpty(chat2.username))) {
            getMessagesController().deleteDialog(j2, 2, z2);
            return;
        }
        boolean z3 = z2;
        if (i2 == 102) {
            int i3 = this.folderId;
            if (i3 != 0 && getDialogsArray(this.currentAccount, this.dialogsType, i3, false).size() == 1) {
                this.progressView.setVisibility(4);
            }
        }
        getUndoView().showWithAction(j2, i2 == 103 ? 0 : 1, new -$$Lambda$DialogsActivity$txFO_N8hhOxXsqlooRmKgc2zyA8(this, i, j, z2, chat, z));
    }

    public /* synthetic */ void lambda$null$13$DialogsActivity(int i, long j, boolean z, Chat chat, boolean z2) {
        if (i == 103) {
            getMessagesController().deleteDialog(j, 1, z);
            return;
        }
        if (chat == null) {
            getMessagesController().deleteDialog(j, 0, z);
            if (z2) {
                getMessagesController().blockUser((int) j);
            }
        } else if (ChatObject.isNotInChat(chat)) {
            getMessagesController().deleteDialog(j, 0, z);
        } else {
            getMessagesController().deleteUserFromChat((int) (-j), getMessagesController().getUser(Integer.valueOf(getUserConfig().getClientUserId())), null);
        }
        if (AndroidUtilities.isTablet()) {
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, Long.valueOf(j));
        }
        MessagesController.getInstance(this.currentAccount).checkIfFolderEmpty(this.folderId);
    }

    public /* synthetic */ void lambda$perfromSelectedDialogsAction$15$DialogsActivity(DialogInterface dialogInterface) {
        hideActionMode(true);
    }

    private void updateCounters(boolean z) {
        this.canUnmuteCount = 0;
        this.canMuteCount = 0;
        this.canPinCount = 0;
        this.canReadCount = 0;
        this.canClearCacheCount = 0;
        if (!z) {
            int i;
            ArrayList selectedDialogs = this.dialogsAdapter.getSelectedDialogs();
            int size = selectedDialogs.size();
            int i2 = 0;
            int i3 = 0;
            int i4 = 0;
            int i5 = 0;
            int i6 = 0;
            int i7 = 0;
            while (i2 < size) {
                ArrayList arrayList;
                Dialog dialog = (Dialog) getMessagesController().dialogs_dict.get(((Long) selectedDialogs.get(i2)).longValue());
                if (dialog == null) {
                    arrayList = selectedDialogs;
                } else {
                    long j = dialog.id;
                    boolean z2 = dialog.pinned;
                    Object obj = (dialog.unread_count != 0 || dialog.unread_mark) ? 1 : null;
                    if (getMessagesController().isDialogMuted(j)) {
                        this.canUnmuteCount++;
                    } else {
                        this.canMuteCount++;
                    }
                    if (obj != null) {
                        this.canReadCount++;
                    }
                    if (this.folderId == 1) {
                        i5++;
                        arrayList = selectedDialogs;
                    } else {
                        arrayList = selectedDialogs;
                        if (!(j == ((long) getUserConfig().getClientUserId()) || j == 777000 || getMessagesController().isProxyDialog(j, false))) {
                            i6++;
                        }
                    }
                    int i8 = (int) j;
                    i = (int) (j >> 32);
                    if (DialogObject.isChannel(dialog)) {
                        Chat chat = getMessagesController().getChat(Integer.valueOf(-i8));
                        if (getMessagesController().isProxyDialog(dialog.id, true)) {
                            this.canClearCacheCount++;
                        } else {
                            if (z2) {
                                i7++;
                            } else {
                                this.canPinCount++;
                            }
                            if (chat == null || !chat.megagroup) {
                                this.canClearCacheCount++;
                                i3++;
                            } else if (!TextUtils.isEmpty(chat.username)) {
                                this.canClearCacheCount++;
                                i3++;
                            }
                        }
                    } else {
                        User user;
                        Object obj2 = (i8 >= 0 || i == 1) ? null : 1;
                        if (obj2 != null) {
                            getMessagesController().getChat(Integer.valueOf(-i8));
                        }
                        if (i8 == 0) {
                            EncryptedChat encryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf(i));
                            user = encryptedChat != null ? getMessagesController().getUser(Integer.valueOf(encryptedChat.user_id)) : new TL_userEmpty();
                        } else {
                            user = (obj2 != null || i8 <= 0 || i == 1) ? null : getMessagesController().getUser(Integer.valueOf(i8));
                        }
                        if (user != null && user.bot) {
                            boolean isSupportUser = MessagesController.isSupportUser(user);
                        }
                        if (z2) {
                            i7++;
                        } else {
                            this.canPinCount++;
                        }
                    }
                    i4++;
                    i3++;
                }
                i2++;
                selectedDialogs = arrayList;
            }
            if (i3 != size) {
                this.deleteItem.setVisibility(8);
            } else {
                this.deleteItem.setVisibility(0);
            }
            i = this.canClearCacheCount;
            if ((i == 0 || i == size) && (i4 == 0 || i4 == size)) {
                this.clearItem.setVisibility(0);
                if (this.canClearCacheCount != 0) {
                    this.clearItem.setText(LocaleController.getString("ClearHistoryCache", NUM));
                } else {
                    this.clearItem.setText(LocaleController.getString("ClearHistory", NUM));
                }
            } else {
                this.clearItem.setVisibility(8);
            }
            if (i5 != 0) {
                this.archiveItem.setTextAndIcon(LocaleController.getString("Unarchive", NUM), NUM);
                this.archiveItem.setVisibility(0);
            } else if (i6 != 0) {
                this.archiveItem.setTextAndIcon(LocaleController.getString("Archive", NUM), NUM);
                this.archiveItem.setVisibility(0);
            } else {
                this.archiveItem.setVisibility(8);
            }
            if (this.canPinCount + i7 != size) {
                this.pinItem.setVisibility(8);
            } else {
                this.pinItem.setVisibility(0);
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
            } else {
                this.pinItem.setIcon(NUM);
                this.pinItem.setContentDescription(LocaleController.getString("UnpinFromTop", NUM));
            }
        }
    }

    private boolean validateSlowModeDialog(long j) {
        if (this.messagesCount <= 1) {
            ChatActivityEnterView chatActivityEnterView = this.commentView;
            if (chatActivityEnterView == null || chatActivityEnterView.getVisibility() != 0 || TextUtils.isEmpty(this.commentView.getFieldText())) {
                return true;
            }
        }
        int i = (int) j;
        if (i >= 0) {
            return true;
        }
        Chat chat = getMessagesController().getChat(Integer.valueOf(-i));
        if (chat == null || ChatObject.hasAdminRights(chat) || !chat.slowmode_enabled) {
            return true;
        }
        AlertsCreator.showSimpleAlert(this, LocaleController.getString("Slowmode", NUM), LocaleController.getString("SlowmodeSendError", NUM));
        return false;
    }

    private void showOrUpdateActionMode(Dialog dialog, View view) {
        this.dialogsAdapter.addOrRemoveSelectedDialog(dialog.id, view);
        ArrayList selectedDialogs = this.dialogsAdapter.getSelectedDialogs();
        boolean z = true;
        if (!this.actionBar.isActionModeShowed()) {
            this.actionBar.createActionMode();
            this.actionBar.showActionMode();
            if (this.menuDrawable != null) {
                this.actionBar.setBackButtonContentDescription(LocaleController.getString("AccDescrGoBack", NUM));
            }
            if (getPinnedCount() > 1) {
                this.dialogsAdapter.onReorderStateChanged(true);
                updateVisibleRows(131072);
            }
            AnimatorSet animatorSet = new AnimatorSet();
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < this.actionModeViews.size(); i++) {
                View view2 = (View) this.actionModeViews.get(i);
                view2.setPivotY((float) (ActionBar.getCurrentActionBarHeight() / 2));
                AndroidUtilities.clearDrawableAnimation(view2);
                arrayList.add(ObjectAnimator.ofFloat(view2, View.SCALE_Y, new float[]{0.1f, 1.0f}));
            }
            animatorSet.playTogether(arrayList);
            animatorSet.setDuration(250);
            animatorSet.start();
            MenuDrawable menuDrawable = this.menuDrawable;
            if (menuDrawable != null) {
                menuDrawable.setRotateToBack(false);
                this.menuDrawable.setRotation(1.0f, true);
            } else {
                BackDrawable backDrawable = this.backDrawable;
                if (backDrawable != null) {
                    backDrawable.setRotation(1.0f, true);
                }
            }
            z = false;
        } else if (selectedDialogs.isEmpty()) {
            hideActionMode(true);
            return;
        }
        updateCounters(false);
        this.selectedDialogsCountTextView.setNumber(selectedDialogs.size(), z);
    }

    private void closeSearch() {
        if (AndroidUtilities.isTablet()) {
            ActionBar actionBar = this.actionBar;
            if (actionBar != null) {
                actionBar.closeSearchField();
            }
            TLObject tLObject = this.searchObject;
            if (tLObject != null) {
                this.dialogsSearchAdapter.putRecentSearch(this.searchDialogId, tLObject);
                this.searchObject = null;
                return;
            }
            return;
        }
        this.closeSearchFieldOnHide = true;
    }

    /* Access modifiers changed, original: protected */
    public RecyclerListView getListView() {
        return this.listView;
    }

    private UndoView getUndoView() {
        if (this.undoView[0].getVisibility() == 0) {
            UndoView[] undoViewArr = this.undoView;
            UndoView undoView = undoViewArr[0];
            undoViewArr[0] = undoViewArr[1];
            undoViewArr[1] = undoView;
            undoView.hide(true, 2);
            ContentView contentView = (ContentView) this.fragmentView;
            contentView.removeView(this.undoView[0]);
            contentView.addView(this.undoView[0]);
        }
        return this.undoView[0];
    }

    private void updateProxyButton(boolean z) {
        if (this.proxyDrawable != null) {
            boolean z2 = false;
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
            boolean z3 = sharedPreferences.getBoolean("proxy_enabled", false) && !TextUtils.isEmpty(sharedPreferences.getString("proxy_ip", ""));
            if (z3 || (getMessagesController().blockedCountry && !SharedConfig.proxyList.isEmpty())) {
                if (!this.actionBar.isSearchFieldVisible()) {
                    this.proxyItem.setVisibility(0);
                }
                ProxyDrawable proxyDrawable = this.proxyDrawable;
                int i = this.currentConnectionState;
                if (i == 3 || i == 5) {
                    z2 = true;
                }
                proxyDrawable.setConnected(z3, z2, z);
                this.proxyItemVisisble = true;
            } else {
                this.proxyItem.setVisibility(8);
                this.proxyItemVisisble = false;
            }
        }
    }

    private void updateSelectedCount() {
        if (this.commentView != null) {
            AnimatorSet animatorSet;
            Animator[] animatorArr;
            if (this.dialogsAdapter.hasSelectedDialogs()) {
                if (this.commentView.getTag() == null) {
                    this.commentView.setFieldText("");
                    this.commentView.setVisibility(0);
                    animatorSet = new AnimatorSet();
                    animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.commentView, View.TRANSLATION_Y, new float[]{(float) r8.getMeasuredHeight(), 0.0f});
                    animatorSet.playTogether(animatorArr);
                    animatorSet.setDuration(180);
                    animatorSet.setInterpolator(new DecelerateInterpolator());
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            DialogsActivity.this.commentView.setTag(Integer.valueOf(2));
                            DialogsActivity.this.commentView.requestLayout();
                        }
                    });
                    animatorSet.start();
                    this.commentView.setTag(Integer.valueOf(1));
                }
                this.actionBar.setTitle(LocaleController.formatPluralString("Recipient", this.dialogsAdapter.getSelectedDialogs().size()));
            } else {
                if (this.dialogsType == 3 && this.selectAlertString == null) {
                    this.actionBar.setTitle(LocaleController.getString("ForwardTo", NUM));
                } else {
                    this.actionBar.setTitle(LocaleController.getString("SelectChat", NUM));
                }
                if (this.commentView.getTag() != null) {
                    this.commentView.hidePopup(false);
                    this.commentView.closeKeyboard();
                    animatorSet = new AnimatorSet();
                    animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.commentView, View.TRANSLATION_Y, new float[]{0.0f, (float) r8.getMeasuredHeight()});
                    animatorSet.playTogether(animatorArr);
                    animatorSet.setDuration(180);
                    animatorSet.setInterpolator(new DecelerateInterpolator());
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            DialogsActivity.this.commentView.setVisibility(8);
                        }
                    });
                    animatorSet.start();
                    this.commentView.setTag(null);
                    this.listView.requestLayout();
                }
            }
        }
    }

    @TargetApi(23)
    private void askForPermissons(boolean z) {
        Activity parentActivity = getParentActivity();
        if (parentActivity != null) {
            ArrayList arrayList = new ArrayList();
            if (getUserConfig().syncContacts && this.askAboutContacts) {
                String str = "android.permission.READ_CONTACTS";
                if (parentActivity.checkSelfPermission(str) != 0) {
                    if (z) {
                        AlertDialog create = AlertsCreator.createContactsPermissionDialog(parentActivity, new -$$Lambda$DialogsActivity$0uSHLkwmlB9mVpQMUrJbrhf-_rI(this)).create();
                        this.permissionDialog = create;
                        showDialog(create);
                        return;
                    }
                    arrayList.add(str);
                    arrayList.add("android.permission.WRITE_CONTACTS");
                    arrayList.add("android.permission.GET_ACCOUNTS");
                }
            }
            String str2 = "android.permission.WRITE_EXTERNAL_STORAGE";
            if (parentActivity.checkSelfPermission(str2) != 0) {
                arrayList.add("android.permission.READ_EXTERNAL_STORAGE");
                arrayList.add(str2);
            }
            if (!arrayList.isEmpty()) {
                try {
                    parentActivity.requestPermissions((String[]) arrayList.toArray(new String[0]), 1);
                } catch (Exception unused) {
                }
            }
        }
    }

    public /* synthetic */ void lambda$askForPermissons$16$DialogsActivity(int i) {
        this.askAboutContacts = i != 0;
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askAboutContacts", this.askAboutContacts).commit();
        askForPermissons(false);
    }

    /* Access modifiers changed, original: protected */
    public void onDialogDismiss(android.app.Dialog dialog) {
        super.onDialogDismiss(dialog);
        android.app.Dialog dialog2 = this.permissionDialog;
        if (dialog2 != null && dialog == dialog2 && getParentActivity() != null) {
            askForPermissons(false);
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (!this.onlySelect) {
            FrameLayout frameLayout = this.floatingButtonContainer;
            if (frameLayout != null) {
                frameLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        DialogsActivity dialogsActivity = DialogsActivity.this;
                        dialogsActivity.floatingButtonTranslation = dialogsActivity.floatingHidden ? (float) AndroidUtilities.dp(100.0f) : 0.0f;
                        DialogsActivity.this.updateFloatingButtonOffset();
                        DialogsActivity.this.floatingButtonContainer.setClickable(DialogsActivity.this.floatingHidden ^ 1);
                        if (DialogsActivity.this.floatingButtonContainer != null) {
                            DialogsActivity.this.floatingButtonContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                });
            }
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i == 1) {
            int i2 = 0;
            while (i2 < strArr.length) {
                if (iArr.length > i2) {
                    String str = strArr[i2];
                    Object obj = -1;
                    int hashCode = str.hashCode();
                    if (hashCode != NUM) {
                        if (hashCode == NUM && str.equals("android.permission.READ_CONTACTS")) {
                            obj = null;
                        }
                    } else if (str.equals("android.permission.WRITE_EXTERNAL_STORAGE")) {
                        obj = 1;
                    }
                    if (obj != null) {
                        if (obj == 1 && iArr[i2] == 0) {
                            ImageLoader.getInstance().checkMediaPaths();
                        }
                    } else if (iArr[i2] == 0) {
                        getContactsController().forceImportContacts();
                    } else {
                        Editor edit = MessagesController.getGlobalNotificationsSettings().edit();
                        this.askAboutContacts = false;
                        edit.putBoolean("askAboutContacts", false).commit();
                    }
                }
                i2++;
            }
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        DialogsAdapter dialogsAdapter;
        DialogsSearchAdapter dialogsSearchAdapter;
        if (i == NotificationCenter.dialogsNeedReload) {
            if (!this.dialogsListFrozen) {
                dialogsAdapter = this.dialogsAdapter;
                if (dialogsAdapter != null) {
                    if (dialogsAdapter.isDataSetChanged() || objArr.length > 0) {
                        this.dialogsAdapter.notifyDataSetChanged();
                    } else {
                        updateVisibleRows(2048);
                    }
                }
                DialogsRecyclerView dialogsRecyclerView = this.listView;
                if (dialogsRecyclerView != null) {
                    try {
                        View view = null;
                        if (dialogsRecyclerView.getAdapter() == this.dialogsAdapter) {
                            this.searchEmptyView.setVisibility(8);
                            dialogsRecyclerView = this.listView;
                            if (this.folderId == 0) {
                                view = this.progressView;
                            }
                            dialogsRecyclerView.setEmptyView(view);
                        } else {
                            if (this.searching && this.searchWas) {
                                this.listView.setEmptyView(this.searchEmptyView);
                            } else {
                                this.searchEmptyView.setVisibility(8);
                                this.listView.setEmptyView(null);
                            }
                            this.progressView.setVisibility(8);
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            }
        } else if (i == NotificationCenter.emojiDidLoad) {
            updateVisibleRows(0);
        } else if (i == NotificationCenter.closeSearchByActiveAction) {
            ActionBar actionBar = this.actionBar;
            if (actionBar != null) {
                actionBar.closeSearchField();
            }
        } else if (i == NotificationCenter.proxySettingsChanged) {
            updateProxyButton(false);
        } else if (i == NotificationCenter.updateInterfaces) {
            Integer num = (Integer) objArr[0];
            updateVisibleRows(num.intValue());
            if ((num.intValue() & 4) != 0) {
                dialogsAdapter = this.dialogsAdapter;
                if (dialogsAdapter != null) {
                    dialogsAdapter.sortOnlineContacts(true);
                }
            }
        } else if (i == NotificationCenter.appDidLogout) {
            dialogsLoaded[this.currentAccount] = false;
        } else if (i == NotificationCenter.encryptedChatUpdated) {
            updateVisibleRows(0);
        } else if (i == NotificationCenter.contactsDidLoad) {
            if (!this.dialogsListFrozen) {
                if (this.dialogsType == 0 && getMessagesController().getDialogs(this.folderId).isEmpty()) {
                    dialogsAdapter = this.dialogsAdapter;
                    if (dialogsAdapter != null) {
                        dialogsAdapter.notifyDataSetChanged();
                    }
                } else {
                    updateVisibleRows(0);
                }
            }
        } else if (i == NotificationCenter.openedChatChanged) {
            if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                boolean booleanValue = ((Boolean) objArr[1]).booleanValue();
                long longValue = ((Long) objArr[0]).longValue();
                if (!booleanValue) {
                    this.openedDialogId = longValue;
                } else if (longValue == this.openedDialogId) {
                    this.openedDialogId = 0;
                }
                dialogsAdapter = this.dialogsAdapter;
                if (dialogsAdapter != null) {
                    dialogsAdapter.setOpenedDialogId(this.openedDialogId);
                }
                updateVisibleRows(512);
            }
        } else if (i == NotificationCenter.notificationsSettingsUpdated) {
            updateVisibleRows(0);
        } else if (i == NotificationCenter.messageReceivedByAck || i == NotificationCenter.messageReceivedByServer || i == NotificationCenter.messageSendError) {
            updateVisibleRows(4096);
        } else if (i == NotificationCenter.didSetPasscode) {
            updatePasscodeButton();
        } else if (i == NotificationCenter.needReloadRecentDialogsSearch) {
            dialogsSearchAdapter = this.dialogsSearchAdapter;
            if (dialogsSearchAdapter != null) {
                dialogsSearchAdapter.loadRecentSearch();
            }
        } else if (i == NotificationCenter.replyMessagesDidLoad) {
            updateVisibleRows(32768);
        } else if (i == NotificationCenter.reloadHints) {
            dialogsSearchAdapter = this.dialogsSearchAdapter;
            if (dialogsSearchAdapter != null) {
                dialogsSearchAdapter.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.didUpdateConnectionState) {
            i = AccountInstance.getInstance(i2).getConnectionsManager().getConnectionState();
            if (this.currentConnectionState != i) {
                this.currentConnectionState = i;
                updateProxyButton(true);
            }
        } else if (i != NotificationCenter.dialogsUnreadCounterChanged) {
            if (i == NotificationCenter.needDeleteDialog) {
                if (this.fragmentView != null && !this.isPaused) {
                    long longValue2 = ((Long) objArr[0]).longValue();
                    User user = (User) objArr[1];
                    Runnable -__lambda_dialogsactivity_shfuf_xzvvtonescklijoihes8g = new -$$Lambda$DialogsActivity$SHFUF_xzVvtOneSCklijoIHeS8g(this, (Chat) objArr[2], longValue2, ((Boolean) objArr[3]).booleanValue());
                    if (this.undoView[0] != null) {
                        getUndoView().showWithAction(longValue2, 1, -__lambda_dialogsactivity_shfuf_xzvvtonescklijoihes8g);
                    } else {
                        -__lambda_dialogsactivity_shfuf_xzvvtonescklijoihes8g.run();
                    }
                }
            } else if (i == NotificationCenter.folderBecomeEmpty) {
                i = ((Integer) objArr[0]).intValue();
                i2 = this.folderId;
                if (i2 == i && i2 != 0) {
                    finishFragment();
                }
            }
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$17$DialogsActivity(Chat chat, long j, boolean z) {
        if (chat == null) {
            getMessagesController().deleteDialog(j, 0, z);
        } else if (ChatObject.isNotInChat(chat)) {
            getMessagesController().deleteDialog(j, 0, z);
        } else {
            getMessagesController().deleteUserFromChat((int) (-j), getMessagesController().getUser(Integer.valueOf(getUserConfig().getClientUserId())), null, false, z);
        }
        MessagesController.getInstance(this.currentAccount).checkIfFolderEmpty(this.folderId);
    }

    private void setDialogsListFrozen(boolean z) {
        if (this.dialogsListFrozen != z) {
            if (z) {
                frozenDialogsList = new ArrayList(getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, false));
            } else {
                frozenDialogsList = null;
            }
            this.dialogsListFrozen = z;
            this.dialogsAdapter.setDialogsListFrozen(z);
            if (!z) {
                this.dialogsAdapter.notifyDataSetChanged();
            }
        }
    }

    public static ArrayList<Dialog> getDialogsArray(int i, int i2, int i3, boolean z) {
        if (z) {
            ArrayList arrayList = frozenDialogsList;
            if (arrayList != null) {
                return arrayList;
            }
        }
        MessagesController messagesController = AccountInstance.getInstance(i).getMessagesController();
        if (i2 == 0) {
            return messagesController.getDialogs(i3);
        }
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
        return i2 == 6 ? messagesController.dialogsGroupsOnly : null;
    }

    public void setSideMenu(RecyclerView recyclerView) {
        this.sideMenu = recyclerView;
        String str = "chats_menuBackground";
        this.sideMenu.setBackgroundColor(Theme.getColor(str));
        this.sideMenu.setGlowColor(Theme.getColor(str));
    }

    private void updatePasscodeButton() {
        if (this.passcodeItem != null) {
            if (SharedConfig.passcodeHash.length() == 0 || this.searching) {
                this.passcodeItem.setVisibility(8);
            } else {
                this.passcodeItem.setVisibility(0);
                if (SharedConfig.appLocked) {
                    this.passcodeItem.setIcon(NUM);
                    this.passcodeItem.setContentDescription(LocaleController.getString("AccDescrPasscodeUnlock", NUM));
                } else {
                    this.passcodeItem.setIcon(NUM);
                    this.passcodeItem.setContentDescription(LocaleController.getString("AccDescrPasscodeLock", NUM));
                }
            }
        }
    }

    private void hideFloatingButton(boolean z) {
        if (this.floatingHidden != z) {
            this.floatingHidden = z;
            AnimatorSet animatorSet = new AnimatorSet();
            float[] fArr = new float[2];
            fArr[0] = this.floatingButtonHideProgress;
            fArr[1] = this.floatingHidden ? 1.0f : 0.0f;
            ValueAnimator.ofFloat(fArr).addUpdateListener(new -$$Lambda$DialogsActivity$0AjZFxkZ7NwJsQuFxZ65NI-T34Q(this));
            animatorSet.playTogether(new Animator[]{r1});
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(this.floatingInterpolator);
            this.floatingButtonContainer.setClickable(z ^ 1);
            animatorSet.start();
        }
    }

    public /* synthetic */ void lambda$hideFloatingButton$18$DialogsActivity(ValueAnimator valueAnimator) {
        this.floatingButtonHideProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.floatingButtonTranslation = ((float) AndroidUtilities.dp(100.0f)) * this.floatingButtonHideProgress;
        updateFloatingButtonOffset();
    }

    private void updateDialogIndices() {
        DialogsRecyclerView dialogsRecyclerView = this.listView;
        if (dialogsRecyclerView != null && dialogsRecyclerView.getAdapter() == this.dialogsAdapter) {
            int i = 0;
            ArrayList dialogsArray = getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, false);
            int childCount = this.listView.getChildCount();
            while (i < childCount) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof DialogCell) {
                    DialogCell dialogCell = (DialogCell) childAt;
                    Dialog dialog = (Dialog) getMessagesController().dialogs_dict.get(dialogCell.getDialogId());
                    if (dialog != null) {
                        int indexOf = dialogsArray.indexOf(dialog);
                        if (indexOf >= 0) {
                            dialogCell.setDialogIndex(indexOf);
                        }
                    }
                }
                i++;
            }
        }
    }

    private void updateVisibleRows(int i) {
        DialogsRecyclerView dialogsRecyclerView = this.listView;
        if (dialogsRecyclerView != null && !this.dialogsListFrozen) {
            int childCount = dialogsRecyclerView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.listView.getChildAt(i2);
                if (childAt instanceof DialogCell) {
                    if (this.listView.getAdapter() != this.dialogsSearchAdapter) {
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
                                if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                                    if (dialogCell.getDialogId() != this.openedDialogId) {
                                        z = false;
                                    }
                                    dialogCell.setDialogSelected(z);
                                }
                            } else if ((i & 512) == 0) {
                                dialogCell.update(i);
                            } else if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                                if (dialogCell.getDialogId() != this.openedDialogId) {
                                    z = false;
                                }
                                dialogCell.setDialogSelected(z);
                            }
                            ArrayList selectedDialogs = this.dialogsAdapter.getSelectedDialogs();
                            if (selectedDialogs != null) {
                                dialogCell.setChecked(selectedDialogs.contains(Long.valueOf(dialogCell.getDialogId())), false);
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
                    for (int i3 = 0; i3 < childCount2; i3++) {
                        View childAt2 = recyclerListView.getChildAt(i3);
                        if (childAt2 instanceof HintDialogCell) {
                            ((HintDialogCell) childAt2).update(i);
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

    public boolean isMainDialogList() {
        return this.delegate == null && this.searchString == null;
    }

    private void didSelectResult(long j, boolean z, boolean z2) {
        int i;
        String str = "OK";
        String str2 = "AppName";
        if (this.addToGroupAlertString == null && this.checkCanWrite) {
            i = (int) j;
            if (i < 0) {
                i = -i;
                Chat chat = getMessagesController().getChat(Integer.valueOf(i));
                if (ChatObject.isChannel(chat) && !chat.megagroup && (this.cantSendToChannels || !ChatObject.isCanWriteToChannel(i, this.currentAccount))) {
                    Builder builder = new Builder(getParentActivity());
                    builder.setTitle(LocaleController.getString(str2, NUM));
                    builder.setMessage(LocaleController.getString("ChannelCantSendMessage", NUM));
                    builder.setNegativeButton(LocaleController.getString(str, NUM), null);
                    showDialog(builder.create());
                    return;
                }
            }
        }
        if (!z || ((this.selectAlertString == null || this.selectAlertStringGroup == null) && this.addToGroupAlertString == null)) {
            if (this.delegate != null) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(Long.valueOf(j));
                this.delegate.didSelectDialogs(this, arrayList, null, z2);
                if (this.resetDelegate) {
                    this.delegate = null;
                }
            } else {
                finishFragment();
            }
        } else if (getParentActivity() != null) {
            Builder builder2 = new Builder(getParentActivity());
            builder2.setTitle(LocaleController.getString(str2, NUM));
            int i2 = (int) j;
            i = (int) (j >> 32);
            if (i2 == 0) {
                if (getMessagesController().getUser(Integer.valueOf(getMessagesController().getEncryptedChat(Integer.valueOf(i)).user_id)) != null) {
                    builder2.setMessage(LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(r12)));
                } else {
                    return;
                }
            } else if (i2 == getUserConfig().getClientUserId()) {
                builder2.setMessage(LocaleController.formatStringSimple(this.selectAlertStringGroup, LocaleController.getString("SavedMessages", NUM)));
            } else if (i2 > 0) {
                if (getMessagesController().getUser(Integer.valueOf(i2)) != null) {
                    builder2.setMessage(LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(r12)));
                } else {
                    return;
                }
            } else if (i2 < 0) {
                if (getMessagesController().getChat(Integer.valueOf(-i2)) != null) {
                    String str3 = this.addToGroupAlertString;
                    if (str3 != null) {
                        builder2.setMessage(LocaleController.formatStringSimple(str3, r12.title));
                    } else {
                        builder2.setMessage(LocaleController.formatStringSimple(this.selectAlertStringGroup, r12.title));
                    }
                } else {
                    return;
                }
            }
            builder2.setPositiveButton(LocaleController.getString(str, NUM), new -$$Lambda$DialogsActivity$5FtMGFvTR-4E9bjCce0LUzqecQQ(this, j));
            builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            showDialog(builder2.create());
        }
    }

    public /* synthetic */ void lambda$didSelectResult$19$DialogsActivity(long j, DialogInterface dialogInterface, int i) {
        didSelectResult(j, false, false);
    }

    public ThemeDescription[] getThemeDescriptions() {
        -$$Lambda$DialogsActivity$wY5FTLZwp7C2Dp8j_WpUN2kAJXI -__lambda_dialogsactivity_wy5ftlzwp7c2dp8j_wpun2kajxi = new -$$Lambda$DialogsActivity$wY5FTLZwp7C2Dp8j_WpUN2kAJXI(this);
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        DialogCell dialogCell = this.movingView;
        if (dialogCell != null) {
            arrayList.add(new ThemeDescription(dialogCell, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        }
        if (this.folderId == 0) {
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
            arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, new Drawable[]{Theme.dialogs_holidayDrawable}, null, "actionBarDefaultTitle"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultSearch"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchPlaceholder"));
        } else {
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefaultArchived"));
            arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefaultArchived"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultArchivedIcon"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, new Drawable[]{Theme.dialogs_holidayDrawable}, null, "actionBarDefaultArchivedTitle"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultArchivedSelector"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultArchivedSearch"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchArchivedPlaceholder"));
        }
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_ITEMSCOLOR, null, null, null, null, "actionBarActionModeDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_BACKGROUND, null, null, null, null, "actionBarActionModeDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_TOPBACKGROUND, null, null, null, null, "actionBarActionModeDefaultTop"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_SELECTORCOLOR, null, null, null, null, "actionBarActionModeDefaultSelector"));
        arrayList.add(new ThemeDescription(this.selectedDialogsCountTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "actionBarActionModeDefaultIcon"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider"));
        arrayList.add(new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder"));
        arrayList.add(new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DialogsEmptyCell.class}, new String[]{"emptyTextView1"}, null, null, null, "chats_nameMessage_threeLines"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DialogsEmptyCell.class}, new String[]{"emptyTextView2"}, null, null, null, "chats_message"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "chats_actionIcon"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "chats_actionBackground"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "chats_actionPressedBackground"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.avatar_savedDrawable}, null, "avatar_text"));
        -$$Lambda$DialogsActivity$wY5FTLZwp7C2Dp8j_WpUN2kAJXI -__lambda_dialogsactivity_wy5ftlzwp7c2dp8j_wpun2kajxi2 = -__lambda_dialogsactivity_wy5ftlzwp7c2dp8j_wpun2kajxi;
        arrayList.add(new ThemeDescription(null, 0, null, null, null, -__lambda_dialogsactivity_wy5ftlzwp7c2dp8j_wpun2kajxi2, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, -__lambda_dialogsactivity_wy5ftlzwp7c2dp8j_wpun2kajxi2, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, -__lambda_dialogsactivity_wy5ftlzwp7c2dp8j_wpun2kajxi2, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, -__lambda_dialogsactivity_wy5ftlzwp7c2dp8j_wpun2kajxi2, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, -__lambda_dialogsactivity_wy5ftlzwp7c2dp8j_wpun2kajxi2, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, -__lambda_dialogsactivity_wy5ftlzwp7c2dp8j_wpun2kajxi2, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, -__lambda_dialogsactivity_wy5ftlzwp7c2dp8j_wpun2kajxi2, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, -__lambda_dialogsactivity_wy5ftlzwp7c2dp8j_wpun2kajxi2, "avatar_backgroundSaved"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, -__lambda_dialogsactivity_wy5ftlzwp7c2dp8j_wpun2kajxi2, "avatar_backgroundArchived"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, -__lambda_dialogsactivity_wy5ftlzwp7c2dp8j_wpun2kajxi2, "avatar_backgroundArchivedHidden"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countPaint, null, null, "chats_unreadCounter"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countGrayPaint, null, null, "chats_unreadCounterMuted"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countTextPaint, null, null, "chats_unreadCounterText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Paint[]{Theme.dialogs_namePaint, Theme.dialogs_searchNamePaint}, null, null, "chats_name"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Paint[]{Theme.dialogs_nameEncryptedPaint, Theme.dialogs_searchNameEncryptedPaint}, null, null, "chats_secretName"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_lockDrawable}, null, "chats_secretIcon"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_groupDrawable, Theme.dialogs_broadcastDrawable, Theme.dialogs_botDrawable}, null, "chats_nameIcon"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_scamDrawable}, null, "chats_draft"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_pinnedDrawable, Theme.dialogs_reorderDrawable}, null, "chats_pinnedIcon"));
        if (SharedConfig.useThreeLinesLayout) {
            arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePaint, null, null, "chats_message_threeLines"));
        } else {
            arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePaint, null, null, "chats_message"));
        }
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messageNamePaint, null, null, "chats_nameMessage_threeLines"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, null, null, null, "chats_draft"));
        -__lambda_dialogsactivity_wy5ftlzwp7c2dp8j_wpun2kajxi2 = -__lambda_dialogsactivity_wy5ftlzwp7c2dp8j_wpun2kajxi;
        arrayList.add(new ThemeDescription(null, 0, null, null, null, -__lambda_dialogsactivity_wy5ftlzwp7c2dp8j_wpun2kajxi2, "chats_nameMessage"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, -__lambda_dialogsactivity_wy5ftlzwp7c2dp8j_wpun2kajxi2, "chats_draft"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, -__lambda_dialogsactivity_wy5ftlzwp7c2dp8j_wpun2kajxi2, "chats_attachMessage"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, -__lambda_dialogsactivity_wy5ftlzwp7c2dp8j_wpun2kajxi2, "chats_nameArchived"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, -__lambda_dialogsactivity_wy5ftlzwp7c2dp8j_wpun2kajxi2, "chats_nameMessageArchived"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, -__lambda_dialogsactivity_wy5ftlzwp7c2dp8j_wpun2kajxi2, "chats_nameMessageArchived_threeLines"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, -__lambda_dialogsactivity_wy5ftlzwp7c2dp8j_wpun2kajxi2, "chats_messageArchived"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePrintingPaint, null, null, "chats_actionMessage"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_timePaint, null, null, "chats_date"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_pinnedPaint, null, null, "chats_pinnedOverlay"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_tabletSeletedPaint, null, null, "chats_tabletSelectedOverlay"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_checkDrawable}, null, "chats_sentCheck"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_checkReadDrawable, Theme.dialogs_halfCheckDrawable}, null, "chats_sentReadCheck"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_clockDrawable}, null, "chats_sentClock"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_errorPaint, null, null, "chats_sentError"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_errorDrawable}, null, "chats_sentErrorIcon"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, null, "chats_verifiedCheck"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedDrawable}, null, "chats_verifiedBackground"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_muteDrawable}, null, "chats_muteIcon"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_mentionDrawable}, null, "chats_mentionIcon"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, null, null, null, "chats_archivePinBackground"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, null, null, null, "chats_archiveBackground"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, -__lambda_dialogsactivity_wy5ftlzwp7c2dp8j_wpun2kajxi2, "chats_archivePullDownBackground"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, -__lambda_dialogsactivity_wy5ftlzwp7c2dp8j_wpun2kajxi2, "chats_archivePullDownBackgroundActive"));
        if (SharedConfig.archiveHidden) {
            arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Arrow1", "avatar_backgroundArchivedHidden"));
            arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Arrow2", "avatar_backgroundArchivedHidden"));
        } else {
            arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Arrow1", "avatar_backgroundArchived"));
            arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Arrow2", "avatar_backgroundArchived"));
        }
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Box2", "avatar_text"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Box1", "avatar_text"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_pinArchiveDrawable}, "Arrow", "chats_archiveIcon"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_pinArchiveDrawable}, "Line", "chats_archiveIcon"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unpinArchiveDrawable}, "Arrow", "chats_archiveIcon"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unpinArchiveDrawable}, "Line", "chats_archiveIcon"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveDrawable}, "Arrow", "chats_archiveBackground"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveDrawable}, "Box2", "chats_archiveIcon"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveDrawable}, "Box1", "chats_archiveIcon"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unarchiveDrawable}, "Arrow1", "chats_archiveIcon"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unarchiveDrawable}, "Arrow2", "chats_archivePinBackground"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unarchiveDrawable}, "Box2", "chats_archiveIcon"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unarchiveDrawable}, "Box1", "chats_archiveIcon"));
        arrayList.add(new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "chats_menuBackground"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, "chats_menuName"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, "chats_menuPhone"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, "chats_menuPhoneCats"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, "chats_menuCloudBackgroundCats"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, "chat_serviceBackground"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, "chats_menuTopShadow"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, "chats_menuTopShadowCats"));
        -__lambda_dialogsactivity_wy5ftlzwp7c2dp8j_wpun2kajxi2 = -__lambda_dialogsactivity_wy5ftlzwp7c2dp8j_wpun2kajxi;
        arrayList.add(new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{DrawerProfileCell.class}, null, null, -__lambda_dialogsactivity_wy5ftlzwp7c2dp8j_wpun2kajxi2, "chats_menuTopBackgroundCats"));
        arrayList.add(new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{DrawerProfileCell.class}, null, null, -__lambda_dialogsactivity_wy5ftlzwp7c2dp8j_wpun2kajxi2, "chats_menuTopBackground"));
        View view = this.sideMenu;
        int i = ThemeDescription.FLAG_IMAGECOLOR;
        Class[] clsArr = new Class[]{DrawerActionCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        arrayList.add(new ThemeDescription(view, i, clsArr, strArr, null, null, null, "chats_menuItemIcon"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerActionCell.class}, new String[]{"textView"}, null, null, null, "chats_menuItemText"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerUserCell.class}, new String[]{"textView"}, null, null, null, "chats_menuItemText"));
        View view2 = this.sideMenu;
        int i2 = ThemeDescription.FLAG_TEXTCOLOR;
        Class[] clsArr2 = new Class[]{DrawerUserCell.class};
        String[] strArr2 = new String[1];
        strArr2[0] = "checkBox";
        arrayList.add(new ThemeDescription(view2, i2, clsArr2, strArr2, null, null, null, "chats_unreadCounterText"));
        arrayList.add(new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DrawerUserCell.class}, new String[]{"checkBox"}, null, null, null, "chats_unreadCounter"));
        arrayList.add(new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DrawerUserCell.class}, new String[]{"checkBox"}, null, null, null, "chats_menuBackground"));
        arrayList.add(new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{DrawerAddCell.class}, new String[]{"textView"}, null, null, null, "chats_menuItemIcon"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerAddCell.class}, new String[]{"textView"}, null, null, null, "chats_menuItemText"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DividerCell.class}, Theme.dividerPaint, null, null, "divider"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, "progressCircle"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_offlinePaint, null, null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_onlinePaint, null, null, "windowBackgroundWhiteBlueText3"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, "key_graySectionText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, "graySection"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{HashtagSearchCell.class}, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.progressView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle"));
        DialogsAdapter dialogsAdapter = this.dialogsAdapter;
        View view3 = null;
        arrayList.add(new ThemeDescription(dialogsAdapter != null ? dialogsAdapter.getArchiveHintCellPager() : null, 0, new Class[]{ArchiveHintInnerCell.class}, new String[]{"imageView"}, null, null, null, "chats_nameMessage_threeLines"));
        dialogsAdapter = this.dialogsAdapter;
        arrayList.add(new ThemeDescription(dialogsAdapter != null ? dialogsAdapter.getArchiveHintCellPager() : null, 0, new Class[]{ArchiveHintInnerCell.class}, new String[]{"imageView2"}, null, null, null, "chats_unreadCounter"));
        dialogsAdapter = this.dialogsAdapter;
        arrayList.add(new ThemeDescription(dialogsAdapter != null ? dialogsAdapter.getArchiveHintCellPager() : null, 0, new Class[]{ArchiveHintInnerCell.class}, new String[]{"headerTextView"}, null, null, null, "chats_nameMessage_threeLines"));
        dialogsAdapter = this.dialogsAdapter;
        arrayList.add(new ThemeDescription(dialogsAdapter != null ? dialogsAdapter.getArchiveHintCellPager() : null, 0, new Class[]{ArchiveHintInnerCell.class}, new String[]{"messageTextView"}, null, null, null, "chats_message"));
        dialogsAdapter = this.dialogsAdapter;
        arrayList.add(new ThemeDescription(dialogsAdapter != null ? dialogsAdapter.getArchiveHintCellPager() : null, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefaultArchived"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGray"));
        DialogsSearchAdapter dialogsSearchAdapter = this.dialogsSearchAdapter;
        arrayList.add(new ThemeDescription(dialogsSearchAdapter != null ? dialogsSearchAdapter.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countPaint, null, null, "chats_unreadCounter"));
        dialogsSearchAdapter = this.dialogsSearchAdapter;
        arrayList.add(new ThemeDescription(dialogsSearchAdapter != null ? dialogsSearchAdapter.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countGrayPaint, null, null, "chats_unreadCounterMuted"));
        dialogsSearchAdapter = this.dialogsSearchAdapter;
        arrayList.add(new ThemeDescription(dialogsSearchAdapter != null ? dialogsSearchAdapter.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countTextPaint, null, null, "chats_unreadCounterText"));
        dialogsSearchAdapter = this.dialogsSearchAdapter;
        arrayList.add(new ThemeDescription(dialogsSearchAdapter != null ? dialogsSearchAdapter.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_archiveTextPaint, null, null, "chats_archiveText"));
        dialogsSearchAdapter = this.dialogsSearchAdapter;
        arrayList.add(new ThemeDescription(dialogsSearchAdapter != null ? dialogsSearchAdapter.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText"));
        dialogsSearchAdapter = this.dialogsSearchAdapter;
        if (dialogsSearchAdapter != null) {
            view3 = dialogsSearchAdapter.getInnerListView();
        }
        arrayList.add(new ThemeDescription(view3, 0, new Class[]{HintDialogCell.class}, null, null, null, "chats_onlineCircle"));
        view2 = this.fragmentView;
        int i3 = ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG;
        clsArr2 = new Class[]{FragmentContextView.class};
        strArr2 = new String[1];
        strArr2[0] = "frameLayout";
        arrayList.add(new ThemeDescription(view2, i3, clsArr2, strArr2, null, null, null, "inappPlayerBackground"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"playButton"}, null, null, null, "inappPlayerPlayPause"));
        view2 = this.fragmentView;
        i2 = ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG;
        clsArr2 = new Class[]{FragmentContextView.class};
        strArr2 = new String[1];
        strArr2[0] = "titleTextView";
        arrayList.add(new ThemeDescription(view2, i2, clsArr2, strArr2, null, null, null, "inappPlayerTitle"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_FASTSCROLL, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, null, null, null, "inappPlayerPerformer"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"closeButton"}, null, null, null, "inappPlayerClose"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, null, null, null, "returnToCallBackground"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, null, null, null, "returnToCallText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{r4}, null, null, null, "windowBackgroundWhiteBlueText2"));
        int i4 = 0;
        while (true) {
            UndoView[] undoViewArr = this.undoView;
            if (i4 < undoViewArr.length) {
                arrayList.add(new ThemeDescription(undoViewArr[i4], ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "undo_background"));
                arrayList.add(new ThemeDescription(this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, null, null, null, "undo_cancelColor"));
                arrayList.add(new ThemeDescription(this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, null, null, null, "undo_cancelColor"));
                arrayList.add(new ThemeDescription(this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, null, null, null, "undo_infoColor"));
                arrayList.add(new ThemeDescription(this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"subinfoTextView"}, null, null, null, "undo_infoColor"));
                arrayList.add(new ThemeDescription(this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, null, null, null, "undo_infoColor"));
                arrayList.add(new ThemeDescription(this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, null, null, null, "undo_infoColor"));
                View view4 = this.undoView[i4];
                clsArr2 = new Class[]{UndoView.class};
                String[] strArr3 = new String[1];
                strArr3[0] = "leftImageView";
                arrayList.add(new ThemeDescription(view4, 0, clsArr2, strArr3, "info1", "undo_background"));
                arrayList.add(new ThemeDescription(this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "info2", "undo_background"));
                arrayList.add(new ThemeDescription(this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luCLASSNAME", "undo_infoColor"));
                arrayList.add(new ThemeDescription(this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luCLASSNAME", "undo_infoColor"));
                arrayList.add(new ThemeDescription(this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luCLASSNAME", "undo_infoColor"));
                arrayList.add(new ThemeDescription(this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc9", "undo_infoColor"));
                arrayList.add(new ThemeDescription(this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc8", "undo_infoColor"));
                arrayList.add(new ThemeDescription(this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc7", "undo_infoColor"));
                arrayList.add(new ThemeDescription(this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc6", "undo_infoColor"));
                arrayList.add(new ThemeDescription(this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc5", "undo_infoColor"));
                arrayList.add(new ThemeDescription(this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc4", "undo_infoColor"));
                arrayList.add(new ThemeDescription(this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc3", "undo_infoColor"));
                arrayList.add(new ThemeDescription(this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc2", "undo_infoColor"));
                arrayList.add(new ThemeDescription(this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc1", "undo_infoColor"));
                arrayList.add(new ThemeDescription(this.undoView[i4], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Oval", "undo_infoColor"));
                i4++;
            } else {
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogBackground"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogBackgroundGray"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogTextBlack"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogTextLink"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogLinkSelection"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogTextBlue"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogTextBlue2"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogTextBlue3"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogTextBlue4"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogTextRed"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogTextRed2"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogTextGray"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogTextGray2"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogTextGray3"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogTextGray4"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogIcon"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogRedIcon"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogTextHint"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogInputField"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogInputFieldActivated"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogCheckboxSquareBackground"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogCheckboxSquareCheck"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogCheckboxSquareUnchecked"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogCheckboxSquareDisabled"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogRadioBackground"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogRadioBackgroundChecked"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogProgressCircle"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogButton"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogButtonSelector"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogScrollGlow"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogRoundCheckBox"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogRoundCheckBoxCheck"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogBadgeBackground"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogBadgeText"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogLineProgress"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogLineProgressBackground"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogGrayLine"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialog_inlineProgressBackground"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialog_inlineProgress"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogSearchBackground"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogSearchHint"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogSearchIcon"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogSearchText"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogFloatingButton"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogFloatingIcon"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialogShadowLine"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "key_sheet_scrollUp"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "key_sheet_other"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "player_actionBar"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "player_actionBarSelector"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "player_actionBarTitle"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "player_actionBarTop"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "player_actionBarSubtitle"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "player_actionBarItems"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "player_background"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "player_time"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "player_progressBackground"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "key_player_progressCachedBackground"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "player_progress"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "player_placeholder"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "player_placeholderBackground"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "player_button"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "player_buttonActive"));
                return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[0]);
            }
        }
    }

    public /* synthetic */ void lambda$getThemeDescriptions$20$DialogsActivity() {
        int i;
        DialogsRecyclerView dialogsRecyclerView = this.listView;
        if (dialogsRecyclerView != null) {
            int childCount = dialogsRecyclerView.getChildCount();
            for (i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof ProfileSearchCell) {
                    ((ProfileSearchCell) childAt).update(0);
                } else if (childAt instanceof DialogCell) {
                    ((DialogCell) childAt).update(0);
                }
            }
        }
        DialogsSearchAdapter dialogsSearchAdapter = this.dialogsSearchAdapter;
        if (dialogsSearchAdapter != null) {
            RecyclerListView innerListView = dialogsSearchAdapter.getInnerListView();
            if (innerListView != null) {
                i = innerListView.getChildCount();
                for (int i2 = 0; i2 < i; i2++) {
                    View childAt2 = innerListView.getChildAt(i2);
                    if (childAt2 instanceof HintDialogCell) {
                        ((HintDialogCell) childAt2).update();
                    }
                }
            }
        }
        RecyclerView recyclerView = this.sideMenu;
        if (recyclerView != null) {
            View childAt3 = recyclerView.getChildAt(0);
            if (childAt3 instanceof DrawerProfileCell) {
                ((DrawerProfileCell) childAt3).applyBackground(true);
            }
        }
        PullForegroundDrawable pullForegroundDrawable = this.pullForegroundDrawable;
        if (pullForegroundDrawable != null) {
            pullForegroundDrawable.updateColors();
        }
    }
}
